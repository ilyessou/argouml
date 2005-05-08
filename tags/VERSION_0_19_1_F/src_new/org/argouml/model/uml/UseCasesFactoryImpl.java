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

package org.argouml.model.uml;

import org.argouml.model.UseCasesFactory;

import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.use_cases.MActor;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MExtensionPoint;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.behavior.use_cases.MUseCaseInstance;
import ru.novosoft.uml.foundation.core.MNamespace;

/**
 * Factory to create UML classes for the UML
 * BehaviorialElements::UseCases package.
 *
 * TODO: Change visibility to package after reflection problem solved.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 */
public class UseCasesFactoryImpl
	extends AbstractUmlModelFactory
	implements UseCasesFactory {

    /**
     * The model implementation.
     */
    private NSUMLModelImplementation nsmodel;

    /**
     * Don't allow instantiation.
     *
     * @param implementation To get other helpers and factories.
     */
    UseCasesFactoryImpl(NSUMLModelImplementation implementation) {
        nsmodel = implementation;
    }

    /**
     * Create an empty but initialized instance of a Extend.
     *
     * @return an initialized Extend instance.
     */
    public Object createExtend() {
        MExtend modelElement = MFactory.getDefaultFactory().createExtend();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a ExtensionPoint.
     *
     * @return an initialized ExtensionPoint instance.
     */
    public Object createExtensionPoint() {
        MExtensionPoint modelElement =
	    MFactory.getDefaultFactory().createExtensionPoint();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a Actor.
     *
     * @return an initialized Actor instance.
     */
    public Object createActor() {
        MActor modelElement = MFactory.getDefaultFactory().createActor();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a Include.
     *
     * @return an initialized Include instance.
     */
    public Object createInclude() {
        MInclude modelElement = MFactory.getDefaultFactory().createInclude();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UseCase.
     *
     * @return an initialized UseCase instance.
     */
    public Object createUseCase() {
        MUseCase modelElement = MFactory.getDefaultFactory().createUseCase();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Create an empty but initialized instance of a UseCaseInstance.
     *
     * @return an initialized UseCaseInstance instance.
     */
    public Object createUseCaseInstance() {
        MUseCaseInstance modelElement =
	    MFactory.getDefaultFactory().createUseCaseInstance();
	super.initialize(modelElement);
	return modelElement;
    }

    /**
     * Build an extend relationship.<p>
     *
     * Set the namespace to the base (preferred) or else extension's
     * namespace. We don't do any checking on base and extension. They
     * should be different, but that is someone else's problem.<p>
     *
     * @param abase       The base use case for the relationship
     *
     * @param anextension The extension use case for the relationship
     *
     * @return            The new extend relationship or <code>null</code>
     *                    if it can't be created.
     */
    public Object buildExtend(Object abase, Object anextension) {
        MUseCase base = (MUseCase) abase;
        MUseCase extension = (MUseCase) anextension;

	MExtend extend =
	    (MExtend) nsmodel.getUseCasesFactory().createExtend();
	// Set the ends

	extend.setBase(base);
	extend.setExtension(extension);

	// Set the namespace to that of the base as first choice, or that of
	// the extension as second choice.

	if (base.getNamespace() != null) {
	    extend.setNamespace(base.getNamespace());
	} else if (extension.getNamespace() != null) {
	    extend.setNamespace(extension.getNamespace());
	}

	// build an extensionpoint in the base
	MExtensionPoint point = (MExtensionPoint) buildExtensionPoint(base);
	extend.addExtensionPoint(point);

	return extend;
    }

    /**
     * Build an extend relationship.<p>
     *
     * @param abase       The base use case for the relationship
     * @param anextension The extension use case for the relationship
     * @param apoint      The insertion point for the extension
     * @return            The new extend relationship or <code>null</code>
     *                    if it can't be created.
     */
    public Object buildExtend(Object abase,
			       Object anextension,
			       Object apoint) {
        MUseCase base = (MUseCase) abase;
        MUseCase extension = (MUseCase) anextension;
        MExtensionPoint point = (MExtensionPoint) apoint;
        if (base == null || extension == null) {
            throw new IllegalArgumentException("Either the base usecase or "
					       + "the extension usecase is "
					       + "null");
        }
        if (point != null) {
            if (!base.getExtensionPoints().contains(point)) {
                throw new IllegalArgumentException("The extensionpoint is no "
						   + "part of the base "
						   + "usecase");
            }
        } else {
            point = (MExtensionPoint) buildExtensionPoint(base);
        }
        MExtend extend = (MExtend) createExtend();
        extend.setBase(base);
        extend.setExtension(extension);
        extend.addExtensionPoint(point);
        return extend;
    }



    /**
     * Builds an extension point for a use case.
     *
     * @param modelElement The owning use case for the extension point.
     * @return The new extension point.
     * @throws IllegalArgumentException if modelElement isn't a use-case.
     */
    public Object buildExtensionPoint(Object modelElement) {
        if (!(modelElement instanceof MUseCase)) {
            throw new IllegalArgumentException("An extension point can only "
					       + "be built on a use case");
        }

        MUseCase useCase = (MUseCase) modelElement;
        MExtensionPoint extensionPoint =
	    (MExtensionPoint) nsmodel.getUseCasesFactory()
	    	.createExtensionPoint();

        // Set the owning use case if there is one given.

        extensionPoint.setUseCase(useCase);

        // Set the namespace to that of the useCase if possible.

        // the usecase itself is a namespace...
        extensionPoint.setNamespace(useCase);
	/*
	if (useCase.getNamespace() != null) {
	extensionPoint.setNamespace(useCase.getNamespace());
	}
	*/

        // For consistency with attribute and operation, give it a default
        // name and location

        extensionPoint.setName("newEP");
        extensionPoint.setLocation("loc");

        return extensionPoint;
    }

    /**
     * Build an include relationship.<p>
     *
     * Set the namespace to the base (preferred) or else extension's
     * namespace. We don't do any checking on base and extension. They
     * should be different, but that is someone else's problem.<p>
     *
     * <em>Note</em>. There is a bug in NSUML that gets the base and
     * addition associations back to front. We reverse the use of
     * their accessors in the code to correct this.<p>
     *
     * @param abase      The base use case for the relationship
     *
     * @param anaddition The extension use case for the relationship
     *
     * @return           The new include relationship or <code>null</code> if
     *                   it can't be created.
     */
    public Object buildInclude(Object/*MUseCase*/ abase,
				 Object/*MUseCase*/ anaddition) {
        MUseCase base = (MUseCase) abase;
        MUseCase addition = (MUseCase) anaddition;
	MInclude include =
	    (MInclude) nsmodel.getUseCasesFactory().createInclude();

	// Set the ends. Because of the NSUML bug we reverse the accessors
	// here.

	include.setAddition(base);
	include.setBase(addition);

	// Set the namespace to that of the base as first choice, or that of
	// the addition as second choice.

	if (base.getNamespace() != null) {
	    include.setNamespace(base.getNamespace());
	} else if (addition.getNamespace() != null) {
	    include.setNamespace(addition.getNamespace());
	}

	return include;
    }

    /**
     * Builds an actor in the given namespace.
     *
     * @param ns the given namespace
     * @param model TODO: What is this? Why is this argument needed?
     * @return The newly build Actor.
     */
    private MActor buildActor(MNamespace ns, Object model) {
     	if (ns == null) {
     	    ns = (MNamespace) model;
     	}
     	MActor actor = (MActor) createActor();
     	actor.setNamespace(ns);
     	actor.setLeaf(false);
     	actor.setRoot(false);
     	return actor;
    }

    /**
     * Builds an actor in the same namespace of the given actor. If
     * object is no actor nothing is built. Did not give MActor as an
     * argument but object to seperate argouml better from NSUML.<p>
     *
     * @param model The current model.
     * @param actor the given Actor
     * @return The newly build Actor
     *
     * @see org.argouml.model.UseCasesFactory#buildActor(java.lang.Object,
     *         java.lang.Object)
     */
    public Object buildActor(Object actor, Object model) {
        if (actor instanceof MActor) {
            return buildActor(((MActor) actor).getNamespace(), model);
        }
        return null;
    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteActor(Object elem) {
        if (!(elem instanceof MActor)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteExtend(Object elem) {
        if (!(elem instanceof MExtend)) {
            throw new IllegalArgumentException();
        }

	nsmodel.getUmlHelper()
		.deleteCollection(((MExtend) elem).getExtensionPoints());
    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteExtensionPoint(Object elem) {
        if (!(elem instanceof MExtensionPoint)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteInclude(Object elem) {
        if (!(elem instanceof MInclude)) {
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteUseCase(Object elem) {
        if (!(elem instanceof MUseCase)) {
            throw new IllegalArgumentException();
        }

	MUseCase useCase = ((MUseCase) elem);
	nsmodel.getUmlHelper().deleteCollection(useCase.getExtends());
	nsmodel.getUmlHelper().deleteCollection(useCase.getExtends2());
	nsmodel.getUmlHelper().deleteCollection(useCase.getIncludes());
	nsmodel.getUmlHelper().deleteCollection(useCase.getIncludes2());
    }

    /**
     * @param elem the UML element to be deleted
     */
    void deleteUseCaseInstance(Object elem) {
        if (!(elem instanceof MUseCaseInstance)) {
            throw new IllegalArgumentException();
        }

    }

}

