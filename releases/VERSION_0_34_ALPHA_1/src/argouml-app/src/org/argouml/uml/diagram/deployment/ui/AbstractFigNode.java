/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007-2009 The Regents of the University of California. All
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

package org.argouml.uml.diagram.deployment.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.argouml.model.AssociationChangeEvent;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.Model;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Geometry;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigCube;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/**
 * Introduce abstract superclass for FigMNode & FigNodeInstance 
 * so that we can do proper inheritance.
 *
 * @author Tom Morris
 */
public abstract class AbstractFigNode extends FigNodeModelElement {

    /**
     * Offset in x & y for depth perspective lines of cube.
     * TODO: This is the same value as the member 'D'in 
     * {@link org.tigris.gef.presentation.FigCube}, but there is
     * nothing enforcing that correspondence.  Things will probably
     * break if they don't match.
     */
    protected static final int DEPTH = 20;
    private FigCube cover;
    private static final int DEFAULT_X = 10;
    private static final int DEFAULT_Y = 10;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 180;

    @Override
    protected Fig createBigPortFig() {
       Fig cpfr = new CubePortFigRect(DEFAULT_X, DEFAULT_Y - DEPTH, 
               DEFAULT_WIDTH + DEPTH, 
               DEFAULT_HEIGHT + DEPTH, DEPTH);
       cpfr.setFilled(false);
       cpfr.setLineWidth(0);
        return cpfr;
    }

    private void initFigs() {
        cover = new FigCube(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH,
                DEFAULT_HEIGHT, LINE_COLOR, FILL_COLOR);

        getNameFig().setLineWidth(0);
        getNameFig().setFilled(false);
        getNameFig().setJustification(0);

        addFig(getBigPort());
        addFig(cover);
        addFig(getStereotypeFig());
        addFig(getNameFig());
    }

    /**
     * Construct a new AbstractFigNode.
     * 
     * @param owner owning UML element
     * @param bounds position and size
     * @param settings render settings
     */
    public AbstractFigNode(Object owner, Rectangle bounds,
            DiagramSettings settings) {
        super(owner, bounds, settings);
        initFigs();
    }
    
    @Override
    public Object clone() {
        AbstractFigNode figClone = (AbstractFigNode) super.clone();
        Iterator it = figClone.getFigs().iterator();
        figClone.setBigPort((FigRect) it.next());
        figClone.cover = (FigCube) it.next();
        it.next();
        figClone.setNameFig((FigText) it.next());
        return figClone;
    }

    @Override
    public void setLineColor(Color c) {
        cover.setLineColor(c);
    }

    @Override
    public void setLineWidth(int w) {
        cover.setLineWidth(w);
    }


    @Override
    public boolean isFilled() {
        return cover.isFilled();
    }

    @Override
    public void setFilled(boolean f) {
        cover.setFilled(f);
    }

    @Override
    public Selection makeSelection() {
        return new SelectionNode(this);
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension stereoDim = getStereotypeFig().getMinimumSize();
        Dimension nameDim = getNameFig().getMinimumSize();
    
        int w = Math.max(stereoDim.width, nameDim.width + 1) + DEPTH;
        int h = stereoDim.height + nameDim.height + DEPTH;
        
        w = Math.max(3 * DEPTH, w); // so it still looks like a cube
        h = Math.max(3 * DEPTH, h);
        return new Dimension(w, h);
    }

    @Override
    protected void setStandardBounds(int x, int y, int w, int h) {
        if (getNameFig() == null) {
            return;
        }
        Rectangle oldBounds = getBounds();
        getBigPort().setBounds(x, y, w, h);
        cover.setBounds(x, y + DEPTH, w - DEPTH, h - DEPTH);

        Dimension stereoDim = getStereotypeFig().getMinimumSize();
        Dimension nameDim = getNameFig().getMinimumSize();
        getNameFig().setBounds(
                x + 4, y + DEPTH + stereoDim.height + 1,
                w - DEPTH - 8, nameDim.height);
        getStereotypeFig().setBounds(x + 1, y + DEPTH + 1,
                w - DEPTH - 2, stereoDim.height);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        firePropChange("bounds", oldBounds, getBounds());
        updateEdges();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        super.mouseClicked(me);
        setLineColor(LINE_COLOR);
    }

    @Override
    public void setEnclosingFig(Fig encloser) {
        if (encloser == null
                || (encloser != null
                && Model.getFacade().isANode(encloser.getOwner()))) {
            super.setEnclosingFig(encloser);
        }
    
        if (getLayer() != null) {
            // elementOrdering(figures);
            Collection contents = getLayer().getContents();
            Collection<FigEdgeModelElement> bringToFrontList = 
                new ArrayList<FigEdgeModelElement>();
            for (Object o : contents) {
                if (o instanceof FigEdgeModelElement) {
                    bringToFrontList.add((FigEdgeModelElement) o);
                }
            }
            for (FigEdgeModelElement figEdge : bringToFrontList) {
                figEdge.getLayer().bringToFront(figEdge);
            }
        }
    }

    @Override
    public boolean getUseTrapRect() {
        return true;
    }

    @Override
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        if (mee instanceof AssociationChangeEvent 
                || mee instanceof AttributeChangeEvent) {
            renderingChanged();
            updateListeners(getOwner(), getOwner());
            damage();
        }
    }

    @Override
    protected void updateListeners(Object oldOwner, Object newOwner) {
        Set<Object[]> l = new HashSet<Object[]>();
        if (newOwner != null) {
            // add the listeners to the newOwner
            l.add(new Object[] {newOwner, null});
            
            Collection c = Model.getFacade().getStereotypes(newOwner);
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Object st = i.next();
                l.add(new Object[] {st, "name"});
            }
        }
        updateElementListeners(l);
    }

    @Override
    public Point getClosestPoint(Point anotherPt) {
        Rectangle r = getBounds();
        int[] xs = {
            r.x,
            r.x + DEPTH,
            r.x + r.width,
            r.x + r.width,
            r.x + r.width - DEPTH,
            r.x,
            r.x,
        };
        int[] ys = {
            r.y + DEPTH,
            r.y,
            r.y,
            r.y + r.height - DEPTH,
            r.y + r.height,
            r.y + r.height,
            r.y + DEPTH,
        };
        Point p = Geometry.ptClosestTo(xs, ys, 7, anotherPt);
        return p;
    }

}
