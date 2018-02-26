package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BucketDetailsModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1852025327294933996L;

	public BucketDetailsModel() {

	}

	public BucketDetailsModel(String name, String region, String encodedName) {
//		setBucketId(bucketId);
		setBucketName(name);
		setRegion(region);	
		setEncodedBucketName(encodedName);
	}

	public String getRegion() {
		return get("region");
	}

	public void setRegion(String region) {
		set("region", region);
	}


	public String getBucketName() {
		return get("name");
	}

	public void setBucketName(String name) {
		set("name", name);
	}
	
	public String getEncodedBucketName() {
		return get("encodedName");
	}

	public void setEncodedBucketName(String encodedName) {
		set("encodedName", encodedName);
	}
	
	public String getBucketId() {
		return get("bucketId");
	}

	public void setBucketId(String bucketId) {
		set("bucketId", bucketId);
	}
	

}
