// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.uml.generator.ui;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import ru.novosoft.uml.foundation.core.*;

import org.tigris.gef.util.*;

import org.argouml.kernel.*;
import org.argouml.ui.*;
import org.argouml.language.java.generator.*;
import org.argouml.application.api.*;
import org.argouml.uml.generator.*;

public class ClassGenerationDialog extends JFrame implements ActionListener {

  ////////////////////////////////////////////////////////////////
  // constants
  private static final String BUNDLE = "Cognitive";

  static final String high = Localizer.localize(BUNDLE, "level.high");
  static final String medium = Localizer.localize(BUNDLE, "level.medium");
  static final String low = Localizer.localize(BUNDLE, "level.low");

  public static final String PRIORITIES[] = { high, medium, low };
  public static final int WIDTH = 300;
  public static final int HEIGHT = 350;

  ////////////////////////////////////////////////////////////////
  // instance variables
  TableModelClassChecks _tableModel = new TableModelClassChecks();
    protected JTable _table = new JTable();
//  protected JTextField _dir = new JTextField();
  protected JComboBox _dir;
  protected JButton _generateButton = new JButton("Generate");
  protected JButton _cancelButton = new JButton("Cancel");

    ArrayList _languages = null;

  ////////////////////////////////////////////////////////////////
  // constructors

  public ClassGenerationDialog(Vector nodes) {
    super("Generate Classes");

    Vector dirs = getClasspathEntries();
//     if (dirs.size() == 0) { 
// 	dispose();
// 	return;
//     }
    _dir = new JComboBox(Converter.convert(getClasspathEntries()));
    _dir.setEditable(true);

    JLabel promptLabel = new JLabel("Generate Classes:");
    JLabel dirLabel = new JLabel("Output Directory:");

    _languages = Notation.getLanguageNotations();
    _tableModel.setTarget(nodes, _languages);
    _table.setModel(_tableModel);

    Font labelFont = MetalLookAndFeel.getSubTextFont();
    _table.setFont(labelFont);

    //_table.setRowSelectionAllowed(false);
    _table.setIntercellSpacing(new Dimension(0, 1));
    _table.setShowVerticalLines(false);
//     _table.getSelectionModel().addListSelectionListener(this);
    _table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

    TableColumn descCol = _table.getColumnModel().getColumn(0);
    descCol.setMinWidth(100);
    descCol.setWidth(200);
    if (_languages.size() <= 1)
	_table.setTableHeader(null);

// Vector nodes = _diagram.getGraphModel().getNodes();
// _table.setModel();
// _table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    setSize(new Dimension(WIDTH, HEIGHT));
    getContentPane().setLayout(new BorderLayout());
    JPanel top = new JPanel();
    GridBagLayout gb = new GridBagLayout();
    top.setLayout(gb);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.0;
    c.ipadx = 3; c.ipady = 3;


    c.gridx = 0;
    c.gridwidth = 1;
    c.gridy = 0;
    gb.setConstraints(promptLabel, c);
    top.add(promptLabel);

    c.gridy = 1;
    c.weighty = 1.0;
    JScrollPane classesSP = new JScrollPane(_table);
    JPanel hack = new JPanel();
    hack.setLayout(new BorderLayout());
    hack.add(classesSP, BorderLayout.CENTER);
    hack.setPreferredSize(new Dimension(250, 200));
    hack.setSize(new Dimension(250, 200));
    gb.setConstraints(hack, c);
    top.add(hack);

    c.weighty = 0.0;
    c.gridy = 2;
    gb.setConstraints(dirLabel, c);
    top.add(dirLabel);

    c.weightx = 1.0;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridy = 3;
    gb.setConstraints(_dir, c);
    top.add(_dir);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JPanel buttonInner = new JPanel(new GridLayout(1, 2));
    buttonInner.add(_generateButton);
    buttonInner.add(_cancelButton);
    buttonPanel.add(buttonInner);

    ProjectBrowser pb = ProjectBrowser.TheInstance;
    Project p = pb.getProject();
    //_dir.setText(p.getGenerationPrefs().getOutputDir());
    _dir.getModel().setSelectedItem(p.getGenerationPrefs().getOutputDir());

    Rectangle pbBox = pb.getBounds();
    setLocation(pbBox.x + (pbBox.width - WIDTH)/2,
		pbBox.y + (pbBox.height - HEIGHT)/2);
    getContentPane().add(top, BorderLayout.NORTH);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    getRootPane().setDefaultButton(_generateButton);
    _generateButton.addActionListener(this);
    _cancelButton.addActionListener(this);
  }

  public final static String pathSep=System.getProperty("path.separator");

  private static Vector getClasspathEntries() {
      String classpath=System.getProperty("java.class.path");
      Vector entries=new Vector();
      StringTokenizer allEntries=new StringTokenizer(classpath,pathSep);
      while (allEntries.hasMoreElements()) {
	  String entry=allEntries.nextToken();
	  if (!entry.toLowerCase().endsWith(".jar")
	      && !entry.toLowerCase().endsWith(".zip")) {
	      entries.addElement(entry);
	  }
      }
      // if (entries.size() == 0) {
// 	  JOptionPane.showMessageDialog(null, "In order to generate Java files, you need to have\nat least one directory in your CLASSPATH environment variable,\nwhere ArgoUML can store and compile the files.", "Code generation", JOptionPane.ERROR_MESSAGE);
// 	  return null;
//       }
      return entries;
  }

