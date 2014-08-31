/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    linus
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2005-2006 The Regents of the University of California. All
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

package org.argouml.model.mdr;

import junit.framework.TestCase;

import org.argouml.model.UmlException;

/**
 * Base test case for MDR Model tests.
 */
public abstract class AbstractMDRModelImplementationTestCase extends TestCase {

    /**
     * The ModelImplementation.<p>
     *
     * The reason for not having this as a member variable that is created by
     * {@link #setUp()} is that the MDR is the initialized several times and
     * the current implementation fails on the second initialization.
     */
    protected static MDRModelImplementation modelImplementation;
    
    /**
     * Initialization state.
     */
    private boolean initialized = false;
    
    protected void init() {
        try {            
            System.setProperty(
                    "org.netbeans.mdr.storagemodel.StorageFactoryClassName",
                    "org.netbeans.mdr.persistence.memoryimpl.StorageFactoryImpl");
            System.setProperty("org.netbeans.lib.jmi.Logger", "0");
            System.setProperty("org.netbeans.mdr.Logger", "0");
            modelImplementation = new MDRModelImplementation();
            initialized = true;
        } catch (UmlException e) {
            e.printStackTrace();
        }
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        if (!initialized) {
            init();
        }
    }

}
