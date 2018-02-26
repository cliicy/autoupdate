package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.List;


public class InstantVMPagingResult implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int startIndex;
	private int totalCount;
	private List<InstantVM> data;
	
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
	public List<InstantVM> getData() {
		return data;
	}
	public void setData(List<InstantVM> data) {
		this.data = data;
	}

}
