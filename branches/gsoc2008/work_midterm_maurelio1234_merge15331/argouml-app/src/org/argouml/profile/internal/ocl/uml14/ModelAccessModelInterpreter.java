// $Id: eclipse-argo-codetemplates.xml 11347 2006-10-26 22:37:44Z linus $
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

package org.argouml.profile.internal.ocl.uml14;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.argouml.model.Facade;
import org.argouml.model.Model;
import org.argouml.profile.internal.ocl.ModelInterpreter;

/**
 * Model Access
 * 
 * @author maurelio1234
 */
public class ModelAccessModelInterpreter implements ModelInterpreter {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(ModelAccessModelInterpreter.class);

    /**
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#invokeFeature(java.util.HashMap,
     *      java.lang.Object, java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public Object invokeFeature(HashMap<String, Object> vt, Object subject,
            String feature, String type, Object[] parameters) {

        if (subject == null) {
            subject = vt.get("self");
        }

        if (Model.getFacade().isAModelElement(subject)) {
            if (type.equals(".")) {
                if (feature.equals("name")) {
                    return Model.getFacade().getName(subject);
                }
            }
        }

        if (Model.getFacade().isAClass(subject)) {
            if (type.equals(".")) {
                if (feature.equals("feature")) {
                    Set<Object> ret = new HashSet<Object>();
                    ret.addAll(Model.getCoreHelper().getAllAttributes(subject));
                    ret.addAll(Model.getCoreHelper().getOperationsInh(subject));
                    return ret;
                }
            }
        }

        return null;
    }

    /**
     * Add the metamodel-metaclasses as built-in symbols
     * @param sym the symbol
     * @return the value of the symbol
     * 
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#getBuiltInSymbol(java.lang.String)
     */
    public Object getBuiltInSymbol(String sym) {
        Method m;
        try {
            m = Facade.class.getDeclaredMethod("isA" + sym,
                    new Class[] {Object.class});
            if (m != null) {
                return new OclType(sym.toString());
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return null;
    }

}
