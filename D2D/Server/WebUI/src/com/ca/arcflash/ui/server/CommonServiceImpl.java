package com.ca.arcflash.ui.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLException;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.JobMonitorConstants;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.serviceinfo.ServiceInfo;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.serviceinfo.ServiceInfoList;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.homepage.SummaryPanel;
import com.ca.arcflash.ui.client.model.ArchiveDiskDestInfoModel;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DeployUpgradeInfoModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.EncryptedRecoveryPointModel;
import com.ca.arcflash.ui.client.model.ExternalLinksModel;
import com.ca.arcflash.ui.client.model.JobMonitorHistoryItemModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.ca.arcflash.ui.client.model.LogEntryType;
import com.ca.arcflash.ui.client.model.MountSessionModel;
import com.ca.arcflash.ui.client.model.NetworkPathModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.ServerInfoModel;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionAlgModel;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionLibModel;
import com.ca.arcflash.ui.server.servlet.ContextListener;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.IFlashService_Oolong1;
import com.ca.arcflash.webservice.IFlashService_R16_5;
import com.ca.arcflash.webservice.IFlashService_R16_U4;
import com.ca.arcflash.webservice.IFlashService_R16_U6;
import com.ca.arcflash.webservice.IFlashService_R16_U7;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.JobMonitorHistoryItem;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.activitylog.ActivityLog;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogType;
import com.ca.arcflash.webservice.data.archive.ArchiveDiskDestInfo;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTargetDetail;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class CommonServiceImpl extends BaseServiceImpl implements CommonService {

	/**
	 *
	 */
	private static final long serialVersionUID = 1431123255159980821L;
	private static final Logger logger = Logger.getLogger(CommonServiceImpl.class);
	
	private static ExternalLinksModel enExternalLinksModel;
	private static ExternalLinksModel frExternalLinksModel;
	private static ExternalLinksModel deExternalLinksModel;
	private static ExternalLinksModel jaExternalLinksModel;
	private static ExternalLinksModel esExternalLinksModel;
	private static ExternalLinksModel ptExternalLinksModel;
	private static ExternalLinksModel itExternalLinksModel;
	private static ExternalLinksModel zhExternalLinksModel;

	public static final int BACKUP_MODE = 0;
	public static final int RESTORE_MODE = 1;
	public static final int COPY_MODE = 2;
	public static final int RESTORE_ALT_MODE = 3;
	public static final int ARCHIVE_MODE = 5;
	public static final int ARCHIVE_RESTORE_MODE = 7;
	public static final int MOUNT_VOLUME_MODEL = 8;
	public static final int ARCHIVE_DEST_MODE = 9;
	public static final int DIAGNOSTIC_MODE = 11;
	
	
	
	@Override
	public PagingLoadResult<LogEntry> getActivityLogs(PagingLoadConfig config)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult = getServiceClient().getService()
					.getActivityLogs(start, count);
			int total = (int) activityLogResult.getTotalCount();
			List<LogEntry> resultList = new ArrayList<LogEntry>();

			if (activityLogResult.getLogs() != null) {
				for (ActivityLog log : activityLogResult.getLogs()) {
//					int type = LogEntryType.Information;
//					if (log.getType() == ActivityLogType.Information)
//						type = LogEntryType.Information;
//					else if (log.getType() == ActivityLogType.Warning)
//						type = LogEntryType.Warning;
//					else
//						type = LogEntryType.Error;

					LogEntry entry = new LogEntry(log.getType(), log.getTime(),log.getD2dName(), 
							log.getMessage(), log.getJobID());
					entry.setTimeZoneOffset(log.getTimeZoneOffset());
					resultList.add(entry);
				}
			}

			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public void deleteActivityLog(Date date) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getService().deleteActivityLogs(date);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}

	public void setDeploymentServers(List<ServerInfoModel> listServerInfo)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			List<RemoteDeployTarget> list = ConverRemoteDeployTarget(listServerInfo);
			if (list != null) {
				RemoteDeployTarget[] remoteDeployTargets = list
						.toArray(new RemoteDeployTarget[0]);
				getServiceClient().getService().setRemoteDeployTargets(remoteDeployTargets);
			}

		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}

	public void startDeploymentServers(List<String> listServers)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			if (listServers != null) {
				String[] serverNames = listServers.toArray(new String[0]);
				String localDomain = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_DOMAIN);
				String localUser = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_USERNAME);
				String localPassword = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_PASSWORD);
				getServiceClient().getService().startRemoteDeploy(localDomain, localUser,
						localPassword, serverNames);
			}

		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}

	private List<RemoteDeployTarget> ConverRemoteDeployTarget(
			List<ServerInfoModel> listServerInfo) {
		List<RemoteDeployTarget> list = new ArrayList<RemoteDeployTarget>();
		if (listServerInfo != null) {
			for (ServerInfoModel si : listServerInfo) {
				RemoteDeployTarget rdt = new RemoteDeployTarget();
				rdt.setInstallDirectory(si.getInstallPath());
				rdt.setServerName(si.getServerName());
				rdt.setAutoStartRRService(si.isAutoStartRemoteRegService());
				rdt.setReboot(si.isReboot());
				rdt.setPassword(si.getPassword());
				rdt.setUsername(si.getUserName());
				rdt.setPort(si.getPort());
				rdt.setIntallDriver(si.isInstallDriver());
				rdt.setUseHttps(si.isUseHttps());
				list.add(rdt);
			}
		}
		return list;
	}

	public List<ServerInfoModel> getDeploymentServers()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			RemoteDeployTargetDetail[] rdt = getServiceClient().getService()
					.getRemoteDeployTargets(null);
			return ConvertServerInfoModel(rdt);

		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		return null;
	}

	private List<ServerInfoModel> ConvertServerInfoModel(
			RemoteDeployTargetDetail[] rdDetails) {
		List<ServerInfoModel> list = new ArrayList<ServerInfoModel>();
		if (rdDetails != null) {
			for (RemoteDeployTargetDetail detail : rdDetails) {
				ServerInfoModel si = new ServerInfoModel();
				si.setDeployPercentage(detail.getPercentage());
				si.setDeployStatusCode(detail.getStatus());
				long msgCode = detail.getMsgCode();
				if (msgCode != 0) {
					String localizedMsg = getADTMsg(msgCode, detail
							.getServerName());
					si.setDeployMessage(localizedMsg);
				}
				si.setInstallPath(detail.getInstallDirectory());
				si.setServerName(detail.getServerName());
				si.setReboot(detail.isReboot());
				si.setAutoStartRemoteRegService(detail.isAutoStartRRService());
				si.setPassword(detail.getPassword());
				si.setUserName(detail.getUsername());
				si.setPort(detail.getPort());
				si.setSelected(detail.isSelected());
				si.setInstallDriver(detail.isInstallDriver());
				si.setUseHttps(detail.isUseHttps());
				list.add(si);
			}
		}
		return list;
	}

	private String getADTMsg(long msgCode, String serverName) {
		String codeId = String
				.valueOf(FlashServiceErrorCode.ADT_BASE + msgCode);
		String msg = ResourcesReader.getResource("ServiceError_" + codeId,
				getServerLocale());

		msg = MessageFormatEx.format(msg, serverName, getProductName());

		return msg;
	}

	@Override
	public void backup(int backupType, String name)
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException {
		try {
			this.getServiceClient().getService().backup(backupType, name);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
	}

	@Override
	public boolean isOnlyFullBackup() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			return getServiceClient().getFlashService(IFlashService_R16_5.class).onlyFullManualBackup();
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return false;
	}

	@Override
	public boolean isBackupCompressionLevelChangedWithLevel(int compressionLevel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			return this.getServiceClient().getService().isBackupCompressionLevelChangedWithLevel(compressionLevel);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return false;
	}

	/*@Override
	public JobMonitorModel getJobMonitor() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException,
			SessionTimeoutException {
		if (this.getThreadLocalRequest().getSession(true).getAttribute(
				SessionConstants.SRING_USERNAME) == null)
			throw new SessionTimeoutException();

		try {
			JobMonitor jobMonitor = this.getServiceClient().getService().getJobMonitor();
			return convert2JobMonitorModel(jobMonitor);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}*/
	
	@Override
	public JobMonitorModel getJobMonitor(String jobType,Long jobId)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();
		try {
			JobMonitor jobMonitor = this.getServiceClient().getServiceV2().getNewJobMonitor(jobType,jobId);
			if(jobMonitor == null)
				return null;
			if(jobMonitor.getCurVolMntPoint() != null && jobMonitor.getCurVolMntPoint().startsWith("\\\\?\\")) {
				String path = this.getServiceClient().getServiceV2().getMntPathFromVolumeGUID(jobMonitor.getCurVolMntPoint());
				jobMonitor.setCurVolMntPoint(path);
			}
			return convert2JobMonitorModel(jobMonitor);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}

	@Override
	public JobMonitorModel[] getJobMonitorMap()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			JobMonitor[] jobMonitors = this.getServiceClient().getServiceV2().getJobMonitorMap();
			return convert2JobMonitorModelArray(jobMonitors);
		} catch (WebServiceException e) {
			logger.error("CommonServiceImpl::getJobMonitorMap() got exception:"+e.getMessage());
			this.proccessAxisFaultException(e);
		}

		return null;
	}
	
	@Override
	public JobMonitorModel[] getJobMonitorMapByPolicyId(String policyId)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			FlashJobMonitor[] jobMonitors = getServiceClient_RpsService().getServiceForCPM().getJobMonitorMapByPolicyId(policyId);
			return null;/*convert2JobMonitorModelArray(jobMonitors);*/
		} catch (WebServiceException e) {
			logger.error("CommonServiceImpl::getJobMonitorMapByPolicyId() got exception:"+e.getMessage());
			this.proccessAxisFaultException(e);
		}

		return null;
	}

	private JobMonitorModel convert2JobMonitorModel(JobMonitor jobMonitor) {
		if (jobMonitor == null)
			return null;

		JobMonitorModel model = new JobMonitorModel();
		model.setID(jobMonitor.getJobId());
		model.setVmInstanceUUID(jobMonitor.getVmInstanceUUID());
		model.setBackupStartTime(jobMonitor.getBackupStartTime());
		model.setCurrentProcessDiskName(jobMonitor.getCurrentProcessDiskName());
		model.setElapsedTime(jobMonitor.getElapsedTime());
		model.setEstimateBytesDisk(jobMonitor.getEstimateBytesDisk());
		model.setEstimateBytesJob(jobMonitor.getEstimateBytesJob());
		model.setFlags(jobMonitor.getFlags());
		model.setJobMethod(jobMonitor.getJobMethod());
		model.setJobPhase(jobMonitor.getJobPhase());
		model.setJobStatus(jobMonitor.getJobStatus());
		model.setJobType(jobMonitor.getJobType());
		model.setSessionID(jobMonitor.getSessionID());
		model.setTransferBytesDisk(jobMonitor.getTransferBytesDisk());
		model.setTransferBytesJob(jobMonitor.getTransferBytesJob());
		model.setTransferMode(jobMonitor.getTransferMode());
		model.setVolMethod(jobMonitor.getVolMethod());
		model.setProgramCPUPercentage(jobMonitor.getnProgramCPU());
		model.setSystemCPUPercentage(jobMonitor.getnSystemCPU());
		model.setReadSpeed(jobMonitor.getnReadSpeed());
		model.setWriteSpeed(jobMonitor.getnWriteSpeed());
		model.setSystemReadSpeed(jobMonitor.getnSystemReadSpeed());
		model.setSystemWriteSpeed(jobMonitor.getnSystemWriteSpeed());
		model.setThrottling(jobMonitor.getThrottling());
		model.setTotalSizeRead(jobMonitor.getTotalSizeRead());
		model.setTotalSizeWritten(jobMonitor.getTotalSizeWritten());
		model.setEncInfoStatus(jobMonitor.getEncInfoStatus());
		model.setCurVolMntPoint(jobMonitor.getCurVolMntPoint());
		model.setCompressLevel(jobMonitor.getCompressLevel());
		model.setCTBKJobName(jobMonitor.getCtBKJobName());
		model.setCTBKStartTime(jobMonitor.getCtBKStartTime());
		model.setCTCurCatVol(jobMonitor.getCtCurCatVol());
		model.setCTDWBKJobID(jobMonitor.getCtDWBKJobID());
		model.setGRTEDB(jobMonitor.getWszEDB());
		model.setGRTMailFolder(jobMonitor.getWszMailFolder());
		model.setGRTProcessFolder(jobMonitor.getUlProcessedFolder());
		model.setGRTTotalFolder(jobMonitor.getUlTotalFolder());
		model.setUlMergedSession(jobMonitor.getUlMergedSessions());
		model.setUlTotalMegedSessions(jobMonitor.getUlTotalMegedSessions());
		model.setd2dServerName(jobMonitor.getD2dServerName());
		model.setPolicyName(jobMonitor.getRpsPolicyName());
		model.setDedupe(jobMonitor.isEnableDedupe());
		model.setTotalUniqueData(jobMonitor.getUniqueData());
		model.setReplicationSavedBandWidth(jobMonitor.getReplicationSavedBandWidth());
		model.setTotalVMJobCount(jobMonitor.getUlTotalVMJobCount());
		model.setFinishedVMJobCount(jobMonitor.getUlFinishedVMJobCount());
		model.setFailedVMJobCount(jobMonitor.getUlFailedVMJobCount());
		model.setCanceledVMJobCount(jobMonitor.getUlCanceledVMJobCount());
		model.setDestinationPath(jobMonitor.getDestinationPath());
		model.setDestinationType(jobMonitor.getDestinationType());
		return model;
	}
	
	private Map<String,JobMonitorModel> convert2JobMonitorModel(JobMonitor[] jobMonitors) {
		Map<String, JobMonitorModel> models = null;
		if (jobMonitors == null)
			return null;
		else
			models = new HashMap<String, JobMonitorModel>();

		for(JobMonitor jm : jobMonitors){
			if(jm.getJobPhase() <= 0 && jm.getJobType() <= 0)
				continue;
			String key = jm.getJobType() == JobMonitorConstants.JOBTYPE_CATALOG_GRT ? jm.getJobId() + "_" + jm.getJobType() :
				String.valueOf(jm.getJobType());
			models.put(key, convert2JobMonitorModel(jm));
		}
		
		return models;
	}
	
	private JobMonitorModel[] convert2JobMonitorModelArray(JobMonitor[] jobMonitors){
		if (jobMonitors == null)
			return null;
		
		JobMonitorModel[] models = new JobMonitorModel[jobMonitors.length];
		int i = 0;
		for(JobMonitor jm : jobMonitors){
			if(jm.getJobPhase() <= 0 && jm.getJobType() <= 0)
				continue;
			models[i++] = convert2JobMonitorModel(jm);
		}
		return models;
	}
	

	public DeployUpgradeInfoModel validDeploymentServer(ServerInfoModel serverInfoModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		try {
			if (serverInfoModel != null) {
				DeployUpgradeInfo deployUpgrade = new DeployUpgradeInfo();
				List<ServerInfoModel> temp = new ArrayList<ServerInfoModel>();
				temp.add(serverInfoModel);
				List<RemoteDeployTarget> targetList = ConverRemoteDeployTarget(temp);
				RemoteDeployTarget remoteTarget = targetList.get(0);

				String localDomain = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_DOMAIN);
				String localUser = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_USERNAME);
				String localPassword = (String) this.getThreadLocalRequest()
						.getSession(true).getAttribute(
								SessionConstants.SRING_PASSWORD);
				deployUpgrade= getServiceClient().getService().validRemoteDeploy(localDomain,
						localUser, localPassword, remoteTarget);
				long ret = deployUpgrade.getDwRet();
				//80061 is for upgrade, the existing version is in deployUpgrade object
				if (ret != 0 && ret!=80061) {
					String errMsg = getADTMsg(ret, remoteTarget.getServerName());
					String errorCode = String
							.valueOf(FlashServiceErrorCode.ADT_BASE + ret);
					throw new BusinessLogicException(errorCode, errMsg);
				}
				DeployUpgradeInfoModel result = new DeployUpgradeInfoModel();
				result.setHostname(deployUpgrade.getHostname());
				result.setBuild(deployUpgrade.getBuild());
				result.setMajVer(deployUpgrade.getMajVer());
				result.setPort(deployUpgrade.getPort());
				result.setInstallPath(deployUpgrade.getInstallPath());
				result.setUseHttps(deployUpgrade.isUseHttps());
				return result;
			}
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		DeployUpgradeInfoModel result = new DeployUpgradeInfoModel();
		result.setHostname("");
		result.setBuild(0L);
		result.setMajVer(0L);
		result.setPort(0L);
		result.setInstallPath("");
		return result;

	}

	@Override
	public void cancelJob(long jobID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getService().cancelJob(jobID);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}

	@Override
	public void logout() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
	}

	@Override
	public PagingLoadResult<LogEntry> getJobActivityLogs(long jobNo, PagingLoadConfig config)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult = getServiceClient().getService()
					.getJobActivityLogs(jobNo, start, count);
			int total = (int) activityLogResult.getTotalCount();
			List<LogEntry> resultList = new ArrayList<LogEntry>();

			if (activityLogResult.getLogs() != null) {
				for (ActivityLog log : activityLogResult.getLogs()) {
					int type = LogEntryType.Information;
					if (log.getType() == ActivityLogType.Information)
						type = LogEntryType.Information;
					else if (log.getType() == ActivityLogType.Warning)
						type = LogEntryType.Warning;
					else
						type = LogEntryType.Error;

					LogEntry entry = new LogEntry(type, log.getTime(),"", 
							log.getMessage(), log.getJobID());
					entry.setTimeZoneOffset(log.getTimeZoneOffset());
					resultList.add(entry);
				}
			}
			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public long validateDest(String path, String domain, String user,
			String pwd, int mode) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		long ret = 0;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}

			ret = getServiceClient().getFlashServiceR16_5().validateDestForMode(path, domain, user, pwd,mode);

		} catch (WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = null;

					switch(mode)
					{
					case RESTORE_ALT_MODE:
						ex = this
						.generateException(FlashServiceErrorCode.Restore_ERR_ValidateAltLocFailed);
						break;
					case ARCHIVE_MODE:
						ex = this
						.generateException(FlashServiceErrorCode.ArchiveConfig_ERR_ValidateDestFailed);
						break;
					case ARCHIVE_DEST_MODE:
						ex = this
						.generateException(FlashServiceErrorCode.ArchiveConfig_Dest_ERR_ValidateDestFailed);
						break;
					case DIAGNOSTIC_MODE:
						ex = this
						.generateException(FlashServiceErrorCode.DiagConfig_ERR_ValidateDestFailed);
						break;
					default:
						ex = this
						.generateException(FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed);
						break;
					}
//					String errMsg = MessageFormatEx.format(
//							ex.getDisplayMessage(), path, exception.getMessage());
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), path, e.getFault().getFaultActor());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			proccessAxisFaultException(exception);

		}
		return ret;
	}
	
	@Override
	public long validateDestOnly(String path, String domain, String user,
			String pwd, int mode) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		long ret = 0;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);
			
			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}
			
			ret = getServiceClient().getService().validateDest(path, domain, user, pwd);
			
		} catch (WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					throw new BusinessLogicException(e.getFault().getFaultCodeAsQName()
										.getLocalPart(), e.getMessage());
				}
			}
			proccessAxisFaultException(exception);
			
		}
		return ret;
	}
	
	@Override
	public long validateSource(String path, String domain, String user,
			String pwd,int in_mode) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return validateSource(path, domain, user, pwd,in_mode, true);
	}

	@Override
	public long validateSource(String path, String domain, String user, String pwd, int in_mode, boolean isNeedCreateFolder)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = 0;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}

			ret = getServiceClient().getServiceV2().validateSourceGenFolder(path, domain, user, pwd, isNeedCreateFolder);

		} catch (WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.RestoreJob_SourceInvalid
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = null;
					
					switch(in_mode)
					{
					case ARCHIVE_RESTORE_MODE:
						ex = this
						.generateException(FlashServiceErrorCode.ArchiveRestoreJob_SourceInvalid);
						break;
					case MOUNT_VOLUME_MODEL:
						ex = this.generateException(FlashServiceErrorCode.Common_MountVolume_SourceInvalid);
						break;
					default:
						ex = this
						.generateException(FlashServiceErrorCode.RestoreJob_SourceInvalid);
						break;
					}
					
					/*ex = this
							.generateException(FlashServiceErrorCode.RestoreJob_SourceInvalid);*/
//					String errMsg = MessageFormatEx.format(
//							ex.getDisplayMessage(), exception.getMessage());
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), e.getFault().getFaultActor());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			{
				proccessAxisFaultException(exception);
			}
		}
		return ret;
	}

	@Override
	public boolean isLocalHost(String host) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		boolean isLocal = false;
		try {
			isLocal = getServiceClient().getService().isLocalHost(host);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		return isLocal;
	}

	@Override
	public long validateCopyDest(String path, String domain, String user,
			String pwd) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = 0;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}

			ret = getServiceClient().getFlashServiceR16_5().validateDestForMode(path, domain, user, pwd,COPY_MODE);

		} catch (WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = this
							.generateException(FlashServiceErrorCode.CopyJob_VaildateCopyDestFailed);
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), e.getFault().getFaultActor());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			proccessAxisFaultException(exception);

		}
		return ret;
	}

	public long getDestDriveType(String path)throws BusinessLogicException, ServiceConnectException,
	 ServiceInternalException{
		logger.debug("getDestDriveType() - start");
		long ret = -1;
		try{
			ret = getServiceClient().getService().getDestDriveType(path);
			logger.debug("dest type:" + ret);
			logger.debug("getDestDriveType() - end");
		}
		catch(WebServiceException ex){
			proccessAxisFaultException(ex);
		}
		return ret;
	}
	
	public long getDestDriveTypeForModeType(String path,int mode)throws BusinessLogicException, ServiceConnectException,
	 ServiceInternalException{
		logger.debug("getDestDriveType() - start");
		long ret = -1;
		try{
			ret = getServiceClient().getFlashServiceR16_5().getDestDriveTypeForMode(path,mode);
			logger.debug("dest type:" + ret);
			logger.debug("getDestDriveType() - end");
		}
		catch(WebServiceException ex){
			proccessAxisFaultException(ex);
		}
		return ret;
	}

	@Override
	public Boolean checkRemotePathAccess(String path, String domain,
			String user, String pwd) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {

		Boolean ret = Boolean.FALSE;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}

			ret = getServiceClient().getService().checkRemotePathAccess(path, domain, user, pwd);

		} catch (WebServiceException exception) {
				proccessAxisFaultException(exception);
		}
		return ret;
	}

	private String getNomalizedStr(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	@Override
	public void disconnectRemotePath(String path, String domain, String user,
			String pwd, boolean force) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}

			try {
			   getServiceClient().getService().disconnectRemotePath(path, domain, user, pwd, force);
			}
			catch(Throwable e) {
				//try to disconnect its parent path
				if(path.endsWith("\\") || path.endsWith("/"))
					path = path.substring(0, path.length() - 1);
				int lastFolder = path.lastIndexOf("\\");
				int lastFolder2 = path.lastIndexOf("/");
				lastFolder = lastFolder > lastFolder2 ? lastFolder : lastFolder2;
				if(lastFolder > 0)
				{
					String parentPath = path.substring(0, lastFolder);
					if (parentPath.lastIndexOf("\\") > 2
							|| parentPath.lastIndexOf("/") > 2)
						getServiceClient().getService().disconnectRemotePath(parentPath, domain,
								user, pwd, force);
				}
			}

		} catch (WebServiceException exception) {
				proccessAxisFaultException(exception);
		}
	}
	
	@Override
	public void cutAllRemoteConnections()throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
				getServiceClient_RpsService().getServiceForCPM().cutAllRemoteConnections();
			}
			catch(WebServiceException exception) {
				proccessAxisFaultException(exception);
			}
	}

	@Override
	public TrustHostModel validateRemoteServer(TrustHostModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		String serverName = model.getHostName();
		String protocol = model.getProtocol();
		String username = model.getUser();
		String password = model.getPassword();
		int port = model.getPort();
		WebServiceClientProxy client = null;
		//issue 19159912, to avoid the time-consuming operation when using https scheme to access http server.
		if(protocol.toLowerCase().startsWith("https"))
		{
			try {
//				ServiceInfoList serviceInfoList = WebServiceFactory.getServiceInfoList("http", serverName, port);
				WebServiceClientProxy testClient = WebServiceFactory.getFlassService("http", serverName, port);
				testClient.getService().validateUser(GetUserNameFromUserTextField(username), password, getDomainNameFromUserTextField(username));
				throw generateException(FlashServiceErrorCode.Common_CantConnectRemoteServer);
			} catch (WebServiceException exception) {
			}
		}

		try
		{
			String serviceID = ServiceInfoConstants.SERVICE_ID_D2D_PROPER;
			ServiceInfoList serviceInfoList = null;
			boolean oldD2D = false;
			try{
			serviceInfoList = WebServiceFactory.getServiceInfoList(protocol, serverName, port);

			}catch(WebServiceException e){
				if(e.getMessage().equals(FlashServiceErrorCode.Common_Service_FAIL_TO_GETLIST)){
					//we think it is old D2D
					client = WebServiceFactory.getFlassService(protocol,serverName,port,serviceID);
					oldD2D = true;
				}else{
					throw e;
				}
			}
			if(!oldD2D){
				serviceID = ServiceInfoConstants.SERVICE_ID_D2D_PROPER;
				ServiceInfo featureServiceInfo = WebServiceFactory.getFeatureServiceInfo(serviceID, serviceInfoList);
				client = WebServiceFactory.getFlassService(protocol,serverName,port,serviceID,featureServiceInfo);
			}
			String uuid = client.getService().validateUser(GetUserNameFromUserTextField(username), password, getDomainNameFromUserTextField(username));
			VersionInfo versionInfo = client.getService().getVersionInfo();
			if(versionInfo.getProductType() != null) {
				model.setProductType(Integer.parseInt(versionInfo.getProductType()));
			}
			Integer D2DVersion = Integer.parseInt(versionInfo.getMajorVersion());
			model.setD2DVersion(D2DVersion);
			model.setUuid(uuid);
			return model;
		}catch(WebServiceException ex){
			logger.error(ex.getMessage());

			if (ex.getCause() instanceof ProtocolException)
				throw generateException(FlashServiceErrorCode.Common_CantConnectRemoteServer);
			else if(ex.getCause() instanceof ConnectException
					|| ex.getCause() instanceof SocketException
					|| ex.getCause() instanceof SSLException // add for that it cannot connect server when we change protocol, for issue 20015152
					|| ex.getCause() instanceof UnknownHostException) {
				throw generateException(FlashServiceErrorCode.Common_CantConnectRemoteServer);
			}else if(ex.getMessage().equals(FlashServiceErrorCode.Common_Service_NOT_FOUND)){
				throw generateException(FlashServiceErrorCode.Common_Service_NOT_FOUND);
			}
			else
				proccessAxisFaultException(ex);
		}

		return null;
	}

	public String getDomainNameFromUserTextField (String strUserInput)
	{
		String strDomain = "";

		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;

		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		}
		else
		{
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}

	public String GetUserNameFromUserTextField (String strUserInput)
	{
		String strUser = "";

		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;

		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		}
		else
		{
			// Extract user name part
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}

	@Override
	public void addTrustHost(TrustHostModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		TrustedHost host = new TrustedHost();
		host.setName(model.getHostName());
		host.setUserName(model.getUser());
		host.setPassword(model.getPassword());
		host.setUuid(model.getUuid());
		host.setType(model.getType());
		host.setPort(model.getPort());
		host.setProtocol(model.getProtocol());
		host.setD2dVersion(model.getD2DVersion());
		try{
			getServiceClient().getService().addTrustedHost(host);
		}
		catch(WebServiceException ex){
			proccessAxisFaultException(ex);
		}
	}

	@Override
	public synchronized ExternalLinksModel getExternalLinks(String language, String country) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		if ("de".equals(language)){
			if (this.deExternalLinksModel == null)
				deExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_de_DE.properties");

			return deExternalLinksModel;
		}else if ("fr".equals(language)){
			if (this.frExternalLinksModel == null)
				frExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_fr_FR.properties");

			return frExternalLinksModel;
		}else if ("ja".equals(language)){
			if (this.jaExternalLinksModel == null)
				jaExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_ja_JP.properties");

			return jaExternalLinksModel;
		}else if ("es".equals(language)){
			if (this.esExternalLinksModel == null)
				esExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_es_ES.properties");

			return esExternalLinksModel;
		}else if ("pt".equals(language)){
			if (this.ptExternalLinksModel == null)
				ptExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_pt_BR.properties");

			return ptExternalLinksModel;
		}else if("it".equals(language)){
			if (this.itExternalLinksModel == null)
				itExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_it_IT.properties");

			return itExternalLinksModel;
		}else if ("zh".equals(language)){
			if (this.zhExternalLinksModel == null){
				if(country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
					zhExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_zh_CN.properties");
				else
					zhExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links_zh_TW.properties");
			}
			
			return zhExternalLinksModel;
		}else{
			if (this.enExternalLinksModel == null)
				enExternalLinksModel = loadExternalLinks(ContextListener.PATH_CUSTOMIZATION+"Links.properties");

			return enExternalLinksModel;
		}
	}

	private ExternalLinksModel loadExternalLinks(String path){
		ExternalLinksModel model = new ExternalLinksModel();

		Properties properties = new Properties();
		FileInputStream fis = null; 
	    try {
	    	fis = new FileInputStream(path);
	        properties.load(fis);

	        model.setHomePage(properties.getProperty("homePage"));
	        model.setHomePagePanelHelp(properties.getProperty("homePagePanelHelp"));

	        model.setBackupSettingsHelp(properties.getProperty("backupSettingsHelp"));
	        model.setBackupSettingDestinationHelp(properties.getProperty("backupSettingDestinationHelp"));
	        model.setBackupSettingScheduleHelp(properties.getProperty("backupSettingScheduleHelp"));
	        model.setBackupSettingSettingsHelp(properties.getProperty("backupSettingSettingsHelp"));
	        model.setBackupSettingAdvancedHelp(properties.getProperty("backupSettingAdvancedHelp"));
	        model.setBackupSettingEmailHelp(properties.getProperty("backupSettingEmailHelp"));
	        
	        model.setBackupSettingAdvanceScheduleHelp(properties.getProperty("backupSettingsAdvanceScheduleHelp"));
	        model.setBackupSettingAdvanceThrottlingHelp(properties.getProperty("backupSettingsAdvanceThrottlingHelp"));
	        model.setBackupSettingAdvanceMergeHelp(properties.getProperty("backupSettingsAdvanceMergeHelp"));

	        model.setBackupSettingScheduleStandardHelp(properties.getProperty("backupSettingScheduleStandardHelp"));
	        model.setBackupSettingScheduleAdvancedHelp(properties.getProperty("backupSettingScheduleAdvancedHelp"));
	        
	        
	        model.setBackUpNowHelp(properties.getProperty("backUpNowHelp"));
	        model.setCollectDiagDataHelp(properties.getProperty("collectDiagDataHelp"));

	        model.setRetoreHelp(properties.getProperty("retoreHelp"));
	        model.setRetoreByRecoveryPointsHelp(properties.getProperty("retoreByRecoveryPointsHelp"));
	        model.setRetoreByRecoveryPointsOptionHelp(properties.getProperty("retoreByRecoveryPointsOptionHelp"));
	        model.setRetoreByRecoveryPointsSummaryHelp(properties.getProperty("retoreByRecoveryPointsSummaryHelp"));
	        model.setRetoreByFindHelp(properties.getProperty("retoreByFindHelp"));
	        model.setRetoreByFindSearchHelp(properties.getProperty("retoreByFindSearchHelp"));
	        model.setRetoreByFindOptionHelp(properties.getProperty("retoreByFindOptionHelp"));
	        model.setRetoreByFindSummaryHelp(properties.getProperty("retoreByFindSummaryHelp"));

	        model.setExportRecoveryPoint(properties.getProperty("exportRecoveryPoint"));
	        model.setViewLogsHelp(properties.getProperty("viewLogsHelp"));
	        model.setRemoteDeployHelp(properties.getProperty("remoteDeployHelp"));

	        model.setKnowledgeCenterURL(properties.getProperty("knowledgeCenterURL"));
	        model.setVideoURL(properties.getProperty("videoURL"));
	        model.setVideoCASupportURL(properties.getProperty("videoCASupportURL"));
	        model.setCASupportURL(properties.getProperty("CASupportURL"));
	        model.setGoogleGroupURL(properties.getProperty("googleGroupURL"));
	        model.setFeedBackURL(properties.getProperty("feedBackURL"));
	        model.setD2DUserCenter(properties.getProperty("D2DUserCenter"));
	        model.setTwitterCom(properties.getProperty("twitterCom"));
	        model.setFaceBookCom(properties.getProperty("faceBookCom"));
	        model.setEmailSupportURL(properties.getProperty("emailSupportURL"));
	        model.setUpgradePaidVersionURL(properties.getProperty("upgradePaidVersionURL"));
	        model.setOnlineSupportURL(properties.getProperty("onlineSupportURL"));
	        model.setSolutionsGuideURL(properties.getProperty("solutionsGuideURL"));
	        model.setAgentForWindowsUserGuideURL(properties.getProperty("agentForWindowsUserGuideURL"));
	        
	        model.setHomepageSupportKnowledgeCenter(properties.getProperty("homepageSupportKnowledgeCenter"));
	        model.setHomepageSupportKnowledgeCenterDescription(properties.getProperty("homepageSupportKnowledgeCenterDescription"));
	        model.setHomepageSupportVideoLabel(properties.getProperty("homepageSupportVideoLabel"));
	        model.setHomepageSupportVideoLabelOnlyEn(properties.getProperty("homepageSupportVideoLabelOnlyEn"));
	        model.setHomepageSupportVideoDescription(properties.getProperty("homepageSupportVideoDescription"));
	        model.setHomepageSupportCASupport(properties.getProperty("homepageSupportCASupport"));
	        model.setHomepageSupportOnlineDescription(properties.getProperty("homepageSupportOnlineDescription"));
	        model.setHomepageSupportSendFeedbackLabel(properties.getProperty("homepageSupportSendFeedbackLabel"));
	        model.setHomepageSupportSendFeedbackLabelOnlyEn(properties.getProperty("homepageSupportSendFeedbackLabelOnlyEn"));
	        model.setHomepageSupportSendFeedbackDescription(properties.getProperty("homepageSupportSendFeedbackDescription"));
	        model.setHomepageSupportGoogleGroupLabel(properties.getProperty("homepageSupportGoogleGroupLabel"));
	        model.setHomepageSupportGoogleGroupLabelDescription(properties.getProperty("homepageSupportGoogleGroupLabelDescription"));
	        model.setHomepageSupportD2DUserCenterLabel(properties.getProperty("homepageSupportD2DUserCenterLabel"));
	        model.setHomepageSupportD2DUserCenterLabelDescription(properties.getProperty("homepageSupportD2DUserCenterLabelDescription"));
	        model.setHomepageSupportD2DOnlineHelp(properties.getProperty("homepageSupportD2DOnlineHelp"));
	        model.setRSSURL(properties.getProperty("rssURL"));
	        model.setReleaseNotesURL(properties.getProperty("releaseNotes"));
	        model.setHomepageLiveChatDescript(properties.getProperty("homepageLiveChatDescription"));
	        model.setHomepageLiveChatLabel(properties.getProperty("homepageLiveChatLabel"));
	        model.setHomepageEmailSupportLabel(properties.getProperty("homepageEmailSupportLabel"));
	        model.setHomepageEmailSupportDescription(properties.getProperty("homepageEmailSupportDescription"));

	        model.setIntroVideoURL(properties.getProperty("introVideoURL"));
	        model.setIntroVideoName(properties.getProperty("introVideoName"));
	        model.setSecondVideoURL(properties.getProperty("secondVideoURL"));
	        model.setSecondVideoName(properties.getProperty("secondVideoName"));
	        model.setLiveChatURL(properties.getProperty("liveChatURL"));
	        model.setCentralLiveChatURL(properties.getProperty("centralLiveChatURL"));
	        //
	        model.setCASupportIntroVideoURL(properties.getProperty("CASupportintroVideoURL"));
	        model.setCASupportSecondVideoURL(properties.getProperty("CASupportsecondVideoURL"));
	        model.setFlashURL(properties.getProperty("flashURL"));
	        model.setLocalUserGuideURL(properties.getProperty("LocalUserGuideURL"));
	        model.setOnlineUserGuideURL(properties.getProperty("OnlineUserGuideURL"));
	        //All Feeds
	        model.setAllFeedsHelp(properties.getProperty("allFeedsHelp"));
			model.setVideoRSSURL(properties.getProperty("videoRSSURL"));
	        model.setGoogleGroupRSSURL(properties.getProperty("googleGroupRSSURL"));
	        model.setFeedBackRSSURL(properties.getProperty("feedBackRSSURL"));
	        model.setD2DUserCenterRSSURL(properties.getProperty("D2DUserCenterRSSURL"));
	        
	        //Archive help urls 
            model.setArchiveSourceSettings(properties.getProperty("archiveSourceSettings"));
	        model.setArchiveDestinationSettings(properties.getProperty("archiveDestinationSettings"));
	        model.setArchiveScheduleSettings(properties.getProperty("archiveScheduleSettings"));
	        model.setArchiveAdvancedSettings(properties.getProperty("archiveAdvancedSettings"));
	        
	        //File Archive
	        model.setFileArchiveSourceSettings(properties.getProperty("fileArchiveSourceSettings"));
	        model.setFileArchiveDestinationSettings(properties.getProperty("fileArchiveDestinationSettings"));
	        model.setFileArchiveScheduleSettings(properties.getProperty("fileArchiveScheduleSettings"));
	        model.setFileArchiveAdvancedSettings(properties.getProperty("fileArchiveAdvancedSettings"));
	        
	        //preferences help urls
	        model.setPreferencesGeneralSettings(properties.getProperty("preferencesGeneralSettings"));
	        model.setPreferencesEmailSettings(properties.getProperty("preferencesEmailSettings"));
	        model.setPreferencesAutoUpdateSettings(properties.getProperty("preferencesAutoUpdateSettings"));
	        
	        //restore help urls
	        model.setRestoreByBrowseHelp(properties.getProperty("restoreByBrowseHelp"));
	        model.setRestoreByBrowseOptionHelp(properties.getProperty("restoreByBrowseOptionHelp"));
	        model.setRestoreByBrowseSummaryHelp(properties.getProperty("restoreByBrowseSummaryHelp"));
	        model.setRecoveryVMHelp(properties.getProperty("recoveryVMHelp"));
	        model.setRecoveryVMSummaryHelp(properties.getProperty("recoveryVMSummaryHelp"));
	        model.setRecoveryVAppHelp(properties.getProperty("recoveryVAppHelp"));
	        model.setRecoveryVAppSpecifyVDCHelp(properties.getProperty("recoveryVAppSpecifyVDCHelp"));
	        model.setExchangeMailHelp(properties.getProperty("exchangeMailHelp"));
	        model.setExchangeMailExplorerHelp(properties.getProperty("exchangeMailExplorerHelp"));
	        model.setExchangeMailOptionHelp(properties.getProperty("exchangeMailOptionHelp"));
	        model.setExchangeMailSummaryHelp(properties.getProperty("exchangeMailSummaryHelp"));
	        model.setApplicatioinRestoreExHelpURL(properties.getProperty("applicationRestoreEx"));
	        model.setApplicatioinRestoreSQLHelpURL(properties.getProperty("applicationRestoreSQL"));
	        model.setRetoreByADRecoveryPointsHelp(properties.getProperty("retoreByADRecoveryPointsHelp"));
	        model.setRetoreByADExplorerHelp(properties.getProperty("restoreByADExplorerHelp"));
	        model.setRetoreByADOptionHelp(properties.getProperty("restoreByADOptionHelp"));
	        model.setRetoreByADSummaryHelp(properties.getProperty("restoreByADSummaryHelp"));
	        model.setExchangeGranularRestoreUtility(properties.getProperty("exchangeGranularRestoreUtility"));
	        
	        //cloud configuration
            model.setcloudConfigurationSettingsHelp(properties.getProperty("cloudConfigurationSettingsHelp"));
            model.setspecifyCloudConfigurationRestore(properties.getProperty("specifyCloudConfigurationRestore"));
            model.setaddCloudBucketHelp(properties.getProperty("addCloudBucketHelp"));
            
            //Archive policies
            model.setArchivePoliciesHelp(properties.getProperty("archivePoliciesHelp"));
            
            // Copy Recovery Points
            model.setCopyRecoveryPointsSettings(properties.getProperty("copyRecoveryPointsSettings"));
            // job monitor help
            model.setJobMonitorPanelHelp(properties.getProperty("jobMonitorPanel"));
            
            //AERP_UDP Registration
            model.setUDPRegistrationWindowHelpURL(properties.getProperty("udpRegistrationWindowHelpURL"));
            model.setUDPPrivacyPolicyURL(properties.getProperty("udpPrivacyPolicyURL"));
            model.setUDPEUModelClauseURL(properties.getProperty("udpEUModelClauseURL"));
            
            //vsphere
            model.setVSphereHomepageHelp(properties.getProperty("vSphereHomepageHelp"));
            model.setVSphereUserGuideHelp(properties.getProperty("vSphereUserGuideHelp"));
            model.setVMBackupNowHelp(properties.getProperty("vmBackupNowHelp"));
            model.setVMCopyRecoveryPointHelp(properties.getProperty("vmCopyRecoveryPointHelp"));
            model.setVMLogActivityHelp(properties.getProperty("vmLogActivityHelp"));
            model.setVMRemoteDeployHelp(properties.getProperty("vmRemoteDeployHelp"));
            model.setVMJobMonitorHelp(properties.getProperty("vmJobMonitorHelp"));
            model.setVMBackupSettingDestinationHelp(properties.getProperty("vmBackupSettingDestinationHelp"));
            model.setVMBackupSettingScheduleHelp(properties.getProperty("vmBackupSettingScheduleHelp"));
            model.setVMBackupSettingScheduleStandardHelp(properties.getProperty("vmBackupSettingSchedulStandardHelp"));
            model.setVMBackupSettingScheduleAdvancedHelp(properties.getProperty("vmBackupSettingSchedulAdvancedHelp"));
            model.setVMBackupSettingSettingsHelp(properties.getProperty("vmBackupSettingSettingsHelp"));
            model.setVMBackupSettingAdvancedHelp(properties.getProperty("vmBackupSettingAdvancedHelp"));
            model.setVMBackupSettingEmailHelp(properties.getProperty("vmBackupSettingEmailHelp"));
			//vsphere support panel
            model.setVSphereVideoURL(properties.getProperty("vSphereVideoURL"));
	        model.setVSphereVideoCASupportURL(properties.getProperty("vSphereVideoCASupportURL"));
	        model.setVSphereCASupportURL(properties.getProperty("vSphereCASupportURL"));
	        model.setVSphereGoogleGroupURL(properties.getProperty("vSphereGoogleGroupURL"));
	        model.setVSphereFeedBackURL(properties.getProperty("vSphereFeedBackURL"));
	        model.setVSphereD2DUserCenter(properties.getProperty("vSphereD2DUserCenter"));
	        model.setVSphereTwitterCom(properties.getProperty("vSphereTwitterCom"));
	        model.setVSphereFaceBookCom(properties.getProperty("vSphereFaceBookCom"));
	        model.setVSphereMountVolumeHelp(properties.getProperty("vSphereMountVolumeHelp"));

            
			//VCM help link
            model.setVirtualizationServerHelp(properties.getProperty("virtualizationServerHelp"));
            model.setVirtualizationMachineHelp(properties.getProperty("virtualizationMachineHelp"));
            model.setStandinSettingsHelp(properties.getProperty("standinSettingsHelp"));
            model.setVirtualStandbyEmailAlertHelp(properties.getProperty("virtualStandbyEmailAlertHelp"));
            model.setVirtualStandbyHelp(properties.getProperty("virtualStandbyHelp"));
            model.setVirtualStandbyUserGuideHelp(properties.getProperty("virtualStandbyUserGuideHelp"));
            model.setVirtualStandbyRecoveryPointSnapshotsHelp(properties.getProperty("virtualStandbyRecoveryPointSnapshotsHelp"));

            model.setVirtualStandbyCASupportURL(properties.getProperty("virtualStandbyVideoCASupportURL"));
            model.setVirtualStandbyViewLogURL(properties.getProperty("virtualStandbyViewLogURL"));
            model.setVirtualStandbyJobMonitorURL(properties.getProperty("virtualStandbyJobMonitorURL"));
            model.setVirtualStandbyFaceBookCom(properties.getProperty("virtualStandbyFaceBookCom"));
            model.setVirtualStandbyTwitterCom(properties.getProperty("virtualStandbyTwitterCom"));
            model.setVirtualStandbyUserCenter(properties.getProperty("virtualStandbyUserCenter"));
            model.setVirtualStandbyFeedBackURL(properties.getProperty("virtualStandbyFeedBackURL"));
            model.setVirtualStandbyGoogleGroupURL(properties.getProperty("virtualStandbyGoogleGroupURL"));
            model.setVirtualStandbyCASupportURL(properties.getProperty("virtualStandbyCASupportURL"));
            model.setVirtualStandbyVideoURL(properties.getProperty("virtualStandbyVideoURL"));
            model.setVirtualStandbyBackupSettingEmailHelp(properties.getProperty("virtualStandbyBackupSettingEmailHelp"));
            
            model.setMountVolumeHelp(properties.getProperty("mountVolumeHelp"));
            model.setJvmOutOfMemoryHelp(properties.getProperty("jvmOutOfMemoryHelp"));
	    } catch (IOException e) {
	    } finally {
	    	try {
	    		if(fis != null) fis.close();
	    	}catch(Throwable t) {}
	    }

		return model;
	}

	@Override
	public void removeTrustedHost(TrustHostModel trustHost)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		TrustedHost host = new TrustedHost();
		host.setName(trustHost.getHostName());
		host.setUuid(trustHost.getUuid());
		host.setPort(trustHost.getPort());

		try {
			getServiceClient().getService().removeTrustedHost(host);
		} catch (WebServiceException ex) {
			proccessAxisFaultException(ex);
		}
	}

	@Override
	public boolean checkBLILic() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("checkBLILic() - enter");
		boolean ret = false;
		try {
			ret = getServiceClient().getService().checkBLILic();
		} catch (WebServiceException ex) {
			logger.error("error occured:" + ex);
			proccessAxisFaultException(ex);
		}
		logger.debug("checkBLILic() - exit: " + ret);
		return ret;
	}

	@Override
	public DestinationCapacityModel getDestCapacity(String destination,
			String domain, String userName, String pwd)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		logger.debug("getDestCapacity(String, String, String) begin");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
	try {
		DestinationCapacity capacity = getServiceClient().getService().getDestCapacity(destination,
				domain, userName, pwd);

		DestinationCapacityModel model = HomepageServiceImpl.convetDestCapacityModel(capacity);

		logger.debug(StringUtil.convertObject2String(model));
		logger.debug("getDestCapacity returns value - end");
		return model;
	}catch(WebServiceException exception) {
		proccessAxisFaultException(exception);
	}
	logger.debug("getDestCapacity returns null - end");
	return null;
	}

	@Override
	public Boolean isYouTubeVideoSource() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
