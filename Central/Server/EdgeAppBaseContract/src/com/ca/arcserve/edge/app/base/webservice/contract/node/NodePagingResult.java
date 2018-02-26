package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

/**
 * Result of querying nodes. The data in this object is a subset of
 * the result record set.
 */
public class NodePagingResult implements Serializable{
	
	private static final long serialVersionUID = 2998601520282461408L;
	private int startIndex;
	private int totalCount;
	private List<Node> data;
	
	/**
	 * Get the index of the first record in the result record set.
	 * 
	 * @return	The index.
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Set the index of the first record in the result record set.
	 * 
	 * @param	startIndex
	 * 			The index.
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	/**
	 * Get total number of records that satisfied the filter.
	 * 
	 * @return	The total record number.
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
	/**
	 * Set total number of records that satisfied the filter.
	 * 
	 * @param	totalCount
	 * 			The total record number.
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * Get the list of records belong to the specified subset of the result
	 * set.
	 * 
	 * @return	The list of the records.
	 */
	public List<Node> getData() {
		return data;
	}
	
	/**
	 * Set the list of records belong to the specified subset of the result
	 * set.
	 * 
	 * @param	data
	 * 			The list of the records.
	 */
	public void setData(List<Node> data) {
		this.data = data;
	}
}
