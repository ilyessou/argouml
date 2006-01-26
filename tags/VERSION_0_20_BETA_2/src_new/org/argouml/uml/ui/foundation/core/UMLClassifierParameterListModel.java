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

package org.argouml.uml.ui.foundation.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.argouml.model.Model;
import org.argouml.uml.ui.UMLModelElementOrderedListModel2;

/**
 * This is the model for the list of parameters for a classifier,
 * as e.g. present on the operation properties panel. <p>
 *
 * This is an ordered list, and hence it supports reordering functions.
 *
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 26, 2003
 */
public class UMLClassifierParameterListModel
    extends UMLModelElementOrderedListModel2 {

    /**
     * Constructor for UMLClassifierParameterListModel.
     * This is an ordered list (2nd parameter = true).
     */
    public UMLClassifierParameterListModel() {
        super("parameter");
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        if (getTarget() != null) {
            setAllElements(Model.getFacade().getParameters(getTarget()));
        }
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        return Model.getFacade().getParameters(getTarget()).contains(element);
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementOrderedListModel2#swap(int, int)
     */
    public void swap(int index1, int index2) {
        Object classifier = getTarget();
        List c = new ArrayList(Model.getFacade().getParameters(classifier));
        // TODO: Verify that the following works now with MDR
        // and replace code - tfm - 20051109

        /* The following does not work, because NSUML does not
         * fire an update event, since no parameters were added or removed...
        Collections.swap(c, index1, index2);
        Model.getFacade().setParameters(classifier, c);
        ... So, lets delete them first, then add them in reverse: */
        Object mem1 = c.get(index1);
        Object mem2 = c.get(index2);
        List cc = new ArrayList(c);
        cc.remove(mem1);
        cc.remove(mem2);
        Model.getCoreHelper().setParameters(classifier, cc);
        Collections.swap(c, index1, index2);
        Model.getCoreHelper().setParameters(classifier, c);
        buildModelList();
    }

}
