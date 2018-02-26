package com.ca.arcserve.edge.app.base.webservice.node.hypervisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.RemoteNativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.HyperVManagerAdapter;

public class HyperVSpecifier extends HypervisorSpecifier {
	
	private static Logger logger = Logger.getLogger(HyperVSpecifier.class);
	
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	HypervProtectionType hypervProtectionType = HypervProtectionType.STANDALONE;
	
	@Override
	protected LicenseMachineType getMachineType() {
		return LicenseMachineType.HYPER_V_VM;
	}

	@Override
	protected List<Result> doSpecify(Hypervisor hypervisor, List<EdgeHost> hosts) {
		List<Result> results = new ArrayList<Result>();
		
		List<JHypervVMInfo> vms = null;
		
		try {
			IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean( IRemoteNativeFacadeFactory.class );
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hypervisor.getGatewayId() );
			vms = nativeFacade.GetVmList(hypervisor.getServerName(), hypervisor.getUsername(), hypervisor.getPassword(), true);
		} catch (EdgeServiceFault e) {
			 logger.error("HyperVSpecifier - get hyper-V vm list failed.", e);
		}
		
		if (vms == null) {
			vms = new ArrayList<JHypervVMInfo>();
		}
		
		int serverId = 0;
		
		Map<String, Integer> temp=new HashMap<String, Integer>();
		for (EdgeHost host : hosts) {
			if (host.getRhostname() == null || host.getRhostname().isEmpty()) {
				continue;
			}
			
			JHypervVMInfo found = findVM(vms, host, hypervisor, hypervProtectionType);
			
			if (found != null) {
				removeLicense(host);
				String infoMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_HyperVSucceed", hypervisor.getServerName());
				results.add(new Result(LogAddEntity.create(Severity.Information, host.getRhostid(), infoMessage)));
				
				if (serverId == 0) {
					try
					{
						serverId = addServer(hypervisor, hypervProtectionType);
					}
					catch (Exception e)
					{
						logger.error( "doSpecify(): Error adding server. Server name: " + hypervisor.getServerName(), e );
					}
				}
				
				int hypervSocketCount=1;
				if(temp.containsKey(found.getHypervisor())){
					hypervSocketCount=temp.get(found.getHypervisor());
				}else{
					IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean( IRemoteNativeFacadeFactory.class );
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hypervisor.getGatewayId() );
					int socketcount=nativeFacade.getHyperVCPUSocketCount(found.getHypervisor(), hypervisor.getUsername(), hypervisor.getPassword());
					temp.put(found.getHypervisor(), socketcount);
					hypervSocketCount=temp.get(found.getHypervisor());
				}
				addVM(host, found, serverId, hypervSocketCount);
			} else {
				String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_HyperVNotBelong", hypervisor.getServerName());
				results.add(new Result(
						LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage), 
						EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_HyperVNotBelong, errorMessage)));
			}
		}
		
		return results;
	}
	
	private boolean compareVm(JHypervVMInfo hvInfo, String hostname){
		String vmHostname = hvInfo.getVmHostName();
		int index = vmHostname.indexOf(".");
		String vmHostnameWithoutDomain = index == -1 ? vmHostname : vmHostname.substring(0, index);
		
		if (hostname.equalsIgnoreCase(vmHostname) || hostname.equalsIgnoreCase(vmHostnameWithoutDomain)) {
			return true;
		} else if (hvInfo.getIpList() != null) {
			for (String ip : hvInfo.getIpList()) {
				if (ip.equalsIgnoreCase(hostname)) {
					return true;
				}
			}
		}
		return false;
	}
	private JHypervVMInfo findVM(List<JHypervVMInfo> hvList, EdgeHost edgehost, Hypervisor hypervisor, HypervProtectionType hypervProtectionType) {
		String host=hypervisor.getServerName();
		if (hypervProtectionType == HypervProtectionType.STANDALONE) {
			for (JHypervVMInfo hvInfo : hvList) {
				if(hvInfo.getVmType() == 1 && hvInfo.getHypervisor().equalsIgnoreCase(host)){
					if(compareVm(hvInfo, edgehost.getRhostname()))
						return hvInfo;
				}
			}
		} else if (hypervProtectionType == HypervProtectionType.CLUSTER) { //input is clust virtual nodes , return all the vm
			for (JHypervVMInfo hvInfo : hvList) {
				if(compareVm(hvInfo, edgehost.getRhostname()))
					return hvInfo;
			}
		} else if (hypervProtectionType == HypervProtectionType.STANDALONEANDCLUSTER) {
			for (JHypervVMInfo hvInfo : hvList) {
				if(hvInfo.getVmType() == 2 || (hvInfo.getVmType() ==1 && hvInfo.getHypervisor().equalsIgnoreCase(host))){
					if(compareVm(hvInfo, edgehost.getRhostname()))
						return hvInfo;
				}
			}
		}
		
		return null;
	}
	
	private int addServer(Hypervisor hypervisor, HypervProtectionType hypervProtectionType) throws Exception {
		List<EdgeHyperV> esxList = new ArrayList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getByName(hypervisor.getGatewayId().getRecordId(), hypervisor.getServerName(), esxList);
		
		int hyperVId = esxList.isEmpty() ? 0 : esxList.get(0).getId();
		int hypervType = hypervProtectionType == HypervProtectionType.CLUSTER ? HypervProtectionType.CLUSTER.getValue() : HypervProtectionType.STANDALONE.getValue();

		int[] output = new int[1];
		hyperVDao.as_edge_hyperv_update(hyperVId, 
				hypervisor.getServerName(), 
				hypervisor.getUsername(), 
				hypervisor.getPassword(), 
				0, 
				0,
				1,
				hypervType,
				output);
		
		if (hyperVId == 0) {
			hyperVId = output[0];
			this.gatewayService.bindEntity( hypervisor.getGatewayId(), hyperVId, EntityType.HyperVServer );
		}
		
		return hyperVId;
	}
	
	private void addVM(EdgeHost host, JHypervVMInfo vm, int serverId, int hypervSocketCount) {
		List<EdgeHyperVHostMapInfo> output = new ArrayList<EdgeHyperVHostMapInfo>();
		hyperVDao.as_edge_hyperv_host_map_getById(host.getRhostid(), output);
		
		if (output.isEmpty()) {
			hyperVDao.as_edge_hyperv_host_map_add(
					serverId, host.getRhostid(), IEdgeHyperVDao.HYPERV_HOST_STATUS_VISIBLE,
					null,
					null,
					null,
					vm.getHypervisor(),
					vm.getVmGuestOS());
		} else {
			hyperVDao.as_edge_hyperv_host_map_updateHyperVIDByID(
					host.getRhostid(), serverId, vm.getHypervisor());
		}
		hyperVDao.as_edge_hyperv_updateLicenseInfo(host.getRhostid(), hypervSocketCount);

	}

	@Override
	protected String getSpecifyBeginMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_HyperVBegin", hypervisor.getServerName());
	}

	@Override
	protected String getSpecifyEndMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_HyperVEnd", hypervisor.getServerName());
	}

	@Override
	protected void testConnection(Hypervisor hypervisor) throws EdgeServiceFault {
		hypervProtectionType = HyperVManagerAdapter.getInstance().getHyperVProtectionType(hypervisor.getGatewayId(), hypervisor.getServerName(), hypervisor.getUsername(), hypervisor.getPassword());

		DiscoveryHyperVOption hyperV = hypervisor.toHyperV();
		hyperV.setHypervProtectionType(hypervProtectionType);
		hyperV.setCluster(hypervProtectionType == HypervProtectionType.CLUSTER);
		HyperVManagerAdapter.getInstance().validateHyperVAccount(hyperV);
	}

}
