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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.argouml.application.ArgoVersion;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectMember;
import org.argouml.util.FileConstants;
import org.tigris.gef.ocl.OCLExpander;
import org.tigris.gef.ocl.TemplateReader;

/**
 * To persist to and from zargo (zipped file) storage.
 *
 * @author Bob Tarling
 */
public class ZargoFilePersister extends UmlFilePersister {
    /**
     * Logger.
     */
    private static final Logger LOG =
	Logger.getLogger(ZargoFilePersister.class);

    /**
     * This is the old version of the ArgoUML tee file which does not contain
     * the detail of member elements.
     */
    private static final String ARGO_MINI_TEE =
        "/org/argouml/persistence/argo.tee";

    /**
     * The constructor.
     */
    public ZargoFilePersister() {
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getExtension()
     */
    public String getExtension() {
        return "zargo";
    }

    /**
     * @see org.argouml.persistence.AbstractFilePersister#getDesc()
     */
    protected String getDesc() {
        return "ArgoUML compressed project file";
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

        BufferedWriter writer = null;
        try {

            project.setFile(file);
            project.setVersion(ArgoVersion.getVersion());
            project.setPersistenceVersion(PERSISTENCE_VERSION);

            ZipOutputStream stream =
		new ZipOutputStream(new FileOutputStream(file));
            writer =
		new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));

            ZipEntry zipEntry =
		new ZipEntry(project.getBaseName()
			     + FileConstants.UNCOMPRESSED_FILE_EXT);
            stream.putNextEntry(zipEntry);

            Hashtable templates =
		TemplateReader.getInstance().read(ARGO_MINI_TEE);
            OCLExpander expander = new OCLExpander(templates);
            expander.expand(writer, project);

            writer.flush();

            stream.closeEntry();

            // First we save all objects that are not XMI objects i.e. the
            // diagrams (first for loop).
            // Then we save all XMI objects (second for loop).
            // This is because order is important on saving.
            // Bob - Why not do it the other way around? Surely
            // when reloading it is better to load XMI first
            // then the diagrams.
            Collection names = new ArrayList();
            int counter = 0;
            int size = project.getMembers().size();
            for (int i = 0; i < size; i++) {
                ProjectMember projectMember =
		    (ProjectMember) project.getMembers().get(i);
                if (!(projectMember.getType().equalsIgnoreCase("xmi"))) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Saving member: "
                                + ((ProjectMember) project.getMembers()
                                        .get(i)).getZipName());
                    }
                    String name = projectMember.getZipName();
                    String originalName = name;
                    while (names.contains(name)) {
                        name = ++counter + originalName;
                    }
                    names.add(name);
                    stream.putNextEntry(new ZipEntry(name));
                    MemberFilePersister persister =
                        getMemberFilePersister(projectMember);
                    persister.save(projectMember, writer, null);
                    writer.flush();
                    stream.closeEntry();
                }
            }

            for (int i = 0; i < size; i++) {
                ProjectMember projectMember =
		    (ProjectMember) project.getMembers().get(i);
                if (projectMember.getType().equalsIgnoreCase("xmi")) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Saving member of type: "
                                + ((ProjectMember) project.getMembers()
                                        .get(i)).getType());
                    }
                    stream.putNextEntry(
                            new ZipEntry(projectMember.getZipName()));
                    MemberFilePersister persister =
                        getMemberFilePersister(projectMember);
                    persister.save(projectMember, writer, null);
                }
            }
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
            } catch (IOException ex) {
                // Do nothing.
            }

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
     * @see org.argouml.persistence.ProjectFilePersister#doLoad(java.io.File)
     */
    public Project doLoad(File file)
        throws OpenException {

        try {
            File combinedFile = File.createTempFile("combinedzargo_", ".uml");
            LOG.info(
                "Combining old style zargo sub files into new style uml file "
                    + combinedFile.getAbsolutePath());
            combinedFile.deleteOnExit();

            String encoding = "UTF-8";
            FileOutputStream stream = new FileOutputStream(combinedFile);
            PrintWriter writer =
		new PrintWriter(new BufferedWriter(
			new OutputStreamWriter(stream, encoding)));

            writer.println("<?xml version = \"1.0\" " + "encoding = \""
                    + encoding + "\" ?>");

            // first read the .argo file from Zip
            ZipInputStream zis;
            String line;
            BufferedReader reader;

            zis = openZipStreamAt(file.toURL(), FileConstants.PROJECT_FILE_EXT);
            reader = new BufferedReader(new InputStreamReader(zis, encoding));
            // Keep reading till we hit the <argo> tag
            String rootLine;
            do {
                rootLine = reader.readLine();
                if (rootLine == null) {
                    throw new OpenException(
                            "Can't find an <argo> tag in the argo file");
                }
            } while(!rootLine.startsWith("<argo"));
            // Get the version from the tag.
            String version = getVersion(rootLine);
            writer.println("<uml version=\"" + version + "\">");
            writer.println(rootLine);
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
            zis.close();
            reader.close();

            // then the xmi
            zis = openZipStreamAt(file.toURL(), ".xmi");
            reader = new BufferedReader(new InputStreamReader(zis, encoding));
            // Skip 1 lines
            reader.readLine();

            readerToWriter(reader, writer);

            zis.close();
            reader.close();

            // Loop round loading the diagrams
            zis = new ZipInputStream(file.toURL().openStream());
            SubInputStream sub = new SubInputStream(zis);

            ZipEntry currentEntry = null;
            while ((currentEntry = sub.getNextEntry()) != null) {
                if (currentEntry.getName().endsWith(".pgml")) {

                    reader = new BufferedReader(
                        	new InputStreamReader(sub, encoding));
                    // Skip the 2 lines
                    //<?xml version="1.0" encoding="UTF-8" ?>
                    //<!DOCTYPE pgml SYSTEM "pgml.dtd">
                    // TODO: This could be made more robust, these 2 lines should be
                    // there but what if they don't exist?
                    reader.readLine();
                    reader.readLine();
                    readerToWriter(reader, writer);
                    sub.close();
                    reader.close();
                }
            }
            zis.close();

            // Alway load the todo items last so that any model
            // elements or figs that the todo items refer to
            // will exist before creating critics.
            zis = openZipStreamAt(file.toURL(), ".todo");
            reader = new BufferedReader(new InputStreamReader(zis, encoding));
            // Skip the 2 lines
            //<?xml version = "1.0" encoding = "UTF-8" ?>
            //<!DOCTYPE todo SYSTEM "todo.dtd" >
            // TODO: This could be made more robust, these 2 lines should be
            // there but what if they don't exist?
            reader.readLine();
            reader.readLine();

            readerToWriter(reader, writer);

            zis.close();
            reader.close();
            
            writer.println("</uml>");
            writer.close();
            LOG.info("Complated combining files");
            Project p =  super.doLoad(file, combinedFile);
            p.setURL(file.toURL());
            return p;
        } catch (IOException e) {
            throw new OpenException(e);
        }
    }

    private void readerToWriter(
            Reader reader,
            Writer writer) throws IOException {

        int ch;
        while ((ch = reader.read()) != -1) {
            if (ch != 0xFFFF) {
                writer.write(ch);
            } else {
                LOG.info("Stripping out 0xFFFF from XMI");
            }
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

    /**
     * A stream of input streams for reading the Zipped file.
     */
    private class SubInputStream extends FilterInputStream {
        private ZipInputStream in;

        /**
         * The constructor.
         *
         * @param z
         *            the zip input stream
         */
        public SubInputStream(ZipInputStream z) {
            super(z);
            in = z;
        }

        /**
         * @see java.io.InputStream#close()
         */
        public void close() throws IOException {
            in.closeEntry();
        }

        /**
         * Reads the next ZIP file entry and positions stream at the beginning
         * of the entry data.
         *
         * @return the ZipEntry just read
         * @throws IOException
         *             if an I/O error has occurred
         */
        public ZipEntry getNextEntry() throws IOException {
            return in.getNextEntry();
        }
    }
}
