package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveSettingsModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6253081194014361495L;
	
	public static final int FILECOPY=1;
	public static final int FILEARCHIVE=2;
	
	private ArchiveSourceInfoModel[] ArchiveSources;
	private ArchiveCloudDestInfoModel cloudConfig;
	
	private AdvanceSchedule advanceSchedule;
	private String policyUUID;
	private RpsHostModel host;

	private boolean backupFrequency;
	private boolean backupSchedule;
	private boolean daily;
	private boolean weekly;
	private boolean monthly;
	private boolean archiveToRPS;
	private int type;
	private String hostname;
	
	private String catalogFolderPath;
	private String catalogFolderUser;
	private String catalogFolderPassword;
	
	
	
	/*private List<FileModel> backupVolumes;
	public List<FileModel> getbackupVolumes()
	{
		return backupVolumes;
	}
	
	public void setbackupVolumes(List<FileModel> in_volumes)
	{
		this.backupVolumes = in_volumes; 
	}*/
	private BackupVolumeModel backupVolumes;
	private String backupDestination;
	
	/*public String getbackupDestination()
	{
		return get("backupDestination");
	}
	
	public void setbackupDestination(String in_strbackupDestination)
	{
		set("backupDestination",in_strbackupDestination);
	}*/
	
	public void setPolicyUUID(String policyUUID){
		this.policyUUID=policyUUID;
	}
	
	public String getPolicyUUID(){
		return policyUUID;
	}
	
	
	public ArchiveSourceInfoModel[] getArchiveSources()
	{
		return ArchiveSources;
	}
	
	public void setArchiveSources(ArchiveSourceInfoModel[] in_archiveSources)
	{
		this.ArchiveSources = in_archiveSources;
	}
	
	public BackupVolumeModel getbackupVolumes()
	{
		return backupVolumes;
	}
	
	public void setbackupVolumes(BackupVolumeModel in_backupVolumes)
	{
		this.backupVolumes = in_backupVolumes;
	}
	
	private Integer archivedFileVersionsRetentionCount = 15;
	private Boolean archiveAfterBackup;
	private Integer archiveAfterNumberofBackups;
	private Boolean purgeScheduleAvailable;
	private Boolean purgeArchiveItems;
	private Integer purgeAfterDays = 1;
	private Long purgeStartTime;
	private Boolean excludeSystemFiles;
	private Boolean excludeAppFiles;
	private Boolean archiveToDrive;
	private String archiveToDrivePath;
	private String destinationPathUserName;
	private String destinationPathPassword;
	private Boolean archiveToCloud;
	private String retentiontime;
	private Integer compressionLevel;
	private Boolean encryption;
	private String encryptionPassword;
	private long rrsFlag;
	private String filesRetentionTime;
	
	//
	private String catalogPath;


	
	public void setCatalogPath(String catalogPath){
		this.catalogPath=catalogPath;
	}
	
	public String getCatalogPath(){
		return this.catalogPath;
	}
	public ArchiveCloudDestInfoModel getCloudConfig() {
		return cloudConfig;
	}

	public void setCloudConfig(ArchiveCloudDestInfoModel cloudConfig) {
		this.cloudConfig = cloudConfig;
	}

	public BackupVolumeModel getBackupVolumes() {
		return backupVolumes;
	}

	public void setBackupVolumes(BackupVolumeModel backupVolumes) {
		this.backupVolumes = backupVolumes;
	}

	public String getBackupDestination() {
		return backupDestination;
	}

	public void setBackupDestination(String backupDestination) {
		this.backupDestination = backupDestination;
	}

	public Integer getArchivedFileVersionsRetentionCount() {
		return archivedFileVersionsRetentionCount;
	}

	public void setArchivedFileVersionsRetentionCount(
			Integer archivedFileVersionsRetentionCount) {
		this.archivedFileVersionsRetentionCount = archivedFileVersionsRetentionCount;
	}

	public Boolean getArchiveAfterBackup() {
		return archiveAfterBackup;
	}

	public void setArchiveAfterBackup(Boolean archiveAfterBackup) {
		this.archiveAfterBackup = archiveAfterBackup;
	}

	public Integer getArchiveAfterNumberofBackups() {
		return archiveAfterNumberofBackups;
	}

	public void setArchiveAfterNumberofBackups(Integer archiveAfterNumberofBackups) {
		this.archiveAfterNumberofBackups = archiveAfterNumberofBackups;
	}

	public Boolean getPurgeScheduleAvailable() {
		return purgeScheduleAvailable;
	}

	public void setPurgeScheduleAvailable(Boolean purgeScheduleAvailable) {
		this.purgeScheduleAvailable = purgeScheduleAvailable;
	}

	public Boolean getPurgeArchiveItems() {
		return purgeArchiveItems;
	}

	public void setPurgeArchiveItems(Boolean purgeArchiveItems) {
		this.purgeArchiveItems = purgeArchiveItems;
	}

	public Integer getPurgeAfterDays() {
		return purgeAfterDays;
	}

	public void setPurgeAfterDays(Integer purgeAfterDays) {
		this.purgeAfterDays = purgeAfterDays;
	}

	public Long getPurgeStartTime() {
		return purgeStartTime;
	}

	public void setPurgeStartTime(Long purgeStartTime) {
		this.purgeStartTime = purgeStartTime;
	}

	public Boolean getExcludeSystemFiles() {
		return excludeSystemFiles;
	}

	public void setExcludeSystemFiles(Boolean excludeSystemFiles) {
		this.excludeSystemFiles = excludeSystemFiles;
	}

	public Boolean getExcludeAppFiles() {
		return excludeAppFiles;
	}

	public void setExcludeAppFiles(Boolean excludeAppFiles) {
		this.excludeAppFiles = excludeAppFiles;
	}

	public Boolean getArchiveToDrive() {
		return archiveToDrive;
	}

	public void setArchiveToDrive(Boolean archiveToDrive) {
		this.archiveToDrive = archiveToDrive;
	}

	public String getArchiveToDrivePath() {
		return archiveToDrivePath;
	}

	public void setArchiveToDrivePath(String archiveToDrivePath) {
		this.archiveToDrivePath = archiveToDrivePath;
	}

	public String getDestinationPathUserName() {
		return destinationPathUserName;
	}

	public void setDestinationPathUserName(String destinationPathUserName) {
		this.destinationPathUserName = destinationPathUserName;
	}

	public String getDestinationPathPassword() {
		return destinationPathPassword;
	}

	public void setDestinationPathPassword(String destinationPathPassword) {
		this.destinationPathPassword = destinationPathPassword;
	}

	public Boolean getArchiveToCloud() {
		return archiveToCloud;
	}

	public void setArchiveToCloud(Boolean archiveToCloud) {
		this.archiveToCloud = archiveToCloud;
	}

	public String getRetentiontime() {
		return retentiontime;
	}

	public void setRetentiontime(String retentiontime) {
		this.retentiontime = retentiontime;
	}

	public Integer getCompressionLevel() {
		return compressionLevel;
	}

	public void setCompressionLevel(Integer compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public Boolean getEncryption() {
		return encryption;
	}

	public void setEncryption(Boolean encryption) {
		this.encryption = encryption;
	}

	public String getEncryptionPassword() {
		return encryptionPassword;
	}

	public void setEncryptionPassword(String encryptionPassword) {
		this.encryptionPassword = encryptionPassword;
	}

	public long getRrsFlag() {
		return rrsFlag;
	}

	public void setRrsFlag(long rrsFlag) {
		this.rrsFlag = rrsFlag;
	}

	public ArchiveCloudDestInfoModel getCloudConfigModel()
	{
		return cloudConfig;
	}
	
	public void setCloudConfigModel(ArchiveCloudDestInfoModel in_CloudConfigModel)
	{
		this.cloudConfig = in_CloudConfigModel;
	}
	
	public AdvanceSchedule getAdvanceSchedule() {
		return this.advanceSchedule;
	}
	
	public void setAdvanceSchedule(AdvanceSchedule advanceSchedule) {
		this.advanceSchedule = advanceSchedule;
	}

	public boolean isBackupFrequency() {
		return backupFrequency;
	}

	public void setBackupFrequency(boolean backupFrequency) {
		this.backupFrequency = backupFrequency;
	}

	public boolean isBackupSchedule() {
		return backupSchedule;
	}

	public void setBackupSchedule(boolean backupSchedule) {
		this.backupSchedule = backupSchedule;
	}

	public boolean isDaily() {
		return daily;
	}

	public void setDaily(boolean daily) {
		this.daily = daily;
	}

	public boolean isWeekly() {
		return weekly;
	}

	public void setWeekly(boolean weekly) {
		this.weekly = weekly;
	}

	public boolean isMonthly() {
		return monthly;
	}

	public void setMonthly(boolean monthly) {
		this.monthly = monthly;
	}
	
	public void setFilesRetentionTime(String filesRetentionTime) {
		this.filesRetentionTime = filesRetentionTime;
	}
	
	public String getFilesRetentionTime() {
		return filesRetentionTime;
	}
	
	public void setArchiveToRPS(boolean archiveToRPS){
		this.archiveToRPS=archiveToRPS;
	}
	
	public boolean isArchiveToRPS() {
		return archiveToRPS;
	}
	
	public int getType(){
		return this.type;
	}
	
	public void setType(int type){
		this.type=type;
	}
	
	public RpsHostModel getHost(){
		return this.host;
	}
	
	public void setHost(RpsHostModel host){
		
			this.host=host;
	}
	public String getHostName(){
		return this.hostname;
	}
	
	public void setHostName(String hostname){
		this.hostname=hostname;
	}
	
	/*public void setCatalogFolderPath(String storePath){
		this.catalogFolderPath = storePath;
	}
	
	public String getCatalogFolderPath(){
		return this.catalogFolderPath;
	}*/
	
	public void setCatalogFolderUser(String storeUser){
		this.catalogFolderUser = storeUser;
	}
	
	public String getCatalogFolderUser(){
		return this.catalogFolderUser;
	}
	
	public void setCatalogFolderPassword(String storePassword){
		this.catalogFolderPassword = storePassword;
	}
	
	public String getCatalogFolderPassword(){
		return this.catalogFolderPassword;
	}
	
	/*public Integer getSpaceUtilizationValue()
	{
		return (Integer)get("SpaceUtilizationValue");
	}
	
	public void setSpaceUtilizationValue(Integer in_iSpaceUtilizationValue)
	{
		set("SpaceUtilizationValue",in_iSpaceUtilizationValue);
	}*/
}
