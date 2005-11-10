// $Id$
// Copyright (c) 2002-2005 The Regents of the University of California. All
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

package org.argouml.model.uml;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;
import ru.novosoft.uml.MFactoryImpl;


/**
 * This class implements an event pump for all modelevents (MEvents
 * with the current NSUML model). Two kinds of listeners can be
 * registred to the pump: listeners to class events and listeners to
 * object events. The pump dispatches all events fired by objects of a
 * certain class to the class listeners (listeners that are registred
 * via addClassModelEventListener). Furthermore, it dispatches all
 * events to listeners that are registered for a certain object if
 * this object fired the original event.<p>
 *
 * This class is part of the NSUML-dependant API of the model component.
 * Use the NSUML-free {@link org.argouml.model.Model#getPump()} and the
 * {@link org.argouml.model.ModelEventPump} interface everywhere except
 * within the NSUML-implementation.
 *
 * Maybe this class should dispatch a thread to handle the incoming
 * event in the future.
 * @since Oct 14, 2002
 * @author jaap.branderhorst@xs4all.nl
 *
 * TODO: Change visibility to package after reflection problem solved.
 */
public final class UmlModelEventPump implements MElementListener {

    /**
     * The Logger.
     */
    private static final Logger LOG =
	    Logger.getLogger(UmlModelEventPump.class);

    /**
     * Indicate wether we pump events to listeners yes or no.
     * There is some fault in the NSUML implementation
     * but where...
     */
    private boolean pumping = true;

    /**
     * "remove".
     */
    public static final String REMOVE = "remove";

    private static UmlModelEventPump theInstance = new UmlModelEventPump();

    /**
     * The 'map' with the eventlistenerlists per modelelement.
     */
    private EventListenerHashMap listenerMap = new EventListenerHashMap();

    private ClassListenerHashMap classListenerMap = new ClassListenerHashMap();

    private EventTreeDefinition definition = new EventTreeDefinition();

    /**
     * Singleton access method.
     *
     * @return UmlModelEventPump
     */
    public static synchronized UmlModelEventPump getPump() {
        return theInstance;
    }

    /**
     * Constructor for UmlModelEventPump, initialises the NSUML event policy.
     */
    private UmlModelEventPump() {
        super();
        // Initialize NSUML
	MFactoryImpl.setEventPolicy(MFactoryImpl.EVENT_POLICY_IMMEDIATE);
    }

    /**
     * Adds a listener that listens to all modelevents that are named
     * eventNames and that occur to instances of a given
     * modelClass.<p>
     *
     * If you want the listener to be registered for remove events
     * (that is: an instance of the class the listener is listening
     * too is removed), then you have to register for the eventname
     * "remove".<p>
     *
     * @param listener is the listener to add.
     * @param metaType is the given model class
     * @param eventNames is a array of strings with event names.
     * @throws IllegalArgumentException if one of the arguments is null or if
     * the modelClass is not a subclass of MBase.
     * @throws IllegalStateException if the listener is allready registred
     */
    public void addClassModelEventListener(MElementListener listener,
					   Object metaType,
					   String[] eventNames) {
        Class modelClass = (Class) metaType;
//      We don't support non-NSUML modeleventlisteners yet,
//      so we return without addition.
        if (!MBase.class.isAssignableFrom(modelClass)) {
            return;
        }
        if (listener == null
            || modelClass == null
            || eventNames == null
            || eventNames.length == 0) {
            throw new IllegalArgumentException("Tried to add illegal class"
					       + " modeleventlistener to "
					       + "possible null class");
        }
        for (int i = 0; i < eventNames.length; i++) {
            executeAddClassModelEventListener(
					      listener,
					      modelClass,
					      eventNames[i]);
        }
    }

    /**
     * Convinience method to add a listener that only listens to one specific
     * event.
     *
     * @param listener The listener to add
     * @param metaType The listener should listen to instances of this class
     * @param eventName The name of the event the listener wants to listen
     * too.
     */
    public void addClassModelEventListener(MElementListener listener,
					   Object metaType,
					   String eventName) {
        Class modelClass = (Class) metaType;
        // We don't support non-NSUML modeleventlisteners yet,
        // so we return without addition.

        if (!MBase.class.isAssignableFrom(modelClass)) {
            return;
        }
        if (listener == null
            || modelClass == null
            || eventName == null
            || eventName.equals("")) {
            throw new IllegalArgumentException();
        }
        executeAddClassModelEventListener(listener, modelClass, eventName);
    }

