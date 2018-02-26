package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JJobScriptRestoreVolApp {
	private int ulFileSystem;
	private int ulSubSessNum;
	private String pwszPath;
	private int nVolItemAppComp;
	private List<JJobScriptVolAppItem> pVolItemAppCompList;
	private int nDestItemCount;
	private List<JJobScriptVolAppItem> pDestItemList;
	private String pDestVolumeName;
	private int nFilterItems;
	private int OnConflictMethod;
	private int fOptions;
	private JJobScriptRestoreOptionExch pRestoreOption_Exch;
	private JJobScriptRestoreOptionAD adOption;
	
	public int getUlFileSystem() {
		return ulFileSystem;
	}
	public void setUlFileSystem(int ulFileSystem) {
		this.ulFileSystem = ulFileSystem;
	}
	public int getUlSubSessNum() {
		return ulSubSessNum;
	}
	public void setUlSubSessNum(int ulSubSessNum) {
		this.ulSubSessNum = ulSubSessNum;
	}
	public String getPwszPath() {
		return pwszPath;
	}
	public void setPwszPath(String pwszPath) {
		this.pwszPath = pwszPath;
	}
	public int getNVolItemAppComp() {
		return nVolItemAppComp;
	}
	public void setNVolItemAppComp(int volItemAppComp) {
		nVolItemAppComp = volItemAppComp;
	}
	public List<JJobScriptVolAppItem> getPVolItemAppCompList() {
		return pVolItemAppCompList;
	}
	public void setPVolItemAppCompList(List<JJobScriptVolAppItem> volItemAppCompList) {
		pVolItemAppCompList = volItemAppCompList;
	}
	public int getNDestItemCount() {
		return nDestItemCount;
	}
	public void setNDestItemCount(int destItemCount) {
		nDestItemCount = destItemCount;
	}
	public List<JJobScriptVolAppItem> getPDestItemList() {
		return pDestItemList;
	}
	public void setPDestItemList(List<JJobScriptVolAppItem> destItemList) {
		pDestItemList = destItemList;
	}
	public String getPDestVolumeName() {
		return pDestVolumeName;
	}
	public void setPDestVolumeName(String destVolumeName) {
		pDestVolumeName = destVolumeName;
	}
	public int getNFilterItems() {
		return nFilterItems;
	}
	public void setNFilterItems(int filterItems) {
		nFilterItems = filterItems;
	}
	public int getOnConflictMethod() {
		return OnConflictMethod;
	}
	public void setOnConflictMethod(int onConflictMethod) {
		OnConflictMethod = onConflictMethod;
	}
	public int getFOptions() {
		return fOptions;
	}
	public void setFOptions(int options) {
		fOptions = options;
	}
	
	public JJobScriptRestoreOptionExch getpRestoreOption_Exch() {
		return pRestoreOption_Exch;
	}
	public void setpRestoreOption_Exch(
			JJobScriptRestoreOptionExch pRestoreOptionExch) {
		pRestoreOption_Exch = pRestoreOptionExch;
	}
	public JJobScriptRestoreOptionAD getAdOption() {
		return adOption;
	}
	public void setAdOption(JJobScriptRestoreOptionAD adOption) {
		this.adOption = adOption;
	}
	
}
