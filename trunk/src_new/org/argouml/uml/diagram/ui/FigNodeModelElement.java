// $Id$
// Copyright (c) 1996-2006 The Regents of the University of California. All
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.argouml.application.api.ArgoEventListener;
import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoNotationEvent;
import org.argouml.application.events.ArgoNotationEventListener;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.Highlightable;
import org.argouml.cognitive.ItemUID;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.ToDoList;
import org.argouml.i18n.Translator;
import org.argouml.kernel.DelayedChangeNotify;
import org.argouml.kernel.DelayedVChangeListener;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.kernel.ProjectSettings;
import org.argouml.model.DeleteInstanceEvent;
import org.argouml.model.DiElement;
import org.argouml.model.Model;
import org.argouml.notation.NotationProviderFactory2;
import org.argouml.ui.ActionGoToCritique;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.ArgoJMenu;
import org.argouml.ui.Clarifier;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.notation.NotationProvider;
import org.argouml.uml.ui.ActionDeleteModelElements;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphSupport;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/**
 * Abstract class to display diagram icons for UML ModelElements that
 * look like nodes and that have editable names and can be
 * resized.
 *
 * @author abonner
 */
public abstract class FigNodeModelElement
    extends FigNode
    implements
        VetoableChangeListener,
        DelayedVChangeListener,
        MouseListener,
        KeyListener,
        PropertyChangeListener,
        PathContainer,
        ArgoNotationEventListener,
        Highlightable {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(FigNodeModelElement.class);

    private DiElement diElement;

    ////////////////////////////////////////////////////////////////
    // constants

    private static final Font LABEL_FONT;
    private static final Font ITALIC_LABEL_FONT;
    private static final Font BOLD_LABEL_FONT;
    private static final Font BOLD_ITALIC_LABEL_FONT;

    protected NotationProvider notationProviderName;
    protected HashMap npArguments = new HashMap();
    
    /**
     * True of a subclass of FigNodeModelElement is allowed to be
     * invisible. This is currently only set true by FigEdgePort
     * TODO: FigEdgePort should be removed from the FigNodeModelElement
     * hierarchy and so the need for this removed.
     */
    protected boolean invisibleAllowed = false;

    /**
     * min. 17, used to calculate y pos of FigText items in a compartment
     */
    protected static final int ROWHEIGHT = 17;

    /**
     * min. 18, used to calculate y pos of stereotype FigText items
     * in a compartment
     */
    protected static final int STEREOHEIGHT = 18;

    /**
     * Needed for loading. Warning: if false, a too small size might look bad!
     */
    private boolean checkSize = true;

    /**
     * Offset from the end of the set of popup actions at which new items
     * should be inserted by concrete figures.
     * See #getPopUpActions()
     */
    private static int popupAddOffset;

    // Fields used in paint() for painting shadows
    private BufferedImage           shadowImage;
    private int                     cachedWidth = -1;
    private int                     cachedHeight = -1;
    private static final LookupOp   SHADOW_LOOKUP_OP;
    private static final ConvolveOp SHADOW_CONVOLVE_OP;

    /**
     * The intensity value of the shadow color (0-255).
     */
    protected static final int SHADOW_COLOR_VALUE = 32;

    /**
     * The transparency value of the shadow color (0-255).
     */
    protected static final int SHADOW_COLOR_ALPHA = 128;

    static {
        LABEL_FONT =
        /* TODO: Why is this different from the FigEdgeModelElement?
         * Should we not use one of the following? 
         * LookAndFeelMgr.getInstance().getStandardFont();
         * new javax.swing.plaf.metal.DefaultMetalTheme().getUserTextFont(); */
            new javax.swing.plaf.metal.DefaultMetalTheme().getSubTextFont();
        ITALIC_LABEL_FONT =
            new Font(LABEL_FONT.getFamily(), Font.ITALIC, LABEL_FONT.getSize());
        BOLD_LABEL_FONT =
            new Font(LABEL_FONT.getFamily(), Font.BOLD, LABEL_FONT.getSize() + 2);
        BOLD_ITALIC_LABEL_FONT =
            new Font(LABEL_FONT.getFamily(), Font.BOLD | Font.ITALIC, LABEL_FONT.getSize() + 2);

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
        SHADOW_LOOKUP_OP = new LookupOp(new ByteLookupTable(0, data), null);
        SHADOW_CONVOLVE_OP = new ConvolveOp(new Kernel(3, 3, blur));
    }

    /**
     * Used for #buildModifierPopUp().
     */
    protected static final int ROOT = 1;

    /**
     * Used for #buildModifierPopUp().
     */
    protected static final int ABSTRACT = 2;

    /**
     * Used for #buildModifierPopUp().
     */
    protected static final int LEAF = 4;

    /**
     * Used for #buildModifierPopUp().
     */
    protected static final int ACTIVE = 8;

    ////////////////////////////////////////////////////////////////
    // instance variables

    private Fig bigPort;

    /**
     * use getNameFig() and setNameFig() to access the Figs.
     * Use getName() and setName() to just change the text.
     */
    private FigText nameFig;

    /**
     * use getter/setter
     * getStereotypeFig() and setStereoTypeFig() to access the Figs.
     * Use getStereotype() and setStereotype() to change stereotype
     * text.
     */
    private Fig stereotypeFig;

    /**
     * EnclosedFigs are the Figs that are enclosed by this figure. Say that
     * it is a Package then these are the Classes, Interfaces, Packages etc
     * that are on this figure. This is not the same as the figures in the
     * FigGroup that this FigNodeModelElement "is", since these are the
     * figures that make up this high-level primitive figure.
     */
    private Vector enclosedFigs = new Vector();

    /**
     * The figure enclosing this figure.
     */
    private Fig encloser;

    private boolean readyToEdit = true;
    private boolean suppressCalcBounds;
    private static boolean showBoldName;
    private int shadowSize;

    private ItemUID itemUid;

    /**
     * Set the removeFromDiagram to false if this node may not
     * be removed from the diagram.
     */
    private boolean removeFromDiagram = true;

    /**
     * Set this to force a repaint of the shadow.
     * Normally repainting only happens
     * when the outside boundaries change
     * (for performance reasons (?)).
     * In some cases this does not
     * suffice, and you can set this attribute to force the update.
     */
    private boolean forceRepaint;

    /**
     * Flag that indicates if the full namespace path should be shown
     * in front of the name.
     */
    private boolean pathVisible;

    /**
     * If the contains text to be edited by the user.
     */
    private boolean editable = true;

    private Collection listeners = new ArrayList();
    /**
     * The main constructor.
     *
     */
    public FigNodeModelElement() {
        // this rectangle marks the whole modelelement figure; everything
        // is inside it:
        bigPort = new FigRect(10, 10, 0, 0, Color.cyan, Color.cyan);

        nameFig = new FigSingleLineText(10, 10, 90, 21, true);
        nameFig.setLineWidth(1);
        nameFig.setFilled(true);
        nameFig.setText(placeString());
        nameFig.setBotMargin(7); // make space for the clarifier
        nameFig.setRightMargin(4); // margin between text and border
        nameFig.setLeftMargin(4);

        stereotypeFig = new FigStereotypesCompartment(10, 10, 90, 15);

        readyToEdit = false;
        ArgoEventPump.addListener(ArgoEventTypes.ANY_NOTATION_EVENT, this);
        
        Project p = ProjectManager.getManager().getCurrentProject();
        ProjectSettings ps = p.getProjectSettings();

        showBoldName = ps.getShowBoldNamesValue();
        if ((nameFig.getFont().getStyle() & Font.ITALIC) != 0) {
            nameFig.setFont(showBoldName ? BOLD_ITALIC_LABEL_FONT : ITALIC_LABEL_FONT);
        } else {
            nameFig.setFont(showBoldName ? BOLD_LABEL_FONT : LABEL_FONT);
        }
        shadowSize = ps.getDefaultShadowWidthValue();
        /* TODO: how to handle changes in shadowsize 
         * from the project properties? */
    }

    /**
     * Partially construct a new FigNode.  This method creates the
     * name element that holds the name of the model element and adds
     * itself as a listener.
     *
     * @param gm ignored
     * @param node the owning UML element
     */
    public FigNodeModelElement(GraphModel gm, Object node) {
        this();
        setOwner(node);
        nameFig.setText(placeString());
        readyToEdit = false;

        //ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    /**
     * Construct a figure at a specific position for a given model element.
     * 
     * @param element ModelElement associated with figure
     * @param x
     * @param y
     */
    public FigNodeModelElement(Object element, int x, int y) {
        this();
        setOwner(element);
        nameFig.setText(placeString());
        readyToEdit = false;
        setLocation(x, y);
        //ArgoEventPump.addListener(ArgoEvent.ANY_NOTATION_EVENT, this);
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        ArgoEventPump.removeListener(ArgoEventTypes.ANY_NOTATION_EVENT, this);
        super.finalize();
    }

    /**
     * After the base clone method has been called determine which child
     * figs of the clone represent the name, stereotype and port. <p>
     *
     * The clone function is used by Copy/Paste operations.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigNodeModelElement clone = (FigNodeModelElement) super.clone();
        Iterator thisIter = this.getFigs().iterator();
        Iterator cloneIter = clone.getFigs().iterator();
        while (thisIter.hasNext()) {
            Fig thisFig = (Fig) thisIter.next();
            Fig cloneFig = (Fig) cloneIter.next();
            if (thisFig == getBigPort()) {
                clone.setBigPort(cloneFig);
            }
            if (thisFig == nameFig) {
                clone.nameFig = (FigSingleLineText) thisFig;
            }
            if (thisFig == stereotypeFig) {
                clone.stereotypeFig = thisFig;
            }
        }
        return clone;
    }
// TODO: _enclosedFigs, _encloser and _eventSenders may also need to be cloned



    /**
     * Default Reply text to be shown while placing node in diagram.
     * Overrule this when the text is not "new [UMLClassName]".
     *
     * @return the text to be shown while placing node in diagram
     */
    public String placeString() {
        if (Model.getFacade().isAModelElement(getOwner())) {
            String placeString = Model.getFacade().getName(getOwner());
            if (placeString == null) {
                placeString =
                    "new " + Model.getFacade().getUMLClassName(getOwner());
            }
            return placeString;
        }
        return "";
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @param id UID
     */
    public void setItemUID(ItemUID id) {
        itemUid = id;
    }

    /**
     * @return UID
     */
    public ItemUID getItemUID() {
        return itemUid;
    }

    /**
     * Get the Fig that displays the model element name.
     *
     * @return the name Fig
     */
    protected FigText getNameFig() {
        return nameFig;
    }
    
    /**
     * Get the Rectangle in which the model elements name is displayed
     * @return bounding box for name
     */
    public Rectangle getNameBounds() {
        return nameFig.getBounds();
    }
    
    /**
     * Set the Fig that displays the model element name.
     *
     * @param fig the name Fig
     */
    protected void setNameFig(FigText fig) {
        nameFig = fig;
        if (nameFig != null) {
            if ((nameFig.getFont().getStyle() & Font.ITALIC) != 0) {
                nameFig.setFont(showBoldName ? BOLD_ITALIC_LABEL_FONT : ITALIC_LABEL_FONT);
            } else {
                nameFig.setFont(showBoldName ? BOLD_LABEL_FONT : LABEL_FONT);
            }
        }
    }

    /**
     * Get the name of the model element this Fig represents.
     *
     * @return the name of the model element
     */
    public String getName() {
        return nameFig.getText();
    }

    /**
     * Change the name of the model element this Fig represents.
     *
     * @param n the name of the model element
     */
    public void setName(String n) {
        nameFig.setText(n);
    }

    /**
     * @see org.tigris.gef.ui.PopupGenerator#getPopUpActions(java.awt.event.MouseEvent)
     */
    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = super.getPopUpActions(me);

        // popupAddOffset should be equal to the number of items added here:
        popUpActions.addElement(new JSeparator());
        popupAddOffset = 1;
        if (removeFromDiagram) {
            popUpActions.addElement(
                    ProjectBrowser.getInstance().getRemoveFromDiagramAction());
            popupAddOffset++;
        }
        popUpActions.addElement(new ActionDeleteModelElements());
        popupAddOffset++;

        /* Check if multiple items are selected: */
        if (TargetManager.getInstance().getTargets().size() == 1) {
            ToDoList list = Designer.theDesigner().getToDoList();
            Vector items =
                    (Vector) list.elementsForOffender(getOwner()).clone();
            if (items != null && items.size() > 0) {
                ArgoJMenu critiques = new ArgoJMenu("menu.popup.critiques");
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

            // Add stereotypes submenu
            Action[] stereoActions =
                StereotypeUtility.getApplyStereotypeActions(getOwner());
            if (stereoActions != null) {
                popUpActions.insertElementAt(new JSeparator(), 0);
                ArgoJMenu stereotypes =
                    new ArgoJMenu("menu.popup.apply-stereotypes");
                for (int i = 0; i < stereoActions.length; ++i) {
                    stereotypes.addCheckItem(stereoActions[i]);
                }
                popUpActions.insertElementAt(stereotypes, 0);
            }
        }

        return popUpActions;
    }

    /**
     * @return the pop-up menu item for Visibility
     */
    protected Object buildVisibilityPopUp() {
        ArgoJMenu visibilityMenu = new ArgoJMenu("menu.popup.visibility");

        visibilityMenu.addRadioItem(new ActionVisibilityPublic(getOwner()));
        visibilityMenu.addRadioItem(new ActionVisibilityPrivate(getOwner()));
        visibilityMenu.addRadioItem(new ActionVisibilityProtected(getOwner()));
        visibilityMenu.addRadioItem(new ActionVisibilityPackage(getOwner()));
        
        return visibilityMenu;
    }

    /**
     * Build a pop-up menu item for the various modifiers.<p>
     *
     * This function is designed to be easily extendable with new items.
     *
     * @param items bitwise OR of the items: ROOT, ABSTRACT, LEAF, ACTIVE.
     * @return the menu item
     */
    protected Object buildModifierPopUp(int items) {
        ArgoJMenu modifierMenu = new ArgoJMenu("menu.popup.modifiers");

        if ((items & ABSTRACT) > 0) {
            modifierMenu.addCheckItem(new ActionModifierAbstract(getOwner()));
	}
        if ((items & LEAF) > 0) {
            modifierMenu.addCheckItem(new ActionModifierLeaf(getOwner()));
	}
        if ((items & ROOT) > 0) {
            modifierMenu.addCheckItem(new ActionModifierRoot(getOwner()));
	}
        if ((items & ACTIVE) > 0) {
            modifierMenu.addCheckItem(new ActionModifierActive(getOwner()));
	}

        return modifierMenu;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getEnclosingFig()
     */
    public Fig getEnclosingFig() {
        return encloser;
    }

    /**
     * Updates the modelelement container if the fig is moved in or
     * out another fig. If this fig doesn't have an enclosing fig
     * anymore, the namespace of the diagram will be the owning
     * modelelement. If this fig is moved inside another
     * FigNodeModelElement the owner of that fignodemodelelement will
     * be the owning modelelement.
     * @see Fig#setEnclosingFig(Fig)
     */
    public void setEnclosingFig(Fig newEncloser) {
	Fig oldEncloser = encloser;
	super.setEnclosingFig(newEncloser);
	if (newEncloser != oldEncloser) {
	    Object owningModelelement = null;
	    if (newEncloser == null && isVisible()) {
	        // If we are not visible most likely we're being deleted.

		// moved outside another fig onto the diagram canvas
		Project currentProject =
		    ProjectManager.getManager().getCurrentProject();
                ArgoDiagram diagram = currentProject.getActiveDiagram();
                // TODO: Who said this was about the active diagram?
                if (diagram instanceof UMLDiagram
			&& ((UMLDiagram) diagram).getNamespace() != null) {
                    owningModelelement = ((UMLDiagram) diagram).getNamespace();
                } else {
                    owningModelelement = currentProject.getRoot();
                }
	    } else if (newEncloser != null
                    && Model.getFacade()
                            .isAModelElement(newEncloser.getOwner())) {
                owningModelelement = newEncloser.getOwner();
            }
            if (owningModelelement != null
		&& getOwner() != null
		&& (!Model.getModelManagementHelper()
		    .isCyclicOwnership(owningModelelement, getOwner()))
		&& ((Model.getCoreHelper()
			.isValidNamespace(getOwner(),
					  owningModelelement)))) {
                Model.getCoreHelper().setModelElementContainer(getOwner(),
						     owningModelelement);
                /* TODO: move the associations to the correct owner (namespace)
                 * i.e. issue 2151
                 */
            }
        }
	if (newEncloser != encloser) {
	    if (encloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) encloser).removeEnclosedFig(this);
            }
	    if (newEncloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) newEncloser).addEnclosedFig(this);
            }
	}
        encloser = newEncloser;
    }

    /**
     * @param fig The fig to be added
     */
    public void addEnclosedFig(Fig fig) {
        enclosedFigs.add(fig);
    }

    /**
     * @param fig The Fig to be removed
     */
    public void removeEnclosedFig(Fig fig) {
        enclosedFigs.remove(fig);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getEnclosedFigs()
     */
    public Vector getEnclosedFigs() {
        return enclosedFigs;
    }

    /**
     * Update the order of this fig and the order of the
     * figs that are inside of this fig.
     *
     * @param figures in the new order
     */
    public void elementOrdering(Vector figures) {
        int size = figures.size();
        getLayer().bringToFront(this);
        if (size > 0) {
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

    /**
     * @see org.tigris.gef.presentation.Fig#makeSelection()
     */
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
        if (shadowSize > 0
	        && g instanceof Graphics2D) {
            int width = getWidth();
            int height = getHeight();
            int x = getX();
            int y = getY();

            /* Only create a new shadow image if figure size has changed.
             * Which does not catch all cases:
             * consider show/hide toggle of a stereotype on a package:
             * in this case the total size remains, but the notch
             * at the corner increases/decreases.
             * Hence also check the "forceRepaint" attribute.
             */
            if (width != cachedWidth
                    || height != cachedHeight
                    || forceRepaint) {
                forceRepaint = false;

                cachedWidth = width;
                cachedHeight = height;

                BufferedImage img =
		    new BufferedImage(width + 100,
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
                shadowImage =
		    SHADOW_CONVOLVE_OP.filter(
			    SHADOW_LOOKUP_OP.filter(img, null), null);
            }

            // Paint shadow image onto canvas
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(
                shadowImage,
                null,
                x + shadowSize - 50,
                y + shadowSize - 50);
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
     * @param g the graphics device
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

    /**
     * @param x the x of the hit
     * @param y the y of the hit
     * @return the todo item of which the clarifier has been hit
     */
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

    /**
     * @see org.tigris.gef.presentation.Fig#getTipString(java.awt.event.MouseEvent)
     */
    public String getTipString(MouseEvent me) {
        ToDoItem item = hitClarifier(me.getX(), me.getY());
        String tip = "";
        if (item != null
            && Globals.curEditor().getSelectionManager().containsFig(this)) {
            tip = item.getHeadline() + " ";
        } else if (getOwner() != null) {
            tip = Model.getFacade().getTipString(getOwner());
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

    /**
     * @see java.beans.VetoableChangeListener#vetoableChange(java.beans.PropertyChangeEvent)
     */
    public void vetoableChange(PropertyChangeEvent pce) {
        LOG.debug("in vetoableChange");
        Object src = pce.getSource();
        if (src == getOwner()) {
            DelayedChangeNotify delayedNotify =
                new DelayedChangeNotify(this, pce);
            SwingUtilities.invokeLater(delayedNotify);
        } else {
            LOG.debug("FigNodeModelElement got vetoableChange"
		      + " from non-owner:"
		      + src);
        }
    }

    /**
     * @see org.argouml.kernel.DelayedVChangeListener#delayedVetoableChange(java.beans.PropertyChangeEvent)
     */
    public void delayedVetoableChange(PropertyChangeEvent pce) {
        LOG.debug("in delayedVetoableChange");
        // update any text, colors, fonts, etc.
        renderingChanged();
        endTrans();
    }

    /**
     * set some new bounds.
     */
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

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent pve) {
        Object src = pve.getSource();
        String pName = pve.getPropertyName();
        if (pve instanceof DeleteInstanceEvent && src == getOwner()) {
            removeFromDiagram();
            return;
        }
        // We handle and consume editing events
        if (pName.equals("editing")
                && Boolean.FALSE.equals(pve.getNewValue())) {
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
                LOG.error("could not parse the text entered. "
			  + "PropertyVetoException",
			  ex);
            }
        } else if (pName.equals("editing")
                        && Boolean.TRUE.equals(pve.getNewValue())) {
            textEditStarted((FigText) src);
        } else {
            super.propertyChange(pve);
        }
        if (Model.getFacade().isAModelElement(src)) {
            /* If the source of the event is an UML object,
             * e.g. the owner of this Fig (but not always only the owner
             * is shown, e.g. for a class, also its attributes are shown),
             * then the UML model has been changed.
             */
            // We catch the exception here so it is handled for all subclasses
            try {
                modelChanged(pve);
            } catch (Exception e) {
                /* need to catch javax.jmi.reflect.InvalidObjectException */
                LOG.debug("modelChanged method accessed deleted element ", e);
            }
        }
    }

    /**
     * This method is called when the user doubleclicked on the text field,
     * and starts editing. Subclasses should overrule this field to e.g.
     * supply help to the user about the used format. <p>
     *
     * It is also possible to alter the text to be edited
     * already here, e.g. by adding the stereotype in front of the name,
     * by adding ["fullyHandleStereotypes", true] in the arguments 
     * HashMap of the NotationProvider.toString() function, 
     * but that seems not user-friendly. See issue 3838.
     *
     * @param ft the FigText that will be edited and contains the start-text
     */
    protected void textEditStarted(FigText ft) {
        if (ft == getNameFig()) {
            showHelp(notationProviderName.getParsingHelp());
            ft.setText(notationProviderName.toString(getOwner(), npArguments));
        }
    }

    /**
     * Utility function to localize the given string with help text,
     * and show it in the status bar of the ArgoUML window.
     * This function is used in favour of the inline call
     * to enable later improvements; e.g. it would be possible to
     * show a help-balloon. TODO: Work this out.
     * One matter to possibly improve: show multiple lines.
     *
     * @param s the given string to be localized and shown
     */
    protected void showHelp(String s) {
        ProjectBrowser.getInstance().getStatusBar().showStatus(
                Translator.localize(s));
    }

    /**
     * This method is called after the user finishes editing a text
     * field that is in the FigNodeModelElement.  Determine which
     * field and update the model.  This class handles the name,
     * and the stereotype,
     * subclasses should override to handle other text elements.
     *
     * @param ft the FigText that has been edited and contains the new text
     * @throws PropertyVetoException thrown when new text represents
     * an unacceptable value
     */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        if (ft == nameFig) {
            if (getOwner() == null) {
                return;
            }
            notationProviderName.parse(getOwner(), ft.getText());
            ft.setText(notationProviderName.toString(getOwner(), npArguments));
        }
    }

    ////////////////////////////////////////////////////////////////
    // event handlers - MouseListener implementation

    /**
     * If the user double clicks on any part of this FigNode, pass it
     * down to one of the internal Figs. This allows the user to
     * initiate direct text editing.
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        if (!readyToEdit) {
            if (Model.getFacade().isAModelElement(getOwner())) {
                Model.getCoreHelper().setName(getOwner(), "");
                readyToEdit = true;
            } else {
                LOG.debug("not ready to edit name");
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
            if (f instanceof MouseListener && f.isVisible()) {
                ((MouseListener) f).mouseClicked(me);
            } else if (f instanceof FigGroup && f.isVisible()) {
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
    }

    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent ke) {
        if (ke.isConsumed() || getOwner() == null) {
            return;
        }
        nameFig.keyPressed(ke);
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent ke) {
        if (ke.isConsumed() || getOwner() == null) {
            return;
        }
        nameFig.keyReleased(ke);
    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent ke) {
        if (!editable) {
            return;
        }
        if (!readyToEdit) {
            if (Model.getFacade().isAModelElement(getOwner())) {
                Model.getCoreHelper().setName(getOwner(), "");
                readyToEdit = true;
            } else {
                LOG.debug("not ready to edit name");
                return;
            }
        }
        if (ke.isConsumed() || getOwner() == null) {
            return;
        }
        nameFig.keyTyped(ke);
    }

    ////////////////////////////////////////////////////////////////
    // internal methods

    /**
     * This is called after any part of the UML MModelElement has
     * changed. This method automatically updates the name FigText.
     * Subclasses should override and update other parts.
     *
     * @param mee the ModelElementEvent that caused the change
     */
    protected void modelChanged(PropertyChangeEvent mee) {
        if (mee == null) {
            throw new IllegalArgumentException("event may never be null "
                           + "with modelchanged");
        }
        Object owner = getOwner();
        // If the element has been deleted, the caller will
        // receive an InvalidElementException that it must handle.
        if (owner == null) {
            return;
        }
        if ("name".equals(mee.getPropertyName())
                && mee.getSource() == owner) {
            updateNameText();
            damage();
        }
        if ((mee.getSource() == owner
                && mee.getPropertyName().equals("stereotype"))) {
            if (mee.getOldValue() != null) {
                removeElementListener(mee.getOldValue());
            }
            if (mee.getNewValue() != null) {
                addElementListener(mee.getNewValue(), "name");
            }
            updateStereotypeText();
            damage();
        }
    }


    /**
     * Create a new feature in the owner fig.
     *
     * must be overridden to make sense
     * (I didn't want to make it abstract because it might not be required)
     *
     * @param fg The fig group to which this applies
     * @param me The input event that triggered us. In the current
     *            implementation a mouse double click.
     */
    protected void createFeatureIn(FigGroup fg, InputEvent me) {

    }

    /**
     * @param o the given object
     * @return true if one of my figs has the given object as owner
     */
    protected boolean isPartlyOwner(Object o) {
        if (o == null || o == getOwner()) {
            return true;
        }
        Iterator it = getFigs().iterator();
        while (it.hasNext()) {
            Fig fig = (Fig) it.next();
            if (isPartlyOwner(fig, o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param fig the given fig (may be a group)
     * @param o the given object
     * @return true if one of the given figs has the given object as owner
     */
    protected boolean isPartlyOwner(Fig fig, Object o) {
        if (o == null) {
            return false;
        }
        if (o == fig.getOwner()) {
            return true;
        }
        if (fig instanceof FigGroup) {
            Iterator it = ((FigGroup) fig).getFigs().iterator();
            while (it.hasNext()) {
                Fig fig2 = (Fig) it.next();
                if (isPartlyOwner(fig2, o)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#deleteFromModel()
     */
    public void deleteFromModel() {
        Object own = getOwner();
        if (own != null) {
            ProjectManager.getManager().getCurrentProject().moveToTrash(own);
        }
        Iterator it = getFigs().iterator();
        while (it.hasNext()) {
            ((Fig) it.next()).deleteFromModel();
        }
        super.deleteFromModel();
    }

    /**
     * This method should only be called once for any one Fig instance that
     * represents a model element (ie not for a FigEdgeNote).
     * It is called either by the constructor that takes an model element as an
     * argument or it is called by PGMLStackParser after it has created the Fig
     * by use of the empty constructor.
     * The assigned model element (owner) must not change during the lifetime
     * of the Fig.
     * TODO: It is planned to refactor so that there is only one Fig
     * constructor. When this is achieved this method can refactored out.
     * 
     * @param owner the model element that this Fig represents.
     * @throws IllegalArgumentException if the owner given is not a model
     * element
     * @see org.tigris.gef.presentation.Fig#setOwner(java.lang.Object)
     */
    public void setOwner(Object owner) {
        if (owner == null) {
            throw new IllegalArgumentException("An owner must be supplied");
        }
        if (getOwner() != null) {
            throw new IllegalStateException(
                    "The owner cannot be changed once set");
        }
        if (!Model.getFacade().isAModelElement(owner)) {
            throw new IllegalArgumentException(
                    "The owner must be a model element - got a "
                    + owner.getClass().getName());
        }
        super.setOwner(owner);
        initNotationProviders(owner);
        readyToEdit = true;
        renderingChanged();
        updateBounds();
        bindPort(owner, bigPort);
        updateListeners(null, owner);
    }

    /**
     * Create the NotationProviders.
     * 
     * @param own the current owner
     */
    protected void initNotationProviders(Object own) {
        if (Model.getFacade().isAModelElement(own)) {
            notationProviderName =
                NotationProviderFactory2.getInstance().getNotationProvider(
                        NotationProviderFactory2.TYPE_NAME, own);
            npArguments.put("pathVisible", Boolean.valueOf(isPathVisible()));
        }
    }

    /**
     * Updates the text of the sterotype FigText. Override in subclasses to get
     * wanted behaviour.
     */
    protected void updateStereotypeText() {
        if (getOwner() == null) {
            LOG.warn("Owner of [" + this.toString() + "/" + this.getClass()
                    + "] is null.");
            LOG.warn("I return...");
            return;
        }

        Object modelElement = getOwner();
        stereotypeFig.setOwner(modelElement);
        if (modelElement != null) {
            ((FigStereotypesCompartment) stereotypeFig).populate();
        }
    }

    /**
     * Updates the text of the name FigText.
     */
    protected void updateNameText() {
        if (readyToEdit) {
            if (getOwner() == null) {
                return;
            }
            if (notationProviderName != null) {
                nameFig.setText(notationProviderName.toString(
                        getOwner(), npArguments));
                Project p = ProjectManager.getManager().getCurrentProject();
                ProjectSettings ps = p.getProjectSettings();
                showBoldName = ps.getShowBoldNamesValue();
                if ((nameFig.getFont().getStyle() & Font.ITALIC) != 0) {
                    nameFig.setFont(showBoldName ? BOLD_ITALIC_LABEL_FONT : ITALIC_LABEL_FONT);
                } else {
                    nameFig.setFont(showBoldName ? BOLD_LABEL_FONT : LABEL_FONT);
                }
                updateBounds();
            }
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.PathContainer#isPathVisible()
     */
    public boolean isPathVisible() {
        return pathVisible;
    }

    /**
     * @see org.argouml.uml.diagram.ui.PathContainer#setPathVisible(boolean)
     */
    public void setPathVisible(boolean visible) {
        if (pathVisible == visible) {
            return;
        }
        MutableGraphSupport.enableSaveAction();
        pathVisible = visible;
        if (notationProviderName != null) {
            npArguments.put("pathVisible", Boolean.valueOf(visible));
        }
        if (readyToEdit) {
            renderingChanged();
            damage();
        }
    }

    /**
     * USED BY PGML.tee.
     * @return the class name and bounds together with compartment
     * visibility.
     */
    public String classNameAndBounds() {
        return getClass().getName()
            + "[" + getX() + ", " + getY() + ", "
            + getWidth() + ", " + getHeight() + "]"
            + "pathVisible=" + isPathVisible() + ";";
    }

    /**
     * Implementations of this method should register/unregister the fig for all
     * (model)events. For FigNodeModelElement only the fig itself is registered
     * as listening to events fired by the owner itself. But for, for example,
     * FigClass the fig must also register for events fired by the operations
     * and attributes of the owner. <p>
     * 
     * An explanation of the original 
     * purpose of this method is given in issue 1321.<p>
     * 
     * This function is used in UMLDiagram, which removes all listeners 
     * to all Figs when a diagram is not displayed, and restore them
     * when it becomes visible again. <p>
     * 
     * In this case, it is imperative that indeed ALL listeners are 
     * updated, since they are ALL removed by 
     * the call to removeElementListener. <p>
     * 
     * Additionally, this function may be used by the modelChanged()
     * function.<p>
     * 
     * In this case, it is also imperative that 
     * all listeners get removed / added.
     * 
     * @param newOwner null, or the owner of this. 
     *          The former means that listeners have to be removed, 
     *          the latter that they have to be set.
     *          TODO: Should this not be boolean, to clarify?
     * @param oldOwner the previous owner
     */
    protected void updateListeners(Object oldOwner, Object newOwner) {
        if (oldOwner == newOwner) {
            LOG.warn("Listners being added and removed from the same owner");
        }
        if (oldOwner != null) {
            removeElementListener(oldOwner);
        }
        if (newOwner != null) {
            addElementListener(newOwner);
        }

    }

    /**
     * @see org.argouml.application.events.ArgoNotationEventListener#notationChanged(org.argouml.application.events.ArgoNotationEvent)
     */
    public void notationChanged(ArgoNotationEvent event) {
        if (getOwner() == null) return;
        initNotationProviders(getOwner());
        try {
            renderingChanged();
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    /**
     * @see org.argouml.application.events.ArgoNotationEventListener#notationAdded(org.argouml.application.events.ArgoNotationEvent)
     */
    public void notationAdded(ArgoNotationEvent event) {
    }

    /**
     * @see org.argouml.application.events.ArgoNotationEventListener#notationRemoved(org.argouml.application.events.ArgoNotationEvent)
     */
    public void notationRemoved(ArgoNotationEvent event) {
    }

    /**
     * @see org.argouml.application.events.ArgoNotationEventListener#notationProviderAdded(org.argouml.application.events.ArgoNotationEvent)
     */
    public void notationProviderAdded(ArgoNotationEvent event) {
    }

    /**
     * @see org.argouml.application.events.ArgoNotationEventListener#notationProviderRemoved(org.argouml.application.events.ArgoNotationEvent)
     */
    public void notationProviderRemoved(ArgoNotationEvent event) {
    }

    /**
     * Rerenders the fig. <p>
     * 
     * This functionality was originally
     * the functionality of modelChanged but modelChanged takes the
     * event now into account.
     */
    public void renderingChanged() {
        updateNameText();
        updateStereotypeText();
        updateBounds();
        damage();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#calcBounds()
     */
    public void calcBounds() {
        if (suppressCalcBounds) {
            return;
        }
        super.calcBounds();
    }

    /**
     * The setter for checkSize.
     *
     * @param flag the new value
     */
    public void enableSizeChecking(boolean flag) {
        checkSize = flag;
    }

    /**
     * @param size the new shadow size
     * TODO: Move the shadow stuff into GEF
     */
    public void setShadowSize(int size) {
        if (size == shadowSize) {
            return;
        }
        MutableGraphSupport.enableSaveAction();
        shadowSize = size;
    }

    /**
     * @deprecated do not use. Delete as soon as its single reference is gone.
     * @param size
     */
    protected void setShadowSizeFriend(int size) {
        if (size == shadowSize) {
            return;
        }
        shadowSize = size;
    }

    /**
     * @return the current shadow size
     */
    public int getShadowSize() {
        return shadowSize;
    }

    /**
     * Necessary since GEF contains some errors regarding the hit subject.
     * @see org.tigris.gef.presentation.Fig#hit(Rectangle)
     */
    public boolean hit(Rectangle r) {
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        if (_filled) {
            return cornersHit > 0 || intersects(r);
        }
        return (cornersHit > 0 && cornersHit < 4) || intersects(r);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#removeFromDiagram()
     */
    public final void removeFromDiagram() {
        Fig delegate = getRemoveDelegate();
        if (delegate instanceof FigNodeModelElement) {
            ((FigNodeModelElement) delegate).removeFromDiagramImpl();
        } else if (delegate instanceof FigEdgeModelElement) {
            ((FigEdgeModelElement) delegate).removeFromDiagramImpl();
        } else if (delegate != null) {
            removeFromDiagramImpl();
        }
    }
    
    /**
     * Subclasses should override this to redirect a remove request from
     * one Fig to another.
     * e.g. FigClassAssociationClass uses this to delegate the remove to
     * its attached FigAssociationClass.
     * @return the fig that handles the remove request
     */
    protected Fig getRemoveDelegate() {
        return this;
    }
    
    protected void removeFromDiagramImpl() {
        ArgoEventPump.removeListener(this);
        removeAllElementListeners();
        shadowSize = 0;
        super.removeFromDiagram();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#postLoad()
     */
    public void postLoad() {
        ArgoEventPump.removeListener(this);
        ArgoEventPump.addListener(this);
        Iterator it = getFigs().iterator();
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
     * Get the Fig containing the stereotype.
     *
     * @return the stereotype Fig
     */
    protected Fig getStereotypeFig() {
        return stereotypeFig;
    }

    /**
     * @return Returns the lABEL_FONT.
     */
    public static Font getLabelFont() {
        return LABEL_FONT;
    }

    /**
     * @return Returns the iTALIC_LABEL_FONT.
     */
    public static Font getItalicLabelFont() {
        return ITALIC_LABEL_FONT;
    }

    /**
     * @param bp The _bigPort to set.
     */
    protected void setBigPort(Fig bp) {
        this.bigPort = bp;
    }

    /**
     * @return the fig which is the port where edges connect to this node
     */
    public Fig getBigPort() {
        return bigPort;
    }

    /**
     * @return Returns the checkSize.
     */
    protected boolean isCheckSize() {
        return checkSize;
    }

    /**
     * @see org.tigris.gef.presentation.FigNode#isDragConnectable()
     */
    public boolean isDragConnectable() {
        return false;
    }

    /**
     * @param e The _encloser to set.
     */
    protected void setEncloser(Fig e) {
        this.encloser = e;
    }

    /**
     * @return Returns the _encloser.
     */
    protected Fig getEncloser() {
        return encloser;
    }
    /**
     * @return Returns the ReadyToEdit.
     */
    protected boolean isReadyToEdit() {
        return readyToEdit;
    }

    /**
     * @param v if ready to edit
     */
    protected void setReadyToEdit(boolean v) {
        readyToEdit = v;
    }

    /**
     * @param scb The suppressCalcBounds to set.
     */
    protected void setSuppressCalcBounds(boolean scb) {
        this.suppressCalcBounds = scb;
    }
    
    public void setVisible(boolean visible) {
        if (!visible && !invisibleAllowed) {
            throw new IllegalArgumentException(
                    "Visibility of a FigNode should never be false");
        }
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setLayer(org.tigris.gef.base.Layer)
     */
    public void setLayer(Layer lay) {
        super.setLayer(lay);
    }

    /**
     * To redraw each element correctly when changing its location
     * with X and U additions.
     *
     * @param xInc the increment in the x direction
     * @param yInc the increment in the y direction
     */
    public void displace (int xInc, int yInc) {
        Vector figsVector;
        Rectangle rFig = getBounds();
        setLocation(rFig.x + xInc, rFig.y + yInc);
        figsVector = ((Vector) getEnclosedFigs().clone());
        if (!figsVector.isEmpty()) {
            for (int i = 0; i < figsVector.size(); i++) {
                ((FigNodeModelElement) figsVector.elementAt(i))
                            .displace(xInc, yInc);
            }
        }
    }


    /**
     * @param allowed true if the function RemoveFromDiagram is allowed
     */
    protected void allowRemoveFromDiagram(boolean allowed) {
        this.removeFromDiagram = allowed;
    }

    /**
     * Force painting the shadow.
     */
    public void forceRepaintShadow() {
        forceRepaint = true;
    }

    public void setDiElement(DiElement element) {
        this.diElement = element;
    }

    public DiElement getDiElement() {
        return diElement;
    }

    /**
     * @return Returns the popupAddOffset.
     */
    protected static int getPopupAddOffset() {
        return popupAddOffset;
    }

    /**
     * Determine if this node can be edited.
     * @return editable state
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * By default a node is directly editable by simply selecting
     * that node and starting to type.
     * Should a subclass of FigNodeModelElement not desire this behaviour
     * then it should call setEditable(false) in its constructor.
     * 
     * @param canEdit new state, false = editing disabled.
     */
    protected void setEditable(boolean canEdit) {
        this.editable = canEdit;
    }

    /**
     * Add an element listener and remember the registration.
     * 
     * @param element
     *            element to listen for changes on
     * @see org.argouml.model.ModelEventPump#addModelEventListener(PropertyChangeListener, Object, String)
     */
    protected void addElementListener(Object element) {
        listeners.add(new Object[] {element, null});
        Model.getPump().addModelEventListener(this, element);
    }
    
    /**
     * Add a listener for a given property name and remember the registration.
     * 
     * @param element
     *            element to listen for changes on
     * @param property
     *            name of property to listen for changes of
     * @see org.argouml.model.ModelEventPump#addModelEventListener(PropertyChangeListener,
     *      Object, String)
     */
    protected void addElementListener(Object element, String property) {
        listeners.add(new Object[] {element, property});
        Model.getPump().addModelEventListener(this, element, property);
    }

    /**
     * Add a listener for an array of property names and remember the
     * registration.
     * 
     * @param element
     *            element to listen for changes on
     * @param property
     *            array of property names (Strings) to listen for changes of
     * @see org.argouml.model.ModelEventPump#addModelEventListener(PropertyChangeListener,
     *      Object, String)
     */
    protected void addElementListener(Object element, String[] property) {
        listeners.add(new Object[] {element, property});
        Model.getPump().addModelEventListener(this, element, property);
    }
    
    /**
     * Remove an element listener and remember the registration.
     * 
     * @param element
     *            element to listen for changes on
     * @see org.argouml.model.ModelEventPump#addModelEventListener(PropertyChangeListener, Object, String)
     */
    protected void removeElementListener(Object element) {
        listeners.remove(new Object[] {element, null});
        Model.getPump().removeModelEventListener(this, element);
    }
    
    /**
     * Unregister all listeners registered through addElementListener
     * @see #addElementListener(Object, String)
     */
    protected void removeAllElementListeners() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            Object[] l = (Object[]) iter.next();
            Object property = l[1];
            if (property == null) {
                Model.getPump().removeModelEventListener(this, l[0]);
            } else if (property instanceof String[]) {
                Model.getPump().removeModelEventListener(this, l[0],
                        (String[]) property);
            } else if (property instanceof String) {
                Model.getPump().removeModelEventListener(this, l[0],
                        (String) property);
            } else {
                throw new RuntimeException(
                        "Internal error in removeAllElementListeners");
            }
        }
        listeners.clear();
    }



} /* end class FigNodeModelElement */
