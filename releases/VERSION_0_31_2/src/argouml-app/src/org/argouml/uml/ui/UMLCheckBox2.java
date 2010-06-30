/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2002-2007 The Regents of the University of California. All
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBox;

import org.argouml.model.Model;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.tigris.gef.presentation.Fig;

/**
 * The checkbox to be used to show boolean UML attributes in the GUI's. Mostly
 * used on proppanels. Other GUI elements (like UMLLinkedList) divide the
 * responsibility of showing an attribute and maintaining the state of the
 * attribute between a GUI element and a model. This is not the case for the
 * UMLCheckBox2. Reason for this is that the model is just too simple to need
 * extra classes for the model.
 * 
 * @since Oct 12, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public abstract class UMLCheckBox2 extends JCheckBox
    implements TargetListener, PropertyChangeListener {

    private Object checkBoxTarget;
    private String propertySetName;

    /**
     * Constructor for UMLCheckBox2.
     * @param text the text of the check box
     * @param a the action we're going to listen to
     * @param name the property set name
     */
    public UMLCheckBox2(String text, Action a, String name) {
        super(text);
        setFont(LookAndFeelMgr.getInstance().getStandardFont());
        propertySetName = name;
        addActionListener(a);

        setActionCommand((String) a.getValue(Action.ACTION_COMMAND_KEY));
    }

    /*
     * The property value has changed so rebuild our view.
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        buildModel();
    }

    /**
     * Return the target. 
     *
     * @return the target
     */
    public Object getTarget() {
        return checkBoxTarget;
    }

    /**
     * Sets the target. This method will not be used until the target does
     * not come via the container.
     * @param target The target to set
     */
    public void setTarget(Object target) {
        target = target instanceof Fig ? ((Fig) target).getOwner() : target;
        if (Model.getFacade().isAUMLElement(checkBoxTarget)) {
            Model.getPump().removeModelEventListener(
                    this, checkBoxTarget, propertySetName);
        }

        if (Model.getFacade().isAUMLElement(target)) {
            checkBoxTarget = target;
            Model.getPump().addModelEventListener(
                    this, checkBoxTarget, propertySetName);
            buildModel();
        }
    }

    /**
     * Builds the model. That is: it sets the checkbox to true or
     * false. The name of this method is chosen to be compliant with
     * for example UMLModelElementListModel2.
     */
    public abstract void buildModel();


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
}
