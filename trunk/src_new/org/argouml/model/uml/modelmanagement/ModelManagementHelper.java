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

package org.argouml.model.uml.modelmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.argouml.ui.ProjectBrowser;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.model_management.MSubsystem;

/**
 * Helper class for UML ModelManagement Package.
 *
 * Current implementation is a placeholder.
 * 
 * @since ARGO0.11.2
 * @author Thierry Lach
 */
public class ModelManagementHelper {

    /** Don't allow instantiation.
     */
    private ModelManagementHelper() {
    }
    
     /** Singleton instance.
     */
    private static ModelManagementHelper SINGLETON =
                   new ModelManagementHelper();

    
    /** Singleton instance access method.
     */
    public static ModelManagementHelper getHelper() {
        return SINGLETON;
    }
    
    /**
	 * Returns all subsystems found in the projectbrowser model
	 * @return Collection
	 */
	public Collection getAllSubSystems() {
		MNamespace model = ProjectBrowser.TheInstance.getProject().getModel();
		return getAllSubSystems(model);
	}
	
	/**
	 * Returns all subsystems found in this namespace and in its children
	 * @return Collection
	 */
	public Collection getAllSubSystems(MNamespace ns) {
		if (ns == null) return new ArrayList();
		Iterator it = ns.getOwnedElements().iterator();
		List list = new ArrayList();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof MNamespace) {
				list.addAll(getAllSubSystems((MNamespace)o));
			} 
			if (o instanceof MSubsystem) {
				list.add(o);
			}
			
		}
		return list;
	}
	
	/**
	 * Returns all namespaces found in the projectbrowser model
	 * @return Collection
	 */
	public Collection getAllNamespaces() {
		MNamespace model = ProjectBrowser.TheInstance.getProject().getModel();
		return getAllNamespaces(model);
	}
	
	/**
	 * Returns all namespaces found in this namespace and in its children
	 * @return Collection
	 */
	public Collection getAllNamespaces(MNamespace ns) {
		if (ns == null) return new ArrayList();
		Iterator it = ns.getOwnedElements().iterator();
		List list = new ArrayList();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof MNamespace) {
				list.add(o);
				list.addAll(getAllNamespaces((MNamespace)o));	
			} 
		}
		return list;
	}
	
	public Collection getAllModelElementsOfKind(Class kind) {
		if (kind == null) return new ArrayList();
		MNamespace model = ProjectBrowser.TheInstance.getProject().getModel();
		return getAllModelElementsOfKind(model, kind);
	}
	
	/**
	 * Returns all modelelements found in this namespace and its children 
	 * that are of some class kind. Does NOT return subtypes of the searched
	 * kind. 
	 * @param ns
	 * @param kind
	 * @return Collection
	 */
	public Collection getAllModelElementsOfKind(MNamespace ns, Class kind) {
		if (ns == null || kind == null) return new ArrayList();
		Iterator it = ns.getOwnedElements().iterator();
		List list = new ArrayList();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof MNamespace) {
				list.addAll(getAllModelElementsOfKind((MNamespace)o, kind));
			} 
			if (isSubtypeFrom(kind, o.getClass())) {
				list.add(o);
			}
		}
		return list;
	}
	
	protected boolean isSubtypeFrom(Class superType, Class subType) {
		if (subType.equals(superType)) return true;
		subType = subType.getSuperclass();
		if (subType.equals(Object.class)) return false;
		return isSubtypeFrom(superType, subType);
	}
}

