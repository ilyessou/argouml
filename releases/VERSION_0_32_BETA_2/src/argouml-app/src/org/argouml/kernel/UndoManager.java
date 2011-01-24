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

// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.kernel;

import java.beans.PropertyChangeListener;

/**
 * Stores Commands that have been executed and allows them to be undone
 * and redone. Commands represent single operations on the ArgoUML model.
 * A single user interaction may generate several Commands. Undo/redo
 * works an a user interaction and so can undo/redo several commands in one
 * call.
 * 
 * @author Bob Tarling
 */
public interface UndoManager {

    /**
     * Adds a new command to the undo stack.
     * @param command the command.
     */
    public abstract void addCommand(Command command);

    /**
     * Execute a command and add it to the undo stack.
     * 
     * @param command the command.
     * @return any resulting object the command creates
     */
    public abstract Object execute(Command command);

    /**
     * Set the maximum number of interactions the stack can hold.
     * @param max the maximum chain count
     */
    public abstract void setUndoMax(int max);

    /**
     * Undo the top user interaction on the undo stack and move
     * it to the redo stack.
     */
    public abstract void undo();

    /**
     * Redo the top user interaction on the redo stack and move
     * it to the undo stack.
     */
    public abstract void redo();

    /**
     * Instructs the UndoManager that a new user interaction is about to take
     * place. All commands received until the next call to startInteraction will
     * form a single undoable unit.
     * 
     * @param label the label for this interaction to build the undo/redo label
     */
    public abstract void startInteraction(String label);
    
    /**
     * Add a new PropertyChangeListener for undo/redo events. Allow a listener
     * to detect when the undo or redo stack changes availability. No guarantees
     * are made about which thread the event will be delivered on, so any
     * specific thread requirements (e.g. Swing/AWT thread requirements) must be
     * dealt with by the
     * {@link PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
     * method.
     * 
     * @param listener a PropertyChangeListener
     */
    public abstract void addPropertyChangeListener(
            PropertyChangeListener listener);
    
    /**
     * Remove the given listener.
     * @param listener a PropertyChangeListener
     */
    public abstract void removePropertyChangeListener(
            PropertyChangeListener listener);
}
