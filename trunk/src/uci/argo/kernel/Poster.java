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



// File: Poster.java
// Classes: Poster
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.argo.kernel;

import java.util.*;
import com.sun.java.swing.*;

import uci.util.*;

/** Interface that defines methods required on any object that can
 *  post a ToDoItem to the Designer's ToDoList. Basically requires that
 *  the poster (1) have contact information, (2) be able to hush
 *  and unhush itself, and (3) be able to determine if a ToDoItem it
 *  posted previously should still be on the Designer's ToDoList. <p>
 *
 *  Currently Critic and Designer implement this interface.
 *
 * @see Critic
 * @see Designer */

public interface Poster {

  ////////////////////////////////////////////////////////////////
  // accessors

  /** Get some contact information on the Poster. */
  String getExpertEmail();

  /** Update the Poster's contact info. Is this needed? */
  void setExpertEmail(String addr);

  /** Reply true if the given item should be kept on the Designer's
   * ToDoList, false if it is no longer valid. */
  boolean stillValid(ToDoItem i, Designer d);

  boolean supports(Decision d);
  Vector getSupportedDecisions();
  boolean supports(Goal g);
  Vector getSupportedGoals();
  boolean containsKnowledgeType(String knowledgeType);
  Set getKnowledgeTypes();

  /** Customize the description string just before it is displayed. */
  String expand(String desc, Set offs);

  public Icon getClarifier();

  ////////////////////////////////////////////////////////////////
  // criticism control

  /** temporarily disable this Poster. */
  void hush();

  /** Unhush this Poster, it may resume posting without further
   * delay. */
  void unhush();

  ////////////////////////////////////////////////////////////////
  // issue resolution

  void fixIt(ToDoItem item, Object arg);

  boolean canFixIt(ToDoItem item);

} /* end interface Poster */
