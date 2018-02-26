package com.ca.arcflash.webservice.jni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.jobscript.failover.DNSUpdaterParameters;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.service.jni.model.JActivityLogResult;
import com.ca.arcflash.service.jni.model.JBackupItem;
import com.ca.arcflash.service.jni.model.JMergeActiveJob;
import com.ca.arcflash.service.jni.model.JMergeData;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.service.jni.model.JSystemInfo;
import com.ca.arcflash.service.jni.model.MergeJobScript;
import com.ca.arcflash.webservice.common.VCMMachineInfo;
import com.ca.arcflash.webservice.data.DataSizesFromStorage;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.HyperVDestinationInfo;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.data.ad.ADAttribute;
import com.ca.arcflash.webservice.data.ad.ADNode;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.catalog.GRTBrowsingContext;
import com.ca.arcflash.webservice.data.catalog.PagedGRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.restore.AlternativePath;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.jni.model.ArchiveJobMinitor;
import com.ca.arcflash.webservice.jni.model.ArchiveSession;
import com.ca.arcflash.webservice.jni.model.IPSettingDetail;
import com.ca.arcflash.webservice.jni.model.IRemoteDeployCallback;
import com.ca.arcflash.webservice.jni.model.JApplicationStatus;
import com.ca.arcflash.webservice.jni.model.JApplicationWriter;
import com.ca.arcflash.webservice.jni.model.JArchiveCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JBackupDestinationInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JBackupVM;
import com.ca.arcflash.webservice.jni.model.JBackupVMOriginalInfo;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JCatalogInfo;
import com.ca.arcflash.webservice.jni.model.JCatalogJob;
import com.ca.arcflash.webservice.jni.model.JDisk;
import com.ca.arcflash.webservice.jni.model.JExchangeDiscoveryItem;
import com.ca.arcflash.webservice.jni.model.JHostNetworkConfig;
import com.ca.arcflash.webservice.jni.model.JIVMJobStatus;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.jni.model.JMountedRecoveryPointItem;
import com.ca.arcflash.webservice.jni.model.JMsgRec;
import com.ca.arcflash.webservice.jni.model.JObjRet;
import com.ca.arcflash.webservice.jni.model.JPFCVMInfo;
import com.ca.arcflash.webservice.jni.model.JPagedCatalogItem;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JSearchResult;
import com.ca.arcflash.webservice.jni.model.JVAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.jni.model.JWindowsServiceModel;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.arcflash.webservice.data.VMwareConnParams;
import com.ca.arcflash.webservice.data.VMwareVolumeInfo;
import com.ca.ha.webservice.jni.JHyperVSystemInfo;

public interface NativeFacade {

	int[] getLastArchiveToTapeSession(String backupDest, String userName, String password);	
	int validateUser(String username, String password, String domain) throws Throwable;

	String browseVSSApplications() throws Throwable;

	String getVolumes(boolean details, String backupDest, String userName, String passwd) throws Throwable;

	List<JCatalogDetail> getCatalogItems(String filepath, long parentID) throws Throwable;

	SearchContext openSearchCatalog(String destination,String sDir, boolean bCaseSensitive, boolean bIncludeSubDir, String pattern) throws Throwable;

	JSearchResult searchNext(SearchContext sContext) throws Throwable;

	void closeSearchCatalog(SearchContext sContext) throws Throwable;

	long backup(JJobScript backupJob) throws Throwable;

	long getD2DFileSize(String filePath, String user, String pwd, String d2dFile, int startSessNo, int endSessNo, int nBackupDescType) throws Throwable;

	JRestorePoint[] getRestorePoints(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate, boolean isQueryDetail) throws Throwable;
	
	//zxh,mark:ASBUGetRecoveryPoint
	JRestorePoint[] getRestorePoints4ASBU(String destination, String domain, String userName, String pwd, Date beginDate,	Date endDate, boolean isQueryDetail) throws Throwable;

	long restore(JJobScript job) throws Throwable;

	JBackupInfoSummary GetBackupInfoSummary(String destination, String domain, String userName, String pwd, boolean onlyDestCapacity) throws Throwable;

	JBackupInfoSummary GetBackupInfoSummary(String destination, String domain, String userName, String pwd, boolean onlyDestCapacity,String foldername) throws Throwable;

	JBackupDestinationInfo GetDestSizeInformation(String destination,String domain, String userName, String pwd) throws Throwable;

	JProtectionInfo[] GetProtectionInformation(String destination, String domain, String userName, String pwd) throws Throwable;
	
	JProtectionInfo[] GetCopyProtectionInformation(String destination, String domain, String userName, String pwd) throws Throwable;

	JBackupInfo[] getMostRecentRecoveryPoints(String destination, String domain, String userName, String pwd, int backupType, int backupStatus, int top,String foldername) throws Throwable;
	
	JBackupInfo[] getRecentBackupsByServerTime(String destination, String domain, String userName, String pwd, int backupType, int backupStatus, Date beginDate, Date endDate, String foldername) throws Throwable;
	
	long isMergeJobAvailableEx(String destination, int retentionCount, int  dailyCount, int weeklyCount, int monthlyCount, String vmUUID, String user, String pwd);
	
	long isMergeJobAvailable(JMergeData mergeInfo);
	
