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

package uci.uml.ui.style;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.text.Document;
import com.sun.java.swing.plaf.metal.MetalLookAndFeel;
import com.sun.java.swing.border.*;

import uci.util.*;
import uci.ui.*;
import uci.gef.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.ui.*;

public class StylePanel extends TabSpawnable
implements TabFigTarget, ItemListener, DocumentListener, ListSelectionListener, ActionListener {

  ////////////////////////////////////////////////////////////////
  // instance vars
  Fig    _target;


  ////////////////////////////////////////////////////////////////
  // constructors

  public StylePanel(String title) {
    super(title);
    GridBagLayout gb = new GridBagLayout();
    setLayout(gb);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.ipadx = 3; c.ipady = 3;
  }



  ////////////////////////////////////////////////////////////////
  // accessors

  public void setTarget(Fig t) {
    _target = t;
    refresh();
  }


  public Fig getTarget() { return _target; }

  public void refresh() {
    //_tableModel.setTarget(_target);
    //_table.setModel(_tableModel);
  }

  public boolean shouldBeEnabled() { return _target != null; }

  ////////////////////////////////////////////////////////////////
  // actions


  ////////////////////////////////////////////////////////////////
  // document event handling

  public void insertUpdate(DocumentEvent e) {
    //System.out.println(getClass().getName() + " insert");
  }

  public void removeUpdate(DocumentEvent e) { insertUpdate(e); }

  public void changedUpdate(DocumentEvent e) {
    //System.out.println(getClass().getName() + " changed");
    // Apparently, this method is never called.
  }

  ////////////////////////////////////////////////////////////////
  // combobox event handling

  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
//     if (src == _persCombo) {
//       //System.out.println("class keywords now is " +
//       //_keywordsField.getSelectedItem());
//       setTablePerspective();
//     }
//     else if (src == _filterCombo) {
//       //System.out.println("class VisibilityKind now is " +
//       //_visField.getSelectedItem());
//       setFilter();
//     }
  }

  /////////////////////////////////////////////////////////////////
  // ListSelectionListener implemention

  public void valueChanged(ListSelectionEvent lse) {
//     if (lse.getValueIsAdjusting()) return;
//     Object src = lse.getSource();
//     if (src == _table.getSelectionModel()) {
//       int row = lse.getFirstIndex();
//       if (_tableModel != null) {
// 	Vector rowObjects = _tableModel.getRowObjects();
// 	if (row >= 0 && row < rowObjects.size()) {
// 	  Object sel = rowObjects.elementAt(row);
// 	  objectSelected(sel);
// 	  return;
// 	}
//       }
//     }
//     objectSelected(null);
  }

//   public void objectSelected(Object sel) {
//     ProjectBrowser pb = ProjectBrowser.TheInstance;
//     pb.setDetailsTarget(sel);
//   }
  
  /////////////////////////////////////////////////////////////////
  // ActionListener implementation

  public void actionPerformed(ActionEvent ae) {
    Object src = ae.getSource();
    //if (src == _config) doConfig();
  }

  
} /* end class StylePanel */



