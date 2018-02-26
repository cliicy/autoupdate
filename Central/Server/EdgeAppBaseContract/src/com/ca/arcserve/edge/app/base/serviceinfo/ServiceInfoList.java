package com.ca.arcserve.edge.app.base.serviceinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceInfoList")
public class ServiceInfoList {
	private List<ServiceInfo> services = new ArrayList<ServiceInfo>();

	public List<ServiceInfo> getServices() {
		return services;
	}

	public void setServices(List<ServiceInfo> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		String service = Arrays.toString(services.toArray(new ServiceInfo[0]));
		return "ServiceInfoList [services=" + service + "]";
	}
	
}
