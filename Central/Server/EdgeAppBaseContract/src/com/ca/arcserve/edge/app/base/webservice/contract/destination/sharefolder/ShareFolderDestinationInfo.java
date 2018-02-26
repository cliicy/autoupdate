package com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder;

import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;

public class ShareFolderDestinationInfo extends DestinationInfo {

	private static final long serialVersionUID = 1L;
	private int planCount;
	private long datasize;
	private boolean isNFS;
	
	public ShareFolderDestinationInfo(){}
	public ShareFolderDestinationInfo( String path, String userName, int planCount, long dataSize ) {
		super( path, userName);
		this.planCount = planCount;
		this.datasize = dataSize;
	}
	public int getPlanCount() {
		return planCount;
	}
	public void setPlanCount(int planCount) {
		this.planCount = planCount;
	}
	public long getDatasize() {
		return datasize;
	}
	public void setDatasize(long datasize) {
		this.datasize = datasize;
	}
	public boolean isNFS() {
		return isNFS;
	}
	public void setNFS(boolean isNFS) {
		this.isNFS = isNFS;
	}
}
