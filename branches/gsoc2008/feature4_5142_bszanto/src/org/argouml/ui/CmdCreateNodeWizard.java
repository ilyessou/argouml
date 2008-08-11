// $Id: svn:keywords $
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

package org.argouml.ui;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.uml.diagram.ui.CreateWizard;
import org.tigris.gef.base.CreateNodeAction;

/**
 * This class creates a new node using a CreateWizard.
 * 
 * @author bszanto
 */
public class CmdCreateNodeWizard extends CreateNodeAction {
    
    /** Wizard to use */
    private CreateWizard wizard = null;
    
    /** Object to be created. */
    private Object node;

    /**
     * @param modelElement
     * @param name
     * @param wiz
     */
    public CmdCreateNodeWizard(Object modelElement, String name, CreateWizard wiz) {
        super(modelElement,
                name,
                ResourceLoaderWrapper.lookupIconResource(
                        ResourceLoaderWrapper.getImageBinding(name)));
        wizard = wiz;
        putValue(Action.SHORT_DESCRIPTION, Translator.localize(name));
        
        System.out.println("done");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        wizard.init();
        // displays the wizard that returns the node to be created
        node = wizard.display();
        
        // if the node is null, the acion was canceled
        if (node != null) {
            super.actionPerformed(e);
        } else {
            return;
        }
    }
    
    @Override
    public Object makeNode() {
        return node;
    }

}
