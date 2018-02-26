package com.ca.arcflash.ui.client.homepage;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author uppra02
 *
 */
@RemoteServiceRelativePath("entitlementRegister")
public interface EntitlementRegisterService extends RemoteService{
	HashMap<String, String> registerEntitlementDetails(HashMap<String,String> entitlementRegisterMap);
	String submitAERPJob();
	String isActivated();
	String validateRegistrationDetails(HashMap<String,String> entitlementRegisterMap);
	HashMap<String, String> getRegistrationDetails();
	String cancelRegistration(HashMap<String,String> entitlementRegisterMap);
}
