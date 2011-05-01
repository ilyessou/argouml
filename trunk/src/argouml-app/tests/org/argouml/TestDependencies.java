/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007-2009 The Regents of the University of California. All
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

package org.argouml;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test for Dependency cycles with JDepend. <p>
 * 
 * This test will guarantee that once 
 * a package is made free of dependency-cycles, 
 * (and it has been added here,)
 * it will stay dependency-cycle free. <p>
 * 
 * It also checks that the top level package(s) are 
 * (and remain) unused by other packages. <p>
 * 
 * It also checks that low level packages do not
 * use other argouml packages.
 * 
 * @author Michiel
 */
public class TestDependencies extends TestCase {
    /**  
     * Tests that a list of packages does not contain
     * any package dependency cycles.
     *
     * @return a list of tests.
     */
    public static Test suite() {
        JDepend jdepend = new JDepend();

        boolean directoryFound = false;

        // For eclipse setup
        try {
	    jdepend.addDirectory("build-eclipse");
            directoryFound = true;
	} catch (IOException e) {
	    // Ignore if the directory does not exist.
	    // This error will throw when running from the ant setup.
	}

        // For ant setup
	try {
	    jdepend.addDirectory("build/classes");
            directoryFound = true;
	} catch (IOException e) {
	    // Ignore if the directory does not exist.
	    // This error will throw when running from the Eclipse setup.
	}
        
        // When running from maven
	try {
	    jdepend.addDirectory("target/classes");
            directoryFound = true;
	} catch (IOException e) {
	    // Ignore if the directory does not exist.
	    // This error will throw when running from the Eclipse setup.
	}
        
        if (!directoryFound) {
            System.out.println("Did not find directory with compiled classes");
        }

        jdepend.analyze();

        TestSuite suite =
            new TestSuite("Tests for dependencies using Jdepend");

        String[] clean = {
            "org.argouml.application.api",
            "org.argouml.application.events",
            "org.argouml.application.helpers",
            "org.argouml.application.security",
            "org.argouml.cognitive.checklist",
            //"org.argouml.cognitive.critics",
            "org.argouml.configuration",
            "org.argouml.i18n",
            "org.argouml.gefext",
            "org.argouml.language.ui",
            "org.argouml.moduleloader",
            //"org.argouml.notation.providers",//fails because sub-packages fail (?)
            //"org.argouml.notation.providers.java",
            //"org.argouml.notation.providers.uml",
            //"org.argouml.notation",//fails because sub-packages fail (?)
            //"org.argouml.notation.ui",
            "org.argouml.swingext",
            "org.argouml.taskmgmt",
            "org.argouml.uml.diagram.layout",
            "org.argouml.uml.generator",
            "org.argouml.uml.util.namespace",
            "org.argouml.util.logging",
            "org.argouml.util.osdep",
            "org.argouml.util",
            // There was a comment saying that the below has no cycles, but
            // Classycle thinks there's a cycle here too, so I believe there
            // really is one - tfm 20070702
//            "org.argouml.uml.cognitive.critics",
        };

        suite.addTest(new TimeStamp());
        for (int i = 0; i < clean.length; i++) {
            suite.addTest(new CheckDependencyCycle(jdepend, clean[i]));
        }

        String[] top = {
            "org.argouml.application",
        };
        for (int i = 0; i < top.length; i++) {
            suite.addTest(new CheckTopLevel(jdepend, top[i]));
        }

        String[] low = {
            "org.argouml.application.security",
            "org.argouml.configuration",
            "org.argouml.i18n",
            "org.argouml.swingext",
            "org.argouml.taskmgmt",
            "org.argouml.util.logging",
            "org.argouml.util.osdep",
        };
        for (int i = 0; i < low.length; i++) {
            suite.addTest(new CheckLowLevel(jdepend, low[i]));
        }

        String[][] dep = {
            // There shall not be a dependency from ... to ...
            {"org.argouml.persistence", "org.argouml.ui"},
            {"org.argouml.moduleloader", "org.argouml.persistence"},
            {"org.argouml.notation", "org.argouml.notation.ui"},
            {"org.argouml.ui.targetmanager", "org.argouml.ui"},
            {"org.argouml.moduleloader", "org.argouml.ui"},
            {"org.argouml.cognitive", "org.argouml.ui"},
            {"org.argouml.cognitive.critics", "org.argouml.cognitive.ui"},
            {"org.argouml.ui", "org.argouml.cognitive.critics.ui"},
//TODO:{"org.argouml.ui", "org.argouml.cognitive.ui"},//fails due to subpackages
//TODO:{"org.argouml.cognitive", "org.argouml.cognitive.critics"},
            {"org.argouml.uml.diagram", "org.argouml.ui"},
            {"org.argouml.ui", "org.argouml.notation.ui"},
            {"org.argouml.util", "org.argouml.ui.cmd"},
            //{"org.argouml.kernel", "org.argouml.uml.diagram.ui"},//why does this fail?
        };
        for (int i = 0; i < dep.length; i++) {
            suite.addTest(new CheckNoDependency(jdepend, dep[i]));
        }

        return suite;
    }
    
    static class DateUtils {
        static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss Z";

