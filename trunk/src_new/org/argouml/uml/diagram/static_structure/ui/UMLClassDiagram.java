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

package org.argouml.uml.diagram.static_structure.ui;

import java.beans.PropertyVetoException;

import javax.swing.Action;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.LayerPerspectiveMutable;

/**
 * @author jrobbins@ics.uci.edy
 */
public class UMLClassDiagram extends UMLDiagram {

    private static final Logger LOG = Logger.getLogger(UMLClassDiagram.class);

    ////////////////
    // actions for toolbar
    private Action actionAssociationClass;
    private Action actionClass;
    private Action actionObject;
    private Action actionInterface;
    private Action actionDependency;
    private Action actionPermission;
    private Action actionUsage;
    private Action actionLink;
    private Action actionGeneralization;
    private Action actionRealization;
    private Action actionPackage;
    private Action actionModel;
    private Action actionSubsystem;
    private Action actionAssociation;
    private Action actionAssociationEnd;
    private Action actionAggregation;
    private Action actionComposition;
    private Action actionUniAssociation;
    private Action actionUniAggregation;
    private Action actionUniComposition;
    private Action actionAttribute;
    private Action actionOperation;

    /**
     * Constructor.
     */
    public UMLClassDiagram() {
        super();
    }

    /**
     * Constructor.
     *
     * @param name the name for the new diagram
     * @param namespace the namespace for the new diagram
     */
    public UMLClassDiagram(String name, Object namespace) {
        super(name, namespace);
    }

    /**
     * The constructor. A default unique diagram name is constructed.
     * @param m the namespace
     */
    public UMLClassDiagram(Object m) {
        super(m);
        try {
            setName(getNewDiagramName());
        } catch (PropertyVetoException pve) { }
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#setNamespace(java.lang.Object)
     */
    public void setNamespace(Object ns) {
        if (!Model.getFacade().isANamespace(ns)) {
            LOG.error("Illegal argument. "
                  + "Object " + ns + " is not a namespace");
            throw new IllegalArgumentException("Illegal argument. "
            			       + "Object " + ns
            			       + " is not a namespace");
        }
        boolean init = (null == getNamespace());
        super.setNamespace(ns);
        ClassDiagramGraphModel gm;
        if (init) {
                gm = new ClassDiagramGraphModel();
        }else {
        	gm = (ClassDiagramGraphModel) this.getGraphModel();
        }
        gm.setHomeModel(ns);
        if (init) {
        	LayerPerspective lay =
        		new LayerPerspectiveMutable(Model.getFacade().getName(ns), gm);
        	ClassDiagramRenderer rend = new ClassDiagramRenderer(); // singleton
        	lay.setGraphNodeRenderer(rend);
        	lay.setGraphEdgeRenderer(rend);
        	setLayer(lay);
        }
    }

    /**
     * Get the actions from which to create a toolbar or equivilent
     * graphic trigger.
     *
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getUmlActions()
     */
    protected Object[] getUmlActions() {
        Object[] actions = {
            getActionPackage(),
            getActionClass(),
            null,
            getAssociationActions(),
            getAggregationActions(),
            getCompositionActions(),
            getActionAssociationEnd(),
            getActionGeneralization(),
            null,
            getActionInterface(),
            getActionRealization(),
            null,
            getDependencyActions(),
            null,
            getActionAttribute(),
            getActionOperation(),
            getActionAssociationClass(),
        };

        return actions;
    }

    // To enable models and subsystems,
    // replace getActionPackage() in the function getUmlActions() above
    // with getPackageActions().
    private Object[] getPackageActions() {
        Object[] actions =
        {
                getActionPackage(),
                getActionModel(),
                getActionSubsystem(),
        };
        manageDefault(actions, "diagram.class.package");
        return actions;
    }

    /**
     * Return an array of dependency actions in the
     * pattern of which to build a popup toolbutton.
     */
    private Object[] getDependencyActions() {
        Object[] actions = {
            getActionDependency(),
            getActionPermission(),
            getActionUsage()
        };
        manageDefault(actions, "diagram.class.dependency");
        return actions;
    }

    /**
     * Return an array of association actions in the
     * pattern of which to build a popup toolbutton.
     */
    private Object[] getAssociationActions() {
        // This calls the getters to fetch actions even though the
        // action variables are defined is instances of this class.
        // This is because any number of action getters could have
        // been overridden in a descendent and it is the action from
        // that overridden method that should be returned in the array.
        Object[] actions = {
            getActionAssociation(),
            getActionUniAssociation()
         };
        manageDefault(actions, "diagram.class.association");
        return actions;
    }

    private Object[] getAggregationActions() {
        Object[] actions = {
            getActionAggregation(),
            getActionUniAggregation()
        };
        manageDefault(actions, "diagram.class.aggregation");
        return actions;
    }

    private Object[] getCompositionActions() {
        Object[] actions = {
            getActionComposition(),
            getActionUniComposition()
        };
        manageDefault(actions, "diagram.class.composition");
        return actions;
    }

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
        return Translator.localize("label.class-diagram");
    }

