// $Id:ArgoFigText.java 13103 2007-07-21 18:14:02Z mvw $
// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.presentation.FigText;

/**
 * Primitive Fig for text.
 *
 * @author Michiel
 */
public class ArgoFigText extends FigText 
    implements NotificationEmitter, ArgoFig {

    private NotificationBroadcasterSupport notifier = 
        new NotificationBroadcasterSupport();

    public ArgoFigText(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public ArgoFigText(int x, int y, int w, int h, boolean expandOnly) {
        super(x, y, w, h, expandOnly);
    }
    
    /*
     * @see org.tigris.gef.presentation.Fig#deleteFromModel()
     */
    @Override
    public void deleteFromModel() {
        super.deleteFromModel();
        firePropChange("remove", null, null);
        notifier.sendNotification(new Notification("remove", this, 0));
    }

    /*
     * @see javax.management.NotificationEmitter#removeNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    public void removeNotificationListener(NotificationListener listener,
        NotificationFilter filter, Object handback) 
        throws ListenerNotFoundException {
        notifier.removeNotificationListener(listener, filter, handback);
    }

    /*
     * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    public void addNotificationListener(NotificationListener listener, 
        NotificationFilter filter, Object handback) 
        throws IllegalArgumentException {
        notifier.addNotificationListener(listener, filter, handback);
    }

    /*
     * @see javax.management.NotificationBroadcaster#getNotificationInfo()
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return notifier.getNotificationInfo();
    }

    /*
     * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
     */
    public void removeNotificationListener(NotificationListener listener) 
        throws ListenerNotFoundException {
        notifier.removeNotificationListener(listener);
    }
    
    /**
     * This optional method is not implemented.  It will throw an
     * {@link UnsupportedOperationException} if used.  Figs are 
     * added to a GraphModel which is, in turn, owned by a project.
     *
     * @param project the project
     * @see org.argouml.uml.diagram.ui.ArgoFig#setProject(org.argouml.kernel.Project)
     */
    public void setProject(Project project) {
        throw new UnsupportedOperationException();
    }
    
    public Project getProject() {
        LayerPerspective layer = (LayerPerspective) getLayer();
        if (layer == null) {
            /* TODO: Without this, we fail to draw e.g. a Class.
             * But is this a good solution? 
             * Why is the Layer not set in the constructor? */
            Editor editor = Globals.curEditor();
            if (editor == null) {
                /* TODO: The above doesn't work reliably in a constructor. 
                * We need a better way of getting default fig settings 
                * for the owning project rather than using the project 
                * manager singleton. - tfm
                */
                return ProjectManager.getManager().getCurrentProject();
            }
            Layer lay = editor.getLayerManager().getActiveLayer();
            if (lay instanceof LayerPerspective) {
                layer = (LayerPerspective) lay;
            }
        }
        UMLMutableGraphSupport gm = 
            (UMLMutableGraphSupport) layer.getGraphModel();
        return gm.getProject();
    }

}
