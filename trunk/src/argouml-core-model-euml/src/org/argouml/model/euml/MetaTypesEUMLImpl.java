// $Id$
/*******************************************************************************
 * Copyright (c) 2007,2010 Tom Morris and other contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tom Morris - initial implementation
 *    thn
 *****************************************************************************/

package org.argouml.model.euml;

import org.argouml.model.MetaTypes;
import org.argouml.model.NotImplementedException;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.Action;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityPartition;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.BehavioralFeature;
import org.eclipse.uml2.uml.CallAction;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.CallConcurrencyKind;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.CreateObjectAction;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.DestroyObjectAction;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.ElementImport;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Expression;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionConstraint;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.ObjectNode;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Reception;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.StructuralFeature;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateParameterSubstitution;
import org.eclipse.uml2.uml.TimeEvent;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.UseCase;
import org.eclipse.uml2.uml.VisibilityKind;



/**
 * The implementation of the MetaTypes for EUML2.
 */
final class MetaTypesEUMLImpl implements MetaTypes {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    /**
     * Constructor.
     * 
     * @param implementation
     *            The ModelImplementation.
     */
    public MetaTypesEUMLImpl(EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public Object getAbstraction() {
        return Abstraction.class;
    }

    public Object getAction() {
        return Action.class;
    }

    public Object getActionExpression() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getActionState() {
        // TODO: ActionState, CallState, and SubactivityState have been
        // replaced in UML 2.0 by explicitly modeled Actions
        return State.class;
    }
    
    public Object getActivity() {
        return Activity.class;
    }

    public Object getActor() {
        return Actor.class;
    }

    public Object getAggregationKind() {
        return AggregationKind.class;
    }
    
    public Object getArtifact() {
        return Artifact.class;
    }

    public Object getAssociation() {
        return Association.class;
    }

    public Object getAssociationClass() {
        return AssociationClass.class;
    }

    public Object getAssociationEnd() {
        // an AssociationEnd is now a Property owned by an Association
        return Property.class;
    }

    public Object getAssociationEndRole() {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return Classifier.class;
    }

    public Object getAssociationRole() {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return Classifier.class;
    }

    public Object getAttribute() {
        return Property.class;
    }

    public Object getBehavioralFeature() {
        return BehavioralFeature.class;
    }
    
    public Object getBinding() {
        return TemplateBinding.class;
    }

    public Object getBooleanExpression() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getCallAction() {
        return CallAction.class;
    }

    public Object getCallBehaviorAction() {
        return CallBehaviorAction.class;
    }

    public Object getCallConcurrencyKind() {
        return CallConcurrencyKind.class;
    }

    public Object getCallState() {
        // TODO: ActionState, CallState, and SubactivityState have been replaced
        // in UML 2.0 by explicitly modeled Actions
        return State.class;
    }

    public Object getClassifier() {
        return Classifier.class;
    }

    public Object getClassifierRole() {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return Classifier.class;
    }

    public Object getCollaboration() {
        return Collaboration.class;
    }

    public Object getComment() {
        return Comment.class;
    }

    public Object getComponent() {
        return Component.class;
    }

    public Object getComponentInstance() {
        // TODO: Gone in UML 2.x
        // return place holder for now
        return InstanceSpecification.class;
    }

    public Object getCompositeState() {
        // TODO: no separate CompositeState in UML 2.1 - tfm
        return State.class;
    }
    
    public Object getConstraint() {
        return Constraint.class;
    }

    public Object getCreateAction() {
        throw new NotImplementedException("This is not a UML2 element");
    }

    public Object getCreateObjectAction() {
        return CreateObjectAction.class;
    }

    public Object getDataType() {
        return DataType.class;
    }

    public Object getDependency() {
        return Dependency.class;
    }

    public Object getDestroyAction() {
        throw new NotImplementedException("This is not a UML2 class");
    }
    
    public Object getDestroyObjectAction() {
        return DestroyObjectAction.class;
    }
    
    public Object getElementImport() {
        return ElementImport.class;
    }

    public Object getEnumeration() {
        return Enumeration.class;
    }

    public Object getEnumerationLiteral() {
        return EnumerationLiteral.class;
    }

    public Object getEvent() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getException() {
        // TODO: Exception has been removed for UML 2.x
        // just return Signal for now - tfm
        return Signal.class;
    }

    public Object getExtend() {
        return Extend.class;
    }

    public Object getExtensionPoint() {
        return ExtensionPoint.class;
    }
    
    public Object getFinalState() {
        return FinalState.class;
    }

    public Object getGeneralizableElement() {
        // Gone in UML 2.x - just Classifier now
        return Classifier.class;
    }

    public Object getGeneralization() {
        return Generalization.class;
    }

    public Object getGuard() {
        // TODO: Not really the same thing, but close
        return InteractionConstraint.class;
    }

    public Object getInclude() {
        return Include.class;
    }

    public Object getInstance() {
        // TODO: Check for changed semantics - tfm
        return InstanceSpecification.class;
    }

    public Object getInteraction() {
        return Interaction.class;
    }

    public Object getInterface() {
        return Interface.class;
    }

    public Object getLink() {
        // TODO: Gone in UML 2.1
        // It is now an InstanceSpecification with an 
        // Association as its classifier
        return InstanceSpecification.class;
    }

    public Object getMessage() {
        return Message.class;
    }

    public Object getModel() {
        return Model.class;
    }

    public Object getModelElement() {
        return Element.class;
    }

    public Object getMultiplicity() {
        return MultiplicityElement.class;
    }

    public String getName(Object element) {
        Class clazz;
        if (element instanceof Class) {
            clazz = (Class) element;
        } else {
            clazz = element.getClass();
        }
        String name = clazz.getName();

        // The name of the meta type is the class name (after the last .)
        // and before the "Impl" or end of class name.
        int startName = name.lastIndexOf('.') + 1;

        // Eclipse UML2 implementation classes often start with "UML"
        final String prefix = "UML"; //$NON-NLS-1$
        if (name.substring(startName).startsWith(prefix)) {
            startName += prefix.length();
        }

        // Eclipse UML2 implementation classes end with "Impl"
        final String suffix = "Impl"; //$NON-NLS-1$
        int endName = name.length();
        if (name.endsWith(suffix)) {
            endName -= suffix.length();
        }

        return name.substring(startName, endName);
    }

    public Object getNamespace() {
        return Namespace.class;
    }

    public Object getNode() {
        return Node.class;
    }

    public Object getNodeInstance() {
        // TODO: Gone in UML 2.x
        // Return place holder for now
        return InstanceSpecification.class;
    }

    public Object getObject() {
        // is used as InstanceSpecification, see UMLDeploymentDiagram
        return org.eclipse.uml2.uml.InstanceSpecification.class;
        // TODO: what about ObjectNode?
//        return org.eclipse.uml2.uml.ObjectNode.class;        
    }

    public Object getObjectFlowState() {
        // TODO: not in UML 2.1
        return ObjectNode.class;
    }

    public Object getOperation() {
        return Operation.class;
    }

    public Object getPackage() {
        return org.eclipse.uml2.uml.Package.class;
    }

    public Object getParameter() {
        return Parameter.class;
    }

    public Object getParameterDirectionKind() {
        return ParameterDirectionKind.class;
    }

    public Object getPartition() {
        return ActivityPartition.class;
    }

    public Object getPackageImport() {
        return PackageImport.class;
    }

    public Object getProfile() {
        return Profile.class;
    }
    
    public Object getPseudostate() {
        return Pseudostate.class;
    }

    public Object getPseudostateKind() {
        return PseudostateKind.class;
    }

    public Object getReception() {
        return Reception.class;
    }

    public Object getReturnAction() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getScopeKind() {
        // Not in UML 2.x - deprecated in Model API
        throw new NotImplementedException();
    }

    public Object getSendAction() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getSignal() {
        return Signal.class;
    }

    public Object getSimpleState() {
        // TODO: Gone in UML 2.1
        return State.class;
    }

    public Object getState() {
        return State.class;
    }

    public Object getStateMachine() {
        return StateMachine.class;
    }

    public Object getStateVertex() {
        // TODO: State & Vertex are independent classes in UML 2.1
        return State.class;
    }

    public Object getStereotype() {
        return Stereotype.class;
    }

    public Object getStimulus() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getStubState() {
        // TODO: gone in UML 2.1
        return State.class;
    }

    public Object getSubactivityState() {
        // TODO: ActionState, CallState, and SubactivityState have been replaced
        // in UML 2.0 by explicitly modeled Actions
        return State.class;
    }

    public Object getSubmachineState() {
        // TODO: gone in UML 2.1
        return State.class;
    }

    public Object getSubsystem() {
        // Changed in UML 2.1 - Component with <<subsystem>> stereotype
        // TODO: We should deprecate this?
        return Component.class;
    }

    public Object getSynchState() {
        // TODO: no separate SyncState in UML 2.1 - tfm
        return State.class;
    }

    public Object getTagDefinition() {
        // TODO: In UML 2.x a TagDefinition has become a Property on a Stereotype
        // Anything that uses this will probably need to be reviewed/changed.
        // Just return Property for now.
        return Property.class;
    }

    public Object getTaggedValue() {
        throw new NotYetImplementedException();
    }
    
    public Object getTemplateArgument() {
        // TODO: Check that this is correct
        return TemplateParameterSubstitution.class;
    }

    public Object getTemplateParameter() {
        return TemplateParameter.class;
    }
    
    public Object getTerminateAction() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object getTransition() {
        return Transition.class;
    }

    public Object getUMLClass() {
        return org.eclipse.uml2.uml.Class.class;
    }

    public Object getUsage() {
        return Usage.class;
    }

    public Object getUseCase() {
        return UseCase.class;
    }

    public Object getVisibilityKind() {
        return VisibilityKind.class;
    }

    
    public Object getActionSequence() {
        // TODO: Need UML 2.x equivalent
        return null /*ActionSequence.class*/;
    }

    public Object getArgument() {
        // TODO: Need UML 2.x equivalent
        return null /*Argument.class*/;
    }

    public Object getAttributeLink() {
        // TODO: Need UML 2.x equivalent
        return null /*AttributeLink.class*/;
    }

    public Object getCallEvent() {
        return CallEvent.class;
    }

    public Object getChangeEvent() {
        return ChangeEvent.class;
    }

    public Object getClassifierInState() {
        // TODO: Need UML 2.x equivalent
        return null /*ClassifierInState.class*/;
    }

    public Object getCollaborationInstanceSet() {
        // TODO: Need UML 2.x equivalent
        return null /*CollaborationInstanceSet.class*/;
    }

    public Object getDataValue() {
        // TODO: Need UML 2.x equivalent
        return null /*DataValue.class*/;
    }

    public Object getElement() {
        return Element.class;
    }

    public Object getElementResidence() {
        // TODO: Need UML 2.x equivalent
        return null /*ElementResidence.class*/;
    }

    public Object getExpression() {
        return Expression.class;
    }

    public Object getFeature() {
        return Feature.class;
    }

    public Object getFlow() {
        // TODO: Need UML 2.x equivalent
        return null /*Flow.class*/;
    }

    public Object getInteractionInstanceSet() {
        // TODO: Need UML 2.x equivalent
        return null /*InteractionInstanceSet.class*/;
    }

    public Object getLinkEnd() {
        // TODO: Need UML 2.x equivalent
        return null /*LinkEnd.class*/;
    }

    public Object getLinkObject() {
        // TODO: Need UML 2.x equivalent
        return null /*LinkObject.class*/;
    }

    public Object getMethod() {
        // TODO: Need UML 2.x equivalent
        return null /*Method.class*/;
    }

    public Object getMultiplicityRange() {
        return MultiplicityElement.class;
    }

    public Object getPrimitiveType() {
        return PrimitiveType.class;
    }

    public Object getRelationship() {
        return Relationship.class;
    }

    public Object getSignalEvent() {
        return SignalEvent.class;
    }

    public Object getStructuralFeature() {
        return StructuralFeature.class;
    }

    public Object getSubsystemInstance() {
        // TODO: Need UML 2.x equivalent
        return null /*SubsystemInstance.class*/;
    }

    public Object getTimeEvent() {
        return TimeEvent.class;
    }

    public Object getUninterpretedAction() {
        // TODO: Need UML 2.x equivalent
        return null /*UninterpretedAction.class*/;
    }

}
