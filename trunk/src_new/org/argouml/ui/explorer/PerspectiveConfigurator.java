// $Id$
// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreeModel;

import org.argouml.ui.SpacerPanel;
import org.argouml.ui.explorer.rules.PerspectiveRule;

import org.apache.log4j.Logger;
import org.tigris.gef.util.Converter;

/**
 *
 * The "Configure Perspectives" dialog.
 *
 * This class replaces the old Perspective configurator and improves it by:
 * - saving perspectives to the user profile.
 *
 * Created on 21 December 2003, 21:47
 * @author  alexb
 */
public class PerspectiveConfigurator extends JDialog{
    
    private static Logger cat =
    Logger.getLogger(PerspectiveConfigurator.class);
    
    public static int _numNavConfig = 0;
    
    ////////////////////////////////////////////////////////////////
    // constants
    public final int WIDTH = 400;
    public final int HEIGHT = 600;
    
    ////////////////////////////////////////////////////////////////
    // instance variables
    
    private JPanel  _mainButtons;
    
    private JButton _okButton;
    private JButton _cancelButton;
    
    private JPanel  _configPanel;
    private JList   _perspectiveList;
    private JList   _perspectiveRulesList;
    private JList   _ruleLibraryList;
    private JButton _newPerspectiveButton;
    private JButton _removePerspectiveButton;
    private JButton _dupPersButton;
    private JButton _addRuleButton;
    private JButton _removeRuleButton;
    
    DefaultListModel _perspectiveListModel;
    DefaultListModel _perspectiveRulesListModel;
    DefaultListModel _ruleLibraryListModel;
    
