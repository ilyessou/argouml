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

// File: CrNoTransitions.java
// Classes: CrNoTransitions
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.critics.Critic;
import org.argouml.model.ModelFacade;

/** A critic to detect when a state has no outgoing transitions. */

public class CrNoTransitions extends CrUML {

    /** constructor 
     */
    public CrNoTransitions() {
	setHeadline("Add Transitions to <ocl>self</ocl>");
	addSupportedDecision(CrUML.decSTATE_MACHINES);
	setKnowledgeTypes(Critic.KT_COMPLETENESS);
	addTrigger("incoming");
	addTrigger("outgoing");
    }

    /** This is the decision routine for the critic. 
     * 
     * @param dm is the UML entity (an NSUML object) that is being checked. 
     * @param dsgr is for future development and can be ignored.
     * 
     * @return boolean problem found
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAStateVertex(dm))) return NO_PROBLEM;
	Object sv = /*(MStateVertex)*/ dm;
	if (ModelFacade.isAState(sv)) {
	    Object sm = ModelFacade.getStateMachine(sv);
	    if (sm != null && ModelFacade.getTop(sm) == sv) return NO_PROBLEM;
	}
	Collection outgoing = ModelFacade.getOutgoings(sv);
	Collection incoming = ModelFacade.getIncomings(sv);
	boolean needsOutgoing = outgoing == null || outgoing.size() == 0;
	boolean needsIncoming = incoming == null || incoming.size() == 0;
	if (ModelFacade.isAPseudostate(sv)) {
	    Object k = ModelFacade.getPseudostateKind(sv);
	    if (k.equals(ModelFacade.BRANCH_PSEUDOSTATEKIND)) {
	        return NO_PROBLEM;
	    }
	    if (k.equals(ModelFacade.JUNCTION_PSEUDOSTATEKIND)) {
	        return NO_PROBLEM;
	    }
	    if (k.equals(ModelFacade.INITIAL_PSEUDOSTATEKIND)) {
                needsIncoming = false;
            }
	}
	if (ModelFacade.isAFinalState(sv)) needsOutgoing = false;
    
	if (needsIncoming && needsOutgoing) return PROBLEM_FOUND;
	return NO_PROBLEM;
    }

} /* end class CrNoTransitions */