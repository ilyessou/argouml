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

package org.argouml.uml.diagram.static_structure.ui;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.model.UUIDManager;
import org.argouml.uml.diagram.use_case.ui.UseCaseDiagramRenderer;


/**
 * An object tagged as being the owner of a FigEdgeNote. Has knowledge
 * about the source and destination of the FigEdgeNote. <p>
 *
 * The source and destination are ModelElements.
 * At least one of them is a Comment - but they may be both Comments.
 *
 * @since Jul 17, 2004
 * @author jaap.branderhorst@xs4all.nl
 */
public class CommentEdge {
    private Object source;
    private Object dest;
    private Object uuid;

    private static final Logger LOG =
        Logger.getLogger(UseCaseDiagramRenderer.class);

    CommentEdge() {
        uuid = UUIDManager.getInstance().getNewUUID();
    }

    /**
     * Constructor.
     *
     * @param source the source
     * @param dest the destination
     */
    public CommentEdge(Object source, Object dest) {
        if (!(Model.getFacade().isAModelElement(source))) {
            throw new IllegalArgumentException(
                    "The source of the CommentEdge must be a model element");
        }
        if (!(Model.getFacade().isAModelElement(dest))) {
            throw new IllegalArgumentException(
                    "The destination of the CommentEdge " +
                    "must be a model element");
        }
        this.source = source;
        this.dest = dest;
        uuid = UUIDManager.getInstance().getNewUUID();
    }

    /**
     * The source of this CommentEdge.
     *
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * The destination of this CommentEdge.
     *
     * @return the destination
     */
    public Object getDestination() {
        return dest;
    }

    /**
     * @return the uuid
     */
    public Object getUUID() {
        return uuid;
    }


    /**
     * @param destination The destination to set.
     */
    public void setDestination(Object destination) {
        if (!(Model.getFacade().isAModelElement(destination))) {
            throw new IllegalArgumentException(
                    "The source of the CommentEdge must be a model element");
        }
        dest = destination;
    }

    /**
     * @param source The source to set.
     */
    public void setSource(Object source) {
        if (!(Model.getFacade().isAModelElement(source))) {
            throw new IllegalArgumentException(
                    "The destination of the CommentEdge must be a model element");
        }
        this.source = source;
    }

    /**
     * Commit suicide. Adapt the UML model.
     */
    public void delete() {
        if (Model.getFacade().isAComment(source)) {
            Model.getCoreHelper().removeAnnotatedElement(source, dest);
        } else {
            // not save to presume the destination is the comment
            if (Model.getFacade().isAComment(dest))
                Model.getCoreHelper().removeAnnotatedElement(dest, source);
        }
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        // This is the tooltip of a comment link
        return Translator.localize("misc.tooltip.commentlink");
    }
}
