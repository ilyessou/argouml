// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.



// File: FigEdgePoly.java
// Classes: FigEdgePoly
// Original Author: agauthie@ics.uci.edu
// $Id$

package uci.gef;

import java.util.*;
import java.util.Enumeration;
import java.awt.*;

/** A Fig that paints edges between ports. This version
 *  automatically routes a rectilinear edge. The routing is not very
 *  good. It avoids the source and sink nodes and no other nodes. It is
 *  basically case-analysis, and some of the cases are wrong or
 *  missing. Anyway, the user can edit the edge by dragging
 *  handles. The 0th and last handles are fixed in position so that
 *  they stay connected to ports. If the user drags a handle next to a
 *  fixed handle, a new vertex is automatically inserted.
 *
 * @see FigPoly */

public class FigEdgePoly extends FigEdge {

  ////////////////////////////////////////////////////////////////
  // instance variables

  /** True if the edge has been laid out automatically once. It will
   *  not be done automatically again since the user may have edited the
   *  edge and I dont want to undo that work. */
  protected boolean _initiallyLaidOut = false;

  ////////////////////////////////////////////////////////////////
  // FigEdge API

  /** Instanciate a FigPoly with its rectilinear flag set. By default
   *  the FigPoly is black and the FigEdge has now ArrowHeads. */
  protected Fig makeEdgeFig() {
    FigPoly res = new FigPoly(Color.black);
    res.setRectilinear(false);
    res.setFixedHandles(1);
    res.setFilled(false);
    return res;
  }

  ////////////////////////////////////////////////////////////////
  // accessors

  public void setInitiallyLaidOut(boolean b) { _initiallyLaidOut = b; }

  ////////////////////////////////////////////////////////////////
  // routing methods

  /** Find the route that the edge should follow.  Basically case
   *  analysis to route around source and destination nodes.
   *  Needs-More-Work: A better algorithm would really be useful.
   *  Needs-More-Work: Sometimes the edge can get non-rectilinear. */
  protected void computeRoute() {
    if (!_initiallyLaidOut) {
      layoutEdge();
      _initiallyLaidOut = true;
    }
    FigPoly p = ((FigPoly) _fig);


    Point srcPt = _sourcePortFig.center();
    Point dstPt = _destPortFig.center();

    if (_useNearest) {
      if (p.getNumPoints() == 2) {
	//? two iterations of refinement, maybe should be a for-loop
	srcPt = _sourcePortFig.connectionPoint(p.getPoints(1));
	dstPt = _destPortFig.connectionPoint(p.getPoints(p.getNumPoints()-2));
	srcPt = _sourcePortFig.connectionPoint(dstPt);
	dstPt = _destPortFig.connectionPoint(srcPt);
      }
      else {
	srcPt = _sourcePortFig.connectionPoint(p.getPoints(1));
	dstPt = _destPortFig.connectionPoint(p.getPoints(p.getNumPoints()-2));
      }
    }

    setEndPoints(srcPt, dstPt);
    calcBounds();
  } /* end computeRoute */

  /** Internal function to actually compute the layout of the line if
   *  it has never been done on that line before. */
  protected void layoutEdge() {
    int npoints = 0;
    int xpoints[] = new int[16];
    int ypoints[] = new int[16];
    Point srcPt = _sourcePortFig.center();
    Point dstPt = _destPortFig.center();

    if (_useNearest) {
      srcPt = _sourcePortFig.connectionPoint(dstPt);
      dstPt = _destPortFig.connectionPoint(srcPt);
      srcPt = _sourcePortFig.connectionPoint(dstPt);
      dstPt = _destPortFig.connectionPoint(srcPt);
    }
    
    Rectangle srcRR = _sourceFigNode.routingRect();
    Rectangle dstRR = _destFigNode.routingRect();

    Object srcPort = _sourcePortFig.getOwner();
    Object dstPort = _destPortFig.getOwner();
    int srcSector = _sourceFigNode.getPortSector(_sourcePortFig);
    int dstSector = _destFigNode.getPortSector(_destPortFig);

    // first decide what layout case we have
    Point srcRRPt = routingRectPoint(srcPt, srcRR, srcSector);
    Point dstRRPt = routingRectPoint(dstPt, dstRR, dstSector);

    if (srcSector == 1 || srcSector == -1) {
      xpoints[npoints] = srcPt.x; ypoints[npoints++] = srcPt.y; }
    xpoints[npoints] = srcPt.x; ypoints[npoints++] = srcPt.y;
    xpoints[npoints] = srcRRPt.x; ypoints[npoints++] = srcRRPt.y;

    npoints = npoints + tryRoute(dstRRPt.x, dstRRPt.y, npoints,
				 xpoints, ypoints,
				 srcRR, dstRR, srcSector, dstSector);

    xpoints[npoints] = dstRRPt.x; ypoints[npoints++] = dstRRPt.y;
    xpoints[npoints] = dstPt.x; ypoints[npoints++] = dstPt.y;
    //   if (dstSector == 1 || dstSector == -1) {
    // xpoints[npoints] = dstPt.x; ypoints[npoints++] = dstPt.y; }
    Polygon routePoly = new Polygon(xpoints, ypoints, npoints);
    ((FigPoly)_fig).setPolygon(routePoly);
  }

