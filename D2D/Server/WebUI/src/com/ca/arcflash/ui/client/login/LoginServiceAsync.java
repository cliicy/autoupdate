package com.ca.arcflash.ui.client.login;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
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

public interface LoginServiceAsync {
	
	void getHostInfoList(AsyncCallback<List<HostInfo>> callback);
	
	void checkSession(AsyncCallback<Boolean> callback);
	
	void getLogonUser(AsyncCallback<String> callback);
	
	void validateUser(String protocol,String host, int port, String domain, String username, String password,AsyncCallback<Boolean> callback);
	void validateUserByUuid(String uuid, String host, int port, String protocol, AsyncCallback<Boolean> callback);
	void saveLocaleSession(String locale, AsyncCallback<Void> callback);
	
	//TODO: Move this to its own service
	void getBackupConfiguration(AsyncCallback<BackupSettingsModel> callback);

	void saveBackupConfiguration(BackupSettingsModel configuration,
			AsyncCallback<java.lang.Long> arg2) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	void getScheduledExportConfiguration(AsyncCallback<ScheduledExportSettingsModel> callback);
	void saveScheduledExportConfiguration(ScheduledExportSettingsModel model, AsyncCallback<Long> callback);
	void validateScheduledExportConfiguration(ScheduledExportSettingsModel model, AsyncCallback<Long> callback);

	void getTreeGridChildren(GridTreeNode loadConfig,
			AsyncCallback<List<GridTreeNode>> callback);	
	void getTreeGridChildrenEx(GridTreeNode loadConfig,
			AsyncCallback<List<GridTreeNode>> callback);	
	void submitRestoreJob(RestoreJobModel job, AsyncCallback<java.lang.Integer> arg2);
	void submitCopyJob(CopyJobModel job, AsyncCallback<java.lang.Integer> arg2);
	
	void openSearchCatalog(String sessionPath, 
			String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern, 
			AsyncCallback<SearchContextModel> callback);
	void openSearchCatalog(String destination, String domain, 
			String userName, String password, String sessionPath, 
			String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern, 
			AsyncCallback<SearchContextModel> callback);
	void closeSearchCatalog(SearchContextModel sContext, AsyncCallback<java.lang.Integer> arg2);
	void searchNext(SearchContextModel sContext, AsyncCallback<SearchResultModel> callback);
	
	void getFileFolderChildren(String path, boolean bIncludeFiles, AsyncCallback<List<FileModel>> callback);
	void getVolumes(int browseClient,AsyncCallback<List<FileModel>> callback);
	void getVolumesWithDetails(int browseClient,String backupDest, String usr, String pwd, AsyncCallback<List<FileModel>> callback);
	void createFolder(String parentPath, String subDir,int browseClient, AsyncCallback<Void> calback);

	void getFileItems(String inputFolder, String user, String password,
			boolean bIncludeFiles,int browseClient, AsyncCallback<List<FileModel>> callback);

	void logout(AsyncCallback<Boolean> asyncCallback);
	void getVersionInfo(AsyncCallback<VersionInfoModel> asyncCallback);
	void getServerTime(AsyncCallback<Date> asyncCallback);
	void getDefaultUserAndBuild(String protocol,String host, int port, AsyncCallback<VersionInfoModel> asyncCallback);
	
	void getNews(String url, AsyncCallback< List<NewsItemModel> > asyncCallback);
	
	void checkDestinationValid(String path, AsyncCallback<Long> asyncCallback);
	void getPathMaxLength(AsyncCallback<Long> asyncCallback);
		
	/**
	 * Returns saved administrator account.
	 * @return
	 * @throws AxisFault
	 */
    void getAdminAccount(AsyncCallback<AccountModel> asyncCallback);
    
 
	void getDefaultUser(String protocol,String host, int port, AsyncCallback<String> callback);

	void getPagingGridTreeNode(GridTreeNode parent, PagingLoadConfig pageCfg,
			AsyncCallback<PagingLoadResult<GridTreeNode>> callback);

	@Deprecated
	void getRecoveryPointsByServerTime(String destination, String domain,
			String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail, AsyncCallback<RecoveryPointModel[]> callback);
	void getRecoveryPointsByServerTime(String destination, String domain,
			String userName, String pwd, D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate, boolean isQueryDetail, AsyncCallback<RecoveryPointModel[]> callback);
	void checkSQLAlternateLocation(String[] basePath, String[] instName,
			   String[] dbName, AsyncCallback<AlternativePathModel[]> callback);
	void getExcludedAppComponents(String[] volumes, AsyncCallback<ApplicationModel[]> callback);