    /**
     * @return Returns the actionAggregation.
     */
    protected Action getActionAggregation() {
        if (actionAggregation == null) {
            actionAggregation = makeCreateAssociationAction(
                Model.getAggregationKind().getAggregate(),
                false,
                "button.new-aggregation");
        }
        return actionAggregation;
    }
    /**
     * @return Returns the actionAssociation.
     */
    protected Action getActionAssociation() {
        if (actionAssociation == null) {
            actionAssociation = makeCreateAssociationAction(
                Model.getAggregationKind().getNone(),
                false, "button.new-association");
        }
        return actionAssociation;
    }
    /**
     * @return Returns the actionAssociation.
     */
    protected Action getActionAssociationEnd() {
        if (actionAssociationEnd == null) {
            actionAssociationEnd = makeCreateAssociationEndAction("button.new-association-end");
        }
        return actionAssociationEnd;
    }

    /**
     * @return Returns the actionClass.
     */
    protected Action getActionClass() {
        if (actionClass == null) {
            actionClass =
                makeCreateNodeAction(Model.getMetaTypes().getUMLClass(),
                        	     "button.new-class");
        }

        return actionClass;
    }

    /**
    * @return Returns the actionAssociationClass.
    */
    protected Action getActionAssociationClass() {
        if (actionAssociationClass == null) {
            actionAssociationClass =
                makeCreateEdgeAction(
                    Model.getMetaTypes().getAssociationClass(),
                    "button.new-associationclass");
        }
        return actionAssociationClass;
    }
    /**
     * @return Returns the actionComposition.
     */
    protected Action getActionComposition() {
        if (actionComposition == null) {
            actionComposition = makeCreateAssociationAction(
                Model.getAggregationKind().getComposite(),
                false, "button.new-composition");
        }
        return actionComposition;
    }
    /**
     * @return Returns the actionDepend.
     */
    protected Action getActionDependency() {
        if (actionDependency == null) {
            actionDependency =
                makeCreateEdgeAction(
                        Model.getMetaTypes().getDependency(),
                        "button.new-dependency");
        }
        return actionDependency;
    }

    /**
     * @return Returns the actionGeneralize.
     */
    protected Action getActionGeneralization() {
        if (actionGeneralization == null) {
            actionGeneralization = makeCreateEdgeAction(
                    Model.getMetaTypes().getGeneralization(),
                    "button.new-generalization");
        }

        return actionGeneralization;
    }

    /**
     * @return Returns the actionInterface.
     */
    protected Action getActionInterface() {
        if (actionInterface == null) {
            actionInterface =
                makeCreateNodeAction(
                        Model.getMetaTypes().getInterface(),
                        "button.new-interface");
        }
        return actionInterface;
    }

