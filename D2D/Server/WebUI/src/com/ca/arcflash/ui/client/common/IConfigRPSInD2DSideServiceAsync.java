package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.model.ProxySettingsModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2D;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IConfigRPSInD2DSideServiceAsync {
	void getRPSPolicyList(String hostName, String userName, String password,
			int port, String protocol,ProxySettingsModel proxy, AsyncCallback<List<RpsPolicy4D2D>> callback);	
	void getRPSHostList(AsyncCallback<List<RpsHostModel>> callback);
}
