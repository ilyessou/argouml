// $Id$
// Copyright (c) 2004-2007 The Regents of the University of California. All
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

import org.argouml.i18n.Translator;
import org.tigris.gef.undo.UndoableAction;

/**
 * This class resembles UMLModelElementListModel2, but is for those associations
 * in the metamodel (see UML standard) that have a {ordered} constraint.
 * <p>
 *
 * This adds the functionality of a popup menu with the items "Move Up",
 * "Move Down", "Move to Top", and "Move to Bottom".
 *
 * @author Michiel
 */
public abstract class UMLModelElementOrderedListModel2 extends
        UMLModelElementListModel2 {

    /**
     * The constructor.
     *
     * @param name
     *            the name
     */
    public UMLModelElementOrderedListModel2(String name) {
        super(name);
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected abstract void buildModelList();

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(java.lang.Object)
     */
    protected abstract boolean isValidElement(Object element);

    /**
     * Move a element from the given position down one position, i.e. a swap of
     * the two positions. Anyone listening to the model will then be updated by
     * the events/listener mechanism. If the element is already the last element
     * in the list, nothing is done.
     *
     * @param index
     *            the current position
     */
    protected abstract void moveDown(int index);

    /**
     * Move element at given index to top of list.
     * 
     * @param index starting position of element to be moved
     */
    protected abstract void moveToTop(int index);

    /**
     * Move element at given index to bottom of list.
     * 
     * @param index starting position of element to be moved
     */
    protected abstract void moveToBottom(int index);

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildPopup(
     *      javax.swing.JPopupMenu, int)
     */
    public boolean buildPopup(JPopupMenu popup, int index) {
        JMenuItem moveToTop = new JMenuItem(new MoveToTopAction(this, index));
        JMenuItem moveUp = new JMenuItem(new MoveUpAction(this, index));
        JMenuItem moveDown = new JMenuItem(new MoveDownAction(this, index));
        JMenuItem moveToBottom = new JMenuItem(new MoveToBottomAction(this,
                index));
        popup.add(moveToTop);
        popup.add(moveUp);
        popup.add(moveDown);
        popup.add(moveToBottom);
        return true;
    }

}

/**
 * The action to move an item in the list one place up.
 *
 * @author mvw@tigris.org
 */
class MoveUpAction extends UndoableAction {
    private UMLModelElementOrderedListModel2 model;

    private int index;

    /**
     * The constructor.
     */
    public MoveUpAction(UMLModelElementOrderedListModel2 theModel, 
            int theIndex) {
        super(Translator.localize("menu.popup.moveup"));
        model = theModel;
        index = theIndex;
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        model.moveDown(index - 1);
    }

    /*
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return index > 0;
    }
}

/**
 * The action to move an item in the list one place down.
 *
 * @author mvw@tigris.org
 */
class MoveDownAction extends UndoableAction {
    private UMLModelElementOrderedListModel2 model;

    private int index;

    /**
     * The constructor.
     */
    public MoveDownAction(UMLModelElementOrderedListModel2 theModel,
            int theIndex) {
        super(Translator.localize("menu.popup.movedown"));
        model = theModel;
        index = theIndex;
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        model.moveDown(index);
    }

    /*
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return model.getSize() > index + 1;
    }
}

/**
 * Action to move an item to the top (beginning) of an ordered list model.
 *
 * @author Aleksandar Vucetica
 */
class MoveToTopAction extends UndoableAction {
    private UMLModelElementOrderedListModel2 model;

    private int index;

    /**
     * Construct an action to move the item at the given index to the top of
     * the given list.
     * 
     * @param theModel
     *            the list model to operate on
     * @param theIndex
     *            starting position of element to move
     */
    public MoveToTopAction(UMLModelElementOrderedListModel2 theModel,
            int theIndex) {
        super(Translator.localize("menu.popup.movetotop"));
        model = theModel;
        index = theIndex;
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        model.moveToTop(index);
    }

    /*
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return model.getSize() > 1 && index > 0;
    }
}

/**
 * Move an item to the bottom of the list.
 *
 * @author Aleksandar Vucetica
 */
class MoveToBottomAction extends UndoableAction {
    private UMLModelElementOrderedListModel2 model;

    private int index;

    /**
     * Construct an action to move the item at the given index to the bottom of
     * the given list.
     * 
     * @param theModel
     *            the list model to operate on
     * @param theIndex
     *            starting position of element to move
     */
    public MoveToBottomAction(UMLModelElementOrderedListModel2 theModel,
            int theIndex) {
        super(Translator.localize("menu.popup.movetobottom"));
        model = theModel;
        index = theIndex;
    }

    /*
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        model.moveToBottom(index);
    }

    /*
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return model.getSize() > 1 && index < model.getSize() - 1;
    }
}
