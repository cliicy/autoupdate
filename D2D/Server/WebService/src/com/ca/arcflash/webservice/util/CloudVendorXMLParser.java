package com.ca.arcflash.webservice.util;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.backup.CloudVendor;


public class CloudVendorXMLParser {
	private static XMLBeanMapper<CloudVendor> cloudVendorMapper;
	public static final String POLICY_ELEMENT = "CloudVendor";
	public static final String POLICY_ROOT = "CloudVendors";
	
	static {
		try {
			cloudVendorMapper = new XMLBeanMapper<CloudVendor>(CloudVendor.class);
		} catch (Exception e) {
			Logger.getLogger(CloudVendorXMLParser.class).error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public Map<String, CloudVendor> loadXML(Document doc) throws Exception{
		Map<String, CloudVendor> cloudVendors = new Hashtable<String, CloudVendor>();
		Element rootEle = doc.getDocumentElement();		
		NodeList cloudVendorList = rootEle.getElementsByTagName(POLICY_ELEMENT);			
		for(int i = 0; i < cloudVendorList.getLength(); i ++) {
				Node node = cloudVendorList.item(i);				
				CloudVendor cloudVendor = cloudVendorMapper.loadBean(node);
				cloudVendors.put(cloudVendor.getVendorId()+"", cloudVendor);
			}	
		
		return cloudVendors;
	}
}
