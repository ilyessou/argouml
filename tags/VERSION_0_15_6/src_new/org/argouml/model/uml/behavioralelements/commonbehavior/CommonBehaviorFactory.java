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

package org.argouml.model.uml.behavioralelements.commonbehavior;

import java.util.Collection;
import java.util.Iterator;

import org.argouml.model.ModelFacade;
import org.argouml.model.uml.AbstractUmlModelFactory;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlHelper;

import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.common_behavior.MArgument;
import ru.novosoft.uml.behavior.common_behavior.MAttributeLink;
import ru.novosoft.uml.behavior.common_behavior.MCallAction;
import ru.novosoft.uml.behavior.common_behavior.MComponentInstance;
import ru.novosoft.uml.behavior.common_behavior.MCreateAction;
import ru.novosoft.uml.behavior.common_behavior.MDataValue;
import ru.novosoft.uml.behavior.common_behavior.MDestroyAction;
import ru.novosoft.uml.behavior.common_behavior.MException;
import ru.novosoft.uml.behavior.common_behavior.MInstance;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.behavior.common_behavior.MLinkEnd;
import ru.novosoft.uml.behavior.common_behavior.MLinkObject;
import ru.novosoft.uml.behavior.common_behavior.MNodeInstance;
import ru.novosoft.uml.behavior.common_behavior.MObject;
import ru.novosoft.uml.behavior.common_behavior.MReception;
import ru.novosoft.uml.behavior.common_behavior.MReturnAction;
import ru.novosoft.uml.behavior.common_behavior.MSendAction;
import ru.novosoft.uml.behavior.common_behavior.MSignal;
import ru.novosoft.uml.behavior.common_behavior.MStimulus;
import ru.novosoft.uml.behavior.common_behavior.MTerminateAction;
import ru.novosoft.uml.behavior.common_behavior.MUninterpretedAction;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MOperation;

/**
 * Factory to create UML classes for the UML
 * BehaviorialElements::CommonBehavior package.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @stereotype singleton
 */
public class CommonBehaviorFactory extends AbstractUmlModelFactory {

    /** Singleton instance.
     */
    private static CommonBehaviorFactory SINGLETON =
        new CommonBehaviorFactory();

    /** Singleton instance access method.
     */
    public static CommonBehaviorFactory getFactory() {
        return SINGLETON;
    }

    /** Don't allow instantiation
     */
    private CommonBehaviorFactory() {}

