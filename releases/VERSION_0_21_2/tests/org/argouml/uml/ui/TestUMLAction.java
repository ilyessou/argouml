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

package org.argouml.uml.ui;

import junit.framework.TestCase;

/**
 * Test the UMLAction class.
 *
 */
public class TestUMLAction extends TestCase {
    /**
     * The constructor.
     *
     * @param name the test name
     */
    public TestUMLAction(String name) {
	super(name);
    }

    /**
     * Testing constructor.
     */
    public void testCreate1() {
	UMLAction to = new UMLAction(new String("hejsan"), true);
	assertTrue("Disabled", to.shouldBeEnabled());
    }

    /**
     * Testing constructor.
     */
    public void testCreate2() {
	UMLAction to = new UMLAction(new String("hejsan"), true);
	assertTrue("Disabled", to.shouldBeEnabled());
    }

    /**
     * Testing constructor.
     */
    public void testCreate3() {
	UMLAction to = new UMLAction(new String("hejsan"), UMLAction.NO_ICON);
	assertTrue("Disabled", to.shouldBeEnabled());
    }

    /**
     * Function never actually called. Provided in order to make
     * sure that the static interface has not changed.
     */
    private void compileTestStatics() {
	UMLAction.getMnemonic(new String());
    }

    /**
     * Test constructors.
     */
    private void compileTestConstructors() {
	new UMLAction(new String(), true);
    }

    /**
     * Test methods.
     */
    private void compileTestMethods() {
	UMLAction to = new UMLAction(new String(), true);

	// to.actionPerformed(new ActionEvent());
	// to.markNeedsSave();
	to.updateEnabled(new Object());
	to.updateEnabled();
	to.shouldBeEnabled();
    }
}
