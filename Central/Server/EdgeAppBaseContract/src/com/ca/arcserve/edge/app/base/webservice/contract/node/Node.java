package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.common.WebServiceConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.LinuxD2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ProxyInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * Detailed information of a node.
 * 
 * @author panbo01
 *
 */
@XmlSeeAlso( {
	ProtectionType.class,
	HostType.class,
	VMVerifyStatus.class,
	PolicyTypes.class,
	RebootType.class,
	DeployStatus.class,
	PlanEnableStatus.class,
	VMStatus.class
	} )
public class Node implements Serializable, BeanModelTag{
	private static final long serialVersionUID = 6886590271338909448L;
	private int id;
	private int appStatus;
	private int nodeType;
	private Date lastupdated;
	private String hostname;
	private String domainName;	
	private String ipaddress;
	private String ServerPrincipalName;
	private String osDescription;
	private String osVersion;
	private String osType;//issue 143314 <zhaji22>
	private boolean sqlServerInstalled;
	private boolean exchangeInstalled;
	private boolean d2dInstalled;
	private boolean d2dOnDInstalled;	
	private boolean arcserveInstalled;
	private boolean linuxD2DInstalled;
	private NodeManagedStatus arcserveManaged;
	private NodeManagedStatus d2dManaged;
	private String arcserveBackupVersion;
	private Version d2dVersion = new Version();
	private ABFuncServerType arcserveType;
	private String policyName;
	private int policyContentFlag;
	private int policyType;
	private int policyDeployStatus;
	private int policyDeployReason; 
	private String Warning;
	private int warnningAcknowledged;
	private String Error;
	private Date lastSuccessfulPolicyDeploy;
	private NodeSyncStatus syncStatus;
	private NodeBkpStatus bkpStatus;
	// GDBId is added by Weiping Li on 2010-10-21 for Issue:19750285 CAN'T DELETE D2D ALSO BRANCH
	// When select a node, we should check GDBId is 0 or not, if zero, then enable delete button
	private int GDBId = 0; 
	private boolean isJobRunning = false;
	private boolean isWaitingJobRunning = false;
	
	private WebServiceConnectInfo d2dWebServiceConnectInfo = new WebServiceConnectInfo();
	private WebServiceConnectInfo asbuWebServiceConnectInfo = new WebServiceConnectInfo();

	// added the following 4 fields for two new columns by lijwe02, D2D and
	// ARCserve Backup Last sync time
	private Date d2dLastUpdateTime; // the last update time for D2D
	private boolean d2dLastUpdateWarning = true; // is it warning for D2D last
													// sync time
	private Date asbuLastUpdateTime; // the last update time for ARCserve sync
	private boolean asbuLastUpdateWarning = true; // is it warning for ARCserve
													// Backup
	private int d2dSyncFrequency; // D2D synchronize frequency
	private int asbuSyncFrequency; // ARCserve Backup synchronize frequency
	
	private String username;
	private @NotPrintAttribute String password;
	
	private String nodeDescription;
	private VCMMonitor vcmMonitor;
	private String vcmSettings;
	
	private long policyIDForEsx = 0;
	private boolean isPhysicalMachine;
	private boolean isVMwareMachine;
	private boolean isHyperVMachine;
	private boolean hasVCMMonitorFlag;
	private boolean isVCMMonitee;
	private boolean isVMImportFromVSphere;
	private boolean isLinuxNode;
	private boolean importedFromRHA;
	private boolean importedFromRHAWithHBBU;
	private boolean importedFromRPS;
	private boolean importedFromRPSReplication;
	private String vmName;
	private String vmInstanceUUID;
	private String hyperVisor;
	private String esxName;
	private int verifyStatus;
	private int vmStatus;
	private int isVisible;
	
	private int rhostType;
	
	private int protectionTypeBitmap;
	private NodeManagedStatus rpsManagedStatus;
	
	private String displayJobPhase = "";
	private long jobPhase;
	private int mergeJobPhase;
	private boolean pauseMergeJobEnabled;
	private boolean resumeMergeJobEnabled;

	
	private List<NodeGroup> groupList;
	
	private String d2DUUID;
	private String authUUID;
	private boolean isVMRunning;
	private String runningVMName;

	private int converterId;
	private String converter;
	private String recoveryPointFolder;
	private String converterUsername;
	private String  converterPassword;
	private int converterPort;
	private int converterProtocol;

	private Node proxyNode;
	
	private String rpsServer;
	
	private boolean vmWindowsOS;

	private List<JobHistory> lstJobHistory;
	
	private Date d2dLastBackupStartTime;
	private JobStatus d2dLastBackupJobStatus;

	// VSB information
	private D2DStatusInfo vsbSatusInfo;
	private boolean isCrossSiteVsb;
	
	private boolean hyperVVmAsPhysicalMachine;
	
	private int timezone;
	
	//remote deploy
	private int installationType; //RebootType 
	private int remoteDeployStatus; //DeployStatus
	private int deployTaskStatus;//task status
	private Date remoteDeployTime;
	
	private int enableStatus;
	private LicenseMachineType machineType;
	private boolean passwordVerified;
	//added by zhati04, used for import function which need validate hypervisor
	private DiscoveryESXOption discoveryESXOption; 
	
	private NodeVcloudSummary vcloudProperties;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	// siteName, node belongs to which site/gateway
	private String siteName;
	private boolean isLocalSite;
	
	private boolean consoleInstalled;
	
	private ProxyInfoSummary proxyInfos;
	
	private LinuxD2DInfoSummary linuxD2DInfoSummary;
	
	
	// banar05
	private String currentConsoleMachineNameForCollectDiag;
	private String currentConsoleIPForCollectDiag;
	
	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	private boolean scheduleDeployCanceled = false;
	
	public boolean isScheduleDeployCanceled() {
		return scheduleDeployCanceled;
	}

	public void setScheduleDeployCanceled(boolean scheduleDeployCanceled) {
		this.scheduleDeployCanceled = scheduleDeployCanceled;
	}

	/**
	 * Get server name of the RPS for the node.
	 * 
	 * @return
	 */
	@Deprecated
	public String getRpsServer() {
		return rpsServer;
	}

	/**
	 * Set server name of the RPS for the node.
	 * 
	 * @param rpsServer
	 */
	@Deprecated
	public void setRpsServer(String rpsServer) {
		this.rpsServer = rpsServer;
	}
	
	/**
	 * Get the user name of the converter of the node.
	 * 
	 * @return
	 */
	public String getConverterUsername() {
		return converterUsername;
	}

