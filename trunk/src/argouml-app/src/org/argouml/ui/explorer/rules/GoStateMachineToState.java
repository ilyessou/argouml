/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tom Morris
 *    Bob Tarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2006 The Regents of the University of California. All
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

// $header$

package org.argouml.ui.explorer.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;

/**
 * PerspectiveRule to navigate from statemachine to the subvertices of
 * its top state (1 level deep only).
 *
 * @author jaap.branderhorst@xs4all.nl
 */
public class GoStateMachineToState extends AbstractPerspectiveRule {

    private static final Logger LOG =
        Logger.getLogger(GoStateMachineToState.class.getName());

    /*
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getRuleName()
     */
    public String getRuleName() {
        return Translator.localize("misc.state-machine.state");
    }

    /*
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getChildren(
     *         java.lang.Object)
     */
    public Collection getChildren(Object parent) {

        if (Model.getFacade().isAStateMachine(parent)) {
            if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
                if (Model.getFacade().getTop(parent) != null) {
                    return Model.getFacade().getSubvertices(
                            Model.getFacade().getTop(parent));
                }
            } else {
                // TODO: UML2 - what do we do here?
                return Collections.EMPTY_SET;
            }
        }
        return Collections.EMPTY_SET;
    }

    /*
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getDependencies(
     *         java.lang.Object)
     */
    public Set getDependencies(Object parent) {
        if (Model.getFacade().isAStateMachine(parent)) {
	    Set set = new HashSet();
	    set.add(parent);
	    try {
	        if (Model.getFacade().getTop(parent) != null) {
	            set.add(Model.getFacade().getTop(parent));
	        }
	    } catch (RuntimeException e) {
	        if (Model.getFacade().getUmlVersion().startsWith("2")) {
                    // TODO: Ignore and report exception until getTop
	            // implemented.
                    LOG.log(Level.SEVERE, "Explorer caught exception ", e);
	        } else {
                    throw e;
	        }
	    }
	    return set;
	}
	return Collections.EMPTY_SET;
    }
}
