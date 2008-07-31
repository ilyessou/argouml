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
import java.util.Dictionary;
import java.util.Hashtable;

import org.argouml.core.propertypanels.xml.XMLPropertyPanelsData;
import org.argouml.core.propertypanels.xml.XmlSinglePanelHandler;
import org.argouml.model.Model;
import org.argouml.uml.ui.PropPanel;
import org.argouml.uml.ui.PropPanelFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author penyaskito
 */
public class XMLPropPanelFactory implements PropPanelFactory {

    private final Dictionary<String, XMLPropertyPanelsData> cache;
    
    private static XMLPropPanelFactory instance;
    
    public static synchronized XMLPropPanelFactory getInstance() 
        throws Exception {
        if (instance == null) {
            instance = new XMLPropPanelFactory();
        }
        return instance;
    }
    
    private XMLPropPanelFactory() throws Exception {
        cache = new Hashtable<String, XMLPropertyPanelsData>();
        parseXML();
    }
    
    public PropPanel createPropPanel(Object target) {
        if (Model.getFacade().isAModelElement(target)) {
            XmlPropertyPanel panel =
                new XmlPropertyPanel("XML Property Panel", null);
            panel.build(target);
            return panel;
        } else {
            return null;
        }
    }
    
    private void parseXML() throws Exception {
        String file = "org/argouml/core/propertypanels/xml/panels.xml";        
        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(new XmlSinglePanelHandler(cache));
        InputStream stream = this.getClass().getClassLoader().
        getResourceAsStream(file);
        if (stream != null) {
            InputSource source = new InputSource(stream);
            parser.parse(source);        
        }       
    }
    
    public XMLPropertyPanelsData getPropertyPanelsData (String forType) {
        return cache.get(forType);
    }
    
    
}
