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

package org.argouml.application;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.argouml.application.api.Argo;
import org.argouml.application.api.CommandLineInterface;
import org.argouml.application.api.Configuration;
import org.argouml.application.security.ArgoAwtExceptionHandler;
import org.argouml.cognitive.AbstractCognitiveTranslator;
import org.argouml.cognitive.Designer;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.moduleloader.ModuleLoader2;
import org.argouml.persistence.PersistenceManager;
import org.argouml.ui.Actions;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.SplashScreen;
import org.argouml.ui.cmd.ActionExit;
import org.argouml.util.logging.SimpleTimer;
import org.tigris.gef.util.Util;

/**
 * Here it all starts...
 *
 */
public class Main {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class);

    ////////////////////////////////////////////////////////////////
    // constants

    /**
     * The location of the default logging configuration (.lcf) file.
     */
    public static final String DEFAULT_LOGGING_CONFIGURATION =
        "org/argouml/resource/default.lcf";


    ////////////////////////////////////////////////////////////////
    // static variables

    private static Vector postLoadActions = new Vector();

    ////////////////////////////////////////////////////////////////
    // main

    /**
     * The main entry point of ArgoUML.
     * @param args command line parameters
     */
    public static void main(String[] args) {
	LOG.info("ArgoUML Started.");

        checkJVMVersion();
        checkHostsFile();

        // Force the configuration to load
        Configuration.load();

        // Synchronize the startup directory
        String directory = Argo.getDirectory();
        org.tigris.gef.base.Globals.setLastDirectory(directory);

        // create an anonymous class as a kind of adaptor for the cognitive
        // System to provide proper translation/i18n.
        org.argouml.cognitive.Translator.setTranslator(
                new AbstractCognitiveTranslator() {
            public String i18nlocalize(String key) {
                return Translator.localize(key);
            }

            public String i18nmessageFormat(String key, Object[] iArgs) {
                return Translator.messageFormat(key, iArgs);
            }
        });

        // then, print out some version info for debuggers...
        org.argouml.util.Tools.logVersionInfo();

        SimpleTimer st = new SimpleTimer("Main.main");
        st.mark("arguments");

        /* set properties for application behaviour */
        System.setProperty("gef.imageLocation", "/org/argouml/Images");

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /* FIX: set the application name for Mac OS X */
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
			   "ArgoUML");


        boolean doSplash = Configuration.getBoolean(Argo.KEY_SPLASH, true);
	// TODO: document use. Ref. 1/2
        boolean preload = Configuration.getBoolean(Argo.KEY_PRELOAD, true);
        boolean reloadRecent =
            Configuration.getBoolean(Argo.KEY_RELOAD_RECENT_PROJECT, false);
	boolean batch = false;
	List commands = new ArrayList();

        String projectName = null;

        //--------------------------------------------
        // Parse command line args:
        // The assumption is that all options precede
        // the name of a project file to load.
        //--------------------------------------------

        String theTheme = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
		String themeName =
                    LookAndFeelMgr.getInstance()
                        .getThemeClassNameFromArg(args[i]);
                if (themeName != null) {
		    theTheme = themeName;
                } else if (
                    args[i].equalsIgnoreCase("-help")
                        || args[i].equalsIgnoreCase("-h")
                        || args[i].equalsIgnoreCase("--help")
                        || args[i].equalsIgnoreCase("/?")) {
                    printUsage();
                    System.exit(0);
                } else if (args[i].equalsIgnoreCase("-nosplash")) {
                    doSplash = false;
                } else if (args[i].equalsIgnoreCase("-noedem")) {
		    // TODO: document use. Ref. 2/2
                    /* useEDEM = false*/;
                } else if (args[i].equalsIgnoreCase("-nopreload")) {
                    preload = false;
                } else if (args[i].equalsIgnoreCase("-norecentfile")) {
                    reloadRecent = false;
                } else if (args[i].equalsIgnoreCase("-command")
			   && i + 1 < args.length) {
		    commands.add(args[i + 1]);
		    i++;
                } else if (args[i].equalsIgnoreCase("-locale")
                           && i + 1 < args.length) {
                    Translator.setLocale(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-batch")) {
                    batch = true;
                } else {
                    System.err.println(
                        "Ignoring unknown/incomplete option '" + args[i] + "'");
                }
            } else {
                if (projectName == null) {
                    System.out.println(
                        "Setting projectName to '" + args[i] + "'");
                    projectName = args[i];
                }
            }
        }

        // Initialize the Model subsystem
        if (!Model.isInitiated()) {
            System.err.println("Model not initated correctly. See log.");
            System.exit(1);
            return;
        }

	// The reason the gui is initialized before the commands are run
	// is that some of the commands will use the projectbrowser.
	st.mark("initialize gui");
        SplashScreen splash = initializeGUI(doSplash && !batch, theTheme);

        // Register the default notation.
        org.argouml.uml.generator.GeneratorDisplay.getInstance();

        // Initialize the UMLActions
        Actions.getInstance();

        if (reloadRecent && projectName == null) {
            // If no project was entered on the command line,
            // try to reload the most recent project if that option is true
            String s =
                Configuration.getString(Argo.KEY_MOST_RECENT_PROJECT_FILE, "");
            if (!("".equals(s))) {
                File file = new File(s);
                if (file.exists()) {
                    LOG.info("Re-opening project " + s);
                    projectName = s;
                } else {
                    LOG.warn(
                        "Cannot re-open " + s + " because it does not exist");
                }
            }
        }

        URL urlToOpen = null;

        if (projectName != null) {
            projectName =
                PersistenceManager.getInstance().fixExtension(projectName);
            urlToOpen = projectUrl(projectName, urlToOpen);
        }

	ProjectBrowser pb = ProjectBrowser.getInstance();

	if (batch) {
	    performCommands(commands);
	    commands = null;

	    System.out.println("Exiting because we are running in batch.");
	    new ActionExit().doCommand(null);
	    return;
	}

        if (splash != null) {
            if (urlToOpen == null) {
		splash.getStatusBar().showStatus(
		    Translator.localize("statusmsg.bar.defaultproject"));
            } else {
		Object[] msgArgs = {
		    projectName,
		};
		splash.getStatusBar().showStatus(
		    Translator.messageFormat("statusmsg.bar.readingproject",
					     msgArgs));
            }

            splash.getStatusBar().showProgress(40);
        }

        st.mark("make empty project");

        Designer.disableCritiquing();
        Designer.clearCritiquing();

        boolean projectLoaded = false;
        if (urlToOpen != null) {
            String filename = urlToOpen.getFile();
            File file = new File(filename);
            System.err.println("The url of the file to open is " + urlToOpen);
            System.err.println("The filename is " + filename);
            System.err.println("The file is " + file);
            System.err.println("File.exists = " + file.exists());
            projectLoaded = pb.loadProject(file, true);
        }
        
        if (!projectLoaded) {
            ProjectManager.getManager().makeEmptyProject();
        }

        st.mark("set project");

        Designer.enableCritiquing();

        st.mark("perspectives");

        if (urlToOpen == null) {
            pb.buildTitle(Translator.localize("label.projectbrowser-title"));
	}

        if (splash != null) {
            splash.getStatusBar().showProgress(75);
        }

        // Initialize the module loader.
        st.mark("modules");

        ModuleLoader2.doLoad(false);
        Argo.initializeModules();

        st.mark("open window");

        if (splash != null) {
            splash.getStatusBar().showStatus(
                Translator.localize("statusmsg.bar.open-project-browser"));
            splash.getStatusBar().showProgress(95);
        }

        pb.setVisible(true);

        st.mark("close splash");
        if (splash != null) {
            splash.setVisible(false);
            splash.dispose();
            splash = null;
        }

        performCommands(commands);
        commands = null;

        st.mark("start critics");
        Runnable startCritics = new StartCritics();
        Main.addPostLoadAction(startCritics);

        st.mark("start preloader");
        if (preload) {
            Runnable preloader = new PreloadClasses();
            Main.addPostLoadAction(preloader);
        }

        PostLoad pl = new PostLoad(postLoadActions);
        Thread postLoadThead = new Thread(pl);
        postLoadThead.start();

        LOG.info("");
        LOG.info("profile of load time ############");
        for (Enumeration i = st.result(); i.hasMoreElements();) {
            LOG.info(i.nextElement());
        }
        LOG.info("#################################");
        LOG.info("");

        st = null;
        pb.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        //ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(50000000);
    }

    /**
     * Calculates the {@link URL} for the given project name.
     * If the file does not exist or cannot be converted the default
     * {@link URL} is returned.
     *
     * @param projectName is the file name of the project
     * @param urlToOpen is the default {@link URL}
     * @return the new URL.
     */
    private static URL projectUrl(final String projectName, URL urlToOpen) {
        File projectFile = new File(projectName);
        if (!projectFile.exists()) {
            System.err.println("Project file '" + projectFile
			       + "' does not exist.");
            /* this will cause an empty project to be created */
        } else {
            try {
                urlToOpen = Util.fileToURL(projectFile);
            } catch (Exception e) {
                LOG.error("Exception opening project in main()", e);
            }
        }

        return urlToOpen;
    }

    /**
     * Prints the usage message.
     */
    private static void printUsage() {
        System.err.println("Usage: [options] [project-file]");
        System.err.println("Options include: ");
        System.err.println("  -help           display this information");
        LookAndFeelMgr.getInstance().printThemeArgs();
        System.err.println("  -nosplash       don't display logo at startup");
        System.err.println("  -noedem         don't report usage statistics");
        System.err.println("  -nopreload      don't preload common classes");
        System.err.println("  -norecentfile   don't reload last saved file");
        System.err.println("  -command <arg>  command to perform on startup");
        System.err.println("  -batch          don't start GUI");
        System.err.println("  -locale <arg>   set the locale (e.g. 'en_GB')");
        System.err.println("");
        System.err.println("You can also set java settings which influence "
			   + "the behaviour of ArgoUML:");
        System.err.println("  -Xms250M -Xmx500M  [makes ArgoUML reserve "
			   + "more memory for large projects]");
        System.err.println("\n\n");
    }

    /**
     * Checks tha JVM Version.<p>
     *
     * If it is a non supported JVM version we exit immediatly.
     */
    private static void checkJVMVersion() {
        // check if we are using a supported java version
        String javaVersion = System.getProperty("java.version", "");
        // exit if unsupported java version.
        if (javaVersion.startsWith("1.3")
            || javaVersion.startsWith("1.2")
            || javaVersion.startsWith("1.1")) {

	    System.err.println("You are using Java " + javaVersion + ", "
			       + "Please use Java 1.4 or later with ArgoUml");
	    System.exit(0);
        }
    }

    /**
     * Check that we can get the InetAddress for localhost.
     * This can fail on Unix if /etc/hosts is not correctly set up.
     */
    private static void checkHostsFile() {
        try {
            InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.err.println("ERROR: unable to get localhost information.");
            e.printStackTrace(System.err);
            System.err.println("On Unix systems this usually indicates that"
                + "your /etc/hosts file is incorrectly setup.");
            System.err.println("Stopping execution of ArgoUML.");
            System.exit(0);
        }
    }


    /**
     * Add an element to the PostLoadActions list.
     *
     * @param r a "Runnable" action
     */
    public static void addPostLoadAction(Runnable r) {
        postLoadActions.addElement(r);
    }

    /**
     * Perform a list of commands that were given on the command line.
     *
     * This first implementation just has a list of commands that
     * is possible to give.
     *
     * @param list The commands, a list of strings.
     */
    public static void performCommands(List list) {
	Iterator iter = list.iterator();

	while (iter.hasNext()) {
	    String commandstring = (String) iter.next();

	    int pos = commandstring.indexOf('=');

	    String commandname;
	    String commandargument;

	    if (pos == -1) {
		commandname = commandstring;
		commandargument = null;
	    } else {
		commandname = commandstring.substring(0, pos);
		commandargument = commandstring.substring(pos + 1);
	    }

	    // Perform one command.
	    Class c;
	    try {
		c = Class.forName(commandname);
	    } catch (ClassNotFoundException e) {
		System.out.println("Cannot find the command: " + commandname);
		continue;
	    }

	    // Now create a new object.
	    Object o = null;
	    try {
		o = c.newInstance();
	    } catch (InstantiationException e) {
		System.out.println(commandname
				   + " could not be instantiated - skipping"
				   + " (InstantiationException)");
		continue;
	    } catch (IllegalAccessException e) {
		System.out.println(commandname
				   + " could not be instantiated - skipping"
				   + " (IllegalAccessException)");
		continue;
	    }


	    if (o == null || !(o instanceof CommandLineInterface)) {
		System.out.println(commandname
				   + " is not a command - skipping.");
		continue;
	    }

	    CommandLineInterface clio = (CommandLineInterface) o;

	    System.out.println("Performing command "
			       + commandname + "( "
			       + (commandargument == null
				  ? "" : commandargument) + " )");
	    boolean result = clio.doCommand(commandargument);
	    if (!result) {
		System.out.println("There was an error executing "
				   + "the command "
				   + commandname + "( "
				   + (commandargument == null
				      ? "" : commandargument) + " )");
		System.out.println("Aborting the rest of the commands.");
		return;
	    }
	}
    }

    /**
     * Install our security handlers,
     * and do basic initialization of log4j.
     *
     * Log4j initialization must be done as
     * part of the main class initializer, so that
     * the log4j initialization is complete
     * before any other static initializers.
     *
     * Also installs a trap to "eat" certain SecurityExceptions.
     * Refer to {@link java.awt.EventDispatchThread} for details.
     */
    static {

        /*  Install the trap to "eat" SecurityExceptions.
         */
        System.setProperty(
            "sun.awt.exception.handler",
            ArgoAwtExceptionHandler.class.getName());

        /*  The string <code>log4j.configuration</code> is the
         *  same string found in
         *  {@link org.apache.log4j.Configuration.DEFAULT_CONFIGURATION_FILE}
         *  but if we use the reference, then log4j configures itself
         *  and clears the system property and we never know if it was
         *  set.
         *
         *  If it is set, then we let the static initializer in
         * {@link Argo} perform the initialization.
         */

        if (System.getProperty("log4j.configuration") == null) {
            Properties props = new Properties();
	    InputStream stream = null;
            try {
		stream =
		    ClassLoader.getSystemResourceAsStream(
		        DEFAULT_LOGGING_CONFIGURATION);

		if (stream != null) {
		    props.load(stream);
		}
            } catch (IOException io) {
                io.printStackTrace();
                System.exit(-1);
            }

	    PropertyConfigurator.configure(props);

	    if (stream == null) {
		BasicConfigurator.configure();
		Logger.getRootLogger().getLoggerRepository()
		    .setThreshold(Level.OFF);
	    }
        }

        // initLogging();
    }

    /**
     * Do a part of the initialization that is very much GUI-stuff.
     *
     * @param doSplash true if we are updating the splash
     * @param theTheme is the theme to set.
     */
    private static SplashScreen initializeGUI(
                    boolean doSplash, String theTheme) {
	// initialize the correct look and feel
	LookAndFeelMgr.getInstance().initializeLookAndFeel();
	if (theTheme != null) {
	    LookAndFeelMgr.getInstance().setCurrentTheme(theTheme);
	}

        SplashScreen splash = null;
        if (doSplash) {
            splash = new SplashScreen();
        }

        // make the projectbrowser
	ProjectBrowser pb = ProjectBrowser.makeInstance(splash);

	JOptionPane.setRootFrame(pb);

	// Set the screen layout to what the user left it before, or
	// to reasonable defaults.
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	pb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	int configFrameWidth =
	    Configuration.getInteger(Argo.KEY_SCREEN_WIDTH,
				     (int) (0.95 * scrSize.width));
	int configFrameHeight =
	    Configuration.getInteger(Argo.KEY_SCREEN_HEIGHT,
				     (int) (0.95 * scrSize.height));
        int w = Math.min(configFrameWidth, scrSize.width);
        int h = Math.min(configFrameHeight, scrSize.height);
	int x = Configuration.getInteger(Argo.KEY_SCREEN_LEFT_X, 0);
	int y = Configuration.getInteger(Argo.KEY_SCREEN_TOP_Y, 0);
	pb.setLocation(x, y);
	pb.setSize(w, h);
        return splash;
    }



} /* end Class Main */

