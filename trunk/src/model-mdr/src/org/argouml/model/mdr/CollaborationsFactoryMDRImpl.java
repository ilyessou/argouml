// $Id$
// Copyright (c) 1996-2006 The Regents of the University of California. All
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
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.Multiplicity;

/**
 * Factory to create UML classes for the UML BehaviorialElements::Collaborations
 * package.<p>
 *
 * TODO: Change visibility to package after reflection problem solved.
 *
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * @author Tom Morris
 * @author Thierry Lach (did derivation from NSUML implementation)
 */
public class CollaborationsFactoryMDRImpl extends AbstractUmlModelFactoryMDR
        implements CollaborationsFactory {

    /**
     * The model implementation.
     */
    private MDRModelImplementation nsmodel;

    /**
     * The Collaborations package.
     */
    private CollaborationsPackage collabPkg;

    /**
     * Don't allow instantiation.
     *
     * @param implementation
     *            To get other helpers and factories.
     */
    CollaborationsFactoryMDRImpl(MDRModelImplementation implementation) {
        nsmodel = implementation;
        collabPkg = nsmodel.getUmlPackage().getCollaborations();
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createAssociationEndRole()
     */
    public Object createAssociationEndRole() {
        AssociationEndRole myAssociationEndRole =
            collabPkg.getAssociationEndRole().createAssociationEndRole();
        super.initialize(myAssociationEndRole);
        return myAssociationEndRole;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createAssociationRole()
     */
    public Object createAssociationRole() {
        AssociationRole myAssociationRole =
            collabPkg.getAssociationRole().createAssociationRole();
        super.initialize(myAssociationRole);
        return myAssociationRole;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createClassifierRole()
     */
    public Object createClassifierRole() {
        ClassifierRole myClassifierRole =
            collabPkg.getClassifierRole().createClassifierRole();
        super.initialize(myClassifierRole);
        return myClassifierRole;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createCollaboration()
     */
    public Object createCollaboration() {
        Collaboration myCollaboration =
            collabPkg.getCollaboration().createCollaboration();
        super.initialize(myCollaboration);
        return myCollaboration;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createCollaborationInstanceSet()
     */
    public Object createCollaborationInstanceSet() {
        CollaborationInstanceSet obj =
            collabPkg.getCollaborationInstanceSet()
                .createCollaborationInstanceSet();
        super.initialize(obj);
        return obj;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createInteraction()
     */
    public Object createInteraction() {
        Interaction myInteraction =
            collabPkg.getInteraction().createInteraction();
        super.initialize(myInteraction);
        return myInteraction;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#createInteractionInstanceSet()
     */
    public Object createInteractionInstanceSet() {
        InteractionInstanceSet obj =
            collabPkg.getInteractionInstanceSet()
                .createInteractionInstanceSet();
        super.initialize(obj);
        return obj;
    }
    /*
     * @see org.argouml.model.CollaborationsFactory#createMessage()
     */
    public Object createMessage() {
        Message myMessage =
            collabPkg.getMessage().createMessage();
        super.initialize(myMessage);
        return myMessage;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildClassifierRole(java.lang.Object)
     */
    public Object buildClassifierRole(Object collaboration) {
        if (!(collaboration instanceof Collaboration)) {
            throw new IllegalArgumentException(
                    "Argument is not a collaboration");
        }

        ClassifierRole classifierRole = (ClassifierRole) createClassifierRole();
        ((Collaboration) collaboration).getOwnedElement().add(classifierRole);
        classifierRole.setMultiplicity((Multiplicity) Model
                .getDataTypesFactory().createMultiplicity("1..1"));
        return classifierRole;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildCollaboration(java.lang.Object)
     */
    public Object buildCollaboration(Object handle) {
        if (!(handle instanceof Namespace)) {
            throw new IllegalArgumentException("Argument is not a namespace");
        }

        Namespace namespace = (Namespace) handle;
        Collaboration modelelement = (Collaboration) createCollaboration();
        modelelement.setNamespace(namespace);
        modelelement.setName("newCollaboration");
        modelelement.setAbstract(false);
        return modelelement;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildCollaboration(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildCollaboration(Object namespace,
            Object representedElement) {
        if (!(namespace instanceof Namespace)) {
            throw new IllegalArgumentException("Argument is not "
                    + "a namespace or element " + "that can be represented "
                    + "by a collaboration");
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

    /*
     * @see org.argouml.model.CollaborationsFactory#buildInteraction(java.lang.Object)
     */
    public Object buildInteraction(Object handle) {
        if (!(handle instanceof Collaboration)) {
            throw new IllegalArgumentException(
                    "Argument is not a collaboration");
        }

        Collaboration collab = (Collaboration) handle;
        Interaction inter = (Interaction) createInteraction();
        inter.setContext(collab);
        inter.setName("newInteraction");
        return inter;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildAssociationEndRole(java.lang.Object)
     */
    public Object buildAssociationEndRole(Object atype) {
        if (!(atype instanceof ClassifierRole)) {
            throw new IllegalArgumentException();
        }

        AssociationEndRole end =
            (AssociationEndRole) createAssociationEndRole();
        end.setParticipant((ClassifierRole) atype);

        return end;
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildAssociationRole(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildAssociationRole(Object from, Object to) {
        if (!(from instanceof ClassifierRole)) {
            throw new IllegalArgumentException("from");
        }
        if (!(to instanceof ClassifierRole)) {
            throw new IllegalArgumentException("to");
        }

        Collaboration colFrom =
            (Collaboration) ((ClassifierRole) from).getNamespace();
        Collaboration colTo =
            (Collaboration) ((ClassifierRole) to).getNamespace();
        if (colFrom != null && colFrom.equals(colTo)) {
            AssociationRole role = (AssociationRole) createAssociationRole();
            // we do not create on basis of associations between the
            // bases of the classifierroles
            role.getConnection().add(buildAssociationEndRole(from));
            role.getConnection().add(buildAssociationEndRole(to));
            colFrom.getOwnedElement().add(role);

            return role;
        }
        throw new IllegalArgumentException("Collaborations must be in"
                + " same non-null namespace");
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildAssociationRole(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Object,
     *      java.lang.Boolean)
     */
    public Object buildAssociationRole(Object from, Object agg1, Object to,
            Object agg2, Boolean unidirectional) {
        if (!(from instanceof ClassifierRole)) {
            throw new IllegalArgumentException();
        }
        if (!(to instanceof ClassifierRole)) {
            throw new IllegalArgumentException();
        }

        Collaboration colFrom =
            (Collaboration) ((ClassifierRole) from).getNamespace();
        Collaboration colTo =
            (Collaboration) ((ClassifierRole) to).getNamespace();

        if (agg1 == null) {
            agg1 = AggregationKindEnum.AK_NONE;
        }
        if (!(agg1 instanceof AggregationKind)) {
            throw new IllegalArgumentException();
        }

        if (agg2 == null) {
            agg2 = AggregationKindEnum.AK_NONE;
        }
        if (!(agg2 instanceof AggregationKind)) {
            throw new IllegalArgumentException();
        }

        if (colFrom != null && colFrom.equals(colTo)) {
            boolean nav1 = Boolean.FALSE.equals(unidirectional);
            boolean nav2 = true;
            AssociationRole role = (AssociationRole) createAssociationRole();
            // we do not create on basis of associations between the
            // bases of the classifierroles
            AssociationEndRole fromEnd =
                (AssociationEndRole) buildAssociationEndRole(from);
            fromEnd.setNavigable(nav1);
            fromEnd.setAggregation((AggregationKind) agg1);
            role.getConnection().add(fromEnd);

            AssociationEndRole toEnd =
                (AssociationEndRole) buildAssociationEndRole(to);
            toEnd.setNavigable(nav2);
            toEnd.setAggregation((AggregationKind) agg2);
            role.getConnection().add(toEnd);

            colFrom.getOwnedElement().add(role);
            return role;
        }
        throw new IllegalArgumentException(
                "Collaborations must be in"
                + " same non-null namespace");
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildAssociationRole(java.lang.Object)
     */
    public Object buildAssociationRole(Object link) {
        if (!(link instanceof Link)) {
            throw new IllegalArgumentException("Argument is not a link");
        }

        Object from = nsmodel.getCoreHelper().getSource(link);
        Object to = nsmodel.getCoreHelper().getDestination(link);
        Object classifierRoleFrom =
            nsmodel.getFacade().getClassifiers(from).iterator().next();
        Object classifierRoleTo =
            nsmodel.getFacade().getClassifiers(to).iterator().next();
        Object collaboration =
            nsmodel.getFacade().getNamespace(classifierRoleFrom);
        if (collaboration != nsmodel.getFacade().getNamespace(
                classifierRoleTo)) {
            throw new IllegalStateException("ClassifierRoles do not belong "
                    + "to the same collaboration");
        }
        if (collaboration == null) {
            throw new IllegalStateException("Collaboration may not be "
                    + "null");
        }
        Object associationRole = createAssociationRole();
        nsmodel.getCoreHelper().setNamespace(associationRole, collaboration);
        nsmodel.getCoreHelper().addLink(associationRole, link);
        return associationRole;
    }

    /**
     * Builds a message within some interaction related to some assocationrole.
     * The message is added as the last in the interaction sequence.
     * Furthermore, the message is added as the last to the list of messages
     * allready attached to the role. Effectively, the already attached messages
     * become predecessors of this message.
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

        Message message = (Message) createMessage();

        inter.getMessage().add(message);

        message.setCommunicationConnection(role);

        if (role.getConnection().size() == 2) {
            message.setSender((ClassifierRole) ((AssociationEnd) role.
                    getConnection().get(0)).getParticipant());
            message.setReceiver((ClassifierRole) ((AssociationEnd) role.
                    getConnection().get(1)).getParticipant());

            Collection messages =
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
     *            A collection containing exclusively MMessages.
     * @param m
     *            A MMessage.
     * @return The last message in the collection, or null.
     */
    private Message lastMessage(Collection c, Message m) {
        Message last = null;
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Message msg = (Message) it.next();
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
     * @param m A MMessage.
     * @return The last message in one branch of the tree rooted at m.
     */
    private Message findEnd(Message m) {
        while (true) {
            Collection c = Model.getFacade().getSuccessors(m);
            Iterator it = c.iterator();
            if (!it.hasNext()) {
                return m;
            }
            m = (Message) it.next();
        }
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildMessage(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildMessage(Object acollab, Object arole) {
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

    private Object buildMessageCollab(Collaboration collab,
            AssociationRole role) {
        Interaction inter = null;
        if (collab.getInteraction().size() == 0) {
            inter = (Interaction) buildInteraction(collab);
        } else {
            inter = (Interaction) (collab.getInteraction().toArray())[0];
        }
        return buildMessageInteraction(inter, role);
    }

    /*
     * @see org.argouml.model.CollaborationsFactory#buildActivator(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildActivator(Object owner, Object interaction) {
        if (!(owner instanceof Message)) {
            throw new IllegalArgumentException(
                    "A message must be supplied as the owner");
        }

        if (interaction == null) {
            interaction = ((Message) owner).getInteraction();
        }
        if (interaction == null) {
            throw new IllegalArgumentException();
        }
        if (!(interaction instanceof Interaction)) {
            throw new IllegalArgumentException();
        }

        Message activator = (Message) createMessage();
        activator.setInteraction((Interaction) interaction);
        ((Message) owner).setActivator(activator);
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
        if (!(elem instanceof AssociationRole)) {
            throw new IllegalArgumentException();
        }

        Iterator it = ((AssociationRole) elem).getMessage().iterator();
        while (it.hasNext()) {
            nsmodel.getUmlFactory().delete(it.next());
        }

    }

    /**
     * @param elem
     *            the UML element to be deleted
     */
    void deleteClassifierRole(Object elem) {
        if (!(elem instanceof ClassifierRole)) {
            throw new IllegalArgumentException();
        }
        ClassifierRole cr = (ClassifierRole) elem;
        // delete Messages which have this as sender or receiver
        nsmodel.getUmlHelper().deleteCollection(
                collabPkg.getAMessageSender().getMessage(cr));
        nsmodel.getUmlHelper().deleteCollection(
                collabPkg.getAReceiverMessage().getMessage(cr));
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
        if (!(elem instanceof Message)) {
            throw new IllegalArgumentException();
        }
        // If this is the only message contained in the Interaction
        // we delete the Interaction
        Interaction i = ((Message) elem).getInteraction();
        if (i != null && i.getMessage().size() == 1) {
            nsmodel.getUmlFactory().delete(i);
        }
    }

}
