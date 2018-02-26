package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.model.ArchiveJobInfoModel;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.BackupSetInfoModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DataStoreInfoModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.LicInfoModel;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.ca.arcflash.ui.client.model.NextScheduleEventModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.ProtectionInformationModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.ca.arcflash.ui.client.model.VMStatusModel;
import com.ca.arcflash.webservice.data.LicInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/homepage")
public interface HomepageService extends RemoteService {
	
	public ProtectionInformationModel[] getProtectionInformation() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public RecoveryPointModel[] getRecentBackups(int backupType, int backupStatus,int top) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public RecoveryPointModel[] getRecentBackupsByServerTime(int backupType, int backupStatus,String serverBeginDate, String serverEndDate, boolean needCatalogStatus) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public BackupInformationSummaryModel getBackupInforamtionSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public ProtectionInformationModel[] updateProtectionInformation() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public ProtectionInformationModel[] updateVMProtectionInformation(BackupVMModel vm) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public DestinationCapacityModel getDestSizeInformation(BackupSettingsModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public NextScheduleEventModel getNextScheduleEvent(int in_iJobType) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	TrustHostModel[] getTrustHosts() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	Boolean becomeTrustHost(TrustHostModel trustHostModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
	
	Boolean checkBaseLicense() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	BackupInformationSummaryModel getBackupInforamtionSummaryWithLicInfo() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	TrustHostModel getLocalHost() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	public int PMInstallPatch(PatchInfoModel in_patchinfoModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	public int PMInstallBIPatch(PatchInfoModel in_patchinfoModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;//added by cliicy.luo

	BackupInformationSummaryModel getVMBackupInforamtionSummaryWithLicInfo(
			BackupVMModel vm)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	BackupInformationSummaryModel getVMBackupInforamtionSummary(BackupVMModel vm)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	ProtectionInformationModel[] getVMProtectionInformation(
			BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;;

	NextScheduleEventModel getVMNextScheduleEvent(BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	public RecoveryPointModel[] getVMRecentBackups(int backupType, int backupStatus,int top,BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	ArchiveJobInfoModel getArchiveInfoSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	LicInfoModel getLicInfo() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	RecoveryPointModel[] getVMRecentBackupsByServerTime(int backupType,
			int backupStatus, String serverBeginDate, String serverEndDate,
			boolean needCatalogStatus, BackupVMModel vmModel)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	VMStatusModel[] getVMStatusModel(BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	BackupVMModel[] getConfiguredVM() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//
	MergeJobMonitorModel getMergeJobMonitor(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	int pauseMerge(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	int resumeMerge(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	MergeStatusModel getMergeStatus(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	ArrayList<BackupSetInfoModel> getBackupSetInfo(String vmInstanceUUID)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	DataStoreInfoModel getDataStoreStatus(String dataStoreUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	DataStoreInfoModel getVMDataStoreStatus(BackupVMModel vm, String dataStoreUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;	
}
