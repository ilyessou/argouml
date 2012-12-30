/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
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

// Copyright (c) 2006-2007 The Regents of the University of California. All
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

package org.argouml.model;

import junit.framework.TestCase;

import org.easymock.MockControl;

/**
 * This is an extremely simple example for testing with a mock model.
 * The purpose of this is to show how to set up the Mock Model correctly.
 *
 * @author Linus Tolke
 */
public class TestAGHelperWithMock extends TestCase {
    private MockModelImplementation mockModelImplementation;
    private MockControl controlAGH;

    /**
     * Constructor.
     *
     * @param arg0 The name of the test case.
     */
    public TestAGHelperWithMock(String arg0) {
        super(arg0);
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        mockModelImplementation = InitializeModel.initializeMock();

        assertNotNull("MockModelImplementation not created",
                mockModelImplementation);

        // Must be called after setImplementation.
        mockModelImplementation.reset();
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() {
        mockModelImplementation.verify();
    }

    /**
     * Testing ActivityGraphsHelper.
     */
    public void testAGHelper() {
        mockModelImplementation.getActivityGraphsHelper()
            .isAddingActivityGraphAllowed(null);
        mockModelImplementation.getActivityGraphsHelperControl()
            .setReturnValue(false);
        mockModelImplementation.replay();

        Model.getActivityGraphsHelper().isAddingActivityGraphAllowed(null);
    }
}
