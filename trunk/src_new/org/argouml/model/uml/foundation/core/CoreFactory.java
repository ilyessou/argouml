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

import org.argouml.application.api.Notation;
import org.argouml.application.api.NotationName;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.AbstractUmlModelFactory;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.model.uml.foundation.datatypes.DataTypesHelper;
import org.argouml.model.uml.foundation.extensionmechanisms.ExtensionMechanismsFactory;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;
import org.argouml.ui.ArgoDiagram;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

import ru.novosoft.uml.MElementListener;
import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.state_machines.MEvent;
import ru.novosoft.uml.foundation.core.MAbstraction;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationClass;
import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MBinding;
import ru.novosoft.uml.foundation.core.MClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MComment;
import ru.novosoft.uml.foundation.core.MComponent;
import ru.novosoft.uml.foundation.core.MConstraint;
import ru.novosoft.uml.foundation.core.MDataType;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MElement;
import ru.novosoft.uml.foundation.core.MElementResidence;
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
import ru.novosoft.uml.foundation.core.MPermission;
import ru.novosoft.uml.foundation.core.MPresentationElement;
import ru.novosoft.uml.foundation.core.MRelationship;
import ru.novosoft.uml.foundation.core.MStructuralFeature;
import ru.novosoft.uml.foundation.core.MTemplateParameter;
import ru.novosoft.uml.foundation.core.MUsage;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;
import ru.novosoft.uml.foundation.data_types.MBooleanExpression;
import ru.novosoft.uml.foundation.data_types.MCallConcurrencyKind;
import ru.novosoft.uml.foundation.data_types.MChangeableKind;
import ru.novosoft.uml.foundation.data_types.MMultiplicity;
import ru.novosoft.uml.foundation.data_types.MOrderingKind;
import ru.novosoft.uml.foundation.data_types.MParameterDirectionKind;
import ru.novosoft.uml.foundation.data_types.MScopeKind;
import ru.novosoft.uml.foundation.data_types.MVisibilityKind;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.model_management.MModel;

/**
 * Factory to create UML classes for the UML
 * Foundation::Core package.
 *
 * Feature, StructuralFeature, and PresentationElement
 * do not have a create methods since
 * it is called an "abstract metaclass" in the
 * UML specifications.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @author Jaap Branderhorst
 * @stereotype singleton
 */

public class CoreFactory extends AbstractUmlModelFactory {

    /** Singleton instance.
     */
    private static final CoreFactory SINGLETON = new CoreFactory();

    /**
     * Singleton instance access method.
     *
     * @return the singleton
     */
    public static CoreFactory getFactory() {
	return SINGLETON;
    }

    /** Don't allow instantiation
     */
    private CoreFactory() {
    }

