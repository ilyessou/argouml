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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectSettings;
import org.argouml.model.Facade;
import org.argouml.model.Model;
import org.argouml.uml.StereotypeUtility;
import org.argouml.util.CustomSeparator;
import org.argouml.util.MyTokenizer;

/**
 * This class is a utility for the UML notation.
 *
 * @author Michiel van der Wulp
 */
public final class NotationUtilityUml {
    /**
     * The array of special properties for attributes.
     */
    static PropertySpecialString[] attributeSpecialStrings;

    /**
     * The list of CustomSeparators to use when tokenizing attributes.
     */
    static List<CustomSeparator> attributeCustomSep;

    /**
     * The array of special properties for operations.
     */
    static PropertySpecialString[] operationSpecialStrings;

    /**
     * The List of CustomSeparators to use when tokenizing attributes.
     */
    static final List<CustomSeparator> operationCustomSep;

    /**
     * The list of CustomSeparators to use when tokenizing parameters.
     */
    private static final List<CustomSeparator> parameterCustomSep;

    private static final String LIST_SEPARATOR = ", ";

    /**
     * The character with a meaning as a visibility at the start
     * of an attribute.
     */
    static final String VISIBILITYCHARS = "+#-~";

    /**
     * The constructor.
     */
    public NotationUtilityUml() { }

    /* TODO: Can we put the static block within the init()? */
    static {
        attributeSpecialStrings = new PropertySpecialString[2];

        attributeCustomSep = new ArrayList<CustomSeparator>();
        attributeCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
        attributeCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
        attributeCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);

        operationSpecialStrings = new PropertySpecialString[8];

