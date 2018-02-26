package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.common.SimpleCacheWebServiceManager;
import com.ca.arcserve.edge.app.base.common.service.WebServiceProviderFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;

public class CachedConnectionFactory implements IConnectionFactory {
	
	@Override
	public D2DConnection createD2DConnection(IConnectionContextProvider contextProvider) {
		return new D2DConnection(contextProvider, SimpleCacheWebServiceManager.getInstance().getProviderByProxyType(WebServiceClientProxy.class));
	}
	
	@Override
	public D2DConnection createD2DConnection(int nodeId) {
		return createD2DConnection(new NodeConnectionContextProvider(nodeId));
	}
	
	@Override
	public D2DConnection createVMBackupProxyConnection(EdgeD2DHost proxy) {
		D2DConnection connection = createD2DConnection(new VMProxyConnectionContextProvider(proxy));
		connection.setAutoUpdateUuid(false);
		return connection;
	}
	
	@Override
	public RPSConnection createRPSConnection(IConnectionContextProvider contextProvider) {
		return new RPSConnection(contextProvider, SimpleCacheWebServiceManager.getInstance().getProviderByProxyType(RPSWebServiceClientProxy.class));
	}

	@Override
	public RPSConnection createRPSConnection(int rpsNodeId) {
		return createRPSConnection(new NodeConnectionContextProvider(rpsNodeId));
	}
	
	@Override
	public LinuxD2DConnection createLinuxD2DConnection(IConnectionContextProvider contextProvider) {
		return new LinuxD2DConnection(contextProvider, SimpleCacheWebServiceManager.getInstance().getProviderByProxyType(BaseWebServiceClientProxy.class));
	}

	@Override
	public LinuxD2DConnection createLinuxD2DConnection(int nodeId) {
		return createLinuxD2DConnection(new NodeConnectionContextProvider(nodeId));
	}

	@Override
	public ASBUConnection createASBUConnection(IConnectionContextProvider contextProvider) {
//		return new ASBUConnection(contextProvider, SimpleCacheWebServiceManager.getInstance().getProviderByProxyType(com.ca.asbu.webservice.WebServiceClientProxy.class));
		return new ASBUConnection(contextProvider, WebServiceProviderFactory.createAsbuServiceProvider());
	}

	@Override
	public ASBUConnection createASBUConnection(int nodeId) {
		return createASBUConnection(new ASBUConnectionContextProvider(nodeId));
	}
	
	@Override
	public OldASBUConnection createOldASBUConnection(
			IConnectionContextProvider contextProvider) {
		return new OldASBUConnection(contextProvider, WebServiceProviderFactory.createOldAsbuServiceProvider());
	}

	@Override
	public InstantVMConnection createInstantVMConnection(
			IConnectionContextProvider contextProvider) {
		return new InstantVMConnection(contextProvider, WebServiceProviderFactory.createInstantVMServiceProvider());
	}

	@Override
	public InstantVMConnection createInstantVMConnection(int nodeId) {
		return createInstantVMConnection(new NodeConnectionContextProvider(nodeId));
	}

	@Override
	public ConsoleConnection createConsoleConnection( IConnectionContextProvider contextProvider )
	{
		return new ConsoleConnection( contextProvider, EdgeFactory.getConsoleServiceProvider() );
	}

	@Override
	public ConsoleConnection createConsoleConnection( ConnectionContext context )
	{
		return createConsoleConnection( new DefaultConnectionContextProvider( context ) );
	}
}
