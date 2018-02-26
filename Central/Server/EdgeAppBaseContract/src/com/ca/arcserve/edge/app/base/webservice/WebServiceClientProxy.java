package com.ca.arcserve.edge.app.base.webservice;

import javax.xml.ws.WebServiceException;

import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;



public class WebServiceClientProxy extends BaseWebServiceClientProxy {

	public WebServiceClientProxy(int port, String protocol,Object service,String host) {
		super(port,protocol,service,host);
	}
	public IEdgeService getService() {
		if(service instanceof IEdgeService)
		return (IEdgeService)service;
		throw new WebServiceException("IEdgeService is not supported");
	}
	public void setService(IEdgeService service) {
		this.service = service;
	}

	public IEdgeSRMService getSrmService(){
		if(service instanceof IEdgeSRMService)
			return (IEdgeSRMService)service;
			throw new WebServiceException("IEdgeSRMService is not supported");
	}


	public IActivityLogService getActivityLogService() {
		if (service instanceof IActivityLogService) {
			return (IActivityLogService)service;
		}

		throw new WebServiceException("IActivityLogService is not supported");
	}

	public IPolicyManagementService getPolicyManagementService()
	{
		if (service instanceof IPolicyManagementService)
			return (IPolicyManagementService)service;

		throw new WebServiceException( "IPolicyManagementService is not supported." );
	}



	public IEdgeD2DReSyncService getIEdgeD2DReSyncService(){
		if(service instanceof IEdgeD2DReSyncService)
			return (IEdgeD2DReSyncService)service;
			throw new WebServiceException("IEdgeD2DReSyncService is not supported");
	}



	public IEdgeD2DRegService getIEdgeD2DRegService(){
		if(service instanceof IEdgeD2DRegService)
			return (IEdgeD2DRegService)service;
			throw new WebServiceException("IEdgeD2DRegService is not supported");
	}
}

