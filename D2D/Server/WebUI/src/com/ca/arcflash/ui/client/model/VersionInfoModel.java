package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VersionInfoModel extends BaseModelData {

	private static final long serialVersionUID = 5664230335630062206L;
	
	public String getCountry() {
		return get("country");
	}
	public void setCountry(String country) {
		set("country", country);
	}
	
	public String getLocale() {
		return get("locale");
	}
	public void setLocale(String locale) {
		set("locale", locale);
	}
	public String getTimeZoneID() {
		return get("timeZoneID");
	}
	public void setTimeZoneID(String timeZoneID) {
		 set("timeZoneID", timeZoneID);
	}
	public Integer getTimeZoneOffset() {
		return get("timeZoneOffset");
	}
	public void setTimeZoneOffset(Integer timeZoneOffset) {
		set("timeZoneOffset", timeZoneOffset);
	}
	public String getMajorVersion() {
		return get("majorVersion");
	}
	public void setMajorVersion(String majorVersion) {
		set("majorVersion", majorVersion);
	}
	public String getMinorVersion() {
		return get("minorVersion");
	}
	public void setMinorVersion(String minorVersion) {
		set("minorVersion", minorVersion);
	}
	public String getBuildNumber() {
		return get("buildNumber");
	}
	public void setBuildNumber(String buildNumber) {
		set("buildNumber", buildNumber);
	}
	
	public String getUpdateNumber() {
		return get("UpdateNumber");
	}
	public void setUpdateNumber(String in_updateNumber) {
		set("UpdateNumber", in_updateNumber);
	}
	
	public void setLocalDriverLetters(List<String> localDriverLetters) {
		set("localDriverLetters", localDriverLetters);
	}
	public List<String> getLocalDriverLetters() {
		return get("localDriverLetters");
	}	

	public Boolean isShowSocialNW() {
//		return get("ShowSocialNW");
		return false;
	}

	public void setShowSocialNW(Boolean isShowSocialNW) {
		set("ShowSocialNW", isShowSocialNW);
	}
	public void setFailoverModeVMType(String vmType){
		set("FailoverModeVMType",vmType);
	}
	public String getFailoverModeVMType(){
		return get("FailoverModeVMType");
	}
	public Integer getLocalADTPackage() {
		return get("localADTPackage");
	}

	public void setLocalADTPackage(Integer localADTPackage) {
		set("localADTPackage", localADTPackage);
	}

	public String getLocalHostName() {
		return get("localHostName");
	}

	public void setLocalHostName(String localHostName) {
		set("localHostName", localHostName);
	}
	
	public Integer getRequestMethod() {
		return (Integer)get("RequestMethod");
	}
	
	public void setRequestMethod(Integer method) {
		set("RequestMethod", method);
	}
	
	public Integer getProductType() {
		return (Integer)get("ProductType");
	}
	
	public void setProductType(Integer ProductType) {
		set("ProductType", ProductType);
	}
	
	public Boolean isShowUpdate() {
		return (Boolean)get("ShowUpdate");
	}
	
	public void setShowUpdate(Boolean show) {
		set("ShowUpdate", show);
	}
	
	public void setDedupInstalled(Boolean isDedupInstalled){
		set("isDedupInstalled",isDedupInstalled);
	}
	
	public Boolean isDedupInstalled(){
		return (Boolean)get("isDedupInstalled");
	}
	
	public void setWin8(Boolean isWin8){
		set("isWin8",isWin8);
	}
	
	public Boolean isWin8(){
		return (Boolean)get("isWin8");
	}
	
	public String getOsName(){
		return (String)get("osName");
	}
	
	public void setOsName(String osName){
		set("osName",osName);
	}
	
	
	public Boolean isUefiFirmware() {
		return (Boolean)get("uefi");
	}
	public void setUefiFirmware(Boolean uefi) {
		set("uefi", uefi);
	}
	
	public Boolean isReFsSupported() {
		return (Boolean)get("isReFsSupported");
	}
	public void setReFsSupported(Boolean isReFsSupported) {
		set("isReFsSupported", isReFsSupported);
	}
	
	/*public void setEdgeInfoCM(EdgeInfoModel edgeInfo){
		set("edgeInfoCM",edgeInfo);
	}
	
	public EdgeInfoModel getEdgeInfoCM(){
		return get("edgeInfoCM");
	}
	public void setEdgeInfoVCM(EdgeInfoModel edgeInfo){
		set("edgeInfoVCM",edgeInfo);
	}
	
	public EdgeInfoModel getEdgeInfoVCM(){
		return get("edgeInfoVCM");
	}
	public void setEdgeInfoVS(EdgeInfoModel edgeInfo){
		set("edgeInfoVS",edgeInfo);
	}
	
	public EdgeInfoModel getEdgeInfoVS(){
		return get("edgeInfoVS");
	}*/
	public EdgeInfoModel edgeInfoCM;
	public EdgeInfoModel edgeInfoVCM;
	public EdgeInfoModel edgeInfoVS;
	public DataFormatModel dataFormat;

	public void setDisplayVserion(String displayVersion) {
		set("displayVersion", displayVersion);		
	}
	
	public String getDisplayVserion() {
		return (String)get("displayVersion");		
	}
	
	public Boolean isSettingConfiged() {
		return (Boolean)get("isSettingConfiged");
	}
	public void setSettingConfiged(Boolean isSettingConfiged) {
		set("isSettingConfiged", isSettingConfiged);
	}
	public void setUserName(String userName) {
		set("userName", userName);		
	}	
	
	public String getUserName() {
		return (String)get("userName");		
	}
	
	//No Charged Edition
	public Boolean isNCE() {
		Boolean bRet = (Boolean)get("NCE");
		if(bRet == null) {
			return false;
		}
		return bRet;
	}
	
	public void setNCE(Boolean nce) {
		set("NCE", nce);
	}
	
	
		public String getUpdateBuildNumber() {
			return get("UpdateBuildNumber");
		}
		public void setUpdateBuildNumber(String in_updateBuildNumber) {
			set("UpdateBuildNumber", in_updateBuildNumber);
		}
}
