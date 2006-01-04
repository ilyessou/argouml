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

package org.argouml.ui.explorer;

import java.beans.PropertyChangeListener;

import org.argouml.application.api.Configuration;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.AddAssociationEvent;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.DeleteInstanceEvent;
import org.argouml.model.Model;
import org.argouml.model.RemoveAssociationEvent;
import org.argouml.notation.Notation;

/**
 * All events going to the Explorer must pass through here first!<p>
 *
 * Most will come from the uml model via the EventAdapter interface.<p>
 *
 * TODO: In some cases (test cases) this object is created without setting
 * the treeModel. I (Linus) will add tests for this now. It would be better
 * if this is created only when the Explorer is created.
 *
 * @since 0.15.2, Created on 16 September 2003, 23:13
 * @author  alexb
 */
public final class ExplorerEventAdaptor
    implements PropertyChangeListener {
    /**
     * The singleton instance.
     *
     * TODO: Why is this a singleton? Wouldn't it be better to have exactly
     * one for every Explorer?
     */
    private static ExplorerEventAdaptor instance;

    /**
     * The tree model to update.
     */
    private TreeModelUMLEventListener treeModel;

    /**
     * @return the instance (singleton)
     */
    public static ExplorerEventAdaptor getInstance() {
        if (instance == null) {
            return instance = new ExplorerEventAdaptor();
	}
	return instance;
    }

    /**
     * Creates a new instance of ExplorerUMLEventAdaptor.
     */
    private ExplorerEventAdaptor() {

        Configuration.addListener(Notation.KEY_USE_GUILLEMOTS, this);
        Configuration.addListener(Notation.KEY_SHOW_STEREOTYPES, this);
        ProjectManager.getManager().addPropertyChangeListener(this);
        Model.getEventAdapter().addPropertyChangeListener(this);
    }

    /**
     * forwards this event to the tree model.
     */
    public void structureChanged() {
        if (treeModel == null) {
            return;
        }
        treeModel.structureChanged();
    }

    /**
     * forwards this event to the tree model.
     *
     * @param source the modelelement to be removed
     */
    public void modelElementRemoved(Object source) {
        if (treeModel == null) {
            return;
        }
        treeModel.modelElementRemoved(source);
    }

    /**
     * forwards this event to the tree model.
     *
     * @param source the modelelement to be added
     */
    public void modelElementAdded(Object source) {
        if (treeModel == null) {
            return;
        }
        treeModel.modelElementAdded(source);
    }

    /**
     * forwards this event to the tree model.
     *
     * @param source the modelelement to be changed
     */
    public void modelElementChanged(Object source) {
        if (treeModel == null) {
            return;
        }
        treeModel.modelElementChanged(source);
    }

    /**
     * sets the tree model that will receive events.
     *
     * @param newTreeModel the tree model to be used
     */
    public void setTreeModelUMLEventListener(
	    TreeModelUMLEventListener newTreeModel) {
        treeModel = newTreeModel;
    }

    /**
     * Listens to events coming from the project manager, config manager, and
     * uml model, passes those events on to the explorer model.
     *
     * @since ARGO0.11.2
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (treeModel == null) {
            return;
        }

        // We don't care if the save state has changed
        if (pce.getPropertyName().equals(
                ProjectManager.SAVE_STATE_PROPERTY_NAME)) {
            return;
        }

        // project events
        if (pce.getPropertyName()
                .equals(ProjectManager.CURRENT_PROJECT_PROPERTY_NAME)) {
            if (pce.getNewValue() != null) {
                treeModel.structureChanged();
            }
            return;
        }

        // notation events
        if (Notation.KEY_USE_GUILLEMOTS.isChangedProperty(pce)
            || Notation.KEY_SHOW_STEREOTYPES.isChangedProperty(pce)) {
            treeModel.structureChanged();
        }

        // uml model events
        if (pce instanceof RemoveAssociationEvent) {
            // TODO: This should really be coded the other way round,
            // to only act on properties that are recognized, rather
            // than exclude one or more, but I don't know what they
            // all are - tfm
            if (!("namespace".equals(pce.getPropertyName()))) {
                treeModel.modelElementChanged(((RemoveAssociationEvent) pce)
                        .getChangedValue());
            }
        }

        if (pce instanceof AddAssociationEvent) {
            if (!("namespace".equals(pce.getPropertyName()))) {
                treeModel.modelElementAdded(
                        ((AddAssociationEvent) pce).getSource());
            }
        }

        if (pce instanceof AttributeChangeEvent) {
            treeModel.modelElementChanged(pce.getSource());
        }

        if (pce instanceof DeleteInstanceEvent) {
            treeModel.modelElementRemoved(((DeleteInstanceEvent) pce)
                    .getSource());
        }

    }
}
