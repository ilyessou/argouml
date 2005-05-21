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

package org.argouml.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.EtchedBorder;
import org.tigris.gef.ui.IStatusBar;

/**
 * The splash screen.
 *
 * TODO: JWindow? I don't want a frame or close widgets.
 *
 */
public class SplashScreen extends JWindow implements IStatusBar {

    private StatusBar statusBar = new StatusBar();

    /**
     * The constructor.
     */
    public SplashScreen() {
        this("Loading ArgoUML...", "Splash");
    }

    /**
     * The constructor.
     *
     * @param title the title of the window
     * @param iconName the icon for the window
     */
    private SplashScreen(String title, String iconName) {
	super();

	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	getContentPane().setLayout(new BorderLayout(0, 0));

	SplashPanel panel = new SplashPanel(iconName);
	if (panel.getImage() != null) {
	    int imgWidth = panel.getImage().getIconWidth();
	    int imgHeight = panel.getImage().getIconHeight();
	    Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(scrSize.width / 2 - imgWidth / 2,
			scrSize.height / 2 - imgHeight / 2);
	}

	JPanel splash = new JPanel(new BorderLayout());
	splash.setBorder(new EtchedBorder(EtchedBorder.RAISED));
	splash.add(panel, BorderLayout.CENTER);
	splash.add(statusBar, BorderLayout.SOUTH);
	getContentPane().add(splash);
	// add preloading progress bar?
	Dimension contentPaneSize = getContentPane().getPreferredSize();
	setSize(contentPaneSize.width, contentPaneSize.height);
	pack();
    }

    /**
     * @return the status bar of this dialog
     */
    public StatusBar getStatusBar() { return statusBar; }

    ////////////////////////////////////////////////////////////////
    // IStatusBar

    /**
     * @see org.tigris.gef.ui.IStatusBar#showStatus(java.lang.String)
     */
    public void showStatus(String s) { statusBar.showStatus(s); }

} /* end class SplashScreen */
