// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.ui;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.profile.FormatingStrategy;
import org.argouml.profile.Profile;

/**
 * A combobox entry. <p>
 *
 * TODO: What is a Phantom element? Document it.
 * MVW: I think it is an entry in the list, 
 * that when selected, deletes the
 * UML model-association, just like the "" 
 * in the comboboxes currently. <p>
 * 
 *  This class does not seem to be in use currently. 
 *  Is it a good idea to complete this? Or shall we remove?
 *  @deprecated in 0.25.1 by Bob Tarling. This class is never instantiated 
 *  by ArgoUML.
 */
public class UMLComboBoxEntry implements Comparable {
    
    private static final Logger LOG = Logger.getLogger(UMLComboBoxEntry.class);
    
    private Object element;
    private String shortName;

    /** longName is composed of an identifier and a name as in Class: String */
    private String longName;
    private FormatingStrategy formater;

    /** display name will be the same as shortName unless there
     *  is a name collision */
    private String displayName;

    /** i am not quite sure what isPhantom means, it may be that it is an
     *  entry that is not in the model list...pjs */
    private boolean thisIsAPhantom;

    /**
     * The constructor.
     *
     * @param modelElement the model element that this combobox entry represents
     * @param theProfile the profile according which the textual
     *                   representatation of the modelelement is generated
     * @param isPhantom true if this is a phantom element
     */
    public UMLComboBoxEntry(Object modelElement,
            Profile theProfile, boolean isPhantom) {
        element = modelElement;
        if (modelElement != null) {
            Object ns = Model.getFacade().getNamespace(modelElement);
            if (theProfile.getFormatingStrategy() != null) {
                shortName = theProfile.getFormatingStrategy().formatElement(
                        modelElement, ns);
            } else {
                // TODO What should we do here?
            }
        } else {
            shortName = "";
        }

        //
        //   format the element in its own namespace
        //       should result in an name without packages
        formater = theProfile.getFormatingStrategy();
        longName = null;
        displayName = shortName;
        thisIsAPhantom = isPhantom;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return displayName;
    }

    /**
     * Generate a string representatation of the UML modelelement
     * of this combobox element.
     */
    public void updateName() {
        if (element != null) {
            Object ns = Model.getFacade().getNamespace(element);
            if (formater != null) {
                shortName = formater.formatElement(element, ns);                
            } else {
                // TODO what should we do here?                
            }
        }
    }

    /**
     * If one of the given names equals the "short name", then
     * we'll display the longname.
     *
     * @param before the first given name
     * @param after the 2nd given name
     */
    public void checkCollision(String before, String after) {
        boolean collision = (before != null && before.equals(shortName))
            || (after != null && after.equals(shortName));
        if (collision) {
            if (longName == null) {
                longName = getLongName();
            }
            displayName = longName;
        }
    }

    /**
     * @return the short name of the modelelement
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the long name of the modelelement
     */
    public String getLongName() {
        if (longName == null) {
            if (element != null) {
                if (formater != null) {
                    longName = formater.formatElement(element, null);
                } else {
                    // TODO what should we do here?
                }
            }
            else {
                longName = "(unspecified)"; // TODO: I18N
            }
        }
        return longName;
    }

