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

/*
 * UMLException.java
 *
 * Created on 16 March 2003, 12:38
 */

package org.argouml.model;

import org.apache.log4j.Logger;

/**
 *
 * @author  unknown (Bob Tarling?)
 */
public class UmlException extends Exception {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(UmlException.class);

    /**
     * Contains the causing Exception (or <code>null</code>).
     */
    private final Throwable cause;

    /**
     * Constructor.
     */
    public UmlException() {
	super();
	cause = null;
    }

    /**
     * Constructor.
     *
     * @param message the message
     */
    public UmlException(String message) {
	super(message);
	cause = null;
    }

    /**
     * @param message the message
     * @param c the cause of the exception
     */
    public UmlException(String message, Throwable c) {
        super(message);
        cause = c;
    }

    /**
     * @param c the cause of the exception
     */
    public UmlException(Throwable c) {
        super();
        cause = c;
    }

    /**
     * @see java.lang.Throwable#getCause()
     */
    public Throwable getCause() {
	return cause;
    }

    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace() {
	super.printStackTrace();
	if (cause != null) {
	    LOG.error("Caused by:", cause);
	}
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace(java.io.PrintStream ps) {
	super.printStackTrace(ps);
	if (cause != null) {
	    ps.println("Caused by:");
	    cause.printStackTrace(ps);
	}
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace(java.io.PrintWriter pw) {
	super.printStackTrace(pw);
	if (cause != null) {
	    pw.println("Caused by:");
	    cause.printStackTrace(pw);
	}
    }
}

