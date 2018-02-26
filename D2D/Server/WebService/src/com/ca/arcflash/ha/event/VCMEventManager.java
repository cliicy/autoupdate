package com.ca.arcflash.ha.event;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.datasync.VCMSyncer;

public class VCMEventManager {
	
	private static final Logger logger = Logger.getLogger(VCMEventManager.class);
	
	private VCMEventManager() {
	}

	public static VCMEventManager getInstance() {
		return new VCMEventManager();
	}

	public void saveVCMEvent(String afGuid, VCMEvent event)
			throws VCMEventException {

		String vcmEventFilePath = getVCMEventFileFullPath(afGuid);

		File vcmEventFile = new File(vcmEventFilePath);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new VCMEventException(e.getMessage());
		}

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new VCMEventException(e.getMessage());
		}

		if (!vcmEventFile.exists()) {

			try {
				vcmEventFile.createNewFile();
			} catch (IOException e) {
				throw new VCMEventException(e.getMessage());
			}

			doc = builder.newDocument();
			Element root = doc.createElement("VCMEventCollection");
			doc.appendChild(root);
			Element eventEvent = constructVCMElement(doc, event);
			root.appendChild(eventEvent);

		} else {

			try {
				doc = builder.parse(vcmEventFile);
				Node root = doc.getElementsByTagName("VCMEventCollection").item(0);
				Element eventElement = constructVCMElement(doc,event);
				root.appendChild(eventElement);
			} catch (SAXException e) {
				throw new VCMEventException(e.getMessage());
			} catch (IOException e) {
				throw new VCMEventException(e.getMessage());
			}
		}
		
		Source source = new DOMSource(doc);
		Result result;
		try {
			result = new StreamResult(new FileOutputStream(vcmEventFile));
		} catch (FileNotFoundException e) {
			throw new VCMEventException(e.getMessage());
		}
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new VCMEventException(e.getMessage());
		}

		//sonle01 keep Event into sync cache file
		VCMSyncer syncer = new VCMSyncer();
		if(0 != syncer.SaveVCMEvent2CacheFile(event)) {
			logger.debug("syncer.SaveVCMEvent2CacheFile() failed!(taskGuid = " 
					+ event.getTaskGuid() + ")");
		}
		else {
			logger.debug("syncer.SaveVCMEvent2CacheFile() succeeded!(taskGuid = " 
					+ event.getTaskGuid() + ")");
			
			EdgeDataSynchronization.SetSyncDataFlag();
		}
		
	}
	
	public Collection<VCMEvent> getVCMEvent(String afGuid) throws VCMEventException{
		
		String dirPath = getVCMEventFileDirPath(afGuid);
		File dir = new File(dirPath);
		if(!dir.exists()){
			throw new VCMEventException("no event history.");
		}
		
		File[] vcmEventFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().indexOf(CommonUtil.VCM_EVENT_HISTORY_FILE) != -1){
					return true;
				}
				return false;
			}
		});
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new VCMEventException(e.getMessage());
		}
		
		List<VCMEvent> eventList = new LinkedList<VCMEvent>();
		
		for (File file : vcmEventFiles) {
			try {
				Document doc = builder.parse(file);
				NodeList nodes = doc.getChildNodes();
				eventList.addAll(constructVCMEventsFromNodeList(nodes));
			} catch (SAXException e) {
				throw new VCMEventException(e.getMessage());
			} catch (IOException e) {
				throw new VCMEventException(e.getMessage());
			}
		}
		
		return eventList;
		
	}
	
	public VCMEvent getVCMEvent(String afGuid, String taksGuid)
			throws VCMEventException {

		String vcmEventFilePath = getVCMEventFileFullPath(afGuid);

		File vcmEventFile = new File(vcmEventFilePath);

		if (!vcmEventFile.exists()) {
			throw new VCMEventException("no such file" + vcmEventFilePath);
		}
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			doc = builder.parse(vcmEventFile);
		} catch (ParserConfigurationException e) {
			throw new VCMEventException(e.getMessage());
		} catch (SAXException e) {
			throw new VCMEventException(e.getMessage());
		} catch (IOException e) {
			throw new VCMEventException(e.getMessage());
		}
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		String expStr = String.format("//VCMEvent[contains(.,%s)]",new Object[]{"'" + taksGuid + "'"});
		XPathExpression expression;
		try {
			expression = xpath.compile(expStr);
			NodeList nodes = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
			System.out.println(nodes.getLength());
			VCMEvent event = constructVCMEventFromNode(nodes.item(0));
			return event;
		} catch (XPathExpressionException e) {
			throw new VCMEventException(e.getMessage());
		}
		
	}
	
	private String getVCMEventFileFullPath(String afGuid){
		
		String vcmEventDirPath = getVCMEventFileDirPath(afGuid);
		
		File vcmEventDir = new File(vcmEventDirPath);
		if (!vcmEventDir.exists()) {
			vcmEventDir.mkdirs();
		}

		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(new Date());
		
		String vcmEventFilePath = vcmEventDirPath + "\\"
				+ CommonUtil.VCM_EVENT_HISTORY_FILE + "-" + date +".xml";

		return vcmEventFilePath;
		
	}
	
	private String getVCMEventFileDirPath(String afGuid){
		
		String d2dPath = CommonUtil.D2DInstallPath;
		if (!d2dPath.endsWith("\\")) {
			d2dPath += "\\";
		}
		String vcmEventDirPath = d2dPath + CommonUtil.VCM_EVENT_HISTORY_DIR
				+ "\\" + afGuid;
		
		return vcmEventDirPath;
		
	}

	private Element createElement(Document doc, String name, String value) {

		Element attribute = doc.createElement(name);
		if(value != null){
			Text txtValue = doc.createTextNode(value);
			attribute.appendChild(txtValue);
		}
		return attribute;

	}
	
	private Collection<VCMEvent> constructVCMEventsFromNodeList(NodeList nodes){
		List<VCMEvent> vcmEventList = new LinkedList<VCMEvent>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			NodeList eventNode = node.getChildNodes();
			for (int j = 0; j < eventNode.getLength(); j++) {
				Node fieldNode = eventNode.item(j);
				vcmEventList.add(constructVCMEventFromNode(fieldNode));
			}
		}
		return vcmEventList;
	}
	
	private VCMEvent constructVCMEventFromNode(Node node){
	
		VCMEvent event = new VCMEvent();
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node tmp = list.item(i);
			String name = tmp.getNodeName();
			String value = tmp.getTextContent();
			if(StringUtil.isEmptyOrNull(value)){
				continue;
			}
			if(name.equals("taskGuid")){
				event.setTaskGuid(value);
			}else if(name.equals("taskName")){
				event.setTaskName(value);
			}else if(name.equals("taskType")){
				event.setTaskType(value);
			}else if(name.equals("startTime")){
				if(!StringUtil.isEmptyOrNull(value)){
					Date startTime = new Date(Long.valueOf(value));
					event.setStartTime(startTime);
				}
			}else if(name.equals("endTime")){
				if(!StringUtil.isEmptyOrNull(value)){
					Date endTime = new Date(Long.valueOf(value));
					event.setEndTime(endTime);
				}
			}else if(name.equals("srcHostName")){
				event.setSrcHostName(value);
			}else if(name.equals("srcVMName")){
				event.setSrcVMName(value);
			}else if(name.equals("srcVirtualCenterName")){
				event.setSrcVirtualCenterName(value);
			}else if(name.equals("srcVMUUID")){
				event.setSrcVMUUID(value);
			}else if(name.equals("srcVMType")){
				event.setSrcVMType(value);
			}else if(name.equals("destHostName")){
				event.setDestHostName(value);
			}else if(name.equals("destVMName")){
				event.setDestVMName(value);
			}else if(name.equals("destVirtualCenterName")){
				event.setDestVirtualCenterName(value);
			}else if(name.equals("destVMUUID")){
				event.setDestVMUUID(value);
			}else if(name.equals("destVMType")){
				event.setDestVMType(value);
			}else if(name.equals("status")){
				event.setStatus(value);
			}else if(name.equals("statusComment")){
				event.setStatusComment(value);
			}else if(name.equals("vcmMonitorHost")){
				event.setVcmMonitorHost(value);
			}else if(name.equals("isProxy")){
				if(value.equalsIgnoreCase("true"))
					event.setProxy(true);
				else
					event.setProxy(false);
			}else if(name.equals("afGuid")){
				event.setAfGuid(value);
			}else if(name.equals("jobID")){
				String strValue = (value==null)?"0":value;
				event.setJobID(Long.valueOf(strValue));
			}
		}
		
		return event;
		
	}
	
	
	private Element constructVCMElement(Document doc, VCMEvent event) {

		Element eventRoot = doc.createElement("VCMEvent");

		Element attribute = createElement(doc, "taskGuid", event.getTaskGuid());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "taskName", event.getTaskName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "taskType", event.getTaskType());
		eventRoot.appendChild(attribute);
		if (event.getStartTime() != null) {
			attribute = createElement(doc, "startTime", event.getStartTime()
					.getTime()
					+ "");
		} else {
			attribute = createElement(doc, "startTime", "");
		}
		eventRoot.appendChild(attribute);

		if (event.getEndTime() != null) {
			attribute = createElement(doc, "endTime", event.getEndTime()
					.getTime()
					+ "");
		} else {
			attribute = createElement(doc, "endTime", "");
		}
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "srcHostName", event.getSrcHostName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "srcVMName", event.getSrcVMName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "srcVirtualCenterName", event
				.getSrcVirtualCenterName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "srcVMUUID", event.getSrcVMUUID());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "srcVMType", event.getSrcVMType());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "destHostName", event.getDestHostName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "destVMName", event.getDestVMName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "destVirtualCenterName", event
				.getDestVirtualCenterName());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "destVMUUID", event.getDestVMUUID());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "destVMType", event.getDestVMType());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "status", event.getStatus());
		eventRoot.appendChild(attribute);

		attribute = createElement(doc, "statusComment", event
				.getStatusComment());
		eventRoot.appendChild(attribute);
		
		attribute = createElement(doc, "vcmMonitorHost", event.getVcmMonitorHost());
		eventRoot.appendChild(attribute);
		
		attribute = createElement(doc, "isProxy", event.isProxy()==true?"true":"false");
		eventRoot.appendChild(attribute);
		
		attribute = createElement(doc, "afGuid", event.getAfGuid());
		eventRoot.appendChild(attribute);
		
		attribute = createElement(doc, "jobID", event.getJobID()+"");
		eventRoot.appendChild(attribute);

		return eventRoot;

	}

	
	public static void main(String[] args) throws VCMEventException {

		VCMEventManager manager =VCMEventManager.getInstance();
		VCMEvent event = new VCMEvent();

		event.setTaskGuid("testGUID");
		event.setTaskName("testTaskName");
		event.setStartTime(new Date());
		event.setDestHostName("dummy dest Host Name");
		event.setDestVirtualCenterName("dummy dest virtual Center Name");
		event.setDestVMName("dummy dest VM name");
		event.setDestVMType("1");
		event.setDestVMUUID("1234567890");
		event.setEndTime(new Date());
		event.setSrcHostName("dummy src Host Name");
		event.setSrcVirtualCenterName("dummy src virtual Center Name");
		event.setSrcVMName("dummy src VM Name");
		event.setSrcVMType("2");
		event.setSrcVMUUID("0987654321");
		event.setStatus("1");
		event.setStatusComment("No Comments");
		event.setTaskType("offline copy");
		event.setVcmMonitorHost("vcmMonitorHost");
		event.setProxy(true);
		event.setJobID(1000);
		event.setAfGuid(java.util.UUID.randomUUID().toString());

		manager.saveVCMEvent("fake-guid123", event);
		
	}

}
