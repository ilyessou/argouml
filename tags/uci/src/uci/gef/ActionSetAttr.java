// Copyright (c) 1995, 1996 Regents of the University of California.
// All rights reserved.
//
// This software was developed by the Arcadia project
// at the University of California, Irvine.
//
// Redistribution and use in source and binary forms are permitted
// provided that the above copyright notice and this paragraph are
// duplicated in all such forms and that any documentation,
// advertising materials, and other materials related to such
// distribution and use acknowledge that the software was developed
// by the University of California, Irvine.  The name of the
// University may not be used to endorse or promote products derived
// from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
// WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.

// File: ActionSetAttr.java
// Classes: ActionSetAttr
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.util.*;
import java.awt.*;

/** Action to set the graphical attributes of a selected
 *  Fig.  For example, this is used to set line color and
 *  fill color.  Multiple graphical attributes can be set at once, that
 *  could allow the definition of styles in the future.
 *
 * @see Fig */

public class ActionSetAttr extends Action {

  /** the graphical attributes to apply to the selection. */
  private Hashtable _newAttrs = new Hashtable(3);

  /** Construct a new ActionSetAttr that sets one attribute value. */
  public ActionSetAttr(String attrName, Object value) {
    _newAttrs.put(attrName, value);
  }

  /** Construct a new ActionSetAttr that sets all the given attributes. */
  public ActionSetAttr(Hashtable gAttrs) { _newAttrs = gAttrs; }

  public String name() { return "Set Object Attributes"; }

  public void doIt(java.awt.Event e) {
    Editor curEditor = Globals.curEditor();
    if (curEditor == null) return;
    SelectionManager sm = curEditor.getSelectionManager();
    if (sm.getLocked()) {
      Globals.showStatus("Cannot Modify Locked Objects");
      return;
    }

    if (sm.size() != 0) {
      sm.startTrans();
      //sel.put(_newAttrs);
      sm.endTrans();
      curEditor.repairDamage();
      /* I must explicitly call repairDamage because that editor might
	 not be getting any events... */
    }
    else {
      curEditor.put(_newAttrs);
    }
  }

  public void undoIt() {
    System.out.println("ActionSetAttr.undoIt() is not implemented yet");
  }

} /* end class ActionSetAttr */
