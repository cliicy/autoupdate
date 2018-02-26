package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoveryPointResultModel extends BaseModelData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2559582500124432660L;
	

	private List<RecoveryPointItemModel> listRecoveryPointItems;
	private List<GridTreeNode> listEdbNodes;
	private List<CatalogInfoModel> listCatalogInfo;
	
	public List<RecoveryPointItemModel> getListRecoveryPointItems()
	{
		return listRecoveryPointItems;
	}
	public void setListRecoveryPointItems(List<RecoveryPointItemModel> listRecoveryPointItems)
	{
		this.listRecoveryPointItems = listRecoveryPointItems;
	}
	public List<GridTreeNode> getListEdbNodes()
	{
		return listEdbNodes;
	}
	public void setListEdbNodes(List<GridTreeNode> listEdbNodes)
	{
		this.listEdbNodes = listEdbNodes;
	}
	public List<CatalogInfoModel> getListCatalogInfo()
	{
		return listCatalogInfo;
	}
	public void setListCatalogInfo(List<CatalogInfoModel> listCatalogInfo)
	{
		this.listCatalogInfo = listCatalogInfo;
	}
}
