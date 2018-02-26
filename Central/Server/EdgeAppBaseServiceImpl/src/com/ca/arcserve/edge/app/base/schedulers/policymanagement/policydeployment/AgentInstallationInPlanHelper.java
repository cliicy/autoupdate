package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.LogUtility;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IEdgeConfigurationService;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.DeployCommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DeployD2DSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.PlanStatusUpdater;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductDeployServiceImpl;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class AgentInstallationInPlanHelper{
	private static Logger logger = Logger.getLogger( PolicyDeploymentTaskAssigner.class );
	private IEdgeConfigurationService configurationService;
	private NodeServiceImpl nodeService;
	protected LogUtility logUtility;
	private IEdgeConnectInfoDao connectInfoDao;
	IEdgeVSBDao vsbDao;
	IEdgeHostMgrDao edgeHostMgrDao;
	protected PolicyManagementServiceImpl policyManagementServiceImpl;
	
	public AgentInstallationInPlanHelper(){
		this.configurationService = new ConfigurationServiceImpl();
		this.nodeService = new NodeServiceImpl();
		this.connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		this.vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		this.edgeHostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		this.policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
	}
	
	public boolean needInstallAgentForMonitor(PolicyDeploymentTask task) {
		int hostId = task.getHostId();
		List<HostConnectInfo> monitorList = new ArrayList<HostConnectInfo>();
		vsbDao.as_edge_vsb_monitor_getByHostId(hostId, monitorList);
		if(monitorList.size() < 1)
			return false;
		List<PolicyDeploymentTask> tasks = new ArrayList<PolicyDeploymentTask>();
		tasks.add(task);
		String monitorUUID = monitorList.get(0).getUuid();
		int[] monitorHostId = new int[1];
		edgeHostMgrDao.as_edge_host_getHostIdByUuid(monitorUUID, ProtectionType.WIN_D2D.getValue(),
				monitorHostId);
		return needInstallAgent(monitorHostId[0],task.getPolicyId() , tasks);
	}
	
	public boolean needInstallAgentForProxy(int policyId , List<PolicyDeploymentTask> taskList) {
		List<EdgeConnectInfo> lstProxy = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_proxy_by_policyid(policyId, lstProxy);
		if (lstProxy.size() < 1 || taskList.size()<1) {				
			return false;
		}
		return needInstallAgent(lstProxy.get(0).getHostid(),policyId , taskList);
	}
	
	public boolean needInstallAgentForSource(PolicyDeploymentTask task) {
		List<PolicyDeploymentTask> tasks = new ArrayList<PolicyDeploymentTask>();
		tasks.add(task);
		return needInstallAgent(task.getHostId(),task.getPolicyId(),tasks);
	}
	
	public boolean needInstallAgent(Integer nodeId ,int policyId , List<PolicyDeploymentTask> taskList) {
		boolean result = false;
		String version = nodeService.getRemoteDeployParam(1);
		EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(nodeId);
		if(hostInfo == null || StringUtil.isEmptyOrNull(hostInfo.getRhostname())){
			return false;
		}
		if(DeployCommonUtil.isFreshOrOldVersionD2D(hostInfo.getD2DMajorversion(), hostInfo.getD2dMinorversion(),hostInfo.getD2dUpdateversionnumber(), hostInfo.getD2dBuildnumber(), version)){//if node have not d2d install or it's version is old , then deploy newest d2d
			result = true;
			InstallAgent(hostInfo , policyId , taskList);
		}	
		return result;
	}
	
	public void InstallAgent(EdgeHost hostInfo,int policyId,List<PolicyDeploymentTask> taskList){
		logger.info("Start to install agent for host: "+hostInfo.getRhostname());
		UnifiedPolicy plan;
		try {
			plan = policyManagementServiceImpl.loadUnifiedPolicyById(policyId);
		} catch (EdgeServiceFault e) {
			logger.error("Install agent failed, cannot load plan information by id.", e);
			return;
		}
		DeployD2DSettings deployD2DSettings;
		try
		{
			deployD2DSettings = this.configurationService.getDeployD2DSettings();
		}
		catch (Exception e)
		{
			deployD2DSettings = DeployD2DSettings.getDefaultSettings();
		}
		if(deployD2DSettings == null){
			deployD2DSettings = DeployD2DSettings.getDefaultSettings();
		}
		//use default setting	
		List<DeployTargetDetail> remoteDeployTargets = new LinkedList<DeployTargetDetail>();
		DeployTargetDetail deployTarget = plan.getDeployD2Dsetting();
		if(deployTarget == null){
			deployTarget = new DeployTargetDetail();
		}
		deployTarget.setNodeID(hostInfo.getRhostid());
		deployTarget.setServerName( hostInfo.getRhostname() );
		deployTarget.setUsername( hostInfo.getUsername() );
		deployTarget.setPassword( hostInfo.getPassword() );
		deployTarget.setAutoStartRRService( true ); // manual remote deploy UI set this to true in AddServerWindow.		
		String d2dInstallPath = DeployD2DSettings.getInstallPathWithProductName(
				deployD2DSettings.getInstallPath(),ProductType.D2D);
		deployTarget.setInstallDirectory(d2dInstallPath);
		deployTarget.setPort( deployD2DSettings.getPort() );
		deployTarget.setProtocol(deployD2DSettings.getProtocol());
		deployTarget.setProductType(Integer.parseInt(ProductType.ProductD2D));
		deployTarget.setUuid(hostInfo.getD2DUUID());
		if(StringUtil.isEmptyOrNull(deployTarget.getUsername())||StringUtil.isEmptyOrNull(deployTarget.getPassword())){
			deployTarget.setUsername(deployD2DSettings.getDeployUserName()); //If node have no username or have no password , use the common username and password
			deployTarget.setPassword(deployD2DSettings.getDeployPassword());
		}
		deployTarget.setPlanIds(String.valueOf(policyId));
		EdgeAccount edgeAccount = new EdgeAccount();
		WSJNI.getEdgeAccount( edgeAccount );
		DeployTargetDetail.localAdmin = edgeAccount.getUserName();
		DeployTargetDetail.localAdminPassword = edgeAccount.getPassword();
		DeployTargetDetail.localDomain = edgeAccount.getDomain();
	
		remoteDeployTargets.add(deployTarget);
		
		try{
			ProductDeployServiceImpl.getInstance().submitRemoteDeploy(remoteDeployTargets);
			logger.info("Schedule an deploy agent job for host: "+hostInfo.getRhostname() +"from plan.");
		}catch(Exception e){
			deployTarget.setProgressMessage(EdgeCMWebServiceMessages.getMessage( "policyDeployment_FailedToDeployD2D", e.getLocalizedMessage()));
			PlanStatusUpdater.updatePlanDeployD2DFailed(deployTarget);
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e, "process(): Failed.");
		}
	}
	
	public static boolean isNeedInstallAgent(int nodeId , UnifiedPolicy policy){
		if(policy.getVSphereBackupConfiguration()==null && policy.getBackupConfiguration()== null)
			return false;
		boolean needInstallAgent = false;
		NodeServiceImpl nodeServiceImpl = new NodeServiceImpl();
		String version = nodeServiceImpl.getRemoteDeployParam(1);
		int nodeIdToBeInstalled = nodeId;
		if(policy.getVSphereBackupConfiguration()!=null){//HBBU BACKUP
			List<EdgeConnectInfo> lstProxy = new ArrayList<EdgeConnectInfo>();
			IEdgeConnectInfoDao conInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class); 
			conInfoDao.as_edge_proxy_by_policyid(policy.getId(), lstProxy);
			if (lstProxy.size() < 1) {				
				return false;
			}
			nodeIdToBeInstalled = lstProxy.get(0).getHostid();
		}
		EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(nodeIdToBeInstalled);
		if(hostInfo == null || StringUtil.isEmptyOrNull(hostInfo.getRhostname()))
			needInstallAgent = false;
		if(DeployCommonUtil.isFreshOrOldVersionD2D(hostInfo.getD2DMajorversion(), hostInfo.getD2dMinorversion(),hostInfo.getD2dUpdateversionnumber(), hostInfo.getD2dBuildnumber(), version)){//if node have not d2d install or it's version is old , then deploy newest d2d
			needInstallAgent = true;
		}	
		return needInstallAgent;
	}
	
	public static String mergedPlanIdsForTwoDeployTarget(DeployTargetDetail originalTarget ,DeployTargetDetail target){
		if(originalTarget.getPlanIds()==null && target.getPlanIds() ==null)
			return null;
		if(originalTarget.getPlanIds()==null && target.getPlanIds()!=null)
			return target.getPlanIds();
		if(originalTarget.getPlanIds()!=null && target.getPlanIds()==null)
			return originalTarget.getPlanIds();
		if(originalTarget.getPlanIds().equals(target.getPlanIds()))
			return target.getPlanIds();
		
		StringBuilder planIdsBuilder = new StringBuilder();
		planIdsBuilder.append(target.getPlanIds());
		List<String> oIds = Arrays.asList(originalTarget.getPlanIds().split(","));
		List<String> nIds = Arrays.asList(target.getPlanIds().split(","));
		for(String planId : oIds){
			if(!nIds.contains(planId)){
				planIdsBuilder.append(",");
				planIdsBuilder.append(planId);
			}
		}

		return planIdsBuilder.toString();
	}
	
	public static boolean needMergedPlanIdsForTwoDeployTarget(DeployTargetDetail originalTarget ,DeployTargetDetail newtarget){
		if(newtarget.getPlanIds()==null && originalTarget.getPlanIds()==null)
			return false;
		if(newtarget.getPlanIds()!=null && originalTarget.getPlanIds()!=null && originalTarget.getPlanIds().equals(newtarget.getPlanIds()))
			return false;
		return true;
	}
}
