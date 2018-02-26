package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;

/**
 * The configuration that specify how many records to be returned and from
 * which record to calculate the record number.
 * 
 * @author panbo01
 *
 */
public class LogPagingConfig implements Serializable {

	private static final long serialVersionUID = -8515566263395242111L;
	
	private int startIndex;
	private int count;
	private SortColumn sortColumn;
	private Boolean asc;
	
	public LogPagingConfig() {
		sortColumn = SortColumn.None;
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
	
	/**
	 * Get by which property the records should be sorted.
	 * 
	 * @return
	 */
	public SortColumn getSortColumn() {
		return sortColumn;
	}
	
	/**
	 * Set by which property the records should be sorted.
	 * 
	 * @param	orderCol
	 * 			The property by which the records will be sorted. See {@link
	 * 			SortColumn} for available values.
	 */
	public void setSortColumn(SortColumn sortColumn) {
		this.sortColumn = sortColumn;
	}
	
	/**
	 * Get whether the records should be sorted in ascending order.
	 * 
	 * @return
	 */
	public Boolean getAsc() {
		return asc;
	}
	
	/**
	 * Set whether the records should be sorted in ascending order.
	 * 
	 * @param asc
	 */
	public void setAsc(Boolean asc) {
		this.asc = asc;
	}

}
