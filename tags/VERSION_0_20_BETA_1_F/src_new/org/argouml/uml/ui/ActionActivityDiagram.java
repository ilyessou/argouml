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

package org.argouml.uml.ui;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.activity.ui.UMLActivityDiagram;
import org.argouml.uml.diagram.ui.UMLDiagram;

/**
 * Action to trigger creation of a new activity diagram.<p>
 *
 * This used to extend the ActionStateDiagram, but lead to problems
 * implementing shouldBeEnabled() and isValidNamespace().
 */
public class ActionActivityDiagram extends ActionAddDiagram {

    private static final Logger LOG =
        Logger.getLogger(ActionStateDiagram.class);

    /**
     * Constructor.
     */
    public ActionActivityDiagram() {
        super("action.activity-diagram");
    }

    /**
     * @see org.argouml.uml.ui.ActionAddDiagram#createDiagram(java.lang.Object)
     */
    public UMLDiagram createDiagram(Object ns) {
        Object target = TargetManager.getInstance().getModelTarget();
        Object/*MActivityGraph*/ graph =
	    Model.getActivityGraphsFactory().buildActivityGraph(target);
        /*if (Model.getFacade().isABehavioralFeature(target)) {
            ns = Model.getFacade().getNamespace(target);
            // this fails always, see issue 1817
        }*/
        return (UMLDiagram)DiagramFactory.getInstance().createDiagram(
                UMLActivityDiagram.class,
                ns,
                graph);
    }

    /**
     * An ActivityGraph specifies the dynamics of<br>
     *       (i) a Package, or<br>
     *      (ii) a Classifier (including UseCase), or<br>
     *     (iii) a BehavioralFeature.<p>
     *
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     */
    public boolean shouldBeEnabled() {
        Object obj = TargetManager.getInstance().getModelTarget();
        return super.shouldBeEnabled()
            && Model.getActivityGraphsHelper()
                .isAddingActivityGraphAllowed(obj);
    }


    /**
     * @see org.argouml.uml.ui.ActionAddDiagram#isValidNamespace(java.lang.Object)
     */
    public boolean isValidNamespace(Object handle) {
        if (!Model.getFacade().isANamespace(handle)) {
            LOG.error("No namespace as argument");
            LOG.error(handle);
            throw new IllegalArgumentException(
                "The argument " + handle + "is not a namespace.");
        }
        return Model.getActivityGraphsHelper()
            .isAddingActivityGraphAllowed(handle);
    }

} /* end class ActionActivityDiagram */
