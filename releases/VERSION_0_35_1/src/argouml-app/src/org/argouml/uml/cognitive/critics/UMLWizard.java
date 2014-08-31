/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2003-2007 The Regents of the University of California. All
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


package org.argouml.uml.cognitive.critics;

import org.argouml.cognitive.ListSet;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Wizard;
import org.argouml.model.Model;


/**
 * UMLWizard is an abstract convenience implementation for UML Wizards,
 * which provides common methods to all its subclasses.
 * @author mkl
 * @see org.argouml.cognitive.critics.Wizard
 */
public abstract class UMLWizard extends Wizard {

    /**
     * The suggestion string.
     */
    private String suggestion;

    /**
     * The constructor.
     *
     */
    public UMLWizard() {
        super();
    }

    /**
     * Preset the number of steps to 1. You need to override this
     * method, in case your Wizard requires a different number of steps.
     * This method is a convenience implementation.
     * 
     * {@inheritDoc}
     */
    public int getNumSteps() {
        return 1;
    }

    /**
     * @return the offending modelelement
     */
    public Object getModelElement() {
        if (getToDoItem() != null) {
            ToDoItem item = (ToDoItem) getToDoItem();
            ListSet offs = item.getOffenders();
            if (offs.size() >= 1) {
                Object me = offs.get(0);
                return me;
            }
        }
        return null;
    }

    /**
     * @return the suggestion string
     */
    public String offerSuggestion() {
        if (suggestion != null) {
            return suggestion;
        }
        Object me = getModelElement();
        if (me != null) {
            String n = Model.getFacade().getName(me);
            return n;
        }
        return "";
    }

    /**
     * @param s set a new suggestion string
     */
    public void setSuggestion(String s) {
	suggestion = s;
    }

    /**
     * @return returns the suggestion string
     */
    public String getSuggestion() {
        return suggestion;
    }
}
