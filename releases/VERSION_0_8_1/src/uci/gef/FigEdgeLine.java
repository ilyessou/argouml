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



// File: FigEdgeLine.java
// Classes: FigEdgeLine
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.util.*;
import java.util.Enumeration;
import java.awt.*;
import java.io.*;

/** An FigEdge that shows a straight line from the source port
 *  to the destination port. */

public class FigEdgeLine extends FigEdge {
  ////////////////////////////////////////////////////////////////
  // FigEdge API

  /** Instanciate a new FigLine as the contained Fig. By default it is
   *  black and the FigEdge has no ArrowHeads. */
  protected Fig makeEdgeFig() { return new FigLine(0, 0, 0, 0, Color.black); }

  /** Compute the shape of the line that presents an Edge. */
  protected void computeRoute() {
    Point srcPt = _sourcePortFig.center();
    Point dstPt = _destPortFig.center();

    if (_useNearest) {
      //? two iterations of refinement, maybe should be a for-loop
      srcPt = _sourcePortFig.connectionPoint(dstPt);
      dstPt = _destPortFig.connectionPoint(srcPt);
      srcPt = _sourcePortFig.connectionPoint(dstPt);
      dstPt = _destPortFig.connectionPoint(srcPt);
    }

    ((FigLine) _fig).setShape(srcPt, dstPt);
    calcBounds();
  }


  public void paint(Graphics g) {
    super.paint(g);
    if (_highlight) {
      FigLine f = (FigLine) getFig();
      paintHighlightLine(g, f.getX1(), f.getY1(), f.getX2(), f.getY2());
    }
  }

  static final long serialVersionUID = -8455231483004481128L;

} /* end class FigEdgeLine */

