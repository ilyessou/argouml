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

// File: ActionReorder.java
// Classes: ActionReorder
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.util.*;
import java.awt.*;

/** Action to change the back-to-front ordering of Fig's.
 *  <A HREF="../features.html#reorder_objects">
 *  <TT>FEATURE: reorder_objects</TT></A>
 *
 * @see LayerDiagram#reorder */

public class ActionReorder extends Action {
  ////////////////////////////////////////////////////////////////
  // constants
  public static final int SEND_TO_BACK = 1;
  public static final int BRING_TO_FRONT = 2;
  public static final int SEND_BACKWARD = 3;
  public static final int BRING_FORWARD = 4;

  ////////////////////////////////////////////////////////////////
  // instance variables
  private int function;

  ////////////////////////////////////////////////////////////////
  // constructor

  /** Construct a new ActionReorder with the given reordering
    constrant (see above) */
  public ActionReorder(int f) { function = f; }

  ////////////////////////////////////////////////////////////////
  // Action API

  public String name() { return "Reorder Objects"; }

  public void doIt(java.awt.Event e) {
    Editor ce = Globals.curEditor();
    LayerManager lm = ce.getLayerManager();
    SelectionManager sm = ce.getSelectionManager();
    sm.startTrans();
    sm.reorder(function, lm.getActiveLayer());
    sm.endTrans();
    ce.repairDamage();
  }

  public void undoIt() {
    System.out.println("Connot undo ActionReorder, yet");
  }

} /* end class ActionReorder */

