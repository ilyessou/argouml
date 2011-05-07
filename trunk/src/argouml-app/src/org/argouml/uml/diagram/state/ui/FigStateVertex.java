/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

package org.argouml.uml.diagram.state.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.argouml.model.Model;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.activity.ui.SelectionActionState;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerDiagram;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 * Abstract class for a State Vertex 
 * which has behavior for nestable nodes in UML Statechart diagrams.
 */
public abstract class FigStateVertex extends FigNodeModelElement {

    /**
     * Constructor used by PGML parser.
     * 
     * @param owner the owning UML element
     * @param bounds rectangle describing bounds
     * @param settings rendering settings
     */
    public FigStateVertex(Object owner, Rectangle bounds, DiagramSettings settings) {
        super(owner, bounds, settings);
        this.allowRemoveFromDiagram(false);
    }

    /*
     * Overridden to make it possible to include a stateVertex in a composite
     * state.
     * @see org.tigris.gef.presentation.Fig#setEnclosingFig(org.tigris.gef.presentation.Fig)
     */
    @Override
    public void setEnclosingFig(Fig encloser) {
        LayerPerspective layer = (LayerPerspective) getLayer();
        
        // If the layer is null, then most likely we are being deleted.
        if (layer == null) {
            return;
        }

        super.setEnclosingFig(encloser);
        
        if (!(Model.getFacade().isAStateVertex(getOwner()))) {
            return;
        }
        Object stateVertex = getOwner();
        Object compositeState = null;
        if (encloser != null
                && (Model.getFacade().isACompositeState(encloser.getOwner()))) {
            compositeState = encloser.getOwner();
            ((FigStateVertex) encloser).redrawEnclosedFigs();
        } else {
            if (Model.getFacade().getUmlVersion().startsWith("1")) {
                compositeState = Model.getStateMachinesHelper().getTop(
                        Model.getStateMachinesHelper()
                                .getStateMachine(stateVertex));
            }
        }
        if (compositeState != null) {
            /* Do not change the model unless needed - avoids issue 4446: */
            if (Model.getFacade().getContainer(stateVertex) != compositeState) {
                Model.getStateMachinesHelper().setContainer(stateVertex,
                        compositeState);
            }
        }
    }

    /**
     * Method to draw a StateVertex Fig's enclosed figs.
     */
    public void redrawEnclosedFigs() {
        Editor editor = Globals.curEditor();
        if (editor != null && !getEnclosedFigs().isEmpty()) {
            LayerDiagram lay =
                ((LayerDiagram) editor.getLayerManager().getActiveLayer());
            for (Fig f : getEnclosedFigs()) {
                lay.bringInFrontOf(f, this);
                if (f instanceof FigNode) {
                    FigNode fn = (FigNode) f;
                    Iterator it = fn.getFigEdges().iterator();
                    while (it.hasNext()) {
                        lay.bringInFrontOf(((FigEdge) it.next()), this);
                    }
                    if (fn instanceof FigStateVertex) {
                        ((FigStateVertex) fn).redrawEnclosedFigs();
                    }
                }
            }
        }
    }

    /**
     * return selectors, depending whether we deal with activity or state
     * diagrams.
     *
     * {@inheritDoc}
     */
    @Override
    public Selection makeSelection() {
        Object pstate = getOwner();

        if (pstate != null) {

            if (Model.getFacade().getUmlVersion().startsWith("1")
                    && Model.getFacade().isAActivityGraph(
                    Model.getFacade().getStateMachine(
                            Model.getFacade().getContainer(pstate)))) {
                return new SelectionActionState(this);
            }
            return new SelectionState(this);
        }
        return null;
    }
    
    /**
     * Number of points to compute for gravity point circle.
     */
    private static final int CIRCLE_POINTS = 32;
    
    /**
     * Return a list of gravity points around circle which is enclosed
     * in the bounding box.  Convenience method for use by FigInitialState
     * and FigFinalState.
     * TODO: As this method is not required by all sub classes,
     * it would seem sensible to extend FigStateVertex with FigCircleVertex
     * and only have the relevant concrete Figs extend that and gain this
     * functionality.
     * @return a List of Points
     */
    List<Point> getCircleGravityPoints() {
        List<Point> ret = new ArrayList<Point>();
        int cx = getBigPort().getCenter().x;
        int cy = getBigPort().getCenter().y;
        double radius = getBigPort().getWidth() / 2 + 1;
        final double pi2 = Math.PI * 2;
        for (int i = 0; i < CIRCLE_POINTS; i++) {
            int x = (int) (cx + Math.cos(pi2 * i / CIRCLE_POINTS) * radius);
            int y = (int) (cy + Math.sin(pi2 * i / CIRCLE_POINTS) * radius);
            ret.add(new Point(x, y));
        }
        return ret;
    }

}
