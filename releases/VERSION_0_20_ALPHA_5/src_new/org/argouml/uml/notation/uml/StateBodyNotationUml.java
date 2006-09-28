// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
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

package org.argouml.uml.notation.uml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.generator.GeneratorDisplay;
import org.argouml.uml.generator.ParserDisplay;
import org.argouml.uml.notation.StateBodyNotation;

/**
 * @author mvw@tigris.org
 */
public class StateBodyNotationUml extends StateBodyNotation {

    /**
     * The constructor.
     *
     * @param state the state represented by the notation
     */
    public StateBodyNotationUml(Object state) {
        super(state);
    }

    /**
     * @see org.argouml.notation.NotationProvider4#parse(java.lang.String)
     */
    public String parse(String text) {
        try {
            parseStateBody(myState, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.statebody";
            Object[] args = {
                    pe.getLocalizedMessage(),
                    new Integer(pe.getErrorOffset()),
            };
            ProjectBrowser.getInstance().getStatusBar().showStatus(
                    Translator.messageFormat(msg, args));
        }
        return toString();
    }

    /**
     * @see org.argouml.notation.NotationProvider4#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-statebody";
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer s = new StringBuffer();

        Object entryAction = Model.getFacade().getEntry(myState);
        Object exitAction = Model.getFacade().getExit(myState);
        Object doAction = Model.getFacade().getDoActivity(myState);
        if (entryAction != null) {
            String entryStr = generateAction(entryAction);
            s.append("entry /").append(entryStr);
        }
        if (doAction != null) {
            String doStr = generateAction(doAction);
            if (s.length() > 0) {
                s.append("\n");
            }
            s.append("do /").append(doStr);

        }
        if (exitAction != null) {
            String exitStr = generateAction(exitAction);
            if (s.length() > 0) {
                s.append("\n");
            }
            s.append("exit /").append(exitStr);
        }
        Collection internaltrans = Model.getFacade().getInternalTransitions(myState);
        if (internaltrans != null) {
            Iterator iter = internaltrans.iterator();
            while (iter.hasNext()) {
                if (s.length() > 0) {
                    s.append("\n");
                }
                Object trans = iter.next();
                s.append(GeneratorDisplay.getInstance().generateTransition(trans));
            }
        }
        return s.toString();
    }

    /**
     * Parse user input for state bodies and assign the individual lines to
     * according actions or transistions. The user input consists of multiple
     * lines like:<pre>
     *   action-label / action-expression
     * </pre> or the format of a regular
     * transition - see parseTransition(). <p>
     *
     * "action-label" stands for one of "entry", "do" and "exit".
     * The words "entry", "do" and "exit" are case-independent.
     *
     * @param st  The State object.
     * @param s   The string to parse.
     * @throws ParseException when there is a syntax problem,
     *         e.g. non-matching brackets () or []
     */
    public void parseStateBody(Object st, String s) throws ParseException {
        boolean foundEntry = false;
        boolean foundExit = false;
        boolean foundDo = false;

        /* Generate all the existing internal transitions,
         * so that we can compare them as text with the newly entered ones.
         */
        ModelElementInfoList internalsInfo =
            new ModelElementInfoList(
                    Model.getFacade().getInternalTransitions(st));

        StringTokenizer lines = new StringTokenizer(s, "\n\r");
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken().trim();
            /* Now let's check if the new line is already present in
             * the old list of internal transitions; if it is, then
             * mark the old one to be retained (i.e. do not create a new one),
             * if it isn't, continue with parsing:
             */
            if (!internalsInfo.checkRetain(line)) {
                if (line.toLowerCase().startsWith("entry")
                        && line.substring(5).trim().startsWith("/")) {
                    parseStateEntryAction(st, line);
                    foundEntry = true;
                } else if (line.toLowerCase().startsWith("exit")
                        && line.substring(4).trim().startsWith("/")) {
                    parseStateExitAction(st, line);
                    foundExit = true;
                } else if (line.toLowerCase().startsWith("do")
                        && line.substring(2).trim().startsWith("/")) {
                    parseStateDoAction(st, line);
                    foundDo = true;
                } else {
                    Object t =
                        Model.getStateMachinesFactory()
                                .buildInternalTransition(st);
                    if (t == null) {
                        continue;
                    }
                    /* TODO: If the next line trows an exception, then what
                     * do we do with the remainder of the
                     * parsed/to be parsed lines?
                     */
                    ParserDisplay.SINGLETON.parseTransition(t, line);
                    /* Add this new one, and mark it to be retained: */
                    internalsInfo.add(t, true);
                }
            }
        }

        if (!foundEntry) {
            delete(Model.getFacade().getEntry(st));
        }
        if (!foundExit) {
            delete(Model.getFacade().getExit(st));
        }
        if (!foundDo) {
            delete(Model.getFacade().getDoActivity(st));
        }

        /* Process the final list of internal transitions,
         * and hook it to the state:
         */
        Model.getStateMachinesHelper().setInternalTransitions(st,
                internalsInfo.finalisedList());
    }

