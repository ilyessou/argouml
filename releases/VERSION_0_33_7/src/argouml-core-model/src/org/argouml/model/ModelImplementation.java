/* $Id$
 *******************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *******************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2007 The Regents of the University of California. All
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

package org.argouml.model;

import java.io.OutputStream;



/**
 * The interface that the ModelImplementation must implement. This is the
 * class finding all helpers and factories.
 */
public interface ModelImplementation {
    /**
     * Get the facade.
     *
     * @return The facade object.
     */
    Facade getFacade();

    /**
     * Get the diagram interchange model.
     *
     * @return the diagram interchange model.
     */
    DiagramInterchangeModel getDiagramInterchangeModel();

    /**
     * Get the event pump.
     *
     * @return the current ModelEventPump.
     */
    ModelEventPump getModelEventPump();

    /**
     * Getter for ActivityGraphsFactory.
     *
     * @return the factory
     */
    ActivityGraphsFactory getActivityGraphsFactory();

    /**
     * Getter for the ActivityGraphsHelper.
     *
     * @return the instance of the helper
     */
    ActivityGraphsHelper getActivityGraphsHelper();

    /**
     * Getter for CollaborationsFactory.
     *
     * @return the factory
     */
    CollaborationsFactory getCollaborationsFactory();

    /**
     * Getter for CollaborationsHelper.
     *
     * @return the helper
     */
    CollaborationsHelper getCollaborationsHelper();

    /**
     * Getter for CommonBehaviorFactory.
     *
     * @return the factory
     */
    CommonBehaviorFactory getCommonBehaviorFactory();

    /**
     * Getter for CommonBehaviorHelper.
     *
     * @return the helper
     */
    CommonBehaviorHelper getCommonBehaviorHelper();

    /**
     * Getter for CoreFactory.
     *
     * @return the factory
     */
    CoreFactory getCoreFactory();

    /**
     * Getter for CoreHelper.
     *
     * @return The helper.
     */
    CoreHelper getCoreHelper();

    /**
     * Getter for DataTypesFactory.
     *
     * @return the factory
     */
    DataTypesFactory getDataTypesFactory();

    /**
     * Getter for DataTypesHelper.
     *
     * @return the helper.
     */
    DataTypesHelper getDataTypesHelper();

    /**
     * Getter for ExtensionMechanismsFactory.
     *
     * @return the factory instance.
     */
    ExtensionMechanismsFactory getExtensionMechanismsFactory();

    /**
     * Getter for ExtensionMechanismsHelper.
     *
     * @return the helper
     */
    ExtensionMechanismsHelper getExtensionMechanismsHelper();

    /**
     * Getter for ModelManagementFactory.
     *
     * @return the factory
     */
    ModelManagementFactory getModelManagementFactory();

    /**
     * Getter for ModelManagementHelper.
     *
     * @return The model management helper.
     */
    ModelManagementHelper getModelManagementHelper();

    /**
     * Getter for StateMachinesFactory.
     *
     * @return the factory
     */
    StateMachinesFactory getStateMachinesFactory();

    /**
     * Getter for StateMachinesHelper.
     *
     * @return the helper
     */
    StateMachinesHelper getStateMachinesHelper();

    /**
     * Getter for UmlFactory.
     *
     * @return the factory
     */
    UmlFactory getUmlFactory();

    /**
     * Getter for UmlHelper.
     *
     * @return the helper
     */
    UmlHelper getUmlHelper();

    /**
     * Getter for UseCasesFactory.
     *
     * @return the factory
     */
    UseCasesFactory getUseCasesFactory();

    /**
     * Getter for UseCasesHelper.
     *
     * @return the helper
     */
    UseCasesHelper getUseCasesHelper();

    /**
     * Getter for MessageSort
     *
     * @return the MessageSort object.
     */
    MessageSort getMessageSort();
    
    /**
     * Getter for the MetaTypes object.
     *
     * @return the MetaTypes object.
     */
    MetaTypes getMetaTypes();

    // Here follows the interfaces that contain the enums of different
    // kinds in the UML meta-model.
    /**
     * Getter for the ChangeableKind object.
     *
     * @return The object implementing the interface.
     * @deprecated for 0.25.4 by tfmorris.  Use 
     *          {@link Facade#isReadOnly(Object)}.
     */
    @Deprecated
    ChangeableKind getChangeableKind();

    /**
     * Getter for the AggregationKind object.
     *
     * @return The object implementing the interface.
     */
    AggregationKind getAggregationKind();

    /**
     * Getter for the PseudostateKind object.
     *
     * @return The object implementing the interface.
     */
    PseudostateKind getPseudostateKind();

    /**
     * Getter for the ScopeKind object.
     * 
     * @return The object implementing the interface.
     * @deprecated for 0.25.4 by tfmorris. Use {@link Facade#isStatic(Object)}.
     */
    @Deprecated
    ScopeKind getScopeKind();

    /**
     * Getter for the ConcurrencyKind object.
     *
     * @return The object implementing the interface.
     */
    ConcurrencyKind getConcurrencyKind();

    /**
     * Getter for the DirectionKind object.
     *
     * @return The object implementing the interface.
     */
    DirectionKind getDirectionKind();

    /**
     * Getter for the OrderingKind object.
     *
     * @return The object implementing the interface.
     */
    OrderingKind getOrderingKind();

    /**
     * Getter for the VisibilityKind object.
     *
     * @return The object implementing the interface.
     */
    VisibilityKind getVisibilityKind();

    /**
     * A factory method that creates a new instance of an XmiReader on each
     * call.
     *
     * @return the object implementing the XmiReader interface
     * @throws UmlException on any error while reading
     */
    XmiReader getXmiReader() throws UmlException;


    /**
     * A factory method that creates a new instance of an XmiWriter on each
     * call.
     * 
     * @param model
     *            the project member model
     * @param stream the output stream to write to
     * @param version
     *            the version of ArgoUML which may be written to the output
     *            to help identify who wrote it
     * @return the object implementing the XmiWriter interface
     * @throws UmlException
     *             on any error while writing
     */
    XmiWriter getXmiWriter(Object model, OutputStream stream, String version)
        throws UmlException;
    
    /**
     * Get the copy helper.
     *
     * @return the CopyHelper
     */
    CopyHelper getCopyHelper();
    

}
