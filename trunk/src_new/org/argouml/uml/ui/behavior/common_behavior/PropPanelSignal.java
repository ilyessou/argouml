// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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
import java.util.List;

import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractActionAddModelElement2;
import org.argouml.uml.ui.AbstractActionRemoveElement;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.UMLModelElementListModel2;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelClassifier;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;

/**
 * The properties panel of a Signal.
 * <p>
 * 
 * NOTE: Although the UML 1.4 spec (both sec. 2.9.2.20 and 3.77.2) says that
 * "parameters are specified as Attributes" the WFR in sect 2.9.3.20 is
 * <code>self.contents->isEmpty</code>, effectively prohibiting this.
 */
public class PropPanelSignal extends PropPanelClassifier {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = -4496838172438164508L;

    /**
     * Construct a default property panel for a Signal.
     */
    public PropPanelSignal() {
        this("label.signal-title", "SignalSending");
    }
    
    /**
     * Construct a new property panel for a Signal with the given name and icon.
     * Use for subclasses that want the same layout/constructor, but a different
     * name e.g. Exception.
     * 
     * @param title
     *            title of the property panel
     * @param iconName
     *            name of the image icon to use
     */
    public PropPanelSignal(String title, String iconName) {
        super(title, lookupIcon(iconName));

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.namespace"),
                getNamespaceSelector());
        add(getModifiersPanel());
        add(getNamespaceVisibilityPanel());
        
        addSeparator();
        
        addField(Translator.localize("label.generalizations"),
				getGeneralizationScroll());
        addField(Translator.localize("label.specializations"),
				getSpecializationScroll());
		
        addSeparator();
		
        AbstractActionAddModelElement2 actionAddContext =
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
        AbstractActionAddModelElement2 actionAddReception =
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
        addAction(getDeleteAction());
    }


} /* end class PropPanelSignal */

/**
 * The model for the listbox showing the receptions of a signal.
 * 
 * @author Michiel
 */
class UMLSignalReceptionListModel extends UMLModelElementListModel2 {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = 3273212639257377015L;

    /**
     * Construct a list model showing the receptions of a signal.
     */
    public UMLSignalReceptionListModel() {
        /*
         * The event to listen to is "reception", so that model updates
         * get shown in the list. Reproduce this by adding a new reception,
         * and see the result displayed in the list.
         */
        super("reception");
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        if (getTarget() != null) {
            setAllElements(Model.getFacade().getReceptions(getTarget()));
        }
    }

    /*
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
class ActionAddReceptionSignal extends AbstractActionAddModelElement2 {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = -2854099588590429237L;

    /**
     * Construct an Action which adds a Reception to a Signal.
     */
    public ActionAddReceptionSignal() {
        super();
    }


    protected List getChoices() {
        List ret = new ArrayList();
        Object model =
            ProjectManager.getManager().getCurrentProject().getModel();
        if (getTarget() != null) {
            ret.addAll(Model.getModelManagementHelper()
                .getAllModelElementsOfKind(model, 
                    Model.getMetaTypes().getReception()));
        }
        return ret;
    }


    protected List getSelected() {
        List ret = new ArrayList();
        ret.addAll(Model.getFacade().getReceptions(getTarget()));
        return ret;
    }


    protected String getDialogTitle() {
        return Translator.localize("dialog.title.add-receptions");
    }


    @Override
    protected void doIt(Collection selected) {
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
     * The serial version.
     */
    private static final long serialVersionUID = -3345844954130000669L;

    /**
     * Construct an Action which removes a Context from a Signal.
     */
    public ActionRemoveContextSignal() {
        super(Translator.localize("menu.popup.remove"));
    }

    /*
     * @see org.tigris.gef.undo.UndoableAction#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object context = getObjectToRemove(); 
        if (context != null) {
            Object signal = getTarget();
            if (Model.getFacade().isASignal(signal)) {
                Model.getCommonBehaviorHelper().removeContext(signal, context);
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
     * The serial version.
     */
    private static final long serialVersionUID = -2630315087703962883L;

    /**
     * Construct an Action which removes a Reception from a Signal.
     */
    public ActionRemoveReceptionSignal() {
        super(Translator.localize("menu.popup.remove"));
    }

    /*
     * @see org.tigris.gef.undo.UndoableAction#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object reception = getObjectToRemove(); 
        if (reception != null) {
            Object signal = getTarget();
            if (Model.getFacade().isASignal(signal)) {
                // TODO: Should we delete the Reception?  A Reception
                // without a Signal violates the cardinality of 1 in
                // the metamodel - tfm - 20070308
                Model.getCommonBehaviorHelper().removeReception(signal, 
                        reception);
            }
        }
    }

}
