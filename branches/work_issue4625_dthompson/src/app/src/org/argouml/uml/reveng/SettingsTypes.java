// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.uml.reveng;

import java.util.List;

/**
 * These are common raw types that a specific import type can use to
 * build complex settings. If additional types are required then this
 * interface should be extended.
 * @author Bogdan Pistol
 */
public interface SettingsTypes {

    /**
     * Base setting class extended by all others
     */
    interface Setting {
        /**
         * Returns the string to use as a label for the associated
         * setting or setting group.  The implementor is responsible for
         * translation into the local language.
         * 
         * @return the String message
         */
        String getLabel();
    }

    /**
     * A generic type that has multiple options, from all these options
     * the user can choose only one option (the selected option).
     * <p>
     * There can be a default pre-selected option.
     */
    interface UniqueSelection extends Setting {

        public int UNDEFINED_SELECTION = -1;

        /**
         * Returns the available options from wich the user can pick one.
         * 
         * @return a list with Strings that identinfies the options
         */
        List getOptions();

        /**
         * This is the default selected option, if the user doesn't choose other
         * option then this will be the selected option.
         * 
         * @return the 0 based index of the default option as is in the list
         *         returned by
         *         {@link SettingsTypes.UniqueSelection#getOptions()} or
         *         UNDEFINED_SELECTION if there is no default option
         */
        int getDefaultSelection();

        /**
         * This is how the user can choose an option.
         * 
         * @param selection
         *            the 0 based index of the default option as is in the list
         *            returned by
         *            {@link SettingsTypes.UniqueSelection#getOptions()}
         * @return true if was successful or false if the selection is out of
         *         bounds
         */
        boolean setSelection(int seletion);
    }
    
    /**
     * Free form string setting to allow user to enter arbitrary string value.
     */
    interface UserString extends Setting {
        /**
         * @return the initial string to display, if any.  May be null.
         */
        String getDefaultString();
        /**
         * @return the user entered string
         */
        String getUserString();
    }
    
    /**
     * Boolean setting which can take values of true/false (on/off).
     */
    interface BooleanSelection extends Setting {
        /**
         * @return the default setting to use when first displayed.
         */
        boolean getDefaultValue();
        /**
         * @return the user selected value
         */
        boolean isSelected();
    }

}
