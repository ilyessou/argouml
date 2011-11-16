/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006, 2009 The Regents of the University of California. All
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

package org.argouml.uml.reveng;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.argouml.application.api.Argo;
import org.argouml.cognitive.Designer;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.taskmgmt.ProgressMonitor;
import org.argouml.ui.explorer.ExplorerEventAdaptor;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.uml.diagram.static_structure.layout.ClassdiagramLayouter;
import org.argouml.util.SuffixFilter;
import org.tigris.gef.base.Globals;

/**
 * Source language import class - GUI independent superclass.
 * <p>
 * Specific Swing and SWT/Eclipse importers will extend this class.
 * <p>
 * <em>NOTE:</em>Any change to the public API here must be tested in both Swing
 * (standalone ArgoUML) and Eclipse (ArgoEclipse) environments.
 * 
 * @author Tom Morris
 */
public abstract class ImportCommon implements ImportSettingsInternal {

    /**
     * The % maximum progress required to preparing for import.
     */
    protected static final int MAX_PROGRESS_PREPARE = 1;

    /**
     * The % maximum progress required to import.
     */
    protected static final int MAX_PROGRESS_IMPORT = 99;

    protected static final int MAX_PROGRESS = MAX_PROGRESS_PREPARE
            + MAX_PROGRESS_IMPORT;
    /**
     * keys are module name, values are PluggableImport instance.
     */
    private Hashtable<String, ImportInterface> modules;

    /**
     * Current language module.
     */
    private ImportInterface currentModule;


    /**
     * Imported directory.
     */
    private String srcPath;

    /**
     * Create a interface to the current diagram.
     */
    private DiagramInterface diagramInterface;

    private File[] selectedFiles;
    
    private SuffixFilter selectedSuffixFilter;

    protected ImportCommon() {
        super();
        modules = new Hashtable<String, ImportInterface>();

        for (ImportInterface importer : ImporterManager.getInstance()
                .getImporters()) {
            modules.put(importer.getName(), importer);
        }
        if (modules.isEmpty()) {
            throw new RuntimeException("Internal error. "
                    + "No importer modules found.");
        }
        // "Java" is the default module for historical reasons,
        // but it's not required to be there
        currentModule = modules.get("Java");
        if (currentModule == null) {
            currentModule = modules.elements().nextElement();
        }
    }

    /*
     * @see org.argouml.uml.reveng.ImportSettings#getImportLevel()
     */
    public abstract int getImportLevel();


    /**
     * Compute and cache the current diagram interface.
     */
    protected void initCurrentDiagram() {
        diagramInterface = getCurrentDiagram();
    }

    /**
     * Set target diagram.<p>
     *
     * @return selected diagram, if it is class diagram,
     * else return null.
     */
    private DiagramInterface getCurrentDiagram() {
        DiagramInterface result = null;
        if (Globals.curEditor().getGraphModel()
                instanceof ClassDiagramGraphModel) {
            result =  new DiagramInterface(Globals.curEditor());
        } else {
            Project p =  ProjectManager.getManager().getCurrentProject();
            TargetManager.getInstance().setTarget(p.getInitialTarget());
            // the previous line helps, but we better check again:
            if (Globals.curEditor().getGraphModel()
                    instanceof ClassDiagramGraphModel) {
                result =  new DiagramInterface(Globals.curEditor());
            }
        }
        return result;
    }

    /*
     * @see org.argouml.uml.reveng.ImportSettings#getInputSourceEncoding()
     */
    public abstract String getInputSourceEncoding();


    /**
     * Get the files.  We generate it based on their specified
     * file suffixes.
     * @param monitor progress monitor which can be used to cancel long running 
     * request
     * @return the list of files to be imported
     */
    protected List<File> getFileList(ProgressMonitor monitor) {
        List<File> files = Arrays.asList(getSelectedFiles());
        if (files.size() == 1) {
            File file = files.get(0);
            SuffixFilter suffixFilters[] = {selectedSuffixFilter};
            if (suffixFilters[0] == null) {
                // not a SuffixFilter selected, so we take all
                suffixFilters = currentModule.getSuffixFilters();
            }
            files =
                FileImportUtils.getList(
                        file, isDescendSelected(),
                        suffixFilters, monitor);
            if (file.isDirectory()) {
                setSrcPath(file.getAbsolutePath());
            } else {
                setSrcPath(null);
            }
        }


        if (isChangedOnlySelected()) {
            // filter out all unchanged files
            Object model =
                ProjectManager.getManager().getCurrentProject().getModel();
            for (int i = files.size() - 1; i >= 0; i--) {
                File f = files.get(i);
                String fn = f.getAbsolutePath();
                String lm = String.valueOf(f.lastModified());
                
                if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
                    // TODO: Not yet working for UML2
                    if (lm.equals(
                            Model.getFacade().getTaggedValueValue(model, fn))) {
                        files.remove(i);
                    }
                }
            }
        }

