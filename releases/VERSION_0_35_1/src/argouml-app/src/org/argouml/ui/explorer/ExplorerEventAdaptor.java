/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoProfileEvent;
import org.argouml.application.events.ArgoProfileEventListener;
import org.argouml.configuration.Configuration;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.AddAssociationEvent;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.DeleteInstanceEvent;
import org.argouml.model.InvalidElementException;
import org.argouml.model.Model;
import org.argouml.model.RemoveAssociationEvent;
import org.argouml.model.UmlChangeEvent;
import org.argouml.notation.Notation;

/**
 * All events going to the Explorer must pass through here first!<p>
 *
 * Most will come from the uml model via the EventAdapter interface.<p>
 *
 * TODO: In some cases (test cases) this object is created without setting
 * the treeModel. I (Linus) will add tests for this now. It would be better
 * if this is created only when the Explorer is created. <p>
 *
 * TODO: The ExplorerTreeNode also listens to some events
 * (from Diagrams), so it does not follow the rule above.
 *
 * @since 0.15.2, Created on 16 September 2003, 23:13
 * @author  alexb
 */
public final class ExplorerEventAdaptor
    implements PropertyChangeListener {

    private static final Logger LOG =
        Logger.getLogger(ExplorerEventAdaptor.class.getName());

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
            instance = new ExplorerEventAdaptor();
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
        // TODO: We really only care about events which affect things that
        // are visible in the current perspective (view).  This could be
        // tailored to cut down on event traffic. - tfm 20060410
        Model.getPump().addClassModelEventListener(this,
                Model.getMetaTypes().getModelElement(), (String[]) null);
        ArgoEventPump.addListener(
                ArgoEventTypes.ANY_PROFILE_EVENT, new ProfileChangeListener());
    }

    /**
     * The tree structure has changed significantly.
     * Inform the associated tree model.
     *
     * TODO:  This shouldn't be public.  Components desiring to
     * inform the Explorer of changes should send events.
     * @deprecated by mvw in V0.25.4. Use events instead.
     */
    @Deprecated
    public void structureChanged() {
        if (treeModel == null) {
            return;
        }
        treeModel.structureChanged();
    }


    /**
     * forwards this event to the tree model.
     *
     * @param element the modelelement to be added
     *
     * TODO:  This shouldn't be public.  Components desiring to
     * inform the Explorer of changes should send events.
     */
    public void modelElementAdded(Object element) {
        if (treeModel == null) {
            return;
        }
        treeModel.modelElementAdded(element);
    }

    /**
     * forwards this event to the tree model.
     *
     * @param element the modelelement to be changed
     *
     * TODO:  This shouldn't be public.  Components desiring to
     * inform the Explorer of changes should send events.
     */
    public void modelElementChanged(Object element) {
        if (treeModel == null) {
            return;
        }
        treeModel.modelElementChanged(element);
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
     * @see PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(final PropertyChangeEvent pce) {
        if (treeModel == null) {
            return;
        }

        // uml model events
        if (pce instanceof UmlChangeEvent) {
            Runnable doWorkRunnable = new Runnable() {
                public void run() {
                    try {
                        modelChanged((UmlChangeEvent) pce);
                    } catch (InvalidElementException e) {
                        LOG.log(Level.FINE, "updateLayout method accessed deleted element", e);
                    }
                }
            };
            SwingUtilities.invokeLater(doWorkRunnable);

        } else if (pce.getPropertyName().equals(
                // TODO: No one should be sending the deprecated event
                // from outside ArgoUML, but keep responding to it for now
                // just in case
                ProjectManager.CURRENT_PROJECT_PROPERTY_NAME)
                || pce.getPropertyName().equals(
                        ProjectManager.OPEN_PROJECTS_PROPERTY)) {
            // project events
            if (pce.getNewValue() != null) {
                treeModel.structureChanged();
            }
            return;
        } else if (Notation.KEY_USE_GUILLEMOTS.isChangedProperty(pce)
            || Notation.KEY_SHOW_STEREOTYPES.isChangedProperty(pce)) {
            // notation events
            treeModel.structureChanged();
        } else if (pce.getSource() instanceof ProjectManager) {
            // Handle remove for non-UML elements (e.g. diagrams)
            if ("remove".equals(pce.getPropertyName())) {
                treeModel.modelElementRemoved(pce.getOldValue());
            }
        }
    }

    private void modelChanged(UmlChangeEvent event) {
        if (event instanceof AttributeChangeEvent) {
            // TODO: Can this be made more restrictive?
            // Do we care about any attributes other than name? - tfm
            treeModel.modelElementChanged(event.getSource());
        } else if (event instanceof RemoveAssociationEvent) {
            // TODO: This should really be coded the other way round,
            // to only act on associations which are important for
            // representing the current perspective (and to only act
            // on a single end of the association) - tfm
            if (!("namespace".equals(event.getPropertyName()))) {
                treeModel.modelElementChanged(((RemoveAssociationEvent) event)
                        .getChangedValue());
            }
        } else if (event instanceof AddAssociationEvent) {
            if (!("namespace".equals(event.getPropertyName()))) {
                treeModel.modelElementAdded(
                        ((AddAssociationEvent) event).getSource());
            }
        } else if (event instanceof DeleteInstanceEvent) {
            treeModel.modelElementRemoved(((DeleteInstanceEvent) event)
                    .getSource());
        }
    }


    /**
     * Listener for additions and removals of profiles.
     * Since they generally have a major impact on the explorer tree,
     * we simply update them completely.
     *
     * @author Michiel
     */
    class ProfileChangeListener implements ArgoProfileEventListener {

        public void profileAdded(ArgoProfileEvent e) {
            structureChanged();
        }

        public void profileRemoved(ArgoProfileEvent e) {
            structureChanged();
        }

    }
}
