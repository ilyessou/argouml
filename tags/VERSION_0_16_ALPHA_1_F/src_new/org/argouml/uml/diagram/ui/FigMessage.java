// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

// File: FigMessage.java
// Original Author: agauthie@ics.uci.edu

package org.argouml.uml.diagram.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.application.api.Notation;
import org.argouml.model.ModelFacade;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.collaboration.ui.FigAssociationRole;
import org.argouml.uml.generator.ParserDisplay;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigText;

/** Class to display graphics for a UML collaboration in a diagram. */

public class FigMessage extends FigNodeModelElement {

    ////////////////////////////////////////////////////////////////
    // constants
    public int PADDING = 5;
    public static Vector ARROW_DIRECTIONS = new Vector();

    ////////////////////////////////////////////////////////////////
    // instance variables

    protected FigPoly _figPoly;
    protected int _arrowDirection = 0;
    //protected Polygon _polygon;

    ////////////////////////////////////////////////////////////////
    // constructors

    public FigMessage() {
	getNameFig().setLineWidth(0);
	getNameFig().setMultiLine(true);
	getNameFig().setFilled(false);
	Dimension nameMin = getNameFig().getMinimumSize();
	getNameFig().setBounds(10, 10, 90, nameMin.height);

	_figPoly = new FigPoly(Color.black, Color.black);
	int[] xpoints = {75, 75, 77, 75, 73, 75};
	int[] ypoints = {33, 24, 24, 15, 24, 24};
	Polygon polygon = new Polygon(xpoints, ypoints, 6);
	_figPoly.setPolygon(polygon);
	_figPoly.setBounds(100, 10, 5, 18);

	ARROW_DIRECTIONS.addElement("North");
	ARROW_DIRECTIONS.addElement("South");
	ARROW_DIRECTIONS.addElement("East");
	ARROW_DIRECTIONS.addElement("West");

	// add Figs to the FigNode in back-to-front order
	addFig(getNameFig());
	addFig(_figPoly);

	Rectangle r = getBounds();
	setBounds(r.x, r.y, r.width, r.height);
    }

    public FigMessage(GraphModel gm, Layer lay, Object node) {
	this();
	setLayer(lay);
	setOwner(node);
    }

    public String placeString() { return "new Message"; }

    public Object clone() {
	FigMessage figClone = (FigMessage) super.clone();
	Iterator it = figClone.getFigs(null).iterator();
	figClone.setNameFig((FigText) it.next());
	figClone._figPoly = (FigPoly) it.next();
	//figClone._polygon = (Polygon) _polygon.clone();
	return figClone;
    }



    ////////////////////////////////////////////////////////////////
    // Fig accessors

    public void setLineColor(Color col) {
	_figPoly.setLineColor(col);
	getNameFig().setLineColor(col);
    }
    public Color getLineColor() { return _figPoly.getLineColor(); }

    public void setFillColor(Color col) {
	_figPoly.setFillColor(col);
	getNameFig().setFillColor(col);
    }
    public Color getFillColor() { return _figPoly.getFillColor(); }

    public void setFilled(boolean f) {  }
    public boolean getFilled() { return true; }

    public void setLineWidth(int w) { _figPoly.setLineWidth(w); }
    public int getLineWidth() { return _figPoly.getLineWidth(); }

    public void setArrow(int direction) {
	Rectangle bbox = getBounds();
    
	_arrowDirection = direction;
	switch (direction) {
	    // south
	case 1: {
	    int[] xpoints = {75, 75, 77, 75, 73, 75};
	    int[] ypoints = {15, 24, 24, 33, 24, 24};
	    Polygon polygon = new Polygon(xpoints, ypoints, 6);
	    _figPoly.setPolygon(polygon);
	    break;
	}
	    // east
	case 2: {
	    int[] xpoints = {66, 75, 75, 84, 75, 75};
	    int[] ypoints = {24, 24, 26, 24, 22, 24};
	    Polygon polygon = new Polygon(xpoints, ypoints, 6);
	    _figPoly.setPolygon(polygon);
	    break;
	}
	    // west
	case 3: {
	    int[] xpoints = {84, 75, 75, 66, 75, 75};
	    int[] ypoints = {24, 24, 26, 24, 22, 24};
	    Polygon polygon = new Polygon(xpoints, ypoints, 6);
	    _figPoly.setPolygon(polygon);
	    break;
	}
	    // north
	default: {
	    int[] xpoints = {75, 75, 77, 75, 73, 75};
	    int[] ypoints = {33, 24, 24, 15, 24, 24};
	    Polygon polygon = new Polygon(xpoints, ypoints, 6);
	    _figPoly.setPolygon(polygon);
	}
	}
	setBounds(bbox);
    }
    public int getArrow() { return _arrowDirection; }

