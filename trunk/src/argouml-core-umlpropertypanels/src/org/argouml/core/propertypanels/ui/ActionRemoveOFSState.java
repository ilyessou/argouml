// $Id$
// Copyright (c) 2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.core.propertypanels.ui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractActionRemoveElement;

/**
 * @author mkl
 */
class ActionRemoveOFSState extends AbstractActionRemoveElement {

    /**
     * Constructor.
     */
    public ActionRemoveOFSState() {
        super(Translator.localize("menu.popup.remove"));
    }

    /*
     * @see org.tigris.gef.undo.UndoableAction#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object state = getObjectToRemove();
        if (state != null) {
            Object t = getTarget();
            if (Model.getFacade().isAObjectFlowState(t)) {
                Object type = Model.getFacade().getType(t);
                if (Model.getFacade().isAClassifierInState(type)) {
                    Collection states =
                        new ArrayList(
                            Model.getFacade().getInStates(type));
                    states.remove(state);
                    Model.getActivityGraphsHelper()
                            .setInStates(type, states);
                }
            }
        }
    }
}
