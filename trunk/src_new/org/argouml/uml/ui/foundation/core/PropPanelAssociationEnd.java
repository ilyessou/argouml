// $Id$
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.swingext.GridLayout2;

import org.argouml.swingext.Orientation;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLMultiplicityComboBox;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.util.ConfigLoader;

/**
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 */
public class PropPanelAssociationEnd extends PropPanelModelElement {

    protected JLabel associationsLabel;

    /**
     * The combobox that shows the type of this association end.
     */
    protected JComboBox _typeCombobox;

    /**
     * The scrollpane showing the association that owns this associationend
     */
    protected JScrollPane _associationScroll;

    /**
     * The combobox for the multiplicity of this type.
     * TODO: should be changed into a textfield so the user can edit it more
     * easily.
     */
    protected JComboBox _multiplicityComboBox;

    /**
     * The checkbox that shows if this association end is navigable.
     */
    protected JCheckBox _navigabilityCheckBox;

    /**
     * The checkbox that shows the ordering of the associationend. It's selected
     * if it's an ordered associationend. Unselected if it's unordered. Sorted
     * is not supported atm.
     */
    protected JCheckBox _orderingCheckBox;

    /**
     * The checkbox that shows the scope of the associationend. Selected means
     * that the scope is the classifier. Unselected means that the scope is the
     * instance (the default).
     */
    protected JCheckBox _targetScopeCheckBox;

    /**
     * The panel with the radiobuttons the user can select to select the
     * aggregation of this associationend.
     */
    protected JPanel _aggregationRadioButtonpanel;

    /**
     * The panel with the radiobuttons the user can select to select the
     * changeability of this associationend.
     */
    protected JPanel _changeabilityRadioButtonpanel;

    /**
     * The panel with the radiobuttons to set the visibility (public, protected,
     * private) of this associationend. There is a bug (or inconsistency) with
     * NSUML since NSUML equals this visibility with the visibility of a
     * modelelement. The UML 1.3 spec does not. Since I try to follow the spec
     * as much as possible, the panel is defined here and not in
     * PropPanelModelElement
     */
    protected JPanel _visibilityRadioButtonPanel;

    /**
     * The list of classifiers that specify the operations that must be
     * implemented by the classifier type. These operations can be used by this
     * association.
     */
    protected JScrollPane _specificationScroll;

    /**
     * Constructs the proppanel including initializing all scrollpanes, panels
     * etc. but excluding placing them on the proppanel itself.
     * @see org.argouml.uml.ui.PropPanel#PropPanel(String, Orientation)
     */
    protected PropPanelAssociationEnd(String name, Orientation orientation) {
        super(name, orientation);
    }

    private String _associationLabel;

    /**
     * Constructs the proppanel and places all scrollpanes etc. on the canvas.
     * @see java.lang.Object#Object()
     */
    public PropPanelAssociationEnd() {
        super("AssociationEnd", ConfigLoader.getTabPropsOrientation());
        _associationLabel = Translator.localize("UMLMenu", "label.association");
        Class mclass = (Class)ModelFacade.ASSOCIATION_END;
        createControls(mclass);
        positionStandardControls();
        positionControls();
    }

    protected void createControls(Class mclass) {
        _typeCombobox = new UMLComboBox2(new UMLAssociationEndTypeComboBoxModel(), ActionSetAssociationEndType.SINGLETON, true);
        _multiplicityComboBox = new UMLMultiplicityComboBox(this, mclass);
        JList associationList = new UMLLinkedList(new UMLAssociationEndAssociationListModel());
        associationList.setVisibleRowCount(1);
        _associationScroll = new JScrollPane(associationList);
        _navigabilityCheckBox = new UMLAssociationEndNavigableCheckBox();
        _orderingCheckBox = new UMLAssociationEndOrderingCheckBox();
        _targetScopeCheckBox = new UMLAssociationEndTargetScopeCheckbox();
        _aggregationRadioButtonpanel = new UMLAssociationEndAggregationRadioButtonPanel(Translator.localize("UMLMenu", "label.aggregation"), true);
        _changeabilityRadioButtonpanel = new UMLAssociationEndChangeabilityRadioButtonPanel(Translator.localize("UMLMenu", "label.changeability"), true);
        _visibilityRadioButtonPanel = new UMLModelElementVisibilityRadioButtonPanel(Translator.localize("UMLMenu", "label.visibility"), true);
        _specificationScroll = new JScrollPane(new UMLMutableLinkedList(new UMLAssociationEndSpecificationListModel(), ActionAddAssociationSpecification.SINGLETON, null, null, true));
    }

