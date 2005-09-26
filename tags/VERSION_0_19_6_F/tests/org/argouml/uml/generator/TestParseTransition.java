// $Id$
// Copyright (c) 2004-2005 The Regents of the University of California. All
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

package org.argouml.uml.generator;

import java.text.ParseException;
import java.util.Collection;

import junit.framework.TestCase;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;

/**
 * Test ParserDisplay: parsing transitions.
 *
 * @author MVW
 *
 */
public class TestParseTransition extends TestCase {
    private Object aClass;
    private Object aOper;
    private Object aStateMachine;
    private Object aState;

    /**
     * The constructor.
     *
     * @param str the name
     */
    public TestParseTransition(String str) {
        super(str);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        Project p = ProjectManager.getManager().getCurrentProject();
        Object model =
            Model.getModelManagementFactory().createModel();
        aClass = Model.getCoreFactory().buildClass(model);
        Collection propertyChangeListeners = p.findFigsForMember(aClass);
        Object mdl = p.getModel();
        Object voidType = p.findType("void");
        aOper = Model.getCoreFactory().buildOperation(aClass, mdl,
                voidType, "myOper", propertyChangeListeners);
        aStateMachine = Model.getStateMachinesFactory()
            .buildStateMachine(aClass);
        Object top = Model.getFacade().getTop(aStateMachine);
        aState = Model.getStateMachinesFactory()
            .buildCompositeState(top);
    }


    /**
     * Test for the parseTransition() method.
     * These should NOT generate a ParseException.
     */
    public void testParseTransitionCreate() {
        checkGenerated(aState, "trigger[guard]/effect", true, true, true,
                false);
        checkGenerated(aState, "trigger[]/effect", true, false, true, false);
        checkGenerated(aState, " tri gg er ( w ) [ ] / e ffect ", true, false,
                true, false);
        checkGenerated(aState, "trigger / effect", true, false, true, false);
        checkGenerated(aState, "/effect", false, false, true, false);
        checkGenerated(aState, "trigger", true, false, false, false);
        checkGenerated(aState, "t(a:int=3, b=4.0:double)", true, false, false,
                false);
        checkGenerated(aState, "", false, false, false, false);
        checkGenerated(aState, "[]/", false, false, false, false);
        checkGenerated(aState, "[ guard ]", false, true, false, false);
        checkGenerated(aState, "trigger()/", true, false, false, false);
    }

    /**
     * Test for the parseTransition() method.
     * These should generate a ParseException.
     */
    public void testParseTransitionNotCreate() {
        checkGenerated(aState, "trigger[guard/effect", false, false, false,
                true);
        checkGenerated(aState, "trigger(", false, false, false, true);
        checkGenerated(aState, "trigger)/eff()", false, false, false, true);
        checkGenerated(aState, "trigger(/)", false, false, false, true);
        checkGenerated(aState, "tr]jhgf[ijh", false, false, false, true);
        checkGenerated(aState, "tr]/e", false, false, false, true);
        checkGenerated(aState, "tr/i[gg]er", false, false, false, true);
        checkGenerated(aState, "tri[g/g]er", false, false, false, true);
    }

    /**
     * Check if the transition parts are generated or not. <p>
     *
     * An internal transition is chosen to test these cases,
     * instead of a transition between states. Reason: simplicity.
     *
     * @param st the state in which to create an internal transition
     * @param text the text to be parsed
     * @param trigger true if we expect a trigger to be generated
     * @param guard true if we expect a guard to be generated
     * @param effect true if we expect an effect to be generated
     * @param exception true if we expect an exception to be trown
     * @return the generated internal transition
     */
    private Object checkGenerated(Object st, String text, boolean trigger,
            boolean guard, boolean effect, boolean exception) {
        Object it = Model.getStateMachinesFactory()
            .buildInternalTransition(st);
        try {
            ParserDisplay.SINGLETON.parseTransition(it, text);
        } catch (ParseException e) {
            assertTrue("Unexpected exception for " + text, exception);
        }
        if (trigger) {
            assertTrue("Trigger was not generated for " + text,
                    Model.getFacade().getTrigger(it) != null);
        } else {
            assertTrue("Trigger was generated for " + text,
                    Model.getFacade().getTrigger(it) == null);
        }
        if (guard) {
            assertTrue("Guard was not generated for " + text,
                    Model.getFacade().getGuard(it) != null);
        } else {
            assertTrue("Guard was generated for " + text,
                    Model.getFacade().getGuard(it) == null);
        }
        if (effect) {
            assertTrue("Effect (action) was not generated for " + text,
                    Model.getFacade().getEffect(it) != null);
        } else {
            assertTrue("Effect (action) was generated for " + text,
                    Model.getFacade().getEffect(it) == null);
        }
        return it;
    }

