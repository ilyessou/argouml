// $Id$
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

// File: FigEdgeModelElement.java
// Classes: FigEdgeModelElement
// Original Author: abonner

// $Id$

package org.argouml.uml.diagram.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.argouml.application.api.ArgoEventListener;
import org.argouml.application.api.Notation;
import org.argouml.application.api.NotationContext;
import org.argouml.application.api.NotationName;
import org.argouml.application.events.ArgoEvent;
import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoNotationEvent;
import org.argouml.application.events.ArgoNotationEventListener;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ItemUID;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.ToDoList;
import org.argouml.kernel.DelayedChangeNotify;
import org.argouml.kernel.DelayedVChangeListener;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.ui.ActionAutoResize;
import org.argouml.ui.ActionGoToCritique;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.ArgoJMenu;
import org.argouml.ui.Clarifier;
import org.argouml.ui.cmd.CmdSetPreferredSize;
import org.argouml.uml.UUIDManager;
import org.argouml.uml.diagram.ui.ActionDeleteFromDiagram;
import org.argouml.uml.diagram.ui.ActionProperties;
import org.argouml.util.Trash;
import org.tigris.gef.base.Selection;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigEdgePoly;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigText;
import org.tigris.gef.presentation.Handle;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;


/** Abstract class to display diagram arcs for UML ModelElements that
 *  look like arcs and that have editiable names. */

