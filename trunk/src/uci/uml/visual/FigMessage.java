// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products may
// be obtained by contacting the University of California. David F. Redmiles
// Department of Information and Computer Science (ICS) University of
// California Irvine, California 92697-3425 Phone: 714-824-3823. This software
// program and documentation are copyrighted by The Regents of the University
// of California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.

// File: FigMessage.java
// Original Author: agauthie@ics.uci.edu
// $Id$

package uci.uml.visual;

import java.awt.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;

import uci.gef.*;
import uci.graph.*;
import uci.uml.ui.*;
import uci.uml.generate.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Behavioral_Elements.Collaborations.*;
import uci.uml.Behavioral_Elements.Common_Behavior.*;

/** Class to display graphics for a UML collaboration in a diagram. */

public class FigMessage extends FigNodeModelElement {

  ////////////////////////////////////////////////////////////////
  // constants
  public int PADDING = 5;
  ////////////////////////////////////////////////////////////////
  // instance variables

  protected FigPoly _figPoly;
  //protected Polygon _polygon;

  ////////////////////////////////////////////////////////////////
  // constructors

  public FigMessage() {
    _name.setLineWidth(0);
    _name.setMultiLine(true);
    _name.setFilled(false);
    Dimension nameMin = _name.getMinimumSize();
    _name.setBounds(10, 10, 90, nameMin.height);

    ///////////////////////////
    _figPoly = new FigPoly(Color.black, Color.black);
    int[] xpoints = {75,73,75,75,75,77,75};
    int[] ypoints = {15,24,24,33,24,24,15};
    Polygon polygon = new Polygon(xpoints, ypoints, 7);
    _figPoly.setPolygon(polygon);
    _figPoly.setBounds(100,10,5,18);

    // add Figs to the FigNode in back-to-front order
    addFig(_name);
    addFig(_figPoly);

    Rectangle r = getBounds();
    setBounds(r.x, r.y, r.width, r.height);
  }

  public FigMessage(GraphModel gm, Object node) {
    this();
    setOwner(node);
  }

  public String placeString() { return "new Message"; }

  public Object clone() {
    FigMessage figClone = (FigMessage) super.clone();
    Vector v = figClone.getFigs();
    figClone._name = (FigText) v.elementAt(0);
    figClone._figPoly = (FigPoly) v.elementAt(1);
    //figClone._polygon = (Polygon) _polygon.clone();
    return figClone;
  }



  ////////////////////////////////////////////////////////////////
  // Fig accessors

  ////////////////////////////////////////////////////////////////
  // Fig accessors

  public void setLineColor(Color col) {
    _figPoly.setLineColor(col);
    _name.setLineColor(col);
  }
  public Color getLineColor() { return _figPoly.getLineColor(); }

  public void setFillColor(Color col) {
    _figPoly.setFillColor(col);
    _name.setFillColor(col);
  }
  public Color getFillColor() { return _figPoly.getFillColor(); }

  public void setFilled(boolean f) {  }
  public boolean getFilled() { return true; }

  public void setLineWidth(int w) { _figPoly.setLineWidth(w); }
  public int getLineWidth() { return _figPoly.getLineWidth(); }

  public Dimension getMinimumSize() {
    Dimension nameMin = _name.getMinimumSize();
    Dimension figPolyMin = _figPoly.getSize();

    int h = Math.max(figPolyMin.height, nameMin.height);
    int w = figPolyMin.width + nameMin.width;
    return new Dimension(w, h);
  }

  /* Override setBounds to keep shapes looking right */
  public void setBounds(int x, int y, int w, int h) {
    if (_name == null) return;

    Rectangle oldBounds = getBounds();

    Dimension nameMin = _name.getMinimumSize();

    int ht = 0;

    if (nameMin.height > _figPoly.getHeight()) 
      ht = (nameMin.height - _figPoly.getHeight()) / 2;

    _name.setBounds(x, y, w-_figPoly.getWidth(), nameMin.height);
    _figPoly.setBounds(x + _name.getWidth(), y + ht,
		       _figPoly.getWidth(), _figPoly.getHeight());

    firePropChange("bounds", oldBounds, getBounds());
    calcBounds(); //_x = x; _y = y; _w = w; _h = h;
    updateEdges();
  }

  protected void textEdited(FigText ft) throws PropertyVetoException {
    super.textEdited(ft);
    Message mes = (Message) getOwner();
    if (ft == _name) {
       String s = ft.getText();
       ParserDisplay.SINGLETON.parseMessage(mes, s);
    }
  }

  protected void modelChanged() {
    super.modelChanged();
    Message mes = (Message) getOwner();
    if (mes == null) return;
    String nameStr = GeneratorDisplay.Generate(mes.getName()).trim();
    String actionString = ((UninterpretedAction)mes.getAction()).getBody().trim();
    
    if( nameStr == "" && actionString == "")
      _name.setText("");
    else
      _name.setText(nameStr.trim() + " : " + actionString);
  }

  public void dispose() {
    if (!(getOwner() instanceof Element)) return;
    Element elmt = (Element) getOwner();
    Project p = ProjectBrowser.TheInstance.getProject();
    p.moveToTrash(elmt);
    super.dispose();
  }

} /* end class FigMessage */
