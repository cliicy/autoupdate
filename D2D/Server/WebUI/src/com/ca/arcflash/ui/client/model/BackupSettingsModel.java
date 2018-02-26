package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;

public class BackupSettingsModel extends BaseModelData{
	private static final long serialVersionUID = -1190005990510526621L;
	
	public BackupScheduleModel fullSchedule;
	public BackupScheduleModel incrementalSchedule;
	public BackupScheduleModel resyncSchedule;
	public BackupVolumeModel backupVolumes;
	public UpdateSettingsModel autoUpdateSettings;
	public SRMAlertSettingModel srmAlertSetting;
	public D2DTimeModel startTime;
	public RetentionPolicyModel retentionPolicy;
	public AdvanceScheduleModel advanceScheduleModel;
	public BackupRPSDestSettingsModel rpsDestSettings;
	public ScheduledExportSettingsModel scheduledExportSettingsModel;
	
	/**alert email */
	public String getMailUser() {
		return get("mailUser");
		
	}
	public void setMailUser(String mailUser) {
		set("mailUser", mailUser);
	}
	public void setEnableTls(Boolean b){
		set("enableTls", b);
	}
	public Boolean isEnableTls() {
		return (Boolean) get("enableTls");
	}
	public void setEnableMailAuth(Boolean b){
		set("enableMailAuth", b);
	}
	public Boolean isEnableMailAuth() {
		return (Boolean) get("enableMailAuth");
	}
	public void setEnableProxyAuth(Boolean b){
		set("enableProxyAuth", b);
	}
	public Boolean isEnableProxyAuth() {
		return (Boolean) get("enableProxyAuth");
	}
	public void setEnableSsl(Boolean b)
	{
		set("enableSsl", b);
	}
	public Boolean isEnableSsl() {
		return (Boolean) get("enableSsl");
	}
	public void setSmtpPort(Integer port)
	{
		set("smtpPort", port);
	}
	public Integer getSmtpPort() {
		return (Integer) get("smtpPort");
	}
	public String getMailPwd() {
		return get("MailPassword");
	}
	public void setMailPwd(String mailPwd) {
		set("MailPassword",mailPwd);
	}
	
	public Integer getMailService(){
		return get("MailServiceName");
	}
	public void setMailService(Integer mailService){
		set("MailServiceName",mailService);
	}
	
	public String getDestination() {
		return get("destination");
	}
	public void setDestination(String destination) {
		set("destination", destination);
	}		
	public Integer getRetentionCount() {
		return (Integer) get("retentionCount");
	}
	public void setRetentionCount(Integer retentionCount) {
		set("retentionCount", retentionCount);
	}	
	public String getCommandBeforeBackup() {
		return get("commandBeforeBackup");
	}
	public void setCommandBeforeBackup(String commandBeforeBackup) {
		set("commandBeforeBackup", commandBeforeBackup);
	}
	public Boolean getEnablePreExitCode()
	{
		return get("enablePreExitCode");
	}
	public void setEnablePreExitCode(Boolean enable)
	{
		set("enablePreExitCode", enable);
	}
	public Integer getPreExitCode()
	{
		return get("preExitCode");
	}
	public void setPreExitCode(Integer preExitCode)
	{
		set("preExitCode", preExitCode);
	}
	public Boolean getSkipJob()
	{
		return get("skipJob");
	}
	public void setSkipJob(Boolean skipJob)
	{
		set("skipJob", skipJob);
	}
	
	public String getCommandAfterBackup() {
		return get("commandAfterBackup");
	}
	public Boolean getRunCommandEvenFailed(){
		return get("runCommandEvenFailed");
	}
	public void setRunCommandEvenFailed(Boolean runCommandEvenFailed){
		set("runCommandEvenFailed", runCommandEvenFailed); 
		
		Boolean b = (Boolean) get("runCommandEvenFailed");
	}
	public void setCommandAfterBackup(String commandAfterBackup) {
		set("commandAfterBackup", commandAfterBackup);
	}		
	
	public String getCommandAfterSnapshot()
	{
		return get("commandAfterSnapshot");
	}
	public void setCommandAfterSnapshot(String commandAfterSnapshot)
	{
		set("commandAfterSnapshot", commandAfterSnapshot);
	}
	
	public void setCompressionLevel(Integer b) {
		set("compressionLevel", b);
	}

	public Integer getCompressionLevel() {
		return (Integer) get("compressionLevel");
	}

	public void setEnableEncryption(Boolean b) {
		set("enableEncryption", b);
	}

	public Boolean getEnableEncryption() {
		return (Boolean) get("enableEncryption");
	}
	
	public void setEncryptionAlgorithm(Integer b) {
		set("encryptionAlgorithm", b);
	}
	