    // Refactoring: static to denote that it doesn't use any class members.
    // TODO:
    // Idea to move this to MMUtil together with the same function from
    // org/argouml/uml/cognitive/critics/WizOperName.java
    // org/argouml/uml/generator/ParserDisplay.java
    private static Object findNamespace(Object phantomNS, Object targetModel) {
        Object ns = null;
        Object targetParentNS = null;
        Object parentNS = Model.getFacade().getNamespace(phantomNS);
        if (parentNS == null) {
            ns = targetModel;
        } else {
            targetParentNS = findNamespace(parentNS, targetModel);
            //
            //   see if there is already an element with the same name
            //
            Collection ownedElements =
                Model.getFacade().getOwnedElements(targetParentNS);
            String phantomName = Model.getFacade().getName(phantomNS);
            String targetName;
            if (ownedElements != null) {
                Object ownedElement;
                Iterator iter = ownedElements.iterator();
                while (iter.hasNext()) {
                    ownedElement = iter.next();
                    targetName = Model.getFacade().getName(ownedElement);
                    if (targetName != null && phantomName.equals(targetName)) {
                        if (Model.getFacade().isAPackage(ownedElement)) {
                            ns = ownedElement;
                            break;
                        }
                    }
                }
            }
            if (ns == null) {
                ns = Model.getModelManagementFactory()
                    .createPackage();
                Model.getCoreHelper().setName(ns, phantomName);
                Model.getCoreHelper().addOwnedElement(targetParentNS, ns);
            }
        }
        return ns;
    }

    /**
     * @param targetModel the UML Model that contains the modelelement
     * @return the modelelement represented by this combobox item
     */
    public Object getElement(Object targetModel) {
        //
        //  if phantom then
        //    we need to possibly recreate the package structure
        //       in the target model
        if (thisIsAPhantom && targetModel != null) {
            Object targetNS =
                findNamespace(
                        Model.getFacade().getNamespace(element),
                        targetModel);
            Object clone = null;
            try {
                clone = element.getClass().getConstructor(
                        new Class[] {}).newInstance(new Object[] {});
                Model.getCoreHelper().setName(
                        clone,
                        Model.getFacade().getName(element));
                Model.getCoreHelper().addAllStereotypes(clone,
                        Model.getFacade().getStereotypes(element));
                if (Model.getFacade().isAStereotype(clone)) {
                    Collection baseClasses =
                        Model.getFacade().getBaseClasses(element);
                    Iterator iter =
                        baseClasses != null ? baseClasses.iterator() : null;
                    while (iter != null && iter.hasNext()) {
                        Model.getExtensionMechanismsHelper().addBaseClass(clone,
                            iter.next());
                    }
                }
                Model.getCoreHelper().addOwnedElement(targetNS, clone);
                element = clone;
            }
            catch (Exception ex) {
                LOG.error("Exception in getElement()", ex);
            }
            thisIsAPhantom = false;
        }
        return element;
    }


    /**
     * @param modelElement the modelelement represented by this combobox item
     * @param isPhantom true if this is a phantom element
     */
    public void setElement(Object modelElement,
            boolean isPhantom) {
        element = modelElement;
        thisIsAPhantom = isPhantom;
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final java.lang.Object other) {
        int compare = -1;
        if (other instanceof UMLComboBoxEntry) {
            UMLComboBoxEntry otherEntry = (UMLComboBoxEntry) other;
            compare = 0;
            if (otherEntry != this) {
                //
                //  if this is a "void" entry it goes first
                //
                if (element == null) {
                    compare = -1;
                }
                else {
                    //
                    //  if the other one is "void" it goes first
                    //
                    if (otherEntry.getElement(null) == null) {
                        compare = 1;
                    }
                    else {
                        //
                        //   compare short names
                        //
                        compare = getShortName()
                            .compareTo(otherEntry.getShortName());
                        //
                        //   compare long names
                        //
                        if (compare == 0) {
                            compare = getLongName()
                                .compareTo(otherEntry.getLongName());
                        }
                    }
                }
            }
        }
        return compare;
    }

    /**
     * @param modelElement the modelelement that has its name changed
     */
    public void nameChanged(Object modelElement) {
        if (modelElement == element && element != null) {
            Object ns = Model.getFacade().getNamespace(element);
            if (formater != null) {
                shortName = formater.formatElement(element, ns);                
            } else {
                // TODO what should we do here?                
            }
            displayName = shortName;
            longName = null;
        }
    }

    /**
     * @return true if this is a phantom element
     */
    public boolean isPhantom() {
        return thisIsAPhantom;
    }
}
