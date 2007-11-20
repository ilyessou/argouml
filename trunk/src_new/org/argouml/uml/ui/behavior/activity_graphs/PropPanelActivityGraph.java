// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.activity_graphs;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLComboBoxModel2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLModelElementListModel2;
import org.argouml.uml.ui.behavior.state_machines.PropPanelStateMachine;

/**
 * PropertyPanel for Activitygraphs. It inherits almost everything from
 * PropPanelStateMachine.
 */
public class PropPanelActivityGraph extends PropPanelStateMachine {

    /**
     * The constructor.
     */
    public PropPanelActivityGraph() {
        super("label.activity-graph-title", lookupIcon("ActivityGraph"));
    }
    
    @Override
    protected UMLComboBoxModel2 getContextComboBoxModel() {
        return new UMLActivityGraphContextComboBoxModel();
    }

    /**
     * @see org.argouml.uml.ui.behavior.state_machines.PropPanelStateMachine#initialize()
     */
    @Override
    protected void initialize() {
        super.initialize();
        
        addSeparator();
        
        JList partitionList = new UMLLinkedList(
                new UMLActivityGraphPartiitionListModel());
        addField(Translator.localize("label.partition"),
                new JScrollPane(partitionList));
    }
    
    /**
     * The model for the partitions of a ActivityGraph.
     *
     * @author Michiel
     */
    public class UMLActivityGraphPartiitionListModel
        extends UMLModelElementListModel2 {

        /**
         * Constructor for UMLActivityGraphPartiitionListModel.
         */
        public UMLActivityGraphPartiitionListModel() {
            super("partition");
        }

        /*
         * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
         */
        protected void buildModelList() {
            setAllElements(Model.getFacade().getPartitions(getTarget()));
        }

        /*
         * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(Object)
         */
        protected boolean isValidElement(Object element) {
            return Model.getFacade().getPartitions(getTarget())
                .contains(element);
        }

    }
}
