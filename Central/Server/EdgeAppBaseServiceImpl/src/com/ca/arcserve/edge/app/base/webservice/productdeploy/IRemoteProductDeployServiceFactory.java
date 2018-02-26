package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import com.ca.arcserve.edge.app.base.webservice.IRemoteProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

public interface IRemoteProductDeployServiceFactory {
	IRemoteProductDeployService createRemoteProductDeployService(GatewayEntity gatewayEntity);
}
