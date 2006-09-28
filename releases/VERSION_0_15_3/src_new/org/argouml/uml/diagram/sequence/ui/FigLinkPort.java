// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// Original author: jaap.branderhorst@xs4all.nl

package org.argouml.uml.diagram.sequence.ui;

import java.awt.Point;
import java.util.Vector;

import org.tigris.gef.presentation.FigLine;


/**
 * The port fig for links.
 * @author : jaap.branderhorst@xs4all.nl
 */
public class FigLinkPort extends FigLine {
	
	/**
	 * If a figlinkport has this activation status, the activation to which it is attached
	 * will be cut off at the y coordinate of this figlink.</br>
	 * The following type of links have this status for their source activation:</br>
	 * <ul>
	 * <li>FigReturnActionLink</li>
	 * </ul>
	 * The following types of links have this status for their destination status:</br>
	 * <ul>
	 * <li>FigCallActionLink</li>
	 * <li>FigDestroyActionLink</li>
	 * </ul>
	 */
	public final static int ACTIVATION_CUTOFF_STATUS = 1;
	
	/**
	 * If a figlinkport has this activation status, the activation to which 
	 * it is attached will not be cutt off.
	 * The following type of links have this status for their source activation:</br>
	 * <ul>
	 * <li>FigCallActionLink</li>
	 * <li>FigDestroyActionLink</li>
	 * <li>FigCreateActionLink</li>
	 * </ul>
	 * The following types of links have this status for their destination status:</br>
	 * <ul>
	 * <li>FigReturnActionLink</li> 
	 * </ul>
	 */
	public final static int ACTIVATION_CONTINUOUS_STATUS = 2;
	
	private int _activationStatus;

    /**
     * Creates a new horizontal FigLinkPort that's not displayed
     * @param x
     * @param y
     * @param x2
     */
    public FigLinkPort(int x, int y, int x2) {
        super(x, y, x2, y);
        setDisplayed(false);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getGravityPoints()
     */
    public Vector getGravityPoints() {
        Vector ret = new Vector();
        Point p1 = new Point(getX(), getY());
        Point p2 = new Point(getX() + getWidth(), getY() + getHeight());
        ret.add(p1);
        ret.add(p2);
        return ret;
    }

    /**
     * @return
     */
    public int getActivationStatus() {
        return _activationStatus;
    }

    /**
     * Sets the activation status. The activation status is a flag to indicate
     * if the activation to which this figlinkport is attached should continue
     * after the y-coordinate of this figlinkport or should be terminated (cut off)
     * after this y-coordinate.
     * @param i
     */
    public void setActivationStatus(int i) {
        _activationStatus = i;
    }

}