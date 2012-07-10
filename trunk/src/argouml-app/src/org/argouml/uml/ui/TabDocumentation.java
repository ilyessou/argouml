/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.argouml.application.api.Argo;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.swingext.UpArrowIcon;
import org.argouml.ui.TabModelTarget;
import org.tigris.gef.presentation.Fig;
import org.tigris.swidgets.Horizontal;
import org.tigris.swidgets.Vertical;

/**
 * This the tab in the details pane for documentation.<p>
 *
 * All data in this tab is stored as Tagged Values,
 * and saved and reloaded correctly.<p>
 *
 * Selecting the menu Edit>Settings... and setting the user name
 * does not affect the author field
 * in the documentation tab. <p>
 *
 * Enabling output from the documentation fields when generating code as
 * embedded in javadocs and for html/diagram creation is considered important
 * by users.<p>
 *
 * When importing sources, already saved javadoc statements are not
 * automatically added to the documenation Jtext window. When Adding notes to
 * classes the notes are not included in the documentation text window.<p>
 *
 * The "Since" field is not validated for real date. Change to DateField?<p>
 *
 * Note that all fields in the TabDocumentation are added automatically
 * to the tagged value tab view.<p>
 *
 * Refactored by: raphael-langerhorst@gmx.at; 5th April 03<p>
 * Changes: <ul>
 * <li>uses LabelledLayout instead of GridBagLayout
 * <li>uses the new event pump introduced late 2002 by Jaap</ul><p>
 *
 * UMLModelElementTaggedValueDocument is used to access the tagged values of an
 * ModelElement.
 */
public class TabDocumentation extends PropPanel implements TabModelTarget {

    private static String orientation = Configuration.getString(Configuration
            .makeKey("layout", "tabdocumentation"));

    /**
     * Construct new documentation tab
     */
    public TabDocumentation() {
        super(Translator.localize("tab.documentation"), (ImageIcon) null);
        setOrientation((
                orientation.equals("West") || orientation.equals("East"))
                ? Vertical.getInstance() : Horizontal.getInstance());
        setIcon(new UpArrowIcon());

        addField(Translator.localize("label.author"), new UMLTextField2(
                new UMLModelElementTaggedValueDocument(Argo.AUTHOR_TAG)));

        addField(Translator.localize("label.version"), new UMLTextField2(
                new UMLModelElementTaggedValueDocument(Argo.VERSION_TAG)));

        addField(Translator.localize("label.since"), new UMLTextField2(
                new UMLModelElementTaggedValueDocument(Argo.SINCE_TAG)));

        addField(Translator.localize("label.deprecated"),
                new UMLDeprecatedCheckBox());

        addField(Translator.localize("label.derived"),
                new UMLDerivedCheckBox());

        UMLTextArea2 see = new UMLTextArea2(
                new UMLModelElementTaggedValueDocument(Argo.SEE_TAG));
        see.setRows(2);
        see.setLineWrap(true);
        see.setWrapStyleWord(true);
        JScrollPane spSee = new JScrollPane();
        spSee.getViewport().add(see);
        addField(Translator.localize("label.see"), spSee);

        //make new column with LabelledLayout
        add(LabelledLayout.getSeparator());

        UMLTextArea2 doc = new UMLTextArea2(
                new UMLModelElementTaggedValueDocument(Argo.DOCUMENTATION_TAG));
        doc.setRows(2);
        doc.setLineWrap(true);
        doc.setWrapStyleWord(true);
        JScrollPane spDocs = new JScrollPane();
        spDocs.getViewport().add(doc);
        addField(Translator.localize("label.documentation"), spDocs);

        // Comment.name text field - editing disabled
        UMLTextArea2 comment = new UMLTextArea2(
                new UMLModelElementCommentDocument(false));
        disableTextArea(comment);
        JScrollPane spComment = new JScrollPane();
        spComment.getViewport().add(comment);
        addField(Translator.localize("label.comment.name"), spComment);

        // Comment.body text field - editing disabled
        UMLTextArea2 commentBody = new UMLTextArea2(
                new UMLModelElementCommentDocument(true));
        disableTextArea(commentBody);
        JScrollPane spCommentBody = new JScrollPane();
        spCommentBody.getViewport().add(commentBody);
        addField(Translator.localize("label.comment.body"), spCommentBody);

        /* Since there are no buttons on this panel, we have to set
         * the size of the buttonpanel, otherwise the 
         * title would not be aligned right. */
        setButtonPanelSize(18);
    }
    
    private void disableTextArea(final JTextArea textArea) {
        textArea.setRows(2);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(textArea.getForeground());
        // Only change the background colour if it is supplied by the LAF.
        // Otherwise leave look and feel to handle this itself.
        final Color inactiveColor =
            UIManager.getColor("TextField.inactiveBackground");
        if (inactiveColor != null) {
            // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4919687
            textArea.setBackground(new Color(inactiveColor.getRGB()));
        }
    }

    /**
     * Checks if the tab should be enabled. Returns true if the target
     * returned by getTarget is a modelelement or if that target shows up as Fig
     * on the active diagram and has a modelelement as owner.
     *
     * @return true if this tab should be enabled, otherwise false.
     */
    public boolean shouldBeEnabled() {
        Object target = getTarget();
        return shouldBeEnabled(target);
    }

    /*
     * @see org.argouml.uml.ui.PropPanel#shouldBeEnabled(java.lang.Object)
     */
    @Override
    public boolean shouldBeEnabled(Object target) {
        target = (target instanceof Fig) ? ((Fig) target).getOwner() : target;
        return Model.getFacade().isAModelElement(target);
    }

}

