package org.argouml.profile.plugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.argouml.model.Model;
import org.argouml.profile.Profile;
import org.argouml.profile.ProfileException;
import org.argouml.profile.ProfileModelLoader;
import org.argouml.profile.ProfileReference;
import org.argouml.profile.ResourceModelLoader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads the profile descriptor from a file called "profile.xml" 
 * in the same directory as the reference class
 * 
 * @author maas
 */
public class PluginProfileLoaderImpl extends DefaultHandler implements PluginProfileLoader  {

	PluginProfileImpl plugin = null;
	Collection profileModel = null;
	
	Profile profile = new Profile() {
		@Override
		public String getDisplayName() {
			return plugin.getName();
		}

		@Override
		public Collection getProfilePackages() throws ProfileException {
			return profileModel;
		}
	};
	
	private Class referenceClass;
	
	private interface Tags {
		static final String PROFILE = "profile";
		static final String MODEL = "model"; 
	}
	
	private interface Atts {
		static final String PROFILE_AUTHOR = "author";
		static final String PROFILE_DESCRIPTION = "description";
		static final String PROFILE_NAME = "name";
		static final String PROFILE_VERSION = "version";
		static final String PROFILE_DOWNLOADSITE = "downloadsite"; 
		static final String MODEL_XMI = "xmi"; 
	}

	public PluginProfile loadProfile(Class cl) throws ErrorLoadingProfile {
		try {
			this.referenceClass = cl;
			
			XMLReader xr = XMLReaderFactory.createXMLReader();
			
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			
			xr.parse(new InputSource(new InputStreamReader(cl.getResourceAsStream("profile.xml"))));
			
		} catch (SAXException e) {
			throw new ErrorLoadingProfile();
		} catch (IOException e) {
			throw new ErrorLoadingProfile();
		}		
		
		return plugin;
	}

	public void startDocument() {
		plugin = new PluginProfileImpl();
	}
		
	public void startElement(String uri, String name, String qName, Attributes atts) {
			for(int i=0;i<atts.getLength();++i) {
				if (name.equalsIgnoreCase(Tags.PROFILE)) {
					if (atts.getLocalName(i).equalsIgnoreCase(Atts.PROFILE_AUTHOR)) {
						plugin.setAuthor(atts.getValue(i));
					} else if (atts.getLocalName(i).equalsIgnoreCase(Atts.PROFILE_DESCRIPTION)) {
						plugin.setDescription(atts.getValue(i));
					} else if (atts.getLocalName(i).equalsIgnoreCase(Atts.PROFILE_NAME)) {
						plugin.setName(atts.getValue(i));
					} else if (atts.getLocalName(i).equalsIgnoreCase(Atts.PROFILE_VERSION)) {
						plugin.setVersion(atts.getValue(i));
					} else if (atts.getLocalName(i).equalsIgnoreCase(Atts.PROFILE_DOWNLOADSITE)) {
						plugin.setDownloadSite(atts.getValue(i));
					}
				} else if (name.equalsIgnoreCase(Tags.MODEL)) {
					if (atts.getLocalName(i).equalsIgnoreCase(Atts.MODEL_XMI)) {
						try {
							loadProfileModel(atts.getValue(i));
						} catch (ProfileException e) {
							e.printStackTrace();
						}
					}
				}
			}
	}
	
	@SuppressWarnings("unchecked")
	private void loadProfileModel(String profile) throws ProfileException {
		ProfileModelLoader profileModelLoader = new ResourceModelLoader(referenceClass);
		try {
			profileModel = profileModelLoader.loadModel(new ProfileReference(
					profile, new URL("http://example.com/argouml/userprofiles/"
							+ profile)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (profileModel == null) {
			profileModel = new ArrayList();
			profileModel.add(Model.getModelManagementFactory().createModel());
		}
	}

	public void endElement(String uri, String name, String qName) {
		if (name.equalsIgnoreCase(Tags.PROFILE)) {
			plugin.setProfile(profile);
		}
	}

	private static PluginProfileLoaderImpl instance = null;
	public static PluginProfileLoader getInstance() {
		if (instance == null) {
			instance = new PluginProfileLoaderImpl();
		}
		return instance;
	}

}
