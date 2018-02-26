package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RolePrivilegeModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1447459311101179840L;

	private static final String STR_KEY_BACKUPFLAG = "backupFlag";
	private static final String STR_KEY_RESTROEFLAG = "RestoreFlag";
	private static final String STR_KEY_SETTINGFLAG = "SettingFlag";
	private static final String STR_KEY_COPYRECOVERPOINTFLAG = "CopyRecoverPointFlag";
	private static final String STR_KEY_MOUNTRECOVERPOINTFLAG = "MountRecoverPointFlag";
	private static final String STR_KEY_VCMFLAG="vcmConfigFlag";
	
	public static final int DISPLAY_DISABLE=-1;

	public Integer getBackupFlag() {
		return get(STR_KEY_BACKUPFLAG);

	}

	public void setBackupFlag(Integer backupFlag) {
		set(STR_KEY_BACKUPFLAG, backupFlag);
	}

	public Integer getRestoreFlag() {
		return get(STR_KEY_RESTROEFLAG);
	}

	public void setRestoreFlag(Integer restoreFlag) {
		set(STR_KEY_RESTROEFLAG, restoreFlag);
	}

	public Integer getSettingFlag() {
		return get(STR_KEY_SETTINGFLAG);
	}

	public void setSettingFlag(Integer settingFlag) {
		set(STR_KEY_SETTINGFLAG, settingFlag);
	}

	public Integer getCopyRecoverPointFlag() {
		return get(STR_KEY_COPYRECOVERPOINTFLAG);
	}

	public void setCopyRecoverPointFlag(Integer copyRecoverPointFlag) {
		set(STR_KEY_COPYRECOVERPOINTFLAG, copyRecoverPointFlag);
	}

	public Integer getMountRecoverPointFlag() {
		return get(STR_KEY_MOUNTRECOVERPOINTFLAG);
	}

	public void setMountRecoverPointFlag(Integer mountRecoverPointFlag) {
		set(STR_KEY_MOUNTRECOVERPOINTFLAG, mountRecoverPointFlag);
	}
	
	public void setVcmConfigFlag(Integer vcmConfigFlag){
		set(STR_KEY_VCMFLAG, vcmConfigFlag);
	}
	
	public Integer getVcmConfigFlag(){
		return get(STR_KEY_VCMFLAG);
	}

}
