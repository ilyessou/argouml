/* $Id$
 *******************************************************************************
 * Copyright (c) 2009-2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *    Michiel van der Wulp
 *******************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2006 The Regents of the University of California. All
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

package org.argouml.model;

import java.util.Collection;
import java.util.List;

/**
 * The interface for the helper for StateMachines.<p>
 *
 * Created from the old StateMachinesHelper.
 */
public interface StateMachinesHelper {
    /**
     * Returns the source of the given transition. This operation is here to
     * give a full implementation of all getSource and getDestination methods
     * on the uml helpers.
     *
     * @param trans the given transition
     * @return MStateVertex the source statevertex
     */
    Object getSource(Object trans);

    /**
     * Returns the destination of the given transition. This operation is here
     * to give a full implementation of all getSource and getDestination methods
     * on the uml helpers.<p>
     *
     * @param trans the given transition
     * @return MStateVertex the destination statevertex
     */
    Object getDestination(Object trans);

    /**
     * Gets the statemachine that contains the given Object
     * Traverses the state hierarchy of the statemachine
     * untill the statemachine is reached.  To decouple ArgoUML as
     * much as possible from the model implementation, the parameter of the
     * method is of type Object, and the result, too.<p>
     * 
     * Only if the given handle is null, then an exception is thrown.
     *
     * @param handle The element for which we want to know the
     * statemachine
     * @return Object MStateMachine The statemachine the state belongs too or
     * null if the given handle is not contained in this statemachine
     */
    Object getStateMachine(Object handle);

    /**
     * Couples a given event to the given transition as being trigger
     * event. To decouple ArgoUML as much as possible from the model
     * implementation, the parameters of the method are of type Object.<p>
     *
     * @param transition the given transition
     * @param event the given event or null
     */
    void setEventAsTrigger(Object transition, Object event);

    /**
     * Returns true if a statemachine may be added to the given
     * context. To decouple ArgoUML as much as possible from the model
     * implementation, the parameter of the method is of type Object.<p>
     *
     * @param context the given context
     * @return boolean true if a statemachine may be added
     */
    boolean isAddingStatemachineAllowed(Object context);

    /**
     * Returns true is the given state is the top state.
     *
     * @author MVW
     * @param o CompositeState
     * @return boolean
     */
    boolean isTopState(Object o);

    /**
     * Returns all statemachines that can be the statemachine the given
     * submachinestate represents. 
     * 
     * @param model the model
     * @param oSubmachineState The submachinestate we are searching the
     * statemachines for.
     * @return Collection The collection with found statemachines.
     */
    Collection getAllPossibleStatemachines(Object model,
            Object oSubmachineState);

    /**
     * Returns all states that can be recursively contained by the given State.
     * <p>
     * TODO: {@link #getAllPossibleSubvertices(Object)} and
     * {@link #getAllSubStates(Object)} are duplicates as implemented for MDR.
     * It isn't clear from the Javadoc or usage whether they should do different
     * things or this just happened because no one noticed we already had
     * another method.
     * 
     * @param oState
     *                the Composite state we are searching the states for,
     * @return Collection the collection with found states
     */
    Collection getAllPossibleSubvertices(Object oState);

    /**
     * Connects a given statemachine to a submachinestate as being the
     * statemachine the submachinestate represents. 
     * 
     * @param oSubmachineState The submachinestate for which we want to set the
     * property submachine
     * @param oStatemachine The statemachine
     */
    void setStatemachineAsSubmachine(Object oSubmachineState,
            Object oStatemachine);

    /**
     * Get the "top" composite state of a statemachine.<p>
     *
     * @param sm the given statemachine
     * @return the top composite state
     */
    Object getTop(Object sm);

    /**
     * Get the regions of a state or statemachine.<p>
     *
     * @param hanlde the given state or statemachine
     * @return the regions
     */
    List getRegions(Object handle);

    
    /**
     * Gets all statevertices that are a target to transitions
     * outgoing from the given statevertex.<p>
     *
     * @param ostatevertex  the given statevertex
     * @return Collection   all statevertices that are a target
     */
    Collection getOutgoingStates(Object ostatevertex);

    /**
     * Finds the operation to which a CallEvent refers, based on the given 
     * transition. This function determines the context of the statemachine 
     * that contains the given transition. If the context is a Classifier, then 
     * all operations of this classifier are candidates. If the context is a 
     * BehavioralFeature, then all operations of the owner Classifier are 
     * candidates. The first operation of which the name matches is the result.
     * For Activity diagrams: If the context is a Package, then
     * all Operations of all Classifiers in the package are candidates.
     * TODO: This function works for the most normal cases,
     * but needs some testing for rare cases, e.g. internal transitions,...
     * TODO: The Parameters of the candidates are ignored. Maybe this 
     * method should return a set of matching operations.
     *
     * @author MVW
     * @param trans Object of type Transition
     * @param opname the name of the operation sought
     * @return Object the operation with the given name, or null
     */
    Object findOperationByName(Object trans, String opname);

    /**
     * Returns all substates some composite state contains.
     * <p>
     * TODO: {@link #getAllPossibleSubvertices(Object)} and
     * {@link #getAllSubStates(Object)} are duplicates as implemented for MDR.
     * It isn't clear from the Javadoc or usage whether they should do different
     * things or this just happened because no one noticed we already had
     * another method.
     * 
     * @param compState
     *                the given compositestate
     * @return all substates
     */
    Collection getAllSubStates(Object compState);
    
