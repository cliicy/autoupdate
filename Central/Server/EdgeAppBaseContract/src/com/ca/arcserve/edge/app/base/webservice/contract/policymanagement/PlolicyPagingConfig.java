package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

public class PlolicyPagingConfig implements Serializable{
	private static final long serialVersionUID = 6031197288964301081L;
	
	private int startpos; 
	private int pagesize;
	private EdgeSortOrder orderType;
	private PolicySortCol orderCol;
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
	public PolicySortCol getOrderCol() {
		return orderCol;
	}
	public void setOrderCol(PolicySortCol orderCol) {
		this.orderCol = orderCol;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}	
}
