package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;

public interface IConnectionContextProvider {
	
	ConnectionContext create() throws EdgeServiceFault;
	
	void updateUuid(String nodeUuid, String authUuid);

}
