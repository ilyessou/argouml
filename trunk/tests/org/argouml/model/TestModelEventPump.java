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

package org.argouml.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import junit.framework.TestCase;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;

/**
 * This test case is initially very much a copy of the applicable parts
 * of the TestUmlModelEventPump test case.
 *
 * TODO: We have not documented exactly what events arrive and when.
 *
 * @author Linus Tolke
 */
public class TestModelEventPump extends TestCase {
    private Object elem;
    private boolean eventcalled;
    private TestListener listener;

    /**
     * A mock listener used for the tests.
     */
    private class TestListener implements PropertyChangeListener {
        /**
         * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent e) {
            // TODO: This log entry tells us what events actually arrive
            // to the listener. I (Linus) suggest we let it in there until
            // all event types and contents are fully documented.
            System.out.println("Received event " + e.getSource()
                    	       + "[" + e.getSource().getClass() + "], "
                    	       + e.getPropertyName() + ", "
                    	       + e.getOldValue() + ", "
                    	       + e.getNewValue());
            eventcalled = true;
        }
    }

    /**
     * Constructor for TestUmlModelEventPump.
     *
     * @param arg0 is the name of the test case.
     */
    public TestModelEventPump(String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        elem = Model.getCoreFactory().createClass();
        Project project = ProjectManager.getManager().getCurrentProject();
        Object model = project.getRoot();
        Model.getCoreHelper().addOwnedElement(model, elem);
        eventcalled = false;
        listener = new TestListener();
        Model.getPump().flushModelEvents();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (elem != null) {
            Model.getUmlFactory().delete(elem);
        }
        listener = null;
    }


    /**
     * Tests if the association from a modelelement to the pump is thrown away
     * after deletion of the element.
     */
    public void testCreateDelete() {
        WeakReference ref = new WeakReference(elem);
        Model.getUmlFactory().delete(elem);
        Model.getPump().flushModelEvents();
        elem = null;
        System.gc();
        assertNull(ref.get());
    }

    /**
     * Tests if a listener that registred for a PropertySet event on
     * the class level really receive the event.
     */
    public void testPropertySetClass() {
        Model.getPump().addClassModelEventListener(listener,
                				   elem.getClass(),
                				   new String[] {
						       "isRoot",
        					   });
        Model.getCoreHelper().setRoot(elem, true);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a Removed event on the
     * class level really receive the event.
     */
    public void testRemovedClass() {
        Model.getPump().addClassModelEventListener(listener,
                				   elem.getClass(),
                				   new String[] {
						       "remove",
        					   });
        Model.getUmlFactory().delete(elem);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a RoleAdded event on the
     * class level really receive the event.
     */
    public void testRoleAddedSetClass() {
        Model.getPump().addClassModelEventListener(listener,
                				   elem.getClass(),
                				   new String[] {
						       "parameter",
        					   });
        Model.getCoreHelper().addParameter(
                elem,
                Model.getCoreFactory().createParameter());
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a RoleRemoved event on
     * the class level really receive the event.
     */
    public void testRoleRemovedSetClass() {
        Object param = Model.getCoreFactory().createParameter();
        Model.getCoreHelper().addParameter(elem, param);
        Model.getPump().addClassModelEventListener(listener,
                				   elem.getClass(),
                				   new String[] {
						       "parameter",
        					   });
        Model.getCoreHelper().removeParameter(elem, param);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a non registred listener does not receive any events.
     */
    public void testFireNonRegistredListener() {
        Model.getCoreFactory().createClass();
        Model.getCoreHelper().addParameter(
                elem,
                Model.getCoreFactory().createParameter());
        Model.getPump().flushModelEvents();
        assertTrue(!eventcalled);
    }

    /**
     * Tests if a listener that registred for a PropertySet event really
     * receive the event.
     */
    public void testPropertySet() {
        Model.getPump().addModelEventListener(listener,
                			      elem,
                			      new String[] {
                				  "isRoot",
        				      });
        Model.getCoreHelper().setRoot(elem, true);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a Removed event really
     * receive the event.
     */
    public void testRemoved() {
        Model.getPump().addModelEventListener(listener,
                			      elem,
                			      new String[] {
				       		  "remove",
				   	      });
        Model.getUmlFactory().delete(elem);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a RoleAddedSet event really
     * receive the event.
     */
    public void testRoleAddedSet() {
        Model.getPump().addModelEventListener(listener,
                			      elem,
                			      new String[] {
                				  "parameter",
        				      });
        Model.getCoreHelper().addParameter(
                elem,
                Model.getCoreFactory().createParameter());
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener that registred for a RoleRemovedSet event really
     * receive the event.
     */
    public void testRoleRemovedSet() {
        Object param = Model.getCoreFactory().createParameter();
        Model.getCoreHelper().addParameter(elem, param);
        Model.getPump().addModelEventListener(listener,
                			      elem,
                			      new String[] {
                				  "parameter",
        				      });
        Model.getCoreHelper().removeParameter(elem, param);
        Model.getPump().flushModelEvents();
        assertTrue(eventcalled);
    }

    /**
     * Tests if a listener to a class that is legally added and then
     * removed, really is removed.
     */
    public void testRemoveLegalClassListener() {
        Model.getPump().addClassModelEventListener(listener,
                				   elem.getClass(),
                				   new String[] {
						       "isRoot",
        					   });
        Model.getPump().removeClassModelEventListener(listener,
                				      elem.getClass(),
                				      new String[] {
					       		  "isRoot",
        					      });
        Model.getCoreHelper().addParameter(
                elem,
                Model.getCoreFactory().createParameter());
        Model.getPump().flushModelEvents();
        assertTrue(!eventcalled);
    }

    /**
     * Tests if a listener that is legally added and then removed,
     * really is removed.
     */
    public void testRemoveLegalListener() {
        String[] map = new String[] {
            "isRoot",
        };
        Model.getPump().addModelEventListener(listener, elem, map);
        Model.getPump().removeModelEventListener(listener, elem, map);
        Model.getCoreHelper().addParameter(
                elem,
                Model.getCoreFactory().createParameter());
        Model.getPump().flushModelEvents();
        assertTrue(!eventcalled);
    }
}