    /**
     * This class manages a list of UML modelelements that existed
     * before and after the parseXxxxx() function was called.
     * It has all the knowledge to deal with additions and removals.
     *
     * @author MVW
     */
    class ModelElementInfoList {
        /**
         * The list that we maintain.
         */
        private Collection theList;

        /**
         * An item in a list, maintains all info about one UML object,
         * its generated version (i.e. textual representation),
         * and if it needs to be retained after parsing.<p>
         *
         * @author MVW
         */
        class InfoItem {
            private String generated;
            private Object umlObject;
            private boolean retainIt = false;

            InfoItem(Object obj) {
                umlObject = obj;
                generated =
                    GeneratorDisplay.getInstance().generate(obj);
            }
            InfoItem(Object obj, boolean r) {
                this(obj);
                retainIt = r;
            }

            /**
             * @return the generated string representation
             */
            String getGenerated() {
                return generated;
            }

            /**
             * @return the UML Object
             */
            Object getUmlObject() {
                return umlObject;
            }

            /**
             * Retain this UML object.
             */
            void retain() {
                retainIt = true;
            }

            /**
             * @return true if the UML object is to be retained,
             *         false if it is to be deleted
             */
            boolean isRetained() {
                return retainIt;
            }
        }

        /**
         * The constructor.
         *
         * @param c the collection of the UML objects
         *          that were present before
         */
        ModelElementInfoList(Collection c) {
            theList = new ArrayList();
            Iterator i = c.iterator();
            while (i.hasNext()) {
                theList.add(new InfoItem(i.next()));
            }
        }

        /**
         * @param obj the UML object
         * @param r true if this UML object needs to be retained
         */
        void add(Object obj, boolean r) {
            theList.add(new InfoItem(obj, r));
        }

        /**
         * Check the given textual description,
         * and if already present in the list, then retain it.
         * @param line the given textual description
         * @return true if the item was already present in the list
         */
        boolean checkRetain(String line) {
            Iterator i = theList.iterator();
            while (i.hasNext()) {
                InfoItem tInfo =
                    (InfoItem) i.next();
                if (tInfo.getGenerated().equals(line)) {
                    tInfo.retain();
                    return true;
                }
            }
            return false;
        }

        /**
         * Finish the procedure, by deleting the UML model items
         * that are not to be retained, and return a collection
         * of the ones to be retained.
         * This method should only be called once!
         * @return the UML objects that survive.
         */
        Collection finalisedList() {
            // don't forget to remove old internals!
            Collection newModelElementsList = new ArrayList();
            Iterator i = theList.iterator();
            while (i.hasNext()) {
                InfoItem tInfo =
                    (InfoItem) i.next();
                if (tInfo.isRetained()) {
                    newModelElementsList.add(tInfo.getUmlObject());
                } else {
                    delete(tInfo.getUmlObject());
                }
            }
            // Make next accesses to this instance predictable:
            theList.clear();
            // and hook in the new ones:
            return newModelElementsList;
        }
    }


    /**
     * Parse a line of the form: "entry /action" and create an action.
     * We do not need to check for the presence of the word "entry" - that
     * is done by the caller.
     *
     * @param st  the state object
     * @param s   the string to be parsed
     */
    public void parseStateEntryAction(Object st, String s) {
        if (s.indexOf("/") > -1) {
            s = s.substring(s.indexOf("/") + 1).trim();
        }
        Object oldEntry = Model.getFacade().getEntry(st);
        if (oldEntry == null) {
            Model.getStateMachinesHelper().setEntry(st, buildNewCallAction(s));
        } else {
            updateAction(oldEntry, s);
        }
    }

