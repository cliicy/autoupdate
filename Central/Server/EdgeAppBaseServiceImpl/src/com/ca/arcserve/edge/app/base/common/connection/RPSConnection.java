package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;

public class RPSConnection extends Connection<RPSWebServiceClientProxy, IRPSService4CPM> {
	
	private NativeFacade d2dNativeFacade = new NativeFacadeImpl();
	
	public RPSConnection(IConnectionContextProvider contextProvider, IWebServiceProvider<RPSWebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}

	@Override
	protected IRPSService4CPM getService(RPSWebServiceClientProxy clientProxy) {
		return clientProxy.getServiceForCPM();
	}

	@Override
	protected void loginWithCredential(IRPSService4CPM service, ConnectionContext context) {
		String nodeUuid = service.login(context.getUsername(), context.getPassword(), context.getDomain());
		if (!isAutoUpdateUuid()) {
			return;
		}
		
		String encryptedAuthUuid = service.establishTrust(context.getUsername(), context.getPassword(), context.getDomain());
		String authUuid = d2dNativeFacade.decrypt(encryptedAuthUuid);
		
		getContextProvider().updateUuid(nodeUuid, authUuid);
	}

	@Override
	protected void loginWithUuid(IRPSService4CPM service, ConnectionContext context) {
		service.validateUserByUUID(context.getAuthUuid());
	}

	@Override
	protected String[] getServiceName() {
		String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.AGENT_SERVICE_NAME;
		String displayName = EdgeCMWebServiceMessages.getMessage("productNameAgent");
		return new String[]{realServiceName,displayName};
	}

	@Override
	protected String getMessageSubject() {
		return NodeExceptionUtil.getRpsMessageSubject();
	}
}
