package com.ca.arcserve.edge.app.rps.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.rps.webservice.rps.IEdgeRPSRegService;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSDataStoreService;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSNodeService;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeRPSService extends IRPSNodeService, IEdgeRPSRegService, IRPSDataStoreService {

}
