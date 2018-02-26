package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GRTBrowsingContextModel extends BaseModelData
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8980344789115028016L;
	
	public Boolean getFolderOnly()
	{
		return (Boolean)get("folderOnly");
	}
	public void setFolderOnly(Boolean folderOnly)
	{
		set("folderOnly", folderOnly);
	}
	
	public Boolean getMailOnly()
	{
		return (Boolean)get("mailOnly");
	}
	public void setMailOnly(Boolean mailOnly)
	{
		set("mailOnly", mailOnly);
	}
	
	public String getFilterKeyword()
	{
		return (String)get("filterKeyword");
	}
	public void setFilterKeyword(String filterKeyword)
	{
		set("filterKeyword", filterKeyword);
	}
	
	public String getSearchKeyword()
	{
		return (String)get("searchKeyword");
	}
	public void setSearchKeyword(String searchKeyword)
	{
		set("searchKeyword", searchKeyword);
	}
	
	private long total = 0;
	private long totalWithoutFilter = 0;

	public long getTotal()
	{
		return total;
	}
	public void setTotal(long total)
	{
		this.total = total;
	}
	
	public long getTotalWithoutFilter()
	{
		return totalWithoutFilter;
	}
	public void setTotalWithoutFilter(long totalWithoutFilter)
	{
		this.totalWithoutFilter = totalWithoutFilter;
	}
	
//	public Long getRequestStart()
//	{
//		return (Long)get("requestStart");
//	}
//	public void setRequestStart(Long requestStart)
//	{
//		set("requestStart", requestStart);
//	}
//	
//	public Long getRequestSize()
//	{
//		return (Long)get("requestSize");
//	}
//	public void setRequestSize(Long requestSize)
//	{
//		set("requestSize", requestSize);
//	}
//	
//	public String getSortColumn()
//	{
//		return (String)get("sortColumn");
//	}
//	public void setSortColumn(String sortColumn)
//	{
//		set("sortColumn", sortColumn);
//	}
//	
//	public Long getSortOrder()
//	{
//		return (Long)get("sortOrder");
//	}
//	public void setSortOrder(Long sortOrder)
//	{
//		set("sortOrder", sortOrder);;
//	}
}
