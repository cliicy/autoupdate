package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

/**
 * The service exposed by a gateway host. Console should not implement
 * this interface.
 * 
 * @author Bo.Pang
 *
 */
public interface IGatewayHostService
{
	void setHeartbeatInterval( int intervalInSecs );
	void doGatewayUpdate() throws EdgeServiceFault;
}
