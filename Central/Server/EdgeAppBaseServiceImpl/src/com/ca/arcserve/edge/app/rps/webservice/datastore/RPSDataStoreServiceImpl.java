package com.ca.arcserve.edge.app.rps.webservice.datastore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.data.ConnectionInfoEx;
import com.ca.arcflash.rps.webservice.data.DisabledNodes;
import com.ca.arcflash.rps.webservice.data.MostRecentRecoveryPoint;
import com.ca.arcflash.rps.webservice.data.RPSSessPwdValidationItem;
import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.ds.HashRoleEnvInfo;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicyType;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;
import com.ca.arcflash.rps.webservice.replication.MigrationRPItem;
import com.ca.arcflash.rps.webservice.replication.SeedingItem;
import com.ca.arcflash.rps.webservice.replication.SeedingJobParameter;
import com.ca.arcflash.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.data.ConnectionInfo;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.edge.data.d2dstatus.AgentOsInfoType;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoUtils;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.datastore.DataSeedingJobScript;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSDataStoreService;

public class RPSDataStoreServiceImpl implements IRPSDataStoreService,Observer{

	private IRpsNodeDao rpsNodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	private IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private final static Logger log = Logger.getLogger(RPSDataStoreServiceImpl.class);
	private static RPSNodeServiceImpl rpsService = new RPSNodeServiceImpl();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	@Override
	public List<DataStoreSettingInfo> getDataStoreListByNode(int nodeId) throws EdgeServiceFault {
		return DataStoreManager.getInstance().getDataStoreByNodeId(nodeId);
	}

	@Override
	public DataStoreSettingInfo getDataStoreById(int dedupId) throws EdgeServiceFault {
		return DataStoreManager.getInstance().getDataStoreByDsId(dedupId);
	}

