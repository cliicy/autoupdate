package com.ca.arcflash.webservice.jni.model;

public class JJobScriptDiskDataStore {
	
	private String disk;
	
	private String dataStore;
	
	private long ulDiskType;
	
	private long ulQuickRecovery;

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getDataStore() {
		return dataStore;
	}

	public void setDataStore(String dataStore) {
		this.dataStore = dataStore;
	}
	
	public long getUlDiskType() {
		return ulDiskType;
	}

	public void setUlDiskType(long ulDiskType) {
		this.ulDiskType = ulDiskType;
	}

	public long getUlQuickRecovery() {
		return ulQuickRecovery;
	}

	public void setUlQuickRecovery(long ulQuickRecovery) {
		this.ulQuickRecovery = ulQuickRecovery;
	}

}
