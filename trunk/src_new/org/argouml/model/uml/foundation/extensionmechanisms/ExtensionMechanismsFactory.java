// Copyright (c) 1996-2002 The Regents of the University of California. All
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

package org.argouml.model.uml.foundation.extensionmechanisms;

import org.argouml.model.uml.AbstractUmlModelFactory;
import org.argouml.model.uml.UmlFactory;

import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;

/**
 * Factory to create UML classes for the UML
 * Foundation::ExtensionMechanisms package.
 * 
 * This class contains all create, remove and build methods for ExtensionMechanism 
 * modelelements.
 * Create methods create an empty modelelement. It is registred with the 
 * eventpump however. Remove methods remove a modelelement including the listener.
 * Build methods create a modelelement but also instantiate the modelelement, 
 * for example with defaults.
 * 
 * Helper methods for ExtensionMechanism should not be placed here. Helper 
 * methods are methods like getReturnParameters. These should be placed in 
 * ExtensionMechanismHelper 
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @author jaap.branderhorst@xs4all.nl
 * 
 * @see org.argouml.model.uml.foundation.extensionmechanisms.ExtensionMechanismsHelper
 * @see org.argouml.model.uml.UmlFactory
 */

public class ExtensionMechanismsFactory extends AbstractUmlModelFactory {

    /** Singleton instance.
     */
    private static ExtensionMechanismsFactory SINGLETON =
                   new ExtensionMechanismsFactory();

    /** Singleton instance access method.
     */
    public static ExtensionMechanismsFactory getFactory() {
        return SINGLETON;
    }

    /** Don't allow instantiation
     */
    private ExtensionMechanismsFactory() {
    }

    /** Create an empty but initialized instance of a UML Stereotype.
     *  
     *  @return an initialized UML Stereotype instance.
     */
    public MStereotype createStereotype() {
        MStereotype modelElement = MFactory.getDefaultFactory().createStereotype();
	super.initialize(modelElement);
	return modelElement;
    }

    /** Create an empty but initialized instance of a UML TaggedValue.
     *  
     *  @return an initialized UML TaggedValue instance.
     */
    public MTaggedValue createTaggedValue() {
        MTaggedValue modelElement = MFactory.getDefaultFactory().createTaggedValue();
	super.initialize(modelElement);
	return modelElement;
    }
    
    /** Remove an instance of a UML Stereotype.
     */
    public void  removeStereotype(MStereotype modelelement) {
    	modelelement.remove();
    }
    
    /** Remove an instance of a UML TaggedValue
     */
    public void  removeTaggedValue(MTaggedValue  modelelement) {
    	modelelement.remove();
    }

}

