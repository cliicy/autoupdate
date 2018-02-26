package com.ca.arcflash.ui.client.login;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.AccountModel;
import com.ca.arcflash.ui.client.model.AlternativePathModel;
import com.ca.arcflash.ui.client.model.ApplicationModel;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationDetailsModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.ArchiveRestoreDestinationVolumesModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.ArchiveSourceInfoModel;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CatalogInfoModel;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CatalogJobParaModel;
import com.ca.arcflash.ui.client.model.CloudModel;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CopyJobModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.D2DSettingModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.ExchangeDiscoveryModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.GRTBrowsingContextModel;
import com.ca.arcflash.ui.client.model.GRTPagingLoadResult;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.HostInfo;
import com.ca.arcflash.ui.client.model.HyperVHostStorageModel;
import com.ca.arcflash.ui.client.model.JMountRecoveryPointParamsModel;
import com.ca.arcflash.ui.client.model.MountedRecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.NewsItemModel;
import com.ca.arcflash.ui.client.model.OndemandInfo4RPS;
import com.ca.arcflash.ui.client.model.PreferencesModel;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RecoveryPointResultModel;
import com.ca.arcflash.ui.client.model.ResourcePoolModel;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.model.ScheduledExportSettingsModel;
import com.ca.arcflash.ui.client.model.SearchContextModel;
import com.ca.arcflash.ui.client.model.SearchResultModel;
import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.ca.arcflash.ui.client.model.VAppBackupVMRecoveryPointModelWrapper;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.ca.arcflash.ui.client.model.VCloudOrgnizationModel;
import com.ca.arcflash.ui.client.model.VCloudStorageProfileModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VDSInfoModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VMNetworkConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMNetworkStandardConfigInfoModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.VirtualCenterNodeModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.webservice.AxisFault;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/login")
public interface LoginService extends RemoteService{
	
	public List<HostInfo> getHostInfoList();
	
	public Boolean checkSession();
	
	public String getLogonUser();
	
	VersionInfoModel getDefaultUserAndBuild(String protocol,String host, int port);
	
