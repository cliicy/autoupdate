package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class GatewaySummary implements Serializable{
	private static final long serialVersionUID = -5556556357000899162L;
	
	private int hostId;
	private int gatewayId = 0;
	private String siteName;
	private int isLocal;
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public int getIsLocal() {
		return isLocal;
	}
	public void setIsLocal(int isLocal) {
		this.isLocal = isLocal;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GatewaySummary other = (GatewaySummary) obj;
		
		if(hostId != other.getHostId())
			return false;
		if(gatewayId != other.getGatewayId())
			return false;
		if(!StringUtil.isEqual(siteName, other.getSiteName()))
			return false;
		return true;
	}
	
	public void update(GatewaySummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(gatewayId != other.getGatewayId())
			gatewayId = other.getGatewayId();
		if(!StringUtil.isEqual(siteName, other.getSiteName()))
			siteName = other.getSiteName();
	}
}
