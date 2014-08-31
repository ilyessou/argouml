/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thomas Neustupny
 *    Michiel van der Wulp
 *    Tom Morris
 *    Bob Tarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2008 The Regents of the University of California. All
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmi.reflect.InvalidObjectException;

import org.argouml.model.CoreFactory;
import org.argouml.model.CoreHelper;
import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.argouml.model.ModelCommand;
import org.argouml.model.ModelManagementHelper;
import org.argouml.model.NotImplementedException;
import org.argouml.model.mdr.UndoCoreHelperDecorator.StringSetter;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ClassifierInState;
import org.omg.uml.behavioralelements.activitygraphs.ObjectFlowState;
import org.omg.uml.behavioralelements.activitygraphs.Partition;
import org.omg.uml.behavioralelements.collaborations.AssociationEndRole;
import org.omg.uml.behavioralelements.collaborations.AssociationRole;
import org.omg.uml.behavioralelements.collaborations.ClassifierRole;
import org.omg.uml.behavioralelements.collaborations.Collaboration;
import org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet;
import org.omg.uml.behavioralelements.collaborations.Interaction;
import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.AttributeLink;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.DataValue;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.behavioralelements.commonbehavior.Reception;
import org.omg.uml.behavioralelements.commonbehavior.Signal;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.SubsystemInstance;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.Event;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.usecases.Actor;
import org.omg.uml.behavioralelements.usecases.Extend;
import org.omg.uml.behavioralelements.usecases.Include;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.behavioralelements.usecases.UseCaseInstance;
import org.omg.uml.foundation.core.Abstraction;
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
import org.omg.uml.foundation.core.Relationship;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StructuralFeature;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TemplateArgument;
import org.omg.uml.foundation.core.TemplateParameter;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.BooleanExpression;
import org.omg.uml.foundation.datatypes.CallConcurrencyKind;
import org.omg.uml.foundation.datatypes.ChangeableKind;
import org.omg.uml.foundation.datatypes.ChangeableKindEnum;
import org.omg.uml.foundation.datatypes.Expression;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.OrderingKind;
import org.omg.uml.foundation.datatypes.ParameterDirectionKind;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;
import org.omg.uml.foundation.datatypes.ProcedureExpression;
import org.omg.uml.foundation.datatypes.PseudostateKind;
import org.omg.uml.foundation.datatypes.ScopeKind;
import org.omg.uml.foundation.datatypes.ScopeKindEnum;
import org.omg.uml.foundation.datatypes.VisibilityKind;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;
import org.omg.uml.modelmanagement.ElementImport;
import org.omg.uml.modelmanagement.Subsystem;
import org.omg.uml.modelmanagement.UmlPackage;


/**
 * The Core Helper implementation for MDR.<p>
 *
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * @author Tom Morris
 * derived from NSUML implementation by:
 * @author Linus Tolke
 */
class CoreHelperMDRImpl implements CoreHelper {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(CoreHelperMDRImpl.class.getName());

    /**
     * The model implementation.
     */
    private MDRModelImplementation modelImpl;

    /**
     * Constructor.
     *
     * @param theModelImpl
     *            The MDRModelImplementation.
     */
    public CoreHelperMDRImpl(MDRModelImplementation theModelImpl) {
        super();
        modelImpl = theModelImpl;
    }

    /**
     * Return the first item from a collection using the most efficient method
     * possible. Returns null for an empty collection.
     *
     * @param c
     *            The Collection.
     * @return the first element of a Collection.
     */
    private static Object getFirstItemOrNull(Collection c) {
        if (c.size() == 0) {
            return null;
        }
        if (c instanceof List) {
            return ((List) c).get(0);
        }
        return c.iterator().next();
    }

    public boolean isSubType(Object type, Object subType) {
        if (!(type instanceof Class) || !(subType instanceof Class)) {
            throw new IllegalArgumentException("Metatypes are expected");
        }
        return ((Class) type).isAssignableFrom((Class) subType);
    }

