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

package org.argouml.uml.diagram.static_structure;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

/**
 * This class defines a bridge between the UML meta-model
 * representation of the design and the GraphModel interface used by
 * GEF.  This class handles only UML Class digrams.
 *
 * @author jrobbins
 */
public class ClassDiagramGraphModel extends UMLMutableGraphSupport
    implements VetoableChangeListener {
    /**
     * Logger.
     */
    private static final Logger LOG =
	Logger.getLogger(ClassDiagramGraphModel.class);

    ////////////////////////////////////////////////////////////////
    // GraphModel implementation

    /**
     * @see org.tigris.gef.graph.GraphModel#getPorts(java.lang.Object)
     *
     * Return all ports on node or edge.
     */
    public List getPorts(Object nodeOrEdge) {
	Vector res = new Vector();  // wasteful!
	if (Model.getFacade().isAClass(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
	if (Model.getFacade().isAInterface(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
	if (Model.getFacade().isAInstance(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
	if (Model.getFacade().isAModel(nodeOrEdge)) {
	    res.addElement(nodeOrEdge);
	}
	return res;
    }

    /**
     * @see org.tigris.gef.graph.BaseGraphModel#getOwner(java.lang.Object)
     *
     * Return the node or edge that owns the given port.
     */
    public Object getOwner(Object port) {
	return port;
    }

    /**
     * @see org.tigris.gef.graph.GraphModel#getInEdges(java.lang.Object)
     *
     * Return all edges going to given port (read Model Element).
     *
     * Instances can't currently be added to a class diagram.
     */
    public List getInEdges(Object port) {

	Vector edges = new Vector();

	// top of the hierarchy is ME:
	if (Model.getFacade().isAModelElement(port)) {
	    Iterator it =
	        Model.getFacade().getSupplierDependencies(port).iterator();
	    while (it.hasNext()) {
		edges.add(it.next());
	    }
	}
	// then Generalizable Element
	if (Model.getFacade().isAGeneralizableElement(port)) {
	    Iterator it = Model.getFacade().getSpecializations(port).iterator();
	    while (it.hasNext()) {
		edges.add(it.next());
	    }
	}
	// then Classifier
	if (Model.getFacade().isAClassifier(port)) {
	    Iterator it = Model.getFacade().getAssociationEnds(port).iterator();
	    while (it.hasNext()) {
		Object nextAssocEnd = it.next();
		// navigable.... only want incoming
		if (Model.getFacade().isNavigable(nextAssocEnd)) {
		    edges.add(nextAssocEnd);
		}
	    }
	}

	return edges;

	//    Vector res = new Vector(); //wasteful!
	//    if (port instanceof MClass) {
	//      MClass cls = (MClass) port;
	//      Collection ends = cls.getAssociationEnds();
	//      if (ends == null) return res; // empty Vector
	//      //java.util.Enumeration endEnum = ends.elements();
	//      Iterator iter = ends.iterator();
	//      while (iter.hasNext()) {
	//          MAssociationEnd ae = (MAssociationEnd) iter.next();
	//          res.add(ae.getAssociation());
	//      }
	//    }
	//    if (port instanceof MInterface) {
	//      MInterface Intf = (MInterface) port;
	//      Collection ends = Intf.getAssociationEnds();
	//      if (ends == null) return res; // empty Vector
	//      Iterator endEnum = ends.iterator();
	//      while (endEnum.hasNext()) {
	//        MAssociationEnd ae = (MAssociationEnd) endEnum.next();
	//        res.addElement(ae.getAssociation());
	//      }
	//    }
	//    if (port instanceof MPackage) {
	//      MPackage cls = (MPackage) port;
	//      Vector ends = cls.getAssociationEnd();
	//      if (ends == null) return res; // empty Vector
	//      java.util.Enumeration endEnum = ends.elements();
	//      while (endEnum.hasMoreElements()) {
	//        MAssociationEnd ae = (MAssociationEnd) endEnum.nextElement();
	//        res.addElement(ae.getAssociation());
	//      }
	//    }
	//    if (port instanceof MInstance) {
	//      MInstance inst = (MInstance) port;
	//      Collection ends = inst.getLinkEnds();
	//      res.addAll(ends);
	//    }
	//    return res;
    }

    /**
     * @see org.tigris.gef.graph.GraphModel#getOutEdges(java.lang.Object)
     *
     * Return all edges going from given port (model element).
     */
    public List getOutEdges(Object port) {

	Vector edges = new Vector();

	// top of the hierarchy is ME:
	if (Model.getFacade().isAModelElement(port)) {
	    Iterator it =
	        Model.getFacade().getClientDependencies(port).iterator();
	    while (it.hasNext()) {
		edges.add(it.next());
	    }
	}
	// then Generalizable Element
	if (Model.getFacade().isAGeneralizableElement(port)) {
	    Iterator it = Model.getFacade().getGeneralizations(port).iterator();
	    while (it.hasNext()) {
		edges.add(it.next());
	    }
	}
	// then Classifier
	if (Model.getFacade().isAClassifier(port)) {
	    Iterator it = Model.getFacade().getAssociationEnds(port).iterator();
	    while (it.hasNext()) {
		Object nextAssocEnd =
		    Model.getFacade().getOppositeEnd(it.next());
		// navigable.... only want outgoing
		if (Model.getFacade().isNavigable(nextAssocEnd)) {
		    edges.add(nextAssocEnd);
		}
	    }
	}

	return edges;
    }

    ////////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    /**
     * @see org.tigris.gef.graph.MutableGraphModel#canAddNode(java.lang.Object)
     *
     * Return true if the given object is a valid node in this graph.
     */
    public boolean canAddNode(Object node) {
        if (super.canAddNode(node) && !containsNode(node)) {
            return true;
        }
    	if (containsNode(node)) {
            LOG.error("Addition of node of type " +
                    node.getClass().getName() +
                    " rejected because its already in the graph model");
    	    return false;
    	}
        if (Model.getFacade().isAAssociation(node)) {
            // N.B. A node which is an Association is either a n-ary association
            // or the Class part of an AssociationClass
            Collection ends = Model.getFacade().getConnections(node);
            Iterator iter = ends.iterator();
            while (iter.hasNext()) {
                Object classifier =
                    Model.getFacade().getClassifier(iter.next());
                if (!containsNode(classifier)) {
                    LOG.error("Addition of node of type " +
                            node.getClass().getName() +
                            " rejected because it is connected to a " +
                            "classifier that is not in the diagram");
                    return false;
                }
            }
            return true;
        }

        // TODO: This logic may well be worth moving into the model component.
        // Provide a similar grid to the connectionsGrid
        return Model.getFacade().isAClass(node)
            || Model.getFacade().isAInterface(node)
            || Model.getFacade().isAModel(node)
            || Model.getFacade().isAPackage(node);
    }

    /**
     * @see org.tigris.gef.graph.MutableGraphModel#canAddEdge(java.lang.Object)
     *
     * Return true if the given object is a valid edge in this graph.
     */
    public boolean canAddEdge(Object edge) {
        if (edge == null) {
            return false;
        }
        if (containsEdge(edge)) {
            return false;
        }
        Object sourceModelElement = null;
        Object destModelElement = null;
        if (Model.getFacade().isAAssociation(edge)) {
            Collection conns = Model.getFacade().getConnections(edge);
            if (conns.size() < 2) {
                LOG.error("Association rejected. Must have at least 2 ends");
                return false;
            }
            Iterator iter = conns.iterator();
            Object associationEnd0 = iter.next();
            Object associationEnd1 = iter.next();
            if (associationEnd0 == null || associationEnd1 == null) {
                LOG.error("Association rejected. Both ends are null");
                return false;
            }
            sourceModelElement = Model.getFacade().getType(associationEnd0);
            destModelElement = Model.getFacade().getType(associationEnd1);
        } else if (Model.getFacade().isAAssociationEnd(edge)) {
            sourceModelElement = Model.getFacade().getAssociation(edge);
            destModelElement = Model.getFacade().getType(edge);

            return (sourceModelElement != null
                    && destModelElement != null
                    && (containsEdge(sourceModelElement) || containsNode(sourceModelElement))
                    && containsNode(destModelElement));
        } else if (Model.getFacade().isAGeneralization(edge)) {
            sourceModelElement = Model.getFacade().getChild(edge);
            destModelElement = Model.getFacade().getParent(edge);
        } else if (Model.getFacade().isADependency(edge)) {
            Collection clients = Model.getFacade().getClients(edge);
            Collection suppliers = Model.getFacade().getSuppliers(edge);
            if (clients == null || suppliers == null) {
                return false;
            }
            sourceModelElement = clients.iterator().next();
            destModelElement = suppliers.iterator().next();
        } else if (Model.getFacade().isALink(edge)) {
            Collection roles = Model.getFacade().getConnections(edge);
            if (roles.size() < 2) {
                return false;
            }
            Iterator iter = roles.iterator();
            Object linkEnd0 = iter.next();
            Object linkEnd1 = iter.next();
            if (linkEnd0 == null || linkEnd1 == null) {
                return false;
            }
            sourceModelElement = Model.getFacade().getInstance(linkEnd0);
            destModelElement = Model.getFacade().getInstance(linkEnd1);
        } else if (edge instanceof CommentEdge) {
            sourceModelElement = ((CommentEdge) edge).getSource();
            destModelElement = ((CommentEdge) edge).getDestination();
        }

        if (sourceModelElement == null || destModelElement == null) {
            LOG.error("Edge rejected. Its ends are not attached to anything");
            return false;
        }

        if (!containsNode(sourceModelElement)
                && !containsEdge(sourceModelElement)) {
            LOG.error("Edge rejected. Its source end is attached to " +
                    sourceModelElement +
                    " but this is not in the graph model");
            return false;
        }
        if (!containsNode(destModelElement)
                && !containsEdge(destModelElement)) {
            LOG.error("Edge rejected. Its destination end is attached to " +
                    destModelElement +
                    " but this is not in the graph model");
            return false;
        }

        return true;
    }


    /**
     * @see org.tigris.gef.graph.MutableGraphModel#addNode(java.lang.Object)
     *
     * Add the given node to the graph, if valid.
     */
    public void addNode(Object node) {
	if (!canAddNode(node)) {
	    return;
	}
	getNodes().add(node);
	if (Model.getFacade().isAModelElement(node)
	        && Model.getFacade().getNamespace(node) == null) {
            Model.getCoreHelper().addOwnedElement(getHomeModel(), node);
	}

	fireNodeAdded(node);
    }

    /**
     * Add the given edge to the graph, if of the correct type.
     * Throws IllegalArgumentException if edge is null or either of its
     * ends are null.
     *
     * @see org.tigris.gef.graph.MutableGraphModel#addEdge(java.lang.Object)
     */
    public void addEdge(Object edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Cannot add a null edge");
        }

        if (getDestPort(edge) == null || getSourcePort(edge) == null) {
            throw new IllegalArgumentException(
                    "The source and dest port should be provided on an edge");
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Adding an edge of type "
                   + edge.getClass().getName()
                   + " to class diagram.");
        }

        if (!canAddEdge(edge)) {
            LOG.info("Attempt to add edge rejected");
            return;
        }

        getEdges().add(edge);

        // TODO: assumes public
        if (Model.getFacade().isAModelElement(edge)
                && Model.getFacade().getNamespace(edge) == null
                && !Model.getFacade().isAAssociationEnd(edge)) {
    	    Model.getCoreHelper().addOwnedElement(getHomeModel(), edge);
        }
        fireEdgeAdded(edge);
    }


    /**
     * Adds the edges from the given node. For example, this method lets you add
     * an allready existing massociation between two figclassifiers.
     * @see org.tigris.gef.graph.MutableGraphModel#addNodeRelatedEdges(Object)
     */
    public void addNodeRelatedEdges(Object node) {
        super.addNodeRelatedEdges(node);

        if (Model.getFacade().isAClassifier(node)) {
            Collection ends = Model.getFacade().getAssociationEnds(node);
            Iterator iter = ends.iterator();
            while (iter.hasNext()) {
                Object association =
                        Model.getFacade().getAssociation(iter.next());
                if (!Model.getFacade().isANaryAssociation(association)
                    && canAddEdge(association)) {

                    addEdge(association);
                }
            }
        }
        if (Model.getFacade().isAGeneralizableElement(node)) {
            Collection generalizations =
                Model.getFacade().getGeneralizations(node);
            Iterator iter = generalizations.iterator();
            while (iter.hasNext()) {
        	Object generalization = iter.next();
        	if (canAddEdge(generalization)) {
        	    addEdge(generalization);
        	    // return;
        	}
            }
            Collection specializations =
                Model.getFacade().getSpecializations(node);
            iter = specializations.iterator();
            while (iter.hasNext()) {
        	Object specialization = iter.next();
        	if (canAddEdge(specialization)) {
        	    addEdge(specialization);
        	    // return;
        	}
            }
        }
        if (Model.getFacade().isAModelElement(node)) {
            Vector specs =
        	new Vector(Model.getFacade().getClientDependencies(node));
            specs.addAll(Model.getFacade().getSupplierDependencies(node));
            Iterator iter = specs.iterator();
            while (iter.hasNext()) {
        	Object dependency = iter.next();
        	if (canAddEdge(dependency)) {
        	    addEdge(dependency);
        	    // return;
                }
            }
        }
        if (Model.getFacade().isAAssociation(node)) {
            Collection ends = Model.getFacade().getConnections(node);
            Iterator iter = ends.iterator();
            while (iter.hasNext()) {
                Object associationEnd = iter.next();
                if (canAddEdge(associationEnd)) {
                    addEdge(associationEnd);
        	}
            }
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
	    Object elementImport = /*(MElementImport)*/ pce.getNewValue();
            Object modelElement =
                    Model.getFacade().getModelElement(elementImport);
	    //MModelElement modelElement = elementImport.getModelElement();
	    if (oldOwned.contains(elementImport)) {
		LOG.debug("model removed " + modelElement);
		if (Model.getFacade().isAClassifier(modelElement)) {
		    removeNode(modelElement);
		}
		if (Model.getFacade().isAPackage(modelElement)) {
		    removeNode(modelElement);
		}
		if (Model.getFacade().isAAssociation(modelElement)) {
		    removeEdge(modelElement);
		}
		if (Model.getFacade().isADependency(modelElement)) {
		    removeEdge(modelElement);
		}
		if (Model.getFacade().isAGeneralization(modelElement)) {
		    removeEdge(modelElement);
		}
	    }
	    else {
		LOG.debug("model added " + modelElement);
	    }
	}
    }


    static final long serialVersionUID = -2638688086415040146L;


    /**
     * When rerouting an edge, this is the first method to
     * be called by SelectionRerouteEdge, in order to determine
     * whether the graphmodel will allow the change.
     *
     * <p>restricted to class<->association changes for now.
     *
     * @param newNode this is the new node that one of the ends is dragged to.
     * @param oldNode this is the existing node that is already connected.
     * @param edge this is the edge that is being dragged/rerouted
     *
     * @return whether or not the rerouting is allowed
     */
    public boolean canChangeConnectedNode(Object newNode, Object oldNode,
					  Object edge) {

	// prevent no changes...
	if (newNode == oldNode)
	    return false;

	// check parameter types:
	if (!(Model.getFacade().isAClass(newNode)
	        || Model.getFacade().isAClass(oldNode)
	        || Model.getFacade().isAAssociation(edge))) {
	    return false;
	}

	return true;
    }

    /**
     * Reroutes the connection to the old node to be connected to
     * the new node.
     *
     * delegates to rerouteXXX(,,,) for each of the 4 possible edges in
     * a class diagram: Association, Dependency, Generalization, Link.
     *
     * @param newNode this is the new node that one of the ends is dragged to.
     * @param oldNode this is the existing node that is already connected.
     * @param edge this is the edge that is being dragged/rerouted
     * @param isSource tells us which end is being rerouted.
     */
    public void changeConnectedNode(Object newNode, Object oldNode,
				    Object edge, boolean isSource) {
	if (Model.getFacade().isAAssociation(edge))
	    rerouteAssociation(newNode,  oldNode,  edge,  isSource);
	else if (Model.getFacade().isAGeneralization(edge))
	    rerouteGeneralization(newNode,  oldNode,  edge,  isSource);
	else if (Model.getFacade().isADependency(edge))
	    rerouteDependency(newNode,  oldNode,  edge,  isSource);
	else if (Model.getFacade().isALink(edge))
	    rerouteLink(newNode,  oldNode,  edge,  isSource);
    }

    /**
     * helper method for changeConnectedNode
     */
    private void rerouteAssociation(Object newNode, Object oldNode,
				    Object edge, boolean isSource) {
	// check param types: only some connections are legal uml connections:

	if (!(Model.getFacade().isAClassifier(newNode))
	    || !(Model.getFacade().isAClassifier(oldNode)))
	    return;

	// can't have a connection between 2 interfaces:
	// get the 'other' end type
	Object /*MModelElement*/ otherNode = null;

	if (isSource) {
	    otherNode =
		Model.getCoreHelper().getDestination(/*(MRelationship)*/ edge);
	}
	else {
	    otherNode =
		Model.getCoreHelper().getSource(/*(MRelationship)*/ edge);
	}

	if (Model.getFacade().isAInterface(newNode)
	        && Model.getFacade().isAInterface(otherNode))
	    return;

        // cast the params
	Object /*MAssociation*/ edgeAssoc = edge;

	Object theEnd = null;
	Object theOtherEnd = null;
        Collection connections = Model.getFacade().getConnections(edgeAssoc);
        Iterator iter = connections.iterator();
        if (isSource) {
            // rerouting the source:
            theEnd = iter.next();
            theOtherEnd = iter.next();
        } else {
            // rerouting the destination:
            theOtherEnd = iter.next();
            theEnd = iter.next();
        }

        // set the ends navigability see also Class ActionNavigability
        if (Model.getFacade().isAInterface(newNode)) {
            Model.getCoreHelper().setNavigable(theEnd, true);
            Model.getCoreHelper().setNavigable(theOtherEnd, false);
        }

        if (Model.getFacade().isAInterface(otherNode)) {
            Model.getCoreHelper().setNavigable(theOtherEnd, true);
            Model.getCoreHelper().setNavigable(theEnd, false);
        }

        //set the new end type!
        Model.getCoreHelper().setType(theEnd, newNode);
    }

    /**
     * helper method for changeConnectedNode
     * <p>empty at the moment
     */
    private void rerouteGeneralization(Object newNode, Object oldNode,
				       Object edge, boolean isSource) {

    }

    /**
     * helper method for changeConnectedNode
     * <p>empty at the moment
     */
    private void rerouteDependency(Object newNode, Object oldNode,
				   Object edge, boolean isSource) {

    }

    /**
     * helper method for changeConnectedNode
     * <p>empty at the moment
     */
    private void rerouteLink(Object newNode, Object oldNode,
			     Object edge, boolean isSource) {

    }

} /* end class ClassDiagramGraphModel */