    /**
     * Does the actual adding.
     *
     * @param listener The listener to add
     * @param modelClass The listener should listen to instances of this class
     * @param eventName The name of the event the listener wants to listen
     */
    private synchronized void executeAddClassModelEventListener(
	    MElementListener listener,
	    Class modelClass,
	    String eventName) {
        // first register the listener for all elements allready in
        // the model modelClass = formatClass(modelClass);
        Object model = Model.getModelManagementFactory().getRootModel();
        Collection col =
            Model.getModelManagementHelper()
	        .getAllModelElementsOfKindWithModel(model, modelClass);

        if (col == Collections.EMPTY_LIST) {
            col = new ArrayList();
        }

        /*
        if (modelClass.isAssignableFrom(MModel.class)) {
            Object root =
                ProjectManager.getManager().getCurrentProject().getRoot();
            if (root != null)
                col.add(root);
        }
        */
        modelClass = formatClass(modelClass);
        EventKey[] keys = definition.getEventTypes(modelClass, eventName);
        Iterator it = col.iterator();
        while (it.hasNext()) {
            MBase base = (MBase) it.next();
            for (int i = 0; i < keys.length; i++) {
                listenerMap.put(base, keys[i], listener);
            }
        }
        // add the class to the 'interested classes list' so the listener is
        // added on creation of a modelelement
        for (int i = 0; i < keys.length; i++) {
            classListenerMap.put(modelClass, keys[i], listener);
        }
    }
    /**
     * Retrieves the  implementation class belonging to some given class. For
     * example, retrieves ClassImpl.class if the input was Class.class or
     * ClassImpl.class.
     * @param inputClass An interface or implementation class from NSUML
     * @return The implementation class from NSUML
     */
    private Class formatClass(Class inputClass) {
        String name = inputClass.getName();
        if (name.endsWith("Impl")) {
            return inputClass;
        }
        try {
            Class returnClass = Class.forName(name + "Impl");
            return returnClass;
        } catch (ClassNotFoundException ignorable) {
            // cannot happen
        }
        return null;

    }

    /**
     * Removes a listener that listens to all modelevents fired by instances of
     * modelClass and that have the original name eventNames.
     * @param listener The listener to remove
     * @param metaType The class the listener does not want to listen to
     * instances anymore
     * @param eventNames The eventnames the listener does not want to listen to
     * anymore
     */
    public void removeClassModelEventListener(
					      MElementListener listener,
					      Object metaType,
					      String[] eventNames) {
        Class modelClass = (Class) metaType;
        // we don't support eventlisteners other then NSUML ModelEventListener.
        // to be forward compatible with other ModelEventListeners,
        // we do not throw an exception but
        // simply return.
        if (!MBase.class.isAssignableFrom(modelClass)) {
            // throw new IllegalArgumentException();
            return;
        }
        if (listener == null
            || modelClass == null
            || eventNames == null
            || eventNames.length == 0) {
            throw new IllegalArgumentException(
                    "Tried to remove invalid listener");
        }
        for (int i = 0; i < eventNames.length; i++) {
            executeRemoveClassModelEventListener(
						 listener,
						 modelClass,
						 eventNames[i]);
        }
    }

    /**
     * Convinience method to remove a listener that listens to events named
     * eventName that are fired by instances of modelClass.
     *
     * @param listener The listener to remove
     * @param metaType The class the listener does not want to listen to
     * instances anymore
     * @param eventName The eventname the listener does not want to listen to
     * anymore
     */
    public void removeClassModelEventListener(
					      MElementListener listener,
					      Object metaType,
					      String eventName) {
        Class modelClass = (Class) metaType;
        // we don't support eventlisteners other then NSUML ModelEventListener.
        // to be forward compatible with other ModelEventListeners,
        // we do not throw an exception but
        // simply return.
        if (!MBase.class.isAssignableFrom(modelClass)) {
            // throw new IllegalArgumentException();
            return;
        }
        if (listener == null
            || modelClass == null
            || eventName == null) {
            throw new IllegalArgumentException(
                    "Tried to remove invalid listener");
        }
        executeRemoveClassModelEventListener(listener, modelClass, eventName);
    }

    /**
     * Executes the removal of a listener to a class.
     *
     * @param listener The listener to remove
     * @param modelClass The class the listener does not want to listen to
     * instances anymore
     * @param eventName The eventname the listener does not want to listen to
     * anymore
     */
    private synchronized void executeRemoveClassModelEventListener(
	    MElementListener listener,
	    Class modelClass,
	    String eventName) {
        // remove all registrations of this listener with all instances of
        // modelClass
        //modelClass = formatClass(modelClass);
        Object model = Model.getModelManagementFactory().getRootModel();
        Iterator it =
            Model.getModelManagementHelper()
	        .getAllModelElementsOfKindWithModel(model, modelClass)
	            .iterator();
        while (it.hasNext()) {
            MBase base = (MBase) it.next();
            removeModelEventListener(listener, base, eventName);
        }
        // remove the listener from the registry
        EventKey[] keys = definition.getEventTypes(modelClass, eventName);
        for (int i = 0; i < keys.length; i++) {
            classListenerMap.remove(modelClass, keys[i], listener);
        }
    }

    /**
     * Adds a listener to modelevents that are fired by some given modelelement
     * and that have the name eventNames.<p>
     *
     * If you want the listener to be registred for remove events (that is: the
     * instance the listener is listening too is removed), then you have to
     * register for the eventname "remove".
     *
     * @param listener The listener to add
     * @param modelelement The modelelement the listener should be added too
     * @param eventNames The array of eventnames the listener should listen
     *                   to.
     */
    public void addModelEventListener(
				      Object listener,
				      Object modelelement,
				      String[] eventNames) {
        // we just return if the modelelement is not a base.
        // we don't support non-nsuml elements yet.
        if (modelelement == null || !(modelelement instanceof MBase)) {
            return;
        }
        if (listener == null
            || eventNames == null
            || eventNames.length == 0
            || !(listener instanceof MElementListener)
            || !(modelelement instanceof MBase)) {
            throw new IllegalArgumentException("Wrong argument types while "
					       + "adding a modelelement "
					       + "listener");
        }
        for (int i = 0; i < eventNames.length; i++) {
            EventKey[] keys =
                definition.getEventTypes(
					  modelelement.getClass(),
					  eventNames[i]);
            for (int j = 0; j < keys.length; j++) {
                listenerMap.remove(
				    (MBase) modelelement,
				    keys[j],
				    (MElementListener) listener);
                listenerMap.put(
				 (MBase) modelelement,
				 keys[j],
				 (MElementListener) listener);
            }
        }
    }

