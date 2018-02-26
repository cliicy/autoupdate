package com.ca.arcflash.webservice.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;

public class ScheduledExportConfigXMLParser {
	private static XMLBeanMapper<ScheduledExportConfiguration> scheduledExportConfigMapper;
	
	private static final String SCHEDULEDEXPORTCONFIGURATION = "ScheduledExportConfiguration";
	
	
	static {
		try {
		scheduledExportConfigMapper = new XMLBeanMapper<ScheduledExportConfiguration>(ScheduledExportConfiguration.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document saveXML(ScheduledExportConfiguration config) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = scheduledExportConfigMapper.saveBean(config, doc);
		doc.appendChild(rootElement);
		
		return doc;
	}
	
	public ScheduledExportConfiguration loadXML(Document doc) throws Exception {
		ScheduledExportConfiguration scheduledExportConfig = null;
		
		NodeList scheduledExportNodeList = doc.getElementsByTagName(SCHEDULEDEXPORTCONFIGURATION);
		
		if (scheduledExportNodeList.getLength()>0)
			scheduledExportConfig = scheduledExportConfigMapper.loadBean(scheduledExportNodeList.item(0));
		
		if (scheduledExportConfig == null)
			return null;

		return scheduledExportConfig;
	}
	
	public ScheduledExportConfiguration loadXML(String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		
		return loadXML(doc);
	}

}
