package com.ca.arcserve.edge.app.base.webservice.vmwaremanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.service.VMwareService;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMServiceUtil;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.VMwareManagerAdapter;

public class VmwareManagerServiceImpl implements IVmwareManagerService{

	private VMwareManagerAdapter vmwareAdepter = VMwareManagerAdapter.getInstance();
	private static final Logger logger = Logger.getLogger(VmwareManagerServiceImpl.class);
	private Map<String,CAVirtualInfrastructureManager> esxServerCache = new ConcurrentHashMap<String,CAVirtualInfrastructureManager>();
	
	private String getKey(DiscoveryESXOption esxOption){
		return esxOption.getEsxServerName().concat(esxOption.getEsxUserName().concat(esxOption.getEsxPassword()));
	}
	
	private CAVirtualInfrastructureManager getService(DiscoveryESXOption esxOption) throws EdgeServiceFault{
		CAVirtualInfrastructureManager vmwareManager;
		String keyString = getKey(esxOption);
		synchronized (esxServerCache) {
			vmwareManager = esxServerCache.get(keyString);
		}
		if(vmwareManager == null){
			vmwareManager = vmwareAdepter.createVMWareManager(esxOption);
			synchronized (esxServerCache) {
				esxServerCache.put(keyString, vmwareManager);
			}
		}
		
		return vmwareManager;
	}
	
