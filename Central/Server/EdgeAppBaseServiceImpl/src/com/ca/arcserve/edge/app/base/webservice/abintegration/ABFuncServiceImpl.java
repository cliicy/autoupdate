package com.ca.arcserve.edge.app.base.webservice.abintegration;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.OldASBUConnection;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUAuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;


/**
 * http://chech24-w8s-18:9999/ABFuncService/metadata
 */
public class ABFuncServiceImpl {
	private IABFuncService m_proxy;
	private boolean m_binit;
	private String m_ARCServerName;
	private int m_Port;
	private static final Logger logger = Logger.getLogger(ABFuncServiceImpl.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	public ABFuncServiceImpl(String strARCServer, int port)
	{
		m_proxy = null;
		m_binit = false;
		m_ARCServerName = strARCServer;
		m_Port = port;
	}

	public String ConnectARCserve(GatewayEntity gateway, String strUser, String strPassword, ABFuncAuthMode mode) throws EdgeServiceFault
	{	
		String strSessionNo = null;
		if(!m_binit){
			initABFunService(gateway, strUser, strPassword, mode);
		}
		try {
			if(m_proxy != null){
				strSessionNo = m_proxy.connectARCserve(strUser, strPassword, mode);
			}else {
				logger.error("[ABFuncServiceImpl] service is null.");
				throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT);
			}
		}catch (SOAPFaultException sofe){	
			String code = sofe.getFault().getFaultCode();
			if(code == null){// issue 764043, code maybe null
				logger.error("[ABFuncServiceImpl] error", sofe);
				throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT);
			}
			if(code.compareToIgnoreCase("s:13") == 0)
			{
				code = EdgeServiceErrorCode.ABFunc_NoPermission;
			}			
			else if(code.compareToIgnoreCase("s:14") == 0)
			{
				code = EdgeServiceErrorCode.ABFunc_UserNamePasswordError;
			}
			else
			{
				throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT);
			}
			EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
			EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
			throw fault;
		}
		catch (Exception e)
		{
			logger.error("[ABFuncServiceImpl] error", e);
			if (e.getCause() != null && (e.getCause() instanceof ConnectException
					|| e.getCause() instanceof SocketException 
					|| e.getCause() instanceof UnknownHostException)) {
				throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT);
			} else {
				throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_MISMATCH_VERSION);
			}
		}
		
		return strSessionNo;
	}
	
	private EdgeServiceFault throwCorrespondingException(String hostName, String errorCode) {
		EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
				errorCode, "");
		bean.setMessageParameters(new Object[]{hostName,errorCode});
		// add error message
		String errorMessage = WebServiceFaultMessageRetriever.
				getErrorMessage(new Locale("en"), bean);
		return new EdgeServiceFault(errorMessage, bean);
	}

	public ABFuncServerType GetServerType(String strSessionNo)throws EdgeServiceFault
	{	
		ABFuncServerType st = ABFuncServerType.UN_KNOWN;
		try 
		{
			st = m_proxy.getServerType(strSessionNo);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}			
		}
		catch (Exception e)
		{
			e.getCause().toString();
			/*EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(e.getCause().toString(), e
					.getLocalizedMessage());
			EdgeServiceFault fault = new EdgeServiceFault(e.getMessage(),
					faultbean);
			throw fault;*/
		}
		
		return st;
		
	}
	
	public String MarkArcserveManageStatus(String strSessionNo, String strEdgeServerId, Boolean bOverwrite, ABFuncManageStatus status)throws EdgeServiceFault
	{	
		String arcserveId = "";
		try 
		{
			String hostname = "";
			String edgeUser = "";
			String edgePassword ="";
			String edgeDomain = "";
			String proto = "";
			String wsdl = "";
			int port = 0;
			if(ABFuncManageStatus.MANAGED == status)
			{
				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
				hostname = localMachine.getHostName();
				
				NativeFacade nativeFacade = new NativeFacadeImpl();				
				EdgeAccount acc = nativeFacade.getEdgeAccount();
				edgeUser = acc.getUserName();
				edgePassword = acc.getPassword();
				edgeDomain = acc.getDomain();
				
				proto = EdgeCommonUtil.getEdgeWebServiceProtocol();
				port = EdgeCommonUtil.getEdgeWebServicePort();
				
				wsdl = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(hostname, port, proto);
			}			
			
			arcserveId = m_proxy.markArcserveManageStatus(strSessionNo, strEdgeServerId, edgeUser, edgePassword, edgeDomain, wsdl, bOverwrite, status);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
			}
			else if(0 == code.compareToIgnoreCase("s:15")) 
			{					
				code = EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer;								
			}			
			EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
			EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
			throw fault;
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}
		
		return arcserveId;
	}
	
	public ABFuncManageStatus GetArcserveManageStatus(String strSessionNo, String strEdgeServerName)throws EdgeServiceFault
	{	
		ABFuncManageStatus status = ABFuncManageStatus.UN_KNOWN;
		try 
		{
			status = m_proxy.getArcserveManageStatus(strSessionNo, strEdgeServerName);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}			
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}
		
		return status;
	}
	
	public String getGDBServer(String strSessionNo)throws EdgeServiceFault
	{		
		String strGDBServer = null;
		try 
		{
			strGDBServer =  m_proxy.getGDBServer(strSessionNo);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}			
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}	
	     
		
		return strGDBServer;
	}
	
	public ArrayOfstring getArcserveVersionInfo(String strSessionNo)throws EdgeServiceFault {
    	ArrayOfstring arcserveVersionInfo = null;
    	try
    	{
    		arcserveVersionInfo =  m_proxy.getArcserveVersionInfo(strSessionNo);
    	}    	
    	catch (SOAPFaultException sofe) 
		{	
    		String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}		
    		
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}
		return arcserveVersionInfo;
	}
	
	public Boolean IsArcserveBranch(String strSessionNo)throws EdgeServiceFault
	{	
		Boolean isBranch = false;
		try 
		{
			isBranch = m_proxy.isArcserveBranch(strSessionNo);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}			
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}
		
		return isBranch;
		
	}
	
	public String GetManagedEdgeServer(String strSessionNo)throws EdgeServiceFault
	{	
		String serverName = "";
		try 
		{
			serverName = m_proxy.getManagedEdgeServer(strSessionNo);
		}
		catch (SOAPFaultException sofe) 
		{				
			String code = sofe.getFault().getFaultCode();
			if(0 == code.compareToIgnoreCase("s:1000000")) // WCF Connect Timeout
			{					
				code = EdgeServiceErrorCode.ABFunc_WCFConnectTimeout;
				EdgeServiceFaultBean faultbean = new EdgeServiceFaultBean(code, sofe.getLocalizedMessage());
				EdgeServiceFault fault = new EdgeServiceFault(sofe.getMessage(), faultbean);
				throw fault;				
			}			
		}
		catch (Exception e)
		{
			e.getCause().toString();			
		}
		
		return serverName;
		
	}
	
	private ConnectionContext getConnectionContext(GatewayEntity gateway, String strUser, String strPassword, ABFuncAuthMode mode){
		ConnectionContext context = new ConnectionContext();
		context.setHost(m_ARCServerName);
		context.setDomain("");
		context.setProtocol("http");
		context.setPort(m_Port);
		context.setAuthenticationType(mode == ABFuncAuthMode.AR_CSERVE ? ASBUAuthenticationType.ARCSERVE_BACKUP.getValue() : ASBUAuthenticationType.WINDOWS.getValue());
		context.setUsername(strUser);
		context.setPassword(strPassword);
		context.setGateway(gateway);
		return context;
	}
	
	private void initABFunService(GatewayEntity gateway, String strUser, String strPassword, ABFuncAuthMode mode)throws EdgeServiceFault{
		ConnectionContext context = getConnectionContext(gateway, strUser, strPassword, mode);
		try (OldASBUConnection connection = connectionFactory.createOldASBUConnection(new DefaultConnectionContextProvider(context))){
			connection.connect();
			m_proxy = connection.getService();
			m_binit = true;
		} catch (Exception e) {
			throw throwCorrespondingException(m_ARCServerName,EdgeServiceErrorCode.Node_ASBU_ADD_CANNOT_CONNECT);
		}
	}
}
