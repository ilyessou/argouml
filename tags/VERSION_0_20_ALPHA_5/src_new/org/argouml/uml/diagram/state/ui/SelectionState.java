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

package org.argouml.uml.diagram.state.ui;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.Model;
import org.tigris.gef.base.ModeCreateEdgeAndNode;
import org.argouml.uml.diagram.ui.SelectionNodeClarifiers;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

/**
 * @author jrobbins@ics.uci.edu
 */
public class SelectionState extends SelectionNodeClarifiers {

    private static final Logger LOG = Logger.getLogger(SelectionState.class);
    ////////////////////////////////////////////////////////////////
    // constants
    private static Icon trans =
	ResourceLoaderWrapper.lookupIconResource("Transition");

    ////////////////////////////////////////////////////////////////
    // instance varables
    private boolean showIncoming = true;
    private boolean showOutgoing = true;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new SelectionState for the given Fig.
     *
     * @param f The given Fig.
     */
    public SelectionState(Fig f) {
	super(f);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @param b true if the buton is enabled
     */
    public void setIncomingButtonEnabled(boolean b) {
	showIncoming = b;
    }

    /**
     * @param b true if the buton is enabled
     */
    public void setOutgoingButtonEnabled(boolean b) {
	showOutgoing = b;
    }

    /**
     * @see org.tigris.gef.base.Selection#hitHandle(java.awt.Rectangle,
     * org.tigris.gef.presentation.Handle)
     */
    public void hitHandle(Rectangle r, Handle h) {
	super.hitHandle(r, h);
	if (h.index != -1) {
	    return;
	}
	if (!isPaintButtons()) {
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
	int iw = trans.getIconWidth();
	int ih = trans.getIconHeight();
	if (showOutgoing && hitLeft(cx + cw, cy + ch / 2, iw, ih, r)) {
	    h.index = 12;
	    h.instructions = "Add an outgoing transition";
	} else if (showIncoming && hitRight(cx, cy + ch / 2, iw, ih, r)) {
	    h.index = 13;
	    h.instructions = "Add an incoming transition";
	} else {
	    h.index = -1;
	    h.instructions = "Move object(s)";
	}
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#paintButtons(Graphics)
     */
    public void paintButtons(Graphics g) {
	int cx = getContent().getX();
	int cy = getContent().getY();
	int cw = getContent().getWidth();
	int ch = getContent().getHeight();
	if (showOutgoing) {
	    paintButtonLeft(trans, g, cx + cw, cy + ch / 2, 12);
	}
	if (showIncoming) {
	    paintButtonRight(trans, g, cx, cy + ch / 2, 13);
	}
    }

    /**
     * @see org.tigris.gef.base.Selection#dragHandle(int, int, int, int,
     * org.tigris.gef.presentation.Handle)
     */
    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
	if (hand.index < 10) {
	    setPaintButtons(false);
	    super.dragHandle(mX, mY, anX, anY, hand);
	    return;
	}
	int cx = getContent().getX(), cy = getContent().getY();
	int cw = getContent().getWidth(), ch = getContent().getHeight();
	Object edgeType = null;
	Object nodeType = Model.getMetaTypes().getSimpleState();

	Editor ce = Globals.curEditor();
	GraphModel gm = ce.getGraphModel();
	if (!(gm instanceof MutableGraphModel)) {
	    return;
	}

	int bx = mX, by = mY;
	boolean reverse = false;
	switch (hand.index) {
	case 12 : //add outgoing
	    edgeType = Model.getMetaTypes().getTransition();
	    by = cy + ch / 2;
	    bx = cx + cw;
	    break;
	case 13 : // add incoming
	    edgeType = Model.getMetaTypes().getTransition();
	    reverse = true;
	    by = cy + ch / 2;
	    bx = cx;
	    break;
	default :
	    LOG.warn("invalid handle number");
	    break;
	}
	if (edgeType != null && nodeType != null) {
	    ModeCreateEdgeAndNode m =
		new ModeCreateEdgeAndNode(ce, edgeType, nodeType, false);
	    m.setup((FigNode) getContent(), getContent().getOwner(),
	            bx, by, reverse);
	    ce.pushMode(m);
	}
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#getNewNode(int)
     */
    protected Object getNewNode(int buttonCode) {
	return Model.getStateMachinesFactory().createSimpleState();
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeAbove(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeAbove(MutableGraphModel mgm, Object newNode) {
	return mgm.connect(newNode, getContent().getOwner(),
			   (Class) Model.getMetaTypes().getTransition());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeLeft(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
	return gm.connect(newNode, getContent().getOwner(),
			  (Class) Model.getMetaTypes().getTransition());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeRight(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
	return gm.connect(getContent().getOwner(), newNode,
			  (Class) Model.getMetaTypes().getTransition());
    }

    /**
     * To enable this we need to add an icon.
     * @see org.tigris.gef.base.SelectionButtons#createEdgeToSelf(
     *         org.tigris.gef.graph.MutableGraphModel)
     */
    protected Object createEdgeToSelf(MutableGraphModel gm) {
	return gm.connect(
			  getContent().getOwner(),
			  getContent().getOwner(),
			  (Class) Model.getMetaTypes().getTransition());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeUnder(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeUnder(MutableGraphModel gm, Object newNode) {
	return gm.connect(getContent().getOwner(), newNode,
			  (Class) Model.getMetaTypes().getTransition());
    }



} /* end class SelectionState */
