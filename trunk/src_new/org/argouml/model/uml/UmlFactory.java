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

package org.argouml.model.uml;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.argouml.model.ModelFacade;
import org.argouml.model.UmlModelEntity;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.activity_graphs.MActivityGraph;
import ru.novosoft.uml.behavior.activity_graphs.MCallState;
import ru.novosoft.uml.behavior.activity_graphs.MClassifierInState;
import ru.novosoft.uml.behavior.activity_graphs.MObjectFlowState;
import ru.novosoft.uml.behavior.activity_graphs.MPartition;
import ru.novosoft.uml.behavior.activity_graphs.MSubactivityState;
import ru.novosoft.uml.behavior.collaborations.MAssociationEndRole;
import ru.novosoft.uml.behavior.collaborations.MAssociationRole;
import ru.novosoft.uml.behavior.collaborations.MClassifierRole;
import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.collaborations.MInteraction;
import ru.novosoft.uml.behavior.collaborations.MMessage;
import ru.novosoft.uml.behavior.common_behavior.MAction;
import ru.novosoft.uml.behavior.common_behavior.MAttributeLink;
import ru.novosoft.uml.behavior.common_behavior.MCallAction;
import ru.novosoft.uml.behavior.common_behavior.MComponentInstance;
import ru.novosoft.uml.behavior.common_behavior.MCreateAction;
import ru.novosoft.uml.behavior.common_behavior.MDataValue;
import ru.novosoft.uml.behavior.common_behavior.MDestroyAction;
import ru.novosoft.uml.behavior.common_behavior.MException;
import ru.novosoft.uml.behavior.common_behavior.MInstance;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.behavior.common_behavior.MLinkEnd;
import ru.novosoft.uml.behavior.common_behavior.MLinkObject;
import ru.novosoft.uml.behavior.common_behavior.MNodeInstance;
import ru.novosoft.uml.behavior.common_behavior.MObject;
import ru.novosoft.uml.behavior.common_behavior.MReception;
import ru.novosoft.uml.behavior.common_behavior.MReturnAction;
import ru.novosoft.uml.behavior.common_behavior.MSendAction;
import ru.novosoft.uml.behavior.common_behavior.MSignal;
import ru.novosoft.uml.behavior.common_behavior.MTerminateAction;
import ru.novosoft.uml.behavior.common_behavior.MUninterpretedAction;
import ru.novosoft.uml.behavior.state_machines.MCompositeState;
import ru.novosoft.uml.behavior.state_machines.MFinalState;
import ru.novosoft.uml.behavior.state_machines.MGuard;
import ru.novosoft.uml.behavior.state_machines.MPseudostate;
import ru.novosoft.uml.behavior.state_machines.MSimpleState;
import ru.novosoft.uml.behavior.state_machines.MState;
import ru.novosoft.uml.behavior.state_machines.MStateMachine;
import ru.novosoft.uml.behavior.state_machines.MStateVertex;
import ru.novosoft.uml.behavior.state_machines.MStubState;
import ru.novosoft.uml.behavior.state_machines.MSubmachineState;
import ru.novosoft.uml.behavior.state_machines.MSynchState;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.behavior.use_cases.MActor;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MExtensionPoint;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.behavior.use_cases.MUseCaseInstance;
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
import ru.novosoft.uml.foundation.data_types.MActionExpression;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
import ru.novosoft.uml.model_management.MElementImport;
import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.model_management.MSubsystem;

/**
 * Root factory for UML model element instance creation.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @stereotype singleton
 */
public class UmlFactory extends AbstractUmlModelFactory {

    /**
     * The logger
     */
    private static final Logger LOG =
        Logger.getLogger(UmlFactory.class);
    
    /**
     * A map of valid connections keyed by the connection type.
     * The constructor builds this from the data in the VALID_CONNECTIONS array
     */
    private Map validConnectionMap = new HashMap();

    private boolean jmiProxyCreated = false;