    /**
     * @return Returns the actionLink.
     */
    protected Action getActionLink() {
        if (actionLink == null) {
            actionLink =
                makeCreateEdgeAction(Model.getMetaTypes().getLink(), "Link");
        }

        return actionLink;
    }
    /**
     * @return Returns the actionModel.
     */
    protected Action getActionModel() {
        if (actionModel == null) {
            actionModel =
                makeCreateNodeAction(Model.getMetaTypes().getModel(), "Model");
        }

        return actionModel;
    }

    /**
     * @return Returns the actionPackage.
     */
    protected Action getActionPackage() {
        if (actionPackage == null) {
            actionPackage =
                makeCreateNodeAction(Model.getMetaTypes().getPackage(),
                        	     "button.new-package");
        }

        return actionPackage;
    }
    /**
     * @return Returns the actionPermission.
     */
    protected Action getActionPermission() {
        if (actionPermission == null) {
            actionPermission =
                makeCreateEdgeAction(
                        Model.getMetaTypes().getPermission(),
                        "button.new-permission");
        }

        return actionPermission;
    }

    /**
     * @return Returns the actionRealize.
     */
    protected Action getActionRealization() {
        if (actionRealization == null) {
            actionRealization =
                makeCreateEdgeAction(
                        Model.getMetaTypes().getAbstraction(),
                        "button.new-realization");
        }

        return actionRealization;
    }

    /**
     * @return Returns the actionSubsystem.
     */
    protected Action getActionSubsystem() {
        if (actionSubsystem == null) {
            actionSubsystem =
                makeCreateNodeAction(
                        Model.getMetaTypes().getSubsystem(),
                        "Subsystem");
        }
        return actionSubsystem;
    }

    /**
     * @return Returns the actionUniAggregation.
     */
    protected Action getActionUniAggregation() {
        if (actionUniAggregation == null) {
            actionUniAggregation = makeCreateAssociationAction(
                Model.getAggregationKind().getAggregate(),
                true,
                "button.new-uniaggregation");
        }
        return actionUniAggregation;
    }

    /**
     * @return Returns the actionUniAssociation.
     */
    protected Action getActionUniAssociation() {
        if (actionUniAssociation == null) {
            actionUniAssociation = makeCreateAssociationAction(
                Model.getAggregationKind().getNone(),
                true,
                "button.new-uniassociation");
        }
        return actionUniAssociation;
    }

    /**
     * @return Returns the actionUniComposition.
     */
    protected Action getActionUniComposition() {
        if (actionUniComposition == null) {
            actionUniComposition = makeCreateAssociationAction(
                Model.getAggregationKind().getComposite(),
                true,
                "button.new-unicomposition");
        }
        return actionUniComposition;
    }

    /**
     * @return Returns the actionUsage.
     */
    protected Action getActionUsage() {
        if (actionUsage == null) {
            actionUsage =
                makeCreateEdgeAction(Model.getMetaTypes().getUsage(), "button.new-usage");
        }
        return actionUsage;
    }

    /**
     * @return Returns the actionAttribute.
     */
    private Action getActionAttribute() {
        if (actionAttribute == null) {
            actionAttribute =
                TargetManager.getInstance().getAddAttributeAction();
        }
        return actionAttribute;
    }

    /**
     * @return Returns the actionOperation.
     */
    private Action getActionOperation() {
        if (actionOperation == null) {
            actionOperation =
                TargetManager.getInstance().getAddOperationAction();
        }
        return actionOperation;
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#isRelocationAllowed(java.lang.Object)
     */
    public boolean isRelocationAllowed(Object base)  {
    	return Model.getFacade().isANamespace(base);
    }

	/**
	 * @see org.argouml.uml.diagram.ui.UMLDiagram#relocate(java.lang.Object)
	 */
	public boolean relocate(Object base) {
		setNamespace(base);
		this.damage();
		return true;
	}

} /* end class UMLClassDiagram */