    /** Create an empty but initialized instance of a UML Abstraction.
     *
     *  @return an initialized UML Abstraction instance.
     */
    public Object createAbstraction() {
	Object modelElement =
	    MFactory.getDefaultFactory().createAbstraction();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Build an empty but initialized instance of a UML Abstraction
     * with a given name.
     *
     * @param name The name.
     * @return an initialized UML Abstraction instance.
     */
    public Object buildAbstraction(String name,
            Object supplier, 
            Object client) {
        if (!(client instanceof MClassifier)
                || !(supplier instanceof MClassifier)) {
            throw new IllegalArgumentException(
                "The supplier and client of an abstration" +
                "should be classifiers" );
        }
        MAbstraction abstraction = (MAbstraction)createAbstraction();
        super.initialize(abstraction);
        ModelFacade.setName(abstraction, name);
        abstraction.addClient((MClassifier)client);
        abstraction.addSupplier((MClassifier)supplier);
        return abstraction;
    }

    /** Create an empty but initialized instance of a UML Association.
     *
     *  @return an initialized UML Association instance.
     */
    public MAssociation createAssociation() {
	MAssociation modelElement =
	    MFactory.getDefaultFactory().createAssociation();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML AssociationClass.
     *
     *  @return an initialized UML AssociationClass instance.
     */
    public MAssociationClass createAssociationClass() {
	MAssociationClass modelElement =
	    MFactory.getDefaultFactory().createAssociationClass();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML AssociationEnd.
     *
     *  @return an initialized UML AssociationEnd instance.
     */
    public MAssociationEnd createAssociationEnd() {
	MAssociationEnd modelElement =
	    MFactory.getDefaultFactory().createAssociationEnd();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Attribute.
     *
     *  @return an initialized UML Attribute instance.
     */
    public MAttribute createAttribute() {
	MAttribute modelElement =
	    MFactory.getDefaultFactory().createAttribute();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Binding.
     *
     *  @return an initialized UML Binding instance.
     */
    public MBinding createBinding() {
	MBinding modelElement = MFactory.getDefaultFactory().createBinding();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Class.
     *
     *  @return an initialized UML Class instance.
     */
    public MClass createClass() {
	MClass modelElement = MFactory.getDefaultFactory().createClass();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Classifier.
     *
     *  @return an initialized UML Classifier instance.
     */
    public MClassifier createClassifier() {
	MClassifier modelElement =
	    MFactory.getDefaultFactory().createClassifier();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Comment.
     *
     *  @return an initialized UML Comment instance.
     */
    public MComment createComment() {
	MComment modelElement = MFactory.getDefaultFactory().createComment();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UML Component.
     *
     * @return an initialized UML Component instance.
     */
    public MComponent createComponent() {
	MComponent modelElement =
	    MFactory.getDefaultFactory().createComponent();
	super.initialize(modelElement);
	return modelElement;
    }



    /** Create an empty but initialized instance of a UML Constraint.
     *
     *  @return an initialized UML Constraint instance.
     */
    public MConstraint createConstraint() {
	MConstraint modelElement =
	    MFactory.getDefaultFactory().createConstraint();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML DataType.
     *
     *  @return an initialized UML DataType instance.
     */
    public MDataType createDataType() {
	MDataType modelElement = MFactory.getDefaultFactory().createDataType();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Dependency.
     *
     *  @return an initialized UML Dependency instance.
     */
    public MDependency createDependency() {
	MDependency modelElement =
	    MFactory.getDefaultFactory().createDependency();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML ElementResidence.
     *
     *  @return an initialized UML ElementResidence instance.
     */
    public MElementResidence createElementResidence() {
	MElementResidence modelElement =
	    MFactory.getDefaultFactory().createElementResidence();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Flow.
     *
     *  @return an initialized UML Flow instance.
     */
    public MFlow createFlow() {
	MFlow modelElement = MFactory.getDefaultFactory().createFlow();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Generalization.
     *
     *  @return an initialized UML Generalization instance.
     */
    public MGeneralization createGeneralization() {
	MGeneralization modelElement =
	    MFactory.getDefaultFactory().createGeneralization();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Interface.
     *
     *  @return an initialized UML Interface instance.
     */
    public MInterface createInterface() {
	MInterface modelElement =
	    MFactory.getDefaultFactory().createInterface();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Method.
     *
     *  @return an initialized UML Method instance.
     */
    public MMethod createMethod() {
	MMethod modelElement = MFactory.getDefaultFactory().createMethod();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Namespace.
     *
     *  @return an initialized UML Namespace instance.
     */
    public MNamespace createNamespace() {
	MNamespace modelElement =
	    MFactory.getDefaultFactory().createNamespace();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Node.
     *
     *  @return an initialized UML Node instance.
     */
    public MNode createNode() {
	MNode modelElement = MFactory.getDefaultFactory().createNode();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Operation.
     *
     *  @return an initialized UML Operation instance.
     */
    public MOperation createOperation() {
	MOperation modelElement =
	    MFactory.getDefaultFactory().createOperation();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Parameter.
     *
     *  @return an initialized UML Parameter instance.
     */
    public MParameter createParameter() {
	MParameter modelElement =
	    MFactory.getDefaultFactory().createParameter();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Permission.
     *
     *  @return an initialized UML Permission instance.
     */
    public MPermission createPermission() {
	MPermission modelElement =
	    MFactory.getDefaultFactory().createPermission();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Relationship.
     *
     *  @return an initialized UML Relationship instance.
     */
    public MRelationship createRelationship() {
	MRelationship modelElement =
	    MFactory.getDefaultFactory().createRelationship();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML TemplateParameter.
     *
     *  @return an initialized UML TemplateParameter instance.
     */
    public MTemplateParameter createTemplateParameter() {
	MTemplateParameter modelElement =
	    MFactory.getDefaultFactory().createTemplateParameter();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Usage.
     *
     *  @return an initialized UML Usage instance.
     */
    public MUsage createUsage() {
	MUsage modelElement = MFactory.getDefaultFactory().createUsage();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Builds a default binary association with two default association ends.
     * @param c1 The first classifier to connect to
     * @param nav1 The navigability of the Associaton end
     * @param agg1 The aggregation type of the second Associaton end
     * @param c2 The second classifier to connect to
     * @param nav2 The navigability of the second Associaton end
     * @param agg2 The aggregation type of the second Associaton end
     * @return MAssociation
     * @throws IllegalArgumentException if either Classifier is null
     */
    private MAssociation buildAssociation(MClassifier c1,
					 boolean nav1,
					 MAggregationKind agg1,
					 MClassifier c2,
					 boolean nav2,
					 MAggregationKind agg2) {
        if (c1 == null || c2 == null) {
            throw new IllegalArgumentException("one of "
					       + "the classifiers to be "
					       + "connected is null");
        }
        MNamespace ns1 = c1.getNamespace();
        MNamespace ns2 = c2.getNamespace();
        if (ns1 == null || ns2 == null) {
            throw new IllegalArgumentException("one of "
					       + "the classifiers does not "
					       + "belong to a namespace");
        }
        MAssociation assoc =
            UmlFactory.getFactory().getCore().createAssociation();
        assoc.setName("");
        assoc.setNamespace(CoreHelper.getHelper().getFirstSharedNamespace(ns1,
        								  ns2));
        buildAssociationEnd(
			    assoc,
			    null,
			    c1,
			    null,
			    null,
			    nav1,
			    null,
			    agg1,
			    null,
			    null,
			    null);
        buildAssociationEnd(
			    assoc,
			    null,
			    c2,
			    null,
			    null,
			    nav2,
			    null,
			    agg2,
			    null,
			    null,
			    null);
        return assoc;
    }

    /**
     * Builds a binary associationrole on basis of two classifierroles,
     * navigation and aggregation.
     *
     * @param fromClassifier   the first given classifier
     * @param aggregationKind1 the first aggregationkind
     * @param toClassifier     the second given classifier
     * @param aggregationKind2 the second aggregationkind
     * @param unidirectional true if unidirectional
     * @return the newly build binary associationrole
     */
    public Object buildAssociation(
					 Object fromClassifier,
					 Object aggregationKind1,
					 Object toClassifier,
					 Object aggregationKind2,
					 Boolean unidirectional) {
        if (fromClassifier == null || toClassifier == null)
                throw new IllegalArgumentException("one of "
        				       + "the classifiers to be "
        				       + "connected is null");
        MClassifier from = (MClassifier)fromClassifier;
        MClassifier to = (MClassifier)toClassifier;
        MAggregationKind agg1 = (MAggregationKind)aggregationKind1;
        MAggregationKind agg2 = (MAggregationKind)aggregationKind2;
        
        MNamespace ns1 = from.getNamespace();
        MNamespace ns2 = to.getNamespace();
        if (ns1 == null || ns2 == null)
                throw new IllegalArgumentException("one of "
        				       + "the classifiers does not "
        				       + "belong to a namespace");
        MAssociation assoc =
                UmlFactory.getFactory().getCore().createAssociation();
        assoc.setName("");
        assoc.setNamespace(
        		   CoreHelper.getHelper().getFirstSharedNamespace(ns1,
        								  ns2));
        
        boolean nav1 = true;
        boolean nav2 = true;

        if (from instanceof MInterface) {
            nav2 = false;
            agg2 = agg1;
            agg1 = null;
        } else if (to instanceof MInterface) {
            nav1 = false;
        } else {
            nav1 = !Boolean.TRUE.equals(unidirectional);
            nav2 = true;
        }

        buildAssociationEnd(
			    assoc,
			    null,
			    from,
			    null,
			    null,
			    nav1,
			    null,
			    agg1,
			    null,
			    null,
			    null);
        buildAssociationEnd(
			    assoc,
			    null,
			    to,
			    null,
			    null,
			    nav2,
			    null,
			    agg2,
			    null,
			    null,
			    null);

        return assoc;
    }


    /**
     * Builds a binary associations between two classifiers with
     * default values for the association ends and the association
     * itself.<p>
     *
     * @param classifier1 The first classifier to connect
     * @param classifier2 The second classifier to connect
     * @return MAssociation
     */
    public MAssociation buildAssociation(
            Object classifier1,
			Object classifier2) {
        MClassifier c1 = (MClassifier) classifier1;
        MClassifier c2 = (MClassifier) classifier2;
        return buildAssociation(c1, true, MAggregationKind.NONE,
                                c2, true, MAggregationKind.NONE);
    }

    /**
     * Builds a default binary association with two default association ends.
     * @param c1 The first classifier to connect to
     * @param nav1 The navigability of the Associaton end
     * @param c2 The second classifier to connect to
     * @param nav2 The navigability of the second Associaton end
     * @return MAssociation
     */
    private MAssociation buildAssociation(
					 MClassifier c1,
					 boolean nav1,
					 MClassifier c2,
					 boolean nav2) {
        return buildAssociation(c1, nav1, MAggregationKind.NONE,
                                c2, nav2, MAggregationKind.NONE);
    }

    /**
     * Builds a binary association with a direction, aggregation
     * and a given name.
     *
     * @param c1 The first classifier to connect to
     * @param nav1 The navigability of the Associaton end
     * @param c2 The second classifier to connect to
     * @param nav2 The navigability of the second Associaton end
     * @param name the given name
     * @return association
     */
    public Object buildAssociation(Object c1, boolean nav1,
				   Object c2, boolean nav2, String name)
    {
        MAssociation assoc =
	    buildAssociation((MClassifier) c1, nav1, MAggregationKind.NONE,
			     (MClassifier) c2, nav2, MAggregationKind.NONE);
        if (assoc != null)
            assoc.setName(name);
        return assoc;
    }

    /**
     * Builds a default binary association with two default association ends.
     * @param c1 The first classifier to connect to
     * @param agg1 The aggregation type of the second Associaton end
     * @param c2 The second classifier to connect to
     * @param agg2 The aggregation type of the second Associaton end
     * @return MAssociation
     */
    private MAssociation buildAssociation(
					 MClassifier c1,
					 MAggregationKind agg1,
					 MClassifier c2,
					 MAggregationKind agg2) {
        return buildAssociation(c1, true, agg1,
                                c2, true, agg2);
    }

    /**
     * Builds an associationClass between classifier end1 and end2 with a
     * default class.<p>
     *
     * @param end1 the first given classifier
     * @param end2 the second given classifier
     * @return MAssociationClass
     */
    public MAssociationClass buildAssociationClass(
						   MClassifier end1,
						   MClassifier end2) {
	if (end1 == null
	    || end2 == null
	    || end1 instanceof MAssociationClass
	    || end2 instanceof MAssociationClass)
	    throw new IllegalArgumentException(""
					       + "either one of the arguments "
					       + "was null or "
					       + "was instanceof "
					       + "MAssociationClass");
	return buildAssociatonClass(buildClass(), end1, end2);
    }

    /**
     * Builds a fully configurable association end. All variables for
     * an associationend can be given as parameter.
     * @param assoc The associaton this end will be part of
     * @param name The name of the association end
     * @param type The type (classifier) the end will connect. The end
     * is a connection piece between an association and a classifier
     * @param multi The multiplicity
     * @param stereo The stereotype
     * @param navigable The navigability. True if this association end
     *                  can be 'passed' from the other classifier.
     * @param order Ordering of the association
     * @param aggregation the aggregationkind
     * @param scope the scope kind
     * @param changeable the changeablekind
     * @param visibility the visibilitykind
     * @return MAssociationEnd
     */
    public MAssociationEnd buildAssociationEnd(
					       MAssociation assoc,
					       String name,
					       MClassifier type,
					       MMultiplicity multi,
					       MStereotype stereo,
					       boolean navigable,
					       MOrderingKind order,
					       MAggregationKind aggregation,
					       MScopeKind scope,
					       MChangeableKind changeable,
					       MVisibilityKind visibility) {
        // wellformednessrules and preconditions
        if (assoc == null || type == null) {
            throw new IllegalArgumentException("either type or association "
					       + "are null");
        }
        if (type instanceof MDataType || type instanceof MInterface) {
            if (!navigable) {
                throw new IllegalArgumentException("type is either datatype "
						   + "or interface and is "
						   + "navigable to");
            }
            List ends = new ArrayList();
            ends.addAll(assoc.getConnections());
            Iterator it = ends.iterator();
            while (it.hasNext()) {
                MAssociationEnd end = (MAssociationEnd) it.next();
                if (end.isNavigable()) {
                    throw new IllegalArgumentException("type is either "
						       + "datatype or "
						       + "interface and is "
						       + "navigable to");
                }
            }
        }
        if (aggregation != null
	    && aggregation.equals(MAggregationKind.COMPOSITE))
	{
            if (multi != null && multi.getUpper() > 1) {
                throw new IllegalArgumentException("aggregation is composite "
						   + "and multiplicity > 1");
            }
        }

        MAssociationEnd end =
	    UmlFactory.getFactory().getCore().createAssociationEnd();
        end.setAssociation(assoc);
        end.setType(type);
        end.setName(name);
        if (multi != null) {
            end.setMultiplicity(multi);
        } else {
            end.setMultiplicity(MMultiplicity.M1_1);
        }
        if (stereo != null) {
            end.setStereotype(stereo);
        }
        end.setNavigable(navigable);
        if (order != null) {
            end.setOrdering(order);
        } else {
            end.setOrdering(MOrderingKind.UNORDERED);
        }
        if (aggregation != null) {
            end.setAggregation(aggregation);
        } else {
            end.setAggregation(MAggregationKind.NONE);
        }
        if (scope != null) {
            end.setTargetScope(scope);
        } else {
            end.setTargetScope(MScopeKind.INSTANCE);
        }
        if (changeable != null) {
            end.setChangeability(changeable);
        } else {
            end.setChangeability(MChangeableKind.CHANGEABLE);
        }
        if (visibility != null) {
            end.setVisibility(visibility);
        } else {
            end.setVisibility(MVisibilityKind.PUBLIC);
        }
        return end;
    }

    /**
     * @param type the given classifier
     * @param assoc the given association
     * @return the newly build associationend
     */
    public MAssociationEnd buildAssociationEnd(
					       MClassifier type,
					       MAssociation assoc) {
	if (type == null || assoc == null)
	    throw new IllegalArgumentException("one of the arguments is null");
	return buildAssociationEnd(
				   assoc,
				   "",
				   type,
				   null,
				   null,
				   true,
				   null,
				   null,
				   null,
				   null,
				   MVisibilityKind.PUBLIC);
    }

    /**
     * Builds an association class from a class and two classifiers
     * that should be associated. Both ends of the associationclass
     * are navigable.<p>
     *
     * @param cl the class
     * @param end1 the first classifier
     * @param end2 the second classifier
     * @return MAssociationClass
     */
    public MAssociationClass buildAssociatonClass(MClass cl,
						  MClassifier end1,
						  MClassifier end2) {
	if (end1 == null
	    || end2 == null
	    || cl == null
	    || end1 instanceof MAssociationClass
	    || end2 instanceof MAssociationClass)
	    throw new IllegalArgumentException("either one of the arguments "
					       + "was null or was instanceof "
					       + "MAssociationClass");
	MAssociationClass assoc = createAssociationClass();
	assoc.setName(cl.getName());
	assoc.setAbstract(cl.isAbstract());
	assoc.setActive(cl.isActive());
	assoc.setAssociationEnds(cl.getAssociationEnds());
	assoc.setClassifierRoles(cl.getClassifierRoles());
	assoc.setClassifierRoles1(cl.getClassifierRoles1());
	assoc.setClassifiersInState(cl.getClassifiersInState());
	assoc.setClientDependencies(cl.getClientDependencies());
	assoc.setCollaborations(cl.getCollaborations());
	assoc.setCollaborations1(cl.getCollaborations1());
	assoc.setComments(cl.getComments());
	assoc.setConstraints(cl.getConstraints());
	assoc.setCreateActions(cl.getCreateActions());
	assoc.setFeatures(cl.getFeatures());
	assoc.setExtensions(cl.getExtensions());
	assoc.setGeneralizations(cl.getGeneralizations());
	assoc.setInstances(cl.getInstances());
	assoc.setLeaf(cl.isLeaf());
	assoc.setNamespace(cl.getNamespace());
	assoc.setObjectFlowStates(cl.getObjectFlowStates());
	assoc.setParameters(cl.getParameters());
	assoc.setParticipants(cl.getParticipants());
	assoc.setPartitions1(cl.getPartitions1());
	assoc.setPowertypeRanges(cl.getPowertypeRanges());
	assoc.setPresentations(cl.getPresentations());
	assoc.setRoot(cl.isRoot());
	assoc.setSourceFlows(cl.getSourceFlows());
	assoc.setSpecification(cl.isSpecification());
	assoc.setStereotype(cl.getStereotype());
	assoc.setStructuralFeatures(cl.getStructuralFeatures());
	assoc.setTaggedValues(cl.getTaggedValues());
	assoc.setVisibility(cl.getVisibility());
	buildAssociationEnd(
			    assoc,
			    null,
			    end1,
			    null,
			    null,
			    true,
			    null,
			    null,
			    null,
			    null,
			    null);
	buildAssociationEnd(
			    assoc,
			    null,
			    end2,
			    null,
			    null,
			    true,
			    null,
			    null,
			    null,
			    null,
			    null);
	return assoc;
    }

    /**
     * Builds a default attribute.
     * @return MAttribute
     */
    public MAttribute buildAttribute() {
	//build the default attribute
	// this should not be here via the ProjectBrowser but the CoreHelper
	// should provide this functionality
	Project p = ProjectManager.getManager().getCurrentProject();
	MClassifier intType = (MClassifier) p.findType("int");
	if (p.getModel() != intType.getNamespace()
	    && !(ModelManagementHelper.getHelper()
		 .getAllNamespaces(p.getModel())
		     .contains(intType.getNamespace())))
	{
	    intType.setNamespace((MModel) p.getModel());
	}
	MAttribute attr = createAttribute();
	attr.setName("newAttr");
	attr.setMultiplicity(UmlFactory.getFactory()
			     .getDataTypes().createMultiplicity(1, 1));
	attr.setStereotype(null);
	attr.setOwner(null);
	attr.setType(intType);
	attr.setInitialValue(null);
	attr.setVisibility(MVisibilityKind.PUBLIC);
	attr.setOwnerScope(MScopeKind.INSTANCE);
	attr.setChangeability(MChangeableKind.CHANGEABLE);
	attr.setTaggedValue("transient", "false");
	attr.setTaggedValue("volatile", "false");
	attr.setTargetScope(MScopeKind.INSTANCE);

	return attr;
    }

    /**
     * Builds a default attribute with a given name.
     *
     * @param name the given name
     * @return attribute the newly build attribute
     */
    public Object buildAttribute(String name) {
        MAttribute attr = buildAttribute();
        if (attr != null)
            attr.setName(name);
        return attr;
    }

    /**
     * Builds an attribute owned by some classifier cls. I don't know
     * if this is legal for an interface (purely UML speaking). In
     * this method it is.<p>
     *
     * @param handle the given classifier
     * @return MAttribute the newly build attribute
     */
    public MAttribute buildAttribute(Object handle) {
	if (!ModelFacade.isAClassifier(handle))
	    return null;
	MClassifier cls = (MClassifier) handle;
	MAttribute attr = buildAttribute();
	cls.addFeature(attr);
	// we set the listeners to the figs here too
	// it would be better to do that in the figs themselves
	Project p = ProjectManager.getManager().getCurrentProject();
	Iterator it = p.findFigsForMember(cls).iterator();
	while (it.hasNext()) {
	    MElementListener listener = (MElementListener) it.next();
	    // UmlModelEventPump.getPump().removeModelEventListener(listener,
	    // attr);
	    UmlModelEventPump.getPump().addModelEventListener(listener, attr);
	}
	return attr;
    }

    /**
     * Builds a binding between a client modelelement and a supplier
     * modelelement.<p>
     *
     * @param client 
     * @param supplier
     * @return MBinding
     */
    public MBinding buildBinding(MModelElement client,
				 MModelElement supplier) {
	// 2002-07-08
	// Jaap Branderhorst
	// checked for existence of client
	Collection clientDependencies = supplier.getClientDependencies();
	if (!clientDependencies.isEmpty()) {
	    if (clientDependencies.contains(client)) {
		throw new IllegalArgumentException(
						   "Supplier has allready "
						   + "client "
						   + client.getName()
						   + " as Client");
	    }
	}
	// end new code
	MBinding binding = createBinding();
	binding.addSupplier(supplier);
	binding.addClient(client);
	if (supplier.getNamespace() != null)
	    binding.setNamespace(supplier.getNamespace());
	else if (client.getNamespace() != null)
	    binding.setNamespace(client.getNamespace());
	return binding;
    }

    /**
     * Builds a default implementation for a class. The class is not owned by
     * any model element by default. Users should not forget to add ownership
     * @return MClass
     */
    public MClass buildClass() {
	MClass cl = createClass();
	// cl.setNamespace(ProjectBrowser.getInstance().getProject()
	// .getModel());
	cl.setName("");
	cl.setStereotype(null);
	cl.setAbstract(false);
	cl.setActive(false);
	cl.setRoot(false);
	cl.setLeaf(false);
	cl.setSpecification(false);
	cl.setVisibility(MVisibilityKind.PUBLIC);
	return cl;
    }

    /**
     * Builds a class with a given namespace.
     *
     * @param owner
     * @return MClass
     * @see #buildClass()
     */
    public MClass buildClass(Object owner) {
	MClass cl = buildClass();
	if (owner instanceof MNamespace)
	    cl.setNamespace((MNamespace) owner);
	return cl;
    }

    /**
     * Builds a class with a given name.
     *
     * @param name
     * @return MClass
     * @see #buildClass()
     */
    public MClass buildClass(String name) {
	MClass cl = buildClass();
	cl.setName(name);
	return cl;
    }

    /**
     * Builds a class with a given name and namespace.
     *
     * @param name
     * @param owner
     * @return MClass
     * @see #buildClass()
     */
    public MClass buildClass(String name, Object owner) {
	MClass cl = buildClass();
	cl.setName(name);
	if (owner instanceof MNamespace)
	    cl.setNamespace((MNamespace) owner);
	return cl;
    }

    /**
     * Builds a default implementation for an interface. The interface
     * is not owned by any model element by default. Users should not
     * forget to add ownership.
     *
     * @return MInterface
     */
    public Object buildInterface() {
	MInterface cl = createInterface();
	// cl.setNamespace(ProjectBrowser.getInstance().getProject()
	// .getModel());
	cl.setName("");
	cl.setStereotype(null);
	cl.setAbstract(false);
	cl.setRoot(false);
	cl.setLeaf(false);
	cl.setSpecification(false);
	cl.setVisibility(MVisibilityKind.PUBLIC);
	return cl;
    }

    /**
     * Builds an interface with a given namespace.
     *
     * @param owner is the owner
     * @return MInterface
     * @see #buildInterface()
     */
    public Object buildInterface(Object owner) {
	MInterface cl = (MInterface) buildInterface();
	if (owner instanceof MNamespace)
	    cl.setNamespace((MNamespace) owner);
	return cl;
    }

    /**
     * Builds an interface with a given name.
     *
     * @param name is the given name.
     * @return MInterface
     * @see #buildInterface()
     */
    public Object buildInterface(String name) {
	MInterface cl = (MInterface) buildInterface();
	cl.setName(name);
	return cl;
    }

    /**
     * Builds an interface with a given name and namespace.
     *
     * @param name is the given name
     * @param owner is the namespace
     * @return MInterface
     * @see #buildInterface()
     */
    public Object buildInterface(String name, Object owner) {
	MInterface cl = (MInterface) buildInterface();
	cl.setName(name);
	if (owner instanceof MNamespace)
	    cl.setNamespace((MNamespace) owner);
	return cl;
    }

    /**
     * Builds a datatype with a given name and namespace.
     *
     * @param name is the name
     * @param owner is the namespace
     * @return an initialized UML DataType instance.
     */
    public Object buildDataType(String name, Object owner) {
	MDataType dt = createDataType();
	dt.setName(name);
	if (owner instanceof MNamespace)
	    dt.setNamespace((MNamespace) owner);
	return dt;
    }

    /**
     * Builds a modelelement dependency between two modelelements.<p>
     *
     * @param clientObj is the client
     * @param supplierObj is the supplier
     * @return MDependency
     */
    public MDependency buildDependency(Object clientObj,
				       Object supplierObj) {

	MModelElement client = (MModelElement) clientObj;
	MModelElement supplier = (MModelElement) supplierObj;
	if (client == null
	    || supplier == null
	    || client.getNamespace() == null
	    || supplier.getNamespace() == null)
	    throw new IllegalArgumentException("client or supplier is null "
					       + "or their namespaces.");
	MDependency dep = createDependency();
	dep.addSupplier(supplier);
	dep.addClient(client);
	if (supplier.getNamespace() != null)
	    dep.setNamespace(supplier.getNamespace());
	else if (client.getNamespace() != null)
	    dep.setNamespace(client.getNamespace());
	return dep;
    }

    /**
     * Builds a modelelement permission between two modelelements.
     *
     * @param clientObj is the client
     * @param supplierObj is the supplier
     * @return MPermission
     */
    public MPermission buildPermission(Object clientObj,
				       Object supplierObj) {

	MModelElement client = (MModelElement) clientObj;
	MModelElement supplier = (MModelElement) supplierObj;
	if (client == null
	    || supplier == null
	    || client.getNamespace() == null
	    || supplier.getNamespace() == null)
	    throw new IllegalArgumentException("client or supplier is null "
					       + "or their namespaces.");
	MPermission per = createPermission();
	per.addSupplier(supplier);
	per.addClient(client);
	if (supplier.getNamespace() != null)
	    per.setNamespace(supplier.getNamespace());
	else if (client.getNamespace() != null)
	    per.setNamespace(client.getNamespace());
	ExtensionMechanismsFactory.getFactory()
	    .buildStereotype(per, "import", per.getNamespace());
	return per;
    }

    /**
     * Builds a generalization between a parent and a child with a given name.
     *
     * @param child is the child
     * @param parent is the parent
     * @param name is the given name
     * @return generalization
     */
    public Object buildGeneralization(Object child, Object parent,
				      String name)
    {
        if (child == null
	    || parent == null
	    || !(child instanceof MGeneralizableElement)
	    || !(parent instanceof MGeneralizableElement))
            return null;
        Object gen = buildGeneralization(child, parent);
        if (gen != null)
            ((MGeneralization) gen).setName(name);
        return gen;
    }

    /**
     * Builds a generalization between a parent and a child. Does not check if
     * multiple inheritance is allowed for the current notation.
     *
     * @param child1 is the child
     * @param parent1 is the parent
     * @return MGeneralization
     */
    public MGeneralization buildGeneralization(Object child1, Object parent1)
    {
        if (!(child1 instanceof MGeneralizableElement)
	    || !(parent1 instanceof MGeneralizableElement))
            throw new IllegalArgumentException();

        MGeneralizableElement child = (MGeneralizableElement) child1;
        MGeneralizableElement parent = (MGeneralizableElement) parent1;

        if (parent.getParents().contains(child))
            return null;
        if (!child.getClass().equals(parent.getClass()))
            return null;
        Iterator it = parent.getGeneralizations().iterator();
        while (it.hasNext()) {
            MGeneralization gen = (MGeneralization) it.next();
            if (gen.getParent().equals(child))
                return null;
        }
        if (parent.getNamespace() == null)
            return null;
        if (parent.isLeaf())
            return null;
        if (child.isRoot())
            return null;

        MGeneralization gen = createGeneralization();
        gen.setParent(parent);
        gen.setChild(child);
        if (parent.getNamespace() != null)
            gen.setNamespace(parent.getNamespace());
        else if (child.getNamespace() != null)
            gen.setNamespace(child.getNamespace());
        return gen;
    }

    /**
     * Builds a default method belonging to a certain operation. The
     * language of the body is set to the selected Notation
     * language. The body of the method is set to an emtpy string.
     *
     * @param op is the operation
     * @return MMethod
     */
    public MMethod buildMethod(MOperation op) {
	return buildMethod(op, Notation.getDefaultNotation(), "");
    }

    /**
     * Builds a method belonging to a certain operation.
     * @param op The operation this method belongs to
     * @param notation The notationname (language name) of the body
     * @param body The actual body of the method
     * @return MMethod
     */
    public MMethod buildMethod(
			       MOperation op,
			       NotationName notation,
			       String body) {
	MMethod method = createMethod();
	if (op != null) {
	    method.setSpecification(op);
	    MClassifier owner = op.getOwner();
	    if (owner != null) {
		method.setOwner(owner);
	    }
	}
	if (notation != null && notation.getName() != null) {
	    method.setBody(
			   UmlFactory
			   .getFactory()
			   .getDataTypes()
			   .createProcedureExpression(
						      notation.getName(),
						      body));
	}
	return method;
    }

    /**
     * Builds a method with a given name.
     *
     * @param name is the given name
     * @return method
     */
    public MMethod buildMethod(String name) {
        MMethod method = createMethod();
        if (method != null)
            method.setName(name);
        return method;
    }

    /**
     * Builds an operation for a classifier.<p>
     *
     * @param handle is the classifier.
     * @return MOperation
     */
    public MOperation buildOperation(Object handle) {
        if (!(handle instanceof MClassifier)) {
            throw new IllegalArgumentException("Handle is not a classifier");
        }
        MClassifier cls = (MClassifier) handle;
	MOperation oper = createOperation();
	oper.setName("newOperation");
	oper.setStereotype(null);
	oper.setOwner(cls);
	oper.setVisibility(MVisibilityKind.PUBLIC);
	oper.setAbstract(false);
	oper.setLeaf(false);
	oper.setRoot(false);
	oper.setQuery(false);
	oper.setOwnerScope(MScopeKind.INSTANCE);
        // Jaap Branderhorst 6-4-2003 commented out next line since an
        // operation cannot have two owners.  the owner must be the
        // owning classifier which must be set via the setOwner
        // method, not via the namespace.
	//
        // oper.setNamespace(cls);
	oper.setConcurrency(MCallConcurrencyKind.SEQUENTIAL);

	MParameter returnParameter = buildParameter(oper);
	returnParameter.setKind(MParameterDirectionKind.RETURN);
	returnParameter.setName("return");
	// we set the listeners to the figs here too it would be
	// better to do that in the figs themselves the
	// elementlistener for the parameter is allready set in
	// buildparameter(oper)
	Project p = ProjectManager.getManager().getCurrentProject();
	Iterator it = p.findFigsForMember(cls).iterator();
	while (it.hasNext()) {
	    MElementListener listener = (MElementListener) it.next();
	    // UmlModelEventPump.getPump().removeModelEventListener(listener,
	    // oper);
	    UmlModelEventPump.getPump().addModelEventListener(listener, oper);
	}
	return oper;
    }

    /**
     * Builds an operation with a given name for classifier.
     *
     * @param cls is the classifier.
     * @param name is the given name.
     * @return MOperation
     */
    public Object buildOperation(Object cls, String name) {
        MOperation oper = buildOperation(cls);
        if (oper != null)
            oper.setName(name);
        return oper;
    }

    /**
     * Constructs a default parameter.
     *
     * @return      The newly created parameter.
     */
    public MParameter buildParameter() {
	// this should not be here via the ProjectBrowser but the CoreHelper
	// should provide this functionality
	Project p = ProjectManager.getManager().getCurrentProject();
	MClassifier voidType = (MClassifier) p.findType("void");
	if (voidType.getModel() != p.getModel()) {
	    voidType.setNamespace((MModel) p.getModel());
	}
	MParameter res = UmlFactory.getFactory().getCore().createParameter();
	res.setName("");
	res.setStereotype(null);
	res.setType(voidType);
	res.setKind(MParameterDirectionKind.IN);
	res.setDefaultValue(null);

	return res;
    }

    /**
     * Constructs a default parameter and adds it to oper. The name is
     * unique in the operation so no code generation problems will
     * exist.
     *
     * @param oper  The operation where it is added to.
     *          If null, it is not added.
     * @return      The newly created parameter.
     */
    public MParameter buildParameter(MBehavioralFeature oper) {
	if (oper == null || oper.getOwner() == null)
	    throw new IllegalArgumentException("operation is null or does not "
					       + "have an owner");
	MParameter res = buildParameter();
	String name = "arg";
	int counter = 1;

	oper.addParameter(res);
	Iterator it = oper.getParameters().iterator();
	while (it.hasNext()) {
	    MParameter para = (MParameter) it.next();
	    if ((name + counter).equals(para.getName())) {
		counter++;
	    }
	}

	res.setName(name + counter);

	// we set the listeners to the figs here too
	// it would be better to do that in the figs themselves
	Project p = ProjectManager.getManager().getCurrentProject();
	it = p.findFigsForMember(oper).iterator();
	while (it.hasNext()) {
	    MElementListener listener = (MElementListener) it.next();
	    // UmlModelEventPump.getPump().removeModelEventListener(listener,
	    // res);
	    UmlModelEventPump.getPump().addModelEventListener(listener, res);
	}

	return res;
    }

    /**
     * Constructs a default parameter, adds it to oper and sets its type
     * (return etc.).<p>
     *
     * @param feature The operation where it is added to.
     *          If null, it is not added.
     * @param dk The directionkind. If null it is not set.
     * @return      The newly created parameter.
     */
    public Object buildParameter(Object feature, Object dk) {
        MBehavioralFeature oper = (MBehavioralFeature) feature;
        MParameterDirectionKind directionKind = (MParameterDirectionKind) dk;
	MParameter res = buildParameter(oper);
	if (directionKind != null) {
	    ModelFacade.setKind(res, directionKind);
	}
	return res;
    }

    /**
     * Adds a parameter initialized to default values to a given event
     * or behavioral feature
     * @param o an event or behavioral feature
     * @return MParameter
     */
    public MParameter buildParameter(Object o) {
	if (o instanceof MEvent) {
	    MEvent event = (MEvent) o;
	    MParameter res = buildParameter();
	    res.setKind(MParameterDirectionKind.IN);
	    //    removing this next line solves issue 2209
	    //res.setNamespace(event.getNamespace()); 
            event.addParameter(res);
	    return res;
	} else if (o instanceof MBehavioralFeature) {
	    return buildParameter((MBehavioralFeature) o);
	} else
	    return null;
    }

    /**
     * Builds a realization between some supplier (for example an
     * interface in Java) and a client who implements the realization.
     *
     * @param client is the client
     * @param supplier is the supplier
     * @return Object the created abstraction
     */
    public Object buildRealization(MModelElement client,
				   MModelElement supplier)
    {
	if (client == null
	    || supplier == null
	    || client.getNamespace() == null
	    || supplier.getNamespace() == null)
	{
	    throw new IllegalArgumentException("faulty arguments.");
	}
	Object realization =
	    UmlFactory.getFactory().getCore().createAbstraction();
	MNamespace nsc = client.getNamespace();
	MNamespace nss = supplier.getNamespace();
	MNamespace ns = null;
	if (nsc != null && nsc.equals(nss)) {
	    ns = nsc;
	} else {
	    ns =
		(MModel) ProjectManager.getManager().getCurrentProject()
		    .getModel();
	}
	ExtensionMechanismsFactory.getFactory().buildStereotype(realization,
								"realize", ns);
	ModelFacade.addClientDependency(client, realization);
	ModelFacade.addSupplierDependency(supplier, realization);
	return realization;
    }

    /**
     * Builds a usage between some client and a supplier. If client
     * and supplier do not have the same model, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param client is the client
     * @param supplier is the supplier
     * @return MUsage
     */
    public MUsage buildUsage(MModelElement client, MModelElement supplier) {
	if (client == null || supplier == null) {
	    throw new IllegalArgumentException("In buildUsage null arguments.");
	}
	if (client.getModel() != supplier.getModel()) {
	    throw new IllegalArgumentException("To construct a usage, the "
					       + "client and the supplier "
					       + "must be part of the same "
					       + "model.");
	}
	MUsage usage = UmlFactory.getFactory().getCore().createUsage();
	usage.addSupplier(supplier);
	usage.addClient(client);
	if (supplier.getNamespace() != null)
	    usage.setNamespace(supplier.getNamespace());
	else if (client.getNamespace() != null)
	    usage.setNamespace(client.getNamespace());
	return usage;
    }

    /**
     * Builds a comment inluding a reference to the given modelelement
     * to comment.  If the element is null, the comment is still build
     * since it is not mandatory to have an annotated element in the
     * comment.<p>
     *
     * @param element is the model element
     * @return MComment
     */
    public MComment buildComment(Object/*MModelElement*/ element) {
        MModelElement elementToComment = (MModelElement) element;
	MComment comment = createComment();
	if (elementToComment != null) {
	    comment.addAnnotatedElement(elementToComment);
	    comment.setNamespace(elementToComment.getModel());
	} else
	    comment.setNamespace((MModel) ProjectManager.getManager()
				 .getCurrentProject().getModel());

	return comment;
    }
    
    /**
     * Builds a comment owned by the namespace of the active diagram or by the model if the active diagram
     * does not have a namespace.
     * @return The comment build
     */
    public MComment buildComment() {
        MComment comment = createComment();
        Object ns = null;
        ArgoDiagram diagram = 
            ProjectManager.getManager().getCurrentProject().getActiveDiagram();
        ns = ((UMLMutableGraphSupport) diagram.getGraphModel()).getNamespace();
        if (ns == null || !ModelFacade.isANamespace(ns)) {
            ns = ProjectManager.getManager().getCurrentProject().getModel();
        }
        ModelFacade.setNamespace(comment, ns);
        return comment;
    }
    
    /**
     * Builds the model behind a connection between a comment and 
     * the annotated modelelement.
     *
     * @param from The comment or annotated element.
     * @param to The comment or annotated element.
     * @return A commentEdge representing the model behind the connection 
     *         between a comment and an annotated modelelement.
     */
    public CommentEdge buildCommentConnection(Object from, Object to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Either fromNode == null "
                    			       + "or toNode == null");
        }
        Object comment = null;
        Object annotatedElement = null;
        if (ModelFacade.isAComment(from)) {
            comment = from;
            annotatedElement = to;
        } else {
            comment = to;
            annotatedElement = from;
        }
        
        CommentEdge connection = new CommentEdge(from, to);
        ModelFacade.addAnnotatedElement(comment, annotatedElement);
        return connection;
        
    }

    /**
     * Builds a constraint that constraints the given modelelement.
     * The namespace of the constraint will be the same as the
     * namespace of the given modelelement.<p>
     *
     * @param constrElement The constrained element.
     * @return MConstraint
     */
    public MConstraint buildConstraint(Object/*MModelElement*/ constrElement) {
        MModelElement constrainedElement = (MModelElement) constrElement;
	if (constrainedElement == null)
	    throw new IllegalArgumentException("the constrained element is "
					       + "mandatory and may not be "
					       + "null.");
	MConstraint con = createConstraint();
	con.addConstrainedElement(constrainedElement);
	con.setNamespace(constrainedElement.getNamespace());
	return con;
    }

    /**
     * Builds a constraint with a given name and boolean expression.<p>
     *
     * @param name is the given name
     * @param bexpr boolean expression
     * @return constraint
     */
    public Object buildConstraint(String name, Object bexpr) {
	if (bexpr == null || !(bexpr instanceof MBooleanExpression))
	    throw new IllegalArgumentException("invalid boolean expression.");
	MConstraint con = createConstraint();
	if (name != null)
	    con.setName(name);
	con.setBody((MBooleanExpression) bexpr);
	return con;
    }

    public void deleteAbstraction(Object elem) {
    }

    public void deleteAssociation(MAssociation elem) {
    }

    public void deleteAssociationClass(MAssociationClass elem) {
    }

    /**
     * <p>
     * Does a 'cascading delete' to all modelelements that are associated
     * with this element that would be in an illegal state after deletion
     * of the element. Does not do an cascading delete for elements that
     * are deleted by the NSUML method remove. This method should not be called
     * directly.
     * </p>
     * <p>
     * In the case of an associationend these are the following elements:
     * </p>
     * <p>
     * - Binary Associations that 'loose' one of the associationends by this
     * deletion.
     * </p>
     * @param elem
     * @see UmlFactory#delete(Object)
     */
    public void deleteAssociationEnd(MAssociationEnd elem) {
	MAssociation assoc = elem.getAssociation();
	if (assoc != null
	    && assoc.getConnections() != null
	    && assoc.getConnections().size() == 2) { // binary association
	    UmlFactory.getFactory().delete(assoc);
	}
    }

    public void deleteAttribute(MAttribute elem) {
    }

    public void deleteBehavioralFeature(MBehavioralFeature elem) {
    }

    public void deleteBinding(MBinding elem) {
    }

    public void deleteClass(MClass elem) {
    }

    /**
     * <p>
     * Does a 'cascading delete' to all modelelements that are associated
     * with this element that would be in an illegal state after deletion
     * of the element. Does not do an cascading delete for elements that
     * are deleted by the NSUML method remove. This method should not be called
     * directly.
     * </p>
     * <p>
     * In the case of a classifier these are the following elements:
     * </p>
     * <p>
     * - AssociationEnds that have this classifier as type
     * </p>
     * @param elem
     * @see UmlFactory#delete(Object)
     */
    public void deleteClassifier(Object elem) {
	if (elem != null && elem instanceof MClassifier) {
	    Collection col = ((MClassifier) elem).getAssociationEnds();
	    Iterator it = col.iterator();
	    while (it.hasNext()) {
		UmlFactory.getFactory().delete(it.next());
	    }
	}
    }

    public void deleteComment(MComment elem) {
    }

    public void deleteComponent(MComponent elem) {
    }

    public void deleteConstraint(MConstraint elem) {
    }

    public void deleteDataType(MDataType elem) {
    }

    public void deleteDependency(MDependency elem) {
    }

    public void deleteElement(MElement elem) {
    }

    public void deleteElementResidence(MElementResidence elem) {
    }

    public void deleteFeature(MFeature elem) {
    }

    public void deleteFlow(MFlow elem) {
    }

    public void deleteGeneralizableElement(MGeneralizableElement elem) {
	Iterator it = elem.getGeneralizations().iterator();
	while (it.hasNext()) {
	    UmlFactory.getFactory().delete(it.next());
	}
	it = elem.getSpecializations().iterator();
	while (it.hasNext()) {
	    UmlFactory.getFactory().delete(it.next());
	}
    }

    public void deleteGeneralization(MGeneralization elem) {
    }

    public void deleteInterface(MInterface elem) {
    }

    public void deleteMethod(MMethod elem) {
    }

    /**
     * <p>
     * Does a 'cascading delete' to all modelelements that are associated
     * with this element that would be in an illegal state after deletion
     * of the element. Does not do an cascading delete for elements that
     * are deleted by the NSUML method remove. This method should not be called
     * directly.
     * </p>
     * <p>
     * In the case of a modelelement these are the following elements:
     * </p>
     * <p>
     * - Dependencies that have the modelelement as supplier or as a client
     * and are binary. (that is, they only have one supplier and one client)
     * </p>
     * @param elem
     * @see UmlFactory#delete(Object)
     */
    public void deleteModelElement(MModelElement elem) {
	Collection supplierDep = elem.getSupplierDependencies();
	Collection clientDep = elem.getClientDependencies();
	Set deps = new HashSet();
	deps.addAll(supplierDep);
	deps.addAll(clientDep);
	Iterator it = deps.iterator();
	while (it.hasNext()) {
	    MDependency dep = (MDependency) it.next();
	    Collection clients = dep.getClients();
	    Collection suppliers = dep.getSuppliers();
	    if ((clients.size() + suppliers.size()) == 2) {
		UmlFactory.getFactory().delete(dep);
	    }
	}
        it = elem.getComments().iterator();
        while (it.hasNext()) {
            MComment comment = (MComment) it.next();
            if (comment.getAnnotatedElements().size() == 1)
                UmlFactory.getFactory().delete(comment);
        }
    }

    /**
     * A namespace deletes its owned elements.
     *
     * @param elem is the namespace.
     */
    public void deleteNamespace(MNamespace elem) {

	List ownedElements = new ArrayList();
	ownedElements.addAll(elem.getOwnedElements());
	Iterator it = ownedElements.iterator();
	while (it.hasNext()) {
	    UmlFactory.getFactory().delete(it.next());
	}
    }

    public void deleteNode(MNode elem) {
    }

    public void deleteOperation(MOperation elem) {
    }

    public void deleteParameter(MParameter elem) {
    }

    public void deletePermission(MPermission elem) {
    }

    public void deletePresentationElement(MPresentationElement elem) {
    }

    public void deleteRelationship(MRelationship elem) {
    }

    public void deleteStructuralFeature(MStructuralFeature elem) {
    }

    public void deleteTemplateParameter(MTemplateParameter elem) {
    }

    public void deleteUsage(MUsage elem) {
    }

    /**
     * Copies a class, and it's features. This may also require other
     * classifiers to be copied.
     *
     * @param source is the class to copy.
     * @param ns is the namespace to put the copy in.
     * @return a newly created class.
     */
    public MClass copyClass(MClass source, MNamespace ns) {
	MClass c = createClass();
	ns.addOwnedElement(c);
	doCopyClass(source, c);
	return c;
    }

    /**
     * Copies a datatype, and it's features. This may also require other
     * classifiers to be copied.
     *
     * @param source is the datatype to copy.
     * @param ns is the namespace to put the copy in.
     * @return a newly created data type.
     */
    public MDataType copyDataType(MDataType source, MNamespace ns) {
	MDataType i = createDataType();
	ns.addOwnedElement(i);
	doCopyDataType(source, i);
	return i;
    }

    /**
     * Copies an interface, and it's features. This may also require other
     * classifiers to be copied.
     *
     * @param source is the interface to copy.
     * @param ns is the namespace to put the copy in.
     * @return a newly created interface.
     */
    public MInterface copyInterface(MInterface source, MNamespace ns) {
	MInterface i = createInterface();
	ns.addOwnedElement(i);
	doCopyInterface(source, i);
	return i;
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    private void doCopyElement(MElement source, MElement target) {
	// Nothing more to do.
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    public void doCopyClass(MClass source, MClass target) {
	doCopyClassifier(source, target);

	target.setActive(source.isActive());
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * TODO: actions? instances? collaborations etc?
     */
    public void doCopyClassifier(MClassifier source, MClassifier target) {
	// TODO: how to merge multiple inheritance? Necessary?
	doCopyNamespace(source, target);
	doCopyGeneralizableElement(source, target);

	// TODO: Features
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    public void doCopyDataType(MDataType source, MDataType target) {
	doCopyClassifier(source, target);
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * TODO: generalizations, specializations?
     */
    public void doCopyGeneralizableElement(MGeneralizableElement source,
					   MGeneralizableElement target) {
	doCopyModelElement(source, target);

	target.setAbstract(source.isAbstract());
	target.setLeaf(source.isLeaf());
	target.setRoot(source.isRoot());
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    public void doCopyInterface(MInterface source, MInterface target) {
	doCopyClassifier(source, target);
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * TODO: template parameters, default type
     * TODO: constraining elements
     * TODO: flows, dependencies, comments, bindings, contexts ???
     * TODO: contents, residences ???
     */
    public void doCopyModelElement(MModelElement source, MModelElement target) {
	// Set the name so that superclasses can find the newly
	// created element in the model, if necessary.
	target.setName(source.getName());
	doCopyElement(source, target);

	target.setSpecification(source.isSpecification());
	target.setVisibility(source.getVisibility());
	DataTypesHelper.getHelper().copyTaggedValues(source, target);

	if (source.getStereotype() != null) {
	    // Note that if we're copying this element then we
	    // must also be allowed to copy other necessary
	    // objects.
	    MStereotype st = (MStereotype)
		ModelManagementHelper.getHelper()
		    .getCorrespondingElement(source.getStereotype(),
					     target.getModel(),
					     true);
	    target.setStereotype(st);
	}
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    public void doCopyNamespace(MNamespace source, MNamespace target) {
	doCopyModelElement(source, target);
	// Nothing more to do, don't copy owned elements.
    }
}

