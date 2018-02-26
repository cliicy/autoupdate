package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

public class PagingConfig implements Serializable {

	private static final long serialVersionUID = -8629937919885241715L;
	
	private int startIndex;
	private int count;
	private EdgeSortOrder orderType;
	
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
	public EdgeSortOrder getOrderType() {
		return orderType;
	}
	public void setOrderType(EdgeSortOrder orderType) {
		this.orderType = orderType;
	}

}