    /**
     * An array of valid connections, the combination of connecting class
     * and node classes must exist as a row in this list to be considered
     * valid.
     * <ul>
     * <li>The 1st column is the connecting element.
     * <li>The 2nd column is the "from" element type.
     * <li>The 3rd column is the "to" element type.
     * <li>The 3rd column is optional, if not given then it is assumed 
     * to be the same as the "to" element.
     * <li>The existence of a 4th column indicates that the connection 
     * is valid in one direction only.
     * </ul>
     */
    private static final Object[][] VALID_CONNECTIONS = 
    {
	{ModelFacade.GENERALIZATION,   ModelFacade.CLASSIFIER_ROLE},
	{ModelFacade.GENERALIZATION,   ModelFacade.CLASS},
	{ModelFacade.GENERALIZATION,   ModelFacade.INTERFACE},
	{ModelFacade.GENERALIZATION,   ModelFacade.PACKAGE},
	{ModelFacade.GENERALIZATION,   ModelFacade.USE_CASE},
	{ModelFacade.GENERALIZATION,   ModelFacade.ACTOR},
	{ModelFacade.DEPENDENCY,       ModelFacade.PACKAGE},
	{ModelFacade.DEPENDENCY,       ModelFacade.CLASS},
	{ModelFacade.DEPENDENCY,       ModelFacade.INTERFACE},
	{ModelFacade.DEPENDENCY,       ModelFacade.INTERFACE,
	 ModelFacade.CLASS},
	{ModelFacade.DEPENDENCY,       ModelFacade.INTERFACE,
	 ModelFacade.PACKAGE},
	{ModelFacade.DEPENDENCY,       ModelFacade.CLASS,
	 ModelFacade.PACKAGE},
	{ModelFacade.DEPENDENCY,       ModelFacade.USE_CASE},
	{ModelFacade.DEPENDENCY,       ModelFacade.ACTOR},
	{ModelFacade.DEPENDENCY,       ModelFacade.ACTOR,
	 ModelFacade.USE_CASE},
	{ModelFacade.DEPENDENCY,       ModelFacade.COMPONENT},
	{ModelFacade.DEPENDENCY,       ModelFacade.COMPONENT_INSTANCE},
	{ModelFacade.DEPENDENCY,       ModelFacade.OBJECT},
	{ModelFacade.DEPENDENCY,       ModelFacade.COMPONENT,
	 ModelFacade.NODE,               null},
	{ModelFacade.DEPENDENCY,       ModelFacade.OBJECT,
	 ModelFacade.COMPONENT,          null},
	{ModelFacade.DEPENDENCY,       ModelFacade.COMPONENT_INSTANCE,
	 ModelFacade.NODE_INSTANCE,      null},
	{ModelFacade.DEPENDENCY,       ModelFacade.OBJECT,
	 ModelFacade.COMPONENT_INSTANCE, null},
	{ModelFacade.DEPENDENCY,       ModelFacade.CLASSIFIER_ROLE},
	{ModelFacade.USAGE,            ModelFacade.CLASS},
	{ModelFacade.USAGE,            ModelFacade.INTERFACE},
	{ModelFacade.USAGE,            ModelFacade.PACKAGE},
	{ModelFacade.USAGE,            ModelFacade.CLASS,
	 ModelFacade.PACKAGE},
	{ModelFacade.USAGE,            ModelFacade.CLASS,
	 ModelFacade.INTERFACE},
	{ModelFacade.USAGE,            ModelFacade.INTERFACE,
	 ModelFacade.PACKAGE},
	{ModelFacade.PERMISSION,       ModelFacade.CLASS},
	{ModelFacade.PERMISSION,       ModelFacade.INTERFACE},
	{ModelFacade.PERMISSION,       ModelFacade.PACKAGE},
	{ModelFacade.PERMISSION,       ModelFacade.CLASS,
	 ModelFacade.PACKAGE},
	{ModelFacade.PERMISSION,       ModelFacade.CLASS,
	 ModelFacade.INTERFACE},
	{ModelFacade.PERMISSION,       ModelFacade.INTERFACE,
	 ModelFacade.PACKAGE},
	{ModelFacade.ABSTRACTION,      ModelFacade.CLASS,
	 ModelFacade.INTERFACE,          null},
	{ModelFacade.ASSOCIATION,      ModelFacade.CLASS},
	{ModelFacade.ASSOCIATION,      ModelFacade.CLASS,
	 ModelFacade.INTERFACE},
	{ModelFacade.ASSOCIATION,      ModelFacade.ACTOR},
	{ModelFacade.ASSOCIATION,      ModelFacade.USE_CASE},
	{ModelFacade.ASSOCIATION,      ModelFacade.ACTOR,
	 ModelFacade.USE_CASE},
	{ModelFacade.ASSOCIATION,      ModelFacade.NODE},
	{ModelFacade.ASSOCIATION_ROLE, ModelFacade.CLASSIFIER_ROLE},
	{ModelFacade.EXTEND,           ModelFacade.USE_CASE},
	{ModelFacade.INCLUDE,          ModelFacade.USE_CASE},
	{ModelFacade.LINK,             ModelFacade.NODE_INSTANCE},
	{ModelFacade.LINK,             ModelFacade.OBJECT},
	{CommentEdge.class,            ModelFacade.MODELELEMENT,
	 ModelFacade.COMMENT},
	{CommentEdge.class,            ModelFacade.COMMENT,
         ModelFacade.MODELELEMENT},
        {ModelFacade.TRANSITION,       ModelFacade.STATEVERTEX}
    };

    /** Singleton instance.
     */
    private static final UmlFactory SINGLETON = new UmlFactory();

    private static Hashtable elements;

    /** Singleton instance access method.
     * @return the singleton.
     */
    public static UmlFactory getFactory() {
        return SINGLETON;
    }

    /** Don't allow external instantiation.
     */
    private UmlFactory() {
        buildValidConnectionMap();
        initializeFactoryMethods();
    }

