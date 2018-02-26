package com.ca.arcserve.edge.app.base.common.connection;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.asbu.serviceinfo.ServiceInfoConstants;
import com.ca.asbu.webservice.IArchiveToTapeService;
import com.ca.asbu.webservice.WebServiceClientProxy;

public class ASBUConnection extends Connection<WebServiceClientProxy, IArchiveToTapeService>{
	private static final Logger logger = Logger.getLogger(ASBUConnection.class);
	public static final String ASBU_SERVICE_PATH = "/WebServiceImpl/services/ASBUServiceImpl";
	
	public ASBUConnection(IConnectionContextProvider contextProvider, IWebServiceProvider<WebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}
	

	@Override
	protected ConnectionContext createConnectionContext() throws EdgeServiceFault {
		ConnectionContext context =  super.createConnectionContext();
		context.buildServiceId(ServiceInfoConstants.getASBUARChiveToTapeServiceID());
		return context;
	}



	@Override
	protected IArchiveToTapeService getService(WebServiceClientProxy clientProxy) {
		return clientProxy.getArchiveToTapeService();
	}

	@Override
	protected void loginWithCredential(IArchiveToTapeService service, ConnectionContext context) {
		int authenticationType = context.getAuthenticationType();
		String username = context.getUsername();
		String domain = context.getDomain();
		String password = context.getPassword();
		/*if(authenticationType == ASBUAuthenticationType.WINDOWS.getValue()){
			if(!username.contains("\\")){
				username = domain + "\\" + username;
			}
		}*/
		if(logger.isDebugEnabled()){
			logger.debug("Start to connect server, host is " + context.getHost() + ", username is " + username +" , authentication type is " + authenticationType +" , domain is "+ domain);
		}
		service.login(context.getHost(), username, password, authenticationType);
	}

	@Override
	protected void loginWithUuid(IArchiveToTapeService service, ConnectionContext context) {
		throw new RuntimeException("this service doesn't have this connect method.");
	}


	@Override
	protected String[] getServiceName() {
		String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.ASBU_SERVICE_NAME;
		String disPlayServiceName = EdgeCMWebServiceMessages.getMessage("productNameASBU");
		return new String[]{realServiceName,disPlayServiceName};
	}


	@Override
	protected String getMessageSubject() {
		return NodeExceptionUtil.getAsbuMessageSubject();
	}
}
