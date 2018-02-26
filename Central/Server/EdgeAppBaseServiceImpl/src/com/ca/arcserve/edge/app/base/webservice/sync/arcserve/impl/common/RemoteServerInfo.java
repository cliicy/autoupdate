package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class RemoteServerInfo {
	
	private Logger logger = Logger.getLogger( RemoteServerInfo.class );
	
	private NativeFacade nativeFacade = null;
	private IEdgeGatewayLocalService gatewayService =  EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public synchronized static RemoteServerInfo getInstance()
	{
		RemoteServerInfo instance = new RemoteServerInfo();
		instance.nativeFacade = new NativeFacadeImpl();
		return instance;
		
	}
	
	public RemoteNodeInfo getNodeInfo(EdgeHost host, EdgeArcserveConnectInfo arcInfo, EdgeConnectInfo conInfo)
	{	
		try {
			
			RemoteNodeInfo remInfo = new RemoteNodeInfo();
			
			synchronized(ABFuncServiceImpl.class)
			{
				ABFuncServiceImpl abImpl = new ABFuncServiceImpl(host.getRhostname(), arcInfo.getPort());
				GatewayEntity gateway = gatewayService.getGatewayByHostId(host.getRhostid());
				String strSessionNo = abImpl.ConnectARCserve(gateway, arcInfo
						.getCauser(), arcInfo.getCapasswd(), ABFuncAuthMode
						.values()[arcInfo.getAuthmode()]);
				ABFuncServerType aRCserveType = abImpl.GetServerType(strSessionNo);
				 
				remInfo.setARCserveType(aRCserveType);
				
				if(aRCserveType == ABFuncServerType.GDB_PRIMARY_SERVER)
				{				
					Boolean bBranch = abImpl.IsArcserveBranch(strSessionNo);
					if(bBranch)
						remInfo.setGdbType(GDBServerType.GDB_IN_BRANCH);
					else
						remInfo.setGdbType(GDBServerType.GDB_REGULAR);
				}
			}
			
			return remInfo;
		} catch (Exception e) {			
			logger.error( "getNodeInfo(): Error getting GDB server type.", e );
		}	
		
		GatewayEntity gateway = null;
		
		try
		{
			gateway = gatewayService.getGatewayByHostId( host.getRhostid() );
		}
		catch (Exception e)
		{
			logger.error( "getNodeInfo(): Error getting gateway information.", e );
		}
		
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade remoteNativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gateway.getId() );
		
		EdgeAccount acc = this.nativeFacade.getEdgeAccount();
		String edgeUser = acc.getUserName();
		String edgePassword = acc.getPassword();
		String edgeDomain = acc.getDomain();
		try {
			return remoteNativeFacade.scanRemoteNode(edgeUser, edgeDomain,
					edgePassword, host.getRhostname(), conInfo.getUsername(),
					conInfo.getPassword());
		} catch (EdgeServiceFault e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
			return null;
		}
		
	}
	
}
