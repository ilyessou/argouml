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

package org.argouml.kernel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.argouml.application.Main;
import org.argouml.cognitive.ProjectMemberTodoList;
import org.argouml.model.uml.UmlHelper;
import org.argouml.ui.ArgoDiagram;
import org.argouml.util.FileConstants;
import org.argouml.xml.argo.ArgoParser;
import org.argouml.xml.xmi.XMIParser;
import org.argouml.xml.xmi.XMIReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class manages the projects loaded in argouml.
 *
 * Classes in Argouml can ask this class for the current
 * project and set the current project.  Since we only have one
 * project in ArgoUML at the moment, this class does not manage a list
 * of projects like one would expect. This could be a nice extension
 * for the future of argouml.  As soon as the current project is
 * changed, a property changed event is fired.
 *
 * @since Nov 17, 2002
 * @author jaap.branderhorst@xs4all.nl
 * @stereotype singleton
 */
public final class ProjectManager {

    public static final String CURRENT_PROJECT_PROPERTY_NAME =
	"currentProject";
    public static final String SAVE_STATE_PROPERTY_NAME = "saveState";

	/** logger */
	private static Logger cat = Logger.getLogger(ProjectManager.class);

    /**
     * The singleton instance of this class
     */
    private static ProjectManager _instance;

    /**
     * The project that is visible in the projectbrowser
     */
    private static Project _currentProject;

    /**
     * Flag to indicate we are creating a new current project
     */
    private boolean _creatingCurrentProject;

    /**
     * The listener list
     */
    private EventListenerList _listenerList = new EventListenerList();

    /**
     * The event to fire
     */
    private PropertyChangeEvent _event;

    /**
     * The singleton accessor method of this class
     */
    public static ProjectManager getManager() {
        if (_instance == null) {
            _instance = new ProjectManager();
        }
        return _instance;
    }

    /**
     * Constructor for ProjectManager.
     */
    private ProjectManager() {
        super();
    }

    /**
     * Adds an instance implementing propertychangelistener to the listener list
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        _listenerList.add(PropertyChangeListener.class, listener);
    }

    /**
     * Removes a listener from the listener list.
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        _listenerList.remove(PropertyChangeListener.class, listener);
    }

    private void firePropertyChanged(String propertyName,
				     Object oldValue, Object newValue) 
    {
        // Guaranteed to return a non-null array
        Object[] listeners = _listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PropertyChangeListener.class) {
                // Lazily create the event:
                if (_event == null)
                    _event =
                        new PropertyChangeEvent(
                            this,
                            propertyName,
                            oldValue,
                            newValue);
                ((PropertyChangeListener) listeners[i + 1]).propertyChange(
                    _event);
            }
        }
        _event = null;

    }

    /**
     * Sets the current project (the project that is viewable in the 
     * projectbrowser).
     * This method fires a propertychanged event.<p>
     *
     * If the argument is null, then the current project will be forgotten 
     * about.
     * 
     * @param newProject The new project.
     */
    public void setCurrentProject(Project newProject) {
        Project oldProject = _currentProject;        
        _currentProject = newProject;
        if (_currentProject != null
	    && _currentProject.getActiveDiagram() == null) {
            Vector diagrams = _currentProject.getDiagrams();
            if (diagrams != null && !diagrams.isEmpty()) {
		ArgoDiagram activeDiagram =
		    (ArgoDiagram) _currentProject.getDiagrams().get(0);
                _currentProject.setActiveDiagram(activeDiagram);
	    }
        }
        firePropertyChanged(CURRENT_PROJECT_PROPERTY_NAME,
			    oldProject, newProject);
    }

    /**
     * Returns the current project.
     * @return Project
     */
    public Project getCurrentProject() {
        if (_currentProject == null && !_creatingCurrentProject) {
            _currentProject = makeEmptyProject();
        }
        return _currentProject;
    }

    /**
     * Makes an empty project with two standard diagrams.
     * @return Project
     */
    public Project makeEmptyProject() {
        _creatingCurrentProject = true;
        cat.info("making empty project");
        Project p = new Project();
        // the following line should not normally be here,
        // but is necessary for argouml start up.
        setCurrentProject(p);
        p.makeUntitledProject();
        // set the current project after making it!
        setCurrentProject(p);
        _creatingCurrentProject = false;
        return p;
    }

    /**   
     * This method creates a project from the specified URL
     *
     * Unlike the constructor which forces an .argo extension This
     * method will attempt to load a raw XMI file
     * 
     * This method can fail in several different ways. Either by
     * throwing an exception or by having the
     * ArgoParser.SINGLETON.getLastLoadStatus() set to not true.
     * 
     * TODO: The exception in the throws clause should be splitted
     * in several other types of exceptions to handle errors better
     */
    public Project loadProject(URL url)
        throws IOException, IllegalFormatException, SAXException,
	       ParserConfigurationException 
    {
        Project p = null;
        String urlString = url.toString();
        int lastDot = urlString.lastIndexOf(".");
        String suffix = "";
        if (lastDot >= 0) {
            suffix = urlString.substring(lastDot).toLowerCase();
        }
        if (suffix.equals(".xmi")) {
            p = loadProjectFromXMI(url);
        } else if (suffix.equals(FileConstants.COMPRESSED_FILE_EXT)) {
	    // normal case, .zargo
            p = loadProjectFromZargo(url);
        } else if (suffix.equals(FileConstants.UNCOMPRESSED_FILE_EXT)) {
	    // the old argo format probably
            p = loadProjectFromZargo(url);
        } else {
            throw new IllegalFormatException(
                "No legal format found for url " + url.toString());
        }
        return p;
    }