    /**
     * Return the transitions belonging to the given StateMachine. 
     * The boolean parameter determines the kinds of Transitions returned.
     * Never returns null. 
     * 
     * @param handle a StateMachine
     * @param includeInternals include all internal transitions
     * @return Collection of Transitions
     * @since 0.33.4
     */
    Collection getTransitions(Object handle, 
            boolean includeInternals);

    /**
     * Remove a given subvertex from a given composite state.
     *
     * @param handle the composite state
     * @param subvertex the StateVertex
     */
    void removeSubvertex(Object handle, Object subvertex);

    /**
     * Add a subvertex to a composite state.
     *
     * @param handle the CompositeState
     * @param subvertex the StateVertex
     */
    void addSubvertex(Object handle, Object subvertex);

    /**
     * Sets the Bound of some SynchState.
     *
     * @param handle Synch State
     * @param bound A positive integer or the value "unlimited" specifying
     *              the maximal count of the SynchState. The count is the
     *              difference between the number of times the incoming
     *              and outgoing transitions of the synch state are fired.
     */
    void setBound(Object handle, int bound);

    /**
     * Makes a Composite State concurrent.
     *
     * @param handle the CompositState
     * @param concurrent boolean
     */
    void setConcurrent(Object handle, boolean concurrent);

    /**
     * Set the container of a statevertex.
     *
     * @param handle is the stateVertex
     * @param compositeState is the container. Can be <code>null</code>.
     */
    void setContainer(Object handle, Object compositeState);

    /**
     * Sets the do activity of a state.
     *
     * @param handle is the state
     * @param value the activity. Can be <code>null</code>.
     */
    void setDoActivity(Object handle, Object value);

    /**
     * Sets the effect of some transition.
     *
     * @param handle is the transition
     * @param value is the effect. Can be <code>null</code>.
     */
    void setEffect(Object handle, Object value);

    /**
     * Sets the entry action of some state.
     *
     * @param handle is the state
     * @param value is the action. Can be <code>null</code>.
     */
    void setEntry(Object handle, Object value);

    /**
     * Sets the exit action of some state.
     *
     * @param handle is the state
     * @param value is the action. Can be <code>null</code>.
     */
    void setExit(Object handle, Object value);

    /**
     * Set the Expression of a Guard or ChangeEvent.
     *
     * @param handle Guard or ChangeEvent
     * @param value BooleanExpression or null
     */
    void setExpression(Object handle, Object value);

    /**
     * Sets the guard of a transition.
     *
     * @param handle to the transition
     * @param guard to be set. Can be null.
     */
    void setGuard(Object handle, Object guard);

    /**
     * @param handle is the target.
     * @param intTrans is a collection of transitions.
     */
    void setInternalTransitions(Object handle, Collection intTrans);

    /**
     * Sets the source state of some transition.
     *
     * @param handle the transition
     * @param state the source state or null
     */
    void setSource(Object handle, Object state);

    /**
     * Sets the state of an internal transition.
     *
     * @param handle the internal transition
     * @param element the state that contains this transition
     */
    void setState(Object handle, Object element);

    /**
     * Sets a state machine of some state or transition.
     *
     * @param handle is the state or transition
     * @param stm is the state machine
     */
    void setStateMachine(Object handle, Object stm);

    /**
     * Set the collection of substates for a CompositeState.
     *
     * @param handle CompositeState
     * @param subvertices collection of sub-StateVertexes
     */
    void setSubvertices(Object handle, Collection subvertices);

    /**
     * Sets the trigger event of a transition.
     *
     * @param handle is the transition
     * @param event is the trigger event or null
     */
    void setTrigger(Object handle, Object event);

    /**
     * Sets the time-expression for a TimeEvent.
     *
     * @param handle Object (TimeEvent)
     * @param value Object (TimeExpression)
     */
    void setWhen(Object handle, Object value);

    /**
     * Sets the change-expression for a ChangeEvent.
     *
     * @param handle Object (ChangeEvent)
     * @param value Object (BooleanExpression)
     */
    void setChangeExpression(Object handle, Object value);
    
    /**
     * Returns the path of a state vertex.
     * @param o the StateVertex
     * @return String
     */
    String getPath(Object o);

    /**
     * Returns a state contained into container.
     *
     * @param path The whole pathname of the state we are looking for.
     * @param container of the state
     * @return Object
     */
    Object getStatebyName(String path, Object container);

    /**
     * Sets the Referenced State of a StubState.
     *
     * @param o Stub State
     * @param referenced state
     */
    void setReferenceState(Object o, String referenced);

    /**
     * Find the correct namespace for an event.
     * This explained by the following
     * quote from the UML spec:
     * "The event declaration has scope within
     * the package it appears in and may be used in
     * state diagrams for classes that have visibility
     * inside the package. An event is not local to
     * a single class."
     *
     * @param trans the transition of which the event is a trigger
     * @param model the default is the model
     * @return the enclosing namespace for the event
     */
    Object findNamespaceForEvent(Object trans, Object model);

    /**
     * Set the Context of a statemachine.
     *
     * @param statemachine The state machine.
     * @param modelElement The context.
     */
    void setContext(Object statemachine, Object modelElement);

    /**
     * Add a deferrable event to a state.
     *
     * @param state The state.
     * @param deferrableEvent The deferrable event.
     */
    void addDeferrableEvent(Object state, Object deferrableEvent);

    /**
     * Remove a deferrable event from a state.
     *
     * @param state The state.
     * @param deferrableEvent The referrable event.
     */
    void removeDeferrableEvent(Object state, Object deferrableEvent);

}
