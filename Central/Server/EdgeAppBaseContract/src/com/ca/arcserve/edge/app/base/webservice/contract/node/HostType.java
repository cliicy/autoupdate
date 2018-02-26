package com.ca.arcserve.edge.app.base.webservice.contract.node;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum HostType implements IBit{
	EDGE_NODE_UNKNOWN (0x00000000),
	EDGE_NODE_PHYSICS_MACHINE (0x00000001),
	EDGE_NODE_VMWARE_VIRTUALMACHINE (0x00000002),
	EDGE_NODE_HYPERV_VIRTUALMACHINE (0x00000004),
	EDGE_NODE_VM_IMPORT_FROM_VSPHERE (0x00000008),
	EDGE_NODE_VCM_MONITOR (0x00000010),
	EDGE_NODE_VCM_MONITEE (0x00000020),
	EDGE_NODE_IMPORT_FROM_RHA (0x00000040),
	EDGE_NODE_IMPORT_FROM_RHA_HBBU_INTEGRATED (0x00000080),
	EDGE_NODE_IMPORT_FROM_RPS (0x00000100),
	EDGE_NODE_IMPORT_FROM_RPS_REPLICA (0x00000200),
	EDGE_NODE_VM_NONWINDOWS (0x00000400),
	EDGE_NODE_LINUX (0x00000800),
	EDGE_NODE_HYPERV_VM_AS_PHYSICAL_MACHINE(0x00001000),
	EDGE_NODE_VM_LINUX (0x00002000),
	EDGE_NODE_HYPERV_CLUSTER_VM(0x00004000),//cluster hyperv vm
	EDGE_NODE_VAPP(0x00008000),
	EDGE_NODE_VAPP_VM(0x00010000);
	
	private final int value;
	private HostType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static HostType parse(int value) {
		HostType[] types = HostType.values();
		for (HostType type : types) {
			if (type.value == value) {
				return type;
			}
		}
		return EDGE_NODE_PHYSICS_MACHINE;
	}
}
