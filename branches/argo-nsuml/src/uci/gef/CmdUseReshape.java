// Copyright (c) 1996-99 The Regents of the University of California. All
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




// File: CmdUseReshape.java
// Classes: CmdUseReshape
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.awt.*;
import java.util.*;
import java.util.Enumeration;

/** Set the current editor to use a SelectionReshape on its current
 *  selections.
 *
 *  @see Editor
 *  @see Selection
 *  @see SelectionReshape
 */

public class CmdUseReshape extends Cmd {

  public CmdUseReshape() { super("Use Reshape Handles", NO_ICON); }

  public void doIt() {
    Editor ce = Globals.curEditor();
    SelectionManager sm = ce.getSelectionManager();
    Enumeration sels = ((Vector)sm.selections().clone()).elements();
    //Enumeration sels = sm.selections().elements();
    while (sels.hasMoreElements()) {
      Selection s = (Selection) sels.nextElement();
      if (s instanceof Selection && !(s instanceof SelectionReshape)) {
	Fig f = s.getContent();
	if (f.isReshapable()) {
	  ce.damaged(s);
	  sm.removeSelection(s);
	  SelectionReshape sr = new SelectionReshape(f);
	  sm.addSelection(sr);
	  ce.damaged(sr);
	}
      }
    }
  }

  public void undoIt() { System.out.println("not done yet"); }

  static final long serialVersionUID = -5876319717228304371L;

} /* end class CmdUseReshape */

