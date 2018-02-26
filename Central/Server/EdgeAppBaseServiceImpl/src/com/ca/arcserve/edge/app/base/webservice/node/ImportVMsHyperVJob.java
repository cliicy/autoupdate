package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;

public class ImportVMsHyperVJob extends ImportNodesJob {
	
	private static final Logger logger = Logger.getLogger(ImportVMsHyperVJob.class);
	
	private static final String TAG_HYPERV = "hyperV";
	private static final String TAG_VMS = "vms";
	private static final String TAG_ADD_TO_AD = "addToADList";
	
	private DiscoveryHyperVOption hyperVOption;
	private Map<NodeRegistrationInfo, DiscoveryVirtualMachineInfo> nodeVmMap;
	private boolean addEsxToADList;
	private int hyperVId;
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public JobDetail createJobDetail(DiscoveryHyperVOption hyperVOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList, NodeServiceImpl nodeService) {
		JobDetail jobDetail = createJobDetail(null, type, nodeService);
		jobDetail.getJobDataMap().put(TAG_HYPERV, hyperVOption);
		jobDetail.getJobDataMap().put(TAG_VMS, vms);
		jobDetail.getJobDataMap().put(TAG_ADD_TO_AD, addEsxToADList);
		return jobDetail;
	}
	
	@Override
	protected void loadContextData(JobExecutionContext context) {
		super.loadContextData(context);
		
		if (context.getJobDetail().getJobDataMap().get(TAG_HYPERV) instanceof DiscoveryHyperVOption) {
			hyperVOption = (DiscoveryHyperVOption)context.getJobDetail().getJobDataMap().get(TAG_HYPERV);
		}
		
		VMRegistrationInfo[] vms = null;
		if (context.getJobDetail().getJobDataMap().get(TAG_VMS) instanceof VMRegistrationInfo[]) {
			vms = (VMRegistrationInfo[])context.getJobDetail().getJobDataMap().get(TAG_VMS);
		}
		
		if (vms == null || vms.length == 0) {
			return;
		}
		
		nodeVmMap = new HashMap<NodeRegistrationInfo, DiscoveryVirtualMachineInfo>();
		nodes = new NodeRegistrationInfo[vms.length];
		
		for (int i = 0; i < vms.length; ++i) {
			if (vms[i] != null && vms[i].getNodeInfo() != null && vms[i].getVmInfo() != null) {
				nodes[i] = vms[i].getNodeInfo();
				nodeVmMap.put(vms[i].getNodeInfo(), vms[i].getVmInfo());
			}
		}
		
		addEsxToADList = (Boolean) context.getJobDetail().getJobDataMap().get(TAG_ADD_TO_AD);
	}
	
