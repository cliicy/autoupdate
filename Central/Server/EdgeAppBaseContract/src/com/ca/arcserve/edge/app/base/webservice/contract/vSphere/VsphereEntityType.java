package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

/**
 * original servertype just only contains Esx and vcenter,
 * please rerence : com.ca.arcflash.ha.vmwaremanager.VMwareServerType
 * For not changing original logic, we set the same vcenter and esx value with the VMwareServerType class.
 * @author zhaji22
 *
 */
public enum VsphereEntityType {
	UNKNOWN(0),
	esxServer(1),
	vCenter(2),
	vCloudDirector(3),
	organization(4), 
	virtualDataCenter(5), 
	vAPP(6),
	vm(7);
	
	private final int value;
	private VsphereEntityType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static VsphereEntityType parse(int value) {
		VsphereEntityType[] types = VsphereEntityType.values();
		for (VsphereEntityType type : types) {
			if (type.value == value) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
