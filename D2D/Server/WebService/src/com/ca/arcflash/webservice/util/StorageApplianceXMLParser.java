//October sprint - Aravind
package com.ca.arcflash.webservice.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.ca.arcflash.common.xml.XMLBeanMapper;
import java.util.Hashtable;
import java.util.Map;

import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;

public class StorageApplianceXMLParser {
	private static XMLBeanMapper<StorageAppliance> storageApplianceMapper;
	public static final String STORAGE_APPLIANCE_ELEMENT = "StorageAppliance";
	public static final String STORAGE_APPLIANCE_ROOT = "StorageApplianceList";
	
	static {
		try {			
			storageApplianceMapper = new XMLBeanMapper<StorageAppliance>(
					StorageAppliance.class);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document saveXML(StorageAppliance storageAppliance) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = storageApplianceMapper.saveBean(storageAppliance, doc,STORAGE_APPLIANCE_ELEMENT);
		doc.appendChild(rootElement);
		return doc;
	}
	
	public Document saveXML(List<StorageAppliance> storageApplianceList) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement(STORAGE_APPLIANCE_ROOT);
		doc.appendChild(root);

		for(StorageAppliance storageAppliance : storageApplianceList) {
			Element element = storageApplianceMapper.saveBean(storageAppliance, doc,STORAGE_APPLIANCE_ELEMENT);
			root.appendChild(element);
		}
		return doc;
	}
	
	public List<StorageAppliance> loadXML(Document doc) throws Exception{
		List<StorageAppliance> storageApplianceList= new LinkedList<StorageAppliance>();
		Element rootEle = doc.getDocumentElement();
		
		if(!rootEle.getNodeName().equals(STORAGE_APPLIANCE_ROOT)){
			StorageAppliance storageAppliance = storageApplianceMapper.loadBean(rootEle);
			storageApplianceList.add(storageAppliance);
			CommonService.getInstance().saveStorageAppliance(storageApplianceList);
		}else {
			NodeList storageAppliances = rootEle.getElementsByTagName(STORAGE_APPLIANCE_ELEMENT);			
			for(int i = 0; i < storageAppliances.getLength(); i ++) {
				Node node = storageAppliances.item(i);				
				StorageAppliance storageAppliance = storageApplianceMapper.loadBean(node);
				storageApplianceList.add(storageAppliance);
			}
		}
		
		return storageApplianceList;
	}
}

