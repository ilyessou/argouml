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

package org.argouml.model.mdr;

import java.util.Collection;
import java.util.Iterator;

import org.argouml.model.CommonBehaviorFactory;
import org.argouml.model.Model;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet;
import org.omg.uml.behavioralelements.collaborations.InteractionInstanceSet;
import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.ActionSequence;
import org.omg.uml.behavioralelements.commonbehavior.Argument;
import org.omg.uml.behavioralelements.commonbehavior.AttributeLink;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.CreateAction;
import org.omg.uml.behavioralelements.commonbehavior.DataValue;
import org.omg.uml.behavioralelements.commonbehavior.DestroyAction;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.LinkObject;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.behavioralelements.commonbehavior.Reception;
import org.omg.uml.behavioralelements.commonbehavior.ReturnAction;
import org.omg.uml.behavioralelements.commonbehavior.SendAction;
import org.omg.uml.behavioralelements.commonbehavior.Signal;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.SubsystemInstance;
import org.omg.uml.behavioralelements.commonbehavior.TerminateAction;
import org.omg.uml.behavioralelements.commonbehavior.UmlException;
import org.omg.uml.behavioralelements.commonbehavior.UninterpretedAction;
import org.omg.uml.behavioralelements.statemachines.SignalEvent;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Operation;

/**
 * Factory to create UML classes for the UML BehaviorialElements::CommonBehavior
 * package.<p>
 * 
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * @author Tom Morris
 * Derived from NSUML implementation by: 
 * @author Thierry Lach
 */
