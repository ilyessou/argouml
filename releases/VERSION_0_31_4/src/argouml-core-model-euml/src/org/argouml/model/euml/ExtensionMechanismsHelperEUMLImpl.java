// $Id$
/*******************************************************************************
 * Copyright (c) 2007,2010 Tom Morris and other contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tom Morris - initial framework 
 *    thn
 *****************************************************************************/

package org.argouml.model.euml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.argouml.model.ExtensionMechanismsHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * The implementation of the ExtensionMechanismsHelper for EUML2.
 */
class ExtensionMechanismsHelperEUMLImpl implements ExtensionMechanismsHelper {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    /**
     * Constructor.
     * 
     * @param implementation The ModelImplementation.
     */
    public ExtensionMechanismsHelperEUMLImpl(
            EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public void addBaseClass(Object handle, Object baseClass) {
        if (handle instanceof Stereotype) {
            org.eclipse.uml2.uml.Class metaclass = getMetaclass(baseClass);
            Profile profile = ((Stereotype) handle).getProfile();
            if (metaclass != null && profile != null) {
                profile.createMetaclassReference(metaclass);
                ((Stereotype) handle).createExtension(metaclass, false);
                return;
            }
        }
        throw new IllegalArgumentException(
                "Not a Stereotype or illegal base class"); //$NON-NLS-1$
    }

    public void addCopyStereotype(Object modelElement, Object stereotype) {
        modelImpl.getCoreHelper().addStereotype(modelElement, stereotype);
    }

    public void addExtendedElement(Object handle, Object extendedElement) {
        // TODO: Auto-generated method stub

    }

    public void addTaggedValue(Object handle, Object taggedValue) {
        // TODO: Auto-generated method stub

    }

    public void applyProfile(Object handle, Object profile) {
        if (profile instanceof Profile) {
            if (handle instanceof Model) {
                ((Model) handle).applyProfile((Profile) profile);
            } else if (handle instanceof Profile) {
                ((Profile) handle).applyProfile((Profile) profile);
            }
            // also apply subprofiles:
            Iterator<Package> iter = ((Profile) profile).getNestedPackages()
                    .iterator();
            while (iter.hasNext()) {
                Package p = iter.next();
                if (p instanceof Profile) {
                    applyProfile(handle, p);
                }
            }
        }
    }

    public Collection getAllPossibleStereotypes(Collection models,
            Object modelElement) {
        List<Stereotype> ret = new ArrayList<Stereotype>();
        if (modelElement instanceof Element) {
            for (Stereotype stereo : getStereotypes(models)) {
                if (((Element) modelElement).isStereotypeApplicable(stereo)) {
                    ret.add(stereo);
                }
            }
        }
        return ret;
    }

    @Deprecated
    public String getMetaModelName(Object m) {
        if (m instanceof Element) {
            return getMetaModelName(m.getClass());
        }
        throw new IllegalArgumentException("Not an Element"); //$NON-NLS-1$
    }

    /**
     * @param clazz the UML class
     * @return the meta name of the UML class
     */
    protected String getMetaModelName(Class clazz) {
        return modelImpl.getMetaTypes().getName(clazz);
    }

    public Object getStereotype(Object ns, Object stereo) {
        if (!(ns instanceof Profile)) {
            throw new IllegalArgumentException("profile"); //$NON-NLS-1$
        }
        if (!(stereo instanceof Stereotype)) {
            throw new IllegalArgumentException("stereotype"); //$NON-NLS-1$
        }
        String name = ((Stereotype) stereo).getName();
        Collection<Class> baseClasses = ((Stereotype) stereo)
                .getAllExtendedMetaclasses();
        if (name == null || baseClasses.size() != 1) {
            return null;
        }
        Class baseClass = baseClasses.iterator().next();

        for (Stereotype o : getStereotypes(ns)) {
            if (name.equals(o.getName())
                    && o.getAllExtendedMetaclasses().contains(baseClass)) {
                return o;
            }
        }
        return null;
    }

    public Object getStereotype(Collection models, Object stereo) {
        if (stereo == null) {
            throw new IllegalArgumentException("null argument"); //$NON-NLS-1$
        }
        if (!(stereo instanceof Stereotype)) {
            throw new IllegalArgumentException("stereotype"); //$NON-NLS-1$
        }
        String name = ((Stereotype) stereo).getName();
        Collection<Class> baseClasses = ((Stereotype) stereo)
                .getAllExtendedMetaclasses();
        if (name == null || baseClasses.size() != 1) {
            return null;
        }
        Class baseClass = baseClasses.iterator().next();

        for (Model model : ((Collection<Model>) models)) {
            // TODO: this should call the single namespace form
            // getStereotype(it2.next(); stereo);
            for (Stereotype o : getStereotypes(model)) {
                if (name.equals(o.getName())
                        && o.getAllExtendedMetaclasses().contains(baseClass)) {
                    return o;
                }
            }
        }
        return null;
    }

    public Collection<Stereotype> getStereotypes(Object ns) {
        if (ns instanceof Profile) {
            return new ArrayList<Stereotype>(((Profile) ns).getOwnedStereotypes());
        }
        return Collections.emptySet();
    }

    public Collection<Stereotype> getStereotypes(Collection models) {
        Collection<Stereotype> l = new ArrayList<Stereotype>();
        if (models != null) {
            for (Object ns : models) {
                if (ns instanceof Profile) {
                    l.addAll(((Profile) ns).getOwnedStereotypes());
                    // TODO: Do we really want stereotypes from nested packages?
                    Iterator<Package> iter = ((Profile) ns).getNestedPackages()
                            .iterator();
                    while (iter.hasNext()) {
                        Package p = iter.next();
                        if (p instanceof Profile) {
                            l.addAll(getAllStereotypesIn((Profile) p));
                        }
                    }
                }
            }
        }
        return l;
    }

    public boolean hasStereotype(Object handle, String name) {
        if (name == null || !(handle instanceof Element)) {
            throw new IllegalArgumentException();
        }
        Element element = (Element) handle;
        if (element.getAppliedStereotype(name) != null) {
            return true;
        }
        return false;
    }

    public boolean isStereotype(Object object, String name, String base) {
        if (!(object instanceof Stereotype)) {
            return false;
        }
        Stereotype st = (Stereotype) object;
        if (name == null && st.getName() != null) {
            return false;
        }
        if (base == null && !(st.getAllExtendedMetaclasses().isEmpty())) {
            return false;
        }
        for (Class c : st.getAllExtendedMetaclasses()) {
            if (c.getName().equals(base)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStereotypeInh(Object object, String name, String base) {
        if (!(object instanceof Stereotype)) {
            return false;
        }
        if (isStereotype(object, name, base)) {
            return true;
        }
        /*
         * TODO: mvw: do we really look into super-types of the stereotype, or
         * should we be looking into super-types of the baseclass?
         */
        Iterator it = modelImpl.getCoreHelper().getSupertypes(object)
                .iterator();
        while (it.hasNext()) {
            if (isStereotypeInh(it.next(), name, base)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidStereotype(Object theModelElement,
            Object theStereotype) {
        if (theModelElement instanceof Element
                && theStereotype instanceof Stereotype) {
            return ((Element) theModelElement)
                    .isStereotypeApplicable((Stereotype) theStereotype);
        }
        return false;
    }

    public void removeBaseClass(Object handle, Object baseClass) {
        if (handle instanceof Stereotype) {
            org.eclipse.uml2.uml.Class metaclass = getMetaclass(baseClass);
            Profile profile = ((Stereotype) handle).getProfile();
            if (metaclass != null && profile != null) {
                Stereotype st = (Stereotype) handle;
                for (Extension ext : profile.getOwnedExtensions(false)) {
                    if (ext.getMetaclass() == metaclass
                            && ext.getEndTypes().contains(st)) {
                        for (Property p : st.getAttributes()) {
                            Association assoc = p.getAssociation();
                            if (assoc != null && assoc == ext) {
                                // additional cleanup needed, because
                                // this would not be removed by ext.destroy():
                                p.destroy();
                                break;
                            }
                        }
                        // remove base class by destroying the extension
                        ext.destroy();
                        break;
                    }
                }
                return;
            }
        }
        throw new IllegalArgumentException(
                "Not a Stereotype or illegal base class"); //$NON-NLS-1$
    }

    public void removeTaggedValue(Object handle, Object taggedValue) {
        // TODO: Auto-generated method stub
    }

    public void setIcon(Object handle, Object icon) {
        // TODO: Auto-generated method stub
    }

    public void setTaggedValue(Object handle, Collection taggedValues) {
        // TODO: Auto-generated method stub
    }

    public void setTagType(Object handle, String tagType) {
        // TODO: Auto-generated method stub
    }

    public void setType(Object handle, Object type) {
        // TODO: Auto-generated method stub
    }

    public void setValueOfTag(Object handle, String value) {
        // TODO: Auto-generated method stub
    }

    public void setDataValues(Object handle, String[] value) {
        // TODO: Auto-generated method stub
    }

    public void unapplyProfile(Object handle, Object profile) {
        if (profile instanceof Profile) {
            if (handle instanceof Package) {
                ((Model) handle).unapplyProfile((Profile) profile);
            } else if (handle instanceof Profile) {
                ((Profile) handle).unapplyProfile((Profile) profile);
            }
            // also unapply subprofiles:
            Iterator<Package> iter = ((Profile) profile).getNestedPackages()
                    .iterator();
            while (iter.hasNext()) {
                Package p = iter.next();
                if (p instanceof Profile) {
                    unapplyProfile(handle, p);
                }
            }
        }
    }

    public Object makeProfileApplicable(Object handle) {
        Object result = null;
        if (handle instanceof Profile) {
            result = ((Profile) handle).define();
            // also define subprofiles:
            Iterator<Package> iter = ((Profile) handle).getNestedPackages()
                    .iterator();
            while (iter.hasNext()) {
                Package p = iter.next();
                if (p instanceof Profile) {
                    makeProfileApplicable(p);
                }
            }
        }
        return result;
    }

    private Collection<Stereotype> getAllStereotypesIn(Profile p) {
        List<Stereotype> l = new ArrayList<Stereotype>();
        Iterator<Element> iter = p.getOwnedElements().iterator();
        while (iter.hasNext()) {
            Element elem = iter.next();
            if (elem instanceof Stereotype) {
                l.add((Stereotype) elem);
            } else if (elem instanceof Profile) {
                l.addAll(getAllStereotypesIn((Profile) elem));
            }
        }
        return l;
    }

    private org.eclipse.uml2.uml.Class getMetaclass(Object baseClass) {
        org.eclipse.uml2.uml.Class metaclass = null;
        if (baseClass instanceof String) {
            URI uri = URI.createURI(UMLResource.UML_METAMODEL_URI);
            ResourceSet rs = modelImpl.getEditingDomain().getResourceSet();
            Resource res = rs.getResource(uri, true);
            Model m = (Model) EcoreUtil.getObjectByType(res.getContents(),
                    UMLPackage.Literals.PACKAGE);
            metaclass = (org.eclipse.uml2.uml.Class) m
                    .getOwnedType((String) baseClass);
        } else if (baseClass instanceof org.eclipse.uml2.uml.Class) {
            metaclass = (org.eclipse.uml2.uml.Class) baseClass;
        }
        return metaclass;
    }
}
