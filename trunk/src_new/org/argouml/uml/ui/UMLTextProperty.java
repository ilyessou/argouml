// Copyright (c) 1996-2002 The Regents of the University of California. All
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
import org.argouml.application.api.Argo;
import org.argouml.kernel.*;
import org.argouml.ui.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.*;

import javax.swing.JOptionPane;
import ru.novosoft.uml.*;

public class UMLTextProperty  {
    private Method _getMethod;
    private Method _setMethod;
    protected String _propertyName;
    static private Object[] _noArg = {};
    
    public UMLTextProperty(String propertyName) {
        _propertyName = propertyName;
    }
    
    public UMLTextProperty(Class elementClass,String propertyName,String getMethod,String setMethod) {
        _propertyName = propertyName;
        Class[] noClass = {};
        try {
            _getMethod = elementClass.getMethod(getMethod,noClass);
        }
        catch(Exception e) {
            System.out.println(e.toString() + " in UMLTextProperty: " + getMethod);
        }
        Class[] stringClass = { String.class };
        try {
            _setMethod = elementClass.getMethod(setMethod,stringClass);
        }
        catch(Exception e) {
            System.out.println(e.toString() + " in UMLTextProperty: " + setMethod);
        }
    }

    public void setProperty(UMLUserInterfaceContainer container,String newValue) throws Exception {
        if(_setMethod != null) {
            Object element = container.getTarget();
            if(element != null) {
               

					
                    	String oldValue = getProperty(container);
					
                    //
                    //  if one or the other is null or they are not equal
                    if(newValue == null || oldValue == null || !newValue.equals(oldValue)) {
                        //
                        //  as long as they aren't both null 
                        //   (or a really rare identical string pointer)
                        if(newValue != oldValue) {
                            Object[] args = { newValue };
                            // 2002-07-18
                            // Jaap Branderhorst
                            // Patch for issue 738
                            // if the setmethod trows a PropertyVetoException it should be handled.
                            // it's handled by showing the user the message in the exception and not
                            // marking the project for change if it is thrown.
                            // this way the setmethod itself can check on some issues.
                            try {
                            	_setMethod.invoke(element,args);
                            	// Mark the project as having been changed 
                            	Project p = ProjectBrowser.TheInstance.getProject(); 
								if (p != null) p.setNeedsSave(true); 
                            }
                            catch (InvocationTargetException inv) {
                            	Throwable targetException = inv.getTargetException();
                            	if (!(targetException instanceof PropertyVetoException)) {
                            		Argo.log.error(inv);
                            		Argo.log.error(targetException);
                            	}
                   				if (targetException instanceof Exception) {
                   					throw (Exception)targetException;
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
        if(_getMethod != null) {
            Object element = container.getTarget();
            if(element != null) {
                try {
                    Object obj =  _getMethod.invoke(element,_noArg);
                    if(obj != null) value = obj.toString();
                }
                catch(Exception e) {
                    System.out.println(e.toString() + " in UMLTextProperty.getMethod()");
                }
            }
        }
        return value;
    }
    
    boolean isAffected(MElementEvent event) {
        String sourceName = event.getName();
        if(_propertyName == null || sourceName == null || sourceName.equals(_propertyName))
            return true;
        return false;
    }
    
    void targetChanged() {
    }
}


