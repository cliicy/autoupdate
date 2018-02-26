package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobArchiveVolumeNodeModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6174644769229858910L;

	public String getdestVolumName() {
		return get("destVolumName");
	}
	public void setdestVolumName(String in_destVolumName) {
		set("destVolumName",in_destVolumName);
	}
	
	public Long getdestItemCount() {
		return (Long)get("destItemCount");
	}
	public void setdestItemCount(Long in_destItemCount) {
		set("destItemCount",in_destItemCount);
	}
	
	public RestoreJobArchiveItemNodeModel[] ArchiveItemsList;
	
}
