package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;
import java.util.List;

/**
 * Result of querying job histories. The data in this object is a subset of
 * the result record set.
 */
public class JobHistoryPagingResult implements Serializable {

	private static final long serialVersionUID = 5456835137860616506L;
	
	private int startIndex;
	private int count;
	private int totalCount;
	private List<JobHistory> data;
	
	/**
	 * Get the index of the first record in the result record set.
	 * 
	 * @return
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Set the index of the first record in the result record set.
	 * 
	 * @param startIndex
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	/**
	 * Get number of records in the subset.
	 * 
	 * @return
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Set number of records in the subset.
	 * 
	 * @param count
	 */
	public void setCount( int count )
	{
		this.count = count;
	}
	
	/**
	 * Get total number of records in the result record set.
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
	/**
	 * Set total number of records in the result record set.
	 * 
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * Get the list of records belong to the specified subset of the result
	 * set.
	 * 
	 * @return
	 */
	public List<JobHistory> getData() {
		return data;
	}
	
	/**
	 * Set the list of records belong to the specified subset of the result
	 * set.
	 * 
	 * @param data
	 */
	public void setData(List<JobHistory> data) {
		this.data = data;
	}

}