class CommonBehaviorFactoryMDRImpl extends AbstractUmlModelFactoryMDR
	implements CommonBehaviorFactory {

    /**
     * The model implementation.
     */
    private MDRModelImplementation modelImpl;

    /**
     * Don't allow instantiation.
     *
     * @param implementation
     *            To get other helpers and factories.
     */
    CommonBehaviorFactoryMDRImpl(MDRModelImplementation implementation) {
        modelImpl = implementation;
    }


    public ActionSequence createActionSequence() {
        ActionSequence myActionSequence = getCbPackage().getActionSequence()
                .createActionSequence();
        super.initialize(myActionSequence);
        return myActionSequence;
    }


    public Argument createArgument() {
        Argument myArgument = getCbPackage()
                .getArgument().createArgument();
        super.initialize(myArgument);
        return myArgument;
    }


    public AttributeLink createAttributeLink() {
        AttributeLink myAttributeLink = getCbPackage().getAttributeLink()
                .createAttributeLink();
        super.initialize(myAttributeLink);
        return myAttributeLink;
    }

    private CommonBehaviorPackage getCbPackage() {
        return modelImpl.getUmlPackage().getCommonBehavior();
    }

    public CallAction createCallAction() {
        CallAction myCallAction = getCbPackage().getCallAction().createCallAction();
        super.initialize(myCallAction);
        return myCallAction;
    }


    public ComponentInstance createComponentInstance() {
        ComponentInstance myComponentInstance = getCbPackage()
                .getComponentInstance().createComponentInstance();
        super.initialize(myComponentInstance);
        return myComponentInstance;
    }


    public CreateAction createCreateAction() {
        CreateAction myCreateAction = getCbPackage().getCreateAction()
                .createCreateAction();
        super.initialize(myCreateAction);
        return myCreateAction;
    }


    public DataValue createDataValue() {
        DataValue myDataValue = getCbPackage().getDataValue().createDataValue();
        super.initialize(myDataValue);
        return myDataValue;
    }


    public DestroyAction createDestroyAction() {
        DestroyAction myDestroyAction = getCbPackage().getDestroyAction()
                .createDestroyAction();
        super.initialize(myDestroyAction);
        return myDestroyAction;
    }


    public UmlException createException() {
        UmlException myUmlException = getCbPackage().getUmlException()
                .createUmlException();
        super.initialize(myUmlException);
        return myUmlException;
    }


    public Link createLink() {
        Link myLink = getCbPackage().getLink().createLink();
        super.initialize(myLink);
        return myLink;
    }


    public LinkEnd createLinkEnd() {
        LinkEnd myLinkEnd = getCbPackage().getLinkEnd().createLinkEnd();
        super.initialize(myLinkEnd);
        return myLinkEnd;
    }


    public LinkObject createLinkObject() {
        LinkObject myLinkObject = getCbPackage().getLinkObject().createLinkObject();
        super.initialize(myLinkObject);
        return myLinkObject;
    }


    public NodeInstance createNodeInstance() {
        NodeInstance myNodeInstance = getCbPackage().getNodeInstance()
                .createNodeInstance();
        super.initialize(myNodeInstance);
        return myNodeInstance;
    }


    public org.omg.uml.behavioralelements.commonbehavior.Object createObject() {
        org.omg.uml.behavioralelements.commonbehavior.Object myObject = 
                getCbPackage().getObject().createObject();
        super.initialize(myObject);
        return myObject;
    }


    public Reception createReception() {
        Reception myReception = getCbPackage().getReception().createReception();
        super.initialize(myReception);
        return myReception;
    }


    public ReturnAction createReturnAction() {
        ReturnAction myReturnAction = getCbPackage().getReturnAction()
                .createReturnAction();
        super.initialize(myReturnAction);
        return myReturnAction;
    }


    public SendAction createSendAction() {
        SendAction mySendAction = getCbPackage().getSendAction().createSendAction();
        super.initialize(mySendAction);
        return mySendAction;
    }


    public Signal createSignal() {
        Signal mySignal = getCbPackage().getSignal().createSignal();
        super.initialize(mySignal);
        return mySignal;
    }


    public Stimulus createStimulus() {
        Stimulus myStimulus = getCbPackage().getStimulus().createStimulus();
        super.initialize(myStimulus);
        return myStimulus;
    }


    public SubsystemInstance createSubsystemInstance() {
        SubsystemInstance obj = getCbPackage().getSubsystemInstance()
                .createSubsystemInstance();
        super.initialize(obj);
        return obj;
    }
    

    public TerminateAction createTerminateAction() {
        TerminateAction myTerminateAction = getCbPackage().getTerminateAction()
                .createTerminateAction();
        super.initialize(myTerminateAction);
        return myTerminateAction;
    }


    public UninterpretedAction createUninterpretedAction() {
        UninterpretedAction myUninterpretedAction = getCbPackage()
                .getUninterpretedAction().createUninterpretedAction();
        super.initialize(myUninterpretedAction);
        return myUninterpretedAction;
    }


    public CallAction buildCallAction(Object oper, String name) {
        if (!(oper instanceof Operation)) {
            throw new IllegalArgumentException("There should be an operation"
                    + " with a callaction.");
        }
        CallAction action = createCallAction();
        action.setName(name);
        action.setOperation((Operation) oper);
        return action;
    }


    public UninterpretedAction buildUninterpretedAction(Object actionState) {
        UninterpretedAction action = createUninterpretedAction();
        if (actionState instanceof ActionState) {
            ((ActionState) actionState).setEntry(action);
        }
        return action;
    }


    public Link buildLink(Object fromInstance, Object toInstance) {
        Link link = createLink();
        LinkEnd le0 =  createLinkEnd();
        le0.setInstance((Instance) fromInstance);
        LinkEnd le1 = createLinkEnd();
        le1.setInstance((Instance) toInstance);
        link.getConnection().add(le0);
        link.getConnection().add(le1);
        return link;
    }


    public Action buildAction(Object message) {
        Action action = createCallAction();
        action.setName("action");
        ((Message) message).setAction(action);
        Object interaction = modelImpl.getFacade().getInteraction(message);
        if (interaction != null
            && modelImpl.getFacade().getContext(interaction) != null) {
            modelImpl.getCoreHelper().setNamespace(action,
                modelImpl.getFacade().getContext(interaction));
        } else {
            throw new IllegalStateException("In buildaction: message does not "
                    + "have an interaction or the "
                    + "interaction does not have " + "a context");
        }
        return action;
    }


    public Signal buildSignal(Object element) {
        Signal signal;
        
        if ((element instanceof BehavioralFeature)) {
            signal = buildSignalInt(element);
            BehavioralFeature bf = (BehavioralFeature) element;
            ((UmlPackage) bf.refOutermostPackage()).getCommonBehavior()
                    .getAContextRaisedSignal().add((BehavioralFeature) element,
                            signal);
        } else if (element instanceof Reception) {
            signal = buildSignalInt(element);
            ((Reception) element).setSignal(signal);
        } else if (element instanceof SendAction) {
            signal = buildSignalInt(element);
            ((SendAction) element).setSignal(signal);
        } else if (element instanceof SignalEvent) {
            signal = buildSignalInt(element);
            ((SignalEvent) element).setSignal(signal);
        } else {
            throw new IllegalArgumentException("Can't build a signal for a " + element);
        }
        
        Object namespace = element;
        while (!Model.getFacade().isAPackage(namespace)) {
            namespace = Model.getFacade().getModelElementContainer(namespace);
        }
        
        Model.getCoreHelper().setNamespace(signal, namespace);
        return signal;
    }

    /**
     * Create a new Signal in the same namespace as the target.
     * @param element target element
     * @return newly created Signal
     */
    private Signal buildSignalInt(Object element) {
        Signal signal = createSignal();
        Namespace ns = ((ModelElement) element).getNamespace();
        signal.setNamespace(ns);
        return signal;
    }
    

    public Stimulus buildStimulus(Object link) {
        if (link instanceof Link
            && modelImpl.getCoreHelper().getSource(link) != null
            && modelImpl.getCoreHelper().getDestination(link) != null) {

            Stimulus stimulus = createStimulus();
            Object sender = modelImpl.getCoreHelper().getSource(link);
            Object receiver = modelImpl.getCoreHelper().getDestination(link);
            modelImpl.getCommonBehaviorHelper().setReceiver(stimulus, receiver);
            modelImpl.getCollaborationsHelper().setSender(stimulus, sender);
            modelImpl.getCommonBehaviorHelper().setCommunicationLink(stimulus,
                    link);
            return stimulus;
        }
        throw new IllegalArgumentException("Argument is not a link or "
                + "does not have " + "a source or " + "destination instance");

    }


    public Reception buildReception(Object aClassifier) {
        Reception reception = createReception();
        if (aClassifier instanceof Classifier) {
            modelImpl.getCoreHelper().setOwner(reception, aClassifier);
        }
        return reception;
    }

    /**
     * @param elem
     *            the Action to be deleted
     */
    void deleteAction(Object elem) {
        if (!(elem instanceof Action)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        // Delete Stimulii which have this as their dispatchAction
        Action action = (Action) elem;
        modelImpl.getUmlHelper().deleteCollection(
                ((org.omg.uml.UmlPackage) action.refOutermostPackage())
                        .getCommonBehavior().getADispatchActionStimulus()
                        .getStimulus((Action) elem));
        // Delete Messages which have this as their action
        modelImpl.getUmlHelper().deleteCollection(
                ((org.omg.uml.UmlPackage) action.refOutermostPackage())
                        .getCollaborations().getAActionMessage().getMessage(
                                (Action) elem));
    }

    /**
     * @param elem
     *            the ActionSequence to be deleted
     */
    void deleteActionSequence(Object elem) {
        if (!(elem instanceof ActionSequence)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteArgument(Object elem) {
        if (!(elem instanceof Argument)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteAttributeLink(Object elem) {
        if (!(elem instanceof AttributeLink)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteCallAction(Object elem) {
        if (!(elem instanceof CallAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteComponentInstance(Object elem) {
        if (!(elem instanceof ComponentInstance)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteCreateAction(Object elem) {
        if (!(elem instanceof CreateAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteDataValue(Object elem) {
        if (!(elem instanceof DataValue)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteDestroyAction(Object elem) {
        if (!(elem instanceof DestroyAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteException(Object elem) {
        if (!(elem instanceof UmlException)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * When an instance is deleted, delete any LinkEnd, AttributLink
     * that depends on it, as well as CollaborationInstanceSets where
     * this is the last participatingInstance
     *
     * @param elem
     *            the element to be deleted
     */
    void deleteInstance(Object elem) {
        if (!(elem instanceof Instance)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        Instance instance = (Instance) elem;

        // Delete LinkEnds
        modelImpl.getUmlHelper().deleteCollection(instance.getLinkEnd());

        // Delete AttributeLinks where this is the value
        modelImpl.getUmlHelper().deleteCollection(
                ((org.omg.uml.UmlPackage) instance
                        .refOutermostPackage()).getCommonBehavior()
                        .getAAttributeLinkValue().getAttributeLink(
                                instance));

        // Delete CollaborationInstanceSets where
        // this is the last participatingInstance
        for (Iterator it = ((org.omg.uml.UmlPackage) instance
                .refOutermostPackage()).getCollaborations()
                .getACollaborationInstanceSetParticipatingInstance()
                .getCollaborationInstanceSet(instance).iterator(); it
                .hasNext();) {
            CollaborationInstanceSet cis = (CollaborationInstanceSet) it.next();
            Collection instances = cis.getParticipatingInstance();
            if (instances.size() == 1 && instances.contains(elem)) {
                modelImpl.getUmlFactory().delete(it.next());
            }
        }

        // Delete Stimuli which have this as a Sender or Receiver
        modelImpl.getUmlHelper().deleteCollection(
                ((UmlPackage) ((Instance) elem).refOutermostPackage())
                        .getCommonBehavior().getAStimulusSender().getStimulus(
                                (Instance) elem));
        modelImpl.getUmlHelper().deleteCollection(
                ((UmlPackage) ((Instance) elem).refOutermostPackage())
                        .getCommonBehavior().getAReceiverStimulus()
                        .getStimulus((Instance) elem));

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteLink(Object elem) {
        if (!(elem instanceof Link)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * when a linkend is deleted, delete its Links.
     *
     * @param elem
     *            the element to be deleted
     */
    void deleteLinkEnd(Object elem) {
        if (!(elem instanceof LinkEnd)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

        Link link = ((LinkEnd) elem).getLink();
        if (link != null && link.getConnection() != null
            && link.getConnection().size() == 2) { // binary link
            modelImpl.getUmlFactory().delete(link);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteLinkObject(Object elem) {
        if (!(elem instanceof LinkObject)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteNodeInstance(Object elem) {
        if (!(elem instanceof NodeInstance)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteObject(Object elem) {
        if (!(Model.getFacade().isAObject(elem))) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteReception(Object elem) {
        if (!(elem instanceof Reception)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteReturnAction(Object elem) {
        if (!(elem instanceof ReturnAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteSendAction(Object elem) {
        if (!(elem instanceof SendAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteSignal(Object elem) {
        if (!(elem instanceof Signal)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        Signal signal = (Signal) elem;
        // Delete all SendActions which have this as signal
        modelImpl.getUmlHelper().deleteCollection(
                ((org.omg.uml.UmlPackage) signal.refOutermostPackage())
                        .getCommonBehavior().getASignalSendAction()
                        .getSendAction(signal));
        // Delete all SignalEvents which have this as the signal
        modelImpl.getUmlHelper().deleteCollection(
                ((org.omg.uml.UmlPackage) signal.refOutermostPackage())
                        .getStateMachines().getASignalOccurrence()
                        .getOccurrence((Signal) elem));
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteStimulus(Object elem) {
        if (!(elem instanceof Stimulus)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        Stimulus stimulus = (Stimulus) elem;
        
        // Delete InteractionInstanceSets where
        // this is the last participatingStimulus
        for (Iterator it = ((org.omg.uml.UmlPackage) stimulus
                .refOutermostPackage()).getCollaborations()
                .getAInteractionInstanceSetParticipatingStimulus()
                .getInteractionInstanceSet(stimulus).iterator(); it.hasNext();) {
            InteractionInstanceSet iis = (InteractionInstanceSet) it.next();
            Collection instances = iis.getParticipatingStimulus();
            if (instances.size() == 1 && instances.contains(elem)) {
                modelImpl.getUmlFactory().delete(it.next());
            }
        }
    }
    
    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteSubsystemInstance(Object elem) {
        if (!(elem instanceof SubsystemInstance)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }


    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteTerminateAction(Object elem) {
        if (!(elem instanceof TerminateAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteUninterpretedAction(Object elem) {
        if (!(elem instanceof UninterpretedAction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }
    
}
