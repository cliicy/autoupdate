/**
 * 
 */
package com.ca.arcserve.edge.app.base.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAMonitorBackupVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAReplNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAReplNodeRole;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHARootDir;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenario;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenarioData;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenarioType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;

/**
 * @author lijwe02
 * 
 */
public class RHAServiceUtil {
	private static final Logger logger = Logger.getLogger(RHAServiceUtil.class);
	public static final String SCENARIO_TYPE_D2D = "D2D";
	public static final String SCENARIO_TYPE_HBBU = "HBBU";
	public static final String REPL_INDEX_MASTER = "1";
	public static final String REPL_INDEX_REPLICA = "2";

	public static List<RHAScenario> parseScenarioList(String strScenarioList) throws SAXException, IOException,
			ParserConfigurationException {
		List<RHAScenario> resultList = new ArrayList<RHAScenario>();
		if (StringUtil.isEmptyOrNull(strScenarioList)) {
			return resultList;
		}
		InputStream is = new ByteArrayInputStream(strScenarioList.getBytes("UTF-8"));
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		NodeList nodeList = XPathUtil.getNodeList(doc, "/scenarios/scenario");
		int count = nodeList.getLength();
		if (count == 0) {
			logger.info("No scenario exists.");
		}
		for (int i = 0; i < count; i++) {
			Node node = nodeList.item(i);
			String id = XPathUtil.getNodeValue(node, "@id");
			String name = XPathUtil.getNodeValue(node, "@name");
			RHAScenario scenario = new RHAScenario();
			scenario.setId(Long.valueOf(id));
			scenario.setName(name);
			resultList.add(scenario);
		}
		return resultList;
	}

	public static RHAScenarioData parseScenarioData(String strScenarioData) throws SAXException, IOException,
			ParserConfigurationException {
		if (StringUtil.isEmptyOrNull(strScenarioData)) {
			return null;
		}
		RHAScenarioData scenarioData = new RHAScenarioData();
		InputStream is = new ByteArrayInputStream(strScenarioData.getBytes("UTF-8"));
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		String scenarioId = XPathUtil.getNodeValue(doc, "/object[@label='Scenario']/data[@label='ScenarioID']/@val");
		scenarioData.setScenarioID(Long.parseLong(scenarioId));
		String scenarioName = XPathUtil
				.getNodeValue(doc, "/object[@label='Scenario']/data[@label='ScenarioName']/@val");
		scenarioData.setScenarioName(scenarioName);
		String d2dIntegration = XPathUtil.getNodeValue(doc,
				"/object[@label='Scenario']/data[@label='ARCserveD2DIntegrated']/@val");
		scenarioData.setD2dIntegrated("True".equalsIgnoreCase(d2dIntegration));
		if (scenarioData.isD2dIntegrated()) {
			// Parse D2D information
			String d2dNamePath = "/object[@label='Scenario']/data[@label='ARCserveD2DIntegrated']/data[@label='ARCserveD2DName']/@val";
			String d2dName = XPathUtil.getNodeValue(doc, d2dNamePath);
			scenarioData.setD2dName(d2dName);
		}
		String cntrlAppIntegrated = XPathUtil.getNodeValue(doc,
				"/object[@label='Scenario']/data[@label='ARCservCntrlAppIntegrated']/@val");
		scenarioData.setCntrlAppIntegrated("True".equalsIgnoreCase(cntrlAppIntegrated));
		if (scenarioData.isCntrlAppIntegrated()) {
			// HBBU integrated
			Node cntrlAppIntegratedNode = XPathUtil.getNode(doc,
					"/object[@label='Scenario']/data[@label='ARCservCntrlAppIntegrated']");
			List<RHAMonitorBackupVM> monitorBackupVMList = parseARCservCntrlAppIntegratedNode(cntrlAppIntegratedNode);
			scenarioData.setMonitorBackupVMList(monitorBackupVMList);
		}
		// Parse ReplicationTree
		Node replicationTreeNode = XPathUtil.getNode(doc, "/object[@label='Scenario']/title[@label='ReplicationTree']");
		NodeList replNodeList = XPathUtil.getNodeList(replicationTreeNode, "object[@label='ReplNode']");
		for (int i = 0; i < replNodeList.getLength(); i++) {
			Node replNode = replNodeList.item(i);
			RHAReplNode rhaReplNode = parseReplNode(replNode);
			scenarioData.addReplNode(rhaReplNode);
		}
		return scenarioData;
	}

