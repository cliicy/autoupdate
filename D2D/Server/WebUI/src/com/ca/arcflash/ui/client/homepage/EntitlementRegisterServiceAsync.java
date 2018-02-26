package com.ca.arcflash.ui.client.homepage;


import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author uppra02
 *
 */
public interface EntitlementRegisterServiceAsync {
	public void registerEntitlementDetails(HashMap<String,String> entitlementRegisterMap, AsyncCallback<HashMap<String, String>> callback);
	public void submitAERPJob(AsyncCallback<String> callback);
	public void isActivated(AsyncCallback<String> callback);
	public void validateRegistrationDetails(HashMap<String,String> entitlementRegisterMap, AsyncCallback<String> callback);
	public void getRegistrationDetails(AsyncCallback<HashMap<String,String>> callback);
	public void cancelRegistration(HashMap<String,String> entitlementRegisterMap, AsyncCallback<String> callback);
}
