// $Id: eclipse-argo-codetemplates.xml 11347 2006-10-26 22:37:44Z linus $
// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.uml.wfr;

import org.argouml.cognitive.ToDoItem;

/**
 * Represents a WellFormedneddRule (WFR) for a UML2 model
 *
 * @author maurelio1234
 */
public abstract class WellFormednessRule {

    /**
     * Enables or disables this WFR
     */
    public boolean enabled = true;
        
    /**
     * This WFR can be automatically corrected
     */
    public boolean canAutomate;

    /**
     * The user allows automatic corrections
     */
    public boolean automateAllowed;
    
    /**
     * The edition of the user model is restricted
     */
    public boolean blockEdition;
 
    /**
     * A description for this WFR
     */
    public String description;
    
    /**
     * A name for this WFR
     */
    public String name;
    
    public abstract boolean  doCheck(Object reason);
    public abstract void     doAutomate(Object reason);
    public abstract ToDoItem doNotify();
    public abstract boolean  isInterestedIn(Object modelElement);
    
    /**
     * @return Returns the automateAllowed.
     */
    public boolean isAutomateAllowed() {
        return automateAllowed;
    }
    /**
     * @param automateAllowed The automateAllowed to set.
     */
    public void setAutomateAllowed(boolean automateAllowed) {
        this.automateAllowed = automateAllowed;
    }
    /**
     * @return Returns the blockEdition.
     */
    public boolean isBlockEdition() {
        return blockEdition;
    }
    /**
     * @param blockEdition The blockEdition to set.
     */
    public void setBlockEdition(boolean blockEdition) {
        this.blockEdition = blockEdition;
    }
    /**
     * @return Returns the canAutomate.
     */
    public boolean isCanAutomate() {
        return canAutomate;
    }
    /**
     * @param canAutomate The canAutomate to set.
     */
    public void setCanAutomate(boolean canAutomate) {
        this.canAutomate = canAutomate;
    }
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
        
}