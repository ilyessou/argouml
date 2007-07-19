// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.collaborations;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLComboBoxModel2;

/**
 * The ComboBox model for the represented Operation 
 * of a Collaboration.
 * 
 * @author michiel
 */
class UMLCollaborationRepresentedOperationComboBoxModel
    extends  UMLComboBoxModel2  {
    
    /**
     * Constructor for UMLCollaborationRepresentedOperationComboBoxModel.
     */
    public UMLCollaborationRepresentedOperationComboBoxModel() {
        super("representedOperation", true);
    }
    
    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        Collection operations = new ArrayList();
        Project p = ProjectManager.getManager().getCurrentProject();
        for (Object model : p.getUserDefinedModelList()) {
            Collection c = Model.getModelManagementHelper()
                .getAllModelElementsOfKind(model, 
                    Model.getMetaTypes().getOperation());
            for (Object oper : c) {
                Object ns = Model.getFacade().getOwner(oper);
                Collection s = Model.getModelManagementHelper()
                    .getAllSurroundingNamespaces(ns);
                if (!s.contains(getTarget())) operations.add(oper);
            }
        }
        setElements(operations);
    }
    
    /*
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        return Model.getFacade().isAOperation(element)
            && Model.getFacade().getRepresentedOperation(getTarget()) 
                == element;
    }
    
    protected Object getSelectedModelElement() {
        return Model.getFacade().getRepresentedOperation(getTarget());
    }
    
    /*
     * @see org.argouml.uml.ui.UMLComboBoxModel2#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /* Do nothing by design. */
    }
}
