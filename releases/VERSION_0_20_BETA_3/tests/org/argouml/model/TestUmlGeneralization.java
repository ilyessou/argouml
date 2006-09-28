// $Id$
// Copyright (c) 2003-2005 The Regents of the University of California. All
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

import java.util.Collection;



/**
 * Test to check various aspects of generalization relationships
 * 
 * @author Markus Klink
 */
public class TestUmlGeneralization extends GenericUmlObjectTestFixture {
    
    private Object class1, class2, class3;
    private Object gen1, gen2;
    private Object namespace;

    /**
     * Constructor.
     * 
     * @param arg0
     *            test name
     */
    public TestUmlGeneralization(String arg0) {
	super(arg0, Model.getMetaTypes().getClass());
	validateTestClassIsGeneric(this);
    }

    
    public void testClasshasGeneralizations() {
        Collection gen = Model.getFacade().getGeneralizations(class2);
        assertNotNull(gen);
        assertTrue(gen.size()==1);
        assertTrue(gen.contains(gen2));
    }
    
   
    public void testClasshasSpecializations() {
        Collection gen = Model.getFacade().getSpecializations(class2);
        assertNotNull(gen);
        assertTrue(gen.size()==1);
        assertTrue(gen.contains(gen1));
    }
    
    public void testDeleteClass() {
        Model.getUmlFactory().delete(class2);
        Collection gen1 = Model.getFacade().getGeneralizations(class1);
        Collection spec1 = Model.getFacade().getSpecializations(class1);
        assertTrue(gen1.isEmpty());
        assertTrue(spec1.isEmpty());
        Collection gen2 = Model.getFacade().getGeneralizations(class3);
        Collection spec2 = Model.getFacade().getSpecializations(class3);
        assertTrue(gen2.isEmpty());
        assertTrue(spec2.isEmpty());
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Object mmodel =
            Model.getModelManagementFactory().createModel();
        Model.getCoreHelper().setName(mmodel, "untitledModel");
        Model.getModelManagementFactory().setRootModel(mmodel);
        namespace = Model.getModelManagementFactory().createPackage();
        class1 = Model.getCoreFactory().buildClass("Class3", namespace);
        class2 = Model.getCoreFactory().buildClass("Class2", namespace);
        class3 = Model.getCoreFactory().buildClass("Class3", namespace);
        
        gen1 =
            Model.getCoreFactory().buildGeneralization(class1, class2);
        gen2 =
            Model.getCoreFactory().buildGeneralization(class2, class3);
        
    }

}
