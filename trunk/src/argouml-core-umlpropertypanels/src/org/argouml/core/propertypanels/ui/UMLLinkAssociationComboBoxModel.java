/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Action;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.UndoableAction;

/**
 * The model for the combobox to show the Association of the Link.
 *
 * @author Michiel
 */
class UMLLinkAssociationComboBoxModel extends UMLComboBoxModel {

    /**
     * The UID.
     */
    private static final long serialVersionUID = 3232437122889409351L;

    /**
     * Constructor for UMLModelElementNamespaceComboBoxModel.
     */
    public UMLLinkAssociationComboBoxModel(
            final String propertyName,
            final Object target) {
        super(target, propertyName, true);
    }

    /*
     * @see org.argouml.uml.ui.UMLComboBoxModel#isValidElement(Object)
     */
    protected boolean isValidElement(Object o) {
        return Model.getFacade().isAAssociation(o);
    }

    /**
     * To simplify implementation, we list all associations
     * found with any of the Classifiers
     * represented by the linked Instances. <p>
     *
     * TODO: Make a foolproof algorithm that only allows selecting associations
     * that create a correct model. Also take into account n-ary associations
     * and associationclasses. This algo best goes in the model subsystem, e.g.
     * in a method getAllPossibleAssociationsForALink().
     *
     * @see org.argouml.uml.ui.UMLComboBoxModel#buildModelList()
     */
    protected void buildModelList() {
        Collection linkEnds;
        Collection associations = new HashSet();
        Object t = getTarget();
        if (Model.getFacade().isALink(t)) {
            linkEnds = Model.getFacade().getConnections(t);
            Iterator ile = linkEnds.iterator();
            while (ile.hasNext()) {
                Object instance = Model.getFacade().getInstance(ile.next());
                Collection c = Model.getFacade().getClassifiers(instance);
                Iterator ic = c.iterator();
                while (ic.hasNext()) {
                    Object classifier = ic.next();
                    Collection ae =
                        Model.getFacade().getAssociationEnds(classifier);
                    Iterator iae = ae.iterator();
                    while (iae.hasNext()) {
                        Object associationEnd = iae.next();
                        Object association =
                            Model.getFacade().getAssociation(associationEnd);
                        associations.add(association);
                    }
                }
            }
        }
        setElements(associations);
    }

    /*
     * @see org.argouml.uml.ui.UMLComboBoxModel#getSelectedModelElement()
     */
    protected Object getSelectedModelElement() {
        if (Model.getFacade().isALink(getTarget())) {
            return Model.getFacade().getAssociation(getTarget());
        }
        return null;
    }
    
    public Action getAction() {
        return new ActionSetLinkAssociation();
    }
    
    /**
     *
     * @author Michiel
     */
    class ActionSetLinkAssociation extends UndoableAction {

       /**
        * The UID.
        */
       private static final long serialVersionUID = 6168167355078835252L;

       /**
        * Constructor for ActionSetModelElementNamespace.
        */
       public ActionSetLinkAssociation() {
           super(Translator.localize("Set"), null);
                   // Set the tooltip string:
           putValue(Action.SHORT_DESCRIPTION, Translator.localize("Set"));
       }

       /*
        * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
        */
       @Override
       public void actionPerformed(ActionEvent e) {
           super.actionPerformed(e);
           Object source = e.getSource();
           Object oldAssoc = null;
           Object newAssoc = null;
           Object link = null;
           UMLComboBox box = (UMLComboBox) source;
           Object o = box.getTarget();
           if (Model.getFacade().isALink(o)) {
               link = o;
               oldAssoc = Model.getFacade().getAssociation(o);
           }
           Object n = box.getSelectedItem();
           if (Model.getFacade().isAAssociation(n)) {
               newAssoc = n;
           }
           if (newAssoc != oldAssoc && link != null && newAssoc != null) {
               Model.getCoreHelper().setAssociation(link, newAssoc);
           }
       }
   }
}
