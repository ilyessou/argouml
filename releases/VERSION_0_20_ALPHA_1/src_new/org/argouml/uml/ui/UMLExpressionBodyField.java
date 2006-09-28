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

package org.argouml.uml.ui;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

/**
 * This text field shows the body of a UML expression.
 *
 */
public class UMLExpressionBodyField extends JTextArea
    implements DocumentListener, UMLUserInterfaceComponent, PropertyChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(UMLExpressionBodyField.class);

    private UMLExpressionModel2 model;
    private boolean notifyModel;

    /**
     * The constructor.
     *
     * @param m Expression model, should be shared between
     * Language and Body fields
     * @param n Only one of Language and Body fields should
     * forward events to model
     */
    public UMLExpressionBodyField(UMLExpressionModel2 m,
				  boolean n) {
        model = m;
        notifyModel = n;
        getDocument().addDocumentListener(this);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetChanged()
     */
    public void targetChanged() {
	LOG.debug("UMLExpressionBodyField: targetChanged");
	if (notifyModel) {
	    model.targetChanged();
	}
        update();
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetReasserted()
     */
    public void targetReasserted() {
    }

    public void propertyChange(PropertyChangeEvent event) {
        LOG.debug("UMLExpressionBodyField: propertySet" + event);
        update();
    }

    private void update() {
        String oldText = getText();
        String newText = model.getBody();
        LOG.debug("UMLExpressionBodyField: update: " + oldText + " " + newText);

        if (oldText == null || newText == null || !oldText.equals(newText)) {
            if (oldText != newText) {
                LOG.debug("setNewText!!");
                setText(newText);
            }
        }
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(final DocumentEvent p1) {
        model.setBody(getText());
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(final DocumentEvent p1) {
        model.setBody(getText());
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(final DocumentEvent p1) {
        model.setBody(getText());
    }
}
