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

package org.argouml.persistence;


/** this needs work,AFAIK none of these strings are
 * saved in the final output in a zargo.
 * @author Jim Holt
 */

public class ArgoTokenTable extends XMLTokenTableBase {

    /**
     * The constructor.
     *
     */
    public ArgoTokenTable() {
	super(32);
    }

    ////////////////////////////////////////////////////////////////
    // constants
    private static final String STRING_ARGO                   = "argo";
    /** doesn't work
     */
    private static final String STRING_AUTHORNAME            = "authorname";
    private static final String STRING_VERSION               = "version";
    private static final String STRING_DESCRIPTION           = "description";
    private static final String STRING_SEARCHPATH            = "searchpath";
    private static final String STRING_MEMBER                = "member";
    private static final String STRING_HISTORYFILE           = "historyfile";
    private static final String STRING_DOCUMENTATION         = "documentation";

    /** The token for argo. */
    public static final int    TOKEN_ARGO                    = 1;
    /** The token for authroname. */
    public static final int    TOKEN_AUTHORNAME              = 2;
    /** The token for version. */
    public static final int    TOKEN_VERSION                 = 3;
    /** The token for description. */
    public static final int    TOKEN_DESCRIPTION             = 4;
    /** The token for search path. */
    public static final int    TOKEN_SEARCHPATH              = 5;
    /** The token for member. */
    public static final int    TOKEN_MEMBER                  = 6;
    /** The token for history file. */
    public static final int    TOKEN_HISTORYFILE             = 7;
    /** This can be saved successfully however there is no
     * way to output this information.
     * The token for argo. */
    public static final int    TOKEN_DOCUMENTATION           = 8;
    /** The token for undefined. */
    public static final int    TOKEN_UNDEFINED               = 9;

    ////////////////////////////////////////////////////////////////
    // protected methods

    /**
     * @see org.argouml.persistence.XMLTokenTableBase#setupTokens()
     */
    protected void setupTokens() {
	addToken(STRING_ARGO, new Integer(TOKEN_ARGO));
	addToken(STRING_AUTHORNAME, new Integer(TOKEN_AUTHORNAME));
	addToken(STRING_VERSION, new Integer(TOKEN_VERSION));
	addToken(STRING_DESCRIPTION, new Integer(TOKEN_DESCRIPTION));
	addToken(STRING_SEARCHPATH, new Integer(TOKEN_SEARCHPATH));
	addToken(STRING_MEMBER, new Integer(TOKEN_MEMBER));
	addToken(STRING_HISTORYFILE, new Integer(TOKEN_HISTORYFILE));
    }

} /* end class ArgoTokenTable */
