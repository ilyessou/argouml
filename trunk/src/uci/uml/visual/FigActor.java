// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
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

// File: FigActor.java
// Classes: FigActor
// Original Author: abonner@ics.uci.edu
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
import uci.uml.Behavioral_Elements.Use_Cases.*;

/** Class to display graphics for a UML State in a diagram. */

public class FigActor extends FigNodeModelElement {


  ////////////////////////////////////////////////////////////////
  // instance variables

  /** UML does not really use ports, so just define one big one so
   *  that users can drag edges to or from any point in the icon. */
  FigRect _bigPort;

  /* Put in the things for the "person" in the FigActor */
  FigCircle _head;
  FigLine _body;
  FigLine _arms;
  FigLine _leftLeg;
  FigLine _rightLeg;

  // add other Figs here aes needed


  ////////////////////////////////////////////////////////////////
  // constructors

  public FigActor() {
    // Put this rectangle behind the rest, so it goes first
    _bigPort = new FigRect(5, 5, 30, 85, Color.gray, Color.gray);
    _head = new FigCircle(10, 10, 20, 30, Color.black, Color.white);
    _body = new FigLine(20, 40, 20, 60, Color.black);
    _arms = new FigLine(10, 50, 30, 50, Color.black);
    _leftLeg = new FigLine(20, 60, 15, 75, Color.black );
    _rightLeg = new FigLine(20, 60, 25, 75, Color.black );
    _name.setBounds(5, 75, 35, 20);

    _name.setTextFilled(false);
    _name.setFilled(false);
    _name.setLineWidth(0);
    // initialize any other Figs here

    // add Figs to the FigNode in back-to-front order
    addFig(_bigPort);
    addFig(_head);
    addFig(_body);
    addFig(_arms);
    addFig(_leftLeg);
    addFig(_rightLeg);
    addFig(_name);

    setBlinkPorts(true); // make port invisble unless mouse enters
    Rectangle r = getBounds();
  }

  public FigActor(GraphModel gm, Object node) {
    this();
    setOwner(node);
  }

  public String placeString() { return "new Actor"; }

  public Object clone() {
    FigActor figClone = (FigActor) super.clone();
    Vector v = figClone.getFigs();
    figClone._bigPort = (FigRect) v.elementAt(0);
    figClone._head = (FigCircle) v.elementAt(1);
    figClone._body = (FigLine) v.elementAt(2);
    figClone._arms = (FigLine) v.elementAt(3);
    figClone._leftLeg = (FigLine) v.elementAt(4);
    figClone._rightLeg = (FigLine) v.elementAt(5);
    figClone._name = (FigText) v.elementAt(6);
    return figClone;
  }

  ////////////////////////////////////////////////////////////////
  // Fig accessors

  public void setOwner(Object node) {
    super.setOwner(node);
    bindPort(node, _bigPort);
  }

  /** Returns true if this Fig can be resized by the user. */
  public boolean isResizable() { return false; }

  public void setLineColor(Color col) {
    _head.setLineColor(col);
    _body.setLineColor(col);
    _arms.setLineColor(col);
    _leftLeg.setLineColor(col);
    _rightLeg.setLineColor(col);
  }
  public Color getLineColor() { return _head.getLineColor(); }

  public void setFillColor(Color col) { _head.setFillColor(col); }
  public Color getFillColor() { return _head.getFillColor(); }

  public void setFilled(boolean f) { _head.setFilled(f); }
  public boolean getFilled() { return _head.getFilled(); }

  public void setLineWidth(int w) {
    _head.setLineWidth(w);
    _body.setLineWidth(w);
    _arms.setLineWidth(w);
    _leftLeg.setLineWidth(w);
    _rightLeg.setLineWidth(w);
  }
  public int getLineWidth() { return _head.getLineWidth(); }

  ////////////////////////////////////////////////////////////////
  // event handlers

//   /** Update the text labels */
//   protected void modelChanged() {
//     super.modelChanged();
//   }


} /* end class FigActor */
