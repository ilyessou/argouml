// $Id$
// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.use_cases;

import java.awt.event.ActionEvent;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLChangeAction;
import org.argouml.uml.ui.UMLComboBox2;

/**
 * Sets the base of an extend. Updates both the model (NSUML) as the diagrams.
 * @since Oct 5, 2002
 * @author jaap.branderhorst@xs4all.nl
 * @stereotype singleton
 */
public class ActionSetExtendBase extends UMLChangeAction {

    private static final ActionSetExtendBase SINGLETON =
        new ActionSetExtendBase();

    /**
     * Constructor for ActionSetExtendBase.
     */
    protected ActionSetExtendBase() {
        super(Translator.localize("action.set"), false, NO_ICON);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        Object newBase = null;
        Object oldBase = null;
        Object extend = null;
        if (source instanceof UMLComboBox2) {
            UMLComboBox2 combo = (UMLComboBox2) source;
            newBase = /*(MUseCase)*/ combo.getSelectedItem();
            Object o = combo.getTarget();
            if (org.argouml.model.ModelFacade.isAExtend(o)) {
                extend = /*(MExtend)*/ o;
                o = combo.getSelectedItem();
                if (org.argouml.model.ModelFacade.isAUseCase(o)) {
                    newBase = /*(MUseCase)*/ o;
                    oldBase = ModelFacade.getBase(extend);
                    if (newBase != oldBase) {
                        ModelFacade.setBase(extend, newBase);
                    }
                } else {
                    if (o != null && o.equals("")) {
                        ModelFacade.setBase(extend, null);
                    }
                }

            }

        }
    }

    /**
     * @return Returns the SINGLETON.
     */
    public static ActionSetExtendBase getInstance() {
        return SINGLETON;
    }
}
