// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// File: CrNoAssociations.javoa
// Classes: CrNoAssociations
// Original Author: jrobbins@ics.uci.edu

package org.argouml.uml.cognitive.critics;

import java.util.Iterator;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.critics.Critic;
import org.argouml.model.ModelFacade;


// Uses Model through ModelFacade

/** A critic to detect when a class might require associations. It checks for
 * inherited associations as well and keeps silent if it finds any.
 */

public class CrNoAssociations extends CrUML {

    /** */    
    public CrNoAssociations() {
        setHeadline("Add Associations to <ocl>self</ocl>");
        addSupportedDecision(CrUML.decRELATIONSHIPS);
        setKnowledgeTypes(Critic.KT_COMPLETENESS);
        addTrigger("associationEnd");
    }

    /** decide whether the given design material causes a problem
     * @param dm the object to criticize
     * the designer who decides the design process
     * @param dsgr the designer
     * @return <CODE>PROBLEM_FOUND</CODE> if there is a problem, otherwise <CODE>NO_PROBLEM</CODE>
     */    
    public boolean predicate2(Object dm, Designer dsgr) {
        if (!(ModelFacade.isAClassifier(dm)))
            return NO_PROBLEM;
        if (!(ModelFacade.isPrimaryObject(dm)))
            return NO_PROBLEM;

        // if the object does not have a name,
        // than no problem
        if ((ModelFacade.getName(dm) == null)
            || ("".equals(ModelFacade.getName(dm))))
            return NO_PROBLEM;
        
        // abstract elements do not necessarily require associations
        if (ModelFacade.isAGeneralizableElement(dm) && 
            ModelFacade.isAbstract(dm)) {
            return NO_PROBLEM;
        }

        // types can probably have associations, but we should not nag at them
        // not having any.
        // utility is a namespace collection - also not strictly required 
        // to have associations.
        if (ModelFacade.isType(dm))
            return NO_PROBLEM;
        if (ModelFacade.isUtility(dm))
            return NO_PROBLEM;

        //TODO: different critic or special message for classes
        //that inherit all ops but define none of their own.

        if (findAssociation(dm, 0))
            return NO_PROBLEM;
        return PROBLEM_FOUND;
    }

    /**
     * @param handle the classifier to examine
     * @param number of levels searched
     * @return true if an association can be found in this classifier
     *		or in any of its generalizations.
     */
    private boolean findAssociation(Object dm, int depth) {
        if (ModelFacade.getAssociationEnds(dm).iterator().hasNext())
            return true;

        if (depth > 50)
            return false;

        Iterator iter = ModelFacade.getGeneralizations(dm).iterator();

        while (iter.hasNext()) {
            Object parent = ModelFacade.getParent(iter.next());

            if (parent == dm)
                continue;

            if (ModelFacade.isAClassifier(parent))
                if (findAssociation(parent, depth + 1))
                    return true;
        }
        return false;
    }

} /* end class CrNoAssociations */
