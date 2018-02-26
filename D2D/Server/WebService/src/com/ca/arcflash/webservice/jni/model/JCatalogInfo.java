package com.ca.arcflash.webservice.jni.model;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.webservice.data.restore.CatalogInfo;
import com.ca.arcflash.webservice.data.restore.CatalogInfo_EDB;

public class JCatalogInfo
{
	private long dwSubSessNo; //subsession number.
    private long dwFlag;      //For subsession catalog. 1 means catalog exists; 0 means catalog doesn't exist.
    private long dwAppFlag;   //For app catalog. Like exchange GRT catalog. 1 means exists. 0 means non-exist.
    private List <JCatalogInfo_EDB> edbCatalogInfoList; 
    
	public long getDwSubSessNo()
	{
		return dwSubSessNo;
	}
	public void setDwSubSessNo(long dwSubSessNo)
	{
		this.dwSubSessNo = dwSubSessNo;
	}
	public long getDwFlag()
	{
		return dwFlag;
	}
	public void setDwFlag(long dwFlag)
	{
		this.dwFlag = dwFlag;
	}
	public long getDwAppFlag()
	{
		return dwAppFlag;
	}
	public void setDwAppFlag(long dwAppFlag)
	{
		this.dwAppFlag = dwAppFlag;
	}	
	
	// utilities methods
	public CatalogInfo Convert2CatalogInfo()
	{
		CatalogInfo item = null;
		JCatalogInfo jItem = this;
		
		if (jItem != null)
		{
			item = new CatalogInfo();
			item.setSubSessNo(jItem.getDwSubSessNo());
			item.setFlag(jItem.getDwFlag());
			item.setAppFlag(jItem.getDwAppFlag());
			
			// convert the EDB catalog info list
			List<JCatalogInfo_EDB> jItem_EDBList = jItem.getEdbCatalogInfoList();
			if (jItem_EDBList != null)
			{				
				List<CatalogInfo_EDB> item_EDBList = new ArrayList<CatalogInfo_EDB>();
				
				for (int i=0; i<jItem_EDBList.size(); i++)
				{
					CatalogInfo_EDB edb = new CatalogInfo_EDB();
					edb.setEdbName(jItem_EDBList.get(i).getEdbName());
					edb.setCatalogCreated(jItem_EDBList.get(i).isCatalogCreated());
					
					item_EDBList.add(edb);			
				}
				
				item.setEdbCatalogInfoList(item_EDBList);
			}
		}
		
		return item;
	}
	
	public List<JCatalogInfo_EDB> getEdbCatalogInfoList()
	{
		return edbCatalogInfoList;
	}	
	public void setEdbCatalogInfoList(List<JCatalogInfo_EDB> edbCatalogInfoList)
	{
		this.edbCatalogInfoList = edbCatalogInfoList;
	}
}
