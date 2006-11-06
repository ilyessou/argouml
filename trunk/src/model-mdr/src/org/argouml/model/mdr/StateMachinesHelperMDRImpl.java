// $Id$
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

package org.argouml.model.mdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.jmi.reflect.InvalidObjectException;

import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.argouml.model.StateMachinesHelper;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.Argument;
import org.omg.uml.behavioralelements.statemachines.ChangeEvent;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.Event;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.StubState;
import org.omg.uml.behavioralelements.statemachines.SubmachineState;
import org.omg.uml.behavioralelements.statemachines.SynchState;
import org.omg.uml.behavioralelements.statemachines.TimeEvent;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.datatypes.BooleanExpression;
import org.omg.uml.foundation.datatypes.Expression;
import org.omg.uml.foundation.datatypes.TimeExpression;

/**
 * The State Machines Helper Implementation for MDR.
 *
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * @author Tom Morris
 */
public class StateMachinesHelperMDRImpl implements StateMachinesHelper {

    private MDRModelImplementation implementation;

    /**
     * Constructor.
     *
     * @param impl The ModelImplementation
     */
    public StateMachinesHelperMDRImpl(MDRModelImplementation impl) {
        super();
        this.implementation = impl;
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getSource(java.lang.Object)
     */
    public Object getSource(Object trans) {
        try {
            if (trans instanceof Transition) {
                return ((Transition) trans).getSource();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("bad argument to "
                + "getSource() - " + trans);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getDestination(java.lang.Object)
     */
    public Object getDestination(Object trans) {
        try {
            if (trans instanceof Transition) {
                return ((Transition) trans).getTarget();
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("bad argument to "
                + "getDestination() - " + trans);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getStateMachine(java.lang.Object)
     */
    public Object getStateMachine(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException("bad argument to "
                    + "getStateMachine() - " + handle);
        }
        try {
            Object container =
                implementation.getFacade().getModelElementContainer(handle);
            while (container != null) {
                if (Model.getFacade().isAStateMachine(container)) {
                    return container;
                }
                container =
                    implementation.getFacade()
                        .getModelElementContainer(container);
            }
            /* In this case, either the container was not set,
             * or it was not contained in a statemachine.
             */
            return null;

        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setEventAsTrigger(java.lang.Object,
     *      java.lang.Object)
     */
    public void setEventAsTrigger(Object transition, Object event) {
        if (!(transition instanceof Transition)) {
            throw new IllegalArgumentException("Transition either null or not "
                    + "an instance of MTransition");
        }
        if (event != null && !(event instanceof Event)) {
            throw new IllegalArgumentException("Event not an "
                    + "instance of MEvent");
        }
        ((Transition) transition).setTrigger((Event) event);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#isAddingStatemachineAllowed(java.lang.Object)
     */
    public boolean isAddingStatemachineAllowed(Object context) {
        return (context instanceof BehavioralFeature
                || context instanceof Classifier);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#isTopState(java.lang.Object)
     */
    public boolean isTopState(Object o) {
        try {
            if (o instanceof CompositeState) {
                CompositeState cs = (CompositeState) o;
                return (cs.getContainer() == null);
            }
            return false;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getAllPossibleStatemachines(java.lang.Object,
     *      java.lang.Object)
     */
    public Collection getAllPossibleStatemachines(Object model,
            Object oSubmachineState) {
        try {
            if (oSubmachineState instanceof SubmachineState) {
                Collection statemachines =
                    Model.getModelManagementHelper()
                        .getAllModelElementsOfKind(model, StateMachine.class);
                statemachines.remove(getStateMachine(oSubmachineState));
                return statemachines;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException(
                "Argument must be a SubmachineState");
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getAllPossibleSubvertices(java.lang.Object)
     */
    public Collection getAllPossibleSubvertices(Object oState) {
        ArrayList v = new ArrayList();
        List v2 = new ArrayList();
        try {
            if (oState instanceof CompositeState) {
                v.addAll(((CompositeState) oState).getSubvertex());
                v2 = (ArrayList) v.clone();
                Iterator it = v2.iterator();
                while (it.hasNext()) {
                    v.addAll(getAllPossibleSubvertices(it.next()));
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return v;
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setStatemachineAsSubmachine(java.lang.Object,
     *      java.lang.Object)
     */
    public void setStatemachineAsSubmachine(Object oSubmachineState,
            Object oStatemachine) {
        if (oSubmachineState instanceof SubmachineState
                && oStatemachine instanceof StateMachine) {
            SubmachineState mss = (SubmachineState) oSubmachineState;
            mss.setSubmachine((StateMachine) oStatemachine);
            return;
        }
        throw new IllegalArgumentException("oSubmachineState: "
                + oSubmachineState + ",oStatemachine: " + oStatemachine);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getTop(java.lang.Object)
     */
    public Object getTop(Object sm) {
        if (!(sm instanceof StateMachine)) {
            throw new IllegalArgumentException();
        }
        if (sm == null) {
            return null;
        }
        
        try  {
            return ((StateMachine) sm).getTop();
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getOutgoingStates(java.lang.Object)
     */
    public Collection getOutgoingStates(Object ostatevertex) {
        try {
            if (ostatevertex instanceof StateVertex) {
                StateVertex statevertex = (StateVertex) ostatevertex;
                Collection col = new ArrayList();
                Iterator it = statevertex.getOutgoing().iterator();
                while (it.hasNext()) {
                    col.add(((Transition) it.next()).getTarget());
                }
                return col;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException(
                "Argument must be a StateVertex");
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#findOperationByName(java.lang.Object,
     *      java.lang.String)
     */
    public Object findOperationByName(Object trans, String opname) {
        if (!(trans instanceof Transition)) {
            throw new IllegalArgumentException();
        }
        try {
            Object sm = getStateMachine(trans);
            Object ns = Model.getFacade().getNamespace(sm);
            if (ns instanceof Classifier) {
                Collection c = Model.getFacade().getOperations(ns);
                Iterator i = c.iterator();
                while (i.hasNext()) {
                    Object op = i.next();
                    String on = ((ModelElement) op).getName();
                    if (on.equals(opname)) {
                        return op;
                    }
                }
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getAllSubStates(java.lang.Object)
     */
    public Collection getAllSubStates(Object compState) {
        try {
            if (compState instanceof CompositeState) {
                List retList = new ArrayList();
                Iterator it =
                    Model.getFacade().getSubvertices(compState).iterator();
                while (it.hasNext()) {
                    Object subState = it.next();
                    if (subState instanceof CompositeState) {
                        retList.addAll(getAllSubStates(subState));
                    }
                    retList.add(subState);
                }
                return retList;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException(
                "Argument is not a composite state");
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#removeSubvertex(java.lang.Object,
     *      java.lang.Object)
     */
    public void removeSubvertex(Object handle, Object subvertex) {
        try {
            if (handle instanceof CompositeState
                    && subvertex instanceof StateVertex) {
                ((CompositeState) handle).getSubvertex().remove(subvertex);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or subvertex: " + subvertex);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#addSubvertex(java.lang.Object,
     *      java.lang.Object)
     */
    public void addSubvertex(Object handle, Object subvertex) {
        if (handle instanceof CompositeState
                && subvertex instanceof StateVertex) {
            implementation.getUmlPackage().getStateMachines().
                    getAContainerSubvertex().add((CompositeState) handle,
                            (StateVertex) subvertex);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or subvertex: " + subvertex);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setBound(java.lang.Object,
     *      int)
     */
    public void setBound(Object handle, int bound) {
        if (handle instanceof SynchState) {
            ((SynchState) handle).setBound(bound);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or bound: "
                + bound);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setConcurrent(java.lang.Object,
     *      boolean)
     */
    public void setConcurrent(Object handle, boolean concurrent) {
        if (handle instanceof CompositeState) {
            ((CompositeState) handle).setConcurrent(concurrent);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setContainer(java.lang.Object,
     *      java.lang.Object)
     */
    public void setContainer(Object handle, Object compositeState) {
        if (handle instanceof StateVertex
                && (compositeState == null
                        || compositeState instanceof CompositeState)) {
            ((StateVertex) handle).
                    setContainer((CompositeState) compositeState);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or compositeState: " + compositeState);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setDoActivity(java.lang.Object,
     *      java.lang.Object)
     */
    public void setDoActivity(Object handle, Object value) {
        if (handle instanceof State
                && (value == null || value instanceof Action)) {
            ((State) handle).setDoActivity((Action) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setEffect(java.lang.Object,
     *      java.lang.Object)
     */
    public void setEffect(Object handle, Object value) {
        if (handle instanceof Transition
                && (value == null || value instanceof Action)) {
            ((Transition) handle).setEffect((Action) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setEntry(java.lang.Object,
     *      java.lang.Object)
     */
    public void setEntry(Object handle, Object value) {
        if (handle instanceof State
                && (value == null || value instanceof Action)) {
            ((State) handle).setEntry((Action) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setExit(java.lang.Object,
     *      java.lang.Object)
     */
    public void setExit(Object handle, Object value) {
        if (handle instanceof State
                && (value == null || value instanceof Action)) {
            ((State) handle).setExit((Action) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setExpression(java.lang.Object,
     *      java.lang.Object)
     */
    public void setExpression(Object handle, Object value) {
        if (handle instanceof Guard
                && (value == null || value instanceof BooleanExpression)) {
            ((Guard) handle).setExpression((BooleanExpression) value);
            return;
        }
        if (handle instanceof ChangeEvent
                && (value == null || value instanceof BooleanExpression)) {
            ChangeEvent ce = (ChangeEvent) handle;
            ce.setChangeExpression((BooleanExpression) value);
            return;
        }
        if (handle instanceof Argument
                && (value == null || value instanceof Expression)) {
            Argument arg = (Argument) handle;
            arg.setValue((Expression) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setGuard(java.lang.Object,
     *      java.lang.Object)
     */
    public void setGuard(Object handle, Object guard) {
        if (handle instanceof Transition
                && (guard == null || guard instanceof Guard)) {
            ((Transition) handle).setGuard((Guard) guard);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or guard: "
                + guard);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setInternalTransitions(java.lang.Object,
     *      java.util.Collection)
     */
    public void setInternalTransitions(Object handle, Collection intTrans) {
        if (handle instanceof State) {
            Collection internalTransitions =
                Model.getFacade().getInternalTransitions(handle);
            if (!internalTransitions.isEmpty()) {
                Vector verts = new Vector();
                verts.addAll(internalTransitions);
                Iterator toRemove = verts.iterator();
                while (toRemove.hasNext()) {
                    removeTransition(handle, toRemove.next());
                }
            }
            if (!intTrans.isEmpty()) {
                Iterator toAdd = intTrans.iterator();
                while (toAdd.hasNext()) {
                    addTransition(handle, toAdd.next());
                }
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    /**
     * Remove a transition.
     * 
     * TODO: This is not part of the public API.  Reduce visibility - tfm
     * 
     * @param handle The state
     * @param intTrans The internal transition to remove
     */
    public void removeTransition(Object handle, Object intTrans) {
        try {
            if (handle instanceof State && intTrans instanceof Transition) {
                ((State) handle).getInternalTransition().remove(intTrans);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or intTrans: " + intTrans);
    }

    /**
     * Add a transition.
     * 
     * TODO: This is not part of the public API.  Reduce visibility - tfm
     * 
     * @param handle The state
     * @param intTrans The internal transition to add
     */
    public void addTransition(Object handle, Object intTrans) {
        if (handle instanceof State && intTrans instanceof Transition) {
            ((State) handle).getInternalTransition().add(intTrans);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or subvertex: " + intTrans);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setSource(java.lang.Object,
     *      java.lang.Object)
     */
    public void setSource(Object handle, Object state) {
        if (handle instanceof Transition && state instanceof StateVertex) {
            ((Transition) handle).setSource((StateVertex) state);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or state: "
                + state);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setState(java.lang.Object,
     *      java.lang.Object)
     */
    public void setState(Object handle, Object element) {
        if (handle instanceof Transition && element instanceof State) {
            addTransition(element, handle);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or element: " + element);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setStateMachine(java.lang.Object,
     *      java.lang.Object)
     */
    public void setStateMachine(Object handle, Object stm) {
        if (handle instanceof State
                && (stm == null || stm instanceof StateMachine)) {
            ((State) handle).setStateMachine((StateMachine) stm);
            return;
        }
        if (handle instanceof Transition
                && (stm == null || stm instanceof StateMachine)) {
            ((Transition) handle).setStateMachine((StateMachine) stm);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or stm: "
                + stm);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setSubvertices(java.lang.Object,
     *      java.util.Collection)
     */
    public void setSubvertices(Object handle, Collection subvertices) {
        if (handle instanceof CompositeState) {
            Collection vertices = Model.getFacade().getSubvertices(handle);
            if (!vertices.isEmpty()) {
                Vector verts = new Vector();
                verts.addAll(vertices);
                Iterator toRemove = verts.iterator();
                while (toRemove.hasNext()) {
                    removeSubvertex(handle, toRemove.next());
                }
            }
            if (!subvertices.isEmpty()) {
                Iterator toAdd = subvertices.iterator();
                while (toAdd.hasNext()) {
                    addSubvertex(handle, toAdd.next());
                }
            }
            return;
        }
        throw new IllegalArgumentException("handle: " + handle
                + " or subvertices: " + subvertices);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setTrigger(java.lang.Object,
     *      java.lang.Object)
     */
    public void setTrigger(Object handle, Object event) {
        if (handle instanceof Transition
                && (event == null || event instanceof Event)) {
            ((Transition) handle).setTrigger((Event) event);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or event: "
                + event);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setWhen(java.lang.Object,
     *      java.lang.Object)
     */
    public void setWhen(Object handle, Object value) {
        if (handle instanceof TimeEvent
                && (value == null || value instanceof TimeExpression)) {
            ((TimeEvent) handle).setWhen((TimeExpression) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setChangeExpression(
     * java.lang.Object, java.lang.Object)
     */
    public void setChangeExpression(Object handle, Object value) {
        if (handle instanceof ChangeEvent
                && (value == null || value instanceof BooleanExpression)) {
            ((ChangeEvent) handle)
                    .setChangeExpression((BooleanExpression) value);
            return;
        }
        throw new IllegalArgumentException("handle: " + handle + " or value: "
                + value);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getPath(java.lang.Object)
     */
    public String getPath(Object o) {
        try {
            if (o instanceof StateVertex) {
                Object o1 = o;
                Object o2 = Model.getFacade().getContainer(o1);
                String path = Model.getFacade().getName(o1);
                while ((o2 != null) && (!Model.getFacade().isTop(o2))) {
                    path = Model.getFacade().getName(o2) + "::" + path;
                    o1 = o2;
                    o2 = Model.getFacade().getContainer(o1);
                }
                return path;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException(
                "Argument must be a StateVertex");
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#getStatebyName(java.lang.String,
     *      java.lang.Object)
     */
    public Object getStatebyName(String path, Object container) {
        try {
            // TODO: This should probably through an exception if it gets
            // invalid arguments rather than just returning null
            if (container != null 
                    && Model.getFacade().isACompositeState(container)
                    && path != null) {

                Iterator it = getAllPossibleSubvertices(container).iterator();
                int index = path.lastIndexOf("::");
                if (index != -1) {
                    index += 2;
                } else {
                    index += 1;
                }
                
                path = path.substring(index);
                while (it.hasNext()) {
                    Object o = it.next();
                    Object oName = Model.getFacade().getName(o);
                    if (oName != null && oName.equals(path)) {
                        return o;
                    }
                }
                
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        return null;
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setReferenceState(java.lang.Object,
     *      java.lang.String)
     */
    public void setReferenceState(Object o, String referenced) {
        if (o instanceof StubState) {
            ((StubState) o).setReferenceState(referenced);
            return;
        }
        throw new IllegalArgumentException("handle: " + o);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#findNamespaceForEvent(java.lang.Object, java.lang.Object)
     */
    public Object findNamespaceForEvent(Object trans, Object model) {
        try {
            Object enclosing =
                Model.getStateMachinesHelper().getStateMachine(trans);
            while ((!Model.getFacade().isAPackage(enclosing))
                    && (enclosing != null)) {
                enclosing = Model.getFacade().getNamespace(enclosing);
            }
            if (enclosing == null) {
                enclosing = model;
            }
            return enclosing;
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#addDeferrableEvent(java.lang.Object, java.lang.Object)
     */
    public void addDeferrableEvent(Object state, Object deferrableEvent) {
        if (state instanceof State && deferrableEvent instanceof Event) {
            implementation.getUmlPackage().getStateMachines()
                    .getAStateDeferrableEvent().add((State) state,
                            (Event) deferrableEvent);
            return;
        }
        throw new IllegalArgumentException("handle: " + state + " or evt: "
                + deferrableEvent);
    }
    /*
     * @see org.argouml.model.StateMachinesHelper#removeDeferrableEvent(java.lang.Object, java.lang.Object)
     */
    public void removeDeferrableEvent(Object state, Object deferrableEvent) {
        try {
            if (state instanceof State && deferrableEvent instanceof Event) {
                implementation.getUmlPackage().getStateMachines()
                        .getAStateDeferrableEvent().remove((State) state,
                                (Event) deferrableEvent);
                return;
            }
        } catch (InvalidObjectException e) {
            throw new InvalidElementException(e);
        }
        throw new IllegalArgumentException("handle: " + state + " or evt: "
                + deferrableEvent);
    }

    /*
     * @see org.argouml.model.StateMachinesHelper#setContext(java.lang.Object, java.lang.Object)
     */
    public void setContext(Object statemachine, Object modelElement) {
        if (statemachine instanceof StateMachine
                && modelElement instanceof ModelElement) {
            ((StateMachine) statemachine)
                    .setContext((ModelElement) modelElement);
            return;
        }
        throw new IllegalArgumentException("handle: " + statemachine
                + " or me: " + modelElement);
    }
}