    /**
     * Convenience method to add a listener that only listens to one specific
     * event.<p>
     *
     * @param listener The listener to add.
     * @param modelelement The modelelement the listener should be added to.
     * @param eventName The eventname the listener should listen to.
     */
    public void addModelEventListener(Object listener,
				      Object modelelement,
				      String eventName) {

        // we just return if the modelelement to add is not a NSUML class.
        // we don't support other event listeners yet.
        if (modelelement == null || !(modelelement instanceof MBase)) {
            return;
        }
        if (listener == null
            || !(listener instanceof MElementListener)
            || eventName == null) {
            throw new IllegalArgumentException();
        }
        EventKey[] keys =
            definition.getEventTypes(modelelement.getClass(), eventName);
        for (int i = 0; i < keys.length; i++) {
            listenerMap.remove((MBase) modelelement,
				keys[i],
				(MElementListener) listener);
            listenerMap.put((MBase) modelelement,
			     keys[i],
			     (MElementListener) listener);
        }
    }

    /**
     * Adds a listener to all events fired by some modelelement.
     *
     * <em>Note:</em> Due to the fact that ALL events are pumped for
     * some modelelement, this is a rather powerfull method but also
     * one that can hog performance. Use this with care!<p>
     *
     * @param listener is the listener to add
     * @param modelelement is the model element
     */
    public void addModelEventListener(Object listener, Object modelelement) {
        // we just return if the modelelement to add is not a NSUML class.
        // we don't support other event listeners yet.
        if (listener == null) {
            throw new IllegalArgumentException(
                    "A listener must be supplied");
        }
        if (modelelement == null) {
            throw new IllegalArgumentException(
                    "A model element must be supplied");
        }
        if (!(modelelement instanceof MBase)) {
            throw new IllegalArgumentException(
                    "The model element must be an MBase got a "
                    + modelelement.getClass().getName());
        }
        if (!(listener instanceof MElementListener)) {
            throw new IllegalArgumentException(
                    "The listener must be an MElementListener - got a "
                    + listener.getClass().getName());
        }
        EventKey[] keys = definition.getEventTypes(modelelement.getClass());
        for (int i = 0; i < keys.length; i++) {
            listenerMap.remove(
				(MBase) modelelement,
				keys[i],
				(MElementListener) listener);
            listenerMap.put(
			     (MBase) modelelement,
			     keys[i],
			     (MElementListener) listener);
        }

    }

    /**
     * Removes a listener that listens to modelevents with name
     * eventNames that are fired by the given modelelement.<p>
     *
     * @param listener The listener to remove
     * @param handle The modelelement that fires the events the
     * listener is listening to
     * @param eventNames The list of event names the listener is
     * interested in
     */
    public void removeModelEventListener(Object/*MElementListener*/ listener,
					 Object handle,
					 String[] eventNames) {
        if (handle == null) {
            return;
        }
        // we don't support eventlisteners other then NSUML ModelEventListener.
        // to be forward compatible with other ModelEventListeners,
        // we do not throw an exception but
        // simply return.
        if (!(handle instanceof MBase)) {
            // throw new IllegalArgumentException();
            return;
        }
        MBase modelElement = (MBase) handle;
        if (listener == null
            || !(listener instanceof MElementListener)
            || modelElement == null
            || eventNames == null
            || eventNames.length == 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < eventNames.length; i++) {
            EventKey[] keys =
                definition.getEventTypes(
					  modelElement.getClass(),
					  eventNames[i]);
            for (int j = 0; j < keys.length; j++) {
                listenerMap.remove(modelElement,
				    keys[j],
				    (MElementListener) listener);
            }
        }
    }

    /**
     * Removes a listener that listens to all events fired by the
     * given modelelement.<p>
     *
     * @param listener is the listener
     * @param handle is the model element
     */
    public void removeModelEventListener(Object/*MElementListener*/ listener,
					 Object handle) {
        if (handle == null) {
            return;
        }
        // we don't support eventlisteners other then NSUML ModelEventListener.
        // to be forward compatible with other ModelEventListeners,
        // we do not throw an exception but
        // simply return.
        if (!(handle instanceof MBase)) {
            // throw new IllegalArgumentException();
            return;
        }

	MBase modelElement = (MBase) handle;
        if (listener == null
	    || modelElement == null
	    || !(listener instanceof MElementListener)) {

            throw new IllegalArgumentException("listener is null or "
					       + "an illegal type");

	}
        EventKey[] keys = definition.getEventTypes(modelElement.getClass());
        for (int i = 0; i < keys.length; i++) {
            listenerMap.remove(modelElement,
			       keys[i],
			       (MElementListener) listener);
        }
    }

