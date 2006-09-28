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

package org.argouml.uml.reveng;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;

import org.apache.log4j.Logger;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectMember;
import org.argouml.ui.ArgoDiagram;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.ProjectMemberDiagram;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.FigInterface;
import org.argouml.uml.diagram.static_structure.ui.FigPackage;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.presentation.Fig;

/**
 * Instances of this class interface the current Class diagram.
 * <p>
 * This class is used by the import mechanism to create packages,
 * interfaces and classes within the diagrams.
 * It is also used to find the correct diagram to work in.
 *
 * @author Andreas Rueckert
 * @since 0.9
 */
public class DiagramInterface {
    private static final Logger LOG =
        Logger.getLogger(DiagramInterface.class);

    private Editor currentEditor = null;

    /**
     * To know what diagrams we have to layout after the import,
     * we store them in this Vector.
     */
    private Vector modifiedDiagrams = new Vector();

    /**
     * The current GraphModel of the current classdiagram.
     */
    private ClassDiagramGraphModel currentGM;

    /**
     * The current Layer of the current classdiagram.
     */
    private LayerPerspective currentLayer;

    /**
     * The current diagram for the isInDiagram method.
     */
    private ArgoDiagram currentDiagram;

    /**
     * Creates a new DiagramInterface.
     *
     * @param editor The editor to operate on.
     */
    public DiagramInterface(Editor editor) {
  	currentEditor = editor;
    }

    /**
     * Get the current editor.
     *
     * @return The current editor.
     */
    Editor getEditor() {
	return currentEditor;
    }

    /**
     * Mark a diagram as modified, so we can layout it, after the
     * import is complete.<p>
     *
     * If the diagram is not already marked, add it to the list.<p>
     *
     * @param diagram The diagram to mark as modified.
     */
    void markDiagramAsModified(Object diagram) {
	if (!modifiedDiagrams.contains(diagram)) {
	    modifiedDiagrams.add(diagram);
	}
    }

    /**
     * Get the list of modified diagrams.
     *
     * @return The list of modified diagrams.
     */
    public Vector getModifiedDiagrams() {
	return modifiedDiagrams;
    }

    /**
     * Reset the list of modified diagrams.
     */
    void resetModifiedDiagrams() {
	modifiedDiagrams = new Vector();
    }

    /**
     * Add a package to the current diagram. If the package already has
     * a representation in the current diagram, it is not(!) added.
     *
     * @param newPackage The package to add.
     */
    public void addPackage(Object newPackage) {

        if (!isInDiagram(newPackage)) {

            FigPackage newPackageFig = new FigPackage(currentGM, newPackage);
            if (currentGM.canAddNode(newPackage)) {

                currentLayer.add(newPackageFig);
                currentGM.addNode(newPackage);
                currentLayer.putInPosition(newPackageFig);
            }
        }
    }

    /**
     * Check if a given package has a representation in the current
     * diagram.
     *
     * @param p The package to lookup in the current diagram.
     * @return true if this package has a figure in the current diagram,
     *         false otherwise.
     */
    public boolean isInDiagram(Object p) {

	if (currentDiagram == null)
            return false;
        else
            return currentDiagram.getNodes(null).contains(p);
    }

    /**
     * Check if this diagram already exists in the project.<p>
     *
     * @param name diagram name.
     * @return true if diagram exists in project.
     */
    public boolean isDiagramInProject(String name) {
        Project project = ProjectManager.getManager().getCurrentProject();
	return (findDiagramMemberByUniqueName(project,
            getDiagramName(name) + ".pgml")) != null;
    }

    /**
     * Create a diagram name from a package name
     *
     * @param packageName The package name.
     * @return The name for the diagram.
     */
    private String getDiagramName(String packageName) {
	return packageName.replace('.', '_') + "_classes";
    }

    /**
     * Select or create a class diagram for a package.
     *
     * @param p The package.
     * @param name The fully qualified name of this package.
     */
    public void selectClassDiagram(Object p, String name) {

	// Check if this diagram already exists in the project
	ProjectMember m;
	if (isDiagramInProject(name)) {
            Project project = ProjectManager.getManager().getCurrentProject();
	    m = findDiagramMemberByUniqueName(project,
                getDiagramName(name) + ".pgml");

	    // The diagram already exists in this project. Select it
	    // as the current target.
	    if (m instanceof ProjectMemberDiagram) {
		setCurrentDiagram(((ProjectMemberDiagram) m).getDiagram());
	    }

	} else {
	    // Otherwise create a new classdiagram for the package.
	    addClassDiagram(p, name);
	}
    }

    /**
     * Add a new class diagram for a package to the project.
     *
     * @param ns The package to attach the diagram to.
     * @param name The fully qualified name of the package, which is
     *             used to generate the diagram name from.
     */
    public void addClassDiagram(Object ns, String name) {
	Project p = ProjectManager.getManager().getCurrentProject();
	try {
        ArgoDiagram d =
            DiagramFactory.getInstance().createDiagram(
                    UMLClassDiagram.class,
                    ns,
                    null);


	    d.setName(getDiagramName(name));
            p.addMember(d);
	    setCurrentDiagram(d);
	}
	catch (PropertyVetoException pve) { }
    }

