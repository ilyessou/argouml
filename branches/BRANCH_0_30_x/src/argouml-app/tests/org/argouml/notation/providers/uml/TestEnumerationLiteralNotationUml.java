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

// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.notation.providers.uml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.InitializeModel;
import org.argouml.model.Model;
import org.argouml.notation.InitNotation;
import org.argouml.notation.NotationSettings;
import org.argouml.profile.ProfileFacade;
import org.argouml.profile.init.InitProfileSubsystem;

/**
 * Test the EnumerationLiteralNotationUml class.
 *
 * @author Michiel
 */
public class TestEnumerationLiteralNotationUml extends TestCase {

    private Object aEnumeration;
    private Object aLiteral;

    private NotationSettings npSettings;

    /**
     * Create an unnamed Enumeration, with one unnamed Literal.
     */
    @Override
    protected void setUp() {
        InitializeModel.initializeDefault();
        assertTrue("Model subsystem init failed.", Model.isInitiated());
        (new InitProfileSubsystem()).init();
        (new InitNotation()).init();
        (new InitNotationUml()).init();
        ProjectManager.getManager().setCurrentProject(
                ProjectManager.getManager().makeEmptyProject());
       
        Object model =
            Model.getModelManagementFactory().createModel();
        aEnumeration = Model.getCoreFactory().buildEnumeration("", model);
        aLiteral = Model.getCoreFactory().createEnumerationLiteral();
        Model.getCoreHelper().addLiteral(aEnumeration, 0, aLiteral);
        npSettings = new NotationSettings();
    }
    

    @Override
    protected void tearDown() throws Exception {
        ProjectManager.getManager().removeProject(
                ProjectManager.getManager().getCurrentProject());
        ProfileFacade.reset();
        super.tearDown();
    }
    
    /**
     * Test the resulting name after parsing the enumeration literal.
     * 
     * @throws ParseException if the parsing fails
     */
    public void testEnumerationLiteralName() throws ParseException {
        checkLiterals(1, 
                new String[] {""});
        checkName(aLiteral, "name", "name");
        checkName(aLiteral, "name2", "name2");
        checkName(aLiteral, "name;create second one", "name");
        checkLiterals(2, 
                new String[] {"name", "create second one"});
        checkName(aLiteral, "na me", "na me");
        checkName(aLiteral, " name ", "name");
        checkName(aLiteral, "<<>>name2", "name2");
        checkName(aLiteral, " << stereotype >> name3", "name3");
        checkName(aLiteral, " << stereotype2 >> name3", "name3");
        checkName(aLiteral, " << s1, s2, s3 >> name3", "name3");
        checkName(aLiteral, " << stereotype >> name", "name");
        checkName(aLiteral, 
                " << stereotype >> name3; << stereotype >> name4", "name3");
        checkLiterals(3, 
                new String[] {"name3", "name4", "create second one"});
        checkName(aLiteral, "name;<<s2>>nameX", "name");
        checkLiterals(4, 
                new String[] {"name", "nameX", "name4", "create second one"});
        checkName(aLiteral, "�������$", "�������$");
        checkName(aLiteral, "name;", "name");
        checkName(aLiteral, " \u00AB stereotype \u00BB name3", "name3");
        checkName(aLiteral, "name;\u00ABstereotype\u00BBname", "name");
        checkLiterals(5, 
                new String[] {"name", "name", 
                    "nameX", "name4", "create second one"});
    }
    
    /**
     * Parse a given text, and check if the given element 
     * was renamed to "name".
     * 
     * @param element an EnumerationLiteral
     * @param text the text to parse
     * @param name this should be the resulting name of the element
     * @throws ParseException if it went wrong
     */
    private void checkName(Object element, String text, String name)
        throws ParseException {

        EnumerationLiteralNotationUml eln = 
            new EnumerationLiteralNotationUml(element); 
        eln.parseEnumerationLiteralFig(
                Model.getFacade().getEnumeration(element),
                element, text);
        assertEquals(text
                + " gave wrong name: "
                + Model.getFacade().getName(element),
                name, Model.getFacade().getName(element));
    }
    
    /**
     * Check if the number of literals is equal to the given number.
     * Check if the names of the literals are equal to the ones given.
     * 
     * @param count the supposed number of literals
     * @param names the supposed names of the literals
     */
    private void checkLiterals(int count, String[] names) {
        List literals = 
            Model.getFacade().getEnumerationLiterals(aEnumeration);
        assertEquals("Unexpected number of Literals", count, 
                literals.size());
        if (count != literals.size()) {
            /* No need to check any further if we fail the 1st part. */
            return;
        }
        int i = 0;
        for (Object lit : literals) {
            String name = Model.getFacade().getName(lit);
            if (name == null) {
                name = "";
            }
            assertEquals("Unexpected Literal name", name, names[i++]);
        }
    }

