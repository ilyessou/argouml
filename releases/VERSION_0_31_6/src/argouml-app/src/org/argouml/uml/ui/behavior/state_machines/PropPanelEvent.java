/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.state_machines;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;

/**
 * The properties panel for an Event.
 *
 * @author oliver.heyden
 * @deprecated in 0.31.2 by Bob Tarling  This is replaced by the XML property
 * panels module
 */
@Deprecated
public abstract class PropPanelEvent extends PropPanelModelElement {

    private JScrollPane paramScroll;

    private UMLEventParameterListModel paramListModel;


    /**
     * Construct a property panel for an Event.
     * 
     * @param name the name string of the properties panel
     * @param icon the icon to be shown next to the name
     */
    public PropPanelEvent(String name, ImageIcon icon) {
        super(name, icon);
        initialize();
    }
    
    /**
     * Initialize the panel with all fields and stuff.
     */
    protected void initialize() {

        paramScroll = getParameterScroll();

        addField("label.name", getNameTextField());
        addField("label.namespace", getNamespaceSelector());

        addSeparator();
        addField("label.parameters", getParameterScroll());
        JList transitionList = new UMLLinkedList(
                new UMLEventTransitionListModel());
        transitionList.setVisibleRowCount(2);
        addField("label.transition",
                new JScrollPane(transitionList));

        addSeparator();

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNewStereotype());
    }


    /**
     * @return the parameter scroll
     */
    protected JScrollPane getParameterScroll() {
        if (paramScroll == null) {
            paramListModel = new UMLEventParameterListModel();
            JList paramList = new UMLLinkedList(paramListModel);
            paramList.setVisibleRowCount(3);
            paramScroll = new JScrollPane(paramList);
        }
        return paramScroll;
    }

}
