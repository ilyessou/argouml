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

package org.argouml.uml.diagram;


import java.io.Writer;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectMember;
import org.argouml.ui.ArgoDiagram;
import org.argouml.xml.pgml.PGMLParser;
import org.tigris.gef.ocl.OCLExpander;
import org.tigris.gef.ocl.TemplateReader;
import org.tigris.gef.util.Util;

/**
 * @author Piotr Kaminski
 */
public class ProjectMemberDiagram extends ProjectMember {
    private static final Logger LOG = 
        Logger.getLogger(ProjectMemberDiagram.class);

    ////////////////////////////////////////////////////////////////
    // constants

    public static final String MEMBER_TYPE = "pgml";
    public static final String FILE_EXT = "." + MEMBER_TYPE;
    public static final String PGML_TEE = "/org/argouml/xml/dtd/PGML.tee";

    ////////////////////////////////////////////////////////////////
    // static variables

    public static OCLExpander expander = null;

    ////////////////////////////////////////////////////////////////
    // instance variables

    private ArgoDiagram _diagram;

    ////////////////////////////////////////////////////////////////
    // constructors

    public ProjectMemberDiagram(String name, Project p) {
        super(name, p);
    }

    public ProjectMemberDiagram(ArgoDiagram d, Project p) {
        super(null, p);
        String s = Util.stripJunk(d.getName());
        setName(s);
        setDiagram(d);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public ArgoDiagram getDiagram() {
        return _diagram;
    }
    /**
     * @see org.argouml.kernel.ProjectMember#getType()
     */
    public String getType() {
        return MEMBER_TYPE;
    }
    /**
     * @see org.argouml.kernel.ProjectMember#getFileExtension()
     */
    public String getFileExtension() {
        return FILE_EXT;
    }

    /**
     * @see org.argouml.kernel.ProjectMember#load()
     */
    public void load() {
        LOG.debug("Reading " + getURL());
        PGMLParser.SINGLETON.setOwnerRegistry(getProject().getUUIDRefs());
        ArgoDiagram d =
	    (ArgoDiagram) PGMLParser.SINGLETON.readDiagram(getURL());
        setDiagram(d);
        getProject().addDiagram(d);

    }

    /**
     * @deprecated since 0.l5.3 since the function in the
     * interface is deprecated since 0.13.6.
     * TODO: This is still used in 0.16.
     */
    public void save(String path, boolean overwrite, Writer writer) {
        if (expander == null)
            expander = new OCLExpander(TemplateReader.readFile(PGML_TEE));
        expander.expand(writer, _diagram, "", "");
    }

    /**
     * Write the diagram to the given writer.
     * @see org.argouml.kernel.ProjectMember#save(java.io.Writer)
     */
    public void save(Writer writer) {
        if (expander == null)
            expander = new OCLExpander(TemplateReader.readFile(PGML_TEE));
        expander.expand(writer, _diagram, "", "");
    }

    protected void setDiagram(ArgoDiagram diagram) {
        _diagram = diagram;
    }

} /* end class ProjectMemberDiagram */
