package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

/**
 * The configuration that specify how many records to be returned and from
 * which record to calculate the record number.
 * 
 * @author panbo01
 *
 */
public class JobHistoryPagingConfig implements Serializable {

	private static final long serialVersionUID = -8515566263395242141L;
	
	private int startIndex;
	private int count;
	private EdgeSortOrder orderType = EdgeSortOrder.ASC;
	private DashboardSortCol sortCol = DashboardSortCol.jobUTCStartTime;
	
	public JobHistoryPagingConfig() {		
	}

	/**
	 * Get in what order the records should be sorted.
	 * 
	 * @return
	 */
	public EdgeSortOrder getOrderType() {
		return orderType;
	}

	/**
	 * Set in what order the records should be sorted.
	 * 
	 * @param	orderType
	 * 			The order direction. See {@link EdgeSortOrder} for available
	 * 			values.
	 */
	public void setOrderType(EdgeSortOrder orderType) {
		this.orderType = orderType;
	}

	/**
	 * Get by which property the records should be sorted.
	 * 
	 * @return
	 */
	public DashboardSortCol getSortCol() {
		return sortCol;
	}

	/**
	 * Set by which property the records should be sorted.
	 * 
	 * @param	orderCol
	 * 			The property by which the records will be sorted. See {@link
	 * 			DashboardSortCol} for available values.
	 */
	public void setSortCol(DashboardSortCol sortCol) {
		this.sortCol = sortCol;
	}

	/**
	 * Get the index of the first record.
	 * 
	 * @return
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Set the index of the first record.
	 * 
	 * @param startpos
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	/**
	 * Get how many records will be returned.
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Set how many records will be returned.
	 * 
	 * @param pagesize
	 */
	public void setCount(int count) {
		this.count = count;
	}	

}
