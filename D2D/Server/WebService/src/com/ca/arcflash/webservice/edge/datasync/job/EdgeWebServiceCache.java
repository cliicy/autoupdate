package com.ca.arcflash.webservice.edge.datasync.job;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.toedge.IEdgeVaildate;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;

class EdgeWebServiceCache<T extends IEdgeVaildate> {
	
	private static Logger logger = Logger.getLogger(EdgeWebServiceCache.class);
	
	private ApplicationType appType;
	private long lastTime;
	private T cachedService;
	private long cacheTimeMillis = 5000;
	private String lastLogMessage;
	
	public EdgeWebServiceCache(ApplicationType appType) {
		this.appType = appType;
	}
	
	public void setAppType(ApplicationType appType) {
		this.appType = appType;
	}
	
	public void setCacheTimeMillis(long cacheTimeMillis) {
		this.cacheTimeMillis = cacheTimeMillis;
	}
	
	public void clear() {
		lastTime = 0;
		cachedService = null;
	}
	
	public T getService(Class<T> type) {
		if (cachedService != null) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastTime <= cacheTimeMillis) {
				logger.debug("Use the cached service.");
				lastTime = currentTime;
				return cachedService;
			}
		}
		
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(appType);
		if (edgeRegInfo == null) {
			logger.debug("Cannot find the registration for Central Applications, app type = " + appType);
			clear();
			return null;
		}
		
		try {
			cachedService = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(), type);
			cachedService.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			lastTime = System.currentTimeMillis();
			
			return cachedService;
		} catch (Exception e) {
			String logMessage = "Create edge web service failed, app type = " + appType + ", WSDL = " + edgeRegInfo.getEdgeWSDL() + ", error message = " + e.getMessage();
			
			if (logMessage.equalsIgnoreCase(lastLogMessage)) {
				logger.debug(logMessage);
			} else {
				logger.error(logMessage);
			}
			
			lastLogMessage = logMessage;
			clear();
			return null;
		}
	}

}