	public Boolean validateUser(String protocol,String host, int port, String domain, String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public Boolean validateUserByUuid(String uuid, String host, int port, String protocol) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public void saveLocaleSession(String locale) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//TEST, Move later
	public BackupSettingsModel getBackupConfiguration();
	public long saveBackupConfiguration(BackupSettingsModel configuration) throws BusinessLogicException, ServiceConnectException,
		ServiceInternalException;
	public long validateBackupConfiguration(BackupSettingsModel configuration) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	
	public long validateRpsDestSettings(BackupSettingsModel configuration) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;

	// scheduled settings
	public ScheduledExportSettingsModel getScheduledExportConfiguration();
	public long saveScheduledExportConfiguration(ScheduledExportSettingsModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public long validateScheduledExportConfiguration(ScheduledExportSettingsModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//Preferences
	public PreferencesModel getPreferences();
	public long savePreferences(PreferencesModel preferencesConfig) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public long validatePreferences(PreferencesModel preferencesConfig) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
    public List<GridTreeNode> getTreeGridChildren(GridTreeNode loadConfig);
    public List<GridTreeNode> getTreeGridChildren(GridTreeNode loadConfig, String userName, 
    		String password) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
    public List<GridTreeNode> getTreeGridChildrenEx(GridTreeNode loadConfig);
	public Integer submitRestoreJob(RestoreJobModel job) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public Integer submitCopyJob(CopyJobModel job) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//TEST, Search
	public SearchContextModel openSearchCatalog(String sessionPath, 
			String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public int closeSearchCatalog(SearchContextModel sContext);
	public SearchResultModel searchNext(SearchContextModel sContext) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public SearchContextModel openSearchCatalog(RecoveryPointModel[] models, String sessionPath, 
			String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern, String domain, 
			String destUser, String destPwd, String[] encryptedHashKey, String[] encryptedPwd) 
	throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	
	//TEST
	public List<FileModel> getFileFolderChildren(String path, boolean bIncludeFiles);	
	public List<FileModel> getVolumes(int browseClient);
	public List<FileModel> getVolumesWithDetails(int browseClient,String backupDest, String usr, String pwd);
	public void createFolder(String parentPath, String subDir,int browseClient)throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;;

	List<FileModel> getFileItems(String inputFolder, String user,
			String password, boolean bIncludeFiles, int browseClient)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;

	boolean logout();
	public VersionInfoModel getVersionInfo();
	public Date getServerTime();
	
	public List<NewsItemModel> getNews(String url);

	public long checkDestinationValid(String path) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public long getPathMaxLength() throws BusinessLogicException, ServiceConnectException, ServiceInternalException ;
	
		
	/**
	 * Returns saved administrator account.
	 * @return
	 * @throws AxisFault
	 */
    public AccountModel getAdminAccount()  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
    
 
	String getDefaultUser(String protocol,String host, int port);

	PagingLoadResult<GridTreeNode> getPagingGridTreeNode(GridTreeNode parent,
			PagingLoadConfig pageCfg) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException;
	
	
	PagingLoadResult<ArchiveGridTreeNode> getArchivePagingGridTreeNode(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode parent,
			PagingLoadConfig pageCfg) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException;
	
	

	@Deprecated
	RecoveryPointModel[] getRecoveryPointsByServerTime(String destination,
			String domain, String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	RecoveryPointModel[] getRecoveryPointsByServerTime(String destination,
			String domain, String userName, String pwd, D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate, boolean isQueryDetail) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	@Deprecated
	RecoveryPointModel[] getRecoveryPointsByServerTimeWithFSCatalogStatus(String destination,
			String domain, String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail);
	RecoveryPointModel[] getRecoveryPointsByServerTimeWithFSCatalogStatus(String destination,
			String domain, String userName, String pwd, D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate, boolean isQueryDetail);
	
	AlternativePathModel[] checkSQLAlternateLocation(String[] basePath, String[] instName, String[] dbName)
	        throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	ApplicationModel[] getExcludedAppComponents(String[] volumes) 
	 		throws  BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<RecoveryPointItemModel> getRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public boolean ValidateServerName(String ServerName) throws BusinessLogicException,ServiceInternalException;

	UpdateSettingsModel testDownloadServerConnection(UpdateSettingsModel testSettings) throws BusinessLogicException,ServiceInternalException;
	UpdateSettingsModel testBIDownloadServerConnection(UpdateSettingsModel testSettings) throws BusinessLogicException,ServiceInternalException;//added by cliicy.luo

	long saveVShpereBackupSetting(VSphereBackupSettingModel backupSettingModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	VSphereBackupSettingModel getVSphereBackupSetting(VirtualCenterNodeModel vc)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	List<VMItemModel> getAllVM(VirtualCenterNodeModel vc)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	VMBackupSettingModel getVMBackupConfiguration(BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	String[] getESXServerDataStore(VirtualCenterModel vc,
			ESXServerModel esxServerModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	VMStorage[] getVMwareDataStore(VirtualCenterModel vc,
			ESXServerModel esxServerModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	List<ESXServerModel> getESXServer(VirtualCenterModel vcModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	List<BackupVMModel> getBackupVMModelList(String destination, String domain,
			String username, String password)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	BackupVMModel getBackupVMModel(String destination, String domain,
			String username, String password)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	boolean checkVMDestination(String destination, String domain,
			String username, String password)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	List<DiskModel> getBackupVMDisk(String destination, String subPath,
			String domain, String username, String password)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	List<VMItemModel> getVMItem(VirtualCenterModel vc, ESXServerModel esxserver)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	int validateVC(VirtualCenterModel vcModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	int submitRecoveryVMJob(RestoreJobModel job)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	int submitVMCopyJob(CopyJobModel job)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;


	public List<ExchangeDiscoveryModel> getTreeExchangeChildren(ExchangeDiscoveryModel loadConfig, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public long d2dExCheckUser(String domain, String user, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	public GRTPagingLoadResult browseGRTCatalog(GridTreeNode parent, PagingLoadConfig pageCfg,
			GRTBrowsingContextModel contextModel) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	public void submitCatalogJob(CatalogJobParaModel catalogJobmodel) throws BusinessLogicException, ServiceInternalException,
	ServiceConnectException;

	public long submitFSCatalogJob(CatalogJobParaModel catalogJobmodel, String vmInstanceUUID) throws BusinessLogicException, ServiceInternalException,
	ServiceConnectException;

	public long validateCatalogFileExist(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;

	public PagingLoadResult<ExchangeDiscoveryModel> getPagingTreeExchangeChildren(ExchangeDiscoveryModel loadConfig,
			PagingLoadConfig pageCfg, String strUser, String strPassword) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	boolean isUsingEdgePolicySettings( int settingsType );
	
	//APIs related to Archive
	public ArchiveSettingsModel getArchiveConfiguration()throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public long saveArchiveConfiguration(ArchiveSettingsModel in_archiveSettingsModel) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	
	public long validateArchiveConfiguration(ArchiveSettingsModel in_archiveSettingsModel) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	
	//Api's for Archive
	//public List<String> getArchivedVolumesList(String strArchiveDestination,String strUserName, String strPassword) throws BusinessLogicException, ServiceConnectException,ServiceInternalException;
	public ArchiveRestoreDestinationVolumesModel[] getArchiveDestinationItems(ArchiveDestinationModel archiveDestModel) throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	
	List<ArchiveGridTreeNode> getArchiveTreeGridChildren(
			ArchiveDestinationModel archiveDestModel,
			ArchiveGridTreeNode loadConfig);
	
	public long getArchiveTreeGridChildrenCount(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode loadConfig);
	
	public long submitRestoreArchiveJob(RestoreArchiveJobModel in_archiveJob) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public Boolean ValidateRestoreArchiveJob(RestoreArchiveJobModel in_archiveJob) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<ArchiveGridTreeNode> getArchivableFilesList(ArchiveSourceInfoModel in_SourceInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//api's to search archive destinations for files
	public List<CatalogItemModel> searchArchiveDestinationItems(ArchiveDestinationModel archiveDestDetailsModel,String path,long in_lSearchOptions, String strfileName,long lIndex,long in_lRequiredItemsCount)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public List<FileModel> getSelectedBackupVolumesInfo() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public List<FileModel> getFATVolumesInfo() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	public List<CatalogInfoModel> checkCatalogExist(String destination, long sessionNumber) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException;	 
	
	public D2DSettingModel getD2DConfiguration() throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;
	public String GetHostName() throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public CloudModel[] getCloudBuckets(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public CloudModel getRegionForBucket(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public String[] getCloudRegions(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public long testConnectionToCloud(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public CloudModel verifyBucketNameWithCloud(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public HashMap<String, CloudVendorInfoModel> getCloudProviderInfo() throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public long validateBucketName(String bucketName, boolean isForEdge) throws BusinessLogicException, ServiceConnectException;
	public long validateBucketNameForAzure(String bucketName, boolean isForEdge) throws BusinessLogicException, ServiceConnectException;
	public RecoveryPointResultModel getRecoveryPointItems_EDB(String dest,String domain, String user, String pwd, String subPath, long sessionNumber) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public int validateProxyInfo(VSphereBackupSettingModel proxy) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	public Boolean installDriver() throws BusinessLogicException,ServiceConnectException,ServiceInternalException; 
	public Boolean installDriverRestart() throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public ArchiveDestinationDetailsModel getArchiveChangedDestinationDetails(ArchiveDestinationModel in_archiveDest) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//archive catalog job
	public long submitArchiveCatalogSyncJob(RestoreArchiveJobModel in_JobModel) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public boolean checkRecoveryVMJobExist(String vmName,String esxServerName) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public boolean checkServerEqualsVMHostName(String destination,String domain,String username,String password) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public List<ResourcePoolModel> getResoucePool(VirtualCenterModel vc, ESXServerModel esxserver,ResourcePoolModel parentResourcePoolModel) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	boolean validateEmailFromAddress(String address) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;
	
	public long checkServiceStatus(String serviceName) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public CustomizationModel getCustomizedModel() throws BusinessLogicException;
	
	public String checkDestChainAccess() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	public String updateDestAccess(String dest, String user, String pass, String domain) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public int getWSPort();

	Boolean ValidateRestoreJob(RestoreArchiveJobModel inArchiveJob)	throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	public VMStorage[] validateRecoveryVMToOriginal(VirtualCenterModel vcModel,BackupVMModel backupVMModel,int sessionNum) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	boolean testMailSettings(EmailAlertsModel emailConf) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;
	
	int checkVMRecoveryPointESXUefi(VirtualCenterModel vcModel,ESXServerModel esxServerModel,String dest, String domain, String user,String pwd, String subPath)
	throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean isUEFIFirmware() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<MountedRecoveryPointItemModel> getAllMountedRecoveryPointItems() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<MountedRecoveryPointItemModel> getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath,String sessionGuid) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	@Deprecated
	long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath, String volGUID,int encryptionType,String encryptPassword, String mountPath)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	long mountRecoveryPointItem( JMountRecoveryPointParamsModel jMntParamsModel ) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	long disMountRecoveryPointItem(String mountPath,int mountDiskSignature)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<String> getAvailableMountDriveLetters() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	SearchContextModel openSearchCatalog(String destination, String domain,
			String userName, String password, String sessionPath, String sDir,
			boolean bCaseSensitive, boolean bIncludeSubDir, String pattern)
	throws BusinessLogicException, ServiceConnectException, ServiceInternalException ;
	
	public long getRecoveryPointItemChildrenCount(GridTreeNode node) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	public long submitFSOndemandCatalg(List<RecoveryPointModel> sessions, String dest, String destUserName, String destPassword, String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;

	long submitFSOndemandCatalog(OndemandInfo4RPS rpsDestInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
			
	long saveD2DConfiguration(D2DSettingModel settings) throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException;
	
	public List<FileModel> getVolumes(VirtualCenterModel vcModel,VMItemModel vmModel);
	public void createFolder(String parentPath, String subDir,VirtualCenterModel vcModel,VMItemModel vmModel)throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException;;

	List<FileModel> getFileItems(String inputFolder,boolean bIncludeFiles,VirtualCenterModel vcModel,VMItemModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	public List<BackupD2DModel> getBackupD2DList(String hostname,String username,String password,String protocol,int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<VDSInfoModel> getVDSInfoList(VirtualCenterModel vc, String esx) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<VMNetworkConfigInfoModel> getVMNetworkConfigList(String rootPath, int sessionNum, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	/**
	 * getVMwareType
	 * 0 - error
	 * 1 - ESXServer
	 * 2 - VirtualCenter
	 */
	public int getVMwareServerType(String host,String username,String password,String protocol,int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<VMNetworkStandardConfigInfoModel> getStandardNetworkInfoList(VirtualCenterModel vc, String esx) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public void validateHyperV(String host, String user, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public void validateHyperVAndCheckIfVMExist(String host, String user,String password,
					                     String vmInstantUUID,String vmName) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public VMNetworkConfigInfoModel[] getHyperVAvailabeNetworkList(String hostname, String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<DiskModel> getHyperVBackupVMDisk(String destination, String subPath,
			String domain, String username, String password)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public List<VMNetworkConfigInfoModel> getHyperVVMNetworkConfigList(String destination, String subPath, String domain, String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	public List<String> getE15CASList(String userName, String password);
	public String getDefaultE15CAS(String userName, String password);
	
	@Deprecated
	RecoveryPointModel[] getRecoveryPointsByServerTimeRPSInfo(String destination, String domain, String userName, String pwd,
			String serverBeginDate, String serverEndDate, boolean isQueryDetail, OndemandInfo4RPS rpsInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	RecoveryPointModel[] getRecoveryPointsByServerTimeRPSInfo(String destination, String domain, String userName, String pwd,
			D2DTimeModel serverBeginDate, D2DTimeModel serverEndDate, boolean isQueryDetail, OndemandInfo4RPS rpsInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<FileModel> browseHyperVHostFolder(String server, String userName,
			String password, String parentFolder) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	boolean createHyperVHostFolder(String server, String userName,
			String password, String path, String folder) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<HyperVHostStorageModel> getHyperVHostStorage(String server, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int CompareHyperVVersion(String server, String userName, String password, String sessUserName, String sessPassword, String sessRootPath, int sessNumber) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String getHyperVDefaultFolderOfVHD(String server, String userName, String password) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	String GetHyperVDefaultFolderOfVM(String server, String userName, String password) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;

	long getVMVFlashReadCache(String rootPath, int sessionNum, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	long getESXVFlashResource(VirtualCenterModel vcModel,ESXServerModel esxServerModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int getHyperVServerType(String server, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	List<String> getHyperVClusterNodes(String server, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	int validateVCloud(VCloudDirectorModel directorModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	List<VCloudOrgnizationModel> getVCloudOrganizations(VCloudDirectorModel directorModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	List<VCloudStorageProfileModel> getStorageProfilesOfVDC(VCloudDirectorModel directorModel, String vDCId) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	List<ESXServerModel> getESXHosts4VAppChildVM(VCloudDirectorModel directorModel, VirtualCenterModel vcModel, String vDCId, String datastoreMoRef) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	VCloudVirtualDataCenterModel getVAppVDCFromSession(String vAppDestination, int vAppSessionNumer, String fullUsername, String password);
	List<VAppBackupVMRecoveryPointModelWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination,
			int vAppSessionNumer, String domain, String username, String password) throws BusinessLogicException, 
			ServiceConnectException, ServiceInternalException;
	Map<String, List<DiskModel>> getVAppChildVMDisks(Map<String, List<String>> pathSubpathMap, String domain,
			String username, String password) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	Map<String, List<VMNetworkConfigInfoModel>> getVAppAndChildVMNetworkConfigLists(String rootPath, int sessionNum,
			String userName, String password) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	List<String> getVMAdapterTypes(VirtualCenterModel vcModel, ESXServerModel esxModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	List<GridTreeNode> getADNodes(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException, ServiceInternalException ;
	PagingLoadResult<GridTreeNode> getADPagingNodes(GridTreeNode parent, PagingLoadConfig loadConfig, String filter) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	List<GridTreeNode> getADAttributes(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException, ServiceInternalException ;
	List<RpsHostModel> getRPSArchiveDestinations()throws BusinessLogicException, ServiceConnectException, ServiceInternalException ;
	List<ArchiveSettingsModel> getArchiveConfigurations() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	List<ArchiveDestinationDetailsModel> getArchiveChangedDestinationDetailList(
			List<ArchiveDestinationModel> list) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException;
	
	public RolePrivilegeModel getRolePrivilegeModel() throws BusinessLogicException;

	List<ArchiveDestinationModel> getAllArchiveDestinationDetails(List<ArchiveDestinationModel> archiveManualDestList) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
}
