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


package org.argouml.uml;

import java.util.Iterator;
/**
 *   This abstract class captures the configurable behavior of Argo.
 *
 *   @author Curt Arnold
 */
public abstract class Profile {

    /**
     *    This method produces a string that represents the specific
     *    model element in the context of the specified namespace.
     *    @param element element to represent.
     *    @param namespace context namespace (may be null).
     *    @return a string representing the model element
     */
    public abstract String formatElement(Object/*MModelElement*/ element,
					 Object namespace);
    /**
     *   This method produces a string the represents the collection
     *   of model elements in the context of the specified namespace.
     *   @param iter iterator over collection
     *   @param namespace context namespace (may be null).
     *   @return a string representing the collection
     */
    public abstract String formatCollection(Iterator iter,
					    Object namespace);
    /**
     * @return the UML Model that contains the profile model
     * @throws ProfileException if failed to get profile.
     */
    public abstract Object/*MModel*/ getProfileModel() throws ProfileException;
}