  public Dimension getMaximumSize() { return new Dimension(WIDTH, HEIGHT); }


  ////////////////////////////////////////////////////////////////
  // event handlers
  /** Either the Generate or the Cancel buttons is pressed.
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == _generateButton) {
      // String path = _dir.getText().trim();
      String path = ((String)_dir.getModel().getSelectedItem()).trim();

      ProjectBrowser pb = ProjectBrowser.TheInstance;
      Project p = pb.getProject();
      p.getGenerationPrefs().setOutputDir(path);
      Vector[] fileNames = new Vector[_languages.size()];
      for (int i = 0; i < _languages.size(); i++) {
	  fileNames[i] = new Vector();
	  NotationName language = (NotationName)_languages.get(i);

	  Generator generator = Generator.getGenerator(language);
	  Set nodes = _tableModel.getChecked(language);
	  for (Iterator iter = nodes.iterator();
	       iter.hasNext();
	       ) {
	      Object node = iter.next();

	      if (node instanceof MClassifier) {
		  // Needs-more-work:
		  // This will only work for languages that have each node
		  // in a separate files (one or more).
		  String fn = generator.GenerateFile((MClassifier) node, path);
		  fileNames[i].add(fn);
	      }
	  }
      }
      setVisible(false);
      dispose();
    }
    if (e.getSource() == _cancelButton) {
      //System.out.println("cancel");
      setVisible(false);
      dispose();
    }
  }
} /* end class ClassGenerationDialog */




class TableModelClassChecks extends AbstractTableModel {
  ////////////////
  // instance varables
  Vector _classes;
    ArrayList _languages;
    Set[] _checked;

  ////////////////
  // constructor
  public TableModelClassChecks() {
  }

  ////////////////
  // accessors
  public void setTarget(Vector classes, ArrayList languages) {
    _classes = classes;

    _languages = languages;
    _checked = new Set[getLanguagesCount()];
    for (int j = 0; j < getLanguagesCount(); j++)
	_checked[j] = new HashSet(); // Doesn't really matter what set we use.

    int size = _classes.size();
    for (int i = 0; i < size; i++) {
      MClassifier cls = (MClassifier) _classes.elementAt(i);
      String name = cls.getName();
      if (!(name.length() > 0))
	  continue;
      
      for (int j = 0; j < getLanguagesCount(); j++) {
	  // Needs-more-work:
	  // if (cls.isSupposedToBeGeneratedAsLanguage(_languages.index(j)))
	  //     _checked[j].add(cls);
	  // else
	  if (((NotationName)_languages.get(j))
	      .equals(Notation.getDefaultNotation())) {
	      _checked[j].add(cls);
	  }
      }
    }
    fireTableStructureChanged();
  }

    private int getLanguagesCount() {
	if (_languages == null)
	    return 0;
	return _languages.size();
    }

    public Set getChecked(NotationName nn) {
	int index = _languages.indexOf(nn);
	if (index == -1)
	    return new HashSet();
	return _checked[index];
    }

    /** All checked classes. Union of all languages.
     */
    public Set getChecked() {
	Set union = new HashSet();
	for (int i = 0; i < getLanguagesCount(); i++)
	    union.addAll(_checked[i]);
	return union;
    }

    ////////////////
    // TableModel implemetation
    public int getColumnCount() { return 1 + getLanguagesCount(); }

    public String  getColumnName(int c) {
	if (c == 0) return "Class Name";
	int langindex = c - 1;
	if (langindex >= 0 && langindex < getLanguagesCount())
	    return ((NotationName)_languages.get(langindex)).getConfigurationValue();
	return "XXX";
    }

    public Class getColumnClass(int c) {
	if (c == 0) return String.class;
	int langindex = c - 1;
	if (langindex >= 0 && langindex < getLanguagesCount())
	    return Boolean.class;
	return String.class;
    }

    public boolean isCellEditable(int row, int col) {
	MClassifier cls = (MClassifier) _classes.elementAt(row);
	if (col == 0)
	    return false;
	if (!(cls.getName().length() > 0))
	    return false;
	int langindex = col - 1;
	if (langindex >= 0 && langindex < getLanguagesCount())
	    return true;
	return false;
    }

    public int getRowCount() {
	if (_classes == null) return 0;
	return _classes.size();
    }

    public Object getValueAt(int row, int col) {
	MClassifier cls = (MClassifier) _classes.elementAt(row);
	int langindex = col - 1;
	if (col == 0) {
	    String name = cls.getName();
	    return (name.length() > 0) ? name : "(anon)";
	}
	else if (langindex >= 0 && langindex < getLanguagesCount()) {
	    return _checked[langindex].contains(cls) 
		? Boolean.TRUE 
		: Boolean.FALSE;
	}
	else
	    return "CC-r:" + row + " c:" + col;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)  {
	//System.out.println("setting table value " + rowIndex + ", " + columnIndex);
	if (columnIndex == 0) return;
	if (columnIndex >= getColumnCount()) return;
	if (!(aValue instanceof Boolean)) return;
	boolean val = ((Boolean)aValue).booleanValue();
	Object cls = _classes.elementAt(rowIndex);

	int langindex = columnIndex - 1;
	if (langindex >= 0 && langindex < getLanguagesCount()) {
	    if (val) _checked[langindex].add(cls);
	    else _checked[langindex].remove(cls);
	}
    }
} /* end class TableModelClassChecks */