    /** Creates a new instance of PerspectiveDesignerDialog */
    public PerspectiveConfigurator(Frame parent) {
        
        super(parent, "Configure Perspectives", false);
        
        int x = parent.getLocation().x + (parent.getSize().width - WIDTH) / 2;
        int y = parent.getLocation().y + (parent.getSize().height - HEIGHT) / 2;
        setLocation(x, y);
        setSize(WIDTH, HEIGHT);
        
        getContentPane().setLayout(new BorderLayout());
        
        initPersPanel();
        loadData();
        
        JPanel persTab = new JPanel(new BorderLayout(3, 3));
        persTab.add(_configPanel, BorderLayout.CENTER);
        
        _mainButtons.add(_cancelButton);
        _mainButtons.add(_okButton);
        
        getContentPane().add(persTab, BorderLayout.CENTER);
        getContentPane().add(_mainButtons, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(_okButton);
        _numNavConfig++;
    }
    
    /**
     * load the perspectives from the perspective manager for presentation.
     */
    private void loadData(){
        
        Vector perspectives = new Vector();
        Vector rulesLib = new Vector();
        
        perspectives.addAll(PerspectiveManager.getInstance().getPerspectives());
        rulesLib.addAll(PerspectiveManager.getInstance().getRules());
        
        for(int i = 0;i<perspectives.size();i++){
            _perspectiveListModel.addElement(perspectives.get(i));
        }
        for(int i = 0;i<rulesLib.size();i++){
            _ruleLibraryListModel.addElement(rulesLib.get(i));
        }
    }
    
    /**
     * Initialize the Perspectives tab panel.
     */
    public void initPersPanel() {
        
        _mainButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        _okButton = new JButton("OK");
        _cancelButton = new JButton("Cancel");
        _configPanel = new JPanel();
        
        _perspectiveList = new JList(_perspectiveListModel = new DefaultListModel());
        _perspectiveRulesList = new JList(_perspectiveRulesListModel = new DefaultListModel());
        _ruleLibraryList = new JList(_ruleLibraryListModel = new DefaultListModel());
        _newPerspectiveButton = new JButton("New");
        _removePerspectiveButton = new JButton("Remove");
        _dupPersButton = new JButton("Duplicate");
        _addRuleButton = new JButton(">>");
        _removeRuleButton = new JButton("<<");
        
        _perspectiveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _perspectiveRulesList
            .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _ruleLibraryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        GridBagLayout gb = new GridBagLayout();
        _configPanel.setLayout(gb);
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 3;      c.ipady = 3;
        
        JLabel persLabel = new JLabel("Perspectives");
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;      c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 1.0;  c.weighty = 0.0;
        gb.setConstraints(persLabel, c);
        _configPanel.add(persLabel);
        
        JScrollPane persScroll =
        new JScrollPane(_perspectiveList,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.gridx = 0;      c.gridy = 1;
        c.gridwidth = 4;
        c.weightx = 1.0;  c.weighty = 1.0;
        gb.setConstraints(persScroll, c);
        _configPanel.add(persScroll);
        
        _newPerspectiveButton.setMargin(new Insets(0, 0, 0, 0));
        _removePerspectiveButton.setMargin(new Insets(0, 0, 0, 0));
        _dupPersButton.setMargin(new Insets(0, 0, 0, 0));
        JPanel persButtons = new JPanel(new GridLayout(3, 1));
        persButtons.add(_newPerspectiveButton);
        persButtons.add(_removePerspectiveButton);
        persButtons.add(_dupPersButton);
        JPanel persButtonWrapper = new JPanel();
        persButtonWrapper.add(persButtons);
        
        c.gridx = 4;      c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.0;  c.weighty = 0.0;
        gb.setConstraints(persButtonWrapper, c);
        _configPanel.add(persButtonWrapper);
        
        JLabel ruleLibLabel = new JLabel("Rules Library");
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0;  c.weighty = 0.0;
        gb.setConstraints(ruleLibLabel, c);
        _configPanel.add(ruleLibLabel);
        
        _addRuleButton.setMargin(new Insets(0, 0, 0, 0));
        _removeRuleButton.setMargin(new Insets(0, 0, 0, 0));
        JPanel xferButtons = new JPanel();
        xferButtons.setLayout(new BoxLayout(xferButtons, BoxLayout.Y_AXIS));
        xferButtons.add(_addRuleButton);
        xferButtons.add(new SpacerPanel());
        xferButtons.add(_removeRuleButton);
        
        c.gridx = 2;      c.gridy = 4;
        c.weightx = 0.0;  c.weighty = 0.0;
        gb.setConstraints(xferButtons, c);
        _configPanel.add(xferButtons);
        
        JLabel rulesLabel = new JLabel("Selected Rules");
        c.gridx = 3;      c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0;
        gb.setConstraints(rulesLabel, c);
        _configPanel.add(rulesLabel);
        
        c.gridx = 0;      c.gridy = 4;
        c.weighty = 1.0;
        c.gridwidth = 2;  c.gridheight = 2;
        JScrollPane ruleLibScroll =
        new JScrollPane(_ruleLibraryList,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gb.setConstraints(ruleLibScroll, c);
        _configPanel.add(ruleLibScroll);
        
        c.gridx = 3;      c.gridy = 4;
        c.gridwidth = 2;  c.gridheight = 2;
        JScrollPane rulesScroll =
        new JScrollPane(_perspectiveRulesList,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gb.setConstraints(rulesScroll, c);
        _configPanel.add(rulesScroll);
        
        // add action listeners:
        _okButton.addActionListener(new OkListener());
        _cancelButton.addActionListener(new CancelListener());
        _newPerspectiveButton
        .addActionListener(new NewPerspectiveListener());
        _removePerspectiveButton
        .addActionListener(new RemovePerspectiveListener());
        _dupPersButton.addActionListener(new DuplicatePerspectiveListener());
        _addRuleButton.addActionListener(new RuleListener());
        _removeRuleButton.addActionListener(new RuleListener());
        _perspectiveList
        .addListSelectionListener(new PerspectiveListSelectionListener());
        _perspectiveRulesList
        .addListSelectionListener(new RulesListSelectionListener());
        _perspectiveRulesList.addMouseListener(new RuleListMouseListener());
        _ruleLibraryList
        .addListSelectionListener(new LibraryListSelectionListener());
        _ruleLibraryList.addMouseListener(new RuleListMouseListener());
        
        _removePerspectiveButton.setEnabled(false);
        _dupPersButton.setEnabled(false);
        
        _addRuleButton.setEnabled(false);
        _removeRuleButton.setEnabled(false);
        
    }
    
    /**
     *updates the perspectives in the explorer,
     * saves the user perspectives and exits.
     */
    class OkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
            PerspectiveManager.getInstance().removeAllPerspectives();
            
            for(int i=0;i<_perspectiveListModel.getSize();i++){
                Object elem = _perspectiveListModel.getElementAt(i);
                PerspectiveManager.getInstance().addPerspective(elem);
            }
            
            PerspectiveManager.getInstance().saveUserPerspectives();
            setVisible(false);
            dispose();
        }
    }
    
    class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
        }
    }
    
