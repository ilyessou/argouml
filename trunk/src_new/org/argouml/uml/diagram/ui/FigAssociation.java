// $Id$
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

package org.argouml.uml.diagram.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.application.api.Notation;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.ui.ArgoJMenu;
import org.argouml.uml.diagram.ui.ActionAggregation;
import org.argouml.uml.diagram.ui.ActionMultiplicity;
import org.argouml.uml.diagram.ui.ActionNavigability;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.PathConvPercent;
import org.tigris.gef.base.PathConvPercentPlusConst;
import org.tigris.gef.presentation.ArrowHead;
import org.tigris.gef.presentation.ArrowHeadComposite;
import org.tigris.gef.presentation.ArrowHeadDiamond;
import org.tigris.gef.presentation.ArrowHeadGreater;
import org.tigris.gef.presentation.ArrowHeadNone;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigText;

import ru.novosoft.uml.MElementEvent;

public class FigAssociation extends FigEdgeModelElement {
    
   

    // TODO: should be part of some preferences object
    public static boolean SUPPRESS_BIDIRECTIONAL_ARROWS = true;

    /**
     * Group for the FigTexts concerning the source association end
     */
    protected FigTextGroup _srcGroup = new FigTextGroup();
    /**
     * Group for the FigTexts concerning the dest association end
     */
    protected FigTextGroup _destGroup = new FigTextGroup();
    /**
     * Group for the FigTexts concerning the name and stereotype of the 
     * association itself.
     */
    protected FigTextGroup _middleGroup = new FigTextGroup();
    
    protected FigText _srcMult, _srcRole;
    protected FigText _destMult, _destRole;
    protected FigText _srcOrdering, _destOrdering;

    protected ArrowHead sourceArrowHead, destArrowHead;



    /**
     * Don't call this constructor directly. It is public since this
     * is necessary for loading. Use the FigAssociation(Object, Layer)
     * constructor instead!
     */
    public FigAssociation() {
        super();
    
        // lets use groups to construct the different text sections at
        // the association
        _middleGroup.addFig(_name);
        _middleGroup.addFig(_stereo);
        addPathItem(_middleGroup, new PathConvPercent(this, 50, 25));
    
        _srcMult = new FigText(10, 10, 90, 20);
        _srcMult.setFont(LABEL_FONT);
        _srcMult.setTextColor(Color.black);
        _srcMult.setTextFilled(false);
        _srcMult.setFilled(false);
        _srcMult.setLineWidth(0);
        _srcMult.setJustification(FigText.JUSTIFY_CENTER);

        _srcRole = new FigText(10, 10, 90, 20);
        _srcRole.setFont(LABEL_FONT);
        _srcRole.setTextColor(Color.black);
        _srcRole.setTextFilled(false);
        _srcRole.setFilled(false);
        _srcRole.setLineWidth(0);
        _srcRole.setJustification(FigText.JUSTIFY_CENTER);

        _srcOrdering = new FigText(10, 10, 90, 20);
        _srcOrdering.setFont(LABEL_FONT);
        _srcOrdering.setTextColor(Color.black);
        _srcOrdering.setTextFilled(false);
        _srcOrdering.setFilled(false);
        _srcOrdering.setLineWidth(0);
        _srcOrdering.setJustification(FigText.JUSTIFY_CENTER);

        _srcGroup.addFig(_srcRole);
        _srcGroup.addFig(_srcOrdering);
        addPathItem(_srcMult, new PathConvPercentPlusConst(this, 0, 15, 15));
        addPathItem(_srcGroup, new PathConvPercentPlusConst(this, 0, 35, -15));
   
        _destMult = new FigText(10, 10, 90, 20);
        _destMult.setFont(LABEL_FONT);
        _destMult.setTextColor(Color.black);
        _destMult.setTextFilled(false);
        _destMult.setFilled(false);
        _destMult.setLineWidth(0);
        _destMult.setJustification(FigText.JUSTIFY_CENTER);

        _destRole = new FigText(0, 0, 90, 20);
        _destRole.setFont(LABEL_FONT);
        _destRole.setTextColor(Color.black);
        _destRole.setTextFilled(false);
        _destRole.setFilled(false);
        _destRole.setLineWidth(0);
        _destRole.setJustification(FigText.JUSTIFY_CENTER);

        _destOrdering = new FigText(0, 0, 90, 20);
        _destOrdering.setFont(LABEL_FONT);
        _destOrdering.setTextColor(Color.black);
        _destOrdering.setTextFilled(false);
        _destOrdering.setFilled(false);
        _destOrdering.setLineWidth(0);
        _destOrdering.setJustification(FigText.JUSTIFY_CENTER);

        _destGroup.addFig(_destRole);
        _destGroup.addFig(_destOrdering);
        addPathItem(_destMult,
		    new PathConvPercentPlusConst(this, 100, -15, 15));
        addPathItem(_destGroup,
		    new PathConvPercentPlusConst(this, 100, -35, -15));
    
        setBetweenNearestPoints(true);
        // next line necessary for loading
        setLayer(ProjectManager.getManager().getCurrentProject().getActiveDiagram().getLayer());
    }

