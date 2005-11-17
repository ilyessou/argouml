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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.notation.Notation;
import org.argouml.notation.NotationContext;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.static_structure.ui.FigFeature;
import org.tigris.gef.presentation.Fig;

/**
 * @author Bob Tarling
 */
public class FigAttributesCompartment extends FigFeaturesCompartment {
    /**
     * The constructor.
     *
     * @param x x
     * @param y y
     * @param w width
     * @param h height
     */
    public FigAttributesCompartment(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigFeaturesCompartment#populate()
     */
    public void populate() {
        if (!isVisible()) {
            return;
        }
        Object cls = /*(MClassifier)*/ getGroup().getOwner();
        Fig attrPort = this.getBigPort();
        int xpos = attrPort.getX();
        int ypos = attrPort.getY();
        int acounter = 2; // Skip background port and seperator

        Collection strs = Model.getFacade().getStructuralFeatures(cls);
        if (strs != null) {
            Iterator iter = strs.iterator();
            List figs = getFigs();
            CompartmentFigText attr = null;
            while (iter.hasNext()) {
                Object structuralFeature = iter.next();
                if (figs.size() <= acounter) {
                    attr =
                        new FigFeature(
                                xpos + 1,
                                ypos + 1
                                + (acounter - 1)
                                	* FigNodeModelElement.ROWHEIGHT,
                                0,
                                FigNodeModelElement.ROWHEIGHT - 2,
                                attrPort);
                    // bounds not relevant here
                    addFig(attr);
                } else {
                    attr = (CompartmentFigText) figs.get(acounter);
                }
                attr.setText(Notation.generate((NotationContext) getGroup(),
                        structuralFeature));
                attr.setOwner(structuralFeature); //TODO: update the model again here?
                /* This causes another event, and modelChanged() called,
                 * and updateAttributes() called again...
                 */

                // underline, if static
                attr.setUnderline(
                        Model.getScopeKind().
                        getClassifier().equals(Model.getFacade().
                                getOwnerScope(structuralFeature)));
                attr.setBotMargin(0);
                acounter++;
            }
            if (attr != null) {
                attr.setBotMargin(6);
            }
            if (figs.size() > acounter) {
                //cleanup of unused attribute FigText's
                for (int i = figs.size() - 1; i >= acounter; i--) {
                    removeFig((Fig) figs.get(i));
                }
            }
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigFeaturesCompartment#createFeature()
     */
    public void createFeature() {
        Object classifier = getGroup().getOwner();

        Project project = ProjectManager.getManager().getCurrentProject();

        Collection propertyChangeListeners =
            project.findFigsForMember(classifier);
        Object intType = project.findType("int");
        Object model = project.getModel();
        Object attr = Model.getCoreFactory().buildAttribute(
                classifier,
                model,
                intType,
                propertyChangeListeners);
        populate();
        TargetManager.getInstance().setTarget(attr);
    }
}