	void getRecoveryPointItems(String dest, String domain, String user,
			String pwd, String subPath,
			AsyncCallback<List<RecoveryPointItemModel>> callback);

	void ValidateServerName(String ServerName, AsyncCallback<Boolean> callback);

	void getPreferences(AsyncCallback<PreferencesModel> callback);

	void savePreferences(PreferencesModel preferencesConfig,
			AsyncCallback<Long> callback);
	
	void validatePreferences(PreferencesModel preferencesConfig,
			AsyncCallback<Long> callback);
	
	void testDownloadServerConnection(UpdateSettingsModel testSettings,
			AsyncCallback<UpdateSettingsModel> asyncCallback);
	
	void testBIDownloadServerConnection(UpdateSettingsModel testSettings,
			AsyncCallback<UpdateSettingsModel> asyncCallback);//added by cliicy.luo
	
	void saveVShpereBackupSetting(VSphereBackupSettingModel backupSettingModel,
			AsyncCallback<Long> callback);

	void getVSphereBackupSetting(VirtualCenterNodeModel vc,
			AsyncCallback<VSphereBackupSettingModel> callback);

	void getAllVM(VirtualCenterNodeModel vc, AsyncCallback<List<VMItemModel>> callback);

	void getVMBackupConfiguration(BackupVMModel vmModel,
			AsyncCallback<VMBackupSettingModel> callback);

	void getESXServerDataStore(VirtualCenterModel vc,ESXServerModel esxServerModel,
			AsyncCallback<String[]> callback);

	void getESXServer(VirtualCenterModel vcModel,
			AsyncCallback<List<ESXServerModel>> callback);

	void getBackupVMModelList(String destination,String domain, String username,
			String password, AsyncCallback<List<BackupVMModel>> callback);

	void checkVMDestination(String destination, String domain, String username,
			String password, AsyncCallback<Boolean> callback);

	void getBackupVMDisk(String destination, String subPath, String domain,
			String username, String password,
			AsyncCallback<List<DiskModel>> callback);

	void getVMItem(VirtualCenterModel vc, ESXServerModel esxserver,
			AsyncCallback<List<VMItemModel>> callback);

	void validateVC(VirtualCenterModel vcModel, AsyncCallback<Integer> callback);

	void submitRecoveryVMJob(RestoreJobModel job,
			AsyncCallback<Integer> callback);

	void submitVMCopyJob(CopyJobModel job, AsyncCallback<Integer> callback); 


	void getVMwareDataStore(VirtualCenterModel vc,
			ESXServerModel esxServerModel, AsyncCallback<VMStorage[]> callback);

	void getTreeExchangeChildren(ExchangeDiscoveryModel loadConfig, String userName, String password,
			AsyncCallback<List<ExchangeDiscoveryModel>> callback);
	void d2dExCheckUser(String domain, String user, String password, AsyncCallback<Long> callback);
	
	void browseGRTCatalog(GridTreeNode parent, PagingLoadConfig pageCfg, GRTBrowsingContextModel contextModel, 
			AsyncCallback<GRTPagingLoadResult> callback);
	
	void submitCatalogJob(CatalogJobParaModel catalogJobModel, AsyncCallback<Void> callback);
	void submitFSCatalogJob(CatalogJobParaModel catalogJobModel,String vmInstanceUUID, AsyncCallback<Long> callback);
	void validateCatalogFileExist(GridTreeNode loadConfig,  AsyncCallback<Long> callback);
	
	void getPagingTreeExchangeChildren(ExchangeDiscoveryModel loadConfig, PagingLoadConfig pageCfg, String strUser, String strPassword,
			AsyncCallback<PagingLoadResult<ExchangeDiscoveryModel>> callback);

	void isUsingEdgePolicySettings( int settingsType,
		AsyncCallback<Boolean> callback );
	
	void getArchiveConfiguration(AsyncCallback<ArchiveSettingsModel> callback);

	void saveArchiveConfiguration(ArchiveSettingsModel in_archiveSettingsModel,
			AsyncCallback<Long> callback);
	
	void validateArchiveConfiguration(ArchiveSettingsModel in_archiveSettingsModel,
			AsyncCallback<Long> callback);

	//void getArchivedVolumesList(String strArchiveDestination,String strUsername,String strPassword,AsyncCallback<List<String>> callback);
	void getArchiveDestinationItems(ArchiveDestinationModel archiveDestModel,AsyncCallback<ArchiveRestoreDestinationVolumesModel[]> callback);
	void getArchiveTreeGridChildren(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode loadConfig,
			AsyncCallback<List<ArchiveGridTreeNode>> callback);
	void submitRestoreArchiveJob(RestoreArchiveJobModel job, AsyncCallback<Long> arg2);
	
