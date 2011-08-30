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

// Copyright (c) 2003-2006 The Regents of the University of California. All
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

import javax.swing.AbstractAction;

import org.argouml.ui.ProjectBrowser;

/**
 * Reopens a project with respect of the calling event handler - should be
 * used with menu item.
 *
 * @author  Frank Jelinek
 * @since 10. November 2003 (0.15.2)
 */
public class ActionReopenProject extends AbstractAction {

    private String filename;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Constructor.
     *
     * @param theFilename The name of the file.
     */
    public ActionReopenProject(String theFilename) {
	super("action.reopen-project");
	filename = theFilename;
    }

    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * Get the filename for comparing during menu creation.
     *
     * @return The filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Performs the save and reload of a project.
     *
     * @param e e should old the event and the eventsource. Event
     * source is the menu item, the text is used for opening the
     * project
     */
    public void actionPerformed(ActionEvent e) {
        if (!ProjectBrowser.getInstance().askConfirmationAndSave()) {
            return;
        }

        File toOpen = new File(filename);
        // load of the new project
        // just reuse of the ActionOpen object
        ProjectBrowser.getInstance().loadProjectWithProgressMonitor(
                toOpen, true);
    }
}
