/// Copyright (c) 2007,2009 Tom Morris and other contributors
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the project or its contributors may be used 
//       to endorse or promote products derived from this software without
//       specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.model.euml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.argouml.model.Facade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.Action;
import org.eclipse.uml2.uml.ActivityPartition;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.BehavioralFeature;
import org.eclipse.uml2.uml.CallAction;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.CollaborationUse;
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
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Expression;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.GeneralizationSet;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.ObjectFlow;
import org.eclipse.uml2.uml.ObjectNode;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Reception;
import org.eclipse.uml2.uml.RedefinableElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.SendObjectAction;
import org.eclipse.uml2.uml.SendSignalAction;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.StructuralFeature;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateableElement;
import org.eclipse.uml2.uml.TimeEvent;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.UseCase;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.VisibilityKind;


/**
 * The implementation of the Facade for EUML2.
 * 
 * @author Tom Morris
 * @author Bogdan Pistol
 */
class FacadeEUMLImpl implements Facade {

    private static final Logger LOG = Logger.getLogger(FacadeEUMLImpl.class);
    
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
    public FacadeEUMLImpl(EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public String getUmlVersion() {
        // TODO: Can we get this from the metamodel?
        return "2.1.1"; //$NON-NLS-1$
    }
    
    public boolean equalsPseudostateKind(Object ps1, Object ps2) {
        throw new NotYetImplementedException();

    }

    public Object getAction(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getActionSequence(Object handle) {
        throw new NotYetImplementedException();

    }

    public List getActions(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getActivatedMessages(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getActivator(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getActivityGraph(Object handle) {
        throw new NotYetImplementedException();

    }

    public List getActualArguments(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getAddition(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getAggregation(Object handle) {
        if (!(handle instanceof Property)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Property"); //$NON-NLS-1$
        }
        return ((Property) handle).getAggregation();
    }

    public String getAlias(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getAnnotatedElements(Object handle) {
        if (!(handle instanceof Comment)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Comment"); //$NON-NLS-1$
        }
        return ((Comment) handle).getAnnotatedElements();
    }

    public List getArguments(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getAssociatedClasses(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getAssociation(Object handle) {
        if (!(handle instanceof Property)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Property"); //$NON-NLS-1$
        }
        return ((Property) handle).getAssociation();
    }

    public Property getAssociationEnd(Object classifier, Object association) {
        if (!(classifier instanceof Classifier)) {
            throw new IllegalArgumentException(
                    "classifier must be instance of Classifier"); //$NON-NLS-1$
        }
        if (!(association instanceof Association)) {
            throw new IllegalArgumentException(
                    "association must be Association"); //$NON-NLS-1$
        }
        for (Property p : UMLUtil.getOwnedAttributes((Classifier) classifier)) {
            if (p.getAssociation() == association) {
                return p;
            }
        }
        return null;
    }

    public Collection getAssociationEnds(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Classifier"); //$NON-NLS-1$
        }
        Collection result = new ArrayList();
        for (Property p : ((Classifier) handle).getAttributes()) {
            if (p.getAssociation() != null) {
                result.add(p);
            }
        }
        return result;
    }

    public Collection getAssociationRoles(Object handle) {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return Collections.EMPTY_SET;
    }

    public List<Property> getAttributes(Object handle) {
        try {
            Classifier classifier = (Classifier) handle;
            EList<Property> attributes = classifier.getAttributes();
            List<Property> result = new ArrayList<Property>();
            for (Property p : attributes) {
                if (p.getAssociation() == null) {
                    result.add(p);
                }
            }
            return result;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "handle must be instance of Classifier"); //$NON-NLS-1$
        }
    }

    public Object getBase(Object handle) {
        if (handle instanceof Extend) {
            return ((Extend) handle).getExtendedCase();
        } else if (handle instanceof Include) {
            return ((Include) handle).getAddition();
        }
        throw new NotYetImplementedException();
    }

    public Collection<String> getBaseClasses(Object handle) {
        if (isAStereotype(handle)) {
            // TODO: it's an implementation, but is it correct? - thn
            EList<Class> eList =
                ((Stereotype) handle).getAllExtendedMetaclasses();
            ArrayList<String> list = new ArrayList<String>();
            Iterator iter = eList.iterator();
            while (iter.hasNext()) {
                list.add(((Class) iter.next()).getName());
            }
            return list;
        }
        throw new IllegalArgumentException(
                "handle must be instance of Stereotype"); //$NON-NLS-1$
    }

    public Collection getBases(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getBehavioralFeature(Object handle) {
        if (!(handle instanceof Parameter)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Parameter"); //$NON-NLS-1$
        }
        return ((Parameter) handle).getOperation();
    }

    public Collection getBehaviors(Object handle) {
        // TODO:
        return Collections.EMPTY_SET;
    }

    public Object getBinding(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getBody(Object handle) {
        if (handle instanceof Comment) {
            return ((Comment) handle).getBody();
        } else if (handle instanceof Constraint) {
            return getValueSpecification(
                    ((Constraint) handle).getSpecification());
        } else if (handle instanceof ValueSpecification) {
            return getValueSpecification((ValueSpecification) handle);
        } else if (handle instanceof Operation) {
            throw new NotYetImplementedException();
        }
        throw new IllegalArgumentException(
                "Unsupported argument type - must be Comment, Constraint," +
                " Expression, or Method ");

    }

    private String getValueSpecification(ValueSpecification handle) {
//        return handle.stringValue();
        if (handle instanceof OpaqueExpression) {
            return modelImpl.getDataTypesHelper().getBody(handle);
        } else if (handle instanceof LiteralBoolean) {
            return Boolean.toString(((LiteralBoolean) handle).isValue());
        } else if (handle instanceof LiteralInteger) {
            return Integer.toString(((LiteralInteger) handle).getValue());
        } else if (handle instanceof LiteralString) {
            return ((LiteralString) handle).getValue();
        } else {
            // TODO: Lots more types - Duration, Instance, Interval
            throw new NotYetImplementedException();
        }
    }

    public int getBound(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getChangeExpression(Object target) {
        throw new NotYetImplementedException();

    }

    @SuppressWarnings("deprecation")
    public Object getChangeability(Object handle) {
        if (!(handle instanceof StructuralFeature)) {
            throw new IllegalArgumentException(
                    "handle must be StructuralFeature"); //$NON-NLS-1$
        }
        return ((StructuralFeature) handle).isReadOnly() ? modelImpl
                .getChangeableKind().getFrozen() : modelImpl
                .getChangeableKind().getChangeable();
    }

    public Object getSpecific(Object handle) {
        if (!(handle instanceof Generalization)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Generalization"); //$NON-NLS-1$
        }
        return ((Generalization) handle).getSpecific();
    }
    
    public Collection getChildren(Object handle) {
        return modelImpl.getCoreHelper().getSubtypes(handle);
    }

    public Object getClassifier(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getClassifierRoles(Object handle) {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return Collections.EMPTY_SET;
    }

    public Collection getClassifiers(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getClassifiersInState(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection<Dependency> getClientDependencies(Object handle) {
        if (!(handle instanceof NamedElement)) {
            throw new IllegalArgumentException(
                    "handle must be instance of NamedElement"); //$NON-NLS-1$
        }
        return ((NamedElement) handle).getClientDependencies();
    }

    public Collection<NamedElement> getClients(Object handle) {
        if (!(handle instanceof Dependency)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Dependency"); //$NON-NLS-1$
        }
        return ((Dependency) handle).getClients();
    }

    public Collection<Collaboration> getCollaborations(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        Set<Collaboration> result = new HashSet<Collaboration>();
        if (handle instanceof Classifier) {
            for (CollaborationUse cu : ((Classifier) handle)
                    .getCollaborationUses()) {
                result.add(cu.getType());
            }
        }
        return result;
    }

    public Collection<Comment> getComments(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Element"); //$NON-NLS-1$
        }
        Collection<Comment> result = new HashSet<Comment>();
        for (EStructuralFeature.Setting reference : UMLUtil
                .getInverseReferences((Element) handle)) {
            if (reference.getEObject() instanceof Comment) {
                result.add((Comment) reference.getEObject());
            }
        }
        return result;
    }

    public Object getCommunicationConnection(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getCommunicationLink(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getComponentInstance(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getConcurrency(Object handle) {
        if (!(handle instanceof BehavioralFeature)) {
            throw new IllegalArgumentException("handle must be a BehavioralFeature!");
        }
        return ((BehavioralFeature)handle).getConcurrency();
    }

    public Object getCondition(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getConnections(Object handle) {
        // Link does not exist in UML2
        if (handle == null) {
            // this is wrongly called with a null handle,
            // as a workaround we return an empty collection
            return Collections.emptyList();
        }
        if (!(handle instanceof Association)) {
            throw new IllegalArgumentException(
                    "handle must be instance of Association"); //$NON-NLS-1$
        }
        return ((Association) handle).getMemberEnds();
    }

    public List getConstrainedElements(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getConstrainingElements(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getConstraints(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getContainer(Object handle) {
        if (handle instanceof Vertex) {
            return ((Vertex) handle).getContainer();
        } else if (handle instanceof Transition) {
            return ((Transition) handle).getContainer();
        } 
        // TODO: unfinished implementation
        throw new NotYetImplementedException();
    }

    public Collection getContents(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getContext(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getContexts(Object handle) {
        // TODO: This is probably related to the SendEvent that is sending the
        // Signal, but the association is not navigable in that direction
        return Collections.EMPTY_SET;
    }

    public Collection getCreateActions(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getDataValue(Object taggedValue) {
        throw new NotYetImplementedException();
    }

    public Object getDefaultElement(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getDefaultValue(Object handle) {
        return ((Parameter) handle).getDefault();
    }

    public Collection getDeferrableEvents(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getDeployedComponents(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getDeploymentLocations(Object handle) {
        throw new NotYetImplementedException();
    }

    @SuppressWarnings("deprecation")
    public Object getDiscriminator(Object handle) {
        // Gone from UML 2.x
//        throw new NotImplementedException();
        return null;
    }

    public Object getDispatchAction(Object handle) {
        throw new NotYetImplementedException();
    }

    public Behavior getDoActivity(Object handle) {
        return ((State) handle).getDoActivity();
    }

    public Behavior getEffect(Object handle) {
        return ((Transition) handle).getEffect();
    }

    public Collection<ElementImport> getElementImports(Object handle) {
        if (!(handle instanceof Namespace)) {
            throw new IllegalArgumentException();
        }
        return ((Namespace) handle).getElementImports();
    }

    public Collection getElementImports2(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        throw new NotYetImplementedException();
    }

    public Collection getElementResidences(Object handle) {
        throw new NotYetImplementedException();
    }

    public Behavior getEntry(Object handle) {
        return ((State) handle).getEntry();
    }

    public Enumeration getEnumeration(Object handle) {
        return ((EnumerationLiteral) handle).getEnumeration();
    }

    public List<EnumerationLiteral> getEnumerationLiterals(Object handle) {
        return ((Enumeration) handle).getOwnedLiterals();
    }

    public Behavior getExit(Object handle) {
        return ((State) handle).getExit();
    }

    public Object getExpression(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getExtendedElements(Object handle) {
        ((Stereotype) handle).getExtendedMetaclasses();
        throw new NotYetImplementedException();

    }

    public Collection getExtenders(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection<Extend> getExtends(Object handle) {
        if (handle instanceof UseCase) {
            return ((UseCase) handle).getExtends();
        } else if (handle instanceof ExtensionPoint) {
            // TODO:
            throw new NotYetImplementedException();
        }
        throw new IllegalArgumentException();
    }

    public UseCase getExtension(Object handle) {
        if (!(handle instanceof Extend)) {
            throw new IllegalArgumentException();
        }
        return ((Extend) handle).getExtension();
    }

    public ExtensionPoint getExtensionPoint(Object handle, int index) {
        if (!(handle instanceof Extend)) {
            throw new IllegalArgumentException();
        }
        return ((Extend) handle).getExtensionLocations().get(index);
    }

    public List<ExtensionPoint> getExtensionPoints(Object handle) {
        if (handle instanceof UseCase) {
            return ((UseCase) handle).getExtensionPoints();
        } else if (handle instanceof Extend) {
            return ((Extend) handle).getExtensionLocations();
        } 
        throw new IllegalArgumentException(
                "Expected UseCase or Extend : " + handle); //$NON-NLS-1$
    }

    public List<Feature> getFeatures(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        List<Feature> result = new ArrayList<Feature>();
        for (Feature f : ((Classifier) handle).getFeatures()) {
            if (!(f instanceof Property) 
                    || ((Property) f).getAssociation() == null) {
                result.add(f);
            }
        }
        return result;
    }

    public Object getGeneralization(Object handle, Object parent) {
        return modelImpl.getCoreHelper().getGeneralization(handle, parent);
    }

    public Collection<Generalization> getGeneralizations(Object handle) {
        Set<Generalization> result = new HashSet<Generalization>();
        for (Generalization g : ((Classifier) handle).getGeneralizations()) {
            result.add(g);
        }
        return result;
    }

    public Object getGuard(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getIcon(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getImportedElement(Object elementImport) {
        if (!(elementImport instanceof ElementImport)) {
            throw new IllegalArgumentException();
        }
        return ((ElementImport) elementImport).getImportedElement();
    }

    public Collection<PackageableElement> getImportedElements(Object pack) {
        if (!(pack instanceof Namespace)) {
            throw new IllegalArgumentException();
        }
        return ((Namespace) pack).getImportedElements();
    }

    public Collection getInStates(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getIncluders(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection<Include> getIncludes(Object handle) {
        if (!(handle instanceof UseCase)) {
            throw new IllegalArgumentException();
        }
        return ((UseCase) handle).getIncludes();
    }

    public Collection getIncomings(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getInitialValue(Object handle) {
        if (!(handle instanceof Property)) {
            throw new IllegalArgumentException();
        }
        return ((Property) handle).getDefaultValue();
    }

    public Object getInstance(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getInstances(Object handle) {
        // TODO: InstanceSpecification -> Classifier association isn't
        // navigable in this direction
        return Collections.EMPTY_SET;
    }

    public Object getInteraction(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getInteractions(Object handle) {
        
        // Comment by A- Rueckert: I don't think it makes much sense to query interactions
        // from a Collaboration in UML2, since this diagram does no longer exist and 
        // an Interaction means something different in UML2.
        if (!(handle instanceof Collaboration)) {
            throw new IllegalArgumentException("handle has to be a Collaboration!");
        }
        return ((Collaboration)handle).getCollaborationRoles();
    }

    public Collection getInternalTransitions(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getKind(Object handle) {
        if (handle instanceof Pseudostate) {
            return ((Pseudostate) handle).getKind();
        } else if (handle instanceof Parameter) {
            return ((Parameter) handle).getDirection();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Object getLink(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getLinkEnds(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getLinks(Object handle) {
        // TODO: no Links in UML 2
        return Collections.EMPTY_SET;
    }

    public String getLocation(Object handle) {
        // TODO: Removed from UML2
        return "";
    }

    public int getLower(Object handle) {
        if (!(handle instanceof MultiplicityElement)) {
            throw new IllegalArgumentException();
        }
        return ((MultiplicityElement) handle).getLower();
    }

    public Collection getMessages(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getMethods(Object handle) {
        if (handle instanceof BehavioralFeature) {
            return ((BehavioralFeature)handle).getMethods();
        }
        // there's more to be handled, which still need to be implemented:
        throw new NotYetImplementedException();
    }

    public Object getInnerContainingModel(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        return ((Element) handle).getModel();        
    }
    
    public Object getRoot(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        // TODO: Does this do what we need? - tfm
        return ((Element) handle).getModel();
    }

    public Object getModelElement(Object handle) {
        throw new NotYetImplementedException();
    }

    public List getModelElementAssociated(Object handle) {
        throw new NotYetImplementedException();
    }

    public Element getModelElementContainer(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        return ((Element) handle).getOwner();
    }

    public List<Element> getModelElementContents(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        return ((Element) handle).getOwnedElements();
    }

    public MultiplicityElement getMultiplicity(Object handle) {
        // MultiplicityElement is now an interface implemented
        // by element types that support multiplicities - tfm
        if (handle instanceof MultiplicityElement) {
            return (MultiplicityElement) handle;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getName(Object handle) {
        if (!(handle instanceof EObject) // should be Element not EObject really
                && !(handle instanceof String)
                && !(handle instanceof Enumerator)) {
            throw new IllegalArgumentException();
        }
        if (handle instanceof String) {
            return (String) handle;
        } else if (handle instanceof NamedElement) {
            if (((NamedElement) handle).getName() != null) {
                return ((NamedElement) handle).getName();
            } else {
                return ""; //$NON-NLS-1$
            }
        } else if (handle instanceof Enumerator) {
            return ((Enumerator) handle).getName();
        } else {
            // TODO: Some elements such as Generalization are
            // no longer named.  For a transitional period we'll
            // return a String to debug can continue, but the
            // calling code should probably be fixed. - tfm 20070607
            return handle.toString();
        }
//        throw new IllegalArgumentException();
    }

    public Object getNamespace(Object handle) {
        Object o = ((Element) handle).getOwner();
        if (o instanceof Namespace) {
            return o;
        }
        return null;
    }

    public Object getNextEnd(Object handle) {
        if (!isAAssociationEnd(handle)) {
            throw new IllegalArgumentException();
        }
        List l = ((Property) handle).getAssociation().getMemberEnds();
        int i = l.indexOf(handle);
        if (i + 1 < l.size()) {
            return l.get(i + 1);
        } else {
            return l.get(0);
        }
    }

    public Object getNodeInstance(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getObjectFlowStates(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getOccurrences(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getOperation(Object handle) {
        throw new NotYetImplementedException();
    }

    public List<Operation> getOperations(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        List<Feature> features = ((Classifier) handle).getFeatures();
        List<Operation> result = new ArrayList<Operation>();
        for (Feature f : features) {
            if (f instanceof Operation) {
                result.add((Operation) f);
            }
        }
        return result;
    }

    public List<Feature> getOperationsAndReceptions(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        List<Feature> features = ((Classifier) handle).getFeatures();
        List<Feature> result = new ArrayList<Feature>();
        for (Feature f : features) {
            if (f instanceof Operation || f instanceof Reception) {
                result.add(f);
            }
        }
        return result;
    }

    public Object getOrdering(Object handle) {
        if (handle instanceof MultiplicityElement) {
            if (((MultiplicityElement) handle).isOrdered()) {
                return modelImpl.getOrderingKind().getOrdered();
            } else {
                return modelImpl.getOrderingKind().getUnordered();
            }
        }
        throw new NotYetImplementedException();
    }

    public Collection getOtherAssociationEnds(Object handle) {
        if (!isAAssociationEnd(handle)) {
            throw new IllegalArgumentException();
        }
        List l = new ArrayList(
                ((Property) handle).getAssociation().getMemberEnds());
        l.remove(handle);
        return l;
    }

    public Collection getOtherLinkEnds(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getOutgoings(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection<Element> getOwnedElements(Object handle) {
        if (!(handle instanceof Namespace)) {
            throw new IllegalArgumentException();
        }
        return ((Namespace) handle).getOwnedElements();
    }

    public Element getOwner(Object handle) {
        if (!(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        return ((Element) handle).getOwner();
    }

    public Namespace getPackage(Object handle) {
        if (!(handle instanceof ElementImport)) {
            throw new IllegalArgumentException();
        }
        return ((ElementImport) handle).getImportingNamespace();
    }

    public Object getParameter(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getParameter(Object handle, int n) {
        throw new NotYetImplementedException();
    }

    public Collection<Parameter> getParameters(Object handle) {
        if (handle instanceof BehavioralFeature || handle instanceof Event) {
            return getParametersList(handle);
        } 
        // TODO: implement remaining supported types
        throw new NotYetImplementedException();
    }

    public List<Parameter> getParametersList(Object handle) {
        if (handle instanceof BehavioralFeature) {
            return ((BehavioralFeature) handle).getOwnedParameters();
        }
        // TODO: implement remaining supported types
        throw new NotYetImplementedException();
    }

    public Classifier getGeneral(Object handle) {
        if (!(handle instanceof Generalization)) {
            throw new IllegalArgumentException();
        }
        return ((Generalization) handle).getGeneral();
    }
    
    public Collection getPartitions(Object container) {
        throw new NotYetImplementedException();
    }

    public Object getPowertype(Object handle) {
        if (handle instanceof Generalization) {
            EList<GeneralizationSet> genSets = ((Generalization) handle).getGeneralizationSets();
            for (GeneralizationSet gs : genSets) {
                Classifier powerType = gs.getPowertype();
            }
        }
        // TODO: This probably can't be implemented in a way that will make
        // the UML 1.4 UI happy.  Needs to be generalized to UML 2 semantics.
        return null;
    }

    public Collection getPowertypeRanges(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getPredecessors(Object handle) {
        throw new NotYetImplementedException();
    }

    public List<Property> getQualifiers(Object handle) {
        if (!(handle instanceof Property)) {
            throw new IllegalArgumentException();
        }
        return ((Property) handle).getQualifiers();
    }

    public Collection getRaisedSignals(Object handle) {
        throw new NotYetImplementedException();
    }
    
    public Collection getRaisedExceptions( Object handle) {
        if (handle instanceof Operation) {
            return ((Operation)handle).getRaisedExceptions();
        }
        return null;
    }

    public Collection getReceivedMessages(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getReceivedStimuli(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getReceiver(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getReceptions(Object handle) {
        // TODO: Signal -> Receptions association not navigable in this
        // direction
        return Collections.EMPTY_SET;
    }

    public Object getRecurrence(Object handle) {
        throw new NotYetImplementedException();
    }

    public String getReferenceState(Object o) {
        throw new NotYetImplementedException();
    }

    public Collection getReferenceValue(Object taggedValue) {
        throw new NotYetImplementedException();
    }

    public Object getRepresentedClassifier(Object handle) {
        
        // Comment by A. Rueckert <a_rueckert@gmx.net> :
        // I think, the handle holding the collaboration implementation, should
        // rather be a CollaborationUse in UML2. 
        // But as a workaround for now, I'll try to get a Collaboration representation
        // (CollaborationUse) and then try to get the owning Classifier from there...
        if (!(handle instanceof Collaboration)) {
            throw new IllegalArgumentException("handle should be a Collaboration!"); //$NON-NLS-<n>$ 
        }
        CollaborationUse collabUse = ((Collaboration)handle).getRepresentation();
        
        return collabUse == null ? null : collabUse.getOwner();
    }

    public Object getRepresentedOperation(Object handle) {
        
        if (!(handle instanceof Collaboration)) {
            throw new IllegalArgumentException("handle should be a Collaboration!"); //$NON-NLS-<n>$
        }
        return ((Collaboration)handle).getOperation(null,null,null);
    }

    public Object getResident(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getResidentElements(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getResidents(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getScript(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getSender(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getSentMessages(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSentStimuli(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getSignal(Object handle) {
        throw new NotYetImplementedException();

    }

    public Vertex getSource(Object handle) {
        if (!(handle instanceof Transition)) {
            throw new IllegalArgumentException();
        }
        return ((Transition) handle).getSource();
    }

    public Collection getSourceFlows(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSources(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSpecializations(Object handle) {
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        return ((Classifier) handle).getTargetDirectedRelationships(
                UMLPackage.Literals.GENERALIZATION);
    }

    public String getSpecification(Object handle) {
        // TODO: The UML2 spec provides a specification for Behavior, which
        // is a BehavioralFeature, but ArgoUML calls this for an Operation
        // instance, so we must check what this method is intended for (bug?).
        if( handle instanceof Behavior) {
            return ((Behavior)handle).getSpecification().getName();
        }
        if( handle instanceof Operation) {
            return null;
        }
        throw new NotYetImplementedException();
    }

    public Collection getSpecifications(Object handle) {
        if (handle instanceof Property) {
            // TODO: unimplemented
//          return ((Property) handle).gets
            return Collections.EMPTY_SET;
        } else if (handle instanceof org.eclipse.uml2.uml.Class) {
            ((org.eclipse.uml2.uml.Class) handle).getInterfaceRealizations();
        }
        throw new NotYetImplementedException();

    }

    public Object getState(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getStateMachine(Object handle) {
        if (handle instanceof Pseudostate) {
            return ((Pseudostate) handle).getStateMachine();
        } else if (handle instanceof Region) {
            return ((Region) handle).getStateMachine();
        } 
        throw new NotYetImplementedException();
    }

    public Collection getStates(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getStereotypes(Object handle) {
        // TODO: Changed to Profiles::Class::extension in UML 2.x?
        return Collections.EMPTY_SET;
    }

    public Collection getStimuli(Object handle) {
        throw new NotYetImplementedException();
    }

    public List<StructuralFeature> getStructuralFeatures(Object handle) {
        List<Feature> features = getFeatures(handle);
        List<StructuralFeature> result = new ArrayList<StructuralFeature>();
        for (Feature f : features) {
            if (f instanceof StructuralFeature) {
                result.add((StructuralFeature) f);
            }
        }
        return result;
    }

    public Object getSubmachine(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSubmachineStates(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSubvertices(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSuccessors(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getSupplierDependencies(Object handle) {
        // TODO: not navigable this direction? - tfm
        return Collections.EMPTY_SET;
    }

    public Collection<NamedElement> getSuppliers(Object handle) {
        if (!(handle instanceof Dependency)) {
            throw new IllegalArgumentException();
        }
        return ((Dependency) handle).getSuppliers();
    }

    public String getTag(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getTagDefinition(Object handle) {
        throw new NotYetImplementedException();

    }

    public Collection getTagDefinitions(Object handle) {
        throw new NotYetImplementedException();

    }

    public String getTagOfTag(Object handle) {
        throw new NotYetImplementedException();

    }

    public Object getTaggedValue(Object handle, String name) {
        // TODO: not implemented
        return null;
    }

    public String getTaggedValueValue(Object handle, String name) {
        throw new NotYetImplementedException();
    }

    public Iterator getTaggedValues(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getTaggedValuesCollection(Object handle) {
        throw new NotYetImplementedException();
    }

    public Vertex getTarget(Object handle) {
        if (!(handle instanceof Transition)) {
            throw new IllegalArgumentException();
        }
        return ((Transition) handle).getTarget();
    }

    public Collection getTargetFlows(Object handle) {
        throw new NotYetImplementedException();
    }

    @SuppressWarnings("deprecation")
    public Object getTargetScope(Object handle) {
        // Removed from UML 2.x and deprecated in Model API
        // so we won't implement it
//        throw new NotImplementedException();
	// we do not throw an exception because ArgoUML still uses this
        return null;
    }

    public Object getTemplate(Object handle) {
        throw new NotYetImplementedException();
    }

    public List getTemplateParameters(Object handle) {
        if (handle instanceof TemplateableElement) {
            return ((TemplateableElement) handle).getTemplateBindings();
        }
        throw new IllegalArgumentException(
            "handle must be instance of TemplateableElement"); //$NON-NLS-1$
    }

    public String getTipString(Object modelElement) {
        // TODO: Not Model implementation dependent
        String name = getName(modelElement);
        if (name.equals("")) { //$NON-NLS-1$
            return getUMLClassName(modelElement);
        } else {
            return getUMLClassName(modelElement) + ": " + name; //$NON-NLS-1$
        }
    }

    public Object getTop(Object handle) {
        throw new NotYetImplementedException();
    }

    public Object getTransition(Object handle) {
        throw new NotYetImplementedException();
    }

    public Collection getTransitions(Object handle) {
        throw new NotYetImplementedException();
    }

    public Trigger getTrigger(Object handle) {
        if (!(handle instanceof Transition)) {
            throw new IllegalArgumentException();
        }
        // TODO: Transitions can have multiple Triggers now?
        // Need API change to handle - tfm
        return ((Transition) handle).getTriggers().get(0);
    }

    public Object getType(Object handle) {
        if (!(handle instanceof TypedElement)) {
            throw new IllegalArgumentException();
        }
        return ((TypedElement) handle).getType();
    }

    public Collection getTypedValues(Object handle) {
        throw new NotYetImplementedException();
    }

    public String getUMLClassName(Object handle) {
        return modelImpl.getMetaTypes().getName(handle);
    }

    public String getUUID(Object element) {
        if (!(element instanceof EObject)) {
            throw new IllegalArgumentException();
        }
        Resource r = ((EObject) element).eResource();
        if (r == null) {
            return "";
            // TODO: Figure out when this is getting thrown
//            throw new UnsupportedOperationException();
        }
        return r.getURIFragment((EObject) element);
    }

    public int getUpper(Object handle) {
        if (!(handle instanceof MultiplicityElement)) {
            throw new IllegalArgumentException();
        }
        return ((MultiplicityElement) handle).getUpper();
    }

    public UseCase getUseCase(Object handle) {
        if (!(handle instanceof ExtensionPoint)) {
            throw new IllegalArgumentException();
        }
        return ((ExtensionPoint) handle).getUseCase();
    }

    public Object getValue(Object handle) {
        throw new NotYetImplementedException();
    }

    public String getValueOfTag(Object handle) {
        throw new NotYetImplementedException();
    }

    public VisibilityKind getVisibility(Object handle) {
        if (!(handle instanceof NamedElement)) {
            throw new IllegalArgumentException();
        }
        return ((NamedElement) handle).getVisibility();
    }

    public ValueSpecification getWhen(Object target) {
        if (!(target instanceof TimeEvent)) {
            throw new IllegalArgumentException();
        }
        return ((TimeEvent) target).getWhen();
    }

    public boolean hasReturnParameterDirectionKind(Object handle) {
        if (handle instanceof Parameter) {
            return ParameterDirectionKind.RETURN_LITERAL
                    .equals(((Parameter) handle).getDirection());
        }
        throw new IllegalArgumentException("Argument must be a Parameter");
    }

    public boolean isAAbstraction(Object handle) {
        return handle instanceof Abstraction;
    }

    public boolean isAAction(Object handle) {
        return handle instanceof Action;
    }

    public boolean isAActionSequence(Object handle) {
        // TODO: gone in UML 2.x
        return false;
    }

    public boolean isAActionState(Object handle) {
        // TODO: ActionState, CallState, and SubactivityState have been replaced
        // in UML 2.0 by explicitly modeled Actions
        return false;
    }

    public boolean isAActivityGraph(Object handle) {
        // TODO: Just a guess - double check - tfm;
//        return handle instanceof ActivityGroup;
        return false;
    }

    public boolean isAActor(Object handle) {
        return handle instanceof Actor;
    }

    public boolean isAAggregationKind(Object handle) {
        return handle instanceof AggregationKind;
    }

    public boolean isAArgument(Object modelElement) {
        // TODO: Double check - tfm
        return modelElement instanceof InputPin
                || modelElement instanceof OutputPin;
    }

    public boolean isAArtifact(Object handle) {
        return handle instanceof Artifact;
    }

    public boolean isAAssociation(Object handle) {
        return handle instanceof Association;
    }

    public boolean isAAssociationClass(Object handle) {
        return handle instanceof AssociationClass;
    }

    public boolean isAAssociationEnd(Object handle) {
        return handle instanceof Property
                && ((Property) handle).getAssociation() != null;
    }

    public boolean isAAssociationEndRole(Object handle) {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return false;
    }

    public boolean isAAssociationRole(Object handle) {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return false;
    }

    public boolean isAAttribute(Object handle) {
        // TODO: This probably needs more qualification - tfm
        return handle instanceof Property
                && ((Property) handle).getClass_() != null;
    }

    public boolean isAAttributeLink(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isABehavioralFeature(Object handle) {
        return handle instanceof BehavioralFeature;
    }

    public boolean isABinding(Object handle) {
        return handle instanceof TemplateBinding;
    }


    public boolean isACallAction(Object handle) {
        return handle instanceof CallAction;
    }

    public boolean isACallEvent(Object handle) {
        return handle instanceof CallEvent;
    }

    public boolean isACallState(Object handle) {
        // TODO: ActionState, CallState, and SubactivityState have been replaced
        // in UML 2.0 by explicitly modeled Actions
        return false;
    }

    public boolean isAChangeEvent(Object handle) {
        return handle instanceof ChangeEvent;
    }

    public boolean isAClass(Object handle) {
        return handle instanceof org.eclipse.uml2.uml.Class;
    }

    public boolean isAClassifier(Object handle) {
        return handle instanceof Classifier;
    }

    public boolean isAClassifierInState(Object handle) {
        // TODO: gone in UML 2
        return false;
    }

    public boolean isAClassifierRole(Object handle) {
        // TODO: In UML 2.0, ClassifierRole, AssociationRole, and
        // AssociationEndRole have been replaced by the internal 
        // structure of the Collaboration
        return false;
    }

    public boolean isACollaboration(Object handle) {
        return handle instanceof Collaboration;
    }

    public boolean isACollaborationInstanceSet(Object handle) {
        throw new NotYetImplementedException();

    }

    public boolean isAComment(Object handle) {
        return handle instanceof Comment;
    }

    public boolean isAComponent(Object handle) {
        return handle instanceof Component;
    }

    public boolean isAComponentInstance(Object handle) {
        // TODO: Gone in UML 2
        return false;
    }

    public boolean isACompositeState(Object handle) {
        // TODO: changed in UML2
        return false;
    }

    public boolean isAConcurrentRegion(Object handle) {
        return false;
    }

    public boolean isAConstraint(Object handle) {
        return handle instanceof Constraint;
    }

    public boolean isACreateAction(Object handle) {
        // Double check - tfm
        return handle instanceof CreateObjectAction;
    }

    public boolean isADataType(Object handle) {
        return handle instanceof DataType;
    }

    public boolean isADataValue(Object handle) {
        return false;
    }

    public boolean isADependency(Object handle) {
        return handle instanceof Dependency;
    }

    public boolean isADestroyAction(Object handle) {
        // TODO: double check - tfm
        return handle instanceof DestroyObjectAction;
    }

    public boolean isAElement(Object handle) {
        return handle instanceof Element;
    }

    public boolean isAElementImport(Object handle) {
        return handle instanceof ElementImport;
    }

    public boolean isAElementResidence(Object handle) {
        // TODO: Restructured in UML 2
        return false;
    }

    public boolean isAEnumeration(Object handle) {
        return handle instanceof Enumeration;
    }

    public boolean isAEnumerationLiteral(Object handle) {
        return handle instanceof EnumerationLiteral;
    }

    public boolean isAEvent(Object handle) {
        return handle instanceof Event;
    }

    public boolean isAException(Object handle) {
        // TODO: This isn't right
        return handle instanceof Signal;
    }

    public boolean isAExpression(Object handle) {
        return handle instanceof Expression
                // below for UML 1.4 compatibility
                || handle instanceof OpaqueExpression;
    }

    public boolean isAExtend(Object handle) {
        return handle instanceof Extend;
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
        // TODO: double check - tfm
        return handle instanceof ObjectFlow;
    }

    public boolean isAGeneralizableElement(Object handle) {
        // TODO: Changed from UML 1.4
        return handle instanceof Classifier;
    }

    public boolean isAGeneralization(Object handle) {
        return handle instanceof Generalization;
    }

    public boolean isAGuard(Object handle) {
        // TODO: gone in UML 2
        return false;
    }

    public boolean isAInclude(Object handle) {
        return handle instanceof Include;
    }

    public boolean isAInstance(Object handle) {
        return handle instanceof InstanceSpecification;
    }

    public boolean isAInteraction(Object handle) {
        // TODO: changed for UML 2.x
        return false;
    }

    public boolean isAInteractionInstanceSet(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isAInterface(Object handle) {
        return handle instanceof Interface;
    }

    public boolean isALink(Object handle) {
        // TODO: check semantics here - tfm
        if (!(handle instanceof InstanceSpecification)) {
            return false;
        }
        List classifiers = ((InstanceSpecification) handle).getClassifiers();
        return classifiers.size() == 1
                && classifiers.get(0) instanceof Association;
    }

    public boolean isALinkEnd(Object handle) {
        // TODO: just a guess, probably not right - tfm
//        return handle instanceof LinkEndData;
        return false;
    }

    public boolean isALinkObject(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isAMessage(Object handle) {
        return handle instanceof Message;
    }

    public boolean isAMethod(Object handle) {
        // TODO: gone from UML 2
        return false;
    }

    public boolean isAModel(Object handle) {
        return handle instanceof Model;
    }

    public boolean isAModelElement(Object handle) {
        // TODO: What do we want to use as an equivalent here?
        return handle instanceof Element;
    }

    public boolean isAMultiplicity(Object handle) {
        // TODO: The UML 1.4 concept of a Multiplicity & Multiplicity Range has
        // been replaced by a single element
        return handle instanceof MultiplicityElement;
    }

    public boolean isAMultiplicityRange(Object handle) {
        return handle instanceof MultiplicityElement;
    }

    public boolean isANamedElement(Object handle) {
        return handle instanceof NamedElement;
    }

    public boolean isANamespace(Object handle) {
        return handle instanceof Namespace;
    }

    public boolean isANaryAssociation(Object handle) {
        return handle instanceof Association
                && ((Association) handle).getMemberEnds().size() > 2;
    }

    public boolean isANode(Object handle) {
        return handle instanceof Node;
    }

    public boolean isANodeInstance(Object handle) {
        // TODO: not in UML 2
        return false;
    }

    public boolean isAObject(Object handle) {
        // TODO: Double check - tfm
        return handle instanceof ObjectNode;
    }

    public boolean isAObjectFlowState(Object handle) {
        // TODO: not in UML 2 
        return false;
    }

    public boolean isAOperation(Object handle) {
        return handle instanceof Operation;
    }

    public boolean isAPackage(Object handle) {
        return handle instanceof org.eclipse.uml2.uml.Package;
    }

    public boolean isAParameter(Object handle) {
        return handle instanceof Parameter;
    }

    public boolean isAPartition(Object handle) {
        return handle instanceof ActivityPartition;
    }
    
    public boolean isAPackageImport(Object handle) {
        return handle instanceof PackageImport;
    }

    public boolean isAPrimitiveType(Object handle) {
        return handle instanceof PrimitiveType;
    }
    
    public boolean isAPseudostate(Object handle) {
        return handle instanceof Pseudostate;
    }

    public boolean isAPseudostateKind(Object handle) {
        return handle instanceof PseudostateKind;
    }

    public boolean isAReception(Object handle) {
        return handle instanceof Reception;
    }

    public boolean isARelationship(Object handle) {
        return handle instanceof Relationship;
    }

    public boolean isAReturnAction(Object handle) {
        // TODO: not implemented
        return handle instanceof Action 
            && false; // && ((Action) handle).get
    }

    public boolean isASendAction(Object handle) {
        // TODO: Do we want both here? - tfm
        return handle instanceof SendObjectAction
                || handle instanceof SendSignalAction;
    }

    public boolean isASignal(Object handle) {
        return handle instanceof Signal;
    }

    public boolean isASignalEvent(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isASimpleState(Object handle) {
        // TODO: not in UML 2
        return false;
    }

    public boolean isAState(Object handle) {
        return handle instanceof State;
    }

    public boolean isAStateMachine(Object handle) {
        return handle instanceof StateMachine;
    }

    public boolean isAStateVertex(Object handle) {
        // TODO: Changed for UML2
        return handle instanceof State;
    }

    public boolean isAStereotype(Object handle) {
        return handle instanceof Stereotype;
    }

    public boolean isAStimulus(Object handle) {
        // TODO: not implemented
        return false;
    }

    public boolean isAStructuralFeature(Object handle) {
        return handle instanceof StructuralFeature;
    }

    public boolean isAStubState(Object handle) {
        // TODO: not in UML 2
        return false;
    }

    public boolean isASubactivityState(Object handle) {
        // TODO: ActionState, CallState, and SubactivityState have been replaced
        // in UML 2.0 by explicitly modeled Actions
        return false;
    }

    public boolean isASubmachineState(Object handle) {
        // NOTE: No longer a separate type in UML 2.1
        return handle instanceof State && ((State) handle).isSubmachineState();
    }

    public boolean isASubsystem(Object handle) {
        // TODO: complete this implementation - tfm 
        return handle instanceof Component
            && false; // has <<subsystem>> stereotype
    }

    public boolean isASubsystemInstance(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isASynchState(Object handle) {
        // TODO: not in UML 2
        return false;
    }

    public boolean isATagDefinition(Object handle) {
        // TODO: TagDefinitions are gone from UML 2
        // they are now Properties of Stereotypes;
        return false;
    }

    public boolean isATaggedValue(Object handle) {
        // TODO: Changed in UML 2.x to special type of Property?
        return false;
    }

    public boolean isATemplateArgument(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isATemplateParameter(Object handle) {
        return handle instanceof TemplateParameter;
    }

    public boolean isATerminateAction(Object handle) {
        // TODO: not in UML 2
        return false;
    }

    public boolean isATimeEvent(Object handle) {
        return handle instanceof TimeEvent;
    }

    public boolean isATransition(Object handle) {
        return handle instanceof Transition;
    }

    public boolean isAUMLElement(Object handle) {
        return handle instanceof Element;
    }

    public boolean isAUninterpretedAction(Object handle) {
        // TODO: not in UML 2
        return false;
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

    public boolean isAbstract(Object handle) {
        if (handle instanceof Classifier) {
            return ((Classifier) handle).isAbstract();
        } else if (handle instanceof BehavioralFeature) {
            return ((BehavioralFeature) handle).isAbstract();
        } else if (handle instanceof Element) {
            return false;
        }
        throw new IllegalArgumentException();
    }

    public boolean isActive(Object handle) {
        return ((org.eclipse.uml2.uml.Class) handle).isActive();
    }

    public boolean isAggregate(Object handle) {
        // TODO: Not sure the semantics are an exact match here between
        // UML 1.4 Aggregate and UML 2.x Shared.
        return AggregationKind.SHARED_LITERAL.equals(((Property) handle)
                .getAggregation());
    }

    public boolean isAsynchronous(Object handle) {
        return !((CallAction) handle).isSynchronous();
    }

    public boolean isComposite(Object handle) {
        return AggregationKind.COMPOSITE_LITERAL.equals(((Property) handle)
                .getAggregation());
    }

    public boolean isConcurrent(Object handle) {
        // TODO: Only occurrence of isConcurrent in UML 2.1.1 is in index
        // it's not on the page that is indexed
        throw new NotYetImplementedException();
    }

    public boolean isConstructor(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isFrozen(Object handle) {
        return isReadOnly(handle);
    }

    public boolean isInitialized(Object handle) {
        return ((Property) handle).getDefaultValue() != null;
    }

    public boolean isInternal(Object handle) {
        if (handle instanceof Transition) {
            Object state = getState(handle);
            Object end0 = getSource(handle);
            Object end1 = getTarget(handle);
            if (end0 != null) {
                return ((state == end0) && (state == end1));
            }
        }
        throw new IllegalArgumentException();
    }

    public boolean isLeaf(Object handle) {
        if (handle instanceof RedefinableElement) {
            return ((RedefinableElement) handle).isLeaf();
        } else if (handle instanceof Element) {
            return false;
        }
        throw new IllegalArgumentException();
    }

    public boolean isNavigable(Object handle) {
        return ((Property) handle).isNavigable();
    }

    public boolean isPackage(Object handle) {
        return VisibilityKind.PACKAGE_LITERAL.equals(getVisibility(handle));
    }

    public boolean isPrimaryObject(Object handle) {
        // TODO: Moved this out of the implementation-specific piece - tfm
        // everything is primary for now (ie not reverse engineered)
        return true;
    }

    public boolean isPrivate(Object handle) {
        return VisibilityKind.PRIVATE_LITERAL.equals(getVisibility(handle));
    }

    public boolean isProtected(Object handle) {
        return VisibilityKind.PROTECTED_LITERAL.equals(getVisibility(handle));
    }

    public boolean isPublic(Object handle) {
        return VisibilityKind.PUBLIC_LITERAL.equals(getVisibility(handle));
    }

    public boolean isQuery(Object handle) {
        return ((Operation) handle).isQuery();
    }

    public boolean isRealize(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isReturn(Object handle) {
        return hasReturnParameterDirectionKind(handle);
    }

    public boolean isRoot(Object handle) {
//        return ((RedefinableElement) handle).isRoot();
        // TODO: One part of UML 2.1.1 spec says that this is as above, 
        // but it appears to be gone - tfm
        return false;
    }

    public boolean isSingleton(Object handle) {
        // TODO: this doesn't belong in the implementation specific piece - tfm
        return false;
    }

    public boolean isSpecification(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isStereotype(Object handle, String stereotypename) {
        throw new NotYetImplementedException();
    }

    public boolean isSynch(Object handle) {
        throw new NotYetImplementedException();
    }

    public boolean isTop(Object handle) {
        if (((State) handle).getContainer() == null) {
            return true;
        }
        return false;
    }

    public boolean isType(Object handle) {
        // TODO: this doesn't belong in the implementation specific piece - tfm
        return false;
    }

    public boolean isUtility(Object handle) {
        // TODO: this doesn't belong in the implementation specific piece - tfm
        return false;
    }

    private NamedElement getElementByName(Namespace ns, String name) {
        if (name == null) {
            return null;
        }
        for (NamedElement e : ns.getOwnedMembers()) {
            if (e.getName() != null && e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    
    public Element lookupIn(Object handle, String name) {
        if (!(handle instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        StringBuffer sb = new StringBuffer(name);
        Namespace ns = (Namespace) handle;

        for (;;) {
            int idx = sb.indexOf("::");

            if (idx != -1) {
                NamedElement subspace = getElementByName(ns, sb.substring(
                        0, idx));
                if (subspace instanceof Namespace) {
                    ns = (Namespace) subspace;
                    sb.delete(0, idx + 2);
                    continue;
                } else {
                    break;
                }
            }

            NamedElement e = getElementByName(ns, sb.toString());
            return e;
        }
        return null;
    }

    public String toString(Object modelElement) {
        if (modelElement instanceof MultiplicityElement) {
            return org.argouml.model.Model.getDataTypesHelper()
                    .multiplicityToString(modelElement);
        } else if (modelElement instanceof Element) {
            return getUMLClassName(modelElement) + ": " //$NON-NLS-1$
                    + getName(modelElement);
        }
        if (modelElement == null) {
            return ""; //$NON-NLS-1$
        }
        return modelElement.toString();
    }

    public boolean isReadOnly(Object handle) {
        if (!(handle instanceof StructuralFeature)) {
            throw new IllegalArgumentException();
        }
        return ((StructuralFeature) handle).isReadOnly();
    }

    public boolean isStatic(Object handle) {
        if (!(handle instanceof Feature)) {
            throw new IllegalArgumentException();
        }
        return ((Feature) handle).isStatic();
    }

    public Collection getRootElements() {
        Collection c = new ArrayList();
        if (modelImpl.getModelManagementFactory().getRootModel() != null) {
            c.add(modelImpl.getModelManagementFactory().getRootModel());
        }
        return c;
    }

    public String[] getMetatypeNames() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
        
    }

    public boolean isA(String metatypeName, Object element) {
        return getUMLClassName(element).equals(metatypeName);
    }

}
