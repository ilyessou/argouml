// Copyright (c) 1996-99 The Regents of the University of California.
// All Rights Reserved.  Permission to use, copy, modify, and distribute
// this software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California.  The software program and documentation are supplied "as
// is", without any accompanying services from The Regents.  The Regents
// do not warrant that the operation of the program will be uninterrupted
// or error-free.  The end-user understands that the program was
// developed for research purposes and is advised not to rely exclusively
// on the program for any reason.  IN NO EVENT SHALL THE UNIVERSITY OF
// CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL,
// INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING
// OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE
// UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
// DAMAGE.  THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.




// File: UMLClassDiagram.java
// Classes: UMLClassDiagram
// Original Author: jrobbins@ics.uci.edy
// $Id$


package uci.uml.visual;

import java.util.*;
import java.awt.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.border.*;

import uci.gef.*;
import uci.graph.*;
import uci.ui.*;
import uci.uml.ui.*;
import uci.uml.Model_Management.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Behavioral_Elements.Common_Behavior.*;

public class DiagramInfo extends JComponent {

  ////////////////////////////////////////////////////////////////
  // instance variables

  protected Diagram _diagram = null;
  protected JLabel _name = new JLabel("");

  ////////////////////////////////////////////////////////////////
  // constructor

  public DiagramInfo(Diagram d) {
    _diagram = d;
    //setBorder(new EtchedBorder());
    setLayout(new BorderLayout());
    add(_name, BorderLayout.CENTER);
    updateName();
  }


  ////////////////////////////////////////////////////////////////
  // updates
  public void updateName() {
    String type = "Diagram";
    if (_diagram instanceof UMLClassDiagram)
      type = "Class Diagram";
    if (_diagram instanceof UMLStateDiagram)
      type = "State Diagram";
    if (_diagram instanceof UMLUseCaseDiagram)
      type = "Use Case Diagram";
    if (_diagram instanceof UMLActivityDiagram)
      type = "Activity Diagram";
    if (_diagram instanceof UMLCollaborationDiagram)
      type = "Collaboration Diagram";
//     if (_diagram instanceof UMLSequenceDiagram)
//       type = "Sequence Diagram";
//     if (_diagram instanceof UMLDeploymentDiagram)
//       type = "Deployment Diagram";
    
    _name.setText(type + ": " + _diagram.getName());
  }

} /* end class DiagramInfo */