public abstract class FigEdgeModelElement
    extends FigEdgePoly
    implements
        VetoableChangeListener,
        DelayedVChangeListener,
        MouseListener,
        KeyListener,
        PropertyChangeListener,
        MElementListener,
        NotationContext,
        ArgoNotationEventListener {

    protected static Logger cat =
        Logger.getLogger(FigEdgeModelElement.class);
        
    protected static final String BUNDLE = "UMLMenu";
        
    ////////////////////////////////////////////////////////////////
    // constants

    public static Font LABEL_FONT;
    public static Font ITALIC_LABEL_FONT;

    static {
        LABEL_FONT = new Font("Dialog", Font.PLAIN, 10);
        ITALIC_LABEL_FONT =
            new Font(LABEL_FONT.getFamily(), Font.ITALIC, LABEL_FONT.getSize());
    }

    public final int MARGIN = 2;

    ////////////////////////////////////////////////////////////////
    // instance variables

    protected FigText _name;
    protected FigText _stereo = new FigText(10, 30, 90, 20);
    private ItemUID _id;
    
    /**
     * The current notation for this fig. The notation is for example
     * UML 1.3 or Java
     */
    private NotationName _currentNotationName;

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Partially construct a new FigNode.  This method creates the
     *  _name element that holds the name of the model element and adds
     *  itself as a listener. */
    public FigEdgeModelElement() {
        _name = new FigText(10, 30, 90, 20);
        _name.setFont(LABEL_FONT);
        _name.setTextColor(Color.black);
        _name.setTextFilled(false);
        _name.setFilled(false);
        _name.setLineWidth(0);
        _name.setExpandOnly(false);
        _name.setMultiLine(false);
        _name.setAllowsTab(false);

        _stereo.setFont(LABEL_FONT);
        _stereo.setTextColor(Color.black);
        _stereo.setTextFilled(false);
        _stereo.setFilled(false);
        _stereo.setLineWidth(0);
        _stereo.setExpandOnly(false);
        _stereo.setMultiLine(false);
        _stereo.setAllowsTab(false);

        setBetweenNearestPoints(true);
        ((FigPoly) _fig).setRectilinear(false);
        ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    public FigEdgeModelElement(Object edge) {
        this();
        setOwner(edge);
        ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    public void finalize() {
        ArgoEventPump.removeListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public void setItemUID(ItemUID id) {
        _id = id;
    }

    public ItemUID getItemUID() {
        return _id;
    }

    public String getTipString(MouseEvent me) {
        ToDoItem item = hitClarifier(me.getX(), me.getY());
        String tip = "";
        if (item != null)
            tip = item.getHeadline();
        else if (getOwner() != null)
            tip = getOwner().toString();
        else
            tip = toString();
        if (tip != null && tip.length() > 0 && !tip.endsWith(" "))
            tip += " ";
        return tip;
    }

    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = super.getPopUpActions(me);
        ToDoList list = Designer.TheDesigner.getToDoList();
        Vector items = (Vector) list.elementsForOffender(getOwner()).clone();
        if (items != null && items.size() > 0) {
            ArgoJMenu critiques = new ArgoJMenu(BUNDLE, "menu.popup.critiques");
            ToDoItem itemUnderMouse = hitClarifier(me.getX(), me.getY());
            if (itemUnderMouse != null) {
                critiques.add(new ActionGoToCritique(itemUnderMouse));
                critiques.addSeparator();
            }
            int size = items.size();
            for (int i = 0; i < size; i++) {
                ToDoItem item = (ToDoItem) items.elementAt(i);
                if (item == itemUnderMouse)
                    continue;
                critiques.add(new ActionGoToCritique(item));
            }
            popUpActions.insertElementAt(critiques, 0);
        }
        popUpActions.addElement(ActionProperties.SINGLETON);
        popUpActions.addElement(ActionDeleteFromDiagram.SINGLETON);
        return popUpActions;
    }

    // distance formula: (x-h)^2 + (y-k)^2 = distance^2
    public int getSquaredDistance(Point p1, Point p2) {
        int xSquared = p2.x - p1.x;
        xSquared *= xSquared;
        int ySquared = p2.y - p1.y;
        ySquared *= ySquared;
        return xSquared + ySquared;
    }

    public void paintClarifiers(Graphics g) {
        int iconPos = 25, gap = 1, xOff = -4, yOff = -4;
        Point p = new Point();
        ToDoList list = Designer.theDesigner().getToDoList();
        Vector items = list.elementsForOffender(getOwner());
        int size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
            }
            stuffPointAlongPerimeter(iconPos, p);
            icon.paintIcon(null, g, p.x + xOff, p.y + yOff);
            iconPos += icon.getIconWidth() + gap;
        }
        items = list.elementsForOffender(this);
        size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
            }
            stuffPointAlongPerimeter(iconPos, p);
            icon.paintIcon(null, g, p.x + xOff, p.y + yOff);
            iconPos += icon.getIconWidth() + gap;
        }
    }

    public ToDoItem hitClarifier(int x, int y) {
        int iconPos = 25, xOff = -4, yOff = -4;
        Point p = new Point();
        ToDoList list = Designer.theDesigner().getToDoList();
        Vector items = list.elementsForOffender(getOwner());
        int size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            stuffPointAlongPerimeter(iconPos, p);
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            if (y >= p.y + yOff
                && y <= p.y + height + yOff
                && x >= p.x + xOff
                && x <= p.x + width + xOff)
                return item;
            iconPos += width;
        }
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
                if (((Clarifier) icon).hit(x, y))
                    return item;
            }
        }
        items = list.elementsForOffender(this);
        size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            stuffPointAlongPerimeter(iconPos, p);
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            if (y >= p.y + yOff
                && y <= p.y + height + yOff
                && x >= p.x + xOff
                && x <= p.x + width + xOff)
                return item;
            iconPos += width;
        }
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
                if (((Clarifier) icon).hit(x, y))
                    return item;
            }
        }
        return null;
    }

    /**
     * Returns a SelectionRerouteEdge object that manages selection
     * and rerouting of the edge.
     */
    public Selection makeSelection() {
        return new SelectionRerouteEdge(this);
    }

    public FigText getNameFig() {
        return _name;
    }
    public FigText getStereotypeFig() {
        return _stereo;
    }

    public void vetoableChange(PropertyChangeEvent pce) {
        Object src = pce.getSource();
        if (src == getOwner()) {
            DelayedChangeNotify delayedNotify =
                new DelayedChangeNotify(this, pce);
            SwingUtilities.invokeLater(delayedNotify);
        }
    }

    public void delayedVetoableChange(PropertyChangeEvent pce) {
        Object src = pce.getSource();
        startTrans();
        // update any text, colors, fonts, etc.
        modelChanged(null);
        // update the relative sizes and positions of internel Figs
        Rectangle bbox = getBounds();
        setBounds(bbox.x, bbox.y, bbox.width, bbox.height);
        endTrans();
    }

    public void propertyChange(PropertyChangeEvent pve) {
        Object src = pve.getSource();
        String pName = pve.getPropertyName();
        if (pName.equals("editing")
            && Boolean.FALSE.equals(pve.getNewValue())) {
            cat.debug("finished editing");
            try {
                startTrans();
                textEdited((FigText) src);
                calcBounds();
                endTrans();
            } catch (PropertyVetoException ex) {
                cat.error(
			  "could not parse the text entered. Propertyvetoexception",
			  ex);
            }
        } else
            super.propertyChange(pve);
    }

    /** This method is called after the user finishes editing a text
     *  field that is in the FigEdgeModelElement.  Determine which field
     *  and update the model.  This class handles the name, subclasses
     *  should override to handle other text elements. */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        if (ft == _name) {
            if (getOwner() == null)
                return;
            ModelFacade.setName(getOwner(), ft.getText());
        }
    }

    protected boolean canEdit(Fig f) {
        return true;
    }

    ////////////////////////////////////////////////////////////////
    // event handlers - MouseListener implementation

    public void mousePressed(MouseEvent me) {
    }
    public void mouseReleased(MouseEvent me) {
    }
    public void mouseEntered(MouseEvent me) {
    }
    public void mouseExited(MouseEvent me) {
    }

    /** If the user double clicks on anu part of this FigNode, pass it
     *  down to one of the internal Figs.  This allows the user to
     *  initiate direct text editing. */
    public void mouseClicked(MouseEvent me) {
        if (me.isConsumed())
            return;
        if (me.getClickCount() >= 2) {
            Fig f = hitFig(new Rectangle(me.getX() - 2, me.getY() - 2, 4, 4));
            if (f instanceof MouseListener && canEdit(f))
		((MouseListener) f).mouseClicked(me);
        }
        me.consume();
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.isConsumed())
            return;
        if (_name != null && canEdit(_name))
            _name.keyPressed(ke);
    }

    /** not used, do nothing. */
    public void keyReleased(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
    }

    ////////////////////////////////////////////////////////////////
    // internal methods

    /** This is called aftern any part of the UML MModelElement has
     *  changed. This method automatically updates the name FigText.
     *  Subclasses should override and update other parts. */
    protected void modelChanged(MElementEvent e) {
        if (e == null
            || (e.getSource() == getOwner() && "name".equals(e.getName())))
            updateNameText();
        updateStereotypeText();

        if (ActionAutoResize.isAutoResizable()) {
            CmdSetPreferredSize cmdSPS =
                new CmdSetPreferredSize(CmdSetPreferredSize.MINIMUM_SIZE);
            cmdSPS.setFigToResize(this);
            cmdSPS.doIt();
        }
        if (!updateClassifiers())
            return;
    }

    private void updateNameText() {
        
        if (getOwner() == null)
            return;
        String nameStr = Notation.generate(this, ModelFacade.getName(getOwner()));
        _name.setText(nameStr);
    }

    protected void updateStereotypeText() {
        if (getOwner() == null) {
            return;
        }
        Object stereotype = null;
        if (ModelFacade.getStereotypes(getOwner()).size() > 0) {
            stereotype = ModelFacade.getStereotypes(getOwner()).iterator().next();
        }
        if (stereotype == null) {
            _stereo.setText("");
            return;
        }
        String stereoStr = ModelFacade.getName(stereotype);
        if (stereoStr.length() == 0)
            _stereo.setText("");
        else {
            _stereo.setText(Notation.generateStereotype(this, stereotype));
        }
    }

    public void setOwner(Object newOwner) {
        super.setOwner(newOwner);
        if (newOwner != null) {
            Object oldOwner = getOwner();

            if (org.argouml.model.ModelFacade.isAModelElement(oldOwner))
                UmlModelEventPump.getPump().removeModelEventListener(this,oldOwner);
            if (org.argouml.model.ModelFacade.isAModelElement(newOwner)) {
                UmlModelEventPump.getPump().addModelEventListener(this, newOwner);
                if (ModelFacade.getUUID(newOwner) == null)
                    ModelFacade.setUUID(newOwner, UUIDManager.SINGLETON.getNewUUID());
            }
            modelChanged(null);
        }

    }

    public void propertySet(MElementEvent mee) {
        
        modelChanged(mee);
        damage();
    }
    public void listRoleItemSet(MElementEvent mee) {
        
        modelChanged(mee);
        damage();
    }
    public void recovered(MElementEvent mee) {
        
        modelChanged(mee);
        damage();
    }
    public void removed(MElementEvent mee) {
        
        cat.debug("deleting: " + this + mee);
        if (mee.getSource() == getOwner())
            this.delete();
        else {
            modelChanged(mee);
            damage();
        }

    }
    public void roleAdded(MElementEvent mee) {
        
        modelChanged(mee);
        damage();
    }
    public void roleRemoved(MElementEvent mee) {
        
        modelChanged(mee);
        damage();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#dispose()
     */
    public void dispose() {
        Object own = getOwner();
        if (own != null) {
            Trash.SINGLETON.addItemFrom(getOwner(), null);
            if (org.argouml.model.ModelFacade.isAModelElement(own)) {
                UmlFactory.getFactory().delete(own);
            }
        }
        Iterator it = getPathItemFigs().iterator();
        while (it.hasNext()) {
            ((Fig) it.next()).dispose();
        }
        super.dispose();
    }

    /** This default implementation simply requests the default notation.
     */
    public NotationName getContextNotation() {
        return _currentNotationName;
    }

    public void notationChanged(ArgoNotationEvent event) {
        PropertyChangeEvent changeEvent =
	    (PropertyChangeEvent) event.getSource();
	_currentNotationName =
	    Notation.findNotation((String) changeEvent.getNewValue());
        renderingChanged();
        damage();
    }

    public void notationAdded(ArgoNotationEvent event) {
    }
    public void notationRemoved(ArgoNotationEvent event) {
    }
    public void notationProviderAdded(ArgoNotationEvent event) {
    }
    public void notationProviderRemoved(ArgoNotationEvent event) {
    }

    /**
     * Rerenders the fig if needed. This functionality was originally
     * the functionality of modelChanged but modelChanged takes the
     * event now into account.
     */
    public void renderingChanged() {
        // updateAnnotationPositions();
        updateClassifiers();
        updateNameText();
        updateStereotypeText();
        damage();
    }

    /**
     * Necessary since GEF contains some errors regarding the hit subject.
     * TODO make the bigBounds port go off a little less
     * @see org.tigris.gef.presentation.Fig#hit(Rectangle)
     */
    public boolean hit(Rectangle r) {
        if (_fig.hit(r))
            return true;
        Polygon polOuter = ((FigPoly) _fig).getPolygon();
        polOuter.translate(-8, -8);
        Polygon polInner = ((FigPoly) _fig).getPolygon();
        polInner.translate(8, 8);
        Polygon containing = new Polygon();
        for (int i = 0; i < polOuter.xpoints.length; i++) {
            containing.addPoint(polOuter.xpoints[i], polOuter.ypoints[i]);
        }
        for (int i = polInner.xpoints.length - 1; i >= 0; i--) {
            containing.addPoint(polInner.xpoints[i], polInner.ypoints[i]);
        }
        if (containing.intersects(r))
            return true;
        // if (polOuter.intersects(r) && !polInner.intersects(r)) return true;

        int size = _pathItems.size();
        for (int i = 0; i < size; i++) {
            Fig f = getPathItemFig((FigEdge.PathItem) _pathItems.elementAt(i));
            if (f.hit(r))
                return true;
        }
        /*Rectangle bigBounds = getBounds();
	  FigRect rect = new FigRect(bigBounds.x, bigBounds.y,
	  bigBounds.width, bigBounds.height); if (rect.hit(r)) return
	  true;
        */
        return false;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#delete()
     */
    public void delete() {
        Object o = getOwner();
        if (org.argouml.model.ModelFacade.isABase(o)) {
            UmlModelEventPump.getPump().removeModelEventListener(this, o);
        }
        if (this instanceof ArgoEventListener) {
            ArgoEventPump.removeListener(this);
        }

        Iterator it = getPathItemFigs().iterator();
        while (it.hasNext()) {
            Fig fig = (Fig) it.next();
            fig.delete();
        }

        // GEF does not take into account the multiple diagrams we have
        // therefore we loop through our diagrams and delete each and every 
        // occurence on our own
        it =
            ProjectManager
	    .getManager()
	    .getCurrentProject()
	    .getDiagrams()
	    .iterator();
        while (it.hasNext()) {
            ArgoDiagram diagram = (ArgoDiagram) it.next();
            diagram.damage();
        }
        super.delete();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#damage()
     */
    public void damage() {
        super.damage();
        _fig.damage();
    }

    /**
     * <p>Updates the classifiers the edge is attached to.  <p>Calls a
     * helper method (layoutThisToSelf) to avoid this edge
     * disappearing if the new source and dest are the same node.
     *
     * @return boolean whether or not the update was sucessful
     */
    protected boolean updateClassifiers() {
        Object owner = getOwner();
        if (owner == null || getLayer() == null)
            return false;

        Object newSource = getSource();
        Object newDest = getDestination();

        Fig currentSourceFig = getSourceFigNode();
        Fig currentDestFig = getDestFigNode();
        Object currentSource = null;
        Object currentDestination = null;
        if (currentSourceFig != null && currentDestFig != null) {
            currentSource = currentSourceFig.getOwner();
            currentDestination = currentDestFig.getOwner();
        }
        if (newSource != currentSource || newDest != currentDestination) {
            Fig newSourceFig = null;
            if (newSource != null)
                newSourceFig = getLayer().presentationFor(newSource);
            Fig newDestFig = null;
            if (newDest != null)
                newDestFig = getLayer().presentationFor(newDest);
            if (newSourceFig == null || newDestFig == null) {
                delete();
                return false;
            }
            if (true) {
            }
            if (newSourceFig != null && newSourceFig != currentSourceFig) {
                setSourceFigNode((FigNode) newSourceFig);
                setSourcePortFig(newSourceFig);

            }
            if (newDestFig != null && newDestFig != currentDestFig) {
                setDestFigNode((FigNode) newDestFig);
                setDestPortFig(newDestFig);
            }
            if (newDestFig != null && newSourceFig != null) {
                ((FigNode) newSourceFig).updateEdges();
            }
            if (newSourceFig != null && newDestFig != null) {
                ((FigNode) newDestFig).updateEdges();
            }
            calcBounds();

            // adapted from SelectionWButtons from line 280
            // calls a helper method to avoid this edge disappearing
            // if the new source and dest are the same node.
            if (newSourceFig == newDestFig) {

                layoutThisToSelf();
            }

        }

        return true;
    }

    /**
     * helper method for updateClassifiers() in order to automatically
     * layout an edge that is now from and to the same node type.
     * <p>adapted from SelectionWButtons from line 280
     */
    private void layoutThisToSelf() {

        FigPoly edgeShape = new FigPoly();
        //newFC = _content;
        Point fcCenter = getSourceFigNode().center();
        Point centerRight =
            new Point(
		      (int) (fcCenter.x
			     + getSourceFigNode().getSize().getWidth() / 2),
		      fcCenter.y);

        int yoffset = (int) ((getSourceFigNode().getSize().getHeight() / 2));
        edgeShape.addPoint(fcCenter.x, fcCenter.y);
        edgeShape.addPoint(centerRight.x, centerRight.y);
        edgeShape.addPoint(centerRight.x + 30, centerRight.y);
        edgeShape.addPoint(centerRight.x + 30, centerRight.y + yoffset);
        edgeShape.addPoint(centerRight.x, centerRight.y + yoffset);

        // place the edge on the layer and update the diagram
        this.setBetweenNearestPoints(true);
        edgeShape.setLineColor(Color.black);
        edgeShape.setFilled(false);
        edgeShape._isComplete = true;
        this.setFig(edgeShape);
    }

    /**
     * Returns the source of the edge. The source is the owner of the
     * node the edge travels from in a binary relationship. For
     * instance: for a classifierrole, this is the sender.
     * @return MModelElement
     */
    protected Object getSource() {
        if (getOwner() != null) {
            return CoreHelper.getHelper().getSource(getOwner());
        }
        return null;
    }
    /**
     * Returns the destination of the edge. The destination is the
     * owner of the node the edge travels to in a binary
     * relationship. For instance: for a classifierrole, this is the
     * receiver. Since we don't support n-array associations but only
     * binary relations, source/destination works for all edges.
     * @return Object
     */
    protected Object getDestination() {
        if (getOwner() != null) {
            return CoreHelper.getHelper().getDestination(getOwner());
        }
        return null;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#postLoad()
     */
    public void postLoad() {
        super.postLoad();        
        if (this instanceof ArgoEventListener) {
            ArgoEventPump.removeListener(this);
            ArgoEventPump.addListener(this);
        }
    }

    /**
     * Overridden to notify project that save is needed when edge is moved.
     */
    public void setPoints(Handle h, int x, int y) {
        super.setPoints(h, x, y);
        Project p = ProjectManager.getManager().getCurrentProject();
        if (p != null) {      
            p.setNeedsSave(true);
        }
    }

} /* end class FigEdgeModelElement */