/* $Id$
 *******************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling - Original implementation
 *******************************************************************************
 */

package org.argouml.core.propertypanels.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Command;
import org.argouml.kernel.NonUndoableCommand;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLAddDialog;
import org.argouml.util.ArgoFrame;

/**
 * Property getters and setters for UML1.4
 * @author Bob Tarling
 */
class GetterSetterManagerImpl extends GetterSetterManager {
    
    private static final Logger LOG =
        Logger.getLogger(GetterSetterManagerImpl.class);
    
    /**
     * The constructor
     */
    public GetterSetterManagerImpl(String type) {
        build(type);
    }
    
    /**
     * Create all the getter/setters for this implementation
     */
    private void build(String type) {
        addGetterSetter("isAbstract", new AbstractGetterSetter());
        addGetterSetter("isLeaf", new LeafGetterSetter());
        addGetterSetter("isRoot", new RootGetterSetter());
        addGetterSetter("isActive", new ActiveGetterSetter());
        addGetterSetter("ownerScope", new OwnerScopeGetterSetter());
        addGetterSetter("targetScope", new TargetScopeGetterSetter());
        addGetterSetter("isQuery", new QueryGetterSetter());
        addGetterSetter("isNavigable", new NavigableGetterSetter());
        addGetterSetter("isSynchronous", new AsynchronousGetterSetter());
        addGetterSetter("isSynch", new SynchGetterSetter());
        addGetterSetter("ordering", new OrderingGetterSetter());
        addGetterSetter("navigable", new NavigableGetterSetter());
        addGetterSetter("derived", new DerivedGetterSetter());
        addGetterSetter("visibility", new VisibilityGetterSetter());
        addGetterSetter("aggregation", new AggregationGetterSetter());
        addGetterSetter("kind", new ParameterDirectionGetterSetter());
        addGetterSetter("changeability", new ChangeabilityGetterSetter());
        addGetterSetter("concurrency", new ConcurrencyGetterSetter());
        addGetterSetter("feature", new FeatureGetterSetter());
        addGetterSetter("parameter", new ParameterGetterSetter());
        addGetterSetter("receiver", new ReceiverGetterSetter());
        addGetterSetter("sender", new SenderGetterSetter());
        addGetterSetter("body", new MethodExpressionGetterSetter());
        addGetterSetter("ownedElement", new OwnedElementGetterSetter());
        addGetterSetter("raisedSignal", new RaisedExceptionGetterSetter());
        addGetterSetter("raisedException", new RaisedExceptionGetterSetter());
        addGetterSetter("method", new MethodGetterSetter());
        addGetterSetter("message", new MessageGetterSetter());
        addGetterSetter("actualArgument", new ArgumentGetterSetter());
        addGetterSetter("extensionPoint", new ExtensionPointGetterSetter());
        addGetterSetter("guard", new GuardGetterSetter());
        addGetterSetter("effect", new EffectGetterSetter());
        addGetterSetter("trigger", new TriggerGetterSetter());
        addGetterSetter("elementImport", new ElementImportGetterSetter());
        addGetterSetter("templateParameter", new TemplateParameterGetterSetter());
        
        // UML2 only
        addGetterSetter("ownedOperation", new FeatureGetterSetter());
    }
    
    /**
     * Helper method for adding a new getter/setter
     * @param propertyName
     * @param bgs
     */
    private void addGetterSetter(String propertyName, BaseGetterSetter bgs) {
        getterSetterByPropertyName.put(propertyName, bgs);
    }
    
