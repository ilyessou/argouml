// $Id$
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.foundation.core.CoreFactory;
import org.argouml.swingext.GridLayout2;

import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLCheckBox;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLEnumerationBooleanProperty;
import org.argouml.uml.ui.UMLList;
import org.argouml.uml.ui.UMLRadioButton;
import org.argouml.uml.ui.UMLReflectionBooleanProperty;
import org.argouml.uml.ui.UMLReflectionListModel;
import org.argouml.uml.ui.UMLVisibilityPanel;
import org.argouml.util.ConfigLoader;

/** A property panel for operations.
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 */
public class PropPanelOperation extends PropPanelModelElement {

    ////////////////////////////////////////////////////////////////
    // contructors
    public PropPanelOperation() {
        super(
	      "Operation",
	      _operationIcon,
	      ConfigLoader.getTabPropsOrientation());

        Class mclass = (Class)ModelFacade.OPERATION;
        //
        //   this will cause the components on this page to be notified
        //      anytime a stereotype, namespace, operation, etc
        //      has its name changed or is removed anywhere in the model
        Class[] namesToWatch = {
	    (Class)ModelFacade.STEREOTYPE, 
	    (Class)ModelFacade.NAMESPACE, 
	    (Class)ModelFacade.CLASSIFIER 
	};
        setNameEventListening(namesToWatch);

        addField(Translator.localize("UMLMenu", "label.name"), getNameTextField());
//        addField(
//                Translator.localize("UMLMenu", "label.stereotype"),
//		 new UMLComboBoxNavigator(
//					  this,
//		         Translator.localize("UMLMenu", "tooltip.nav-stereo"),
//					  getStereotypeBox()));
        addField(Translator.localize("UMLMenu", "label.stereotype"), getStereotypeBox());

        JList ownerList = new UMLList(
				      new UMLReflectionListModel(
								 this,
								 "owner",
								 false,
								 "getOwner",
								 null,
								 null,
								 null),
				      true);
        ownerList.setBackground(getBackground());
        ownerList.setForeground(Color.blue);
        ownerList.setVisibleRowCount(1);
        JScrollPane ownerScroll =
            new JScrollPane(
			    ownerList,
			    JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        addField(Translator.localize("UMLMenu", "label.owner"), ownerScroll);

        addField(
                Translator.localize("UMLMenu", "label.visibility"),
		 new UMLVisibilityPanel(this, mclass, 2, false));

        addSeperator();

        JPanel modPanel =
            new JPanel(new GridLayout2(0, 2, GridLayout2.ROWCOLPREFERRED));
        modPanel.add(
		     new UMLCheckBox(
		             Translator.localize("UMLMenu", "checkbox.abstract-lc"),
				     this,
				     new UMLReflectionBooleanProperty(
								      "isAbstract",
								      mclass,
								      "isAbstract",
								      "setAbstract")));
        modPanel.add(
		     new UMLCheckBox(
		             Translator.localize("UMLMenu", "checkbox.final-lc"),
				     this,
				     new UMLReflectionBooleanProperty(
								      "isLeaf",
								      mclass,
								      "isLeaf",
								      "setLeaf")));
        modPanel.add(
		     new UMLCheckBox(
				     localize("root"),
				     this,
				     new UMLReflectionBooleanProperty(
								      "isRoot",
								      mclass,
								      "isRoot",
								      "setRoot")));
        modPanel.add(
		     new UMLCheckBox(
				     localize("query"),
				     this,
				     new UMLReflectionBooleanProperty(
								      "isQuery",
								      mclass,
								      "isQuery",
								      "setQuery")));
        modPanel.add(
		     new UMLCheckBox(
				     localize("static"),
				     this,
				     new UMLEnumerationBooleanProperty(
								       "ownerscope",
								       mclass,
								       "getOwnerScope",
								       "setOwnerScope",
								       ModelFacade.SCOPEKIND,
								       ModelFacade.CLASSIFIER_SCOPEKIND,
								       ModelFacade.INSTANCE_SCOPEKIND)));
        addField(Translator.localize("UMLMenu", "label.modifiers"), modPanel);

        JPanel concurPanel =
            new JPanel(new GridLayout2(0, 2, GridLayout2.ROWCOLPREFERRED));
        ButtonGroup group = new ButtonGroup();
        UMLRadioButton sequential =
            new UMLRadioButton(
			       "sequential",
			       this,
			       new UMLEnumerationBooleanProperty(
								 "concurrency",
								 mclass,
								 "getConcurrency",
								 "setConcurrency",
								 ModelFacade.CALLCONCURRENCYKIND,
								 ModelFacade.SEQUENTIAL_CONCURRENCYKIND,
								 null));
        group.add(sequential);
        concurPanel.add(sequential);
        UMLRadioButton synchd =
            new UMLRadioButton(
			       "guarded",
			       this,
			       new UMLEnumerationBooleanProperty(
								 "concurrency",
								 mclass,
								 "getConcurrency",
								 "setConcurrency",
								 ModelFacade.CALLCONCURRENCYKIND,
								 ModelFacade.GUARDED_CONCURRENCYKIND,
								 null));
        group.add(synchd);
        concurPanel.add(synchd);
        UMLRadioButton concur =
            new UMLRadioButton(
			       "concurrent",
			       this,
			       new UMLEnumerationBooleanProperty(
								 "concurrency",
								 mclass,
								 "getConcurrency",
								 "setConcurrency",
								 ModelFacade.CALLCONCURRENCYKIND,
								 ModelFacade.CONCURRENT_CONCURRENCYKIND,
								 null));
        group.add(concur);
        concurPanel.add(concur);
        addField("Concurrency:", concurPanel);

        addSeperator();

        JList paramList =
            new UMLList(
			new UMLReflectionListModel(
						   this,
						   "parameter",
						   true,
						   "getParameters",
						   "setParameters",
						   "addParameter",
						   null),
			true);
        paramList.setForeground(Color.blue);
        paramList.setFont(smallFont);
        addField(
                Translator.localize("UMLMenu", "label.parameters"),
		 new JScrollPane(paramList));

        JList exceptList =
            new UMLList(
			new UMLReflectionListModel(
						   this,
						   "signal",
						   true,
						   "getRaisedSignals",
						   "setRaisedSignals",
						   "addRaisedSignal",
						   null),
			true);
        exceptList.setForeground(Color.blue);
        exceptList.setFont(smallFont);
        addField(
                Translator.localize("UMLMenu", "label.raisedsignals"),
		 new JScrollPane(exceptList));

        new PropPanelButton(
			    this,
			    buttonPanel,
			    _navUpIcon,
				Translator.localize("UMLMenu", "button.go-up"),
			    "navigateUp",
			    null);
        new PropPanelButton(
			    this,
			    buttonPanel,
			    _addOpIcon,
				Translator.localize("UMLMenu", "button.new-operation"),
			    "buttonAddOperation",
			    null);
        // I uncommented this next line. I don't know why it was commented out, it seems to work just fine...--pjs--
        new PropPanelButton(
			    this,
			    buttonPanel,
			    _parameterIcon,
				Translator.localize("UMLMenu", "button.new-parameter"),
			    "buttonAddParameter",
			    null);
        new PropPanelButton(
			    this,
			    buttonPanel,
			    _signalIcon,
			    localize("New Raised Signal"),
			    "buttonAddRaisedSignal",
			    null);
        new PropPanelButton(
			    this,
			    buttonPanel,
			    _deleteIcon,
				Translator.localize("UMLMenu", "button.delete-operation"),
			    "removeElement",
			    null);
    }

    public Object getReturnType() {
        Object type = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            Collection params = org.argouml.model.ModelFacade.getParameters(target);
            if (params != null) {
                Iterator iter = params.iterator();
                Object param;
                while (iter.hasNext()) {
                    param = /*(MParameter)*/ iter.next();
                    if (ModelFacade.getKind(param) == ModelFacade.RETURN_PARAMETERDIRECTIONKIND) {
                        type = ModelFacade.getType(param);
                        break;
                    }
                }
            }
        }
        return type;
    }

