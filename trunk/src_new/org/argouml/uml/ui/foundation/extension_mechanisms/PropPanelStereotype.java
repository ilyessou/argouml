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

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.uml.ui.foundation.core.UMLGeneralizableElementAbstractCheckBox;
import org.argouml.uml.ui.foundation.core.UMLGeneralizableElementGeneralizationListModel;
import org.argouml.uml.ui.foundation.core.UMLGeneralizableElementLeafCheckBox;
import org.argouml.uml.ui.foundation.core.UMLGeneralizableElementRootCheckBox;
import org.argouml.uml.ui.foundation.core.UMLGeneralizableElementSpecializationListModel;
import org.argouml.util.ConfigLoader;
import org.tigris.swidgets.FlexiGridLayout;

/**
 * The properties panel for a Stereotype.
 */
public class PropPanelStereotype extends PropPanelModelElement {

    private static UMLGeneralizableElementSpecializationListModel
    specializationListModel =
        new UMLGeneralizableElementSpecializationListModel();

    private static UMLGeneralizableElementGeneralizationListModel
        generalizationListModel =
            new UMLGeneralizableElementGeneralizationListModel();

    private static UMLTagDefinitionListModel
    tagDefinitionListModel =
        new UMLTagDefinitionListModel();

    private JScrollPane generalizationScroll;

    private JScrollPane specializationScroll;

    private JScrollPane tagDefinitionScroll;

    /**
     * Construct new stereotype properties tab
     */
    public PropPanelStereotype() {
        super("Stereotype", lookupIcon("Stereotype"),
                ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"), getNameTextField());


        JComboBox baseClass = new UMLComboBox2(new UMLMetaClassComboBoxModel(),
                ActionSetMetaClass.SINGLETON, false);
        addField(Translator.localize("label.base-class"), baseClass);

        addField(Translator.localize("label.namespace"),
                 getNamespaceSelector());


        JPanel modifiersPanel = new JPanel(new FlexiGridLayout(0, 3,
                FlexiGridLayout.ROWCOLPREFERRED));
        modifiersPanel.setBorder(new TitledBorder(
                Translator.localize("label.modifiers")));
        modifiersPanel.add(new UMLGeneralizableElementAbstractCheckBox());
        modifiersPanel.add(new UMLGeneralizableElementLeafCheckBox());
        modifiersPanel.add(new UMLGeneralizableElementRootCheckBox());
        add(modifiersPanel);

        addSeperator();

        addField(Translator.localize("label.generalizations"),
                getGeneralizationScroll());
        addField(Translator.localize("label.specializations"),
                getSpecializationScroll());
        addField(Translator.localize("label.tagdefinitions"),
                getTagDefinitionScroll());
        addAction(new ActionNavigateNamespace());
        addAction(new ActionNewStereotype());
        addAction(new ActionNewTagDefinition());
        addAction(new ActionDeleteSingleModelElement());
    }

    /**
     * Returns the generalizationScroll.
     *
     * @return JScrollPane
     */

    protected JScrollPane getGeneralizationScroll() {
        if (generalizationScroll == null) {
            JList list = new UMLLinkedList(generalizationListModel);
            generalizationScroll = new JScrollPane(list);
        }
        return generalizationScroll;
    }

    /**
     * Returns the specializationScroll.
     *
     * @return JScrollPane
     */
    protected JScrollPane getSpecializationScroll() {
        if (specializationScroll == null) {
            JList list = new UMLLinkedList(specializationListModel);
            specializationScroll = new JScrollPane(list);
        }
        return specializationScroll;
    }

    /**
     * Returns the tagDefinitionScroll.
     *
     * @return JScrollPane
     */
    protected JScrollPane getTagDefinitionScroll() {
        if (tagDefinitionScroll == null) {
            JList list = new UMLLinkedList(tagDefinitionListModel);
            tagDefinitionScroll = new JScrollPane(list);
        }
        return tagDefinitionScroll;
    }

} /* end class PropPanelStereotype */
