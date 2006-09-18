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

package org.argouml.uml.diagram.ui;

import java.awt.Rectangle;

/**
 * An interface to be implemented by any Fig that contains a FigEnumLiterals.
 * 
 * @author Tom Morris
 * 
 * TODO: I've just extended the existing structure for now, but it all really
 * needs to be refactored into something more general so that we can easily deal
 * with arbitrary types and numbers of optionally displayed containers. - 
 * tfm - 20060315
 */
public interface EnumLiteralsCompartmentContainer {
    /**
     * Determine if the EnumerationLiterals compartment is visible.
     * @return true if the EnumerationLiterals compartment is visible.
     */
    boolean isEnumLiteralsVisible();

    /**
     * Set the visibility of the EnumerationLiterals compartment.
     * @param visible the new visibility status.
     */
    void setEnumLiteralsVisible(boolean visible);

    /**
     * @return The bounds of the EnumerationLiterals compartment
     */
    Rectangle getEnumLiteralsBounds();
}
