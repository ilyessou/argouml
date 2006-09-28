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

package org.argouml.uml.diagram.static_structure.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.ui.CompartmentFigText;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigText;
import org.tigris.gef.presentation.Handle;

/**
 * Fig to show features in class or interface like attributes or operations.
 *
 * @since Dec 1, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class FigFeature extends CompartmentFigText {

    private class SelectionFeature extends Selection {

        /**
         * Constructor for SelectionFeature.
         * @param f
         */
        public SelectionFeature(Fig f) {
            super(f);
        }

        /**
         * Does nothing.
         * @see org.tigris.gef.base.Selection#dragHandle(int, int,
         * int, int, org.tigris.gef.presentation.Handle)
         */
        public void dragHandle(int mx, int my, int anX, int anY, Handle h) {
        }

        /**
         * Does nothing.
         * @see
         * org.tigris.gef.base.Selection#hitHandle(java.awt.Rectangle,
         * org.tigris.gef.presentation.Handle)
         */
        public void hitHandle(Rectangle r, Handle h) {
        }

        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN
                || e.getKeyCode() == KeyEvent.VK_UP) {

                Fig fig = getGroup().getGroup();
                if (fig instanceof FigClass) {
                    FigGroup group = (FigGroup) getGroup();
                // TODO: in future version of GEF call getFigs returning array
                    Object[] figs = group.getFigs().toArray();
                    for (int i = 1; i < figs.length; i++) {
			// the first element is no attr or oper
                        if (figs[i].equals(getContent())) {
                            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                if (i < figs.length - 1) {
                                    TargetManager.getInstance().setTarget(
                                        figs[i + 1]);
                                }
                            } else if (i > 1) {
                                TargetManager.getInstance().setTarget(
                                    figs[i - 1]);

                            }
                            e.consume();
                            return;
                        }
                    }
                }

            }
        }

    }

    /**
    * Constructor for FigFeature.
    * @param x x
    * @param y x
    * @param w w
    * @param h h
    * @param aFig the fig
    */
    public FigFeature(int x, int y, int w, int h, Fig aFig) {
        super(x, y, w, h, aFig);
        setFilled(false);
        setLineWidth(0);
        setFont(FigNodeModelElement.getLabelFont());
        setTextColor(Color.black);
        setTextFilled(false);
        setJustification(FigText.JUSTIFY_LEFT);
        setReturnAction(FigText.END_EDITING);
        setRightMargin(3);
        setLeftMargin(3);
    }

    /**
     * Via makeSelection we can add a custom selection class. This way
     * we can add a custom keyevent handler for our attributes or operations
     * @see org.tigris.gef.presentation.Fig#makeSelection()
     */
    public Selection makeSelection() {
        return new SelectionFeature(this);
    }

    public void setTextFilled(boolean filled) {
        super.setTextFilled(false);
    }

    public void setFilled(boolean filled) {
        super.setFilled(false);
    }
}
