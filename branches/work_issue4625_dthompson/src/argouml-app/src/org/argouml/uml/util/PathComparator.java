// $Id$
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

package org.argouml.uml.util;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.argouml.model.Model;


/**
 * Comparator which orders model elements alphabetically by name, ignoring case.
 * Ties are broken using names from the path in reverse order.
 * 
 * @author Tom Morris <tfmorris@gmail.com>
 */
public class PathComparator implements Comparator {

    /**
     * Compare two UML elements names, ignoring case, using names from the path
     * as tie breakers.
     * 
     * @param o1 first model element
     * @param o2 second model element
     * @return -1, 0, 1
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        // Elements are collated first by name and then by 
        // their enclosing path to distinguish them
        List<String> path1 = Model.getModelManagementHelper().getPathList(o1);
        Collections.reverse(path1);
        List<String> path2 = Model.getModelManagementHelper().getPathList(o2);
        Collections.reverse(path2);
        return compareStringLists(path1, path2);
    }

    /*
     * Compare two lists of strings using a primary strength text collator. 
     * This will collate e, E, �, � together, but not eliminate non-identical
     * strings which collate in the same place.
     * 
     * @return equivalent of list1.compareTo(list2)
     */
    private int compareStringLists(List<String> list1, List<String> list2) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);
        Iterator<String> i2 = list2.iterator();
        Iterator<String> i1 = list1.iterator();
        boolean caseDiffers = false;
        while (i2.hasNext()) {
            String name2 = i2.next();
            if (!i1.hasNext()) {
                return -1;
            }
            String name1 = i1.next();
            if (name1 == null) {
                return -1;
            }
            int comparison = collator.compare(name1, name2);
            if (comparison != 0) {
                return comparison;
            }
            caseDiffers = caseDiffers | !(name1.equals(name2));
        }
        if (i2.hasNext()) {
            return 1;
        }
        // If the strings differed only in non-primary characteristics at
        // some point (case, accent, etc) pick an arbitrary collating order.
        // We don't call them equal to keep them from being merged in the list.
        if (caseDiffers) {
            return 1;
        }
        // It's illegal in UML to have multiple elements in a namespace with
        // the same name, but if it happens, keep them distinct so the user
        // has a chance of catching the error.  Pick an arbitrary collating 
        // order.
        // Note: this may make the collating order unstable.
        return 1;
    }
}



