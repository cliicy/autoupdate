package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.LinuxD2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.alert.AlertManager;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyUtil;

public class PolicyDeployUIWarningWriter {
	
	private static Logger logger = Logger.getLogger( PolicyDeployUIWarningWriter.class );
	private LogUtility logUtility;
	private IEdgePolicyDao edgePolicyDao;
	
	public PolicyDeployUIWarningWriter()
	{
		this.edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
		this.logUtility = new LogUtility( logger );
	}
	
	public void addWarningErrorMessageFromD2D(	PolicyDeploymentTask task, List<PolicyDeploymentError> errorList, boolean removePolicyFlag )
	{
		try
		{
			StringBuilder emailContent = new StringBuilder();
			
			edgePolicyDao.deletePolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType());
			StringBuffer warningMessage = new StringBuffer("");
			StringBuffer errorMessage = new StringBuffer("");
			if(errorList != null){
				for (PolicyDeploymentError error : errorList)
				{
					String nodeName = PolicyUtil.getNodeNameOfError( task, error );
					switch (error.getErrorType())
					{
						case PolicyDeploymentError.ErrorTypes.Error:
							errorMessage.append(String.format(
								EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedToApplySettings" ),
								getSettingsName( error.getSettingsType() ),
								nodeName ));
							String errorMessageStr = String.format(
									EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
									errorMessage.toString(), getD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );
							errorMessage = new StringBuffer(errorMessageStr);
							
							emailContent.append(errorMessage + "\r\n");
							break;
							
						case PolicyDeploymentError.ErrorTypes.Warning:
							if(warningMessage.length()>1)
								warningMessage.append("<br>");
							warningMessage.append(String.format(
								EdgeCMWebServiceMessages.getResource( "policyDeployment_WarningInApplySettings" ),
								//getSettingsName( error.getSettingsType() ),
								nodeName ));
							String warningMessageStr = String.format(
									EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
									warningMessage.toString(), getD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );
							warningMessage = new StringBuffer(warningMessageStr);
							break;
					}
				}
			}
			if(!(removePolicyFlag && (errorList == null || getErrorCount(errorList) == 0)))
				edgePolicyDao.setPolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType(), warningMessage.toString(), errorMessage.toString());

			if (errorMessage.length() > 0)
			{
				AlertManager.getInstance().saveAlertToDB( EdgeEmailService.GetInstance().getHostName().toLowerCase(), 
						PolicyUtil.getNodeName(task.getHostId()), -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(),  errorMessage.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );		
			}
			
			if (emailContent.length() > 0) {
				sendFailedDeployEmail(emailContent.toString());
			}
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	public void addWarningErrorMessageFromLinuxD2D(	PolicyDeploymentTask task, List<PolicyDeploymentError> errorList, boolean removePolicyFlag,String d2dServerName )
	{
		try
		{
			StringBuilder emailContent = new StringBuilder();
			
			edgePolicyDao.deletePolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType());
			StringBuffer warningMessage = new StringBuffer("");
			StringBuffer errorMessage = new StringBuffer("");
			if(errorList != null)
				for (PolicyDeploymentError error : errorList)
				{
					switch (error.getErrorType())
					{
						case PolicyDeploymentError.ErrorTypes.Error:
							errorMessage.append(String.format(
								EdgeCMWebServiceMessages.getResource( "policyDeployment_linux_FailedToApplySettings" ),
								getSettingsName( error.getSettingsType() ),
								d2dServerName ));
							String errorMessageStr = String.format(
									EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
									errorMessage.toString(), getLinuxD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );
							errorMessage = new StringBuffer(errorMessageStr);
							
							emailContent.append(errorMessage + "\r\n");
							break;
							
						case PolicyDeploymentError.ErrorTypes.Warning:
							warningMessage.append(String.format(
								EdgeCMWebServiceMessages.getResource( "policyDeployment_linux_WarningInApplySettings" ),
								//getSettingsName( error.getSettingsType() ),
								d2dServerName ));
							String warningMessageStr = String.format(
									EdgeCMWebServiceMessages.getResource( "policyDeployment_ErrorMessageFormat" ),
									warningMessage.toString(), getLinuxD2DErrorMessage( error.getErrorCode(), error.getErrorParameters() ) );
							warningMessage = new StringBuffer(warningMessageStr);
							warningMessage.append("<br>");
							break;
					}
				}
			if(!(removePolicyFlag && (errorList == null || getErrorCount(errorList) == 0)))
				edgePolicyDao.setPolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType(), warningMessage.toString(), errorMessage.toString());

