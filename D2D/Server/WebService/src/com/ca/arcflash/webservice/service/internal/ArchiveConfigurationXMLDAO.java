package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import org.w3c.dom.Document;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.util.ArchiveConfigXMLParser;

public class ArchiveConfigurationXMLDAO extends XMLDAO {
	private ArchiveConfigXMLParser archiveConfigXMLParser = new ArchiveConfigXMLParser();
	
	synchronized public void Save(String archiveConfigurationFilePath,
			ArchiveConfiguration archiveConfig) throws Exception 
	{
		Document xmlDocument = archiveConfigToXmlDocument(archiveConfig);
		doc2XmlFile(xmlDocument, archiveConfigurationFilePath);
	}
	
	public Document archiveConfigToXmlDocument( ArchiveConfiguration archiveConfig ) throws Exception
	{
		/*if(!archiveConfig.isbPurgeScheduleAvailable())//purge schedule.
		{
			archiveConfig.setbPurgeArchiveItems(true);
			archiveConfig.setiPurgeAfterDays(1);
			archiveConfig.setlPurgeStartTime(0);//12 am by default.
		}*/
		Document xmlDocument = archiveConfigXMLParser.saveXML(archiveConfig);
		
		return xmlDocument;
	}

	public ArchiveConfiguration get(String archiveConfigurationFilePath) throws Exception {
		File file = new File(archiveConfigurationFilePath);
		if (!file.exists())
			return null;
		ArchiveConfiguration archiveConfig = null;
		/*DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		ArchiveConfiguration archiveConfig = null;
		db = dbf.newDocumentBuilder();
		
		Document doc = db.parse(file);*/
		archiveConfig = archiveConfigXMLParser.loadXML(archiveConfigurationFilePath);
		
		return archiveConfig;
	}
	
	//this api is used to save archive sourceinfo alone to xml file to read all the archivable files information
	public boolean saveSourceInfo(String in_ArchivePoliciesXMLPath, ArchiveSourceInfoConfiguration[] in_ArchiveSourceInfo) throws Exception{
		File file = new File(in_ArchivePoliciesXMLPath);
		if (file.exists())
			file.delete();
		
		Document xmlDocument = archiveConfigXMLParser.saveArchiveSourcesXML(in_ArchiveSourceInfo);
		
		doc2XmlFile(xmlDocument, in_ArchivePoliciesXMLPath);
		return true;
	}

}
