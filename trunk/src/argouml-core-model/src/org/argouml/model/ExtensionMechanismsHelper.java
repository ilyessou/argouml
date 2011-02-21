/* $Id$
 *******************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thomas Neustupny
 *******************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2008 The Regents of the University of California. All
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
 * The interface for the helper of the ExtensionMechanisms.
 */
public interface ExtensionMechanismsHelper {
    /**
     * Returns all stereotypes in a given namespace, 
     * and all those in any sub-namespace of the given namespace.
     *
     * @param ns is the namespace.
     * @return a Collection with the stereotypes.
     */
    Collection getStereotypes(Object ns);

    /**
     * Finds a stereotype in a given namespace, 
     * and all those in any sub-namespace of the given namespace.
     * Returns null if no such stereotype is found.
     * <p>
     * TODO: What if stereo.getName() or stereo.getBaseClass() is null?
     * Then you know immediately that none will be found, but is that the
     * correct answer?
     * Currently, null is returned in these cases. <p>
     * 
     * TODO: This function should not take a stereotype object as parameter,
     * but a name and a baseclass. <p>
     * TODO: Currently only works for stereotypes with only one baseclass. <p>
     * TODO: Currently only works for stereotypes where the baseclass is 
     * equal to the given one - inheritance does not work.
     * 
     * @return the stereotype found or null.
     * @param ns is the namespace.
     * @param stereo is the stereotype.
     */
    Object getStereotype(Object ns, Object stereo);

    /**
     * Searches for a stereotype just like the given stereotype in all
     * given models (and their sub-namespaces).
     * The given stereotype can not have its namespace set yet;
     * otherwise it will be returned itself!
     *
     * TODO: This function should not take a stereotype object as parameter,
     * but a name and a baseclass. <p>
     * TODO: Currently only works for stereotypes with only one baseclass. <p>
     * TODO: Should it only search for stereotypes owned by the Model object? 
     *
     * @param models a collection of models
     * @param stereo is the given stereotype
     * @return Stereotype
     */
    Object getStereotype(Collection models, Object stereo);

    /**
     * @param m the ModelElement
     * @return the name of the metatype (i.e UML type) of this Element
     * @deprecated for 0.27.3 by tfmorris. Use {@link MetaTypes#getName(Object)}
     */
    @Deprecated
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
     * Returns <code>true</code> if the given stereotype has a baseclass that
     * equals the baseclass of the given ModelElement or one of the superclasses
     * of the given ModelElement.
     * 
     * @param theModelElement
     *                is the model element
     * @param theStereotype
     *                is the stereotype
     * @return boolean
     */
    boolean isValidStereotype(Object theModelElement, Object theStereotype);

    /**
     * Get all stereotypes from all Models in the list. <p>
     *
     * Finds also all stereotypes owned by any sub-namespaces of the Model.
     * 
     * @return the collection of stereotypes in all models
     *         in the current project
     * @param models the models to search
     */
    Collection getStereotypes(Collection models);

    /**
     * Get commonly used tagged value types. <p>
     * While in early UML 1.x versions only String was provided, in UML 2.x
     * tagged values are stereotype properties with any possible type. However
     * even in UML 2.x only primitive types are used in most cases. The client
     * of the model subsystem should be able to handle at least the returned
     * types, but is still free to provide support for any type.
     * 
     * @return a collection of types
     */
    Collection getCommonTaggedValueTypes();

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
     * Tests if a stereotype has a given name and given base class.
     * While comparing the baseclass, inheritance is not considered.
     *
     * @param object is the stereotype.
     * @param name is the name of the stereotype.
     * @param base is a string representing the base class of the stereotype.
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
     * Add a baseclass to some stereotype.
     *
     * @param handle the stereotype
     * @param baseClass the baseclass to add
     */
    void addBaseClass(Object handle, Object baseClass);

    /**
     * Remove baseclass from some stereotype.
     *
     * @param handle the stereotype
     * @param baseClass the baseclass to remove
     */
    void removeBaseClass(Object handle, Object baseClass);

    /**
     * Set the icon for a stereotype.
     *
     * @param handle Stereotype
     * @param icon String
     */
    void setIcon(Object handle, Object icon);


    /**
     * Set the tagType of a TaggedDefinition.  This controls the range of legal
     * values for the associated TaggedValues.  
     * 
     * @param handle the taggedValue
     * @param tagType A string containing the name of the type for values that
     *                may be assigned to this tag. This can either be the name
     *                of a datatype (e.g. "String", "Integer" or "Boolean") or
     *                the name of a metaclass for more complex types of tagged
     *                values.
     */
    void setTagType(Object handle, String tagType);
    
    /**
     * Set the type of a taggedvalue.
     * 
     * @param handle the taggedValue
     * @param type the tagDefinition
     */
    void setType(Object handle, Object type);

    /**
     * Sets the dataValues of the given TaggedValue to a single String value.
     * Provided for backward compatibility with UML 1.3. new uses should use
     * setDataValues.
     * 
     * @param handle
     *                is the tagged value
     * @param value
     *                is the value
     * @deprecated for 0.25.5 by tfmorris. Use
     *             {@link #setDataValues(Object, String[])}.
     */
    @Deprecated
    void setValueOfTag(Object handle, String value);

    /**
     * Sets the dataValues of the given TaggedValue. UML1 only.
     *
     * @param handle is the tagged value
     * @param values an array of String values
     */
    void setDataValues(Object handle, String[] values);
    
    //additional support for tagged values

    /**
     * Add a tagged value.
     *
     * @param handle The model element to add to.
     * @param taggedValue The tagged value to add.
     */
    void addTaggedValue(Object handle, Object taggedValue);

    /**
     * Apply a profile to a model or another profile.
     * 
     * @param handle The model or profile.
     * @param profile The to be applied profile.
     */
    public void applyProfile(Object handle, Object profile);

    /**
     * Remove a tagged value.
     *
     * @param handle The model element to remove from.
     * @param taggedValue The tagged value to remove.
     */
    void removeTaggedValue(Object handle, Object taggedValue);

    /**
     * Set the list of tagged values for a model element. UML1 only.
     *
     * @param handle The model element to set for.
     * @param taggedValues A Collection of tagged values.
     */
    void setTaggedValue(Object handle, Collection taggedValues);

    /**
     * Set tagged value (bound to a stereotype) for a model element.
     * The value might be an List, depending on the multiplicity of the
     * property.
     *
     * @param handle The model element to set for.
     * @param property The property of the applied stereotype.
     * @param value Single value or a List value to set to.
     */
    void setTaggedValue(Object handle, Object property, Object value);

    /**
     * Unapply a profile to a model or another profile.
     * 
     * @param handle The model or profile.
     * @param profile The to be unapplied profile.
     */
    public void unapplyProfile(Object handle, Object profile);

    /**
     * Returns <code>true</code> if the given ModelElement has a Stereotype
     * with the given name.
     * 
     * @param element the given ModelElement
     * @param name the given name
     * @return true if there is such a stereotype
     */
    boolean hasStereotype(Object element, String name);

    /**
     * Make a profile applicable. Usually a profile edited in a UML design
     * environment like ArgoUML is not directly applicable, but needs to be
     * shifted to the metamodel level, so that it can serve as an extension
     * to the UML metamodel.
     * 
     * @param handle the profile before made applicable to a model
     * @return the profile made applicable to a model
     */
    Object makeProfileApplicable(Object handle);
}
