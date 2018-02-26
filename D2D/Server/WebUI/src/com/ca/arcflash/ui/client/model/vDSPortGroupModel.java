package com.ca.arcflash.ui.client.model;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class vDSPortGroupModel extends BaseModelData
{
	private static final long serialVersionUID = -7157934496118963324L;
	private String tag_vDSPortGroupName = "vDSPortGroupName";
	private String tag_vDSPortGroupKey = "vDSPortGroupKey";
	public String getvDSPortGroupName()
	{
		return get(tag_vDSPortGroupName);
	}
	public void setvDSPortGroupName(String vDSPortGroupName)
	{
		set(tag_vDSPortGroupName, vDSPortGroupName);
	}
	public String getvDSPortGroupKey()
	{
		return get(tag_vDSPortGroupKey);
	}
	public void setvDSPortGroupKey(String vDSPortGroupKey)
	{
		set(tag_vDSPortGroupKey, vDSPortGroupKey);
	}
}
