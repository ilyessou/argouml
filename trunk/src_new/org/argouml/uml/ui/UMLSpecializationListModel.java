// $Id$
// Copyright (c) 1996-2001 The Regents of the University of California. All
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

// 25 Mar 2002: Jeremy Bennett (mail@jeremybennett.com). Tidied up layout, to
// facilitate comparison with UMLGeneralizationListModel.java. Made
// getSpecializations public. Simplified getModelElement to remove duplicated
// checking.

// 3 May 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to mark the
// project as needing saving if a generalization is added, deleted, changed or
// moved.


package org.argouml.uml.ui;

import java.util.*;

import org.tigris.gef.graph.MutableGraphModel;
import org.argouml.application.api.Argo;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.foundation.core.CoreFactory;
import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;

/**
 * <p>A concrete class to provide the list of model elements that are
 *   specializations of some other element.</p>
 *
 * <p>This list should support the full set of "Open", "Add", "Delete", "Move
 *   Up" and "Move Down" in its context sensitive menu.</p>
 *
 * <p>Where there is no entry, the default text is "null".</p>
 *
 * @deprecated as of ArgoUml 0.13.5 (10-may-2003),
 *             replaced by {@link org.argouml.uml.ui.foundation.core.UMLGeneralizableElementSpecializationListModel},
 *             this class is part of the 'old'(pre 0.13.*) implementation of proppanels
 *             that used reflection a lot.
 */

public class UMLSpecializationListModel extends UMLBinaryRelationListModel {

    /**
     * Constructor for UMLSpecializationListModel.
     * @param container
     * @param property
     * @param showNone
     */
    public UMLSpecializationListModel(
				      UMLUserInterfaceContainer container,
				      String property,
				      boolean showNone) {
	super(container, property, showNone);
    }

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#build(MModelElement, MModelElement)
     */
    protected void build(Object/*MModelElement*/ from, Object/*MModelElement*/ to) {
	CoreFactory.getFactory().buildGeneralization(to, from);
    }

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#connect(MutableGraphModel, MModelElement, MModelElement)
     */
    protected void connect(
			   MutableGraphModel gm,
			   Object/*MModelElement*/ from,
			   Object/*MModelElement*/ to) {
	gm.connect(to, from, (Class)ModelFacade.GENERALIZATION);
    }
	

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#getAddDialogTitle()
     */
    protected String getAddDialogTitle() {
	return Argo.localize("UMLMenu", "dialog.title.add-specializations");
    }

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#getChoices()
     */
    protected Collection getChoices() {
	if (org.argouml.model.ModelFacade.isAGeneralizableElement(getTarget())) {
	    Object/*MGeneralizableElement*/ target = getTarget();
	    if (ModelFacade.isLeaf(target)) return new ArrayList();
	    Collection genElem = ModelManagementHelper.getHelper().getAllModelElementsOfKind(getTarget().getClass());
	    List list = new ArrayList();
	    Iterator it = genElem.iterator();
	    while (it.hasNext()) {
		Object/*MGeneralizableElement*/ elem = it.next();
		if (elem == target || !ModelFacade.isRoot(elem)) {
		    list.add(elem);
		}
	    }
	    return list;
	} else 
	    throw new IllegalStateException("In getChoices: target not instanceof MGeneralizableElement");
    }

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#getRelation(MModelElement, MModelElement)
     */
    protected Object getRelation(Object from, Object to) {
	return CoreHelper.getHelper().getGeneralization(to, from);
    }

    /**
     * @see org.argouml.uml.ui.UMLBinaryRelationListModel#getSelected()
     */
    protected Collection getSelected() {
	Object/*MGeneralizableElement*/ target = getTarget();
	return CoreHelper.getHelper().getExtendingElements(target);
    }

} /* End of class UMLSpecializationListModel */