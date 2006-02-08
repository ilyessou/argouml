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

package org.argouml.ui.targetmanager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.tigris.gef.undo.UndoableAction;

/**
 * Action to add an operation to a classifier.
 */
class ActionAddOperation extends UndoableAction {
    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * The constructor.
     */
    ActionAddOperation() {
        super(Translator.localize("button.new-operation"),
                ResourceLoaderWrapper.lookupIcon("button.new-operation"));
    }

    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {

        super.actionPerformed(ae);

        Project project = ProjectManager.getManager().getCurrentProject();
        Object target =  TargetManager.getInstance().getModelTarget();
        Object classifier = null;

        if (Model.getFacade().isAClassifier(target)) {
            classifier = target;
        } else if (Model.getFacade().isAFeature(target)) {
            classifier = Model.getFacade().getOwner(target);
        } else {
            return;
        }

        Collection propertyChangeListeners =
            project.findFigsForMember(classifier);
        Object model = project.getModel();
        Object voidType = project.findType("void");
        Object oper =
            Model.getCoreFactory().buildOperation(
                classifier, model, voidType, propertyChangeListeners);
        TargetManager.getInstance().setTarget(oper);

        // TODO: None of the following should be needed.
        // Fig such as FigClass and FigInterface should be listening for
        // add/remove events and know when an operation has been added and
        // add a listener to the operation to themselves
        // See similar in FigOperationsCompartment
        Iterator it = project.findAllPresentationsFor(classifier).iterator();
        while (it.hasNext()) {
            PropertyChangeListener listener =
                (PropertyChangeListener) it.next();
            Model.getPump().removeModelEventListener(listener, oper);
            Model.getPump().addModelEventListener(listener, oper);
        }
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = -1383845502957256177L;
} /* end class ActionAddOperation */
