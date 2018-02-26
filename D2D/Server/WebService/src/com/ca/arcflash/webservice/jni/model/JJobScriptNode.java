package com.ca.arcflash.webservice.jni.model;

import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;

public class JJobScriptNode {
	private String pwszNodeName;
	private String pwszNodeAddr;
	private String pwszUserName;
	@NotPrintAttribute
	private String pwszUserPW;
	private String pwszSessPath;
	private int ulSessNum;
	private int nVolumeApp;
	private List<JJobScriptBackupVol> pBackupVolumeList;
	private List<JJobScriptRestoreVolApp> pRestoreVolumeAppList;
	private int nFilterItems;
	private int fOptions;
	private int dwEncryptTypeRestore;
	private String pwszEncryptPasswordRestore;
	private JJobScriptBackupOptionExch pBackupOption_Exch;
	private JJobScriptBackupOptionSp pBackupOption_Sp;

	public String getPwszNodeName() {
		return pwszNodeName;
	}
	public void setPwszNodeName(String pwszNodeName) {
		this.pwszNodeName = pwszNodeName;
	}
	public String getPwszNodeAddr() {
		return pwszNodeAddr;
	}
	public void setPwszNodeAddr(String pwszNodeAddr) {
		this.pwszNodeAddr = pwszNodeAddr;
	}
	public String getPwszUserName() {
		return pwszUserName;
	}
	public void setPwszUserName(String pwszUserName) {
		this.pwszUserName = pwszUserName;
	}
	public String getPwszUserPW() {
		return pwszUserPW;
	}
	public void setPwszUserPW(String pwszUserPW) {
		this.pwszUserPW = pwszUserPW;
	}
	public String getPwszSessPath() {
		return pwszSessPath;
	}
	public void setPwszSessPath(String pwszSessPath) {
		this.pwszSessPath = pwszSessPath;
	}
	public int getUlSessNum() {
		return ulSessNum;
	}
	public void setUlSessNum(int ulSessNum) {
		this.ulSessNum = ulSessNum;
	}
	public int getNVolumeApp() {
		return nVolumeApp;
	}
	public void setNVolumeApp(int volumeApp) {
		nVolumeApp = volumeApp;
	}
	public List<JJobScriptBackupVol> getPBackupVolumeList() {
		return pBackupVolumeList;
	}
	public void setPBackupVolumeList(List<JJobScriptBackupVol> backupVolumeList) {
		pBackupVolumeList = backupVolumeList;
	}
	public List<JJobScriptRestoreVolApp> getPRestoreVolumeAppList() {
		return pRestoreVolumeAppList;
	}
	public void setPRestoreVolumeAppList(
			List<JJobScriptRestoreVolApp> restoreVolumeAppList) {
		pRestoreVolumeAppList = restoreVolumeAppList;
	}
	public int getNFilterItems() {
		return nFilterItems;
	}
	public void setNFilterItems(int filterItems) {
		nFilterItems = filterItems;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	public int getDwEncryptTypeRestore() {
		return dwEncryptTypeRestore;
	}
	public void setDwEncryptTypeRestore(int dwEncryptType) {
		this.dwEncryptTypeRestore = dwEncryptType;
	}
	public String getPwszEncryptPasswordRestore() {
		return pwszEncryptPasswordRestore;
	}
	public void setPwszEncryptPasswordRestore(String pwszEncryptPassword) {
		this.pwszEncryptPasswordRestore = pwszEncryptPassword;
	}
	public JJobScriptBackupOptionExch getpBackupOption_Exch() {
		return pBackupOption_Exch;
	}
	public void setpBackupOption_Exch(JJobScriptBackupOptionExch pBackupOptionExch) {
		pBackupOption_Exch = pBackupOptionExch;
	}
	public JJobScriptBackupOptionSp getpBackupOption_Sp() {
		return pBackupOption_Sp;
	}
	public void setpBackupOption_Sp(JJobScriptBackupOptionSp pBackupOptionSp) {
		pBackupOption_Sp = pBackupOptionSp;
	}	

}
