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

import javax.swing.Icon;

import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.cognitive.ui.Wizard;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;

/**
 * Critic to detect whether a class name obeys to certain rules.
 */
public class CrUnconventionalClassName extends AbstractCrUnconventionalName {

    /**
     * The constructor.
     */
    public CrUnconventionalClassName() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.NAMING);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("name");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(Model.getFacade().isAClass(dm))
            && !(Model.getFacade().isAInterface(dm))) {
	    return NO_PROBLEM;
	}
	Object cls = /*(MClassifier)*/ dm;
	String myName = Model.getFacade().getName(cls);
	if (myName == null || myName.equals("")) {
	    return NO_PROBLEM;
	}
	String nameStr = myName;
	if (nameStr == null || nameStr.length() == 0) {
	    return NO_PROBLEM;
	}
	char initialChar = nameStr.charAt(0);
	if (!Character.isUpperCase(initialChar)) {
	    return PROBLEM_FOUND;
	}
	return NO_PROBLEM;
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
	if (w instanceof WizMEName) {
	    ToDoItem item = (ToDoItem) w.getToDoItem();
	    Object me = /*(MModelElement)*/ item.getOffenders().elementAt(0);
	    String sug = Model.getFacade().getName(me);
	    sug = computeSuggestion(sug);
	    String ins =
	        "Change the class name to start with an "
		+ "uppercase letter.";
	    ((WizMEName) w).setInstructions(ins);
	    ((WizMEName) w).setSuggestion(sug);
	}
    }

    /**
     * @see org.argouml.uml.cognitive.critics.AbstractCrUnconventionalName#computeSuggestion(java.lang.String)
     */
    public String computeSuggestion(String sug) {
        if (sug == null) {
            return "";
        }
        return sug.substring(0, 1).toUpperCase() + sug.substring(1);
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) { return WizMEName.class; }

    /**
     * The UID.
     */
    private static final long serialVersionUID = -3341858698991522822L;
} /* end class CrUnconventionalClassName */
