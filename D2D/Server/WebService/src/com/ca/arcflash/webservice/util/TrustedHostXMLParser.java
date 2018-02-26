package com.ca.arcflash.webservice.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.TrustedHost;

public class TrustedHostXMLParser {
	private static XMLBeanMapper<TrustedHost> hostMapper;
	
	static {
		try {
			hostMapper = new XMLBeanMapper<TrustedHost>(TrustedHost.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document saveXML(TrustedHost[] hosts) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = doc.createElement("TrustedHosts");
		doc.appendChild(rootElement);
		
		List<Element> elements = hostMapper.saveBeans(Arrays.asList(hosts), doc);
		for(Element element : elements){
			rootElement.appendChild(element);
		}
		
		return doc;
	}
	
	public List<TrustedHost> loadXML(Document doc) throws Exception{
		NodeList backupNodeList = doc.getElementsByTagName("TrustedHost");
		return hostMapper.loadBeans(backupNodeList);
	}
}