	/**
	 * This function is similar to the getMostRecentRecoveryPoints, with the exception that it takes into consideration with begin data and enddate
	 * @param destination
	 * @param domain
	 * @param userName
	 * @param pwd
	 * @param beginDate
	 * @param endDate
	 * @param top
	 * @return
	 * @throws Throwable
	 */
	JBackupInfo[] getRecoveryPoints(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate,int top) throws Throwable;

	void addLogActivity(long level, long resourceID, String... param);
	void addLogActivityWithJobID(long level, long jobID,long resourceID, String[] param);
	void addVMLogActivity(long level, long resourceID, String[] param,String vmIndentification);
	void addVMLogActivityWithJobID(long level, long jobID,long resourceID, String[] param,String vmIndentification);
	void addLogActivityWithDetailsEx(JActLogDetails logDetails, long resourceID, String[] param);
	JActivityLogResult getActivityLogs(int start, int count);

	JActivityLogResult getJobActivityLogs(long jobNo, int start, int count);

	void deleteActivityLog(int year, int month, int day, int hour, int minute, int second);

	void StartToDeploy(String localDomain, String localUser,
			String localPassword, String uuid, String serverName,
			String username, String password, int port,
			String installDirectory, boolean autoStartRRService, boolean reboot, boolean bInstallDriver,
			boolean useHttps, boolean resumedAndCheck, IRemoteDeployCallback callback);

	String encrypt(String source);

	String decrypt(String source);

	FileFolderItem getFileFolderItem(String path, String userName, String pwd) throws ServiceException;

	boolean isCompressionLevelChanged(String destination, String domain, String username, String pwd, int level);

	boolean isEncryptionAlgorithmAndKeyChanged(String destination, String domain, String username, String pwd, int encryptionAlgorithm, String encryptionKey);

	long initBackupDestination(String newDestination, String newDomain, String newUsername, String newPassword,
			String destination, String domain, String username, String pwd, long bkpType) throws ServiceException;

	long initCopyDestination(String detination, String domain, String userName, String password) throws ServiceException;

	JJobMonitor GetJobMonitor(long address);

	void releaseJobMonitor(long address);

	long createJobMonitor(long shrmemid);

	DeployUpgradeInfo validRemoteDeploy(String localDomain, String localUser,
			String localPassword,RemoteDeployTarget remoteTarget)throws Throwable;

	long getJobID();

	long getJobIDs(int count);
	
	long getCurrentJobID();

	boolean checkJobExist();

	boolean checkBMRPerformed();

	long copy(JJobScript targetJob) throws Throwable;

	int cancelJob(long jobID) throws Throwable;

	long checkFolderAccess(String path, String domain, String user, String pwd) throws ServiceException;

	long checkDestinationValid(String path) throws Throwable;

	long getPathMaxLength() throws Throwable;
	
	long getVSpherePathMaxLength() throws Throwable;

	JObjRet<String> checkDestNeedHostName(String path, String serverName, String nodeId, String userName, String password, boolean isCreate) throws ServiceException;

	JObjRet<String> checkDestNeedVMInfo(String path, String serverName,String instanceUUID) throws ServiceException;

	void saveAdminAccount(Account account) throws ServiceException;

	Account getAdminAccount() throws ServiceException;

	void validateAdminAccount(Account account)throws ServiceException;

	long getDestDriveType(String path) throws ServiceException;

	long disconnectRemotePath(String path, String domain, String user,
			String pwd, boolean force) throws ServiceException;

	void createDir(String parentPath, String subDir) throws ServiceException;

	boolean checkRemotePathAccess(String path, String domain, String user,
			String pwd)  throws ServiceException;

	JPagedCatalogItem getPagedCatalogItems(String catPath, long parentID,
			int start, int size) throws ServiceException;

	long saveThreshold(long threshold) throws Throwable;

	String findNewDestination(String oldDestination) throws Throwable;

	AlternativePath checkSQLAlternateLocation(String basePath, String instName, String dbName) throws ServiceException;
	boolean checkBLILic();

	boolean checkDirPathValid(String path, String domain, String user, String pwd) throws ServiceException;
	
	boolean validateDirPath(String path, String domain, String user, String pwd) throws ServiceException;

	void checkContainRecoveryPoints(JNetConnInfo connInfo)	throws ServiceException;

	boolean checkBaseLic();

	int getLocalADTPackage();

	List<JApplicationWriter> getExcludedAppComponents(List<String> volumeList) throws ServiceException;

	void regenerateWriterMetadata() throws ServiceException;

	List<String> getLocalDestVolumes(String destPath) throws ServiceException ;

	List<JBackupItem> GetBackupItem(String dest, String domain,
			String user, String pwd, String subPath);

	List<NetworkPath> getNetworkPathForMappedDrive(String userName) throws ServiceException;
	@Deprecated
	LicInfo getLicInfo() throws ServiceException;

	void checkRestoreSession(String sessionPath, String domain,
			String userName, String passwd, List<Integer> list) throws ServiceException;

	public boolean validateSessionPassword(String password, String destination,
			long sessionNum, HttpSession session) throws ServiceException;
	
	public boolean AFValidateSessPasswordByHash(String password, long pwdLen, String hashValue,
			long hashLen, HttpSession session) throws ServiceException;

	public String[] getSessionPasswordBySessionGuid(String[] sessionGuid) throws ServiceException;

	//
	/* Patch Manager */
	//
	public long checkUpdate();
	
