// Copyright (c) 1996-2002 The Regents of the University of California. All
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

import org.argouml.model.uml.AbstractUmlModelFactory;
import org.argouml.model.uml.UmlFactory;

import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.state_machines.MCallEvent;
import ru.novosoft.uml.behavior.state_machines.MChangeEvent;
import ru.novosoft.uml.behavior.state_machines.MCompositeState;
import ru.novosoft.uml.behavior.state_machines.MEvent;
import ru.novosoft.uml.behavior.state_machines.MFinalState;
import ru.novosoft.uml.behavior.state_machines.MGuard;
import ru.novosoft.uml.behavior.state_machines.MPseudostate;
import ru.novosoft.uml.behavior.state_machines.MSignalEvent;
import ru.novosoft.uml.behavior.state_machines.MSimpleState;
import ru.novosoft.uml.behavior.state_machines.MState;
import ru.novosoft.uml.behavior.state_machines.MStateMachine;
import ru.novosoft.uml.behavior.state_machines.MStateVertex;
import ru.novosoft.uml.behavior.state_machines.MStubState;
import ru.novosoft.uml.behavior.state_machines.MSubmachineState;
import ru.novosoft.uml.behavior.state_machines.MSynchState;
import ru.novosoft.uml.behavior.state_machines.MTimeEvent;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;

/**
 * Factory to create UML classes for the UML
 * BehaviorialElements::StateMachines package.
 *
 * MEvent and MStateVertex do not have create methods
 * since they are abstract classes in the NSUML model.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 */
public class StateMachinesFactory extends AbstractUmlModelFactory {

    /** Singleton instance.
     */
    private static StateMachinesFactory SINGLETON =
                   new StateMachinesFactory();

    /** Singleton instance access method.
     */
    public static StateMachinesFactory getFactory() {
        return SINGLETON;
    }

    /** Don't allow instantiation
     */
    private StateMachinesFactory() {
    }

    /** Create an empty but initialized instance of a UML CallEvent.
     *  
     *  @return an initialized UML CallEvent instance.
     */
    public MCallEvent createCallEvent() {
        MCallEvent modelElement = MFactory.getDefaultFactory().createCallEvent();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML ChangeEvent.
     *  
     *  @return an initialized UML ChangeEvent instance.
     */
    public MChangeEvent createChangeEvent() {
        MChangeEvent modelElement = MFactory.getDefaultFactory().createChangeEvent();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML CompositeState.
     *  
     *  @return an initialized UML CompositeState instance.
     */
    public MCompositeState createCompositeState() {
        MCompositeState modelElement = MFactory.getDefaultFactory().createCompositeState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML FinalState.
     *  
     *  @return an initialized UML FinalState instance.
     */
    public MFinalState createFinalState() {
        MFinalState modelElement = MFactory.getDefaultFactory().createFinalState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Guard.
     *  
     *  @return an initialized UML Guard instance.
     */
    public MGuard createGuard() {
        MGuard modelElement = MFactory.getDefaultFactory().createGuard();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Pseudostate.
     *  
     *  @return an initialized UML Pseudostate instance.
     */
    public MPseudostate createPseudostate() {
        MPseudostate modelElement = MFactory.getDefaultFactory().createPseudostate();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML SignalEvent.
     *  
     *  @return an initialized UML SignalEvent instance.
     */
    public MSignalEvent createSignalEvent() {
        MSignalEvent modelElement = MFactory.getDefaultFactory().createSignalEvent();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML SimpleState.
     *  
     *  @return an initialized UML SimpleState instance.
     */
    public MSimpleState createSimpleState() {
        MSimpleState modelElement = MFactory.getDefaultFactory().createSimpleState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML State.
     *  
     *  @return an initialized UML State instance.
     */
    public MState createState() {
        MState modelElement = MFactory.getDefaultFactory().createState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML StateMachine.
     *  
     *  @return an initialized UML StateMachine instance.
     */
    public MStateMachine createStateMachine() {
        MStateMachine modelElement = MFactory.getDefaultFactory().createStateMachine();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML StubState.
     *  
     *  @return an initialized UML StubState instance.
     */
    public MStubState createStubState() {
        MStubState modelElement = MFactory.getDefaultFactory().createStubState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML SubmachineState.
     *  
     *  @return an initialized UML SubmachineState instance.
     */
    public MSubmachineState createSubmachineState() {
        MSubmachineState modelElement = MFactory.getDefaultFactory().createSubmachineState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML SynchState.
     *  
     *  @return an initialized UML SynchState instance.
     */
    public MSynchState createSynchState() {
        MSynchState modelElement = MFactory.getDefaultFactory().createSynchState();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML TimeEvent.
     *  
     *  @return an initialized UML TimeEvent instance.
     */
    public MTimeEvent createTimeEvent() {
        MTimeEvent modelElement = MFactory.getDefaultFactory().createTimeEvent();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML Transition.
     *  
     *  @return an initialized UML Transition instance.
     */
    public MTransition createTransition() {
        MTransition modelElement = MFactory.getDefaultFactory().createTransition();
	super.initialize(modelElement);
	return modelElement;
    }
    
	/**
	 * Builds a complete transition including all associations (statemachine the
     * transition belongs to, source the transition is coming from, destination
     * the transition is going to).
	 * @param owningStatemachine
	 * @param source
	 * @param dest
	 * @return MTransition
	 */
    public MTransition buildTransition(MStateMachine owningStatemachine, 
        MStateVertex source, MStateVertex dest) {
      MTransition trans = createTransition();
      owningStatemachine.addTransition(trans);
      trans.setSource(source);
      trans.setTarget(dest);
      return trans;
    }
    
	/**
	 * Builds a compositestate as top for some statemachine
	 * @param statemachine
	 * @return MCompositeState
	 */
    public MCompositeState buildCompositeState(MStateMachine statemachine) {
    	if (statemachine != null ) {
    		MCompositeState state = createCompositeState();
    		state.setStateMachine(statemachine);
    		return state;
    	} else
    		throw new IllegalArgumentException("In buildCompositeState: statemachine is null");
    }
    
    /**
	 * Builds a state machine owned by the given context
	 * @param context
	 * @return MActivityGraph
	 */
    public MStateMachine buildStateMachine(MModelElement context) {
    	if (context != null && (context instanceof MBehavioralFeature || context instanceof MClassifier)) {
    		MStateMachine graph = createStateMachine();
    		graph.setContext(context);
    		if (context instanceof MNamespace) {
    			graph.setNamespace((MNamespace)context);
    		} else
    		if (context instanceof MBehavioralFeature) {
    			graph.setNamespace(context.getNamespace());
    		}
    		StateMachinesFactory.getFactory().buildCompositeState(graph);
    		return graph;
    	} else 
    		throw new IllegalArgumentException("In buildStateMachine: context null or not legal");
    }

}

