// $Id$
// Copyright (c) 1996-2001 The Regents of the University of California. All
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

package org.argouml.uml.cognitive.checklist;

import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.argouml.cognitive.checklist.CheckManager;
import org.argouml.model.ModelFacade;

/** Registers Checklists for different kinds of model elements. If you
 *  add a new checklist, a line must be added here.
 *
 *  @see org.argouml.cognitive.checklist.CheckManager */

public class Init {

    protected static Logger cat =
	Logger.getLogger(Init.class);

    /** static initializer, register all appropriate critics */
    public static void init(Locale locale) {
	ResourceBundle bundle =
	    ResourceBundle
	    .getBundle("org.argouml.i18n.UMLCognitiveResourceBundle", locale);
	addChecklist(bundle, (Class)ModelFacade.CLASS, "ChClass");
	addChecklist(bundle, (Class)ModelFacade.INTERFACE, "ChInterface");
	addChecklist(bundle, (Class)ModelFacade.ATTRIBUTE, "ChAttribute");
	addChecklist(bundle, (Class)ModelFacade.OPERATION, "ChOperation");
	addChecklist(bundle, (Class)ModelFacade.ASSOCIATION, "ChAssociation");
	addChecklist(bundle, (Class)ModelFacade.ASSOCIATION_CLASS, "ChAssociation");
	addChecklist(bundle, (Class)ModelFacade.STATE, "ChState");
	addChecklist(bundle, (Class)ModelFacade.TRANSITION, "ChTransition");
	addChecklist(bundle, (Class)ModelFacade.USE_CASE, "ChUseCase");
	addChecklist(bundle, (Class)ModelFacade.ACTOR, "ChActor");
    }

    private static void addChecklist(ResourceBundle bundle,
				     Class cls, String key) {
        try {
            UMLChecklist checklist =
		new UMLChecklist((String[][]) bundle.getObject(key));
            CheckManager.register(cls, checklist);
        }
        catch (MissingResourceException e) {
            cat.error(e);
            e.printStackTrace();
        }
    }

} /* end class Init */
