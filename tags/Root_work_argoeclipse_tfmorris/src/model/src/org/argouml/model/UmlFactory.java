// $Id$
// Copyright (c) 2005-2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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


/**
 * The interface for the UmlFactory.
 */
public interface UmlFactory {
    /**
     * Creates a UML model element of the given type and uses
     * this to connect two other existing UML model elements.
     * This only works for UML elements. If a diagram contains
     * elements of another type then it is the responsibility
     * of the diagram manage those items and not call this
     * method.
     *
     * @param connectionType the UML object type of the connection
     * @param fromElement    the UML object for the "from" element
     * @param fromStyle      the aggregationkind for the connection
     *                       in case of an association
     * @param toElement      the UML object for the "to" element
     * @param toStyle        the aggregationkind for the connection
     *                       in case of an association
     * @param unidirectional for association and associationrole
     * @param namespace      the namespace to use if it can't be determined
     * @return               the newly build connection (UML object)
     * @throws IllegalModelElementConnectionException if the connection is not
     *                                                a valid thing to do
     */
    Object buildConnection(Object connectionType, Object fromElement,
            Object fromStyle, Object toElement, Object toStyle,
            Object unidirectional, Object namespace)
    	throws IllegalModelElementConnectionException;

    /**
     * Creates a UML model element of the given type.
     * This only works for UML elements. If a diagram contains
     * elements of another type then it is the responsibility
     * of the diagram manage those items and not call this
     * method. It also only works for UML model elements that
     * are represented in diagrams by a node.
     *
     * @param elementType the type of model element to build
     * @return the model element
     */
    Object buildNode(Object elementType);

    /**
     * Checks if some type of UML model element is valid to
     * connect other UML model elements.<p>
     *
     * @param connectionType  the UML object type of the connection
     * @return true if valid
     */
    boolean isConnectionType(Object connectionType);
    
    /**
     * Checks if some type of UML model element is valid to
     * connect two other existing UML model elements.<p>
     * 
     * This only works for UML elements. If a diagram contains
     * elements of another type then it is the responsibility
     * of the diagram to filter those out before calling this
     * method.
     *
     * @param connectionType  the UML object type of the connection
     * @param fromElement     the UML object type of the "from"
     * @param toElement       the UML object type of the "to"
     * @return true if valid
     */
    boolean isConnectionValid(Object connectionType, Object fromElement,
            Object toElement);

    /**
     * Delete a model element. This will do a a 'cascading deletes' 
     * which recursively deletes any model elements which would no
     * longer be valid after this element is deleted. For example, a binary
     * association which has one end deleted will also be deleted because
     * it no longer meets the minimum multiplicity constraint.
     * <p>
     * Callers who are interested in receiving notification of all elements
     * which were deleted should register an event listener to receive 
     * delete events.
     * 
     * @param elem The element to be deleted
     */
    void delete(Object elem);

    /**
     * Check whether a model element has been deleted.
     * <p>
     * <em>Note</em> that without external synchronization that there's no
     * guarantee that the element won't be deleted by another after this method
     * returns, but before the caller can do anything with it.
     *
     * @param o the object to be checked
     * @return true if removed
     */
    boolean isRemoved(Object o);

}