    /**
     * Test if exceptions are thrown for invalid text to parse.
     */
    public void testEnumerationLiteralParseExceptions() {
        checkThrows(aLiteral, "<<name");
        checkThrows(aLiteral, "<<s1>> <<s2>>name");
        checkThrows(aLiteral, "<<name<<");
        checkThrows(aLiteral, "name1<<s1>>name2");
    }
    
    private void checkThrows(
            Object element,
            String text) {
        try {
            EnumerationLiteralNotationUml eln = 
                new EnumerationLiteralNotationUml(element); 
            eln.parseEnumerationLiteralFig(
                    Model.getFacade().getEnumeration(element),
                    element, text);
            fail("didn't throw for " + text);
        } catch (ParseException pe) {
            assertTrue(text + " threw ParseException " + pe, true);
        }

    }

    /**
     * Test the parsing of a literal's stereotype.
     * 
     * @throws ParseException if the parsing fails
     */
    public void testLiteralStereotype() throws ParseException {
        checkStereotype(aLiteral, "name", new String[] {});
        checkStereotype(aLiteral, "<<st1>>name", new String[] {"st1"});
        checkStereotype(aLiteral, "<<st2>>name", new String[] {"st1", "st2"});
        checkStereotype(aLiteral, "<<st2>>name2", new String[] {"st1", "st2"});
        checkStereotype(aLiteral, "\u00ABst2\u00BBname", 
                new String[] {"st1", "st2"});
    }
    
    /**
     * Parses the text into the literal and checks that the literal gets
     * the stereotype with the name val.
     *
     * @param literal The enumeration literal.
     * @param text The text to parse.
     * @param val The name of the stereotype(s).
     * @throws ParseException if we cannot parse the text.
     */
    private void checkStereotype(Object literal, String text, String[] val)
        throws ParseException {

        EnumerationLiteralNotationUml np = 
            new EnumerationLiteralNotationUml(literal); 
        np.parseEnumerationLiteralFig(
                Model.getFacade().getEnumeration(literal),
                literal, text);

        Collection stereos = Model.getFacade().getStereotypes(literal);
        List<String> stereoNames = new ArrayList<String>();
        for (Object stereo : stereos) {
            stereoNames.add(Model.getFacade().getName(stereo));
        }
        boolean stereosMatch = true;
        for (String v : val) {
            if (!stereoNames.contains(v)) {
                stereosMatch = false;
            }
        }
        assertTrue(
               text + " gave wrong stereotype " + stereos.toArray(),
                  val.length == stereos.size()
                  && stereosMatch);
        
        String str = np.toString(literal, 
                NotationSettings.getDefaultSettings());
        assertTrue("Empty string", str.length() > 0);
        // TODO: Test if the generated string is correct.
    }

    /**
     * Test if the Notation generates the correct text.
     * @throws ParseException when parsing goes wrong
     */
    public void testGenerateLiteral() throws ParseException {
        checkGenerate(aLiteral, "", NotationSettings.getDefaultSettings());
        checkName(aLiteral, " << s1, s2, s3 >> name3", "name3");
        checkGenerate(aLiteral, "<<s1, s2, s3>> name3", 
                NotationSettings.getDefaultSettings());
        npSettings.setUseGuillemets(true);
        checkGenerate(aLiteral, "\u00ABs1, s2, s3\u00BB name3", npSettings);
    }
    
    private void checkGenerate(Object literal, String text, 
            NotationSettings settings) {
        EnumerationLiteralNotationUml notation = 
            new EnumerationLiteralNotationUml(aLiteral); 
        assertEquals("Incorrect generation", 
                text, notation.toString(literal, settings));
    }
    
    /**
     * Test if help is correctly provided.
     */
    public void testGetHelpOperation() {
        EnumerationLiteralNotationUml notation = 
            new EnumerationLiteralNotationUml(aLiteral); 
        String help = notation.getParsingHelp();
        assertTrue("No help at all given", help.length() > 0);
        assertTrue("Parsing help not conform for translation", 
                help.startsWith("parsing."));
    }
    
    /**
     * Test if the notationProvider refuses to instantiate 
     * without showing it the right UML element.
     */
    public void testValidObjectCheck() {
        try {
            new EnumerationLiteralNotationUml(aEnumeration);
            fail("The NotationProvider did not throw for a wrong UML element.");
        } catch (IllegalArgumentException e) {
            /* Everything fine... */
        } 
    }
}
