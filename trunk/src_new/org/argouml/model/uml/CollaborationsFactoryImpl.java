// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.model.uml;

import java.util.Collection;
import java.util.Iterator;

import org.argouml.model.CollaborationsFactory;

import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.collaborations.MAssociationEndRole;
import ru.novosoft.uml.behavior.collaborations.MAssociationRole;
import ru.novosoft.uml.behavior.collaborations.MClassifierRole;
import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.collaborations.MInteraction;
import ru.novosoft.uml.behavior.collaborations.MMessage;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;

/**
 * Factory to create UML classes for the UML
 * BehaviorialElements::Collaborations package.
 *
 * TODO: Change visibility to package after reflection problem solved.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 */
public class CollaborationsFactoryImpl
	extends AbstractUmlModelFactory
	implements CollaborationsFactory {

    /**
     * The model implementation.
     */
    private NSUMLModelImplementation nsmodel;

    /**
     * Don't allow instantiation.
     *
     * @param implementation To get other helpers and factories.
     */
    CollaborationsFactoryImpl(NSUMLModelImplementation implementation) {
        nsmodel = implementation;
    }

    /**
     * Create an empty but initialized instance of a UML AssociationEndRole.
     *
     * @return an initialized UML AssociationEndRole instance.
     */
    public Object createAssociationEndRole() {
        MAssociationEndRole modelElement =
            MFactory.getDefaultFactory().createAssociationEndRole();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML AssociationRole.
     *
     * @return an initialized UML AssociationRole instance.
     */
    public Object createAssociationRole() {
        MAssociationRole modelElement =
            MFactory.getDefaultFactory().createAssociationRole();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML ClassifierRole.
     *
     * @return an initialized UML ClassifierRole instance.
     */
    public Object createClassifierRole() {
        MClassifierRole modelElement =
            MFactory.getDefaultFactory().createClassifierRole();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML Collaboration.
     *
     * @return an initialized UML Collaboration instance.
     */
    public Object createCollaboration() {
        MCollaboration modelElement =
            MFactory.getDefaultFactory().createCollaboration();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML Interaction.
     *
     * @return an initialized UML Interaction instance.
     */
    public Object createInteraction() {
        MInteraction modelElement =
            MFactory.getDefaultFactory().createInteraction();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML Message.
     *
     * @return an initialized UML Message instance.
     */
    public Object createMessage() {
        MMessage modelElement = MFactory.getDefaultFactory().createMessage();
        super.initialize(modelElement);
        return modelElement;
    }

    /**
     * Creates a classifierrole and adds it to the given collaboration.
     *
     * @param collaboration the given collaboration
     * @return the created classifier role
     */
    public Object buildClassifierRole(Object collaboration) {
        if (!(collaboration instanceof MCollaboration)) {
            throw new IllegalArgumentException(
                    "Argument is not a collaboration");
        }

        MClassifierRole classifierRole =
            (MClassifierRole) createClassifierRole();
        ((MCollaboration) collaboration).addOwnedElement(classifierRole);
        return classifierRole;
    }

    /**
     * Builds a default collaboration not attached to a classifier.
     * I.e. the represented element is NOT filled in.
     *
     * @param handle the namespace for the collaboration
     * @return the created collaboration
     */
    public Object buildCollaboration(Object handle) {
        if (!(handle instanceof MNamespace)) {
            throw new IllegalArgumentException("Argument is not a namespace");
        }

        MNamespace namespace = (MNamespace) handle;
        MCollaboration modelelement =
            (MCollaboration) createCollaboration();
        modelelement.setNamespace(namespace);
        modelelement.setName("newCollaboration");
        modelelement.setAbstract(false);
        return modelelement;
    }

    /**
     * Builds a collaboration that is owned by a certain namespace and
     * represents the given represented element.
     *
     * @param namespace the namespace for the collaboration
     * @param representedElement the represented element
     * @return the created collaboration
     */
    public Object buildCollaboration(
        Object namespace,
        Object representedElement) {
        if (!(namespace instanceof MNamespace)) {
            throw new IllegalArgumentException("Argument is not "
			   + "a namespace or element "
			   + "that can be represented "
			   + "by a collaboration");
        }

        if (!(representedElement instanceof MClassifier
                || representedElement instanceof MOperation)) {
            throw new IllegalArgumentException();
        }

        MCollaboration collaboration =
            (MCollaboration) buildCollaboration(namespace);
        if (representedElement instanceof MClassifier) {
            collaboration.setRepresentedClassifier(
                    (MClassifier) representedElement);
            return collaboration;
        }
        if (representedElement instanceof MOperation) {
            collaboration.setRepresentedOperation(
                    (MOperation) representedElement);
            return collaboration;
        }
        // Not reached.
        return null;
    }

    /**
     * Builds an interaction belonging to some collaboration.
     *
     * @param handle the collaboration that will be the context
     * for the new interaction
     * @return the newly build interaction
     */
    public Object buildInteraction(Object handle) {
        if (!(handle instanceof MCollaboration)) {
            throw new IllegalArgumentException(
                    "Argument is not a collaboration");
        }

        MCollaboration collab = (MCollaboration) handle;
        MInteraction inter = (MInteraction) createInteraction();
        inter.setContext(collab);
        inter.setName("newInteraction");
        return inter;
    }

    /**
     * Builds an associationendrole based on some classifierrole.
     *
     * @param atype the classifierrole
     * @return the associationendrole
     */
    public Object buildAssociationEndRole(Object atype) {
        if (!(atype instanceof MClassifierRole)) {
            throw new IllegalArgumentException();
        }

        MAssociationEndRole end =
            (MAssociationEndRole) createAssociationEndRole();
        end.setType((MClassifierRole) atype);
        return end;
    }

    /**
     * Builds a binary associationrole on basis of two classifierroles.
     *
     * @param from the first classifierrole
     * @param to the second classifierrole
     * @return the newly build associationrole
     */
    public Object buildAssociationRole(Object from, Object to) {
        if (!(from instanceof MClassifierRole)) {
            throw new IllegalArgumentException("from");
        }
        if (!(to instanceof MClassifierRole)) {
            throw new IllegalArgumentException("to");
        }

        MCollaboration colFrom =
	    (MCollaboration) ((MClassifierRole) from).getNamespace();
        MCollaboration colTo =
            (MCollaboration) ((MClassifierRole) to).getNamespace();
        if (colFrom != null && colFrom.equals(colTo)) {
            MAssociationRole role = (MAssociationRole) createAssociationRole();
            // we do not create on basis of associations between the
            // bases of the classifierroles
            role.addConnection(
                    (MAssociationEndRole) buildAssociationEndRole(from));
            role.addConnection(
                    (MAssociationEndRole) buildAssociationEndRole(to));
            colFrom.addOwnedElement(role);
            return role;
        }
        return null;
    }

    /**
     * Builds a binary associationrole on basis of two classifierroles,
     * navigation and aggregation.
     *
     * @param from   the first classifierrole
     * @param agg1   the first aggregationkind
     * @param to     the second classifierrole
     * @param agg2   the second aggregationkind
     * @param unidirectional true if unidirectional
     * @return the newly build assoc. role
     */
    public Object buildAssociationRole(
        Object from,
        Object agg1,
        Object to,
        Object agg2,
        Boolean unidirectional) {
        if (!(from instanceof MClassifierRole)) {
            throw new IllegalArgumentException();
        }
        if (!(to instanceof MClassifierRole)) {
            throw new IllegalArgumentException();
        }

        MCollaboration colFrom =
            (MCollaboration) ((MClassifierRole) from).getNamespace();
        MCollaboration colTo =
            (MCollaboration) ((MClassifierRole) to).getNamespace();

        if (agg1 == null) {
            agg1 = MAggregationKind.NONE;
        }
        if (!(agg1 instanceof MAggregationKind)) {
            throw new IllegalArgumentException();
        }

        if (agg2 == null) {
            agg2 = MAggregationKind.NONE;
        }
        if (!(agg2 instanceof MAggregationKind)) {
            throw new IllegalArgumentException();
        }

        if (colFrom != null && colFrom.equals(colTo)) {
            boolean nav1 = Boolean.FALSE.equals(unidirectional);
            boolean nav2 = true;
            MAssociationRole role = (MAssociationRole) createAssociationRole();
            // we do not create on basis of associations between the
            // bases of the classifierroles
            MAssociationEndRole fromEnd =
                (MAssociationEndRole) buildAssociationEndRole(from);
            fromEnd.setNavigable(nav1);
            fromEnd.setAggregation((MAggregationKind) agg1);
            role.addConnection(fromEnd);

            MAssociationEndRole toEnd =
                (MAssociationEndRole) buildAssociationEndRole(to);
            toEnd.setNavigable(nav2);
            toEnd.setAggregation((MAggregationKind) agg2);
            role.addConnection(toEnd);

            colFrom.addOwnedElement(role);
            return role;
        }
        return null;
    }

    /**
     * Builds an associationrole based on a given link. The link must
     * have a source and a destination instance that both have a
     * classifierrole as classifier.  The classifierroles must have
     * the same collaboration as owner. This collaboration will be the
     * new owner of the associationrole.
     *
     * @param link a UML Link
     * @return the newly created association role (an Object)
     */
    public Object buildAssociationRole(Object link) {
        if (!(link instanceof MLink)) {
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
        if (collaboration
                != nsmodel.getFacade().getNamespace(classifierRoleTo)) {
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
     * Builds a message within some interaction related to some
     * assocationrole. The message is added as the last in the
     * interaction sequence. Furthermore, the message is added as the
     * last to the list of messages allready attached to the
     * role. Effectively, the allready attached messages become
     * predecessors of this message.
     *
     * @param inter The Interaction.
     * @param role The Association Role.
     * @return The newly created Message.
     */
    private MMessage buildMessageInteraction(MInteraction inter,
					     MAssociationRole role) {
        if (inter == null || role == null) {
            return null;
        }

        MMessage message = (MMessage) createMessage();

        inter.addMessage(message);

        message.setCommunicationConnection(role);

        if (role.getConnections().size() == 2) {
            message.setSender(
                    (MClassifierRole) role.getConnection(0).getType());
            message.setReceiver(
                (MClassifierRole) role.getConnection(1).getType());

            Collection messages = message.getSender().getMessages1();
            MMessage lastMsg = lastMessage(messages, message);

            if (lastMsg != null) {
                message.setActivator(lastMsg);
                messages = lastMsg.getMessages4();
            } else {
                messages = message.getSender().getMessages2();
            }

            lastMsg = lastMessage(messages, message);
            if (lastMsg != null) {
                message.addPredecessor(findEnd(lastMsg));
            }

        }

        return message;
    }

    /**
     * Finds the last message in the collection not equal to null and not
     * equal to m.
     *
     * @param c A collection containing exclusively MMessages.
     * @param m A MMessage.
     * @return The last message in the collection, or null.
     */
    private MMessage lastMessage(Collection c, MMessage m) {
        MMessage last = null;
        Iterator it = c.iterator();
        while (it.hasNext()) {
            MMessage msg = (MMessage) it.next();
            if (msg != null && msg != m) {
                last = msg;
            }
        }
        return last;
    }

    /**
     * Walks the tree of successors to m rooted until a leaf is found. The
     * leaf is the returned. If m is itself a leaf, then m is returned.
     *
     * @param m A MMessage.
     * @return The last message in one branch of the tree rooted at m.
     */
    private MMessage findEnd(MMessage m) {
        while (true) {
            Collection c = m.getMessages3();
            Iterator it = c.iterator();
            if (!it.hasNext()) {
                return m;
            }
            m = (MMessage) it.next();
        }
    }

    /**
     * Builds a message within some collaboration or interaction.
     *
     * @param acollab a collaboration or interaction
     * @param arole an associationrole
     * @return the newly build message
     */
    public Object buildMessage(Object acollab, Object arole) {
	if (acollab instanceof MCollaboration) {
	    return buildMessageCollab(acollab, arole);
	}
	if (acollab instanceof MInteraction) {
	    return buildMessageInteraction((MInteraction) acollab,
					   (MAssociationRole) arole);
	}
	throw new IllegalArgumentException("No valid object " + acollab);
    }

    private Object buildMessageCollab(Object acollab, Object arole) {
        MCollaboration collab = (MCollaboration) acollab;
        MAssociationRole role = (MAssociationRole) arole;
        MInteraction inter = null;
        if (collab.getInteractions().size() == 0) {
            inter = (MInteraction) buildInteraction(collab);
        } else {
            inter = (MInteraction) (collab.getInteractions().toArray())[0];
        }
        return buildMessageInteraction(inter, role);
    }

    /**
     * Builds an activator for some message.
     *
     * @param owner the owner
     * @param interaction the interaction
     * @return the newly build message
     */
    public Object buildActivator(Object owner, Object interaction) {
        if (owner == null) {
            return null;
        }
        if (!(owner instanceof MMessage)) {
            throw new IllegalArgumentException();
        }

        if (interaction == null) {
            interaction = ((MMessage) owner).getInteraction();
        }
        if (interaction == null) {
            return null;
        }
        if (!(interaction instanceof MInteraction)) {
            throw new IllegalArgumentException();
        }

        MMessage activator = (MMessage) createMessage();
        activator.setInteraction((MInteraction) interaction);
        ((MMessage) owner).setActivator(activator);
        return activator;
    }

    /**
     * @param elem the associationendrole
     */
    void deleteAssociationEndRole(Object elem) {
        if (!(elem instanceof MAssociationEndRole)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the associationrole
     */
    void deleteAssociationRole(Object elem) {
        if (!(elem instanceof MAssociationRole)) {
            throw new IllegalArgumentException();
        }

        Iterator it = ((MAssociationRole) elem).getMessages().iterator();
        while (it.hasNext()) {
            nsmodel.getUmlFactory().delete(it.next());
        }
    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteClassifierRole(Object elem) {
        if (!(elem instanceof MClassifierRole)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be delete
     */
    void deleteCollaboration(Object elem) {
        if (!(elem instanceof MCollaboration)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be delete
     */
    void deleteInteraction(Object elem) {
        if (!(elem instanceof MInteraction)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be delete
     */
    void deleteMessage(Object elem) {
        if (!(elem instanceof MMessage)) {
            throw new IllegalArgumentException();
        }

    }

}
