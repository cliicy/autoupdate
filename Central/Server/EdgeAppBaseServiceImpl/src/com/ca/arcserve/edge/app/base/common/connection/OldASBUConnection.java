package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.abintegration.IABFuncService;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy;

public class OldASBUConnection extends Connection<ABFunWebServiceClientProxy, IABFuncService>{
	private static String serviceId = "IABFuncService";
	public OldASBUConnection(IConnectionContextProvider contextProvider,
			IWebServiceProvider<ABFunWebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}
	
	@Override
	protected ConnectionContext createConnectionContext() throws EdgeServiceFault {
		ConnectionContext context =  super.createConnectionContext();
		context.buildServiceId(serviceId);
		return context;
	}



	@Override
	protected IABFuncService getService(ABFunWebServiceClientProxy clientProxy) {
		return clientProxy.getService();
	}

	@Override
	protected void loginWithCredential(IABFuncService service, ConnectionContext context) {
		//No need to login
	}

	@Override
	protected void loginWithUuid(IABFuncService service, ConnectionContext context) {
		//No need to login
	}


	@Override
	protected String[] getServiceName() {
		return null;
	}


	@Override
	protected String getMessageSubject() {
		return EdgeCMWebServiceMessages.getMessage("node");
	}

}
