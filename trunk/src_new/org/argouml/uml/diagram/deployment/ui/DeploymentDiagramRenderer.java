// $Id$
// Copyright (c) 2003-2005 The Regents of the University of California. All
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

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.uml.diagram.UmlDiagramRenderer;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.FigComment;
import org.argouml.uml.diagram.static_structure.ui.FigEdgeNote;
import org.argouml.uml.diagram.static_structure.ui.FigInterface;
import org.argouml.uml.diagram.static_structure.ui.FigLink;
import org.argouml.uml.diagram.ui.FigAssociation;
import org.argouml.uml.diagram.ui.FigAssociationClass;
import org.argouml.uml.diagram.ui.FigAssociationEnd;
import org.argouml.uml.diagram.ui.FigDependency;
import org.argouml.uml.diagram.ui.FigGeneralization;
import org.argouml.uml.diagram.ui.FigNodeAssociation;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 * This class defines a renderer object for UML Deployment Diagrams.
 *
 */
public class DeploymentDiagramRenderer extends UmlDiagramRenderer {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(DeploymentDiagramRenderer.class);

    /**
     * Return a Fig that can be used to represent the given node.
     *
     * @see org.tigris.gef.graph.GraphNodeRenderer#getFigNodeFor(
     *         org.tigris.gef.graph.GraphModel, org.tigris.gef.base.Layer,
     *         java.lang.Object, java.util.Map)
     */
    public FigNode getFigNodeFor(
            GraphModel gm,
            Layer lay,
            Object node,
            Map styleAttributes) {
        if (Model.getFacade().isANode(node)) {
            return new FigMNode(gm, node);
        } else if (Model.getFacade().isAAssociation(node)) {
            return new FigNodeAssociation(gm, node);
        } else if (Model.getFacade().isANodeInstance(node)) {
            return new FigMNodeInstance(gm, node);
        } else if (Model.getFacade().isAComponent(node)) {
            return new FigComponent(gm, node);
        } else if (Model.getFacade().isAComponentInstance(node)) {
            return new FigComponentInstance(gm, node);
        } else if (Model.getFacade().isAClass(node)) {
            return new FigClass(gm, node);
        } else if (Model.getFacade().isAInterface(node)) {
            return new FigInterface(gm, node);
        } else if (Model.getFacade().isAObject(node)) {
            return new FigObject(gm, node);
        } else if (Model.getFacade().isAComment(node)) {
            return new FigComment(gm, node);
        }
        LOG.debug("TODO: DeploymentDiagramRenderer getFigNodeFor");
        return null;
    }

    /**
     * Return a Fig that can be used to represent the given edge.
     *
     * @see org.tigris.gef.graph.GraphEdgeRenderer#getFigEdgeFor(
     *         org.tigris.gef.graph.GraphModel, org.tigris.gef.base.Layer,
     *         java.lang.Object, java.util.Map)
     */
    public FigEdge getFigEdgeFor(
            GraphModel gm,
            Layer lay,
            Object edge,
            Map styleAttributes) {
        if (Model.getFacade().isAAssociationClass(edge)) {
            FigAssociationClass ascCFig = new FigAssociationClass(edge, lay);
            return ascCFig;
        } else if (Model.getFacade().isAAssociation(edge)) {
            Object asc = /*(MAssociation)*/ edge;
            FigAssociation ascFig = new FigAssociation(asc, lay);
            return ascFig;
        } else if (Model.getFacade().isAAssociationEnd(edge)) {
            FigAssociationEnd asend = new FigAssociationEnd(edge, lay);
            Model.getFacade().getAssociation(edge);
            FigNode associationFN =
                    (FigNode) lay.presentationFor(Model
                            .getFacade().getAssociation(edge));
            FigNode classifierFN =
                    (FigNode) lay.presentationFor(Model
                            .getFacade().getType(edge));

            asend.setSourcePortFig(associationFN);
            asend.setSourceFigNode(associationFN);
            asend.setDestPortFig(classifierFN);
            asend.setDestFigNode(classifierFN);
            return asend;
        }

        if (Model.getFacade().isALink(edge)) {
            Object lnk = /*(MLink)*/ edge;
            FigLink lnkFig = new FigLink(lnk);
            Collection linkEnds = Model.getFacade().getConnections(lnk);
            if (linkEnds == null) {
		LOG.debug("null linkRoles....");
	    }
            Object[] leArray = linkEnds.toArray();
            Object fromEnd = leArray[0];
            Object fromInst = Model.getFacade().getInstance(fromEnd);
            Object toEnd = leArray[1];
            Object toInst = Model.getFacade().getInstance(toEnd);
            FigNode fromFN = (FigNode) lay.presentationFor(fromInst);
            FigNode toFN = (FigNode) lay.presentationFor(toInst);
            lnkFig.setSourcePortFig(fromFN);
            lnkFig.setSourceFigNode(fromFN);
            lnkFig.setDestPortFig(toFN);
            lnkFig.setDestFigNode(toFN);
            return lnkFig;
        }
        if (Model.getFacade().isADependency(edge)) {
            Object dep = /*(MDependency)*/ edge;
            FigDependency depFig = new FigDependency(dep);

            Object supplier =
        	 ((Model.getFacade().getSuppliers(dep).toArray())[0]);
            Object client =
        	 ((Model.getFacade().getClients(dep).toArray())[0]);

            FigNode supFN = (FigNode) lay.presentationFor(supplier);
            FigNode cliFN = (FigNode) lay.presentationFor(client);

            depFig.setSourcePortFig(cliFN);
            depFig.setSourceFigNode(cliFN);
            depFig.setDestPortFig(supFN);
            depFig.setDestFigNode(supFN);
            depFig.getFig().setDashed(true);
            return depFig;
        }
        if (Model.getFacade().isAGeneralization(edge)) {
            Object gen = /*(MGeneralization)*/ edge;
            FigGeneralization genFig = new FigGeneralization(gen, lay);
            return genFig;
        }
        if (edge instanceof CommentEdge) {
            return new FigEdgeNote(edge, lay);
        }

        return null;
    }

    static final long serialVersionUID = 8002278834226522224L;

}
