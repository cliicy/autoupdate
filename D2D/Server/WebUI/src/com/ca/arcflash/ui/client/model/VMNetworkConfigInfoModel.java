package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMNetworkConfigInfoModel extends BaseModelData
{
	private static final long serialVersionUID = 8367493110363218813L;
	// network adapter info
	private String tag_deviceType = "deviceType";
	private String tag_label = "label";
		
	private String tag_backingInfoType = "backingInfoType";
	
	// network info
	// when backingType is VirtualEthernetCardNetworkBackingInfo
	private String tag_deviceName = "deviceName";	
	
	// when backingType is VirtualEthernetCardDistributedVirtualPortBackingInfo
	private String tag_switchUUID = "switchUUID";
	private String tag_portgroupKey = "portgroupKey";
	
	// the names are not available from VMSnapshotConfigInfo.vsci
	// but we'll use them for createVM
	private String tag_switchName = "switchName";      
	private String tag_portgroupName = "portgroupName";
	
	private String hyperV_adapter_type = "hyperVAdapterType";
	
	//for vAPP and child VM network
	private String tag_networkId = "networkId";
	private String tag_adapterType = "adapterType";
	private String tag_parentName = "parentName"; //the mapped to VDC/VApp network name
	private String tag_parentId = "parentId"; //the mapped to VDC/VApp network id
	
	public Integer getHyperVAdapterType()
	{
		return get(hyperV_adapter_type);
	}
	public void setHyperVAdapterType(Integer type)
	{
		set(hyperV_adapter_type, type);
	}
	
	public String getDeviceType()
	{
		return get(tag_deviceType);
	}
	public void setDeviceType(String deviceType)
	{
		set(tag_deviceType, deviceType);
	}
	public String getLabel()
	{
		return get(tag_label);
	}
	public void setLabel(String label)
	{
		set(tag_label, label);
	}
	
	public String getDeviceName()
	{
		return get(tag_deviceName);
	}
	public void setDeviceName(String deviceName)
	{
		set(tag_deviceName, deviceName);
	}
	public String getSwitchUUID()
	{
		return get(tag_switchUUID);
	}
	public void setSwitchUUID(String switchUUID)
	{
		set(tag_switchUUID, switchUUID);
	}
	public String getPortgroupKey()
	{
		return get(tag_portgroupKey);
	}
	public void setPortgroupKey(String portgroupKey)
	{
		set(tag_portgroupKey, portgroupKey);
	}	
	public String getBackingInfoType()
	{
		return get(tag_backingInfoType);
	}
	public void setBackingInfoType(String backingInfoType)
	{
		set(tag_backingInfoType, backingInfoType);
	}
	public String getSwitchName()
	{
		return get(tag_switchName);
	}
	public void setSwitchName(String switchName)
	{
		set(tag_switchName, switchName);
	}
	public String getPortgroupName()
	{
		return get(tag_portgroupName);
	}
	public void setPortgroupName(String portgroupName)
	{
		set(tag_portgroupName, portgroupName);
	}
	public Integer getHyperVAdapterID() {
		return get("hyperVAdapterID");
	}
	public void setHyperVAdapterID(Integer hyperVAdapterID) {
		set("hyperVAdapterID", hyperVAdapterID);
	}
	public String getNetworkId() {
		return get(tag_networkId);
	}
	public void setNetworkId(String networkId) {
		set(tag_networkId, networkId);
	}
	public String getAdapterType() {
		return get(tag_adapterType);
	}
	public void setAdapterType(String adapterType) {
		set(tag_adapterType, adapterType);
	}
	public String getParentName() {
		return get(tag_parentName);
	}
	public void setParentName(String parentName) {
		set(tag_parentName, parentName);
	}
	public String getParentId() {
		return get(tag_parentId);
	}
	public void setParentId(String parentId) {
		set(tag_parentId, parentId);
	}
}
