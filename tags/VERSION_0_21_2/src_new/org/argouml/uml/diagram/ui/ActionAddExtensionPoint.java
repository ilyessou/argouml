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

import java.awt.event.ActionEvent;

import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.UMLAction;

/**
 * A class to implement the addition of extension points to use cases.<p>
 *
 * This is a singleton. Implemented with a private constructor and a static
 * access method. Marked as final, since it can't sensibly be subclassed (the
 * access method wouldn't work properly).<p>
 *
 * @author  Jeremy Bennett (mail@jeremybennett.com).
 * @stereotype singleton
 */
public final class ActionAddExtensionPoint extends UMLAction {


    ///////////////////////////////////////////////////////////////////////////
    //
    // Class variables
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Our private copy of the instance. Only accessible through the proper
     * access method.<p>
     */
    private static ActionAddExtensionPoint singleton;


    ///////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructor is private, since it cannot be called directly for a
     * singleton. Make use of the access funtion.<p>
     */
    public ActionAddExtensionPoint() {
        super("button.new-extension-point", true, HAS_ICON);
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Main methods
    //
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Get the single instance of the action.<p>
     *
     * Since we are a singleton, this is the only way of accessing the
     * instance, which is created if it does not exist.<p>
     *
     * @return The singleton instance.
     */
    public static ActionAddExtensionPoint singleton() {

        // Create the singleton if it does not exist, and then return it

        if (singleton == null) {
            singleton = new ActionAddExtensionPoint();
        }

        return singleton;
    }

    /**
     * Called if this action is invoked.<p>
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * @param ae  The action that caused us to be invoked.
     */
    public void actionPerformed(ActionEvent ae) {

        // Find the target in the project browser. We can only do anything if
        // its a use case.

	Object         target = TargetManager.getInstance().getModelTarget();

	if (!(Model.getFacade().isAUseCase(target))) {
            return;
        }

        // Create a new extension point and make it the browser target. Then
        // invoke the superclass action method.

	Object ep =
            Model.getUseCasesFactory()
            	.buildExtensionPoint(target);

        TargetManager.getInstance().setTarget(ep);
	super.actionPerformed(ae);
    }


    /**
     * A predicate to determine if this action should be enabled.<p>
     *
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     * @return  <code>true</code> if the superclass believes we should be
     *          enabled and the target is a use case. <code>false</code>
     *          otherwise.
     */
    public boolean shouldBeEnabled() {
	Object target = TargetManager.getInstance().getModelTarget();

	return super.shouldBeEnabled()
                && (Model.getFacade().isAUseCase(target));
    }

} /* end class ActionAddExtensionPoint */
