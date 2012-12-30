/* $Id$
 *******************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *******************************************************************************
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

package org.argouml.application.api;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.argouml.i18n.Translator;
import org.argouml.util.ArgoFrame;
import org.tigris.swidgets.Orientable;
import org.tigris.swidgets.Orientation;

/**
 * A subclass of JPanel that can act as a tab in the DetailsPane or
 * MultiEditorPane. Added functionality:<p>
 *
 * Spawning: When the tab is double-clicked, this JPanel will generate a
 * separate window of the same size and with the same contents. This is almost
 * like "tearing off" a tab.<p>
 *
 * TODO: Spawning of windows disabled in spawn()<p>
 *
 * Title: This JPanel keeps track of its own title.<p>
 *
 * Icon: This JPanel keeps track of its own icon; i.e. an arrow pointing to
 * the panel that it gives details of.<p>
 *
 * Orientation: This JPanel is Orientable.<p>
 *
 * Cloning: This JPanel may be cloned.<p>
 *
 * This class used to be named TabSpawnable.
 * Renamed since it is not a Tab, but a Panel, and being spawnable is
 * not any more its main purpose.
 */
public abstract class AbstractArgoJPanel extends JPanel
    implements Cloneable, Orientable {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(AbstractArgoJPanel.class.getName());

    private static final int OVERLAPP = 30;

    private String title = Translator.localize("tab.untitled");

    private Icon icon = null;

    /**
     * if true, remove tab from parent JTabbedPane.
     */
    private boolean tear = false;

    private Orientation orientation;

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }
    ////////////////////////////////////////////////////////////////
    // constructor

    /**
     * The constructor.
     *
     */
    public AbstractArgoJPanel() {
        this(Translator.localize("tab.untitled"), false);
    }

    /**
     * The constructor.
     *
     * @param theTitle The name as a localized string.
     */
    // TODO: Review all callers to make sure that they localize the title
    public AbstractArgoJPanel(String theTitle) {
        this(theTitle, false);
    }

    /**
     * The constructor.
     *
     * @param theTitle The name (a localized string).
     * @param t if true, remove tab from parent JTabbedPane
     */
    // TODO: Review all callers to make sure that they localize the title
    // In process by Harold Braun 20070912
    public AbstractArgoJPanel(String theTitle, boolean t) {
        setTitle(theTitle);
        tear = t;
    }

    /**
     * This is not a real clone since it doesn't copy anything from the object
     * it is cloning. The {@link #spawn} method copies the title and in
     * some cases also the Target.
     *
     * @return the new object or null if not possible.
     */
    public Object clone() {
        try {
            return this.getClass().newInstance();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "exception in clone()", ex);
        }
        return null;
    }

    /*
     * @see org.tigris.swidgets.Orientable#setOrientation(Orientation)
     */
    public void setOrientation(Orientation o) {
        this.orientation = o;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @return The title of the panel, a localized string.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param t The title, a localized string.
     */
    public void setTitle(String t) {
        title = t;
    }

    /**
     * @return the icon to be shown for this panel
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * @param theIcon this icon will be shown in front of the title
     */
    public void setIcon(Icon theIcon) {
        this.icon = theIcon;
    }

    ////////////////////////////////////////////////////////////////
    // actions

    /**
     * This should take its inspiration from
     * {@link org.tigris.gef.base.CmdSpawn}.<p>
     *
     * The spawned/cloned tab will be a JFrame. Currently this feature is
     * disabled for ArgoUML, except for the find dialog.
     * Code should behave though as if spawning might work at a
     * later stage.
     *
     * @return a copy of the frame or null if not clone-able.
     */
    public AbstractArgoJPanel spawn() {

        JDialog f = new JDialog(ArgoFrame.getFrame());
        f.getContentPane().setLayout(new BorderLayout());
        // TODO: Once we have fixed all subclasses the title will
        // always be localized so this localization can be removed.
        f.setTitle(Translator.localize(title));
        AbstractArgoJPanel newPanel = (AbstractArgoJPanel) clone();
        if (newPanel == null) {
	    return null; //failed to clone
	}

        // TODO: Once we have fixed all subclasses the title will
        // always be localized so this localization can be removed.
        newPanel.setTitle(Translator.localize(title));

        f.getContentPane().add(newPanel, BorderLayout.CENTER);
        Rectangle bounds = getBounds();
        bounds.height += OVERLAPP * 2;
        f.setBounds(bounds);

        Point loc = new Point(0, 0);
        SwingUtilities.convertPointToScreen(loc, this);
        loc.y -= OVERLAPP;
        f.setLocation(loc);
        f.setVisible(true);

        if (tear && (getParent() instanceof JTabbedPane)) {
	    ((JTabbedPane) getParent()).remove(this);
	}

        return newPanel;

    }

}
