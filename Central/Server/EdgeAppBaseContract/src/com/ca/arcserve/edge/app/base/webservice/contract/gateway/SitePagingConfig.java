package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

/**
 * The configuration that specify how many records to be returned and from
 * which record to calculate the record number.
 * 
 * @author lijyo03 
 *
 */
public class SitePagingConfig implements Serializable {

	private static final long serialVersionUID = -8515566263395242141L;
	
	private int startIndex = 0;
	private int pageSize = 50;
	private EdgeSortOrder sortOrder = EdgeSortOrder.ASC;
	private SiteSortCol sortColumn = SiteSortCol.siteId;
	
	public SitePagingConfig() {		
	}

	/**
	 * Get in what order the records should be sorted.
	 * 
	 * @return
	 */
	public EdgeSortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * Set in what order the records should be sorted.
	 * 
	 * @param	orderType
	 * 			The order direction. See {@link EdgeSortOrder} for available
	 * 			values.
	 */
	public void setSortOrder(EdgeSortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Get by which property the records should be sorted.
	 * 
	 * @return
	 */
	public SiteSortCol getSortColumn() {
		return sortColumn;
	}

	/**
	 * Set by which property the records should be sorted.
	 * 
	 * @param	orderCol
	 * 			The property by which the records will be sorted. See {@link
	 * 			SiteSortCol} for available values.
	 */
	public void setSortColumn(SiteSortCol sortColumn) {
		this.sortColumn = sortColumn;
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
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * Set how many records will be returned.
	 * 
	 * @param pagesize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}	

}
