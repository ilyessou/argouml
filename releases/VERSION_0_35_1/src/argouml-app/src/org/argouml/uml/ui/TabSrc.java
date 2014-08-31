/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    euluis
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2006 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;

import org.argouml.application.api.Predicate;
import org.argouml.language.ui.LanguageComboBox;
import org.argouml.model.Model;
import org.argouml.ui.TabText;
import org.argouml.uml.generator.GeneratorHelper;
import org.argouml.uml.generator.Language;
import org.argouml.uml.generator.SourceUnit;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 * Details panel tabbed panel for displaying a source code representation of
 * a UML model element in a particular Language.
 */
public class TabSrc
    extends TabText
    implements ItemListener {

    private static final long serialVersionUID = -4958164807996827484L;

    private static final Logger LOG = Logger.getLogger(TabSrc.class.getName());

    private Language langName = null;
    private String fileName = null;
    private SourceUnit[] files = null;

    private LanguageComboBox cbLang = new LanguageComboBox();
    private JComboBox cbFiles = new JComboBox();

    /**
     * These predicates determine if this tab is enabled.
     */
    private static List<Predicate> predicates = new ArrayList<Predicate>();
    static {
        /* Add a predicate for ArgoUML's default capabilities: */
        predicates.add(new DefaultPredicate());
    }


    /**
     * Create a tab that contains a toolbar.
     * Then add a language selector onto it.
     */
    public TabSrc() {
        super("tab.source", true);

        setEditable(false);
        langName = (Language) cbLang.getSelectedItem();
        fileName = null;
        getToolbar().add(cbLang);
        getToolbar().addSeparator();
        cbLang.addItemListener(this);
        getToolbar().add(cbFiles);
        getToolbar().addSeparator();
        cbFiles.addItemListener(this);
    }


    @Override
    protected void finalize() {
        cbLang.removeItemListener(this);
    }


    /**
     * Populate files[] and cbFiles, using the specified element.
     */
    private void generateSource(Object elem) {
      if ( LOG.isLoggable( Level.FINE ) ) {
          LOG.log(Level.FINE, "TabSrc.genText(): getting src for {0}",
                  Model.getFacade().getName(elem));
      }
	Collection code =
	    GeneratorHelper.generate(langName, elem, false);
	cbFiles.removeAllItems();
	if (!code.isEmpty()) {
	    files = new SourceUnit[code.size()];
	    files = (SourceUnit[]) code.toArray(files);
	    for (int i = 0; i < files.length; i++) {
		StringBuilder title = new StringBuilder(files[i].getName());
		if (files[i].getBasePath().length() > 0) {
		    title.append(" ( " + files[i].getFullName() + ")");
		}
		cbFiles.addItem(title.toString());
	    }
	}
    }


    @Override
    protected String genText(Object modelObject) {
        if (files == null) {
	    generateSource(modelObject);
        }
        if (files != null && files.length > cbFiles.getSelectedIndex()) {
            return files[cbFiles.getSelectedIndex()].getContent();
        }
        return null;
    }

    @Override
    protected void parseText(String s) {
        if ( LOG.isLoggable(Level.FINE) ) {
            LOG.log(Level.FINE, "TabSrc setting src for {0}",
                    Model.getFacade().getName(getTarget()));
        }
        Object modelObject = getTarget();
        if (getTarget() instanceof FigNode) {
            modelObject = ((FigNode) getTarget()).getOwner();
        }
        if (getTarget() instanceof FigEdge) {
            modelObject = ((FigEdge) getTarget()).getOwner();
        }
        if (modelObject == null) {
            return;
        }
        /* TODO: Implement this! */
        //Parser.ParseAndUpdate(modelObject, s);
    }


    @Override
    public void setTarget(Object t) {
        Object modelTarget = (t instanceof Fig) ? ((Fig) t).getOwner() : t;
        setShouldBeEnabled(Model.getFacade().isAClassifier(modelTarget));
	cbFiles.removeAllItems();
	files = null;
        super.setTarget(t);
    }

    /**
     * Determines if the current tab should be enabled with the given target.
     * Returns true if the given target is or represents a Classifier.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean shouldBeEnabled(Object target) {
        target = (target instanceof Fig) ? ((Fig) target).getOwner() : target;

        setShouldBeEnabled(false);
        for (Predicate p : predicates) {
            if (p.evaluate(target)) {
                setShouldBeEnabled(true);
            }
        }

        return shouldBeEnabled();
    }

    /*
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == cbLang) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
		Language newLang = (Language) cbLang.getSelectedItem();
		if (!newLang.equals(langName)) {
		    langName = newLang;
		    refresh();
		}
            }
        } else if (event.getSource() == cbFiles) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
		String newFile = (String) cbFiles.getSelectedItem();
		if (!newFile.equals(fileName)) {
		    fileName = newFile;
		    super.setTarget(getTarget());
		}
            }
        }
    }

    @Override
    public void refresh() {
        setTarget(getTarget());
    }

    /**
     * This function allows extra predicates to be added.
     * The predicates are conditions for cases where the
     * TabSrc should show source code. If a plugin module
     * is able to generate code for certain objects, for
     * which ArgoUML itself does not generate code, then
     * this function will allow the module to show the tab.
     *
     * @param predicate the predicate to be added
     */
    public static void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    private static class DefaultPredicate implements Predicate {
        public boolean evaluate(Object object) {
            return (Model.getFacade().isAClassifier(object));
        }
    }
}