    public void setReturnType(Object/*MClassifier*/ type) {
        Object target = getTarget();
        if (ModelFacade.isAOperation(target)) {
            Object oper = /*(MOperation)*/ target;
            Collection params = ModelFacade.getParameters(oper);
            Object param;
            //
            //   remove first (hopefully only) return parameters
            //
            if (type == null) {
                if (params != null) {
                    Iterator iter = params.iterator();
                    while (iter.hasNext()) {
                        param = /*(MParameter)*/ iter.next();
                        if (ModelFacade.getKind(param)
                            == ModelFacade.RETURN_PARAMETERDIRECTIONKIND) {
                            ModelFacade.removeParameter(oper, param);
                            break;
                        }
                    }
                }
            } else {
                Object retParam = null;
                if (params != null) {
                    Iterator iter = params.iterator();
                    while (iter.hasNext()) {
                        param = /*(MParameter)*/ iter.next();
                        if (ModelFacade.getKind(param)
                            == ModelFacade.RETURN_PARAMETERDIRECTIONKIND) {
                            retParam = param;
                            break;
                        }
                    }
                }
                if (retParam == null) {
                    retParam =
                        UmlFactory.getFactory().getCore().buildParameter(
									 oper,
									 ModelFacade.RETURN_PARAMETERDIRECTIONKIND);
                }
                ModelFacade.setType(retParam, type);
            }
        }
    }

