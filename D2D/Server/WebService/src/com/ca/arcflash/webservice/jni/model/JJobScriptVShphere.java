package com.ca.arcflash.webservice.jni.model;

import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;

public class JJobScriptVShphere {
	
	private int ulVersion;
	private long ulJobID;
	private int usJobType;
	private int nNodeItems;
	private List<JJobScriptVSphereNode> pAFNodeList;
	private String pwszDestPath;
	
	private String pwszUserName;//user name for accessing backup destination;restore and copy source 
	
	@NotPrintAttribute
	private String pwszPassword;//password for accessing backup destination;restore and copy source
	
	private String  pwszUserName_2; //user name to accessing restore and copy destination
	
	@NotPrintAttribute
	private String  pwszPassword_2; //password to accessing restore and copy destination

	private String pwszComments;
	private String pwszBeforeJob;
	private String pwszAfterJob;
	private String pwszPostSnapshotCmd;
	private String pwszPrePostUser;
	@NotPrintAttribute
	private String pwszPrePostPassword;
	private int usPreExitCode;
	private int usJobMethod;
	private int usRestPoint;
	private int fOptions;
	private long dwCompressionLevel;
	private long dwJobHistoryDays = 10; //TODO hardcode to 10 currently
	private long dwSqlLogDays;
	private long dwExchangeLogDays;
	
	public long getDwJobHistoryDays() {
		return dwJobHistoryDays;
	}
	public void setDwJobHistoryDays(long dwJobHistoryDays) {
		this.dwJobHistoryDays = dwJobHistoryDays;
	}
	public long getDwSqlLogDays() {
		return dwSqlLogDays;
	}
	public void setDwSqlLogDays(long dwSqlLogDays) {
		this.dwSqlLogDays = dwSqlLogDays;
	}
	public long getDwExchangeLogDays() {
		return dwExchangeLogDays;
	}
	public void setDwExchangeLogDays(long dwExchangeLogDays) {
		this.dwExchangeLogDays = dwExchangeLogDays;
	}
	public long getDwCompressionLevel() {
		return dwCompressionLevel;
	}
	public void setDwCompressionLevel(long dwCompressionLevel) {
		this.dwCompressionLevel = dwCompressionLevel;
	}
	public int getUlVersion() {
		return ulVersion;
	}
	public void setUlVersion(int ulVersion) {
		this.ulVersion = ulVersion;
	}
	public long getUlJobID() {
		return ulJobID;
	}
	public void setUlJobID(long ulJobID) {
		this.ulJobID = ulJobID;
	}
	public int getUsJobType() {
		return usJobType;
	}
	public void setUsJobType(int usJobType) {
		this.usJobType = usJobType;
	}
	public int getNNodeItems() {
		return nNodeItems;
	}
	public void setNNodeItems(int nodeItems) {
		nNodeItems = nodeItems;
	}
	
	public List<JJobScriptVSphereNode> getpAFNodeList() {
		return pAFNodeList;
	}
	public void setpAFNodeList(List<JJobScriptVSphereNode> pAFNodeList) {
		this.pAFNodeList = pAFNodeList;
	}
	public String getPwszDestPath() {
		return pwszDestPath;
	}
	public void setPwszDestPath(String pwszDestPath) {
		this.pwszDestPath = pwszDestPath;
	}	
	public String getPwszUserName() {
		return pwszUserName;
	}
	public void setPwszUserName(String pwszUserName) {
		this.pwszUserName = pwszUserName;
	}
	public String getPwszPassword() {
		return pwszPassword;
	}
	public void setPwszPassword(String pwszPassword) {
		this.pwszPassword = pwszPassword;
	}
	public String getPwszComments() {
		return pwszComments;
	}
	public void setPwszComments(String pwszComments) {
		this.pwszComments = pwszComments;
	}
	public String getPwszBeforeJob() {
		return pwszBeforeJob;
	}
	public void setPwszBeforeJob(String pwszBeforeJob) {
		this.pwszBeforeJob = pwszBeforeJob;
	}
	public String getPwszAfterJob() {
		return pwszAfterJob;
	}
	public void setPwszAfterJob(String pwszAfterJob) {
		this.pwszAfterJob = pwszAfterJob;
	}	
	public String getPwszPostSnapshotCmd() {
		return pwszPostSnapshotCmd;
	}
	public void setPwszPostSnapshotCmd(String pwszPostSnapshotCmd) {
		this.pwszPostSnapshotCmd = pwszPostSnapshotCmd;
	}
	public String getPwszPrePostUser() {
		return pwszPrePostUser;
	}
	public void setPwszPrePostUser(String pwszPrePostUser) {
		this.pwszPrePostUser = pwszPrePostUser;
	}
	public String getPwszPrePostPassword() {
		return pwszPrePostPassword;
	}
	public void setPwszPrePostPassword(String pwszPrePostPassword) {
		this.pwszPrePostPassword = pwszPrePostPassword;
	}
	public int getUsPreExitCode() {
		return usPreExitCode;
	}
	public void setUsPreExitCode(int usPreExitCode) {
		this.usPreExitCode = usPreExitCode;
	}
	public int getUsJobMethod() {
		return usJobMethod;
	}
	public void setUsJobMethod(int usJobMethod) {
		this.usJobMethod = usJobMethod;
	}
	public int getUsRestPoint() {
		return usRestPoint;
	}
	public void setUsRestPoint(int usRestPoint) {
		this.usRestPoint = usRestPoint;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	public String getPwszUserName_2() {
		return pwszUserName_2;
	}
	public void setPwszUserName_2(String pwszUserName_2) {
		this.pwszUserName_2 = pwszUserName_2;
	}
	public String getPwszPassword_2() {
		return pwszPassword_2;
	}
	public void setPwszPassword_2(String pwszPassword_2) {
		this.pwszPassword_2 = pwszPassword_2;
	}

}
