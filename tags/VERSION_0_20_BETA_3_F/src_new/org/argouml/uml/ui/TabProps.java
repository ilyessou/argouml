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

import java.awt.BorderLayout;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.application.api.PluggablePropertyPanel;
import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoModuleEvent;
import org.argouml.application.events.ArgoModuleEventListener;
import org.argouml.model.Model;
import org.argouml.ui.AbstractArgoJPanel;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.activity.ui.UMLActivityDiagram;
import org.argouml.uml.diagram.collaboration.ui.UMLCollaborationDiagram;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram;
import org.argouml.uml.diagram.state.ui.UMLStateDiagram;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.diagram.ui.PropPanelString;
import org.argouml.uml.diagram.ui.PropPanelUMLActivityDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLClassDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLCollaborationDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLDeploymentDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLSequenceDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLStateDiagram;
import org.argouml.uml.diagram.ui.PropPanelUMLUseCaseDiagram;
import org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelActionState;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelActivityGraph;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelCallState;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelObjectFlowState;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelPartition;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelSubactivityState;
import org.argouml.uml.ui.behavior.collaborations.PropPanelAssociationEndRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelAssociationRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelClassifierRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelCollaboration;
import org.argouml.uml.ui.behavior.collaborations.PropPanelInteraction;
import org.argouml.uml.ui.behavior.collaborations.PropPanelMessage;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelArgument;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelCallAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelComponentInstance;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelCreateAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelDestroyAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelLink;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelLinkEnd;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelNodeInstance;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelObject;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelReception;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelReturnAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelSendAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelSignal;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelStimulus;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelTerminateAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelUninterpretedAction;
import org.argouml.uml.ui.behavior.state_machines.PropPanelCallEvent;
import org.argouml.uml.ui.behavior.state_machines.PropPanelChangeEvent;
import org.argouml.uml.ui.behavior.state_machines.PropPanelCompositeState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelFinalState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelGuard;
import org.argouml.uml.ui.behavior.state_machines.PropPanelPseudostate;
import org.argouml.uml.ui.behavior.state_machines.PropPanelSignalEvent;
import org.argouml.uml.ui.behavior.state_machines.PropPanelState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelStateMachine;
import org.argouml.uml.ui.behavior.state_machines.PropPanelStubState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelSubmachineState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelSynchState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelTimeEvent;
import org.argouml.uml.ui.behavior.state_machines.PropPanelTransition;
import org.argouml.uml.ui.behavior.use_cases.PropPanelActor;
import org.argouml.uml.ui.behavior.use_cases.PropPanelExtend;
import org.argouml.uml.ui.behavior.use_cases.PropPanelExtensionPoint;
import org.argouml.uml.ui.behavior.use_cases.PropPanelInclude;
import org.argouml.uml.ui.behavior.use_cases.PropPanelUseCase;
import org.argouml.uml.ui.foundation.core.PropPanelAbstraction;
import org.argouml.uml.ui.foundation.core.PropPanelAssociation;
import org.argouml.uml.ui.foundation.core.PropPanelAssociationClass;
import org.argouml.uml.ui.foundation.core.PropPanelAssociationEnd;
import org.argouml.uml.ui.foundation.core.PropPanelAttribute;
import org.argouml.uml.ui.foundation.core.PropPanelClass;
import org.argouml.uml.ui.foundation.core.PropPanelComment;
import org.argouml.uml.ui.foundation.core.PropPanelComponent;
import org.argouml.uml.ui.foundation.core.PropPanelDataType;
import org.argouml.uml.ui.foundation.core.PropPanelDependency;
import org.argouml.uml.ui.foundation.core.PropPanelEnumeration;
import org.argouml.uml.ui.foundation.core.PropPanelFlow;
import org.argouml.uml.ui.foundation.core.PropPanelGeneralization;
import org.argouml.uml.ui.foundation.core.PropPanelInterface;
import org.argouml.uml.ui.foundation.core.PropPanelMethod;
import org.argouml.uml.ui.foundation.core.PropPanelNode;
import org.argouml.uml.ui.foundation.core.PropPanelOperation;
import org.argouml.uml.ui.foundation.core.PropPanelParameter;
import org.argouml.uml.ui.foundation.core.PropPanelPermission;
import org.argouml.uml.ui.foundation.core.PropPanelUsage;
import org.argouml.uml.ui.foundation.extension_mechanisms.PropPanelStereotype;
import org.argouml.uml.ui.foundation.extension_mechanisms.PropPanelTagDefinition;
import org.argouml.uml.ui.model_management.PropPanelModel;
import org.argouml.uml.ui.model_management.PropPanelPackage;
import org.argouml.uml.ui.model_management.PropPanelSubsystem;
import org.argouml.util.ConfigLoader;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigText;
import org.tigris.swidgets.Orientable;
import org.tigris.swidgets.Orientation;

