package com.ca.arcserve.edge.app.base.webservice.contract.filter;

public enum FilterType {
	
	NodeFilter(1), DashboardFilter(2),LogTimeFilter(3);
	
	private int value;
	
	private FilterType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
}
