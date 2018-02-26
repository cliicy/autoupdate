package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

public class BaseVSpherePolicyModel extends BackupSettingsModel implements IEmailConfigModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public VSphereProxyModel vSphereProxyModel;
	
	public Integer getPreAllocationValue() {
		return (Integer)get("PreAllocation");
	}
	
	public void setPreAllocationValue(Integer value) {
		set("PreAllocation", value);
	}
	
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
	public void setCommandAfterBackup(String commandAfterBackup) {
		set("commandAfterBackup", commandAfterBackup);
	}		
public Boolean getRunCommandEvenFailed(){
		return get("runCommandEvenFailed");
	}
	public void setRunCommandEvenFailed(Boolean runcommandEvenFailed){
		set("runCommandEvenFailed",runcommandEvenFailed);
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
	
	public Long getThrottling() {
		return (Long)get("throttling");
	}
	public void setThrottling(Long throttling) {
		set("throttling", throttling);
	}
	
	//Backup Source
	public Boolean getFullNodeBackup() {
		return (Boolean) get("fullNodeBackup");
	}
	public void setFullNodeBackup(Boolean fullNodeBackup) {
		set("fullNodeBackup", fullNodeBackup);				
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
	public Boolean getEnableEmailOnRecoveryPointCheckFailure()
	{
		return (Boolean)get("enableEmailOnCheckRPSFailure");
	}
	
	public void setEnableEmailOnRecoveryPointCheckFailure(Boolean b){
		set("enableEmailOnCheckRPSFailure", b);
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
	
	@Override
	public ArrayList<String> getRecipients() {
		return Recipients;
	}
	
	@Override
	public void setRecipients(ArrayList<String> recipients ) {
		this.Recipients = recipients;
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
	
	public Boolean getEnableEnumeration(){
		return (Boolean)get("enableEnumeration");
	}
	
	public void setEnableEnumeration(Boolean b){
		set("enableEnumeration",b);
	}

	
	public void setEnableEmailOnDataStoreNotEnough(Boolean enableEmailOnDataStoreNotEnough){
		set("enableEmailOnDataStoreNotEnough",enableEmailOnDataStoreNotEnough);
	}
	
	public Boolean getEnableEmailOnDataStoreNotEnough(){
		return get("enableEmailOnDataStoreNotEnough");
	}
	public void setEnableEmailOnHostNotFound(Boolean enableEmailOnHostNotFound){
		set("enableEmailOnHostNotFound",enableEmailOnHostNotFound);
	}
	
	public Boolean getEnableEmailOnHostNotFound(){
		return get("enableEmailOnHostNotFound");
	}
	
	public void setEnableEmailOnLicensefailure(Boolean enableEmailOnLicensefailure){
		set("enableEmailOnLicensefailure",enableEmailOnLicensefailure);
	}
	
	public Boolean getEnableEmailOnLicensefailure(){
		return get("enableEmailOnLicensefailure");
	}

	public void setEnableEmailOnJobQueue(Boolean enableEmailOnJobQueue){
		set("enableEmailOnJobQueue",enableEmailOnJobQueue);
	}
	
	public Boolean getEnableEmailOnJobQueue(){
		return get("enableEmailOnJobQueue");
	}
	
	public Integer getGenerateType() {
		return get("generateType");
	}

	public void setGenerateType(Integer generateType) {
		set("generateType", generateType);
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
	
	public void setHyperVSnapshotSeparationIndividually(Boolean isHyperVSnapshotSeparationIndividually) {
		set("isHyperVSnapshotSeparationIndividually", isHyperVSnapshotSeparationIndividually);
	}
	
	public Boolean getHyperVSnapshotSeparationIndividually() {
		return get("isHyperVSnapshotSeparationIndividually");
	}
}
