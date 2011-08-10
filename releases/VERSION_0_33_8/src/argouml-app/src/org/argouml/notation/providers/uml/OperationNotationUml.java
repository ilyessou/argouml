/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2009 The Regents of the University of California. All
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

package org.argouml.notation.providers.uml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoHelpEvent;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.argouml.notation.NotationSettings;
import org.argouml.notation.providers.OperationNotation;
import org.argouml.uml.StereotypeUtility;
import org.argouml.util.MyTokenizer;

/**
 * The UML notation for an Operation or a Reception.
 * 
 * @author mvw
 */
public class OperationNotationUml extends OperationNotation {

    private static final String RECEPTION_KEYWORD = "signal";
    
    /**
     * The constructor.
     *
     * @param operation the operation that is represented
     */
    public OperationNotationUml(Object operation) {
        super(operation);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        try {
            parseOperationFig(Model.getFacade().getOwner(modelElement), 
                    modelElement, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.operation";
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
     * Parse a string representing one ore more ';' separated operations. The
     * case that a String or char contains a ';' (e.g. in an initializer) is
     * handled, but not other occurences of ';'.
     *
     * @param classifier  Classifier The classifier the operation(s) belong to
     * @param operation   Operation The operation on which the editing happened
     * @param text The string to parse
     * @throws ParseException for invalid input
     */
    public void parseOperationFig(
            Object classifier,
            Object operation,
            String text) throws ParseException {

        if (classifier == null || operation == null) {
            return;
        }
        ParseException pex = null;
        int start = 0;
        int end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        Project currentProject =
            ProjectManager.getManager().getCurrentProject();
        if (end == -1) {
            //no text? remove op!
            currentProject.moveToTrash(operation);
            return;
        }
        String s = text.substring(start, end).trim();
        if (s.length() == 0) {
            //no non-whitechars in text? remove op!
            currentProject.moveToTrash(operation);
            return;
        }
        parseOperation(s, operation);
        int i = Model.getFacade().getFeatures(classifier).indexOf(operation);
        // check for more operations (';' separated):
        start = end + 1;
        end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        while (end > start && end <= text.length()) {
            s = text.substring(start, end).trim();
            if (s.length() > 0) {
                // yes, there are more:
                Object returnType = currentProject.getDefaultReturnType();
                Object newOp =
                    Model.getCoreFactory()
                        .buildOperation(classifier, returnType);
                if (newOp != null) {
                    try {
                        parseOperation(s, newOp);
                        //newOp.setOwnerScope(op.getOwnerScope()); //
                        //not needed in case of operation
                        if (i != -1) {
                            Model.getCoreHelper().addFeature(
                                    classifier, ++i, newOp);
                        } else {
                            Model.getCoreHelper().addFeature(
                                    classifier, newOp);
                        }
                    } catch (ParseException ex) {
                        if (pex == null) {
                            pex = ex;
                        }
                    }
                }
            }
            start = end + 1;
            end = NotationUtilityUml.indexOfNextCheckedSemicolon(text, start);
        }
        if (pex != null) {
            throw pex;
        }
    }


    /**
     * Parse a line of text and aligns the Operation to the specification
     * given. The line should be on the following form:<ul>
     * <li> visibility name (parameter list) : return-type-expression
     * {property-string}
     * </ul>
     *
     * All elements are optional and, if left unspecified, will preserve their
     * old values.<p>
     * 
     * <em>Stereotypes</em> can be given between any element in the line on the
     * form: &lt;&lt;stereotype1,stereotype2,stereotype3&gt;&gt;<p>
     *
     * The following properties are recognized to have special meaning:
     * abstract, concurrency, concurrent, guarded, leaf, query, root and
     * sequential.<p>
     *
     * This syntax is compatible with the UML 1.3 spec.<p>
     *
     * (formerly visibility name (parameter list) : return-type-expression
     * {property-string} ) (formerly 2nd: [visibility] [keywords] returntype
     * name(params)[;] )
     *
     * @param s   The String to parse.
     * @param op  The Operation to adjust to the specification in s.
     * @throws ParseException
     *             when it detects an error in the attribute string. See also
     *             ParseError.getErrorOffset().
     */
    public void parseOperation(String s, Object op) throws ParseException {
        MyTokenizer st;
        boolean hasColon = false;
        String name = null;
        String parameterlist = null;
        StringBuilder stereotype = null;
        String token;
        String type = null;
        String visibility = null;
        List<String> properties = null;
        int paramOffset = 0;

        s = s.trim();
        if (s.length() > 0 
                && NotationUtilityUml.VISIBILITYCHARS.indexOf(s.charAt(0)) 
                    >= 0) {
            visibility = s.substring(0, 1);
            s = s.substring(1);
        }

        try {
            st = new MyTokenizer(s, " ,\t,<<,\u00AB,\u00BB,>>,:,=,{,},\\,",
                    NotationUtilityUml.operationCustomSep);
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (" ".equals(token) || "\t".equals(token)
                        || ",".equals(token)) {
                    continue; // Do nothing
                } else if ("<<".equals(token) || "\u00AB".equals(token)) {
                    if (stereotype != null) {
                        parseError("operation.stereotypes", 
                                st.getTokenIndex());
                    }
                    stereotype = new StringBuilder();
                    while (true) {
                        token = st.nextToken();
                        if (">>".equals(token) || "\u00BB".equals(token)) {
                            break;
                        }
                        stereotype.append(token);
                    }
                } else if ("{".equals(token)) {
                    properties = tokenOpenBrace(st, properties);
                } else if (":".equals(token)) {
                    hasColon = true;
                } else if ("=".equals(token)) {
                    parseError("operation.default-values", st.getTokenIndex());
                } else if (token.charAt(0) == '(' && !hasColon) {
                    if (parameterlist != null) {
                        parseError("operation.two-parameter-lists", 
                                st.getTokenIndex());
                    }

                    parameterlist = token;
                } else {
                    if (hasColon) {
                        if (type != null) {
                            parseError("operation.two-types", 
                                    st.getTokenIndex());
                        }

                        if (token.length() > 0
                                && (token.charAt(0) == '\"'
                                    || token.charAt(0) == '\'')) {
                            parseError("operation.type-quoted",
                                    st.getTokenIndex());
                        }

                        if (token.length() > 0 && token.charAt(0) == '(') {
                            parseError("operation.type-expr", 
                                    st.getTokenIndex());
                        }

                        type = token;
                    } else {
                        if (name != null && visibility != null) {
                            parseError("operation.extra-text",
                                    st.getTokenIndex());
                        }

                        if (token.length() > 0
                                && (token.charAt(0) == '\"'
                                    || token.charAt(0) == '\'')) {
                            parseError("operation.name-quoted",
                                    st.getTokenIndex());
                        }

                        if (token.length() > 0 && token.charAt(0) == '(') {
                            parseError("operation.name-expr", 
                                    st.getTokenIndex());
                        }

                        if (name == null
                                && visibility == null
                                && token.length() > 1
                                && NotationUtilityUml.VISIBILITYCHARS.indexOf(
                                        token.charAt(0))
                                                    >= 0) {
                            visibility = token.substring(0, 1);
                            token = token.substring(1);
                        }

                        if (name != null) {
                            visibility = name;
                            name = token;
                        } else {
                            name = token;
                        }
                    }
                }
            } // end while loop
        } catch (NoSuchElementException nsee) {
            parseError("operation.unexpected-end-operation", 
                    s.length());
        } catch (ParseException pre) {
            throw pre;
        }
        
        if (parameterlist != null) {
            // parameterlist is guaranteed to contain at least "("
            if (parameterlist.charAt(parameterlist.length() - 1) != ')') {
                parseError("operation.parameter-list-incomplete",
                        paramOffset + parameterlist.length() - 1);
            }

            paramOffset++;
            parameterlist = parameterlist.substring(1,
                    parameterlist.length() - 1);
            NotationUtilityUml.parseParamList(op, parameterlist, paramOffset);
        }

        if (visibility != null) {
            Model.getCoreHelper().setVisibility(op,
                    NotationUtilityUml.getVisibility(visibility.trim()));
        }

        if (name != null) {
            Model.getCoreHelper().setName(op, name.trim());
        } else if (Model.getFacade().getName(op) == null
                || "".equals(Model.getFacade().getName(op))) {
            Model.getCoreHelper().setName(op, "anonymous");
        }

        if (type != null) {
            Object ow = Model.getFacade().getOwner(op);
            Object ns = null;
            if (ow != null && Model.getFacade().getNamespace(ow) != null) {
                ns = Model.getFacade().getNamespace(ow);
            } else {
                ns = Model.getFacade().getRoot(op);
            }
            Object mtype = NotationUtilityUml.getType(type.trim(), ns);
            setReturnParameter(op, mtype);
        }

        if (properties != null) {
            NotationUtilityUml.setProperties(op, properties, 
                    NotationUtilityUml.operationSpecialStrings);
        }

        // Don't create a stereotype for <<signal>> on a Reception
        // but create any other parsed stereotypes as needed
        if (!Model.getFacade().isAReception(op) 
                || !RECEPTION_KEYWORD.equals(stereotype.toString())) {
            StereotypeUtility.dealWithStereotypes(op, stereotype, true);
        }
    }

    /**
     * Convenience method to signal a parser error.
     * 
     * @param message
     *            string containing error message literal. It will be appended
     *            to the base "parser.error." and localized.
     * @param offset
     *            offset to where error occurred
     * @throws ParseException
     */
    private void parseError(String message, int offset)
        throws ParseException {

        throw new ParseException(
                Translator.localize("parsing.error." + message), 
                offset);
    }

    /**
     * Parse tokens following an open brace (properties).
     * 
     * @param st tokenizer being used
     * @param properties current properties list
     * @return updated list of properties
     * @throws ParseException
     */
    private List<String> tokenOpenBrace(MyTokenizer st, List<String> properties)
        throws ParseException {
        String token;
        StringBuilder propname = new StringBuilder();
        String propvalue = null;

        if (properties == null) {
            properties = new ArrayList<String>();
        }
        while (true) {
            token = st.nextToken();
            if (",".equals(token) || "}".equals(token)) {
                if (propname.length() > 0) {
                    properties.add(propname.toString());
                    properties.add(propvalue);
                }
                propname = new StringBuilder();
                propvalue = null;

                if ("}".equals(token)) {
                    break;
                }
            } else if ("=".equals(token)) {
                if (propvalue != null) {
                    String msg = 
                        "parsing.error.operation.prop-stereotypes";
                    Object[] args = {propname};
                    throw new ParseException(
                    		Translator.localize(msg, 
                            args), 
                            st.getTokenIndex());
                }
                propvalue = "";
            } else if (propvalue == null) {
                propname.append(token);
            } else {
                propvalue += token;
            }
        }
        if (propname.length() > 0) {
            properties.add(propname.toString());
            properties.add(propvalue);
        }
        return properties;
    }


    /**
     * Sets the return parameter of op to be of type type. If there is none, one
     * is created. If there are many, all but one are removed.
     *
     * @param op the operation
     * @param type the type of the return parameter
     */
    private void setReturnParameter(Object op, Object type) {
        Object param = null;
        Iterator it = Model.getFacade().getParameters(op).iterator();
        while (it.hasNext()) {
            Object p = it.next();
            if (Model.getFacade().isReturn(p)) {
                param = p;
                break;
            }
        }
        while (it.hasNext()) {
            Object p = it.next();
            if (Model.getFacade().isReturn(p)) {
                ProjectManager.getManager().getCurrentProject().moveToTrash(p);
            }
        }
        if (param == null) {
            Object returnType =
                ProjectManager.getManager()
                        .getCurrentProject().getDefaultReturnType();
            param = Model.getCoreFactory().buildParameter(op, returnType);
        }
        Model.getCoreHelper().setType(param, type);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.operation";
    }

    /**
     * Generate an operation according to the UML notation:
     * <pre>
     *         stereotype visibility name (parameter-list) :
     *                         return-type-expression {property-string}
     * </pre>
     * For the return-type-expression: only the types of the return parameters
     * are shown.  Depending on settings in Notation, visibility and
     * properties are shown/not shown.
     * 
     * @param modelElement UML Operation element
     * @param settings notation settings
     * @return a formatted text string
     * @see org.argouml.notation.NotationProvider#toString(java.lang.Object, org.argouml.notation.NotationSettings)
     */
    public String toString(Object modelElement, NotationSettings settings) {
        return toString(modelElement, settings.isUseGuillemets(), 
                settings.isShowVisibilities(), settings.isShowTypes(),
                settings.isShowProperties());
    }
    
    /**
     * Generate an operation according to the UML notation:
     * <pre>
     *         stereotype visibility name (parameter-list) :
     *                         return-type-expression {property-string}
     * </pre>
     * For the return-type-expression: only the types of the return parameters
     * are shown.  Depending on settings in Notation, visibility and
     * properties are shown/not shown.
     *
     * @author jaap.branderhorst@xs4all.nl
     */
    private String toString(Object modelElement, boolean useGuillemets, 
            boolean showVisibility,
            boolean showTypes, boolean showProperties) {
        try {
            String stereoStr = NotationUtilityUml.generateStereotype(
                    Model.getFacade().getStereotypes(modelElement), 
                    useGuillemets);
            boolean isReception = Model.getFacade().isAReception(modelElement);
            // TODO: needs I18N
            if (isReception) {
                stereoStr =
                        NotationUtilityUml
                                .generateStereotype(RECEPTION_KEYWORD, 
                                        useGuillemets)
                                + " " + stereoStr;
            }

            // Unused currently
//            StringBuffer taggedValuesSb = getTaggedValues(modelElement);
            
            // lets concatenate it to the resulting string (genStr)
            StringBuffer genStr = new StringBuffer(30);
            if ((stereoStr != null) && (stereoStr.length() > 0)) {
                genStr.append(stereoStr).append(" ");
            }
            if (showVisibility) {
                String visStr = NotationUtilityUml
                        .generateVisibility2(modelElement);
                if (visStr != null) {
                    genStr.append(visStr);
                }
            }
            
            String nameStr = Model.getFacade().getName(modelElement);
            if ((nameStr != null) && (nameStr.length() > 0)) {
                genStr.append(nameStr);
            }
            
            /* The "show types" defaults to TRUE, to stay compatible with older
             * ArgoUML versions that did not have this setting: */
            if (showTypes) {
                // the parameters
                StringBuffer parameterStr = new StringBuffer();
                parameterStr.append("(").append(getParameterList(modelElement))
                        .append(")");

                // the returnparameters
                StringBuffer returnParasSb = getReturnParameters(modelElement,
                        isReception);
                genStr.append(parameterStr).append(" ");
                if ((returnParasSb != null) && (returnParasSb.length() > 0)) {
                    genStr.append(returnParasSb).append(" ");
                }
            } else {
                genStr.append("()");
            }
            if (showProperties) {
                StringBuffer propertySb = getProperties(modelElement,
                        isReception);
                if (propertySb.length() > 0) {
                    genStr.append(propertySb);
                }
            }
            return genStr.toString().trim();
        } catch (InvalidElementException e) {
            // The model element was deleted while we were working on it
            return "";   
        }

    }


    private StringBuffer getParameterList(Object modelElement) {
        StringBuffer parameterListBuffer = new StringBuffer();
        Collection coll = Model.getFacade().getParameters(modelElement);
        Iterator it = coll.iterator();
        int counter = 0;
        while (it.hasNext()) {
            Object parameter = it.next();
            if (!Model.getFacade().hasReturnParameterDirectionKind(
                    parameter)) {
                counter++;
                parameterListBuffer.append(
                        NotationUtilityUml.generateParameter(parameter));
                parameterListBuffer.append(",");
            }
        }
        if (counter > 0) {
            parameterListBuffer.delete(
                    parameterListBuffer.length() - 1,
                    parameterListBuffer.length());
        }
        return parameterListBuffer;
    }

    private StringBuffer getReturnParameters(Object modelElement,
            boolean isReception) {
        StringBuffer returnParasSb = new StringBuffer();
        if (!isReception) {
            Collection coll = 
                Model.getCoreHelper().getReturnParameters(modelElement);
            if (coll != null && coll.size() > 0) {
                returnParasSb.append(": ");
                Iterator it2 = coll.iterator();
                while (it2.hasNext()) {
                    Object type = Model.getFacade().getType(it2.next());
                    if (type != null) {
                        returnParasSb.append(Model.getFacade()
                                .getName(type));
                    }
                    returnParasSb.append(",");
                }
                // if we have only one return value and without type,
                // the return param string is ": ,", we remove it
                if (returnParasSb.length() == 3) {
                    returnParasSb.delete(0, returnParasSb.length());
                }
                // else: we remove only the extra ","
                else {
                    returnParasSb.delete(
                            returnParasSb.length() - 1,
                            returnParasSb.length());
                }
            }
        }
        return returnParasSb;
    }
    
    
    private StringBuffer getProperties(Object modelElement, 
            boolean isReception) {
        StringBuffer propertySb = new StringBuffer().append("{");
        // the query state
        if (Model.getFacade().isQuery(modelElement)) {
            propertySb.append("query,");
        }
        /*
         * Although Operation and Signal are peers in the UML type 
         * hierarchy they share the attributes isRoot, isLeaf, 
         * isAbstract, and  specification. Concurrency is *not* 
         * shared and is specific to Operation.
         */
        if (Model.getFacade().isRoot(modelElement)) {
            propertySb.append("root,");
        }
        if (Model.getFacade().isLeaf(modelElement)) {
            propertySb.append("leaf,");
        }
        if (!isReception) {
            if (Model.getFacade().getConcurrency(modelElement) != null) {
                propertySb.append(Model.getFacade().getName(
                        Model.getFacade().getConcurrency(modelElement)));
                propertySb.append(',');
            }
        }
        if (propertySb.length() > 1) {
            propertySb.delete(propertySb.length() - 1, propertySb.length());
            // remove last ,
            propertySb.append("}");
        } else {
            propertySb = new StringBuffer();
        }
        return propertySb;
    }


    private StringBuffer getTaggedValues(Object modelElement) {
        StringBuffer taggedValuesSb = new StringBuffer();
        Iterator it3 = Model.getFacade().getTaggedValues(modelElement);
        if (it3 != null && it3.hasNext()) {
            while (it3.hasNext()) {
                taggedValuesSb.append(
                        NotationUtilityUml.generateTaggedValue(it3.next()));
                taggedValuesSb.append(",");
            }
            taggedValuesSb.delete(
                    taggedValuesSb.length() - 1,
                    taggedValuesSb.length());
        }
        return taggedValuesSb;
    }
}
