package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class EdgeHost {
	public static final String DEF_IPADDRESS = "0.0.0.0";
	
	private int rhostid;
	private Date lastupdated;
	private String rhostname;
	private String ipaddress;
	private String osdesc;
	private String ostype;
	private int IsVisible;
	private int appStatus;
	private String ServerPrincipalName;
	private int rhostType;
	private int arcserveManagedStatus;
	private int d2dManagedStatus;
	private String arcserveBackupVersion;
	private String d2DMajorversion;
	private String d2dMinorversion;
	private String d2dBuildnumber;
	private String d2dUpdateversionnumber;
	private ABFuncServerType ARCserveType;
	private int arcserveProtocol;
	private int d2dProtocol;
	private String d2dPort;
	private String arcservePort;
	private int arcSyncStatus;
	private int arcSyncChangeStatus;
	private int d2dStatus;
	private int policyId;
	private String policyName;
	private int policyContentFlag;
	private int policytype;
	private int deploystatus;
	private Date lastsuccdeploy;
	private String vmname;
	private String vmInstanceUuid;
	private String esxName;
	private int esxType;
	private String esxHost;
	private int deployreason; 
	private int verifyStatus;
	private int vmStatus;

	// D2D and ARCserve Backup Last sync time by lijwe02 on 2010-11-04
	private Date d2dLastUpdateTime; // the last update time for D2D
	private Date asbuLastUpdateTime; // the last update time for ARCserve sync

	private String username;
	private @NotPrintAttribute String password;
	
	private String nodeDescription;
	
	private int hasD2DStatusInfo = 0;
	
	// last d2d backup info
	private Date d2dLastBackupStartTime;
	private int d2dLastBackupType;
	private int d2dLastBackupJobStatus;
	private int d2dLastBackupStatus;
	
	// d2d recovery point info
	private int d2dRecPointRetentionCount;
	private int d2dRecPointCount;
	private String d2dRecPointMounted;
	private int d2dRecPointStatus;
	private boolean d2dIsUseBackupSets;
	
	// d2d destination info
	private String d2dDestPath;
	private int d2dDestAccessible;
	private long d2dDestFreeSpace;
	private int d2dDestEstimatedBackupCount;
	private int d2dDestStatus;
	
	// overall info
	private int d2dOverallStatus;
	

	private int isDriverInstalled;
	private int isRestarted;
	private int estimatedValue;
	private int isBackupConfiged;
	
	private String d2DUUID;
	private String authUUID;
	
	private int converterId;
	private String converter;
	private String recoveryPointFolder;
	private String converterUsername;
	private String  converterPassword;
	private int converterPort;
	private int converterProtocol;
	
	private int protectionTypeBitmap;
	private int rpsManagedStatus;

	//rps d2d nodes
	private String rpsName;
	
	private int hasVSBStatusInfo = 0;
	// VSB information
	private String standbyVMName;
	private String standbyVMRecentSnapshot;
	private long snapshotTimeZoneOffset;
	private int    vmPowerStatus;
	private String currentRunningSnapshot;
	
	//Last VSB job info
	private Date d2dLastVSBStartTime;
	private int d2dLastVSBType;
	private int d2dLastVSBJobStatus;
	private int d2dLastVSBStatus;
	private int autoOfflieCopyStatus;//Next standby is paused or resumed
	
	//Snapshot info
	private int d2dSnapShotRetentionCount;
	private int d2dSnapShotCount;
	
	//VSB destination
	private long vsbDestFreeSpace;
	private int  vsbDestStatus;
	
	//monitor
	private int heartbeatStatus;
	
	private int vsbOverallStatus;
	
	private long lastUpdateTimeDiffSeconds;
	
	private int timezone;
	
	//remote deploy
	private int installationType;
	private int remoteDeployStatus;
	private int deployTaskStatus;
	private Date remoteDeployTime;
	
	private int enableStatus;
	private int machineType;
	
	//added by tonyzhai, used for hypervisor information
	private String hypervisorHostName;
	private String hypervisorUsername;
	private String hypervisorPassword;
	private Protocol hypervisorProtocol;
	private int hypervisorPort;
	private int hypervisorSocketCount;
	private int hypervisorVisible;
	private int hypervisorServerType;
	private int hypervisorEssential;
	
	//added by tonyzhai, missing vm information
	private String vmGuestOS;
	private String vmUUID;
	private String vmXPath;
	
	//asbu authentication type
	private int authMode;

	// siteName, node belongs to which site/gateway
	private String siteName;
	private String fqdnNames;
	
	private int gatewayId = 0;
	
	public String getRpsName() {
		return rpsName;
	}

	public void setRpsName(String rpsName) {
		this.rpsName = rpsName;
	}
	
	public String getConverterUsername() {
		return converterUsername;
	}

	public void setConverterUsername(String converterUsername) {
		this.converterUsername = converterUsername;
	}

	public String getConverterPassword() {
		return converterPassword;
	}

	public void setConverterPassword(String converterPassword) {
		this.converterPassword = converterPassword;
	}

	public int getConverterPort() {
		return converterPort;
	}

	public void setConverterPort(int converterPort) {
		this.converterPort = converterPort;
	}

	public int getConverterProtocol() {
		return converterProtocol;
	}

	public void setConverterProtocol(int converterProtocol) {
		this.converterProtocol = converterProtocol;
	}

	@EncryptSave
	public String getD2DUUID() {
		return d2DUUID;
	}

	public void setD2DUUID(String d2duuid) {
		d2DUUID = d2duuid;
	}

	public int getIsDriverInstalled() {
		return isDriverInstalled;
	}
	
	public void setIsDriverInstalled(int isDriverInstalled) {
		this.isDriverInstalled = isDriverInstalled;
	}
	
	public int getIsRestarted() {
		return isRestarted;
	}
	
	public void setIsRestarted(int isRestarted) {
		this.isRestarted = isRestarted;
	}
	
	public int getEstimatedValue() {
		return estimatedValue;
	}
	
	public void setEstimatedValue(int estimatedValue) {
		this.estimatedValue = estimatedValue;
	}
	
	public int getIsBackupConfiged() {
		return isBackupConfiged;
	}
	
	public void setIsBackupConfiged(int isBackupConfiged) {
		this.isBackupConfiged = isBackupConfiged;
	}

	public void setNodeDescription(String nodeDescription)
	{
		this.nodeDescription = nodeDescription;
	}
	
	public String getNodeDescription()
	{
		return this.nodeDescription;
	}

	public Date getD2dLastUpdateTime() {
		return d2dLastUpdateTime;
	}

	public void setD2dLastUpdateTime(Date d2dLastUpdateTime) {
		this.d2dLastUpdateTime = d2dLastUpdateTime;
	}

	public Date getAsbuLastUpdateTime() {
		return asbuLastUpdateTime;
	}

	public void setAsbuLastUpdateTime(Date asbuLastUpdateTime) {
		this.asbuLastUpdateTime = asbuLastUpdateTime;
	}

	public void setD2dPort(String d2dPort)
	{
		this.d2dPort = d2dPort;
	}
	public String getD2dPort()
	{
		return this.d2dPort;
	}
	public void setArcservePort(String arcservePort)
	{
		this.arcservePort = arcservePort;
	}
	public String getArcservePort()
	{
		return this.arcservePort;
	}
	public void setD2dProtocol(int d2dProtocol)
	{
		this.d2dProtocol = d2dProtocol;
	}
	public int getD2dProtocol()
	{
		return this.d2dProtocol;
	}
	
	public int getArcserveProtocol()
	{
		return this.arcserveProtocol ;
	}
	public void setArcserveProtocol(int arcserveProtocol)
	{
		this.arcserveProtocol = arcserveProtocol;
	}
	public ABFuncServerType getARCserveType() {
		return ARCserveType;
	}

	public void setARCserveType(ABFuncServerType aRCserveType) {
		ARCserveType = aRCserveType;
	}

	public String getArcserveBackupVersion() {
		return arcserveBackupVersion;
	}

	public void setArcserveBackupVersion(String arcserveBackupVersion) {
		this.arcserveBackupVersion = arcserveBackupVersion;
	}

	public String getD2DMajorversion() {
		return d2DMajorversion;
	}

	public void setD2DMajorversion(String d2dMajorversion) {
		d2DMajorversion = d2dMajorversion;
	}

	public String getD2dMinorversion() {
		return d2dMinorversion;
	}

	public void setD2dMinorversion(String d2dMinorversion) {
		this.d2dMinorversion = d2dMinorversion;
	}

	public String getD2dBuildnumber() {
		return d2dBuildnumber;
	}

	public void setD2dBuildnumber(String d2dBuildnumber) {
		this.d2dBuildnumber = d2dBuildnumber;
	}

	public String getD2dUpdateversionnumber() {
		return d2dUpdateversionnumber;
	}

	public void setD2dUpdateversionnumber(String d2dUpdateversionnumber) {
		this.d2dUpdateversionnumber = d2dUpdateversionnumber;
	}

	public int getArcserveManagedStatus() {
		return arcserveManagedStatus;
	}

	public void setArcserveManagedStatus(int arcserveManagedStatus) {
		this.arcserveManagedStatus = arcserveManagedStatus;
	}

	public int getD2dManagedStatus() {
		return d2dManagedStatus;
	}

	public void setD2dManagedStatus(int d2dManagedStatus) {
		this.d2dManagedStatus = d2dManagedStatus;
	}

	public int getRhostid() {
		return rhostid;
	}

	public void setRhostid(int rhostid) {
		this.rhostid = rhostid;
	}

	public Date getLastupdated() {
		return lastupdated;
	}

	public void setLastupdated(Date lastupdated) {
		this.lastupdated = lastupdated;
	}

	public String getRhostname() {
		return rhostname;
	}

	public void setRhostname(String rhostname) {
		this.rhostname = rhostname;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getOsdesc() {
		return osdesc;
	}

	public void setOsdesc(String osdesc) {
		this.osdesc = osdesc;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
	
	public int getIsVisible() {
		return IsVisible;
	}

	public void setIsVisible(int isVisible) {
		IsVisible = isVisible;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public String getServerPrincipalName() {
		return ServerPrincipalName;
	}

	public void setServerPrincipalName(String serverPrincipalName) {
		ServerPrincipalName = serverPrincipalName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@EncryptSave  
	public String getPassword() {
		return password;
	}

	public int getRhostType() {
		return rhostType;
	}

	public void setRhostType(int rhostType) {
		this.rhostType = rhostType;
	}
	
	public int getArcSyncStatus() {
		return arcSyncStatus;
	}

	public void setArcSyncStatus(int arcSyncStatus) {
		this.arcSyncStatus = arcSyncStatus;
	}

	public int getArcSyncChangeStatus() {
		return arcSyncChangeStatus;
	}

	public void setArcSyncChangeStatus(int arcSyncChangeStatus) {
		this.arcSyncChangeStatus = arcSyncChangeStatus;
	}

	public int getD2dStatus() {
		return d2dStatus;
	}

	public void setD2dStatus(int d2dStatus) {
		this.d2dStatus = d2dStatus;
	}

	public int getPolicyId() {
		return policyId;
	}

	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	public int getPolicyContentFlag() {
		return policyContentFlag;
	}

	public void setPolicyContentFlag(int policyContentFlag) {
		this.policyContentFlag = policyContentFlag;
	}

	public int getPolicytype() {
		return policytype;
	}

	public void setPolicytype(int policytype) {
		this.policytype = policytype;
	}

	public int getDeploystatus() {
		return deploystatus;
	}

	public void setDeploystatus(int deploystatus) {
		this.deploystatus = deploystatus;
	}

	public int getDeployReason(){
		return deployreason;
	}
	
	public void setDeployReason(int deployreason){
		this.deployreason = deployreason;
	}
	
	public Date getLastsuccdeploy() {
		return lastsuccdeploy;
	}

	public void setLastsuccdeploy(Date lastsuccdeploy) {
		this.lastsuccdeploy = lastsuccdeploy;
	}

	public String getVmname() {
		return vmname;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}

	public String getVmInstanceUuid() {
		return vmInstanceUuid;
	}

	public void setVmInstanceUuid(String vmInstanceUuid) {
		this.vmInstanceUuid = vmInstanceUuid;
	}

	public String getEsxName() {
		return esxName;
	}

	public void setEsxName(String esxName) {
		this.esxName = esxName;
	}

	public int getEsxType() {
		return esxType;
	}

	public void setEsxType(int esxType) {
		this.esxType = esxType;
	}

	public String getEsxHost() {
		return esxHost;
	}

	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public int getHasD2DStatusInfo()
	{
		return hasD2DStatusInfo;
	}

	public void setHasD2DStatusInfo( int hasD2DStatusInfo )
	{
		this.hasD2DStatusInfo = hasD2DStatusInfo;
	}

	public Date getD2dLastBackupStartTime()
	{
		return d2dLastBackupStartTime;
	}

	public void setD2dLastBackupStartTime( Date d2dLastBackupStartTime )
	{
		this.d2dLastBackupStartTime = d2dLastBackupStartTime;
	}

	public int getD2dLastBackupType()
	{
		return d2dLastBackupType;
	}

	public void setD2dLastBackupType( int d2dLastBackupType )
	{
		this.d2dLastBackupType = d2dLastBackupType;
	}

	public int getD2dLastBackupJobStatus()
	{
		return d2dLastBackupJobStatus;
	}

	public void setD2dLastBackupJobStatus( int d2dLastBackupJobStatus )
	{
		this.d2dLastBackupJobStatus = d2dLastBackupJobStatus;
	}

	public int getD2dLastBackupStatus()
	{
		return d2dLastBackupStatus;
	}

	public void setD2dLastBackupStatus( int d2dLastBackupStatus )
	{
		this.d2dLastBackupStatus = d2dLastBackupStatus;
	}

	public int getD2dRecPointRetentionCount()
	{
		return d2dRecPointRetentionCount;
	}

	public void setD2dRecPointRetentionCount( int d2dRecPointRetentionCount )
	{
		this.d2dRecPointRetentionCount = d2dRecPointRetentionCount;
	}

	public int getD2dRecPointCount()
	{
		return d2dRecPointCount;
	}

	public void setD2dRecPointCount( int d2dRecPointCount )
	{
		this.d2dRecPointCount = d2dRecPointCount;
	}

	public String getD2dRecPointMounted()
	{
		return d2dRecPointMounted;
	}

	public void setD2dRecPointMounted( String d2dRecPointMounted )
	{
		this.d2dRecPointMounted = d2dRecPointMounted;
	}

	public int getD2dRecPointStatus()
	{
		return d2dRecPointStatus;
	}

	public void setD2dRecPointStatus( int d2dRecPointStatus )
	{
		this.d2dRecPointStatus = d2dRecPointStatus;
	}

	public boolean getD2dIsUseBackupSets()
	{
		return d2dIsUseBackupSets;
	}
	
	public void setD2dIsUseBackupSets( boolean d2dIsUseBackupSets )
	{
		this.d2dIsUseBackupSets = d2dIsUseBackupSets;
	}
	
	public String getD2dDestPath()
	{
		return d2dDestPath;
	}

	public void setD2dDestPath( String d2dDestPath )
	{
		this.d2dDestPath = d2dDestPath;
	}

	public int getD2dDestAccessible()
	{
		return d2dDestAccessible;
	}

	public void setD2dDestAccessible( int d2dDestAccessible )
	{
		this.d2dDestAccessible = d2dDestAccessible;
	}

	public long getD2dDestFreeSpace()
	{
		return d2dDestFreeSpace;
	}

	public void setD2dDestFreeSpace( long d2dDestFreeSpace )
	{
		this.d2dDestFreeSpace = d2dDestFreeSpace;
	}

	public int getD2dDestEstimatedBackupCount()
	{
		return d2dDestEstimatedBackupCount;
	}

	public void setD2dDestEstimatedBackupCount( int d2dDestEstimatedBackupCount )
	{
		this.d2dDestEstimatedBackupCount = d2dDestEstimatedBackupCount;
	}

	public int getD2dDestStatus()
	{
		return d2dDestStatus;
	}

	public void setD2dDestStatus( int d2dDestStatus )
	{
		this.d2dDestStatus = d2dDestStatus;
	}

	public int getD2dOverallStatus()
	{
		return d2dOverallStatus;
	}

	public void setD2dOverallStatus( int d2dOverallStatus )
	{
		this.d2dOverallStatus = d2dOverallStatus;
	}

	public int getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(int vmStatus) {
		this.vmStatus = vmStatus;
	}

	public int getConverterId() {
		return converterId;
	}

	public void setConverterId(int converterId) {
		this.converterId = converterId;
	}

	public String getConverter() {
		return converter;
	}

	public void setConverter(String converter) {
		this.converter = converter;
	}

	public String getRecoveryPointFolder() {
		return recoveryPointFolder;
	}

	public void setRecoveryPointFolder(String recoveryPointFolder) {
		this.recoveryPointFolder = recoveryPointFolder;
	}

	public int getProtectionTypeBitmap() {
		return protectionTypeBitmap;
	}
	
	public void setProtectionTypeBitmap(int protectionTypeBitmap) {
		this.protectionTypeBitmap = protectionTypeBitmap;
	}

	public int getRpsManagedStatus() {
		return rpsManagedStatus;
	}

	public void setRpsManagedStatus(int rpsManagedStatus) {
		this.rpsManagedStatus = rpsManagedStatus;
	}

	public String getStandbyVMName() {
		return standbyVMName;
	}

	public void setStandbyVMName(String standbyVMName) {
		this.standbyVMName = standbyVMName;
	}

	public String getStandbyVMRecentSnapshot() {
		return standbyVMRecentSnapshot;
	}

	public void setStandbyVMRecentSnapshot(String standbyVMRecentSnapshot) {
		this.standbyVMRecentSnapshot = standbyVMRecentSnapshot;
	}
	
	public long getSnapshotTimeZoneOffset() {
		return snapshotTimeZoneOffset;
	}

	public void setSnapshotTimeZoneOffset(long snapshotTimeZoneOffset) {
		this.snapshotTimeZoneOffset = snapshotTimeZoneOffset;
	}
	
	public int getVmPowerStatus() {
		return vmPowerStatus;
	}

	public void setVmPowerStatus(int vmPowerStatus) {
		this.vmPowerStatus = vmPowerStatus;
	}

	public Date getD2dLastVSBStartTime() {
		return d2dLastVSBStartTime;
	}

	public void setD2dLastVSBStartTime(Date d2dLastVSBStartTime) {
		this.d2dLastVSBStartTime = d2dLastVSBStartTime;
	}

	public int getD2dLastVSBType() {
		return d2dLastVSBType;
	}

	public void setD2dLastVSBType(int d2dLastVSBType) {
		this.d2dLastVSBType = d2dLastVSBType;
	}

	public int getD2dLastVSBJobStatus() {
		return d2dLastVSBJobStatus;
	}

	public void setD2dLastVSBJobStatus(int d2dLastVSBJobStatus) {
		this.d2dLastVSBJobStatus = d2dLastVSBJobStatus;
	}

	public int getD2dLastVSBStatus() {
		return d2dLastVSBStatus;
	}

	public void setD2dLastVSBStatus(int d2dLastVSBStatus) {
		this.d2dLastVSBStatus = d2dLastVSBStatus;
	}

	public int getAutoOfflieCopyStatus() {
		return autoOfflieCopyStatus;
	}

	public void setAutoOfflieCopyStatus(int autoOfflieCopyStatus) {
		this.autoOfflieCopyStatus = autoOfflieCopyStatus;
	}

	public int getD2dSnapShotRetentionCount() {
		return d2dSnapShotRetentionCount;
	}

	public void setD2dSnapShotRetentionCount(int d2dSnapShotRetentionCount) {
		this.d2dSnapShotRetentionCount = d2dSnapShotRetentionCount;
	}

	public int getD2dSnapShotCount() {
		return d2dSnapShotCount;
	}

	public void setD2dSnapShotCount(int d2dSnapShotCount) {
		this.d2dSnapShotCount = d2dSnapShotCount;
	}

	public long getVsbDestFreeSpace() {
		return vsbDestFreeSpace;
	}

	public void setVsbDestFreeSpace(long vsbDestFreeSpace) {
		this.vsbDestFreeSpace = vsbDestFreeSpace;
	}

	public int getVsbDestStatus() {
		return vsbDestStatus;
	}

	public void setVsbDestStatus(int vsbDestStatus) {
		this.vsbDestStatus = vsbDestStatus;
	}

	public int getHeartbeatStatus() {
		return heartbeatStatus;
	}

	public void setHeartbeatStatus(int heartbeatStatus) {
		this.heartbeatStatus = heartbeatStatus;
	}

	public int getHasVSBStatusInfo() {
		return hasVSBStatusInfo;
	}

	public void setHasVSBStatusInfo(int hasVSBStatusInfo) {
		this.hasVSBStatusInfo = hasVSBStatusInfo;
	}

	public int getVsbOverallStatus() {
		return vsbOverallStatus;
	}

	public void setVsbOverallStatus(int vsbOverallStatus) {
		this.vsbOverallStatus = vsbOverallStatus;
	}

	@EncryptSave
	public String getCurrentRunningSnapshot() {
		return currentRunningSnapshot;
	}

	public void setCurrentRunningSnapshot(String currentRunningSnapshot) {
		this.currentRunningSnapshot = currentRunningSnapshot;
	}

	public long getLastUpdateTimeDiffSeconds() {
		return lastUpdateTimeDiffSeconds;
	}

	public void setLastUpdateTimeDiffSeconds(long lastUpdateTimeDiffSeconds) {
		this.lastUpdateTimeDiffSeconds = lastUpdateTimeDiffSeconds;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}
	
	public int getInstallationType() {
		return installationType;
	}

	public void setInstallationType(int installationType) {
		this.installationType = installationType;
	}

	public int getRemoteDeployStatus() {
		return remoteDeployStatus;
	}

	public void setRemoteDeployStatus(int remoteDeployStatus) {
		this.remoteDeployStatus = remoteDeployStatus;
	}

	public Date getRemoteDeployTime() {
		return remoteDeployTime;
	}

	public void setRemoteDeployTime(Date remoteDeployTime) {
		this.remoteDeployTime = remoteDeployTime;
	}

	public int getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(int enableStatus) {
		this.enableStatus = enableStatus;
	}

	public int getMachineType() {
		return machineType;
	}

	public void setMachineType(int machineType) {
		this.machineType = machineType;
	}

	public String getHypervisorHostName() {
		return hypervisorHostName;
	}

	public void setHypervisorHostName(String hypervisorHostName) {
		this.hypervisorHostName = hypervisorHostName;
	}

	public String getHypervisorUsername() {
		return hypervisorUsername;
	}

	public void setHypervisorUsername(String hypervisorUsername) {
		this.hypervisorUsername = hypervisorUsername;
	}

	public String getHypervisorPassword() {
		return hypervisorPassword;
	}

	public void setHypervisorPassword(String hypervisorPassword) {
		this.hypervisorPassword = hypervisorPassword;
	}

	public Protocol getHypervisorProtocol() {
		return hypervisorProtocol;
	}

	public void setHypervisorProtocol(Protocol hypervisorProtocol) {
		this.hypervisorProtocol = hypervisorProtocol;
	}

	public int getHypervisorPort() {
		return hypervisorPort;
	}

	public void setHypervisorPort(int hypervisorPort) {
		this.hypervisorPort = hypervisorPort;
	}

	public int getHypervisorSocketCount() {
		return hypervisorSocketCount;
	}

	public void setHypervisorSocketCount(int hypervisorSocketCount) {
		this.hypervisorSocketCount = hypervisorSocketCount;
	}

	public int getHypervisorVisible() {
		return hypervisorVisible;
	}

	public void setHypervisorVisible(int hypervisorVisible) {
		this.hypervisorVisible = hypervisorVisible;
	}

	public int getHypervisorServerType() {
		return hypervisorServerType;
	}

	public void setHypervisorServerType(int hypervisorServerType) {
		this.hypervisorServerType = hypervisorServerType;
	}

	public int getHypervisorEssential() {
		return hypervisorEssential;
	}

	public void setHypervisorEssential(int hypervisorEssential) {
		this.hypervisorEssential = hypervisorEssential;
	}

	public String getVmGuestOS() {
		return vmGuestOS;
	}

	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}

	public String getVmUUID() {
		return vmUUID;
	}

	public void setVmUUID(String vmUUID) {
		this.vmUUID = vmUUID;
	}

	public String getVmXPath() {
		return vmXPath;
	}

	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}

	public int getAuthMode() {
		return authMode;
	}

	public void setAuthMode(int authMode) {
		this.authMode = authMode;
	}

	public int getDeployTaskStatus() {
		return deployTaskStatus;
	}

	public void setDeployTaskStatus(int deployTaskStatus) {
		this.deployTaskStatus = deployTaskStatus;
	}

	public String getAuthUUID() {
		return authUUID;
	}

	public void setAuthUUID(String authUUID) {
		this.authUUID = authUUID;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getFqdnNames() {
		return fqdnNames;
	}

	public void setFqdnNames(String fqdnNames) {
		this.fqdnNames = fqdnNames;
	}

}