    /**
     * Convenience method to remove a listener to some event.<p>
     *
     * @param listener is the listener to remove
     * @param handle is the object
     * @param eventName is the name of the event
     */
    public void removeModelEventListener(Object/*MElementListener*/ listener,
					 Object handle,
					 String eventName) {
        if (handle == null) {
            return;
        }
        // we don't support eventlisteners other then NSUML ModelEventListener.
        // to be forward compatible with other ModelEventListeners,
        // we do not throw an exception but
        // simply return.
        if (!(handle instanceof MBase)) {
            // throw new IllegalArgumentException();
            return;
        }
        MBase modelElement = (MBase) handle;
        if (listener == null
	    || modelElement == null
	    || eventName == null
	    || !(listener instanceof MElementListener)) {

            throw new IllegalArgumentException("null listener or illegal type");
	}
        EventKey[] keys =
            definition.getEventTypes(modelElement.getClass(), eventName);
        for (int j = 0; j < keys.length; j++) {
            listenerMap.remove(modelElement,
				keys[j],
				(MElementListener) listener);
        }

    }

    /**
     * Method to remove some element from the listenerObjectMap. Used by
     * delete on UmlFactory to make sure all listeners are removed.
     * @param element
     */
    synchronized void cleanUp(MBase element) {
        listenerMap.remove(element);
    }

    /**
     * Make all event sources described by the given document
     * available to the UMLModelEventPump. The document must match the
     * pattern given by the following example:
     *
     * <pre>
     * &lt;eventtree&gt;
     *   &lt;source classname="classname of a model element"&gt;
     *     &lt;eventtype name="event fired by this model element"&gt;
     *       &lt;type&gt;1&lt;/type&gt;
     *     &lt;/eventtype&gt;
     *   &lt;/source&gt;
     * &lt;/eventtree&gt;
     * </pre>
     *
     * The classname is fully qualified.<p>
     *
     * The type is one of the numbers defined in {@link
     * MElementEvent}. Each of the elements can be used multiple
     * times.
     *
     * This operation is particularly provided for use by modules
     * which add custom model elements.
     *
     * @param doc the document, the contents of which should be made available.
     *            This must be preparsed.
     */
    public void addEventSourcesFromDocument (Document doc) {
        definition.addSourcesFromDocument(doc);
    }

    /**
     * Make event types for a single event source available to this
     * UMLModelEventPump.<p>
     *
     * This operation is particularly provided for use by modules which add
     * custom model elements.
     *
     * @param cSource the source class for which to make event types available.
     *
     * @param mpNameMap a map of the event types to add for the source class.
     *                  The keys are Strings indicating the names of events,
     *                  while the values are int[] which contain all the event
     *                  types the indicated event represents. The specific int
     *                  values used can be found in {@link MElementEvent}.
     */
    public void addEventSource (Class cSource, Map mpNameMap) {
        definition.addSource(cSource, mpNameMap);
    }

    /**
     * @see MElementListener#listRoleItemSet(MElementEvent)
     */
    public void listRoleItemSet(MElementEvent e) {
        MElementListener[] listeners = getListenerList(e);
        for (int i = 0; i < listeners.length; i++) {
            try {
                listeners[i].listRoleItemSet(e);
            } catch (Exception re) {
                LOG.error("Listener " + listeners[i]
                	  + " threw an unhandled Exception", re);
            }
        }
    }

    private MElementListener[] getListenerList(MElementEvent e) {
        return listenerMap.getListeners((MBase) e.getSource(),
					new EventKey(e.getType(),
						     e.getName()));
    }

    /**
     * @see MElementListener#propertySet(MElementEvent)
     */
    public void propertySet(MElementEvent e) {
        if (!pumping) {
            return;
        }
        if (e.getNewValue() == null
            || !(e.getNewValue().equals(e.getOldValue()))) {
            MElementListener[] listeners = getListenerList(e);
            for (int i = 0; i < listeners.length; i++) {
		try {
		    listeners[i].propertySet(e);
		} catch (Exception re) {
		    LOG.error("Listener " + listeners[i]
			      + " threw an unhandled Exception", re);
		}
            }
        }
    }

    /**
     * @see MElementListener#recovered(MElementEvent)
     */
    public void recovered(MElementEvent e) {
        if (!pumping) {
            return;
        }
        MElementListener[] listeners = getListenerList(e);
        for (int i = 0; i < listeners.length; i++) {
	    try {
		listeners[i].recovered(e);
	    } catch (Exception re) {
		LOG.error("Listener " + listeners[i]
			  + " threw an unhandled Exception", re);
	    }
        }
    }

    /**
     * @see MElementListener#removed(MElementEvent)
     */
    public void removed(MElementEvent e) {
        if (!pumping) {
            return;
        }
        MElementListener[] listeners = getListenerList(e);
        for (int i = 0; i < listeners.length; i++) {
	    try {
		listeners[i].removed(e);
	    } catch (Exception re) {
		LOG.error("Listener " + listeners[i]
			  + " threw an unhandled Exception", re);
	    }
        }
    }

    /**
     * @see MElementListener#roleAdded(MElementEvent)
     */
    public void roleAdded(MElementEvent e) {
        if (!pumping) {
            return;
        }
        MElementListener[] listeners = getListenerList(e);
        for (int i = 0; i < listeners.length; i++) {
	    try {
		listeners[i].roleAdded(e);
	    } catch (Exception re) {
		LOG.error("Listener " + listeners[i]
			  + " threw an unhandled Exception", re);
	    }
        }
    }

    /**
     * @see MElementListener#roleRemoved(MElementEvent)
     */
    public void roleRemoved(MElementEvent e) {
        if (!pumping) {
            return;
        }
        MElementListener[] listeners = getListenerList(e);
        for (int i = 0; i < listeners.length; i++) {
	    try {
		listeners[i].roleRemoved(e);
	    } catch (Exception re) {
		LOG.error("Listener " + listeners[i]
			  + " threw an unhandled Exception", re);
	    }
        }
    }

