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

// File: FigTransition.java
// Classes: FigTransition
// Original Author: your email address here
// $Id$

package org.argouml.uml.diagram.state.ui;

import java.awt.Color;
import java.awt.Point;
import java.beans.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.behavior.common_behavior.*;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;

import org.argouml.application.api.*;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.ui.*;
import org.argouml.uml.generator.*;

public class FigTransition extends FigEdgeModelElement {

  ////////////////////////////////////////////////////////////////
  // constructors
  public FigTransition() {
    super();
    addPathItem(_name, new PathConvPercent(this, 50, 10));
    _fig.setLineColor(Color.black);
    setDestArrowHead(new ArrowHeadGreater());
  }
  
  public FigTransition(Object edge, Layer lay) {
    this();
    if (edge instanceof MTransition) {
      MTransition tr = (MTransition)edge;
      MStateVertex sourceSV = tr.getSource();
      MStateVertex destSV = tr.getTarget();
      FigNode sourceFN = (FigNode) lay.presentationFor(sourceSV);
      FigNode destFN = (FigNode) lay.presentationFor(destSV);
      setSourcePortFig(sourceFN);
      setSourceFigNode(sourceFN);
      setDestPortFig(destFN);
      setDestFigNode(destFN);
    }
    setOwner(edge);
  }

  public FigTransition(Object edge) {
    this(edge, ProjectBrowser.TheInstance.getActiveDiagram().getLayer());
  }

  ////////////////////////////////////////////////////////////////
  // event handlers

  /** This method is called after the user finishes editing a text
   *  field that is in the FigEdgeModelElement.  Determine which field
   *  and update the model.  This class handles the name, subclasses
   *  should override to handle other text elements. */
  protected void textEdited(FigText ft) throws PropertyVetoException {
    
    MTransition t = (MTransition) getOwner();
    if (t == null) return;
    String s = ft.getText();
    ParserDisplay.SINGLETON.parseTransition(t, s);
   
  }

  /** This is called aftern any part of the UML MModelElement has
   *  changed. This method automatically updates the name FigText.
   *  Subclasses should override and update other parts. */
  protected void modelChanged() {
    MModelElement me = (MModelElement) getOwner();
    if (me == null) return;
    //System.out.println("FigTransition modelChanged: " + me.getClass());
    String nameStr = Notation.generate(this, me);
    _name.setText(nameStr);
  }
  
/**
 * Returns all transitions existing between the source and the destination 
 * of this FigTransition
 * @return Collection the collection with all the transitions in it
 */
  protected Vector getTransitions() {  
    Fig dest = getDestPortFig();
    Fig source = getSourcePortFig();
    if (dest != null && source != null) {
        MStateVertex destOwner = (MStateVertex)dest.getOwner();
        MStateVertex sourceOwner = (MStateVertex)source.getOwner();
        Set set = new HashSet();
        if (destOwner != null) {
        	set.addAll(destOwner.getOutgoings());
        	set.addAll(destOwner.getIncomings());
        }
        if (sourceOwner != null) {
        	set.addAll(sourceOwner.getOutgoings());
        	set.addAll(sourceOwner.getIncomings());
        }
        Vector retVector = new Vector();
        retVector.addAll(set);
        return retVector;                  
    }
    return new Vector(); // return an empty vector to prevent nullpointers.
    
  }
  
  protected int[] flip(int[] Ps) {
    int[] r = new int[Ps.length];
    for (int i = Ps.length; i == 0; i--) {
        r[Ps.length-i] = Ps[i];
    }
    return r;
  }
    
  protected void updateRoute() {
    
    // first see if there are transitions the other way around.
    List transitions = new ArrayList();
    transitions.addAll(getTransitions());
    transitions.remove(getOwner());
    if (!transitions.isEmpty()) {
        // we have to find all transitions that have equal points
        int[] xs = getXs();
        int[] ys = getYs();
        for (int i = 0; i < transitions.size(); i++) {
            FigEdge fig = ((FigEdge)getLayer().presentationFor(transitions.get(i)));
            // next lines patch for loading old 0.10.1 projects in 0.11.1 and further
            if (fig == null) {
            	Iterator it = ProjectBrowser.TheInstance.getProject().findFigsForMember(transitions.get(i)).iterator();
            	if (it.hasNext()) {
            		fig = (FigEdge)it.next();
            	}
            }
            if (fig != null) {
            	if ((xs.equals(fig.getXs()) && ys.equals(fig.getYs())) || (xs.equals(flip(fig.getXs())) && ys.equals(flip(fig.getYs())))) {
                	Point startPoint = getFirstPoint();
                	Point endPoint = getLastPoint();
                 
	                int x = (int)Math.round(Math.abs((startPoint.getX()-endPoint.getX())/2) + Math.min(startPoint.getX(), endPoint.getX()));
	                int y = (int)Math.round(Math.abs((startPoint.getY()-endPoint.getY())/2) + Math.min(startPoint.getY(), endPoint.getY()));
	                insertPoint(1, x, y);
	            }
            }
        }
    }
  }

	/**
	 * @see org.tigris.gef.presentation.FigEdge#computeRoute()
	 */
	public void computeRoute() {
		super.computeRoute();
        updateRoute();
	}

} /* end class FigTransition */

