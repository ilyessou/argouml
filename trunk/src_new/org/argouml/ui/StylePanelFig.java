// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// File: FigStylePanelFig.java
// Classes: FigStylePanelFig

package org.argouml.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.apache.log4j.Logger;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.ui.ColorRenderer;

public class StylePanelFig extends StylePanel
    implements ItemListener, FocusListener, KeyListener 
{
    protected static Logger cat = 
        Logger.getLogger(StylePanelFig.class);

    ////////////////////////////////////////////////////////////////
    // constants
    private static final String BUNDLE = "Cognitive";
    
    private static final String CUSTOM_ITEM = Translator.localize(
        BUNDLE, "stylepane.label.custom") + "...";

    ////////////////////////////////////////////////////////////////
    // instance vars
    protected JLabel _bboxLabel =
	new JLabel(Translator.localize(BUNDLE, "stylepane.label.bounds") + ": ");
    protected JTextField _bboxField = new JTextField();
    protected JLabel _fillLabel =
	new JLabel(Translator.localize(BUNDLE, "stylepane.label.fill") + ": ");
    protected JComboBox _fillField = new JComboBox();
    protected JLabel _lineLabel =
	new JLabel(Translator.localize(BUNDLE, "stylepane.label.line") + ": ");
    protected JComboBox _lineField = new JComboBox();
    protected JLabel _shadowLabel =
	new JLabel(Translator.localize(BUNDLE, "stylepane.label.shadow") + ": ");
    protected JComboBox _shadowField = new ShadowComboBox();
    //protected JLabel _dashedLabel = new JLabel("Dashed: ");
    //protected JComboBox _dashedField = new JComboBox(Fig.DASHED_CHOICES);
    protected SpacerPanel _spacer = new SpacerPanel();
    protected SpacerPanel _spacer2 = new SpacerPanel();
    protected SpacerPanel _spacer3 = new SpacerPanel();

    ////////////////////////////////////////////////////////////////
    // contructors
    public StylePanelFig() {
	super("Fig Appearance");
	initChoices();
	GridBagLayout gb = (GridBagLayout) getLayout();
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.ipadx = 0; c.ipady = 0;

	Document bboxDoc = _bboxField.getDocument();
	bboxDoc.addDocumentListener(this);
	_bboxField.addKeyListener(this);
	_bboxField.addFocusListener(this);
	_fillField.addItemListener(this);
	_lineField.addItemListener(this);
	_shadowField.addItemListener(this);
	//_dashedField.addItemListener(this);

	_fillField.setRenderer(new ColorRenderer());
	_lineField.setRenderer(new ColorRenderer());
	//_dashedField.setRenderer(DashRenderer.SINGLETON);

	c.gridx = 0;
	c.gridwidth = 1;
	c.gridy = 1;
	c.weightx = 0.0;
	gb.setConstraints(_bboxLabel, c);
	add(_bboxLabel);
	c.gridy = 2;
	gb.setConstraints(_fillLabel, c);
	add(_fillLabel);
	c.gridy = 3;
	gb.setConstraints(_lineLabel, c);
	add(_lineLabel);
	c.gridy = 4;
	gb.setConstraints(_shadowLabel, c);
	add(_shadowLabel);
	//c.gridy = 5;
	//gb.setConstraints(_dashedLabel, c);
	//add(_dashedLabel);

	c.weightx = 1.0;
	c.gridx = 1;
	//c.gridwidth = GridBagConstraints.REMAINDER;
	c.gridy = 1;
	gb.setConstraints(_bboxField, c);
	add(_bboxField);
	c.gridy = 2;
	gb.setConstraints(_fillField, c);
	add(_fillField);
	c.gridy = 3;
	gb.setConstraints(_lineField, c);
	add(_lineField);
	c.gridy = 4;
	gb.setConstraints(_shadowField, c);
	add(_shadowField);
	//c.gridy = 5;
	//gb.setConstraints(_dashedField, c);
	//add(_dashedField);

	c.weightx = 0.0;
	c.gridx = 2;
	c.gridy = 1;
	gb.setConstraints(_spacer, c);
	add(_spacer);

	c.gridx = 3;
	c.gridy = 10;
	gb.setConstraints(_spacer2, c);
	add(_spacer2);

	c.weightx = 1.0;
	c.gridx = 4;
	c.gridy = 10;
	gb.setConstraints(_spacer3, c);
	add(_spacer3);
    }

    protected void initChoices() {
	_fillField.addItem(Translator.localize(BUNDLE, "stylepane.label.no-fill"));
	_fillField.addItem(Color.black);
	_fillField.addItem(Color.white);
	_fillField.addItem(Color.gray);
	_fillField.addItem(Color.lightGray);
	_fillField.addItem(Color.darkGray);
	_fillField.addItem(new Color(255, 255, 200));
	_fillField.addItem(new Color(255, 200, 255));
	_fillField.addItem(new Color(200, 255, 255));
	_fillField.addItem(new Color(200, 200, 255));
	_fillField.addItem(new Color(200, 255, 200));
	_fillField.addItem(new Color(255, 200, 200));
	_fillField.addItem(new Color(200, 200, 200));
	_fillField.addItem(Color.red);
	_fillField.addItem(Color.blue);
	_fillField.addItem(Color.cyan);
	_fillField.addItem(Color.yellow);
	_fillField.addItem(Color.magenta);
	_fillField.addItem(Color.green);
	_fillField.addItem(Color.orange);
	_fillField.addItem(Color.pink);
	_fillField.addItem(CUSTOM_ITEM);

	_lineField.addItem(Translator.localize(BUNDLE, "stylepane.label.no-line"));
	_lineField.addItem(Color.black);
	_lineField.addItem(Color.white);
	_lineField.addItem(Color.gray);
	_lineField.addItem(Color.lightGray);
	_lineField.addItem(Color.darkGray);
	_lineField.addItem(new Color(60, 60, 200));
	_lineField.addItem(new Color(60, 200, 60));
	_lineField.addItem(new Color(200, 60, 60));
	_lineField.addItem(Color.red);
	_lineField.addItem(Color.blue);
	_lineField.addItem(Color.cyan);
	_lineField.addItem(Color.yellow);
	_lineField.addItem(Color.magenta);
	_lineField.addItem(Color.green);
	_lineField.addItem(Color.orange);
	_lineField.addItem(Color.pink);
	_lineField.addItem(CUSTOM_ITEM);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * <p>Handle a refresh of the style panel after the fig has moved.</p>
     *
     * <p><em>Warning</em>. There is a circular trap here. Editing the
     *   boundary box will also trigger a refresh, and so we reset the boundary
     *   box, which causes funny behaviour (the cursor keeps jumping to the end
     *   of the text).</p>
     *
     * <p>The solution is to not reset the boundary box field if the boundaries
     *   have not changed.</p>
     */

    public void refresh() {

        // Let the parent do its refresh.

        super.refresh();

        // The boundary box as held in the target fig, and as listed in the
        // boundary box style field (null if we don't have anything valid)

        Rectangle figBounds   = _target.getBounds();
        Rectangle styleBounds = parseBBox();

        // Only reset the text if the two are not the same (i.e the fig has
        // moved, rather than we've just edited the text, when setTargetBBox()
        // will have made them the same). Note that styleBounds could be null,
        // so we do the test this way round.

        if (!(figBounds.equals(styleBounds))) {
            _bboxField.setText(figBounds.x + "," + figBounds.y + "," +
                               figBounds.width + ","  + figBounds.height);
        }

        // Change the fill colour
        
        if (_target.getFilled()) {
            Color c = _target.getFillColor();
            _fillField.setSelectedItem(c);
            if (c != null && !_fillField.getSelectedItem().equals(c)) {
                _fillField.insertItemAt(c, _fillField.getItemCount() - 1);
                _fillField.setSelectedItem(c);
            }
        }
        else {
            _fillField.setSelectedIndex(0);
        }

        // Change the line colour

        if (_target.getLineWidth() > 0) {
            Color c = _target.getLineColor();
            _lineField.setSelectedItem(c);
            if (c != null && !_lineField.getSelectedItem().equals(c)) {
                _lineField.insertItemAt(c, _lineField.getItemCount() - 1);
                _lineField.setSelectedItem(c);
            }
        }
        else {
            _lineField.setSelectedIndex(0);
        }

        // Change the shadow size if appropriate

        int shadowSize = ((FigNodeModelElement) _target).getShadowSize();

        if ((_target instanceof FigNodeModelElement) && (shadowSize > 0)) {
            _shadowField.setSelectedIndex(shadowSize);
        }
        else {
            _shadowField.setSelectedIndex(0);
        }
        
        // lets redraw the box
        setTargetBBox();
    }


    /**
     * <p>Change the bounds of the target fig. Called whenever the bounds box
     *   is edited.</p>
     *
     * <p>Format of the bounds is four integers representing x, y, width and
     *   height separated by spaces or commas. An empty field is treated as no
     *   change and leading and trailing spaces are ignored.</p>
     *
     * <p><em>Note</em>. There is a note in the old code that more work might
     *   be needed, because this could change the graph model. I don't see how
     *   that could ever be.</p>
     */

    protected void setTargetBBox() {

        // Can't do anything if we don't have a fig.

        if (_target == null) {
            return;
        }

        // Parse the boundary box text. Null is returned if it is empty or
        // invalid, which causes no change. Otherwise we tell GEF we are making
        // a change, make the change and tell GEF we've finished.

        Rectangle bounds = parseBBox();

        if (bounds == null) {
            return;
        }

        if (!_target.getBounds().equals(bounds)) {
            _target.startTrans();
            _target.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
            _target.endTrans();
            markNeedsSave();
        }
    }

    /**
     * <p>Parse the boundary box string and return the rectangle it
     *   represents.</p>
     *
     * <p>The syntax are four integers separated by spaces or commas. We ignore
     *   leading and trailing blanks.</p>
     *
     * <p>If we have the empty string we return <code>null</code>.</p>
     *
     * <p>If we fail to parse, then we return <code>null</code> and print out a
     *   rude message.</p>
     *
     * @return The size of the box, or <code>null</code> if the bounds string
     *         is empty or invalid.
     */

    protected Rectangle parseBBox() {

        // Get the text in the field, and don't do anything if the field is
        // empty.

        String bboxStr = _bboxField.getText().trim();

        if (bboxStr.length() == 0) {
            return null;
        }

        // Parse the string as if possible

        Rectangle res = new Rectangle();

        java.util.StringTokenizer st =
            new java.util.StringTokenizer(bboxStr, ", ");

        try {
            boolean changed = false;
            if (!st.hasMoreTokens()) return _target.getBounds();
            res.x      = Integer.parseInt(st.nextToken());
            if (!st.hasMoreTokens()) {
                res.y = _target.getBounds().y;
                res.width = _target.getBounds().width;
                res.height = _target.getBounds().height;
                return res;
            }
            res.y      = Integer.parseInt(st.nextToken());
            if (!st.hasMoreTokens()) {
                res.width = _target.getBounds().width;
                res.height = _target.getBounds().height;
                return res;
            }
            res.width  = Integer.parseInt(st.nextToken());            
            if ((res.width + res.x) > 6000 ) {
                res.width = 6000 - res.x;
                changed = true;
            }
            if (!st.hasMoreTokens()) {
                res.width = _target.getBounds().width;
                return res;
            }
            res.height = Integer.parseInt(st.nextToken());
            if ((res.height + res.y) > 6000) {
                res.height = 6000 - res.y;
                changed = true;
            }
            if (changed) {
                StringBuffer sb = new StringBuffer();
                sb.append(Integer.toString(res.x));
                sb.append(",");
                sb.append(Integer.toString(res.y));
                sb.append(",");
                sb.append(Integer.toString(res.width));
                sb.append(",");
                sb.append(Integer.toString(res.height));                
                _bboxField.setText(sb.toString());
            }
        }
        catch (NumberFormatException ex) {
            return null;
        }        

        return res;
    }
    
    /**
     * Prompts the user for a new custom color and adds that color to the combo box.
     */
    protected void handleCustomColor(JComboBox field, String title, Color targetColor) {
        Color newColor = JColorChooser.showDialog(
            ProjectBrowser.getInstance(), title, targetColor);
        if (newColor != null) {
            field.insertItemAt(newColor, field.getItemCount() - 1);
            field.setSelectedItem(newColor);
        }
        else if (_target != null) {
            field.setSelectedItem(targetColor);
        }        
    }

    public void setTargetFill() {
        Object c =  _fillField.getSelectedItem();
        if (_target == null || c == null) return;
        Color oldColor = _target.getFillColor();
        _target.startTrans();
        if (c instanceof Color) _target.setFillColor((Color) c);
        _target.setFilled(c instanceof Color);
        _target.endTrans();
        if (!c.equals(oldColor)) {
            markNeedsSave();
        }
    }

    public void setTargetLine() {
        Object c =  _lineField.getSelectedItem();
        if (_target == null || c == null) return;
        Color oldColor = _target.getLineColor();
        _target.startTrans();
        if (c instanceof Color) _target.setLineColor((Color) c);
        _target.setLineWidth((c instanceof Color) ? 1 : 0);
        _target.endTrans();
        if (!c.equals(oldColor)) {
            markNeedsSave();
        }
    }

    public void setTargetShadow() {
	int i =  _shadowField.getSelectedIndex();
	if (_target == null || !(_target instanceof FigNodeModelElement))
	    return;
        FigNodeModelElement nodeTarget = (FigNodeModelElement) _target;
        int oldShadowSize = nodeTarget.getShadowSize();
        _target.startTrans();
        nodeTarget.setShadowSize(i);
        _target.endTrans();
        if (i != oldShadowSize) {
            markNeedsSave();
        }
    }

    // public void setTargetDashed() {
    //     String dashStr = (String) _dashedField.getSelectedItem();
    //     if (_target == null || dashStr == null) return;
    //     _target.startTrans();
    //     _target.setDashedString(dashStr);
    //     _target.endTrans();
    //  }

    /**
     * Called when some property of the target has changed through
     * the style panel. Sets the save flag in the current project.
    **/
    protected void markNeedsSave() {
        Project p = ProjectManager.getManager().getCurrentProject();
        if (p != null) {      
            p.setNeedsSave(true);
        }
    }

    ////////////////////////////////////////////////////////////////
    // event handling

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (e.getStateChange() == ItemEvent.SELECTED && _target != null) {
            if (src == _fillField) {
                if (e.getItem() == CUSTOM_ITEM) {
                    handleCustomColor(
                        _fillField, "Custom Fill Color", _target.getFillColor());
                }
                setTargetFill();
            }
            else if (src == _lineField) {
                if (e.getItem() == CUSTOM_ITEM) {
                    handleCustomColor(
                        _lineField, "Custom Line Color", _target.getLineColor());
                }
                setTargetLine();
            }
            else if (src == _shadowField)
                setTargetShadow();
            //else if (src == _dashedField) setTargetDashed();
        }
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * Makes sure that the fig is updated when the _bboxField loses focus.
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        if (e.getSource() == _bboxField) {
            setTargetBBox();
        }
    }

    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Tests if enter is pressed in the _bbodField so we need to set the target
     * bounds.
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
        if (e.getSource().equals(_bboxField) && e.getKeyChar() == '\n') {
            setTargetBBox();
        }
    }

} /* end class StylePanelFig */
