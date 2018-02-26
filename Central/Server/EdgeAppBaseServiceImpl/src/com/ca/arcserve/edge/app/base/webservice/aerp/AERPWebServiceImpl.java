package com.ca.arcserve.edge.app.base.webservice.aerp;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.arcserve.cserp.entitlements.entitlementReg.client.EntitlementRegister;
import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.ca.arcserve.edge.app.base.schedulers.aerp.AERPSchedulerService;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IEdgeAERPService;

public class AERPWebServiceImpl implements IEdgeAERPService{
	
	private static final Logger logger = Logger.getLogger(AERPWebServiceImpl.class);
	
	@Override
	public String[] registerEntitlementDetails(String name, String company,
			String contactNumber, String emailID, String netSuiteId) {
		String[] response = new String[2];
		try
		{
			logger.info("Invoking AERPClient to register details" + this.getClass().getName());
			String responseCode = EntitlementRegister.registerEntitlement(name, company, contactNumber, emailID, netSuiteId);
			logger.info("Entitlement Register response " + responseCode);
			if(responseCode != null && (responseCode.equalsIgnoreCase("REGISTRATION_SUCCESS")|| responseCode.equalsIgnoreCase("REGISTRATION_SUCCESS_LINK")))
			{
				response[0] = EdgeCMWebServiceMessages.getResource("REGISTRATION_SUCCESS_AERP");	
				response[1] = "true";
			}
			else
			{
				response[0] = EdgeCMWebServiceMessages.getResource(responseCode);
				response[1] = "false";
			}
			logger.info("Entitlement Register Service, Success" + this.getClass().getName());
		}
		catch(Exception e)
		{
			logger.error("Entitlement Register failed, Service Error" + this.getClass().getName() + " " + e.getMessage());
		}
		return response;
	}

	@Override
	public String submitAERPJob() {
		String responseCode = "SUBMITAERPJOB_FAILED";
		try {
			HashMap<String, String> entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
			if(entitlementFreq != null && entitlementFreq.containsKey("uploadfrequency") && entitlementFreq.containsKey("uploadTimeStamp"))
	    	{
				AERPSchedulerService.getInstance().submitAERPJob(Integer.parseInt(entitlementFreq.get("uploadfrequency").trim()), entitlementFreq.get("uploadTimeStamp").trim());
				responseCode= "SUBMITAERPJOB_SUCCESS";
	    	}
		} catch (Exception e) {
			logger.error("Error submitting AERPJob " + this.getClass().getName() + " " + e.getMessage());
		}
		return EdgeCMWebServiceMessages.getResource(responseCode);
	}

	@Override
	public String isActivated() {
		String response = "ISACTIVATED_EXCEPTION";
		try
		{
			logger.info("Invoking isActivated service");
			response = EntitlementRegister.isActivated();
			logger.info("isActivated Service response " + response);
			if((response.equalsIgnoreCase("ISACTIVATED_ACTIVE") || response.equalsIgnoreCase("ISACTIVATED_INACTIVE"))
					&& !AERPSchedulerService.getInstance().isAERPJobTriggered())  
			{
				logger.info("Triggering  submitAERPJob for isActivated response " + response);
				submitAERPJob();
			}
		}
		catch(Exception e)
		{
			logger.error("Exception Invoking isActivated Service" + e.getMessage());
		}
		return response;
	}
	
	@Override
	public String cancelRegistration(String name, String company, String contactNumber, String emailID, String netSuiteId) {
		// TODO Auto-generated method stub
		String responseCode = "";
		try
		{
			logger.info("Invoking AERPClient to cancel registration details " + this.getClass().getName());
			responseCode = EntitlementRegister.cancelRegistration(name, company, contactNumber, emailID, netSuiteId);
			logger.info("Cancel Register response " + responseCode);
			if(responseCode != null && (responseCode.equalsIgnoreCase("CANCELREGISTRATION_SUCCESS")))
			{	
				logger.info("Stopping AERPJob for Cancel Registration " + responseCode);
				if(AERPSchedulerService.getInstance().isAERPJobTriggered())
				{
					AERPSchedulerService.getInstance().terminateAERPJob();
					logger.info("Cancel Register response " + responseCode);
				}
			}
			logger.info("Cancel registration Service, Success " + this.getClass().getName());
		}
		catch(Exception e)
		{
			logger.error("Cancel Register failed, Service Error " + this.getClass().getName() + " " + e.getMessage());
		}
		return responseCode;
	}

}
