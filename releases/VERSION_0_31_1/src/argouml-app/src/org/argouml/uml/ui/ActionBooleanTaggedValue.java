/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
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

// Copyright (c) 2003-2007 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.UndoableAction;

/**
 * An action which can be used to create arbitrary tagged values which hold
 * boolean data. It is designed (and implicitly) relies on a UMLCheckBox2.
 *
 * @see UMLCheckBox2
 * @author mkl
 */
public class ActionBooleanTaggedValue extends UndoableAction {

    private String tagName;

    /**
     * The constructor takes the name of the tagged value as a string, which
     * will hold boolean data.
     *
     * @param theTagName
     *            the name of the taggedvalue containing boolean values.
     */
    public ActionBooleanTaggedValue(String theTagName) {
        super(Translator.localize("Set"), null);
        // Set the tooltip string:
        putValue(Action.SHORT_DESCRIPTION, 
                Translator.localize("Set"));
        tagName = theTagName;
    }

    /**
     * set the taggedvalue according to the condition of the checkbox. The
     * taggedvalue will be created if not existing.
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (!(e.getSource() instanceof UMLCheckBox2)) {
            return;
        }

        UMLCheckBox2 source = (UMLCheckBox2) e.getSource();
        Object obj = source.getTarget();

        if (!Model.getFacade().isAModelElement(obj)) {
            return;
        }

        boolean newState = source.isSelected();

        Object taggedValue = Model.getFacade().getTaggedValue(obj, tagName);
        if (taggedValue == null) {
            taggedValue =
                    Model.getExtensionMechanismsFactory().buildTaggedValue(
                            tagName, "");
            // TODO: Rework to use UML 1.4 TagDefinitions - tfm
            Model.getExtensionMechanismsHelper().addTaggedValue(
                    obj, taggedValue);
        }
        if (newState) {
            Model.getCommonBehaviorHelper().setValue(taggedValue, "true");
        } else {
            Model.getCommonBehaviorHelper().setValue(taggedValue, "false");
        }
    }
}
