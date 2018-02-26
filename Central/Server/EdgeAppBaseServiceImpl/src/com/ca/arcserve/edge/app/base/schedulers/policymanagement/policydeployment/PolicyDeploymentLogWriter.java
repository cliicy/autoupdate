package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.LinuxD2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyUtil;

public class PolicyDeploymentLogWriter
{
	private static PolicyDeploymentLogWriter instance = null;
	private static Logger logger = Logger.getLogger( PolicyDeploymentLogWriter.class );
	private IActivityLogService activityLogService;
	private LogUtility logUtility;
	

	//////////////////////////////////////////////////////////////////////////

	protected PolicyDeploymentLogWriter()
	{
		this.activityLogService = new ActivityLogServiceImpl();
		this.logUtility = new LogUtility( logger );
	}

	//////////////////////////////////////////////////////////////////////////

	public static PolicyDeploymentLogWriter getInstance()
	{
		if (instance == null)
			instance = new PolicyDeploymentLogWriter();

		return instance;
	}

	//////////////////////////////////////////////////////////////////////////

	private String generateSummaryLogString(
		PolicyDeploymentTask task, boolean isSuccessful, String hostName )
	{
//		String hostName = getNodeName( task.getHostId() );
		EdgePolicy policy =
			PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );
		String taskDesc = getTaskDescription( task );
		String taskResult = EdgeCMWebServiceMessages.getResource(
			isSuccessful ? "policyDeployment_Succeed" : "policyDeployment_Failed" );

