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

// File: FigInstance.java
// Classes: FigInstance
// Original Author: agauthie@ics.uci.edu
// $Id$

package org.argouml.uml.diagram.static_structure.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Iterator;

import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Globals;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/** Class to display graphics for a UML MInstance in a diagram. */

public class FigInstance extends FigNodeModelElement {

    /** UML does not really use ports, so just define one big one so
     *  that users can drag edges to or from any point in the icon. */

    FigText _attr;

    // add other Figs here aes needed


    ////////////////////////////////////////////////////////////////
    // constructors

    public FigInstance() {
	Color handleColor = Globals.getPrefs().getHandleColor();

	getNameFig().setUnderline(true);
	getNameFig().setTextFilled(true);

	// initialize any other Figs here
	_attr = new FigText(10, 30, 90, 40, Color.black, "Times", 10);
	_attr.setFont(LABEL_FONT);
	_attr.setExpandOnly(true);
	_attr.setTextColor(Color.black);
	_attr.setAllowsTab(false);

	//_attr.setExpandOnly(true);
	_attr.setJustification(FigText.JUSTIFY_LEFT);

	// add Figs to the FigNode in back-to-front order
	addFig(_bigPort);
	addFig(getNameFig());
	addFig(_attr);

	setBlinkPorts(true); //make port invisble unless mouse enters
	Rectangle r = getBounds();
	setBounds(r.x, r.y, r.width, r.height);
    }

    public FigInstance(GraphModel gm, Object node) {
	this();
	setOwner(node);
    }

    public String placeString() { return "new MInstance"; }

    public Object clone() {
	FigInstance figClone = (FigInstance) super.clone();
	Iterator iter = figClone.getFigs(null).iterator();
	figClone._bigPort = (FigRect) iter.next();
	figClone.setNameFig((FigText) iter.next());
	figClone._attr = (FigText) iter.next();
	return figClone;
    }

    public Dimension getMinimumSize() {
	Dimension nameMin = getNameFig().getMinimumSize();
	Dimension attrMin = _attr.getMinimumSize();

	int h = nameMin.height + attrMin.height;
	int w = Math.max(nameMin.width, attrMin.width);
	return new Dimension(w, h);
    }


    /* Override setBounds to keep shapes looking right */
    public void setBounds(int x, int y, int w, int h) {
	if (getNameFig() == null) return;
	Rectangle oldBounds = getBounds();

	Dimension nameMinimum = getNameFig().getMinimumSize();

	getNameFig().setBounds(x, y, w, nameMinimum.height);
	_attr.setBounds(x, y + getNameFig().getBounds().height,
			w, h - getNameFig().getBounds().height);
	_bigPort.setBounds(x + 1, y + 1, w - 2, h - 2);

	calcBounds(); //_x = x; _y = y; _w = w; _h = h;
	updateEdges();
	firePropChange("bounds", oldBounds, getBounds());
    }


} /* end class FigInstance */
