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

package org.argouml.uml.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.AbstractArgoJPanel;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.ui.targetmanager.TargettableModelView;
import org.argouml.uml.Profile;
import org.tigris.gef.presentation.Fig;
import org.tigris.swidgets.LabelledLayout;
import org.tigris.swidgets.Orientation;
import org.tigris.swidgets.Vertical;
import org.tigris.toolbar.ToolBar;

/**
 * This abstract class provides the basic layout and event dispatching support
 * for all Property Panels.
 * <p>
 * The property panel is {@link org.tigris.swidgets.LabelledLayout} layed out as
 * a number (specified in the constructor) of equally sized panels that split
 * the available space. Each panel has a column of "captions" and matching
 * column of "fields" which are laid out indepently from the other panels.
 * <p>
 * The Properties panels for UML Model Elements are structured in an inheritance
 * hierarchy that matches the UML 1.3 metamodel.
 */
public abstract class PropPanel extends AbstractArgoJPanel implements
        TabModelTarget, UMLUserInterfaceContainer {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(PropPanel.class);

    private Object target;

    private Object modelElement;

    private EventListenerList listenerList;

    private JToolBar buttonPanel;

    private JLabel titleLabel;

    private Font smallFont = LookAndFeelMgr.getInstance().getSmallFont();

    /**
     * Construct new PropPanel using LabelledLayout.
     * <p>
     *
     * @param icon
     *            The icon to display for the panel
     * @param title
     *            The title of the panel
     * @param orientation
     *            the orientation
     */
    public PropPanel(String title, ImageIcon icon, Orientation orientation) {
        super(title);
        setOrientation(orientation);
        buttonPanel = new ToolBar();
        buttonPanel.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        buttonPanel.setFloatable(false);

        setLayout(new LabelledLayout(orientation == Vertical.getInstance()));

        if (icon != null) {
            titleLabel = new JLabel(title, icon, SwingConstants.LEFT);
        } else {
            titleLabel = new JLabel(title);
        }
        titleLabel.setLabelFor(buttonPanel);
        add(titleLabel);
        add(buttonPanel);
    }

    /**
     * Constructs a new Proppanel without an icon. If there is an icon it's
     * updated at runtime via settarget.
     * <p>
     *
     * @param title
     *            the title
     * @param orientation
     *            the orientation
     */
    public PropPanel(String title, Orientation orientation) {
        this(title, null, orientation);
    }

    /**
     * Set the orientation of the panel.
     *
     * @see org.tigris.swidgets.Orientable#setOrientation(org.tigris.swidgets.Orientation)
     */
    public void setOrientation(Orientation orientation) {
        super.setOrientation(orientation);
    }

    /**
     * @param button
     *            the button to be added to the button panel
     * @deprecated use addAction instead. Remove 0.20.0
     */
    protected void addButton(Component button) {
        button.setFocusable(false); // Buttons shall not go in the tabbing loop
        buttonPanel.add(button);
    }

    /**
     * Add a button to the toolbar of a property panel using the action to
     * control the behavior of the action.
     *
     * @param action
     *            the action which will be used in the toolbar button.
     */
    protected void addAction(Action action) {
        addAction(action, null);
    }

    /**
     * Add a button to the toolbar of a property panel using the action to
     * control the behavior of the action.
     *
     * @param action
     *            the action which will be used in the toolbar button.
     * @param tooltip
     *            the tooltip to set, or null to skip setting of a new tooltip.
     */
    protected void addAction(Action action, String tooltip) {
        JButton button = new JButton(action);
        if (tooltip != null) button.setToolTipText(tooltip);
        button.setText("");
        button.setFocusable(false);
        buttonPanel.add(button);
    }

    /**
     * Add a component with the specified label.
     * <p>
     *
     * @param label
     *            the label for the component
     * @param component
     *            the component
     * @return the label added
     */
    public JLabel addField(String label, Component component) {
        // This test will prevent us placing the stereotype
        // combo still left over from NSUML
        if (component != null) {
            JLabel jlabel = new JLabel(label);
            jlabel.setFont(smallFont);
            component.setFont(smallFont);
            jlabel.setLabelFor(component);
            add(jlabel);
            add(component);
            return jlabel;
        } else {
            return null;
        }
    }

    /**
     * Add a component with the specified label positioned after another
     * component.
     *
     * @param label
     *            the label for the component
     * @param component
     *            the component
     * @param afterComponent
     *            the component before
     * @return the newly added label
     */
    public JLabel addFieldAfter(String label, Component component,
            Component afterComponent) {
        int nComponent = this.getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == afterComponent) {
                JLabel jlabel = new JLabel(label);
                jlabel.setFont(smallFont);
                component.setFont(smallFont);
                jlabel.setLabelFor(component);
                add(jlabel, ++i);
                add(component, ++i);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     * Add a component with the specified label positioned before another
     * component.
     * <p>
     *
     * @param label
     *            the label for the component
     * @param component
     *            the component
     * @param beforeComponent
     *            the component
     * @return the newly added component
     */
    public JLabel addFieldBefore(String label, Component component,
            Component beforeComponent) {
        int nComponent = this.getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == beforeComponent) {
                JLabel jlabel = new JLabel(label);
                jlabel.setFont(smallFont);
                component.setFont(smallFont);
                jlabel.setLabelFor(component);
                add(jlabel, i);
                add(component, ++i);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     * Add a seperator.
     */
    protected final void addSeperator() {
        add(LabelledLayout.getSeperator());
    }

    /**
     * Set the target to be associated with a particular property panel.
     * <p>
     * This involves resetting the third party listeners.
     * <p>
     *
     * @deprecated As Of Argouml version 0.13.5, This will change visibility
     *             from release 0.16
     * @param t
     *            The object to be set as a target.
     */
    public void setTarget(Object t) {
        LOG.debug("setTarget called with " + t + " as parameter (not target!)");
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;

        // If the target has changed notify the third party listener if it
        // exists and dispatch a new element event listener to
        // ourself. Otherwise dispatch a target reasserted to ourself.
        Runnable dispatch = null;
        if (t != target) {

            // Set up the target and its model element variant.

            target = t;
            modelElement = null;
            if (listenerList == null) {
                listenerList = registrateTargetListeners(this);
            }

            if (Model.getFacade().isAModelElement(target)) {
                modelElement = target;
            }

            // This will add a new ModelElement event listener
            // after update is complete

            dispatch = new UMLChangeDispatch(this,
                    UMLChangeDispatch.TARGET_CHANGED_ADD);

        } else {
            dispatch = new UMLChangeDispatch(this,
                    UMLChangeDispatch.TARGET_REASSERTED);

        }
        SwingUtilities.invokeLater(dispatch);

        // update the titleLabel
        // MVW: This overrules the icon set initiallly... Why do we need this?
        if (titleLabel != null) {
            Icon icon = ResourceLoaderWrapper.getInstance().lookupIcon(t);
            if (icon != null) {
                titleLabel.setIcon(icon);
            }
        }
    }

    /**
     * Builds a eventlistenerlist of all targetlisteners that are part of this
     * container and its children.
     *
     * @param container
     *            the container to search for targetlisteners
     * @return an EventListenerList with all TargetListeners on this container
     *         and its children.
     */
    private EventListenerList registrateTargetListeners(Container container) {
        Component[] components = container.getComponents();
        EventListenerList list = new EventListenerList();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof TargetListener) {
                list.add(TargetListener.class, (TargetListener) components[i]);
            }
            if (components[i] instanceof TargettableModelView) {
                list.add(TargetListener.class,
                        ((TargettableModelView) components[i])
                                .getTargettableModel());
            }
            if (components[i] instanceof Container) {
                EventListenerList list2 = registrateTargetListeners(
                                                (Container) components[i]);
                Object[] objects = list2.getListenerList();
                for (int j = 1; j < objects.length; j += 2) {
                    list.add(TargetListener.class, (TargetListener) objects[j]);
                }
            }
        }
        return list;
    }

    /**
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public final Object getTarget() {
        return target;
    }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        SwingUtilities.invokeLater(new UMLChangeDispatch(this, 0));
    }

    /**
     * @see org.argouml.ui.TabTarget#shouldBeEnabled(java.lang.Object)
     */
    public boolean shouldBeEnabled(Object t) {
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;
        return Model.getFacade().isAModelElement(t);
    }

    /**
     * This method can be overriden in derived Panels where the appropriate
     * namespace for display may not be the same as the namespace of the target.
     *
     * @return the namespace
     */
    protected Object getDisplayNamespace() {
        Object ns = null;
        Object theTarget = getTarget();
        if (Model.getFacade().isAModelElement(theTarget)) {
            ns = Model.getFacade().getNamespace(theTarget);
        }
        return ns;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getProfile()
     */
    public Profile getProfile() {
        return ProjectManager.getManager().getCurrentProject().getProfile();
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getModelElement()
     */
    public final Object getModelElement() {
        return modelElement;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatElement(java.lang.Object)
     */
    public String formatElement(/* MModelElement */Object element) {
        return getProfile().formatElement(element, getDisplayNamespace());
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatNamespace(java.lang.Object)
     */
    public String formatNamespace(/* MNamespace */Object namespace) {
        return getProfile().formatElement(namespace, null);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatCollection(java.util.Iterator)
     */
    public String formatCollection(Iterator iter) {
        Object namespace = getDisplayNamespace();
        return getProfile().formatCollection(iter, namespace);
    }

    /**
     * Remove this element.
     */
    public void removeElement() {
        Object theTarget = getTarget();
        if (Model.getFacade().isABase(theTarget)) {
            Object newTarget = Model.getFacade().getModelElementContainer(
                    theTarget);
            Object base = theTarget;
            TargetManager.getInstance().setTarget(base);
            ActionEvent event = new ActionEvent(this, 1, "delete");
            new ActionDeleteSingleModelElement().actionPerformed(event);
            if (newTarget != null) {
                TargetManager.getInstance().setTarget(newTarget);
            }
        }
    }

    /**
     * Check whether this element can be deleted. Currently it only checks
     * whether we delete the main model. ArgoUML does not like that.
     *
     * @since 0.13.2
     * @return whether this element can be deleted
     */
    public boolean isRemovableElement() {
        return ((getTarget() != null) && (getTarget() != (ProjectManager
                .getManager().getCurrentProject().getModel())));
    }

    /**
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        // we can neglect this, the TabProps allways selects the first target
        // in a set of targets. The first target can only be
        // changed in a targetRemoved or a TargetSet event
        if (listenerList == null) {
            listenerList = registrateTargetListeners(this);
        }
        fireTargetAdded(e);
    }

    /**
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // how to handle empty target lists?
        // probably the TabProps should only show an empty pane in that case
        fireTargetRemoved(e);
        // setTarget(e.getNewTarget());

    }

    /**
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
        fireTargetSet(e);

    }

    private void fireTargetSet(TargetEvent targetEvent) {
        if (listenerList == null)
            listenerList = registrateTargetListeners(this);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                // Lazily create the event:
                ((TargetListener) listeners[i + 1]).targetSet(targetEvent);
            }
        }
    }

    private void fireTargetAdded(TargetEvent targetEvent) {
        if (listenerList == null)
            listenerList = registrateTargetListeners(this);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                // Lazily create the event:
                ((TargetListener) listeners[i + 1]).targetAdded(targetEvent);
            }
        }
    }

    private void fireTargetRemoved(TargetEvent targetEvent) {
        if (listenerList == null)
            listenerList = registrateTargetListeners(this);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                // Lazily create the event:
                ((TargetListener) listeners[i + 1]).targetRemoved(targetEvent);
            }
        }
    }

    /**
     * @param theTitleLabel
     *            the title of the panel shown at the top
     */
    protected void setTitleLabel(JLabel theTitleLabel) {
        this.titleLabel = theTitleLabel;
    }

    /**
     * @return the title of the panel shown at the top
     */
    protected JLabel getTitleLabel() {
        return titleLabel;
    }

    /**
     * Look up an icon.
     *
     * @param name
     *            the resource name.
     * @return an ImageIcon corresponding to the given resource name
     */
    protected static ImageIcon lookupIcon(String name) {
        return ResourceLoaderWrapper.lookupIconResource(name);
    }

} /* end class PropPanel */