	public Integer getEncryptionAlgorithm() {
		return (Integer) get("encryptionAlgorithm");
	}
	
	public void setEncryptionKey(String b) {
		set("encryptionKey", b);
	}
	
	public String getEncryptionKey() {
		return (String) get("encryptionKey");
	}
	
	public void setGenerateCatalog(Boolean b) {
		set("generateCatalog", b);
	}
	
	public Boolean getGenerateCatalog() {
		return (Boolean)get("generateCatalog");
	}
	
	//Schedules
	/*
	public BackupSettingsScheduleModel getFullBackupSchedule()
	{		
		return fullSchedule;
	}
	public void setFullBackupSchedule(BackupSettingsScheduleModel schedule)
	{
		fullSchedule = schedule;		
	}
	
	public BackupSettingsScheduleModel getIncrementalBackupSchedule()
	{
		return incrementalSchedule;
	}
	public void setIncrementalBackupSchedule(BackupSettingsScheduleModel schedule)
	{
		incrementalSchedule = schedule;
	}
	
	public BackupSettingsScheduleModel getResyncBackupSchedule()
	{
		return resyncSchedule;
	}
	public void setResyncBackupSchedule(BackupSettingsScheduleModel schedule)
	{
		resyncSchedule = schedule;
	}
	*/
	
	//Backup Source
	public Boolean getFullNodeBackup() {
		return (Boolean) get("fullNodeBackup");
	}
	public void setFullNodeBackup(Boolean fullNodeBackup) {
		set("fullNodeBackup", fullNodeBackup);				
	}
	
	
	public List<String> volumes;
	
	public Long getSharePointGRTSetting(){
		return (Long) get("sharePointGRTSetting");
	}
	
	public void setSharePointGRTSetting(Long b){
		set("sharePointGRTSetting",b);
	}
	
	//Backup Email
	public Boolean getEnableEmail() {
		return (Boolean) get("enableEmail");
	}
	public void setEnableEmail(Boolean b) {
		set("enableEmail", b);		
	}
	public Boolean getEnableEmailOnSuccess() {
		return (Boolean) get("enableEmailOnSuccess");
	}
	public void setEnableEmailOnSuccess(Boolean b) {
		set("enableEmailOnSuccess", b);		
	}
	public Boolean getEnableEmailOnMergeFailure() {
		return (Boolean) get("enableEmailOnMergeFailure");
	}
	public void setEnableEmailOnMergeFailure(Boolean b) {
		set("enableEmailOnMergeFailure", b);		
	}
	public Boolean getEnableEmailOnMergeSuccess() {
		return (Boolean) get("enableEmailOnMergeSuccess");
	}
	public void setEnableEmailOnMergeSuccess(Boolean b) {
		set("enableEmailOnMergeSuccess", b);		
	}
	public Boolean getEnableEmailOnRecoveryPointCheckFailure(){  
		return (Boolean) get("EnableEmailOnRecoveryPointCheckFailure");
	}
	public void setEnableEmailOnRecoveryPointCheckFailure(Boolean b){  
		set("EnableEmailOnRecoveryPointCheckFailure", b);
	}
	public Boolean getEnableHTMLFormat(){
		return (Boolean) get("enableHTMLFormat");
	}
	public void setEnableHTMLFormat(Boolean b)
	{
		set("enableHTMLFormat", b);
	}
	public Boolean isEnableProxy() {
		return (Boolean) get("enableProxy");
	}
	public void setEnableProxy(Boolean b) {
		set("enableProxy", b);
	}
	public String getProxyAddress() {
		return get("proxyAddress");
	}
	public void setProxyAddress(String proxyAddress) {
		set("proxyAddress", proxyAddress);		
	}
	public Integer getProxyPort() {
		return (Integer) get("proxyPort");
	}
	public void setProxyPort(Integer port) {
		set("proxyPort", port);			
	}
	public String getProxyUsername() {
		return get("proxyUsername");
	}
	public void setProxyUsername(String proxyUsername) {
		set("proxyUsername", proxyUsername);		
	}
	public String getProxyPassword() {
		return get("proxyPassword");
	}
	public void setProxyPassword(String proxyPassword) {
		set("proxyPassword", proxyPassword);		
	}
	public String getSubject() {
		return get("subject");
	}
	public void setSubject(String subject) {
		set("subject", subject);		
	}
	public String getContent() {
		return get("content");
	}
	public void setContent(String content) {
		set("content", content);		
	}
	public String getFromAddress() {
		return get("fromAddress");
	}
	public void setFromAddress(String fromAddress) {
		set("fromAddress", fromAddress);		
	}
	
	public String getSMTP() {
		return get("SMTP");
	}

	public void setSMTP(String smtp) {
		set("SMTP", smtp);
	}
	
	public ArrayList<String> Recipients;
	
