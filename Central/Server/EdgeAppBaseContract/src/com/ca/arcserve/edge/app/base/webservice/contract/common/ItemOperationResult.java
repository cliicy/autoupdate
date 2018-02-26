package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

/**
 * The result of an operation on an item.
 */
public class ItemOperationResult implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int itemId;
	private String itemGuid;
	private int resultCode;
	
	public ItemOperationResult() {
	}
	
	public ItemOperationResult(int itemId, int resultCode) {
		super();
		this.itemId = itemId;
		this.resultCode = resultCode;
	}

	public ItemOperationResult(String itemGuid, int resultCode) {
		super();
		this.itemGuid = itemGuid;
		this.resultCode = resultCode;
	}


	
	/**
	 * Get the ID of the item.
	 * 
	 * @return
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * Set the ID of the item.
	 * 
	 * @param itemId
	 */
	public void setItemId( int itemId )
	{
		this.itemId = itemId;
	}

	/**
	 * Get the result code of the operation.
	 * 
	 * @return
	 */
	public int getResultCode()
	{
		return resultCode;
	}
	
	/**
	 * set the result code of the operation.
	 * 
	 * @param resultCode
	 */
	public void setResultCode( int resultCode )
	{
		this.resultCode = resultCode;
	}

	/**
	 * Get the GUID of the item.
	 * 
	 * @return
	 */
	public String getItemGuid() {
		return itemGuid;
	}

	/**
	 * Set the GUID of the item.
	 * 
	 * @param itemGuid
	 */
	public void setItemGuid(String itemGuid) {
		this.itemGuid = itemGuid;
	}
}
