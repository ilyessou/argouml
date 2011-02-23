/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    penyaskito
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.core;

import javax.swing.DefaultListModel;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;

/**
 * The properties panel for an EnumerationLiteral.
 * 
 * TODO: Everytime you select the EnumerationliteralProppanel, 
 * a new instance of the EnumerationListModel is created,
 * and it registers as targetlistener. 
 * All these models keep getting target change events... 
 * Make this conform with all others!
 * @deprecated in 0.31.2 by Bob Tarling  This is replaced by the XML property
 * panels module
 */
@Deprecated
public class PropPanelEnumerationLiteral extends PropPanelModelElement {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = 1486642919681744144L;

    /**
     * Construct a property panel for UML EnumerationLiterals.
     */
    public PropPanelEnumerationLiteral() {
        super("label.enumeration-literal", lookupIcon("EnumerationLiteral"));

        addField(Translator.localize("label.name"),
                getNameTextField());

        addField(Translator.localize("label.enumeration"),
                getSingleRowScroll(new EnumerationListModel()));

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionAddLiteral());
        addAction(new ActionNewStereotype());
        addAction(getDeleteAction());
    }
}

/**
 * The list model for the enumeration of a literal.
 *
 * @author mvw@tigris.org
 */
class EnumerationListModel extends DefaultListModel implements TargetListener {

    /**
     * Constructor for EnumerationListModel.
     */
    public EnumerationListModel() {
        super();
        setTarget(TargetManager.getInstance().getModelTarget());
        TargetManager.getInstance().addTargetListener(this);
    }

    /*
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /*
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /*
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    private void setTarget(Object t) {
        removeAllElements();
        if (Model.getFacade().isAEnumerationLiteral(t)) {
            addElement(Model.getFacade().getEnumeration(t));
        }
    }
}
