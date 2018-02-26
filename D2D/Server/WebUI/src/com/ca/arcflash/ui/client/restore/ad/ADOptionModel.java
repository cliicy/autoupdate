package com.ca.arcflash.ui.client.restore.ad;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ADOptionModel extends BaseModelData{
	private static final long serialVersionUID = 1L;
	
	private boolean skipRenamedObject = true;
	private boolean skipMovedObject = true;
	private boolean skipDeletedObject = true;
	
	public boolean isSkipRenamedObject() {
		return skipRenamedObject;
	}
	public void setSkipRenamedObject(boolean skipRenamedObject) {
		this.skipRenamedObject = skipRenamedObject;
	}
	public boolean isSkipMovedObject() {
		return skipMovedObject;
	}
	public void setSkipMovedObject(boolean skipMovedObject) {
		this.skipMovedObject = skipMovedObject;
	}
	public boolean isSkipDeletedObject() {
		return skipDeletedObject;
	}
	public void setSkipDeletedObject(boolean skipDeletedObject) {
		this.skipDeletedObject = skipDeletedObject;
	}
	
}