	void ValidateRestoreArchiveJob(RestoreArchiveJobModel job, AsyncCallback<Boolean> arg2);
	void ValidateRestoreJob(RestoreArchiveJobModel job, AsyncCallback<Boolean> arg2);
	void getArchivableFilesList(ArchiveSourceInfoModel in_sourceInfo,AsyncCallback<List<ArchiveGridTreeNode>> callback);
	
	void searchArchiveDestinationItems(ArchiveDestinationModel archiveDestDetailsModel,String path,long in_lSearchOptions, String strfileName,long in_lIndex,long in_lRequiredItemsCount,AsyncCallback<List<CatalogItemModel>> callback);
	void getSelectedBackupVolumesInfo(AsyncCallback<List<FileModel>> callback);
	void getFATVolumesInfo(AsyncCallback<List<FileModel>> callback);

    void checkCatalogExist(String destination, long sessionNumber, AsyncCallback<List<CatalogInfoModel>> callback);

	void GetHostName(AsyncCallback<String> callback);

	void getCloudBuckets(ArchiveCloudDestInfoModel in_cloudInfo,
			AsyncCallback<CloudModel[]> callback);

	void getCloudRegions(ArchiveCloudDestInfoModel in_cloudInfo,AsyncCallback<String[]> callback);

	void testConnectionToCloud(ArchiveCloudDestInfoModel in_cloudInfo,AsyncCallback<Long> callback);

	void verifyBucketNameWithCloud(ArchiveCloudDestInfoModel in_cloudInfo,
			AsyncCallback<CloudModel> callback);

	void getRegionForBucket(ArchiveCloudDestInfoModel in_cloudInfo,
			AsyncCallback<CloudModel> callback);

	void validateBucketName(String bucketName,boolean isForEdge, AsyncCallback<Long> callback);
	
	void getRecoveryPointItems_EDB(String dest, String domain, String user,	String pwd, String subPath,long sessionNumber,
			AsyncCallback<RecoveryPointResultModel> callback);

	@Deprecated
	void getRecoveryPointsByServerTimeWithFSCatalogStatus(String destination,
			String domain, String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail,
			AsyncCallback<RecoveryPointModel[]> callback);
	void getRecoveryPointsByServerTimeWithFSCatalogStatus(String destination,
			String domain, String userName, String pwd, D2DTimeModel serverBeginDate,
			D2DTimeModel serverEndDate, boolean isQueryDetail,
			AsyncCallback<RecoveryPointModel[]> callback);

	void getArchivePagingGridTreeNode(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode parent,
			PagingLoadConfig pageCfg,
			AsyncCallback<PagingLoadResult<ArchiveGridTreeNode>> callback);
			
	void validateProxyInfo(VSphereBackupSettingModel proxy, AsyncCallback<Integer> callback);

	void installDriver(AsyncCallback<Boolean> callback) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;;

	void installDriverRestart(AsyncCallback<Boolean> callback) throws BusinessLogicException,ServiceConnectException,ServiceInternalException;;
	void getArchiveChangedDestinationDetails(ArchiveDestinationModel in_archiveDest,AsyncCallback<ArchiveDestinationDetailsModel> callback);
	void submitArchiveCatalogSyncJob(RestoreArchiveJobModel in_JobModel,AsyncCallback<Long> callback);

	void checkRecoveryVMJobExist(String vmName,
			String esxServerName, AsyncCallback<Boolean> callback);

	void checkServerEqualsVMHostName(String destination, String domain,
			String username, String password, AsyncCallback<Boolean> callback);

	void validateEmailFromAddress(String address,
			AsyncCallback<Boolean> callback);

	void getResoucePool(VirtualCenterModel vc, ESXServerModel esxserver,ResourcePoolModel parentResourcePoolModel,
			AsyncCallback<List<ResourcePoolModel>> callback);
	
	void checkServiceStatus(String serviceName, AsyncCallback<Long> callback);

	void getCustomizedModel(AsyncCallback<CustomizationModel> callback);

	void getBackupVMModel(String destination, String domain, String username,
			String password, AsyncCallback<BackupVMModel> callback);
			
	void getCloudProviderInfo(AsyncCallback<HashMap<String,CloudVendorInfoModel>> callback);			

	void checkDestChainAccess(AsyncCallback<String> callback);

	void updateDestAccess(String dest, String user, String pass, String domain,
			AsyncCallback<String> callback);

	void getWSPort(AsyncCallback<Integer> callback);

	void validateBucketNameForAzure(String bucketName,boolean isForEdge,
			AsyncCallback<Long> callback);

