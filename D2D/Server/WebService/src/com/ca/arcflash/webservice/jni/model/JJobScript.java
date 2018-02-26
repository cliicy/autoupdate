package com.ca.arcflash.webservice.jni.model;

import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.data.archive.JJobScriptArchiveInfo;

public class JJobScript {
	private int ulVersion;
	private long ulJobID;
	private int usJobType;
	private int nNodeItems;
	private List<JJobScriptNode> pAFNodeList;
	//October sprint 
	private int nStorageApplianceItems;
	private List<JJobScriptStorageAppliance> pAFStorageApplianceList;
	private List<JJobScriptVSphereNode> pVSphereNodeList;
	private List<JJobScriptRecoverVMNode> pRecoverVMNodeList;
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
	private long dwEncryptType;
	@NotPrintAttribute
	private String pwszEncryptPassword;
	private long dwEncryptTypeCopySession;
	@NotPrintAttribute
	private String pwszEncryptPasswordCopySession;
	private long dwJobHistoryDays = 10; //TODO hardcode to 10 currently
	private boolean softwareOrHardwareSnapshotType = true;
	private boolean useTrasportableSnapshot;
	private boolean failoverToSoftwareSnapshot = true;
	private long dwSqlLogDays;
	private long dwExchangeLogDays;
	private boolean bRetainEncryptionAsSource=false;//for copy recovery point
	private long dwThrottlingByKB;
	private long ulJobAttribute;
	private String launcherInstanceUUID;
	//fanda03 fix 102889
	private int preAllocationSpace;
	//for RPS
	private String rpsPolicyName;
	private String rpsHostname;
	private String rpsSID;
	private String rpsPolicyID;
	private String RPSDataStoreName;
	private String RPSDataStoreDisplayName;
	private String pwszVDiskPassword;
	private long ullScheduledTime;
	private long dwMasterJobId; // specific for vApp master job, so that child jobs can associate with.
	private String generatedDestination; //vApp child VM backup destination path
	
	private JJobScriptArchiveInfo archiveInfo; // for archive job
	
	//VC connect info for VMware vApp
	private List<JJobScriptBackupVC> vAppVCInfos;
	private int vAppVCCount;
	
	public long getDwThrottlingByKB() {
		return dwThrottlingByKB;
	}
	public void setDwThrottlingByKB(long dwThrottlingByKB) {
		this.dwThrottlingByKB = dwThrottlingByKB;
	}
	public long getDwJobHistoryDays() {
		return dwJobHistoryDays;
	}
	public void setDwJobHistoryDays(long dwJobHistoryDays) {
		this.dwJobHistoryDays = dwJobHistoryDays;
	}
	
	public void setSoftwareOrHardwareSnapshotType(boolean softwareOrHardwareSnapshotType) {
		this.softwareOrHardwareSnapshotType = softwareOrHardwareSnapshotType;
	}
	public boolean getSoftwareOrHardwareSnapshotType() {
		return softwareOrHardwareSnapshotType;
	}
	
	public void setFailoverToSoftwareSnapshot(boolean failoverToSoftwareSnapshot) {
		this.failoverToSoftwareSnapshot = failoverToSoftwareSnapshot;
	}
	public boolean getFailoverToSoftwareSnapshot() {
		return failoverToSoftwareSnapshot;
	}
	
