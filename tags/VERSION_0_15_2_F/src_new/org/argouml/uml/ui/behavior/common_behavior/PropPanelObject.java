// $Id$
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// File: PropPanelObject.java
// Classes: PropPanelObject
// Original Author: 5eichler@informatik.uni-hamburg.de
// $Id$

package org.argouml.uml.ui.behavior.common_behavior;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;

import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLClassifierComboBoxModel;
import org.argouml.uml.ui.UMLComboBox;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLList;
import org.argouml.uml.ui.UMLStimulusListModel;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 */
public class PropPanelObject extends PropPanelModelElement {

    public PropPanelObject() {
	super("Object", _objectIcon, ConfigLoader.getTabPropsOrientation());

	Class mclass = (Class) ModelFacade.OBJECT;

	addField(Translator.localize("UMLMenu", "label.name"), getNameTextField());

	UMLClassifierComboBoxModel classifierModel = new UMLClassifierComboBoxModel(this, "isAcceptibleClassifier", "classifier", "getClassifier", "setClassifier", true, (Class)ModelFacade.CLASSIFIER, true);
	UMLComboBox clsComboBox = new UMLComboBox(classifierModel);
	addField("Classifier:", new UMLComboBoxNavigator(this, Translator.localize("UMLMenu", "tooltip.nav-class"), clsComboBox));

	addField(Translator.localize("UMLMenu", "label.stereotype"), new UMLComboBoxNavigator(this, Translator.localize("UMLMenu", "tooltip.nav-stereo"), getStereotypeBox()));

	addLinkField(Translator.localize("UMLMenu", "label.namespace"), getNamespaceComboBox());

        addSeperator();

	JList sentList = new UMLList(new UMLStimulusListModel(this, null, true, "sent"), true);
	sentList.setForeground(Color.blue);
	JScrollPane sentScroll = new JScrollPane(sentList);
	addField("Stimuli sent:", sentScroll);

	JList receivedList = new UMLList(new UMLStimulusListModel(this, null, true, "received"), true);
	receivedList.setForeground(Color.blue);
	JScrollPane receivedScroll = new JScrollPane(receivedList);
	addField("Stimuli received:", receivedScroll);

	new PropPanelButton(this, buttonPanel, _navUpIcon, Translator.localize("UMLMenu", "button.go-up"), "navigateNamespace", null);
	new PropPanelButton(this, buttonPanel, _deleteIcon, localize("Delete object"), "removeElement", null);
    }


    public void navigateNamespace() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAModelElement(target)) {
            Object elem = /*(MModelElement)*/ target;
            Object ns = ModelFacade.getNamespace(elem);
            if (ns != null) {
                TargetManager.getInstance().setTarget(ns);
            }
        }
    }



    public boolean isAcceptibleClassifier(Object/*MModelElement*/ classifier) {
        return org.argouml.model.ModelFacade.isAClassifier(classifier);
    }

    public Object getClassifier() {
        Object classifier = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAInstance(target)) {
	    //    UML 1.3 apparently has this a 0..n multiplicity
	    //    I'll have to figure out what that means
	    //            classifier = ((MInstance) target).getClassifier();

	    // at the moment , we only deal with one classifier
	    Collection col = ModelFacade.getClassifiers(target);
            Iterator iter = col.iterator();
            if (iter.hasNext()) {
                classifier = /*(MClassifier)*/ iter.next();
            }
        }
        return classifier;
    }

    public void setClassifier(Object/*MClassifier*/ element) {
        Object target = getTarget();

        if (org.argouml.model.ModelFacade.isAInstance(target)) {
	    Object inst = /*(MInstance)*/ target;
	    Vector classifiers = new Vector();
	    if (element != null) {
	    	classifiers.add(element);
	    }
        
            boolean changed = false;
            if (ModelFacade.getClassifiers(inst) == null 
                    || classifiers.size() != ModelFacade.getClassifiers(inst).size()) {
                changed = true;
            }
            else {
                Iterator iter1 = classifiers.iterator();
                Iterator iter2 = ModelFacade.getClassifiers(inst).iterator();
                while (!changed && iter1.hasNext()) {
                    if (!(iter1.next().equals(iter2.next()))) {
                        changed = true;
                    }
                }
            }

            if (changed) {
                ModelFacade.setClassifiers(inst, classifiers);
            }
        }
	/*
	//            ((MInstance) target).setClassifier((MClassifier) element);

	// delete all classifiers
	Collection col = inst.getClassifiers();
	if (col != null) {
	Iterator iter = col.iterator();
	if (iter != null && iter.hasNext()) {
	MClassifier classifier = (MClassifier)iter.next();
	inst.removeClassifier(classifier);
	}
	}

	Iterator it = inst.getClassifiers().iterator();
	while (it.hasNext()) {
	inst.removeClassifier((MClassifier)it.next());
	}
	// add classifier
	if (element != null) {
	inst.addClassifier( element);
	}

        }
        */
    }


    public void removeElement() {

        Object target = /*(MObject)*/ getTarget();
	Object newTarget = /*(MModelElement)*/ ModelFacade.getNamespace(target);

        UmlFactory.getFactory().delete(target);
	if (newTarget != null) TargetManager.getInstance().setTarget(newTarget);
    }
}