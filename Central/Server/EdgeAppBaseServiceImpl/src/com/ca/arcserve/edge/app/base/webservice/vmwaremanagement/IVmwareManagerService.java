package com.ca.arcserve.edge.app.base.webservice.vmwaremanagement;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;

public interface IVmwareManagerService {
	
	DiscoveryVirtualMachineInfo getVMDetail(DiscoveryESXOption esxOption,DiscoveryVirtualMachineInfo vmInfo);
	
	int updateEsxServerType(DiscoveryESXOption esxOption) ;
	
	ArrayList<ESXNode> getEsxNodeList(DiscoveryESXOption esxOption) ;
	
	ArrayList<ESXNode> getEsxNodeListWithOriginal(DiscoveryESXOption esxOption)throws EdgeServiceFault ;
	
	List<DiscoveryVirtualMachineInfo> getVmList(DiscoveryESXOption esxOption,ESXNode esxNode, Module userModule, boolean onlyWindows);
	
	DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive) throws EdgeServiceFault ;
	
	DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive, String esxName) throws EdgeServiceFault ;
	
	List<String> getNetworkAdapterTypeByOs(DiscoveryESXOption esxOption, ESXNode eNode);
	
	ArrayList<String> getVirtualNetworkList(DiscoveryESXOption esxOption, ESXNode eNode);
	
	String getVMHostName(DiscoveryESXOption esxOption,String vmName, String vmUUID);
	
	VM_Info getVMInfo(DiscoveryESXOption esxOption,String vmName, String vmUUID)throws Exception ;
	
	VM_Info getVMInfoWithOriginal(DiscoveryESXOption esxOption,String vmName, String vmUUID)throws Exception ;
	
	int checkAndEnableCBT(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception;
	
	int checkForHWSnaphostSupport(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception;
	int checkVMToolsVersion(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception ;
	
	ArrayList<Disk_Info> getDiskInfoForEdge(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	powerState getVMPowerStatus(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	String getEsxVersion(DiscoveryESXOption esxOption)throws Exception;
	
	boolean isVMHasIDEDisks(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	boolean isVMHasSATADisks(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	boolean isVMHasSCSISlots(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	boolean isVMHasSharedSCSI(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	List<Disk_Info> getgetDiskInfoForEdge(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception;
	
	void validateEsxAccount(DiscoveryESXOption esxOption)throws EdgeServiceFault ;
	
	List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(DiscoveryESXOption esxOption);
	
	ArrayList<VM_Info> getEsxVMs(DiscoveryESXOption esxOption, ESXNode esx, boolean getAll) throws Exception ;
	
	String[] getESXLicenseType(DiscoveryESXOption esxOption,String esxHost);
	
	VMwareServerType getVMwareServerType(DiscoveryESXOption esxOption)throws EdgeServiceFault;
	
	int getESXCPUSockets(DiscoveryESXOption esxOption, String esxName)throws Exception;
	
	boolean isVMwareEssentialLicense(DiscoveryESXOption esxOption, String esxHost)throws EdgeServiceFault;
	
	void addConnection(DiscoveryESXOption esxOption)throws EdgeServiceFault;
	
	void cleanCache();
	
	void close();
	
	EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, VWWareESXNode esxNode) throws EdgeServiceFault;
	
	VM_Info getVMInfoByMoId(DiscoveryESXOption esxOption, String vmMoId) throws Exception; // get VM info by Managed Object ID
	int validateVMCredential(DiscoveryESXOption esxOption, String vmName, String vmUuid, String vmUsername, String vmPassword)throws Exception;
	
}
