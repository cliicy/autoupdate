package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.util.RetryPolicyXMLParser;

public class RetryPolicyXMLDAO  extends XMLDAO {
	private RetryPolicyXMLParser retryPolicyXMLParser = new RetryPolicyXMLParser();

	synchronized public Map<String, RetryPolicy> get(String filePath)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		Map<String, RetryPolicy> config = retryPolicyXMLParser.loadXML(doc);

		return config;
	}

	synchronized public void save(String filePath,
			RetryPolicy configuration) throws Exception {
		
		Document xmlDocument = retryPolicyXMLParser.saveXML(configuration);
		
		doc2XmlFile(xmlDocument, filePath);
		

	}
	
	synchronized public void save(String filePath,
			Map<String,RetryPolicy> configuration) throws Exception {
		
		Document xmlDocument = retryPolicyXMLParser.saveXML(configuration);
		File file = new File(filePath);
		file.deleteOnExit();
		
		doc2XmlFile(xmlDocument, filePath);
		

	}
}
