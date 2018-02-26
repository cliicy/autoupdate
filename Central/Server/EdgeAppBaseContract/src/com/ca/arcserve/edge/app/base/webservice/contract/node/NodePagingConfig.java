package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;

/**
 * The configuration that specify how many records to be returned and from
 * which record to calculate the record number.
 * 
 * @author panbo01
 *
 */
public class NodePagingConfig implements Serializable{

	private static final long serialVersionUID = -3805671491008415285L;
	
	private int startpos; 
	private int pagesize;
	private EdgeSortOrder orderType;
	private NodeSortCol orderCol;
	
	/**
	 * Get the index of the first record.
	 * 
	 * @return
	 */
	public int getStartpos() {
		return startpos;
	}
	
	/**
	 * Set the index of the first record.
	 * 
	 * @param startpos
	 */
	public void setStartpos(int startpos) {
		this.startpos = startpos;
	}
	
	/**
	 * Get how many records will be returned.
	 * 
	 * @return
	 */
	public int getPagesize() {
		return pagesize;
	}
	
	/**
	 * Set how many records will be returned.
	 * 
	 * @param pagesize
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
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
	public NodeSortCol getOrderCol() {
		return orderCol;
	}
	
	/**
	 * Set by which property the records should be sorted.
	 * 
	 * @param	orderCol
	 * 			The property by which the records will be sorted. See {@link
	 * 			NodeSortCol} for available values.
	 */
	public void setOrderCol(NodeSortCol orderCol) {
		this.orderCol = orderCol;
	}
	
}
