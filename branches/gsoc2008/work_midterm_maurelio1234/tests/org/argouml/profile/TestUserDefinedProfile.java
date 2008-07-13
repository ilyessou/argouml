// $Id$
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

package org.argouml.profile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.argouml.FileHelper;
import org.argouml.model.InitializeModel;
import org.argouml.profile.internal.ProfileManagerImpl;
import org.argouml.profile.internal.ocl.CrOCL;
import org.argouml.uml.cognitive.critics.CrUML;

/**
 * Some basic tests for the {@link UserDefinedProfile} class.
 * 
 * @author Luis Sergio Oliveira (euluis)
 * @authos maurelio1234
 */
public class TestUserDefinedProfile extends TestCase {
    
    private File testDir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InitializeModel.initializeDefault();
        testDir = FileHelper.setUpDir4Test(getClass());
    }
    
    @Override
    protected void tearDown() throws Exception {
        FileHelper.deleteDir(testDir);
        super.tearDown();
    }
    
    /**
     * Test loading of a very simple profile via its constructor. Check that its
     * display name contains the file name.
     * 
     * @throws Exception if something goes wrong
     */
    public void testLoadingConstructor() throws Exception {
        // create profile model
        ProfileMother profileMother = new ProfileMother();
        Object model = profileMother.createSimpleProfileModel();
        // save the profile into a xmi file
        File profileFile = new File(testDir, "testLoadingConstructor.xmi");
        profileMother.saveProfileModel(model, profileFile);
        Profile profile = new UserDefinedProfile(profileFile);
        assertTrue(profile.getDisplayName().contains(profileFile.getName()));
    }

    /**
     * Test the constructor used for loading a profile from a Jar file
     * 
     * TODO Test FigNode!
     * 
     * @throws Exception if something goes wrong
     */
    public void testLoadingAsFromJar() throws Exception {
        ProfileManager pm = new ProfileManagerImpl();
        
        // create profile model
        ProfileMother profileMother = new ProfileMother();
        Object model = profileMother.createSimpleProfileModel();
        // save the profile into a xmi file
        File profileFile = new File(testDir, "testLoadingConstructor.xmi");
        profileMother.saveProfileModel(model, profileFile);

        CrOCL critic = new CrOCL("context Class inv: 3 > 2");
        Set<CrUML> critics = new HashSet<CrUML>();
        critics.add(critic);
        
        Set<Profile> profiles = new HashSet<Profile>();
        profiles.add(pm.getUMLProfile());
        
        Profile profile = new UserDefinedProfile("displayName", profileFile
                .toURI().toURL(), critics, profiles);
        
        assertEquals(profile.getDisplayName(), "displayName");
        assertTrue(profile.getDependencies().contains(pm.getUMLProfile()));
        assertTrue(profile.getCritics().contains(critic));       
    }
}