  /** Reply a point on the given routing rect that is "straight out"
   *  from the connection point in the proper direction. */
  protected Point routingRectPoint(Point p, Rectangle r, int sector) {
    switch (sector) {
    case -1: return new Point(p.x, r.y);
    case 2: return new Point(r.x, p.y);
    case 1: return new Point(p.x, r.y + r.height);
    case -2: return new Point(r.x + r.width, p.y);
    default: System.out.println("error, undefined sector!");
      return p;
    }
  }

  /** Try to find a route from the last point in (xs, ys) to point (x,
   *  y).  Try to avoid the given rectangles along the
   *  way. Needs-More-Work: should allow a vector of rectangles to
   *  avoid. */
  protected int tryRoute(int x, int y, int np, int xs[], int ys[],
			 Rectangle avoid1, Rectangle avoid2,
			 int srcSector, int dstSector) {
    if (np > 12 ) return 0;
    int fx = xs[np-1], fy = ys[np-1];
    if ((fx == x || fy == y) && segOK(fx, fy, x, y, avoid1, avoid2)) {
      xs[np] = x; ys[np++] = y;
      xs[np] = x; ys[np++] = y;
      return 1;
    }
    if (segOK(fx,fy,fx,y,avoid1,avoid2) && segOK(fx,y,x,y,avoid1,avoid2)) {
      xs[np] = fx; ys[np++] = y;
      xs[np] = x; ys[np++] = y;
      return 2;
    }
    if (segOK(fx,fy,x,fy,avoid1,avoid2) && segOK(x,fy,x,y,avoid1,avoid2)) {
      xs[np] = x; ys[np++] = fy;
      xs[np] = x; ys[np++] = y;
      return 2;
    }

    Point avoidPt = findAvoidPt(fx, fy, x, y, avoid1, avoid2);
    if ((srcSector==1 || srcSector==-1 || fy==avoidPt.y) && fx!=avoidPt.x) {
      xs[np] = avoidPt.x;
      ys[np++] = fy;
      return tryRoute(x,y,np,xs,ys,avoid1, avoid2, srcSector, dstSector) + 1;
    }
    if ((srcSector==2 || srcSector==-2 || fx==avoidPt.x) && fy != avoidPt.y) {
      xs[np] = fx;
      ys[np++] = avoidPt.y;
      return tryRoute(x,y,np,xs,ys,avoid1,avoid2,srcSector,dstSector) + 1;
    }
    return 0;
  }

  /** Find a point on the corner of one of the avoid rectangles that
   *  is a good intermediate point for the edge layout. */
  protected Point findAvoidPt(int fx, int fy, int x, int y,
			      Rectangle avoid1, Rectangle avoid2) {
    Point res = new Point(fx, fy);
    if (avoid1.x + avoid1.width < avoid2.x) res.x = avoid2.x;
    else if (avoid2.x + avoid2.width < avoid1.x) res.x = avoid1.x;
    else res.x = Math.min(avoid1.x, avoid2.x);

    if (avoid1.y + avoid1.height < avoid2.y) res.y = avoid2.y;
    else if (avoid2.y + avoid2.height < avoid1.y) res.y = avoid1.y;
    else res.y = Math.min(avoid1.y, avoid2.y);

    return res;
  }

