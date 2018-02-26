package com.ca.arcserve.edge.app.base.webservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.webservice.exception.EdgeExceptionHandler;
import com.ca.arcserve.edge.app.base.webservice.exception.ExceptionHandler;
import com.ca.arcserve.edge.app.base.webservice.exception.WebServiceExceptionHandler;

/**
 * This factory is used to create proxy for web service. The proxy will:<br>
 * <li>Check session.</li>
 * <li>Handle the web service exception in common way.</li>
 * @author qiubo01
 */
public class EdgeWebServiceProxyFactory {
	
	public static interface IExceptionHandlerFactory {
		ExceptionHandler create(ExceptionHandler handler);
	}
	
	private static class DefaultExceptionHandlerFactory implements IExceptionHandlerFactory {

		@Override
		public ExceptionHandler create(ExceptionHandler handler) {
			ExceptionHandler guardExceptionHandler = new EdgeExceptionHandler();
			
			if (handler == null) {
				return guardExceptionHandler;
			}
			
			handler.add(guardExceptionHandler);
			return handler;
		}
		
	}
	
	private static Logger logger = Logger.getLogger(EdgeWebServiceProxyFactory.class);
	private static IExceptionHandlerFactory exceptionHandlerFactory = new DefaultExceptionHandlerFactory();
	
	public static IExceptionHandlerFactory getExceptionHandlerFactory() {
		return exceptionHandlerFactory;
	}

	public static void setExceptionHandlerFactory(IExceptionHandlerFactory exceptionHandlerFactory) {
		EdgeWebServiceProxyFactory.exceptionHandlerFactory = exceptionHandlerFactory;
	}
	
	private EdgeWebServiceProxyFactory() {
	}
	
	public static <T> T createProxy4D2D(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		ExceptionHandler d2dExceptionHandler = new WebServiceExceptionHandler(FaultType.D2D, EdgeServiceErrorCode.Node_CantConnectRemoteD2D);
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(d2dExceptionHandler));
	}
	
	public static <T> T createProxy4LinuxD2D(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		ExceptionHandler linuxD2DExceptionHandler = new WebServiceExceptionHandler(FaultType.LinuxD2D, EdgeServiceErrorCode.Node_CantConnectRemoteD2D);
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(linuxD2DExceptionHandler));
	}
	
	public static <T> T createProxy4RPS(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		ExceptionHandler rpsExceptionHandler = new WebServiceExceptionHandler(FaultType.RPSRemote, EdgeServiceErrorCode.RPS_CannotConnectServer);
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(rpsExceptionHandler));
	}
	
	public static <T> T createASBUProxy(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		ExceptionHandler asbuExceptionHandler = new WebServiceExceptionHandler(FaultType.ASBU, EdgeServiceErrorCode.Node_CantConnectRemoteD2D);
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(asbuExceptionHandler));
	}
	
	public static <T> T createRBACProxy(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		ExceptionHandler asbuExceptionHandler = new WebServiceExceptionHandler(FaultType.RBAC, EdgeServiceErrorCode.RBAC_CreateServiceFail);
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(asbuExceptionHandler));
	}
	
	public static <T> T createProxy(T interfaceImpl, Class<T> interfaceClass, IServiceSecure secure) {
		return createProxy(interfaceImpl, interfaceClass, secure, exceptionHandlerFactory.create(null));
	}
	
	public static <T> T createProxy(T interfaceImpl, Class<T> interfaceClass) {
		return createProxy(interfaceImpl, interfaceClass, null);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T createProxy(final T interfaceImpl, Class<T> interfaceClass, final IServiceSecure secure, final ExceptionHandler handler) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (secure != null) {
					secure.checkSession();
				}
				
				try {
					return method.invoke(interfaceImpl, args);
				} catch (InvocationTargetException e) {
					logger.info("EdgeWebServiceProxyFactory - invocation failed, method = " + interfaceImpl.getClass().getSimpleName() + "." + method.getName());
					
					if (handler != null) {
						handler.handleException(e.getTargetException());
					}
					
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "cannot handle the exception.");	// should never occur
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
				}
			}
			
		});
	}
}
