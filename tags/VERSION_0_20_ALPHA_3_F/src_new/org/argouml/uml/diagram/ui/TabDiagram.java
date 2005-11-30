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

package org.argouml.uml.diagram.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.application.api.Configuration;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.AbstractArgoJPanel;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionCopy;
import org.argouml.uml.ui.ActionCut;
import org.argouml.uml.ui.TabModelTarget;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.FigModifyingMode;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerManager;
import org.tigris.gef.base.ModeSelect;
import org.tigris.gef.event.GraphSelectionEvent;
import org.tigris.gef.event.GraphSelectionListener;
import org.tigris.gef.event.ModeChangeEvent;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.presentation.Fig;
import org.tigris.toolbar.ToolBarFactory;


/**
 * The TabDiagram is the tab in the multieditorpane that holds a diagram. The
 * TabDiagram consists of a JGraph (with the figs) and a toolbar.
 * It used to be possible (in past versions of ArgoUML)
 * to spawn objects of this class into a dialog via the spawn method of its
 * parent.
 */
public class TabDiagram
    extends AbstractArgoJPanel
    implements TabModelTarget, GraphSelectionListener, ModeChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(TabDiagram.class);

    ////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The diagram object.
     */
    private UMLDiagram target;

    /**
     * The GEF JGraph in where the figs are painted.
     */
    private JGraph graph;

    /**
     * Prevents target event cycles between this and the TargetManager.
     */
    private boolean updatingSelection = false;

    /**
     * The toolbar that is positioned just above
     * the diagram containing the drawing tools.
     */
    private JToolBar toolBar;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Default constructor.
     */
    public TabDiagram() {
        this("Diagram");
    }

    /**
     * Constructor.
     *
     * @param tag The type of diagram that we are creating.
     */
    public TabDiagram(String tag) {
        super(tag);
        setLayout(new BorderLayout());
        graph = new DnDJGraph();
        graph.setDrawingSize((612 - 30) * 2, (792 - 55 - 20) * 2);
        // TODO: should update to size of diagram contents

        Globals.setStatusBar(ProjectBrowser.getInstance());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        p.add(graph, BorderLayout.CENTER);
        add(p, BorderLayout.CENTER);
        graph.addGraphSelectionListener(this);
        graph.addModeChangeListener(this);
    }

    /**
     * The clone method that should clone the JGraph with it's contents and
     * the toolbar with it's contents. Since both JGraph as the toolbar are
     * coming from the GEF framework, cloning them will be hard work and should
     * actually not be placed here but in a clone method of the JGraph and the
     * Toolbar.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        // next statement gives us a clone JGraph but not a cloned Toolbar
        TabDiagram newPanel = new TabDiagram();
        if (target != null) {
            newPanel.setTarget(target);
        }
        newPanel.setToolBar(ToolBarFactory.createToolBar(true/*rollover*/,
                                                         target.getActions(),
                                                         false/*floating*/));
        setToolBar(ToolBarFactory.createToolBar(true/*rollover*/,
                                                target.getActions(),
                                                false/*floating*/));
        return newPanel;
    }

    /**
     * @see org.argouml.ui.TabTarget#setTarget(java.lang.Object)
     *
     * Sets the target of the tab. The target should allways be an instance of
     * UMLDiagram
     * @param t
     * @deprecated As of ArgoUml version 0.13.5, the visibility of
     * this method will change in the future, replaced by {@link
     * org.argouml.ui.targetmanager.TargetManager}.
     */
    public void setTarget(Object t) {

        if (!(t instanceof UMLDiagram)) {
            // This is perfectly normal and happens among other things
            // within the call to setDiagram (below).
            LOG.debug("target is null in set target or "
		      + "not an instance of UMLDiagram");
            return;
        }
        if (target != null) {
            target.removeAsTarget();
        }
        UMLDiagram newTarget = (UMLDiagram) t;

        newTarget.setAsTarget();

        setToolBar(newTarget.getJToolBar());
        // This call fixes a seemingly-harmless NPE when we switch to a
        // sequence diagram; I have no idea why
        // --Michael MacDonald
        javax.swing.RepaintManager.currentManager(this).paintDirtyRegions();
        graph.removeGraphSelectionListener(this);
        graph.setDiagram(newTarget);
        graph.addGraphSelectionListener(this);
        target = newTarget;
    }

    /**
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Getter for the Toolbar.
     *
     * @return The ToolBar.
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() {
        setTarget(target);
    }

    /**
     * @see org.argouml.ui.TabTarget#shouldBeEnabled(java.lang.Object)
     */
    public boolean shouldBeEnabled(Object newTarget) {
        return newTarget instanceof UMLDiagram;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * Getter for the {@link JGraph}.
     *
     * @return The JGraph.
     */
    public JGraph getJGraph() {
        return graph;
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean b) {
        super.setVisible(b);
        getJGraph().setVisible(b);
    }

    ////////////////////////////////////////////////////////////////
    // events

    /**
     * In the selectionChanged method not only the selection of this
     * diagram is set but also the selection in the projectbrowser.
     *
     * @param gse The event.
     */
    public void selectionChanged(GraphSelectionEvent gse) {
        if (!updatingSelection) {
            updatingSelection = true;
            Vector sels = gse.getSelections(); // the new selection
            ActionCut.getInstance().setEnabled(sels != null && !sels.isEmpty());

            // TODO: If ActionCopy is no longer a singleton, how shall
            //       this work?
            ActionCopy.getInstance()
                    .setEnabled(sels != null && !sels.isEmpty());
            /*
             * ActionPaste.getInstance().setEnabled( Globals.clipBoard
             * != null && !Globals.clipBoard.isEmpty());
             */
            Collection currentSelection = // the old selection
                TargetManager.getInstance().getTargets();

            List removedTargets = new ArrayList(currentSelection);
            Iterator i = sels.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                o = TargetManager.getInstance().getOwner(o);
                if (currentSelection.contains(o)) {
                    removedTargets.remove(o); // remains selected
                } else {
                    // add to selection
                    TargetManager.getInstance().addTarget(o);
                }
            }
            i = removedTargets.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                // remove from selection
                TargetManager.getInstance().removeTarget(o);
            }
            updatingSelection = false;
        }

    }

    /**
     * @param listener the listener to be removed
     */
    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        graph.removeGraphSelectionListener(listener);
    }

    /**
     * @see org.tigris.gef.event.ModeChangeListener#modeChange(org.tigris.gef.event.ModeChangeEvent)
     */
    public void modeChange(ModeChangeEvent mce) {
        LOG.debug("TabDiagram got mode change event");
        if (!Globals.getSticky() && Globals.mode() instanceof ModeSelect) {
//            if (_target instanceof UMLDiagram) {
	    target.deselectAllTools();
//            }
        }
    }


    /**
     * @param listener the listener to be removed
     */
    public void removeModeChangeListener(ModeChangeListener listener) {
        graph.removeModeChangeListener(listener);
    }

    /**
     * Sets the toolbar.  Adds the toolbar to the north borderlayout
     * position of the diagram.<p>
     *
     * @param toolbar is the toolbar to be set.
     */
    public void setToolBar(JToolBar toolbar) {
        if (!Arrays.asList(getComponents()).contains(toolbar)) {
            if (target != null) {
                remove(((UMLDiagram) getTarget()).getJToolBar());
            }
            add(toolbar, BorderLayout.NORTH);
            toolBar = toolbar;
            invalidate();
            validate();
            repaint();
        }
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetAdded(
     *          TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetRemoved(
     *          TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // how to handle empty target lists?
        // probably the TabDiagram should only show an empty pane in that case
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(
     *          org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    private void select(Object[] targets) {
        LayerManager manager = graph.getEditor().getLayerManager();
        Vector figList = new Vector();
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != null) {
                Object theTarget = null;
                if (targets[i] instanceof Fig
		        && manager.getActiveLayer().getContents().contains(
                        targets[i])) {
		    theTarget = targets[i];
                } else {
		    theTarget = manager.presentationFor(targets[i]);
                }

                if (theTarget != null && !figList.contains(theTarget)) {
                    figList.add(theTarget);
                }
            }
        }

	if (!figList.equals(graph.selectedFigs())) {
            graph.deselectAll();
            graph.select(figList);
	}
    }

}


/**
 * The ArgoUML editor.
 */
class ArgoEditor extends Editor {

    private RenderingHints  argoRenderingHints;

    /**
     * Constructor for the Editor.
     *
     * @param d The Diagram that this editor works in.
     */
    public ArgoEditor(Diagram d) {
	super(d);
        setupRenderingHints();
    }

    /**
     * Constructor for the Editor.
     *
     * @param gm The Graphmodel.
     * @param c The component.
     */
    public ArgoEditor(GraphModel gm, JComponent c) {
	super(gm, c);
        setupRenderingHints();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
	if (getActiveTextEditor() != null) {
            getActiveTextEditor().requestFocus();
        }
	translateMouseEvent(me);
	Globals.curEditor(this);
	pushMode((FigModifyingMode) Globals.mode());
	setUnderMouse(me);
	_modeManager.mouseEntered(me);
    }

    /**
     * Invoked when the mouse has been moved (with no buttons down).
     *
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     * @param me The {@link MouseEvent}.
     */
    public void mouseMoved(MouseEvent me) {
	//- RedrawManager.lock();
	translateMouseEvent(me);
	Globals.curEditor(this);
	setUnderMouse(me);
        Fig currentFig = getCurrentFig();
	if (currentFig != null && Globals.getShowFigTips()) {
	    String tip = currentFig.getTipString(me);
	    if (tip != null && (getJComponent() != null)) {
	        JComponent c = getJComponent();
	        if (c.getToolTipText() == null
		    || !(c.getToolTipText().equals(tip))) {
	            c.setToolTipText(tip);
	        }
            }
	} else if (getJComponent() != null
		   && getJComponent().getToolTipText() != null) {
            getJComponent().setToolTipText(null); //was ""
	}

	_selectionManager.mouseMoved(me);
	_modeManager.mouseMoved(me);
	//- RedrawManager.unlock();
	//- _redrawer.repairDamage();
    }

    /**
     * Overridden to set Argo-specific RenderingHints to determine whether
     * or not antialiasing should be turned on.
     *
     * @see org.tigris.gef.base.Editor#paint(java.awt.Graphics)
     */
    public synchronized void paint(Graphics g) {
        if (!shouldPaint()) {
            return;
        }

        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(argoRenderingHints);
	    double scale = getScale();
            g2.scale(scale, scale);
        }
        getLayerManager().paint(g);
        //getLayerManager().getActiveLayer().paint(g);
        if (_canSelectElements) {
            _selectionManager.paint(g);
            _modeManager.paint(g);
        }
    }

    /**
     * Construct a new set of RenderingHints to reflect current user
     * settings.
     */
    private void setupRenderingHints() {
        argoRenderingHints = new RenderingHints(null);

        argoRenderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        if (Configuration.getBoolean(Argo.KEY_SMOOTH_EDGES, false)) {
            argoRenderingHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
            argoRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            argoRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            argoRenderingHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
            argoRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
            argoRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }
}
