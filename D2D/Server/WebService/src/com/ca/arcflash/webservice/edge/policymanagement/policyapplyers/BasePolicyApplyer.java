package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.ca.arcflash.webservice.edge.policymanagement.IPolicyApplyer;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyUsageMarker;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyXmlObject;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.ScheduledExportConfigXMLParser;

public abstract class BasePolicyApplyer implements IPolicyApplyer
{
	/**
	 * if policy model is changed, we can change this version, in order to force make the policy uuid different.
	 */
	public static final String POLICYVERSION = "1";
	
	private static final Logger logger = Logger.getLogger( BasePolicyApplyer.class );
	protected static final LogUtility logUtility = new LogUtility( logger );
	
	protected PolicyXmlObject policyXmlObject;
	protected List<PolicyDeploymentError> errorList;
	protected PolicyUsageMarker policyUsageMarker;
	protected String policyParameter;
	protected String policyUuid;
	protected String planId;
	
	protected static final int RETRY_TIMES = 20;
	protected static final int RETRY_INTERVAL = 500;

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void applyPolicy(
		List<PolicyDeploymentError> errorList, String policyUuid,
		String policyXml ,String parameter)
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyPolicy() enter." );
			
			////////////////////////////////////////
			// initialize common variables
		
			try
			{
				this.errorList = errorList;
				this.policyXmlObject = new PolicyXmlObject();
				this.policyXmlObject.setPolicyXmlString( policyXml );
				this.policyUsageMarker = PolicyUsageMarker.getInstance();
				this.policyParameter = parameter;
				this.policyUuid= policyUuid +":"+POLICYVERSION;
				this.planId=policyUuid;
			}
			catch (PolicyXmlObject.InvalidXmlException e)
			{
				logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"Translating policy XML failed." );
		
				addGeneralError( null,
					ID2DPolicyManagementService.GenericErrors.InvalidPolicyXml );
				return;
			}
			catch (Exception e)
			{
				logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"Initialize common variables failed." );
	
				addGeneralError( null,
					ID2DPolicyManagementService.GenericErrors.InternalError );
				return;
			}
			
			////////////////////////////////////////
			// call doApplying()
			
			logUtility.writeLog( LogUtility.LogTypes.Info,
				"applyPolicy(): before call doApplying()." );
			
			this.doApplying();
			
			logUtility.writeLog( LogUtility.LogTypes.Info,
				"applyPolicy(): doApplying() returned." );
			
			////////////////////////////////////////
			// set policy usage flag
			
//			if (this.errorList.size() == 0) // no errors
//			{
//				this.policyUsageMarker.setUsePolicy(
//					this.getResponsiblePolicyType(), true );
//			}
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyPolicy() error." );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyPolicy() exit." );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void unapplyPolicy(
		List<PolicyDeploymentError> errorList, boolean keepCurrentSettings , String parameter )
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyPolicy() enter." );
	
			////////////////////////////////////////
			// initialize common variables
		
			try
			{
				this.errorList = errorList;
				this.policyUsageMarker = PolicyUsageMarker.getInstance();
				this.policyParameter = parameter;
			}
			catch (Exception e)
			{
				logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"Initialize common variables failed." );
	
				addGeneralError( null,
					ID2DPolicyManagementService.GenericErrors.InternalError );
				return;
			}
			
			////////////////////////////////////////
			// call doUnApplying()
			
			if (!keepCurrentSettings)
				doUnApplying();
			
			removePolicyRecord();
			
			////////////////////////////////////////
			// set policy usage flag
			
//			if (this.errorList.size() == 0) // no errors
//			{
//				this.policyUsageMarker.setUsePolicy(
//					this.getResponsiblePolicyType(), false );
//			}
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyPolicy() error." );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyPolicy() exit." );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////

	private boolean isErrorForNode( PolicyDeploymentError error, String nodeName )
	{
		try
		{
			if (error == null)
				throw new IllegalArgumentException();
			
			if ((nodeName == null) && (error.getNodeName() == null))
				return true;
			
			if ((nodeName == null) || (error.getNodeName() == null))
				return false;
			
			return error.getNodeName().equalsIgnoreCase( nodeName );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"isErrorForNode() error." );
			
			return false;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////

	private void addError(
		List<PolicyDeploymentError> errorList, String nodeName,
		int policyType, int settingsType, int errorType, String errorCode, Object[] errorParameters )
	{
		// skip the duplicate error code of same node
		for (PolicyDeploymentError pde : errorList) {
			if (isErrorForNode( pde, nodeName ) &&
				pde.getPolicyType() == policyType &&
				pde.getErrorCode().equalsIgnoreCase(errorCode)){
				return;
			}
		}
		
		PolicyDeploymentError error = new PolicyDeploymentError();
		error.setNodeName( nodeName );
		error.setPolicyType( policyType );
		error.setSettingsType( settingsType );
		error.setErrorType( errorType );
		error.setErrorCode( errorCode );
		error.setErrorParameters(errorParameters);
		errorList.add( error );
	}

	//////////////////////////////////////////////////////////////////////////

	protected void addGeneralError( String nodeName, String errorCode )
	{
		addError( this.errorList, nodeName,
			this.getResponsiblePolicyType(), SettingsTypes.WholePolicy,
			PolicyDeploymentError.ErrorTypes.Error, errorCode, null );
	}

	//////////////////////////////////////////////////////////////////////////

	protected void addError( String nodeName, int settingsType, String errorCode, Object[] errorParameters )
	{
		addError( this.errorList, nodeName,
			this.getResponsiblePolicyType(), settingsType,
			PolicyDeploymentError.ErrorTypes.Error, errorCode, errorParameters );
	}

	//////////////////////////////////////////////////////////////////////////

	protected void addWarning( String nodeName, int settingsType, String errorCode, Object[] errorParameters )
	{
		addError( this.errorList, nodeName,
			this.getResponsiblePolicyType(), settingsType,
			PolicyDeploymentError.ErrorTypes.Warning, errorCode, errorParameters );
	}
	
	protected boolean hasError() {
		for (PolicyDeploymentError error : errorList) {
			if (error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error)
				return true;
		}
		
		return false;
	}

	//////////////////////////////////////////////////////////////////////////
	// Abstract functions
	
	protected abstract int getResponsiblePolicyType();
	protected abstract void doApplying();
	protected abstract void doUnApplying();
	protected abstract void removePolicyRecord();
	
	protected ScheduledExportConfiguration getScheduledExportSettings() throws Exception {
		
		Document settingsDocument = this.policyXmlObject
				.getSettingsSection(PolicyXmlObject.PolicyXmlSectionNames.ScheduledExportSettings);
	
		if (settingsDocument == null)
			return null;
	
		ScheduledExportConfigXMLParser scheduledExportConfigXMLParser = new ScheduledExportConfigXMLParser();
		ScheduledExportConfiguration sefConfiguration = scheduledExportConfigXMLParser
				.loadXML(settingsDocument);
	
		return sefConfiguration;
	}

	protected boolean tryDeleteFile(File fileToDelete) throws Exception {
		boolean result=CommonUtil.tryDeleteFile(fileToDelete);
		if(result) return result;
		else
		throw new ServiceException(FlashServiceErrorCode.Common_Delete_Configuration_Fail,
				new Object[] {fileToDelete.getName()});
	}
}
