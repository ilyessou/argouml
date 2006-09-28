// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.uml.diagram.deployment.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.ModelFacade;
import org.argouml.uml.diagram.ui.ModeCreateEdgeAndNode;
import org.argouml.uml.diagram.ui.SelectionWButtons;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

public class SelectionComponent extends SelectionWButtons {
    /**
     * @deprecated by Linus Tolke as of 0.15.7. Will be removed.
     *             Use your own Logger!
     */
    protected static Logger cat = 
        Logger.getLogger(SelectionComponent.class);

    private static final Logger LOG = 
        Logger.getLogger(SelectionComponent.class);
    ////////////////////////////////////////////////////////////////
    // constants
    public static Icon dep =
	ResourceLoaderWrapper.getResourceLoaderWrapper()
	    .lookupIconResource("Dependency");
    public static Icon depRight = 
	ResourceLoaderWrapper.getResourceLoaderWrapper()
	    .lookupIconResource("DependencyRight");


    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new SelectionComponent for the given Fig.
     *
     * @param f The given Fig.
     */
    public SelectionComponent(Fig f) { super(f); }

    public void hitHandle(Rectangle r, Handle h) {
	super.hitHandle(r, h);
	if (h.index != -1) return;
	if (!_paintButtons) return;
	Editor ce = Globals.curEditor();
	SelectionManager sm = ce.getSelectionManager();
	if (sm.size() != 1) return;
	ModeManager mm = ce.getModeManager();
	if (mm.includes(ModeModify.class) && _pressedButton == -1) return;
	int cx = _content.getX();
	int cy = _content.getY();
	int cw = _content.getWidth();
	int ch = _content.getHeight();
	int aw = dep.getIconWidth();
	int ah = dep.getIconHeight();
	if (hitAbove(cx + cw / 2, cy, aw, ah, r)) {
	    h.index = 10;
	    h.instructions = "Add a component";
	}
	else if (hitBelow(cx + cw / 2, cy + ch, aw, ah, r)) {
	    h.index = 11;
	    h.instructions = "Add a component";
	}
	else if (hitLeft(cx + cw, cy + ch / 2, aw, ah, r)) {
	    h.index = 12;
	    h.instructions = "Add a component";
	}
	else if (hitRight(cx, cy + ch / 2, aw, ah, r)) {
	    h.index = 13;
	    h.instructions = "Add a component";
	}
	else {
	    h.index = -1;
	    h.instructions = "Move object(s)";
	}
    }


    /**
     * @see SelectionWButtons#paintButtons(Graphics)
     */
    public void paintButtons(Graphics g) {
	int cx = _content.getX();
	int cy = _content.getY();
	int cw = _content.getWidth();
	int ch = _content.getHeight();
	paintButtonAbove(dep, g, cx + cw / 2, cy, 10);
	paintButtonBelow(dep, g, cx + cw / 2, cy + ch, 11);
	paintButtonLeft(depRight, g, cx + cw, cy + ch / 2, 12);
	paintButtonRight(depRight, g, cx, cy + ch / 2, 13);
    }


    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
	if (hand.index < 10) {
	    _paintButtons = false;
	    super.dragHandle(mX, mY, anX, anY, hand);
	    return;
	}
	int cx = _content.getX(), cy = _content.getY();
	int cw = _content.getWidth(), ch = _content.getHeight();
	int newX = cx, newY = cy, newW = cw, newH = ch;
	Dimension minSize = _content.getMinimumSize();
	int minWidth = minSize.width, minHeight = minSize.height;
	Class edgeClass = null;
	Class nodeClass = (Class) ModelFacade.COMPONENT;
	int bx = mX, by = mY;
	boolean reverse = false;
	switch (hand.index) {
	case 10: //add dep
	    edgeClass = (Class) ModelFacade.DEPENDENCY;
	    reverse = false;
	    by = cy;
	    bx = cx + cw / 2;
	    break;
	case 11: //add dep
	    edgeClass = (Class) ModelFacade.DEPENDENCY;
	    reverse = true;
	    by = cy + ch;
	    bx = cx + cw / 2;
	    break;
	case 12: //add dep
	    edgeClass = (Class) ModelFacade.DEPENDENCY;
	    reverse = false;
	    by = cy + ch / 2;
	    bx = cx + cw;
	    break;
	case 13: // add dep
	    edgeClass = (Class) ModelFacade.DEPENDENCY;
	    reverse = true;
	    by = cy + ch / 2;
	    bx = cx;
	    break;
	default:
	    LOG.warn("invalid handle number");
	    break;
	}
	if (edgeClass != null && nodeClass != null) {
	    Editor ce = Globals.curEditor();
	    ModeCreateEdgeAndNode m = new
		ModeCreateEdgeAndNode(ce, edgeClass, nodeClass, false);
	    m.setup((FigNode) _content, _content.getOwner(), bx, by, reverse);
	    ce.pushMode(m);
	}

    }

    public Object addCompClassAbove(MutableGraphModel mgm, Object component,
				    Object newComponent) {
        if (!ModelFacade.isAComponent(component)
	    || !ModelFacade.isAComponent(newComponent)) {
               throw new IllegalArgumentException();
        }
	return mgm.connect(component, newComponent,
                            (Class) ModelFacade.DEPENDENCY);
    }

    public Object addCompClassBelow(MutableGraphModel mgm, Object component,
				    Object newComponent) {
        if (!ModelFacade.isAComponent(component)
	    || !ModelFacade.isAComponent(newComponent)) {
               throw new IllegalArgumentException();
        }
	return mgm.connect(component, newComponent,
                            (Class) ModelFacade.DEPENDENCY);
    }
        
    public Object addCompClassRight(MutableGraphModel mgm, Object component,
				    Object newComponent) {
        if (!ModelFacade.isAComponent(component)
	    || !ModelFacade.isAComponent(newComponent)) {
               throw new IllegalArgumentException();
        }
	return mgm.connect(component, newComponent,
                            (Class) ModelFacade.DEPENDENCY);
    }

    public Object addCompClassLeft(MutableGraphModel mgm, Object component,
				    Object newComponent) {
        if (!ModelFacade.isAComponent(component)
	    || !ModelFacade.isAComponent(newComponent)) {
               throw new IllegalArgumentException();
        }
	return mgm.connect(component, newComponent, 
                            (Class) ModelFacade.DEPENDENCY);
    }

    /**
     * @see org.argouml.uml.diagram.ui.SelectionWButtons#getNewNode(int)
     */
    protected Object getNewNode(int buttonCode) {
        return UmlFactory.getFactory().getCore().createComponent();
    }

    /**
     * @see SelectionWButtons#createEdgeAbove(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeAbove(MutableGraphModel gm, Object newNode) {
        return gm.connect(_content.getOwner(), newNode,
                            (Class) ModelFacade.DEPENDENCY);
    }

    /**
     * @see SelectionWButtons#createEdgeLeft(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, _content.getOwner(),
                            (Class) ModelFacade.DEPENDENCY);
    }

    /**
     * @see SelectionWButtons#createEdgeRight(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
        return gm.connect(_content.getOwner(), newNode,
                            (Class) ModelFacade.DEPENDENCY);
    }

    /**
     * @see SelectionWButtons#createEdgeUnder(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeUnder(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, _content.getOwner(),
                            (Class) ModelFacade.DEPENDENCY);
    }

} /* end class SelectionComponent */
