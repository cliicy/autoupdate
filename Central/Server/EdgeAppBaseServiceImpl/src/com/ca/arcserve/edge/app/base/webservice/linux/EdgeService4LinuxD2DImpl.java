package com.ca.arcserve.edge.app.base.webservice.linux;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.arcserve.edge.common.annotation.NonSecured;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeGatewayEntity;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeJobHistory;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSourceGroup;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IPolicyManagementService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.LinuxD2DJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImplWrapper;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.LinuxNodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.LinuxPlanManagmentHelper;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PlanRedeployService;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.linuximaging.webservice.AxisFault;
import com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode;
import com.ca.arcserve.linuximaging.webservice.data.D2DTime;
import com.ca.arcserve.linuximaging.webservice.data.ExportJobResult;
import com.ca.arcserve.linuximaging.webservice.data.NodeConnectionInfo;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.TargetMachineInfo;
import com.ca.arcserve.linuximaging.webservice.data.VersionInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupTarget;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult.LicenseComponent;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult.LicenseStatus;
import com.ca.arcserve.linuximaging.webservice.data.license.LicensedMachine;
import com.ca.arcserve.linuximaging.webservice.data.sync.PlanStatus;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncActivityLog;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncData;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncDataResult;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncDataResultItem;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncDataResultItemForJob;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncDataResultItemForNode;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncJobHistory;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncJobMonitor;
import com.ca.arcserve.linuximaging.webservice.edge.IEdgeService4LinuxD2D;

public class EdgeService4LinuxD2DImpl implements IEdgeService4LinuxD2D {

