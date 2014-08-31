/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Mode;
import org.tigris.gef.base.ModeCreateEdgeAndNode;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.ModePlace;
import org.tigris.gef.base.SelectionButtons;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

/**
 * Enhanced version of SelectionNodeClarifiers with the new methods
 * necessary for the enhanced support marked as abstract so that implementors
 * are forced to implement them.  SelectionNodeClarifiers is simple
 * extension of this which implements null versions of the required
 * methods for backward compatibility with the previous implementation.
 * <p>
 * To upgrade subtypes of SelectionNodeClarifiers, change them to
 * extend this class instead and implement the required abstract methods.
 * The methods paintButtons, dragHandle, hitHandle, and createEdge* can
 * all usually be removed.
 *
 * @author jrobbins
 * @author Tom Morris
 */
public abstract class SelectionNodeClarifiers2 extends SelectionButtons {

    private static final Logger LOG =
        Logger.getLogger(SelectionNodeClarifiers2.class.getName());

    /** Base index of array */
    protected static final int BASE = 10;
    /** Top Handle */
    protected static final int TOP = 10;
    /** Bottom Handle */
    protected static final int BOTTOM = 11;
    /** Left Handle */
    protected static final int LEFT = 12;
    /** Right Handle */
    protected static final int RIGHT = 13;
    /** Lower left corner Handle */
    protected static final int LOWER_LEFT = 14;

    private static final int OFFSET = 2;

    private Object newEdge = null;

    private int button;

    /**
     * Construct a new SelectionNodeClarifiers for the given Fig
     *
     * @param f
     *            the given Fig
     */
    public SelectionNodeClarifiers2(Fig f) {
        super(f);
    }

    /*
     * @see org.tigris.gef.base.SelectionButtons#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        final Mode topMode = Globals.curEditor().getModeManager().top();
        if (!(topMode instanceof ModePlace)) {
            // If the user has selected ModePlace either by a diagram
            // tool or AddToDiagram then we don't want to show the
            // clarifiers.
            ((Clarifiable) getContent()).paintClarifiers(g);
        }
        super.paint(g);
    }

    /*
     * @see org.tigris.gef.base.SelectionButtons#paintButtons(Graphics)
     */
    public void paintButtons(Graphics g) {
        final Mode topMode = Globals.curEditor().getModeManager().top();
        if (!(topMode instanceof ModePlace)) {
            // If the user has selected ModePlace either by a diagram
            // tool or AddToDiagram then we don't want to show the
            // toolbelt items.
            Icon[] icons = getIcons();
            if (icons == null) {
                return;
            }
            int cx = getContent().getX();
            int cy = getContent().getY();
            int cw = getContent().getWidth();
            int ch = getContent().getHeight();

            if (icons[0] != null) {
                paintButtonAbove(icons[0], g, cx + cw / 2, cy - OFFSET, TOP);
            }
            if (icons[1] != null) {
                paintButtonBelow(icons[1], g, cx + cw / 2, cy + ch + OFFSET,
                        BOTTOM);
            }
            if (icons[2] != null) {
                paintButtonLeft(icons[2], g, cx - OFFSET, cy + ch / 2, LEFT);
            }
            if (icons[3] != null) {
                paintButtonRight(icons[3], g, cx + cw + OFFSET, cy + ch / 2,
                        RIGHT);
            }
            if (icons[4] != null) {
                paintButtonLeft(icons[4], g, cx - OFFSET, cy + ch, LOWER_LEFT);
            }
        }
    }

    /*
     * @see org.tigris.gef.base.SelectionButtons#getNewNode(int)
     */
    protected Object getNewNode(int arg0) {
        return null;
    }

