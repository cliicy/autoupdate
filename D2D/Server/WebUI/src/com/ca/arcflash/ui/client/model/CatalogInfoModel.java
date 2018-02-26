package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CatalogInfoModel extends BaseModelData 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7268057452396164082L;
	
	public Long getSubSessNo()
	{
		return get("subSessNo");
	}
	public void setSubSessNo(Long subSessNo)
	{
		set("subSessNo", subSessNo);
	}
	
	public Long getFlag()
	{
		return get("flag");
	}
	public void setFlag(Long flag)
	{
		set("flag", flag);
	}
	
	public Long getAppFlag()
	{
		return get("appFlag");
	}
	public void setAppFlag(Long appFlag)
	{
		set("appFlag", appFlag);
	}	
	
	private List<CatalogInfo_EDB_Model> edbCatalogInfoList;

	public List<CatalogInfo_EDB_Model> getEdbCatalogInfoList()
	{
		return edbCatalogInfoList;
	}
	public void setEdbCatalogInfoList(List<CatalogInfo_EDB_Model> edbCatalogInfoList)
	{
		this.edbCatalogInfoList = edbCatalogInfoList;
	}
}
