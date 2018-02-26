package com.ca.arcserve.edge.app.rps.webservice.rpsReg;

import java.util.Locale;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.registration.RPSRegInfo;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegistrationReturnCode;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.WebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.rps.IEdgeRPSRegService;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;

public class EdgeRPSRegServiceImpl implements IEdgeRPSRegService {

	private static Logger logger = Logger.getLogger(EdgeRPSRegServiceImpl.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IRpsConnectionInfoDao rpsConnectionInfoDao = DaoFactory.getDao(IRpsConnectionInfoDao.class);
	
	public int regRps(RPSConnection connection, ConnectionContext connectionContext, boolean forceFlag) throws EdgeServiceFault {
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
		String edgeWSDL = WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
		String consoleUrl = EdgeCommonUtil.getConsoleUrl( edgeHostName, edgePort, edgeProtocol );
		
		logger.debug("UpdateRegInfoToRpsServer(): edgeWSDL = " + edgeWSDL);
		
		RPSRegInfo info = connection.getService().getEdgeRpsRegInfo();
		String consoleName = (info!=null&&info.getRpsAppHostName()!=null)?info.getRpsAppHostName():"";
		if (info != null) {
			consoleName = ConsoleUrlUtil.getConsoleHostName(info.getConsoleUrl());
		}
		if (forceFlag && info != null && !"".equals(consoleName) && !edgeHostName.equals(consoleName)) {
			//Not managed by current cpm
			String message = EdgeCMWebServiceMessages.getMessage("forceManageRPS", edgeHostName, consoleName);
			RPSNodeServiceImpl rpsNodeService = new RPSNodeServiceImpl(); 
			rpsNodeService.addActivityLogForImportNodes(connectionContext.getGateway().getId().getRecordId(), connection.getClientProxy().getHost(), Severity.Warning, ImportNodeType.RPSSelfUIImport, message);
		}
		
		String locale = Locale.getDefault().toString();
		if(info == null){
			info = new RPSRegInfo();
		}
		info.setRpsAppHostName(edgeHostName);
		info.setRpsAppLocale(locale);
		info.setRpsAppUUID(edgeUUID);
		info.setRpsAppWSDL(edgeWSDL);
		info.setRpsName(connectionContext.getHost());
		info.setConsoleUrl( consoleUrl );
		info.setRpsConnectNameList(CommonUtil.getConnectNameList());
		int retcode = 0;
		try {
			retcode = connection.getService().register4Console(info, forceFlag);
		} catch (Exception e) {
			logger.error("[EdgeRPSRegServiceImpl] regRps() invoke register4Console failed.",e);
			if(e instanceof SOAPFaultException){ // If Rps is lower version,then invoke old API
				logger.info("[EdgeD2DRegServiceImpl] regRps() will invoke the old interface Register4RPSApp");
				try {
					return connection.getService().Register4RPSApp(edgeUUID, edgeHostName, edgeWSDL, locale, forceFlag);
				} catch (WebServiceException ex) {
					NodeExceptionUtil.convertWebServiceException(ex, connectionContext, 
							NodeExceptionUtil.getNodeMessageSubject(), NodeExceptionUtil.getAgentServiceNames());
				}
			}else {
				NodeExceptionUtil.convertWebServiceException(e, connectionContext,
						NodeExceptionUtil.getNodeMessageSubject(), NodeExceptionUtil.getAgentServiceNames());
			}
		}
		return retcode;
	}
	
	@Override
	public void UpdateRegInfoToRpsServer(ConnectionContext context,int nodeId, boolean forceFlag) throws EdgeServiceFault {
		try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			int result = regRps(connection, context,forceFlag);
			if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_SUCCEED 
					|| result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE
					|| result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_NeedRedeploy) {
				if(nodeId != 0)
					rpsConnectionInfoDao.as_edge_rps_connection_info_manage_update(nodeId, NodeManagedStatus.Managed.ordinal());
			} if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_FATAL_ERROR) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_Fatal_Error, "Call D2D interface D2DRegister4Edge() failed");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REG_SAAS_NOT_ALLOW_MANAGED) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_SAAS_NOT_ALLOW_Managed, "SaaS D2D can't be managed");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_Again, "Registered already. update it again");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_OTHER_EDGE) {
				StringBuilder message = new StringBuilder("Registered by another Edge Server. Cannot update");
				
				RPSRegInfo edgeInfo = connection.getService().getEdgeRpsRegInfo();
				String consoleName = (edgeInfo!=null && edgeInfo.getRpsAppHostName()!=null)?edgeInfo.getRpsAppHostName():"";
				if (edgeInfo != null) {
					consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
				}
				if (null != edgeInfo) {
					message.append("^" + consoleName);
				}
				
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_Duplicate, message.toString());
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_EDGE_CONNECTION_FAILURE) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_RPS_CANNOT_CONNECT_EDGE, "D2D service cannot connect to this Edge service");
			}
		}
	}

	@Override
	public void RemoveRegInfoFromRpsServer(RpsNode node,RpsConnectionInfo conInfo, boolean forceFlag) throws EdgeServiceFault {
		int rpsNodeId = node.getNode_id() > 0 ? node.getNode_id() : conInfo.getNode_id();
		if (rpsNodeId == 0) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the rps node id is invalid.");
		}
		
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
		
		try (RPSConnection connection = connectionFactory.createRPSConnection(rpsNodeId)) {
			connection.connect();
			
			int result = connection.getService().UnRegister4RPSApp(edgeUUID, edgeHostName, forceFlag);
			if (result == -1) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_Reg_Fatal_Error, "Call D2D interface D2DUnRegister4Edge() failed");
			} else if (result == 2) {
				throw EdgeServiceFault.getFault(EdgeRpsServiceErrorCode.Node_RPS_UnReg_Not_Owner, "Registered by another Edge Server. Cannot remove it");
			}
		}
	}

}
