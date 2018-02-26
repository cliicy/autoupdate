package com.ca.arcserve.edge.app.base.webservice.vcm;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeVCMService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;

public class VCMServiceImpl implements IEdgeVCMService {
	private IEdgeVCMDao vcmDao = DaoFactory.getDao(IEdgeVCMDao.class);
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	public VCMServiceImpl(){}
	
	//////////////////////////////////////////////////////////////////////////
	
	public VSphereProxyInfo getVSphereProxyInfoByHostId(int hostId)
			throws EdgeServiceFault {
		List<EdgeVSphereProxyInfo> proxyList = new ArrayList<EdgeVSphereProxyInfo>();
		vcmDao.as_edge_vsphere_proxy_getByHostId(hostId, proxyList);
		if(proxyList == null || proxyList.size()==0){
			return null;
		}else{
			EdgeVSphereProxyInfo daoProxyInfo = proxyList.get( 0 );

			VSphereProxyInfo proxyInfo = new VSphereProxyInfo();
			proxyInfo.setvSphereProxyId( daoProxyInfo.getId() );
			proxyInfo.setVSphereProxyName( daoProxyInfo.getHostname() );
			proxyInfo.setVSphereProxyUsername( daoProxyInfo.getUsername() );
			proxyInfo.setVSphereProxyPassword( daoProxyInfo.getPassword());
			proxyInfo.setVSphereProxyProtocol( Protocol.parse(daoProxyInfo.getProtocol()));
			proxyInfo.setVSphereProxyPort( daoProxyInfo.getPort() );
			proxyInfo.setVSphereProxyUuid( daoProxyInfo.getUuid() );
			proxyInfo.setvSphereProxyGatewayId(gatewayService.getGatewayByHostId(hostId).getId());
			return proxyInfo;
		}
	}
	
	@Override
	public void testMonitorConnection(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(monitorInfo)) {
			connection.connect();
		}
	}
	
	private D2DConnection createConnection(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		ConnectionContext context = new ConnectionContext(monitorInfo.getProtocol(), monitorInfo.getHostName(), monitorInfo.getPort());
		context.buildCredential(monitorInfo.getUserName(), monitorInfo.getPassword(), "");
		
		GatewayEntity gateway;
		
		if (monitorInfo.getGatewayId().isValid()) {
			gateway = gatewayService.getGatewayById(monitorInfo.getGatewayId());
		} else {
			gateway = gatewayService.getGatewayByHostId(monitorInfo.getHostId());
		}
		
		context.setGateway(gateway);
		
		return connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));
	}
	

	@Override
	public MonitorHyperVInfo getMonitorHyperVInfo(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(monitorInfo)) {
			connection.connect();
			
			boolean installed = connection.getService().isHyperVRoleInstalled();
			boolean validOS = connection.getService().isHostOSGreaterEqualW2K8SP2();
			String[] networks = connection.getService().getHypervNetworksFromMonitor("", "", "");
			String[] networkAdapterTypes = connection.getService().getHypervNetworkAdapterTypes();
			ESXServerInfo serverInfo = connection.getService().getHypervInfo("", "", "");
			
			MonitorHyperVInfo info = new MonitorHyperVInfo();
			info.setInstalled(installed);
			info.setValidOS(validOS);
			info.setNetworks(networks);
			info.setNetworkAdapterTypes(networkAdapterTypes);
			info.setServerInfo(serverInfo);
			
			return info;
		}
	}

	private String escapeNull(String str) {
		return str == null ? "" : str;
	}

	@Override
	public void validateSource(HostConnectInfo monitorInfo, String path, String domain, String user,
			String pwd, boolean isNeedCreateFolder) throws EdgeServiceFault {
		path = escapeNull(path);
		domain = escapeNull(domain);
		user = escapeNull(user);
		pwd = escapeNull(pwd);
		
		if (domain.trim().length() == 0) {
			int indx = user.indexOf('\\');
			if (indx > 0) {
				domain = user.substring(0, indx);
				user = user.substring(indx + 1);
			}
		}
		
		try (D2DConnection connection = createConnection(monitorInfo)) {
			connection.connect();
			
			connection.getService().validateSourceGenFolder(path, domain, user, pwd, isNeedCreateFolder);
		}
	}

	@Override
	public Volume[] getMonitorVolumes(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(monitorInfo)) {
			connection.connect();
			
			return connection.getService().getVolumesWithDetails(null, null, null);
		}
	}

	@Override
	public FileFolderItem getFiles(HostConnectInfo hostInfo,
			String parentFolder, String user, String password) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(hostInfo)) {
			connection.connect();
			
			return connection.getService().getFileFolderWithCredentials(parentFolder, user, password);
		}
	}

	@Override
	public Volume[] getVolumesByHostConnect(HostConnectInfo hostInfo) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(hostInfo)) {
			connection.connect();
			
			return connection.getService().getVolumes();
		}
	}

	@Override
	public boolean createFolderByHostConnect(HostConnectInfo hostInfo, String parentFolder, String folderName) throws EdgeServiceFault {
		try (D2DConnection connection = createConnection(hostInfo)) {
			connection.connect();
			
			return connection.getService().createFolder(parentFolder, folderName);
		}
	}

}