    class NewPerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
	ExplorerPerspective newPers =
	    new ExplorerPerspective("Explorer Perspective " +
			       (_perspectiveList.getModel().getSize()+1));
	_perspectiveListModel.insertElementAt(newPers,0);
	_perspectiveList.setSelectedValue(newPers, true);
        _perspectiveRulesListModel.clear();
        }
    }
    
    class RemovePerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object sel = _perspectiveList.getSelectedValue();
            _perspectiveListModel.removeElement(sel);
            _perspectiveList.setSelectedIndex(0);
            if(_perspectiveListModel.getSize() ==1)
                _removeRuleButton.setEnabled(false);
        }
    }
    
    class DuplicatePerspectiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
        }
    }
    
    class RuleListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
            Object src = e.getSource();
            if (_perspectiveList.getSelectedValue() == null) return;
            
            if (src == _addRuleButton) doAddRule();
            else if (src == _removeRuleButton) doRemoveRule();
            //            else if (src == _ruleLibList) doAddRule();
            //            else if (src == _rulesList) doRemoveRule();
        }
    }
    
    class RuleListMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent me) {
            Object src = me.getSource();
            if (me.getClickCount() != 2 ||
                _perspectiveList.getSelectedValue() == null) return;
            
            if (src == _ruleLibraryList && _addRuleButton.isEnabled())
                doAddRule();
            if (src == _perspectiveRulesList && _removeRuleButton.isEnabled())
                doRemoveRule();
        }
    }
    
    private void doAddRule(){
        Object sel = _ruleLibraryList.getSelectedValue();
        try{
            String ruleName = sel.getClass().getName();
            PerspectiveRule newRule =  
                (PerspectiveRule)Class.forName(ruleName).newInstance();
            
            _perspectiveRulesListModel.insertElementAt(newRule,0);
            ((ExplorerPerspective)_perspectiveList
                .getSelectedValue()).addRule(newRule);
        }catch(Exception e){
            cat.error("problem adding rule");
        }
    }
    
    private void doRemoveRule(){
        PerspectiveRule sel = (PerspectiveRule)_perspectiveRulesList.getSelectedValue();
            Object selPers = _perspectiveList.getSelectedValue();
        
        _perspectiveRulesListModel.removeElement(sel);
        ((ExplorerPerspective)selPers).removeRule(sel);
        
        _perspectiveRulesList.setSelectedIndex(0);
    }
    
    class PerspectiveListSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = _perspectiveList.getSelectedValue();
            Object selRule = _ruleLibraryList.getSelectedValue();
            _removePerspectiveButton.setEnabled(selPers != null);
            
            if (selPers == null) return;
            
            ExplorerPerspective pers = (ExplorerPerspective) selPers;
            _perspectiveRulesListModel.clear();
            
            for(int i=0;i<pers.getRulesArray().length;i++){
                _perspectiveRulesListModel.insertElementAt(pers.getRulesArray()[i],0);
            }
            _addRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
    
    class RulesListSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = _perspectiveList.getSelectedValue();
            Object selRule = _perspectiveRulesList.getSelectedValue();
            _removeRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
    
    class LibraryListSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            
            Object selPers = _perspectiveList.getSelectedValue();
            Object selRule = _ruleLibraryList.getSelectedValue();
            _addRuleButton.setEnabled(selPers != null && selRule != null);
        }
    }
}


// -------------------

