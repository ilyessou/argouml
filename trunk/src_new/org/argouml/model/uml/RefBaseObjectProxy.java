// $Id$
// Copyright (c) 2003-2005 The Regents of the University of California. All
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import javax.jmi.reflect.RefBaseObject;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.model_management.MPackage;

/**
 * @author Thierry
 */
public class RefBaseObjectProxy implements InvocationHandler, RefBaseObject {

    private static final Logger LOG =
        Logger.getLogger(RefBaseObjectProxy.class);

    private Object realObject;

    /**
     * Returns the actual object which was proxied.
     *
     * @param o proxied object to extract from
     * @return the realObject behind the proxy
     */
    public static Object getProxiedObject(RefBaseObjectProxy o) {
        return o.realObject;
    }

    /** Creates a new instance of the proxied object.
     *
     * @param obj to proxy
     * @return a proxy object if obj does not already implement the interface.
     */
    public static Object newInstance(Object obj) {
        Class[] newInterfaces = null;
        if (obj instanceof RefBaseObject) {
            // We don't need to add the interface
            return obj;
        }
        // Get the set of interfaces of the object
        Class[] oldInterfaces = obj.getClass().getInterfaces();
        /*
         * Create an array to contain the old set of interfaces
         * plus the one we are adding
         */
        newInterfaces = new Class[oldInterfaces.length + 1];
        // Copy the old interfaces into the new array
        System.arraycopy(oldInterfaces, 0,
                         newInterfaces, 0, oldInterfaces.length);
        // Add our new interface
        newInterfaces[oldInterfaces.length] = RefBaseObject.class;
        // Now return the proxy
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                                      newInterfaces,
                                      new RefBaseObjectProxy(obj));
    }

    /**
     * @param obj The object to proxy
     */
    public RefBaseObjectProxy(Object obj) {
        realObject = obj;
    }

    /**
      * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
      * java.lang.reflect.Method, java.lang.Object[])
      */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        Object result = null;

        LOG.debug("method: " + method.getName());

        if (method.getName().equals("refMetaObject")) {
            result = refMetaObject();
        }
        else if (method.getName().equals("refMofId")) {
            result = refMofId();
        }
        else if (method.getName().equals("refImmediatePackage")) {
            result = refImmediatePackage();
        }
        else if (method.getName().equals("refOutermostPackage")) {
            result = refOutermostPackage();
        }
        else {
            try {
                LOG.debug("Executing " + method.getName());
                result = method.invoke(realObject, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        return result;
    }

    /**
     * @see javax.jmi.reflect.RefBaseObject#refMetaObject()
     */
    public RefObject refMetaObject() {
        return null;
    }

    /**
     * @see javax.jmi.reflect.RefBaseObject#refImmediatePackage()
     */
    public RefPackage refImmediatePackage() {
        if (realObject instanceof MBase) {
            MBase base = (MBase) realObject;
            Object container = base.getModelElementContainer();
            while (container != null) {
                if (container instanceof MPackage) {
                    return (RefPackage) container;
                }
            }
        }
        return null;
    }

    /**
     * @see javax.jmi.reflect.RefBaseObject#refOutermostPackage()
     */
    public RefPackage refOutermostPackage() {
        Object outermost = null;
        if (realObject instanceof MBase) {
            MBase base = (MBase) realObject;
            Object container = base.getModelElementContainer();
            while (container != null) {
                if (container instanceof MPackage) {
                    outermost = container;
                }
            }
        }
        return (RefPackage) outermost;
    }

    /**
     * @see javax.jmi.reflect.RefBaseObject#refMofId()
     */
    public String refMofId() {
        if (realObject instanceof MBase) {
            MBase base = (MBase) realObject;
            return base.getUUID();
        }
        return null;
    }

    /**
     * @see javax.jmi.reflect.RefBaseObject#refVerifyConstraints(boolean)
     */
    public Collection refVerifyConstraints(boolean arg0) {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * @return the proxied object
     */
    protected Object getRealObject() {
        return realObject;
    }

}
