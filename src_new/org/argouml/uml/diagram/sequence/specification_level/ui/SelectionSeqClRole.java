// Copyright (c) 1996-2002 The Regents of the University of California. All
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




// File: SelectionSeqClRole.java
// Classes: SelectionSeqClRole
// Original Author: 5kanzler@informatik.uni-hamburg.de

/**
 * This class provides dynamically placed rapid buttons for objects in a sequence diagram.
 * By clicking the mouse button on the object's lifeline, the rapid buttons appear 
 * at this position.
 * If you click on the rapid button , a new object is created right beside the selected object.
 */

package org.argouml.uml.diagram.sequence.specification_level.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Icon;

import org.apache.log4j.Category;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.uml.UmlFactory;
import org.argouml.uml.diagram.sequence.specification_level.ui.FigSeqClRole;
import org.argouml.uml.diagram.ui.ModeCreateEdgeAndNode;
import org.argouml.uml.diagram.ui.SelectionWButtons;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.Mode;
import org.tigris.gef.base.ModeCreatePolyEdge;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.Handle;

import ru.novosoft.uml.behavior.collaborations.MClassifierRole;
import ru.novosoft.uml.behavior.common_behavior.MCallAction;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.behavior.common_behavior.MLinkEnd;


public class SelectionSeqClRole extends SelectionWButtons {

    protected static Category cat = Category.getInstance(SelectionSeqClRole.class);

    ////////////////////////////////////////////////////////////////
    // constants
    public static Icon stimCall = ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIconResource("StimulusCall");
    public static Icon stimRet = ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIconResource("StimulusReturn");

    ////////////////////////////////////////////////////////////////
    // instance variables
    private int yPos=0;

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new SelectionClass for the given Fig */
    public SelectionSeqClRole(Fig f) {
        super(f);
    }

