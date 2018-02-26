package com.ca.arcflash.webservice.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;

public class VirtualCenterXMLParser {
	
	private static XMLBeanMapper<VirtualCenter> vcMapper;
	
	static {
		try {
			vcMapper = new XMLBeanMapper<VirtualCenter>(VirtualCenter.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document saveXML(VirtualCenter[] vcs) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = doc.createElement("VirtualCenters");
		doc.appendChild(rootElement);
		
		List<Element> elements = vcMapper.saveBeans(Arrays.asList(vcs), doc);
		for(Element element : elements){
			rootElement.appendChild(element);
		}
		
		return doc;
	}
	
	public List<VirtualCenter> loadXML(Document doc) throws Exception{
		NodeList vcNodeList = doc.getElementsByTagName("VirtualCenter");
		return vcMapper.loadBeans(vcNodeList);
	}

}