	private static Logger logger = Logger
			.getLogger(EdgeService4LinuxD2DImpl.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory
			.getDao(IEdgeHostMgrDao.class);
	private static IEdgeConnectInfoDao connectDao = DaoFactory
			.getDao(IEdgeConnectInfoDao.class);
	private static IEdgePolicyDao edgePolicyDao = DaoFactory
			.getDao(IEdgePolicyDao.class);
	private IEdgeJobHistoryDao jobHistoryDao = DaoFactory
			.getDao(IEdgeJobHistoryDao.class);
	private IEdgeGatewayDao gatewayDao = DaoFactory
			.getDao(IEdgeGatewayDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IPolicyManagementService policyService = new PolicyManagementServiceImpl();
	private IActivityLogService logService = new ActivityLogServiceImpl();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory
			.getBean(IEdgeGatewayLocalService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);

	public EdgeService4LinuxD2DImpl() {
	}

	@Override
	public int synchronizeActivityLog(List<SyncActivityLog> list) {
		logger.debug("start to synchronize ActivityLog.");
		if (list == null || list.size() == 0) {
			return 0;
		}
		DaoFactory.beginTrans();
		try {

			for (SyncActivityLog log : list) {
				saveActivityLog(log);
			}
		} catch (Exception e) {
			logger.error(
					"synchronizeActivityLog failed, insert log into DB failed.",
					e);
			DaoFactory.rollbackTrans();
			return 1;
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		return 0;
	}

	private void saveActivityLog(SyncActivityLog log) throws EdgeServiceFault {
		logger.debug(log.toString());
		int serverHostId = EdgeService4LinuxD2DUtil.getD2DHostId(log
				.getServerUuid());
		logger.debug("serverHostId is " + serverHostId);
		if (serverHostId == 0) {
			logger.error("can't find linux server in CM. drop the activity log: "
					+ log.toString());
			return;
		}
		int targetHostId = getLinuxNodeId(log.getTargetHost(),
				log.getTargetUUID());

		if (targetHostId == 0) {
			logger.error("can't find linux node in CM. drop the activity log: "
					+ log.toString());
			return;
		}
		logService.addD2dLog(log.getVersion(), log.getProductType(), new Date(
				log.getLogUtcTime()), new Date(log.getLogLocalTime()), log
				.getSeverity(), log.getJobId(), log.getJobType(), serverHostId,
				targetHostId, 0, 0, "", "", log.getPlanId(), "", log
						.getMessageText().trim());
	}

	@Override
	public int synchronizeJobHistory(List<SyncJobHistory> list) {
		logger.debug("start to synchronize JobHistory.");
		if (list == null || list.size() == 0) {
			return 0;
		}
		DaoFactory.beginTrans();
		try {
			for (SyncJobHistory history : list) {
				saveJobHistory(history);
			}
		} catch (Exception e) {
			logger.error(
					"synchronizeJobHistory failed, insert history into DB failed.",
					e);
			DaoFactory.rollbackTrans();
			return 1;
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		return 0;
	}

	private void saveJobHistory(SyncJobHistory history) {
		logger.debug(history.toString());
		int serverHostId = EdgeService4LinuxD2DUtil.getD2DHostId(history
				.getServerUuid());
		logger.debug("serverHostId is " + serverHostId);
		if (serverHostId == 0) {
			logger.error("can't find linux server in CM. drop the job history: "
					+ history.toString());
			return;
		}
		int targetHostId = getLinuxNodeId(history.getTargetHost(),
				history.getTargetUUID());
		if (targetHostId == 0) {
			logger.error("can't find linux node in CM. drop the job history: "
					+ history.toString());
			return;
		}

		logger.info("Linux job history rps uuid " + history.getRpsUUID());
		int targetRpsHostId = 0;
		if (history.getRpsUUID() != null && !history.getRpsUUID().equals("")) {
			targetRpsHostId = D2DEdgeServiceImpl.getRpsHostId(history
					.getRpsUUID());
		}
	
		// 751567 752721 Job monitor in node recent event cannot shown if run job immediately after previous backup complete
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		jobHistoryDao.as_edge_d2dJobHistory_monitor_getJobMonitorByHistoy(history.getJobType(), history.getJobId(), serverHostId, targetHostId, lstJobHistory);
		if( (lstJobHistory!=null) && (!lstJobHistory.isEmpty())){
			JobHistory removeHistory = new JobHistory();
			removeHistory.setJobType(lstJobHistory.get(0).getJobType());
			removeHistory.setJobId(lstJobHistory.get(0).getJobId());
			removeHistory.setServerId(lstJobHistory.get(0).getServerId());
			removeHistory.setAgentId(lstJobHistory.get(0).getAgentId());
			removeHistory.setJobUUID(lstJobHistory.get(0).getJobUUID());
			D2DAllJobStatusCache.getD2DAllJobStatusCache().removeCacheJobMonitorByHistory(removeHistory);
		}		
		long[] jobHistoryId = new long[1];
		jobHistoryDao.as_edge_d2dJobHistory_add(history.getVersion(), history
				.getJobId(), history.getJobType(), history.getJobMethod(),
				history.getJobStatus(), new Date(history.getJobUTCStartDate()),
				new Date(history.getJobLocalStartDate()),
				new Date(history.getJobUTCEndDate()),
				new Date(history.getJobLocalEndDate()), serverHostId,
				targetHostId, 0, targetRpsHostId, history.getDatastoreUUID(),
				"", history.getPlanId(), "", history.getProductType(), 0,
				jobHistoryId);
		logger.debug("jobHistoryId: " + jobHistoryId[0]);
		jobHistoryDao.as_edge_d2dJobHistoryDetail_add(jobHistoryId[0],
				history.getProtectedDataSize(), history.getRawDataSize(),
				history.getBackupedDataSize(), -1, -1, -1, history.getBackupDestination(),
				history.getSessionId(), history.getBmrFlag(),
				history.getSessionEncrypted(), history.getRecoveryPointType(),
				"", "", -1, "");
	}

	private int getLinuxNodeId(String nodeName, String nodeUUID) {
		int nodeId = 0;
		if (nodeUUID == null || nodeUUID.isEmpty()) {
		 logger.error("Linux node does not have uuid jobMonitor:"
					+ nodeName);
		} else {
			nodeId = getLinuxNodeIdFromConnectionInfo(nodeUUID);
			if (nodeId == 0) {
				logger.error("Can't find linux node in host using nodeUUID "
						+ nodeUUID + " try to find it in hbbu vm.");
				nodeId = getLinuxVMNodeId(nodeUUID);
				if (nodeId == 0) {
					logger.error("Can't find linux vm node in hbbu vm nodeUUID "
							+ nodeUUID);
				}
			}
		}
		return nodeId;
	}

	private int getLinuxNodeIdFromConnectionInfo(String nodeUUID) {
		String[] hostname = new String[1];
		int[] protcol = new int[1];
		int[] port = new int[1];
		int[] targetHostId = new int[1];
		connectDao.as_edge_GetConnInfoByUUID(nodeUUID, targetHostId, hostname,
				protcol, port);
		return targetHostId[0];
	}

	private int getLinuxVMNodeId(String nodeUUID) {
		int[] targetHostId = new int[1];
		esxDao.as_edge_host_getHostByInstanceUUID(0, nodeUUID, targetHostId);	//TODO: gateway
		return targetHostId[0];
	}

	@Override
	@NonSecured
	public int validateLinuxD2DByUUID(String uuid) {
		if (uuid == null || uuid.isEmpty())
			throw EdgeService4LinuxD2DUtil
					.generateSOAPFaultException(
							EdgeServiceErrorCode.Login_UUIDRequired,
							"UUID is required");

		String appUUID = CommonUtil.retrieveCurrentAppUUID();
		if (!uuid.equals(appUUID))
			throw EdgeService4LinuxD2DUtil.generateSOAPFaultException(
					EdgeServiceErrorCode.Login_WrongUUID, "Wrong UUID");
		return 0;
	}

	@Override
	public int synchronizeJobMonitor(List<SyncJobMonitor> jobMonitorList) {
		LinuxD2DJobMonitor jobMonitor = null;

		for (SyncJobMonitor jm : jobMonitorList) {
			jobMonitor = new LinuxD2DJobMonitor();
			jobMonitor.setJobId(jm.getJobID());
			jobMonitor.setElapsedTime(jm.getElapsedTime());
			jobMonitor.setJobMethod(jm.getJobMethod());
			jobMonitor.setJobPhase(jm.getJobPhase());
			jobMonitor.setJobStatus(jm.getStatus());
			jobMonitor.setJobType(jm.getJobType());
			jobMonitor.setProgress(jm.getProgress());
			jobMonitor.setStartTime(jm.getExecuteTime());
			jobMonitor.setnReadSpeed(jm.getThroughput());
			jobMonitor.setnWriteSpeed(jm.getWriteThroughput());
			jobMonitor.setCurrentProcessDiskName(jm.getVolume());
			jobMonitor.setTransferBytesJob(jm.getProcessedData());
			jobMonitor.setJobUUID(jm.getJobUUID());
			jobMonitor.setPlanUUID(jm.getPlanUUID());
			jobMonitor.setCompressLevel(jm.getCompression());
			jobMonitor.setEncInfoStatus(jm.getEncryption());
			jobMonitor.setRunningOnRPS(jm.isBackupToRps());
			if (jobMonitor.getJobType() == JobType.JOBTYPE_LINUX_INSTANT_VM) {
				/**
				 * set start/stop job id into job id. 
				 * set finished if start job end. 
				 * set job mothod(60/61) into job type
				 */
				if(jm.isStartInstantVMFinished() && jm.getStopInstantVMJobId()>0){
					jobMonitor.setJobId(jm.getStopInstantVMJobId());
				}
				jobMonitor.setFinished(jm.isStartInstantVMFinished());
				if(jm.getJobMethod() == JobType.JOBTYPE_STOP_INSTANT_VM || jm.getJobMethod() == JobType.JOBTYPE_START_INSTANT_VM){
					jobMonitor.setJobType(jm.getJobMethod());
				}
				InstantVMManager.getInstance().handleJobMonitor(jobMonitor,JobHistoryProductType.LinuxD2D);
				continue;
			}
			int nodeId = getLinuxNodeId(jm.getNodeName(), jm.getNodeUUID());
			jobMonitor.setNodeId(nodeId);
			jobMonitor.setAgentNodeName(jm.getNodeName());
			// defect 192793
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			hostMgrDao.as_edge_host_list(nodeId, 1, hosts);
			if (hosts.size() > 0) {
				jobMonitor.setAgentNodeName(hosts.get(0).getRhostname());
				jobMonitor.setD2dUuid(hosts.get(0).getD2DUUID());
				jobMonitor.setVmInstanceUUID(hosts.get(0).getVmInstanceUuid());
			}
			List<EdgeConnectInfo> linuxD2DInfo = new ArrayList<EdgeConnectInfo>();
			connectDao.as_edge_linux_d2d_server_by_hostid(nodeId, linuxD2DInfo);
			if (linuxD2DInfo != null && linuxD2DInfo.size() > 0) {
				jobMonitor.setRunningServerId(linuxD2DInfo.get(0).getHostid());
				jobMonitor
						.setServerNodeName(linuxD2DInfo.get(0).getRhostname());
			} else {
				List<EdgeSourceGroup> groups = new ArrayList<EdgeSourceGroup>();
				hostMgrDao.as_edge_linuxServer_group_list(0,groups);
				for (EdgeSourceGroup group : groups) {
					if (jm.getLinuxD2DIp() != null) {
						if (group.getName().equalsIgnoreCase(
								jm.getLinuxD2DName())
								|| jm.getLinuxD2DIp().contains(group.getName())) {
							jobMonitor.setRunningServerId(group.getId());
							jobMonitor.setServerNodeName(group.getName());
						}
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("LinuxD2D").append("-").append(nodeId).append("-")
					.append(jobMonitor.getRunningServerId()).append("-")
					.append(jobMonitor.getJobType()).append("-")
					.append(jobMonitor.getJobUUID());
			logger.debug("Linux D2D job monitor" + sb.toString()
					+ " AgentId = " + nodeId + " AgentName = "
					+ jm.getNodeName() + " " + jobMonitor.toString());
			jobMonitor.setJobMonitorId(sb.toString());
			D2DAllJobStatusCache.getD2DAllJobStatusCache().put(sb.toString(),
					jobMonitor);
		}
		return 0;
	}

	@Override
	public int getPlanStatus(int planId, String planUUID, String nodeName) {
		int planStatus = PlanStatus.OK;
		try {
			UnifiedPolicy policy = policyService.loadUnifiedPolicyById(planId);
			if (!policy.getUuid().equals(planUUID)) {
				planStatus = PlanStatus.PLAN_CHANGED;
			} else {
				int[] targetHostId = new int[1];
				hostMgrDao.as_edge_host_getIdByHostnameForLinux(nodeName, null,
						1, targetHostId);
				int nodeId = targetHostId[0];
				// List<Integer> nodeList = policy.getNodes();
				List<Integer> nodeList = new ArrayList<Integer>();
				List<ProtectedResourceIdentifier> identifiers = policy
						.getProtectedResources();
				for (ProtectedResourceIdentifier identifier : identifiers) {
					if (identifier.getType() == ProtectedResourceType.node) {
						nodeList.add(identifier.getId());
					}
				}
				if (!nodeList.contains(nodeId)) {
					planStatus = PlanStatus.NODE_NOT_PROTECT_BY_THE_PLAN;
				}
			}
		} catch (EdgeServiceFault e) {
			planStatus = PlanStatus.PLAN_NOT_EXIST;
		}
		return planStatus;
	}

	@Override
	@NonSecured
	public LicenseResult checkCentralLicense(String authKey,
			LicensedMachine node) {
		validateLinuxD2DByUUID(authKey);
		if (node == null) {
			logger.error("parameter is null.");
			return new LicenseResult(LicenseComponent.Basic,
					LicenseStatus.ERROR);
		} else {
			logger.debug("checkCentralLicense start. linux node info: "
					+ node.toString());
		}
		MachineInfo machine = EdgeService4LinuxD2DUtil
				.convertToMachineInfo(node);
		long required_feature = EdgeService4LinuxD2DUtil
				.convertToRequiredFeature(node.getMachineType());
		logger.debug("convert to MachineInfo: " + machine.toString());
		logger.debug("convert to required_feature: " + required_feature);
		LicenseCheckResult result = null;
		try {
			LicenseServiceImplWrapper licenseServiceNew = new LicenseServiceImplWrapper();
			result = licenseServiceNew.checkLicense(
					LicenseDef.UDP_CLIENT_TYPE.UDP_LINUX_AGENT, machine,
					required_feature);
		} catch (Exception e) {
			logger.error("checkCentralLicense occurs exception: ", e);
			return new LicenseResult(LicenseComponent.Basic,
					LicenseStatus.ERROR);
		}

		if (result == null) {
			logger.debug("checkLicense return null.");
			return new LicenseResult(LicenseComponent.Basic,
					LicenseStatus.TERMINATE);
		} else {
			logger.debug("checkLicense return: "
					+ result.getLicense().getCode() + " "
					+ result.getLicense().getDisplayName() + " "
					+ result.getLicense().getId() + " "
					+ result.getState().name());
			return EdgeService4LinuxD2DUtil.convertToLicenseResult(result);
		}
	}

	@Override
	public SyncDataResult synchronizeData(SyncData data) {
		DaoFactory.beginTrans();
		try {

			SyncDataResult result = new SyncDataResult();
			int resultCode = SyncDataResult.SYNC_DATA_RESULT_SUCCED;
			ServerInfo serverInfo = data.getBackupServer();
			LinuxNodeServiceImpl nodeImpl = new LinuxNodeServiceImpl();
			String ip = getIPAddress(serverInfo.getName());
			int serverHostId = EdgeService4LinuxD2DUtil.getD2DHostId(serverInfo
					.getUuid());
			SyncDataResultItem itemForServer = new SyncDataResultItem();
			if (serverHostId > 0) {
				result.setResult(SyncDataResult.SYNC_DATA_RESULT_FAILED);
				itemForServer
						.setItemResult(SyncDataResultItem.SYNC_DATA_RESULT_ITEM_FAILED);
				itemForServer
						.setErrorCode(FlashServiceErrorCode.Sync_Data_Server_Exist_In_UDP);
				result.setSyncDataResultItemForServer(itemForServer);
				return result;
			}
			VersionInfo versionInfo = data.getVersionInfo();
			String majorVersion = "";
			String minorVersion = "";
			String buildNumber = "";
			if (versionInfo.getVersion() != null) {
				String version = versionInfo.getVersion();
				if (version.contains(".")) {
					String[] versionArray = version.split("\\.");
					majorVersion = versionArray[0];
					minorVersion = versionArray[1];
				}
			}
			if (versionInfo.getBuildNumber() != null) {
				buildNumber = versionInfo.getBuildNumber();
			}
			int[] output = new int[1];

			String hostName = serverInfo.getName();
			if (!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
			GatewayId gatewayId = getGatewayId(data.getGatewayUUID());
			
			List<String> fqdnNameList = new ArrayList<String>();
			if(gatewayId != null && gatewayId.isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[EdgeService4LinuxD2DImpl] synchronizeData() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			hostMgrDao.as_edge_host_update(0, new Date(), hostName,
					serverInfo.getDescription(), ip,
					serverInfo.getDescription(), "", 1,
					ApplicationUtil.setLinuxD2DInstalled(0), "", 0,
					ProtectionType.LINUX_D2D_SERVER.getValue(), fqdnNames, output);
			hostMgrDao.as_edge_host_update_timezone_by_id(output[0],
					versionInfo.getTimeZoneOffset());
			connectDao.as_edge_connect_info_update(output[0],
					serverInfo.getUser(), serverInfo.getPassword(),
					serverInfo.getUuid(),
					getProtocol(serverInfo.getProtocol()),
					serverInfo.getPort(), 0, majorVersion, minorVersion, "",
					buildNumber, NodeManagedStatus.Managed.ordinal());
			connectDao.as_edge_connect_info_setAuthUuid(serverInfo.getUuid(),
					serverInfo.getAuthKey());
			int d2dServerId = output[0];
			
			this.gatewayService.bindEntity(gatewayId, d2dServerId,
					EntityType.Node);
			itemForServer
					.setItemResult(SyncDataResultItem.SYNC_DATA_RESULT_ITEM_SUCCED);
			result.setSyncDataResultItemForServer(itemForServer);
			List<TargetMachineInfo> nodeList = data.getNodeList();
			Map<String, Integer> nodeIdMap = null;
			if (nodeList != null && nodeList.size() > 0) {
				nodeIdMap = syncNodeInfo(nodeImpl, nodeList, result, gatewayId);
			}
			syncJobInfo(data.getJobList(), serverInfo, d2dServerId, nodeIdMap,
					result);
			result.setManagedServerUrl(getConsoleUrl());
			result.setResult(resultCode);
			return result;
		} catch (Exception e) {
			logger.error("synchronizeData failed, insert data into DB failed.",
					e);
			DaoFactory.rollbackTrans();
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		return null;
	}

	private String getConsoleUrl() {
		String edgeHostName = "";
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int edgePort = EdgeCommonUtil.getEdgeWebServicePort(); // ???
		edgeHostName = CommonUtil.getServerIpFromFile();
		if (StringUtil.isEmptyOrNull(edgeHostName)) {
			edgeHostName =EdgeCommonUtil.getLocalFqdnName();
		}
		return EdgeCommonUtil.getConsoleUrl(edgeHostName, edgePort,
				edgeProtocol);
	}

	private GatewayId getGatewayId(String gatewayUUID) {
		logger.info("Gateway uuid: " + gatewayUUID);
		List<EdgeGatewayEntity> gatewayList = new ArrayList<EdgeGatewayEntity>();
		gatewayDao.getGatewayByUuid(gatewayUUID, gatewayList);
		if (gatewayList.size() == 0) {
			logger.info("use local Gateway.");
			return gatewayService.getLocalGateway().getId();
		} else {
			GatewayId gatewayId = new GatewayId();
			gatewayId.setRecordId(gatewayList.get(0).getId());
			logger.info("Gateway uuid: " + gatewayId.getRecordId());
			return gatewayId;
		}
	}

	private Map<String, Integer> syncNodeInfo(LinuxNodeServiceImpl nodeImpl,
			List<TargetMachineInfo> nodeList, SyncDataResult result,
			GatewayId gatewayId) {
		Map<String, Integer> nodeIdMap = new HashMap<String, Integer>();
		List<SyncDataResultItemForNode> resultForNode = new ArrayList<SyncDataResultItemForNode>();
		for (TargetMachineInfo node : nodeList) {
			String nodeIp = getIPAddress(node.getName());
			boolean isNodeExist = nodeImpl.isNodeExists(node.getName(), nodeIp,
					1);
			SyncDataResultItemForNode itemForNode = new SyncDataResultItemForNode();
			if (isNodeExist) {
				itemForNode
						.setItemResult(SyncDataResultItem.SYNC_DATA_RESULT_ITEM_FAILED);
				itemForNode
						.setErrorCode(FlashServiceErrorCode.Sync_Data_Node_Exist_In_UDP);
				itemForNode.setNodeName(node.getName());
			} else {
				int[] op = new int[1];

				String hostName = node.getName();
				if (!StringUtil.isEmptyOrNull(hostName))
					hostName = hostName.toLowerCase();

				List<String> fqdnNameList = new ArrayList<String>();
				if(gatewayId != null && gatewayId.isValid()){
					try {
						IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
						fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
					} catch (Exception e) {
						logger.error("[EdgeService4LinuxD2DImpl] syncNodeInfo() get fqdn name failed.",e);
					}
				}
				String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
				
				hostMgrDao.as_edge_host_update(0, new Date(), hostName,
						node.getDescription(), nodeIp,
						node.getOperatingSystem(), "", 1, 0, "",
						HostTypeUtil.setLinuxNode(0),
						ProtectionType.WIN_D2D.getValue(), fqdnNames, op);
				connectDao.as_edge_connect_info_update(op[0], node.getUser(),
						node.getPassword(), node.getUUID(), 0, 0, 0, "", "",
						"", "", NodeManagedStatus.Managed.ordinal());
				this.gatewayService.bindEntity( gatewayId, op[0], EntityType.Node );
				nodeIdMap.put(node.getName(), op[0]);
				itemForNode
						.setItemResult(SyncDataResultItem.SYNC_DATA_RESULT_ITEM_SUCCED);
			}
			resultForNode.add(itemForNode);
		}
		result.setSyncDataResultItemsForNode(resultForNode);
		return nodeIdMap;
	}

	private void syncJobInfo(List<BackupConfiguration> bcList,
			ServerInfo serverInfo, int d2dServerId,
			Map<String, Integer> nodeIdMap, SyncDataResult result)
			throws EdgeServiceFault {
		if (bcList != null && bcList.size() > 0) {
			List<SyncDataResultItemForJob> syncDataResultList = new ArrayList<SyncDataResultItemForJob>();
			PolicyManagementServiceImpl serviceImpl = new PolicyManagementServiceImpl();
			for (BackupConfiguration bc : bcList) {
				int policyId = serviceImpl.getPolicyIdByName(bc.getJobName());
				SyncDataResultItemForJob resultForJob = new SyncDataResultItemForJob();
				int syncDataResultCode = SyncDataResultItem.SYNC_DATA_RESULT_ITEM_SUCCED;
				String jobName = bc.getJobName();
				String errorCode = null;
				if (policyId > 0) {
					syncDataResultCode = SyncDataResultItem.SYNC_DATA_RESULT_ITEM_WARNING;
					errorCode = FlashServiceErrorCode.Sync_Data_Policy_Rename_In_UDP;
					resultForJob.setOldJobName(jobName);
					jobName = jobName + "_" + serverInfo.getName();
					bc.setJobName(jobName);

				}

				UnifiedPolicy plan = convertToUnifiedPolicy(bc, d2dServerId,
						nodeIdMap);

				int newPolicyId = LinuxPlanManagmentHelper
						.saveLinuxUnifiedPolicy(serviceImpl, plan, true, false);
				/*
				 * for(int nodeId : plan.getNodes()){
				 * edgePolicyDao.assignPolicy(nodeId, PolicyTypes.Unified,
				 * newPolicyId, PolicyDeployStatus.ToBeDeployed); }
				 * edgePolicyDao
				 * .as_edge_policy_AddD2DRole(newPolicyId,d2dServerId
				 * ,D2DRole.LinuxD2D);
				 */
				edgePolicyDao
						.as_edge_policy_updateStatus(
								newPolicyId,
								com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus.CreateSuccess);

				resultForJob.setItemResult(syncDataResultCode);
				resultForJob.setErrorCode(errorCode);
				resultForJob.setJobUUID(bc.getTemplateID());
				resultForJob.setNewPlanId(newPolicyId);
				resultForJob.setJobName(jobName);
				syncDataResultList.add(resultForJob);
			}
			result.setSyncDataResultItemsForJob(syncDataResultList);
		}
	}

	private UnifiedPolicy convertToUnifiedPolicy(BackupConfiguration bc,
			int d2dServerId, Map<String, Integer> nodeIdMap) {
		UnifiedPolicy plan = new UnifiedPolicy();
		plan.setName(bc.getJobName());
		LinuxBackupSettings lbs = new LinuxBackupSettings();
		LinuxBackupLocationInfo location = new LinuxBackupLocationInfo();
		location.setBackupDestLocation(bc.getBackupLocationInfo()
				.getBackupDestLocation());
		location.setBackupDestUser(bc.getBackupLocationInfo()
				.getBackupDestUser());
		location.setBackupDestPasswd(bc.getBackupLocationInfo()
				.getBackupDestPasswd());
		location.setType(bc.getBackupLocationInfo().getType());
		lbs.setBackupLocationInfo(location);
		lbs.setLinuxD2DServerId(d2dServerId);
		lbs.setExcludeFiles(bc.getExcludeFiles());
		lbs.setExcludeVolumes(bc.getVolumesFilter());
		lbs.setServerScriptAfterJob(bc.getServerScriptAfterJob());
		lbs.setServerScriptBeforeJob(bc.getServerScriptBeforeJob());
		lbs.setTargetScriptAfterJob(bc.getTargetScriptAfterJob());
		lbs.setTargetScriptAfterSnapshot(bc.getTargetScriptAfterSnapshot());
		lbs.setTargetScriptBeforeJob(bc.getTargetScriptBeforeJob());
		lbs.setTargetScriptBeforeSnapshot(bc.getTargetScriptBeforeSnapshot());
		plan.setLinuxBackupsetting(lbs);

		com.ca.arcflash.webservice.data.backup.BackupConfiguration bConfig = new com.ca.arcflash.webservice.data.backup.BackupConfiguration();
		bConfig.setCompressionLevel(getCompressionLevel(bc.getCompressLevel()));
		int encryptAlgo = getEncryptAlgo(bc.getEncryptAlgoName());
		bConfig.setEncryptionAlgorithm(encryptAlgo);
		bConfig.setEnableEncryption(encryptAlgo == 0 ? false : true);
		bConfig.setEncryptionKey(bc.getEncryptPasswd());
		bConfig.setThrottling(bc.getThrottle());

		RetentionPolicy retentionPolicy = new RetentionPolicy();
		retentionPolicy
				.setBackupSetCount(bc.getRetention().getBackupSetCount());
		retentionPolicy.setUseWeekly(bc.getRetention().isUseWeekly());
		retentionPolicy.setDayOfMonth(bc.getRetention().getDayOfMonth());
		retentionPolicy.setDayOfWeek(bc.getRetention().getDayOfWeek());
		bConfig.setRetentionPolicy(retentionPolicy);

		AdvanceSchedule advanceSchecule = new AdvanceSchedule();
		Map<Integer, DailyScheduleDetailItem> itemMap = new HashMap<Integer, DailyScheduleDetailItem>();
		Map<Integer, ArrayList<ScheduleDetailItem>> detailItemMap = new HashMap<Integer, ArrayList<ScheduleDetailItem>>();
		for (BackupSchedule bSchedule : bc.getWeeklySchedule()
				.getScheduleList()) {
			DailyScheduleDetailItem item = itemMap.get(bSchedule.getDay());
			if (item == null) {
				item = new DailyScheduleDetailItem();
				item.setDayofWeek(bSchedule.getDay());
				itemMap.put(bSchedule.getDay(), item);
			}
			ArrayList<ScheduleDetailItem> detailItems = detailItemMap
					.get(bSchedule.getDay());
			if (detailItems == null) {
				detailItems = new ArrayList<ScheduleDetailItem>();
				detailItemMap.put(bSchedule.getDay(), detailItems);
			}
			ScheduleDetailItem detailItem = new ScheduleDetailItem();
			detailItem.setInterval(bSchedule.getInterval());
			detailItem.setIntervalUnit(bSchedule.getIntervalUnit());
			detailItem.setJobType(getBackupType(bSchedule.getJobType()));
			detailItem.setRepeatEnabled(bSchedule.isEnabled());
			detailItem.setStartTime(convertToDayTime(bSchedule.getStartTime()));
			if (bSchedule.isEnabled())
				detailItem.setEndTime(convertToDayTime(bSchedule.getEndTime()));
			detailItems.add(detailItem);
			item.setScheduleDetailItems(detailItems);
		}
		List<DailyScheduleDetailItem> scheduleItemList = new ArrayList<DailyScheduleDetailItem>();
		for (DailyScheduleDetailItem item : itemMap.values()) {
			scheduleItemList.add(item);
		}
		advanceSchecule.setDailyScheduleDetailItems(scheduleItemList);
		bConfig.setAdvanceSchedule(advanceSchecule);
		plan.setBackupConfiguration(bConfig);
		plan.setUuid(bc.getTemplateID());
		plan.setEnable(!bc.isDisable());

		List<ProtectedResourceIdentifier> nodeList = new ArrayList<ProtectedResourceIdentifier>();
		List<BackupTarget> targetList = bc.getOtherTargets();
		if (nodeIdMap != null && targetList != null) {
			for (BackupTarget target : targetList) {
				if (nodeIdMap.get(target.getName()) != null) {
					ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
					identifier.setId(nodeIdMap.get(target.getName()));
					identifier.setType(ProtectedResourceType.node);
					nodeList.add(identifier);
				}
			}
		}
		// plan.setNodes(nodeList);
		plan.setProtectedResources(nodeList);

		List<Integer> orderList = new ArrayList<Integer>();
		orderList.add(11);
		plan.setOrderList(orderList);
		return plan;
	}

	private int getCompressionLevel(int level) {
		if (level == 2) {
			return 9;
		}
		return level;
	}

	private int getEncryptAlgo(String encryptAlgorithmName) {
		int encryptAlgorithm = 0;
		if (encryptAlgorithmName.equals("None")) {
			encryptAlgorithm = 0;
		} else if (encryptAlgorithmName.equals("AES128")) {
			encryptAlgorithm = 65537;
		} else if (encryptAlgorithmName.equals("AES192")) {
			encryptAlgorithm = 65538;
		} else if (encryptAlgorithmName.equals("AES256")) {
			encryptAlgorithm = 65539;
		}
		return encryptAlgorithm;
	}

	private DayTime convertToDayTime(D2DTime d2dTime) {
		DayTime time = new DayTime();
		time.setHour(d2dTime.getHour());
		time.setMinute(d2dTime.getMinute());

		if (d2dTime.getAmPM() == 1) {
			time.setHour(d2dTime.getHour() + 12);
		}

		return time;
	}

	private int getBackupType(int jobType) {
		int newType = 0;
		switch (jobType) {
		case 3:
			newType = 0;
			break;
		case 4:
			newType = 1;
			break;
		case 5:
			newType = 2;
			break;
		}
		return newType;
	}

	@Override
	@NonSecured
	public String validateLinuxD2DByUser(String username, String password) {
		throw new RuntimeException("this method will be never called");
	}

	private int getProtocol(String protocol) {
		if ("http".equalsIgnoreCase(protocol)) {
			return Protocol.Http.ordinal();
		} else {
			return Protocol.Https.ordinal();
		}
	}

	private String getIPAddress(String hostname) {
		try {
			InetAddress addr = InetAddress.getByName(hostname);
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("getIPAddress(String)", e);
			return "";
		}
	}

	@Override
	public ExportJobResult importLinuxJobs(String backupServer,
			List<BackupConfiguration> jobList) {
		String ip = getIPAddress(backupServer);
		int[] d2dServerId = new int[1];
		hostMgrDao.as_edge_host_getIdByHostnameForLinux(backupServer, ip, 2,
				d2dServerId);
		PolicyManagementServiceImpl serviceImpl = new PolicyManagementServiceImpl();
		Map<String, String> errorMap = new HashMap<String, String>();
		ExportJobResult result = new ExportJobResult();
		for (BackupConfiguration bc : jobList) {
			if (bc.getScheduleType() != 5) {
				errorMap.put(
						bc.getJobName(),
						FlashServiceErrorCode.Sync_Data_Policy_Not_SUPPORT_In_UDP);
			} else {
				try {
					int policyId = serviceImpl.getPolicyIdByName(bc
							.getJobName());
					if (policyId > 0) {
						errorMap.put(bc.getJobName(),
								FlashServiceErrorCode.Common_Error_Job_Exist);
					} else {
						UnifiedPolicy plan = convertToUnifiedPolicy(bc,
								d2dServerId[0], null);
						int newPolicyId = serviceImpl.saveUnifiedPolicy(plan,
								false);
						edgePolicyDao.as_edge_policy_AddD2DRole(newPolicyId,
								d2dServerId[0], D2DRole.LinuxD2D);
						edgePolicyDao
								.as_edge_policy_updateStatus(
										newPolicyId,
										com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus.CreateSuccess);

					}
				} catch (EdgeServiceFault e) {
					logger.error("importLinuxJobs", e);
				}
			}
		}
		if (errorMap.size() == jobList.size()) {
			result.setResult(ExportJobResult.FAILED_NO_JOB_IMPORT);
		}
		result.setErrorMap(errorMap);

		return result;
	}

	@Override
	public NodeConnectionInfo registNodeToUDP(TargetMachineInfo targetNode,
			boolean isForce) {
		NodeRegistrationInfo nodeRegistrationInfo = convertToNodeRegistrationInfo(targetNode);
		LinuxNodeServiceImpl nodeImpl = new LinuxNodeServiceImpl();
		RegistrationNodeResult registrationNodeResult;
		try {
			registrationNodeResult = nodeImpl.registerLinuxNode(
					nodeRegistrationInfo, isForce);
		} catch (EdgeServiceFault e) {
			logger.error("regist node failed.", e);
			throw convertEdgeServiceFault2AxisFault(e);
		}

		NodeConnectionInfo nodeInfo = convertRegistrationNodeResultToNodeConnectionInfo(registrationNodeResult);
		return nodeInfo;
	}

	@Override
	public NodeConnectionInfo modifyNodeInUDP(TargetMachineInfo targetNode,
			boolean isForce) {
		int isExisted[] = new int[1];
		hostMgrDao.as_edge_host_linux_node_isexisted(targetNode.getName(),
				null, 1, isExisted);
		if (isExisted[0] == 0) {
			NodeConnectionInfo nci = new NodeConnectionInfo();
			nci.setErrCode1(90);
			return nci;
		} else {
			int hostId[] = new int[1];
			hostMgrDao.as_edge_host_getIdByHostnameForLinux(
					targetNode.getName(), null, 1, hostId);
			NodeRegistrationInfo nodeRegistrationInfo = convertToNodeRegistrationInfo(targetNode);
			nodeRegistrationInfo.setId(hostId[0]);
			LinuxNodeServiceImpl nodeImpl = new LinuxNodeServiceImpl();
			RegistrationNodeResult registrationNodeResult;
			try {
				registrationNodeResult = nodeImpl.registerLinuxNode(
						nodeRegistrationInfo, isForce);
			} catch (EdgeServiceFault e) {
				logger.error("regist node failed.", e);
				throw convertEdgeServiceFault2AxisFault(e);
			}

			NodeConnectionInfo nodeInfo = convertRegistrationNodeResultToNodeConnectionInfo(registrationNodeResult);
			return nodeInfo;
		}

	}

	private NodeConnectionInfo convertRegistrationNodeResultToNodeConnectionInfo(
			RegistrationNodeResult registrationNodeResult) {
		NodeConnectionInfo nodeInfo = new NodeConnectionInfo();

		if (registrationNodeResult.getErrorCodes()[0] == null) {
			nodeInfo.setErrCode1(0);
		} else {
			nodeInfo.setErrCode1(Integer.parseInt(registrationNodeResult
					.getErrorCodes()[0]));
		}
		if (registrationNodeResult.getErrorCodes()[0] != null
				&& registrationNodeResult.getErrorCodes()[0].equals("20")) {
			if (registrationNodeResult.getErrorCodes()[1] != null) {
				nodeInfo.setErrCode2(Integer.parseInt(registrationNodeResult
						.getErrorCodes()[1]));
			}
			// nodeInfo.setErrCode2(Integer.parseInt(registrationNodeResult.getErrorCodes()[1]));
		}
		return nodeInfo;
	}

	protected SOAPFaultException convertEdgeServiceFault2AxisFault(
			EdgeServiceFault serviceException) {
		String err = MessageReader.getErrorMessage(serviceException
				.getFaultInfo().getCode(), Locale.getDefault());
		// logger.info(err, serviceException);
		logger.error(serviceException.getFaultInfo().getCode());
		logger.error(serviceException.getMessage(), serviceException);
		return AxisFault.fromAxisFault(err, serviceException.getFaultInfo()
				.getCode());
	}

	private NodeRegistrationInfo convertToNodeRegistrationInfo(
			TargetMachineInfo targetNode) {
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
		nodeRegistrationInfo.setNodeName(targetNode.getName());
		nodeRegistrationInfo.setUsername(targetNode.getUser());
		nodeRegistrationInfo.setPassword(targetNode.getPassword());
		nodeRegistrationInfo.setNodeDescription(targetNode.getDescription());

		return nodeRegistrationInfo;

	}

	@Override
	public List<TargetMachineInfo> getNodeList() {
		NodeServiceImpl nodeService = new NodeServiceImpl();

		NodePagingConfig npc = new NodePagingConfig();
		npc.setPagesize(Integer.MAX_VALUE);
		npc.setOrderType(EdgeSortOrder.DESC);
		npc.setOrderCol(NodeSortCol.hostname);

		NodePagingResult nodePagingResult = null;
		EdgeNodeFilter enf = new EdgeNodeFilter();

		try {
			nodePagingResult = nodeService.getNodesESXByGroupAndTypePaging(-32,
					-9, enf, npc);
		} catch (EdgeServiceFault e) {
			logger.error("failed to get node list.", e);
			throw convertEdgeServiceFault2AxisFault(e);
		}

		return convertNodePagingResultToTargetMachineInfo(nodePagingResult);
	}

	private List<TargetMachineInfo> convertNodePagingResultToTargetMachineInfo(
			NodePagingResult nodePagingResult) {
		List<TargetMachineInfo> targetMachineInfoList = new ArrayList<TargetMachineInfo>();
		List<Node> nodeList = nodePagingResult.getData();
		for (int i = 0; i < nodeList.size(); i++) {
			TargetMachineInfo targetMachineInfo = new TargetMachineInfo();
			targetMachineInfo.setName(nodeList.get(i).getHostname());
			targetMachineInfo.setUser(nodeList.get(i).getUsername());
			targetMachineInfo.setJobName(nodeList.get(i).getPolicyName());
			;
			targetMachineInfo.setOperatingSystem(nodeList.get(i)
					.getOsDescription());
			targetMachineInfo.setDescription(nodeList.get(i)
					.getNodeDescription());

			targetMachineInfoList.add(targetMachineInfo);
		}
		return targetMachineInfoList;
	}

	@Override
	public void redeployLinuxPlan(String planName, List<String> nodeList) {
		List<Integer> policyIdList = new ArrayList<Integer>();
		int id = 0;
		try {
			id = policyService.getPolicyIdByName(planName);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to get plan id by plan name:" + planName, e);
		}
		if (id != 0) {
			policyIdList.add(id);
			if (nodeList == null || nodeList.size() == 0) {
				try {
					PlanRedeployService.getInstance().redeploy(policyIdList);
				} catch (EdgeServiceFault e) {
					logger.error("Failed to redeploy Linux plan", e);
				}
			} else {
				List<ProtectedResourceIdentifier> nList = new ArrayList<ProtectedResourceIdentifier>();
				for (String nodename : nodeList) {
					int[] nodeid = new int[1];
					hostMgrDao.as_edge_host_getIdByHostnameForLinux(nodename,
							nodename, 1, nodeid);
					if (nodeid[0] == 0) {
						continue;
					}
					ProtectedResourceIdentifier p = new ProtectedResourceIdentifier();
					p.setId(nodeid[0]);
					p.setType(ProtectedResourceType.node);
					nList.add(p);
				}
				if (nList.size() > 0) {
					try {
						UnifiedPolicy policy = policyService
								.loadUnifiedPolicyById(id);
						policy.setProtectedResources(nList);
						policyService.updateUnifiedPolicy(policy);
					} catch (EdgeServiceFault e) {
						logger.error("Failed to get policy.", e);
					}
				}

			}
		}

	}

	@Override
	public void redeployFailedLinuxPlan(int planId, List<String> nodeList) {
		List<Integer> policyIdList = new ArrayList<Integer>();
		
		if (planId != 0) {
			policyIdList.add(planId);
			if (nodeList == null || nodeList.size() == 0) {
				try {
					PlanRedeployService.getInstance().redeploy(policyIdList);
				} catch (EdgeServiceFault e) {
					logger.error("Failed to redeployFailedLinuxPlan Linux plan", e);
				}
			} else {
				List<ProtectedResourceIdentifier> nList = new ArrayList<ProtectedResourceIdentifier>();
				for (String nodename : nodeList) {
					int[] nodeid = new int[1];
					hostMgrDao.as_edge_host_getIdByHostnameForLinux(nodename,
							nodename, 1, nodeid);
					if (nodeid[0] == 0) {
						continue;
					}
					ProtectedResourceIdentifier p = new ProtectedResourceIdentifier();
					p.setId(nodeid[0]);
					p.setType(ProtectedResourceType.node);
					nList.add(p);
				}
				if (nList.size() > 0) {
					try {
						UnifiedPolicy policy = policyService
								.loadUnifiedPolicyById(planId);
						policy.setProtectedResources(nList);
						policyService.updateUnifiedPolicy(policy);
					} catch (EdgeServiceFault e) {
						logger.error("redeployFailedLinuxPlan : Failed to get policy.", e);
					}
				}

			}
		}	
	}
}
