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

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectMember;
import org.argouml.model.Model;
import org.argouml.model.UmlException;
import org.argouml.model.XmiWriter;
import org.argouml.uml.ProjectMemberModel;
import org.xml.sax.InputSource;

/**
 * The file persister for the UML model.
 * @author Bob Tarling
 */
public class ModelMemberFilePersister extends MemberFilePersister {

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
            XMIParser.getSingleton().readModels(project ,source);
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

        project.setUUIDRefs(new HashMap(XMIParser.getSingleton().getUUIDRefs()));
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

            File tempFile = null;
            Writer writer = null;
            if (indent != null) {
                // If we have an indent then we are adding this file
                // to a superfile.
                // That is most likely inserting the XMI into the .uml file
                tempFile = File.createTempFile("xmi", null);
                tempFile.deleteOnExit();

                writer =
                    new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(tempFile), "UTF-8"));
                //writer = new FileWriter(tempFile);
                XmiWriter xmiWriter = Model.getXmiWriter(model, writer);
                xmiWriter.write();
                addXmlFileToWriter((PrintWriter) w, tempFile,
                                   indent.intValue());
            } else {
                // Othewise we are writing into a zip writer.
                XmiWriter xmiWriter = Model.getXmiWriter(model, w);
                xmiWriter.write();
            }
        } catch (IOException e) {
            throw new SaveException(e);
        } catch (UmlException e) {
            throw new SaveException(e);
        }

    }
}
