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




package uci.uml.ui.props;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import uci.util.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.model_management.*;
import uci.uml.ui.*;


// needs-more-work: list of implemented interfaces
// needs-more-work: setting base class

public class PropPanelClass extends PropPanel
implements ItemListener, DocumentListener {

  ////////////////////////////////////////////////////////////////
  // constants
  public static final String VISIBILITIES[] = {
      "", MVisibilityKind.PUBLIC.getName()};// MVisibilityKind.PRIVATE.getName(), MVisibilityKind.PROTECTED.getName() };
	// what about PACKAGE in nsuml?


  public static final String CLASSKEYWORDS[] = { "none", "abstract", "final"};

  
  ////////////////////////////////////////////////////////////////
  // instance vars
  JLabel _visLabel = new JLabel("Visibility: ");
  JComboBox _visField = new JComboBox(VISIBILITIES);
  JLabel _keywordsLabel = new JLabel("Keywords: ");
  JComboBox _keywordsField = new JComboBox(CLASSKEYWORDS);
  JLabel _extendsLabel = new JLabel("Extends: ");
  JComboBox _extendsField = new JComboBox();
  JLabel _impleLabel = new JLabel("Implements: ");
  JList _implList = new JList();
  SpacerPanel _spacer = new SpacerPanel();

  ////////////////////////////////////////////////////////////////
  // contructors
  public PropPanelClass() {
    super("Class Properties");
    GridBagLayout gb = (GridBagLayout) getLayout();    
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.ipadx = 0; c.ipady = 0;


    //_visField.getEditor().getEditorComponent().setBackground(Color.white);
    //_keywordsField.getEditor().getEditorComponent().setBackground(Color.white);
    _extendsField.setEditable(true);
    _extendsField.getEditor().getEditorComponent().setBackground(Color.white);

    Component ed = _extendsField.getEditor().getEditorComponent();
    Document extendsDoc = ((JTextField)ed).getDocument();
    extendsDoc.addDocumentListener(this);
    _visField.addItemListener(this);
    _keywordsField.addItemListener(this);
    _extendsField.addItemListener(this);


    //_extendsField.setRenderer(new ModelElementRenderer());

    //_implList.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    Font labelFont = MetalLookAndFeel.getSubTextFont();
    _implList.setFont(labelFont);
    _implList.setCellRenderer(new UMLListCellRenderer());

    //_implList.addList...Listener(this);

    c.gridx = 0;
    c.gridwidth = 1;
    c.gridy = 1;
    c.weightx = 0.0;
    gb.setConstraints(_visLabel, c);
    add(_visLabel);
    c.gridy = 2;
    gb.setConstraints(_keywordsLabel, c);
    add(_keywordsLabel);
    c.gridy = 3;
    gb.setConstraints(_extendsLabel, c);
    add(_extendsLabel);


    
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
    gb.setConstraints(_extendsField, c);
    add(_extendsField);

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
    gb.setConstraints(_impleLabel, c);
    add(_impleLabel);
    c.gridy = 1;
    c.gridheight = 11;
    JScrollPane implSP = new JScrollPane(_implList);
    gb.setConstraints(implSP, c);
    add(implSP);

  }

  ////////////////////////////////////////////////////////////////
  // accessors

  protected void setTargetInternal(Object t) {
    super.setTargetInternal(t);
	//System.out.println("PropPanelClass: setTargetInternal "+t);
    MClass cls = (MClass) t;

    MVisibilityKind vk = cls.getVisibility();
    if (vk != null) 
	_visField.setSelectedItem(vk.getName());

    if (cls.isAbstract())
      _keywordsField.setSelectedItem("abstract");
    else if (cls.isLeaf())
      _keywordsField.setSelectedItem("final");
    else
      _keywordsField.setSelectedItem("none");

    Vector gens = new Vector(cls.getGeneralizations());
    MGeneralization gen = null;
    JTextField ed = (JTextField) _extendsField.getEditor().getEditorComponent();
    if (gens != null && gens.size() == 1)
      gen = (MGeneralization) gens.firstElement();
    if (gen == null) {
      //System.out.println("null base class");
      _extendsField.setSelectedItem(null);
      ed.setText("");
    }
    else {
      //System.out.println("base class found");
		MGeneralizableElement parent = gen.getParent();
      _extendsField.setSelectedItem(parent);
      if (parent != null)
		  ed.setText(parent.getName());
      else
		  ed.setText("");
    }
	/*    Vector interfaces = new Vector();
		  Vector specs = cls.getSpecification();
		  int size = specs.size();
		  for (int i = 0; i < size; i++) {
		  Realization r = (Realization) specs.elementAt(i);
		  interfaces.addElement(r.getSupertype());
		  }
		  _implList.setListData(interfaces);
		  _implList.setCellRenderer(new UMLListCellRenderer());
	*/
    updateExtendsChoices();
  }


  public void setTargetExtends() {
    if (_target == null) return;
    if (_inChange) return;
    Object base = _extendsField.getSelectedItem();
    //System.out.println("needs-more-work: baseClass = " + base);
    // needs-more-work: this could involve changes to the graph model
  }

  public void setTargetVisibility() {
    if (_target == null) return;
    if (_inChange) return;
    MVisibilityKind vk = MVisibilityKind.forName((String)_visField.getSelectedItem());
    MClass cls = (MClass) _target;
	cls.setVisibility(vk);
  }

  public void setTargetKeywords() {
    if (_target == null) return;
    if (_inChange) return;
    String keys = (String) _keywordsField.getSelectedItem();
    if (keys == null) {
      //System.out.println("keywords are null");
      return;
    }
    MClass cls = (MClass) _target;
	if (keys.equalsIgnoreCase("none")) {
		cls.setAbstract(false);
		cls.setLeaf(false);
	}
	else if (keys.equalsIgnoreCase("abstract")) {
		cls.setAbstract(true);
		cls.setLeaf(false);
	}
	else if (keys.equalsIgnoreCase("final")) {
		cls.setAbstract(false);
		cls.setLeaf(true);
	}
  }

  ////////////////////////////////////////////////////////////////
  // utility functions

  public void updateExtendsChoices() {
    // needs-more-work: build a list of existing (non-final) classes
  }

  
  ////////////////////////////////////////////////////////////////
  // event handling

  public void insertUpdate(DocumentEvent e) {
    //System.out.println(getClass().getName() + " insert");
    Component ed = _extendsField.getEditor().getEditorComponent();
    Document extendsDoc = ((JTextField)ed).getDocument();
    if (e.getDocument() == extendsDoc) setTargetExtends();
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
      //System.out.println("class keywords now is " +
      //_keywordsField.getSelectedItem());
      setTargetKeywords();
    }
    else if (src == _visField) {
      //System.out.println("class MVisibilityKind now is " +
      //_visField.getSelectedItem());
      setTargetVisibility();
    }
    else if (src == _extendsField) {
      //System.out.println("class extends now is " +
      //_extendsField.getSelectedItem());
      setTargetExtends();
    } 
	else if (src == _namespaceField) {
		// what to do here?
		//setTargetInternal();
	}
	else if (src == _stereoField) {
		setTargetStereotype();
	}
  }

  
} /* end class PropPanelClass */
