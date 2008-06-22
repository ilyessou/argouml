// $Id$
// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argouml.core.propertypanels.panel;

import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.argouml.core.propertypanels.xml.XMLPropertyPanelsData;
import org.argouml.core.propertypanels.xml.XMLPropertyPanelsHandler;
import org.argouml.uml.ui.UMLTextField2;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

// TODO: This class will be an interface or abstract class
// and will be implemented by SwingUIFactory and SwtUIFactory.
/**
 * Creates the XML Property panels
 * <<factory>><<singleton>>
 */
public class UIFactory {
    
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(UIFactory.class);
    
    private static UIFactory instance = new UIFactory();
    
    public UIFactory() {
        
    }
    
    public static UIFactory getInstance() {        
        return instance;
    }
    
    // TODO: This will be a template method, where there will be two
    // implementations, for Swing and for SWT.
    // TODO: This should take care of diagrams? or only model
    // elements?
    /**
     * Creates a panel based on the target, using a XML that
     * describes the UI of the panel.     * 
     * @param target The model element selected
     * @return A Panel to be added to the main panel
     * @throws Exception If something goes wrong
     */
    public JPanel createGUI (Object target) throws Exception {
        String filename = getXMLFileName(target);
        LOG.info("[XMLPP] filename is:" + filename);
        XMLPropertyPanelsData data = parseXML(filename);
        JPanel panel = buildPanel(data, target);
        return panel;       
    }
    
    private JPanel buildPanel(XMLPropertyPanelsData data, Object target) {
        
        JPanel panel = new JPanel();
               
        for (String prop : data.getProperties()) {
            JLabel label = new JLabel(prop);
            GenericUMLPlainTextDocument document = 
                new GenericUMLPlainTextDocument(prop);
            document.setTarget(target);
            JTextField tfield = 
                new UMLTextField2(document);
            tfield.setColumns(80);
            panel.add(label);
            panel.add(tfield);
            panel.add(new JSeparator());
        }
        
        return panel;
    }

    private String getXMLFileName(Object target) {
        String classname = target.getClass().getSimpleName();
        // TODO: I don't like this hack, it may exist a better way.
        return classname.replace("$Impl", "");
    }

    private XMLPropertyPanelsData parseXML(String filename) 
        throws Exception {
        
        XMLPropertyPanelsData data = new XMLPropertyPanelsData();
        
        // TODO: I have to investigate how to read the XML.
        // There are some different APIs available, but
        // I'll choose SAX because it's the one API used in
        // PGML, so we don't have different APIs in Argo.
        XMLReader parser = XMLReaderFactory.createXMLReader(
                "org.apache.xerces.parsers.SAXParser"              
        );
        parser.setContentHandler(new XMLPropertyPanelsHandler(data));

        String file = "org/argouml/core/propertypanels/xml/"
            + filename + ".xml";
        LOG.info("File = " + file);
        InputStream stream = this.getClass().getClassLoader().
            getResourceAsStream(file);
        if (stream != null) {
            InputSource source = new InputSource(stream);
            parser.parse(source);        
        }
        return data;
    }
    

}
