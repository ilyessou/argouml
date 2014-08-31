// $Id$
/*******************************************************************************
 * Copyright (c) 2007-2012 Tom Morris and other contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tom Morris - initial implementation
 *    Bogdan Pistol - undo support
 *    thn
 *
 * This implementation uses ideas and code snippets from the
 * "org.eclipse.uml2.uml.editor.presentation" package.
 *
 * The package "org.eclipse.uml2.uml.editor.presentation" is part of the
 * Eclipse UML2 plugin and it is available under the terms of
 * the Eclipse Public License v1.0.
 *****************************************************************************/

package org.argouml.model.euml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.model.DiagramInterchangeModel;
import org.argouml.model.MessageSort;
import org.argouml.model.ModelImplementation;
import org.argouml.model.UmlException;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.uml2.common.edit.domain.UML2AdapterFactoryEditingDomain;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.edit.providers.UMLItemProviderAdapterFactory;
import org.eclipse.uml2.uml.edit.providers.UMLReflectiveItemProviderAdapterFactory;
import org.eclipse.uml2.uml.edit.providers.UMLResourceItemProviderAdapterFactory;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resource.XMI212UMLResource;
import org.eclipse.uml2.uml.resource.XMI2UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.XMI2UMLResource;

/**
 * Eclipse UML2 implementation of the ArgoUML Model subsystem. Although built on
 * the Eclipse UML2 plugin which is, in turn, built on eCore and the Eclipse
 * Modeling Framework (EMF), the only things required from Eclipse are the five
 * Jars which implement UML2, eCore, and the subset of EMF needed for to support
 * them.
 * <p>
 * The implementation of this subsystem was generously sponsored by Google as
 * part of the Google Summer of Code 2007. A large part of the implementation
 * was built by the sponsored student, Bogdan Ciprian Pistol, who was mentored
 * by Tom Morris.
 * <p>
 * This implementation uses ideas and code snippets from the
 * "org.eclipse.uml2.uml.editor.presentation" package which is part of the
 * Eclipse UML2 plugin.
 *
 * @author Bogdan Ciprian Pistol
 * @author Tom Morris <tfmorris@gmail.com>
 * @since ArgoUML 0.25.4, May 2007
 */
public class EUMLModelImplementation implements ModelImplementation {

    private static final Logger LOG =
        Logger.getLogger(EUMLModelImplementation.class.getName());

    private ActivityGraphsFactoryEUMLlImpl theActivityGraphsFactory;

    private ActivityGraphsHelperEUMLImpl theActivityGraphsHelper;

    private AggregationKindEUMLImpl theAggregationKind;

    @SuppressWarnings("deprecation")
    private ChangeableKindEUMLImpl theChangeableKind;

    private CollaborationsFactoryEUMLImpl theCollaborationsFactory;

    private CollaborationsHelperEUMLImpl theCollaborationsHelper;

    private CommonBehaviorFactoryEUMLImpl theCommonBehaviorFactory;

    private CommonBehaviorHelperEUMLImpl theCommonBehaviorHelper;

    private ConcurrencyKindEUMLImpl theConcurrencyKind;

    private CopyHelperEUMLImpl theCopyHelper;

    private CoreFactoryEUMLImpl theCoreFactory;

    private CoreHelperEUMLImpl theCoreHelper;

    private DataTypesFactoryEUMLImpl theDataTypesFactory;

    private DataTypesHelperEUMLImpl theDataTypesHelper;

    private DirectionKindEUMLImpl theDirectionKind;

    private ExtensionMechanismsFactoryEUMLImpl theExtensionMechanismsFactory;

    private ExtensionMechanismsHelperEUMLImpl theExtensionMechanismsHelper;

    private FacadeEUMLImpl theFacade;

    private MessageSort theMessageSort;

    private MetaTypesEUMLImpl theMetaTypes;

    private ModelEventPumpEUMLImpl theModelEventPump;

    private ModelManagementFactoryEUMLImpl theModelManagementFactory;

    private ModelManagementHelperEUMLImpl theModelManagementHelper;

    private OrderingKindEUMLImpl theOrderingKind;

    private PseudostateKindEUMLImpl thePseudostateKind;

    @SuppressWarnings("deprecation")
    private ScopeKindEUMLImpl theScopeKind;

    private StateMachinesFactoryEUMLImpl theStateMachinesFactory;

    private StateMachinesHelperEUMLImpl theStateMachinesHelper;

    private UmlFactoryEUMLImpl theUmlFactory;

    private UmlHelperEUMLImpl theUmlHelper;

    private UseCasesFactoryEUMLImpl theUseCasesFactory;

