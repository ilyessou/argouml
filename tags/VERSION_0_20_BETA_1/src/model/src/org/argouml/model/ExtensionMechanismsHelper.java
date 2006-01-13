// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
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

import java.util.Collection;


/**
 * The interface for the helper of the ExtensionMechanisms.<p>
 *
 * Created from the old ExtensionMechanismsHelper.
 */
public interface ExtensionMechanismsHelper {
    /**
     * Returns all stereotypes in a namespace, but not those in a subnamespace.
     *
     * @param ns is the namespace.
     * @return a Collection with the stereotypes.
     */
    Collection getStereotypes(Object ns);

    /**
     * Finds a stereotype in some namespace, but not in its subnamespaces.
     * Returns null if no such stereotype is found.
     *
     * TODO: What if stereo.getName() or stereo.getBaseClass() is null?
     * Then you know immediately that none will be found, but is that the
     * correct answer?
     *
     * @return the stereotype found or null.
     * @param ns is the namespace.
     * @param stereo is the stereotype.
     */
    Object getStereotype(Object ns, Object stereo);

    /**
     * Searches for a stereotype just like the given stereotype in all
     * given models.
     * The given stereotype can not have its namespace set yet;
     * otherwise it will be returned itself!
     *
     * TODO: Should it only search for stereotypes owned by the Model object?
     *
     * @param models a collection of models
     * @param stereo is the given stereotype
     * @return Stereotype
     */
    Object getStereotype(Collection models, Object stereo);

    /**
     * @param m the ModelElement
     * @return the meta name of the ModelElement
     */
    String getMetaModelName(Object m);

    /**
     * Returns all possible stereotypes for some
     * modelelement. Possible stereotypes are those stereotypes that
     * are owned by the same namespace the modelelement is owned by
     * and that have a baseclass that is the same as the
     * metamodelelement name of the modelelement.
     *
     * @param modelElement is the model element
     * @param models the models to search in
     * @return Collection
     */
    Collection getAllPossibleStereotypes(Collection models,
            				 Object modelElement);

    /**
     * Returns true if the given stereotype has a baseclass that
     * equals the baseclass of the given modelelement or one of the
     * superclasses of the given modelelement.
     *
     * @param theModelElement is the model element
     * @param theStereotype   is the stereotype
     * @return boolean
     */
    boolean isValidStereoType(Object theModelElement, Object theStereotype);

    /**
     * Get all stereotypes from all Models in the list.
     *
     * Finds only stereotypes owned by the Model objects themselves.
     *
     * @return the collection of stereotypes in all models
     *         in the current project
     * @param models the models to search
     */
    Collection getStereotypes(Collection models);

    /**
     * Sets the stereotype of some modelelement. The method also
     * copies a stereotype that is not a part of the current model to
     * the current model.<p>
     *
     * @param modelElement is the model element
     * @param stereotype is the stereotype
     */
    void addCopyStereotype(Object modelElement, Object stereotype);

    /**
     * Tests if a stereotype is a stereotype with some name and base class.
     *
     * @param object is the stereotype.
     * @param name is the name of the stereotype.
     * @param base is the base class of the stereotype.
     * @return true if object is a stereotype with the desired characteristics.
     */
    boolean isStereotype(Object object, String name, String base);

    /**
     * Tests if a stereotype is or inherits from a stereotype with some
     * name and base class.
     *
     * @param object is the stereotype.
     * @param name is the name of the stereotype.
     * @param base is the base class of the stereotype.
     * @return true if object is a (descendant of a) stereotype with the
     *	desired characteristics.
     */
    boolean isStereotypeInh(Object object, String name, String base);

    /**
     * Add an extended element to a stereotype.
     *
     * @param handle Stereotype
     * @param extendedElement ExtensionPoint
     */
    void addExtendedElement(Object handle, Object extendedElement);

    /**
     * Set the baseclass of some stereotype.
     *
     * @param handle the stereotype
     * @param baseClass the baseclass
     */
    void setBaseClass(Object handle, Object baseClass);

    /**
     * Set the icon for a stereotype.
     *
     * @param handle Stereotype
     * @param icon String
     */
    void setIcon(Object handle, Object icon);

    /**
     * Set the Tag of a TaggedValue.
     *
     * @param handle TaggedValue
     * @param tag String
     */
    void setTag(Object handle, Object tag);

    /**
     * Sets a value of some taggedValue.
     *
     * @param handle is the tagged value
     * @param value is the value
     */
    void setValueOfTag(Object handle, String value);

    //additional support for tagged values

    /**
     * Add a tagged value.
     *
     * @param handle The model element to add to.
     * @param taggedValue The tagged value to add.
     */
    void addTaggedValue(Object handle, Object taggedValue);

    /**
     * Remove a tagged value.
     *
     * @param handle The model element to remove from.
     * @param taggedValue The tagged value to remove.
     */
    void removeTaggedValue(Object handle, Object taggedValue);

    /**
     * Se the list of tagged values for a model element.
     *
     * @param handle The model element to set for.
     * @param taggedValues A Collection of tagged values.
     */
    void setTaggedValue(Object handle, Collection taggedValues);

}
