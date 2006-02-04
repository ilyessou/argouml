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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectMember;
import org.argouml.model.Model;
import org.argouml.uml.ProjectMemberModel;
import org.argouml.uml.cognitive.ProjectMemberTodoList;
import org.argouml.uml.diagram.ProjectMemberDiagram;
import org.xml.sax.InputSource;

/**
 * To persist to and from zipped xmi file storage.
 *
 * @author Bob Tarling
 * @author Ludovic Ma&icirc;tre
 */
public class ZipFilePersister extends XmiFilePersister {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ZipFilePersister.class);

    /**
     * The constructor.
     */
    public ZipFilePersister() {
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getExtension()
     */
    public String getExtension() {
        return "zip";
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getDesc()
     */
    protected String getDesc() {
        return "XMI compressed project file";
    }

    /**
     * It is being considered to save out individual xmi's from individuals
     * diagrams to make it easier to modularize the output of Argo.
     *
     * @param file
     *            The file to write.
     * @param project
     *            the project to save
     * @throws SaveException
     *             when anything goes wrong
     *
     * @see org.argouml.persistence.ProjectFilePersister#save(
     *      org.argouml.kernel.Project, java.io.File)
     */
    public void doSave(Project project, File file) throws SaveException {

        LOG.info("Receiving file '" + file.getName() + "'");

        File lastArchiveFile = new File(file.getAbsolutePath() + "~");
        File tempFile = null;

        try {
            tempFile = createTempFile(file);
        } catch (FileNotFoundException e) {
            throw new SaveException(
                    "Failed to archive the previous file version", e);
        } catch (IOException e) {
            throw new SaveException(
                    "Failed to archive the previous file version", e);
        }

        OutputStreamWriter writer = null;
        try {
            //project.setFile(file);

            ZipOutputStream stream =
                new ZipOutputStream(new FileOutputStream(file));
            String fileName = file.getName();
            ZipEntry xmiEntry =
                new ZipEntry(fileName.substring(0, fileName.lastIndexOf(".")));
            stream.putNextEntry(xmiEntry);
            OutputStream bout = new BufferedOutputStream(stream);
            writer = new OutputStreamWriter(bout, getEncoding());

            int size = project.getMembers().size();
            for (int i = 0; i < size; i++) {
                ProjectMember projectMember =
                    (ProjectMember) project.getMembers().get(i);
                if (projectMember.getType().equalsIgnoreCase("xmi")) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Saving member of type: "
                              + ((ProjectMember) project.getMembers()
                                    .get(i)).getType());
                    }
                    MemberFilePersister persister
                        = new ModelMemberFilePersister();
                    persister.save(projectMember, writer, null);
                }
            }
            stream.close();
            // if save did not raise an exception
            // and name+"#" exists move name+"#" to name+"~"
            // this is the correct backup file
            if (lastArchiveFile.exists()) {
                lastArchiveFile.delete();
            }
            if (tempFile.exists() && !lastArchiveFile.exists()) {
                tempFile.renameTo(lastArchiveFile);
            }
            if (tempFile.exists()) {
                tempFile.delete();
            }
        } catch (Exception e) {
            LOG.error("Exception occured during save attempt", e);
            try {
                writer.close();
            } catch (IOException ex) { }

            // frank: in case of exception
            // delete name and mv name+"#" back to name if name+"#" exists
            // this is the "rollback" to old file
            file.delete();
            tempFile.renameTo(file);
            // we have to give a message to user and set the system to unsaved!
            throw new SaveException(e);
        }

        try {
            writer.close();
        } catch (IOException ex) {
            LOG.error("Failed to close save output writer", ex);
        }
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
        } else if (pm instanceof ProjectMemberModel) {
            persister = new ModelMemberFilePersister();
        }
        return persister;
    }

    /**
     * @see org.argouml.persistence.ProjectFilePersister#doLoad(java.io.File)
     */
    public Project doLoad(File file)
        throws OpenException {

        LOG.info("Receiving file '" + file.getName() + "'");

        try {
            Project p = new Project();
            String fileName = file.getName();
            String extension =
                fileName.substring(
                        fileName.indexOf('.'),
                        fileName.lastIndexOf('.'));
            InputStream stream = openZipStreamAt(file.toURL(), extension);
            InputSource is = new InputSource(stream);
            is.setSystemId(file.toURL().toExternalForm());
            XMIParser.getSingleton().readModels(p, is);
            XMIParser.getSingleton().registerDiagrams(p);            
            Object model = XMIParser.getSingleton().getCurModel();
            Model.getUmlHelper().addListenersToModel(model);
            p.setUUIDRefs(XMIParser.getSingleton().getUUIDRefs());
            p.addMember(new ProjectMemberTodoList("", p));
            p.addMember(model);
            p.setRoot(model);
            ProjectManager.getManager().setNeedsSave(false);
            return p;
        } catch (IOException e) {
            throw new OpenException(e);
        }

    }

    /**
     * Open a ZipInputStream to the first file found with a given extension.
     *
     * @param url
     *            The URL of the zip file.
     * @param ext
     *            The required extension.
     * @return the zip stream positioned at the required location.
     * @throws IOException
     *             if there is a problem opening the file.
     */
    private ZipInputStream openZipStreamAt(URL url, String ext)
        throws IOException {
        ZipInputStream zis = new ZipInputStream(url.openStream());
        ZipEntry entry = zis.getNextEntry();
        while (entry != null && !entry.getName().endsWith(ext)) {
            entry = zis.getNextEntry();
        }
        return zis;
    }
}
