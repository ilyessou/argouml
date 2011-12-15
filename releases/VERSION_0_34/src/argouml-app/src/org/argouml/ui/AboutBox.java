/* $Id$
 *******************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Linus Tolke
 *******************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.argouml.i18n.Translator;
import org.argouml.moduleloader.ModuleLoader2;
import org.argouml.util.ArgoDialog;
import org.argouml.util.Tools;

/**
 * This is what you see after you activate the "Help->About ArgoUML" menu-item.
 * <p>
 * Modules can add extra tabs at will.
 */
public class AboutBox extends ArgoDialog {

    /**
     * Insets in pixels.
     */
    private static final int INSET_PX = 3;
    
    private static Map<String, Component> extraTabs = 
        new HashMap<String, Component>();

    private JTabbedPane tabs = new JTabbedPane();

    /**
     * Shared splash panel.
     */
    private SplashPanel splashPanel;

    /**
     * Class constructor.
     */
    public AboutBox() {
	this((Frame) null, false);
    }

    /**
     * Class constructor.
     *
     * @param owner     the frame from which the dialog is displayed
     */
    public AboutBox(Frame owner) {
	this(owner, false);
    }

    /**
     * @see Translator#localize(String)
     *
     * @param str The key to localize.
     * @return The localized String.
     */
    private static String localize(String str) {
	return Translator.localize(str);
    }

    /**
     * Creates a JScrollPane from the text.
     *
     * @return          the JScrollPane
     * @param text      the text to represent
     */
    private JScrollPane createPane(String text) {
	JTextArea a = new JTextArea();
	a.setEditable(false);
	a.setLineWrap(true);
	a.setWrapStyleWord(true);
	a.setMargin(new Insets(INSET_PX, INSET_PX, INSET_PX, INSET_PX));
	a.setText(text);
	a.setCaretPosition(0);
	return new JScrollPane(a);
    }

    /**
    * Class constructor.
    *
    * @param owner      the frame from which the dialog is displayed
    * @param modal      true for a modal dialog, false for one that allows
    *                   other windows to be active at the same time
    */
    public AboutBox(Frame owner, boolean modal) {
	super(localize("aboutbox.aboutbox-title"), modal);
	splashPanel = new SplashPanel("Splash");
	int imgWidth = splashPanel.getImage().getIconWidth();
	int imgHeight = splashPanel.getImage().getIconHeight();
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	setLocation(scrSize.width / 2 - imgWidth / 2,
		    scrSize.height / 2 - imgHeight / 2);
	getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel myInsetPanel = new JPanel();
        /* top, left, bottom, right */
        myInsetPanel.setBorder(new EmptyBorder(30, 40, 40, 40));
        /* This gives some more space to the row of tabs,
           so that there will not be 2 rows of tabs
           See issue 2365, remark 3 from Jeremy.         */
        imgWidth  += 40 + 40;
        /* It looks better if the height increases, too */
        imgHeight += 40 + 40;
        myInsetPanel.add(splashPanel);
        tabs.addTab(localize("aboutbox.tab.splash"), myInsetPanel);

	tabs.addTab(localize("aboutbox.tab.version"), createPane(getVersion()));
	tabs.addTab(localize("aboutbox.tab.credits"), createPane(getCredits()));

	tabs.addTab(localize("aboutbox.tab.contacts"),
		     createPane(localize("aboutbox.contacts")));
	tabs.addTab(localize("aboutbox.tab.bugreport"),
		     createPane(localize("aboutbox.bugreport")));
	tabs.addTab(localize("aboutbox.tab.legal"),
		     createPane(localize("aboutbox.legal")));

        for (String key : extraTabs.keySet()) {
            tabs.addTab(key, extraTabs.get(key));
        }

	getContentPane().setLayout(new BorderLayout(0, 0));
	getContentPane().add(tabs, BorderLayout.CENTER);

	// TODO: 10 and 120 were found by trial and error.  Calculate them.
	setSize(imgWidth + 10, imgHeight + 120);
	//pack();
    }

    /**
     * Add an extra tab to the About box.
     * 
     * @param name the name of the tab as shown on screen
     * @param tab the tab
     */
    public static void addAboutTab(String name, Component tab) {
        extraTabs.put(name, tab);
    }
    
    /**
     * Remove a previously added tab from the About Box.
     * 
     * @param name the name of the tab as shown on screen
     */
    public static void removeAboutTab(String name) {
        extraTabs.remove(name);
    }

