// $Id$
// Copyright (c) 1996-2002, 2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.use_case.ui;

import junit.framework.*;


/**
 * Test if a Fig is cloneable.
 *
 */
public class TestFigClonable extends TestCase {

    /**
     * The constructor.
     *
     * @param name the test name
     */
    public TestFigClonable(String name) {
	super(name);
    }

    /** try to clone FigUseCase in package diagram.use_case.ui.
     */
    public void testUseCaseClonable() {
	try {
	    FigUseCase usecase = new FigUseCase();
	    FigUseCase usecaseclone;

	    usecaseclone = (FigUseCase) usecase.clone();
	    assertTrue("FigUseCase cloned", true);
	}
	catch (Exception e) {
	    assertTrue("FigUseCase not clonable", false);
	}
    }

    /** clone FigActor. */
    public void testActorClonable() {
	try {
	    FigActor actor = new FigActor();
	    FigActor actorclone;

	    actorclone = (FigActor) actor.clone();
	    assertTrue("FigActor cloned", true);
	}
	catch (Exception e) {
	    assertTrue("FigActor not clonable", false);
	}
    }
}
