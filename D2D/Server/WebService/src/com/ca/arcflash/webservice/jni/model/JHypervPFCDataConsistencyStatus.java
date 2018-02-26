package com.ca.arcflash.webservice.jni.model;

import java.io.Serializable;

public class JHypervPFCDataConsistencyStatus implements Serializable{
	private static final long serialVersionUID = 1L;

	// 1 if the VM has File Systems other than NTFS/ReFS
	private int hasNotSupportedFileSystem;

	// 1 if the VM has dynamic disk/GPT style disk
	private int hasNotSupportedDiskType;

	// 1 if the integration service is in a bad state
	private int isIntegrationServiceInBadState;

	// 1 if the ScopeSnapshot is enabled in the VM
	private int isScopeSnapshotEnabled;

	// 1 if the VM has physical disk drive
	private int isPhysicalHardDisk;

	// 1 if the VM has disk image on remote share
	private int hasDiskOnRemoteShare;
	
	// 1 if the VM credential is NOT OK
	private int isVMCredentialNotOK;
	
	// 1 if data consistency is NOT supported
	private int isDataConsistencyNotSupported;
	
	// 1 if a volume's shadow copy storage is not located on itself
	private int hasShadowStorageOnDifferentVolume;
	
	// 1 if the VM has storage space
	private int hasStorageSpace;
	
	public int getHasNotSupportedFileSystem() {
		return hasNotSupportedFileSystem;
	}

	public int getHasNotSupportedDiskType() {
		return hasNotSupportedDiskType;
	}

	public int getIsIntegrationServiceInBadState() {
		return isIntegrationServiceInBadState;
	}

	public int getIsScopeSnapshotEnabled() {
		return isScopeSnapshotEnabled;
	}

	public int getIsPhysicalHardDisk() {
		return isPhysicalHardDisk;
	}

	public int getHasDiskOnRemoteShare() {
		return hasDiskOnRemoteShare;
	}

	public void setHasNotSupportedFileSystem(int hasNotSupportedFileSystem) {
		this.hasNotSupportedFileSystem = hasNotSupportedFileSystem;
	}

	public void setHasNotSupportedDiskType(int hasNotSupportedDiskType) {
		this.hasNotSupportedDiskType = hasNotSupportedDiskType;
	}

	public void setIsIntegrationServiceInBadState(int isIntegrationServiceInBadState) {
		this.isIntegrationServiceInBadState = isIntegrationServiceInBadState;
	}

	public void setIsScopeSnapshotEnabled(int isScopeSnapshotEnabled) {
		this.isScopeSnapshotEnabled = isScopeSnapshotEnabled;
	}

	public void setIsPhysicalHardDisk(int isPhysicalHardDisk) {
		this.isPhysicalHardDisk = isPhysicalHardDisk;
	}

	public void setHasDiskOnRemoteShare(int hasDiskOnRemoteShare) {
		this.hasDiskOnRemoteShare = hasDiskOnRemoteShare;
	}

	public int getIsVMCredentialNotOK()
	{
		return isVMCredentialNotOK;
	}

	public void setIsVMCredentialNotOK(int isVMCredentialNotOK)
	{
		this.isVMCredentialNotOK = isVMCredentialNotOK;
	}

	public int getIsDataConsistencyNotSupported()
	{
		return isDataConsistencyNotSupported;
	}

	public void setIsDataConsistencyNotSupported(int isDataConsistencyNotSupported)
	{
		this.isDataConsistencyNotSupported = isDataConsistencyNotSupported;
	}

	public int getHasShadowStorageOnDifferentVolume()
	{
		return hasShadowStorageOnDifferentVolume;
	}

	public void setHasShadowStorageOnDifferentVolume(int hasShadowStorageOnDifferentVolume)
	{
		this.hasShadowStorageOnDifferentVolume = hasShadowStorageOnDifferentVolume;
	}

	public int getHasStorageSpace()
	{
		return hasStorageSpace;
	}

	public void setHasStorageSpace(int hasStorageSpace)
	{
		this.hasStorageSpace = hasStorageSpace;
	}
}