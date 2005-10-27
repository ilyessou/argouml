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

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ListSet;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.cognitive.ui.Wizard;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;
import org.argouml.uml.cognitive.UMLToDoItem;

/**
 * Well-formedness rule [1] for MNamespace. See page 33 of UML 1.1
 * Semantics. OMG document ad/97-08-04.
 */
public class CrNameConfusion extends CrUML {

    /**
     * The constructor.
     */
    public CrNameConfusion() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.NAMING);
	setKnowledgeTypes(Critic.KT_PRESENTATION);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("name");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(Model.getFacade().isAModelElement(dm))) {
	    return NO_PROBLEM;
	}
	Object me = /*(MModelElement)*/ dm;
	ListSet offs = computeOffenders(me);
	if (offs.size() > 1) {
	    return PROBLEM_FOUND;
	}
	return NO_PROBLEM;
    }

    /**
     * @param dm the given modelelement
     * @return the vectorset of offenders
     */
    public ListSet computeOffenders(Object/*MModelElement*/ dm) {
	Object ns = Model.getFacade().getNamespace(dm);
	ListSet res = new ListSet(dm);
	String n = Model.getFacade().getName(dm);
	if (n == null || n.equals("")) {
	    return res;
	}
	String dmNameStr = n;
	if (dmNameStr == null || dmNameStr.length() == 0) {
	    return res;
	}
	String stripped2 = strip(dmNameStr);
	if (ns == null) {
	    return res;
	}
	Collection oes = Model.getFacade().getOwnedElements(ns);
	if (oes == null) {
	    return res;
	}
	Iterator elems = oes.iterator();
	while (elems.hasNext()) {
	    Object me2 = /*(MModelElement)*/ elems.next();
	    if (me2 == dm) {
	        continue;
	    }
	    String meName = Model.getFacade().getName(me2);
	    if (meName == null || meName.equals("")) {
	        continue;
	    }
	    String compareName = meName;
	    if (confusable(stripped2, strip(compareName))
                && !dmNameStr.equals(compareName)) {
		res.addElement(me2);
	    }
	}
	return res;
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#toDoItem(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public ToDoItem toDoItem(Object dm, Designer dsgr) {
	Object me = /*(MModelElement)*/ dm;
	ListSet offs = computeOffenders(me);
	return new UMLToDoItem(this, offs, dsgr);
    }

    /**
     * @see org.argouml.cognitive.Poster#stillValid(
     * org.argouml.cognitive.ToDoItem, org.argouml.cognitive.Designer)
     */
    public boolean stillValid(ToDoItem i, Designer dsgr) {
	if (!isActive()) {
	    return false;
	}
	ListSet offs = i.getOffenders();
	Object dm = /*(MModelElement)*/ offs.firstElement();
	if (!predicate(dm, dsgr)) {
	    return false;
	}
	ListSet newOffs = computeOffenders(dm);
	boolean res = offs.equals(newOffs);
	return res;
    }

    /**
     * @param stripped1 given string 1
     * @param stripped2 given string 2
     * @return true if the both given strings are confusingly similar
     */
    public boolean confusable(String stripped1, String stripped2) {
	int countDiffs = countDiffs(stripped1, stripped2);
	return countDiffs <= 1;
    }

    /**
     * @param s1 given string 1
     * @param s2 given string 2
     * @return positive int, representing the number of different chars, or
     *         if the lengths differ more than 2, this length difference
     */
    public int countDiffs(String s1, String s2) {
	int len = Math.min(s1.length(), s2.length());
	int count = Math.abs(s1.length() - s2.length());
	if (count > 2) {
	    return count;
	}
	for (int i = 0; i < len; i++) {
	    if (s1.charAt(i) != s2.charAt(i)) {
	        count++;
	    }
	}
	return count;
    }

    /**
     * @param s the given string
     * @return the string s with all non-letters/digits stripped off
     */
    public String strip(String s) {
	StringBuffer res = new StringBuffer(s.length());
	int len = s.length();
	for (int i = 0; i < len; i++) {
	    char c = s.charAt(i);
	    if (Character.isLetterOrDigit(c)) {
	        res.append(Character.toLowerCase(c));
	    } else if (c == ']' && i > 1 && s.charAt(i - 1) == '[') {
		res.append("[]");
	    }
	}
	return res.toString();
    }

    /**
     * @see org.argouml.cognitive.Poster#getClarifier()
     */
    public Icon getClarifier() {
	return ClClassName.getTheInstance();
    }


    /**
     * @see org.argouml.cognitive.critics.Critic#initWizard(
     *         org.argouml.cognitive.ui.Wizard)
     */
    public void initWizard(Wizard w) {
	if (w instanceof WizManyNames) {
	    ToDoItem item = (ToDoItem) w.getToDoItem();
	    ((WizManyNames) w).setMEs(item.getOffenders().asVector());
	}
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) {
	return WizManyNames.class;
    }

} /* end class CrNameConfusion.java */
