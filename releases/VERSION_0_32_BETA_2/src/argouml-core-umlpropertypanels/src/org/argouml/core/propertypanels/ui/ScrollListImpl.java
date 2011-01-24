/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.core.propertypanels.ui;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;

/**
 * A scrollable list of items.<p>
 * This makes sure that there is no horizontal
 * scrollbar (which takes up too much screen real estate) and that sideways
 * scrolling can be achieved instead with arrow keys.
 * The component will automatically expand downward on mouse enter to
 * give the user a view of as many items as possible.
 * 
 * @author Bob Tarling
 */
class ScrollListImpl extends JScrollPane implements ScrollList, KeyListener {

    /**
     * The UID.
     */
    private static final long serialVersionUID = 6711776013279497682L;

    /**
     * The Component that this scroll is wrapping.
     */
    private UMLLinkedList list;
    
    /**
     * The height of the component when the mouse moved into it
     */
    int originalHeight;
    
    /**
     * Builds a JList from a given list model and wraps
     * in a scrollable view.
     * @param listModel The model from which to build the list
     */
    public ScrollListImpl(final org.argouml.core.propertypanels.ui.SimpleListModel listModel) {
        setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        list = new UMLLinkedList(
                listModel,
                true,
                false);
        setViewportView(list);
    }

    /**
     * Builds a JList from a given list model and wraps
     * in a scrollable view.
     * @param listModel The model from which to build the list
     * @param visibleRowCount an integer specifying the preferred number of
     * rows to display without requiring scrolling
     */
    public ScrollListImpl(ListModel listModel, int visibleRowCount) {
        setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        list = new UMLLinkedList(listModel, true, true);
        list.setVisibleRowCount(visibleRowCount);
        setViewportView(list);
    }

    /**
     * Builds a ScrollList from a given list model and wraps
     * in a scrollable view.
     * @param list The JList to wrap in a scroll
     * @deprecated in 0.27.2 use any other ScrollList constructor
     */
    @Deprecated
    public ScrollListImpl(JList alist) {
        setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.list = (UMLLinkedList) alist;
        setViewportView(list);
    }
    
    public ListModel getListModel() {
        return list.getModel();
    }
    
    
    /**
     * Examine key event to scroll left or right depending on key press
     * @param e the key event to examine
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            final Point posn = getViewport().getViewPosition();
            if (posn.x > 0) {
                getViewport().setViewPosition(new Point(posn.x - 1, posn.y));
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            final Point posn = getViewport().getViewPosition();
            if (list.getWidth() - posn.x > getViewport().getWidth()) {
                getViewport().setViewPosition(new Point(posn.x + 1, posn.y));
            }
        }
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void keyTyped(KeyEvent arg0) {
    }
    
    public void addNotify() {
        super.addNotify();
        list.addKeyListener(this);
    }
    
    public void removeNotify() {
        super.removeNotify();
        list.removeKeyListener(this);
    }
    
    public JList getList() {
        return list;
    }
}
