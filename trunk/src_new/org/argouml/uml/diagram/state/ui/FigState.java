// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.state.ui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import org.argouml.application.api.Notation;
import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigText;

/**
 * The fig hierarchy should comply as much as possible to the hierarchy of the
 * UML metamodel. Reason for this is to make sure that events from the model are
 * not missed by the figs. The hierarchy of the states was not compliant to
 * this. This resulted in a number of issues (issue 1430 for example). Therefore
 * introduced a FigState and made FigCompositeState and FigSimpleState
 * subclasses of this state.
 *
 * @author jaap.branderhorst@xs4all.nl
 * @since Dec 30, 2002
 */
public abstract class FigState extends FigStateVertex {

    /**
     * the text inside the state
     */
    private FigText internal;

    /**
     * Constructor for FigState.
     */
    public FigState() {
        super();
        internal = new FigText(getInitialX() + 2, getInitialY() + 2 + 21 + 4,
                getInitialWidth() - 4, getInitialHeight()
                        - (getInitialY() + 2 + 21 + 4));
        internal.setFont(getLabelFont());
        internal.setTextColor(Color.black);
        internal.setLineWidth(0);
        internal.setFilled(false);
        internal.setExpandOnly(true);
        internal.setMultiLine(true);
        internal.setJustification(FigText.JUSTIFY_LEFT);
    }

    /**
     * Constructor for FigState, used when an UML elm already exists.
     *
     * @param gm ignored
     * @param node the UML element
     */
    public FigState(GraphModel gm, Object node) {
        this();
        setOwner(node);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setOwner(java.lang.Object)
     */
    public void setOwner(Object node) {
        super.setOwner(node);
        updateInternal();
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        if (mee.getSource().equals(getOwner())) {
            // the events concerning the MState
            if (mee.getPropertyName().equals("classifierInState")
                    || mee.getPropertyName().equals("deferrableEvent")
                    || mee.getPropertyName().equals("internalTransition")
                    || mee.getPropertyName().equals("doActivity")
                    || mee.getPropertyName().equals("entry")
                    || mee.getPropertyName().equals("exit")) {
                updateInternal();
                // register this fig as a listener if the event is
                // about adding modelelements to the state
                updateListeners(getOwner());
                damage();
            }
            // we don't have to act on incoming and outgoing
            // transitions since that doesn't change the fig.
        } else if (ModelFacade.getInternalTransitions(getOwner()).contains(
                mee.getSource())
                || // the internal transitions
                (mee.getSource() == ModelFacade.getEntry(getOwner()))
                || // the entry
                (mee.getSource() == ModelFacade.getExit(getOwner()))
                || // the exit
                (mee.getSource() == ModelFacade.getDoActivity(getOwner()))
                || // the doacitivity
                ModelFacade.getDeferrableEvents(getOwner()).contains(
                        mee.getSource())) {
            // the defered events
            updateInternal();
            updateListeners(getOwner());
            damage();
        }

    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateListeners(java.lang.Object)
     */
    protected void updateListeners(Object newOwner) {
        super.updateListeners(newOwner);
        if (newOwner != null) {
            // register for events from all internal transitions
            Object state = newOwner;
            Iterator it = ModelFacade.getInternalTransitions(state).iterator();
            while (it.hasNext()) {
                Model.getPump().addModelEventListener(this,
                        it.next());
            }
            // register for the doactivity etc.
            if (ModelFacade.getDoActivity(state) != null) {
                Model.getPump().addModelEventListener(this,
                        ModelFacade.getDoActivity(state));
            }
            if (ModelFacade.getEntry(state) != null) {
                Model.getPump().addModelEventListener(this,
                        ModelFacade.getEntry(state));
            }
            if (ModelFacade.getExit(state) != null) {
                Model.getPump().addModelEventListener(this,
                        ModelFacade.getExit(state));
            }
        } else {
            // lets remove all registrations since this is called
            // BEFORE the owner is changed (I hope nobody is going to
            // change that...) the owner is the oldOwner
            Object state = getOwner();
            if (state != null) {
                Iterator it = ModelFacade.getInternalTransitions(state)
                        .iterator();
                while (it.hasNext()) {
                    Model.getPump().removeModelEventListener(this,
                            it.next());
                }
                if (ModelFacade.getDoActivity(state) != null) {
                    Model.getPump().removeModelEventListener(this,
                            ModelFacade.getDoActivity(state));
                }
                if (ModelFacade.getEntry(state) != null) {
                    Model.getPump().removeModelEventListener(this,
                            ModelFacade.getEntry(state));
                }
                if (ModelFacade.getExit(state) != null) {
                    Model.getPump().removeModelEventListener(this,
                            ModelFacade.getExit(state));
                }
            }

        }
    }

    /**
     * Updates the text inside the state
     */
    protected void updateInternal() {
        Object state = getOwner();
        if (state == null) return;
        String newText = Notation.generateStateBody(this, state);
        internal.setText(newText);

        calcBounds();
        setBounds(getBounds());
    }

    /**
     * @return the initial X
     */
    protected abstract int getInitialX();

    /**
     * @return the initial Y
     */
    protected abstract int getInitialY();

    /**
     * @return the initial width
     */
    protected abstract int getInitialWidth();

    /**
     * @return the initial height
     */
    protected abstract int getInitialHeight();

    /**
     * @param theInternal The internal to set.
     */
    protected void setInternal(FigText theInternal) {
        this.internal = theInternal;
    }

    /**
     * @return Returns the internal.
     */
    protected FigText getInternal() {
        return internal;
    }

}
