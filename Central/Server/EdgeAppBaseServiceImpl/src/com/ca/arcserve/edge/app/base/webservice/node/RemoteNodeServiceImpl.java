package com.ca.arcserve.edge.app.base.webservice.node;

import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;

public class RemoteNodeServiceImpl implements IRemoteNodeService
{

	@Override
	public int validateProxyInfo( String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet )
	{
		FlashServiceImpl d2dService = new FlashServiceImpl();
		return d2dService.validateProxyInfo_NoSessionCheck(
			hostName, protocol, port, userName, password, isUseTimeRange, isUseBackupSet );
	}

	@Override
	public ResourcePool[] getResourcePool( VirtualCenter vc,
		ESXServer esxServer, ResourcePool parentResourcePool )
	{
		FlashServiceImpl d2dService = new FlashServiceImpl();
		d2dService.setLocalCheckSession( true );
		return d2dService.getResourcePool( vc, esxServer, parentResourcePool );
	}

}
