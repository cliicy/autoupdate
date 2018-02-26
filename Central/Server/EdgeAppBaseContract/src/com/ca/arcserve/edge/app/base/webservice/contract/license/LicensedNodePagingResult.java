package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.util.List;

public class LicensedNodePagingResult {
	
	private int startIndex;
	private int totalCount;
	private List<LicensedNodeInfo> data;
	
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
	public List<LicensedNodeInfo> getData() {
		return data;
	}
	public void setData(List<LicensedNodeInfo> data) {
		this.data = data;
	}
	
}
