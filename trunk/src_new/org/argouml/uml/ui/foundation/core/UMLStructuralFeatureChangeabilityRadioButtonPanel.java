// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// $Id$
package org.argouml.uml.ui.foundation.core;

import java.util.HashMap;
import java.util.Map;

import org.argouml.application.api.Argo;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLRadioButtonPanel;

/**
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 29, 2003
 */
public class UMLStructuralFeatureChangeabilityRadioButtonPanel
	extends UMLRadioButtonPanel {

    private static Map labelTextsAndActionCommands = new HashMap();

    static {
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-addonly"), ActionSetChangeability.ADDONLY_COMMAND);
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-changeable"), ActionSetChangeability.CHANGEABLE_COMMAND);
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-frozen"), ActionSetChangeability.FROZEN_COMMAND);
    }

    /**
     * Constructor for UMLAssociationEndChangeabilityRadioButtonPanel.
     * @param title
     * @param labeltexts
     * @param propertySetName
     * @param setAction
     * @param horizontal
     */
    public UMLStructuralFeatureChangeabilityRadioButtonPanel(String title, boolean horizontal) {
        super(title, labelTextsAndActionCommands, "visibility", ActionSetChangeability.SINGLETON, horizontal);
    }

    /**
     * @see org.argouml.uml.ui.UMLRadioButtonPanel#buildModel()
     */
    public void buildModel() {
        if (getTarget() != null) {
            Object target = /*(MStructuralFeature)*/ getTarget();
            Object/*MChangeableKind*/ kind = ModelFacade.getChangeability(target);
            if (kind == null || kind.equals(ModelFacade.ADD_ONLY_CHANGEABLEKIND)) {
                setSelected(ActionSetChangeability.ADDONLY_COMMAND);
            } else if (kind.equals(ModelFacade.CHANGEABLE_CHANGEABLEKIND)) {
                setSelected(ActionSetChangeability.CHANGEABLE_COMMAND); 
            } else if (kind.equals(ModelFacade.FROZEN_CHANGEABLEKIND)) {
                setSelected(ActionSetChangeability.FROZEN_COMMAND);
            } else {
                setSelected(ActionSetChangeability.CHANGEABLE_COMMAND);
            }
        }
    }
}

