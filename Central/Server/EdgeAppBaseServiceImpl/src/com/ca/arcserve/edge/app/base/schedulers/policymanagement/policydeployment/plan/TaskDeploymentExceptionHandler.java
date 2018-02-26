package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetD2DConnectInfoException;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetPolicyContentXmlException;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.NodeIsNotManagedException;

public abstract class TaskDeploymentExceptionHandler{
	private static Logger logger = Logger.getLogger(VSphereBackupTaskDeployment.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private static IEdgeExternalLinks edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	
	protected void handleException(String deployOrRemove,PolicyDeploymentTask argument, Exception e, int nodeId, String nodeName){
		if(e instanceof GetPolicyContentXmlException){
			
			handleGetPolicyContentXmlException(deployOrRemove,argument,e,nodeName);
			
		}else if(e instanceof GetD2DConnectInfoException){
			
			handleGetD2DConnectInfoException(deployOrRemove, argument, e, nodeName);

		}else if(e instanceof NodeIsNotManagedException){

			handleNodeIsNotManagedException(deployOrRemove, argument, e, nodeName);
		}else if(e instanceof DeploymentException) {
			
			handleDeploymentException(deployOrRemove, argument, e, nodeName);

		}else if(e instanceof SOAPFaultException){ //Agent side exception
			
			handleSOAPFaultException(deployOrRemove, argument, (SOAPFaultException)e, nodeName);

		}else if(e instanceof WebServiceException){
			
			handleWebServiceException(deployOrRemove, argument, (WebServiceException)e, nodeId, nodeName);
			
		}else if(e instanceof EdgeServiceFault){
			
			handleEdgeServiceFault(deployOrRemove, argument, (EdgeServiceFault)e, nodeName, nodeId);
			
		}else{
			handleUnkownException(deployOrRemove, argument, e, nodeName);
		}
	}
	
	private void handleGenerateException(PolicyDeploymentTask argument, Exception e,String nodeName, String errorMsg,String innerLogMsg){
		this.updateDeployStatus(argument);
		logger.error(innerLogMsg,e);
		this.writeActivityLogAndDeployMessage(argument, errorMsg, nodeName);
	}
	
	private void handleGetPolicyContentXmlException(String deployOrRemove,PolicyDeploymentTask argument, Exception e, String nodeName){
		String errorMessage = EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToGetPolicyContent");
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+":Failed to get policy contents.");
	}
	
	private void handleGetD2DConnectInfoException(String deployOrRemove,PolicyDeploymentTask argument, Exception e, String nodeName) {
		String errorMessage = EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToGetNodeConnectInfo");
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to get node connect information.");
	}
	
	private void handleNodeIsNotManagedException(String deployOrRemove,PolicyDeploymentTask argument, Exception e, String nodeName){
		String errorMessage = EdgeCMWebServiceMessages.getResource("policyDeployment_TheNodeIsNotManagedByEdge",getMessageSubject(),nodeName);
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Node is not managed by current console.");
	}
	
	private void handleDeploymentException(String deployOrRemove,PolicyDeploymentTask argument, Exception e, String nodeName){
		String errorMessage = EdgeCMWebServiceMessages.getResource("policyDeployment_FailedToUpdateDeploymentStatusOfTheNode");
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to update policy deployment status.");
	}
	
