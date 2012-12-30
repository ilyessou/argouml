/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    linus
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2004-2007 The Regents of the University of California. All
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


package org.argouml.uml.cognitive.critics;

import junit.framework.TestCase;

import org.argouml.model.InitializeModel;
import org.argouml.model.Model;

/**
 * Testing the class {@link CrUnconventionalPackName}.
 *
 * @author mkl
 */
public class TestCrUnconventionalPackName extends TestCase {
    /**
     * An instance of the class to test.
     */
    private CrUnconventionalPackName cr = new CrUnconventionalPackName();

    /**
     * The constructor.
     *
     * @param arg0 The test case name.
     */
    public TestCrUnconventionalPackName(String arg0) {
        super(arg0);
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
	super.setUp();
        InitializeModel.initializeDefault();
    }

    /**
     * Testing computeSuggestion.
     */
    public void testComputeSuggestion() {
        assertEquals("packageName", cr.computeSuggestion(null));
        assertEquals("x", cr.computeSuggestion("X"));
    }
    
    public void testPredicate2() {
        Object me = Model.getModelManagementFactory().createPackage();
        Model.getCoreHelper().setName(me, null);
        assertFalse(cr.predicate2(me, null));
        
        Model.getCoreHelper().setName(me, "Uppercase");
        assertTrue(cr.predicate2(me, null));
        
        Model.getCoreHelper().setName(me, "lowercase");
        assertFalse(cr.predicate2(me, null));
    }
}
