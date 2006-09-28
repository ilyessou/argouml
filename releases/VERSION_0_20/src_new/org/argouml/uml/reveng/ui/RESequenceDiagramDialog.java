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

package org.argouml.uml.reveng.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.ArgoDialog;
import org.argouml.ui.CheckboxTableModel;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.explorer.ExplorerEventAdaptor;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.sequence.MessageNode;
import org.argouml.uml.diagram.sequence.SequenceDiagramGraphModel;
import org.argouml.uml.diagram.sequence.ui.FigClassifierRole;
import org.argouml.uml.diagram.sequence.ui.FigMessage;
import org.argouml.uml.diagram.sequence.ui.SequenceDiagramLayout;
import org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.argouml.uml.reveng.java.JavaLexer;
import org.argouml.uml.reveng.java.JavaRecognizer;
import org.argouml.uml.reveng.java.Modeller;
import org.argouml.uml.ui.ActionBaseDelete;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Mode;
import org.tigris.gef.presentation.Fig;

/**
 * The dialog that starts the reverse engineering of operations.<br>
 * TODO: subsequent parsing of further operation bodies <br>
 * TODO: suppressing multiple creation of already created messages<br>
 * TODO: processing of non-constructor-calls to other classifiers<br>
 * TODO: i18n<br>
 * TODO: ...<br>
 */
public class RESequenceDiagramDialog extends ArgoDialog implements ActionListener, ItemListener {

    private final static int X_OFFSET = 10;

    /**
     * Constructor.
     *
     * @param operation The operation that should be reverse engineered.
     */
    public RESequenceDiagramDialog(Object operation) {
        this(operation, null);
    }

    /**
     * Constructor.
     *
     * @param operation The operation that should be reverse engineered.
     * @param figMessage the message figure where the result will be drawn to
     */
    public RESequenceDiagramDialog(Object operation, FigMessage figMessage) {
        super(
                ProjectBrowser.getInstance(),
                "NOT FUNCTIONAL!!! " +
                Translator.localize("dialog.title.reverse-engineer-sequence-diagram")
                 + (operation != null ? (' ' + Model.getFacade().getName(operation) + "()") : ""),
                ArgoDialog.OK_CANCEL_OPTION,
                true);
            setResizable(false);

            _operation = operation;
            _model = ProjectManager.getManager().getCurrentProject().getModel();
            try {
                _modeller = new Modeller(_model, null, null, true, true, null);
            } catch (Exception ex) {}

            _classifier = Model.getFacade().getOwner(_operation);
            if (figMessage != null) {
                _isNewSequenceDiagram = false;
                _graphModel = (SequenceDiagramGraphModel)Globals.curEditor().getGraphModel();
                _collaboration = _graphModel.getCollaboration();
                // maybe there is an easier way to find the current diagram than this:
                Project p = ProjectManager.getManager().getCurrentProject();
                Iterator iter = p.getDiagrams().iterator();
                while (iter.hasNext()) {
                    _diagram = (UMLDiagram)iter.next();
                    if (_graphModel == _diagram.getGraphModel()) {
                        break;
                    }
                }
                _classifierRole = getClassifierRole(_classifier, "obj");
                FigClassifierRole dest = figMessage.getDestFigClassifierRole();
                _portCnt = SequenceDiagramLayout.getNodeIndex(
                    figMessage.getDestMessageNode().getFigMessagePort().getY());
                Enumeration enu = _diagram.elements();
                while (enu.hasMoreElements()) {
                    Object f = enu.nextElement();
                    if (f instanceof FigClassifierRole) {
                        int x = ((Fig)f).getX();
                        if (_maxXPos < x) {
                            _maxXPos = x;
                        }
                        if (Model.getFacade().getName(((Fig)f).getOwner()).startsWith("anon")) {
                            _anonCnt++;
                        }
                    } else if (f instanceof FigMessage) {
                        dest = ((FigMessage)f).getDestFigClassifierRole();
                        int port = SequenceDiagramLayout.getNodeIndex(
                            ((FigMessage)f).getDestMessageNode().getFigMessagePort().getY());
                        if (_maxPort < port) {
                            _maxPort = port;
                        }
                    }
                }
            } else {
                _isNewSequenceDiagram = true;
                buildSequenceDiagram(_classifier);
                _classifierRole = getClassifierRole(_classifier, "obj");
                _maxXPos = _classifierRole.getX();
            }
            parseBody();

            JPanel contentPanel = getContentPanel();
            setContent(contentPanel);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        if (e.getSource() == getOkButton()) {
            for (int i = 0; i < _callTable.getRowCount(); i++) {
                if (Boolean.TRUE.equals((Boolean)_callTable.getValueAt(i, 1))) {
                    buildAction((String)_callTable.getValueAt(i, 0));
                }
            }
        } else if (e.getSource() == getCancelButton() && _isNewSequenceDiagram) {
            // remove SD and clean up everything
            Project p = ProjectManager.getManager().getCurrentProject();
            Object newTarget = null;
            if (ActionBaseDelete.sureRemove(_diagram)) {
                // remove from the model
                newTarget = getNewTarget(_diagram);
                p.moveToTrash(_diagram);
                p.moveToTrash(_collaboration);
            }
            if (newTarget != null) {
                TargetManager.getInstance().setTarget(newTarget);
            }
        }
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
		if (_modeChoice.getSelectedIndex() != 1) {
			_changingPanel.remove(_autoPanel);
			_changingPanel.add(_manuPanel, BorderLayout.CENTER);
			pack();
		} else {
			_changingPanel.remove(_manuPanel);
			_changingPanel.add(_autoPanel, BorderLayout.CENTER);
			pack();
		}
    }

