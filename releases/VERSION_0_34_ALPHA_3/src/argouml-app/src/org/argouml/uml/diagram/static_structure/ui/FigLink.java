/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

import org.argouml.model.Model;
import org.argouml.model.UmlChangeEvent;
import org.argouml.notation.NotationProvider;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigTextGroup;
import org.argouml.uml.diagram.ui.PathItemPlacement;
import org.tigris.gef.presentation.Fig;

/**
 * Class to display graphics for a UML Link in a diagram. <p>
 * 
 * The underlined association name is shown next to the middle of the path.
 * 
 * TODO: Show more notation as described in the standard:
 * "A rolename may be shown at each end of the link. An association 
 * name may be shown near the path. If present, it is underlined
 * to indicate an instance. Links do not have instance names, 
 * they take their identity from the instances that they relate.
 * Multiplicity is not shown for links because they are instances. 
 * Other association adornments (aggregation, composition, 
 * navigation) may be shown on the link ends."
 */
public class FigLink extends FigEdgeModelElement {

    /*
     * Text group to contain name & stereotype
     */
    private FigTextGroup middleGroup; 

    private void initialize() {
        middleGroup.addFig(getNameFig());
        middleGroup.addFig(getStereotypeFig());
        addPathItem(middleGroup,
                new PathItemPlacement(this, middleGroup, 50, 25));
        getNameFig().setUnderline(true);
	getFig().setLineColor(LINE_COLOR);
	setBetweenNearestPoints(true);
    }

    /**
     * Create a Fig representing a Link
     * 
     * @param element owning UML element
     * @param settings render settings
     */
    public FigLink(Object element, DiagramSettings settings) {
        super(element, settings);
        middleGroup = new FigTextGroup(element, settings);
        initialize();
        updateListeners(null, getOwner());
    }

    /*
     * Nothing is editable, since a Link takes its identity 
     * from the Association.
     * 
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#canEdit(
     * org.tigris.gef.presentation.Fig)
     */
    protected boolean canEdit(Fig f) { return false; }

    /*
     * Listen also to the association, of which the link is an instantiation, 
     * since we want to update the rendering when 
     * the association name changes.
     * 
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#updateListeners(
     * java.lang.Object, java.lang.Object)
     */
    protected void updateListeners(Object oldOwner, Object newOwner) {
        if (oldOwner != null) {
            removeElementListener(oldOwner);
            Object oldAssociation = Model.getFacade().getAssociation(oldOwner);
            if (oldAssociation != null) {
                removeElementListener(oldAssociation);
            }
        }
        if (newOwner != null) {
            addElementListener(newOwner, 
                    new String[] {"remove", "association"});
            Object newAssociation = Model.getFacade().getAssociation(newOwner);
            if (newAssociation != null) {
                addElementListener(newAssociation, "name");
            }
        }
    }

    @Override
    public void notationRenderingChanged(NotationProvider np, String rendering) {
        /* Do nothing. */
    }

    @Override
    protected void updateLayout(UmlChangeEvent event) {
        super.updateLayout(event);
        updateNameText();
    }

    /**
     * Generate the notation for the modelelement and stuff it into the text Fig
     */
    protected void updateNameText() {
        if (getOwner() == null) {
            return;
        }
        String nameString = "";
        Object association = Model.getFacade().getAssociation(getOwner());
        if (association != null) {
            nameString = Model.getFacade().getName(association);
            if (nameString == null) {
                nameString = "";
            }
        }
        getNameFig().setText(nameString);
        calcBounds();
        setBounds(getBounds());
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#getDestination()
     */
    protected Object getDestination() {
        if (getOwner() != null) {
            return Model.getCommonBehaviorHelper().getDestination(getOwner());
        }
        return null;
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#getSource()
     */
    protected Object getSource() {
        if (getOwner() != null) {
            return Model.getCommonBehaviorHelper().getSource(getOwner());
        }
        return null;
    }

} /* end class FigLink */