    public FigAssociation(Object edge, Layer lay) {
	this();
	setLayer(lay);
	setOwner(edge);
    }

    public void setOwner(Object association) {
	Object oldOwner = getOwner();
	super.setOwner(association);

	if (org.argouml.model.ModelFacade.isAAssociation(association)) {
	    Collection connections = ModelFacade.getConnections(association);
	    for (int i = 0; i < connections.size(); i++) {
		Object assEnd = (connections.toArray())[i];
		UmlModelEventPump.getPump().removeModelEventListener(this, assEnd);
		UmlModelEventPump.getPump().addModelEventListener(this, assEnd);
	    }
	    UmlModelEventPump.getPump().removeModelEventListener(this, association);
	    UmlModelEventPump.getPump().addModelEventListener(this, association);
	    Object assEnd1 = (connections.toArray())[0];
	    Object assEnd2 = (connections.toArray())[1];
	    FigNode destNode =
		(FigNode) getLayer().presentationFor(ModelFacade.getType(assEnd1));
	    FigNode srcNode =
		(FigNode) getLayer().presentationFor(ModelFacade.getType(assEnd1));
	    if (destNode != null) {
		setDestFigNode(destNode);
		setDestPortFig(destNode);
	    }
	    if (srcNode != null) {
		setSourceFigNode(srcNode); 
		setSourcePortFig(srcNode);  
	    }
	}
   
	modelChanged(null);
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    protected void textEdited(FigText ft) throws PropertyVetoException {
	
        if (getOwner() == null) return;
	super.textEdited(ft);

	Collection conn = ModelFacade.getConnections(getOwner());
	if (conn == null || conn.size() == 0) return;

	if (ft == _srcRole) {
	    Object srcAE = (conn.toArray())[0];
	    ModelFacade.setName(srcAE, _srcRole.getText());
	}
	if (ft == _destRole) {
	    Object destAE = (conn.toArray())[1];
	    ModelFacade.setName(destAE, _destRole.getText());
	}
	// TODO: parse multiplicities
    }
  
    private void updateEnd(FigText multiToUpdate, FigText roleToUpdate,
			   FigText orderingToUpdate,
			   Object end) {
                               
        if(!ModelFacade.isAAssociationEnd(end))
            throw new IllegalArgumentException();
        
	Object multi = ModelFacade.getMultiplicity(end);
	String name = ModelFacade.getName(end);
	Object order = ModelFacade.getOrdering(end);
        Object stereo = null;
        if (ModelFacade.getStereotypes(end).size() > 0) {
            stereo = ModelFacade.getStereotypes(end).iterator().next();
        }
    
	multiToUpdate.setText(Notation.generate(this, multi));
	orderingToUpdate.setText(getOrderingName(order));
	if (stereo != null) {
	    roleToUpdate.setText(Notation.generate(this, stereo)
				 + " "
				 + Notation.generate(this, name));
	} else
	    roleToUpdate.setText(Notation.generate(this, name));
    }

    protected void modelChanged(MElementEvent e) {
	super.modelChanged(e);
	Object associationEnd = getOwner();//MAssociation
	if (associationEnd == null || getLayer() == null) return;
    
	Object ae0 =
	    ((ModelFacade.getConnections(associationEnd)).toArray())[0];//MAssociationEnd
	Object ae1 =
	    ((ModelFacade.getConnections(associationEnd)).toArray())[1];//MAssociationEnd
	updateEnd(_srcMult, _srcRole, _srcOrdering, ae0);
	updateEnd(_destMult, _destRole, _destOrdering, ae1);
    
	boolean srcNav = ModelFacade.isNavigable(ae0);
	boolean destNav = ModelFacade.isNavigable(ae1);
	if (srcNav && destNav && SUPPRESS_BIDIRECTIONAL_ARROWS)
	    srcNav = destNav = false;
	sourceArrowHead = chooseArrowHead(ModelFacade.getAggregation(ae0), srcNav);
	destArrowHead = chooseArrowHead(ModelFacade.getAggregation(ae1), destNav);
	setSourceArrowHead(sourceArrowHead);
	setDestArrowHead(destArrowHead);
	_srcGroup.calcBounds();
	_destGroup.calcBounds();
	_middleGroup.calcBounds();
	this.computeRoute();
    }

    static ArrowHead _NAV_AGGREGATE =
	new ArrowHeadComposite(ArrowHeadDiamond.WhiteDiamond,
			       ArrowHeadGreater.TheInstance);

    static ArrowHead _NAV_COMP =
	new ArrowHeadComposite(ArrowHeadDiamond.BlackDiamond,
			       ArrowHeadGreater.TheInstance);

    protected ArrowHead chooseArrowHead(Object ak, boolean nav) {
        
        if(!ModelFacade.isAAggregationKind(ak))
            throw new IllegalArgumentException();
        
	ArrowHead arrow = ArrowHeadNone.TheInstance;

	if (nav) {
	    if (ModelFacade.NONE_AGGREGATIONKIND.equals(ak) || (ak == null))
		arrow = ArrowHeadGreater.TheInstance;
	    else if (ModelFacade.AGGREGATE_AGGREGATIONKIND.equals(ak))
		arrow = _NAV_AGGREGATE;
	    else if (ModelFacade.COMPOSITE_AGGREGATIONKIND.equals(ak))
		arrow = _NAV_COMP;
	}
	else {
	    if (ModelFacade.NONE_AGGREGATIONKIND.equals(ak) || (ak == null)) {
		arrow = ArrowHeadNone.TheInstance;
	    }
	    else if (ModelFacade.AGGREGATE_AGGREGATIONKIND.equals(ak)) {
		arrow = ArrowHeadDiamond.WhiteDiamond;
	    }
	    else if (ModelFacade.COMPOSITE_AGGREGATIONKIND.equals(ak)) {
		arrow = ArrowHeadDiamond.BlackDiamond;
	    }
	}
	return arrow;
    }

    public Vector getPopUpActions(MouseEvent me) {
	Vector popUpActions = super.getPopUpActions(me);
	// x^2 + y^2 = r^2  (equation of a circle)
	Point firstPoint = this.getFirstPoint();
	Point lastPoint = this.getLastPoint();
	int length = getPerimeterLength();

	int rSquared = (int) (.3 * length);

	// max distance is set at 100 pixels, (rSquared = 100^2)
	if ( rSquared > 100 )
	    rSquared = 10000;
	else
	    rSquared *= rSquared;

	int srcDeterminingFactor =
	    getSquaredDistance(me.getPoint(), firstPoint);
	int destDeterminingFactor =
	    getSquaredDistance(me.getPoint(), lastPoint);

	if (srcDeterminingFactor < rSquared &&
	    srcDeterminingFactor < destDeterminingFactor) {
        ArgoJMenu multMenu = new ArgoJMenu(BUNDLE, "menu.popup.multiplicity");
        
	    multMenu.add(ActionMultiplicity.SrcMultOne);
	    multMenu.add(ActionMultiplicity.SrcMultZeroToOne);
	    multMenu.add(ActionMultiplicity.SrcMultOneToMany);
	    multMenu.add(ActionMultiplicity.SrcMultZeroToMany);
	    popUpActions.insertElementAt(multMenu, popUpActions.size() - 2);

        ArgoJMenu aggMenu = new ArgoJMenu(BUNDLE, "menu.popup.aggregation");
        
	    aggMenu.add(ActionAggregation.SrcAggNone);
	    aggMenu.add(ActionAggregation.SrcAgg);
	    aggMenu.add(ActionAggregation.SrcAggComposite);
	    popUpActions.insertElementAt(aggMenu, popUpActions.size() - 2);
	}
	else if (destDeterminingFactor < rSquared) {
        ArgoJMenu multMenu = new ArgoJMenu(BUNDLE, "menu.popup.multiplicity");
	    multMenu.add(ActionMultiplicity.DestMultOne);
	    multMenu.add(ActionMultiplicity.DestMultZeroToOne);
	    multMenu.add(ActionMultiplicity.DestMultOneToMany);
	    multMenu.add(ActionMultiplicity.DestMultZeroToMany);
	    popUpActions.insertElementAt(multMenu, popUpActions.size() - 2);

        ArgoJMenu aggMenu = new ArgoJMenu(BUNDLE, "menu.popup.aggregation");
	    aggMenu.add(ActionAggregation.DestAggNone);
	    aggMenu.add(ActionAggregation.DestAgg);
	    aggMenu.add(ActionAggregation.DestAggComposite);
	    popUpActions.insertElementAt(aggMenu, popUpActions.size() - 2);
	}
	else {
	    // No particular options for right click in middle of line
	}

	// Options available when right click anywhere on line (added
	// by BobTarling 7-Jan-2002)
	Object asc = getOwner();//MAssociation
	if (asc != null) {
	    // Navigability menu with suboptions built dynamically to
	    // allow navigability from atart to end, from end to start
	    // or bidirectional

	    Collection ascEnds = ModelFacade.getConnections(asc);
            Iterator iter = ascEnds.iterator();
	    Object ascStart = iter.next();
	    Object ascEnd = iter.next();

	    if (ModelFacade.isAClassifier(ModelFacade.getType(ascStart))
                    && ModelFacade.isAClassifier(ModelFacade.getType(ascEnd))) {
        ArgoJMenu navMenu = new ArgoJMenu(BUNDLE, "menu.popup.navigability");
        
		navMenu.add(ActionNavigability.newActionNavigability(ascStart,
								     ascEnd,
								     ActionNavigability.BIDIRECTIONAL));
		navMenu.add(ActionNavigability.newActionNavigability(ascStart,
								     ascEnd,
								     ActionNavigability.STARTTOEND));
		navMenu.add(ActionNavigability.newActionNavigability(ascStart,
								     ascEnd,
								     ActionNavigability.ENDTOSTART));

		popUpActions.insertElementAt(navMenu, popUpActions.size() - 2);
	    }
	}


	return popUpActions;
    }

    /* returns the name of the OrderingKind.
     * @return "{ordered}", "{sorted}" or "" if null or "unordered"
     */
    private String getOrderingName(Object orderingKind) {
	if (orderingKind == null) return "";
	if (ModelFacade.getName(orderingKind) == null) return ""; 
	if ("".equals(ModelFacade.getName(orderingKind))) return "";
	if ("unordered".equals(ModelFacade.getName(orderingKind))) return "";
        
	return "{" + ModelFacade.getName(orderingKind) + "}";
    }

    static final long serialVersionUID = 9100125695919853919L;
  
    public void paint(Graphics g) {
        if (sourceArrowHead == null || destArrowHead == null) {
	    modelChanged(null);
        }
        if (sourceArrowHead != null && destArrowHead != null) {
	    sourceArrowHead.setLineColor(getLineColor());
	    destArrowHead.setLineColor(getLineColor());   
        }
        super.paint(g);
    }

} /* end class FigAssociation */