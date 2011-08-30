/* $Id$
 *****************************************************************************
 * Copyright (c) 2009,2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *    Tom Morris
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

import javax.jmi.reflect.InvalidObjectException;

import org.argouml.model.CollaborationsFactory;
import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.omg.uml.behavioralelements.collaborations.AssociationEndRole;
import org.omg.uml.behavioralelements.collaborations.AssociationRole;
import org.omg.uml.behavioralelements.collaborations.ClassifierRole;
import org.omg.uml.behavioralelements.collaborations.Collaboration;
import org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet;
import org.omg.uml.behavioralelements.collaborations.CollaborationsPackage;
import org.omg.uml.behavioralelements.collaborations.Interaction;
import org.omg.uml.behavioralelements.collaborations.InteractionInstanceSet;
import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;

/**
 * Factory to create UML classes for the UML BehaviorialElements::Collaborations
 * package.<p>
 *
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * @author Tom Morris
 * @author Thierry Lach (did derivation from NSUML implementation)
 */
class CollaborationsFactoryMDRImpl extends AbstractUmlModelFactoryMDR
        implements CollaborationsFactory {

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
    CollaborationsFactoryMDRImpl(MDRModelImplementation implementation) {
        modelImpl = implementation;
    }

    public AssociationEndRole createAssociationEndRole() {
        AssociationEndRole myAssociationEndRole =
            getCollabPkg().getAssociationEndRole().createAssociationEndRole();
        super.initialize(myAssociationEndRole);
        return myAssociationEndRole;
    }
    
    private CollaborationsPackage getCollabPkg() {
        return modelImpl.getUmlPackage().getCollaborations();
    }


    public AssociationRole createAssociationRole() {
        AssociationRole myAssociationRole =
            getCollabPkg().getAssociationRole().createAssociationRole();
        super.initialize(myAssociationRole);
        return myAssociationRole;
    }


    public ClassifierRole createClassifierRole() {
        return createLifeline();
    }


    public Collaboration createCollaboration() {
        Collaboration myCollaboration =
            getCollabPkg().getCollaboration().createCollaboration();
        super.initialize(myCollaboration);
        return myCollaboration;
    }


    public CollaborationInstanceSet createCollaborationInstanceSet() {
        CollaborationInstanceSet obj =
            getCollabPkg().getCollaborationInstanceSet()
                .createCollaborationInstanceSet();
        super.initialize(obj);
        return obj;
    }


    public Interaction createInteraction() {
        Interaction myInteraction =
            getCollabPkg().getInteraction().createInteraction();
        super.initialize(myInteraction);
        return myInteraction;
    }


    public InteractionInstanceSet createInteractionInstanceSet() {
        InteractionInstanceSet obj =
            getCollabPkg().getInteractionInstanceSet()
                .createInteractionInstanceSet();
        super.initialize(obj);
        return obj;
    }

    public ClassifierRole createLifeline() {
        ClassifierRole myClassifierRole =
            getCollabPkg().getClassifierRole().createClassifierRole();
        super.initialize(myClassifierRole);
        return myClassifierRole;
    }

    
    public Message createMessage() {
        Message myMessage =
            getCollabPkg().getMessage().createMessage();
        super.initialize(myMessage);
        return myMessage;
    }


    public ClassifierRole buildClassifierRole(Object collaboration) {
        Collaboration myCollaboration = (Collaboration) collaboration;
        ClassifierRole classifierRole = createClassifierRole();
        classifierRole.setNamespace(myCollaboration);
        modelImpl.getCoreHelper().setMultiplicity(classifierRole, 1, 1);
        return classifierRole;
    }


    public Object buildCollaboration(Object handle) {
        Namespace namespace = (Namespace) handle;
        Collaboration modelelement = createCollaboration();
        modelelement.setNamespace(namespace);
        modelelement.setName("newCollaboration");
        modelelement.setAbstract(false);
        return modelelement;
    }


    public Object buildCollaboration(Object namespace,
            Object representedElement) {
        if (!(namespace instanceof Namespace)) {
            throw new IllegalArgumentException("Argument is not "
                    + "a namespace");
        }

        if (representedElement instanceof Classifier
                || representedElement instanceof Operation) {

            Collaboration collaboration =
                (Collaboration) buildCollaboration(namespace);
            if (representedElement instanceof Classifier) {
                collaboration.
                setRepresentedClassifier((Classifier) representedElement);

                return collaboration;
            }
            if (representedElement instanceof Operation) {
                collaboration.
                setRepresentedOperation((Operation) representedElement);

                return collaboration;
            }
        }
        throw new IllegalArgumentException("Represented element must be"
                + " Collaboration or Operation");
    }


    public Interaction buildInteraction(Object handle) {
        Collaboration collab = (Collaboration) handle;
        Interaction inter = createInteraction();
        inter.setContext(collab);
        inter.setName("newInteraction");
        return inter;
    }
    
    public ClassifierRole buildLifeline(Object collaboration) {
        Collaboration myCollaboration = (Collaboration) collaboration;
        ClassifierRole classifierRole = createClassifierRole();
        classifierRole.setNamespace(myCollaboration);
        modelImpl.getCoreHelper().setMultiplicity(classifierRole, 1, 1);
        return classifierRole;
    }
    

    public AssociationEndRole buildAssociationEndRole(Object atype) {
        ClassifierRole type = (ClassifierRole) atype;
        AssociationEndRole end = createAssociationEndRole();
        end.setParticipant(type);
        return end;
    }

    public AssociationRole buildAssociationRole(Object from, Object to) {
        return buildAssociationRole((ClassifierRole) from, (ClassifierRole) to);
    }
    
    /**
     * Internal type-checked version of buildAssociationRole.
     */
    private AssociationRole buildAssociationRole(ClassifierRole from,
            ClassifierRole to) {
        Collaboration collaboration = (Collaboration) from.getNamespace();
        if (collaboration == null 
                || !collaboration.equals(to.getNamespace())) {
            throw new IllegalArgumentException("ClassifierRoles must be in"
                    + " same non-null namespace");
        }
        AssociationRole role = createAssociationRole();
        role.setNamespace(collaboration);
        // The 4-arg version of this method depends on this ordering.
        // Don't change it!
        role.getConnection().add(buildAssociationEndRole(from));
        role.getConnection().add(buildAssociationEndRole(to));
        return role;
    }


    @Deprecated
    public AssociationRole buildAssociationRole(Object from, Object agg1, Object to,
            Object agg2, Boolean unidirectional) {
        if (unidirectional == null) {
            return buildAssociationRole(from, agg1, to, agg2, false);
        } else {
            return buildAssociationRole(from, agg1, to, agg2, 
                    unidirectional.booleanValue());
        }
    }

    
    public AssociationRole buildAssociationRole(Object from, Object agg1,
            Object to, Object agg2, boolean unidirectional) {

        AggregationKind ak1 = checkAggregationKind(agg1);
        AggregationKind ak2 = checkAggregationKind(agg2);
        
        AssociationRole role = buildAssociationRole((ClassifierRole) from, 
                (ClassifierRole) to);

        AssociationEndRole end =
                (AssociationEndRole) role.getConnection().get(0);
        end.setAggregation(ak1);
        end.setNavigable(!unidirectional);

        end = (AssociationEndRole) role.getConnection().get(1);
        end.setAggregation(ak2);
        end.setNavigable(true); // probably redundant - just in case

        return role;
    }
    
    /**
     * Checks that aggregationKind is valid and promotes null
     * to AK_NONE.
     * @param aggregationKind Candidate AggregationKind or null
     * @return valid checked AggregationKind
     */
    private AggregationKind checkAggregationKind(Object aggregationKind) {
        if (aggregationKind == null) {
            aggregationKind = AggregationKindEnum.AK_NONE;
        }
        return (AggregationKind) aggregationKind;
    }


    public AssociationRole buildAssociationRole(Object link) {
        if (!(link instanceof Link)) {
            throw new IllegalArgumentException("Argument is not a link");
        }

        Object from = modelImpl.getCoreHelper().getSource(link);
        Object to = modelImpl.getCoreHelper().getDestination(link);
        Object classifierRoleFrom =
            modelImpl.getFacade().getClassifiers(from).iterator().next();
        Object classifierRoleTo =
            modelImpl.getFacade().getClassifiers(to).iterator().next();
        Object collaboration =
            modelImpl.getFacade().getNamespace(classifierRoleFrom);
        if (collaboration != modelImpl.getFacade().getNamespace(
                classifierRoleTo)) {
            throw new IllegalStateException("ClassifierRoles do not belong "
                    + "to the same collaboration");
        }
        if (collaboration == null) {
            throw new IllegalStateException("Collaboration may not be "
                    + "null");
        }
        AssociationRole associationRole = createAssociationRole();
        modelImpl.getCoreHelper().setNamespace(associationRole, collaboration);
        modelImpl.getCoreHelper().addLink(associationRole, link);
        return associationRole;
    }

    /**
     * Builds a message within some interaction related to some assocationrole.
     * The message is added as the last in the interaction sequence.
     * Furthermore, the message is added as the last to the list of messages
     * already attached to the role. Effectively, the already attached messages
     * become predecessors of this message.
     * TODO: This sets the activator as a side effect. However it is impossible
     * to determine the activator at this stage as we don't yet know what the
     * action will be of the message we're creating. See issue 5692.
     *
     * @param inter
     *            The Interaction.
     * @param role
     *            The Association Role.
     * @return The newly created Message.
     */
    private Message buildMessageInteraction(Interaction inter,
            AssociationRole role) {
        assert inter != null : "An interaction must be provided";
        assert role != null : "An association role must be provided";

        Message message = createMessage();

        inter.getMessage().add(message);

        message.setCommunicationConnection(role);

        if (role.getConnection().size() == 2) {
            message.setSender((ClassifierRole) role.getConnection().get(0)
                    .getParticipant());
            message.setReceiver((ClassifierRole) role.getConnection().get(1)
                    .getParticipant());

            Collection<Message> messages =
                Model.getFacade().getReceivedMessages(message.getSender());
            Message lastMsg = lastMessage(messages, message);

            if (lastMsg != null) {
                message.setActivator(lastMsg);
                messages = Model.getFacade().getActivatedMessages(lastMsg);
            } else {
                messages = Model.getFacade().getSentMessages(
                        message.getSender());
            }

            lastMsg = lastMessage(messages, message);
            if (lastMsg != null) {
                message.getPredecessor().add(findEnd(lastMsg));
            }

        }

        return message;
    }

    /**
     * Finds the last message in the collection not equal to null and not equal
     * to m.
     *
     * @param c
     *            A collection containing exclusively Messages.
     * @param m
     *            A Message.
     * @return The last message in the collection, or null.
     */
    private Message lastMessage(Collection<Message> c, Message m) {
        Message last = null;
        for (Message msg : c) {
            if (msg != null && msg != m) {
                last = msg;
            }
        }
        return last;
    }

    /**
     * Walks the tree of successors to m rooted until a leaf is found. The leaf
     * is the returned. If m is itself a leaf, then m is returned.
     *
     * @param m A Message.
     * @return The last message in one branch of the tree rooted at m.
     */
    private Message findEnd(Message m) {
        while (true) {
            Collection<Message> c = Model.getFacade().getSuccessors(m);
            Iterator<Message> it = c.iterator();
            if (!it.hasNext()) {
                return m;
            }
            m = it.next();
        }
    }


    public Message buildMessage(Object acollab, Object arole) {
        if (!(arole instanceof AssociationRole)) {
            throw new IllegalArgumentException(
                    "An association role must be supplied - got " + arole);
        }
        try {
            if (acollab instanceof Collaboration) {
                return buildMessageCollab((Collaboration) acollab,
                        (AssociationRole) arole);
            }
            if (acollab instanceof Interaction) {
                return buildMessageInteraction((Interaction) acollab,
                        (AssociationRole) arole);
            }
            throw new IllegalArgumentException("No valid object " + acollab);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    private Message buildMessageCollab(Collaboration collab,
            AssociationRole role) {
        Interaction inter = null;
        if (collab.getInteraction().size() == 0) {
            inter = buildInteraction(collab);
        } else {
            inter = (Interaction) (collab.getInteraction().toArray())[0];
        }
        return buildMessageInteraction(inter, role);
    }


    public Message buildActivator(Object owner, Object interaction) {
        Message theOwner = (Message) owner;
        Interaction theInteraction;
        if (interaction == null) {
            theInteraction = theOwner.getInteraction();
        } else {
            theInteraction = (Interaction) interaction;
        }
        if (interaction == null) {
            throw new IllegalArgumentException();
        }

        Message activator = createMessage();
        activator.setInteraction(theInteraction);
        theOwner.setActivator(activator);
        return activator;
    }

    /**
     * @param elem
     *            the associationendrole
     */
    void deleteAssociationEndRole(Object elem) {
        if (!(elem instanceof AssociationEndRole)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem
     *            the associationrole
     */
    void deleteAssociationRole(Object elem) {
        AssociationRole role = (AssociationRole) elem;
        for (Message message : role.getMessage()) {
            modelImpl.getUmlFactory().delete(message);
        }
    }

    /**
     * @param elem
     *            the UML element to be deleted
     */
    void deleteClassifierRole(Object elem) {
        ClassifierRole cr = (ClassifierRole) elem;
        // delete Messages which have this as sender or receiver
        CollaborationsPackage cPkg = ((org.omg.uml.UmlPackage) cr
                .refOutermostPackage()).getCollaborations();
        modelImpl.getUmlHelper().deleteCollection(
                cPkg.getAMessageSender().getMessage(cr));
        modelImpl.getUmlHelper().deleteCollection(
                cPkg.getAReceiverMessage().getMessage(cr));
        // TODO: delete Collaborations where this is the last ClassifierRole?
//        Object owner = cr.refImmediateComposite();
//        if (owner instanceof Collaboration) {
//            Collection ownedElements = ((Collaboration) owner)
//                    .getOwnedElement();
//            if (ownedElements.size() == 1 && ownedElements.contains(cr))
//                nsmodel.getUmlFactory().delete(owner);
//        }
    }

    /**
     * @param elem
     *            the UML element to be delete
     */
    void deleteCollaboration(Object elem) {
        if (!(elem instanceof Collaboration)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param elem
     *            the UML element to be delete
     */
    void deleteCollaborationInstanceSet(Object elem) {
        if (!(elem instanceof CollaborationInstanceSet)) {
            throw new IllegalArgumentException();
        }
        // InteractionInstanceSets will get deleted automatically
        // because they are associated by composition
    }

    /**
     * @param elem
     *            the UML element to be delete
     */
    void deleteInteraction(Object elem) {
        if (!(elem instanceof Interaction)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param elem
     *            the UML element to be delete
     */
    void deleteInteractionInstanceSet(Object elem) {
        if (!(elem instanceof InteractionInstanceSet)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param elem
     *            the UML element to be delete
     */
    void deleteMessage(Object elem) {
        Message message = (Message) elem;
        // If this is the only message contained in the Interaction
        // we delete the Interaction
        Interaction i = message.getInteraction();
        if (i != null && i.getMessage().size() == 1) {
            modelImpl.getUmlFactory().delete(i);
        }
    }

}
