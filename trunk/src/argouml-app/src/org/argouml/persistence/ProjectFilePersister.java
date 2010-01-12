/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    linus
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
package org.argouml.persistence;

import java.io.File;

import org.argouml.kernel.Project;
import org.argouml.taskmgmt.ProgressListener;

/**
 * To persist a project to and from file storage.
 *
 * @author Bob Tarling
 */
public interface ProjectFilePersister {

    /**
     * @param project the project to save
     * @param file The file to write.
     * @throws SaveException if anything goes wrong.
     * @throws InterruptedException     if the thread is interrupted
     */
    void save(Project project, File file) throws SaveException, 
    InterruptedException;

    /**
     * @param file the file of the project to load.
     * @return the Project
     *
     * @throws OpenException when we fail to open from this url
     * @throws InterruptedException     if the thread is interrupted
     */
    Project doLoad(File file) throws OpenException, InterruptedException;
    
    /**
     * Add any object interested in listening to persistence progress.
     *
     * @param listener the interested listener.
     */
    public void addProgressListener(ProgressListener listener);
    
    /**
     * Remove any object no longer interested in listening to persistence
     * progress.
     *
     * @param listener the listener to remove.
     */
    public void removeProgressListener(ProgressListener listener);    
}
