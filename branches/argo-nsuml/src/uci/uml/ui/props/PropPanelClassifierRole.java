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



// File: PropPanelClassifierRole.java
// Classes: PropPanelClassifierRole
// Original Author: agauthie@ics.uci.edu
// $Id$

package uci.uml.ui.props;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.java.util.collections.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.sun.java.util.collections.*;
import uci.util.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.model_management.*;
import ru.novosoft.uml.behavior.collaborations.*;
import uci.uml.ui.*;
import uci.uml.generate.*;

/** User interface panel shown at the bottom of the screen that allows
 *  the user to edit the properties of the selected UML model element.
 *  Needs-More-Work: cut and paste base class code from
 *  PropPanelClass. */

public class PropPanelClassifierRole extends PropPanel
implements ItemListener {

  ////////////////////////////////////////////////////////////////
  // constants

  ////////////////////////////////////////////////////////////////
  // instance vars
  JLabel _baseLabel = new JLabel("Base: ");
  SpacerPanel _spacer = new SpacerPanel();
  SpacerPanel _placeHolder = new SpacerPanel();

  //JTextField _baseField = new JTextField();
  JComboBox _baseField = new JComboBox();

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
    //_baseField.getDocument().addDocumentListener(this);
    _baseField.addItemListener(this);
    _baseField.setFont(_stereoField.getFont());
    _baseField.setEditable(true);
    _baseField.getEditor().getEditorComponent().setBackground(Color.white);


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
  protected void setTargetInternal(Object t) {
    super.setTargetInternal(t);
    MClassifierRole cr = (MClassifierRole) t;
    if (cr.getBases() != null) {
		Vector bases = new Vector(cr.getBases());
		Component ed = _baseField.getEditor().getEditorComponent();
		if (bases.size() == 1)
		   ((JTextField)ed).setText(((MClassifier)bases.elementAt(0)).getName());
		
		else if (bases.size() == 0)
		   ((JTextField)ed).setText("(anon)");
		
		else
		   ((JTextField)ed).setText("(multiple bases)");
		
	}
    // set the values to be shown in all widgets based on model

    //_tableModel.setTarget(cr);
    //TableColumn descCol = _extPts.getColumnModel().getColumn(0);
    //descCol.setMinWidth(50);
    validate();
  }

  ////////////////////////////////////////////////////////////////
  // event handlers


  /** The user typed some text */
 /* public void insertUpdate(DocumentEvent e) {
    super.insertUpdate(e);
    if (e.getDocument() == _baseField.getDocument()) {
      setTargetBaseString(_baseField.getText().trim());
    }
  }

  public void removeUpdate(DocumentEvent e) { 
	  // insertUpdate(e); 
  }

  public void changedUpdate(DocumentEvent e) {
    // Apparently, this method is never called.
  }
  
  */

  /** The user modified one of the widgets */
  public void itemStateChanged(ItemEvent e) {
    // check for each widget, and update the model with new value
    if (e.getStateChange() == ItemEvent.SELECTED) {
		  Object src = e.getSource();
		  
		  if (src == _namespaceField) {
			  System.out.println("namespacefield event");
			  // what to do here?
			  //setTargetInternal();
		  }
		  else if (src == _stereoField) {
			  System.out.println("stereofield event");
			  setTargetStereotype();
		  }
		  else if (src == _baseField) {
			  System.out.println("baseField event");
			  	Component ed = _baseField.getEditor().getEditorComponent();
				String baseName = ((JTextField)ed).getText();
	
			  setTargetBaseString(baseName.trim());
		  }
	  }
  }

  protected void setTargetBaseString(String s) {
	  if (_target == null) return;
	  if (_inChange) return;
	  	  MClassifierRole cr = (MClassifierRole) _target;

	  Project p = ProjectBrowser.TheInstance.getProject();
	  MClassifier type = p.findType(s);
	  if (type != null) {
		  cr.setBases(new Vector());
		  cr.addBase(type);
	  }
	  else {
		  // generate critic here? (Toby)
		  Component ed = _baseField.getEditor().getEditorComponent();
		  ((JTextField)ed).setText("(no such type)");
	  }
  }
			

} /* end class PropPanelClassifierRole */