    /** Return a handle ID for the handle under the mouse, or -1 if
     *  none. TODO: in the future, return a Handle instance or
     *  null. <p>
     *  <pre>
     *   0-------1-------2
     *   |               |
     *   3               4
     *   |               |
     *   5-------6-------7
     * </pre>
     */
    public void hitHandle(Rectangle r, Handle h) {
        super.hitHandle(r, h);
        if (h.index != -1) return;
        if (!_paintButtons) return;
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.size() != 1) return;
        ModeManager mm = ce.getModeManager();
        if (mm.includes(ModeModify.class) && _pressedButton == -1) return;
        int cx = _content.getX();
        int cy = _content.getY();
        int cw = _content.getWidth();
        int ch = _content.getHeight();
        int sCw = stimCall.getIconWidth();
        int sCh = stimCall.getIconHeight();
        int sRw = stimRet.getIconWidth();
        int sRh = stimRet.getIconHeight();
        if (hitLeft(cx + cw, yPos, sCw, sCh, r)) {
            h.index = 10;
            h.instructions = "Add a called object";
        } else if (hitRight(cx, yPos, sRw, sRh, r)) {
            h.index = 11;
            h.instructions = "Add a calling object";
        } else {
            h.index = -1;
            h.instructions = "Move object(s)";
        }
    }

    /** Paint the handles at the four corners and midway along each edge
     * of the bounding box.
     */
    public void paintButtons(Graphics g) {
        int cx = _content.getX();
        int cy = _content.getY();
        int cw = _content.getWidth();
        int ch = _content.getHeight();
        // get the position at which the user clicked the mouse button
        yPos = ((FigSeqClRole)_content)._yPos;
        paintButtonLeft(stimCall, g, cx + cw, yPos , 10);  
        paintButtonRight(stimRet, g, cx, yPos, 11);
    }

    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
        if (hand.index < 10) {
            _paintButtons = false;
            super.dragHandle(mX, mY, anX, anY, hand);
            return;
        }
        int cx = _content.getX(), cy = _content.getY();
        int cw = _content.getWidth(), ch = _content.getHeight();
        int newX = cx, newY = cy, newW = cw, newH = ch;
        Dimension minSize = _content.getMinimumSize();
        int minWidth = minSize.width, minHeight = minSize.height;
        Class edgeClass = null;
        Class nodeClass = ru.novosoft.uml.behavior.collaborations.MClassifierRoleImpl.class;
        Class actionClass = null;
        int bx = mX, by = mY;
        boolean reverse = false;
        switch (hand.index) {
            case 10: //add a called object
                edgeClass = ru.novosoft.uml.behavior.common_behavior.MLink.class;
                actionClass =  ru.novosoft.uml.behavior.common_behavior.MCallActionImpl.class;
                by = yPos;
                bx = cx + cw;
                break;
            case 11: // add a calling object
                edgeClass = ru.novosoft.uml.behavior.common_behavior.MLink.class;
                actionClass = ru.novosoft.uml.behavior.common_behavior.MReturnActionImpl.class;
                //reverse = true;
                by = yPos;
                bx = cx;
                break;
            default:
                cat.warn("invalid handle number");
            break;
        }
        if (edgeClass != null && nodeClass != null && actionClass != null) {
            Editor ce = Globals.curEditor();
            // last parameter in  constructor interface:
            // if true, the method ModeCreateEdgeAndNode.postProcessEdge() will be called
            ModeCreateEdgeAndNode m = new
            ModeCreateEdgeAndNode(ce, edgeClass, nodeClass, false);
            m.setup((FigNode)_content, _content.getOwner(), bx, by, reverse);
            ce.mode(m);
            m.setArg("action", actionClass);
        }
    }


    public void buttonClicked(int buttonCode) {
        super.buttonClicked(buttonCode);
        MClassifierRole newNode = UmlFactory.getFactory().getCollaborations().createClassifierRole();
        FigSeqClRole fc = (FigSeqClRole)_content;
        MClassifierRole cls = (MClassifierRole)fc.getOwner();
        Editor ce = Globals.curEditor();
        GraphModel gm = ce.getGraphModel();
        if (!(gm instanceof MutableGraphModel)) return;
        MutableGraphModel mgm = (MutableGraphModel) gm;
        if (!mgm.canAddNode(newNode)) return;
        GraphNodeRenderer renderer = ce.getGraphNodeRenderer();
        LayerPerspective lay = (LayerPerspective)
        ce.getLayerManager().getActiveLayer();
        Fig newFC = renderer.getFigNodeFor(gm, lay, newNode);
        if (buttonCode == 10) {
            int index = lay.indexOf( fc );
            lay.insertAt(newFC, index+1);      
        } else if (buttonCode == 11) {
            int index = lay.indexOf( fc );
            lay.insertAt(newFC, index);
        }
        mgm.addNode(newNode);
        FigPoly edgeShape = new FigPoly();
        Point fcCenter = fc.center();
        //edgeShape.addPoint(fcCenter.x, fcCenter.y);
        edgeShape.addPoint(fcCenter.x, yPos);
        Point newFCCenter = newFC.center();
        edgeShape.addPoint(newFCCenter.x, yPos);
        Object newEdge = null;
        if (buttonCode == 10) {
            Mode mode = new ModeCreatePolyEdge();
            Hashtable args = new Hashtable();
            args.put("action", MCallAction.class);
            args.put("edgeClass", MLink.class);
            mode.init(args);
            Globals.mode(mode);
            newEdge = addLinkStimulusCall(mgm, cls, newNode);
        } else if (buttonCode == 11) {
            newEdge = addLinkStimulusReturn(mgm, cls, newNode);
        }
        MLink link = (MLink) newEdge;
        FigSeqAsRole figSeqAsRole = (FigSeqAsRole) lay.presentationFor(newEdge);
        edgeShape.setLineColor(Color.black);
        edgeShape.setFilled(false);
        edgeShape._isComplete = true;
        figSeqAsRole.setFig(edgeShape);
        ce.getSelectionManager().select(fc);
        Collection liEnds = link.getConnections();
        if (liEnds.size() != 2 ) return;
        Iterator iter = liEnds.iterator();
        MLinkEnd le1 = (MLinkEnd)iter.next();
        MLinkEnd le2 = (MLinkEnd)iter.next();
        MClassifierRole objSrc = (MClassifierRole)le1.getInstance();
        MClassifierRole objDst = (MClassifierRole)le2.getInstance();
        FigSeqClRole figObjSrc = (FigSeqClRole)lay.presentationFor(objSrc);
        FigSeqClRole figObjDst = (FigSeqClRole)lay.presentationFor(objDst);
        if ( figObjSrc != null && figObjDst != null ) {
            figSeqAsRole.setSourcePortFig(figObjSrc);
            figSeqAsRole.setDestPortFig(figObjDst);
        }
        figSeqAsRole.addFigSeqMessageWithAction();
    }

    public Object addLinkStimulusCall(MutableGraphModel mgm, MClassifierRole cls, MClassifierRole newCls) {
        Editor ce = Globals.curEditor();
        ModeManager modeManager = ce.getModeManager();
        Mode mode = (Mode)modeManager.top();
        mode.setArg("action", ru.novosoft.uml.behavior.common_behavior.MCallAction.class);
        return mgm.connect(cls, newCls, MLink.class);
    }

    public Object addLinkStimulusReturn(MutableGraphModel mgm, MClassifierRole cls, MClassifierRole newCls) {
        Editor ce = Globals.curEditor();
        ModeManager modeManager = ce.getModeManager();
        Mode mode = (Mode)modeManager.top();
        mode.setArg("action", ru.novosoft.uml.behavior.common_behavior.MReturnAction.class);
        return mgm.connect(cls, newCls, MLink.class);
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    public void mouseReleased(MouseEvent me) {
        if (_pressedButton < 10) return;
        Handle h = new Handle(-1);
        hitHandle(me.getX(), me.getY(), 0, 0, h);
        if (_pressedButton == h.index) {
            buttonClicked(_pressedButton);
        }
        _pressedButton = -1;
        Editor ce = Globals.curEditor();
        ce.damaged(this);
    }

    public void mouseEntered(MouseEvent me) {
        System.out.println("SelectionSeqClRole: mouseEntered");
        super.mouseEntered(me);
    }

    public void mouseExited(MouseEvent me) {
        System.out.println("SelectionSeqClRole: mouseExited");
        super.mouseExited(me);
    }

    public void mouseMoved(MouseEvent me) {
        System.out.println("SelectionSeqClRole: mouseMoved");
        super.mouseMoved(me);
    }
} /* end class SelectionClass */
