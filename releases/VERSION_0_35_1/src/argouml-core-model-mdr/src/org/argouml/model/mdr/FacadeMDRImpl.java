/* $Id$
 *******************************************************************************
 * Copyright (c) 2005-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Linus Tolke - initial framework implementation
 *    <see source control change log for other early contributors>
 *    Ludovic Maitre - UML 1.4
 *    Tom Morris - UML 1.4
 *    Bob Tarling
 *    Michiel van der Wulp
 *    Luis Sergio Oliveira (euluis)
 *******************************************************************************
 */
// Copyright (c) 2005-2009 The Regents of the University of California. All
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

package org.argouml.model.mdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmi.model.MofClass;
import javax.jmi.model.Reference;
import javax.jmi.reflect.InvalidObjectException;
import javax.jmi.reflect.RefBaseObject;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefFeatured;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

import org.argouml.model.CoreFactory;
import org.argouml.model.Facade;
import org.argouml.model.InvalidElementException;
import org.argouml.model.NotImplementedException;
import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.CallState;
import org.omg.uml.behavioralelements.activitygraphs.ClassifierInState;
import org.omg.uml.behavioralelements.activitygraphs.ObjectFlowState;
import org.omg.uml.behavioralelements.activitygraphs.Partition;
import org.omg.uml.behavioralelements.activitygraphs.SubactivityState;
import org.omg.uml.behavioralelements.collaborations.AssociationEndRole;
import org.omg.uml.behavioralelements.collaborations.AssociationRole;
import org.omg.uml.behavioralelements.collaborations.ClassifierRole;
import org.omg.uml.behavioralelements.collaborations.Collaboration;
import org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet;
import org.omg.uml.behavioralelements.collaborations.Interaction;
import org.omg.uml.behavioralelements.collaborations.InteractionInstanceSet;
import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.ActionSequence;
import org.omg.uml.behavioralelements.commonbehavior.Argument;
import org.omg.uml.behavioralelements.commonbehavior.AttributeLink;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
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
import org.omg.uml.behavioralelements.statemachines.CallEvent;
import org.omg.uml.behavioralelements.statemachines.ChangeEvent;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.Event;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SignalEvent;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.StubState;
import org.omg.uml.behavioralelements.statemachines.SubmachineState;
import org.omg.uml.behavioralelements.statemachines.SynchState;
import org.omg.uml.behavioralelements.statemachines.TimeEvent;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.usecases.Actor;
import org.omg.uml.behavioralelements.usecases.Extend;
import org.omg.uml.behavioralelements.usecases.ExtensionPoint;
import org.omg.uml.behavioralelements.usecases.Include;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.foundation.core.Abstraction;
import org.omg.uml.foundation.core.Artifact;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.Binding;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Comment;
import org.omg.uml.foundation.core.Component;
import org.omg.uml.foundation.core.Constraint;
import org.omg.uml.foundation.core.DataType;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Element;
import org.omg.uml.foundation.core.ElementResidence;
import org.omg.uml.foundation.core.Enumeration;
import org.omg.uml.foundation.core.EnumerationLiteral;
import org.omg.uml.foundation.core.Feature;
import org.omg.uml.foundation.core.Flow;
import org.omg.uml.foundation.core.GeneralizableElement;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.Interface;
import org.omg.uml.foundation.core.Method;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Node;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.core.Permission;
import org.omg.uml.foundation.core.Primitive;
import org.omg.uml.foundation.core.Relationship;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StructuralFeature;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TemplateArgument;
import org.omg.uml.foundation.core.TemplateParameter;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.Usage;
import org.omg.uml.foundation.datatypes.ActionExpression;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.CallConcurrencyKind;
import org.omg.uml.foundation.datatypes.CallConcurrencyKindEnum;
import org.omg.uml.foundation.datatypes.ChangeableKind;
import org.omg.uml.foundation.datatypes.ChangeableKindEnum;
import org.omg.uml.foundation.datatypes.Expression;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.foundation.datatypes.OrderingKind;
import org.omg.uml.foundation.datatypes.OrderingKindEnum;
import org.omg.uml.foundation.datatypes.ParameterDirectionKind;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;
import org.omg.uml.foundation.datatypes.PseudostateKind;
import org.omg.uml.foundation.datatypes.ScopeKind;
import org.omg.uml.foundation.datatypes.ScopeKindEnum;
import org.omg.uml.foundation.datatypes.VisibilityKind;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;
import org.omg.uml.modelmanagement.ElementImport;
import org.omg.uml.modelmanagement.Model;
import org.omg.uml.modelmanagement.Subsystem;
import org.omg.uml.modelmanagement.UmlPackage;

/**
 * Model Facade implementation.
 *
 * This class provides read-only access to model data in the repository.
 * Factories and setters are in separate UML package specific classes.
 * <p>
 * Most methods in this class can throw the run-time exception
 * {@link org.argouml.model.InvalidElementException}.
 * <p>
 * Unless otherwise noted by the Javadoc, all methods in this class implement
 * methods from the interface {@link Facade}.
 */
class FacadeMDRImpl implements Facade {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(FacadeMDRImpl.class.getName());

    private MDRModelImplementation modelImpl;

    // Shorthand notation for convenience
    private static final javax.jmi.model.AggregationKindEnum MOF_COMPOSITE =
        javax.jmi.model.AggregationKindEnum.COMPOSITE;

    /**
     * Constructor.
     *
     * @param impl
     *            The model implementation
     */
    public FacadeMDRImpl(MDRModelImplementation impl) {
        modelImpl = impl;
    }

    public String getUmlVersion() {
        return "1.4";
    }

    public boolean isAAbstraction(Object handle) {
        return handle instanceof Abstraction;
    }

