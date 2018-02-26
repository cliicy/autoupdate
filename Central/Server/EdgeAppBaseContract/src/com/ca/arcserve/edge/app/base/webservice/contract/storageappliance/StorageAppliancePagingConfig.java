package com.ca.arcserve.edge.app.base.webservice.contract.storageappliance;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

public class StorageAppliancePagingConfig implements Serializable{
	private static final long serialVersionUID = 1692204029213184006L;
	
	private int startpos; 
	private int pagesize;
	private EdgeSortOrder orderType;
	private int gatewayId = 0;
	
	
	public int getStartpos() {
		return startpos;
	}
	public void setStartpos(int startpos) {
		this.startpos = startpos;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public EdgeSortOrder getOrderType() {
		return orderType;
	}
	public void setOrderType(EdgeSortOrder orderType) {
		this.orderType = orderType;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}	
}
