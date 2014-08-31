/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
 *    Thomas Neustupny
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.argouml.application.api.Argo;
import org.argouml.application.helpers.ApplicationVersion;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectFactory;
import org.argouml.kernel.ProjectMember;
import org.argouml.model.UmlException;
import org.argouml.util.ThreadUtils;
import org.tigris.gef.ocl.ExpansionException;
import org.tigris.gef.ocl.OCLExpander;
import org.tigris.gef.ocl.TemplateReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * To persist to and from argo (xml file) storage.
 *
 * @author Bob Tarling
 */
public class UmlFilePersister extends AbstractFilePersister {

    /**
     * The PERSISTENCE_VERSION is increased every time the persistence format
     * changes. This controls conversion of old persistence version files to be
     * converted to the current one, keeping ArgoUML backwards compatible.
     */
    public static final int PERSISTENCE_VERSION = 6;

    /**
     * The TOTAL_PHASES_LOAD constant is the number of phases used by the load
     * process.
     */
    protected static final int UML_PHASES_LOAD = 2;

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(UmlFilePersister.class.getName());

    private static final String ARGO_TEE = "/org/argouml/persistence/argo.tee";

    /**
     * The constructor.
     */
    public UmlFilePersister() {
    }

    /*
     * @see org.argouml.persistence.AbstractFilePersister#getExtension()
     */
    public String getExtension() {
        return "uml";
    }

    /*
     * @see org.argouml.persistence.AbstractFilePersister#getDesc()
     */
    protected String getDesc() {
        return Translator.localize("combobox.filefilter.uml");
    }

