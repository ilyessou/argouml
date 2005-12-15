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

import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNavigate;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.PropPanel;
import org.argouml.util.ConfigLoader;

/**
 * This class represents the properties panel for a Diagram.
 *
 */
public class PropPanelDiagram extends PropPanel {

    /**
     * Constructs a proppanel with a given name.
     * @see org.argouml.ui.AbstractArgoJPanel#AbstractArgoJPanel(String)
     */
    protected PropPanelDiagram(String diagramName, ImageIcon icon) {
        super(diagramName, icon, ConfigLoader.getTabPropsOrientation());

        JTextField field = new JTextField();
        field.getDocument().addDocumentListener(new DiagramNameDocument(field));
        addField(Translator.localize("label.name"), field);

        JList lst = new OneRowLinkedList(new UMLDiagramHomeModelListModel());
        addField(Translator.localize("label.home-model"), new JScrollPane(lst));

        addAction(new ActionNavigateUpFromDiagram());
        addAction(new ActionDeleteSingleModelElement());
    }

    /**
     * Default constructor if there is no child of this class that can show the
     * diagram.
     */
    public PropPanelDiagram() {
        this("Diagram", null);
    }

    /**
     * @see org.argouml.uml.ui.PropPanel#removeElement()
     */
    public void removeElement() {
        Object target = getTarget();
        if (target instanceof ArgoDiagram) {
            try {
                ArgoDiagram diagram = (ArgoDiagram) target;
                Project project =
		    ProjectManager.getManager().getCurrentProject();
                //
                //  can't easily find owner of diagram
                //    set new target to the model
                //
                Object newTarget = project.getModel();
                project.moveToTrash(diagram);
                TargetManager.getInstance().setTarget(newTarget);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

} /* end class PropPanelDiagram */

class ActionNavigateUpFromDiagram extends AbstractActionNavigate {

    /**
     * The constructor.
     */
    public ActionNavigateUpFromDiagram() {
        super("button.go-up", true);
    }

    /**
     * @see org.argouml.uml.ui.AbstractActionNavigate#navigateTo(java.lang.Object)
     */
    protected Object navigateTo(Object source) {
        if (source instanceof UMLDiagram) {
            return ((UMLDiagram) source).getNamespace();
        }
        return null;
    }
    /**
     * @see javax.swing.Action#isEnabled()
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object target = TargetManager.getInstance().getTarget();
        Object destination = navigateTo(target);
        if (destination != null) {
            TargetManager.getInstance().setTarget(destination);
        }
    }
}

/**
 * The list model for the namespace of a diagram.
 *
 * @author mvw@tigris.org
 */
class UMLDiagramHomeModelListModel
    extends DefaultListModel
    implements TargetListener {

    /**
     * Constructor for UMLCommentAnnotatedElementListModel.
     */
    public UMLDiagramHomeModelListModel() {
        super();
        setTarget(TargetManager.getInstance().getTarget());
        TargetManager.getInstance().addTargetListener(this);
    }

    /**
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /**
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /**
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    private void setTarget(Object t) {
        UMLDiagram target = null;
        if (t instanceof UMLDiagram) {
            target = (UMLDiagram) t;
        }
        removeAllElements();

        Object ns = null;
        if (target != null) {
            ns = target.getNamespace();
        }
        if (ns != null) {
            addElement(ns);
        }
    }
}
