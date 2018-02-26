//October Sprint - Aravind
package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.StorageApplianceXMLParser;

public class StorageApplianceXMLDAO  extends XMLDAO {
	private StorageApplianceXMLParser storageApplianceXMLParser = new StorageApplianceXMLParser();

	synchronized public List<StorageAppliance> get(String filePath)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		List<StorageAppliance> config = storageApplianceXMLParser.loadXML(doc);

		for(StorageAppliance s : config)
			s.setPassword(CommonService.getInstance().getNativeFacade().decrypt(s.getPassword()));
		return config;
	}

	synchronized public void save(String filePath,
			StorageAppliance configuration) throws Exception {
		configuration.setPassword(CommonService.getInstance().getNativeFacade().encrypt(configuration.getPassword()));
		Document xmlDocument = storageApplianceXMLParser.saveXML(configuration);
		doc2XmlFile(xmlDocument, filePath);
		

	}
	
	synchronized public void save(String filePath,
			List<StorageAppliance> configuration) throws Exception {
		for(StorageAppliance s : configuration)
			s.setPassword(CommonService.getInstance().getNativeFacade().encrypt(s.getPassword()));
		Document xmlDocument = storageApplianceXMLParser.saveXML(configuration);
		File file = new File(filePath);
		//file.deleteOnExit();
		doc2XmlFile(xmlDocument, filePath);
		

	}
}
