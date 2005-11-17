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

import java.beans.PropertyChangeListener;
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
public class FigOperationsCompartment extends FigFeaturesCompartment {
    /**
     * The constructor.
     *
     * @param x x
     * @param y y
     * @param w width
     * @param h height
     */
    public FigOperationsCompartment(int x, int y, int w, int h) {
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
        Fig operPort = this.getBigPort();

        int xpos = operPort.getX();
        int ypos = operPort.getY();
        int ocounter = 2; // Skip background port and seperator

        Collection behs = Model.getFacade().getOperations(cls);
        if (behs != null) {
            Iterator iter = behs.iterator();
            List figs = getFigs();
            CompartmentFigText oper = null;
            while (iter.hasNext()) {
                Object behaviouralFeature = iter.next();
                if (figs.size() <= ocounter) {
                    oper =
                        new FigFeature(
                                xpos + 1,
                                ypos + 1
                                + (ocounter - 1)
                                 	* FigNodeModelElement.ROWHEIGHT,
                                0,
                                FigNodeModelElement.ROWHEIGHT - 2,
                                operPort);
                    // bounds not relevant here
                    addFig(oper);
                } else {
                    oper = (CompartmentFigText) figs.get(ocounter);
                }
                oper.setText(Notation.generate((NotationContext) getGroup(),
                        behaviouralFeature));
                oper.setOwner(behaviouralFeature);
                oper.setUnderline(
                        Model.getScopeKind().
                        getClassifier().equals(
                                Model.getFacade().
                                getOwnerScope(behaviouralFeature)));
                // italics, if abstract
                //oper.setItalic(((MOperation)bf).isAbstract()); //
                //does not properly work (GEF bug?)
                if (Model.getFacade().isAbstract(behaviouralFeature)) {
                    oper.setFont(FigNodeModelElement.getItalicLabelFont());
                } else {
                    oper.setFont(FigNodeModelElement.getLabelFont());
                }
                oper.damage();
                oper.setBotMargin(0);
                ocounter++;
            }
            if (oper != null) {
                oper.setBotMargin(9);
            }
            if (figs.size() > ocounter) {
                //cleanup of unused operation FigText's
                for (int i = figs.size() - 1; i >= ocounter; i--) {
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
        Object model = project.getModel();
        Object voidType = project.findType("void");
        Object oper =
            Model.getCoreFactory()
                .buildOperation(classifier, model, voidType, propertyChangeListeners);
        populate();
        TargetManager.getInstance().setTarget(oper);

        // TODO: None of the following should be needed. Fig such as FigClass and
        // FigInterface should be listening for add/remove events and know when
        // an operation has been added and add a listener to the operation to themselves
        // See similar in ActionAddOperation
        Iterator it = project.findAllPresentationsFor(classifier).iterator();
        while (it.hasNext()) {
            PropertyChangeListener listener =
                (PropertyChangeListener) it.next();
            Model.getPump().removeModelEventListener(listener, oper);
            Model.getPump().addModelEventListener(listener, oper);
        }
    }
}
