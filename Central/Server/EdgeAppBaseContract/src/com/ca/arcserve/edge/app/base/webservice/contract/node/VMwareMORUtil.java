package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class VMwareMORUtil {
	
	public final static int MOR_NONE = 0x0000;
	public final static int MOR_DATA_CENTER = 0x0001;//dataCenter
	public final static int MOR_FOLDER=0x0002;//folder
	public final static int MOR_HOST_SYSTEM=0x0004;//hostsystem
	public final static int MOR_COMPUTER_RESOURCE=0x0008;//ComputeResource
	public final static int MOR_RESOURCE_POOL=0x0010;//resourcePool
	public final static int MOR_VIRTUAL_APP=0x0020;//virtualapp
	public final static int MOR_VIRTUAL_MACHINE=0x0040;//VirtualMachine
	public final static int MOR_CLUSTER_COMPUTER_RESOURCE=0x0080;//clustercomputeresource
	
	public static boolean hasBit(int bitmap, int morType) {
		return (bitmap & morType) == morType;
	}
	
	public static int getVMLocationTreeBitmap(){
		return MOR_NONE | MOR_DATA_CENTER | MOR_FOLDER | MOR_HOST_SYSTEM | MOR_COMPUTER_RESOURCE | MOR_RESOURCE_POOL | MOR_VIRTUAL_APP | MOR_CLUSTER_COMPUTER_RESOURCE;
	}

}
