package com.ca.arcserve.edge.app.base.webservice.node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

//import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.LinuxNodeUtil;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxNodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.LinuxD2DServerRegistrationResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfoForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResultForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeLinuxD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.LinuxDiagInfoCollectorConfiguration;
import com.ca.arcserve.linuximaging.webservice.data.NodeConnectionInfo;
import com.ca.arcserve.linuximaging.webservice.data.TargetMachineInfo;
import com.ca.arcserve.linuximaging.webservice.data.register.D2DServerRegistrationInfo;

public class LinuxNodeServiceImpl implements IEdgeLinuxNodeService {

	private static final Logger logger = Logger
			.getLogger(LinuxNodeServiceImpl.class);
	IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory
			.getDao(IEdgeConnectInfoDao.class);
	IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private IEdgeGatewayLocalService gatewayService;
	private IConnectionFactory connectionFactory = EdgeFactory
			.getBean(IConnectionFactory.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory
			.getBean(IRemoteNativeFacadeFactory.class);

	private IEdgeGatewayLocalService getGatewayService() {
		if (this.gatewayService == null)
			this.gatewayService = EdgeFactory
					.getBean(IEdgeGatewayLocalService.class);
		return this.gatewayService;
	}

	@Override
	public RegistrationNodeResult registerLinuxNode(
			NodeRegistrationInfo registrationNodeInfo, boolean isForce)
			throws EdgeServiceFault {
		return registerLinuxNode(registrationNodeInfo, isForce, false);
	}

	public RegistrationNodeResult registerLinuxNode(
			NodeRegistrationInfo registrationNodeInfo, boolean isForce,
			boolean isAddForLinuxD2DServer) throws EdgeServiceFault {
		NodeConnectionInfo nodeInfo = validateNode(null, registrationNodeInfo,
				isForce, true);
		RegistrationNodeResult result = this
				.getRegistrationNodeResult(nodeInfo);
		if ((result.getErrorCodes()[0] == null)
				|| (LinuxNodeUtil.getMessageType(result.getErrorCodes()) == Severity.Warning)) {
			String ip = getIPAddress(registrationNodeInfo.getGatewayId(),
					registrationNodeInfo.getNodeName());
			int nodeId = registrationNodeInfo.getId();
			boolean needRedeploy = false;
			int policyId = 0;
			if (nodeId > 0) {
				List<EdgeHost> hostList = new ArrayList<EdgeHost>();
				hostMgrDao.as_edge_host_list(nodeId, 1, hostList);

				if (isHostnameChanged(hostList.get(0), registrationNodeInfo)) {
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(
							1);
					policyDao.getHostPolicyMap(nodeId, PolicyTypes.Unified,
							mapList);
					if (mapList.size() > 0) {
						removeOldNodeInD2DServer(mapList.get(0), hostList
								.get(0).getRhostname());
						needRedeploy = true;
						policyId = mapList.get(0).getPolicyId();
					} else {

					}
				} else if (isCredentialChanged(hostList.get(0),
						registrationNodeInfo)) {
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(
							1);
					policyDao.getHostPolicyMap(nodeId, PolicyTypes.Unified,
							mapList);
					if (mapList.size() > 0) {
						updateCredentialOnD2DServer(mapList.get(0),
								registrationNodeInfo);
					}
				}
			}

			String hostName = registrationNodeInfo.getNodeName();
			if (!StringUtil.isEmptyOrNull(hostName)) {
				hostName = hostName.toLowerCase();
			}

			// List<String> fqdnNameList =
			// CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if (registrationNodeInfo.getGatewayId() != null
					&& registrationNodeInfo.getGatewayId().isValid()) {
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory
							.createRemoteNativeFacade(registrationNodeInfo
									.getGatewayId());
					fqdnNameList = nativeFacade
							.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error(
							"[LinuxNodeServiceImpl] registerLinuxNode() get fqdn name failed.",
							e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);

			int[] output = new int[1];
			hostMgrDao.as_edge_host_update(
					nodeId > 0 ? nodeId : 0,
					new Date(),
					hostName,
					registrationNodeInfo.getNodeDescription(),
					ip,
					nodeInfo.getOsName(),
					"",
					1,
					isAddForLinuxD2DServer ? ApplicationUtil
							.setLinuxD2DInstalled(0) : 0, "", HostTypeUtil
							.setLinuxNode(0),
					ProtectionType.WIN_D2D.getValue(), fqdnNames, output);
			connectionInfoDao.as_edge_connect_info_update(output[0],
					registrationNodeInfo.getUsername(),
					registrationNodeInfo.getPassword(), nodeInfo.getNodeUUID(),
					0, 0, 0, "", "", "", "",
					NodeManagedStatus.Managed.ordinal());
			// deal with gateway and host map
			getGatewayService().addNode(registrationNodeInfo.getGatewayId(),
					output[0]);
			if (needRedeploy) {
				redeployPolicy(policyId);
				doDeployNowByPlanId(policyId);
			}
			result.setHostID(output[0]);
		}
		return result;
	}

	private boolean isCredentialChanged(EdgeHost oldHost,
			NodeRegistrationInfo newHost) {
		if (oldHost == null) {
			return false;
		}
		if (oldHost.getUsername() == null) {
			if (newHost.getUsername() == null)
				return false;
			else
				return true;
		} else if (oldHost.getPassword() == null) {
			if (newHost.getPassword() == null)
				return false;
			else
				return true;
		} else if (oldHost.getPassword().equals(newHost.getPassword())
				&& oldHost.getUsername().equals(newHost.getUsername())) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isHostnameChanged(EdgeHost oldHost,
			NodeRegistrationInfo newHost) {
		if (oldHost == null) {
			return false;
		}
		if (!oldHost.getRhostname().equals(newHost.getNodeName())) {
			return true;
		}
		return false;
	}

	private int removeOldNodeInD2DServer(EdgeHostPolicyMap map,
			String oldHostname) {
		HostConnectInfo d2dServerInfo = null;
		int ret = 0;
		try {
			d2dServerInfo = this.getLinuxD2DServerInfoByHostId(map.getHostId());
			LinuxD2DConnection connection = connectionFactory
					.createLinuxD2DConnection(d2dServerInfo.getId());
			connection.connect();
			ILinuximagingService service = connection.getService();
			ret = service.validateByKey(d2dServerInfo.getAuthUuid());
			if (ret == 0) {
				String[] nodes = new String[1];
				nodes[0] = oldHostname;
				ret = service.deleteJobForNodes(map.getPolicyId(), nodes);
			}
			connection.close();
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			return -2;
		} catch (EdgeServiceFault e) {
			logger.error("removeOldNodeInD2DServer", e);
			return -3;
		}
		return ret;

	}

	private int updateCredentialOnD2DServer(EdgeHostPolicyMap map,
			NodeRegistrationInfo registrationNodeInfo) {
		HostConnectInfo d2dServerInfo = null;
		int ret = 0;
		try {
			d2dServerInfo = this.getLinuxD2DServerInfoByHostId(map.getHostId());
			LinuxD2DConnection connection = connectionFactory
					.createLinuxD2DConnection(d2dServerInfo.getId());
			connection.connect();
			ILinuximagingService service = connection.getService();
			ret = service.validateByKey(d2dServerInfo.getAuthUuid());
			if (ret == 0) {
				TargetMachineInfo machineInfo = service
						.getTargetMachineByName(registrationNodeInfo
								.getNodeName());
				machineInfo.setUser(registrationNodeInfo.getUsername());
				machineInfo.setPassword(registrationNodeInfo.getPassword());
				machineInfo.setDescription(registrationNodeInfo
						.getNodeDescription());
				List<TargetMachineInfo> list = new ArrayList<TargetMachineInfo>();
				list.add(machineInfo);
				ret = service.modifyTargetMachineList(list);
			}
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			return -2;
		} catch (EdgeServiceFault e) {
			logger.error("updateCredentialOnD2DServer", e);
			return -3;
		}
		return ret;
	}

	private void redeployPolicy(int policyId) {
		policyDao.redeployPolicy(PolicyTypes.Unified, policyId,
				PolicyDeployReasons.ReDeployManually);
	}

	private void doDeployNowByPlanId(int policyId) {
		logger.info("LinuxNodeServiceImpl.doDeployNowByPlanId(): Launch policy deployment immediately, policy id is: "
				+ policyId);
		PolicyManagementServiceImpl policyImpl = new PolicyManagementServiceImpl();
		policyImpl.getPolicyDeploymentScheduler().doDeploymentNowByPlanId(
				policyId);
	}

	@Override
	public RegistrationNodeResult registerLinuxD2DServer(
			NodeRegistrationInfo registrationNodeInfo, boolean isForce,
			boolean isClearExistingData) throws EdgeServiceFault {

		return registerLinuxD2DServer(registrationNodeInfo, isForce,
				isClearExistingData, null);
	}

	public RegistrationNodeResult registerLinuxD2DServer(
			NodeRegistrationInfo registrationNodeInfo, boolean isForce,
			boolean isClearExistingData, Version d2dVersion)
			throws EdgeServiceFault {

		String ip = getIPAddress(registrationNodeInfo.getGatewayId(),
				registrationNodeInfo.getNodeName());
		int nodeId = registrationNodeInfo.getId() <= 0 ? 0
				: registrationNodeInfo.getId();
		if (nodeId == 0) {
			if (isNodeExists(registrationNodeInfo.getNodeName(), ip, 2))
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_AlreadyExist, ""));
		}
		int[] output = new int[1];
		RegistrationNodeResult result = new RegistrationNodeResult();
		String[] errorCodes = new String[1];
		errorCodes[0] = null;
		try {
			LinuxD2DServerRegistrationResponse response = markLinuxD2DAsManaged(
					registrationNodeInfo, isForce, isClearExistingData);
			List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
			boolean needRedeploy = false;
			if (nodeId > 0) {
				policyDao.as_edge_policy_list_by_linux_d2d_server(nodeId,
						policyList);

				if (policyList.size() > 0) {
					List<EdgeHost> hostList = new ArrayList<EdgeHost>();
					hostMgrDao.as_edge_host_list(nodeId, 1, hostList);
					EdgeHost host = hostList.get(0);
					if (this.isHostnameChanged(host, registrationNodeInfo)) {
						removePolicyInOldD2DServer(host, policyList);
					}
					needRedeploy = true;
				}
			}
			String majorVersion = "";
			String minorVersion = "";
			String buildNumber = "";
			if (response.getVersionNumber() != null) {
				String version = response.getVersionNumber();
				if (version.contains(".")) {
					String[] versionArray = version.split("\\.");
					majorVersion = versionArray[0];
					minorVersion = versionArray[1];
				}
			}
			if (response.getBuildNumber() != null) {
				buildNumber = response.getBuildNumber();
			}

			String hostName = registrationNodeInfo.getNodeName();
			if (!StringUtil.isEmptyOrNull(hostName)) {
				hostName = hostName.toLowerCase();
			}

			// List<String> fqdnNameList =
			// CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if (registrationNodeInfo.getGatewayId() != null
					&& registrationNodeInfo.getGatewayId().isValid()) {
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory
							.createRemoteNativeFacade(registrationNodeInfo
									.getGatewayId());
					fqdnNameList = nativeFacade
							.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error(
							"[LinuxNodeserviceImpl] registerLinuxD2DServer() get fqdn name failed.",
							e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);

			hostMgrDao.as_edge_host_update(nodeId, new Date(), hostName,
					registrationNodeInfo.getNodeDescription(), ip,
					response.getOsName(), "", 1,
					ApplicationUtil.setLinuxD2DInstalled(0), "", 0,
					ProtectionType.LINUX_D2D_SERVER.getValue(), fqdnNames,
					output);

			getGatewayService().bindEntity(registrationNodeInfo.getGatewayId(),
					output[0], EntityType.Node);
			hostMgrDao.as_edge_host_update_timezone_by_id(output[0],
					response.getD2dServerTimezone());
			connectionInfoDao.as_edge_connect_info_update(output[0],
					registrationNodeInfo.getUsername(), registrationNodeInfo
							.getPassword(), response.getD2dServerUUID(),
					registrationNodeInfo.getD2dProtocol().ordinal(),
					registrationNodeInfo.getD2dPort(), 0, majorVersion,
					minorVersion, "0", buildNumber, NodeManagedStatus.Managed
							.ordinal());
			connectionInfoDao
					.as_edge_connect_info_setAuthUuid(
							response.getD2dServerUUID(),
							response.getD2dServerAuthKey());
			if (needRedeploy) {
				for (EdgePolicy policy : policyList) {
					redeployPolicy(policy.getId());
					doDeployNowByPlanId(policy.getId());
				}
			}

			if (d2dVersion != null) {
				d2dVersion.setMajorVersion(Integer.parseInt(majorVersion));
				d2dVersion.setMinorVersion(Integer.parseInt(minorVersion));
				d2dVersion.setBuildNumber(buildNumber);
			}

		} catch (EdgeServiceFault e) {
			throw e;
		}

		result.setErrorCodes(errorCodes);
		result.setHostID(output[0]);

		// if(registrationNodeInfo.getId() == 0){
		// tryToAddServerAsOneNode(registrationNodeInfo);
		// }

		return result;
	}

