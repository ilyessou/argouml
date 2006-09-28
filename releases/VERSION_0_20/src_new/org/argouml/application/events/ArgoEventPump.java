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

package org.argouml.application.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.argouml.application.api.ArgoEventListener;

/**
 * ArgoEventPump is an eventhandler which handles events regarding
 * the loading and unloading of modules.
 */
public final class ArgoEventPump {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(ArgoEventPump.class);

    /**
     * <code>listeners</code> contains the list of register listeners.
     *
     * It is a list of {@link Pair}.
     */
    private ArrayList listeners = null;

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
     * @param event the event to what the listener will listen (?)
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
            listeners = new ArrayList();
        }
        listeners.add(new Pair(event, listener));
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
        Iterator it = listeners.iterator();
        List removeList = new ArrayList();
        if (event == ArgoEventTypes.ANY_EVENT) {

            while (it.hasNext()) {
                Pair p = (Pair) it.next();
                if (p.listener == listener) {
                    removeList.add(p);
                }
            }

        } else {
            Pair test = new Pair(event, listener);
            while (it.hasNext()) {
                Pair p = (Pair) it.next();
                if (p.equals(test)) {
                    removeList.add(p);
                }
            }
        }
        listeners.removeAll(removeList);
    }

    /**
     * Handle firing a module event to a given listener.
     *
     * @param event The event fired.
     * @param listener The listener.
     */
    private void handleFireModuleEvent(
        ArgoModuleEvent event,
        ArgoModuleEventListener listener) {
        switch (event.getEventType()) {
	case ArgoEventTypes.MODULE_LOADED :
	    listener.moduleLoaded(event);
	    break;

	case ArgoEventTypes.MODULE_UNLOADED :
	    listener.moduleUnloaded(event);
	    break;

	case ArgoEventTypes.MODULE_ENABLED :
	    listener.moduleEnabled(event);
	    break;

	case ArgoEventTypes.MODULE_DISABLED :
	    listener.moduleDisabled(event);
	    break;

	default :
	    LOG.error("Invalid event:" + event.getEventType());
	    break;
        }
    }

    /**
     * Handle firing a notation event.
     *
     * @param event The event to be fired.
     * @param listener The listener.
     */
    private void handleFireNotationEvent(
        ArgoNotationEvent event,
        ArgoNotationEventListener listener) {
        switch (event.getEventType()) {
	case ArgoEventTypes.NOTATION_CHANGED :
	    listener.notationChanged(event);
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
	    LOG.error("Invalid event:" + event.getEventType());
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
            LOG.error("Invalid event:" + event.getEventType());
            break;
        }
    }

    private void handleFireEvent(ArgoEvent event, ArgoEventListener listener) {
        if (event.getEventType() == ArgoEventTypes.ANY_EVENT) {
            if (listener instanceof ArgoModuleEventListener) {
                handleFireModuleEvent((ArgoModuleEvent) event,
				      (ArgoModuleEventListener) listener);
            }
            if (listener instanceof ArgoNotationEventListener) {
                handleFireNotationEvent((ArgoNotationEvent) event,
					(ArgoNotationEventListener) listener);
            }
        } else {
            if (event.getEventType() >= ArgoEventTypes.ANY_MODULE_EVENT
                && event.getEventType() < ArgoEventTypes.LAST_MODULE_EVENT) {
                if (listener instanceof ArgoModuleEventListener) {
                    handleFireModuleEvent((ArgoModuleEvent) event,
					  (ArgoModuleEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_NOTATION_EVENT
                && event.getEventType() < ArgoEventTypes.LAST_NOTATION_EVENT) {
                if (listener instanceof ArgoNotationEventListener) {
                    handleFireNotationEvent((ArgoNotationEvent) event,
					(ArgoNotationEventListener) listener);
                }
            }
            if (event.getEventType() >= ArgoEventTypes.ANY_GENERATOR_EVENT
                && event.getEventType() < ArgoEventTypes.LAST_GENERATOR_EVENT) {
                if (listener instanceof ArgoGeneratorEventListener) {
                    handleFireGeneratorEvent((ArgoGeneratorEvent) event,
                            (ArgoGeneratorEventListener) listener);
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

        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            Pair pair = (Pair) iterator.next();
            if (pair.getEventType() == ArgoEventTypes.ANY_EVENT) {
                handleFireEvent(event, pair.getListener());
            } else if (
                (pair.getEventType() >= event.getEventStartRange())
                    && (pair.getEventType() <= event.getEventEndRange())) {
                handleFireEvent(event, pair.getListener());
            }
        }

    }

    /**
     * Data structure handling listener registrations.
     */
    class Pair {
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

        int getEventType() {
            return eventType;
        }

        ArgoEventListener getListener() {
            return listener;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "{Pair(" + eventType + "," + listener + ")}";
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            if (listener != null) {
                return eventType + listener.hashCode();
            }
            return eventType;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
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
