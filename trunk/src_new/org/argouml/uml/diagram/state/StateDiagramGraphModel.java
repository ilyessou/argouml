// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

// File: StateDiagramGraphModel.java
// Classes: StateDiagramGraphModel
// Original Author: your email address here
package org.argouml.uml.diagram.state;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.behavioralelements.statemachines.StateMachinesFactory;
import org.argouml.model.uml.behavioralelements.statemachines.StateMachinesHelper;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

/**
 * This class defines a bridge between the UML meta-model representation of the
 * design and the GraphModel interface used by GEF. This class handles only UML
 * MState Digrams.
 */

public class StateDiagramGraphModel extends UMLMutableGraphSupport implements
        VetoableChangeListener {

    private static final Logger LOG = 
        Logger.getLogger(StateDiagramGraphModel.class);
    /**
     * The "home" UML model of this diagram, not all ModelElements in
     * this graph are in the home model, but if they are added and
     * don't already have a model, they are placed in the "home
     * model". Also, elements from other models will have their
     * FigNodes add a line to say what their model is.
     */
    private Object namespace;

    /** The statemachine we are diagramming */
    private Object machine;

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @see org.argouml.uml.diagram.UMLMutableGraphSupport#getNamespace()
     */
    public Object getNamespace() {
        return namespace;
    }

    /**
     * @param ns the namespace
     */
    public void setNamespace(Object ns) {

        if (!ModelFacade.isANamespace(ns))
                throw new IllegalArgumentException();
        namespace = ns;
    }

    /**
     * @return the statemachine of this diagram
     */
    public Object getMachine() {
        return machine;
    }

    /**
     * @param sm   the statemachine of this diagram
     */
    public void setMachine(Object sm) {

        if (!ModelFacade.isAStateMachine(sm))
                throw new IllegalArgumentException();

        if (sm != null) {
            machine = sm;
        }
    }

    ////////////////////////////////////////////////////////////////
    // GraphModel implementation

    /**
     * Return all ports on node or edge.
     *
     * @return The ports.
     * @param nodeOrEdge The node or the edge.
     */
    public Vector getPorts(Object nodeOrEdge) {
        Vector res = new Vector(); //wasteful!
        if (ModelFacade.isAState(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
        if (ModelFacade.isAPseudostate(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
        return res;
    }

    /**
     * Return the node or edge that owns the given port.
     *
     * @param port the port
     * @return The owner of the port.
     * @see org.tigris.gef.graph.BaseGraphModel#getOwner(java.lang.Object)
     */
    public Object getOwner(Object port) {
        return port;
    }

    /** Return all edges going to given port 
     * 
     * @see org.tigris.gef.graph.GraphModel#getInEdges(java.lang.Object)
     */
    public Vector getInEdges(Object port) {
        if (ModelFacade.isAStateVertex(port)) {
	    return new Vector(ModelFacade.getIncomings(port));
	}
        LOG.debug("TODO: getInEdges of MState");
        return new Vector(); //wasteful!
    }

    /** Return all edges going from given port
     * 
     * @see org.tigris.gef.graph.GraphModel#getOutEdges(java.lang.Object)
     */
    public Vector getOutEdges(Object port) {
        if (ModelFacade.isAStateVertex(port)) {
	    return new Vector(ModelFacade.getOutgoings(port));
	}
        LOG.debug("TODO: getOutEdges of MState");
        return new Vector(); //wasteful!
    }

    /** Return one end of an edge
     * 
     * @see org.tigris.gef.graph.BaseGraphModel#getSourcePort(java.lang.Object)
     */
    public Object getSourcePort(Object edge) {
        if (ModelFacade.isATransition(edge)) {
	    return StateMachinesHelper.getHelper()
		.getSource(/* (MTransition) */edge);
	}
        LOG.debug("TODO: getSourcePort of MTransition");
        return null;
    }

    /** Return the other end of an edge 
     *
     * @see org.tigris.gef.graph.BaseGraphModel#getDestPort(java.lang.Object)
     */
    public Object getDestPort(Object edge) {
        if (ModelFacade.isATransition(edge)) {
	    return StateMachinesHelper.getHelper()
		.getDestination(/* (MTransition) */edge);
	}
        LOG.debug("TODO: getDestPort of MTransition");
        return null;
    }

    ////////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    /** Return true if the given object is a valid node in this graph
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#canAddNode(java.lang.Object)
     */
    public boolean canAddNode(Object node) {
        if (node == null) return false;
        if (_nodes.contains(node)) return false;
        return (ModelFacade.isAStateVertex(node) 
                || ModelFacade.isAPartition(node) 
                || ModelFacade.isAComment(node));
    }

    /** Return true if the given object is a valid edge in this graph
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#canAddEdge(java.lang.Object)
     */
    public boolean canAddEdge(Object edge) {
        if (super.canAddEdge(edge)) {
            return true;
        }
        if (edge == null) return false;
        if (_edges.contains(edge)) return false;
        Object end0 = null, end1 = null, state = null;

        if (ModelFacade.isATransition(edge)) {
            state = ModelFacade.getState(edge);
            end0 = ModelFacade.getSource(edge);
            end1 = ModelFacade.getTarget(edge);
            // it's not allowed to directly draw a transition 
            // from a composite state to one of it's substates.
            if (ModelFacade.isACompositeState(end0) 
                    && StateMachinesHelper.getHelper().getAllSubStates(end0)
                                                        .contains(end1)) {
                return false;
            }
        }

        if (end0 == null || end1 == null) return false;
        // if all states are equal it is an internal transition
        if ((state == end0) && (state == end1)) return false;
        if (!_nodes.contains(end0)) return false;
        if (!_nodes.contains(end1)) return false;
        
        return true;
    }

    /** Add the given node to the graph, if valid.
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#addNode(java.lang.Object)
     */
    public void addNode(Object node) {
        LOG.debug("adding statechart diagram node: " + node);
        if (!canAddNode(node)) return;
        if (!(ModelFacade.isAStateVertex(node))) {
            LOG.error("internal error: got past canAddNode");
            return;
        }
        Object sv = /* (MStateVertex) */node;

        if (_nodes.contains(sv)) return;
        _nodes.addElement(sv);
        // TODO: assumes public, user pref for default visibility?
        //if (sv.getNamespace() == null)
        //_namespace.addOwnedElement(sv);
        // TODO: assumes not nested in another composite state
        Object top = /* (MCompositeState) */StateMachinesHelper.getHelper()
                .getTop(getMachine());

        ModelFacade.addSubvertex(top, sv);
        //       sv.setParent(top); this is done in setEnclosingFig!!
        //      if ((sv instanceof MState) &&
        //      (sv.getNamespace()==null))
        //      ((MState)sv).setStateMachine(_machine);
        fireNodeAdded(node);
    }

    /** Add the given edge to the graph, if valid.     
     * @see org.tigris.gef.graph.MutableGraphModel#addEdge(java.lang.Object)
     */
    public void addEdge(Object edge) {
        LOG.debug("adding statechart diagram edge!!!!!!");

        if (!canAddEdge(edge)) return;
        Object tr = /* (MTransition) */edge;
        _edges.addElement(tr);
        fireEdgeAdded(edge);
    }

    /**
     * @see org.tigris.gef.graph.MutableGraphModel#addNodeRelatedEdges(java.lang.Object)
     */
    public void addNodeRelatedEdges(Object node) {
        if (ModelFacade.isAStateVertex(node)) {
            Vector transen = new Vector(ModelFacade.getOutgoings(node));
            transen.addAll(ModelFacade.getIncomings(node));
            Iterator iter = transen.iterator();
            while (iter.hasNext()) {
                Object dep = /* (MTransition) */iter.next();
                if (canAddEdge(dep)) addEdge(dep);
            }
        }
    }

    /**
     * Return true if the two given ports can be connected by a kind of edge to
     * be determined by the ports.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#canConnect(java.lang.Object, java.lang.Object)
     */
    public boolean canConnect(Object fromPort, Object toPort) {
        if (!(ModelFacade.isAStateVertex(fromPort))) {
            LOG.error("internal error not from sv");
            return false;
        }
        if (!(ModelFacade.isAStateVertex(toPort))) {
            LOG.error("internal error not to sv");
            return false;
        }

        if (ModelFacade.isAFinalState(fromPort)) { return false; }
        if (ModelFacade.isAPseudostate(toPort)) {
            if ((ModelFacade.INITIAL_PSEUDOSTATEKIND).equals(ModelFacade
                    .getKind(toPort))) { return false; }
        }        
        return true;
    }

    /** Contruct and add a new edge of the given kind
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#connect(java.lang.Object, java.lang.Object, java.lang.Class)
     */
    public Object connect(Object fromPort, Object toPort,
			  Class edgeClass) {

        if (ModelFacade.isAFinalState(fromPort)) {
	    return null;
	}
        if (ModelFacade.isAPseudostate(toPort)) {
	    if ((ModelFacade.INITIAL_PSEUDOSTATEKIND).equals(
			ModelFacade.getKind(toPort))) {
		return null;
	    }
	}

        if (edgeClass == (Class) ModelFacade.TRANSITION) {
            Object tr = null;
            Object comp = ModelFacade.getContainer(fromPort);
            tr = StateMachinesFactory.getFactory()
                    .buildTransition(fromPort, toPort);
            if (canAddEdge(tr)) {
                addEdge(tr);
            } else {
                ProjectManager.getManager().getCurrentProject().moveToTrash(tr);
                tr = null;
            }
            return tr;
        } else
            if (edgeClass == CommentEdge.class) {
                try {
                    Object connection = UmlFactory.getFactory()
                        .buildConnection(edgeClass, fromPort, null, 
                                                toPort, null, null);
                    addEdge(connection);
                    return connection;
                }
                catch (Exception ex) {
                    // fail silently                
                }
                return null;
            }
            
            else {
                LOG.debug("wrong kind of edge in StateDiagram connect3 "
                        + edgeClass);
                return null;
            }
    }

    ////////////////////////////////////////////////////////////////
    // VetoableChangeListener implementation

    /**
     * @see java.beans.VetoableChangeListener#vetoableChange(java.beans.PropertyChangeEvent)
     */
    public void vetoableChange(PropertyChangeEvent pce) {
        //throws PropertyVetoException

        if ("ownedElement".equals(pce.getPropertyName())) {
            Vector oldOwned = (Vector) pce.getOldValue();
            Object eo = /* (MElementImport) */pce.getNewValue();
            Object me = ModelFacade.getModelElement(eo);
            if (oldOwned.contains(eo)) {
                LOG.debug("model removed " + me);
                if (ModelFacade.isAState(me)) removeNode(me);
                if (ModelFacade.isAPseudostate(me)) removeNode(me);
                if (ModelFacade.isATransition(me)) removeEdge(me);
            } else {
                LOG.debug("model added " + me);
            }
        }
    }

    static final long serialVersionUID = -8056507319026044174L;

    /**
     * @param newNode
     *            this is the new node that one of the ends is dragged to.
     * @param oldNode
     *            this is the existing node that is already connected.
     * @param edge
     *            this is the edge that is being dragged/rerouted
     * @return true if a transition is being rerouted between two states.
     */
    public boolean canChangeConnectedNode(Object newNode, Object oldNode,
            Object edge) {
        // prevent no changes...
        if (newNode == oldNode) return false;

        // check parameter types:
        if (!(ModelFacade.isAState(newNode)
	      || ModelFacade.isAState(oldNode)
	      || ModelFacade.isATransition(edge))) {
	    return false;
	}

        return true;
    }

    /**
     * Reroutes the connection to the old node to be connected to the new node.
     * 
     * @param newNode
     *            this is the new node that one of the ends is dragged to.
     * @param oldNode
     *            this is the existing node that is already connected.
     * @param edge
     *            this is the edge that is being dragged/rerouted
     * @param isSource
     *            tells us which end is being rerouted.
     */
    public void changeConnectedNode(Object newNode, Object oldNode,
            Object edge, boolean isSource) {

        if (isSource)
            ModelFacade.setSource(edge, newNode);
        else
            ModelFacade.setTarget(edge, newNode);

    }

} /* end class StateDiagramGraphModel */
