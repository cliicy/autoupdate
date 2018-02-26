package com.ca.arcserve.edge.app.base.common.connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;

public abstract class Connection<P, S> implements AutoCloseable {
	
	private static Logger logger = Logger.getLogger(Connection.class);
	
	private IConnectionContextProvider contextProvider;
	private IWebServiceProvider<P> serviceProvider;
	
	private int connectTimeout;
	private int requestTimeout;
	
	private P clientProxy;
	private S service;
	
	private boolean autoUpdateUuid = true;
	
	public Connection(IConnectionContextProvider contextProvider, IWebServiceProvider<P> serviceProvider) {
		this.contextProvider = contextProvider;
		this.serviceProvider = serviceProvider;
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	
	public P getClientProxy() {
		return clientProxy;
	}
	
	public S getService() {
		return service;
	}
	
	public boolean isAutoUpdateUuid() {
		return autoUpdateUuid;
	}
	
	public void setAutoUpdateUuid(boolean autoUpdateUuid) {
		this.autoUpdateUuid = autoUpdateUuid;
	}
	
	public IConnectionContextProvider getContextProvider() {
		return contextProvider;
	}
	
	protected abstract S getService(P clientProxy);
	protected abstract void loginWithCredential(S service, ConnectionContext context);
	protected abstract void loginWithUuid(S service, ConnectionContext context);
	protected abstract String[] getServiceName();
	protected abstract String getMessageSubject();
	
	protected ConnectionContext createConnectionContext() throws EdgeServiceFault {
		ConnectionContext context = contextProvider.create();
		context.setConnectTimeout(connectTimeout);
		context.setRequestTimeout(requestTimeout);
		return context;
	}
	
	public void connect() throws EdgeServiceFault {
		connect(true);
	}
	
	public void connect(boolean login) throws EdgeServiceFault {
		ConnectionContext context = createConnectionContext();
		try {
			clientProxy = serviceProvider.getProxy(context);
			service = getService(clientProxy);
		
			if (!login) {
				return;
			}
		
			if (context.getAuthUuid() == null || context.getAuthUuid().isEmpty()) {
				loginWithCredential(service, context);
			} else {
				loginWithUuid(service, context);
			}
		} catch (Exception e) {
			if (isInvalidUuidException(e)) {
				logger.info("Login by uuid failed, use credential to login again. " + context);
				loginWithCredential(service, context);
			}else if(e instanceof WebServiceException){
				NodeExceptionUtil.convertWebServiceException(e, context, getMessageSubject(), getServiceName());
			}else if(e instanceof UndeclaredThrowableException){
				Throwable undeclare = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
				if(undeclare instanceof EdgeServiceFault){
					throw (EdgeServiceFault)undeclare;
				}else if (undeclare instanceof InvocationTargetException) {
					Throwable exception = ((InvocationTargetException) undeclare).getTargetException();
					if(exception instanceof WebServiceException){
						NodeExceptionUtil.convertWebServiceException(e, context, getMessageSubject(), getServiceName());
					}else if (exception instanceof EdgeServiceFault) {
						throw (EdgeServiceFault)exception;
					}
				}
			}
		}
	}
	
	protected boolean isInvalidUuidException(Exception e) {
		if (!(e instanceof SOAPFaultException)) {
			return false;
		}
		
		SOAPFaultException soapFaultException = (SOAPFaultException) e;
		String errorCode = soapFaultException.getFault().getFaultCodeAsQName().getLocalPart();
		
		return FlashServiceErrorCode.Login_WrongUUID.equalsIgnoreCase(errorCode);
	}

	@Override
	public void close() throws EdgeServiceFault {
		if (clientProxy != null) {
			serviceProvider.closeWsProxy(clientProxy);
			clientProxy = null;
			service = null;
		}
	}
}
