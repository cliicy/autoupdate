package com.ca.arcserve.edge.app.msp.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.edge.data.d2dstatus.AgentOsInfoType;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.common.connection.ConverterConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IPolicyManagementService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ConversionTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.messages.MspWebServiceMessages;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;

public class MspNodeServiceImpl implements IMspNodeService {
	
	private static final Logger logger = Logger.getLogger(MspNodeServiceImpl.class);
	
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IRpsNodeDao rpsNodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	private IEdgeVSBDao vsbDao;
	IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IPolicyManagementService policyManagementService;
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IActivityLogService logService = new ActivityLogServiceImpl();
	
	private MspWebServiceMessages mspMessages = MessageReader.loadMessages(MspWebServiceMessages.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	public int addNode(String nodeName, String instanceUuid, long osInfoType) throws EdgeServiceFault {
		if (nodeName == null) {
			nodeName = "";
		}
		
		int[] hostId = new int[1];
		//Check same uuid from physical nodes
		hostDao.as_edge_host_getHostIdByUuid(instanceUuid, ProtectionType.WIN_D2D.getValue(),hostId);
	
		if(hostId[0] <= 0){
			//Check same instanceuuid from vms
			esxDao.as_edge_host_getHostByInstanceUUID(0, instanceUuid, hostId);
		}
		
		if(hostId[0]>0){
			EdgeHost host = HostInfoCache.getInstance().getHostInfo(hostId[0]);
			String nodenameP = StringUtil.isEmptyOrNull(nodeName)?instanceUuid:nodeName;
			String nodenamep1 = StringUtil.isEmptyOrNull(host.getRhostname())
					?(EdgeCMWebServiceMessages.getResource("unknown_vm",host.getVmname())):host.getRhostname();
			String site = host.getSiteName();
			String message = EdgeCMWebServiceMessages.getResource("mspNodeExsit", nodenameP,nodenamep1,site);
			addActivityLogForAddRemoteNodes(nodeName, Severity.Warning, message);
			throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_AlreadyExist));
		}
		
		int id = -1;
		logger.info("MspNodeServiceImpl: osInfoType = " + String.valueOf(osInfoType));
		if(!StringUtil.isEmptyOrNull(nodeName))
			nodeName = nodeName.toLowerCase();
		
		int hostType = HostType.EDGE_NODE_IMPORT_FROM_RPS_REPLICA.getValue();
		
		if(osInfoType == AgentOsInfoType.AgentOSType.Linux){
			hostType = HostType.EDGE_NODE_IMPORT_FROM_RPS_REPLICA.getValue() | HostType.EDGE_NODE_LINUX.getValue();
		}
		
		hostDao.as_edge_host_update(id, new Date(), nodeName, "", "", "", "", 1, 0, "", 
				hostType, ProtectionType.WIN_D2D.getValue(), "",hostId); //not set fqdn name for remote node
		
		connectInfoDao.as_edge_connect_info_update(hostId[0], "", "", instanceUuid, 
				0, 0, 0, "", "", "", "", NodeManagedStatus.Managed.ordinal());
		
		// The node is a virtual node, it doesn't need a gateway, so give it a binding record
		// to avoid special processing in other logics. The value of the gateway ID here is
		// meaningless.
		this.gatewayService.bindEntity( new GatewayId( 1 ), hostId[0], EntityType.Node );
		logService.addUnifiedLog(LogAddEntity.create(Severity.Information, hostId[0], mspMessages.importReplicatedRemoteNodeFinished(nodeName)));
		
