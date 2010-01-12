/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.dev.figinspector;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.argouml.dev.MessageNodeBuilder;
import org.argouml.sequence2.diagram.FigClassifierRole;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.event.GraphSelectionEvent;
import org.tigris.gef.event.GraphSelectionListener;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigText;

/**
 * The fig inspector listens for selection of a single fig.
 * It presents the composite structure of the selected Fig
 * in the dev panel.
 * @author Bob Tarling
 */
public final class FigInspectorPanel
    extends JPanel implements GraphSelectionListener {

    /**
     * The UID.
     */
    private static final long serialVersionUID = -3483456053389473380L;

    /**
     * The instance.
     */
    private static final FigInspectorPanel INSTANCE =
        new FigInspectorPanel();

    /**
     * @return The instance.
     */
    public static FigInspectorPanel getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    private FigInspectorPanel() {
        Globals.curEditor().getSelectionManager()
            .addGraphSelectionListener(this);
        setLayout(new BorderLayout());
    }

    public void selectionChanged(GraphSelectionEvent selectionEvent) {
        removeAll();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        if (selectionEvent.getSelections().size() == 0) {
            Layer lay = Globals.curEditor().getLayerManager().getActiveLayer();
            for (Object o :  lay.getContents()) {
                addFig ((Fig) o, rootNode, false);
            }
        } else if (selectionEvent.getSelections().size() == 1) {
            addFig ((Fig) selectionEvent.getSelections().get(0),
        	    rootNode,
        	    true);
        }
        
        FigTree tree = new FigTree(rootNode);
        tree.setRootVisible(false);
        tree.expandAll();

        JScrollPane scroller = new JScrollPane(tree);
        add(scroller);
    }
    
    private void addFig(
	    final Fig f, 
	    final DefaultMutableTreeNode rootNode, 
	    final boolean includeEncloser) {
        // Build the selected Fig first and then iterate up through
        // its enclosers building those also.
        for (Fig fig = f;
                fig != null;
                fig = includeEncloser ? fig.getEnclosingFig() : null) {
            DefaultMutableTreeNode figNode =
                new DefaultMutableTreeNode(getDescr(fig));
            rootNode.add(figNode);
            buildTree(fig, figNode);
            
            // For a classifier role on a sequence diagram
            // show its message nodes
            if (fig instanceof FigClassifierRole) {
                MessageNodeBuilder.addNodeTree(rootNode,
                        (FigClassifierRole) fig);
            }
        }
    }

    private void buildTree(Fig f, DefaultMutableTreeNode tn) {
        if (f instanceof FigGroup) {
            FigGroup fg = (FigGroup) f;
            for (int i = 0; i < fg.getFigCount(); ++i) {
                addNode(tn, fg.getFigAt(i));
            }
        } else if (f instanceof FigEdge) {
            FigEdge fe = (FigEdge) f;
            Fig lineFig = fe.getFig();
            addNode(tn, lineFig);
            addNode(tn, fe.getSourceFigNode());
            addNode(tn, fe.getSourcePortFig());
            addNode(tn, fe.getDestFigNode());
            addNode(tn, fe.getDestPortFig());
            for (Fig pathFig : (Vector<Fig>) fe.getPathItemFigs()) {
                addNode(tn, pathFig);
            }
        }
    }

    private void addNode(DefaultMutableTreeNode tn, Fig fig) {
        DefaultMutableTreeNode childNode =
            new DefaultMutableTreeNode(getDescr(fig));
        buildTree(fig, childNode);
        tn.add(childNode);
    }
    
    private String getDescr(Fig f) {
        if (f == null) {
            return null;
        }
        String className = f.getClass().getName();
        StringBuffer descr = new StringBuffer(
                className.substring(className.lastIndexOf(".") + 1));
//        descr.append(" - paints=").append(f.getPaintCount());
//        descr.append(" - damages=").append(f.getDamageCount());
        descr.append(
                " - bounds=[" + f.getX() + "," + f.getY() + "," + f.getWidth()
                        + "," + f.getHeight() + "]");
        if (!f.isVisible()) {
            descr.append(" - INVISIBLE");
        }
        if (f.isFilled()) {
            descr.append(" - FILLED");
        }
        descr.append(
                " - fill=[" + f.getFillColor().getRed() + ","
                        + f.getFillColor().getGreen() + ","
                        + f.getFillColor().getBlue() + "]");
        if (f.getOwner() != null) {
            descr.append(" - owner=").append(f.getOwner());
        }
        if (f instanceof FigText) {
            descr.append(" \"").append(((FigText) f).getText()).append("\"");
        }
        
        descr.append(" - lay=").append(toString(f.getLayer()));
        descr.append(" - grp=").append(toString(f.getGroup()));
        return descr.toString();
    }
    
    private static String toString(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            return o.toString();
        } catch (Throwable e) {
            return "???";
        }
    }
}