    public Collection getParameters() {
        Collection params = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            params = org.argouml.model.ModelFacade.getParameters(target);
        }
        return params;
    }

    public void setParameters(Collection newParams) {
        Object target = getTarget();
        if (ModelFacade.isAOperation(target)) {
            ModelFacade.setParameters(target, newParams);
        }
    }

    public void addParameter(Integer indexObj) {
        buttonAddParameter();
    }

    public Object getOwner() {
        Object owner = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            owner = ModelFacade.getOwner(target);
        }
        return owner;
    }

    public Collection getRaisedSignals() {
        Collection signals = null;
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            signals = ModelFacade.getRaisedSignals(target);
        }
        return signals;
    }

    public void setRaisedSignals(Collection signals) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            ModelFacade.setRaisedSignals(target, signals);
        }
    }

    public void addRaisedSignal(Integer index) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            Object oper = /*(MOperation)*/ target;
            Object newSignal = UmlFactory.getFactory().getCommonBehavior().createSignal();//((MOperation)oper).getFactory().createSignal();
            ModelFacade.addOwnedElement(ModelFacade.getNamespace(ModelFacade.getOwner(oper)), newSignal);
            ModelFacade.addRaisedSignal(oper, newSignal);
            TargetManager.getInstance().setTarget(newSignal);
        }
    }

    public void buttonAddParameter() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            Object param =
                CoreFactory.getFactory().buildParameter(/*(MOperation)*/ target);
            TargetManager.getInstance().setTarget(param);
            /*
	      MOperation oper = (MOperation) target;
	      MParameter newParam = oper.getFactory().createParameter();
	      newParam.setKind(MParameterDirectionKind.INOUT);
	      oper.addParameter(newParam);
	      navigateTo(newParam);
            */
        }
    }

    public void buttonAddOperation() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            Object oper = /*(MOperation)*/ target;
            Object owner = ModelFacade.getOwner(oper);
            if (owner != null) {
                Object newOper =
                    UmlFactory.getFactory().getCore().buildOperation(owner);
                TargetManager.getInstance().setTarget(newOper);

            }
        }
    }

    public void buttonAddRaisedSignal() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAOperation(target)) {
            addRaisedSignal(new Integer(1));
        }
    }

    /**
     *   Appropriate namespace is the namespace of our class,
     *      not the class itself
     */
    protected Object getDisplayNamespace() {
        Object namespace = null;
        Object target = getTarget();
        if (ModelFacade.isAAttribute(target)) {
            if (ModelFacade.getOwner(target) != null) {
                namespace = ModelFacade.getNamespace(ModelFacade.getOwner(target));
            }
        }
        return namespace;
    }

} /* end class PropPanelOperation */