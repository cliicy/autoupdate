package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IEdgeService;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;

public class ConsoleConnection extends Connection<BaseWebServiceClientProxy, IEdgeService>
{

	public ConsoleConnection( IConnectionContextProvider contextProvider,
		IWebServiceProvider<BaseWebServiceClientProxy> serviceProvider )
	{
		super( contextProvider, serviceProvider );
	}

	@Override
	protected IEdgeService getService( BaseWebServiceClientProxy clientProxy )
	{
		return (IEdgeService) clientProxy.getService();
	}

	@Override
	protected void loginWithCredential(
		IEdgeService service, ConnectionContext context )
	{
		//service.validateUserByUser( context.getUsername(), context.getPassword(), context.getDomain() );
	}

	@Override
	protected void loginWithUuid(
		IEdgeService service, ConnectionContext context )
	{
		//service.validateUserByUUID( context.getAuthUuid() );
	}

	@Override
	protected String[] getServiceName() {
		return null;
	}

	@Override
	protected String getMessageSubject() {
		return EdgeCMWebServiceMessages.getMessage("productNameUPM");
	}
}
