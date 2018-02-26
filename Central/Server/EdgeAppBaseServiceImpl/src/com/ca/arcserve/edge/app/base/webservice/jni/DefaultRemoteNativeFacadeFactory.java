package com.ca.arcserve.edge.app.base.webservice.jni;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DefaultRemoteNativeFacadeFactory implements IRemoteNativeFacadeFactory {

	@Override
	public IRemoteNativeFacade createRemoteNativeFacade(GatewayId gatewayId) {
		return RemoteNativeFacadeImpl.getInstance();
	}

}
