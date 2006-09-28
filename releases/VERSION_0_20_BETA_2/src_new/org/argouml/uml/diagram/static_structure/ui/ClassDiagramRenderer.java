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

package org.argouml.uml.diagram.static_structure.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.ui.GraphChangeAdapter;
import org.argouml.uml.diagram.UmlDiagramRenderer;
import org.argouml.uml.diagram.ui.FigAssociation;
import org.argouml.uml.diagram.ui.FigAssociationClass;
import org.argouml.uml.diagram.ui.FigAssociationEnd;
import org.argouml.uml.diagram.ui.FigDependency;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigGeneralization;
import org.argouml.uml.diagram.ui.FigNodeAssociation;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.diagram.ui.FigPermission;
import org.argouml.uml.diagram.ui.FigRealization;
import org.argouml.uml.diagram.ui.FigUsage;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 * This class defines a renderer object for UML Class Diagrams. In a
 * Class Diagram the following UML objects are displayed with the
 * following Figs: <p>
 *
 * <pre>
 *  UML Object       ---  Fig
 *  ---------------------------------------
 *  Class            ---  FigClass
 *  Interface        ---  FigInterface
 *  Instance         ---  FigInstance
 *  Model            ---  FigModel
 *  Subsystem        ---  FigSubsystem
 *  Package          ---  FigPackage
 *  Comment          ---  FigComment
 *  (CommentEdge)    ---  FigEdgeNote
 *  Generalization   ---  FigGeneralization
 *  Realization      ---  FigRealization
 *  Permission       ---  FigPermission
 *  Usage            ---  FigUsage
 *  Dependency       ---  FigDependency
 *  Association      ---  FigAssociation
 *  AssociationClass ---  FigAssociationClass
 *  Dependency       ---  FigDependency
 *  Link             ---  FigLink
 * </pre>
 *
 * @author jrobbins
 */
