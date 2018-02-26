package com.ca.arcflash.ha.webservice;

import com.ca.arcflash.webservice.IFlashGRT;
import com.ca.arcflash.webservice.IFlashService;
import com.ca.arcflash.webservice.IFlashServiceV2;
import com.ca.arcflash.webservice.IFlashServiceVGRT;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeCM;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeVCM;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeVSphere;

public class WebServiceClientProxyWrapper extends WebServiceClientProxy {
	
	private WebServiceClientProxy proxy = null;
	private long lastAccessTime = 0l;
	
	public WebServiceClientProxyWrapper(WebServiceClientProxy proxy) {
		super(proxy.getPort(), proxy.getProtocol(), proxy.getService());
		this.proxy = proxy;
		updateAccessTime();
	}

	public String getHost() {
		return proxy.getHost();
	}
	public void setHost(String host) {
		proxy.setHost(host);
	}
	public int getPort() {
		return proxy.getPort();
	}
	public void setPort(int port) {
		proxy.setPort(port);
	}
	public String getProtocol() {
		return proxy.getProtocol();
	}
	public void setProtocol(String protocol) {
		proxy.setProtocol(protocol);
	}
	
	public IFlashServiceV2 getServiceV2() {
		updateAccessTime();
		return proxy.getServiceV2();
	}

	private void updateAccessTime() {
		lastAccessTime = System.currentTimeMillis();
	}
	public void setService(IFlashServiceV2 service) {
		proxy.setService(service);
	}
	public IFlashService getService() {
		updateAccessTime();
		return proxy.getService();
	}
	public void setService(IFlashService service) {
		proxy.setService(service);
	}
	public IFlashServiceVGRT getServiceVGRT() {
		updateAccessTime();
		return proxy.getServiceVGRT();
	}
	public IFlashGRT getServiceGRT() {
		updateAccessTime();
		return proxy.getServiceGRT();
	}
	public ID2D4EdgeCM getServiceForEdgeCM() {
		updateAccessTime();
		return proxy.getServiceForEdgeCM();
	}
	public ID2D4EdgeVCM getServiceForEdgeVCM() {
		updateAccessTime();
		return proxy.getServiceForEdgeVCM();
	}
	public ID2D4EdgeVSphere getServiceForEdgeVSphere() {
		updateAccessTime();
		return proxy.getServiceForEdgeVSphere();
	}
	
	public long getLastAccessServerTime() {
		return lastAccessTime;
	}
	
	public boolean isPossibleSessionTimeout() {
		return (System.currentTimeMillis() - lastAccessTime) > WebServiceFactory.TIME_OUT_VALUE/3*2;
			
	}
}
