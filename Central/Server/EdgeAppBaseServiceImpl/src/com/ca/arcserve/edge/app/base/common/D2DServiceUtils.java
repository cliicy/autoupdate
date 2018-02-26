package com.ca.arcserve.edge.app.base.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;

public class D2DServiceUtils {
	
	public static class D2DServiceConnectInfo {
		public int nodeid;
		public String protocol;
		public String hostname;
		public int port;
		public String uuid;
		public String username;
		public String password;
		public ProtectionType protectionType; 
		public String authuuid;
	}
	
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	
	public static D2DServiceConnectInfo getD2DConnectInfo(int nodeId) throws EdgeServiceFault {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, hostList);
		if (hostList.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND, "Cannot find the node by id.");
		}
		
		List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(nodeId, connectInfoList);
		if (connectInfoList.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND, "Cannot find the D2D connection info by node id.");
		}
		
		D2DServiceConnectInfo info = new D2DServiceConnectInfo();
		
		info.nodeid = nodeId;
		info.protocol = connectInfoList.get(0).getProtocol() == 1 ? "http" : "https";
		info.hostname = hostList.get(0).getRhostname();
		info.port = connectInfoList.get(0).getPort();
		info.uuid = connectInfoList.get(0).getUuid();
		info.authuuid = connectInfoList.get(0).getAuthUuid();
		info.username = connectInfoList.get(0).getUsername();
		info.password = connectInfoList.get(0).getPassword();
		//info.protectionType = hostList.get(0).getProtectionTypeBitmap() == 1? ProtectionType.WIN_D2D : ProtectionType.RPS ;
		switch (hostList.get(0).getProtectionTypeBitmap()) {
		case 0x00000001:
			info.protectionType = ProtectionType.WIN_D2D;
			break;
		case 0x00000002:
			info.protectionType = ProtectionType.BAB;
			break;
		case 0x00000004:
			info.protectionType = ProtectionType.RPS;
			break;
		case 0x00000008:
			info.protectionType = ProtectionType.Restore;
			break;
		case 0x00000010:
			info.protectionType = ProtectionType.Conversion;
			break;
		case 0x00000020:
			info.protectionType = ProtectionType.Replication;
			break;
		case 0x00000040:
			info.protectionType = ProtectionType.Unprotected;
			break;
		case 0x00000080:
			info.protectionType = ProtectionType.LINUX_D2D_SERVER;
			break;
		case 0x00000100:
			info.protectionType = ProtectionType.ASBUServer;
			break;	
		default:
			info.protectionType = ProtectionType.Unprotected;
			break;
		}		
		return info;
	}
	
	public static RPSConnection createRPSService( D2DServiceConnectInfo rpsConnInfo ) throws EdgeServiceFault {
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		RPSConnection connection = connectionFactory.createRPSConnection(rpsConnInfo.nodeid);
		connection.connect();
		return connection;
	}
	
	public static LinuxD2DConnection createLinuxService(D2DServiceConnectInfo linuxConnInfo) throws EdgeServiceFault {  
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(linuxConnInfo.nodeid);
		connection.connect();
		return connection;
	}
	public static String[] parseUsername(String username) {
		String[] result = new String[2];
		result[0] = "";
		result[1] = username;
		
		if (username == null || username.isEmpty()) {
			return result;
		}
		
		int pos = username.indexOf("\\");
		if (pos != -1) {
			result[0] = username.substring(0, pos);
			result[1] = username.substring(pos + 1);
		}
		
		return result;
	}
	
	public static String getD2DErrorMessage(SOAPFaultException e) {
		String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
		return D2DWebServiceErrorMessages.getMessage(errorCode);
	}

}
