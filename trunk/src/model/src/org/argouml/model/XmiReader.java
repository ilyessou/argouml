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

package org.argouml.model;

import java.util.Collection;
import java.util.Map;

import org.xml.sax.InputSource;

/**
 * A wrapper around the genuine XmiReader that provides public
 * access with no knowledge of actual UML implementation.
 * Unlike many of the interfaces to the model there is no control to force
 * a single instance of an XmiReader. This is to allow work objects generated
 * by the imlementation to be garbage collected when an XmiReader instance
 * falls out of scope.
 *
 * @author Bob Tarling
 */
public interface XmiReader {

    /**
     * Parses a given inputsource to a model.
     *
     * @param pIs the input source for parsing
     * @return MModel the UML model
     * @throws UmlException on any error
     * @deprecated use parse()
     */
    Object parseToModel(InputSource pIs) throws UmlException;

    /**
     * Parses a given inputsource to a collection of top level elements.
     *
     * @param pIs the input source for parsing
     * @return a collection of top level elements
     * @throws UmlException on any error
     */
    Collection parse(InputSource pIs) throws UmlException;

    /**
     * @return the map
     */
    Map getXMIUUIDToObjectMap();
}
