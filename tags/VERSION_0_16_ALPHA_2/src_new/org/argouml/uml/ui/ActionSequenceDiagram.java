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

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.ui.explorer.ExplorerEventAdaptor;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram;

/** 
 * Action to add a new sequence diagram.<p>
 *
 * Fully rebuild starting 1-8-2003<p>
 *
 * This action is subclassed from UMLChangeAction and not
 * ActionAddDiagram since the namespace stuff in ActionAddDiagram
 * should be refactored out.<p>
 *
 * @author jaap.branderhorst@xs4all.nl
 */
public class ActionSequenceDiagram extends UMLChangeAction {

    ////////////////////////////////////////////////////////////////
    // static variables

    public static ActionSequenceDiagram SINGLETON = new ActionSequenceDiagram();

    ////////////////////////////////////////////////////////////////
    // constructors

    private ActionSequenceDiagram() {
        super("action.sequence-diagram", true, true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object target = TargetManager.getInstance().getModelTarget();
        Object owner = null;
        if (ModelFacade.isAClassifier(target)) {
            owner = ModelFacade.getNamespace(target);
        } else if (ModelFacade.isAOperation(target)) {
            owner = ModelFacade.getNamespace(target);
        }
        Object collaboration =
            UmlFactory.getFactory().getCollaborations().buildCollaboration(
                owner,
                target);
        UMLSequenceDiagram diagram = new UMLSequenceDiagram(collaboration);
        ProjectManager.getManager().getCurrentProject().addMember(diagram);
        TargetManager.getInstance().setTarget(diagram);
	ExplorerEventAdaptor.getInstance().modelElementChanged(owner);
    }

    /**
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     */
    public boolean shouldBeEnabled() {
        Object target = TargetManager.getInstance().getModelTarget();
        if (ModelFacade.isAClassifier(target)
            || ModelFacade.isAOperation(target)) {
            return true;
        }

        return false;
    }


} /* end class ActionSequenceDiagram */
