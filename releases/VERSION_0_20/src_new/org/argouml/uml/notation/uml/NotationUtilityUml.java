// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
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

package org.argouml.uml.notation.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.Profile;
import org.argouml.uml.ProfileException;
import org.argouml.util.MyTokenizer;

/**
 * This class is a utility for the UML notation. <p>
 * 
 * TODO: No need to make this class public.
 * 
 * @author mvw@tigris.org
 */
public final class NotationUtilityUml {

    /**
     * The constructor.
     */
    private NotationUtilityUml() {
    }
    
    /**
     * This function shall replace the previous set of stereotypes 
     * of the given modelelement with a new set, 
     * given in the form of a "," seperated string of stereotype names.
     * 
     * @param umlobject the UML element to adapt
     * @param stereotype comma seperated stereotype names
     * @param full false if stereotypes are only added, 
     *             true if removal should be done, too.
     */
    public static void dealWithStereotypes(Object umlobject, String stereotype, 
            boolean full) {
        String token;
        MyTokenizer mst;
        Collection stereotypes = new ArrayList();
        
        /* Convert the string (e.g. "aaa,bbb,ccc") 
         * into seperate stereotype-names (e.g. "aaa", "bbb", "ccc").
         */
        if (stereotype != null) {
            mst = new MyTokenizer(stereotype, " ,\\,");
            while (mst.hasMoreTokens()) {
                token = mst.nextToken();
                if (!",".equals(token) && !" ".equals(token)) {
                    stereotypes.add(token);
                }
            }
        }
        
        if (full) {
            // collect the to be removed stereotypes
            Collection toBeRemoved = new ArrayList();
            Iterator i = Model.getFacade().getStereotypes(umlobject).iterator();
            while (i.hasNext()) {
                String stereotypename = Model.getFacade().getName(i.next());
                if (stereotypename != null 
                        && !stereotypes.contains(stereotypename)) {
                    toBeRemoved.add(getStereotype(umlobject, stereotypename));
                }
            }
        
            // and now remove them
            i = toBeRemoved.iterator();
            while(i.hasNext()) {
                Model.getCoreHelper().removeStereotype(umlobject, i.next());
            }
        }

        // add stereotypes
        if (!stereotypes.isEmpty()) {
            Iterator i = stereotypes.iterator();
            while (i.hasNext()) {
                String stereotypename = (String) i.next();
                Object umlstereo = getStereotype(umlobject, stereotypename);
                if (umlstereo != null) {
                    Model.getCoreHelper().addStereotype(umlobject, umlstereo);
                }
            }
        }
    }

    /**
     * Finds a stereotype named name either in the subtree of the model rooted
     * at root, or in the the ProfileJava model.<p>
     *
     * TODO: Should create the stereotype under root if it isn't found.
     *
     * @param obj
     *            A ModelElement to find a suitable stereotype for.
     * @param name
     *            The name of the stereotype to search for.
     * @return A stereotype named name, or possibly null.
     */
    public static Object getStereotype(Object obj, String name) {
        Object root = Model.getFacade().getModel(obj);
        Object stereo;

        stereo = recFindStereotype(obj, root, name);
        if (stereo != null) {
            return stereo;
        }

        try {
            Project project = ProjectManager.getManager().getCurrentProject();
            Profile profile = project.getProfile();
            stereo = recFindStereotype(obj, profile.getProfileModel(), name);
        } catch (ProfileException e) {
            // TODO: How are we going to handle exceptions here?
            // I suspect the profile should be part of the project
            // and not a singleton.
        }

        if (stereo != null) {
            return Model.getModelManagementHelper().getCorrespondingElement(
                    stereo, root);
        }

        if (root != null && name.length() > 0) {
            stereo = Model.getExtensionMechanismsFactory().buildStereotype(
                    obj, name, root);
        }

        return stereo;
    }
    
    /**
     * Recursively search a hive of a model for a stereotype with the name given
     * in name.
     *
     * @param obj
     *            The model element to be suitable for.
     * @param root
     *            The model element to search from.
     * @param name
     *            The name of the stereotype to search for.
     * @return An stereotype named name, or null if none is found.
     */
    private static Object recFindStereotype(Object obj, Object root, String name) {
        Object stereo;

        if (root == null) {
            return null;
        }

        if (Model.getFacade().isAStereotype(root)
                && name.equals(Model.getFacade().getName(root))) {
            if (Model.getExtensionMechanismsHelper().isValidStereoType(obj,
            /* (MStereotype) */root)) {
                return root;
            }
        }

        if (!Model.getFacade().isANamespace(root)) {
            return null;
        }

        Collection ownedElements = Model.getFacade().getOwnedElements(root);

        if (ownedElements == null) {
            return null;
        }

        // Loop through each element in the namespace, recursing.

        Iterator iter = ownedElements.iterator();

        while (iter.hasNext()) {
            stereo = recFindStereotype(obj, iter.next(), name);
            if (stereo != null) {
                return stereo;
            }
        }
        return null;
    }

}
