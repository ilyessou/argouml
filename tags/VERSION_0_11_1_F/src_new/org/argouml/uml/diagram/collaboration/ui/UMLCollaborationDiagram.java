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

// File: UMLCollaborationDiagram.java
// Classes: UMLCollaborationDiagram
// Original Author: agauthie@ics.uci.edu
// $Id$

package org.argouml.uml.diagram.collaboration.ui;

import java.util.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;

import ru.novosoft.uml.model_management.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.behavior.collaborations.*;

import org.apache.log4j.*;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;
import org.tigris.gef.ui.*;

import org.argouml.ui.*;
import org.argouml.uml.ui.*;
import org.argouml.uml.diagram.ui.*;
import org.argouml.uml.diagram.collaboration.*;

public class UMLCollaborationDiagram extends UMLDiagram {

  /** for logging */
  private final static Category cat = 
      Category.getInstance("org.argouml.uml.diagram.collaboration.ui.UMLCollaborationDiagram");

  ////////////////
  // actions for toolbar


  protected static Action _actionClassifierRole =
  new CmdCreateNode(MClassifierRoleImpl.class, "ClassifierRole");

  protected static Action _actionAssoc =
  new CmdSetMode(ModeCreatePolyEdge.class,
		 "edgeClass", MAssociationRoleImpl.class,
		 "AssociationRole");


  ////////////////////////////////////////////////////////////////
  // contructors
  protected static int _CollaborationDiagramSerial = 1;


  public UMLCollaborationDiagram() {
  	
    try { setName(getNewDiagramName()); }
    catch (PropertyVetoException pve) { }
  }

  public UMLCollaborationDiagram(MNamespace m) {
    this();
    setNamespace(m);
  }

  public int getNumMessages() {
    Layer lay = getLayer();
    Vector figs = lay.getContents();
    int res = 0;
    int size = figs.size();
    for (int i=0; i < size; i++) {
      Fig f = (Fig) figs.elementAt(i);
      if (f.getOwner() instanceof MMessage) res++;
    }
    return res;
  }
  
    /** method to perform a number of important initializations of a <I>CollaborationDiagram</I>. 
     * 
     * @see      each diagram type has a similar <I>UMLxxxDiagram</I> class.
     *
     * @param m  MNamespace from the model in NSUML...
     *
     * @modified changed <I>lay</I> from <I>LayerPerspective</I> to <I>LayerPerspectiveMutable</I>. 
     *           This class is a child of <I>LayerPerspective</I> and was implemented 
     *           to correct some difficulties in changing the model. <I>lay</I> is used 
     *           mainly in <I>LayerManager</I>(GEF) to control the adding, changing and 
     *           deleting layers on the diagram...
     *           psager@tigris.org   Jan. 24, 2oo2
     */        
  public void setNamespace(MNamespace m) {
      super.setNamespace(m);
      CollabDiagramGraphModel gm = new CollabDiagramGraphModel();
      gm.setNamespace(m);
      setGraphModel(gm);
      LayerPerspective lay = new LayerPerspectiveMutable(m.getName(), gm);
      setLayer(lay);
      CollabDiagramRenderer rend = new CollabDiagramRenderer(); // singleton
      lay.setGraphNodeRenderer(rend);
      lay.setGraphEdgeRenderer(rend);
  }

  /** initialize the toolbar for this diagram type */
  protected void initToolBar() {
    _toolBar = new ToolBar();
    _toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//     _toolBar.add(Actions.Cut);
//     _toolBar.add(Actions.Copy);
//     _toolBar.add(Actions.Paste);
//     _toolBar.addSeparator();

    _toolBar.add(_actionSelect);
    _toolBar.add(_actionBroom);
    _toolBar.addSeparator();

    _toolBar.add(_actionClassifierRole);
    _toolBar.addSeparator();
    _toolBar.add(_actionAssoc);
    _toolBar.add(ActionAddMessage.SINGLETON);
    // other actions
    _toolBar.addSeparator();

    _toolBar.add(_actionRectangle);
    _toolBar.add(_actionRRectangle);
    _toolBar.add(_actionCircle);
    _toolBar.add(_actionLine);
    _toolBar.add(_actionText);
    _toolBar.add(_actionPoly);
    _toolBar.add(_actionSpline);
    _toolBar.add(_actionInk);
    _toolBar.addSeparator();

    _toolBar.add(_diagramName);
  }


  /**  After loading the diagram it?s necessary to connect
    *  every FigMessage to its FigAssociationRole. 
    *  This is done by adding the FigMessage 
    *  to the PathItems of its FigAssociationRole */  
  public void postLoad() {

    super.postLoad();

    Collection messages;
    Iterator msgIterator;
    if (getNamespace() == null) {
        cat.error("Collaboration Diagram does not belong to a namespace");
        return;
    }
    Collection ownedElements = getNamespace().getOwnedElements();
    Iterator oeIterator = ownedElements.iterator();   
    Layer lay = getLayer();
    while(oeIterator.hasNext()) {
	MModelElement me = (MModelElement)oeIterator.next();
	if (me instanceof MAssociationRole) {
           messages= ((MAssociationRole) me).getMessages();
           msgIterator= messages.iterator();
           while(msgIterator.hasNext()) {
             MMessage message = (MMessage)msgIterator.next();            
             FigMessage figMessage = (FigMessage) lay.presentationFor(message);
             if ( figMessage != null ) {
               figMessage.addPathItemToFigAssociationRole(lay);
             }
           }
       }
    }
  }
  
  protected static String getNewDiagramName() {
  	String name = null;
  	Object[] args = {name};
  	do {
        name = "collaboration diagram " + _CollaborationDiagramSerial;
        _CollaborationDiagramSerial++;
        args[0] = name;
    }
    while (TheInstance.vetoCheck("name", args));
    return name;
  }

} /* end class UMLCollaborationDiagram */
