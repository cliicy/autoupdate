package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JPagedCatalogItem {
	private List<JCatalogDetail> details;
	private long total;

	public void setDetails(List<JCatalogDetail> details) {
		this.details = details;
	}

	public List<JCatalogDetail> getDetails() {
		return details;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getTotal() {
		return total;
	}

}
