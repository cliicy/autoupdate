/**
 * 
 */
package com.ca.arcflash.ui.server;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.arcserve.cserp.entitlements.entitlementReg.client.EntitlementRegister;
import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.ca.arcflash.ui.client.homepage.EntitlementRegisterService;

/**
 * @author uppra02
 *
 */
@SuppressWarnings("serial")
public class EntitlementRegisterServiceImpl extends BaseServiceImpl implements EntitlementRegisterService 
{
	
	private static final Logger logger = Logger.getLogger(EntitlementRegisterServiceImpl.class);
	/**
	 * 
	 */
	@Override
	public HashMap<String, String> registerEntitlementDetails(HashMap<String,String> entitlementRegisterMap) {
		HashMap<String, String> result = new HashMap<String,String>();
		try {
			logger.info("Registering Entitlement Details calling web service" + this.getClass().getName());
			String[] response = this.getServiceClient().getService().registerEntitlementDetails(
					entitlementRegisterMap.get("name"),
					entitlementRegisterMap.get("company"),
					entitlementRegisterMap.get("contactNumber"),
					entitlementRegisterMap.get("emailID"),
					entitlementRegisterMap.get("netSuiteId"));
			logger.info("Response registerEntitlement details " + response);
			if(response != null && response.length > 0)
			{
				result.put("responseMsg", response[0]);
				logger.info("Response SubmitAERPJob " + response[1]);
				if(response[1].equalsIgnoreCase("true"))
				{
					result.put("submitAERPJob", response[1]);
					logger.info("Submmiting AERPJob for registration");
				}
			}
			logger.info("Entitlement Register Service, Success " + this.getClass().getName());
		} 
		catch(Exception e)
		{
			   e.printStackTrace();
			   logger.error("Exception in registerEntitlementDetails" + e.getMessage());
		}
		return result;
	}		

	/**
	 * This method invokes the submitAERPJob service
	 */
	@Override
	public String submitAERPJob() {
		String response = "";
		try
		{
			logger.info("Invoking submitAERPJob service" + this.getClass().getName());
	    	this.getServiceClient().getService().submitAERPJob();
	    	response = "SubmitAERPJob Success " + this.getClass().getName();
	    	logger.info(response);
		}
		catch(Exception e)
		{
			logger.error("Exception in submitAERPJob" + e.getMessage());
			response = "SubmitAERPJob Failure " + this.getClass().getName();
		}
		return response;
	}

	@Override
	public String isActivated() {
		String response = "";
		try
		{
			logger.info("Invoking isActivated service" + this.getClass().getName());
			response = this.getServiceClient().getService().isActivated();
		}
		catch(Exception e)
		{
			logger.error("Error calling isActivated " + e.getMessage());	
		}
		return response;
	}
	
	@Override
	public HashMap<String, String> getRegistrationDetails() {
		HashMap<String, String> regDetails = null;
		try
		{
			regDetails = new EntitlementRegisterUtility().getRegistrationDetails();
			String responseCode = EntitlementRegister.isActivated();
			regDetails.put("isActivated", responseCode);
		}
		catch(Exception e)
		{
			logger.error("Exception in getRegistrationDetails");
		}
		return regDetails;
	}

	@Override
	public String validateRegistrationDetails(
			HashMap<String, String> entitlementRegisterMap) {
		String response = "";
		try
		{
			response =  new EntitlementRegisterUtility().validateEntitlementRegisterData(entitlementRegisterMap);
		}
		catch(Exception e)
		{
			logger.error("Exception in validateRegistrationDetails ");
		}
		return response;
	}


	@Override
	public String cancelRegistration(
			HashMap<String, String> entitlementRegisterMap) {
		logger.info("Invoking cancelRegistration service" + this.getClass());
			String result = "";
			try {
				result = this.getServiceClient().getService().cancelRegistration(
						entitlementRegisterMap.get("name"),
						entitlementRegisterMap.get("company"),
						entitlementRegisterMap.get("contactNumber"),
						entitlementRegisterMap.get("emailID"),
						entitlementRegisterMap.get("netSuiteId"));
			} catch (Exception e) {
				e.printStackTrace();
				result = "CANCELREGISTRATION_FAILED_EXCEPTION";
			}
			return result;
	}
	
	
}