	@Override
	public DiscoveryVirtualMachineInfo getVMDetail( DiscoveryESXOption esxOption, DiscoveryVirtualMachineInfo vmInfo) {
		
		DiscoveryVirtualMachineInfo rstVmInfo = null;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			rstVmInfo = vmwareAdepter.getVMDetails(vmwareManager, vmInfo);
			
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		}
		return rstVmInfo;
	}

	@Override
	public int updateEsxServerType(DiscoveryESXOption esxOption) {
		int type = 0;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			if(vmwareManager.getVMwareServerType() == VMwareServerType.esxServer){
				type = 1; // ESX 
			}else if (vmwareManager.getVMwareServerType() == VMwareServerType.virtualCenter){
				type = 2; // vCenter
			}	
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return type;
	}

	@Override
	public ArrayList<ESXNode> getEsxNodeList(DiscoveryESXOption esxOption){
		ArrayList<ESXNode> esxNodes = null;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			esxNodes = vmwareManager.getESXNodeList();			
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return esxNodes;
	}
	
	@Override
	public ArrayList<ESXNode> getEsxNodeListWithOriginal(DiscoveryESXOption esxOption)throws EdgeServiceFault{
		ArrayList<ESXNode> esxNodes = null;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			esxNodes = vmwareManager.getESXNodeList();
			
			if (vmwareManager.getVMwareServerType() == VMwareServerType.esxServer){
				for (ESXNode esx:esxNodes)
					esx.setEsxName(vmwareManager.getServerName());
			}	
		} catch (EdgeServiceFault e) {
			throw e;
		} catch (Exception e) {
			logger.error("[VmwareManagerServiceImpl] getEsxNodeListWithOriginal() failed.",e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_InvalidInformationOrServerNotAvailable, e.getMessage());
		}
		return esxNodes;
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVmList( DiscoveryESXOption esxOption, ESXNode esxNode, Module userModule, boolean onlyWindows) {
		List<DiscoveryVirtualMachineInfo> vmList = null;
		
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			vmList = vmwareAdepter.getVMEntryList(vmwareManager, esxNode, userModule, onlyWindows);
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} 
		return vmList;
	}

	@Override
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(
			DiscoveryESXOption esxOption, boolean recursive) throws EdgeServiceFault{
		DiscoveryVmwareEntityInfo discoveryVmwareEntityInfo = null;
//		try{
//			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
//			discoveryVmwareEntityInfo = vmwareAdepter.getVmwareTreeRootEntity(vmwareManager, recursive);
//		} catch (EdgeServiceFault e) {
//			logger.error(e.getMessage(), e);
//		} 
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		discoveryVmwareEntityInfo = vmwareAdepter.getVmwareTreeRootEntity(vmwareManager, recursive);
		return discoveryVmwareEntityInfo;
	}

	@Override
	public List<String> getNetworkAdapterTypeByOs(DiscoveryESXOption esxOption,ESXNode eNode) {
		List<String> networkAdepterTypeList = null; 
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			networkAdepterTypeList = vmwareManager.getOsSupportedVirtualNetworkAdapterMap(eNode);
			
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return networkAdepterTypeList;
	}

	@Override
	public String getVMHostName(DiscoveryESXOption esxOption, String vmName, String vmUUID){
		String hostName = null;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			hostName = vmwareAdepter.queryVMHostname(vmwareManager, vmName, vmUUID);
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} 
		return hostName;
	}

	@Override
	public ArrayList<String> getVirtualNetworkList(DiscoveryESXOption esxOption, ESXNode eNode) {
		ArrayList<String> vNetworkList = new ArrayList<String>();
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			
			ArrayList<VirtualNetworkInfo> networkInfos = vmwareManager.getVirtualNetworkList(eNode);
			for (int i = 0; i < networkInfos.size(); i++) {
				vNetworkList.add(networkInfos.get(i).getVirtualName());
			}
			
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return vNetworkList;
	}
	
	@Override
	public VM_Info getVMInfo(DiscoveryESXOption esxOption, String vmName,String vmUUID) throws Exception{

		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getVMInfo(vmName, vmUUID);
	}
	
	@Override
	public VM_Info getVMInfoWithOriginal(DiscoveryESXOption esxOption, String vmName,String vmUUID) throws Exception{

		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getVMInfoWithOriginal(vmName, vmUUID);
	}
	
	@Override
	public int checkVMToolsVersion(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception {
		
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.checkVMToolsVersion(vmName, vmUUID);	
	}

	@Override
	public ArrayList<Disk_Info> getDiskInfoForEdge(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception{
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getDiskInfoForEdge(vmName, vmUUID);
	}
	
	@Override
	public String getEsxVersion(DiscoveryESXOption esxOption) throws Exception {
		
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		ArrayList<ESXNode> list = vmwareManager.getESXNodeList();
		if(vmwareManager.getVMwareServerType() == VMwareServerType.esxServer){
			return vmwareManager.getESXHostVersion(list.get(0));
		}else{
			for (ESXNode esxNode : list) {
				if (esxNode.getEsxName().equals(esxOption.getEsxHost())
						|| esxNode.getEsxName().equals(esxOption.getEsxServerName())) {
					return vmwareManager.getESXHostVersion(esxNode);
				}
			}
		}
		return null;
	}	

	@Override
	public boolean isVMHasIDEDisks(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.CheckifVMHasIDEDisks(vmName, vmUUID);
	}

	@Override
	public boolean isVMHasSATADisks(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.CheckifVMHasSATADisks(vmName, vmUUID);
	}

	@Override
	public boolean isVMHasSCSISlots(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.CheckifVMHasSCSISlots(vmName, vmUUID);
	}
	
	@Override
	public boolean isVMHasSharedSCSI(DiscoveryESXOption esxOption, String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.CheckifVMHasSharedSCSI(vmName, vmUUID);
	}
	

	@Override
	public ArrayList<Disk_Info> getgetDiskInfoForEdge( DiscoveryESXOption esxOption, String vmName, String vmUUID)throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getDiskInfoForEdge(vmName, vmUUID);
	}
	

	@Override
	public int checkAndEnableCBT(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.CheckAndEnableCBT(vmName, vmUUID);
	}
	
	@Override
	public int checkForHWSnaphostSupport(DiscoveryESXOption esxOption,String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		logger.info("checking for hardware snapshot support for VM" + vmName);
			return vmwareManager.checkForHWSnaphostSupport(vmName, vmUUID);
		}
	@Override
	public powerState getVMPowerStatus(DiscoveryESXOption esxOption,
			String vmName, String vmUUID) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getVMPowerstate(vmName, vmUUID);
	}

	@Override
	public void addConnection(DiscoveryESXOption esxOption) throws EdgeServiceFault{

		String keyString = esxOption.getEsxHost().concat(esxOption.getEsxServerName().concat(esxOption.getEsxPassword()));
		CAVirtualInfrastructureManager vmManager;
		synchronized (esxServerCache) {
			vmManager = esxServerCache.get(keyString);
		}
		if( vmManager == null){
			vmManager = vmwareAdepter.createVMWareManager(esxOption);
			synchronized (esxServerCache) {
				esxServerCache.put(keyString, vmManager);
			}
		}
	}
	
	@Override
	public void cleanCache(){
		close();
	}
	
	@Override
	public void close(){
		synchronized (esxServerCache) {
			for (CAVirtualInfrastructureManager manager : esxServerCache.values()) {
				if (manager != null) {
					try {
						manager.close();
					} catch (Exception e) {
						logger.debug("Close VMware webservice client failed");
					}
				}
			}
			esxServerCache.clear();
		}
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(DiscoveryESXOption esxOption) {

		List<DiscoveryVirtualMachineInfo> rst = null;
		try{
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			rst = vmwareAdepter.getVMEntryList(vmwareManager);
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
		} 
		return rst;
	}

	@Override
	public ArrayList<VM_Info> getEsxVMs(DiscoveryESXOption esxOption, ESXNode esx, boolean getAll) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getVMNames(esx, getAll);
	}

	@Override
	public VMwareServerType getVMwareServerType(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		try {
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			return vmwareManager.getVMwareServerType();	
		} catch (EdgeServiceFault e) {
			throw e;
		}catch (Exception e) {
			logger.error("[VmwareManagerServiceImpl] getVMwareServerType() failed.",e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_InvalidInformationOrServerNotAvailable, e.getMessage());
		}
	}

	@Override
	public int getESXCPUSockets(DiscoveryESXOption esxOption, String esxName) throws Exception {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);	
		return vmwareManager.getESXCPUSockets(esxName);
	}
	
	@Override
	public void validateEsxAccount(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		vmwareAdepter.createVMWareManager(esxOption);
	}

	@Override
	public String[] getESXLicenseType(DiscoveryESXOption esxOption,	String esxHost) {
		String [] rst = null;
		try {
			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
			rst = vmwareManager.getESXLicenseType(esxHost);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rst;
	}

	@Override
	public boolean isVMwareEssentialLicense(DiscoveryESXOption esxOption,
			String esxHost) throws EdgeServiceFault {
		CAVirtualInfrastructureManager vmwareManager;
		vmwareManager = getService(esxOption);
		return VSphereLicenseCheck.isVMwareEssentialLicense(vmwareManager,esxHost);
		
	}

	@Override
	public EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		
		try {
			return VMwareService.getInstance().getEsxServerInformation(vmwareManager,
					esxOption.getEsxServerName(), esxOption.getEsxUserName(), esxOption.getEsxPassword(), 
					esxOption.getProtocol().toString(), esxOption.getPort());
		} catch (Exception e) {
			logger.error("Failed to getEsxServerInformation.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_CantObtainVirtualMachinesFromESXOrVC, e.getMessage());
		}
	}

	@Override
	public EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, VWWareESXNode esxNode) throws EdgeServiceFault {
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		
		try {
			return VMwareService.getInstance().getEsxHostInformation(vmwareManager, esxNode);
		} catch (Exception e) {
			logger.error("Failed to getEsxHostInformation.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_CantObtainVirtualMachinesFromESXOrVC, e.getMessage());
		}
	}

	@Override
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(
			DiscoveryESXOption esxOption, boolean recursive, String esxName) throws EdgeServiceFault {
		DiscoveryVmwareEntityInfo discoveryVmwareEntityInfo = null;
//		try{
//			CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
//			discoveryVmwareEntityInfo = vmwareAdepter.getVmwareTreeRootEntity(vmwareManager, recursive, esxName);
//		} catch (EdgeServiceFault e) {
//			logger.error(e.getMessage(), e);
//		} 
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		logger.info("getVmwareTreeRootEntity() morType: "+esxOption.getMorType());
		if(esxOption.getMorType()>0){
			discoveryVmwareEntityInfo = vmwareAdepter.getVmwareTreeRootEntity(vmwareManager, recursive, esxOption.getMorType(), esxName);
		}else{
			discoveryVmwareEntityInfo = vmwareAdepter.getVmwareTreeRootEntity(vmwareManager, recursive, esxName);
		}
		logger.debug("getVmwareTreeRootEntity() return: "+InstantVMServiceUtil.printObject(discoveryVmwareEntityInfo));
		return discoveryVmwareEntityInfo;
	}

	@Override
	public VM_Info getVMInfoByMoId( DiscoveryESXOption esxOption, String vmMoId ) throws Exception
	{
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.getVMInfoByMoId(vmMoId);
	}

	@Override
	public int validateVMCredential(DiscoveryESXOption esxOption, String vmName, String vmUuid, String vmUsername, String vmPassword) throws Exception  
	{
		CAVirtualInfrastructureManager vmwareManager = getService(esxOption);
		return vmwareManager.validateVMCredential(vmName, vmUuid, vmUsername, vmPassword);
	}
}
