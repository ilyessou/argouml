// $Id$
// Copyright (c) 2007, The ArgoUML Project
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the ArgoUML Project nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE ArgoUML PROJECT ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE ArgoUML PROJECT BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.model.euml;

import java.util.List;

import org.argouml.model.CoreFactory;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.VisibilityKind;


/**
 * The implementation of the CoreFactory for EUML2.
 */
class CoreFactoryEUMLImpl implements CoreFactory {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;


    /**
     * Constructor.
     *
     * @param implementation The ModelImplementation.
     */
    public CoreFactoryEUMLImpl(EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public Abstraction buildAbstraction(String name, Object supplier, Object client) {
        Abstraction abstraction = createAbstraction();
        abstraction.setName(name);
        abstraction.getSuppliers().add((NamedElement) supplier);
        abstraction.getClients().add((NamedElement) client);
        return abstraction;
    }

    // TODO: A few different ways of building Associations.  Pick one that
    // works and is efficient - tfm
    public Object buildAssociation(Object fromClassifier,
            Object aggregationKind1, Object toClassifier,
            Object aggregationKind2, Boolean unidirectional) {
        
//        return ((Type) fromClassifier).createAssociation(
//                true, (AggregationKind) aggregationKind1, null, 1, 1, 
//                (Type) toClassifier, 
//                true, (AggregationKind) aggregationKind2, null, 1, 1); 

        Association assoc = (Association) createAssociation();
        Property end1 = assoc.createNavigableOwnedEnd(null, (Type) fromClassifier);
        Property end2 = assoc.createOwnedEnd(null, (Type) toClassifier);
        boolean uni = false;
        if (unidirectional != null && unidirectional.booleanValue()) {
            uni = true;
        }
        end2.setIsNavigable(uni);
        return assoc;
        
    }

    public Object buildAssociation(Object classifier1, Object classifier2) {
        return buildAssociation(
                classifier1, AggregationKind.NONE_LITERAL, classifier2,
                AggregationKind.NONE_LITERAL, false);
    }

    public Object buildAssociation(Object c1, boolean nav1, Object c2,
            boolean nav2, String name) {
        Association assoc = (Association) createAssociation();
        Property end1 = assoc.createOwnedEnd(null, (Type) c1);
        end1.setIsNavigable(nav1);
        Property end2 = assoc.createOwnedEnd(null, (Type) c2);
        end2.setIsNavigable(nav1);
        assoc.setName(name);
        return assoc;
    }

    public Object buildAssociationClass(Object end1, Object end2) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildAssociationEnd(Object assoc, String name, Object type, Object multi, Object stereo, boolean navigable, Object order, Object aggregation, Object scope, Object changeable, Object visibility) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildAssociationEnd(Object type, Object assoc) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Property buildAttribute() {
        Property attr = createAttribute();
        attr.setName("newAttr");
        attr.setLower(1);
        attr.setUpper(1);
        attr.setVisibility(VisibilityKind.PUBLIC_LITERAL);
        attr.setIsStatic(false);
        attr.setIsReadOnly(false);
        return attr;
    }

    public Property buildAttribute(Object model, Object type) {
        Property attr = buildAttribute();
        attr.setType((Type) type);
        return attr;
    }

    public Property buildAttribute(Object handle, Object model, Object type) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildAttribute2(Object type) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildAttribute2(Object handle, Object type) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildBinding(Object client, Object supplier, List arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildClass() {
        return createClass();
    }

    public Object buildClass(Object owner) {
        return buildClass(null, owner);
    }

    public Object buildClass(String name) {
        return buildClass(name, null);
    }

    public Object buildClass(String name, Object owner) {
        org.eclipse.uml2.uml.Class cls =
                (org.eclipse.uml2.uml.Class) createClass();
        if (owner != null) {
            modelImpl.getCoreHelper().addOwnedElement(owner, cls);
        }
        if (name != null) {
            cls.setName(name);
        }
        return cls;
    }

    public Object buildComment(Object element, Object model) {
        if (model == null) {
            throw new IllegalArgumentException("A namespace must be supplied.");
        }
        Element elementToAnnotate = (Element) element;
        // TODO: This actually creates the Comment as owned by
        // the Element itself, rather than the Element's namespace which
        // seems to make more sense, but is different than the specified
        // Model API semantics - tfm
        if (elementToAnnotate != null) {
            return elementToAnnotate.createOwnedComment();
        } else {
            return ((Namespace) model).createOwnedComment();
        }

    }

    public Object buildConstraint(Object constrElement) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildConstraint(String name, Object bexpr) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildDataType(String name, Object owner) {
        DataType dt = (DataType) createDataType();
        dt.setName(name);
        modelImpl.getCoreHelper().addOwnedElement(owner, dt);
        return dt;
    }

    public Object buildDependency(Object clientObj, Object supplierObj) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildElementResidence(Object me, Object component) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildEnumeration(String name, Object owner) {
        Enumeration enumer = (Enumeration) createEnumeration();
        enumer.setName(name);
        if (owner != null) {
            modelImpl.getCoreHelper().addOwnedElement(owner, enumer);
        }
        return enumer;
    }

    public Object buildEnumerationLiteral(String name, Object enumeration) {
        return ((Enumeration) enumeration).createOwnedLiteral(name);
    }

    public Generalization buildGeneralization(Object child, Object parent, String name) {
        // TODO: Generalizations are unnamed in UML 2.x
        return buildGeneralization(parent, child);
    }

    public Generalization buildGeneralization(Object child, Object parent) {
        Generalization generalization =
            ((BehavioredClassifier) child).createGeneralization((Classifier) parent);
        return generalization;
    }

    public Interface buildInterface() {
        return createInterface();
    }

    public Interface buildInterface(Object owner) {
        // TODO Auto-generated method stub
        return null;
    }

    public Interface buildInterface(String name) {
        Interface i = buildInterface();
        i.setName(name);
        return i;
    }

    public Interface buildInterface(String name, Object owner) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object buildMethod(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation buildOperation(Object classifier, Object model, Object returnType) {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation buildOperation(Object classifier, Object returnType) {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation buildOperation(Object cls, Object model, Object returnType, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation buildOperation2(Object cls, Object returnType, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Parameter buildParameter(Object o, Object model, Object type) {
        // TODO Auto-generated method stub
        return null;
    }

    public Parameter buildParameter(Object o, Object type) {
        // TODO Auto-generated method stub
        return null;
    }

    public PackageImport buildPermission(Object clientObj, Object supplierObj) {
        // TODO Auto-generated method stub
        return null;
    }

    public InterfaceRealization buildRealization(Object client,
            Object supplier, Object namespace) {
        // TODO: namespace is ignored
        InterfaceRealization realization =
                ((BehavioredClassifier) client).createInterfaceRealization(
                        null, (Interface) supplier);
        return realization;
    }

    public Object buildTemplateArgument(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public Usage buildUsage(Object client, Object supplier) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object copyClass(Object source, Object ns) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object copyDataType(Object source, Object ns) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object copyFeature(Object source, Object classifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object copyInterface(Object source, Object ns) {
        // TODO Auto-generated method stub
        return null;
    }

    public Abstraction createAbstraction() {
        return UMLFactory.eINSTANCE.createAbstraction();
    }

    public Artifact createArtifact() {
        return UMLFactory.eINSTANCE.createArtifact();
    }

    public Association createAssociation() {
        return UMLFactory.eINSTANCE.createAssociation();
    }

    public AssociationClass createAssociationClass() {
        return UMLFactory.eINSTANCE.createAssociationClass();
    }

    public Property createAssociationEnd() {
        // TODO: Double check - tfm
        return UMLFactory.eINSTANCE.createProperty();
    }

    public Property createAttribute() {
        // TODO: Double check - tfm
        return UMLFactory.eINSTANCE.createProperty();
    }

    public Object createBinding() {
        return UMLFactory.eINSTANCE.createTemplateBinding();
    }

    public org.eclipse.uml2.uml.Class createClass() {
        return UMLFactory.eINSTANCE.createClass();
    }

    public Comment createComment() {
        return UMLFactory.eINSTANCE.createComment();
    }

    public Object createComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    public Constraint createConstraint() {
        return UMLFactory.eINSTANCE.createConstraint();
    }

    public DataType createDataType() {
        return UMLFactory.eINSTANCE.createDataType();
    }

    public Dependency createDependency() {
        return UMLFactory.eINSTANCE.createDependency();
    }

    public Object createElementResidence() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration createEnumeration() {
        return UMLFactory.eINSTANCE.createEnumeration();
    }

    public EnumerationLiteral createEnumerationLiteral() {
        return UMLFactory.eINSTANCE.createEnumerationLiteral();
    }

    public Object createFlow() {
        // TODO Auto-generated method stub
        return null;
    }

    public Generalization createGeneralization() {
        return UMLFactory.eINSTANCE.createGeneralization();
    }

    public Interface createInterface() {
        return UMLFactory.eINSTANCE.createInterface();
    }

    public Object createMethod() {
        // TODO Auto-generated method stub
        return null;
    }

    public Node createNode() {
        return UMLFactory.eINSTANCE.createNode();
    }

    public Operation createOperation() {
        return UMLFactory.eINSTANCE.createOperation();
    }

    public Parameter createParameter() {
        return UMLFactory.eINSTANCE.createParameter();
    }

    public PackageImport createPermission() {
        return UMLFactory.eINSTANCE.createPackageImport();
    }

    public Object createPrimitive() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object createProgrammingLanguageDataType() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object createTemplateArgument() {
        // TODO Auto-generated method stub
        return null;
    }

    public TemplateParameter createTemplateParameter() {
        return UMLFactory.eINSTANCE.createTemplateParameter();
    }

    public Usage createUsage() {
        return UMLFactory.eINSTANCE.createUsage();
    }


}
