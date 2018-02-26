package com.ca.arcflash.ui.client.common;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
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
import com.ca.arcflash.ui.client.model.MountSessionModel;
import com.ca.arcflash.ui.client.model.NetworkPathModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.ServerInfoModel;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionLibModel;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServiceAsync {
	
	void getActivityLogs(PagingLoadConfig config, AsyncCallback<PagingLoadResult<LogEntry>> callback) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	void getJobActivityLogs(long jobNo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<LogEntry>> callback) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void deleteActivityLog(Date date,AsyncCallback<Void> callback);

	void setDeploymentServers(List<ServerInfoModel> serverInfolist,
			AsyncCallback<Void> asyncCallback);

	void getDeploymentServers(
			AsyncCallback<List<ServerInfoModel>> callback);

	void startDeploymentServers(List<String> listServers,
			AsyncCallback<Void> callback);
	
	void backup(int backupType, String name, AsyncCallback<Void> callback);
	
//	void isCompressionLevelChagned(AsyncCallback<Boolean> callbac);
	void isOnlyFullBackup(AsyncCallback<Boolean> callback);
	
	void isBackupEncryptionAlgorithmAndKeyChanged(int encryptionAlgorithm, String encryptionKey, AsyncCallback<Boolean> callback);
	
	void isBackupCompressionLevelChangedWithLevel(int compressionLevel,AsyncCallback<Boolean> callbac);
	
//	void getJobMonitor(AsyncCallback<JobMonitorModel> callbac);

	void validDeploymentServer(ServerInfoModel serverInfoModel,
			AsyncCallback<DeployUpgradeInfoModel> baseAsyncCallback);
	
	void cancelJob(long jobID, AsyncCallback<Void> baseAsyncCallback);
	
	void logout(AsyncCallback<Void> baseAsyncCallback);

	void validateDest(String path, String domain, String user, String pwd, int mode,
			AsyncCallback<Long> callback);

	void validateSource(String path, String domain, String user, String pwd, int mode, AsyncCallback<Long> callback);
	
	void validateSource(String path, String domain, String user, String pwd, int mode, boolean isNeedCreateFolder,
			AsyncCallback<Long> callback);
			
	void isLocalHost(String host, AsyncCallback<Boolean> callback);

	void validateCopyDest(String path, String domain, String user, String pwd,
			AsyncCallback<Long> callback);
	
	void getDestDriveType(String path, AsyncCallback<Long> callback);
	
	void getDestDriveTypeForModeType(String path,int mode,AsyncCallback<Long> callback);
	
	void checkRemotePathAccess(String path, String domain, String user, String pwd,
			AsyncCallback<Boolean> callback);
	
	void disconnectRemotePath(String path, String domain, String user, String pwd, boolean force,
			AsyncCallback<Void> callback);
	
	void validateRemoteServer(TrustHostModel model,AsyncCallback<TrustHostModel> callback);
	
	void addTrustHost(TrustHostModel trustHost, AsyncCallback<Void> callback);
	
	void getExternalLinks(String language, String country, AsyncCallback<ExternalLinksModel> callback);
	
	void removeTrustedHost(TrustHostModel trustHost, AsyncCallback<Void> callback);
	
	void checkBLILic(AsyncCallback<Boolean> callback);
	
	void getDestCapacity(String destination, String domain, String userName, String pwd, AsyncCallback<DestinationCapacityModel> callback);
	
	void isYouTubeVideoSource(AsyncCallback<Boolean> asyncCallback);
	
	void setYouTubeVideoSource(Boolean b, AsyncCallback<Void> asyncCallback);
	
	void getMappedNetworkPath(String userName, AsyncCallback<List<NetworkPathModel>> asyncCallback);
	
	void getMaxRPLimit(AsyncCallback<Long> callback);
	void IsPatchManagerRunning(AsyncCallback<Boolean> callback);
	void checkUpdate(AsyncCallback<PatchInfoModel> callback);
	void checkBIUpdate(AsyncCallback<BIPatchInfoModel> callback);//added by cliicy.luo
	
	@Deprecated
	void SubmitRequest(int in_iRequestType, AsyncCallback<PatchInfoModel> callback);
	void IsPatchManagerBusy(AsyncCallback<Boolean> callback);
	void getPatchManagerStatus(AsyncCallback<Integer> callback);
	
	void getUpdateInfo(AsyncCallback<PatchInfoModel> callback);
	void getBIUpdateInfo(AsyncCallback<BIPatchInfoModel> callback);//added by cliicy.luo

	void getEncryptionAlgorithm(AsyncCallback<EncryptionLibModel> callback);
	
	void validateSessionPassword(String password, String destination, long sessionNum, AsyncCallback<Boolean> callback);

	void getJobMonitorHistory(AsyncCallback<JobMonitorHistoryItemModel[]> callback);
	
	void getSessionPasswordBySessionGuid(String[] sessionGuid,
			AsyncCallback<String[]> callback);
	
	void addVirtualCenter(VirtualCenterModel vcModel,AsyncCallback<Integer> callback);
	
	void removeVirtualCenter(VirtualCenterModel vcModel,AsyncCallback<Integer> callback);
	
	void validateVirtualCenter(VirtualCenterModel vcModel,AsyncCallback<Integer> callback);
	void backupVM(int backupType, String name, BackupVMModel vmModel,
			AsyncCallback<Void> callback);
	void getVMJobMonitor(String vmInstanceUUID,String jobType,Long jobId,
			AsyncCallback<JobMonitorModel> callback);
	void cancelVMJob(long jobID,BackupVMModel vmModel, AsyncCallback<Void> callback);
	void isVMCompressionLevelChagned(BackupVMModel vmModel,
			AsyncCallback<Boolean> callback);

	void getVMActivityLogs(PagingLoadConfig config, BackupVMModel vmModel,
			AsyncCallback<PagingLoadResult<LogEntry>> callback)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	void getVMJobActivityLogs(long jobNo, PagingLoadConfig config,
			BackupVMModel vmModel, AsyncCallback<PagingLoadResult<LogEntry>> callback)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	void deleteVMActivityLog(Date date, BackupVMModel vmModel,
			AsyncCallback<Void> callback);
	void getArchiveJobMonitor(AsyncCallback<JobMonitorModel> callback);
	void getArchiveRestoreJobMonitor(AsyncCallback<JobMonitorModel> callback);
	void ValidateArchiveSource(ArchiveDiskDestInfoModel sourceInfo,
			AsyncCallback<Long> callback);
	void getSymbolicLinkActualPath(String archiveSource,AsyncCallback<String> callback);
	void getJobMonitorMap(AsyncCallback<JobMonitorModel[]> callback);
	void getJobMonitor(String jobType,Long jobId, AsyncCallback<JobMonitorModel> callback);
	void getVMJobMonitorMap(BackupVMModel vmModel,
			AsyncCallback<JobMonitorModel[]> callback);
	void validateSessionPasswordByHash(String password, long pwdLen,
			String hashValue, long hashLen, AsyncCallback<Boolean> callback);
	void getMntPathFromVolumeGUID(String strGUID, AsyncCallback<String> callback);
	void getMountedSession(AsyncCallback<MountSessionModel[]> callback);
	void getServerTimezoneOffsetByMillis(long date, AsyncCallback<Long> callback);
	void getServerTimezoneOffset(int year, int month, int day, int hour,int min,
			AsyncCallback<Long> callback);
	void validateBackupStartTime(int year, int month, int day, int hour, int min, AsyncCallback<Long> callback);
	void getMountedSessionByDest(String currentDest,
			AsyncCallback<MountSessionModel[]> callback);
	void backup(int backupType, String name, boolean convert,
			AsyncCallback<Void> callback);
	void backupVM(int backupType, String name, BackupVMModel vmModel,
			boolean convert, AsyncCallback<Void> callback);
	void updateSessionPassword(String dest, String domain, String userName, String password, 
			List<EncryptedRecoveryPointModel> models,
			AsyncCallback<List<RecoveryPointModel>> callback);
	void getMountedSessionToMerge(String vmInstanceUUID, AsyncCallback<MountSessionModel[]> callback);
	
	void cutAllRemoteConnections(AsyncCallback<Void> callback);
	
	void validateDestOnly(String path, String domain, String user, String pwd,
			int mode, AsyncCallback<Long> callback);
	void getJobMonitorMapByPolicyId(String policyId,
			AsyncCallback<JobMonitorModel[]> baseAsyncCallback);
	void getLicenseText(AsyncCallback<String> baseAsyncCallback);
	void cancelvAppChildJobs(String vmInstanceUUID, long jobType, AsyncCallback<Void> callback);
	void waitUntilvAppChildJobCancelled(String vmInstanceUUID, long jobType, AsyncCallback<Void> callback);
	void cancelGroupJob(String vmInstanceUUID, long jobID, long jobType, AsyncCallback<Void> callback);
	// May sprint
	void collectDiagnosticInfo(DiagInfoCollectorConfiguration config, AsyncCallback<Integer> callback);	
	void getDiagInfoFromXml(AsyncCallback<DiagInfoCollectorConfiguration> callback);	
	void isExchangeGRTFuncEnabled(AsyncCallback<Boolean> callback);
}
