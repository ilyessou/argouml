/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.argouml.application.api.Argo;
import org.argouml.application.api.GUISettingsTabInterface;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.uml.ui.SaveGraphicsManager;
import org.argouml.util.SuffixFilter;
import org.tigris.swidgets.LabelledLayout;

/**
 * Settings panel for handling ArgoUML environment related settings.
 *
 * @author Thierry Lach
 * @since  0.9.4
 */
class SettingsTabEnvironment extends JPanel
    implements GUISettingsTabInterface {

    private JPanel topPanel;
    private JTextField fieldArgoExtDir;
    private JTextField fieldJavaHome;
    private JTextField fieldUserHome;
    private JTextField fieldUserDir;
    private JTextField fieldStartupDir;
    private JComboBox fieldGraphicsFormat;
    private JComboBox fieldGraphicsResolution;
    private Collection<GResolution> theResolutions;

    /**
     * The constructor.
     */
    SettingsTabEnvironment() {
        super();
    }

    private void buildPanel() {
        setLayout(new BorderLayout());
        int labelGap = 10;
        int componentGap = 5;
        topPanel = new JPanel(new LabelledLayout(labelGap, componentGap));

        JLabel label =
            new JLabel(Translator.localize("label.default.graphics-format"));
        fieldGraphicsFormat = new JComboBox();
        label.setLabelFor(fieldGraphicsFormat);
        topPanel.add(label);
        topPanel.add(fieldGraphicsFormat);

        label =
            new JLabel(
                    Translator.localize("label.default.graphics-resolution"));
        theResolutions = new ArrayList<GResolution>();
        theResolutions.add(new GResolution(1, "combobox.item.resolution-1"));
        theResolutions.add(new GResolution(2, "combobox.item.resolution-2"));
        theResolutions.add(new GResolution(4, "combobox.item.resolution-4"));
        fieldGraphicsResolution = new JComboBox(); //filled in later
        label.setLabelFor(fieldGraphicsResolution);
        topPanel.add(label);
        topPanel.add(fieldGraphicsResolution);

 	// This string is NOT to be translated! See issue 2381.
	label = new JLabel("${argo.ext.dir}");
	JTextField j2 = new JTextField();
        fieldArgoExtDir = j2;
	fieldArgoExtDir.setEnabled(false);
        label.setLabelFor(fieldArgoExtDir);
        topPanel.add(label);
        topPanel.add(fieldArgoExtDir);

  	// This string is NOT to be translated! See issue 2381.
	label = new JLabel("${java.home}");
	JTextField j3 = new JTextField();
        fieldJavaHome = j3;
	fieldJavaHome.setEnabled(false);
        label.setLabelFor(fieldJavaHome);
        topPanel.add(label);
        topPanel.add(fieldJavaHome);

  	// This string is NOT to be translated! See issue 2381.
	label = new JLabel("${user.home}");
	JTextField j4 = new JTextField();
        fieldUserHome = j4;
	fieldUserHome.setEnabled(false);
        label.setLabelFor(fieldUserHome);
        topPanel.add(label);
        topPanel.add(fieldUserHome);

	// This string is NOT to be translated! See issue 2381.
	label = new JLabel("${user.dir}");
	JTextField j5 = new JTextField();
        fieldUserDir = j5;
	fieldUserDir.setEnabled(false);
        label.setLabelFor(fieldUserDir);
        topPanel.add(label);
        topPanel.add(fieldUserDir);

  	label = new JLabel(Translator.localize("label.startup-directory"));
  	JTextField j6 = new JTextField();
        fieldStartupDir = j6;
	fieldStartupDir.setEnabled(false);
        label.setLabelFor(fieldStartupDir);
        topPanel.add(label);
        topPanel.add(fieldStartupDir);

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	add(topPanel, BorderLayout.NORTH);
	
	JPanel bottom = new JPanel();
	bottom.add(new JLabel(
	    Translator.localize("label.graphics-export-resolution.warning")));
	add(bottom, BorderLayout.SOUTH);
    }

    /*
     * @see GUISettingsTabInterface#handleSettingsTabRefresh()
     */
    public void handleSettingsTabRefresh() {
        fieldArgoExtDir.setText(System.getProperty("argo.ext.dir"));
        fieldJavaHome.setText(System.getProperty("java.home"));
        fieldUserHome.setText(System.getProperty("user.home"));
        fieldUserDir.setText(Configuration.getString(Argo.KEY_STARTUP_DIR,
		System.getProperty("user.dir")));
        fieldStartupDir.setText(Argo.getDirectory());

        fieldGraphicsFormat.removeAllItems();
        Collection c = SaveGraphicsManager.getInstance().getSettingsList();
        fieldGraphicsFormat.setModel(new DefaultComboBoxModel(c.toArray()));

        fieldGraphicsResolution.removeAllItems();
        fieldGraphicsResolution.setModel(new DefaultComboBoxModel(
                theResolutions.toArray()));
        int defaultResolution =
            Configuration.getInteger(
                SaveGraphicsManager.KEY_GRAPHICS_RESOLUTION, 1);
        for (GResolution gr : theResolutions) {
            if (defaultResolution == gr.getResolution()) {
                fieldGraphicsResolution.setSelectedItem(gr);
                break;
            }
        }
    }

    /*
     * @see GUISettingsTabInterface#handleSettingsTabSave()
     */
    public void handleSettingsTabSave() {
        Configuration.setString(Argo.KEY_STARTUP_DIR, fieldUserDir.getText());

        GResolution r = (GResolution) fieldGraphicsResolution.getSelectedItem();
        Configuration.setInteger(SaveGraphicsManager.KEY_GRAPHICS_RESOLUTION,
                r.getResolution());

        SaveGraphicsManager.getInstance().setDefaultFilter(
                (SuffixFilter) fieldGraphicsFormat.getSelectedItem());
    }

    /*
     * @see GUISettingsTabInterface#handleSettingsTabCancel()
     */
    public void handleSettingsTabCancel() {
	handleSettingsTabRefresh();
    }

    /*
     * @see org.argouml.ui.GUISettingsTabInterface#handleResetToDefault()
     */
    public void handleResetToDefault() {
        // Do nothing - these buttons are not shown.
    }

    /*
     * @see GUISettingsTabInterface#getTabKey()
     */
    public String getTabKey() {
        return "tab.environment";
    }

    /*
     * @see GUISettingsTabInterface#getTabPanel()
     */
    public JPanel getTabPanel() {
        if (topPanel == null) {
            buildPanel();
        }
        return this;
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = 543442930918741133L;
}


class GResolution {
    private int resolution;
    private String label;

    /**
     * Constructor.
     *
     * @param r
     * @param name
     */
    GResolution(int r, String name) {
        resolution = r;
        label = Translator.localize(name);
    }

    int getResolution() {
        return resolution;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return label;
    }
}

