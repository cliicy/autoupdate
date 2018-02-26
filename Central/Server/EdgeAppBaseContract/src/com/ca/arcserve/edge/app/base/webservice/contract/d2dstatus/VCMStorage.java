package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "VCMStorage", namespace = "http://webservice.edge.arcserve.ca.com/d2dstatus/")
public class VCMStorage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private long freeSize;
	private long totalSize; // in byte
	private long coldStandySize; // in byte
	private long otherSize;  // in byte
	
	/**
	 * Get the name of the storage.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set the name of the storage.
	 * 
	 * @param name
	 */
	public void setName( String name )
	{
		this.name = name;
	}
	
	/**
	 * Get the free size of the storage.
	 * 
	 * @return
	 */
	public long getFreeSize()
	{
		return freeSize;
	}
	
	/**
	 * Set the free size of the storage.
	 * 
	 * @param freeSize
	 */
	public void setFreeSize( long freeSize )
	{
		this.freeSize = freeSize;
	}

	/**
	 * Get the total size of the storage.
	 * 
	 * @return
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * Set the total size of the storage.
	 * 
	 * @param totalSize
	 */
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	/**
	 * Get the virtual conversion size of the storage.
	 * 
	 * @return
	 */
	public long getColdStandySize() {
		return coldStandySize;
	}

	/**
	 * Set the virtual conversion size of the storage.
	 * 
	 * @param coldStandySize
	 */
	public void setColdStandySize(long coldStandySize) {
		this.coldStandySize = coldStandySize;
	}

	/**
	 * Get the other size of the storage.
	 * 
	 * @return
	 */
	public long getOtherSize() {
		return otherSize;
	}

	/**
	 * Set the other size of the storage.
	 * 
	 * @param otherSize
	 */
	public void setOtherSize(long otherSize) {
		this.otherSize = otherSize;
	}
}