    /**
     * Test for the parseTrigger() method: TimeEvent.
     * Also changing triggertype is tested.
     */
    public void testParseTriggerTimeEvent() {
        Object trans;
        Object trig;
        String text;
        Object expr;

        //try creating a TimeEvent
        text = "after(a while)";
        trans = checkGenerated(aState, text, true, false, false,
                false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of TimeEvent for "
                + text, Model.getFacade().isATimeEvent(trig));
        expr = Model.getFacade().getExpression(trig);
        assertTrue("Incorrectly set TimeExpression for" + text,
                Model.getFacade().getBody(expr).equals("a while"));


        //try changing the triggertype to ChangeEvent
        text = "when(it happens)";
        try {
            ParserDisplay.SINGLETON.parseTransition(trans, text);
        } catch (ParseException e) {
            assertTrue("Unexpected exception for " + text, true);
        }
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of ChangeEvent",
                Model.getFacade().isAChangeEvent(trig));
    }

    /**
     * Test for the parseTrigger() method: ChangeEvent.
     * Testing creation and deletion.
     */
    public void testParseTriggerChangeEvent() {
        Object trans;
        Object trig;
        String text;
        Object expr;

        text = "when(it changed)/effect";
        trans = checkGenerated(aState, text, true, false, true, false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of ChangeEvent for "
                + text, Model.getFacade().isAChangeEvent(trig));
        expr = Model.getFacade().getExpression(trig);
        assertTrue("Incorrectly set ChangeExpression for" + text,
                Model.getFacade().getBody(expr).equals("it changed"));

        text = "/effect";
        try {
            ParserDisplay.SINGLETON.parseTransition(trans, text);
        } catch (ParseException e) {
            assertTrue("Unexpected exception when removing ChangeEvent trigger",
                    true);
        }
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Trigger not deleted", trig != null);
    }

    /**
     * Test for the parseTrigger() method: CallEvent.
     */
    public void testParseTriggerCallEvent() {
        Object trans;
        Object trig;
        String text;

        text = "call(a method)";
        trans = checkGenerated(aState, text, true, false, false, false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of CallEvent for "
                + text, Model.getFacade().isACallEvent(trig));

        text = "call()";
        trans = checkGenerated(aState, text, true, false, false, false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of CallEvent for "
                + text, Model.getFacade().isACallEvent(trig));
    }

    /**
     * Test for the parseTrigger() method: SignalEvent.
     */
    public void testParseTriggerSignalEvent() {
        Object trans;
        Object trig;
        String text;

        text = "signal";
        trans = checkGenerated(aState, text, true, false, false, false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of SignalEvent",
                Model.getFacade().isASignalEvent(trig));

        text = "/effect";
        try {
            ParserDisplay.SINGLETON.parseTransition(trans, text);
        } catch (ParseException e) {
            assertTrue("Unexpected exception when removing SignalEvent trigger",
                    true);
        }
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Trigger not deleted", trig != null);
    }

    /**
     * Test for the parseTrigger() method:
     * linking of an Operation for a CallEvent.
     */
    public void testParseTriggerCallEventOperation() {
        Object trans;
        Object trig;
        String text;
        Object myOp;

        text = "myOper()"; // this is an existing operation of the class!
        trans = checkGenerated(aState, text, true, false, false, false);
        trig = Model.getFacade().getTrigger(trans);
        assertTrue("Unexpected triggertype found instead of CallEvent for "
                + text, Model.getFacade().isACallEvent(trig));
        myOp = Model.getFacade().getOperation(trig);
        assertTrue("Operation of CallEvent not linked", myOp != null);
    }

}
