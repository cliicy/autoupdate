package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.quartz.SchedulerException;

import com.ca.arcflash.assurerecovery.AssureRecoveryJobScript;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.service.InstantVMService;
import com.ca.arcflash.webservice.service.ServiceException;

public class AssureRecoveryPolicyApplyer extends BasePolicyApplyer {
	
	static final JAXBContext arPolicyContext = initContext();
	
	private static JAXBContext initContext() {
		try {
			return JAXBContext.newInstance(AssureRecoveryJobScript.class);
		} catch (JAXBException e) {
			logUtility.writeLog( LogUtility.LogTypes.Error, 
					"Fail to create JAXB instance for AssureRecoveryJobScript");
		}
		return null;
	}
	
	@Override
	protected int getResponsiblePolicyType()
	{
		return ID2DPolicyManagementService.PolicyTypes.AssureRecovery;
	}
	
	@Override
	protected void doApplying() {
		AssureRecoveryJobScript jobScript = null;
		try {
			jobScript = (AssureRecoveryJobScript) arPolicyContext.createUnmarshaller()
					.unmarshal(new StringReader(this.policyXmlObject.getPolicyXmlString()));
		} catch (JAXBException e) {
			logUtility.writeLog(LogUtility.LogTypes.Error,
					"Fail to unmarshall AssureRecoveryJobScript");
		}
		if (jobScript != null) {
			try {
				InstantVMService.getInstance().applyPolicy(jobScript);
			} catch (ServiceException e) {
				this.addError( null,
						ID2DPolicyManagementService.SettingsTypes.ARSettings,
						e.getErrorCode(), e.getMultipleArguments() );
			} catch (SchedulerException e) {
				// TODO
			}
		}
	}
	
	@Override
	protected void doUnApplying()
	{
	}

	@Override
	protected void removePolicyRecord() {
		// TODO Auto-generated method stub
		
	}

}
