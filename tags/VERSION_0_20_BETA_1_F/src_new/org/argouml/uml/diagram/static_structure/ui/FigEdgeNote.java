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

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.HashMap;

import javax.swing.Action;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.DelayedVChangeListener;
import org.argouml.model.Model;
import org.argouml.uml.UUIDHelper;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.diagram.ui.StereotypeUtility;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerPerspectiveMutable;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;


/**
 * Class to display a UML note connection to a
 * annotated model element.<p>
 * <p>
 * The owner of this fig is allways CommentEdge
 * </p>
 *
 * @author Andreas Rueckert <a_rueckert@gmx.net>
 * @author jaap.branderhorst@xs4all.nl
 */
public class FigEdgeNote
    extends FigEdgeModelElement
    implements VetoableChangeListener,
	       DelayedVChangeListener,
	       MouseListener,
	       KeyListener,
	       PropertyChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(FigEdgeNote.class);

    private Object owner;


    /**
     * Construct a new note connection. Use the same layout as for
     * other edges.
     */
    public FigEdgeNote() {
        super();
        LOG.info("Constructing a FigEdgeNote");
        setBetweenNearestPoints(true);
        getFig().setDashed(true);
        allowRemoveFromDiagram(false);
    }

    /**
     * Constructor that hooks the Fig to a CommentEdge
     * @param commentEdge the CommentEdge
     * @param theLayer the layer (ignored)
     */
    public FigEdgeNote(Object commentEdge, Layer theLayer) {
        this();

        if (!(theLayer instanceof LayerPerspectiveMutable)) {
            throw new IllegalArgumentException("The layer must be a mutable perspective. Got " + theLayer);
        }

        if (!(commentEdge instanceof CommentEdge)) {
            throw new IllegalArgumentException("The owner must be a CommentEdge. Got " + commentEdge);
        }

        Object fromNode = ((CommentEdge) commentEdge).getSource();
        if (!(Model.getFacade().isAModelElement(fromNode))) {
            throw new IllegalArgumentException("The given comment edge must start at a model element. Got " + fromNode);
        }

        Object toNode = ((CommentEdge) commentEdge).getDestination();
        if (!(Model.getFacade().isAModelElement(toNode))) {
            throw new IllegalArgumentException("The given comment edge must end at a model element. Got " + toNode);
        }

        Fig destFig = theLayer.presentationFor(toNode);
        if (destFig instanceof FigEdgeModelElement) {
            destFig = ((FigEdgeModelElement)destFig).getCommentPort();
        }
        if (!(destFig instanceof FigNodeModelElement)) {
            throw new IllegalArgumentException("The given comment edge must end at a model element in the given layer.");
        }

        Fig sourceFig = theLayer.presentationFor(fromNode);
        if (sourceFig instanceof FigEdgeModelElement) {
            sourceFig = ((FigEdgeModelElement)sourceFig).getCommentPort();
        }
        if (!(sourceFig instanceof FigNodeModelElement)) {
            throw new IllegalArgumentException("The given comment edge must start at a model element in the given layer.");
        }

        setLayer(theLayer);
        setDestFigNode((FigNode) destFig);
        setDestPortFig(destFig);
        setSourceFigNode((FigNode) sourceFig);
        setSourcePortFig(sourceFig);
        computeRoute();

        setOwner(commentEdge);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @see org.tigris.gef.presentation.FigEdge#setFig(org.tigris.gef.presentation.Fig)
     */
    public void setFig(Fig f) {
        LOG.info("Setting the internal fig to " + f);
        super.setFig(f);
        getFig().setDashed(true);
        //throw new IllegalArgumentException();
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#canEdit(org.tigris.gef.presentation.Fig)
     */
    protected boolean canEdit(Fig f) { return false; }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return Translator.localize("misc.comment-edge");
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent e) {
    }
    /**
     * @see org.tigris.gef.presentation.Fig#setOwner(java.lang.Object)
     */
    public void setOwner(Object newOwner) {
        if (newOwner == null) {
            // hack to avoid loading problems since we cannot store
            // the whole model yet in XMI
            newOwner = new CommentEdge();
        }
        owner = newOwner;
        if (UUIDHelper.getUUID(newOwner) == null) {
            Model.getCoreHelper().setUUID(newOwner, UUIDHelper.getNewUUID());
        }
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getOwner()
     */
    public Object getOwner() {
        return owner;
    }
    
    /**
     * Overrides the standard method to return null. A note edge
     * cannot have a stereotype.
     */
    final protected Action[] getApplyStereotypeActions() {
        return null;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#postLoad()
     */
    public void postLoad() {
        super.postLoad();
        CommentEdge o = (CommentEdge) getOwner();
        o.setDestination(getDestFigNode().getOwner());
        o.setSource(getSourceFigNode().getOwner());
    }
} /* end class FigEdgeNote */