    public Collection<GeneralizableElement> getAllSupertypes(Object cls1) {

        if (!(cls1 instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        GeneralizableElement ge = (GeneralizableElement) cls1;

        Collection<GeneralizableElement> result =
            new HashSet<GeneralizableElement>();
        try {
            Collection<GeneralizableElement> toBeAdded = getSupertypes(ge);
            do {
                Collection<GeneralizableElement> newlyAdded =
                    new HashSet<GeneralizableElement>();
                for (GeneralizableElement element : toBeAdded) {
                    newlyAdded.addAll(getSupertypes(element));
                }
                result.addAll(toBeAdded);
                toBeAdded = newlyAdded;
                toBeAdded.removeAll(result);
            } while (!toBeAdded.isEmpty());
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }

    public Collection<GeneralizableElement> getSupertypes(Object genElement) {
        Collection<GeneralizableElement> result =
            new HashSet<GeneralizableElement>();
        try {
            if (genElement instanceof GeneralizableElement) {
                for (Generalization gen : ((GeneralizableElement) genElement)
                        .getGeneralization()) {
                    result.add(gen.getParent());
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }

    public Collection<Object> getUtilizedElements(Object artifact) {
        throw new NotImplementedException("UML 1.4 artifacts have no manifestations with utilized elements");
    }

    public Collection<AssociationEnd> getAssociateEndsInh(Object classifier1) {
        if (!(classifier1 instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        Classifier classifier = (Classifier) classifier1;
        Collection<AssociationEnd> result = new ArrayList<AssociationEnd>();
        try {
            result.addAll(modelImpl.getFacade().getAssociationEnds(classifier));
            for (Generalization gen : classifier.getGeneralization()) {
                Object parent = gen.getParent();
                result.addAll(getAssociateEndsInh(parent));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public void removeFeature(Object cls, Object feature) {
        try {
            if (cls instanceof Classifier && feature instanceof Feature) {
                ((Classifier) cls).getFeature().remove(feature);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("classifier: " + cls
                + " or feature: " + feature);
    }


    public void removeLiteral(Object enu, Object literal) {
        try {
            if (enu instanceof Enumeration
                    && literal instanceof EnumerationLiteral) {
                ((Enumeration) enu).getLiteral().remove(literal);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("enumeration: " + enu
                + " or literal: " + literal);
    }


    public void setOperations(Object classifier, List operations) {
        if (classifier instanceof Classifier) {
            Classifier mclassifier = (Classifier) classifier;
            List<Feature> result =
                new ArrayList<Feature>(mclassifier.getFeature());
            for (Feature feature : mclassifier.getFeature()) {
                if (feature instanceof Operation) {
                    result.remove(feature);
                }
            }
            result.addAll(operations);
            // TODO: A minimal update strategy would be better here to
            // minimize work and events firing, but it may be better to
            // deprecate the method and force calls to manage updates
            // themselves (since they probably usually want to just add
            // or remove a single element) - tfm 20061108
            mclassifier.getFeature().clear();
            mclassifier.getFeature().addAll(result);
        }
    }


    public void setAttributes(Object classifier, List attributes) {
        if (classifier instanceof Classifier) {
            Classifier mclassifier = (Classifier) classifier;
            List<Feature> result =
                new ArrayList<Feature>(mclassifier.getFeature());
            for (Feature feature : mclassifier.getFeature()) {
                if (feature instanceof Attribute) {
                    result.remove(feature);
                }
            }
            result.addAll(attributes);
            // TODO: This should use a minimal update strategy instead of
            // removing everything and adding it again. - tfm
            mclassifier.getFeature().clear();
            mclassifier.getFeature().addAll(result);
        }
    }


    public Collection<Attribute> getAttributesInh(Object classifier) {

        if (!(classifier instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        Collection result = new ArrayList();
        try {
            result.addAll(modelImpl.getFacade().getStructuralFeatures(
                    classifier));
            for (Generalization gen : ((Classifier) classifier)
                    .getGeneralization()) {
                result.addAll(getAttributesInh(gen.getParent()));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Operation> getOperationsInh(Object classifier) {
        if (!(classifier instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        Collection<Operation> result = new ArrayList<Operation>();
        try {
            result.addAll(modelImpl.getFacade().getOperations(classifier));
            for (Generalization gen : ((Classifier) classifier)
                    .getGeneralization()) {
                result.addAll(getOperationsInh(gen.getParent()));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }



    public Collection<GeneralizableElement> getParents(
            Object generalizableElement) {
        if (!(generalizableElement instanceof GeneralizableElement)) {
            throw new IllegalArgumentException();
        }
        try {
            return getParents((GeneralizableElement) generalizableElement);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /**
     * Return the parents of a GeneralizableElement
     * @param ge generalizable element
     * @return parents of all generalizations
     */
    static Collection<GeneralizableElement> getParents(
            GeneralizableElement ge) {
        Set<GeneralizableElement> result = new HashSet<GeneralizableElement>();
        for (Generalization g : ge.getGeneralization()) {
            result.add(g.getParent());
        }
        return result;
    }

    /**
     * Return the parents of a GeneralizableElement and parents of those parents
     * all the way up the hierarchy.
     *
     * @param ge generalizable element
     * @return parents of all generalizations
     */
    static Collection<GeneralizableElement> getAllParents(
            GeneralizableElement ge) {
        Collection<GeneralizableElement> result =
            new HashSet<GeneralizableElement>(2000);
        getAllParents(result, ge);
        return result;
    }

    private static void getAllParents(
            final Collection<GeneralizableElement> result,
            final GeneralizableElement ge) {
        for (Generalization g : ge.getGeneralization()) {
            GeneralizableElement parent = g.getParent();
            result.add(parent);
            getAllParents(result, parent);
        }
    }

    /**
     * A recursive method that iterates through generalizable elements to find
     * if the model element is visible from any super namespace.
     *
     * @param element the model element to test
     * @param ge the namespace which is a generalizable element
     * @param dupCheck Used to prevent recursion continuing endlessly due to
     * cyclic generalizations in the model. This should be an empty Set
     * except when this method calls itself recursively.
     * @return true if the model element is visible from any part of the
     * generalization hierarchy.
     */
    private boolean isVisiblyOwned(
            final ModelElement element,
            final GeneralizableElement ge,
            final Set<ModelElement> dupCheck) {

        assert dupCheck != null;
        assert ge != null;
        assert element != null;

        final boolean alreadyChecked = !dupCheck.add(ge);
        if (alreadyChecked) {
            LOG.log(Level.WARNING, "Cyclic generalization found " + getFullName(ge));
            return false;
        }

        for (final Generalization g : ge.getGeneralization()) {
            final GeneralizableElement parent = g.getParent();
            if (parent instanceof Namespace
                    && isVisiblyOwned(element, (Namespace) parent)) {
                return true;
            }
        }

        for (final Generalization g : ge.getGeneralization()) {
            // Recurse into ourself for each parent
            if (isVisiblyOwned(element, g.getParent(), dupCheck)) {
                return true;
            }
        }
        return false;
    }

    private String getFullName(ModelElement elem) {
        String name = elem.getName();
        while (elem.getNamespace() != null) {
            elem = elem.getNamespace();
            name = elem.getName() + "." + name;
        }
        return name;
    }

    public List<Parameter> getReturnParameters(Object bf) {
        List<Parameter> returnParams = new ArrayList<Parameter>();
        try {
            for (Parameter parameter : ((BehavioralFeature) bf).getParameter()) {
                if (ParameterDirectionKindEnum.PDK_RETURN.equals(parameter
                        .getKind())) {
                    returnParams.add(parameter);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return returnParams;
    }

    public Object getSpecification(Object object) {
        if (!(object instanceof Method)) {
            throw new IllegalArgumentException("Not a method : " + object);
        }
        try {
            return ((Method) object).getSpecification();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    public Collection<GeneralizableElement> getSubtypes(Object cls) {
        if (!(cls instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        Collection<GeneralizableElement> result =
            new ArrayList<GeneralizableElement>();
        try {
            Collection<Generalization> gens = Model.getFacade()
                    .getSpecializations(cls);
            for (Generalization gen : gens) {
                result.add(gen.getChild());
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<BehavioralFeature> getAllBehavioralFeatures(Object element) {
        if (!(element instanceof ModelElement)) {
            throw new IllegalArgumentException();
        }
        List contents = new ArrayList();
        List<BehavioralFeature> result = new ArrayList<BehavioralFeature>();
        try {
            contents.addAll(Model.getFacade()
                    .getTaggedValuesCollection(element));
            contents.addAll(((ModelElement) element).getTemplateParameter());
            Iterator it = contents.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof Classifier) {
                    Classifier clazz = (Classifier) o;
                    if (!(clazz instanceof DataType)) {
                        for (Object o1 : clazz.getFeature()) {
                            if (o1 instanceof BehavioralFeature) {
                                result.add((BehavioralFeature) o1);
                            }
                        }
                    }
                } else {
                    // TODO: 2nd next() for single hasNext()
                    result.addAll(getAllBehavioralFeatures(it.next()));
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public List<BehavioralFeature> getBehavioralFeatures(Object clazz) {
        if (clazz instanceof Classifier) {
            List<BehavioralFeature> ret = new ArrayList<BehavioralFeature>();
            try {
                for (Object o : modelImpl.getFacade().getFeatures(clazz)) {
                    if (o instanceof BehavioralFeature) {
                        ret.add((BehavioralFeature) o);
                    }
                }
            } catch (InvalidObjectException e) {
                throw new InvalidElementException(e);
            }
            return ret;
        } else {
            throw new IllegalArgumentException("Argument is not a classifier");
        }
    }


    public Collection<Interface> getAllInterfaces(Object ns) {
        if (ns == null) {
            return Collections.EMPTY_SET;
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }
        List<Interface> result = new ArrayList<Interface>();
        for (Object o : ((Namespace) ns).getOwnedElement()) {
            if (o instanceof Namespace) {
                result.addAll(getAllInterfaces(o));
            }
            if (o instanceof Interface) {
                result.add((Interface) o);
            }
        }
        return result;
    }


    public Collection<UmlClass> getAllClasses(Object ns) {
        if (ns == null) {
            return Collections.EMPTY_SET;
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        List<UmlClass> result = new ArrayList<UmlClass>();
        try {
            for (Object o : ((Namespace) ns).getOwnedElement()) {
                if (o instanceof Namespace) {
                    result.addAll(getAllClasses(o));
                }
                if (o instanceof UmlClass) {
                    result.add((UmlClass) o);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Interface> getRealizedInterfaces(Object cls) {
        Classifier classifier = (Classifier) cls;
        if (classifier == null) {
            return Collections.emptyList();
        }
        List<Interface> result = new ArrayList<Interface>();
        // TODO: This should be using internalGetAllRealizedInterfaces()
        try {
            for (Dependency clientDependency
                    : classifier.getClientDependency()) {
                if (clientDependency instanceof Abstraction) {
                    Abstraction abstraction = (Abstraction) clientDependency;
                    for (Stereotype stereo : abstraction.getStereotype()) {
                        if (stereo != null
                                && CoreFactory.REALIZE_STEREOTYPE.equals(stereo
                                        .getName())
                                // the following should always be true
                                && stereo.getBaseClass()
                                        .contains("Abstraction")) {
                            for (Object supplier : abstraction.getSupplier()) {
                                if (supplier instanceof Interface) {
                                    result.add((Interface) supplier);
                                }
                            }
                        }
                    }
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Classifier> getExtendedClassifiers(Object clazz) {
        if (clazz == null) {
            return Collections.EMPTY_SET;
        }
        List<Classifier> result = new ArrayList<Classifier>();
        try {
            for (Object gen : modelImpl.getFacade().getGeneralizations(clazz)) {
                GeneralizableElement parent = ((Generalization) gen).getParent();
                if (parent != null) {
                    // If we were handed a Classifier to start,
                    // this must be a Classifier
                    result.add((Classifier) parent);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Object getGeneralization(Object achild, Object aparent) {
        if (!(achild instanceof GeneralizableElement)
                || !(aparent instanceof GeneralizableElement)) {
            throw new IllegalArgumentException();
        }
        GeneralizableElement child = (GeneralizableElement) achild;
        GeneralizableElement parent = (GeneralizableElement) aparent;
        try {
            Iterator it =
                modelImpl.getFacade().getGeneralizations(child).iterator();
            while (it.hasNext()) {
                Generalization gen = (Generalization) it.next();
                if (gen.getParent() == parent) {
                    return gen;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }


    public String getBody(Object comment) {
        if (comment instanceof Comment) {
            /*
             * In UML 1.3, the name is the only place to store text.
             * In UML 1.4, there is a body as well, but some tools
             * still seem to use name.
             */
            try {
                return ((Comment) comment).getBody();
            } catch (InvalidObjectException e) {
                throw new InvalidElementException(e);
            }
        }
        throw new IllegalArgumentException();
    }


    public Collection<Flow> getFlows(Object source, Object target) {
        if (!(source instanceof ModelElement)) {
            throw new IllegalArgumentException("source");
        }
        if (!(target instanceof ModelElement)) {
            throw new IllegalArgumentException("target");
        }

        List<Flow> ret = new ArrayList<Flow>();
        try {
            Collection<Flow> targetFlows = ((ModelElement) target).getTargetFlow();
            for (Flow flow : ((ModelElement) source).getSourceFlow()) {
                if (targetFlows.contains(flow)) {
                    ret.add(flow);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return ret;
    }


    public Collection<GeneralizableElement> getExtendingElements(Object clazz) {
        if (clazz == null) {
            return Collections.EMPTY_SET;
        }
        List<GeneralizableElement> list =
            new ArrayList<GeneralizableElement>();
        try {
            for (Generalization gen : (Collection<Generalization>) modelImpl
                    .getFacade().getSpecializations(clazz)) {
                GeneralizableElement client = gen.getChild();
                if (client != null) {
                    list.add(client);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return list;
    }


    public Collection<Classifier> getExtendingClassifiers(Object clazz) {
        if (clazz == null) {
            return Collections.EMPTY_SET;
        }
        if (!(clazz instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        List<Classifier> result = new ArrayList<Classifier>();
        try {
            for (Object gen : Model.getFacade().getSpecializations(clazz)) {
                GeneralizableElement client = ((Generalization) gen).getChild();
                if (client instanceof Classifier) {
                    result.add((Classifier) client);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Component> getAllComponents(Object ns) {
        if (ns == null) {
            return Collections.EMPTY_SET;
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        List<Component> result = new ArrayList<Component>();
        try {
            for (Object o : ((Namespace) ns).getOwnedElement()) {
                if (o instanceof Namespace) {
                    result.addAll(getAllComponents(o));
                }
                if (o instanceof Component) {
                    result.add((Component) o);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<DataType> getAllDataTypes(Object ns) {
        if (ns == null) {
            return Collections.EMPTY_SET;
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        List<DataType> result = new ArrayList<DataType>();
        try {
            for (Object o : ((Namespace) ns).getOwnedElement()) {
                if (o instanceof Namespace) {
                    result.addAll(getAllDataTypes(o));
                }
                if (o instanceof DataType) {
                    result.add((DataType) o);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Node> getAllNodes(Object ns) {
        if (ns == null) {
            return Collections.EMPTY_SET;
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        List<Node> result = new ArrayList<Node>();
        try {
            for (Object o : ((Namespace) ns).getOwnedElement()) {
                if (o instanceof Namespace) {
                    result.addAll(getAllNodes(o));
                }
                if (o instanceof Node) {
                    result.add((Node) o);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<Classifier> getAssociatedClassifiers(Object aclassifier) {
        Classifier classifier = (Classifier) aclassifier;
        if (classifier == null) {
            return Collections.EMPTY_SET;
        }
        List<Classifier> list = new ArrayList<Classifier>();
        try {
            for (AssociationEnd end : (Collection<AssociationEnd>) Model
                    .getFacade().getAssociationEnds(classifier)) {
                UmlAssociation assoc = end.getAssociation();
                for (AssociationEnd end2 : assoc.getConnection()) {
                    if (end2 != end) {
                        list.add(end2.getParticipant());
                    }
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return list;
    }


    public Collection<UmlAssociation> getAssociations(Object from, Object to) {
        if (!(from instanceof Classifier) || !(to instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        Set<UmlAssociation> ret = new HashSet<UmlAssociation>();
        try {
            for (AssociationEnd end : (Collection<AssociationEnd>) Model
                    .getFacade().getAssociationEnds(from)) {
                UmlAssociation assoc = end.getAssociation();
                for (AssociationEnd end2 : assoc.getConnection()) {
                    if (end2.getParticipant() == to) {
                        ret.add(assoc);
                    }
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return ret;
    }


    public Collection<Classifier> getAllClassifiers(Object namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException();
        }
        List<Classifier> result = new ArrayList<Classifier>();
        try {
            for (Object o : ((Namespace) namespace).getOwnedElement()) {
                if (o instanceof Namespace) {
                    result.addAll(getAllClassifiers(o));
                }
                if (o instanceof Classifier) {
                    result.add((Classifier) o);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public Collection<UmlAssociation> getAssociations(Object oclassifier) {
        Collection<UmlAssociation> result = new ArrayList<UmlAssociation>();
        try {
            if (oclassifier instanceof Classifier) {
                for (Object end : Model.getFacade().getAssociationEnds(
                        oclassifier)) {
                    result.add(((AssociationEnd) end).getAssociation());
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }


    public AssociationEnd getAssociationEnd(Object type, Object assoc) {
        if (!(type instanceof Classifier)) {
            throw new IllegalArgumentException();
        }
        if (!(assoc instanceof UmlAssociation)) {
            throw new IllegalArgumentException();
        }
        try {
            for (AssociationEnd end : (Collection<AssociationEnd>) Model
                    .getFacade().getAssociationEnds(type)) {
                if (((UmlAssociation) assoc).getConnection().contains(end)) {
                    return end;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }


    public Collection<Attribute> getAllAttributes(Object clazz) {
        if (clazz == null) {
            return Collections.emptySet();
        }
        if (!(clazz instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        List<Attribute> list = new ArrayList<Attribute>();
        try {
            for (Feature feature : ((Classifier) clazz).getFeature()) {
                if (feature instanceof Attribute) {
                    list.add((Attribute) feature);
                }
            }
            for (Object gen : modelImpl.getFacade().getGeneralizations(clazz)) {
                list.addAll(getAllAttributes(gen));
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return list;
    }


    public Collection<ModelElement> getAllVisibleElements(Object ns) {
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }
        try {
            return getAllVisibleElements((Namespace) ns);
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /**
     * Get all publicly visible elements in a namespace.
     * @param ns the namespace
     * @return all owned elements with a visibility of VK_PUBLIC
     */
    static Collection<ModelElement> getAllVisibleElements(Namespace ns) {
        Collection<ModelElement> result = new ArrayList<ModelElement>();
        for (ModelElement element : ns.getOwnedElement()) {
            if (VisibilityKindEnum.VK_PUBLIC.equals(element.getVisibility())) {
                result.add(element);
            }
        }
        return result;
    }


    public Object getSource(Object relationship) {
        if (!(relationship instanceof Relationship)
                && !(relationship instanceof Link)
                && !(relationship instanceof Message)
                && !(relationship instanceof AssociationEnd)) {

            throw new IllegalArgumentException("Argument "
                    + relationship.toString() + " is not " + "a relationship");

        }
        try {
            if (relationship instanceof Link) {
                Iterator it =
                    modelImpl.getFacade()
                        .getConnections(relationship).iterator();
                if (it.hasNext()) {
                    return modelImpl.getFacade().getInstance(it.next());
                } else {
                    return null;
                }
            }
            if (relationship instanceof UmlAssociation) {
                UmlAssociation assoc = (UmlAssociation) relationship;
                List<AssociationEnd> conns = assoc.getConnection();
                if (conns == null || conns.isEmpty()) {
                    return null;
                }
                return conns.get(0).getParticipant();
            }
            if (relationship instanceof Generalization) {
                Generalization gen = (Generalization) relationship;
                return gen.getChild();
            }
            if (relationship instanceof Dependency) {
                Dependency dep = (Dependency) relationship;
                Collection<ModelElement> col = dep.getClient();
                if (col.isEmpty()) {
                    return null;
                }
                return col.iterator().next();
            }
            if (relationship instanceof Flow) {
                Flow flow = (Flow) relationship;
                Collection<ModelElement> col = flow.getSource();
                if (col.isEmpty()) {
                    return null;
                }
                return col.iterator().next();
            }
            if (relationship instanceof Extend) {
                Extend extend = (Extend) relationship;
                return extend.getExtension(); // we have to follow the
                                                // arrows..
            }
            if (relationship instanceof Include) {
                Include include = (Include) relationship;
                return modelImpl.getFacade().getBase(include);
            }
            if (relationship instanceof AssociationEnd) {
                return ((AssociationEnd) relationship).getAssociation();
            }
            if (relationship instanceof Message) {
                return ((Message) relationship).getSender();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }


    public Object getDestination(Object relationship) {
        if (!(relationship instanceof Relationship)
                && !(relationship instanceof Link)
                && !(relationship instanceof Message)
                && !(relationship instanceof AssociationEnd)) {

            throw new IllegalArgumentException("Argument is not "
                    + "a relationship");
        }

        try {
            if (relationship instanceof Link) {
                Iterator it = modelImpl.getFacade()
                        .getConnections(relationship).iterator();
                if (it.hasNext()) {
                    it.next();
                    if (it.hasNext()) {
                        return modelImpl.getFacade().getInstance(it.next());
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (relationship instanceof UmlAssociation) {
                UmlAssociation assoc = (UmlAssociation) relationship;
                List<AssociationEnd> conns = assoc.getConnection();
                if (conns.size() <= 1) {
                    return null;
                }
                return conns.get(1).getParticipant();
            }
            if (relationship instanceof Generalization) {
                Generalization gen = (Generalization) relationship;
                return gen.getParent();
            }
            if (relationship instanceof Dependency) {
                Dependency dep = (Dependency) relationship;
                Collection<ModelElement> col = dep.getSupplier();
                if (col.isEmpty()) {
                    return null;
                }
                return getFirstItemOrNull(col);
            }
            if (relationship instanceof Flow) {
                Flow flow = (Flow) relationship;
                Collection<ModelElement> col = flow.getTarget();
                if (col.isEmpty()) {
                    return null;
                }
                return getFirstItemOrNull(col);
            }
            if (relationship instanceof Extend) {
                Extend extend = (Extend) relationship;
                return extend.getBase();
            }
            if (relationship instanceof Include) {
                Include include = (Include) relationship;
                return modelImpl.getFacade().getAddition(include);
            }
            if (relationship instanceof AssociationEnd) {
                return ((AssociationEnd) relationship).getParticipant();
            }
            if (relationship instanceof Message) {
                return ((Message) relationship).getReceiver();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }


    public Collection<Dependency> getDependencies(Object supplierObj,
            Object clientObj) {

        if (!(supplierObj instanceof ModelElement)
                || !(clientObj instanceof ModelElement)) {
            throw new IllegalArgumentException("invalid argument(s)");
        }

        ModelElement supplier = (ModelElement) supplierObj;
        ModelElement client = (ModelElement) clientObj;

        List<Dependency> ret = new ArrayList<Dependency>();
        try {
            Collection<Dependency> clientDependencies = client
                    .getClientDependency();
            Iterator it =
                Model.getFacade().getSupplierDependencies(supplier).iterator();
            while (it.hasNext()) {
                Dependency dep = (Dependency) it.next();
                if (clientDependencies.contains(dep)) {
                    ret.add(dep);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return ret;
    }

    public Collection<Permission> getPackageImports(Object client) {
        if (!(client instanceof Namespace)) {
            throw new IllegalArgumentException("invalid argument");
        }
        List<Permission> result = new ArrayList<Permission>();
        try {
            for (Dependency dependency : ((ModelElement) client)
                    .getClientDependency()) {
                if (dependency instanceof Permission
                        && hasImportStereotype((Permission) dependency)) {
                    result.add((Permission) dependency);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return result;
    }

    /**
     * Return true if the given Permission has any of our acceptable
     * import stereotypes <code>import</code> or <code>access</code>.
     * <p>
     * NOTE: We don't currently consider the <code>friend</code> stereotype.
     *
     * @param permission Permission to test
     * @return true if this is an import permission
     */
    private boolean hasImportStereotype(Permission permission) {
        return (Model.getExtensionMechanismsHelper().hasStereotype(permission,
                ModelManagementHelper.IMPORT_STEREOTYPE)
                || Model.getExtensionMechanismsHelper().hasStereotype(
                        permission, ModelManagementHelper.ACCESS_STEREOTYPE)
                        // TODO: Do we want to <<friend>> stereotypes too?
//                        || Model.getExtensionMechanismsHelper().hasStereotype(
//                        permission, ModelManagementHelper.FRIEND_STEREOTYPE)
                        );
    }

    public Permission getPackageImport(Object supplier, Object client) {
        for (Dependency dependency : getDependencies(supplier, client)) {
            if (dependency instanceof Permission
                    && Model.getExtensionMechanismsHelper()
                            .hasStereotype(dependency,
                                    ModelManagementHelper.IMPORT_STEREOTYPE)) {
                return (Permission) dependency;
            }
        }
        return null;
    }


    public Collection getRelationships(Object source, Object dest) {
        Set ret = new HashSet();
        if (!(source instanceof ModelElement)) {
            throw new IllegalArgumentException("source");
        }
        if (!(dest instanceof ModelElement)) {
            throw new IllegalArgumentException("dest");
        }

        try {
            ret.addAll(getFlows(source, dest));
            ret.addAll(getFlows(dest, source));
            ret.addAll(getDependencies(source, dest));
            ret.addAll(getDependencies(dest, source));
            if (source instanceof GeneralizableElement
                    && dest instanceof GeneralizableElement) {
                ret.add(getGeneralization(source, dest));
                ret.add(getGeneralization(dest, source));
                if (source instanceof Classifier
                        && dest instanceof Classifier) {
                    ret.addAll(getAssociations(source, dest));
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return ret;
    }


    public boolean isValidNamespace(Object mObj, Object nsObj) {

        if (!(mObj instanceof ModelElement) || !(nsObj instanceof Namespace)) {
            return false;
        }

        ModelElement modelElement = (ModelElement) mObj;
        Namespace ns = (Namespace) nsObj;

        try {

            if (modelElement == ns) {
                return false;
            }

            if (ns instanceof Interface
                    || ns instanceof Actor
                    || ns instanceof DataType
                    || ns instanceof DataValue
                    || ns instanceof NodeInstance
                    || ns instanceof Signal
                    // || ns instanceof UseCase // see comment below
                    || ns instanceof UseCaseInstance
                    || ns instanceof ClassifierInState) {
                return false;
            } else if (ns instanceof UseCase
                    && modelElement instanceof Classifier) {
                /*
                 * NOTE: Although WFR #3 in section 2.11.3.5 UseCase of the
                 * UML 1.4 spec says "A UseCase cannot contain any
                 * Classifiers," the OCL is actually self.contents->isEmpty
                 * which would seem to imply it can't contain any elements
                 * - tfm - 20060416
                 */
                return false;
            } else if (ns instanceof Component) {
                return (modelElement instanceof Component
                        && modelElement != ns);
            } else if (ns instanceof ComponentInstance) {
                return (modelElement instanceof ComponentInstance
                        && modelElement != ns);
            } else if (ns
                    instanceof
                    org.omg.uml.behavioralelements.commonbehavior.Object) {
                // Made following changes from OCL in UML 1.4 section 2.9.3.16:
                //   CollaborationInstance -> CollaborationInstanceSet
                //   Stimuli -> Stimulus
                if (!(modelElement
                        instanceof
                        org.omg.uml.behavioralelements.commonbehavior.Object
                        || modelElement instanceof DataValue
                        || modelElement instanceof Link
                        || modelElement instanceof UseCaseInstance
                        || modelElement instanceof CollaborationInstanceSet
                        || modelElement instanceof Stimulus)) {
                    return false;
                }
            } else if (ns instanceof SubsystemInstance) {
                // Made following change from OCL in UML 1.4 section 2.9.3.22:
                //   CollaborationInstance -> CollaborationInstanceSet
                if (!(modelElement
                        instanceof
                        org.omg.uml.behavioralelements.commonbehavior.Object
                        || modelElement instanceof DataValue
                        || modelElement instanceof Link
                        || modelElement instanceof UseCaseInstance
                        || modelElement instanceof CollaborationInstanceSet
                        || modelElement instanceof SubsystemInstance
                        || modelElement instanceof Stimulus)) {
                    return false;
                }
            } else if (ns instanceof Subsystem) {
                if (!(modelElement instanceof UmlPackage
                        || modelElement instanceof UmlClass
                        || modelElement instanceof DataType
                        || modelElement instanceof Interface
                        || modelElement instanceof UseCase
                        || modelElement instanceof Actor
                        || modelElement instanceof Subsystem
                        || modelElement instanceof Signal
                        || modelElement instanceof UmlAssociation
                        || modelElement instanceof Generalization
                        || modelElement instanceof Dependency
                        || modelElement instanceof Constraint
                        || modelElement instanceof Collaboration
                        || modelElement instanceof StateMachine
                        || modelElement instanceof Stereotype)) {
                    return false;
                }
            } else if (modelElement instanceof ClassifierRole) {
                return ns instanceof Collaboration;
            } else if (ns instanceof Collaboration) {
                /*
                 * Although not represented in the OCL (or our Java), the
                 * English text of WFR #4 of Section 2.10.3.4 in the UML 1.4
                 * spec is more restrictive - "[4] A Collaboration may only
                 * contain ClassifierRoles and AssociationRoles, the
                 * Generalizations and the Constraints between them, and
                 * Actions used in the Collaboration's Interactions."
                 */
                if (!(modelElement instanceof ClassifierRole
                        || modelElement instanceof AssociationRole
                        || modelElement instanceof Generalization
                        || modelElement instanceof Action
                        || modelElement instanceof Constraint)) {
                    return false;
                }
            } else if (ns instanceof UmlPackage) {
                // A Profile is a special package having the <<profile>>
                // stereotype which can only contain the following types
                if (isProfilePackage(ns)) {
                    if (!(modelElement instanceof Stereotype
                            || modelElement instanceof Constraint
                            || modelElement instanceof TagDefinition
                            || modelElement instanceof DataType)) {
                        return false;
                    }
                } else {
                    if (!(modelElement instanceof UmlPackage
                            || modelElement instanceof Classifier
                            || modelElement instanceof UmlAssociation
                            || modelElement instanceof Generalization
                            || modelElement instanceof Dependency
                            /* The next 2 needed for issue 2148: */
                            || modelElement instanceof Extend
                            || modelElement instanceof Include
                            || modelElement instanceof Constraint
                            || modelElement instanceof Collaboration
                            || modelElement instanceof StateMachine
                            || modelElement instanceof Stereotype)) {
                        return false;
                    }
                }
            } else if (ns instanceof UmlClass) {
                if (!(modelElement instanceof UmlClass
                        || modelElement instanceof UmlAssociation
                        || modelElement instanceof Generalization
                        || modelElement instanceof UseCase
                        || modelElement instanceof Constraint
                        || modelElement instanceof Dependency
                        || modelElement instanceof Collaboration
                        // Having StateMachine here would be non-standard.
                        // See issue 4284. Hence removed:
                        /* || modelElement instanceof StateMachine */
                        || modelElement instanceof DataType
                        || modelElement instanceof Interface)) {
                    return false;
                }
            } else if (ns instanceof ClassifierRole) {
                ClassifierRole cr = (ClassifierRole) ns;
                if (!(cr.getAvailableContents().contains(modelElement) || cr.
                        getAvailableFeature().contains(modelElement))) {
                    return false;
                }
            }
            if (modelElement instanceof StructuralFeature) {
                if (!isValidNamespace((StructuralFeature) modelElement, ns)) {
                    return false;
                }
                // TODO: Issue 6144. It is proposed to allow any paclage to return as a potential
                // namespace. Commenting out until this is reviewed and clearly accepted by all.
//            } else if (modelElement instanceof GeneralizableElement) {
//                GeneralizableElement ge = (GeneralizableElement) modelElement;
//                if (!isValidNamespace(ge, ns)) {
//                    return false;
//                }
            } else if (modelElement instanceof Generalization) {
                if (!isValidNamespace((Generalization) modelElement, ns)) {
                    return false;
                }
            }
            if (modelElement instanceof UmlAssociation) {
                if (!isValidNamespace((UmlAssociation) modelElement, ns)) {
                    return false;
                }
            } else if (modelElement instanceof Collaboration) {
                if (!isValidNamespace((Collaboration) modelElement, ns)) {
                    return false;
                }
            }
            return true;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    private boolean isProfilePackage(Namespace ns) {
        // A Profile is a special package having the <<profile>> stereotype
        return Model.getFacade().isStereotype(ns, "profile");
    }

    /**
     * The base of a AssociationRole or ClassifierRole should be contained in
     * the given Namespace. If no base is set (yet), then allow any namespace.
     *
     * @param collab the given collaboration
     * @param ns the given candidate namespace
     * @return true if the given namespace may contain the collaboration
     */
    private boolean isValidNamespace(Collaboration collab, Namespace ns) {
        for (ModelElement m : collab.getOwnedElement()) {
            if (m instanceof ClassifierRole) {
                ClassifierRole role = (ClassifierRole) m;
                for (Classifier base : role.getBase()) {
                    if (!ns.getOwnedElement().contains(base)) {
                        return false;
                    }
                }
            } else if (m instanceof AssociationRole) {
                AssociationRole ar = (AssociationRole) m;
                UmlAssociation a = ar.getBase();
                if (a != null && !ns.getOwnedElement().contains(a)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidNamespace(Generalization gen, Namespace ns) {
        // TODO: Implement following WFR for GeneralizableElements
        // [4] The parent must be included in the Namespace of the
        //     GeneralizableElement.
        //       self.generalization->forAll(g |
        //           self.namespace.allContents->includes(g.parent) )
//        return ModelManagementHelperMDRImpl.getContents(ns).contains(
//                gen.getParent());

        // There actually don't appear to be any restrictions on the
        // namespace for a Generalization.  The UML 1.4 WFR 2.5.3.20 #3
        // refers to GeneralizableElements, not Generalizations - tfm
        return true;

        // These old checks don't appear to be supported by the
        // UML 1.4 spec. - tfm 20080514
//      if (gen.getParent() == null || gen.getChild() == null) {
//      return true;
//  }
//        Namespace ns1 = gen.getParent().getNamespace();
//        Namespace ns2 = gen.getChild().getNamespace();
//        if (ns == getFirstSharedNamespace(ns1, ns2)) {
//            return true;
//        }
//        return false;
    }

    private boolean isValidNamespace(StructuralFeature struc, Namespace ns) {
        // Technically this is legal, but a StructuralFeature should probably
        // only ever have an owner instead of a namespace. - tfm
        if (struc.getType() == null || struc.getOwner() == null) {
            return true;
        }
        /*
         * The following from the original NSUML implemenetation was attempting
         * to implement WFR #1 from section 2.5.3.32 of the UML 1.4 spec, but if
         * there is an owner set, no namespace is valid. The checks for this
         * WFR, if desired, need to go in setOwner() and setType() - tfm
         */
//        return struc.getOwner().getNamespace().getOwnedElement().contains(
//                struc.getType());
        return false;
    }

    private boolean isValidNamespace(
            final UmlAssociation assoc,
            final Namespace ns) {
        for (AssociationEnd end : assoc.getConnection()) {
            if (!isVisible(end.getParticipant(), ns)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if the given element is visible from this namespace.
     * <blockquote>The OCL that this is intended to check is this:
     * self.allConnections->forAll(r | self.namespace.allContents->includes
        (r.participant) ) or
        self.allConnections->forAll(r | self.namespace.allContents->excludes
        (r.participant) implies
        self.namespace.clientDependency->exists (d |
        d.oclIsTypeOf(Permission) and
        d.stereotype.name = 'access' and
        d.supplier.oclAsType(Namespace).ownedElement->select (e |
        e.elementOwnership.visibility =
        #public)->includes (r.participant) or
        d.supplier.oclAsType(GeneralizableElement).
        allParents.oclAsType(Namespace).ownedElement->select (e |
        e. elementOwnership.visibility =
        #public)->includes (r.participant) or
        d.supplier.oclAsType(Package).allImportedElements->select (e |
        e. elementImport.visibility =
        #public) ->includes (r.participant) ) )
        </blockquote>
     * <p>
     * NOTE: This is very similar to the logic in
     * {@link ModelManagementHelperMDRImpl#getAllImportedElements(Object)}
     * which returns a collection of imported elements.  Here we quit as soon
     * as we find the element that we're testing for (and we don't deal with
     * the <code>friend</code> or <code>access</code> stereotypes.
     *
     * @param ns
     *                The namespace to check visibility from
     * @param element
     *                the element to check for visibility
     * @return Return true if the given element is visible from this namespace.
     */
    private boolean isVisible(ModelElement element, Namespace ns) {
        if (ns == null || element == null) {
            return false;
        }

        //  self.allConnections->forAll(r
        // | self.namespace.allContents->includes(r.participant) )
        Collection nsAllContents =
            modelImpl.getModelManagementHelper().getAllContents(ns);
        if (nsAllContents.contains(element)) {
            return true;
        }
        // or
        //      self.allConnections->forAll(
        //              r | self.namespace.allContents->excludes
        //      (r.participant) implies
        //      self.namespace.clientDependency->exists (d |
        //      d.oclIsTypeOf(Permission) and
        //      d.stereotype.name = 'access' and

        // TODO: this actually returns permissions with stereotypes
        // of both <<access>> and <<import>> when the spec calls for
        // only the former, but that seems to give different semantics
        // to the way package imports work.  Review to see which is wrong.
        final Collection<Permission> permissions = getPackageImports(ns);
        for (Permission imp : permissions) {
            final Collection<ModelElement> suppliers = imp.getSupplier();
            for (ModelElement me : suppliers) {
                //  d.supplier.oclAsType(Namespace).ownedElement->select (e |
                //  e.elementOwnership.visibility =
                //  #public)->includes (r.participant) or
                if (me instanceof Namespace
                        && isVisiblyOwned(element, (Namespace) me)) {
                    return true;
                }
                //  d.supplier.oclAsType(GeneralizableElement).
                //  allParents.oclAsType(Namespace).ownedElement->select (e |
                //              e. elementOwnership.visibility =
                //                      #public)->includes (r.participant) or
                if (me instanceof GeneralizableElement) {

                    // TODO: Performance. Consider instantiating this just
                    // once outside the for loops and clear at this point
                    // instead.
                    final Set<ModelElement> dupCheck =
                        new HashSet<ModelElement>(10);

                    if (isVisiblyOwned(
                                element,
                                (GeneralizableElement) me,
                                dupCheck)) {
                        return true;
                    }
                }
                //  d.supplier.oclAsType(Package).allImportedElements->select (
                //                  e | e. elementImport.visibility =
                //                      #public) ->includes (r.participant) ) )
                if (me instanceof UmlPackage) {
                    final Collection<ElementImport> imports =
                            ((UmlPackage) me).getElementImport();
                    for (ElementImport ei : imports) {
                        if (element.equals(ei.getImportedElement())
                                && VisibilityKindEnum.VK_PUBLIC.equals(ei
                                        .getVisibility())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if ModelElement is owned by the given Namespace and it is
     * publicly visible.
     *
     * @param me ModelElement
     * @param ns Namespace
     * @return true if ModelElement is owned by the given Namespace and it is
     *         publicly visible.
     */
    private boolean isVisiblyOwned(ModelElement me, Namespace ns) {
        return ns.getOwnedElement().contains(me)
                && VisibilityKindEnum.VK_PUBLIC.equals(me.getVisibility());
    }

    /**
     * This code tests WFR [4] of a GeneralizableElement,
     * according OMG UML 1.4.2 standard page 59:
     *
     * [4]The parent must be included in the Namespace
     * of the GeneralizableElement.
     * self.generalization->forAll(g |
     *   self.namespace.allContents->includes(g.parent) )
     */
// TODO: Issue 6144. It is proposed to allow any paclage to return as a potential
// namespace. Commenting out until this is reviewed and clearly accepted by all.

//    private boolean isValidNamespace(
//            GeneralizableElement generalizableElement,
//            Namespace namespace) {
//
//        Collection<Generalization> generalizations =
//            generalizableElement.getGeneralization();
//
//        ModelManagementHelperMDRImpl modelManagementHelper =
//            (ModelManagementHelperMDRImpl) modelImpl.getModelManagementHelper();
//
//        for (Generalization generalization : generalizations) {
//            final GeneralizableElement parent = generalization.getParent();
//            final Set<ModelElement> results = new HashSet<ModelElement>(2000);
//            final Set<ModelElement> dupCheck = new HashSet<ModelElement>(2000);
//            modelManagementHelper.getAllContents(results, namespace, dupCheck);
//            if (!results.contains(parent)) {
//                return false;
//            }
//        }
//        return true;
//    }


    public Namespace getFirstSharedNamespace(Object ns1, Object ns2) {
        if (ns1 == null || ns2 == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (!(ns1 instanceof Namespace)) {
            throw new IllegalArgumentException(
                    "Expecting a Namespace argument. Got a "
                            + ns1.getClass().getName());
        }
        if (!(ns2 instanceof Namespace)) {
            throw new IllegalArgumentException(
                    "Expecting a Namespace argument. Got a "
                            + ns2.getClass().getName());
        }
        if (ns1 == ns2) {
            return (Namespace) ns1;
        }

        try {
            // Get the namespace hierarchy for each element
            Iterator<Namespace> path1 = getPath((Namespace) ns1).iterator();
            Iterator<Namespace> path2 = getPath((Namespace) ns2).iterator();

            // Traverse the lists looking for the last (innermost) match
            Namespace lastMatch = null;
            while (path1.hasNext() && path2.hasNext()) {
                Namespace element = path1.next();
                if (element != path2.next()) {
                    return lastMatch;
                }
                lastMatch = element;
            }
            return lastMatch;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }


    /*
     * Return a list of namespaces enclosing this element.
     * The list is ordered outer to inner. i.e. it starts at the root model.
     */
    private List<Namespace> getPath(Namespace element) {
        LinkedList<Namespace> path = new LinkedList<Namespace>();
        path.add(element);
        Namespace ns = element.getNamespace();
        while (ns != null) {
            path.addFirst(ns);
            ns = ns.getNamespace();
        }
        return path;
    }


    public Collection<Namespace> getAllPossibleNamespaces(Object modelElement,
            Object model) {
        LOG.log(Level.FINE, "getAllPossibleNamespaces start");
        ModelElement m = (ModelElement) modelElement;
        Collection<Namespace>  ret = new HashSet<Namespace> ();
        if (m == null) {
            LOG.log(Level.FINE, "getAllPossibleNamespaces end");
            return ret;
        }

        try {
            if (isValidNamespace(m, model)) {
                ret.add((Namespace) model);
            }
            Collection<Namespace> namespaces = modelImpl
                    .getModelManagementHelper().getAllModelElementsOfKind(
                            model, Namespace.class);
            for (Namespace ns : namespaces) {
                if (isValidNamespace(m, ns)) {
                    ret.add(ns);
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }

        // This is an expensive method that we should ensure is called
        // rarely. Hence info level to track easily.
        LOG.log(Level.FINE,
                "getAllPossibleNamespaces returns {0} items", ret.size() );
        return ret;
    }


    public Collection<GeneralizableElement> getChildren(Object o) {
        if (o instanceof GeneralizableElement) {
            Collection<GeneralizableElement> col =
                new ArrayList<GeneralizableElement>();
            Collection<Generalization> generalizations = new ArrayList<Generalization>();
            try {
                if (o instanceof GeneralizableElement) {
                    Collection<Generalization> specializations =
                        Model.getFacade().getSpecializations(o);
                    for (Generalization specialization : specializations) {
                        getChildren(col, specialization, generalizations);
                    }
                }
            } catch (InvalidObjectException e) {
                throw new InvalidElementException(e);
            }
            return col;
        }
        throw new IllegalArgumentException("Illegal arguments: " + o);
    }

    /**
     * Adds all children recursively to the Collection in the first argument.
     * The algorithm assumes that there is a cycle when a node has been visited
     * twice using already known generalizations.
     *
     * @param currentChildren
     *            collection to collect them in.
     * @param gen
     *            element whose children are added.
     * @param generalizations
     *            the list of already traversed generalizations.
     * @throws IllegalStateException
     *             if there is a circular reference.
     */
    private void getChildren(Collection<GeneralizableElement> currentChildren,
            Generalization gen, Collection<Generalization> generalizations) {
        GeneralizableElement child = gen.getChild();
        if (currentChildren.contains(child) && generalizations.contains(gen)) {
            throw new IllegalStateException("Circular inheritance occured.");
        } else {
            currentChildren.add(child);
            generalizations.add(gen);
        }
        Collection<Generalization> specializations =
            Model.getFacade().getSpecializations(child);
        for (Generalization specialization : specializations) {
            getChildren(currentChildren, specialization, generalizations);
        }
    }


    public Collection getAllRealizedInterfaces(Object o) {
        try {
            return internalGetAllRealizedInterfaces(o, new ArrayList(),
                    new HashSet());
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /**
     * Helper method for getAllRealizedInterfaces.
     *
     * @param o
     * @param col
     * @param visited
     * @return Collection
     */
    private Collection internalGetAllRealizedInterfaces(Object o,
            Collection col, Set visited) {
        visited.add(o);
        if (o != null) {
            if (o instanceof Classifier) {
                Classifier clazz = (Classifier) o;
                for (Dependency dependency : clazz.getClientDependency()) {
                    Stereotype stereo =
                        (Stereotype) getFirstItemOrNull(
                                dependency.getStereotype());
                    if (dependency instanceof Abstraction && stereo != null
                            && CoreFactory.REALIZE_STEREOTYPE.equals(stereo
                                    .getName())
                            && "Abstraction".equals(stereo.getBaseClass())) {

                        col.addAll(dependency.getSupplier());
                    }
                }
                for (Object supertype : getSupertypes(o)) {
                    if (!visited.contains(supertype)) {
                        internalGetAllRealizedInterfaces(supertype, col,
                                visited);
                    }
                }
            }
        }
        return col;
    }


    public final boolean hasCompositeEnd(Object association) {
        if (!(association instanceof UmlAssociation)) {
            throw new IllegalArgumentException();
        }

        UmlAssociation association1 = (UmlAssociation) association;
        try {
            for (AssociationEnd end : association1.getConnection()) {
                if (end.getAggregation() == AggregationKindEnum.AK_COMPOSITE) {
                    return true;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return false;
    }


    public final boolean equalsAggregationKind(Object associationEnd,
            String kindType) {
        if (!(associationEnd instanceof AssociationEnd)) {
            throw new IllegalArgumentException();
        }

        AssociationEnd associationEnd1 = (AssociationEnd) associationEnd;

        // TODO: This should either be extended to support all AggreationKinds
        // or it should be simplified/removed from the API - tfm - 20070331
        if (kindType.equals("composite")) {
            return AggregationKindEnum.AK_COMPOSITE.equals(associationEnd1.
                    getAggregation());
        } else {
            throw new IllegalArgumentException("kindType: " + kindType
                    + " not supported");
        }
    }


    public void removeAnnotatedElement(Object handle, Object me) {
        if (handle instanceof Comment && me instanceof ModelElement) {
            try {
                if (((Comment) handle).getAnnotatedElement().contains(me)) {
                    ((Comment) handle).getAnnotatedElement().remove(me);
                }
            } catch (InvalidObjectException e) {
                throw new InvalidElementException(e);
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or model element: " + me);
    }


    public void removeClientDependency(Object handle, Object dep) {
        try {
            if (handle instanceof ModelElement && dep instanceof Dependency) {
                ModelElement me = (ModelElement) handle;
                Collection deps = me.getClientDependency();
                if (deps != null && deps.contains(dep)) {
                    deps.remove(dep);
                }
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException();
    }


    public void removeConnection(Object handle, Object connection) {
        try {
            if (handle instanceof UmlAssociation
                    && connection instanceof AssociationEnd) {
                ((UmlAssociation) handle).getConnection().remove(connection);
                return;
            }
            if (handle instanceof Link && connection instanceof LinkEnd) {
                ((Link) handle).getConnection().remove(connection);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or connection: " + connection);

    }


    public void removeConstraint(Object handle, Object cons) {
        try {
            if (handle instanceof ModelElement && cons instanceof Constraint) {
                ((ModelElement) handle).getConstraint().remove(cons);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or cons: "
                + cons);
    }


    public void removeOwnedElement(Object handle, Object value) {
        try {
            if (handle instanceof Namespace && value instanceof ModelElement) {
                ModelElement elem = (ModelElement) value;
                if (!(elem.getNamespace().equals(handle))) {
                    throw new IllegalStateException(
                        "ModelElement isn't in Namespace");
                }
                elem.setNamespace(null);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }


    public void removeParameter(Object handle, Object parameter) {
        try {
            if (parameter instanceof Parameter) {
                if (handle instanceof ObjectFlowState) {
                    ((ObjectFlowState) handle).getParameter().remove(
                            parameter);
                    return;
                }
                if (handle instanceof Event) {
                    ((Event) handle).getParameter().remove(parameter);
                    return;
                }
                if (handle instanceof BehavioralFeature) {
                    ((BehavioralFeature) handle).getParameter().remove(
                            parameter);
                    return;
                }
                if (handle instanceof Classifier) {
                    ((Parameter) parameter).setType(null);
                    return;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }


    public void removeQualifier(Object handle, Object qualifier) {
        try {
            if (qualifier instanceof Attribute) {
                if (handle instanceof AssociationEnd) {
                    ((AssociationEnd) handle).getQualifier().remove(
                            qualifier);
                    return;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or qualifier: " + qualifier);
    }


    public void removeSourceFlow(Object handle, Object flow) {
        try {
            if (handle instanceof ModelElement && flow instanceof Flow) {
                ((ModelElement) handle).getSourceFlow().remove(flow);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or flow: "
                + flow);
    }


    public void removeSupplierDependency(Object supplier, Object dependency) {
        try {
            if (supplier instanceof ModelElement
                    && dependency instanceof Dependency) {
                ((Dependency) dependency).getSupplier().remove(supplier);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("supplier: " + supplier
                + " or dependency: " + dependency);
    }


    public void removeTargetFlow(Object handle, Object flow) {
        try {
            if (handle instanceof ModelElement && flow instanceof Flow) {
                ((ModelElement) handle).getTargetFlow().remove(flow);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or flow: "
                + flow);
    }


    public void removeTemplateArgument(Object handle, Object argument) {
        try {
            if (argument instanceof TemplateArgument) {
                if (handle instanceof Binding) {
                    ((Binding) handle).getArgument().remove(argument);
                    return;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + argument);
    }


    public void removeTemplateParameter(Object handle, Object parameter) {
        try {
            if (parameter instanceof TemplateParameter) {
                if (handle instanceof ModelElement) {
                    ((ModelElement) handle).getTemplateParameter().remove(
                            parameter);
                    return;
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }

    public void addAnnotatedElement(Object comment, Object annotatedElement) {
        if (comment instanceof Comment
                && annotatedElement instanceof ModelElement) {
            ((Comment) comment).getAnnotatedElement().add((ModelElement) annotatedElement);
            return;
        }
        throw new IllegalArgumentException("comment: " + comment
                + " or annotatedElement: " + annotatedElement);
    }


    public void addClient(Object handle, Object element) {
        if (handle instanceof Dependency && element instanceof ModelElement) {
            ((Dependency) handle).getClient().add((ModelElement) element);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or element: " + element);
    }


    public void addClientDependency(Object handle, Object dependency) {
        if (handle instanceof ModelElement
                && dependency instanceof Dependency) {
            ((ModelElement) handle).getClientDependency().add(
                    (Dependency) dependency);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or dependency: " + dependency);
    }


    public void addComment(Object element, Object comment) {
        if (element instanceof ModelElement && comment instanceof Comment) {
            ((ModelElement) element).getComment().add((Comment) comment);
            return;
        }
        throw new IllegalArgumentException("element: " + element);
    }


    public void addConnection(Object handle, Object connection) {
        if (handle instanceof UmlAssociation
                && connection instanceof AssociationEnd) {
            List<AssociationEnd> ends =
                ((UmlAssociation) handle).getConnection();
            ends.add((AssociationEnd) connection);
            // UML 1.4 WFR 2.5.3.1 #3
            if (ends.size() >= 3) {
                for (AssociationEnd end : ends) {
                    end.setAggregation(AggregationKindEnum.AK_NONE);
                }
            }
            return;
        }
        if (handle instanceof Link && connection instanceof LinkEnd) {
            ((Link) handle).getConnection().add((LinkEnd) connection);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or connection: " + connection);
    }


    public void addConnection(Object handle, int position, Object connection) {
        if (handle instanceof UmlAssociation
                && connection instanceof AssociationEnd) {
            ((UmlAssociation) handle).getConnection().add(position,
                    (AssociationEnd) connection);
            List<AssociationEnd> ends =
                ((UmlAssociation) handle).getConnection();
            // UML 1.4 WFR 2.5.3.1 #3 - no aggregation for N-ary associations
            if (ends.size() >= 3) {
                for (AssociationEnd end : ends) {
                    end.setAggregation(AggregationKindEnum.AK_NONE);
                }
            }
            return;
        }
        /* Strange, but the Link.getConnection()
         * returns a Collection, not a List!
         * This is a bug, compared to the UML standard (IMHO, mvw).
         * Hence, the LinkEnd is added to the end instead... */
        if (handle instanceof Link && connection instanceof LinkEnd) {
            ((Link) handle).getConnection().add((LinkEnd) connection);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or connection: " + connection);
    }


    public void addConstraint(Object handle, Object mc) {
        if (handle instanceof ModelElement && mc instanceof Constraint) {
            ((ModelElement) handle).getConstraint().add((Constraint) mc);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or mc: "
                + mc);
    }


    public void addDeploymentLocation(Object handle, Object node) {
        if (handle instanceof Component && node instanceof Node) {
            ((Component) handle).getDeploymentLocation().add((Node) node);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or node: "
                + node);
    }


    public void addElementResidence(Object handle, Object node) {
        // TODO: This is ambiguous as to whether it should be adding a container
        // or resident.
        if (handle instanceof ModelElement
                && node instanceof ElementResidence) {
            ((ElementResidence) node).setResident((ModelElement) handle);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or node: "
                + node);
    }


    public void removeElementResidence(Object handle, Object node) {
        try {
            if (handle instanceof ModelElement
                    && node instanceof ElementResidence) {
                ((ElementResidence) node).setResident(null);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or node: "
                + node);
    }

    /**
     * Get ElementResidences.
     *
     * TODO: Unused.  Should be added to Facade? - tfm
     *
     * @param handle A modelElement
     * @return Collection The ElementResidence for this model element
     */
    public Collection<ElementResidence> getElementResidence(Object handle) {
        try {
            if (handle instanceof ModelElement) {
                return ((org.omg.uml.UmlPackage) ((ModelElement) handle)
                        .refOutermostPackage()).getCore()
                        .getAResidentElementResidence().getElementResidence(
                                (ModelElement) handle);
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void addFeature(Object handle, int index, Object f) {
        if (handle instanceof Classifier && f instanceof Feature) {
            ((Classifier) handle).getFeature().add(index, (Feature) f);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or f: " + f);
    }


    public void addLiteral(Object handle, int index, Object literal) {
        if (handle instanceof Enumeration
                && literal instanceof EnumerationLiteral) {
            ((Enumeration) handle).getLiteral().add(index,
                    (EnumerationLiteral) literal);
            return;
        }
        throw new IllegalArgumentException("enumeration: " + handle
                + " or literal: " + literal);
    }


    public void addFeature(Object handle, Object f) {
        if (handle instanceof Classifier && f instanceof Feature) {
            ((Classifier) handle).getFeature().add((Feature) f);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void addLink(Object handle, Object link) {
        if (handle instanceof UmlAssociation && link instanceof Link) {
            ((Link) link).setAssociation((UmlAssociation) handle);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or link: "
                + link);
    }

    public void addManifestation(Object handle, Object manifestation) {
        throw new NotImplementedException( "UML 1.4 has no manifestations");
    }

    public void addMethod(Object handle, Object m) {
        if (handle instanceof Operation && m instanceof Method) {
            ((Method) m).setVisibility(((Operation) handle).getVisibility());
            ((Method) m).setOwnerScope(((Operation) handle).getOwnerScope());
            ((Method) m).setSpecification((Operation) handle);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or m: " + m);
    }


    public void addOwnedElement(Object handle, Object me) {
        if (handle instanceof Namespace && me instanceof ModelElement) {
            Namespace ns = (Namespace) handle;
            ModelElement elem = (ModelElement) me;
            elem.setNamespace(ns);
            ns.getOwnedElement().add(elem);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or me: "
                + me);
    }


    public void addParameter(Object handle, int index, Object parameter) {
        if (parameter instanceof Parameter) {
            if (handle instanceof Event) {
                ((Event) handle).getParameter().add(index,
                        (Parameter) parameter);
                return;
            }
            if (handle instanceof BehavioralFeature) {
                ((BehavioralFeature) handle).getParameter().add(index,
                        (Parameter) parameter);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }


    public void addParameter(Object handle, Object parameter) {
        if (parameter instanceof Parameter) {
            if (handle instanceof ObjectFlowState) {
                ((ObjectFlowState) handle).getParameter().add(
                        (Parameter) parameter);
                return;
            }
            if (handle instanceof Event) {
                ((Event) handle).getParameter().add((Parameter) parameter);
                return;
            }
            if (handle instanceof BehavioralFeature) {
                ((BehavioralFeature) handle).getParameter().add(
                        (Parameter) parameter);
                return;
            }
            if (handle instanceof Classifier) {
                ((Parameter) parameter).setType((Classifier) handle);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }


    public void addQualifier(Object handle, int index, Object qualifier) {
        if (qualifier instanceof Attribute) {
            if (handle instanceof AssociationEnd) {
                ((AssociationEnd) handle).getQualifier().add(index,
                        (Attribute) qualifier);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or qualifier: " + qualifier);
    }


    public void addRaisedSignal(Object handle, Object sig) {
        if (sig instanceof Signal) {
            if (handle instanceof Message) {
                ((org.omg.uml.UmlPackage) ((Signal) sig)
                        .refOutermostPackage()).getCommonBehavior()
                        .getAContextRaisedSignal().add(
                                (BehavioralFeature) handle, (Signal) sig);
                return;
            }
            if (handle instanceof Operation) {
                ((org.omg.uml.UmlPackage) ((Signal) sig).refOutermostPackage())
                        .getCommonBehavior().getAContextRaisedSignal().add(
                                (BehavioralFeature) handle, (Signal) sig);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle + " or sig: "
                + sig);
    }

    /**
     *  Dummy method for exceptions in MDR metamodel.
     *
     * @author Andreas Rueckert <a_rueckert@gmx.net>
     *
     * @see org.argouml.model.CoreHelper#addRaisedException(java.lang.Object, java.lang.Object)
     */
    public void addRaisedException(Object handle, Object Exception) {
        throw new NotImplementedException( "UML 1.4 has no exceptions for operations");
    }

    public void addSourceFlow(Object handle, Object flow) {
        if (handle instanceof ModelElement && flow instanceof Flow) {
            ((ModelElement) handle).getSourceFlow().add((Flow) flow);
            return;
        }

        throw new IllegalArgumentException("handle: " + handle + " or flow: "
                + flow);
    }


    public void addSupplier(Object handle, Object element) {
        if (handle instanceof Dependency && element instanceof ModelElement) {
            ((Dependency) handle).getSupplier().add((ModelElement) element);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or element: " + element);
    }


    public void addSupplierDependency(Object supplier, Object dependency) {
        if (supplier instanceof ModelElement
                && dependency instanceof Dependency) {
            ((Dependency) dependency).getSupplier()
                    .add((ModelElement) supplier);
            return;
        }
        throw new IllegalArgumentException("supplier: " + supplier
                + " or dependency: " + dependency);
    }


    public void addTargetFlow(Object handle, Object flow) {
        if (handle instanceof ModelElement && flow instanceof Flow) {
            ((ModelElement) handle).getTargetFlow().add((Flow) flow);
            return;
        }

        throw new IllegalArgumentException("handle: " + handle + " or flow: "
                + flow);
    }

    public void addTemplateArgument(Object handle, int index, Object argument) {
        if (argument instanceof TemplateArgument) {
            if (handle instanceof Binding) {
                ((Binding) handle).getArgument().add(index,
                        (TemplateArgument) argument);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + argument);
    }


    public void addTemplateArgument(Object handle, Object argument) {
        if (argument instanceof TemplateArgument
                && handle instanceof Binding) {
            ((Binding) handle).getArgument().add((TemplateArgument) argument);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or argument: " + argument);
    }


    public void addTemplateParameter(Object handle, int index,
            Object parameter) {
        if (parameter instanceof TemplateParameter) {
            if (handle instanceof ModelElement) {
                ((ModelElement) handle).getTemplateParameter().add(
                        index, (TemplateParameter) parameter);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }


    public void addTemplateParameter(Object handle, Object parameter) {
        if (parameter instanceof TemplateParameter) {
            if (handle instanceof ModelElement) {
                ((ModelElement) handle).getTemplateParameter().add(
                        (TemplateParameter) parameter);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameter: " + parameter);
    }


    public void setAbstract(Object handle, boolean flag) {
        if (handle instanceof GeneralizableElement) {
            ((GeneralizableElement) handle).setAbstract(flag);
            return;
        }
        if (handle instanceof Operation) {
            ((Operation) handle).setAbstract(flag);
            return;
        }
        if (handle instanceof Reception) {
            ((Reception) handle).setAbstract(flag);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setActive(Object handle, boolean active) {
        if (handle instanceof UmlClass) {
            ((UmlClass) handle).setActive(active);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    public void setAggregation(Object handle, Object aggregationKind) {
        if (!(handle instanceof AssociationEnd)) {
            throw new IllegalArgumentException("An association end was expected got " + handle);
        }
        if (!(aggregationKind instanceof AggregationKind)) {
            throw new IllegalArgumentException("An aggregation kind was expected got " + aggregationKind);
        }
        AggregationKind ak = (AggregationKind) aggregationKind;
        AssociationEnd ae = (AssociationEnd) handle;
        // We silently ignore requests which conflict with
        // UML 1.4 WFR 2.5.3.1 #3 - no aggregation for n-ary associations
        if (ak == AggregationKindEnum.AK_NONE
                || ae.getAssociation().getConnection().size() < 3) {
            ae.setAggregation(ak);
        } else {
            ae.setAggregation(AggregationKindEnum.AK_NONE);
        }
        // If we made something aggregate, make sure the other ends conform
        // to UML 1.4 WFR 2.5.3.1 #2 - no more than one aggregate end
        if (ak == AggregationKindEnum.AK_AGGREGATE
                || ak == AggregationKindEnum.AK_COMPOSITE) {
            for (AssociationEnd end : ae.getAssociation().getConnection()) {
                if (!end.equals(ae)
                        && end.getAggregation() != AggregationKindEnum.AK_NONE) {
                    end.setAggregation(AggregationKindEnum.AK_NONE);
                }
            }
        }
        return;
    }

    public void setAggregation1(Object handle, Object aggregationKind) {
        setAggregation(handle, aggregationKind);
    }

    public void setAggregation2(Object handle, Object aggregationKind) {
        if (!(handle instanceof AssociationEnd)) {
            throw new IllegalArgumentException("An association end was expected got " + handle);
        }
        if (!(aggregationKind instanceof AggregationKind)) {
            throw new IllegalArgumentException("An aggregation kind was expected got " + aggregationKind);
        }
        AssociationEnd assEnd = (AssociationEnd) handle;
        Collection<AssociationEnd> assEnds = assEnd.getAssociation().getConnection();
        Iterator<AssociationEnd> it = assEnds.iterator();
        AssociationEnd other = it.next();
        if (other == handle) {
            other = it.next();
        }
        setAggregation(other, aggregationKind);
    }


    public void setAnnotatedElements(Object handle, Collection elems) {
        if (handle instanceof Comment && elems instanceof List) {
            CollectionHelper.update(
                    ((Comment) handle).getAnnotatedElement(), elems);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setAssociation(Object handle, Object association) {
        if (association instanceof UmlAssociation) {
            if (handle instanceof AssociationEnd) {
                ((AssociationEnd) handle).
                        setAssociation((UmlAssociation) association);
                return;
            }
            if (handle instanceof Link) {
                ((Link) handle).setAssociation((UmlAssociation) association);
                return;
            }
        } else if (association instanceof AssociationRole) {
            if (handle instanceof AssociationEndRole) {
                ((AssociationEndRole) handle).
                        setAssociation((AssociationRole) association);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or association: " + association);
    }


    public void setLeaf(Object handle, boolean flag) {
        if (handle instanceof Reception) {
            ((Reception) handle).setLeaf(flag);
            return;
        }
        if (handle instanceof Operation) {
            ((Operation) handle).setLeaf(flag);
            return;
        }
        if (handle instanceof GeneralizableElement) {
            ((GeneralizableElement) handle).setLeaf(flag);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setRaisedSignals(Object handle, Collection raisedSignals) {
        throw new NotImplementedException();
    }


    public void setBody(Object handle, Object expr) {
        if (handle instanceof Method
                && (expr == null || expr instanceof ProcedureExpression)) {
            ProcedureExpression oldPe = ((Method)handle).getBody();
            if (!equal(oldPe, (ProcedureExpression) expr)) {
                ((Method) handle).setBody(null);
                if (oldPe != null) {
                    Model.getUmlFactory().delete(oldPe);
                }
                ((Method) handle).setBody((ProcedureExpression) expr);
            }
            return;
        }

        if (handle instanceof Constraint
                && (expr == null || expr instanceof BooleanExpression)) {
            BooleanExpression oldBe = ((Constraint)handle).getBody();
            if (!equal(oldBe, (BooleanExpression) expr)) {
                ((Constraint) handle).setBody((BooleanExpression) expr);
                if (oldBe != null) {
                    Model.getUmlFactory().delete(oldBe);
                }
            }
            return;
        }

        throw new IllegalArgumentException("handle: " + handle + " or expr: "
                + expr);
    }


    private boolean equal(Expression expr1, Expression expr2) {
        if (expr1 == null) {
            if (expr2 == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return expr1.equals(expr2);
        }
    }

    @Deprecated
    public void setChangeability(Object handle, Object ck) {
        if (ck == null || ck instanceof ChangeableKind) {
            ChangeableKind changeableKind = (ChangeableKind) ck;

            if (handle instanceof StructuralFeature) {
                ((StructuralFeature) handle).setChangeability(changeableKind);
                return;
            }
            if (handle instanceof AssociationEnd) {
                ((AssociationEnd) handle).setChangeability(changeableKind);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle + " or ck: "
                + ck);
    }


    public void setReadOnly(Object handle, boolean isReadOnly) {
        setChangeability(handle, isReadOnly ? ChangeableKindEnum.CK_FROZEN
                : ChangeableKindEnum.CK_CHANGEABLE);
    }

    public void setChild(Object handle, Object child) {
        if (handle instanceof Generalization) {
            ((Generalization) handle).setChild((GeneralizableElement) child);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or child: "
                + child);
    }


    public void setConcurrency(Object handle, Object concurrencyKind) {
        if (handle instanceof Operation
                && (concurrencyKind == null
                    || concurrencyKind instanceof CallConcurrencyKind)) {
            ((Operation) handle).
                    setConcurrency((CallConcurrencyKind) concurrencyKind);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or concurrencyKind: " + concurrencyKind);
    }


    public void setConnections(Object handle, Collection elems) {
        if (handle instanceof UmlAssociation && elems instanceof List) {
            List<AssociationEnd> ends =
                ((UmlAssociation) handle).getConnection();
            CollectionHelper.update(ends, elems);
            // UML 1.4 WFR 2.5.3.1 #3 - no aggregation for N-ary associations
            if (ends.size() >= 3) {
                for (AssociationEnd end : ends) {
                    end.setAggregation(AggregationKindEnum.AK_NONE);
                }
            }
            return;
        }
        if (handle instanceof Link && elems instanceof List) {
            CollectionHelper.update(
                    ((Link) handle).getConnection(), elems);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    public void setDefaultElement(Object handle, Object element) {
        if (handle instanceof TemplateParameter
                && element instanceof ModelElement) {
            ((TemplateParameter) handle)
                    .setDefaultElement((ModelElement) element);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or model element: " + element);
    }


    public void setDefaultValue(Object handle, Object expr) {
        if (handle instanceof Parameter
                && (expr == null || expr instanceof Expression)) {
            Expression oldExp = ((Parameter) handle).getDefaultValue();
            if (!equal(oldExp, (Expression) expr)) {
                ((Parameter) handle).setDefaultValue((Expression) expr);
                if (oldExp != null) {
                    Model.getUmlFactory().delete(oldExp);
                }
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or expr: "
                + expr);
    }


    public void setDiscriminator(Object handle, String discriminator) {
        if (handle instanceof Generalization) {
            ((Generalization) handle).setDiscriminator(discriminator);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setFeature(Object elem, int i, Object impl) {
        if (elem instanceof Classifier && impl instanceof Feature) {
            ((Classifier) elem).getFeature().add(i, (Feature) impl);
            return;
        }

        throw new IllegalArgumentException("elem: " + elem + " or impl: "
                + impl);
    }


    public void setFeatures(Object handle, Collection features) {
        if (handle instanceof Classifier) {
            List featuresList = null;
            if (features instanceof List) {
                featuresList = (List) features;
            } else {
                featuresList = new ArrayList(features);
            }
            // TODO: A minimal update strategy would be better here to
            // minimize work and events firing, but it may be better to
            // deprecate the method and force calls to manage updates
            // themselves (since they probably usually want to just add
            // or remove a single element) - tfm 20061108
            ((Classifier) handle).getFeature().clear();
            ((Classifier) handle).getFeature().addAll(featuresList);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setContainer(Object handle, Object container) {
        if (handle instanceof ElementResidence
                && (container instanceof Component || container == null)) {
            ElementResidence er = (ElementResidence) handle;
            er.setContainer((Component) container);
        } else {
            throw new IllegalArgumentException("handle: " + handle
                    + " or container: " + container);
        }
    }


    public void setInitialValue(Object at, Object expr) {
        if (at instanceof Attribute
                && (expr == null || expr instanceof Expression)) {
            Expression oldExp = ((Attribute) at).getInitialValue();
            if (!equal(oldExp, (Expression) expr)) {
                ((Attribute) at).setInitialValue((Expression) expr);
                if (oldExp != null) {
                    Model.getUmlFactory().delete(oldExp);
                }
            }
            return;
        }
        throw new IllegalArgumentException("at: " + at + " or expr: " + expr);
    }


    public void setKind(Object handle, Object kind) {
        if (handle instanceof Parameter
                && kind instanceof ParameterDirectionKind) {
            ((Parameter) handle).setKind((ParameterDirectionKind) kind);
            return;
        }
        if (handle instanceof Pseudostate && kind instanceof PseudostateKind) {
            ((Pseudostate) handle).setKind((PseudostateKind) kind);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or kind: "
                + kind);
    }


    public void setModelElementContainer(Object handle, Object container) {
        /*
         * <em>Warning: when changing the implementation of this method be
         * warned that the sequence of the if then else tree DOES matter.</em>
         * Most notably, do not move the setNamespace method any level up in
         * the tree.<p>
         */
        if (handle instanceof Partition && container instanceof ActivityGraph) {
            ((Partition) handle).setActivityGraph((ActivityGraph) container);
        } else if (handle instanceof ModelElement
                && container instanceof Partition) {
            ((Partition) container).getContents().add((ModelElement) handle);
        } else if (handle instanceof Constraint
                && container instanceof Stereotype) {
            ((Stereotype) container).getStereotypeConstraint().add(
                    (Constraint) handle);
        } else if (handle instanceof Interaction
                && container instanceof Collaboration) {
            ((Interaction) handle).setContext((Collaboration) container);
        } else if (handle instanceof ElementResidence
                && (container instanceof Component || container == null)) {
            ElementResidence er = (ElementResidence) handle;
            er.setContainer((Component) container);
        } else if (handle instanceof AttributeLink
                && container instanceof Instance) {
            ((AttributeLink) handle).setInstance((Instance) container);
        } else if (handle instanceof Message
                && container instanceof Interaction) {
            ((Message) handle).setInteraction((Interaction) container);
        } else if (handle instanceof LinkEnd && container instanceof Link) {
            ((LinkEnd) handle).setLink((Link) container);
        } else if (handle instanceof AttributeLink
                && container instanceof LinkEnd) {
            ((AttributeLink) handle).setLinkEnd((LinkEnd) container);
        } else if (handle instanceof TaggedValue
                && container instanceof Stereotype) {
            ((TaggedValue) handle).getStereotype().clear();
            ((TaggedValue) handle).getStereotype().add((Stereotype) container);
        } else if (handle instanceof TaggedValue
                && container instanceof ModelElement) {
            ((TaggedValue) handle).setModelElement((ModelElement) container);
        } else if (handle instanceof StateVertex
                && container instanceof CompositeState) {
            ((StateVertex) handle).setContainer((CompositeState) container);
        } else if (handle instanceof ElementImport
                && container instanceof UmlPackage) {
            ((ElementImport) handle).setUmlPackage((UmlPackage) container);
        } else if (handle instanceof Transition && container instanceof State) {
            ((State) container).getInternalTransition()
                    .add((Transition) handle);
        } else if (handle instanceof State
                && container instanceof StateMachine) {
            ((State) handle).setStateMachine((StateMachine) container);
        } else if (handle instanceof Transition
                && container instanceof StateMachine) {
            ((Transition) handle).setStateMachine((StateMachine) container);
        } else if (handle instanceof Action
                && container instanceof Transition) {
            ((Transition) container).setEffect((Action) handle);
        } else if (handle instanceof Guard && container instanceof Transition) {
            ((Guard) handle).setTransition((Transition) container);
        } else if (handle instanceof ModelElement
                && container instanceof Namespace) {
            ((ModelElement) handle).setNamespace((Namespace) container);
        } else {
            throw new IllegalArgumentException("handle: " + handle
                    + " or container: " + container);
        }
    }

    public void setMultiplicity(Object handle, String arg) {
        if (arg == null) {
            setMultiplicityInternal(handle, null);
        } else {
            setMultiplicityInternal(handle,createMultiplicity(arg));
        }
    }

    /**
     * @param handle shall not be null. Shall be one of AssociationRole,
     * ClassifierRole, StructuralFeature, AssociationEnd, TagDefinition.
     * @param arg null is allowed
     */
    private void setMultiplicityInternal(Object handle, Multiplicity arg) {
        Multiplicity previousMult =
            (Multiplicity) Model.getFacade().getMultiplicity(handle);
        if (handle instanceof AssociationRole) {
            ((AssociationRole) handle).setMultiplicity(arg);
        } else if (handle instanceof ClassifierRole) {
            ((ClassifierRole) handle).setMultiplicity(arg);
        } else if (handle instanceof StructuralFeature) {
            ((StructuralFeature) handle).setMultiplicity(arg);
        } else if (handle instanceof AssociationEnd) {
            ((AssociationEnd) handle).setMultiplicity(arg);
        } else if (handle instanceof TagDefinition) {
            ((TagDefinition) handle).setMultiplicity(arg);
        }
        /* See issue 6038 for the reason behind the next statements: */
        if (previousMult != null) {
            LOG.log(Level.FINE, "Previous multiplicity of {0} will be deleted.", new Object[]{ handle, arg});
            Model.getUmlFactory().delete(previousMult);
        }
    }

    @Deprecated
    public void setMultiplicity(Object handle, Object arg) {
        if (arg instanceof String) {
            setMultiplicity(handle, (String) arg);
            return;
        }
        if (arg == null || arg instanceof Multiplicity) {
            setMultiplicityInternal(handle, (Multiplicity) arg);
        } else {
            throw new IllegalArgumentException("handle: " + handle + " or arg: "
                    + arg);
        }
    }

    /**
     * @param sarg may not be null
     */
    private Multiplicity createMultiplicity(String sarg) {
        boolean allDigits = true;
        for (int i=0; i < sarg.length(); ++i) {
            if (!Character.isDigit(sarg.charAt(i))) {
                allDigits = false;
            }
        }
        if (allDigits || sarg.indexOf('.') > -1 || sarg.indexOf(',') > -1) {
            return modelImpl.getDataTypesFactoryInternal()
                    .createMultiplicityInternal(sarg);
        } else {
            // TODO: We have multiple string representations for multiplicities
            // these should be consolidated. This form is used by
            // org.argouml.uml.reveng
            if ("1_N".equals(sarg)) {
                return modelImpl.getDataTypesFactoryInternal()
                        .createMultiplicityInternal(1, -1);
            } else {
                return modelImpl.getDataTypesFactoryInternal()
                        .createMultiplicityInternal(1, 1);
            }
        }
    }

    public void setMultiplicity(Object handle, int lower, int upper) {

        Multiplicity arg = modelImpl.getDataTypesFactoryInternal()
                .createMultiplicityInternal(lower, upper);

        setMultiplicityInternal(handle, arg);
    }

    public void setName(final Object handle, final String name) {
        if (handle instanceof ModelElement) {
            createCommand(
                new StringSetter() {
                    public void set(String value) {
                        ((ModelElement) handle).setName(value);
                    }
                },
                name,
                Model.getFacade().getName(handle),
                "name = " + name);

            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or name: "
                + name);
    }


    public void setBody(Object handle, String body) {
        if ((handle instanceof Comment) && (body != null)) {
            // All text was stored in name field in UML 1.3
            ((Comment) handle).setBody(body);
        }
    }


    public void setNamespace(Object handle, Object ns) {
        if (handle instanceof ModelElement
                && (ns == null || ns instanceof Namespace)) {
            Namespace oldNs = ((ModelElement) handle).getNamespace();
            if (((ns == null) && (oldNs == null))
                    || ((oldNs != null) && oldNs.equals(ns))) {
                return;
            }
            ((ModelElement) handle).setNamespace((Namespace) ns);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or ns: "
                + ns);
    }


    public void setNavigable(Object handle, boolean flag) {
        if (handle instanceof AssociationEnd) {
            ((AssociationEnd) handle).setNavigable(flag);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setOrdering(Object handle, Object ok) {
        if (handle instanceof AssociationEnd && ok instanceof OrderingKind) {
            ((AssociationEnd) handle).setOrdering((OrderingKind) ok);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or ok: "
                + ok);
    }


    public void setOwner(Object handle, Object owner) {
        if (handle instanceof Feature
                && (owner == null || owner instanceof Classifier)) {
            setNamespace(handle, null);
            ((Feature) handle).setOwner((Classifier) owner);
            return;
        }
        if (handle instanceof TagDefinition
                && (owner == null || owner instanceof Stereotype)) {
            setNamespace(handle, null);
            ((TagDefinition) handle).setOwner((Stereotype) owner);
            if (owner != null) {
                ((Stereotype) owner).getDefinedTag()
                        .add((TagDefinition) handle);
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or owner: "
                + owner);
    }


    public void setStatic(Object handle, boolean isStatic) {
        if (handle instanceof Feature) {
            ((Feature) handle)
                    .setOwnerScope(isStatic ? ScopeKindEnum.SK_CLASSIFIER
                            : ScopeKindEnum.SK_INSTANCE);
            return;
        }
        if (handle instanceof AssociationEnd) {
            ((AssociationEnd) handle)
                    .setTargetScope(isStatic ? ScopeKindEnum.SK_CLASSIFIER
                            : ScopeKindEnum.SK_INSTANCE);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    public void setParameter(Object handle, Object parameter) {
        if (handle instanceof TemplateParameter
                && parameter instanceof ModelElement) {
            ((TemplateParameter) handle).setParameter((ModelElement) parameter);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameters: " + parameter);
    }

    public void setParameters(Object handle, Collection parameters) {
        if (handle instanceof ObjectFlowState || handle instanceof Classifier
                || handle instanceof Event
                || handle instanceof BehavioralFeature) {
            Collection params = Model.getFacade().getParameters(handle);
            if (!params.isEmpty()) {
                Collection actualParams = new ArrayList(params);
                for (Object param : actualParams) {
                    removeParameter(handle, param);
                }
            }
            for (Object param : parameters) {
                addParameter(handle, param);
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or parameters: " + parameters);
    }


    public void setParent(Object handle, Object parent) {
        if (handle instanceof Generalization
                && parent instanceof GeneralizableElement) {
            ((Generalization) handle).setParent((GeneralizableElement) parent);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or parent: "
                + parent);
    }


    public void setPowertype(Object handle, Object pt) {
        if (handle instanceof Generalization
                && (pt == null || pt instanceof Classifier)) {
            ((Generalization) handle).setPowertype((Classifier) pt);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or pt: "
                + pt);
    }


    public void setQualifiers(Object handle, List elems) {
        if (handle instanceof AssociationEnd) {
            ((AssociationEnd) handle).getQualifier().clear();
            ((AssociationEnd) handle).getQualifier().addAll(elems);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setQuery(Object handle, boolean flag) {
        if (handle instanceof BehavioralFeature) {
            ((BehavioralFeature) handle).setQuery(flag);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setResident(Object handle, Object resident) {
        if (handle instanceof ElementResidence
                && (resident == null || resident instanceof ModelElement)) {
            ((ElementResidence) handle).setResident((ModelElement) resident);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or resident: " + resident);
    }


    public void setResidents(Object handle, Collection residents) {
        if (handle instanceof NodeInstance) {
            CollectionHelper.update(
                    ((NodeInstance) handle).getResident(), residents);
            return;
        }
        if (handle instanceof ComponentInstance) {
            CollectionHelper.update(
                    ((ComponentInstance) handle).getResident(), residents);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setRoot(Object handle, boolean flag) {
        if (handle instanceof Reception) {
            ((Reception) handle).setRoot(flag);
            return;
        }
        if (handle instanceof Operation) {
            ((Operation) handle).setRoot(flag);
            return;
        }
        if (handle instanceof GeneralizableElement) {
            ((GeneralizableElement) handle).setRoot(flag);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setSources(Object handle, Collection specifications) {
        if (handle instanceof Flow) {
            CollectionHelper.update(
                    ((Flow) handle).getSource(), specifications);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setSpecification(Object handle, boolean specification) {
        if (handle instanceof ModelElement) {
            ((ModelElement) handle).setSpecification(specification);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    public void setSpecification(Object operation, String specification) {
        if (operation instanceof Operation) {
            ((Operation) operation).setSpecification(specification);
            return;
        }
        if (operation instanceof Reception) {
            ((Reception) operation).setSpecification(specification);
            return;
        }
        throw new IllegalArgumentException("operation: " + operation);
    }


    public void setSpecification(Object method, Object specification) {
        if (method instanceof Method && specification instanceof Operation) {
            ((Method) method).setSpecification((Operation) specification);
            return;
        }
        throw new IllegalArgumentException("method: " + method
                + " or operation: " + specification);
    }


    public void setSpecifications(Object handle, Collection specifications) {
        if (handle instanceof AssociationEnd) {
            ((AssociationEnd) handle).getSpecification().addAll(specifications);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    @Deprecated
    public void setTaggedValue(Object handle, String tag, String value) {
        if (handle instanceof ModelElement) {
            TaggedValue tv =
                    (TaggedValue) modelImpl.getFacade().getTaggedValue(handle,
                            tag);
            if (tv == null) {
                tv =
                    (TaggedValue) modelImpl.getExtensionMechanismsFactory().
                        buildTaggedValue(tag, value);
                ((ModelElement) handle).getTaggedValue().add(tv);
            } else {
                modelImpl.getExtensionMechanismsHelper().setDataValues(tv,
                        new String[] {value});
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }


    @Deprecated
    public void setTargetScope(Object handle, Object scopeKind) {
        if (scopeKind instanceof ScopeKind) {
            if (handle instanceof StructuralFeature) {
                ((StructuralFeature) handle).
                        setTargetScope((ScopeKind) scopeKind);
                return;
            }
            if (handle instanceof AssociationEnd) {
                ((AssociationEnd) handle).setTargetScope((ScopeKind) scopeKind);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or scopeKind: " + scopeKind);
    }


    public void setType(Object handle, Object type) {
        if (type == null || type instanceof Classifier) {
            if (handle instanceof ObjectFlowState) {
                ((ObjectFlowState) handle).setType((Classifier) type);
                return;
            }
            if (handle instanceof ClassifierInState) {
                ((ClassifierInState) handle).setType((Classifier) type);
                return;
            }
            if (handle instanceof Parameter) {
                ((Parameter) handle).setType((Classifier) type);
                return;
            }
            if (handle instanceof AssociationEnd) {
                ((AssociationEnd) handle).setParticipant((Classifier) type);
                return;
            }
            if (handle instanceof StructuralFeature) {
                ((StructuralFeature) handle).setType((Classifier) type);
                return;
            }
            if (handle instanceof Operation) {
                if (!getReturnParameters(handle).isEmpty()) {
                    Parameter p = getReturnParameters(handle).get(0);
                    p.setType((Classifier) type);
                }
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle + " or type: "
                + type);
    }


    public void setVisibility(Object handle, Object visibility) {
        if (visibility instanceof VisibilityKind) {
            if (handle instanceof ModelElement) {
                ((ModelElement) handle).
                        setVisibility((VisibilityKind) visibility);
                return;
            }
            if (handle instanceof ElementResidence) {
                ((ElementResidence) handle).
                        setVisibility((VisibilityKind) visibility);
                return;
            }
            if (handle instanceof ElementImport) {
                ((ElementImport) handle).
                        setVisibility((VisibilityKind) visibility);
                return;
            }
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or visibility: " + visibility);
    }


    public void removeDeploymentLocation(Object handle, Object node) {
        try {
            if (handle instanceof Component && node instanceof Node) {
                ((Component) handle).getDeploymentLocation().remove(node);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle + " or node: "
                + node);
    }


    public void addStereotype(Object modelElement, Object stereo) {
        if (modelElement instanceof ModelElement
                && stereo instanceof Stereotype) {
            ModelElement me = (ModelElement) modelElement;
            Stereotype stereotype = (Stereotype) stereo;
            if (!(me.getStereotype().contains(stereo))) {
                me.getStereotype().add(stereotype);
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + modelElement
                + " or stereo: " + stereo);
    }



    public void addAllStereotypes(Object modelElement, Collection stereos) {
        if (modelElement instanceof ModelElement) {
            ModelElement me = (ModelElement) modelElement;
            me.getStereotype().addAll(stereos);
            return;
        }
        throw new IllegalArgumentException("handle: " + modelElement
                + " or stereos: " + stereos);
    }


    public void removeStereotype(Object modelElement, Object stereo) {
        try {
            if (modelElement instanceof ModelElement
                    && stereo instanceof Stereotype) {

                ModelElement me = (ModelElement) modelElement;
                Stereotype stereotype = (Stereotype) stereo;

                if (me.getStereotype().contains(stereo)) {
                    me.getStereotype().remove(stereotype);
                }
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + modelElement
                + " or stereo: " + stereo);
    }


    /*
     * @see org.argouml.model.CoreHelper#clearStereotypes(java.lang.Object)
     */
    public void clearStereotypes(Object modelElement) {
        if (modelElement instanceof ModelElement) {
            ((ModelElement) modelElement).getStereotype().clear();
            return;
        }
        throw new IllegalArgumentException("handle: " + modelElement);
    }


    public void setEnumerationLiterals(Object enumeration, List literals) {
        if (enumeration instanceof Enumeration) {
            ((Enumeration) enumeration).getLiteral().clear();
            ((Enumeration) enumeration).getLiteral().addAll(literals);
        }
    }


    public Collection<String> getAllMetatypeNames() {
        Set<String> names = new HashSet<String>();
        for (Object element : modelImpl.getModelPackage().getMofClass()
                .refAllOfClass()) {
            String name = ((javax.jmi.model.ModelElement) element).getName();
            if (names.add(name)) {
                LOG.log(Level.FINE, " Class {0}", name);
            } else {
                LOG.log(Level.SEVERE, "Found duplicate class " + name + " in metamodel");
            }
        }
        return names;
    }

    public Collection<String> getAllMetaDatatypeNames() {
        Set<String> names = new HashSet<String>();
        // Returns nothing
        for (Object element : modelImpl.getModelPackage().getDataType()
                .refAllOfClass()) {
            String name = ((javax.jmi.model.DataType) element).getName();
            if (names.add(name)) {
                LOG.log(Level.FINE, " DataType {0}", name);
            } else {
                LOG.log(Level.SEVERE, "Found duplicate datatype " + name + " in metamodel");
            }
        }
        // ScopeKind, VisibilityKind, PseudostateKind, etc
        // Doesn't include Boolean though, despite what UML 1.4 spec says
        for (Object element : modelImpl.getModelPackage().getEnumerationType()
                .refAllOfClass()) {
            String name = ((javax.jmi.model.EnumerationType) element).getName();
            if (names.add(name)) {
                LOG.log(Level.FINE, " EnumerationType {0}", name);
            } else {
                LOG.log(Level.SEVERE, "Found duplicate EnumerationType " + name
                        + " in metamodel");
            }
        }

        // Boolean, String, Integer, Long, Float, Double, etc
        for (Object element : modelImpl.getModelPackage().getPrimitiveType()
                .refAllOfClass()) {
            String name = ((javax.jmi.model.PrimitiveType) element).getName();
            if (names.add(name)) {
                LOG.log(Level.FINE, " PrimitiveType {0}", name);
            } else {
                LOG.log(Level.SEVERE, "Found duplicate primitive type " + name
                        + " in metamodel");
            }
        }

        return names;
    }


    /**
     * Create a command for a setter of a String value.
     *
     * @param accesser The accesser.
     * @param newValue The new value.
     * @param oldValue The old value.
     */
    private void createCommand(
            final StringSetter accesser,
            final String newValue,
            final String oldValue,
            final String descr) {
        if (newValue == oldValue) {
            return;
        }
        if (newValue != null
                && newValue.equals(oldValue)) {
            return;
        }
        ModelCommand command = new ModelCommand() {
            public void undo() {
                accesser.set(oldValue);
            }
            public Object execute() {
                accesser.set(newValue);
                return null;
            }
            public boolean isUndoable() {
                return true;
            }
            public boolean isRedoable() {
                return true;
            }
            public String toString() {
                return descr;
            }
        };
        Model.execute(command);
    }
}
