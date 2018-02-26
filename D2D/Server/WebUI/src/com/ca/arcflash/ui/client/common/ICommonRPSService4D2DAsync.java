package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.ca.arcflash.ui.client.model.rps.RpsDatastore4D2dSettings;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICommonRPSService4D2DAsync {

	void getRPSPolicyList(String hostName, String userName, String password,
			int port, String protocol,
			AsyncCallback<List<RpsPolicy4D2DSettings>> callback);

	void getRPSHostList(AsyncCallback<List<RpsHostModel>> callback);

	void getRPSPolicyList4Restore(String hostName, String userName,
			String password, int port, String protocol,
			AsyncCallback<List<RpsPolicy4D2DRestoreModel>> callback);

	void getRPSDatastoreList(String hostName, String userName, String password,
			int port, String protocol,
			AsyncCallback<List<RpsDatastore4D2dSettings>> callback);

	void getDataStoreStatus(RpsHostModel host, String rpsDataStoreUUID,
			AsyncCallback<Long> callback);
	
}