	private void tryToAddServerAsOneNode(
			NodeRegistrationInfo registrationNodeInfo) {
		try {
			registerLinuxNode(registrationNodeInfo, true, true);
		} catch (EdgeServiceFault e) {
			logger.error("tryToAddServerAsOneNode", e);
		} catch (Exception e) {
			logger.error("tryToAddServerAsOneNode", e);
		}
	}

	private int removePolicyInOldD2DServer(EdgeHost oldServer,
			List<EdgePolicy> policyList) {
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(oldServer.getRhostid(),
				connInfoLst);
		if (connInfoLst.size() == 0)
			return 0;
		EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
		int ret = -1;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(oldServer.getRhostid())) {
			connection.connect();
			ILinuximagingService service = connection.getService();
			ret = service.validateByKey(d2dServerInfo.getAuthUuid());
			if (ret == 0) {
				for (EdgePolicy policy : policyList) {
					ret = service.deletePlan(policy.getId());
				}
			}
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			return -2;
		} catch (EdgeServiceFault e) {
			logger.error(e);
		}
		return ret;
	}

	public LinuxD2DServerRegistrationResponse markLinuxD2DAsManaged(
			NodeRegistrationInfo registrationNodeInfo, boolean isForce,
			boolean clearExistingData) throws EdgeServiceFault {
		IEdgeLinuxD2DRegService regService = new EdgeLinuxD2DRegServiceImpl();
		try {
			return regService.RegInfoToLinuxD2D(registrationNodeInfo, isForce,
					clearExistingData);
		} catch (EdgeServiceFault e) {
			logger.error("markLinuxD2DAsManaged error", e);
			throw e;
		}
	}

	public void unRegInfoToLinuxD2D(int nodeId, boolean isForce)
			throws EdgeServiceFault {
		IEdgeLinuxD2DRegService regService = new EdgeLinuxD2DRegServiceImpl();
		try {
			int ret = regService.unRegInfoToLinuxD2D(nodeId, isForce);
			if (ret != 0) {
				logger.error("unRegInfoToLinuxD2D error");
			}
		} catch (EdgeServiceFault e) {
			logger.error("unRegInfoToLinuxD2D error", e);
			throw e;
		} catch (Exception e) {
			logger.error("unRegInfoToLinuxD2D error", e);
		}
	}

	public String getIPAddress(GatewayId gatewayId, String hostname) {
		try {
			return this.getIPAddressWithException(gatewayId, hostname);
		} catch (UnknownHostException e) {
			logger.error("getIPAddress(String)", e);
			return "";
		}
	}

	public String getIPAddressWithException(GatewayId gatewayId, String hostname)
			throws UnknownHostException {
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory
				.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory
				.createRemoteNativeFacade(gatewayId);
		return nativeFacade.getIpByHostName(hostname);
	}

	private int validateNode(EdgeHost d2dServer,
			NodeRegistrationInfo registrationNodeInfo,
			NodeConnectionInfo nodeRegResult, boolean isForce, boolean writeFile)
			throws EdgeServiceFault {
		String hostname = CommonUtil.getServerIpFromFile();
		if (StringUtil.isEmptyOrNull(hostname)) {
			hostname = EdgeCommonUtil.getLocalFqdnName();
		} else {
			try {
				this.getIPAddressWithException(
						registrationNodeInfo.getGatewayId(), hostname);
			} catch (UnknownHostException e) {
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_Linux_D2D_Server_UDP_IP_Not_Reachable,
						hostname);
				b.setMessageParameters(new String[] { hostname });
				throw new EdgeServiceFault(hostname, b);
			}
		}
		String uuid = CommonUtil.retrieveCurrentAppUUID();

		ILinuximagingService service = connectToD2DServer(d2dServer);

		D2DServerRegistrationInfo regInfo = new D2DServerRegistrationInfo();
		regInfo.setServerName(hostname);
		regInfo.setServerUUID(uuid);
		NodeConnectionInfo retInfo = service.validateNode(regInfo,
				registrationNodeInfo.getNodeName(),
				registrationNodeInfo.getUsername(),
				registrationNodeInfo.getPassword(), isForce, writeFile);
		nodeRegResult.setErrCode1(retInfo.getErrCode1());
		nodeRegResult.setErrCode2(retInfo.getErrCode2());
		nodeRegResult.setD2dServer(retInfo.getD2dServer());
		nodeRegResult.setOsName(retInfo.getOsName());
		nodeRegResult.setNodeUUID(retInfo.getNodeUUID());
		return 0;
	}

	private ILinuximagingService connectToD2DServer(EdgeHost d2dServer)
			throws EdgeServiceFault {
		int ret = 0;
		ILinuximagingService service = null;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServer.getRhostid())) {
			connection.connect();
			service = connection.getService();
			if ((d2dServer.getD2dManagedStatus() != NodeManagedStatus.Managed
					.ordinal())
					&& (d2dServer.getD2DUUID() == null || d2dServer
							.getD2DUUID().equals(""))) {
				String retStr = service.validateUser(d2dServer.getUsername(),
						d2dServer.getPassword());
				if (retStr == null) {
					throw EdgeServiceFault
							.getFault(
									EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential,
									"Failed to login to Linux D2D");
				}
			} else {
				List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
				connectionInfoDao.as_edge_connect_info_list(
						d2dServer.getRhostid(), connInfoLst);
				ret = service.validateByKey(connInfoLst.get(0).getAuthUuid());
				if (ret != 0) {
					EdgeServiceFaultBean b = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_Linux_D2D_Server_Managed_By_Others,
							d2dServer.getRhostname());
					b.setMessageParameters(new String[] { d2dServer
							.getRhostname() });
					throw new EdgeServiceFault(d2dServer.getRhostname(), b);
				}
			}
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			EdgeServiceFaultBean b = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,
					d2dServer.getRhostname());
			b.setMessageParameters(new String[] { d2dServer.getRhostname() });
			throw new EdgeServiceFault(d2dServer.getRhostname(), b);
		}
		return service;
	}

	public boolean isNodeExists(String name, String ip, int nodetype) {
		int[] output = new int[1];
		hostMgrDao
				.as_edge_host_linux_node_isexisted(name, ip, nodetype, output);
		return output[0] == 0 ? false : true;
	}

	public int getNodeId(String name, String ip, int nodetype) {
		int[] output = new int[1];
		hostMgrDao
				.as_edge_host_linux_node_isexisted(name, ip, nodetype, output);
		return output[0];
	}

	@Override
	public RegistrationNodeResult validateLinuxNode(
			NodeRegistrationInfo d2dServer,
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		NodeConnectionInfo nodeInfo = validateNode(d2dServer,
				registrationNodeInfo, false, false);
		return getRegistrationNodeResult(nodeInfo);
	}

	@Override
	public int validateManaged(NodeRegistrationInfo registrationNodeInfo)
			throws EdgeServiceFault {

		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		// connectionInfoDao.as_edge_linux_d2d_server_by_hostid(
		// registrationNodeInfo.getId(), connInfoLst);
		connectionInfoDao.as_edge_connect_info_list(
				registrationNodeInfo.getId(), connInfoLst);
		EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
		ILinuximagingService service = null;
		int ret = -1;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServerInfo.getHostid())) {
			connection.connect();
			service = connection.getService();
			try {
				ret = service.validateByKey(d2dServerInfo.getAuthUuid());
				if (ret != 0) {
					EdgeServiceFaultBean b = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.LinuxBackup_NodeManagedByOthers,
							d2dServerInfo.getRhostname());
					b.setMessageParameters(new String[] { d2dServerInfo
							.getRhostname() });
					throw new EdgeServiceFault(d2dServerInfo.getRhostname(), b);
				}
			} catch (SOAPFaultException e) {
				try {
					if (e.getFault() != null)
						e.getFault()
								.setFaultActor(d2dServerInfo.getRhostname());
				} catch (SOAPException e1) {
					// do nothing
				}
				logger.error("fail to submit backup job SOAPFaultException", e);
				throw e;
			} catch (WebServiceException e) {
				logger.error("fail to submit backup job WebServiceException", e);
				throw EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Node_D2D_Reg_connection_refuse,
						"connect to D2D refused");
			}
		}
		return ret;
	}

	protected NodeConnectionInfo validateNode(
			NodeRegistrationInfo inputD2DServer,
			NodeRegistrationInfo registrationNodeInfo, boolean isForce,
			boolean writeFile) throws EdgeServiceFault {
		String ip = getIPAddress(registrationNodeInfo.getGatewayId(),
				registrationNodeInfo.getNodeName());
		int nodeId = registrationNodeInfo.getId();
		if (nodeId <= 0) {
			if (isNodeExists(registrationNodeInfo.getNodeName(), ip, 1))
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_AlreadyExist,
						"This linux node has already exists"));
		}
		List<EdgeHost> linuxD2DList = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, linuxD2DList);// get
																			// all
																			// d2d
																			// server
																			// and
																			// then
																			// select
																			// one
																			// to
																			// validate
																			// linux
																			// node
		NodeConnectionInfo nodeInfo = new NodeConnectionInfo();

		if (inputD2DServer != null) {
			linuxD2DList.add(getEdgeHost(inputD2DServer));
		}

		if (linuxD2DList == null || linuxD2DList.size() == 0) {
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_Linux_No_D2D_Server, ""));
		}
		if (registrationNodeInfo.getId() > 0
				&& this.doesNodeHavePolicy(nodeId, PolicyTypes.Unified)) {
			List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
			connectionInfoDao.as_edge_linux_d2d_server_by_hostid(
					registrationNodeInfo.getId(), connInfoLst);
			EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
			EdgeHost d2dServer = new EdgeHost();
			d2dServer.setRhostid(d2dServerInfo.getHostid());
			d2dServer.setRhostname(d2dServerInfo.getRhostname());
			d2dServer.setD2dPort(String.valueOf(d2dServerInfo.getPort()));
			d2dServer.setD2dProtocol(d2dServerInfo.getProtocol());
			d2dServer.setD2DUUID(d2dServerInfo.getUuid());
			d2dServer.setD2dManagedStatus(d2dServerInfo.getStatus());
			d2dServer.setUsername(d2dServerInfo.getUsername());
			d2dServer.setPassword(d2dServerInfo.getPassword());
			validateNode(d2dServer, registrationNodeInfo, nodeInfo, isForce,
					writeFile);
		} else {
			boolean allD2DServerNotAvailable = true;
			EdgeHost sameNameD2DServer = findD2DServerHasSameNameWithNode(
					linuxD2DList, registrationNodeInfo.getNodeName());
			if (sameNameD2DServer != null) {
				logger.info("Find same name d2dserver and make it as first d2d server. d2d server name: "
						+ sameNameD2DServer.getRhostname());
				linuxD2DList.add(0, sameNameD2DServer);
			}
			if (registrationNodeInfo instanceof NodeRegistrationInfoForLinux) {
				int linuxBackupServerIdUsedForValidation = ((NodeRegistrationInfoForLinux) registrationNodeInfo)
						.getLinuxBackupServerIdUsedForValidation();
				if (linuxBackupServerIdUsedForValidation != 0) {
					for (EdgeHost d2dServer : linuxD2DList) {
						if (d2dServer.getRhostid() == linuxBackupServerIdUsedForValidation) {
							linuxD2DList.add(0, d2dServer);
							logger.info("Use specified linux backup server to valiate. Id: "
									+ linuxBackupServerIdUsedForValidation);
							break;
						}
					}
				}
			}

			for (EdgeHost d2dServer : linuxD2DList) {
				try {
					int ret = validateNode(d2dServer, registrationNodeInfo,
							nodeInfo, isForce, writeFile);
					if (ret != 0) {
						continue;
					}
					if (isNodeNotReachable(nodeInfo)) {
						allD2DServerNotAvailable = false;
						continue;
					}
					allD2DServerNotAvailable = false;
					break;
				} catch (EdgeServiceFault e) {
					if (e.getFaultInfo()
							.getCode()
							.equals(EdgeServiceErrorCode.Node_Linux_D2D_Server_UDP_IP_Not_Reachable)) {
						throw e;
					}
				} catch (Exception e) {
					logger.error(
							"validate node with d2dserver:"
									+ d2dServer.getRhostname() + "failed", e);
				}
			}

			if (allD2DServerNotAvailable) {
				throw new EdgeServiceFault(
						"",
						new EdgeServiceFaultBean(
								EdgeServiceErrorCode.Node_Linux_No_Available_D2D_Server,
								""));
			}
		}
		return nodeInfo;
	}

	private boolean isNodeNotReachable(NodeConnectionInfo nodeInfo) {
		if (nodeInfo.getErrCode1() == 1 || nodeInfo.getErrCode1() == 3
				|| nodeInfo.getErrCode1() == 9) {
			return true;
		}
		return false;
	}

	private EdgeHost findD2DServerHasSameNameWithNode(
			List<EdgeHost> linuxD2DList, String nodeName) {
		for (EdgeHost edgeHost : linuxD2DList) {
			if (edgeHost.getRhostname().equals(nodeName)) {
				return edgeHost;
			}
		}
		return null;
	}

	private EdgeHost getEdgeHost(NodeRegistrationInfo inputD2DServer) {
		EdgeHost edgeHost = new EdgeHost();
		edgeHost.setRhostname(inputD2DServer.getNodeName());
		edgeHost.setD2dPort(String.valueOf(inputD2DServer.getD2dPort()));
		if (inputD2DServer.getD2dProtocol() != null) {
			edgeHost.setD2dProtocol(inputD2DServer.getD2dProtocol().ordinal());
		}
		edgeHost.setUsername(inputD2DServer.getUsername());
		edgeHost.setPassword(inputD2DServer.getPassword());
		edgeHost.setD2dManagedStatus(NodeManagedStatus.Unmanaged.ordinal());
		return edgeHost;
	}

	public RegistrationNodeResult getRegistrationNodeResult(
			NodeConnectionInfo nodeInfo) {
		RegistrationNodeResult result = new RegistrationNodeResult();
		result.setNodeUUID(nodeInfo.getNodeUUID());
		if (nodeInfo != null) {
			if (nodeInfo.getErrCode1() == 0) {
				String[] errorCodes = new String[1];
				errorCodes[0] = null;
				result.setErrorCodes(errorCodes);
			} else {

				if (nodeInfo.getD2dServer() != null) {
					String[] errorCodes = new String[3];
					errorCodes[0] = String.valueOf(nodeInfo.getErrCode1());
					errorCodes[1] = String.valueOf(nodeInfo.getErrCode2());
					errorCodes[2] = nodeInfo.getD2dServer().getServerName();
					result.setErrorCodes(errorCodes);
				} else {
					String[] errorCodes = new String[2];
					errorCodes[0] = String.valueOf(nodeInfo.getErrCode1());
					errorCodes[1] = String.valueOf(nodeInfo.getErrCode2());
					result.setErrorCodes(errorCodes);
				}

			}

		}
		return result;
	}

	@Override
	public RegistrationNodeResultForLinux validateLinuxD2DServer(
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		String ip = getIPAddress(registrationNodeInfo.getGatewayId(),
				registrationNodeInfo.getNodeName());

		if (registrationNodeInfo.getId() <= 0) {
			if (isNodeExists(registrationNodeInfo.getNodeName(), ip, 2))
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_AlreadyExist,
						"This linux backup server has already exists."));
		} else {
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			hostMgrDao
					.as_edge_host_list(registrationNodeInfo.getId(), 1, hosts);
			EdgeHost d2dServer = hosts.get(0);
			registrationNodeInfo.setNodeName(d2dServer.getRhostname());
			registrationNodeInfo.setUsername(d2dServer.getUsername());
			registrationNodeInfo.setPassword(d2dServer.getPassword());
			registrationNodeInfo.setD2dProtocol(Protocol.parse(d2dServer
					.getD2dProtocol()));
			registrationNodeInfo.setD2dPort(Integer.valueOf(d2dServer
					.getD2dPort()));
		}
		RegistrationNodeResultForLinux result = new RegistrationNodeResultForLinux();
		String[] errorCodes = new String[1];
		errorCodes[0] = null;

		IEdgeLinuxD2DRegService regService = new EdgeLinuxD2DRegServiceImpl();
		try {
			regService.validateRegistrationInfo(registrationNodeInfo, false,
					false);
		} catch (EdgeServiceFault e) {
			logger.error("markLinuxD2DAsManaged error", e);
			throw e;
		}

		result.setErrorCodes(errorCodes);
		List<EdgeHost> backupServerList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, backupServerList);
		result.setExistLinuxBackupServer(backupServerList.size() > 0);
		return result;
	}

	@Override
	public int backupLinuxNode(int nodeId, String nodeName, int backupType)
			throws EdgeServiceFault {
		List<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
		policyDao.as_edge_policy_list_by_hostId(nodeId, policyList);

		if (policyList.size() == 0) {
			return -1;
		}
		PolicyInfo policy = policyList.get(0);

		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_linux_d2d_server_by_policyid(
				policy.getPolicyId(), connInfoLst);

		EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
		int ret = -1;
		ILinuximagingService service = null;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServerInfo.getHostid())) {
			connection.connect();
			service = connection.getService();
			try {
				ret = service.validateByKey(d2dServerInfo.getAuthUuid());
				if (ret == 0) {
					ret = service.runJobByPlanIdAndNodename(
							policy.getPolicyId(), nodeName,
							getBackupType(backupType));
				} else {
					EdgeServiceFaultBean b = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.LinuxBackup_NodeManagedByOthers,
							d2dServerInfo.getRhostname());
					b.setMessageParameters(new String[] { d2dServerInfo
							.getRhostname() });
					throw new EdgeServiceFault(d2dServerInfo.getRhostname(), b);
				}
			} catch (SOAPFaultException e) {
				try {
					if (e.getFault() != null)
						e.getFault().setFaultActor(nodeName);
				} catch (SOAPException e1) {
					// do nothing
				}
				logger.error("fail to submit backup job SOAPFaultException", e);
				throw e;
			} catch (WebServiceException e) {
				logger.error("fail to submit backup job WebServiceException", e);
				throw EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Node_D2D_Reg_connection_refuse,
						"connect to D2D refused");
			}
		}
		// catch (WebServiceException e) {
		// logger.error("cannot connect to Linux D2D service", e);
		// ret = -2;
		// }

		return ret;
	}

	private int getBackupType(int backupType) {
		return backupType + 3;
	}

	public void deleteLinuxNode(int nodeId, boolean keepCurrentSettings) {
		if (this.doesNodeHavePolicy(nodeId, PolicyTypes.Unified)) {
			removePolicyForNode(nodeId);
		} else {
			unRegistorNode(nodeId);
			hostMgrDao.as_edge_host_remove(nodeId);
			logger.info("LinuxNodeServiceImpl.deleteLinuxNode(): delete node, nodeId:"
					+ nodeId);
		}

		// deleteNodeInDB(nodeId);
	}

	private void removePolicyForNode(int nodeId) {
		PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl
				.getInstance();

		try {
			policyManagementServiceImpl.removePolicyFromNodeImmedately(nodeId,
					PolicyTypes.Unified,
					PolicyDeployFlags.UnregisterNodeAfterUnassign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unRegisterLinuxD2DForTask(PolicyDeploymentTask task) {
		List<Integer> nodeList = (List<Integer>) task.getTaskParameters();
		for (Integer nodeId : nodeList) {
			policyDao.deleteHostPolicyMap(nodeId, task.getPolicyType());
			unRegistorNode(nodeId);
			hostMgrDao.as_edge_host_remove(nodeId);
			logger.info("LinuxNodeServiceImpl.unRegisterLinuxD2DForTask(): delete node, nodeId:"
					+ nodeId);
		}
	}

	public void unRegistorNode(int nodeId) {
		List<EdgeHost> nodeList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_hosts_list("(" + nodeId + ")", nodeList);
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(nodeId, connInfoLst);

		if (connInfoLst.size() == 0) {
			return;
		}

		List<EdgeHost> linuxD2DList = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, linuxD2DList);// get
																			// all
																			// d2d
																			// server
																			// and
																			// then
																			// select
																			// one
																			// to
																			// validate
																			// linux
																			// node

		if (linuxD2DList.size() == 0) {
			logger.error("unRegistorNode error: no linux d2d server in db");
			return;
		}
		EdgeHost node = nodeList.get(0);
		EdgeConnectInfo nodeConnectionInfo = connInfoLst.get(0);
		boolean allD2DServerNotAvailable = true;
		for (EdgeHost d2dServer : linuxD2DList) {
			try {
				ILinuximagingService service = connectToD2DServer(d2dServer);
				String edgeHostName = "";
				String edgeUUID = CommonUtil.retrieveCurrentAppUUID();

				try {
					InetAddress addr = InetAddress.getLocalHost();
					edgeHostName = addr.getHostName().toLowerCase();
				} catch (UnknownHostException e) {
					logger.error("Cannot get local hostname", e);
				}
				D2DServerRegistrationInfo registerInfo = new D2DServerRegistrationInfo();
				registerInfo.setServerUUID(edgeUUID);
				registerInfo.setServerName(edgeHostName);
				allD2DServerNotAvailable = false;
				TargetMachineInfo[] machines = new TargetMachineInfo[1];
				machines[0] = getTargetMachineInfo(node, nodeConnectionInfo);
				service.unRegisterNodeInfo(registerInfo, machines);
				connectionInfoDao.as_edge_connect_remove(nodeId);
				break;
			} catch (Exception e) {
				logger.error(
						"delete node with d2dserver:"
								+ d2dServer.getRhostname() + "failed", e);
			}
		}
		if (allD2DServerNotAvailable) {
			logger.info("unRegistorNode error: no availiable linux d2d server");
		}
	}

	private TargetMachineInfo getTargetMachineInfo(EdgeHost node,
			EdgeConnectInfo nodeConnectionInfo) {
		TargetMachineInfo info = new TargetMachineInfo();
		info.setName(node.getRhostname());
		info.setUser(nodeConnectionInfo.getUsername());
		info.setPassword(nodeConnectionInfo.getPassword());
		return info;
	}

	public void deleteLinuxD2DServer(int nodeId) throws EdgeServiceFault {
		checkLinuxD2DCanBeDeleted(nodeId);

		try {
			unRegInfoToLinuxD2D(nodeId, true);
		} catch (EdgeServiceFault e) {
			logger.error("unRegInfoToLinuxD2D failed", e);
		}
		deleteNodeInDB(nodeId);
	}

	public void checkLinuxD2DCanBeDeleted(int nodeId) throws EdgeServiceFault {
		if (doesLinuxD2DServerHavePolicy(nodeId)) {
			hostMgrDao.as_edge_host_set_visible(nodeId, 1);
			List<EdgeHost> lstHosts = new LinkedList<EdgeHost>();
			hostMgrDao.as_edge_host_list(nodeId, 1, lstHosts);

			String message = MessageReader
					.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							EdgeServiceErrorCode.Node_Linux_D2D_Server_Having_Plan);
			;
			String failedMessageString = EdgeCMWebServiceMessages
					.getMessage("deleteNodeFailed", lstHosts.get(0)
							.getRhostname(), message);
			logger.error("[LinuxNodeServiceImpl] checkLinuxD2DCanBeDeleted() Delete node failed, because the linux server have plan.");
			addActivityLog(nodeId, lstHosts.get(0).getRhostname(),
					Severity.Error, EdgeCMWebServiceMessages.getMessage(
							"deleteNodePrefix", failedMessageString),
					Module.DeleteNode);

			throw new EdgeServiceFault("", new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_Linux_D2D_Server_Having_Plan, ""));
		}

		List<EdgeHost> backupServerList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, backupServerList);

		int[] exist = new int[1];
		hostMgrDao.as_edge_node_linux_exist(exist);

		if (backupServerList.size() == 0 && exist[0] == 1) {
			hostMgrDao.as_edge_host_set_visible(nodeId, 1);
			List<EdgeHost> lstHosts = new LinkedList<EdgeHost>();
			hostMgrDao.as_edge_host_list(nodeId, 1, lstHosts);
			String message = MessageReader
					.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							EdgeServiceErrorCode.Node_Delete_Linux_D2D_Server_Linux_Node_Exist);
			;
			String failedMessageString = EdgeCMWebServiceMessages
					.getMessage("deleteNodeFailed", lstHosts.get(0)
							.getRhostname(), message);
			logger.error("[LinuxNodeServiceImpl] checkLinuxD2DCanBeDeleted() Delete node failed, because a Linux node exists and this is the only Linux Backup Server.");
			addActivityLog(nodeId, lstHosts.get(0).getRhostname(),
					Severity.Error, EdgeCMWebServiceMessages.getMessage(
							"deleteNodePrefix", failedMessageString),
					Module.DeleteNode);
			throw new EdgeServiceFault(
					"",
					new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_Delete_Linux_D2D_Server_Linux_Node_Exist,
							""));
		}
	}

	private void deleteNodeInDB(int nodeId) {
		connectionInfoDao.as_edge_connect_remove(nodeId);
		hostMgrDao.as_edge_host_remove(nodeId);
		logger.info("LinuxNodeServiceImpl.deleteNodeInDB(): delete node, nodeId:"
				+ nodeId);
	}

	private boolean doesLinuxD2DServerHavePolicy(int d2dserverId) {
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_by_linux_d2d_server(d2dserverId,
				policyList);
		return policyList.size() > 0;
	}

	private boolean doesNodeHavePolicy(int nodeId, int policyType) {
		try {
			List<EdgeHostPolicyMap> mapList = new LinkedList<EdgeHostPolicyMap>();
			this.policyDao.getHostPolicyMap(nodeId, policyType, mapList);
			return (mapList.size() > 0);
		} catch (Exception e) {
			logger.error("doesNodeHavePolicy() failed.", e);
			return false;
		}
	}

	@Override
	public int cancelLinuxJob(int nodeId, String jobUUID)
			throws EdgeServiceFault {
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_linux_d2d_server_by_hostid(nodeId,
				connInfoLst);

		if (connInfoLst.size() == 0)
			return 0;

		EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
		int ret = -1;
		ILinuximagingService service = null;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServerInfo.getHostid())) {
			connection.connect();
			service = connection.getService();
			ret = service.validateByKey(d2dServerInfo.getAuthUuid());
			if (ret == 0) {
				ret = service.cancelJob(jobUUID);
			}
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			ret = -2;
		}
		return ret;
	}

	@Override
	public HostConnectInfo getLinuxD2DServerInfoByHostId(int hostId)
			throws EdgeServiceFault {
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_linux_d2d_server_by_hostid(hostId,
				connInfoLst);
		if (connInfoLst.size() > 0) {
			return getHostConnectInfo(connInfoLst.get(0));
		}
		return null;
	}

	private HostConnectInfo getHostConnectInfo(EdgeConnectInfo connectInfo) {
		if (connectInfo == null)
			return null;

		HostConnectInfo hInfo = new HostConnectInfo();
		hInfo.setHostName(connectInfo.getRhostname());
		hInfo.setId(connectInfo.getHostid());
		hInfo.setUserName(connectInfo.getUsername());
		hInfo.setPassword(connectInfo.getPassword());
		hInfo.setProtocol(Protocol.parse(connectInfo.getProtocol()));
		hInfo.setPort(connectInfo.getPort());
		hInfo.setUuid(connectInfo.getUuid());
		hInfo.setAuthUuid(connectInfo.getAuthUuid());

		return hInfo;

	}

	public void addActivityLog(int nodeId, String nodeName, Severity severity,
			String message, Module module) {
		ActivityLog log = new ActivityLog();
		if (module != null) {
			log.setModule(module);
		}
		log.setHostId(nodeId);
		log.setNodeName(nodeName != null ? nodeName : "");
		log.setMessage(message);
		log.setSeverity(severity);
		log.setTime(new Date());
		try {
			logService.addLog(log);
		} catch (EdgeServiceFault e) {
			logger.error("Error occurs during add activity log", e);
		}
	}

	@Override
	public boolean checkLinuxD2DServerCanBeDeleted(int[] node) {
		List<EdgeHost> backupServerList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, backupServerList);

		List<EdgeHost> linuxNodeList = new ArrayList<EdgeHost>();
		int[] totalCount = new int[1];
		hostMgrDao.as_edge_GetFilteredPagingNodeList(-1, -9, 0, 1, 0, "", 0, 0,
				0, 0, 0, 2, 0, 0, "", 0, 0, 1, "ASC", "rhostname", null,
				totalCount, linuxNodeList);

		return !(backupServerList.size() == node.length && linuxNodeList.size() > 0);
	}

	// linux log collection
	@Override
	public int collectDiagnosticInfo(DiagInfoCollectorConfiguration diagObj,
			int linuxNodeOrServerId, boolean isLinuxBackupServer,
			String linuxServerAuthUUID, String hostname) {
		HostConnectInfo d2dServerInfo = null;
		int ret = 0;
		LinuxDiagInfoCollectorConfiguration linuxDiagInfoConfiguration = new LinuxDiagInfoCollectorConfiguration();
		linuxDiagInfoConfiguration.setAdvancedLogCollection(diagObj
				.getAdvancedLogCollection());
		linuxDiagInfoConfiguration.setDestinationType(diagObj
				.getDestinationType());
		linuxDiagInfoConfiguration.setUserName(diagObj.getUserName());
		linuxDiagInfoConfiguration.setPassword(diagObj.getPassword());
		linuxDiagInfoConfiguration.setUploadDestination(diagObj
				.getUploadDestination());
		linuxDiagInfoConfiguration.setCollectServerlogs(isLinuxBackupServer);
		linuxDiagInfoConfiguration.setServerName(hostname);
		int tempNodeId = linuxNodeOrServerId;
		String tempAuthUUID = linuxServerAuthUUID; // linux server's authUUID
		if (isLinuxBackupServer == false) // if is linux node then get the linux
											// backup server details to connect
		{
			try {
				d2dServerInfo = this
						.getLinuxD2DServerInfoByHostId(linuxNodeOrServerId); // get
																				// linux
																				// backup
																				// server's
																				// details
																				// from
																				// linux
																				// node
																				// id
			} catch (EdgeServiceFault e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			tempNodeId = d2dServerInfo.getId();
			tempAuthUUID = d2dServerInfo.getAuthUuid(); // get linux server's
														// authUUID
		}
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(tempNodeId)) {

			connection.connect();
			ILinuximagingService service = connection.getService();
			if (isLinuxBackupServer == false) {
				ret = service.validateByKey(tempAuthUUID);
			} else {
				ret = 0;
			}
			if (ret == 0) {
				List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
				connectionInfoDao.as_edge_connect_info_list(
						linuxNodeOrServerId, connInfoLst);
				if (connInfoLst.size() == 0) {
					logger.error("cannot get the linux node information from linux node id");
					return -4;
				}
				EdgeConnectInfo nodeConnectionInfo = connInfoLst.get(0);
				linuxDiagInfoConfiguration.setNodeUUID(nodeConnectionInfo
						.getUuid());
				ret = service.collectLogs(linuxDiagInfoConfiguration);
			}
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			ActivityLog log = new ActivityLog();
			log.setModule(Module.All);
			log.setSeverity(Severity.Error);
			log.setNodeName(hostname);
			log.setMessage(EdgeCMWebServiceMessages
					.getResource("DiagUtilityExecFailNode"));
			/*
			 * try { logService.addLog(log); } catch (EdgeServiceFault e1) {
			 * logger.error("Error occurs during add activity log", e1); }
			 */
			return -2;
		} catch (EdgeServiceFault e) {
			logger.error("updateCredentialOnD2DServer", e);
			ActivityLog log = new ActivityLog();
			log.setModule(Module.All);
			log.setSeverity(Severity.Error);
			log.setNodeName(hostname);
			log.setMessage(EdgeCMWebServiceMessages
					.getResource("DiagUtilityExecFailNode"));
			try {
				logService.addLog(log);
			} catch (EdgeServiceFault e1) {
				logger.error("Error occurs during add activity log", e1);
			}
			return -3;
		}
		return ret;
	}

	@Override
	public String getLinuxVersionInfo(int nodeId) throws EdgeServiceFault {
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(nodeId)) {

			connection.connect();
			ILinuximagingService service = connection.getService();
			com.ca.arcserve.linuximaging.webservice.data.VersionInfo linuxInfo = service
					.getVersionInfo();
			if (linuxInfo == null || linuxInfo.getVersion() == null
					|| linuxInfo.getVersion().compareTo("6.0") < 0) {
				throw EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Node_Linux_D2D_Server_Version_Low,
						"Linux D2D version is not 6.0 or above");
			}
			return linuxInfo.getVersion();
		} catch (EdgeServiceFault e) {
			logger.error("getLinuxVersionInfo failed", e);
			throw e;
		}
	}

}