    private UseCasesHelperEUMLImpl theUseCasesHelper;

    private VisibilityKindEUMLImpl theVisibilityKind;

    private CommandStackImpl theCommandStack;

    /**
     * This keeps track of the editing domain that is used to track all changes
     * to the model.
     * <p>
     * TODO: This probably needs to be a set of EditingDomain so that we can
     * manage an EditingDomain per project.
     */
    private AdapterFactoryEditingDomain editingDomain;

    /**
     * Map of which resources are read-only.
     * <p>
     * TODO: This needs to be managed per EditingDomain.
     */
    private Map<Resource, Boolean> readOnlyMap =
        new HashMap<Resource, Boolean>();

    /**
     * Constructor.
     */
    public EUMLModelImplementation() {
        initializeEditingDomain();
        LOG.log(Level.FINE,
                "EUML Init - editing domain initialized"); //$NON-NLS-1$
    }

    /**
     * This sets up the editing domain for the model editor.
     *
     * TODO: We probably need an EditingDomain per Argo project so that we can
     * keep the ResourceSets separate.
     */
    private void initializeEditingDomain() {
        // If the eUML.resources system property is defined then we are in a
        // stand alone application, else we're in an Eclipse plug in.
        // The eUML.resource should contain the path to the
        // org.eclipse.uml2.uml.resource jar plugin.
        String path = System.getProperty("eUML.resources"); //$NON-NLS-1$

        BasicCommandStack commandStack = new BasicCommandStack() {

            @Override
            protected void handleError(Exception exception) {
                super.handleError(exception);
                throw new RuntimeException(exception);
            }

        };

        List<AdapterFactory> factories = new ArrayList<AdapterFactory>();
        factories.add(new UMLResourceItemProviderAdapterFactory());
        factories.add(new UMLItemProviderAdapterFactory());
        factories.add(new EcoreItemProviderAdapterFactory());
        factories.add(new UMLReflectiveItemProviderAdapterFactory());
        ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
                factories);

        // TODO: This will need to be adapted to send undo/redo commands back
        // to our consumer (e.g. ArgoUML) if a new undo mechanism is implemented
        // for the Model subsystem - tfm
        editingDomain = new UML2AdapterFactoryEditingDomain(
                adapterFactory, commandStack, readOnlyMap);

        ResourceSet resourceSet = editingDomain.getResourceSet();
        Map<String, Object> extensionToFactoryMap =
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();

        if (path != null) {
            try {
                FileInputStream in = new FileInputStream(path);
                in.close();
            } catch (IOException e) {
                throw (new RuntimeException(e));
            }

            path = path.replace('\\', '/');
            // These lines were one cause for issue 5915: (Were they needed?)
            // TODO: Review - tfm
            //if (Character.isLetter(path.charAt(0))) {
            //    path = '/' + path;
            //}
            URI uri =
                URI.createURI(
                        "jar:file:" //$NON-NLS-1$
                        + path
                        + "!/"); //$NON-NLS-1$
            LOG.log(Level.FINE, "eUML.resource URI --> " + uri); //$NON-NLS-1$

            Registry packageRegistry = resourceSet.getPackageRegistry();
            packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
            packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
            // for other xmi files with further namespaces:
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_1_NS_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_1_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_NS_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_2_NS_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_NS_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put(XMI212UMLResource.UML_METAMODEL_URI,
                    UMLPackage.eINSTANCE);
            // eclipse namespaces:
            packageRegistry.put(UML212UMLResource.UML_METAMODEL_NS_URI,
                    UMLPackage.eINSTANCE);
            packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML",
                    UMLPackage.eINSTANCE);

            // For the .uml files in the eclipse jar files, we need this:
            extensionToFactoryMap.put(
                    UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
            uriMap.put(
                    URI.createURI(UMLResource.LIBRARIES_PATHMAP),
                    uri.appendSegment("libraries") //$NON-NLS-1$
                       .appendSegment("")); //$NON-NLS-1$
            uriMap.put(
                    URI.createURI(UMLResource.METAMODELS_PATHMAP),
                    uri.appendSegment("metamodels") //$NON-NLS-1$
                       .appendSegment("")); //$NON-NLS-1$
            uriMap.put(
                    URI.createURI(UMLResource.PROFILES_PATHMAP),
                    uri.appendSegment("profiles") //$NON-NLS-1$
                       .appendSegment("")); //$NON-NLS-1$
        }

