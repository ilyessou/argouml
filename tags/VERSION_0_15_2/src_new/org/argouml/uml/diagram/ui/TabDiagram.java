
// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// $Id$

package org.argouml.uml.diagram.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;


import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.application.api.Configuration;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.TabSpawnable;
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
import org.tigris.gef.graph.presentation.DefaultGraphModel;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.presentation.Fig;
import org.tigris.toolbar.ToolBarFactory;


/**
 * The TabDiagram is the tab in the multieditorpane that holds a diagram. The 
 * TabDiagram consists of a JGraph (with the figs) and a toolbar. It is possible
 * to spawn objects of this class into a dialog via the spawn method of its
 * parent.
 */
public class TabDiagram
    extends TabSpawnable
    implements TabModelTarget, GraphSelectionListener, ModeChangeListener {

    private Logger cat = Logger.getLogger(TabDiagram.class);

    ////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * the diagram object
     */
    protected UMLDiagram _target;

    /**
     * The GEF JGraph in where the figs are painted
     */
    protected JGraph _jgraph;

    /**
     * used but there doesn't appear to be a purpose.
     */
    protected boolean _shouldBeEnabled = true;

    /**
     * The toolbar that is positioned just above
     * the diagram containing the drawing tools.
     */
    protected JToolBar _toolBar;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * calls the other constructor.
     */
    public TabDiagram() {
        this("Diagram");
    }

    public TabDiagram(String tag) {
        super(tag);
        setLayout(new BorderLayout());
        _jgraph = new ArgoJGraph();
        _jgraph.setDrawingSize((612 - 30) * 2, (792 - 55 - 20) * 2);
        // TODO: should update to size of diagram contents

        Globals.setStatusBar(ProjectBrowser.getInstance());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        p.add(_jgraph, BorderLayout.CENTER);
        add(p, BorderLayout.CENTER);
        _jgraph.addGraphSelectionListener(this);
        _jgraph.addModeChangeListener(this);
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
        if (_target != null) {
            newPanel.setTarget(_target);
        }
        newPanel.setToolBar(ToolBarFactory.createToolBar(true/*rollover*/,
                                                         _target.getActions(),
                                                         false/*floating*/));
        setToolBar(ToolBarFactory.createToolBar(true/*rollover*/,
                                                _target.getActions(),
                                                false/*floating*/));
        return newPanel;
    }

    /**
     * Sets the target of the tab. The target should allways be an instance of
     * UMLDiagram
     * @param t
     * @deprecated As of ArgoUml version 0.13.5, the visibility of
     * this method will change in the future, replaced by {@link
     * org.argouml.ui.targetmanager.TargetManager}.
     */
    public void setTarget(Object t) {

        if (!(t instanceof UMLDiagram)) {
            _shouldBeEnabled = false;
            // This is perfectly normal and happens among other things
            // within the call to setDiagram (below).
            cat.debug(
		      "target is null in set target or not an instance of UMLDiagram");
            return;
        }
        if (_target != null) {
            _target.removeAsTarget();
        }
        UMLDiagram target = (UMLDiagram) t;

        target.setAsTarget();

        _shouldBeEnabled = true;
        setToolBar(target.getJToolBar());
        _jgraph.removeGraphSelectionListener(this);
        _jgraph.setDiagram(target);
        _jgraph.addGraphSelectionListener(this);
        _target = target;
    }

    public Object getTarget() {
        return _target;
    }

    public JToolBar getToolBar() {
        return _toolBar;
    }

    public void refresh() {
        setTarget(_target);
    }

    public boolean shouldBeEnabled(Object target) {

        if (target instanceof UMLDiagram) {
            _shouldBeEnabled = true;
        } else {
            _shouldBeEnabled = false;
        }

        return _shouldBeEnabled;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public JGraph getJGraph() {
        return _jgraph;
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        getJGraph().setVisible(b);
    }

    ////////////////////////////////////////////////////////////////
    // events

    /**
     * In the selectionChanged method not only the selection of this
     * diagram is set but also the selection in the projectbrowser.
     */
    public void selectionChanged(GraphSelectionEvent gse) {
        Vector sels = gse.getSelections();
        ActionCut.getInstance().setEnabled(sels != null && !sels.isEmpty());
        ActionCopy.getInstance().setEnabled(sels != null && !sels.isEmpty());
        /*
	  ActionPaste.getInstance().setEnabled(
	  Globals.clipBoard != null && !Globals.clipBoard.isEmpty());
	*/
        TargetManager.getInstance().setTargets(sels);

    }

    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        _jgraph.removeGraphSelectionListener(listener);
    }

    public void modeChange(ModeChangeEvent mce) {
        cat.debug("TabDiagram got mode change event");
        if (!Globals.getSticky() && Globals.mode() instanceof ModeSelect) {
//            if (_target instanceof UMLDiagram) {
                _target.deselectAllTools();
//            }
        }
    }

    
    public void removeModeChangeListener(ModeChangeListener listener) {
        _jgraph.removeModeChangeListener(listener);
    }

    /**
     * Sets the toolbar. Adds the toolbar to the north borderlayout position of
     * the diagram.
     * @param toolbar
     */
    public void setToolBar(JToolBar toolbar) {
        if (!Arrays.asList(getComponents()).contains(toolbar)) {
            if (_target != null) {
                remove(((UMLDiagram) getTarget()).getJToolBar());
            }
            add(toolbar, BorderLayout.NORTH);
            _toolBar = toolbar;
            invalidate();
            validate();
            repaint();
        }
    }

    /**
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetAdded(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    /**
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetRemoved(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // how to handle empty target lists?
        // probably the TabDiagram should only show an empty pane in that case
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    /**
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
        select(e.getNewTargets());
    }

    private void select(Object[] targets) {
        LayerManager manager = _jgraph.getEditor().getLayerManager();
        Vector figList = new Vector();
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != null) {
                Object target =
                    (targets[i] instanceof Fig
		     && manager.getContents().contains(targets[i]))
		    ? targets[i]
		    : manager.presentationFor(targets[i]);
                if (target != null) {
                    figList.add(target);
                }
            }
        }

	if (!figList.equals(_jgraph.selectedFigs())) {
            _jgraph.deselectAll();
            _jgraph.select(figList);
	}
    }

}

/**
 * UMLJGraph is a JGraph that updates the Figs representing modelelements if
 * they are in the clipping area.
 * @author jaap.branderhorst@xs4all.nl
 * @since Apr 13, 2003
 */
class ArgoJGraph extends JGraph {

    

    public boolean equals(Object o) {
        if (o instanceof ArgoJGraph) {
            ArgoJGraph a = (ArgoJGraph) o;            
            if (((this._currentDiagramId != null
		  && this._currentDiagramId.equals(a._currentDiagramId))
		 || (this._currentDiagramId == null
		     && a._currentDiagramId == null))
                && this.getEditor().equals(a.getEditor()))
                return true;
        }
        return false;
    }

    /** Make a new JGraph with a new DefaultGraphModel.
       * @see uci.graph.DefaultGraphModel */
    public ArgoJGraph() {
        this(new DefaultGraphModel());
    }

    /** Make a new JGraph with a the GraphModel and Layer from the given
     *  Diagram. */
    public ArgoJGraph(Diagram d) {
        this(new ArgoEditor(d));
    }

    /** Make a new JGraph with the given GraphModel */
    public ArgoJGraph(GraphModel gm) {
        this(new ArgoEditor(gm, null));
    }
    
    /** Make a new JGraph with the given Editor.  All JGraph contructors
     *  eventually call this contructor. */
    public ArgoJGraph(Editor ed) {
        super(ed); 
    }
        

}

class ArgoEditor extends Editor {
    
    private RenderingHints  _argoRenderingHints;
        
    public ArgoEditor(Diagram d) {
	super(d);
    setupRenderingHints();
    }
        
    public ArgoEditor(GraphModel gm, Component c) {
	super(gm, c);
    setupRenderingHints();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
	if (_activeTextEditor != null)
	    _activeTextEditor.requestFocus();         
	translateMouseEvent(me);
	Globals.curEditor(this);
	mode((FigModifyingMode) Globals.mode());
	setUnderMouse(me);
	_modeManager.mouseEntered(me);
    }
        
    /** Invoked when the mouse button has been moved (with no buttons
     * no down). */
    public void mouseMoved(MouseEvent me) {
	//- RedrawManager.lock();
	translateMouseEvent(me);
	Globals.curEditor(this);
	setUnderMouse(me);
	if (_curFig != null && Globals.getShowFigTips()) {
	    String tip = _curFig.getTipString(me);
	    if (tip != null && tip.length() > 0 && !tip.endsWith(" "))
		tip += " ";
	    if (tip != null && (getAwtComponent() instanceof JComponent)) {
		if (((JComponent) getAwtComponent()).getToolTipText() != null
		    && !((JComponent) getAwtComponent()).getToolTipText().equals(tip))
                    ((JComponent) getAwtComponent()).setToolTipText(tip);
	    }
	} else if (getAwtComponent() instanceof JComponent) {
	    if (((JComponent) getAwtComponent()).getToolTipText() != null)
		((JComponent) getAwtComponent()).setToolTipText(null); //was ""
	}

	_selectionManager.mouseMoved(me);
	_modeManager.mouseMoved(me);
	//- RedrawManager.unlock();
	//- _redrawer.repairDamage();
    }
    
    /**
     * Overridden to set Argo-specific RenderingHints to determine whether
     * or not antialiasing should be turned on.
     */
    public synchronized void paint(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(_argoRenderingHints);
            g2.scale(_scale, _scale);
        }
        getLayerManager().paint(g);
        _selectionManager.paint(g);
        _modeManager.paint(g);
        
// Use this code once new version of GEF (post-0.9.6) becomes available:        
//        if(!shouldPaint())
//            return;
//
//        if(g instanceof Graphics2D) {
//            Graphics2D g2 = (Graphics2D) g;
//            g2.setRenderingHints(_argoRenderingHints);
//            g2.scale(_scale, _scale);
//        }
//        getLayerManager().paint(g);
//        //getLayerManager().getActiveLayer().paint(g);
//        if(_canSelectElements) {
//            _selectionManager.paint(g);
//            _modeManager.paint(g);
//        }        
    }
    
    /**
     * Construct a new set of RenderingHints to reflect current user
     * settings.
     */
    private void setupRenderingHints() {
        _argoRenderingHints = new RenderingHints(null);

        _argoRenderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, 
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        if (Configuration.getBoolean(Argo.KEY_SMOOTH_EDGES, false)) {
            _argoRenderingHints.put(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            _argoRenderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            _argoRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);        
        }
        else {
            _argoRenderingHints.put(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_SPEED);
            _argoRenderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_OFF);
            _argoRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);        
        }
    }
}
