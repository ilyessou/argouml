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

package org.argouml.uml.ui.behavior.state_machines;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.util.ConfigLoader;
import org.tigris.swidgets.Orientation;

/**
 * @since Dec 15, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class PropPanelSubmachineState extends PropPanelCompositeState {

    /**
     * @param name the name of the properties panel
     * @param icon the icon to be shown next to the name
     * @param orientation the orientation of the panel
     */
    public PropPanelSubmachineState(String name, ImageIcon icon,
            Orientation orientation) {
        super(name, icon, orientation);
        initialize();
    }

    /**
     * Constructor for PropPanelSubmachineState.
     */
    public PropPanelSubmachineState() {
        super("Submachine State", lookupIcon("SubmachineState"),
                ConfigLoader.getTabPropsOrientation());
        getTitleLabel().setText("Submachine State");
        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());
        addField(Translator.localize("label.container"),
                getContainerScroll());
        JComboBox submachineBox = new UMLComboBox2(
                new UMLSubmachineStateComboBoxModel(),
                ActionSetSubmachineStateSubmachine.getInstance());
        addField(Translator.localize("label.submachine"),
                new UMLComboBoxNavigator(this, Translator.localize(
                        "tooltip.nav-submachine"), submachineBox));
        addField(Translator.localize("label.entry"),
                getEntryScroll());
        addField(Translator.localize("label.exit"),
                getExitScroll());
        addField(Translator.localize("label.do-activity"),
                getDoScroll());

        addSeperator();

        addField(Translator.localize("label.incoming"),
                getIncomingScroll());
        addField(Translator.localize("label.outgoing"),
                getOutgoingScroll());
        addField(Translator.localize("label.internal-transitions"),
                getInternalTransitionsScroll());

        addSeperator();

        addField(Translator.localize("label.subvertex"),
                new JScrollPane(new UMLMutableLinkedList(
                        new UMLCompositeStateSubvertexListModel(), null,
                        ActionNewStubState.getInstance())));
    }

    /**
     * @see org.argouml.uml.ui.behavior.state_machines.PropPanelStateVertex#addExtraButtons()
     */
    protected void addExtraButtons() {
        // Intentionally do nothing.
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        super.targetSet(e);
        if (e != null) {
            Object source = e.getSource();
            if (source != null
                    && source instanceof TargetManager) {
                Object target =
                    ((TargetManager) e.getSource()).getModelTarget();
                if (Model.getFacade().isASubmachineState(target)) {
                    getTitleLabel().setText("Submachine State");
                }
            }
        }
    }

}
