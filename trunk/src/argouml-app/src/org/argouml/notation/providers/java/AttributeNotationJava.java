/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006-2009 The Regents of the University of California. All
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

package org.argouml.notation.providers.java;

import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoHelpEvent;
import org.argouml.model.Model;
import org.argouml.notation.NotationSettings;
import org.argouml.notation.providers.AttributeNotation;

/**
 * @author michiel
 */
public class AttributeNotationJava extends AttributeNotation {

    /**
     * The constructor.
     * @param attribute the UML object
     */
    public AttributeNotationJava(Object attribute) {
        super(attribute);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
//        return "parsing.java.help.attribute";
        return "Parsing in Java not yet supported";
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        ArgoEventPump.fireEvent(new ArgoHelpEvent(
                ArgoEventTypes.HELP_CHANGED, this,
            "Parsing in Java not yet supported"));
    }

    private String toString(Object modelElement) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(NotationUtilityJava.generateVisibility(modelElement));
        sb.append(NotationUtilityJava.generateScope(modelElement));
        sb.append(NotationUtilityJava.generateChangeability(modelElement));
        Object type = Model.getFacade().getType(modelElement);
        Object multi = Model.getFacade().getMultiplicity(modelElement);
        // handle multiplicity here since we need the type
        // actually the API of generator is buggy since to generate
        // multiplicity correctly we need the attribute too
        if (type != null && multi != null) {
            if (Model.getFacade().getUpper(multi) == 1) {
                sb.append(NotationUtilityJava.generateClassifierRef(type))
                    .append(' ');
            } else if (Model.getFacade().isADataType(type)) {
                sb.append(NotationUtilityJava.generateClassifierRef(type))
                    .append("[] ");
            } else {
                sb.append("java.util.Vector ");
            }
        }

        sb.append(Model.getFacade().getName(modelElement));
        Object init = Model.getFacade().getInitialValue(modelElement);
        if (init != null) {
            String initStr = 
                NotationUtilityJava.generateExpression(init).trim();
            if (initStr.length() > 0) {
                sb.append(" = ").append(initStr);
            }
        }

        return sb.toString();
    }

    @Override
    public String toString(Object modelElement, NotationSettings settings) {
        return toString(modelElement);
    }

}
