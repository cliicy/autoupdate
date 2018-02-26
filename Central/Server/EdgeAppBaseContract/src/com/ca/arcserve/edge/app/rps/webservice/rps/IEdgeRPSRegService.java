package com.ca.arcserve.edge.app.rps.webservice.rps;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;

public interface IEdgeRPSRegService {
	/**
	 * Register RPS node to RPS Server<br>
	 * After registration one xml file will be added/modified in RPS Server
	 * @param nodeId
	 * @param forceFlag if remove other Edge-Rps info in RPS Server and manage this RPS Server force
	 * @throws EdgeServiceFault
	 */
	void UpdateRegInfoToRpsServer(ConnectionContext context, int nodeId, boolean forceFlag)throws EdgeServiceFault;
	/**
	 * Remove registration info in RPS Server
	 * @param nodeId
	 * @param forceFlag
	 * @throws EdgeServiceFault
	 */
	void RemoveRegInfoFromRpsServer(RpsNode node,RpsConnectionInfo conInfo, boolean forceFlag)throws EdgeServiceFault;
	
}
