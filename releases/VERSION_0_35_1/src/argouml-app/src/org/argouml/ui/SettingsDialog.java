/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *    Michiel van der Wulp
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.argouml.application.api.GUISettingsTabInterface;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.util.ArgoDialog;

/**
 * Action for starting the Argo settings window.
 *
 * @author Thomas N
 * @author Thierry Lach
 * @since 0.9.4
 */
class SettingsDialog extends ArgoDialog implements WindowListener {

    private JButton applyButton;

    private JTabbedPane tabs;

    private boolean windowOpen;

    /**
     * Constructor to build new settings dialog.
     */
    SettingsDialog() {
        super(Translator.localize("dialog.settings"),
              ArgoDialog.OK_CANCEL_OPTION,
              true);

        tabs = new JTabbedPane();

        applyButton = new JButton(Translator.localize("button.apply"));
        String mnemonic = Translator.localize("button.apply.mnemonic");
        if (mnemonic != null && mnemonic.length() > 0) {
            applyButton.setMnemonic(mnemonic.charAt(0));
        }
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
        addButton(applyButton);

        // Add settings from the settings registry.
        settingsTabs = GUI.getInstance().getSettingsTabs();
        for (GUISettingsTabInterface stp : settingsTabs) {
            // TODO: Only fetch names and defer fetching panels until needed
            tabs.addTab(
                    Translator.localize(stp.getTabKey()),
                    stp.getTabPanel());
        }

        // Increase width to accommodate all tabs on one row.
        final int minimumWidth = 600;
        tabs.setPreferredSize(new Dimension(Math.max(tabs
                .getPreferredSize().width, minimumWidth), tabs
                .getPreferredSize().height));

        tabs.setTabPlacement(SwingConstants.LEFT);
        setContent(tabs);
        addWindowListener(this);
    }

    @Override
    public void setVisible(boolean show) {
        if (show) {
            handleRefresh();
            toFront();
        }
        super.setVisible(show);
        // windowOpen state will be changed when window is activated
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev) {
        super.actionPerformed(ev);
        if (ev.getSource() == getOkButton()) {
            handleSave();
        } else if (ev.getSource() == getCancelButton()) {
            handleCancel();
        }
    }

    /*
     * Called when the user has pressed Save. Performs "Save" in all Tabs.
     */
    private void handleSave() {
        for (GUISettingsTabInterface tab : settingsTabs) {
            tab.handleSettingsTabSave();
        }
        windowOpen = false;
        Configuration.save();
    }

    /*
     * Called when the user has pressed Cancel. Performs "Cancel" in all Tabs.
     */
    private void handleCancel() {
        for (GUISettingsTabInterface tab : settingsTabs) {
            tab.handleSettingsTabCancel();
        }
        windowOpen = false;
    }

    /**
     * Perform "Refresh" in all Tabs.
     */
    private void handleRefresh() {
        for (GUISettingsTabInterface tab : settingsTabs) {
            tab.handleSettingsTabRefresh();
        }
    }

    private void handleOpen() {
        // We only request focus the first time we become visible
        if (!windowOpen) {
            getOkButton().requestFocusInWindow();
            windowOpen = true;
        }
    }

    /*
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
        handleOpen();
    }

    /*
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
        // ignored - we only care about open/closing
    }

    /*
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
        // ignored - we only care about open/closing
    }

    /*
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
        // ignored - we only care about open/closing
    }

    /*
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
        // ignored - we only care about open/closing
    }

    /*
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
        handleOpen();
    }

    /*
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        // Handle the same as an explicit cancel
        handleCancel();
    }

    /**
     * The serial version.
     */
    private static final long serialVersionUID = -8233301947357843703L;

    private List<GUISettingsTabInterface> settingsTabs;
}
