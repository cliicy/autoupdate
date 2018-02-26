package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.rps.webservice.data.DisabledNodes;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.NatReplicationSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeSettingsWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.FileCopySettingWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.policy.PolicyManagement;

public class RpsSettingTaskDeployment {
	
	private static Logger logger = Logger.getLogger(RpsSettingTaskDeployment.class);

	private IActivityLogService activityLogService = new ActivityLogServiceImpl();

	public void createRpsPolicySettings(UnifiedPolicy policy) throws EdgeServiceFault {
		int rpsPolicyCount = policy.getRpsPolices().size();
		if (rpsPolicyCount == 0) {
			return;
		}
		//Get plan enable status
		List<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
		IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
		edgePolicyDao.as_edge_plan_getPlanList(policyList);
		DisabledNodes dn = new DisabledNodes();
		dn.setDisablePlan(!policy.isEnable());
		//List<Integer> lstNodeId = policy.getNodes();
		List<Integer> lstNodeId = getNodeIdsByIdentifiers(policy.getProtectedResources());
		if (lstNodeId != null && !lstNodeId.isEmpty()) {
			NodeServiceImpl nodeServiceImpl = new NodeServiceImpl();
			ArrayList<String> lstNodeIdString = new ArrayList<String>();
			
			if (policy.getVSphereBackupConfiguration() != null) {
				for (Integer id : lstNodeId) {
					String instanceUUID = nodeServiceImpl.getVMInstanceUUIDById(id);
					if (instanceUUID != null && !instanceUUID.isEmpty()) {
						lstNodeIdString.add(instanceUUID);
					}
				}
			} else {
				for (Integer id : lstNodeId) {
					EdgeConnectInfo info = nodeServiceImpl.getEdgeConnectInfoById(id);
					if (info.getUuid() != null && !"".equals(info.getUuid())) {
						lstNodeIdString.add(info.getUuid());
					}
				}
			}
			
			dn.setDisabledNodes(lstNodeIdString);
		}
		
		Map<RpsHost, String> alreadyDeployed=new HashMap<RpsHost, String>();
		int serverId = 0;
		try{
			// create replication RPS policy in reversed order from n-1 to 1
			boolean isAlreadyBind = false;
			boolean isFileArchiveAlreadyBind = false;
			Map<String, ArchiveConfig> archiveRpsMap = new HashMap<String, ArchiveConfig>();
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper: policy.getArchiveToTapeSettingsWrapperList()){
				if(archiveToTapeWrapper.getArchiveToTapeSettings() != null){
					logger.debug("Bind RPS Information: archive task id is " + archiveToTapeWrapper.getTaskId() + ", source mapping task id" + 
							archiveToTapeWrapper.getArchiveToTapeSettings().getSource().getTaskId());
					String sourceTaskId = archiveToTapeWrapper.getArchiveToTapeSettings().getSource().getTaskId();
					archiveRpsMap.put(sourceTaskId, archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveConfig());
				}
			}
			Map<String, ArchiveConfiguration> fileCopyRpsMap = new HashMap<String, ArchiveConfiguration>();
			for(FileCopySettingWrapper fileCopySettingWrapper : policy.getFileCopySettingsWrapper()){
				if(fileCopySettingWrapper.getArchiveConfiguration() != null){
					fileCopyRpsMap.put(fileCopySettingWrapper.getArchiveConfiguration().getSelectedSourceId(), fileCopySettingWrapper.getArchiveConfiguration());
				}
					
			}
			// add ReplicateTaskNatSetting to source RPS's RPSPolicy 
			setReplicateNatSetting(policy);
			for (int i = rpsPolicyCount - 1; i > 0; --i) {
				RPSPolicyWrapper previousRpsPolicyWrapper = policy.getRpsPolices().get(i - 1);
				RPSPolicy previous = previousRpsPolicyWrapper.getRpsPolicy();
				RPSPolicyWrapper currentRpsPolicyWrapper = policy.getRpsPolices().get(i);
				RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
				
				logger.debug("Bind RPS Information: RPSPolicy[" +i+ "] task id is " + currentRpsPolicyWrapper.getTaskId());
				if(!archiveRpsMap.isEmpty() && StringUtil.isNotEmpty(currentRpsPolicyWrapper.getTaskId())){
					if(archiveRpsMap.containsKey(currentRpsPolicyWrapper.getTaskId())){
						logger.debug("Bind RPS Information: match task id successfull, rps policy id is "+currentRpsPolicy.getId());
						currentRpsPolicy.setArchiveTotape(archiveRpsMap.get(currentRpsPolicyWrapper.getTaskId()));
						archiveRpsMap.remove(currentRpsPolicyWrapper.getTaskId());//after matching rps, remove archiveRpsMap
					}
				}
				
				if(!fileCopyRpsMap.isEmpty() && StringUtil.isNotEmpty(currentRpsPolicyWrapper.getTaskId())){
					if(fileCopyRpsMap.containsKey(currentRpsPolicyWrapper.getTaskId())){
						logger.debug("Bind RPS Information: match task id successful, rps policy id is "+currentRpsPolicy.getId());
						currentRpsPolicy.setFileCopyConfiguration(fileCopyRpsMap.remove(currentRpsPolicyWrapper.getTaskId()));
					}
				}
				
				if(policy.getFileArchiveConfiguration() != null && StringUtil.isNotEmpty(currentRpsPolicyWrapper.getTaskId())){
					if(policy.getFileArchiveConfiguration().getSelectedSourceId().equals(currentRpsPolicyWrapper.getTaskId())){
						isFileArchiveAlreadyBind = true;
						currentRpsPolicy.setFileArchiveConfiguration(policy.getFileArchiveConfiguration());
					}
				}
				// add debug log
				if(policy.getRpsPolices().get(i).getRpsPolicy()!=null
						&&policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings()!=null
						&&policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings().getRpsReplicationSettings()!=null){
						if(policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getNatReplicationSettings()!=null){
							NatReplicationSettings settings = policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getNatReplicationSettings();
							logger.info("deployRpsPolicy to ServerName="+previous.getRpsSettings().getRpsReplicationSettings().getHostName()
									+" NatSetting["+settings.isEnableNAT()+","+settings.getHostName()+","+settings.getPort()+"]");
						}
						if(policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().isEnableProxy()){
							RPSReplicationSettings proxy = policy.getRpsPolices().get(i).getRpsPolicy().getRpsSettings().getRpsReplicationSettings();
							logger.info("deployRpsPolicy to serverName="+previous.getRpsSettings().getRpsReplicationSettings().getHostName()
									+" EnableProxy["+proxy.getProxyHostname()+":"+proxy.getProxyPort()+","+proxy.isProxyRequireAuthentication()
									+","+proxy.getProxyUsername()+","+proxy.getProxyPassword()+"]");
						}else{
							logger.info("deployRpsPolicy to serverName="+previous.getRpsSettings().getRpsReplicationSettings().getHostName()
									+" DisableProxy");
						}						
				} else{
					logger.info("deployRpsPolicy to ServerName="+previous.getRpsSettings().getRpsReplicationSettings().getHostName()+" no use NatSetting");
				}
				// end debug log
				deployRpsPolicy(previous.getRpsSettings().getRpsReplicationSettings(), policy.getRpsPolices().get(i).getRpsPolicy(), dn);
				serverId = previous.getRpsSettings().getRpsReplicationSettings().getHostId();
				alreadyDeployed.put(getRpsHost(previous.getRpsSettings().getRpsReplicationSettings()), policy.getRpsPolices().get(i).getRpsPolicy().getId());
			}
			if(logger.isDebugEnabled()){
				if(isAlreadyBind){
					logger.debug("Bind RPS Information: user select replication as archive to tape source");
				}
			}
			if(!archiveRpsMap.isEmpty() && archiveRpsMap.containsKey(policy.getRpsPolices().get(0).getTaskId())){//archive task source is backup
				logger.debug("Bind RPS Information: user select backup task as archive to tape source, RPSPolicy[0]'s id is " + policy.getRpsPolices().get(0).getRpsPolicy().getId());
				policy.getRpsPolices().get(0).getRpsPolicy().setArchiveTotape(archiveRpsMap.get(policy.getRpsPolices().get(0).getTaskId()));
			}
			
			if(!fileCopyRpsMap.isEmpty() && fileCopyRpsMap.containsKey(policy.getRpsPolices().get(0).getTaskId())){
				logger.debug("Bind RPS Information: user select backup task as file copy source, RPSPolicy[0]'s id is " + policy.getRpsPolices().get(0).getRpsPolicy().getId());
				policy.getRpsPolices().get(0).getRpsPolicy().setFileCopyConfiguration(fileCopyRpsMap.get(policy.getRpsPolices().get(0).getTaskId()));
			}
			
			if(policy.getFileArchiveConfiguration() != null && !isFileArchiveAlreadyBind){
				policy.getRpsPolices().get(0).getRpsPolicy().setFileArchiveConfiguration(policy.getFileArchiveConfiguration());
			}	
			String servername = null;
			if (isVMBackupToRpsServer(policy)) {
				serverId = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				servername = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				deployRpsPolicy(policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), policy.getRpsPolices().get(0).getRpsPolicy(), dn);				
			} else if (isD2DBackupToRpsServer(policy)) {
				serverId = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				servername = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				deployRpsPolicy(policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), policy.getRpsPolices().get(0).getRpsPolicy(), dn);
			} else if (policy.getMspServerReplicationSettings() != null) {
				deployRpsPolicy(policy.getMspServerReplicationSettings(), policy.getRpsPolices().get(0).getRpsPolicy(), dn);
				serverId = policy.getMspServerReplicationSettings().getHostId();
				servername = policy.getMspServerReplicationSettings().getHostName();
			}
			// add debug log
			if(policy.getRpsPolices().get(0).getRpsPolicy()!=null
					&&policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings()!=null
					&&policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings()!=null){
					if(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getNatReplicationSettings()!=null){
						NatReplicationSettings settings = policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getNatReplicationSettings();
						logger.info("deployRpsPolicy to ServerId="+serverId +" serverName="+servername
								+" NatSetting["+settings.isEnableNAT()+","+settings.getHostName()+","+settings.getPort()+"]");						
					}
					if(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings().isEnableProxy()){
						RPSReplicationSettings proxy = policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsReplicationSettings();
						logger.info("deployRpsPolicy to ServerId="+serverId +" serverName="+servername
								+" EnableProxy["+proxy.getProxyHostname()+":"+proxy.getProxyPort()+","+proxy.isProxyRequireAuthentication()
								+","+proxy.getProxyUsername()+","+proxy.getProxyPassword()+"]");
					}else{
						logger.info("deployRpsPolicy to ServerId="+serverId +" serverName="+servername
								+" DisableProxy");
					}					
			} else{
				logger.info("deployRpsPolicy to ServerId="+serverId +" serverName="+servername+" no use NatSetting");
			}
			// end debug log
			createRPSActivityLog(serverId, EdgeCMWebServiceMessages.getMessage( "rpsPolicyCreateSuccLog" ) ,Severity.Information);	
		}catch(EdgeServiceFault | RuntimeException e){
			for(RpsHost h: alreadyDeployed.keySet()){
				try{
				PolicyManagement.getInstance().deletePolicy(h.getRhostId(), alreadyDeployed.get(h));
				}catch(Exception ex){
					logger.error(ex);
				}
			}
			String message =  e.getMessage();
			if(e instanceof EdgeServiceFault){
				EdgeServiceFault fault = (EdgeServiceFault)e;
				message = WebServiceFaultMessageRetriever.
						getErrorMessage( DataFormatUtil.getServerLocale(),(fault.getFaultInfo()));
			}
			createRPSActivityLog(serverId, EdgeCMWebServiceMessages.getMessage( "rpsPolicyCreateFailLogPrefix" ) + " " + message, Severity.Error);	
			throw e;
		}
	}
	
	private void setReplicateNatSetting(UnifiedPolicy policy){
		
		int rpsPolicyCount = policy.getRpsPolices().size();
		for (int i = rpsPolicyCount - 1; i >= 0; --i) {		
			RPSPolicy currentRpsPolicy = policy.getRpsPolices().get(i).getRpsPolicy();
			if(i+1 < rpsPolicyCount){
				RPSPolicyWrapper nextRpsWrapper = policy.getRpsPolices().get(i+1);
				if(nextRpsWrapper!=null 
						&& nextRpsWrapper.getNatReplicationSettings()!=null 
						&& StringUtil.isNotEmpty(nextRpsWrapper.getNatReplicationSettings().getHostName())){				
					currentRpsPolicy.getRpsSettings().getRpsReplicationSettings().setNatReplicationSettings(nextRpsWrapper.getNatReplicationSettings());
				} 
			}			
		}
	}
	
	private void createRPSActivityLog(int hostId, String message, Severity severity) throws EdgeServiceFault{
		try
		{
			ActivityLog log = new ActivityLog();
			log.setModule(Module.RpsPolicyManagement);
			log.setHostId(hostId);
			log.setMessage(message);
			log.setSeverity(severity);
			log.setTime(new Date());
			activityLogService.addLog( log );
		}
		catch (Exception e)
		{
			logger.error( "writeActivityLog(): Error writting activity log. (Message: '" + message + "')", e );
		}
	}
	
	public List<Integer> getNodeIdsByIdentifiers(List<ProtectedResourceIdentifier> identifierList) {
		List<Integer> lstNodeId = new ArrayList<Integer>();
		if(identifierList == null || identifierList.isEmpty())
			return lstNodeId;
		for(ProtectedResourceIdentifier identifier : identifierList){
			if(identifier.getType() == ProtectedResourceType.node){
				lstNodeId.add(identifier.getId());
			}else {//just only handled the esx group type
				int groupId = identifier.getId();
				IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
				List<EdgeHost> hostList = new ArrayList<EdgeHost>();
				esxDao.as_edge_vsphere_entity_map_getVappHostList_By_EsxGroup(groupId, hostList);
				List<Integer> vAppHostList = new ArrayList<Integer>();
				for (EdgeHost host : hostList) {
					vAppHostList.add(host.getRhostid());
				}
				lstNodeId.addAll(vAppHostList);
			}
		}
		return lstNodeId;
	}
	
	private RpsHost getRpsHost(RPSReplicationSettings setting) {
		RpsHost host=new RpsHost();
		host.setRhostId(setting.getHostId());
		host.setRhostname(setting.getHostName());
		host.setPort(setting.getPort());
		host.setHttpProtocol(setting.getProtocol() == 0 );
		host.setUsername(setting.getUserName());
		host.setPassword(setting.getPassword());
		host.setUuid(setting.getUuid());
		return host;
	}

	private static boolean isVMBackupToRpsServer(UnifiedPolicy policy) {
		return policy.getVSphereBackupConfiguration() != null && !policy.getVSphereBackupConfiguration().isD2dOrRPSDestType();
	}
	
	private static boolean isD2DBackupToRpsServer(UnifiedPolicy policy) {
		return policy.getBackupConfiguration() != null && !policy.getBackupConfiguration().isD2dOrRPSDestType();
	}
	
	public static void setPlanUuid(UnifiedPolicy policy){
		for(RPSPolicyWrapper p:policy.getRpsPolices()){
			p.getRpsPolicy().setPlanUUID(policy.getUuid());
			
			RPSReplicationSettings replication = p.getRpsPolicy().getRpsSettings().getRpsReplicationSettings();
			if (replication.isEnableReplication() && replication.getMspReplicationSettings() == null) {
				replication.setTargetPlanUuid(policy.getUuid());
			}
		}
	}
	
	public static void setRpsPolicyUuid(UnifiedPolicy policy, boolean forceNewUuid) {	
		if (policy.getRpsPolices().isEmpty()) {
			return;
		}
		
		List<RPSPolicyWrapper> pl = policy.getRpsPolices();
		for(int i=pl.size()-1;i>=0;i--){
			RPSPolicy target = pl.get(i).getRpsPolicy();
			target.setName(policy.getName()); // defect 69190
			if(target.getId()==null || target.getId().isEmpty() || forceNewUuid){
				target.setId(UUID.randomUUID().toString());
				if(i>0){
					RPSPolicy source = pl.get(i-1).getRpsPolicy();
					source.getRpsSettings().getRpsReplicationSettings().getReplicationPolicySettings().setUuid(target.getId());
					source.getRpsSettings().getRpsReplicationSettings().getReplicationPolicySettings().setName(target.getName());
				}
			}
		}
		
		RPSPolicy target = pl.get(0).getRpsPolicy();
		if (isVMBackupToRpsServer(policy)) {
			policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().setRPSPolicyUUID(target.getId());
			policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().setRPSPolicy(target.getName());
		} else if (isD2DBackupToRpsServer(policy)) {
			policy.getBackupConfiguration().getBackupRpsDestSetting().setRPSPolicyUUID(target.getId());
			policy.getBackupConfiguration().getBackupRpsDestSetting().setRPSPolicy(target.getName());
		} else if (policy.getMspServerReplicationSettings() != null) {
			policy.getMspServerReplicationSettings().getReplicationPolicySettings().setUuid(target.getId());
			policy.getMspServerReplicationSettings().getReplicationPolicySettings().setName(target.getName());
		}
	}
	
	public static void deployRpsPolicy(RpsHost rpsHost, RPSPolicy rpsPolicy, DisabledNodes dn) throws EdgeServiceFault {
		PolicyManagement.getInstance().deployPolicy(rpsHost.getRhostId(), rpsPolicy, dn);
	}

	public static void deployRpsPolicy(RPSReplicationSettings replication, RPSPolicy rpsPolicy, DisabledNodes dn) throws EdgeServiceFault {
		PolicyManagement.getInstance().deployPolicy(replication.getHostId(), rpsPolicy, dn);
	}
	
	private Map<String, ValuePair<RpsHost, RPSPolicy>> extractRpspolicies(UnifiedPolicy policy){
		Map<String, ValuePair<RpsHost, RPSPolicy>> rpspolicies=new HashMap<String, ValuePair<RpsHost, RPSPolicy>>();
		if (policy.getRpsPolices().isEmpty()) {
			return rpspolicies;
		}
		
		RpsHost host = null;
		
		if (isD2DBackupToRpsServer(policy)) {
			host = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
		} else if (isVMBackupToRpsServer(policy)) {
			host = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
		} else if (policy.getMspServerReplicationSettings() != null) {
			host = tranRpsReplicationSettings2RpsHost(policy.getMspServerReplicationSettings());
		}
		
		if (host != null) {
			rpspolicies.put(host.getRhostname(), new ValuePair<RpsHost, RPSPolicy>(host, policy.getRpsPolices().get(0).getRpsPolicy()));
		}
		
		for(int i=1;i<policy.getRpsPolices().size();i++){
			host=tranRpsReplicationSettings2RpsHost(policy.getRpsPolices().get(i-1).getRpsPolicy().getRpsSettings().getRpsReplicationSettings());
			rpspolicies.put(host.getRhostname(), new ValuePair<RpsHost, RPSPolicy>(host, policy.getRpsPolices().get(i).getRpsPolicy()));
		}
		
		return rpspolicies;
	}
	
	private RpsHost tranRpsReplicationSettings2RpsHost(RPSReplicationSettings s){
		RpsHost h=new RpsHost();
		h.setRhostId(s.getHostId());
		h.setRhostname(s.getHostName());
		h.setUsername(s.getUserName());
		h.setPassword(s.getPassword());
		h.setHttpProtocol(s.getProtocol()==0);
		h.setPort(s.getPort());
		return h;
	}
	
	public void deleteRpsPolicySettings(UnifiedPolicy policy) throws EdgeServiceFault {
		int rpsPolicyCount = policy.getRpsPolices().size();
		if (rpsPolicyCount == 0) {
			return;
		}

		// delete RPS policy for destination
		if (isVMBackupToRpsServer(policy)) {
			deleteRpsPolicy(policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), policy.getRpsPolices().get(0).getRpsPolicy());
		} else if (isD2DBackupToRpsServer(policy)) {
			deleteRpsPolicy(policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost(), policy.getRpsPolices().get(0).getRpsPolicy());
		} else if (policy.getMspServerReplicationSettings() != null) {
			deleteRpsPolicy(policy.getMspServerReplicationSettings(), policy.getRpsPolices().get(0).getRpsPolicy());
		}

		// delete replication RPS policy from 1 to n-1
		for (int i = 1; i < rpsPolicyCount; ++i) {
			RPSPolicy previous = policy.getRpsPolices().get(i - 1).getRpsPolicy();
			deleteRpsPolicy(previous.getRpsSettings().getRpsReplicationSettings(), policy.getRpsPolices().get(i).getRpsPolicy());
		}
	}

	private void deleteRpsPolicy(RpsHost rpsHost, RPSPolicy rpsPolicy) throws EdgeServiceFault {
		try {
			PolicyManagement.getInstance().deletePolicy(rpsHost.getRhostId(), rpsPolicy.getId());
			createRPSActivityLog(rpsHost.getRhostId(), EdgeCMWebServiceMessages.getMessage( "rpsPolicyDeleteSuccLog" ), Severity.Information);	
		} catch (Exception e) {
			logger.warn("RpsSettingTaskDeployment.deleteRpsPolicy - ignore deleting RPS policy failure, error message = " + e.getMessage());
			createRPSActivityLog(rpsHost.getRhostId(), EdgeCMWebServiceMessages.getMessage( "rpsPolicyDeleteFailLogPrefix" ) + " " + e.getMessage(), Severity.Error);	
		}
	}

	private void deleteRpsPolicy(RPSReplicationSettings replication, RPSPolicy rpsPolicy) throws EdgeServiceFault {
		try {
			PolicyManagement.getInstance().deletePolicy(replication.getHostId(), rpsPolicy.getId());
			createRPSActivityLog(replication.getHostId(), EdgeCMWebServiceMessages.getMessage( "rpsPolicyDeleteSuccLog" ), Severity.Information);	
		} catch (Exception e) {
			logger.warn("RpsSettingTaskDeployment.deleteRpsPolicy - ignore deleting RPS policy failure, error message = " + e.getMessage());
			createRPSActivityLog(replication.getHostId(), EdgeCMWebServiceMessages.getMessage( "rpsPolicyDeleteFailLogPrefix" ) + " " + e.getMessage(), Severity.Error);	
		}
	}

	public void deleteOldRpsPolicySettings(UnifiedPolicy oldPolicy, UnifiedPolicy newPolicy) throws EdgeServiceFault {
		Map<String, ValuePair<RpsHost, RPSPolicy>> oldRpspolicies=extractRpspolicies(oldPolicy);
		Map<String, ValuePair<RpsHost, RPSPolicy>> newRpspolicies=extractRpspolicies(newPolicy);
		List<ValuePair<RpsHost, RPSPolicy>> toDels=new ArrayList<ValuePair<RpsHost, RPSPolicy>>();
		for(String key:oldRpspolicies.keySet()){
			if(newRpspolicies.containsKey(key)){
				if(oldRpspolicies.get(key).getValue().getId().equals(newRpspolicies.get(key).getValue().getId())){
					continue;
				}
			}
			toDels.add(oldRpspolicies.get(key));
		}
		
		for(ValuePair<RpsHost, RPSPolicy> v:toDels){
			deleteRpsPolicy(v.getKey(), v.getValue());
		}
	}
	
	/*public void updateRpsPolicySettings(UnifiedPolicy oldPolicy, UnifiedPolicy newPolicy) throws EdgeServiceFault {
		createRpsPolicySettings(newPolicy);
		
		try {
			deleteOldRpsPolicySettings(oldPolicy, newPolicy);
		} catch (Exception e) {
			logger.debug("update rps policy settings incompleted, cannot delete the old rps policy settings. error message = " + e.getMessage());
		}
	}*/

}
