package com.ca.arcserve.edge.app.base.webservice.node.hypervisor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
//import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

public class EsxSpecifier extends HypervisorSpecifier {
	
	private static Logger logger = Logger.getLogger(EsxSpecifier.class);
	
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private int esxSocketCount = 1;
	private boolean essentialEsx;
	
	private NodeServiceImpl nodeService;

	@Override
	protected LicenseMachineType getMachineType() {
		return LicenseMachineType.VSHPERE_VM;
	}

	@Override
	protected List<Result> doSpecify(Hypervisor hypervisor, List<EdgeHost> hosts) {
		List<Result> results = new ArrayList<Result>();
		
		ArrayList<VM_Info> vms = getEsxVMs(hypervisor);
		
		int serverId = 0;
		
		for (EdgeHost host : hosts) {
			if (host.getRhostname() == null || host.getRhostname().isEmpty()) {
				continue;
			}
			
			VM_Info found = findVM(vms, host);
			
			if (found != null) {
				removeLicense(host);
				String infoMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_EsxSucceed", hypervisor.getServerName());
				results.add(new Result(LogAddEntity.create(Severity.Information, host.getRhostid(), infoMessage)));
				
				if (serverId == 0) {
					serverId = addServer(hypervisor);
				}
				
				found.setvmEsxHost(hypervisor.getServerName()); //set user-input as ESX hostname
				addVM(host, found, serverId);
			} else {
				String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_EsxNotBelong", hypervisor.getServerName());
				results.add(new Result(
						LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage), 
						EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_EsxNotBelong, errorMessage)));
			}
		}
		
		return results;
	}
	
	private VM_Info findVM(ArrayList<VM_Info> vms, EdgeHost host) {
		for (VM_Info vm : vms) {
			String vmHostname = vm.getVMHostName();
			int index = vmHostname.indexOf(".");
			String vmHostnameWithoutDomain = index == -1 ? vmHostname : vmHostname.substring(0, index);
			String hostName = host.getRhostname();
			index = hostName.indexOf(":"); // defect 175987, get the hostname without ssh port for linux node
			hostName = (index == -1) ? hostName: hostName.substring(0, index);
			
			if (hostName.equalsIgnoreCase(vmHostname) || hostName.equalsIgnoreCase(vmHostnameWithoutDomain)) {
				return vm;
			} else if (vm.getIpAddresses() != null) {
				for (String ip : vm.getIpAddresses()) {
					if (ip.equalsIgnoreCase(hostName)) {
						return vm;
					}
				}
			}
		}
		
		
		return null;
	}
	
	private int addServer(Hypervisor hypervisor) {
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getByName(hypervisor.getGatewayId().getRecordId(), hypervisor.getServerName(), esxList);
		
		int esxId = esxList.isEmpty() ? 0 : esxList.get(0).getId();
		
		int[] output = new int[1];
		esxDao.as_edge_esx_update(esxId, 
				hypervisor.getServerName(), 
				hypervisor.getUsername(), 
				hypervisor.getPassword(), 
				hypervisor.getProtocol().ordinal(), 
				hypervisor.getPort(),
				VsphereEntityType.esxServer.getValue(),
				0,
				"",
				"",
				output);
		
		if (esxId == 0) {
			esxId = output[0];
			this.gatewayService.bindEntity( hypervisor.getGatewayId(), esxId, EntityType.VSphereEntity );
		}
		
		return esxId;
	}
	
	private void addVM(EdgeHost host, VM_Info vm, int serverId) {
		if(nodeService == null)
			nodeService = new NodeServiceImpl();
		nodeService.saveVMToDB_NoUuid(host.getGatewayId(), serverId, host.getRhostid(),vm.getVMvmInstanceUUID(),vm.getVMHostName(),vm.getVMName()
				,vm.getVMUUID(),vm.getvmEsxHost(),vm.getVMVMX(),vm.getvmGuestOS(),
				"","",0,0,0,"");
		
		esxDao.as_edge_esx_updateLicenseInfo(host.getRhostid(), essentialEsx ? 1 : 0, esxSocketCount);
	}
	
	private ArrayList<VM_Info> getEsxVMs(Hypervisor hypervisor) {
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( hypervisor.getGatewayId() );
		try {
			List<ESXNode> esxNodes = vmwareService.getEsxNodeList(hypervisor.toEsx());
			if (esxNodes.isEmpty()) {
				return new ArrayList<VM_Info>();
			}
			
			ESXNode esx = esxNodes.get(0);
			
			try {
				esxSocketCount = vmwareService.getESXCPUSockets(hypervisor.toEsx(), esx.getEsxName());
			} catch (Exception e) {
				logger.error("get ESX server socket count failed.", e);
			}
			
			essentialEsx = vmwareService.isVMwareEssentialLicense(hypervisor.toEsx(), esx.getEsxName());
			
			try {
				return vmwareService.getEsxVMs(hypervisor.toEsx(),esx, true);
			} catch (Exception e) {
				logger.error("specify hypervisor failed, get VM names failed, error message = " + e.getMessage());
				return new ArrayList<VM_Info>();
			}
		} catch (EdgeServiceFault e) {
			logger.error("get vm lsit failed, error message = " + e.getMessage());
			return new ArrayList<VM_Info>();
		} finally {
			vmwareService.close();
		}
	}

	@Override
	protected String getSpecifyBeginMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_EsxBegin", hypervisor.getServerName());
	}

	@Override
	protected String getSpecifyEndMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_EsxEnd", hypervisor.getServerName());
	}

	@Override
	protected void testConnection(Hypervisor hypervisor) throws EdgeServiceFault {

		VMwareServerType type;
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( hypervisor.getGatewayId() );
		try {
			type = vmwareService.getVMwareServerType(hypervisor.toEsx());
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_CantObtainVirtualMachinesFromESXOrVC, "cannot detect the vmware server type.");
		} finally {
			vmwareService.close();
		}
			
		if (type == VMwareServerType.virtualCenter) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_vCenterNotAllowed, "Only support to specify an ESX server.");
		}
	}
}