	public static List<RHASourceNode> getRHASourceNodeList(RHAScenarioData scenarioData) {
		if (scenarioData == null) {
			logger.error("The scenarioData is null.");
			return null;
		}
		List<RHASourceNode> rhaSourceNodeList = new ArrayList<RHASourceNode>();
		if (scenarioData.isD2dIntegrated()) {
			if (StringUtil.isEmptyOrNull(scenarioData.getD2dName())) {
				logger.error("The d2d name is empty.");
				return null;
			}
			String nodeName = getNodeNameFromURL(scenarioData.getD2dName());
			List<RHAReplNode> replNodeList = scenarioData.getReplicationTree();
			for (RHAReplNode replNode : replNodeList) {
				List<RHAReplNode> replicaNodeList = getReplNodeListByRole(replNode, RHAReplNodeRole.Replica);
				for (RHAReplNode replicaNode : replicaNodeList) {
					List<RHARootDir> rootDirList = replicaNode.getRootDirList();
					if (rootDirList == null) {
						continue;
					}
					for (RHARootDir rootDir : rootDirList) {
						RHASourceNode sourceNode = getSourceNodeFromRootDir(nodeName, replicaNode, rootDir);
						if (sourceNode == null) {
							logger.info("Failed to get the source node, maybe the configuration of the scenario "
									+ scenarioData.getScenarioName() + " has some problem.");
							continue;
						}
						sourceNode.setScenarioId(scenarioData.getScenarioID());
						sourceNode.setScenarioName(scenarioData.getScenarioName());
						sourceNode.setScenarioType(RHAScenarioType.D2DIntegrated);
						sourceNode.setVmInstanceUUID("");
						sourceNode.setVmName("");
						rhaSourceNodeList.add(sourceNode);
					}
				}
			}
		} else if (scenarioData.isCntrlAppIntegrated()) {
			List<RHAMonitorBackupVM> vmList = scenarioData.getMonitorBackupVMList();
			List<RHAReplNode> replNodeList = scenarioData.getReplicationTree();
			List<RHAReplNode> masterReplNodeList = new ArrayList<RHAReplNode>();
			List<RHAReplNode> replicaReplNodeList = new ArrayList<RHAReplNode>();
			for (RHAReplNode replNode : replNodeList) {
				masterReplNodeList.addAll(getReplNodeListByRole(replNode, RHAReplNodeRole.Master));
				replicaReplNodeList.addAll(getReplNodeListByRole(replNode, RHAReplNodeRole.Replica));
			}
			for (RHAMonitorBackupVM vm : vmList) {
				String nodeName = vm.getBackupVMNodeName();
				String vmPath = vm.getBackupDestinationDir();
				String pathId = null;
				for (RHAReplNode masterReplNode : masterReplNodeList) {
					for (RHARootDir rootDir : masterReplNode.getRootDirList()) {
						if (vmPath.equalsIgnoreCase(rootDir.getPath())) {
							pathId = rootDir.getId();
							break;
						}
					}
					if (pathId != null) {
						break;
					}
				}
				if (pathId == null) {
					logger.error("didn't find pathid for path:" + vmPath);
					continue;
				}
				for (RHAReplNode replicaReplNode : replicaReplNodeList) {
					for (RHARootDir rootDir : replicaReplNode.getRootDirList()) {
						if (pathId.equalsIgnoreCase(rootDir.getId())) {
							RHASourceNode sourceNode = getSourceNodeFromRootDir(nodeName, replicaReplNode, rootDir);
							sourceNode.setScenarioId(scenarioData.getScenarioID());
							sourceNode.setScenarioName(scenarioData.getScenarioName());
							sourceNode.setScenarioType(RHAScenarioType.HBBUIntegrated);
							sourceNode.setVmInstanceUUID(vm.getVmInstanceUUID());
							sourceNode.setVmName(vm.getBackupVMName());
							rhaSourceNodeList.add(sourceNode);
						}
					}
				}
			}
		}
		return rhaSourceNodeList;
	}