	@Override
	protected boolean validateContextData() {
		if (!super.validateContextData()) {
			return false;
		}
		
		if (hyperVOption == null) {
			logger.error("HyperV information is null.");
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void importAll() {
		try {
			imporHyperV();
		} catch (Exception e) {
			String message = EdgeCMWebServiceMessages.getResource("importVMJob_ImportHyperVFail");
			logger.error(message, e);
			nodeService.addActivityLogForImportNodes(Severity.Error, type, message);
			return;
		}
		
		super.importAll();
	}
	
	private void imporHyperV() throws Exception {
		List<EdgeHyperV> esxList = new ArrayList<EdgeHyperV>();
		
		//add hyperv
		esxList.clear();
		hyperVDao.as_edge_hyperv_getByName(hyperVOption.getGatewayId().getRecordId(), hyperVOption.getServerName(), esxList);
		hyperVId = esxList.isEmpty() ? 0 : esxList.get(0).getId();
		int hypervType = hyperVOption.getHypervProtectionType().getValue() == HypervProtectionType.CLUSTER.getValue() ? HypervProtectionType.CLUSTER.getValue() : HypervProtectionType.STANDALONE.getValue();
		int[] output = new int[1];
		
		hyperVDao.as_edge_hyperv_update(hyperVId, 
				hyperVOption.getServerName(), 
				hyperVOption.getUsername(), 
				hyperVOption.getPassword(), 
				0, 
				0,
				1,
				hypervType,
				output);
		
		if (hyperVId == 0) {
			hyperVId = output[0];
			
			this.gatewayService.bindEntity( hyperVOption.getGatewayId(), hyperVId, EntityType.HyperVServer );

			IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean( IRemoteNativeFacadeFactory.class );
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hyperVOption.getGatewayId() );
			int esxSocketCount = nativeFacade.getHyperVCPUSocketCount(hyperVOption.getServerName(), hyperVOption.getUsername(), hyperVOption.getPassword());
			hyperVDao.as_edge_hyperv_updateLicenseInfo(hyperVId, esxSocketCount);
		}
	}
	
	@Override
	protected int importSingle(NodeRegistrationInfo node) {
	
		node.setPhysicsMachine(false);
		node.setVMWareVM(false);
		node.setId(-1);
		
		DiscoveryVirtualMachineInfo vmInfo = nodeVmMap.get(node);
		node.setHyperVVM(true);
		if(vmInfo != null && vmInfo.getVmType() == HypervEntityType.HypervStandAloneVMINCluster.getValue())
			node.setHyperVClusterVM(true);
		
		//if the vmInstance exists already ,get hostid and just update this node.
		if(vmInfo!=null){
			int[] ids = new int[1];
			hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(node.getGatewayId().getRecordId(),vmInfo.getVmInstanceUuid(), ids);
			if(ids[0] > 0){
				node.setId(ids[0]);
			}
		}
		
//		if(vmInfo!=null && StringUtil.isEmptyOrNull(node.getNodeName())) {
//			node.setNodeName(EdgeCMWebServiceMessages.getMessage("unknown_vm", vmInfo.getVmName()));	
//		}

		int nodeId = super.importSingle(node);
		if (nodeId == 0) {
			return nodeId;
		}
		
		try {
			addHypervHostMap(nodeId, node);
		} catch (Exception e) {
			String message = String.format(EdgeCMWebServiceMessages.getResource("importVMJobFailed"), node.getNodeName());
			logger.error(message, e);
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
		}
		
		
		try{
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
					logger.error("[ImportVMsHypervJob] importSingle() get fqdn name failed.",e);
				}
			}
			String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
			
			nodeService.hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), edgeHost.getLastupdated(), hostName,edgeHost.getNodeDescription(),
					edgeHost.getIpaddress(), edgeHost.getOsdesc(),edgeHost.getOstype(), edgeHost.getIsVisible(), edgeHost.getAppStatus(),
					"",edgeHost.getRhostType(), node.getProtectionType().getValue(), fqdnNames, new int[1]);
		}catch(Exception e){
			
		}
		return nodeId;
	}

	private void addHypervHostMap(int nodeId, NodeRegistrationInfo node) throws Exception {
		
		DiscoveryVirtualMachineInfo vmInfo = null;
		if (node != null && nodeVmMap.containsKey(node)) {
			vmInfo = nodeVmMap.get(node);
		}else{
			return;
		}
		String hypervisorName=hyperVOption.getServerName();
		int[] output = new int[1];
		hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(node.getGatewayId().getRecordId(), vmInfo.getVmInstanceUuid(), output);
		
		if (output[0] == 0) {
			hyperVDao.as_edge_hyperv_host_map_add(
					hyperVId, nodeId, IEdgeHyperVDao.HYPERV_HOST_STATUS_VISIBLE,
					vmInfo.getVmName(),
					vmInfo.getVmUuid(),
					vmInfo.getVmInstanceUuid(),
					hypervisorName,
					vmInfo.getVmGuestOS());
			logger.info("[ImportVMsHypervJob]:addHypervHostMap() insert one item to as_edge_hyperv_host_map, "
					+ "the nodeId is "+nodeId +"the vminstanceuuid is "+vmInfo.getVmInstanceUuid()+" the hypervid is "+hyperVId);
		}else{
			hyperVDao.as_edge_hyperv_host_map_update(nodeId, 
					vmInfo.getVmName(), 
					vmInfo.getVmUuid(), 
					vmInfo.getVmInstanceUuid(), 
					hypervisorName,  
					vmInfo.getVmGuestOS());
			hyperVDao.as_edge_hyperv_host_map_updateHyperVIDByVMUUID(
					vmInfo.getVmInstanceUuid(), 
					hyperVId);
			logger.info("[ImportVMsHypervJob]:addHypervHostMap() update one item to as_edge_hyperv_host_map, "
					+ "the nodeId is "+nodeId +"the vminstanceuuid is "+vmInfo.getVmInstanceUuid()+" the hypervid is "+hyperVId);
		}
	}

	@Override
	protected void tryMarkARCserveProducts(NodeRegistrationInfo node, EdgeHost edgeHost) {
		return;
	}
	
	@Override
	protected RemoteNodeInfo getRemoteNodeInfo(NodeRegistrationInfo node) {
		if (node.getNodeName()==null || node.getNodeName().isEmpty())
			return null;
		node.setD2dProtocol(Protocol.Http);
		node.setD2dPort(8014);
		return super.getRemoteNodeInfo(node);
	}
	
	
	@Override
	protected boolean updateRemoteNodeInfo(NodeRegistrationInfo node) {
		boolean queryRemoteRegRet = true;
		RemoteNodeInfo nodeInfo = getRemoteNodeInfo(node);
		if (nodeInfo == null) {
			queryRemoteRegRet = false;
//			String nodeName = node.getNodeName();
//			if(StringUtil.isEmptyOrNull(nodeName) && node.getVmRegistrationInfo() != null && node.getVmRegistrationInfo().getVmInfo() != null) {
//				nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmRegistrationInfo().getVmInfo().getVmName());
//			}
//			nodeService.addActivityLogForImportNodes(nodeName, Severity.Warning, type, EdgeCMWebServiceMessages.getMessage("ImportNode_FailedQueryRemoteRegistry"));
			nodeInfo = new RemoteNodeInfo();
			if (node.getD2dPort() != 0) {
				nodeInfo.setD2DPortNumber(node.getD2dPort());				
			}
			if (node.getD2dProtocol() != null) {
				nodeInfo.setD2DProtocol(node.getD2dProtocol()); 
			}
			nodeInfo.setD2DInstalled(false);
			node.setRegisterD2D(false);
		}
		node.setNodeInfo(nodeInfo);
		return queryRemoteRegRet;
	}
}
