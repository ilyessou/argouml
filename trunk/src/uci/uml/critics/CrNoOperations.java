// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.







// File: CrNoOperations.javoa
// Classes: CrNoOperations
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.uml.critics;

import java.util.*;
import com.sun.java.swing.*;

import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Foundation.Extension_Mechanisms.*;

/** A critic to detect when a class can never have instances (of
 *  itself of any subclasses). */

public class CrNoOperations extends CrUML {

  public CrNoOperations() {
    setHeadline("Add Operations to {name}");
    sd("You have not yet specified operations for {name}. "+
       "Normally classes provide operations that define their behavior. \n\n"+
       "Defining operations is needed to complete the behavioral "+
       "specification part of your design. \n\n"+
       "To fix this, press the \"Next>\" button, or add operations manually "+
       "by clicking on {name} in the navigator pane and "+
       "using the Create menu to make a new operations. ");

    addSupportedDecision(CrUML.decBEHAVIOR);
    setKnowledgeTypes(Critic.KT_COMPLETENESS);
    addTrigger("behavioralFeature");
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof MMClass)) return NO_PROBLEM;
    MMClass cls = (MMClass) dm;
    //if (cls.containsStereotype(Stereotype.UTILITY)) return NO_PROBLEM;
    // stereotype <<record>>?
    //needs-more-work: different critic or special message for classes
    //that inherit all ops but define none of their own.

    Vector beh = cls.getInheritedBehavioralFeatures();
    if (beh == null) return PROBLEM_FOUND;
    int size = beh.size();
    for (int i = 0; i < size; i++) {
      BehavioralFeature bf = (BehavioralFeature) beh.elementAt(i);
      ScopeKind sk = bf.getOwnerScope();
      if (ScopeKind.INSTANCE.equals(sk)) return NO_PROBLEM;
    }
    //needs-more-work?: don't count static or constants?
    return PROBLEM_FOUND;
  }

  public Icon getClarifier() {
    return ClOperationCompartment.TheInstance;
  }

} /* end class CrNoOperations */

