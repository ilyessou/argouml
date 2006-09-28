// $Id$
// Copyright (c) 2003-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

import javax.swing.ImageIcon;
import javax.swing.JComboBox;

import org.argouml.i18n.Translator;
import org.tigris.swidgets.Orientation;
import org.argouml.uml.ui.UMLSearchableComboBox;
import org.argouml.uml.ui.behavior.state_machines.AbstractPropPanelState;
import org.argouml.util.ConfigLoader;

/**
 *
 * @author mkl
 *
 */
public class PropPanelObjectFlowState extends AbstractPropPanelState {

    private JComboBox classifierComboBox;

    private UMLObjectFlowStateClassifierComboBoxModel classifierComboBoxModel =
        new UMLObjectFlowStateClassifierComboBoxModel();

    /**
     * Constructor
     */
    public PropPanelObjectFlowState() {
        this("ObjectFlowState", lookupIcon("ObjectFlowState"), ConfigLoader
                .getTabPropsOrientation());
    }

    /**
     * Constructor
     *
     * @param name the name of the properties panel, shown at the top
     * @param icon the icon shown at the top
     * @param orientation the orientation
     */
    public PropPanelObjectFlowState(String name, ImageIcon icon,
            Orientation orientation) {
        super(name, icon, ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());
        addField(Translator.localize("label.container"),
                getContainerScroll());

        // field for Classifier(InState)
        addField(Translator.localize("label.type"),
                getClassifierComboBox());

        //TODO: Add field for State

        addSeperator();

        addField(Translator.localize("label.incoming"),
                getIncomingScroll());
        addField(Translator.localize("label.outgoing"),
                getOutgoingScroll());

    }

    /**
     * @return the combo box for the type (Classifier or ClassifierInState)
     */
    protected JComboBox getClassifierComboBox() {
        if (classifierComboBox == null) {
            classifierComboBox = new UMLSearchableComboBox(
                    classifierComboBoxModel,
                    ActionSetObjectFlowStateClassifier.SINGLETON, true);
        }
        return classifierComboBox;

    }
}
