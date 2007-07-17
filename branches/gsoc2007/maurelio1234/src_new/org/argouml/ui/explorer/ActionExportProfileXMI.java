// $Id: eclipse-argo-codetemplates.xml 11347 2006-10-26 22:37:44Z linus $
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

package org.argouml.ui.explorer;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ApplicationVersion;
import org.argouml.configuration.Configuration;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.model.UmlException;
import org.argouml.model.XmiWriter;
import org.argouml.persistence.PersistenceManager;
import org.argouml.persistence.UmlFilePersister;
import org.argouml.ui.ArgoFrame;
import org.argouml.uml.profile.Profile;
import org.argouml.uml.ui.ProjectFileView;

/**
 * Exports the model of a selected profile as XMI
 *
 * @author Marcos Aur�lio
 */
public class ActionExportProfileXMI extends AbstractAction {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(ActionExportProfileXMI.class);

    private Profile selectedProfile;
    
    /**
     * Default Constructor
     * 
     * @param profile the selected profile
     */
    public ActionExportProfileXMI(Profile profile) {
        super(Translator.localize("action.export-profile-as-xmi"));
        this.selectedProfile = profile;
    }

    /**
     * @param arg0
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {        
        Object model = selectedProfile.getModel();
        if (model != null) {
            File destiny = getTargetFile();
            if (destiny != null) {
                try {
                    saveModel(destiny, model);
                } catch (IOException e) {
                    LOG.error("Exception", e);
                } catch (UmlException e) {
                    LOG.error("Exception", e);
                }
            }
        }
    }

    private void saveModel(File destiny, Object model) throws IOException,
            UmlException {
        FileWriter w = new FileWriter(destiny);

        XmiWriter xmiWriter = Model.getXmiWriter(model, w, ApplicationVersion
                .getVersion()
                + "(" + UmlFilePersister.PERSISTENCE_VERSION + ")");
        xmiWriter.write();
    }

    private File getTargetFile() {
        PersistenceManager pm = PersistenceManager.getInstance();
        // show a chooser dialog for the file name, only xmi is allowed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(Translator.localize(
                                       "action.export-profile-as-xmi"));
        chooser.setFileView(ProjectFileView.getInstance());
        chooser.setApproveButtonText(Translator.localize(
                                             "filechooser.export"));
        chooser.setAcceptAllFileFilterUsed(true);

        String fn =
            Configuration.getString(
                PersistenceManager.KEY_PROJECT_NAME_PATH);
        if (fn.length() > 0) {
            fn = PersistenceManager.getInstance().getBaseName(fn);
            chooser.setSelectedFile(new File(fn));
        }

        int result = chooser.showSaveDialog(ArgoFrame.getInstance());
        if (result == JFileChooser.APPROVE_OPTION) {
            File theFile = chooser.getSelectedFile();
            if (theFile != null) {
                return theFile;
            }
        }
        
        return null;
    }

}
