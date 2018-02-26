package com.ca.arcserve.edge.app.base.webservice.destinationmanagement;

import com.ca.arcserve.edge.app.base.webservice.IRemoteShareFolderService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DefaultRemoteShareFolderServiceFactory implements IRemoteShareFolderServiceFactory{


	@Override
	public IRemoteShareFolderService createRemoteShareFolderService(GatewayId gatewayId) {
		// TODO Auto-generated method stub
		return new RemoteShareFolderServiceImpl();
	}

}
