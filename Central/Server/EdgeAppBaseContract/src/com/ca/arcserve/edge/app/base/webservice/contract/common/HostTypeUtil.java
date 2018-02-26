package com.ca.arcserve.edge.app.base.webservice.contract.common;

import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;

public class HostTypeUtil {
	
	public static int setPhysicsMachine(int hostType){
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_VM_IMPORT_FROM_VSPHERE.getValue());
		return hostType | HostType.EDGE_NODE_PHYSICS_MACHINE.getValue();
	}

	public static boolean isPhysicsMachine(int hostType){
		return ((hostType & HostType.EDGE_NODE_PHYSICS_MACHINE.getValue()) == HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
	}
	
	public static int setVMWareVirtualsMachine(int hostType){
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
		return hostType | HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue();
	}
	
	public static boolean isVMWareVirtualMachine(int hostType){
		return ((hostType & HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue()) == HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue());
	}
	
	public static int setHyperVVirtualsMachine(int hostType){
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue());
		hostType = negatedCentainType(hostType, HostType.EDGE_NODE_HYPERV_CLUSTER_VM.getValue());
		return hostType | HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue();
	}
	
	public static int setHyperVClusterVirtualsMachine(int hostType){ //cluster
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue());
		hostType = hostType | HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue(); //If it is cluster machine , same time it is hyperv virtual machine
		return hostType | HostType.EDGE_NODE_HYPERV_CLUSTER_VM.getValue();
	}
	
	public static boolean isHyperVVirtualMachine(int hostType){
		return ((hostType & HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue()) == HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
	}
	
	public static int setVMImportFromVSphere(int hostType){
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
		return hostType | HostType.EDGE_NODE_VM_IMPORT_FROM_VSPHERE.getValue();
	}
	
	public static boolean isVMImportFromVSphere(int hostType){
		return ((hostType & HostType.EDGE_NODE_VM_IMPORT_FROM_VSPHERE.getValue()) == HostType.EDGE_NODE_VM_IMPORT_FROM_VSPHERE.getValue());
	}
	
	public static int setVCMMonitor( int hostType )
	{
		return hostType | HostType.EDGE_NODE_VCM_MONITOR.getValue();
	}
	
	public static boolean isVCMMonitor( int hostType )
	{
		return ((hostType & HostType.EDGE_NODE_VCM_MONITOR.getValue()) == HostType.EDGE_NODE_VCM_MONITOR.getValue());
	}
	
	public static int setVCMMonitee( int hostType )
	{
		return hostType | HostType.EDGE_NODE_VCM_MONITEE.getValue();
	}
	
	public static boolean isVCMMonitee( int hostType )
	{
		return ((hostType & HostType.EDGE_NODE_VCM_MONITEE.getValue()) == HostType.EDGE_NODE_VCM_MONITEE.getValue());
	}
	
	public static int negatedCentainType(int input,int source){
		return input = input & ~(source);
	}
	public static int setNodeImportedFromRHA(int hostType){
		return hostType | HostType.EDGE_NODE_IMPORT_FROM_RHA.getValue();
	}
	
	public static boolean isNodeImportFromRHA(int hostType){
		return ((hostType & HostType.EDGE_NODE_IMPORT_FROM_RHA.getValue()) == HostType.EDGE_NODE_IMPORT_FROM_RHA.getValue());
	}
	
	public static boolean isNodeImportFromRHAWithHBBU(int hostType){
		return ((hostType & HostType.EDGE_NODE_IMPORT_FROM_RHA_HBBU_INTEGRATED.getValue()) == HostType.EDGE_NODE_IMPORT_FROM_RHA_HBBU_INTEGRATED.getValue());
	}
	
	public static boolean isNodeImportFromRPS(int hostType){
		return ((hostType & HostType.EDGE_NODE_IMPORT_FROM_RPS.getValue()) == HostType.EDGE_NODE_IMPORT_FROM_RPS.getValue());
	}
	
	public static boolean isNodeImportFromRPSReplica(int hostType){
		return ((hostType & HostType.EDGE_NODE_IMPORT_FROM_RPS_REPLICA.getValue()) == HostType.EDGE_NODE_IMPORT_FROM_RPS_REPLICA.getValue());
	}
	
	public static boolean isVMNonWindowsOS(int hostType){
		boolean isUnknownOS = ((hostType & HostType.EDGE_NODE_VM_NONWINDOWS.getValue()) == HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
		boolean isLinuxVM = ((hostType & HostType.EDGE_NODE_VM_LINUX.getValue()) == HostType.EDGE_NODE_VM_LINUX.getValue());
		return isUnknownOS || isLinuxVM;
	}
	
	public static int setVMNonWindowsOS(int hostType){
		return hostType | HostType.EDGE_NODE_VM_NONWINDOWS.getValue();
	}
	
	public static boolean isLinuxNode(int hostType){
		return ((hostType & HostType.EDGE_NODE_LINUX.getValue()) == HostType.EDGE_NODE_LINUX.getValue());
	}
	
	public static int setLinuxNode(int hostType){
		return hostType | HostType.EDGE_NODE_LINUX.getValue();
	}
	
	public static boolean containHostType(int hostType, HostType type) {
		return ((hostType & type.getValue()) == type.getValue());
	}

	public static boolean isHyperVVmAsPhysicalMachine(int hostType) {
		return (containHostType(hostType, HostType.EDGE_NODE_HYPERV_VM_AS_PHYSICAL_MACHINE));
	}
	
	public static boolean isLinuxVMNode(int hostType){
		return ((hostType & HostType.EDGE_NODE_VM_LINUX.getValue()) == HostType.EDGE_NODE_VM_LINUX.getValue());
	}
	
	public static int setLinuxVMNode(int hostType){
		return hostType | HostType.EDGE_NODE_VM_LINUX.getValue();
	}
	
	public static boolean isHyperVClusterVM(int hostType){
		if((hostType & HostType.EDGE_NODE_HYPERV_CLUSTER_VM.getValue()) == HostType.EDGE_NODE_HYPERV_CLUSTER_VM.getValue())
			return true;
		return false;
	}
	public static boolean isVapp(int hostType){
		if((hostType & HostType.EDGE_NODE_VAPP.getValue()) == HostType.EDGE_NODE_VAPP.getValue())
			return true;
		return false;
	}
	public static boolean isVmOfVapp(int hostType){
		if((hostType & HostType.EDGE_NODE_VAPP_VM.getValue()) == HostType.EDGE_NODE_VAPP_VM.getValue())
			return true;
		return false;
	}
	public static int setVappType(int hostType) {
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
		hostType = hostType | HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue(); //If it is cluster machine , same time it is hyperv virtual machine
		return hostType | HostType.EDGE_NODE_VAPP.getValue();
	}
	public static int setVmOfVappType(int hostType){
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_PHYSICS_MACHINE.getValue());
		hostType = negatedCentainType(hostType,HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue());
		hostType = hostType | HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue(); //If it is cluster machine , same time it is hyperv virtual machine
		return hostType | HostType.EDGE_NODE_VAPP_VM.getValue();
	}
}
