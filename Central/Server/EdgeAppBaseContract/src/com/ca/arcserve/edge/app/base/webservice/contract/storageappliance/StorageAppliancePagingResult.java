package com.ca.arcserve.edge.app.base.webservice.contract.storageappliance;

import java.io.Serializable;
import java.util.List;

public class StorageAppliancePagingResult  implements Serializable{
	private static final long serialVersionUID = 1289888028913135399L;
	private int startIndex;
	private int totalCount;
	private List<StorageApplianceInfo> data;
	
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
	public List<StorageApplianceInfo> getData() {
		return data;
	}
	public void setData(List<StorageApplianceInfo> data) {
		this.data = data;
	}

}
