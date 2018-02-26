/**
 * 
 */
package com.ca.arcflash.ui.server;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.ca.arcflash.ui.client.notifications.NotificationService;

/**
 * @author uppra02
 *
 */
@SuppressWarnings("serial")
public class NotificationServiceImpl extends BaseServiceImpl implements NotificationService 
{
	
	private static final Logger logger = Logger.getLogger(EntitlementRegisterServiceImpl.class);
			
	@Override
	public HashMap<String, String> getNotifications() {
		HashMap<String,String> notifications = new HashMap<String,String>();
		String aerpNotification = getAERPActivationNotification();
		if(!aerpNotification.equalsIgnoreCase("ISACTIVATED_ACTIVE"))
		{
			notifications.put("AERP", aerpNotification);	
			if(aerpNotification.equalsIgnoreCase("ISACTIVATED_INACTIVE")
					&& !aerpNotification.equalsIgnoreCase("USER_CANCELLED_REGISTRATION"))
			{
				String emailID = getRegisteredEmailID();
				if(emailID != null && !emailID.isEmpty())
				{
					notifications.put("emailID", emailID);	
				}
			}
		}
		return notifications;
	}
	
	private String getAERPActivationNotification() {
		String response = "ISACTIVATED_NOTREGISTERED";
		try
		{
			logger.info("Invoking isActivated service" + this.getClass().getName());
			response = this.getServiceClient().getService().isActivated();
		}
		catch(Exception e)
		{
			logger.error("Error calling isActivated " + e.getMessage());	
			response = "ISACTIVATED_EXCEPTION";
		}
		return response;
	}
	
	private String getRegisteredEmailID()
	{
		String emailID = "";
		try
		{
			HashMap<String, String> regDetails = new EntitlementRegisterUtility().getRegistrationDetails();
			if(regDetails != null)
			{
				emailID = regDetails.get("emailID");
			}
		}
		catch(Exception e)
		{
			logger.error("Exception getRegistrationDetails " + e.getMessage());
		}
		return emailID;
	}
}