			if (errorMessage.length() > 0)
			{				
				AlertManager.getInstance().saveAlertToDB(
						EdgeEmailService.GetInstance().getHostName().toLowerCase(), PolicyUtil.getNodeName(task.getHostId()), -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(),  errorMessage.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );		
			}
			
			if (emailContent.length() > 0) {
				sendFailedDeployEmail(emailContent.toString());
			}
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
//	private String getWarningErrorMessage4VM(PolicyDeploymentError deploymentError, String proxyHostname) {
//		String message = "";
//		String settingName = getSettingsName(deploymentError.getSettingsType());
//		
//		String messageWithDetailsFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_ErrorMessageFormat");
//		String d2dErrorMessage = getD2DErrorMessage(deploymentError.getErrorCode(), deploymentError.getErrorParameters());
//		
//		switch (deploymentError.getErrorType()) {
//		case PolicyDeploymentError.ErrorTypes.Error:
//			String errorMessageFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_vSphere_FailedToApplySettings");
//			message = String.format(errorMessageFormat, settingName, proxyHostname);
//			message = String.format(messageWithDetailsFormat, message, d2dErrorMessage);
//			break;
//		case PolicyDeploymentError.ErrorTypes.Warning:
//			String warningMessageFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_vSphere_WarningInApplySettings");
//			message = String.format(warningMessageFormat, settingName, proxyHostname);
//			message = String.format(messageWithDetailsFormat, message, d2dErrorMessage);
//			break;
//		default:
//			break;
//		}
//		
//		return message;
//	}
	
	public void addWarningErrorMessageFromD2D4VM(List<Integer> vmIdList, List<PolicyDeploymentError> errorList, String proxyHostname, boolean removePolicyFlag) {
		logger.info( "addWarningErrorMessageFromD2D4VM(): Begin to add warning/error messages." );
		try {
			if (vmIdList != null) {
				logger.info("addWarningErrorMessageFromD2D4VM(): Delete the deployment warning/error message for VM ID in " + vmIdList.toString());
				for (Integer vmId : vmIdList) {
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmId, getPolicyTypeByApplicationType());
				}
			}
			
			if (errorList == null) {
				return;
			}
			logger.info( "addWarningErrorMessageFromD2D4VM(): Error list size: " + errorList.size() );
			
			//sort the errorList by VM instance UUID
			Collections.sort(errorList, new Comparator<PolicyDeploymentError>() {
				@Override
				public int compare(PolicyDeploymentError o1, PolicyDeploymentError o2) {
					String instanceUUID1 = o1.getVmInstanceUuid();
					String instanceUUID2 = o2.getVmInstanceUuid();
					if (instanceUUID1 == null && instanceUUID2 == null) {
						return 0;
					}
					if (instanceUUID1 == null) {
						return -1;
					}
					if (instanceUUID2 == null) {
						return 1;
					}
					
					return instanceUUID1.hashCode() - instanceUUID2.hashCode();
				}
			});
			
			boolean sendEmail = true;
			String warningTmpStr = "";
			String errorTmpStr = "";
			for (int i = 0 ; i < errorList.size() ; i ++ ) {
				PolicyDeploymentError deploymentError  = errorList.get(i);
				if (removePolicyFlag && deploymentError.getErrorType() != PolicyDeploymentError.ErrorTypes.Error) {
					continue;
				}
				if (deploymentError.getVmInstanceUuid() == null) {
					continue;
				}
				
				int[] vmHostId = new int[1];
				IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
				esxDao.as_edge_host_getHostByInstanceUUID(0, deploymentError.getVmInstanceUuid(), vmHostId);	//TODO: find gateway
				if (vmHostId[0] <= 0) {
					IEdgeHyperVDao edgeHypervDao= DaoFactory.getDao(IEdgeHyperVDao.class);
					edgeHypervDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, deploymentError.getVmInstanceUuid(), vmHostId);	//TODO: find gateway
				}
				
				if (vmHostId[0] <= 0) {
					continue;
				}
				
				logger.info("Prepare warning/error message for VM [hostId=" + vmHostId[0] + "]");
				String message = "";
				if(deploymentError.getErrorCode() != null && deploymentError.getErrorCode().equalsIgnoreCase(EdgeServiceErrorCode.PolicyManagement_Deploy_VMBackupJob_Running)){
					message = MessageReader.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							deploymentError.getErrorCode(), deploymentError.getErrorParameters());
				}else{
					message = getD2DErrorMessage(deploymentError.getErrorCode(), deploymentError.getErrorParameters());
				}
				if(deploymentError.getErrorType() == PolicyDeploymentError.ErrorTypes.Warning){
					if(!warningTmpStr.isEmpty())
						warningTmpStr += " " + message;
					else
						warningTmpStr = message;
				}else if(deploymentError.getErrorType() == PolicyDeploymentError.ErrorTypes.Error){
					if(!errorTmpStr.isEmpty())
						errorTmpStr += " " + message;
					else
						errorTmpStr = message;
				}
				
