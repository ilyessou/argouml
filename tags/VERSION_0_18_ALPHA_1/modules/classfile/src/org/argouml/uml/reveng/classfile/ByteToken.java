// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.reveng.classfile;

import antlr.*;

/**
 * A class representing a Token that is a byte.
 *
 */
public class ByteToken extends Token {

    //////////////////////
    // Instance variables.

    private byte val = 0;


    ///////////////
    // Constructors

    /**
     * Create a new ByteToken instance with a given type.
     *
     * @param type The type of the ByteToken.
     */
    public ByteToken( int type) {
	super(type);
    }

    /**
     * Create a new ByteToken instance with a given type and
     * byte value.
     *
     * @param type The type of the token.
     * @param value The byte value of the token.
     */
    public ByteToken( int type, byte value) {
	this(type);
	setValue(value);
    }


    //////////
    // Methods

    /**
     * Set the byte value of this token.
     *
     * @param value The new byte value.
     */
    final void setValue( byte value) {
	val = value;
    }

    /**
     * Get the byte value of this token.
     *
     * @return the byte value of this token.
     */
    final byte getValue() {
	return val;
    }

    /**
     * Get the value of the byte as a masked short (no sign extension if < 0).
     *
     * @return The byte value of this token as a masked sort.
     */
    final short getShortValue() {
	return (short) ((short) val & (short) 0xff);
    }

    /**
     * Get the value of the byte as a masked int (no sign extension if < 0).
     *
     * @return The byte value of this token as a masked int.
     */
    final int getIntValue() {
	return (int) val & 0xff;
    }
}



