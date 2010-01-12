/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
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

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.argouml.application.api.CommandLineInterface;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.persistence.AbstractFilePersister;
import org.argouml.persistence.PersistenceManager;
import org.argouml.persistence.ProjectFileView;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.UndoableAction;
import org.argouml.util.ArgoFrame;

/**
 * Action that loads the project.
 * This will throw away the project that we were working with up to this
 * point so some extra caution.
 *
 * @see ActionSaveProject
 */
public class ActionOpenProject extends UndoableAction
    implements CommandLineInterface {

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Constructor for this action.
     */
    public ActionOpenProject() {
        super(Translator.localize("action.open-project"),
                ResourceLoaderWrapper.lookupIcon("action.open-project"));
        // Set the tooltip string:
        putValue(Action.SHORT_DESCRIPTION, 
                Translator.localize("action.open-project"));
    }

    ////////////////////////////////////////////////////////////////
    // main methods


    /**
     * Performs the action of opening a project.
     *
     * @param e an event
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Project p = ProjectManager.getManager().getCurrentProject();
        PersistenceManager pm = PersistenceManager.getInstance();

        if (!ProjectBrowser.getInstance().askConfirmationAndSave()) {
            return;
        }

        // next line does give user.home back but this is not
        // compliant with how the project.uri works and therefore
        // open and save project as give different starting
        // directories.  String directory =
        // Globals.getLastDirectory();
        JFileChooser chooser = null;
        if (p != null && p.getURI() != null) {
            File file = new File(p.getURI());
            if (file.getParentFile() != null) {
                chooser = new JFileChooser(file.getParent());
            }
        } else {
            chooser = new JFileChooser();
        }

        if (chooser == null) {
            chooser = new JFileChooser();
        }

        chooser.setDialogTitle(
                Translator.localize("filechooser.open-project"));

        chooser.setAcceptAllFileFilterUsed(false);

        // adding project files icon
        chooser.setFileView(ProjectFileView.getInstance());
        
        pm.setOpenFileChooserFilter(chooser);

        String fn = Configuration.getString(
                PersistenceManager.KEY_OPEN_PROJECT_PATH);
        if (fn.length() > 0) {
            chooser.setSelectedFile(new File(fn));
        }

        int retval = chooser.showOpenDialog(ArgoFrame.getFrame());
        if (retval == JFileChooser.APPROVE_OPTION) {
            File theFile = chooser.getSelectedFile();

            if (!theFile.canRead()) {
                /* Try adding the extension from the chosen filter. */
                FileFilter ffilter = chooser.getFileFilter();
                if (ffilter instanceof AbstractFilePersister) {
                    AbstractFilePersister afp = 
                        (AbstractFilePersister) ffilter;
                    File m =
                        new File(theFile.getPath() + "."
                                + afp.getExtension());
                    if (m.canRead()) {
                        theFile = m;
                    }
                }
                if (!theFile.canRead()) {
                    /* Try adding the default extension. */
                    File n =
                        new File(theFile.getPath() + "."
                                + pm.getDefaultExtension());
                    if (n.canRead()) {
                        theFile = n;
                    }
                }
            }
            if (theFile != null) {
                Configuration.setString(
                        PersistenceManager.KEY_OPEN_PROJECT_PATH,
                        theFile.getPath());

                ProjectBrowser.getInstance().loadProjectWithProgressMonitor(
                		theFile, true);
            }
        }
    }

    /**
     * Execute this action from the command line.
     *
     * @see org.argouml.application.api.CommandLineInterface#doCommand(String)
     * @param argument is the url of the project we load.
     * @return true if it is OK.
     */
    public boolean doCommand(String argument) {
        return ProjectBrowser.getInstance()
            .loadProject(new File(argument), false, null);
    }

} /* end class ActionOpenProject */
