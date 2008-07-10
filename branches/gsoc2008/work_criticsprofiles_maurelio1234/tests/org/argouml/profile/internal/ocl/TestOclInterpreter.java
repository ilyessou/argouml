// $Id: TestProfileJava.java 13911 2007-12-13 00:09:36Z euluis $
// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.profile.internal.ocl;

import java.util.HashMap;

import junit.framework.TestCase;

import org.argouml.model.InitializeModel;
import org.argouml.model.Model;

/**
 * Tests for the ProfileJava class.
 * 
 * @author Luis Sergio Oliveira (euluis)
 */
public class TestOclInterpreter extends TestCase {

    private class DefaultModelInterpreter implements ModelInterpreter {
        public Object invokeFeature(HashMap<String, Object> vt, Object subject,
                String feature, String type, Object[] parameters) {
            return null;
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InitializeModel.initializeDefault();
    }

    /**
     * Test <code>applicable</code> operation
     * 
     * @throws Exception
     */
    public void testApplicable() throws Exception {
        Object obj1 = Model.getUseCasesFactory().createActor();
        Object obj2 = Model.getActivityGraphsFactory().createPartition();

        String ocl = "context Actor inv: 2 > 0";

        OclInterpreter interpreter = new OclInterpreter(ocl,
                new DefaultModelInterpreter());
        
        assertTrue(interpreter.applicable(obj1));
        assertFalse(interpreter.applicable(obj2));
    }
    
    /**
     * Test <code>getTriggers</code> operation
     * 
     * @throws Exception
     */
    public void testGetTriggers() throws Exception {
        String ocl = "context Actor inv: 2 > 0";

        OclInterpreter interpreter = new OclInterpreter(ocl,
                new DefaultModelInterpreter());
        
        assertTrue(interpreter.getTriggers().contains("actor"));
    }

    /**
     * Test <code>check</code> operation
     * 
     * @throws Exception
     */
    public void testCheck() throws Exception {
        Object obj = Model.getUseCasesFactory().createActor();

        String ocl1 = "context Actor inv: 2 > 0";
        String ocl2 = "context Actor inv: 2 < 0";

        OclInterpreter interpreter1 = new OclInterpreter(ocl1,
                new DefaultModelInterpreter());
        OclInterpreter interpreter2 = new OclInterpreter(ocl2,
                new DefaultModelInterpreter());
        
        assertTrue(interpreter1.check(obj));
        assertFalse(interpreter2.check(obj));
    }

}
