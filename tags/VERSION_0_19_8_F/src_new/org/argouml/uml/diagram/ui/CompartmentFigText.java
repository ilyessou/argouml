// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

import java.awt.Color;

import org.apache.log4j.Logger;
import org.argouml.ui.targetmanager.TargetManager;
import org.tigris.gef.presentation.Fig;

/**
 * A FigText class extension for FigClass/FigInterface/FigUseCase
 * compartments.<p>
 *
 * This implementation now supports the extension point compartment in
 * a use case.<p>
 *
 * @author thn
 */
public class CompartmentFigText extends FigSingleLineText {
    private static final Logger LOG =
	Logger.getLogger(CompartmentFigText.class);

    ///////////////////////////////////////////////////////////////////////////
    //
    // Instance variables
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The bounding figure of the compartment containing this fig text.<p>
     */
    private Fig           refFig;


    /**
     * Record whether we are currently highlighted.<p>
     */
    private boolean       isHighlighted;


    /**
     * The model element with which we are associated.<p>
     */
    private Object modelElement;


    ///////////////////////////////////////////////////////////////////////////
    //
    // constructors
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Build a new compartment figText of the given dimensions, within
     * the compartment described by <code>aFig</code>.<p>
     *
     * Invoke the parent constructor, then set the reference to the
     * associated compartment figure. The associated FigText is marked
     * as expand only.<p>
     *
     * <em>Warning</em>. Won't work properly if <code>aFig</code> is
     * null. A warning is printed.<p>
     *
     * @param x     X coordinate of the top left of the FigText.
     *
     * @param y     Y coordinate of the top left of the FigText.
     *
     * @param w     Width of the FigText.
     *
     * @param h     Height of the FigText.
     *
     * @param aFig  The figure describing the whole compartment
     */
    public CompartmentFigText(int x, int y, int w, int h, Fig aFig) {
        super(x, y, w, h, true);

        // Set the enclosing compartment fig. Warn if its null (which will
        // break).

        refFig = aFig;

        if (refFig == null) {
            LOG.warn(this.getClass().toString()
		     + ": Cannot create with null compartment fig");
        }
    }

    /**
     * Override for correct graphical behaviour.<p>
     *
     * @return  Current fill status&mdash;always <code>true</code>.
     */
    public boolean getFilled() {
        return false;
    }

    /**
     * Override for correct graphical behaviour.<p>
     *
     * @return  Current fill colour&mdash;always the fill colour of the
     *          associated compartment fig.
     */
    public Color getLineColor() {
        return refFig.getLineColor();
    }

    /**
     * Mark whether this item is to be highlighted.<p>
     *
     * If it is highlighted, make the superclass line width 1 rather
     * than 0 and set the associated component fig as the target in
     * the browser.<p>
     *
     * @param flag  <code>true</code> if the entry is to be highlighted,
     *              <code>false</code> otherwise.
     */
    public void setHighlighted(boolean flag) {
        isHighlighted = flag;
        super.setLineWidth(isHighlighted ? 1 : 0);

        if (flag && (modelElement != null)) {
            TargetManager.getInstance().setTarget(modelElement);
        }
    }


    /**
     * Return whether this item is highlighted.<p>
     *
     * @return  <code>true</code> if the entry is highlighted,
     *          <code>false</code> otherwise.
     */
    public boolean isHighlighted() {
        return isHighlighted;
    }


} /* End of class CompartmentFigText */
