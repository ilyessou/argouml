// $Id$
// Copyright (c) 2004-2005 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This class resembles UMLModelElementListModel2, but is for those
 * associations in the metamodel (see UML standard) that have a {ordered}
 * constraint. <p>
 *
 * This adds the functionality of a popup menu with the items "Move Up"
 * and "Move Down".
 *
 * @author mvw@tigris.org
 *
 */
public abstract class UMLModelElementOrderedListModel2
    extends UMLModelElementListModel2 {

    /**
     * The constructor.
     */
    public UMLModelElementOrderedListModel2() {
        super();
    }

    /**
     * The constructor.
     *
     * @param name the name
     */
    public UMLModelElementOrderedListModel2(String name) {
        super(name);
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected abstract void buildModelList();

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(java.lang.Object)
     */
    protected abstract boolean isValidElement(Object element);

    /**
     * This function should swap two elements in the UML model.
     * The list will then be updated by the events/listener mechanism.
     *
     * @param index1 the 1st element
     * @param index2 the 2nd element
     */
    public abstract void swap(int index1, int index2);

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildPopup(
     * javax.swing.JPopupMenu, int)
     */
    public boolean buildPopup(JPopupMenu popup, int index) {
        JMenuItem moveUp =
	    new JMenuItem(
                new MoveUpAction(this, index));
        JMenuItem moveDown =
	    new JMenuItem(
                new MoveDownAction(this, index));
        popup.add(moveUp);
        popup.add(moveDown);
        return true;
    }

}


/**
 * TODO: Once finished, this class may be extracted in a seperate file,
 * for use in other places.
 *
 * @author mvw@tigris.org
 */
class MoveUpAction extends UMLAction {
    private UMLModelElementOrderedListModel2 model;
    private int index;

    /**
     * The constructor.
     */
    public MoveUpAction(UMLModelElementOrderedListModel2 theModel,
            int theIndex) {
        super("menu.popup.moveup", NO_ICON);
        model = theModel;
        index = theIndex;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        model.swap(index - 1, index);
    }
    /**
     * @see javax.swing.Action#isEnabled()
     */
    public boolean isEnabled() {
        return index > 0;
    }
}

/**
 * TODO: Once finished, this class may be extracted in a seperate file,
 * for use in other places.
 *
 * @author mvw@tigris.org
 */
class MoveDownAction extends UMLAction {
    private UMLModelElementOrderedListModel2 model;
    private int index;

    /**
     * The constructor.
     */
    public MoveDownAction(UMLModelElementOrderedListModel2 theModel,
            int theIndex) {
        super("menu.popup.movedown", NO_ICON);
        model = theModel;
        index = theIndex;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        model.swap(index, index + 1);
    }
    /**
     * @see javax.swing.Action#isEnabled()
     */
    public boolean isEnabled() {
        return model.getSize() > index + 1;
    }
}
