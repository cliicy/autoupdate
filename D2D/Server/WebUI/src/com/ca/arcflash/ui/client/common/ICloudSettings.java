package com.ca.arcflash.ui.client.common;

import java.util.HashMap;


import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.CloudSubVendorType;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICloudSettings {
	
	public void disableProxyByDefault();
	
	public ArchiveCloudDestInfoModel save();
	
	public void refreshData(ArchiveCloudDestInfoModel cloudDestInfoModel);
	
	public boolean validate(boolean validateBucket);
	
	public ArchiveCloudDestInfoModel getArchiveCloudInfo();	
	
	public ArchiveCloudDestInfoModel getArchiveCloudConfigModel();
	
	public LayoutContainer getLcCloudSettingsContainer();
	
	public void setVendorUrl(String vendorUrl);
	
	public String getVendorUrl();
	
	public void validateForEdge(AsyncCallback<Long> callback);
	
	public void setVendorURL(HashMap<String,CloudVendorInfoModel> providerInfo);
	
	public void showHideFieldsForCloudSubVendor(CloudSubVendorType cloudSubVendor);

}
