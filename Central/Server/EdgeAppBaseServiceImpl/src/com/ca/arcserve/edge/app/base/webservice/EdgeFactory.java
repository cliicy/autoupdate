package com.ca.arcserve.edge.app.base.webservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcserve.edge.app.base.common.service.WebServiceProviderFactory;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFunWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;

public class EdgeFactory {
	
	private static class DefaultBeanFactory implements IBeanFactory {
		private Class<?> beanClass;
		
		public DefaultBeanFactory(Class<?> beanClass) {
			this.beanClass = beanClass;
		}

		public void setBeanClass( Class<?> beanClass )
		{
			this.beanClass = beanClass;
		}

		@Override
		public Object createBean() {
			try {
				return beanClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("new instance using bean class failed", e);
			}
		}
	}
	
//	private static class SingletonBeanFactory extends DefaultBeanFactory {
//		private Object instance;
//
//		public SingletonBeanFactory(Class<?> beanClass) {
//			super(beanClass);
//		}
//		
//		public SingletonBeanFactory(Object singleton) {
//			super(null);
//			instance = singleton;
//		}
//		
//		@Override
//		public Object createBean() {
//			if (instance == null) {
//				synchronized (this) {
//					if (instance == null) {
//						instance = super.createBean();
//					}
//				}
//			}
//			
//			return instance;
//		}
//	}
	
	private static class SingletonBeanFactory extends DefaultBeanFactory
	{
		private BeanInvokeHandler invocationHandler = null;
		private Object proxy = null;

		public SingletonBeanFactory( Class<?> beanClass )
		{
			super( beanClass );
		}

		public SingletonBeanFactory( Object singleton )
		{
			super( null );
			createProxy( singleton );
		}

		@Override
		public Object createBean()
		{
			synchronized (this)
			{
				if (invocationHandler == null)
				{
					Object object = super.createBean();
					createProxy( object );
				}
			}

			return proxy;
		}
		
		private void createProxy( Object object )
		{
			invocationHandler = new BeanInvokeHandler();
			invocationHandler.setBean( object );
			
			proxy = Proxy.newProxyInstance(
				object.getClass().getClassLoader(),
				object.getClass().getInterfaces(),
				invocationHandler );
		}
		
		public void setBeanClass( Class<?> beanClass )
		{
			super.setBeanClass( beanClass );
		
			synchronized (this)
			{
				if (invocationHandler == null)
					return;
				
				Object bean = super.createBean();
				invocationHandler.setBean( bean );
			}
		}
		
		public void setBeanObject( Object bean )
		{
			synchronized (this)
			{
				if (invocationHandler == null)
				{
					createProxy( bean );
				}
				else
				{
					invocationHandler.setBean( bean );
				}
			}
		}
	}
	
	public static class BeanInvokeHandler implements InvocationHandler
	{
		private Object bean;
		
		public Object getBean()
		{
			return bean;
		}

		public void setBean( Object bean )
		{
			this.bean = bean;
		}

		@Override
		public Object invoke( Object proxy, Method method, Object[] args )
			throws Throwable
		{
			try
			{
				return method.invoke( bean, args );
			}
			catch (InvocationTargetException e)
			{
				throw e.getTargetException();
			}
		}
	}
	
	private static class DummyBeanFactory extends DefaultBeanFactory
	{
		private Class<?> interfaceClass = null;
		private DummyBeanInvokeHandler invocationHandler = null;
		private Object proxy = null;

		public DummyBeanFactory( Class<?> interfaceClass )
		{
			super( null );
			this.interfaceClass = interfaceClass;
		}

		@Override
		public Object createBean()
		{
			synchronized (this)
			{
				if (invocationHandler == null)
					createProxy();
			}

			return proxy;
		}
		
