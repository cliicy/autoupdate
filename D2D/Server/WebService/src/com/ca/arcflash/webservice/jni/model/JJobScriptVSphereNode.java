package com.ca.arcflash.webservice.jni.model;

import java.util.LinkedList;
import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;

public class JJobScriptVSphereNode {
	
	private String pwszNodeName;
	private String pwszNodeAddr;
	private String pwszUserName;
	@NotPrintAttribute
	private String pwszUserPW;
	private String pwszSessPath;
	private int ulSessNum;
	private int nVolumeApp;
	
	private JJobScriptBackupVC vc;
	private JJobScriptBackupVM vm;
	private List<JJobScriptBackupVM> vAppChildVMList = new LinkedList<JJobScriptBackupVM>();
	private int nFilterItems;
	private int fOptions;
	private List<JJobScriptRestoreVolApp> pRestoreVolumeAppList = new LinkedList<JJobScriptRestoreVolApp>();
	private JJobScriptBackupOptionExch pBackupOption_Exch;
	private String TransportMode;
	private int VmwareQuiescenceMethod;
	private int HyperVSnapshotConsistencyType;
	private boolean HyperVSnapshotSeparationIndividually;
	private boolean runCommandEvenFailed;
	
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
	public JJobScriptBackupVC getVc() {
		return vc;
	}
	public void setVc(JJobScriptBackupVC vc) {
		this.vc = vc;
	}
	public JJobScriptBackupVM getVm() {
		return vm;
	}
	public void setVm(JJobScriptBackupVM vm) {
		this.vm = vm;
	}	
	public JJobScriptBackupOptionExch getpBackupOption_Exch() {
		return pBackupOption_Exch;
	}
	public void setpBackupOption_Exch(JJobScriptBackupOptionExch pBackupOptionExch) {
		pBackupOption_Exch = pBackupOptionExch;
	}
	public List<JJobScriptRestoreVolApp> getpRestoreVolumeAppList() {
		return pRestoreVolumeAppList;
	}
	public void setpRestoreVolumeAppList(
			List<JJobScriptRestoreVolApp> pRestoreVolumeAppList) {
		this.pRestoreVolumeAppList = pRestoreVolumeAppList;
	}
	public List<JJobScriptBackupVM> getvAppChildVMList() {
		return vAppChildVMList;
	}
	public void setvAppChildVMList(List<JJobScriptBackupVM> vAppChildVMList) {
		this.vAppChildVMList = vAppChildVMList;
	}	
	public String getTransportMode() {
		return TransportMode;
	}
	public void setTransportMode(String transportMode) {
		TransportMode = transportMode;
	}
	public int getVmwareQuiescenceMethod() {
		return VmwareQuiescenceMethod;
	}
	public void setVmwareQuiescenceMethod(int vmwareQuiescenceMethod) {
		VmwareQuiescenceMethod = vmwareQuiescenceMethod;
	}	
	public int getHyperVSnapshotConsistencyType() {
		return HyperVSnapshotConsistencyType;
	}
	public void setHyperVSnapshotConsistencyType(int hyperVSnapshotConsistencyType) {
		HyperVSnapshotConsistencyType = hyperVSnapshotConsistencyType;
	}
	public boolean isHyperVSnapshotSeparationIndividually() {
		return HyperVSnapshotSeparationIndividually;
	}
	public void setHyperVSnapshotSeparationIndividually(
			boolean hyperVSnapshotSeparationIndividually) {
		HyperVSnapshotSeparationIndividually = hyperVSnapshotSeparationIndividually;
	}
	public boolean isRunCommandEvenFailed() {
		return runCommandEvenFailed;
	}
	public void setRunCommandEvenFailed(boolean runCommandEvenFailed) {
		this.runCommandEvenFailed = runCommandEvenFailed;
	}
}
