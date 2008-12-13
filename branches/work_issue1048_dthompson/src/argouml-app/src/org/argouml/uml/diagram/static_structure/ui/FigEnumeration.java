// $Id$
// Copyright (c) 1996-2008 The Regents of the University of California. All
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

package org.argouml.uml.diagram.static_structure.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

import org.argouml.model.AssociationChangeEvent;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.Model;
import org.argouml.ui.ArgoJMenu;
import org.argouml.uml.diagram.ui.EnumLiteralsCompartmentContainer;
import org.argouml.uml.diagram.ui.FigEnumLiteralsCompartment;
import org.argouml.uml.diagram.ui.FigStereotypesGroup;
import org.argouml.uml.ui.foundation.core.ActionAddEnumerationLiteral;
import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;

/**
 * Class to display graphics for a UML Enumeration in a diagram.
 * It depends on FigDataType for most of its behavior.<p>
 * 
 */
public class FigEnumeration extends FigDataType 
    implements EnumLiteralsCompartmentContainer {

    /**
     * Serial version (generated)
     */
    private static final long serialVersionUID = 3333154292883077250L;

    /**
     * The Fig that represents the literals compartment.
     */
    private FigEnumLiteralsCompartment literalsCompartment;

    /**
     * Main constructor for a {@link FigEnumeration}.
     */
    public FigEnumeration() {
        super();
        FigStereotypesGroup fsc = getStereotypeFig();
        fsc.setKeyword("enumeration");

        enableSizeChecking(true);
        setSuppressCalcBounds(false);

        addFig(getLiteralsCompartment()); // This creates the compartment.
        setBounds(getBounds());
    }

    /**
     * Constructor for use if this figure is created for an
     * existing interface node in the metamodel.
     *
     * @param gm   Not actually used in the current implementation
     *
     * @param node The UML object being placed.
     */
    public FigEnumeration(GraphModel gm, Object node) {
        this();
        enableSizeChecking(true);
        setEnumLiteralsVisible(true);
        setOwner(node);
        literalsCompartment.populate();
        setBounds(getBounds());
    }

    /*
     * @see org.argouml.uml.diagram.static_structure.ui.FigDataType#makeSelection()
     */
    @Override
    public Selection makeSelection() {
        return new SelectionEnumeration(this);
    }

    @Override
    public Object clone() {
        FigEnumeration clone = (FigEnumeration) super.clone();
        clone.literalsCompartment = 
            (FigEnumLiteralsCompartment) literalsCompartment.clone();
        return clone;
    }

    /*
     * @see org.argouml.uml.diagram.static_structure.ui.FigClassifierBox#buildAddMenu()
     */
    @Override
    protected ArgoJMenu buildAddMenu() {
        ArgoJMenu addMenu = super.buildAddMenu();
        
        Action addEnumerationLiteral = new ActionAddEnumerationLiteral();
        addEnumerationLiteral.setEnabled(isSingleTarget());
        addMenu.add(addEnumerationLiteral);
        return addMenu;
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        if (mee instanceof AssociationChangeEvent 
                || mee instanceof AttributeChangeEvent) {
            renderingChanged();
            updateListeners(getOwner(), getOwner());
        }
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#renderingChanged()
     */
    @Override
    public void renderingChanged() {
        super.renderingChanged();
               if (getOwner() != null) {
            updateEnumLiterals();
        }
    }
    
    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateListeners(java.lang.Object)
     */
    @Override
    protected void updateListeners(Object oldOwner, Object newOwner) {
        Set<Object[]> l = new HashSet<Object[]>();
        if (newOwner != null) {
            // add the listeners to the newOwner
            l.add(new Object[] {newOwner, null});
            // and its stereotypes
            for (Object stereo : Model.getFacade().getStereotypes(newOwner)) {
                l.add(new Object[] {stereo, null});                
            }
            // and its features
            for (Object feat : Model.getFacade().getFeatures(newOwner)) {
                l.add(new Object[] {feat, null});
                // and the stereotypes of its features
                for (Object stereo : Model.getFacade().getStereotypes(feat)) {
                    l.add(new Object[] {stereo, null});
                }
            }
            // and its enumerationLiterals
            for (Object literal : Model.getFacade().getEnumerationLiterals(
                    newOwner)) {
                l.add(new Object[] {literal, null});
            }
        }
        // And now add listeners to them all:
        updateElementListeners(l);

    }

    /**
     * Update (i.e. redraw) the compartment with the literals.
     */
    protected void updateEnumLiterals() {
        if (!literalsCompartment.isVisible()) {
            return;
        }
        literalsCompartment.populate();

        // TODO: make setBounds, calcBounds and updateBounds consistent
        setBounds(getBounds());
    }

    /*
     * @see org.argouml.uml.diagram.static_structure.ui.FigDataType#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        // Start with the minimum for our parent
        Dimension aSize = super.getMinimumSize();

        aSize = addChildDimensions(aSize, literalsCompartment);
        
        return aSize;
    }
    
    /*
     * @see org.tigris.gef.presentation.Fig#setBoundsImpl(int, int, int, int)
     */
    @Override
    protected void setStandardBounds(final int x, final int y, final int w,
            final int h) {

        // Save our old boundaries so it can be used in property message later
        Rectangle oldBounds = getBounds();

        // set bounds of big box
        getBigPort().setBounds(x, y, w, h);
        borderFig.setBounds(x, y, w, h);

        getNameFig().setLineWidth(0);
        
        // Vertical whitespace to be distributed
        // TODO: This continually adds more whitespace.  Figure out the problem.
        //final int whitespace = Math.max(0, h - getMinimumSize().height);
        final int whitespace = 0;
                
        int currentHeight = 0;

        if (getStereotypeFig().isVisible()) {
            int stereotypeHeight = getStereotypeFig().getMinimumSize().height;
            getStereotypeFig().setBounds(
                    x,
                    y,
                    w,
                    stereotypeHeight);
            currentHeight += stereotypeHeight;
        }

        int nameHeight = getNameFig().getMinimumSize().height;
        getNameFig().setBounds(x, y + currentHeight, w, nameHeight);
        currentHeight += nameHeight;

        if (getLiteralsCompartment().isVisible()) {
            int literalsHeight = 
                getLiteralsCompartment().getMinimumSize().height;
            literalsHeight += whitespace / 2;
            getLiteralsCompartment().setBounds(
                    x,
                    y + currentHeight,
                    w,
                    literalsHeight);
            currentHeight += literalsHeight;
        }
        
        if (getOperationsFig().isVisible()) {
            int operationsHeight = getOperationsFig().getMinimumSize().height;
            operationsHeight += whitespace / 2;
            getOperationsFig().setBounds(
                    x,
                    y + currentHeight,
                    w,
                    operationsHeight);
            currentHeight += operationsHeight;
        }

        // Now force calculation of the bounds of the figure, update the edges
        // and trigger anyone who's listening to see if the "bounds" property
        // has changed.

        calcBounds();
        updateEdges();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * @return the Fig for the EnumerationLiterals compartment
     */
    public FigEnumLiteralsCompartment getLiteralsCompartment() {
        // Set bounds will be called from our superclass constructor before
        // our constructor has run, so make sure this gets set up if needed.
        if (literalsCompartment == null) {
            literalsCompartment = new FigEnumLiteralsCompartment(getOwner(),
                    DEFAULT_COMPARTMENT_BOUNDS, getSettings());
        }
        return literalsCompartment;
    }
    
    /**
     * @return true if the literals compartment is visible
     */
    public boolean isEnumLiteralsVisible() {
        return literalsCompartment.isVisible();
    }

    /**
     * @param isVisible true will show the enumeration literal compartment
     */
    public void setEnumLiteralsVisible(boolean isVisible) {
        setCompartmentVisible(literalsCompartment, isVisible);
    }
    
    /**
     * @return the bounds of the EnumerationLiterals compartment
     */
    public Rectangle getEnumLiteralsBounds() {
        return literalsCompartment.getBounds();
    }
    
} 
