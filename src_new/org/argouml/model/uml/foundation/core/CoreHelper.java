// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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
package org.argouml.model.uml.foundation.core;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.model.uml.foundation.extensionmechanisms.ExtensionMechanismsFactory;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

import ru.novosoft.uml.MElementListener;
import ru.novosoft.uml.behavior.collaborations.MAssociationRole;
import ru.novosoft.uml.behavior.collaborations.MClassifierRole;
import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.state_machines.MStateMachine;
import ru.novosoft.uml.behavior.use_cases.MActor;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MComponent;
import ru.novosoft.uml.foundation.core.MConstraint;
import ru.novosoft.uml.foundation.core.MDataType;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MFeature;
import ru.novosoft.uml.foundation.core.MFlow;
import ru.novosoft.uml.foundation.core.MGeneralizableElement;
import ru.novosoft.uml.foundation.core.MGeneralization;
import ru.novosoft.uml.foundation.core.MInterface;
import ru.novosoft.uml.foundation.core.MMethod;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MNode;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MParameter;
import ru.novosoft.uml.foundation.core.MRelationship;
import ru.novosoft.uml.foundation.core.MStructuralFeature;
import ru.novosoft.uml.foundation.data_types.MParameterDirectionKind;
import ru.novosoft.uml.foundation.data_types.MVisibilityKind;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.model_management.MModel;

/**
 * Helper class for UML Foundation::Core Package.<p>
 *
 * Current implementation is a placeholder.<p>
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @author Jaap Branderhorst
 * @stereotype singleton
 */
public class CoreHelper {

    private static final Logger LOG = Logger.getLogger(CoreHelper.class);

    /**
     * Don't allow instantiation.
     */
    private CoreHelper() {
    }

    /**
     * Singleton instance.
     */
    private static final CoreHelper SINGLETON = new CoreHelper();

    /**
     * Singleton instance access method.
     *
     * @return The instance.
     */
    public static CoreHelper getHelper() {
        return SINGLETON;
    }

    /** This method returns all Classifiers of which this class is a
     *	direct or indirect subtype.
     *
     * @param cls1  the class you want to have the parents for
     * @return a collection of the parents, each of which is a
     *         {@link MGeneralizableElement MGeneralizableElement}
     */
    public Collection getAllSupertypes(Object cls1) {
        
        if (!(cls1 instanceof MClassifier))
            throw new IllegalArgumentException();
        
        MClassifier cls = (MClassifier) cls1;
        
        Collection result = new HashSet();
        Collection add = getSupertypes(cls);
        do {
            Collection newAdd = new HashSet();
            Iterator addIter = add.iterator();
            while (addIter.hasNext()) {
                MGeneralizableElement next =
                    (MGeneralizableElement) addIter.next();
                if (next instanceof MClassifier) {
                    newAdd.addAll(getSupertypes(next));
                }
            }
            result.addAll(add);
            add = newAdd;
            add.removeAll(result);
        }
        while (!add.isEmpty());
        return result;
    }

    /**
     * This method returns all Classifiers of which this class is a
     * direct subtype.<p>
     *
     * @param ogeneralizableelement the class you want to have the parents for
     * @return a collection of the parents, each of which is a
     *         {@link MGeneralizableElement MGeneralizableElement}
     */
    public Collection getSupertypes(Object ogeneralizableelement) {
        Collection result = new HashSet();
        if (ModelFacade.isAGeneralizableElement(ogeneralizableelement)) {
            MGeneralizableElement cls =
                (MGeneralizableElement) ogeneralizableelement;
            Collection gens = cls.getGeneralizations();
            Iterator genIterator = gens.iterator();
            while (genIterator.hasNext()) {
                MGeneralization next = (MGeneralization) genIterator.next();
                result.add(next.getParent());
            }
        }
        return result;
    }

    /** This method returns all opposite AssociationEnds of a given
     * Classifier
     *
     * @param classifier the classifier you want to have the opposite
     * association ends for
     * @return a collection of the opposite associationends
     */
    public Collection getAssociateEnds(MClassifier classifier) {
        Collection result = new ArrayList();
        Iterator ascends = classifier.getAssociationEnds().iterator();
        while (ascends.hasNext()) {
            MAssociationEnd ascend = (MAssociationEnd) ascends.next();
            if ((ascend.getOppositeEnd() != null))
                result.add(ascend.getOppositeEnd());
        }
        return result;
    }

    /**
     * This method returns all opposite AssociationEnds of a given
     * Classifier, including inherited
     *
     * @param classifier1 the classifier you want to have the opposite
     * association ends for
     * @return a collection of the opposite associationends
     */
    public Collection getAssociateEndsInh(Object classifier1) {
        
        if (!(classifier1 instanceof MClassifier))
            throw new IllegalArgumentException();
        
        MClassifier classifier = (MClassifier) classifier1;
        
        Collection result = new ArrayList();
        result.addAll(getAssociateEnds(classifier));
        Iterator parents = classifier.getParents().iterator();
        while (parents.hasNext()) {
            result.addAll(getAssociateEndsInh(parents.next()));
        }
        return result;
    }

    /**
     * This method removes a feature from a classifier.
     *
     * @param cls
     * @param feature
     */
    public void removeFeature(Object cls, Object feature) {
        if (cls != null
            && feature != null
            && cls instanceof MClassifier
            && feature instanceof MFeature) {
            ((MClassifier) cls).removeFeature((MFeature) feature);
        }
    }

    /**
     * This method returns the name of a feature.
     *
     * @param o is the feature
     * @return name
     */
    public String getFeatureName(Object o) {
        if (o != null && o instanceof MFeature)
            return ((MFeature) o).getName();
        return null;
    }

