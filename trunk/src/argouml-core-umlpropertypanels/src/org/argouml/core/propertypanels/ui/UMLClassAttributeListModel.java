// $Id: UMLClassAttributeListModel.java 12801 2007-06-09 15:49:57Z mvw $
// Copyright (c) 1996-2006 The Regents of the University of California. All
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

package org.argouml.core.propertypanels.ui;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.model.Model;

/**
 *
 * @author jaap.branderhorst@xs4all.nl, alexb
 * @since Mar 22, 2003
 */
class UMLClassAttributeListModel
        extends UMLModelElementListModel 
        implements Ordered {

    private static final Logger LOG = 
        Logger.getLogger(UMLClassAttributeListModel.class);
    
    /**
     * Constructor for UMLClassifierStructuralFeatureListModel.
     */
    public UMLClassAttributeListModel(Object modelElement) {
        super("feature", true, false, Model.getMetaTypes().getAttribute());
        setTarget(modelElement);
        LOG.info("Constructed UMLClassAttributeListModel");
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        if (getTarget() != null) {
            setAllElements(getModelElements());
        }
    }
    
    public Collection getModelElements() {
        return Model.getFacade().getAttributes(getTarget());
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        return Model.getFacade().isAAttribute(element)
            && (getModelElements().contains(element));
    }
}
