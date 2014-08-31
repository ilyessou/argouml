/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
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

import javax.swing.Action;
import javax.swing.JRadioButton;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.UndoableAction;
import org.argouml.uml.ui.UMLRadioButtonPanel;

/**
 * @author jaap.branderhorst@xs4all.nl, alexb
 */
public class ActionSetChangeability extends UndoableAction {

    private static final ActionSetChangeability SINGLETON =
        new ActionSetChangeability();

    /**
     * ADDONLY_COMMAND determines a changeability kind.
     * TODO: Removed from UML 2.x.  Phase out of UI - tfm - 20070529
     */
    @Deprecated
    public static final String ADDONLY_COMMAND = "addonly";

    /**
     * CHANGEABLE_COMMAND determines a changeability kind.
     */
    public static final String CHANGEABLE_COMMAND = "changeable";

    /**
     * FROZEN_COMMAND determines a changeability kind.
     */
    public static final String FROZEN_COMMAND = "frozen";

    /**
     * Constructor for ActionSetElementOwnershipSpecification.
     */
    protected ActionSetChangeability() {
        super(Translator.localize("Set"), null);
        // Set the tooltip string:
        putValue(Action.SHORT_DESCRIPTION, 
                Translator.localize("Set"));
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() instanceof JRadioButton) {
            JRadioButton source = (JRadioButton) e.getSource();
            String actionCommand = source.getActionCommand();
            Object target =
                ((UMLRadioButtonPanel) source.getParent()).getTarget();
            if (Model.getFacade().isAAssociationEnd(target)
		|| Model.getFacade().isAAttribute(target)) {
                Object m =  target;
                if (actionCommand.equals(CHANGEABLE_COMMAND)) {
                    Model.getCoreHelper().setReadOnly(m, false);
                } else if (actionCommand.equals(ADDONLY_COMMAND)) {
                    // TODO: Removed from UML 2.x - phase out usage - tfm 20070530
                    Model.getCoreHelper().setChangeability(
                            m, Model.getChangeableKind().getAddOnly());
                } else {
                    Model.getCoreHelper().setReadOnly(m, true);
                }

            }
        }
    }

    /**
     * @return Returns the SINGLETON.
     */
    public static ActionSetChangeability getInstance() {
        return SINGLETON;
    }
}
