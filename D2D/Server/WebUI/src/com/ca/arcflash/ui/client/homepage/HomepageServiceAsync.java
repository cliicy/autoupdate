package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;

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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HomepageServiceAsync {
	public void getProtectionInformation(AsyncCallback<ProtectionInformationModel[]> callback);

	public void getRecentBackups(int backupType, int backupStatus,int top, AsyncCallback<RecoveryPointModel[]> callback);
	
	public void getRecentBackupsByServerTime(int backupType, int backupStatus, String serverBeginDate, String serverEndDate, boolean needCatalogStatus, AsyncCallback<RecoveryPointModel[]> callback);
	
	public void getVMRecentBackupsByServerTime(int backupType, int backupStatus, String serverBeginDate, String serverEndDate, boolean needCatalogStatus, BackupVMModel vmModel,AsyncCallback<RecoveryPointModel[]> callback);
	
	public void getBackupInforamtionSummary(AsyncCallback<BackupInformationSummaryModel> callback);
	
	public void updateProtectionInformation(AsyncCallback<ProtectionInformationModel[]> callback);
	
	public void getDestSizeInformation(BackupSettingsModel model,AsyncCallback<DestinationCapacityModel> callback);
	
	public void getNextScheduleEvent(int in_iJobType,AsyncCallback<NextScheduleEventModel> callback);

	public void getTrustHosts(
			AsyncCallback<TrustHostModel[]> callback);

	void becomeTrustHost(TrustHostModel trustHostModel,
			AsyncCallback<Boolean> callback);

	void checkBaseLicense(AsyncCallback<Boolean> callback);

	void getBackupInforamtionSummaryWithLicInfo(AsyncCallback<BackupInformationSummaryModel> callback);
	
	void getLocalHost(AsyncCallback<TrustHostModel> callback);

	void PMInstallPatch(PatchInfoModel in_patchinfoModel,
			AsyncCallback<Integer> callback);
	
	void PMInstallBIPatch(PatchInfoModel in_patchinfoModel,
			AsyncCallback<Integer> callback);//added by cliicy.luo
	
	void getVMBackupInforamtionSummaryWithLicInfo(BackupVMModel vm,
			AsyncCallback<BackupInformationSummaryModel> callback);

	void getVMBackupInforamtionSummary(BackupVMModel vm,
			AsyncCallback<BackupInformationSummaryModel> callback);

	void getVMProtectionInformation(BackupVMModel vmModel,
			AsyncCallback<ProtectionInformationModel[]> callback);

	void getVMNextScheduleEvent(BackupVMModel vmModel,
			AsyncCallback<NextScheduleEventModel> callback);
	public void getVMRecentBackups(int backupType, int backupStatus,int top,BackupVMModel vmModel, AsyncCallback<RecoveryPointModel[]> callback);

	void updateVMProtectionInformation(BackupVMModel vm,
			AsyncCallback<ProtectionInformationModel[]> callback);

	void getArchiveInfoSummary(AsyncCallback<ArchiveJobInfoModel> callback);

	void getLicInfo(AsyncCallback<LicInfoModel> callback);

	void getVMStatusModel(BackupVMModel vmModel,
			AsyncCallback<VMStatusModel[]> callback);

	void getConfiguredVM(AsyncCallback<BackupVMModel[]> callback);

	void getMergeJobMonitor(String vmInstanceUUID,
			AsyncCallback<MergeJobMonitorModel> callback);

	void pauseMerge(String vmInstanceUUID, AsyncCallback<Integer> callback);

	void resumeMerge(String vmInstanceUUID, AsyncCallback<Integer> callback);

	void getMergeStatus(String vmInstanceUUID,
            AsyncCallback<MergeStatusModel> callback);

	void getBackupSetInfo(String vmInstanceUUID, 
			AsyncCallback<ArrayList<BackupSetInfoModel>> callback);
	
	public void getDataStoreStatus(String dataStoreUUID, AsyncCallback<DataStoreInfoModel> callback);

	void getVMDataStoreStatus(BackupVMModel vm, String dataStoreUUID,
			AsyncCallback<DataStoreInfoModel> callback);
	
}