        return files;
    }

    /**
     * Set path for processed directory.
     *
     * @param path the given path
     */
    public void setSrcPath(String path) {
        srcPath = path;
    }

    /**
     * @return path for processed directory.
     */
    public String getSrcPath() {
        return srcPath;
    }

    /*
     * Create a TaggedValue with a tag/type matching our source module
     * filename and a value of the file's last modified timestamp.
     *
     * TODO: This functionality needs to be moved someplace useful if
     * it's needed, otherwise it can be deleted. - tfm - 20070217
     */
    private void setLastModified(Project project, File file) {
        // set the lastModified value
        String fn = file.getAbsolutePath();
        String lm = String.valueOf(file.lastModified());
        if (lm != null) {
            Model.getCoreHelper()
                .setTaggedValue(project.getModel(), fn, lm);
        }
    }

    /*
     * @see org.argouml.uml.reveng.ImportSettings#isCreateDiagramsSelected()
     */
    public abstract boolean isCreateDiagramsSelected();

    /*
     * @see org.argouml.uml.reveng.ImportSettings#isMinimiseFigsSelected()
     */
    public abstract boolean isMinimizeFigsSelected();

    /*
     * @see org.argouml.uml.reveng.ImportSettingsInternal#isDiagramLayoutSelected()
     */
    public abstract boolean isDiagramLayoutSelected();

    /*
     * @see org.argouml.uml.reveng.ImportSettingsInternal#isDescendSelected()
     */
    public abstract boolean isDescendSelected();

    /*
     * @see org.argouml.uml.reveng.ImportSettingsInternal#isChangedOnlySelected()
     */
    public abstract boolean isChangedOnlySelected();

    /**
     * Gets the specified import module.
     * 
     * @param importerName The import module's name
     * @return The found import module, otherwise null
     */
    public ImportInterface getImporter(String importerName) {
        return getModules().get(importerName);
    }

    /**
     * Sets the files that will be imported.
     * 
     * @param files The array of files.
     */
    public void setFiles(File[] files) {
        if (files != null) {
            setSelectedFiles(files);
        }
    }

    protected Hashtable<String, ImportInterface> getModules() {
        return modules;
    }

    protected void setSelectedFiles(final File[] files) {
        selectedFiles = files;
    }

    /**
     * Set the selected (file) suffix filter.
     * 
     * @param suffixFilter the (file) suffix filter
     */
    protected void setSelectedSuffixFilter(final SuffixFilter suffixFilter) {
        selectedSuffixFilter = suffixFilter;
    }

    protected File[] getSelectedFiles() {
	File[] copy = new File[selectedFiles.length];
	for (int i = 0; i < selectedFiles.length; i++) {
	    copy[i] = selectedFiles[i];
	}
	return copy;
        //return Arrays.copyOf(selectedFiles, selectedFiles.length);
    }
    
    /**
     * Sets the current import module.
     * 
     * @param module
     */
    public void setCurrentModule(ImportInterface module) {
        currentModule = module;
    }

    protected ImportInterface getCurrentModule() {
        return currentModule;
    }

    /**
     * Returns the possible languages in which the user can import the sources.
     * @return a list of Strings with the names of the languages available
     */
    public List<String> getLanguages() {
        return Collections.unmodifiableList(
                new ArrayList<String>(modules.keySet()));
    }

    /**
     * The flag for: descend directories recursively.
     * This should be asked by the GUI for initialization.
     * @return the flag stored in KEY_IMPORT_GENERAL_SETTINGS_FLAGS key or
     * true if this is null.
     */
    public boolean isDescend() {
        String flags =
                Configuration.getString(
                        Argo.KEY_IMPORT_GENERAL_SETTINGS_FLAGS);
        if (flags != null && flags.length() > 0) {
            StringTokenizer st = new StringTokenizer(flags, ",");
            if (st.hasMoreTokens() && st.nextToken().equals("false")) {
                return false;
            }
        }
        return true;
    }

    /**
     * The flag for: changed/new files only.
     * This should be asked by the GUI for initialization.
     * @return the flag stored in KEY_IMPORT_GENERAL_SETTINGS_FLAGS key or
     * true if this is null.
     */
    public boolean isChangedOnly() {
        String flags =
                Configuration.getString(Argo.KEY_IMPORT_GENERAL_SETTINGS_FLAGS);
        if (flags != null && flags.length() > 0) {
            StringTokenizer st = new StringTokenizer(flags, ",");
            skipTokens(st, 1);
            if (st.hasMoreTokens() && st.nextToken().equals("false")) {
                return false;
            }
        }
        return true;
    }

    /**
     * The flag for: create diagrams from imported code.
     * This should be asked by the GUI for initialization.
     * @return the flag stored in KEY_IMPORT_GENERAL_SETTINGS_FLAGS key or
     * true if this is null.
     */
    public boolean isCreateDiagrams() {
        String flags =
                Configuration.getString(
                        Argo.KEY_IMPORT_GENERAL_SETTINGS_FLAGS);
        if (flags != null && flags.length() > 0) {
            StringTokenizer st = new StringTokenizer(flags, ",");
            skipTokens(st, 2);
            if (st.hasMoreTokens() && st.nextToken().equals("false")) {
                return false;
            }
        }
        return true;
    }

    /**
     * The flag for: minimise class icons in diagrams.
     * This should be asked by the GUI for initialization.
     * @return the flag stored in KEY_IMPORT_GENERAL_SETTINGS_FLAGS key or
     * true if this is null.
     */
    public boolean isMinimizeFigs() {
        String flags =
                Configuration.getString(
                        Argo.KEY_IMPORT_GENERAL_SETTINGS_FLAGS);
        if (flags != null && flags.length() > 0) {
            StringTokenizer st = new StringTokenizer(flags, ",");
            skipTokens(st, 3);
            if (st.hasMoreTokens() && st.nextToken().equals("false")) {
                return false;
            }
        }
        return true;
    }

    private void skipTokens(StringTokenizer st, int count) {
        for (int i = 0; i < count; i++) {
            if (st.hasMoreTokens()) {
                st.nextToken();
            }
        }
    }

    /**
     * The flag for: perform automatic diagram layout.
     * This should be asked by the GUI for initialization.
     * @return the flag stored in KEY_IMPORT_GENERAL_SETTINGS_FLAGS key or
     * true if this is null.
     */
    public boolean isDiagramLayout() {
        String flags =
                Configuration.getString(
                        Argo.KEY_IMPORT_GENERAL_SETTINGS_FLAGS);
        if (flags != null && flags.length() > 0) {
            StringTokenizer st = new StringTokenizer(flags, ",");
            skipTokens(st, 4);
            if (st.hasMoreTokens() && st.nextToken().equals("false")) {
                return false;
            }
        }
        return true;
    }

    /**
     * The default encoding. This should be asked by the GUI for
     * initialization.
     * @return the encoding stored in Argo.KEY_INPUT_SOURCE_ENCODING key or if
     * this is null the default system encoding
     */
    public String getEncoding() {
        String enc = Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING);
        if (enc == null || enc.trim().equals("")) { //$NON-NLS-1$
            enc = System.getProperty("file.encoding"); //$NON-NLS-1$
        }

        return enc;
    }


    /**
     * Layouts the diagrams.
     *
     * @param monitor
     *            the progress meter.  Null if not progress updates desired.
     * @param startingProgress
     *            the actual progress until now
     */
    public void layoutDiagrams(ProgressMonitor monitor, int startingProgress) {

        if (diagramInterface == null) {
            return;
        }
//        if (monitor != null) {
//            monitor.updateSubTask(ImportsMessages.layoutingAction);
//        }
        List<ArgoDiagram> diagrams = diagramInterface.getModifiedDiagramList();
        int total = startingProgress + diagrams.size()
                / 10;
        for (int i = 0; i < diagrams.size(); i++) {
            ArgoDiagram diagram = diagrams.get(i);
            ClassdiagramLayouter layouter = new ClassdiagramLayouter(diagram);
            layouter.layout();
            int act = startingProgress + (i + 1) / 10;
            int progress = MAX_PROGRESS_PREPARE
                    + MAX_PROGRESS_IMPORT * act / total;
            if (monitor != null) {
                monitor.updateProgress(progress);
            }
//          iss.setValue(countFiles + (i + 1) / 10);
        }

    }


    /**
     * Import the selected source files. It calls the actual
     * parser methods depending on the type of the file.
     *
     * @param monitor
     *            a ProgressMonitor to both receive progress updates and to be
     *            polled for user requests to cancel.
     */
    public void doImport(ProgressMonitor monitor) {
        // Roughly equivalent to and derived from old Import.doFile()
        monitor.setMaximumProgress(MAX_PROGRESS);
        int progress = 0;
        monitor.updateSubTask(Translator.localize("dialog.import.preImport"));
        List<File> files = getFileList(monitor);
        progress += MAX_PROGRESS_PREPARE;
        monitor.updateProgress(progress);
        if (files.size() == 0) {
            monitor.notifyNullAction();
            return;
        }
        Model.getPump().stopPumpingEvents();
        boolean criticThreadWasOn = Designer.theDesigner().getAutoCritique();
        if (criticThreadWasOn) {
            Designer.theDesigner().setAutoCritique(false);
        }
        try {
            doImportInternal(files, monitor, progress);
        } finally {
            if (criticThreadWasOn) {
                Designer.theDesigner().setAutoCritique(true);
            }
            // TODO: Send an event instead of calling Explorer directly
            ExplorerEventAdaptor.getInstance().structureChanged();
            Model.getPump().startPumpingEvents();
        }
    }


    /**
     * Do the import.
     * @param filesLeft the files to parse
     * @param monitor the progress meter
     * @param progress the actual progress until now
     */
    private void doImportInternal(List<File> filesLeft,
            final ProgressMonitor monitor, int progress) {
        Project project =  ProjectManager.getManager().getCurrentProject();
        initCurrentDiagram();
        final StringBuffer problems = new StringBuffer();
        Collection newElements = new HashSet();
        
        try {
            newElements.addAll(currentModule.parseFiles(
                    project, filesLeft, this, monitor));
        } catch (Exception e) {
            problems.append(printToBuffer(e));
        }
        // New style importers don't create diagrams, so we'll do it
        // based on the list of newElements that they created
        if (isCreateDiagramsSelected() && diagramInterface != null) {
            addFiguresToDiagrams(newElements);
        }

        // Do layout even if problems occurred during import
        if (isDiagramLayoutSelected()) {
            // TODO: Monitor is getting dismissed before layout is complete
            monitor.updateMainTask(
                    Translator.localize("dialog.import.postImport"));
            monitor.updateSubTask(
                    Translator.localize("dialog.import.layoutAction"));
            layoutDiagrams(monitor, progress + filesLeft.size());
        }
        
        // Add messages from caught exceptions
        if (problems != null && problems.length() > 0) {
            monitor.notifyMessage(
                    Translator.localize(
                            "dialog.title.import-problems"), //$NON-NLS-1$
                            Translator.localize(
                            "label.import-problems"),        //$NON-NLS-1$
                            problems.toString());
        }
        
        monitor.updateMainTask(Translator.localize("dialog.import.done"));
        monitor.updateSubTask(""); //$NON-NLS-1$
        monitor.updateProgress(MAX_PROGRESS);

    }


    /**
     * Create diagram figures for a collection of model elements.
     *
     * @param newElements
     *            the collection of elements for which figures should be
     *            created.
     */
    private void addFiguresToDiagrams(Collection newElements) {
        for (Object element : newElements) {
            if (Model.getFacade().isAClassifier(element)
                    || Model.getFacade().isAPackage(element)) {

                Object ns = Model.getFacade().getNamespace(element);
                if (ns == null) {
                    diagramInterface.createRootClassDiagram();
                } else {
                    String packageName = getQualifiedName(ns);
                    // Select the correct diagram (implicitly creates it)
                    if (packageName != null
                            && !packageName.equals("")) {
                        diagramInterface.selectClassDiagram(ns,
                                packageName);
                    } else {
                        diagramInterface.createRootClassDiagram();
                    }
                    // Add the element to the diagram
                    if (Model.getFacade().isAInterface(element)) {
                        diagramInterface.addInterface(element,
                                isMinimizeFigsSelected());
                    } else if (Model.getFacade().isAClass(element)) {
                        diagramInterface.addClass(element,
                                isMinimizeFigsSelected());
                    } else if (Model.getFacade().isAPackage(element)) {
                        diagramInterface.addPackage(element);
                    }
                }
            }
        }
    }

    /**
     * Return the fully qualified name of a model element in Java (dot
     * separated) format.
     * <p>
     * TODO: We really need a language independent format here. Perhaps the list
     * of names that form the hierarchy? - tfm
     */
    private String getQualifiedName(Object element) {
        StringBuffer sb = new StringBuffer();
        
        Object ns = element;
        while (ns != null) {
            String name = Model.getFacade().getName(ns);
            if (name == null) {
                name = "";
            }
            sb.insert(0, name);
            ns = Model.getFacade().getNamespace(ns);
            if (ns != null) {
                sb.insert(0, ".");
            }
        }
        return sb.toString();
    }

    /*
     * Print an exception trace to a string buffer
     */
    private StringBuffer printToBuffer(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.getBuffer();
    }

}