    /**
     * Get the contents of the version tab.<p>
     *
     * This should include all libraries used by ArgoUML.<p>
     *
     * <em>Library maintainers! Add and update information here!</em>
     *
     * @return The contents.
     */
    private String getVersion() {
        StringBuffer buf = new StringBuffer();
        buf.append(localize("aboutbox.generated-version-header"));
        buf.append(Tools.getVersionInfo());
        buf.append(localize("aboutbox.used-tools-header"));
        // Not localized:
        buf.append("* GEF (gef.tigris.org)\n");
        buf.append("* Xerces-J 2.6.2\n");
        buf.append("* NetBeans MDR (mdr.netbeans.org    )\n");
        buf.append("* TU-Dresden OCL-Compiler "
                	  + "(dresden-ocl.sourceforge.net)\n");
        buf.append("* ANTLR 2.7.7 (www.antlr.org) *DEPRECATED*\n");

        buf.append("\n");
        buf.append(localize("aboutbox.loaded-modules-header"));
        Iterator<String> iter = ModuleLoader2.allModules().iterator();
        while (iter.hasNext()) {
            String moduleName = iter.next();
            buf.append("\nModule: ");
            buf.append(moduleName);
            buf.append("\nDescription: ");
            String desc = ModuleLoader2.getDescription(moduleName);
            buf.append(desc.replaceAll("\n\n", "\n"));
        }

        buf.append("\n\n");
        buf.append(localize("aboutbox.thanks"));
        buf.append("\n");
        return buf.toString();
    }

