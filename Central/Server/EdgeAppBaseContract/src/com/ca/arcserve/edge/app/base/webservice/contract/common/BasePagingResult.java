package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

public class BasePagingResult implements Serializable {

	private static final long serialVersionUID = -6127831193320776075L;
	
	private int startIndex;
	private int totalCount;
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
