// $Id$
// Copyright (c) 2003 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import java.util.Iterator;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigText;

/**
 * Custom class to group FigTexts in such a way that they don't
 * overlap and that the group is shrinked to fit (no whitespace in
 * group).
 * 
 * @author jaap.branderhorst@xs4all.nl
 */
public class FigTextGroup extends FigGroup {

    public final static int ROWHEIGHT = 17;
    protected boolean supressCalcBounds = false;

    /**
     * Adds a FigText to the list with figs. Makes sure that the
     * figtexts do not overlap.
     * @see org.tigris.gef.presentation.FigGroup#addFig(Fig)
     */
    public void addFig(Fig f) {
	super.addFig(f);
        updateFigTexts();
        calcBounds();
    }
    
    /**
     * Updates the FigTexts. FigTexts without text (equals "") are not shown. 
     * The rest of the figtexts are shown non-overlapping. The first figtext 
     * added (via addFig) is shown at the bottom of the FigTextGroup.
     */
    protected void updateFigTexts() {
        Iterator it = getFigs(null).iterator();
        int height = 0;
        while (it.hasNext()) {
            FigText fig = (FigText) it.next();
            if (fig.getText().equals("")) {
                fig.setHeight(0);
            } else {
                fig.setHeight(ROWHEIGHT);
            }
            fig.startTrans();
            fig.setX(getX());
            fig.setY(getY() + height);
            fig.endTrans();
            height += fig.getHeight();
        }
        // calcBounds();
    }
            

    /**
     * @see org.tigris.gef.presentation.Fig#calcBounds()
     */
    public void calcBounds() {
	updateFigTexts();
        if (!supressCalcBounds) {
	    super.calcBounds();
            // get the widest of all textfigs
            // calculate the total height
            int maxWidth = 0;
            int height = 0;
            Iterator it = getFigs(null).iterator();
            while (it.hasNext()) {
                FigText fig = (FigText) it.next();
                if (fig.getText().equals("")) {
                    fig.setBounds(fig.getX(), fig.getY(), fig.getWidth(), 0);
                } 
                else {
                    if (fig.getWidth() > maxWidth) {
                        maxWidth = fig.getWidth();
                    }
                    if (!fig.getText().equals("")) {
                        fig.setHeight(ROWHEIGHT);
                    }
                    height += fig.getHeight();
                }
            }        
            _w = maxWidth;
            _h = height;
        }
    }   

    /**
     * @see org.tigris.gef.presentation.Fig#delete()
     */
    public void delete() {
        Iterator it = getFigs(null).iterator();
        while (it.hasNext()) {
            ((Fig) it.next()).delete();
        }
        super.delete();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#dispose()
     */
    public void dispose() {
        Iterator it = getFigs(null).iterator();
        while (it.hasNext()) {
            ((Fig) it.next()).dispose();
        }
        super.dispose();
    }

}
