// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.core;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.ActionNavigateUpNextDown;
import org.argouml.uml.ui.ActionNavigateUpPreviousDown;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLExpressionBodyField;
import org.argouml.uml.ui.UMLExpressionLanguageField;
import org.argouml.uml.ui.UMLExpressionModel2;
import org.argouml.uml.ui.UMLUserInterfaceContainer;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;

/**
 * The properties panel for an Attribute of a Classifier, 
 * and the Qualifier of an AssociationEnd.
 *
 * @author jrobbins
 * @author jaap.branderhorst
 */
public class PropPanelAttribute extends PropPanelStructuralFeature {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = -5596689167193050170L;

    /**
     * The constructor.
     *
     */
    public PropPanelAttribute() {
        super("label.attribute", lookupIcon("Attribute"));

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.type"),
                new UMLComboBoxNavigator(
                        Translator.localize("label.class.navigate.tooltip"),
                        getTypeComboBox()));
        addField(Translator.localize("label.multiplicity"),
                getMultiplicityComboBox());
        addField(Translator.localize("label.owner"),
                getOwnerScroll());

        addSeparator();
        
        add(getVisibilityPanel());
        add(getChangeabilityRadioButtonPanel());

        JPanel modifiersPanel = createBorderPanel(
                Translator.localize("label.modifiers"));
        modifiersPanel.add(getOwnerScopeCheckbox());
        add(modifiersPanel);

        UMLExpressionModel2 initialModel = new UMLInitialValueExpressionModel(
                this, "initialValue");
        JPanel initialPanel = createBorderPanel(Translator
                .localize("label.initial-value"));
        initialPanel.add(new JScrollPane(new UMLExpressionBodyField(
                initialModel, true)));
        initialPanel.add(new UMLExpressionLanguageField(initialModel,
                false));
        add(initialPanel);

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNavigateUpPreviousDown() {
            public List getFamily(Object parent) {
                if (Model.getFacade().isAAssociationEnd(parent)) {
                    return Model.getFacade().getQualifiers(parent);
                }
                return Model.getFacade().getAttributes(parent);
            }

            public Object getParent(Object child) {
                return Model.getFacade().getModelElementContainer(child);
            }
        });
        addAction(new ActionNavigateUpNextDown() {
            public List getFamily(Object parent) {
                if (Model.getFacade().isAAssociationEnd(parent)) {
                    return Model.getFacade().getQualifiers(parent);
                }
                return Model.getFacade().getAttributes(parent);
            }

            public Object getParent(Object child) {
                return Model.getFacade().getModelElementContainer(child);
            }
        });
        addAction(new ActionAddAttribute());
        addAction(new ActionAddDataType());
        addAction(new ActionAddEnumeration());
        addAction(new ActionNewStereotype());
        addAction(getDeleteAction());
    }
    
    private class UMLInitialValueExpressionModel extends UMLExpressionModel2 {

        /**
         * The constructor.
         *
         * @param container the container of UML user interface components
         * @param propertyName the name of the property
         */
        public UMLInitialValueExpressionModel(
                UMLUserInterfaceContainer container,
                String propertyName) {
            super(container, propertyName);
        }

        /*
         * @see org.argouml.uml.ui.UMLExpressionModel2#getExpression()
         */
        public Object getExpression() {
            Object target = getTarget();
            if (target == null) {
                return null;
            }
            return Model.getFacade().getInitialValue(target);
        }

        /*
         * @see org.argouml.uml.ui.UMLExpressionModel2#setExpression(java.lang.Object)
         */
        public void setExpression(Object expression) {
            Object target = getTarget();
    
            if (target == null) {
                throw new IllegalStateException(
                        "There is no target for " + getContainer());
            }
            Model.getCoreHelper().setInitialValue(target, expression);
        }
    
        /*
         * @see org.argouml.uml.ui.UMLExpressionModel2#newExpression()
         */
        public Object newExpression() {
            return Model.getDataTypesFactory().createExpression("", "");
        }

    }

}

