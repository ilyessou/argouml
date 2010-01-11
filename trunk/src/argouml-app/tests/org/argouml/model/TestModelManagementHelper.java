/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
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

package org.argouml.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

/**
 * @since Oct 10, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class TestModelManagementHelper extends TestCase {

    private Object theModel;
    private Object theGoodPackage;
    private Object theBadPackage;
    private Object theStereotype;
    private Object theTagDefinition;
    private Object theClass;
    private List<String> tdPath = new ArrayList<String>();

    /**
     * Constructor for TestModelManagementHelper.
     *
     * @param arg0 is the name of the test case.
     */
    public TestModelManagementHelper(String arg0) {
	super(arg0);
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() {
        InitializeModel.initializeDefault();
    }

    /**
     * Test getting the metamodel name.
     */
    public void testGetMetaModelName() {
	CheckUMLModelHelper.metaModelNameCorrect(
			 Model.getModelManagementFactory(),
			 TestModelManagementFactory.getAllModelElements());
    }

    /**
     * Test stereotypes.
     */
    public void testIsValidStereoType() {
	CheckUMLModelHelper.isValidStereoType(
		      Model.getModelManagementFactory(),
		      TestModelManagementFactory.getAllModelElements());
    }
    
    private void setUpTestsOfTagDefinitionContainedInStereotype() {
        theModel = Model.getModelManagementFactory().createModel();
        Model.getCoreHelper().setName(theModel, "TheModel");
        Model.getModelManagementFactory().setRootModel(theModel);
        
        theGoodPackage = Model.getModelManagementFactory().createPackage();
        Model.getCoreHelper().setName(theGoodPackage, "TheGoodPackage");
        Model.getCoreHelper().setNamespace(theGoodPackage, theModel);
        
        theBadPackage = Model.getModelManagementFactory().createPackage();
        Model.getCoreHelper().setName(theBadPackage, "TheBadPackage");
        Model.getCoreHelper().setNamespace(theBadPackage, theModel);
        
        theClass = Model.getCoreFactory().buildClass("TheClass", 
                    theGoodPackage);
        theStereotype = Model.getExtensionMechanismsFactory().buildStereotype(
                theClass, "containedStereotype", theGoodPackage);
        theTagDefinition = Model.getExtensionMechanismsFactory()
                .buildTagDefinition("TagDefinition", theStereotype, null);

        tdPath.add("TheModel");
        tdPath.add("TheGoodPackage");
        tdPath.add("containedStereotype");
        tdPath.add("TagDefinition");
    }
    
    /**
     * Test getAllModelElementsOfKind() for tag definitions contained in 
     * stereotypes. Issue #3829.
     */
    public void testGetAllModelElementsOfKindForTagDefinitionInStereotype() {
        setUpTestsOfTagDefinitionContainedInStereotype();
        Model.getCoreHelper().addStereotype(theClass, theStereotype);
        assertTrue(Model.getFacade().getStereotypes(theClass).
            contains(theStereotype));
        Collection col = Model.getModelManagementHelper().
            getAllModelElementsOfKind(
                theGoodPackage, Model.getMetaTypes().getTagDefinition());
        assertTrue("Tag definition should be contained in the model!", 
            col.contains(theTagDefinition));
    }
    
    /**
     * Test getAllModelElementsOfKind() to make sure that it doesn't
     * return elements which are not in the requested namespace.
     */
    public void testGetAllModelElementsOfKindNamespaceConstraint() {
        setUpTestsOfTagDefinitionContainedInStereotype();
        Model.getCoreHelper().addStereotype(theClass, theStereotype);
        Collection elements = Model.getModelManagementHelper()
                .getAllModelElementsOfKind(theBadPackage,
                        Model.getMetaTypes().getTagDefinition());
        assertTrue("Elements not in namespace returned", elements.isEmpty());
        elements = Model.getModelManagementHelper().getAllModelElementsOfKind(
                theBadPackage, Model.getMetaTypes().getUMLClass());
        assertTrue("Elements not in namespace returned", elements.isEmpty());
        elements = Model.getModelManagementHelper().getAllModelElementsOfKind(
                theBadPackage, Model.getMetaTypes().getPackage());
        assertTrue("Elements not in namespace returned", elements.isEmpty());
    }
    
    /**
     * Test to make sure that we can get UmlClass since its name is different
     */
    public void testGetUmlClassModelElements() {
        setUpTestsOfTagDefinitionContainedInStereotype();
        Collection col = Model.getModelManagementHelper()
                .getAllModelElementsOfKind(theGoodPackage,
                        Model.getMetaTypes().getUMLClass());
        assertTrue("Failed to get UmlClass ModelElement", col
                .contains(theClass));
    }
    
    /**
     * Test the getPath method with a tag definition contained in a stereotype.
     * Also test lookup of element by path name in a series of namespaces.
     */
    public void testGetPathForTagDefinitionInStereotype() {
        setUpTestsOfTagDefinitionContainedInStereotype();

        Collection path = Model.getModelManagementHelper().getPathList(
                theTagDefinition);
        assertEquals("Wrong path returned for TagDefinition", 
                tdPath, path);
        
        // Go the other way to lookup an element by path 
        Object st =
                Model.getModelManagementHelper().getElement(
                        tdPath.subList(1, 3), theModel);
        assertEquals("Found wrong element", theStereotype, st);
    }
}