	void getArchiveTreeGridChildrenCount(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode loadConfig,
			AsyncCallback<Long> callback);

	void validateRecoveryVMToOriginal(VirtualCenterModel vcModel,
			BackupVMModel backupVMModel,int sessionNum, AsyncCallback<VMStorage[]> callback);

	void testMailSettings(EmailAlertsModel emailConf,
			AsyncCallback<Boolean> callback);
			
	void checkVMRecoveryPointESXUefi(VirtualCenterModel vcModel, ESXServerModel esxServerModel,String dest, String domain, String user,String pwd, 
			String subPath, AsyncCallback<Integer> callback);

	void isUEFIFirmware(AsyncCallback<Boolean> callback);
	
	void getAllMountedRecoveryPointItems(AsyncCallback<List<MountedRecoveryPointItemModel>> callback);
	void getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath,String sessionGuid,
			AsyncCallback<List<MountedRecoveryPointItemModel>> callback);
	
	@Deprecated
	void mountRecoveryPointItem( String dest,String domain, String user, String pwd, String subPath, String volGUID,int encryptionType,String encryptPassword, String mountPath,
			AsyncCallback<Long> callback);
	void mountRecoveryPointItem( JMountRecoveryPointParamsModel jMntParams, AsyncCallback<Long> callback);
	
	void disMountRecoveryPointItem(String mountPath, int mountDiskSignature,AsyncCallback<Long> callback);
	void getAvailableMountDriveLetters(AsyncCallback<List<String>> callback);
	void validateBackupConfiguration(BackupSettingsModel configuration,
			AsyncCallback<Long> callback);
	void getTreeGridChildren(GridTreeNode loadConfig, String userName,
			String password, AsyncCallback<List<GridTreeNode>> callback);

	void getRecoveryPointItemChildrenCount(GridTreeNode node,
			AsyncCallback<Long> callback);

	void openSearchCatalog(RecoveryPointModel[] models, String sessionPath,
			String sDir, boolean bCaseSensitive, boolean bIncludeSubDir,
			String pattern, String domain, String destUser, String destPwd,
			String[] encryptedHashKey, String[] encryptedPwd, AsyncCallback<SearchContextModel> callback);

	void submitFSOndemandCatalg(List<RecoveryPointModel> sessions,
			String dest, String destUserName, String destPassword,
			String vmInstanceUUID,
			AsyncCallback<Long> callback);
	
	void submitFSOndemandCatalog(OndemandInfo4RPS rpsDestInfo, AsyncCallback<Long> callback);
	
	void saveD2DConfiguration(D2DSettingModel settings,AsyncCallback<Long> callback);

	void getD2DConfiguration(AsyncCallback<D2DSettingModel> callback);		
			
	void getVolumes(VirtualCenterModel vcModel, VMItemModel vmModel,
			AsyncCallback<List<FileModel>> callback);

	void createFolder(String parentPath, String subDir,
			VirtualCenterModel vcModel, VMItemModel vmModel,
			AsyncCallback<Void> callback);

	void getFileItems(String inputFolder,boolean bIncludeFiles, VirtualCenterModel vcModel,
			VMItemModel vmModel, AsyncCallback<List<FileModel>> callback);

	void getBackupD2DList(String hostname, String username, String password, String protocol, int port, AsyncCallback<List<BackupD2DModel>> callback);

	void validateRpsDestSettings(BackupSettingsModel configuration,
			AsyncCallback<Long> callback);

	void getVDSInfoList(VirtualCenterModel vc, String esx,
			AsyncCallback<List<VDSInfoModel>> callback);
	
	void getVMNetworkConfigList(String rootPath, int sessionNum, String userName, String password,
			AsyncCallback<List<VMNetworkConfigInfoModel>> callback);
	
	void getVMwareServerType(String host, String username, String password, String protocol, int port, AsyncCallback<Integer> callback);

	void getStandardNetworkInfoList(VirtualCenterModel vc, String esx, AsyncCallback<List<VMNetworkStandardConfigInfoModel>> callback);

	void validateHyperV(String host, String user, String password,
			AsyncCallback<Void> callback);
	
	void validateHyperVAndCheckIfVMExist(String host, String user, String password, String vmInstantUUID, String vmName, AsyncCallback<Void> callback);

	void getHyperVAvailabeNetworkList(String hostname, String username,
			String password, AsyncCallback<VMNetworkConfigInfoModel[]> callback);

	void getHyperVBackupVMDisk(String destination, String subPath,
			String domain, String username, String password,
			AsyncCallback<List<DiskModel>> callback);

	void getHyperVVMNetworkConfigList(String destination, String subPath, String domain, String username, String password,
			AsyncCallback<List<VMNetworkConfigInfoModel>> callback);
	
	void getE15CASList(String userName, String password, AsyncCallback<List<String>> callback);

	void getDefaultE15CAS(String userName, String password, AsyncCallback<String> callback);

	@Deprecated
	void getRecoveryPointsByServerTimeRPSInfo(String sessionPath, String string, String userName, String password,
			String sDate, String eDate, boolean b, OndemandInfo4RPS rpsInfo,
			AsyncCallback<RecoveryPointModel[]> callback);
	void getRecoveryPointsByServerTimeRPSInfo(String sessionPath, String string, String userName, String password,
			D2DTimeModel sDate, D2DTimeModel eDate, boolean b, OndemandInfo4RPS rpsInfo,
			AsyncCallback<RecoveryPointModel[]> callback);
	
	void browseHyperVHostFolder(String server, String userName, String password, String parentFolder, AsyncCallback<List<FileModel>> callback);
	
	void createHyperVHostFolder(String server, String userName, String password, String path, String folder, AsyncCallback<Boolean> callback);

	void getHyperVHostStorage(String server, String userName, String password, AsyncCallback<List<HyperVHostStorageModel>> callback);

	void CompareHyperVVersion(String server, String userName, String password, String sessUserName, String sessPassword, String sessRootPath, int sessNumber, AsyncCallback<Integer> callback);

	void getHyperVDefaultFolderOfVHD(String server, String userName,
			String password, AsyncCallback<String> callback);

	void GetHyperVDefaultFolderOfVM(String server, String userName,
			String password, AsyncCallback<String> callback);
	
	void getVMVFlashReadCache(String rootPath, int sessionNum, String userName, String password, AsyncCallback<Long> callback);
	void getESXVFlashResource(VirtualCenterModel vcModel, ESXServerModel esxServerModel, AsyncCallback<Long> callback);

	void getHyperVServerType(String server, String userName, String password,
			AsyncCallback<Integer> callback);

	void getHyperVClusterNodes(String server, String userName, String password,
			AsyncCallback<List<String>> callback);

	void validateVCloud(VCloudDirectorModel directorModel, AsyncCallback<Integer> callback);
	void getVCloudOrganizations(VCloudDirectorModel directorModel, AsyncCallback<List<VCloudOrgnizationModel>> callback);
	void getStorageProfilesOfVDC(VCloudDirectorModel directorModel, String vDCId, AsyncCallback<List<VCloudStorageProfileModel>> callback);
	void getESXHosts4VAppChildVM(VCloudDirectorModel directorModel, VirtualCenterModel vcModel, String vDCId, String datastoreMoRef, AsyncCallback<List<ESXServerModel>> callback);
	void getVAppVDCFromSession(String vAppDestination, int vAppSessionNumer, String fullUsername, String password, AsyncCallback<VCloudVirtualDataCenterModel> callback);
	void getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination, int vAppSessionNumer, String domain,
			String username, String password, AsyncCallback<List<VAppBackupVMRecoveryPointModelWrapper>> callback);
	void getVAppChildVMDisks(Map<String, List<String>> pathSubPathMap, String domain, String username, String password, 
			AsyncCallback<Map<String, List<DiskModel>>> callback);
	void getVAppAndChildVMNetworkConfigLists(String rootPath, int sessionNum, String userName, String password,
			AsyncCallback<Map<String, List<VMNetworkConfigInfoModel>>> callback);
	void getVMAdapterTypes(VirtualCenterModel vcModel, ESXServerModel esxModel, AsyncCallback<List<String>> callback);

	void getADNodes(GridTreeNode loadConfig, AsyncCallback<List<GridTreeNode>> callback);
	void getADPagingNodes(GridTreeNode parent, PagingLoadConfig loadConfig, String filter, AsyncCallback<PagingLoadResult<GridTreeNode>> callback);
	void getADAttributes(GridTreeNode loadConfig, AsyncCallback<List<GridTreeNode>> callback);
	void getRPSArchiveDestinations(AsyncCallback<List<RpsHostModel>> asyncCallback) ;
	void getArchiveConfigurations(AsyncCallback<List<ArchiveSettingsModel>> asyncCallback);

	void getArchiveChangedDestinationDetailList(
			List<ArchiveDestinationModel> list,
			AsyncCallback<List<ArchiveDestinationDetailsModel>> callback);

	void getRolePrivilegeModel(AsyncCallback<RolePrivilegeModel> callback);

	void getAllArchiveDestinationDetails(List<ArchiveDestinationModel> archiveManualDestList, AsyncCallback<List<ArchiveDestinationModel>> callback);
}
