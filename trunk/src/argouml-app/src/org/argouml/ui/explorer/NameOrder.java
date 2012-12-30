/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    euluis
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

package org.argouml.ui.explorer;

import java.text.Collator;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProfileConfiguration;
import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.argouml.profile.Profile;
import org.tigris.gef.base.Diagram;

/**
 * Sorts explorer nodes by their user object name.
 *
 * @author  alexb
 * @since 0.15.2, Created on 28 September 2003, 10:02
 */
public class NameOrder
    implements Comparator {
    
    private Collator collator = Collator.getInstance();

    /**
     * Creates a new instance of NameOrder.
     */
    public NameOrder() {
        collator.setStrength(Collator.PRIMARY);
    }

    /*
     * Do string compare of names of UML objects.  Comparison is
     * case insensitive using a primary strength collator in the user's
     * locale.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object obj1, Object obj2) {
        
	if (obj1 instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj1;
	    obj1 = node.getUserObject();
	}

	if (obj2 instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj2;
	    obj2 = node.getUserObject();
	}

        return compareUserObjects(obj1, obj2);
    }

    /**
     * Alphabetic ordering of user object names instead of type names.
     *
     * @param obj Diagram or Base
     * @param obj1 Diagram or Base
     * @return 0 if invalid params. 0 if the objects are equally named.
     *         A positive or negative int if the names differ.
     */
    protected int compareUserObjects(Object obj, Object obj1) {
        // this is safe because getName always returns a string of some type
        return collator.compare(getName(obj), getName(obj1));
    }

    /**
     * Get the name of the diagram or model element.
     *
     * @param obj the item to fetch name from
     * @return the name
     */
    private String getName(Object obj) {
        String name;
        if (obj instanceof Diagram) {
            name = ((Diagram) obj).getName();
        } else if (obj instanceof ProfileConfiguration) {
            name = "Profile Configuration";
        } else if (obj instanceof Profile) {
            name = ((Profile) obj).getDisplayName();
        } else if (Model.getFacade().isANamedElement(obj)) {
            try {
                name = Model.getFacade().getName(obj);
            } catch (InvalidElementException e) {
                name = Translator.localize("misc.name.deleted");
            }
        } else {
            name = "";
        }

        if (name == null) {
            return "";
        }
        return name;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Translator.localize("combobox.order-by-name");
    }
}
