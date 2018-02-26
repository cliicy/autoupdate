package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import org.w3c.dom.Document;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.util.ArchiveConfigXMLParser;
import com.ca.arcflash.webservice.util.SubscriptionConfigXMLParser;

public class SubscriptionConfigurationXMLDAO extends XMLDAO {
	private SubscriptionConfigXMLParser subscriptionConfigXMLParser = new SubscriptionConfigXMLParser();
	
	synchronized public void Save(String archiveConfigurationFilePath,SubscriptionConfiguration subscriptionConfig) throws Exception {
		Document xmlDocument = subscriptionConfigToXmlDocument(subscriptionConfig);
		doc2XmlFile(xmlDocument, archiveConfigurationFilePath);
	}
	
	public Document subscriptionConfigToXmlDocument( SubscriptionConfiguration subscriptionConfig ) throws Exception{
		Document xmlDocument = subscriptionConfigXMLParser.saveXML(subscriptionConfig);
		return xmlDocument;
	}

	public SubscriptionConfiguration get(String archiveConfigurationFilePath) throws Exception {
		File file = new File(archiveConfigurationFilePath);
		if (!file.exists())
			return null;
		SubscriptionConfiguration subscriptionConfig = null;
		subscriptionConfig = subscriptionConfigXMLParser.loadXML(archiveConfigurationFilePath);
		
		return subscriptionConfig;
	}
	
}
