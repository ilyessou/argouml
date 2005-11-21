// $Id$
// Copyright (c) 2003-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.deployment.ui;

import java.beans.PropertyVetoException;

import javax.swing.Action;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.CmdCreateNode;
import org.argouml.ui.CmdSetMode;
import org.argouml.uml.diagram.deployment.DeploymentDiagramGraphModel;
import org.argouml.uml.diagram.ui.ActionSetAddAssociationMode;
import org.argouml.uml.diagram.ui.RadioAction;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.LayerPerspectiveMutable;
import org.tigris.gef.base.ModeCreatePolyEdge;

/**
 * The base class of the deployment diagram.<p>
 *
 * Defines the toolbar, provides for its initialization and provides
 * constructors for a top level diagram and one within a defined
 * namespace.<p>
 *
 * @author Clemens Eichler
 */
public class UMLDeploymentDiagram extends UMLDiagram {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(UMLDeploymentDiagram.class);

    ////////////////
    // actions for toolbar

    private Action actionMNode;
    private Action actionMNodeInstance;
    private Action actionMComponent;
    private Action actionMComponentInstance;
    private Action actionMClass;
    private Action actionMInterface;
    private Action actionMObject;
    private Action actionMDependency;
    private Action actionMAssociation;
    private Action actionMLink;
    private Action actionAssociation;
    private Action actionAggregation;
    private Action actionComposition;
    private Action actionUniAssociation;
    private Action actionUniAggregation;
    private Action actionUniComposition;
    private Action actionMGeneralization;
    private Action actionMAbstraction;


    ////////////////////////////////////////////////////////////////
    // contructors

    /**
     * Constructor.
     */
    public UMLDeploymentDiagram() {

        try {
            setName(getNewDiagramName());
        } catch (PropertyVetoException pve) { }
    }

    /**
     * @param namespace the namespace for the new diagram
     */
    public UMLDeploymentDiagram(Object namespace) {
        this();
        setNamespace(namespace);
    }

    /**
     * Method to perform a number of important initializations of a
     * <em>Deployment Diagram</em>.<p>
     *
     * Each diagram type has a similar <em>UMLxxxDiagram</em> class.<p>
     *
     * Changed <em>lay</em> from <em>LayerPerspective</em> to
     * <em>LayerPerspectiveMutable</em>.  This class is a child of
     * <em>LayerPerspective</em> and was implemented to correct some
     * difficulties in changing the model. <em>lay</em> is used mainly
     * in <em>LayerManager</em>(GEF) to control the adding, changing and
     * deleting layers on the diagram...<p>
     *
     * @param handle Namespace from the model
     * @author psager@tigris.org Jan. 24, 2002
     */
    public void setNamespace(Object handle) {
        if (!Model.getFacade().isANamespace(handle)) {
            LOG.error(
                "Illegal argument. Object " + handle + " is not a namespace");
            throw new IllegalArgumentException(
                "Illegal argument. Object " + handle + " is not a namespace");
        }
        Object m = /*(MNamespace)*/ handle;
        super.setNamespace(m);
        DeploymentDiagramGraphModel gm = new DeploymentDiagramGraphModel();
        gm.setHomeModel(m);
        LayerPerspective lay =
            new LayerPerspectiveMutable(Model.getFacade().getName(m), gm);
        DeploymentDiagramRenderer rend = new DeploymentDiagramRenderer();
        lay.setGraphNodeRenderer(rend);
        lay.setGraphEdgeRenderer(rend);
        setLayer(lay);

        // singleton

    }

    /**
     * Get the actions from which to create a toolbar or equivilent
     * graphic triggers.
     *
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getUmlActions()
     */
    protected Object[] getUmlActions() {
        Object[] actions = {
            getActionMNode(),
            getActionMNodeInstance(),
            getActionMComponent(),
            getActionMComponentInstance(),
            getActionMGeneralization(),
            getActionMAbstraction(),
            getActionMDependency(),
            getAssociationActions(),
            getActionMObject(),
            getActionMLink(),
        };
        return actions;
    }

    private Object[] getAssociationActions() {
        Object[][] actions = {
	    {getActionAssociation(), getActionUniAssociation() },
	    {getActionAggregation(), getActionUniAggregation() },
	    {getActionComposition(), getActionUniComposition() },
        };
        manageDefault(actions, "diagram.deployment.association");
        return actions;
    }

    /**
     * The UID.
     */
    static final long serialVersionUID = -375918274062198744L;

