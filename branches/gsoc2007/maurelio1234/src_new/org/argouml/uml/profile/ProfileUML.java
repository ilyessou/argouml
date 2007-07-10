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

package org.argouml.uml.profile;

import org.argouml.model.Model;

/**
 * This class represents the default UML profile
 *
 * @author Marcos Aur�lio
 */
public class ProfileUML extends Profile {

    private FormatingStrategy formatingStrategy;
    private ProfileModelLoader profileModelLoader;
    private Object model;
    private static ProfileUML instance;
    
    /**
     * @return the unique instance for this profile
     */
    public static ProfileUML getInstance() {
        if (instance == null) {
            instance = new ProfileUML();
        }
        return instance;
    }
    
    /**
     * The default constructor for this class 
     */
    private ProfileUML() {
        formatingStrategy = new JavaFormatingStrategy();
	profileModelLoader = new ResourceModelLoader();
	model = profileModelLoader
		.loadModel("/org/argouml/default-uml14.xmi");

	if (model == null) {
	    model = Model.getModelManagementFactory().createModel();
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
     * @return the Java model
     * @see org.argouml.uml.profile.Profile#getModel()
     */
    public Object getModel() {
	return model;
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
    
}
