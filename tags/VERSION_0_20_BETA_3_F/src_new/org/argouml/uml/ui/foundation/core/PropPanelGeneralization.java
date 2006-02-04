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

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLTextField2;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel for a Generalization.
 */
public class PropPanelGeneralization extends PropPanelModelElement {

    private JTextField discriminatorTextField;

    private JScrollPane parentScroll;

    private JScrollPane childScroll;

    private static UMLDiscriminatorNameDocument discriminatorDocument =
        new UMLDiscriminatorNameDocument();

    private static UMLGeneralizationChildListModel childListModel =
        new UMLGeneralizationChildListModel();

    private static UMLGeneralizationParentListModel parentListModel =
        new UMLGeneralizationParentListModel();

    /**
     * The constructor.
     *
     */
    public PropPanelGeneralization() {
        super("Generalization",
            lookupIcon("Generalization"),
            ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());

        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());

        addField(Translator.localize("label.discriminator"),
                getDiscriminatorTextField());

        addField(Translator.localize("label.namespace"),
                getNamespaceSelector());

        addSeperator();

        addField(Translator.localize("label.parent"),
                getParentScroll());

        addField(Translator.localize("label.child"),
                getChildScroll());

        addField(Translator.localize("label.powertype"),
                new UMLComboBox2(new UMLGeneralizationPowertypeComboBoxModel(),
                        ActionSetGeneralizationPowertype.getInstance()));

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNewStereotype());
        addAction(new ActionDeleteSingleModelElement());
    }

    /**
     * @see org.argouml.uml.ui.foundation.core.PropPanelModelElement#navigateUp()
     */
    public void navigateUp() {
        Object target = getTarget();
        if (Model.getFacade().isAModelElement(target)) {
            Object namespace = Model.getFacade().getNamespace(target);
            if (namespace != null) {
                TargetManager.getInstance().setTarget(namespace);
            }
        }
    }

    /**
     * @return the discriminator textfield
     */
    protected JTextField getDiscriminatorTextField() {
        if (discriminatorTextField == null) {
            discriminatorTextField = new UMLTextField2(discriminatorDocument);
        }
        return discriminatorTextField;
    }

    /**
     * @return the scrollpane for the parent
     */
    protected JScrollPane getParentScroll() {
        if (parentScroll == null) {
            JList list = new UMLLinkedList(parentListModel);
            list.setVisibleRowCount(1);
            parentScroll =
                new JScrollPane(list,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        return parentScroll;
    }

    /**
     * @return the scrollpane for the child
     */
    public JScrollPane getChildScroll() {
        if (childScroll == null) {
            JList list = new UMLLinkedList(childListModel);
            list.setVisibleRowCount(1);
            childScroll =
                new JScrollPane(list,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        return childScroll;
    }

} /* end class PropPanelGeneralization */
