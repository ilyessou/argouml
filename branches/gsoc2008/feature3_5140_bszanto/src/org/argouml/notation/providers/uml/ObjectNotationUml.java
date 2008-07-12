// $Id$
// Copyright (c) 2005-2007 The Regents of the University of California. All
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

package org.argouml.notation.providers.uml;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.notation.providers.ObjectNotation;

/**
 * @author mvw@tigris.org
 *
 */
public class ObjectNotationUml extends ObjectNotation {

    /**
     * The constructor.
     *
     * @param theObject the UML Object
     */
    public ObjectNotationUml(Object theObject) {
        super(theObject);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-object";
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        String s = text.trim();
        if (s.length() == 0) {
            return;
        }
        // strip any trailing semi-colons
        if (s.charAt(s.length() - 1) == ';') {
            s = s.substring(0, s.length() - 2);
        }

        String name = "";
        String bases = "";
        StringTokenizer baseTokens = null;

        if (s.indexOf(":", 0) > -1) {
            name = s.substring(0, s.indexOf(":", 0)).trim();
            bases = s.substring(s.indexOf(":", 0) + 1).trim();
            baseTokens = new StringTokenizer(bases, ",");
        } else {
            name = s;
        }

        Model.getCommonBehaviorHelper().setClassifiers(modelElement, 
                Collections.EMPTY_LIST);
        if (baseTokens != null) {
            while (baseTokens.hasMoreElements()) {
                String typeString = baseTokens.nextToken();
                Object type =
                    ProjectManager.getManager()
                        .getCurrentProject().findType(typeString);
                Model.getCommonBehaviorHelper().addClassifier(modelElement, 
                        type);
            }
        }
        /* This updates the diagram - hence as last statement: */
        Model.getCoreHelper().setName(modelElement, name);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#toString(java.lang.Object, java.util.Map)
     */
    public String toString(Object modelElement, Map args) {
        String nameStr = "";
        if (Model.getFacade().getName(modelElement) != null) {
            nameStr = Model.getFacade().getName(modelElement).trim();
        }


        StringBuilder baseString = new StringBuilder();
        Iterator bases = Model.getFacade().getClassifiers(modelElement)
                .iterator();
        if (bases.hasNext()) {
            baseString.append(Model.getFacade().getName(bases.next()));
            while (bases.hasNext()) {
                baseString.append(", ").append(
                        Model.getFacade().getName(bases.next()));
            }
        }

        if ((nameStr.length() == 0) && (baseString.length() == 0)) {
            return "";
        }
        String base = baseString.toString().trim();
        if (base.length() < 1) {
            return nameStr.trim();
        }
        return nameStr.trim() + " : " + base;
    }

}
