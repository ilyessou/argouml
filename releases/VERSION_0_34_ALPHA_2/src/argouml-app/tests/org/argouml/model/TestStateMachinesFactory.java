/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2002-2007 The Regents of the University of California. All
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.argouml.profile.init.InitProfileSubsystem;

/**
 * Test the StateMachinesFactory class.
 *
 */
public class TestStateMachinesFactory extends TestCase {
    private Object aClass;
    private Object aStateMachine;
    private Object aState;

    /**
     * Model elements to test.
     */
    private static String[] allModelElements =
    {
	"CallEvent",
	"ChangeEvent",
	"CompositeState",
	"Event", // abstract
	"FinalState",
	"Guard",
	"Pseudostate",
	"SignalEvent",
	"SimpleState",
	"State", // abstract
	"StateMachine",
	"StateVertex", // abstract
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
     * @return the concrete ModelElements which are testable
     */
    static List<String> getTestableModelElements() {
        List<String> c = new ArrayList<String>(Arrays.asList(allModelElements));
        c.remove("Event");
        c.remove("State");
        c.remove("StateVertex");
        return c;
    }
    
    /**
     * @return Returns the allModelElements.
     */
    static String[] getAllModelElements() {
        return allModelElements;
    }
    
    /*
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        InitializeModel.initializeDefault();
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
        CheckUMLModelHelper.createAndRelease(
                Model.getStateMachinesFactory(), getTestableModelElements());
    }
    
    /**
     * Test issue 5227: Guard not deleted with Transition.
     */
    public void testDeleteTransition()  {
        new InitProfileSubsystem().init();
        Object model =
            Model.getModelManagementFactory().createModel();
        aClass = Model.getCoreFactory().buildClass(model);
        Object returnType = null;
        Model.getCoreFactory().buildOperation2(aClass, returnType, "myOper");
        aStateMachine =
            Model.getStateMachinesFactory().buildStateMachine(aClass);
        Object top = Model.getFacade().getTop(aStateMachine);
        aState = Model.getStateMachinesFactory().buildCompositeState(top);
        Object internalTransition =
            Model.getStateMachinesFactory().buildInternalTransition(aState);
        Object guard = 
            Model.getStateMachinesFactory().buildGuard(internalTransition);
        Object effect = 
            Model.getCommonBehaviorFactory().createUninterpretedAction();
        Model.getStateMachinesHelper().setEffect(internalTransition, effect);
        Model.getUmlFactory().delete(aStateMachine);
        assertTrue("Effect action not deleted with Transition", 
                Model.getUmlFactory().isRemoved(effect));
        assertTrue("Guard not deleted with Transition", 
                Model.getUmlFactory().isRemoved(guard));
    }

}
