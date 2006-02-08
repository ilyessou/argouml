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

package org.argouml.uml.ui.foundation.core;

import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLPlainTextDocument;
import org.argouml.uml.ui.UMLTextArea2;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

/**
 * Proppanel for comments (notes). <p>
 *
 * In UML 1.3, the text of the comment is kept
 * in the name of the Comment.<p>
 *
 * In UML 1.4 and beyond, the Comment has a "body"
 * attribute, to contain the comment string.
 */
public class PropPanelComment extends PropPanelModelElement {

    /**
     * Constructor for PropPanelComment.
     */
    public PropPanelComment() {
        super("Comment", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());

        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());

        addField(Translator.localize("label.annotated-elements"),
            new JScrollPane(new UMLLinkedList(
                    new UMLCommentAnnotatedElementListModel())));

        addSeperator();

        UMLTextArea2 text = new UMLTextArea2(new UMLCommentBodyDocument());
        text.setLineWrap(true);
        text.setRows(5);
        JScrollPane pane = new JScrollPane(text);
        addField(Translator.localize("label.comment.body"), pane);

        addAction(new ActionNavigateContainerElement());
        addAction(new ActionNewStereotype());
        addAction(new ActionDeleteSingleModelElement());
    }
}

class UMLCommentBodyDocument extends UMLPlainTextDocument {
    
    /**
     * Constructor for UMLModelElementNameDocument.
     */
    public UMLCommentBodyDocument() {
        super("body"); 
        /*
         * TODO: This is probably not the right location
         * for switching off the "filterNewlines".
         * The setting gets lost after selecting a different
         * ModelElement in the diagram.
         * BTW, see how it is used in
         * javax.swing.text.PlainDocument.
         * See issue 1812.
         */
        putProperty("filterNewlines", Boolean.FALSE);
    }
    
    /**
     * @see org.argouml.uml.ui.UMLPlainTextDocument#setProperty(java.lang.String)
     */
    protected void setProperty(String text) {
        Model.getCoreHelper().setBody(getTarget(), text);
    }
    
    /**
     * @see org.argouml.uml.ui.UMLPlainTextDocument#getProperty()
     */
    protected String getProperty() {
        return (String) Model.getFacade().getBody(getTarget());
    }
    
}
