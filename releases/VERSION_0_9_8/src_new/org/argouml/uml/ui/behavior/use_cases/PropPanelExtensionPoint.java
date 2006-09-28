// Copyright (c) 1996-99 The Regents of the University of California. All
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

// File: PropPanelExtensionPoint.java
// Classes: PropPanelExtensionPoint
// Original Author: mail@jeremybennett.com
// $Id$

// 27 Mar 2002: Jeremy Bennett (mail@jeremybennett.com). Created to support a
// proper Extend implementation with Use Cases


package org.argouml.uml.ui.behavior.use_cases;

import org.argouml.uml.ui.*;
import org.argouml.uml.ui.foundation.core.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.behavior.use_cases.*;
import ru.novosoft.uml.model_management.*;


/**
 * <p>Builds the property panel for an extension point.</p>
 *
 * <p>This is a child of PropPanelModelElement.</p>
 */

public class PropPanelExtensionPoint extends PropPanelModelElement {


    /**
     * Constructor. Builds up the various fields required.
     */

    public PropPanelExtensionPoint() {

        // Invoke the ModelElement constructor, but passing in our name and
        // representation (we use the same as dependency) and requesting 2
        // columns 

        super("ExtensionPoint", _extensionPointIcon, 2);

        // First column

        // nameField, stereotypeBox and namespaceScroll are all set up by
        // PropPanelModelElement.

        addCaption("Name:", 1, 0, 0);
        addField(nameField, 1, 0, 0);

        addCaption("Stereotype:", 2, 0, 0);
        addField(new UMLComboBoxNavigator(this,"NavStereo",stereotypeBox),
                 2, 0, 0);

        addCaption("Namespace:", 3, 0, 0);
        addField(namespaceScroll, 3, 0, 0);

        // Our location (a String). We can pass in the get and set methods from
        // NSUML associated with the NSUML type. Allow the location label to
        // expand vertically so we all float to the top.

        UMLTextField locationField = 
            new UMLTextField(this,
                             new UMLTextProperty(MExtensionPoint.class,
                                                 "location","getLocation",
                                                 "setLocation"));

        addCaption("Location:", 4, 0, 1);
        addField(locationField, 4, 0, 0);

        // Second column

        // Link to the base use case with which we are associated.

        UMLComboBoxModel     model = 
            new UMLComboBoxModel(this, "isAcceptableUseCase",
                                 "useCase", "getUseCase", "setUseCase",
                                 true, MUseCase.class, true);
        UMLComboBox          box   = new UMLComboBox(model);
        UMLComboBoxNavigator nav   =
            new UMLComboBoxNavigator(this, "NavUseCase", box);

        addCaption("Base Use Case:", 0, 1, 0);
        addField(nav, 0, 1, 0);

        // The extension use cases (via the Extend relationship)

        JList extendList =
            new UMLList(new UMLExtendListModel(this, null, true),
                        true);

        extendList.setBackground(getBackground());
        extendList.setForeground(Color.blue);

        JScrollPane extendScroll =
            new JScrollPane(extendList,
                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        addCaption("Extending Use Cases:", 2, 1, 1);
        addField(extendScroll, 2, 1, 1);


        // Add the toolbar. Just the four basic buttons for now.

        new PropPanelButton(this, buttonPanel, _navUpIcon,
                            localize("Go up"), "navigateNamespace", null);
        new PropPanelButton(this, buttonPanel, _navBackIcon,
                            localize("Go back"), "navigateBackAction",
                            "isNavigateBackEnabled");
        new PropPanelButton(this, buttonPanel, _navForwardIcon,
                            localize("Go forward"), "navigateForwardAction",
                            "isNavigateForwardEnabled");
        new PropPanelButton(this, buttonPanel, _deleteIcon,
                            localize("Delete"), "removeElement", null); 
    }


    /**
     * <p>Check if a given name is our metaclass name, or that of one of our
     *   parents. Used to determine which stereotypes to show. Only handles
     *   metaclasses below ModelElement.</p>
     *
     * <p>Since we are a child of ModelElement, we effectively have no
     *   parents.</p>
     *
     * @param baseClass  the string representation of the base class to test.
     *
     * @return           <code>true</code> if baseClass is our metaclass name
     *                   of that of one of our parents.
     */

    protected boolean isAcceptibleBaseMetaClass(String baseClass) {

        return baseClass.equals("ExtensionPoint");
    }


    /**
     * <p>Get the use case associated with the extension point.</p>
     *
     * @return  The {@link MUseCase} associated with this extension point or
     *          <code>null</code> if there is none. Returned as type {@link
     *          MUseCase} to fit in with the type specified for the {@link
     *          UMLComboBoxModel}.
     */ 

    public MUseCase getUseCase() {
        MUseCase useCase   = null;
        Object   target = getTarget();

        if (target instanceof MExtensionPoint) {
            useCase = ((MExtensionPoint) target).getUseCase();
        }

        return useCase;
    }


    /**
     * <p>Set the use case associated with the extension point.</p>
     *
     * @param useCase  The {@link MUseCase} to associate with this extension
     *              point. Supplied as type {@link MUseCase} to fit in
     *              with the type specified for the {@link UMLComboBoxModel}.
     */

    public void setUseCase(MUseCase useCase) {
        Object target = getTarget();

        if(target instanceof MExtensionPoint) {
            ((MExtensionPoint) target).setUseCase(useCase);
        }
    }


    /**
     * <p>Predicate to test if a model element may appear in the list of
     *   potential use cases.</p>
     *
     * <p><em>Note</em>. We don't try to prevent the user setting up circular
     *   extend relationships. This may be necessary temporarily, for example
     *   while reversing a relationship. It is up to a critic to track
     *   this.</p>
     *
     * @param modElem  the {@link MModelElement} to test.
     *
     * @return         <code>true</code> if modElem is a use case,
     *                 <code>false</code> otherwise.
     */

    public boolean isAcceptableUseCase(MModelElement modElem) {

        return modElem instanceof MUseCase;
    }


} /* end class PropPanelExtend */