	@Override
	public HashRoleEnvInfo getHashRoleEnvInfo(int nodeID) throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			return proxy.getServiceForCPM().getHashRoleEnvInfo();
		}
	}
	
	@Override
	public void createFolder(int nodeID, NodeRegistrationInfo rpsInfo, String parentPath, String subDir,String username, String password) throws EdgeServiceFault {
		if(nodeID == 0 && rpsInfo != null){
			ConnectionContext context = new ConnectionContext(rpsInfo.getD2dProtocol(), rpsInfo.getNodeName(), rpsInfo.getD2dPort());
			context.buildCredential(rpsInfo.getUsername(), rpsInfo.getPassword(), "");
			GatewayEntity gateway = gatewayService.getGatewayById(rpsInfo.getGatewayId());
			context.setGateway(gateway);
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				RPSWebServiceClientProxy proxy = connection.getClientProxy();
				proxy.getServiceForCPM().createFolderWithDetails(parentPath, subDir, username, password);
			}
		}else{
			try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				proxy.getServiceForCPM().createFolderWithDetails(parentPath, subDir, username, password);
			}
		}
	}
	
	@Override
	public FileFolderItem getFileItems(int nodeID, NodeRegistrationInfo rpsInfo, String inputFolder, String username, String password, boolean bIncludeFiles, int browseClient) throws EdgeServiceFault{
		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}
		
		if (inputFolder != null && inputFolder.endsWith("\\") && !inputFolder.endsWith("\\\\"))
			inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("\\"));
			
		if (inputFolder != null && inputFolder.endsWith("/"))
			inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("/"));
		
		RPSWebServiceClientProxy proxy;
		if(nodeID == 0 && rpsInfo != null){
			ConnectionContext context = new ConnectionContext(rpsInfo.getD2dProtocol(), rpsInfo.getNodeName(), rpsInfo.getD2dPort());
			context.buildCredential(rpsInfo.getUsername(), rpsInfo.getPassword(), "");
			GatewayEntity gateway = gatewayService.getGatewayById(rpsInfo.getGatewayId());
			context.setGateway(gateway);
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				proxy = connection.getClientProxy();
				if (browseClient == 2) {
					username = rpsInfo.getUsername();
					password = rpsInfo.getPassword();
				}
			}
		}else{
			try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
				proxy = conn.getClientProxy();
				EdgeRpsNode rpsNode= EdgeCommonUtil.getRPSNodeInfo(nodeID, rpsNodeDao);
				if (browseClient == 2) {
					username = rpsNode.getUsername();
					password = rpsNode.getPassword();
				}
			}
		}		
			
		FileFolderItem item = proxy.getServiceForCPM().getFileFolderWithCredentials(inputFolder, username, password);
			
		return item;
	}
	
	@Override
	public Volume[] getVolumes(int nodeID, NodeRegistrationInfo rpsInfo ,int browseClient) throws EdgeServiceFault{
		if(nodeID == 0 && rpsInfo != null){
			ConnectionContext context = new ConnectionContext(rpsInfo.getD2dProtocol(), rpsInfo.getNodeName(), rpsInfo.getD2dPort());
			context.buildCredential(rpsInfo.getUsername(), rpsInfo.getPassword(), "");
			GatewayEntity gateway = gatewayService.getGatewayById(rpsInfo.getGatewayId());
			context.setGateway(gateway);
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				RPSWebServiceClientProxy proxy = connection.getClientProxy();
				return proxy.getServiceForCPM().getVolumes();
			}
		}else{
			try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				return proxy.getServiceForCPM().getVolumes();
			}
		}
	}

	@Override
	public void cutAllRemoteConnections(int nodeID, NodeRegistrationInfo rpsInfo) throws EdgeServiceFault {
		if(nodeID == 0 && rpsInfo != null){
			ConnectionContext context = new ConnectionContext(rpsInfo.getD2dProtocol(), rpsInfo.getNodeName(), rpsInfo.getD2dPort());
			context.buildCredential(rpsInfo.getUsername(), rpsInfo.getPassword(), "");
			GatewayEntity gateway = gatewayService.getGatewayById(rpsInfo.getGatewayId());
			context.setGateway(gateway);
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				RPSWebServiceClientProxy proxy = connection.getClientProxy();
				proxy.getServiceForCPM().cutAllRemoteConnections();
			}
		}else{
			try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				proxy.getServiceForCPM().cutAllRemoteConnections();
			}
		}

	}
	
	@Override
	public long validateDestOnly(int nodeID, NodeRegistrationInfo rpsInfo, String path, String domain, String user, String pwd, int mode) throws EdgeServiceFault{
		path = path == null ? "":path;
		domain = domain == null ? "":domain;
		user = user == null ? "":user;
		pwd = pwd == null ? "":pwd;
		
		if (domain.trim().length() == 0) {
			int indx = user.indexOf('\\');
			if (indx > 0) {
				domain = user.substring(0, indx);
				user = user.substring(indx + 1);
			}
		}
		if(nodeID == 0 && rpsInfo != null){
			ConnectionContext context = new ConnectionContext(rpsInfo.getD2dProtocol(), rpsInfo.getNodeName(), rpsInfo.getD2dPort());
			context.buildCredential(rpsInfo.getUsername(), rpsInfo.getPassword(), "");
			GatewayEntity gateway = gatewayService.getGatewayById(rpsInfo.getGatewayId());
			context.setGateway(gateway);
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
				connection.connect();
				RPSWebServiceClientProxy proxy = connection.getClientProxy();
				return proxy.getServiceForCPM().validateDest(path, domain, user, pwd);
			}
		}else{
			try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
				RPSWebServiceClientProxy proxy = conn.getClientProxy();
				return proxy.getServiceForCPM().validateDest(path, domain, user, pwd);
			}
		}
	}

	@Override
	public String saveDataStoreSetting(DataStoreSettingInfo settingInfo) throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().save(settingInfo);
	}
	
	@Override
	public boolean checkDataStoreDuplicate(DataStoreSettingInfo settingInfo) throws EdgeServiceFault{
		return  DataStoreManager.getDataStoreManager().checkDataStoreDuplicate(settingInfo);
	}
	
	@Override
	public DataStoreSettingInfo getDataStoreByGuid(int nodeId, String guid) throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().getDataStoreByGuid(nodeId, guid);
	}
	
	@Override
	public List<DataStoreStatusListElem> getDataStoreSummariesByNodefromCache( int nodeId ) throws EdgeServiceFault {
		return StatusUtil.getDataStoreSummaryByNodeId( nodeId );	
	}
	@Override
	public List<DataStoreStatusListElem> getDataStoreSummariesByNode(int nodeId) throws EdgeServiceFault {
		return  Arrays.asList(DataStoreManager.getDataStoreManager().getDataStoreSummariesByNode(nodeId));
	}
	@Override
	public void triggerDataStoreSummarySync() throws EdgeServiceFault {
		StatusUtil.initDataStoreSummaryInfo();
	}
	@Override
	public DataStoreStatusListElem getDataStoreSummary(int nodeId, String guid) throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().getDataStoreSummary(nodeId, guid);
	}

	@Override
	public void startDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		DataStoreManager.getDataStoreManager().startDataStoreInstance(nodeId, dataStoreUuid);
	}

	@Override
	public void stopDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		DataStoreManager.getDataStoreManager().stopDataStoreInstance(nodeId, dataStoreUuid);	
	}

	@Override
	public DataStoreSettingInfo importDataStoreInstance(int nodeID,
			DataStoreSettingInfo storeSettings, boolean bOverWrite,
			boolean bForceTakeOwnership) throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().importDataStoreInstance(
				nodeID, storeSettings, bOverWrite, bForceTakeOwnership);
	}

	@Override
	public DataStoreSettingInfo getDataStoreInfoFromDisk(int nodeID, String strPath,
			String strUser, String strPassword, String strDataStorePassword)
			throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().getDataStoreInfoFromDisk(nodeID, strPath,
				strUser, strPassword, strDataStorePassword);
	}

	@Override
	public List<DataStoreSettingInfo> getDataStoreHistoryByGuid(int nodeId,
			String guid, Date timeStamp) throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().getDataStoreHistoryByGuid(nodeId, guid, timeStamp);
	}

	@Override
	public long getDataStoreDedupeRequiredMinMemSizeByte(int nodeId, String dataStoreId)
			throws EdgeServiceFault {
		return DataStoreManager.getDataStoreManager().getDataStoreDedupeRequiredMinMemSizeByte(nodeId, dataStoreId);
	}

	@Override
	public List<PlanInDestination> getNodesFromDataStroe(int rpsNodeId,
			String dataStoreUUID, boolean filterNullClientUuid) throws EdgeServiceFault {
		List<BackupD2D> nodes ;
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			nodes = service.getRegistedClientByDatastore(dataStoreUUID);
		}
		if (nodes == null) {
			return null;
		}
		log.debug("getNodesFromDataStroe");
		Map<String, List<ProtectedNodeInDestination>> planCache = new HashMap<String, List<ProtectedNodeInDestination>>();//plan name <-> node list
