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

// File: CrTooManyClasses.java
// Classes: CrTooManyClasses
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.uml.critics;

import java.util.*;
import com.sun.java.swing.*;

import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.visual.*;


/** A critic to detect when a class can never have instances (of
 *  itself of any subclasses). */

public class CrTooManyClasses extends CrUML {

  ////////////////////////////////////////////////////////////////
  // constants
  public static String THRESHOLD = "Threshold";

  ////////////////////////////////////////////////////////////////
  // constructor
  public CrTooManyClasses() {
    // needs-more-work: {name} is not expanded for diagram objects
    setHeadline("Reduce Classes in diagram {name}");
    sd("There are too many classes in {name}.  If one class diagram "+
       "has too many classes it may become very difficult for humans "+
       "to understand. \n\n"+
       "Defining an understandable set of class diagrams is an important "+
       "part of your design. \n\n"+
       "To fix this, press the \"Next>\" button, or remove classes manually "+
       "by clicking on a class in the navigator pane or diagram and "+
       "presing the \"Del\" key.  Or you can make a new diagram...");

    addSupportedDecision(CrUML.decCLASS_SELECTION);
    setArg(THRESHOLD, new Integer(20));
  }

  ////////////////////////////////////////////////////////////////
  // critiquing API
  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof UMLClassDiagram)) return NO_PROBLEM;
    UMLClassDiagram d = (UMLClassDiagram) dm;

    int threshold = ((Integer)getArg(THRESHOLD)).intValue();
    Vector nodes = d.getGraphModel().getNodes();
    if (nodes.size() <= threshold) return NO_PROBLEM;
    return PROBLEM_FOUND;
  }

} /* end class CrTooManyClasses */

