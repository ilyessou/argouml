// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.



// File: MutableGraphEvent.java
// Interfaces: MutableGraphEvent
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.graph;

import java.util.EventObject;
import java.util.EventListener;

/** A notification that a graph has changed.  The source is the object
 *  that implements MutableGraphModel. The argument is the specific
 *  node or edge that was involved when a node or edge is added or
 *  removed. The argument is null if the entire graph changed. */

public class MutableGraphEvent extends EventObject {
  ////////////////////////////////////////////////////////////////
  // instance variables
  /** The specific node, port, or arc that was modified. */
  protected Object _arg;

  ////////////////////////////////////////////////////////////////
  // constructors
  public MutableGraphEvent(Object src) { this(src, null); }
  public MutableGraphEvent(Object src, Object arg) {
    super(src);
    _arg = arg;
  }

  public Object getArg() { return _arg; }

} /* end class MutableGraphEvent */
