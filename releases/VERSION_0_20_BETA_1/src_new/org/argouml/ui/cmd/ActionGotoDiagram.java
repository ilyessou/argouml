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

package org.argouml.ui.cmd;

import java.awt.event.ActionEvent;

import org.argouml.application.api.CommandLineInterface;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.GotoDialog;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.UMLAction;
import org.tigris.gef.base.Diagram;

////////////////////////////////////////////////////////////////
// items on view menu

/**
 * This Action will display a dialogbox listing all diagrams.
 * Doubleclicking on any listed diagram selects it.
 *
 */
public class ActionGotoDiagram
	extends UMLAction
	implements CommandLineInterface {

    /**
     * The constructor.
     */
    public ActionGotoDiagram() {
        super("action.goto-diagram", NO_ICON);
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
        new GotoDialog().setVisible(true);
    }

    /**
     * @see org.argouml.application.api.CommandLineInterface#doCommand(java.lang.String)
     */
    public boolean doCommand(String argument) {
	Project p = ProjectManager.getManager().getCurrentProject();
        Diagram d = p.getDiagram(argument);
        if (d != null) {
            TargetManager.getInstance().setTarget(d);
            return true;
        }
        return false;
    }
} /* end class ActionGotoDiagram */