	private void handleSOAPFaultException(String deployOrRemove,PolicyDeploymentTask argument, SOAPFaultException e, String nodeName){
		if("20937966885".equalsIgnoreCase(e.getFault().getFaultCode())){//credential error
			try {
				String uiErrorMsg = EdgeCMWebServiceMessages.getMessage("policyDeployment_NodeCredentialError",getMessageSubject(),nodeName); 
				e.getFault().setFaultString(uiErrorMsg);
			} catch (SOAPException e1) {
				//do nothing
			}
		}
		String errorMessage = e.getFault().getFaultString(); 
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": agent side SOAPFaultException.");
	}
	
	private void handleWebServiceException(String deployOrRemove,PolicyDeploymentTask argument, WebServiceException e, int nodeId, String nodeName){
		if (e.getCause() instanceof SocketTimeoutException) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_Timeout",
					getMessageSubject(),nodeName);
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API - timeout.");
		} else {
			try {
				GatewayEntity gateway = gatewayService.getGatewayByHostId(nodeId);
				EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(nodeId);
				IRemoteNativeFacade remoteNativeFacadeImpl = remoteNativeFacadeFactory.createRemoteNativeFacade(gateway.getId());
				
				//test whether network is available
				boolean isReachable = remoteNativeFacadeImpl.isHostReachble(hostInfo.getRhostname());
				if(!isReachable){
					//net work error
					String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_NetWorkNotReach",
							getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
					handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node. - Network is not available.");
					return;
				}
				
				//try to test whether D2D service is down
				ServiceState agentServiceIsRunning = ServiceState.UnKnown; //unknown
				agentServiceIsRunning = remoteNativeFacadeImpl.checkServiceIsRunning(hostInfo.getRhostname(), ServiceInfoConstants.AGENT_SERVICE_NAME
						, hostInfo.getUsername(), hostInfo.getPassword());
				
				if(agentServiceIsRunning == ServiceState.NotRunning){ //service is down
					String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_ServiceDown",
							getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
					handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node - D2D service is stopped.");
				}else if(agentServiceIsRunning == ServiceState.Running){//service is running, check protocol and port
					String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_WrongProtocolOrPort",
							getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
					handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node - Used wrong protocol or wrong port.");
				}else {//can't communicate service
					String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_ShareNotAvailable",
							getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
					handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node. - Admin$ share is not available.");
				}
			} catch (Exception e1) {
				logger.error("[TaskDeploymentExceptionHandler]handleException(): get gateway failed or test network reachable failed.",e1);
				String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode",
						getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
				handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node.");
			}
		}
	}
	
	private void handleEdgeServiceFault(String deployOrRemove, PolicyDeploymentTask argument, EdgeServiceFault e, String nodeName, int nodeId){
		
		if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(e.getFaultInfo().getCode())) {
			this.updateDeployStatus(argument,PolicyDeployStatus.DeployFaileBecauseOtherEdge);
			logger.error(deployOrRemove+": This node is already managed by other.", e);
			String edgeHostName = "localhost";
			try {
				InetAddress addr = InetAddress.getLocalHost();
				edgeHostName = addr.getHostName().toLowerCase();
			} catch (UnknownHostException ex) {
				logger.debug("deployUnifiedPolicy: Cannot get local hostname", ex);
			}
			String message = e.getFaultInfo().getMessage();
			String hostEdgeNameOfD2D = message.substring(message.indexOf('^')+1);
			String subject;
			if(e.getFaultInfo().getMessageParameters()!= null){
				subject = EdgeCMWebServiceMessages.getResource((String)e.getFaultInfo().getMessageParameters()[0]);
			} else {
				subject = getMessageSubject();
			}
			String logMsg = EdgeCMWebServiceMessages.getMessage(
					"policyDeployment_TheNodeIsManagedByOtherEdge_log", subject,nodeName,hostEdgeNameOfD2D,
					edgeHostName);
			this.writeActivityLogAndDeployMessage(argument, logMsg, nodeName);
			
		}else if (EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error.equals(e.getFaultInfo().getCode())) {
			
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToConnectToTheNode",
					getMessageSubject(), nodeName);
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to connect to the node.");
			
		}else if (EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential.equals(e.getFaultInfo().getCode())) {
			
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_NodeCredentialError",
					getMessageSubject(), nodeName);
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to connect to the node, invalid credential.");
			
		}else if (EdgeServiceErrorCode.Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE.equals(e.getFaultInfo().getCode())) {
			GatewayId gatewayId = new GatewayId(1);
			
			try {
				GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(nodeId);
				gatewayId = gatewayEntity.getId();
			} catch (EdgeServiceFault e1) {
				logger.error("[TaskDeploymentExceptionHandler] handleEdgeServiceFault() get gateway failed for node: "+nodeName);
			}
			e.getFaultInfo().setMessageParameters(new String[]{EdgeCommonUtil.getGatewayHostNameByGateWayId(gatewayId)});
			String errorMessage = WebServiceFaultMessageRetriever.
					getErrorMessage( DataFormatUtil.getServerLocale(),(e.getFaultInfo()));
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Agent failed to connect to the console.");
			
		}
		else if(EdgeServiceErrorCode.Node_CantConnect_NetWorkNotAvailable.equals(e.getFaultInfo().getCode())){
			String gatewayHostName = EdgeCommonUtil.getGatewayHostNameByNodeId(argument.getHostId());
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_NetWorkNotReach",
					getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService(), gatewayHostName);
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node. - Network is not available.");
		
		}else if (EdgeServiceErrorCode.Node_CantConnect_Admin$Disable.equals(e.getFaultInfo().getCode())) {
			
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_ShareNotAvailable",
					getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node. - Admin$ share is not available.");
		
		}else if (EdgeServiceErrorCode.Node_CantConnect_ServiceDown.equals(e.getFaultInfo().getCode())) {
		
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_ServiceDown",
					getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node - D2D service is stopped.");
		
		}else if (EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort.equals(e.getFaultInfo().getCode())) {

			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_WrongProtocolOrPort",
					getMessageSubject(),nodeName,edgeExternalLinks.cannotConnectD2DWebService());
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node - Used wrong protocol or wrong port.");
		
		}else if(EdgeServiceErrorCode.Node_CantConnect_AccessServiceError.equals(e.getFaultInfo().getCode())){
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_FailedToInvodeD2DWebServiceAPIOfTheNode_CannotAccessService",
					getMessageSubject(),nodeName,getMessageSubject(),edgeExternalLinks.cannotConnectD2DWebService());
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Failed to invoke D2D web service API of the node - Used wrong protocol or wrong port or service is down.");
		}else if(EdgeServiceErrorCode.Node_Linux_D2D_Server_Version_Low.equals(e.getFaultInfo().getCode())){
			String errorMessage = EdgeCMWebServiceMessages.getMessage("policyDeployment_LinuxD2DServerVersionLow");
			handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": Linux D2D server version is low.");
		}else{
			handleUnkownException(deployOrRemove,argument, e, nodeName);
		}
	}
	
	private void handleUnkownException(String deployOrRemove, PolicyDeploymentTask argument, Exception e, String nodeName){
		String errorMessage = EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownError",
				e.getLocalizedMessage());
		handleGenerateException(argument, e, nodeName, errorMessage, deployOrRemove+": unknown error.");
	}
	
	private void updateDeployStatus(PolicyDeploymentTask argument){
		updateDeployStatus(argument, PolicyDeployStatus.DeploymentFailed);
	}
	
	abstract void updateDeployStatus(PolicyDeploymentTask argument, int policyDeployStatus);
	abstract void writeActivityLogAndDeployMessage(PolicyDeploymentTask argument, String message, String nodeName);
	abstract String getMessageSubject();
}
