package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMNetworkStandardConfigInfoModel extends BaseModelData
{
	private static final long serialVersionUID = -2257429975869565296L;
	private static String TAG_NETWORKNAME = "networkName";
	
	public String getNetworkName() {
		return get(TAG_NETWORKNAME);
	}
	public void setNetworkName(String networkName) {
		set(TAG_NETWORKNAME, networkName);
	}
}
