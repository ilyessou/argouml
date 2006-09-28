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



// File: PropPanelMessage.java
// Classes: PropPanelMessage
// Original Author: agauthie@ics.uci.edu
// $Id$

package org.argouml.uml.ui.behavior.collaborations;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.behavioralelements.commonbehavior.CommonBehaviorFactory;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components, specifically namesToWatch mechanism.
 */
public class PropPanelMessage extends PropPanelModelElement {

    ////////////////////////////////////////////////////////////////
    // constants


    ////////////////////////////////////////////////////////////////
    // contructors
    public PropPanelMessage() {
	super("Message", ConfigLoader.getTabPropsOrientation());

	Class mclass = (Class)ModelFacade.MESSAGE;

	Class[] namesToWatch = {
	    (Class)ModelFacade.STEREOTYPE,
	    (Class)ModelFacade.CLASSIFIER_ROLE,
	    (Class) ModelFacade.ACTION 
	};
	setNameEventListening(namesToWatch);

	addField(Translator.localize("UMLMenu", "label.name"), getNameTextField());
	addField(Translator.localize("UMLMenu", "label.stereotype"),
		 getStereotypeBox());
	// a message does not have a namespace. removed therefore
	// addField(Argo.localize("UMLMenu", "label.namespace"),
	// getNamespaceScroll());
	JList interactionList =
	    new UMLLinkedList(new UMLMessageInteractionListModel());
	interactionList.setVisibleRowCount(1);
	addField(Translator.localize("UMLMenu", "label.interaction"),
		 new JScrollPane(interactionList));

	JList senderList = new UMLLinkedList(new UMLMessageSenderListModel());
	senderList.setVisibleRowCount(1);
	JScrollPane senderScroll = new JScrollPane(senderList);
	addField(Translator.localize("UMLMenu", "label.sender"), senderScroll);

	JList receiverList =
	    new UMLLinkedList(new UMLMessageReceiverListModel());
	receiverList.setVisibleRowCount(1);
	JScrollPane receiverScroll = new JScrollPane(receiverList);
	addField(Translator.localize("UMLMenu", "label.receiver"), receiverScroll);

	addSeperator();

	addField(Translator.localize("UMLMenu", "label.activator"),
		 new UMLMessageActivatorComboBox(this,
			 new UMLMessageActivatorComboBoxModel()));

	JList actionList =
		 new UMLMutableLinkedList(new UMLMessageActionListModel(),
					  null,
					  ActionNewAction.SINGLETON);
	actionList.setVisibleRowCount(1);
	JScrollPane actionScroll = new JScrollPane(actionList);
	addField(Translator.localize("UMLMenu", "label.action"), actionScroll);

	JScrollPane predecessorScroll =
		 new JScrollPane(new UMLMutableLinkedList(new UMLMessagePredecessorListModel(),
							  ActionAddMessagePredecessor.SINGLETON,
							  null));
	addField(Translator.localize("UMLMenu", "label.predecessor"),
		 predecessorScroll);

	new PropPanelButton(this, buttonPanel, _navUpIcon,
			    Translator.localize("UMLMenu", "button.go-up"),
			    "navigateInteraction",
			    null);
	new PropPanelButton(this, buttonPanel, _actionIcon,
			    Translator.localize("UMLMenu", "button.add-action"),
			    "addAction",
			    "isAddActionEnabled");
	// ActionNewAction.SINGLETON.setTarget((MModelElement)getTarget());
	// buttonPanel.add(new PropPanelButton2(this,
	// ActionNewAction.SINGLETON));
	new PropPanelButton(this, buttonPanel, _deleteIcon, localize("Delete"), "removeElement", null);
    }



    public Object addAction() {
    	Object action = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAMessage(target)) {
            action =
		/*(MCallAction)*/ CommonBehaviorFactory.getFactory().buildAction(/*(MMessage)*/ target);
        }
        return action;
    }

    public boolean isAddActionEnabled() {
    	return (org.argouml.model.ModelFacade.isAMessage(getTarget())) && (ModelFacade.getAction(getTarget()) == null);
    }

    public void navigateInteraction() {
    	Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAMessage(target)) {
            TargetManager.getInstance().setTarget(ModelFacade.getInteraction(target));
        }
    }



} /* end class PropPanelMessage */