    /** Create an empty but initialized instance of a UML Action.
     *  
     *  @return an initialized UML Action instance.
     */
    public Object createAction() {
        Object modelElement = MFactory.getDefaultFactory().createAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML ActionSequence.
     *  
     *  @return an initialized UML ActionSequence instance.
     */
    public Object createActionSequence() {
        Object modelElement =
            MFactory.getDefaultFactory().createActionSequence();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Argument.
     *  
     *  @return an initialized UML Argument instance.
     */
    public MArgument createArgument() {
        MArgument modelElement = MFactory.getDefaultFactory().createArgument();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML AttributeLink.
     *  
     *  @return an initialized UML AttributeLink instance.
     */
    public MAttributeLink createAttributeLink() {
        MAttributeLink modelElement =
            MFactory.getDefaultFactory().createAttributeLink();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML CallAction.
     *  
     *  @return an initialized UML CallAction instance.
     */
    public Object createCallAction() {
        MCallAction modelElement =
            MFactory.getDefaultFactory().createCallAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML ComponentInstance.
     *  
     *  @return an initialized UML ComponentInstance instance.
     */
    public MComponentInstance createComponentInstance() {
        MComponentInstance modelElement =
            MFactory.getDefaultFactory().createComponentInstance();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML CreateAction.
     *  
     *  @return an initialized UML CreateAction instance.
     */
    public MCreateAction createCreateAction() {
        MCreateAction modelElement =
            MFactory.getDefaultFactory().createCreateAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML DataValue.
     *  
     *  @return an initialized UML DataValue instance.
     */
    public MDataValue createDataValue() {
        MDataValue modelElement =
            MFactory.getDefaultFactory().createDataValue();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML DestroyAction.
     *  
     *  @return an initialized UML DestroyAction instance.
     */
    public MDestroyAction createDestroyAction() {
        MDestroyAction modelElement =
            MFactory.getDefaultFactory().createDestroyAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Exception.
     *  
     *  @return an initialized UML Exception instance.
     */
    public MException createException() {
        MException modelElement =
            MFactory.getDefaultFactory().createException();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Instance.
     *  
     *  @return an initialized UML Instance instance.
     */
    public MInstance createInstance() {
        MInstance modelElement = MFactory.getDefaultFactory().createInstance();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Link.
     *  
     *  @return an initialized UML Link instance.
     */
    public MLink createLink() {
        MLink modelElement = MFactory.getDefaultFactory().createLink();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML LinkEnd.
     *  
     *  @return an initialized UML LinkEnd instance.
     */
    public Object createLinkEnd() {
        MLinkEnd modelElement = MFactory.getDefaultFactory().createLinkEnd();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML LinkObject.
     *  
     *  @return an initialized UML LinkObject instance.
     */
    public MLinkObject createLinkObject() {
        MLinkObject modelElement =
            MFactory.getDefaultFactory().createLinkObject();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML NodeInstance.
     *  
     *  @return an initialized UML NodeInstance instance.
     */
    public MNodeInstance createNodeInstance() {
        MNodeInstance modelElement =
            MFactory.getDefaultFactory().createNodeInstance();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Object.
     *  
     *  @return an initialized UML Object instance.
     */
    public MObject createObject() {
        MObject modelElement = MFactory.getDefaultFactory().createObject();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Reception.
     *  
     *  @return an initialized UML Reception instance.
     */
    public MReception createReception() {
        MReception modelElement =
            MFactory.getDefaultFactory().createReception();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML ReturnAction.
     *  
     *  @return an initialized UML ReturnAction instance.
     */
    public MReturnAction createReturnAction() {
        MReturnAction modelElement =
            MFactory.getDefaultFactory().createReturnAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML SendAction.
     *  
     *  @return an initialized UML SendAction instance.
     */
    public MSendAction createSendAction() {
        MSendAction modelElement =
            MFactory.getDefaultFactory().createSendAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Signal.
     *  
     *  @return an initialized UML Signal instance.
     */
    public MSignal createSignal() {
        MSignal modelElement = MFactory.getDefaultFactory().createSignal();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML Stimulus.
     *  
     *  @return an initialized UML Stimulus instance.
     */
    public MStimulus createStimulus() {
        MStimulus modelElement = MFactory.getDefaultFactory().createStimulus();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML TerminateAction.
     *  
     *  @return an initialized UML TerminateAction instance.
     */
    public MTerminateAction createTerminateAction() {
        MTerminateAction modelElement =
            MFactory.getDefaultFactory().createTerminateAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /** Create an empty but initialized instance of a UML UninterpretedAction.
     *  
     *  @return an initialized UML UninterpretedAction instance.
     */
    public MUninterpretedAction createUninterpretedAction() {
        MUninterpretedAction modelElement =
            MFactory.getDefaultFactory().createUninterpretedAction();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Builds a Callaction belonging to operation oper with a given name. 
     * Ownership of this modelelement is not set! It is unwise to build a 
     * callaction without an operation since the multiplicity according to the
     * UML spec 1.3 is 1..1. Therefore precondition is that there is an 
     * operation.
     * @param oper
     * @param name
     * @return MCallAction
     */
    public Object /*MCallAction*/
    buildCallAction(MOperation oper, String name) {
        if (oper == null) {
            throw new IllegalArgumentException(
                "There should be an operation" + " with a callaction.");
        }
        Object action = createCallAction();
        ModelFacade.setName(action, name);
        ModelFacade.setOperation(action, oper);
        return action;
    }

    /** 
     * Builds a Link between two Instances
     */
    public Object buildLink(Object fromInstance, Object toInstance) {
        Object link = UmlFactory.getFactory().getCommonBehavior().createLink();
        Object /*MLinkEnd*/
        le0 = UmlFactory.getFactory().getCommonBehavior().createLinkEnd();
        ModelFacade.setInstance(le0, fromInstance);
        Object /*MLinkEnd*/
        le1 = UmlFactory.getFactory().getCommonBehavior().createLinkEnd();
        ModelFacade.setInstance(le1, toInstance);
        ModelFacade.addConnection(link, le0);
        ModelFacade.addConnection(link, le1);
        return link;
    }

    /**
     * Builds an action (actually an uninterpretedaction) for some message
     */
    public Object buildAction(Object message) {
        Object action = createCallAction();
        ModelFacade.setName(action, "action");
        ModelFacade.setAction(message, action);
        Object interaction = ModelFacade.getInteraction(message);
        if (interaction != null
            && ModelFacade.getContext(interaction) != null) {
            ModelFacade.setNamespace(
                action,
                ModelFacade.getContext(interaction));
        } else
            throw new IllegalStateException(
                "In buildaction: message does not "
                    + "have an interaction or the "
                    + "interaction does not have "
                    + "a context");
        return action;
    }

    /**
     * Builds a signal belonging to some behavioralfeature
     */
    public MSignal buildSignal(MBehavioralFeature feature) {
        if (feature == null)
            return null;
        MSignal signal = createSignal();
        signal.addContext(feature);
        return signal;
    }

    /**
     * Builds a stimulus based on a given link. The link must have two linkends that 
     * are connected to an instance. These instances are used as sender and receiver
     * of the stimulus. The source will become the sender, the destination the 
     * receiver.
     * @param link the link
     * @return the stimulus
     */
    public Object buildStimulus(Object link) {
        if (ModelFacade.isALink(link)
            && UmlHelper.getHelper().getCore().getSource(link) != null
            && UmlHelper.getHelper().getCore().getDestination(link) != null) {
            Object stimulus = createStimulus();
            Object sender = UmlHelper.getHelper().getCore().getSource(link);
            Object receiver =
                UmlHelper.getHelper().getCore().getDestination(link);
            ModelFacade.setReceiver(stimulus, receiver);
            ModelFacade.setSender(stimulus, sender);
            ModelFacade.setCommunicationLink(stimulus, link);
            return stimulus;
        }
        throw new IllegalArgumentException("Argument is not a link or does not has a source or destination instance");

    }

    /**
     * Builds a reception belonging to some signal
     */
    public MReception buildReception(Object asignal) {
        MSignal signal = (MSignal) asignal;
        if (signal == null)
            return null;
        MReception reception = createReception();
        reception.setSignal(signal);
        if (signal.getNamespace() != null) {
            reception.setNamespace(signal.getNamespace());
        }
        return reception;
    }

    public void deleteAction(Object elem) {}

    public void deleteActionSequence(Object elem) {}

    public void deleteArgument(MArgument elem) {}

    public void deleteAttributeLink(MAttributeLink elem) {}

    public void deleteCallAction(MCallAction elem) {}

    public void deleteComponentInstance(MComponentInstance elem) {}

    public void deleteCreateAction(MCreateAction elem) {}

    public void deleteDataValue(MDataValue elem) {}

    public void deleteDestroyAction(MDestroyAction elem) {}

    public void deleteException(MException elem) {}

    /**
     * when an instance is deleted,
     * delete its linkend's.
     * similar to deleting a classifier in the CoreFactory.
     */
    public void deleteInstance(MInstance elem) {

        if (elem != null) {
            Collection col = ((MInstance) elem).getLinkEnds();
            Iterator it = col.iterator();
            while (it.hasNext()) {
                UmlFactory.getFactory().delete(it.next());
            }
        }
    }

    public void deleteLink(MLink elem) {}

    /**
     * when a linkend is deleted,
     * delete its Links
     */
    public void deleteLinkEnd(MLinkEnd elem) {

        MLink link = elem.getLink();
        if (link != null
            && link.getConnections() != null
            && link.getConnections().size() == 2) { // binary link
            UmlFactory.getFactory().delete(link);
        }
    }

    public void deleteLinkObject(MLinkObject elem) {}

    public void deleteNodeInstance(MNodeInstance elem) {}

    public void deleteObject(MObject elem) {}

    public void deleteReception(MReception elem) {}

    public void deleteReturnAction(MReturnAction elem) {}

    public void deleteSendAction(MSendAction elem) {}

    public void deleteSignal(MSignal elem) {}

    public void deleteStimulus(MStimulus elem) {}

    public void deleteTerminateAction(MTerminateAction elem) {}

    public void deleteUninterpretedAction(MUninterpretedAction elem) {}
}
