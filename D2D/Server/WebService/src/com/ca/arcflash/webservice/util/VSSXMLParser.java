package com.ca.arcflash.webservice.util;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.vss.Application;
import com.ca.arcflash.webservice.data.vss.exchange.ExchangeComponent;
import com.ca.arcflash.webservice.data.vss.sql.SQLComponent;
import com.ca.arcflash.webservice.data.vss.sql.SQLInstance;

public class VSSXMLParser {
	
	private static XMLBeanMapper<SQLInstance> sqlInstanceMapper;
	private static XMLBeanMapper<ExchangeComponent> componentMapper;
	
	static {
		try {
			sqlInstanceMapper = new XMLBeanMapper<SQLInstance>(SQLInstance.class);
			componentMapper = new XMLBeanMapper<ExchangeComponent>(ExchangeComponent.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Application> parseXML(Document xmlDocument) throws Exception{
		List<Application> result = new LinkedList<Application>();
		
		NodeList applicationNodeList = xmlDocument.getElementsByTagName("AppInfo");
		
		for(int index = 0; index < applicationNodeList.getLength(); index++){
			Node node = applicationNodeList.item(index);
			Application application = null;
			
			String type = node.getAttributes().getNamedItem("AppType").getNodeValue();
			
			if ("0".equals(type)){
				XMLBeanMapper<Application> applicationMapper = new XMLBeanMapper<Application>(Application.class);
				application = applicationMapper.loadBean(node);
				NodeList instanceNodeList = node.getChildNodes();
				List<SQLInstance> instanceList = parseSQLInstances(instanceNodeList);
				application.setSqlInstances(instanceList.toArray(new SQLInstance[0]));
			}else{
				XMLBeanMapper<Application> applicationMapper = new XMLBeanMapper<Application>(Application.class);
				application = applicationMapper.loadBean(node);
				
				NodeList componentNodeList = node.getChildNodes();
				
				(application).setExchangeComponents(parseExchangeComponents(componentNodeList).toArray(new ExchangeComponent[0]));
			}
				
			
			if (application!=null)
				result.add(application);
		}
		
		return result;
	}
	
	private List<ExchangeComponent> parseExchangeComponents(NodeList instanceNodeList) throws Exception{
		List<ExchangeComponent> instanceList = new LinkedList<ExchangeComponent>();
		for(int indexInstance = 0; indexInstance < instanceNodeList.getLength(); indexInstance++){
			Node instanceNode = instanceNodeList.item(indexInstance);
			if (instanceNode.getNodeType() == Node.ELEMENT_NODE){
				ExchangeComponent instance = componentMapper.loadBean(instanceNode);
				instanceList.add(instance);
				
				NodeList componentNodeList = instanceNode.getChildNodes();
				instance.setComponents(parseItems(componentNodeList, ExchangeComponent.class).toArray(new ExchangeComponent[0]));
			}
		}
		
		return instanceList;
	}
	
	private List<SQLInstance> parseSQLInstances(NodeList instanceNodeList) throws Exception{
		List<SQLInstance> instanceList = new LinkedList<SQLInstance>();
		for(int indexInstance = 0; indexInstance < instanceNodeList.getLength(); indexInstance++){
			Node instanceNode = instanceNodeList.item(indexInstance);
			if (instanceNode.getNodeType() == Node.ELEMENT_NODE){
				SQLInstance instance = sqlInstanceMapper.loadBean(instanceNode);
				instanceList.add(instance);
				
				NodeList componentNodeList = instanceNode.getChildNodes();
				instance.setComponents(parseSQLComponents(componentNodeList).toArray(new SQLComponent[0]));
			}
		}
		
		return instanceList;
	}
	
	private List<SQLComponent> parseSQLComponents(NodeList nodeList) throws Exception{	
		return parseItems(nodeList, SQLComponent.class);
	}
	
	private <T> List<T> parseItems(NodeList nodeList, Class<T> tClass) throws Exception{
		List<T> result = new LinkedList<T>();
		XMLBeanMapper<T> mapper = new XMLBeanMapper<T>(tClass);
		
		for(int indexComponent = 0; indexComponent<nodeList.getLength();indexComponent++){
			Node componentNode = nodeList.item(indexComponent);
			if (componentNode.getNodeType() == Node.ELEMENT_NODE){
				T component = mapper.loadBean(componentNode);
				result.add(component);
			}
		}
		
		return result;
	}
}
