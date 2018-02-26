package com.ca.arcserve.edge.app.base.webservice.contract.gateway;


public enum SiteSortCol {

	siteId("id"), siteName("name"), description("description"), 
	address("address"), email("email"), lastContactTime("lastContactTime");
	
	private final String value;
	
	SiteSortCol(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static SiteSortCol fromValue(String v) {
		for (SiteSortCol c : SiteSortCol.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
