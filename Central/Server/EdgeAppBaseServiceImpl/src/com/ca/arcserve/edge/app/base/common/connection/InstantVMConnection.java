package com.ca.arcserve.edge.app.base.common.connection;

import org.apache.log4j.Logger;

import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.IInstantVMService;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;

public class InstantVMConnection extends Connection<WebServiceClientProxy, IInstantVMService>{
	private static final Logger logger = Logger.getLogger(InstantVMConnection.class);
	public InstantVMConnection(IConnectionContextProvider contextProvider, IWebServiceProvider<WebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}
	@Override
	protected ConnectionContext createConnectionContext() throws EdgeServiceFault {
		ConnectionContext context =  super.createConnectionContext();
		context.buildServiceId(ServiceInfoConstants.SERVICE_ID_INSTANT_VM_SERVICE);
		return context;
	}
	@Override
	protected IInstantVMService getService(WebServiceClientProxy clientProxy) {
		return clientProxy.getInstantVMService();
	}
	@Override
	protected void loginWithCredential(IInstantVMService service, ConnectionContext context) {
		String username = context.getUsername();
		String domain = context.getDomain();
		String password = context.getPassword();
		if(logger.isDebugEnabled()){
			logger.debug("Start to connect server, host is " + context.getHost() + ", username is " + username +" , domain is "+ domain);
		}
		service.validateUser(username, password, domain);
	}
	@Override
	protected void loginWithUuid(IInstantVMService service, ConnectionContext context) {
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
		return EdgeCMWebServiceMessages.getMessage("node");
	}
}
