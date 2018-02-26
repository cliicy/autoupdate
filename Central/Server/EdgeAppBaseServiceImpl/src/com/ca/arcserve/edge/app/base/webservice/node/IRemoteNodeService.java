package com.ca.arcserve.edge.app.base.webservice.node;

import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;

public interface IRemoteNodeService
{
	int validateProxyInfo( String hostName, String protocol, int port,
		String userName, String password,
		boolean isUseTimeRange, boolean isUseBackupSet );
	
	ResourcePool[] getResourcePool(
		VirtualCenter vc,
		ESXServer esxServer,
		ResourcePool parentResourcePool
		);
}
