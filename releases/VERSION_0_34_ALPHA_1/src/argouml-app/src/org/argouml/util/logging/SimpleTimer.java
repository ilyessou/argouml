/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    penyaskito
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

package org.argouml.util.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This class makes it easy to get the time between two or several
 * points in the code.
 *
 * @author Linus Tolke
 */
public class SimpleTimer {
    private List<Long> points = new ArrayList<Long>();
    private List<String> labels = new ArrayList<String>();

    /**
     * The constructor. Creates a simple timer.
     */
    public SimpleTimer() {
    }

    /**
     * Mark (Store) the current time.
     */
    public void mark() {
	points.add(new Long(System.currentTimeMillis()));
	labels.add(null);
    }

    /**
     * Mark (Store) the current time.
     *
     * @param label the mark will be labeled with this string
     */
    public void mark(String label) {
	mark();
	labels.set(labels.size() - 1, label);
    }

    /**
     * Returns an enumeration of formatted distances.
     *
     * @return an enumeration representing the results
     */
    public Enumeration result() {
	mark();
	return new SimpleTimerEnumeration();
    }

    /**
     * An enumeration to walk through all entries.<p>
     *
     * This allows us to get a summary at the end.
     *
     * @author Linus Tolke
     */
    class SimpleTimerEnumeration implements Enumeration<String> {
        /**
         * Keep track of where we are in the list.
         */
        private int count = 1;

        /*
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return count <= points.size();
        }

        /*
         * @see java.util.Enumeration#nextElement()
         */
        public String nextElement() {
            StringBuffer res = new StringBuffer();
            synchronized (points) {
                if (count < points.size()) {
                    if (labels.get(count - 1) == null) {
                        res.append("phase ").append(count);
                    } else {
                        res.append(labels.get(count - 1));
                    }
                    res.append("                            ");
                    res.append("                            ");
                    res.setLength(60);
    		    res.append(points.get(count) - points.get(count - 1));
                } else if (count == points.size()) {
                    res.append("Total                      ");
                    res.setLength(18);
                    res.append(points.get(points.size() - 1) - (points.get(0)));
                }
            }
            count++;
            return res.toString();
        }
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
	StringBuffer sb = new StringBuffer("");

	for (Enumeration e = result(); e.hasMoreElements();) {
	    sb.append((String) e.nextElement());
	    sb.append("\n");
	}
	return sb.toString();
    }
}
