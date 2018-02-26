package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.model.BucketDetailsModel;
import com.ca.arcflash.ui.client.model.CloudModel;

public interface ICloudBucketsCacheManager {

	public void cacheBuckets(List<BucketDetailsModel> buckets);

	public void updateBucketsCache(BucketDetailsModel bucket);

	public List<BucketDetailsModel> getBucketsListFromCache();

	public boolean isRegionAvailableForBucketInCache(String bucket);

	public CloudModel[] filterBuckets(CloudModel[] buckets, String mode);

}