    public boolean isAAcceptEventAction(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isAAction(Object handle) {
        return handle instanceof Action;
    }

    public boolean isAActionExpression(Object handle) {
        return handle instanceof ActionExpression;
    }

    public boolean isAActionSequence(Object handle) {
        return handle instanceof ActionSequence;
    }

    public boolean isAActionState(Object handle) {
        return handle instanceof ActionState;
    }

    public boolean isACallState(Object handle) {
        return handle instanceof CallState;
    }

    public boolean isAObjectFlowState(Object handle) {
        return handle instanceof ObjectFlowState;
    }

    public boolean isAObjectNode(Object handle) {
        // NOT UML1.4
        return false;
    }

    public boolean isASubactivityState(Object handle) {
        return handle instanceof SubactivityState;
    }

    public boolean isAActor(Object handle) {
        return handle instanceof Actor;
    }

    public boolean isAAggregationKind(Object handle) {
        return handle instanceof AggregationKind;
    }

    public boolean isAAppliedProfileElement(Object handle) {
        // non existent in UML1
        return false;
    }

    public boolean isAArgument(Object modelElement) {
        return modelElement instanceof Argument;
    }

    public boolean isAArtifact(Object modelElement) {
        return modelElement instanceof Artifact;
    }

    public boolean isAAssociation(Object handle) {
        return handle instanceof UmlAssociation;
    }

    public boolean isAAssociationEnd(Object handle) {
        return handle instanceof AssociationEnd;
    }

    public boolean isAAssociationRole(Object handle) {
        return handle instanceof AssociationRole;
    }

    public boolean isAAssociationEndRole(Object handle) {
        return handle instanceof AssociationEndRole;
    }

    public boolean isAAttribute(Object handle) {
        return handle instanceof Attribute;
    }

    public boolean isAAttributeLink(Object handle) {
        return handle instanceof AttributeLink;
    }

    public boolean isAsynchronous(Object handle) {
        if (handle instanceof Action) {
            try {
                return ((Action) handle).isAsynchronous();
            } catch (InvalidObjectException e) {
                throw new InvalidElementException(e);
            }
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isAbstract(Object handle) {
        try {
            if (handle instanceof Operation) {
                return ((Operation) handle).isAbstract();
            }
            if (handle instanceof GeneralizableElement) {
                return ((GeneralizableElement) handle).isAbstract();
            }
            if (handle instanceof UmlAssociation) {
                return ((UmlAssociation) handle).isAbstract();
            }
            if (handle instanceof Reception) {
                return ((Reception) handle).isAbstract();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        if (isAUMLElement(handle)) {
            return false;
        } else {
            return illegalArgumentBoolean(handle);
        }
    }

    public boolean isAActivityEdge(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isAActivityGraph(Object handle) {
        return handle instanceof ActivityGraph;
    }

    public boolean isAActivityNode(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isABehavioralFeature(Object handle) {
        return handle instanceof BehavioralFeature;
    }

    public boolean isABinding(Object handle) {
        return handle instanceof Binding;
    }

    public boolean isACallAction(Object handle) {
        return handle instanceof CallAction;
    }

    public boolean isACallEvent(Object handle) {
        return handle instanceof CallEvent;
    }

    public boolean isAChangeEvent(Object handle) {
        return handle instanceof ChangeEvent;
    }

    public boolean isAClass(Object handle) {
        return handle instanceof UmlClass;
    }

    public boolean isAAssociationClass(Object handle) {
        return handle instanceof AssociationClass;
    }

    public boolean isAClassifier(Object handle) {
        return handle instanceof Classifier;
    }

    public boolean isAClassifierInState(Object handle) {
        return handle instanceof ClassifierInState;
    }

    public boolean isAClassifierRole(Object handle) {
        return handle instanceof ClassifierRole;
    }

    public boolean isAComment(Object handle) {
        return handle instanceof Comment;
    }

    public boolean isACollaboration(Object handle) {
        return handle instanceof Collaboration;
    }

    public boolean isACollaborationInstanceSet(Object handle) {
        return handle instanceof CollaborationInstanceSet;
    }

    public boolean isAComponent(Object handle) {
        return handle instanceof Component;
    }

    public boolean isAComponentInstance(Object handle) {
        return handle instanceof ComponentInstance;
    }

    public boolean isAComponentRealization(Object handle) {
        return false;
    }

    public boolean isAConnector(Object handle) {
        return false;
    }

    public boolean isAConnectorEnd(Object handle) {
        return false;
    }

    public boolean isAConstraint(Object handle) {
        return handle instanceof Constraint;
    }

    public boolean isACreateAction(Object handle) {
        return handle instanceof CreateAction;
    }

    public boolean isADataType(Object handle) {
        return handle instanceof DataType;
    }

    public boolean isADataValue(Object handle) {
        return handle instanceof DataValue;
    }

    public boolean isADependency(Object handle) {
        return handle instanceof Dependency;
    }

    public boolean isADestroyAction(Object handle) {
        return handle instanceof DestroyAction;
    }

    public boolean isACompositeState(Object handle) {
        return handle instanceof CompositeState;
    }

    public boolean isAElement(Object handle) {
        return handle instanceof Element;
    }

    public boolean isAElementImport(Object handle) {
        return handle instanceof ElementImport;
    }

    public boolean isAElementResidence(Object handle) {
        return handle instanceof ElementResidence;
    }

    public boolean isAEvent(Object handle) {
        return handle instanceof Event;
    }

    public boolean isAException(Object handle) {
        return handle instanceof UmlException;
    }

    public boolean isAExpression(Object handle) {
        return handle instanceof Expression;
    }

    public boolean isAExtend(Object handle) {
        return handle instanceof Extend;
    }

    public boolean isAExtension(Object handle) {
        throw new NotImplementedException(
        "There are no extensions in UML 1.x");
    }

    public boolean isAExtensionPoint(Object handle) {
        return handle instanceof ExtensionPoint;
    }

    public boolean isAFeature(Object handle) {
        return handle instanceof Feature;
    }

    public boolean isAFinalState(Object handle) {
        return handle instanceof FinalState;
    }

    public boolean isAFlow(Object handle) {
        return handle instanceof Flow;
    }

    public boolean isAGuard(Object handle) {
        return handle instanceof Guard;
    }

    public boolean isAGeneralizableElement(Object handle) {
        return handle instanceof GeneralizableElement;
    }

    public boolean isAGeneralization(Object handle) {
        return handle instanceof Generalization;
    }

    public boolean isAInclude(Object handle) {
        return handle instanceof Include;
    }

    public boolean isAInstance(Object handle) {
        return handle instanceof Instance;
    }

    public boolean isAInstanceSpecification(Object handle) {
        return false;
    }

    public boolean isAInteraction(Object handle) {
        return handle instanceof Interaction;
    }

    public boolean isAInteractionInstanceSet(Object handle) {
        return handle instanceof InteractionInstanceSet;
    }

    public boolean isAInterface(Object handle) {
        return handle instanceof Interface;
    }

    public boolean isALifeline(Object handle) {
        return handle instanceof ClassifierRole;
    }

    public boolean isALink(Object handle) {
        return handle instanceof Link;
    }

    public boolean isALinkEnd(Object handle) {
        return handle instanceof LinkEnd;
    }

    public boolean isALinkObject(Object handle) {
        return handle instanceof LinkObject;
    }

    public boolean isALiteralBoolean(Object handle) {
        return false;
    }

    public boolean isALiteralInteger(Object handle) {
        return false;
    }

    public boolean isALiteralString(Object handle) {
        return false;
    }

    public boolean isAMessage(Object handle) {
        return handle instanceof Message;
    }

    public boolean isAMethod(Object handle) {
        return handle instanceof Method;
    }

    public boolean isAModel(Object handle) {
        return handle instanceof Model;
    }

    public boolean isAModelElement(Object handle) {
        return handle instanceof ModelElement;
    }

    public boolean isAMultiplicity(Object handle) {
        return handle instanceof Multiplicity;
    }

    public boolean isAMultiplicityRange(Object handle) {
        return handle instanceof MultiplicityRange;
    }

    public boolean isANamedElement(Object handle) {
        return handle instanceof ModelElement;
    }

    public boolean isANamespace(Object handle) {
        return handle instanceof Namespace;
    }

    public boolean isANaryAssociation(Object handle) {
        if (handle instanceof UmlAssociation) {
            return (getConnections(handle).size() > 2);
        }
        return false;
    }

    public boolean isANode(Object handle) {
        return handle instanceof Node;
    }

    public boolean isANodeInstance(Object handle) {
        return handle instanceof NodeInstance;
    }

    public boolean isAOperation(Object handle) {
        return handle instanceof Operation;
    }

    public boolean isAOpaqueExpression(Object handle) {
        return false;
    }

    public boolean isAObject(Object handle) {
        return handle
            instanceof org.omg.uml.behavioralelements.commonbehavior.Object;
    }

    public boolean isAPackage(Object handle) {
        return handle instanceof UmlPackage;
    }

    public boolean isAPackageImport(Object handle) {
        return handle instanceof Permission;
    }

    public boolean isAParameter(Object handle) {
        return handle instanceof Parameter;
    }

    public boolean isAPartition(Object handle) {
        return handle instanceof Partition;
    }

    public boolean isAPort(Object handle) {
        return false;
    }

    public boolean isAPrimitiveType(Object handle) {
        return handle instanceof Primitive;
    }

    public boolean isAProfile(Object handle) {
        if (handle instanceof Model) {
            for (Object stereo : getStereotypes(handle)) {
                if (modelImpl.getExtensionMechanismsHelper()
                        .isStereotypeInh(stereo, "profile",
                                "Package")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAProfileApplication(Object handle) {
        throw new NotImplementedException(
        "There are no profile application objects in UML 1.x");
    }

    public boolean isAProperty(Object handle) {
        throw new NotImplementedException(
        "There are no UML Property objects in UML 1.x");
    }

    public boolean isAPseudostate(Object handle) {
        return handle instanceof Pseudostate;
    }

    public boolean isAPseudostateKind(Object handle) {
        return handle instanceof PseudostateKind;
    }


    public Object getKind(Object handle) {
        try {
            if (handle instanceof Pseudostate) {
                return ((Pseudostate) handle).getKind();
            }
            if (handle instanceof Parameter) {
                return ((Parameter) handle).getKind();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getReceiver(Object handle) {
        try {
            if (handle instanceof Stimulus) {
                return ((Stimulus) handle).getReceiver();
            }
            if (handle instanceof Message) {
                return ((Message) handle).getReceiver();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public Object getLifeline(Object handle) {
        throw new NotImplementedException("Not a UML1.4 element");
    }

    public Object getLink(Object handle) {
        try {
            if (handle instanceof LinkEnd) {
                return ((LinkEnd) handle).getLink();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public boolean equalsPseudostateKind(Object ps1, Object ps2) {
        try {
            if (isAPseudostateKind(ps1)) {
                return ((PseudostateKind) ps1).equals(ps2);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(ps1);
    }


    public boolean isAReception(Object handle) {
        return handle instanceof Reception;
    }

    public boolean isARegion(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isAReturnAction(Object handle) {
        return handle instanceof ReturnAction;
    }

    public boolean isARelationship(Object handle) {
        return handle instanceof Relationship;
    }

    public boolean isASendAction(Object handle) {
        return handle instanceof SendAction;
    }

    public boolean isASendObjectAction(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isASendSignalAction(Object handle) {
        return false; // Not in UML1.4
    }

    public boolean isASignal(Object handle) {
        return handle instanceof Signal;
    }

    public boolean isASignalEvent(Object handle) {
        return handle instanceof SignalEvent;
    }

    public boolean isASimpleState(Object handle) {
        return handle instanceof SimpleState;
    }

    public boolean isAStateMachine(Object handle) {
        return handle instanceof StateMachine;
    }

    public boolean isAStimulus(Object handle) {
        return handle instanceof Stimulus;
    }

    public boolean isAStateVertex(Object handle) {
        return handle instanceof StateVertex;
    }

    public boolean isAVertex(Object handle) {
        return false; // not in UML1.4
    }

    public boolean isAStereotype(Object handle) {
        return handle instanceof Stereotype;
    }

    public boolean isAStructuralFeature(Object handle) {
        return handle instanceof StructuralFeature;
    }

    public boolean isAState(Object handle) {
        return handle instanceof State;
    }

    public boolean isAStubState(Object handle) {
        return handle instanceof StubState;
    }

    public boolean isASubmachineState(Object handle) {
        return handle instanceof SubmachineState;
    }

    public boolean isASubsystem(Object handle) {
        return handle instanceof Subsystem;
    }

    public boolean isASubsystemInstance(Object handle) {
        return handle instanceof SubsystemInstance;
    }

    public boolean isASynchState(Object handle) {
        return handle instanceof SynchState;
    }

    public boolean isATaggedValue(Object handle) {
        return handle instanceof TaggedValue;
    }

    public boolean isATemplateArgument(Object handle) {
        return handle instanceof TemplateArgument;
    }

    public boolean isATemplateParameter(Object handle) {
        return handle instanceof TemplateParameter;
    }

    public boolean isATransition(Object handle) {
        return handle instanceof Transition;
    }

    public boolean isATrigger(Object handle) {
        return false; // not in UML1.4
    }

    public boolean isATimeEvent(Object handle) {
        return handle instanceof TimeEvent;
    }


    public boolean isAUMLElement(Object handle) {
        return handle instanceof Element
                || handle instanceof ElementImport
                || handle instanceof ElementResidence
                || handle instanceof Expression
                || handle instanceof Multiplicity
                || handle instanceof MultiplicityRange
                || handle instanceof TemplateArgument
                || handle instanceof TemplateParameter;
    }


    public boolean isAUninterpretedAction(Object handle) {
        return handle instanceof UninterpretedAction;
    }

    public boolean isAUsage(Object handle) {
        return handle instanceof Usage;
    }

    public boolean isAUseCase(Object handle) {
        return handle instanceof UseCase;
    }


    public boolean isAVisibilityKind(Object handle) {
        return handle instanceof VisibilityKind;
    }


    public boolean isActive(Object handle) {
        try {
            if (handle instanceof UmlClass) {
                return ((UmlClass) handle).isActive();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isConcurrent(Object handle) {
        try {
            if (handle instanceof CompositeState) {
                return ((CompositeState) handle).isConcurrent();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isAConcurrentRegion(Object handle) {
        try {
            if ((handle instanceof CompositeState)
                    && (getContainer(handle) != null)) {
                return (isConcurrent(getContainer(handle)));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return false;
    }


    public boolean isConstructor(Object handle) {
        try {
            Operation operation = null;
            if (handle instanceof Method) {
                operation = ((Method) handle).getSpecification();
                if (operation == null) {
                    // This is not a well formed model in a strict sense.
                    // See the multiplicity in UML 1.3 Figure 2-5.
                    return false;
                }
            } else if (handle instanceof Operation) {
                operation = (Operation) handle;
            } else {
                return illegalArgumentBoolean(handle);
            }

            for (Object stereo : getStereotypes(operation)) {
                if (modelImpl.getExtensionMechanismsHelper()
                        .isStereotypeInh(stereo, "create",
                                "BehavioralFeature")) {
                    return true;
                }
            }
            return false;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isFrozen(Object handle) {
        try {
            if (handle instanceof ChangeableKind) {
                ChangeableKind ck = (ChangeableKind) handle;
                return ChangeableKindEnum.CK_FROZEN.equals(ck);
            }
            if (handle instanceof AssociationEnd) {
                return ChangeableKindEnum.CK_FROZEN
                        .equals(((AssociationEnd) handle).getChangeability());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isComposite(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return AggregationKindEnum.AK_COMPOSITE
                .equals(((AssociationEnd) handle).getAggregation());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isAggregate(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return AggregationKindEnum.AK_AGGREGATE
                        .equals(((AssociationEnd) handle).getAggregation());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isInitialized(Object handle) {
        try {
            if (handle instanceof Attribute) {
                Expression init = ((Attribute) handle).getInitialValue();

                if (init != null && init.getBody() != null
                        && init.getBody().trim().length() > 0) {
                    return true;
                }
                return false;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isInternal(Object handle) {
        try {
            if (handle instanceof Transition) {
                Object state = getState(handle);
                Object end0 = getSource(handle);
                Object end1 = getTarget(handle);
                if (end0 != null) {
                    return ((state == end0) && (state == end1));
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isLeaf(Object handle) {
        try {
            if (handle instanceof GeneralizableElement) {
                return ((GeneralizableElement) handle).isLeaf();
            }
            if (handle instanceof Operation) {
                return ((Operation) handle).isLeaf();
            }
            if (handle instanceof Reception) {
                return ((Reception) handle).isLeaf();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        if (isAUMLElement(handle)) {
            return false;
        } else {
            return illegalArgumentBoolean(handle);
        }
    }


    public boolean isRoot(Object handle) {
        try {
            if (handle instanceof GeneralizableElement) {
                return ((GeneralizableElement) handle).isRoot();
            }
            if (handle instanceof Operation) {
                return ((Operation) handle).isRoot();
            }
            if (handle instanceof Reception) {
                return ((Reception) handle).isRoot();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        if (isAUMLElement(handle)) {
            return false;
        } else {
            return illegalArgumentBoolean(handle);
        }
    }


    public boolean isSpecification(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).isSpecification();
            }
            if (handle instanceof ElementImport) {
                return ((ElementImport) handle).isSpecification();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }

    public boolean isNavigable(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).isNavigable();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }

    public boolean isOrthogonal(Object handle) {
        throw new NotImplementedException(
                "Orthogonal is not a UML1.4 property");
    }

    public boolean isPrimaryObject(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                for (TaggedValue tv
                        : ((ModelElement) handle).getTaggedValue()) {
                    TagDefinition type = tv.getType();
                    if (type != null
                            && GENERATED_TAG.equals(type.getTagType())) {
                        return false;
                    }
                }
                return true;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isPackage(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                ModelElement element = (ModelElement) handle;
                return VisibilityKindEnum.VK_PACKAGE.equals(
                        element.getVisibility());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public boolean isPrivate(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                ModelElement elem = (ModelElement) handle;
                return VisibilityKindEnum.VK_PRIVATE
                    .equals(elem.getVisibility());
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isPublic(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                ModelElement elem = (ModelElement) handle;
                return VisibilityKindEnum.VK_PUBLIC
                    .equals(elem.getVisibility());
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isQuery(Object handle) {
        try {
            if (handle instanceof BehavioralFeature) {
                return ((BehavioralFeature) handle).isQuery();
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isProtected(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                ModelElement elem = (ModelElement) handle;
                return VisibilityKindEnum.VK_PROTECTED
                    .equals(elem.getVisibility());
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isRealize(Object handle) {
        return isStereotype(handle, CoreFactory.REALIZE_STEREOTYPE);
    }


    public boolean isReturn(Object handle) {
        return hasReturnParameterDirectionKind(handle);
    }


    public boolean isSingleton(Object handle) {
        return isStereotype(handle, "singleton");
    }


    public boolean isStereotype(Object handle, String stereotypeName) {
        try {
            if (handle instanceof ModelElement) {
                Collection stereotypes =
                    ((ModelElement) handle).getStereotype();
                Iterator it = stereotypes.iterator();
                Stereotype stereotype;
                while (it.hasNext()) {
                    stereotype = (Stereotype) it.next();
                    if (stereotypeName.equals(stereotype.getName())) {
                        return true;
                    }
                }
                return false;
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isATerminateAction(Object handle) {
        return handle instanceof TerminateAction;
    }


    public boolean isTop(Object handle) {
        try {
            if (isACompositeState(handle)) {
                return ((CompositeState) handle).getStateMachine() != null;
            }
            return illegalArgumentBoolean(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isType(Object handle) {
        return isStereotype(handle, "type");
    }

    public boolean isUtility(Object handle) {
        return isStereotype(handle, "utility");
    }


    public Object getAssociation(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getAssociation();
            }
            if (handle instanceof Link) {
                return ((Link) handle).getAssociation();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getAssociationEnd(Object handle, Object assoc) {
        try {
            if (handle instanceof Classifier
                    && assoc instanceof UmlAssociation) {
                Classifier classifier = (Classifier) handle;
                Iterator it = getAssociationEnds(classifier).iterator();
                while (it.hasNext()) {
                    AssociationEnd end = (AssociationEnd) it.next();
                    if (((UmlAssociation) assoc).getConnection()
                            .contains(end)) {
                        return end;
                    }
                }
                return null;
            }
            throw new IllegalArgumentException("handle: " + handle + ",assoc: "
                    + assoc);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    /**
     * Helper method to get a reference to the outermost package.
     *
     * @param handle the handle.
     * @return The outermost package.
     */
    private org.omg.uml.UmlPackage getRefOutermostPackage(Object handle) {
        final RefPackage refPackage =
            ((ModelElement) handle).refOutermostPackage();
        return (org.omg.uml.UmlPackage) refPackage;
    }

    public Collection getAssociations(Object handle) {
        throw new NotImplementedException("Not available for UML1.4");
    }

    public Collection getAssociationEnds(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCore()
                        .getAParticipantAssociation().getAssociation(
                                (Classifier) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Collection getAssociationRoles(Object handle) {
        try {
            if (handle instanceof UmlAssociation) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getABaseAssociationRole().getAssociationRole(
                                (UmlAssociation) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public List getAttributes(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getStructuralFeatures(handle);
            }
            return illegalArgumentList(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<String> getBaseClasses(Object handle) {
        try {
            if (isAStereotype(handle)) {
                return ((Stereotype) handle).getBaseClass();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getBase(Object handle) {
        try {
            if (handle instanceof AssociationEndRole) {
                return ((AssociationEndRole) handle).getBase();
            } else if (handle instanceof AssociationRole) {
                return ((AssociationRole) handle).getBase();
            } else if (handle instanceof Extend) {
                return ((Extend) handle).getBase();
            } else if (handle instanceof Include) {
                return ((Include) handle).getBase();
            } else if (handle instanceof ClassifierRole) {
                // TODO: this returns a Collection, not a single Object
                // Is this what the callers expect?
                return ((ClassifierRole) handle).getBase();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<Classifier> getBases(Object handle) {
        try {
            if (handle instanceof ClassifierRole) {
                return ((ClassifierRole) handle).getBase();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getBehaviors(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return getRefOutermostPackage(handle)
                        .getStateMachines().getABehaviorContext().getBehavior(
                                (ModelElement) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getBehavioralFeature(Object handle) {
        try {
            if (handle instanceof Parameter) {
                return ((Parameter) handle).getBehavioralFeature();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getBody(Object handle) {
        try {
            if (handle instanceof Comment) {
                // Text was stored in name in UML 1.3
                return ((Comment) handle).getBody();
            }
            if (handle instanceof Constraint) {
                return ((Constraint) handle).getBody();
            }
            if (handle instanceof Expression) {
                return ((Expression) handle).getBody();
            }
            if (handle instanceof Method) {
                return ((Method) handle).getBody();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public int getBound(Object handle) {
        try {
            if (handle instanceof SynchState) {
                return ((SynchState) handle).getBound();
            }
            return illegalArgumentInt(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    @Deprecated
    public Object getChangeability(Object handle) {
        try {
            if (handle instanceof StructuralFeature) {
                return ((StructuralFeature) handle).getChangeability();
            }
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getChangeability();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isReadOnly(Object handle) {
        ChangeableKind ck;
        try {
            if (handle instanceof StructuralFeature) {
                ck = ((StructuralFeature) handle).getChangeability();
            } else if (handle instanceof AssociationEnd) {
                ck = ((AssociationEnd) handle).getChangeability();
            } else {
                return illegalArgumentBoolean(handle);
            }
            return ChangeableKindEnum.CK_FROZEN.equals(ck);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Object getSpecific(Object handle) {
        try {
            if (handle instanceof Generalization) {
                return ((Generalization) handle).getChild();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<GeneralizableElement> getChildren(Object handle) {
        try {
            return modelImpl.getCoreHelper().getChildren(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getClassifierRoles(Object handle) {
        try {
            if (handle instanceof Feature) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAClassifierRoleAvailableFeature()
                        .getClassifierRole((Feature) handle);
            }
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAClassifierRoleBase().getClassifierRole(
                                (Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getClassifier(Object handle) {
        try {
            if (isAAssociationEnd(handle)) {
                return ((AssociationEnd) handle).getParticipant();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<Classifier> getClassifiers(Object handle) {
        try {
            if (handle instanceof Instance) {
                return ((Instance) handle).getClassifier();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getClassifiersInState(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getActivityGraphs()
                        .getATypeClassifierInState().getClassifierInState(
                                (Classifier) handle);
            }
            if (handle instanceof State) {
                return getRefOutermostPackage(handle).getActivityGraphs()
                        .getAClassifierInStateInState().getClassifierInState(
                                (State) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<ModelElement> getClients(Object handle) {
        try {
            if (isADependency(handle)) {
                return ((Dependency) handle).getClient();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<Dependency> getClientDependencies(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                final ModelElement me = (ModelElement) handle;
                return Collections.unmodifiableCollection(
                        me.getClientDependency());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }


    public Object getCondition(Object handle) {
        try {
            if (handle instanceof Extend) {
                return ((Extend) handle).getCondition();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getConcurrency(Object handle) {
        try {
            if (handle instanceof Operation) {
                return ((Operation) handle).getConcurrency();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getConnections(Object handle) {
        try {
            if (handle instanceof UmlAssociation) {
                // returns List
                return ((UmlAssociation) handle).getConnection();
            }
            if (handle instanceof Link) {
                return ((Link) handle).getConnection();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getEffect(Object handle) {
        try {
            if (handle instanceof Transition) {
                return ((Transition) handle).getEffect();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getElementResidences(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return getRefOutermostPackage(handle)
                        .getCore().getAResidentElementResidence()
                        .getElementResidence((ModelElement) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getElementImports2(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return getRefOutermostPackage(handle)
                        .getModelManagement()
                        .getAImportedElementElementImport().getElementImport(
                                (ModelElement) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Collection<ElementImport> getElementImports(Object handle) {
        try {
            if (handle instanceof UmlPackage) {
                return ((UmlPackage) handle).getElementImport();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Object getEntry(Object handle) {
        try {
            if (handle instanceof State) {
                try {
                    return ((State) handle).getEntry();
                } catch (InvalidObjectException e) {
                    throw new InvalidElementException(e);
                }
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getEnumeration(Object handle) {
        try {
            if (handle instanceof EnumerationLiteral) {
                return ((EnumerationLiteral) handle).getEnumeration();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getExit(Object handle) {
        try {
            if (handle instanceof State) {
                return ((State) handle).getExit();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getExpression(Object handle) {
        try {
            if (handle instanceof Guard) {
                return ((Guard) handle).getExpression();
            }
            if (handle instanceof ChangeEvent) {
                return ((ChangeEvent) handle).getChangeExpression();
            }
            if (handle instanceof TimeEvent) {
                return ((TimeEvent) handle).getWhen();
            }
            if (handle instanceof Argument) {
                return ((Argument) handle).getValue();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getExtendedElements(Object handle) {
        try {
            if (!(handle instanceof Stereotype)) {
                return illegalArgumentCollection(handle);
            }
            Stereotype stereotype = (Stereotype) handle;
            return getRefOutermostPackage(handle)
                    .getCore().getAStereotypeExtendedElement()
                    .getExtendedElement(stereotype);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getExtends(Object handle) {
        try {
            if (handle instanceof UseCase) {
                return ((UseCase) handle).getExtend();
            }
            if (handle instanceof ExtensionPoint) {
                ExtensionPoint ep = (ExtensionPoint) handle;
                return getRefOutermostPackage(handle)
                        .getUseCases().getAExtensionPointExtend().getExtend(ep);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getExtenders(Object handle) {
        try {
            if (handle instanceof UseCase) {
                return getRefOutermostPackage(handle).getUseCases()
                        .getABaseExtender().getExtender((UseCase) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getExtension(Object handle) {
        try {
            if (handle instanceof Extend) {
                return ((Extend) handle).getExtension();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getExtensionPoint(Object handle, int index) {
        try {
            if (handle instanceof Extend) {
                return ((Extend) handle).getExtensionPoint().get(index);
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<ExtensionPoint> getExtensionPoints(Object handle) {
        try {
            if (handle instanceof UseCase) {
                return ((UseCase) handle).getExtensionPoint();
            }
            if (handle instanceof Extend) {
                // returns a List
                return ((Extend) handle).getExtensionPoint();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public List<Feature> getFeatures(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return ((Classifier) handle).getFeature();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public Object getGeneralization(Object handle, Object parent) {
        try {
            if (handle instanceof GeneralizableElement
                    && parent instanceof GeneralizableElement) {
                Iterator it = getGeneralizations(handle).iterator();
                while (it.hasNext()) {
                    Generalization gen = (Generalization) it.next();
                    if (gen.getParent() == parent) {
                        return gen;
                    }
                }
                return null;
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<Generalization> getGeneralizations(Object handle) {
        try {
            if (handle instanceof GeneralizableElement) {
                return ((GeneralizableElement) handle).getGeneralization();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getGuard(Object handle) {
        try {
            if (isATransition(handle)) {
                return ((Transition) handle).getGuard();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getIcon(Object handle) {
        try {
            if (handle instanceof Stereotype) {
                return ((Stereotype) handle).getIcon();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<ModelElement> getImportedElements(Object pack) {
        if (!(pack instanceof UmlPackage)) {
            return illegalArgumentCollection(pack);
        }
        Collection<ModelElement> results = new ArrayList<ModelElement>();
        try {
            /*
             * TODO: This code manually processes the ElementImports of a
             * Package, but we need to check whether MDR already does something
             * similar automatically as part of its namespace processing.
             * - tfm - 20060408
             */
            for (ElementImport ei : ((UmlPackage) pack).getElementImport()) {
                ModelElement element = ei.getImportedElement();
                results.add(element);
            }
            return results;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getImportedElement(Object elementImport) {
        if (!(elementImport instanceof ElementImport)) {
            return illegalArgumentCollection("This is not an ElementImport: "
                    + elementImport);
        }
        try {
            return ((ElementImport) elementImport).getImportedElement();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Collection<Include> getIncludes(Object handle) {
        try {
            if (handle instanceof UseCase) {
                return ((UseCase) handle).getInclude();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    public Collection getIncluders(Object handle) {
        try {
            if (handle instanceof UseCase) {
                return getRefOutermostPackage(handle).getUseCases()
                        .getAIncluderAddition().getIncluder((UseCase) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getIncomings(Object handle) {
        try {
            if (isAGuard(handle) || isAAction(handle)) {
                return getIncomings(getTransition(handle));
            }
            if (isAEvent(handle)) {
                Iterator trans = getTransitions(handle).iterator();
                Collection incomings = new ArrayList();
                while (trans.hasNext()) {
                    incomings.addAll(getIncomings(trans.next()));
                }
                return incomings;
            }
            if (isAStateVertex(handle)) {
                return ((StateVertex) handle).getIncoming();
            }
            // For a Transition use indirection through source StateVertex
            if (isATransition(handle)) {
                return ((Transition) handle).getSource().getIncoming();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getInitialValue(Object handle) {
        try {
            if (handle instanceof Attribute) {
                return ((Attribute) handle).getInitialValue();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getInstance(Object handle) {
        try {
            if (handle instanceof AttributeLink) {
                return ((AttributeLink) handle).getInstance();
            }
            if (handle instanceof LinkEnd) {
                return ((LinkEnd) handle).getInstance();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getInstances(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAInstanceClassifier().getInstance(
                                (Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<State> getInStates(Object handle) {
        try {
            if (handle instanceof ClassifierInState) {
                return ((ClassifierInState) handle).getInState();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getInteraction(Object handle) {
        try {
            if (handle instanceof Message) {
                return ((Message) handle).getInteraction();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<Interaction> getInteractions(Object handle) {
        try {
            if (handle instanceof Collaboration) {
                return ((Collaboration) handle).getInteraction();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<Transition> getInternalTransitions(Object handle) {
        try {
            if (handle instanceof State) {
                return ((State) handle).getInternalTransition();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<Message> getMessages(Object handle) {
        try {
            if (isAInteraction(handle)) {
                return ((Interaction) handle).getMessage();
            }
            if (handle instanceof AssociationRole) {
                return ((AssociationRole) handle).getMessage();
            }
            if (handle instanceof Action) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAActionMessage().getMessage(((Action) handle));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getMessageSort(java.lang.Object)
     */
    public Object getMessageSort(Object handle) {
        return getAction(handle);
    }



    public Collection getSuccessors(Object handle) {
        try {
            if (handle instanceof Message) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAPredecessorSuccessor().getSuccessor(
                                (Message) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Collection getActivatedMessages(Object handle) {
        try {
            if (handle instanceof Message) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAMessageActivator().getMessage((Message) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    public Collection getReceivedMessages(Object handle) {
        try {
            if (handle instanceof ClassifierRole) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAReceiverMessage().getMessage(
                                (ClassifierRole) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    public Collection getSentMessages(Object handle) {
        try {
            if (handle instanceof ClassifierRole) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAMessageSender()
                        .getMessage((ClassifierRole) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    public Object getInnerContainingModel(Object handle) {
        try {
            if (isAModel(handle)) {
                return handle;
            }
            if (!isAUMLElement(handle)) {
                return illegalArgumentObject(handle);
            }
            // If we can't find a model, return the outermost
            // containing model element
            if (getModelElementContainer(handle) == null) {
                return handle;
            }
            // TODO: replace this recursive function by a loop like in getRoot:
            return getInnerContainingModel(
                getModelElementContainer(handle));
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Object getRoot(final Object handle) {
        Object result = handle;
        try {
            if (!isAUMLElement(handle)) {
                return illegalArgumentObject(handle);
            }
            Object container = getModelElementContainer(handle);
            while (container != null) {
                result = container;
                container = getModelElementContainer(result);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }

    public Collection getRootElements() {
        Collection elements = new ArrayList();
        org.omg.uml.UmlPackage pkg = modelImpl.getUmlPackage();
        if (pkg != null) {
            for (RefObject obj : (Collection<RefObject>) pkg.getCore()
                    .getElement().refAllOfType()) {
                if (obj.refImmediateComposite() == null) {
                    elements.add(obj);
                }
            }
        } else {
            LOG.log(Level.WARNING, "No default extent in getRootElements");
        }
        return elements;
    }


    public Object getModelElement(Object handle) {
        try {
            if (handle instanceof ElementImport) {
                // TODO: This does not belong here - use getImportedElement.
                return ((ElementImport) handle).getImportedElement();
            }
            if (handle instanceof TaggedValue) {
                return ((TaggedValue) handle).getModelElement();
            }
            if (handle instanceof TemplateArgument) {
                return ((TemplateArgument) handle).getModelElement();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getMultiplicity(Object handle) {
        try {
            if (handle instanceof StructuralFeature) {
                StructuralFeature sf = (StructuralFeature) handle;
                return sf.getMultiplicity();
            } else if (handle instanceof TagDefinition) {
                TagDefinition td = (TagDefinition) handle;
                return td.getMultiplicity();
            } else if (handle instanceof ClassifierRole) {
                ClassifierRole cr = (ClassifierRole) handle;
                return cr.getMultiplicity();
            } else if (handle instanceof AssociationEnd) {
                AssociationEnd ae = (AssociationEnd) handle;
                return ae.getMultiplicity();
            } else if (handle instanceof AssociationRole) {
                AssociationRole ar = (AssociationRole) handle;
                return ar.getMultiplicity();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public Collection<Comment> getComments(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getComment();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<ModelElement> getAnnotatedElements(Object handle) {
        try {
            if (handle instanceof Comment) {
                return ((Comment) handle).getAnnotatedElement();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getCommunicationConnection(Object handle) {
        try {
            if (handle instanceof Message) {
                return ((Message) handle).getCommunicationConnection();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getCommunicationLink(Object handle) {
        try {
            if (handle instanceof Stimulus) {
                return ((Stimulus) handle).getCommunicationLink();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getCollaborations(Object handle) {
        try {
            if (handle instanceof Operation) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getARepresentedOperationCollaboration()
                        .getCollaboration((Operation) handle);
            }
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getARepresentedClassifierCollaboration()
                        .getCollaboration((Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getComponentInstance(Object handle) {
        try {
            if (handle instanceof Instance) {
                return ((Instance) handle).getComponentInstance();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<ModelElement> getConstrainingElements(Object handle) {
        try {
            if (handle instanceof Collaboration) {
                return ((Collaboration) handle).getConstrainingElement();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public List<ModelElement> getConstrainedElements(Object handle) {
        try {
            if (handle instanceof Constraint) {
                return ((Constraint) handle).getConstrainedElement();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public Collection<Constraint> getConstraints(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getConstraint();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getModelElementContainer(Object handle) {
        try {
            if (handle instanceof RefObject) {
                return ((RefObject) handle).refImmediateComposite();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public List getModelElementContents(Object handle) {
        List results = getModelElementAssociated(handle, true);
        return results;
    }


    public List getModelElementAssociated(Object handle) {
        return getModelElementAssociated(handle, false);
    }

    /**
     * Return all model elements associated with the given model element,
     * optionally excluding associations which are not composite.<p>
     *
     * NOTE: The current implementation only follows associations which
     * are navigable FROM the model element, so it's possible that there
     * are additional associated elements which would be too resource
     * intensive to search for.<p>
     *
     * Implementation queries the metamodel to find all attributes/references
     * for the metatype of the given object and uses that set of names
     * to query the object using the JMI reflective interface.
     *
     * @return A List with {@link RefBaseObject}s.
     */
    private List getModelElementAssociated(Object handle,
            boolean contentsOnly) {
        List<RefBaseObject> results = new ArrayList<RefBaseObject>();

        if (!(handle instanceof RefFeatured)) {
            throw new IllegalArgumentException(
                    "Element doesn't appear to be a MOF element :" + handle);
        }

        try {
            modelImpl.getRepository().beginTrans(false);
            RefFeatured rf = (RefFeatured) handle;
            RefObject metaobject = rf.refMetaObject();
            if (!(metaobject instanceof MofClass)) {
                throw new IllegalArgumentException(
                        "Element doesn't appear to be a MOF element :"
                        + handle);
            }
            MofClass metaclass = (MofClass) metaobject;
            List<MofClass> types =
                    new ArrayList<MofClass>(metaclass.allSupertypes());
            types.add(metaclass);
            for (MofClass s : types) {
                for (Object contentElement : s.getContents()) {
                    getReferenceOrAttribute(
                            (RefFeatured) handle, contentElement, results,
                            contentsOnly);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        } finally {
            modelImpl.getRepository().endTrans();
        }
        return results;
    }

    /**
     * Helper function to add the value(s) of an Attribute or Reference to the
     * given collection.
     *
     * @param returns A Collection that {@link RefBaseObject}s are added to.
     */
    private void getReferenceOrAttribute(RefFeatured parent, Object element,
            Collection<RefBaseObject> returns, boolean contentsOnly) {

        try {
            if (!(element instanceof javax.jmi.model.Attribute
                    || element instanceof Reference)) {
                return;
            }
            if (element instanceof Reference) {
                javax.jmi.model.AggregationKind ak = ((Reference) element)
                        .getExposedEnd().getAggregation();
                if (contentsOnly && ak != MOF_COMPOSITE) {
                    return;
                }
            }

            String valueName = ((javax.jmi.model.ModelElement) element)
                    .getName();
            Object refends = parent.refGetValue(valueName);
            if (refends == null) {
                return;
            }
            if (refends instanceof Collection) {
                Collection c = (Collection) refends;
                if (!c.isEmpty()) {
                    for (Iterator it = c.iterator(); it.hasNext();) {
                        Object o = it.next();
                        // Only add MOF elements, not primitive datatypes
                        if (o instanceof RefBaseObject) {
                            returns.add((RefBaseObject) o);
                        }
                    }
                }
            } else {
                // Only add MOF elements, not primitive datatypes
                if (refends instanceof RefBaseObject) {
                    returns.add((RefBaseObject) refends);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    // TODO: Rationalize this with getModelElementContainer
    public Object getContainer(Object handle) {
        try {
            if (isAGuard(handle) || isAAction(handle)) {
                return getContainer(getTransition(handle));
            }
            if (isAEvent(handle)) {
                Object container;
                Iterator it = getTransitions(handle).iterator();
                while (it.hasNext()) {
                    container = getContainer(it.next());
                    if (container != null) {
                        return container;
                    }
                }
            }
            if (handle instanceof StateVertex) {
                return ((StateVertex) handle).getContainer();
            }
            // TODO: Use getModelElementContainer for transition
            if (isATransition(handle)) {
                return ((Transition) handle).getStateMachine().getTop()
                        .getContainer();
            }
            if (handle instanceof ElementResidence) {
                return ((ElementResidence) handle).getContainer();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<ModelElement> getContents(Object handle) {
        try {
            if (handle instanceof Partition) {
                return ((Partition) handle).getContents();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getContext(Object handle) {
        try {
            if (isAStateMachine(handle)) {
                return ((StateMachine) handle).getContext();
            }
            if (isAInteraction(handle)) {
                return ((Interaction) handle).getContext();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getContexts(Object handle) {
        try {
            if (handle instanceof Signal) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAContextRaisedSignal().getContext((Signal) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getCreateActions(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getACreateActionInstantiation().getCreateAction(
                                (Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getDefaultValue(Object handle) {
        try {
            if (handle instanceof Parameter) {
                return ((Parameter) handle).getDefaultValue();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<Event> getDeferrableEvents(Object handle) {
        try {
            if (handle instanceof State) {
                return ((State) handle).getDeferrableEvent();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<Node> getDeploymentLocations(Object handle) {
        try {
            if (handle instanceof Component) {
                return ((Component) handle).getDeploymentLocation();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }

    public Collection<Component> getDeployedComponents(Object handle) {
        try {
            if (handle instanceof Node) {
                return ((Node) handle).getDeployedComponent();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    @Deprecated
    public Object getDiscriminator(Object handle) {
        try {
            if (handle instanceof Generalization) {
                return ((Generalization) handle).getDiscriminator();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("Expected a Generalization. Got a "
                + handle.getClass().getName());
    }


    public Object getDispatchAction(Object handle) {
        try {
            if (handle instanceof Stimulus) {
                return ((Stimulus) handle).getDispatchAction();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getDoActivity(Object handle) {
        try {
            if (handle instanceof State) {
                return ((State) handle).getDoActivity();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getLinks(Object handle) {
        try {
            if (handle instanceof UmlAssociation) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAAssociationLink().getLink((UmlAssociation) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getLinkEnds(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAAssociationEndLinkEnd().getLinkEnd(
                                (AssociationEnd) handle);
            }
            if (handle instanceof Instance) {
                return ((Instance) handle).getLinkEnd();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public String getLocation(Object handle) {
        try {
            if (handle instanceof ExtensionPoint) {
                return ((ExtensionPoint) handle).getLocation();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentString(handle);
    }


    public Collection getMethods(Object handle) {
        try {
            if (handle instanceof Operation) {
                return getRefOutermostPackage(handle).getCore()
                        .getASpecificationMethod()
                        .getMethod((Operation) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getNamespace(Object handle) {
        if (!(handle instanceof ModelElement)) {
            return illegalArgumentObject(handle);
        }
        try {
            return ((ModelElement) handle).getNamespace();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getNodeInstance(Object handle) {
        try {
            if (handle instanceof ComponentInstance) {
                return ((ComponentInstance) handle).getNodeInstance();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getObjectFlowStates(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getActivityGraphs()
                        .getATypeObjectFlowState().getObjectFlowState(
                                (Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getOperation(Object handle) {
        try {
            if (handle instanceof CallAction) {
                return ((CallAction) handle).getOperation();
            }
            if (handle instanceof CallEvent) {
                return ((CallEvent) handle).getOperation();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getOccurrences(Object handle) {
        try {
            if (handle instanceof Operation) {
                return getRefOutermostPackage(handle).getStateMachines()
                        .getAOccurrenceOperation().getOccurrence(
                                (Operation) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public List<Operation> getOperations(Object handle) {
        if (!(handle instanceof Classifier)) {
            return illegalArgumentList(handle);
        }
        List<Operation> result = new ArrayList<Operation>();
        try {
            for (Feature feature : getFeatures(handle)) {
                if (feature instanceof Operation) {
                    result.add((Operation) feature);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }

        return result;
    }

    public List<Feature> getOperationsAndReceptions(Object classifier) {
        List<Feature> opsAndReceps = new ArrayList<Feature>();
        // TODO: Return empty collection on null input or throw IllegalArgument?
        if (classifier != null) {
            for (Feature f : getFeatures(classifier)) {
                if (isAOperation(f) || isAReception(f)) {
                    opsAndReceps.add(f);
                }
            }
        }
        return opsAndReceps;
    }


    public Object getNextEnd(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                List<AssociationEnd> assocEnds =
                        (((AssociationEnd) handle).getAssociation())
                                .getConnection();
                int index = assocEnds.indexOf(handle);
                if (index == -1 || assocEnds.size() < 2) {
                    return null; // shouldn't happen
                }
                if (index == assocEnds.size() - 1) {
                    return assocEnds.get(0);
                } else {
                    return assocEnds.get(index + 1);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getOrdering(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getOrdering();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getOutgoings(Object handle) {
        try {
            if (isAGuard(handle) || isAAction(handle)) {
                return getOutgoings(getTransition(handle));
            }
            if (isAEvent(handle)) {
                Iterator trans = getTransitions(handle).iterator();
                Collection outgoings = new ArrayList();
                while (trans.hasNext()) {
                    outgoings.addAll(getOutgoings(trans.next()));
                }
                return outgoings;
            }
            if (isAStateVertex(handle)) {
                return ((StateVertex) handle).getOutgoing();
            }
            // For Transition use indirection through target StateVertex
            if (isATransition(handle)) {
                return ((Transition) handle).getTarget().getOutgoing();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<AssociationEnd> getOtherAssociationEnds(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                UmlAssociation a = ((AssociationEnd) handle).getAssociation();

                if (a == null) {
                    return Collections.emptyList();
                }

                Collection<AssociationEnd> allEnds = a.getConnection();
                if (allEnds == null) {
                    return Collections.emptyList();
                }

                // TODO: An Iterator filter would be nice here instead of the
                // mucking around with the Collection.
                allEnds = new ArrayList<AssociationEnd>(allEnds);
                allEnds.remove(handle);
                return allEnds;
            }

        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<LinkEnd> getOtherLinkEnds(Object handle) {
        try {
            if (handle instanceof LinkEnd) {
                Link link = ((LinkEnd) handle).getLink();

                if (link == null) {
                    return Collections.emptyList();
                }

                Collection<LinkEnd> allEnds = link.getConnection();
                if (allEnds == null) {
                    return Collections.emptyList();
                }

                // TODO: An Iterator filter would be nice here instead of the
                // mucking around with the Collection.
                allEnds = new ArrayList<LinkEnd>(allEnds);
                allEnds.remove(handle);
                return allEnds;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<ModelElement> getOwnedElements(Object handle) {
        if (!(handle instanceof Namespace)) {
            return illegalArgumentCollection(handle);
        }
        try {
            return ((Namespace) handle).getOwnedElement();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public boolean isStatic(Object handle) {
        try {
            if (handle instanceof Feature) {
                return ScopeKindEnum.SK_CLASSIFIER.equals(((Feature) handle)
                        .getOwnerScope());
            }
            if (handle instanceof AssociationEnd) {
                return ScopeKindEnum.SK_CLASSIFIER
                        .equals(((AssociationEnd) handle).getTargetScope());
            }
            throw new IllegalArgumentException();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getPowertype(Object handle) {
        try {
            if (handle instanceof Generalization) {
                return ((Generalization) handle).getPowertype();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<Generalization> getPowertypeRanges(Object handle) {
        try {
            if (handle instanceof Classifier) {
                return ((Classifier) handle).getPowertypeRange();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection<Message> getPredecessors(Object handle) {
        try {
            if (handle instanceof Message) {
            // TODO: This has different semantics than everything else
                return Collections.unmodifiableCollection(
                        new ArrayList<Message>(
                                ((Message) handle).getPredecessor()));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public List<Attribute> getQualifiers(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getQualifier();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public boolean hasReturnParameterDirectionKind(Object handle) {
        try {
            if (handle instanceof Parameter) {
                Parameter parameter = (Parameter) handle;
                return (ParameterDirectionKindEnum.PDK_RETURN.equals(parameter
                        .getKind()));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentBoolean(handle);
    }


    public Object getPackage(Object handle) {
        try {
            if (handle instanceof ElementImport) {
                return ((ElementImport) handle).getUmlPackage();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getParameter(Object handle, int n) {
        Collection collection = getParameters(handle);
        if (collection instanceof List) {
            return ((List) collection).get(n);
        }
        return illegalArgumentObject(handle);
    }


    public Object getParameter(Object handle) {
        if (handle instanceof TemplateParameter) {
            return ((TemplateParameter) handle).getParameter();
        }
        return illegalArgumentObject(handle);
    }


    public Collection getParameters(Object handle) {
        try {
            if (handle instanceof BehavioralFeature
                    || handle instanceof Event) {
                return getParametersList(handle);
            }
            if (handle instanceof ObjectFlowState) {
                return ((ObjectFlowState) handle).getParameter();
            }
            if (handle instanceof Classifier) {
                return getRefOutermostPackage(handle).getCore()
                        .getATypedParameterType().getTypedParameter(
                                (Classifier) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public List<Parameter> getParametersList(Object handle) {
        try {
            if (handle instanceof BehavioralFeature) {
                return ((BehavioralFeature) handle).getParameter();
            }
            if (handle instanceof Event) {
                return ((Event) handle).getParameter();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public Object getGeneral(Object handle) {
        try {
            if (handle instanceof Generalization) {
                return ((Generalization) handle).getParent();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getRaisedSignals(Object handle) {
        return getRaisedExceptions(handle);
    }

    /**
     * Dummy method for UML 1.x
     *
     * @author Andreas Rueckert <a_rueckert@gmx.net>
     *
     * @see org.argouml.model.Facade#getRaisedExceptions(java.lang.Object)
     */
    public Collection getRaisedExceptions(Object handle) {
        try {
            if (handle instanceof BehavioralFeature) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAContextRaisedSignal().getRaisedSignal(
                                (BehavioralFeature) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Collection getReceptions(Object handle) {
        try {
            if (handle instanceof Signal) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getASignalReception().getReception((Signal) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getRecurrence(Object handle) {
        try {
            if (handle instanceof Action) {
                return ((Action) handle).getRecurrence();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getRepresentedClassifier(Object handle) {
        try {
            if (handle instanceof Collaboration) {
                return ((Collaboration) handle).getRepresentedClassifier();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getRepresentedOperation(Object handle) {
        try {
            if (handle instanceof Collaboration) {
                return ((Collaboration) handle).getRepresentedOperation();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getScript(Object handle) {
        try {
            if (handle instanceof Action) {
                return ((Action) handle).getScript();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getSender(Object handle) {
        try {
            if (handle instanceof Stimulus) {
                return ((Stimulus) handle).getSender();
            }
            if (handle instanceof Message) {
                return ((Message) handle).getSender();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getSignal(Object handle) {
        try {
            if (handle instanceof SendAction) {
                return ((SendAction) handle).getSignal();
            }
            if (handle instanceof SignalEvent) {
                return ((SignalEvent) handle).getSignal();
            }
            if (handle instanceof Reception) {
                return ((Reception) handle).getSignal();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getResident(Object handle) {
        try {
            if (handle instanceof ElementResidence) {
                return ((ElementResidence) handle).getResident();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<ElementResidence> getResidentElements(Object handle) {
        try {
            if (handle instanceof Component) {
                return ((Component) handle).getResidentElement();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getResidents(Object handle) {
        try {
            if (isANodeInstance(handle)) {
                return ((NodeInstance) handle).getResident();
            }
            if (isAComponentInstance(handle)) {
                return ((ComponentInstance) handle).getResident();
            }
            if (isAComponent(handle)) {
                Collection residents = new ArrayList();
                for (Iterator it =
                        ((Component) handle).getResidentElement().iterator();
                    it.hasNext();) {
                    ModelElement me = ((ElementResidence) it.next()).getResident();
                    if (me != null) {
                        residents.add(me);
                    }
                }
                return residents;
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getSource(Object handle) {
        try {
            if (isATransition(handle)) {
                return ((Transition) handle).getSource();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection<ModelElement> getSources(Object handle) {
        try {
            if (handle instanceof Flow) {
                return ((Flow) handle).getSource();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSourceFlows(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getSourceFlow();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSpecializations(Object handle) {
        if (!(handle instanceof GeneralizableElement)) {
            return illegalArgumentCollection(handle);
        }
        try {
            return getRefOutermostPackage(handle).getCore()
                    .getAParentSpecialization().getSpecialization(
                            (GeneralizableElement) handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getStateMachine(Object handle) {
        try {
            if (handle instanceof State) {
                return ((State) handle).getStateMachine();
            }
            if (handle instanceof Transition) {
                return ((Transition) handle).getStateMachine();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getState(Object handle) {
        try {
            if (handle instanceof Transition) {
                return getRefOutermostPackage(handle).getStateMachines()
                        .getAStateInternalTransition().getState(
                                (Transition) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getStates(Object handle) {
        try {
            if (handle instanceof Event) {
                return getRefOutermostPackage(handle).getStateMachines()
                        .getAStateDeferrableEvent().getState((Event) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getStereotypes(Object handle) {
        if (!(handle instanceof ModelElement)) {
            return illegalArgumentCollection(handle);
        }
        try {
            return ((ModelElement) handle).getStereotype();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getStimuli(Object handle) {
        try {
            if (isALink(handle)) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAStimulusCommunicationLink().getStimulus(
                                (Link) handle);
            }
            if (isAAction(handle)) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getADispatchActionStimulus().getStimulus(
                                (Action) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public Collection getReceivedStimuli(Object handle) {
        try {
            if (handle instanceof Instance) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAReceiverStimulus().getStimulus((Instance) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSentStimuli(Object handle) {
        try {
            if (handle instanceof Instance) {
                return getRefOutermostPackage(handle).getCommonBehavior()
                        .getAStimulusSender().getStimulus((Instance) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSubvertices(Object handle) {
        try {
            if (isACompositeState(handle)) {
                return ((CompositeState) handle).getSubvertex();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getSubmachine(Object handle) {
        try {
            if (handle instanceof SubmachineState) {
                return ((SubmachineState) handle).getSubmachine();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getSubmachineStates(Object handle) {
        try {
            if (handle instanceof StateMachine) {
                return ((StateMachine) handle).getSubmachineState();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSupplierDependencies(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return Collections.unmodifiableCollection(
                        getRefOutermostPackage(handle)
                        .getCore().getASupplierSupplierDependency()
                        .getSupplierDependency((ModelElement) handle)
                );
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getTop(Object handle) {
        try {
            if (handle instanceof StateMachine) {
                return ((StateMachine) handle).getTop();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getTransition(Object handle) {
        try {
            if (handle instanceof Guard) {
                return ((Guard) handle).getTransition();
            }
            if (handle instanceof Action) {
                return getRefOutermostPackage(handle).getStateMachines()
                        .getATransitionEffect().getTransition((Action) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getTrigger(Object handle) {
        try {
            if (handle instanceof Transition) {
                return ((Transition) handle).getTrigger();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public List getTriggers(Object handle) {
        try {
            if (handle instanceof Transition) {
                ArrayList l = new ArrayList();
                Event trig = ((Transition) handle).getTrigger();
                if (trig != null) {
                    l.add(trig);
                }
                return l;
            }
            return illegalArgumentList(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getType(Object handle) {
        try {
            if (handle instanceof StructuralFeature) {
                return ((StructuralFeature) handle).getType();
            }
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getParticipant();
            }
            if (handle instanceof Parameter) {
                return ((Parameter) handle).getType();
            }
            if (handle instanceof ObjectFlowState) {
                return ((ObjectFlowState) handle).getType();
            }
            if (handle instanceof TagDefinition) {
                return ((TagDefinition) handle).getTagType();
            }
            if (handle instanceof ClassifierInState) {
                return ((ClassifierInState) handle).getType();
            }
            if (handle instanceof TaggedValue) {
                return ((TaggedValue) handle).getType();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);

    }


    public Collection getTypedValues(Object handle) {
        try {
            if (handle instanceof TagDefinition) {
                return getRefOutermostPackage(handle).getCore()
                        .getATypeTypedValue().getTypedValue(
                                (TagDefinition) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getTarget(Object handle) {
        try {
            if (isATransition(handle)) {
                return ((Transition) handle).getTarget();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    @Deprecated
    public Object getTargetScope(Object handle) {
        try {
            if (handle instanceof StructuralFeature) {
                return ((StructuralFeature) handle).getTargetScope();
            }
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getTargetScope();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getTargetFlows(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getTargetFlow();
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public int getUpper(Object handle) {
        try {
            if (isAAssociationEnd(handle)) {
                int upper = 0;
                AssociationEnd end = (AssociationEnd) handle;
                if (end.getMultiplicity() != null) {
                    upper = getUpper(end.getMultiplicity());
                }
                return upper;
            }
            if (isAMultiplicity(handle)) {
                Multiplicity up = (Multiplicity) handle;
                List ranges = new ArrayList(up.getRange());
                // TODO: this assumes ranges are sorted. Is this true? - tfm
                return getUpper(ranges.get(ranges.size() - 1));
            }
            if (isAMultiplicityRange(handle)) {
                MultiplicityRange up = (MultiplicityRange) handle;
                return up.getUpper();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        illegalArgument(handle);
        return 0;
    }


    public Object getUseCase(Object handle) {
        try {
            if (handle instanceof ExtensionPoint) {
                return ((ExtensionPoint) handle).getUseCase();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public int getLower(Object handle) {
        try {
            if (isAAssociationEnd(handle)) {
                int lower = 0;
                AssociationEnd end = (AssociationEnd) handle;
                if (end.getMultiplicity() != null) {
                    lower = getLower(end.getMultiplicity());
                }
                return lower;
            }
            if (isAMultiplicity(handle)) {
                Multiplicity low = (Multiplicity) handle;
                List ranges = new ArrayList(low.getRange());
                // TODO: this assumes ranges are sorted. Is this true? - tfm
                return getLower(ranges.get(0));
            }
            if (isAMultiplicityRange(handle)) {
                MultiplicityRange low = (MultiplicityRange) handle;
                return low.getLower();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        illegalArgument(handle);
        return 0;
    }


    public Collection getTransitions(Object handle) {
        try {
            if (isAStateMachine(handle)) {
                return ((StateMachine) handle).getTransitions();
            } else if (isAState(handle)) {
                return ((State) handle).getInternalTransition();
            } else if (isAEvent(handle)) {
                return getRefOutermostPackage(handle).getStateMachines()
                        .getATransitionTrigger().getTransition((Event) handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public List getStructuralFeatures(Object handle) {
        if (!(handle instanceof Classifier)) {
            return illegalArgumentList(handle);
        }
        List result = new ArrayList();
        Classifier mclassifier = (Classifier) handle;

        try {
            Iterator features = mclassifier.getFeature().iterator();
            while (features.hasNext()) {
                Feature feature = (Feature) features.next();
                if (isAStructuralFeature(feature)) {
                    result.add(feature);
                }
            }
            return result;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public String getSpecification(Object handle) {
        try {
            if (handle instanceof Reception) {
                return ((Reception) handle).getSpecification();
            }
            if (handle instanceof Operation) {
                return ((Operation) handle).getSpecification();
            }
            // This case is handled by CoreHelper.getSpecification
            // confusing !
            // TODO: rationalize/merge these two methods
            // if (handle instanceof Method) {
            // return ((Method) handle).getSpecification();
            // }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }

        return illegalArgumentString(handle);
    }


    public Collection getSpecifications(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getSpecification();
            }
            if (handle instanceof Classifier) {
                return modelImpl.getCoreHelper().getRealizedInterfaces(
                        handle);
            }
            return illegalArgumentCollection(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection getSuppliers(Object handle) {
        try {
            if (handle instanceof Dependency) {
                return ((Dependency) handle).getSupplier();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    /**
     * @see org.argouml.model.Facade#getAction(java.lang.Object)
     */
    public Object getAction(Object handle) {
        try {
            if (handle instanceof Message) {
                return getRefOutermostPackage(handle).getCollaborations()
                        .getAActionMessage().getAction((Message) handle);
            }
            if (handle instanceof Argument) {
                return ((Argument) handle).getAction();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public List getActions(Object handle) {
        try {
            if (handle instanceof ActionSequence) {
                return ((ActionSequence) handle).getAction();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public Object getActionSequence(Object handle) {
        try {
            if (handle instanceof Action) {
                return ((Action) handle).getActionSequence();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }


    public Object getActivator(Object handle) {
        try {
            if (handle instanceof Message) {
                return ((Message) handle).getActivator();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getActivityGraph(Object handle) {
        try {
            if (handle instanceof Partition) {
                return ((Partition) handle).getActivityGraph();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getActivity(Object handle) {
        throw new NotImplementedException(
                "UML 1.x does not support the model element " //$NON-NLS-1$
                + handle.getClass().getName());
    }


    public List getActualArguments(Object handle) {
        try {
            if (handle instanceof Action) {
                return ((Action) handle).getActualArgument();
            }
            return illegalArgumentList(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Object getAddition(Object handle) {
        try {
            if (handle instanceof Include) {
                return ((Include) handle).getAddition();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getAggregation(Object handle) {
        return getAggregation1(handle);
    }

    public Object getAggregation1(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                return ((AssociationEnd) handle).getAggregation();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public Object getAggregation2(Object handle) {
        try {
            if (handle instanceof AssociationEnd) {
                // Simulates UML2 getting the aggregation from the opposite end
                AssociationEnd assEnd = (AssociationEnd) handle;
                Collection<AssociationEnd> assEnds = assEnd.getAssociation().getConnection();
                Iterator<AssociationEnd> it = assEnds.iterator();
                AssociationEnd other = it.next();
                if (other != assEnd) {
                    return other.getAggregation();
                } else {
                    other = it.next();
                    return other.getAggregation();
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public String getAlias(Object handle) {
        try {
            if (handle instanceof ElementImport) {
                return ((ElementImport) handle).getAlias();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException(
                "Must have an MDR element supplied. Received a "
                        + handle.getClass().getName());
    }


    public Collection getAssociatedClasses(Object handle) {
        try {
            Collection col = new ArrayList();
            if (handle instanceof Classifier) {
                Classifier classifier = (Classifier) handle;
                Collection ends = getAssociationEnds(classifier);
                Iterator it = ends.iterator();
                Set associations = new HashSet();
                while (it.hasNext()) {
                    AssociationEnd ae = (AssociationEnd) it.next();
                    associations.add(ae.getAssociation());
                }
                Collection otherEnds = new ArrayList();
                it = associations.iterator();
                while (it.hasNext()) {
                    otherEnds.addAll(((UmlAssociation) it.next())
                            .getConnection());
                }
                otherEnds.removeAll(ends);
                it = otherEnds.iterator();
                while (it.hasNext()) {
                    col.add(((AssociationEnd) it.next()).getParticipant());
                }
                return col;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public String getName(Object handle) {
        try {
            if (handle instanceof OrderingKindEnum) {
                return handle.toString().replaceAll("ok_", "");
            }
            if (handle instanceof CallConcurrencyKindEnum) {
                return handle.toString().replaceAll("cck_", "");
            }
            if (handle instanceof ChangeableKindEnum) {
                return handle.toString().replaceAll("ck_", "");
            }
            if (handle instanceof VisibilityKindEnum) {
                return handle.toString().replaceAll("vk_", "");
            }
            if (handle instanceof AggregationKindEnum) {
                return handle.toString().replaceAll("ak_", "");
            }
            if (handle instanceof ParameterDirectionKindEnum) {
                return handle.toString().replaceAll("pdk_", "");
            }
            if (handle instanceof PseudostateKind) {
                return handle.toString().replaceAll("pk_", "");
            }
            if (handle instanceof ModelElement) {
                ModelElement me = (ModelElement) handle;
                return me.getName();
            }
            if (handle instanceof Multiplicity) {
                return modelImpl.getDataTypesHelper().multiplicityToString(
                        handle);
            }
            if (handle instanceof Expression) {
                return ((Expression) handle).getBody();
            }
            if (handle instanceof ElementResidence) {
                return "";
            }
            if (handle instanceof TemplateParameter) {
                TemplateParameter templateParameter =
                    (TemplateParameter) handle;
                // TODO: Do we want to construct an artificial name here?
                StringBuffer result = new StringBuffer();
                ModelElement template = templateParameter.getTemplate();
                if (template != null) {
                    String name = template.getName();
                    if (name != null) {
                        result.append(name);
                    }
                }
                result.append(":");
                ModelElement parameter = templateParameter.getParameter();
                if (parameter != null) {
                    String name = parameter.getName();
                    if (name != null) {
                        result.append(name);
                    }
                }
                return "";
            }
            // TODO: What other non-ModelElement types do we need to handle here?
        } catch (InvalidObjectException e) {
            String uuid = getUUID(handle);
            if (uuid != null) {
                throw new InvalidElementException("UUID: " + uuid, e);
            } else {
                throw new InvalidElementException(e);
            }
        }
        throw new IllegalArgumentException(
                "Must have an MDR element supplied. Received a "
                        + handle.getClass().getName());
    }


    public Object getOwner(Object handle) {
        try {
            if ((handle instanceof Attribute)
                    && ((Attribute) handle).getAssociationEnd() != null) {
                return ((Attribute) handle).getAssociationEnd();
            }
            if (handle instanceof Feature) {
                return ((Feature) handle).getOwner();
            }
            if (handle instanceof TagDefinition) {
                return ((TagDefinition) handle).getOwner();
            }
            return illegalArgumentObject(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public String getTag(Object handle) {
        try {
            if (handle instanceof TaggedValue) {
                TagDefinition td = ((TaggedValue) handle).getType();
                if (td != null) {
                    return td.getName();
                }
                return null;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentString(handle);
    }


    public Iterator getTaggedValues(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return getTaggedValuesCollection(handle).iterator();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }


    public Collection getTaggedValuesCollection(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getTaggedValue();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getTaggedValue(Object handle, String name) {
        try {
            if (handle instanceof ModelElement) {
                ModelElement me = ((ModelElement) handle);
                Iterator i = me.getTaggedValue().iterator();
                while (i.hasNext()) {
                    TaggedValue tv = (TaggedValue) i.next();
                    if (tv.getType() != null
                            && name.equals(tv.getType().getName())) {
                        return tv;
                    }
                }
                return null;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public String getTaggedValueValue(Object handle, String name) {
        try {
            Object taggedValue = getTaggedValue(handle, name);
            if (taggedValue == null) {
                return "";
            }
            return getValueOfTag(taggedValue);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public String getTagOfTag(Object handle) {
        return getTag(handle);
    }


    public Object getTagDefinition(Object handle) {
        try {
            if (handle instanceof TaggedValue) {
                return ((TaggedValue) handle).getType();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Collection getTagDefinitions(Object handle) {
        try {
            if (handle instanceof Stereotype) {
                return ((Stereotype) handle).getDefinedTag();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(handle);
    }


    public Object getValue(Object handle) {
        try {
            if (handle instanceof TaggedValue) {
                return getValueOfTag(handle);
            }
            if (handle instanceof Argument) {
                return ((Argument) handle).getValue();
            }
            if (handle instanceof AttributeLink) {
                return ((AttributeLink) handle).getValue();
            }
            if (handle instanceof AggregationKind) {
                return Integer.valueOf(((AggregationKind) handle).toString());
            }
            if (handle instanceof OrderingKind) {
                return Integer.valueOf(((OrderingKind) handle).toString());
            }
            if (handle instanceof ParameterDirectionKind) {
                return Integer.valueOf(((ParameterDirectionKind) handle)
                        .toString());
            }
            if (handle instanceof VisibilityKind) {
                return Integer.valueOf(((VisibilityKind) handle).toString());
            }
            if (handle instanceof ScopeKind) {
                return Integer.valueOf(((ScopeKind) handle).toString());
            }
            /*
             * if (handle instanceof MessageDirectionKind) { return new
             * Integer(((MessageDirectionKind) handle).getValue()); }
             */
            if (handle instanceof ChangeableKind) {
                return Integer.valueOf(((ChangeableKind) handle).toString());
            }
            if (handle instanceof PseudostateKind) {
                return Integer.valueOf(((PseudostateKind) handle).toString());
            }
            if (handle instanceof CallConcurrencyKind) {
                return Integer.valueOf(((CallConcurrencyKind) handle)
                        .toString());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public String getValueOfTag(Object handle) {
        try {
            if (handle instanceof TaggedValue) {
                TaggedValue tv = (TaggedValue) handle;
                Collection refValues = tv.getReferenceValue();
                Collection values = tv.getDataValue();

                if (values.isEmpty() && refValues.isEmpty()) {
                    return "";
                }
                // TODO: Implement support for multiple TaggedValues
                if (values.size() + refValues.size() > 1) {
                    LOG.log(Level.WARNING,
                            "Don't know how to manage multiple values "
                            + "for a TaggedValue, returning first value");
                }

                // TODO: More is required here to support referenceValues
                Object value;
                if (refValues.size() > 0) {
                    value = refValues.iterator().next();
                } else {
                    value = values.iterator().next();
                }
                if (value instanceof String) {
                    return (String) value;
                } else if (value instanceof EnumerationLiteral) {
                    // TODO: Temporary stopgap for EnumerationLiteral
                    return ((EnumerationLiteral) value).getName();
                } else {
                    // TODO: Implement support for types other than String
                    LOG.log(Level.WARNING,
                            "Can't handled TaggedValue.dataValues which "
                            + " aren't Strings.  Converting to String");
                    return value.toString();
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentString(handle);
    }

    public Object getValueOfTag(Object handle, Object property) {
        throw new NotImplementedException();
    }

    public Collection getReferenceValue(Object taggedValue) {
        try {
            if (taggedValue instanceof TaggedValue) {
                TaggedValue tv = (TaggedValue) taggedValue;
                return tv.getReferenceValue();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(taggedValue);
    }


    public Collection getDataValue(Object taggedValue) {
        try {
            if (taggedValue instanceof TaggedValue) {
                TaggedValue tv = (TaggedValue) taggedValue;
                return tv.getDataValue();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(taggedValue);
    }


    public String getUUID(Object base) {
        try {
            if (base instanceof RefBaseObject) {
                String mofId = ((RefBaseObject) base).refMofId();
                // Look for an existing reference matching our MofID
                XmiReference ref = ((XmiReference) modelImpl
                        .getObjectToId().get(mofId));
                if (ref == null) {
                    return mofId;
                }
                // FIXME: depends on internal behavior of XmiReaderImpl.
                // Needed for solving issue 5017.
                else if (!ref.getSystemId().startsWith(
                        XmiReaderImpl.getTempXMIFileURIPrefix())) {
                    return ref.getSystemId() + "#" + ref.getXmiId();
                } else {
                    return ref.getXmiId();
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentString(base);
    }


    public Object getVisibility(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getVisibility();
            }
            if (handle instanceof ElementResidence) {
                return ((ElementResidence) handle).getVisibility();
            }
            if (handle instanceof ElementImport) {
                return ((ElementImport) handle).getVisibility();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }


    public Object getWhen(Object target) {
        try {
            if (isATimeEvent(target)) {
                return ((TimeEvent) target).getWhen();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(target);
    }


    public Object getChangeExpression(Object target) {
        try {
            if (isAChangeEvent(target)) {
                return ((ChangeEvent) target).getChangeExpression();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(target);
    }


    public Collection getPartitions(Object container) {
        try {
            if (container instanceof ActivityGraph) {
                return ((ActivityGraph) container).getPartition();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentCollection(container);
    }


    public String getReferenceState(Object o) {
        try {
            if (o instanceof StubState) {
                return ((StubState) o).getReferenceState();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentString(o);
    }


    public Object lookupIn(Object handle, String name) {
        try {
            if (handle instanceof Model) {
                return lookup((Model) handle, name);
            }
            if (handle instanceof Namespace) {
                return lookup((Namespace) handle, name);
            }
            if (handle instanceof Classifier) {
                return lookup((Classifier) handle, name);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    private ModelElement lookup(Namespace ns, String name) {
        try {
            int idx = name.indexOf("::");

            if (idx != -1) {
                String nm;
                nm = name.substring(0, idx);
                ModelElement e = lookup(ns, nm);
                if (e == null || !(e instanceof Namespace)) {
                    return null;
                }
                Namespace n = (Namespace) e;
                nm = name.substring(idx + 2);
                return lookup(n, nm);
            }
            Iterator i = ns.getOwnedElement().iterator();
            while (i.hasNext()) {
                ModelElement e = (ModelElement) i.next();
                if (name.equals(e.getName())) {
                    return e;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }


    public String toString(Object modelElement) {
        try {
            if (modelElement instanceof Multiplicity) {
                return org.argouml.model.Model.getDataTypesHelper()
                        .multiplicityToString(modelElement);
            } else if (modelElement instanceof Expression) {
                Expression exp = (Expression) modelElement;
                String result = getUMLClassName(modelElement);
                String language = exp.getLanguage();
                String body = exp.getBody();
                // TODO: I18N
                if (language != null && language.length() > 0) {
                    result += " (" + language + ")";
                }
                if (body != null && body.length() > 0) {
                    result += ": " + body;
                }
                return result;
            } else if (modelElement instanceof ElementImport) {
                ElementImport ei = (ElementImport) modelElement;
                ModelElement me = ei.getImportedElement();
                String typeName = getUMLClassName(me);
                String elemName = toString(me);
                String alias = ei.getAlias();
                if (alias != null && alias.length() > 0) {
                    // TODO: I18N This needs to be localized, but we don't
                    // have any localization capabilities in the model subsystem
//                    Object[] args = { typeName, elemName, alias };
//                  misc.name.element-import.alias = Imported {0}: {1} alias {2}
//                    return Translator.localize(
//                            "misc.name.element-import.alias", args);
                    return "Imported " + typeName + ": " + elemName + " alias "
                            + alias;
                } else {
//                    Object[] args = { typeName, elemName };
//                  misc.name.element-import = Imported {0}: {1}
//                    return Translator
//                            .localize("misc.name.element-import", args);
                    return "Imported " + typeName + ": " + elemName;
                }
            } else if (isAUMLElement(modelElement)) {
                return getUMLClassName(modelElement) + ": "
                        + getName(modelElement);
            } else if (modelElement == null) {
                return "";
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return modelElement.toString();
    }

    public String getUMLClassName(Object handle) {
        if (!isAUMLElement(handle)) {
            return illegalArgumentString(handle);
        }
        try {
            return modelImpl.getMetaTypes().getName(handle);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return Object for use in the return statement.
     */
    private Object illegalArgumentObject(Object arg) {
        illegalArgument(arg);
        return null;
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return String for use in the return statement.
     */
    private String illegalArgumentString(Object arg) {
        illegalArgument(arg);
        return null;
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     *
     * @param arg
     *            is the incorrect argument.
     */
    private void illegalArgument(Object arg) {
        throw new IllegalArgumentException("Unrecognized object "
                + getClassNull(arg));
    }

    /**
     * @param handle
     *            the Class or null
     * @return String
     */
    protected String getClassNull(Object handle) {
        if (handle == null) {
            return "[null]";
        }
        return "[" + handle + "/" + handle.getClass() + "]";
    }


    public String getTipString(Object modelElement) {
        return getUMLClassName(modelElement) + ": " + getName(modelElement);
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return Collection for use in the return statement.
     */
    private Collection illegalArgumentCollection(Object arg) {
        illegalArgument(arg);
        return null;
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return Collection for use in the return statement.
     */
    private List illegalArgumentList(Object arg) {
        illegalArgument(arg);
        return null;
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return a boolean for use in the return statement.
     */
    private boolean illegalArgumentBoolean(Object arg) {
        illegalArgument(arg);
        return false;
    }

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.<p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return Int for use in the return statement.
     */
    private int illegalArgumentInt(Object arg) {
        illegalArgument(arg);
        return 0;
    }

    public boolean isATagDefinition(Object handle) {
        return handle instanceof TagDefinition;
    }

    public List getEnumerationLiterals(Object handle) {
        try {
            if (!isAEnumeration(handle)) {
                throw new IllegalArgumentException("handle: " + handle);
            }
            return ((Enumeration) handle).getLiteral();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    public boolean isAEnumeration(Object handle) {
        return handle instanceof Enumeration;
    }

    public boolean isAEnumerationLiteral(Object handle) {
        return handle instanceof EnumerationLiteral;
    }

    public List getArguments(Object handle) {
        try {
            if (handle instanceof Binding) {
                return ((Binding) handle).getArgument();
            } else if (handle instanceof Action) {
                return ((Action) handle).getActualArgument();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }

    public Object getBinding(Object handle) {
        try {
            if (handle instanceof TemplateArgument) {
                return ((TemplateArgument) handle).getBinding();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public Object getDefaultElement(Object handle) {
        try {
            if (handle instanceof TemplateParameter) {
                return ((TemplateParameter) handle).getDefaultElement();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public Object getTemplate(Object handle) {
        try {
            if (handle instanceof TemplateParameter) {
                return ((TemplateParameter) handle).getTemplate();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentObject(handle);
    }

    public List getTemplateParameters(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((ModelElement) handle).getTemplateParameter();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return illegalArgumentList(handle);
    }

    public boolean isSynch(Object handle) {
        try {
            return ((ObjectFlowState) handle).isSynch();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public String[] getMetatypeNames() {
        // Get all (UML) metaclasses
        Collection<MofClass> metaTypes = getMetaClasses();
        String[] names = new String[metaTypes.size()];
        int i = 0;
        for (MofClass mofClass : metaTypes) {
            // TODO: Do we need to worry about UmlClass, UmlPackage, etc?
            names[i++] = mofClass.getName();
        }
        return names;
    }

    Collection<MofClass> getMetaClasses() {
        Collection<MofClass> metaTypes = modelImpl.getModelPackage()
                .getMofClass().refAllOfClass();
        return metaTypes;
    }

    public boolean isA(String metatypeName, Object element) {
        MofClass metaObject = (MofClass) ((RefObject) element).refMetaObject();
        return metatypeName != null
                && metatypeName.equals(metaObject.getName());
    }

    MofClass getMofClass(String metatypeName) {
        Collection<MofClass> metaTypes = getMetaClasses();
        for (MofClass mofClass : metaTypes) {
            // TODO: Generalize - assumes UML type names are unique
            // without the qualifying package names - true for UML 1.4
            if (metatypeName.equals(mofClass.getName())) {
                return mofClass;
            }
        }
        return null;
    }

    RefClass getProxy(String metatypeName, RefPackage extent) {
        Collection<MofClass> metaTypes = getMetaClasses();
        MofClass mofClass = getMofClass(metatypeName);
        List<String> names = mofClass.getQualifiedName();
        // Although this only handles one level of package, it is
        // OK for UML 1.4 because of clustering
        // Get the right UML package in the extent
        RefPackage pkg = extent.refPackage(names.get(0));
        // Return the metatype proxy
        return pkg.refClass(names.get(1));
    }


    public Collection getTargets(Object handle) {
        return illegalArgumentCollection(handle);
    }

    public boolean isADirectedRelationship(Object handle) {
        return false;
    }

    public boolean isAASynchCallMessage(Object handle) {
        Message message = (Message) handle;
        if (message.getAction() instanceof CallAction) {
            CallAction ca = (CallAction) message.getAction();
            return ca.isAsynchronous();
        }
        return false;
    }

    public boolean isAASynchSignalMessage(Object handle) {
        Message message = (Message) handle;
        if (message.getAction() instanceof SendAction) {
            SendAction sa = (SendAction) message.getAction();
            return sa.isAsynchronous();
        }
        return false;
    }

    public boolean isACreateMessage(Object handle) {
        Message message = (Message) handle;
        return (message.getAction() instanceof CreateAction);
    }

    public boolean isADeleteMessage(Object handle) {
        Message message = (Message) handle;
        return (message.getAction() instanceof DestroyAction);
    }

    public boolean isAReplyMessage(Object handle) {
        Message message = (Message) handle;
        return (message.getAction() instanceof ReturnAction);
    }

    public boolean isASynchCallMessage(Object handle) {
        Message message = (Message) handle;
        if (message.getAction() instanceof CallAction) {
            CallAction ca = (CallAction) message.getAction();
            return !ca.isAsynchronous();
        }
        return false;
    }
}
