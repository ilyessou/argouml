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

package uci.uml.ui.nav;

import java.util.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;

import uci.uml.Model_Management.*;
import uci.uml.Foundation.Core.*;
import uci.uml.visual.UMLDiagram;
import uci.uml.ui.*;

public class GoModelToDiagram implements TreeModelPrereqs {

  public String toString() { return "Package->Diagram"; }

  public Object getRoot() {
    System.out.println("getRoot should never be called");
    return null;
  }

  public Object getChild(Object parent, int index) {
    if (parent instanceof Namespace) {
      Namespace m = (Namespace) parent;
      Project proj = ProjectBrowser.TheInstance.getProject();
      Vector diags = proj.getDiagrams();
      java.util.Enumeration diagEnum = diags.elements();
      while (diagEnum.hasMoreElements()) {
	UMLDiagram d = (UMLDiagram) diagEnum.nextElement();
	if (d.getNamespace() == m) index--;
	if (index == -1) return d;
      }
    }
    System.out.println("getChild should never be get here GoModelToDiagram");
    return null;
  }

  public int getChildCount(Object parent) {
    if (parent instanceof Namespace) {
      int count = 0;
      Namespace m = (Namespace) parent;
      Project proj = ProjectBrowser.TheInstance.getProject();
      Vector diags = proj.getDiagrams();
      java.util.Enumeration diagEnum = diags.elements();
      while (diagEnum.hasMoreElements()) {
	UMLDiagram d = (UMLDiagram) diagEnum.nextElement();
	if (d.getNamespace() == m) count++;
      }
      return count;
    }
    return 0;
  }

  public int getIndexOfChild(Object parent, Object child) {
    if (parent instanceof Namespace) {
      int count = 0;
      Namespace m = (Namespace) parent;
      Project proj = ProjectBrowser.TheInstance.getProject();
      Vector diags = proj.getDiagrams();
      java.util.Enumeration diagEnum = diags.elements();
      while (diagEnum.hasMoreElements()) {
	UMLDiagram d = (UMLDiagram) diagEnum.nextElement();
	if (d.getNamespace() != m) continue;
	if (d == child) return count;
	count++;
      }
      return count;
    }
    return -1;
  }

  public boolean isLeaf(Object node) {
    return !(node instanceof Namespace && getChildCount(node) > 0);
  }

  public void valueForPathChanged(TreePath path, Object newValue) { }
  public void addTreeModelListener(TreeModelListener l) { }
  public void removeTreeModelListener(TreeModelListener l) { }

  public Vector getPrereqs() {
    Vector pres = new Vector();
    pres.addElement(uci.uml.Model_Management.Model.class);
    return pres;
  }
  public Vector getProvidedTypes() {
    Vector pros = new Vector();
    pros.addElement(uci.gef.Diagram.class);
    return pros;
  }

}
