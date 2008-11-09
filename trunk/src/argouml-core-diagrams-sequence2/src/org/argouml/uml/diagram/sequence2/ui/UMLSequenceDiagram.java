// $Id$
// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.uml.diagram.sequence2.ui;

import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.diagram.sequence2.ActionSetAddMessageMode;
import org.argouml.uml.diagram.sequence2.SequenceDiagramGraphModel;
import org.argouml.uml.diagram.static_structure.ui.FigComment;
import org.argouml.uml.diagram.ui.ActionSetMode;
import org.argouml.uml.diagram.ui.RadioAction;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.LayerPerspectiveMutable;
import org.tigris.gef.base.ModePlace;
import org.tigris.gef.graph.GraphFactory;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;

/**
 *
 * @author penyaskito
 */
public class UMLSequenceDiagram extends UMLDiagram {
    
    private Object[] actions;
    
    /**
     * Logger.
     */
    private static final Logger LOG = Logger
        .getLogger(UMLSequenceDiagram.class);
    
    /**
     * 
     */
    public UMLSequenceDiagram() {
        super();
        // Create the graph model
        MutableGraphModel gm = new SequenceDiagramGraphModel(); 
        setGraphModel(gm);
        
        // Create the layer
        LayerPerspective lay = new
            LayerPerspectiveMutable(this.getName(), gm);
        setLayer(lay);
        
        // Create the renderer
        SequenceDiagramRenderer renderer = new SequenceDiagramRenderer();
        lay.setGraphNodeRenderer(renderer);
        lay.setGraphEdgeRenderer(renderer);
        
        LOG.debug("Created sequence diagram");
    }
    
    /**
     * Creates a new UmlSequenceDiagram with a collaboration.
     * @param collaboration The collaboration
     * 
     */
    public UMLSequenceDiagram(Object collaboration) {
        this();
        try {
            this.setName(getNewDiagramName());
        } catch (PropertyVetoException e) {
            LOG.error("Exception", e);
        }
        ((SequenceDiagramGraphModel) getGraphModel()).
            setCollaboration(collaboration);
        setNamespace(collaboration);
    }
    
    /**
     * Get the Uml actions that can be performed in the diagram 
     * @return An array with the Uml actions 
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getUmlActions()
     */
    @Override
    protected Object[] getUmlActions() {
        if (actions == null) {
            actions = new Object[7];
            actions[0] = new RadioAction(new ActionAddClassifierRole());        
            actions[1] = new RadioAction(new ActionSetAddMessageMode(
                    Model.getMetaTypes().getCallAction(),
                    "button.new-callaction"));
            actions[2] = new RadioAction(new ActionSetAddMessageMode(
                    Model.getMetaTypes().getReturnAction(),
                    "button.new-returnaction"));
            actions[3] = new RadioAction(new ActionSetAddMessageMode(
                    Model.getMetaTypes().getCreateAction(),
                    "button.new-createaction"));
            actions[4] = new RadioAction(new ActionSetAddMessageMode(
                    Model.getMetaTypes().getDestroyAction(),
                    "button.new-destroyaction"));
            actions[5] = new RadioAction(new ActionSetMode(
                    ModeBroomMessages.class,
                    "button.broom-messages"));
        }
        return actions;
    }
    
    /**
     * Get the localized label name for the diagram
     * @return The localized label name for the diagram
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getLabelName()
     */
    @Override
    public String getLabelName() {
        return Translator.localize("label.sequence-diagram");
    }
    
    @Override
    public void encloserChanged(FigNode enclosed, FigNode oldEncloser,
            FigNode newEncloser) {
    	// Do nothing.        
    }
    
    /**
     * Creates a new diagramname.
     *
     * @return a new unique name.
     */
    protected String getNewDiagramName() {
        String name = getLabelName() + " " + getNextDiagramSerial();
        if (!(ProjectManager.getManager().getCurrentProject()
                .isValidDiagramName(name))) {
            name = getNewDiagramName();
        }
        return name;
    }
    
    @Override
    public boolean isRelocationAllowed(Object base)  {
    	return Model.getFacade().isACollaboration(base);
    }

    @SuppressWarnings("unchecked")
    public Collection getRelocationCandidates(Object root) {
        return 
        Model.getModelManagementHelper().getAllModelElementsOfKindWithModel(
            root, Model.getMetaTypes().getCollaboration());
    }

