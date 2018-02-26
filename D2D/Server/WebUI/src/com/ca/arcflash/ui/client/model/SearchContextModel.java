package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SearchContextModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3977798498329806405L;

	public Long getContextID() {
		return (Long)get("contextID");
	}
	public void setContextID(Long contextID) {
		set("contextID", contextID);
	}
	public Long getTag() {
		return (Long) get("tag");
	}
	public void setTag(Long tag) {
		set("tag", tag);
	}
	
	public void setCurrKind(long kind) {
		this.currKind = kind;
	}

	public long getCurrKind() {
		return currKind;
	}

	public void setSearchkind(long searchkind) {
		if ((searchkind & KIND_FILE) == 0) {
			excludeFileSystem = true;
		}
		this.searchkind = searchkind;
	}

	public long getSearchkind() {
		return searchkind;
	}

	public void setExcludeFileSystem(boolean excludeFileSystem) {
		this.excludeFileSystem = excludeFileSystem;
	}

	public boolean isExcludeFileSystem() {
		return excludeFileSystem;
	}

	private long currKind;
	private long searchkind;
	private boolean excludeFileSystem = false;
	public static final long KIND_FILE = 0x0000000000000001;

}