	public static List<RHASourceNode> getRHASourceNodeListFromReplicationInfo(String replicationInfo)
			throws SAXException, IOException, ParserConfigurationException {
		if (StringUtil.isEmptyOrNull(replicationInfo)) {
			return null;
		}
		List<RHASourceNode> rhaSourceNodeList = new ArrayList<RHASourceNode>();
		RHASourceNode commonSourceNode = new RHASourceNode();
		InputStream is = new ByteArrayInputStream(replicationInfo.getBytes("UTF-8"));
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		String scenarioId = XPathUtil.getNodeValue(doc, "/scenario/@Id");
		commonSourceNode.setScenarioId(Long.parseLong(scenarioId));
		String scenarioName = XPathUtil.getNodeValue(doc, "/scenario/@Name");
		commonSourceNode.setScenarioName(scenarioName);
		String scenarioType = XPathUtil.getNodeValue(doc, "/scenario/@Type");
		if (SCENARIO_TYPE_D2D.equalsIgnoreCase(scenarioType)) {
			// D2D integrated scenario
			commonSourceNode.setScenarioType(RHAScenarioType.D2DIntegrated);
		} else if (SCENARIO_TYPE_HBBU.equalsIgnoreCase(scenarioType)) {
			commonSourceNode.setScenarioType(RHAScenarioType.HBBUIntegrated);
		} else {
			logger.error("Unsupported scenario type:" + scenarioType + " for scenario:" + scenarioName);
			return null;
		}

		// Parse master
		Node masterNode = XPathUtil.getNode(doc, "/scenario/ReplNode");
		String index = XPathUtil.getNodeValue(masterNode, "@Index");
		if (REPL_INDEX_MASTER.equals(index)) {
			String masterIp = XPathUtil.getNodeValue(masterNode, "@IP");
			String masterHost = XPathUtil.getNodeValue(masterNode, "@Host");
			// System.out.println("masterInfo:" + index + "\t" + masterIp + "\t" + masterHost);
			commonSourceNode.setMasterIp(masterIp);
			commonSourceNode.setMasterHost(masterHost);
		} else {
			logger.error("Failed to get master node from replication information:" + replicationInfo);
			return null;
		}

		// Parse replica
		Node replicaNode = XPathUtil.getNode(masterNode, "ReplNode");
		String replicaIndex = XPathUtil.getNodeValue(replicaNode, "@Index");
		if (REPL_INDEX_REPLICA.equalsIgnoreCase(replicaIndex)) {
			String replicaIp = XPathUtil.getNodeValue(replicaNode, "@IP");
			String replicaHost = XPathUtil.getNodeValue(replicaNode, "@Host");
			commonSourceNode.setReplicaIp(replicaIp);
			commonSourceNode.setReplicaHost(replicaHost);
		} else {
			logger.error("Failed to get replica node from replication information:" + replicationInfo);
			return null;
		}

		// Parse node
		NodeList nodeList = XPathUtil.getNodeList(replicaNode, "Node");
		if (nodeList == null) {
			logger.error("Failed to get Node element from replica element, the replNode is:" + replicaNode);
			return null;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String nodeName = XPathUtil.getNodeValue(node, "@Name");
			String vmName = XPathUtil.getNodeValue(node, "@VMName");
			String vmUUID = XPathUtil.getNodeValue(node, "@UUID");
			if (vmUUID == null) {
				vmUUID = "";
			}
			String hypervisor = XPathUtil.getNodeValue(node, "@Hypervisor");

			commonSourceNode.setNodeName(nodeName);
			commonSourceNode.setVmName(vmName);
			commonSourceNode.setVmInstanceUUID(vmUUID);
			commonSourceNode.setHypervisorName(hypervisor);

			boolean protectManyD2DNodes = false;

			NodeList rootDirList = XPathUtil.getNodeList(node, "ListRootDirs/RootDir");
			if (rootDirList == null) {
				logger.error("The root dir is empty, invalid replication information:" + replicationInfo);
				return null;
			}
			int rootDirCount = rootDirList.getLength();
			if (rootDirCount > 1) {
				if (commonSourceNode.getScenarioType() == RHAScenarioType.HBBUIntegrated) {
					logger.error("Many root dirs for HBBU integrated scenario. Invalid replication information:"
							+ replicationInfo);
					return null;
				}
				if (commonSourceNode.getScenarioType() == RHAScenarioType.D2DIntegrated) {
					protectManyD2DNodes = true;
				}
				if (logger.isInfoEnabled()) {
					logger.info("There are " + rootDirCount + " root dirs for d2d integrated scenario.");
				}
			}
			for (int j = 0; j < rootDirCount; j++) {
				RHASourceNode sourceNode = new RHASourceNode(commonSourceNode);
				Node rootDirNode = rootDirList.item(j);
				String rootDirName = XPathUtil.getNodeValue(rootDirNode, "@Name");
				if (rootDirName == null || rootDirName.trim().length() == 0) {
					logger.error("The root dir is empty, try to parse next root dir.");
					continue;
				}
				String filtered = XPathUtil.getNodeValue(rootDirNode, "@Filtered");
				if (filtered != null && "True".equalsIgnoreCase(filtered)) {
					logger.info("The rootDir:" + rootDirName + " for source node:" + commonSourceNode + " is filtered.");
					continue;
				} else {
					String converter = getConverterFromRootDirPath(rootDirName);
					if (converter == null) {
						converter = commonSourceNode.getReplicaHost();
					}
					if (protectManyD2DNodes) {
						sourceNode.setNodeName(getNodeNameFromRootDir(rootDirName));
						sourceNode.setConverter(converter);
					} else {
						sourceNode.setConverter(converter);
					}
					// if (StringUtil.isEmptyOrNull(sourceNode.getNodeName())) {
					// sourceNode.setNodeName(getNodeNameFromRootDir(rootDirName));
					// }
					String path = rootDirName;
					if (path != null) {
						path = path.replaceAll("\\/", "\\\\");
						while (path.endsWith("\\")) {
							path = path.substring(0, path.length() - 1);
						}
						if (path.length() == 0) {
							logger.error("The path root dir " + rootDirName + " is invalid.");
							continue;
						}
					}
					sourceNode.setRecoveryPointFolder(path);
					rhaSourceNodeList.add(sourceNode);
				}
			}
		}
		return rhaSourceNodeList;
	}

