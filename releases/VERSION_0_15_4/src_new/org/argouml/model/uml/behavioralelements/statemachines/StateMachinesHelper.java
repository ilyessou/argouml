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

package org.argouml.model.uml.behavioralelements.statemachines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.model.ModelFacade;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;

import ru.novosoft.uml.behavior.state_machines.MEvent;
import ru.novosoft.uml.behavior.state_machines.MState;
import ru.novosoft.uml.behavior.state_machines.MStateMachine;
import ru.novosoft.uml.behavior.state_machines.MStateVertex;
import ru.novosoft.uml.behavior.state_machines.MSubmachineState;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MClassifier;

/**
 * Helper class for UML BehavioralElements::StateMachines Package.
 *
 * Current implementation is a placeholder.
 * 
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @stereotype singleton
 */
public class StateMachinesHelper {

    /** Don't allow instantiation.
     */
    private StateMachinesHelper() {
    }

    /**
     * Singleton instance.
     */
    private static StateMachinesHelper SINGLETON = new StateMachinesHelper();

    /** 
     * Singleton instance access method.
     */
    public static StateMachinesHelper getHelper() {
        return SINGLETON;
    }

    /**
     * Returns the source of the given transition. This operation is here to 
     * give a full implementation of all getSource and getDestination methods
     * on the uml helpers.
     *
     * @param trans
     * @return MStateVertex
     */
    public Object getSource(Object trans) {
        return ((MTransition) trans).getSource();
    }

    /**
     * Returns the destination of the given transition. This operation is here 
     * to give a full implementation of all getSource and getDestination methods
     * on the uml helpers.<p>
     *
     * @param trans
     * @return MStateVertex
     */
    public Object getDestination(Object trans) {
        return ((MTransition) trans).getTarget();
    }

    

    

    /**
     * Gets the statemachine that contains the given parameter
     * oState. Traverses the state hierarchy of the statemachine
     * untill the statemachine is reached.  To decouple ArgoUML as
     * much as possible from the NSUML model, the parameter of the
     * method is of type Object.<p>
     *
     * @param oStateVertex The state for which we want to know the
     * statemachine
     * @return MStateMachine The statemachine the state belongs too or
     * null if the given parameter is not a state or null itself.
     */
    public MStateMachine getStateMachine(Object oStateVertex) {
        if (oStateVertex instanceof MStateVertex) {
            MStateVertex state = (MStateVertex) oStateVertex;
            if (state instanceof MState
		&& ((MState) state).getStateMachine() != null) 
	    {
                return ((MState) state).getStateMachine();
            } else
                return getStateMachine(state.getContainer());
        }
        return null;
    }

    /**
     * Couples a given event to the given transition as being trigger
     * event. To decouple ArgoUML as much as possible from the NSUML
     * model, the parameters of the method are of type Object.<p>
     *
     * @param transition
     * @param event
     */
    public void setEventAsTrigger(Object transition, Object event) {
        if (transition == null || !(transition instanceof MTransition)) {
            throw new IllegalArgumentException("Transition either null or not "
					       + "an instance of MTransition");
        }
        if (event == null || !(event instanceof MEvent)) {
            throw new IllegalArgumentException("Event either null or not an "
					       + "instance of MEvent");
        }
        ((MTransition) transition).setTrigger((MEvent) event);
    }
    
    /**
     * Returns true if a statemachine may be added to the given
     * context. To decouple ArgoUML as much as possible from the NSUML
     * model, the parameter of the method is of type Object.<p>
     *
     * @param context
     * @return boolean
     */
    public boolean isAddingStatemachineAllowed(Object context) {
        if (context instanceof MBehavioralFeature
	    || context instanceof MClassifier)
	{
            return true;
	}
        return false;
    }
    
    /**
     * Returns all statemachines that can be the statemachine the given
     * submachinestate represents. To decouple ArgoUML as much as possible from
     * the NSUML model, the parameter of the method is of type Object.
     * @param oSubmachineState The submachinestate we are searching the
     * statemachines for.
     * @return Collection The collection with found statemachines.
     */
    public Collection getAllPossibleStatemachines(Object oSubmachineState) {
        if (oSubmachineState instanceof MSubmachineState) {
            Collection statemachines =
		ModelManagementHelper.getHelper()
		    .getAllModelElementsOfKind(MStateMachine.class);
            statemachines.remove(getStateMachine(oSubmachineState));
            return statemachines;
        }
        return null;
    }
    
    /**
     * Connects a given statemachine to a submachinestate as being the
     * statemachine the submachinestate represents. To decouple ArgoUML as much
     * as possible from the NSUML model, the parameters of the method are of
     * type Object.
     * @param oSubmachineState The submachinestate for which we want to set the
     * property submachine
     * @param oStatemachine The statemachine
     */
    public void setStatemachineAsSubmachine(Object oSubmachineState,
					    Object oStatemachine)
    {
        if (oSubmachineState instanceof MSubmachineState
	    && oStatemachine instanceof MStateMachine)
	{
	    MSubmachineState mss = (MSubmachineState) oSubmachineState;
	    mss.setSubmachine((MStateMachine) oStatemachine);
        }
    }
    
    public MState getTop(Object sm) {
        
        if (!(sm instanceof MStateMachine))
            throw new IllegalArgumentException();
        
        if (sm == null)
        	return null;
        return ((MStateMachine) sm).getTop();
    }
    
    /**
     * Gets all statevertices that are a target to transitions
     * outgoing from the given statevertex.<p>
     *
     * @param ostatevertex
     * @return Collection
     */
    public Collection getOutgoingStates(Object ostatevertex) {
        if (ModelFacade.isAStateVertex(ostatevertex)) {
            MStateVertex statevertex = (MStateVertex) ostatevertex;
            Collection col = new ArrayList();
            Iterator it = statevertex.getOutgoings().iterator();
            while (it.hasNext()) {
                col.add(((MTransition) it.next()).getTarget());           
            }
            return col;
        }
        return null;
    }
    
    

}
