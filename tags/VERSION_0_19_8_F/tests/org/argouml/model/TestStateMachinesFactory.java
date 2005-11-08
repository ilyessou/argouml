// $Id$
// Copyright (c) 2002-2005 The Regents of the University of California. All
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

package org.argouml.model;

import junit.framework.TestCase;

/**
 * Test the StateMachinesFactory class.
 *
 */
public class TestStateMachinesFactory extends TestCase {
    /**
     * Model elements to test.
     */
    private static String[] allModelElements =
    {
	"CallEvent",
	"ChangeEvent",
	"CompositeState",
	"Event",
	"FinalState",
	"Guard",
	"Pseudostate",
	"SignalEvent",
	"SimpleState",
	"State",
	"StateMachine",
	"StateVertex",
	"StubState",
	"SubmachineState",
	"SynchState",
	"TimeEvent",
	"Transition",
    };

    /**
     * The constructor.
     *
     * @param n the name of the test
     */
    public TestStateMachinesFactory(String n) {
	super(n);
    }

    /**
     * Test if this class is really a singleton.
     */
    public void testSingleton() {

	Object o1 = Model.getStateMachinesFactory();

	Object o2 = Model.getStateMachinesFactory();

	assertTrue("Different singletons", o1 == o2);

    }

    /**
     * Test creation.
     */
    public void testCreates() {
	// Do not test State, Event or StateVertex. They are abstract.
	String[] objs = {
	    "CallEvent", 
            "ChangeEvent", 
            "CompositeState",
	    // "Event",
	    "FinalState",
	    "Guard",
	    "Pseudostate",
	    "SignalEvent",
	    "SimpleState",
	    // "State",
	    "StateMachine",
	    // "StateVertex",
	    "StubState",
	    "SubmachineState",
	    "SynchState",
	    "TimeEvent",
	    "Transition",
	    null,
	};

	CheckUMLModelHelper.createAndRelease(
					     Model.getStateMachinesFactory(),
					     objs);
    }

    /**
     * @return Returns the allModelElements.
     */
    static String[] getAllModelElements() {
        return allModelElements;
    }
}
