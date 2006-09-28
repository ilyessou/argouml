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

package org.argouml.model.mdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.jmi.model.MofClass;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;
import org.argouml.model.ModelManagementHelper;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.modelmanagement.Model;
import org.omg.uml.modelmanagement.Subsystem;

/**
 * Helper class for UML ModelManagement Package.
 * <p>
 * 
 * @since ARGO0.19.5
 * @author Ludovic Ma&icirc;tre
 * <p>
 * derived from NSUML implementation by:
 * @author Thierry Lach
 */
class ModelManagementHelperMDRImpl implements ModelManagementHelper {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.
            getLogger(ModelManagementHelperMDRImpl.class);

    /**
     * The model implementation.
     */
    private MDRModelImplementation nsmodel;

    /**
     * Don't allow instantiation.
     * 
     * @param implementation
     *            To get other helpers and factories.
     */
    ModelManagementHelperMDRImpl(MDRModelImplementation implementation) {
        nsmodel = implementation;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllSubSystems(java.lang.Object)
     */
    public Collection getAllSubSystems(Object ns) {
        if (ns == null) {
            return new ArrayList();
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        Iterator it = ((Namespace) ns).getOwnedElement().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Namespace) {
                list.addAll(getAllSubSystems(o));
            }
            if (o instanceof Subsystem) {
                list.add(o);
            }

        }
        return list;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllNamespaces(java.lang.Object)
     * 
     * This method is CPU intensive and therefore needs to be as efficient as
     * possible.
     * 
     */
    public Collection getAllNamespaces(Object ns) {
        if (ns == null || !(ns instanceof Namespace)) {
            return new ArrayList();
        }

        Collection namespaces = ((Namespace) ns).getOwnedElement();
        // the list of namespaces to return
        List list = Collections.EMPTY_LIST;

        // if there are no owned elements then return empty list
        if (namespaces == Collections.EMPTY_LIST || namespaces.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        // work with an array instead of iterator.
        Object[] nsArray = namespaces.toArray();

        for (int i = 0; i < nsArray.length; i++) {

            Object o = nsArray[i];
            if (o instanceof Namespace) {

                // only build a namepace if needed, with
                if (list == Collections.EMPTY_LIST) {
                    list = new ArrayList(nsArray.length);
                }

                list.add(o);

                Collection namespaces1 = getAllNamespaces(o);
                // only add all if there are some to add.
                if (namespaces1 != Collections.EMPTY_LIST
                        && namespaces1.size() > 0) {
                    list.addAll(namespaces1);
                }
            }
        }
        return list;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllModelElementsOfKindWithModel(java.lang.Object, java.lang.Object)
     */
    public Collection getAllModelElementsOfKindWithModel(Object model,
            Object type) {
        if (model == null) {
            throw new IllegalArgumentException("A model must be supplied");
        }
        Class kind = (Class) type;
        Collection ret = getAllModelElementsOfKind(model, kind);
        if (kind.isAssignableFrom(model.getClass())) {
            ret = new ArrayList(ret);
            if (!ret.contains(model)) {
                ret.add(model);
            }
        }
        return ret;
    }
    
    /*
     * @see org.argouml.model.ModelManagementHelper#getAllModelElementsOfKind(java.lang.Object, java.lang.Object)
     */
    public Collection getAllModelElementsOfKind(Object nsa, Object type) {
        if (nsa == null || type == null) {
            return Collections.EMPTY_LIST;
        }
        if (type instanceof String) {
            return getAllModelElementsOfKind(nsa, (String) type);
        }
        if (!(nsa instanceof Namespace) || !(type instanceof Class)) {
            throw new IllegalArgumentException("illegal argument - namespace: "
                    + nsa + " type: " + type);
        }

        /*
         * Because we get the metatype class stripped of its reflective
         * proxies, we need to jump through a hoop or two to find it
         * in the metamodel, then work from there to get its proxy.
         */
        String name = ((Class) type).getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        if (name.startsWith("Uml")) name = name.substring(3);

        Collection allOfType = Collections.EMPTY_LIST;
        // Get all (UML) metaclasses and search for the requested one
        Collection metaTypes = nsmodel.getModelPackage().getMofClass()
                .refAllOfClass();
        for (Iterator it = metaTypes.iterator(); it.hasNext();) {
            MofClass elem = (MofClass) it.next();
            // TODO: Generalize - assumes UML type names are unique
            // without the qualifying package names - true for UML 1.4
            if (name.equals(elem.getName())) {
                List names = elem.getQualifiedName();
                // TODO: Generalize to handle more than one level of package
                // OK for UML 1.4 because of clustering
                RefPackage pkg = nsmodel.getUmlPackage().refPackage(
                        (String) names.get(0));
                // Get the metatype proxy and use it to find all instances
                RefClass classProxy = pkg.refClass((String) names.get(1));
                allOfType = classProxy.refAllOfType();
                break;
            }
        }

        // Remove any elements not in requested namespace
        Collection returnElements = new ArrayList();
        for (Iterator i = allOfType.iterator(); i.hasNext();) {
            Object me = i.next();
            if (contained(nsa, me)) {
                returnElements.add(me);
            } 
        }
        return returnElements;
    }

    /*
     * Check whether model element is contained in given namespace/container
     */
    private boolean contained(Object container, Object candidate) {
        Object current = candidate;
        while (current != null) {
            if (container.equals(current))
                return true;
            current = nsmodel.getFacade().getModelElementContainer(current);
        }
        return false;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllModelElementsOfKind(java.lang.Object,
     *      java.lang.String)
     */
    public Collection getAllModelElementsOfKind(Object nsa, String kind) {

        if (nsa == null || kind == null) {
            return Collections.EMPTY_LIST;
        }
        if (!(nsa instanceof Namespace)) {
            throw new IllegalArgumentException("given argument " + nsa
                    + " is not a namespace");
        }
        Collection col = null;
        try {
            col = getAllModelElementsOfKind(nsa, Class.forName(kind));
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException(
                    "Can't derive a class name from " + kind);
        }
        return col;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllSurroundingNamespaces(java.lang.Object)
     */
    public Collection getAllSurroundingNamespaces(Object ns) {
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        Set set = new HashSet();
        set.add(ns);
        Namespace namespace = ((Namespace) ns);
        if (namespace.getNamespace() != null) {
            set.addAll(getAllSurroundingNamespaces(namespace.getNamespace()));
        }
        return set;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getAllBehavioralFeatures(java.lang.Object)
     * 
     * TODO: As currently coded, this actually returns all BehavioralFeatures
     * which are owned by Classifiers contained in the given namespace, which
     * is slightly different then what's documented.  It will not include any
     * BehavioralFeatures which are part of the Namespace, but which don't have
     * an owner.
     */
    public Collection getAllBehavioralFeatures(Object ns) {
        // Get Classifiers in Namespace
        Collection classifiers = getAllModelElementsOfKind(ns, nsmodel.
                getMetaTypes().getClassifier());
        ArrayList features = new ArrayList();
        Iterator i = classifiers.iterator();
        // Get Features owned by those Classifiers
        while (i.hasNext()) {
            features.addAll(nsmodel.getFacade().getFeatures(i.next()));
        }
        // Select those Features which are BehavioralFeatures
        ArrayList behavioralfeatures = new ArrayList();
        Iterator ii = features.iterator();
        while (ii.hasNext()) {
            Object f = ii.next();
            if (f instanceof BehavioralFeature) {
                behavioralfeatures.add(f);
            }
        }
        return behavioralfeatures;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getElement(java.util.Vector, java.lang.Object)
     */
    public Object getElement(Vector path, Object theRootNamespace) {
        ModelElement root = (ModelElement) theRootNamespace;
        Object name;
        int i;

        // TODO: This is very inefficient.  Investigate a direct method - tfm
        
        for (i = 0; i < path.size(); i++) {
            if (root == null || !(root instanceof Namespace)) {
                return null;
            }

            name = path.get(i);
            Iterator it = ((Namespace) root).getOwnedElement().iterator();
            root = null;
            while (it.hasNext()) {
                ModelElement me = (ModelElement) it.next();
                if (i < path.size() - 1 && !(me instanceof Namespace)) {
                    continue;
                }
                if (name.equals(me.getName())) {
                    root = me;
                    break;
                }
            }
        }
        return root;
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getPath(java.lang.Object)
     */
    public Vector getPath(Object element) {
        Vector path;

        // TODO: This only returns the path to the innermost nested Model.
        // We should have a version that returns the full path. - tfm
        if (element == null || element instanceof Model) {
            return new Vector();
        }

        path = getPath(nsmodel.getFacade().getModelElementContainer(element));
        path.add(nsmodel.getFacade().getName(element));

        return path;
    }


    /*
     * @see org.argouml.model.ModelManagementHelper#getCorrespondingElement(java.lang.Object, java.lang.Object)
     */
    public Object getCorrespondingElement(Object elem, Object model) {
        return getCorrespondingElement(elem, model, true);
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#getCorrespondingElement(java.lang.Object, java.lang.Object, boolean)
     * 
     * TODO: This should be supplement/replaced with methods to manage
     * references to external profiles using HREFs rather than using 
     * copy-on-reference semantics
     */
    public Object getCorrespondingElement(Object elem, Object model,
            boolean canCreate) {
        if (elem == null || model == null || !(elem instanceof ModelElement)) {
            throw new NullPointerException("elem: " + elem 
                    + ",model: " + model);
        }

        // Trivial case
        if (nsmodel.getFacade().getModel(elem) == model) {
            return elem;
        }

        // Base case
        if (elem instanceof Model) {
            return model;
        }

        // The cast is actually safe
        Namespace ns = (Namespace) getCorrespondingElement(
                ((ModelElement) elem).getNamespace(), model, canCreate);
        if (ns == null) {
            return null;
        }

        Iterator it = ns.getOwnedElement().iterator();
        while (it.hasNext()) {
            ModelElement e = (ModelElement) it.next();
            if (e.getClass() == ((ModelElement) elem).getClass()
                    && ((((ModelElement) elem).getName() == null 
                            && e.getName() == null) || (((ModelElement) elem).
                            getName() != null && ((ModelElement) elem).
                            getName().equals(e.getName())))) {
                return e;
            }
        }

        if (!canCreate) {
            return null;
        }

        return nsmodel.getCopyHelper().copy(elem, ns);
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#corresponds(java.lang.Object, java.lang.Object)
     */
    public boolean corresponds(Object obj1, Object obj2) {
        if (!(obj1 instanceof ModelElement)) {
            throw new IllegalArgumentException("obj1");
        }
        if (!(obj2 instanceof ModelElement)) {
            throw new IllegalArgumentException("obj2");
        }

        if (obj1 instanceof Model && obj2 instanceof Model) {
            return true;
        }
        if (obj1.getClass() != obj2.getClass()) {
            return false;
        }

        ModelElement modelElement1 = (ModelElement) obj1;
        ModelElement modelElement2 = (ModelElement) obj2;
        if ((modelElement1.getName() == null && modelElement2.getName() != null)
                || (modelElement1.getName() != null && !modelElement1.getName().
                        equals(modelElement2.getName()))) {

            return false;

        }
        return corresponds(modelElement1.getNamespace(), modelElement2.
                getNamespace());
    }

    /*
     * @see org.argouml.model.ModelManagementHelper#isCyclicOwnership(java.lang.Object, java.lang.Object)
     */
    public boolean isCyclicOwnership(Object parent, Object child) {
        return (getOwnerShipPath(parent).contains(child) || parent == child);
    }

    private List getOwnerShipPath(Object elem) {
        if (elem instanceof ModelElement) {
            List ownershipPath = new ArrayList();
            Object parent = nsmodel.getFacade().getModelElementContainer(elem);
            while (parent != null) {
                ownershipPath.add(parent);
                parent = nsmodel.getFacade().getModelElementContainer(parent);
            }
            return ownershipPath;
        }
        throw new IllegalArgumentException("Not a base");
    }
}
