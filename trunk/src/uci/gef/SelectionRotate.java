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




// File: SelectionRotate.java
// Classes: SelectionRotate
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.util.*;
import java.awt.*;

/** Selection object that allows the user to rotate the selected Fig.
 * Needs-more-work: not implemented yet. */

public class SelectionRotate extends Selection {

  /** Construct a new SelectionRotate around the given DiagramElement */
  public SelectionRotate(Fig f) { super(f); }

  /** Paint the selection. */
  public void paint(Graphics g) {
    int x = _content.getX();
    int y = _content.getY();
    int w = _content.getWidth();
    int h = _content.getHeight();
    g.setColor(Globals.getPrefs().handleColorFor(_content));
    g.fillOval(x - HAND_SIZE, y - HAND_SIZE, HAND_SIZE, HAND_SIZE);
    g.fillOval(x + w, y - HAND_SIZE, HAND_SIZE, HAND_SIZE);
    g.fillOval(x - HAND_SIZE, y + h, HAND_SIZE, HAND_SIZE);
    g.fillOval(x + w, y + h, HAND_SIZE, HAND_SIZE);
  }

  /** Rotate the Fig when the user drags the roataion
   *  handle(s). Needs-more-work: not implemented yet.  */
  public void dragHandle(int mx, int my, int an_x,int an_y, Handle h) {
    /* do nothing */
  }

  /** Returns -2 to indicate that the user did not click on a handle
   *  or the body of the Fig. Needs-more-work. */
  public int hitHandle(Rectangle r) { return -2; }

  static final long serialVersionUID = -1661014460931572067L;
} /* end class SelectionRotate */