        extensionToFactoryMap.put(
                UML22UMLResource.FILE_EXTENSION,
                UML22UMLResource.Factory.INSTANCE);
        extensionToFactoryMap.put(
                XMI2UMLResource.FILE_EXTENSION,
                XMI2UMLResource.Factory.INSTANCE);
        uriMap.putAll(UML22UMLExtendedMetaData.getURIMap());
        uriMap.putAll(XMI2UMLExtendedMetaData.getURIMap());
    }

    /**
     * Getter for {@link #editingDomain the Editing Domain}
     *
     * @return the editing domain of the current EUMLModelImplementation
     *         instance
     */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }

    /**
     * @return the read only map for resources in the EditingDomain
     */
    public Map<Resource, Boolean> getReadOnlyMap() {
        return readOnlyMap;
    }

    public ActivityGraphsFactoryEUMLlImpl getActivityGraphsFactory() {
        if (theActivityGraphsFactory == null) {
            theActivityGraphsFactory =
                new ActivityGraphsFactoryEUMLlImpl(this);
        }
        return theActivityGraphsFactory;
    }

    public ActivityGraphsHelperEUMLImpl getActivityGraphsHelper() {
        if (theActivityGraphsHelper == null) {
            theActivityGraphsHelper = new ActivityGraphsHelperEUMLImpl(this);
        }
        return theActivityGraphsHelper;
    }

    public AggregationKindEUMLImpl getAggregationKind() {
        if (theAggregationKind == null) {
            theAggregationKind = new AggregationKindEUMLImpl();
        }
        return theAggregationKind;
    }

    @SuppressWarnings("deprecation")
    public ChangeableKindEUMLImpl getChangeableKind() {
        if (theChangeableKind == null) {
            theChangeableKind = new ChangeableKindEUMLImpl();
        }
        return theChangeableKind;
    }

    public CollaborationsFactoryEUMLImpl getCollaborationsFactory() {
        if (theCollaborationsFactory == null) {
            theCollaborationsFactory = new CollaborationsFactoryEUMLImpl(this);
        }
        return theCollaborationsFactory;
    }

    public CollaborationsHelperEUMLImpl getCollaborationsHelper() {
        if (theCollaborationsHelper == null) {
            theCollaborationsHelper = new CollaborationsHelperEUMLImpl(this);
        }
        return theCollaborationsHelper;
    }

    public CommonBehaviorFactoryEUMLImpl getCommonBehaviorFactory() {
        if (theCommonBehaviorFactory == null) {
            theCommonBehaviorFactory = new CommonBehaviorFactoryEUMLImpl(this);
        }
        return theCommonBehaviorFactory;
    }

    public CommonBehaviorHelperEUMLImpl getCommonBehaviorHelper() {
        if (theCommonBehaviorHelper == null) {
            theCommonBehaviorHelper = new CommonBehaviorHelperEUMLImpl(this);
        }
        return theCommonBehaviorHelper;
    }

    public ConcurrencyKindEUMLImpl getConcurrencyKind() {
        if (theConcurrencyKind == null) {
            theConcurrencyKind = new ConcurrencyKindEUMLImpl();
        }
        return theConcurrencyKind;
    }

    public CopyHelperEUMLImpl getCopyHelper() {
        if (theCopyHelper == null) {
            theCopyHelper = new CopyHelperEUMLImpl(this);
        }
        return theCopyHelper;
    }

    public CoreFactoryEUMLImpl getCoreFactory() {
        if (theCoreFactory == null) {
            theCoreFactory = new CoreFactoryEUMLImpl(this);
        }
        return theCoreFactory;
    }

    public CoreHelperEUMLImpl getCoreHelper() {
        if (theCoreHelper == null) {
            theCoreHelper = new CoreHelperEUMLImpl(this);
        }
        return theCoreHelper;
    }

    public DataTypesFactoryEUMLImpl getDataTypesFactory() {
        if (theDataTypesFactory == null) {
            theDataTypesFactory = new DataTypesFactoryEUMLImpl(this);
        }
        return theDataTypesFactory;
    }

    public DataTypesHelperEUMLImpl getDataTypesHelper() {
        if (theDataTypesHelper == null) {
            theDataTypesHelper = new DataTypesHelperEUMLImpl(this);
        }
        return theDataTypesHelper;
    }

    public DirectionKindEUMLImpl getDirectionKind() {
        if (theDirectionKind == null) {
            theDirectionKind = new DirectionKindEUMLImpl();
        }
        return theDirectionKind;
    }

    public ExtensionMechanismsFactoryEUMLImpl getExtensionMechanismsFactory() {
        if (theExtensionMechanismsFactory == null) {
            theExtensionMechanismsFactory =
                new ExtensionMechanismsFactoryEUMLImpl(this);
        }
        return theExtensionMechanismsFactory;
    }

    public ExtensionMechanismsHelperEUMLImpl getExtensionMechanismsHelper() {
        if (theExtensionMechanismsHelper == null) {
            theExtensionMechanismsHelper =
                new ExtensionMechanismsHelperEUMLImpl(this);
        }
        return theExtensionMechanismsHelper;
    }

    public FacadeEUMLImpl getFacade() {
        if (theFacade == null) {
            theFacade = new FacadeEUMLImpl(this);
        }
        return theFacade;
    }

    public MetaTypesEUMLImpl getMetaTypes() {
        if (theMetaTypes == null) {
            theMetaTypes = new MetaTypesEUMLImpl(this);
        }
        return theMetaTypes;
    }

    public ModelEventPumpEUMLImpl getModelEventPump() {
        if (theModelEventPump == null) {
            theModelEventPump = new ModelEventPumpEUMLImpl(this);
        }
        return theModelEventPump;
    }

    public ModelManagementFactoryEUMLImpl getModelManagementFactory() {
        if (theModelManagementFactory == null) {
            theModelManagementFactory =
                new ModelManagementFactoryEUMLImpl(this);
        }
        return theModelManagementFactory;
    }

    public ModelManagementHelperEUMLImpl getModelManagementHelper() {
        if (theModelManagementHelper == null) {
            theModelManagementHelper = new ModelManagementHelperEUMLImpl(this);
        }
        return theModelManagementHelper;
    }

    public OrderingKindEUMLImpl getOrderingKind() {
        if (theOrderingKind == null) {
            theOrderingKind = new OrderingKindEUMLImpl();
        }
        return theOrderingKind;
    }

    public MessageSort getMessageSort() {
        if (theMessageSort == null) {
            theMessageSort = new MessageSortEUMLImpl();
        }
        return theMessageSort;
    }

    public PseudostateKindEUMLImpl getPseudostateKind() {
        if (thePseudostateKind == null) {
            thePseudostateKind = new PseudostateKindEUMLImpl();
        }
        return thePseudostateKind;
    }

    @SuppressWarnings("deprecation")
    public ScopeKindEUMLImpl getScopeKind() {
        if (theScopeKind == null) {
            theScopeKind = new ScopeKindEUMLImpl();
        }
        return theScopeKind;
    }


    public StateMachinesFactoryEUMLImpl getStateMachinesFactory() {
        if (theStateMachinesFactory == null) {
            theStateMachinesFactory = new StateMachinesFactoryEUMLImpl(this);
        }
        return theStateMachinesFactory;
    }

    public StateMachinesHelperEUMLImpl getStateMachinesHelper() {
        if (theStateMachinesHelper == null) {
            theStateMachinesHelper = new StateMachinesHelperEUMLImpl(this);
        }
        return theStateMachinesHelper;
    }

    public UmlFactoryEUMLImpl getUmlFactory() {
        if (theUmlFactory == null) {
            theUmlFactory = new UmlFactoryEUMLImpl(this);
        }
        return theUmlFactory;
    }

    public UmlHelperEUMLImpl getUmlHelper() {
        if (theUmlHelper == null) {
            theUmlHelper = new UmlHelperEUMLImpl(this);
        }
        return theUmlHelper;
    }

    public UseCasesFactoryEUMLImpl getUseCasesFactory() {
        if (theUseCasesFactory == null) {
            theUseCasesFactory = new UseCasesFactoryEUMLImpl(this);
        }
        return theUseCasesFactory;
    }

    public UseCasesHelperEUMLImpl getUseCasesHelper() {
        if (theUseCasesHelper == null) {
            theUseCasesHelper = new UseCasesHelperEUMLImpl(this);
        }
        return theUseCasesHelper;
    }

    public VisibilityKindEUMLImpl getVisibilityKind() {
        if (theVisibilityKind == null) {
            theVisibilityKind = new VisibilityKindEUMLImpl();
        }
        return theVisibilityKind;
    }

    public XmiReaderEUMLImpl getXmiReader() throws UmlException {
        return new XmiReaderEUMLImpl(this);
    }

    public XmiWriterEUMLImpl getXmiWriter(Object model, OutputStream stream,
            String version) throws UmlException {
        return new XmiWriterEUMLImpl(this, model, stream, version);
    }

    public DiagramInterchangeModel getDiagramInterchangeModel() {
        return null;
    }

    /**
     * Unload all resources in the editing domain and clear the read only map.
     */
    void clearEditingDomain() {
        for (Resource resource
                : editingDomain.getResourceSet().getResources()) {
            unloadResource(resource);
        }
    }

    void unloadResource(Resource resource) {
        resource.unload();
        readOnlyMap.remove(resource);
    }
}
