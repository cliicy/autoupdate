/**
 * 
 */
package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.VCMD2DBackupInfo;
import com.ca.arcflash.jobscript.failover.DNSUpdaterParameters;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.data.policy.VCMPolicyDeployParameters;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.common.connection.ConverterConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.LogUtility;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentExceptionErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMConverterType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ConversionTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.JobScriptCombo4Wan;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.ProxyConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetD2DConnectInfoException;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyUtil;
import com.ca.arcserve.edge.app.base.webservice.vcm.VCMServiceImpl;

/**
 * @author lijwe02
 * 
 */
public class VSBTaskDeployment extends TaskDeploymentExceptionHandler implements ITaskDeployment {
	private static final Logger logger = Logger.getLogger(VSBTaskDeployment.class);
	private static final Lock locker = new ReentrantLock();
	private NodeServiceImpl nodeService = null;
	private HostInfoCache hostInfoCache;
	private IEdgeConnectInfoDao connectInfoDao;
	private LogUtility logUtility;
	private IEdgeHostMgrDao hostDao = null;
	private static IEdgeVSBDao vsbDao = null;
	//private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	//private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgePolicyDao policyDao = null;
	private long edgeTaskId = PolicyManagementServiceImpl.getTaskId();
	private PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
	protected IEdgeExternalLinks edgeExternalLinks;
	private VCMServiceImpl vcmService = null;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);

	public VSBTaskDeployment() {
		this.hostInfoCache = HostInfoCache.getInstance();
		this.connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
		logUtility = new LogUtility(logger);
		this.edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.ITaskDeployment#deployTask(com
	 * .ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask,
	 * com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean deployTask(PolicyDeploymentTask policyDeploymentTask, UnifiedPolicy plan,
			boolean updateDeployStatusOnSuccess) {
		logger.info("Start deploy VSB Task.");
		if (Utils.hasHbbuBackupTask(policyDeploymentTask.getContentFlag())) {
			boolean result = true;
			Object taskParameters = policyDeploymentTask.getTaskParameters();
			if (taskParameters != null && taskParameters instanceof List) {
				List<Integer> vmList = (List<Integer>) taskParameters;
				for (Integer vmId : vmList) {
					//check vm deployment status, if it have failed when deploy hbbu ask,
					//then no need to do vsb task deployment
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
					policyDao.getHostPolicyMap(vmId, policyDeploymentTask.getPolicyType(), mapList);
					if(!mapList.isEmpty()
							&& mapList.get(0).getDeployStatus() == PolicyDeployStatus.DeploymentFailed){
						logger.info("[VSBTaskDeployment] deployTask: the vm "+vmId + "deploy hbbu task failed. not to do VSB task deployment");
						continue;
					}
					//start to deploy vsb task
					int deploystatus = 0;
					if(!mapList.isEmpty())
						deploystatus = mapList.get(0).getDeployStatus();
					logger.info("[VSBTaskDeployment] deployTask: the vm "+vmId + " deploy status is "+deploystatus);
					policyDeploymentTask.setHostId(vmId);
					setVcmBackupInfo(vmId,true,plan);
					boolean deployResult = deployOneTask(policyDeploymentTask, plan, updateDeployStatusOnSuccess);
					logger.info("Deploy vsb task for vm:" + vmId + " result is:" + deployResult);
					result = result && deployResult;
				}
				return result;
			}
		}
		setVcmBackupInfo(policyDeploymentTask.getHostId(),false,plan);
		return deployOneTask(policyDeploymentTask, plan, updateDeployStatusOnSuccess);
	}

	// console has no store Node/vmNode's HostName(not IP) and osVersion, so Cannot SetVCMD2DBackupInfo only via consoleDB
	// must connect to Agent to get Node's VCMD2DBackupInfo then transfer this to Convert
	private void setVcmBackupInfo(int hostId, boolean isVmBackupTask, UnifiedPolicy plan){
		ConversionTask vsbTask = plan.getConversionConfiguration();
		if (!vsbTask.isSourceTaskRemoteReplicate()) {
			logger.info("setVcmBackupInfo no need to setVCMBackupInfo hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;
		}
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		getEdgeHostDao().as_edge_host_list(hostId,1,hostList);
		String afGuid = null;	
		if(hostList==null||hostList.isEmpty()){			
			logger.error("setVcmBackupInfo hostList.size=0 hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;
		}
		for (EdgeHost host:hostList) {
			if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isNotEmpty(host.getVmInstanceUuid())){
				afGuid = host.getVmInstanceUuid();
			}else if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isNotEmpty(host.getD2DUUID())){
				afGuid = host.getD2DUUID();
			}
			break;
		}
		if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isEmptyOrNull(afGuid)){					
			logger.error("setVcmBackupInfo afGuid=null hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;			
		}
		if(isVmBackupTask){
			int proxyHostId = plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID();
			logger.debug("setVcmBackupInfo isVmBackupTask proxyHostId="+proxyHostId);
			try (D2DConnection connection = connectionFactory.createD2DConnection(proxyHostId)) {
				connection.connect();
				VCMD2DBackupInfo info = connection.getService().getVCMD2DBackupInfo(afGuid);
				vsbTask.getConversionJobScript().setVcmBackupInfo(info);
				logger.info("setVcmBackupInfo isVmBackupTask proxyHostId="+proxyHostId+" afGuid="+afGuid+" VCMD2DBackupInfo:AFGuid="
						+info.getAFGuid()+" BackupDestName="+info.getBackupDestName());
			} catch (Exception e) {
				logger.error("setVcmBackupInfo isVmBackupTask proxyHostId="+proxyHostId+" afGuid="+afGuid+" "+e.getMessage());
			}
		}else{
			logger.debug("setVcmBackupInfo hostId="+hostId);
			try (D2DConnection connection = connectionFactory.createD2DConnection(hostId)) {
				connection.connect();
				VCMD2DBackupInfo info = connection.getService().getVCMD2DBackupInfo(afGuid);
				logger.debug("setVcmBackupInfo info="+info.getAFGuid()+"");
				vsbTask.getConversionJobScript().setVcmBackupInfo(info);
				logger.info("setVcmBackupInfo hostId="+hostId+" afGuid="+afGuid+" VCMD2DBackupInfo:AFGuid="
						+info.getAFGuid()+" BackupDestName="+info.getBackupDestName());
			} catch (Exception e) {
				logger.error("setVcmBackupInfo hostId="+hostId+" afGuid="+afGuid+" "+e.getMessage());
			}
		}
	}
	
	/*
	// console has no store Node/vmNode's HostName(not IP) and osVersion, so Cannot SetVCMD2DBackupInfo only via consoleDB
	// must connect to Agent to get Node's VCMD2DBackupInfo then transfer this to Convert
	private void setVcmBackupInfo(int hostId, boolean isVmBackupTask, UnifiedPolicy plan){
		ConversionTask vsbTask = plan.getConversionConfiguration();
		if (!vsbTask.isSourceTaskRemoteReplicate()) {
			logger.info("setVcmBackupInfo no need to setVCMBackupInfo hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;
		}
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		getEdgeHostDao().as_edge_host_list(hostId,1,hostList);
		String afGuid = null;	
		StringBuilder backupDestName = new StringBuilder();	// D2D:hostname[uid]  VM: vmname@hypervisor[uid] 	
		boolean isX86 = false;
		if(hostList==null||hostList.isEmpty()){
			logger.error("setVcmBackupInfo hostList.size=0 hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;
		}
		
		EdgeHost host = hostList.get(0);
		isX86 = !com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isEqual(host.getOstype(), "64-bit");
		if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isNotEmpty(host.getVmInstanceUuid())){
			afGuid = host.getVmInstanceUuid();
			backupDestName.append(host.getVmname()).append("@");
			
			List<EdgeHost> hypervisorHosts = new ArrayList<EdgeHost>();
			esxDao.as_edge_esx_vm_list_by_ids(host.getRhostid()+" ",hypervisorHosts);
			if(hypervisorHosts.isEmpty()){
				hyperVDao.as_edge_hyperv_vm_list_by_ids(host.getRhostid()+" ",hypervisorHosts);
			}
			for(EdgeHost hypervisorHost:hypervisorHosts){
				backupDestName.append(hypervisorHost.getHypervisorHostName());
				break;
			}
			backupDestName.append("[");
			backupDestName.append(host.getVmInstanceUuid());
			backupDestName.append("]");
		}else if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isNotEmpty(host.getD2DUUID())){
			afGuid = host.getD2DUUID();
			backupDestName.append(host.getRhostname());
			backupDestName.append("[");
			backupDestName.append(host.getD2DUUID());
			backupDestName.append("]");	
		}
		logger.debug("setVcmBackupInfo afGuid="+afGuid);
		if(com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil.isEmptyOrNull(afGuid)){					
			logger.error("setVcmBackupInfo afGuid=null hostId="+hostId+" isVmBackupTask="+isVmBackupTask);
			return;			
		}	
		String rpsPolicyUUID = populateRpsPolicyUuid(plan);
		if(rpsPolicyUUID==null){
			logger.error("setVcmBackupInfo populateRpsPolicyUuid rpsPolicyUUID=null");
			return;
		}
		VCMD2DBackupInfo vcmBackupInfo = new VCMD2DBackupInfo();
		vcmBackupInfo.setAFGuid(afGuid);
		vcmBackupInfo.setBackupDestName(backupDestName.toString());
		vcmBackupInfo.setOsVersion(host.getOsdesc());
		vcmBackupInfo.setRpsPolicyUUID(rpsPolicyUUID);
		vcmBackupInfo.setX86(isX86);
		vsbTask.getConversionJobScript().setVcmBackupInfo(vcmBackupInfo);
		logger.info("setVcmBackupInfo hostId="+hostId+" isVmBackupTask="+isVmBackupTask 
				+" vcmBackupInfo[ afGuid:"+afGuid+" BackupDestName:"+backupDestName.toString()
				+" OsVersion:"+host.getOsdesc()+" rpsPolicyUUID:"+rpsPolicyUUID+" isX86:"+isX86+"]");
	}
	
	private String populateRpsPolicyUuid(UnifiedPolicy plan) {
		
		ConversionTask conversionTask = plan.getConversionConfiguration();
		int sourceTaskId = conversionTask.getSourceTaskId();
		List<Integer> orderList = plan.getOrderList();
		if (sourceTaskId >= orderList.size()) {
			logger.error("The source task id is not correct, it's large than task count.");
			return null;
		}
		int sourceIndex = 0;
		for (int i = 0; i <= sourceTaskId; i++) {
			int itemId = orderList.get(i);
			if (itemId == 11 || itemId == 12) {
				sourceIndex++;
			}
		}
		if (sourceIndex > 0) {
			List<RPSPolicyWrapper> rpsPolicies = plan.getRpsPolices();
			if (rpsPolicies != null && rpsPolicies.size() >= sourceIndex) {
				RPSPolicy sourceRpsPolicy = rpsPolicies.get(sourceIndex - 1).getRpsPolicy();
				//rpsInfo.setRpsPolicy(sourceRpsPolicy.getName());
				//rpsInfo.setRpsPolicyUUID(sourceRpsPolicy.getId());
				logger.info("populateRpsPolicyUuid sourceRpsPolicy.getId()="+sourceRpsPolicy.getId());
				return sourceRpsPolicy.getId();
			}
		}
		logger.error("The source task id maybe 0 sourceIndex="+sourceIndex);
		return null;
	}
	*/
	
	/**
	 * The original deploy logic, for hbbu will deploy batch nodes at one time, and it will pass succeed nodes in task
	 * parameter, so we separate the vm ids and call one by one
	 * 
	 * @param policyDeploymentTask
	 *            The deploy parameter
	 * @param plan
	 *            The plan to deploy
	 * @param updateDeployStatusOnSuccess
	 *            Whether update the status on database or not
	 * @return Deploy result
	 */
	public boolean deployOneTask(PolicyDeploymentTask policyDeploymentTask, UnifiedPolicy plan,
			boolean updateDeployStatusOnSuccess) {
		int converterHostId = 0;
		String converterHostName = "";
		int monitorHostId = 0;
		String monitorHostName = "";
		try {
			int hostId = policyDeploymentTask.getHostId();
			int hostType = getHostType(hostId);
			logger.info("Start deploy vsb task:" + policyDeploymentTask.getPolicyId() + " for node:" + hostId);
			// Check whether the node has vsb task deployed or not
			EdgeVCMConnectInfo oldConverter = getOldConverter(hostId, hostType);
			if (HostTypeUtil.isVMNonWindowsOS(hostType)) {
				if (oldConverter != null) {
					logger.info("The node has Non windows os, we will remove the vsb task from the old converter:"
							+ oldConverter.getHostName());
					removeOneTask(oldConverter, policyDeploymentTask, false, true);
				}
				logUtility.writeLog(LogUtility.LogTypes.Warning, "%s: The node is not windows node for plan: %d",
						"deployVSBTask", policyDeploymentTask.getPolicyId());
				handleNonWindowsOSNode(policyDeploymentTask, updateDeployStatusOnSuccess);
				return true;
			}

			// Check VSB Task
			ConversionTask vsbTask = plan.getConversionConfiguration();
			if (vsbTask == null) {
				logger.error("The VSB Task is null for deploy.");
				return false;
			}

			boolean isNodeImportedFromRHA = HostTypeUtil.isNodeImportFromRHA(hostType);
			// Check whether the remote node has session password or not
			HostConnectInfo vcmConverter = vsbTask.getConverter();
			if (isNodeImportedFromRHA) {
				vcmConverter = getConverter(hostId).toHostConnectInfo();
				vcmConverter.setConverterType(VCMConverterType.RHASessionDestination);
			}
			// Ignore session password check when deploy vsb task for MSP nodes
			// if (vcmConverter != null && VCMConverterType.RPSServer2MSPRPSServer == vcmConverter.getConverterType()) {
			// List<String> sessionPasswordList = getSessionPasswords(hostId);
			// if (sessionPasswordList.size() == 0) {
			// logger.error("The session password is not set for node:" + hostId);
			// List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
			// PolicyDeploymentError error = new PolicyDeploymentError();
			// error.setErrorCode(FlashServiceErrorCode.VCM_MSP_NODE_NO_SESSION_PASSWORD);
			// error.setErrorType(PolicyDeploymentError.ErrorTypes.Error);
			// error.setSettingsType(SettingsTypes.VCMSettings);
			// errorList.add(error);
			// DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, false);
			// if (updateDeployStatusOnSuccess) {
			// DeployUtil.setDeployStatus(policyDeploymentTask, false);
			// }
			// return false;
			// }
			// }

			// Get new converter for VSB Task
			D2DConnectInfo converterConnectInfo = isNodeImportedFromRHA ? VSBTaskUtils.getD2DConnectInfo(vcmConverter)
					: getConverterConnectInfo(policyDeploymentTask, plan);
			if (converterConnectInfo == null) {
				logger.error("Failed to get converter connect info.");
				return false;
			}
			
			if (!converterConnectInfo.getGatewayId().isValid()) {
				if (converterConnectInfo.getHostId() > 0) {
					GatewayEntity gateway = gatewayService.getGatewayByHostId(converterConnectInfo.getHostId());
					converterConnectInfo.setGatewayId(gateway.getId());
				} else {
					logger.error("Failed to get the gateway information of converter.");
				}
			}

			if (oldConverter != null) {
				if (oldConverter.getHostName().equalsIgnoreCase(converterConnectInfo.getHostName())) {
					logger.info("The converter is not changed.");
				} else {
					logger.info("The converter is changed from:" + oldConverter.getHostName() + " to:"
							+ converterConnectInfo.getHostName()
							+ ", we need to undeploy policy from old converter and deploy to new converter.");
					removeOneTask(oldConverter, policyDeploymentTask, false, true);
				}
			}

			converterHostId = converterConnectInfo.getHostId();
			converterHostName = converterConnectInfo.getHostName();

			if ((converterConnectInfo.getUsername() == null)
					|| (EdgeCommonUtil.getUserName(converterConnectInfo.getUsername()).trim().length() == 0)) {
				logger.error("The user name for the converter:" + converterConnectInfo.getHostName() + " is empty.");
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential, "Invalid user credentials for converter.");
			}

			ProxyConnectInfo proxyConnectInfo = new ProxyConnectInfo(converterConnectInfo.getGatewayId(), converterConnectInfo.getHostName(),
					converterConnectInfo.getPort(), converterConnectInfo.getProtocol(),
					EdgeCommonUtil.getUserName(converterConnectInfo.getUsername()), converterConnectInfo.getPassword(),
					EdgeCommonUtil.getDomainName(converterConnectInfo.getUsername()), converterConnectInfo.getUuid());
			IEdgeD2DRegService regService = new EdgeD2DRegServiceImpl();
			regService.UpdateRegInfoToProxy(proxyConnectInfo, false);

			VCMPolicyDeployParameters parametersObj = getDeployParamaters(policyDeploymentTask, vsbTask.getTaskType(),
					converterConnectInfo, false);
			parametersObj.setEnabled(plan.isEnable());
			parametersObj.setReuseVM(false);// this parameter is no use in background , remove it in future
			populateRpsPolicyUuid(parametersObj, plan);
			String deployParameters = com.ca.arcflash.common.CommonUtil.marshal(parametersObj);

			try {
					StringBuilder sBuilder = new StringBuilder();
					sBuilder.append("VSBTaskDeployment deployOneTask ConversionTask{");
					sBuilder.append("TaskType="+vsbTask.getTaskType());
					sBuilder.append(",SourceTaskRemoteReplicate="+vsbTask.isSourceTaskRemoteReplicate());
					sBuilder.append(",SourceTaskId="+vsbTask.getSourceTaskId());
					sBuilder.append(",ConversionJobScript{\n AlertJobScript jobtype="+vsbTask.getConversionJobScript().getAlertJobScript().getJobType());
					sBuilder.append(",GenerateType="+vsbTask.getConversionJobScript().getAlertJobScript().getGenerateType());
					sBuilder.append("\n FailoverJobScript jobtype="+vsbTask.getConversionJobScript().getFailoverJobScript().getJobType());
					sBuilder.append(",GenerateType="+vsbTask.getConversionJobScript().getFailoverJobScript().getGenerateType());
					sBuilder.append("\n HbJobScript jobtype="+vsbTask.getConversionJobScript().getHbJobScript().getJobType());					
					sBuilder.append(",GenerateType="+vsbTask.getConversionJobScript().getHbJobScript().getGenerateType());
					sBuilder.append(",HeartBeatMonitorHostName="+vsbTask.getConversionJobScript().getHbJobScript().getHeartBeatMonitorHostName());					
					sBuilder.append(",VirtualType()="+vsbTask.getConversionJobScript().getHbJobScript().getVirtualType());					
					sBuilder.append("\n RepJobScript jobtype="+vsbTask.getConversionJobScript().getRepJobScript().getJobType());
					sBuilder.append(",GenerateType="+vsbTask.getConversionJobScript().getRepJobScript().getGenerateType());					
					sBuilder.append("} }");
					logger.info(sBuilder.toString());
				} catch (Exception e) {
					logger.error("VSBTaskDeployment deployOneTask ConversionTask printout encount an error ",e);
			}
		
			
			logger.info("Deploy vsb task to converter:" + converterHostName);
			List<PolicyDeploymentError> errorList = PolicyManagementServiceImpl.getInstance().deployVSBTask(
					policyDeploymentTask, converterConnectInfo, vsbTask, deployParameters);

			if (errorList != null) {
				for (PolicyDeploymentError error : errorList) {
					logger.error("Error code:" + error.getErrorCode());
				}
			}

			if (!DeployUtil.hasError(errorList)) {
				if (updateDeployStatusOnSuccess) {
					DeployUtil.setDeployStatus(policyDeploymentTask, true);
					DeployUtil.addDeploymentSucceedLog(edgeTaskId, policyDeploymentTask, null);
				}
				DeployUtil.addDeploymentLogs(edgeTaskId, policyDeploymentTask, errorList);
				DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, false);

				// Save converter information to database
				updateConverterForNode(hostId, converterConnectInfo, vsbTask.getTaskType(), true, hostType);
				HostConnectInfo converter = getHostConnectInfo(converterConnectInfo);
				addConverter(hostId, converter);

				HostConnectInfo monitorInfo = getMonitor(vsbTask, converter);
				if (monitorInfo != null) {
					updateUuid(monitorInfo);
					// monitorInfo.setUuid(getD2DHostUuid(monitorInfo));
					updateMonitorForNode(hostId, monitorInfo, true);
					addMonitor(hostId, monitorInfo);
					monitorHostId = monitorInfo.getHostId();
					monitorHostName = monitorInfo.getHostName();
					updateRegInfoToD2D(monitorInfo);
				} else {
					logUtility.writeLog(LogUtility.LogTypes.Error, "%s: Error getting monitor info from policy %d",
							"deployTask", policyDeploymentTask.getPolicyId());

					EdgeHost edgeHost = this.hostInfoCache.getHostInfo(hostId);
					String hostName = edgeHost.getRhostname();
					hostName = (hostName == null) ? "" : hostName;

					String message = EdgeCMWebServiceMessages.getResource("policyDeployment_FailedGetMonitor");
					DeployUtil.writeActivityLog(Severity.Error, hostId, hostName, message);
				}

				// Mark node as monitee when deploy vcm policy, we need to change it when move to plan and task
				markNodeAsMonitee(hostId, true);

				logger.info("Deploy VSB task succeeded.");
				return true;
			} else {
				// has errors
				logger.error("Failed to deploy vsb task");
				DeployUtil.setDeployStatus(policyDeploymentTask, false);
				DeployUtil.addDeploymentLogs(edgeTaskId, policyDeploymentTask, errorList);
				DeployUtil.addDeploymentFailedLog(edgeTaskId, policyDeploymentTask, null);
				DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, false);
			}
		} catch (EdgeServiceFault e) {
			if(e.getFaultInfo().getMessageParameters()!= null){
				super.handleException("deployVsbTask",policyDeploymentTask, e, monitorHostId, monitorHostName);
			} else {
				super.handleException("deployVsbTask",policyDeploymentTask, e, converterHostId, converterHostName);
			}
		} catch (Exception e) {
			super.handleException("deployVsbTask",policyDeploymentTask, e, converterHostId, converterHostName);
		}
		logger.info("Deploy VSB Task failed.");
		return false;
	}

	private void handleNonWindowsOSNode(PolicyDeploymentTask policyDeploymentTask, boolean updateDeployStatusOnSuccess)
			throws DeploymentException {
		int hostId = policyDeploymentTask.getHostId();
		EdgeHost edgeHost = this.hostInfoCache.getHostInfo(hostId);

		String nodeName = PolicyUtil.getNodeName(hostId);
		String osDesc = edgeHost.getOsdesc();
		String errorCode = FlashServiceErrorCode.VCM_NON_WINDOWS_OS_NODE;
		String deployErrorCode = errorCode;
		if (HostTypeUtil.isHyperVVirtualMachine(edgeHost.getRhostType()) && StringUtil.isEmptyOrNull(osDesc)) {
			errorCode = FlashServiceErrorCode.VCM_NON_WINDOWS_OS_HYPERV_NODE;
			deployErrorCode = FlashServiceErrorCode.VCM_NON_WINDOWS_OS_HYPERV_NODE_WITH_HELP;
		}
		osDesc = PolicyUtil.getFixedOsDescription(osDesc);
		List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
		PolicyDeploymentError error = new PolicyDeploymentError();
		error.setNodeName(nodeName);
		error.setErrorParameters(new Object[] { nodeName, osDesc });
		error.setErrorType(PolicyDeploymentError.ErrorTypes.Warning);
		error.setSettingsType(SettingsTypes.VCMSettings);
		errorList.add(error);
		// In Activity log, we will show help information for hyper-v vm
		error.setErrorCode(deployErrorCode);
		DeployUtil.addDeploymentLogs(edgeTaskId, policyDeploymentTask, errorList);
		// Change another error code, so in UI, it won't show help information
		error.setErrorCode(errorCode);
		DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, false);
		if (updateDeployStatusOnSuccess) {
			DeployUtil.setDeployStatus(policyDeploymentTask, true);
		}
	}
	
	private void populateRpsPolicyUuid(VCMPolicyDeployParameters param, UnifiedPolicy plan) {
		if (param == null) {
			return;
		}
		RPSInfo rpsInfo = param.getRPSInfo();
		if (rpsInfo != null) {
			ConversionTask conversionTask = plan.getConversionConfiguration();
			int sourceTaskId = conversionTask.getSourceTaskId();
			List<Integer> orderList = plan.getOrderList();
			if (sourceTaskId >= orderList.size()) {
				logger.error("The source task id is not correct, it's large than task count.");
				return;
			}
			int sourceIndex = 0;
			for (int i = 0; i <= sourceTaskId; i++) {
				int itemId = orderList.get(i);
				if (itemId == 11 || itemId == 12) {
					sourceIndex++;
				}
			}
			if (sourceIndex > 0) {
				List<RPSPolicyWrapper> rpsPolicies = plan.getRpsPolices();
				if (rpsPolicies != null && rpsPolicies.size() >= sourceIndex) {
					RPSPolicy sourceRpsPolicy = rpsPolicies.get(sourceIndex - 1).getRpsPolicy();
					rpsInfo.setRpsPolicy(sourceRpsPolicy.getName());
					rpsInfo.setRpsPolicyUUID(sourceRpsPolicy.getId());
				}
			}
		}
	}
	
	private void updateRegInfoToD2D(HostConnectInfo hostConnectInfo) throws EdgeServiceFault {
		// Update reg info to monitor
		try {
			ProxyConnectInfo proxyConnectInfo = new ProxyConnectInfo(hostConnectInfo.getGatewayId(), hostConnectInfo.getHostName(),
					hostConnectInfo.getPort(), hostConnectInfo.getProtocol() == Protocol.Https ? "https" : "http",
					EdgeCommonUtil.getUserName(hostConnectInfo.getUserName()), hostConnectInfo.getPassword(),
					EdgeCommonUtil.getDomainName(hostConnectInfo.getUserName()), null);
			IEdgeD2DRegService regService = new EdgeD2DRegServiceImpl();
			regService.UpdateRegInfoToProxy(proxyConnectInfo, false);
		} catch (EdgeServiceFault e){
			e.getFaultInfo().setMessageParameters(new String[]{"monitor"});
			throw e;
		} catch (Exception e1) {
			logger.error("Failed to update registry information on monitor.", e1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.ITaskDeployment#removeTask(com
	 * .ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask,
	 * com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeTask(PolicyDeploymentTask policyDeploymentTask, boolean updateDeployStatusOnSuccess) {
		logger.info("Start remove VSB task.");
		if (Utils.hasHbbuBackupTask(policyDeploymentTask.getContentFlag())) {
			boolean result = true;
			Object taskParameters = policyDeploymentTask.getTaskParameters();
			if (taskParameters != null && taskParameters instanceof List) {
				List<Integer> vmList = (List<Integer>) taskParameters;
				for (Integer vmId : vmList) {
					policyDeploymentTask.setHostId(vmId);
					boolean removeResult = removeOneTask(null, policyDeploymentTask, updateDeployStatusOnSuccess, false);
					logger.info("Remove vsb task for vm:" + vmId + " result is:" + removeResult);
					result = result && removeResult;
				}
				return result;
			}
		}
		return removeOneTask(null, policyDeploymentTask, updateDeployStatusOnSuccess, false);
	}

	public boolean removeOneTask(EdgeVCMConnectInfo converter, PolicyDeploymentTask policyDeploymentTask,
			boolean updateDeployStatusOnSuccess, boolean forDeploy) {
		int converterHostId = 0;
		String converterHostName = "";
		int hostId = 0;
		int hostType = 0;
		try {
			boolean nodeHasNonWindowsOS = false;
			hostId = policyDeploymentTask.getHostId();
			logger.info("Remove vsb task:" + policyDeploymentTask.getPolicyId() + " for node:" + hostId);
			hostType = getHostType(hostId);
			if (HostTypeUtil.isVMNonWindowsOS(hostType)) {
				logger.info("The node has non windows os, we will try to check whether the node has vsb task or not.");
				nodeHasNonWindowsOS = true;
			}
			EdgeVCMConnectInfo oldConverter = ((converter == null) ? getConverter(hostId) : converter);
			D2DConnectInfo converterConnectInfo = VSBTaskUtils.getD2DConnectInfo(oldConverter);
			if (converterConnectInfo == null) {
				if (!forDeploy && updateDeployStatusOnSuccess) {
					DeployUtil.setDeployStatus(policyDeploymentTask, true);
				}
				if (nodeHasNonWindowsOS) {
					if (!forDeploy && updateDeployStatusOnSuccess) {
						DeployUtil.addDeploymentSucceedLog(edgeTaskId, policyDeploymentTask, null);
					}
				} else {
					logger.info("The node has no converter connect info.");
				}
				return true;
			}
			converterHostId = converterConnectInfo.getHostId();
			converterHostName = converterConnectInfo.getHostName();
			if ((converterConnectInfo.getUsername() == null)
					|| (EdgeCommonUtil.getUserName(converterConnectInfo.getUsername()).trim().length() == 0))
				throw new DeploymentException(DeploymentExceptionErrorCode.InvalidD2DConnectInfoException);

			VCMPolicyDeployParameters parametersObj = getDeployParamaters(policyDeploymentTask,
					oldConverter.getTaskType(), converterConnectInfo, true);
			String deployParameters = com.ca.arcflash.common.CommonUtil.marshal(parametersObj);

			logger.info("Remove vsb task from converter:" + converterHostName);
			List<PolicyDeploymentError> errorList = PolicyManagementServiceImpl.getInstance().removeVSBTask(
					policyDeploymentTask, converterConnectInfo, oldConverter.getTaskType(), deployParameters);

			if (!DeployUtil.hasError(errorList)) {
				if (updateDeployStatusOnSuccess) {
					DeployUtil.setDeployStatus(policyDeploymentTask, true);
					DeployUtil.addDeploymentSucceedLog(edgeTaskId, policyDeploymentTask, null);
				}
				if (!forDeploy) {
					DeployUtil.addDeploymentLogs(edgeTaskId, policyDeploymentTask, errorList);
					DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, true);
				}
				logger.info("Remove VSB task succeeded.");
				return true;
			} else {// has errors
				logger.error("Failed to remove vsb task");
				for (PolicyDeploymentError error : errorList) {
					logger.error("Error code:" + error.getErrorCode());
				}
				if (!forDeploy) {
					DeployUtil.setDeployStatus(policyDeploymentTask, false);
					DeployUtil.addDeploymentLogs(edgeTaskId, policyDeploymentTask, errorList);
					DeployUtil.addDeploymentFailedLog(edgeTaskId, policyDeploymentTask, null);
					DeployUtil.addWarningErrorMessageFromD2D(policyDeploymentTask, errorList, true);
					// this.clearD2DManagedStatus(task, errorList);
				}
			}
		} catch (Exception e) {
			if (forDeploy) {
				logger.error("Failed when remove old vsb task when deploy new vsb task.", e);
			} else {
				super.handleException("removeVsbTask",policyDeploymentTask, e, converterHostId, converterHostName);
			}
		} finally {
			if (hostId > 0) {
				clearVSBInfoWhenRemoveTask(hostId, hostType);
			}
		}
		logger.info("Remove VSB task with error.");
		return false;
	}

	private void clearVSBInfoWhenRemoveTask(int hostId, int hostType) {
		logger.info("Clear VSB information when remove task.");
		// remove map for node and converter
		updateConverterForNode(hostId, null, -1, false, hostType);
		// Remove map for node and monitor
		updateMonitorForNode(hostId, null, false);
		// When remove VCM policy, then remove monitee flag
		markNodeAsMonitee(hostId, false);
	}

	public NodeServiceImpl getNodeService() {
		if (nodeService == null) {
			nodeService = new NodeServiceImpl();
		}
		return nodeService;
	}

	protected D2DConnectInfo getConverterConnectInfo(PolicyDeploymentTask policyDeploymentTask, UnifiedPolicy plan) {
		try {
			ConversionTask vsbTask = plan.getConversionConfiguration();
			HostConnectInfo converter = vsbTask.getConverter();
			D2DConnectInfo converterConnectInfo = VSBTaskUtils.getD2DConnectInfo(converter);
			if (converterConnectInfo == null) {
				int hostId = policyDeploymentTask.getHostId();
				converterConnectInfo = PolicyManagementServiceImpl.getInstance().getD2DConnectInfo(hostId);
				if (converterConnectInfo != null) {
					converterConnectInfo.setVcmConverterType(VCMConverterType.Agent);
				}
			}
			return converterConnectInfo;
		} catch (GetD2DConnectInfoException e) {
			logger.error("Failed to fetch converter connection information.", e);
		}
		return null;
	}

	private VCMPolicyDeployParameters getDeployParamaters(PolicyDeploymentTask policyDeploymentTask, int taskType,
			D2DConnectInfo d2dConnectInfo, boolean forRemove) throws Exception {
		int hostId = policyDeploymentTask.getHostId();
		VCMConverterType converterType = d2dConnectInfo == null ? null : d2dConnectInfo.getVcmConverterType();
		VCMPolicyDeployParameters parametersObj = new VCMPolicyDeployParameters();

		EdgeHost hostInfo = this.hostInfoCache.getHostInfo(hostId);
		parametersObj.setHostname(hostInfo.getRhostname());
		if (!StringUtil.isEmptyOrNull(hostInfo.getVmname())) {
			parametersObj.setHostname(hostInfo.getVmname());
		}

		if (isConverterRpsServer(converterType)) {
			populateRPSInfo(d2dConnectInfo, parametersObj);
		}

		populateSourceMachineInfo(hostId, converterType, parametersObj);

		// populate session password and VM network configuration
		Node node = new Node();
		node.setId(hostId);
		node.setPolicyIDForEsx(policyDeploymentTask.getPolicyId());
		if (taskType != PolicyTypes.RemoteVCMForRHA) {
			node.setImportedFromRHA(false);
		} else {
			node.setImportedFromRHA(true);
			parametersObj.setSessionFolderPath(hostInfo.getRecoveryPointFolder());
		}

		if (!forRemove) {
			if (PolicyTypes.isRemoteVCMPolicy(taskType)) {
				// Set session password
				populateSesionPassword(hostId, parametersObj);
			}
			populateIPSetting(node, parametersObj);
		}

		return parametersObj;
	}

	private void populateRPSInfo(D2DConnectInfo d2dConnectInfo, VCMPolicyDeployParameters parametersObj) {
		if (parametersObj == null) {
			return;
		}
		parametersObj.setRPSNode(true);
		parametersObj.setRPSInfo(getRPSInfo(d2dConnectInfo));
	}

	private void populateSourceMachineInfo(int hostId, VCMConverterType converterType,
			VCMPolicyDeployParameters parametersObj) throws GetD2DConnectInfoException, EdgeServiceFault {
		if (parametersObj == null) {
			return;
		}
		if (isConverterRpsServer(converterType) || isConverterHbbuProxy(converterType)) {
			parametersObj.setSourceMachineInfo(getSourceMachineInfo(hostId, converterType));
			parametersObj.setInstanceUuid(parametersObj.getSourceMachineInfo().getInstanceUuid());
		} else {
			// not a VM imported from Edge vSphere
			List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
			this.connectInfoDao.as_edge_connect_info_list(hostId, connectInfoList);
			EdgeConnectInfo connectInfo = connectInfoList.get(0);
			if (connectInfo != null) {
				parametersObj.setInstanceUuid(connectInfo.getUuid());
			}
		}
	}

	private boolean isConverterRpsServer(VCMConverterType converterType) {
		return (converterType != null && (VCMConverterType.Agent2RPSServer == converterType
				|| VCMConverterType.HBBUProxy2RPSServer == converterType
				|| VCMConverterType.RPSServer2RPSServerForAgent == converterType
				|| VCMConverterType.RPSServer2RPSServerForHBBU == converterType || VCMConverterType.RPSServer2MSPRPSServer == converterType));
	}

	private boolean isConverterHbbuProxy(VCMConverterType converterType) {
		return (converterType != null && VCMConverterType.HBBUProxy == converterType);
	}

	private void populateSesionPassword(int hostId, VCMPolicyDeployParameters parametersObj) {
		if (parametersObj == null) {
			logger.error("Failed to populate session passwords, the deploy parameter object is null!");
			return;
		}
		List<String> sessionPasswordList = getSessionPasswords(hostId);
		parametersObj.setSessionPasswordList(sessionPasswordList);
	}

	private List<String> getSessionPasswords(int hostId) {
		List<String> sessionPasswordList = new ArrayList<String>();
		try {
			List<SessionPassword> passwordList = getNodeService().getSessionPasswordForHost(hostId);
			if (passwordList != null && passwordList.size() > 0) {
				for (SessionPassword sessionPassword : passwordList) {
					sessionPasswordList.add(sessionPassword.getPassword());
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error("Failed to get session password for host:" + hostId, e);
		}
		return sessionPasswordList;
	}

	/**
	 * Query ipsettings from database, and set them to parameters object
	 * 
	 * @param node
	 *            The node information to get, it should contain nodeId and policyId
	 * @param parametersObj
	 *            The parameter object container
	 */
	private void populateIPSetting(Node node, VCMPolicyDeployParameters parametersObj) {
		if (parametersObj == null) {
			logger.error("Failed to populate ip settings, the deploy parameter object is null!");
			return;
		}
		try {
			List<DNSUpdaterParameters> dnsParameters = new ArrayList<DNSUpdaterParameters>();
			List<IPSetting> ipSettings = getNodeService().getIPSettingFromVCM(node, dnsParameters);
			parametersObj.setIpSettings(ipSettings);
			parametersObj.setDnsParameters(dnsParameters);
		} catch (EdgeServiceFault e) {
			logger.error("Fail to get vm network configuration when deploy policy", e);
		}
	}

	private RPSInfo getRPSInfo(D2DConnectInfo d2dConnectInfo) {
		VCMConverterType converterType = d2dConnectInfo.getVcmConverterType();
		if (VCMConverterType.HBBUProxy2RPSServer == converterType || VCMConverterType.Agent2RPSServer == converterType
				|| VCMConverterType.RPSServer2RPSServerForAgent == converterType
				|| VCMConverterType.RPSServer2RPSServerForHBBU == converterType
				|| VCMConverterType.RPSServer2MSPRPSServer == converterType) {
			RPSInfo rpsInfo = new RPSInfo();
			rpsInfo.setRpsHostName(d2dConnectInfo.getHostName());
			rpsInfo.setRpsProtocol("https".equalsIgnoreCase(d2dConnectInfo.getProtocol()) ? Protocol.Https.ordinal()
					: Protocol.Http.ordinal());
			rpsInfo.setRpsPort(d2dConnectInfo.getPort());
			rpsInfo.setRpsUserName(d2dConnectInfo.getUsername());
			rpsInfo.setRpsPassword(d2dConnectInfo.getPassword());
			return rpsInfo;
		} else {
			logger.error("The converter is not RPS server.");
		}
		return null;
	}

	/*
	 * Get source machine connection info , which is needed by RPS converter. for D2D node , it return D2D connection
	 * info for HBBU node, it returns proxy connection info.
	 */
	private MachineDetail getSourceMachineInfo(int hostId, VCMConverterType converterType)
			throws GetD2DConnectInfoException, EdgeServiceFault {
		MachineDetail detail = new MachineDetail();
		EdgeHost hostInfo = this.hostInfoCache.getHostInfo(hostId);
		if (VCMConverterType.isHbbuConverter(converterType)) {
			// For HBBU node
			VSphereProxyInfo proxyInfo = getVCMService().getVSphereProxyInfoByHostId(hostId);
			if (proxyInfo != null) {
				detail.setHostName(hostInfo.getRhostname());
				detail.setHypervisorHostName(proxyInfo.getVSphereProxyName());
				detail.setHypervisorPort(proxyInfo.getvSphereProxyPort());
				detail.setHypervisorProtocol(proxyInfo.getVSphereProxyProtocol() == Protocol.Https ? "https" : "http");
				detail.setHypervisorUserName(proxyInfo.getVSphereProxyUsername());
				detail.setHypervisorPassword(proxyInfo.getVSphereProxyPassword());
			} else {
				logger.error("there is no proxy info , hostID : " + hostId);
			}
			MachineType machineType = MachineType.VSPHERE_ESX_VM;
			if (HostTypeUtil.isHyperVVirtualMachine(hostInfo.getRhostType())) {
				machineType = MachineType.HYPERV_VM;
			}
			detail.setMachineType(machineType);
			detail.setInstanceUuid(hostInfo.getVmInstanceUuid());
			detail.setAuthUuid(hostInfo.getVmInstanceUuid());// TODO need to set the proxy's auth uuid here
			detail.setESXHostName(hostInfo.getEsxName());
			detail.setVmName(hostInfo.getVmname());
		} else {
			// For D2D node
			D2DConnectInfo d2dConnectInfo = PolicyManagementServiceImpl.getInstance().getD2DConnectInfo(hostId);
			detail.setHostName(d2dConnectInfo.getHostName());
			detail.setHypervisorHostName(d2dConnectInfo.getHostName());
			detail.setHypervisorPort(d2dConnectInfo.getPort());
			detail.setHypervisorProtocol(d2dConnectInfo.getProtocol());
			detail.setHypervisorUserName(d2dConnectInfo.getUsername());
			detail.setHypervisorPassword(d2dConnectInfo.getPassword());
			detail.setMachineType(MachineType.PHYSICAL);
			detail.setInstanceUuid(d2dConnectInfo.getUuid());
			detail.setAuthUuid(d2dConnectInfo.getAuthUuid());
		}
		return detail;
	}

	private HostConnectInfo getMonitor(ConversionTask vsbTask, HostConnectInfo converter) {
		if (vsbTask == null) {
			logger.error("The VSB task is null.");
			return null;
		}
		JobScriptCombo4Wan jobScriptCombo = vsbTask.getConversionJobScript();
		if (jobScriptCombo == null) {
			logger.error("The JobScriptCombo in VSB task is empty.");
			return null;
		}
		HeartBeatJobScript hbJobScript = jobScriptCombo.getHbJobScript();
		if (hbJobScript == null) {
			logger.error("The heart beat job script in JobScriptCombo is null.");
			return null;
		}
		HostConnectInfo monitorInfo = new HostConnectInfo();
		if (PolicyTypes.isRemoteVCMPolicy(vsbTask.getTaskType())
				// CrossSiteVsb and hypervisor is vmware case need ReplicationDstRPS as Default Monitor
				|| (vsbTask.isSourceTaskRemoteReplicate()
						&& jobScriptCombo.getFailoverJobScript()!=null
						&& jobScriptCombo.getFailoverJobScript().getVirtualType()!=VirtualizationType.HyperV)) {
			FailoverJobScript failoverJobScript = jobScriptCombo.getFailoverJobScript();
			if (failoverJobScript != null) {
				if (VirtualizationType.HyperV != failoverJobScript.getVirtualType()) {
					logger.info("For RemoteVsb/CrossSiteVsb and hypervisor is vmware, the monitor is converter.");
					monitorInfo.setHostName(converter.getHostName());
					monitorInfo.setProtocol(converter.getProtocol());
					monitorInfo.setPort(converter.getPort());
					monitorInfo.setUserName(converter.getUserName());
					monitorInfo.setPassword(converter.getPassword());
					monitorInfo.setGatewayId(converter.getGatewayId());
					return monitorInfo;
				}
			}
		}
		monitorInfo.setHostName(hbJobScript.getHeartBeatMonitorHostName());
		monitorInfo.setProtocol("https".equalsIgnoreCase(hbJobScript.getHeartBeatMonitorProtocol()) ? Protocol.Https
				: Protocol.Http);
		monitorInfo.setPort(hbJobScript.getHeartBeatMonitorPort());
		monitorInfo.setUserName(hbJobScript.getHeartBeatMonitorUserName());
		monitorInfo.setPassword(hbJobScript.getHeartBeatMonitorPassword());
		monitorInfo.setGatewayId(converter.getGatewayId());
		return monitorInfo;
	}

	private NodeRegistrationInfo getNodeRegistrationInfo(HostConnectInfo hostConnectInfo) {
		NodeRegistrationInfo regInfo;
		// get node info
		boolean failedReadRemoteRegistry = false;
		RemoteNodeInfo remoteNodeInfo = null;
		try {
			remoteNodeInfo = getNodeService().queryRemoteNodeInfo(hostConnectInfo.getGatewayId(), hostConnectInfo.getHostId(), hostConnectInfo.getHostName(),
					hostConnectInfo.getUserName(), hostConnectInfo.getPassword(),
					(Protocol.Https==hostConnectInfo.getProtocol())?"https":"http",hostConnectInfo.getPort());
		} catch (EdgeServiceFault ef) {
			logUtility.writeLog(LogUtility.LogTypes.Debug, ef,
					"QeuryRemoteNodeInfo(%s) with user (%s) Failed (%s), we will adopt the input values.",
					hostConnectInfo.getHostName(), hostConnectInfo.getUserName(), ef.getMessage());
		}
		if (remoteNodeInfo == null) {
			failedReadRemoteRegistry = true;

			remoteNodeInfo = new RemoteNodeInfo();
			remoteNodeInfo.setD2DInstalled(true);
			remoteNodeInfo.setD2DPortNumber(hostConnectInfo.getPort());
			remoteNodeInfo.setD2DProtocol(hostConnectInfo.getProtocol());
			remoteNodeInfo.setARCserveBackInstalled(false);
		}

		// register node
		regInfo = new NodeRegistrationInfo();
		regInfo.setFailedReadRemoteRegistry(failedReadRemoteRegistry);
		regInfo.setNodeName(hostConnectInfo.getHostName());
		regInfo.setUsername(hostConnectInfo.getUserName());
		regInfo.setPassword(hostConnectInfo.getPassword());
		regInfo.setNodeInfo(remoteNodeInfo);
		regInfo.setPhysicsMachine(true);
		regInfo.setVCMMonitor(true);
		regInfo.setGatewayId( hostConnectInfo.getGatewayId() );

		regInfo.setRegisterD2D(false);
		regInfo.setD2dPort(remoteNodeInfo.getD2DPortNumber());
		regInfo.setD2dProtocol(remoteNodeInfo.getD2DProtocol());

		if (remoteNodeInfo.isARCserveBackInstalled()) {
			regInfo.setRegisterARCserveBackup(false);
			regInfo.setAbAuthMode(ABFuncAuthMode.WINDOWS);
			regInfo.setCarootUsername(hostConnectInfo.getUserName());
			regInfo.setCarootPassword(hostConnectInfo.getPassword());
			regInfo.setArcservePort(remoteNodeInfo.getARCservePortNumber());
			regInfo.setArcserveProtocol(remoteNodeInfo.getARCserveProtocol());
		}
		return regInfo;
	}

	private void addMonitor(int taskHostId, HostConnectInfo monitorInfo) {
		String functionName = "addMonitor";
		try {
			int hostId = getHostId(monitorInfo);
			if (hostId != 0) {
				logger.info("The node with same uuid as monitor:" + monitorInfo.getHostName() + " already exists.");
				return;
			}
			NodeRegistrationInfo regInfo = getNodeRegistrationInfo(monitorInfo);
			regInfo.setNodeDescription(EdgeCMWebServiceMessages
					.getResource("policyDeployment_NodeDesc_AutoAddedMonitor"));

			try {
				RegistrationNodeResult registrationResult = getNodeService().registerNode(
						regInfo.isFailedReadRemoteRegistry(), regInfo);
				registrationResult.getErrorCodes();

				logUtility.writeLog(LogUtility.LogTypes.Info, "%s: Monitor '%s' was added.", functionName,
						monitorInfo.getHostName());

				String message = String.format(
						EdgeCMWebServiceMessages.getResource("policyDeployment_NewMonitorAdded"),
						monitorInfo.getHostName());
				DeployUtil.writeActivityLog(Severity.Information, taskHostId, monitorInfo.getHostName(), message);
			} catch (Exception e) {
				boolean ignore = false;

				if (e instanceof EdgeServiceFault) {
					EdgeServiceFault serviceFault = (EdgeServiceFault) e;
					if (serviceFault.getFaultInfo().getCode().equals(EdgeServiceErrorCode.Node_AlreadyExist)) {
						ignore = true;

						logUtility.writeLog(LogUtility.LogTypes.Warning, e, "%s: Monitor '%s' exists already.",
								functionName, monitorInfo.getHostName());

						onMonitorNodeAlreadyExist(taskHostId, regInfo);
					}
				}

				if (!ignore) {
					throw e;
				}
			}
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e, "%s: Failed.", functionName);

			String message = String.format(EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToAddMonitor"),
					monitorInfo.getHostName());
			DeployUtil.writeActivityLog(Severity.Error, taskHostId, monitorInfo.getHostName(), message);
		}
	}

	private int getHostId(HostConnectInfo hostConnectInfo) {
		int[] hostId = new int[1];
		getEdgeHostDao().as_edge_host_getHostIdByUuid(hostConnectInfo.getUuid(), ProtectionType.WIN_D2D.getValue(),
				hostId);
		return hostId[0];
	}

	private void addConverter(int taskHostId, HostConnectInfo converterInfo) {
		String functionName = "addConverter";
		try {
			int hostId = getHostId(converterInfo);
			if (hostId != 0) {
				logger.info("The node with same uuid as converter:" + converterInfo.getHostName() + " already exists.");
				return;
			}
			NodeRegistrationInfo regInfo = getNodeRegistrationInfo(converterInfo);
			regInfo.setNodeDescription(EdgeCMWebServiceMessages
					.getResource("policyDeployment_NodeDesc_AutoAddedConverter"));

			try {
				RegistrationNodeResult registrationResult = getNodeService().registerNode(
						regInfo.isFailedReadRemoteRegistry(), regInfo);
				registrationResult.getErrorCodes();

				logUtility.writeLog(LogUtility.LogTypes.Info, "%s: Converter '%s' was added.", functionName,
						converterInfo.getHostName());

				String message = String.format(
						EdgeCMWebServiceMessages.getResource("policyDeployment_NewConverterAdded"),
						converterInfo.getHostName());
				DeployUtil.writeActivityLog(Severity.Information, taskHostId, converterInfo.getHostName(), message);
			} catch (Exception e) {
				boolean ignore = false;

				if (e instanceof EdgeServiceFault) {
					EdgeServiceFault serviceFault = (EdgeServiceFault) e;
					if (serviceFault.getFaultInfo().getCode().equals(EdgeServiceErrorCode.Node_AlreadyExist)) {
						ignore = true;

						logUtility.writeLog(LogUtility.LogTypes.Warning, e, "%s: Converter '%s' exists already.",
								functionName, converterInfo.getHostName());

						// onConverterNodeAlreadyExist(regInfo);
					}
				}

				if (!ignore) {
					throw e;
				}
			}
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e, "%s: Failed.", functionName);

			String message = String.format(
					EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToAddConverter"),
					converterInfo.getHostName());
			DeployUtil.writeActivityLog(Severity.Error, taskHostId, converterInfo.getHostName(), message);
		}
	}

	private void onMonitorNodeAlreadyExist(int taskHostId, NodeRegistrationInfo regInfo) {
		String functionName = "DefaultDeployTaskRunner.onMonitorNodeAlreadyExist()";

		if (regInfo == null)
			return;

		try {
			markNodeAsMonitor(regInfo);
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e, "%s: Failed.", functionName);

			String message = String.format(
					EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToMarkNodeAsVCMMonitor"),
					regInfo.getNodeName());
			DeployUtil.writeActivityLog(Severity.Error, taskHostId, regInfo.getNodeName(), message);
		}
	}

	private void markNodeAsMonitor(NodeRegistrationInfo regInfo) throws DeploymentException {
		try {
			// use populateEdgeHost() to fill IP address
			EdgeHost hostInfo = getNodeService().populateEdgeHost(regInfo);

			int[] hostIds = new int[1];
			getEdgeHostDao().as_edge_host_getIdByHostnameIp(regInfo.getGatewayId().getRecordId(), hostInfo.getRhostname(), hostInfo.getIpaddress(),1, hostIds);

			hostInfo = this.hostInfoCache.getHostInfo(hostIds[0]);
			hostInfo.setRhostType(HostTypeUtil.setVCMMonitor(hostInfo.getRhostType()));

			int[] output = new int[1];
			String hostName = hostInfo.getRhostname();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
			//List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			GatewayId gatewayId = regInfo.getGatewayId();
			if(gatewayId != null && gatewayId.isValid()){
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
				try {
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[VSBTaskDeployment] markNodeAsMonitor() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			getEdgeHostDao().as_edge_host_update(hostIds[0], hostInfo.getLastupdated(), hostName,
					hostInfo.getNodeDescription(), hostInfo.getIpaddress(), hostInfo.getOsdesc(), hostInfo.getOstype(),
					1, hostInfo.getAppStatus(), "", hostInfo.getRhostType(), regInfo.getProtectionType().getValue(),
					fqdnNames, output);
		} catch (Exception e) {
			throw new DeploymentException(DeploymentExceptionErrorCode.MarkNodeAsMonitorException, e);
		}
	}

	private void markNodeAsMonitee(int hostId, boolean isMonitee) {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		getEdgeHostDao().as_edge_host_list(hostId, 1, hostList);
		if (hostList.size() == 1) {
			EdgeHost edgeHost = hostList.get(0);
			int hostType = edgeHost.getRhostType();
			int moniteeFlag = HostType.EDGE_NODE_VCM_MONITEE.getValue();
			hostType = isMonitee ? HostTypeUtil.setVCMMonitee(hostType) : hostType & (~moniteeFlag);
			getEdgeHostDao().as_edge_host_update_rhosttype_by_id(hostId, hostType);
		} else {
			logger.error("Failed to get host by id:" + hostId);
		}
	}

	private IEdgeHostMgrDao getEdgeHostDao() {
		if (hostDao == null) {
			hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		}
		return hostDao;
	}

	private static IEdgeVSBDao getEdgeVSBDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}

	/**
	 * When register is true, we will add converter to database, and map the node to the converter
	 * 
	 * @param hostId
	 *            The host id for register or unregister
	 * @param connectInfo
	 *            the converter connect information
	 * @param taskType
	 *            The original policy type, it can be VCM policy or RemoteVCM policy
	 * @param register
	 *            whether register or unregister
	 * @param hostType
	 *            The host type
	 */
	public void updateConverterForNode(int hostId, D2DConnectInfo connectInfo, int taskType, boolean register,
			int hostType) {
		try {
			locker.lock();
			logger.info("Update converter for host=" + hostId + " registerFlag=" + register);
			if (register && connectInfo == null) {
				logger.error("The converter connect info is null.");
				return;
			}
			if (register) {
				int converterHostId = connectInfo.getHostId();
				String hostName = connectInfo.getHostName();
				int protocol = "https".equalsIgnoreCase(connectInfo.getProtocol()) ? Protocol.Https.ordinal()
						: Protocol.Http.ordinal();
				int port = connectInfo.getPort();
				String userName = connectInfo.getUsername();
				String password = connectInfo.getPassword();
				String uuid = connectInfo.getUuid();
				String authUuid = connectInfo.getAuthUuid();
				int[] id = new int[1];
				int[] insert = new int[1];
				getEdgeVSBDao().as_edge_vsb_converter_cu(0, converterHostId, hostName, port, protocol, userName,
						password, uuid, authUuid, id, insert);
				gatewayService.bindEntity(connectInfo.getGatewayId(), id[0], EntityType.Converter);
				if (id[0] > 0) {
					// TODO [lijwe02] need to investigate whether save 0 or not, and do we need to keep current setting
					// for remote ndoes
					getEdgeVSBDao().as_edge_host_converter_map_cu(hostId, id[0],
							connectInfo.getVcmConverterType().ordinal(), taskType, 0);
					logger.info("Save converter:" + hostName + " and map to host:" + hostId + " successfully.");
				} else {
					logger.error("Failed to save converter:" + hostName);
				}
			} else {
				if (!HostTypeUtil.isNodeImportFromRHA(hostType)) {
					logger.info("Unregister converter for host:" + hostId);
					getEdgeVSBDao().as_edge_host_converter_map_d(hostId);
				}
			}
			cleanUpUselessConverterAndMaps();
		} catch (Exception e) {
			logger.error("Update converter for node failed, hostId=" + hostId + " registerFlag=" + register, e);
		} finally {
			locker.unlock();
		}
	}
	
	private void cleanUpUselessConverterAndMaps()
	{
		try
		{
			logger.info( "VSBTaskDeployer.cleanUpUselessConverterAndMaps(): Clear useless converter map and converters." );
			
			getEdgeVSBDao().cleanUpUselessConverterMaps();
			
			List<IntegerId> converterIds = new ArrayList<>();
			getEdgeVSBDao().getUselessConverters( converterIds );
			
			for (int i = 0; i < converterIds.size(); i ++)
			{
				int converterId = converterIds.get( i ).getId();
				getEdgeVSBDao().deleteConverter( converterId );
				gatewayService.unbindEntity( converterId, EntityType.Converter );
			}
		}
		catch (Exception e)
		{
			logger.error(
				"VSBTaskDeployer.cleanUpUselessConverterAndMaps(): Error clean up useless converter maps and converters.",
				e );
		}
	}

	/**
	 * When register is true, we will add monitor to database, and map the node to the monitor
	 * 
	 * @param hostId
	 *            The host id for register or unregister
	 * @param connectInfo
	 *            the monitor connect information
	 * @param register
	 *            whether register or unregister
	 */
	private void updateMonitorForNode(int hostId, HostConnectInfo connectInfo, boolean register) {
		try {
			locker.lock();
			logger.info("Update monitor for host=" + hostId + " registerFlag=" + register);
			if (register && connectInfo == null) {
				logger.error("The monitor connect info is null.");
				return;
			}
			if (register) {
				String hostName = connectInfo.getHostName();
				int protocol = connectInfo.getProtocol().ordinal();
				int port = connectInfo.getPort();
				String userName = connectInfo.getUserName();
				String password = connectInfo.getPassword();
				String uuid = connectInfo.getUuid();
				String authUuid = connectInfo.getAuthUuid();
				int[] id = new int[1];
				getEdgeVSBDao()
						.as_edge_vsb_monitor_cu(hostName, port, protocol, userName, password, uuid, authUuid, id);
				if (id[0] > 0) {
					getEdgeVSBDao().as_edge_host_monitor_map_cu(hostId, id[0]);
					logger.info("Save monitor:" + hostName + " and map to host:" + hostId + " successfully.");
				} else {
					logger.error("Failed to save monitor:" + hostName);
				}
			} else {
				logger.info("Unregister monitor for host:" + hostId);
				getEdgeVSBDao().as_edge_host_monitor_map_d(hostId);
			}
		} catch (Exception e) {
			logger.error("Failed to update monitor information, hostId=" + hostId + " registerFlag=" + register, e);
		} finally {
			locker.unlock();
		}
	}

	public EdgeVCMConnectInfo getConverter(int hostId) throws EdgeServiceFault {
		List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
		getEdgeVSBDao().as_edge_vsb_converter_getByHostId(hostId, converterList);
		if (converterList.size() > 0) {
			if (converterList.size() > 1) {
				logger.warn("More than one converter is queried from database, there might some error in the database.");
			}
			
			EdgeVCMConnectInfo converter = converterList.get(0);
			
			// this should get convertGateway not host gateway
			//GatewayEntity gateway = gatewayService.getGatewayByHostId( hostid );
			GatewayEntity gateway = gatewayService.getGatewayByHostId( converter.getHostId() );
			converter.setGatewayId( gateway.getId() );
			
			return converter;
		}
		return null;
	}

	private EdgeVCMConnectInfo getOldConverter(int hostId, int hostType) throws EdgeServiceFault {
		EdgeVCMConnectInfo converter = getConverter(hostId);
		if (converter != null && HostTypeUtil.isNodeImportFromRHA(hostType)) {
			// Get old converter
			int oldConverterId = converter.getOldConverterId();
			if (oldConverterId > 0) {
				List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
				getEdgeVSBDao().as_edge_vsb_converter_getById(oldConverterId, converterList);
				if (converterList.size() > 0) {
					EdgeVCMConnectInfo oldConverter = converterList.get(0);
					oldConverter.setGatewayId( converter.getGatewayId() );
					return oldConverter;
				}
			} else {
				return null;
			}
		}
		return converter;
	}

	private HostConnectInfo getHostConnectInfo(D2DConnectInfo d2dConnectInfo) {
		if (d2dConnectInfo == null) {
			return null;
		}
		HostConnectInfo hostConnectInfo = new HostConnectInfo();
		hostConnectInfo.setHostId( d2dConnectInfo.getHostId() );
		hostConnectInfo.setHostName(d2dConnectInfo.getHostName());
		hostConnectInfo.setPort(d2dConnectInfo.getPort());
		hostConnectInfo.setProtocol("HTTPS".equalsIgnoreCase(d2dConnectInfo.getProtocol()) ? Protocol.Https
				: Protocol.Http);
		hostConnectInfo.setUserName(d2dConnectInfo.getUsername());
		hostConnectInfo.setPassword(d2dConnectInfo.getPassword());
		hostConnectInfo.setUuid(d2dConnectInfo.getUuid());
		hostConnectInfo.setConverterType(d2dConnectInfo.getVcmConverterType());
		hostConnectInfo.setAuthUuid(d2dConnectInfo.getAuthUuid());
		hostConnectInfo.setGatewayId( d2dConnectInfo.getGatewayId() );
		return hostConnectInfo;
	}

	private VCMServiceImpl getVCMService() {
		if (vcmService == null) {
			vcmService = new VCMServiceImpl();
		}
		return vcmService;
	}

	private int getHostType(int hostId) throws Exception {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		getEdgeHostDao().as_edge_host_list(hostId, 1, hostList);
		if (hostList.isEmpty()) {
			throw new Exception("Node not exists.");
		}
		EdgeHost host = hostList.get(0);
		return host.getRhostType();
	}

	// private boolean isHostNonWindowsOS(int hostId) throws Exception {
	// List<EdgeHost> hostList = new ArrayList<EdgeHost>();
	// getEdgeHostDao().as_edge_host_list(hostId, 1, hostList);
	// if (hostList.isEmpty()) {
	// throw new Exception("Node not exists.");
	// }
	// EdgeHost host = hostList.get(0);
	// return HostTypeUtil.isVMNonWindowsOS(host.getRhostType());
	// }

	private void updateUuid(HostConnectInfo hostConnectInfo) {
		if (hostConnectInfo == null) {
			return;
		}

		String protocol = Protocol.Https == hostConnectInfo.getProtocol() ? "https" : "http";
		String hostName = hostConnectInfo.getHostName();
		int port = hostConnectInfo.getPort();
		if (StringUtil.isEmptyOrNull(hostName)) {
			return;
		}
		
		ConnectionContext context = new ConnectionContext(protocol, hostName, port);
		context.buildAuthUuid(hostConnectInfo.getAuthUuid());
		context.buildCredential(hostConnectInfo.getUserName(), hostConnectInfo.getPassword(), "");
		GatewayEntity gateway;
		try {
			gateway = gatewayService.getGatewayById(hostConnectInfo.getGatewayId());
		} catch (EdgeServiceFault e) {
			logger.error("Failed to find the gateway by id " + hostConnectInfo.getGatewayId() + ". " + e.getMessage());
			return;
		}
		context.setGateway(gateway);

		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			hostConnectInfo.setUuid(connection.getNodeUuid());
			hostConnectInfo.setAuthUuid(connection.getAuthUuid());
		} catch (Exception e) {
			logger.error("Failed to update uuid for node:" + hostName, e);
		}
	}

	/**
	 * When user change the session password from node view, after save the session password, we will update them to
	 * converter
	 * 
	 * @param hostIdList
	 *            The hosts to be update
	 */
	public void updateSessionPasswordToConverter(List<Integer> hostIdList) {
		if (hostIdList == null) {
			logger.error("The host id list is empty.");
			return;
		}
		for (Integer hostId : hostIdList) {
			try {
				logger.info("Update session password for node:" + hostId);
				EdgeVCMConnectInfo converter = getConverter(hostId);
				if (converter == null) {
					logger.error("The host:" + hostId + " has no converter.");
					continue;
				}
				
				VCMPolicyDeployParameters deployParameter = new VCMPolicyDeployParameters();
				populateSourceMachineInfo(hostId, converter.getConverterType(), deployParameter);
				populateSesionPassword(hostId, deployParameter);
				
				try (D2DConnection connection = connectionFactory.createD2DConnection(new ConverterConnectionContextProvider(hostId, getNodeService()))) {
					connection.connect();
					connection.getService().updateVCMSessionPassword(deployParameter);
				}
				
				logger.info("Update session password for node:" + hostId + " succeeded.");
			} catch (Exception e) {
				logger.error("Failed to update session password for node:" + hostId, e);
			}
		}
	}

	/**
	 * When user set the network setting, update network settings on converter
	 * 
	 * @param hostIdList
	 *            The nodes to be update
	 */
	public void updateIPSettingsToConverter(List<Integer> hostIdList) {
		if (hostIdList == null) {
			logger.error("The host id list is empty.");
			return;
		}
		for (Integer hostId : hostIdList) {
			try {
				logger.info("Update ip setting for node:" + hostId);
				List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
				policyDao.getHostPolicyMap(hostId, PolicyTypes.Unified, mapList);
				if (mapList.size() == 0) {
					logger.error("The node:" + hostId + " has no plan deployed.");
					continue;
				}
				EdgeVCMConnectInfo converter = getConverter(hostId);
				if (converter == null) {
					logger.error("The host:" + hostId + " has no converter.");
					continue;
				}
				
				VCMPolicyDeployParameters deployParameter = new VCMPolicyDeployParameters();
				populateSourceMachineInfo(hostId, converter.getConverterType(), deployParameter);
				Node node = new Node();
				node.setId(hostId);
				node.setPolicyIDForEsx(mapList.get(0).getPolicyId());
				populateIPSetting(node, deployParameter);
				
				try (D2DConnection connection = connectionFactory.createD2DConnection(new ConverterConnectionContextProvider(hostId, getNodeService()))) {
					connection.connect();
					connection.getService().updateVCMIPSettings(deployParameter);
				}
				
				logger.info("Update vcm ip setting for node:" + hostId + " succeeded.");
			} catch (Exception e) {
				logger.error("Failed to update vcm ip setting for node:" + hostId, e);
			}
		}
	}

	@Override
	void updateDeployStatus(PolicyDeploymentTask argument, int policyDeployStatus) {
		DeployUtil.updateNodeDeployStatus(argument.getHostId(), policyDeployStatus);
	}

	@Override
	void writeActivityLogAndDeployMessage(PolicyDeploymentTask argument,
			String message, String nodeName) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog(edgeTaskId, argument, null, message);
		warningErrorMessageWriter.addErrorMessage(argument, null, message);
	}

	@Override
	String getMessageSubject() {
		return EdgeCMWebServiceMessages.getResource("converter");
	}
}
