// Copyright (c) 1996-99 The Regents of the University of California. All// Rights Reserved. Permission to use, copy, modify, and distribute this// software and its documentation without fee, and without a written// agreement is hereby granted, provided that the above copyright notice// and this paragraph appear in all copies.  This software program and// documentation are copyrighted by The Regents of the University of// California. The software program and documentation are supplied "AS// IS", without any accompanying services from The Regents. The Regents// does not warrant that the operation of the program will be// uninterrupted or error-free. The end-user understands that the program// was developed for research purposes and is advised not to rely// exclusively on the program for any reason.  IN NO EVENT SHALL THE// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.// File: SequenceDiagramRenderer.java// Classes: SequenceDiagramRenderer// Original Author: 5eichler@inormatik.uni-hamburg.de// $Id$package org.argouml.uml.diagram.sequence.specification_level.ui;import java.util.*;import ru.novosoft.uml.foundation.core.*;import ru.novosoft.uml.behavior.collaborations.*;import ru.novosoft.uml.behavior.common_behavior.*;import org.tigris.gef.base.*;import org.tigris.gef.presentation.*;import org.tigris.gef.graph.*;import org.apache.log4j.Category;import org.argouml.uml.diagram.ui.*;import org.argouml.uml.diagram.sequence.ui.FigSeqStimulus;public class SequenceDiagramRenderer implements GraphNodeRenderer, GraphEdgeRenderer {    protected static Category cat =         Category.getInstance(SequenceDiagramRenderer.class);    /** Return a Fig that can be used to represent the given node */    public FigNode getFigNodeFor(GraphModel gm, Layer lay, Object node) {        if (node instanceof MObject) return new FigSeqClRole(node);        if (node instanceof MStimulus) return new FigSeqStimulus(null, node);  // Needed to convert old SQ diagrams        if (node instanceof MClassifierRole) return new FigSeqClRole(node);//        if (node instanceof MMessage) return new FigSeqMessage(node);        cat.debug("TODO SequenceDiagramRenderer getFigNodeFor");        return null;    }    /** Return a Fig that can be used to represent the given edge */    /** Generally the same code as for the ClassDiagram, since its      very related to it. */    public FigEdge getFigEdgeFor(GraphModel gm, Layer lay, Object edge) {        if (edge instanceof MLink) {            MLink ml = (MLink)edge;            FigSeqAsRole arFig = new FigSeqAsRole(ml);            Collection connections = ml.getConnections();            if (connections == null) {                cat.debug("null connections....");                return null;            }            MLinkEnd  fromEnd  = (MLinkEnd) ((Object[])connections.toArray())[0];            MInstance fromInst = (MInstance) fromEnd.getInstance();            MLinkEnd  toEnd  = (MLinkEnd) ((Object[])connections.toArray())[1];            MInstance toInst = (MInstance) toEnd.getInstance();            FigNode fromFN = (FigNode) lay.presentationFor(fromInst);            FigNode toFN = (FigNode) lay.presentationFor(toInst);            arFig.setSourcePortFig(fromFN);            arFig.setSourceFigNode(fromFN);            arFig.setDestPortFig(toFN);            arFig.setDestFigNode(toFN);            return arFig;        }//        if (edge instanceof MAssociationRole) {        if (edge instanceof MAssociationRole) {            MAssociationRole ar = (MAssociationRole)edge;            FigSeqAsRole arFig = new FigSeqAsRole(ar);            Collection connections = ar.getConnections();            if (connections == null) {                cat.debug("null connections....");                return null;            }            MAssociationEndRole fromEnd  = (MAssociationEndRole)(connections.toArray()[0]);            MClassifierRole     fromClfr = (MClassifierRole)fromEnd.getType();            MAssociationEndRole toEnd    = (MAssociationEndRole)(connections.toArray()[1]);            MClassifierRole     toClfr   = (MClassifierRole)fromEnd.getType();            FigNode fromFN = (FigNode) lay.presentationFor(fromClfr);            FigNode toFN   = (FigNode) lay.presentationFor(toClfr);            arFig.setSourcePortFig(fromFN);            arFig.setSourceFigNode(fromFN);            arFig.setDestPortFig(toFN);            arFig.setDestFigNode(toFN);            return arFig;        }        cat.debug("TODO SequenceDiagramRenderer getFigEdgeFor");        return null;    }} /* end class SequenceDiagramRenderer */