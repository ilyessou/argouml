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

package org.argouml.uml.ui.behavior.activity_graphs;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLComboBoxModel2;

/**
 * A model for the classifier of an ObjectFlowState.
 *
 * @since Oct 10, 2002
 * @author jaap.branderhorst@xs4all.nl, alexb
 */
public class UMLObjectFlowStateClassifierComboBoxModel
    extends UMLComboBoxModel2 {

    /**
     * Constructor.
     * TODO: MVW: I do not understand this! Is it correct?
     */
    public UMLObjectFlowStateClassifierComboBoxModel() {
        super("classifier", false);
        Model.getPump().addClassModelEventListener(this,
                Model.getMetaTypes().getClassifier(), "type");
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object o) {
        return Model.getFacade().isAClassifier(o);
        // && CoreHelper.getHelper().isValidNamespace(
        //      /*(MModelElement)*/ getTarget(), /*(MNamespace)*/ o)
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#buildModelList()
     */
    protected void buildModelList() {
        Object model =
            ProjectManager.getManager().getCurrentProject().getModel();
        setElements(Model.getCoreHelper().getAllClassifiers(model));
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#getSelectedModelElement()
     */
    protected Object getSelectedModelElement() {
        if (getTarget() != null) {
            return Model.getFacade().getType(getTarget());
        }
        return null;
    }

}