    /**
     * Add a class to the current diagram.
     *
     * @param newClass The new class to add to the editor.
     * @param minimise minimise the class fig by hiding compartiments
     *                 (of attributes and operations)
     */
    public void addClass(Object newClass, boolean minimise) {

        // if the class is not in the current diagram, add it:
        if (currentGM.canAddNode(newClass)) {

	    FigClass newClassFig = new FigClass(currentGM, newClass);
            currentLayer.add(newClassFig);
            currentGM.addNode(newClass);
            currentLayer.putInPosition(newClassFig);

            newClassFig.setAttributesVisible(!minimise);
            newClassFig.setOperationsVisible(!minimise);

            newClassFig.setSize(newClassFig.getMinimumSize());
        }
        // the class is in the diagram
        // so we are on a second pass,
        // find the fig for this class can update its visible state.
        else {
            FigClass existingFig = null;
            List figs = currentLayer.getContentsNoEdges();
            for (int i = 0; i < figs.size(); i++) {
                Fig fig = (Fig) figs.get(i);
                if (newClass == fig.getOwner())
                    existingFig = (FigClass) fig;
            }
            existingFig.renderingChanged();
        }

        // add edges
        // for a 2-pass r.e. process we might have already added the
        // class but not its edges
	currentGM.addNodeRelatedEdges(newClass);
    }

    /**
     * Add a interface to the current diagram.
     *
     * @param newInterface The interface to add.
     * @param minimise minimise the class fig by hiding compartiments
     *                 (of attributes and operations)
     */
    public void addInterface(Object newInterface, boolean minimise) {


        // if the Interface is not in the current diagram, add it:
        if (currentGM.canAddNode(newInterface)) {

            FigInterface     newInterfaceFig =
		new FigInterface(currentGM, newInterface);

            currentLayer.add(newInterfaceFig);
            currentGM.addNode(newInterface);
            currentLayer.putInPosition(newInterfaceFig);

            newInterfaceFig.setOperationsVisible(!minimise);
            newInterfaceFig.setSize(newInterfaceFig.getMinimumSize());
        }
        // the class is in the diagram
        // so we are on a second pass,
        // find the fig for this class can update its visible state.
        else {
            FigInterface existingFig = null;
            List figs = currentLayer.getContentsNoEdges();
            for (int i = 0; i < figs.size(); i++) {
                Fig fig = (Fig) figs.get(i);
                if (newInterface == fig.getOwner())
                    existingFig = (FigInterface) fig;
            }
            existingFig.renderingChanged();
        }

        // add edges
        // for a 2-pass r.e. process we might have already added the
        // interface but not its edges
	currentGM.addNodeRelatedEdges(newInterface);
    }

    /**
     * Creates new class diagram for package or selects existing one.
     * @param currentPackage  The package to attach the diagram to.
     * @param currentPackageName The fully qualified name of the
     * package, which is used to generate the diagram name from.
     */
    public void createOrSelectClassDiagram(Object currentPackage,
					   String currentPackageName) {
        Project p = ProjectManager.getManager().getCurrentProject();
        String diagramName = currentPackageName.replace('.', '_') + "_classes";
        ArgoDiagram d =
            DiagramFactory.getInstance().createDiagram(
                    UMLClassDiagram.class,
                    currentPackage == null ? p.getRoot() : currentPackage,
                    null);

	if (findDiagramMemberByUniqueName(p, diagramName + ".pgml") == null) {
	    try {
		d.setName(diagramName);
	    } catch (Exception e) {
		LOG.error("Failed to set diagram name.");
	    }
	    p.addMember(d);
	    setCurrentDiagram(d);
	} else {
	    ArgoDiagram ddi =
	        ((ProjectMemberDiagram) findDiagramMemberByUniqueName(p,
	                diagramName + ".pgml")).getDiagram();
	    setCurrentDiagram(ddi);
	}
    }

    /**
     * Creates class diagram under the root.
     * Is used for classes out of packages.
     *
     */
    public void createRootClassDiagram() {
	createOrSelectClassDiagram(null, "");
    }

    /**
     * selects a diagram without affecting the gui.
     *
     * @param diagram the diagram
     */
    public void setCurrentDiagram(ArgoDiagram diagram) {

        if (diagram == null) {
            throw new RuntimeException("you can't select a null diagram");
        }

        currentGM = (ClassDiagramGraphModel) diagram.getGraphModel();
        currentLayer = diagram.getLayer();
        currentDiagram = diagram;

        markDiagramAsModified(diagram);
    }

    /**
     * @param name the name of the member to be found
     * @param project the project
     * @return the member
     */
    public ProjectMember findDiagramMemberByUniqueName(Project project,
            String name) {
        ArrayList diagramMembers =
            project.getMembers().getMembers(ProjectMemberDiagram.class);
        for (int i = 0; i < diagramMembers.size(); i++) {
            ProjectMember pm = (ProjectMember) diagramMembers.get(i);
            if (name.equals(pm.getUniqueDiagramName())) {
                return pm;
            }
        }
        return null;
    }
}

























