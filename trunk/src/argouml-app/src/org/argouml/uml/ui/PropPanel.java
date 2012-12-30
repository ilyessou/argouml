/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
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

package org.argouml.uml.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;

import org.argouml.application.api.AbstractArgoJPanel;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProfileConfiguration;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.ActionCreateContainedModelElement;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargettableModelView;
import org.tigris.gef.presentation.Fig;
import org.tigris.swidgets.GridLayout2;
import org.tigris.swidgets.Orientation;
import org.tigris.toolbar.ToolBarFactory;

/**
 * This abstract class provides the basic layout and event dispatching support
 * for all Property Panels.<p>
 *
 * The property panel is {@link org.argouml.uml.ui.LabelledLayout} layed out as
 * a number (specified in the constructor) of equally sized panels that split
 * the available space. Each panel has a column of "captions" and matching
 * column of "fields" which are laid out independently from the other
 * panels.<p>
 *
 * The Properties panels for UML Model Elements are structured in an inheritance
 * hierarchy that matches the UML metamodel.
 */
public abstract class PropPanel extends AbstractArgoJPanel implements
        UMLUserInterfaceContainer, ComponentListener {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(PropPanel.class.getName());

    private Object target;

    private Object modelElement;

    /**
     * List of event listeners to notify. This is computed one time and frozen
     * the first time any target change method (e.g. setTarget, targetAdded) is
     * called.
     */
    private EventListenerList listenerList;

    private JPanel buttonPanel = new JPanel(new GridLayout());

    private JLabel titleLabel;

    /**
     * A list with "actions".<p>
     *
     * Action in this respect are one of:<ul>
     * <li> {@link Action}
     * <li> {@link JButton}
     * <li> {@link Object}[]
     * </ul>
     */
    private List actions = new ArrayList();

    private static Font stdFont =
        LookAndFeelMgr.getInstance().getStandardFont();

    /**
     * Construct new PropPanel using LabelledLayout.
     * <p>
     * @param icon The icon to display for the panel
     * @param label The label for the title of the panel (to be localized).
     */
    public PropPanel(String label, ImageIcon icon) {
        super(Translator.localize(label));

        LabelledLayout layout = new LabelledLayout();
        layout.setHgap(5);
        setLayout(layout);

        if (icon != null) {
            setTitleLabel(new JLabel(Translator.localize(label), icon,
                    SwingConstants.LEFT));
        } else {
            setTitleLabel(new JLabel(Translator.localize(label)));
        }
        titleLabel.setLabelFor(buttonPanel);
        add(titleLabel);
        add(buttonPanel);

        addComponentListener(this);
    }


    /*
     * @see org.tigris.swidgets.Orientable#setOrientation(org.tigris.swidgets.Orientation)
     */
    @Override
    public void setOrientation(Orientation orientation) {
        // TODO: Do we need to change the layout manager when
        // changing orientation to match the behavior of the constructor?
//        if (getOrientation() != orientation) {
//            LabelledLayout layout = new LabelledLayout(orientation == Vertical
//                    .getInstance());
//            setLayout(layout);
//        }
        super.setOrientation(orientation);
    }

    /**
     * Add a button to the toolbar of a property panel using the action to
     * control the behavior of the action.
     *
     * @param action
     *            the action which will be used in the toolbar button.
     */
    protected void addAction(Action action) {
        actions.add(action);
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
        JButton button = new TargettableButton(action);
        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }
        button.setText("");
        button.setFocusable(false);
        actions.add(button);
    }

    /**
     * Add multiple buttons at once.
     *
     * @param actionArray the Actions.
     */
    protected void addAction(Object[] actionArray) {
        actions.add(actionArray);
    }

    public void buildToolbar() {
        LOG.log(Level.FINE, "Building toolbar");

        ToolBarFactory factory = new ToolBarFactory(getActions());
        factory.setRollover(true);
        factory.setFloatable(false);
        JToolBar toolBar = factory.createToolBar();
        toolBar.setName("misc.toolbar.properties");

	buttonPanel.removeAll();
        buttonPanel.add(BorderLayout.WEST, toolBar);
        // Set the tooltip of the arrow to open combined tools:
        buttonPanel.putClientProperty("ToolBar.toolTipSelectTool",
                Translator.localize("action.select"));
    }

    /**
     * Get the actions that will make up the toolbar on this panel.
     * @return The list of actions to show for this panel.
     */
    protected List getActions() {
        return actions;
    }

    private static class TargettableButton extends JButton
        implements TargettableModelView {

        public TargettableButton(Action action) {
            super(action);
        }

        public TargetListener getTargettableModel() {
            if (getAction() instanceof TargetListener) {
                return (TargetListener) getAction();
            }
            return null;
        }

    }

    /**
     * Add a component with the specified label.<p>
     *
     * @param label
     *            the label for the component
     * @param component
     *            the component
     * @return the label added
     */
    public JLabel addField(String label, Component component) {
        JLabel jlabel = createLabelFor(label, component);
        component.setFont(stdFont);
        add(jlabel);
        add(component);
        if (component instanceof UMLStereotypeList) {
            UMLModelElementListModel2 list =
                (UMLModelElementListModel2) ((UMLStereotypeList) component).getModel();
            ActionCreateContainedModelElement newAction =
                new ActionCreateContainedModelElement(
                        list.getMetaType(),
                        list.getTarget(),
                        "New..."); // TODO: i18n
        }
        return jlabel;
    }

    /**
     * @param label The text of the label (the method cares about i18n)
     * @param comp The component that this label is for
     * @return a new JLabel
     */
    private JLabel createLabelFor(String label, Component comp) {
        JLabel jlabel = new JLabel(Translator.localize(label));
        jlabel.setToolTipText(Translator.localize(label));
        jlabel.setFont(stdFont);
        jlabel.setLabelFor(comp);
        return jlabel;
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
        int nComponent = getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == afterComponent) {
                JLabel jlabel = createLabelFor(label, component);
                component.setFont(stdFont);
                add(jlabel, ++i);
                add(component, ++i);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     * Add a component with the specified label positioned before another
     * component.<p>
     *
     * @param label
     *            the label for the component
     * @param component
     *            the to be added component
     * @param beforeComponent
     *            the component before its label we add
     * @return the newly added component
     */
    public JLabel addFieldBefore(String label, Component component,
            Component beforeComponent) {
        int nComponent = getComponentCount();
        for (int i = 0; i < nComponent; ++i) {
            if (getComponent(i) == beforeComponent) {
                JLabel jlabel = createLabelFor(label, component);
                component.setFont(stdFont);
                add(jlabel, i - 1);
                add(component, i++);
                return jlabel;
            }
        }
        throw new IllegalArgumentException("Component not found");
    }

    /**
     * Add a separator.
     */
    protected final void addSeparator() {
        add(LabelledLayout.getSeparator());
    }

    /**
     * Set the target to be associated with a particular property panel.<p>
     *
     * This involves resetting the third party listeners.
     *
     * @param t
     *            The object to be set as a target.
     */
    public void setTarget(Object t) {

        LOG.log(Level.FINE, "setTarget called with {0} as parameter (not target!)", t);

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
                listenerList = collectTargetListeners(this);
            }

            if (Model.getFacade().isAUMLElement(target)) {
                modelElement = target;
            }

            // This will add a new ModelElement event listener
            // after update is complete

            dispatch = new UMLChangeDispatch(this,
                    UMLChangeDispatch.TARGET_CHANGED_ADD);

            buildToolbar();
        } else {
            dispatch = new UMLChangeDispatch(this,
                    UMLChangeDispatch.TARGET_REASSERTED);

        }
        SwingUtilities.invokeLater(dispatch);

        // update the titleLabel
        // MVW: This overrules the icon set initiallly... Why do we need this?
        if (titleLabel != null) {
            Icon icon = null;
            if (t != null) {
                icon = ResourceLoaderWrapper.getInstance().lookupIcon(t);
            }
            if (icon != null) {
                titleLabel.setIcon(icon);
            }
        }
    }

    /**
     * Builds a eventlistenerlist of all targetlisteners that are part of this
     * container and its children. Components do not need to register
     * themselves. They are registered implicitly if they implement the
     * TargetListener interface.
     *
     * @param container
     *            the container to search for targetlisteners
     * @return an EventListenerList with all TargetListeners on this container
     *         and its children.
     */
    private EventListenerList collectTargetListeners(Container container) {
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
                EventListenerList list2 = collectTargetListeners(
                                                (Container) components[i]);
                Object[] objects = list2.getListenerList();
                for (int j = 1; j < objects.length; j += 2) {
                    list.add(TargetListener.class, (TargetListener) objects[j]);
                }
            }
        }
        if (container instanceof PropPanel) {
            /* We presume that the container equals this PropPanel. */
            for (TargetListener action : collectTargetListenerActions()) {
                list.add(TargetListener.class, action);
            }
        }
        return list;
    }

    private Collection<TargetListener> collectTargetListenerActions() {
        Collection<TargetListener> set = new HashSet<TargetListener>();
        for (Object obj : actions) {
            if (obj instanceof TargetListener) {
                set.add((TargetListener) obj);
            }
        }
        return set;
    }

    /*
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public final Object getTarget() {
        return target;
    }

    /*
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        SwingUtilities.invokeLater(new UMLChangeDispatch(this, 0));
    }

    /*
     * @see org.argouml.ui.TabTarget#shouldBeEnabled(java.lang.Object)
     */
    public boolean shouldBeEnabled(Object t) {
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;
        return Model.getFacade().isAUMLElement(t);
    }

    /**
     * This method can be overridden in derived Panels where the appropriate
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

    /*
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getProfile()
     */
    public ProfileConfiguration getProfile() {
        return ProjectManager.getManager().getCurrentProject()
                .getProfileConfiguration();
    }

    /*
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#getModelElement()
     */
    public final Object getModelElement() {
        return modelElement;
    }

    /*
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatElement(java.lang.Object)
     */
    public String formatElement(Object element) {
        return getProfile().getFormatingStrategy().formatElement(element,
                getDisplayNamespace());
    }

    /*
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatNamespace(java.lang.Object)
     */
    public String formatNamespace(Object namespace) {
        return getProfile().getFormatingStrategy().formatElement(namespace,
                null);
    }

    /*
     * @see org.argouml.uml.ui.UMLUserInterfaceContainer#formatCollection(java.util.Iterator)
     */
    public String formatCollection(Iterator iter) {
        Object namespace = getDisplayNamespace();
        return getProfile().getFormatingStrategy().formatCollection(iter,
                namespace);
    }


    /**
     * Get the delete action.
     *
     * @return the delete action
     */
    protected final Action getDeleteAction() {
        return ActionDeleteModelElements.getTargetFollower();
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

    /*
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        if (listenerList == null) {
            listenerList = collectTargetListeners(this);
        }
        setTarget(e.getNewTarget());
        if (isVisible()) {
            fireTargetAdded(e);
        }
    }

    /*
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        setTarget(e.getNewTarget());
        if (isVisible()) {
            fireTargetRemoved(e);
        }
    }

    /*
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
        if (isVisible()) {
            fireTargetSet(e);
        }
    }

    private void fireTargetSet(TargetEvent targetEvent) {
        if (listenerList == null) {
            listenerList = collectTargetListeners(this);
        }
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                ((TargetListener) listeners[i + 1]).targetSet(targetEvent);
            }
        }
    }

    private void fireTargetAdded(TargetEvent targetEvent) {
        if (listenerList == null) {
            listenerList = collectTargetListeners(this);
        }
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                ((TargetListener) listeners[i + 1]).targetAdded(targetEvent);
            }
        }
    }

    private void fireTargetRemoved(TargetEvent targetEvent) {
        if (listenerList == null) {
            listenerList = collectTargetListeners(this);
        }
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                ((TargetListener) listeners[i + 1]).targetRemoved(targetEvent);
            }
        }
    }

    /**
     * @param theTitleLabel
     *            the title of the panel shown at the top
     */
    protected void setTitleLabel(JLabel theTitleLabel) {
        titleLabel = theTitleLabel;
        titleLabel.setFont(stdFont);
    }

    /**
     * @return the title of the panel shown at the top
     */
    protected JLabel getTitleLabel() {
        return titleLabel;
    }

    protected final JPanel createBorderPanel(String title) {
    	return new GroupPanel(Translator.localize(title));
    }

    private class GroupPanel extends JPanel {

        public GroupPanel(String title) {
            super(new GridLayout2());
            TitledBorder border = new TitledBorder(Translator.localize(title));
            border.setTitleFont(stdFont);
            setBorder(border);
        }

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            for (final Component component : getComponents()) {
                component.setEnabled(enabled);
            }
        }
    }

    /**
     * If there are no buttons to show in the toolbar,
     * then set the height to e.g. 18, so that the title
     * is aligned right by the LabelledLayout.
     *
     * @param height the height
     */
    protected void setButtonPanelSize(int height) {
        /* Set the minimum and preferred equal,
         * so that the size is fixed for the labelledlayout.
         */
        buttonPanel.setMinimumSize(new Dimension(0, height));
        buttonPanel.setPreferredSize(new Dimension(0, height));
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


    /*
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
        // TODO: do we want to fire targetRemoved here or is it enough to just
        // stop updating the targets?
    }

    /*
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
        // Refresh the target for all our children which weren't getting
        // while not visible
        fireTargetSet(new TargetEvent(
                this, TargetEvent.TARGET_SET, null, new Object[] {target}));
    }

    /*
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
        // ignored
    }

    /*
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        // ignored
    }
}
