package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicensedNodeInfo implements Serializable, BeanModelTag{
	private static final long serialVersionUID = 7833885454837480018L;
	
	private String nodeName;
	private int clientType;
	private long used_num;
	
	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public LicensedNodeInfo(){
	}
	
	public LicensedNodeInfo(String nodeName, int clientType) {
		this.nodeName = nodeName;
		this.clientType = clientType;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public long getUsed_num() {
		return used_num;
	}

	public void setUsed_num(long used_num) {
		this.used_num = used_num;
	}
}
