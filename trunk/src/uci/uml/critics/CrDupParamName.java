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



// File: CrDupParamName.java.java
// Classes: CrDupParamName.java
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.uml.critics;

import java.util.*;
import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;

/** Well-formedness rule [1] for BehavioralFeature. See page 28 of UML 1.1
 *  Semantics. OMG document ad/97-08-04. */

public class CrDupParamName extends CrUML {

  public CrDupParamName() {
    setHeadline("Duplicate Parameter Name");
    sd("Each parameter of an operation must have a unique name. \n\n"+
       "Clean and unambigous naming is needed for code generation and to "+
       "achieve clear and maintainable designs.\n\n"+
       "To fix this, use the FixIt button, or manually rename one of the "+
       "parameters to this operation.");

    addSupportedDecision(CrUML.decCONTAINMENT);
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof BehavioralFeature)) return NO_PROBLEM;
    BehavioralFeature bf = (BehavioralFeature) dm;
    Vector params = bf.getParameter();
    Vector namesSeen = new Vector();
    java.util.Enumeration enum = params.elements();
    while (enum.hasMoreElements()) {
      Parameter p = (Parameter) enum.nextElement();
      Name pName = p.getName();
      if (Name.UNSPEC.equals(pName)) continue;
      String nameStr = pName.getBody();
      if (nameStr.length() == 0) continue;
      if (namesSeen.contains(nameStr)) return PROBLEM_FOUND;
      namesSeen.addElement(nameStr);
    }
    return NO_PROBLEM;
  }

} /* end class CrDupParamName.java */

