// $Id$
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

package org.argouml.uml.ui.behavior.common_behavior;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractActionAddModelElement;
import org.argouml.uml.ui.AbstractActionRemoveElement;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.UMLModelElementListModel2;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel of a Signal.
 *
 */
public class PropPanelSignal extends PropPanelModelElement {

    /**
     * The constructor.
     */
    public PropPanelSignal() {
        super("Signal", lookupIcon("SignalSending"),
                ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());
        addField(Translator.localize("label.namespace"),
                getNamespaceSelector());

        AbstractActionAddModelElement actionAddContext =
            new ActionAddContextSignal();
        AbstractActionRemoveElement actionRemoveContext =
            new ActionRemoveContextSignal();
        JScrollPane operationScroll = new JScrollPane(
                new UMLMutableLinkedList(
                        new UMLSignalContextListModel(),
                        actionAddContext, null, 
                        actionRemoveContext, true));
        addField(Translator.localize("label.contexts"),
                operationScroll);

        addSeperator();

        AbstractActionAddModelElement actionAddReception =
            new ActionAddReceptionSignal();
        AbstractActionRemoveElement actionRemoveReception =
            new ActionRemoveReceptionSignal();
        JScrollPane receptionScroll = new JScrollPane(
                new UMLMutableLinkedList(
                        new UMLSignalReceptionListModel(),
                        actionAddReception, null, 
                        actionRemoveReception, true));
        addField(Translator.localize("label.receptions"),
                receptionScroll);

        addAction(new ActionNavigateNamespace());
        addAction(new ActionNewSignal());
        addAction(new ActionNewStereotype());
        addAction(new ActionDeleteSingleModelElement());
    }


} /* end class PropPanelSignal */

/**
 * The model for the listbox showing the receptions of a signal.
 * 
 * @author Michiel
 */
class UMLSignalReceptionListModel extends UMLModelElementListModel2 {

    /**
     * The constructor.
     */
    public UMLSignalReceptionListModel() {
        /*
         * The event to listen to is "reception", so that model updates
         * get shown in the list. Reproduce this by adding a new reception,
         * and see the result displayed in the list.
         */
        super("reception");
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        if (getTarget() != null) {
            setAllElements(Model.getFacade().getReceptions(getTarget()));
        }
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(java.lang.Object)
     */
    protected boolean isValidElement(Object element) {
        return Model.getFacade().isAReception(element)
            && Model.getFacade().getReceptions(getTarget()).contains(element);
    }

}


/**
 * This Action adds a Reception to a Signal.
 * 
 * @author Michiel
 */
class ActionAddReceptionSignal extends AbstractActionAddModelElement {

    /**
     * The constructor.
     */
    public ActionAddReceptionSignal() {
        super();
    }

    /**
     * @see org.argouml.uml.ui.AbstractActionAddModelElement#getChoices()
     */
    protected Vector getChoices() {
        Vector ret = new Vector();
        Object model =
            ProjectManager.getManager().getCurrentProject().getModel();
        if (getTarget() != null) {
            ret.addAll(Model.getModelManagementHelper()
                .getAllModelElementsOfKind(model, 
                    Model.getMetaTypes().getReception()));
        }
        return ret;
    }

    /**
     * @see org.argouml.uml.ui.AbstractActionAddModelElement#getSelected()
     */
    protected Vector getSelected() {
        Vector ret = new Vector();
        ret.addAll(Model.getFacade().getReceptions(getTarget()));
        return ret;
    }

    /**
     * @see org.argouml.uml.ui.AbstractActionAddModelElement#getDialogTitle()
     */
    protected String getDialogTitle() {
        return Translator.localize("dialog.title.add-receptions");
    }

    /**
     * @see org.argouml.uml.ui.AbstractActionAddModelElement#doIt(java.util.Vector)
     */
    protected void doIt(Vector selected) {
        Model.getCommonBehaviorHelper().setReception(getTarget(), selected);
    }

}

/**
 * This Action removes a Context from a Signal.
 * 
 * @author Michiel
 */
class ActionRemoveContextSignal extends AbstractActionRemoveElement {

    /**
     * The constructor.
     */
    public ActionRemoveContextSignal() {
        super(Translator.localize("menu.popup.remove"));
    }

    /**
     * @see org.tigris.gef.undo.UndoableAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object ctxt = getObjectToRemove(); 
        if (ctxt != null) {
            Object signal = getTarget();
            if (Model.getFacade().isASignal(signal)) {
                Collection contexts = new ArrayList(
                        Model.getFacade().getContexts(signal));
                contexts.remove(ctxt);
                Model.getCommonBehaviorHelper().setContexts(signal, contexts);
            }
        }
    }

}

/**
 * This Action removes a Reception from a Signal.
 * 
 * @author Michiel
 */
class ActionRemoveReceptionSignal extends AbstractActionRemoveElement {

    /**
     * The constructor.
     */
    public ActionRemoveReceptionSignal() {
        super(Translator.localize("menu.popup.remove"));
    }

    /**
     * @see org.tigris.gef.undo.UndoableAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object rec = getObjectToRemove(); 
        if (rec != null) {
            Object signal = getTarget();
            if (Model.getFacade().isASignal(signal)) {
                Collection receptions = new ArrayList(
                        Model.getFacade().getReceptions(signal));
                receptions.remove(rec);
                Model.getCommonBehaviorHelper().setReception(signal, receptions);
            }
        }
    }

}
