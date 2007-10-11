// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.reveng.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.taskmgmt.ProgressMonitor;
import org.argouml.uml.reveng.FileImportUtils;
import org.argouml.uml.reveng.ImportInterface;
import org.argouml.uml.reveng.ImportSettings;
import org.argouml.uml.reveng.ImporterManager;
import org.argouml.util.FileFilters;
import org.argouml.util.SuffixFilter;

/**
 * This is the main class for Java reverse engineering. It's based
 * on the Antlr Java example.
 *
 * @author Andreas Rueckert <a_rueckert@gmx.net>
 */
public class JavaImport implements ImportInterface {

    /** logger */
    private static final Logger LOG = Logger.getLogger(JavaImport.class);
    
    /**
     * New model elements that were added
     */
    private Collection newElements;
    
    /*
     * @see org.argouml.uml.reveng.ImportInterface#parseFiles(org.argouml.kernel.Project, java.util.Collection, org.argouml.uml.reveng.ImportSettings, org.argouml.application.api.ProgressMonitor)
     */
    public Collection parseFiles(Project p, Collection files,
            ImportSettings settings, ProgressMonitor monitor)
        throws ImportException {

        newElements = new HashSet();
        monitor.updateMainTask(Translator.localize("dialog.import.pass1"));
        if (settings.getImportLevel() == ImportSettings.DETAIL_CLASSIFIER_FEATURE
                || settings.getImportLevel() == ImportSettings.DETAIL_FULL) {
            monitor.setMaximumProgress(files.size() * 2);
            doImportPass(p, files, settings, monitor, 0, 1);
            if (!monitor.isCanceled()) {
                monitor.updateMainTask(
                        Translator.localize("dialog.import.pass2"));
                doImportPass(p, files, settings, monitor, files.size(), 2);
            }
        } else {
            monitor.setMaximumProgress(files.size() * 2);
            doImportPass(p, files, settings, monitor, 0, 0);
        }
        monitor.close();
        return newElements;
    }


    private void doImportPass(Project p, Collection files,
            ImportSettings settings, ProgressMonitor monitor, int startCount,
            int pass) throws ImportException {
        
        int count = startCount;
        for (Iterator it = files.iterator(); it.hasNext();) {
            if (monitor.isCanceled()) {
                monitor.updateSubTask(
                        Translator.localize("dialog.import.cancelled"));
                return;
            }
            Object file = it.next();
            if (file instanceof File) {
                parseFile(p, (File) file, settings, pass);
                monitor.updateProgress(count++);
                monitor.updateSubTask(Translator.localize(
                        "dialog.import.parsingAction",
                        new Object[] {((File) file).getAbsolutePath()}));
            } else {
                throw new ImportException("Object isn't a file " + file);
            }
        }

        return;
    }
   

    /**
     * Do a single import pass of a single file.
     * 
     * @param p
     *            the project
     * @param f
     *            the source file
     * @param settings
     *            the user provided import settings
     * @param pass
     *            current import pass - 0 = single pass, 1 = pass 1 of 2, 2 =
     *            pass 2 of 2
     */
    private void parseFile(Project p, File f, ImportSettings settings, int pass)
        throws ImportException {

        try {
            // Create a scanner that reads from the input stream
            String encoding = settings.getInputSourceEncoding();
            FileInputStream in = new FileInputStream(f);
            InputStreamReader isr;
            try {
                isr = new InputStreamReader(in, encoding);
            } catch (UnsupportedEncodingException e) {
                // fall back to default encoding
                isr = new InputStreamReader(in);
            }
            JavaLexer lexer = new JavaLexer(new BufferedReader(isr));

            // We use a special Argo token, that stores the preceding
            // whitespaces.
            lexer.setTokenObjectClass("org.argouml.uml.reveng.java.ArgoToken");

            // Create a parser that reads from the scanner
            JavaRecognizer parser = new JavaRecognizer(lexer);
            
            // Pass == 0 means single pass recognition
            int parserMode =
                    JavaRecognizer.MODE_IMPORT_PASS1
                            | JavaRecognizer.MODE_IMPORT_PASS2;
            if (pass == 1) {
                parserMode = JavaRecognizer.MODE_IMPORT_PASS1;
            } else if (pass == 2) {
                parserMode = JavaRecognizer.MODE_IMPORT_PASS2;
            }
            parser.setParserMode(parserMode);

            // Create a modeller for the parser
            Modeller modeller = new Modeller(p.getModel(),
                    settings,
                    f.getName());

            // Print the name of the current file, so we can associate
            // exceptions to the file.
            LOG.info("Parsing " + f.getAbsolutePath());

            modeller.setAttribute("level", 
                    Integer.valueOf(pass));

            try {
                // start parsing at the compilationUnit rule
                parser.compilationUnit(modeller, lexer);
            } catch (Exception e) {
                String errorString = buildErrorString(f);
                LOG.error(e.getClass().getName()
                        + errorString, e);
                throw new ImportException(errorString, e);
            }
            newElements.addAll(modeller.getNewElements());
            in.close();
        } catch (IOException e) {
            throw new ImportException(buildErrorString(f), e);
        }
    }

    private String buildErrorString(File f) {
        String path = "";
        try {
            path = f.getCanonicalPath();
        } catch (IOException e) {
            // Just ignore - we'll use the simple file name
        }
        return "Exception in file: " + path + " " + f.getName();
    }


    /*
     * @see org.argouml.uml.reveng.ImportInterface#getSuffixFilters()
     */
    public SuffixFilter[] getSuffixFilters() {
	SuffixFilter[] result = {FileFilters.JAVA_FILE_FILTER};
	return result;
    }

    /*
     * @see org.argouml.uml.reveng.ImportInterface#isParseable(java.io.File)
     */
    public boolean isParseable(File file) {
        return FileImportUtils.matchesSuffix(file, getSuffixFilters());
    }

    /*
     * @see org.argouml.moduleloader.ModuleInterface#getName()
     */
    public String getName() {
	return "Java";
    }

    /*
     * @see org.argouml.moduleloader.ModuleInterface#getInfo(int)
     */
    public String getInfo(int type) {
        switch (type) {
        case DESCRIPTION:
            return "This is a module for import from Java files.";
        case AUTHOR:
            return "Marcus Andersson, Thomas Neustupny, Andreas R�ckert";
        case VERSION:
            return "1.0";
        default:
            return null;
        }
    }

    /*
     * @see org.argouml.moduleloader.ModuleInterface#disable()
     */
    public boolean disable() {
        // We are permanently enabled
        return false;
    }

    /*
     * @see org.argouml.moduleloader.ModuleInterface#enable()
     */
    public boolean enable() {
        init();
        return true;
    }

    /**
     * Enable the importer.
     */
    public void init() {
	ImporterManager.getInstance().addImporter(this);
    }
    
    /*
     * @see org.argouml.uml.reveng.ImportInterface#getImportSettings()
     */
    public List getImportSettings() {
        return null;
    }

}
