package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ha.model.ESXNetworkInfo;
import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.Host_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.webservice.data.VWWareESXNode;

public class VMwareService {
	
	private static VMwareService instance = new VMwareService();
	
	private VMwareService() {}
	
	public static VMwareService getInstance() {
		return instance;
	}
	
	public EsxServerInformation getEsxServerInformation(CAVirtualInfrastructureManager vmwareManager, 
			String esxServer, String username, String passwod, String protocol, int port) throws Exception {
		EsxServerInformation vmWareServerInfo = new EsxServerInformation();
		vmWareServerInfo.setEsxServer(esxServer);
		vmWareServerInfo.setUserName(username);
		vmWareServerInfo.setPasswod(passwod);
		vmWareServerInfo.setProtocol(protocol);
		vmWareServerInfo.setPort(port);
		
		VMwareServerType serverType = vmwareManager.getVMwareServerType();
		switch (serverType) {
		case esxServer:
			vmWareServerInfo.setServerType(1);
			break;
		case virtualCenter:
			vmWareServerInfo.setServerType(2);
			break;
		default:
			vmWareServerInfo.setServerType(0);
			break;
		}
		
		String serverVersion = vmwareManager.GetESXServerVersion();
		vmWareServerInfo.setServerVersion(serverVersion);
		
		ArrayList<ESXNode> nodes = vmwareManager.getESXNodeList();
		if (nodes == null || nodes.isEmpty()) {
			return vmWareServerInfo;
		}

		for (ESXNode node : nodes) {
			if (node.getConnectionState().equalsIgnoreCase("connected")) {
				vmWareServerInfo.addEsxNode(new VWWareESXNode(
						node.getEsxName(), node.getEsxMoID(), node.getDataCenter(), node.getDcDisplayName()));
			}
		}

		return vmWareServerInfo;
	}
	
	public EsxHostInformation getEsxHostInformation(CAVirtualInfrastructureManager vmwareManager, 
			VWWareESXNode esxNode) throws Exception {
		EsxHostInformation hostInformation = new EsxHostInformation();
		
		// getVmStorages
		ESXNode eNode = new ESXNode();
		eNode.setDataCenter(esxNode.getDataCenter());
		
		eNode.setEsxName(esxNode.getEsxName());
		eNode.setClusterName(esxNode.getClusterName());
		
		eNode.setEsxMoID(esxNode.getEsxMoID());
		
		vmwareManager.updateESXHostName(eNode);
		
		VMwareStorage[] results = vmwareManager.getVMwareStorages(eNode, null);
		List<VMStorage> vmRet = new ArrayList<VMStorage>();
		for (VMwareStorage storage : results) {
			if (!storage.isAccessible())
				continue;
			VMStorage tmp = new VMStorage();
			tmp.setName(storage.getName());
			tmp.setColdStandySize(storage.getColdStandySize());
			tmp.setFreeSize(storage.getFreeSize());
			tmp.setTotalSize(storage.getTotalSize());
			tmp.setOtherSize(storage.getOtherSize());
			vmRet.add(tmp);
		}
		hostInformation.setVmStorages(vmRet.toArray(new VMStorage[0]));

		// getNetworkType
		List<String> os_NicTypesMap = vmwareManager.getOsSupportedVirtualNetworkAdapterMap(eNode);
		List<String> result = new ArrayList<String>();
		if (os_NicTypesMap != null) {
			for (String os_nic : os_NicTypesMap) {
				String[] tempNic = os_nic.substring(os_nic.indexOf("+") + 1).split(",");
				for (String nic : tempNic) {
					// nic like: VirtualE1000e , should be E1000E
					String nicType = nic.substring(nic.indexOf("Virtual") + 7).toUpperCase().trim();
					if (!result.contains(nicType))
						result.add(nicType);
				}
			}
		}
		if (result.contains("PCNET32")){
			result.remove("PCNET32");
        }
		
		if (result.contains("VMXNET")){
			result.remove("VMXNET");	
		}
		
		if (result.contains("SRIOVETHERNETCARD")) {
			result.remove("SRIOVETHERNETCARD");
		}
		
		if (result.contains("Flexible")) {
			result.remove("Flexible");
		}
		
		hostInformation.setNetworkAdapterTypes(result.toArray(new String[result.size()]));

		// getNetworkConnection
//		ArrayList<String> networkList = vmwareManager.getVirtualNetworkList(eNode);
		//enhance function to support cluster.
		ArrayList<VirtualNetworkInfo> networkList = vmwareManager.getVirtualNetworkList(eNode);
		if (networkList == null || networkList.size() == 0) {
			hostInformation.setNetworkConnections(new String[0]);
			hostInformation.setNetworkConnectionsMOB(new ArrayList<ESXNetworkInfo>());
		} else {
			String[] networkConnections = new String[networkList.size()];
			ArrayList<ESXNetworkInfo> networkConnectionInfos = new ArrayList<ESXNetworkInfo>();
			for (int i = 0; i < networkList.size(); i++) {
				networkConnections[i] = networkList.get(i).getVirtualName();
				networkConnectionInfos.add(new ESXNetworkInfo(networkList.get(i).getVirtualName(), networkList.get(i).getVirtualReferenceID(), networkList.get(i).isVDS()));
			}
			hostInformation.setNetworkConnections(networkConnections);
			hostInformation.setNetworkConnectionsMOB(networkConnectionInfos);
		}

		// getVirtualizationSuppoortInformation
		ESXServerInfo supportInfo = new ESXServerInfo();
		Host_Info info = vmwareManager.getHostInfo(eNode);
		if (info != null) {
			supportInfo.setCpuCount(info.getCpuCount());
			supportInfo.setMemorySize(info.getMemorySize());
			supportInfo.setAvailableMemorySize(info.getAvailableMemorySize());
		}
		hostInformation.setSupportInfo(supportInfo);

		return hostInformation;
	}
	
}
