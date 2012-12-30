/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
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

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.UmlModelMutator;
import org.argouml.model.Model;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.UndoableAction;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.CommentEdge;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.DiagramUtils;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPoly;

/**
 * Action to add a note aka comment. This action adds a Comment to 0..*
 * modelelements. <p>
 *
 * The modelelements that are present on the current diagram, are connected
 * graphically. All others are only annotated in the model.
 */
@UmlModelMutator
public class ActionAddNote extends UndoableAction {

    /**
     * The default position (x and y) of the new fig.
     */
    private static final int DEFAULT_POS = 20;

    /**
     * The distance (x and y) from other figs where we place this.
     */
    private static final int DISTANCE = 80;

    /**
     * The constructor. This action is not global, since it is never disabled.
     */
    public ActionAddNote() {
        super(Translator.localize("action.new-comment"),
                ResourceLoaderWrapper.lookupIcon("action.new-comment"));
        // Set the tooltip string:
        putValue(Action.SHORT_DESCRIPTION, 
                Translator.localize("action.new-comment"));
        putValue(Action.SMALL_ICON, ResourceLoaderWrapper
                .lookupIconResource("New Note"));
    }


    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        super.actionPerformed(ae); //update all tools' enabled status
        Collection targets = TargetManager.getInstance().getModelTargets();

        //Let's build the comment first, unlinked.
        ArgoDiagram diagram = DiagramUtils.getActiveDiagram();
        Object comment =
            Model.getCoreFactory().buildComment(null,
                diagram.getNamespace());
        MutableGraphModel mgm = (MutableGraphModel) diagram.getGraphModel();

        //Now, we link it to the modelelements which are represented by FigNode
        Object firstTarget = null;
        Iterator i = targets.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            Fig destFig = diagram.presentationFor(obj);
            if (destFig instanceof FigEdgeModelElement) {
                FigEdgeModelElement destEdge = (FigEdgeModelElement) destFig;
                destEdge.makeEdgePort();
                destFig = destEdge.getEdgePort();
                destEdge.calcBounds();
            }
            if (Model.getFacade().isAModelElement(obj)
                    && (!(Model.getFacade().isAComment(obj)))) {
                if (firstTarget == null) {
                    firstTarget = obj;
                }
                /* Prevent e.g. AssociationClasses from being added trice: */
                if (!Model.getFacade().getAnnotatedElements(comment)
                        .contains(obj)) {
                    Model.getCoreHelper().addAnnotatedElement(comment, obj);
                }
            }
        }

        //Create the Node Fig for the comment itself and draw it
        mgm.addNode(comment);
        // remember the fig for later
        Fig noteFig = diagram.presentationFor(comment);

        //Create the comment links and draw them
        i = Model.getFacade().getAnnotatedElements(comment).iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (diagram.presentationFor(obj) != null) {
                CommentEdge commentEdge = new CommentEdge(comment, obj);
                mgm.addEdge(commentEdge);
                FigEdge fe = (FigEdge) diagram.presentationFor(commentEdge);
                FigPoly fp = (FigPoly) fe.getFig();
                fp.setComplete(true);
            }
        }

        //Place the comment Fig on the nicest spot on the diagram
        noteFig.setLocation(calculateLocation(diagram, firstTarget, noteFig));

        //Select the new comment as target
        TargetManager.getInstance().setTarget(noteFig.getOwner());
    }

    /**
     * Calculate the position of the comment, based on the 1st target only.
     *
     * @param diagram The Diagram that we are working in.
     * @param firstTarget The object element of the first found comment.
     * @param noteFig The Fig for the comment.
     * @return The position where it should be placed.
     */
    private Point calculateLocation(
            ArgoDiagram diagram, Object firstTarget, Fig noteFig) {
        Point point = new Point(DEFAULT_POS, DEFAULT_POS);

        if (firstTarget == null) {
            return point;
        }

        Fig elemFig = diagram.presentationFor(firstTarget);
        if (elemFig == null) {
            return point;
        }

        if (elemFig instanceof FigEdgeModelElement) {
            elemFig = ((FigEdgeModelElement) elemFig).getEdgePort();
        }

        if (elemFig instanceof FigNode) {
            // TODO: We need a better algorithm.
            point.x = elemFig.getX() + elemFig.getWidth() + DISTANCE;
            point.y = elemFig.getY();
            // TODO: This can't depend on ProjectBrowser.  Alternate below
            Rectangle drawingArea =
                ProjectBrowser.getInstance().getEditorPane().getBounds();
            // Perhaps something like the following would work instead
//            Rectangle drawingArea =
//                Globals.curEditor().getJComponent().getVisibleRect();
            
            if (point.x + noteFig.getWidth() > drawingArea.getX()) {
                point.x = elemFig.getX() - noteFig.getWidth() - DISTANCE;

                if (point.x >= 0) {
                    return point;
                }

                point.x = elemFig.getX();
                point.y = elemFig.getY() - noteFig.getHeight() - DISTANCE;
                if (point.y >= 0) {
                    return point;
                }

                point.y = elemFig.getY() + elemFig.getHeight() + DISTANCE;
                if (point.y + noteFig.getHeight() > drawingArea.getHeight()) {
                    return new Point(0, 0);
                }
            }
        }

        return point;
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = 6502515091619480472L;
}
