// $Id$
// Copyright (c) 1996-99 The Regents of the University of California. All
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

// File: UseCaseDiagramRenderer.java
// Classes: UseCaseDiagramRenderer
// Original Author: abonner@ics.uci.edu
// $Id$

// 3 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support the
// Extend and Include relationships. JavaDoc added for clarity.


package org.argouml.uml.diagram.use_case.ui;

import org.apache.log4j.Logger;
import org.argouml.model.ModelFacade;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;
import org.argouml.uml.diagram.static_structure.ui.FigComment;
import org.argouml.uml.diagram.static_structure.ui.FigEdgeNote;
import org.argouml.uml.diagram.ui.FigAssociation;
import org.argouml.uml.diagram.ui.FigDependency;
import org.argouml.uml.diagram.ui.FigGeneralization;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

// could be singleton

/**
 * <p>This class defines a renderer object for UML Use Case Diagrams. In a
 *   Class Diagram the following UML objects are displayed with the
 *   following Figs:</p>
 *
 * <pre>
 *   UML Object       ---  Fig
 *   ---------------------------------------
 *   MActor           ---  FigActor
 *   MUseCase         ---  FigUseCase
 * </pre>
 *
 * <p>Provides {@link #getFigNodeFor} to implement the {@link
 *   GraphNodeRenderer} interface and {@link #getFigEdgeFor} to implement the
 *   {@link GraphEdgeRenderer} interface.</p>
 *
 * <p><em>Note</em>. Should be implemented as a singleton - we don't really
 *   need a separate instance for each use case diagram.</p>
 */

public class UseCaseDiagramRenderer
    implements GraphNodeRenderer, GraphEdgeRenderer 
{
    protected static Logger cat =
	Logger.getLogger(UseCaseDiagramRenderer.class);


    /**
     * <p>Return a Fig that can be used to represent the given node.</p>
     *
     * @param gm    The graph model for which we are rendering.
     *
     * @param lay   The layer in the graph on which we want this figure.
     *
     * @param node  The node to be rendered (an NSUML object)
     *
     * @return      The fig to be used, or <code>null</code> if we can't create
     *              one.
     */

    public FigNode getFigNodeFor(GraphModel gm, Layer lay, Object node) {

        // Create a new version of the relevant fig

        if (org.argouml.model.ModelFacade.isAActor(node)) {
            return new FigActor(gm, node);
        }
        else if (org.argouml.model.ModelFacade.isAUseCase(node)) {
            return new FigUseCase(gm, node);
        }
        else if (ModelFacade.isAComment(node)) {
            return new FigComment(gm, node);
        }

        // If we get here we were asked for a fig we can't handle.

        cat.debug(this.getClass().toString() +
		  ": getFigNodeFor(" + gm.toString() + ", " +
		  lay.toString() + ", " + node.toString() +
		  ") - cannot create this sort of node.");
        return null;
    }


    /**
     * <p>Return a Fig that can be used to represent the given edge.</p>
     *
     * <p>Generally the same code as for the ClassDiagram, since it's very
     *   related to it. Deal with each of the edge types in turn.</p>
     *
     * @param gm    The graph model for which we are rendering.
     *
     * @param lay   The layer in the graph on which we want this figure.
     *
     * @param edge  The edge to be rendered (an NSUML object)
     *
     * @return      The fig to be used, or <code>null</code> if we can't create
     *              one.
     */

    public FigEdge getFigEdgeFor(GraphModel gm, Layer lay, Object edge) {        

        cat.debug("making figedge for " + edge);

        // If the edge is an association, we'll need a FigAssociation

        if (org.argouml.model.ModelFacade.isAAssociation(edge)) {
            Object   asc         = /*(MAssociation)*/ edge;
            FigAssociation ascFig      = new FigAssociation(asc, lay);

            return ascFig;
        }

        // Generalization needs a FigGeneralization

        else if (org.argouml.model.ModelFacade.isAGeneralization(edge)) {
            Object   gen    = /*(MGeneralization)*/ edge;
            FigGeneralization genFig = new FigGeneralization(gen, lay);
            return genFig;
        }

        // Extend relationship

        else if (org.argouml.model.ModelFacade.isAExtend(edge)) {
            Object   ext    = /*(MExtend)*/ edge;
            FigExtend extFig = new FigExtend(ext);

            // The nodes at the two ends

            Object base      = ModelFacade.getBase(ext);
            Object extension = ModelFacade.getExtension(ext);

            // The figs for the two end nodes

            FigNode baseFN      = (FigNode) lay.presentationFor(base);
            FigNode extensionFN = (FigNode) lay.presentationFor(extension);

            // Link the new extend relationship in to the ends. Remember we
            // draw from the extension use case to the base use case.

            extFig.setSourcePortFig(extensionFN);
            extFig.setSourceFigNode(extensionFN);

            extFig.setDestPortFig(baseFN);
            extFig.setDestFigNode(baseFN);

            return extFig;
        }

        // Include relationship is very like extend. Watch out for the NSUML
        // bug here.

        else if (org.argouml.model.ModelFacade.isAInclude(edge)) {
            Object   inc    = /*(MInclude)*/ edge;
            FigInclude incFig = new FigInclude(inc);          

            Object base     = ModelFacade.getBase(inc);
            Object addition = ModelFacade.getAddition(inc);

            // The figs for the two end nodes

            FigNode baseFN     = (FigNode) lay.presentationFor(base);
            FigNode additionFN = (FigNode) lay.presentationFor(addition);

            // Link the new include relationship in to the ends

            incFig.setSourcePortFig(baseFN);
            incFig.setSourceFigNode(baseFN);

            incFig.setDestPortFig(additionFN);
            incFig.setDestFigNode(additionFN);

            return incFig;
        }

        // Dependency needs a FigDependency

        else if (org.argouml.model.ModelFacade.isADependency(edge)) {
            Object   dep    = /*(MDependency)*/ edge;
            FigDependency depFig = new FigDependency(dep);

            // Where there is more than one supplier or client, take the first
            // element in each case. There really ought to be a check that
            // there are some here for safety.

            Object supplier =
                /*(MModelElement)*/ ((ModelFacade.getSuppliers(dep).toArray())[0]);
            Object client =
                /*(MModelElement)*/ ((ModelFacade.getClients(dep).toArray())[0]);

            // The figs for the two end nodes

            FigNode supplierFN = (FigNode) lay.presentationFor(supplier);
            FigNode clientFN   = (FigNode) lay.presentationFor(client);

            // Link the new dependency in to the ends

            depFig.setSourcePortFig(clientFN);
            depFig.setSourceFigNode(clientFN);

            depFig.setDestPortFig(supplierFN);
            depFig.setDestFigNode(supplierFN);

            return depFig;
        } else 
        if (edge instanceof CommentEdge) {
            return new FigEdgeNote(edge, lay);
        }
            

        // If we get here, we can't handle this sort of edge.

        // What about realizations? They are not distinct objects in my UML
        // model maybe they should be, just as an implementation issue, dont
        // remove any of the methods that are there now.

        cat.debug(this.getClass().toString() +
		  ": getFigEdgeFor(" + gm.toString() + ", " +
		  lay.toString() + ", " + edge.toString() +
		  ") - needs more work to handle this sort of edge");
        return null;
    }

    static final long serialVersionUID = 2217410137377934879L;

} /* end class UseCaseDiagramRenderer */