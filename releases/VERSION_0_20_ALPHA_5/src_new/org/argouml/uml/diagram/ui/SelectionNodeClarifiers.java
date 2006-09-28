// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

// $Id$
package org.argouml.uml.diagram.ui;

import java.awt.Graphics;

import org.tigris.gef.base.SelectionButtons;
import org.tigris.gef.presentation.Fig;

/**
 *
 *
 *
 * @author jrobbins
 */
public class SelectionNodeClarifiers extends SelectionButtons {

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new SelectionNodeClarifiers for the given Fig
     *
     * @param f the given Fig
     */
    public SelectionNodeClarifiers(Fig f) {
        super(f);
    }

    /**
     * Paint the handles at the four corners and midway along each edge of the
     * bounding box.
     *
     * @see org.tigris.gef.base.Selection#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        ((FigNodeModelElement) getContent()).paintClarifiers(g);
        super.paint(g);
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#paintButtons(java.awt.Graphics)
     */
    public void paintButtons(Graphics g) {
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#getNewNode(int)
     */
    protected Object getNewNode(int arg0) {
        return null;
    }

} /* end class SelectionNodeClarifiers */

