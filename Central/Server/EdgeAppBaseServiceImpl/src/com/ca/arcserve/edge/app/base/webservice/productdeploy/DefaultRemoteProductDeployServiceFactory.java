package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import com.ca.arcserve.edge.app.base.webservice.IRemoteProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

public class DefaultRemoteProductDeployServiceFactory implements IRemoteProductDeployServiceFactory{

	@Override
	public IRemoteProductDeployService createRemoteProductDeployService(
			GatewayEntity gatewayEntity) {
		return new RemoteProductDeployServiceImpl();
	}

}
