// $Id $
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argouml.uml.notation;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author Michiel
 */
public class TestNotationProvider extends TestCase {

    /**
     * Test the existence of the 
     * toString(Object modelElement, HashMap args) method.
     * TODO: Need to find a more usefull test.
     */
    public void testToString() {
        NotationProvider np = new NPImpl();
        HashMap args = new HashMap();
        args.put("b", "c");
        assertTrue("Test toString()", "a1".equals(np.toString("a", args)));
        args.put("d", "e");
        assertTrue("Test toString()", "f2".equals(np.toString("f", args)));
    }
    
    /**
     * Test the isValue utility function.
     */
    public void testIsValue() {
        HashMap args = new HashMap();
        args.put("not a boolean", "c");
        args.put("true", Boolean.TRUE);
        args.put("false", Boolean.FALSE);
        args.put("null", null);
        assertTrue("Not a boolean", 
                !NotationProvider.isValue("not a boolean", args));
        assertTrue("Finding true", 
                NotationProvider.isValue("true", args));
        assertTrue("Finding false", 
                !NotationProvider.isValue("false", args));
        assertTrue("Finding null", 
                !NotationProvider.isValue("null", args));
        assertTrue("Not encountered", 
                !NotationProvider.isValue("xyz", args));
    }

    private class NPImpl extends NotationProvider {

        /**
         * @see org.argouml.uml.notation.NotationProvider#getParsingHelp()
         */
        public String getParsingHelp() {
            return null;
        }

        public String toString(Object modelElement, HashMap args) {
            return modelElement.toString() + args.size();
        }

        public void parse(Object modelElement, String text) {

        }
        
    }
}
