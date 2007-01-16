// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.uml.notation.uml;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectSettings;
import org.argouml.model.Model;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.notation.AssociationNameNotation;

/**
 * Handles the notation of the name of an association modelelement in UML,
 * ie a string on the format:<pre>
 *     [ &lt;&lt; stereotype &gt;&gt;] [+|-|#] [name]
 * </pre>
 * 
 * @author Michiel
 */
public class AssociationNameNotationUml extends AssociationNameNotation {

    /**
     * The constructor.
     * 
     * @param association the association modelelement 
     * that we represent in textual form.
     */
    public AssociationNameNotationUml(Object association) {
        super(association);
    }

    /*
     * @see org.argouml.uml.notation.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-association-name";
    }

    /*
     * @see org.argouml.uml.notation.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        try {
            NotationUtilityUml.parseModelElement(modelElement, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.association-name";
            Object[] args = {
                pe.getLocalizedMessage(),
                new Integer(pe.getErrorOffset()),
            };
            ProjectBrowser.getInstance().getStatusBar().showStatus(
                Translator.messageFormat(msg, args));
        }
    }

    /*
     * @see org.argouml.uml.notation.NotationProvider#toString(java.lang.Object, java.util.HashMap)
     */
    public String toString(Object modelElement, HashMap args) {
        String name = Model.getFacade().getName(modelElement);
        StringBuffer sb = new StringBuffer("");
        if (isValue("fullyHandleStereotypes", args)) {
            sb.append(generateStereotypes(modelElement));
        }
        sb.append(generateVisibility(modelElement, args));
        if (isValue("pathVisible", args)) {
            sb.append(NotationUtilityUml.generatePath(modelElement));
        }
        if (name != null) {
            sb.append(name);
        }
        return sb.toString();
    }

    /**
     * @param modelElement the UML element to generate for
     * @return a string which represents the stereotypes
     */
    protected static String generateStereotypes(Object modelElement) {
        Collection c = Model.getFacade().getStereotypes(modelElement);
        StringBuffer sb = new StringBuffer(50);
        Iterator i = c.iterator();
        boolean first = true;
        while (i.hasNext()) {
            Object o = i.next();
            if (!first) {
                sb.append(',');
            }
            if (o != null) {
                sb.append(Model.getFacade().getName(o));
                first = false;
            }
        }
        Project project = 
            ProjectManager.getManager().getCurrentProject();
        ProjectSettings ps = project.getProjectSettings();
        return first ? "" : ps.getLeftGuillemot()
            + sb.toString()
            + ps.getRightGuillemot();
    }
    
    /**
     * @param modelElement the UML element to generate for
     * @param args arguments that influence the generation
     * @return a string representing the visibility
     */
    protected String generateVisibility(Object modelElement, HashMap args) {
        String s = "";
        if (isValue("visibilityVisible", args)) {
            Object v = Model.getFacade().getVisibility(modelElement);
            if (v == null) {
                /* Initially, the visibility is not set in the model.
                 * Still, we want to show the default, i.e. public.
                 */
                v = Model.getVisibilityKind().getPublic();
            }
            s = NotationUtilityUml.generateVisibility(v);
            if (s.length() > 0) {
                s = s + " ";
            }
        }
        /* This for when nothing is generated: omit the space. */
        return s;
    }
}