    /**
     * Save the project in ".uml" format.
     *
     * @param file The file to write.
     * @param project the project to save
     * @throws SaveException when anything goes wrong
     * @throws InterruptedException if the thread is interrupted
     *
     * @see org.argouml.persistence.ProjectFilePersister#save(org.argouml.kernel.Project,
     *      java.io.File)
     */
    public void doSave(Project project, File file) throws SaveException,
        InterruptedException {

        /* Retain the previous project file even when the save operation
         * crashes in the middle. Also create a backup file after saving. */
        boolean doSafeSaves = useSafeSaves();

        ProgressMgr progressMgr = new ProgressMgr();
        progressMgr.setNumberOfPhases(4);
        progressMgr.nextPhase();

        File lastArchiveFile = new File(file.getAbsolutePath() + "~");
        File tempFile = null;

        if (doSafeSaves) {
            try {
                tempFile = createTempFile(file);
            } catch (FileNotFoundException e) {
                throw new SaveException(Translator.localize(
                        "optionpane.save-project-exception-cause1"), e);
            } catch (IOException e) {
                throw new SaveException(Translator.localize(
                        "optionpane.save-project-exception-cause2"), e);
            }
        }

        try {
            project.setFile(file);
            project.setVersion(ApplicationVersion.getVersion());
            project.setPersistenceVersion(PERSISTENCE_VERSION);

            OutputStream stream = new FileOutputStream(file);

            writeProject(project, stream, progressMgr);

            stream.close();

            progressMgr.nextPhase();

            String path = file.getParent();

            LOG.log(Level.INFO, "Dir == {0}", path);

            if (doSafeSaves) {
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
            }

            progressMgr.nextPhase();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception occured during save attempt", e);

            if (doSafeSaves) {
                // frank: in case of exception
                // delete name and mv name+"#" back to name if name+"#" exists
                // this is the "rollback" to old file
                file.delete();
                tempFile.renameTo(file);
            }
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            } else {
                // we have to give a message to user and set the system
                // to unsaved!
                throw new SaveException(e);
            }
        }
    }

    /**
     * The .uml save format is no longer available to save.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveEnabled() {
        return true;
    }

    /**
     * Write the output for a project on the given stream.
     *
     * @param project The project to output.
     * @param stream The stream to write to.
     * @throws SaveException If something goes wrong.
     * @throws InterruptedException if the thread is interrupted
     */
    void writeProject(Project project, OutputStream oStream,
            ProgressMgr progressMgr) throws SaveException, InterruptedException {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(oStream, Argo
                    .getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new SaveException(e);
        }
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                outputStreamWriter));

        XmlFilterOutputStream filteredStream = new XmlFilterOutputStream(
                oStream, Argo.getEncoding());
        try {
            writer.println("<?xml version = \"1.0\" " + "encoding = \""
                    + Argo.getEncoding() + "\" ?>");
            writer.println("<uml version=\"" + PERSISTENCE_VERSION + "\">");
            // Write out header section
            try {
                Hashtable templates = TemplateReader.getInstance().read(
                        ARGO_TEE);
                OCLExpander expander = new OCLExpander(templates);
                expander.expand(writer, project, "  ");
            } catch (ExpansionException e) {
                throw new SaveException(e);
            }
            writer.flush();

            if (progressMgr != null) {
                progressMgr.nextPhase();
            }

            // Note we assume members are ordered correctly already
            for (ProjectMember projectMember : project.getMembers()) {

                LOG.log(Level.INFO, "Saving member: {0}", projectMember);

                MemberFilePersister persister = getMemberFilePersister(projectMember);
                filteredStream.startEntry();
                persister.save(projectMember, filteredStream);
                try {
                    filteredStream.flush();
                } catch (IOException e) {
                    throw new SaveException(e);
                }
            }

            writer.println("</uml>");

            writer.flush();
        } finally {
            writer.close();
            try {
                filteredStream.reallyClose();
            } catch (IOException e) {
                throw new SaveException(e);
            }
        }
    }

    /*
     * @see org.argouml.persistence.ProjectFilePersister#doLoad(java.io.File)
     */
    public Project doLoad(File file) throws OpenException, InterruptedException {
        // let's initialize the progressMgr
        ProgressMgr progressMgr = new ProgressMgr();
        progressMgr.setNumberOfPhases(UML_PHASES_LOAD);

        ThreadUtils.checkIfInterrupted();
        return doLoad(file, file, progressMgr);
    }

    protected Project doLoad(File originalFile, File file,
            ProgressMgr progressMgr) throws OpenException, InterruptedException {

        XmlInputStream inputStream = null;
        try {
            Project p = ProjectFactory.getInstance()
                    .createProject(file.toURI());

            // Run through any stylesheet upgrades
            int fileVersion = getPersistenceVersionFromFile(file);

            LOG.log(Level.INFO, "Loading uml file of version {0}", fileVersion);
            if (!checkVersion(fileVersion, getReleaseVersionFromFile(file))) {
                // If we're about to upgrade the file lets take an archive
                // of it first.
                String release = getReleaseVersionFromFile(file);
                copyFile(originalFile, new File(originalFile.getAbsolutePath()
                        + '~' + release));

                progressMgr.setNumberOfPhases(progressMgr.getNumberOfPhases()
                        + (PERSISTENCE_VERSION - fileVersion));

                while (fileVersion < PERSISTENCE_VERSION) {
                    ++fileVersion;

                    LOG.log(Level.INFO, "Upgrading to version {0}", fileVersion);

                    long startTime = System.currentTimeMillis();
                    file = transform(file, fileVersion);

                    long endTime = System.currentTimeMillis();
                    LOG.log(Level.INFO, "Upgrading took " + ((endTime - startTime) / 1000)
                            + " seconds");
                    progressMgr.nextPhase();
                }
            }

            progressMgr.nextPhase();

            inputStream = new XmlInputStream(file.toURI().toURL().openStream(),
                    "argo", file.length(), 100000);

            ArgoParser parser = new ArgoParser();
            Reader reader = new InputStreamReader(inputStream, Argo
                    .getEncoding());
            parser.readProject(p, reader);

            List memberList = parser.getMemberList();

            LOG.log(Level.INFO,memberList.size() + " members");

            for (int i = 0; i < memberList.size(); ++i) {
                MemberFilePersister persister = getMemberFilePersister((String) memberList
                        .get(i));
                LOG.log(Level.INFO, "Loading member with "+ persister.getClass().getName());

                inputStream.reopen(persister.getMainTag());
                // TODO: Do we need to set the input encoding here? It was
                // done for ToDo parsing, but none of the other member types
                // InputSource inputSource = new InputSource(
                // new InputStreamReader(inputStream, Argo
                // .getEncoding()));
                InputSource inputSource = new InputSource(inputStream);
                // Don't use systemId here or it will get opened in preference
                // to inputStream.
                inputSource.setPublicId(originalFile.toURI().toURL()
                        .toExternalForm());
                try {
                    persister.load(p, inputSource);
                } catch (OpenException e) {
                    // UML 2.x files could also contain a profile model.
                    // Try again with uml:Profile as main tag.
                    if ("uml:Model".equals(persister.getMainTag())
                            && e.getCause() instanceof UmlException
                            && e.getCause().getCause() instanceof IOException) {
                        inputStream.reopen("uml:Profile");
                        persister.load(p, inputSource);
                        p.setProjectType(Project.PROFILE_PROJECT);
                    } else {
                        throw e;
                    }
                }
            }

            // let's update the progress
            progressMgr.nextPhase();
            ThreadUtils.checkIfInterrupted();
            inputStream.realClose();
            p.postLoad();
            return p;
        } catch (InterruptedException e) {
            throw e;
        } catch (OpenException e) {
            throw e;
        } catch (IOException e) {
            throw new OpenException(e);
        } catch (SAXException e) {
            throw new OpenException(e);
        }
    }

    protected boolean checkVersion(int fileVersion, String releaseVersion)
        throws OpenException, VersionException {
        // If we're trying to load a file from a future version
        // complain and refuse.
        if (fileVersion > PERSISTENCE_VERSION) {
            throw new VersionException(
                    "The file selected is from a more up to date version of "
                            + "ArgoUML. It has been saved with ArgoUML version "
                            + releaseVersion
                            + ". Please load with that or a more up to date"
                            + "release of ArgoUML");
        }
        return fileVersion >= PERSISTENCE_VERSION;
    }

    /**
     * Transform a string of XML data according to the service required.
     *
     * @param file The XML file to be transformed
     * @param version the version of the persistence format the XML is to be
     *            transformed to.
     * @return the transformed XML file
     * @throws OpenException on XSLT transformation error or file read
     */
    public final File transform(File file, int version) throws OpenException {

        try {
            String upgradeFilesPath = "/org/argouml/persistence/upgrades/";
            String upgradeFile = "upgrade" + version + ".xsl";

            String xsltFileName = upgradeFilesPath + upgradeFile;
            URL xsltUrl = UmlFilePersister.class.getResource(xsltFileName);

            LOG.log(Level.INFO, "Resource is {0}", xsltUrl);

            // Read xsltStream into a temporary file
            // Get url for temp file.
            // openStream from url and wrap in StreamSource
            StreamSource xsltStreamSource = new StreamSource(xsltUrl
                    .openStream());
            xsltStreamSource.setSystemId(xsltUrl.toExternalForm());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsltStreamSource);

            File transformedFile = File.createTempFile("upgrade_" + version
                    + "_", ".uml");
            transformedFile.deleteOnExit();

            FileOutputStream stream = new FileOutputStream(transformedFile);
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream,
                    Argo.getEncoding()));
            Result result = new StreamResult(writer);

            StreamSource inputStreamSource = new StreamSource(file);
            inputStreamSource.setSystemId(file);
            transformer.transform(inputStreamSource, result);

            writer.close();
            return transformedFile;
        } catch (IOException e) {
            throw new OpenException(e);
        } catch (TransformerException e) {
            throw new OpenException(e);
        }
    }

    /**
     * Read stream in .argo format and extracts the persistence version number
     * from the root tag.
     *
     * @param file the XML file
     * @return The version number
     * @throws OpenException on any error
     */
    private int getPersistenceVersionFromFile(File file) throws OpenException {
        InputStream stream = null;
        try {
            stream = new BufferedInputStream(file.toURI().toURL().openStream());
            int version = getPersistenceVersion(stream);
            stream.close();
            return version;
        } catch (MalformedURLException e) {
            throw new OpenException(e);
        } catch (IOException e) {
            throw new OpenException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Reads an XML file of uml format and extracts the persistence version
     * number from the root tag.
     *
     * @param inputStream stream pointing to file to read.
     * @return The version number
     * @throws OpenException on any error
     */
    protected int getPersistenceVersion(InputStream inputStream)
        throws OpenException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, Argo
                    .getEncoding()));
            String rootLine = reader.readLine();
            while (rootLine != null && !rootLine.trim().startsWith("<argo ")) {
                rootLine = reader.readLine();
            }
            if (rootLine == null) {
                return 1;
            }
            return Integer.parseInt(getVersion(rootLine));
        } catch (IOException e) {
            throw new OpenException(e);
        } catch (NumberFormatException e) {
            throw new OpenException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // No more we can do here on failure
            }
        }
    }

    /**
     * Reads an XML file of uml format and extracts the persistence version
     * number from the root tag.
     *
     * @param file the XML file
     * @return The ArgoUML release number
     * @throws OpenException on any error
     */
    private String getReleaseVersionFromFile(File file) throws OpenException {
        InputStream stream = null;
        try {
            stream = new BufferedInputStream(file.toURI().toURL().openStream());
            String version = getReleaseVersion(stream);
            stream.close();
            return version;
        } catch (MalformedURLException e) {
            throw new OpenException(e);
        } catch (IOException e) {
            throw new OpenException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Reads an XML file of uml format and extracts the persistence version
     * number from the root tag.
     *
     * @param inputStream the stream point to the XML file
     * @return The ArgoUML release number
     * @throws OpenException on any error
     */
    protected String getReleaseVersion(InputStream inputStream)
        throws OpenException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, Argo
                    .getEncoding()));
            String versionLine = reader.readLine();
            while (!versionLine.trim().startsWith("<version>")) {
                versionLine = reader.readLine();
                if (versionLine == null) {
                    throw new OpenException(
                            "Failed to find the release <version> tag");
                }
            }
            versionLine = versionLine.trim();
            int end = versionLine.lastIndexOf("</version>");
            return versionLine.trim().substring(9, end);
        } catch (IOException e) {
            throw new OpenException(e);
        } catch (NumberFormatException e) {
            throw new OpenException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // No more we can do here on failure
            }
        }
    }

    /**
     * Get the version attribute value from a string of XML.
     *
     * @param rootLine the line
     * @return the version
     */
    protected String getVersion(String rootLine) {
        String version;
        int versionPos = rootLine.indexOf("version=\"");
        if (versionPos > 0) {
            int startPos = versionPos + 9;
            int endPos = rootLine.indexOf("\"", startPos);
            version = rootLine.substring(startPos, endPos);
        } else {
            version = "1";
        }
        return version;
    }

    /**
     * Returns true. All Argo specific files have an icon.
     *
     * {@inheritDoc}
     */
    public boolean hasAnIcon() {
        return true;
    }

    /**
     * Class to filter XML declaration and DOCTYPE declaration from an output
     * stream to allow use as nested XML files.
     *
     * @author Tom Morris
     */
    class XmlFilterOutputStream extends FilterOutputStream {

        private CharsetDecoder decoder;

        private boolean headerProcessed = false;

        private static final int BUFFER_SIZE = 120;

        /**
         * The following three fields make up a doubly mapped buffer with two
         * sets of pointers (the ByteBuffer objects), one for input and one for
         * output. Bytes are written into the buffer using outBB which advances
         * the current position. The following ASCII art shows what the buffer
         * looks like.<code>
         *           vp       vl
         * xxxxxx++++..........
         *       ^p  ^l
         * </code> vp & vl are the output (buffer writing) position and limit,
         * respectively and show the free space that can receive bytes. The ^p
         * and ^l pointers show the input (buffer reading) position and limit.
         * They point to valid bytes which have not yet been converted to
         * characters. The xxx bytes have been both written in and read back
         * out. The +++ bytes have been written into the buffer, but not read
         * out yet.
         */
        private byte[] bytes = new byte[BUFFER_SIZE * 2];

        private ByteBuffer outBB = ByteBuffer.wrap(bytes);

        private ByteBuffer inBB = ByteBuffer.wrap(bytes);

        // Buffer containing characters which have been decoded from the bytes
        // in inBB.
        private CharBuffer outCB = CharBuffer.allocate(BUFFER_SIZE);

        // RegEx pattern for XML declaration and, optionally, DOCTYPE
        // Backslashes are doubled up - one for Java, one for Regex
        private final Pattern xmlDeclarationPattern = Pattern
                .compile("\\s*<\\?xml.*\\?>\\s*(<!DOCTYPE.*>\\s*)?");

        /**
         * Construct a filtered output stream using the given character set
         * name.
         *
         * @param outputStream source output stream to filter
         * @param charsetName name of character set to use for encoding
         */
        public XmlFilterOutputStream(OutputStream outputStream,
                String charsetName) {
            this(outputStream, Charset.forName(charsetName));
        }

        /**
         * Construct a filtered output stream using the given character set.
         *
         * @param outputStream source output stream to filter
         * @param charset character set to use for encoding
         */
        public XmlFilterOutputStream(OutputStream outputStream, Charset charset) {
            super(outputStream);
            decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            startEntry();
        }

        /**
         * Reset processing for the beginning of an entry.
         */
        public void startEntry() {
            headerProcessed = false;
            resetBuffers();
        }

        private void resetBuffers() {
            inBB.limit(0);
            outBB.position(0);
            outCB.position(0);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if ((off | len | (b.length - (len + off)) | (off + len)) < 0) {
                throw new IndexOutOfBoundsException();
            }

            if (headerProcessed) {
                out.write(b, off, len);
            } else {
                // TODO: Make this more efficient for large I/Os
                for (int i = 0; i < len; i++) {
                    write(b[off + i]);
                }
            }

        }

        @Override
        public void write(int b) throws IOException {

            if (headerProcessed) {
                out.write(b);
            } else {
                outBB.put((byte) b);
                inBB.limit(outBB.position());
                // Convert from bytes back to characters
                CoderResult result = decoder.decode(inBB, outCB, false);
                if (result.isError()) {
                    throw new RuntimeException(
                            "Unknown character decoding error");
                }

                // This will have problems if the smallest possible
                // data segment is smaller than the size of the buffer
                // needed for regex matching
                if (outCB.position() == outCB.limit()) {
                    processHeader();
                }

            }
        }

        private void processHeader() throws IOException {
            headerProcessed = true;
            outCB.position(0); // rewind our character buffer

            Matcher matcher = xmlDeclarationPattern.matcher(outCB);
            // Remove anything that matches our pattern
            String headerRemainder = matcher.replaceAll("");
            int index = headerRemainder.length() - 1;
            if (headerRemainder.charAt(index) == '\0') {
                // Remove null characters at the end
                do {
                    index--;
                } while (index >= 0 && headerRemainder.charAt(index) == '\0');
                headerRemainder = headerRemainder.substring(0, index + 1);
            }

            // Reencode the remaining characters as bytes again
            ByteBuffer bb = decoder.charset().encode(headerRemainder);

            // and write them to our output stream
            byte[] outBytes = new byte[bb.limit()];
            bb.get(outBytes);
            out.write(outBytes, 0, outBytes.length);

            // Write any left over bytes in the input buffer
            // (perhaps from a partially decoded character)
            if (inBB.remaining() > 0) {
                out.write(inBB.array(), inBB.position(), inBB.remaining());
                inBB.position(0);
                inBB.limit(0);
            }
        }

        /**
         * This method has no effect to keep sub-writers from closing it
         * accidently. The master can use the method {@link #reallyClose()} to
         * actually close the underlying stream.
         */
        @Override
        public void close() throws IOException {
            flush();
        }

        /**
         * Close the stream.
         *
         * @throws IOException
         */
        public void reallyClose() throws IOException {
            out.close();
        }

        /**
         * Flush the stream. If the stream is flushed before the header is
         * completely processed, whatever has been written so far will get
         * processed before the flush.
         */
        @Override
        public void flush() throws IOException {
            if (!headerProcessed) {
                processHeader();
            }
            out.flush();
        }

    }
}
