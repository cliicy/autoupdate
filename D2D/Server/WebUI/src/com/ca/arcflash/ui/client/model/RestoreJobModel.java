package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.restore.ad.ADOptionModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6393128638889547343L;

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

	public Integer getDestType() {
		return (Integer) get("destType");
	}

	public void setDestType(Integer DestType) {
		set("destType", DestType);
	}

	public String getRDBName() {
		return (String) get("RDBName");
	}

	public void setRDBName(String RDBName) {
		set("RDBName", RDBName);
	}
	
	public void setJobId(Long jobId) {
		set("JobId", jobId);
	}
	
	public Long getJobId() {
		return (Long) get("JobId");
	}

	public void setMasterJobId(Long masterJobId) {
		set("MasterJobId", masterJobId);
	}
	
	public Long getMasterJobId() {
		return (Long) get("MasterJobId");
	}
	
	public FileSystemOptionModel fileSystemOption;
	public List<com.ca.arcflash.ui.client.model.RestoreJobNodeModel> listOfRestoreJobNodes;
	public List<com.ca.arcflash.ui.client.model.SQLModel> listOfSQLMode;
	public ExchangeOptionModel exchangeOption;
	public RecoverVMOptionModel recoverVMOption;
    public ExchangeGRTOptionModel exchangeGRTOption;
    public RpsHostModel sourceRPSHost;
    public ADOptionModel adOption;
    public List<RestoreJobModel> childRestoreJobList; //child restore job model for vApp

	public void setDestUser(String username) {
		set("destUser", username);
	}

	public String getDestUser() {
		return get("destUser");
	}

	public void setDestPass(String password) {
		set("destPass", password);
	}

	public String getDestPass() {
		return get("destPass");
	}

	public String getEncryptPassword() {
		return get("encryptPassword");
	}
	public void setEncryptPassword(String encryptPassword) {
		set("encryptPassword", encryptPassword);
	}
	
	public void setJobLauncher(Long launcher){
		set("jobLauncher",launcher);
	}
	
	public Long getJobLauncher(){
		return get("jobLauncher");
	}
	
	public void setVMInstanceUUID(String instanceUUID){
		set("vmInstanceUUID",instanceUUID);
	}
	
	public String getVMInstanceUUID(){
		return get("vmInstanceUUID");
	}
	
	public String getRpsHostname(){
		return get("rpsHostname");
	}
	
	public void setRpsHostname(String rpsHostname){
		set("rpsHostname",rpsHostname);
	}
	
	public void setRpsDataStoreName(String rpsDataStoreName){
		set("rpsDataStoreName",rpsDataStoreName);
	}
	
	public String getRpsDataStoreName(){
		return get("rpsDataStoreName");
	}
	
	public void setRpsPolicy(String policyUUID) {
		set("rpsPolicy", policyUUID);
	}
	
	public String getRpsPolicy() {
		return (String)get("rpsPolicy");
	}
	
	public void setRpsDataStoreDisplayName(String displayName){
		set("rpsDSDisplayName", displayName);
	}
	
	public String getRpsDataStoreDisplayName(){
		return (String)get("rpsDSDisplayName");
	}
}
