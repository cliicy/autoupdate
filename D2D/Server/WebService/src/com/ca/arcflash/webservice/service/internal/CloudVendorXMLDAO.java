package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ca.arcflash.webservice.data.backup.CloudVendor;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.util.CloudVendorXMLParser;
import com.ca.arcflash.webservice.util.RetryPolicyXMLParser;

public class CloudVendorXMLDAO  extends XMLDAO {
	private CloudVendorXMLParser cloudVendorXMLParser = new CloudVendorXMLParser();

	synchronized public Map<String, CloudVendor> get(String filePath)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		Map<String, CloudVendor> config = cloudVendorXMLParser.loadXML(doc);

		return config;
	}
	
}
