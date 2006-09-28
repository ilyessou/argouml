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

package org.argouml.uml.ui.behavior.state_machines;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.uml.ui.UMLMutableLinkedList;
import org.tigris.swidgets.Orientation;

/**
 * The abstract properties panel for a State.
 *
 */
public abstract class AbstractPropPanelState extends PropPanelStateVertex {

    private JScrollPane entryScroll;
    private JScrollPane exitScroll;
    private JScrollPane doScroll;
    private JScrollPane internalTransitionsScroll;
    private JScrollPane deferrableEventsScroll;
    private JList entryList;
    private JList exitList;
    private JList doList;
    private JList internalTransitionList;


    /**
     * Constructor for AbstractPropPanelState.
     * @param name the name of the properties panel, to be shown at the top
     * @param icon the icon to be shown next to the name
     * @param orientation the orientation of the panel
     */
    public AbstractPropPanelState(
                    String name, ImageIcon icon, Orientation orientation)
    {
        super(name, icon, orientation);

        JList deferrableList = new UMLStateDeferrableEventList(
                new UMLStateDeferrableEventListModel());

        deferrableEventsScroll = new JScrollPane(deferrableList);

        entryList = new UMLStateEntryList(new UMLStateEntryListModel());
        entryList.setVisibleRowCount(1);
        entryScroll = new JScrollPane(entryList);
        exitList = new UMLStateExitList(new UMLStateExitListModel());
        exitList.setVisibleRowCount(1);
        exitScroll = new JScrollPane(exitList);
        internalTransitionList = new UMLMutableLinkedList(
                new UMLStateInternalTransition(), null,
                ActionNewTransition.getInstance());
        internalTransitionsScroll = new JScrollPane(internalTransitionList);
        doList = new UMLStateDoActivityList(
                new UMLStateDoActivityListModel());
        doList.setVisibleRowCount(1);
        doScroll = new JScrollPane(doList);
    }


    /**
     * @return Returns the entryScroll.
     */
    protected JScrollPane getEntryScroll() {
        return entryScroll;
    }


    /**
     * @return Returns the exitScroll.
     */
    protected JScrollPane getExitScroll() {
        return exitScroll;
    }


    /**
     * @return Returns the doScroll.
     */
    protected JScrollPane getDoScroll() {
        return doScroll;
    }


    /**
     * @return Returns the internalTransitionsScroll.
     */
    protected JScrollPane getInternalTransitionsScroll() {
        return internalTransitionsScroll;
    }


    /**
     * @return Returns the deferrableEventsScroll.
     */
    protected JScrollPane getDeferrableEventsScroll() {
        return deferrableEventsScroll;
    }




} /* end class AbstractPropPanelState */



