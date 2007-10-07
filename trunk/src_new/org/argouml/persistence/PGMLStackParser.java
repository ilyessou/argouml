// $Id$
// Copyright (c) 2005-2007 The Regents of the University of California. All
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

package org.argouml.persistence;

import java.awt.Rectangle;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.argouml.uml.diagram.AttributesCompartmentContainer;
import org.argouml.uml.diagram.ExtensionsCompartmentContainer;
import org.argouml.uml.diagram.OperationsCompartmentContainer;
import org.argouml.uml.diagram.PathContainer;
import org.argouml.uml.diagram.StereotypeContainer;
import org.argouml.uml.diagram.VisibilityContainer;
import org.argouml.uml.diagram.activity.ui.FigPool;
import org.argouml.uml.diagram.deployment.ui.FigMNodeInstance;
import org.argouml.uml.diagram.deployment.ui.FigNodeInstance;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigEdgePort;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.persistence.pgml.Container;
import org.tigris.gef.persistence.pgml.FigEdgeHandler;
import org.tigris.gef.persistence.pgml.FigGroupHandler;
import org.tigris.gef.persistence.pgml.HandlerStack;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The PGML Parser.
 */
class PGMLStackParser
    extends org.tigris.gef.persistence.pgml.PGMLStackParser {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(PGMLStackParser.class);

    private List figEdges = new ArrayList(50);
    
    private LinkedHashMap modelElementsByFigEdge = new LinkedHashMap(50);
    
    /**
     * Constructor.
     * @param modelElementsByUuid a map of model elements indexed
     *                            by a unique string identifier.
     */
    public PGMLStackParser(Map modelElementsByUuid) {
        super(modelElementsByUuid);
        // TODO: Use stylesheet to convert or wait till we use Fig
        // factories in diagram subsystem.
        // What is the last version that used FigNote?
        addTranslation("org.argouml.uml.diagram.ui.FigNote",
        	"org.argouml.uml.diagram.static_structure.ui.FigComment");
        addTranslation("org.argouml.uml.diagram.static_structure.ui.FigNote",
            "org.argouml.uml.diagram.static_structure.ui.FigComment");
        addTranslation("org.argouml.uml.diagram.state.ui.FigState",
            "org.argouml.uml.diagram.state.ui.FigSimpleState");
        addTranslation("org.argouml.uml.diagram.ui.FigCommentPort",
            "org.argouml.uml.diagram.ui.FigEdgePort");
        // Convert class reference to a string name when the class is removed
        addTranslation(
                FigMNodeInstance.class.getName(),
                FigNodeInstance.class.getName());
    }

    /*
     * @see org.tigris.gef.persistence.pgml.HandlerFactory#getHandler(
     *         HandlerStack, Object, String, String, String, Attributes)
     */
    public DefaultHandler getHandler(HandlerStack stack,
                                             Object container,
                                             String uri,
                                             String localname,
                                             String qname,
                                             Attributes attributes)
        throws SAXException {

        String href = attributes.getValue("href");
        Object owner = null;
        
        if (href != null) {
            owner = findOwner(href);
            if (owner == null) {
                LOG.warn("Found href of " 
                	+ href
                	+ " with no matching element in model");
                return null;
            }
        }
	
        // Ignore non-private elements within FigNode groups
        if (container instanceof FigGroupHandler) {
            FigGroup group = ((FigGroupHandler) container).getFigGroup();
            if (group instanceof FigNode && !qname.equals("private")) {
                return null;
            }
        }

        // Handle ItemUID in container contents
        if (qname.equals("private") && (container instanceof Container)) {
            return new PrivateHandler(this, (Container) container);
        }

        DefaultHandler handler =
            super.getHandler(stack, container, uri, localname, qname,
                    attributes);

        if (handler instanceof FigEdgeHandler) {
            return new org.argouml.persistence.FigEdgeHandler(
                    this, ((FigEdgeHandler) handler).getFigEdge());
        }

        return handler;

    }

    /*
     * @see org.tigris.gef.persistence.pgml.PGMLStackParser#setAttrs(
     *         org.tigris.gef.presentation.Fig, org.xml.sax.Attributes)
     */
    protected final void setAttrs(Fig f, Attributes attrList)
        throws SAXException {
        
        if (f instanceof FigGroup) {
            FigGroup group = (FigGroup) f;
            String clsNameBounds = attrList.getValue("description");
            if (clsNameBounds != null) {
                StringTokenizer st =
                    new StringTokenizer(clsNameBounds, ",;[] ");
                // Discard class name, x y w h
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }

                Map attributeMap = interpretStyle(st);
                setStyleAttributes(group, attributeMap);
            }
        }

        // TODO: Attempt to move the following code to GEF

        String name = attrList.getValue("name");
        if (name != null && !name.equals("")) {
            registerFig(f, name);
        }

        setCommonAttrs(f, attrList);

        String href = attrList.getValue("href");
        if (href != null && !href.equals("")) {
            Object modelElement = findOwner(href);
            if (modelElement == null) {
                LOG.error("Can't find href of " + href);
                throw new SAXException("Found href of " + href
				       + " with no matching element in model");
            }
            if (f.getOwner() != modelElement) {
                // Assign nodes immediately but edges later. See issue 4310.
                if (f instanceof FigEdge) {
                    modelElementsByFigEdge.put(f, modelElement);
                } else {
                    f.setOwner(modelElement);
                }
            } else {
                LOG.debug("Ignoring href on " + f.getClass().getName()
			 + " as it's already set");
            }
        }
    }

    /**
     * The StringTokenizer is expected to be positioned at the start of a string
     * of style identifiers in the format name=value;name=value;name=value....
     * Each name value pair is interpreted and the Fig configured accordingly.
     * The value is optional and will default to a value applicable for its
     * name.
     * The current applicable names are operationsVisible and attributesVisible
     * and are used to show or hide the compartments within Class and Interface.
     * The default values are true.
     * @param st The StrinkTokenizer positioned at the first style identifier
     * @return a map of attributes
     */
    private Map interpretStyle(StringTokenizer st) {
        Map map = new HashMap();
        String name;
        String value;
        while (st.hasMoreElements()) {
            String namevaluepair = st.nextToken();
            int equalsPos = namevaluepair.indexOf('=');
            if (equalsPos < 0) {
                name = namevaluepair;
                value = "true";
            } else {
                name = namevaluepair.substring(0, equalsPos);
                value = namevaluepair.substring(equalsPos + 1);
            }

            map.put(name, value);
        }
        return map;
    }

    /**
     * Set the fig style attributes. This should move into
     * the render factories as described in issue 859.
     * @param fig the fig to style.
     * @param attributeMap a map of name value pairs
     */
    private void setStyleAttributes(Fig fig, Map attributeMap) {
        String name;
        String value;
        Iterator it = attributeMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            name = (String) entry.getKey();
            value = (String) entry.getValue();

            if ("operationsVisible".equals(name)) {
                ((OperationsCompartmentContainer) fig)
                    .setOperationsVisible(value.equalsIgnoreCase("true"));
            } else if ("attributesVisible".equals(name)) {
                ((AttributesCompartmentContainer) fig)
                    .setAttributesVisible(value.equalsIgnoreCase("true"));
            } else if ("stereotypeVisible".equals(name)) {
                ((StereotypeContainer) fig)
                    .setStereotypeVisible(value.equalsIgnoreCase("true"));
            } else if ("visibilityVisible".equals(name)) {
                ((VisibilityContainer) fig)
                .setVisibilityVisible(value.equalsIgnoreCase("true"));
            } else if ("pathVisible".equals(name)) {
                ((PathContainer) fig)
                    .setPathVisible(value.equalsIgnoreCase("true"));
            } else if ("extensionPointVisible".equals(name)) {
                ((ExtensionsCompartmentContainer) fig)
                    .setExtensionPointVisible(value.equalsIgnoreCase("true"));
            }
        }
    }
    
    public Diagram readDiagram(InputStream is, boolean closeStream)
        throws SAXException {
        Diagram d = super.readDiagram(is, closeStream);
        
        attachEdges(d);
        
        return d;
    }
    
    /**
     * This is called when all nodes and edges have been read and placed on
     * the diagram. This method then attaches the edges to the correct node,
     * including the nodes contained within edges allowing edge to edge
     * connections for comment edges, association classes and dependencies.
     * @param d the Diagram
     */
    private void attachEdges(Diagram d) {
        for (Iterator it = figEdges.iterator(); it.hasNext(); ) {
            EdgeData edgeData = (EdgeData) it.next();
            FigEdge edge = edgeData.getFigEdge();
            
            Object modelElement = modelElementsByFigEdge.get(edge);
            if (modelElement != null) {
                edge.setOwner(modelElement);
            }
        }
        
        for (Iterator it = figEdges.iterator(); it.hasNext(); ) {
            EdgeData edgeData = (EdgeData) it.next();
            FigEdge edge = edgeData.getFigEdge();
            
            Fig sourcePortFig = null;
            Fig destPortFig = null;
            FigNode sourceFigNode = null;
            FigNode destFigNode = null;
            
            sourcePortFig = findFig(edgeData.getSourcePortFig());
            destPortFig = findFig(edgeData.getDestPortFig());
            sourceFigNode = getFigNode(edgeData.getSourceFigNode());
            destFigNode = getFigNode(edgeData.getDestFigNode());
            
            if (sourcePortFig == null && sourceFigNode != null) {
                sourcePortFig = getPortFig(sourceFigNode);
            }

            if (destPortFig == null && destFigNode != null) {
                destPortFig = getPortFig(destFigNode);
            }

            if (sourcePortFig == null
            	|| destPortFig == null 
            	|| sourceFigNode == null 
            	|| destFigNode == null) {
                LOG.error("Can't find nodes for FigEdge: "
                        + edge.getId() + ":"
                        + edge.toString());
                edge.removeFromDiagram();
            } else {
                edge.setSourcePortFig(sourcePortFig);
                edge.setDestPortFig(destPortFig);
                edge.setSourceFigNode(sourceFigNode);
                edge.setDestFigNode(destFigNode);
            }
        }
        
        // Once all edges are connected do a compute route on each to make
        // sure that annotations and the edge port is positioned correctly
        // Only do this after all edges are connected as compute route
        // requires all edges to be connected to nodes.
        for (Iterator it = d.getLayer().getContentsEdgesOnly().iterator();
                it.hasNext(); ) {
            ((FigEdge) it.next()).computeRouteImpl();
        }
    }
    
    // TODO: Move to GEF
    public void addFigEdge(FigEdge figEdge, String sourcePortFig, 
            String destPortFig, String sourceFigNode, String destFigNode) {
        figEdges.add(new EdgeData(figEdge, sourcePortFig, destPortFig, 
                sourceFigNode, destFigNode));
    }
    
    // TODO: Move to GEF
    /**
     * Get the FigNode that the fig id represents.
     *
     * @param figId (In the form Figx.y.z)
     * @return the FigNode with the given id
     * @throws IllegalStateException
     *              if the figId supplied is not of a FigNode
     */
    private FigNode getFigNode(String figId) throws IllegalStateException {
        if (figId.indexOf('.') < 0) {
            // If there is no dot then this must be a top level Fig and can be
            // assumed to be a FigNode.
            Fig f = findFig(figId);
            if (f instanceof FigNode) {
                return (FigNode) f;
            } else {
        	LOG.error("FigID " + figId + " is not a node, edge ignored");
                return null;
            }
        }
        // If the id does not look like a top-level Fig then we can assume that
        // this is an id of a FigEdgePort inside some FigEdge.
        // So extract the FigEdgePort from the FigEdge and return that as
        // the FigNode.
        figId = figId.substring(0, figId.indexOf('.'));
        FigEdgeModelElement edge = (FigEdgeModelElement) findFig(figId);
        if (edge == null) {
            throw new IllegalStateException(
                    "Can't find a FigNode with id " + figId);
        }
        edge.makeEdgePort();
        return edge.getEdgePort();
    }
    

    // TODO: Move to GEF
    /**
     * Get the Fig from the FigNode that is the port.
     *
     * @param figNode the FigNode
     * @return the Fig that is the port on the given FigNode
     */
    private Fig getPortFig(FigNode figNode) {
        if (figNode instanceof FigEdgePort) {
            // TODO: Can we just do this every time, no need for else - Bob
            return figNode;
        } else {
            return (Fig) figNode.getPortFigs().get(0);
        }
    }
    
    // TODO: Move to GEF
    private class EdgeData {
        private FigEdge figEdge;
        private String sourcePortFig;
        private String destPortFig;
        private String sourceFigNode;
        private String destFigNode;
        
        public EdgeData(FigEdge edge, String sourcePort, 
                String destPort, String sourceNode, String destNode) {
            this.figEdge = edge;
            this.sourcePortFig = sourcePort;
            this.destPortFig = destPort;
            this.sourceFigNode = sourceNode;
            this.destFigNode = destNode;
        }
        
        public String getDestFigNode() {
            return destFigNode;
        }
        public String getDestPortFig() {
            return destPortFig;
        }
        public FigEdge getFigEdge() {
            return figEdge;
        }
        public String getSourceFigNode() {
            return sourceFigNode;
        }
        public String getSourcePortFig() {
            return sourcePortFig;
        }
    }

    @Override
    protected Fig constructFig(String className, String href, Rectangle bounds)
        throws SAXException {
	
	Fig f = null;
	
        if (className.equals(FigPool.class.getName())) {
            f = new FigPool(bounds);
        } else {
            f = super.constructFig(className, href, bounds);
        }

	return f;
    }
}