    /**
     * Get the contents of the Credits tab.<p>
     *
     * This should include a list of all persons that have ever contributed
     * to ArgoUML.<p>
     *
     * <em>Commiters, add your contributor here.</em>
     *
     * @return The contents.
     */
    private String getCredits() {
        StringBuffer buf = new StringBuffer();
        buf.append(localize("aboutbox.developed-by"));
        buf.append("\n\n");
        buf.append(localize("aboutbox.project-leader"));
        buf.append(" Linus Tolke (linus@tigris.org)");
        buf.append("\n\n");
        buf.append(localize("aboutbox.module-owners"));
        buf.append("\n");

        buf.append("+ UML Model, Diagrams, GUI, Persistence: Bob Tarling\n");
	buf.append("+ MDR Implementation: Tom Morris\n");
        buf.append("+ Sequence Diagrams: Christian L�pez Esp�nola\n");
        buf.append("+ C++: Luis Sergio Oliveira\n");
        buf.append("+ Csharp: Jan Magne Andersen\n");
        buf.append("+ PHP 4/5: Kai Schroeder\n");
        buf.append("+ SQL: Kai Drahmann\n");
        buf.append("+ Code Generation/Reverse Engineering: Thomas Neustupny\n");
        buf.append("+ Cognitive support: Markus Klink\n");
        buf.append("+ Notation, User Manual: Michiel van der Wulp\n");
        buf.append("+ Localization French: Jean-Hugues de Raigniac\n");
        buf.append("+ Localization Russian: Alexey Aphanasyev\n");
        buf.append("+ Localization German: Harald Braun\n");
        buf.append("+ Localization Spanish: Stewart Munoz\n");
        buf.append("+ Localization British English: Alex Bagehot\n");
        buf.append("+ Localization Norwegian (bokm\u00E5l): "
                + "Hans Fredrik Nordhaug\n");
        buf.append("+ Localization Chinese: Jeff Liu\n");
        buf.append("+ Localization Portuguese: Sergio Agostinho\n");

        buf.append("\n");

        buf.append(Translator.messageFormat(
                "aboutbox.contrib-developers-for-release",
                new Object[] {
                    "0.28",
                }));
        buf.append("\n");

        buf.append("+ Bob Tarling\n");
        buf.append("+ Bogdan Szanto\n");
        buf.append("+ Bogdan Pistol\n");
        buf.append("+ Brian Hudson\n");
        buf.append("+ Christian L�pez Esp�nola\n");
        buf.append("+ Dave Thompson\n");
        buf.append("+ Harald Braun\n");
        buf.append("+ Jan Magne Andersen\n");
        buf.append("+ Luis Sergio Oliveira\n");
        buf.append("+ Linus Tolke\n");
        buf.append("+ Lukasz Gromanowski\n");
        buf.append("+ Marcos Aurelio\n");
        buf.append("+ Michiel van der Wulp\n");
        buf.append("+ Thilina Hasantha\n");
        buf.append("+ Thomas Neustupny\n");
        buf.append("+ Tom Morris\n");
        
        buf.append("\n");

        buf.append(Translator.messageFormat(
                "aboutbox.contrib-developers-for-release",
                new Object[] {
                    "0.26",
                }));
        buf.append("\n");

        buf.append("+ Aleksandar\n");
        buf.append("+ Alexandre da Silva\n");
        buf.append("+ Scott Roberts\n");
        buf.append("+ S�rgio Adriano Fernandes Lopes\n");
        
        buf.append("\n");

        buf.append(Translator.messageFormat(
                "aboutbox.contrib-developers-for-release",
                new Object[] {
                    "0.24",
                }));
        buf.append("\n");
        
        buf.append("+ Andrea Nironi\n");
        buf.append("+ Hans Fredrik Nordhaug\n");
        buf.append("+ Markus Klink\n");
        buf.append("+ Sergio Agostinho\n");
        buf.append("+ Stewart Munoz\n");

        buf.append("\n");

        buf.append(Translator.messageFormat(
                "aboutbox.contrib-developers-for-release",
                new Object[] {
                    "0.22",
                }));
        buf.append("\n");

        buf.append("+ Jeff Liu\n");
        buf.append("+ Ludovic Maitre\n");

        buf.append("\n");

        buf.append(Translator.messageFormat(
                "aboutbox.contrib-developers-for-release",
                new Object[] {
                    "0.20",
        	}));
        buf.append("\n");

        final String cpbi =
            	", Polytechnic of Bandung Indonesia"
            	+ ", Computer Engineering Departement";
        // Alphabetic order!
        buf.append("+ Decki" + cpbi + "\n");
        buf.append("+ Endi" + cpbi + "\n");
        buf.append("+ Kai Schroeder\n");
        buf.append("+ Michael A. MacDonald\n");
        buf.append("+ Yayan" + cpbi + "\n");

        buf.append("\n");

        buf.append(localize("aboutbox.past-developers"));
        buf.append("\n");

        // Alphabetic order!
        buf.append("+ Adam Gauthier\n");
        buf.append("+ Adam Bonner\n");
        buf.append("+ Alex Bagehot\n");
        buf.append("+ Alexander Lepekhine\n");
        buf.append("+ Alexey Aphanasyev\n");
        buf.append("+ Andreas Rueckert (a_rueckert@gmx.net) (Java RE)\n");
        buf.append("+ Clemens Eichler\n");
        buf.append("+ Curt Arnold\n");
        buf.append("+ David Glaser\n");
        buf.append("+ David Hilbert\n");
        buf.append("+ David Redmiles\n");
        buf.append("+ Dennis Daniels (denny_d@hotmail.com)\n");
        buf.append("+ Donat Wullschleger\n");
        buf.append("+ Edwin Park\n");
        buf.append("+ Eric Lefevre\n");
        buf.append("+ Eugenio Alvarez\n");
        buf.append("+ Frank Finger\n");
        buf.append("+ Frank Wienberg\n");
        buf.append("+ Grzegorz Prokopski\n");
        buf.append("+ Jaap Branderhorst\n");
        buf.append("+ Jason Robbins (Project founder, researcher)\n");
        buf.append("+ Jean-Hugues de Raigniac\n");
        buf.append("+ Jeremy Jones\n");
        buf.append("+ Jim Holt\n");
        buf.append("+ Luc Maisonobe\n");
        buf.append("+ Marcus Andersson\n");
        buf.append("+ Marko Boger (GentleWare) (UML Diagrams)\n");
        buf.append("+ Michael Stockman\n");
        buf.append("+ Nick Santucci\n");
        buf.append("+ Phil Sager\n");
        buf.append("+ Philippe Vanpeperstraete (User Manual)\n");
        buf.append("+ Piotr Kaminski\n");
        buf.append("+ Scott Guyer\n");
        buf.append("+ Sean Chen\n");
        buf.append("+ Steffen Zschaler\n");
        buf.append("+ Steve Poole\n");
        buf.append("+ Stuart Zakon\n");
        buf.append("+ Thierry Lach\n");
        buf.append("+ Thomas Schaumburg\n");
        buf.append("+ Thorsten Sturm (thorsten.sturm@gentleware.de) (GEF)\n");
        buf.append("+ Toby Baier (UML Metamodel, XMI, Project leader)\n");
        buf.append("+ Will Howery\n");
        buf.append("+ ICS 125 team Spring 1996\n");
        buf.append("+ ICS 125 teams Spring 1998\n");

        return buf.toString();
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = -3647983226617303893L;
} /* end class AboutBox */
