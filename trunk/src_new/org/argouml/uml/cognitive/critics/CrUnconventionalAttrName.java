// $Id$
// Copyright (c) 1996-99 The Regents of the University of California. All
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



// File: CrUnconventionalAttrName.java
// Classes: CrUnconventionalAttrName
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.argouml.uml.cognitive.critics;

import javax.swing.Icon;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.uml.cognitive.UMLToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.kernel.Wizard;
import org.argouml.model.ModelFacade;
import org.tigris.gef.util.VectorSet;
/** Critic to detect whether an attribute name obeys to certain rules.
 *  <p>
 *  Checks for:
 *  <ul>
 *  <li> all lower case or
 *  <li> all upper case
 *  </ul>
 *  where trailing underscores are removed, and
 *  constants are not nagged at.
 */
public class CrUnconventionalAttrName extends CrUML {

    /**
     * The constructor.
     * 
     */
    public CrUnconventionalAttrName() {
	setHeadline("Choose a Better MAttribute Name");
	addSupportedDecision(CrUML.DEC_NAMING);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("feature_name");
    }


    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAAttribute(dm))) return NO_PROBLEM;
	Object attr = /*(MAttribute)*/ dm;
	String myName = ModelFacade.getName(attr);
	if (myName == null || myName.equals("")) return NO_PROBLEM;
	String nameStr = myName;
	if (nameStr == null || nameStr.length() == 0) return NO_PROBLEM;
	// remove trailing underscores, if
	// remaining string is of zero length obviously this is a problem.
	while (nameStr.startsWith("_")) nameStr = nameStr.substring(1);
	if (nameStr.length() == 0) return PROBLEM_FOUND;

	// check for all uppercase and/or mixed with underspores
	char initalChar = nameStr.charAt(0);
	boolean allCapitals = true;
	for (int i = 0; i < nameStr.length() && allCapitals; i++) {
	    if (!(Character.isUpperCase(nameStr.charAt(i)) 
                || nameStr.charAt(i) == '_')) {
		allCapitals = false;
		continue;
	    }
	}
	if (allCapitals) return NO_PROBLEM;

	// check whether constant, constants are often weird and thus not a
	// problem
	Object/*MChangeableKind*/ ck = ModelFacade.getChangeability(attr);
        if (ModelFacade.isFrozen(ck)) return NO_PROBLEM;
	if (!Character.isLowerCase(initalChar)) {
	    return PROBLEM_FOUND;
	}
	return NO_PROBLEM;
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#toDoItem(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public ToDoItem toDoItem(Object dm, Designer dsgr) {
	Object f = /*(MFeature)*/ dm;
	VectorSet offs = computeOffenders(f);
	return new UMLToDoItem(this, offs, dsgr);
    }

    /**
     * @param dm the feature
     * @return the set of offenders
     */
    protected VectorSet computeOffenders(Object /*MFeature*/ dm) {
	VectorSet offs = new VectorSet(dm);
	offs.addElement(ModelFacade.getOwner(dm));
	return offs;
    }

    /**
     * @see org.argouml.cognitive.Poster#getClarifier()
     */
    public Icon getClarifier() {
	return ClAttributeCompartment.getTheInstance();
    }

    /**
     * @see org.argouml.cognitive.Poster#stillValid(
     * org.argouml.cognitive.ToDoItem, org.argouml.cognitive.Designer)
     */
    public boolean stillValid(ToDoItem i, Designer dsgr) {
	if (!isActive()) return false;
	VectorSet offs = i.getOffenders();
	Object f = /*(MFeature)*/ offs.firstElement();
	if (!predicate(f, dsgr)) return false;
	VectorSet newOffs = computeOffenders(f);
	boolean res = offs.equals(newOffs);
	return res;
    }


    /**
     * @see org.argouml.cognitive.critics.Critic#initWizard(org.argouml.kernel.Wizard)
     */
    public void initWizard(Wizard w) {
	if (w instanceof WizMEName) {
	    ToDoItem item = w.getToDoItem();
	    Object me =
		/*(MModelElement)*/ item.getOffenders().elementAt(0);
	    String sug = ModelFacade.getName(me);
	    if (sug.startsWith("_"))
		sug =
		    "_" + sug.substring(1, 2).toLowerCase() + sug.substring(2);
	    else
		sug = sug.substring(0, 1).toLowerCase() + sug.substring(1);
	    String ins = "Change the attribute name to start with a " 
	        + "lowercase letter.";
	    ((WizMEName) w).setInstructions(ins);
	    ((WizMEName) w).setSuggestion(sug);
	}
    }
    
    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) { return WizMEName.class; }

} /* end class CrUnconventionalAttrName */