    /**
     * This method returns if the object is a method.
     *
     * @param o object
     * @return true if it's a method, false if not
     */
    public boolean isMethod(Object o) {
        return (o instanceof MMethod);
    }

    /** This method returns if the object is an operation.
     *
     * @param o object
     * @return true if it's an operation, false if not
     */
    public boolean isOperation(Object o) {
        return (o instanceof MOperation);
    }
    /** This method returns all operations of a given Classifier
     *
     * @param classifier the classifier you want to have the operations for
     * @return a collection of the operations
     */
    public Collection getOperations(Object classifier) {
        Collection result = new ArrayList();
        if (ModelFacade.isAClassifier(classifier)) {
            MClassifier mclassifier = (MClassifier) classifier;
            Iterator features = mclassifier.getFeatures().iterator();
            while (features.hasNext()) {
                MFeature feature = (MFeature) features.next();
                if (ModelFacade.isAOperation(feature))
                    result.add(feature);
            }
        }
        return result;
    }    

    /** This method returns all attributes of a given Classifier.
     *
     * @param classifier the classifier you want to have the attributes for
     * @return a collection of the attributes
     */
    public Collection getAttributes(MClassifier classifier) {
	Collection result = new ArrayList();
	Iterator features = classifier.getFeatures().iterator();
	while (features.hasNext()) {
	    MFeature feature = (MFeature) features.next();
	    if (feature instanceof MAttribute)
		result.add(feature);
	}
	return result;
    }  

    /** This method returns all attributes of a given Classifier,
     * including inherited
     *
     * @param classifier the classifier you want to have the attributes for
     * @return a collection of the attributes
     */
    public Collection getAttributesInh(Object classifier) {
        
        if (!(classifier instanceof MClassifier))
            throw new IllegalArgumentException();
        
        Collection result = new ArrayList();
        result.addAll(ModelFacade.getStructuralFeatures(classifier));
        Iterator parents = ((MClassifier) classifier).getParents().iterator();
        while (parents.hasNext()) {
            MClassifier parent = (MClassifier) parents.next();
            LOG.debug("Adding attributes for: " + parent);
            result.addAll(getAttributesInh(parent));
        }
        return result;
    }

    /** This method returns all operations of a given Classifier,
     * including inherited
     *
     * @param classifier the classifier you want to have the operations for
     * @return a collection of the operations
     */
    public Collection getOperationsInh(MClassifier classifier) {
        Collection result = new ArrayList();
        result.addAll(ModelFacade.getOperations(classifier));
        Iterator parents = classifier.getParents().iterator();
        while (parents.hasNext()) {
            result.addAll(getOperationsInh((MClassifier) parents.next()));
        }
        return result;
    }

