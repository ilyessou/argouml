// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products may
// be obtained by contacting the University of California. David F. Redmiles
// Department of Information and Computer Science (ICS) University of
// California Irvine, California 92697-3425 Phone: 714-824-3823. This software
// program and documentation are copyrighted by The Regents of the University
// of California. The software program and documentation are supplied "as is",
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

// File: PropPanelAttr.java
// Classes: PropPanelAttr
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.uml.ui;

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

import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Model_Management.*;

/** User interface panel shown at the bottom of the screen that allows
 *  the user to edit the properties of the selected UML model
 *  element. */

public class PropPanelAttr extends PropPanel
implements DocumentListener, ItemListener {

  ////////////////////////////////////////////////////////////////
  // constants
  public static final VisibilityKind VISIBILITIES[] = {
    VisibilityKind.PUBLIC, VisibilityKind.PRIVATE,
    VisibilityKind.PROTECTED, VisibilityKind.PACKAGE };
  public static final String ATTRKEYWORDS[] = {
    "None", "transient", "static", "final", "static final"};


  ////////////////////////////////////////////////////////////////
  // instance vars
  JLabel _visLabel = new JLabel("Visibility: ");
  JComboBox _visField = new JComboBox(VISIBILITIES);
  JLabel _keywordsLabel = new JLabel("Keywords: ");
  JComboBox _keywordsField = new JComboBox(ATTRKEYWORDS);
  JLabel _typeLabel = new JLabel("Type: ");
  JComboBox _typeField = new JComboBox();
  JLabel _initLabel = new JLabel("Initial Value: ");
  JTextArea _initText = new JTextArea();
  SpacerPanel _spacer = new SpacerPanel();

  ////////////////////////////////////////////////////////////////
  // contructors
  public PropPanelAttr() {
    super("Attribute Properties");
    GridBagLayout gb = (GridBagLayout) getLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.0;
    c.ipadx = 0; c.ipady = 0;


    //_visField.getEditor().getEditorComponent().setBackground(Color.white);
    //_keywordsField.getEditor().getEditorComponent().setBackground(Color.white);
    _typeField.setEditable(true);
    _typeField.getEditor().getEditorComponent().setBackground(Color.white);

    c.gridx = 0;
    c.gridwidth = 1;
    c.gridy = 1;
    gb.setConstraints(_visLabel, c);
    add(_visLabel);
    c.gridy = 2;
    gb.setConstraints(_keywordsLabel, c);
    add(_keywordsLabel);
    c.gridy = 3;
    gb.setConstraints(_typeLabel, c);
    add(_typeLabel);


    
    c.weightx = 1.0;
    c.gridx = 1;
    //c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridy = 1;
    gb.setConstraints(_visField, c);
    add(_visField);
    c.gridy = 2;
    gb.setConstraints(_keywordsField, c);
    add(_keywordsField);
    c.gridy = 3;
    gb.setConstraints(_typeField, c);
    add(_typeField);

    c.weightx = 0.0;
    c.gridx = 2;
    c.gridy = 0;
    gb.setConstraints(_spacer, c);
    add(_spacer);
    

    c.weightx = 1.0;
    c.gridwidth = 3;
    c.gridx = 3;
    //c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridy = 0;
    gb.setConstraints(_initLabel, c);
    add(_initLabel);
    c.gridy = 1;
    c.gridheight = GridBagConstraints.REMAINDER;
    gb.setConstraints(_initText, c);
    add(_initText);

    Component ed = _typeField.getEditor().getEditorComponent();
    Document typeDoc = ((JTextField)ed).getDocument();
    typeDoc.addDocumentListener(this);

    _initText.getDocument().addDocumentListener(this);

    _visField.addItemListener(this);
    _keywordsField.addItemListener(this);
    _typeField.addItemListener(this);

  }

  ////////////////////////////////////////////////////////////////
  // accessors

  public void setTarget(Object t) {
    super.setTarget(t);
    Attribute attr = (Attribute) t;

    VisibilityKind vk = attr.getVisibility();
    _visField.setSelectedItem(vk);

    ScopeKind sk = attr.getOwnerScope();
    ChangeableKind ck = attr.getChangeable();
    
    if (ScopeKind.CLASSIFIER.equals(sk) && ChangeableKind.FROZEN.equals(ck))
      _keywordsField.setSelectedItem("static final");
    else if (ScopeKind.CLASSIFIER.equals(sk))
      _keywordsField.setSelectedItem("static");
    else if (ChangeableKind.FROZEN.equals(ck))
      _keywordsField.setSelectedItem("final");
    else
      _keywordsField.setSelectedItem("None");

    Classifier type = attr.getType();
    _typeField.setSelectedItem(type);
    
    Expression expr = attr.getInitialValue();
    if (expr == null) _initText.setText("");
    else _initText.setText(expr.getBody().getBody());

  }



  public void setTargetVisibility() {
    if (_target == null) return;
    VisibilityKind vk = (VisibilityKind) _visField.getSelectedItem();
    Attribute attr = (Attribute) _target;
    try {
        attr.setVisibility(vk);
    }
    catch (PropertyVetoException ignore) { }
  }

  public void setTargetKeywords() {
    if (_target == null) return;
    String keys = (String) _keywordsField.getSelectedItem();
    if (keys == null) {
      System.out.println("keywords are null");
      return;
    }
    Attribute attr = (Attribute) _target;
    try { 
      if (keys.equals("None")) {
	attr.setOwnerScope(ScopeKind.INSTANCE);
	attr.setChangeable(ChangeableKind.NONE);
      }
      else if (keys.equals("transient")) {
	System.out.println("needs-more-work: setting to transient...");
	attr.setOwnerScope(ScopeKind.INSTANCE);
	attr.setChangeable(ChangeableKind.NONE);
      }
      else if (keys.equals("static")) {
	attr.setOwnerScope(ScopeKind.CLASSIFIER);
	attr.setChangeable(ChangeableKind.NONE);
      }
      else if (keys.equals("final")) {
	attr.setOwnerScope(ScopeKind.INSTANCE);
	attr.setChangeable(ChangeableKind.FROZEN);
      }
      else if (keys.equals("static final")) {
	attr.setOwnerScope(ScopeKind.CLASSIFIER);
	attr.setChangeable(ChangeableKind.FROZEN);
      }
      }
      catch (PropertyVetoException pve) {
        System.out.println("could not set keywords!");
       }
  }

  public void setTargetType() {
    if (_target == null) return;
    System.out.println("needs-more-work: set target type");
  }


  public void setTargetInit() {
    if (_target == null) return;
    String initStr = _initText.getText();
    Attribute attr = (Attribute) _target;
    try {
        attr.setInitialValue(new Expression(initStr));
    }
    catch (PropertyVetoException pve) { }
  }



  
  ////////////////////////////////////////////////////////////////
  // event handlers


  public void insertUpdate(DocumentEvent e) {
    System.out.println(getClass().getName() + " insert");
    Component ed = _typeField.getEditor().getEditorComponent();
    Document typeDoc = ((JTextField)ed).getDocument();

    Document initDoc = _initText.getDocument();

    if (e.getDocument() == typeDoc) setTargetType();
    else if (e.getDocument() == initDoc) setTargetInit();
    super.insertUpdate(e);
  }

  public void removeUpdate(DocumentEvent e) { insertUpdate(e); }

  public void changedUpdate(DocumentEvent e) {
    System.out.println(getClass().getName() + " changed");
    // Apparently, this method is never called.
  }

  
  
  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    if (src == _keywordsField) {
      System.out.println("attr keywords now is " +
			 _keywordsField.getSelectedItem());
      setTargetKeywords();
    }
    else if (src == _visField) {
      System.out.println("attr VisibilityKind now is " +
			 _visField.getSelectedItem());
      setTargetVisibility();
    }
    else if (src == _typeField) {
      System.out.println("attr type now is " +
			 _typeField.getSelectedItem());
      setTargetType();
    }
    else if (src == _initText) {
      // for now this cannot be called, but I might want to have
      // a history of useful expressions or something
      System.out.println("attr init now is " +
			 _initText.getText());
      setTargetInit();
    }
    
  }


} /* end class PropPanelAttr */
