// $Id$
// Copyright (c) 2007, The ArgoUML Project
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the ArgoUML Project nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE ArgoUML PROJECT ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE ArgoUML PROJECT BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.model.euml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.argouml.model.ModelManagementHelper;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;

/**
 * The implementation of the ModelManagementHelper for EUML2.
 */
class ModelManagementHelperEUMLImpl implements ModelManagementHelper {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    /**
     * Constructor.
     * 
     * @param implementation
     *                The ModelImplementation.
     */
    public ModelManagementHelperEUMLImpl(EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public boolean corresponds(Object obj1, Object obj2) {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection getAllBehavioralFeatures(Object ns) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getAllContents(Object element) {
        if (!(element instanceof Element)) {
            throw new IllegalArgumentException(
                    "The argument must be instance of Element"); //$NON-NLS-1$
        }
        Collection result = new HashSet();
        if (element instanceof Collaboration) {
            // TODO: implement
        }
        if (element instanceof Classifier) {
            result.addAll(((Classifier) element).allFeatures());
        }
        if (element instanceof Namespace) {
            result.addAll(((Namespace) element).getOwnedMembers());
        }
        return result;
    }

    public Collection getAllImportedElements(Object pack) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getAllModelElementsOfKind(Object nsa, Object type) {
        if (!(nsa instanceof Namespace)) {
            throw new IllegalArgumentException(
                    "nsa must be instance of Namespace"); //$NON-NLS-1$
        }
        Class theType = null;
        if (type instanceof String) {
            try {
                theType = Class.forName((String) type);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (type instanceof Class) {
            theType = (Class) type;
        } else {
            throw new IllegalArgumentException(
                    "type must be instance of Class or String"); //$NON-NLS-1$
        }
        if (!Element.class.isAssignableFrom(theType)) {
            throw new IllegalArgumentException("type must represent an Element"); //$NON-NLS-1$
        }

        Collection<Element> result = new ArrayList<Element>();

        for (Element element : ((Namespace) nsa).allOwnedElements()) {
            if (theType.isAssignableFrom(element.getClass())) {
                result.add(element);
            }
        }

        return result;
    }

    /*
     * Check whether model element is contained in given namespace/container.
     * TODO: Investigate a faster way to do this
     */
    private boolean contained(Object container, Object candidate) {
        Object current = candidate;
        while (current != null) {
            if (container.equals(current)) {
                return true;
            }
            current = modelImpl.getFacade().getModelElementContainer(current);
        }
        return false;
    }

    public Collection getAllModelElementsOfKind(Object nsa, String kind) {
        return getAllModelElementsOfKind(nsa, kind);
    }

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

    public Collection getAllNamespaces(Object ns) {
        Set<Element> results = new HashSet<Element>();
        for (Element element : ((Namespace) ns).allOwnedElements()) {
            if (element instanceof Namespace) {
                results.add(element);
            }
        }
        return results;
    }

    public Collection getAllPossibleImports(Object pack) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getAllSubSystems(Object ns) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getAllSurroundingNamespaces(Object ns) {
        return ((NamedElement) ns).allNamespaces();
    }

    public Collection getContents(Object namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getCorrespondingElement(Object elem, Object model) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getCorrespondingElement(Object elem, Object model,
            boolean canCreate) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getElement(Vector path, Object theRootNamespace) {
        // TODO Auto-generated method stub
        return null;
    }

    public Vector<String> getPath(Object element) {
        // TODO: Needs completing - stub implementation only! - tfm
        Vector<String> path = new Vector<String>();
        path.add(modelImpl.getFacade().getName(element));
        return path;
    }

    public boolean isCyclicOwnership(Object parent, Object child) {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeImportedElement(Object handle, Object me) {
        // TODO Auto-generated method stub

    }

    public void setAlias(Object handle, String alias) {
        // TODO Auto-generated method stub

    }

    public void setImportedElements(Object pack, Collection imports) {
        // TODO Auto-generated method stub

    }

    public void setSpecification(Object handle, boolean isSpecification) {
        // TODO Auto-generated method stub

    }

}
