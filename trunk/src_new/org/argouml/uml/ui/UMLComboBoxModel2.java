// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.apache.log4j.Logger;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelEventPump;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.tigris.gef.presentation.Fig;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;

/**
 * ComboBoxmodel for UML modelelements. This implementation does not use 
 * reflection and seperates Model, View and Controller better then does
 * UMLComboBoxModel. In the future UMLComboBoxModel and UMLComboBox will be
 * replaced with this implementation to improve performance.
 */
public abstract class UMLComboBoxModel2
    extends AbstractListModel
    implements MElementListener, ComboBoxModel, TargetListener {

    private static Logger log =
        Logger.getLogger("org.argouml.uml.ui.UMLComboBoxModel2");

    /**
     * The taget of the comboboxmodel. This is some UML modelelement
     */
    protected Object _target = null;

    /**
     * The list with objects that should be shown in the combobox
     */
    private List _objects = new ArrayList();

    /**
     * The selected object
     */
    private Object _selectedObject = null;

    /**
     * Flag to indicate if the user may select "" as value in the combobox. If
     * true the attribute that is shown by this combobox may be set to null.
     * Makes sure that there is allways a "" in the list with objects so the
     * user has the oportunity to select this to clear the attribute.
     */
    private boolean _clearable = false;

    /**
     * The name of the event with which NSUML sets the attribute that is shown
     * in this comboboxmodel.
     */
    protected String _propertySetName;

    /**
     * Flag to indicate wether list events should be fired
     */
    protected boolean _fireListEvents = true;
    
    /**
     * Flag to indicate wether the model is being build
     */
    private boolean _buildingModel = false;

    /**
     * Constructs a model for a combobox. The container given is used
     * to retreive the target that is manipulated through this
     * combobox. If clearable is true, the user can select null in the
     * combobox and thereby clear the attribute in the model.
     *
     * @param propertySetName The name of the NSUML event that must be
     * fired to set the selected item programmatically (via setting
     * the NSUML model)
     * @throws IllegalArgumentException if one of the arguments is null
     */
    public UMLComboBoxModel2(String propertySetName, boolean clearable) {
        super();
        if (propertySetName == null || propertySetName.equals(""))
            throw new IllegalArgumentException("one of the arguments is null");
        // it would be better that we don't need the container to get
        // the target this constructor can be without parameters as
        // soon as we improve targetChanged
        _clearable = clearable;
        _propertySetName = propertySetName;
    }

    /**
     * @see ru.novosoft.uml.MElementListener#listRoleItemSet(MElementEvent)
     */
    public void listRoleItemSet(MElementEvent e) { }

    /**
     * If the property that this comboboxmodel depicts is changed by the NSUML
     * model, this method will make sure that it is changed in the comboboxmodel
     * too.
     * @see ru.novosoft.uml.MElementListener#propertySet(MElementEvent)
     */
    public void propertySet(MElementEvent e) {
        if (e.getName().equals(_propertySetName)
            && e.getSource() == getTarget()
	    && (_clearable || getChangedElement(e) != null))
	{
	    Object elem = getChangedElement(e);
	    if (!contains(elem))
		addElement(elem);
            setSelectedItem(elem);
        }
    }

    /**
     * @see ru.novosoft.uml.MElementListener#recovered(MElementEvent)
     */
    public void recovered(MElementEvent e) { }

    /**
     * @see ru.novosoft.uml.MElementListener#removed(MElementEvent)
     */
    public void removed(MElementEvent e) {
        if (contains(getChangedElement(e))) {
            Object o = getChangedElement(e);
            if (o instanceof Collection) {
                removeAll((Collection) o);
            } else {
                removeElement(o);
            }
        }
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleAdded(MElementEvent)
     */
    public void roleAdded(MElementEvent e) {
        if (getTarget() != null && isValidEvent(e)) {
            Object o = getChangedElement(e);
            if (o instanceof Collection) { // this should not happen but
                // you never know with NSUML
                log.warn(
			 "Collection added via roleAdded! The correct element"
			 + "is probably not selected...");
                Iterator it = ((Collection) o).iterator();
                while (it.hasNext()) {
                    Object o2 = it.next();
                    addElement(it.next());
                }
            } else {
                addElement(o);
            }
        }
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleRemoved(MElementEvent)
     */
    public void roleRemoved(MElementEvent e) {
        if (contains(getChangedElement(e))) {
            Object o = getChangedElement(e);
            if (o instanceof Collection) {
                removeAll((Collection) o);
            } else {
                removeElement(o);
            }
        }
    }

    /**
     * Returns true if the given element is valid, i.e. it may be added to the 
     * list of elements.
     * @param element
     */
    protected abstract boolean isValidElement(Object element);

    /**
     * Builds the list of elements and sets the selectedIndex to the currently 
     * selected item if there is one. Called from targetChanged every time the 
     * target of the proppanel is changed.
     */
    protected abstract void buildModelList();

    /**
     * Utility method to change all elements in the list with modelelements
     * at once.
     * @param elements
     */
    protected void setElements(Collection elements) {
        if (elements != null) {
            //removeAllElements();
            if (!_objects.isEmpty()) {
                fireIntervalRemoved(this, 0, _objects.size() - 1);
            }
            //addAll(elements);
            _objects = Collections.synchronizedList(new ArrayList(elements));
            if (!_objects.isEmpty()) {
                fireIntervalAdded(this, 0, _objects.size() - 1);
            }
            _selectedObject = null;
            if (_clearable && !elements.contains("")) {
                addElement("");
            }
        } else
            throw new IllegalArgumentException("In setElements: may not set "
					       + "elements to null collection");
    }

    /**
     * Utility method to get the target. Sets the _target if the
     * _target is null via the method setTarget().
     *
     * @return MModelElement
     */
    protected Object getTarget() {
        return _target;
    }

    /**
     * Utility method to remove a collection of elements from the model.
     *
     * @param col
     */
    protected void removeAll(Collection col) {
        Iterator it = col.iterator();
        int lower = -1;
        int index = -1;
        int lastindex = -1;
        _fireListEvents = false;
        while (it.hasNext()) {
            Object o = it.next();
            removeElement(o);
            if (lower == -1) { // start of interval
                lower = getIndexOf(o);
                index = lower;
            } else {
                if (getIndexOf(o) != index + 1) { // end of interval
                    _fireListEvents = true;
                    fireIntervalRemoved(this, lower, index);
                    _fireListEvents = false;
                    lower = -1;
                } else { // in middle of interval
                    index++;
                }
            }
        }
        _fireListEvents = true;
    }

    /**
     * Utility method to add a collection of elements to the model
     * @param col
     */
    protected void addAll(Collection col) {
        Iterator it = col.iterator();
        Object o2 = getSelectedItem();
        _fireListEvents = false;
        int oldSize = _objects.size();
        while (it.hasNext()) {
            Object o = it.next();
            addElement(o);
        }
        setSelectedItem(o2);
        _fireListEvents = true;
        fireIntervalAdded(this, oldSize - 1, _objects.size() - 1);
    }

    /**
     * Utility method to get the changed element from some event e
     * @param e
     * @return Object
     */
    protected Object getChangedElement(MElementEvent e) {
        if (e.getAddedValue() != null)
            return e.getAddedValue();
        if (e.getRemovedValue() != null)
            return e.getRemovedValue();
        if (e.getNewValue() != null)
            return e.getNewValue();
        return null;
    }

    /**
     * Sets the target. If the old target is instanceof MBase, it also removes
     * the model from the element listener list of the target. If the new target
     * is instanceof MBase, the model is added as element listener to the new 
     * target.
     * @param target
     */
    protected void setTarget(Object target) {
        target = target instanceof Fig ? ((Fig) target).getOwner() : target;
        if (ModelFacade.isABase(target) || ModelFacade.isADiagram(target)) {
            UmlModelEventPump eventPump = UmlModelEventPump.getPump();
            if (ModelFacade.isABase(_target)) {
                eventPump.removeModelEventListener(this, _target,
						   _propertySetName);
            }

            if (ModelFacade.isABase(target)) {
                _target = target;
                // UmlModelEventPump.getPump()
                // .removeModelEventListener(this, (MBase)_target,
                // _propertySetName);
                eventPump.addModelEventListener(this, _target,
						_propertySetName);
            } else {
                _target = null;
            }
            _fireListEvents = false;
            removeAllElements();
            _fireListEvents = true;
            if (_target != null) {
                _buildingModel = true;
                buildModelList();
                _buildingModel = false;
                setSelectedItem(getSelectedModelElement());
                if (getSize() > 0) {
                    fireIntervalAdded(this, 0, getSize() - 1);
                }
               
            }
            if (getSelectedItem() != null && _clearable) {
                addElement(""); // makes sure we can select 'none'
            }
        }
    }

    /**
     * Gets the modelelement that is selected in the NSUML model. For
     * example, say that this ComboBoxmodel contains all namespaces
     * (as in UMLNamespaceComboBoxmodel) , this method should return
     * the namespace that owns the target then.
     *
     * @return Object
     */
    protected abstract Object getSelectedModelElement();

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        if (index >= 0 && index < _objects.size())
            return _objects.get(index);
        return null;
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return _objects.size();
    }

    public int getIndexOf(Object o) {
        return _objects.indexOf(o);
    }

    public void addElement(Object o) {
        if (!_objects.contains(o)) {
            _objects.add(o);
            fireIntervalAdded(this, _objects.size() - 1, _objects.size() - 1);
        }
    }

    public void setSelectedItem(Object o) {
        if ((_selectedObject != null && !_selectedObject.equals(o))
            || (_selectedObject == null && o != null)) {
            _selectedObject = o;
            fireContentsChanged(this, -1, -1);
        }
    }

    public void removeElement(Object o) {
        int index = _objects.indexOf(o);
        if (getElementAt(index) == _selectedObject) {
            if (index == 0) {
                setSelectedItem(getSize() == 1
				? null
				: getElementAt(index + 1));
            } else {
                setSelectedItem(getElementAt(index - 1));
            }
        }
        if (index >= 0) {
            _objects.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    public void removeAllElements() {
        int startIndex = 0;
        int endIndex = _objects.size() - 1;
        // if (!_objects.isEmpty()) {
        _objects.clear();
        _selectedObject = null;
        fireIntervalRemoved(this, startIndex, endIndex);
        // }
    }

    public Object getSelectedItem() {
        return _selectedObject;
    }

    /**
     * Returns true if some object elem is contained by the list of choices
     * @param elem
     * @return boolean
     */
    public boolean contains(Object elem) {
        if (_objects.contains(elem))
            return true;
        if (elem instanceof Collection) {
            Iterator it = ((Collection) elem).iterator();
            while (it.hasNext()) {
                if (!_objects.contains(it.next()))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if some event is valid. An event is valid if the
     * element changed in the event is valid. This is determined via a
     * call to isValidElement.  This method can be overriden by
     * subclasses if they cannot determine if it is a valid event just
     * by checking the changed element.
     *
     * @param e
     * @return boolean
     */
    protected boolean isValidEvent(MElementEvent e) {
        boolean valid = false;
        if (!(getChangedElement(e) instanceof Collection)) {
            valid = isValidElement(getChangedElement(e));
            if (!valid && e.getNewValue() == null && e.getOldValue() != null) {
                valid = true; // we tried to remove a value
            }
        } else {
            Collection col = (Collection) getChangedElement(e);
            Iterator it = col.iterator();
            if (!col.isEmpty()) {
                valid = true;
                while (it.hasNext()) {
                    Object o = it.next();
                    if (!isValidElement(o)) {
                        valid = false;
                        break;
                    }
                }
            } else {
                if (e.getOldValue() instanceof Collection
                    && !((Collection) e.getOldValue()).isEmpty()) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    /**
     * @see javax.swing.AbstractListModel#fireContentsChanged(
     *          Object, int, int)
     */
    protected void fireContentsChanged(Object source, int index0, int index1) {
        if (_fireListEvents && !_buildingModel)
            super.fireContentsChanged(source, index0, index1);
    }

    /**
     * @see javax.swing.AbstractListModel#fireIntervalAdded(
     *          Object, int, int)
     */
    protected void fireIntervalAdded(Object source, int index0, int index1) {
        if (_fireListEvents && !_buildingModel)
            super.fireIntervalAdded(source, index0, index1);
    }

    /**
     * @see javax.swing.AbstractListModel#fireIntervalRemoved(
     *          Object, int, int)
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        if (_fireListEvents && !_buildingModel)
            super.fireIntervalRemoved(source, index0, index1);
    }

    /**
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) { }

    /**
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        Object currentTarget = _target;
        Object oldTarget = e.getOldTargets().length > 0 ? e.getOldTargets()[0] : null;
        if (oldTarget instanceof Fig) {
            oldTarget = ((Fig)oldTarget).getOwner();
        }
        if (oldTarget == currentTarget) {
            if (ModelFacade.isABase(currentTarget)) {
                UmlModelEventPump.getPump().removeModelEventListener(this,
                        currentTarget, _propertySetName);
            }
            _target = e.getNewTarget();
        }                
        // setTarget(e.getNewTarget());
    }

    /**
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());

    }

}
