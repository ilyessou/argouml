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



// File: PropPanelClassifierRole.java
// Classes: PropPanelClassifierRole
// Original Author: agauthie@ics.uci.edu
// $Id$

package uci.uml.ui.props;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;
import com.sun.java.swing.text.*;
import com.sun.java.swing.border.*;
import com.sun.java.swing.table.*;
import com.sun.java.swing.plaf.metal.MetalLookAndFeel;

import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Model_Management.*;
import uci.uml.Behavioral_Elements.Collaborations.*;
import uci.uml.ui.*;
import uci.uml.generate.*;

/** User interface panel shown at the bottom of the screen that allows
 *  the user to edit the properties of the selected UML model element.
 *  Needs-More-Work: cut and paste base class code from
 *  PropPanelClass. */

public class PropPanelClassifierRole extends PropPanel
implements ItemListener, DocumentListener {

  ////////////////////////////////////////////////////////////////
  // constants

  ////////////////////////////////////////////////////////////////
  // instance vars
  JLabel _baseLabel = new JLabel("Base: ");
  SpacerPanel _spacer = new SpacerPanel();
  SpacerPanel _placeHolder = new SpacerPanel();

  JTextField _baseField = new JTextField();

  ////////////////////////////////////////////////////////////////
  // contructors
  public PropPanelClassifierRole() {
    super("ClassifierRole Properties");
    GridBagLayout gb = (GridBagLayout) getLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.0;
    c.ipadx = 0; c.ipady = 0;

    //_baseField.addItemListener(this);

    // add all widgets and labels
    c.gridx = 0;
    c.gridwidth = 1;
    c.gridy = 1;
    c.weightx = 0.0;
    gb.setConstraints(_baseLabel, c);
    add(_baseLabel);

    c.weightx = 1.0;
    c.gridx = 1;
    c.gridy = 1;
    _baseField.setMinimumSize(new Dimension(120, 20));
    gb.setConstraints(_baseField, c);
    add(_baseField);
    _baseField.getDocument().addDocumentListener(this);
    _baseField.setFont(_stereoField.getFont());

    c.gridx = 2;
    c.gridwidth = 1;
    c.weightx = 0;
    c.gridy = 0;
    gb.setConstraints(_spacer, c);
    add(_spacer);

    c.gridx = 3;
    c.gridwidth = 3;
    c.gridheight = 5;
    c.weightx = 1;
    c.gridy = 1;
    gb.setConstraints(_placeHolder, c);
    add(_placeHolder);

    // register interest in change events from all widgets
  }

  ////////////////////////////////////////////////////////////////
  // accessors

  /** Set the values to be shown in all widgets based on model */
  public void setTarget(Object t) {
    super.setTarget(t);
    ClassifierRole cr = (ClassifierRole) t;
    if (cr.getBaseString() != null)
      _baseField.setText(cr.getBaseString().trim());
    // set the values to be shown in all widgets based on model

    //_tableModel.setTarget(cr);
    //TableColumn descCol = _extPts.getColumnModel().getColumn(0);
    //descCol.setMinWidth(50);
    validate();
  }

  ////////////////////////////////////////////////////////////////
  // event handlers


  /** The user typed some text */
  public void insertUpdate(DocumentEvent e) {
    super.insertUpdate(e);
    if (e.getDocument() == _baseField.getDocument()) {
      setTargetBaseString(_baseField.getText().trim());
    }
  }

  public void removeUpdate(DocumentEvent e) { insertUpdate(e); }

  public void changedUpdate(DocumentEvent e) {
    // Apparently, this method is never called.
  }

  /** The user modified one of the widgets */
  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    // check for each widget, and update the model with new value
  }

  protected void setTargetBaseString(String s) {
    if (_target == null) return;
    try {
      ((ClassifierRole)_target).setBaseString(s);
    }
    catch (PropertyVetoException pve) { } 
  }



} /* end class PropPanelClassifierRole */