//    ////////////////////////////////////////////////////////////////
//    // actions
//
//    /**
//     * Create a new perspective, add to the list.
//     *
//     * TODO: Not a robust naming scheme since duplicates
//     * are still possible; initPersPanel() mentions the need to allow editing.
//     */
//    public void doNewPers() {
//	NavPerspective newPers =
//	    new NavPerspective("New Perspective " +
//			       (_navPane.buildPerspectives().size() + 1));
//	_navPane.addPerspective(newPers);
//	_persList.setListData(Converter.convert(_navPane.buildPerspectives()));
//	_persList.setSelectedValue(newPers, true);
//    }
//
//    /**
//     * Remove selected perspective from the list.
//     */
//    public void doRemovePers() {
//	Object sel = _persList.getSelectedValue();
//	if (!(sel instanceof NavPerspective)) {
//	    cat.warn("doRemovePers: unexepected non-NavPerspective");
//	    return;
//	}
//	NavPerspective np = (NavPerspective) sel;
//
//	// are you sure?
//	int response =
//	    JOptionPane.showConfirmDialog(this,
//					  "Remove Perspective, \""
//					  + np.getName() + "\"?",
//					  "Are you sure?",
//					  JOptionPane.YES_NO_OPTION);
//	if (response == JOptionPane.YES_OPTION) {
////	    _navPane.removePerspective(np);
////
////	    // Remove it from the UI list
////	    _persList
////		.setListData(Converter.convert(_navPane.buildPerspectives()));
//	}
//    }
//
//    /**
//     * not currently supported.
//     */
//    public void doDupPers() {
//	Object sel = _persList.getSelectedValue();
//	if (!(sel instanceof NavPerspective)) {
//	    cat.warn("doDupPers: unexepected non-NavPerspective");
//	    return;
//	}
//	//NavPerspective np = (NavPerspective) sel;
//	//try {
//	//  NavPerspective newNP = (NavPerspective) np.clone();
//	//  NavPerspective.unregisterPerspective(newNP);
//	//}
//	//catch (CloneNotSupportedException cnse) {
//	//    cat.error("exception while cloning NavPerspective", cnse);
//	//}
//    }
//
//    public void doAddRule() {
//	Object sel = _persList.getSelectedValue();
//	if (!(sel instanceof NavPerspective)) {
//	    cat.warn("doAddRule: unexepected non-NavPerspective");
//	    return;
//	}
//	NavPerspective np = (NavPerspective) sel;
//	Object selRule = _ruleLibList.getSelectedValue();
//	if (!(selRule instanceof TreeModel)) {
//	    cat.warn("doAddRule: unexepected non-TreeModel");
//	    return;
//	}
//	TreeModel tm = (TreeModel) selRule;
//	np.addSubTreeModel(tm);
//	_rulesList.clearSelection();
//	_rulesList.setListData(Converter.convert(np.getSubTreeModels()));
//    }
//
//    public void doRemoveRule() {
//	Object sel = _persList.getSelectedValue();
//	if (!(sel instanceof NavPerspective)) {
//	    cat.warn("doRemoveRule: unexepected non-NavPerspective");
//	    return;
//	}
//	NavPerspective np = (NavPerspective) sel;
//	Object selRule = _rulesList.getSelectedValue();
//	if (!(selRule instanceof TreeModel)) {
//	    cat.warn("doRemoveRule: unexepected non-TreeModel");
//	    return;
//	}
//	TreeModel tm = (TreeModel) selRule;
//	np.removeSubTreeModel(tm);
//	_rulesList.clearSelection();
//	_rulesList.setListData(Converter.convert(np.getSubTreeModels()));
//    }
//
//    public void doSelectPers() {
//	Object selPers = _persList.getSelectedValue();
//	Object selRule = _ruleLibList.getSelectedValue();
//	_removePersButton.setEnabled(selPers != null);
//	_dupPersButton.setEnabled(selPers != null);
//	if (selPers == null) return;
//	NavPerspective np = (NavPerspective) selPers;
//	_rulesList.setListData(Converter.convert(np.getSubTreeModels()));
//	_addRuleButton.setEnabled(selPers != null && selRule != null);
//    }
//
//    public void doSelectLibRule() {
//	Object selPers = _persList.getSelectedValue();
//	Object selRule = _ruleLibList.getSelectedValue();
//	_addRuleButton.setEnabled(selPers != null && selRule != null);
//    }
//
//    public void doSelectRule() {
//	Object selPers = _persList.getSelectedValue();
//	Object selRule = _rulesList.getSelectedValue();
//	_removeRuleButton.setEnabled(selPers != null && selRule != null);
//    }
//
//    public void doOk() {
//	NavigatorPane np = NavigatorPane.getInstance();
//	//np.setPerspectives(NavPerspective.getRegisteredPerspectives());
//	//np.updateTree();
//	setVisible(false);
//	dispose();
//    }
//
//    ////////////////////////////////////////////////////////////////
//    // event handlers
//
//    public void actionPerformed(ActionEvent e) {
//	Object src = e.getSource();
//	if (src == _okButton) doOk();
//	else if (src == _newPersButton) doNewPers();
//	else if (src == _removePersButton) doRemovePers();
//	else if (src == _dupPersButton) doDupPers();
//	else if (src == _addRuleButton) doAddRule();
//	else if (src == _removeRuleButton) doRemoveRule();
//	else if (src == _ruleLibList) doAddRule();
//	else if (src == _rulesList) doRemoveRule();
//    }
//
//
//    public void stateChanged(ChangeEvent ce) {
//	Object src = ce.getSource();
//	cat.debug("stateChanged " + src);
//    }
//
//
//    /** Called when the user changes selections in a list. */
//    public void valueChanged(ListSelectionEvent lse) {
//	if (lse.getValueIsAdjusting()) return;
//	Object src = lse.getSource();
//	if (src == _persList) doSelectPers();
//	else if (src == _ruleLibList) doSelectLibRule();
//	else if (src == _rulesList) doSelectRule();
//    }
//
//    public void mousePressed(MouseEvent me) { }
//    public void mouseReleased(MouseEvent me) { }
//    public void mouseEntered(MouseEvent me) { }
//    public void mouseExited(MouseEvent me) { }
//    public void mouseClicked(MouseEvent me) {
//	Object src = me.getSource();
//	if (me.getClickCount() != 2) return;
//	if (src == _ruleLibList && _addRuleButton.isEnabled()) doAddRule();
//	if (src == _rulesList && _removeRuleButton.isEnabled()) doRemoveRule();
//    }
//
//}

