package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcserve.edge.app.base.appdaos.AuthUuidWrapper;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.BackupScript;
import com.ca.arcserve.linuximaging.webservice.data.D2DTime;
import com.ca.arcserve.linuximaging.webservice.data.Retention;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupTarget;
import com.ca.arcserve.linuximaging.webservice.data.backup.CopyRecoveryPointSettings;
import com.ca.arcserve.linuximaging.webservice.data.backup.DataStoreInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.EveryDaySchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.EveryMonthSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.EveryWeekSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.PeriodSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.ThrottleSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.WeeklySchedule;

public class LinuxBackupTaskDeployment extends TaskDeploymentExceptionHandler implements ITaskDeployment {

	private static Logger logger = Logger
			.getLogger(LinuxBackupTaskDeployment.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory
			.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectionInfoDao = DaoFactory
			.getDao(IEdgeConnectInfoDao.class);
	private static PolicyDeploymentLogWriter activityLogWriter = PolicyDeploymentLogWriter
			.getInstance();
	private static PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
	private long edgeTaskId = PolicyManagementServiceImpl.getTaskId();
	private PolicyManagementServiceImpl policyImpl = new PolicyManagementServiceImpl();
	private IConnectionFactory connectionFactory = EdgeFactory
			.getBean(IConnectionFactory.class);

	@Override
	public boolean deployTask(PolicyDeploymentTask argument,
			UnifiedPolicy plan, boolean updateDeployStatusOnSuccess) {
		int d2dServerId = plan.getLinuxBackupsetting().getLinuxD2DServerId();
		EdgeConnectInfo d2dServer = getD2DServerConnectionInfo(d2dServerId);
		@SuppressWarnings("unchecked")
		List<Integer> idList = (List<Integer>) argument.getTaskParameters();
		List<EdgeConnectInfo> nodeList = getNodeConnectionInfoList(idList);
		PolicyDeploymentError error = null;
		int ret = -1;
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServerId)) {
			connection.connect();
			ILinuximagingService service = connection.getService();
			ret = service.validateByKey(d2dServer.getAuthUuid());
			if (ret == 0) {
				int deployReason = argument.getDeployReason();
				if (deployReason == PolicyDeployReasons.EnablePlan
						|| deployReason == PolicyDeployReasons.DisablePlan) {
					boolean isEnable = deployReason == PolicyDeployReasons.EnablePlan ? true
							: false;
					ret = service.enablePlan(plan.getId(), null, isEnable);
				} else {
					BackupScript bs = getBackupScript(plan, null,
							d2dServer.getRhostname());
					bs.setTemplateID(plan.getUuid());
					bs.setId(argument.getPolicyId());
					List<BackupTarget> targetList = getBackupTargetList(
							nodeList, plan);
					bs.setSettings(targetList);
					ret = service.savePlan(bs);
				}
			} else {
				handleGeneralException(argument,
						"policyDeployment_LinuxD2DServerIsNotManagedByEdge",
						null,
						"deployPolicy(): Invalid credential for the node.",
						idList, d2dServer.getRhostname());
				ret = -2;
			}
		} catch (SOAPFaultException e) {
			logger.error("SOAPFaultException", e);
			String errorCode = e.getFault().getFaultCodeAsQName()
					.getLocalPart();
			error = new PolicyDeploymentError();
			error.setErrorCode(errorCode);
			error.setErrorType(PolicyDeploymentError.ErrorTypes.Error);
			error.setHostType(HostTypeUtil.setLinuxNode(0));
			// error.setNodeName(d2dServer.getRhostname());
			error.setSettingsType(ID2DPolicyManagementService.SettingsTypes.BackupSettings);
			error.setPolicyType(ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving);
			ret = -1;
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			handleGeneralException(argument,
					"policyDeployment_FailedToConnectToTheNode", e,
					"deployPolicy(): Failed to connect to the node.", idList);
			ret = -3;
		} catch (EdgeServiceFault e) {
			//logger.error(e);
			super.handleException("deployLinuxTask",argument, e, d2dServerId, d2dServer.getRhostname());
			return false;
		}
		try {
			List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
			for (EdgeConnectInfo node : nodeList) {
				argument.setHostId(node.getHostid());
				if (ret != 0 || updateDeployStatusOnSuccess) {
					DeployUtil.setDeployStatus(argument, ret == 0 ? true
							: false);
				}
				if (error != null) {
					error.setNodeName(node.getRhostname());
					errorList = new ArrayList<PolicyDeploymentError>();
					errorList.add(error);
				}

				activityLogWriter.addDeploymentLogs4LinuxD2D(edgeTaskId,
						argument, errorList, d2dServer.getRhostname());

				if (ret == 0) {
					if (updateDeployStatusOnSuccess) {
						activityLogWriter.addDeploymentSucceedLog(edgeTaskId,
								argument, node.getRhostname());
					}
				} else {
					activityLogWriter.addDeploymentFailedLog(edgeTaskId,
							argument, node.getRhostname());
				}
				if (ret == -1) {
					warningErrorMessageWriter
							.addWarningErrorMessageFromLinuxD2D(argument,
									errorList, false, d2dServer.getRhostname());
				}
			}

			// warningErrorMessageWriter.addWarningErrorMessageFromD2D(argument,
			// errorList, false);
		} catch (DeploymentException e) {
			logger.error(e.getMessage(), e);
		}
		return ret == 0;
	}

	private void handleGeneralException(PolicyDeploymentTask argument,
			String errorKey, Throwable deployException, String innerlogMsg,
			List<Integer> nodeList, Object... appendParam) {
		try {
			for (Integer nodeId : nodeList) {
				argument.setHostId(nodeId);
				String uiErrorMsg = EdgeCMWebServiceMessages.getMessage(
						errorKey, appendParam);
				logger.error(innerlogMsg, deployException);
				activityLogWriter.addDeploymentFailedLog(edgeTaskId, argument,
						null, uiErrorMsg);
				warningErrorMessageWriter.addErrorMessage(argument, null,
						uiErrorMsg);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private BackupScript getBackupScript(UnifiedPolicy plan,
			List<EdgeConnectInfo> nodeList,
			String d2dServerName) {
		BackupConfiguration bc = plan.getBackupConfiguration();
		BackupScript bs = new BackupScript();
		LinuxBackupSettings lbs = plan.getLinuxBackupsetting();
		bs.setJobName(plan.getName());
		bs.setDisable(!plan.isEnable());
		BackupLocationInfo locationInfo = new BackupLocationInfo();
		if (bc.isD2dOrRPSDestType()) {
			locationInfo.setBackupDestLocation(lbs.getBackupLocationInfo()
					.getBackupDestLocation());
			locationInfo.setBackupDestUser(lbs.getBackupLocationInfo()
					.getBackupDestUser());
			locationInfo.setBackupDestPasswd(lbs.getBackupLocationInfo()
					.getBackupDestPasswd());
			locationInfo.setType(lbs.getBackupLocationInfo().getType());
		} else {
			BackupRPSDestSetting rpsDestSetting = bc.getBackupRpsDestSetting();
			ServerInfo rpsServerInfo = new ServerInfo();
			rpsServerInfo.setName(rpsDestSetting.getRpsHost().getRhostname());
			rpsServerInfo.setUser(rpsDestSetting.getRpsHost().getUsername());
			rpsServerInfo
					.setPassword(rpsDestSetting.getRpsHost().getPassword());
			rpsServerInfo.setPort(rpsDestSetting.getRpsHost().getPort());
			rpsServerInfo.setUuid(rpsDestSetting.getRpsHost().getUuid());
			List<AuthUuidWrapper> wrappers = new ArrayList<AuthUuidWrapper>();
			IRpsConnectionInfoDao rpsConnectionInfoDao = DaoFactory
					.getDao(IRpsConnectionInfoDao.class);
			rpsConnectionInfoDao.as_edge_rps_connection_info_getAuthUuid(
					rpsDestSetting.getRpsHost().getUuid(), wrappers);
			if (!wrappers.isEmpty()) {
				rpsServerInfo.setAuthKey(wrappers.get(0).getAuthUuid());
			}

			rpsServerInfo.setProtocol(rpsDestSetting.getRpsHost()
					.isHttpProtocol() ? "http" : "https");
			locationInfo.setServerInfo(rpsServerInfo);

			DataStoreInfo dataStoreInfo = new DataStoreInfo();
			dataStoreInfo.setName(rpsDestSetting.getRPSDataStoreDisplayName());
			dataStoreInfo.setUuid(rpsDestSetting.getRPSDataStore());
			locationInfo.setDataStoreInfo(dataStoreInfo);
			locationInfo
					.setType(BackupLocationInfo.BACKLOCATION_TYPE_RPS_SERVER);
			bs.setRpsPolicyUUID(rpsDestSetting.getRPSPolicyUUID());
			bs.setBackupToRps(true);
		}
		bs.setBackupLocationInfo(locationInfo);
		bs.setExclude(lbs.isExclude());
		bs.setExcludeVolumes(lbs.getExcludeVolumes());
		bs.setExcludeFiles(lbs.getExcludeFiles());
		bs.setServerScriptBeforeJob(lbs.getServerScriptBeforeJob());
		bs.setServerScriptAfterJob(lbs.getServerScriptAfterJob());
		bs.setTargetScriptAfterJob(lbs.getTargetScriptAfterJob());
		bs.setTargetScriptAfterSnapshot(lbs.getTargetScriptAfterSnapshot());
		bs.setTargetScriptBeforeJob(lbs.getTargetScriptBeforeJob());
		bs.setTargetScriptBeforeSnapshot(lbs.getTargetScriptBeforeSnapshot());

		bs.setCompressLevel(getCompressionLevel(bc.getCompressionLevel()));
		bs.setEncryptAlgoName(getEncryptAlgoName(bc.getEncryptionAlgorithm()));
		bs.setEncryptPasswd(bc.getEncryptionKey());

		bs.setEncryptAlgoType(bc.getEncryptionAlgorithm());
		bs.setThrottle(bc.getThrottling());
		Retention retention = new Retention();
		retention
				.setBackupSetCount(bc.getRetentionPolicy().getBackupSetCount());
		retention.setUseWeekly(bc.getRetentionPolicy().isUseWeekly());
		retention.setDayOfMonth(bc.getRetentionPolicy().getDayOfMonth());
		retention.setDayOfWeek(bc.getRetentionPolicy().getDayOfWeek());
		bs.setRetention(retention);
		bs.setScheduleType(5);
		WeeklySchedule ws = new WeeklySchedule();
		ws.setStartTime(converToD2DTime(bc.getAdvanceSchedule().getScheduleStartTime()));
		List<BackupSchedule> scheduleList = new ArrayList<BackupSchedule>();
		List<ThrottleSchedule> throttleScheduleList = new ArrayList<ThrottleSchedule>();
		for (DailyScheduleDetailItem schedule : bc.getAdvanceSchedule()
				.getDailyScheduleDetailItems()) {
			for (ScheduleDetailItem item : schedule.getScheduleDetailItems()) {
				BackupSchedule bSchedule = new BackupSchedule();
				bSchedule.setDay(schedule.getDayofWeek());
				bSchedule.setJobType(getBackupType(item.getJobType()));
				bSchedule.setStartTime(convertToD2DTime(item.getStartTime()));
				bSchedule.setEnabled(item.isRepeatEnabled());
				if(item.isRepeatEnabled()){
					bSchedule.setInterval(item.getInterval());
					bSchedule.setIntervalUnit(item.getIntervalUnit());
					bSchedule.setEndTime(convertToD2DTime(item.getEndTime()));
				}
				scheduleList.add(bSchedule);
			}
			for (ThrottleItem item : schedule.getThrottleItems()) {
				ThrottleSchedule throttleSchedule = new ThrottleSchedule();
				throttleSchedule.setDay(schedule.getDayofWeek());
				throttleSchedule.setStartTime(convertToD2DTime(item
						.getStartTime()));
				throttleSchedule
						.setEndTime(convertToD2DTime(item.getEndTime()));
				throttleSchedule.setThrottleValue(item.getThrottleValue());
				throttleSchedule.setUnit(item.getUnit());
				throttleScheduleList.add(throttleSchedule);
			}

		}
		ws.setScheduleList(scheduleList);
		ws.setThrottleScheduleList(throttleScheduleList);
		PeriodSchedule periodSchedule = new PeriodSchedule();
		if (bc.getAdvanceSchedule().isPeriodEnabled()) {
			if (bc.getAdvanceSchedule().getPeriodSchedule() != null) {
				periodSchedule.setEnabled(bc.getAdvanceSchedule()
						.getPeriodSchedule().isEnabled());
				EveryDaySchedule daySchedule = new EveryDaySchedule();
				daySchedule.setEnabled(bc.getAdvanceSchedule()
						.getPeriodSchedule().getDaySchedule().isEnabled());
				if (daySchedule.isEnabled()) {
					daySchedule.setBkpType(getBackupType(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getDaySchedule().getBkpType()));
					daySchedule.setDayEnabled(bc.getAdvanceSchedule()
							.getPeriodSchedule().getDaySchedule()
							.getDayEnabled());
					daySchedule.setDayTime(convertToDayTime(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getDaySchedule().getDayTime()));
					daySchedule.setRetentionCount(bc.getAdvanceSchedule()
							.getPeriodSchedule().getDaySchedule()
							.getRetentionCount());
					daySchedule.setDayEnabled(bc.getAdvanceSchedule()
							.getPeriodSchedule().getDaySchedule()
							.getDayEnabled());
				}
				periodSchedule.setDaySchedule(daySchedule);

				EveryWeekSchedule weekSchedule = new EveryWeekSchedule();
				weekSchedule.setEnabled(bc.getAdvanceSchedule()
						.getPeriodSchedule().getWeekSchedule().isEnabled());
				if (weekSchedule.isEnabled()) {
					weekSchedule.setBkpType(getBackupType(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getWeekSchedule().getBkpType()));
					weekSchedule.setDayOfWeek(bc.getAdvanceSchedule()
							.getPeriodSchedule().getWeekSchedule()
							.getDayOfWeek());
					weekSchedule.setDayTime(convertToDayTime(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getWeekSchedule().getDayTime()));
					weekSchedule.setRetentionCount(bc.getAdvanceSchedule()
							.getPeriodSchedule().getWeekSchedule()
							.getRetentionCount());
				}
				periodSchedule.setWeekSchedule(weekSchedule);

				EveryMonthSchedule monthSchedule = new EveryMonthSchedule();
				monthSchedule.setEnabled(bc.getAdvanceSchedule()
						.getPeriodSchedule().getMonthSchedule().isEnabled());
				if (monthSchedule.isEnabled()) {
					monthSchedule.setBkpType(getBackupType(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getMonthSchedule().getBkpType()));
					monthSchedule.setDayOfMonth(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.getDayOfMonth());
					monthSchedule.setDayOfMonthEnabled(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.isDayOfMonthEnabled());
					monthSchedule.setDayTime(convertToDayTime(bc
							.getAdvanceSchedule().getPeriodSchedule()
							.getMonthSchedule().getDayTime()));
					monthSchedule.setRetentionCount(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.getRetentionCount());
					monthSchedule.setWeekDayOfMonth(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.getWeekDayOfMonth());
					monthSchedule.setWeekNumOfMonth(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.getWeekNumOfMonth());
					monthSchedule.setWeekOfMonthEnabled(bc.getAdvanceSchedule()
							.getPeriodSchedule().getMonthSchedule()
							.isWeekOfMonthEnabled());
				}
				periodSchedule.setMonthSchedule(monthSchedule);
			}
		}
		ws.setPeriodSchedule(periodSchedule);
		bs.setWeeklySchedule(ws);
		if (plan.getExportConfiguration() != null) {
			ScheduledExportConfiguration seConfig = plan
					.getExportConfiguration();
			CopyRecoveryPointSettings cpSettings = new CopyRecoveryPointSettings();
			cpSettings.setEnableCopyRecoveryPoint(seConfig
					.isEnableScheduledExport());
			cpSettings.setCompressionLevel(getCompressionLevel(seConfig
					.getCompressionLevel()));
			cpSettings.setEncryptionAlgorithm(getEncryptAlgoName(seConfig
					.getEncryptionAlgorithm()));
			cpSettings.setEncryptionKey(seConfig.getEncryptionKey());
			cpSettings.setExportInterval(seConfig.getExportInterval());
			cpSettings.setKeepRecoveryPoints(seConfig.getKeepRecoveryPoints());
			BackupLocationInfo cpLocationInfo = new BackupLocationInfo();
			cpLocationInfo.setBackupDestLocation(seConfig.getDestination());
			cpLocationInfo.setBackupDestUser(seConfig.getDestUserName());
			cpLocationInfo.setBackupDestPasswd(seConfig.getDestPassword());
			cpSettings.setDestination(cpLocationInfo);
			bs.setCopyRecoveryPointSettings(cpSettings);

		}
		bs.setServerName(d2dServerName);
		/*
		 * List<BackupTarget> btList = new ArrayList<BackupTarget>();
		 * for(EdgeConnectInfo node : nodeList){ BackupTarget bt = new
		 * BackupTarget(); bt.setName(node.getRhostname());
		 * bt.setUser(node.getUsername()); bt.setPassword(node.getPassword());
		 * bt.setOperatingSystem(node.getOsName());
		 * bt.setJobName(plan.getName()); btList.add(bt); }
		 * bs.setSettings(btList);
		 */
		return bs;
	}

	private List<BackupTarget> getBackupTargetList(
			List<EdgeConnectInfo> nodeList, UnifiedPolicy plan) {
		List<BackupTarget> btList = new ArrayList<BackupTarget>();
		for (EdgeConnectInfo node : nodeList) {
			BackupTarget bt = new BackupTarget();
			bt.setName(node.getRhostname());
			bt.setUser(node.getUsername());
			bt.setPassword(node.getPassword());
			bt.setOperatingSystem(node.getOsName());
			bt.setJobName(plan.getName());
			bt.setUUID(node.getUuid());
			btList.add(bt);
		}
		return btList;
	}

	private int getCompressionLevel(int level) {
		if (level == 9) {
			return 2;
		}
		return level;
	}

	private String getEncryptAlgoName(int encryptAlgorithm) {
		String algoName = "";
		switch (encryptAlgorithm) {
		case 0:
			algoName = "None";
			break;
		case 65537:
			algoName = "AES128";
			break;
		case 65538:
			algoName = "AES192";
			break;
		case 65539:
			algoName = "AES256";
			break;
		default:
			algoName = "None";
			break;
		}
		return algoName;
	}

	private D2DTime convertToD2DTime(DayTime time) {
		D2DTime d2dTime = new D2DTime();
		d2dTime.setHourOfday(time.getHour());
		if (time.getHour() > 12) {
			d2dTime.setHour(time.getHour() - 12);
			d2dTime.setAmPM(1);
		} else {
			d2dTime.setHour(time.getHour());
			d2dTime.setAmPM(0);
		}
		d2dTime.setMinute(time.getMinute());
		return d2dTime;
	}

	private D2DTime converToD2DTime(long date){
		Date dt = new Date(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		D2DTime time = new D2DTime();
		time.setYear(calendar.get(Calendar.YEAR));
		time.setMonth(calendar.get(Calendar.MONTH));
		time.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		time.setHour(calendar.get(Calendar.HOUR));
		time.setMinute(calendar.get(Calendar.MINUTE));
		time.setHourOfday(calendar.get(Calendar.HOUR_OF_DAY));
		time.setAmPM(-1);
		return time;
	}
	
	
	private com.ca.arcserve.linuximaging.webservice.data.backup.DayTime convertToDayTime(
			DayTime time) {
		com.ca.arcserve.linuximaging.webservice.data.backup.DayTime dt = new com.ca.arcserve.linuximaging.webservice.data.backup.DayTime();
		dt.setHour(time.getHour());
		dt.setMinute(time.getMinute());
		return dt;
	}

	private int getBackupType(int jobType) {
		int newType = 3;
		switch (jobType) {
		case 0:
			newType = 3;
			break;
		case 1:
			newType = 4;
			break;
		case 2:
			newType = 5;
			break;
		}
		return newType;
	}

	private EdgeConnectInfo getD2DServerConnectionInfo(int d2dServerId) {
		List<EdgeHost> d2dServerList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(d2dServerId, 1, d2dServerList);

		EdgeHost d2dServer = d2dServerList.get(0);
		if (d2dServer == null) {
			return null;
		}

		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(d2dServerId, connInfoLst);

		EdgeConnectInfo connectionInfo = connInfoLst.get(0);

		if (connectionInfo == null) {
			return null;
		}

		connectionInfo.setRhostname(d2dServer.getRhostname());
		return connectionInfo;
	}

	private List<EdgeConnectInfo> getNodeConnectionInfoList(List<Integer> idList) {
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_linux_node_by_ids(getIdString(idList),
				connInfoLst);
		return connInfoLst;

	}

	private String getIdString(List<Integer> idList) {
		if (idList.size() > 0) {
			StringBuffer idString = new StringBuffer();
			for (Integer nodeId : idList) {
				idString.append(nodeId + ",");
			}
			return "(" + idString.substring(0, idString.length() - 1) + ")";
		} else {
			return "";
		}
	}

	@Override
	public boolean removeTask(PolicyDeploymentTask argument,
			boolean updateDeployStatusOnSuccess) {
		PolicyDeploymentError error = null;
		int ret = -1;
		UnifiedPolicy plan = null;
		try {
			plan = policyImpl.getUnifiedPolicyById(argument
					.getPolicyId());
		} catch (EdgeServiceFault e1) {
			logger.error("[LinuxBackupTaskDeployment] remove linux backup task failed.",e1);
			return false;
		}
		int d2dServerId = plan.getLinuxBackupsetting()
				.getLinuxD2DServerId();
		EdgeConnectInfo d2dServer = getD2DServerConnectionInfo(d2dServerId);
		@SuppressWarnings("unchecked")
		List<Integer> idList = (List<Integer>) argument.getTaskParameters();
		try (LinuxD2DConnection connection = connectionFactory
				.createLinuxD2DConnection(d2dServerId)) {
			connection.connect();
			ILinuximagingService service = connection.getService();
			ret = service.validateByKey(d2dServer.getAuthUuid());
			if (ret == 0) {
				if ((argument.getDeployFlags() & PolicyDeployFlags.BackupTaskDeleted) != 0
						|| (argument.getDeployFlags() & PolicyDeployFlags.UnregisterNodeAfterUnassign) != 0) {
					String idString = getIdString(idList);
					List<EdgeHost> edgeHostList = new ArrayList<EdgeHost>();
					hostMgrDao.as_edge_hosts_list(idString, edgeHostList);
					String[] nodes = new String[edgeHostList.size()];
					for (int i = 0; i < edgeHostList.size(); i++) {
						nodes[i] = edgeHostList.get(i).getRhostname();
					}
					ret = service.deleteJobForNodes(argument.getPolicyId(),
							nodes);
				} else {
					ret = service.deletePlan(argument.getPolicyId());
				}
			} else {
				handleGeneralException(argument,
						"policyDeployment_LinuxD2DServerIsNotManagedByEdge",
						null,
						"deployPolicy(): Invalid credential for the node.",
						idList, d2dServer.getRhostname());
				ret = -2;
			}
		} catch (SOAPFaultException e) {
			logger.error("SOAPFaultException", e);
			String errorCode = e.getFault().getFaultCodeAsQName()
					.getLocalPart();
			error = new PolicyDeploymentError();
			error.setErrorCode(errorCode);
			error.setErrorType(PolicyDeploymentError.ErrorTypes.Error);
			error.setHostType(HostTypeUtil.setLinuxNode(0));
			// error.setNodeName(d2dServer.getRhostname());
			error.setSettingsType(ID2DPolicyManagementService.SettingsTypes.BackupSettings);
			error.setPolicyType(ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving);
			ret = -1;
		} catch (WebServiceException e) {
			logger.error("cannot connect to Linux D2D service", e);
			handleGeneralException(argument,
					"policyDeployment_FailedToConnectToTheNode", e,
					"deployPolicy(): Failed to connect to the node.", idList);
			ret = -1;
		} catch (EdgeServiceFault e) {
			logger.error(e);
			super.handleException("removeLinuxTask", argument, e, d2dServerId,d2dServer.getRhostname());
			return false;
		}

		try {
			DeployUtil.setDeployStatus(argument, ret == 0);
		} catch (DeploymentException e) {
			logger.error(e.getMessage(), e);
		}
		return ret == 0;
	}

	@Override
	void updateDeployStatus(PolicyDeploymentTask argument,
			int policyDeployStatus) {
		@SuppressWarnings("unchecked")
		List<Integer> linuxNodeList = (List<Integer>) argument.getTaskParameters();
		for (Integer linuxNodeId : linuxNodeList) {
			argument.setHostId(linuxNodeId);
			try {
				DeployUtil.setDeployStatus( argument, false, policyDeployStatus, false );
			} catch (Exception e) {
				// donothing
				//set deploy status, ignore exception
			}
		}
	}

	@Override
	void writeActivityLogAndDeployMessage(PolicyDeploymentTask argument,
			String message, String nodeName) {
		activityLogWriter.addDeploymentFailedLog4VM(edgeTaskId,
				argument, nodeName);
		warningErrorMessageWriter.addErrorMessage4VM(argument, nodeName, message);
	}

	@Override
	String getMessageSubject() {
		return EdgeCMWebServiceMessages.getResource("linuxD2dServer");
	}

}
