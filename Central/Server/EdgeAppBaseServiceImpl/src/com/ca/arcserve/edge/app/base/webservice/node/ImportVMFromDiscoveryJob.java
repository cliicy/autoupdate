package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

public class ImportVMFromDiscoveryJob extends ImportNodesJob {

	protected IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private static final Logger logger = Logger.getLogger(ImportVMFromDiscoveryJob.class);
	
	@Override
	protected int importSingle(NodeRegistrationInfo node) {

		int nodeId = super.importSingle(node);
		
		EdgeEsxVmInfo vmDetail = new EdgeEsxVmInfo();
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(nodeId, vmList);
		if(!vmList.isEmpty()){
			vmDetail = vmList.get(0);
		}
		
		EdgeEsx edgeEsx = new EdgeEsx();
		List<EdgeEsx> hyperVisors = new LinkedList<>();
		esxDao.as_edge_esx_getHypervisorByHostId(nodeId,hyperVisors);
		if(!hyperVisors.isEmpty()){
			edgeEsx = hyperVisors.get(0);
		}
		
		DiscoveryESXOption esxOption = new DiscoveryESXOption();
		esxOption.setEsxPassword(edgeEsx.getPassword());
		esxOption.setEsxUserName(edgeEsx.getUsername());
		esxOption.setEsxServerName(edgeEsx.getHostname());
		esxOption.setId(edgeEsx.getId());
		esxOption.setPort(edgeEsx.getPort());
		esxOption.setProtocol(Protocol.values()[edgeEsx.getProtocol()]);
		esxOption.setGatewayId( node.getGatewayId() );
		
		DiscoveryVirtualMachineInfo vmInfo = new DiscoveryVirtualMachineInfo();
		vmInfo.setVmInstanceUuid(vmDetail.getVmInstanceUuid());
			
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		vmInfo = vmwareService.getVMDetail(esxOption, vmInfo);
		vmwareService.close();
			
		vmInfo.setWindowsOS(vmInfo.getVmGuestOS()!=null && vmInfo.getVmGuestOS().contains("Microsoft"));
		
		List<EdgeHost> resultList = new LinkedList<EdgeHost>();
		nodeService.hostMgrDao.as_edge_host_list(nodeId, 1, resultList);
			
		EdgeHost edgeHost = resultList.get(0);
		if (StringUtil.isEmptyOrNull(edgeHost.getOsdesc())){
			edgeHost.setOsdesc(vmInfo.getVmGuestOS());
			if (!vmInfo.isWindowsOS()) {
				if(CommonUtil.isGuestOSLinux(vmInfo.getVmGuestOS())) {
					edgeHost.setRhostType(HostTypeUtil.setLinuxVMNode(edgeHost.getRhostType()));
					edgeHost.setRhostType(edgeHost.getRhostType() & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
				} else {
					edgeHost.setRhostType(HostTypeUtil.setVMNonWindowsOS(edgeHost.getRhostType()));
				}
			}
			
			String hostName = edgeHost.getRhostname();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
//			List<String> fqdnNameList = com.ca.arcserve.edge.app.base.util.CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[ImportVMFromDiscoveryJob] importSingle() get fqdn name failed.",e);
				}
			}
			String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
			
			nodeService.hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), edgeHost.getLastupdated(),hostName,edgeHost.getNodeDescription(),
						edgeHost.getIpaddress(), edgeHost.getOsdesc(),edgeHost.getOstype(), edgeHost.getIsVisible(), edgeHost.getAppStatus(), 
						"",edgeHost.getRhostType(), node.getProtectionType().getValue(), fqdnNames, new int[1]);
		}
			
		return nodeId;
	}

}
