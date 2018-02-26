package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class CopyJobModel extends BaseModelData{
	public RpsHostModel rpsHost;
	
	public String getRpsPolicyUUID(){
		return (String)get("rpsPolicy");
	}
	
	public void setRpsPolicyUUID(String rpsPolicyUUID){
		set("rpsPolicy", rpsPolicyUUID);
	}
	
	public String getRPSDataStoreUUID(){
		return (String)get("rpsDataStore");
	}
	
	public void setRPSDataStoreUUID(String dataStoreUUID){
		set("rpsDataStore", dataStoreUUID);
	}
	
	public String getSessionPath() {
		return (String) get("SessionPath");
	}

	public void setSessionPath(String sessionPath) {
		set("SessionPath", sessionPath);
	}

	public String getDestinationPath() {
		return (String) get("DestinationPath");
	}

	public void setDestinationPath(String destinationPath) {
		set("DestinationPath", destinationPath);
	}

	public Integer getJobType() {
		return (Integer) get("jobType");
	}

	public void setJobType(Integer jobType) {
		set("jobType", jobType);
	}

	public String getUserName() {
		return get("userName");
	}

	public String getPassword() {
		return get("password");
	}

	public void setUserName(String userName) {
		set("userName", userName);
	}

	public void setPassword(String password) {
		set("password", password);
	}

	public void setCompressionLevel(Integer b) {
		set("compressionLevel", b);
	}

	public Integer getCompressionLevel() {
		return (Integer) get("compressionLevel");
	}
	
	public Long getEncryptType() {
		return (Long) get("encryptType");
	}
	
	public void setEncryptType(Long encryptType) {
		set("encryptType", encryptType);
	}
	
	public String getEncryptPassword() {
		return (String) get("encryptPassword");
	}
	
	public void setEncryptPassword(String encryptPassword) {
		set("encryptPassword", encryptPassword);
	}
	
	public Long getEncryptTypeCopySession() {
		return (Long) get("encryptTypeCopySession");
	}
	
	public void setEncryptTypeCopySession(Long encryptTypeCopySession) {
		set("encryptTypeCopySession", encryptTypeCopySession);
	}
	
	public String getEncryptPasswordCopySession() {
		return (String) get("encryptPasswordCopySession");
	}
	
	public void setEncryptPasswordCopySession(String encryptPassword) {
		set("encryptPasswordCopySession", encryptPassword);
	}
	
	public String getDestinationUserName() {
		return get("destUserName");
	}

	public String getDestinationPassword() {
		return get("destPassword");
	}

	public void setDestinationUserName(String userName) {
		set("destUserName", userName);
	}

	public void setDestinationPassword(String password) {
		set("destPassword", password);
	}
	public Integer getSessionNumber() {
		return (Integer) get("sessionNumber");
	}

	public void setSessionNumber(Integer sessionNumber) {
		set("sessionNumber", sessionNumber);
	}
	public String getVMInstanceUUID(){
		return get("vmInstanceUUID");
	}
	public void setVMInstanceUUID(String vmInstanceUUID){
		set("vmInstanceUUID",vmInstanceUUID);
	}
	public Long getJobLauncher(){
		return get("jobLauncher");
	}
	public void setJobLauncher(Long launcher){
		set("jobLauncher",launcher);
	}
	public Integer getRestPoint() {
		return (Integer)get("restPoint");
	}
	public void setRestPoint(Integer restPoint) {
		set("restPoint", restPoint);
	}
	
	public String getRpsHostname(){
		return get("rpsHostname");
	}
	
	public void setRpsHostname(String rpsHostname){
		set("rpsHostname",rpsHostname);
	}
	public void setRpsDataStoreDisplayName(String displayName){
		set("rpsDSDisplayName", displayName);
	}
	
	public String getRpsDataStoreDisplayName(){
		return (String)get("rpsDSDisplayName");
	}
	
	public void setRetainEncryptionAsSource(Boolean retainEncryptionAsSource){
		set("retainEncryptionAsSource",retainEncryptionAsSource);
	}
	public Boolean getRetainEncryptionAsSource(){
		return (Boolean)get("retainEncryptionAsSource");
	}
	
}