	public long checkUpdateEx(int Type);

	public String getUpdateStatusFile();

	public String getUpdateStatusFileEx(int type);
	
	public String getUpdateSettingsFile();

	public String getUpdateSettingsFileEx(int type);
	
	public boolean IsPatchManagerBusy(String busyMutexName);
	
	public boolean IsPatchManagerBusyEx(String busyMutexName, int type);
	
	public long testUpdateServerConnection(	int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	public long testUpdateServerConnectionEx( int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	
	//added by cliicy.luo
	public long testBIUpdateServerConnection(int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	public long testBIUpdateServerConnectionEx( int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	
	public long installBIUpdate( );
	
	public long installBIUpdateEx( int type);
	
	public String getBIUpdateStatusFile();

	public String getBIUpdateStatusFileEx(int type);
	
	public long checkBIUpdate();

	public long checkBIUpdateEx(int Type);
	//added by cliicy.luo
	
	public long installUpdate( );
	
	public long installUpdateEx( int type);
	
	public String getUpdateErrorMessage( int errorCode );
	
	public String getUpdateErrorMessageEx( int errorCode, int type );
	
	@Deprecated
	long GetLastError();

	@Deprecated
	String FormatMessage(int errorCode);

	boolean IsPatchManagerRunning(String runningMutexName);

	//
	/*Patch Manager End*/
	//
	
	String GetCacheFile4Sync();

	long DeleteCacheFile4Sync();

	String GetReSyncData();

	String GetD2DSysFolder();

	int cancelVMJob(long jobID,String vmIdentification) throws Throwable;

	long createVMJobMonitor(String vmIndentification);

	List<JBackupVM> getBackupVMList(String destination,String domain,String username,String password) throws ServiceException;
	
	JBackupVM getBackupVM(String destination,String domain,String username,String password) throws ServiceException;
	
	List<JVAppChildBackupVMRestorePointWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination,
			int vAppSessionNumer, String domain, String username, String password) throws ServiceException;

	boolean checkVMDestination(String destination,String domain,String username,String password) throws ServiceException;

	List<JDisk> getBackupVMDisk(String destination,String subPath,String domain,String username,String password) throws ServiceException;

	JBackupVMOriginalInfo getBackupVMOriginalInfo(String destination,int sessionNum,String domain,String username,String password) throws ServiceException;
	
	JActivityLogResult getVMActivityLogs(int start, int count,String vmUUID);
	JActivityLogResult getVMJobActivityLogs(long jobNo, int start, int count,String vmUUID);
	JActivityLogResult GetJobLogActivityForVM(long jobNo, int start, int count,String vmUUID);
	void deleteVMActivityLog(int year, int month, int day, int hour, int minute, int second,String vmUUID);
	String getVDDKVersion();
	String getVIXVersion();
	long NetConn(String username, String password,
			String remoteName) throws ServiceException;

	int NetConnWithLocal(String username, String password,
			String localName, String remoteName);

	long NetCancel(String name, boolean isForce) throws ServiceException;
	long GetAllBackupDestinations(String destDir,List<String> destinations);
	//if destDir is UNC, use username and password for auth
	long getReplicatedSessions(String destDir,List<String> destinations,String serverName, String serverPort, String username,String password);

	long HyperVRep(HyperVRepParameterModel prams);

	long ConvertVHD2VMDK(String afGuid,String vhdFileName,String moref,
			  String hostname,String username,String password,
			  String diskURL,int vddkPort,VMwareConnParams exParams, int diskType, String snapMoref ,String jobID,
			  long blockSize, int nBackupDescType, String NetConnUserName, String NetConnPwd, List<Long> errorCode);
	
	public long UpdateDiskSigViaNBD(String afGuid,String vhdFileName,String moref, String hostname,String username,String password,
				String diskURL,int vddkPort,VMwareConnParams exParams, int diskType,String snapMoref,String jobID, int nBackupDescType,String netConnUserName,String netConnPwd);

	public long D2D2VmdkSmartCopy(String afGuid,String d2dFilePathBegin, String d2dFilePathEnd,
			    String moref, String hostname,String username,String password,
			    String diskURL,int vddkPort,VMwareConnParams exParams, int diskType,String snapMoref,String jobID,
			    long blockSize, int nBackupDescType, String NetConnUserName, String NetConnPwd,List<Long> errorCode);

	String GetVMDKSignature(String afguid, String esxHost,String esxUser,String esxPassword,
							String moreInf,int port, VMwareConnParams exParams, String snapMoref, String vmdkUrl,String jobID);

	long SetVMDKGeometry(String esxHost,String esxUser,String esxPassword,
			 String moreInf,int port,VMwareConnParams exParams, String vmdkUrl, long volOffset, String JobID, String afguid);

	long DoVMWareDriverInjection(String esxServer,String username, String password,
									  String morefId, int adminPort, VMwareConnParams exParams, List<String> vmdkUrls,
									  VMwareVolumeInfo volumeInfo,String hostname,String failoverMode,
									  String key,String value,String jobID, String afguid, boolean isUEFI, String drzFilePath);
	
	long DriverInjectSingleVMDK(String esxServer,String username, String password,
			   String morefId, int adminPort, VMwareConnParams exParams, String vmdkUrl,
			   String hostname,String failoverMode,
			   String key,String value,String jobID, String afguid);
	
	

	String GetGuestID();

	void InstallVMwareTools();

	Map<String, String> GetHostAdapterList();

	void EnableHostDHCP(String adapterName);

	void EnableHostStatic(String adapterName, List<String> ipAddresses, List<String> vMasks);

	void EnableHostDNS(String adapterName);

	void SetHostDNSDomain(String adapterName,String dnsDomain);

	void SetHostGateways(String adapterName, List<String> gateways, List<Integer> costMetrics);

	void SetHostDNSServerSearchOrder(String adapterName, List<String> vDNSServerSearchOrder);

	public int GetMaxCPUSForHypervVm(long handle) throws ServiceException;

	public long GetMaxRAMForHypervVm(long handle) throws ServiceException;
	
	public long GetHyperVSystemInfo(long handle, JHyperVSystemInfo hyperVSysInfo) throws ServiceException;

	public long OpenHypervHandle(String host, String user, String password) throws ServiceException;

	public void CloseHypervHandle(long handle) throws ServiceException;

	public Map<String,String> GetHyperVVmList(long handle)	throws ServiceException;
	
	public String GetHyperVVmNotes(long handle, String vmGuid)	throws ServiceException;

	public int GetHyperVVmState(long handle, String vmGuid) throws ServiceException;

	public Map<String,String> GetHyperVVmSnapshots(long handle, String vmGuid) throws ServiceException;

	public int PowerOnHyperVVM(long handle, String vmGuid)	throws ServiceException;

	public int ShutdownHyperVVM(long handle, String vmGuid)	throws ServiceException;

	public String TakeHyperVVmSnapshot(long handle, String vmGuid, String snapshotName,
			String snapshotNotes) throws ServiceException;

	public Vector<Disk> getOnlineDisks() throws ServiceException;

	public Vector<Volume> GetOnlineVolumes() throws ServiceException;
	
	public boolean HAIsHostOSGreaterEqual(int dwMajor,int dwMinor,short servicePackMajor,
			 short servicePackMinor) throws ServiceException;
	
	public int CheckFolderCompressAttribute(String folderPath) throws ServiceException;
	
	public int CheckVolumeCompressAttribute(String volumePath) throws ServiceException;
	
	
	public int GetOnlineDisksAndVolumes(Vector<Disk> vecDisk, Vector<Volume> vecVolume) throws ServiceException;

	public Vector<JHostNetworkConfig> GetHostNetworkConfig() throws ServiceException;

	public short GetHostProcessorArchitectural() throws ServiceException;

	public long GetOfflinecopySize(String rootDest) throws ServiceException;

	public int AdjustVolumeBootCodeForHyperV(String pwszVmGuid, String pwszSnapGuid) throws ServiceException;

	public void CancelReplicationForHyperV(String jobId) throws ServiceException;

	public void stopHAServerProxy() throws ServiceException;

	public void CancelReplicationForVMware(String jobId) throws ServiceException;
	
	public int OnlineDisks() throws ServiceException;

	public String GetLastSnapshotForHyper(long handle, String vmGuid)throws ServiceException;

	public int generateAdrconfigure(String dest)throws ServiceException;

	public int generateIVMAdrconfigure(String dest, String vmuuid, String vmname)throws ServiceException;
	
	public String GenerateAdrInfoC()throws ServiceException;	
	
	public String GetDrInfoLocalCopyPathForSnapNow_HyperV(String vmname,String vmuuid) throws ServiceException;
	
	public String GetAdrInfoCLocalCopyPathForSnapNow_HyperV(String vmname,String vmuuid) throws ServiceException;

	String GetDestSubRoot(HyperVRepParameterModel parameterModel) throws ServiceException;

	// chefr03: SMART_COPY_BITMAP
	// Create bitmap for the newest session
	public int CreateSessionBitmap(String srcSessionDest, String destSessionDest, int nBackupDescType) throws ServiceException;
	// Delete bitmaps for specified session and all older sessions
	public int DeleteSessionBitmap(String sessionDest, String sessionName) throws ServiceException;
	// Get the session name list for bitmaps
	public int GetSessionBitmapList(String sessionDest, List<String> vBitmapList) throws ServiceException;
	// chefr03: SMART_COPY_BITMAP
	public int DetectAndRemoveObsolteBitmap(String sessionDest,String beginSession, String endSession);
	
	/**
	 * Refresh the DNS of localhost and return the ips of the <code>hostName</code>.
	 * @param hostName
	 * @return the ip list of <code>hostName</code>
	 */
	List<String> getIpAddressFromDns(String hostName);

	List<JMsgRec> getGRTCatalogItems(String filepath, long lowSelfid, long highSelfid) throws ServiceException;
	String getMsgCatalogPath(String dbIdentify, String backupDestination, long sessionNumber, long subSessionNumber) throws ServiceException;

	PagedGRTCatalogItem getPagedGRTCatalogItems(String catPath,
			long lowSelfID, long highSelfID, int start, int size)
			throws ServiceException;

	long d2dExCheckUser(String domain, String user, String password) throws ServiceException;
	JSearchResult searchMsgNext(SearchContext context);

	String aoeGetOrganizationName(String strUser, String strPassword);
	List<JExchangeDiscoveryItem> aoeGetServers(String strUser, String strPassword) throws ServiceException;
	List<JExchangeDiscoveryItem> aoeGetStorageGroups(String dn, String strUser, String strPassword) throws ServiceException;
	List<JExchangeDiscoveryItem> aoeGetEDBs(String dn, String strUser, String strPassword) throws ServiceException;
	List<JExchangeDiscoveryItem> aoeGetMailboxes(String dn, String strUser, String strPassword) throws ServiceException;

	long aoeCheckServiceStatus(String serviceName);

	long createDesktopINI(String backupDestination) throws Throwable;

	PagedGRTCatalogItem browseGRTCatalog(GRTBrowsingContext context) throws ServiceException;

	@Deprecated
	void closeBrowseGRTCatalog(GRTBrowsingContext context) throws ServiceException;

	long catalogJob(JCatalogJob job);

	boolean isCatalogJobRunning(String backupDestination, long sessionNumber, long subSessionNumber);

	boolean isCatalogJobInQueue(long queueType, String backupDestination, long sessionNumber, long subSessionNumber, String vmInstanceUUID);

	long validateCatalogFileExist(String dbIdentify, String backupDestination, long sessionNumber, long subSessionNumber);

	boolean isVolumeMounted(String volName);

	public void RebootSystem(boolean force) throws ServiceException;

	String getHAConfigurationFileURL(String lastRepDest, String fileName) throws ServiceException;

	String getHAConfigurationFileURLByVMGUID(String vmGUID, String fileName) throws ServiceException;

	public String GetD2DActiveLogTransFileXML();

	public long DelD2DActiveLogTransFileXML();

	public String GetFullD2DActiveLogTransFileXML();

	long CheckSessVerByNo(String destination, long sessionNumber);

	boolean IsFirstD2DSyncCalled();

	void MarkFirstD2DSyncCalled();

	ArchiveJobMinitor getArchiveJobStatus(long jobType)throws ServiceException;
	ArchiveSession getArchiveSession()throws ServiceException;

	//archive restore api's
	List<String> getArchiveDestinationVolumes(String hostName,ArchiveDestinationConfig archiveDestConfig) throws ServiceException;

	long getArchivedVolumeHandle(String strVolume);

		List<JArchiveCatalogDetail> getArchiveCatalogItems(long inVolumeHandle,
			String inCatalogFilePath, long lIndex, long lCount) throws Throwable;

	List<JArchiveCatalogDetail> getArchiveCatalogItems(long inVolumeHandle,
			String inCatalogFilePath) throws Throwable;

	long GetArchiveChildrenCount(long lVolumeHandle, String string,
			JRWLong childrenCnt);

	long archive(ArchiveJobScript jobScript) throws Throwable;
	long archiveRestore(ArchiveJobScript job) throws Throwable;
	long archiveCatalogSync(ArchiveJobScript job) throws Throwable;

	long CanArchiveJobBeSubmitted(JArchiveJob out_jArchiveJob)throws Throwable;
	long CanArchiveSourceDeleteJobBeSubmitted(JArchiveJob out_jArchiveJob)throws Throwable;

	long purge(ArchiveJobScript jobScript) throws Throwable;
	long getArchivableFilesInformation(String archiveSourcePoliciesFilePath,List<ArchiveFileItem> filesList)throws Throwable;

	//archive search api's
	List<JArchiveCatalogDetail> getArchiveCatalogItemsBySearch(String in_fileName,String in_HostName,String inSearchpath, ArchiveDestinationConfig in_archiveDestConfig,long in_lSearchOptions, long lIndex, long lCount) throws Throwable;

	List<ArchiveJobInfo> GetArchiveJobsInfo(JArchiveJob outArchiveJob) throws Throwable;

	boolean IsArchiveJobRunning() throws Throwable;
	boolean IsArchiveRestoreJobRunning() throws Throwable;
	boolean IsArchivePurgeJobRunning() throws Throwable;
	boolean IsArchiveCatalogSyncJobRunning() throws Throwable;
	boolean IsFileArchiveJobRunning() throws Throwable;
	String GetArchiveDNSHostName() throws Throwable;
	String GetArchiveDNSHostSID() throws Throwable;
	
	public ArchiveDestinationDetailsConfig getArchiveChangedDestinationDetails(ArchiveDestinationConfig in_archiveDestConfig) throws Throwable;

    public List<JCatalogInfo> AFSCheckCatalogExist(String destination, long sessionNumber) throws ServiceException;

    public  String GetCachedVmInfo4Trans();
    public  String GetAllVmInfo4Trans();
    public  long DeleteVmInfoTransFile();
    public  long DeleteAllVmInfoTransFile();
    public  String GetArchiveCacheFileName4Trans();
    public  long DeleteArchiveCacheFileTrans();
 //cloud test
	public long verifyBucketName(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	public String[] getCloudBuckets(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	public String[] getCloudRegions(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	public String getRegionForBucket(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	
	public String getCACloudStorageKey(ArchiveCloudDestInfo cloudDestInfo, String userName, String password)throws ServiceException;
	public CloudProviderInfo getCloudProviderInfo(long cloudProviderType) throws ServiceException;
	public String updateStorageKey(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException;
	public String getGeminarePortalURL(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException;

	//scheduled export
    long enabledScheduledExport(boolean enabled);
    boolean checkScheduledExportInterval(int interval);
    long addSucceedBackupNum();
    String getSessPwdFromKeyMgmtBySessNum(long sessionNum, String destPath);
    //VCM
    long MountVHDGetWinSysBootVol(long handle,String vhdFile,List<String> bootVolumePaths,List<String> systemVolume);
    long HAUnlockCTF2(long handle);
    long GetVDDKRegistryKey(String keyName);
    long launchVSphereCatalogJob(long jobId, long type, String vmIndentification,String adminName, String adminPass);
    boolean checkRecoveryVMJobExist(String vmName,String esxServerName);
    boolean AFCheckFolderContainsBackup(String domain, String userName, String password, String destination) throws ServiceException;
    long getHyperVVMPathType(String vmPath);
    void getHyperVDestInfo(List<String> vmPathList, HyperVDestinationInfo retObj);
    /**
     * Returns the result whether VMware Tools is installed or not and whether VMware Tools service is running.
     * @return  if (resust & 1) > 0, VMware Tools is installed.
     * 			if (resust & 2) > 0, VMware Tools service is running.
     * @throws ServiceException
     */
    int getVMwareToolStatus() throws ServiceException;
    long getVMApplicationStatus(String vmInstanceUUID,JApplicationStatus appStatus);
    ArrayList validateEncryptionSettings(ArchiveJobScript in_archiveConfig) throws ServiceException;
    long deleteAllPendingFileCopyJobs(String destination, String destinationDomain, String destUserName, String destPassword)throws ServiceException;
    
    String getSymbolicLinkActualPath(String sourcePath)throws ServiceException;
    
    LicInfo AFGetLicenseEx(boolean filterLicInfo) throws ServiceException;

	void cutAllRemoteConnections() throws ServiceException;
	
	long deleteLicError(long licCode);
	
	public long CreateEvent(String eventName, boolean manualReset, boolean initialState);
    
    public void SetEvent(long handle);
    
    public void ResetEvent(long handle);
    
    /**
     * @param handle
     * @param milliSeconds: milliSeconds is -1, the function's time-out interval never elapses.
     * @return 0: event is signaled; 1: The time-out interval elapsed, and the object's state is nonsignaled.
     */
    public int WaitForSingleObject(long handle, long milliSeconds);
    
    
    long createMutex(boolean initiallyOwned, String mutexName);
    void releaseMutex(long handle);

    public long getApplicationDetailsInESXVM(
			String esxServerName, String esxUserName, String esxPassword,
			String vmName, String vmVMX, String userName, String password);

    public long getVMInformation(JPFCVMInfo vmInfo,
			String esxServerName, String esxUserName, String esxPassword,
			String vmName, String vmVMX, String userName, String password);
    public long setPreAllocSpacePercent(long dwPercent) throws ServiceException;
	public long getPreAllocSpacePercent() throws ServiceException ;
	
	public boolean IsFirmwareuEFI();
    
    public String GetRestorePointPath(String dest, String domain, String user,String pwd, String subPath);
    
    //D2D mount volume API begin
    public List<JMountedRecoveryPointItem> getAllMountedRecoveryPointItems();
	
    public List<JMountedRecoveryPointItem> getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath);
	
    @Deprecated
	public long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath, String volGUID,int encryptionType,String encryptPassword, String mountPath);
    
    public long mountRecoveryPointItem( JMountRecoveryPointParams jMntParams );
	
	public long disMountRecoveryPointItem(String mountPath,int mountDiskSignature);
	
	public List<String> getAvailableMountDriveLetters();
	
	public String getWindowsTempDir();
	
	public List<JMountedRecoveryPointItem> getMntInfoForVolume(String dest,String domain, String user, String pwd, String subPath, String volGUID);
	//end
	
	public String getErrorMsg(long dwErrCode);
	
	public boolean CheckIfExistDiskLargerThan2T() throws ServiceException;

        long startD2dCallback() throws ServiceException;

	long stopD2dCallback() throws ServiceException;

	boolean isShowUpdate();
	
	public JMountPoint MountVolume(String userName, String passWord,
			String backupDest, long session, String volumeGUID, String encryptionKey) throws ServiceException ;
	
	public int UnMountVolume(JMountPoint mntPoint);
	
	/*zxh,
    * Method:    		VDDismountResBrsVols
    * description: 		this function is used to Dismount ResBrowse Volumn.
    * 
    */
    public long VDDismountResBrsVols(boolean bForceDisMntAllResBrw);

    /*zxh,
    * Class:     		com_ca_arcflash_webservice_jni_WSJNI
    * Method:    		VDUpateVolumeExtData
    * description: 		the param iUpField can used as follow values.
    * 					1:update last access time.
    * 					2:update all fields.
    */
	public long VDUpateVolumeMountTimestamp(String sVolumeSignatureID);

	List<JCatalogDetail> getFileItems(String mountVolGUID, String volumeGUID, String parentPath);

	JPagedCatalogItem getPagedFileItems(String mountVolGUID, String catPath, String parentID,
			int start, int size) throws ServiceException;

	long GetChildCount(long sessNum, String catalogPath, String volumeGUID, String destination,
			String userName, String passWord, String encryptedPwd) throws ServiceException ;
	
	long InitGRTMounter();
	
	int ExitGRTMounter(long handle);
	
	/*pidma02 - support for restore by search using mount point*/
	long SearchMountPoint(String mntVolumeGUID, String backupDestination, String volDisplayName, String sDir, boolean bCaseSensitive, boolean bIncludeSubDir, String pattern);
	
	public JSearchResult FindNextSearchItems(SearchContext context) ;
	
	int FindCloseSearchItems(SearchContext context);

	//pidma02: Support for search in single session for catalog enabled sessions
	SearchContext openSearchCatalogEx(String catalogSessionPath,String sDir, boolean bCaseSensitive, boolean bIncludeSubDir, String pattern) throws Throwable;

	JSearchResult searchNextEx(SearchContext sContext) throws Throwable;

	int GetCatalogStatusForSession(String sessionPath);

	int SetCatalogStatusForSession(String backupDest, String sessionPath);
	
	int UpdateCatalogJobScript(String backupDest, String userName, String passWord, long sessNum, String jobScript, String vmInstanceUUID);

	int updateSessionPasswordByGUID(String[] guids, String[] pwds);
	
	public boolean isAdjustJavaHeapSize(ApplicationType appType);
	
	
	//////////////////////////////////////////////////////////////////////////
	// Manual Conversion
	
	public long createVssManager(); // returns vssManagerHandle, 0 if error
    public int vssManager_Init( long vssManagerHandle, int flag, boolean isPersistent ); // returns 0 for success, and -1 for failure
    public String vssManager_CreateSnapshotSet( long vssManagerHandle, List<String> volumes ); // returns snapshotSetId
    public int vssManager_DeleteSnapshotSet( long vssManagerHandle, String snapshotSetId ); // returns 0 for success, and -1 for failure
    public long vssManager_GetSnapshotSetByGuid( long vssManagerHandle, String snapshotSetId ); // returns snapshotSetHandle, 0 if error
    public void vssManager_Release( long vssManagerHandle ); // returns 0 for success, and -1 for failure
    
    public String vcmSnapshotSet_QuerySnapshotDeviceName( long snapshotSetHandle, String originalVolumeName ); // returns snapshot volume name
    public void vcmSnapshotSet_Release( long snapshotSetHandle ); // returns 0 for success, and -1 for failure
    	//
	long startMerge(MergeJobScript mjs) throws ServiceException;
	long stopMerge(long jobId) throws ServiceException;
	MergeJobMonitor getMergeJobMonitor(MergeJobMonitor mJM, long handle);
	long createMergeJobMonitor(long jobId);
	long releaseMergeJobMonitor(long handle);
	boolean markBackupSetFlag(String destination, String domain, String userName, String password, long sessionNum, String vmInstanceUUID);
	void unmarkBackupSetFlag(String destination, String domain, String userName, String password, long sessionNum, String vmInstanceUUID);
	long isMergeJobAvailable(int retentionCount, String destination, String vmUUID, String userName, 
			String password);
	JRestorePoint[] getRestorePointsForBackupSet(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate) throws ServiceException;
	//wanqi06
	public List<JMergeActiveJob> getActiveMergeJobInfo();
	//public void vcmSnapshotSet_Release( long snapshotSetHandle ); // returns 0 for success, and -1 for failure
    
    public List<Integer> getIntactSessions( String sessionFolderPath );
    
    public int GetLocalPathOfShareName( String shareName, List<String> localPathList, List<Integer> errorCodeList );
    
    public static enum RHAScenarioState
    {
    	Run,
    	Stop,
    	Sync,
    	Unknown,
    }
    
    public RHAScenarioState getRHAScenarioState( String rootPath ) throws Exception;
    
    boolean IsRHASyncReplicated( String rootPath ) throws Exception;
    
    JSystemInfo getSystemInfo() throws ServiceException;
	

	public long DoRVCMInjectService(
		String esxHostname,
		int esxPort,
		VMwareConnParams exParams,
		String username,
		String password,
		String moRefId,
		VMwareVolumeInfo volumeInfo,
		String failoverMode,
		List<String> vmdkUrlList,
		String iniFolderPath,
		String jobId,
		List<Volume> volumesOnNonBootOrSystemDisk,
		boolean isX86,
		String scriptPath);
	
	public long DoRVCMInjectServiceForHyperV(
		String vmWinDir,
		String winSystemDir,
		List<Volume> volumesOnNonBootOrSystemDisk,
		boolean isX86, 
		String scriptPath);
	long lauchCatalogJob(long jobId, long type,String vmInstanceUUID, String destination, String userName, String password);	
	FileFolderItem getVMVolumes(JBackupVM jBackupVM) throws Throwable;
	
	FileFolderItem getVMFileFolderItem(String path, JBackupVM jBackupVM) throws ServiceException;
	
	void createVMDir(String parentPath, String subDir,JBackupVM jBackupVM) throws ServiceException;
	
	List<Integer> GetSessNumListForNextMerge(int retentionCount, String backDest);
	
	long getFullSessNumber4Incre(long incrSessNumber, String backDest);

	VCMMachineInfo getMachineDetailFromBackupSession(String backupDest);
	
	long getTickCount();
	
	void startNICMonitor();
	
	void stopNICMonitor();
	
	void startClusterMonitor();
	
	void stopClusterMonitor();
	
	boolean isX86();
	
	/**
	 * Get datastore hash key or session encryption key for a specified. 
	 * <br/> A connection to the datastore shared path should be created before call it.
	 * @param sharePath: datastore shared path
	 * @param sessionNumber: the specified session number if you want to get the encryption key for it.
	 * @param sessionPwd: session password for the session encryption key or datastore password for datastore hash key. 
	 * @return If the passed in session number is not 0 and the passed in sessionPwd if the session password, 
	 * it will return the encryption key for that session.<p/>
	 * If the sessinoNumber is 0 and the sessionPwd is datastore password, it will return the datastor hashkey
	 */
	String getRPSDataStoreHashKey(String sharePath, String userName, String password, int sessionNumber, String sessionPwd);
	
	boolean isCatalogExist(String backupdest, String userName, String password, int sessionNumber);
	
	long validateDestUser(String destination, String domain, String userName, String password) throws ServiceException ;
	
	List<String> getD2DIPList();
	
	long updateJobHistory( JJobHistory jobHistory );
	
	boolean markD2DJobEnd(long jobID, long jobStatus, String jobDetails);
	
	int updateThrottling(long jobID, long throttling);
	
	int addMissedJobHistory( JJobHistory jobHistory );
	
	boolean ifJobHistoryExist(int productType, long jobID, int jobType, String agentNodeID);
	
	List<JDisk> getHyperVBackupVMDisk(String destination,String subPath,String domain,String username,String password) throws ServiceException;

	List<String> getE15CASList(String strUser, String strPassword);
	
	String getDefaultE15CAS();

	void setDefaultE15CAS(String cas);
	
	/**
	 * Functions to get switch through JNI
	 */
	public int getSwitchIntFromFile( String strApp, String strKey, int nDefault, String fullPathOfIniFile);
	
	public int getSwitchIntFromReg( String strValueName, int nDefaultValue, String strSubKey);
	
	public String getSwitchStringFromFile( String strApp, String strKey, String strDefault, String fullPathOfIniFile);
	
	public String getSwitchStringFromReg( String strValueName, String strDefaultValue, String strSubKey );
	public void getSourceNodeSysInfo(SourceNodeSysInfo sourceNodeSysInfo);
	/*
	 * end
	 */
	public long getMailAlertFiles( ArrayList<String> fileList);

	public long getMailAlertFilesEx( int type, ArrayList<String> fileList);
	
	public int getHyperVCPUSocketCount(long handle);
	
	public String getHyperVDefaultFolderOfVHD(long handle);
	
	public String GetHyperVDefaultFolderOfVM(long handle);

	public boolean CheckVHDMerging(String strRootPath, int beginSessNumber, int endSessNumber);
	
	public long getLicenseStatus(boolean standAlone);

	public long getRecoveryPoint4Sync( String strDest, String strDomain, String strUser, String strPassword, String strVmUUID, boolean bFullSync, ArrayList<String> filePaths );
	
	public int getHyperVServerType(long handle);
	
	public List<String> getHyperVClusterNodes(long handle);
	
	public long startInstantVM(String paraXMLPath, int startFlag);
	
	public long stopInstantVM(String ivmJobUUID, long jobID, int jobType, boolean isDelete);
	
	public long startHydration(String ivmJobUUID);
	
	public long stopHydration(String ivmJobUUID);
	
	public long checkHyperVVMToolVersion(long handle, String ivmUUID) throws HyperVException, ServiceException;
	
	public long getHyperVIPAddresses(long handle, String ivmUUID, List<String> ips) throws HyperVException, ServiceException;
	
	public long getInstantVMJobStatus(JIVMJobStatus jobStatus);
	
	public void CleanShareMemory(String ivmJobUUID);
	
	public long isInstantVMProxyMeetRequirement(PreCheckInstantVMProxyModel precheckProxyStruct, ArrayList<String> warningList, StringBuilder errMsg);
	
	public int cancelGroupJob(long jobType, long jobID, List<Long> finishedChildJobIDs);
	
	public long enabledScheduledExport(boolean enabled, String vmInstanceUUID );
    public boolean checkScheduledExportInterval(int interval, String vmInstanceUUID);
    public long addSucceedBackupNum(String vmInstanceUUID);
    public String EncryptDNSPassword(String dnsPassword);
    public String DecryptDNSPassword(String dnsPassword);
    
    public String getHyperVPhysicalName(String serverName, String user, String password, String vmInstaceUUID);
	
	public List<ADAttribute> getADAttributes(String destination, String userName, String passWord, long sessionNumber, long subSessionID, String encryptedPwd, long nodeID) throws ServiceException;
	
	public List<ADNode> getADNodes(String destination, String userName, String passWord, long sessionNumber, long subSessionID, String encryptedPwd, long parentID) throws ServiceException;

	public String getOSVersion();
	
    public ArrayList<DataSizesFromStorage> getDataSizesFromStorage(String path, String usrName, String usrPwd) throws ServiceException ;
    
    public int getVolumeSize(String directoryName, List<Long> sizes);
    
    public String getFileCopyCatalogPath(String MachineName, long ProductType);

	public boolean isArchiveSourceDeleteJobRunning();

	public long archiveSourceDelete(ArchiveJobScript jobScript);
	
	public boolean isIVMAgentExist(String ivmJobUUID);
	
	public boolean isHyperVVmExist(String guid, long handle);

    public int checkServiceState(String hostName, String serviceName, String userName, String password, JWindowsServiceModel service);
    
    public String getDisplayLanguage();
    
    public String getHostFQDN();
    
    public long AFGetArchiveJobInfoCount(JArchiveJob out_archiveJob);
}
