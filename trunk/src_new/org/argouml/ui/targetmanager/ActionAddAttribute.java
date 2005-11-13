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
import java.util.Collection;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.tigris.gef.undo.UndoableAction;

/**
 * Action to add an attribute to a classifier.<p>
 *
 * @stereotype singleton
 */
class ActionAddAttribute extends UndoableAction {
    /**
     * The constructor for this class.
     */
    ActionAddAttribute() {
        super(Translator.localize("button.new-attribute"),
                ResourceLoaderWrapper.lookupIcon("button.new-attribute"));
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {

        super.actionPerformed(ae);

        Object target = TargetManager.getInstance().getModelTarget();
        Object classifier = null;

        if (Model.getFacade().isAClassifier(target)
                || Model.getFacade().isAAssociationEnd(target)) {
            classifier = target;
        } else if (Model.getFacade().isAAttribute(target)) {
            classifier = Model.getFacade().getOwner(target);
        } else {
            return;
        }

        Project project = ProjectManager.getManager().getCurrentProject();

        Collection propertyChangeListeners =
            project.findFigsForMember(classifier);
        Object intType = project.findType("int");
        Object model = project.getModel();
        Object attr =
            Model.getCoreFactory().buildAttribute(
                classifier,
                model,
                intType,
                propertyChangeListeners);
        TargetManager.getInstance().setTarget(attr);
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = -111785878370086329L;
} /* end class ActionAddAttribute */