	public String getDestUserName() {
		return get("destUserName");
	}

	public String getDestPassword() {
		return get("destPassword");
	}

	public void setDestUserName(String destUserName) {
		set("destUserName", destUserName);
	}

	public void setDestPassword(String destPassword) {
		set("destPassword", destPassword);
	}
	
	public String getActionsUserName() {
		return get("actionsUserName");
	}

	public String getActionsPassword() {
		return get("actionsPassword");
	}

	public void setActionsUserName(String destUserName) {
		set("actionsUserName", destUserName);
	}

	public void setActionsPassword(String destPassword) {
		set("actionsPassword", destPassword);
	}

	public Boolean getChangedBackupDest()
	{
		return (Boolean)get("ChangedBackupDest");
	}
	public void setChangedBackupDest(Boolean b)
	{
		set("ChangedBackupDest", b);
	}
	public Integer getChangedBackupDestType()
	{
		return (Integer)get("ChangedBackupDestType");
	}
	public void setChangedBackupDestType(Integer changedBackupDestType)
	{
		set("ChangedBackupDestType", changedBackupDestType);
	}
	public Long getPurgeSQLLogDays() {
		return (Long)get("purgeSQLLogDays");
	}
	public void setPurgeSQLLogDays(Long purgeSQLLogDays) {
		set("purgeSQLLogDays", purgeSQLLogDays);
	}
	public Long getPurgeExchangeLogDays() {
		return (Long)get("purgeExchangeLogDays");
	}
	public void setPurgeExchangeLogDays(Long purgeExchangeLogDays) {
		set("purgeExchangeLogDays", purgeExchangeLogDays);
	}
	public Long getExchangeGRTSetting() {
		return (Long)get("exchangeGRTSetting");
	}
	public void setExchangeGRTSetting(Long exchangeGRTSetting) {
		set("exchangeGRTSetting", exchangeGRTSetting);
	}
	public Long getBackupStartTime()
	{
		return (Long)get("BackupStartTime");
	}
	public void setBackupStartTime(Long backupStartTime)
	{
		set("BackupStartTime", backupStartTime);
	}
	public String getAdminUserName()
	{
		return (String)get("AdminUserName");
	}
	public void setAdminUserName(String userName)
	{
	     set("AdminUserName", userName);	
	}
	public String getAdminPassword()
	{
		return (String)get("AdminPassword");
	}
	public void setAdminPassword(String adminPassword)
	{
	     set("AdminPassword", adminPassword);	
	}
	public Boolean getEnableSpaceNotification(){
		return get("enableSpaceNotification");
	}
	public void setEnableSpaceNotification(boolean enableSpaceNotification){
		set("enableSpaceNotification", enableSpaceNotification);
	}
	public Double getSpaceMeasureNum(){
		return get("spaceMeasureNum");
	}
	public void setSpaceMeasureNum(double spaceMeasureNum){
		set("spaceMeasureNum", spaceMeasureNum);
	}
	public String getSpaceMeasureUnit(){
		return get("spaceMeasureUnit");
	}
	public void setSpaceMeasureUnit(String spaceMeasureUnit){
		set("spaceMeasureUnit", spaceMeasureUnit);
	}
	public BackupVolumeModel getBackupVolumes() {
		return backupVolumes;
	}
	public void setBackupVolumes(BackupVolumeModel backupVolumes) {
		this.backupVolumes = backupVolumes;
	}
	public Integer getSpaceSavedAfterCompression() {
		return (Integer)get("spaceSavedAfterCompression");
	}
	public void setSpaceSavedAfterCompression(Integer spaceSaved) {
		set("spaceSavedAfterCompression", spaceSaved);
	}
	public Integer getGrowthRate() {
		return (Integer)get("growthRate");
	}
	public void setGrowthRate(Integer growthRate) {
		set("growthRate", growthRate);
	}
	
	public Boolean getEnableEmailOnMissedJob() {
		return (Boolean) get("enableEmailOnMissedJob");
	}
	public void setEnableEmailOnMissedJob(Boolean b) {
		set("enableEmailOnMissedJob", b);		
	}
	
	public Boolean getEnableNotifyOnNewUpdates() {
		return (Boolean) get("enableNotifyOnNewUpdates");
	}
	public void setEnableNotifyOnNewUpdates(Boolean b) {
		set("enableNotifyOnNewUpdates", b);		
	}
	
	public UpdateSettingsModel getautoUpdateSettings()
	{
		return autoUpdateSettings;
	}
	
	public void setautoUpdateSettings(UpdateSettingsModel in_autoUpdateSettings)
	{
		this.autoUpdateSettings = in_autoUpdateSettings;
	}
	public Long getThrottling() {
		return (Long)get("throttling");
	}
	public void setThrottling(Long throttling) {
		set("throttling", throttling);
	}

