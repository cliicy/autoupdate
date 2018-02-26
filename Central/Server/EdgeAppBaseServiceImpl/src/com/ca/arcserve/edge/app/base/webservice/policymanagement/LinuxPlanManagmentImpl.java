package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxPlanService;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class LinuxPlanManagmentImpl implements IEdgeLinuxPlanService {

	protected static final Logger logger = Logger.getLogger( LinuxPlanManagmentImpl.class );
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	@Override
	public List<String> getPrepostScriptList(int linuxD2DServerId)
			throws EdgeServiceFault {
		List<String> scriptList = new ArrayList<String>();
		ILinuximagingService service = null;
		EdgeConnectInfo d2dServer = null;
		d2dServer = getD2DServerConnectionInfo(linuxD2DServerId);
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(d2dServer.getHostid())){
			connection.connect();
			service = connection.getService();
			int ret = service.validateByKey(d2dServer.getAuthUuid());
			if(ret == 0){
				try{
					scriptList = service.getScripts(2);
				}catch(Exception e){
					logger.error("Failed to get prepos script",e);
				}
			}
		}catch(WebServiceException e){
			logger.error("cannot connect to Linux D2D service",e);
		}
		return scriptList;
	}

	@Override
	public boolean validateBackupLocation(int linuxD2DServerId,
			LinuxBackupLocationInfo locationInfo) throws EdgeServiceFault {
		boolean isValide = false;
		EdgeConnectInfo d2dServer = getD2DServerConnectionInfo(linuxD2DServerId);;
		ILinuximagingService service = null;
		int ret = -1;
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(d2dServer.getHostid())){
			connection.connect();
			service = connection.getService();
			ret = service.validateByKey(d2dServer.getAuthUuid());
			if(ret == 0){
				try{
					BackupLocationInfo info = new BackupLocationInfo();
					info.setBackupDestLocation(locationInfo.getBackupDestLocation());
					info.setBackupDestPasswd(locationInfo.getBackupDestPasswd());
					info.setBackupDestUser(locationInfo.getBackupDestUser());
					info.setType(locationInfo.getType());
					isValide = service.validateBackupLocation(info);
				}catch (SOAPFaultException e) {
					logger.error("validate backup location SOAPFaultException",e);
					String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
					if(FlashServiceErrorCode.Browser_PathNotFound.equals(errorCode) || FlashServiceErrorCode.Browser_Cifs_Share_Not_Exist.equals(errorCode)){
						EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_Linux_Destination_Not_Found,d2dServer.getRhostname());
						b.setMessageParameters(new String[]{d2dServer.getRhostname()});
						throw new EdgeServiceFault(d2dServer.getRhostname(),b);
					}else if(FlashServiceErrorCode.Browser_Cifs_Share_Wrong_Credential.equals(errorCode)){
						EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_Linux_Destination_Wrong_Credentials,d2dServer.getRhostname());
						b.setMessageParameters(new String[]{d2dServer.getRhostname()});
						throw new EdgeServiceFault(d2dServer.getRhostname(),b);
					}else if(FlashServiceErrorCode.Browser_No_Write_Permission.equals(errorCode)){
						EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_BrowserNoWritePermission,d2dServer.getRhostname());
						b.setMessageParameters(new String[]{d2dServer.getRhostname()});
						throw new EdgeServiceFault(d2dServer.getRhostname(),b);
					}else if(FlashServiceErrorCode.Browser_Cifs_Share_Common_Error.equals(errorCode)){
						EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_Linux_Destination_Common_ERROR,d2dServer.getRhostname());
						b.setMessageParameters(new String[]{d2dServer.getRhostname()});
						throw new EdgeServiceFault(d2dServer.getRhostname(),b);
					}else{
						throw e;
					}
				} catch(Exception e){
					logger.error("validate backup location excpetion",e);
				}
			}else{
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Managed_By_Others,d2dServer.getRhostname());
				b.setMessageParameters(new String[]{d2dServer.getRhostname()});
				throw new EdgeServiceFault(d2dServer.getRhostname(),b);
			}
		}catch(WebServiceException e){
			EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Linux_D2D_Server_Not_Reachable,d2dServer.getRhostname());
			b.setMessageParameters(new String[]{d2dServer.getRhostname()});
			throw new EdgeServiceFault(d2dServer.getRhostname(),b);
		}
		return isValide;
	}

	private EdgeConnectInfo getD2DServerConnectionInfo(int d2dServerId){
		List<EdgeHost> d2dServerList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(d2dServerId, 1, d2dServerList);
		
		EdgeHost d2dServer = d2dServerList.get(0);
		if(d2dServer == null){
			return null;
		}
		
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list(d2dServerId, connInfoLst);
		
		EdgeConnectInfo connectionInfo = connInfoLst.get(0);
		
		if(connectionInfo == null){
			return null;
		}
		
		connectionInfo.setRhostname(d2dServer.getRhostname());
		return connectionInfo;
	}
	
}