	/**
	 * Set the user name of the converter of the node.
	 * 
	 * @param converterUsername
	 */
	public void setConverterUsername(String converterUsername) {
		this.converterUsername = converterUsername;
	}

	/**
	 * Get the password of the converter of the node.
	 * 
	 * @return
	 */
	public String getConverterPassword() {
		return converterPassword;
	}

	/**
	 * Set the password of the converter of the node.
	 * 
	 * @param converterPassword
	 */
	public void setConverterPassword(String converterPassword) {
		this.converterPassword = converterPassword;
	}

	/**
	 * Get the web service port of the converter of the node.
	 * 
	 * @return
	 */
	public int getConverterPort() {
		return converterPort;
	}

	/**
	 * Set the web service port of the converter of the node.
	 * 
	 * @param converterPort
	 */
	public void setConverterPort(int converterPort) {
		this.converterPort = converterPort;
	}

	/**
	 * Get the web service protocol of the converter of the node.
	 * 
	 * @return
	 */
	public int getConverterProtocol() {
		return converterProtocol;
	}

	/**
	 * Set the web service port of the converter of the node.
	 * 
	 * @param converterProtocol
	 */
	public void setConverterProtocol(int converterProtocol) {
		this.converterProtocol = converterProtocol;
	}

	/**
	 * Get the machine name of the running VM that was converted for the node.
	 * 
	 * @return
	 */
	public String getRunningVMName() {
		return runningVMName;
	}

	/**
	 * Set the machine name of the running VM that was converted for the node.
	 * 
	 * @param runningVMName
	 */
	public void setRunningVMName(String runningVMName) {
		this.runningVMName = runningVMName;
	}

	/**
	 * Whether the converted VM for the node is running.
	 * 
	 * @return
	 */
	public boolean isVMRunning() {
		return isVMRunning;
	}

	/**
	 * Set whether the converted VM for the node is running.
	 * 
	 * @param isVMRunning
	 */
	public void setVMRunning(boolean isVMRunning) {
		this.isVMRunning = isVMRunning;
	}

	/**
	 * Whether there is a job running on the node.
	 * 
	 * @return
	 */
	public boolean isJobRunning() {
		return isJobRunning;
	}

	/**
	 * Set whether there is a job running on the node.
	 * 
	 * @param isJobRunning
	 */
	public void setJobRunning(boolean isJobRunning) {
		this.isJobRunning = isJobRunning;
	}
	
	/**
	 * Whether there is a pending job will be run on the node.
	 * 
	 * @return
	 */
	public boolean isWaitingJobRunning() {
		return isWaitingJobRunning;
	}

	/**
	 * Set whether there is a pending job will be run on the node.
	 * 
	 * @param isWaitingJobRunning
	 */
	public void setWaitingJobRunning(boolean isWaitingJobRunning) {
		this.isWaitingJobRunning = isWaitingJobRunning;
	}

	/**
	 * Get the UUID of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2DUUID() {
		return d2DUUID;
	}

	/**
	 * Set the UUID of the UDP agent running on the node.
	 * 
	 * @param d2duuid
	 */
	public void setD2DUUID(String d2duuid) {
		d2DUUID = d2duuid;
	}
	
	public String getAuthUUID() {
		return authUUID;
	}

