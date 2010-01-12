/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.sequence2.diagram;

import java.awt.Polygon;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;

import org.argouml.model.Model;
import org.argouml.uml.diagram.ui.SelectionNodeClarifiers2;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.Handle;

/**
 * A custom select object to handle the special requirements of reshaping a
 * classifier role.
 * 
 * @author Bob Tarling
 */
class SelectionClassifierRole extends SelectionNodeClarifiers2 {

    /**
     * The constructor.
     * 
     * @param f
     *                the fig
     */
    public SelectionClassifierRole(Fig f) {
       super(f);
    }

    /**
     * Makes sure that dragging on the CR keeps them all aligned and resizing
     * doesn't force FigMessages overlaying.
     * 
     * @param mX
     *                New X position (aka current mouse X position)
     * @param mY
     *                New Y position (aka current mouse Y position)
     * @param anX
     *                Old X position
     * @param anY
     *                Old Y position
     * @param hand
     *                The handle being dragged
     */
    @Override
    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {

        if (!getContent().isResizable()) {
            return;
        }

        final List<Fig> figs = getContent().getLayer().getContents();

        int minimumHeight = 0;
        for (Fig workOnFig : figs) {
            if (workOnFig instanceof FigClassifierRole
                    && workOnFig.getMinimumSize().height > minimumHeight) {
                minimumHeight = workOnFig.getMinimumSize().height;
            }
        }

        int deltaY = mY - getContent().getY();
        
        // vertical resizing
        switch (hand.index) {
        case Handle.NORTHWEST:
        case Handle.NORTH:
        case Handle.NORTHEAST:
            int newHeight = getContent().getHeight() - deltaY;
            if (newHeight < minimumHeight) {
                newHeight = minimumHeight;
                deltaY = getContent().getHeight() - newHeight;
            }
            
            final HashMap<Fig, Polygon> polygonsByFig =
            	new HashMap<Fig, Polygon>();
            
            // There is a bug in GEF where positioning nodes can affect
            // edge positions. We need to do 3 iterations to protect
            // against that.
            
            // 1. Remember current message paths
            for (Fig workOnFig : figs) {
                if (workOnFig instanceof FigMessage && 
                		!((FigMessage) workOnFig).isSelfMessage()) {
                    polygonsByFig.put(
                    		workOnFig,
                    		((FigMessage) workOnFig).getPolygon());
                }
            }

            // 2. Reposition and resize nodes
            for (Fig workOnFig : figs) {
                if (workOnFig instanceof FigClassifierRole) {
                    workOnFig.setHeight(newHeight);
                    workOnFig.translate(0, deltaY);
                }
            }

            // 3. Now reposition messages based on their original position
            for (Fig workOnFig : figs) {
                if (workOnFig instanceof FigMessage) {
                	if (((FigMessage) workOnFig).isSelfMessage()) {
                		((FigMessageSpline) ((FigMessage) workOnFig).getFig())
                				.translateFig(0, deltaY);
                	} else {
                		polygonsByFig.get(workOnFig).translate(0, deltaY);
                		((FigMessage) workOnFig).setPolygon(
                				polygonsByFig.get(workOnFig));
                	}
                }
            }
            break;
        case Handle.SOUTH:
        case Handle.SOUTHEAST:
        case Handle.SOUTHWEST:
            newHeight = deltaY;
            if (newHeight < minimumHeight) {
                newHeight = minimumHeight;
            }
            for (Fig workOnFig : figs) {
                if (workOnFig instanceof FigClassifierRole) {
                    workOnFig.setHeight(newHeight);
                }
            }
        default:
        }

        final Fig workOnFig = getContent();
        
        int oldCenterX = 0;
        int newCenterX = 0;
        // Compute the initial center position of the CR
        if (workOnFig instanceof FigClassifierRole) {
        	FigClassifierRole f = (FigClassifierRole) workOnFig;
        	oldCenterX = f.getWidth() / 2 + f.getX();
        }
        
        // horizontal resizing
        switch (hand.index) {
        case Handle.NORTHWEST:
        case Handle.SOUTHWEST:
            workOnFig.setWidth(workOnFig.getX() - mX + workOnFig.getWidth());
            workOnFig.setX(mX);
            break;
        case Handle.NORTHEAST:
        case Handle.SOUTHEAST:
            workOnFig.setWidth(mX - workOnFig.getX());
            break;
        default:
        }

        // Compute the final center position of the CR
        if (workOnFig instanceof FigClassifierRole) {
        	FigClassifierRole f = (FigClassifierRole) workOnFig;
        	newCenterX = f.getWidth() / 2 + f.getX();
        }
        
        // Self messages act differently so it is needed to move them separetly.
        // Only self messages of selected CR should be horizontally translated.
        for (Fig fig : figs) {
        	if (fig instanceof FigMessage && ((FigMessage) fig).isSelfMessage()
        			&& Model.getCoreHelper().getDestination(fig.getOwner()).
        					equals(workOnFig.getOwner())) {
        		((FigMessageSpline) ((FigMessage) fig).getFig())
	        			.translateFig(newCenterX - oldCenterX, 0);
        	}
		}
        
    }

    @Override
    protected Object getNewNode(int index) {
        return null;
    }

    @Override
    protected Icon[] getIcons() {
        return null;
    }

    @Override
    protected String getInstructions(int index) {
        return null;
    }

    @Override
    protected Object getNewEdgeType(int index) {
        return null;
    }

    @Override
    protected Object getNewNodeType(int index) {
        return null;
    }
}
