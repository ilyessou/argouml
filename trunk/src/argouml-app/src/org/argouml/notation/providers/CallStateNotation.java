/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006-2007 The Regents of the University of California. All
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

package org.argouml.notation.providers;

import org.argouml.model.Model;
import org.argouml.notation.NotationProvider;

/**
 * This abstract class forms the basis of all Notation providers
 * for the text shown in the Fig that represents the CallState.
 * Subclass this for all languages.
 *
 * @author Michiel van der Wulp
 */
public abstract class CallStateNotation extends NotationProvider {

    /**
     * The constructor.
     *
     * @param callState the UML element
     */
    public CallStateNotation(Object callState) {
        if (!Model.getFacade().isACallState(callState)) {
            throw new IllegalArgumentException("This is not an CallState.");
        }
    }

    @Override
    public void initialiseListener(Object modelElement) {
        // register for events from all modelelements
        // that change the name and body text
        // i.e. when the CallAction is replaced:
        addElementListener(modelElement, 
                new String[] {"entry", "name", "remove"});
        Object entryAction = Model.getFacade().getEntry(modelElement);
        if (Model.getFacade().isACallAction(entryAction)) {
            // and when the Operation is replaced:
            addElementListener(entryAction, "operation");
            Object operation = Model.getFacade().getOperation(entryAction);
            if (operation != null) {
                // and when the owner is replaced (unlikely for operations),
                // and when the operation changes name:
                addElementListener(operation,
                        new String[] {"owner", "name"});
                Object classifier = Model.getFacade().getOwner(operation);
                // and when the class changes name:
                addElementListener(classifier, "name");
            }
        }
    }

}

