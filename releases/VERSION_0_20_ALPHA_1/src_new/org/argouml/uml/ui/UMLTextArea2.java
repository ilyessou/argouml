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

// $Id$
package org.argouml.uml.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextArea;

import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargettableModelView;

/**
 * A JTextArea especially made to represent UMLPlainTextDocuments.
 * @author jaap.branderhorst@xs4all.nl
 * @since Dec 28, 2002
 */
public class UMLTextArea2 extends JTextArea
    implements PropertyChangeListener, TargettableModelView {


    /**
     * Constructor for UMLTextArea2.
     * @param doc the plain text document
     */
    public UMLTextArea2(UMLPlainTextDocument doc) {
        super(doc);
        addCaretListener(ActionCopy.getInstance());
        addCaretListener(ActionCut.getInstance());
        addCaretListener(ActionPaste.getInstance());
        addFocusListener(ActionPaste.getInstance());
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        ((UMLPlainTextDocument) getDocument()).propertyChange(evt);
    }

    /**
     * @see org.argouml.ui.targetmanager.TargettableModelView#getTargettableModel()
     */
    public TargetListener getTargettableModel() {
        return ((TargetListener) getDocument());
    }

}
