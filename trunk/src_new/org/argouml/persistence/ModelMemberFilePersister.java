// $Id$
// Copyright (c) 1996-2006 The Regents of the University of California. All
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.argouml.application.ArgoVersion;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectMember;
import org.argouml.model.Model;
import org.argouml.model.UmlException;
import org.argouml.model.XmiExtensionWriter;
import org.argouml.model.XmiWriter;
import org.argouml.ocl.ArgoFacade;
import org.argouml.ocl.OCLExpander;
import org.argouml.uml.ProjectMemberModel;
import org.argouml.uml.cognitive.ProjectMemberTodoList;
import org.argouml.uml.diagram.ProjectMemberDiagram;
import org.tigris.gef.ocl.TemplateReader;
import org.xml.sax.InputSource;

/**
 * The file persister for the UML model.
 * @author Bob Tarling
 */
public class ModelMemberFilePersister extends MemberFilePersister implements XmiExtensionWriter, XmiExtensionParser {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ModelMemberFilePersister.class);

    /**
     * Loads a model (XMI only) from an input source. BE ADVISED this
     * method has a side effect. It sets _UUIDREFS to the model.<p>
     *
     * If there is a problem with the xmi file, an error is set in the
     * getLastLoadStatus() field. This needs to be examined by the
     * calling function.<p>
     *
     * @see org.argouml.persistence.MemberFilePersister#load(org.argouml.kernel.Project,
     * java.io.InputStream)
     */
    public void load(Project project, InputStream inputStream)
        throws OpenException {

        InputSource source = new InputSource(inputStream);
        Object mmodel = null;

        // 2002-07-18
        // Jaap Branderhorst
        // changed the loading of the projectfiles to solve hanging
        // of argouml if a project is corrupted. Issue 913
        // Created xmireader with method getErrors to check if parsing went well
        try {
            source.setEncoding("UTF-8");
            XMIParser.getSingleton().readModels(project, source);
            mmodel = XMIParser.getSingleton().getCurModel();
        } catch (OpenException e) {
            LastLoadInfo.getInstance().setLastLoadStatus(false);
            LastLoadInfo.getInstance().setLastLoadMessage(
                    "UmlException parsing XMI.");
            LOG.error("UmlException caught", e);
            throw new OpenException(e);
        }
        // This should probably be inside xmiReader.parse
        // but there is another place in this source
        // where XMIReader is used, but it appears to be
        // the NSUML XMIReader.  When Argo XMIReader is used
        // consistently, it can be responsible for loading
        // the listener.  Until then, do it here.
        Model.getUmlHelper().addListenersToModel(mmodel);

        project.addMember(mmodel);

        project.setUUIDRefs(
                new HashMap(XMIParser.getSingleton().getUUIDRefs()));
    }

    /**
     * @see org.argouml.persistence.MemberFilePersister#getMainTag()
     */
    public String getMainTag() {
        return "XMI";
    }

    /**
     * Save the project model to XMI.
     *
     * @see org.argouml.persistence.MemberFilePersister#save(
     *         org.argouml.kernel.ProjectMember, java.io.Writer,
     *         java.lang.Integer)
     */
    public void save(ProjectMember member, Writer w, Integer indent)
    	throws SaveException {

        if (w == null) {
            throw new IllegalArgumentException("No Writer specified!");
        }

        try {
            ProjectMemberModel pmm = (ProjectMemberModel) member;
            Object model = pmm.getModel();
            XmiWriter xmiWriter = Model.getXmiWriter(model, w, ArgoVersion.getVersion() + "(" + UmlFilePersister.PERSISTENCE_VERSION + ")");
            LOG.info("Registering extension writer to XmiWriter");
            xmiWriter.setXmiExtensionWriter(this);
            xmiWriter.write();
        } catch (UmlException e) {
            throw new SaveException(e);
        }

    }
    

    public void write(Writer writer) throws IOException {
        writer.write("<XMI.extensions xmi.extender='ArgoUML'>\n");
        
        Project project = ProjectManager.getManager().getCurrentProject();
        
        for (Iterator it = project.getMembers().iterator(); it.hasNext(); ) {
            ProjectMember projectMember = (ProjectMember) it.next();
            
            writer.write("<XMI.extension xmi.extender='ArgoUML' xmi.label='argo'>\n");
            
            try {
                Hashtable templates =
                    TemplateReader.getInstance().read("/org/argouml/persistence/argo.tee");
                OCLExpander expander = new OCLExpander(templates);
                expander.expand(writer, project);
            } catch (Exception e) {
                LOG.error("Exception expanding argo.tee", e);
                throw new IOException("Exception expanding argo.tee");
            }

            writer.write("</XMI.extension>\n");
            
            if (!projectMember.getType().equalsIgnoreCase("xmi")) {
                writer.write("<XMI.extension xmi.extender='ArgoUML' xmi.label='"
                        + projectMember.getType().toLowerCase() + "'>\n");
                if (LOG.isInfoEnabled()) {
                    LOG.info("Saving member of type: "
                          + projectMember.getType());
                }
                MemberFilePersister persister
                    = getMemberFilePersister(projectMember);
                try {
                    persister.save(projectMember, writer, null);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }
                writer.write("</XMI.extension>\n");
            }
        }
        
        writer.write("</XMI.extensions>\n");
    }
    
    /**
     * Get a MemberFilePersister based on a given ProjectMember.
     *
     * @param pm the project member
     * @return the persister
     */
    protected MemberFilePersister getMemberFilePersister(ProjectMember pm) {
        MemberFilePersister persister = null;
        if (pm instanceof ProjectMemberDiagram) {
            persister =
                PersistenceManager.getInstance()
                .getDiagramMemberFilePersister();
        } else if (pm instanceof ProjectMemberTodoList) {
            persister = new TodoListMemberFilePersister();
        }
        return persister;
    }

    public void parse(String label, String xmiExtensionString) {
        LOG.info("Parsing an extension for " + label);
    }
}
