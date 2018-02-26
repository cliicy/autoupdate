package com.ca.arcflash.webservice.common;

import com.ca.arcflash.webservice.edge.license.MachineInfo;


public class VCMMachineInfo {
	private String afGUID;
	private MachineInfo machineInfo;
	private int osProductType;

//	   typedef enum _D2D_NODE_TYPE
//	   {
//		   DNT_PHYSICAL  = 0,
//		   DNT_HYPERV_VM = 1,
//		   DNT_ESX_VM    = 2,
//		   DNT_HBBU_VM   = 3,
//	   }D2D_NODE_TYPE;
	public enum VCMNodeType {
		PHYSICAL, HYPERV_VM, ESX_VM, HBBU_VM, OTHER_VM
	}
	private VCMNodeType nodeType;

	public String getAfGUID() {
		return afGUID;
	}
	public void setAfGUID(String afGUID) {
		this.afGUID = afGUID;
	}

	public MachineInfo getMachineInfo() {
		return machineInfo;
	}
	public void setMachineInfo(MachineInfo machineInfo) {
		this.machineInfo = machineInfo;
	}

	public VCMNodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(VCMNodeType nodeType) {
		this.nodeType = nodeType;
	}

	public int getOsProductType() {
		return osProductType;
	}
	public void setOsProductType(int osProductType) {
		this.osProductType = osProductType;
	}
}