    private void buildValidConnectionMap() {
        // A list of valid connections between elements, the
        // connection type first and then the elements to be connected
        
        Object connection = null;
        for (int i = 0; i < VALID_CONNECTIONS.length; ++i) {
            connection = VALID_CONNECTIONS[i][0];
            ArrayList validItems =
                (ArrayList) validConnectionMap.get(connection);
            if (validItems == null) {
                validItems = new ArrayList();
                validConnectionMap.put(connection, validItems);
            }
            if (VALID_CONNECTIONS[i].length < 3) {
                // If there isn't a 3rd column then this represents a connection
                // of elements of the same type.
                Object[] modeElementPair = new Class[2];
                modeElementPair[0] = VALID_CONNECTIONS[i][1];
                modeElementPair[1] = VALID_CONNECTIONS[i][1];
                validItems.add(modeElementPair);
            } else {
                // If there is a 3rd column then this represents a connection
                // of between 2 different types of element.
                Object[] modeElementPair = new Class[2];
                modeElementPair[0] = VALID_CONNECTIONS[i][1];
                modeElementPair[1] = VALID_CONNECTIONS[i][2];
                validItems.add(modeElementPair);
                // If the array hasn't been flagged to indicate otherwise
                // swap elements the elemnts and add again.
                if (VALID_CONNECTIONS[i].length < 4) {
                    Object[] reversedModeElementPair = new Class[2];
                    reversedModeElementPair[0] = VALID_CONNECTIONS[i][2];
                    reversedModeElementPair[1] = VALID_CONNECTIONS[i][1];
                    validItems.add(reversedModeElementPair);
                }
            }
        }
    }

    /**
     * Initialization for the "create(UmlModelEntity)" function.
     */
    private void initializeFactoryMethods() {
        MFactory factory = MFactory.getDefaultFactory();
        elements = new Hashtable(80);
        
        elements.put(Uml.ABSTRACTION,
            new ObjectCreateInfo(MAbstraction.class,
                factory,
                "createAbstraction"));
        elements.put(Uml.ASSOCIATION,
            new ObjectCreateInfo(MAssociation.class,
                factory,
                "createAssociation"));
        elements.put(Uml.ASSOCIATION_ROLE,
            new ObjectCreateInfo(MAssociationRole.class,
                factory,
                "createAssociationRole"));
        elements.put(Uml.DEPENDENCY,
            new ObjectCreateInfo(MDependency.class,
                factory,
                "createDependency"));
        elements.put(Uml.EXTEND,
            new ObjectCreateInfo(MExtend.class, factory, "createExtend"));
        elements.put(Uml.GENERALIZATION,
            new ObjectCreateInfo(MGeneralization.class,
                factory,
                "createGeneralization"));
        elements.put(Uml.INCLUDE,
            new ObjectCreateInfo(MInclude.class, factory, "createInclude"));
        elements.put(Uml.LINK,
            new ObjectCreateInfo(MLink.class, factory, "createLink"));
        elements.put(Uml.LINK_END,
            new ObjectCreateInfo(MLinkEnd.class, factory, "createLinkEnd"));
        elements.put(Uml.PERMISSION,
            new ObjectCreateInfo(MPermission.class,
                factory,
                "createPermission"));
        elements.put(Uml.USAGE,
            new ObjectCreateInfo(MUsage.class, factory, "createUsage"));
        elements.put(Uml.TRANSITION,
            new ObjectCreateInfo(MTransition.class,
                factory,
                "createTransition"));
        elements.put(Uml.ACTOR,
            new ObjectCreateInfo(MActor.class, factory, "createActor"));
        elements.put(Uml.CLASS,
            new ObjectCreateInfo(MClass.class, factory, "createClass"));
        elements.put(Uml.EXCEPTION,
            new ObjectCreateInfo(MException.class,
                factory,
                "createException"));
        elements.put(Uml.CLASSIFIER,
            new ObjectCreateInfo(MClassifier.class,
                factory,
                "createClassifier"));
        elements.put(Uml.CLASSIFIER_ROLE,
            new ObjectCreateInfo(MClassifierRole.class,
                factory,
                "createClassifierRole"));
        elements.put(Uml.COMPONENT,
            new ObjectCreateInfo(MComponent.class, factory, "createComponent"));
        elements.put(Uml.COMPONENT_INSTANCE,
            new ObjectCreateInfo(MComponentInstance.class,
                CommonBehaviorFactory.getFactory(),
                "createComponentInstance"));
        elements.put(Uml.INSTANCE,
            new ObjectCreateInfo(MInstance.class, factory, "createInstance"));
        elements.put(Uml.INTERFACE,
            new ObjectCreateInfo(MInterface.class, factory, "createInterface"));
        elements.put(Uml.NODE,
            new ObjectCreateInfo(MNode.class, factory, "createNode"));
        elements.put(Uml.NODE_INSTANCE,
            new ObjectCreateInfo(MNodeInstance.class,
                factory,
                "createNodeInstance"));
        elements.put(Uml.OBJECT,
            new ObjectCreateInfo(MObject.class, factory, "createObject"));
        elements.put(Uml.PACKAGE,
            new ObjectCreateInfo(MPackage.class, factory, "createPackage"));
	elements.put(Uml.PARTITION,
	    new ObjectCreateInfo(MPartition.class, factory, "createPartition"));
        elements.put(Uml.STATE,
            new ObjectCreateInfo(MState.class, factory, "createState"));
        elements.put(Uml.CALL_STATE,
            new ObjectCreateInfo(MCallState.class, 
                factory, 
                "createCallState"));
        elements.put(Uml.COMPOSITE_STATE,
            new ObjectCreateInfo(MCompositeState.class,
                factory,
                "createCompositeState"));
        elements.put(Uml.PSEUDOSTATE,
            new ObjectCreateInfo(MPseudostate.class,
                factory,
                "createPseudostate"));
        elements.put(Uml.OBJECT_FLOW_STATE,
            new ObjectCreateInfo(MObjectFlowState.class,
                factory,
                "createObjectFlowState"));
        elements.put(Uml.CLASSIFIER_IN_STATE,
            new ObjectCreateInfo(MClassifierInState.class,
                factory,
                "createClassifierInState"));
        elements.put(Uml.SUBACTIVITY_STATE,
            new ObjectCreateInfo(MSubactivityState.class,
                factory,
                "createSubactivityState"));
        elements.put(Uml.USE_CASE,
            new ObjectCreateInfo(MUseCase.class, factory, "createUseCase"));
        elements.put(Uml.ACTION,
            new ObjectCreateInfo(MAction.class, factory, "createAction"));
        elements.put(Uml.ASSOCIATION_END,
            new ObjectCreateInfo(MAssociationEnd.class,
                factory,
                "createAssociationEnd"));
        elements.put(Uml.CALL_ACTION,
            new ObjectCreateInfo(MCallAction.class,
                factory,
                "createCallAction"));
        elements.put(Uml.NAMESPACE,
            new ObjectCreateInfo(MNamespace.class, factory, "createNamespace"));
        elements.put(Uml.RECEPTION,
            new ObjectCreateInfo(MReception.class, factory, "createReception"));
        elements.put(Uml.STEREOTYPE,
            new ObjectCreateInfo(MStereotype.class,
                factory,
                "createStereotype"));
        elements.put(Uml.ATTRIBUTE,
            new ObjectCreateInfo(MAttribute.class, factory, "createAttribute"));
        elements.put(Uml.OPERATION,
            new ObjectCreateInfo(MOperation.class, factory, "createOperation"));
        elements.put(Uml.MODEL,
            new ObjectCreateInfo(MModel.class, factory, "createModel"));
        elements.put(Uml.DATATYPE,
            new ObjectCreateInfo(MDataType.class, factory, "createDataType"));

        // NSUML does not have a factory method for this
        elements.put(Uml.ACTION_EXPRESSION,
            new ObjectCreateInfo(MActionExpression.class,
                this,
                "createActionExpression"));

        // NSUML cannot instantiate an Event object
        // elements.put(Uml.EVENT,
        //     new NsumlObjectInfo(factory,MEvent.class, "createEvent"));

        // NSUML cannot instantiate a State Vertex object
        // elements.put(Uml.STATE_VERTEX,
        //     new NsumlObjectInfo(factory, MStateVertex.class,
        //         "createStateVertex"));
    }

