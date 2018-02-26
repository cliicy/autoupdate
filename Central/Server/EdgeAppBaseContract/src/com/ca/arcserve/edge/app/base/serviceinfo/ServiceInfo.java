package com.ca.arcserve.edge.app.base.serviceinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceInfo")
public class ServiceInfo {
	private List<String> serviceIDList = new ArrayList<String>();
	private String serviceName;
	private String portName;
	private String bindingType;
	private String namespace;
	private String wsdlURL;
	public List<String> getServiceIDList() {
		return serviceIDList;
	}
	public void setServiceIDList(List<String> serviceIDList) {
		this.serviceIDList = serviceIDList;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getBindingType() {
		return bindingType;
	}
	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getWsdlURL() {
		return wsdlURL;
	}
	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}
	@Override
	public String toString() {
		
		String idstr = null;
		if(serviceIDList!=null) idstr = Arrays.toString(serviceIDList.toArray(new String[0]));
		return "ServiceInfo [bindingType=" + bindingType + ", namespace="
				+ namespace + ", portName=" + portName + ", serviceIDList="
				+ idstr + ", serviceName=" + serviceName + ", wsdlURL="
				+ wsdlURL + "]";
	}
	
}
