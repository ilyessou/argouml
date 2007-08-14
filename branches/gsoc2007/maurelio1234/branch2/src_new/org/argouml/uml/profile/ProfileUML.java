// $Id: ProfileUML.java 13040 2007-07-10 20:00:25Z linus $
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

package org.argouml.uml.profile;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.model.Model;

/**
 * This class represents the default UML profile
 *
 * @author Marcos Aur�lio
 */
public class ProfileUML extends Profile {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(ProfileUML.class);
    
    private FormatingStrategy formatingStrategy;
    private ProfileModelLoader profileModelLoader;
    private Collection model;
    private static ProfileUML instance;
    
    /**
     * @return the unique instance for this profile
     */
    public static ProfileUML getInstance() {
        if (instance == null) {           
            try {
                instance = new ProfileUML();
            } catch (ProfileException e) {
                LOG.error("Exception", e);
                instance = null;
            }
        }
        return instance;
    }
    
    /**
     * The default constructor for this class 
     * @throws ProfileException 
     */
    @SuppressWarnings("unchecked")
    private ProfileUML() throws ProfileException {
        formatingStrategy = new JavaFormatingStrategy();
	profileModelLoader = new ResourceModelLoader();
	model = profileModelLoader
		.loadModel("/org/argouml/default-uml14.xmi");

        if (model == null) {
            model = new ArrayList();
            model.add(Model.getModelManagementFactory().createModel());
        }        
    }    

    
    /**
     * @return the Java formating strategy
     * @see org.argouml.uml.profile.Profile#getFormatingStrategy()
     */
    public FormatingStrategy getFormatingStrategy() {
	return formatingStrategy;
    }

    /**
     * @return "UML 1.4"
     * @see org.argouml.uml.profile.Profile#getDisplayName()
     */
    public String getDisplayName() {
	return "UML 1.4";
    }

    /**
     * @return null
     * @see org.argouml.uml.profile.Profile#getFigureStrategy()
     */
    public FigNodeStrategy getFigureStrategy() {
	return null;
    }

    @Override
    public Collection getProfilePackages() throws ProfileException {
        return model;
    }
    
}
