package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.webservice.client.IBaseService;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeCM4EdgeReport extends IBaseService,IEdgeConfigurationService{

}
