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


// File: ColorTilePanel.java
// Classes: ColorTilePanel
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.beans.editors;

import java.awt.*;
import java.applet.*;
import java.util.*;

/** A Panel that shows an array of little colored squares to allow the
 *  user to pick a color. By default I use "netscape colors" which are
 *  colors that Netscape Navigator (TM) tries to allocate when it
 *  starts. */

public class ColorTilePanel extends Panel {
  ////////////////////////////////////////////////////////////////
  // constants
  public final static int TILESIZE = 6;

  ////////////////////////////////////////////////////////////////
  // instance variables

  /** The colors that will be shown */
  protected Vector _colors;

  /** The index of the selected color */
  protected int _selected = 0;

  /** The number of columns to use when displaying the array of
  /** colors. */
  protected int _nCols;

  /** True iff the user can select a color, false if this widget is
  /** disabled. */
  protected boolean _allowSelection = true;

  ////////////////////////////////////////////////////////////////
  // constructors

  public ColorTilePanel() { this(netscapeColors()); }
  public ColorTilePanel(int nCols) { this(netscapeColors(), nCols); }


  public ColorTilePanel(Vector cs) {
    _colors = cs;
    _nCols = (int) Math.sqrt(cs.size());
  }

  public ColorTilePanel(Vector cs, int nCols) {
    _colors = cs;
    _nCols = nCols;
  }

  ////////////////////////////////////////////////////////////////
  // accessors

  public Color getColor() { return (Color)_colors.elementAt(_selected); }

  /** Select the little rectangle for the given color, ONLY if it is
   * one of the displayed colors. */
  public void setColor(Color c) {
    if (!_colors.contains(c)) return;
    setSelectionIndex(_colors.indexOf(c));
  }

  /** The user can be prevented from selecting a color if there would
   * be nothing to do the the selected color. E.g., the Color Picker
   * window is open but no DiagramElement is selected. */
  public void allowSelection(boolean as) {
    _allowSelection = as;
    if (_allowSelection) showSelection();
    else { hideSelection(); }
  }

  ////////////////////////////////////////////////////////////////
  // painting and related methods

  public void paint(Graphics g) {
    for (int i = 0; i < _colors.size(); ++i) paintTile(g, i);
    showSelection();
  }

  public void paintTile(Graphics g, int tileNum) {
    g.setColor((Color)_colors.elementAt(tileNum));
    int col = tileNum % _nCols;
    int row = tileNum / _nCols;
    g.fillRect(col*TILESIZE, row*TILESIZE, TILESIZE, TILESIZE);
  }

  /** Draw a black or white hollow rectangle to indicate which color
   * the user selected. */
  public void showSelection() {
    Color c, sc;
    c = (Color)_colors.elementAt(_selected);
    if (c.getRed() + c.getBlue() + c.getGreen() > 255 * 3 / 2)
      sc = Color.black; else sc = Color.white;
    Graphics g = getGraphics();
    g.setColor(sc);
    g.drawRect((_selected % _nCols)*TILESIZE,
	       (_selected / _nCols)*TILESIZE,
	       TILESIZE -1, TILESIZE-1);
    if (TILESIZE >= 8)
      g.drawRect((_selected % _nCols)*TILESIZE + 1,
		 (_selected / _nCols)*TILESIZE + 1,
		 TILESIZE -3, TILESIZE - 3);
  }

  /** Make the selection rectangle invisible. */
  public void hideSelection() {
    paintTile(getGraphics(), _selected);
  }

  public Dimension minimumSize() {
    int xSize = TILESIZE * _nCols;
    int ySize = TILESIZE * ( _colors.size() / _nCols + 2);
    return (new Dimension(xSize, ySize));
  }

  public Dimension preferredSize() { return minimumSize(); }

  public boolean setSelectionIndex(int s) {
    if (s < 0 || s > _colors.size()) return false;
    if (s == _selected) return false;
    int oldSelection = _selected;
    Graphics g = getGraphics();
    if (g == null) return true;
    hideSelection();
    _selected = s;
    showSelection();
    return true;
  }

  ////////////////////////////////////////////////////////////////
  // event handling

  /** When the user releases the mouse button that signifies that
   * (s)he has made a selection, post an ACTION_EVENT that the
   * enclosing Container can handle. */
  public boolean mouseUp(Event e, int x, int y) {
    if (x > _nCols * TILESIZE - 1 ||
        y > (_colors.size() / _nCols) * TILESIZE - 1)
      return false;
    if (!_allowSelection) return true;
    int col = x / TILESIZE;
    int row = y / TILESIZE;
    if (setSelectionIndex(col + row * _nCols)) {
      e.id = Event.ACTION_EVENT;
      postEvent(e);
    }
    return true;
  }

  /** Mousedown is the same as mouse up: it selects the color under
   * the mouse */
  public boolean mouseDown(Event e, int x, int y) { return mouseUp(e, x, y); }

  /** Dragging is the same as clicking on every point along the drag */
  public boolean mouseDrag(Event e, int x, int y) { return mouseUp(e, x, y); }

  ////////////////////////////////////////////////////////////////
  // color sets

  public static Vector _NetscapeColors = null;

  public static Vector netscapeColors() {
    if (_NetscapeColors == null) {
      int values[] = new int[6];
      int nValues = 0;
      values[nValues++] = 0;
      values[nValues++] = 3*16 + 3;
      values[nValues++] = 6*16 + 6;
      values[nValues++] = 9*16 + 9;
      values[nValues++] = 12*16 + 12;
      values[nValues++] = 15*16 + 15;
      _NetscapeColors = new Vector(nValues * nValues * nValues);
      for (int r = 0; r < nValues; ++r)
	for (int g = 0; g < nValues; ++g)
	  for (int b = 0; b < nValues; ++b) {
	    Color c = new Color(values[r], values[g], values[b]);
	    _NetscapeColors.addElement(c);
	  }
    }
    return _NetscapeColors;
  }


} /* end class ColorTilePanel */

