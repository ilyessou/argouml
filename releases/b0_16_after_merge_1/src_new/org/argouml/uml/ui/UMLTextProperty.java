// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import org.apache.log4j.Logger;
import org.argouml.kernel.*;
import java.lang.reflect.*;
import ru.novosoft.uml.MElementEvent;

/**
 * @deprecated as of ArgoUml 0.13.5 (10-may-2003),
 * TODO: What is this replaced by?,
 * this class is part of the 'old'(pre 0.13.*) implementation of proppanels
 * that used reflection a lot.
 */
public class UMLTextProperty  {

    /** logger */
    private static Logger cat = Logger.getLogger(UMLTextProperty.class);

    private Method _getMethod;
    private Method _setMethod;
    protected String _propertyName;
    private static Object[] _noArg = {};
    private Class _objectClass;

    /**
     * This constructor seems completely useless. And indeed noone uses it.
     * Neither should you.
     */
    public UMLTextProperty(String propertyName) {
        _propertyName = propertyName;
    }
    
    public UMLTextProperty(Class elementClass, String propertyName,
			   String getMethod, String setMethod) {
	_objectClass = elementClass;
        _propertyName = propertyName;
        Class[] noClass = {};

	try {
            _getMethod = elementClass.getMethod(getMethod, noClass);
        }
        catch (Exception e) {
            cat.error(e.toString() + " in UMLTextProperty: " + getMethod, e);
            // 2002-07-20
            // Jaap Branderhorst
            // If it is illegal we should throw an exception
            throw new IllegalArgumentException("The method "
					       + getMethod
					       + " is not a legal method to "
					       + "get the property "
					       + propertyName);
        }

        Class[] stringClass = { String.class };

        try {
            _setMethod = elementClass.getMethod(setMethod, stringClass);
        }
        catch (Exception e) {
            cat.error(e.toString() + " in UMLTextProperty: " + setMethod, e);
            // 2002-07-20
            // Jaap Branderhorst
            // If it is illegal we should throw an exception
            throw new IllegalArgumentException("The method "
					       + setMethod
					       + " is not a legal method "
					       + "to set the property "
					       + propertyName);
        }
    }
    
    public void setProperty(UMLUserInterfaceContainer container,
			    String newValue)
	throws Exception {

    	setProperty(container, newValue, false);

    }

    public void setProperty(UMLUserInterfaceContainer container,
			    String newValue, boolean vetoableCheck)
	throws Exception {
	
        if (_setMethod != null) {
            Object element = container.getTarget();

            if (element != null) {
		String oldValue = getProperty(container);

		//  if one or the other is null or they are not equal
		if (newValue == null
		    || oldValue == null
		    || !newValue.equals(oldValue)) {

		    //  as long as they aren't both null 
		    //   (or a really rare identical string pointer)
		    if (newValue != oldValue) {
			try {
			    Object[] args = { newValue };

			    _setMethod.invoke(element, args);

			    // Mark the project as having been changed 
			    Project p =
				ProjectManager.getManager().getCurrentProject();
			    if (p != null) p.setNeedsSave(true); 
			}
			catch (InvocationTargetException inv) {
			    Throwable targetException =
				inv.getTargetException();
			    cat.error(inv);
			    cat.error(targetException);
			    if (targetException instanceof Exception) {
				throw (Exception) targetException;
			    }
			    System.exit(-1); // we have a real error
			}
		    }
		}  
            }
        }
    }

    
    public String getProperty(UMLUserInterfaceContainer container) {
        String value = null;
        if (_getMethod != null) {
            Object element = container.getTarget();
            if (element != null
		&& _objectClass.isAssignableFrom(element.getClass())) {
                try {
                    Object obj =  _getMethod.invoke(element, _noArg);
                    if (obj != null) value = obj.toString();
                }
                catch (InvocationTargetException e) {
                    cat.error(e.getTargetException().toString()
			      + " is invocationtargetexception "
			      + "in UMLTextProperty.getMethod()", 
			      e.getTargetException());
                }
                catch (Exception e) {
                    cat.error(e.toString() + " in UMLTextProperty.getMethod()",
			      e);
                }
            }
        }
        return value;
    }
    
    boolean isAffected(MElementEvent event) {
        String sourceName = event.getName();
        if (_propertyName == null
	    || sourceName == null
	    || sourceName.equals(_propertyName)) {

            return true;

	}
        return false;
    }
    
    void targetChanged() {
    }
}