		return String.format(
			EdgeCMWebServiceMessages.getResource( "policyDeployment_DeployResultMessage" ),
			policy.getName(), hostName, taskDesc, taskResult );
	}

	//////////////////////////////////////////////////////////////////////////

	private String getTaskDescription( PolicyDeploymentTask task )
	{
		switch (task.getDeployReason())
		{
		case PolicyDeployReasons.PolicyAssigned:
			return EdgeCMWebServiceMessages.getResource( "policyDeployment_Operation_AssignPolicy" );

		case PolicyDeployReasons.PolicyUnassigned:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Operation_UnassignPolicy" );

		case PolicyDeployReasons.PolicyContentChanged:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Operation_UpgradePolicy" );

		case PolicyDeployReasons.ReDeployManually:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Operation_RedeployPolicy" );
		case PolicyDeployReasons.EnablePlan:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Operation_EnablePolicy" );
		case PolicyDeployReasons.DisablePlan:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Operation_DisablePolicy" );
		}

		this.logUtility.writeLog( LogUtility.LogTypes.Error,
			"getTaskDescription(): Unknown operation" );
		return EdgeCMWebServiceMessages.getResource(
			"policyDeployment_Operation_UnknownOperation" );
	}

	//////////////////////////////////////////////////////////////////////////

	public void addDeploymentLogs(
		PolicyDeploymentTask task, List<PolicyDeploymentError> errorList )
	{
		addDeploymentLogs(0, task, errorList );
	}
	
	public void addDeploymentLogs(
			long edgeTaskId, PolicyDeploymentTask task, List<PolicyDeploymentError> errorList )
		{
			try
			{
				for (PolicyDeploymentError error : errorList)
				{
					String logMessage = "";
					String nodeName = PolicyUtil.getNodeNameOfError( task, error );
					Severity severity = Severity.Information;

					switch (error.getErrorType())
					{
					case PolicyDeploymentError.ErrorTypes.Error:
						logMessage = String.format(
							EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedToApplySettings" ),
							getSettingsName( error.getSettingsType() ),
							nodeName );
						severity = Severity.Error;
						break;

					case PolicyDeploymentError.ErrorTypes.Warning:
						logMessage = String.format(
							EdgeCMWebServiceMessages.getResource( "policyDeployment_WarningInApplySettings" ),
							//getSettingsName( error.getSettingsType() ),
							nodeName );
						severity = Severity.Warning;
						break;
					}

					logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
						logMessage, getD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );

					writeActivityLog( edgeTaskId, severity, task.getHostId(), nodeName, logMessage );
				}
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}

	public void addDeploymentLogs4VM(PolicyDeploymentTask task, List<PolicyDeploymentError> errorList, String proxyHostname) {
		addDeploymentLogs4VM(0, task, errorList, proxyHostname);
	}
	
	public void addDeploymentLogs4VM(long edgeTaskId, PolicyDeploymentTask task, List<PolicyDeploymentError> errorList, String proxyHostname) {
		try
		{
			for (PolicyDeploymentError error : errorList)
			{
				String logMessage = "";
				String nodeName = PolicyUtil.getNodeNameOfError( task, error );
				Severity severity = Severity.Information;

				switch (error.getErrorType())
				{
				case PolicyDeploymentError.ErrorTypes.Error:
					logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_vSphere_FailedToApplySettings" ),
						getSettingsName( error.getSettingsType() ),
						proxyHostname );
					severity = Severity.Error;
					break;

				case PolicyDeploymentError.ErrorTypes.Warning:
					logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_vSphere_WarningInApplySettings" ),
						proxyHostname );
					severity = Severity.Warning;
					break;
				}
				String errorMsg = "";
				if(error.getErrorCode() != null && error.getErrorCode().equalsIgnoreCase(EdgeServiceErrorCode.PolicyManagement_Deploy_VMBackupJob_Running)){
					errorMsg = MessageReader.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							error.getErrorCode(), error.getErrorParameters());
				}else{
					errorMsg = getD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() );
				}
				logMessage = String.format(
					EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
					logMessage,  errorMsg);

				writeActivityLog( edgeTaskId, severity, task.getHostId(), nodeName, logMessage );
			}
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}

	public void addDeploymentLogs4LinuxD2D(long edgeTaskId, PolicyDeploymentTask task, List<PolicyDeploymentError> errorList, String linuxD2DServerName) {
		try
		{
			for (PolicyDeploymentError error : errorList)
			{
				String logMessage = "";
				String nodeName = error.getNodeName();
				Severity severity = Severity.Information;

				switch (error.getErrorType())
				{
				case PolicyDeploymentError.ErrorTypes.Error:
					logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_linux_FailedToApplySettings" ),
						getSettingsName( error.getSettingsType() ),
						linuxD2DServerName );
					severity = Severity.Error;
					break;

				case PolicyDeploymentError.ErrorTypes.Warning:
					logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_linux_WarningInApplySettings" ),
						//getSettingsName( error.getSettingsType() ),
						linuxD2DServerName );
					severity = Severity.Warning;
					break;
				}

				logMessage = String.format(
					EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
					logMessage, getLinuxD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );

				writeActivityLog( edgeTaskId, severity, task.getHostId(), nodeName, logMessage );
			}
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////

	public void addDeploymentSucceedLog( PolicyDeploymentTask task, String nodeName )
	{
		addDeploymentSucceedLog( 0, task, nodeName );
	}
	
	public void addDeploymentSucceedLog( long edgeTaskId, PolicyDeploymentTask task, String nodeName )
	{
		try
		{
			if (nodeName == null)
				nodeName = PolicyUtil.getNodeName( task.getHostId() );

			String logMessage = generateSummaryLogString( task, true, nodeName );

			writeActivityLog(edgeTaskId,
				Severity.Information, task.getHostId(), nodeName, logMessage );
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentSucceedLog(): Failed to write deployment succeed log." );
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void addDeploymentSucceedLog4VM(long edgeTaskId, PolicyDeploymentTask task, String nodeName) {
//		List<Integer> vmHostIdList = (List<Integer>) task.getTaskParameters();
//		int originalHostId = task.getHostId();
//		
//		for (Integer vmHostId : vmHostIdList) {
//			task.setHostId(vmHostId);
//			addDeploymentSucceedLog(edgeTaskId, task, nodeName);
//		}
//		
//		task.setHostId(originalHostId);
//	}

	//////////////////////////////////////////////////////////////////////////

	public void addDeploymentFailedLog( PolicyDeploymentTask task, String nodeName )
	{
		addDeploymentFailedLog( 0, task, nodeName );
	}
	
	public void addDeploymentFailedLog( long edgeTaskId, PolicyDeploymentTask task, String nodeName )
	{
		try
		{
			if (nodeName == null)
				nodeName = PolicyUtil.getNodeName( task.getHostId() );

			String logMessage = generateSummaryLogString( task, false, nodeName );

			writeActivityLog(edgeTaskId,
				Severity.Error, task.getHostId(), nodeName, logMessage );
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	public void addDeploymentUnassignLog(PolicyDeploymentTask task, String nodeName)
	{
		if (nodeName == null)
			nodeName = PolicyUtil.getNodeName( task.getHostId() );
		EdgePolicy policy =	PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );
		String gatewayHostName = EdgeCommonUtil.getGatewayHostNameByNodeId(task.getHostId());
		String msg = EdgeCMWebServiceMessages.getResource( "policyDeployment_UnassignFailed",gatewayHostName, nodeName , policy.getName());
		writeActivityLog(0, Severity.Warning, task.getHostId(), nodeName, msg );
	}
	
//	@SuppressWarnings("unchecked")
//	public void addDeploymentFailedLog4VM(long edgeTaskId, PolicyDeploymentTask task) {
//		List<Integer> vmHostIdList = (List<Integer>) task.getTaskParameters();
//		int originalHostId = task.getHostId();
//		
//		for (Integer vmHostId : vmHostIdList) {
//			task.setHostId(vmHostId);
//			addDeploymentFailedLog(edgeTaskId, task, null);
//		}
//		
//		task.setHostId(originalHostId);
//	}

	//////////////////////////////////////////////////////////////////////////

	public void addDeploymentFailedLog(
		PolicyDeploymentTask task, String nodeName, String failureReason )
	{
		addDeploymentFailedLog(0, task, nodeName, failureReason );
	}
	
	public void addDeploymentFailedLog(long edgeTaskId,
			PolicyDeploymentTask task, String nodeName, String failureReason )
		{
			try
			{
				if (nodeName == null)
					nodeName = PolicyUtil.getNodeName( task.getHostId() );

				String logMessage = generateSummaryLogString( task, false, nodeName );
				logMessage = String.format(
					EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedLogFormat" ),
					logMessage, failureReason );

				writeActivityLog(edgeTaskId,
					Severity.Error, task.getHostId(), nodeName, logMessage );
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}

	@SuppressWarnings("unchecked")
	public void addDeploymentFailedLog4VM(long edgeTaskId,
			PolicyDeploymentTask task, String failureReason )
	{
		List <Integer> vmIds = (List <Integer>)task.getTaskParameters();
		if(vmIds == null)
			return;
		for (Integer vm : vmIds) {
			
			try
			{
				String nodeName = PolicyUtil.getNodeName( vm );
				
				String logMessage = generateSummaryLogString( task, false, nodeName );
				logMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedLogFormat" ),
						logMessage, failureReason );
				
				writeActivityLog(edgeTaskId,
						Severity.Error, task.getHostId(), nodeName, logMessage );
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
						"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addDeploymentUnassignLog4VM(long edgeTaskId, PolicyDeploymentTask task)
	{
		List <Integer> vmIds = (List <Integer>)task.getTaskParameters();
		for (Integer vm : vmIds) {
			
			try
			{
				String nodeName = PolicyUtil.getNodeName( vm );

				EdgePolicy policy =	PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );
				String gatewayHostName = EdgeCommonUtil.getGatewayHostNameByNodeId(vm);
				String msg = EdgeCMWebServiceMessages.getResource( "policyDeployment_UnassignFailed", gatewayHostName,nodeName, policy.getName());
				writeActivityLog(0, Severity.Warning, vm, nodeName, msg );
				
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public void writeActivityLog(
		Severity severity, int hostId, String nodeName, String message )
	{
		writeActivityLog(0, severity, hostId, nodeName, message );
	}
	
	public void writeActivityLog(
			long edgeTaskId, Severity severity, int hostId, String nodeName, String message )
		{
			try
			{
				ActivityLog log = new ActivityLog();
				log.setJobId(edgeTaskId);
				log.setModule( Module.PolicyManagement );
				log.setSeverity( severity );
				log.setHostId(hostId);
				log.setNodeName( nodeName );
				log.setMessage( message );
				log.setTime( new Date() );
				this.activityLogService.addLog( log );
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"writeActivityLog(): Error writting activity log. (Node name: '%s', Message: '%s')",
					nodeName, message );
			}
		}

	//////////////////////////////////////////////////////////////////////////

	private String getSettingsName( int settingsType )
	{
		switch (settingsType)
		{
		case SettingsTypes.WholePolicy:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_WholePolicy" );
			
		case SettingsTypes.BackupSettings:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_BackupSettings" );

		case SettingsTypes.Archiving:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_ArchiveSettings" );
			
		case SettingsTypes.ArchiveToTapeSettings:
			return EdgeCMWebServiceMessages.getResource(
					"policyDeployment_Settings_ArchiveToTapeSettings" );
			
		case SettingsTypes.ArchiveFileSettings:
			return EdgeCMWebServiceMessages.getResource(
					"policyDeployment_Settings_ArchiveFileSettings" );

		case SettingsTypes.ScheduledExportSettings:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_ScheduledExportSettings" );

		case SettingsTypes.Preferences:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_Preferences" );

		case SettingsTypes.VCMSettings:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_VCMSettings" );

		case SettingsTypes.VMBackupSettings:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_VMBackupSettings" );
		case SettingsTypes.LicenseFailSettings:
			return EdgeCMWebServiceMessages.getResource(
				"policyDeployment_Settings_LicenseSettings" );
		}

		this.logUtility.writeLog( LogUtility.LogTypes.Error,
			"getSettingsName(): Unknown settings type." );
		return EdgeCMWebServiceMessages.getResource(
			"policyDeployment_Settings_UnknownSettings" );
	}

	//////////////////////////////////////////////////////////////////////////
	
	private String getResource( String errorCode, Object[] errorParameters) {
		String errorMessage = "";
		
		if (errorParameters != null && errorParameters.length > 0) {
			for(int i=0;i<errorParameters.length;i++){
				if(errorParameters[i]==null)
					errorParameters[i]="";
			}
			errorMessage =D2DWebServiceErrorMessages.getMessage(errorCode, errorParameters);
		}
		else {
			errorMessage = D2DWebServiceErrorMessages.getMessage(errorCode);
		}
		return errorMessage;
	}
	
	private String getLinuxResource( String errorCode, Object[] errorParameters) {
		String errorMessage = "";
		
		if (errorParameters != null && errorParameters.length > 0) {
			for(int i=0;i<errorParameters.length;i++){
				if(errorParameters[i]==null)
					errorParameters[i]="";
			}
			errorMessage = LinuxD2DWebServiceErrorMessages.getMessage(errorCode, errorParameters);
		}
		else {
			errorMessage = LinuxD2DWebServiceErrorMessages.getMessage(errorCode);
		}
		return errorMessage;
	}
	
	//////////////////////////////////////////////////////////////////////////

	private String getD2DErrorMessage( String errorCode, Object[] errorParameters )
	{
		try
		{
			return getResource(
					errorCode, errorParameters);
		}
		catch (MissingResourceException e)
		{
			// other exceptions will terminate the function, and
			// MissingResourceException will go through down
		}

		// find in general errors

		String errorMessage = getGeneralErrorMessage( errorCode );
		if (errorMessage != null)
			return errorMessage;

		// still not found

		return String.format(
			EdgeCMWebServiceMessages.getResource( "policyDeployment_UnknownErrorCode" ),
			errorCode );
	}

	
	private String getLinuxD2DErrorMessage( String errorCode, Object[] errorParameters )
	{
		try
		{
			return getLinuxResource(
					errorCode, errorParameters);
		}
		catch (MissingResourceException e)
		{
			// other exceptions will terminate the function, and
			// MissingResourceException will go through down
		}

		// find in general errors

		String errorMessage = getGeneralErrorMessage( errorCode );
		if (errorMessage != null)
			return errorMessage;

		// still not found

		return String.format(
			EdgeCMWebServiceMessages.getResource( "policyDeployment_UnknownErrorCode" ),
			errorCode );
	}
	//////////////////////////////////////////////////////////////////////////

	private String getGeneralErrorMessage( String errorCode )
	{
		if (errorCode.equals( ID2DPolicyManagementService.GenericErrors.InternalError ) ||
			errorCode.equals( ID2DPolicyManagementService.GenericErrors.InvalidParameter ) ||
			errorCode.equals( ID2DPolicyManagementService.GenericErrors.UnknownPolicyType ) ||
			errorCode.equals( ID2DPolicyManagementService.GenericErrors.InvalidPolicyXml ))
		{
			return String.format(
				EdgeCMWebServiceMessages.getResource( "policyDeployment_GeneralErrorMessage" ),
				errorCode );
		}

		return null;
	}
}
