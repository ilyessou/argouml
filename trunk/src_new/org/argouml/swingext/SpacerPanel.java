// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.swingext;

import java.awt.Dimension;
import javax.swing.JPanel;

/** A Swing panel that displays nothing, but takes up a specified
 *  amount of space.  Used to make panels with GridBagLayouts look
 *  better.
 */
public class SpacerPanel extends JPanel {

    private int w = 10, h = 10;

    /**
     * The constructor.
     *
     */
    public SpacerPanel() { }

    /**
     * The constructor.
     *
     * @param width the width
     * @param height the height
     */
    public SpacerPanel(int width, int height) { w = width; h = height; }

    /**
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() { return new Dimension(w, h); }

    /**
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() { return new Dimension(w, h); }

    /**
     * @see java.awt.Component#getSize()
     */
    public Dimension getSize() { return new Dimension(w, h); }
} /* end class SpacerPanel */
