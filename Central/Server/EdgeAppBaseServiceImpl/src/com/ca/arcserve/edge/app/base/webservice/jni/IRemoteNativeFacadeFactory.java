package com.ca.arcserve.edge.app.base.webservice.jni;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface IRemoteNativeFacadeFactory {
	
	IRemoteNativeFacade createRemoteNativeFacade(GatewayId gatewayId);

}
