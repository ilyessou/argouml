/* $Id:  $
 *****************************************************************************
 * Copyright (c) 2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
 *****************************************************************************
 */

package org.argouml.core.propertypanels.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The cache of property panel metadata
 *
 * @author Bob Tarling
 */
public class MetaDataCache {
    
    private static final Logger LOG = Logger.getLogger(MetaDataCache.class);
    
    private Map<Class<?>, PanelData> cache =
        new HashMap<Class<?>, PanelData>();
    
    private Map<String, Class<?>> metaTypeByName;
    private Map<Class<?>, String> nameByMetaType;

    public MetaDataCache() throws Exception {
        Document doc = getDocument();
        NodeList nl = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            if (n.getNodeName().equals("classes")) {
        	metaTypeByName = getClasses((Element) n);
        	
        	nameByMetaType = new HashMap<Class<?>, String>(metaTypeByName.size());
        	for (Map.Entry<String, Class<?>> s : metaTypeByName.entrySet()) {
        	    nameByMetaType.put(s.getValue(), s.getKey());
        	}
        	
            } else if (n.getNodeName().equals("panels")) {
        	cache = getPanels((Element) n);
            }
        }
    }
    
    public PanelData get(Class<?> clazz) {
	Class<?>[] interfaces = clazz.getInterfaces();
	
	for (Class interfaze : interfaces) {
	    PanelData pd = cache.get(interfaze);
	    if (pd != null) {
		return pd;
	    }
	}
        return null;
    }
    
    private Document getDocument() throws IOException, DOMException, ParserConfigurationException, SAXException {
        final String filename;
        if (Model.getFacade().getUmlVersion().charAt(0) == '2') {
            filename = "org/argouml/core/propertypanels/model/metamodel2.xml";
        } else {
            filename = "org/argouml/core/propertypanels/model/metamodel.xml";
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        InputSource inputSource = new InputSource(inputStream);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(inputSource);
    }
    
    private HashMap<String, Class<?>> getClasses(Element classesNode) {
        final HashMap<String, Class<?>> map =
            new HashMap<String, Class<?>>(
        	    classesNode.getChildNodes().getLength());
        final NodeList nl = classesNode.getElementsByTagName("class");
        for (int i = 0; i < nl.getLength(); ++i) {
            Node classNode = nl.item(i);
            String className = classNode.getTextContent();
            try {
                final String name = 
        	    classNode.getAttributes().getNamedItem("name").getNodeValue();
                map.put(name, Class.forName(className));
            } catch (ClassNotFoundException e) {
        	    LOG.error("Class not found " + className, e);
            }
        }
        return map;
    }
    
    private Map<Class<?>, PanelData> getPanels(Element panelsNode) {
	
        final Map<Class<?>, PanelData> map =
            new HashMap<Class<?>, PanelData>();
        
        final NodeList panelNodes = panelsNode.getElementsByTagName("panel");
        for (int i = 0; i < panelNodes.getLength(); ++i) {
            
            Element panelNode = (Element) panelNodes.item(i);
            final String name = 
    	        panelNode.getAttributes().getNamedItem("name").getNodeValue();
            Class<?> clazz = metaTypeByName.get(name);

            if (clazz == null) {
                LOG.warn("No class name translation found for panel: " + name);
            } else {
                PanelData pm = new PanelData(clazz, name);
                map.put(clazz, pm);
                
                final NodeList controlNodes = panelNode.getElementsByTagName("*");
                for (int j = 0; j < controlNodes.getLength(); ++j) {
                    Element controlNode = (Element) controlNodes.item(j);
                    
                    final String propertyName = controlNode.getAttribute("name");
                    final String label = controlNode.getAttribute("label");
                    
                    final ControlData controlData =
                        new ControlData(controlNode.getTagName(), propertyName, label);
                    
                    final String types = controlNode.getAttribute("type");
                    StringTokenizer st = new StringTokenizer(types, ",");
                    while (st.hasMoreTokens()) {
                        controlData.addType(metaTypeByName.get(st.nextToken()));
                    }
                    
                    if (controlNode.getTagName().equals("checkgroup")) {
                        addCheckboxes(controlData, controlNode);
                    }
                    pm.addControlData(controlData);
                }
            }
        }
	    
        return map;
    }
    
    private void addCheckboxes(ControlData controlData, Element controlElement) {
        final NodeList checkBoxElements =
            controlElement.getElementsByTagName("checkbox");
        for (int i = 0; i < checkBoxElements.getLength(); ++i) {
            Element cbNode = (Element) checkBoxElements.item(i);
            
            final String checkBoxType =
        	cbNode.getAttributes().getNamedItem("type").getNodeValue();
            final String checkBoxName = 
    	        cbNode.getAttributes().getNamedItem("name").getNodeValue();
            
            CheckBoxData cbd =
        	new CheckBoxData(metaTypeByName.get(checkBoxType), checkBoxName);
            controlData.addCheckbox(cbd);
        }
    }
}
