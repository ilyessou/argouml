// $Id$
// Copyright (c) 2007, The ArgoUML Project
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the ArgoUML Project nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE ArgoUML PROJECT ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE ArgoUML PROJECT BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.model.euml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.argouml.model.NotImplementedException;
import org.argouml.model.UmlException;
import org.argouml.model.XmiExtensionWriter;
import org.argouml.model.XmiWriter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

/**
 * Eclipse UML2 implementation of XmiWriter.
 * 
 * TODO: We need facilities for writing and reading stable IDs to/from either
 * xmi.id or xmi.uuid.
 * 
 * @author Tom Morris
 */
class XmiWriterEUMLImpl implements XmiWriter {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    private OutputStream oStream;
    
    private org.eclipse.uml2.uml.Package model;
    
    /**
     * Old style constructor.  Unsupported (and shouldn't get called)
     */
    public XmiWriterEUMLImpl(EUMLModelImplementation implementation,
            Object model, Writer writer, String version) {
        throw new NotImplementedException();
    }
    
    /**
     * Constructor.
     * 
     * @param implementation
     *            The ModelImplementation.
     * @param model
     *            The project member model.
     * @param writer
     *            The writer.
     * @param version
     *            The version of ArgoUML.
     */
    public XmiWriterEUMLImpl(EUMLModelImplementation implementation,
            Object theModel, OutputStream stream, String version) {
        modelImpl = implementation;
        if (stream == null) {
            throw new IllegalArgumentException("An OutputStream must be provided");
        }
        if (!(theModel instanceof org.eclipse.uml2.uml.Package)) {
            throw new IllegalArgumentException("A model must be provided"
                    + " and it must be a UML 2 Package");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("A parent must be provided");
        }
        modelImpl = implementation;
        model = (org.eclipse.uml2.uml.Package) theModel;
        oStream = stream;
    }

    public void write() throws UmlException {
        // This URI is a dummy.  We're going to write to our OutputStream
        Resource resource =
                new ResourceSetImpl().createResource(URI
                        .createFileURI("foo.xmi"));
        EList<EObject> contents = resource.getContents();
        contents.add(model);

        // Do we need to get stereotype applications for each element? - tfm
//        for (Iterator allContents = UMLUtil.getAllContents(model, true,
//                false); allContents.hasNext();) {
//            EObject eObject = (EObject) allContents.next();
//            if (eObject instanceof Element) {
//                contents.addAll(((Element) eObject).getStereotypeApplications());
//            }
//        }

        Map options = new HashMap();
        options.put(XMLResource.OPTION_LINE_WIDTH, 100);
        
        // TODO: Is there an option we can use to save our ArgoUML version?
        
        try {
            resource.save(oStream, options);
        } catch (IOException ioe) {
            throw new UmlException(ioe);
        }

    }

    public void setXmiExtensionWriter(XmiExtensionWriter xmiExtensionWriter) {
        throw new NotYetImplementedException();
    }

    
}