	public void setAuthUUID(String authUUID) {
		this.authUUID = authUUID;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @return
	 */
	@Deprecated
	public String getDisplayJobPhase() {
		return displayJobPhase;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @param displayJobPhase
	 */
	@Deprecated
	public void setDisplayJobPhase(String displayJobPhase) {
		this.displayJobPhase = displayJobPhase;
	}

	public String getNodeIdString() {
		return String.valueOf(id);
	}

	/**
	 * For internal use only.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isD2dOnDInstalled() {
		return d2dOnDInstalled;
	}
	
	/**
	 * For internal use only.
	 * 
	 * @param d2dOnDInstalled
	 */
	@Deprecated
	public void setD2dOnDInstalled(boolean d2dOnDInstalled) {
		this.d2dOnDInstalled = d2dOnDInstalled;
	}
	public void setPolicyIDForEsx(long policyIDForEsx)
	{
		this.policyIDForEsx = policyIDForEsx;
	}
	
	public long getPolicyIDForEsx()
	{
		return this.policyIDForEsx;
	}
	
	/**
	 * Set the description of the node.
	 * 
	 * @param nodeDescription
	 */
	public void setNodeDescription(String nodeDescription)
	{
		this.nodeDescription = nodeDescription;
	}
	
	/**
	 * Get the description of the node.
	 * 
	 * @return
	 */
	public String getNodeDescription()
	{
		return this.nodeDescription;
	}
	
	/**
	 * Get the synchronization frequency for the UDP agent running on the node.
	 * 
	 * @return
	 */
	public int getD2dSyncFrequency() {
		return d2dSyncFrequency;
	}

	/**
	 * Set the synchronization frequency for the UDP agent running on the node.
	 * 
	 * @param d2dSyncFrequency
	 */
	public void setD2dSyncFrequency(int d2dSyncFrequency) {
		this.d2dSyncFrequency = d2dSyncFrequency;
	}

	/**
	 * Get the synchronization frequency for the ARCserve Backup running on
	 * the node.
	 * 
	 * @return
	 */
	public int getAsbuSyncFrequency() {
		return asbuSyncFrequency;
	}

	/**
	 * Set the synchronization frequency for the ARCserve Backup running on
	 * the node.
	 * 
	 * @param asbuSyncFrequency
	 */
	public void setAsbuSyncFrequency(int asbuSyncFrequency) {
		this.asbuSyncFrequency = asbuSyncFrequency;
	}

	/**
	 * Get the last UDP agent synchronization time of the node.
	 * 
	 * @return
	 */
	public Date getD2dLastUpdateTime() {
		return d2dLastUpdateTime;
	}

	/**
	 * Set the last UDP agent synchronization time of the node.
	 * 
	 * @param d2dLastUpdateTime
	 */
	public void setD2dLastUpdateTime(Date d2dLastUpdateTime) {
		this.d2dLastUpdateTime = d2dLastUpdateTime;
	}

	/**
	 * Whether the last UDP agent synchronization of the node has warnings.
	 * 
	 * @return
	 */
	public boolean isD2dLastUpdateWarning() {
		return d2dLastUpdateWarning;
	}

	/**
	 * Set whether the last UDP agent synchronization of the node has warnings.
	 * 
	 * @param d2dLastUpdateWarning
	 */
	public void setD2dLastUpdateWarning(boolean d2dLastUpdateWarning) {
		this.d2dLastUpdateWarning = d2dLastUpdateWarning;
	}

	/**
	 * Get the last ARCserve Backup synchronization time of the node.
	 * 
	 * @return
	 */
	public Date getAsbuLastUpdateTime() {
		return asbuLastUpdateTime;
	}

	/**
	 * Set the last ARCserve Backup synchronization time of the node.
	 * 
	 * @param asbuLastUpdateTime
	 */
	public void setAsbuLastUpdateTime(Date asbuLastUpdateTime) {
		this.asbuLastUpdateTime = asbuLastUpdateTime;
	}

	/**
	 * Whether the last ARCserve Backup synchronization of the node has
	 * warnings.
	 * 
	 * @return
	 */
	public boolean isAsbuLastUpdateWarning() {
		return asbuLastUpdateWarning;
	}

	/**
	 * Set whether the last ARCserve Backup synchronization of the node has
	 * warnings.
	 * 
	 * @param asbuLastUpdateWarning
	 */
	public void setAsbuLastUpdateWarning(boolean asbuLastUpdateWarning) {
		this.asbuLastUpdateWarning = asbuLastUpdateWarning;
	}

	/**
	 * Set web service port of the UDP agent running on the node.
	 * 
	 * @param d2dPort
	 */
	public void setD2dPort(String d2dPort)
	{
		d2dPort = ensureNumberString( d2dPort );
		this.d2dWebServiceConnectInfo.setPort( Integer.parseInt( d2dPort ) );
	}
	
	/**
	 * Get web service port of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2dPort()
	{
		return Integer.toString( this.d2dWebServiceConnectInfo.getPort() );
	}
	
	/**
	 * Set web service port of the ARCserve Backup running on the node.
	 * 
	 * @param arcservePort
	 */
	public void setArcservePort(String arcservePort)
	{
		arcservePort = ensureNumberString( arcservePort );
		this.asbuWebServiceConnectInfo.setPort( Integer.parseInt( arcservePort ) );
	}
	
	/**
	 * Get web service port of the ARCserve Backup running on the node.
	 * 
	 * @return
	 */
	public String getArcservePort()
	{
		return Integer.toString( this.asbuWebServiceConnectInfo.getPort() );
	}
	
	/**
	 * Set web service protocol of the UDP agent running on the node.
	 * 
	 * @param d2dProtocol
	 */
	public void setD2dProtocol(int d2dProtocol)
	{
		this.d2dWebServiceConnectInfo.setProtocol( Protocol.parse( d2dProtocol ) );
	}
	
	/**
	 * Get web service protocol of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public int getD2dProtocol()
	{
		return this.d2dWebServiceConnectInfo.getProtocol().ordinal();
	}
	
	/**
	 * Get web service protocol of the ARCserve Backup running on the node.
	 * 
	 * @return
	 */
	public int getArcserveProtocol()
	{
		return this.asbuWebServiceConnectInfo.getProtocol().ordinal();
	}
	
	/**
	 * Set web service protocol of the ARCserve Backup running on the node.
	 * 
	 * @param arcserveProtocol
	 */
	public void setArcserveProtocol(int arcserveProtocol)
	{
		this.asbuWebServiceConnectInfo.setProtocol( Protocol.parse( arcserveProtocol ) );
	}
	
	/**
	 * Get name of the plan assigned to the node.
	 * 
	 * @return
	 */
	public String getPolicyName() {
		return policyName;
	}
	
	/**
	 * Set name of the plan assigned to the node.
	 * 
	 * @param policyName
	 */
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	/**
	 * Get content flag of the plan assigned to the node. The content flag
	 * indicate what configurations are set in the plan.
	 * 
	 * @return
	 */
	public int getPolicyContentFlag() {
		return policyContentFlag;
	}

	/**
	 * Set content flag of the plan assigned to the node. The content flag
	 * indicate what configurations are set in the plan.
	 * 
	 * @param policyContentFlag
	 */
	public void setPolicyContentFlag(int policyContentFlag) {
		this.policyContentFlag = policyContentFlag;
	}
	
	/**
	 * Get the deploying status of the plan assigned to the node.
	 * 
	 * @return
	 */
	public int getPolicyDeployStatus()
	{
		return policyDeployStatus;
	}

	/**
	 * Set the deploying status of the plan assigned to the node.
	 * 
	 * @param policyDeployStatus
	 */
	public void setPolicyDeployStatus( int policyDeployStatus )
	{
		this.policyDeployStatus = policyDeployStatus;
	}

	/**
	 * Get the reason of the deployment of the plan assigned to the node.
	 * 
	 * @return
	 */
	public int getPolicyDeployReason() 
	{
		return policyDeployReason;
	}
	
	/**
	 * Set the reason of the deployment of the plan assigned to the node.
	 * 
	 * @param policyDeployReason
	 */
	public void setPolicyDeployReason( int policyDeployReason ) 
	{
		this.policyDeployReason = policyDeployReason;
	}
	
	/**
	 * Get error message of plan deploying of the node.
	 * 
	 * @return
	 */
	public String getError()
	{
		return Error;
	}
	
	/**
	 * Set error message of plan deploying of the node.
	 * 
	 * @param Error
	 */
	public void setError(String Error)
	{
		this.Error = Error;
	}
	
	/**
	 * Get warning message of plan deploying of the node.
	 * 
	 * @return
	 */
	public String getWarning()
	{
		return Warning;
	}
	
	/**
	 * Set warning message of plan deploying of the node.
	 * 
	 * @param Warning
	 */
	public void setWarning(String Warning)
	{
		this.Warning = Warning;
	}	
	
	/**
	 * Get the time of last successfully deploying the plan assigned to the
	 * node.
	 * 
	 * @return
	 */
	public Date getLastSuccessfulPolicyDeploy()
	{
		return lastSuccessfulPolicyDeploy;
	}

	/**
	 * Set the time of last successfully deploying the plan assigned to the
	 * node.
	 * 
	 * @param lastSuccessfulPolicyDeploy
	 */
	public void setLastSuccessfulPolicyDeploy( Date lastSuccessfulPolicyDeploy )
	{
		this.lastSuccessfulPolicyDeploy = lastSuccessfulPolicyDeploy;
	}

	/**
	 * Get what type of ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public ABFuncServerType getArcserveType() {
		return arcserveType;
	}
	
	/**
	 * Set what type of ARCserve Backup that is running on the node. See {@link
	 * ABFuncServerType} for available values.
	 * 
	 * @param arcserveType
	 */
	public void setArcserveType(ABFuncServerType arcserveType) {
		this.arcserveType = arcserveType;
	}
	
	/**
	 * Get the version of the ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public String getArcserveBackupVersion() {
		return arcserveBackupVersion;
	}
	
	/**
	 * Set the version of the ARCserve Backup that is running on the node.
	 * 
	 * @param arcserveBackupVersion
	 */
	public void setArcserveBackupVersion(String arcserveBackupVersion) {
		this.arcserveBackupVersion = arcserveBackupVersion;
	}
	
	/**
	 * Get the major version of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2DMajorversion() {
		return Integer.toString( this.d2dVersion.getMajorVersion() );
	}
	
	/**
	 * Set the major version of the UDP agent running on the node.
	 * 
	 * @param d2dMajorversion
	 */
	public void setD2DMajorversion(String d2dMajorversion) {
		
		d2dMajorversion = ensureNumberString( d2dMajorversion );
		this.d2dVersion.setMajorVersion( Integer.parseInt( d2dMajorversion ) );
	}
	
	/**
	 * Get the minor version of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2dMinorversion() {
		return Integer.toString( this.d2dVersion.getMinorVersion() );
	}
	
	/**
	 * Set the minor version of the UDP agent running on the node.
	 * 
	 * @param d2dMinorversion
	 */
	public void setD2dMinorversion(String d2dMinorversion) {
		
		d2dMinorversion = ensureNumberString( d2dMinorversion );	
		this.d2dVersion.setMinorVersion( Integer.parseInt( d2dMinorversion ) );
	}
	
	/**
	 * Get the builder of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2dBuildnumber() {
		return this.d2dVersion.getBuildNumber();
	}
	
	/**
	 * Set the build number of the UDP agent running on the node.
	 * 
	 * @param d2dBuildnumber
	 */
	public void setD2dBuildnumber(String d2dBuildnumber) {
		d2dBuildnumber = ensureNumberString( d2dBuildnumber );
		this.d2dVersion.setBuildNumber(d2dBuildnumber);
	}
	
	/**
	 * Get the update version of the UDP agent running on the node.
	 * 
	 * @return
	 */
	public String getD2dUpdateversionnumber() {
		return Integer.toString( this.d2dVersion.getUpdateNumber() );
	}
	
	/**
	 * Set the update version of the UDP agent running on the node.
	 * 
	 * @param d2dUpdateversionnumber
	 */
	public void setD2dUpdateversionnumber(String d2dUpdateversionnumber) {
		d2dUpdateversionnumber = ensureNumberString( d2dUpdateversionnumber );
		this.d2dVersion.setUpdateInfo( Integer.parseInt( d2dUpdateversionnumber ), 0 );
	}
	
	/**
	 * Get whether the ARCserve Backup running on the node is managed.
	 * 
	 * @return
	 */
	public NodeManagedStatus getArcserveManaged() {
		return arcserveManaged;
	}
	
	/**
	 * Set whether the ARCserve Backup running on the node is managed.
	 * 
	 * @param arcserveManaged
	 */
	public void setArcserveManaged(NodeManagedStatus arcserveManaged) {
		this.arcserveManaged = arcserveManaged;
	}
	
	/**
	 * Get whether the UDP agent running on the node is managed.
	 * 
	 * @return
	 */
	public NodeManagedStatus getD2dManaged() {
		return d2dManaged;
	}
	
	/**
	 * Set whether the UDP agent running on the node is managed.
	 * 
	 * @param d2dManaged
	 */
	public void setD2dManaged(NodeManagedStatus d2dManaged) {
		this.d2dManaged = d2dManaged;
	}
	
	/**
	 * Get whether SQL Server is installed on the node.
	 * 
	 * @return
	 */
	public boolean isSqlServerInstalled() {
		return sqlServerInstalled;
	}
	
	/**
	 * Set whether SQL Server is installed on the node.
	 * 
	 * @param sqlServerInstalled
	 */
	public void setSqlServerInstalled(boolean sqlServerInstalled) {
		this.sqlServerInstalled = sqlServerInstalled;
	}
	
	/**
	 * Get whether Exchange is installed on the node.
	 * 
	 * @return
	 */
	public boolean isExchangeInstalled() {
		return exchangeInstalled;
	}
	
	/**
	 * Set whether Exchange is installed on the node.
	 * 
	 * @param exchangeInstalled
	 */
	public void setExchangeInstalled(boolean exchangeInstalled) {
		this.exchangeInstalled = exchangeInstalled;
	}
	
	/**
	 * Get whether UDP agent is installed on the node.
	 * 
	 * @return
	 */
	public boolean isD2dInstalled() {
		return d2dInstalled;
	}
	
	/**
	 * Set whether UDP agent is installed on the node.
	 * 
	 * @param d2dInstalled
	 */
	public void setD2dInstalled(boolean d2dInstalled) {
		this.d2dInstalled = d2dInstalled;
	}
	
	/**
	 * Get whether ARCserve Backup is installed on the node.
	 * 
	 * @return
	 */
	public boolean isArcserveInstalled() {
		return arcserveInstalled;
	}
	
	/**
	 * Set whether ARCserve Backup is installed on the node.
	 * 
	 * @param arcserveInstalled
	 */
	public void setArcserveInstalled(boolean arcserveInstalled) {
		this.arcserveInstalled = arcserveInstalled;
	}
	
	/**
	 * Get description of the operating system running on the node.
	 * 
	 * @return
	 */
	public String getOsDescription() {
		return osDescription;
	}
	
	/**
	 * Set description of the operating system running on the node.
	 * 
	 * @param osDescription
	 */
	public void setOsDescription(String osDescription) {
		this.osDescription = osDescription;
	}
	
	/**
	 * Get version of the operating system running on the node.
	 * 
	 * @return
	 */
	@Deprecated
	public String getOsVersion() {
		return osVersion;
	}
	
	/**
	 * Set version of the operating system running on the node.
	 * 
	 * @param osVersion
	 */
	@Deprecated
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	
	/**
	 * Get type of the operating system running on the node.
	 * 
	 * @return
	 */
	public String getOsType() {//<zhaji22>
		return osType;
	}

	/**
	 * Set type of the operating system running on the node.
	 * 
	 * @param osType
	 */
	public void setOsType(String osType) {
		this.osType = osType;
	}

	/**
	 * Get ID of the node.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set ID of the node.
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Get the flag indicate what ARCserve product were installed on the node.
	 * 
	 * @return
	 */
	public int getAppStatus() {
		return appStatus;
	}
	
	/**
	 * Set the flag indicate what ARCserve product were installed on the node.
	 * 
	 * @param appStatus
	 */
	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}
	
	/**
	 * Get the last time the database record of the node was updated.
	 *  
	 * @return
	 */
	public Date getLastupdated() {
		return lastupdated;
	}
	
	/**
	 * Set the last time the database record of the node was updated.
	 * 
	 * @param lastupdated
	 */
	public void setLastupdated(Date lastupdated) {
		this.lastupdated = lastupdated;
	}
	
	/**
	 * Get host name of the node.
	 * 
	 * @return
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Set host name of the node.
	 * 
	 * @param hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * Get IP address of the node.
	 * 
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}
	
	/**
	 * Set IP address of the node.
	 * 
	 * @param ipaddress
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	/**
	 * Get the principal name of the node.
	 * 
	 * @return
	 */
	public String getServerPrincipalName() {
		return ServerPrincipalName;
	}
	
	/**
	 * Set the principal name of the node.
	 * 
	 * @param serverPrincipalName
	 */
	public void setServerPrincipalName(String serverPrincipalName) {
		ServerPrincipalName = serverPrincipalName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		return (id == other.id && (policyName == other.policyName || policyName.equals(other.policyName)) && policyType == other.policyType);
//		if (id != other.id)
//			return false;
//		return true;
	}
	
	/**
	 * Get synchronization status of the node.
	 * 
	 * @param syncStatus
	 */
	public void setSyncStatus(NodeSyncStatus syncStatus) {
		this.syncStatus = syncStatus;
	}
	
	/**
	 * Set synchronization status of the node.
	 * 
	 * @return
	 */
	public NodeSyncStatus getSyncStatus() {
		return syncStatus;
	}
	
	/**
	 * Get backup status of the node.
	 * 
	 * @param bkpStatus
	 */
	public void setBkpStatus(NodeBkpStatus bkpStatus) {
		this.bkpStatus = bkpStatus;
	}
	
	/**
	 * Set backup status of the node.
	 * 
	 * @return
	 */
	public NodeBkpStatus getBkpStatus() {
		return bkpStatus;
	}
	
	/**
	 * Get the Global Dashboard group id of the node. The node is a Global
	 * Dashboard branch server.
	 * 
	 * @return
	 */
	public int getGDBId() {
		return GDBId;
	}
	
	/**
	 * Set the Global Dashboard group id of the node. The node is a Global
	 * Dashboard branch server.
	 * 
	 * @param gDBId
	 */
	public void setGDBId(int gDBId) {
		GDBId = gDBId;
	}

	/**
	 * Get user name of the node.
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Set user name of the node.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get password of the node.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Set password of the node.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get information of virtual conversion monitor of the node.
	 * 
	 * @return
	 */
	public VCMMonitor getVcmMonitor() {
		return vcmMonitor;
	}

	/**
	 * Set information of virtual conversion monitor of the node.
	 * 
	 * @param vcmMonitor
	 */
	public void setVcmMonitor(VCMMonitor vcmMonitor) {
		this.vcmMonitor = vcmMonitor;
	}

	/**
	 * Get virtual conversion configuration of the node.
	 * 
	 * @return	Marsharled form of {@link JobScriptCombo} object.
	 */
	public String getVcmSettings() {
		return vcmSettings;
	}

	/**
	 * Set virtual conversion configuration of the node.
	 * 
	 * @param	vcmSettings
	 * 			Marsharled form of {@link JobScriptCombo} object.
	 */
	public void setVcmSettings(String vcmSettings) {
		this.vcmSettings = vcmSettings;
	}

	/**
	 * Whether the node is a physical machine.
	 * 
	 * @return
	 */
	public boolean isPhysicalMachine() {
		return isPhysicalMachine;
	}

	/**
	 * Set whether the node is a physical machine.
	 * 
	 * @param isPhysicalMachine
	 */
	public void setPhysicalMachine(boolean isPhysicalMachine) {
		this.isPhysicalMachine = isPhysicalMachine;
	}

	/**
	 * Whether the node is a VMware virtual machine.
	 * 
	 * @return
	 */
	public boolean isVMwareMachine() {
		return isVMwareMachine;
	}

	/**
	 * Set whether the node is a VMware virtual machine.
	 * 
	 * @param isVMwareMachine
	 */
	public void setVMwareMachine(boolean isVMwareMachine) {
		this.isVMwareMachine = isVMwareMachine;
	}

	/**
	 * Whether the node is a Hyper-V virtual machine.
	 * 
	 * @return
	 */
	public boolean isHyperVMachine() {
		return isHyperVMachine;
	}

	/**
	 * Set whether the node is a Hyper-V virtual machine.
	 * 
	 * @param isHyperVMachine
	 */
	public void setHyperVMachine(boolean isHyperVMachine) {
		this.isHyperVMachine = isHyperVMachine;
	}

//	public boolean isVCMMonitor() {
//		return isVCMMonitor;
//	}
//
//	public void setVCMMonitor(boolean isVCMMonitor) {
//		this.isVCMMonitor = isVCMMonitor;
//	}

	/**
	 * Whether the node has a virtual conversion monitor.
	 * 
	 * @return
	 */
	public boolean isHasVCMMonitorFlag() {
		return hasVCMMonitorFlag;
	}

	/**
	 * Set whether the node has a virtual conversion monitor.
	 * 
	 * @param hasVCMMonitorFlag
	 */
	public void setHasVCMMonitorFlag(boolean hasVCMMonitorFlag) {
		this.hasVCMMonitorFlag = hasVCMMonitorFlag;
	}

	/**
	 * Whether the node is a virtual conversion monitee.
	 * 
	 * @return
	 */
	public boolean isVCMMonitee() {
		return isVCMMonitee;
	}

	/**
	 * Set whether the node is a virtual conversion monitee.
	 * 
	 * @param isVCMMonitee
	 */
	public void setVCMMonitee(boolean isVCMMonitee) {
		this.isVCMMonitee = isVCMMonitee;
	}

	/**
	 * Whether the node is imported from HBBU. HBBU is a component in UDP v16.
	 * 
	 * @return
	 */
	public boolean isVMImportFromVSphere() {
		return isVMImportFromVSphere;
	}

	/**
	 * Set whether the node is imported from HBBU. HBBU is a component in UDP v16.
	 * 
	 * @param isVMImportFromVSphere
	 */
	public void setVMImportFromVSphere(boolean isVMImportFromVSphere) {
		this.isVMImportFromVSphere = isVMImportFromVSphere;
	}
	
	/**
	 * Get VM name of the node if it's a virtual machine.
	 * 
	 * @return
	 */
	public String getVmName() {
		return vmName;
	}

	/**
	 * Set VM name of the node if it's a virtual machine.
	 * 
	 * @param vmName
	 */
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	/**
	 * Get VM instance UUID of the node if it's a virtual machine.
	 * 
	 * @return
	 */
	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	/**
	 * Set VM instance UUID of the node if it's a virtual machine.
	 * 
	 * @param vmInstanceUUID
	 */
	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	/**
	 * Set the domain of the node.
	 * 
	 * @param domainName
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * Get the domain of the node.
	 * 
	 * @return
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Set hypervisor of the node if it's a virtual machine.
	 * 
	 * @param hyperVisor
	 */
	public void setHyperVisor(String hyperVisor) {
		this.hyperVisor = hyperVisor;
	}

	/**
	 * Get hypervisor of the node if it's a virtual machine.
	 * 
	 * @return
	 */
	public String getHyperVisor() {
		return hyperVisor;
	}
	
	public String getEsxName() {
		return esxName;
	}

	public void setEsxName(String esxName) {
		this.esxName = esxName;
	}

	/**
	 * Whether the node is visible in UI.
	 * 
	 * @return
	 */
	public int getIsVisible() {
		return isVisible;
	}

	/**
	 * Set whether the node is visible in UI.
	 * 
	 * @param isVisible
	 */
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}
	
