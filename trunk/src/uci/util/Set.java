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




// File: Set.java
// Classes: Set
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.util;

import java.util.*;


/** An Ordered, non-duplicated collecton of objects (not exactly a
 *  mathemetical set because it is ordered).  Implemented with a
 *  Vector. */

public class Set implements java.io.Serializable  {
  ////////////////////////////////////////////////////////////////
  // constants
  public static final int TC_LIMIT = 50;

  ////////////////////////////////////////////////////////////////
  // instance variables
  private Vector _v;

  ////////////////////////////////////////////////////////////////
  // constructors
  
  public Set() { _v = new Vector(); }
  public Set(int n) { _v = new Vector(n); }
  public Set(Object o1) { _v = new Vector(); addElement(o1); }

  public void addElement(Object o) { if (!contains(o)) _v.addElement(o); }
  public void addAllElements(Vector v) {
    if (v == null) return;
    addAllElements(v.elements());
  }
  public void addAllElements(Enumeration enum) {
    while (enum.hasMoreElements()) {
      addElement(enum.nextElement());
    }
  }
  public void addAllElementsSuchThat(Enumeration enum, Predicate p) {
    if (p instanceof PredicateTrue) addAllElements(enum);
    else 
      while (enum.hasMoreElements()) {
	Object e = enum.nextElement();
	if (p.predicate(e)) addElement(e);
      }
  }  
  public void addAllElements(Set s) { addAllElements(s.elements()); }
  public void addAllElementsSuchThat(Set s, Predicate p) {
    addAllElementsSuchThat(s.elements(), p);
  }

  public void remove(Object o) { _v.removeElement(o); }
  public void removeElement(Object o) { _v.removeElement(o); }
  public void removeAllElements() { _v.removeAllElements(); }
  public boolean contains(Object o) { return _v.contains(o); }
  public boolean containsSuchThat(Predicate p) {
    return findSuchThat(p) != null;
  }

  /** return the first element that causes p.predicate() to return
   * true. */
  public Object findSuchThat(Predicate p) {
    Enumeration elts = elements();
    while (elts.hasMoreElements()) {
      Object o = elts.nextElement();
      if (p.predicate(o)) return o;
    }
    return null;
  }

  public Enumeration elements() { return _v.elements(); }

  public Object elementAt(int index) { return _v.elementAt(index); }

  public Vector asVector() { return _v; }

  public boolean equals(Object o) {
    if (!(o instanceof Set)) return false;
    Set set = (Set) o;
    if (set.size() != size()) return false;
    Enumeration myEs = elements();
    while (myEs.hasMoreElements()) {
      Object obj = myEs.nextElement();
      if (!(set.contains(obj))) return false;
    }
    return true;
  }


  public Object firstElement() { return _v.firstElement(); }
						       
  public int size() { return _v.size(); }
  public String toString() {
    String res = "Set{";
    Enumeration eles = elements();
    while (eles.hasMoreElements()) {
      res += eles.nextElement().toString();
      if (eles.hasMoreElements()) res += ", ";
    }
    return res + "}";
  }

  /** Reply the Set of all objects that can be reached from the
   * receiving Set by taking steps defined by the given
   * ChildGenerator.  The result includes the elements of the original
   * Set. In order to avoid very deep searches which are often
   * programming mistakes, only paths of length TC_LIMIT or less are
   * considered. */
  public Set transitiveClosure(ChildGenerator cg) {
    return transitiveClosure(cg, TC_LIMIT, PredicateTrue.theInstance());
  }

  /** Reply the Set of all objects that can be reached from the
   * receiving Set by taking steps defined by the given
   * ChildGenerator.  The result DOES NOT include the elements of the
   * original Set. In order to avoid very deep searches which are
   * often programming mistakes, only paths of length TC_LIMIT or less
   * are considered.*/
  public Set reachable(ChildGenerator cg) {
    return reachable(cg, TC_LIMIT, PredicateTrue.theInstance());
  }
  
  /** Reply the Set of all objects that can be reached from the
   * receiving Set by taking steps defined by the given
   * ChildGenerator.  The result DOES NOT include the elements of the
   * original Set. In order to avoid very deep searches which are
   * often programming mistakes, only paths of given max length or
   * less are considered. Only paths consisting of elements which all
   * cause p.predicate() to return true are considered. */
  public Set reachable(ChildGenerator cg, int max, Predicate p) {
    Set kids = new Set();
    Enumeration rootEnum = elements();
    while (rootEnum.hasMoreElements()) {
      Object r = rootEnum.nextElement();
      kids.addAllElementsSuchThat(cg.gen(r), p);
    }
    return kids.transitiveClosure(cg, max, p);
  }
  
  /** Reply the Set of all objects that can be reached from the
   * receiving Set by taking steps defined by the given
   * ChildGenerator.  The result includes the elements of the original
   * Set. In order to avoid very deep searches which are often
   * programming mistakes, only paths of given max length or less are
   * considered. Only paths consisting of elements which all cause
   * p.predicate() to return true are considered. */
  public Set transitiveClosure(ChildGenerator cg, int max, Predicate p) {
    int iterCount = 0;
    int lastSize = -1;
    Set touched = new Set();
    Set frontier, recent = this;

    touched.addAllElements(this);
    while ((iterCount < max) && (touched.size() > lastSize)) {
      iterCount++;
      lastSize = touched.size();
      frontier = new Set();
      Enumeration recentEnum = recent.elements();
      while (recentEnum.hasMoreElements()) {
	Enumeration frontsEnum = cg.gen(recentEnum.nextElement());
	frontier.addAllElementsSuchThat(frontsEnum, p);
      }
      touched.addAllElements(frontier);
      recent = frontier;
    }
    return touched;
  }

} /* end class Set */
