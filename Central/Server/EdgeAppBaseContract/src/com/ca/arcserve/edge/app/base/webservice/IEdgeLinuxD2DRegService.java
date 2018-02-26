package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.LinuxD2DServerRegistrationResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;

public interface IEdgeLinuxD2DRegService {

	int unRegInfoToLinuxD2D(int d2dHostId, boolean forceFlag)throws EdgeServiceFault;
	LinuxD2DServerRegistrationResponse RegInfoToLinuxD2D(NodeRegistrationInfo registrationNodeInfo,boolean forceFlag,boolean clearExistingData)throws EdgeServiceFault;
	LinuxD2DServerRegistrationResponse RegInfoToLinuxD2D(
		GatewayId gatewayId, String hostName, int port, String protocol, String userName, String password,
		boolean forceFlag,boolean clearExistingData) throws EdgeServiceFault;
	void validateRegistrationInfo(NodeRegistrationInfo registrationNodeInfo,boolean forceFlag,boolean clearExistingData)throws EdgeServiceFault;
}
