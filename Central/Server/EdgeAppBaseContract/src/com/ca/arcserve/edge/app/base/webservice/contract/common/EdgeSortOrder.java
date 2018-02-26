package com.ca.arcserve.edge.app.base.webservice.contract.common;

public enum EdgeSortOrder {
	ASC("ASC"), DESC("DESC");

	private final String value;

	EdgeSortOrder(String v) {
		value = v;
	}

	public String value() {
		return value;
	}
}