				// Check if next VM is the same VM. if yes we will append warning/error messages in next loop.
				if(i+1 < errorList.size()){
					if(deploymentError.getVmInstanceUuid().equals(errorList.get(i+1).getVmInstanceUuid())){
						continue;
					}
				}
				
				// Format warning/error message
				String settingName = getSettingsName(deploymentError.getSettingsType());
				String messageWithDetailsFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_ErrorMessageFormat");
				String errorMessage = "";
				if(!errorTmpStr.isEmpty()){
					String errorMessageFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_vSphere_FailedToApplySettings");
					String errorInfo = String.format(errorMessageFormat, settingName, proxyHostname);
					errorMessage = String.format(messageWithDetailsFormat, errorInfo, errorTmpStr);					
				}

				String warningMessage = "";
				if(!warningTmpStr.isEmpty()){
					String warningMessageFormat = EdgeCMWebServiceMessages.getResource("policyDeployment_vSphere_WarningInApplySettings");
					String warningInfo = String.format(warningMessageFormat, proxyHostname);
					warningMessage = String.format(messageWithDetailsFormat, warningInfo, warningTmpStr);					
				}

				// Save message to DB
				logger.info("Update the deployment warning/error message for VM [hostId=" + vmHostId[0] + "]");
				edgePolicyDao.setPolicyDeployWarningErrorMessage(vmHostId[0], getPolicyTypeByApplicationType(), warningMessage, errorMessage);
				
