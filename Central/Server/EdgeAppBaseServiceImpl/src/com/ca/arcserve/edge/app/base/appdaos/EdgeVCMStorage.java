package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeVCMStorage
{
	private String storageName;
	private long freeSize;
	private long coldStandySize; // in byte
	private long totalSize; // in byte
	private long otherSize;  // in byte
	
	public String getStorageName()
	{
		return storageName;
	}
	
	public void setStorageName( String storageName )
	{
		this.storageName = storageName;
	}
	
	public long getFreeSize()
	{
		return freeSize;
	}
	
	public void setFreeSize( long freeSize )
	{
		this.freeSize = freeSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getColdStandySize() {
		return coldStandySize;
	}

	public void setColdStandySize(long coldStandySize) {
		this.coldStandySize = coldStandySize;
	}

	public long getOtherSize() {
		return otherSize;
	}

	public void setOtherSize(long otherSize) {
		this.otherSize = otherSize;
	}
}
