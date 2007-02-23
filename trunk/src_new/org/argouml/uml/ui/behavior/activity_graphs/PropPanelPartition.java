// $Id$
// Copyright (c) 2004-2007 The Regents of the University of California. All
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


package org.argouml.uml.ui.behavior.activity_graphs;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractActionAddModelElement;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLModelElementListModel2;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;


/**
 * The properties panel for a Partition (Swimlane).
 *
 * @author mkl
 */
public class PropPanelPartition extends PropPanelModelElement {

    private JScrollPane contentsScroll;
    private JScrollPane activityGraphScroll;

    private static UMLPartitionContentListModel contentListModel =
        new UMLPartitionContentListModel("contents");

    /**
     * constructor.
     */
    public PropPanelPartition() {
        super("Swimlane",  lookupIcon("Partition"),
      	      ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"), getNameTextField());
        
        JList activityGraphList = new UMLLinkedList(
                new UMLPartitionActivityGraphListModel());
        activityGraphList.setVisibleRowCount(1);
        activityGraphScroll = new JScrollPane(activityGraphList);
        addField(Translator.localize("label.activity-graph"), 
                getActivityGraphField());

        addSeparator();

        addField(Translator.localize("label.contents"), getContentsField());

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNewStereotype());
        addAction(getDeleteAction());
    }

    /**
     * @return a textfield for the name
     */
    protected JComponent getContentsField() {
        if (contentsScroll == null) {
            JList contentList = new UMLMutableLinkedList(
                    contentListModel, 
                    new ActionAddPartitionContent(),
                    null);
            contentsScroll = new JScrollPane(contentList);
        }
        return contentsScroll;
    }
    
    protected JScrollPane getActivityGraphField() {
        return activityGraphScroll;
    }

    class ActionAddPartitionContent extends AbstractActionAddModelElement {

        public ActionAddPartitionContent() {
            super();
            setMultiSelect(true);
        }

        protected void doIt(Vector selected) {
            Object partition = getTarget();
            if (Model.getFacade().isAPartition(partition)) {
                Model.getActivityGraphsHelper().setContents(
                        partition, selected);
            }
        }

        protected Vector getChoices() {
            Vector ret = new Vector();
            if (Model.getFacade().isAPartition(getTarget())) {
                Object partition = getTarget();
                Object ag = Model.getFacade().getActivityGraph(partition);
                if (ag != null) {
                    Object top = Model.getFacade().getTop(ag);
                    /* There are no composite states, so this will work: */
                    ret.addAll(Model.getFacade().getSubvertices(top));
                }
            }
            return ret;
        }

        protected String getDialogTitle() {
            return Translator.localize("dialog.title.add-contents");
        }

        protected Vector getSelected() {
            Vector ret = new Vector();
            ret.addAll(Model.getFacade().getContents(getTarget()));
            return ret;
        }

    }
}

class UMLPartitionContentListModel extends  UMLModelElementListModel2 {

    public UMLPartitionContentListModel(String name) {
        super(name);
    }

    protected void buildModelList() {
        Object partition = getTarget();
        setAllElements(Model.getFacade().getContents(partition));
    }

    protected boolean isValidElement(Object element) {
        if (!Model.getFacade().isAModelElement(element)) {
            return false;
        }
        Object partition = getTarget();
        return Model.getFacade().getContents(partition).contains(element);
    }
    
}

class UMLPartitionActivityGraphListModel extends UMLModelElementListModel2
{

    /**
     * Constructor for UMLStateVertexIncomingListModel.
     */
    public UMLPartitionActivityGraphListModel() {
        super("activityGraph");
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        removeAllElements();
        addElement(Model.getFacade().getActivityGraph(getTarget()));
    }

    /*
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object element) {
        return Model.getFacade().getActivityGraph(getTarget()) == element;
    }
}