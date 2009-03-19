// $Id$
// Copyright (c) 2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.core.propertypanels.panel;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.argouml.core.propertypanels.ui.SwingUIFactory;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.TabFigTarget;
import org.argouml.uml.ui.PropPanel;

/**
 * This class is the main property panel, based on XML
 *
 * @author penyaskito
 */
public class XmlPropertyPanel extends PropPanel 
    implements TabFigTarget {
    
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(XmlPropertyPanel.class);
    
    private JPanel currentPanel = null;
    
    public XmlPropertyPanel(String label, ImageIcon icon) {
        super(label, icon);
        /* Since there are no buttons on this panel (YET), 
         * we have to set the size of the buttonpanel, 
         * otherwise the layout will give it a lot of space 
         * */
        setButtonPanelSize(18);
    }

    /**
     * The instance.
     */
    private static final XmlPropertyPanel INSTANCE =
        new XmlPropertyPanel("XML Properties", null);

    /**
     * @return The instance.
     */
    public static XmlPropertyPanel getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void setTarget(Object target) {
        super.setTarget(target);       
        // TODO: Here will have to do something based on the 
        // type of the target received. For
        // commodity, we could use just the name:
        // p.e.: org.omg.uml.foundation.core.UmlClass$Impl
        // Our XML can be called Class.xml or if we split the
        // UI and the model info, Class.ui.xml and Class.model.xml
        build(target);
    }
    
    public void build(Object target) {
        LOG.info("[XMLPP] t is type:" + target.getClass());
        
        if (currentPanel != null) {
            this.remove(currentPanel);
        }
        try {
            // TODO: This references the concrete factory
            // We need a factories factory
            UIFactory factory = SwingUIFactory.getInstance();
            currentPanel = factory.createGUI(target);
            this.getTitleLabel().setText(getPanelTitle(target));
            this.add(currentPanel);
        } catch (Exception e) {
            // TODO: Auto-generated catch block
            LOG.error("Exception", e);
        }        
    }
    
    /**
     * @return the title of the panel, according to the target 
     */
    private String getPanelTitle(Object target) {
        String title = null;
        // if is a pseudostate, we have to look for the pseudostate kind.
        if (Model.getFacade().isAPseudostate(target)) {
            Object kind = Model.getFacade().getKind(target);
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getFork())) {
                title = Translator.localize("label.pseudostate.fork");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getJoin())) {
                title = Translator.localize("label.pseudostate.join");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getChoice())) {
                title = Translator.localize("label.pseudostate.choice");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getDeepHistory())) {
                title = Translator.localize("label.pseudostate.deephistory");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getShallowHistory())) {
                title = Translator.localize("label.pseudostate.shallowhistory");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getInitial())) {
                title = Translator.localize("label.pseudostate.initial");
            }
            if (Model.getFacade().equalsPseudostateKind(kind,
                    Model.getPseudostateKind().getJunction())) {
                title = Translator.localize("label.pseudostate.junction");
            }
        }
        // there are other cases that need special treatment, 
        // like concurrent regions
        if (Model.getFacade().isAConcurrentRegion(target)) {
            title = Translator.localize("label.concurrent.region");
        } else if (Model.getFacade().isConcurrent(target)) {
            title = Translator.localize("label.concurrent.composite.state");
        } else if (!Model.getFacade().isASubmachineState(target)) {
            // PropPanelSubmachine is a subclass that handles its own title
            title = Translator.localize("label.composite-state");
        }
        else {
            title = Model.getMetaTypes().getName(target); 
        }            
        return title; 
    }
   
}
