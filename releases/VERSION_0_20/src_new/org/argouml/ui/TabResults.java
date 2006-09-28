// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ChildGenRelated;
import org.argouml.uml.PredicateFind;
import org.argouml.uml.TMResults;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.util.ChildGenerator;

/**
 * The results tab for the find dialog.
 *
 */
public class TabResults
        extends AbstractArgoJPanel
        implements
                Runnable,
                MouseListener,
                ActionListener,
                ListSelectionListener,
                KeyListener {
    private static final Logger LOG = Logger.getLogger(TabResults.class);

    private static int numJumpToRelated = 0;

    /** Insets in pixels  */
    private static final int INSET_PX = 3;

    ////////////////////////////////////////////////////////////////
    // instance variables
    private PredicateFind pred;
    private ChildGenerator cg = null;
    private Object root = null;
    private JSplitPane mainPane;
    private Vector results = new Vector();
    private Vector related = new Vector();
    private Vector diagrams = new Vector();
    private boolean relatedShown = false;

    private JLabel resultsLabel = new JLabel(/*"Results:"*/);
    private JTable resultsTable;
    private TMResults resultsModel;

    private JLabel relatedLabel = new JLabel(/*"Related Elements:"*/);
    private JTable relatedTable = new JTable(4, 4);
    private TMResults relatedModel = new TMResults();

    /**
     * The constructor.
     *
     */
    public TabResults() {
	this(true);
    }

    /**
     * The constructor.
     *
     * @param showRelated true if related results should be shown
     */
    public TabResults(boolean showRelated) {
	super("Results", true);
	relatedShown = showRelated;
	setLayout(new BorderLayout());
	resultsTable = new JTable(10, showRelated ? 4 : 3);
	resultsModel = new TMResults(showRelated);

	JPanel resultsW = new JPanel();
	JScrollPane resultsSP = new JScrollPane(resultsTable);
	resultsW.setLayout(new BorderLayout());
	resultsLabel.setBorder(BorderFactory.createEmptyBorder(
                INSET_PX, INSET_PX, INSET_PX, INSET_PX));
	resultsW.add(resultsLabel, BorderLayout.NORTH);
	resultsW.add(resultsSP, BorderLayout.CENTER);
	resultsTable.setModel(resultsModel);
	resultsTable.addMouseListener(this);
	resultsTable.addKeyListener(this);
	resultsTable.getSelectionModel().addListSelectionListener(
								   this);
	resultsTable.setSelectionMode(
				       ListSelectionModel.SINGLE_SELECTION);
	resultsW.setMinimumSize(new Dimension(100, 100));

	JPanel relatedW = new JPanel();
	if (relatedShown) {
	    JScrollPane relatedSP = new JScrollPane(relatedTable);
	    relatedW.setLayout(new BorderLayout());
            relatedLabel.setBorder(BorderFactory.createEmptyBorder(
                    INSET_PX, INSET_PX, INSET_PX, INSET_PX));
	    relatedW.add(relatedLabel, BorderLayout.NORTH);
	    relatedW.add(relatedSP, BorderLayout.CENTER);
	    relatedTable.setModel(relatedModel);
	    relatedTable.addMouseListener(this);
	    relatedTable.addKeyListener(this);
	    relatedW.setMinimumSize(new Dimension(100, 100));
	}

	if (relatedShown) {
	    mainPane =
		new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			       resultsW,
			       relatedW);
	    add(mainPane, BorderLayout.CENTER);
	} else {
	    add(resultsW, BorderLayout.CENTER);
	}

    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @param p the predicate for the search
     */
    public void setPredicate(PredicateFind p) {
	pred = p;
    }

    /**
     * @param r the root object for the search
     */
    public void setRoot(Object r) {
	root = r;
    }

    /**
     * @param gen the generator
     */
    public void setGenerator(ChildGenerator gen) {
	cg = gen;
    }

    /**
     * @param res the results
     * @param dia the diagrams
     */
    public void setResults(Vector res, Vector dia) {
	results = res;
	diagrams = dia;
	Object[] msgArgs = {new Integer(results.size()) };
	resultsLabel.setText(Translator.messageFormat(
            "dialog.tabresults.results-items", msgArgs));
	resultsModel.setTarget(results, diagrams);
	relatedModel.setTarget(null, null);
	relatedLabel.setText(
            Translator.localize("dialog.tabresults.related-items"));
    }

    /**
     * @see org.argouml.ui.AbstractArgoJPanel#spawn()
     */
    public AbstractArgoJPanel spawn() {
	TabResults newPanel = (TabResults) super.spawn();
	if (newPanel != null) {
	    newPanel.setResults(results, diagrams);
	}
	return newPanel;
    }

    /**
     * Handle a doubleclick on the results tab.
     */
    public void doDoubleClick() {
	myDoubleClick(resultsTable);
    }

    /**
     * Select the result at the given index.
     *
     * @param index the given index
     */
    public void selectResult(int index) {
	if (index < resultsTable.getRowCount()) {
	    resultsTable.getSelectionModel().setSelectionInterval(index,
								   index);
	}
    }

    ////////////////////////////////////////////////////////////////
    // ActionListener implementation

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
    }

    ////////////////////////////////////////////////////////////////
    // MouseListener implementation

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
	if (me.getClickCount() >= 2)
	    myDoubleClick(me.getSource());
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent me) {
    }

    private void myDoubleClick(Object src) {
	Object sel = null;
	Diagram d = null;
	if (src == resultsTable) {
	    int row = resultsTable.getSelectionModel().getMinSelectionIndex();
	    if (row < 0)
		return;
	    sel = results.elementAt(row);
	    d = (Diagram) diagrams.elementAt(row);
	} else if (src == relatedTable) {
	    int row = relatedTable.getSelectionModel().getMinSelectionIndex();
	    if (row < 0)
		return;
	    numJumpToRelated++;
	    sel = related.elementAt(row);
	}

	if (d != null)
	    LOG.debug("go " + sel + " in " + d.getName());
	if (d != null)
	    TargetManager.getInstance().setTarget(d);
	TargetManager.getInstance().setTarget(sel);
    }

    ////////////////////////////////////////////////////////////////
    // KeyListener implementation

    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    e.consume();
	    myDoubleClick(e.getSource());
	}
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
    }

    ////////////////////////////////////////////////////////////////
    // ListSelectionListener implementation

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent lse) {
	if (lse.getValueIsAdjusting()) {
	    return;
	}
	if (relatedShown) {
	    int row = lse.getFirstIndex();
	    Object sel = results.elementAt(row);
	    LOG.debug("selected " + sel);
	    related.removeAllElements();
	    Enumeration elems =
		ChildGenRelated.getSingleton().gen(sel);
	    if (elems != null) {
		while (elems.hasMoreElements()) {
		    related.addElement(elems.nextElement());
		}
	    }
	    relatedModel.setTarget(related, null);
	    Object[] msgArgs = {new Integer(related.size()) };
	    relatedLabel.setText(Translator.messageFormat(
                "dialog.find.related-elements", msgArgs));
	}
    }

    ////////////////////////////////////////////////////////////////
    // actions

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
	resultsLabel.setText(Translator.localize("dialog.find.searching"));
	results.removeAllElements();
	depthFirst(root, null);
	setResults(results, diagrams);
    }

    private void depthFirst(Object node, Diagram lastDiagram) {
	if (node instanceof Diagram) {
	    lastDiagram = (Diagram) node;
	    if (!pred.matchDiagram(lastDiagram))
		return;
	    // diagrams are not placed in search results
	}
	Enumeration elems = cg.gen(node);
	while (elems.hasMoreElements()) {
	    Object c = elems.nextElement();
	    if (pred.predicate(c)) {
		results.addElement(c);
		diagrams.addElement(lastDiagram);
	    }
	    depthFirst(c, lastDiagram);
	}
    }

} /* end class TabResults */
