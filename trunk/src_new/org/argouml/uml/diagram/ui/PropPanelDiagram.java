// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

import javax.swing.JTextField;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNavigate;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.util.ConfigLoader;

/**
 * This class represents the properties panel for a Diagram.
 *
 */
public class PropPanelDiagram extends PropPanel {

    private JTextField field;

    /**
     * Constructs a proppanel with a given name.
     * @see org.argouml.ui.TabSpawnable#TabSpawnable(String)
     */
    protected PropPanelDiagram(String diagramName) {
        super(diagramName, ConfigLoader.getTabPropsOrientation());

        field = new JTextField();
        field.getDocument().addDocumentListener(new DiagramNameDocument(field));

        addField(Translator.localize("label.name"), field);


        new PropPanelButton(this,
                ResourceLoaderWrapper.lookupIconResource("NavigateUp"),
                Translator.localize("button.go-up"),
                new ActionNavigateUpFromDiagram());
        new PropPanelButton(this,
                ResourceLoaderWrapper.lookupIconResource("Delete"),
                Translator.localize("button.delete"),
                new ActionRemoveFromModel());
    }

    /**
     * Default constructor if there is no child of this class that can show the
     * diagram.
     */
    public PropPanelDiagram() {
        this("Diagram");
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
