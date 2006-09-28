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

// $Id$

package org.argouml.uml.diagram.static_structure.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Polygon;

import org.apache.log4j.Logger;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigPoly;
import org.argouml.model.ModelFacade;

/** Class to display graphics for a UML model in a class diagram. */

public class FigModel extends FigPackage {
    /**
     * @deprecated by Linus Tolke as of 0.15.4. Use your own logger in your
     * class. This will be removed.
     */
    protected static Logger cat = Logger.getLogger(FigModel.class);

    protected FigPoly _figPoly;

    ////////////////////////////////////////////////////////////////
    // constructors

    public FigModel() {
        super();
        _figPoly = new FigPoly(Color.black, Color.black);
        int[] xpoints = {
	    125, 130, 135, 125
	};
        int[] ypoints = {
	    45, 40, 45, 45
	};
        Polygon polygon = new Polygon(xpoints, ypoints, 4);
        _figPoly.setPolygon(polygon);
        _figPoly.setFilled(false);
        addFig(_figPoly);
        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
        updateEdges();
        
    }

    public FigModel(GraphModel gm, Object node) {
        this();
        setOwner(node);

        // If this figure is created for an existing package node in the
        // metamodel, set the figure's name according to this node. This is
        // used when the user click's on 'add to diagram' in the navpane.
        // Don't know if this should rather be done in one of the super
        // classes, since similar code is used in FigClass.java etc.
        // Andreas Rueckert <a_rueckert@gmx.net>
        if (ModelFacade.isAModel(node) && ModelFacade.getName(node) != null) {
            getNameFig().setText(ModelFacade.getName(node));
	}
    }

    public String placeString() {
        return "new Model";
    }

} /* end class FigModel */
