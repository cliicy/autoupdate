package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.ProxyConnectInfo;

@WebService(targetNamespace="http://webservice.d2dreg.edge.arcserve.ca.com/")
public interface IEdgeD2DRegService {
	void UpdateRegInfoToD2D(ConnectionContext context, int d2dHostId, boolean forceFlag)throws EdgeServiceFault;
	void RemoveRegInfoFromD2D(int d2dHostId, boolean forceFlag)throws EdgeServiceFault;
	void UpdateRegInfoToProxy(ProxyConnectInfo proxyConnectInfo, boolean forceFlag)
			throws EdgeServiceFault;
}