	private static List<RHAMonitorBackupVM> parseARCservCntrlAppIntegratedNode(Node node) {
		List<RHAMonitorBackupVM> vmList = new ArrayList<RHAMonitorBackupVM>();
		String cntrlAppName = XPathUtil.getNodeValue(node, "data[@label='ARCservCntrlAppName']/@val");
		Node listMonitorBackupVMNode = XPathUtil.getNode(node, "data[@label='ListMonitorBackupVM']");
		NodeList monitorBackupVMList = XPathUtil.getNodeList(listMonitorBackupVMNode,
				"object[@label='MonitorBackupVM']");
		for (int i = 0; i < monitorBackupVMList.getLength(); i++) {
			Node monitorBackupVMNode = monitorBackupVMList.item(i);
			String vmInstanceUUID = XPathUtil.getNodeValue(monitorBackupVMNode, "data[@label='VMInstanceUUID']/@val");
			String backupVMNodeName = XPathUtil.getNodeValue(monitorBackupVMNode,
					"data[@label='BackupVMNodeName']/@val");
			String backupDestinationDir = XPathUtil.getNodeValue(monitorBackupVMNode,
					"data[@label='BackupDestinationDir']/@val");
			String backupVMName = XPathUtil.getNodeValue(monitorBackupVMNode, "data[@label='BackupVMName']/@val");
			RHAMonitorBackupVM vm = new RHAMonitorBackupVM();
			vm.setCntrlAppName(cntrlAppName);
			vm.setBackupDestinationDir(backupDestinationDir);
			vm.setBackupVMNodeName(backupVMNodeName);
			vm.setVmInstanceUUID(vmInstanceUUID);
			vm.setBackupVMName(backupVMName);
			vmList.add(vm);
		}
		return vmList;
	}