	public void setUseTransportableSnapshot(boolean useTrasportableSnapshot) {
		this.useTrasportableSnapshot = useTrasportableSnapshot;
	}
	public boolean getUseTrasportableSnapshot() {
		return useTrasportableSnapshot;
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
	public long getDwEncryptType() {
		return dwEncryptType;
	}
	public void setDwEncryptType(long dwEncryptType) {
		this.dwEncryptType = dwEncryptType;
	}
	public String getPwszEncryptPassword() {
		return pwszEncryptPassword;
	}
	public void setPwszEncryptPassword(String pwszEncryptPassword) {
		this.pwszEncryptPassword = pwszEncryptPassword;
	}
	public long getDwEncryptTypeCopySession() {
		return dwEncryptTypeCopySession;
	}
	public void setDwEncryptTypeCopySession(long dwEncryptTypeCopySession) {
		this.dwEncryptTypeCopySession = dwEncryptTypeCopySession;
	}
	public String getPwszEncryptPasswordCopySession() {
		return pwszEncryptPasswordCopySession;
	}
	public void setPwszEncryptPasswordCopySession(String pwszEncryptPassword) {
		this.pwszEncryptPasswordCopySession = pwszEncryptPassword;
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
	public List<JJobScriptNode> getPAFNodeList() {
		return pAFNodeList;
	}
	public void setPAFNodeList(List<JJobScriptNode> nodeList) {
		pAFNodeList = nodeList;
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
	public List<JJobScriptVSphereNode> getpVSphereNodeList() {
		return pVSphereNodeList;
	}
	public void setpVSphereNodeList(List<JJobScriptVSphereNode> pVSphereNodeList) {
		this.pVSphereNodeList = pVSphereNodeList;
	}
	public List<JJobScriptRecoverVMNode> getpRecoverVMNodeList() {
		return pRecoverVMNodeList;
	}
	public void setpRecoverVMNodeList(
			List<JJobScriptRecoverVMNode> pRecoverVMNodeList) {
		this.pRecoverVMNodeList = pRecoverVMNodeList;
	}
	public long getUlJobAttribute() {
		return ulJobAttribute;
	}
	public void setUlJobAttribute(long ulJobAttribute) {
		this.ulJobAttribute = ulJobAttribute;
	}
	public String getLauncherInstanceUUID() {
		return launcherInstanceUUID;
	}
	public void setLauncherInstanceUUID(String launcherInstanceUUID) {
		this.launcherInstanceUUID = launcherInstanceUUID;
	}
	
	public int getPreAllocationSpace() {
		return preAllocationSpace;
	}
	public void setPreAllocationSpace(int preAllocationSpace) {
		this.preAllocationSpace = preAllocationSpace;
	}
	public String getRpsPolicyName() {
		return rpsPolicyName;
	}
	public void setRpsPolicyName(String rpsPolicyName) {
		this.rpsPolicyName = rpsPolicyName;
	}
	public String getRpsHostname() {
		return rpsHostname;
	}
	public void setRpsHostname(String rpsHostname) {
		this.rpsHostname = rpsHostname;
	}
	public String getRpsSID() {
		return rpsSID;
	}
	public void setRpsSID(String rpsSID) {
		this.rpsSID = rpsSID;
	}
	public String getRpsPolicyID() {
		return rpsPolicyID;
	}
	public void setRpsPolicyID(String rpsPolicyID) {
		this.rpsPolicyID = rpsPolicyID;
	}
	public String getRPSDataStoreName() {
		return RPSDataStoreName;
	}
	public void setRPSDataStoreName(String rPSDataStoreName) {
		RPSDataStoreName = rPSDataStoreName;
	}
	public String getRPSDataStoreDisplayName() {
		return RPSDataStoreDisplayName;
	}
	public void setRPSDataStoreDisplayName(String rPSDataStoreDisplayName) {
		RPSDataStoreDisplayName = rPSDataStoreDisplayName;
	}
	public String getPwszVDiskPassword() {
		return pwszVDiskPassword;
	}
	public void setPwszVDiskPassword(String pwszVDiskPassword) {
		this.pwszVDiskPassword = pwszVDiskPassword;
	}
	public long getUllScheduledTime() {
		return ullScheduledTime;
	}
	public void setUllScheduledTime(long ullScheduledTime) {
		this.ullScheduledTime = ullScheduledTime;
	}
	public List<JJobScriptBackupVC> getVAppVCInfos() {
		return vAppVCInfos;
	}
	public void setVAppVCInfos(List<JJobScriptBackupVC> vAppVCInfos) {
		this.vAppVCInfos = vAppVCInfos;
	}
	public int getVAppVCCount() {
		return vAppVCCount;
	}
	public void setVAppVCCount(int vAppVCCount) {
		this.vAppVCCount = vAppVCCount;
	}
	public long getDwMasterJobId() {
		return dwMasterJobId;
	}
	public void setDwMasterJobId(long dwMasterJobId) {
		this.dwMasterJobId = dwMasterJobId;
	}
	public String getGeneratedDestination() {
		return generatedDestination;
	}
	public void setGeneratedDestination(String generatedDestination) {
		this.generatedDestination = generatedDestination;
	}
	// October sprint 
	public List<JJobScriptStorageAppliance> getpAFStorageApplianceList() {
		return pAFStorageApplianceList;
	}
	public void setpAFStorageApplianceList(List<JJobScriptStorageAppliance> pAFStorageApplianceList) {
		this.pAFStorageApplianceList = pAFStorageApplianceList;
	}
	public int getnStorageApplianceItems() {
		return nStorageApplianceItems;
	}
	public void setnStorageApplianceItems(int nStorageApplianceItems) {
		this.nStorageApplianceItems = nStorageApplianceItems;
	}
	public JJobScriptArchiveInfo getArchiveInfo() {
		return archiveInfo;
	}
	public void setArchiveInfo(JJobScriptArchiveInfo archiveInfo) {
		this.archiveInfo = archiveInfo;
	}
	
	public void setBRetainEncryptionAsSource(boolean bRetainEncryptionAsSource){
		this.bRetainEncryptionAsSource=bRetainEncryptionAsSource;
	}
	
	public boolean getBRetainEncryptionAsSource(){
		return bRetainEncryptionAsSource;
	}
	
}
