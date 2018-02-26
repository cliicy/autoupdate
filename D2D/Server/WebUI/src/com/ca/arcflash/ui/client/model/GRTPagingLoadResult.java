package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;


public class GRTPagingLoadResult extends BaseModelData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5074991447044375334L;

	private long totalWithoutFilter = 0;
	BasePagingLoadResult<GridTreeNode> pagingLoadResult = new BasePagingLoadResult<GridTreeNode>(null, 0, 0);
	
	public GRTPagingLoadResult(List<GridTreeNode> data, int offset, int totalLength, long totalWithoutFilter)
	{
		pagingLoadResult = new BasePagingLoadResult<GridTreeNode>(data, offset, totalLength);
		this.totalWithoutFilter = totalWithoutFilter;
	}
	
	public GRTPagingLoadResult()
	{
		
	}

	public BasePagingLoadResult<GridTreeNode> getPagingLoadResult()
	{
		return pagingLoadResult;
	}

	public void setPagingLoadResult(BasePagingLoadResult<GridTreeNode> pagingLoadResult)
	{
		this.pagingLoadResult = pagingLoadResult;
	}

	public long getTotalWithoutFilter()
	{
		return totalWithoutFilter;
	}

	public void setTotalWithoutFilter(long totalWithoutFilter)
	{
		this.totalWithoutFilter = totalWithoutFilter;
	}


}