    /**
     * Reads an XMI file.<p>
     *
     * This could be used to import models from other tools.
     *
     * @param url is the file name of the file
     * @return Project is a new project containing the read model
     * @throws IOException is thrown if some error occurs
     */
    private Project loadProjectFromXMI(URL url) throws IOException {
        Project p = new Project();
        XMIParser.SINGLETON.readModels(p, url);
        Object model = XMIParser.SINGLETON.getCurModel();
        UmlHelper.getHelper().addListenersToModel(model);
        p.setUUIDRefs(XMIParser.SINGLETON.getUUIDRefs());
        p.addMember(new ProjectMemberTodoList("", p));
        p.addMember(model);
        p.setNeedsSave(false);
        Main.addPostLoadAction(new ResetStatsLater());
        return p;
    }

    /**
     * Reads an url of the .zargo format.
     * @param url
     * @return Project
     * @throws Exception if there is an exception during load. Should be handled
     * by the GUI.
     */
    private Project loadProjectFromZargo(URL url)
        throws IOException, SAXException, ParserConfigurationException {
        Project p = null;
        // read the argo 
        try {
            ZipInputStream zis = new ZipInputStream(url.openStream());

            // first read the .argo file from Zip
            ZipEntry entry = zis.getNextEntry();
            while (entry != null
		   && !entry.getName().endsWith(FileConstants.PROJECT_FILE_EXT))
	    {
                entry = zis.getNextEntry();
            }

            // the "false" means that members should not be added,
            // we want to do this by hand from the zipped stream.
            ArgoParser.SINGLETON.setURL(url);
            ArgoParser.SINGLETON.readProject(zis, false);
            p = ArgoParser.SINGLETON.getProject();
            ArgoParser.SINGLETON.setProject(null);// clear up project refs

            zis.close();

        } catch (IOException e) {
            // exception can occur both due to argouml code as to J2SE
            // code, so lets log it
            cat.error(e);
            throw e;
        }
        // read the xmi
        try {
            ZipInputStream zis = new ZipInputStream(url.openStream());

            // first read the .argo file from Zip
            String name = zis.getNextEntry().getName();
            while (!name.endsWith(".xmi")) {
                ZipEntry nextEntry = zis.getNextEntry();
                if (nextEntry == null)
                    throw new IOException("The XMI file is missing "
					  + "from the .zargo file.");
                name = nextEntry.getName();
            }

            XMIReader xmiReader = null;
            try {
                xmiReader = new org.argouml.xml.xmi.XMIReader();
            } catch (SAXException se) { // duh, this must be catched and handled
                cat.error(se);
                throw se;
            } catch (ParserConfigurationException pc) {
		// duh, this must be catched and handled
                cat.error(pc);
                throw pc;
            }
//            Object mmodel = null;

            InputSource source = new InputSource(zis);
            source.setEncoding("UTF-8");
//            mmodel = xmiReader.parseToModel(new InputSource(zis));
            // the following strange construction is needed because
            // Novosoft does not really know how to handle
            // exceptions...
            if (xmiReader.getErrors()) {
                if (xmiReader.getErrors()) {
                    ArgoParser.SINGLETON.setLastLoadStatus(false);
                    ArgoParser.SINGLETON.setLastLoadMessage(
                        "XMI file "
                            + url.toString()
                            + " could not be "
                            + "parsed.");
                    cat.error(
                        "XMI file "
                            + url.toString()
                            + " could not be "
                            + "parsed.");
                    throw new SAXException(
                        "XMI file "
                            + url.toString()
                            + " could not be "
                            + "parsed.");
                }
            }
            zis.close();

        } catch (IOException e) {
            // exception can occur both due to argouml code as to J2SE
            // code, so lets log it
            cat.error(e);
            throw e;
        }
        p.loadZippedProjectMembers(url);
        p.postLoad();
        return p;
    }

    /**
     * Loads a project from an url of the argo format.
     * @param url
     * @return Project
     * @throws IOException
     */
    private Project loadProjectFromArgo(URL url)
        throws IOException, ParserConfigurationException, SAXException {
        ArgoParser.SINGLETON.readProject(url);
        Project p = ArgoParser.SINGLETON.getProject();
            ArgoParser.SINGLETON.setProject(null);// clear up project refs
        p.loadAllMembers();
        p.postLoad();
        return p;
    }
    
    /**
     * notify the gui from the project manager that the
     * current project's save state has changed.
     */
    public void notifySavePropertyChanged(boolean newValue) {
        
        firePropertyChanged(SAVE_STATE_PROPERTY_NAME,
                            new Boolean(!newValue),
                            new Boolean(newValue));
    }
    
    /**
     * prepare project for gc
     */
    public void removeProject(Project oldProject){
        
        if(_currentProject == oldProject){
            _currentProject = null;
        }
        
        oldProject.remove();
    }
}


/**
 * @deprecated since 0.15.1. TODO: What is this replaced by?
 */
class ResetStatsLater implements Runnable {
	public void run() {
		Project.resetStats();
	}
} /* end class ResetStatsLater */
