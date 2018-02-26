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
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;

public class ImportVMsJob extends ImportNodesJob {
	
	private static final Logger logger = Logger.getLogger(ImportVMsJob.class);
	
	private static final String TAG_ESX = "esx";
	private static final String TAG_VMS = "vms";
	private static final String TAG_ADD_TO_AD = "addToADList";
	
	private DiscoveryESXOption esxOption;
	private Map<NodeRegistrationInfo, DiscoveryVirtualMachineInfo> nodeVmMap;
	private int esxId;
	
	public JobDetail createJobDetail(DiscoveryESXOption esxOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList, NodeServiceImpl nodeService) {
		JobDetail jobDetail = createJobDetail(null, type, nodeService);
		jobDetail.getJobDataMap().put(TAG_ESX, esxOption);
		jobDetail.getJobDataMap().put(TAG_VMS, vms);
		jobDetail.getJobDataMap().put(TAG_ADD_TO_AD, addEsxToADList);
		return jobDetail;
	}
	
	@Override
	protected void loadContextData(JobExecutionContext context) {
		super.loadContextData(context);
		
		if (context.getJobDetail().getJobDataMap().get(TAG_ESX) instanceof DiscoveryESXOption) {
			esxOption = (DiscoveryESXOption)context.getJobDetail().getJobDataMap().get(TAG_ESX);
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
	}
	
	@Override
	protected boolean validateContextData() {
		if (!super.validateContextData()) {
			return false;
		}
		
		if (esxOption == null) {
			logger.error("ESX information is null.");
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void importAll() {
		try {
			importEsx();
		} catch (Exception e) {
			String message = EdgeCMWebServiceMessages.getResource("importVMJob_ImportEsxFail");
			logger.error(message, e);
			nodeService.addActivityLogForImportNodes(Severity.Error, type, message);
			return;
		}
		
		// update ESX Server Type		
		nodeService.updateEsxServerType(esxOption, esxId);
		
		super.importAll();
	}
	
	private void importEsx() throws Exception {
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
		
		nodeService.esxDao.as_edge_esx_getByName(esxOption.getGatewayId().getRecordId(), esxOption.getEsxServerName(), esxList);
		esxId = esxList.isEmpty() ? 0 : esxList.get(0).getId();
		
		int[] output = new int[1];
		
		nodeService.esxDao.as_edge_esx_update(esxId, 
				esxOption.getEsxServerName(), 
				esxOption.getEsxUserName(), 
				esxOption.getEsxPassword(), 
				esxOption.getProtocol().ordinal(), 
				esxOption.getPort(),
				0,
				0,
				"",
				"",
				output);
		
		if (esxId == 0) {
			esxId = output[0];
		}
	}
	
	@Override
	protected int importSingle(NodeRegistrationInfo node) {
		int nodeId = node.getId();
		try {
			DiscoveryVirtualMachineInfo vmInfo = nodeVmMap.get(node);
			if(vmInfo == null)
				return 0;
			nodeId = saveVmToHostTable(node, vmInfo);
			if(nodeId ==0)
				return 0;
			nodeService.saveVMToDB(node.getGatewayId().getRecordId(), esxId, nodeId, vmInfo.getVmInstanceUuid(),
					vmInfo.getVmHostName(),vmInfo.getVmName(), vmInfo.getVmUuid(),vmInfo.getVmEsxHost()
					,vmInfo.getVmXPath(),vmInfo.getVmGuestOS(),vmInfo.getUserName(),vmInfo.getPassword(),0,0,0,"",true);
		} catch (Exception e) {
			String message = String.format(EdgeCMWebServiceMessages.getResource("importVMJobFailed"), node.getNodeName());
			logger.error(message, e);
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Error, type, message);
		}
		return nodeId;
	}
	
	private int saveVmToHostTable(NodeRegistrationInfo node, DiscoveryVirtualMachineInfo vmInfo){
		node.setPhysicsMachine(false);
		node.setVMWareVM(true);
		if (node.getId() == 0)
			node.setId(-1);		
		int[] ids = new int[1];
		nodeService.esxDao.as_edge_host_getHostByInstanceUUID(node.getGatewayId().getRecordId(), vmInfo.getVmInstanceUuid(), ids);
		if(ids[0] > 0){
			node.setId(ids[0]);
		}
		
		int nodeId = super.importSingle(node);
		if (nodeId == 0)
			return nodeId;
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
					logger.error("[ImportVMsJob] saveVmToHostTable() get fqdn name failed.",e);
				}
			}
			String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
			
			nodeService.hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), edgeHost.getLastupdated(), hostName,edgeHost.getNodeDescription(),
					edgeHost.getIpaddress(), edgeHost.getOsdesc(),edgeHost.getOstype(), edgeHost.getIsVisible(), edgeHost.getAppStatus(), 
					"",edgeHost.getRhostType(), node.getProtectionType().getValue(), fqdnNames, new int[1]);
		}
		return nodeId;
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

	@Override
	protected void tryMarkARCserveProducts(NodeRegistrationInfo node,
			EdgeHost edgeHost) {
		return;
	}
}
