/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.argouml.kernel.DelayedChangeNotify;
import org.argouml.kernel.DelayedVChangeListener;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.Model;
import org.argouml.model.RemoveAssociationEvent;
import org.argouml.model.UmlChangeEvent;
import org.argouml.ui.ArgoJMenu;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.FigMultiLineText;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Geometry;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/**
 * Class to display a UML comment in a diagram.
 *
 * @author Andreas Rueckert
 */
public class FigComment
    extends FigNodeModelElement
    implements VetoableChangeListener,
	       DelayedVChangeListener,
	       MouseListener,
	       KeyListener,
	       PropertyChangeListener {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(FigComment.class.getName());

    ////////////////////////////////////////////////////////////////
    // constants

    private int width = 80;
    private int height = 60;

    /**
     * A dog-ear is a bent corner in a book.
     */
    private int dogear = 10;

    private boolean readyToEdit = true;

    // The figure that holds the text of the note.
    private FigText bodyTextFig;

    private FigPoly outlineFig;

    /**
     * The upper right corner.
     */
    private FigPoly urCorner;

    /**
     * Flag to indicate that we have just been created. This is to fix the
     * problem with loading comments that have stereotypes already
     * defined.<p>
     */
    private boolean newlyCreated;

    @Override
    protected Fig createBigPortFig() {
        FigRect fr = new FigRect(0, 0, width, height, null, null);
        fr.setFilled(false);
        fr.setLineWidth(0);
        return fr;
    }

    private void initialize() {
        Color fg = super.getLineColor(); // Use super because not fully init'd
        Color fill = super.getFillColor();

        outlineFig = new FigPoly(fg, fill);
        outlineFig.addPoint(0, 0);
        outlineFig.addPoint(width - 1 - dogear, 0);
        outlineFig.addPoint(width - 1, dogear);
        outlineFig.addPoint(width - 1, height - 1);
        outlineFig.addPoint(0, height - 1);
        outlineFig.addPoint(0, 0);
        outlineFig.setFilled(true);
        outlineFig.setLineWidth(LINE_WIDTH);

        urCorner = new FigPoly(fg, fill);
        urCorner.addPoint(width - 1 - dogear, 0);
        urCorner.addPoint(width - 1, dogear);
        urCorner.addPoint(width - 1 - dogear, dogear);
        urCorner.addPoint(width - 1 - dogear, 0);
        urCorner.setFilled(true);
        Color col = outlineFig.getFillColor();
        urCorner.setFillColor(col.darker());
        urCorner.setLineWidth(LINE_WIDTH);

        // add Figs to the FigNode in back-to-front order
        addFig(getBigPort());
        addFig(outlineFig);
        addFig(urCorner);
        addFig(getStereotypeFig());
        addFig(bodyTextFig);

        col = outlineFig.getFillColor();
        urCorner.setFillColor(col.darker());

        setBlinkPorts(false); //make port invisible unless mouse enters
        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
        updateEdges();

        readyToEdit = false;
        // Mark this as newly created. This is to get round the problem with
        // creating figs for loaded comments that had stereotypes. They are
        // saved with their dimensions INCLUDING the stereotype, but since we
        // pretend the stereotype is not visible, we add height the first time
        // we render such a comment. This is a complete fudge, and really we
        // ought to address how comment objects with stereotypes are saved. But
        // that will be hard work.
        newlyCreated = true;
    }

    /**
     * Construct a comment figure with the given model element, bounds, and
     * settings. This constructor is used by the PGML parser.
     *
     * @param owner owning Comments
     * @param bounds position and size
     * @param settings rendering settings
     */
    public FigComment(Object owner, Rectangle bounds,
            DiagramSettings settings) {
        super(owner, bounds, settings);

        bodyTextFig = new FigMultiLineText(getOwner(),
                new Rectangle(2, 2, width - 2 - dogear, height - 4),
                getSettings(), true);

        initialize();
        updateBody();
        if (bounds != null) {
            setLocation(bounds.x, bounds.y);
        }
    }

    /**
     * Get the default text for this figure.
     *
     * @return The default text for this figure.
     */
    @Override
    public String placeString() {
        String placeString = retrieveBody();
        if (placeString == null) {
            // TODO: I18N
            placeString = "new note";
        }
        return placeString;
    }

    /**
     * Clone this figure.
     *
     * @return The cloned figure.
     */
    @Override
    public Object clone() {
        FigComment figClone = (FigComment) super.clone();
        Iterator thisIter = this.getFigs().iterator();
        while (thisIter.hasNext()) {
            Object thisFig = thisIter.next();
            if (thisFig == outlineFig) {
                figClone.outlineFig = (FigPoly) thisFig;
            }
            if (thisFig == urCorner) {
                figClone.urCorner = (FigPoly) thisFig;
            }
            if (thisFig == bodyTextFig) {
                figClone.bodyTextFig = (FigText) thisFig;
            }
        }
        return figClone;
    }

    private void updateBody() {
        if (getOwner() != null) {
            String body = (String) Model.getFacade().getBody(getOwner());
            if (body != null) {
                bodyTextFig.setText(body);
            }
        }
    }


    /**
     * See FigNodeModelElement.java for more info on these methods.
     */

    /**
     * If the user double clicks on any part of this FigNode, pass it
     * down to one of the internal Figs.  This allows the user to
     * initiate direct text editing.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (!readyToEdit) {
            Object owner = getOwner();
            if (Model.getFacade().isAModelElement(owner)
                    && !Model.getModelManagementHelper().isReadOnly(owner)) {
                readyToEdit = true;
            } else {
                LOG.log(Level.FINE, "not ready to edit note");
                return;
            }
        }
        if (me.isConsumed()) {
            return;
        }
        if (me.getClickCount() >= 2
	    && !(me.isPopupTrigger()
		 || me.getModifiers() == InputEvent.BUTTON3_MASK)) {
            if (getOwner() == null) {
                return;
            }
            Fig f = hitFig(new Rectangle(me.getX() - 2, me.getY() - 2, 4, 4));
            if (f instanceof MouseListener) {
                ((MouseListener) f).mouseClicked(me);
            }
        }
        me.consume();
    }

    /*
     * @see java.beans.VetoableChangeListener#vetoableChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void vetoableChange(PropertyChangeEvent pce) {
        Object src = pce.getSource();
        if (src == getOwner()) {
            DelayedChangeNotify delayedNotify =
		new DelayedChangeNotify(this, pce);
            SwingUtilities.invokeLater(delayedNotify);
        } else {
            LOG.log(Level.FINE,
                    "FigNodeModelElement got vetoableChange from non-owner: {0}",
                    src);
        }
    }

    /*
     * @see org.argouml.kernel.DelayedVChangeListener#delayedVetoableChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void delayedVetoableChange(PropertyChangeEvent pce) {
        // update any text, colors, fonts, etc.
        renderingChanged();
        // update the relative sizes and positions of internel Figs
        endTrans();
    }

    /*
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent pve) {
        Object src = pve.getSource();
        String pName = pve.getPropertyName();
        if (pName.equals("editing")
	    && Boolean.FALSE.equals(pve.getNewValue())) {
            //parse the text that was edited
            textEdited((FigText) src);
            // resize the FigNode to accomodate the new text
            Rectangle bbox = getBounds();
            Dimension minSize = getMinimumSize();
            bbox.width = Math.max(bbox.width, minSize.width);
            bbox.height = Math.max(bbox.height, minSize.height);
            setBounds(bbox.x, bbox.y, bbox.width, bbox.height);
            endTrans();
        } else {
            super.propertyChange(pve);
        }
    }

    /*
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        // Not used, do nothing.
    }

    /*
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        // Not used, do nothing.
    }

    /*
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent ke) {
        if (Character.isISOControl(ke.getKeyChar())) {
            return;
        }
        if (!readyToEdit) {
            Object owner = getOwner();
            if (Model.getFacade().isAModelElement(owner)
                    && !Model.getModelManagementHelper().isReadOnly(owner)) {
                storeBody("");
                readyToEdit = true;
            } else {
                LOG.log(Level.FINE, "not ready to edit note");
                return;
            }
        }
        if (ke.isConsumed()) {
            return;
        }
        if (getOwner() == null) {
            return;
        }
        bodyTextFig.keyTyped(ke);
    }

    ////////////////////////////////////////////////////////////////
    // Fig accessors

    /**
     * @return an empty menu
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#buildShowPopUp()
     */
    @Override
    protected ArgoJMenu buildShowPopUp() {
        return new ArgoJMenu("menu.popup.show");
    }

    /*
     * @see org.tigris.gef.presentation.Fig#makeSelection()
     */
    @Override
    public Selection makeSelection() {
        return new SelectionComment(this);
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    @Override
    public void setLineColor(Color col) {
        // The text element has no border, so the line color doesn't matter.
        outlineFig.setLineColor(col);
        urCorner.setLineColor(col);
    }

    /*
     * @see org.tigris.gef.presentation.Fig#getLineColor()
     */
    @Override
    public Color getLineColor() {
        return outlineFig.getLineColor();
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setFillColor(java.awt.Color)
     */
    @Override
    public void setFillColor(Color col) {
        outlineFig.setFillColor(col);
        urCorner.setFillColor(col);
    }

    /*
     * @see org.tigris.gef.presentation.Fig#getFillColor()
     */
    @Override
    public Color getFillColor() {
        return outlineFig.getFillColor();
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    @Override
    public void setFilled(boolean f) {
        bodyTextFig.setFilled(false); // The text is always opaque.
        outlineFig.setFilled(f);
        urCorner.setFilled(f);
    }

    @Override
    public boolean isFilled() {
        return outlineFig.isFilled();
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    @Override
    public void setLineWidth(int w) {
        bodyTextFig.setLineWidth(0); // Make a seamless integration of the text
        // in the note figure.
        outlineFig.setLineWidth(w);
        urCorner.setLineWidth(w);
    }

    /*
     * @see org.tigris.gef.presentation.Fig#getLineWidth()
     */
    @Override
    public int getLineWidth() {
        return outlineFig.getLineWidth();
    }

    ////////////////////////////////////////////////////////////////
    // user interaction methods

    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    @Override
    protected void textEdited(FigText ft) {
        if (ft == bodyTextFig) {
            storeBody(ft.getText());
        }
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEditStarted(org.tigris.gef.presentation.FigText)
     */
    @Override
    protected void textEditStarted(FigText ft) {
        showHelp("parsing.help.comment");
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setEnclosingFig(org.tigris.gef.presentation.Fig)
     */
    @Override
    public void setEnclosingFig(Fig encloser) {
        super.setEnclosingFig(encloser);
    }

    ////////////////////////////////////////////////////////////////
    // accessor methods

    /**
     * Stores the body text in the associated model element.
     *
     * @param body The body text to store.
     */
    public final void storeBody(String body) {
        if (getOwner() != null) {
            Model.getCoreHelper().setBody(getOwner(), body);
        }
    }

    /**
     * Retrieve the body text from the associated model element.
     *
     * @return The body from the associated model element.
     */
    private String retrieveBody() {
        return (getOwner() != null)
            ? (String) Model.getFacade().getBody(getOwner())
            : null;
    }

    /*
     * @see org.tigris.gef.presentation.Fig#getUseTrapRect()
     */
    @Override
    public boolean getUseTrapRect() {
        return true;
    }

    /**
     * Always returns null as the FigComment does not display its name.
     * @return null
     */
    @Override
    public Rectangle getNameBounds() {
        return null;
    }

    /**
     * Get the minimum size for the note figure.
     *
     * @return The minimum size for the note figure.
     */
    @Override
    public Dimension getMinimumSize() {

        // Get the size of the text field.
        Dimension aSize = bodyTextFig.getMinimumSize();

        // If we have a stereotype displayed, then allow some space for that
        // (width and height)

        if (getStereotypeFig().isVisible()) {
            Dimension stereoMin = getStereotypeFig().getMinimumSize();
            aSize.width =
                Math.max(aSize.width,
                         stereoMin.width);
            aSize.height += stereoMin.height;
        }

        // And add the gaps around the textfield to get the minimum
        // size of the note.
        return new Dimension(aSize.width + 4 + dogear,
			     aSize.height + 4);
    }

    /*
     * @see org.tigris.gef.presentation.Fig#setBounds(int, int, int, int)
     */
    @Override
    protected void setStandardBounds(int px, int py, int w, int h) {
        if (bodyTextFig == null) {
            return;
        }

        Dimension stereoMin = getStereotypeFig().getMinimumSize();

        int stereotypeHeight = 0;
        if (getStereotypeFig().isVisible()) {
            stereotypeHeight = stereoMin.height;
        }

        Rectangle oldBounds = getBounds();

        // Resize the text figure
        bodyTextFig.setBounds(px + 2, py + 2 + stereotypeHeight,
                w - 4 - dogear, h - 4 - stereotypeHeight);

        getStereotypeFig().setBounds(px + 2, py + 2,
                w - 4 - dogear, stereoMin.height);

        // Resize the big port around the figure
        getBigPort().setBounds(px, py, w, h);

        // Since this is a complex polygon, there's no easy way to resize it.
        Polygon newPoly = new Polygon();
        newPoly.addPoint(px, py);
        newPoly.addPoint(px + w - 1 - dogear, py);
        newPoly.addPoint(px + w - 1, py + dogear);
        newPoly.addPoint(px + w - 1, py + h - 1);
        newPoly.addPoint(px, py + h - 1);
        newPoly.addPoint(px, py);
        outlineFig.setPolygon(newPoly);

        // Just move the corner to it's new position.
        urCorner.setBounds(px + w - 1 - dogear, py, dogear, dogear);

        calcBounds(); //_x = x; _y = y; _w = w; _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /*
     * TODO: This is the same as in parent - remove?
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateBounds()
     */
    @Override
    protected void updateBounds() {
        Rectangle bbox = getBounds();
        Dimension minSize = getMinimumSize();
        bbox.width = Math.max(bbox.width, minSize.width);
        bbox.height = Math.max(bbox.height, minSize.height);
        setBounds(bbox.x, bbox.y, bbox.width, bbox.height);
    }

    ///////////////////////////////////////////////////////////////////
    // Internal methods

    @Override
    protected final void updateLayout(UmlChangeEvent mee) {
        super.updateLayout(mee);

        if (mee instanceof AttributeChangeEvent
                && mee.getPropertyName().equals("body")) {

            bodyTextFig.setText(mee.getNewValue().toString());
            calcBounds();
            setBounds(getBounds());
            damage();
        } else if (mee instanceof RemoveAssociationEvent
                && mee.getPropertyName().equals("annotatedElement")) {
            /* Remove the commentedge.
             * If there are more then one comment-edges between
             * the 2 objects, then delete them all. */
            Collection<FigEdgeNote> toRemove = new ArrayList<FigEdgeNote>();
            Collection c = getFigEdges(); // all connected edges
            for (Iterator i = c.iterator(); i.hasNext(); ) {
                FigEdgeNote fen = (FigEdgeNote) i.next();
                Object otherEnd = fen.getDestination(); // the UML object
                if (otherEnd == getOwner()) { // wrong end of the edge
                    otherEnd = fen.getSource();
                }
                if (otherEnd == mee.getOldValue())  {
                    toRemove.add(fen);
                }
            }

            for (FigEdgeNote fen : toRemove) {
                fen.removeFromDiagram();
            }
        }
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateStereotypeText()
     */
    @Override
    protected void updateStereotypeText() {
        Object me = getOwner();

        if (me == null) {
            return;
        }

        Rectangle rect = getBounds();

        Dimension stereoMin = getStereotypeFig().getMinimumSize();

        if (Model.getFacade().getStereotypes(me).isEmpty()) {

            if (getStereotypeFig().isVisible()) {
                getStereotypeFig().setVisible(false);
                rect.y += stereoMin.height;
                rect.height -= stereoMin.height;
                setBounds(rect.x, rect.y, rect.width, rect.height);
                calcBounds();
            }
        } else {
            if (!getStereotypeFig().isVisible()) {
                getStereotypeFig().setVisible(true);

                // Only adjust the stereotype height if we are not newly
                // created. This gets round the problem of loading classes with
                // stereotypes defined, which have the height already including
                // the stereotype.

                if (!newlyCreated) {
                    rect.y -= stereoMin.height;
                    rect.height += stereoMin.height;
                    rect.width =
                        Math.max(getMinimumSize().width, rect.width);
                    setBounds(rect.x, rect.y, rect.width, rect.height);
                    calcBounds();
                }
            }
        }
        // Whatever happened we are no longer newly created, so clear the
        // flag. Then set the bounds for the rectangle we have defined.
        newlyCreated = false;
    }

    /**
     * Get the text body of the comment.
     * @return the body of the comment
     */
    public String getBody() {
        return bodyTextFig.getText();
    }

    /*
     * @see org.tigris.gef.presentation.Fig#getClosestPoint(java.awt.Point)
     */
    @Override
    public Point getClosestPoint(Point anotherPt) {
        Rectangle r = getBounds();
        int[] xs = {
            r.x, r.x + r.width - dogear, r.x + r.width,
            r.x + r.width,  r.x,            r.x,
        };
        int[] ys = {
            r.y, r.y,                    r.y + dogear,
            r.y + r.height, r.y + r.height, r.y,
        };
        Point p =
            Geometry.ptClosestTo(
                xs,
                ys,
                6,
                anotherPt);
        return p;
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = 7242542877839921267L;
}
