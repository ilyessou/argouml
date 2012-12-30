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

package org.argouml.uml.diagram.use_case.ui;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.model.Model;
import org.argouml.uml.CommentEdge;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.DiagramEdgeSettings;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.DiagramUtils;
import org.argouml.uml.diagram.GraphChangeAdapter;
import org.argouml.uml.diagram.UmlDiagramRenderer;
import org.argouml.uml.diagram.static_structure.ui.FigEdgeNote;
import org.argouml.uml.diagram.ui.FigAssociation;
import org.argouml.uml.diagram.ui.FigDependency;
import org.argouml.uml.diagram.ui.FigGeneralization;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

// could be singleton

/**
 * This class defines a renderer object for UML Use Case Diagrams. In a
 * Class Diagram the following UML objects are displayed with the
 * following Figs:<p>
 *
 * <pre>
 *   UML Object       ---  Fig
 *   ---------------------------------------
 *   MActor           ---  FigActor
 *   MUseCase         ---  FigUseCase
 * </pre>
 *
 * Provides {@link #getFigNodeFor} to implement the
 * {@link org.tigris.gef.graph.GraphNodeRenderer} interface and
 * {@link #getFigEdgeFor} to implement the
 * {@link org.tigris.gef.graph.GraphEdgeRenderer} interface.<p>
 *
 * <em>Note</em>. Should be implemented as a singleton - we don't really
 * need a separate instance for each use case diagram.<p>
 *
 * @author abonner
 */
public class UseCaseDiagramRenderer extends UmlDiagramRenderer {

    static final long serialVersionUID = 2217410137377934879L;

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(UseCaseDiagramRenderer.class.getName());


    /**
     * Return a Fig that can be used to represent the given node.<p>
     *
     * @param gm    The graph model for which we are rendering.
     *
     * @param lay   The layer in the graph on which we want this figure.
     *
     * @param node  The node to be rendered (an model element object)
     *
     * @param styleAttributes an optional map of attributes to style the fig
     *
     * @return      The fig to be used, or <code>null</code> if we can't create
     *              one.
     */
    public FigNode getFigNodeFor(GraphModel gm, Layer lay, Object node,
            Map styleAttributes) {

        FigNodeModelElement figNode = null;

        // Create a new version of the relevant fig

        ArgoDiagram diag = DiagramUtils.getActiveDiagram();
        if (diag instanceof UMLDiagram
            && ((UMLDiagram) diag).doesAccept(node)) {
            figNode =
                (FigNodeModelElement) ((UMLDiagram) diag).drop(node, null);

        } else {
            LOG.log(Level.FINE,
                  "{0}: getFigNodeFor({1}, {2}, {3}) - cannot create this sort of node.",
                  new Object[]{this.getClass(), gm, lay, node});
            return null;
            // TODO: Shouldn't we throw an exception here?!?!
        }

        lay.add(figNode);
        figNode.setDiElement(
                GraphChangeAdapter.getInstance().createElement(gm, node));

        return figNode;
    }


    /**
     * Return a Fig that can be used to represent the given edge.<p>
     *
     * Generally the same code as for the ClassDiagram, since it's very
     * related to it. Deal with each of the edge types in turn.<p>
     *
     * @param gm    The graph model for which we are rendering.
     *
     * @param lay   The layer in the graph on which we want this figure.
     *
     * @param edge  The edge to be rendered (an model element object)
     *
     * @param styleAttributes an optional map of attributes to style the fig
     *
     * @return      The fig to be used, or <code>null</code> if we can't create
     *              one.
     *
     * @see org.tigris.gef.graph.GraphEdgeRenderer#getFigEdgeFor(
     *         org.tigris.gef.graph.GraphModel, org.tigris.gef.base.Layer,
     *         java.lang.Object, java.util.Map)
     */
    public FigEdge getFigEdgeFor(GraphModel gm, Layer lay, Object edge,
            Map styleAttributes) {

        LOG.log(Level.FINE, "making figedge for {0}", edge);

        if (edge == null) {
            throw new IllegalArgumentException("A model edge must be supplied");
        }

        assert lay instanceof LayerPerspective;
        ArgoDiagram diag = (ArgoDiagram) ((LayerPerspective) lay).getDiagram();
        DiagramSettings settings = diag.getDiagramSettings();

        FigEdge newEdge = null;

        if (Model.getFacade().isAAssociation(edge)) {
            final Object[] associationEnds =
                Model.getFacade().getConnections(edge).toArray();
            newEdge = new FigAssociation(
                    new DiagramEdgeSettings(
                            edge,
                            associationEnds[0],
                            associationEnds[1]),
                            settings);
            final FigNode sourceFig =
                getFigNodeForAssociationEnd(diag, associationEnds[0]);
            final FigNode destFig =
                getFigNodeForAssociationEnd(diag, associationEnds[1]);
            newEdge.setSourceFigNode(sourceFig);
            newEdge.setSourcePortFig(sourceFig);
            newEdge.setDestFigNode(destFig);
            newEdge.setDestPortFig(destFig);
        } else if (Model.getFacade().isAGeneralization(edge)) {
            newEdge = new FigGeneralization(edge, settings);
        } else if (Model.getFacade().isAExtend(edge)) {
            newEdge = new FigExtend(edge, settings);

            // The nodes at the two ends
            Object base = Model.getFacade().getBase(edge);
            Object extension = Model.getFacade().getExtension(edge);

            // The figs for the two end nodes
            FigNode baseFN = (FigNode) lay.presentationFor(base);
            FigNode extensionFN = (FigNode) lay.presentationFor(extension);

            // Link the new extend relationship in to the ends. Remember we
            // draw from the extension use case to the base use case.
            newEdge.setSourcePortFig(extensionFN);
            newEdge.setSourceFigNode(extensionFN);

            newEdge.setDestPortFig(baseFN);
            newEdge.setDestFigNode(baseFN);

        } else if (Model.getFacade().isAInclude(edge)) {
            newEdge = new FigInclude(edge, settings);

            Object base = Model.getFacade().getBase(edge);
            Object addition = Model.getFacade().getAddition(edge);

            // The figs for the two end nodes
            FigNode baseFN = (FigNode) lay.presentationFor(base);
            FigNode additionFN = (FigNode) lay.presentationFor(addition);

            // Link the new include relationship in to the ends
            newEdge.setSourcePortFig(baseFN);
            newEdge.setSourceFigNode(baseFN);

            newEdge.setDestPortFig(additionFN);
            newEdge.setDestFigNode(additionFN);
        } else if (Model.getFacade().isADependency(edge)) {
            newEdge = new FigDependency(edge, settings);

            // Where there is more than one supplier or client, take the first
            // element in each case. There really ought to be a check that
            // there are some here for safety.

            Object supplier =
                 ((Model.getFacade().getSuppliers(edge).toArray())[0]);
            Object client =
                 ((Model.getFacade().getClients(edge).toArray())[0]);

            // The figs for the two end nodes
            FigNode supplierFN = (FigNode) lay.presentationFor(supplier);
            FigNode clientFN = (FigNode) lay.presentationFor(client);

            // Link the new dependency in to the ends
            newEdge.setSourcePortFig(clientFN);
            newEdge.setSourceFigNode(clientFN);

            newEdge.setDestPortFig(supplierFN);
            newEdge.setDestFigNode(supplierFN);

        } else if (edge instanceof CommentEdge) {
            newEdge = new FigEdgeNote(edge, settings);
        }

        addEdge(lay, newEdge, edge);
        return newEdge;
    }

}
