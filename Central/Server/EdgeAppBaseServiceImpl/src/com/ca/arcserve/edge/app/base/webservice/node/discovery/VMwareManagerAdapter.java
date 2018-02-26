package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.InvalidLoginException;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareInfrastructureEntityInfo;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;

public class VMwareManagerAdapter {
	
	private static VMwareManagerAdapter instance = new VMwareManagerAdapter();
	private static Logger logger = Logger.getLogger(VMwareManagerAdapter.class);
	
	private VMwareManagerAdapter() {
	}
	
	public static VMwareManagerAdapter getInstance() {
		return instance;
	}
	
	public CAVirtualInfrastructureManager createVMWareManager(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		if (esxOption == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_InvalidEsxServerOrVCName, "");
		}
		
		if (esxOption.getEsxServerName() == null || esxOption.getEsxServerName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_InvalidEsxServerOrVCName, "");
		}

		if (esxOption.getEsxUserName() == null || esxOption.getEsxUserName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_InvalidUser, "");
		}

		if (esxOption.getEsxPassword() == null) {
			esxOption.setEsxPassword("");
		}
		
		CAVirtualInfrastructureManager manager = null;

		try {
			manager = createVMWareManager(
					esxOption.getEsxServerName(), 
					esxOption.getEsxUserName(), 
					esxOption.getEsxPassword(), 
					esxOption.getProtocol(),
					esxOption.isIgnoreCertificate(),
					esxOption.getPort());
		} catch (InvalidLoginException e) {
			this.throwExcetpion(e, "autoDiscovery_ESX_ConnectFail_InvalidLoginException", EdgeServiceErrorCode.Node_ESX_ConnectFail_InvalidLoginException);
		}
		
		catch (Exception e) {
			if (e.getCause() instanceof NoRouteToHostException) {
				throwExcetpion(e, "autoDiscovery_ESX_ConnectFail_NoRouteToHostException", EdgeServiceErrorCode.Node_ESX_ConnectFail_NoRouteToHostException);
			} else if (e.getCause() instanceof SocketException || e.getCause() instanceof UnknownHostException) {
				this.throwExcetpion(e, "autoDiscovery_ESX_ConnectFail_SocketException", EdgeServiceErrorCode.Node_ESX_ConnectFail_SocketException);
			} 
			else if( e instanceof RemoteException && e.getMessage().matches( "^.*[(]{1}[\\s]*301[\\s]*[)]{1}.*$" )  ) {
				this.throwExcetpion(e, "autoDiscovery_ESX_ConnectFail_HTTPRedirect", EdgeServiceErrorCode.NODE_ESX_HTTP_REDIRECT );
			}
			else {
				this.throwExcetpion(e, "autoDiscovery_ESX_ConnectFail", EdgeServiceErrorCode.Node_ESX_InvalidInformationOrServerNotAvailable);
			}
		}
		
		if (manager == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_InvalidInformationOrServerNotAvailable, "");
		}
		
		// [lijwe02] fix defect 37133 check version, the minimum version is 4.0 >>
		String version = null;
		try {
			// The version of the API as a dot-separated string. For example, "1.0.0".
			version = manager.GetESXServerVersion();
		} catch (Exception e) {
			logger.error("Error on getESXServerVersion.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.NODE_ESX_SERVER_VERSION_RETRIEVE_FAIL,
					"Failed to get the ESX Server Version");
		}
		if (version == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.NODE_ESX_SERVER_VERSION_RETRIEVE_FAIL,
					"Failed to get the ESX Server Version");
		}
		logger.info("ESXServerVersion is:" + version);
		String[] parts = version.split("\\.");
		if (parts.length > 0) {
			int mainVersion = Integer.parseInt(parts[0]);
			if (mainVersion < 4) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.NODE_ESX_SERVER_VERSION_NOT_SUPPORT,
						"Version is lower than 4.0");
			}
		} else {
			logger.error("Version " + version + "format is not league!");
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.NODE_ESX_SERVER_VERSION_RETRIEVE_FAIL,
					"Failed to get the ESX Server Version");
		}
		// << lijwe02
		
		return manager;
	}
	
	private void throwExcetpion(Exception e, String resorce, String errorCode) throws EdgeServiceFault {
		String logMessage = EdgeCMWebServiceMessages.getResource(resorce);
		logger.error(logMessage, e);
		throw EdgeServiceFault.getFault(errorCode, logMessage);
	}
	
	protected CAVirtualInfrastructureManager createVMWareManager(
			String esxServerName,
			String esxUsername,
			String esxPassword,
			Protocol protocol,
			boolean ignoreCertification,
			int port) throws Exception {
		String protocolStr = protocol == Protocol.Https ? "https" : "http";
		return CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
				esxServerName, esxUsername, esxPassword, protocolStr, ignoreCertification, port);
	}
	
	public void validateEsxAccount(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		createVMWareManager(esxOption);
	}
	
	public List<ESXNode> getEsxNodeList(CAVirtualInfrastructureManager vmwareManager) throws EdgeServiceFault {
		if (vmwareManager == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		try {
			List<ESXNode> nodeList = vmwareManager.getESXNodeList();
			
			if (nodeList == null) {
				nodeList = new ArrayList<ESXNode>();
			}
			
			logger.debug("There are " + nodeList.size() + " ESX nodes discovered.");
			
			Iterator<ESXNode> esxNodeIterator = nodeList.iterator();
			while (esxNodeIterator.hasNext()) {
				ESXNode esxNode = esxNodeIterator.next();
				if (esxNode.isSkipNode()) {
					esxNodeIterator.remove();
				}
			}
			
			logger.debug("After skipping some ESX nodes, there are " + nodeList.size() + " left.");
			
			return nodeList;
		} catch (Exception e) {
			throw handleVMwareException(e);
		}
	}
	
	public List<DiscoveryVirtualMachineInfo> getVMEntryList(CAVirtualInfrastructureManager vmwareManager, ESXNode esxNode, Module userModule) throws EdgeServiceFault {
		return getVMEntryList(vmwareManager, esxNode, userModule, true);
	}
	
	public List<DiscoveryVirtualMachineInfo> getVMEntryList(CAVirtualInfrastructureManager vmwareManager, ESXNode esxNode, Module userModule, boolean onlyWindows) throws EdgeServiceFault {
		if (vmwareManager == null || esxNode == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		List<DiscoveryVirtualMachineInfo> vmList = new ArrayList<DiscoveryVirtualMachineInfo>();
		ArrayList<VM_Info> arrVm = null;
		
		String esxNodeName = esxNode.getEsxName();
		
		try {
			// The VM_Info will contain VMName, Instance UUID and VM HostName (if the vmware tools are up and running).
			// Then invoke public VM_Info getVMInfo(String vmName, String vmInstanceUUID) to get rest properties.
			logger.debug("Start to discover VM from ESX node " + esxNodeName);
			if (!onlyWindows)
				arrVm = vmwareManager.getVMNames(esxNode,true);
			else
				arrVm = vmwareManager.getVMNamesforEdge(esxNode);
			logger.debug("Discovered VM count is " + (arrVm == null ? 0 : arrVm.size()));
		} catch (Exception e) {
			throw handleVMwareException(e);
		}
		
		if (arrVm == null) {
			return vmList;
		}
		
		int type = 0;
		VMwareServerType server = null;
		try {
			server = vmwareManager.getVMwareServerType();
		} catch (Exception e) {
			throw handleVMwareException(e);
		}
		if(server != null)
		{
			if(server == VMwareServerType.esxServer)
				type = 1;
			else if(server == VMwareServerType.virtualCenter)
				type = 2;
			else
				type = 0;
		}

		for (VM_Info info: arrVm) {
			if (info == null) {
				continue;
			}
			
			DiscoveryVirtualMachineInfo vmInfo = new DiscoveryVirtualMachineInfo();
			
			vmInfo.setVmName(info.getVMName());
			
			// fix TFS bug 752024 - auto discovery cannot get hostname of powered off VM
			//vmInfo.setVmHostName(info.getVMPowerstate() ? info.getVMHostName() : "");
			vmInfo.setVmHostName(info.getVMHostName());
			
			vmInfo.setVmInstanceUuid(info.getVMvmInstanceUUID());
			vmInfo.setVmGuestOS(info.getvmGuestOS());
			vmInfo.setVmServerType(type);
			if(StringUtil.isEmptyOrNull(info.getvmEsxHost()))
				vmInfo.setVmEsxHost(esxNodeName);
			else
				vmInfo.setVmEsxHost(info.getvmEsxHost());
			vmInfo.setWindowsOS(info.getvmGuestOS()!=null && info.getvmGuestOS().contains("Microsoft"));
			vmInfo.setVmConnectionState(info.getConnectionState());
			vmInfo.setVmEsxSocketCount(info.getCpuSocketCount());
			vmInfo.setVmEsxEssential(info.isEssentialLicense());
			
			vmList.add(vmInfo);
		}
		
		String message = EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_DiscoverInfo",
				String.valueOf(esxNode.getiTotalVMCount()), esxNode.getEsxName());
		logger.info(message);
		
//		ActivityLogServiceImpl activityLogService = new ActivityLogServiceImpl();
//		ActivityLog log = new ActivityLog();
//		log.setModule( userModule );
//		log.setSeverity( Severity.Information );
//		log.setNodeName( esxNode.getEsxName() );
//		log.setMessage(message);
//		log.setTime( new Date() );
//		activityLogService.addLog(log);
		
		return vmList;
	}
	
	public List<DiscoveryVirtualMachineInfo> getVMEntryList(CAVirtualInfrastructureManager vmwareManager) throws EdgeServiceFault {
		List<ESXNode> esxNodeList = getEsxNodeList(vmwareManager);
		
		List<DiscoveryVirtualMachineInfo> vmList = new LinkedList<DiscoveryVirtualMachineInfo>();
		
		for (ESXNode esxNode : esxNodeList) {
			List<DiscoveryVirtualMachineInfo> esxNodeVMList = getVMEntryList(vmwareManager, esxNode, Module.ImportNodesFromHypervisor);
			vmList.addAll(esxNodeVMList);
		}
		
		return vmList;
	}
	
	private DiscoveryVmwareEntityInfo convertFromVMwareInfrastructureEntityInfo(VMwareInfrastructureEntityInfo info, DiscoveryVmwareEntityInfo current,  boolean isESX, String esxName) {
		if(info == null) return null;
		DiscoveryVmwareEntityInfo dvEntity = new DiscoveryVmwareEntityInfo();
		dvEntity.setName(info.getName());
		dvEntity.setType(info.getType());
		dvEntity.setRefId( info.getMorId() );
		dvEntity.setGlobalIdForwebService( "vmware_entity_"+ info.getType() + "_" + info.getName() ); 
		dvEntity.setParent( current );
		if(dvEntity.getType() != null && dvEntity.getType().equalsIgnoreCase("VirtualMachine") && 
			info.getVmInfo() != null) {
			DiscoveryVirtualMachineInfo vmInfo = new DiscoveryVirtualMachineInfo();
			VM_Info detailInfo = info.getVmInfo();
			
			vmInfo.setVmName(detailInfo.getVMName());
			vmInfo.setVmUuid(detailInfo.getVMUUID());
			vmInfo.setVmHostName(detailInfo.getVMPowerstate() ? detailInfo.getVMHostName() : "");
			vmInfo.setVmInstanceUuid(detailInfo.getVMvmInstanceUUID());
			vmInfo.setVmEsxHost(isESX?esxName:detailInfo.getvmEsxHost());
			vmInfo.setVmEsxSocketCount(detailInfo.getCpuSocketCount());
			vmInfo.setVmEsxEssential(detailInfo.isEssentialLicense());
			vmInfo.setVmXPath(detailInfo.getVMVMX());
			vmInfo.setVmGuestOS(detailInfo.getvmGuestOS());
			vmInfo.setVmIP(detailInfo.getVMPowerstate() ? detailInfo.getVMIP() : "");
			vmInfo.setbRunning(detailInfo.getVMPowerstate());
			vmInfo.setWindowsOS(detailInfo.getvmGuestOS()!=null && detailInfo.getvmGuestOS().contains("Microsoft"));
			vmInfo.setVmConnectionState(detailInfo.getConnectionState());
			vmInfo.setManagedByVCloud(detailInfo.isManagedByVCloud());
			dvEntity.setVMInfo(vmInfo);
		}
		
		for(VMwareInfrastructureEntityInfo child : info.getChildren()) {
			dvEntity.addChild(convertFromVMwareInfrastructureEntityInfo(child, dvEntity,isESX, esxName));
		}
		
		return dvEntity;
	}
	
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(CAVirtualInfrastructureManager vmwareManager, boolean recursive) throws EdgeServiceFault {
		DiscoveryVmwareEntityInfo dvEntity = null;
		try {
			VMwareInfrastructureEntityInfo vmWareEntity = vmwareManager.getVmwareTreeRootEntity(null, recursive);
			if(vmWareEntity != null) {
				dvEntity = convertFromVMwareInfrastructureEntityInfo(vmWareEntity, null, vmwareManager.getVMwareServerType() == VMwareServerType.esxServer, "");
			}
		} catch (Exception e) {
			logger.error("Error on getVmwareTreeRootEntity.", e);
		}
		
		return dvEntity;
	}
	
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(CAVirtualInfrastructureManager vmwareManager, boolean recursive, String esxName) throws EdgeServiceFault {
		DiscoveryVmwareEntityInfo dvEntity = null;
		try {
			VMwareInfrastructureEntityInfo vmWareEntity = vmwareManager.getVmwareTreeRootEntity(null, recursive);
			if(vmWareEntity != null) {
				dvEntity = convertFromVMwareInfrastructureEntityInfo(vmWareEntity, null,vmwareManager.getVMwareServerType() == VMwareServerType.esxServer, esxName);
			}
		} catch (Exception e) {
			logger.error("Error on getVmwareTreeRootEntity.", e);
		}
		
		return dvEntity;
	}
	
	//this method is only for ivm.
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(CAVirtualInfrastructureManager vmwareManager, boolean recursive, int morType, String esxName) throws EdgeServiceFault {
		DiscoveryVmwareEntityInfo dvEntity = null;
		try {
			VMwareInfrastructureEntityInfo vmWareEntity = vmwareManager.getVmwareTreeRootEntity(null, recursive, morType);
			if(vmWareEntity != null) {
				boolean isEsx = vmwareManager.getVMwareServerType() == VMwareServerType.esxServer;
				dvEntity = convertFromVMwareInfrastructureEntityInfo(vmWareEntity, null, isEsx, esxName);
//				dvEntity = filterEntity(dvEntity, isEsx);
//				dvEntity = getConnectInfo(vmwareManager, vmWareEntity, dvEntity);
				if(vmwareManager.getVMwareServerType() == VMwareServerType.virtualCenter){
					dvEntity.setvCenter(true);
				}else{
					dvEntity.setvCenter(false);
				}
			}
		} catch (Exception e) {
			logger.error("Error on getVmwareTreeRootEntity.", e);
		}
		
		return dvEntity;
	}
	
//	private DiscoveryVmwareEntityInfo filterEntity(DiscoveryVmwareEntityInfo dvEntity, boolean isESX){
//		if(isESX){
//			while(true){
//				if(dvEntity.getChildren()!=null && dvEntity.getChildren().size()!=0){
//					if(dvEntity.getType() != null && dvEntity.getType().equalsIgnoreCase("HostSystem")) {
//						break;
//					}else{
//						dvEntity = dvEntity.getChildren().get(0);
//					}
//				}else{
//					break;
//				}
//			}
//		}
//		
//		dvEntity.setParent(null);
//		return dvEntity;
//	}
	
//	private DiscoveryVmwareEntityInfo getConnectInfo(CAVirtualInfrastructureManager vmwareManager, VMwareInfrastructureEntityInfo vmWareEntity, DiscoveryVmwareEntityInfo dvEntity)throws EdgeServiceFault{
//		VMwareInfrastructureEntityInfo current = vmWareEntity;
//		try {
//			if(vmwareManager.getVMwareServerType() == VMwareServerType.esxServer) {
//				while(true) {
//					if(current != null) {
//						if(current.getType() != null && current.getType().equalsIgnoreCase("Datacenter")) {
//							dvEntity.setDcName(current.getName());
//						} else if(current.getType() != null && current.getType().equalsIgnoreCase("HostSystem")){
//							dvEntity.setEsxName(current.getName());
//						}
//						
//						if(current.getChildren()!=null && current.getChildren().size() > 0){
//							current = current.getChildren().get(0);
//						}else{
//							break;
//						}
//						
//					}else{
//						break;
//					}
//				} 
//			}
//		} catch (Exception e1) {
//			throw handleVMwareException(e1);
//		}
//		
//		return dvEntity;
//	}
	
	
	public DiscoveryVirtualMachineInfo getVMDetails(CAVirtualInfrastructureManager vmwareManager, DiscoveryVirtualMachineInfo vmEntry) throws EdgeServiceFault {
		if (vmwareManager == null || vmEntry == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		VM_Info detailInfo = null;
		
		try {
			detailInfo = vmwareManager.getVMInfo(vmEntry.getVmName(), vmEntry.getVmInstanceUuid());
		} catch (Exception e) {
			throw handleVMwareException(e);
		}
		
		DiscoveryVirtualMachineInfo vmInfo = new DiscoveryVirtualMachineInfo();
		
		vmInfo.setVmName(detailInfo.getVMName());
		vmInfo.setVmUuid(detailInfo.getVMUUID());
		vmInfo.setVmHostName(detailInfo.getVMPowerstate() ? detailInfo.getVMHostName() : "");
		vmInfo.setVmInstanceUuid(detailInfo.getVMvmInstanceUUID());
		vmInfo.setVmEsxHost(detailInfo.getvmEsxHost());
		vmInfo.setVmXPath(detailInfo.getVMVMX());
		vmInfo.setVmGuestOS(detailInfo.getvmGuestOS());
		vmInfo.setVmIP(detailInfo.getVMPowerstate() ? detailInfo.getVMIP() : "");
		vmInfo.setbRunning(detailInfo.getVMPowerstate());
		vmInfo.setVmEsxSocketCount(detailInfo.getCpuSocketCount());
		vmInfo.setVmEsxEssential(detailInfo.isEssentialLicense());
		
		return vmInfo;
	}
	
	public String queryVMHostname(CAVirtualInfrastructureManager vmwareManager, String vmName, String vmUUID) throws EdgeServiceFault{
		try {
			VM_Info vm = vmwareManager.getVMInfo(vmName, vmUUID);
			return vm.getVMPowerstate() ? vm.getVMHostName() : "";
		} catch (Exception e) {
			logger.error("can't get vm hostname:", e);
			throw handleVMwareException(e);
		}
	}
	
	private EdgeServiceFault handleVMwareException(Exception e) {
		String logMessage = EdgeCMWebServiceMessages.getResource("autoDiscovery_ESX_DiscoverFail");
		logger.error(logMessage, e);
		return EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_CantObtainVirtualMachinesFromESXOrVC, logMessage);
	}

}