        operationCustomSep = new ArrayList<CustomSeparator>();
        operationCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
        operationCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
        operationCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);

        parameterCustomSep = new ArrayList<CustomSeparator>();
        parameterCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
        parameterCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
        parameterCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);
    }

    static void init() {
        int assPos = 0;
        attributeSpecialStrings[assPos++] =
            new PropertySpecialString("frozen",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        if (Model.getFacade().isAStructuralFeature(element)) {
                            if (value == null) { 
                                /* the text was: {frozen} */
                                Model.getCoreHelper().setReadOnly(element, true);
                            } else if ("false".equalsIgnoreCase(value)) {
                                /* the text was: {frozen = false} */
                                Model.getCoreHelper().setReadOnly(element, false);
                            } else if ("true".equalsIgnoreCase(value)) {
                                /* the text was: {frozen = true} */
                                Model.getCoreHelper().setReadOnly(element, true);
                            }
                        }
                    }
                });
        
        // TODO: AddOnly has been removed in UML 2.x, so we should phase out
        // support of it - tfm - 20070529
        attributeSpecialStrings[assPos++] =
            new PropertySpecialString("addonly",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        if (Model.getFacade().isAStructuralFeature(element)) {
                            if ("false".equalsIgnoreCase(value)) {
                                Model.getCoreHelper().setReadOnly(element, true);
                            } else {
                                Model.getCoreHelper().setChangeability(element,
                                    Model.getChangeableKind().getAddOnly());
                            }
                        }
                    }
                });

        assert assPos == attributeSpecialStrings.length;

        operationSpecialStrings = new PropertySpecialString[8];
        int ossPos = 0;
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("sequential",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setConcurrency(element,
                                Model.getConcurrencyKind().getSequential());
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("guarded",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        Object kind = Model.getConcurrencyKind().getGuarded();
                        if (value != null && value.equalsIgnoreCase("false")) {
                            kind = Model.getConcurrencyKind().getSequential();
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setConcurrency(element, kind);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("concurrent",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        Object kind =
                            Model.getConcurrencyKind().getConcurrent();
                        if (value != null && value.equalsIgnoreCase("false")) {
                            kind = Model.getConcurrencyKind().getSequential();
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setConcurrency(element, kind);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("concurrency",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        Object kind =
                            Model.getConcurrencyKind().getSequential();
                        if ("guarded".equalsIgnoreCase(value)) {
                            kind = Model.getConcurrencyKind().getGuarded();
                        } else if ("concurrent".equalsIgnoreCase(value)) {
                            kind = Model.getConcurrencyKind().getConcurrent();
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setConcurrency(element, kind);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("abstract",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        boolean isAbstract = true;
                        if (value != null && value.equalsIgnoreCase("false")) {
                            isAbstract = false;
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setAbstract(
                                    element,
                                    isAbstract);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("leaf",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        boolean isLeaf = true;
                        if (value != null && value.equalsIgnoreCase("false")) {
                            isLeaf = false;
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setLeaf(element, isLeaf);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("query",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        boolean isQuery = true;
                        if (value != null && value.equalsIgnoreCase("false")) {
                            isQuery = false;
                        }
                        if (Model.getFacade().isABehavioralFeature(element)) {
                            Model.getCoreHelper().setQuery(element, isQuery);
                        }
                    }
                });
        operationSpecialStrings[ossPos++] =
            new PropertySpecialString("root",
                new PropertyOperation() {
                    public void found(Object element, String value) {
                        boolean isRoot = true;
                        if (value != null && value.equalsIgnoreCase("false")) {
                            isRoot = false;
                        }
                        if (Model.getFacade().isAOperation(element)) {
                            Model.getCoreHelper().setRoot(element, isRoot);
                        }
                    }
                });

        assert ossPos == operationSpecialStrings.length;
    }

    /**
     * Parse a string on the format:
     * <pre>
     *     [ &lt;&lt; stereotype &gt;&gt;] [+|-|#|~] [full_pathname ::] [name]
     * </pre>
     * 
     * @param me   The ModelElement <em>text</em> describes.
     * @param text A String on the above format.
     * @throws ParseException
     *             when it detects an error in the attribute string. See also
     *             ParseError.getErrorOffset().
     */
    protected static void parseModelElement(Object me, String text)
        throws ParseException {
        MyTokenizer st;

        List<String> path = null;
        String name = null;
        StringBuilder stereotype = null;
        String token;

        try {
            st = new MyTokenizer(text, "<<,\u00AB,\u00BB,>>,::");
            while (st.hasMoreTokens()) {
                token = st.nextToken();

                if ("<<".equals(token) || "\u00AB".equals(token)) {
                    if (stereotype != null) {
                        String msg = 
                            "parsing.error.model-element-name.twin-stereotypes";
                        throw new ParseException(Translator.localize(msg),
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
                } else if ("::".equals(token)) {
                    if (name != null) {
                        name = name.trim();
                    }

                    if (path != null && (name == null || "".equals(name))) {
                        String msg = 
                            "parsing.error.model-element-name.anon-qualifiers";
                        throw new ParseException(Translator.localize(msg), 
                                st.getTokenIndex());
                    }

                    if (path == null) {
                        path = new ArrayList<String>();
                    }
                    if (name != null) {
                        path.add(name);
                    }
                    name = null;
                } else {
                    if (name != null) {
                        String msg = 
                            "parsing.error.model-element-name.twin-names";
                        throw new ParseException(Translator.localize(msg), 
                                st.getTokenIndex());
                    }

                    name = token;
                }
            }
        } catch (NoSuchElementException nsee) {
            String msg = 
                "parsing.error.model-element-name.unexpected-name-element";
            throw new ParseException(Translator.localize(msg),
                    text.length());
        } catch (ParseException pre) {
            throw pre;
        }

        if (name != null) {
            name = name.trim();
        }

        if (path != null && (name == null || "".equals(name))) {
            String msg = "parsing.error.model-element-name.must-end-with-name";
            throw new ParseException(Translator.localize(msg), 0);
        }

        if (name != null && name.startsWith("+")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(me,
                            Model.getVisibilityKind().getPublic());
        }
        if (name != null && name.startsWith("-")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(me,
                            Model.getVisibilityKind().getPrivate());
        }
        if (name != null && name.startsWith("#")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(me,
                            Model.getVisibilityKind().getProtected());
        }
        if (name != null && name.startsWith("~")) {
            name = name.substring(1).trim();
            Model.getCoreHelper().setVisibility(me,
                            Model.getVisibilityKind().getPackage());
        }
        if (name != null) {
            Model.getCoreHelper().setName(me, name);
        }

        StereotypeUtility.dealWithStereotypes(me, stereotype, false);

        if (path != null) {
            Object nspe =
                Model.getModelManagementHelper().getElement(
                        path,
                        Model.getFacade().getRoot(me));

            if (nspe == null || !(Model.getFacade().isANamespace(nspe))) {
                String msg = 
                        "parsing.error.model-element-name.namespace-unresolved";
                throw new ParseException(Translator.localize(msg), 
                        0);
            }
            if (!Model.getCoreHelper().isValidNamespace(me, nspe)) {
                String msg = 
                        "parsing.error.model-element-name.namespace-invalid";
                throw new ParseException(Translator.localize(msg), 
                        0);
            }

            Model.getCoreHelper().addOwnedElement(nspe, me);
        }
    }
    
    /**
     * Utility function to determine the presence of a key. 
     * The default is false.
     * 
     * @param key the string for the key
     * @param map the Map to check for the presence 
     * and value of the key
     * @return true if the value for the key is true, otherwise false
     */
    public static boolean isValue(final String key, final Map map) {
        if (map == null) {
            return false;
        }
        Object o = map.get(key);
        if (!(o instanceof Boolean)) {
            return false;
        }
        return ((Boolean) o).booleanValue();
    }
    
    /**
     * Returns a visibility String either for a VisibilityKind or a model
     * element.
     * 
     * @param o a modelelement or a visibilitykind
     * @return a string. May be the empty string, but guaranteed not to be null
     */
    public static String generateVisibility2(Object o) {
        if (o == null) {
            return "";
        }
        if (Model.getFacade().isANamedElement(o)) {
            if (Model.getFacade().isPublic(o)) {
                return "+";
            }
            if (Model.getFacade().isPrivate(o)) {
                return "-";
            }
            if (Model.getFacade().isProtected(o)) {
                return "#";
            }
            if (Model.getFacade().isPackage(o)) {
                return "~";
            }
        }
        if (Model.getFacade().isAVisibilityKind(o)) {
            if (Model.getVisibilityKind().getPublic().equals(o)) {
                return "+";
            }
            if (Model.getVisibilityKind().getPrivate().equals(o)) {
                return "-";
            }
            if (Model.getVisibilityKind().getProtected().equals(o)) {
                return "#";
            }
            if (Model.getVisibilityKind().getPackage().equals(o)) {
                return "~";
            }
        }
        return "";
    }

    /**
     * @param modelElement the UML element to generate for
     * @return a string which represents the path
     */
    protected static String generatePath(Object modelElement) {
        StringBuilder s = new StringBuilder();
        Object p = modelElement;
        Stack<String> stack = new Stack<String>();
        Object ns = Model.getFacade().getNamespace(p);
        while (ns != null && !Model.getFacade().isAModel(ns)) {
            stack.push(Model.getFacade().getName(ns));
            ns = Model.getFacade().getNamespace(ns);
        }
        while (!stack.isEmpty()) {
            s.append(stack.pop() + "::");
        }

        if (s.length() > 0 && !(s.lastIndexOf(":") == s.length() - 1)) {
            s.append("::");
        }
        return s.toString();
    }

    /**
     * Parses a parameter list and aligns the parameter list in op to that
     * specified in param. A parameter list generally has the following syntax:
     *
     * <pre>
     * param := [inout] [name] [: type] [= initial value]
     * list := [param] [, param]*
     * </pre>
     *
     * <code>inout</code> is optional and if omitted the old value preserved.
     * If no value has been assigned, then <code>in </code> is assumed.<p>
     *
     * <code>name</code>, <code>type</code> and <code>initial value</code>
     * are optional and if omitted the old value preserved.<p>
     *
     * <code>type</code> and <code>initial value</code> can be given
     * in any order.<p>
     *
     * Unspecified properties is carried over by position, so if a parameter is
     * inserted into the list, then it will inherit properties from the
     * parameter that was there before for unspecified properties.<p>
     *
     * This syntax is compatible with the UML 1.3 specification.
     *
     * @param op
     *            The operation the parameter list belongs to.
     * @param param
     *            The parameter list, without enclosing parentheses.
     * @param paramOffset
     *            The offset to the beginning of the parameter list. Used for
     *            error reports.
     * @throws java.text.ParseException
     *             when it detects an error in the attribute string. See also
     *             ParseError.getErrorOffset().
     */
    static void parseParamList(Object op, String param, int paramOffset)
        throws ParseException {
        MyTokenizer st =
            new MyTokenizer(param, " ,\t,:,=,\\,", parameterCustomSep);
        // Copy returned parameters because it will be a live collection for MDR
        Collection origParam =
            new ArrayList(Model.getFacade().getParameters(op));
        Object ns = Model.getFacade().getRoot(op);
        if (Model.getFacade().isAOperation(op)) {
            Object ow = Model.getFacade().getOwner(op);

            if (ow != null && Model.getFacade().getNamespace(ow) != null) {
                ns = Model.getFacade().getNamespace(ow);
            }
        }

        Iterator it = origParam.iterator();
        while (st.hasMoreTokens()) {
            String kind = null;
            String name = null;
            String tok;
            String type = null;
            StringBuilder value = null;
            Object p = null;
            boolean hasColon = false;
            boolean hasEq = false;

            while (it.hasNext() && p == null) {
                p = it.next();
                if (Model.getFacade().isReturn(p)) {
                    p = null;
                }
            }

            while (st.hasMoreTokens()) {
                tok = st.nextToken();

                if (",".equals(tok)) {
                    break;
                } else if (" ".equals(tok) || "\t".equals(tok)) {
                    if (hasEq) {
                        value.append(tok);
                    }
                } else if (":".equals(tok)) {
                    hasColon = true;
                    hasEq = false;
                } else if ("=".equals(tok)) {
                    if (value != null) {
                    	String msg =
                            "parsing.error.notation-utility.two-default-values";
                        throw new ParseException(Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }
                    hasEq = true;
                    hasColon = false;
                    value = new StringBuilder();
                } else if (hasColon) {
                    if (type != null) {
                        String msg = "parsing.error.notation-utility.two-types";
                        throw new ParseException(Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    if (tok.charAt(0) == '\'' || tok.charAt(0) == '\"') {
                        String msg =
                            "parsing.error.notation-utility.type-quoted";
                        throw new ParseException(Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    if (tok.charAt(0) == '(') {
                        String msg =
                            "parsing.error.notation-utility.type-expr";
                        throw new ParseException(Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    type = tok;
                } else if (hasEq) {
                    value.append(tok);
                } else {
                    if (name != null && kind != null) {
                        String msg =
                            "parsing.error.notation-utility.extra-text";
                        throw new ParseException(Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    if (tok.charAt(0) == '\'' || tok.charAt(0) == '\"') {
                        String msg =
                            "parsing.error.notation-utility.name-kind-quoted";
                        throw new ParseException(
                                Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    if (tok.charAt(0) == '(') {
                        String msg =
                            "parsing.error.notation-utility.name-kind-expr";
                        throw new ParseException(
                                Translator.localize(msg),
                                paramOffset + st.getTokenIndex());
                    }

                    kind = name;
                    name = tok;
                }
            }

            if (p == null) {
                /* Leave the type undefined (see issue 6145): */
                p = Model.getCoreFactory().buildParameter(op, null);
            }

            if (name != null) {
                Model.getCoreHelper().setName(p, name.trim());
            }

            if (kind != null) {
                setParamKind(p, kind.trim());
            }

            if (type != null) {
                Model.getCoreHelper().setType(p, getType(type.trim(), ns));
            }

            if (value != null) {
                // TODO: Find a better default language
                // TODO: We should know the notation language, since it is us
                Project project =
                    ProjectManager.getManager().getCurrentProject();
                ProjectSettings ps = project.getProjectSettings();
                String notationLanguage = ps.getNotationLanguage();

                Object initExpr =
                    Model.getDataTypesFactory()
                        .createExpression(
                                notationLanguage,
                                value.toString().trim());
                Model.getCoreHelper().setDefaultValue(p, initExpr);
            }
        }

        while (it.hasNext()) {
            Object p = it.next();
            if (!Model.getFacade().isReturn(p)) {
                Model.getCoreHelper().removeParameter(op, p);
                Model.getUmlFactory().delete(p);
            }
        }
    }

    /**
     * Set a parameters kind according to a string description of
     * that kind.
     * @param parameter the parameter
     * @param description the string description
     */
    private static void setParamKind(Object parameter, String description) {
        Object kind;
        if ("out".equalsIgnoreCase(description)) {
            kind = Model.getDirectionKind().getOutParameter();
        } else if ("inout".equalsIgnoreCase(description)) {
            kind = Model.getDirectionKind().getInOutParameter();
        } else {
            kind = Model.getDirectionKind().getInParameter();
        }
        Model.getCoreHelper().setKind(parameter, kind);
    }

    /**
     * Finds the classifier associated with the type named in name.
     *
     * @param name
     *            The name of the type to get.
     * @param defaultSpace
     *            The default name-space to place the type in.
     * @return The classifier associated with the name.
     */
    static Object getType(String name, Object defaultSpace) {
        Object type = null;
        Project p = ProjectManager.getManager().getCurrentProject();
        // Should we be getting this from the GUI? BT 11 aug 2002
        type = p.findType(name, false);
        if (type == null) { // no type defined yet
            type = Model.getCoreFactory().buildClass(name,
                    defaultSpace);
        }
        return type;
    }

    /**
     * Applies a List of name/value pairs of properties to a model element.
     * The name is treated as the tag of a tagged value unless it is one of the
     * PropertySpecialStrings, in which case the action of the
     * PropertySpecialString is invoked.
     *
     * @param elem
     *            An model element to apply the properties to.
     * @param prop
     *            A List with name, value pairs of properties.
     * @param spec
     *            An array of PropertySpecialStrings to use.
     */
    static void setProperties(Object elem, List<String> prop,
            PropertySpecialString[] spec) {
        String name;
        String value;
        int i, j;

    nextProp:
        for (i = 0; i + 1 < prop.size(); i += 2) {
            name = prop.get(i);
            value = prop.get(i + 1);

            if (name == null) {
                continue;
            }

            name = name.trim();
            if (value != null) {
                value = value.trim();
            }

            /* If the current property occurs a second time
             * in the given list of properties, then skip it: */
            for (j = i + 2; j < prop.size(); j += 2) {
                String s = prop.get(j);
                if (s != null && name.equalsIgnoreCase(s.trim())) {
                    continue nextProp;
                }
            }

            if (spec != null) {
                for (j = 0; j < spec.length; j++) {
                    if (spec[j].invoke(elem, name, value)) {
                        continue nextProp;
                    }
                }
            }

            Model.getCoreHelper().setTaggedValue(elem, name, value);
        }
    }

    /**
     * Make the given UML object derived or not. The UML standard 
     * defines "derived" as a tagged value for any ModelElement.
     * 
     * @param umlObject the UML ModelElement to be adapted (null is not 
     * allowed)
     * @param derived boolean flag for derived according the UML standard
     */
    static void setDerived(Object umlObject, boolean derived) {
        /* This code was copied from ActionBooleanTaggedValue: */
        String tagName = Facade.DERIVED_TAG;
        Object taggedValue = Model.getFacade().getTaggedValue(umlObject, tagName);
        if (derived) {
            if (taggedValue == null) {
                /* This automatically pulls in a TagDefinition from the profile: */
                taggedValue =
                        Model.getExtensionMechanismsFactory().buildTaggedValue(
                                tagName, "true");
                /* We need to extend the ExtensionMechanismsFactory so that 
                 * we can replace the above deprecated call with something like this: */
//                Model.getExtensionMechanismsFactory().buildTaggedValue(
//                        tagName, new String[] {"true"}, Model.getFacade().getRoot(umlObject));

                Model.getExtensionMechanismsHelper().addTaggedValue(
                        umlObject, taggedValue);
            } else {
                /* The TV existed, but maybe it was not "true": */
                /* TODO: For UML2: Check if the type of the TV is String. */
                Model.getExtensionMechanismsHelper().setDataValues(
                        taggedValue, new String[] {"true"});
            }
        } else {
            if (taggedValue != null) {
                /* There are 2 possibilities: either (1) we follow the traditional notation 
                 * philosophy, and set the tagged value to false, or (2) we restore to 
                 * the pristine situation and delete the taggedValue (whatever the 
                 * value was). I chose the latter (mvw).*/
                /* This would be solution (1): 
                 * Model.getExtensionMechanismsHelper().setDataValues(
                 *      taggedValue, new String[] {"false"});
                 */
                Model.getUmlFactory().delete(taggedValue);
            }
        }
    }

    /**
     * Interface specifying the operation to take when a
     * PropertySpecialString is matched.
     *
     * @author Michael Stockman
     * @since 0.11.2
     * @see PropertySpecialString
     */
    interface PropertyOperation {
        /**
         * Invoked by PropertySpecialString when it has matched a property name.
         *
         * @param element
         *            The element on which the property was set.
         * @param value
         *            The value of the property,
         *            may be null if no value was given.
         */
        void found(Object element, String value);
    }

    /**
     * Declares a string that should take special action when it is found
     * as a property in
     * {@link ParserDisplay#setProperties ParserDisplay.setProperties}.<p>
     *
     * <em>Example:</em>
     *
     * <pre>
     * attributeSpecialStrings[0] =
     *     new PropertySpecialString(&quot;frozen&quot;,
     *         new PropertyOperation() {
     *             public void found(Object element, String value) {
     *                 if (Model.getFacade().isAStructuralFeature(element))
     *                     Model.getFacade().setChangeable(element,
     *                          (value != null &amp;&amp; value
     *                             .equalsIgnoreCase(&quot;false&quot;)));
     *             }
     *         });
     * </pre>
     *
     * Taken from the (former) ParserDisplay constructor.
     * It creates a PropertySpecialString that is invoken when the String
     * "frozen" is found as a property name. Then
     * the found mehod in the anonymous inner class
     * defined on the 2nd line is invoked and performs
     * a custom action on the element on which the property was
     * specified by the user. In this case it does a setChangeability
     * on an attribute instead of setting a tagged value,
     * which would not have the desired effect.
     *
     * @author Michael Stockman
     * @since 0.11.2
     * @see PropertyOperation
     * @see ParserDisplay#setProperties
     */
    static class PropertySpecialString {
        private String name;

        private PropertyOperation op;

        /**
         * Constructs a new PropertySpecialString that will invoke the
         * action in propop when {@link #invoke(Object, String, String)} is
         * called with name equal to str and then return true from invoke.
         *
         * @param str
         *            The name of this PropertySpecialString.
         * @param propop
         *            An object containing the method to invoke on a match.
         */
        public PropertySpecialString(String str, PropertyOperation propop) {
            name = str;
            op = propop;
        }

        /**
         * Called by {@link NotationUtilityUml#setProperties(Object, 
         * java.util.Vector, PropertySpecialString[])} while 
         * searching for an action to
         * invoke for a property. If it returns true, then setProperties
         * may assume that all required actions have been taken and stop
         * searching.
         *
         * @param pname
         *            The name of a property.
         * @param value
         *            The value of a property.
         * @param element
         *            A model element to apply the properties to.
         * @return <code>true</code> if an action is performed, otherwise
         *         <code>false</code>.
         */
        boolean invoke(Object element, String pname, String value) {
            if (!name.equalsIgnoreCase(pname)) {
                return false;
            }
            op.found(element, value);
            return true;
        }
    }

    /**
     * Checks for ';' in Strings or chars in ';' separated tokens in order to
     * return an index to the next attribute or operation substring, -1
     * otherwise (a ';' inside a String or char delimiters is ignored).
     *
     * @param s The string to search.
     * @param start The position to start at.
     * @return the index to the next attribute
     */
    static int indexOfNextCheckedSemicolon(String s, int start) {
        if (s == null || start < 0 || start >= s.length()) {
            return -1;
        }
        int end;
        boolean inside = false;
        boolean backslashed = false;
        char c;
        for (end = start; end < s.length(); end++) {
            c = s.charAt(end);
            if (!inside && c == ';') {
                return end;
            } else if (!backslashed && (c == '\'' || c == '\"')) {
                inside = !inside;
            }
            backslashed = (!backslashed && c == '\\');
        }
        return end;
    }

    /**
     * Finds a visibility for the visibility specified by name. If no known
     * visibility can be deduced, private visibility is used.
     *
     * @param name
     *            The Java name of the visibility.
     * @return A visibility corresponding to name.
     */
    static Object getVisibility(String name) {
        if ("+".equals(name) || "public".equals(name)) {
            return Model.getVisibilityKind().getPublic();
        } else if ("#".equals(name) || "protected".equals(name)) {
            return Model.getVisibilityKind().getProtected();
        } else if ("~".equals(name) || "package".equals(name)) {
            return Model.getVisibilityKind().getPackage();
        } else {
            /* if ("-".equals(name) || "private".equals(name)) */
            return Model.getVisibilityKind().getPrivate();
        }
    }

    /**
     * Generate the text for one or more stereotype(s).
     * 
     * @param st One of:
     *            <ul>
     *            <li>a stereotype UML object</li>
     *            <li>a string</li>
     *            <li>a collection of stereotypes</li>
     *            <li>a modelelement of which the stereotypes are retrieved</li>
     *            </ul>
     * @param useGuillemets true if Unicode double angle bracket quote
     *            characters should be used.
     * @return fully formatted string with list of stereotypes separated by
     *         commas and surround in brackets
     */
    public static String generateStereotype(Object st, boolean useGuillemets) {
        if (st == null) {
            return "";
        }

        if (st instanceof String) {
            return formatStereotype((String) st, useGuillemets);
        }
        if (Model.getFacade().isAStereotype(st)) {
            return formatStereotype(Model.getFacade().getName(st),
                    useGuillemets);
        }

        if (Model.getFacade().isAModelElement(st)) {
            st = Model.getFacade().getStereotypes(st);
        }
        
        if (st instanceof Collection) {
            String result = null;
            boolean found = false;
            for (Object stereotype : (Collection) st) {
                String name =  Model.getFacade().getName(stereotype);
                if (!found) {
                    result = name;
                    found = true;
                } else {
                    // Allow concatenation order and separator to be localized
                    result = Translator.localize("misc.stereo.concatenate",
                            new Object[] {result, name});
                }
            }
            if (found) {
                return formatStereotype(result, useGuillemets);
            }
        }
        return "";
    }
    
    /**
     * Create a string representation of a stereotype, keyword or comma separate
     * list of names. This method just wraps the string in <<angle brackets>> or
     * guillemets (double angle bracket characters) depending on the setting
     * of the flag <code>useGuillemets</code>.
     * 
     * @param name the name of the stereotype
     * @param useGuillemets true if Unicode double angle bracket quote
     *            characters should be used.
     * @return the string representation
     */
    public static String formatStereotype(String name, boolean useGuillemets) {
        if (name == null || name.length() == 0) {
            return "";
        }

        String key = "misc.stereo.guillemets."
                + Boolean.toString(useGuillemets);
        return Translator.localize(key, new Object[] {name});
    }
    

    /**
     * Generates the representation of a parameter on the display (diagram). The
     * string to be returned will have the following syntax:
     * <p>
     * 
     * kind name : type-expression = default-value
     * 
     * @see org.argouml.notation.NotationProvider2#generateParameter(java.lang.Object)
     */
    static String generateParameter(Object parameter) {
        StringBuffer s = new StringBuffer();
        s.append(generateKind(Model.getFacade().getKind(parameter)));
        if (s.length() > 0) {
            s.append(" ");
        }
        s.append(Model.getFacade().getName(parameter));
        String classRef =
            generateClassifierRef(Model.getFacade().getType(parameter));
        if (classRef.length() > 0) {
            s.append(" : ");
            s.append(classRef);
        }
        String defaultValue =
            generateExpression(Model.getFacade().getDefaultValue(parameter));
        if (defaultValue.length() > 0) {
            s.append(" = ");
            s.append(defaultValue);
        }
        return s.toString();
    }

    private static String generateExpression(Object expr) {
        if (Model.getFacade().isAExpression(expr)) {
            return generateUninterpreted(
                    (String) Model.getFacade().getBody(expr));
        } else if (Model.getFacade().isAConstraint(expr)) {
            return generateExpression(Model.getFacade().getBody(expr));
        }
        return "";
    }

    private static String generateUninterpreted(String un) {
        if (un == null) {
            return "";
        }
        return un;
    }

    private static String generateClassifierRef(Object cls) {
        if (cls == null) {
            return "";
        }
        return Model.getFacade().getName(cls);
    }

    private static String generateKind(Object /*Parameter etc.*/ kind) {
        StringBuffer s = new StringBuffer();
        // TODO: I18N
        if (kind == null /* "in" is the default */
                || kind == Model.getDirectionKind().getInParameter()) {
            s.append(/*"in"*/ ""); /* See issue 3421. */
        } else if (kind == Model.getDirectionKind().getInOutParameter()) {
            s.append("inout");
        } else if (kind == Model.getDirectionKind().getReturnParameter()) {
            // return nothing
        } else if (kind == Model.getDirectionKind().getOutParameter()) {
            s.append("out");
        }
        return s.toString();
    }

    /**
     * @param tv a tagged value
     * @return a string that represents the tagged value
     */
    static String generateTaggedValue(Object tv) {
        if (tv == null) {
            return "";
        }
        return Model.getFacade().getTagOfTag(tv)
            + "="
            + generateUninterpreted(Model.getFacade().getValueOfTag(tv));
    }

    /**
     * Generate the text of a multiplicity.
     * 
     * @param element a multiplicity or an element which has a multiplicity
     * @param showSingularMultiplicity if false return the empty string for 1..1
     *            multiplicities.
     * @return a string containing the formatted multiplicity,
     * or the empty string
     */
    public static String generateMultiplicity(Object element, 
            boolean showSingularMultiplicity) {
        Object multiplicity;
        if (Model.getFacade().isAMultiplicity(element)) { 
            multiplicity = element;
        } else if (Model.getFacade().isAUMLElement(element)) {
            multiplicity = Model.getFacade().getMultiplicity(element);
        } else {
            throw new IllegalArgumentException();
        }
        // it can still be null if the UML element 
        // did not have a multiplicity defined.
        if (multiplicity != null) {
            int upper = Model.getFacade().getUpper(multiplicity);
            int lower = Model.getFacade().getLower(multiplicity);
            if (lower != 1 || upper != 1 || showSingularMultiplicity) {
                // TODO: I18N
                return Model.getFacade().toString(multiplicity);
            }
        }
        return "";
    }
    
    /**
     * @param umlAction the action
     * @return the generated text (never null)
     */
    static String generateAction(Object umlAction) {
        Collection c;
        Iterator it;
        String s;
        StringBuilder p;
        boolean first;
        if (umlAction == null) {
            return "";
        }

        Object script = Model.getFacade().getScript(umlAction);

        if ((script != null) && (Model.getFacade().getBody(script) != null)) {
            s = Model.getFacade().getBody(script).toString();
        } else {
            s = "";
        }

        p = new StringBuilder();
        c = Model.getFacade().getActualArguments(umlAction);
        if (c != null) {
            it = c.iterator();
            first = true;
            while (it.hasNext()) {
                Object arg = it.next();
                if (!first) {
                    // TODO: I18N
                    p.append(", ");
                }

                if (Model.getFacade().getValue(arg) != null) {
                    p.append(generateExpression(
                            Model.getFacade().getValue(arg)));
                }
                first = false;
            }
        }
        if (s.length() == 0 && p.length() == 0) {
            return "";
        }

        /* If there are no arguments, then do not show the ().
         * This solves issue 1758.
         * Arguments are not supported anyhow in the UI yet.
         * These brackets are easily confused with the brackets
         * for the Operation of a CallAction.
         */
        if (p.length() == 0) {
            return s;
        }

        // TODO: I18N
        return s + " (" + p + ")";
    }

    /**
     * Generate a textual representation of the given Action or ActionSequence 
     * according the UML standard notation.
     * 
     * @param a the UML Action or ActionSequence
     * @return the generated textual representation 
     * of the given action(sequence).
     * This value is guaranteed NOT null.
     */
    public static String generateActionSequence(Object a) {
        if (Model.getFacade().isAActionSequence(a)) {
            StringBuffer str = new StringBuffer("");
            Collection actions = Model.getFacade().getActions(a);
            Iterator i = actions.iterator();
            if (i.hasNext()) {
                str.append(generateAction(i.next()));
            }
            while (i.hasNext()) {
                str.append("; ");
                str.append(generateAction(i.next()));
            }
            return str.toString();
        } else {
            return generateAction(a);
        }
    }
    
    static StringBuilder formatNameList(Collection modelElements) {
        return formatNameList(modelElements, LIST_SEPARATOR);
    }

    static StringBuilder formatNameList(Collection modelElements, 
            String separator) {
        StringBuilder result = new StringBuilder();
        for (Object element : modelElements) {
            String name = Model.getFacade().getName(element);
            // TODO: Any special handling for null names? append will use "null"
            result.append(name).append(separator);
        }
        if (result.length() >= separator.length()) {
            result.delete(result.length() - separator.length(), 
                    result.length());
        }
        return result;
    }
}
