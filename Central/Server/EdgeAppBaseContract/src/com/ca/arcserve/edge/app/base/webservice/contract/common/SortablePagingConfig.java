package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;

@XmlSeeAlso(value = {NodeSortCol.class})
public class SortablePagingConfig<T> extends PagingConfig implements Serializable {
	
	private static final long serialVersionUID = -6028203525025541627L;
	
	private T sortColumn;
	private boolean asc;
	
	public T getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(T sortColumn) {
		this.sortColumn = sortColumn;
	}
	public boolean isAsc() {
		return asc;
	}
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

}
