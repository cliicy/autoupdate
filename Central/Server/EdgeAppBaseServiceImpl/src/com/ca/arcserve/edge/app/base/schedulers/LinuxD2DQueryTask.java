package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.QueryD2DStatusJob.Status;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.VersionInfo;

public class LinuxD2DQueryTask implements Runnable {
	private static Logger log = Logger.getLogger(LinuxD2DQueryTask.class);
	EdgeHost host;
	IEdgeConnectInfoDao connectDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	public LinuxD2DQueryTask(EdgeHost host) {
		this.host = host;
	}
	
	@Override
	public void run() {
		log.debug(Utils.getMessage("QueryLinuxD2DStatusJob's QueryTask running, hostname:{0}", host.getRhostname()));
		try(LinuxD2DConnection connection = connectLinuxD2D()){
			ILinuximagingService service = connection.getService();
			if(service == null){
				updateStatus(Status.ERRORWEBSERVICE);
			}else{
				int ret = 0;
				try{
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					connectDao.as_edge_connect_info_list(host.getRhostid(), connInfoLst);
					ret = service.validateByKey(connInfoLst.get(0).getAuthUuid()); //0. managed -1.not managed by this console
				}catch (WebServiceException e) {
					ret = -2;
					updateStatus( Status.ERRORWEBSERVICE);
				}
				if(ret != -2){
					NodeManagedStatus managedStatus;
					if(ret != -1){
						updateStatus( Status.FIT);
						managedStatus = NodeManagedStatus.Managed;
					} else {
						updateStatus(Status.ERROR_D2D_CANNOT_ACCESS_EDGE);  //-1. node is not managed by this console.
																			//ERROR_D2D_CANNOT_ACCESS_EDGE: agent can not connect to console. In this case, this node is not managed by current console.
						managedStatus = NodeManagedStatus.Unmanaged;
					}
					if(ret != 0){
						managedStatus = NodeManagedStatus.Unmanaged;
					}else{
						VersionInfo versionInfo = service.getVersionInfo();
						if(isTimezoneChanged(versionInfo.getTimeZoneOffset() , host.getTimezone())){
							IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
							hostMgrDao.as_edge_host_update_timezone_by_id(host.getRhostid(), versionInfo.getTimeZoneOffset());
						}
						checkVersionChanged(versionInfo);
					}
					connectDao.as_edge_connect_update_managedStatus(host.getRhostid(), managedStatus.ordinal());
				}
			}
		} catch (EdgeServiceFault e1) {
			log.error(e1);
		}
		
	}

	private boolean isTimezoneChanged(int newTimezone,int oldTimezone){
		return newTimezone != oldTimezone;
	}
	
	private void checkVersionChanged(VersionInfo versionInfo){
		String oldMajorVersion = host.getD2DMajorversion();
		String oldMinorVersion = host.getD2dMinorversion();
		String oldBuildNumber = host.getD2dBuildnumber();
		
		String majorVersion = "";
		String minorVersion = "";
		String buildNumber = "";
		if(versionInfo.getVersion()!=null){
			String version = versionInfo.getVersion();
			if(version.contains(".")){
				String[] versionArray = version.split("\\.");
				majorVersion = versionArray[0];
				minorVersion = versionArray[1];
			}
		}
		if(versionInfo.getBuildNumber()!=null){
			buildNumber = versionInfo.getBuildNumber();
		} 
		
		if(!majorVersion.equals(oldMajorVersion) || !minorVersion.equals(oldMinorVersion) || !buildNumber.equals(oldBuildNumber)){
			connectDao.as_edge_connect_info_update_version(host.getRhostid(), majorVersion, minorVersion, "", buildNumber);
		}
	}
	
	private void updateStatus(Status status){
		IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		hostDao.as_edge_update_D2D_status(host.getRhostid(), status.ordinal(), 0);
	}
	
	private LinuxD2DConnection connectLinuxD2D() throws EdgeServiceFault{
		LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(host.getRhostid());
		connection.connect();
		return connection;
	}
	
}