    /**
     * @return boolean to indicate if the JMI Reflective Proxy
     * over NSUML is created.
     */
    public boolean isJmiProxyCreated() {
        return jmiProxyCreated;
    }

    /**
     * @param arg true to cause the JMI Reflective proxy over NSUML to be used.
     */
    public void setJmiProxyCreated(boolean arg) {
        jmiProxyCreated = arg;
    }
    
    /**
     * Create a new connection model element (a relationship or link)
     * between any existing node model elements.
     * 
     * @return the newly created connection element
     * @param connectionType is the type of relationship
     * @param fromElement is an existing model element
     * @param toElement is another existing model element
     * @throws IllegalModelElementConnectionException if the connection is bad
     */
    public Object buildConnection(Object connectionType, 
				  Object fromElement, Object toElement)
        throws IllegalModelElementConnectionException {

        return buildConnection(connectionType,
			       fromElement, null, toElement, null, null);

    }
    
    /**
     * @param connectionType the UML object type of the connection
     * @param fromElement    the UML object for the "from" element
     * @param fromStyle      the aggregationkind for the connection 
     *                       in case of an association
     * @param toElement      the UML object for the "to" element
     * @param toStyle        the aggregationkind for the connection 
     *                       in case of an association
     * @param unidirectional for association and associationrole
     * @return               the newly build connection (UML object)
     * @throws IllegalModelElementConnectionException if the connection is not 
     *                                                a valid thing to do
     */
    public Object buildConnection(Object connectionType,
                  Object fromElement, Object fromStyle,
                  Object toElement, Object toStyle,
                  Object unidirectional)
        throws IllegalModelElementConnectionException
    {

        if (!isConnectionValid(connectionType, fromElement, toElement)) {
            throw new IllegalModelElementConnectionException(
                "Cannot make a "
            + connectionType.getClass().getName() 
            + " between a " + fromElement.getClass().getName() 
            + " and a " + toElement.getClass().getName());
        }
        
        Object connection = null;
        
        if (connectionType == ModelFacade.ASSOCIATION) {
            connection = getCore().buildAssociation((MClassifier) fromElement, 
                       (MAggregationKind) fromStyle, 
                       (MClassifier) toElement, 
                       (MAggregationKind) toStyle, 
                       (Boolean) unidirectional);
        } else if (connectionType == ModelFacade.ASSOCIATION_ROLE) {
            connection = getCollaborations()
                .buildAssociationRole((MClassifierRole) fromElement, 
                      (MAggregationKind) fromStyle, 
                      (MClassifierRole) toElement,
                      (MAggregationKind) toStyle, 
                      (Boolean) unidirectional);
        } else if (connectionType == ModelFacade.GENERALIZATION) {
            connection = getCore()
                .buildGeneralization(fromElement, toElement);
        } else if (connectionType == ModelFacade.PERMISSION) {
            connection = getCore().buildPermission(fromElement, toElement);
        } else if (connectionType == ModelFacade.USAGE) {
            connection = getCore().buildUsage((MModelElement) fromElement,
                     (MModelElement) toElement);
        } else if (connectionType == ModelFacade.GENERALIZATION) {
            connection = getCore()
                .buildGeneralization(fromElement, toElement);
        } else if (connectionType == ModelFacade.DEPENDENCY) {
            connection = getCore().buildDependency(fromElement, toElement);
        } else if (connectionType == ModelFacade.ABSTRACTION) {
            connection = getCore().buildRealization((MModelElement) fromElement,
                (MModelElement) toElement);
        } else if (connectionType == ModelFacade.LINK) {
            connection = getCommonBehavior().buildLink(fromElement, toElement);
        } else if (connectionType == ModelFacade.EXTEND) {
            // Extend, but only between two use cases. Remember we draw from the
            // extension port to the base port.
            connection = getUseCases().buildExtend(toElement, fromElement);
        } else if (connectionType == ModelFacade.INCLUDE) {
            connection = getUseCases().buildInclude(fromElement, toElement);
        } else if (connectionType == CommentEdge.class) {
            connection = 
                getCore().buildCommentConnection(fromElement, toElement);
        }
    
        if (connection == null) {
            throw new IllegalModelElementConnectionException(
            "Cannot make a " 
            + connectionType.getClass().getName() 
            + " between a " + fromElement.getClass().getName() 
            + " and a " + toElement.getClass().getName());
        }
        
        return connection;
    }
    
