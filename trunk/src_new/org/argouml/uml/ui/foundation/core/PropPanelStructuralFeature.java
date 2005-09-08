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

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.UMLCheckBox2;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLMultiplicityComboBox2;
import org.argouml.uml.ui.UMLRadioButtonPanel;
import org.tigris.swidgets.Orientation;

/**
 * @since Nov 6, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class PropPanelStructuralFeature extends PropPanelFeature {

    private UMLComboBox2 multiplicityComboBox;
    private UMLComboBox2 typeComboBox;
    private UMLRadioButtonPanel changeabilityRadioButtonPanel;
    private UMLCheckBox2 targetScopeCheckBox;

    private static UMLStructuralFeatureMultiplicityComboBoxModel
        multiplicityComboBoxModel;
    private static UMLStructuralFeatureTypeComboBoxModel typeComboBoxModel;

    /**
     * Constructor for PropPanelStructuralFeature.
     * @param name the name of the panel, to be shown at the top
     * @param orientation the orientation of the panel
     */
    protected PropPanelStructuralFeature(String name,
					 Orientation orientation) {
	super(name, orientation);

    }

    /**
     * Returns the multiplicityComboBox.
     * @return UMLMultiplicityComboBox2
     */
    public UMLComboBox2 getMultiplicityComboBox() {
	if (multiplicityComboBox == null) {
	    if (multiplicityComboBoxModel == null) {
		multiplicityComboBoxModel =
		    new UMLStructuralFeatureMultiplicityComboBoxModel();
	    }
	    multiplicityComboBox =
		new UMLMultiplicityComboBox2(multiplicityComboBoxModel,
		    ActionSetStructuralFeatureMultiplicity.getInstance());
	    multiplicityComboBox.setEditable(true);
	}
	return multiplicityComboBox;
    }

    /**
     * Returns the typeComboBox.
     * @return UMLComboBox2
     */
    public UMLComboBox2 getTypeComboBox() {
        if (typeComboBox == null) {
	    if (typeComboBoxModel == null) {
		typeComboBoxModel =
		    new UMLStructuralFeatureTypeComboBoxModel();
	    }
            typeComboBox =
		new UMLComboBox2(
				 typeComboBoxModel,
				 ActionSetStructuralFeatureType.getInstance());
	}
	return typeComboBox;
    }

    /**
     * Returns the changeabilityRadioButtonPanel.
     * @return UMLRadioButtonPanel
     */
    public UMLRadioButtonPanel getChangeabilityRadioButtonPanel() {
        if (changeabilityRadioButtonPanel == null) {
            changeabilityRadioButtonPanel =
                new UMLStructuralFeatureChangeabilityRadioButtonPanel(
                        Translator.localize("label.changeability"),
                        true);
        }
	return changeabilityRadioButtonPanel;
    }

    /**
     * Returns the targetScopeCheckBox.
     * @return UMLCheckBox2
     */
    public UMLCheckBox2 getTargetScopeCheckBox() {
        if (targetScopeCheckBox == null) {
	    targetScopeCheckBox = new UMLStructuralFeatureTargetScopeCheckBox();
        }
        return targetScopeCheckBox;
    }

}
