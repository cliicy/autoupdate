package com.ca.arcflash.webservice.jni.model;


public class JMountedRecoveryPointItem{
	private String sesionGuid;
	private long sessionID;
	private long volumeSize;
	private String volumePath;
	private String volumeGuid;
	
	private int mountDiskSignature;
	private String mountPath;
	private int mountFlag;
	
	private String date;
	private String time;
	private String recoveryPointPath;
	
	
	public String getSesionGuid() {
		return sesionGuid;
	}
	public void setSesionGuid(String sesionGuid) {
		this.sesionGuid = sesionGuid;
	}
	public long getSessionID() {
		return sessionID;
	}
	public void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	}
	public long getVolumeSize() {
		return volumeSize;
	}
	public void setVolumeSize(long volumeSize) {
		this.volumeSize = volumeSize;
	}
	public String getVolumePath() {
		return volumePath;
	}
	public void setVolumePath(String volumePath) {
		this.volumePath = volumePath;
	}
	public String getVolumeGuid() {
		return volumeGuid;
	}
	public void setVolumeGuid(String volumeGuid) {
		this.volumeGuid = volumeGuid;
	}
	public int getMountDiskSignature() {
		return mountDiskSignature;
	}
	public void setMountDiskSignature(int mountDiskSignature) {
		this.mountDiskSignature = mountDiskSignature;
	}
	public String getMountPath() {
		return mountPath;
	}
	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}
	public int getMountFlag() {
		return mountFlag;
	}
	public void setMountFlag(int mountFlag) {
		this.mountFlag = mountFlag;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRecoveryPointPath() {
		return recoveryPointPath;
	}
	public void setRecoveryPointPath(String recoveryPointPath) {
		this.recoveryPointPath = recoveryPointPath;
	}
	
	
}