/**
 * This is the tab on the details panel (DetailsPane) that holds the property
 * panel. On change of target, the property panel in TabProps is changed. <p>
 *
 * With the introduction of the TargetManager,
 * this class holds its original power
 * of controlling its target. The property panels (subclasses of PropPanel) for
 * which this class is the container are being registered as TargetListeners in
 * the setTarget method of this class.
 * They are not registered with TargetManager
 * but with this class to prevent race-conditions while firing TargetEvents from
 * TargetManager.
 */
public class TabProps
    extends AbstractArgoJPanel
    implements TabModelTarget, ArgoModuleEventListener {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(TabProps.class);
    ////////////////////////////////////////////////////////////////
    // instance variables
    private boolean shouldBeEnabled = false;
    private JPanel blankPanel = new JPanel();
    private Hashtable panels = new Hashtable();
    private JPanel lastPanel = null;
    private String panelClassBaseName = "";

    private Object target;

    /**
     * The list with targetlisteners, these are the property panels
     * managed by TabProps.
     * It should only contain one listener at a time.
     */
    private EventListenerList listenerList = new EventListenerList();

    /**
     * The constructor.
     *
     */
    public TabProps() {
        this("tab.properties", "ui.PropPanel");
    }

    /**
     * The constructor.
     *
     * @param tabName the name of the tab
     * @param panelClassBase the panel class base
     */
    public TabProps(String tabName, String panelClassBase) {
        super(tabName);
        TargetManager.getInstance().addTarget(this);
        setOrientation(ConfigLoader.getTabPropsOrientation());
        panelClassBaseName = panelClassBase;
        setLayout(new BorderLayout());
        //setFont(new Font("Dialog", Font.PLAIN, 10));

        ArrayList list = Argo.getPlugins(PluggablePropertyPanel.class);
        ListIterator iterator = list.listIterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            PluggablePropertyPanel ppp = (PluggablePropertyPanel) o;
            panels.put(ppp.getClassForPanel(), ppp.getPropertyPanel());
        }

        ArgoEventPump.addListener(ArgoEventTypes.ANY_MODULE_EVENT, this);
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        ArgoEventPump.removeListener(ArgoEventTypes.ANY_MODULE_EVENT, this);
    }

    /**
     * Set the orientation of the property panel.
     *
     * @param orientation the new orientation for this property panel
     *
     * @see org.tigris.swidgets.Orientable#setOrientation(org.tigris.swidgets.Orientation)
     */
    public void setOrientation(Orientation orientation) {
        super.setOrientation(orientation);
        Enumeration pps = panels.elements();
        while (pps.hasMoreElements()) {
            Object o = pps.nextElement();
            if (o instanceof Orientable) {
                Orientable orientable = (Orientable) o;
                orientable.setOrientation(orientation);
            }
        }
    }

    /**
     * Adds a property panel to the internal list. This allows a plugin to
     * add / register a new property panel at run-time.
     * This property panel will then
     * be displayed in the detatils pane whenever an element
     * of the given metaclass is selected.
     *
     * @param c the metaclass whose details show be displayed
     *          in the property panel p
     * @param p an instance of the property panel for the metaclass m
     *
     */
    public void addPanel(Class c, PropPanel p) {
        panels.put(c, p);
    }


    ////////////////////////////////////////////////////////////////
    // accessors
    /**
     * Sets the target of the property panel. The given target t
     * may either be a Diagram or a modelelement. If the target
     * given is a Fig, a check is made if the fig has an owning
     * modelelement and occurs on the current diagram.
     * If so, that modelelement is the target.
     *
     * @deprecated As of ArgoUml version 0.13.5,
     *         the visibility of this method will change in the future,
     *         replaced by {@link org.argouml.ui.targetmanager.TargetManager}.
     *
     * @see org.argouml.ui.TabTarget#setTarget(java.lang.Object)
     */
    public void setTarget(Object t) {
        // targets ought to be modelelements or diagrams
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;
        if (!(t == null || Model.getFacade().isABase(t)
                || t instanceof ArgoDiagram)) {
            return;
        }

        if (lastPanel != null) {
            remove(lastPanel);
            if (lastPanel instanceof TargetListener) {
                removeTargetListener((TargetListener) lastPanel);
            }
        }
        target = t;
        if (t == null) {
            add(blankPanel, BorderLayout.CENTER);
            shouldBeEnabled = false;
            lastPanel = blankPanel;
        } else {
            shouldBeEnabled = true;
            TabModelTarget newPanel = null;
            newPanel = findPanelFor(t);
            if (newPanel != null) {
                addTargetListener(newPanel);
            }
            if (newPanel instanceof JPanel) {
                add((JPanel) newPanel, BorderLayout.CENTER);
                shouldBeEnabled = true;
                lastPanel = (JPanel) newPanel;
            } else {
                add(blankPanel, BorderLayout.CENTER);
                shouldBeEnabled = false;
                lastPanel = blankPanel;
            }

        }
    }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        setTarget(TargetManager.getInstance().getTarget());
    }

    /**
     * Find the correct properties panel for the target.
     *
     * @param targetClass the target class
     * @return the tab panel
     */
    private TabModelTarget findPanelFor(Object trgt) {
        /* 1st attempt: get a panel that we created before: */
        TabModelTarget p = (TabModelTarget) panels.get(trgt.getClass());
        if (p != null) {
            LOG.info("Getting prop panel for: " + trgt.getClass().getName()
                    + ", " + "found (in cache?) " + p);
            return p;
        }

        /* 2nd attempt: If we didn't find the panel then
         * use the factory to create a new one*/
        p = createPropPanel(trgt);
        if (p != null) {
            LOG.info("Factory created " + p.getClass().getName()
                    + " for " + trgt.getClass().getName());
            panels.put(trgt.getClass(), p);
            return p;
        }

        // TODO: If the factory didn't know how to create the panel then
        // we fall through to the old reflection method. The code below
        // should be removed one the createPropPanel method is complete.

        /* 3rd attempt: use the reflection method: */
        Class panelClass = panelClassFor(trgt.getClass());
        if (panelClass == null) {
            LOG.error("No panel class found for: " + trgt.getClass());
            return null;
        }
        LOG.info("panelClass found for: " + panelClass);
        try {
            // if a class is abstract we do not need to try
            // to instantiate it.
            if (Modifier.isAbstract(panelClass.getModifiers())) {
                return null;
            }
            p = (TabModelTarget) panelClass.newInstance();
            // moved next line inside try block to avoid filling
            // the hashmap with bogus values.
            panels.put(trgt.getClass(), p);
        } catch (IllegalAccessException ignore) {
            // doubtfull if this must be ignored.
            LOG.error("Failed to create a prop panel", ignore);
            return null;
        } catch (InstantiationException ignore) {
            // doubtfull if this must be ignored.
            LOG.error("Failed to create a prop panel", ignore);
            return null;
        }

        LOG.warn(p.getClass().getName()
                + " has been created by reflection. "
                + "This should be added to the createPropPanel method.");
        return p;
    }

    /**
     * A factory method to create a PropPanel for a particular model
     * element.
     *
     * @param modelElement The model element
     * @return A new prop panel to display any model element of the given type
     */
    private TabModelTarget createPropPanel(Object modelElement) {

        // Create prop panels for diagrams
        if (modelElement instanceof UMLActivityDiagram) {
            return new PropPanelUMLActivityDiagram();
        }
        if (modelElement instanceof UMLClassDiagram) {
            return new PropPanelUMLClassDiagram();
        }
        if (modelElement instanceof UMLCollaborationDiagram) {
            return new PropPanelUMLCollaborationDiagram();
        }
        if (modelElement instanceof UMLDeploymentDiagram) {
            return new PropPanelUMLDeploymentDiagram();
        }
        if (modelElement instanceof UMLSequenceDiagram) {
            return new PropPanelUMLSequenceDiagram();
        }
        if (modelElement instanceof UMLStateDiagram) {
            return new PropPanelUMLStateDiagram();
        }
        if (modelElement instanceof UMLUseCaseDiagram) {
            return new PropPanelUMLUseCaseDiagram();
        }

        if (Model.getFacade().isASubmachineState(modelElement)) {
            return new PropPanelSubmachineState();
        }
        if (Model.getFacade().isASubactivityState(modelElement)) {
            return new PropPanelSubactivityState();
        }
        // Create prop panels for model elements
        if (Model.getFacade().isAAbstraction(modelElement)) {
            return new PropPanelAbstraction();
        }
        if (Model.getFacade().isAActionState(modelElement)) {
            return new PropPanelActionState();
        }
        if (Model.getFacade().isAActivityGraph(modelElement)) {
            return new PropPanelActivityGraph();
        }
        if (Model.getFacade().isAActor(modelElement)) {
            return new PropPanelActor();
        }
        if (Model.getFacade().isAArgument(modelElement)) {
            return new PropPanelArgument();
        }
        if (Model.getFacade().isAAssociationClass(modelElement)) {
            return new PropPanelAssociationClass();
        }
        if (Model.getFacade().isAAssociationRole(modelElement)) {
            return new PropPanelAssociationRole();
        }
        if (Model.getFacade().isAAssociation(modelElement)) {
            return new PropPanelAssociation();
        }
        if (Model.getFacade().isAAssociationEndRole(modelElement)) {
            return new PropPanelAssociationEndRole();
        }
        if (Model.getFacade().isAAssociationEnd(modelElement)) {
            return new PropPanelAssociationEnd();
        }
        if (Model.getFacade().isAAttribute(modelElement)) {
            return new PropPanelAttribute();
        }
        if (Model.getFacade().isACallAction(modelElement)) {
            return new PropPanelCallAction();
        }
        if (Model.getFacade().isACallState(modelElement)) {
            return new PropPanelCallState();
        }
        if (Model.getFacade().isAClass(modelElement)) {
            return new PropPanelClass();
        }
        if (Model.getFacade().isAClassifierRole(modelElement)) {
            return new PropPanelClassifierRole();
        }
        if (Model.getFacade().isACollaboration(modelElement)) {
            return new PropPanelCollaboration();
        }
        if (Model.getFacade().isAComment(modelElement)) {
            return new PropPanelComment();
        }
        if (Model.getFacade().isAComponent(modelElement)) {
            return new PropPanelComponent();
        }
        if (Model.getFacade().isAComponentInstance(modelElement)) {
            return new PropPanelComponentInstance();
        }
        if (Model.getFacade().isACompositeState(modelElement)) {
            return new PropPanelCompositeState();
        }
        if (Model.getFacade().isACreateAction(modelElement)) {
            return new PropPanelCreateAction();
        }
        if (Model.getFacade().isAEnumeration(modelElement)) {
            return new PropPanelEnumeration();
        }
        if (Model.getFacade().isADataType(modelElement)) {
            return new PropPanelDataType();
        }
        if (Model.getFacade().isADestroyAction(modelElement)) {
            return new PropPanelDestroyAction();
        }
        if (Model.getFacade().isAExtend(modelElement)) {
            return new PropPanelExtend();
        }
        if (Model.getFacade().isAExtensionPoint(modelElement)) {
            return new PropPanelExtensionPoint();
        }
        if (Model.getFacade().isAFinalState(modelElement)) {
            return new PropPanelFinalState();
        }
        if (Model.getFacade().isAFlow(modelElement)) {
            return new PropPanelFlow();
        }
        if (Model.getFacade().isAGeneralization(modelElement)) {
            return new PropPanelGeneralization();
        }
        if (Model.getFacade().isAGuard(modelElement)) {
            return new PropPanelGuard();
        }
        if (Model.getFacade().isAInclude(modelElement)) {
            return new PropPanelInclude();
        }
        if (Model.getFacade().isAInteraction(modelElement)) {
            return new PropPanelInteraction();
        }
        if (Model.getFacade().isAInterface(modelElement)) {
            return new PropPanelInterface();
        }
        if (Model.getFacade().isALink(modelElement)) {
            return new PropPanelLink();
        }
        if (Model.getFacade().isALinkEnd(modelElement)) {
            return new PropPanelLinkEnd();
        }
        if (Model.getFacade().isAMessage(modelElement)) {
            return new PropPanelMessage();
        }
        if (Model.getFacade().isAMethod(modelElement)) {
            return new PropPanelMethod();
        }
        if (Model.getFacade().isAModel(modelElement)) {
            return new PropPanelModel();
        }
        if (Model.getFacade().isANode(modelElement)) {
            return new PropPanelNode();
        }
        if (Model.getFacade().isANodeInstance(modelElement)) {
            return new PropPanelNodeInstance();
        }
        if (Model.getFacade().isAObject(modelElement)) {
            return new PropPanelObject();
        }
        if (Model.getFacade().isAObjectFlowState(modelElement)) {
            return new PropPanelObjectFlowState();
        }
        if (Model.getFacade().isAOperation(modelElement)) {
            return new PropPanelOperation();
        }
        if (Model.getFacade().isAPackage(modelElement)) {
            return new PropPanelPackage();
        }
        if (Model.getFacade().isAParameter(modelElement)) {
            return new PropPanelParameter();
        }
        if (Model.getFacade().isAPartition(modelElement)) {
            return new PropPanelPartition();
        }
        if (Model.getFacade().isAPermission(modelElement)) {
            return new PropPanelPermission();
        }
        if (Model.getFacade().isAPseudostate(modelElement)) {
            return new PropPanelPseudostate();
        }
        if (Model.getFacade().isAReception(modelElement)) {
            return new PropPanelReception();
        }
        if (Model.getFacade().isAReturnAction(modelElement)) {
            return new PropPanelReturnAction();
        }
        if (Model.getFacade().isASendAction(modelElement)) {
            return new PropPanelSendAction();
        }
        if (Model.getFacade().isASignal(modelElement)) {
            return new PropPanelSignal();
        }
        if (Model.getFacade().isAState(modelElement)) {
            return new PropPanelState();
        }
        if (Model.getFacade().isAStateMachine(modelElement)) {
            return new PropPanelStateMachine();
        }
        if (Model.getFacade().isAStereotype(modelElement)) {
            return new PropPanelStereotype();
        }
        if (Model.getFacade().isAStimulus(modelElement)) {
            return new PropPanelStimulus();
        }
        if (Model.getFacade().isAStubState(modelElement)) {
            return new PropPanelStubState();
        }
        if (Model.getFacade().isASubsystem(modelElement)) {
            return new PropPanelSubsystem();
        }
        if (Model.getFacade().isASynchState(modelElement)) {
            return new PropPanelSynchState();
        }
        if (Model.getFacade().isATagDefinition(modelElement)) {
            return new PropPanelTagDefinition();
        }
        if (Model.getFacade().isATerminateAction(modelElement)) {
            return new PropPanelTerminateAction();
        }
        if (Model.getFacade().isATransition(modelElement)) {
            return new PropPanelTransition();
        }
        if (Model.getFacade().isAUninterpretedAction(modelElement)) {
            return new PropPanelUninterpretedAction();
        }
        if (Model.getFacade().isAUsage(modelElement)) {
            return new PropPanelUsage();
        }
        if (Model.getFacade().isAUseCase(modelElement)) {
            return new PropPanelUseCase();
        }
        if (Model.getFacade().isACallEvent(modelElement)) {
            return new PropPanelCallEvent();
        }
        if (Model.getFacade().isAChangeEvent(modelElement)) {
            return new PropPanelChangeEvent();
        }
        if (Model.getFacade().isASignalEvent(modelElement)) {
            return new PropPanelSignalEvent();
        }
        if (Model.getFacade().isATimeEvent(modelElement)) {
            return new PropPanelTimeEvent();
        }
        if (Model.getFacade().isADependency(modelElement)) {
            return new PropPanelDependency();
        }
        // Create prop panels for primitives
        if (modelElement instanceof FigText) {
            return new PropPanelString();
        }


        return null;
    }

    /**
     * Locate the panel for the given class.
     * TODO: Remove when createPropPanel complete
     *
     * @param targetClass the given class
     * @return the properties panel for the given class, or null if not found
     */
    private Class panelClassFor(Class targetClass) {

        String panelClassName = "";
        String pack = "org.argouml.uml";
        String base = "";

        String targetClassName = targetClass.getName();
        LOG.info("Trying to locate panel for: " + targetClassName);
        int lastDot = targetClassName.lastIndexOf(".");

        //remove "org.omg.uml."
        if (lastDot > 0) {
            base = targetClassName.substring(12, lastDot + 1);
        } else {
            base = targetClassName.substring(12);
        }

        targetClassName = Model.getMetaTypes().getName(targetClass);

        // This doesn't work for panel property tabs - they are being put in the
        // wrong place. Really we should have defined these are preloaded them
        // along with ArgoDiagram in initPanels above.

        try {
            panelClassName =
                pack + ".ui." + base + "PropPanel" + targetClassName;
            LOG.info("Looking for: " + panelClassName);
            return Class.forName(panelClassName);
        } catch (ClassNotFoundException ignore) {
            LOG.error(
		      "Class " + panelClassName + " for Panel not found!",
		      ignore);
        }
        return null;
    }

    /**
     * @return the name
     */
    protected String getClassBaseName() {
        return panelClassBaseName;
    }

    /**
     * Returns the current target.
     * @deprecated As of ArgoUml version 0.13.5,
     * the visibility of this method will change in the future, replaced by
     * {@link org.argouml.ui.targetmanager.TargetManager#getTarget()
     * TargetManager.getInstance().getTarget()}.
     *
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Determines if the property panel should be enabled. Returns true if it
     * should be enabled. The property panel should allways be enabled if the
     * target is an instance of a modelelement or an argodiagram.
     * If the target given is a Fig, a check is made if the fig
     * has an owning modelelement and occurs on
     * the current diagram. If so, that modelelement is the target.
     *
     * @see org.argouml.ui.TabTarget#shouldBeEnabled(Object)
     */
    public boolean shouldBeEnabled(Object t) {
        t = (t instanceof Fig) ? ((Fig) t).getOwner() : t;
        if (t instanceof Diagram || Model.getFacade().isABase(t)) {
            shouldBeEnabled = true;
        } else {
            shouldBeEnabled = false;
        }

        return shouldBeEnabled;
    }

    /**
     * @see org.argouml.application.events.ArgoModuleEventListener#moduleLoaded(org.argouml.application.events.ArgoModuleEvent)
     */
    public void moduleLoaded(ArgoModuleEvent event) {
        if (event.getSource() instanceof PluggablePropertyPanel) {
            PluggablePropertyPanel p =
                (PluggablePropertyPanel) event.getSource();
            panels.put(p.getClassForPanel(), p.getPropertyPanel());

        }
    }
    /**
     * @see org.argouml.application.events.ArgoModuleEventListener#moduleUnloaded(org.argouml.application.events.ArgoModuleEvent)
     */
    public void moduleUnloaded(ArgoModuleEvent event) {
    }

    /**
     * @see org.argouml.application.events.ArgoModuleEventListener#moduleEnabled(org.argouml.application.events.ArgoModuleEvent)
     */
    public void moduleEnabled(ArgoModuleEvent event) {
    }

    /**
     * @see org.argouml.application.events.ArgoModuleEventListener#moduleDisabled(org.argouml.application.events.ArgoModuleEvent)
     */
    public void moduleDisabled(ArgoModuleEvent event) {
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetAdded(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(TargetManager.getInstance().getSingleTarget());
        fireTargetAdded(e);
        if (listenerList.getListenerCount() > 0) {
            validate();
            repaint();
        }

    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetRemoved(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        setTarget(TargetManager.getInstance().getSingleTarget());
        fireTargetRemoved(e);
        validate();
        repaint();
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(TargetManager.getInstance().getSingleTarget());
        fireTargetSet(e);
        validate();
        repaint();
    }

    /**
     * Adds a listener.
     * @param listener the listener to add
     */
    private void addTargetListener(TargetListener listener) {
        listenerList.add(TargetListener.class, listener);
    }

    /**
     * Removes a target listener.
     * @param listener the listener to remove
     */
    private void removeTargetListener(TargetListener listener) {
        listenerList.remove(TargetListener.class, listener);
    }

    private void fireTargetSet(TargetEvent targetEvent) {
        //      Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                // Lazily create the event:
		((TargetListener) listeners[i + 1]).targetSet(targetEvent);
            }
        }
    }

    private void fireTargetAdded(TargetEvent targetEvent) {
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
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TargetListener.class) {
                // Lazily create the event:
                ((TargetListener) listeners[i + 1]).targetRemoved(targetEvent);
            }
        }
    }

} /* end class TabProps */