	public SRMAlertSettingModel getSrmAlertSetting() {
		return srmAlertSetting;
	}
	public void setSrmAlertSetting(SRMAlertSettingModel srmAlertSetting) {
		this.srmAlertSetting = srmAlertSetting;
	}
	
	public void setStartTimezoneOffset(long offSet) {
		set("timeOffset", offSet);
	}
	
	public Long getStartTimezoneOffset() {
		return (Long)get("timeOffset");
	}
	
	public Integer getPreAllocationValue() {
		return (Integer)get("PreAllocation");
	}
	
	public void setPreAllocationValue(Integer value) {
		set("PreAllocation", value);
	}
	
	public String getMajorVersion(){
		return (String)get("majorVersion");
	}
	
	public void setMajorVersion(String majorVersion){
		set("majorVersion",majorVersion);
	}
	
	public String getMinorVersion(){
		return (String)get("minorVersion");
	}
	
	public void setMinorVersion(String minorVersion){
		set("minorVersion",minorVersion);
	}
	
	public Boolean isBackupToRps(){
		return (Boolean)get("backupToRps");
	}
	
	public void setBackupToRps(Boolean backupToRps){
		set("backupToRps", backupToRps);
	}
	public void setBackupDataFormat(int backupDataFormat) {
		set("backupDataFormat", backupDataFormat);		
	}
	
	public Integer getBackupDataFormat() {
		return (Integer)get("backupDataFormat");
	}
	
	public void setWindowsDeduplicationRate(int NtfsDeduplicationRate) {
		set("WindowsDeduplicationRate", NtfsDeduplicationRate);		
	}
	
	public Integer getWindowsDeduplicationRate() {
		return (Integer)get("WindowsDeduplicationRate");
	}

	public Boolean isSoftwareOrHardwareSnapshotType(){
		return get("softwareOrHardwareSnapshotType");
	}
	public void setSoftwareOrHardwareSnapshotType(boolean softwareOrHardwareSnapshotType){
		set("softwareOrHardwareSnapshotType", softwareOrHardwareSnapshotType);
	}
	public Boolean isFailoverToSoftwareSnapshot(){
		return (Boolean) get("failoverToSoftwareSnapshot");
	}
	public void setFailoverToSoftwareSnapshot(boolean failoverToSoftwareSnapshot){
		set("failoverToSoftwareSnapshot", failoverToSoftwareSnapshot);
	}
	public Boolean isUseTransportableSnapshot(){
		return (Boolean) get("useTransportableSnapshot");
	}
	public void setUseTransportableSnapshot(boolean useTransportableSnapshot){
		set("useTransportableSnapshot", useTransportableSnapshot);
	}
	
	private List<String> vmwareTransportModes;


	public List<String> getVmwareTransportModes() {
		return vmwareTransportModes;
	}

	public void setVmwareTransportModes(List<String> vmwareTransportModes) {
		this.vmwareTransportModes = vmwareTransportModes;
	}
	
	public void setCheckRecoveryPoint(Boolean checkRecoveryPoint) {
		set("checkRecoveryPoint", checkRecoveryPoint);
	}
	
	public Boolean getCheckRecoveryPoint() {
		return get("checkRecoveryPoint");
	}
	
	public void setVmwareQuiescenceMethod(Integer vmwareQuiescenceMethod) {
		set("vmwareQuiescenceMethod", vmwareQuiescenceMethod);
	}
	
	public Integer getVmwareQuiescenceMethod() {
		return get("vmwareQuiescenceMethod");
	}
	
	public void setHyperVConsistentSnapshotType(Integer hyperVConsistentSnapshotType) {
		set("hyperVConsistentSnapshotType", hyperVConsistentSnapshotType);
	}

	public Integer getHyperVConsistentSnapshotType() {
		return get("hyperVConsistentSnapshotType");
	}
	
	public void setHyperVCrashConsistentSnapshotWithSavedVMState(Boolean isHyperVCrashConsistentSnapshotWithSavedVMState) {
		set("isHyperVCrashConsistentSnapshotWithSavedVMState", isHyperVCrashConsistentSnapshotWithSavedVMState);
	}
	
	public Boolean getHyperVCrashConsistentSnapshotWithSavedVMState() {
		return get("isHyperVCrashConsistentSnapshotWithSavedVMState");
	}
	
	public void setHyperVSnapshotSeparationIndividually(Boolean isHyperVSnapshotSeparationIndividually) {
		set("isHyperVSnapshotSeparationIndividually", isHyperVSnapshotSeparationIndividually);
	}
	
	public Boolean getHyperVSnapshotSeparationIndividually() {
		return get("isHyperVSnapshotSeparationIndividually");
	}
}
