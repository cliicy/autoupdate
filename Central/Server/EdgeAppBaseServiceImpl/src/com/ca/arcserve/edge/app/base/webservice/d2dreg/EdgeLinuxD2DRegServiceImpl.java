package com.ca.arcserve.edge.app.base.webservice.d2dreg;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.LinuxD2DServerRegistrationResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.VersionInfo;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.D2DServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.register.D2DServerRegistrationInfo;
import com.ca.arcserve.linuximaging.webservice.data.register.D2DServerRegistrationResponse;

public class EdgeLinuxD2DRegServiceImpl implements IEdgeLinuxD2DRegService {
	
	private static final String SERVER_IP_FILE = "server_ip.ini";
	private static final Logger logger = Logger.getLogger(EdgeLinuxD2DRegServiceImpl.class);
	IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	
	EdgeConnectInfo _connInfo = null;
	private String d2dHost = "";
	private int d2dPort = 0;
	private String d2dProtocol = "";
	private String d2dUserName = "";
	private String d2dPassword = "";
	private String d2dAuthKey = "";
	private GatewayEntity gateway = null;
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	@Override
	public int unRegInfoToLinuxD2D(int d2dHostId, boolean forceFlag)
			throws EdgeServiceFault {
		int result = getConnInfoByHostId(d2dHostId);
		if(result != 0)
		{
			logger.error("unRegInfoToLinuxD2D(): cannot get connect info!!");
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error,"cannot get connect info");
		}
		ILinuximagingService service = connectToLinuxD2DWithKey();
		
		try{
			D2DServerRegistrationInfo registerInfo = new D2DServerRegistrationInfo();
			registerInfo.setForce(forceFlag);
			registerInfo.setServerUUID(CommonUtil.retrieveCurrentAppUUID());
			registerInfo.setAuthKey(d2dAuthKey);
			
			return service.unRegisterD2DServer(registerInfo);
		}catch(SOAPFaultException e){
			logger.error("unRegister linux D2D error",e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_Linux_D2D_Server_Managed_By_Others,"Failed to register Linux D2D managed by other cpm");
		}
		
	}

	private LinuxD2DServerRegistrationResponse regLinuxD2D(boolean isForce,boolean clearExistingData,boolean checkVersion) throws EdgeServiceFault{
		D2DServerRegistrationInfo registerInfo = getD2DServerRegistrationInfo(isForce,clearExistingData);
		
		ILinuximagingService service = connectToLinuxD2D(checkVersion);
		
		try{
			D2DServerInfo serverInfo = service.getD2DServerInfo();
			VersionInfo versionInfo = service.getVersionInfo();
			D2DServerRegistrationResponse response = service.registerD2DServer(registerInfo);			
			LinuxD2DServerRegistrationResponse rep = new LinuxD2DServerRegistrationResponse();
			rep.setD2dServerAuthKey(response.getD2dServerAuthKey());
			rep.setD2dServerUUID(response.getD2dServerUUID());
			rep.setOsName(serverInfo.getOsVersion());
			rep.setD2dServerTimezone(versionInfo.getTimeZoneOffset());
			rep.setBuildNumber(versionInfo.getBuildNumber());
			rep.setVersionNumber(versionInfo.getVersion());
			return rep;
		}catch(SOAPFaultException e){
			logger.error("register linux D2D error",e);
			throw e;
		}
	}
	
	private ILinuximagingService connectToLinuxD2D(boolean checkVersion) throws EdgeServiceFault{
		ConnectionContext context = new ConnectionContext();
		context.setHost(d2dHost);
		context.setProtocol(d2dProtocol);
		context.setPort(d2dPort);
		context.setUsername(d2dUserName);
		context.setPassword(d2dPassword);
		context.setGateway(gateway);
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(new DefaultConnectionContextProvider(context))){
			try{
				connection.connect();
			}catch(EdgeServiceFault e){
				if(EdgeServiceErrorCode.Node_Linux_D2D_Server_Version_Low.equalsIgnoreCase(e.getFaultInfo().getCode()))
				{				
					if(checkVersion){
						throw e;
					}
				}else{
					throw e;
				}
			}
			ILinuximagingService service = connection.getService();
			String key = null;
			try{
				key = service.validateUser(d2dUserName, d2dPassword);
			}catch (SOAPFaultException e) {
				logger.error("Linux D2D Login failed",e);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential,"Failed to login to Linux D2D");
			} catch (WebServiceException e) {
				logger.info("Linux D2D login failed, error message = " + e.getMessage());
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,d2dHost);
				b.setMessageParameters(new String[]{d2dHost});
				throw new EdgeServiceFault(d2dHost,b);
			}
			if(key == null){
				logger.error("Linux D2D Login failed");
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential,"Failed to login to Linux D2D");
			}
			return service;
		} catch (EdgeServiceFault e) {
			logger.error("connect linux d2d fail ", e);
			throw e;
		}
