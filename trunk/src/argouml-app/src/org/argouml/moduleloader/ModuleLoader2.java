/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2004-2008 The Regents of the University of California. All
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

package org.argouml.moduleloader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.application.api.AbstractArgoJPanel;
import org.argouml.application.api.Argo;
import org.argouml.i18n.Translator;


/**
 * This is the module loader that loads modules implementing the
 * ModuleInterface.<p>
 *
 * This is a singleton. There are convenience functions that are static
 * to access the module.<p>
 *
 * @stereotype singleton
 * @author Linus Tolke
 * @since 0.15.4
 */
public final class ModuleLoader2 {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ModuleLoader2.class.getName());

    /**
     * This map contains the module loader information about the module.<p>
     *
     * The keys is the list of available modules.
     */
    private Map<ModuleInterface, ModuleStatus> moduleStatus;

    /**
     * List of locations that we've searched and/or loaded modules
     * from.  This is for information purposes only to allow it to
     * be displayed in the settings Environment tab.
     */
    private List<String> extensionLocations = new ArrayList<String>();

    /**
     * The module loader object.
     */
    private static final ModuleLoader2 INSTANCE = new ModuleLoader2();

    /**
     * The prefix in URL:s that are files.
     */
    private static final String FILE_PREFIX = "file:";

    /**
     * The prefix in URL:s that are jars.
     */
    private static final String JAR_PREFIX = "jar:";

    /**
     * Class file suffix.
     */
    public static final String CLASS_SUFFIX = ".class";

    /**
     * Constructor for this object.
     */
    private ModuleLoader2() {
        moduleStatus = new HashMap<ModuleInterface, ModuleStatus>();
        computeExtensionLocations();
    }

    /**
     * Get hold of the instance of this object.
     *
     * @return the instance
     */
    public static ModuleLoader2 getInstance() {
        return INSTANCE;
    }

    /**
     * @return a list of Details panel tabs
     */
    List<AbstractArgoJPanel> getDetailsTabs() {
        List<AbstractArgoJPanel> result = new ArrayList<AbstractArgoJPanel>();
        for (ModuleInterface module : getInstance().availableModules()) {
            ModuleStatus status = moduleStatus.get(module);
            if (status == null) {
                continue;
            }
            if (status.isEnabled()) {
                if (module instanceof DetailsTabProvider) {
                    result.addAll(
                            ((DetailsTabProvider) module).getDetailsTabs());
                }
            }
        }
        return result;
    }
    /**
     * Return a collection of all available modules.
     *
     * @return A Collection of all available modules.
     */
    private Collection<ModuleInterface> availableModules() {
        return Collections.unmodifiableCollection(moduleStatus.keySet());
    }

    // Access methods for program infrastructure.
    /**
     * Enables all selected modules and disabling all modules not selected.<p>
     *
     * In short this attempts to make the modules obey their selection.<p>
     *
     * @param failingAllowed is <code>true</code> if enabling or disabling of
     *                       some of the modules is allowed to fail.
     */
    public static void doLoad(boolean failingAllowed) {
        getInstance().doInternal(failingAllowed);
    }

    // Access methods for modules that need to query about the status of
    // other modules.
    /**
     * Gets the loaded status for some other module.
     *
     * @return true if the module exists and is enabled.
     * @param name is the module name of the queried module
     */
    public static boolean isEnabled(String name) {
        return getInstance().isEnabledInternal(name);
    }

    // Access methods for the GUI that the user uses to enable and disable
    // modules.

    /**
     * Get a Collection with all the names.
     *
     * @return all the names.
     */
    public static Collection<String> allModules() {
        Collection<String> coll = new HashSet<String>();

        for (ModuleInterface mf : getInstance().availableModules()) {
            coll.add(mf.getName());
        }

        return coll;
    }

    /**
     * Get the selected.
     *
     * @param name The name of the module.
     * @return <code>true</code> if the module is selected.
     */
    public static boolean isSelected(String name) {
        return getInstance().isSelectedInternal(name);
    }

    /**
     * Get the selected.
     *
     * @see #isSelected(String)
     * @param name The name of the module.
     * @return <code>true</code> if the module is selected.
     */
    private boolean isSelectedInternal(String name) {
        Map.Entry<ModuleInterface, ModuleStatus> entry = findModule(name);

        if (entry != null) {
            ModuleStatus status = entry.getValue();

            if (status == null) {
                return false;
            }

            return status.isSelected();
        }
        return false;
    }

    /**
     * Set the selected value.
     *
     * @param name The name of the module.
     * @param value Selected or not.
     */
    public static void setSelected(String name, boolean value) {
        getInstance().setSelectedInternal(name, value);
    }

    /**
     * Set the selected value.
     *
     * @see #setSelected(String, boolean)
     * @param name The name of the module.
     * @param value Selected or not.
     */
    private void setSelectedInternal(String name, boolean value) {
        Map.Entry<ModuleInterface, ModuleStatus> entry = findModule(name);

        if (entry != null) {
            ModuleStatus status = entry.getValue();

            status.setSelected(value);
        }
    }

    /**
     * Create a description of the module based on the information provided
     * by the module itself.
     *
     * @param name The name of the module.
     * @return The description.
     */
    public static String getDescription(String name) {
        return getInstance().getDescriptionInternal(name);
    }

    /**
     * Create a description of the module based on the information provided
     * by the module itself.
     *
     * @see #getDescription(String)
     * @param name The name of the module.
     * @return The description.
     */
    private String getDescriptionInternal(String name) {
        Map.Entry<ModuleInterface, ModuleStatus> entry = findModule(name);

        if (entry == null) {
            throw new IllegalArgumentException("Module does not exist.");
        }

        ModuleInterface module = entry.getKey();
        StringBuffer sb = new StringBuffer();
        String desc = module.getInfo(ModuleInterface.DESCRIPTION);
        if (desc != null) {
            sb.append(desc);
            sb.append("\n\n");
        }
        String author = module.getInfo(ModuleInterface.AUTHOR);
        if (author != null) {
            sb.append("Author: ").append(author);
            sb.append("\n");
        }
        String version = module.getInfo(ModuleInterface.VERSION);
        if (version != null) {
            sb.append("Version: ").append(version);
            sb.append("\n");
        }
        return sb.toString();
    }


    // Access methods for the program infrastructure
    /**
     * Enables all selected modules.
     *
     * @param failingAllowed is true if this is not the last attempt at
     * turning on.
     */
    private void doInternal(boolean failingAllowed) {
        huntForModules();

        boolean someModuleSucceeded;
        do {
            someModuleSucceeded = false;

            for (ModuleInterface module : getInstance().availableModules()) {

                ModuleStatus status = moduleStatus.get(module);

                if (status == null) {
                    continue;
                }

                if (!status.isEnabled() && status.isSelected()) {
                    try {
                        if (module.enable()) {
                            someModuleSucceeded = true;
                            status.setEnabled();
                        }
                    }
                    // Catch all exceptions and errors, however severe
		    catch (Throwable e) {
                        LOG.log(Level.SEVERE,
                                "Exception or error while trying to "
                                + "enable module " + module.getName(),
                                e);
		    }
		} else if (status.isEnabled() && !status.isSelected()) {
		    try {
		        if (module.disable()) {
		            someModuleSucceeded = true;
		            status.setDisabled();
		        }
		    }
                    // Catch all exceptions and errors, however severe
		    catch (Throwable e) {
                        LOG.log(Level.SEVERE,
                                "Exception or error while trying to "
                                + "disable module " + module.getName(),
                                e);
		    }
		}
	    }
	} while (someModuleSucceeded);

	if (!failingAllowed) {
	    // Notify the user that the modules in the list that are selected
	    // but not enabled were not possible to enable and that are not
	    // selected that we cannot disable.
	    //
	    // Currently we just log this.
	    //
	    // TODO: We could eventually pop up some warning window.
	    //
	    for (ModuleInterface module : getInstance().availableModules()) {

		ModuleStatus status = moduleStatus.get(module);

		if (status == null) {
		    continue;
		}

		if (status.isEnabled() && status.isSelected()) {
		    continue;
		}

		if (!status.isEnabled() && !status.isSelected()) {
		    continue;
		}

		if (status.isSelected()) {
                    LOG.log(Level.WARNING,
                            "ModuleLoader was not able to enable module "
                            + module.getName());
		} else {
                    LOG.log(Level.WARNING,
                            "ModuleLoader was not able to disable module "
                            + module.getName());
		}
	    }
	}
    }

    /**
     * Gets the loaded status for some other module.
     *
     * @return true if the module exists and is enabled.
     * @param name is the module name of the queried module
     */
    private boolean isEnabledInternal(String name) {
	Map.Entry<ModuleInterface, ModuleStatus> entry = findModule(name);

	if (entry != null) {
	    ModuleStatus status = entry.getValue();

	    if (status == null) {
		return false;
	    }

	    return status.isEnabled();
	}
	return false;
    }


    /**
     * Return the ModuleInterface, ModuleStatus pair for the module
     * with the given name or <code>null</code> if there isn't any.
     *
     * @param name The given name.
     * @return A pair (Map.Entry).
     */
    private Map.Entry<ModuleInterface, ModuleStatus> findModule(String name) {
        for (Map.Entry<ModuleInterface, ModuleStatus> entry : moduleStatus
                .entrySet()) {
            ModuleInterface module = entry.getKey();
            if (name.equalsIgnoreCase(module.getName())) {
                return entry;
            }
        }
	return null;
    }

    /**
     * Tries to find as many available modules as possible.<p>
     *
     * As the modules are found they are appended to {@link #moduleStatus}.<p>
     */
    private void huntForModules() {
        huntForModulesFromExtensionDir();
        // TODO: huntForModulesFromJavaWebStart();

        // Load modules specified by a System property.
        // Modules specified by a system property is for
        // running modules from within Eclipse and running
        // from Java Web Start.
        String listOfClasses = System.getProperty("argouml.modules");
        if (listOfClasses != null) {
            StringTokenizer si = new StringTokenizer(listOfClasses, ";");
            while (si.hasMoreTokens()) {
                String className = si.nextToken();
                try {
                    addClass(className);
                } catch (ClassNotFoundException e) {
                    LOG.log(Level.SEVERE,
                            "Could not load module from class " + className);
                }
            }
        }
    }

    /**
     * Find and enable modules from our "ext" directory and from the
     * directory specified in "argo.ext.dir".<p>
     */
    private void huntForModulesFromExtensionDir() {
        for (String location : extensionLocations) {
            huntModulesFromNamedDirectory(location);
        }
    }

    /**
     * This does a calculation of where our "ext" directory is.
     * TODO: We should eventually make sure that this calculation is
     *       only present in one place in the code and not several.
     */
    private void computeExtensionLocations() {
        // Use a little trick to find out where Argo is being loaded from.
        // TODO: Use a different resource here. ARGOINI is unused and deprecated
        String extForm = getClass().getResource(Argo.ARGOINI).toExternalForm();
        String argoRoot =
            extForm.substring(0,
                              extForm.length() - Argo.ARGOINI.length());

        // If it's a jar, clean it up and make it look like a file url
        if (argoRoot.startsWith(JAR_PREFIX)) {
            argoRoot = argoRoot.substring(JAR_PREFIX.length());
            if (argoRoot.endsWith("!")) {
                argoRoot = argoRoot.substring(0, argoRoot.length() - 1);
            }
        }

        String argoHome = null;

        if (argoRoot != null) {
            LOG.log(Level.INFO, "argoRoot is {0}", argoRoot);
            if (argoRoot.startsWith(FILE_PREFIX)) {
                argoHome =
                    new File(argoRoot.substring(FILE_PREFIX.length()))
                        .getAbsoluteFile().getParent();
            } else {
                argoHome = new File(argoRoot).getAbsoluteFile().getParent();
            }

            try {
                argoHome = java.net.URLDecoder.decode(argoHome,
                        Argo.getEncoding());
            } catch (UnsupportedEncodingException e) {
                LOG.log(Level.WARNING,
                        "Encoding "
                        + Argo.getEncoding()
                        + " is unknown.");
            }

            LOG.log(Level.INFO, "argoHome is {0}", argoHome);
        }

        if (argoHome != null) {
            String extdir;
            if (argoHome.startsWith(FILE_PREFIX)) {
                extdir = argoHome.substring(FILE_PREFIX.length())
                        + File.separator + "ext";
            } else {
                extdir = argoHome + File.separator + "ext";
            }
            extensionLocations.add(extdir);
        }

        String extdir = System.getProperty("argo.ext.dir");
        if (extdir != null) {
            extensionLocations.add(extdir);
        }
    }

    /**
     * Get the list of locations that we've loaded extension modules from.
     * @return A list of the paths we've loaded from.
     */
    public List<String> getExtensionLocations() {
        return Collections.unmodifiableList(extensionLocations);
    }

    /**
     * Find and enable a module from a given directory.
     *
     * @param dirname The name of the directory.
     */
    private void huntModulesFromNamedDirectory(String dirname) {
	File extensionDir = new File(dirname);
	if (extensionDir.isDirectory()) {
	    File[] files = extensionDir.listFiles(new JarFileFilter());
	    for (File file : files) {
		JarFile jarfile = null;
		// Try-catch only the JarFile instantiation so we
		// don't accidentally mask anything in ArgoJarClassLoader
		// or processJarFile.
		try {
		    jarfile = new JarFile(file);
		    if (jarfile != null) {
	                ClassLoader classloader =
			    new URLClassLoader(new URL[] {
				file.toURI().toURL(),
			    }, getClass().getClassLoader());
	                try {
	                    processJarFile(classloader, file);
	                } catch (ClassNotFoundException e) {
                            LOG.log(Level.SEVERE, "The class is not found.", e);
	                    return;
	                }
		    }
		} catch (IOException ioe) {
                    LOG.log(Level.SEVERE, "Cannot open Jar file " + file, ioe);
		}
	    }
	}
    }

    /**
     * Check a jar file for an ArgoUML extension/module.<p>
     *
     * If there isn't a manifest or it isn't readable, we fall back to using
     * the raw JAR entries.
     *
     * @param classloader The classloader to use.
     * @param file The file to process.
     * @throws ClassNotFoundException if the manifest file contains a class
     *         that doesn't exist.
     */
    private void processJarFile(ClassLoader classloader, File file)
        throws ClassNotFoundException {

        LOG.log(Level.INFO, "Opening jar file {0}", file);
        JarFile jarfile;
	try {
	    jarfile = new JarFile(file);
	} catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to open " + file, e);
            return;
	}

        Manifest manifest;
        try {
            manifest = jarfile.getManifest();
            if (manifest == null) {
                // We expect all extensions to have a manifest even though we
                // can operate without one if necessary.
                LOG.log(Level.WARNING,file + " does not have a manifest");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to read manifest of " + file, e);
            return;
        }

        // TODO: It is a performance drain to load all classes at startup time.
        // They should be lazy loaded when needed.  Instead of scanning all
        // classes for ones which implement our loadable module interface, we
        // should use a manifest entry or a special name/name pattern that we
        // look for to find the single main module class to load here.  - tfm

        boolean loadedClass = false;
        if (manifest == null) {
            Enumeration<JarEntry> jarEntries = jarfile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                loadedClass =
                        loadedClass
                                | processEntry(classloader, entry.getName());
            }
        } else {
            Map<String, Attributes> entries = manifest.getEntries();
            for (String key : entries.keySet()) {
                // Look for our specification
                loadedClass =
                    loadedClass
                            | processEntry(classloader, key);
            }
        }

        // Add this to search list for I18N properties
        // (Done for both modules & localized property file sets)
        Translator.addClassLoader(classloader);

        // If it didn't have a loadable module class and it doesn't look like
        // a localized property set, warn the user that something funny is in
        // their extension directory
        if (!loadedClass && !file.getName().contains("argouml-i18n-")) {
            LOG.log(Level.SEVERE,
                    "Failed to find any loadable ArgoUML modules in jar "
                    + file);
        }
    }

    /**
     * Process a JAR file entry, attempting to load anything that looks like a
     * Java class.
     *
     * @param classloader
     *            the classloader to use when loading the class
     * @param cname
     *            the class name
     * @throws ClassNotFoundException
     * @return true if class was a module class and loaded successfully
     */
    private boolean processEntry(ClassLoader classloader, String cname)
        throws ClassNotFoundException {
        if (cname.endsWith(CLASS_SUFFIX)) {
            int classNamelen = cname.length() - CLASS_SUFFIX.length();
            String className = cname.substring(0, classNamelen);
            className = className.replace('/', '.');
            return addClass(classloader, className);
        }
        return false;
    }

    /**
     * Add a class from the current class loader.
     *
     * @param classname The name of the class (including package).
     * @throws ClassNotFoundException if the class classname is not found.
     */
    public static void addClass(String classname)
        throws ClassNotFoundException {

        getInstance().addClass(ModuleLoader2.class.getClassLoader(),
			       classname);
    }

    /**
     * Try to load a module from the given ClassLoader.<p>
     *
     * Only add it as a module if it is a module (i.e. it implements the
     * {@link ModuleInterface} interface.
     *
     * @param classLoader The ClassLoader to load from.
     * @param classname The name.
     * @throws ClassNotFoundException if the class classname is not found.
     */
    private boolean addClass(ClassLoader classLoader, String classname)
        throws ClassNotFoundException {

        LOG.log(Level.INFO, "Loading module " + classname);
        Class moduleClass;
        try {
            moduleClass = classLoader.loadClass(classname);
        } catch (UnsupportedClassVersionError e) {
            LOG.log(Level.SEVERE,
                    "Unsupported Java class version for " + classname);
            return false;
        } catch (NoClassDefFoundError e) {
            LOG.log(Level.SEVERE,
                    "Unable to find required class while loading "
                    + classname + " - may indicate an obsolete"
                    + " extension module or an unresolved dependency",
                    e);
            return false;
        } catch (Throwable e) {
            if (e instanceof ClassNotFoundException) {
                throw (ClassNotFoundException) e;
            }
            LOG.log(Level.SEVERE,
                    "Unexpected error while loading " + classname,
                    e);
            return false;
        }

        if (!ModuleInterface.class.isAssignableFrom(moduleClass)) {
            LOG.log(Level.FINE, "The class {0} is not a module.", classname);
            return false;
        }

        Constructor defaultConstructor;
        try {
            defaultConstructor =
                    moduleClass.getDeclaredConstructor(new Class[] {});
        } catch (SecurityException e) {
            LOG.log(Level.SEVERE,
                    "The default constructor for class " + classname
                    + " is not accessable.",
                    e);
            return false;
        } catch (NoSuchMethodException e) {
            LOG.log(Level.SEVERE,
                    "The default constructor for class " + classname
                    + " is not found.",
                    e);
            return false;
        } catch (NoClassDefFoundError e) {
            LOG.log(Level.SEVERE,
                    "Unable to find required class while loading "
                    + classname + " - may indicate an obsolete"
                    + " extension module or an unresolved dependency",
                    e);
            return false;
        } catch (Throwable e) {
            LOG.log(Level.SEVERE,
                    "Unexpected error while loading " + classname,
                    e);
            return false;
        }

        if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
            LOG.log(Level.SEVERE,
                    "The default constructor for class " + classname
                    + " is not public.  Not loaded.");
            return false;
        }
        Object moduleInstance;
        try {
            moduleInstance = defaultConstructor.newInstance(new Object[]{});
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE,
                    "The constructor for class " + classname
                    + " is called with incorrect argument.",
                    e);
            return false;
        } catch (InstantiationException e) {
            LOG.log(Level.SEVERE,
                    "The constructor for class " + classname
                    + " threw an exception.",
                    e);
            return false;
        } catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE,
                    "The constructor for class " + classname
                    + " is not accessible.",
                    e);
            return false;
        } catch (InvocationTargetException e) {
            LOG.log(Level.SEVERE,
                    "The constructor for class " + classname
                    + " cannot be called.",
                    e);
            return false;
        } catch (NoClassDefFoundError e) {
            LOG.log(Level.SEVERE,
                    "Unable to find required class while instantiating "
                    + classname + " - may indicate an obsolete"
                    + " extension module or an unresolved dependency",
                    e);
            return false;
        } catch (Throwable e) {
            LOG.log(Level.SEVERE,
                    "Unexpected error while instantiating " + classname,
                    e);
            return false;
        }

        // The following check should have been satisfied before we
        // instantiated the module, but double check again
        if (!(moduleInstance instanceof ModuleInterface)) {
            LOG.log(Level.SEVERE,
                    "The class " + classname + " is not a module.");
            return false;
        }
        ModuleInterface mf = (ModuleInterface) moduleInstance;

        addModule(mf);
        LOG.log(Level.INFO, "Succesfully loaded module {0}", classname);
        return true;
    }

    /**
     * Add a newly found module to {@link #moduleStatus}. If we already
     * know about it, don't add it.
     *
     * @param mf The module to add.
     */
    private void addModule(ModuleInterface mf) {
	// Since there is no way to compare the objects as equal,
	// we have to search through the list at this point.
        for (ModuleInterface foundMf : moduleStatus.keySet()) {
	    if (foundMf.getName().equals(mf.getName())) {
		return;
	    }
	}

	// We havn't found it. Add it.
	ModuleStatus ms = new ModuleStatus();

	// Enable it.
	// TODO: This by default selects all modules that are found.
	//       Eventually we would rather obey a default either from the
	//       modules themselves, from how they are found, and also
	//       have information on what modules are selected from the
	//       configuration.
	ms.setSelected();

	moduleStatus.put(mf, ms);
    }


    /**
     * The file filter that selects Jar files.
     */
    static class JarFileFilter implements FileFilter {
	/*
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
	    return (pathname.canRead()
		    && pathname.isFile()
		    && pathname.getPath().toLowerCase().endsWith(".jar"));
	}
    }
}


/**
 * Status for each of the available modules. This is created in one copy per
 * available module.
 */
class ModuleStatus {
    /**
     * If the module is enabled.
     */
    private boolean enabled;

    /**
     * If the module is selected.
     */
    private boolean selected;

    /**
     * Tells if the module is enabled or not.
     *
     * @return true if the module is enabled.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Setter for enabled.
     */
    public void setEnabled() {
	enabled = true;
    }

    /**
     * Setter for enabled.
     */
    public void setDisabled() {
	enabled = false;
    }

    /**
     * Tells if the module is selected by the user or not.
     *
     * @return true if it is selected.
     */
    public boolean isSelected() {
	return selected;
    }


    /**
     * Setter for selected.
     */
    public void setSelected() {
	selected = true;
    }

    /**
     * Setter for selected.
     */
    public void setUnselect() {
	selected = false;
    }

    /**
     * Setter for selected.
     *
     * @param value The value to set.
     */
    public void setSelected(boolean value) {
	if (value) {
	    setSelected();
	} else {
	    setUnselect();
	}
    }
}
