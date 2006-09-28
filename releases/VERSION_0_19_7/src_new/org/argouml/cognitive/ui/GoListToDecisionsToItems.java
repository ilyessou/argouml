// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.cognitive.ui;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.argouml.cognitive.Decision;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.ToDoList;


/**
 * Rule for sorting the ToDo list: Decision -> Item.
 *
 */
public class GoListToDecisionsToItems extends AbstractGoList {

    ////////////////////////////////////////////////////////////////
    // TreeModel implementation


    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {
	if (parent instanceof ToDoList) {
	    return getDecisions().elementAt(index);
	}
	if (parent instanceof Decision) {
	    Decision dec = (Decision) parent;
	    Enumeration itemEnum =
		Designer.theDesigner().getToDoList().elements();
	    while (itemEnum.hasMoreElements()) {
		ToDoItem item = (ToDoItem) itemEnum.nextElement();
		if (item.getPoster().supports(dec)) {
		    if (index == 0) return item;
		    index--;
		}
	    }
	}

	throw new IndexOutOfBoundsException("getChild shouldn't get here "
					    + "GoListToDecisionsToItems");
    }

    private int getChildCountCond(Object parent, boolean stopafterone) {
	if (parent instanceof ToDoList) {
	    return getDecisions().size();
	}
	if (parent instanceof Decision) {
	    Decision dec = (Decision) parent;
	    Enumeration itemEnum =
		Designer.theDesigner().getToDoList().elements();
	    int count = 0;
	    while (itemEnum.hasMoreElements()) {
		ToDoItem item = (ToDoItem) itemEnum.nextElement();
		if (item.getPoster().supports(dec)) count++;
		if (stopafterone && count > 0) break;
	    }
	    return count;
	}
	return 0;
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return getChildCountCond(parent, false);
    }

    /**
     * @param parent the object to check its offspring
     * @return the nr of children
     */
    private boolean hasChildren(Object parent) {
        return getChildCountCond(parent, true) > 0;
    }


    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
     * java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
	if (parent instanceof ToDoList) {
	    return getDecisions().indexOf(child);
	}
	if (parent instanceof Decision) {
	    // instead of makning a new vector, decrement index, return when
	    // found and index == 0
	    Vector candidates = new Vector();
	    Decision dec = (Decision) parent;
	    Enumeration itemEnum =
		Designer.theDesigner().getToDoList().elements();
	    while (itemEnum.hasMoreElements()) {
		ToDoItem item = (ToDoItem) itemEnum.nextElement();
		if (item.getPoster().supports(dec)) candidates.addElement(item);
	    }
	    return candidates.indexOf(child);
	}
	return -1;
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
	if (node instanceof ToDoList) return false;
	if (node instanceof Decision && hasChildren(node)) return false;
	return true;
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(
     * javax.swing.tree.TreePath, java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) { }

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) { }

    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) { }

    ////////////////////////////////////////////////////////////////
    // utility methods

    /**
     * @return the decisions
     */
    public Vector getDecisions() {
	return Designer.theDesigner().getDecisionModel().getDecisions();
    }




} /* end class GoListToDecisionsToItems */
