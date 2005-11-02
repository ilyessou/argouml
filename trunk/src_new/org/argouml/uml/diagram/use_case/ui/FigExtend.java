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

package org.argouml.uml.diagram.use_case.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import org.argouml.model.Model;
import org.argouml.notation.Notation;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.tigris.gef.base.PathConvPercent;
import org.tigris.gef.presentation.ArrowHeadGreater;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigText;

/**
 * A fig for use with extend relationships on use case diagrams.<p>
 *
 * Realised as a dotted line with an open arrow head and the label
 * &laquo;extend&raquo; together with any condition alongside.<p>
 *
 * @author mail@jeremybennett.com
 */
public class FigExtend extends FigEdgeModelElement {

    /**
     * The &laquo;extend&raquo; label.<p>
     */
    private FigText label;

    /**
     * The condition expression.<p>
     */
    private FigText condition;

    /**
     * The group of label and condition.<p>
     */
    private FigGroup fg;


    private ArrowHeadGreater endArrow = new ArrowHeadGreater();

    ///////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The default constructor, but should never be called directly
     * (use {@link #FigExtend(Object)}, since that sets the
     * owner. However we can't mark it as private, since GEF expects
     * to be able to call this when creating the diagram.<p>
     */
    public FigExtend() {

        // We need a FigText to hold the <<extend>> label. Details are the
        // same as a stereotype, and we use the stereotype notation generator
        // to give us the text. When its all done, use calcBounds() to shrink
        // to size.

        label = new FigText(10, 30, 90, 20);

        label.setFont(getLabelFont());
        label.setTextColor(Color.black);
        label.setTextFilled(false);
        label.setFilled(false);
        label.setLineWidth(0);
        label.setExpandOnly(false);
        label.setReturnAction(FigText.END_EDITING);
        label.setTabAction(FigText.END_EDITING);
        label.setText("<<extend>>");


        label.calcBounds();

        // We need a FigText to hold the condition. At this stage we have
        // nothing to put in it (since we have no owner). Place it immediately
        // below the label, and with the same height and width.

        condition = new FigText(10, 30 + label.getBounds().height,
                                label.getBounds().width,
                                label.getBounds().height);

        condition.setFont(getLabelFont());
        condition.setTextColor(Color.black);
        condition.setTextFilled(false);
        condition.setFilled(false);
        condition.setLineWidth(0);
        condition.setExpandOnly(false);
        condition.setReturnAction(FigText.END_EDITING);
        condition.setTabAction(FigText.END_EDITING);

        // Join the two into a group

        fg = new FigGroup();

        fg.addFig(label);
        fg.addFig(condition);

        // Place in the middle of the line and ensure the line is dashed.  Add
        // an arrow with an open arrow head. Remember that for an extends
        // relationship, the arrow points to the base use case, but because of
        // the way we draw it, that is still the destination end.

        addPathItem(fg, new PathConvPercent(this, 50, 10));

        setDashed(true);

        setDestArrowHead(endArrow);

        // Make the edge go between nearest points

        setBetweenNearestPoints(true);
    }


    /**
     * The main constructor. Builds the FigEdge required and makes the
     * given edge object its owner.<p>
     *
     * @param edge  The edge that will own the fig
     */
    public FigExtend(Object edge) {
        this();
        setOwner(edge);
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Accessors
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Set a new fig to represent this edge.<p>
     *
     * We invoke the superclass accessor. Then change aspects of the
     * new fig that are not as we want. In this case to use dashed
     * lines.<p>
     *
     * @param f  The fig to use.
     */
    public void setFig(Fig f) {
        super.setFig(f);

        // Make sure the line is dashed

        setDashed(true);
    }

    /**
     * Define whether the given fig can be edited (it can't).<p>
     *
     * @param f  The fig about which the enquiry is being made. Ignored in this
     *           implementation.
     *
     * @return   <code>false</code> under all circumstances.
     */
    protected boolean canEdit(Fig f) {
        return false;
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Event handlers
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is called after any part of the UML MModelElement has
     * changed. This method automatically updates things specific to
     * this fig. Subclasses should override and update other parts.<p>
     *
     * We reset the condition text. We really ought to check that
     * there has actually been a change, but for now we do it every
     * time.<p>
     *
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent e) {

        // Give up if we have no owner
        Object extend = /*(MExtend)*/ getOwner();

        if (extend == null) {
            return;
        }

        // Let the superclass sort itself out, and then tell GEF we are going
        // to start something
        super.modelChanged(e);

        // Now sort out the condition text. Use the null string if there is no
        // condition set. We call the main generate method, which will realise
        // this is a MExpression (subclass) and invoke the correct method.
        Object/*MBooleanExpression*/ c =
            Model.getFacade().getCondition(extend);

        if (c == null) {
            condition.setText("");
        }
        else {
            condition.setText(Notation.generate(this, c));
        }

        // Let the group recalculate its bounds and then tell GEF we've
        // finished.
        fg.calcBounds();
        endTrans();
    }


    /**
     * @see org.tigris.gef.presentation.Fig#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        endArrow.setLineColor(getLineColor());
        super.paint(g);
    }


} /* end class FigExtend */