	private static RHAReplNode parseReplNode(Node replNode) {
		if (replNode == null) {
			return null;
		}
		RHAReplNode rhaReplNode = new RHAReplNode();
		// Parse CommonHostProps
		Node commonHostPropsNode = XPathUtil.getNode(replNode, "object[@label='CommonHostProps']");
		if (commonHostPropsNode != null) {
			String role = XPathUtil.getNodeValue(commonHostPropsNode, "data[@label='Role']/@val");
			String host = XPathUtil.getNodeValue(commonHostPropsNode, "data[@label='Host']/@val");
			String ip = XPathUtil.getNodeValue(commonHostPropsNode, "data[@label='IP']/@val");
			rhaReplNode.setHost(host);
			rhaReplNode.setIp(ip);
			rhaReplNode.setRole(RHAReplNodeRole.valueOf(role));
			// Parse RootDir
			Node listRootDirsNode = XPathUtil.getNode(commonHostPropsNode, "data[@label='ListRootDirs']");
			NodeList rootDirNodeList = XPathUtil.getNodeList(listRootDirsNode, "object[@label='RootDir']");
			for (int i = 0; i < rootDirNodeList.getLength(); i++) {
				Node rootDirNode = rootDirNodeList.item(i);
				String path = XPathUtil.getNodeValue(rootDirNode, "data[@label='Path']/@val");
				String id = XPathUtil.getNodeValue(rootDirNode, "data[@label='ID']/@val");
				RHARootDir rootDir = new RHARootDir();
				rootDir.setId(id);
				rootDir.setPath(path);
				rhaReplNode.addRootDir(rootDir);
			}
			Node subReplNode = XPathUtil.getNode(replNode, "object[@label='ReplNode']");
			if (subReplNode != null) {
				RHAReplNode subRHAReplNode = parseReplNode(subReplNode);
				if (subRHAReplNode != null) {
					rhaReplNode.addSubReplNode(subRHAReplNode);
				}
			}
		} else {
			logger.error("Can't find CommonHostProps node.");
			return null;
		}
		return rhaReplNode;
	}

	private static RHASourceNode getSourceNodeFromRootDir(String nodeName, RHAReplNode replicaNode, RHARootDir rootDir) {
		RHASourceNode sourceNode = new RHASourceNode();
		sourceNode.setNodeName(nodeName);
		String converter = getConverterFromRootDirPath(rootDir.getPath());
		if (converter == null) {
			converter = replicaNode.getHost();
		}
		sourceNode.setConverter(converter);
		String path = rootDir.getPath();
		if (path == null || path.trim().length() == 0) {
			logger.error("The path is empty.");
			return null;
		}
		if (path != null) {
			path = path.replaceAll("\\/", "\\\\");
		}
		sourceNode.setRecoveryPointFolder(path);
		return sourceNode;
	}

	private static String getNodeNameFromURL(String url) {
		int index = url.indexOf("://");
		if (index > 0) {
			url = url.substring(index + 3);
		}
		index = url.indexOf(":");
		if (index > 0) {
			url = url.substring(0, index);
		}
		return url;
	}

	/**
	 * Expand all replNode with role
	 * 
	 * @param replNode
	 *            the replNode for check
	 * @param role
	 *            the role for check, if role is null, then all nodes will be returned.
	 * @return the nodes which was match for the role
	 */
	private static List<RHAReplNode> getReplNodeListByRole(RHAReplNode replNode, RHAReplNodeRole role) {
		if (replNode == null) {
			return null;
		}
		List<RHAReplNode> nodeList = new ArrayList<RHAReplNode>();
		if (role == null || replNode.getRole() == role) {
			nodeList.add(replNode);
		}
		// check sub nodes
		List<RHAReplNode> subReplNodeList = replNode.getSubReplNodeList();
		if (subReplNodeList != null && subReplNodeList.size() > 0) {
			for (RHAReplNode subReplNode : subReplNodeList) {
				List<RHAReplNode> subList = getReplNodeListByRole(subReplNode, role);
				if (subList != null) {
					nodeList.addAll(subList);
				}
			}
		}
		return nodeList;
	}

	private static String getConverterFromRootDirPath(String path) {
		if (path == null) {
			logger.error("The root dir path is null!");
			return null;
		}
		if (!(path.startsWith("\\\\") || path.startsWith("//"))) {
			return null;
		}
		path = path.replaceAll("\\\\", "/");
		path = path.substring(2);
		int slashIndex = path.indexOf("/");
		if (slashIndex > 0) {
			path = path.substring(0, slashIndex);
		}
		return path;
	}

	public static String getNodeNameFromRootDir(String rootDir) {
		if (StringUtil.isEmptyOrNull(rootDir)) {
			return null;
		}
		rootDir = rootDir.replaceAll("\\\\", "/");
		if (rootDir.endsWith("/")) {
			rootDir = rootDir.substring(0, rootDir.length() - 1);
		}
		int lastSperatorIndex = rootDir.lastIndexOf("/");
		if (lastSperatorIndex > 0) {
			return rootDir.substring(lastSperatorIndex + 1);
		}
		return null;
	}
}
