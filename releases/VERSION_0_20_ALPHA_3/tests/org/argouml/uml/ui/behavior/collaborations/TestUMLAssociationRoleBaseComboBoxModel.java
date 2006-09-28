// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.collaborations;

import junit.framework.TestCase;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetEvent;

/**
 * @since Oct 30, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class TestUMLAssociationRoleBaseComboBoxModel extends TestCase {

    private Object elem;
    private UMLAssociationRoleBaseComboBoxModel model;
    private Object[] bases;

    /**
     * Constructor for TestUMLAssociationRoleBaseComboBoxModel.
     *
     * @param arg0 is the name of the test case.
     */
    public TestUMLAssociationRoleBaseComboBoxModel(String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Project p = ProjectManager.getManager().getCurrentProject();
        model = new UMLAssociationRoleBaseComboBoxModel();
        Object class1 = Model.getCoreFactory().createClass();
        Object class2 = Model.getCoreFactory().createClass();
        Object m = Model.getModelManagementFactory().createModel();
        p.setRoot(m);
        Model.getCoreHelper().setNamespace(class1, m);
        Model.getCoreHelper().setNamespace(class2, m);
        bases = new Object[10];
        for (int i = 0; i < 10; i++) {
            bases[i] =
		Model.getCoreFactory().buildAssociation(class1, class2);
        }
        Object role1 =
	    Model.getCollaborationsFactory().createClassifierRole();
        Object role2 =
	    Model.getCollaborationsFactory().createClassifierRole();
        Model.getCollaborationsHelper().addBase(role1, class1);
        Model.getCollaborationsHelper().addBase(role2, class2);
        Object col =
	    Model.getCollaborationsFactory().createCollaboration();
        Model.getCoreHelper().setNamespace(role1, col);
        Model.getCoreHelper().setNamespace(role2, col);
        elem =
	    Model.getCollaborationsFactory().buildAssociationRole(role1, role2);
        model.targetSet(new TargetEvent(this,
					"set",
					new Object[0],
					new Object[] {
					    elem,
					}));
        Model.getPump().flushModelEvents();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Model.getUmlFactory().delete(elem);
        model = null;
    }

    /**
     * Test setup.
     */
    public void testSetUp() {
        // there is one extra element due to the empty element that
        // the user can select
        assertEquals(10 + 1, model.getSize());
        assertTrue(model.contains(bases[5]));
        assertTrue(model.contains(bases[0]));
        assertTrue(model.contains(bases[9]));
    }

    /**
     * Test setting the Base.
     */
    public void testSetBase() {
        Model.getCollaborationsHelper().setBase(elem, bases[0]);
        Model.getPump().flushModelEvents();
        assertTrue(model.getSelectedItem() == bases[0]);
    }

    /**
     * Test setting the Base to null.
     */
    public void testSetBaseToNull() {
        Model.getCollaborationsHelper().setBase(elem, bases[0]);
        Model.getCollaborationsHelper().setBase(elem, null);
        Model.getPump().flushModelEvents();
        assertNull(model.getSelectedItem());
    }

    /**
     * Test removing the Base.
     */
    public void testRemoveBase() {
        Model.getUmlFactory().delete(bases[9]);
        // there is one extra element since removal of the base is allowed.
        Model.getPump().flushModelEvents();
        assertEquals(9 + 1, model.getSize());
        assertTrue(!model.contains(bases[9]));
    }

}
