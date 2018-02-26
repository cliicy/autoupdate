package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;
import java.util.List;

public class PolicyPagingResult  implements Serializable{

	private static final long serialVersionUID = -5893003287244215274L;

	private int startIndex;
	private int totalCount;
	private List<PolicyInfo> data;
	
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
	public List<PolicyInfo> getData() {
		return data;
	}
	public void setData(List<PolicyInfo> data) {
		this.data = data;
	}

}
