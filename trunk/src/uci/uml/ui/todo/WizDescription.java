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




package uci.uml.ui.todo;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;

import uci.ui.ToolBar;
import uci.util.*;
import uci.argo.kernel.*;
import uci.uml.Foundation.Core.*;
import uci.uml.ui.*;


public class WizDescription extends WizStep {

  ////////////////////////////////////////////////////////////////
  // instance variables

  JTextArea _description = new JTextArea();


  public WizDescription() {
    super();
    System.out.println("making WizDescription");

    _description.setLineWrap(true);
    _description.setWrapStyleWord(true);

    _mainPanel.setLayout(new BorderLayout());
    _mainPanel.add(new JScrollPane(_description), BorderLayout.CENTER);
  }

  public void setTarget(Object item) {
    super.setTarget(item);
    if (_target == null) {
      _description.setText("No ToDoItem selected");
    }
    else if (_target instanceof ToDoItem) {
      ToDoItem tdi = (ToDoItem) _target;
      _description.setEnabled(true);
      _description.setText(tdi.getDescription());
      _description.setCaretPosition(0);
    }
    else if (_target instanceof PriorityNode) {
      _description.setText("This branch contains " + _target.toString() +
			   " priority \"to do\" items.");
      return;
    }
    else if (_target instanceof Critic) {
      _description.setText("This branch contains \"to do\" items "+
			   "generated by the critic: \n" +
			   _target.toString() + ".");
      return;
    }
    else if (_target instanceof ModelElement) {
      _description.setText("This branch contains \"to do\" items "+
			   "related to the model element: \n" +
			   _target.toString() + ".");
      return;
    }
    else if (_target instanceof Decision) {
      _description.setText("This branch contains \"to do\" items "+
			   "related to the decision: \n" +
			   _target.toString() + ".");
      return;
    }
    else if (_target instanceof Goal) {
      _description.setText("This branch contains \"to do\" items "+
			   "related to the goal: \n" +
			   _target.toString() + ".");
      return;
    }
    else if (_target instanceof KnowledgeTypeNode) {
      _description.setText("This branch contains \"to do\" items "+
			   "that provide " + _target.toString() +
			   " related knowledge.");
      return;
    }
    else {
      _description.setText("");
      return;
    }
  }



} /* end class WizDescription */