    /**
     * Gets the object that should be target after the given target is
     * deleted from the model.
     *
     * @param target the target to delete
     * @return The object.
     */
    private Object getNewTarget(Object target) {
        Project p = ProjectManager.getManager().getCurrentProject();
        Object newTarget = null;
        if (target instanceof Fig) {
            target = ((Fig) target).getOwner();
        }
        if (Model.getFacade().isAModelElement(target)
                && Model.getFacade().getNamespace(target) != null) {
            newTarget = Model.getFacade().getNamespace(target);
        } else if (target instanceof Diagram) {
            Diagram firstDiagram = (Diagram) p.getDiagrams().get(0);
            if (target != firstDiagram) {
                newTarget = firstDiagram;
            } else {
                if (p.getDiagrams().size() > 1) {
                    newTarget = p.getDiagrams().get(1);
                } else {
                    newTarget = p.getRoot();
                }
            }
        } else {
            newTarget = p.getRoot();
        }
        return newTarget;
    }

    /**
     * Gets the content panel, containing all the gui.
     * @return the constructed panel
     */
    private JPanel getContentPanel() {
		JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;

		constraints.insets = new Insets(2, 2, 2, 2);
        content.add(new JLabel("Mode:"), constraints);

		constraints.gridy = 1;
		_modeChoice = new JComboBox();
		_modeChoice.addItem("Manually select calls of this operation");
		_modeChoice.addItem("Traverse calls automatically with a chosen depth");
		_modeChoice.addItemListener(this);
		content.add(_modeChoice, constraints);

		_manuPanel = getManuallyTab();
		_autoPanel = getAutomaticallyTab();

		constraints.gridy = 2;
        _changingPanel = new JPanel(new BorderLayout(0, 0));
		_changingPanel.add(_manuPanel, BorderLayout.CENTER);
        content.add(_changingPanel, constraints);

		return content;
    }

