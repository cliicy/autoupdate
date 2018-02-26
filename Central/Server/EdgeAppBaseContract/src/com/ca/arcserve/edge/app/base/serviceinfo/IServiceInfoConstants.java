package com.ca.arcserve.edge.app.base.serviceinfo;

public interface IServiceInfoConstants {
	public static final String SERVICE_BINDING_SOAP11 = "soap11";
	public static final String SERVICE_BINDING_SOAP12 = "soap12";
	public static final String SERVICE_BINDING_REST = "rest";
	public static final String DEFAULT_SERVICE_LIST_PATH = "/ListService";
	public  Class<?> getServiceInterfaceClass(String serviceID);
	public void registerID(String serviceID,Class<?> serviceInterface);
	public String getServiceInfoPath();
	public void setServiceInfoPath(String serviceInfoPath);
}