	/**
	 * Get protection type bitmap of the node. See {@link ProtectionType} for
	 * the definitions.
	 * 
	 * @return
	 */
	public int getProtectionTypeBitmap() {
		return protectionTypeBitmap;
	}

	/**
	 * Set protection type bitmap of the node. See {@link ProtectionType} for
	 * the definitions.
	 * 
	 * @param protectionTypeBitmap
	 */
	public void setProtectionTypeBitmap(int protectionTypeBitmap) {
		this.protectionTypeBitmap = protectionTypeBitmap;
	}

	/**
	 * Whether the RPS node is managed by UDP.
	 * 
	 * @return
	 */
	public NodeManagedStatus getRpsManagedStatus() {
		return rpsManagedStatus;
	}

	/**
	 * Set whether the RPS node is managed by UDP.
	 * 
	 * @param rpsManagedStatus
	 */
	public void setRpsManagedStatus(NodeManagedStatus rpsManagedStatus) {
		this.rpsManagedStatus = rpsManagedStatus;
	}

	/**
	 * Get the PFC check status. See {@link VMVerifyStatus.CheckStatus} for
	 * available values.
	 * 
	 * @return
	 */
	public int getVerifyStatus() {
		return verifyStatus;
	}

	/**
	 * Set the PFC check status. See {@link VMVerifyStatus.CheckStatus} for
	 * available values.
	 * 
	 * @param verifyStatus
	 */
	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	/**
	 * Get the list of groups the node belongs to.
	 * 
	 * @return
	 */
	@Deprecated
	public List<NodeGroup> getGroupList() {
		return groupList;
	}