		private void createProxy()
		{
			invocationHandler = new DummyBeanInvokeHandler();
			
			proxy = Proxy.newProxyInstance(
				interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass },
				invocationHandler );
		}
	}
	
	public static class DummyBeanInvokeHandler implements InvocationHandler
	{
		private static Logger logger = Logger.getLogger( DummyBeanInvokeHandler.class );

		@Override
		public Object invoke( Object proxy, Method method, Object[] args )
			throws Throwable
		{
			String logPrefix = this.getClass().getSimpleName() + ".invoke(): ";
			logger.error( logPrefix + "Dummy bean got invoked. Method: " + method.getName(),
				new Exception( "Dummy bean got invoked." ) );
			return null;
		}
	}
	
	private static Logger logger = Logger.getLogger( EdgeFactory.class );
	
	private static IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy>
		consoleServiceProvider = WebServiceProviderFactory.createConsoleServiceProvider();
	private static IWebServiceProvider<WebServiceClientProxy> d2dServiceProvider = WebServiceProviderFactory.createD2DServiceProvider();
	private static IWebServiceProvider<RPSWebServiceClientProxy> rpsServiceProvider = WebServiceProviderFactory.createRPSServiceProvider();
	private static IWebServiceProvider<BaseWebServiceClientProxy> linuxD2DServiceProvider = WebServiceProviderFactory.createLinuxD2DServiceProvider();
	private static IWebServiceProvider<com.ca.asbu.webservice.WebServiceClientProxy> asbuServiceProvider = WebServiceProviderFactory.createAsbuServiceProvider();
	private static IWebServiceProvider<ABFunWebServiceClientProxy> oldAsbuServiceProvider = WebServiceProviderFactory.createOldAsbuServiceProvider();
	
	private static ConcurrentMap<Class<?>, IBeanFactory> beanLookup = new ConcurrentHashMap<Class<?>, IBeanFactory>();
	
	private static boolean isAssemblingFinished = false;
	
	public static boolean isAssemblingFinished()
	{
		return isAssemblingFinished;
	}

	public static void setAssemblingFinished( boolean isAssemblingFinished )
	{
		EdgeFactory.isAssemblingFinished = isAssemblingFinished;
	}
	
	private static interface IBeanFactory {
		Object createBean();
	}
	
	static {
//		registerSingleton(IRemoteNativeFacade.class, RemoteNativeFacadeImpl.class);
//		registerSingleton(IRemoteProductDeployService.class, RemoteProductDeployServiceImpl.class);
//		register(IVmwareManagerService.class, VmwareManagerServiceImpl.class);
//		
//		registerSingleton(IEdgeGatewayLocalService.class, EdgeGatewayBean.class);
//		registerSingleton(IEdgeGatewayService.class, EdgeFactory.getBean(IEdgeGatewayLocalService.class));
//		
//		registerSingleton(IASBUService.class, ASBUServiceImpl.class);
//		
//		registerSingleton(NativeFacade.class, NativeFacadeImpl.class);
//		registerSingleton(com.ca.arcflash.webservice.jni.NativeFacade.class, com.ca.arcflash.webservice.jni.NativeFacadeImpl.class);
//		
//		registerSingleton(IRemoteNativeFacadeFactory.class, DefaultRemoteNativeFacadeFactory.class);
//		registerSingleton(IConnectionFactory.class, CachedConnectionFactory.class);
//		registerSingleton(IVmwareManagerServiceFactory.class, DefaultVmwareManagerServiceFactory.class);
//		registerSingleton(IRemoteShareFolderServiceFactory.class, DefaultRemoteShareFolderServiceFactory.class);
//		registerSingleton(IRemoteShareFolderService.class, RemoteShareFolderServiceImpl.class);
//		registerSingleton(IRemoteProductDeployServiceFactory.class, DefaultRemoteProductDeployServiceFactory.class);
//		registerSingleton(IRemoteNodeServiceFactory.class, DefaultRemoteNodeServiceFactory.class);
//		registerSingleton(IRemoteNodeService.class, RemoteNodeServiceImpl.class);
//		//registerSingleton(IProductDeployService.class, ProductDeployServiceImpl.getInstance());
//		registerSingleton(IProductDeployService.class, ProductDeployServiceImpl.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> assignableClass) {
		
		if (!isAssemblingFinished())
		{
			logger.warn(
				"EdgeFactory.getBean() was invaked before assembling finished. assignableClass: " + assignableClass.getName(),
				new Exception() );
		}
		
		IBeanFactory factory = beanLookup.get(assignableClass);
		if (factory == null) {
			throw new RuntimeException("The bean is not registered by assignableClass " + assignableClass);
		}
		
		if (factory instanceof DummyBeanFactory)
		{
			logger.error(
				"A class registered as dummy bean is being getting. assignableClass: " + assignableClass.getSimpleName(),
				new Exception( "Dummy is been getting." ) );
		}
		
		Object object = factory.createBean();
		
//		StringBuilder sb = new StringBuilder();
//		sb.append( EdgeFactory.class.getSimpleName() + ".getBean(): " );
//		sb.append( "assignableClass " + assignableClass.getSimpleName() + ", Returns: "+ object.getClass().getName() );
//		String message = sb.toString();
//		logger.info( message );
		
		return (T) object;
	}
	
	public static void register(Class<?> assignableClass, Class<?> beanClass) {
		if (!assignableClass.isAssignableFrom(beanClass)) {
			throw new RuntimeException("The assignable class " + assignableClass + " is not assignable from bean class " + beanClass);
		}
		
		beanLookup.put(assignableClass, new DefaultBeanFactory(beanClass));
	}
	
	public static void registerSingleton(Class<?> assignableClass, Class<?> beanClass) {
		if (!assignableClass.isAssignableFrom(beanClass)) {
			throw new RuntimeException("The assignable class " + assignableClass + " is not assignable from bean class " + beanClass);
		}
		
		IBeanFactory factory = beanLookup.get(assignableClass);
		if ((factory != null) && (factory instanceof SingletonBeanFactory))
		{
			StringBuilder sb = new StringBuilder();
			sb.append( EdgeFactory.class.getSimpleName() + ".registerSingleton(): " );
			sb.append( "Singleton of " + assignableClass.getSimpleName() + " will be replaced by "+ beanClass.getName() );
			String message = sb.toString();
			logger.info( message );
			
			SingletonBeanFactory singletonBeanFactory = (SingletonBeanFactory)factory;
			singletonBeanFactory.setBeanClass( beanClass );
		}
		else
		{
			beanLookup.put(assignableClass, new SingletonBeanFactory(beanClass));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append( EdgeFactory.class.getSimpleName() + ".registerSingleton(): " );
		sb.append( "Register " + assignableClass.getSimpleName() + " with "+ beanClass.getName() );
		String message = sb.toString();
		logger.info( message );
	}
	
	public static void registerSingleton(Class<?> assignableClass, Object singleton) {
		if (!assignableClass.isAssignableFrom(singleton.getClass())) {
			throw new RuntimeException("The assignable class " + assignableClass + " is not assignable from singleton object " + singleton);
		}
		
		IBeanFactory factory = beanLookup.get(assignableClass);
		if ((factory != null) && (factory instanceof SingletonBeanFactory))
		{
			StringBuilder sb = new StringBuilder();
			sb.append( EdgeFactory.class.getSimpleName() + ".registerSingleton(): " );
			sb.append( "Singleton " + assignableClass.getSimpleName() + " will be replaced by object of "+ singleton.getClass().getName() );
			String message = sb.toString();
			logger.info( message );
			
			SingletonBeanFactory singletonBeanFactory = (SingletonBeanFactory)factory;
			singletonBeanFactory.setBeanObject( singleton );
		}
		else
		{
			beanLookup.put(assignableClass, new SingletonBeanFactory(singleton));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append( EdgeFactory.class.getSimpleName() + ".registerSingleton(): " );
		sb.append( "Register " + assignableClass.getSimpleName() + " with "+ singleton.getClass().getName() );
		String message = sb.toString();
		logger.info( message );
	}
	
	public static void registerDummyBean( Class<?> assignableClass )
	{
		beanLookup.put( assignableClass, new DummyBeanFactory( assignableClass ) );
		
		StringBuilder sb = new StringBuilder();
		sb.append( EdgeFactory.class.getSimpleName() + ".registerDummyBean(): " );
		sb.append( "Register " + assignableClass.getSimpleName() + " with dummy bean." );
		String message = sb.toString();
		logger.info( message );
	}
	
	public static IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy> getConsoleServiceProvider()
	{
		return consoleServiceProvider;
	}

	public static void setConsoleServiceProvider(
		IWebServiceProvider<com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy> edgeServiceProvider )
	{
		EdgeFactory.consoleServiceProvider = edgeServiceProvider;
	}

	public static IWebServiceProvider<WebServiceClientProxy> getD2dServiceProvider() {
		return d2dServiceProvider;
	}
	public static void setD2dServiceProvider(IWebServiceProvider<WebServiceClientProxy> d2dServiceProvider) {
		EdgeFactory.d2dServiceProvider = d2dServiceProvider;
	}
	public static IWebServiceProvider<RPSWebServiceClientProxy> getRpsServiceProvider() {
		return rpsServiceProvider;
	}
	public static void setRpsServiceProvider(IWebServiceProvider<RPSWebServiceClientProxy> rpsServiceProvider) {
		EdgeFactory.rpsServiceProvider = rpsServiceProvider;
	}
	public static IWebServiceProvider<BaseWebServiceClientProxy> getLinuxD2DServiceProvider() {
		return linuxD2DServiceProvider;
	}
	public static void setLinuxD2DServiceProvider(IWebServiceProvider<BaseWebServiceClientProxy> linuxD2DServiceProvider) {
		EdgeFactory.linuxD2DServiceProvider = linuxD2DServiceProvider;
	}
	public static IWebServiceProvider<com.ca.asbu.webservice.WebServiceClientProxy> getAsbuServiceProvider() {
		return asbuServiceProvider;
	}
	public static void setAsbuServiceProvider(IWebServiceProvider<com.ca.asbu.webservice.WebServiceClientProxy> asbuServiceProvider) {
		EdgeFactory.asbuServiceProvider = asbuServiceProvider;
	}
	
	public static IWebServiceProvider<ABFunWebServiceClientProxy> getOldAsbuServiceProvider() {
		return oldAsbuServiceProvider;
	}
	public static void setOldAsbuServiceProvider(IWebServiceProvider<ABFunWebServiceClientProxy> oldAsbuServiceProvider) {
		EdgeFactory.oldAsbuServiceProvider = oldAsbuServiceProvider;
	}
}
