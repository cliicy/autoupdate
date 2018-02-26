package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class IPForInstantVM implements Serializable{
	private static final long serialVersionUID = 189699209425195897L;
	private String ip = "";
	private String subnet = "";

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSubnet() {
		return subnet;
	}
	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}
	@Override
	public String toString() {
		return "IPInfo [ip=" + ip + ", subnet=" + subnet + "]";
	}

}
