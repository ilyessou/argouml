// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

// File: FigNodeModelElement.java
// Classes: FigNodeModelElement
// Original Author: abonner

package org.argouml.uml.diagram.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.argouml.application.api.ArgoEventListener;
import org.argouml.application.api.Configuration;
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
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;
import org.argouml.ui.ActionGoToCritique;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.ArgoJMenu;
import org.argouml.ui.Clarifier;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.UUIDManager;
import org.argouml.uml.generator.ParserDisplay;
import org.argouml.util.Trash;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;

/** Abstract class to display diagram icons for UML ModelElements that
 *  look like nodes and that have editiable names and can be
 *  resized.
 */
public abstract class FigNodeModelElement
    extends FigNode
    implements
        VetoableChangeListener,
        DelayedVChangeListener,
        MouseListener,
        KeyListener,
        PropertyChangeListener,
        MElementListener, // TODO NSUML interface, how do we rid ourselves of this?
        NotationContext,
        ArgoNotationEventListener {            

    private Logger cat = Logger.getLogger(this.getClass());
    ////////////////////////////////////////////////////////////////
    // constants

    private NotationName _currentNotationName;
    public static Font LABEL_FONT;
    public static Font ITALIC_LABEL_FONT;
    public final int MARGIN = 2;

    protected static final int ROWHEIGHT = 17;
    // min. 17, used to calculate y pos of FigText items in a compartment
    protected static final int STEREOHEIGHT = 18;
    protected boolean checkSize = true;
    // Needed for loading. Warning: if false, a too small size might look bad!
    
    /**
     * Offset from the end of the set of popup actions at which new items
     * should be inserted by concrete figures.
    **/
    protected static final int POPUP_ADD_OFFSET = 3;
    
    // Fields used in paint() for painting shadows
    private BufferedImage           _shadowImage = null;
    private int                     _cachedWidth = -1;
    private int                     _cachedHeight = -1;
    private static final LookupOp   _shadowLookupOp;
    private static final ConvolveOp _shadowConvolveOp;

    /**
     * The intensity value of the shadow color (0-255).
    **/
    protected static final int SHADOW_COLOR_VALUE = 32;
    
    /**
     * The transparency value of the shadow color (0-255).
    **/    
    protected static final int SHADOW_COLOR_ALPHA = 128;
    
    protected static final String BUNDLE = "UMLMenu";

    static {
        LABEL_FONT =
            new javax.swing.plaf.metal.DefaultMetalTheme().getSubTextFont();
        ITALIC_LABEL_FONT =
            new Font(LABEL_FONT.getFamily(), Font.ITALIC, LABEL_FONT.getSize());

        // Setup image ops used in rendering shadows            
        byte[][] data = new byte[4][256];
        for (int i = 1; i < 256; ++i) {
            data[0][i] = (byte) SHADOW_COLOR_VALUE;
            data[1][i] = (byte) SHADOW_COLOR_VALUE;
            data[2][i] = (byte) SHADOW_COLOR_VALUE;
            data[3][i] = (byte) SHADOW_COLOR_ALPHA;
        }        
        float[] blur = new float[9];
        for (int i = 0; i < blur.length; ++i) {
            blur[i] = 1 / 12f;
        }
        _shadowLookupOp = new LookupOp(new ByteLookupTable(0, data), null);
        _shadowConvolveOp = new ConvolveOp(new Kernel(3, 3, blur));            
    }

    ////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * @deprecated 0.15.3 visibility will change use getter/setter
     */
    protected FigRect _bigPort;
    /**
     * @deprecated 0.15.3 visibility will change use
     * getNameFig() and setNameFig() to access the Figs.
     * Use getName() and setName() to just change the text.
     */
    public FigText _name; // TODO: - public!! Make private!
    /**
     * @deprecated 0.15.3 visibility will change use getter/setter
     * getStereotypeFig() and setStereoTypeFig() to access the Figs.
     * Use getStereotype() and setStereotype() to change stereotype
     * text.
     */
    public FigText _stereo; // TODO: - public!! Make private!
    // TODO could somebody please javadoc what _enclosedFigs is.
    // Is it just a duplicate of the collection in a FigGroup.
    // Is this anything to do with FigClass inside FigPackage in
    // a class diagram? Bob Tarling 28 Jan 2004
    protected Vector _enclosedFigs = new Vector();
    protected Fig _encloser = null;
    protected boolean _readyToEdit = true;
    protected boolean suppressCalcBounds = false;
    /**
     * @deprecated 0.15.3 visibility will change use getter/setter
     */
    public int _shadowSize =
        Configuration.getInteger(Notation.KEY_DEFAULT_SHADOW_WIDTH, 1);
    private ItemUID _id;

    /**
     * A set of object arrays consisting of a sender of events and the
     * event types this object is interested in. The eventSenders are
     * a cache to improve performance when this fig is
     * disabled/enabled as interested listener to the events
     * maintained in the _eventSenders set.
     */
    private Set _eventSenders = new HashSet();

    ////////////////////////////////////////////////////////////////
    // constructors

    public FigNodeModelElement() {
        // this rectangle marks the whole interface figure; everything
        // is inside it:
        _bigPort = new FigRect(10, 10, 0, 0, Color.cyan, Color.cyan);

        _name = new FigText(10, 10, 90, 21, true);
        _name.setFont(LABEL_FONT);
        _name.setTextColor(Color.black);
        // _name.setFilled(false);
        _name.setMultiLine(false);
        _name.setAllowsTab(false);
        _name.setText(placeString());

        _stereo = new FigText(10, 10, 90, 15, true);
        _stereo.setFont(LABEL_FONT);
        _stereo.setTextColor(Color.black);
        _stereo.setFilled(false);
        _stereo.setLineWidth(0);
        //_stereo.setLineColor(Color.black);
        _stereo.setEditable(false);

        _readyToEdit = false;
        ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    /** Partially construct a new FigNode.  This method creates the
     *  _name element that holds the name of the model element and adds
     *  itself as a listener. */
    public FigNodeModelElement(GraphModel gm, Object node) {
        this();
        setOwner(node);
        _name.setText(placeString());
        _readyToEdit = false;
        ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    public void finalize() {
        ArgoEventPump.removeListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

// TODO Too close to a release to introduce this now
// but I think we need this clone method at this level to save
// duplicated code in ancestors
// Bob Tarling 28 Jan 2004
//    public Object clone() {
//        FigNodeModelElement figClone = (FigNodeModelElement) super.clone();
//        figClone._bigPort = _bigPort;
//        figClone._name = _name;
//        return figClone;
//    }
// _enclosedFigs, _encloser and _eventSenders may also need to be cloned
// must check usage
//


    /** Reply text to be shown while placing node in diagram */
    public String placeString() {
        if (org.argouml.model.ModelFacade.isAModelElement(getOwner())) {
            String placeString = ModelFacade.getName(getOwner());
            if (placeString == null) {
                placeString = "new " + ModelFacade.getUMLClassName(getOwner());
            }
            return placeString;
        }
        return "";
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public void setItemUID(ItemUID id) {
        _id = id;
    }

    public ItemUID getItemUID() {
        return _id;
    }

    /**
     * Get the Fig that displays the model element name
     * @return the name Fig
     */
    public FigText getNameFig() {
        return _name;
    }

    /**
     * Set the Fig that displays the model element name
     * @param fig the name Fig
     */
    protected void setNameFig(FigText fig) {
        _name = fig;
    }

    /**
     * Get the name of the model element this Fig represents
     * @return the name of the model element
     */
    public String getName() {
        return _name.getText();
    }

    /**
     * Change the name of the model element this Fig represents
     * @param name the name of the model element
     */
    public void setName(String name) {
        _name.setText(name);
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
                if (item != itemUnderMouse) {
                    critiques.add(new ActionGoToCritique(item));
                }
            }
            popUpActions.insertElementAt(new JSeparator(), 0);
            popUpActions.insertElementAt(critiques, 0);
        }
        // POPUP_ADD_OFFSET should be equal to the number of items added here:
        popUpActions.addElement(new JSeparator());
        popUpActions.addElement(ActionProperties.SINGLETON);
        popUpActions.addElement(ActionDeleteFromDiagram.SINGLETON);
        return popUpActions;
    }

    ////////////////////////////////////////////////////////////////
    // Fig API

    public Fig getEnclosingFig() {
        return _encloser;
    }

    /**
     * Updates the modelelement container if the fig is moved in or
     * out another fig. If this fig doesn't have an enclosing fig
     * anymore, the namespace of the diagram will be the owning
     * modelelement. If this fig is moved inside another
     * FigNodeModelElement the owner of that fignodemodelelement will
     * be the owning modelelement.
     * @see org.tigris.gef.presentation.Fig#setEnclosingFig(org.tigris.gef.presentation.Fig)
     */
    public void setEnclosingFig(Fig encloser) {
	super.setEnclosingFig(encloser);
	Fig oldEncloser = _encloser;
	if (encloser != oldEncloser) {
	    Object owningModelelement = null;
	    if (encloser == null) {
		// moved outside another fig onto the diagram canvas
		Project currentProject =
		    ProjectManager.getManager().getCurrentProject();
                ArgoDiagram diagram = currentProject.getActiveDiagram();
                if (diagram instanceof UMLDiagram
			&& ((UMLDiagram) diagram).getNamespace() != null) {
                    owningModelelement = ((UMLDiagram) diagram).getNamespace();
                } else {
                    owningModelelement = currentProject.getRoot();
                }
	    } else {
		// moved into a fig
                if (ModelFacade.isABase(encloser.getOwner())) {
                    owningModelelement = encloser.getOwner();
                }
            }
            if (owningModelelement != null
                    && getOwner() != null
                    && (!ModelManagementHelper.getHelper()
                        .isCyclicOwnership(owningModelelement, getOwner()))
                    && (!ModelFacade.isANamespace(owningModelelement)
                        || CoreHelper.getHelper()
                    	.isValidNamespace(getOwner(),
					  owningModelelement))) {
                ModelFacade.setModelElementContainer(getOwner(), 
						     owningModelelement);
                // TODO: move the associations to the correct owner (namespace)
            }
        }
	if (encloser != _encloser) {
	    if (_encloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) _encloser)._enclosedFigs.remove(this);
            }
	    if (encloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) encloser)._enclosedFigs.add(this);
            }
	}
        _encloser = encloser;
    }

    public Vector getEnclosedFigs() {
        return _enclosedFigs;
    }

    /** Update the order of this fig and the order of the
     *    figs that are inside of this fig */
    public void elementOrdering(Vector figures) {
        int size = figures.size();
        getLayer().bringToFront(this);
        if (figures != null && (size > 0)) {
            for (int i = 0; i < size; i++) {
                Object o = figures.elementAt(i);
                if (o instanceof FigNodeModelElement
                    && o != getEnclosingFig()) {
                    FigNodeModelElement fignode = (FigNodeModelElement) o;
                    Vector enclosed = fignode.getEnclosedFigs();
                    fignode.elementOrdering(enclosed);
                }
            }
        }
    }

    public Selection makeSelection() {
        return new SelectionNodeClarifiers(this);
    }

    /**
     * Overridden to paint shadows. This method supports painting shadows
     * for any FigNodeModelElement. Any Figs that are nested within the
     * FigNodeModelElement will be shadowed.<p>
     *
     * TODO: If g is not a Graphics2D shadows cannot be painted. This is
     * a problem when saving the diagram as SVG.
     *
     * @param g is a Graphics that we paint this object on.
     */
    public void paint(Graphics g) {
        if (_shadowSize > 0
	        && g instanceof Graphics2D) {
            int width = getWidth();
            int height = getHeight();
            int x = getX();
            int y = getY();

            // Only create new shadow image if figure size has changed.
            if (width != _cachedWidth 
                    || height != _cachedHeight) {
                _cachedWidth = width;
                _cachedHeight = height;

                BufferedImage img = new BufferedImage(
                    width + 100,
                    height + 100,
                    BufferedImage.TYPE_INT_ARGB);

                // Paint figure onto offscreen image
                Graphics ig = img.getGraphics();
                ig.translate(50 - x, 50 - y);
                super.paint(ig);

                // Apply two filters to the image:
                // 1. Apply LookupOp which converts all pixel data in the
                //    figure to the same shadow color.
                // 2. Apply ConvolveOp which creates blurred effect around
                //    the edges of the shadow.
                _shadowImage = _shadowConvolveOp.filter(
                    _shadowLookupOp.filter(img, null), null);
            }

            // Paint shadow image onto canvas
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(
                _shadowImage,
                null,
                x + _shadowSize - 50,
                y + _shadowSize - 50);
        }
            
        // Paint figure on top of shadow
        super.paint(g);
    }
    
    /**
     * Displays visual indications of pending ToDoItems.
     * Please note that the list of advices (ToDoList) is not the same
     * as the list of element known by the FigNode (_figs). Therefore,
     * it is necessary to check if the graphic item exists before drawing
     * on it. See ClAttributeCompartment for an example.
     * @see org.argouml.uml.cognitive.critics.ClAttributeCompartment
     */
    public void paintClarifiers(Graphics g) {
        int iconX = getX();
        int iconY = getY() - 10;
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
            if (icon != null) {
                icon.paintIcon(null, g, iconX, iconY);
                iconX += icon.getIconWidth();
            }
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
            if (icon != null) {
                icon.paintIcon(null, g, iconX, iconY);
                iconX += icon.getIconWidth();
            }
        }
    }

    public ToDoItem hitClarifier(int x, int y) {
        int iconX = getX();
        ToDoList list = Designer.theDesigner().getToDoList();
        Vector items = list.elementsForOffender(getOwner());
        int size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            int width = icon.getIconWidth();
            if (y >= getY() - 15
                    && y <= getY() + 10
                    && x >= iconX
                    && x <= iconX + width) {
                return item;
            }
            iconX += width;
        }
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
                if (((Clarifier) icon).hit(x, y)) {
                    return item;
                }
            }
        }
        items = list.elementsForOffender(this);
        size = items.size();
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            int width = icon.getIconWidth();
            if (y >= getY() - 15
                    && y <= getY() + 10
                    && x >= iconX
                    && x <= iconX + width) {
                return item;
            }
            iconX += width;
        }
        for (int i = 0; i < size; i++) {
            ToDoItem item = (ToDoItem) items.elementAt(i);
            Icon icon = item.getClarifier();
            if (icon instanceof Clarifier) {
                ((Clarifier) icon).setFig(this);
                ((Clarifier) icon).setToDoItem(item);
                if (((Clarifier) icon).hit(x, y)) {
                    return item;
                }
            }
        }
        return null;
    }

    public String getTipString(MouseEvent me) {
        ToDoItem item = hitClarifier(me.getX(), me.getY());
        String tip = "";
        if (item != null 
            && Globals.curEditor().getSelectionManager().containsFig(this)) {
            tip = item.getHeadline() + " ";
        } else if (getOwner() != null) {
            tip = getOwner().toString();
        } else {
            tip = toString();
        }
        if (tip != null && tip.length() > 0 && !tip.endsWith(" ")) {
            tip += " ";
        }
        return tip;
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    public void vetoableChange(PropertyChangeEvent pce) {
        cat.debug("in vetoableChange");
        Object src = pce.getSource();
        if (src == getOwner()) {
            DelayedChangeNotify delayedNotify =
                new DelayedChangeNotify(this, pce);
            SwingUtilities.invokeLater(delayedNotify);
        } else {
            cat.debug("FigNodeModelElement got vetoableChange"
		      + " from non-owner:"
		      + src);
        }
    }

    public void delayedVetoableChange(PropertyChangeEvent pce) {
        cat.debug("in delayedVetoableChange");
        // TODO the src variable is never used. Must check if getSource()
        // has any side effects before removing entire line
        Object src = pce.getSource();
        // update any text, colors, fonts, etc.
        renderingChanged();
        endTrans();
    }

    protected void updateBounds() {
        if (!checkSize) {
            return;
        }
        Rectangle bbox = getBounds();
        Dimension minSize = getMinimumSize();
        bbox.width = Math.max(bbox.width, minSize.width);
        bbox.height = Math.max(bbox.height, minSize.height);
        setBounds(bbox.x, bbox.y, bbox.width, bbox.height);
    }

    public void propertyChange(PropertyChangeEvent pve) {
        Object src = pve.getSource();
        String pName = pve.getPropertyName();
        if (pName.equals("editing")
                && Boolean.FALSE.equals(pve.getNewValue())) {
            cat.debug("finished editing");
            try {
                //parse the text that was edited
                textEdited((FigText) src);
                // resize the FigNode to accomodate the new text
                Rectangle bbox = getBounds();
                Dimension minSize = getMinimumSize();
                bbox.width = Math.max(bbox.width, minSize.width);
                bbox.height = Math.max(bbox.height, minSize.height);
                setBounds(bbox.x, bbox.y, bbox.width, bbox.height);
                endTrans();
            } catch (PropertyVetoException ex) {
                cat.error("could not parse the text entered. "
			  + "PropertyVetoException",
			  ex);
            }
        } else {
            super.propertyChange(pve);
        }
    }

    /** This method is called after the user finishes editing a text
     *  field that is in the FigNodeModelElement.  Determine which field
     *  and update the model.  This class handles the name, subclasses
     *  should override to handle other text elements. */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        if (ft == _name) {
            if (getOwner() == null) {
                return;
            }
            try {
                ParserDisplay.SINGLETON.parseModelElement(getOwner(),
							  ft.getText().trim());
                ProjectBrowser.getInstance().getStatusBar().showStatus("");
                updateNameText();
            } catch (ParseException pe) {
                ProjectBrowser.getInstance().getStatusBar()
		    .showStatus("Error: " + pe + " at " + pe.getErrorOffset());
                // if there was a problem parsing,
                // then reset the text in the fig - because the model was not
                // updated.
                if (ModelFacade.getName(getOwner()) != null) {
                    ft.setText(ModelFacade.getName(getOwner()));
                } else {
                    ft.setText("");
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // event handlers - MouseListener implementation

    /** If the user double clicks on any part of this FigNode, pass it
     *  down to one of the internal Figs. This allows the user to
     *  initiate direct text editing. */
    public void mouseClicked(MouseEvent me) {
        if (!_readyToEdit) {
            if (ModelFacade.isAModelElement(getOwner())) {
                ModelFacade.setName(getOwner(),"");
                _readyToEdit = true;
            } else {
                cat.debug("not ready to edit name");
                return;
            }
        }
        if (me.isConsumed()) {
            return;
        }
        if (me.getClickCount() >= 2
                && !(me.isPopupTrigger()
		|| me.getModifiers() == InputEvent.BUTTON3_MASK)) {
            if (getOwner() == null) {
                return;
            }
            Rectangle r = new Rectangle(me.getX() - 2, me.getY() - 2, 4, 4);
            Fig f = hitFig(r);
            if (f instanceof MouseListener) {
		((MouseListener) f).mouseClicked(me);
            } else if (f instanceof FigGroup) {
                //this enables direct text editing for sub figs of a
                //FigGroup object:
                Fig f2 = ((FigGroup) f).hitFig(r);
                if (f2 instanceof MouseListener) {
		    ((MouseListener) f2).mouseClicked(me);
                } else {
                    createFeatureIn((FigGroup) f, me);
                }
            }
        }
        me.consume();
    }

    public void keyPressed(KeyEvent ke) {
        if (!_readyToEdit) {
            if (ModelFacade.isAModelElement(getOwner())) {
                ModelFacade.setName(getOwner(),"");
                _readyToEdit = true;
            } else {
                cat.debug("not ready to edit name");
                return;
            }
        }
        if (ke.isConsumed() || getOwner() == null) {
            return;
        }
        _name.keyPressed(ke);
    }

    /** not used, do nothing. */
    public void keyReleased(KeyEvent ke) {
    }

    /** not used, do nothing. */
    public void keyTyped(KeyEvent ke) {
    }

    ////////////////////////////////////////////////////////////////
    // internal methods

    /** This is called aftern any part of the UML MModelElement has
     *  changed. This method automatically updates the name FigText.
     *  Subclasses should override and update other parts.
     */
    protected void modelChanged(MElementEvent mee) {
        if (mee == null) {
            throw new IllegalArgumentException("event may never be null "
					       + "with modelchanged");
        }
        if (getOwner() == null) {
            return;
        }
        if ("name".equals(mee.getName()) && mee.getSource() == getOwner()) {
            updateNameText();
            damage();
        }
        if ((mee.getSource() == getOwner()
	     && mee.getName().equals("stereotype"))) {
            updateStereotypeText();
            damage();
        }
    }

    protected void createFeatureIn(FigGroup fg, InputEvent me) {
        // must be overridden to make sense
        // (I didn't want to make it abstract because it might not be required)
    }

    public void propertySet(MElementEvent mee) {
        //if (_group != null) _group.propertySet(mee);        
        modelChanged(mee);
    }
    public void listRoleItemSet(MElementEvent mee) {
        //if (_group != null) _group.listRoleItemSet(mee);
        modelChanged(mee);
    }
    public void recovered(MElementEvent mee) {
        //if (_group != null) _group.recovered(mee);
    }
    public void removed(MElementEvent mee) {
        cat.debug("deleting: " + this + mee);
        Object o = mee.getSource();
        if (o == getOwner()) {
            delete();
        } else if (isPartlyOwner(o)) {
            updateBounds();
            damage();
            return;
        }

    }

    protected boolean isPartlyOwner(Object o) {
        if (o == null || o == getOwner()) {
            return true;
        }
        Iterator it = getFigs(null).iterator();
        while (it.hasNext()) {
            Fig fig = (Fig) it.next();
            if (isPartlyOwner(fig, o)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isPartlyOwner(Fig fig, Object o) {
        if (o == null) {
            return false;
        }
        if (o == fig.getOwner()) {
            return true;
        }
        if (fig instanceof FigGroup) {
            Iterator it = ((FigGroup) fig).getFigs(null).iterator();
            while (it.hasNext()) {
                Fig fig2 = (Fig) it.next();
                if (isPartlyOwner(fig2, o)) {
                    return true;
                }
            }
        }
        return false;
    }
    public void roleAdded(MElementEvent mee) {
        //if (_group != null) _group.roleAdded(mee);
        modelChanged(mee);
    }
    public void roleRemoved(MElementEvent mee) {
        //if (_group != null) _group.roleRemoved(mee);
        modelChanged(mee);
    }

    public void dispose() {
        Object own = getOwner();
        if (own != null) {
            Trash.SINGLETON.addItemFrom(own, null);
            if (ModelFacade.isAModelElement(own)) {
                UmlFactory.getFactory().delete(own);
            }
        }
        Iterator it = getFigs(null).iterator();
        while (it.hasNext()) {
            ((Fig) it.next()).dispose();
        }
        super.dispose();
    }

    public void setOwner(Object own) {
        // TODO the oldOwner variable is never used. Must check if getOwner()
        // has any side effects before removing entire line
        Object oldOwner = getOwner();
        updateListeners(own);
        super.setOwner(own);
        if (ModelFacade.isAModelElement(own)
                && ModelFacade.getUUID(own) == null) {
            ModelFacade.setUUID(own, UUIDManager.SINGLETON.getNewUUID());
        }
        _readyToEdit = true;
        if (own != null) {
            renderingChanged();
        }
        updateBounds();
        bindPort(own, _bigPort);
    }

    /**
     * Updates the text of the sterotype FigText. Override in subclasses to get
     * wanted behaviour.
     *
     * TODO: remove all 'misuses' of the stereotype figtexts (like in
     * FigInterface)
     */
    protected void updateStereotypeText() {
        Object stereotype = null;
        if (getOwner() == null) {
	    cat.warn("Owner of [" + this.toString() + "/"
		     + this.getClass() + "] is null.");
	    cat.warn("I return...");
	    return;
        }
        if (ModelFacade.getStereotypes(getOwner()).size() > 0) {
            stereotype =
		ModelFacade.getStereotypes(getOwner()).iterator().next();
        }
        _stereo.setText(Notation.generate(this, stereotype));
    }

    /**
     * Updates the text of the name FigText.
     */
    protected void updateNameText() {
        if (_readyToEdit) {
            if (getOwner() == null)
                return;
            String nameStr =
                Notation.generate(this, ModelFacade.getName(getOwner()));
            _name.setText(nameStr);
            updateBounds();
        }
    }

    /**
     * Implementations of this method should register/unregister the fig for all
     * (model)events. For FigNodeModelElement only the fig itself is registred
     * as listening to events fired by the owner itself. But for, for example,
     * FigClass the fig must also register for events fired by the operations
     * and attributes of the owner.
     * @param newOwner
     */
    protected void updateListeners(Object newOwner) {
        Object oldOwner = getOwner();
        if (oldOwner != null) {
            UmlModelEventPump.getPump().removeModelEventListener(this,
								 oldOwner);
        }
        if (newOwner != null) {
            UmlModelEventPump.getPump().addModelEventListener(this, newOwner);
        }

    }

    /**
     * Returns the notation name for this fig. First start to
     * implement notations on a per fig basis.
     * @see org.argouml.application.api.NotationContext#getContextNotation()
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
        updateNameText();
        updateStereotypeText();
        updateBounds();
        damage();
    }

    public void calcBounds() {
        if (suppressCalcBounds) {
            return;
        }
        super.calcBounds();
    }

    public void enableSizeChecking(boolean flag) {
        checkSize = flag;
    }

    /** returns the new size of the FigGroup (either attributes or
     * operations) after calculation new bounds for all sub-figs,
     * considering their minimal sizes; FigGroup need not be
     * displayed; no update event is fired
     */
    protected Dimension getUpdatedSize(
				       FigGroup fg,
				       int x,
				       int y,
				       int w,
				       int h) {
        int newW = w;
        int n = fg.getFigs(null).size() - 1;
        int newH = checkSize ? Math.max(h, ROWHEIGHT * Math.max(1, n) + 2) : h;
        int step = (n > 0) ? (newH - 1) / n : 0;
        // width step between FigText objects int maxA =
        //Toolkit.getDefaultToolkit().getFontMetrics(LABEL_FONT).getMaxAscent();

        //set new bounds for all included figs
        Enumeration figs = fg.elements();
        Fig bigPort = (Fig) figs.nextElement();
        Fig fi;
        int fw, yy = y;
        while (figs.hasMoreElements()) {
            fi = (Fig) figs.nextElement();
            fw = fi.getMinimumSize().width;
            if (!checkSize && fw > newW - 2)
                fw = newW - 2;
            fi.setBounds(x + 1, yy + 1, fw, Math.min(ROWHEIGHT, step) - 2);
            if (checkSize && newW < fw + 2)
                newW = fw + 2;
            yy += step;
        }
        bigPort.setBounds(x, y, newW, newH);
        // rectangle containing all following FigText objects
        fg.calcBounds();
        return new Dimension(newW, newH);
    }

    public void setShadowSize(int size) {
        _shadowSize = size;
    }

    public int getShadowSize() {
        return _shadowSize;
    }
    /**
     * Necessary since GEF contains some errors regarding the hit subject.
     * @see org.tigris.gef.presentation.Fig#hit(Rectangle)
     */
    public boolean hit(Rectangle r) {
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        if (_filled)
            return cornersHit > 0 || intersects(r);
        else
            return (cornersHit > 0 && cornersHit < 4) || intersects(r);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#delete()
     */
    public void delete() {
        if (this instanceof ArgoEventListener) {
            ArgoEventPump.removeListener(this);
        }

        Object own = getOwner();
        if (org.argouml.model.ModelFacade.isAClassifier(own)) {
            Iterator it = ModelFacade.getFeatures(own).iterator();
            while (it.hasNext()) {
                Object feature = it.next();
                if (ModelFacade.isAOperation(feature)) {
                    Iterator it2 =
			ModelFacade.getParameters(feature).iterator();
                    while (it2.hasNext()) {
                        UmlModelEventPump.getPump().removeModelEventListener(
                                                        	    this,
                                                                    it2.next());
                    }
                }
                UmlModelEventPump.getPump().removeModelEventListener(
								     this,
								     feature);
            }
        }
        if (ModelFacade.isABase(own)) {
            UmlModelEventPump.getPump().removeModelEventListener(this, own);
        }
        super.delete();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#damage()
     */
    public void damage() {
        updateEdges();
        super.damage();
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
        Iterator it = getFigs(null).iterator();
        while (it.hasNext()) {
            Fig fig = (Fig) it.next();
            if (fig instanceof ArgoEventListener) {
                // cannot do the adding of listeners recursive since
                // some are not children of FigNodeModelELement or
                // FigEdgeModelElement
                ArgoEventPump.removeListener((ArgoEventListener) fig);
                ArgoEventPump.addListener((ArgoEventListener) fig);
            }
        }
    }

    /**
     * Overridden to notify project that save is needed when figure is moved.
     */
    public void superTranslate(int dx, int dy) {
        super.superTranslate(dx, dy);
        Project p = ProjectManager.getManager().getCurrentProject();
        if (p != null) {      
            p.setNeedsSave(true);
        }
    }

    /**
     * Overridden to notify project that save is needed when figure is resized.
     */
    public void setHandleBox(int x, int y, int w, int h) {
        super.setHandleBox(x, y, w, h);
        Project p = ProjectManager.getManager().getCurrentProject();
        if (p != null) {      
            p.setNeedsSave(true);
        }
    }
    
    

    /**
     * Adds a fig to this FigNodeModelElement and removes it from the
     * group it belonged to if any.  Correction to the GEF
     * implementation that does not handle the double association
     * correctly.
     * @see org.tigris.gef.presentation.FigGroup#addFig(org.tigris.gef.presentation.Fig)
     * TODO remove this once GEF0.10 is in place and tested
     */
    public void addFig(Fig f) {
        Fig group = f.getGroup();
        if (group != null) {
            ((FigGroup) group).removeFig(f);
        }
        super.addFig(f);
    }

    /**
     * Set the Fig containing the stereotype
     * @param fig the stereotype Fig
     */
    protected void setStereotypeFig(Fig fig) {
        _stereo = (FigText) fig;
    }

    /**
     * Get the Fig containing the stereotype
     * @return the stereotype Fig
     */
    protected Fig getStereotypeFig() {
        return _stereo;
    }

    /**
     * Set the text describing the stereotype
     * @param stereotype the stereotype text
     */
    public void setStereotype(String stereotype) {
        _stereo.setText(stereotype);
    }

    /**
     * Get the text describing the stereotype
     * @return the stereotype text
     */
    public String getStereotype() {
        return _stereo.getText();
    }
} /* end class FigNodeModelElement */

