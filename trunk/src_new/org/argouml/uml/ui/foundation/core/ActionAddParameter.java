// $Id$
// Copyright (c) 2004-2005 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.core;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.Action;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNewModelElement;


/**
 * Action to create a new parameter "next to" the existing parameter
 * (that is actually selected by the user).
 *
 * @author Michiel
 */
class ActionAddParameter extends AbstractActionNewModelElement {

    /**
     * The constructor.
     */
    public ActionAddParameter() {
        super("button.new-parameter");
        putValue(Action.NAME, Translator.localize("button.new-parameter"));
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object target = TargetManager.getInstance().getModelTarget();
        Object feature = null;
        if (Model.getFacade().isAParameter(target)) {
            feature = Model.getFacade().getBehavioralFeature(target);
            if (feature != null) {
                Project currentProject =
                    ProjectManager.getManager().getCurrentProject();
                Object model = currentProject.getModel();
                Object voidType = currentProject.findType("void");
                Collection propertyChangeListeners =
                    currentProject.findFigsForMember(feature);
                TargetManager.getInstance().setTarget(
                        Model.getCoreFactory().buildParameter(
                                feature, model, voidType,
                                propertyChangeListeners));
                super.actionPerformed(e);
            }
        }
    }
}
