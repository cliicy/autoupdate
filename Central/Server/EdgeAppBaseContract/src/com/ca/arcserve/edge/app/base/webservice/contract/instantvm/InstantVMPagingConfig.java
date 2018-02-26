package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

public class InstantVMPagingConfig implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int startIndex;
	private int count;
	private EdgeSortOrder orderType;
	private InstantVMSortCol orderCol;

	public InstantVMPagingConfig() {
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}	
	public void setOrderType(EdgeSortOrder orderType) {
		this.orderType = orderType;
	}	
	public EdgeSortOrder getOrderType() {
		return orderType;
	}
	public void setOrderCol(InstantVMSortCol orderCol) {
		this.orderCol = orderCol;
	}
	public InstantVMSortCol getOrderCol() {
		return orderCol;
	}
}
