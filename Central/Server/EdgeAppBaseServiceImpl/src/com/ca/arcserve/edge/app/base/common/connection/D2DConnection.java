package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;

public class D2DConnection extends Connection<WebServiceClientProxy, ID2D4EdgeService_Oolong> {
	
	private NativeFacade d2dNativeFacade = new NativeFacadeImpl();
	
	public D2DConnection(IConnectionContextProvider contextProvider, IWebServiceProvider<WebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}
	
	@Override
	protected ConnectionContext createConnectionContext() throws EdgeServiceFault {
		ConnectionContext context = super.createConnectionContext();
		context.buildServiceId(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_OOLONG);
		return context;
	}
	
	@Override
	protected ID2D4EdgeService_Oolong getService(WebServiceClientProxy clientProxy) {
		return clientProxy.getService(ID2D4EdgeService_Oolong.class);
	}

	@Override
	protected void loginWithCredential(ID2D4EdgeService_Oolong service, ConnectionContext context) {
		String nodeUuid = service.login(context.getUsername(), context.getPassword(), context.getDomain());
		if (!isAutoUpdateUuid()) {
			return;
		}
		
		String encryptedAuthUuid = service.EstablishTrust(context.getUsername(), context.getPassword(), context.getDomain());
		String authUuid = d2dNativeFacade.decrypt(encryptedAuthUuid);
		
		getContextProvider().updateUuid(nodeUuid, authUuid);
	}

	@Override
	protected void loginWithUuid(ID2D4EdgeService_Oolong service, ConnectionContext context) {
		service.validateUserByUUID(context.getAuthUuid());
	}
	
	public String getNodeUuid() throws EdgeServiceFault {
		return getService().getNodeID();
	}
	
	public String getAuthUuid() throws EdgeServiceFault {
		ConnectionContext context = createConnectionContext();
		
		if (context.getAuthUuid() != null && !context.getAuthUuid().isEmpty()) {
			return context.getAuthUuid();
		}
		
		String encryptedAuthUuid = getService().EstablishTrust(context.getUsername(), context.getPassword(), context.getDomain());
		return d2dNativeFacade.decrypt(encryptedAuthUuid);
	}

	@Override
	protected String[] getServiceName() {
		String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.AGENT_SERVICE_NAME;
		String displayName = EdgeCMWebServiceMessages.getMessage("productNameAgent");
		return new String[]{realServiceName,displayName};
	}

	@Override
	protected String getMessageSubject() {
		return NodeExceptionUtil.getNodeMessageSubject();
	}
}
