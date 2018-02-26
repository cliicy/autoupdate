package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class SearchResultModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6452145191557900747L;

	public Long getFound() {
		return (Long) get("found");
	}

	public void setFound(Long found) {
		set("found", found);
	}

	public Long getCurrent() {
		return (Long) get("current");
	}

	public void setCurrent(Long current) {
		set("current", current);
	}	

	public Boolean hasNext() {
		return !(getCurrent() == -1);
	}
	
	public ArrayList<com.ca.arcflash.ui.client.model.CatalogItemModel>  listOfItems;
	public void setNextKind(long kind) {
		this.kind = kind;
	}
	public long getNextKind() {
		return kind;
	}
	private long kind;
}
