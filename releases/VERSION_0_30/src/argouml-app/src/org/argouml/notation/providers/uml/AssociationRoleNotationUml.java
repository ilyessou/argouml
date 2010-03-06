/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
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

package org.argouml.notation.providers.uml;

import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoHelpEvent;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.notation.NotationSettings;
import org.argouml.notation.providers.AssociationRoleNotation;
import org.argouml.util.MyTokenizer;

/**
 * The UML notation for an AssociationRole.
 * 
 * @author michiel
 */
public class AssociationRoleNotationUml extends AssociationRoleNotation {

    /**
     * The constructor.
     * 
     * @param role the given association-role
     */
    public AssociationRoleNotationUml(Object role) {
        super(role);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-association-role";
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        try {
            parseRole(modelElement, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.association-role";
            Object[] args = {
                pe.getLocalizedMessage(),
                Integer.valueOf(pe.getErrorOffset()),
            };
            ArgoEventPump.fireEvent(new ArgoHelpEvent(
                    ArgoEventTypes.HELP_CHANGED, this,
                Translator.messageFormat(msg, args)));
        }
    }

    /**
     * Parse the string that represents an AssociationRole: <pre>
     *     ["/" name] [":" name_of_the_base_association]
     * </pre>
     * 
     * @param role   The AssociationRole <em>text</em> describes.
     * @param text A String on the above format.
     * @throws ParseException
     *             when it detects an error in the role string. See also
     *             ParseError.getErrorOffset().
     */
    protected void parseRole(Object role, String text)
        throws ParseException {
        String token;
        boolean hasColon = false;
        boolean hasSlash = false;
        String rolestr = null;
        String basestr = null;
        
        MyTokenizer st = new MyTokenizer(text, " ,\t,/,:");

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (" ".equals(token) || "\t".equals(token)) {
                /* Do nothing. */
            } else if ("/".equals(token)) {
                hasSlash = true;
                hasColon = false;

            } else if (":".equals(token)) {
                hasColon = true;
                hasSlash = false;
                
            } else if (hasColon) {
                if (basestr != null) {
                    String msg = 
                    	"parsing.error.association-role.association-extra-text";
                    throw new ParseException(Translator.localize(msg), st
                                    .getTokenIndex());
                }
                basestr = token;
            } else if (hasSlash) {
                if (rolestr != null) {
                    String msg = 
                        "parsing.error.association-role.association-extra-text";
                    throw new ParseException(Translator.localize(msg), st
                                    .getTokenIndex());
                }
                rolestr = token;
            } else {
            	String msg = 
                    "parsing.error.association-role.association-extra-text";
                throw new ParseException(Translator.localize(msg), 
                		st.getTokenIndex());
            }
        }
        
        if (basestr == null) {
            /* If no base was typed, then only set the name: */
            if (rolestr != null) {
                Model.getCoreHelper().setName(role, rolestr.trim());
            }
            return;
        }
        /* If the base was not changed, then only set the name: */
        Object currentBase = Model.getFacade().getBase(role);
        if (currentBase != null) {
            String currentBaseStr = Model.getFacade().getName(currentBase);
            if (currentBaseStr == null) {
                /* TODO: Is this needed? */
                currentBaseStr = "";
            }
            if (currentBaseStr.equals(basestr)) {
                if (rolestr != null) {
                    Model.getCoreHelper().setName(role, rolestr.trim());
                }
                return;
            }
        }
        Collection c = 
            Model.getCollaborationsHelper().getAllPossibleBases(role);
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Object candidate = i.next();
            if (basestr.equals(Model.getFacade().getName(candidate))) {
                if (Model.getFacade().getBase(role) != candidate) {
                    /* If the base is already set to this assoc, 
                     * then do not set it again.
                     * This check is needed, otherwise the setbase()
                     *  below gives an exception.*/
                    Model.getCollaborationsHelper().setBase(role, candidate);
                }
                /* Only set the name if the base was found: */
                if (rolestr != null) {
                    Model.getCoreHelper().setName(role, rolestr.trim());
                }
                return;
            }
        }
        String msg = "parsing.error.association-role.base-not-found";
        throw new ParseException(Translator.localize(msg), 0);        
    }

    private String toString(final Object modelElement) {
        //get the associationRole name
        String name = Model.getFacade().getName(modelElement);
        if (name == null) {
            name = "";
        }
        if (name.length() > 0) {
            name = "/" + name;
        }
        //get the base association name
        Object assoc = Model.getFacade().getBase(modelElement);
        if (assoc != null) {
            String baseName = Model.getFacade().getName(assoc);
            if (baseName != null && baseName.length() > 0) {
                name = name + ":" + baseName;
            }
        }
        return name;
    }

    /*
     * Generate the name of an association role of the form:
     *  ["/" name] [":" name_of_the_base_association]
     * <p>
     * Remark: 
     * So, if both names are empty, then nothing is shown! 
     * See issue 2712.
     */
    @Override
    public String toString(final Object modelElement, 
            final NotationSettings settings) {
        return toString(modelElement);
    }

}
