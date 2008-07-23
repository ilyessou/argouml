// $Id: eclipse-argo-codetemplates.xml 11347 2006-10-26 22:37:44Z linus $
// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.profile.internal.ocl;

import java.util.Vector;

import org.argouml.cognitive.Decision;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.profile.internal.ocl.uml14.Uml14ModelInterpreter;
import org.argouml.uml.cognitive.UMLDecision;
import org.argouml.uml.cognitive.critics.CrUML;

/**
 * Represents an critics defined as an OCL expression in a profile
 * 
 * @author maurelio1234
 */
public class CrOCL extends CrUML {

    /**
     * Logger.
     */
    // private static final Logger LOG = Logger.getLogger(CrOCL.class);
    /**
     * the OCL Interpreter
     */
    private OclInterpreter interpreter = null;

    /**
     * the OCL string
     */
    private String ocl = null;
    
    /**
     * Creates a new OCL critic
     * 
     * @param oclConstraint ocl expression
     * @param headline headline
     * @param description description
     * @param moreInfoURL the info url
     * @param knowledgeTypes the knowledge types
     * @param supportedDecisions the decisions
     * @param priority the priority
     * @throws InvalidOclException if the ocl is not valid
     */
    public CrOCL(String oclConstraint, String headline, String description,
            Integer priority, Vector<Decision> supportedDecisions,
            Vector<String> knowledgeTypes, String moreInfoURL)
        throws InvalidOclException {
        interpreter = 
            new OclInterpreter(oclConstraint, new Uml14ModelInterpreter());
        this.ocl = oclConstraint;
        
        addSupportedDecision(UMLDecision.PLANNED_EXTENSIONS);
        setPriority(ToDoItem.HIGH_PRIORITY);

        Vector<String> triggers = interpreter.getTriggers();

        for (String string : triggers) {
            addTrigger(string);
        }

        if (headline == null) {
            super.setHeadline("OCL Expression");
        } else {
            super.setHeadline(headline);
        }

        if (description == null) {
            super.setDescription("");
        } else {
            super.setDescription(description);
        }

        if (priority == null) {
            setPriority(ToDoItem.HIGH_PRIORITY);
        } else {
            setPriority(priority);
        }

        if (supportedDecisions != null) {
            for (Decision d : supportedDecisions) {
                addSupportedDecision(d);
            }
        }

        if (knowledgeTypes != null) {
            for (String k : knowledgeTypes) {
                addKnowledgeType(k);
            }
        }

        if (moreInfoURL != null) {
            setMoreInfoURL(moreInfoURL);
        }
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(java.lang.Object,
     *      org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
        if (!interpreter.applicable(dm)) {
            return NO_PROBLEM;
        } else {
            if (interpreter.check(dm)) {
                return NO_PROBLEM;
            } else {
                return PROBLEM_FOUND;
            }
        }
    }

    /**
     * @return the ocl constraint
     */
    public String getOCL() {
        return ocl;
    }

}
