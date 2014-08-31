/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    maurelio1234
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

package org.argouml.uml.cognitive.critics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.argouml.cognitive.Critic;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ListSet;
import org.argouml.cognitive.ToDoItem;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;
import org.argouml.uml.cognitive.UMLToDoItem;

/**
 * Well-formedness rule [1] for Namespace. See page 33 of UML 1.1 Semantics.
 * OMG document ad/97-08-04. <p>
 *
 * Well-formedness rule [1] for Namespace. See page 62 of UML 1.4
 * Semantics. OMG document UML 1.4.2 formal/04-07-02.
 *
 * Names of contained elements in a namespace (i.e. the design material) 
 * must be unique. 
 * This condition does not apply to names of Generalizations, 
 * or elements without name.
 */
public class CrNameConflict extends CrUML {

    /**
     * The constructor.
     */
    public CrNameConflict() {
        setupHeadAndDesc();
        addSupportedDecision(UMLDecision.NAMING);
        setKnowledgeTypes(Critic.KT_SYNTAX);
        addTrigger("name");
        addTrigger("feature_name");
    }

    /*
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     *      java.lang.Object, org.argouml.cognitive.Designer)
     */
    @Override
    public boolean predicate2(Object dm, Designer dsgr) {
        return computeOffenders(dm).size() > 1;
    }

    /*
     * @see org.argouml.cognitive.critics.Critic#toDoItem( java.lang.Object,
     *      org.argouml.cognitive.Designer)
     */
    @Override
    public ToDoItem toDoItem(Object dm, Designer dsgr) {
        ListSet offs = computeOffenders(dm);
        return new UMLToDoItem(this, offs, dsgr);
    }

    /**
     * @param dm
     *            the object to check
     * @return the set of offenders
     */
    protected ListSet computeOffenders(Object dm) {
        ListSet offenderResult = new ListSet();
        if (Model.getFacade().isANamespace(dm)) {
            HashMap<String, Object> names = new HashMap<String, Object>();
            for (Object name1Object :  Model.getFacade().getOwnedElements(dm)) {
                if (!Model.getFacade().isANamedElement(name1Object)) {
                    continue;
                }
                // TODO: Do we need this Generalization is not a named element
                if (Model.getFacade().isAGeneralization(name1Object)) {
                    continue;
                }
                String name = Model.getFacade().getName(name1Object);
                if (name == null) {
                    continue;
                }
                if ("".equals(name)) {
                    continue;
                }
                if (names.containsKey(name)) {
                    Object offender = names.get(name);
                    if (!offenderResult.contains(offender)) {
                        offenderResult.add(offender);
                    }
                    offenderResult.add(name1Object);
                }
                names.put(name, name1Object);
            }
        }
        return offenderResult;
    }

    /*
     * @see org.argouml.cognitive.Poster#stillValid(
     *      org.argouml.cognitive.ToDoItem, org.argouml.cognitive.Designer)
     */
    @Override
    public boolean stillValid(ToDoItem i, Designer dsgr) {
        if (!isActive()) {
            return false;
        }
        ListSet offs = i.getOffenders();

        // first element is e.g. the class, but we need to have its namespace
        // to recompute the offenders.
        Object f = offs.get(0);
        Object ns = Model.getFacade().getNamespace(f);
        if (!predicate(ns, dsgr)) {
            return false;
        }
        ListSet newOffs = computeOffenders(ns);
        boolean res = offs.equals(newOffs);
        return res;
    }

    /*
     * @see org.argouml.uml.cognitive.critics.CrUML#getCriticizedDesignMaterials()
     */
    @Override
    public Set<Object> getCriticizedDesignMaterials() {
        Set<Object> ret = new HashSet<Object>();
        ret.add(Model.getMetaTypes().getNamespace());
        return ret;
    }
    
}