  /** Reply true if the line segment from (x1, y1) to (x2, y2) does
   *  not intersect the given avoid rectangles. */
  protected boolean segOK(int x1, int y1, int x2, int y2,
			  Rectangle avoid1, Rectangle avoid2) {
    int xmin = Math.min(x1, x2);
    int xmax = Math.max(x1, x2);
    int ymin = Math.min(y1, y2);
    int ymax = Math.max(y1, y2);
    int rright = avoid1.x + avoid1.width;
    int rbot = avoid1.y + avoid1.height;
    if (x1 == x2) { // vertical
      if (x1 > avoid1.x && x1 < rright &&
	  ((ymin < avoid1.y && ymax > avoid1.y) ||
	   (ymin < rbot && ymax > rbot)))
	return false;
    }
    if (y1 == y2) { // horizontal
      if (y1 > avoid1.y && y1 < rbot &&
	  ((xmin < avoid1.x && xmax > avoid1.x) ||
	   (xmin < rright && xmax > rright)))
	return false;
    }
    rright = avoid2.x + avoid2.width;
    rbot = avoid2.y + avoid2.height;
    if (x1 == x2) { // vertical
      if (x1 > avoid2.x && x1 < rright &&
	  ((ymin < avoid2.y && ymax > avoid2.y) ||
	   (ymin < rbot && ymax > rbot)))
	return false;
    }
    if (y1 == y2) { // horizontal
      if (y1 > avoid2.y && y1 < rbot &&
	  ((xmin < avoid2.x && xmax > avoid2.x) ||
	   (xmin < rright && xmax > rright)))
	return false;
    }
    return true;
  }
    public void moveVertex(Handle h, int x, int y, boolean ov) {
    int i = h.index;
    int np = _fig.getNumPoints();
    FigPoly p = ((FigPoly) _fig);
    if (!p.getRectilinear()) {
      if (p._isComplete) {
        if (i == 0) {
          if(p._xpoints[i+1] == x && p._ypoints[i+1] == y)
            if (p._isSelfLoop && p._npoints <= 4){;}
            else
              p.removePoint(i+1);
        }
        else if (i == (np-1)) {
          if(p._xpoints[i-1] == x && p._ypoints[i-1] == y)
            if (p._isSelfLoop && p._npoints <= 4){;}
            else
              p.removePoint(i-1);
        }
        if (np > 2) {
          //Point handlePoint = new Point(p._xpoints[i],p._ypoints[i]);
	  Point handlePoint = new Point(x, y);
          Point srcPt = _sourcePortFig.center();
          Point dstPt = _destPortFig.center();
          if (i == 1 && np == 3) {
            srcPt = _sourcePortFig.connectionPoint(handlePoint);
            dstPt = _destPortFig.connectionPoint(handlePoint);
          p.moveVertex(new Handle(0),srcPt.x,srcPt.y,true);
          p.moveVertex(new Handle(np-1),dstPt.x,dstPt.y,true);
          calcBounds();
          }
          else if (i == 1){
            srcPt = _sourcePortFig.connectionPoint(handlePoint);
          p.moveVertex(new Handle(0),srcPt.x,srcPt.y,true);
          calcBounds();
          }
          else if (i == (np -2 )){
            dstPt = _destPortFig.connectionPoint(handlePoint);
          p.moveVertex(new Handle(np-1),dstPt.x,dstPt.y,true);
          calcBounds();
          }
        }
      }
      p._xpoints[i] = x; 
      p._ypoints[i] = y;
    }
  }

  /** When the user drags the handles, move individual points */
  public void setPoints(Handle h, int mX, int mY) {
    moveVertex(h, mX, mY, false);
    calcBounds();
  }

  /** Add a point to this polygon. Fires PropertyChange with "bounds". */
  public void insertPoint(int i, int x, int y) {
    FigPoly p = ((FigPoly) _fig);
    p.insertPoint(i,x,y);
  }

  private static Handle _TempHandle = new Handle(0);

  /** Set the end points of this polygon, regardless of the number of
   *  fixed handles. This is used when nodes move. */
  public void setEndPoints(Point start, Point end) {
    FigPoly p = ((FigPoly) _fig);
    while (p._npoints < 2) p.addPoint(start);
    synchronized (_TempHandle) {
      _TempHandle.index = 0;
      moveVertex(_TempHandle, start.x, start.y, true);
      _TempHandle.index = p._npoints - 1;
      moveVertex(_TempHandle, end.x, end.y, true);
    }
  }

  public void paint(Graphics g) {
    super.paint(g);
    if (_highlight) {
      FigPoly f = (FigPoly) getFig();
      int nPoints = f.getNumPoints();
      int xs[] = f.getXs();
      int ys[] = f.getYs();
      for (int i = 1; i < nPoints; i++)
	paintHighlightLine(g, xs[i-1], ys[i-1], xs[i], ys[i]);
    }
  }

  static final long serialVersionUID = -8604764896732440025L;

} /* end class FigEdgePoly */
