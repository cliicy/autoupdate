/**
 * @(#)PolicyManagement.java 7/19/2011
 * Copyright 2011 CA Technologies, Inc. All rights reserved.
 */
package com.ca.arcserve.edge.app.rps.webservice.policy;

import java.util.Arrays;

import javax.xml.ws.soap.SOAPFaultException;

//import org.apache.log4j.Logger;




import com.ca.arcflash.rps.webservice.data.DisabledNodes;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.rps.webservice.registration.RPSRegInfo;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegistrationReturnCode;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.NodeConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.IMessageServiceModule;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
//import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.rpsReg.EdgeRPSRegServiceImpl;

/**
 * Class<code>PolicyManagement</code> this class is used to describe RPS policy management behavior.
 *  It will be used in the RPS web service implement.
 * 
 * @author lijbi02
 * @version 1.0 07/19/2011
 * @since JDK1.6
 */
public class PolicyManagement {
	
	private final static PolicyManagement INSTANCE = new PolicyManagement();
	//private final static Logger log = Logger.getLogger(PolicyManagement.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	private PolicyManagement() {
	}
	
	public static PolicyManagement getInstance(){
		return INSTANCE;
	}
	
	public void deletePolicy(int rpsNodeId, String rpsPolicyUuid) throws EdgeServiceFault {
		IRPSService4CPM service4cpm = connectRpsServer(rpsNodeId);
		service4cpm.deleteRPSPolicies(Arrays.asList(rpsPolicyUuid));
	}
	
	private IRPSService4CPM connectRpsServer(int rpsNodeId) throws EdgeServiceFault {
		ConnectionContext connectionContext = new NodeConnectionContextProvider(rpsNodeId).create();
		try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(connectionContext))) {
			connection.connect();
			
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp(connection.getService())) {
				return connection.getService();
			} else {
				EdgeRPSRegServiceImpl impl = new EdgeRPSRegServiceImpl();
				int result = impl.regRps(connection, connectionContext, false);
				if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_OTHER_EDGE){
					RPSRegInfo consoleInfo = connection.getService().getEdgeRpsRegInfo();
					String curRegisteredEdgeHostName = consoleInfo == null ? "" : consoleInfo.getRpsAppHostName();
					if (consoleInfo != null) {				
						String consoleName = ConsoleUrlUtil.getConsoleHostName(consoleInfo.getConsoleUrl());
						if(!StringUtil.isEmptyOrNull(consoleName))
							curRegisteredEdgeHostName = consoleName;
					}
					String host = RpsNodeUtil.getNodeById(rpsNodeId).getNode_name();
					EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(EdgeServiceErrorCode.POLICY_RPS_MANAGED_BY_ANOTHER_CONSOLE,EdgeRPSWebServiceMessages.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP", host));
					faultInfo.setMessageParameters(new String[]{host,curRegisteredEdgeHostName, String.valueOf(rpsNodeId)});
					throw new EdgeServiceFault(EdgeRPSWebServiceMessages.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP", host,curRegisteredEdgeHostName), faultInfo);
				} else if (result != EdgeRegistrationReturnCode.REG_ERROR_CODE_SUCCEED) {
					String host = RpsNodeUtil.getNodeById(rpsNodeId).getNode_name();
					throw AxisFault.fromAxisFault(EdgeRPSWebServiceMessages.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP", host), FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER);
				} else {
					return connection.getService();				
				}
			}
		} catch(SOAPFaultException e){
			String host = RpsNodeUtil.getNodeById(rpsNodeId).getNode_name();
			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(EdgeServiceErrorCode.POLICY_RPS_WrongCredential,EdgeRPSWebServiceMessages.getResource("RPS_SERVER_LOGIN_FAILED", host));
			faultInfo.setMessageParameters(new String[]{host});
			throw new EdgeServiceFault(EdgeRPSWebServiceMessages.getResource("RPS_SERVER_LOGIN_FAILED", host), faultInfo);
		} catch (EdgeServiceFault e){
			throw e;
		} catch (Exception e) {
			
			IMessageServiceModule msgSvcModule = EdgeFactory.getBean( IMessageServiceModule.class );
			if (msgSvcModule.isMessageServiceException( e ))
				throw msgSvcModule.convertExceptionToEdgeServiceFault( e );
			
			String host = RpsNodeUtil.getNodeById(rpsNodeId).getNode_name();
			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,EdgeRPSWebServiceMessages.getResource("RPS_SERVER_LOGIN_FAILED", host));
			faultInfo.setMessageParameters(new String[]{host});
			throw new EdgeServiceFault(EdgeRPSWebServiceMessages.getResource("RPS_SERVER_LOGIN_FAILED", host), faultInfo);
		}
	}
	
	public void deployPolicy(int rpsNodeId, RPSPolicy policy, DisabledNodes dn) throws EdgeServiceFault{
		IRPSService4CPM service4cpm = connectRpsServer(rpsNodeId);
		deployPolicyToRPSServer(service4cpm, policy, dn);
		//outputSuccMessageToActivityLog(policy.getName(), RpsNodeUtil.getNodeById(rpsNodeId).getNode_name(), PolicyDeployReason.Assign);
	}

	private void deployPolicyToRPSServer(IRPSService4CPM service4cpm, RPSPolicy policy, DisabledNodes dn) throws EdgeServiceFault {
		if (service4cpm != null) {			
			service4cpm.saveRPSPolicy(policy, dn);
		} else {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, EdgeRPSWebServiceMessages.getMessage("COMMON_ERROR_NULL_POINTER"));
		}
	}
	
	/*private void outputSuccMessageToActivityLog(String policyname, String host, PolicyDeployReason reason) {
		String formatStr;
		if(reason.equals(PolicyDeployReason.Assign))
			formatStr = EdgeRPSWebServiceMessages.getMessage("POLICY_DEPLOYMENT_RESULT_MESSAGE");
		else
			formatStr = EdgeRPSWebServiceMessages.getMessage("POLICY_UNASSIGN_RESULT_MESSAGE");
			
		String outputStr = String.format(formatStr, policyname, host, EdgeRPSWebServiceMessages
				.getMessage("POLICY_DEPLOYMENT_SUCCEED"),"");
//		PolicyLogProxy.getInstance().addSuccessLog(Module.RpsPolicyManagement, host, outputStr);
		log.info(outputStr);
	}*/

}