//		List<ProtectedNodeInDestination> protectedNodes = new ArrayList<ProtectedNodeInDestination>();
		for (BackupD2D d2d : nodes) {
			if (filterNullClientUuid) {
				if (d2d.getClientUUID() == null || "".equals(d2d.getClientUUID())) {
					//Block the nodes which don't have uuid
					continue;
				}
			}
			ProtectedNodeInDestination node = new ProtectedNodeInDestination();
			node.setNodeName(d2d.getHostname());
			node.setNodeUuid(d2d.getClientUUID());
			node.setMostRecentRecoveryPoint(null);
			node.setRecoveryCount(-1);
			node.setDestination(d2d.getFullBackupDestination());
			node.setUsername(d2d.getDesUsername());
			node.setPassword(d2d.getDesPassword());
			node.setPlanUuid(d2d.getPlanUUID());
			node.setHaveSessions(d2d.getLastBackupTime()<=0?false:true);
			node.setIntegral(d2d.isIntegral());
			int[] hostIds = new int[1];
			hostDao.as_edge_host_getHostIdByUuid(d2d.getClientUUID(), ProtectionType.WIN_D2D.getValue(),
					hostIds);
			if(hostIds[0]==0){//check whether it is vm
				hostDao.as_edge_host_vm_by_instanceUUID(d2d.getClientUUID(), hostIds);
			}
			node.setNodeId(hostIds[0]);
			node.setParentRpsNodeId(rpsNodeId);
			node.setParentDataStoreUUID(dataStoreUUID);
			node.setParentDataStoreName(d2d.getDatastoreName());
			String planName="";
			
			//issue 108256
			List<PolicyInfo> policyInfoList = new ArrayList<PolicyInfo>();
			policyDao.as_edge_policy_list_by_hostId(node.getNodeId(), policyInfoList);
			if(policyInfoList!=null && policyInfoList.size()>0){
				if(!StringUtil.isEmptyOrNull(d2d.getPlanUUID())){
					List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
					policyDao.as_edge_policy_list_by_uuid(d2d.getPlanUUID(), policies);
					if(policies!=null && policies.size()>0 && policyInfoList.get(0).getPolicyUuid().equalsIgnoreCase(policies.get(0).getUuid())){
						planName = policies.get(0).getName();
					}
				}
				/*//Bug 761350: remove "replicate now" from browse recovery point page
				// get policy 's replication rpsPolicy uuid
				List<ManualReplicationRPSParam> paramslist = PolicyManagementServiceImpl.getInstance().getReplicationRpsParamsByPolicyId(policyInfoList.get(0).getPolicyId());
				if (paramslist!=null&& (!paramslist.isEmpty())) {
					for (ManualReplicationRPSParam param:paramslist) {
						if(param.getSrcRpsHostId()==rpsNodeId){
							node.setCanReplication(true);
							node.setReplicationRPSPolicy(param.getReplicationItem().getPolicyUUID());
							break;
						}
					}
				}*/
				
			}
			
			node.setPlanName(planName);
			List<ProtectedNodeInDestination> nodeList = planCache.get(planName);
			if(nodeList == null){
				nodeList = new ArrayList<ProtectedNodeInDestination>();
				nodeList.add(node);
				planCache.put(planName, nodeList);
			}else{
				nodeList.add(node);
			}
			log.debug("nodename:" +d2d.getHostname()+ ",  NodeUuid:"+ d2d.getClientUUID()+ ", Destination:" + d2d.getFullBackupDestination() +
					", DesUsername:" + d2d.getDesUsername() + ", PlanUuid:" + d2d.getPlanUUID() + ", LastBackupTime:" + d2d.getLastBackupTime() + 
					", Integral:" + d2d.isIntegral());
//			protectedNodes.add( node ); 
		}
		List<PlanInDestination> plans = new ArrayList<PlanInDestination>();
		Iterator<String> iterator = planCache.keySet().iterator();
		PlanInDestination noNamePlan = new PlanInDestination();
		while (iterator.hasNext()) {
			String planName = iterator.next();
			if(planName.equals("")){
				createPlanDateStore(noNamePlan, planName, planCache);
				continue;
			}
			PlanInDestination plan = new PlanInDestination();
			createPlanDateStore(plan, planName, planCache);
			plans.add(plan);
		}
		Collections.sort(plans, new Comparator<PlanInDestination>() {
			@Override
			public int compare(PlanInDestination o1, PlanInDestination o2) {
				if(o1.getPlanName()!=null && o2.getPlanName()!=null){
					return o1.getPlanName().compareTo(o2.getPlanName());
				}else if (o1.getPlanName()==null && o2.getPlanName()!=null) {
					return -1;
				}else if (o1.getPlanName()!=null && o2.getPlanName()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		if(noNamePlan.getNodeList()!=null)
			plans.add(noNamePlan);
		return plans;
//		return RecoveryPointBrowseUtil.getInstance().groupProtectedNodeByPlan( protectedNodes , true );
	}
	
	private void createPlanDateStore(PlanInDestination plan ,String planName, Map<String, List<ProtectedNodeInDestination>> planCache){
		plan.setPlanName(planName);
		List<ProtectedNodeInDestination> nodeList = planCache.get(planName);
		Collections.sort(nodeList,new Comparator<ProtectedNodeInDestination>() {

			@Override
			public int compare(ProtectedNodeInDestination o1, ProtectedNodeInDestination o2) {
				if(o1.getNodeName()!=null && o2.getNodeName()!=null){
					return o1.getNodeName().compareTo(o2.getNodeName());
				}else if (o1.getNodeName()==null && o2.getNodeName()!=null) {
					return -1;
				}else if (o1.getNodeName()!=null && o2.getNodeName()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		plan.setNodeList(nodeList);
		plan.setRecoveryPointCount(-1);
	}
	
	@Override
	public List<ProtectedNodeInDestination> getNodesDetailFromDataStore(
			int rpsNodeId,List<ProtectedNodeInDestination> originalNodeList) throws EdgeServiceFault {
		List<ConnectionInfoEx> nodeConnectList = new ArrayList<ConnectionInfoEx>();
		for(ProtectedNodeInDestination orignalNode : originalNodeList){
			nodeConnectList.add(getConnectionInfo(rpsNodeId, orignalNode));
		}
		List<MostRecentRecoveryPoint> mostRecentRecoveryPoints ;
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			mostRecentRecoveryPoints = service.getMostRecentRecoveryPoints(nodeConnectList);
		}
		for(MostRecentRecoveryPoint mostRecentRecoveryPoint : mostRecentRecoveryPoints){
			for(ProtectedNodeInDestination oNode: originalNodeList){
				if(isSameProtectedNode(mostRecentRecoveryPoint, oNode)) {
					if(mostRecentRecoveryPoint.getRecoveryPointCnt()>0 
							&& mostRecentRecoveryPoint.getMostRecentRecoveryPointLst()!=null 
							&& mostRecentRecoveryPoint.getMostRecentRecoveryPointLst().size()>0
							&& mostRecentRecoveryPoint.getMostRecentRecoveryPointLst().get(0)!=null){
						String lastbackupTime= mostRecentRecoveryPoint.getMostRecentRecoveryPointLst().get(0).getLastBackupTime();
						if(!StringUtil.isEmptyOrNull(lastbackupTime))
							oNode.setMostRecentRecoveryPoint(DaoUtils.toUTC(BackupConverterUtil.string2Date(lastbackupTime)));
						else
							oNode.setMostRecentRecoveryPoint(null);
						String firstbackupTime= mostRecentRecoveryPoint.getEarlistRecoveryPointList().get(0).getLastBackupTime();
						if(!StringUtil.isEmptyOrNull(firstbackupTime))
							oNode.setFirstRecoveryPoint(DaoUtils.toUTC(BackupConverterUtil.string2Date(firstbackupTime)));
						else
							oNode.setFirstRecoveryPoint(null);
						
						if(mostRecentRecoveryPoint.getOsInfo() != null){
							if(AgentOsInfoType.AgentOSType.Linux == mostRecentRecoveryPoint.getOsInfo().getAgentOSType()){
								oNode.setLinux(true);
							}
							
							if (AgentOsInfoType.VMGuestOsType.Linux == mostRecentRecoveryPoint.getOsInfo().getVmGuestOsType()) {
								oNode.setLinux(true);
							}							
						}
						
						log.debug("NodeName:" + oNode.getNodeName() + ", RecoveryCount:" + mostRecentRecoveryPoint.getRecoveryPointCnt()
								+ ", firstbackupTime:" + mostRecentRecoveryPoint.getEarlistRecoveryPointList().get(0).getLastBackupTime()
								+ ", agentOsType: " + (mostRecentRecoveryPoint.getOsInfo()==null?"unkown":mostRecentRecoveryPoint.getOsInfo().getAgentOSType())
								+ ", vmgestosType: " + (mostRecentRecoveryPoint.getOsInfo()==null?"unkown":mostRecentRecoveryPoint.getOsInfo().getVmGuestOsType()));
					}else {
						oNode.setMostRecentRecoveryPoint(null);
						oNode.setFirstRecoveryPoint(null);
						
						log.debug("NodeName:" + oNode.getNodeName() + ", RecoveryCount:" + mostRecentRecoveryPoint.getRecoveryPointCnt());
					}
					
					oNode.setRecoveryCount(mostRecentRecoveryPoint.getRecoveryPointCnt());
				}
			}
		}
		return originalNodeList;
	}
	
	private boolean isSameProtectedNode(MostRecentRecoveryPoint recoveryPoint, ProtectedNodeInDestination node){
		String clientUUID = recoveryPoint.getClientUUID()==null?"":recoveryPoint.getClientUUID();
		String clientNodeName = recoveryPoint.getClientName()==null?"":recoveryPoint.getClientName();
		String recoveryPointKey = clientUUID+clientNodeName;
		
		clientUUID = node.getNodeUuid()==null?"":node.getNodeUuid();
		clientNodeName = node.getNodeName()==null?"":node.getNodeName();
		String nodeKey = clientUUID+clientNodeName;
		
		if(recoveryPointKey.equals(nodeKey)){
			return true;
		}
		return false;
	}
	
	private ConnectionInfoEx getConnectionInfo(int rpsNodeId , ProtectedNodeInDestination node){
		if(StringUtil.isEmptyOrNull(node.getUsername())||StringUtil.isEmptyOrNull(node.getPassword())){ //for rps local , need set the username and password for destination access
			List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
			rpsNodeDao.as_edge_rps_node_list(rpsNodeId, nodeList);
			if(nodeList!=null && nodeList.size()>0){
				if(StringUtil.isEmptyOrNull(node.getUsername())){
					node.setUsername(nodeList.get(0).getUsername());
				}
				if(StringUtil.isEmptyOrNull(node.getPassword())){
					node.setPassword(nodeList.get(0).getPassword());
				}
			}
		}
		ConnectionInfoEx connectionInfo = new ConnectionInfoEx();
		connectionInfo.setClientUUID(node.getNodeUuid());
		connectionInfo.setDestination(node.getDestination());
		connectionInfo.setUserName(com.ca.arcserve.edge.app.base.webservice.contract.common.Utils.getUserNameNoDomain(node.getUsername()));
		connectionInfo.setPassword(node.getPassword());
		connectionInfo.setDomain(com.ca.arcserve.edge.app.base.webservice.contract.common.Utils.getDomainByUserName(node.getUsername()));
		connectionInfo.setClientName(node.getNodeName());
		return connectionInfo;
	}
	
	@Override
	public List<ProtectedNodeInDestination> getDataSeedingNodes(int sourceRpsNodeId, String sourceDataStoreUuid) throws EdgeServiceFault {
		List<ProtectedNodeInDestination> seedingNodes = new ArrayList<ProtectedNodeInDestination>();
		
		List<BackupD2D> nodes;
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(sourceRpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			nodes = service.getRegistedClientByDatastore(sourceDataStoreUuid);
		}
		if (nodes == null) {
			return seedingNodes;
		}
		
		Map<String, String> planCache = new HashMap<String, String>();
		
		for (BackupD2D d2d : nodes) {
			if (d2d.getPlanUUID() == null) {
				continue;
			}
			
			ProtectedNodeInDestination seedingNode = new ProtectedNodeInDestination();
			
			String planName = planCache.get(d2d.getPlanUUID());
			if (planName == null) {
				List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
				policyDao.as_edge_policy_list_by_uuid(d2d.getPlanUUID(), policies);
				
				if (!policies.isEmpty()) {
					planName = policies.get(0).getName();
					planCache.put(d2d.getPlanUUID(), planName);
				}
			}
			
			if (planName != null) {
				seedingNode.setNodeName(d2d.getHostname());
				seedingNode.setNodeUuid(d2d.getClientUUID());
				seedingNode.setPlanName(planName);
				
				seedingNodes.add(seedingNode);
			}
		}
		
		return seedingNodes;
	}

	@Override
	public void submitDataSeedingJob(DataSeedingJobScript script) throws EdgeServiceFault {
		if (script.getSeedingNodes() == null || script.getSeedingNodes().isEmpty()) {
			return;
		}
		
		String seedingPlanUuid = "06b5b33c-1a86-4570-a576-2a466a52bbf3";	// TODO: dummy plan UUID for job monitor/events
		
		RPSConnection conn=null;
		RPSConnection conn1=null;
		try{
			conn=EdgeCommonUtil.getRPSServerProxyByNodeId(script.getTargetRpsNodeId());
				
			IRPSService4CPM targetRpsService = conn.getClientProxy().getServiceForCPM();
			targetRpsService.checkIsSeedingRunning(null);
			RPSPolicy targetRpsPolicy = createSeedingJobTargetPolicy(script, seedingPlanUuid);
			if (script.isFromShareFolder()) {
				targetRpsPolicy.setPolicyType(RPSPolicyType.RPS_POLICY_MIGRATION_SHARE_FOLDER);
			}
			targetRpsService.saveRPSPolicy(targetRpsPolicy, new DisabledNodes());
			
			RPSPolicy sourceRpsPolicy = createSeedingJobSourcePolicy(script, seedingPlanUuid, targetRpsPolicy);
			if (script.isFromShareFolder()) {
				sourceRpsPolicy.setPolicyType(RPSPolicyType.RPS_POLICY_MIGRATION_SHARE_FOLDER);
			}
			IRPSService4CPM sourceRpsService;
			
			try {
				if (script.getSourceRpsNodeId() == script.getTargetRpsNodeId()
						|| script.getSourceRpsNodeId() == -1) {
					sourceRpsService = targetRpsService;
				} else {
					conn1=EdgeCommonUtil.getRPSServerProxyByNodeId(script.getSourceRpsNodeId());
					sourceRpsService = conn1.getClientProxy().getServiceForCPM();
				}
				
				sourceRpsService.saveRPSPolicy(sourceRpsPolicy, new DisabledNodes());
			} catch (Exception e) {
				targetRpsService.deleteRPSPolicies(Arrays.asList(targetRpsPolicy.getId()));
				throw e;
			}
			SeedingJobParameter parameter = new SeedingJobParameter();
			parameter.setItems(new ArrayList<SeedingItem>());
			if (script.isFromShareFolder()) {
				parameter.setPolicyUUID(sourceRpsPolicy.getId());
				for (ProtectedNodeInDestination node : script.getSeedingNodes()) {
					String nodeName = node.getNodeName();
					boolean hasFound = false;
					for (SeedingItem seedingItem : parameter.getItems()) {
						if (seedingItem.getSrcShareFolderPath().endsWith(nodeName)) {
							hasFound = true;
							for (String sessionGuid : node.getSessionGuidList()) {
								MigrationRPItem rpItem = new MigrationRPItem();
								rpItem.setSessionGuid(sessionGuid);
								if (node.getEncryptPasswordHash() != null && !"".equals(node.getEncryptPasswordHash())) {
									rpItem.setSessionPwd(node.getPassword());
								} else {
									rpItem.setSessionPwd("");
								}
								rpItem.setTargetSessionPwd(node.getPassword());
								if (seedingItem.getMigrationRPInfo() == null) {
									seedingItem.setMigrationRPInfo(new ArrayList<MigrationRPItem>());
								}
								seedingItem.getMigrationRPInfo().add(rpItem);
							}
						}
					}
					if (!hasFound) {
						SeedingItem seedingItem = new SeedingItem();
						seedingItem.setbSeedingFromShare(true);
						seedingItem.setClientName(node.getNodeName());
						seedingItem.setMigrationRPInfo(new ArrayList<MigrationRPItem>());
						for (String sessionGuid : node.getSessionGuidList()) {
							MigrationRPItem rpItem = new MigrationRPItem();
							rpItem.setSessionGuid(sessionGuid);
							if (node.getEncryptPasswordHash() != null && !"".equals(node.getEncryptPasswordHash())) {
								rpItem.setSessionPwd(node.getPassword());
							} else {
								rpItem.setSessionPwd("");
							}
							rpItem.setTargetSessionPwd(node.getPassword());
							seedingItem.getMigrationRPInfo().add(rpItem);
						}
						seedingItem.setShareFolderPassword(node.getSfPassword());
						seedingItem.setShareFolderUserName(node.getSfUserName());
						seedingItem.setSrcShareFolderPath(node.getSfFullPath());
						parameter.getItems().add(seedingItem);
					}
				}
				try {
					targetRpsService.submitSeedingJob(parameter);
				} catch (Exception e) {
					throw e;
				}
			} else {
				parameter.setPolicyUUID(sourceRpsPolicy.getId());
				
				for (ProtectedNodeInDestination node : script.getSeedingNodes()) {
					SeedingItem item = new SeedingItem();
					item.setClientName(node.getNodeName());
					item.setClientUUID(node.getNodeUuid());
					parameter.getItems().add(item);
				}
				
				try {
					sourceRpsService.submitSeedingJob(parameter);
				} catch (Exception e) {
					sourceRpsService.deleteRPSPolicies(Arrays.asList(sourceRpsPolicy.getId()));
					targetRpsService.deleteRPSPolicies(Arrays.asList(targetRpsPolicy.getId()));
					throw e;
				}
			}
		}finally{
			if(conn!=null)
				conn.close();
			if(conn1!=null)
				conn1.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	private RPSPolicy createDefaultSeedingPolicy(String planUuid) {
		RPSPolicy policy = new RPSPolicy();
		
		policy.setId(UUID.randomUUID().toString());
		policy.setName(policy.getId());
		policy.setPlanUUID(planUuid);
		
		policy.getRpsSettings().getRpsBasicSettings().setMaxIncrementalSessions(7);
		policy.getRpsSettings().getRpsBasicSettings().setMaxIncrementalSessionsUnit(2);
		policy.getRpsSettings().getRpsBasicSettings().setMaxSyntheticalAndFullSessions(4);
		
		policy.getRpsSettings().getRpsRetentionSettings().setRetentionCount(1);
		
		return policy;
	}
	
	private RPSPolicy createSeedingJobTargetPolicy(DataSeedingJobScript script, String planUuid) {
		RPSPolicy policy = createDefaultSeedingPolicy(planUuid);
		
		policy.getRpsSettings().getRpsDataStoreSettings().setDataStoreName(script.getTargetDataStoreUuid());
		
		return policy;
	}

	private RPSPolicy createSeedingJobSourcePolicy(DataSeedingJobScript script, String planUuid, RPSPolicy targetRpsPolicy) {
		RPSPolicy policy = createDefaultSeedingPolicy(planUuid);
		
		policy.getRpsSettings().getRpsDataStoreSettings().setDataStoreName(script.getSourceDataStoreUuid());
		
		RPSReplicationSettings settings = policy.getRpsSettings().getRpsReplicationSettings();
		settings.setEnableReplication(true);
		
		EdgeRpsNode node = RpsNodeUtil.getNodeById(script.getTargetRpsNodeId());
		settings.setHostName(node.getNode_name());
		settings.setUserName(node.getUsername());
		settings.setPassword(node.getPassword());
		settings.setUuid(node.getUuid());
		settings.setProtocol(node.getProtocol() == Protocol.Https.ordinal() ? 1 : 0);
		settings.setPort(node.getPort());
		
		settings.getReplicationPolicySettings().setUuid(targetRpsPolicy.getId());
		settings.getReplicationPolicySettings().setName(targetRpsPolicy.getName());
		
		if (script.getProxy() != null) {
			settings.setEnableProxy(script.getProxy().isUseProxy());
			settings.setProxyHostname(script.getProxy().getProxyServerName());
			settings.setProxyPort(script.getProxy().getProxyServerPort());
			settings.setProxyRequireAuthentication(script.getProxy().isProxyRequiresAuth());
			settings.setProxyUsername(script.getProxy().getProxyUserName());
			settings.setProxyPassword(script.getProxy().getProxyPassword());
		}
		
		return policy;
	}
	
	@Override
	public List<SessionPassword> validateSessionPassword(int rpsNodeId, List<SessionPassword> list) throws EdgeServiceFault {
		
		List<RPSSessPwdValidationItem> pwdItems = new ArrayList<RPSSessPwdValidationItem>();
		for (SessionPassword sessionPassword : list) {
			RPSSessPwdValidationItem para = new RPSSessPwdValidationItem();
			para.setPwdHash(sessionPassword.getEncryptPasswordHash());
			para.setSessionPwd(sessionPassword.getPassword());
			pwdItems.add(para);
		}
		
		List<SessionPassword> returnList = new ArrayList<SessionPassword>();
		
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			List<RPSSessPwdValidationItem> resultList = proxy.getServiceForCPM().validateSessPasswordByHash(pwdItems);
			
			for (RPSSessPwdValidationItem result : resultList) {
				SessionPassword returnItem = new SessionPassword();
				returnItem.setEncryptPasswordHash(result.getPwdHash());
				returnItem.setPassword(result.getSessionPwd());
				returnItem.setValidate(result.isCorrect());
				returnList.add(returnItem);
			}
		}
		return returnList;
	}
	
	@Override
	public void deleteRecoveryPointsFromDataStore(int rpsNodeId, String dataStoreUUID,
			List<String> nodeUUIDList) throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			service.deleteNodesFromDataStore(dataStoreUUID, nodeUUIDList);
		}
	}
	
	@Override
	public List<ProtectedNodeInDestination> getNodesFromShareFolder(int rpsNodeId,
			ConnectionInfo connectionInfo) throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			Date beginDate;
			try {
				// defect 191232
				if (service.isPathInDataStore(connectionInfo)) {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.RPS_DATASTORE_INVALIDPATH, "The share folder belongs to a data store.");
				}
				beginDate = formatter.parse("1970-01-01");
				Date endDate = formatter.parse("2999-12-31");
				List<RecoveryPointWithNodeInfo> recoveryList = service.getRecoveryPointsWithNodeInfo(connectionInfo, beginDate, endDate);
				Collections.sort(recoveryList, new Comparator<RecoveryPointWithNodeInfo>() {
					@Override
					public int compare(RecoveryPointWithNodeInfo o1, RecoveryPointWithNodeInfo o2) {
						return o1.getNodeName().compareToIgnoreCase(o2.getNodeName());
					}
				});
				List<RecoveryPointWithNodeInfo> groupedRecoveryList = new ArrayList<RecoveryPointWithNodeInfo>();
				for (RecoveryPointWithNodeInfo node : recoveryList) {
					if (node.getRecoveryPoints() != null) {
						String currentHash = null;
						RecoveryPointWithNodeInfo nodeInfo = null;
						for (RecoveryPoint rp : node.getRecoveryPoints()) {						
							if (nodeInfo == null) {
								nodeInfo = new RecoveryPointWithNodeInfo();
								nodeInfo.setNodeName(node.getNodeName());
								nodeInfo.setFullPath(node.getFullPath());
								nodeInfo.setRecoveryPoints(new ArrayList<RecoveryPoint>());
							}
							if (currentHash == null) {
								currentHash = rp.getEncryptPasswordHash();
							}
							if (!currentHash.equals(rp.getEncryptPasswordHash())) {
								nodeInfo = new RecoveryPointWithNodeInfo();
								nodeInfo.setNodeName(node.getNodeName());
								nodeInfo.setFullPath(node.getFullPath());
								nodeInfo.setRecoveryPoints(new ArrayList<RecoveryPoint>());
								currentHash = rp.getEncryptPasswordHash();
							}
							nodeInfo.getRecoveryPoints().add(rp);
							if (!groupedRecoveryList.contains(nodeInfo)) {
								groupedRecoveryList.add(nodeInfo);
							}
						}
					}
				}
				
				List<ProtectedNodeInDestination> resultList = new ArrayList<ProtectedNodeInDestination>();
				for (RecoveryPointWithNodeInfo node : groupedRecoveryList) {
					ProtectedNodeInDestination nodeInDataStore = new ProtectedNodeInDestination();
					nodeInDataStore.setNodeName(node.getNodeName());
					nodeInDataStore.setSfFullPath(node.getFullPath());
					nodeInDataStore.setRecoveryCount(node.getRecoveryPoints() != null ? node.getRecoveryPoints().size() : 0);
					Date firstDate = null;
					Date mostRecentDate = null;
					if (node.getRecoveryPoints() != null) {
						for (RecoveryPoint rp : node.getRecoveryPoints()) {
							if (firstDate == null) {
								firstDate = rp.getTime();
							} else if (rp.getTime().before(firstDate)){
								firstDate = rp.getTime();
							}
							if (mostRecentDate == null) {
								mostRecentDate = rp.getTime();
							} else if (rp.getTime().after(mostRecentDate)) {
								mostRecentDate = rp.getTime();
							}
							if (rp.getEncryptPasswordHash() != null && !"".equals(rp.getEncryptPasswordHash())) {
								nodeInDataStore.setEncryptPasswordHash(rp.getEncryptPasswordHash());
							} else {
								nodeInDataStore.setEncryptPasswordHash("");
								nodeInDataStore.setValidated(true);
							}
							if (nodeInDataStore.getSessionGuidList() == null) {
								nodeInDataStore.setSessionGuidList(new ArrayList<String>());
							}
							nodeInDataStore.getSessionGuidList().add(rp.getSessionGuid());
						}
					}
					nodeInDataStore.setFirstRecoveryPoint(firstDate);
					nodeInDataStore.setMostRecentRecoveryPoint(mostRecentDate);
					resultList.add(nodeInDataStore);
				}
				return resultList;
			} catch (ParseException e) {
				return new ArrayList<ProtectedNodeInDestination>();
			}
		}
	}
	
	public void forceRefreshDataStoreStatus(int rpsNodeId) throws EdgeServiceFault{
		DataStoreManager.getDataStoreManager().forceRefreshDataStoreStatus(rpsNodeId);
	}

	//This will observe RPS node remote deployment status
	//If finished then invoke create datastore
	@Override
	public void update(Observable o, Object arg) {
		DeployTargetDetail targetDetail = (DeployTargetDetail) arg;
		if (TaskStatus.Error.getValue() == targetDetail.getTaskstatus()) {
			log.error("[RPSDataStoreServiceImpl] RPS deployment failed, so can't create dataStore.");
			// Rps product install failed
			try { 			
				List<DataStoreSettingInfo> settingInfos = getDataStoreListByNode(targetDetail.getNodeID());
				for (DataStoreSettingInfo settingInfo : settingInfos) {
					if (settingInfo.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE){
						try {
							String msg = EdgeCMWebServiceMessages.getMessage("deployNewFailed", EdgeCMWebServiceMessages.getResource("productShortNameRPS"));
							RpsDataStoreUtil.updateDataStoreMessageToDB(settingInfo,msg);
						} catch (EdgeServiceFault e1) {
							log.error("[RPSDataStoreServiceImpl]Rps deployment finished, create data store faild, save errormessage to DB faild.",e1);
						}
					}
				}
			} catch (EdgeServiceFault e) {
				log.error("[RPSDataStoreServiceImpl] RPS deployment failed, getDataStoreListByNode failed.", e);
			}
			
		} else {
			try {
				List<DataStoreSettingInfo> settingInfos = getDataStoreListByNode(targetDetail.getNodeID());
				for (DataStoreSettingInfo settingInfo : settingInfos) {
					if (settingInfo.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE) {
						try {
							DataStoreManager.getDataStoreManager().save(settingInfo);
						} catch (Exception e) {
							log.error("[RPSDataStoreServiceImpl]Rps deployment finished, create data store faild.",	e);
						}
					}
				}
			} catch (EdgeServiceFault e2) {
				log.error("[RPSDataStoreServiceImpl]Rps deployment finished, getDataStoreListByNode faild.", e2);
			}
		}
	}
	
	public boolean haveDataStoreSettingsToCreate(){
		try{
			List<RpsNode> rpsNodeList = rpsService.getRpsNodesByGroup(0, -1);	
			for(RpsNode node : rpsNodeList){
				List<DataStoreSettingInfo> settingInfos = getDataStoreListByNode(node.getNode_id());
				for(DataStoreSettingInfo settingInfo : settingInfos){
					if(settingInfo.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE){
						return true;
					}
				}
			}
		}catch(EdgeServiceFault e) {
			log.error("[RPSDataStoreServiceImpl] haveDataStoreSettingsToCreate failed.", e );
			return false;
		}
		return false;
	}

	@Override
	public void startReplicationNow(int rpsNodeId,
			List<ManualReplicationItem> replicationitems)
			throws EdgeServiceFault {		
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			service.startReplicationNow(replicationitems);
		}catch(javax.xml.ws.soap.SOAPFaultException e) {
			String errorMessage = e.getFault().getFaultString();			
			if (errorMessage != null && 
					errorMessage.contains("Cannot find dispatch method")) {					
				log.info("[RPSDataStoreServiceImpl] startReplicationNow catch ERROR(Cannot find dispatch method) to notify user upgrade");	
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.METHODNOTSUPPORT_RPS,
						new Object[] { rpsNodeId }, "METHODNOTSUPPORT_RPS" );
			}else{
				log.error("[RPSDataStoreServiceImpl] startReplicationNow failed.", e );
				throw e;
			}
		}	
	}

}
