/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    andreas
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996,2009 The Regents of the University of California. All
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

package org.argouml.ui.cmd;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.cognitive.critics.ui.ActionOpenCritics;
import org.argouml.cognitive.ui.ActionAutoCritique;
import org.argouml.cognitive.ui.ActionOpenDecisions;
import org.argouml.cognitive.ui.ActionOpenGoals;
import org.argouml.i18n.Translator;
import org.argouml.ui.ActionExportXMI;
import org.argouml.ui.ActionImportXMI;
import org.argouml.ui.ActionProjectSettings;
import org.argouml.ui.ActionSettings;
import org.argouml.ui.ArgoJMenu;
import org.argouml.ui.ArgoToolbarManager;
import org.argouml.ui.ProjectActions;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.ZoomSliderButton;
import org.argouml.ui.explorer.ActionPerspectiveConfig;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionActivityDiagram;
import org.argouml.uml.ui.ActionClassDiagram;
import org.argouml.uml.ui.ActionCollaborationDiagram;
import org.argouml.uml.ui.ActionDeleteModelElements;
import org.argouml.uml.ui.ActionDeploymentDiagram;
import org.argouml.uml.ui.ActionGenerateAll;
import org.argouml.uml.ui.ActionGenerateProjectCode;
import org.argouml.uml.ui.ActionGenerationSettings;
import org.argouml.uml.ui.ActionImportFromSources;
import org.argouml.uml.ui.ActionLayout;
import org.argouml.uml.ui.ActionOpenProject;
import org.argouml.uml.ui.ActionRevertToSaved;
import org.argouml.uml.ui.ActionSaveAllGraphics;
import org.argouml.uml.ui.ActionSaveGraphics;
import org.argouml.uml.ui.ActionSaveProjectAs;
import org.argouml.uml.ui.ActionSequenceDiagram;
import org.argouml.uml.ui.ActionStateDiagram;
import org.argouml.uml.ui.ActionUseCaseDiagram;
import org.argouml.util.osdep.OSXAdapter;
import org.argouml.util.osdep.OsUtil;
import org.tigris.gef.base.AlignAction;
import org.tigris.gef.base.DistributeAction;
import org.tigris.gef.base.ReorderAction;
import org.tigris.toolbar.ToolBarFactory;

/**
 * GenericArgoMenuBar defines the menu bar for all operating systems which do
 * not explicitly ask for a different kind of menu bar, such as Mac OS X.
 * <p>
 *
 * Menu's and the mnemonics of menu's and the menuitems are separated in the
 * PropertyResourceBundle <em>menu.properties</em>.
 * <p>
 *
 * menu items are separated in the PropertyResourceBundle
 * <em>action.properties</em>.
 * <p>
 *
 * The key's in menu.properties have the following structure:
 *
 * <pre>
 *   menu:                    [file].[name of menu]
 *    e.g:                    menu.file
 *
 *   mnemonics of menu's:     [file].[name of menu].mnemonic
 *    e.g:                    menu.file.mnemonic
 *
 *   mnemonics of menuitems:  [file].[flag for item].[name of menuitem].mnemonic
 *    e.g:                    menu.item.new.mnemonic
 * </pre>
 *
 * TODO: Add registration for new menu items.
 * @deprecated in 0.29.2 by Bob Tarling. This class will be moved and made
 * private in future. Use MenuBarFactory.createApplicationMenuBar
 */
