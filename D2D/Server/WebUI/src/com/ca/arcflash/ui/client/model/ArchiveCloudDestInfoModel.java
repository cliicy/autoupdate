package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveCloudDestInfoModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8967327757763989663L;
	
	public ArchiveCloudDestInfoModel()
	{
		set("cloudVendorType",0L);
	}
	
	public Long getcloudVendorType(){
		return (Long)get("cloudVendorType");
	}
	public void setcloudVendorType(Long in_cloudVendorType){
		set("cloudVendorType",in_cloudVendorType);
	}
	
	public Long getCloudSubVendorType(){
		return (Long)get("cloudSubVendorType");
	}
	public void setCloudSubVendorType(Long in_cloudSubVendorType){
		set("cloudSubVendorType",in_cloudSubVendorType);
	}

	public String getcloudVendorURL(){
		return get("cloudVendorURL");
	}
	public void setcloudVendorURL(String in_cloudVendorURL){
		set("cloudVendorURL",in_cloudVendorURL);
	}
	
	public String getcloudBucketName(){
		return get("cloudBucketName");
	}
	public void setcloudBucketName(String cloudBucketName){
		set("cloudBucketName",cloudBucketName);
	}
	
	public String getcloudBucketRegionName(){
		return get("cloudBucketRegionName");
	}
	public void setcloudBucketRegionName(String in_cloudBucketRegionName){
		set("cloudBucketRegionName",in_cloudBucketRegionName);
	}
	
	public String getcloudVendorUserName(){
		return get("cloudVendorUserName");
	}
	public void setcloudVendorUserName(String in_cloudVendorUserName){
		set("cloudVendorUserName",in_cloudVendorUserName);
	}
	public String getcloudVendorPassword(){
		return get("cloudVendorPassword");
	}
	public void setcloudVendorPassword(String in_cloudVendorPassword){
		set("cloudVendorPassword",in_cloudVendorPassword);
	}
	public String getcloudCertificatePath(){
		return get("cloudCertificatePath");
	}
	public void setcloudCertificatePath(String in_cloudCertificatePath){
		set("cloudCertificatePath",in_cloudCertificatePath);
	}
	public String getcloudCertificatePassword(){
		return get("cloudCertificatePassword");
	}
	public void setcloudCertificatePassword(String in_cloudCertificatePassword){
		set("cloudCertificatePassword",in_cloudCertificatePassword);
	}
	public String getcloudVendorHostName(){
		return get("cloudVendorHostName");
	}
	public void setcloudVendorHostName(String in_cloudVendorHostName){
		set("cloudVendorHostName",in_cloudVendorHostName);
	}
	public Long getcloudVendorPort(){
		return (Long)get("cloudVendorPort");
	}
	public void setcloudVendorPort(long in_cloudVendorPort){
		set("cloudVendorPort",in_cloudVendorPort);
	}
	public Boolean getcloudUseProxy(){
		return (Boolean)get("cloudUseProxy");
	}
	public void setcloudUseProxy(Boolean in_bcloudUseProxy){
		set("cloudUseProxy",in_bcloudUseProxy);
	}
	public String getcloudProxyServerName(){
		return get("cloudProxyServerName");
	}
	public void setcloudProxyServerName(String in_cloudProxyServerName){
		set("cloudProxyServerName",in_cloudProxyServerName);
	}
	public Long getcloudProxyPort(){
		return (Long)get("cloudProxyPort");
	}
	public void setcloudProxyPort(Long in_cloudProxyPort){
		set("cloudProxyPort",in_cloudProxyPort);
	}
	public Boolean getcloudProxyRequireAuth(){
		return (Boolean)get("cloudProxyRequireAuth");
	}
	public void setcloudProxyRequireAuth(boolean in_cloudProxyRequireAuth){
		set("cloudProxyRequireAuth",in_cloudProxyRequireAuth);
	}
	public String getcloudProxyUserName(){
		return get("cloudProxyUserName");
	}
	public void setcloudProxyUserName(String in_cloudProxyUserName){
		set("cloudProxyUserName",in_cloudProxyUserName);
	}
	public String getcloudProxyPassword(){
		return get("cloudProxyPassword");
	}
	public void setcloudProxyPassword(String in_cloudProxyPassword){
		set("cloudProxyPassword",in_cloudProxyPassword);
	}
	
	public String getencodedBucketName(){
		return get("encodedBucket");
	}
	public void setencodedBucketName(String encodedBucket){
		set("encodedBucket",encodedBucket);
	}
	public Long getrrsFlag(){
		return get("rrsFlag");
	}
	public void setrrsFlag(Long rrsFlag){
		set("rrsFlag",rrsFlag);
	}
	
}