    /**
     * Gets the panel of the automatically tab.
     * @return the constructed panel
     */
    private JPanel getAutomaticallyTab() {
        JPanel top = new JPanel();
        top.setLayout(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.gridy = 0;
        labelConstraints.gridx = 0;
        labelConstraints.insets = new Insets(10, 2, 2, 2);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.NONE;
        fieldConstraints.gridy = 0;
        fieldConstraints.gridx = 1;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(4, 2, 2, 2);

        JPanel depthPanel = new JPanel(new FlowLayout());
        JRadioButton unlimited = new JRadioButton("unlimited");
        JRadioButton limited = new JRadioButton("limit to", true);
        ButtonGroup group = new ButtonGroup();
        group.add(unlimited);
        group.add(limited);
        depthPanel.add(limited);
        depthPanel.add(new JSpinner(new SpinnerNumberModel(1, 0, 999, 1)));
        depthPanel.add(unlimited);

        labelConstraints.gridy = 0;
        fieldConstraints.gridy = 0;
        top.add(new JLabel("Depth:"), labelConstraints);
        top.add(depthPanel, fieldConstraints);

        labelConstraints.gridy = 1;
        fieldConstraints.gridy = 1;
        top.add(new JLabel("Assumption table:"), labelConstraints);
        top.add(new JButton("Update"), fieldConstraints);

        ArrayList assumptions = new ArrayList();
        assumptions.add("calls.hasMoreElements()");
        assumptions.add("methods != null && !methods.isEmpty()");
        Object[] data = null;
        JTable table = new JTable(new CheckboxTableModel(
            assumptions.toArray(),
            data,
            "Conditions",
            "Assume true"));
        table.setShowVerticalLines(true);

        fieldConstraints.gridx = 0;
        fieldConstraints.gridy = 2;
        fieldConstraints.gridwidth = 2;
        fieldConstraints.anchor = GridBagConstraints.CENTER;
        fieldConstraints.fill = GridBagConstraints.BOTH;
        fieldConstraints.weighty = 1.0;
        top.add(new JScrollPane(table), fieldConstraints);

        fieldConstraints.insets = new Insets(0, 2, 0, 2);
        JCheckBox checkbox1 = new JCheckBox("also process create calls", true);
        fieldConstraints.gridy = 3;
        top.add(checkbox1, fieldConstraints);
        JCheckBox checkbox2 = new JCheckBox("also process local calls", true);
        fieldConstraints.gridy = 4;
        top.add(checkbox2, fieldConstraints);
        JCheckBox checkbox3 = new JCheckBox("also process calls inside package", true);
        fieldConstraints.gridy = 5;
        top.add(checkbox3, fieldConstraints);

        return top;
    }

    /**
     * Gets the panel of the manually tab.
     * @return the constructed panel
     */
    private JPanel getManuallyTab() {
        JPanel top = new JPanel();
        top.setLayout(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.insets = new Insets(10, 2, 2, 2);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.NONE;
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 0;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(4, 2, 2, 2);

        top.add(new JLabel("Method call table:"), labelConstraints);

        _callTable = new CheckboxTableModel(
            _calls.toArray(),
            _calldata.toArray(),
            "Method calls",
            "Enable");
        JTable table = new JTable(_callTable);
        table.setShowVerticalLines(true);

        fieldConstraints.gridx = 0;
        fieldConstraints.gridy = 1;
        fieldConstraints.gridwidth = 2;
        fieldConstraints.anchor = GridBagConstraints.CENTER;
        fieldConstraints.fill = GridBagConstraints.BOTH;
        fieldConstraints.weighty = 1.0;
        top.add(new JScrollPane(table), fieldConstraints);

        fieldConstraints.insets = new Insets(0, 2, 0, 2);
        JCheckBox checkbox1 = new JCheckBox("(un)check create calls", true);
        fieldConstraints.gridy = 2;
        top.add(checkbox1, fieldConstraints);
        JCheckBox checkbox2 = new JCheckBox("(un)check local calls", true);
        fieldConstraints.gridy = 3;
        top.add(checkbox2, fieldConstraints);
        JCheckBox checkbox3 = new JCheckBox("(un)check package calls", true);
        fieldConstraints.gridy = 4;
        top.add(checkbox3, fieldConstraints);
        JCheckBox checkbox4 = new JCheckBox("(un)check far calls", true);
        fieldConstraints.gridy = 5;
        top.add(checkbox4, fieldConstraints);

        return top;
    }

    /**
     * Buiids the sequence diagram for a classifier.
     */
    private void buildSequenceDiagram(Object classifier) {
        _collaboration =
            Model.getCollaborationsFactory().buildCollaboration(
                Model.getFacade().getNamespace(classifier),
                classifier);
        _diagram =
            (UMLDiagram)DiagramFactory.getInstance().createDiagram(
                UMLSequenceDiagram.class,
                _collaboration,
                null);
        _graphModel = (SequenceDiagramGraphModel)_diagram.getGraphModel();
        ProjectManager.getManager().getCurrentProject().addMember(_diagram);
        TargetManager.getInstance().setTarget(_diagram);
    }

    /**
     * Gets or builds the figure of the classifier role for a given classifier
     * and object name.
     */
    private FigClassifierRole getClassifierRole(Object classifier, String objName) {
        FigClassifierRole crFig = null;
        // first check if the fig of the classifier role already exists
        Collection coll = _diagram.getLayer().getContents();
        Iterator iter = coll != null ? coll.iterator() : null;
        while (iter != null && iter.hasNext()) {
            Object fig = iter.next();
            if (fig instanceof FigClassifierRole) {
                Object elem = ((FigClassifierRole)fig).getOwner();
                Collection bases = Model.getFacade().getBases(elem);
                if (Model.getFacade().isAClassifierRole(elem)
                     && Model.getFacade().getName(elem).equals(objName)
                     && bases != null && bases.contains(classifier)) {
                    // yes found, so this will be returned
                    crFig = (FigClassifierRole)fig;
                    break;
                }
            }
        }
        if (crFig == null) {
            // classifier role does not exists, so create a new one
            Object node = Model.getCollaborationsFactory().buildClassifierRole(_collaboration);
            if (objName != null) {
                Model.getCoreHelper().setName(node, objName);
            } else {
                Model.getCoreHelper().setName(node, "anon" + (++_anonCnt));
            }
            coll = new ArrayList();
            coll.add(classifier);
            Model.getCollaborationsHelper().setBases(node, coll);
            crFig = new FigClassifierRole(node);

            // location must be set for correct automatic layouting (how funny)
            // otherwise, the new classifier role is not the rightmost
            _maxXPos += X_OFFSET;
            crFig.setLocation(_maxXPos, 0);

            _diagram.add(crFig);
            _graphModel.addNode(node);
            ExplorerEventAdaptor.getInstance().modelElementChanged(
                Model.getFacade().getNamespace(_classifier));
        }
        return crFig;
    }

    /**
     * Parses a body of the actual operation.
     */
    private void parseBody() {
        JavaLexer lexer = null;
        JavaRecognizer parser = null;
        _calls.clear();
        _types.clear();
        _modeller.clearMethodCalls();
        _modeller.clearLocalVariableDeclarations();
        try {
            lexer = new JavaLexer(new StringReader('{' + getBody(_operation) + '}'));
            lexer.setTokenObjectClass("org.argouml.uml.reveng.java.ArgoToken");
            parser = new JavaRecognizer(lexer);
            parser.setModeller(_modeller);
            parser.setParserMode(JavaRecognizer.MODE_REVENG_SEQUENCE);
        } catch (Exception ex) {}
        if (_modeller != null && parser != null) {
            try {
                parser.compoundStatement();
            } catch (Exception ex) {
                LOG.debug("Parsing method body failed:", ex);
            }
            Collection methodCalls = _modeller.getMethodCalls();
            if (methodCalls != null) {
                _calls.addAll(methodCalls);
                Hashtable types = _modeller.getLocalVariableDeclarations();
                if (types != null) {
                    _types.putAll(types);
                }
            }
        }
    }

    /**
     * Gets the (first) body of an operation.
     */
    private static String getBody(Object operation) {
        String body = null;
        Collection methods = Model.getFacade().getMethods(operation);
        if (methods != null && !methods.isEmpty()) {
            Object expression = Model.getFacade().getBody(methods.iterator().next());
            body = (String)Model.getFacade().getBody(expression);
        }
        if (body == null) {
            body = "";
        }
        return body;
    }

    /**
     * Builds the complete action and its target classifier role (if not existing).
     */
    private Object buildAction(String call) {
        Object action = null;
        StringBuffer sb = new StringBuffer(call);
        int findpos = sb.lastIndexOf(".");
        int createPos = sb.indexOf("new ");
        boolean isCreate = createPos != -1 && (createPos == 0 || sb.charAt(createPos - 1) == '=');
        if (!isCreate && findpos == -1) {
            // call of a method of the class
            action = buildEdge(call, _classifierRole, _classifierRole, Model.getMetaTypes().getCallAction());
        } else if (!isCreate && findpos <= 5 && (call.startsWith("super.") || call.startsWith("this."))){
            // also call of a method of the class, but prefixed with "super." or "this."
            action = buildEdge(call, _classifierRole, _classifierRole, Model.getMetaTypes().getCallAction());
        } else {
            String type = null;
            if (isCreate) {
                // creator (constructor) call
                type = sb.substring(createPos + 4);
                String objName = createPos >= 2 ? sb.substring(0, createPos - 1) : null;
                Object cls = getClassifierFromModel(type, objName);
                FigClassifierRole endFig = getClassifierRole(cls, objName);
                action = buildEdge(
                    sb.substring(createPos),
                    _classifierRole,
                    endFig,
                    Model.getMetaTypes().getCreateAction());
            } else {
                String teststring = call.substring(0, findpos);
                type = (String)_types.get(teststring);
                if (type != null) {
                    Object cls = getClassifierFromModel(type, teststring);
                    FigClassifierRole endFig = getClassifierRole(cls, teststring);
                    action = buildEdge(call, _classifierRole, endFig, Model.getMetaTypes().getCallAction());
                }
            }
            if (type != null) {
                // call of a method of a local object
                // or call of a static method of a classifier
            } else {
                // unknown type
            }
        }
        return action;
    }

    /**
     * Builds the edge figure for an action.
     */
    private FigMessage buildEdge(String call, FigClassifierRole startFig, FigClassifierRole endFig, Object callType) {
        FigMessage figEdge = null;
        SequenceDiagramLayout lay = (SequenceDiagramLayout)_diagram.getLayer();
        int n = startFig == endFig ? 2 : 1;
        if (_portCnt < _maxPort) {
            lay.expandDiagram(_portCnt + 1, n);
        }
        MessageNode startPort = startFig.getNode(_portCnt + 1);
        MessageNode foundPort = endFig.getNode(_portCnt + n);
        _portCnt += n;
        _maxPort += n;
        Fig startPortFig = startFig.getPortFig(startPort);
        Fig destPortFig = endFig.getPortFig(foundPort);
        Object edgeType = Model.getMetaTypes().getMessage();
        Editor ce = Globals.curEditor();
        Hashtable args = new Hashtable();
        args.put("action", callType);
        Mode mode = (Mode)ce.getModeManager().top();
        mode.setArgs(args);
        Object newEdge = _graphModel.connect(startPort, foundPort, edgeType);
        if (null != newEdge) {
            Model.getCoreHelper().setName(newEdge, call);
            figEdge = (FigMessage)lay.presentationFor(newEdge);
            figEdge.setSourcePortFig(startPortFig);
            figEdge.setSourceFigNode(startFig);
            figEdge.setDestPortFig(destPortFig);
            figEdge.setDestFigNode(endFig);
            endFig.updateEdges();
            if (startFig != endFig) {
                startFig.updateEdges();
            }
        }
        return figEdge;
    }

    /**
     * Gets or builds a classifier role from a type. The type is a classifier
     * name, either fully qualified (with whole package path) or not.
     * Also ensures that there is an association to the actual classifier.
     */
    private Object getClassifierFromModel(String type, String objName) {
        FigClassifierRole crFig = null;
        Object classifier = null;
        int pos = type.lastIndexOf(".");
        if (pos != -1) {
            // full package path given, so let's get it from the model
            Object namespace = _model;
            pos = 0;
            StringTokenizer st = new StringTokenizer(type, ".");
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                pos += s.length();
                Object element = Model.getFacade().lookupIn(namespace, s);
                if (element == null) {
                    // package/classifier is missing, so create one
                    if (st.hasMoreTokens()) {
                        // must be a package
                        element = Model.getModelManagementFactory().buildPackage(s, type.substring(0, pos));
                    } else {
                        // must be a classifier, let's assume a class
                        element = Model.getCoreFactory().buildClass(s);
                    }
                    Model.getCoreHelper().setNamespace(element, namespace);
                    Model.getCoreHelper().addOwnedElement(namespace, element);
                }
                namespace = element;
                pos++;
            }
            classifier = namespace;
        } else {
            // classifier without package information given
            // first, let's look in the namespace of the actual classifier
            Object namespace = Model.getFacade().getNamespace(_classifier);
            classifier = Model.getFacade().lookupIn(namespace, type);
            if (!Model.getFacade().isAClassifier(classifier)) {
                classifier = null;
                // let's search for it in the imports (component dependencies)
                Collection sdeps = Model.getFacade().getSupplierDependencies(_classifier);
                Iterator iter1 = sdeps != null ? sdeps.iterator() : null;
                while(classifier == null && iter1 != null && iter1.hasNext()) {
                    Object dep = iter1.next();
                    if (Model.getFacade().isADependency(dep)) {
                        Collection clients = Model.getFacade().getClients(dep);
                        Iterator iter2 = clients != null ? clients.iterator() : null;
                        while(classifier == null && iter2 != null && iter2.hasNext()) {
                            Object comp = iter2.next();
                            if (Model.getFacade().isAComponent(comp)) {
                                classifier = permissionLookup(comp, type);
                            }
                        }
                    }
                }
            }
        }
        if (classifier == null) {
            // not found any matching classifier, so create one, and put it
            // into the namespace of the actual classifier
            classifier = Model.getCoreFactory().buildClass(type);
            Object namespace = Model.getFacade().getNamespace(_classifier);
            Model.getCoreHelper().setNamespace(classifier, namespace);
            Model.getCoreHelper().addOwnedElement(namespace, classifier);
        }
        ensureDirectedAssociation(_classifier, classifier);
        return classifier;
    }

    /**
     * Checks if there is a directed association between two classifiers, and
     * creates one if necessary.
     */
    private void ensureDirectedAssociation(Object fromCls, Object toCls) {
        String fromName = Model.getFacade().getName(fromCls);
        String toName = Model.getFacade().getName(toCls);
        Object assocEnd = null;
        for (Iterator i = Model.getFacade().getAssociationEnds(toCls).iterator(); i.hasNext();) {
            Object ae = i.next();
            if (Model.getFacade().getType(Model.getFacade().getOppositeEnd(ae)) == fromCls
                 && Model.getFacade().getName(ae) == null
                 && Model.getFacade().isNavigable(ae)) {
                assocEnd = ae;
            }
        }
        if (assocEnd == null) {
            String assocName = fromName + " -> " + toName;
            Object assoc = Model.getCoreFactory().buildAssociation(fromCls, false, toCls, true, assocName);
        }

    }

    /**
     * Get the classifier with the given name from a permission
     * (null if not found).
     */
    private Object permissionLookup(Object comp, String clsName) {
        Object classifier = null;
        Collection cdeps = Model.getFacade().getClientDependencies(comp);
        Iterator iter1 = cdeps != null ? cdeps.iterator() : null;
        while(classifier == null && iter1 != null && iter1.hasNext()) {
            Object perm = iter1.next();
            if (Model.getFacade().isAPermission(perm)) {
                Collection suppliers = Model.getFacade().getSuppliers(perm);
                Iterator iter2 = suppliers != null ? suppliers.iterator() : null;
                while(classifier == null && iter2 != null && iter2.hasNext()) {
                    Object elem = iter2.next();
                    if (Model.getFacade().isAClassifier(elem)
                         && clsName.equals(Model.getFacade().getName(elem))) {
                        classifier = elem;
                    }
                }
            }
        }
        return classifier;
    }

    private Object _model = null;
    private Modeller _modeller = null;
    private Object _classifier = null;
    private Object _operation = null;
    private FigClassifierRole _classifierRole = null;
    private ArrayList _calls = new ArrayList();
    private ArrayList _calldata = new ArrayList();
    private ArrayList _conditions = new ArrayList();
    private Hashtable _types = new Hashtable();
    private Object _collaboration = null;
    private UMLDiagram _diagram = null;
    private SequenceDiagramGraphModel _graphModel = null;
    private Object default_state = null;
    private Object default_state_machine = null;
    private CheckboxTableModel _callTable = null;
    private CheckboxTableModel _assumptionTable = null;
	private JComboBox _modeChoice = null;
	private JPanel _changingPanel = null;
	private JPanel _manuPanel = null;
	private JPanel _autoPanel = null;
    private int _maxXPos = -X_OFFSET;
    private int _maxPort = 0;
    private int _portCnt = 0;
    private int _anonCnt = 0;
    private boolean _isNewSequenceDiagram = false;

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(RESequenceDiagramDialog.class);

} /* end class RESequenceDiagramDialog */
