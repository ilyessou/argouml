// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
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

package org.argouml.model.uml;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Run all tests in this package.
 */
public final class AllTests {
    /**
     * Constructor.
     */
    private AllTests() {
    }

    /**
     * Get the list.
     *
     * @return a list of all test cases.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.argouml.model.uml");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestActivityGraphsFactory.class);
        suite.addTestSuite(TestCommonBehaviorHelper.class);
        suite.addTestSuite(TestModelManagementHelper.class);
        suite.addTestSuite(TestUmlModel.class);
        suite.addTestSuite(GUITestCopyHelper.class);
        suite.addTestSuite(TestActivityGraphsHelper.class);
        suite.addTestSuite(TestCollaborationsHelper.class);
        suite.addTestSuite(TestUmlActor.class);
        suite.addTestSuite(TestUml.class);
        suite.addTestSuite(TestUmlUseCase.class);
        suite.addTestSuite(TestUmlNamespace.class);
        suite.addTestSuite(TestUseCasesFactory.class);
        suite.addTestSuite(TestCopyHelper.class);
        suite.addTestSuite(TestModelManagementFactory.class);
        suite.addTestSuite(TestUmlModelEventPumpDeprecated.class);
        suite.addTestSuite(TestExtensionMechanismsHelper.class);
        suite.addTestSuite(TestExtensionMechanismsFactory.class);
        suite.addTestSuite(TestCoreHelper.class);
        suite.addTestSuite(TestUmlFactory.class);
        suite.addTest(TestAgainstUmlModel.suite());
        suite.addTestSuite(TestStateMachinesFactory.class);
        suite.addTestSuite(TestUmlModelElement.class);
        suite.addTestSuite(TestCoreFactory.class);
        suite.addTestSuite(TestCollaborationsFactory.class);
        suite.addTestSuite(TestStateMachinesHelper.class);
        suite.addTestSuite(TestDataTypesFactory.class);
        suite.addTestSuite(TestCommonBehaviorFactory.class);
        suite.addTestSuite(TestUseCasesHelper.class);
        //$JUnit-END$
        return suite;
    }
}