        public static String now() {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
            return sdf.format(cal.getTime());
        }

    }

    static class TimeStamp extends TestCase {

        TimeStamp() {
            super("Timestamp: " + DateUtils.now());
        }

        @SuppressWarnings("unchecked")
        public void runTest() {
            // do nothing by design
        }
    }

    static class CheckDependencyCycle extends TestCase {
        private String packageName;
        private JDepend jdepend;

        CheckDependencyCycle(JDepend jd, String name) {
            super("Check dependency cycle in " + name);
            jdepend = jd;
            packageName = name;
        }

        @SuppressWarnings("unchecked")
        public void runTest() {
            JavaPackage p = jdepend.getPackage(packageName);
            assertNotNull(p);
            if (p.containsCycle()) {
                StringBuffer msg = new StringBuffer(
                        "JDepend indicates a dependency cycle in ");
                msg.append(p.getName());
                List<JavaPackage> firstCycle = new ArrayList<JavaPackage>();
                p.collectCycle(firstCycle);
                msg.append("(" + firstCycle.size());
                msg.append(" packages in first cycle: ");
                for (JavaPackage cp : firstCycle) {
                    msg.append(cp.getName()).append(" ");
                }
                msg.append(") -- ");
                List<JavaPackage> otherCycles = new ArrayList<JavaPackage>();
                p.collectAllCycles(otherCycles);
                otherCycles.removeAll(firstCycle);
                if (!otherCycles.isEmpty()) {
                    msg.append("(" + otherCycles.size());
                    msg.append(" packages in additional cycle(s): ");
                    for (JavaPackage cp : otherCycles) {
                        msg.append(cp.getName()).append(" ");
                    }
                    msg.append(") -- ");
                }
                
//                msg.append("(" + p.getClassCount() + " classes: ");
//                Collection<JavaClass> c = p.getClasses();
//                for (JavaClass jc : c) {
//                    msg.append(jc.getName());
//                    msg.append(" ");
//                }
//                msg.append(")");
                assertTrue(msg.toString(), false);
            }
        }
    }

    static class CheckTopLevel extends TestCase {
        private String packageName;
        private JDepend jdepend;

        CheckTopLevel(JDepend jd, String name) {
            super("Check top level dependencies for " + name);
            jdepend = jd;
            packageName = name;
        }

        @SuppressWarnings("unchecked")
        public void runTest() {
            JavaPackage p = jdepend.getPackage(packageName);
            assertNotNull(p);
            Collection<JavaPackage> afferents = p.getAfferents();
            if (afferents.size() > 0) {
                StringBuffer msg = new StringBuffer("JDepend "
                    + "indicates an afferent dependency "
                    + "to a top level package: ");
                msg.append(p.getName());
                msg.append(" is used by ");
                msg.append(afferents.size());
                msg.append(" packages: ");
                for (JavaPackage jp : afferents) {
                    msg.append(jp.getName());
                    msg.append(" ");
                }
                assertTrue(msg.toString(), false);
            }
        }
    }
    
    static class CheckLowLevel extends TestCase {
        private String packageName;
        private JDepend jdepend;

        CheckLowLevel(JDepend jd, String name) {
            super("Check low level dependencies for " + name);
            jdepend = jd;
            packageName = name;
        }

        @SuppressWarnings("unchecked")
        public void runTest() {
            JavaPackage p = jdepend.getPackage(packageName);
            assertNotNull(p);
            Collection<JavaPackage> efferents = p.getEfferents();
            Collection<JavaPackage> wrong = new ArrayList<JavaPackage>();
            for (JavaPackage jp : efferents) {
                if (jp.getName().startsWith("org.argouml")) {
                    wrong.add(jp);
                }
                if (!wrong.isEmpty()) {
                    StringBuffer msg = new StringBuffer("JDepend "
                            + "indicates a dependency from "
                            + "the low level package ");
                    msg.append(p.getName());
                    msg.append(" to the argouml package(s)");
                    for (JavaPackage jpWrong : wrong) {
                        msg.append(" ");
                        msg.append(jpWrong.getName());
                    }
                    assertTrue(msg.toString(), false);
                }
            }
        }
    }
    
    static class CheckNoDependency extends TestCase {
        private String nameFrom;
        private String nameTo;
        private JDepend jdepend;

        CheckNoDependency(JDepend jd, String[] name) {
            super("Check for dependency from " + name[0] + " to " + name[1]);
            jdepend = jd;
            nameFrom = name[0];
            nameTo = name[1];
        }

        @SuppressWarnings("unchecked")
        public void runTest() {
            JavaPackage packageFrom = jdepend.getPackage(nameFrom);
            JavaPackage packageTo = jdepend.getPackage(nameTo);
            assertNotNull("Missing package", packageFrom);
            Collection<JavaPackage> efferents = packageFrom.getEfferents();
            for (JavaPackage jp : efferents) {
                if (jp.equals(packageTo)) {
                    StringBuffer msg = new StringBuffer(
                        "JDepend indicates a dependency from ");
                    msg.append(nameFrom);
                    msg.append(" to ");
                    msg.append(nameTo);
                    assertTrue(msg.toString(), false);
                }
            }
        }
    }
}