				if (!errorMessage.isEmpty()) {
					AlertManager.getInstance().saveAlertToDB( 
							EdgeEmailService.GetInstance().getHostName().toLowerCase(), proxyHostname, -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(), errorMessage, Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
					
					if (sendEmail && !sendFailedDeployEmailWithHost(vmHostId[0], errorMessage)) {
						sendEmail = false;
					}
				}
				
				warningTmpStr = "";
				errorTmpStr = "";
			}
		} catch (Exception e) {
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"addWarningErrorMessageFromD2D4VM(): Failed to add warning/error message for VMs." );
		}
		
		logger.info( "addWarningErrorMessageFromD2D4VM(): Adding warning/error messages finished." );
	}
	
	public void addUnassignMessage(
			PolicyDeploymentTask task, String nodeName ){
		try
		{
			edgePolicyDao.deletePolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType());
			if (nodeName == null)
				nodeName = PolicyUtil.getNodeName( task.getHostId() );
			
			EdgePolicy policy =	PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );
			String gatewayHostName=EdgeCommonUtil.getGatewayHostNameByNodeId(task.getHostId());
			String msg = EdgeCMWebServiceMessages.getResource( "policyDeployment_UnassignFailed", gatewayHostName,nodeName ,policy.getName());
			
			edgePolicyDao.setPolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType(), msg, "");
			
			AlertManager.getInstance().saveAlertToDB( EdgeEmailService.GetInstance().getHostName().toLowerCase(), 
					nodeName, -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(), msg.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
			
			
			sendFailedDeployEmail(msg);
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	/**
	 * just for the email content is the log (like, policyDeployment_TheNodeIsManagedByOtherEdge_log)
	 * , not the warning message.
	 * @param task
	 * @param nodeName
	 * @param failureReason
	 * @param emailContent
	 */
	public void addErrorMessage(
		PolicyDeploymentTask task, String nodeName, String failureReason, String emailContent )
	{
		try
		{
			String errorMessage = addWarningMessage(task,nodeName,failureReason);
			if (!StringUtil.isEmptyOrNull(emailContent)) {
				errorMessage = emailContent;
			}
			sendFailedDeployEmail(errorMessage);
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	public void addErrorMessage(
			PolicyDeploymentTask task, String nodeName, String failureReason )
		{
			try
			{
				sendFailedDeployEmail(addWarningMessage(task,nodeName,failureReason));
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}
	
	private String addWarningMessage(PolicyDeploymentTask task, String nodeName, String failureReason) {
			if (nodeName == null)
				nodeName = PolicyUtil.getNodeName( task.getHostId() );
			
			String errorMessage = generateSummaryLogString( task, false, nodeName );
			
			errorMessage = String.format(
				EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedLogFormat" ),
				errorMessage, failureReason );
			edgePolicyDao.deletePolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType());
			edgePolicyDao.setPolicyDeployWarningErrorMessage(task.getHostId(), getPolicyTypeByApplicationType(), "", errorMessage);
			
			AlertManager.getInstance().saveAlertToDB(
					EdgeEmailService.GetInstance().getHostName().toLowerCase(), nodeName, -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(),  errorMessage.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );		
			
			return errorMessage;
	}
	
	@SuppressWarnings("unchecked")
	public void addErrorMessage4VM(
		PolicyDeploymentTask task, String proxyName, String failureReason )
	{
		try
		{
			List <Integer> vmIds = (List <Integer>)task.getTaskParameters();
			if(vmIds == null)
				return;
			boolean sendEmail = true;
			
			for(Integer vmid : vmIds)
			{
				String nodeName = PolicyUtil.getNodeName(vmid);
				String errorMessage = generateSummaryLogString( task, false, nodeName );
				
				errorMessage = String.format(
					EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedLogFormat" ),
					errorMessage, failureReason );
				
				edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType());
				edgePolicyDao.setPolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType(), "", errorMessage);
				AlertManager.getInstance().saveAlertToDB( EdgeEmailService.GetInstance().getHostName().toLowerCase(), proxyName, -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(), errorMessage.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
				
				
				
				if (sendEmail && !sendFailedDeployEmailWithHost(vmid.intValue(), errorMessage)) {
					sendEmail = false;
				}
			}			
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addErrorMessage4VM(
			PolicyDeploymentTask task, String proxyName, String failureReason,  String emailContent)
		{
			try
			{
				List <Integer> vmIds = (List <Integer>)task.getTaskParameters();
				boolean sendEmail = true;
				
				for(Integer vmid : vmIds)
				{
					String errorMessage = generateSummaryLogString( task, false, PolicyUtil.getNodeName(vmid) );
					
					errorMessage = String.format(
						EdgeCMWebServiceMessages.getResource( "policyDeployment_FailedLogFormat" ),
						errorMessage, failureReason );
					
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType());
					edgePolicyDao.setPolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType(), "", errorMessage);
					AlertManager.getInstance().saveAlertToDB( EdgeEmailService.GetInstance().getHostName().toLowerCase(), proxyName, -1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(), errorMessage.toString(), Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
					
					if (!StringUtil.isEmptyOrNull(emailContent)) {						
						errorMessage = emailContent;
					}
					
					if (sendEmail && !sendFailedDeployEmailWithHost(vmid.intValue(), errorMessage)) {
						sendEmail = false;
					}
				}
			}
			catch (Exception e)
			{
				this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"addDeploymentFailedLog(): Failed to write deployment failed log." );
			}
		}

	@SuppressWarnings("unchecked")
	public void addUnassignMessage4VM(
			PolicyDeploymentTask task, String proxyName )
	{
		try
		{
			List <Integer> vmIds = (List <Integer>)task.getTaskParameters();
			EdgePolicy policy =	PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );	
			boolean sendEmail = true;
			
			for(Integer vmid : vmIds)
			{
				String nodeName = PolicyUtil.getNodeName( vmid );
				String gatewayHostName = EdgeCommonUtil.getGatewayHostNameByNodeId(vmid);
				String msg = EdgeCMWebServiceMessages.getResource( "policyDeployment_UnassignFailed",gatewayHostName,nodeName, policy.getName());
				
				edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType());
				edgePolicyDao.setPolicyDeployWarningErrorMessage(vmid, getPolicyTypeByApplicationType(), msg, "");
				
				AlertManager.getInstance().saveAlertToDB( EdgeEmailService.GetInstance().getHostName().toLowerCase(), proxyName, 
						-1L, CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue(), getPolicyDeploymentAlertSubject(), msg, Calendar.getInstance().getTime(), CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
				if (sendEmail && !sendFailedDeployEmailWithHost(vmid.intValue(), msg)) {
					sendEmail = false;
				}
			}			
		}
		catch (Exception e)
		{
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
			"addDeploymentFailedLog(): Failed to write deployment failed log." );
		}
	}
	
	private String generateSummaryLogString(
		PolicyDeploymentTask task, boolean isSuccessful,String hostName )
	{
		EdgePolicy policy =
			PolicyContentCache.getInstance().getPolicyContent( task.getPolicyId() );
		String taskDesc = getTaskDescription( task );
		String taskResult = EdgeCMWebServiceMessages.getResource(
			isSuccessful ? "policyDeployment_Succeed" : "policyDeployment_Failed" );
		
		if(hostName == null || hostName.isEmpty())
			hostName=EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownNode");
		else
			hostName="'"+hostName+"'";
		
		return String.format(
			EdgeCMWebServiceMessages.getResource( "policyDeployment_DeployResultMessage" ),
			policy.getName(), hostName, taskDesc, taskResult );
	}
	
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
	
	private int getPolicyTypeByApplicationType()
	{
		return PolicyManagementServiceImpl.getPolicyTypeByApplicationType();
	}
	
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
	
	private String getD2DErrorMessage( String errorCode, Object[] errorParameters )
	{
		try
		{
			String message = getResource(
					errorCode, errorParameters);
			
			StringBuilder sb = new StringBuilder();
			if (message == null || message.isEmpty()) {
				if (errorParameters != null && errorParameters.length > 0) {
					for (Object para : errorParameters) {
						if (para instanceof String) {
							sb.append(para + " ");
						}
					}
					
					if (sb.length() > 0) {
						message = sb.toString().substring(0, sb.length());
					}
				}
			}
			
			return message;
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
	
	private int getErrorCount( List<PolicyDeploymentError> errorList )
	{
		int count = 0;
		for (PolicyDeploymentError error : errorList)
		{
			if (error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error)
				count ++;
		}
		return count;
	}	
	
	private void sendFailedDeployEmail(String content) {
		sendFailedDeployEmailWithHost(-1, content);
	}
	
	private boolean sendFailedDeployEmailWithHost(int hostid, String content) {
		EdgeEmailService emailSrv = EdgeEmailService.GetInstance();
		EdgeApplicationType edgeAppType = EdgeWebServiceContext.getApplicationType();
		int emailTemplateId = EmailTemplateFeature.D2DPolicy;
		switch (edgeAppType) {
		case CentralManagement:
			emailTemplateId = EmailTemplateFeature.D2DPolicy;
			break;

		case VirtualConversionManager:
			emailTemplateId = EmailTemplateFeature.VCMPolicy;
			break;

		case vShpereManager:
			emailTemplateId = EmailTemplateFeature.VSpherePolicy;
			break;

		case Report:
			emailTemplateId = EmailTemplateFeature.Report;
			break;
		}

		String hostName = PolicyUtil.getNodeName(hostid);
		String subject = getPolicyDeploymentAlertSubject();
		if (hostid == -1) {
			return emailSrv.SendMailWithGlobalSetting(emailSrv.getHostName(),
					subject, content, emailTemplateId);			
		} else {			
			subject += " - " + hostName;
			return emailSrv.SendMailWithGlobalSetting(emailSrv.getHostName(),
					subject, content, emailTemplateId);
		}

	}
	
	private String getPolicyDeploymentAlertSubject() {
		EdgeApplicationType edgeAppType = EdgeWebServiceContext.getApplicationType();
		int emailTemplateId = EmailTemplateFeature.D2DPolicy;
		switch (edgeAppType) {
		case CentralManagement:
			emailTemplateId = EmailTemplateFeature.D2DPolicy;
			break;

		case VirtualConversionManager:
			emailTemplateId = EmailTemplateFeature.VCMPolicy;
			break;

		case vShpereManager:
			emailTemplateId = EmailTemplateFeature.VSpherePolicy;
			break;

		case Report:
			emailTemplateId = EmailTemplateFeature.Report;
			break;
		}	
		
		
		ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
		String policyMailSubject = null;
		try {
			EmailTemplateSetting emailTemplate = configurationServiceImpl.getEmailTemplateSetting(emailTemplateId);
			if (emailTemplate==null) {
				policyMailSubject =  EdgeCMWebServiceMessages.getResource("EDGEMAIL_ALERT_SUBJECT");
			} else {
				policyMailSubject =  emailTemplate.getSubject();
			}
			policyMailSubject = policyMailSubject + ": " + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_PLANEMAIL_SUBJECT");
		} catch (EdgeServiceFault e) {
			this.logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"getPolicyDeploymentAlertSubject()." );
		}
		
		return policyMailSubject;
	}
	
}