    protected void positionStandardControls() {
        addField(Translator.localize("UMLMenu", "label.name"), getNameTextField());
        addField(Translator.localize("UMLMenu", "label.stereotype"), new UMLComboBoxNavigator(this, Translator.localize("UMLMenu", "tooltip.nav-stereo"), getStereotypeBox()));
    }

    protected void positionControls() {
        addField(_associationLabel, _associationScroll);
        addField(Translator.localize("UMLMenu", "label.type"), _typeCombobox);
        addField(Translator.localize("UMLMenu", "label.multiplicity"), _multiplicityComboBox);

        addSeperator();

        JPanel panel = new JPanel(new GridLayout2());
        panel.add(_navigabilityCheckBox);
        panel.add(_orderingCheckBox);
        panel.add(_targetScopeCheckBox);
        panel.setBorder(new TitledBorder(Translator.localize("UMLMenu", "label.modifiers")));
        panel.setVisible(true);
        add(panel);
        addField(Translator.localize("UMLMenu", "label.specification"), _specificationScroll);

        addSeperator();

        add(_aggregationRadioButtonpanel);
        add(_changeabilityRadioButtonpanel);
        add(_visibilityRadioButtonPanel);

        new PropPanelButton(this, buttonPanel, _navUpIcon, Translator.localize("UMLMenu", "button.go-up"), "navigateUp", null);
        //does this make sense?? new PropPanelButton(this,buttonPanel,_interfaceIcon, Translator.localize("UMLMenu", "button.add-new-interface"),"newInterface",null);
        new PropPanelButton(this, buttonPanel, _assocEndIcon, localize("Go to other end"), "gotoOther", null);
        new PropPanelButton(this, buttonPanel, _deleteIcon, Translator.localize("UMLMenu", "button.delete-association-end"), "removeElement", "isDeleteEnabled");
    }

    protected void setAssociationLabel(String label) {
    }

    /**
     * Happens when the user presses the up button. In this case, ArgoUML navigates
     * to the association that owns this associationend.
     * @see org.argouml.uml.ui.foundation.core.PropPanelModelElement#navigateUp()
     */
    public void navigateUp() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAAssociationEnd(target)) {
            Object assoc = ModelFacade.getAssociation(target);
            if (assoc != null) {
                TargetManager.getInstance().setTarget(assoc);
            }
        }
    }

    /**
     * Action behind pressing the button go to other.
     * TODO: as soon as we don't support JDK 1.2 any more, drop this method and
     * replace it with an action.
     */
    public void gotoOther() {
        Object target = getTarget();
        if (ModelFacade.isAAssociationEnd(target)) {
            Object end = /*(MAssociationEnd)*/ target;
            TargetManager.getInstance().setTarget(ModelFacade.getOppositeEnd(end));
        }
    }

    /**
     * Checks if the delete button of the associationend panel should be
     * enabled. It should be disabled if there are two or less association ends.
     * @return boolean
     */
    public boolean isDeleteEnabled() {
        if (ModelFacade.isAAssociationEnd(getTarget())) {
          return ModelFacade.getOtherAssociationEnds(getTarget()).size() > 1;
        }
        return false;
    }


} /* end class PropPanelAssociationEnd */