    public Dimension getMinimumSize() {
	Dimension nameMin = getNameFig().getMinimumSize();
	Dimension figPolyMin = _figPoly.getSize();

	int h = Math.max(figPolyMin.height, nameMin.height);
	int w = figPolyMin.width + nameMin.width;
	return new Dimension(w, h);
    }

    /* Override setBounds to keep shapes looking right */
    public void setBounds(int x, int y, int w, int h) {
	if (getNameFig() == null) {
	    return;
	}

	Rectangle oldBounds = getBounds();

	Dimension nameMin = getNameFig().getMinimumSize();

	int ht = 0;

	if (nameMin.height > _figPoly.getHeight()) 
	    ht = (nameMin.height - _figPoly.getHeight()) / 2;

	getNameFig().setBounds(x, y, w - _figPoly.getWidth(), nameMin.height);
	_figPoly.setBounds(x + getNameFig().getWidth(), y + ht,
			   _figPoly.getWidth(), _figPoly.getHeight());

	firePropChange("bounds", oldBounds, getBounds());
	calcBounds(); //_x = x; _y = y; _w = w; _h = h;
	updateEdges();
    }

    protected void textEdited(FigText ft) throws PropertyVetoException {
	Object message = getOwner();
	if (message != null && ft == getNameFig()) {
	    String s = ft.getText();
	    try {
		ParserDisplay.SINGLETON.parseMessage(message, s);
		ProjectBrowser.getInstance().getStatusBar().showStatus("");
	    } catch (ParseException pe) {
		ProjectBrowser.getInstance().getStatusBar()
		    .showStatus("Error: " + pe + " at " + pe.getErrorOffset());
	    }
	}
	else
	    super.textEdited(ft);
    }

    /**
     * Determines the direction of the message arrow. Deetermination of the 
     * type of arrow happens in modelchanged
     */
    protected void updateArrow() {
  	Object mes = getOwner(); // MMessage
	if (mes == null || getLayer() == null) return;
	Object sender = ModelFacade.getSender(mes); // MClassifierRole
	Object receiver = ModelFacade.getReceiver(mes); // MClassifierRole
	Fig senderPort = getLayer().presentationFor(sender);
	Fig receiverPort = getLayer().presentationFor(receiver);
	if (senderPort == null || receiverPort == null) return;
	int sx = senderPort.getX();
	int sy = senderPort.getY();
	int rx = receiverPort.getX();
	int ry = receiverPort.getY();
	if (sx < rx && Math.abs(sy - ry) <= Math.abs(sx - rx)) { // east
	    setArrow(2);
	} else 
	    if (sx > rx && Math.abs(sy - ry) <= Math.abs(sx - rx)) { // west
		setArrow(3);
	    } else 
		if (sy < ry) { // south
		    setArrow(1);
		} else
		    setArrow(4);
    }

    /**
     * Add the FigMessage to the Path Items of its FigAssociationRole.
     */
    public void addPathItemToFigAssociationRole(Layer lay) {

	Object associationRole =
	    ModelFacade.getCommunicationConnection(getOwner());
	if (associationRole != null && lay != null) {
	    FigAssociationRole figAssocRole =
		(FigAssociationRole) lay.presentationFor(associationRole);
	    if (figAssocRole != null) {
		figAssocRole.addMessage(this);
		figAssocRole.updatePathItemLocations();
		lay.bringToFront(this);
	    }
	}
    }

 
	

	

    /**
     * @see org.tigris.gef.presentation.Fig#paint(Graphics)
     */
    public void paint(Graphics g) {
	updateArrow();
	super.paint(g);
    }

    

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateNameText()
     */
    protected void updateNameText() {
        Object mes =  getOwner();
        if (mes == null) return;
        getNameFig().setText(Notation.generate(this, mes));
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#renderingChanged()
     */
    public void renderingChanged() {
        super.renderingChanged();
        updateArrow();
    }

} /* end class FigMessage */
