// $Id$
// Copyright (c) 2007-2008 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.argouml.application.api.AbstractArgoJPanel;
import org.argouml.application.api.GUISettingsTabInterface;
import org.argouml.application.api.InitSubsystem;
import org.argouml.model.Model;

/**
 * Initialise this subsystem.
 *
 * @author Michiel
 */
public class InitUmlUI implements InitSubsystem {

    public void init() {
        /* Set up the property panels for UML elements: */
        PropPanelFactory elementFactory = new ElementPropPanelFactory();
        PropPanelFactoryManager.addPropPanelFactory(elementFactory);

        /* Set up the property panels for other UML objects: */
        PropPanelFactory umlObjectFactory = new UmlObjectPropPanelFactory();
        PropPanelFactoryManager.addPropPanelFactory(umlObjectFactory);
    }

    public List<AbstractArgoJPanel> getDetailsTabs() {
        List<AbstractArgoJPanel> result = 
            new ArrayList<AbstractArgoJPanel>();
        result.add(new TabProps());
        result.add(new TabDocumentation());
        result.add(new TabStyle());
        result.add(new TabSrc());
        result.add(new TabConstraints());
        result.add(new TabStereotype());
        // Add the tagged values for UML 1.x
        if( Model.getFacade().getUmlVersion().charAt(0) == '1') {
            result.add(new TabTaggedValues());
        }
        return result;
    }

    public List<GUISettingsTabInterface> getProjectSettingsTabs() {
        return Collections.emptyList();
    }

    public List<GUISettingsTabInterface> getSettingsTabs() {
        return Collections.emptyList();
    }

}