    /**
     * Clears the hashmaps with listeners. This is only needed by the JUnit
     * tests. It's an implementation detail that the visibility is public.
     * THIS METHOD SHOULD NOT BE USED OUTSIDE JUNIT TESTS.
     */
    public void cleanUp() {
        listenerMap = null;
        listenerMap = new EventListenerHashMap();
        classListenerMap = null;
        classListenerMap = new ClassListenerHashMap();
    }

    ClassListenerHashMap getClassListenerMap() {
        return classListenerMap;
    }

    EventListenerHashMap getEventListenerMap() {
        return listenerMap;
    }

    /**
     * changes the NSUML event policy in order to stop udating the ui.
     */
    public void stopPumpingEvents() {
        pumping = false;

        MFactoryImpl.setEventPolicy(MFactoryImpl.EVENT_POLICY_DISABLED);
    }

    /**
     * changes the NSUML event policy in order to start udating the ui.
     */
    public void startPumpingEvents() {
        pumping = true;

        MFactoryImpl.flushEvents();
        MFactoryImpl.setEventPolicy(MFactoryImpl.EVENT_POLICY_IMMEDIATE);
    }

    /**
     * changes the NSUML event policy to flush model events.
     */
    public void flushModelEvents() {
        MFactoryImpl.flushEvents();
    }
}

/**
 * Value object class to find the correct eventlistenerlist in the
 * eventhashmap.<p>
 *
 * @author jaap.branderhorst@xs4all.nl
 */
class EventKey {
    private Integer type;
    private String name;

    static final EventKey EMPTY_KEY = new EventKey(null, null);

    public EventKey(int t, String n) {
        setType(t);
        setName(n);
    }

    /**
     * Constructor for the event key.
     *
     * @param t The type.
     * @param n The name.
     */
    public EventKey(Integer t, String n) {
        type = t;
        setName(n);
    }

    /**
     * @param t The new type to set.
     */
    private void setType(int t) {
        if (t < 0 || t > 10) {
            throw new IllegalArgumentException(
                "This is not a legal eventtype: " + t);
        }
        type = new Integer(t);
    }

    /**
     * @return The type.
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param n The new name.
     */
    private void setName(String n) {
        name = n;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int code = 0;
        if (getType() != null) {
            code += getType().hashCode();
        }
        if (getName() != null) {
            code += getName().hashCode();
        }
        return code;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof EventKey) {
            EventKey key = (EventKey) o;
            if ((key.getType() == null && getType() == null)
                || (key.getType() != null
		    && key.getType().equals(getType())
		    && key.getName() != null
		    && key.getName().equals(getName()))) {
                return true;
            }
        }
        return false;
    }

}

/**
 * A wrapped object array that contains triples of eventtype,
 * eventname, interested listener. I used an Object array for this
 * purpose since it performs fast. Copied a lot of the code from
 * javax.swing.EventListenerList.
 * @author jaap.branderhorst@xs4all.nl
 */
class EventListenerList {
    private static final Logger LOG = Logger.getLogger(EventListenerList.class);
    /**
     * A null array to be shared by all empty listener lists.
     */
    private static final Object[] NULL_ARRAY = new Object[0];
    /**
     *  The list of EventKey - Listener pairs. It's visibility is default since
     * it's used in AbstractUmlModelFactory in a performant but quite awkward
     * way from an encapsulation point of view.
     */
    private Object[] listenerList = NULL_ARRAY;

    /**
     * Returns an array of listeners that are interested in an event
     * that is typed by the given EventKey. If the name is null of the
     * EventKey, all listeners are returned that have the
     * corresponding name filled or null. Same is true for the type.
     *
     * @param key
     * @return An array of listeners that are interested in the event typed by
     * the given EventKey
     */
    public synchronized MElementListener[] getListeners(EventKey key) {
        Object[] lList = listenerList;
        int n = getListenerCount(lList, key);
        MElementListener[] result =
            (MElementListener[]) Array.newInstance(MElementListener.class, n);
        int j = 0;
        // if the event name is not set we should return all listeners
        // interested
        if (key.getType().intValue() == 0) {
            Integer type = key.getType();
            for (int i = lList.length - 3; i >= 0; i -= 3) {
                if (type.equals(lList[i])) {
                    result[j++] = (MElementListener) lList[i + 2];
                }
            }
        } else if (key.getName() != null && key.getType() != null) {
            Integer type = key.getType();
            String name = key.getName();
            for (int i = lList.length - 3; i >= 0; i -= 3) {
                if (name.equals(lList[i + 1]) && type.equals(lList[i])) {
                    result[j++] = (MElementListener) lList[i + 2];
                }
            }
        } else {
            throw new IllegalArgumentException("Illegal eventkey!");
        }
        return result;
    }

