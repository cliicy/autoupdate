package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class MountedRecoveryPointItemModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -754372283521972747L;
	
	public String getSessionGuid() {
		return (String)get("sessionGuid");
	}
	public void setSessionGuid(String sessionGuid) {
		set("sessionGuid", sessionGuid);
	}
	
	public Long getSessionID() {
		return (Long)get("sessionID");
	}
	public void setSessionID(Long sessionID) {
		set("sessionID", sessionID);
	}
	
	public Long getVolumeSize() {
		return (Long)get("volumeSize");
	}
	public void setVolumeSize(Long volumeSize) {
		set("volumeSize", volumeSize);
	}
	
	public String getVolumePath() {
		return (String)get("volumePath");
	}
	public void setVolumePath(String volumePath) {
		set("volumePath", volumePath);
	}
	
	public String getVolumeGuid() {
		return (String)get("volumeGuid");
	}
	public void setVolumeGuid(String volumeGuid) {
		set("volumeGuid", volumeGuid);
	}
	
	public Integer getMountDiskSignature() {
		return (Integer)get("mountDiskSignature");
	}
	public void setMountDiskSignature(Integer mountDiskSignature) {
		set("mountDiskSignature", mountDiskSignature);
	}
	
	public String getMountPath() {
		return (String)get("mounthPath");
	}
	public void setMountPath(String mounthPath) {
		set("mounthPath", mounthPath);
	}
	public Integer getMountFlag() {
		return (Integer)get("mountFlag");
	}
	public void setMountFlag(Integer mountFlag) {
		set("mountFlag", mountFlag);
	}
	
	public String getRecoveryPointPath(){
		return (String)get("recoveryPointPath");
	}
	
	public void setRecoveryPointPath(String recoveryPointPath){
		set("recoveryPointPath", recoveryPointPath);
	}
	
	public Date getTime() {
		return (Date) get("time");
	}
	public void setTime(Date time) {
		set("time",time);
	}	
	
	public Integer getTimeZoneOffset() {
		return get("timeZoneOffset");
	}
	public void setTimeZoneOffset(Integer timeZoneOffset) {
		set("timeZoneOffset", timeZoneOffset);
	}

	public Boolean getIsReadOnly() {
		return (Boolean)get("readOnly");
	}
	public void setIsReadOnly(Boolean readOnly) {
		set("readOnly", readOnly);
	}
}
