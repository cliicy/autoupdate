package com.ca.arcserve.edge.app.base.webservice.vSphere;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.ApplicationStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMConnectionDAO;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSphereDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployingCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IPolicyManagementService;
import com.ca.arcserve.edge.app.base.webservice.IVSphereService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgeConnectInfoVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EdgeHostVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VMInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VmEsxMap;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class VSphereServiceImpl implements IVSphereService {
	private static final Logger logger = Logger.getLogger(VSphereServiceImpl.class);
	private IEdgeVSphereDao vSphereDao = DaoFactory.getDao(IEdgeVSphereDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IEdgeVCMDao vcmDao = DaoFactory.getDao(IEdgeVCMDao.class);
	private IEdgeVCMConnectionDAO vcmConnectionDao = DaoFactory.getDao(IEdgeVCMConnectionDAO.class);
	private NodeServiceImpl nodeService = new NodeServiceImpl();

	
	public void setPolicyManagermentService(IPolicyManagementService pmservice)
	{
		if (pmservice == null) {
			throw new NullPointerException();
		}
	}
	
	public void setPolicyDao(IEdgePolicyDao dao)
	{
		if (dao == null) {
			throw new NullPointerException();
		}
		 this.policyDao = dao;
	}
	
	public void setvSphereDao(IEdgeVSphereDao dao)
	{
		if (dao == null) {
			throw new NullPointerException();
		}
		this.vSphereDao = dao;
	}

	public void setVcmDao(IEdgeVCMDao dao)
	{
		if (dao == null) {
			throw new NullPointerException();
		}
		this.vcmDao = dao;
	}
	
	
	public VMInfo getVMNodesFromVSphere() throws EdgeServiceFault
	{
		VMInfo vmInfo = new VMInfo();
		List<EdgeHostVSphere> vmNodesList = new LinkedList<EdgeHostVSphere>();
		List<EdgeHostVSphere> vmNodesListWithProxy = new LinkedList<EdgeHostVSphere>();
		vSphereDao.as_edge_vsphere_vmlist(vmNodesList);
		vmInfo.setVmNodesList(vmNodesList);

		List<EdgeConnectInfoVSphere> vmConnectInfoList = new LinkedList<EdgeConnectInfoVSphere>();
		vSphereDao.as_edge_vsphere_vmConnectInfolist(vmConnectInfoList);
		vmInfo.setVmConnectInfoList(vmConnectInfoList);

		List<EsxVSphere> vmESXInfoList = new LinkedList<EsxVSphere>();
		String vsphereTypes = "("+VsphereEntityType.esxServer.getValue()+","+VsphereEntityType.vCenter.getValue()+")";
		vSphereDao.as_edge_vsphere_vmESXInfolist(0, vsphereTypes, vmESXInfoList);
		vmInfo.setVmESXInfoList(vmESXInfoList);

		//-----This method have no use , should be deleted after a later
		//List<VmEsxMap> vmESXMapList = new LinkedList<VmEsxMap>();
		//vSphereDao.as_edge_vsphere_vmESXMaplist(vmESXMapList);
		//vmInfo.setVmESXMapList(vmESXMapList);

/*		List<EdgePolicyVSphere> vmPolicyList = new LinkedList<EdgePolicyVSphere>();
		vSphereDao.as_edge_vsphere_vmPolicylist(vmPolicyList);
		vmInfo.setVmPolicyList(vmPolicyList);

		List<EdgeVMPolicyMap> vmPolicyMapList = new LinkedList<EdgeVMPolicyMap>();
		vSphereDao.as_edge_vsphere_vmPolicyMaplist(vmPolicyMapList);
		vmInfo.setVmPolicyMapList(vmPolicyMapList);*/

		List<VSphereProxyInfo> vmProxyList = new LinkedList<VSphereProxyInfo>();
		if(vmNodesList != null)
		{
			try{
				for(EdgeHostVSphere node : vmNodesList)
				{
					if (HostTypeUtil.isVMNonWindowsOS(node.getRhostType()))
						continue;
					
					List<EdgeHostPolicyMap> map = new  LinkedList<EdgeHostPolicyMap>();

					policyDao.getHostPolicyMap(node.getRhostid(), PolicyTypes.VMBackup, map);

					if (map.size()>0){
						EdgeHostPolicyMap firstMap = map.get(0);
						List<EdgeVSphereProxyInfo> proxyList = new ArrayList<EdgeVSphereProxyInfo>();
						vcmDao.as_edge_vsphere_proxy_getByHostId(firstMap.getHostId(), proxyList);
						
						if(proxyList.size()>0){
							EdgeVSphereProxyInfo proxy = proxyList.get(0);
							
							VSphereProxyInfo vSphereProxyInfo = new VSphereProxyInfo();
							
							vSphereProxyInfo.setVmHostID(node.getRhostid());
							vSphereProxyInfo.setVSphereProxyName(proxy.getHostname());
							vSphereProxyInfo.setVSphereProxyPassword(proxy.getPassword());
							vSphereProxyInfo.setVSphereProxyPort(proxy.getPort());
							vSphereProxyInfo.setVSphereProxyProtocol(Protocol.parse(proxy.getProtocol()));
							vSphereProxyInfo.setVSphereProxyUsername(proxy.getUsername());
							vSphereProxyInfo.setVSphereProxyUuid(proxy.getUuid());
							vmProxyList.add(vSphereProxyInfo);
							vmNodesListWithProxy.add(node);
						}
					}
				}
			}catch(Exception e)
			{}
		}
		vmInfo.setVmProxyList(vmProxyList);
		vmInfo.setVmNodesList(vmNodesListWithProxy);
		
		removeRedundant(vmInfo);
		
		return vmInfo;
	}
	
	private void removeRedundant(VMInfo vmInfo) {
		Set<Integer> vmNodeIds = new HashSet<Integer>();
		
		for (EdgeHostVSphere vmNode : vmInfo.getVmNodesList()) {
			if (!vmNodeIds.contains(vmNode.getRhostid())) {
				vmNodeIds.add(vmNode.getRhostid());
			}
		}
		
		Iterator<EdgeConnectInfoVSphere> connectInfoIterator = vmInfo.getVmConnectInfoList().iterator();
		while (connectInfoIterator.hasNext()) {
			EdgeConnectInfoVSphere connectInfo = connectInfoIterator.next();
			if (!vmNodeIds.contains(connectInfo.getHostid())) {
				connectInfoIterator.remove();
			}
		}
		
		Set<Integer> esxIds = new HashSet<Integer>();
		Iterator<VmEsxMap> vmEsxMapIterator = vmInfo.getVmESXMapList().iterator();
		while (vmEsxMapIterator.hasNext()) {
			VmEsxMap map = vmEsxMapIterator.next();
			if (!vmNodeIds.contains(map.getHostId())) {
				vmEsxMapIterator.remove();
			} else if (!esxIds.contains(map.getEsxId())) {
				esxIds.add(map.getEsxId());
			}
		}
		
		Iterator<EsxVSphere> esxIterator = vmInfo.getVmESXInfoList().iterator();
		while (esxIterator.hasNext()) {
			EsxVSphere esx = esxIterator.next();
			if (!esxIds.contains(esx.getId())) {
				esxIterator.remove();
			}
		}
	}

	@Override
	public void setVMApplicationStatus(String vmInstanceUuid,
			ApplicationStatus appStatus) throws EdgeServiceFault {
		int appStatusValue = ApplicationUtil.getValue(appStatus);
		vSphereDao.as_edge_vsphere_updateVmAppStatus(vmInstanceUuid, appStatusValue);
			
		// update node 's osDescribe as appStatus.getOSVersion()
		try {
			int[] output = new int[1];	
			esxDao.as_edge_host_getHostByInstanceUUID(0,vmInstanceUuid, output);
			if(output[0] > 0){
				List<EdgeHost> resultList = new LinkedList<EdgeHost>();
				hostMgrDao.as_edge_host_list(output[0], 1, resultList);
				if(resultList.isEmpty())
					return;
				EdgeHost edgeHost = resultList.get(0);				
				String vmGuestOs = StringUtil.isEmptyOrNull(appStatus.getOSVersion())?edgeHost.getOsdesc():appStatus.getOSVersion();				
				
				//defect 764842
				if(!StringUtil.isEmptyOrNull(appStatus.getOSVersion())){
					logger.debug("[VSphereServiceImpl] update vm's os to "+appStatus.getOSVersion());
					String os = appStatus.getOSVersion();
					if(!os.contains("Windows") && !os.contains("Microsoft")){
						if(CommonUtil.isGuestOSLinux(os)) {
							edgeHost.setRhostType(HostTypeUtil.setLinuxVMNode(edgeHost.getRhostType()));
							edgeHost.setRhostType(edgeHost.getRhostType() & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
						} else {
							edgeHost.setRhostType(HostTypeUtil.setVMNonWindowsOS(edgeHost.getRhostType()));
						}
						logger.debug("[VSphereServiceImpl] update vm hostType to "+edgeHost.getRhostType());
					}else {
						edgeHost.setRhostType(edgeHost.getRhostType() & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
					}
				}
				
				hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), new Date(),
						edgeHost.getRhostname(), edgeHost.getNodeDescription(), edgeHost.getIpaddress(),
						vmGuestOs, edgeHost.getOstype(), edgeHost.getIsVisible(),
						edgeHost.getAppStatus(), "", edgeHost.getRhostType(), edgeHost.getProtectionTypeBitmap(),
						edgeHost.getFqdnNames(), new int[1]);
			}
		}catch (Exception e) {
			logger.error("[setVMApplicationStatus] as_edge_host_getHostByInstanceUUID() failed for vm vmInstanceUuid="+vmInstanceUuid,e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Dao_Execption, "");
		}
		
	}

	private List<String> GetConnInfoByUUID(String uuid) {
		List<String> proxyList=new ArrayList<String>();
		List<VmEsxMap> vmInstanceUUIDList = new ArrayList<VmEsxMap>();
		vSphereDao.as_edge_vsphere_get_vms_from_proxy(uuid, vmInstanceUUIDList);
		for (VmEsxMap vm : vmInstanceUUIDList) {
			proxyList.add(vm.getVmInstanceUuid());
		}
		return proxyList;
	}
	
	@Override
	public List<String> getManagedVMbyProxy(String uuid) throws EdgeServiceFault {
		Set<String> vmInstanceUuids = new HashSet<String>();
		
		vmInstanceUuids.addAll(GetConnInfoByUUID(uuid));
		vmInstanceUuids.addAll(PolicyDeployingCache.getInstance().getCachedVMInstanceUuids(uuid));
		
		return new LinkedList<String>(vmInstanceUuids);
	}

	public VMInfo getVMNodesFromVSphere2(VCMConnectionInfo vcmConnection)
			throws EdgeServiceFault {
		VMInfo vmInfo = getVMNodesFromVSphere();
		logger.debug(StringUtil.convertObject2String(vcmConnection));
		
		try{
			vcmConnectionDao.deleteAll();
			vcmConnectionDao.clearVCMVMMap();
			
			
			int vcmID = vcmConnectionDao.insert(vcmConnection.getHostname(), vcmConnection.getUsername(), vcmConnection.getPassword(), 
					vcmConnection.getUuid(), vcmConnection.getProtocol().ordinal(), vcmConnection.getPort());
				
			List<EdgeHostVSphere> hosts = vmInfo.getVmNodesList();
			for (EdgeHostVSphere host:hosts)
					vcmConnectionDao.addVCMVMMap(host.getRhostid(), vcmID);
			
		}catch(Exception e){
			logger.error(e);
		}
		
		return vmInfo;
	}
	
	@Override
	public void verifyVMsByInstanceUUID(List<String> vmInstanceUUIDs) throws EdgeServiceFault {
		if(vmInstanceUUIDs != null && vmInstanceUUIDs.size()>0){
			List<Integer> tmpIds = new ArrayList<Integer>();
			for(String vmInstanceUUID : vmInstanceUUIDs){
				NodeDetail nodeDetail = nodeService.getNodeDetailInformationByVMID(vmInstanceUUID);
				if(nodeDetail != null ){
					tmpIds.add(nodeDetail.getId());
				}
			}
	    
			int[] nodeIDs = new int[tmpIds.size()];
			for(int i = 0; i<tmpIds.size(); i++){
				nodeIDs[i] = tmpIds.get(i);
			}
			nodeService.verifyVMs(nodeIDs);
		}
	}
}
