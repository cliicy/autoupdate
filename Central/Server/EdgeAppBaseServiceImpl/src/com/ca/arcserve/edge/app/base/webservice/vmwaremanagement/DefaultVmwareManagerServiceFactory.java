package com.ca.arcserve.edge.app.base.webservice.vmwaremanagement;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DefaultVmwareManagerServiceFactory implements IVmwareManagerServiceFactory
{

	@Override
	public IVmwareManagerService createVmwareManagerService( GatewayId gatewayId )
	{
		return new VmwareManagerServiceImpl();
	}

}
