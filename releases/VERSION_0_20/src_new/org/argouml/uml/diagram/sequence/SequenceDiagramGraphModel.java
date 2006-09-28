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

package org.argouml.uml.diagram.sequence;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.sequence.ui.FigClassifierRole;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Mode;
import org.tigris.gef.base.ModeManager;

/**
 * This class defines a bridge between the UML meta-model
 * representation of the design and the GraphModel interface used by
 * GEF.  This class handles only UML Sequence Digrams.
 *
 * @author 5eichler@informatik.uni-hamburg.de
 */
public class SequenceDiagramGraphModel
    extends UMLMutableGraphSupport
    implements VetoableChangeListener {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(SequenceDiagramGraphModel.class);

    ////////////////////////////////////////////////////////////////
    // instance variables

        /**
     * The collaboration this sequence diagram belongs too.
         */
    private Object collaboration;

        /**
     * The interaction that is shown on the sequence diagram.
         */
    private Object interaction;

        /**
     * State for actions in sequence diagram
        */
    private Object default_state;

    /**
     * State machine for default state
     */
    private Object default_state_machine;

    ////////////////////////////////////////////////////////////////
    // GraphModel implementation

    /**
     * Default constructor. Constructs a model and a collaboration in
     * the root of the current project.
     *
     * @param c the collaboration
     */
    public SequenceDiagramGraphModel() {
    }

    /**
     * Return all ports on node or edge.
     *
     * @see org.tigris.gef.graph.GraphModel#getPorts(java.lang.Object)
     */
    public List getPorts(Object nodeOrEdge) {
        Vector ports = new Vector();
        if (Model.getFacade().isAClassifierRole(nodeOrEdge)) {
            ports.addAll(Model.getFacade().getMessages1(nodeOrEdge));
            ports.addAll( Model.getFacade().getMessages2(nodeOrEdge));
        } else if (Model.getFacade().isAMessage(nodeOrEdge)) {
            ports.add( Model.getFacade().getSender( nodeOrEdge));
            ports.add( Model.getFacade().getReceiver( nodeOrEdge));
        }
        return ports;
    }

    /**
     * Return the node or edge that owns the given port.
     *
     * @see org.tigris.gef.graph.BaseGraphModel#getOwner(java.lang.Object)
     */
    public Object getOwner(Object port) {
        return port;
    }

    /**
     * Return all edges going to given port.
     *
     * @see org.tigris.gef.graph.GraphModel#getInEdges(java.lang.Object)
     */
    public List getInEdges(Object port) {
        Vector res = new Vector();
        if (Model.getFacade().isAClassifierRole(port)) {
            res.addAll(Model.getFacade().getMessages2(port));
        }
        return res;
    }

    /**
     * Return all edges going from given port.
     *
     * @see org.tigris.gef.graph.GraphModel#getOutEdges(java.lang.Object)
     */
    public List getOutEdges(Object port) {
        Vector res = new Vector();
        if (Model.getFacade().isAClassifierRole(port)) {
           res.addAll(Model.getFacade().getMessages1(port));
        }
        return res;
    }

    ////////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    /**
     * Return true if the given object is a valid node in this graph.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#canAddNode(java.lang.Object)
     */
    public boolean canAddNode(Object node) {
        if (node == null) {
            return false;
        }
        return ! getNodes().contains( node)
          && Model.getFacade().getNamespace( node)==getCollaboration();
    }

    /**
     * Return true if the given object is a valid edge in this graph.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#canAddEdge(java.lang.Object)
     */
    public boolean canAddEdge(Object edge) {
        if (edge == null) {
            return false;
        }
        
        if ( getEdges().contains( edge)) {
            return false;
        }
        
        Object end0 = null;
        Object end1 = null;

        if (Model.getFacade().isAMessage(edge)) {
            end0 = Model.getFacade().getSender( edge);
            end1 = Model.getFacade().getReceiver( edge);
        } else if (edge instanceof CommentEdge) {
            end0 = ((CommentEdge) edge).getSource();
            end1 = ((CommentEdge) edge).getDestination();
        }
        // Both ends must be defined and nodes that are on the graph already.
        if (end0 == null || end1 == null) {
            LOG.error("Edge rejected. Its ends are not attached to anything");
            return false;
        }
        
        if (!containsNode(end0) && !containsEdge(end0)) {
            LOG.error("Edge rejected. Its source end is attached to " +
                    end0 +
                    " but this is not in the graph model");
            return false;
        }
        if (!containsNode(end1) && !containsEdge(end1)) {
            LOG.error("Edge rejected. Its destination end is attached to " +
                    end1 +
                    " but this is not in the graph model");
            return false;
        }

        System.out.println("We can add " + edge);
        return true;
    }

    /**
     * Add the given node to the graph, if valid.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#addNode(java.lang.Object)
     */
    public void addNode(Object node) {
        if (canAddNode(node)) {
            getNodes().add( node);
            fireNodeAdded(node);
        }

    }

    /**
     * Adds an edge to the model if this is allowed. If the edge is a link,
     * an associationrole and a stimulus to accompany this link are build.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#addEdge(java.lang.Object)
     */
    public void addEdge(Object edge) {
        if (canAddEdge(edge)) {
            getEdges().add( edge);
        fireEdgeAdded(edge);
        }
    }

    /**
     * @see org.tigris.gef.graph.MutableGraphModel#addNodeRelatedEdges(java.lang.Object)
     */
    public void addNodeRelatedEdges(Object node) {
        super.addNodeRelatedEdges(node);

        if (Model.getFacade().isAClassifierRole(node)) {
            Collection ends = Model.getFacade().getMessages2(node);
            Iterator iter = ends.iterator();
            while (iter.hasNext()) {
                addEdge( iter.next());
                }
            iter=Model.getFacade().getMessages1(node).iterator();
            while (iter.hasNext()) {
                addEdge( iter.next());
            }
        }
    }

    /**
     * Return true if the two given ports can be connected by a
     * kind of edge to be determined by the ports.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#canConnect(
     * java.lang.Object, java.lang.Object)
     */
    public boolean canConnect(Object fromP, Object toP, Object edgeType) {
        
        if (edgeType == CommentEdge.class && 
                (Model.getFacade().isAComment(fromP) || 
                        Model.getFacade().isAComment(toP)) &&
                !(Model.getFacade().isAComment(fromP) &&
                        Model.getFacade().isAComment(toP))) {
            // We can connect if we get a comment edge and one (only one) node
            // that is a comment.
            return true;
        }
        
        
        if ( ! ( fromP instanceof MessageNode) || ! ( toP instanceof MessageNode))
            return false;
        if ( fromP==toP)
            return false;

        MessageNode nodeFrom=(MessageNode)fromP;
        MessageNode nodeTo=(MessageNode)toP;

        if ( nodeFrom.getFigClassifierRole()==nodeTo.getFigClassifierRole())
        {
            FigClassifierRole fig=nodeFrom.getFigClassifierRole();
            if ( fig.getIndexOf( nodeFrom)>=fig.getIndexOf( nodeTo))
                return false;
        }

        Editor curEditor = Globals.curEditor();
        ModeManager modeManager = curEditor.getModeManager();
        Mode mode = (Mode) modeManager.top();
        Hashtable args = mode.getArgs();
        Object actionType = args.get("action");
        if (Model.getMetaTypes().getCallAction().equals(actionType)) {
            return nodeFrom.canCall() && nodeTo.canBeCalled();
        } else if (Model.getMetaTypes().getReturnAction().equals(actionType)) {
            return nodeTo.canBeReturnedTo() && nodeFrom.canReturn( nodeTo.getClassifierRole());
        } else if (Model.getMetaTypes().getCreateAction().equals(actionType)) {
            if ( nodeFrom.getFigClassifierRole()==nodeTo.getFigClassifierRole())
                return false;
            return nodeFrom.canCreate() && nodeTo.canBeCreated();
        } else if (Model.getMetaTypes().getDestroyAction().equals(actionType)) {
            return nodeFrom.canDestroy() && nodeTo.canBeDestroyed();
        }
        // not supported action
        return false;
    }

    /**
     * Creates a link based on the given from and toPort. The fromPort
     * should allways point to a MessageCoordinates instance. The toPort
     * can point to a MessageCoordinates instance or to a Object
     * instance. On a sequence diagram you can only draw Messages. So
     * other edgeClasses then links are not supported.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#connect(
     *          Object, Object, Class)
     */
    public Object connect(Object fromPort, Object toPort, Object edgeType) {
        if (!canConnect(fromPort, toPort, edgeType)) {
            return null;
        }
        if (edgeType == CommentEdge.class) {
            return super.connect(fromPort, toPort, (Object)edgeType);
        }
        Object edge = null;
        Object fromObject = null;
        Object toObject = null;
        Object action = null;
        if (Model.getMetaTypes().getMessage().equals(edgeType)) {
            Editor curEditor = Globals.curEditor();
            ModeManager modeManager = curEditor.getModeManager();
            Mode mode = modeManager.top();
            Hashtable args = mode.getArgs();
            Object actionType = args.get("action");
            if (Model.getMetaTypes().getCallAction().equals(actionType)) {
                if (fromPort instanceof MessageNode
                    && toPort instanceof MessageNode) {
                    fromObject = ((MessageNode) fromPort).getClassifierRole();
                    toObject = ((MessageNode) toPort).getClassifierRole();

                    action =
                        Model.getCommonBehaviorFactory()
                            .createCallAction();
                }
            } else if (Model.getMetaTypes().getCreateAction()
                    .equals(actionType)) {
                if (fromPort instanceof MessageNode
                    && toPort instanceof MessageNode) {
                    fromObject = ((MessageNode) fromPort).getClassifierRole();
                    toObject = ((MessageNode)toPort).getClassifierRole();
                    action =
                        Model.getCommonBehaviorFactory()
                            .createCreateAction();
                }
            } else if (Model.getMetaTypes().getReturnAction()
                    .equals(actionType)) {
                if (fromPort instanceof MessageNode
                    && toPort instanceof MessageNode) {
                    fromObject = ((MessageNode) fromPort).getClassifierRole();
                    toObject = ((MessageNode) toPort).getClassifierRole();
                    action =
                        Model.getCommonBehaviorFactory()
                            .createReturnAction();

                }
            } else if (Model.getMetaTypes().getDestroyAction()
                    .equals(actionType)) {
                if (fromPort instanceof MessageNode
                    && toPort instanceof MessageNode) {
                    fromObject = ((MessageNode) fromPort).getClassifierRole();
                    toObject = ((MessageNode) fromPort).getClassifierRole();
                    action =
                        Model.getCommonBehaviorFactory()
                            .createDestroyAction();
                }
            } else if (Model.getMetaTypes().getSendAction()
                    .equals(actionType)) {
                ;// no implementation, not of importance to sequence diagrams
            } else if (Model.getMetaTypes().getTerminateAction()
                    .equals(actionType)) {
                ;// not implemented yet
            }
        }
        if (fromObject != null && toObject != null && action != null) {
            Object associationRole =
                Model.getCollaborationsHelper().getAssocationRole(
                    fromObject,
                    toObject);
            if (associationRole == null) {
                associationRole =
                    Model.getCollaborationsFactory().buildAssociationRole(
                    fromObject, toObject);
            }

            Object message =
                Model.getCollaborationsFactory().buildMessage(
                    getInteraction(),
                    associationRole);
            if ( action!=null)
            {
            Model.getCollaborationsHelper().setAction(message, action);
                Model.getStateMachinesHelper().setDoActivity(
                    Model.getStateMachinesFactory().buildSimpleState( getDefaultState()),
                    action);
            }
            Model.getCollaborationsHelper()
                .setSender(message, fromObject);
            Model.getCommonBehaviorHelper()
                .setReceiver(message, toObject);

            addEdge(message);
            edge = message;
        }
        if (edge == null) {
            LOG.debug("Incorrect edge");
        }
        return edge;

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
            Object eo = /*(MElementImport)*/ pce.getNewValue();
            Object me = Model.getFacade().getModelElement(eo);
            if (oldOwned.contains(eo)) {
                LOG.debug("model removed " + me);
                if (Model.getFacade().isAClassifierRole(me)) {
                    removeNode(me);
                }
                if (Model.getFacade().isAMessage(me)) {
                    removeEdge(me);
                }
            } else {
                LOG.debug("model added " + me);
            }
        }
    }

    /**
     * Gets the collaboration that is shown on the sequence diagram.<p>
     *
     * @return the collaboration of the diagram.
     */
    public Object getCollaboration() {
        if ( collaboration==null){
            collaboration = Model.getCollaborationsFactory().buildCollaboration(
                ProjectManager.getManager().getCurrentProject()
                .getRoot());
        }

        return collaboration;
    }

    /**
     * Sets the collaboration that is shown at the sequence diagram.
     *
     * @param c the collaboration
     */
    public void setCollaboration(Object c) {
        collaboration = c;
        Collection interactions=Model.getFacade().getInteractions( c);
        if ( ! interactions.isEmpty())
            interaction=interactions.iterator().next();
    }

    private Object getInteraction() {
        if (interaction == null) {
            interaction =
                Model.getCollaborationsFactory().buildInteraction(
                    collaboration);
        }
        return interaction;
    }

    private Object getDefaultStateMachine() {
        if ( default_state_machine==null)
        {
            Object ns = Model.getFacade().getRepresentedClassifier( getCollaboration());
            Iterator it = Model.getFacade().getOwnedElements( ns).iterator();
            while (it.hasNext()) {
                Object child = it.next();
                if (Model.getFacade().isAStateMachine(child)) {
                    default_state_machine = child;
                    break;
                }
            }
            if (default_state_machine == null)
            {
                default_state_machine = Model.getStateMachinesFactory().
                    buildStateMachine(
                    ns);
                Model.getStateMachinesFactory().buildCompositeStateOnStateMachine(
                    default_state_machine);
            }
        }
        return default_state_machine;
    }

    private Object getDefaultState() {
        if ( default_state==null)
        {
            default_state=Model.getStateMachinesHelper().getTop(
                getDefaultStateMachine());
        }
        return default_state;
    }

    /**
     * @see org.argouml.uml.diagram.UMLMutableGraphSupport#getHomeModel()
     */
    public Object getHomeModel() {
        return getCollaboration();
    }


    public void setHomeModel(Object namespace) {
        if (!Model.getFacade().isANamespace(namespace))
            throw new IllegalArgumentException(
                    "A sequence diagram home model must be a namespace, received a "
                    + namespace);
        setCollaboration( namespace);
        super.setHomeModel( namespace);
    }

}