    /**
     * This method finds all paramters of the given operation which
     * have the MParamterDirectionType RETURN. If it is only one, it
     * is returned.  In case there are no return parameters, null is
     * returned. If there is more than one return paramter, first of
     * them is returned, but a message is logged.<p>
     *
     * @param operation1 the operation you want to find the return
     * parameter for.
     * @return If this operation has only one paramter with Kind: RETURN,
     *         this is it, otherwise null
     */
    public MParameter getReturnParameter(Object operation1) {
        
        if (!(operation1 instanceof MOperation))
            throw new IllegalArgumentException();
        
        MOperation operation = (MOperation) operation1;
        
        Vector returnParams = new Vector();
        Iterator params = operation.getParameters().iterator();
        while (params.hasNext()) {
            MParameter parameter = (MParameter) params.next();
            if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
                returnParams.add(parameter);
            }
        }
        switch (returnParams.size()) {
	case 1 :
	    return (MParameter) returnParams.elementAt(0);
	case 0 :
	    //Next line gives too many strings while debugging
	    // obscuring other errors.
	    //cat.debug("No ReturnParameter found!");
	    return null;
	default :
	    LOG.debug(
		      "More than one ReturnParameter found, returning first!");
	    return (MParameter) returnParams.elementAt(0);
        }
    }
    /**
     * Returns all return parameters for an operation.
     *
     * @param operation is the operation.
     * @return Collection
     */
    public Collection getReturnParameters(Object operation) {
        Vector returnParams = new Vector();
        Iterator params = ((MOperation) operation).getParameters().iterator();
        while (params.hasNext()) {
            MParameter parameter = (MParameter) params.next();
            if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
                returnParams.add(parameter);
            }
        }
        return returnParams;
    }
    
    /**
     * Returns the operation that some method realized. Returns null if
     * object isn't a method or, possibly, if the method isn't properly
     * defined.
     *
     * @param object  the method you want the realized operation of.
     * @return an operation, or null.
     */
    public MOperation getSpecification(Object object) {
	if (!(object instanceof MMethod))
	    return null;
	return ((MMethod) object).getSpecification();
    }

    /**
     * Returns all Interfaces of which this class is a realization.<p>
     *
     * @param classifier  the class you want to have the interfaces for
     * @return a collection of the Interfaces
     */
    public Collection getSpecifications(Object classifier) {
        Collection result = new Vector();
        Collection deps = ((MClassifier) classifier).getClientDependencies();
        Iterator depIterator = deps.iterator();
        while (depIterator.hasNext()) {
            Object dep = depIterator.next();
            Object stereo = null;
            if (ModelFacade.getStereotypes(dep).size() > 0) {
                stereo = ModelFacade.getStereotypes(dep).iterator().next();
            }
            if ((ModelFacade.isAAbstraction(dep))
                && stereo != null
                && ModelFacade.getName(stereo) != null
                && ModelFacade.getName(stereo).equals("realize")) {
		Object i = ModelFacade.getSuppliers(dep).toArray()[0];
                result.add(i);
            }
        }
        return result;
    }
    /** This method returns all Classifiers of which this class is a
     *	direct supertype.
     *
     * @param cls  the class you want to have the children for
     * @return a collection of the children, each of which is a
     *         {@link MGeneralizableElement MGeneralizableElement}
     */
    public Collection getSubtypes(MClassifier cls) {
        Collection result = new Vector();
        Collection gens = cls.getSpecializations();
        Iterator genIterator = gens.iterator();
        while (genIterator.hasNext()) {
            MGeneralization next = (MGeneralization) genIterator.next();
            result.add(next.getChild());
        }
        return result;
    }
    /**
     * Build a returnparameter. Removes all current return parameters from the
     * operation and adds the supplied parameter. The directionkind of the
     * parameter will be return. The name will be equal to the name of the last
     * found return parameter or the default value "return" if no return
     * parameter was present in the operation.
     *
     * @param operation is the operation
     * @param newReturnParameter is the return parameter.
     */
    public void setReturnParameter(Object/*MOperation*/ operation,
				   Object/*MParameter*/ newReturnParameter) {
        Iterator params = ModelFacade.getParameters(operation).iterator();
        String name = "return";
        while (params.hasNext()) {
            MParameter parameter = (MParameter) params.next();
            if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
                ModelFacade.removeParameter(operation, parameter);
                if (parameter.getName() != null || parameter.getName() == "") {
                    name = parameter.getName();
                }
            }
        }
        ModelFacade.setName(newReturnParameter, name);
        ModelFacade.setKind(newReturnParameter, MParameterDirectionKind.RETURN);
        ModelFacade.addParameter(operation, 0, newReturnParameter);
        // we set the listeners to the figs here too
        // it would be better to do that in the figs themselves
        Project p = ProjectManager.getManager().getCurrentProject();
        Iterator it = p.findFigsForMember(operation).iterator();
        while (it.hasNext()) {
            MElementListener listener = (MElementListener) it.next();
            // UmlModelEventPump.getPump().removeModelEventListener(listener,
            // newReturnParameter);
            UmlModelEventPump.getPump()
		.addModelEventListener(listener,
				       newReturnParameter);
        }
    }

    /**
     * Builds a dependency with stereotype support.
     *
     * @param from is the model element where the dependency begins
     * @param to is the model element where dependency ends
     * @return a newly created dependency.
     */
    public MDependency buildSupportDependency(MModelElement from,
					      MModelElement to) {
        MDependency dep = CoreFactory.getFactory().buildDependency(from, to);
        ExtensionMechanismsFactory.getFactory()
	    .buildStereotype(dep, "support", getCurrentModel());
        return dep;
    }

    /**
     * Returns all behavioralfeatures found in this element and its
     * children.<p>
     *
     * @param element is the element
     * @return Collection
     */
    public Collection getAllBehavioralFeatures(MModelElement element) {
        Iterator it = element.getModelElementContents().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MClassifier) {
                list.addAll(getAllBehavioralFeatures((MClassifier) o));
            } else {
                list.addAll(getAllBehavioralFeatures(
			(MModelElement) it.next()));
            }
        }
        return list;
    }
    
    /**
     * Returns all behavioral features of some classifier.
     * @param clazz The classifier
     * @return
     */
    public Collection getBehavioralFeatures(Object clazz) {
        if (ModelFacade.isAClassifier(clazz)) {
            List ret = new ArrayList();
            Iterator it = ModelFacade.getFeatures(clazz).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (ModelFacade.isABehavioralFeature(o)) {
                    ret.add(o);
                }      
            }        
            return ret;
        } else {
            throw new IllegalArgumentException("Argument is not a classifier");
        }
    }

    /**
     * Returns all behavioralfeatures found in this classifier and its
     * children.<p>
     *
     * @param clazz is the classifier.
     * @return Collection
     */
    public Collection getAllBehavioralFeatures(MClassifier clazz) {
        List features = new ArrayList();
        if (!(clazz instanceof MDataType)) {
            Iterator it = clazz.getFeatures().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof MBehavioralFeature) {
                    features.add(o);
                }
            }
        }
        return features;
    }

    /**
     * Returns all behavioralfeatures found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllBehavioralFeatures() {
        MNamespace model = getCurrentModel();
        return getAllBehavioralFeatures(model);
    }

    /**
     * Returns all interfaces found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllInterfaces() {
        MNamespace model = getCurrentModel();
        return getAllInterfaces(model);
    }

    /**
     * Returns all interfaces found in this namespace and in its children
     * @return Collection
     */
    public Collection getAllInterfaces(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllInterfaces((MNamespace) o));
            }
            if (o instanceof MInterface) {
                list.add(o);
            }
        }
        return list;
    }

    /**
     * Returns all classes found in the projectbrowser model.<p>
     * @return Collection
     */
    public Collection getAllClasses() {
        MNamespace model = getCurrentModel();
        return getAllClasses(model);
    }

    /**
     * Returns all classes found in this namespace and in its children.<p>
     *
     * @param ns is the namespace.
     * @return Collection
     */
    public Collection getAllClasses(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllClasses((MNamespace) o));
            }
            if (o instanceof MClass) {
                list.add(o);
            }
        }
        return list;
    }

    /**
     * Return all interfaces the given class realizes.<p>
     *
     * @param classifier is the class
     * @return Collection
     */
    public Collection getRealizedInterfaces(MClassifier classifier) {
        if (classifier == null)
            return new ArrayList();
        Iterator it = classifier.getClientDependencies().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object clientDependency = it.next();
            if (ModelFacade.isAAbstraction(clientDependency)) {
                Object stereo = null;
                if (ModelFacade.getStereotypes(clientDependency).size() > 0) {
                    stereo =
			ModelFacade.getStereotypes(clientDependency).iterator()
			    .next();
                }
                if (stereo != null
                        && ModelFacade.getBaseClass(stereo) != null
                        && ModelFacade.getName(stereo) != null
                        && ModelFacade.getBaseClass(stereo)
		                .equals("Abstraction")
                        && ModelFacade.getName(stereo).equals("realize")) {
                    Iterator it2 =
			ModelFacade.getSuppliers(clientDependency).iterator();
                    while (it2.hasNext()) {
                        Object supplier = it2.next();
                        if (supplier instanceof MInterface) {
                            list.add(supplier);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    /**
     * Returns all classes some generalizable element extends.
     *
     * @param clazz is the generalizable element
     * @return Collection
     */
    public Collection getExtendedClassifiers(Object clazz) {
        if (clazz == null)
            return new ArrayList();
        Iterator it = ModelFacade.getGeneralizations(clazz).iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            MGeneralization gen = (MGeneralization) it.next();
            MGeneralizableElement parent = gen.getParent();
            if (parent != null) {
                list.add(parent);
            }
        }
        return list;
    }

    /**
     * Gets the generalization between two generalizable elements.
     * Returns null if there is none.<p>
     *
     * @param achild is the child generalizable element.
     * @param aparent is the parent generalizable element.
     * @return MGeneralization
     */
    public MGeneralization getGeneralization(Object achild,
					     Object aparent) {
        MGeneralizableElement child = (MGeneralizableElement) achild;
        MGeneralizableElement parent = (MGeneralizableElement) aparent;
        if (child == null || parent == null)
            return null;
        Iterator it = child.getGeneralizations().iterator();
        while (it.hasNext()) {
            MGeneralization gen = (MGeneralization) it.next();
            if (gen.getParent() == parent) {
                return gen;
            }
        }
        return null;
    }

    /**
     * Returns all flows from some source modelelement to a target
     * modelelement.<p>
     *
     * @param source is the source model element.
     * @param target is the target model element.
     * @return Collection
     */
    public Collection getFlows(MModelElement source, MModelElement target) {
        if (source == null || target == null)
            return null;
        List ret = new ArrayList();
        Collection targetFlows = target.getTargetFlows();
        Iterator it = source.getSourceFlows().iterator();
        while (it.hasNext()) {
            MFlow flow = (MFlow) it.next();
            if (targetFlows.contains(flow)) {
                ret.add(flow);
            }
        }
        return ret;
    }

    /**
     * Returns all elements that extend some class.
     *
     * @param clazz is the class (a generalizable element)
     * @return Collection
     */
    public Collection getExtendingElements(Object clazz) {
        if (clazz == null)
            return new ArrayList();
        Iterator it = ModelFacade.getSpecializations(clazz).iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object/*MGeneralization*/ gen = it.next();
            Object/*MGeneralizableElement*/ client = ModelFacade.getChild(gen);
            if (client != null) {
                list.add(client);
            }
        }
        return list;
    }

    /**
     * Returns all classifiers that extend some classifier.
     *
     * @param clazz is the classifier.
     * @return Collection
     */
    public Collection getExtendingClassifiers(MClassifier clazz) {
        if (clazz == null)
            return new ArrayList();
        Iterator it = clazz.getSpecializations().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            MGeneralization gen = (MGeneralization) it.next();
            MGeneralizableElement client = gen.getChild();
            if (client instanceof MClassifier) {
                list.add(client);
            }
        }
        return list;
    }

    /**
     * Returns all components found in the projectbrowser model
     *
     * @return Collection
     */
    public Collection getAllComponents() {
        MNamespace model = getCurrentModel();
        return getAllComponents(model);
    }

    /**
     * Returns all components found in this namespace and in its children.
     *
     * @param ns is the namespace.
     * @return Collection
     */
    public Collection getAllComponents(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllComponents((MNamespace) o));
            }
            if (o instanceof MComponent) {
                list.add(o);
            }
        }
        return list;
    }

    /**
     * Returns all datatypes found in the projectbrowser model.
     *
     * @return Collection
     */
    public Collection getAllDataTypes() {
        MNamespace model = getCurrentModel();
        return getAllDataTypes(model);
    }

    /**
     * Returns all components found in this namespace and in its children.
     *
     * @param ns is the namespace
     * @return Collection
     */
    public Collection getAllDataTypes(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllDataTypes((MNamespace) o));
            }
            if (o instanceof MDataType) {
                list.add(o);
            }
        }
        return list;
    }

    /**
     * Returns all nodes found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllNodes() {
        MNamespace model = getCurrentModel();
        return getAllNodes(model);
    }

    /**
     * Returns all components found in this namespace and in its children.<p>
     *
     * @param ns is the namespace
     * @return Collection
     */
    public Collection getAllNodes(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllNodes((MNamespace) o));
            }
            if (o instanceof MNode) {
                list.add(o);
            }
        }
        return list;
    }
    /**
     * Gets all classifiers that are associated to the given
     * classifier (have an association relationship with the
     * classifier).<p>
     *
     * @param aclassifier an MClassifier
     * @return Collection
     */
    public Collection getAssociatedClassifiers(Object aclassifier) {
        MClassifier classifier = (MClassifier) aclassifier;
        if (classifier == null)
            return new ArrayList();
        List list = new ArrayList();
        Iterator it = classifier.getAssociationEnds().iterator();
        while (it.hasNext()) {
            MAssociationEnd end = (MAssociationEnd) it.next();
            MAssociation assoc = end.getAssociation();
            Iterator it2 = assoc.getConnections().iterator();
            while (it2.hasNext()) {
                MAssociationEnd end2 = (MAssociationEnd) it2.next();
                if (end2 != end) {
                    list.add(end2.getType());
                }
            }
        }
        return list;
    }

    /**
     * Gets the associations between the classifiers from and to. Returns null
     * if from or to is null or if there is no association between them.
     *
     * @param from a classifier
     * @param to a classifier
     * @return MAssociation
     */
    public Collection getAssociations(Object/*MClassifier*/ from,
				      Object/*MClassifier*/ to) {
        Set ret = new HashSet();
        if (from == null || to == null)
            return ret;
        Iterator it = ModelFacade.getAssociationEnds(from).iterator();
        while (it.hasNext()) {
            MAssociationEnd end = (MAssociationEnd) it.next();
            MAssociation assoc = end.getAssociation();
            Iterator it2 = assoc.getConnections().iterator();
            while (it2.hasNext()) {
                MAssociationEnd end2 = (MAssociationEnd) it2.next();
                if (end2.getType() == to) {
                    ret.add(assoc);
                }
            }
        }
        return ret;
    }
    /**
     * Returns all classifiers found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllClassifiers() {
        MNamespace model = getCurrentModel();
        return getAllClassifiers(model);
    }
    /**
     * Returns all classifiers found in this namespace and in its children
     * @return Collection
     */
    public Collection getAllClassifiers(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllClassifiers((MNamespace) o));
            }
            if (o instanceof MClassifier) {
                list.add(o);
            }
        }
        return list;
    }

    /**
     * Returns all associations for some classifier.<p>
     *
     * @param oclassifier
     * @return Collection
     */
    public Collection getAssociations(Object oclassifier) {
        Collection col = new ArrayList();
        if (ModelFacade.isAClassifier(oclassifier)) {
            MClassifier classifier = (MClassifier) oclassifier;
            Iterator it = classifier.getAssociationEnds().iterator();
            while (it.hasNext()) {
                col.add(((MAssociationEnd) it.next()).getAssociation());
            }
        }
        return col;
    }
    /**
     * Returns the associationend between a classifier and
     * an associaton.<p>
     *
     * @param type is the classifier
     * @param assoc is the association
     * @return MAssociationEnd
     */
    public MAssociationEnd getAssociationEnd(MClassifier type,
					     MAssociation assoc) {
        if (type == null || assoc == null)
            return null;
        Iterator it = type.getAssociationEnds().iterator();
        while (it.hasNext()) {
            MAssociationEnd end = (MAssociationEnd) it.next();
            if (assoc.getConnections().contains(end))
                return end;
        }
        return null;
    }
    /**
     * Returns the contents (owned elements) of this classifier and
     * all its parents as specified in section 2.5.3.8 of the UML 1.3
     * spec.<p>
     *
     * @param clazz is the classifier
     * @return Collection
     */
    public Collection getAllContents(MClassifier clazz) {
        if (clazz == null)
            return new ArrayList();
        List list = new ArrayList();
        Iterator it = clazz.getOwnedElements().iterator();
        while (it.hasNext()) {
            MModelElement element = (MModelElement) it.next();
            if (element.getVisibility().equals(MVisibilityKind.PUBLIC)
                || element.getVisibility().equals(MVisibilityKind.PROTECTED)) {
                list.add(element);
            }
        }
        it = clazz.getGeneralizations().iterator();
        while (it.hasNext()) {
            list.addAll(getAllContents((MClassifier) it.next()));
        }
        return list;
    }

    /**
     * Returns all attributes of some classifier and of its parents.
     *
     * @param clazz is the classifier
     * @return Collection
     */
    public Collection getAllAttributes(MClassifier clazz) {
        if (clazz == null)
            return new ArrayList();
        List list = new ArrayList();
        Iterator it = clazz.getFeatures().iterator();
        while (it.hasNext()) {
            MFeature element = (MFeature) it.next();
            if (element instanceof MAttribute) {
                list.add(element);
            }
        }
        it = clazz.getGeneralizations().iterator();
        while (it.hasNext()) {
            list.addAll(getAllAttributes((MClassifier) it.next()));
        }
        return list;
    }

    /**
     * Returns the source of a relation. The source of a relation is
     * defined as the modelelement that propagates this relation. If
     * there are more then 1 sources, only the first is returned. If
     * there is no source, null is returned. Examples of sources
     * include classifiers that are types to associationends, usecases
     * that are bases to extend and include relations and so on. A
     * source is allways the start from the arrow in the fig, the
     * destination the end.<p>
     *
     * This method also works to get the source of a Link.<p>
     * <p>This method also works for CommentEdge</p>
     * TODO: move this method to a generic ModelHelper
     *
     * @param relationship is the relation
     * @return Object
     */
    public Object getSource(Object relationship) {        
        if (!(relationship instanceof MRelationship)
	    && !(ModelFacade.isALink(relationship))) {


            throw new IllegalArgumentException("Argument " 
                    			       + relationship.toString() 
                    			       + " is not "
					       + "a relationship");

	}
        if (ModelFacade.isALink(relationship)) {
	    Iterator it = ModelFacade.getConnections(relationship).iterator();
	    if (it.hasNext()) {
		return ModelFacade.getInstance(it.next());
	    } else {
		return null;
	    }
        }       
        if (relationship instanceof MAssociation) {
            MAssociation assoc = (MAssociation) relationship;
            List conns = assoc.getConnections();
            if (conns == null || conns.isEmpty())
                return null;
            return ((MAssociationEnd) conns.get(0)).getType();
        }
        if (relationship instanceof MGeneralization) {
            MGeneralization gen = (MGeneralization) relationship;
            return gen.getChild();
        }
        if (relationship instanceof MDependency) {
            MDependency dep = (MDependency) relationship;
            Collection col = dep.getClients();
            if (col.isEmpty())
                return null;
            return (MModelElement) (col.toArray())[0];
        }
        if (relationship instanceof MFlow) {
            MFlow flow = (MFlow) relationship;
            Collection col = flow.getSources();
            if (col.isEmpty())
                return null;
            return (MModelElement) (col.toArray())[0];
        }
        if (relationship instanceof MExtend) {
            MExtend extend = (MExtend) relationship;
            return extend.getExtension(); // we have to follow the arrows..
        }
        if (relationship instanceof MInclude) {
            MInclude include = (MInclude) relationship;
            // we use modelfacade here to cover up for a messup in NSUML
            return ModelFacade.getBase(include);
        }
        if (relationship instanceof CommentEdge) {
            return ((CommentEdge) relationship).getSource();
        }
        return null;
    }

    /**
     * Returns the destination of a relation. The destination of a
     * relation is defined as the modelelement that receives this
     * relation.  If there are more then 1 destinations, only the
     * first is returned.  If there is no destination, null is
     * returned.  Examples of sources include classifiers that are
     * types to associationends, usecases that are bases to extend and
     * include relations and so on.  In the case of an association,
     * the destination is defined as the type of the second element in
     * the connections list.<p>
     *
     * This method also works for links.<p>
     * <p>This method also works for CommentEdge</p>
     * TODO: move this method to a generic ModelHelper
     *
     * @param relationship is the relation
     * @return object
     */
    public Object getDestination(Object relationship) {
        
	if (!(relationship instanceof MRelationship)
	    && !(ModelFacade.isALink(relationship)) 
	    && !(relationship instanceof CommentEdge)) {

	    throw new IllegalArgumentException("Argument is not "
					       + "a relationship");
	}
	if (ModelFacade.isALink(relationship)) {
	    Iterator it = ModelFacade.getConnections(relationship).iterator();
	    if (it.hasNext()) {
		it.next();
		if (it.hasNext()) {
		    return ModelFacade.getInstance(it.next());
		} else {
		    return null;
		}				
	    } else
		return null;        
	}
              
        
        if (relationship instanceof MAssociation) {
            MAssociation assoc = (MAssociation) relationship;
            List conns = assoc.getConnections();
            if (conns.size() <= 1)
                return null;
            return ((MAssociationEnd) conns.get(1)).getType();
        }
        if (relationship instanceof MGeneralization) {
            MGeneralization gen = (MGeneralization) relationship;
            return gen.getParent();
        }
        if (relationship instanceof MDependency) {
            MDependency dep = (MDependency) relationship;
            Collection col = dep.getSuppliers();
            if (col.isEmpty())
                return null;
            return (MModelElement) (col.toArray())[0];
        }
        if (relationship instanceof MFlow) {
            MFlow flow = (MFlow) relationship;
            Collection col = flow.getTargets();
            if (col.isEmpty())
                return null;
            return (MModelElement) (col.toArray())[0];
        }
        if (relationship instanceof MExtend) {
            MExtend extend = (MExtend) relationship;
            return extend.getBase();
        }
        if (relationship instanceof MInclude) {
            MInclude include = (MInclude) relationship;
            return ModelFacade.getAddition(include);
        }
        if (relationship instanceof CommentEdge) {
            return ((CommentEdge) relationship).getDestination();
        }
        return null;
    }

    /**
     * Returns the dependencies between some supplier modelelement and
     * some client modelelement.  Does not return the vica versa
     * relationship (dependency 'from client to supplier').<p>
     *
     * @param supplierObj a MModelElement
     * @param clientObj a MModelElement
     * @return Collection
     */
    public Collection getDependencies(
				      Object supplierObj,
				      Object clientObj) {
            
        if (!(supplierObj instanceof MModelElement)
	    || !(clientObj instanceof MModelElement)) {

            return null;

	}
        
        MModelElement supplier = (MModelElement) supplierObj;
        MModelElement client = (MModelElement) clientObj;
        
        List ret = new ArrayList();
        Collection clientDependencies = client.getClientDependencies();
        Iterator it = supplier.getSupplierDependencies().iterator();
        while (it.hasNext()) {
            MDependency dep = (MDependency) it.next();
            if (clientDependencies.contains(dep)) {
                ret.add(dep);
            }
        }
        return ret;
    }

    /**
     * Returns all relationships between the source and dest
     * modelelement and vica versa.<p>
     *
     * @param source is the source model element
     * @param dest is the destination model element
     * @return Collection
     */
    public Collection getRelationships(MModelElement source,
				       MModelElement dest) {
        Set ret = new HashSet();
        if (source == null || dest == null)
            return ret;
        ret.addAll(getFlows(source, dest));
        ret.addAll(getFlows(dest, source));
        ret.addAll(getDependencies(source, dest));
        ret.addAll(getDependencies(dest, source));
        if (source instanceof MGeneralizableElement
            && dest instanceof MGeneralizableElement) {
            ret.add(getGeneralization(source, dest));
            ret.add(getGeneralization(dest, source));
            if (source instanceof MClassifier && dest instanceof MClassifier) {
                ret.addAll(getAssociations(source, dest));
            }
        }
        return ret;
    }

    /**
     * Returns true if some modelelement may be owned by the given
     * namespace.<p>
     *
     * @param mObj a MModelElement
     * @param nsObj a MNamespace
     * @return boolean
     */
    public boolean isValidNamespace(Object mObj, Object nsObj) {
        
        if (!(mObj instanceof MModelElement)
	    || !(nsObj instanceof MNamespace)) {

            return false;

	}
        
        MModelElement m = (MModelElement) mObj;
        MNamespace ns = (MNamespace) nsObj;
        
        if (m == null || ns == null)
            return false;
        if (ns.getModel() != m.getModel())
            return false;
        if (m == ns)
            return false;
        if (m instanceof MNamespace
            && m == getFirstSharedNamespace((MNamespace) m, ns))
            return false;
        if (ns instanceof MInterface
            || ns instanceof MActor
            || ns instanceof MUseCase)
            return false;
        else if (ns instanceof MComponent)
            return (m instanceof MComponent && m != ns);
        else if (ns instanceof MCollaboration) {
            if (!(m instanceof MClassifierRole
		  || m instanceof MAssociationRole
		  || m instanceof MGeneralization
		  || m instanceof MConstraint))
                return false;
        } else if (ns instanceof MPackage) {
            if (!(m instanceof MPackage
		  || m instanceof MClassifier
		  || m instanceof MAssociation
		  || m instanceof MGeneralization
		  || m instanceof MDependency
		  || m instanceof MConstraint
		  || m instanceof MCollaboration
		  || m instanceof MStateMachine
		  || m instanceof MStereotype))
                return false;
        } else if (ns instanceof MClass) {
            if (!(m instanceof MClass
		  || m instanceof MAssociation
		  || m instanceof MGeneralization
		  || m instanceof MUseCase
		  || m instanceof MConstraint
		  || m instanceof MDependency
		  || m instanceof MCollaboration
		  || m instanceof MDataType
		  || m instanceof MInterface))
                return false;
        } else if (ns instanceof MClassifierRole) {
            if (!(((MClassifierRole) ns).getAvailableContentses().contains(m)
		  || ((MClassifierRole) ns).getAvailableFeatures().contains(m)))
                return false;
        }
        if (m instanceof MStructuralFeature) {
            if (!isValidNamespace((MStructuralFeature) m, ns))
                return false;
        } else if (m instanceof MGeneralizableElement) {
            if (!isValidNamespace((MGeneralizableElement) m, ns))
                return false;
        } else if (m instanceof MGeneralization) {
            if (!isValidNamespace((MGeneralization) m, ns))
                return false;
        }
        if (m instanceof MAssociation) {
            if (!isValidNamespace((MAssociation) m, ns))
                return false;
        } else if (m instanceof MCollaboration) {
            if (!isValidNamespace((MCollaboration) m, ns))
                return false;
        }
        return true;
    }
    private boolean isValidNamespace(MCollaboration collab, MNamespace ns) {
        Iterator it = collab.getOwnedElements().iterator();
        while (it.hasNext()) {
            MModelElement m = (MModelElement) it.next();
            if (m instanceof MClassifierRole) {
                MClassifierRole role = (MClassifierRole) m;
                Iterator it2 = role.getBases().iterator();
                while (it2.hasNext()) {
                    if (!ns.getOwnedElements().contains(it2.next()))
                        return false;
                }
            } else if (m instanceof MAssociationRole) {
                if (!ns.getOwnedElements()
		    .contains(((MAssociationRole) m).getBase()))
                    return false;
            }
        }
        return true;
    }

    private boolean isValidNamespace(MGeneralization gen, MNamespace ns) {
        if (gen.getParent() == null || gen.getChild() == null)
            return true;
        MNamespace ns1 = gen.getParent().getNamespace();
        MNamespace ns2 = gen.getChild().getNamespace();
        if (ns == getFirstSharedNamespace(ns1, ns2))
            return true;
        return false;
    }

    private boolean isValidNamespace(MStructuralFeature struc, MNamespace ns) {
        if (struc.getType() == null || struc.getOwner() == null)
            return true;
        return struc.getOwner().getNamespace()
	    .getOwnedElements().contains(struc.getType());
    }
    
    private boolean isValidNamespace(MAssociation assoc, MNamespace ns) {
        Iterator it = assoc.getConnections().iterator();
        List namespaces = new ArrayList();
        while (it.hasNext()) {
            MAssociationEnd end = (MAssociationEnd) it.next();
            namespaces.add(end.getType().getNamespace());
        }
        it = namespaces.iterator();
        while (it.hasNext()) {
            MNamespace ns1 = (MNamespace) it.next();
            if (it.hasNext()) {
                MNamespace ns2 = (MNamespace) it.next();
                // TODO: this contains a small error (ns can be part
                // of hierarchy of namespaces, that's not taken into
                // account)
                if (ns == getFirstSharedNamespace(ns1, ns2))
                    return true;
            }
        }
        return false;
    }
    
    private boolean isValidNamespace(
				     MGeneralizableElement gen,
				     MNamespace ns) {
        Iterator it = gen.getParents().iterator();
        while (it.hasNext()) {
            MGeneralizableElement gen2 = (MGeneralizableElement) it.next();
            if (!ns.getOwnedElements().contains(gen2)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Gets the first namespace two namespaces share. That is: it
     * returns the first namespace that owns the given namespaces
     * itself or some owner of the given namespaces.<p>
     *
     * @param ns1 is the first name space
     * @param ns2 is the second name space
     * @return MNamespace
     */
    public MNamespace getFirstSharedNamespace(MNamespace ns1, MNamespace ns2) {
        if (ns1 == null || ns2 == null)
            return null;
        if (ns1 == ns2)
            return ns1;
        boolean ns1Owner =
            ModelManagementHelper.getHelper()
	        .getAllNamespaces(ns1).contains(ns2);
        boolean ns2Owner =
            ModelManagementHelper.getHelper()
	        .getAllNamespaces(ns2).contains(ns1);
        if (ns1Owner)
            return ns1;
        if (ns2Owner)
            return ns2;
        return getFirstSharedNamespace(ns1.getNamespace(), ns2.getNamespace());
    }

    /**
     * Returns all possible namespaces that may be selected by some given
     * modelelement. Which namespaces are allowed, is decided in the method
     * isValidNamespace.<p>
     *
     * @param modelElement is the model element
     * @return Collection
     */
    public Collection getAllPossibleNamespaces(Object modelElement) {
        MModelElement m = (MModelElement) modelElement;
        List ret = new ArrayList();
        if (m == null)
            return ret;
        MNamespace model =
            (MModel) ProjectManager.getManager().getCurrentProject().getRoot();
        if (isValidNamespace(m, model))
            ret.add(model);
        Iterator it =
            ModelManagementHelper.getHelper()
	        .getAllModelElementsOfKind(model, MNamespace.class)
	            .iterator();
        while (it.hasNext()) {
            MNamespace ns = (MNamespace) it.next();
            if (isValidNamespace(m, ns))
                ret.add(ns);
        }
        return ret;
    }

    /**
     * Returns the base classes (that are the classes that do not have any
     * generalizations) for some given namespace. Personally, this seems a
     * pointless operation to me but in GoModelToBaseElements this is done like
     * this for some reason.
     * TODO: find out if someone uses this.
     *
     * @param o is the given namespace.
     * @return Collection
     */
    public Collection getBaseClasses(Object o) {
        Collection col = new ArrayList();
        if (ModelFacade.isANamespace(o)) {
            Iterator it =
                ModelManagementHelper.getHelper()
		    .getAllModelElementsOfKind(o, MGeneralizableElement.class)
		        .iterator();
            while (it.hasNext()) {
                MGeneralizableElement gen = (MGeneralizableElement) it.next();
                if (gen.getGeneralizations().isEmpty()) {
                    col.add(gen);
                }
            }
        }
        return col;
    }

    /**
     * Returns all children from some given generalizableelement on
     * all levels (the complete tree excluding the generalizable
     * element itself).<p>
     *
     * @throws IllegalStateException if there is a circular reference.
     * @param o is the generalizable element
     * @return Collection
     */
    // TODO: Argument shall be typed to catch a lot of problems at 
    //       compile time.
    // TODO: The exception shall be declared explicitly.
    // TODO: Use an exception that needs to be caugh so users of
    //       getChildren won't forget that they need to catch it.
    public Collection getChildren(Object o) {
        Collection col = new ArrayList();
        if (ModelFacade.isAGeneralizableElement(o)) {
            Iterator it =
                ((MGeneralizableElement) o).getSpecializations().iterator();
            while (it.hasNext()) {
                getChildren(col, (MGeneralization) it.next());
            }
        }
        return col;
    }

    /**
     * Adds all children recursively to the Collection in the first argument.
     *
     * @param currentChildren collection to collect them in.
     * @param gen element whose children are added.
     * @throws IllegalStateException if there is a circular reference.
     */
    private void getChildren(Collection currentChildren, MGeneralization gen) 
	throws IllegalStateException
    {
	MGeneralizableElement child = gen.getChild();
	if (currentChildren.contains(child))
	    throw new IllegalStateException("Circular inheritance occured.");
	currentChildren.add(child);
	Iterator it = child.getSpecializations().iterator();
	while (it.hasNext()) {
	    getChildren(currentChildren, (MGeneralization) it.next());
	}
    }

    /**
     * Returns all interfaces that are realized by the given class or
     * by its superclasses. It's possible that interfaces occur twice
     * in the collection returned. In that case there is a double
     * reference to that interface.
     *
     * @param o is the given class
     * @return Collection
     */
    public Collection getAllRealizedInterfaces(Object o) {
        return internalGetAllRealizedInterfaces(
						o,
						new ArrayList(),
						new HashSet());
    }

    /**
     * Helper method for getAllRealizedInterfaces.
     * @param o
     * @param col
     * @param visited
     * @return Collection
     */
    private Collection internalGetAllRealizedInterfaces(
				Object o, Collection col, Set visited) {
        visited.add(o);
        if (o != null) {
            if (ModelFacade.isAClass(o)) {
                MClass clazz = (MClass) o;
                Collection supDependencies = clazz.getClientDependencies();
                Iterator it = supDependencies.iterator();
                while (it.hasNext()) {
                    MDependency dependency = (MDependency) it.next();
                    MStereotype stereo = dependency.getStereotype();
                    if (ModelFacade.isAAbstraction(dependency)
                        && stereo != null
			&& "realize".equals(stereo.getName())
			&& "Abstraction".equals(stereo.getBaseClass())) {

                        col.addAll(dependency.getSuppliers());

                    }
                }
                Collection superTypes = getSupertypes(o);
                it = superTypes.iterator();
                while (it.hasNext()) {
		    Object obj = it.next();
		    if (!visited.contains(obj)) {
			internalGetAllRealizedInterfaces(obj, col, visited);
		    }
                }
            }
        }
        return col;
    }
    
    public final boolean hasCompositeEnd(Object association)
    {
        if (!(association instanceof MAssociation)) {
            throw new IllegalArgumentException();
	}
        
        MAssociation association1 = (MAssociation) association;
        
	List ends = association1.getConnections();
	for (Iterator iter = ends.iterator(); iter.hasNext();) {
	    MAssociationEnd end = (MAssociationEnd) iter.next();
	    if (end.getAggregation() == MAggregationKind.COMPOSITE)
		return true;
	}
	return false;
    }
    
    /**
     * @param associationEnd is the association end
     * @param kindType the MAggregationKind as a string in lower case letter,
     *                 eg: composite.
     * @return true if the aggregation kinds are the same.
     */
    public final boolean equalsAggregationKind(Object associationEnd,
                                               String kindType) {
        if (!(associationEnd instanceof MAssociationEnd)) {
            throw new IllegalArgumentException();
	}
        
        MAssociationEnd associationEnd1 = (MAssociationEnd) associationEnd;
        
        if (kindType.equals("composite")) {
            return MAggregationKind.COMPOSITE.equals(
                            associationEnd1.getAggregation());
        }
        else {
            throw new IllegalArgumentException("kindType: " + kindType
					       + " not supported");
        }
    }

    /**
     * Get the current model.
     *
     * @return the current model.
     */
    private MNamespace getCurrentModel() {
	Project project = ProjectManager.getManager().getCurrentProject();
	return (MModel) project.getModel();
    }
}
