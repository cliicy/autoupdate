package com.ca.arcserve.edge.app.base.webservice.vmwaremanagement;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface IVmwareManagerServiceFactory
{
	IVmwareManagerService createVmwareManagerService( GatewayId gatewayId );
}
