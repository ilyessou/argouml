// $Id$
// Copyright (c) 1996-2001 The Regents of the University of California. All
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

import org.argouml.uml.diagram.ui.*;
import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;
import java.awt.event.*;
import java.util.*;
import org.argouml.model.ModelFacade;

/**
 * @deprecated as of 0.15.2 replace with {@link 
 *  org.argouml.uml.diagram.ui.ActionMultiplicity}, remove 0.15.3, alexb
 */

public class ActionMultiplicity extends UMLAction {
    String str = "";
    Object/*MMultiplicity*/ mult = null;


    ////////////////////////////////////////////////////////////////
    // static variables

    // multiplicity
    public static UMLAction SrcMultOne =
	new ActionMultiplicity(ModelFacade.M1_1_MULTIPLICITY, "src");
    public static UMLAction DestMultOne =
	new ActionMultiplicity(ModelFacade.M1_1_MULTIPLICITY, "dest");

    public static UMLAction SrcMultZeroToOne =
	new ActionMultiplicity(ModelFacade.M0_1_MULTIPLICITY, "src");
    public static UMLAction DestMultZeroToOne =
	new ActionMultiplicity(ModelFacade.M0_1_MULTIPLICITY, "dest");

    public static UMLAction SrcMultZeroToMany =
	new ActionMultiplicity(ModelFacade.M0_N_MULTIPLICITY, "src");
    public static UMLAction DestMultZeroToMany =
	new ActionMultiplicity(ModelFacade.M0_N_MULTIPLICITY, "dest");

    public static UMLAction SrcMultOneToMany =
	new ActionMultiplicity(ModelFacade.M1_N_MULTIPLICITY, "src");
    public static UMLAction DestMultOneToMany =
	new ActionMultiplicity(ModelFacade.M1_N_MULTIPLICITY, "dest");


    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionMultiplicity(Object/*MMultiplicity*/ m, String s) {
	//super(m.getLower() + ".." + m.getUpper(), NO_ICON);
	super(m.toString(), NO_ICON);
	str = s;
	mult = m;
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent ae) {
	Vector sels = Globals.curEditor().getSelectionManager().selections();
	if ( sels.size() == 1 ) {
	    Selection sel = (Selection) sels.firstElement();
	    Fig f = sel.getContent();
	    Object owner = ((FigEdgeModelElement) f).getOwner();
	    Collection ascEnds = ModelFacade.getConnections(owner);
            Iterator iter = ascEnds.iterator();
	    Object ascEnd = null;
	    if (str.equals("src")) {
		ascEnd = iter.next();
            }
	    else {
                while (iter.hasNext()) {
                    ascEnd = iter.next();
                }
            }
	    ModelFacade.setMultiplicity(ascEnd, mult);
	}
    }

    public boolean shouldBeEnabled() { 
	return true; 
    }
} /* end class ActionSrcMultOneToMany */