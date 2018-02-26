package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2D;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupInformationSummaryModel extends BaseModelData {
	private static final long serialVersionUID = -2258143091032878727L;
	private RecentBackupModel recentFullBackup;
	private RecentBackupModel recentIncrementalBackup;
	private RecentBackupModel recentResyncBackup;
	private DestinationCapacityModel destinationCapacityModel;
	private LicInfoModel licInfo;
	private PatchInfoModel patchInfoModel;
	private BIPatchInfoModel BIpatchInfoModel;//added by cliicy.luo
	private ArchiveJobInfoModel archiveJobInfo;
	private UpdateSettingsModel updateSettingsModel;
	private boolean isBackupSet;
	private RpsHostModel rpsHost;
	private RpsPolicy4D2D rpsPolicy4D2D;
	private String dsHealth;
	private boolean isAdvanced;
	private boolean isPeriodEnabled;
	private AdvanceScheduleModel advanceScheduleModel;
	private int recoveryPointCount4Repeat;
	private int recoveryPointCount4Day;
	private int recoveryPointCount4Week;
	private int recoveryPointCount4Month;
	
	public int getRecoveryPointCount4Day() {
		return recoveryPointCount4Day;
	}

	public void setRecoveryPointCount4Day(int recoveryPointCount4Day) {
		this.recoveryPointCount4Day = recoveryPointCount4Day;
	}

	public int getRecoveryPointCount4Week() {
		return recoveryPointCount4Week;
	}

	public void setRecoveryPointCount4Week(int recoveryPointCount4Week) {
		this.recoveryPointCount4Week = recoveryPointCount4Week;
	}

	public int getRecoveryPointCount4Month() {
		return recoveryPointCount4Month;
	}

	public void setRecoveryPointCount4Month(int recoveryPointCount4Month) {
		this.recoveryPointCount4Month = recoveryPointCount4Month;
	}

	public void setRpsHostModel(RpsHostModel value) {
		rpsHost = value;
	}
	
	public RpsHostModel getRpsHostModel() {
		return rpsHost;
	}
	
	public void setRpsPolicy4D2D(RpsPolicy4D2D value) {
		rpsPolicy4D2D = value;
	}
	
	public RpsPolicy4D2D getRpsPolicy4D2D() {
		return rpsPolicy4D2D;
	}
	
	//wanqi06 add
	public boolean isBackupSet() {
		return isBackupSet;
	}

	public void setBackupSet(boolean value) {
		this.isBackupSet = value;
	}
	
	
	public LicInfoModel getLicInfo() {
		return licInfo;
	}

	public void setLicInfo(LicInfoModel licInfo) {
		this.licInfo = licInfo;
	}

	public ArchiveJobInfoModel getArchiveJobInfo() {
		return archiveJobInfo;
	}

	public void setArchiveJobInfo(ArchiveJobInfoModel in_archiveJobInfo) {
		this.archiveJobInfo = in_archiveJobInfo;
	}
	
	public BackupInformationSummaryModel(){
		this.setAllowNestedValues(true);
	}
	
	public String getDestination() {
		return (String) get("destination");		
	}

	public void setDestination(String destination) {
		set("destination", destination);		
	}
	public Integer getTotalSuccessfulCount() {
		return (Integer)get("totalSuccessfulCount");
	}
	public void setTotalSuccessfulCount(Integer totalSuccessfulCount) {
		set("totalSuccessfulCount",totalSuccessfulCount);
	}
	public Integer getTotalFailedCount() {
		return (Integer)get("totalFailedCount");
	}
	public void setTotalFailedCount(Integer totalFailedCount) {
		set("totalFailedCount",totalFailedCount);
	}
	public int getTotalCanceledCount() {
		return (Integer)get("totalCanceledCount");
	}
	public void setTotalCanceledCount(int totalCanceledCount) {
		set("totalCanceledCount", totalCanceledCount);
	}
	public int getTotalCrashedCount() {
		return (Integer)get("totalCrashedCount");
	}
	public void setTotalCrashedCount(int totalCrashedCount) {
		set("totalCrashedCount", totalCrashedCount);
	}
	public Integer getRecoveryPointCount() {
		return (Integer)get("recoveryPointCount");
	}
	public void setRecoveryPointCount(Integer recoveryPointCount) {
		set("recoveryPointCount",recoveryPointCount);
	}
	public RecentBackupModel getRecentFullBackup() {
		return recentFullBackup;
	}

	public void setRecentFullBackup(RecentBackupModel recentFullBackup) {
		this.recentFullBackup = recentFullBackup;
	}

	public RecentBackupModel getRecentIncrementalBackup() {
		return recentIncrementalBackup;
	}

	public void setRecentIncrementalBackup(RecentBackupModel recentIncrementalBackup) {
		this.recentIncrementalBackup = recentIncrementalBackup;
	}

	public RecentBackupModel getRecentResyncBackup() {
		return recentResyncBackup;
	}

	public void setRecentResyncBackup(RecentBackupModel recentResyncBackup) {
		this.recentResyncBackup = recentResyncBackup;
	}

	public DestinationCapacityModel getDestinationCapacityModel() {
		return destinationCapacityModel;
	}

	public void setDestinationCapacityModel(
			DestinationCapacityModel destinationCapacityModel) {
		this.destinationCapacityModel = destinationCapacityModel;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public Integer getRetentionCount() {
		return (Integer)get("retentionCount");
	}
	public void setRetentionCount(Integer retentionCount) {
		set("retentionCount",retentionCount);
	}
	public void setErrorCode(int errorCode) {
		set("errorCode", errorCode);
	}
	public int getErrorCode() {
		return (Integer)get("errorCode");
	}
	public String getSpaceMeasureUnit() {
		return get("SpaceMeasureUnit");
	}
	public void setSpaceMeasureUnit(String spaceMeasureUnit) {
		set("SpaceMeasureUnit", spaceMeasureUnit);
	}
	public Double getSpaceMeasureNum() {
		return (Double)get("SpaceMeasureNum");
	}
	public void setSpaceMeasureNum(double spaceMeasureNum) {
		set("SpaceMeasureNum", spaceMeasureNum);
	}
	
	public PatchInfoModel getpatchInfoModel()
	{
		return patchInfoModel;
	}
	
	public void setpatchInfoModel(PatchInfoModel in_PatchInfoModel)
	{
		this.patchInfoModel = in_PatchInfoModel;
	}
	
	//added by cliicy.luo
	public void setBIpatchInfoModel(BIPatchInfoModel in_PatchInfoModel)
	{
		this.BIpatchInfoModel = in_PatchInfoModel;
	}
	
	public BIPatchInfoModel getBIpatchInfoModel()
	{
		return BIpatchInfoModel;
	}
	//added by cliicy.luo
	
	public UpdateSettingsModel getupdateSettingsModel()
	{
		return updateSettingsModel;
	}
	
	public void setupdateSettingsModel(UpdateSettingsModel in_updateSettingsModel)
	{
		this.updateSettingsModel = in_updateSettingsModel;
	}
	public Integer getRecoverySetCount() {
		return (Integer)get("RecoverySetCount");
	}
	public void setRecoverySetCount(Integer count) {
		set("RecoverySetCount", count);
	}
	public String getMergeStartTime() {
		return (String)get("MergeStartTime");
	}
	public void setMergeStartTime(String startTime) {
		set("MergeStartTime", startTime);
	}

	public Boolean isInSchedule() {
		return get("isInSchedule");
	}

	public void setIsInSchedule(Boolean isInSchedule) {
		set("isInSchedule",isInSchedule);
	}
	
	public String getDsHealth() {
		return dsHealth;
	}
	
	public void setDsHealth(String dsHealth) {
		this.dsHealth = dsHealth;
	}
	
	public int getDSRunningState() {
		return get("dsRunningState");
	}
	
	// 1:Running 2:Deleted 3:not running
	public void setDSRunningState(int dsRunningState) {
		set("dsRunningState", dsRunningState);
	}

	public AdvanceScheduleModel getAdvanceScheduleModel() {
		return advanceScheduleModel;
	}

	public void setAdvanceScheduleModel(AdvanceScheduleModel advanceScheduleModel) {
		this.advanceScheduleModel = advanceScheduleModel;
	}

	public boolean isAdvanced() {
		return isAdvanced;
	}

	public void setAdvanced(boolean isAdvanced) {
		this.isAdvanced = isAdvanced;
	}

	public boolean isPeriodEnabled() {
		return isPeriodEnabled;
	}

	public void setPeriodEnabled(boolean isPeriodEnabled) {
		this.isPeriodEnabled = isPeriodEnabled;
	}

	public int getRecoveryPointCount4Repeat() {
		return recoveryPointCount4Repeat;
	}

	public void setRecoveryPointCount4Repeat(int recoveryPointCount4Repeat) {
		this.recoveryPointCount4Repeat = recoveryPointCount4Repeat;
	}
}
