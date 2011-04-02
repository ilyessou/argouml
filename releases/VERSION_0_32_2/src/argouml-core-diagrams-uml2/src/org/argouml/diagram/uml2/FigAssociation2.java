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

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

package org.argouml.diagram.uml2;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.argouml.uml.diagram.DiagramEdgeSettings;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.FigAssociation;


/**
 * This class represents the Fig of a binary association on a diagram.
 *
 */
class FigAssociation2 extends FigAssociation {
    
    private static final Logger LOG = Logger.getLogger(FigAssociation2.class);

    /**
     * Constructor used by PGML parser.
     * 
     * @param owner owning uml element
     * @param settings rendering settings
     */
    public FigAssociation2(
            final DiagramEdgeSettings diagramEdgeSettings,
            final DiagramSettings settings) {
        super(diagramEdgeSettings, settings);
        addListener(diagramEdgeSettings.getOwner());
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent pve) {
        if ("navigableOwnedEnd".equals(pve.getPropertyName())) {
            // TODO: We need to amend the arrow heads here
            LOG.debug("Navigation has changed");
        }
        super.propertyChange(pve);
    }
    
    /*
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#updateListeners(java.lang.Object, java.lang.Object)
     */
    @Override
    public void updateListeners(Object oldOwner, Object newOwner) {
        super.updateListeners(oldOwner, newOwner);
        addListener(newOwner);
    }
    
    private void addListener(Object owner) {
        addElementListener(owner, new String[] {"remove", "navigableOwnedEnd"});
    }
}
