// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.sequence.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import org.argouml.i18n.Translator;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerManager;
import org.tigris.gef.base.ModeCreate;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigNode;

/**
 * Mode to create a link between two figobjects. This mode extends
 * ModeCreate and not ModeCreateEdge because ModeCreateEdge hides its
 * variables a bit too much...
 *
 * @author jaap.branderhorst@xs4all.nl
 */
public class ModeCreateMessage extends ModeCreate {

    private static final Logger LOG =
	Logger.getLogger(ModeCreateMessage.class);

    /**
     * The NetPort where the arc is paintn from.
     */
    private Object startPort;

    /**
     * The Fig that presents the starting NetPort.
     */
    private Fig startPortFig;

    /**
     * The FigNode on the NetNode that owns the start port.
     */
    private FigClassifierRole sourceFigClassifierRole;

    /**
     * The new message that is being created.
     */
    private Object message;

    /**
     * The constructor.
     *
     */
    public ModeCreateMessage() {
        super();
    }

    /**
     * The constructor.
     *
     * @param par the editor
     */
    public ModeCreateMessage(Editor par) {
        super(par);
    }

    /**
     * @see org.tigris.gef.base.ModeCreate#createNewItem(
     * java.awt.event.MouseEvent, int, int)
     */
    public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigLine(
            snapX,
            snapY,
            me.getX(),
            snapY,
            Globals.getPrefs().getRubberbandColor());
    }

    /**
     * @see org.tigris.gef.base.FigModifyingMode#instructions()
     */
    public String instructions() {
        return Translator.localize("action.sequence.new."
				   + getArg("actionName"));
    }

    /**
     * On mousePressed determine what port the user is dragging from.
     * The mousePressed event is sent via ModeSelect.
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }
        int x = me.getX(), y = me.getY();
        Editor ce = Globals.curEditor();
        Fig underMouse = ce.hit(x, y);
        if (underMouse == null) {
            underMouse = ce.hit(x - 16, y - 16, 32, 32);
        }
        if (underMouse == null) {
            done();
            me.consume();
            return;
        }
        if (!(underMouse instanceof FigClassifierRole)) {
            done();
            me.consume();
            return;
        }
        sourceFigClassifierRole = (FigClassifierRole) underMouse;
        startPort = sourceFigClassifierRole.deepHitPort(x, y);
        if (startPort == null) {
            done();
            me.consume();
            return;
        }
        startPortFig = sourceFigClassifierRole.getPortFig(startPort);
        start();
        Point snapPt = new Point();
        synchronized (snapPt) {
            snapPt.setLocation(
                startPortFig.getX() + FigClassifierRole.WIDTH / 2,
                startPortFig.getY());
            editor.snap(snapPt);
            anchorX = snapPt.x;
            anchorY = snapPt.y;
        }
        _newItem = createNewItem(me, anchorX, anchorY);
        me.consume();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * On mouseReleased, find the port in the destination node that's
     * on the same height as the source port fig, ask the GraphModel
     * to connect the two ports.  If that connection is allowed, then
     * construct a new FigEdge and add it to the Layer and send it to
     * the back.
     *
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }
        if (sourceFigClassifierRole == null) {
            done();
            me.consume();
            return;
        }

        int x = me.getX(), y = me.getY();
        Class arcClass;
        Editor ce = Globals.curEditor();
        Fig f = ce.hit(x, y);
        if (f == null) {
            f = ce.hit(x - 16, y - 16, 32, 32);
        }
        GraphModel gm = ce.getGraphModel();
        if (!(gm instanceof MutableGraphModel)) {
            f = null;
	}
        MutableGraphModel mgm = (MutableGraphModel) gm;
        if (f instanceof FigClassifierRole) {
            FigClassifierRole destFigClassifierRole = (FigClassifierRole) f;
            // If its a FigNode, then check within the
            // FigNode to see if a port exists
            Object foundPort = null;
            if (destFigClassifierRole != sourceFigClassifierRole) {
                y = startPortFig.getY();
                foundPort = destFigClassifierRole.deepHitPort(x, y);
            } else {
                foundPort = destFigClassifierRole.deepHitPort(x, y);
            }

            if (foundPort != null && foundPort != startPort) {
                Fig destPortFig = destFigClassifierRole.getPortFig(foundPort);
                Object edgeType = getArg("edgeClass");
                if (edgeType != null) {
                    message =
                        mgm.connect(startPort, foundPort, edgeType);
                } else {
                    message = mgm.connect(startPort, foundPort);
                }

                // Calling connect() will add the edge to the GraphModel and
                // any LayerPersectives on that GraphModel will get a
                // edgeAdded event and will add an appropriate FigEdge
                // (determined by the GraphEdgeRenderer).

                if (null != message) {
                    LayerManager lm = ce.getLayerManager();
                    ce.damaged(_newItem);
                    sourceFigClassifierRole.damage();
                    destFigClassifierRole.damage();
                    _newItem = null;
                    FigMessage fe =
                        (FigMessage) ce.getLayerManager()
                            .getActiveLayer().presentationFor(message);
                    fe.setSourcePortFig(startPortFig);
                    fe.setSourceFigNode(sourceFigClassifierRole);
                    fe.setDestPortFig(destPortFig);
                    fe.setDestFigNode(destFigClassifierRole);
                    // set the new edge in place
                    if (sourceFigClassifierRole != null) {
                        sourceFigClassifierRole.updateEdges();
                    }
                    if (destFigClassifierRole != null) {
                        destFigClassifierRole.updateEdges();
                    }
                    if (fe != null) {
                        ce.getSelectionManager().select(fe);
                    }
                    done();
                    me.consume();
                    return;
                } else {
                    LOG.debug("connection return null");
                }
            }
        }
        sourceFigClassifierRole.damage();
        ce.damaged(_newItem);
        _newItem = null;
        done();
        me.consume();
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent me) {
        if (me.isConsumed()) {
            return;
	}
        if (_newItem != null) {
            editor.damaged(_newItem);
            creationDrag(me.getX(), startPortFig.getY());
            editor.damaged(_newItem);
        }
        editor.scrollToShow(me.getX(), startPortFig.getY());
        me.consume();
    }
} /* end class ModeCreateEdge */
