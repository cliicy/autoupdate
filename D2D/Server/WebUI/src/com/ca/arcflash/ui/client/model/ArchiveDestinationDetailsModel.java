package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveDestinationDetailsModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8236907114787073119L;
	public Date getLastSyncDate()
	{
		return (Date)get("LastSyncDate");			
	}
	public void setLastSyncDate(Date in_date)
	{
		set("LastSyncDate", in_date);
	}
	
	public Boolean getCatalogAvailable()
	{
		return (Boolean) get("CatalogAvailable");
	}
	public void setCatalogAvailable(Boolean in_bCatalogAvailable)
	{
		set("CatalogAvailable", in_bCatalogAvailable);
	}
	
	public String gethostName()
	{
		return get("hostName");			
	}
	public void sethostName(String in_hostName)
	{
		set("hostName", in_hostName);
	}
}