//		catch (Exception e) {
//			logger.error("connect linux d2d fail ", e);
//			EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,d2dHost);
//			b.setMessageParameters(new String[]{d2dHost});
//			throw new EdgeServiceFault(d2dHost,b);
//		}
	}
	
	private ILinuximagingService connectToLinuxD2DWithKey() throws EdgeServiceFault{
		ConnectionContext context = new ConnectionContext();
		context.setHost(d2dHost);
		context.setProtocol(d2dProtocol);
		context.setPort(d2dPort);
		context.setUsername(d2dUserName);
		context.setPassword(d2dPassword);
		context.setGateway(gateway);
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(new DefaultConnectionContextProvider(context))){
			connection.connect();
			ILinuximagingService service = connection.getService();
			int ret = -1;
			try{
				ret = service.validateByKey(d2dAuthKey);
			}catch (SOAPFaultException e) {
				logger.error("Linux D2D Login failed",e);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential,"Failed to login to Linux D2D");
			} catch (WebServiceException e) {
				logger.info("Linux D2D login failed, error message = " + e.getMessage());
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,d2dHost);
				b.setMessageParameters(new String[]{d2dHost});
				throw new EdgeServiceFault(d2dHost,b);
			}
			if(ret !=0){
				logger.error("Linux D2D Login failed");
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_Linux_D2D_Server_Managed_By_Others,"Linux D2D is managed by others");
			}
			return service;
		} catch (EdgeServiceFault e) {
			logger.error("connect linux d2d fail ", e);
			EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,d2dHost);
			b.setMessageParameters(new String[]{d2dHost});
			throw new EdgeServiceFault(d2dHost,b);
		}
	}
	
	private int getConnInfoByHostId(int d2dHostId) {
		IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		List<EdgeHost> hostLst = new ArrayList<EdgeHost>();
		String ids = "(" + d2dHostId + ")";
		hostMgrDao.as_edge_hosts_list(ids, hostLst);
		EdgeHost edgeHost = null;
		Iterator<EdgeHost> iterHost = hostLst.iterator();
		if (iterHost.hasNext()) {
			edgeHost = iterHost.next();
			d2dHost = edgeHost.getRhostname();
		} else {
			logger.debug("EdgeLinuxD2DRegServiceImpl.UpdateRegInfoToLinuxD2D(): cannot get host info of linux d2d host id "
							+ d2dHostId);
			return -1;
		}

		IEdgeConnectInfoDao connectInfoDao = DaoFactory
				.getDao(IEdgeConnectInfoDao.class);
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list(d2dHostId, connInfoLst);
		Iterator<EdgeConnectInfo> iter = connInfoLst.iterator();
		if (iter.hasNext()) {
			_connInfo = iter.next();
			d2dPort = _connInfo.getPort();
			if (_connInfo.getProtocol() == Protocol.Https.ordinal())
				d2dProtocol = "https";
			else
				d2dProtocol = "http";
			d2dUserName = _connInfo.getUsername();
			d2dPassword = _connInfo.getPassword();
			d2dAuthKey = _connInfo.getAuthUuid();
			bindGatewayIdentity(d2dHostId, null);
		} else {
			logger.debug("EdgeLinuxD2DRegServiceImpl.UpdateRegInfoToD2D(): cannot get connect info of linux d2d host id "
							+ d2dHostId );
			return -1;
		}

		return 0;
	}

	@Override
	public LinuxD2DServerRegistrationResponse RegInfoToLinuxD2D(NodeRegistrationInfo registrationNodeInfo,boolean forceFlag,boolean clearExistingData) throws EdgeServiceFault {
		setConnectionInfo(registrationNodeInfo);
		
		return regLinuxD2D(forceFlag,clearExistingData,true);
	}
	
	@Override
	public LinuxD2DServerRegistrationResponse RegInfoToLinuxD2D(
		GatewayId gatewayId, String hostName, int port, String protocol, String userName, String password,
		boolean forceFlag,boolean clearExistingData) throws EdgeServiceFault {
		
		d2dHost = hostName;
		d2dPort = port;
		d2dProtocol = protocol;
		d2dUserName = userName;
		d2dPassword = password;
		
		gateway = this.gatewayService.getGatewayById( gatewayId );
		
		return regLinuxD2D(true,clearExistingData,false);
	}

	@Override
	public void validateRegistrationInfo(
			NodeRegistrationInfo registrationNodeInfo, boolean forceFlag,
			boolean clearExistingData) throws EdgeServiceFault {
		setConnectionInfo(registrationNodeInfo);
		validateRegInfo(forceFlag,clearExistingData);
	}
	
	private void validateRegInfo(boolean isForce,boolean clearExistingData) throws EdgeServiceFault{
		D2DServerRegistrationInfo registerInfo = getD2DServerRegistrationInfo(isForce,clearExistingData);
		
		ILinuximagingService service = connectToLinuxD2D(true);
		
		try{
			service.validateRegistrationInfo(registerInfo);
		}catch(SOAPFaultException e){
			logger.error("register linux D2D error",e);
			throw e;
		}
	}
	
	private D2DServerRegistrationInfo getD2DServerRegistrationInfo(boolean isForce,boolean clearExistingData) throws EdgeServiceFault{
		String edgeHostName = "";
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int    edgePort = EdgeCommonUtil.getEdgeWebServicePort(); //???
		String edgeWSDL = "";
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();

		edgeHostName = CommonUtil.getServerIpFromFile();
		if(StringUtil.isEmptyOrNull(edgeHostName)){
			edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		}else{
			try {
				InetAddress.getByName(edgeHostName);
			} catch (UnknownHostException e) {
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_UDP_IP_Not_Reachable,edgeHostName);
				b.setMessageParameters(new String[]{edgeHostName});
				throw new EdgeServiceFault(edgeHostName,b);
			} 
		}
		
		edgeWSDL = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
		D2DServerRegistrationInfo registerInfo = new D2DServerRegistrationInfo();
		registerInfo.setForce(isForce);
		registerInfo.setPassword(d2dPassword);
		registerInfo.setUsername(d2dUserName);
		registerInfo.setServerUUID(edgeUUID);
		registerInfo.setServerWSDL(edgeWSDL);
		registerInfo.setServerName(edgeHostName);
		registerInfo.setServerURL(EdgeCommonUtil.getConsoleUrl(edgeHostName, edgePort, edgeProtocol));
		registerInfo.setManagedServerType(D2DServerRegistrationInfo.MANAGED_SERVER_TYPE_OOLONG);
		registerInfo.setClearExistingData(clearExistingData);
		registerInfo.setServerConnectNameList(CommonUtil.getConnectNameList());
		return registerInfo;
	}
	
	private void setConnectionInfo(NodeRegistrationInfo registrationNodeInfo){
		d2dHost = registrationNodeInfo.getNodeName();
		d2dPort = registrationNodeInfo.getD2dPort();
		d2dProtocol = registrationNodeInfo.getD2dProtocol().name();
		d2dUserName = registrationNodeInfo.getUsername();
		d2dPassword = registrationNodeInfo.getPassword();
		bindGatewayIdentity(registrationNodeInfo.getId(), registrationNodeInfo.getGatewayId());
	}

	private void bindGatewayIdentity(int id, GatewayId gatewayId) {
		GatewayEntity entity = null;
		try {
			if(id > 0){
				entity = gatewayService.getGatewayByHostId(id);
			}else{
				if(gatewayId != null){
					entity = gatewayService.getGatewayById(gatewayId);
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error("get gateway entity fail ", e);
		}
		gateway = entity;
	}
	
}