    /**
     * Compute handle selection, if any, from cursor location.
     *
     * @param cursor
     *            cursor point represented by a 0-size rectangle
     * @param h
     *            handle in which to return selected Handle information (output
     *            parameter). A handle index of -1 indicates that the cursor is
     *            not over any handle.
     *
     * If GEF had any API documentation you could see the following:
     * @see org.tigris.gef.base.SelectionResize#hitHandle(java.awt.Rectangle,
     *      org.tigris.gef.presentation.Handle)
     */
    public void hitHandle(Rectangle cursor, Handle h) {
        super.hitHandle(cursor, h);
        if (h.index != -1) {
            // super implementation found a hit
            return;
        }
        if (!isPaintButtons()) {
            return;
        }
        Icon[] icons = getIcons();
        if (icons == null) {
            return;
        }
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.size() != 1) {
            return;
        }
        ModeManager mm = ce.getModeManager();
        if (mm.includes(ModeModify.class) && getPressedButton() == -1) {
            return;
        }
        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();

        /*
         * Crazy numbering scheme at work here.  Here's how the handle numbers
         * are laid out.  Values 0-7 are defined by GEF and go left to
         * right, top to bottom (ie not clockwise or counterclockwise).
         * Values 10-14 zigzag North, South, West, East, Southwest.
         * If you can correctly guess where 15 will go, you should buy
         * a lottery ticket immediately.
         *  <pre>
         *            10
         *     0-------1-------2
         *     |               |
         *  12 3               4 13
         *     |               |
         *  14 5-------6-------7
         *            11
         * </pre>
         */
        if (icons[0] != null && hitAbove(cx + cw / 2, cy,
                icons[0].getIconWidth(), icons[0].getIconHeight(),
                cursor)) {
            h.index = TOP;
        } else if (icons[1] != null && hitBelow(cx + cw / 2, cy + ch,
                icons[1].getIconWidth(), icons[1].getIconHeight(),
                cursor)) {
            h.index = BOTTOM;
        } else if (icons[2] != null && hitLeft(cx, cy + ch / 2,
                icons[2].getIconWidth(), icons[2].getIconHeight(),
                cursor)) {
            h.index = LEFT;
        } else if (icons[3] != null && hitRight(cx + cw, cy + ch / 2,
                icons[3].getIconWidth(), icons[3].getIconHeight(),
                cursor)) {
            h.index = RIGHT;
        } else if (icons[4] != null && hitLeft(cx, cy + ch,
                icons[4].getIconWidth(), icons[4].getIconHeight(),
                cursor)) {
            h.index = LOWER_LEFT;
        } else {
            h.index = -1;
        }
        if (h.index == -1) {
            h.instructions = getInstructions(15);
        } else {
            h.instructions = getInstructions(h.index);
        }
    }

    /*
     * @see org.tigris.gef.base.Selection#dragHandle(int, int, int, int,
     * org.tigris.gef.presentation.Handle)
     */
    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {

        // Don't allow drag outside of bounds of diagram
        mX = Math.max(mX, 0);
        mY = Math.max(mY, 0);

        if (hand.index < 10) {
            setPaintButtons(false);
            super.dragHandle(mX, mY, anX, anY, hand);
            return;
        }
        if (!isDraggableHandle(hand.index)) {
            return;
        }
        int cx = getContent().getX(), cy = getContent().getY();
        int cw = getContent().getWidth(), ch = getContent().getHeight();

        int bx = mX, by = mY;

        // Remember what handle was clicked for the case where the drag
        // is released over empty space
        button = hand.index;

        switch (hand.index) {
        case TOP:
            by = cy;
            bx = cx + cw / 2;
            break;
        case BOTTOM:
            by = cy + ch;
            bx = cx + cw / 2;
            break;
        case LEFT:
            by = cy + ch / 2;
            bx = cx;
            break;
        case RIGHT:
            by = cy + ch / 2;
            bx = cx + cw;
            break;
        case LOWER_LEFT:
            by = cy + ch;
            bx = cx;
            break;
        default:
            LOG.log(Level.WARNING, "invalid handle number");
            break;
        }

        Object nodeType = getNewNodeType(hand.index);
        Object edgeType = getNewEdgeType(hand.index);
        boolean reverse = isReverseEdge(hand.index);

        if (edgeType != null && nodeType != null) {
            Editor ce = Globals.curEditor();
            ModeCreateEdgeAndNode m =
                getNewModeCreateEdgeAndNode(ce,
                        edgeType, isEdgePostProcessRequested(), this);
            m.setup((FigNode) getContent(), getContent().getOwner(),
                    bx, by, reverse);
            ce.pushMode(m);
        }
    }

    /**
     * Override this to implement post-processing.
     *
     * @param ce the current Editor
     * @param edgeType the new edge type
     * @param postProcess true if post-processing is wanted
     * @param nodeCreator this class will create the node
     * @return the ModeCreate
     */
    protected ModeCreateEdgeAndNode getNewModeCreateEdgeAndNode(
            Editor ce, Object edgeType, boolean postProcess,
            SelectionNodeClarifiers2 nodeCreator) {
        return  new ModeCreateEdgeAndNode(ce,
              edgeType, postProcess, nodeCreator);
    }

    @Override
    public void buttonClicked(int buttonCode) {
        super.buttonClicked(buttonCode);
        if (isEdgePostProcessRequested()) {
            postProcessEdge2(newEdge);
        }
    }

    protected Object createEdgeAbove(MutableGraphModel gm, Object newNode) {
        return createEdge(gm, newNode, TOP);
    }

    protected Object createEdgeUnder(MutableGraphModel gm, Object newNode) {
        return createEdge(gm, newNode, BOTTOM);
    }

    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
        return createEdge(gm, newNode, LEFT);
    }

    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
        return createEdge(gm, newNode, RIGHT);
    }

    private Object createEdge(MutableGraphModel gm, Object newNode, int index) {
        if (isReverseEdge(index)) {
            newEdge = gm.connect(
                    newNode, getContent().getOwner(), getNewEdgeType(index));
        } else {
            newEdge = gm.connect(
                    getContent().getOwner(), newNode, getNewEdgeType(index));
        }
        return newEdge;
    }

    protected Object createEdgeToSelf(MutableGraphModel gm) {
        Object edge = gm.connect(
                getContent().getOwner(), getContent().getOwner(),
                getNewEdgeType(LOWER_LEFT));
        return edge;
    }

    /**
     * Get array of icons to use when drawing handles.
     * @return icon or null
     */
    protected abstract Icon[] getIcons();

    /**
     * Get the "instructions" string to pass to GEF for the given handle number.
     *
     * @param index
     *            handle number that is being dragged from
     * @return string or null
     */
    protected abstract String getInstructions(int index);

    /**
     * Get the node type to create when dragging from the given handle number.
     *
     * @param index
     *            handle number that is being dragged from
     * @return metatype for model element. Null to disallow drag.
     */
    protected abstract Object getNewNodeType(int index);

    /**
     * Get the edge type to create when dragging from the given handle number.
     *
     * @param index
     *            handle number that is being dragged from
     * @return metatype for model element. Null to disallow drag.
     */
    protected abstract Object getNewEdgeType(int index);

    /**
     * Get the node type to create when dragging from the given handle number.
     *
     * @param index
     *            handle number that is being dragged from
     * @return true to reverse direction of association from direction of drag.
     *         e.g. specialization instead of generalization.  Default
     *         implementation always returns false.
     */
    protected boolean isReverseEdge(int index) {
        return false;
    }

    /**
     * Get the draggability of a particular handle. Default implementation
     * always returns true. Override to return false for handles which shouldn't
     * be draggable (i.e. they only support clicks, not drags).
     *
     * @param index
     *            handle index to check draggability for
     * @return true if this handle is draggable, false otherwise
     */
    protected boolean isDraggableHandle(int index) {
        return true;
    }

    /**
     * Request post processing of edge by GEF after it is created using
     * {@link ModeCreateEdgeAndNode#ModeCreateEdgeAndNode(Editor, Object, Object, boolean)}
     *
     * @return true if postprocessing requested
     */
    protected boolean isEdgePostProcessRequested() {
        return false;
    }

    /**
     * @param newEdge the new edge to post-process
     */
    protected void postProcessEdge2(Object newEdge) {
        // do nothing by default
    }

    /**
     * @return index of last button/handle that was clicked
     */
    protected int getButton() {
        return button;
    }

}