    /**
     * Registers the given listeners for the event typed by
     * EventKey. If the name is null (of the EventKey), the listener
     * is registred for all events that have a type corresponding to
     * the type in the given key, no matter what the name of the event
     * is. Vice versa for the type.<p>
     *
     * <strong>A listener that has been added twice will get the
     * events for which it registred twice. Be careful with
     * registring listeners!</strong>
     *
     * @param key
     * @param listener
     */
    public synchronized void add(EventKey key, MElementListener listener) {
        if (listener == null || key == null) {
            throw new IllegalArgumentException("Null key or null listener");
        }
        // check if there allready is a listener
        // if (!Arrays.asList(getListeners(key)).contains(listener)) {

        if (listenerList == NULL_ARRAY) {
            // if this is the first listener added,
            // initialize the lists
            listenerList =
                new Object[] {
		    key.getType(), key.getName(), listener,
		};
        } else {
            if (!contains(key, listener)) {
                // Otherwise copy the array and add the new listener
                int i = listenerList.length;
                Object[] tmp = new Object[i + 3];
                System.arraycopy(listenerList, 0, tmp, 0, i);

                tmp[i] = key.getType();
                tmp[i + 1] = key.getName();
                tmp[i + 2] = listener;

                listenerList = tmp;
            }
        }
    }

