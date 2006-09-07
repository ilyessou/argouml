// $Id$
// Copyright (c) 1996-2006 The Regents of the University of California. All
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
import java.awt.Rectangle;
import java.util.Iterator;
import org.argouml.uml.diagram.ui.CompartmentFigText;
import org.argouml.uml.diagram.ui.FigEmptyRect;
import org.argouml.uml.diagram.ui.FigOperationsCompartment;
import org.argouml.uml.diagram.ui.OperationsCompartmentContainer;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;

/**
 * Class to display graphics for a UML Class in a diagram.<p>
 *
 * Note that the upper line of the name box will be blanked out
 * if there is eventually a stereotype above.
 */
public abstract class FigClassifierBox extends FigCompartmentBox
        implements OperationsCompartmentContainer {

    /**
     * The Fig for the operations compartment (if any).
     */
    protected FigOperationsCompartment operationsFig;

    /**
     * Text highlighted by mouse actions on the diagram.<p>
     */
    protected CompartmentFigText highlightedFigText;

    protected Fig borderFig;
    
    /**
     * Constructor.
     */
    FigClassifierBox() {

        // this rectangle marks the operation section; all operations
        // are inside it
        operationsFig =
            new FigOperationsCompartment(
                    10, 31 + ROWHEIGHT, 60, ROWHEIGHT + 2);

        // Set properties of the stereotype box. Make it 1 pixel higher than
        // before, so it overlaps the name box, and the blanking takes out both
        // lines. Initially not set to be displayed, but this will be changed
        // when we try to render it, if we find we have a stereotype.
        getStereotypeFig().setFilled(true);
        getStereotypeFig().setLineWidth(1);
        // +1 to have 1 pixel overlap with getNameFig()
        getStereotypeFig().setHeight(STEREOHEIGHT + 1);

        borderFig = new FigEmptyRect(10, 10, 0, 0);
        borderFig.setLineWidth(1);
        borderFig.setLineColor(Color.black);

        getBigPort().setLineWidth(0);
        getBigPort().setFillColor(Color.white);

    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigClassifierBox figClone = (FigClassifierBox) super.clone();
        Iterator thisIter = this.getFigs().iterator();
        while (thisIter.hasNext()) {
            Fig thisFig = (Fig) thisIter.next();
            if (thisFig == operationsFig) {
                figClone.operationsFig = (FigOperationsCompartment) thisFig;
                return figClone;
            }
        }
        return figClone;
    }

    /**
     * Updates the operations box. Called from modelchanged if there is
     * a modelevent effecting the attributes and from renderingChanged in all
     * cases.
     */
    protected void updateOperations() {
        if (!isOperationsVisible()) {
            return;
        }
        operationsFig.populate();

        Rectangle rect = getBounds();
        // ouch ugly but that's for a next refactoring
        // TODO: make setBounds, calcBounds and updateBounds consistent
        setBounds(rect.x, rect.y, rect.width, rect.height);
        damage();
    }

    /**
     * @return The vector of graphics for operations (if any).
     * First one is the rectangle for the entire operations box.
     */
    protected FigOperationsCompartment getOperationsFig() {
        return operationsFig;
    }

    /**
     * Get the bounds of the operations compartment.
     *
     * @return the bounds of the operations compartment
     */
    public Rectangle getOperationsBounds() {
        return operationsFig.getBounds();
    }

    /**
     * Returns the visibility status of the operations compartment.
     *
     * @return true if the operations are visible, false otherwise
     *
     * @see org.argouml.uml.diagram.ui.OperationsCompartmentContainer#isOperationsVisible()
     */
    public boolean isOperationsVisible() {
        return operationsFig.isVisible();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#translate(int, int)
     */
    public void translate(int dx, int dy) {
        super.translate(dx, dy);
        Editor ce = Globals.curEditor();
        Selection sel = ce.getSelectionManager().findSelectionFor(this);
        if (sel instanceof SelectionClass) {
            ((SelectionClass) sel).hideButtons();
        }
    }
}
