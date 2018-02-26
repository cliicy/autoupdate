package com.ca.arcflash.ui.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoveryPointModel extends BaseModelData{
	
	private static final long serialVersionUID = 1448916318634207299L;
	
	//private RecoveryPointItemModel[] items;
	public String getName() {
		return get("Name");
	}
	public void setName(String name) {
		set("Name",name);
	}	
	public Date getTime() {
		return (Date) get("Time");
	}
	public void setTime(Date time) {
		set("Time",time);
	}	
	public D2DTimeModel getD2DTime() {
		return (D2DTimeModel) get("d2dTime");
	}
	public void setD2DTime(D2DTimeModel d2dTime) {
		set("d2dTime", d2dTime);
	}
	public Integer getBackupStatus() {
		return (Integer) get("BackupStatus");
	}
	public void setBackupStatus(Integer backupStatus) {
		set("BackupStatus", backupStatus);
	}
	public Integer getBackupType() {
		return (Integer) get("backupType");
	}
	public void setBackupType(Integer backupType) {
		set("backupType", backupType);
	}
	public Long getLogicalSize() {
		return (Long) get("logicalSize");
	}
	public void setLogicalSize(Long logicalSize) {
		set("logicalSize", logicalSize);
	}
	public Long getDataSize() {
		return (Long) get("dataSize");
	}
	public void setDataSize(Long dataSize) {
		set("dataSize", dataSize);
	}
	public Integer getSessionID() {
		return (Integer) get("sessionID");
	}
	public void setSessionID(Integer sessionID) {
		set("sessionID", sessionID);
	}
	
	public Integer getTimeZoneOffset() {
		return get("timeZoneOffset");
	}
	public void setTimeZoneOffset(Integer timeZoneOffset) {
		set("timeZoneOffset", timeZoneOffset);
	}
	public String getPath() {
		return get("path");
	}
	public void setPath(String name) {
		set("path",name);
	}
	public Integer getEncryptionType() {
		return (Integer)get("encryptionType");
	}
	public void setEncryptionType(Integer encryptionType) {
		set("encryptionType", encryptionType);
	}
	public String getEncryptPwdHashKey() {
		return get("encryptPwdHashKey");
	}
	public void setEncryptPwdHashKey(String encryptPwdHashKey) {
		set("encryptPwdHashKey", encryptPwdHashKey);
	}
	public String getSessionGuid(){
		return get("sessionGuid");
	}
	public void setSessionGuid(String sessionGuid){
		set("sessionGuid", sessionGuid);
	}
	
	public Long getSessionVersion()
	{
		return get("sessionVersion");
	}
	
	public void setSessionVersion(Long sessionVersion)
	{
		set("sessionVersion", sessionVersion);
	}

	public Integer getArchiveJobStatus() {
		return get("archiveJobStatus");
	}
	public void setArchiveJobStatus(Integer in_archiveJobStatus) {
		set("archiveJobStatus", in_archiveJobStatus);
	}
	
	public void setFSCatalogStatus(Integer catalogStatus) {
		set("CatalogStatus", catalogStatus);
	}
	
	public Integer getFSCatalogStatus() {
		return get("CatalogStatus");
	}
	
	public Integer getBackupSetFlag() {
		return (Integer)get("BackupSetFlag");
	}
	
	public void setBackupSetFlag(Integer flag) {
		set("BackupSetFlag", flag);
	}
	
	public Boolean isCanCatalog() {
		return (Boolean)get("canCatalog");
	}
	
	public void setCanCatalog(Boolean flag) {
		set("canCatalog", flag);
	}
	
	public Boolean isCanMount() {
		return (Boolean)get("canMount");
	}
	
	public void setCanMount(Boolean flag) {
		set("canMount", flag);
	}
	
	public Integer getPeriodRetentionFlag() {
		return (Integer)get("periodRetentionFlag");
	}
	
	public void setPeriodRetentionFlag(Integer flag) {
		set("periodRetentionFlag", flag);
	}
	
	public boolean isEncrypted(){
		if(isDefaultSessPwd() != null && isDefaultSessPwd())
			return false;
		
		return getEncryptPwdHashKey() != null && !getEncryptPwdHashKey().isEmpty() ;
	}
	
	public void setDefaultSessPwd(Boolean isDefaultSessPwd) {
		set("isDefaultSessPwd", isDefaultSessPwd);
	}
	
	public Boolean isDefaultSessPwd() {
		return (Boolean)get("isDefaultSessPwd");
	}
	
	public void setVMHypervisor(Integer vmHypervisor)
	{
		set("vmHypervisor", vmHypervisor);
	}
	
	public Integer getVMHypervisor() {
		return (Integer)get("vmHypervisor");
	}
	
	//<huvfe01>added for vm recovery
	public String getVMName(){
		return get("vmName");
	}
	
	public void setVMName(String vmName){
		set("vmName", vmName);
	}
	
	public String getvCenter(){
		return get("vCenter");
	}
	
	public void setvCenter(String name){
		set("vCenter", name);
	}
	
	public String getESXHost(){
		return get("esxHost");
	}
	
	public void setESXHost(String name){
		set("esxHost", name);
	}
	
	public Integer getAgentBackupType(){
		return (Integer)get("agentBackupType");
	}
	
	public void setAgentBackupType(Integer agentBackupType){
		set("agentBackupType",agentBackupType);
	}
	
	/** 
	 * @gwt.typeArgs <com.ca.arcflash.ui.client.model.RecoveryPointItemModel> 
	 */ 
	 public List<com.ca.arcflash.ui.client.model.RecoveryPointItemModel> listOfRecoveryPointItems; 
		 
	 public List<com.ca.arcflash.ui.client.model.CatalogInfoModel> listOfCatalogInfo;
		 
	 public List<com.ca.arcflash.ui.client.model.GridTreeNode> listOfEdbNodes;  // for Exchange GRT only
	 
	 public Map<String, RecoveryPointModel> childVMRecoveryPointModelMap = new HashMap<String, RecoveryPointModel>();
}
