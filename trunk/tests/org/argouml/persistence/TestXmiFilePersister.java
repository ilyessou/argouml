// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.persistence;

import java.io.File;

import junit.framework.TestCase;
import org.argouml.model.InitializeModel;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.profile.internal.InitProfileSubsystem;

/**
 * Testclass for the XMIReader. Placeholder for all saving/loading tests
 * concerning XMIReader (like the dreaded ClassCastException issues).
 *
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 17, 2003
 */
public class TestXmiFilePersister extends TestCase {

    /**
     * Constructor for TestXMIReader.
     * @param arg0 is the name of the test case.
     */
    public TestXmiFilePersister(String arg0) {
        super(arg0);
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
	super.setUp();
        InitializeModel.initializeDefault();
        new InitProfileSubsystem().init();
    }

    /**
     * This is a regression test for issue 1504.
     * Test basic serialization to XMI file.
     * 
     * @throws Exception if saving fails.
     */
    public void testSave() throws Exception {
        Project p = ProjectManager.getManager().makeEmptyProject();
        Object clazz = Model.getCoreFactory().buildClass(p.getModel());
        Object returnType =
            ProjectManager.getManager()
            	.getCurrentProject().getDefaultReturnType();
        Object oper =
            Model.getCoreFactory().buildOperation(clazz, returnType);
        Model.getCoreHelper().setType(
                Model.getFacade().getParameter(oper, 0),
                p.findType("String"));
        File file = new File("test.xmi");
        XmiFilePersister persister = new XmiFilePersister();
        p.preSave();
        persister.save(p, file);
        p.postSave();
    }
    
    /**
     * This is more like a functional test, exercising several sub-systems 
     * of ArgoUML, including persistence, kernel and model.
     * It is composed of the following steps:
     * <ol>
     * <li>create a model with a class in it, then assert that the class is 
     * found in the project;</li>
     * <li>save the model as an XMI file;</li>
     * <li>load the model and create a project around it, then assert that 
     * the class is found again.</li>
     * </ol>
     * 
     * @throws Exception when any of the activities fails
     */
    public void testCreateSaveAndLoadYeldsCorrectModel() throws Exception {
        Project project = ProjectManager.getManager().makeEmptyProject();
        Object model = project.getModel();
        assertNotNull(model);
        Model.getCoreFactory().buildClass("Foo", model);
        assertNotNull(project.findType("Foo", false));
        File file = new File("testCreateSaveAndLoadYeldsCorrectModel.xmi");
        XmiFilePersister persister = new XmiFilePersister();
        project.preSave();
        persister.save(project, file);
        project.postSave();

        ProjectManager.getManager().makeEmptyProject();
        
        persister = new XmiFilePersister();
        project = persister.doLoad(file);
        assertNotNull(project.findType("Foo", false));
    }

    /**
     * This is a regression test for issue 1504.
     * Test loading from minimal XMI file.
     * 
     * @throws Exception if loading project fails
     */
    public void testLoadProject() throws Exception {
        File file = new File("test.xmi");

        XmiFilePersister persister = new XmiFilePersister();

        ProjectManager.getManager().makeEmptyProject();

        persister.doLoad(file);
    }
}
