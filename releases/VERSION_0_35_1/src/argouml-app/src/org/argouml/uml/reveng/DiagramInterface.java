/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
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

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.DiagramElement;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.uml.diagram.static_structure.ui.FigClassifierBox;
import org.argouml.uml.diagram.ui.FigCompartment;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.presentation.Fig;

/**
 * Instances of this class interface the current Class diagram.<p>
 *
 * This class is used by the import mechanism to create packages,
 * interfaces and classes within the diagrams.
 * It is also used to find the correct diagram to work in.
 *
 * @author Andreas Rueckert
 * @since 0.9
 */
public class DiagramInterface {

    private static final char DIAGRAM_NAME_SEPARATOR = '_';
    private static final String DIAGRAM_NAME_SUFFIX = "classes";

    private static final Logger LOG =
        Logger.getLogger(DiagramInterface.class.getName());

    private Editor currentEditor;

    /**
     * To know what diagrams we have to layout after the import,
     * we store them in this list.
     */
    private List<ArgoDiagram> modifiedDiagrams =
        new ArrayList<ArgoDiagram>();

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

    private Project currentProject;

    /**
     * Creates a new DiagramInterface.
     *
     * @param editor The editor to operate on.
     */
    public DiagramInterface(Editor editor) {
  	currentEditor = editor;
  	LayerPerspective layer =
  	    (LayerPerspective) editor.getLayerManager().getActiveLayer();
  	currentProject = ((ArgoDiagram) layer.getDiagram()).getProject();
    }