    /**
     * Parse a line of the form: "exit /action" and create an action.
     * We do not need to check for the presence of the word "exit" - that
     * is done by the caller.
     *
     * @param st
     *            the state object
     * @param s
     *            the string to be parsed
     */
    public void parseStateExitAction(Object st, String s) {
        if (s.indexOf("/") > -1) {
            s = s.substring(s.indexOf("/") + 1).trim();
        }
        Object oldExit = Model.getFacade().getExit(st);
        if (oldExit == null) {
            Model.getStateMachinesHelper().setExit(st, buildNewCallAction(s));
        } else {
            updateAction(oldExit, s);
        }
    }

    /**
     * Parse a line of the form: "do /action" and create an action.
     * We do not need to check for the presence of the word "do" - that
     * is done by the caller.
     *
     * @param st  the state object
     * @param s   the string to be parsed
     */
    public void parseStateDoAction(Object st, String s) {
        if (s.indexOf("/") > -1) {
            s = s.substring(s.indexOf("/") + 1).trim();
        }
        Object oldDo = Model.getFacade().getDoActivity(st);
        if (oldDo == null) {
            Model.getStateMachinesHelper().setDoActivity(st,
                    buildNewCallAction(s));
        } else {
            updateAction(oldDo, s);
        }
    }

    /**
     * This builds a CallAction with default attributes. But without Operation!
     *
     * @author MVW
     * @param s
     *            string representing the Script of the Action
     * @return The newly created CallAction.
     */
    private Object buildNewCallAction(String s) {
        Object a =
            Model.getCommonBehaviorFactory().createCallAction();
        Object ae =
            Model.getDataTypesFactory().createActionExpression("Java", s);
        Model.getCommonBehaviorHelper().setScript(a, ae);
        Model.getCoreHelper().setName(a, "anon");
        return a;
    }

    /**
     * Update an existing Action with a new Script.
     *
     * @author MVW
     * @param old the Action
     * @param s   a string representing a new Script for the ActionExpression
     */
    private void updateAction(Object old, String s) {
        Object ae = Model.getFacade().getScript(old); // the ActionExpression
        String language = "Java";
        if (ae != null) {
            language = Model.getDataTypesHelper().getLanguage(ae);
            String body = (String) Model.getFacade().getBody(ae);
            if (body.equals(s)) {
                return;
            }
        }
        ae = Model.getDataTypesFactory().createActionExpression(language, s);
        Model.getCommonBehaviorHelper().setScript(old, ae);
    }

    /**
     * This deletes modelelements, and swallows null without barking.
     *
     * @author mvw@tigris.org
     * @param obj
     *            the modelelement to be deleted
     */
    private void delete(Object obj) {
        if (obj != null) {
            ProjectManager.getManager().getCurrentProject().moveToTrash(obj);
        }
    }

    public String generateAction(Object m) {
        Collection c;
        Iterator it;
        String s;
        String p;
        boolean first;

        Object script = Model.getFacade().getScript(m);

        if ((script != null) && (Model.getFacade().getBody(script) != null)) {
            s = Model.getFacade().getBody(script).toString();
        } else {
            s = "";
        }

        p = "";
        c = Model.getFacade().getActualArguments(m);
        if (c != null) {
            it = c.iterator();
            first = true;
            while (it.hasNext()) {
                Object arg = /*(MArgument)*/ it.next();
                if (!first) {
                    p += ", ";
                }

                if (Model.getFacade().getValue(arg) != null) {
                    p += generateExpression(Model.getFacade().getValue(arg));
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

        return s + " (" + p + ")";
    }

    public String generateExpression(Object expr) {
        if (Model.getFacade().isAExpression(expr))
            return generateUninterpreted(
                    (String) Model.getFacade().getBody(expr));
        else if (Model.getFacade().isAConstraint(expr))
            return generateExpression(Model.getFacade().getBody(expr));
        return "";
    }

    /**
     * Make a string non-null.<p>
     *
     * What is the purpose of this function? Shouldn't it be private static?
     *
     * @param un The String.
     * @return a non-null string.
     */
    public String generateUninterpreted(String un) {
        if (un == null)
            return "";
        return un;
    }
}
