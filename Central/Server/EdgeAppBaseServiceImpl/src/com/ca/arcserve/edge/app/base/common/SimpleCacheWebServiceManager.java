package com.ca.arcserve.edge.app.base.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcserve.edge.app.base.common.connection.ASBUConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceManager;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.asbu.webservice.IArchiveToTapeService;

public class SimpleCacheWebServiceManager implements IWebServiceManager {
	
	public static interface IConnectionContextBinder {
		void bind(ConnectionContext context);
	}

	private static SimpleCacheWebServiceManager instance = new SimpleCacheWebServiceManager();;
	private static Logger logger = Logger.getLogger(SimpleCacheWebServiceManager.class); 
	//init eager
	public static SimpleCacheWebServiceManager getInstance() {
		return instance;
	}
	
	private SimpleCacheWebServiceManager(){
		rpsCreator.initCaches(); 
		linuxCreator.initCaches();
		agentCreator.initCaches();
//		asbuCreator.initCaches();
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized<P>  IWebServiceProvider<P> getProviderByProxyType(
			Class<P> proxy_Class ) {
		if( proxy_Class == WebServiceClientProxy.class ) {
			return  (IWebServiceProvider<P>) agentCreator;
		}
		else if( proxy_Class == RPSWebServiceClientProxy.class ) {
			return (IWebServiceProvider<P>) rpsCreator;
		}
		else if( proxy_Class == BaseWebServiceClientProxy.class ) {
			return (IWebServiceProvider<P>) linuxCreator;
		}
		else if( proxy_Class == com.ca.asbu.webservice.WebServiceClientProxy.class ) {
			return (IWebServiceProvider<P>) asbuCreator;
		}
		else {
			return null;
		}
	}

	private AgentWSCreator agentCreator = new AgentWSCreator( 7,12 );
	private RPSWSCreator rpsCreator = new RPSWSCreator( 2,3 );
	private LinuxCreator linuxCreator = new LinuxCreator( 1,2 );
	private AsbuCreator asbuCreator = new AsbuCreator( 2,5 );
	public synchronized void destory() {
		rpsCreator.destory(); 
		linuxCreator.destory();
		agentCreator.destory();
		asbuCreator.destory();
	}

	private static class CacheEntry<T> {
		T wsproxy;
		@SuppressWarnings("unused")
		boolean isCore = false; //actually is no use now;
		@SuppressWarnings("unused")
		Date lastOutCacheTime;  //used for monitor; not implement it now
		@SuppressWarnings("unused")
		String toWhich;
	}
	
	private abstract class BaseWSCreator<P> implements IWebServiceProvider<P>{
		protected int InitSize;
		protected int maxSize;
		private List<CacheEntry<P>> cachedProxys = new ArrayList<CacheEntry<P>>(); 
		private Map<P, CacheEntry<P>> using = new ConcurrentHashMap<P, CacheEntry<P>>();
		
		protected Object cacheListMutex = new Object(); 
		
		/**
		 *now we use existing webserviceFactory to create webservice port;  but every time the factory is called; it recreate a new Service object and
		 *a new Port object; actually the Service object can reuse, so only port object need recreate! so current implementation may cause excess memory 
		 *usage; but it not cause memory leak, So i don't change the factory code( Actually, some factory is written by other people in the jar package,I cannot change it! ) 
		 *
		 */
		abstract CacheEntry<P> getNewProxyCacheEntry();
		protected abstract void configureProxy( P proxy, ConnectionContext context );
		/**
		 * close the stub only when the stub is not return to cache( created when concurrent size exceed the max cache size);
		 * because if we close a stub, it cannot reuse in future;
		 * @param proxy
		 * @param isReturnToPool
		 * @throws IOException
		 */
		protected abstract void doClose( P proxy, boolean isReturnToPool ) throws IOException; 
		
		public BaseWSCreator(int initSize, int maxSize) {
			this.InitSize = initSize;
			this.maxSize = maxSize;
		}
		public void initCaches() {
			//use only 1 thread to do init to reduce resource cost; 
			//this init operation not block getProxy() operation; if excess proxy be created because of getProxy() called before init finish; the 
			//excess proxy will only used by the caller, it will not return to cache if the current cache size is bigger than max size; 
			Thread initThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for (int i = 0; i < InitSize; i++) {							
							CacheEntry<P> entry = getNewProxyCacheEntry();
							//entry.isCore = true; 
							synchronized(cacheListMutex) {
								cachedProxys.add(entry);
							}
						}
					}
					catch( Exception e ) {
						logger.error("SimpleCachewebServiceManager: init error " , e); 
					}
				}
			});
			initThread.start();
		}
		@Override 
		public P getProxy(ConnectionContext context) {
			P proxy =  innerGetProxy(1);
			configureProxy(proxy, context);
			return proxy;
		}
		private  P innerGetProxy( int depth ) {
			try {
				synchronized(cacheListMutex) {
					if( cachedProxys.size() >=1 ) {
						CacheEntry<P> entry = cachedProxys.remove( cachedProxys.size()-1 );
						using.put(entry.wsproxy, entry);
						bookMarkCache( entry, false );
						return entry.wsproxy;
					}
				}
				/**
				 * the used proxy may be return to cache; so we shouldn't create new proxy immediately but wait for a moment; because the create new is too expensive!
				 */
				if( depth >= 3 ) {  //stop recursion;
					CacheEntry<P> entry = getNewProxyCacheEntry();
					using.put(entry.wsproxy, entry);
					bookMarkCache( entry, false );
					return entry.wsproxy;
				}
				else {
					try {
						Thread.sleep(100);
					} 
					catch (InterruptedException e) {
						logger.error("SimpleCachewebServiceManager: wait proxy error " + e); 
					}
					return innerGetProxy( ++depth );  //recursion
				}
			}
			catch( Exception e ) {
				logger.error("SimpleCachewebServiceManager: failed obtain webservice proxy from cache!! " , e); 
				return null;
			}
			
		}
		
		@Override
		public void closeWsProxy( P proxy ) throws EdgeServiceFault {
			if( proxy == null ) {
				throw  EdgeServiceFault.getFault("",  " empty proxy is pass into closeProxy() function" );
			}
			CacheEntry<P> entry = using.get(proxy);
			bookMarkCache( entry, true );
			if( entry == null ) {
				throw  EdgeServiceFault.getFault("",  "the proxy " + proxy.toString() +" cannot be closed, because it already be cloased or not created by this cache manager" );
			}
			else {
				synchronized(cacheListMutex) {
					using.remove(proxy);
					boolean isReturntoPool = false;
					if( cachedProxys.size() < maxSize ) {
						isReturntoPool = true;
						cachedProxys.add(entry);
					}
					try {
						doClose( proxy, isReturntoPool );
					}
					catch( Exception e ) {
						logger.error("SimpleCachewebServiceManager: failed close webserice stub!! " + e); 
						throw  EdgeServiceFault.getFault("", " SimpleCachewebServiceManager: failed close webserice stub! "  );
					}
					
				}
			}
		}
		private void bookMarkCache(CacheEntry<P> entry, boolean isClose ) {
			if( !isClose ) {
				entry.lastOutCacheTime = new Date();
				entry.toWhich = Thread.currentThread().toString();
			}
		}
		protected String makeEndpointAddress(String protocol, String host,int port, String servicePart){
			
			if (protocol != null && !protocol.endsWith(":"))
				protocol = protocol + ":";
			
			if(servicePart != null && !servicePart.startsWith("/")){
				servicePart = "/" + servicePart;
			}
			String endpointAddress = protocol + "//" + host + ":" + port + servicePart;
			return endpointAddress;
		}
		public void destory(){
			this.cachedProxys.clear();
			this.using.clear();
		}
		
		protected void bindConnectionContext(Object service, String servicePart, ConnectionContext context) {
			if (service instanceof IConnectionContextBinder) {
				((IConnectionContextBinder) service).bind(context);
			}
			
			if (!(service instanceof BindingProvider)) {
				return;
			}
			
			Map<String, Object> rc = ((BindingProvider) service).getRequestContext();
			
			String endpointAddress = makeEndpointAddress(context.getProtocol(), context.getHost(), context.getPort(), servicePart);
		    rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		}
		
		protected void closeService(Object service) throws IOException {
			if (service instanceof Closeable) {
				((Closeable) service).close();
			}
		}
	}
	
	class AgentWSCreator extends BaseWSCreator<WebServiceClientProxy> {
		
		private final ConnectionContext dummyConnectionContext = 
				new ConnectionContext("http", "dummyAgentHost", 8014).buildServiceId(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_OOLONG);
		
		public AgentWSCreator(int initSize, int maxSize) {
			super(initSize, maxSize);
		}
		@Override
		protected void configureProxy( WebServiceClientProxy proxy, ConnectionContext context ) {
			ID2D4EdgeService_Oolong service = proxy.getService(ID2D4EdgeService_Oolong.class);
			bindConnectionContext(service, WebServiceFactory.SERVICE_PART, context);
		}
		@Override
		CacheEntry<WebServiceClientProxy> getNewProxyCacheEntry() {
			WebServiceClientProxy clientProxy = EdgeFactory.getD2dServiceProvider().getProxy(dummyConnectionContext);
			CacheEntry<WebServiceClientProxy> entry = new CacheEntry<WebServiceClientProxy>();
			entry.wsproxy = clientProxy;
			return entry;
		}
		@Override
		protected void doClose(WebServiceClientProxy proxy,
				boolean isReturnToPool) throws IOException {
			if( !isReturnToPool ) {
				ID2D4EdgeService_Oolong service = proxy.getService(ID2D4EdgeService_Oolong.class);
				closeService(service);
			}
			
		}
	}
	
	class RPSWSCreator extends BaseWSCreator<RPSWebServiceClientProxy> {
		
		private final ConnectionContext dummyConnectionContext = new ConnectionContext("http", "dummyRPSHost", 8014);

		public RPSWSCreator(int initSize, int maxSize) {
			super(initSize, maxSize);
		}
		@Override
		protected void configureProxy( RPSWebServiceClientProxy proxy, ConnectionContext context ) {
			IRPSService4CPM service = proxy.getServiceForCPM();
			bindConnectionContext(service, RPSWebServiceFactory.servicePart4CPM, context);
		}
		
		@Override
		CacheEntry<RPSWebServiceClientProxy> getNewProxyCacheEntry() {
			RPSWebServiceClientProxy rpsProxy = EdgeFactory.getRpsServiceProvider().getProxy(dummyConnectionContext);
			CacheEntry<RPSWebServiceClientProxy> entry = new CacheEntry<RPSWebServiceClientProxy>();
			entry.wsproxy = rpsProxy;
			return entry;
		}
		@Override
		protected void doClose(RPSWebServiceClientProxy proxy,
				boolean isReturnToPool) throws IOException {
			if( !isReturnToPool ) {
				IRPSService4CPM service = proxy.getServiceForCPM();
				closeService(service);
			}
		}
	}

	class LinuxCreator extends BaseWSCreator<BaseWebServiceClientProxy> {
		
		private final ConnectionContext dummyConnectionContext = new ConnectionContext("http", "dummyLinuxD2DHost", 8014);

		public LinuxCreator(int initSize, int maxSize) {
			super(initSize, maxSize);
		}
		
		@Override
		protected void configureProxy( BaseWebServiceClientProxy proxy, ConnectionContext context ) {
			ILinuximagingService service = (ILinuximagingService) proxy.getService();
			bindConnectionContext(service, LinuxD2DConnection.LinuxD2DServicePart, context);
		}
		@Override
		protected CacheEntry<BaseWebServiceClientProxy> getNewProxyCacheEntry() {
			BaseWebServiceClientProxy clientProxy = EdgeFactory.getLinuxD2DServiceProvider().getProxy(dummyConnectionContext);
			CacheEntry<BaseWebServiceClientProxy> entry = new CacheEntry<BaseWebServiceClientProxy>();
			entry.wsproxy = clientProxy;
			return entry;
		}

		@Override
		protected void doClose(BaseWebServiceClientProxy proxy,
				boolean isReturnToPool) throws IOException {
			if( !isReturnToPool ) {
				ILinuximagingService service = (ILinuximagingService) proxy.getService();
				closeService(service);
			}
		}
	}
	class AsbuCreator extends BaseWSCreator<com.ca.asbu.webservice.WebServiceClientProxy> {
		
		private final ConnectionContext dummyConnectionContext = 
				new ConnectionContext("http", "dummyRPSHost", 8014).buildServiceId(com.ca.asbu.serviceinfo.ServiceInfoConstants.getASBUARChiveToTapeServiceID());

		public AsbuCreator(int initSize, int maxSize) {
			super(initSize, maxSize);
		}

		@Override
		CacheEntry<com.ca.asbu.webservice.WebServiceClientProxy> getNewProxyCacheEntry() {
			com.ca.asbu.webservice.WebServiceClientProxy clientProxy = EdgeFactory.getAsbuServiceProvider().getProxy(dummyConnectionContext);
			CacheEntry<com.ca.asbu.webservice.WebServiceClientProxy> entry = new CacheEntry<>();
			entry.wsproxy = clientProxy;
			return entry;
		}

		@Override
		protected void configureProxy(com.ca.asbu.webservice.WebServiceClientProxy proxy, ConnectionContext context) {
			IArchiveToTapeService service = (IArchiveToTapeService)proxy.getService();
			bindConnectionContext(service, ASBUConnection.ASBU_SERVICE_PATH, context);
		}

		@Override
		protected void doClose(com.ca.asbu.webservice.WebServiceClientProxy proxy,boolean isReturnToPool) throws IOException {
			if( !isReturnToPool ) {
				IArchiveToTapeService service = (IArchiveToTapeService) proxy.getService();
				closeService(service);
			}
		}
	}
}