    /**
     * Creates a new diagramname.
     * @return String
     */
    protected String getNewDiagramName() {
        String name = getLabelName() + " " + getNextDiagramSerial();
        if (!ProjectManager.getManager().getCurrentProject()
	        .isValidDiagramName(name)) {
            name = getNewDiagramName();
        }
        return name;
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getLabelName()
     */
    public String getLabelName() {
        return Translator.localize("label.deployment-diagram");
    }

    //////////////////////////////
    // Getters for plugin modules:
    //////////////////////////////

    /**
     * @return Returns the actionAggregation.
     */
    protected Action getActionAggregation() {
        if (actionAggregation == null) {
            actionAggregation =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getAggregate(),
                        false,
                        "button.new-aggregation"));
        }
        return actionAggregation;
    }

    /**
     * @return Returns the actionAssociation.
     */
    protected Action getActionAssociation() {
        if (actionAssociation == null) {
            actionAssociation =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getNone(),
                        false,
                        "button.new-association"));
        }
        return actionAssociation;
    }

    /**
     * @return Returns the actionComposition.
     */
    protected Action getActionComposition() {
        if (actionComposition == null) {
            actionComposition =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getComposite(),
                        false,
                        "button.new-composition"));
        }
        return actionComposition;
    }

    /**
     * @return Returns the actionMAssociation.
     */
    protected Action getActionMAssociation() {
        if (actionMAssociation == null) {
            actionMAssociation =
                new RadioAction(new CmdSetMode(
                        ModeCreatePolyEdge.class,
                        "edgeClass",
                        Model.getMetaTypes().getAssociation(),
                        "button.new-association"));
        }
        return actionMAssociation;
    }

    /**
     * @return Returns the actionMClass.
     */
    protected Action getActionMClass() {
        if (actionMClass == null) {
            actionMClass =
                new RadioAction(
                        new CmdCreateNode(Model.getMetaTypes().getUMLClass(),
                                "button.new-class"));
        }
        return actionMClass;
    }

    /**
     * @return Returns the actionMComponent.
     */
    protected Action getActionMComponent() {
        if (actionMComponent == null) {
            actionMComponent =
                new RadioAction(
                        new CmdCreateNode(
                                Model.getMetaTypes().getComponent(),
                                "button.new-component"));
        }
        return actionMComponent;
    }

    /**
     * @return Returns the actionMComponentInstance.
     */
    protected Action getActionMComponentInstance() {
        if (actionMComponentInstance == null) {
            actionMComponentInstance =
                new RadioAction(new CmdCreateNode(
                        Model.getMetaTypes().getComponentInstance(),
                	"button.new-componentinstance"));
        }
        return actionMComponentInstance;
    }

    /**
     * @return Returns the actionMDependency.
     */
    protected Action getActionMDependency() {
        if (actionMDependency == null) {
            actionMDependency =
                new RadioAction(new CmdSetMode(
                        ModeCreatePolyEdge.class,
                        "edgeClass",
                        Model.getMetaTypes().getDependency(),
                        "button.new-dependency"));
        }
        return actionMDependency;
    }

    /**
     * @return Returns the actionMGeneralization.
     */
    protected Action getActionMGeneralization() {
        if (actionMGeneralization == null) {
            actionMGeneralization =
                new RadioAction(new CmdSetMode(
                        ModeCreatePolyEdge.class,
                        "edgeClass",
                        Model.getMetaTypes().getGeneralization(),
                	"button.new-generalization"));
        }
        return actionMGeneralization;
    }

    /**
     * @return Returns the actionMAbstraction.
     */
    protected Action getActionMAbstraction() {
        if (actionMAbstraction == null) {
            actionMAbstraction =
                new RadioAction(new CmdSetMode(
                        ModeCreatePolyEdge.class,
                        "edgeClass",
                        Model.getMetaTypes().getAbstraction(),
                	"button.new-realization"));
        }
        return actionMAbstraction;
    }

    /**
     * @return Returns the actionMInterface.
     */
    protected Action getActionMInterface() {
        if (actionMInterface == null) {
            actionMInterface =
                new RadioAction(
                        new CmdCreateNode(
                                Model.getMetaTypes().getInterface(),
                                "button.new-interface"));
        }
        return actionMInterface;
    }

    /**
     * @return Returns the actionMLink.
     */
    protected Action getActionMLink() {
        if (actionMLink == null) {
            actionMLink =
                new RadioAction(new CmdSetMode(
                        ModeCreatePolyEdge.class,
                        "edgeClass",
                        Model.getMetaTypes().getLink(),
                        "button.new-link"));
        }
        return actionMLink;
    }

    /**
     * @return Returns the actionMNode.
     */
    protected Action getActionMNode() {
        if (actionMNode == null) {
            actionMNode =
                new RadioAction(new CmdCreateNode(
                    Model.getMetaTypes().getNode(),
                    "button.new-node"));
        }
        return actionMNode;
    }

    /**
     * @return Returns the actionMNodeInstance.
     */
    protected Action getActionMNodeInstance() {
        if (actionMNodeInstance == null) {
            actionMNodeInstance =
                new RadioAction(new CmdCreateNode(
                    Model.getMetaTypes().getNodeInstance(),
                    "button.new-nodeinstance"));
        }
        return actionMNodeInstance;
    }

    /**
     * @return Returns the actionMObject.
     */
    protected Action getActionMObject() {
        if (actionMObject == null) {
            actionMObject =
                new RadioAction(
                        new CmdCreateNode(Model.getMetaTypes().getObject(),
                                "button.new-object"));
        }
        return actionMObject;
    }

    /**
     * @return Returns the actionUniAggregation.
     */
    protected Action getActionUniAggregation() {
        if (actionUniAggregation == null) {
            actionUniAggregation =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getAggregate(),
                        true, "button.new-uniaggregation"));
        }
        return actionUniAggregation;
    }

    /**
     * @return Returns the actionUniAssociation.
     */
    protected Action getActionUniAssociation() {
        if (actionUniAssociation == null) {
            actionUniAssociation =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getNone(),
                        true, "button.new-uniassociation"));
        }
        return actionUniAssociation;
    }

    /**
     * @return Returns the actionUniComposition.
     */
    protected Action getActionUniComposition() {
        if (actionUniComposition == null) {
            actionUniComposition =
                new RadioAction(
                    new ActionSetAddAssociationMode(
                        Model.getAggregationKind().getComposite(),
                        true, "button.new-unicomposition"));
        }
        return actionUniComposition;
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#isRelocationAllowed(java.lang.Object)
     */
    public boolean isRelocationAllowed(Object base)  {
    	return false;
		/* TODO: We may return the following when the
		 * relocate() has been implemented.
                 */
//    	base == ProjectManager.getManager().getCurrentProject().getModel();
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#relocate(java.lang.Object)
     */
    public boolean relocate(Object base) {
        return false;
    }
} /* end class UMLDeploymentDiagram */