    /**
     * Creates a new DiagramInterface.
     *
     * @param editor The editor to operate on.
     * @param project the project being operated on
     */
    public DiagramInterface(Editor editor, Project project) {
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
    void markDiagramAsModified(ArgoDiagram diagram) {
        if (!modifiedDiagrams.contains(diagram)) {
            modifiedDiagrams.add(diagram);
        }
    }


    /**
     * Get the list of modified diagrams.
     *
     * @return The list of modified diagrams.
     */
    public List<ArgoDiagram> getModifiedDiagramList() {
        return modifiedDiagrams;
    }

    /**
     * Reset the list of modified diagrams.
     */
    void resetModifiedDiagrams() {
	modifiedDiagrams = new ArrayList<ArgoDiagram>();
    }

    /**
     * Add a package to the current diagram. If the package already has
     * a representation in the current diagram, it is not(!) added.
     *
     * @param newPackage The package to add.
     */
    public void addPackage(Object newPackage) {
        if (!isInDiagram(newPackage)) {
            if (currentGM.canAddNode(newPackage)) {
                DiagramElement newPackageFig =
                    currentDiagram.createDiagramElement(
                        newPackage,
                        new Rectangle(0, 0, 0, 0));
                currentLayer.add((FigNodeModelElement) newPackageFig);
                currentGM.addNode(newPackage);
                currentLayer.putInPosition((FigNodeModelElement) newPackageFig);
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
	if (currentDiagram == null) {
            return false;
        } else {
            return currentDiagram.getNodes().contains(p);
        }
    }

    /**
     * Check if this diagram already exists in the project.<p>
     *
     * @param name package name (converted to class name)
     * @return true if diagram exists in project.
     */
    public boolean isDiagramInProject(String name) {
        if (currentProject == null) {
            throw new RuntimeException("current project not set yet");
        }
        return currentProject.getDiagram(getDiagramName(name)) != null;
    }

    /**
     * Create a diagram name from a package name.
     *
     * @param packageName The package name.
     * @return The name for the diagram.
     */
    private String getDiagramName(String packageName) {
        /*
         * TODO: This transformation is Java specific. We need a more
         * language/notation scheme for specifying qualified names.
         * Possible algorithm - replace all punctuation with our
         * internal separator, replace multiple separators with a single
         * instance (for languages like C++).  What about I18N? - tfm
         */
	return packageName.replace('.', DIAGRAM_NAME_SEPARATOR)
                + DIAGRAM_NAME_SEPARATOR + DIAGRAM_NAME_SUFFIX;
    }

    /**
     * Select a class diagram as the current diagram, creating it
     * if necessary.
     *
     * @param p The package.
     * @param name The fully qualified name of this package.
     */
    public void selectClassDiagram(Object p, String name) {
        // Check if this diagram already exists in the project
        if (currentProject == null) {
            throw new RuntimeException("current project not set yet");
        }
        ArgoDiagram m = currentProject.getDiagram(getDiagramName(name));
        if (m != null) {
            // The diagram already exists in this project. Select it
            // as the current target.
            setCurrentDiagram(m);
        } else {
            // Otherwise create a new classdiagram for the package.
            addClassDiagram(p, name);
        }
    }

    /**
     * Add a new class diagram for a package to the project.
     *
     * @param ns
     *            The namespace to contain the diagram. If null, the root model
     *            will be used.
     * @param name
     *            The fully qualified name of the package, which is used to
     *            generate the diagram name from.
     */
    public void addClassDiagram(Object ns, String name) {
        if (currentProject == null) {
            throw new RuntimeException("current project not set yet");
        }
        ArgoDiagram d = DiagramFactory.getInstance().createDiagram(
                DiagramFactory.DiagramType.Class,
                ns == null ? currentProject.getRoot() : ns, null);

        try {
            d.setName(getDiagramName(name));
        } catch (PropertyVetoException pve) {
            LOG.log(Level.SEVERE, "Failed to set diagram name.", pve);
        }
        currentProject.addMember(d);
        setCurrentDiagram(d);
    }

    /**
     * Add a class to the current diagram.
     *
     * @param newClass The new class to add to the editor.
     * @param minimise minimise the class fig by hiding compartiments
     *                 (of attributes and operations)
     */
    public void addClass(Object newClass, boolean minimise) {
        addClassifier(newClass, minimise);
    }

    /**
     * Add a classier to the current diagram.
     *
     * @param classifier The new class or interface to add to the editor.
     * @param minimise minimise the class fig by hiding compartments
     *                 (of attributes and operations)
     */
    private void addClassifier(Object classifier, boolean minimise) {
        // if the classifier is not in the current diagram, add it:
        if (currentGM.canAddNode(classifier)) {
            FigClassifierBox newFig =
                (FigClassifierBox) currentDiagram.createDiagramElement(
                        classifier,
                        new Rectangle(0, 0, 0, 0));

            /*
             * The following calls are ORDER DEPENDENT. Not sure why, but the
             * layer add must come before the model add or we'll end up with
             * duplicate figures in the diagram. - tfm
             */
            currentLayer.add(newFig);
            currentGM.addNode(classifier);
            currentLayer.putInPosition(newFig);

            if (Model.getUmlFactory().isContainmentValid(
                    Model.getMetaTypes().getOperation(),
                    classifier)) {
                FigCompartment ops =
                    newFig.getCompartment(Model.getMetaTypes().getOperation());
                newFig.setCompartmentVisible(ops, !minimise);
            }
            if (Model.getUmlFactory().isContainmentValid(
                    Model.getMetaTypes().getAttribute(),
                    classifier)) {
                FigCompartment atts =
                    newFig.getCompartment(Model.getMetaTypes().getAttribute());
                newFig.setCompartmentVisible(atts, !minimise);
            }

            newFig.renderingChanged();
        } else {
            // the class is in the diagram
            // so we are on a second pass,
            // find the fig for this class can update its visible state.
            FigClassifierBox existingFig = null;
            List figs = currentLayer.getContentsNoEdges();
            for (int i = 0; i < figs.size(); i++) {
                Fig fig = (Fig) figs.get(i);
                if (classifier == fig.getOwner()) {
                    existingFig = (FigClassifierBox) fig;
                }
            }
            existingFig.renderingChanged();
        }

        // add edges
        // for a 2-pass r.e. process we might have already added the
        // class but not its edges
	currentGM.addNodeRelatedEdges(classifier);
    }

    /**
     * Add a interface to the current diagram.
     *
     * @param newInterface The interface to add.
     * @param minimise minimise the class fig by hiding compartiments
     *                 (of attributes and operations)
     */
    public void addInterface(Object newInterface, boolean minimise) {
        addClassifier(newInterface, minimise);
    }


    /**
     * Creates class diagram under the root.
     * Is used for classes out of packages.
     *
     */
    public void createRootClassDiagram() {
	selectClassDiagram(null, "");
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
        currentProject = diagram.getProject();

        markDiagramAsModified(diagram);
    }

}





















