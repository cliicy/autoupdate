package com.ca.arcflash.webservice.jni.model;

public class JJobScriptVMNetworkConfigInfo {
	private String deviceType;
	private String label;
		
	private String backingInfoType;
	
	// network info
	// when backingType is VirtualEthernetCardNetworkBackingInfo
	private String deviceName;	
	
	// when backingType is VirtualEthernetCardDistributedVirtualPortBackingInfo
	private String switchUUID;
	private String portgroupKey;
	
	// the names are not available from VMSnapshotConfigInfo.vsci
	// but we'll use them for createVM
	private String switchName;      
	private String portgroupName;
	
	// for vApp and child VM network
	private String Id;
	private String adapterType;
	private String parentName;
	private String parentId;
	
	public String getDeviceType()
	{
		return deviceType;
	}
	public void setDeviceType(String deviceType)
	{
		this.deviceType = deviceType;
	}
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getDeviceName()
	{
		return deviceName;
	}
	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}
	public String getSwitchUUID()
	{
		return switchUUID;
	}
	public void setSwitchUUID(String switchUUID)
	{
		this.switchUUID = switchUUID;
	}
	public String getPortgroupKey()
	{
		return portgroupKey;
	}
	public void setPortgroupKey(String portgroupKey)
	{
		this.portgroupKey = portgroupKey;
	}	
	public String getBackingInfoType()
	{
		return backingInfoType;
	}
	public void setBackingInfoType(String backingInfoType)
	{
		this.backingInfoType = backingInfoType;
	}
	public String getSwitchName()
	{
		return switchName;
	}
	public void setSwitchName(String switchName)
	{
		this.switchName = switchName;
	}
	public String getPortgroupName()
	{
		return portgroupName;
	}
	public void setPortgroupName(String portgroupName)
	{
		this.portgroupName = portgroupName;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getAdapterType() {
		return adapterType;
	}
	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}
