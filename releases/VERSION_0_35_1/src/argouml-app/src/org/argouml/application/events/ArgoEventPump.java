/* $Id$
 *******************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *******************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

package org.argouml.application.events;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.argouml.application.api.ArgoEventListener;

/**
 * ArgoEventPump is an event dispatcher which handles events that are global
 * in nature for the entire application.
 * <p>
 * TODO: DiagramAppearance and Notation events are not application-wide and will
 * be moved from here to someplace more specific in the future so that they can
 * be managed on a per-project or per-diagram basis.
 */
public final class ArgoEventPump {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ArgoEventPump.class.getName());

    /**
     * <code>listeners</code> contains the list of register listeners.
     *
     * It is a list of {@link Pair}.
     */
    private List<Pair> listeners;

    /**
     * The singleton.
     */
    static final ArgoEventPump SINGLETON = new ArgoEventPump();

    /**
     * @return the singleton
     */
    public static ArgoEventPump getInstance() {
        return SINGLETON;
    }

    /**
     * Constructor.
     */
    private ArgoEventPump() {
    }

    /**
     * @param listener The listener to be added.
     */
    public static void addListener(ArgoEventListener listener) {
        SINGLETON.doAddListener(ArgoEventTypes.ANY_EVENT, listener);
    }

    /**
     * @param event the event-type to what the listener will listen
     * @param listener the listener to be added
     */
    public static void addListener(int event, ArgoEventListener listener) {
        SINGLETON.doAddListener(event, listener);
    }

    /**
     * @param listener the listener to be removed
     */
    public static void removeListener(ArgoEventListener listener) {
        SINGLETON.doRemoveListener(ArgoEventTypes.ANY_EVENT, listener);
    }

    /**
     * @param event the event to which the listener will not listen any more
     * @param listener the listener to be removed
     */
    public static void removeListener(int event, ArgoEventListener listener) {
        SINGLETON.doRemoveListener(event, listener);
    }

    /**
     * @param event the event to what the listener will listen (?)
     * @param listener the listener to be added
     */
    protected void doAddListener(int event, ArgoEventListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<Pair>();
        }
        synchronized (listeners) {
            listeners.add(new Pair(event, listener));
        }
    }

    /**
     * Removes a listener, eventtype pair from the listener list.
     *
     * TODO: replace the listener implementation with a EventListenerList
     * for better performance
     *
     * @param event the event to which the listener will not listen any more
     * @param listener the listener to be removed
     */
    protected void doRemoveListener(int event, ArgoEventListener listener) {
        if (listeners == null) {
            return;
        }
        synchronized (listeners) {
            List<Pair> removeList = new ArrayList<Pair>();
            if (event == ArgoEventTypes.ANY_EVENT) {
                // TODO: This is a linear search of a list that contain many
                // thousands of items (one for every Fig in the entire project)
                for (Pair p : listeners) {
                    if (p.listener == listener) {
                        removeList.add(p);
                    }
                }
            } else {
                Pair test = new Pair(event, listener);
                // TODO: This is a linear search of a list that contain many
                // thousands of items (one for every Fig in the entire project)
                for (Pair p : listeners) {
                    if (p.equals(test)) {
                        removeList.add(p);
                    }
                }
            }
            listeners.removeAll(removeList);
        }
    }

    /**
     * Handle firing a notation event.
     * <p>
     * TODO: This needs to be managed on a per-diagram or per-project basis.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireNotationEvent(
        final ArgoNotationEvent event,
        final ArgoNotationEventListener listener) {

        // Notation events are likely to cause GEF/Swing operations, so we
        // dispatch them on the Swing event thread as a convenience so that
        // the receiving notationChanged() methods don't need to deal with it
        if (SwingUtilities.isEventDispatchThread()) {
            fireNotationEventInternal(event, listener);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireNotationEventInternal(event, listener);
                }
            });
        }
    }

    private void fireNotationEventInternal(ArgoNotationEvent event,
            ArgoNotationEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.NOTATION_CHANGED :
            listener.notationChanged(event);
            /* Remark: The code in
             * ProjectSettings.init() currently presumes
             * that nobody is using this event. */
            break;

        case ArgoEventTypes.NOTATION_ADDED :
            listener.notationAdded(event);
            break;

        case ArgoEventTypes.NOTATION_REMOVED :
            listener.notationRemoved(event);
            break;

        case ArgoEventTypes.NOTATION_PROVIDER_ADDED :
            listener.notationProviderAdded(event);
            break;

        case ArgoEventTypes.NOTATION_PROVIDER_REMOVED :
            listener.notationProviderRemoved(event);
            break;

	default :
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
	    break;
        }
    }

    /**
     * Handle firing a diagram appearance event.
     * <p>
     * TODO: This needs to be managed on a per-diagram or per-project basis.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireDiagramAppearanceEvent(
        final ArgoDiagramAppearanceEvent event,
        final ArgoDiagramAppearanceEventListener listener) {
        if (SwingUtilities.isEventDispatchThread()) {
            fireDiagramAppearanceEventInternal(event, listener);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireDiagramAppearanceEventInternal(event, listener);
                }
            });
        }
    }

    private void fireDiagramAppearanceEventInternal(
            final ArgoDiagramAppearanceEvent event,
            final ArgoDiagramAppearanceEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.DIAGRAM_FONT_CHANGED :
            listener.diagramFontChanged(event);
            break;
        default :
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
            break;
        }
    }

    /**
     * Handle firing a help text event.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireHelpEvent(
        ArgoHelpEvent event,
        ArgoHelpEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.HELP_CHANGED :
            listener.helpChanged(event);
            break;

        case ArgoEventTypes.HELP_REMOVED :
            listener.helpRemoved(event);
            break;

        default :
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
            break;
        }
    }


    /**
     * Handle firing a status text event.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireStatusEvent(
        ArgoStatusEvent event,
        ArgoStatusEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.STATUS_TEXT :
            listener.statusText(event);
            break;

        case ArgoEventTypes.STATUS_CLEARED :
            listener.statusCleared(event);
            break;

        case ArgoEventTypes.STATUS_PROJECT_SAVED :
            listener.projectSaved(event);
            break;

        case ArgoEventTypes.STATUS_PROJECT_LOADED :
            listener.projectLoaded(event);
            break;

        case ArgoEventTypes.STATUS_PROJECT_MODIFIED :
            listener.projectModified(event);
            break;

        default :
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
            break;
        }
    }

    /**
     * Handle firing a profile event.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireProfileEvent(
        ArgoProfileEvent event,
        ArgoProfileEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.PROFILE_ADDED:
            listener.profileAdded(event);
            break;

        case ArgoEventTypes.PROFILE_REMOVED:
            listener.profileRemoved(event);
            break;

        default:
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
            break;
        }
    }

    /**
     * Handle firing a generator event.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireGeneratorEvent(
        ArgoGeneratorEvent event,
        ArgoGeneratorEventListener listener) {
        switch (event.getEventType()) {
        case ArgoEventTypes.GENERATOR_CHANGED:
            listener.generatorChanged(event);
            break;

        case ArgoEventTypes.GENERATOR_ADDED:
            listener.generatorAdded(event);
            break;

        case ArgoEventTypes.GENERATOR_REMOVED:
            listener.generatorRemoved(event);
            break;

        default:
            LOG.log(Level.SEVERE, "Invalid event:" + event.getEventType());
            break;
        }
    }

    private void handleFireEvent(ArgoEvent event, ArgoEventListener listener) {
        if (event.getEventType() == ArgoEventTypes.ANY_EVENT) {
            if (listener instanceof ArgoNotationEventListener) {
                handleFireNotationEvent((ArgoNotationEvent) event,
					(ArgoNotationEventListener) listener);
            }
            if (listener instanceof ArgoHelpEventListener) {
                handleFireHelpEvent((ArgoHelpEvent) event,
                                        (ArgoHelpEventListener) listener);
            }
            if (listener instanceof ArgoStatusEventListener) {
                handleFireStatusEvent((ArgoStatusEvent) event,
                        (ArgoStatusEventListener) listener);
            }
        } else {
            if (event.getEventType() >= ArgoEventTypes.ANY_NOTATION_EVENT
                && event.getEventType() < ArgoEventTypes.LAST_NOTATION_EVENT) {
                if (listener instanceof ArgoNotationEventListener) {
                    handleFireNotationEvent((ArgoNotationEvent) event,
					(ArgoNotationEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes
                            .ANY_DIAGRAM_APPEARANCE_EVENT
                    && event.getEventType() < ArgoEventTypes
                            .LAST_DIAGRAM_APPEARANCE_EVENT) {
                if (listener instanceof ArgoDiagramAppearanceEventListener) {
                    handleFireDiagramAppearanceEvent(
                            (ArgoDiagramAppearanceEvent) event,
                            (ArgoDiagramAppearanceEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_HELP_EVENT
                    && event.getEventType() < ArgoEventTypes.LAST_HELP_EVENT) {
                if (listener instanceof ArgoHelpEventListener) {
                    handleFireHelpEvent((ArgoHelpEvent) event,
                            (ArgoHelpEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_GENERATOR_EVENT
                && event.getEventType() < ArgoEventTypes.LAST_GENERATOR_EVENT) {
                if (listener instanceof ArgoGeneratorEventListener) {
                    handleFireGeneratorEvent((ArgoGeneratorEvent) event,
                            (ArgoGeneratorEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_STATUS_EVENT
                    && event.getEventType() < ArgoEventTypes
                            .LAST_STATUS_EVENT) {
                if (listener instanceof ArgoStatusEventListener) {
                    handleFireStatusEvent((ArgoStatusEvent) event,
                            (ArgoStatusEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_PROFILE_EVENT
                    && event.getEventType() < ArgoEventTypes
                            .LAST_PROFILE_EVENT) {
                if (listener instanceof ArgoProfileEventListener) {
                    handleFireProfileEvent((ArgoProfileEvent) event,
                            (ArgoProfileEventListener) listener);
                }
            }
        }
    }

    /**
     * @param event the event to be fired
     */
    public static void fireEvent(ArgoEvent event) {
        SINGLETON.doFireEvent(event);
    }

    /**
     * @param event the event to be fired
     */
    protected void doFireEvent(ArgoEvent event) {
        if (listeners == null) {
            return;
        }

        // Make a read-only copy of the listeners list so that reentrant calls
        // back to add/removeListener won't mess us up.
        // TODO: Potential performance issue, but we need the correctness - tfm
        List<Pair> readOnlyListeners;
        synchronized (listeners) {
            readOnlyListeners = new ArrayList<Pair>(listeners);
        }

        for (Pair pair : readOnlyListeners) {
            if (pair.getEventType() == ArgoEventTypes.ANY_EVENT) {
                handleFireEvent(event, pair.getListener());
            } else if (pair.getEventType() == event.getEventStartRange()
                    || pair.getEventType() == event.getEventType()) {
                handleFireEvent(event, pair.getListener());
            }
        }

    }

    /**
     * Data structure handling listener registrations.
     */
    static class Pair {
        private int eventType;
        private ArgoEventListener listener;

        /**
         * Constructor.
         *
         * @param myEventType The event type.
         * @param myListener The listener.
         */
        Pair(int myEventType, ArgoEventListener myListener) {
            eventType = myEventType;
            listener = myListener;
        }

        /**
         * @return The event type.
         */
        int getEventType() {
            return eventType;
        }

        /**
         * @return The listener.
         */
        ArgoEventListener getListener() {
            return listener;
        }


        @Override
        public String toString() {
            return "{Pair(" + eventType + "," + listener + ")}";
        }


        @Override
        public int hashCode() {
            if (listener != null) {
                return eventType + listener.hashCode();
            }
            return eventType;
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof Pair) {
                Pair p = (Pair) o;
                if (p.eventType == eventType && p.listener == listener) {
                    return true;
                }
            }
            return false;
        }
    }
}
