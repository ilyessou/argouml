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

package org.argouml.uml.cognitive;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ResolvedCritic;
import org.argouml.cognitive.ToDoItem;
import org.argouml.kernel.AbstractProjectMember;
import org.argouml.kernel.Project;
import org.argouml.persistence.ResolvedCriticXMLHelper;
import org.argouml.persistence.ToDoItemXMLHelper;

/**
 * Helper class to act as a project member on behalf of the todo list.
 * It helps the todo list get loaded and saved together with the rest
 * of the project.
 *
 * @author	Michael Stockman
 */
public class ProjectMemberTodoList extends AbstractProjectMember {

    private static final String TO_DO_EXT = ".todo";

    /**
     * The constructor.
     *
     * @param name the name
     * @param p the project
     */
    public ProjectMemberTodoList(String name, Project p) {
    	super(name, p);
    }

    /**
     * @see org.argouml.kernel.AbstractProjectMember#getType()
     */
    public String getType() {
        return "todo";
    }

    /**
     * @see org.argouml.kernel.AbstractProjectMember#getZipFileExtension()
     */
    public String getZipFileExtension() {
        return TO_DO_EXT;
    }

    /**
     * @return a vector containing the to do list
     */
    public Vector getToDoList() {
        Vector in, out;
        ToDoItem tdi;
        Designer dsgr;
        int i;

        dsgr = Designer.theDesigner();
        in = dsgr.getToDoList().getToDoItems();
        out = new Vector();
        for (i = 0; i < in.size(); i++) {
            try {
            	tdi = (ToDoItem) in.elementAt(i);
            	if (tdi == null) {
                    continue;
                }
            } catch (ClassCastException e) {
                continue;
            }

            if (tdi.getPoster() instanceof Designer) {
                out.addElement(new ToDoItemXMLHelper(tdi));
            }
        }
        return out;
    }

    /**
     * @return Vector conaining the resolved critics list
     */
    public Vector getResolvedCriticsList() {
    	LinkedHashSet in;
        Vector out;
    	ResolvedCritic rci;
    	Designer dsgr;

    	dsgr = Designer.theDesigner();
    	in = dsgr.getToDoList().getResolvedItems();

    	out = new Vector();
    	for (Iterator it = in.iterator(); it.hasNext();) {
            Object o = it.next();
    	    try {
                rci = (ResolvedCritic) o;
                if (rci == null) {
                    continue;
                }
    	    } catch (ClassCastException e) {
        		continue;
    	    }
    	    out.addElement(new ResolvedCriticXMLHelper(rci));
    	}
    	return out;
    }
}