    @Override
    public boolean relocate(Object base) {
        ((SequenceDiagramGraphModel) getGraphModel())
	    	.setCollaboration(base);
        setNamespace(base);
        damage();
        return true;
    }
    
    /**
     * A sequence diagram can accept all classifiers. It will add them as a new 
     * Classifier Role with that classifier as a base.
     * @param objectToAccept
     * @return
     * @see org.argouml.uml.diagram.ui.UMLDiagram#doesAccept(java.lang.Object)
     */
    @Override
    public boolean doesAccept(Object objectToAccept) {
        if (Model.getFacade().isAClassifier(objectToAccept)) {
            return true;
        } else if (Model.getFacade().isAComment(objectToAccept)) {
        	return true;
        }
        return false;
    }
    
    /**
     * Creates a new Classifier Role with a specified base.
     * @param base
     * @return The new CR
     */
    private Object makeNewCR(Object base) {
        Object node = null;
        Editor ce = Globals.curEditor();
        GraphModel gm = ce.getGraphModel();
        if (gm instanceof SequenceDiagramGraphModel) {
            Object collaboration =
                ((SequenceDiagramGraphModel) gm).getCollaboration();
            node =
                Model.getCollaborationsFactory().buildClassifierRole(
                        collaboration);
          }
        Model.getCollaborationsHelper().addBase(node, base);
        
        return node;
    }
    
    /**
     * Creates the Fig for the CR. Y position will be adjusted to match other 
     * the other CRs.
     * @param classifierRole
     * @param location The position where to put the new fig.
     * @return
     */
    private FigClassifierRole makeNewFigCR(Object classifierRole, 
            Point location) {
        if (classifierRole != null) {
        	FigClassifierRole newCR = new FigClassifierRole(classifierRole);
            
            getGraphModel().getNodes().add(newCR.getOwner());
            
            // Y position of the new CR should match existing CRs Y position
            List nodes = getLayer().getContentsNoEdges();
            int i = 0;
            boolean figClassifierRoleFound = false;
            Fig fig = null;
            while (i < nodes.size() && !figClassifierRoleFound) {
                fig = (Fig) nodes.get(i);
                if (nodes.get(i) instanceof Fig) {
                    if (fig != newCR && fig instanceof FigClassifierRole) {
                        newCR.setY(((Fig) fig).getY());
                        newCR.setHeight(((Fig) fig).getHeight());
                        figClassifierRoleFound = true;
                    }
                }
                i++;
            }
            if (location != null) {
                if (newCR.getY() == 0) {
                    newCR.setY(location.y);
                }
                newCR.setX(location.x);
            }
            return newCR;
        }
        return null;
    }
    
    @Override
    public FigNode drop(Object droppedObject, Point location) {
        FigNode figNode = null;
        
        if (Model.getFacade().isAComment(droppedObject)) {
        	figNode = new FigComment(getGraphModel(), droppedObject);
        } else if (Model.getFacade().isAClassifierRole(droppedObject)) {
        	figNode = makeNewFigCR(droppedObject, location);           
        } else if (Model.getFacade().isAClassifier(droppedObject)){
        	figNode = makeNewFigCR(makeNewCR(droppedObject), location);
        } else if (Model.getFacade().isAComment(droppedObject)) {
            figNode = new FigComment(getGraphModel(), droppedObject);
        }
        
        if (figNode != null) {
            LOG.debug("Dropped object " + droppedObject + " converted to " 
                    + figNode);
        } else {
            LOG.debug("Dropped object NOT added " + droppedObject);
        }
        return figNode;
    }
    
    @Override
    public String getInstructions(Object droppedObject) {
    	if (Model.getFacade().isAClassifierRole(droppedObject)) {
    		return super.getInstructions(droppedObject);
    	} else if (Model.getFacade().isAClassifier(droppedObject)) {
            return Translator.localize(
            		"misc.message.click-on-diagram-to-add-as-cr", 
            		new Object[] {Model.getFacade().toString(droppedObject)});
        }
        return super.getInstructions(droppedObject);
    }
    
    @Override
    public ModePlace getModePlace(GraphFactory gf, String instructions) {
        return new ModePlaceClassifierRole(gf, instructions);
    }
}
