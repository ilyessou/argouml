// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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


/*
 * ArgoModule.java
 *
 * Created on June 12, 2001, 6:47 AM
 */

package org.argouml.application.api;

import java.util.Vector;

import org.apache.log4j.*;

/** Interface that defines the characteristics of an external
 *  module usable by Argo.
 *
 * @author  Will Howery
 * @author  Thierry Lach
 * @since 0.9.4
 */
public interface ArgoModule { 

    /**
     * Define a static log4j category variable for ArgoUML configuration.
     *
     * @deprecated by Linus Tolke as of 0.15.4. Use your own logger in your
     * class. This will be removed.
     */
    public static final Logger cat = 
	Logger.getLogger("org.argouml.application.modules");

    public static final String MODULEFILENAME = ".argo.modules";
    public static final String MODULEFILENAME_ALTERNATE = "argo.modules";

    /** Method called when Argo is loading a module.
     * 
     * @return true if the module initialized properly.
     */
    public boolean initializeModule();
    
    /** Method called when Argo is unloading a module.
     * 
     * @return true if the module terminated properly.
     */
    public boolean shutdownModule();

    /** Called to enable or disable a module programmatically.
     * 
     * @param tf true to enable module, false to disable
     */
    public void setModuleEnabled(boolean tf);
    
    /** Allows determination if a module is enabled or disabled
     * 
     * @return true if the module is enabled, otherwise false
     */
    public boolean isModuleEnabled(); // determines if enabled-disabled

    /** Display name of the module.
     *
     * @return the module name
     */
    public String getModuleName();

    /** Textual description of the module.
     * 
     * @return the module description
     */
    public String getModuleDescription(); 

    /** The module version.
     * 
     * There is no specified format.
     * 
     * @return a string containing the module version
     */
    public String getModuleVersion(); 
    
    /** The module author.
     * 
     * @return a string containing the module author
     */
    public String getModuleAuthor(); 
    
    /** 
     * Calls all modules to let them add to a popup menu.
     * 
     * @param popUpActions Vector of actions
     * @param context which the actions are valid for
     */
    public Vector getModulePopUpActions(Vector popUpActions, Object context);

    /** The module identifying key
     * 
     * @return the string key the module uses to identify itself
     */
    public String getModuleKey();
}