    /**
     * Checks if some type of connection is valid between two elements
     * @param connectionType  the UML object type of the connection
     * @param fromElement     the UML object type of the "from" 
     * @param toElement       the UML object type of the "to"
     * @return true if valid
     */
    public boolean isConnectionValid(Object connectionType,
                     Object fromElement, Object toElement)
    {
        // Get the list of valid model item pairs for the given connection type
        ArrayList validItems =
            (ArrayList) validConnectionMap.get(connectionType);
        if (validItems == null) {
            return false;
        }
        // See if there's a pair in this list that match the given
        // model elements
        Iterator it = validItems.iterator();
        while (it.hasNext()) {
            Class[] modeElementPair = (Class[]) it.next();
            if (modeElementPair[0].isInstance(fromElement)
                && modeElementPair[1].isInstance(toElement)) 
            {
                return true;
            }
        }
        return false;
    }
    
    /** Returns the package factory for the UML
     *  package Foundation::ExtensionMechanisms.
     *
     *  @return the ExtensionMechanisms factory instance.
     */
    public ExtensionMechanismsFactory getExtensionMechanisms() {
        return ExtensionMechanismsFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package Foundation::DataTypes.
     *
     *  @return the DataTypes factory instance.
     */
    public DataTypesFactory getDataTypes() {
        return DataTypesFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package Foundation::Core.
     *
     *  @return the Core factory instance.
     */
    public CoreFactory getCore() {
        return CoreFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package BehavioralElements::CommonBehavior.
     *
     *  @return the CommonBehavior factory instance.
     */
    public CommonBehaviorFactory getCommonBehavior() {
        return CommonBehaviorFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package BehavioralElements::UseCases.
     *
     *  @return the UseCases factory instance.
     */
    public UseCasesFactory getUseCases() {
        return UseCasesFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package BehavioralElements::StateMachines.
     *
     *  @return the StateMachines factory instance.
     */
    public StateMachinesFactory getStateMachines() {
        return StateMachinesFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package BehavioralElements::Collaborations.
     *
     *  @return the Collaborations factory instance.
     */
    public CollaborationsFactory getCollaborations() {
        return CollaborationsFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package BehavioralElements::ActivityGraphs.
     *
     *  @return the ActivityGraphs factory instance.
     */
    public ActivityGraphsFactory getActivityGraphs() {
        return ActivityGraphsFactory.getFactory();
    }

    /** Returns the package factory for the UML
     *  package ModelManagement.
     *
     *  @return the ModelManagement factory instance.
     */
    public ModelManagementFactory getModelManagement() {
        return ModelManagementFactory.getFactory();
    }

    /**
     * Deletes a modelelement. It calls the remove method of the
     * modelelement but also does 'cascading deletes' that are not
     * provided for in the remove method of the modelelement
     * itself. For example: this delete method also removes the binary
     * associations that a class has if the class is deleted. In this
     * way, it is not longer possible that illegal states exist in the
     * model.<p>
     *
     * The actual deletion is delegated to delete methods in the rest of the
     * factories. For example: a method deleteClass exists on CoreHelper.
     * Delete methods as deleteClass should only do those extra actions that are
     * necessary for the deletion of the modelelement itself. I.e. deleteClass
     * should only take care of things specific to MClass.<p>
     *
     * The delete methods in the UML Factories should not be called directly
     * throughout the code! Calls should allways refer to this method and never
     * call the deleteXXX method on XXXFactory directly. The reason that it is
     * possible to call the deleteXXX methods directly is a pure implementation
     * detail.<p>
     *
     * The implementation of this method uses a quite complicate if then else
     * tree. This is done to provide optimal performance and full compliance to
     * the UML 1.3 model. The last remark refers to the fact that the UML 1.3
     * model knows multiple inheritance in several places. This has to be taken
     * into account.<p>
     * 
     * Extensions and its children are not taken into account
     * here. They do not require extra cleanup actions. Not in the
     * form of a call to the remove method as is normal for all
     * children of MBase and not in the form of other behaviour we
     * want to implement via this operation.
     * @param elem The element to be deleted
     */
    public void delete(Object elem) {
        if (elem == null)
            throw new IllegalArgumentException("Element may not be null "
                + "in delete");        
        if (elem instanceof MElement) {
            getCore().deleteElement((MElement) elem);
            if (elem instanceof MModelElement) {
                getCore().deleteModelElement((MModelElement) elem);
                if (elem instanceof MFeature) {
                    deleteFeature((MFeature) elem);
                } else if (elem instanceof MNamespace) {
                    deleteNamespace((MNamespace) elem);
                } 
        // no else here to make sure MClassifier with
        // its double inheritance goes ok
                // no else here too to make sure MAssociationClass goes ok
        
                if (elem instanceof MGeneralizableElement) {
                    MGeneralizableElement ge = (MGeneralizableElement) elem;
                    getCore().deleteGeneralizableElement(ge);
                    if (elem instanceof MStereotype) {
                        MStereotype s = (MStereotype) elem;
                        getExtensionMechanisms().deleteStereotype(s);
                    }
                } // no else here to make sure MAssociationClass goes ok
        
                if (elem instanceof MParameter) {
                    getCore().deleteParameter((MParameter) elem);
                } else if (elem instanceof MConstraint) {
                    getCore().deleteConstraint((MConstraint) elem);
                } else if (elem instanceof MRelationship) {
                    deleteRelationship((MRelationship) elem);
                } else if (elem instanceof MAssociationEnd) {
                    getCore().deleteAssociationEnd((MAssociationEnd) elem);
                    if (elem instanceof MAssociationEndRole) {
                        getCollaborations()
                            .deleteAssociationEndRole(
                                 (MAssociationEndRole) elem);
                    }
                } else if (elem instanceof MComment) {
                    getCore().deleteComment((MComment) elem);
                } else if (ModelFacade.isAAction(elem)) {
                    deleteAction(elem);
                } else if (elem instanceof MAttributeLink) {
                    getCommonBehavior()
                        .deleteAttributeLink((MAttributeLink) elem);
                } else if (elem instanceof MInstance) {
                    deleteInstance((MInstance) elem);
                } // no else to handle multiple inheritance of linkobject

                if (elem instanceof MLink) {
                    getCommonBehavior().deleteLink((MLink) elem);
                } else if (elem instanceof MLinkEnd) {
                    getCommonBehavior().deleteLinkEnd((MLinkEnd) elem);
                } else if (elem instanceof MInteraction) {
                    getCollaborations().deleteInteraction((MInteraction) elem);
                } else if (elem instanceof MMessage) {
                    getCollaborations().deleteMessage((MMessage) elem);
                } else if (elem instanceof MExtensionPoint) {
                    getUseCases().deleteExtensionPoint((MExtensionPoint) elem);
                } else if (elem instanceof MStateVertex) {
                    deleteStateVertex((MStateVertex) elem);
                }

                if (elem instanceof MStateMachine) {
                    getStateMachines().deleteStateMachine((MStateMachine) elem);
                    if (elem instanceof MActivityGraph) {
                        getActivityGraphs()
                            .deleteActivityGraph((MActivityGraph) elem);
                    }
                } else if (elem instanceof MTransition) {
                    getStateMachines().deleteTransition((MTransition) elem);
                } else if (elem instanceof MGuard) {
                    getStateMachines().deleteGuard((MGuard) elem);
                }
        // else if (elem instanceof MEvent) {
        //
        //}
            } else if (elem instanceof MPresentationElement) {
                getCore().deletePresentationElement(
                    (MPresentationElement) elem);
            }
        } else if (elem instanceof MTemplateParameter) {
            getCore().deleteTemplateParameter((MTemplateParameter) elem);
        } else if (elem instanceof MTaggedValue) {
            getExtensionMechanisms().deleteTaggedValue((MTaggedValue) elem);
        }

        if (elem instanceof MPartition) {
            getActivityGraphs().deletePartition((MPartition) elem);
        } else if (elem instanceof MElementImport) {
            getModelManagement().deleteElementImport((MElementImport) elem);
        }

        if (elem instanceof MBase) {
            ((MBase) elem).remove();
            UmlModelEventPump.getPump().cleanUp((MBase) elem);
        }        
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteFeature(MFeature elem) {
        getCore().deleteFeature(elem);
        if (elem instanceof MBehavioralFeature) {
            getCore().deleteBehavioralFeature((MBehavioralFeature) elem);
            if (elem instanceof MOperation) {
                getCore().deleteOperation((MOperation) elem);
            } else if (elem instanceof MMethod) {
                getCore().deleteMethod((MMethod) elem);
            } else if (elem instanceof MReception) {
                getCommonBehavior().deleteReception((MReception) elem);
            }
        } else if (elem instanceof MStructuralFeature) {
            getCore().deleteStructuralFeature((MStructuralFeature) elem);
            if (elem instanceof MAttribute) {
                getCore().deleteAttribute((MAttribute) elem);
            }
        }
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteNamespace(MNamespace elem) {
        getCore().deleteNamespace(elem);
        if (elem instanceof MClassifier) {
            getCore().deleteClassifier(elem);
            if (elem instanceof MClass) {
                getCore().deleteClass((MClass) elem);
                if (elem instanceof MAssociationClass) {
                    getCore().deleteAssociationClass((MAssociationClass) elem);
                }
            } else if (elem instanceof MInterface) {
                getCore().deleteInterface((MInterface) elem);
            } else if (elem instanceof MDataType) {
                getCore().deleteDataType((MDataType) elem);
            } else if (elem instanceof MNode) {
                getCore().deleteNode((MNode) elem);
            } else if (elem instanceof MComponent) {
                getCore().deleteComponent((MComponent) elem);
            } else if (elem instanceof MSignal) {
                getCommonBehavior().deleteSignal((MSignal) elem);
                if (elem instanceof MException) {
                    getCommonBehavior().deleteException((MException) elem);
                }
            } else if (elem instanceof MClassifierRole) {
                getCollaborations().deleteClassifierRole(
                    (MClassifierRole) elem);
            } else if (elem instanceof MUseCase) {
                getUseCases().deleteUseCase((MUseCase) elem);
            } else if (elem instanceof MActor) {
                getUseCases().deleteActor((MActor) elem);
            } else if (elem instanceof MClassifierInState) {
                getActivityGraphs().deleteClassifierInState(
                    (MClassifierInState) elem);
            }
        } else if (elem instanceof MCollaboration) {
            getCollaborations().deleteCollaboration((MCollaboration) elem);
        } else if (elem instanceof MPackage) {
            getModelManagement().deletePackage((MPackage) elem);
            if (elem instanceof MModel) {
                getModelManagement().deleteModel((MModel) elem);
            } else if (elem instanceof MSubsystem) {
                getModelManagement().deleteSubsystem((MSubsystem) elem);
            }
        }
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteRelationship(MRelationship elem) {
        getCore().deleteRelationship(elem);
        if (elem instanceof MFlow) {
            getCore().deleteFlow((MFlow) elem);
        } else if (elem instanceof MGeneralization) {
            getCore().deleteGeneralization((MGeneralization) elem);
        } else if (elem instanceof MAssociation) {
            getCore().deleteAssociation((MAssociation) elem);
            if (elem instanceof MAssociationRole) {
                getCollaborations().deleteAssociationRole(
                    (MAssociationRole) elem);
            }
        } else if (elem instanceof MDependency) {
            getCore().deleteDependency((MDependency) elem);
            if (ModelFacade.isAAbstraction(elem)) {
                getCore().deleteAbstraction(elem);
            } else if (elem instanceof MBinding) {
                getCore().deleteBinding((MBinding) elem);
            } else if (elem instanceof MUsage) {
                getCore().deleteUsage((MUsage) elem);
            } else if (elem instanceof MPermission) {
                getCore().deletePermission((MPermission) elem);
            }
        } else if (elem instanceof MInclude) {
            getUseCases().deleteInclude((MInclude) elem);
        } else if (elem instanceof MExtend) {
            getUseCases().deleteExtend((MExtend) elem);
        }
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteAction(Object elem) {
        getCommonBehavior().deleteAction(elem);
        if (ModelFacade.isAActionSequence(elem)) {
            getCommonBehavior().deleteActionSequence(elem);
        } else if (elem instanceof MCreateAction) {
            getCommonBehavior().deleteCreateAction((MCreateAction) elem);
        } else if (elem instanceof MCallAction) {
            getCommonBehavior().deleteCallAction((MCallAction) elem);
        } else if (elem instanceof MReturnAction) {
            getCommonBehavior().deleteReturnAction((MReturnAction) elem);
        } else if (elem instanceof MSendAction) {
            getCommonBehavior().deleteSendAction((MSendAction) elem);
        } else if (elem instanceof MTerminateAction) {
            getCommonBehavior().deleteTerminateAction((MTerminateAction) elem);
        } else if (elem instanceof MUninterpretedAction) {
            getCommonBehavior().deleteUninterpretedAction(
                (MUninterpretedAction) elem);
        } else if (elem instanceof MDestroyAction) {
            getCommonBehavior().deleteDestroyAction((MDestroyAction) elem);
        }
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteInstance(MInstance elem) {
        getCommonBehavior().deleteInstance(elem);
        if (elem instanceof MDataValue) {
            getCommonBehavior().deleteDataValue((MDataValue) elem);
        } else if (elem instanceof MComponentInstance) {
            getCommonBehavior().deleteComponentInstance(
                (MComponentInstance) elem);
        } else if (elem instanceof MNodeInstance) {
            getCommonBehavior().deleteNodeInstance((MNodeInstance) elem);
        } else if (elem instanceof MObject) {
            getCommonBehavior().deleteObject((MObject) elem);
            if (elem instanceof MLinkObject) {
                getCommonBehavior().deleteLinkObject((MLinkObject) elem);
            }
        }
        if (elem instanceof MUseCaseInstance) {
            getUseCases().deleteUseCaseInstance((MUseCaseInstance) elem);
        }
    }

    /**
     * Factored this method out of delete to simplify the design of the delete
     * operation
     * @param elem
     */
    private void deleteStateVertex(MStateVertex elem) {
        getStateMachines().deleteStateVertex(elem);
        if (elem instanceof MPseudostate) {
            getStateMachines().deletePseudostate((MPseudostate) elem);
        } else if (elem instanceof MSynchState) {
            getStateMachines().deleteSynchState((MSynchState) elem);
        } else if (elem instanceof MStubState) {
            getStateMachines().deleteStubState((MStubState) elem);
        } else if (elem instanceof MState) {
            getStateMachines().deleteState((MState) elem);
            if (elem instanceof MCompositeState) {
                getStateMachines().deleteCompositeState((MCompositeState) elem);
                if (elem instanceof MSubmachineState) {
                    getStateMachines().deleteSubmachineState(
                        (MSubmachineState) elem);
                    if (elem instanceof MSubactivityState) {
                        getActivityGraphs().deleteSubactivityState(
                            (MSubactivityState) elem);
                    }
                }
            } else if (elem instanceof MSimpleState) {
                getStateMachines().deleteSimpleState((MSimpleState) elem);
                if (ModelFacade.isAActionState(elem)) {
                    getActivityGraphs().deleteActionState(elem);
                    if (elem instanceof MCallState) {
                        getActivityGraphs().deleteCallState((MCallState) elem);
                    }
                } else if (elem instanceof MObjectFlowState) {
                    getActivityGraphs().deleteObjectFlowState(
                        (MObjectFlowState) elem);
                }
            } else if (elem instanceof MFinalState) {
                getStateMachines().deleteFinalState((MFinalState) elem);
            }
        }
    }

    class ObjectCreateInfo {
    
        private Object factory;

        private String createMethod;

        private Class javaClass;

//        ObjectCreateInfo (Class javaClass, Object fact, String meth) {
//            this(javaClass, fact, meth, fact, meth);
//        }
        
        ObjectCreateInfo (Class cls, Object fact, String meth) {
            javaClass = cls;
            factory = fact;
            createMethod = meth;
        }
        /**
         * @return
         */
        public Class getJavaClass() {
            return javaClass;
        }

        /**
         * @return
         */
        public String getCreateMethod() {
            return createMethod;
        }

        /**
         * @return
         */
        public Object getFactory() {
            return factory;
        }

    }

    /** Create an empty but initialized instance of a UML ActionExpression.
     *  NSUML does not have a factory method for this.
     * 
     *  @return an initialized UML ActionExpression instance.
     */
    public MActionExpression createActionExpression() {
        MActionExpression expression = new MActionExpression("", "");
        DataTypesFactory.getFactory().initialize(expression);
        return expression;
    }

    /** Create a UML object from the implementation name.
     * 
     * This will allow abstraction of the create mechanism at a single point.
     * 
     * @param entity name to create - must be implemented in 
     * {@link org.argouml.model.uml.Uml}.
     * 
     * @return the entity requested or null if unable to create
     */
    public Object create(String entity) {
        throw new RuntimeException("Not yet implemented");
        // return create((Class) Uml.getUmlClassList().get(entity));
    }

    /** Create a UML object from the implementation.
     * 
     * This will allow abstraction of the create mechanism at a single point.
     * 
     * @param entity Class to create - must implement
     *        {@link UmlModelEntity}
     * @return the created entity or null if unable to create
     */
    public Object create(UmlModelEntity entity) {
        ObjectCreateInfo oi = (ObjectCreateInfo) elements.get(entity);
        if (oi == null) {
            return null;
            // TODO: decide if we want to throw an exception instead
            // throw new InvalidObjectRequestException
            //("Cannot identify the object type", entity);
        }
        Method method = null;
        try {
            method = oi.getFactory().getClass().getMethod(oi.getCreateMethod(),
                                                          new Class[] {} );
        }
        catch (Exception e) {
            LOG.error("Failed to invoke create method on factory.", e);
            return null;
            // TODO: decide if we want to throw an exception instead
            // throw new InvalidObjectRequestException
            //("Cannot find creator method", entity, e);
        }

        Object obj = null;
        try {
            obj = method.invoke(oi.getFactory(), new Object[] {} );
        }
        catch (Exception e) {
            // TODO: decide if we want to throw an exception instead
            // throw new InvalidObjectRequestException
            //("Cannot execute creator method", entity, e);
            LOG.error("Failed to invoke create method on factory.", e);
            return null;
        }
        UmlFactory.getFactory().initialize(obj);
        
        // Allow for testing of the proxy capability
        if (jmiProxyCreated) {
        	// TODO: implement RefPackageProxy handling
        	
			// if (obj instanceof MPackage) {
			//     return RefPackageProxy.newInstance(obj);
			// }
			return RefBaseObjectProxy.newInstance(obj);
        }
        return obj;
    }

}

