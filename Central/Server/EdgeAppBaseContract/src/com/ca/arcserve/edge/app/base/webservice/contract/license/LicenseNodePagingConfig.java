package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;

public class LicenseNodePagingConfig implements Serializable {

	private static final long serialVersionUID = 4623715912223530684L;
	private int startIndex;
	private int limit;
	private LicensedNodeSortColumn sortColumn;
	private boolean isASC;
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public LicensedNodeSortColumn getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(LicensedNodeSortColumn sortColumn) {
		this.sortColumn = sortColumn;
	}
	public boolean isASC() {
		return isASC;
	}
	public void setASC(boolean isASC) {
		this.isASC = isASC;
	}
	
}
