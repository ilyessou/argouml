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

// File: PropPanelStimulus.java
// Classes: PropPanelStimulus
// Original Author: agauthie@ics.uci.edu
// $Id$

package org.argouml.uml.ui.behavior.common_behavior;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.argouml.application.api.Argo;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLList;
import org.argouml.uml.ui.UMLReflectionListModel;
import org.argouml.uml.ui.UMLStimulusActionTextField;
import org.argouml.uml.ui.UMLStimulusActionTextProperty;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

import ru.novosoft.uml.MElementEvent;

/**
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 */
public class PropPanelStimulus extends PropPanelModelElement {

    protected static ImageIcon _stimulusIcon = ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIconResource("Stimulus");

    public PropPanelStimulus() {
        super("Stimulus Properties", _stimulusIcon, ConfigLoader.getTabPropsOrientation());

        Class[] namesToWatch = {
	    (Class) ModelFacade.ACTION
	};
        setNameEventListening(namesToWatch);

        Class mclass = (Class)ModelFacade.STIMULUS;

        addField(Argo.localize("UMLMenu", "label.name"), getNameTextField());
        addField("Action:", new UMLStimulusActionTextField(this, new UMLStimulusActionTextProperty("name")));
        addField(Argo.localize("UMLMenu", "label.stereotype"), getStereotypeBox());

        UMLList senderList = new UMLList(new UMLReflectionListModel(this, "sender", true, "getSender", null, null, null), true);
        senderList.setForeground(Color.blue);
        senderList.setVisibleRowCount(1);
        senderList.setFont(smallFont);
        JScrollPane senderScroll = new JScrollPane(senderList);
        addField("Sender:", senderScroll);

        UMLList receiverList = new UMLList(new UMLReflectionListModel(this, "receiver", true, "getReceiver", null, null, null), true);
        receiverList.setForeground(Color.blue);
        receiverList.setVisibleRowCount(1);
        receiverList.setFont(smallFont);
        JScrollPane receiverScroll = new JScrollPane(receiverList);
        addField(Argo.localize("UMLMenu", "label.receiver"), receiverScroll);

        addLinkField(Argo.localize("UMLMenu", "label.namespace"), getNamespaceComboBox());

        new PropPanelButton(this, buttonPanel, _navUpIcon, Argo.localize("UMLMenu", "button.go-up"), "navigateNamespace", null);
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

    public void removed(MElementEvent mee) {
    }

    public Object getSender() {
        Object sender = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            sender =  ModelFacade.getSender(target);
        }
        return sender;
    }

    public void setSender(Object/*MInstance*/ element) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAStimulus(target)) {
            ModelFacade.setSender(target, element);
        }
    }


    public Object getReceiver() {
        Object receiver = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            receiver =  ModelFacade.getReceiver(target);
        }
        return receiver;
    }

    public void setReceiver(Object/*MInstance*/ element) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAStimulus(target)) {
            ModelFacade.setReceiver(target, element);
        }
    }

    public boolean isAcceptibleAssociation(Object/*MModelElement*/ classifier) {
        return org.argouml.model.ModelFacade.isAAssociation(classifier);
    }

    public Object getAssociation() {
        Object association = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            Object link = ModelFacade.getCommunicationLink(target);
            if (link != null) {
                association = ModelFacade.getAssociation(link);
            }
        }
        return association;
    }

    public void setAssociation(Object/*MAssociation*/ element) {
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            Object stimulus = /*(MStimulus)*/ target;
            Object link = ModelFacade.getCommunicationLink(stimulus);
            if (link == null) {
                link = UmlFactory.getFactory().getCommonBehavior().createLink();//((MStimulus)stimulus).getFactory().createLink();
                if (link != null) {
                    ModelFacade.addStimulus(link, stimulus);
                    ModelFacade.setCommunicationLink(stimulus, /*(MLink)*/link);
                }
            }
            Object oldAssoc = ModelFacade.getAssociation(link);
            if (oldAssoc != element) {
                ModelFacade.setAssociation(link, element);
                //
                //  TODO: more needs to go here
                //
            }
        }
    }

    public void removeElement() {
        Object target = /*(MStimulus)*/ getTarget();
	Object newTarget = /*(MModelElement)*/ ModelFacade.getNamespace(target);

	UmlFactory.getFactory().delete(target);
	if (newTarget != null) {
            TargetManager.getInstance().setTarget(newTarget);
	}

    }

}