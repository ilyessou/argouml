// $Id$
// Copyright (c) 1996-99 The Regents of the University of California. All
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
import org.argouml.uml.*;
import java.util.*;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;

public class UMLComboBoxEntry implements Comparable {
    private Object/*MModelElement*/ _element;
    private String _shortName;
    
    /** _longName is composed of an identifier and a name as in Class: String */
    private String _longName;
    private Profile _profile;
    
    /** _display name will be the same as shortName unless there 
     *  is a name collision */
    private String _displayName;
    
    /** i am not quite sure what _isPhantom means, it may be that it is an
     *  entry that is not in the model list...pjs */
    private boolean _isPhantom;

    public UMLComboBoxEntry(Object/*MModelElement*/ element, Profile profile, boolean isPhantom) {
        _element = element;
        if (element != null) {
            Object/*MNamespace*/ ns = ModelFacade.getNamespace(element);
            _shortName = profile.formatElement(element, ns);
        }
        else {
            _shortName = "";
        }


        //
        //   format the element in its own namespace
        //       should result in an name without packages
        _profile = profile;
        _longName = null;
        _displayName = _shortName;
        _isPhantom = isPhantom;
    }

    public String toString() {
        return _displayName;
    }

    public void updateName() {
        if (_element != null) {
            Object/*MNamespace*/ ns = ModelFacade.getNamespace(_element);
            _shortName = _profile.formatElement(_element, ns);
        }
    }

    public void checkCollision(String before, String after) {
        boolean collision = (before != null && before.equals(_shortName)) ||
                (after != null && after.equals(_shortName));
        if (collision) {
            if (_longName == null) {
                _longName = getLongName();
            }
            _displayName = _longName;
        }
    }

    public String getShortName() {
        return _shortName;
    }

    public String getLongName() {
        if (_longName == null) {
            if (_element != null) {
                _longName = _profile.formatElement(_element, null);
            }
            else {
                _longName = "void";
            }
        }
        return _longName;
    }

    // Refactoring: static to denote that it doesn't use any class members.
    // TODO:
    // Idea to move this to MMUtil together with the same function from
    // org/argouml/uml/cognitive/critics/WizOperName.java
    // org/argouml/uml/generator/ParserDisplay.java
    private static Object findNamespace(Object/*MNamespace*/ phantomNS, Object/*MModel*/ targetModel) {
        Object/*MNamespace*/ ns = null;
        Object/*MNamespace*/ targetParentNS = null;
        Object/*MNamespace*/ parentNS = ModelFacade.getNamespace(phantomNS);
        if (parentNS == null) {
            ns = targetModel;
        }
        else {
            targetParentNS = findNamespace(parentNS, targetModel);
            //
            //   see if there is already an element with the same name
            //
            Collection ownedElements = ModelFacade.getOwnedElements(targetParentNS);
            String phantomName = ModelFacade.getName(phantomNS);
            String targetName;
            if (ownedElements != null) {
                Object/*MModelElement*/ ownedElement;
                Iterator iter = ownedElements.iterator();
                while (iter.hasNext()) {
                    ownedElement = iter.next();
                    targetName = ModelFacade.getName(ownedElement);
                    if (targetName != null && phantomName.equals(targetName)) {
                        if (ModelFacade.isAPackage(ownedElement)) {
                            ns = ownedElement;
                            break;
                        }
                    }
                }
            }
            if (ns == null) {
                ns = UmlFactory.getFactory().getModelManagement().createPackage();
                ModelFacade.setName(ns, phantomName);
                ModelFacade.addOwnedElement(targetParentNS, ns);
            }
        }
        return ns;
    }

    public Object/*MModelElement*/ getElement(Object targetModel) {
        //
        //  if phantom then
        //    we need to possibly recreate the package structure
        //       in the target model
        if (_isPhantom && targetModel != null) {
            Object/*MNamespace*/ targetNS = findNamespace(ModelFacade.getNamespace(_element), targetModel);
            Object/*MModelElement*/ clone = null;
            try {
                clone = _element.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});
                ModelFacade.setName(clone, ModelFacade.getName(_element));
                Object stereo = null;
                if (ModelFacade.getStereotypes(_element).size() > 0) {
                    stereo = ModelFacade.getStereotypes(_element).iterator().next();
                }
                ModelFacade.setStereotype(clone, stereo);
                if (ModelFacade.isAStereotype(clone)) {
                    ModelFacade.setBaseClass(clone, ModelFacade.getBaseClass(_element));
                }
                ModelFacade.addOwnedElement(targetNS, clone);
                _element = clone;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            _isPhantom = false;
        }
        return _element;
    }


    public void setElement(Object/*MModelElement*/ element, boolean isPhantom) {
        _element = element;
        _isPhantom = isPhantom;
    }

    public int compareTo(final java.lang.Object other) {
        int compare = -1;
        if (other instanceof UMLComboBoxEntry) {
            UMLComboBoxEntry otherEntry = (UMLComboBoxEntry) other;
            compare = 0;
            if (otherEntry != this) {
                //
                //  if this is a "void" entry it goes first
                //
                if (_element == null) {
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
                        compare = getShortName().compareTo(otherEntry.getShortName());
                        //
                        //   compare long names
                        //
                        if (compare == 0) {
                            compare = getLongName().compareTo(otherEntry.getLongName());
                        }
                    }
                }
            }
        }
        return compare;
    }

    public void nameChanged(Object/*MModelElement*/ element) {
        if (element == _element && _element != null) {
            Object/*MNamespace*/ ns = ModelFacade.getNamespace(_element);
            _shortName = _profile.formatElement(_element, ns);
            _displayName = _shortName;
            _longName = null;
        }
    }

    public boolean isPhantom() {
        return _isPhantom;
    }
}