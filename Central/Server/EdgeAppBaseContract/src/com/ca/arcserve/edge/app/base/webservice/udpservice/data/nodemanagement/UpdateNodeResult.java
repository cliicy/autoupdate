package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement;

import java.io.Serializable;

public class UpdateNodeResult implements Serializable
{
	private static final long serialVersionUID = -2577469611819289069L;

	private boolean isUpdatingD2DSuccessful;
	private String UpdatingD2DErrorCode;
	private boolean isUpdatingASBUSuccessful;
	private String updatingASBUErrorCode;
	
	/**
	 * Whether UDP agent information has been updated successfully.
	 * 
	 * @return
	 */
	public boolean isUpdatingD2DSuccessful()
	{
		return isUpdatingD2DSuccessful;
	}
	
	/**
	 * Set whether UDP agent information has been updated successfully.
	 * 
	 * @param isUpdatingD2DSuccessful
	 */
	public void setUpdatingD2DSuccessful( boolean isUpdatingD2DSuccessful )
	{
		this.isUpdatingD2DSuccessful = isUpdatingD2DSuccessful;
	}
	
	/**
	 * Get the error code for updating UDP agent information.
	 * 
	 * @return
	 */
	public String getUpdatingD2DErrorCode()
	{
		return UpdatingD2DErrorCode;
	}
	
	/**
	 * Set the error code for updating UDP agent information.
	 * 
	 * @param	updatingD2DErrorCode
	 */
	public void setUpdatingD2DErrorCode( String updatingD2DErrorCode )
	{
		UpdatingD2DErrorCode = updatingD2DErrorCode;
	}
	
	/**
	 * Whether ARCserve Backup information has been updated successfully.
	 * 
	 * @return
	 */
	public boolean isUpdatingASBUSuccessful()
	{
		return isUpdatingASBUSuccessful;
	}
	
	/**
	 * Whether ARCserve Backup information has been updated successfully.
	 * 
	 * @param	isUpdatingASBUSuccessful
	 */
	public void setUpdatingASBUSuccessful( boolean isUpdatingASBUSuccessful )
	{
		this.isUpdatingASBUSuccessful = isUpdatingASBUSuccessful;
	}
	
	/**
	 * Get the error code for updating ARCserve Backup information.
	 * 
	 * @return
	 */
	public String getUpdatingASBUErrorCode()
	{
		return updatingASBUErrorCode;
	}
	
	/**
	 * Set the error code for updating ARCserve Backup information.
	 * 
	 * @param updatingASBUErrorCode
	 */
	public void setUpdatingASBUErrorCode( String updatingASBUErrorCode )
	{
		this.updatingASBUErrorCode = updatingASBUErrorCode;
	}
	
}
