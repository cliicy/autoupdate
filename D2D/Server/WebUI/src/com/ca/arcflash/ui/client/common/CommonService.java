package com.ca.arcflash.ui.client.common;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/common")
 public interface CommonService extends RemoteService{
	
	 PagingLoadResult<LogEntry> getActivityLogs(PagingLoadConfig config) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	 PagingLoadResult<LogEntry> getJobActivityLogs(long jobNo, PagingLoadConfig config) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 void deleteActivityLog(Date date) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	 void setDeploymentServers(List<ServerInfoModel> store)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;

	 List<ServerInfoModel> getDeploymentServers()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;

	 void startDeploymentServers(List<String> listServers)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	 void backup(int backupType, String name) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 boolean isOnlyFullBackup() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 boolean isBackupEncryptionAlgorithmAndKeyChanged(int encryptionAlgorithm, String encryptionKey) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 boolean isBackupCompressionLevelChangedWithLevel(int compressionLevel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 JobMonitorModel getJobMonitor(String jobType,Long jobId) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 JobMonitorModel[] getJobMonitorMap() throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	
	 JobMonitorModel[] getVMJobMonitorMap(BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	
	 JobMonitorHistoryItemModel[] getJobMonitorHistory() throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;

	DeployUpgradeInfoModel validDeploymentServer(ServerInfoModel serverInfoModel) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	void cancelJob(long jobID) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	void logout() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;

	long validateDest(String path, String domain, String user, String pwd, int mode)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	long validateSource(String path, String domain, String user, String pwd, int mode) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	long validateSource(String path, String domain, String user, String pwd, int mode, boolean isNeedCreateFolder)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;

	boolean isLocalHost(String host) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException;

	long validateCopyDest(String path, String domain, String user, String pwd)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	long getDestDriveType(String path)throws BusinessLogicException, ServiceConnectException,
		ServiceInternalException;
	
	long getDestDriveTypeForModeType(String path,int mode)throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	
	Boolean checkRemotePathAccess(String path, String domain, String user, String pwd)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void disconnectRemotePath(String path, String domain, String user, String pwd, boolean force)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	TrustHostModel validateRemoteServer(TrustHostModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void addTrustHost(TrustHostModel trustHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	ExternalLinksModel getExternalLinks(String lanuage, String country) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void removeTrustedHost(TrustHostModel trustHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean checkBLILic() throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException ;
	
	DestinationCapacityModel getDestCapacity(String destination, String domain, String userName, String pwd) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	Boolean isYouTubeVideoSource()  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void setYouTubeVideoSource(Boolean b)  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<NetworkPathModel> getMappedNetworkPath(String userName)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	long getMaxRPLimit();
	
	boolean IsPatchManagerRunning() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	PatchInfoModel checkUpdate()throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	BIPatchInfoModel checkBIUpdate()throws BusinessLogicException, ServiceConnectException, ServiceInternalException;//added by cliicy.luo
	
	@Deprecated
	PatchInfoModel SubmitRequest(int in_iRequestType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	boolean IsPatchManagerBusy() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	int getPatchManagerStatus() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	PatchInfoModel getUpdateInfo() throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	BIPatchInfoModel getBIUpdateInfo() throws BusinessLogicException,ServiceConnectException,ServiceInternalException;//added by cliicy.luo
	
	EncryptionLibModel getEncryptionAlgorithm();

	boolean validateSessionPassword(String password, String destination, long sessionNum)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean validateSessionPasswordByHash(String password, long pwdLen, String hashValue, long hashLen)
	
	throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String[] getSessionPasswordBySessionGuid(String[] sessionGuid)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	int addVirtualCenter(VirtualCenterModel vcModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	int removeVirtualCenter(VirtualCenterModel vcModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	int validateVirtualCenter(VirtualCenterModel vcModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	void backupVM(int backupType, String name, BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	JobMonitorModel getVMJobMonitor(String vmInstanceUUID,String jobType,Long jobId)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	void cancelVMJob(long jobID, BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;
	boolean isVMCompressionLevelChagned(BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	 PagingLoadResult<LogEntry> getVMActivityLogs(PagingLoadConfig config,BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 PagingLoadResult<LogEntry> getVMJobActivityLogs(long jobNo, PagingLoadConfig config,BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 void deleteVMActivityLog(Date date,BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	 JobMonitorModel getArchiveJobMonitor() throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 JobMonitorModel getArchiveRestoreJobMonitor() throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 Long ValidateArchiveSource(ArchiveDiskDestInfoModel sourceInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 String getMntPathFromVolumeGUID(String strGUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 MountSessionModel[] getMountedSession() throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	 MountSessionModel[] getMountedSessionToMerge(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;	
	 MountSessionModel[] getMountedSessionByDest(String currentDest) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	long getServerTimezoneOffset(int year, int month, int day, int hour, int min) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	long getServerTimezoneOffsetByMillis(long date) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	long validateBackupStartTime(int year, int month, int day, int hour, int min) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	String getSymbolicLinkActualPath(String archiveSource) throws BusinessLogicException,ServiceConnectException,ServiceInternalException, SessionTimeoutException;
	void backup(int backupType, String name, boolean convert) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	List<RecoveryPointModel> updateSessionPassword(String dest, String domain, String userName, String password, List<EncryptedRecoveryPointModel> models) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
		void backupVM(int backupType, String name, BackupVMModel vmModel, boolean convert)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	void cutAllRemoteConnections()throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	long validateDestOnly(String path, String domain, String user, String pwd, int mode) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	JobMonitorModel[] getJobMonitorMapByPolicyId(String policyId) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	String getLicenseText() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	void cancelvAppChildJobs(String vmInstanceUUID, long jobType) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;;
	void waitUntilvAppChildJobCancelled(String vmInstanceUUID, long jobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	void cancelGroupJob(String vmInstanceUUID, long jobID, long jobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	// May sprint
	int collectDiagnosticInfo(DiagInfoCollectorConfiguration config)
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException;
	boolean isExchangeGRTFuncEnabled();
	DiagInfoCollectorConfiguration getDiagInfoFromXml()
			throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException;
}