// 		all videos will only be posted to YouTube and no longer on CA Support.
//		return BaseServiceImpl.isVideoSourceYouTube();
		return true;
	}

	@Override
	public void setYouTubeVideoSource(Boolean b) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		BaseServiceImpl.setVideoSourceYouTube(b);
	}

	@Override
	public List<NetworkPathModel> getMappedNetworkPath(String userName)throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException{
		logger.debug("getMappedNetworkPath(String) - start");
		logger.debug("userName:" + userName);
		try {
			if(userName != null && userName.indexOf("\\") > 0) {
				int backSlash = userName.indexOf("\\");
				String domain = userName.substring(0, backSlash);
				if(domain.equalsIgnoreCase("localhost"))
					userName = userName.substring(backSlash + 1);
			}
			NetworkPath[] pathArr = getServiceClient().getService().getMappedNetworkPath(userName);
			List<NetworkPathModel> modelList = new ArrayList<NetworkPathModel>();
			if(pathArr != null && pathArr.length > 0)
			{

				for (int i = 0; i < pathArr.length; i++) {
					final NetworkPathModel model = new NetworkPathModel();
					model.setDriverletter(pathArr[i].getDriverletter());
					model.setRemotePath(pathArr[i].getRemotePath());
					modelList.add(model);
				}
			}
			logger.debug(StringUtil.convertArray2String(pathArr));

			logger.debug("getMappedNetworkPath(String) - end");
			return modelList;

		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		logger.debug("getMappedNetworkPath(String) - return null - end");
		return null;
	}


	@Override
	public long getMaxRPLimit() {
		logger.debug("getMaxRPLimit() - start");
		long maxRP = BaseServiceImpl.getMaxRecPointLimit();
		logger.debug("getMaxRPLimit() - end :" + maxRP);
		return maxRP;
	}

	@Override
	public int getPatchManagerStatus()
	{
		int iStatus = 0;
		try {
			iStatus = this.getServiceClient().getServiceV2().IsPatchManagerReady();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return iStatus;
	}

	@Override
	public boolean IsPatchManagerRunning()
	{
		boolean bRunning = false;
		try {
			return this.getServiceClient().getServiceV2().IsPatchManagerRunning();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return bRunning;
	}

	@Override
	public boolean IsPatchManagerBusy()
	{
		boolean bPMBusy = false;
		try {
			return this.getServiceClient().getServiceV2().IsPatchManagerBusy();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return bPMBusy;
	}

	@Override
	@Deprecated
	public PatchInfoModel SubmitRequest(int in_iRequestType)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		PatchInfoModel patchInfo = new PatchInfoModel();
		try {
			PMResponse pmResponse = this.getServiceClient().getServiceV2().checkUpdate();
			
			int nResponseError = pmResponse.getM_iResponseError();			
			switch( nResponseError ){
			case 0:
				{
					patchInfo = ConvertPatchInfoModel(this.getServiceClient().getServiceV2().getPatchInfo());
					if(pmResponse.isIsRequestFailed() == true){	
						patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
					 	patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());					
					}
					else{
						patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS);
					}
				}
				break;
			case 1: // already update to date
			case 2: // no new updates found
				patchInfo.setError_Status(SummaryPanel.ERROR_NONEW_PATCHES_AVAILABLE);
				patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());
				break;
			case -1:
				patchInfo.setError_Status(SummaryPanel.WARNING_NODE_MANAGED_BY_CPM);
				patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());
				break;
			default:
				patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
				patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return patchInfo;
	}
	
	@Override
	public PatchInfoModel checkUpdate() throws BusinessLogicException,ServiceConnectException, ServiceInternalException {
		PatchInfoModel patchInfo = new PatchInfoModel();
		patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
		try {
			PMResponse pmResponse = this.getServiceClient().getServiceV2().checkUpdate();
			if(pmResponse==null){
				return patchInfo;
			}else{
				patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());
			}
			switch (pmResponse.getM_iResponseError()) {
			case PMResponse.RESPONSE_ERROR_FailedToCheckUpdate:
				patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Timeout: //request timeout
				patchInfo.setError_Status(SummaryPanel.WARNING_GET_PATCH_TIMEOUT);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Downloading: // in progress
				patchInfo.setError_Status(SummaryPanel.WARNING_GET_PATCH_IN_PROGRESS);
				break;
			case PMResponse.RESPONSE_ERROR_Warning_ManagedByCPM: //managed by CPM
				patchInfo.setError_Status(SummaryPanel.WARNING_NODE_MANAGED_BY_CPM);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Success: //update is ready
				patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS);
				break;
			case PMResponse.RESPONSE_ERROR_ProductUpToDate: // already update to date
			case PMResponse.RESPONSE_ERROR_NoNewUpdate: // no new updates found
				patchInfo.setError_Status(SummaryPanel.ERROR_NONEW_PATCHES_AVAILABLE);
				break;
			default:
			}
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger.debug("checkUpdate return: "+patchInfo.getError_Status()+" "+patchInfo.getErrorMessage());
		return patchInfo;
	}

	private PatchInfoModel ConvertPatchInfoModel(PatchInfo in_patchInfo)
	{
		PatchInfoModel patchInfoModel = new PatchInfoModel();

		//product information
		patchInfoModel.setMajorversion(in_patchInfo.getMajorversion());
		patchInfoModel.setMinorVersion(in_patchInfo.getMinorVersion());
		patchInfoModel.setServicePack(in_patchInfo.getServicePack());

		////patch information
		patchInfoModel.setPackageID(in_patchInfo.getPackageID());
		patchInfoModel.setPublishedDate(in_patchInfo.getPublishedDate());
		patchInfoModel.setDescription(in_patchInfo.getDescription());
		patchInfoModel.setPatchDownloadLocation(in_patchInfo.getPatchDownloadLocation());
		patchInfoModel.setPatchURL(in_patchInfo.getPatchURL());
		patchInfoModel.setRebootRequired(in_patchInfo.getRebootRequired());
		patchInfoModel.setSize(in_patchInfo.getSize());
		patchInfoModel.setPatchVersionNumber(in_patchInfo.getPatchVersionNumber());
		patchInfoModel.setAvailableStatus(in_patchInfo.getAvailableStatus());
		patchInfoModel.setDownloadStatus(in_patchInfo.getDownloadStatus());
		patchInfoModel.setInstallStatus(in_patchInfo.getInstallStatus());
		patchInfoModel.setErrorMessage(in_patchInfo.getErrorMessage());
		patchInfoModel.setError_Status(in_patchInfo.getError_Status());

		return patchInfoModel;
	}

	//added by cliicy.luo
	
	@Override
	public BIPatchInfoModel checkBIUpdate() throws BusinessLogicException,ServiceConnectException, ServiceInternalException {
		BIPatchInfoModel patchInfo = new BIPatchInfoModel();
		patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
		try {
			PMResponse pmResponse = this.getServiceClient().getServiceV2().checkBIUpdate();
			if(pmResponse==null){
				return patchInfo;
			}else{
				patchInfo.setErrorMessage(pmResponse.getM_ErrorMessage());
			}
			switch (pmResponse.getM_iResponseError()) {
			case PMResponse.RESPONSE_ERROR_FailedToCheckUpdate:
				patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_FAIL);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Timeout: //request timeout
				patchInfo.setError_Status(SummaryPanel.WARNING_GET_PATCH_TIMEOUT);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Downloading: // in progress
				patchInfo.setError_Status(SummaryPanel.WARNING_GET_PATCH_IN_PROGRESS);
				break;
			case PMResponse.RESPONSE_ERROR_Warning_ManagedByCPM: //managed by CPM
				patchInfo.setError_Status(SummaryPanel.WARNING_NODE_MANAGED_BY_CPM);
				break;
			case PMResponse.RESPONSE_ERROR_Update_Success: //update is ready
				patchInfo.setError_Status(SummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS);
				break;
			case PMResponse.RESPONSE_ERROR_ProductUpToDate: // already update to date
			case PMResponse.RESPONSE_ERROR_NoNewUpdate: // no new updates found
				patchInfo.setError_Status(SummaryPanel.ERROR_NONEW_PATCHES_AVAILABLE);
				break;
			default:
			}
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger.debug("checkBIUpdate return: "+patchInfo.getError_Status()+" "+patchInfo.getErrorMessage());
		return patchInfo;
	}
	
		
	@Override
	public PatchInfoModel getUpdateInfo() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		
		PatchInfoModel patchInfo = new PatchInfoModel();
		patchInfo.setError_Status(PatchInfo.ERROR_NONEW_PATCHES_AVAILABLE);
		try {
			PatchInfo info =this.getServiceClient().getServiceV2().getPatchInfo();
			if(info!=null){
				patchInfo= ConvertPatchInfoModel(info); 
			}
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return patchInfo;
	}

	
	
	@Override
	public BIPatchInfoModel getBIUpdateInfo() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		
		BIPatchInfoModel patchInfo = null;
		
		try {
			BIPatchInfo info =this.getServiceClient().getServiceV2().getPMBIPatchInfo();
			if(info !=null){
				patchInfo= BIPatchInfoModel.ConvertBIPatchInfoModel(info); 
			}
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return patchInfo;
	}
	//added by cliicy.luo
	
	@Override
	public boolean isBackupEncryptionAlgorithmAndKeyChanged(
			int encryptionAlgorithm, String encryptionKey)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			return this.getServiceClient().getServiceV2().isBackupEncryptionAlgorithmAndKeyChangedWithParams(encryptionAlgorithm, encryptionKey);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		
		return false;
	}

	@Override
	public JobMonitorHistoryItemModel[] getJobMonitorHistory()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();
		try{
			JobMonitorHistoryItem[] jobMonitorHistoryItems = getServiceClient().getServiceV2().getJobMonitorHistory(); 
			JobMonitorHistoryItemModel[] jobMonitorHistoryModels = new JobMonitorHistoryItemModel[jobMonitorHistoryItems.length];
			for(int i = 0; i < jobMonitorHistoryItems.length; i++ ){
				jobMonitorHistoryModels[i]= convert2JobMoninorHistoryItemModel(jobMonitorHistoryItems[i]); 
			}
			return jobMonitorHistoryModels;
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return null;
	}
	
	private JobMonitorHistoryItemModel convert2JobMoninorHistoryItemModel(JobMonitorHistoryItem jobMonitorHistoryItem){
		if(jobMonitorHistoryItem == null )
			return null;
		JobMonitorHistoryItemModel model = new JobMonitorHistoryItemModel();
		model.setSessionID(jobMonitorHistoryItem.getSessionID());
		model.setReadSpeed(jobMonitorHistoryItem.getnReadSpeed());
		model.setWriteSpeed(jobMonitorHistoryItem.getnWriteSpeed());
		
		return model;
	}

	@Override
	public EncryptionLibModel getEncryptionAlgorithm() {
		EncryptionLibModel libModel = new EncryptionLibModel();
		libModel.setLibName("MSCRYPTO");
		libModel.setLibType(1);
		EncryptionAlgModel[] algModels = new EncryptionAlgModel[3];
		algModels[0] = new EncryptionAlgModel();
		algModels[0].setName("AES-128");
		algModels[0].setAlgType(1);
		
		algModels[1] = new EncryptionAlgModel();
		algModels[1].setName("AES-192");
		algModels[1].setAlgType(2);
		
		algModels[2] = new EncryptionAlgModel();
		algModels[2].setName("AES-256");
		algModels[2].setAlgType(3);
//		libModel.setAlgorithms(algModels);
		libModel.algorithms = algModels;
		return libModel;
	}

	@Override
	public boolean validateSessionPasswordByHash(String password, long pwdLen,
			String hashValue, long hashLen) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		if(logger.isDebugEnabled()) {
			logger.debug("validateSessionPasswordByHash(String, long, String, long) - start");
		}
		
		try {
			boolean isValid = getServiceClient().getServiceV2()
				.validateSessionPasswordByHash(password, pwdLen, hashValue, hashLen);
			return isValid;
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} 
			
		logger.debug("validateSessionPasswordByHash(String, String, long) - end");
		return false;
	}

	@Override
	public boolean validateSessionPassword(String password, String destination,
			long sessionNum) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		if(logger.isDebugEnabled()) {
			logger.debug("validateSessionPassword(String, String, long) - start");
			logger.debug("password:");
			logger.debug("destination:" + destination);
			logger.debug("sessionNum:" + sessionNum);
		}
		
		try {
			boolean isValid = getServiceClient().getServiceV2().validateSessionPassword(password, destination, sessionNum);
			return isValid;
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} 
			
		logger.debug("validateSessionPassword(String, String, long) - end");
		return false;
	}

	@Override
	public String[] getSessionPasswordBySessionGuid(String[] sessionGuid)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		if(logger.isDebugEnabled()){
			logger.debug("getSessionPasswordBySessionGuid - start");
			logger.debug("session guid: " + StringUtil.convertArray2String(sessionGuid));
		}
		try {
			String[] pwdsStrings = getServiceClient().getServiceV2().getSessionPasswordBySessionGuid(sessionGuid);
			if(pwdsStrings != null) {
				for(int i = 0; i < pwdsStrings.length; i ++) {
					if(pwdsStrings[i].isEmpty()) {
						pwdsStrings[i] = null;
					}
				}
			}
			
			return pwdsStrings;
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} 
		return null;
	}
	
	@Override
	public int addVirtualCenter(VirtualCenterModel vcModel) {
		VirtualCenter vc = new VirtualCenter();
		vc.setVcName(vcModel.getVcName());
		vc.setUsername(vcModel.getUsername());
		vc.setPassword(vcModel.getPassword());
		vc.setProtocol(vcModel.getProtocol());
		vc.setPort(vcModel.getPort());
		try {
			return getServiceClient().getServiceV2().addVirtualCenter(vc);
		} catch (WebServiceException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public int removeVirtualCenter(VirtualCenterModel vcModel) {
		VirtualCenter vc = new VirtualCenter();
		vc.setVcName(vcModel.getVcName());
		//vc.setUsername(vcModel.getUsername());
		//vc.setPassword(vcModel.getPassword());
		//vc.setProtocol(vcModel.getProtocol());
		//vc.setPort(vcModel.getPort());
		try{
			return getServiceClient().getServiceV2().removeVirtualCenter(vc);
		}catch (WebServiceException e){
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public int validateVirtualCenter(VirtualCenterModel vcModel) {
		try{
			return getServiceClient().getServiceV2().validateVC(ConvertToVirtualCenter(vcModel));
		}catch (WebServiceException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public VirtualCenter ConvertToVirtualCenter(VirtualCenterModel vcModel){
		VirtualCenter vc = new VirtualCenter();
		vc.setVcName(vcModel.getVcName());
		vc.setUsername(vcModel.getUsername());
		vc.setPassword(vcModel.getPassword());
		vc.setProtocol(vcModel.getProtocol());
		vc.setPort(vcModel.getPort());
		return vc;
	}

	@Override
	public JobMonitorModel getVMJobMonitor(String vmInstanceUUID,String jobType,Long jobId)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			VirtualMachine virtualMachine = new VirtualMachine(vmInstanceUUID);
			JobMonitor jobMonitor = this.getServiceClient().getServiceV2().getVMJobMonitor(virtualMachine,jobType,jobId);
			return convert2JobMonitorModel(jobMonitor);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}
	
	@Override
	public JobMonitorModel[] getVMJobMonitorMap(BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			if(vmModel == null){
				return null;
			}
			JobMonitor[] jobMonitors = this.getServiceClient().getServiceV2().getVMJobMonitorMap(ConvertToVirtualMachine(vmModel));
			return convert2JobMonitorModelArray(jobMonitors);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}
	
	public static VirtualMachine ConvertToVirtualMachine(BackupVMModel vmModel){
		if(vmModel == null){
			return null;
		}
		VirtualMachine vm = new VirtualMachine();
		vm.setVmHostName(vmModel.getVmHostName());
		vm.setVmName(vmModel.getVMName());
		vm.setVmUUID(vmModel.getUUID());
		vm.setVmInstanceUUID(vmModel.getVmInstanceUUID());
		return vm;
	}

	@Override
	public boolean isVMCompressionLevelChagned(BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			if(vmModel ==null){
				return false;
			}
			return this.getServiceClient().getServiceV2().isVMBackupCompressionLevelChanged(ConvertToVirtualMachine(vmModel));
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return false;
	}

	@Override
	public void backupVM(int backupType, String name,BackupVMModel vmModel)
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException {
		try {
			VirtualMachine vm = ConvertToVirtualMachine(vmModel);
			this.getServiceClient().getServiceV2().backupVM(backupType, name,vm);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
	}

	@Override
	public void cancelVMJob(long jobID,BackupVMModel vmModel) throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getServiceV2().cancelVMJob(jobID, vmModel.getVmInstanceUUID());
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}
	
	@Override
	public void deleteVMActivityLog(Date date, BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			getServiceClient().getServiceV2().deleteVMActivityLogs(date,ConvertToVirtualMachine(vmModel));
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		
	}

	@Override
	public PagingLoadResult<LogEntry> getVMActivityLogs(
			PagingLoadConfig config, BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult = getServiceClient().getServiceV2()
					.getVMActivityLogs(start, count,ConvertToVirtualMachine(vmModel));
			int total = (int) activityLogResult.getTotalCount();
			List<LogEntry> resultList = new ArrayList<LogEntry>();

			if (activityLogResult.getLogs() != null) {
				for (ActivityLog log : activityLogResult.getLogs()) {
					int type = LogEntryType.Information;
					if (log.getType() == ActivityLogType.Information)
						type = LogEntryType.Information;
					else if (log.getType() == ActivityLogType.Warning)
						type = LogEntryType.Warning;
					else
						type = LogEntryType.Error;

					LogEntry entry = new LogEntry(type, log.getTime(),"", 
							log.getMessage(), log.getJobID());
					entry.setTimeZoneOffset(log.getTimeZoneOffset());
					resultList.add(entry);
				}
			}
			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public PagingLoadResult<LogEntry> getVMJobActivityLogs(long jobNo,
			PagingLoadConfig config, BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult = getServiceClient().getServiceV2()
					.getVMJobActivityLogs(jobNo, start, count,ConvertToVirtualMachine(vmModel));
			int total = (int) activityLogResult.getTotalCount();
			List<LogEntry> resultList = new ArrayList<LogEntry>();

			if (activityLogResult.getLogs() != null) {
				for (ActivityLog log : activityLogResult.getLogs()) {
					int type = LogEntryType.Information;
					if (log.getType() == ActivityLogType.Information)
						type = LogEntryType.Information;
					else if (log.getType() == ActivityLogType.Warning)
						type = LogEntryType.Warning;
					else
						type = LogEntryType.Error;

					LogEntry entry = new LogEntry(type, log.getTime(),"", 
							log.getMessage(), log.getJobID());
					entry.setTimeZoneOffset(log.getTimeZoneOffset());
					resultList.add(entry);
				}
			}
			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public JobMonitorModel getArchiveJobMonitor()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			JobMonitor jobMonitor = getServiceClient().getServiceV2().getArchiveJobMonitor();
			return convert2JobMonitorModel(jobMonitor);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}

	private Boolean sessionInvalid() {
		if ( this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_USERNAME) == null
				&& this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_UUID) == null)
			
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	@Override
	public JobMonitorModel getArchiveRestoreJobMonitor()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		if (sessionInvalid())
			throw new SessionTimeoutException();

		try {
			JobMonitor jobMonitor = getServiceClient().getServiceV2().getArchiveRestoreJobMonitor();
			return convert2JobMonitorModel(jobMonitor);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}

		return null;
	}

	@Override
	public Long ValidateArchiveSource(ArchiveDiskDestInfoModel sourceInfo)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		long ret = -1;
		//try {
			String path = getNomalizedStr(sourceInfo.getArchiveDiskDestPath());
			sourceInfo.setArchiveDiskDestPath(path);

			ArchiveDiskDestInfo archiveDiskDestConfig = new ArchiveDiskDestInfo();
			
			archiveDiskDestConfig.setArchiveDiskDestPath(sourceInfo.getArchiveDiskDestPath());
			archiveDiskDestConfig.setArchiveDiskUserName(sourceInfo.getArchiveDiskUserName());
			archiveDiskDestConfig.setArchiveDiskPassword(sourceInfo.getArchiveDiskPassword());
			
			try {
				return getServiceClient().getServiceV2().validateArchiveSource(archiveDiskDestConfig);
			} catch (WebServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*	} catch (AxisFault exception) {
			if (FlashServiceErrorCode.RestoreJob_SourceInvalid
					.equals(exception.getFaultCode().getLocalPart())) {
				ret = 1;
				BusinessLogicException ex = this
						.generateException(FlashServiceErrorCode.RestoreJob_SourceInvalid);
				String errMsg = MessageFormatEx.format(ex.getDisplayMessage(),
						exception.getMessage());
				ex.setDisplayMessage(errMsg);
				throw ex;
			} else {
				proccessAxisFaultException(exception);
			}
		}*/
		return ret;
	}

	@Override
	public String getSymbolicLinkActualPath(String sourcePath)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		String ret = null;			
			try {
				return getServiceClient().getFlashService(IFlashService_R16_U6.class).getSymbolicLinkActualPath(sourcePath);
			} catch (WebServiceException e) {
				proccessAxisFaultException(e);
				
			}

		return ret;
	}

	@Override
	public String getMntPathFromVolumeGUID(String strGUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			return getServiceClient().getServiceV2().getMntPathFromVolumeGUID(strGUID);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return null;
	}

	@Override
	public MountSessionModel[] getMountedSession()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			MountSession[] sessions =  getServiceClient().getServiceV2().getMountedSessions();
			return convertTOMountSessionModel(sessions);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return null;
	}
	
	@Override
	public MountSessionModel[] getMountedSessionByDest(String currentDest)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			MountSession[] sessions =  getServiceClient().getFlashService
				(IFlashService_R16_U4.class).getMountedSessionsByBackupDest(currentDest);
			return convertTOMountSessionModel(sessions);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return null;
	}	
	
	@Override
	public MountSessionModel[] getMountedSessionToMerge(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			MountSession[] sessions = null;
			if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
				sessions = getServiceClient().getFlashServiceR16_5().getMountSessionsToMerge();
			else
				sessions = getServiceClient().getFlashServiceR16_5().getVMMountSessionsToMerge(vmInstanceUUID);
			return convertTOMountSessionModel(sessions);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return null;
	}	
	
	private MountSessionModel[] convertTOMountSessionModel(MountSession[] sessions) {
		if(sessions == null)
			return null;
		MountSessionModel[] models = new MountSessionModel[sessions.length];
		for(int i = 0; i < sessions.length; i ++) {
			models[i] = new MountSessionModel();
			models[i].setSessionNum(sessions[i].getSessionNum());
			models[i].setSessionPath(sessions[i].getSessionPath());
		}
		return models;
	}
	@Override
	public long getServerTimezoneOffset(int year, int month, int day, int hour, int min)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			return getServiceClient().getServiceV2().getServerTimezoneOffset(year, month, day, hour, min);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return -1;	
	}

	@Override
	public long getServerTimezoneOffsetByMillis(long date)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			return getServiceClient().getServiceV2().getServerTimezoneOffsetByMillis(date);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return -1;
	}

	@Override
	public long validateBackupStartTime(int year, int month, int day, int hour, int min)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			
			Long serverTimeZoneOffset = service.getServerTimezoneOffset(year, month, day, hour, min);
			if(service.isTimeInDSTBeginInterval(year, month, day, hour, min)) 
				throw new BusinessLogicException("-1", "");
			if(service.isTimeInDSTEndInterval(year, month, day, hour, min))
				throw new BusinessLogicException("-2", serverTimeZoneOffset.toString());
			
			return serverTimeZoneOffset;
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		
		return 0;
	}
	
	@Override
	public List<RecoveryPointModel> updateSessionPassword(String dest, String domain, String userName, String destPassword, 
			List<EncryptedRecoveryPointModel> sessions)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		List<RecoveryPointModel> wrongModels = new ArrayList<RecoveryPointModel>();
		WebServiceClientProxy client = null;
		
		try {			
			client = getServiceClient();
			if(client != null) {
				String[] sessionGUID = new String[sessions.size()];
				String[] password = new String[sessions.size()];
				String[] passwordHash = new String[sessions.size()];
				//first we need to validate the passwords
				for(int i = 0; i < sessions.size(); i ++) {
					EncryptedRecoveryPointModel model = sessions.get(i);
					String pwd = model.getSessionPwd();
					sessionGUID[i] = model.getSessionGuid();
					password[i] = model.getSessionPwd();
					passwordHash[i] = model.getEncryptPwdHashKey();
					boolean valid = client.getServiceV3().validateSessionPasswordByHash(pwd, pwd.length(), 
							model.getEncryptPwdHashKey(), model.getEncryptPwdHashKey().length());
					if(!valid) {
						model.setSessionPwd(null);
						wrongModels.add(model);
					}
				}
				
				if(wrongModels.isEmpty()) {
					//update the session passwords
					client.getServiceV3().updateSessionPassword(dest, domain, userName, destPassword, 
							sessionGUID, password, passwordHash);
				}
			}
		}catch(Exception e) {
			if(e instanceof WebServiceException) {
				this.proccessAxisFaultException((WebServiceException)e);
			}
		}
		
		return wrongModels;
	}
	
	@Override
	public void backupVM(int backupType, String name, BackupVMModel vmModel,
			boolean convert) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			VirtualMachine vm = ConvertToVirtualMachine(vmModel);
			this.getServiceClient().getFlashService(IFlashService_R16_U7.class).backupVMWithFlag(
					backupType, name, vm, convert);
		} catch (WebServiceException e) {
			if (e instanceof SOAPFaultException) {
				SOAPFaultException se = (SOAPFaultException) e;
				if (se.getFault() != null
						&& FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION
								.equals(se.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), se.getMessage());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
				if (se.getFault() != null
						&& FlashServiceErrorCode.VSPHERE_LICENSE_FAILED_CANNOT_CONNECT_SERVER
						.equals(se.getFault().getFaultCodeAsQName()
								.getLocalPart())) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), se.getMessage(),se.getMessage());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			this.proccessAxisFaultException(e);
		}
	}
	
	@Override
	public void backup(int backupType, String name, boolean convert)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			this.getServiceClient().getFlashService(IFlashService_R16_U7.class).backupWithFlag(
					backupType, name, convert);
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
	}
	
	@Override
	public String getLicenseText() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			return this.getServiceClient().getFlashService(IFlashService_Oolong1.class).getLicenseText();
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return "";
	}
	
	@Override
	public void cancelvAppChildJobs(String vmInstanceUUID, long jobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getServiceV2().cancelvAppChildVMJob(vmInstanceUUID, jobType);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}
	@Override
	public void waitUntilvAppChildJobCancelled(String vmInstanceUUID, long jobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getServiceV2().waitUntilvAppChildJobCancelled(vmInstanceUUID, jobType);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}
	
	@Override
	public void cancelGroupJob(String vmInstanceUUID, long jobID, long jobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			getServiceClient().getServiceV2().cancelGroupJob(vmInstanceUUID, jobID, jobType);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}
	// May sprint
	@Override
	public int collectDiagnosticInfo(DiagInfoCollectorConfiguration config)
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException {
		try {
			//return this.getServiceClient().getServiceV2().collectDiagnosticInfo(config);
			return 1;
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return 0;
	}
	
	@Override
	public DiagInfoCollectorConfiguration getDiagInfoFromXml()
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException {
		try {
			return this.getServiceClient().getServiceV2().getDiagInfoFromXml();
			
		} catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return null;
	}

	@Override
	public boolean isExchangeGRTFuncEnabled() {
		boolean result = false;
		
		try {
			String envValue = System.getenv("ARCUDP_EN_EXGRT");
			if (null == envValue) {
				return result;
			}	
			result = Boolean.TRUE.toString().equalsIgnoreCase(envValue); 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
}
