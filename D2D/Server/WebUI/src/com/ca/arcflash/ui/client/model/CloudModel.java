package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CloudModel extends BaseModelData{
	
	private static final long serialVersionUID = -1190009901052662442L;
	
	public Long getResult() {
		return (Long) get("result");
	}
	
	public void setResult(Long result) {
		set("result",result);
	}
	
	public String getRegion() {
		return  get("region");
	}
	
	public void setRegion(String region) {
		set("region",region);
	}
	
	public String getEncodedBucketName() {
		return  get("encodedBucketName");
	}
	
	public void setEncodedBucketName(String encodedBucketName) {
		set("encodedBucketName",encodedBucketName);
    }
	
	public String getBucketName() {
		return  get("bucketName");
	}
	
	public void setBucketName(String bucketName) {
		set("bucketName",bucketName);
    }
}