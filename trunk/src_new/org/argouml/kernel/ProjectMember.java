// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.kernel;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

public abstract class ProjectMember {

    ////////////////////////////////////////////////////////////////
    // instance varables

    //protected String _name;
    private String _name;
    protected Project _project = null;

    ////////////////////////////////////////////////////////////////
    // constructors

    public ProjectMember(String name, Project project) {
	_project = project;
	setName(name);
    }


    ////////////////////////////////////////////////////////////////
    // accessors


    /**
     * In contrast to {@link #getName} returns the member's name without the
     * prepended name of the project. This is the name that
     * {@link Project#findMemberByName} goes by.
     *
     * @author Steffen Zschaler
     */
    public String getPlainName() {
	String s = _name;
    
	if (s != null) {
	    if (!s.endsWith (getFileExtension())) {
		s += getFileExtension();
	    }
	}
    
	return s;
    }

    /**
     * In contrast to {@link #getPlainName} returns the member's name
     * including the project's base name. The project's base name is
     * prepended followed by an underscore '_'.
     */
    public String getName() {
	if (_name == null)
	    return null;

	String s = _project.getBaseName();

	if (_name.length() > 0)
	    s += "_" + _name;
    
	if (!s.endsWith(getFileExtension()))
	    s += getFileExtension();
    
	return s;
    }
  
    public void setName(String s) { 
	_name = s;

	if (_name == null)
	    return;

	if (_name.startsWith (_project.getBaseName())) {
	    _name = _name.substring (_project.getBaseName().length());
	    int i = 0;
	    for (; i < _name.length(); i++)
		if (_name.charAt(i) != '_')
		    break;
	    if (i > 0)
		_name = _name.substring(i);
	}

	if (_name.endsWith(getFileExtension()))
	    _name =
		_name.substring(0,
				_name.length() - getFileExtension().length());
    }

    public Project getProject() { return _project; }

    public abstract String getType();
    public abstract String getFileExtension();

    public URL getURL() {
	return getProject().findMemberURLInSearchPath(getName());
    }

    ////////////////////////////////////////////////////////////////
    // actions

    public abstract void load() throws IOException, org.xml.sax.SAXException;

    /**
     * Save the projectmember to the given writer.
     * @param writer
     * @throws Exception
     */
    public abstract void save(Writer writer) throws Exception;

    public void remove() {
        _name = null;
        _project = null;
    }
} /* end class ProjectMember */