/**
 * Class to hold a list of actions to be perform and to perform them
 * after the initializations is done.
 */
class PostLoad implements Runnable {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(PostLoad.class);

    /**
     * The list of actions to perform.
     */
    private Vector postLoadActions;

    /**
     * Constructor.
     *
     * @param v The actions to perform.
     */
    public PostLoad(Vector v) {
        postLoadActions = v;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
            LOG.error("post load no sleep", ex);
        }
        int size = postLoadActions.size();
        for (int i = 0; i < size; i++) {
            Runnable r = (Runnable) postLoadActions.elementAt(i);
            r.run();
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                LOG.error("post load no sleep2", ex);
            }
        }
    }
} /* end class PostLoad */

/**
 * Class to do preloading of a lot of classes.
 */
class PreloadClasses implements Runnable {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(PreloadClasses.class);

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        Class c = null;
        if (c == null) {
            // otherwise anoying warning
            LOG.info("preloading...");
        }

	// Alphabetic order
        c = org.argouml.kernel.DelayedChangeNotify.class;
        c = org.argouml.cognitive.ui.Wizard.class;
        c = org.argouml.ui.Clarifier.class;
        c = org.argouml.ui.StylePanelFigNodeModelElement.class;
        c = org.argouml.uml.GenCompositeClasses.class;
        c = org.argouml.uml.cognitive.critics.ClAttributeCompartment.class;
        c = org.argouml.uml.cognitive.critics.ClClassName.class;
        c = org.argouml.uml.cognitive.critics.ClOperationCompartment.class;
        c = org.argouml.uml.diagram.static_structure.ui.FigClass.class;
        c = org.argouml.uml.diagram.static_structure.ui.FigInterface.class;
        c = org.argouml.uml.diagram.static_structure.ui.FigPackage.class;
        c = org.argouml.uml.diagram.static_structure.ui.SelectionClass.class;
        c =
            org.argouml.uml.diagram.static_structure.ui
	    .StylePanelFigClass.class;
        c =
            org.argouml.uml.diagram.static_structure.ui
	    .StylePanelFigInterface.class;
        c = org.argouml.uml.diagram.ui.FigAssociation.class;
        c = org.argouml.uml.diagram.ui.FigGeneralization.class;
        c = org.argouml.uml.diagram.ui.FigRealization.class;
        c = org.tigris.gef.base.ModeCreateEdgeAndNode.class;
        c = org.argouml.uml.diagram.ui.SPFigEdgeModelElement.class;
        c = org.argouml.uml.diagram.ui.SelectionNodeClarifiers.class;
        c = org.argouml.uml.ui.foundation.core.PropPanelAssociation.class;
        c = org.argouml.uml.ui.foundation.core.PropPanelClass.class;
        c = org.argouml.uml.ui.foundation.core.PropPanelInterface.class;

        c = org.tigris.gef.base.CmdSetMode.class;
        c = org.tigris.gef.base.Geometry.class;
        c = org.tigris.gef.base.ModeModify.class;
        c = org.tigris.gef.base.ModePlace.class;
        c = org.tigris.gef.base.PathConvPercentPlusConst.class;
        c = org.tigris.gef.base.SelectionResize.class;
        c = org.tigris.gef.event.ModeChangeEvent.class;
        c = org.tigris.gef.graph.GraphEdgeHooks.class;
        c = org.tigris.gef.graph.GraphEvent.class;
        c = org.tigris.gef.graph.presentation.NetEdge.class;
        c = org.tigris.gef.presentation.ArrowHeadNone.class;
        c = org.tigris.gef.presentation.FigPoly.class;
        c = org.tigris.gef.ui.ColorRenderer.class;
        c = org.tigris.gef.ui.Swatch.class;
        c = org.tigris.gef.util.EnumerationEmpty.class;
        c = org.tigris.gef.util.EnumerationSingle.class;

        LOG.info(" done preloading");
    }

} /* end class PreloadClasses */