		return hostId[0];
	}

	public void assignPlan(int nodeId, String nodeName, String mspPlanUuid, String nodeInstanceUuid) throws EdgeServiceFault {
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_by_uuid(mspPlanUuid, policies);
		if (policies.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "cannot find the policy, uuid = " + mspPlanUuid);
		}
		
		EdgePolicy policy = policies.get(0);
		if (!Utils.hasBit(policy.getContentflag(), PlanTaskType.MspServerReplication)) {
			return;
		}

		boolean assignNewPlan = true;
		int deployStatus = PolicyDeployStatus.DeployedSuccessfully;
		List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
		policyDao.getHostPolicyMap(nodeId, PolicyTypes.Unified, mapList);
		if (mapList.size() == 1) {
			EdgeHostPolicyMap map = mapList.get(0);
			if (map.getPolicyId() == policy.getId()) {
				logger.info("The plan has been assigned to the node already");
				assignNewPlan = false;
				deployStatus = map.getDeployStatus();
			}
		}

		if (assignNewPlan && Utils.hasVSBTask(policy.getContentflag())) {
			// Check whether the node has converter or not
			List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
			getVsbDao().as_edge_vsb_converter_getByHostId(nodeId, converterList);
			if (converterList.size() == 0) {
				logger.info("The node has no converter information, so we will deploy the vsb task again.");
				deployStatus = PolicyDeployStatus.ToBeDeployed;
			} else {
				// Get converter
				logger.info("Check whether the node need to deploy vsb task or not from converter .");
				UnifiedPolicy plan = getPolicyManagementService().loadUnifiedPolicyById(policy.getId());
				ConversionTask conversionTask = plan.getConversionConfiguration();
				if (conversionTask != null) {
					IConnectionContextProvider provider = new ConverterConnectionContextProvider(conversionTask.getConverter().getHostId());
					try (D2DConnection connection = connectionFactory.createD2DConnection(provider)) {
						connection.connect();
						
						if (connection.getService().ifNeedDeployVCMJobPolicyForMSP(nodeInstanceUuid, plan.getUuid())) {
							logger.info("Need deploy vsb task again.");
							deployStatus = PolicyDeployStatus.ToBeDeployed;
						} else {
							logger.info("Converter already has the same task, so didn't need to deploy again.");
						}
					} catch (Exception e) {
						logger.error(
								"Failed to check plan status for node:" + nodeName + " with plan:" + plan.getName(), e);
					}
				}
			}
		}

		policyDao.assignPolicy(nodeId, PolicyTypes.Unified, policy.getId(), deployStatus,0);
		
		if (assignNewPlan) {
			logService.addUnifiedLog(LogAddEntity.create(Severity.Information, nodeId, 
					mspMessages.assignPlanToReplicatedRemoteNodeFinished(policy.getName(), nodeName)));
		}
		
		if (assignNewPlan && deployStatus != PolicyDeployStatus.DeployedSuccessfully) {
			PolicyManagementServiceImpl.getInstance().getPolicyDeploymentScheduler().doDeploymentNowByPlanId(policy.getId());
		}
	}

	@Override
	public void importNodesFromRPS(int rpsHostId) throws EdgeServiceFault {
		List<EdgeRpsNode> rpsNodes = new ArrayList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list(rpsHostId, rpsNodes);
		
		if (rpsNodes.isEmpty()) {
			return;
		}
		
		Set<Integer> mspReplicateRpsServerIds = getMspReplicateRpsServerIds();
		
		for (EdgeRpsNode rpsNode : rpsNodes) {
			if (mspReplicateRpsServerIds.contains(rpsNode.getNode_id())) {
				importMspReplicateNodesFromRPS(rpsNode);
			}
		}
	}
	
	public Set<Integer> getMspReplicateRpsServerIds() throws EdgeServiceFault {
		Set<Integer> ids = new HashSet<Integer>();
		
		MspPlanServiceImpl mspPlanService = new MspPlanServiceImpl();
		List<PolicyInfo> mspPlans = mspPlanService.getCustomerPlans(0);
		
		for (PolicyInfo plan : mspPlans) {
			UnifiedPolicy policy = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(plan.getPolicyId());
			if (policy.getMspServerReplicationSettings() != null) {
				ids.add(policy.getMspServerReplicationSettings().getHostId());
			}
		}
		
		return ids;
	}
	
	private void importMspReplicateNodesFromRPS(EdgeRpsNode rpsNode) throws EdgeServiceFault {
		try(RPSConnection connection = connectionFactory.createRPSConnection(rpsNode.getNode_id())){
			connection.connect();
			
			List<BackupD2D> nodes = connection.getService().getRegistedClientList();
			if (nodes != null) {
				for (BackupD2D d2d : nodes) {
					if (d2d.isReplicatedClient()) {
						try {
							importRemoteNodeFromRPS(d2d);
						} catch (EdgeServiceFault e) {
							if(EdgeServiceErrorCode.Node_AlreadyExist == e.getFaultInfo().getCode()){
								String hostname = d2d.isVm() ? d2d.getVmHostName() : d2d.getHostname();
								logger.warn("Msp4RpsServiceImpl.importRemoteNodeFromRPS - node aready exist, "
										+ "skip this node: "+hostname+" and continue.");
								continue;
							}else {
								throw e;
							}
						}
						
					}
				}
			}
		}
	}
	
	private void importRemoteNodeFromRPS(BackupD2D d2d) throws EdgeServiceFault {
		String instanceUuid = WSJNI.AFDecryptStringEx(d2d.getClientUUID() == null ? "" : d2d.getClientUUID());
		String hostname = d2d.isVm() ? d2d.getVmHostName() : d2d.getHostname();
		int nodeId = addNode(hostname, instanceUuid, AgentOsInfoType.AgentOSType.UNKNOWN);
		assignPlan(nodeId, hostname, d2d.getPlanUUID(), instanceUuid);
	}

	public IPolicyManagementService getPolicyManagementService() {
		if (policyManagementService == null) {
			policyManagementService = PolicyManagementServiceImpl.getInstance();
		}
		return policyManagementService;
	}

	public IEdgeVSBDao getVsbDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}
	
	public void addActivityLogForAddRemoteNodes(String nodeName, Severity severity, String message){
		ActivityLog log = new ActivityLog();
		log.setNodeName(nodeName != null ? nodeName : "");
		log.setMessage(message);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setModule(Module.ImportNodesFromFile);
		log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_File_Log", log.getMessage()));
		try {
			logService.addLog(log);
		} catch (Exception e) {
			logger.error("[MspNodeServiceImpl] addActivityLogForAddRemoteNodes(): "
					+ "Error occurs during add activity log", e);
		}
	}
}
