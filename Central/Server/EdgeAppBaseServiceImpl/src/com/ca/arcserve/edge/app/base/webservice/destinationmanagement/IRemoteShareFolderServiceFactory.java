package com.ca.arcserve.edge.app.base.webservice.destinationmanagement;

import com.ca.arcserve.edge.app.base.webservice.IRemoteShareFolderService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface IRemoteShareFolderServiceFactory {

	IRemoteShareFolderService createRemoteShareFolderService(GatewayId gatewayId);

}
