package com.ca.arcserve.edge.app.base.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.externalLink.ExternalLinkCreator;

public class ExternalLinkManager {
	
	private static final Logger logger = Logger.getLogger(ExternalLinkManager.class);
	private static final ExternalLinkManager instance = new ExternalLinkManager();
	
	private static class InnerInvocationHandler implements InvocationHandler {
		
		private ExternalLinkCreator linkCreator;
		private String redirectUrl;
		
		public InnerInvocationHandler(String redirectUrl, String localeString) {
			this.linkCreator = new ExternalLinkCreator( localeString );
			this.redirectUrl = redirectUrl;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getReturnType() != String.class) {
				throw new RuntimeException("The return type must be String.");
			}
			
			return linkCreator.create(redirectUrl, EdgeWebServiceContext.getApplicationType(), method.getName());
		}
		
	}
	
	public static ExternalLinkManager getInstance() {
		return instance;
	}
	
	private Map<String, InvocationHandler> handlerCache = new HashMap<String, InvocationHandler>();
	private Map<String, Map<Class<?>, Object>> linksCache = new HashMap<String, Map<Class<?>,Object>>();
	
	private ExternalLinkManager() {
	}
	
	public synchronized <T> T getLinks(Class<T> type) {
		return getLinks(type, System.getProperty("user.language"), System.getProperty("user.country"));
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T getLinks(Class<T> type, String language, String country) {
		//String propertyFileName = getPropertyFileName(language, country);
		String propertyFileName = "ExternalLinks.properties";
		
		String localeString = language + "_" + country;
		
		if (linksCache.containsKey(localeString) && linksCache.get(localeString).containsKey(type)) {
			return (T) linksCache.get(localeString).get(type);
		}
		
		InvocationHandler handler;
		if (handlerCache.containsKey(localeString)) {
			handler = handlerCache.get(localeString);
		} else {
			String configFolderPath = CommonUtil.getConfigurationFolder(EdgeWebServiceContext.getApplicationType());
			String redirectUrl = "";
			
			try {
				redirectUrl = getRedirectUrl(configFolderPath + propertyFileName);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			
			handler = new InnerInvocationHandler(redirectUrl, localeString);
			handlerCache.put(localeString, handler);
		}
		
		T links = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, handler);
		
		if (!linksCache.containsKey(localeString)) {
			linksCache.put(localeString, new HashMap<Class<?>, Object>());
		}
		
		if (!linksCache.get(localeString).containsKey(type)) {
			linksCache.get(localeString).put(type, links);
		}
		
		return links;
	}
	
//	private String getPropertyFileName(String language, String country) {
//		if ("de".equals(language)){
//			return "ExternalLinks_de_DE.properties";
//		}else if ("fr".equals(language)){
//			return "ExternalLinks_fr_FR.properties";
//		}else if ("ja".equals(language)){
//			return "ExternalLinks_ja_JP.properties";
//		}else if ("es".equals(language)){
//			return "ExternalLinks_es_ES.properties";
//		}else if ("pt".equals(language)){
//			return "ExternalLinks_pt_BR.properties";
//		}else if("it".equals(language)){
//			return "ExternalLinks_it_IT.properties";
//		}else if("zh".equals(language)){
//			if ("TW".equalsIgnoreCase(country)) {
//				return "ExternalLinks_zh_TW.properties";
//			} else {
//				return "ExternalLinks_zh_CN.properties";
//			}
//		}else{
//			return "ExternalLinks.properties";
//		}
//	}
	
	private String getRedirectUrl(String propertyFilePath) throws IOException {
		String redirectUrl = null;
		Properties properties = new Properties();
		FileInputStream fis = null;
		
	    try {
			fis = new FileInputStream(propertyFilePath);
	        properties.load(fis);
	        redirectUrl = properties.getProperty("baseUrl");
	    } finally {
	    	if (fis != null) {
	    		fis.close();
    		}
	    }
	    
	    return redirectUrl == null ? "" : redirectUrl;
	}

}
