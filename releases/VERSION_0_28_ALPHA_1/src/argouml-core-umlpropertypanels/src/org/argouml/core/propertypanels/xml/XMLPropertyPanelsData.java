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

package org.argouml.core.propertypanels.xml;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Contains the data read on the XML file.
 *
 * @author penyaskito
 */
public class XMLPropertyPanelsData  {
    
    /**
     * Logger.
     */
    private static final Logger LOG = 
        Logger.getLogger(XMLPropertyPanelsData.class);
        
    /**
     * The info of the properties in the XML.
     */
    private List<XMLPropertyPanelsDataRecord> properties;
    
    /**
     * The info of the panels in the XML.
     */
    private XMLPropertyPanelsDataRecord panel;

    
    public XMLPropertyPanelsData() {
        properties = new LinkedList<XMLPropertyPanelsDataRecord>();
    }
    
    public void addProperty(XMLPropertyPanelsDataRecord record) {
        properties.add(record);
    }
    
    public void addPanel(XMLPropertyPanelsDataRecord record) {
        if (panel != null) {
            LOG.error("You tried to add a panel "
            		+ "when a previous one exist.");            
        }
        panel = record;
    }
    
    public String getTitle() {
        return panel.getName();
    }    
    
    public List<XMLPropertyPanelsDataRecord> getProperties () {
        return Collections.unmodifiableList(properties);
    }
}