public class GenericArgoMenuBar extends JMenuBar implements
        TargetListener {

    private static final Logger LOG =
        Logger.getLogger(GenericArgoMenuBar.class.getName());

    private static List<JMenu> moduleMenus = new ArrayList<JMenu>();

    private static List<Action> moduleCreateDiagramActions =
        new ArrayList<Action>();

    private Collection<Action> disableableActions = new ArrayList<Action>();

    /**
     * The zoom factor - defaults to 90%/110%
     */
    public static final double ZOOM_FACTOR = 0.9;

    /**
     * Name and prepareKey-Strings of/for the i18n menu.properties. Prefix for
     * menu-keys.
     */
    private static final String MENU = "menu.";

    /**
     * Prefix for menuitem-keys.
     */
    private static final String MENUITEM = "menu.item.";

    /**
     * The toolbars.
     */
    private JToolBar fileToolbar;

    private JToolBar editToolbar;

    private JToolBar viewToolbar;

    private JToolBar createDiagramToolbar;

    /**
     * Most recently used project list.
     */
    private LastRecentlyUsedMenuList mruList;

    /**
     * Edit menu.
     */
    private JMenu edit;

    /**
     * The Select menu is a submenu of Edit.
     */
    private JMenu select;

    /**
     * View under which is the Goto Diagram, Find, Zoom, Adjust grid etc.
     */
    private ArgoJMenu view;

    /**
     * Toolbar:create diagram.
     */
    private JMenu createDiagramMenu;

    /**
     * Currently not used by core ArgoUML.
     */
    private JMenu tools;

    /**
     * Supports java generation, modules for php and html/javadocs are planned
     * feel free to contribute here!
     */
    private JMenu generate;

    /**
     * This should be invoked automatically when importing sources.
     */
    private ArgoJMenu arrange;

    /**
     * The critique menu.
     */
    private ArgoJMenu critique;

    /**
     * It needs it. Currently there is only system information and an about
     * text. Hyperlinking to online docs at argouml.org is considered to be a
     * basic improvement.
     */
    private JMenu help;

    private Action navigateTargetForwardAction;

    private Action navigateTargetBackAction;

    // References to actions that we need for Mac hack
    private ActionSettings settingsAction;
    private ActionAboutArgoUML aboutAction;
    private ActionExit exitAction;
    private ActionOpenProject openAction;
    private ActionLayout layoutAction;

    /**
     * The constructor.
     */
    public GenericArgoMenuBar() {
        initActions();
        initMenus();
        initModulesUI();
        registerForMacEvents();
    }

    private void initActions() {
        navigateTargetForwardAction = new NavigateTargetForwardAction();
        disableableActions.add(navigateTargetForwardAction);
        navigateTargetBackAction = new NavigateTargetBackAction();
        disableableActions.add(navigateTargetBackAction);
        layoutAction = new ActionLayout();
        disableableActions.add(layoutAction);

        TargetManager.getInstance().addTargetListener(this);
        /* This next line added to solve issue 5755.
         * Bring the enabled/disabled state of the actions in line with the
         * last target change that happened before we registered as listener. */
        setTarget();
    }

    /**
     * This should be a user specified option. New laws for handicapped people
     * who cannot use the mouse require software developers in US to make all
     * components of User interface accessible through keyboard
     *
     * @param item
     *            is the JMenuItem to do this for.
     * @param key
     *            is the key that we do this for.
     */
    protected static final void setMnemonic(JMenuItem item, String key) {
        String propertykey = "";
        if (item instanceof JMenu) {
            propertykey = MENU + prepareKey(key) + ".mnemonic";
        } else {
            propertykey = MENUITEM + prepareKey(key) + ".mnemonic";
        }

        String localMnemonic = Translator.localize(propertykey);
        char mnemonic = ' ';
        if (localMnemonic != null && localMnemonic.length() == 1) {
            mnemonic = localMnemonic.charAt(0);
        }
        item.setMnemonic(mnemonic);
    }

    /**
     * @param key
     *            the key to localize
     * @return the localized string
     */
    protected static final String menuLocalize(String key) {
        return Translator.localize(MENU + prepareKey(key));
    }

    /**
     * @param key the key to localize
     * @return the localized string
     */
    static final String menuItemLocalize(String key) {
        return Translator.localize(MENUITEM + prepareKey(key));
    }

    /**
     * Construct the ordinary all purpose Argo Menu Bar.
     */
    protected void initMenus() {
        initMenuFile();
        initMenuEdit();
        initMenuView();
        initMenuCreate();
        initMenuArrange();
        initMenuGeneration();
        initMenuCritique();
        initMenuTools();
        initMenuHelp();
    }

    private void initModulesUI () {
        initModulesMenus();
        initModulesActions();
    }

    /**
     * Build the menu "File".
     */
    private void initMenuFile() {
        Collection<Action> toolbarTools = new ArrayList<Action>();
        JMenu file = new JMenu(menuLocalize("File"));
        add(file);
        setMnemonic(file, "File");
        JMenuItem newItem = file.add(new ActionNew());
        setMnemonic(newItem, "New");
        ShortcutMgr.assignAccelerator(newItem, ShortcutMgr.ACTION_NEW_PROJECT);
        toolbarTools.add((new ActionNew()));
        JMenuItem newProfileItem = file.add(new ActionNewProfile());
        ShortcutMgr.assignAccelerator(newProfileItem, null);
        toolbarTools.add((new ActionNewProfile()));
        openAction = new ActionOpenProject();
        JMenuItem openProjectItem = file.add(openAction);
        setMnemonic(openProjectItem, "Open");
        ShortcutMgr.assignAccelerator(openProjectItem,
                ShortcutMgr.ACTION_OPEN_PROJECT);
        toolbarTools.add(new ActionOpenProject());
        file.addSeparator();

        JMenuItem saveProjectItem = file.add(ProjectBrowser.getInstance()
                .getSaveAction());
        setMnemonic(saveProjectItem, "Save");
        ShortcutMgr.assignAccelerator(saveProjectItem,
                ShortcutMgr.ACTION_SAVE_PROJECT);
        toolbarTools.add((ProjectBrowser.getInstance().getSaveAction()));
        JMenuItem saveProjectAsItem = file.add(new ActionSaveProjectAs());
        setMnemonic(saveProjectAsItem, "SaveAs");
        ShortcutMgr.assignAccelerator(saveProjectAsItem,
                ShortcutMgr.ACTION_SAVE_PROJECT_AS);
        JMenuItem revertToSavedItem = file.add(new ActionRevertToSaved());
        setMnemonic(revertToSavedItem, "Revert To Saved");
        ShortcutMgr.assignAccelerator(revertToSavedItem,
                ShortcutMgr.ACTION_REVERT_TO_SAVED);
        file.addSeparator();

        ShortcutMgr.assignAccelerator(file.add(new ActionImportXMI()),
                ShortcutMgr.ACTION_IMPORT_XMI);
        ShortcutMgr.assignAccelerator(file.add(new ActionExportXMI()),
                ShortcutMgr.ACTION_EXPORT_XMI);

        JMenuItem importFromSources = file.add(ActionImportFromSources
                .getInstance());
        setMnemonic(importFromSources, "Import");
        ShortcutMgr.assignAccelerator(importFromSources,
                ShortcutMgr.ACTION_IMPORT_FROM_SOURCES);
        file.addSeparator();

        Action a = new ActionProjectSettings();
        toolbarTools.add(a);

        JMenuItem pageSetupItem = file.add(new ActionPageSetup());
        setMnemonic(pageSetupItem, "PageSetup");
        ShortcutMgr.assignAccelerator(pageSetupItem,
                ShortcutMgr.ACTION_PAGE_SETUP);

        JMenuItem printItem = file.add(new ActionPrint());
        setMnemonic(printItem, "Print");
        ShortcutMgr.assignAccelerator(printItem, ShortcutMgr.ACTION_PRINT);
        toolbarTools.add((new ActionPrint()));
        JMenuItem saveGraphicsItem = file.add(new ActionSaveGraphics());
        setMnemonic(saveGraphicsItem, "SaveGraphics");
        ShortcutMgr.assignAccelerator(saveGraphicsItem,
                ShortcutMgr.ACTION_SAVE_GRAPHICS);

        ShortcutMgr.assignAccelerator(file.add(new ActionSaveAllGraphics()),
                ShortcutMgr.ACTION_SAVE_ALL_GRAPHICS);

        file.addSeparator();

        JMenu notation = (JMenu) file.add(new ActionNotation().getMenu());
        setMnemonic(notation, "Notation");

        JMenuItem propertiesItem = file.add(new ActionProjectSettings());
        ShortcutMgr.assignAccelerator(propertiesItem,
                ShortcutMgr.ACTION_PROJECT_SETTINGS);

        file.addSeparator();

        // add last recently used list _before_ exit menu
        mruList = new LastRecentlyUsedMenuList(file);

        // and exit menu entry starting with separator.
        exitAction = new ActionExit();
        if (!OsUtil.isMacOSX()) {
            file.addSeparator();
            JMenuItem exitItem = file.add(exitAction);
            setMnemonic(exitItem, "Exit");
            /* The "Close window" shortcut (ALT+F4) actually can't
             * be registered as a shortcut,
             * because it closes the configuration dialog! */
            exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                    InputEvent.ALT_MASK));
        }

        fileToolbar = (new ToolBarFactory(toolbarTools)).createToolBar();
        fileToolbar.setName(Translator.localize("misc.toolbar.file"));
        fileToolbar.setFloatable(true);
    }

    /**
     * Build the menu "Edit".
     */
    private void initMenuEdit() {

        edit = add(new JMenu(menuLocalize("Edit")));
        setMnemonic(edit, "Edit");

// Comment out when we are ready to release undo/redo
//        JMenuItem undoItem = edit.add(
//                ProjectActions.getInstance().getUndoAction());
//        setMnemonic(undoItem, "Undo");
//        ShortcutMgr.assignAccelerator(undoItem, ShortcutMgr.ACTION_UNDO);
//
//        JMenuItem redoItem = edit.add(
//                ProjectActions.getInstance().getRedoAction());
//        setMnemonic(redoItem, "Redo");
//        ShortcutMgr.assignAccelerator(redoItem, ShortcutMgr.ACTION_REDO);
//
//        edit.addSeparator();

        select = new JMenu(menuLocalize("Select"));
        setMnemonic(select, "Select");
        edit.add(select);

        JMenuItem selectAllItem = select.add(new ActionSelectAll());
        setMnemonic(selectAllItem, "Select All");
        ShortcutMgr.assignAccelerator(selectAllItem,
                ShortcutMgr.ACTION_SELECT_ALL);
        select.addSeparator();
        JMenuItem backItem = select.add(navigateTargetBackAction);
        setMnemonic(backItem, "Navigate Back");
        ShortcutMgr.assignAccelerator(backItem,
                ShortcutMgr.ACTION_NAVIGATE_BACK);
        JMenuItem forwardItem = select.add(navigateTargetForwardAction);
        setMnemonic(forwardItem, "Navigate Forward");
        ShortcutMgr.assignAccelerator(forwardItem,
                ShortcutMgr.ACTION_NAVIGATE_FORWARD);
        select.addSeparator();

        JMenuItem selectInvert = select.add(new ActionSelectInvert());
        setMnemonic(selectInvert, "Invert Selection");
        ShortcutMgr.assignAccelerator(selectInvert,
                ShortcutMgr.ACTION_SELECT_INVERT);

        edit.addSeparator();

        // JMenuItem cutItem = edit.add(ActionCut.getInstance());
        // setMnemonic(cutItem, "Cut");
        // setAccelerator(cutItem, ctrlX);
        //
        // JMenuItem copyItem = edit.add(ActionCopy.getInstance());
        // setMnemonic(copyItem, "Copy");
        // setAccelerator(copyItem, ctrlC);
        //
        // JMenuItem pasteItem = edit.add(ActionPaste.getInstance());
        // setMnemonic(pasteItem, "Paste");
        // setAccelerator(pasteItem, ctrlV);
        //
        // edit.addSeparator();

        Action removeFromDiagram = ProjectActions.getInstance()
                .getRemoveFromDiagramAction();
        JMenuItem removeItem = edit.add(removeFromDiagram);

        setMnemonic(removeItem, "Remove from Diagram");
        ShortcutMgr.assignAccelerator(removeItem,
                ShortcutMgr.ACTION_REMOVE_FROM_DIAGRAM);

        JMenuItem deleteItem = edit.add(ActionDeleteModelElements
                .getTargetFollower());
        setMnemonic(deleteItem, "Delete from Model");
        ShortcutMgr.assignAccelerator(deleteItem,
                ShortcutMgr.ACTION_DELETE_MODEL_ELEMENTS);

        edit.addSeparator();

        ShortcutMgr.assignAccelerator(edit.add(new ActionPerspectiveConfig()),
                ShortcutMgr.ACTION_PERSPECTIVE_CONFIG);

        settingsAction = new ActionSettings();
        if (!OsUtil.isMacOSX()) {
            JMenuItem settingsItem = edit.add(settingsAction);
            setMnemonic(settingsItem, "Settings");
            ShortcutMgr.assignAccelerator(settingsItem,
                    ShortcutMgr.ACTION_SETTINGS);
        }
    }


    /**
     * Build the menu "View".
     */
    private void initMenuView() {

        view = (ArgoJMenu) add(new ArgoJMenu(MENU + prepareKey("View")));
        setMnemonic(view, "View");

        JMenuItem gotoDiagram = view.add(new ActionGotoDiagram());
        setMnemonic(gotoDiagram, "Goto-Diagram");
        ShortcutMgr.assignAccelerator(gotoDiagram,
                ShortcutMgr.ACTION_GO_TO_DIAGRAM);

        JMenuItem findItem = view.add(new ActionFind());
        setMnemonic(findItem, "Find");
        ShortcutMgr.assignAccelerator(findItem, ShortcutMgr.ACTION_FIND);

        view.addSeparator();

        JMenu zoom = (JMenu) view.add(new JMenu(menuLocalize("Zoom")));
        setMnemonic(zoom, "Zoom");

        ZoomActionProxy zoomOutAction = new ZoomActionProxy(ZOOM_FACTOR);
        JMenuItem zoomOut = zoom.add(zoomOutAction);
        setMnemonic(zoomOut, "Zoom Out");
        ShortcutMgr.assignAccelerator(zoomOut, ShortcutMgr.ACTION_ZOOM_OUT);

        JMenuItem zoomReset = zoom.add(new ZoomActionProxy(0.0));
        setMnemonic(zoomReset, "Zoom Reset");
        ShortcutMgr.assignAccelerator(zoomReset, ShortcutMgr.ACTION_ZOOM_RESET);

        ZoomActionProxy zoomInAction =
            new ZoomActionProxy((1.0) / (ZOOM_FACTOR));
        JMenuItem zoomIn = zoom.add(zoomInAction);
        setMnemonic(zoomIn, "Zoom In");
        ShortcutMgr.assignAccelerator(zoomIn, ShortcutMgr.ACTION_ZOOM_IN);

        view.addSeparator();

        JMenu grid = (JMenu) view.add(new JMenu(menuLocalize("Adjust Grid")));
        setMnemonic(grid, "Adjust Grid");
        List<Action> gridActions =
            ActionAdjustGrid.createAdjustGridActions(false);
        ButtonGroup groupGrid = new ButtonGroup();
        ActionAdjustGrid.setGroup(groupGrid);
        for ( Action cmdAG : gridActions) {
            JRadioButtonMenuItem mi = new JRadioButtonMenuItem(cmdAG);
            groupGrid.add(mi);
            JMenuItem adjustGrid = grid.add(mi);
            setMnemonic(adjustGrid, (String) cmdAG.getValue(Action.NAME));
            ShortcutMgr.assignAccelerator(adjustGrid,
                    ShortcutMgr.ACTION_ADJUST_GRID + cmdAG.getValue("ID"));
        }

        JMenu snap = (JMenu) view.add(new JMenu(menuLocalize("Adjust Snap")));
        setMnemonic(snap, "Adjust Snap");
        List<Action> snapActions = ActionAdjustSnap.createAdjustSnapActions();
        ButtonGroup groupSnap = new ButtonGroup();
        ActionAdjustSnap.setGroup(groupSnap);
        for ( Action cmdAS : snapActions) {
            JRadioButtonMenuItem mi = new JRadioButtonMenuItem(cmdAS);
            groupSnap.add(mi);
            JMenuItem adjustSnap = snap.add(mi);
            setMnemonic(adjustSnap, (String) cmdAS.getValue(Action.NAME));
            ShortcutMgr.assignAccelerator(adjustSnap,
                    ShortcutMgr.ACTION_ADJUST_GUIDE + cmdAS.getValue("ID"));
        }

        Action pba  = new ActionAdjustPageBreaks();
        JMenuItem adjustPageBreaks = view.add(pba);
        setMnemonic(adjustPageBreaks, "Adjust Pagebreaks");
        ShortcutMgr.assignAccelerator(adjustPageBreaks,
                ShortcutMgr.ACTION_ADJUST_PAGE_BREAKS);

        view.addSeparator();
        JMenu menuToolbars = ArgoToolbarManager.getInstance().getMenu();
        menuToolbars.setText(menuLocalize("toolbars"));
        setMnemonic(menuToolbars, "toolbars");
        view.add(menuToolbars);
        view.addSeparator();

        JMenuItem showSaved = view.add(new ActionShowXMLDump());
        setMnemonic(showSaved, "Show Saved");
        ShortcutMgr.assignAccelerator(showSaved,
                ShortcutMgr.ACTION_SHOW_XML_DUMP);
    }

    /**
     * Build the menu "Create" and the toolbar for diagram creation. These are
     * build together to guarantee that the same items are present in both, and
     * in the same sequence.
     * <p>
     *
     * The sequence of these items was determined by issue 1821.
     */
    protected void initMenuCreate() {
        Collection<Action> toolbarTools = new ArrayList<Action>();
        createDiagramMenu = add(new JMenu(menuLocalize("Create Diagram")));
        setMnemonic(createDiagramMenu, "Create Diagram");
        JMenuItem usecaseDiagram = createDiagramMenu
                .add(new ActionUseCaseDiagram());
        setMnemonic(usecaseDiagram, "Usecase Diagram");
        toolbarTools.add((new ActionUseCaseDiagram()));
        ShortcutMgr.assignAccelerator(usecaseDiagram,
                ShortcutMgr.ACTION_USE_CASE_DIAGRAM);

        JMenuItem classDiagram =
            createDiagramMenu.add(new ActionClassDiagram());
        setMnemonic(classDiagram, "Class Diagram");
        toolbarTools.add((new ActionClassDiagram()));
        ShortcutMgr.assignAccelerator(classDiagram,
                ShortcutMgr.ACTION_CLASS_DIAGRAM);

        JMenuItem sequenzDiagram =
            createDiagramMenu.add(new ActionSequenceDiagram());
        setMnemonic(sequenzDiagram, "Sequenz Diagram");
        toolbarTools.add((new ActionSequenceDiagram()));
        ShortcutMgr.assignAccelerator(sequenzDiagram,
                ShortcutMgr.ACTION_SEQUENCE_DIAGRAM);

        JMenuItem collaborationDiagram =
            createDiagramMenu.add(new ActionCollaborationDiagram());
        setMnemonic(collaborationDiagram, "Collaboration Diagram");
        toolbarTools.add((new ActionCollaborationDiagram()));
        ShortcutMgr.assignAccelerator(collaborationDiagram,
                ShortcutMgr.ACTION_COLLABORATION_DIAGRAM);

        JMenuItem stateDiagram =
            createDiagramMenu.add(new ActionStateDiagram());
        setMnemonic(stateDiagram, "State Diagram");
        toolbarTools.add((new ActionStateDiagram()));
        ShortcutMgr.assignAccelerator(stateDiagram,
                ShortcutMgr.ACTION_STATE_DIAGRAM);

        JMenuItem activityDiagram =
            createDiagramMenu.add(new ActionActivityDiagram());
        setMnemonic(activityDiagram, "Activity Diagram");
        toolbarTools.add((new ActionActivityDiagram()));
        ShortcutMgr.assignAccelerator(activityDiagram,
                ShortcutMgr.ACTION_ACTIVITY_DIAGRAM);

        JMenuItem deploymentDiagram =
            createDiagramMenu.add(new ActionDeploymentDiagram());
        setMnemonic(deploymentDiagram, "Deployment Diagram");
        toolbarTools.add((new ActionDeploymentDiagram()));
        ShortcutMgr.assignAccelerator(deploymentDiagram,
                ShortcutMgr.ACTION_DEPLOYMENT_DIAGRAM);

        createDiagramToolbar =
            (new ToolBarFactory(toolbarTools)).createToolBar();
        createDiagramToolbar.setName(
                Translator.localize("misc.toolbar.create-diagram"));
        createDiagramToolbar.setFloatable(true);
    }

    /**
     * Build the menu "Arrange".
     */
    private void initMenuArrange() {
        arrange = (ArgoJMenu) add(new ArgoJMenu(MENU + prepareKey("Arrange")));
        setMnemonic(arrange, "Arrange");

        JMenu align = (JMenu) arrange.add(new JMenu(menuLocalize("Align")));
        setMnemonic(align, "Align");
        JMenu distribute = (JMenu) arrange.add(new JMenu(
                menuLocalize("Distribute")));
        setMnemonic(distribute, "Distribute");
        JMenu reorder = (JMenu) arrange.add(new JMenu(menuLocalize("Reorder")));
        setMnemonic(reorder, "Reorder");

        JMenuItem preferredSize = arrange.add(new CmdSetPreferredSize());
        setMnemonic(preferredSize, "Preferred Size");
        ShortcutMgr.assignAccelerator(preferredSize,
                ShortcutMgr.ACTION_PREFERRED_SIZE);

        arrange.add(layoutAction);

        // This used to be deferred, but it's only 30-40 msec of work.
        initAlignMenu(align);
        initDistributeMenu(distribute);
        initReorderMenu(reorder);
    }

    /**
     * Initialize submenus of the Align menu.
     *
     * @param align
     *            the Align menu
     */
    private static void initAlignMenu(JMenu align) {
        AlignAction a = new AlignAction(AlignAction.ALIGN_TOPS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignTops"));
        JMenuItem alignTops = align.add(a);
        setMnemonic(alignTops, "align tops");
        ShortcutMgr.assignAccelerator(alignTops, ShortcutMgr.ACTION_ALIGN_TOPS);

        a = new AlignAction(
                AlignAction.ALIGN_BOTTOMS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignBottoms"));
        JMenuItem alignBottoms = align.add(a);
        setMnemonic(alignBottoms, "align bottoms");
        ShortcutMgr.assignAccelerator(alignBottoms,
                ShortcutMgr.ACTION_ALIGN_BOTTOMS);

        a = new AlignAction(
                AlignAction.ALIGN_RIGHTS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignRights"));
        JMenuItem alignRights = align.add(a);
        setMnemonic(alignRights, "align rights");
        ShortcutMgr.assignAccelerator(alignRights,
                ShortcutMgr.ACTION_ALIGN_RIGHTS);

        a = new AlignAction(
                AlignAction.ALIGN_LEFTS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignLefts"));
        JMenuItem alignLefts = align.add(a);
        setMnemonic(alignLefts, "align lefts");
        ShortcutMgr.assignAccelerator(alignLefts,
                ShortcutMgr.ACTION_ALIGN_LEFTS);

        a = new AlignAction(
                AlignAction.ALIGN_H_CENTERS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignHorizontalCenters"));
        JMenuItem alignHCenters = align.add(a);
        setMnemonic(alignHCenters,
                "align horizontal centers");
        ShortcutMgr.assignAccelerator(alignHCenters,
                ShortcutMgr.ACTION_ALIGN_H_CENTERS);

        a = new AlignAction(
                AlignAction.ALIGN_V_CENTERS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignVerticalCenters"));
        JMenuItem alignVCenters = align.add(a);
        setMnemonic(alignVCenters, "align vertical centers");
        ShortcutMgr.assignAccelerator(alignVCenters,
                ShortcutMgr.ACTION_ALIGN_V_CENTERS);

        a = new AlignAction(
                AlignAction.ALIGN_TO_GRID);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("AlignToGrid"));
        JMenuItem alignToGrid = align.add(a);
        setMnemonic(alignToGrid, "align to grid");
        ShortcutMgr.assignAccelerator(alignToGrid,
                ShortcutMgr.ACTION_ALIGN_TO_GRID);
    }

    /**
     * Initialize submenus of the Distribute menu.
     *
     * @param distribute
     *            the Distribute menu
     */
    private static void initDistributeMenu(JMenu distribute) {
        DistributeAction a = new DistributeAction(
                DistributeAction.H_SPACING);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon(
                        "DistributeHorizontalSpacing"));
        JMenuItem distributeHSpacing = distribute.add(a);
        setMnemonic(distributeHSpacing,
                "distribute horizontal spacing");
        ShortcutMgr.assignAccelerator(distributeHSpacing,
                ShortcutMgr.ACTION_DISTRIBUTE_H_SPACING);

        a = new DistributeAction(
                DistributeAction.H_CENTERS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon(
                        "DistributeHorizontalCenters"));
        JMenuItem distributeHCenters = distribute.add(a);
        setMnemonic(distributeHCenters,
                "distribute horizontal centers");
        ShortcutMgr.assignAccelerator(distributeHCenters,
                ShortcutMgr.ACTION_DISTRIBUTE_H_CENTERS);

        a = new DistributeAction(
                DistributeAction.V_SPACING);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("DistributeVerticalSpacing"));
        JMenuItem distributeVSpacing = distribute.add(a);
        setMnemonic(distributeVSpacing,
                "distribute vertical spacing");
        ShortcutMgr.assignAccelerator(distributeVSpacing,
                ShortcutMgr.ACTION_DISTRIBUTE_V_SPACING);

        a = new DistributeAction(
                DistributeAction.V_CENTERS);
        a.putValue(Action.SMALL_ICON,
                ResourceLoaderWrapper.lookupIcon("DistributeVerticalCenters"));
        JMenuItem distributeVCenters = distribute.add(a);
        setMnemonic(distributeVCenters,
                "distribute vertical centers");
        ShortcutMgr.assignAccelerator(distributeVCenters,
                ShortcutMgr.ACTION_DISTRIBUTE_V_CENTERS);
    }

    /**
     * Initialize the submenus for the Reorder menu.
     *
     * @param reorder
     *            the main Reorder menu
     */
    private static void initReorderMenu(JMenu reorder) {
        JMenuItem reorderBringForward = reorder.add(new ReorderAction(
                Translator.localize("action.bring-forward"),
                ResourceLoaderWrapper.lookupIcon("Forward"),
                ReorderAction.BRING_FORWARD));
        setMnemonic(reorderBringForward,
                "reorder bring forward");
        ShortcutMgr.assignAccelerator(reorderBringForward,
                ShortcutMgr.ACTION_REORDER_FORWARD);

        JMenuItem reorderSendBackward = reorder.add(new ReorderAction(
                Translator.localize("action.send-backward"),
                ResourceLoaderWrapper.lookupIcon("Backward"),
                ReorderAction.SEND_BACKWARD));
        setMnemonic(reorderSendBackward,
                "reorder send backward");
        ShortcutMgr.assignAccelerator(reorderSendBackward,
                ShortcutMgr.ACTION_REORDER_BACKWARD);

        JMenuItem reorderBringToFront = reorder.add(new ReorderAction(
                Translator.localize("action.bring-to-front"),
                ResourceLoaderWrapper.lookupIcon("ToFront"),
                ReorderAction.BRING_TO_FRONT));
        setMnemonic(reorderBringToFront,
                "reorder bring to front");
        ShortcutMgr.assignAccelerator(reorderBringToFront,
                ShortcutMgr.ACTION_REORDER_TO_FRONT);

        JMenuItem reorderSendToBack = reorder.add(new ReorderAction(
                Translator.localize("action.send-to-back"),
                ResourceLoaderWrapper.lookupIcon("ToBack"),
                ReorderAction.SEND_TO_BACK));
        setMnemonic(reorderSendToBack,
                "reorder send to back");
        ShortcutMgr.assignAccelerator(reorderSendToBack,
                ShortcutMgr.ACTION_REORDER_TO_BACK);
    }


    /**
     * Build the menu "Generation".
     */
    private void initMenuGeneration() {

        // KeyStroke f7 = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);

        generate = add(new JMenu(menuLocalize("Generation")));
        setMnemonic(generate, "Generation");
        JMenuItem genAllItem = generate.add(new ActionGenerateAll());
        setMnemonic(genAllItem, "action.generate-code");
        ShortcutMgr.assignAccelerator(genAllItem,
                ShortcutMgr.ACTION_GENERATE_ALL_CLASSES);
        generate.addSeparator();
        JMenuItem genProject = generate.add(new ActionGenerateProjectCode());
        setMnemonic(genProject, "action.generate-code-for-project");
        ShortcutMgr.assignAccelerator(genProject,
                ShortcutMgr.ACTION_GENERATE_PROJECT_CODE);
        JMenuItem generationSettings = generate
                .add(new ActionGenerationSettings());
        setMnemonic(generationSettings, "action.settings-for-project-code-generation");
        ShortcutMgr.assignAccelerator(generationSettings,
                ShortcutMgr.ACTION_GENERATION_SETTINGS);
        // generate.add(Actions.GenerateWeb);
    }

    /**
     * Build the menu "Critique".
     */
    private void initMenuCritique() {
        // TODO: This creates a dependency on the Critics subsystem.
        // Instead that subsystem should register its desired menus and actions.
        critique =
            (ArgoJMenu) add(new ArgoJMenu(MENU + prepareKey("Critique")));
        setMnemonic(critique, "Critique");
        JMenuItem toggleAutoCritique = critique
                .addCheckItem(new ActionAutoCritique());
        setMnemonic(toggleAutoCritique, "Toggle Auto Critique");
        ShortcutMgr.assignAccelerator(toggleAutoCritique,
                ShortcutMgr.ACTION_AUTO_CRITIQUE);
        critique.addSeparator();
        JMenuItem designIssues = critique.add(new ActionOpenDecisions());
        setMnemonic(designIssues, "Design Issues");
        ShortcutMgr.assignAccelerator(designIssues,
                ShortcutMgr.ACTION_OPEN_DECISIONS);
        JMenuItem designGoals = critique.add(new ActionOpenGoals());
        setMnemonic(designGoals, "Design Goals");
        ShortcutMgr.assignAccelerator(designGoals,
                ShortcutMgr.ACTION_OPEN_GOALS);
        JMenuItem browseCritics = critique.add(new ActionOpenCritics());
        setMnemonic(browseCritics, "Browse Critics");
        ShortcutMgr.assignAccelerator(designIssues,
                ShortcutMgr.ACTION_OPEN_CRITICS);
    }

    /**
     * Build the menu "Tools".
     */
    private void initMenuTools() {
        tools = new JMenu(menuLocalize("Tools"));
        setMnemonic(tools, "Tools");

        // TODO: Add empty placeholder here?

        add(tools);

    }

    /**
     * Build the menu "Help".
     */
    private void initMenuHelp() {
        help = new JMenu(menuLocalize("Help"));
        setMnemonic(help, "Help");
        if (help.getItemCount() > 0) {
            help.insertSeparator(0);
        }

        // Add the help menu item.
        JMenuItem argoHelp = help.add(new ActionHelp());
        setMnemonic(argoHelp, "ArgoUML help");
        ShortcutMgr.assignAccelerator(argoHelp, ShortcutMgr.ACTION_HELP);
        help.addSeparator();

        JMenuItem systemInfo = help.add(new ActionSystemInfo());
        setMnemonic(systemInfo, "System Information");
        ShortcutMgr.assignAccelerator(systemInfo,
                ShortcutMgr.ACTION_SYSTEM_INFORMATION);

        aboutAction = new ActionAboutArgoUML();
        if (!OsUtil.isMacOSX()) {
            help.addSeparator();
            JMenuItem aboutArgoUML = help.add(aboutAction);
            setMnemonic(aboutArgoUML, "About ArgoUML");
            ShortcutMgr.assignAccelerator(aboutArgoUML,
                    ShortcutMgr.ACTION_ABOUT_ARGOUML);
        }

        // setHelpMenu(help);
        add(help);
    }

    private void initModulesMenus() {
        for (JMenu menu : moduleMenus) {
            add(menu);
        }
    }

    private void initModulesActions() {
        for (Action action : moduleCreateDiagramActions) {
            createDiagramToolbar.add(action);
        }
    }

    /**
     * Get the create diagram toolbar.
     *
     * @return Value of property _createDiagramToolbar.
     */
    public JToolBar getCreateDiagramToolbar() {
        return createDiagramToolbar;
    }

    /**
     * Get the create diagram menu. Provided to allow plugins
     * to appeand their own actions to this menu.
     *
     * @return Value of property createDiagramMenu
     */
    public JMenu getCreateDiagramMenu() {
        return createDiagramMenu;
    }

    /**
     * Get the edit toolbar.
     *
     * @return the edit toolbar.
     */
    public JToolBar getEditToolbar() {
        if (editToolbar == null) {
            /* Create the edit toolbar based on the Menu.
             * All menuItems that have an Icon are presumed to
             * be based upon an Action,
             * and these Actions are used in the toolbar.  */
            Collection<Action> c = new ArrayList<Action>();
            for (Object mi : edit.getMenuComponents()) {
                if (mi instanceof JMenuItem) {
                    if (((JMenuItem) mi).getIcon() != null) {
                        c.add(((JMenuItem) mi).getAction());
                    }
                }
            }
            editToolbar = (new ToolBarFactory(c)).createToolBar();
            editToolbar.setName(Translator.localize("misc.toolbar.edit"));
            editToolbar.setFloatable(true);
        }
        return editToolbar;
    }

    /**
     * Getter for the file toolbar.
     *
     * @return the file toolbar.
     *
     */
    public JToolBar getFileToolbar() {
        return fileToolbar;
    }

    /**
     * Getter for the view toolbar.
     *
     * @return the view toolbar.
     */
    public JToolBar getViewToolbar() {
        if (viewToolbar == null) {
            Collection<Object> c = new ArrayList<Object>();
            //Component or Action
            c.add(new ActionFind());
            c.add(new ZoomSliderButton());
            viewToolbar = (new ToolBarFactory(c)).createToolBar();
            viewToolbar.setName(Translator.localize("misc.toolbar.view"));
            viewToolbar.setFloatable(true);
        }
        return viewToolbar;
    }

    /**
     * Prepares one part of the key for menu- or/and menuitem-mnemonics used in
     * menu.properties.
     *
     * The method changes the parameter key to lower cases. Spaces in the
     * parameter key are changed to hyphens.
     *
     * @param key
     * @return the prepared key
     */
    private static String prepareKey(String key) {
        return key.toLowerCase().replace(' ', '-');
    }

    /**
     * Adds the entry to the mru list.
     *
     * @param filename
     *            of the project
     *
     * TODO: This should listen for file save events rather than being called
     * directly - tfm.
     */
    public void addFileSaved(String filename) {
        mruList.addEntry(filename);
    }

    /**
     * Getter for the Tools menu.
     *
     * @return The Tools menu.
     */
    public JMenu getTools() {
        return tools;
    }

    /**
     * Getter for the Generation menu.
     *
     * @return The Generation menu.
     */
    public JMenu getGeneration() {
	return generate;
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = 2904074534530273119L;

    /**
     * Target changed - update the actions that depend on the target.
     */
    private void setTarget() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (Action action : disableableActions) {
                    action.setEnabled(action.isEnabled());
                }
            }
        });
    }

    public void targetAdded(TargetEvent e) {
        setTarget();
    }

    public void targetRemoved(TargetEvent e) {
        setTarget();
    }

    public void targetSet(TargetEvent e) {
        setTarget();
    }

    /**
     * Add a new menu to the main toolbar.
     *
     * @param menu the menu to be added
     */
    public static void registerMenuItem(JMenu menu) {
        moduleMenus.add(menu);
    }

    /**
     * Add a new Action to the Create Diagram toolbar.  This can be used to
     * register create actions for new diagram types by plugin modules.
     *
     * @param action the create action to be registered.
     */
    public static void registerCreateDiagramAction(Action action) {
        moduleCreateDiagramActions.add(action);
    }

    private void registerForMacEvents() {
        LOG.log(Level.INFO, "Determining if Mac OS to set special handlers");
        if (OsUtil.isMacOSX()) {
            LOG.log(Level.INFO, "System is Mac OS - setting special handlers");
            try {
                // Generate and register the OSXAdapter, passing the methods
                // we wish to use as delegates for various
                // com.apple.eawt.ApplicationListener methods
                LOG.log(Level.INFO, "Registering Quit handler for Mac");
                OSXAdapter.setQuitHandler(this, getClass().getMethod(
                        "macQuit", (Class[]) null));
                LOG.log(Level.INFO, "Registering About handler for Mac");
                OSXAdapter.setAboutHandler(this, getClass().getMethod(
                        "macAbout", (Class[]) null));
                LOG.log(Level.INFO, "Registering Preferences handler for Mac");
                OSXAdapter.setPreferencesHandler(this, getClass()
                        .getMethod("macPreferences", (Class[]) null));
                LOG.log(Level.INFO, "Registering File handler for Mac");
                OSXAdapter.setFileHandler(this, getClass().getMethod(
                        "macOpenFile", new Class[] {String.class}));
                LOG.log(Level.INFO, "All Mac handlers set");
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while loading the OSXAdapter:", e);
            }
        }
    }

    /**
     * Internal use only.  Do not use.
     */
    public boolean macQuit() {
        LOG.log(Level.INFO, "Quit has been chosen on a Mac");
        return ProjectBrowser.getInstance().tryExit();
    }

    /**
     * Internal use only.  Do not use.
     */
    public void macAbout() {
        aboutAction.actionPerformed(null);
    }

    /**
     * Internal use only.  Do not use.
     */
    public void macPreferences() {
        settingsAction.actionPerformed(null);
    }

    /**
     * Internal use only.  Do not use.
     * @param filename name of file to be opened
     */
    public void macOpenFile(String filename) {
        openAction.doCommand(filename);
    }
}
