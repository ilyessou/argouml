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

package org.argouml.ui;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.argouml.cognitive.ItemUID;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.diagram.static_structure.ui.FigComment;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.graph.MutableGraphSupport;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 * This class represents all Diagrams within ArgoUML.
 * It is based upon the GEF Diagram.
 */
public class ArgoDiagram extends Diagram {

    private ItemUID id;

    static {
        /**
         * Hack to use vetocheck in constructing names.
         *
         * TODO: Is this needed?
         */
        new ArgoDiagram();
    }

    /**
     * The constructor.
     */
    public ArgoDiagram() {
        super();
        // really dirty hack to remove unwanted listeners
        getLayer().getGraphModel().removeGraphEventListener(getLayer());
    }

    /**
     * The constructor.
     *
     * @param diagramName the name of the diagram
     */
    public ArgoDiagram(String diagramName) {
        // next line patch to issue 596 (hopefully)
        super(diagramName);
        try {
            setName(diagramName);
        } catch (PropertyVetoException pve) { }
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @see org.tigris.gef.base.Diagram#setName(java.lang.String)
     */
    public void setName(String n) throws PropertyVetoException {
        super.setName(n);
        MutableGraphSupport.enableSaveAction();
    }

    /**
     * @param i the new id
     */
    public void setItemUID(ItemUID i) {
        id = i;
    }

    /**
     * USED BY pgml.tee!!
     * @return the item UID
     */
    public ItemUID getItemUID() {
        return id;
    }

    ////////////////////////////////////////////////////////////////
    // event management
    /**
     * The UID.
     */
    static final long serialVersionUID = -401219134410459387L;

    /**
     * TODO: The reference to the method
     * org.argouml.uml.ui.VetoablePropertyChange#getVetoMessage(String)
     * was here but the class does exist anymore. Where is it?
     * This method is never used!
     *
     * @param propertyName is the name of the property
     * @return a message or null if not applicable.
     */
    public String getVetoMessage(String propertyName) {
    	if (propertyName.equals("name")) {
	    return "Name of diagram may not exist already";
    	}
        return null;
    }

    /**
     * Finds the presentation (the Fig) for some object. If the object
     * is a modelelement that is contained in some other modelelement
     * that has its own fig, that fig is returned. It extends
     * presentationFor that only gets the fig belonging to the node
     * obj.<p>
     *
     * @author jaap.branderhorst@xs4all.nl
     * @return the Fig for the object
     * @param obj is th object
     */
    public Fig getContainingFig(Object obj) {
        Fig fig = super.presentationFor(obj);
        if (fig == null && Model.getFacade().isAModelElement(obj)) {
	    // maybe we have a modelelement that is part of some other
            // fig
            if (Model.getFacade().isAOperation(obj)
		|| Model.getFacade().isAAttribute(obj)) {

                // get all the classes from the diagram
                return presentationFor(Model.getFacade().getOwner(obj));
            }
        }
        return fig;
    }

    /**
     * @see org.tigris.gef.base.Diagram#initialize(Object)
     */
    public void initialize(Object owner) {
        super.initialize(owner);
        ProjectManager.getManager().getCurrentProject().setActiveDiagram(this);
    }

    /**
     * This will mark the entire visible area of all Editors to be repaired
     *  from any damage - i.e. repainted.
     */
    public void damage() {
        if (getLayer() != null && getLayer().getEditors() != null) {
            Iterator it = getLayer().getEditors().iterator();
            while (it.hasNext()) {
                ((Editor) it.next()).damageAll();
            }
        }
    }

    /**
     * Get all the model elements in this diagram that are represented
     * by a FigEdge
     * @see Diagram#getEdges()
     */
    public List getEdges() {
        if (getGraphModel() != null) {
            return getGraphModel().getEdges();
        }
        return super.getEdges();
    }
    
    /**
     * @see Diagram#getEdges(Collection)
     * TODO: This method can be deleted after GEF 0.11.3M6
     */
    public Collection getEdges(Collection c) {
        if (getGraphModel() != null) {
            return getGraphModel().getEdges();
        }
        return getEdges();
    }

    /**
     * Get all the model elements in this diagram that are represented
     * by a FigNode
     * @see Diagram#getNodes()
     */
    public List getNodes() {
        if (getGraphModel() != null) {
            return getGraphModel().getNodes();
        }
        return super.getNodes();
    }
    
    /**
     * @see Diagram#getEdges(Collection)
     * TODO: This method can be deleted after GEF 0.11.3M6
     */
    public Collection getNodes(Collection c) {
        if (getGraphModel() != null) {
            return getGraphModel().getNodes();
        }
        return getNodes();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Diagram: " + getName();
    }
    
    /**
     * We hang our heads in shame. There are still bugs in ArgoUML
     * and/or GEF that cause corruptions in the model.
     * Before a save takes place we repair the model in order to
     * be as certain as possible that the saved file will reload.
     */
    public String repair() {
        String report = "";
        
        List figs = new ArrayList(getLayer().getContents());
        for (Iterator i = figs.iterator(); i.hasNext(); ) {
            Fig f = (Fig)i.next();
            
            // 1. Make sure all Figs in the Diagrams layer refer back to
            // that layer.
            if (!getLayer().equals(f.getLayer())) {
                // The report
                if (f.getLayer() == null) {
                    report += "Fixed: " + figDescription(f) + " layer was null\n";
                } else {
                    report += "Fixed: " + figDescription(f) + " refered to wrong layer\n";
                }
                // The fix
                f.setLayer(getLayer());
            }
            
            // 2. Make sure all FigNodes and FigEdges have an valid owner
            if (f instanceof FigNode || f instanceof FigEdge) {
                // The report
                Object owner = f.getOwner();
                if (owner == null) {
                    report += "Removed: " + figDescription(f) + " owner was null\n";
                } else if (Model.getUmlFactory().isRemoved(owner)) {
                    report += "Removed: " + figDescription(f) + " model element no longer in the repository\n";
                }
                // The fix
                f.removeFromDiagram();
            }
            
        }
        
        return report;
    }
    
    private String figDescription(Fig f) {
        String description = f.getClass().getName();
        if (f instanceof FigComment) {
            description += ((FigComment)f).getBody();
        } else if (f instanceof FigNodeModelElement) {
            description += ((FigNodeModelElement)f).getName();
        } else if (f instanceof FigEdgeModelElement) {
            description += ((FigEdgeModelElement)f).getName();
        }
        return description;
    }

} /* end class ArgoDiagram */