public class ClassDiagramRenderer extends UmlDiagramRenderer {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ClassDiagramRenderer.class);

    /**
     * @see org.tigris.gef.graph.GraphNodeRenderer#getFigNodeFor(
     *         org.tigris.gef.graph.GraphModel,
     *         org.tigris.gef.base.Layer, java.lang.Object, java.util.Map)
     *
     * Return a Fig that can be used to represent the given node.
     */
    public FigNode getFigNodeFor(GraphModel gm, Layer lay,
				 Object node, Map styleAttributes) {

        FigNodeModelElement figNode = null;

        if (node == null) {
            throw new IllegalArgumentException("A node must be supplied");
        }
        if (Model.getFacade().isAClass(node)) {
            figNode = new FigClass(gm, node);
        } else if (Model.getFacade().isAInterface(node)) {
            figNode = new FigInterface(gm, node);
        } else if (Model.getFacade().isAInstance(node)) {
            figNode = new FigInstance(gm, node);
        } else if (Model.getFacade().isAModel(node)) {
            figNode = new FigModel(gm, node);
        } else if (Model.getFacade().isASubsystem(node)) {
            figNode = new FigSubsystem(gm, node);
        } else if (Model.getFacade().isAPackage(node)) {
            figNode = new FigPackage(gm, node);
        } else if (Model.getFacade().isAComment(node)) {
            figNode = new FigComment(gm, node);
        } else if (Model.getFacade().isAAssociation(node)) {
            figNode = new FigNodeAssociation(gm, node);
        } else {
            LOG.error("TODO: ClassDiagramRenderer getFigNodeFor " + node);
            throw new IllegalArgumentException(
                    "Node is not a recognised type. Received "
                    + node.getClass().getName());
        }

        figNode.setDiElement(
                GraphChangeAdapter.getInstance().createElement(gm, node));


        return figNode;

    }

    /**
     * Return a Fig that can be used to represent the given edge.
     * Throws IllegalArgumentException if the edge is not of an expected type.
     * Throws IllegalStateException if the edge generated has no source
     *                               or dest port.
     *
     * @see org.tigris.gef.graph.GraphEdgeRenderer#getFigEdgeFor(
     *         org.tigris.gef.graph.GraphModel, org.tigris.gef.base.Layer,
     *         java.lang.Object, java.util.Map)
     */
    public FigEdge getFigEdgeFor(GraphModel gm, Layer lay,
				 Object edge, Map styleAttribute) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("making figedge for " + edge);
        }
        if (edge == null) {
            throw new IllegalArgumentException("A model edge must be supplied");
        }
        FigEdgeModelElement newEdge = null;
        if (Model.getFacade().isAAssociationClass(edge)) {
            FigAssociationClass ascCFig = new FigAssociationClass(edge, lay);
            // TODO: check why we are returning early for an association class.
            return ascCFig;
        } else if (Model.getFacade().isAAssociationEnd(edge)) {
            FigAssociationEnd asend = new FigAssociationEnd(edge, lay);
            Model.getFacade().getAssociation(edge);
            FigNode associationFN =
                (FigNode) lay.presentationFor(
			Model.getFacade().getAssociation(edge));
            FigNode classifierFN =
                (FigNode) lay.presentationFor(Model.getFacade().getType(edge));

            asend.setSourcePortFig(associationFN);
            asend.setSourceFigNode(associationFN);
            asend.setDestPortFig(classifierFN);
            asend.setDestFigNode(classifierFN);
            newEdge = asend;
        } else if (Model.getFacade().isAAssociation(edge)) {
            newEdge = new FigAssociation(edge, lay);
        } else if (Model.getFacade().isALink(edge)) {
            Object lnk = /*(MLink)*/ edge;
            FigLink lnkFig = new FigLink(lnk);
            Collection linkEndsColn = Model.getFacade().getConnections(lnk);

            Object[] linkEnds = linkEndsColn.toArray();
            Object fromInst = Model.getFacade().getInstance(linkEnds[0]);
            Object toInst = Model.getFacade().getInstance(linkEnds[1]);

            FigNode fromFN = (FigNode) lay.presentationFor(fromInst);
            FigNode toFN = (FigNode) lay.presentationFor(toInst);
            lnkFig.setSourcePortFig(fromFN);
            lnkFig.setSourceFigNode(fromFN);
            lnkFig.setDestPortFig(toFN);
            lnkFig.setDestFigNode(toFN);
            lnkFig.getFig().setLayer(lay);
            newEdge = lnkFig;
        } else if (Model.getFacade().isAGeneralization(edge)) {
            newEdge = new FigGeneralization(edge, lay);
        } else if (Model.getFacade().isAPermission(edge)) {
            newEdge = new FigPermission(edge, lay);
        } else if (Model.getFacade().isAUsage(edge)) {
            newEdge = new FigUsage(edge, lay);
        } else if (Model.getFacade().isAAbstraction(edge)) {
            newEdge = new FigRealization(edge);
        } else if (Model.getFacade().isADependency(edge)) {
            
            Collection c = Model.getFacade().getStereotypes(edge);
            Iterator i = c.iterator();
            String name = "";
            while (i.hasNext()) {
                Object o = i.next();
                name = Model.getFacade().getName(o);
                if ("realize".equals(name)) break;
            }
            if("realize".equals(name)) {
                FigRealization realFig = new FigRealization(edge);

                Object supplier =
                    ((Model.getFacade().getSuppliers(edge).toArray())[0]);
                Object client =
                    ((Model.getFacade().getClients(edge).toArray())[0]);

                FigNode supFN = (FigNode) lay.presentationFor(supplier);
                FigNode cliFN = (FigNode) lay.presentationFor(client);

                realFig.setSourcePortFig(cliFN);
                realFig.setSourceFigNode(cliFN);
                realFig.setDestPortFig(supFN);
                realFig.setDestFigNode(supFN);
                realFig.getFig().setLayer(lay);
                newEdge = realFig;
            } else {
                FigDependency depFig = new FigDependency(edge, lay);
                newEdge = depFig;
            }
        } else if (edge instanceof CommentEdge) {
            newEdge = new FigEdgeNote(edge, lay);
        }

        if (newEdge == null) {
            throw new IllegalArgumentException(
                    "Don't know how to create FigEdge for model type "
                    + edge.getClass().getName());
        }

        if (newEdge.getSourcePortFig() == null) {
            Object source;
            if (edge instanceof CommentEdge) {
                source = ((CommentEdge) edge).getSource();
            } else {
                source = Model.getUmlHelper().getSource(edge);
            }
            setSourcePort(newEdge, (FigNode) lay.presentationFor(source));
        }

        if (newEdge.getDestPortFig() == null) {
            Object dest;
            if (edge instanceof CommentEdge) {
                dest = ((CommentEdge) edge).getDestination();
            } else {
                dest = Model.getUmlHelper().getDestination(edge);
            }
            setDestPort(newEdge, (FigNode) lay.presentationFor(dest));
        }

        if (newEdge.getSourcePortFig() == null
                || newEdge.getDestPortFig() == null) {
            throw new IllegalStateException("Edge of type "
                    + newEdge.getClass().getName()
                    + " created with no source or destination port");
        }

        newEdge.setDiElement(
            GraphChangeAdapter.getInstance().createElement(gm, edge));

        return newEdge;
    }

    private void setSourcePort(FigEdge edge, FigNode source) {
        edge.setSourcePortFig(source);
        edge.setSourceFigNode(source);
    }

    private void setDestPort(FigEdge edge, FigNode dest) {
        edge.setDestPortFig(dest);
        edge.setDestFigNode(dest);
    }

    static final long serialVersionUID = 675407719309039112L;

} /* end class ClassDiagramRenderer */
