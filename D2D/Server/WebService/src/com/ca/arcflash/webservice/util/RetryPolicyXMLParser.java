package com.ca.arcflash.webservice.util;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;


public class RetryPolicyXMLParser {
	private static XMLBeanMapper<RetryPolicy> retryPolicyMapper;
	public static final String POLICY_ELEMENT = "RetryPolicy";
	public static final String POLICY_ROOT = "Policys";
	
	static {
		try {
			retryPolicyMapper = new XMLBeanMapper<RetryPolicy>(RetryPolicy.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document saveXML(RetryPolicy retryPolicy) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = retryPolicyMapper.saveBean(retryPolicy, doc,POLICY_ELEMENT);
		doc.appendChild(rootElement);
		return doc;
	}
	
	public Document saveXML(Map<String, RetryPolicy> policies) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement(POLICY_ROOT);
		doc.appendChild(root);
		for(RetryPolicy policy : policies.values()) {
			Element element = retryPolicyMapper.saveBean(policy, doc,POLICY_ELEMENT);
			root.appendChild(element);
		}
		return doc;
	}
	
	public Map<String, RetryPolicy> loadXML(Document doc) throws Exception{
		Map<String, RetryPolicy> policies = new Hashtable<String, RetryPolicy>();
		Element rootEle = doc.getDocumentElement();
		
		if(!rootEle.getNodeName().equals(POLICY_ROOT)){
			RetryPolicy retryPolicy = retryPolicyMapper.loadBean(rootEle);
			policies.put(CommonService.RETRY_BACKUP, retryPolicy);
			retryPolicy.setJobType(CommonService.RETRY_BACKUP);
			policies.put(CommonService.RETRY_CATALOG, CatalogService.getInstance().defaultRetryPolicy());
			CommonService.getInstance().saveRetryPolicy(policies);
		}else {
			NodeList retryPolicyNodeList = rootEle.getElementsByTagName(POLICY_ELEMENT);			
			for(int i = 0; i < retryPolicyNodeList.getLength(); i ++) {
				Node node = retryPolicyNodeList.item(i);				
				RetryPolicy retryPolicy = retryPolicyMapper.loadBean(node);
				policies.put(retryPolicy.getJobType(), retryPolicy);
			}
		}
		
		return policies;
	}
}