    public boolean contains(EventKey key, MElementListener listener) {
        if (key == null) {
            LOG.info(" KEy null");
        }
        for (int i = listenerList.length - 1; i > 0; i -= 3) {
            if (listenerList[i] == listener
                && ((listenerList[i - 1] == null && key.getName() == null)
                    || (listenerList[i - 1] != null
                        && listenerList[i - 1].equals(key.getName()))
                    && ((listenerList[i - 2] == null && key.getType() == null)
                        || (listenerList[i - 2] != null
                            && listenerList[i - 2].equals(key.getType()))))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a listener from the list.
     *
     * @param key
     * @param listener
     */
    public void remove(EventKey key, MElementListener listener) {
        if (key.getName() != null && key.getType() != null) {
            for (int i = listenerList.length - 3; i >= 0; i -= 3) {
                if (listenerList[i + 2] == listener
                    && key.getName().equals(listenerList[i + 1])
		    && key.getType().equals(listenerList[i])) {
                    removeElement(i);
                    break;
                }
            }
        } else if (key.equals(EventKey.EMPTY_KEY)) {
            for (int i = listenerList.length - 1; i >= 0; i -= 3) {
                if (listenerList[i] == listener) {
                    removeElement(i - 2);
                }
            }
        } else if (key.getName() != null) {
            String name = key.getName();
            for (int i = listenerList.length - 1; i >= 0; i -= 3) {
                if (listenerList[i] == listener
                    && name.equals(listenerList[i - 1])) {
                    removeElement(i - 2);
                }
            }
        } else {
            Integer type = key.getType();
            for (int i = listenerList.length - 1; i >= 0; i -= 3) {
                if (listenerList[i] == listener
                    && type.equals(listenerList[i - 2])) {
                    removeElement(i - 2);
                }
            }
        }

    }

    /**
     * Does the actual removal of an element at the given index in the list.
     *
     * @param index
     */
    private synchronized void removeElement(int index) {
        Object[] tmp = new Object[listenerList.length - 3];
        // Copy the list up to index
        System.arraycopy(listenerList, 0, tmp, 0, index);
        // Copy from two past the index, up to
        // the end of tmp (which is three elements
        // shorter than the old list)
        if (index < tmp.length) {
            System.arraycopy(listenerList,
			     index + 3,
			     tmp,
			     index,
			     tmp.length - index);
        }
        // set the listener array to the new array or null
        if (tmp.length == 0) {
            listenerList = NULL_ARRAY;
        } else {
            listenerList = tmp;
        }
    }

    /**
     * Returns the number of listeners that are interested in the given key and
     * exist in the given object array.
     *
     * @param list
     * @param key
     * @return the count of listeners.
     */
    private int getListenerCount(Object[] list, EventKey key) {
        int count = 0;
        String name = key.getName();
        Integer type = key.getType();
        if (type.intValue() == 0) {
            for (int i = 0; i < list.length; i += 3) {
                if (type.equals(list[i])) {
                    count++;
                }
            }
        } else if (name != null && type != null) {
            for (int i = 0; i < list.length; i += 3) {
                if (type.equals(list[i]) && name.equals(list[i + 1])) {
                    count++;
                }
            }
        } else {
            throw new IllegalArgumentException("Illegal eventkey!");
        }
        return count;
    }

    /**
     * Returns the total number of listeners of the supplied type
     * for this listener list.
     *
     * @param key The type.
     * @return The number of listeners.
     */
    public synchronized int getListenerCount(EventKey key) {
        return getListenerCount(listenerList, key);
    }

    /**
     * Returns the total number of listeners for this listener list.
     *
     * @return The number of listeners.
     */
    public synchronized int getListenerCount() {
        return listenerList.length / 3;
    }

    /**
     * @param ll The listenerList to set.
     */
    void setListenerList(Object[] ll) {
        this.listenerList = ll;
    }

    /**
     * @return Returns the listenerList.
     */
    Object[] getListenerList() {
        return listenerList;
    }

}

/**
 * A map containing instances of meta-classes (modelelements) as keys and
 * EventListenerLists as values. The class is a wrapper around an underlying
 * java.util.HashMap and provides some custom methods for easy access to the
 * underlying data structure.
 *
 * @author jaap.branderhorst@xs4all.nl
 */
class EventListenerHashMap {

    /**
     * A null array to be shared by all empty listener lists.
     */
    private static final MElementListener[] NULL_ARRAY =
        new MElementListener[0];

    /**
     * The list of ListenerType - Listener pairs.
     */
    private transient Map listenerMap = new HashMap();

    /**
     * Puts the given listener as listener to the given modelelement
     * and given eventKey in the map.
     *
     * @param element
     * @param key
     * @param listener
     */
    public synchronized void put(
        MBase element,
        EventKey key,
        MElementListener listener) {
        EventListenerList list = (EventListenerList) listenerMap.get(element);
        if (list == null) {
            list = new EventListenerList();
            listenerMap.put(element, list);
        }
        list.add(key, listener);
    }

    /**
     * Removes a listener for a given eventkey and a given modelelement.
     * @param element
     * @param key
     * @param listener
     */
    public synchronized void remove(
        MBase element,
        EventKey key,
        MElementListener listener) {
        EventListenerList list = (EventListenerList) listenerMap.get(element);
        if (list != null) {
            list.remove(key, listener);
        }
    }

    /**
     * Removes the complete EventListenerList for the given element.
     *
     * @param element
     */
    public synchronized void remove(MBase element) {
        listenerMap.remove(element);
    }

    /**
     * Returns all listeners that are registered for the given modelElement and
     * the given EventKey.
     *
     * @param element
     * @param key
     * @return an array of listerns.
     */
    public MElementListener[] getListeners(MBase element, EventKey key) {
        EventListenerList list = (EventListenerList) listenerMap.get(element);
        if (list == null) {
            return NULL_ARRAY;
        }
        return list.getListeners(key);
    }

    /**
     * Tests wether there are any listeners registred for any
     * modelelements and eventkeys.
     *
     * @return true if empty.
     */
    public boolean isEmpty() {
        return listenerMap.isEmpty();
    }
}

/**
 * A map that holds ElementListenerLists with lists of listeners that
 * are interested in each and every event of a certain type of the
 * instances of a certain meta-class.
 *
 * @author jaap.branderhorst@xs4all.nl
 */
class ClassListenerHashMap {

    /**
     * A null array to be shared by all empty listener lists.
     */
    private static final MElementListener[] NULL_ARRAY =
        new MElementListener[0];

    /**
     * The list of ListenerType - Listener pairs.
     */
    private transient Map listenerMap = new HashMap();

    /**
     * Puts a listener that is interested in a certain event that will be send
     * by instances of the given meta-class.
     *
     * @param element The meta-class the listener is interested in
     * @param key The type/name pair designating the event type
     * @param listener The listener that's interested in the given event type
     */
    public synchronized void put(
        Class element,
        EventKey key,
        MElementListener listener) {
        if (element == null || listener == null) {
            throw new IllegalArgumentException("Modelelement or listener null");
        }
        EventListenerList list =
            (EventListenerList) listenerMap.get(element.getName());
        if (list == null) {
            list = new EventListenerList();
            listenerMap.put(element.getName(), list);
        }
        list.add(key, listener);
    }

    /**
     * Removes a listener as being interested in the given event.
     *
     * @param element
     * @param key
     * @param listener
     */
    public synchronized void remove(
        Class element,
        EventKey key,
        MElementListener listener) {
        if (element == null || listener == null) {
            throw new IllegalArgumentException("Modelelement or listener null");
        }
        EventListenerList list =
            (EventListenerList) listenerMap.get(element.getName());
        if (list != null) {
            list.remove(key, listener);
        }
    }

    /**
     * Removes the list of listeners for the given meta-class.
     * @param element
     */
    public synchronized void remove(Class element) {
        listenerMap.remove(element);
    }

    /**
     * Returns all listeners that are interested in the given event.
     * @param element
     * @param key
     * @return an array of listeners.
     */
    public MElementListener[] getListeners(Class element, EventKey key) {
        EventListenerList list = (EventListenerList) listenerMap.get(element);
        if (list == null) {
            return NULL_ARRAY;
        }
        return list.getListeners(key);
    }

    /**
     * Returns an EventListenerList with listeners that are interested
     * in certain events of the given meta-class.
     *
     * @param element
     * @return an array of EventListenerLists.
     */
    public EventListenerList[] getListenerList(Class element) {
        // element = formatClass(element);
        Class[] hierarchy = getHierarchy(element);
        EventListenerList[] lists = new EventListenerList[hierarchy.length];
        EventListenerList list = null;
        for (int i = 0; i < lists.length; i++) {
            list = (EventListenerList) listenerMap.get(hierarchy[i].getName());
            if (list == null) {
                lists[i] = new EventListenerList();
            } else {
                lists[i] = list;
            }
        }
        return lists;
    }

    private Class[] getHierarchy(Class clazz) {
        Class[] returnClass = null;
        if (clazz != Object.class) {
            Class[] tmp = getHierarchy(clazz.getSuperclass());
            returnClass =
                (Class[]) Array.newInstance(Class.class, tmp.length + 1);
            System.arraycopy(tmp, 0, returnClass, 0, tmp.length);
            returnClass[tmp.length] = clazz;
        } else {
            returnClass = new Class[] {
		clazz,
	    };
        }
        return returnClass;
    }

    /**
     * Tests if the hashmap is empty.
     *
     * @return true if empty.
     */
    public boolean isEmpty() {
        return listenerMap.isEmpty();
    }

}

/**
 * Class containing the definitions of all events in NSUML. The file
 * eventtree.xml is loaded by this class and converted to a hashmap
 * containing the classes firing events as keys and (hashmaps
 * containing eventnames as keys and eventtypes as values) as values.
 * @author jaap.branderhorst@xs4all.nl
 */
class EventTreeDefinition {
    private static final Logger LOG =
        Logger.getLogger(EventTreeDefinition.class);
    private static final String FILE_NAME = "org/argouml/model/uml/eventtree.xml";
    private Map definition = new HashMap();

    /**
     * Create an instance of EventTreeDefinition, reading
     * org/argouml/eventtree.xml to obtain the initial configuration.
     */
    public EventTreeDefinition() {
        Document doc = loadDocument();
        synchronized (doc) { // TODO: Why is this synchronized??
            addSourcesFromDocument (doc);
        }
    }

    /**
     * Add all event sources described by the given document.
     * The document must match  the pattern given by the following example:
     *
     * <pre>
     * &lt;eventtree&gt;
     *   &lt;source classname="classname of a model element"&gt;
     *     &lt;eventtype name="event fired by this model element"&gt;
     *       &lt;type&gt;1&lt;/type&gt;
     *     &lt;/eventtype&gt;
     *   &lt;/source&gt;
     * &lt;/eventtree&gt;
     * </pre>
     *
     * The classname is fully qualified.<p>
     *
     * The type is one of the numbers defined in {@link MElementEvent}. Each
     * of the elements can be used multiple times.
     *
     * @param doc the document, the contents of which should be added to this
     *            event tree definition. This must be preparsed.
     */
    synchronized void addSourcesFromDocument (final Document doc) {
        NodeList sources = doc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < sources.getLength(); i++) {
            Element source = (Element) sources.item(i);
            String className = source.getAttribute("classname");
            Class sourceClass = null;
            try {
                sourceClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOG.error(e);
            }
            Map nameMap = new HashMap();
            NodeList eventTypes = source.getChildNodes();
            for (int j = 0; j < eventTypes.getLength(); j++) {
                Element eventType = (Element) eventTypes.item(j);
                String name = eventType.getAttribute("name");
                NodeList typeNodes = eventType.getChildNodes();
                int typeLength = typeNodes.getLength();
                int[] types = new int[typeLength];
                for (int k = 0; k < typeLength; k++) {
                    Element typeNode = (Element) typeNodes.item(k);
                    types[k] =
                        Integer.parseInt(
                            typeNode.getFirstChild().getNodeValue());
                }
                nameMap.put(name, types);
            }
            // remove case
            nameMap.put(UmlModelEventPump.REMOVE, new int[] {
                0,
            });
            addSource (sourceClass, nameMap);
        }
    }

    /**
     * Add event types for a single source to this event tree.
     *
     * @param cSource the source class for which to add event types.
     *
     * @param mpNameMap a map of the event types to add for the source class.
     *                  The keys are Strings indicating the names of events,
     *                  while the values are int[] which contain all the event
     *                  types the indicated event represents. The specific int
     *                  values used can be found in {@link MElementEvent}.
     */
    synchronized void addSource (Class cSource, Map mpNameMap) {
        definition.put (cSource, mpNameMap);
    }

    /**
     * Returns all eventkeys that an instance of the given modelClass
     * could possibly fire.
     * @param modelClass
     * @return The event keys.
     */
    public synchronized EventKey[] getEventTypes(Class modelClass) {
        modelClass = formatClass(modelClass);
        Map nameMap = (Map) definition.get(modelClass);
        Iterator it = nameMap.keySet().iterator();
        int size = 0;
        while (it.hasNext()) {
            size += ((int[]) nameMap.get(it.next())).length;
        }
        EventKey[] result = new EventKey[size + 1];
        int counter = 0;
        it = nameMap.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            int[] types = (int[]) nameMap.get(name);
            EventKey[] keys = new EventKey[types.length];
            for (int i = 0; i < types.length; i++) {
                keys[i] = new EventKey(types[i], name);
            }
            System.arraycopy(keys, 0, result, counter, keys.length);
            counter += keys.length;
        }
        // remove event
        System.arraycopy(new EventKey[] {
	    new EventKey(0, null),
	},
			 0,
			 result,
			 counter,
			 1);
        return result;
    }

    /**
     * Returns all EventKeys (eventdefinitions) with the given eventName
     * that the given modelClass can fire.
     *
     * @param modelClass
     * @param name
     * @return All event keys.
     */
    public synchronized EventKey[] getEventTypes(Class modelClass,
                                                 String name) {
        modelClass = formatClass(modelClass);
        Map nameMap = (Map) definition.get(modelClass);
        if (nameMap != null) {
            int[] types = (int[]) nameMap.get(name);
            if (types != null) {

                EventKey[] keys = new EventKey[types.length];
                for (int i = 0; i < types.length; i++) {
                    keys[i] = new EventKey(types[i], name);
                }
                return keys;
            }
        }
        return new EventKey[0];
    }

    private synchronized Document loadDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document eventNamesDoc =
                builder.parse(
                    getClass().getClassLoader().getResourceAsStream(FILE_NAME));
            return eventNamesDoc;
        } catch (ParserConfigurationException e) {
            LOG.fatal(e);
            System.exit(-1);
        } catch (SAXException e) {
            LOG.fatal(e);
            System.exit(-1);
        } catch (IOException e) {
            LOG.fatal(e);
            System.exit(-1);
        }
        return null;
    }

    private Class formatClass(Class inputClass) {
        String name = inputClass.getName();
        if (name.endsWith("Impl")) {
            return inputClass;
        }
        try {
            Class returnClass = Class.forName(name + "Impl");
            return returnClass;
        } catch (ClassNotFoundException ignorable) {
            // cannot happen
        }
        return null;
    }
}
