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

package org.argouml.persistence;

import java.io.File;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.argouml.kernel.Project;

/**
 * Testcase to load projects without exception.
 */
public class TestZargoFilePersister extends TestCase {
    /**
     * The constructor.
     *
     * @param name the name
     */
    public TestZargoFilePersister(String name) {
        super(name);
    }

    /**
     * @param args the arguments given on the commandline
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * @return the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TestZargoFilePersister.class);

        return suite;
    }

    /**
     * Tests that a project is loadable.
     *
     * @param filename of the project file to load
     * @throws OpenException if something goes wrong.
     */
    private Project doLoad(String filename) throws OpenException {
        URL url = TestZargoFilePersister.class.getResource(filename);
        ZargoFilePersister persister = new ZargoFilePersister();
        Project p = persister.doLoad(new File(url.getFile()));
        assertTrue("Load Status for " + filename + ".",
               LastLoadInfo.getInstance().getLastLoadStatus());
        return p;
    }

    /**
     * Test loading a zargo.
     *
     * @throws Exception when e.g. the filke is not found
     */
    public void testDoLoad1() throws Exception {
        doLoad("/testmodels/Empty.zargo");
    }

    /**
     * Test loading a zargo.
     *
     * @throws Exception when e.g. the filke is not found
     */
    public void testDoLoad2() throws Exception {
        doLoad("/testmodels/Alittlebitofeverything.zargo");
    }

    /**
     * Test saving a zargo.
     *
     * @throws Exception when e.g. the filke is not found
     */
    public void testSave() throws Exception {
        Project p = doLoad("/testmodels/Alittlebitofeverything.zargo");
        ZargoFilePersister persister = new ZargoFilePersister();
        persister.save(p, new File("Alittlebitofeverything2.zargo"));
    }

    /**
     * Test loading some garbage in a zargo.
     */
    public void testLoadGarbage() {
        File file = null;
        boolean loaded = true;
        try {
            file = new File("/testmodels/Garbage.zargo");
            ZargoFilePersister persister = new ZargoFilePersister();
            persister.doLoad(file);
            assertTrue("Load Status",
                    !LastLoadInfo.getInstance().getLastLoadStatus());
        } catch (OpenException io) {
            // This is the normal case.
            loaded = false;
        }
        assertTrue("No exception was thrown.", !loaded);
    }
}



