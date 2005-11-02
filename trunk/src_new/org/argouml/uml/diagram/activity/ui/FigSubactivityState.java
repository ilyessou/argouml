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

package org.argouml.uml.diagram.activity.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.Iterator;

import org.argouml.model.Model;
import org.argouml.notation.Notation;
import org.argouml.uml.diagram.state.ui.FigStateVertex;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigRRect;
import org.tigris.gef.presentation.FigText;


/**
 * Class to display graphics for a UML SubactivityState in a diagram.
 *
 * @author MVW
 */
public class FigSubactivityState extends FigStateVertex {
    ////////////////////////////////////////////////////////////////
    // constants

    private static final int PADDING = 8;

    private static final int X = 10;
    private static final int Y = 10;
    private static final int W = 90;
    private static final int H = 25;

    private static final int SX = 3;
    private static final int SY = 3;
    private static final int SW = 9;
    private static final int SH = 5;

    ////////////////////////////////////////////////////////////////
    // instance variables

    private FigRRect cover;
    //private FigGroup icon;

    private FigRRect s1;
    private FigRRect s2;
    private FigLine s3;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Main Constructor (called from file loading).
     */
    public FigSubactivityState() {
        FigRRect bigPort = new FigRRect(X, Y, W, H, Color.cyan, Color.cyan);
        bigPort.setCornerRadius(bigPort.getHeight() / 2);
        cover = new FigRRect(X, Y, W, H, Color.black, Color.white);
        cover.setCornerRadius(getHeight() / 2);

        bigPort.setLineWidth(0);

        //icon = makeSubStatesIcon(X + W, Y); // the substate icon in the corner

        getNameFig().setLineWidth(0);
        getNameFig().setBounds(10 + PADDING, 10, 90 - PADDING * 2, 25);
        getNameFig().setFilled(false);
        getNameFig().setReturnAction(FigText.INSERT);

        // add Figs to the FigNode in back-to-front order
        addFig(bigPort);
        addFig(cover);
        addFig(getNameFig());
        //addFig(icon);

        makeSubStatesIcon(X + W, Y);

        setBigPort(bigPort);
        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * @param x the x-coordinate of the right corner
     * @param y the y coordinate of the bottom corner
     */
    private void makeSubStatesIcon(int x, int y) {
        s1 = new FigRRect(x - 22, y + 3, 8, 6, Color.black, Color.white);
        s2 = new FigRRect(x - 11, y + 9, 8, 6, Color.black, Color.white);
        s1.setFilled(true);
        s2.setFilled(true);
        s1.setLineWidth(1);
        s2.setLineWidth(1);
        s1.setCornerRadius(SH);
        s2.setCornerRadius(SH);
        s3 = new FigLine(x - 18, y + 6, x - 7, y + 12, Color.black);

        addFig(s3); // add them back to front
        addFig(s1);
        addFig(s2);
    }

    /**
     * Constructor that hooks the Fig into
     * an existing UML model element.
     *
     * @param gm ignored!
     * @param node owner, i.e. the UML element
     */
    public FigSubactivityState(GraphModel gm, Object node) {
        this();
        setOwner(node);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#placeString()
     */
    public String placeString() {
        return "new SubactivityState";
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigSubactivityState figClone = (FigSubactivityState) super.clone();
        Iterator it = figClone.getFigs().iterator();
        figClone.setBigPort((FigRRect) it.next());
        figClone.cover = (FigRRect) it.next();
        figClone.setNameFig((FigText) it.next());
        return figClone;
    }

    ////////////////////////////////////////////////////////////////
    // Fig accessors

    /**
     * @see org.tigris.gef.presentation.Fig#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        Dimension nameDim = getNameFig().getMinimumSize();
        int w = nameDim.width + PADDING * 2;
        int h = nameDim.height + PADDING;
        return new Dimension(Math.max(w, W / 2), Math.max(h, H / 2));
    }

    /**
     * Override setBounds to keep shapes looking right.
     *
     * @see org.tigris.gef.presentation.Fig#setBoundsImpl(int, int, int, int)
     */
    protected void setBoundsImpl(int x, int y, int w, int h) {
        if (getNameFig() == null) {
            return;
        }
        Rectangle oldBounds = getBounds();

        getNameFig().setBounds(x + PADDING, y, w - PADDING * 2, h - PADDING);
        getBigPort().setBounds(x, y, w, h);
        cover.setBounds(x, y, w, h);
        ((FigRRect) getBigPort()).setCornerRadius(h);
        cover.setCornerRadius(h);

        s1.setBounds(x + w - 2 * (SX + SW), y + h - 1 * (SY + SH), SW, SH);
        s2.setBounds(x + w - 1 * (SX + SW), y + h - 2 * (SY + SH), SW, SH);
        s3.setShape(x + w - (SX * 2 + SW + SW / 2), y + h - (SY + SH / 2),
                x + w - (SX + SW / 2), y + h - (SY * 2 + SH + SH / 2));

        calcBounds();
        updateEdges();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    public void setLineColor(Color col) {
        cover.setLineColor(col);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getLineColor()
     */
    public Color getLineColor() {
        return cover.getLineColor();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setFillColor(java.awt.Color)
     */
    public void setFillColor(Color col) {
        cover.setFillColor(col);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getFillColor()
     */
    public Color getFillColor() {
        return cover.getFillColor();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    public void setFilled(boolean f) {
        cover.setFilled(f);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getFilled()
     */
    public boolean getFilled() {
        return cover.getFilled();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    public void setLineWidth(int w) {
        cover.setLineWidth(w);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getLineWidth()
     */
    public int getLineWidth() {
        return cover.getLineWidth();
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        if (mee.getSource() == getOwner()
                && mee.getPropertyName().equals("entry")) {
            if (mee.getNewValue() != null) {
                Model.getPump().addModelEventListener(this,
                                            mee.getNewValue(), "script");
            } else
                if (mee.getOldValue() != null) {
                    Model.getPump().removeModelEventListener(this,
                                            mee.getOldValue(), "script");
                }
            updateNameText();
            damage();
        } else
            if (Model.getFacade().getEntry(getOwner()) == mee.getSource()) {
                updateNameText();
                damage();
            }

    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateNameText()
     */
    protected void updateNameText() {
        if (getOwner() != null) {
            String s = Notation.generate(this, getOwner());
            if (s != null) {
                getNameFig().setText(s);
            }
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        /*if (ft == getNameFig() && this.getOwner() != null) {
            //TODO: Write this function in ParserDisplay. Uncomment then.
            ParserDisplay.SINGLETON.parseSubactionState(ft.getText(),
                    this.getOwner());
        } else*/
        super.textEdited(ft);
    }
} /* end class FigSubactivityState */
