package com.ca.arcserve.edge.app.base.common.service;

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfo;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.IEdgeService;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;

public class WebServiceProviderFactory {
	
	private static Logger logger = Logger.getLogger( WebServiceProviderFactory.class );
	
	private static final String CONSOLE_ENDPOINT = "EdgeServiceConsoleImpl";
		
	private static void closeService(Object service) {
		if (service instanceof Closeable) {
			try {
				((Closeable) service).close();
			} catch (IOException e) {
			}
		}
	}
	
	public static IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy>
		createConsoleServiceProvider()
	{
		return new ConsoleServiceProvider();
	}
	
	public static abstract class EdgeServiceProvider implements
		IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy>
	{
		private String className = "EdgeServiceProvider";
		private BaseWebServiceFactory serviceFactory = new BaseWebServiceFactory();
		
		@SuppressWarnings( "deprecation" )
		@Override
		public com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy
			getProxy(ConnectionContext context)
		{
			com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy webService = null;
			
			try
			{
				// Set ServiceInfo
				ServiceInfo serviceInfo = new ServiceInfo();
				serviceInfo.setBindingType(ServiceInfoConstants.SERVICE_BINDING_SOAP11);
				serviceInfo.setNamespace(ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE);
				serviceInfo.setPortName(this.getWSPortName());
				serviceInfo.setServiceName(this.getWSServiceName());
				
				String host = context.getHost();
				String protocol = context.getProtocol();
				int port = context.getPort();

				if (!protocol.endsWith(":")) {
					protocol = protocol + ":";
				}

				String wsdlURL = protocol + "//" + host + ":" + port
					+ CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH
					+ "/services/" + CONSOLE_ENDPOINT + "?wsdl";
				serviceInfo.setWsdlURL(wsdlURL);

				// Set ServiceInfoConstants
				String serviceID = ServiceInfoConstants.SERVICE_ID_EDGE_PROPER;

				webService = serviceFactory.getWebService(
					protocol, host, port, serviceID, serviceInfo, IEdgeService.class);
				
				if (webService == null)
					throw new Exception();
			}
			catch (Exception e)
			{
				logger.error( this.className + ": Failed to connect Edge. Connection context: " + context, e );
				throw new RuntimeException( e );
			}
			
			return webService;
		}
		
		@Override
		public void closeWsProxy(
			com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy proxy) throws EdgeServiceFault
		{
			try
			{
				// no logout method for IEdgeService yet
			}
			finally
			{
				closeService( proxy.getService() );
			}
		}
		
		protected abstract String getWSPortName();
		protected abstract String getWSServiceName();
	}
	
	public static class ConsoleServiceProvider extends EdgeServiceProvider
	{
		@Override
		protected String getWSPortName()
		{
			return ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_PORT_NAME;
		}

		@Override
		protected String getWSServiceName()
		{
			return ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME;
		}
	}
	
	public static class GatewayServiceProvider extends EdgeServiceProvider
	{
		@Override
		protected String getWSPortName()
		{
			return ServiceInfoConstants.SERVICE_EDGE_PROPER_PORT_NAME;
		}

		@Override
		protected String getWSServiceName()
		{
			return ServiceInfoConstants.SERVICE_EDGE_PROPER_SERVICE_NAME;
		}
	}
	
	public static IWebServiceProvider<WebServiceClientProxy> createD2DServiceProvider() {
		return new IWebServiceProvider<WebServiceClientProxy>() {

			@Override
			public WebServiceClientProxy getProxy(ConnectionContext context) {
				return WebServiceFactory.getFlassService(context.getProtocol(), context.getHost(), context.getPort(), 
						context.getServiceId(), context.getConnectTimeout(), context.getRequestTimeout());
			}

			@Override
			public void closeWsProxy(WebServiceClientProxy proxy) throws EdgeServiceFault {
				try {
					ID2D4EdgeService_Oolong service = proxy.getService(ID2D4EdgeService_Oolong.class);
					service.logout();
				} finally {
					closeService(proxy.getService());
				}
			}
			
		};
	}
	
	public static IWebServiceProvider<RPSWebServiceClientProxy> createRPSServiceProvider() {
		return new IWebServiceProvider<RPSWebServiceClientProxy>() {

			@Override
			public RPSWebServiceClientProxy getProxy(ConnectionContext context) {
				return RPSWebServiceFactory.getRPSService4CPM(context.getProtocol(), context.getHost(), context.getPort());
			}

			@Override
			public void closeWsProxy(RPSWebServiceClientProxy proxy)throws EdgeServiceFault {
				closeService(proxy.getServiceForCPM());
			}
			
		};
	}
	
	public static IWebServiceProvider<BaseWebServiceClientProxy> createLinuxD2DServiceProvider() {
		return new IWebServiceProvider<BaseWebServiceClientProxy>() {

			@Override
			public BaseWebServiceClientProxy getProxy(ConnectionContext context) {
				return CommonUtil.getLinuxD2DForEdgeService(context.getProtocol(), context.getHost(), context.getPort(), 
						context.getConnectTimeout(), context.getRequestTimeout());
			}

			@Override
			public void closeWsProxy(BaseWebServiceClientProxy proxy) throws EdgeServiceFault {
				closeService(proxy.getService());
			}
			
		};
	}
	
	public static IWebServiceProvider<com.ca.asbu.webservice.WebServiceClientProxy> createAsbuServiceProvider() {
		return new IWebServiceProvider<com.ca.asbu.webservice.WebServiceClientProxy>() {

			@Override
			public com.ca.asbu.webservice.WebServiceClientProxy getProxy(ConnectionContext context) {
				return com.ca.asbu.webservice.WebServiceFactory.getASBUService(
						context.getProtocol(), context.getHost(), context.getPort(), context.getServiceId());
			}

			@Override
			public void closeWsProxy(com.ca.asbu.webservice.WebServiceClientProxy proxy) throws EdgeServiceFault {
				closeService(proxy.getArchiveToTapeService());
			}
			
		};
	}
	
	public static IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy> createOldAsbuServiceProvider() {
		return new IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy>() {

			@Override
			public com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy getProxy(ConnectionContext context) {
				return com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceFactory.getOldASBUService(
						context.getHost(), context.getPort());
			}

			@Override
			public void closeWsProxy(com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy proxy) throws EdgeServiceFault {
				closeService(proxy.getService());
			}
			
		};
	}
	
	
	public static IWebServiceProvider<com.ca.arcflash.webservice.WebServiceClientProxy> createInstantVMServiceProvider() {
		return new IWebServiceProvider<com.ca.arcflash.webservice.WebServiceClientProxy>() {

			@Override
			public com.ca.arcflash.webservice.WebServiceClientProxy getProxy(ConnectionContext context) {
				return com.ca.arcflash.webservice.WebServiceFactory.getInstantVMService(
						context.getProtocol(), context.getHost(), context.getPort(), context.getServiceId(), 0, 0);
			}

			@Override
			public void closeWsProxy(com.ca.arcflash.webservice.WebServiceClientProxy proxy) throws EdgeServiceFault {
				closeService(proxy.getInstantVMService());
			}
			
		};
	}
}
