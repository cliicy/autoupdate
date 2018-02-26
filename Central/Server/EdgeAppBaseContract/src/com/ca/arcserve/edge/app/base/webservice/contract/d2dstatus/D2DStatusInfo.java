package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.ca.arcflash.webservice.edge.data.d2dstatus.VMPowerStatus;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

@XmlType(name = "D2DStatusInfo", namespace = "http://webservice.edge.arcserve.ca.com/d2dstatus/")
public class D2DStatusInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//////////////////////////////////////////////////////////////////////////
	private int hostId;
	private int hasVSBStatusInfo = 0;
	
	private boolean isDriverInstalled;
	private boolean isRestarted;
	private D2DEstimatedValue estimatedValue;
	private boolean isBackupConfiged;
	
	// backup info
	private Date lastBackupStartTime;
	private D2DBackupType lastBackupType;
	private D2DJobStatus lastBackupJobStatus = D2DJobStatus.Unknown;
	private D2DStatus lastBackupStatus;
	
	// recovery point info
	private int recoveryPointRetentionCount;
	private int recoveryPointCount;
	private String recoveryPointMounted;
	private D2DStatus recoveryPointStatus;
	private boolean isUseBackupSets;
	
	// destination info
	private String destinationPath;
	private boolean isDestinationAccessible;
	private long destinationFreeSpace;
	private int destinationEstimatedBackupCount;
	private VCMStorage[] destinationVCMStorages;
	private D2DStatus destinationStatus;
	
	// VM info
	private String vmName;
	private VMPowerStatus vmPowerStatus; 
	private String currentRunningSnapshot;
	private String standbyVMRecentSnapshot;
	private long snapshotTimeZoneOffset;

	// virtual standby info
	private int autoOfflieCopyStatus;
	private int heartbeatStatus;
		
	// overall info
	private D2DStatus overallStatus;
	
	private long lastUpdateTimeDiffSeconds;

	public static D2DStatusInfo NullObject = new D2DStatusInfo();

	//////////////////////////////////////////////////////////////////////////
	
	public D2DStatusInfo()
	{
		lastBackupStartTime				= null;
		lastBackupType					= D2DBackupType.Unknown;
		lastBackupJobStatus				= D2DJobStatus.Unknown;
		lastBackupStatus				= D2DStatus.Unknown;
		
		recoveryPointRetentionCount		= -1;
		recoveryPointCount				= -1;
		recoveryPointMounted			= null;
		recoveryPointStatus				= D2DStatus.Unknown;
		isUseBackupSets					= false;
		
		destinationPath					= null;
		isDestinationAccessible			= false;
		destinationFreeSpace			= -1;
		destinationEstimatedBackupCount	= -1;
		destinationVCMStorages			= new VCMStorage[0];
		destinationStatus				= D2DStatus.Unknown;
		
		isDriverInstalled               = true;
		isRestarted                     = true;
		estimatedValue                  = D2DEstimatedValue.Unknown;
		isBackupConfiged                = true;
		
		overallStatus					= D2DStatus.Unknown;
	}
	
	public int getHostId() {
		return hostId;
	}
	
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	
	public int getHasVSBStatusInfo() {
		return hasVSBStatusInfo;
	}

	public void setHasVSBStatusInfo(int hasVSBStatusInfo) {
		this.hasVSBStatusInfo = hasVSBStatusInfo;
	}

	/**
	 * Whether the driver is installed on the node.
	 * 
	 * @return
	 */
	public boolean isDriverInstalled() {
		return isDriverInstalled;
	}

	/**
	 * Set whether the driver is installed on the node.
	 * 
	 * @param isDriverInstalled
	 */
	public void setDriverInstalled(boolean isDriverInstalled) {
		this.isDriverInstalled = isDriverInstalled;
	}

	/**
	 * Whether the node restarted after installing driver.
	 * 
	 * @return
	 */
	public boolean isRestarted() {
		return isRestarted;
	}

	/**
	 * Set whether the node restarted after installing driver.
	 * 
	 * @param isRestarted
	 */
	public void setRestarted(boolean isRestarted) {
		this.isRestarted = isRestarted;
	}

	public D2DEstimatedValue getEstimatedValue() {
		return estimatedValue;
	}

	public void setEstimatedValue(D2DEstimatedValue estimatedValue) {
		this.estimatedValue = estimatedValue;
	}

	/**
	 * Whether the backup was configured for the node.
	 * 
	 * @return
	 */
	public boolean isBackupConfiged() {
		return isBackupConfiged;
	}

	/**
	 * Set whether the backup was configured for the node.
	 * 
	 * @param isBackupConfiged
	 */
	public void setBackupConfiged(boolean isBackupConfiged) {
		this.isBackupConfiged = isBackupConfiged;
	}
	
	/**
	 * Get the start time of last backup.
	 * 
	 * @return
	 */
	public Date getLastBackupStartTime()
	{
		return lastBackupStartTime;
	}

	/**
	 * Set the start time of last backup.
	 * 
	 * @param lastBackupStartTime
	 */
	public void setLastBackupStartTime( Date lastBackupStartTime )
	{
		this.lastBackupStartTime = lastBackupStartTime;
	}

	/**
	 * Get the backup type of last backup.
	 * 
	 * @return
	 */
	public D2DBackupType getLastBackupType()
	{
		return lastBackupType;
	}

	/**
	 * Set the backup type of last backup.
	 * 
	 * @param lastBackupType
	 */
	public void setLastBackupType( D2DBackupType lastBackupType )
	{
		this.lastBackupType = lastBackupType;
	}

	/**
	 * Get the job status of last backup job.
	 * 
	 * @return
	 */
	public D2DJobStatus getLastBackupJobStatus()
	{
		return lastBackupJobStatus;
	}

	/**
	 * Set the job status of last backup job.
	 * 
	 * @param lastBackupJobStatus
	 */
	public void setLastBackupJobStatus( D2DJobStatus lastBackupJobStatus )
	{
		this.lastBackupJobStatus = lastBackupJobStatus;
	}

	/**
	 * Get the status of last backup.
	 * 
	 * @return
	 */
	public D2DStatus getLastBackupStatus()
	{
		return lastBackupStatus;
	}

	/**
	 * Set the status of last backup.
	 * 
	 * @param lastBackupStatus
	 */
	public void setLastBackupStatus( D2DStatus lastBackupStatus )
	{
		this.lastBackupStatus = lastBackupStatus;
	}

	public boolean isUseBackupSets()
	{
		return isUseBackupSets;
	}
	
	public void setIsUseBackupSets(boolean isUseBackupsets)
	{
		this.isUseBackupSets = isUseBackupsets;
	}
	
	/**
	 * Get recovery point retention count.
	 * 
	 * @return
	 */
	public int getRecoveryPointRetentionCount()
	{
		return recoveryPointRetentionCount;
	}

	/**
	 * Set recovery point retention count.
	 * 
	 * @param recoveryPointRetentionCount
	 */
	public void setRecoveryPointRetentionCount( int recoveryPointRetentionCount )
	{
		this.recoveryPointRetentionCount = recoveryPointRetentionCount;
	}

	/**
	 * Get count of recovery point.
	 * 
	 * @return
	 */
	public int getRecoveryPointCount()
	{
		return recoveryPointCount;
	}

	/**
	 * Set count of recovery point.
	 * 
	 * @param recoveryPointCount
	 */
	public void setRecoveryPointCount( int recoveryPointCount )
	{
		this.recoveryPointCount = recoveryPointCount;
	}

	/**
	 * Get the mounted recovery point.
	 * 
	 * @return
	 */
	public String getRecoveryPointMounted()
	{
		return recoveryPointMounted;
	}

	/**
	 * Set the mounted recovery point.
	 * 
	 * @param recoveryPointMounted
	 */
	public void setRecoveryPointMounted( String recoveryPointMounted )
	{
		this.recoveryPointMounted = recoveryPointMounted;
	}

	/**
	 * Get recovery point status.
	 * 
	 * @return
	 */
	public D2DStatus getRecoveryPointStatus()
	{
		return recoveryPointStatus;
	}

	/**
	 * Set recovery point status.
	 * 
	 * @param recoveryPointStatus
	 */
	public void setRecoveryPointStatus( D2DStatus recoveryPointStatus )
	{
		this.recoveryPointStatus = recoveryPointStatus;
	}

	/**
	 * Get destination path.
	 * 
	 * @return
	 */
	public String getDestinationPath()
	{
		return destinationPath;
	}

	/**
	 * Set destination path.
	 * 
	 * @param destinationPath
	 */
	public void setDestinationPath( String destinationPath )
	{
		this.destinationPath = destinationPath;
	}

	/**
	 * Get whether the destination can be accessed.
	 * 
	 * @return
	 */
	public boolean isDestinationAccessible()
	{
		return isDestinationAccessible;
	}

	/**
	 * Set whether the destination can be accessed.
	 * 
	 * @param isDestinationAccessible
	 */
	public void setDestinationAccessible( boolean isDestinationAccessible )
	{
		this.isDestinationAccessible = isDestinationAccessible;
	}

	/**
	 * Get the free space size of the destination.
	 * 
	 * @return
	 */
	public long getDestinationFreeSpace()
	{
		return destinationFreeSpace;
	}

	/**
	 * Set the free space size of the destination.
	 * 
	 * @param destinationFreeSpace
	 */
	public void setDestinationFreeSpace( long destinationFreeSpace )
	{
		this.destinationFreeSpace = destinationFreeSpace;
	}

	/**
	 * Get estimated backup count according to current destination status.
	 * 
	 * @return
	 */
	public int getDestinationEstimatedBackupCount()
	{
		return destinationEstimatedBackupCount;
	}

	/**
	 * Set estimated backup count according to current destination status.
	 * 
	 * @param destinationEstimatedBackupCount
	 */
	public void setDestinationEstimatedBackupCount(
		int destinationEstimatedBackupCount )
	{
		this.destinationEstimatedBackupCount = destinationEstimatedBackupCount;
	}

	/**
	 * Get destination status.
	 * 
	 * @return
	 */
	public D2DStatus getDestinationStatus()
	{
		return destinationStatus;
	}

	/**
	 * Set destination status.
	 * 
	 * @return
	 */
	public void setDestinationStatus( D2DStatus destinationStatus )
	{
		this.destinationStatus = destinationStatus;
	}

	/**
	 * Get all virtual conversion storages of the destination.
	 * 
	 * @return
	 */
	public VCMStorage[] getDestinationVCMStorages()
	{
		return destinationVCMStorages;
	}

	/**
	 * Set all virtual conversion storages of the destination.
	 * 
	 * @param destinationVCMStorages
	 */
	public void setDestinationVCMStorages( VCMStorage[] destinationVCMStorages )
	{
		this.destinationVCMStorages = destinationVCMStorages;
	}

	/**
	 * Get overall status of the node.
	 * 
	 * @return
	 */
	public D2DStatus getOverallStatus()
	{
		return overallStatus;
	}

	/**
	 * Set overall status of the node.
	 * 
	 * @param overallStatus
	 */
	public void setOverallStatus( D2DStatus overallStatus )
	{
		this.overallStatus = overallStatus;
	}
	
	/**
	 * Get the name of the virtual machine.
	 * 
	 * @return
	 */
	public String getVmName() {
		return vmName;
	}

	/**
	 * Set the name of the virtual machine.
	 * 
	 * @param vmName
	 */
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	/**
	 * Get the power status of the virtual machine.
	 * 
	 * @return
	 */
	public VMPowerStatus getVmPowerStatus() {
		return vmPowerStatus;
	}

	/**
	 * Set the power status of the virtual machine.
	 * 
	 * @param vmPowerStatus
	 */
	public void setVmPowerStatus(VMPowerStatus vmPowerStatus) {
		this.vmPowerStatus = vmPowerStatus;
	}

	/**
	 * Get automatic offline copy status. See {@link com.ca.arcflash.jobscript.replication.ReplicationJobScript}.
	 * <p>
	 * <ul>
	 * <li>0: AUTO_OFFLINE_COPY_NO_EXIST
	 * <li>1: AUTO_OFFLINE_COPY_DISABLED
	 * <li>2: AUTO_OFFLINE_COPY_ENABLED
	 * </ul>
	 * 
	 * @return
	 */
	public int getAutoOfflieCopyStatus() {
		return autoOfflieCopyStatus;
	}

	/**
	 * Set automatic offline copy status. See {@link com.ca.arcflash.jobscript.replication.ReplicationJobScript}.
	 * <p>
	 * <ul>
	 * <li>0: AUTO_OFFLINE_COPY_NO_EXIST
	 * <li>1: AUTO_OFFLINE_COPY_DISABLED
	 * <li>2: AUTO_OFFLINE_COPY_ENABLED
	 * </ul>
	 * 
	 * @param autoOfflieCopyStatus
	 */
	public void setAutoOfflieCopyStatus(int autoOfflieCopyStatus) {
		this.autoOfflieCopyStatus = autoOfflieCopyStatus;
	}

	/**
	 * Get the heart beat status of the node. See {@link com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript}
	 * for available values.
	 * 
	 * @return
	 */
	public int getHeartbeatStatus() {
		return heartbeatStatus;
	}

	/**
	 * Set the heart beat status of the node. See {@link com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript}
	 * for available values.
	 * 
	 * @param heartbeatStatus
	 */
	public void setHeartbeatStatus(int heartbeatStatus) {
		this.heartbeatStatus = heartbeatStatus;
	}

	/**
	 * Get the snapshot that is currently running.
	 * 
	 * @return
	 */
	@EncryptSave
	public String getCurrentRunningSnapshot() {
		return currentRunningSnapshot;
	}

	/**
	 * Set the snapshot that is currently running.
	 * 
	 * @param currentRunningSnapshot
	 */
	public void setCurrentRunningSnapshot(String currentRunningSnapshot) {
		this.currentRunningSnapshot = currentRunningSnapshot;
	}

	public String getStandbyVMRecentSnapshot() {
		return standbyVMRecentSnapshot;
	}

	public void setStandbyVMRecentSnapshot(String standbyVMRecentSnapshot) {
		this.standbyVMRecentSnapshot = standbyVMRecentSnapshot;
	}

	/**
	 * Get the time zone offset of the snapshot.
	 * 
	 * @return
	 */
	public long getSnapshotTimeZoneOffset() {
		return snapshotTimeZoneOffset;
	}

	/**
	 * Set the time zone offset of the snapshot.
	 * 
	 * @param snapshotTimeZoneOffset
	 */
	public void setSnapshotTimeZoneOffset(long snapshotTimeZoneOffset) {
		this.snapshotTimeZoneOffset = snapshotTimeZoneOffset;
	}

	public long getLastUpdateTimeDiffSeconds() {
		return lastUpdateTimeDiffSeconds;
	}

	public void setLastUpdateTimeDiffSeconds(long lastUpdateTimeDiffSeconds) {
		this.lastUpdateTimeDiffSeconds = lastUpdateTimeDiffSeconds;
	}
}
