package com.ca.arcserve.edge.app.base.webservice.exception;

import java.util.Date;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUAuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class NodeExceptionUtil {
	private static Logger logger = Logger.getLogger(NodeExceptionUtil.class);
	private static IEdgeExternalLinks edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	private static ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private static final String[] internalErrorCodes = new String[]{"404","500","501","502","503"};
	
	public static void convertWebServiceException(Exception e, ConnectionContext context, String messageSubject, String[] serviceName )throws EdgeServiceFault{
		//check user credentials
		if (e instanceof SOAPFaultException) {
			String errorMessage = ((SOAPFaultException)e).getFault().getFaultString();
			if (errorMessage != null && (errorMessage.contains(EdgeServiceFault.INVALID_USER_CREDENTIALS)
											||errorMessage.contains(EdgeServiceFault.INVALID_USER_NAMEORPASSWORD))) {
				throw convert(e, EdgeServiceErrorCode.Node_InvalidUser,new String[]{});
			}else
				throw (SOAPFaultException) e;
		}
		
		String link = getLinkByService(messageSubject);
		
		//check webServer internal error
		WebServiceException exception = (WebServiceException)e;
		for(int i=0 ; i < internalErrorCodes.length ; i++){
			if(exception.getMessage().contains(internalErrorCodes[i])){
				throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_ServiceInternalError
						,new String[]{
						messageSubject,
						context.getHost(),
						serviceName[1],
						messageSubject,
						link});
			}
		}
		
		//check network 
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade(context.getGateway().getId());
		
		boolean isReachable = true;
		try {
			isReachable = nativeFacade.isHostReachble(context.getHost());
		} catch (Exception e1) {
			logger.debug("[Connection]: Test network reachale failed.",e);
		}
		if(messageSubject == null)
			messageSubject = "";
		if(!isReachable){
			throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_NetWorkNotAvailable
					,new String[]{messageSubject
					,context.getHost()
					,messageSubject
					,link});
		}else {
			if(serviceName != null && !StringUtil.isEmptyOrNull(serviceName[0])){
				if(serviceName[0].equals(com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.ASBU_SERVICE_NAME) 
						&& context.getAuthenticationType() == ASBUAuthenticationType.ARCSERVE_BACKUP.getValue()){//asbu webservice && AuthenticationType is arcserve backup
					throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_ASBUServiceError
							,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});	
				}else{
					ServiceState agentServiceIsRunning = ServiceState.UnKnown; //unknown
					agentServiceIsRunning = nativeFacade.checkServiceIsRunning(context.getHost(), 
						serviceName[0],context.getUsername(), context.getPassword());
					if(agentServiceIsRunning == ServiceState.NotRunning){//serviceDown
						throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_ServiceDown
								,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});		
					}else if(agentServiceIsRunning == ServiceState.Running){//protocol or port wrong
						if(serviceName[0].equals(com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.ASBU_SERVICE_NAME)){
							throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_ASBUServiceError
									,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});	
						}else {
							throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});
						}
					}else {
						//throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_Admin$Disable,new String[]{messageSubject, context.getHost(),edgeExternalLinks.cannotConnectD2DWebService()});
						if(serviceName[0].equals(com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.ASBU_SERVICE_NAME)){
							throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_ASBUServiceError
									,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});	
						}else {
							throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_AccessServiceError
									,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});}
					}
				}
			}else if(serviceName != null){
				throw convert(exception, EdgeServiceErrorCode.Node_CantConnect_AccessServiceError
						,new String[]{messageSubject,context.getHost(),serviceName[1],messageSubject,link});		
			}
		}
	}
	
	private static EdgeServiceFault convert(Exception exception,String errorCode,String[] parameters) {
		logger.debug(exception.getMessage(), exception); 
		return createEdgeServiceFault(errorCode, exception.getMessage(), null,parameters);
	}
	
	private static EdgeServiceFault createEdgeServiceFault(String errorCode, String errorMessage, FaultType type, String[] parameters) {
		EdgeServiceFault fault = EdgeServiceFault.getFault(errorCode,parameters, errorMessage);
		fault.getFaultInfo().setFaultType(type);
		return fault;
	}
	
	public static String getNodeMessageSubject(){
		return EdgeCMWebServiceMessages.getMessage("node");
	}
	
	public static String getLinuxServerMessageSubject(){
		return EdgeCMWebServiceMessages.getMessage("linuxD2dServer");
	}
	
	public static String getRpsMessageSubject(){
		return EdgeCMWebServiceMessages.getMessage("productShortNameRPS");
	}
	
	public static String getAsbuMessageSubject(){
		return EdgeCMWebServiceMessages.getMessage("ASBUServer");
	}
	
	public static String[] getAgentServiceNames(){
		String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.AGENT_SERVICE_NAME;
		String displayName = EdgeCMWebServiceMessages.getMessage("productNameAgent");
		return new String[]{realServiceName,displayName};
	}
	
	public static String[] getAsbuServiceNames(){
		String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.ASBU_SERVICE_NAME;
		String disPlayServiceName = EdgeCMWebServiceMessages.getMessage("productNameASBU");
		return new String[]{realServiceName,disPlayServiceName};
	}
	
	public static long generateActivityLogByException(Module module,Node node, String logWrapperKey, Exception exception){
		String errorMessage = "";
		if(exception instanceof EdgeServiceFault){
			EdgeServiceFaultBean fault = ((EdgeServiceFault) exception).getFaultInfo();
			errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),fault);
		}else if (exception instanceof SOAPFaultException) {
			errorMessage = ((SOAPFaultException) exception).getFault().getFaultString();
		}else{
			EdgeServiceFaultBean fault  = new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General, null);
			errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),fault);
		}
		String logMsg = EdgeCMWebServiceMessages.getMessage(logWrapperKey,errorMessage);
		String nodeName = (node == null ? "":node.getHostname());
		int nodeId = (node == null ? 0:node.getId());
		ActivityLog log = new ActivityLog();
		log.setNodeName(nodeName);
		log.setHostId(nodeId);
		log.setModule(module);
		log.setSeverity(Severity.Error);
		log.setTime(new Date());
		log.setMessage(logMsg);
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error("[NodeExceptionUtil] add activity log failed.",e);
		}
		return 0;
	}
	
	public static long generateActivityLogByExceptionForRegInfo(Module module,NodeRegistrationInfo nodeRegInfo, String logWrapperKey, Exception exception){
		if(nodeRegInfo == null){
			return generateActivityLogByException(module, null, logWrapperKey, exception);
		}else {
			Node tempNode = new Node();
			tempNode.setId(nodeRegInfo.getId());
			tempNode.setHostname(nodeRegInfo.getNodeName());
			return generateActivityLogByException(module, tempNode, logWrapperKey, exception);
		}
	}
	
	public static String getLinkByService(String messageSubject){
		if(messageSubject.equalsIgnoreCase(getNodeMessageSubject())){
			return edgeExternalLinks.cannotConnectD2DWebService();
		}else if(messageSubject.equalsIgnoreCase(getLinuxServerMessageSubject())){
			return edgeExternalLinks.cannotConnectLinuxBackupServerWebService();
		}else if(messageSubject.equalsIgnoreCase(getRpsMessageSubject())){
			return edgeExternalLinks.cannotConnectRpsWebService();
		}else if (messageSubject.equalsIgnoreCase(getAsbuMessageSubject())) {
			return edgeExternalLinks.cannotConnectAsbuWebService();
		}else {
			return edgeExternalLinks.cannotConnectD2DWebService();
		}
	}
}
