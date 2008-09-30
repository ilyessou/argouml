// $Id$
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

package org.argouml.uml.ui.foundation.core;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.model.AttributeChangeEvent;
import org.argouml.model.Model;
import org.argouml.model.UmlChangeEvent;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionNavigateOwner;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLComboBoxModel2;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLExpressionBodyField;
import org.argouml.uml.ui.UMLExpressionLanguageField;
import org.argouml.uml.ui.UMLExpressionModel2;
import org.argouml.uml.ui.UMLUserInterfaceContainer;
import org.tigris.gef.undo.UndoableAction;

/**
 * A property panel for methods.
 *
 * @author thn@tigris.org
 */
public class PropPanelMethod extends PropPanelFeature {

    private UMLComboBox2 specificationComboBox;
    private static UMLMethodSpecificationComboBoxModel 
    specificationComboBoxModel;

    /**
     * Construct a property panel for UML Method elements.
     */
    public PropPanelMethod() {
        super("label.method", lookupIcon("Method"));

        addField(Translator.localize("label.name"),
                getNameTextField());

        addField(Translator.localize("label.owner"),
                getOwnerScroll());

        /* The specification field shows the Operation: */
        addField(Translator.localize("label.specification"),
                new UMLComboBoxNavigator(
                        Translator
                            .localize("label.specification.navigate.tooltip"),
                        getSpecificationComboBox()));

        add(getVisibilityPanel());

        JPanel modifiersPanel = createBorderPanel(Translator.localize(
                "label.modifiers"));
        modifiersPanel.add(new UMLBehavioralFeatureQueryCheckBox());
        modifiersPanel.add(new UMLFeatureOwnerScopeCheckBox());
        add(modifiersPanel);

        addSeparator();

        UMLExpressionModel2 procedureModel = 
            new UMLMethodProcedureExpressionModel(
                this, "");
        addField(Translator.localize("label.language"),
                new UMLExpressionLanguageField(procedureModel,
                false));
        JScrollPane bodyPane = new JScrollPane(
                new UMLExpressionBodyField(
                        procedureModel, true));
        addField(Translator.localize("label.body"), bodyPane);

        addAction(new ActionNavigateOwner());
        addAction(getDeleteAction());
    }

    /**
     * @return the Specification ComboBox
     */
    public UMLComboBox2 getSpecificationComboBox() {
        if (specificationComboBox == null) {
            if (specificationComboBoxModel == null) {
                specificationComboBoxModel =
                    new UMLMethodSpecificationComboBoxModel();
            }
            specificationComboBox =
                new UMLComboBox2(
                        specificationComboBoxModel,
                                 new ActionSetMethodSpecification());
        }
        return specificationComboBox;
    }

    private static class UMLMethodSpecificationComboBoxModel
        extends UMLComboBoxModel2 {
        /**
         * Constructor.
         */
        public UMLMethodSpecificationComboBoxModel() {
            super("specification", false);
            Model.getPump().addClassModelEventListener(this,
                    Model.getMetaTypes().getOperation(), "method");
        }

        /*
         * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidElement(
         *         java.lang.Object)
         */
        protected boolean isValidElement(Object element) {
            Object specification =
                Model.getCoreHelper().getSpecification(getTarget());
            return specification == element;
        }

        /*
         * @see org.argouml.uml.ui.UMLComboBoxModel2#buildModelList()
         */
        protected void buildModelList() {
            if (getTarget() != null) {
                removeAllElements();
                Object classifier = Model.getFacade().getOwner(getTarget());
                addAll(Model.getFacade().getOperations(classifier));
            }
        }

        /*
         * @see org.argouml.uml.ui.UMLComboBoxModel2#getSelectedModelElement()
         */
        protected Object getSelectedModelElement() {
            return Model.getCoreHelper().getSpecification(getTarget());
        }

        /*
         * @see java.beans.PropertyChangeListener#propertyChange(
         *         java.beans.PropertyChangeEvent)
         */
        public void modelChanged(UmlChangeEvent evt) {
            if (evt instanceof AttributeChangeEvent) {
                if (evt.getPropertyName().equals("specification")) {
                    if (evt.getSource() == getTarget()
                            && (getChangedElement(evt) != null)) {
                        Object elem = getChangedElement(evt);
                        setSelectedItem(elem);
                    }
                }
            }
        }

    }

    private static class ActionSetMethodSpecification extends UndoableAction {

        /**
         * Constructor for ActionSetStructuralFeatureType.
         */
        protected ActionSetMethodSpecification() {
            super(Translator.localize("Set"), null);
            // Set the tooltip string:
            putValue(Action.SHORT_DESCRIPTION, 
                    Translator.localize("Set"));
        }

        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            Object source = e.getSource();
            Object oldOperation = null;
            Object newOperation = null;
            Object method = null;
            if (source instanceof UMLComboBox2) {
                UMLComboBox2 box = (UMLComboBox2) source;
                Object o = box.getTarget(); // the method
                if (Model.getFacade().isAMethod(o)) {
                    method = o;
                    oldOperation =
                        Model.getCoreHelper().getSpecification(method);
                }
                o = box.getSelectedItem(); // the selected operation
                if (Model.getFacade().isAOperation(o)) {
                    newOperation = o;
                }
            }
            if (newOperation != oldOperation && method != null) {
                Model.getCoreHelper().setSpecification(method, newOperation);
            }
        }
    }

}

/**
 * The model for the procedure expression of a Method.
 * 
 * @author Michiel
 */
class UMLMethodProcedureExpressionModel extends UMLExpressionModel2 {

    private static final Logger LOG =
        Logger.getLogger(UMLMethodProcedureExpressionModel.class);

    /**
     * The constructor.
     *
     * @param container the container of UML user interface components
     * @param propertyName the name of the property
     */
    public UMLMethodProcedureExpressionModel(
            UMLUserInterfaceContainer container,
            String propertyName) {
        super(container, propertyName);
    }

    /**
     * This returns a ProcedureExpression.
     * @see org.argouml.uml.ui.UMLExpressionModel2#getExpression()
     */
    public Object getExpression() {
        return Model.getFacade().getBody(
                TargetManager.getInstance().getTarget());
    }

    /**
     * Sets the "Body" of the target (which is a Method).
     * The Body is the ProcedureExpression, which consists 
     * of a body and language.
     * 
     * @param expression a ProcedureExpression
     * @see org.argouml.uml.ui.UMLExpressionModel2#setExpression(java.lang.Object)
     */
    public void setExpression(Object expression) {
        Object target = TargetManager.getInstance().getTarget();

        if (target == null) {
            throw new IllegalStateException("There is no target for "
                    + getContainer());
        }
        Model.getCoreHelper().setBody(target, expression);
    }

    /*
     * @see org.argouml.uml.ui.UMLExpressionModel2#newExpression()
     */
    public Object newExpression() {
        LOG.debug("new empty procedure expression");
        return Model.getDataTypesFactory().createProcedureExpression("", "");
    }

}