    /**
     * Set a UML property by property name
     * @param handle the element to which a property must be set
     * @param value the new property value
     * @param propertyName the property name
     */
    public void set(Object handle, Object value, String propertyName) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs != null) {
            bgs.set(handle, value);
        }
    }
    
    /**
     * Get a UML property by property name
     * @param handle the element from which a property must be return
     * @param value the new property value
     * @param propertyName the property name
     */
    public Object get(Object handle, String propertyName, String type) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs != null) {
            return bgs.get(handle, type);
        }
        
        return null;
    }
    
    public Collection getOptions(
            final Object umlElement,
            final String propertyName,
            final String type) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof OptionGetterSetter) {
            return ((OptionGetterSetter) bgs).getOptions(umlElement, type);
        }
        
        return null;
    }
    
    public boolean isFullBuildOnly(
            final String propertyName) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof ListGetterSetter) {
            return ((ListGetterSetter) bgs).isFullBuildOnly();
        }
        
        return false;
    }
    
    
    public Object create(String propertyName, String language, String body) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof ExpressionGetterSetter) {
            return ((ExpressionGetterSetter) bgs).create(language, body);
        }
        
        return null;
    }
    
    public boolean isValidElement(
            final String propertyName,
            final String type,
            final Object element) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof ListGetterSetter) {
            return ((ListGetterSetter) bgs).isValidElement(element, type);
        }
        
        return false;
    }
    
    public Object getMetaType(String propertyName) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof ListGetterSetter) {
            return ((ListGetterSetter) bgs).getMetaType();
        }
        
        return null;
    }
    

	@Override
	public Command getAddCommand(String propertyName, Object umlElement) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof Addable) {
            return ((Addable) bgs).getAddCommand(umlElement);
        }
		return null;
	}

	@Override
	public Command getRemoveCommand(String propertyName, Object umlElement, Object objectToRemove) {
        BaseGetterSetter bgs = getterSetterByPropertyName.get(propertyName);
        if (bgs instanceof Removeable) {
            return ((Removeable) bgs).getRemoveCommand(umlElement, objectToRemove);
        }
		return null;
	}
	
	private interface Addable {
		Command getAddCommand(Object umlElement);
	}
    
	private interface Removeable {
		Command getRemoveCommand(Object umlElement, Object objectToRemove);
	}
    
    
    /**
     * The getter/setter for the Absrtact property
     * @author Bob Tarling
     */
    private class AbstractGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isAbstract(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setAbstract(modelElement, (Boolean) value);
        }
    }
    
    private class LeafGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isLeaf(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setLeaf(modelElement, (Boolean) value);
        }
    }
    
    private class RootGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isRoot(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setRoot(modelElement, (Boolean) value);
        }
    }
    
    private class ActiveGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isActive(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setActive(modelElement, (Boolean) value);
        }
    }
    
    private class OwnerScopeGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isStatic(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setStatic(modelElement, (Boolean) value);
        }
    }
    
    private class TargetScopeGetterSetter extends BaseGetterSetter {
        // Have we handled UML2 here?
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isStatic(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setStatic(modelElement, (Boolean) value);
        }
    }
    
    private class QueryGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isQuery(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setQuery(modelElement, (Boolean) value);
        }
    }
    
    private class NavigableGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isNavigable(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setNavigable(modelElement, (Boolean) value);
        }
    }
    
    private class AsynchronousGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isAsynchronous(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getCommonBehaviorHelper().setAsynchronous(modelElement, (Boolean) value);
        }
    }
    
    private class SynchGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().isSynch(modelElement);
        }
        public void set(Object modelElement, Object value) {
            Model.getActivityGraphsHelper().setSynch(modelElement, (Boolean) value);
        }
    }
    
    private class OrderingGetterSetter extends BaseGetterSetter {
        public Object get(Object modelElement, String type) {
            return Model.getFacade().getOrdering(modelElement) ==
                Model.getOrderingKind().getOrdered();
        }
        public void set(Object modelElement, Object value) {
            if ((Boolean) value) {
                Model.getCoreHelper().setOrdering(modelElement,
                        Model.getOrderingKind().getOrdered());
            } else {
                Model.getCoreHelper().setOrdering(modelElement,
                        Model.getOrderingKind().getUnordered());
            }
        }
    }
    
    private class DerivedGetterSetter extends BaseGetterSetter {
        
        /**
         * Derived is not a true UML property but is in fact a pseudo property
         * stored in a tag named "derived"
         */
        private static final String TagName = "derived";
        
        public Object get(Object modelElement, String type) {
            Object tv = Model.getFacade().getTaggedValue(modelElement, TagName);
            if (tv != null) {
                String tag = Model.getFacade().getValueOfTag(tv);
                return ("true".equals(tag));
            }
            return false;
        }
        public void set(Object modelElement, Object value) {
            Object taggedValue = Model.getFacade().getTaggedValue(modelElement, TagName);
            if (taggedValue == null) {
                taggedValue =
                        Model.getExtensionMechanismsFactory().buildTaggedValue(
                                TagName, "");
                Model.getExtensionMechanismsHelper().addTaggedValue(
                        modelElement, taggedValue);
            }
            if ((Boolean) value) {
                Model.getCommonBehaviorHelper().setValue(taggedValue, "true");
            } else {
                Model.getCommonBehaviorHelper().setValue(taggedValue, "false");
            }
        }
    }
    
    
    public class VisibilityGetterSetter extends OptionGetterSetter {
        
        /**
         * Identifier for public visibility.
         */
        public static final String PUBLIC = "public";

        /**
         * Identifier for protected visibility.
         */
        public static final String PROTECTED = "protected";

        /**
         * Identifier for private visibility.
         */
        public static final String PRIVATE = "private";

        /**
         * Identifier for package visibility.
         */
        public static final String PACKAGE = "package";
        
        public VisibilityGetterSetter() {
            setOptions(Arrays.asList((new String[] {PUBLIC, PACKAGE, PROTECTED, PRIVATE})));
        }
        
        public Object get(Object modelElement, String type) {
            Object kind = Model.getFacade().getVisibility(modelElement);
            if (kind == null) {
                return null;
            } else if (kind.equals(Model.getVisibilityKind().getPublic())) {
                return PUBLIC;
            } else if (kind.equals(Model.getVisibilityKind().getPackage())) {
                return PACKAGE;
            } else if (kind.equals(Model.getVisibilityKind().getProtected())) {
                return PROTECTED;
            } else if (kind.equals(Model.getVisibilityKind().getPrivate())) {
                return PRIVATE;
            } else {
                return PUBLIC;
            }
        }
        
        public void set(Object modelElement, Object value) {
            Object kind = null;
            if (value.equals(PUBLIC)) {
                kind = Model.getVisibilityKind().getPublic();
            } else if (value.equals(PROTECTED)) {
                kind = Model.getVisibilityKind().getProtected();
            } else if (value.equals(PACKAGE)) {
                kind = Model.getVisibilityKind().getPackage();
            } else {
                kind = Model.getVisibilityKind().getPrivate();
            }
            Model.getCoreHelper().setVisibility(modelElement, kind);
        }
    }
    
    
    private class AggregationGetterSetter extends OptionGetterSetter {
        
        /**
         * Identifier for aggregate aggregation kind.
         */
        public static final String AGGREGATE = "aggregate";

        /**
         * Identifier for composite aggregation kind.
         */
        public static final String COMPOSITE = "composite";

        /**
         * Identifier for no aggregation kind.
         */
        public static final String NONE = "none";
        
        public AggregationGetterSetter() {
            setOptions(Arrays.asList(new String[] {AGGREGATE, COMPOSITE, NONE}));
        }
        
        public Object get(Object modelElement, String type) {
            Object kind = Model.getFacade().getAggregation(modelElement);
            if (kind == null) {
                return null;
            } else if (kind.equals(Model.getAggregationKind().getNone())) {
                return NONE;
            } else if (kind.equals(Model.getAggregationKind().getAggregate())) {
                return AGGREGATE;
            } else if (kind.equals(Model.getAggregationKind().getComposite())) {
                return COMPOSITE;
            } else {
                return NONE;
            }
        }
        
        public void set(Object modelElement, Object value) {
            Object kind = null;
            
            if (value.equals(AGGREGATE)) {
                kind = Model.getAggregationKind().getAggregate();
            } else if (value.equals(COMPOSITE)) {
                kind = Model.getAggregationKind().getComposite();
            } else {
                kind = Model.getAggregationKind().getNone();
            }
            Model.getCoreHelper().setAggregation(modelElement, kind);
            
        }
    }
    
    private class ParameterDirectionGetterSetter extends OptionGetterSetter {
        
        /**
         * Identifier for an "in" parameter.
         */
        public static final String IN = "in";

        /**
         * Identifier for an "out" parameter.
         */
        public static final String OUT = "out";

        /**
         * Identifier for an "in/out" parameter.
         */
        public static final String INOUT = "inout";

        /**
         * Identifier for a "return" parameter.
         */
        public static final String RETURN = "return";
        
        public ParameterDirectionGetterSetter() {
            setOptions(Arrays.asList(new String[] {
                    IN,
                    OUT,
                    INOUT,
                    RETURN}));
        }
        
        public Object get(Object modelElement, String type) {
            Object kind = Model.getFacade().getKind(modelElement);
            if (kind == null) {
                return null;
            } else if (kind.equals(Model.getDirectionKind().getInParameter())) {
                return IN;
            } else if (kind.equals(Model.getDirectionKind().getInOutParameter())) {
                return INOUT;
            } else if (kind.equals(Model.getDirectionKind().getOutParameter())) {
                return OUT;
            } else {
                return RETURN;
            }
        }
        
        public void set(Object modelElement, Object value) {
            Object kind = null;
            if (value == null) {
                kind = null;
            } else if (value.equals(IN)) {
                kind = Model.getDirectionKind().getInParameter();
            } else if (value.equals(OUT)) {
                kind = Model.getDirectionKind().getOutParameter();
            } else if (value.equals(INOUT)) {
                kind = Model.getDirectionKind().getInOutParameter();
            } else if (value.equals(RETURN)) {
                kind = Model.getDirectionKind().getReturnParameter();
            }
            Model.getCoreHelper().setKind(modelElement, kind);
            
        }
    }
    
    
    private class ConcurrencyGetterSetter extends OptionGetterSetter {
        
        /**
         * Identifier for sequential concurrency.
         */
        public static final String SEQUENTIAL= "sequential";

        /**
         * Identifier for guarded concurrency.
         */
        public static final String GUARDED = "guarded";

        /**
         * Identifier for concurrent concurrency.
         */
        public static final String CONCURRENT = "concurrent";

        public ConcurrencyGetterSetter() {
            setOptions(Arrays.asList(new String[] {
                    SEQUENTIAL,
                    GUARDED,
                    CONCURRENT}));
        }
        
        public Object get(Object modelElement, String type) {
            Object kind = Model.getFacade().getConcurrency(modelElement);
            if (kind == null) {
                return null;
            } else if (kind.equals(Model.getConcurrencyKind().getSequential())) {
                return SEQUENTIAL;
            } else if (kind.equals(Model.getConcurrencyKind().getGuarded())) {
                return GUARDED;
            } else if (kind.equals(Model.getConcurrencyKind().getConcurrent())) {
                return CONCURRENT;
            } else {
                return SEQUENTIAL;
            }
        }
        
        public void set(Object modelElement, Object value) {
            Object kind = null;
            if (value.equals(SEQUENTIAL)) {
                kind = Model.getConcurrencyKind().getSequential();
            } else if (value.equals(GUARDED)) {
                kind = Model.getConcurrencyKind().getGuarded();
            } else {
                kind = Model.getConcurrencyKind().getConcurrent();
            }
            Model.getCoreHelper().setConcurrency(modelElement, kind);
        }
    }
    
    
    private class ChangeabilityGetterSetter extends OptionGetterSetter {
        
        /**
         * Identifier for addonly changeability.
         * TODO: Note this should not be ni UML2 version
         */
        public static final String ADDONLY = "addonly";

        /**
         * CHANGEABLE_COMMAND determines a changeability kind.
         */
        public static final String CHANGEABLE = "changeable";

        /**
         * FROZEN_COMMAND determines a changeability kind.
         */
        public static final String FROZEN = "frozen";

        public ChangeabilityGetterSetter() {
            setOptions(Arrays.asList(new String[] {ADDONLY, CHANGEABLE, FROZEN}));
        }
        
        public Object get(Object modelElement, String type) {
            Object kind = Model.getFacade().getChangeability(modelElement);
            if (kind == null) {
                return null;
            } else if (kind.equals(Model.getChangeableKind().getAddOnly())) {
                return ADDONLY;
            } else if (kind.equals(Model.getChangeableKind().getChangeable())) {
                return CHANGEABLE;
            } else if (kind.equals(Model.getChangeableKind().getFrozen())) {
                return FROZEN;
            } else {
                return CHANGEABLE;
            }
        }
        
        public void set(Object modelElement, Object value) {
            if (value.equals(CHANGEABLE)) {
                Model.getCoreHelper().setReadOnly(modelElement, false);
            } else if (value.equals(ADDONLY)) {
                Model.getCoreHelper().setChangeability(
                        modelElement, Model.getChangeableKind().getAddOnly());
            } else {
                Model.getCoreHelper().setReadOnly(modelElement, true);
            }
        }
    }

    private class FeatureGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the features for the model
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type /* TODO: change this to a metatype */ ) {
            
            StringTokenizer st = new StringTokenizer(type, ",");
            try {
                Class metaType = Class.forName(st.nextToken());
                if (Model.getMetaTypes().getAttribute().equals(metaType)) {
                    return Model.getFacade().getAttributes(modelElement);
                } else if (Model.getMetaTypes().getOperation().equals(metaType)) {
                    return Model.getFacade().getOperationsAndReceptions(modelElement);
                } else {
                    return Collections.EMPTY_LIST;
                }
            } catch (ClassNotFoundException e) {
                LOG.error("Exception", e);
                return Collections.EMPTY_LIST;
            }
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getOperation();
        }
    }
    
    

    private class OwnedElementGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the owned elements for the namespace
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            
            return Model.getFacade().getOwnedElements(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getOperation();
        }
    }
    

    private class RaisedExceptionGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the owned elements for the namespace
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            return Model.getFacade().getRaisedExceptions(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getSignal();
        }
    }
    
    private class MethodGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the method for the operation
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            return Model.getFacade().getMethods(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getMethod();
        }
    }
    
    
    private class MessageGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the method for the operation
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            return Model.getFacade().getMessages(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getMessage();
        }
    }
    
    
    private class ArgumentGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the method for the operation
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            return Model.getFacade().getArguments(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getArgument();
        }
    }
    
    
    private class ExtensionPointGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the extension points
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            return Model.getFacade().getExtensionPoints(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getExtensionPoint();
        }
    }
    
    private class GuardGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the guards
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            final ArrayList l = new ArrayList(1);
            l.add(Model.getFacade().getGuard(modelElement));
            return l;
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getGuard();
        }
    }
    
    private class EffectGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the effects
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            final ArrayList l = new ArrayList(1);
            l.add(Model.getFacade().getEffect(modelElement));
            return l;
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getAction();
        }
    }
    
    private class TriggerGetterSetter extends ListGetterSetter {
        
        /**
         * Get all the effects
         * @param modelElement
         * @param type
         * @return
         * @see org.argouml.core.propertypanels.model.GetterSetterManager.OptionGetterSetter#getOptions(java.lang.Object, java.lang.String)
         */
        public Collection getOptions(
                final Object modelElement,
                final String type) {
            final ArrayList l = new ArrayList(1);
            l.add(Model.getFacade().getTrigger(modelElement));
            return l;
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(
                final Object element,
                final String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getEvent();
        }
    }
    
    private class ParameterGetterSetter extends ListGetterSetter {
        
        public Collection getOptions(Object modelElement, String type) {
            return Model.getFacade().getParameters(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(Object element, String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getParameter();
        }
    }
    
    private class TemplateParameterGetterSetter extends ListGetterSetter {
        
        public Collection getOptions(Object modelElement, String type) {
            return Model.getFacade().getTemplateParameters(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(Object element, String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getTemplateParameter();
        }
    }
    
    private class ElementImportGetterSetter extends ListGetterSetter implements Addable, Removeable {
        
        public Collection getOptions(Object modelElement, String type) {
            return Model.getFacade().getImportedElements(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
        
        public boolean isFullBuildOnly() {
        	return true;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(Object element, String type) {
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getModelElement();
        }
        
        public Command getAddCommand(Object modelElement) {
        	return new AddElementImportCommand(modelElement);
        }
        
        public Command getRemoveCommand(Object modelElement, Object objectToRemove) {
        	return new RemoveElementImportCommand(modelElement, objectToRemove);
        }
        
        private class AddElementImportCommand extends AddModelElementCommand {

        	final Object target;
        	
            /**
             * Constructor for ActionAddPackageImport.
             */
            public AddElementImportCommand(Object target) {
                super();
                this.target = target;
            }


            protected List getChoices() {
                List list = new ArrayList();
                /* TODO: correctly implement next function 
                 * in the model subsystem for 
                 * issue 1942: */
                list.addAll(Model.getModelManagementHelper()
                        .getAllPossibleImports(target));
                return list;
            }


            protected List getSelected() {
                List list = new ArrayList();
                list.addAll(Model.getFacade().getImportedElements(target));
                return list;
            }


            protected String getDialogTitle() {
                return Translator.localize("dialog.title.add-imported-elements");
            }


            @Override
            protected void doIt(Collection selected) {
            	if (LOG.isInfoEnabled()) {
                	LOG.info("Setting " + selected.size() + "imported elements");
            	}
                Model.getModelManagementHelper().setImportedElements(target, selected);
            }
        }
        
        private class RemoveElementImportCommand
    	    extends NonUndoableCommand {
        	
        	private final Object target;
        	private final Object objectToRemove;
        	
    	    /**
    	     * Constructor for ActionRemovePackageImport.
    	     */
    	    public RemoveElementImportCommand(final Object target, final Object objectToRemove) {
    	        this.target = target;
    	        this.objectToRemove = objectToRemove;
    	    }
    	    
    	    /*
    	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    	     */
    	    public Object execute() {
    	        Model.getModelManagementHelper()
    	            .removeImportedElement(target, objectToRemove);
    	        return null;
    	    }
    	}
        
        public abstract class AddModelElementCommand extends NonUndoableCommand {

            private Object target;
            private boolean multiSelect = true;
            private boolean exclusive = true;

            /**
             * Construct a command to add a model element to some list.
             */
            protected AddModelElementCommand() {
            }

            /*
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public Object execute() {
                UMLAddDialog dialog =
                    new UMLAddDialog(getChoices(), getSelected(), getDialogTitle(),
                                     isMultiSelect(),
                                     isExclusive());
                int result = dialog.showDialog(ArgoFrame.getFrame());
                if (result == JOptionPane.OK_OPTION) {
                    doIt(dialog.getSelected());
                }
                return null;
            }
            
            /**
             * Returns the choices the user has in the UMLAddDialog. The choices are
             * depicted on the left side of the UMLAddDialog (sorry Arabic users) and
             * can be moved via the buttons on the dialog to the right side. On the
             * right side are the selected modelelements.
             * @return List of choices
             */
            protected abstract List getChoices();

            
            /**
             * The modelelements already selected BEFORE the dialog is shown.
             * @return List of model elements
             */
            protected abstract List getSelected();

            /**
             * The action that has to be done by ArgoUml after the user clicks ok in the
             * UMLAddDialog.
             * @param selected The choices the user has selected in the UMLAddDialog
             */
            protected abstract void doIt(Collection selected);

            /**
             * Returns the UML model target.
             * @return UML ModelElement
             */
            protected Object getTarget() {
                return target;
            }

            /**
             * Sets the UML model target.
             * @param theTarget The target to set
             */
            public void setTarget(Object theTarget) {
                target = theTarget;
            }

            /**
             * Returns the title of the dialog.
             * @return String
             */
            protected abstract String getDialogTitle();

            /**
             * Returns the exclusive.
             * @return boolean
             */
            public boolean isExclusive() {
                return exclusive;
            }

            /**
             * Returns the multiSelect.
             * @return boolean
             */
            public boolean isMultiSelect() {
                return multiSelect;
            }

            /**
             * Sets the exclusive.
             * @param theExclusive The exclusive to set
             */
            public void setExclusive(boolean theExclusive) {
                exclusive = theExclusive;
            }

            /**
             * Sets the multiSelect.
             * @param theMultiSelect The multiSelect to set
             */
            public void setMultiSelect(boolean theMultiSelect) {
                multiSelect = theMultiSelect;
            }

        }
        
        
    }
    
    
    private class MethodExpressionGetterSetter extends ExpressionGetterSetter {
        
        @Override
        public Object get(Object modelElement, String type) {
            return Model.getFacade().getBody(modelElement);
        }
      
        @Override
        public void set(Object modelElement, Object value) {
            Model.getCoreHelper().setBody(modelElement, value);
        }

        @Override
        public Object create(final String language, final String body) {
            return Model.getDataTypesFactory().createProcedureExpression(language, body);
        }
    }
    
    private class SenderGetterSetter extends ListGetterSetter {
        
        public Collection getOptions(Object modelElement, String type) {
            return Model.getFacade().getSentStimuli(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(Object element, String type) {
          
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getAttribute();
        }
    }
    
    private class ReceiverGetterSetter extends ListGetterSetter {
        
        public Collection getOptions(Object modelElement, String type) {
            return Model.getFacade().getReceivedStimuli(modelElement);
        }
      
        public Object get(Object modelElement, String type) {
            // not needed
            return null;
        }
      
        public void set(Object element, Object x) {
            // not needed
        }

        public boolean isValidElement(Object element, String type) {
          
            return getOptions(element, type).contains(element);
        }
        
        public Object getMetaType() {
            return Model.getMetaTypes().getAttribute();
        }
    }
}
