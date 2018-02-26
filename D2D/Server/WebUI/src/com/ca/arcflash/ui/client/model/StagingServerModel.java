package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StagingServerModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7263475193474595010L;

	public int getStagingServerId()
	{
		return (Integer)get("StagingServerId");
	}
	
	public void setStagingServerId(int in_iserverID)
	{
		set("StagingServerId",in_iserverID);
	}
	
	public String getStagingServer()
	{
		return (String)get("StagingServer");
	}
	
	public void setStagingServer(String sStagingServer)
	{
		set("StagingServer",sStagingServer);
	}
	
	public int getStagingServerPort()
	{
		return (Integer)get("StagingServerPort");
	}
	
	public void setStagingServerPort(int in_iStagingServerPort)
	{
		set("StagingServerPort",in_iStagingServerPort);
	}
	
	public int getStagingServerStatus()
	{
		return (Integer)get("StagingServerStatus");
	}
	
	public void setStagingServerStatus(int in_iStagingServerStatus)
	{
		set("StagingServerStatus",in_iStagingServerStatus);
	}
}
