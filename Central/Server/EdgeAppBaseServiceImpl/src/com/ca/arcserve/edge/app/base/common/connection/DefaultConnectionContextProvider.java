package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;

public class DefaultConnectionContextProvider implements IConnectionContextProvider {
	
	private ConnectionContext context;
	
	public DefaultConnectionContextProvider(ConnectionContext context) {
		this.context = context;
	}

	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		return context;
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
		// do nothing
	}

}
