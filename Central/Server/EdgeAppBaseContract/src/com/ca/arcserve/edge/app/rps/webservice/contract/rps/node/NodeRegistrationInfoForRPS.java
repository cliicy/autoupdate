package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;

public class NodeRegistrationInfoForRPS extends NodeRegistrationInfo{
	
	private static final long serialVersionUID = 1L;
	DeployTargetDetail targetDetail;
	List<DataStoreSettingInfo> dataStoreSettings;

	public DeployTargetDetail getTargetDetail() {
		return targetDetail;
	}
	public void setTargetDetail(DeployTargetDetail targetDetail) {
		this.targetDetail = targetDetail;
	}
	public List<DataStoreSettingInfo> getDataStoreSettings() {
		return dataStoreSettings;
	}
	public void setDataStoreSettings(List<DataStoreSettingInfo> dataStoreSettings) {
		this.dataStoreSettings = dataStoreSettings;
	}
}
