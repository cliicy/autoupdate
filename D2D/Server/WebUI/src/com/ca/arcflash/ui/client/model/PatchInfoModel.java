package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
//import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.user.client.ui.Label;

public class PatchInfoModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5449255731632759077L;

	public void setPatchDownloadLocation(String in_strpatchdownloadlocation) {
		set("PatchDownloadLocation",in_strpatchdownloadlocation);
	}
	public String getPatchDownloadLocation() {
		return get("PatchDownloadLocation");
	}
	public void setMajorversion(int in_imajorversion) {
		set("Majorversion",in_imajorversion);
	}
	public Integer getMajorversion() {
		return (Integer)get("Majorversion");
	}
	public void setServicePack(String in_strServicePack) {
		set("ServicePack",in_strServicePack);
	}
	public String getServicePack() {
		return get("ServicePack");
	}
	public void setMinorVersion(int in_iminorVersion) {
		set("MinorVersion",in_iminorVersion);
	}
	public Integer getMinorVersion() {
		return (Integer)get("MinorVersion");
	}
	public void setPackageID(String in_strPackageID) {
		set("PackageID",in_strPackageID);
	}
	public String getPackageID() {
		return get("PackageID");
	}
	public void setPublishedDate(String in_strPublishedDate) {
		set("PublishedDate",in_strPublishedDate);
	}
	public String getPublishedDate() {
		return get("PublishedDate");
	}
	public void setDescription(String in_strDescription) {
		set("Description",in_strDescription);
	}
	public String getDescription() {
		return get("Description");
	}
	public void setPatchURL(String in_strPatchURL) {
		set("UpdateURL",in_strPatchURL);
	}
	public String getPatchURL() {
		return get("UpdateURL");
	}

	public void setRebootRequired(int in_iRebootRequired) {
		set("RebootRequired",in_iRebootRequired);
	}

	public Integer getRebootRequired() {
		return (Integer)get("RebootRequired");
	}
	
	public void setDownloadStatus(int in_iDownloadStatus) {
		set("DownloadStatus",in_iDownloadStatus);
	}

	public Integer getDownloadStatus() {
		return (Integer)get("DownloadStatus");
	}

	public void setInstallStatus(int in_iInstallStatus) {
		set("InstallStatus",in_iInstallStatus);
	}

	public Integer getInstallStatus() {
		return (Integer)get("InstallStatus");
	}	

	public void setAvailableStatus(int in_iAvailableStatus) {
		set("AvailableStatus",in_iAvailableStatus);
	}

	public Integer getAvailableStatus() {
		return (Integer)get("AvailableStatus");
	}

	public void setPatchVersionNumber(int in_ipatchVersionNumber) {
		set("PatchVersionNumber",in_ipatchVersionNumber);
	}

	public Integer getPatchVersionNumber() {
		return (Integer)get("PatchVersionNumber");
	}

	public void setSize(int in_isize) {
		set("Size",in_isize);
	}

	public Integer getSize() {
		return (Integer)get("Size");
	}

	public void setError_Status(int in_ierror_Status)
	{
		set("Error_Status",in_ierror_Status);
	}

	public Integer getError_Status() {
		return (Integer)get("Error_Status");
	}
	
	public void setErrorMessage(String in_strErrorMessage)
	{
		set("ErrorMessage",in_strErrorMessage);
	}
	
	public String getErrorMessage()
	{
		return get("ErrorMessage");
	}
	
	//added by cliicy.luo
	public void setPatchUpdateName(String in_strPatchUpdateName) {
		set("Update",in_strPatchUpdateName);
	}
	public String getPatchUpdateName() {
		return get("Update");
	}
	
	public void setPatchDependency(String in_strPatchPdy) {
		set("Dependency",in_strPatchPdy);
	}
	public String getPatchDependency() {
		return get("Dependency");
	}
	
	public void setInstalldepLink(Label lInstalldepLink) {
		set("InstalldepLink",lInstalldepLink);
	}
	public Label getInstalldepLink() {
		return get("InstalldepLink");
	}
	//added by cliicy.luo
}
