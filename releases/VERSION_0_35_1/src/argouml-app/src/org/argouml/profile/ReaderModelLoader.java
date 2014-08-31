/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.profile;

import java.io.Reader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.argouml.model.Model;
import org.argouml.model.UmlException;
import org.argouml.model.XmiReader;
import org.xml.sax.InputSource;

/**
 * TODO: this doesn't need a full ProfileReference since it uses the
 * reader handed in the constructor. It doesn't make much sense to make
 * its callers init the path to some name which it doesn't need...
 *
 * @author Luis Sergio Oliveira (euluis)
 */
public class ReaderModelLoader implements ProfileModelLoader {

    private static final Logger LOG =
        Logger.getLogger(ReaderModelLoader.class.getName());

    private Reader reader;

    /**
     * Create a ModelLoader that will load the model from the given reader.
     *
     * @param theReader Reader from which the model will be loaded.
     */
    public ReaderModelLoader(Reader theReader) {
        this.reader = theReader;
    }


    /*
     * @see ProfileModelLoader#loadModel(ProfileReference)
     */
    public Collection loadModel(ProfileReference reference)
        throws ProfileException {

        if (reader == null) {
            LOG.log(Level.SEVERE, "Profile not found");
            throw new ProfileException("Profile not found!");
        }

        try {
            XmiReader xmiReader = Model.getXmiReader();
            InputSource inputSource = new InputSource(reader);
            inputSource.setSystemId(reference.getPath());
            inputSource.setPublicId(
                    reference.getPublicReference().toString());
            Collection elements = xmiReader.parse(inputSource, true);
            return elements;
        } catch (UmlException e) {
            throw new ProfileException("Invalid XMI data!", e);
        }
    }

}
