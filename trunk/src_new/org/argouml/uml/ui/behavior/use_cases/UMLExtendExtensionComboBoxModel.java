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

package org.argouml.uml.ui.behavior.use_cases;

import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.uml.ui.UMLComboBoxModel2;

/**
 * @since Oct 6, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class UMLExtendExtensionComboBoxModel extends UMLComboBoxModel2 {

    /**
     * Constructor for UMLExtendExtensionComboBoxModel.
     */
    public UMLExtendExtensionComboBoxModel() {
        super("extension", false);

    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#buildModelList()
     */
    protected void buildModelList() {
        Object extend = /*(MExtend)*/ getTarget();
        if (extend == null) {
            return;
        }
        Object ns = ModelFacade.getNamespace(extend);
        addAll(Model.getModelManagementHelper().getAllModelElementsOfKind(
                ns, ModelFacade.getUseCaseToken()));
        if (ModelFacade.getBase(extend) != null) {
            removeElement(ModelFacade.getBase(extend));
        }
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#getSelectedModelElement()
     */
    protected Object getSelectedModelElement() {
        if (getTarget() != null) {
            return ModelFacade.getExtension(getTarget());
        }
        return null;
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        return org.argouml.model.ModelFacade.isAUseCase(element);
    }



    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetRemoved(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // if (e.getNewTarget() != getTarget())
        UmlModelEventPump.getPump().removeClassModelEventListener(this,
                    ModelFacade.getNamespaceToken(), "ownedElement");
        super.targetRemoved(e);
    }
    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        UmlModelEventPump.getPump().addClassModelEventListener(this,
                ModelFacade.getNamespaceToken(), "ownedElement");
        super.targetSet(e);
    }
}
