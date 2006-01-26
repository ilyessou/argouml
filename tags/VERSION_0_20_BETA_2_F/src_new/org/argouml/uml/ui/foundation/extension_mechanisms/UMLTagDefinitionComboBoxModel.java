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

package org.argouml.uml.ui.foundation.extension_mechanisms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLComboBoxModel2;

/**
 * A model for tagdefinitions.
 * @author lmaitre
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 5, 2003
 */
public class UMLTagDefinitionComboBoxModel  extends UMLComboBoxModel2 {

    private Logger LOG = Logger.getLogger(UMLTagDefinitionComboBoxModel.class);

    /**
     * Constructor for UMLTagDefinitionComboBoxModel.
     */
    public UMLTagDefinitionComboBoxModel() {
        super("tagdefinition", false);
    }


    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        Object owner = Model.getFacade().getOwner(element);
        return (Model.getFacade().isATagDefinition(element)
                && (owner == null || Model
                .getFacade().getStereotypes(getTarget()).contains(owner)));
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object o) {
        setFireListEvents(false);
        super.setSelectedItem(o);
        setFireListEvents(true);
    }


    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#buildModelList()
     */
    protected void buildModelList() {
        removeAllElements();
        Object t = getTarget();
        addAll(getApplicableTagDefinitions(t));
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#getSelectedModelElement()
     */
    protected Object getSelectedModelElement() {
        return getSelectedItem();
    }

    private Collection getApplicableTagDefinitions(Object t) {
        Set paths = new HashSet();
        Set availableTagDefs = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                try {
                    String name1 = Model.getFacade().getName(o1);
                    String name2 = Model.getFacade().getName(o2);
                    name1 = (name1 != null ? name1 : "");
                    name2 = (name2 != null ? name2 : "");

                    return name1.compareTo(name2);
                } catch (Exception e) {
                    throw new ClassCastException(e.getMessage());
                }
            }
        });
        Collection stereotypes = Model.getFacade().getStereotypes(t);
        Project project = ProjectManager.getManager().getCurrentProject();
        Collection models = project.getModels();
        Iterator it = models.iterator();
        while (it.hasNext()) {
            addAllUniqueModelElementsFrom(availableTagDefs, paths,
                    Model.getModelManagementHelper().getAllModelElementsOfKind(
                            it.next(), Model.getMetaTypes().getTagDefinition()));
        }
        addAllUniqueModelElementsFrom(availableTagDefs, paths,
                Model.getModelManagementHelper().getAllModelElementsOfKind(
                        project.getDefaultModel(),
                        Model.getMetaTypes().getTagDefinition()));
        ArrayList notValids = new ArrayList();
        it = availableTagDefs.iterator();
        while (it.hasNext()) {
            Object tagDef = it.next();
            Object owner = Model.getFacade().getOwner(tagDef);
            if (owner != null && !stereotypes.contains(owner))
                notValids.add(tagDef);
        }
        availableTagDefs.removeAll(notValids);
        return availableTagDefs;
    }

    /**
     * Helper method for buildModelList.
     * <p>
     * Adds those elements from source that do not have the same path as any
     * path in paths to elements, and its path to paths. Thus elements will
     * never contain two objects with the same path, unless they are added by
     * other means.
     */
    private static void addAllUniqueModelElementsFrom(Set elements, Set paths,
            Collection source) {
        Iterator it2 = source.iterator();

        while (it2.hasNext()) {
            Object obj = it2.next();
            Object path = Model.getModelManagementHelper().getPath(obj);
            if (!paths.contains(path)) {
                paths.add(path);
                elements.add(obj);
            }
        }
    }

}