	/**
	 * Set the list of groups the node belongs to.
	 * 
	 * @param groupList
	 */
	@Deprecated
	public void setGroupList(List<NodeGroup> groupList) {
		this.groupList = groupList;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @return
	 */
	@Deprecated
	public long getJobPhase() {
		return jobPhase;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @param jobPhase
	 */
	@Deprecated
	public void setJobPhase(long jobPhase) {
		this.jobPhase = jobPhase;
	}

	/**
	 * Get status of this VM node. See {@link VMStatus} for available values.
	 * 
	 * @return
	 */
	public int getVmStatus() {
		return vmStatus;
	}

	/**
	 * Set status of this VM node. See {@link VMStatus} for available values.
	 * 
	 * @param vmStatus
	 */
	public void setVmStatus(int vmStatus) {
		this.vmStatus = vmStatus;
	}

	/**
	 * Whether the node is imported form RHA.
	 * 
	 * @return
	 */
	public boolean isImportedFromRHA() {
		return importedFromRHA;
	}

	/**
	 * Set whether the node is imported form RHA.
	 * 
	 * @param importedFromRHA
	 */
	public void setImportedFromRHA(boolean importedFromRHA) {
		this.importedFromRHA = importedFromRHA;
	}

	/**
	 * Get node ID of the converter of the node.
	 * 
	 * @return
	 */
	public int getConverterId() {
		return converterId;
	}

	/**
	 * Set node ID of the converter of the node.
	 * 
	 * @param converterId
	 */
	public void setConverterId(int converterId) {
		this.converterId = converterId;
	}

	/**
	 * Get host name of the converter of the node.
	 * 
	 * @return
	 */
	public String getConverter() {
		return converter;
	}

	/**
	 * Set host name of the converter of the node.
	 * 
	 * @param converter
	 */
	public void setConverter(String converter) {
		this.converter = converter;
	}

	/**
	 * Get the folder path for storing recovery point of the node.
	 * 
	 * @return
	 */
	public String getRecoveryPointFolder() {
		return recoveryPointFolder;
	}

	/**
	 * Set the folder path for storing recovery point of the node.
	 * 
	 * @param recoveryPointFolder
	 */
	public void setRecoveryPointFolder(String recoveryPointFolder) {
		this.recoveryPointFolder = recoveryPointFolder;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @return
	 */
	@Deprecated
	public int getMergeJobPhase() {
		return mergeJobPhase;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @param mergeJobPhase
	 */
	@Deprecated
	public void setMergeJobPhase(int mergeJobPhase) {
		this.mergeJobPhase = mergeJobPhase;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isPauseMergeJobEnabled() {
		return pauseMergeJobEnabled;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @param pauseMergeJobEnabled
	 */
	@Deprecated
	public void setPauseMergeJobEnabled(boolean pauseMergeJobEnabled) {
		this.pauseMergeJobEnabled = pauseMergeJobEnabled;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isResumeMergeJobEnabled() {
		return resumeMergeJobEnabled;
	}

	/**
	 * Will no longer be used.
	 * 
	 * @param resumeMergeJobEnabled
	 */
	@Deprecated
	public void setResumeMergeJobEnabled(boolean resumeMergeJobEnabled) {
		this.resumeMergeJobEnabled = resumeMergeJobEnabled;
	}

	/**
	 * Whether the node is a HBBU node imported from RHA for remote conversion.
	 * 
	 * @return
	 */
	public boolean isImportedFromRHAWithHBBU() {
		return importedFromRHAWithHBBU;
	}

	/**
	 * Set whether the node is a HBBU node imported from RHA for remote
	 * conversion.
	 * 
	 * @param importedFromRHAWithHBBU
	 */
	public void setImportedFromRHAWithHBBU(boolean importedFromRHAWithHBBU) {
		this.importedFromRHAWithHBBU = importedFromRHAWithHBBU;
	}

	/**
	 * Whether the node was imported from RPS.
	 * 
	 * @return
	 */
	public boolean isImportedFromRPS() {
		return importedFromRPS;
	}

	/**
	 * Set whether the node was imported from RPS.
	 * 
	 * @param importedFromRPS
	 */
	public void setImportedFromRPS(boolean importedFromRPS) {
		this.importedFromRPS = importedFromRPS;
	}

	/**
	 * Whether the node was imported from RPS replication.
	 * 
	 * @return
	 */
	public boolean isImportedFromRPSReplication() {
		return importedFromRPSReplication;
	}

	/**
	 * Set whether the node was imported from RPS replication.
	 * 
	 * @param importedFromRPSReplication
	 */
	public void setImportedFromRPSReplication(boolean importedFromRPSReplication) {
		this.importedFromRPSReplication = importedFromRPSReplication;
	}

	/**
	 * Get the HBBU proxy of the node.
	 * 
	 * @return
	 */
	@Deprecated
	public Node getProxyNode() {
		return proxyNode;
	}

	/**
	 * Set the HBBU proxy of the node.
	 * 
	 * @param proxyNode
	 */
	@Deprecated
	public void setProxyNode(Node proxyNode) {
		this.proxyNode = proxyNode;
	}

	/**
	 * Whether the operation system running on the node is Windows.
	 * 
	 * @return
	 */
	public boolean isVmWindowsOS() {
		return vmWindowsOS;
	}

	/**
	 * Set whether the operation system running on the node is Windows.
	 * 
	 * @param vmWindowsOS
	 */
	public void setVmWindowsOS(boolean vmWindowsOS) {
		this.vmWindowsOS = vmWindowsOS;
	}

	/**
	 * Get type of policy that the node is using. See {@link PolicyTypes} for
	 * available values.
	 * 
	 * @return
	 */
	public int getPolicyType() {
		return policyType;
	}

	/**
	 * Set type of policy that the node is using. See {@link PolicyTypes} for
	 * available values.
	 * 
	 * @param policyType
	 */
	public void setPolicyType(int policyType) {
		this.policyType = policyType;
	}

	/**
	 * Get job history list of the node.
	 * 
	 * @return
	 */
	public List<JobHistory> getLstJobHistory() {
		return lstJobHistory;
	}

	/**
	 * Set job history list of the node.
	 * 
	 * @param lstJobHistory
	 */
	public void setLstJobHistory(List<JobHistory> lstJobHistory) {
		this.lstJobHistory = lstJobHistory;
	}

	/**
	 * Not used.
	 * 
	 * @return
	 */
	@Deprecated
	public int getNodeType() {
		return nodeType;
	}

	/**
	 * Not used.
	 * 
	 * @param nodeType
	 */
	@Deprecated
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * Get virtual standby status of the node.
	 * 
	 * @return
	 */
	public D2DStatusInfo getVsbSatusInfo() {
		return vsbSatusInfo;
	}

	/**
	 * Set virtual standby status of the node.
	 * 
	 * @param vsbSatusInfo
	 */
	public void setVsbSatusInfo(D2DStatusInfo vsbSatusInfo) {
		this.vsbSatusInfo = vsbSatusInfo;
	}
	
	public boolean isCrossSiteVsb() {
		return isCrossSiteVsb;
	}

	public void setCrossSiteVsb(boolean isCrossSiteVsb) {
		this.isCrossSiteVsb = isCrossSiteVsb;
	}

	/**
	 * Whether this node is a Linux node.
	 * 
	 * @return
	 */
	public boolean isLinuxNode() {
		return isLinuxNode;
	}

	/**
	 * Set whether this node is a Linux node.
	 * 
	 * @param isLinuxNode
	 */
	public void setLinuxNode(boolean isLinuxNode) {
		this.isLinuxNode = isLinuxNode;
	}

	/**
	 * Will be no long used.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isHyperVVmAsPhysicalMachine() {
		return hyperVVmAsPhysicalMachine;
	}

	/**
	 * Will be no long used.
	 * 
	 * @param hyperVVmAsPhysicalMachine
	 */
	@Deprecated
	public void setHyperVVmAsPhysicalMachine(boolean hyperVVmAsPhysicalMachine) {
		this.hyperVVmAsPhysicalMachine = hyperVVmAsPhysicalMachine;
	}

	/**
	 * Whether Linux D2D was installed on the node.
	 * 
	 * @return
	 */
	public boolean isLinuxD2DInstalled() {
		return linuxD2DInstalled;
	}

	/**
	 * Set whether Linux D2D was installed on the node.
	 * 
	 * @param linuxD2DInstalled
	 */
	public void setLinuxD2DInstalled(boolean linuxD2DInstalled) {
		this.linuxD2DInstalled = linuxD2DInstalled;
	}

	/**
	 * Get start time of the last backup running on the node.
	 * 
	 * @return
	 */
	public Date getD2dLastBackupStartTime()
	{
		return d2dLastBackupStartTime;
	}

	/**
	 * Set start time of the last backup running on the node.
	 * 
	 * @param d2dLastBackupStartTime
	 */
	public void setD2dLastBackupStartTime( Date d2dLastBackupStartTime )
	{
		this.d2dLastBackupStartTime = d2dLastBackupStartTime;
	}

	/**
	 * Get status of the last backup job running on the node.
	 * 
	 * @return
	 */
	public JobStatus getD2dLastBackupJobStatus()
	{
		return d2dLastBackupJobStatus;
	}

	/**
	 * Set status of the last backup job running on the node.
	 * 
	 * @param d2dLastBackupJobStatus
	 */
	public void setD2dLastBackupJobStatus( JobStatus d2dLastBackupJobStatus )
	{
		this.d2dLastBackupJobStatus = d2dLastBackupJobStatus;
	}

	/**
	 * Get time zone settings of the node.
	 * 
	 * @return
	 */
	public int getTimezone() {
		return timezone;
	}

	/**
	 * Set time zone settings of the node.
	 * 
	 * @param timezone
	 */
	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}
	
	/**
	 * Get reboot type of the node while deploy UDP agent to the node. See
	 * {@link RebootType) for details.
	 * 
	 * @return
	 */
	public int getInstallationType() {
		return installationType;
	}

	/**
	 * Set reboot type of the node while deploy UDP agent to the node. See
	 * {@link RebootType) for details.
	 * 
	 * @param installationType
	 */
	public void setInstallationType(int installationType) {
		this.installationType = installationType;
	}

	/**
	 * Get the status of deploying UDP agent to the node. See
	 * {@link DeployStatus} for details.
	 * 
	 * @return
	 */
	public int getRemoteDeployStatus() {
		return remoteDeployStatus;
	}

	/**
	 * Set the status of deploying UDP agent to the node. See
	 * {@link DeployStatus} for details.
	 * 
	 * @param remoteDeployStatus
	 */
	public void setRemoteDeployStatus(int remoteDeployStatus) {
		this.remoteDeployStatus = remoteDeployStatus;
	}

	public int getDeployTaskStatus() {
		return deployTaskStatus;
	}

	public void setDeployTaskStatus(int deployTaskStatus) {
		this.deployTaskStatus = deployTaskStatus;
	}

	/**
	 * Get the time of deploying UDP agent to the node.
	 * 
	 * @return
	 */
	public Date getRemoteDeployTime() {
		return remoteDeployTime;
	}

	/**
	 * Get the time of deploying UDP agent to the node.
	 * 
	 * @param remoteDeployTime
	 */
	public void setRemoteDeployTime(Date remoteDeployTime) {
		this.remoteDeployTime = remoteDeployTime;
	}

	/**
	 * Get plan enabling status of the node. See {@link PlanEnableStatus} for
	 * available values.
	 * 
	 * @return
	 */
	public int getEnableStatus() {
		return enableStatus;
	}

	/**
	 * Set plan enabling status of the node. See {@link PlanEnableStatus} for
	 * available values.
	 * 
	 * @param enableStatus
	 */
	public void setEnableStatus(int enableStatus) {
		this.enableStatus = enableStatus;
	}

	/**
	 * Get the machine type of the node. This is used for license controlling.
	 * 
	 * @return
	 */
	public LicenseMachineType getMachineType() {
		return machineType;
	}

	/**
	 * Set the machine type of the node. This is used for license controlling.
	 * 
	 * @param machineType
	 */
	public void setMachineType(LicenseMachineType machineType) {
		this.machineType = machineType;
	}
	
	/**
	 * Whether the specify hypervisor feature is enabled. This is only for
	 * internal use.
	 * 
	 * @return
	 */
	public boolean isSpecifyHypervisorEnabled() {
		return isLinuxNode || (isPhysicalMachine && machineType != LicenseMachineType.PHYSICAL_MACHINE && machineType != LicenseMachineType.Unsupported);
	}

	/**
	 * Not used.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isPasswordVerified() {
		return passwordVerified;
	}

	/**
	 * Not used.
	 * 
	 * @param passwordVerified
	 */
	@Deprecated
	public void setPasswordVerified(boolean passwordVerified) {
		this.passwordVerified = passwordVerified;
	}

	/**
	 * Get host type flags of the the node. See {@link HostType} for the
	 * definition.
	 * 
	 * @return
	 */
	public int getRhostType() {
		return rhostType;
	}

	/**
	 * Set host type flags of the the node. See {@link HostType} for the
	 * definition.
	 * 
	 * @param rhostType
	 */
	public void setRhostType(int rhostType) {
		this.rhostType = rhostType;
	}

	public DiscoveryESXOption getDiscoveryESXOption() {
		return discoveryESXOption;
	}

	public void setDiscoveryESXOption(DiscoveryESXOption discoveryESXOption) {
		this.discoveryESXOption = discoveryESXOption;
	}

	public NodeVcloudSummary getVcloudProperties() {
		return vcloudProperties;
	}

	public void setVcloudProperties(NodeVcloudSummary vcloudProperties) {
		this.vcloudProperties = vcloudProperties;
	}
	
	private String ensureNumberString( String string )
	{
		return ((string == null) || string.trim().isEmpty()) ? "0" : string;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}	
	public boolean isLocalSite() {
		return isLocalSite;
	}

	public void setLocalSite(boolean isLocalSite) {
		this.isLocalSite = isLocalSite;
	}

	/**
	 * Get whether Console is installed on the node.
	 * 
	 * @return
	 */
	public boolean isConsoleInstalled() {
		return consoleInstalled;
	}
	
	/**
	 * Set whether UDP Console is installed on the node.
	 * 
	 * @param consoleInstalled
	 */
	public void setConsoleInstalled(boolean consoleInstalled) {
		this.consoleInstalled = consoleInstalled;
	}

	public int getWarnningAcknowledged() {
		return warnningAcknowledged;
	}

	public void setWarnningAcknowledged(int warnningAcknowledged) {
		this.warnningAcknowledged = warnningAcknowledged;
	}

	public ProxyInfoSummary getProxyInfos() {
		return proxyInfos;
	}

	public void setProxyInfos(ProxyInfoSummary proxyInfos) {
		this.proxyInfos = proxyInfos;
	}

	public LinuxD2DInfoSummary getLinuxD2DInfoSummary() {
		return linuxD2DInfoSummary;
	}

	public void setLinuxD2DInfoSummary(LinuxD2DInfoSummary linuxD2DInfoSummary) {
		this.linuxD2DInfoSummary = linuxD2DInfoSummary;
	}

	// banar05
	public String getCurrentConsoleMachineNameForCollectDiag() {
		return currentConsoleMachineNameForCollectDiag;
	}

	public void setCurrentConsoleMachineNameForCollectDiag(
			String currentConsoleMachineNameForCollectDiag) {
		this.currentConsoleMachineNameForCollectDiag = currentConsoleMachineNameForCollectDiag;
	}

	public String getCurrentConsoleIPForCollectDiag() {
		return currentConsoleIPForCollectDiag;
	}

	public void setCurrentConsoleIPForCollectDiag(
			String currentConsoleIPForCollectDiag) {
		this.currentConsoleIPForCollectDiag = currentConsoleIPForCollectDiag;
	}
	
}
