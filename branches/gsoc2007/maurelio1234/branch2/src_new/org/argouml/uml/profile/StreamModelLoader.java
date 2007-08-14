// $Id: StreamModelLoader.java 13040 2007-07-10 20:00:25Z linus $
// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.uml.profile;

import java.io.InputStream;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.argouml.model.UmlException;
import org.argouml.model.XmiReader;
import org.xml.sax.InputSource;

/**
 * Loads models from a given InputStream
 *
 * @author Marcos Aur�lio
 */
public abstract class StreamModelLoader implements ProfileModelLoader {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(StreamModelLoader.class);

    /**
     * @param path the path of the model
     * @return the loaded model
     * @throws ProfileException
     * @see org.argouml.uml.profile.ProfileModelLoader#loadModel(java.lang.String)
     */
    public abstract Collection loadModel(String path) throws ProfileException;

    /**
     * @param is the stream from where the model should be loaded
     * @return the model
     * @throws ProfileException if the XMIReader couldn't read the input stream
     */
    protected Collection loadModel(InputStream is) throws ProfileException {
        if (is != null) {
            try {
                XmiReader xmiReader = Model.getXmiReader();
                InputSource inputSource = new InputSource(is);
                Collection elements = xmiReader.parse(inputSource, true);
                return elements;
            } catch (UmlException e) {
                LOG.error("Exception while loading profile ", e);
                throw new ProfileException("Invalid XMI data!");
            }
        }
        LOG.error("Profile not found");
        throw new ProfileException("Profile not found!");	
    }
}
