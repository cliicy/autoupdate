/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.node;

import static com.ca.arcserve.edge.app.base.webservice.client.IWebServiceFactory.CONNECT_TIMEOUT;
import static com.ca.arcserve.edge.app.base.webservice.client.IWebServiceFactory.REQUEST_TIMEOUT;
import static com.ca.arcserve.edge.app.base.webservice.client.IWebServiceFactory.TIME_OUT_VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeRHADao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.common.RHAServiceUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.INodeRHAService;
import com.ca.arcserve.edge.app.base.webservice.IPolicyManagementService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenario;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenarioType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMConverterType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAParameters;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAResult;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.rha.service.XosoapapiC;
import com.ca.arcserve.rha.service.XosoapapiCSoap;

/**
 * @author lijwe02
 * 
 */
public class NodeRHAServiceImpl implements INodeRHAService {

	private static final Logger logger = Logger.getLogger(NodeRHAServiceImpl.class);
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeRHADao rhaDao = DaoFactory.getDao(IEdgeRHADao.class);
	private IEdgeConnectInfoDao conInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IActivityLogService logService = new ActivityLogServiceImpl();
	private static Map<RHAControlService, XosoapapiCSoap> SOAPSERVICE_MAP = new Hashtable<RHAControlService, XosoapapiCSoap>();
	private IEdgeVSBDao vsbDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ca.arcserve.edge.app.base.webservice.INodeRHAService#getScenarioList(com.ca.arcserve.edge.app.base.webservice
	 * .contract.node.RHAControlService)
	 */
	@Override
	public List<RHAScenario> getScenarioList(RHAControlService controlService) throws EdgeServiceFault {
		validateControlService(controlService);
		try {
			XosoapapiCSoap soapService = getSoapService(controlService);
			Long sessionId = getSessionId(soapService, controlService);
			String scenarioList = soapService.getScenarioList(sessionId);
			closeSession(soapService, sessionId);
			return RHAServiceUtil.parseScenarioList(scenarioList);
		} catch (NumberFormatException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (DOMException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (SAXException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (IOException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (ParserConfigurationException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (WebServiceException e) {
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToConnectRHAControlService, "");
		} catch (Exception e) {
			if (e instanceof EdgeServiceFault) {
				throw (EdgeServiceFault) e;
			}
			logger.error("Failed to get scenario list.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		}
	}

	@Override
	public List<RHASourceNode> getSourceNodeList(RHAControlService controlService) throws EdgeServiceFault {
		validateControlService(controlService);
		try {
			List<RHASourceNode> resultList = new ArrayList<RHASourceNode>();
			List<RHAScenario> scenarioList = getScenarioList(controlService);
			XosoapapiCSoap soapService = getSoapService(controlService);
			Long sessionId = getSessionId(soapService, controlService);
			for (RHAScenario scenario : scenarioList) {
				Holder<Boolean> executeResult = new Holder<Boolean>();
				Holder<String> returnValueHolder = new Holder<String>();
				Holder<String> errMessages = new Holder<String>();
				soapService.getD2DReplicationInfoInScenario(sessionId, scenario.getId(), executeResult,
						returnValueHolder, errMessages);
				if (!executeResult.value && errMessages.value != null) {
					logger.error("Error on fetch replication information for scenario:" + scenario.getName()
							+ " executeResult=" + executeResult.value + " errorMessage is:" + errMessages.value
							+ " and return value is:" + returnValueHolder.value);
					continue;
				}
				String replicationInfo = returnValueHolder.value;
				List<RHASourceNode> sourceNodeList = RHAServiceUtil
						.getRHASourceNodeListFromReplicationInfo(replicationInfo);
				if (sourceNodeList == null) {
					logger.info("Failed to get the source node list for scenario:" + scenario.getName());
					continue;
				}
				resultList.addAll(sourceNodeList);
			}
			closeSession(soapService, sessionId);
			// Fill Configuration field
			checkNodeStatus(controlService, resultList);
			return resultList;
		} catch (SAXException e) {
			logger.error("Failed to parse source node.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (IOException e) {
			logger.error("Failed to parse source node.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse source node.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		} catch (Exception e) {
			if (e instanceof EdgeServiceFault) {
				throw (EdgeServiceFault) e;
			}
			logger.error("Failed to parse source node.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToParseRHAData, "");
		}
	}

	private void checkNodeStatus(RHAControlService controlService, List<RHASourceNode> sourceNodeList) {
		if (sourceNodeList == null || sourceNodeList.size() == 0) {
			return;
		}
		List<RHASourceNode> nodeList = new ArrayList<RHASourceNode>();
		rhaDao.as_edge_source_node_list(controlService.getServer(), nodeList);
		if (nodeList.size() == 0) {
			return;
		}
		List<RHASourceNode> needDeleted = new ArrayList<RHASourceNode>();
		for (RHASourceNode sourceNode : sourceNodeList) {
			for (RHASourceNode nodeInDB : nodeList) {
				if (sourceNode.getScenarioId() == nodeInDB.getScenarioId()
						&& equals(sourceNode.getNodeName(), nodeInDB.getNodeName())
						&& equals(sourceNode.getVmInstanceUUID(), nodeInDB.getVmInstanceUUID())) {
					// Set the status as same first, then check the status
					sourceNode.setStatus(RHASourceStatus.Same);
					if (!equalsIgnoreCase(sourceNode.getRecoveryPointFolder(), nodeInDB.getRecoveryPointFolder())) {
						logger.debug("The recovery point folder is changed.");
						sourceNode.setStatus(RHASourceStatus.Changed);
						sourceNode.setRecoveryPointFolderChanged(true);
					}
					if (!equals(sourceNode.getConverter(), nodeInDB.getConverter())) {
						logger.debug("The converter is changed.");
						sourceNode.setStatus(RHASourceStatus.Changed);
						sourceNode.setConverterChanged(true);
					}
					// if (!equals(sourceNode.getScenarioName(), nodeInDB.getScenarioName())) {
					// logger.debug("The scenario name is changed.");
					// sourceNode.setStatus(RHASourceStatus.Changed);
					// sourceNode.setScenarioNameChanged(true);
					// }
					// add for issue 163692
					if (sourceNode.getStatus().equals(RHASourceStatus.Same))
						needDeleted.add(sourceNode);
					break;
				}
			}
		}
		// add for issue 163692
		sourceNodeList.removeAll(needDeleted);
	}

	private boolean equals(String first, String second) {
		if (first == second) {
			return true;
		}
		return (first != null && first.equals(second));
	}

	private boolean equalsIgnoreCase(String first, String second) {
		if (first == second) {
			return true;
		}
		return (first != null && first.equalsIgnoreCase(second));
	}

	private void validateControlService(RHAControlService controlService) throws EdgeServiceFault {
		if (controlService == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Control Service is empty.");
		}
		if (StringUtil.isEmptyOrNull(controlService.getUserName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "The userName is empty.");
		}
	}

	private XosoapapiCSoap getSoapService(RHAControlService controlService) {
		if (controlService == null) {
			return null;
		}
		if (!SOAPSERVICE_MAP.containsKey(controlService)) {
			String wsdl = controlService.getWSDL();
			XosoapapiC soapAPI = new XosoapapiC(wsdl);
			XosoapapiCSoap soapService = soapAPI.getXosoapapiCSoap();
			Map<String, Object> requestContext = ((BindingProvider) soapService).getRequestContext();
			requestContext.put(CONNECT_TIMEOUT, TIME_OUT_VALUE);
			requestContext.put(REQUEST_TIMEOUT, TIME_OUT_VALUE);
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsdl);
			SOAPSERVICE_MAP.put(controlService, soapService);
		}
		return SOAPSERVICE_MAP.get(controlService);
	}

	@Override
	public ImportNodeFromRHAResult importNodeFromRHA(ImportNodeFromRHAParameters parameters) throws EdgeServiceFault {
		ImportNodeFromRHAResult result = new ImportNodeFromRHAResult();
		if (parameters != null) {
			if (parameters.isImportFromFile()) {
				return importNodeFromFile(parameters);
			} else {
				return importNodeFromControlService(parameters);
			}
		}
		return result;
	}

	private ImportNodeFromRHAResult importNodeFromControlService(ImportNodeFromRHAParameters parameters)
			throws EdgeServiceFault {
		ImportNodeFromRHAResult result = new ImportNodeFromRHAResult();
		if (parameters != null) {
			RHAControlService controlService = parameters.getControlService();
			int[] newId = new int[1];
			// Save control service information
			rhaDao.as_edge_rha_cu(0, controlService.getServer(), controlService.getProtocol(),
					controlService.getPort(), 0, controlService.getUserName(), controlService.getPassword(), newId);
			if (newId[0] <= 0) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General,
						"Failed to insert control service");
			}
			List<RHASourceNode> nodeList = parameters.getNodeList();
			int insertNodes = 0;
			int updateNodes = 0;
			List<Integer> needRedeployNodeIds = new ArrayList<Integer>();
			for (RHASourceNode node : nodeList) {
				int[] newScenarioId = new int[1];
				// Save scenario information
				rhaDao.as_edge_rha_scenario_cu(0, newId[0], node.getScenarioId(), node.getScenarioName(), node
						.getScenarioType().getValue(), newScenarioId);
				// Save node
				int hostId = getHostIdForNodeImportedFromRHA(controlService, node);
				int[] newHostId = new int[1];
				int[] insertHost = new int[1];
				int hostType = HostType.EDGE_NODE_IMPORT_FROM_RHA.getValue();
				if (node.getScenarioType() == RHAScenarioType.HBBUIntegrated) {
					hostType |= HostType.EDGE_NODE_IMPORT_FROM_RHA_HBBU_INTEGRATED.getValue();
				}
				hostDao.as_edge_host_update_ImportFromRHA(hostId, new Date(), node.getNodeName(), 1,
						DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_NONE.getValue(), hostType,
						ProtectionType.WIN_D2D.getValue(), newHostId, insertHost);
				if (node.isNeedRedeploy()) {
					needRedeployNodeIds.add(newHostId[0]);
				}
				// check whether the node is new inserted or updated, if insert the node, then insert connect info
				if (insertHost[0] == 1) {
					String uuid = node.getVmInstanceUUID();
					if (StringUtil.isEmptyOrNull(uuid)) {
						uuid = UUID.randomUUID().toString();
					}
					conInfoDao.as_edge_connect_info_update(newHostId[0], "", "", uuid, 0, 0, 0, "", "", "", "", 0);
				}
				// insert converter
				int[] newConverterId = new int[1];
				int[] insertConverter = new int[1];
				getVsbDao().as_edge_vsb_converter_cu(1, 0, node.getConverter(), 8014, Protocol.Http.ordinal(), "", "",
						"", "", newConverterId, insertConverter);
				// Process host and converter mapping
				// When oldConverterId is null, it means the old converter is the same current converter, we don't need
				// to update the mapping
				Integer oldConverterId = null;
				if (insertHost[0] == 0) {
					// Query converter information according to the host id
					List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
					getVsbDao().as_edge_vsb_converter_getByHostId(newHostId[0], converterList);
					if (converterList.size() == 1) {
						// Get the old converter
						EdgeVCMConnectInfo converter = converterList.get(0);
						if (converter.getId() != newConverterId[0]) {
							// Old converter is not the same as the new converter
							oldConverterId = converter.getId();
						}
					} else {
						// Insert new converter
						oldConverterId = 0;
					}
				} else {
					oldConverterId = 0;
				}
				if (oldConverterId != null) {
					getVsbDao().as_edge_host_converter_map_cu(newHostId[0], newConverterId[0],
							VCMConverterType.Unknown.ordinal(), PolicyTypes.RemoteVCMForRHA, oldConverterId);
				}
				if (insertConverter[0] == 1) {
					result.addConverterId(newConverterId[0]);
				} else if (isConverterInfoIncomplete(newConverterId[0])) {
					result.addConverterId(newConverterId[0]);
				}
				rhaDao.as_edge_rha_scenario_host_map_cu(newScenarioId[0], newHostId[0], node.getRecoveryPointFolder(),
						node.getVmInstanceUUID(), node.getVmName(), node.getHypervisorName(), node.getMasterHost(),
						node.getMasterIp(), node.getReplicaHost(), node.getReplicaIp(), newConverterId[0]);
				if (insertHost[0] == 1) {
					result.addInsertNode(newHostId[0]);
				} else {
					result.addUpdateNode(newHostId[0]);
				}
			}
			// redeploy policy for the changed nodes
			// TODO [lijwe02] We need to query plan
			List<Integer> nodesWithPolicy = getNodesWithPolicy(needRedeployNodeIds, PlanTaskType.RemoteConversion.getValue());
			if (nodesWithPolicy.size() > 0) {
				String message = EdgeCMWebServiceMessages.getResource("importNodes_RHA_RedeployPolicyForChangedNodes",
						String.valueOf(nodesWithPolicy.size()));
				addActivityLog(Severity.Information, message);
				IPolicyManagementService policyManagementService = PolicyManagementServiceImpl.getInstance();
				policyManagementService.redeployPolicyToNodes(nodesWithPolicy, PolicyTypes.Unified, -1);
			}
		}
		return result;
	}

	private int getHostIdForNodeImportedFromRHA(RHAControlService controlService, RHASourceNode node) {
		int[] hostId = new int[1];
		String rhaServerName = controlService == null ? "" : controlService.getServer();
		rhaDao.as_edge_rha_getHostIdForNodeImportedFromRHA(rhaServerName, node.getScenarioId(), node.getNodeName(),
				node.getVmInstanceUUID(), hostId);
		return hostId[0];
	}

	private boolean isConverterInfoIncomplete(int converterId) {
		List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
		getVsbDao().as_edge_vsb_converter_getById(converterId, converterList);
		if (converterList.size() == 1) {
			EdgeVCMConnectInfo converter = converterList.get(0);
			if (StringUtil.isEmptyOrNull(converter.getUserName())) {
				return true;
			}
		}
		return false;
	}

	private ImportNodeFromRHAResult importNodeFromFile(ImportNodeFromRHAParameters parameters) throws EdgeServiceFault {
		ImportNodeFromRHAResult result = new ImportNodeFromRHAResult();
		if (parameters != null) {
			List<RHASourceNode> nodeList = parameters.getNodeList();
			int insertNodes = 0;
			int updateNodes = 0;
			List<Integer> needRedeployNodeIds = new ArrayList<Integer>();
			for (RHASourceNode node : nodeList) {
				int[] newScenarioId = new int[1];
				// Save scenario information
				rhaDao.as_edge_rha_scenario_cu(0, 0, node.getScenarioId(), node.getScenarioName(),
						RHAScenarioType.D2DIntegrated.getValue(), newScenarioId);
				// Save node
				int hostId = getHostIdForNodeImportedFromRHA(null, node);
				int[] newHostId = new int[1];
				int[] insert = new int[1];
				hostDao.as_edge_host_update_ImportFromRHA(hostId, new Date(), node.getNodeName(), 1,
						DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_NONE.getValue(),
						HostType.EDGE_NODE_IMPORT_FROM_RHA.getValue(), ProtectionType.WIN_D2D.getValue(), newHostId,
						insert);
				if (node.isNeedRedeploy()) {
					needRedeployNodeIds.add(newHostId[0]);
				}
				// check whether the node is new inserted or updated, if insert the node, then insert connect info
				if (insert[0] == 1) {
					String uuid = node.getVmInstanceUUID();
					if (StringUtil.isEmptyOrNull(uuid)) {
						uuid = UUID.randomUUID().toString();
					}
					conInfoDao.as_edge_connect_info_update(newHostId[0], "", "", uuid, 0, 0, 0, "", "", "", "", 0);
				}
				// insert converter
				int[] newConverterId = new int[1];
				int[] insertConverter = new int[1];
				getVsbDao().as_edge_vsb_converter_cu(1, 0, node.getConverter(), 8014, Protocol.Http.ordinal(), "", "",
						"", "", newConverterId, insertConverter);
				// hostDao.as_edge_host_OffsiteVCMConverters_insert(0, node.getConverter(), "", "",
				// Protocol.Http.ordinal(), newConverterId, insertConverter);
				if (insertConverter[0] == 1) {
					result.addConverterId(newConverterId[0]);
				} else if (isConverterInfoIncomplete(newConverterId[0])) {
					result.addConverterId(newConverterId[0]);
				}
				rhaDao.as_edge_rha_scenario_host_map_cu(newScenarioId[0], newHostId[0], node.getRecoveryPointFolder(),
						node.getVmInstanceUUID(), node.getVmName(), node.getHypervisorName(), node.getMasterHost(),
						node.getMasterIp(), node.getReplicaHost(), node.getReplicaIp(), newConverterId[0]);
				if (insert[0] == 1) {
					result.addInsertNode(newHostId[0]);
				} else {
					result.addUpdateNode(newHostId[0]);
				}
			}
			// redeploy policy for the changed nodes
			List<Integer> nodesWithPolicy = getNodesWithPolicy(needRedeployNodeIds, PlanTaskType.RemoteConversion.getValue());
			if (nodesWithPolicy.size() > 0) {
				String message = EdgeCMWebServiceMessages.getResource("importNodes_RHA_RedeployPolicyForChangedNodes",
						String.valueOf(nodesWithPolicy.size()));
				addActivityLog(Severity.Information, message);
				IPolicyManagementService policyManagementService = PolicyManagementServiceImpl.getInstance();
				policyManagementService.redeployPolicyToNodes(nodesWithPolicy, PolicyTypes.Unified, -1);
			}
		}
		return result;
	}

	public List<Integer> getNodesWithPolicy(List<Integer> nodeIdList, int planTaskType) {
		List<Integer> nodeIdWithPolicyList = new ArrayList<Integer>();
		for (int nodeId : nodeIdList) {
			List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
			this.edgePolicyDao.getHostPolicyMapByHostAndPlanTaskType(nodeId, planTaskType, mapList);
			if (mapList.size() > 0) {
				nodeIdWithPolicyList.add(nodeId);
			}
		}
		return nodeIdWithPolicyList;
	}

	@Override
	public List<RHAControlService> getControlServiceList(String serverNamePrefix) throws EdgeServiceFault {
		List<RHAControlService> controlServiceList = new ArrayList<RHAControlService>();
		rhaDao.as_edge_rha_list(serverNamePrefix, controlServiceList);
		return controlServiceList;
	}

	public void addActivityLog(Severity severity, String message) {
		try {
			ActivityLog log = new ActivityLog();
			log.setJobId(0);
			log.setModule(Module.ImportNodesFromRHA);
			log.setSeverity(severity);
			log.setMessage(message);
			log.setTime(new Date());
			logService.addLog(log);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to add activity log.", e);
		} catch (Exception e) {
			logger.error("Failed to add activity log.", e);
		}
	}

	@Override
	public OffsiteVCMConverterInfo getOffsiteVCMConverterInfoByHostId(int hostId) throws EdgeServiceFault {
		List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
		getVsbDao().as_edge_vsb_converter_getByHostId(hostId, converterList);
		if (converterList.size() > 0) {
			return NodeServiceImpl.daoConverterInfoToContractConverterInfo(converterList.get(0));
		}
		return null;
	}

	private Long getSessionId(XosoapapiCSoap soapService, RHAControlService controlService) throws EdgeServiceFault {
		Holder<Long> createSessionResult = new Holder<Long>();
		Holder<Long> errorCode = new Holder<Long>();
		String userName = controlService.getUserName();
		if (userName != null) {
			if (userName.indexOf("\\") == -1) {
				userName = "\\" + userName;
			}
		}
		soapService.createSession(userName, controlService.getPassword(), createSessionResult, errorCode);
		if (errorCode.value == 0) {
			return createSessionResult.value;
		}
		logger.error("Failed to create session, the error code is:" + errorCode.value);
		if (errorCode.value == 1326) {
			logger.error("Username or password is invalid.");
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToConnectRHACredentialError, "");
		}
		throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_FailedToConnectRHAControlService, "");
	}

	private void closeSession(XosoapapiCSoap soapService, Long sessionId) {
		Holder<Boolean> closeSessionResult = new Holder<Boolean>();
		Holder<String> whyNotReason = new Holder<String>();
		soapService.closeSession(sessionId, closeSessionResult, whyNotReason);
		if (!closeSessionResult.value) {
			logger.error("Failed to close session, reason:" + whyNotReason.value);
		}
	}

	public IEdgeVSBDao getVsbDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}
}
