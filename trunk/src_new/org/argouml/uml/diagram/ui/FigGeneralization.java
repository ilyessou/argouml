// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.PathConvPercent;
import org.tigris.gef.presentation.ArrowHeadTriangle;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigText;

/**
 * @author abonner@ics.uci.edu, jaap.branderhorst@xs4all.nl
 */
public class FigGeneralization extends FigEdgeModelElement {

    /**
     * Text box for discriminator
     */
    private FigText discriminator = new FigText(10, 30, 90, 20);

    ////////////////////////////////////////////////////////////////
    // constructors

    private ArrowHeadTriangle endArrow;

    /**
     * The constructor
     */
    public FigGeneralization() {
	addPathItem(getStereotypeFig(), new PathConvPercent(this, 50, 10));
	endArrow = new ArrowHeadTriangle();

	discriminator.setFont(getLabelFont());
	discriminator.setTextColor(Color.black);
	discriminator.setTextFilled(false);
	discriminator.setFilled(false);
	discriminator.setLineWidth(0);
	discriminator.setExpandOnly(false);
	discriminator.setReturnAction(FigText.END_EDITING);
	discriminator.setTabAction(FigText.END_EDITING);
	addPathItem(discriminator, new PathConvPercent(this, 40, -10));
	endArrow.setFillColor(Color.white);
	setDestArrowHead(endArrow);
	setBetweenNearestPoints(true);

	if (getLayer() == null) {
	    setLayer(ProjectManager.getManager()
		     .getCurrentProject().getActiveDiagram().getLayer());
	}

    }

    /**
     * The constructor that hooks the Fig into the UML element
     * @param edge the UML element
     * @param lay the layer
     */
    public FigGeneralization(Object edge, Layer lay) {
	this();
	setLayer(lay);
	setOwner(edge);

    }

    /**
     * The constructor that hooks the Fig into the UML element
     * @param edge the UML element
     */
    public FigGeneralization(Object edge) {
  	this();
  	setOwner(edge);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#canEdit(org.tigris.gef.presentation.Fig)
     */
    protected boolean canEdit(Fig f) { return false; }

    ////////////////////////////////////////////////////////////////
    // event handlers

    /**
     * This is called aftern any part of the UML MModelElement has
     * changed.
     *
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent e) {
        super.modelChanged(e);
        updateListeners(getOwner());
	updateDiscriminatorText();
    }

    /**
     * Updates the discriminator text. Called if the model is changed
     * and on construction time.
     */
    public void updateDiscriminatorText() {
  	Object me = getOwner(); // MGeneralization
  	if (me == null) {
	    return;
  	}
  	String disc = (String) Model.getFacade().getDiscriminator(me);
  	if (disc == null) {
	    disc = "";
  	}
  	discriminator.setText(disc);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        endArrow.setLineColor(getLineColor());
        super.paint(g);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setOwner(Object)
     */
    public void setOwner(Object own) {
	super.setOwner(own);
	if (Model.getFacade().isAGeneralization(own)) {
	    Object gen = own;	// MGeneralization
	    Object subType =
		Model.getFacade().getChild(gen); // MGeneralizableElement
	    Object superType =
		Model.getFacade().getParent(gen); // MGeneralizableElement
	    // due to errors in earlier releases of argouml it can
	    // happen that there is a generalization without a child
	    // or parent.
	    if (subType == null || superType == null) {
	        removeFromDiagram();
		return;
	    }
	    FigNode subTypeFN = (FigNode) getLayer().presentationFor(subType);
	    FigNode superTypeFN =
		(FigNode) getLayer().presentationFor(superType);
	    setSourcePortFig(subTypeFN);
	    setSourceFigNode(subTypeFN);
	    setDestPortFig(superTypeFN);
	    setDestFigNode(superTypeFN);

	} else
	    if (own != null) {
		throw new IllegalStateException("FigGeneralization "
						+ "has an illegal owner");
	    }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#updateListeners(java.lang.Object)
     */
    protected void updateListeners(Object newOwner) {
        Object oldOwner = getOwner();
        if (oldOwner != null) {
            Model.getPump().removeModelEventListener(this, oldOwner);
            if (Model.getFacade().isAGeneralization(oldOwner)) {
                Collection oldConns = Model.getFacade().getStereotypes(oldOwner);
                for (Iterator i = oldConns.iterator(); i.hasNext();) {
                    Model.getPump().removeModelEventListener(this, i.next());
                }
            }
        }
        if (Model.getFacade().isAGeneralization(newOwner)) {
            Model.getPump().addModelEventListener(this, newOwner);
            Collection newConns = Model.getFacade().getStereotypes(newOwner);
            for (Iterator i = newConns.iterator(); i.hasNext();) {
                Model.getPump().addModelEventListener(this, i.next());
            }
        }
    }

    /**
     * This method is called after the fig is loaded from pgml. Implemented here
     * to fix errors with the model concerning the fig not having an owner.
     * @see org.tigris.gef.presentation.Fig#postLoad()
     */
    public void postLoad() {
        super.postLoad();
        Object own = getOwner();
        if (own == null) {
            fixModel();
        }
    }

    private void fixModel() {
        Fig sourceFig = getSourceFigNode();
        Fig destFig = getDestFigNode();
        Object source = sourceFig.getOwner();
        Object dest = destFig.getOwner();
        if (Model.getFacade().isAGeneralizableElement(source)
	        && Model.getFacade().isAGeneralizableElement(dest)) {
            setOwner(Model.getCoreFactory().buildGeneralization(source,
								  dest));
        }
    }
} /* end class FigGeneralization */
