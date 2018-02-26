package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting.SettingType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;
//import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;

public class EsxDiscoveryTask implements Callable<Void> {
	
	private DiscoveryESXOption[] esxOptions;
	private IEsxDiscoveryMonitor monitor;
	private static Logger logger = Logger.getLogger(EsxDiscoveryTask.class);
	private static IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private static IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private NodeServiceImpl nodeService = new NodeServiceImpl();
	
	private static String unknown = EdgeCMWebServiceMessages.getResource("EDGEMAIL_Unknown");
	private static String EDGEMAIL_Esxname = EdgeCMWebServiceMessages.getResource("EDGEMAIL_Esxname");
	private static String EDGEMAIL_DiscoveredVMAmount = EdgeCMWebServiceMessages.getResource("EDGEMAIL_DiscoveredVMAmount");
	private static String EDGEMAIL_VMName = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VMName");
	private static String EDGEMAIL_VC_Esxname = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VC_Esxname");
	private static String EDGEMAIL_VC_EsxUser = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VC_EsxUser");
	private static String EDGEMAIL_DiscoveryBeginTime = EdgeCMWebServiceMessages.getResource("EDGEMAIL_DiscoveryBeginTime");
	private static String EDGEMAIL_DiscoveryEndTime = EdgeCMWebServiceMessages.getResource("EDGEMAIL_DiscoveryEndTime");
	private static String EDGEMAIL_AccessAddress = EdgeCMWebServiceMessages.getResource("EDGEMAIL_AccessAddress");
	private static SimpleDateFormat formatter = new SimpleDateFormat( MessageReader.getDateFormat("fullTimeDateFormat") );
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	public EsxDiscoveryTask() {
		this(null, null);
	}
	
	public EsxDiscoveryTask(DiscoveryESXOption[] esxOptions, IEsxDiscoveryMonitor monitor) {
		this.esxOptions = esxOptions;
		this.monitor = monitor;
	}
	
	public void setEsxOptions(DiscoveryESXOption[] esxOptions) {
		this.esxOptions = esxOptions;
	}
	
	public void setMonitor(IEsxDiscoveryMonitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public Void call() throws Exception {
		if (monitor == null) {
			throw new NullPointerException();
		}
		
		monitor.onTaskStart();
		
		try {
			String discoveryErrorCode = doDiscovery();
			if (discoveryErrorCode != null) {
				monitor.onTaskFail(discoveryErrorCode, null);
			} else {
				monitor.onTaskSuccessful();
				DiscoveryManager.getInstance().updateLastESXDiscoveryDate();
			}
		} catch (EdgeServiceFault e) {
			String errorCode = e.getFaultInfo().getCode();
			String errorMessage = EdgeCMWebServiceMessages.getResource("autoDiscovery_ESX_JobDiscoverEsxDBFail");
			logger.error("Failed to discover virtual machines from ESX/vCenter servers. (" + e.getFaultInfo().getMessage()+ ")");
			monitor.onTaskFail(errorCode, errorMessage);
		}
		
		return null;
	}

	private boolean isTaskCanceled() {
		return Thread.currentThread().isInterrupted();
	}
	
	protected String doDiscovery() throws EdgeServiceFault {
		String retval = null;
		
		if (esxOptions == null || esxOptions.length == 0) {
			return retval;
		}
		
		
		Date beginTime = null;
		Date endTime = null;
		
		// init job with pending status
		for (DiscoveryESXOption option : esxOptions) {
			int taskId = TaskMonitor.registerNewTask(Module.Discovery, option.getEsxServerName(), getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_PENDING));
			option.setTaskId(taskId);
		}
		Map<String, HashMap<Integer, String>> vmInstanceServerMap = new HashMap<String, HashMap<Integer, String>>(); //string is for esxHost
		for (DiscoveryESXOption esxOption : esxOptions) {
			try {
				TaskMonitor.updateTaskStatus(esxOption.getTaskId(), TaskStatus.InProcess, getTaskDetail(esxOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_ACTIVE));
				beginTime = new Date(System.currentTimeMillis());
				boolean enable = DiscoveryUtil.getEnableAutoDiscoveryEmailAlert();
				monitor.onDiscoveryStart(esxOption);			
				
				List<ESXNode> esxNodeList = null;
				List<DiscoveryVirtualMachineInfo> vmEntryList = new LinkedList<DiscoveryVirtualMachineInfo>();
				
				IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
				IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
				HashMap<String, List<DiscoveryVirtualMachineInfo>> hm = new HashMap<String, List<DiscoveryVirtualMachineInfo>>();
				try {
					if (isTaskCanceled()) {
						updateEsxServer(vmInstanceServerMap);
							deleteAllTask();
						return retval;
					}				
	
					esxNodeList = vmwareService.getEsxNodeListWithOriginal(esxOption);
					VMwareServerType vMwareServerType = vmwareService.getVMwareServerType(esxOption);
					
					for (ESXNode esxNode : esxNodeList) {
						if (isTaskCanceled()) {
							updateEsxServer(vmInstanceServerMap);
								deleteAllTask();
							return retval;
						}		
						
						List<DiscoveryVirtualMachineInfo> jNodesNewAdded = new LinkedList<DiscoveryVirtualMachineInfo>();	
						hm.put(esxNode.getEsxName(), jNodesNewAdded);
								
						List<DiscoveryVirtualMachineInfo> esxNodeVMList = vmwareService.getVmList(esxOption, esxNode, Module.Common, false);
						
						int[] isExist = new int[1];					
						for (DiscoveryVirtualMachineInfo info : esxNodeVMList) {
							if (isTaskCanceled()) {
								updateEsxServer(vmInstanceServerMap);
									deleteAllTask();
								return retval;
							}
							if (vMwareServerType == VMwareServerType.esxServer)
						        info.setVmEsxHost(esxNode.getEsxName());
							updateDB(info, esxOption.getGatewayId(), esxOption.getId(), isExist);
							
							if (isExist[0] == 1) {
								fillVmInstanceUUIDServerMap(vmInstanceServerMap, info, esxOption.getId());
							}
							
							if(isExist[0] == 0) {
								jNodesNewAdded.add(info);
							} else {
								monitor.onDiscoveryUpdate(info);
							}
						}
						
						vmEntryList.addAll(esxNodeVMList);
					}
					
					if (isTaskCanceled()) {
						updateEsxServer(vmInstanceServerMap);
							deleteAllTask();
						return retval;
					}
					
					// update esx server type (ESX/vCenter)
					int type = vmwareService.updateEsxServerType(esxOption);
					esxDao.as_edge_esx_update_type(esxOption.getId(), type);
					
					monitor.onDiscoverySuccessful(esxOption, vmEntryList);
					
					if (isTaskCanceled()) {
						updateEsxServer(vmInstanceServerMap);
							deleteAllTask();
						return retval;
					}
					
					// Set VMs to deleted if there are VMs in host table but cannot be found in ESX server.
					setDeletedVMsFlag(esxOption, vmEntryList);
					
				}catch (EdgeServiceFault e) {
					saveAutoDiscoveryResult(esxOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED.getDiscoveryStatus(), beginTime, endTime, -1);
					retval = e.getFaultInfo().getCode();
					String errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),e.getFaultInfo());
					monitor.onDiscoveryFail(esxOption, vmEntryList, retval, errorMessage);
				} finally {
					vmwareService.close();
				}
				int newDiscoveryNodeSize = 0;
				boolean bSend = false;
				Iterator<String> keys = hm.keySet().iterator();
				while(keys.hasNext())
				{
					String esxName = keys.next();
					List<DiscoveryVirtualMachineInfo> vms = hm.get(esxName);		
					if(vms.size() > 0)
					{
						newDiscoveryNodeSize = newDiscoveryNodeSize + vms.size();
						bSend = true;
					}					
				}	
			
				if (isTaskCanceled()) {
					updateEsxServer(vmInstanceServerMap);
					return retval;
				}
				endTime = new Date(System.currentTimeMillis());
				saveAutoDiscoveryResult(esxOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_FINISHED.getDiscoveryStatus(), beginTime, endTime, newDiscoveryNodeSize);
				if(esxOption.getJobType() == 1 && enable && hm.size() >0 && bSend) // schedule job			
				{
					String content = "";				
									
					EmailTemplateSetting template = DiscoveryUtil.getEmailTemplateSetting();
					if( null == template)  // not configure the email template 
						continue;
					
					String subject = template.getSubject() + ": " + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_EsxServer_NewNodesDiscovered", esxOption.getEsxServerName());;				
					
					if(template.getHtml_flag() == 1)
					{
						content = getHtmlContent(subject, esxOption, beginTime, endTime, hm);
					}
					else
					{
						content = getPlainTextContent(subject, esxOption, beginTime, endTime, hm);
					}
					
					DiscoveryUtil.sendAutoDiscoveryEmailWithHost(subject, content);
					DiscoveryUtil.sendAutoDiscoveryEmailWithHostToCPM(subject, content);
				}
			} finally {
				TaskMonitor.deleteTask(esxOption.getTaskId());
			}
		}

		updateEsxServer(vmInstanceServerMap);
		return retval;
	}
	private void deleteAllTask() {
		for (DiscoveryESXOption option : esxOptions) {
			TaskMonitor.deleteTask(option.getTaskId());
		}
	}
	private TaskDetail<DiscoveryHistory> getTaskDetail(int id, DiscoveryStatus status) {
		TaskDetail<DiscoveryHistory> detail = new TaskDetail<DiscoveryHistory>();
		DiscoveryHistory history = new DiscoveryHistory();
		history.setId(id);
		history.setDiscoveryType(SettingType.ESX);
		history.setStatus(status);
		detail.setRawData(history);
		return detail;
	}
	private void saveAutoDiscoveryResult(int relatedId, int jobType, Date startTime, Date endTime, int result) {
		adDao.as_edge_save_ad_discovery_result(relatedId, SettingType.ESX.ordinal(),jobType, startTime, endTime, result);
	}
	private void updateEsxServer(Map<String, HashMap<Integer, String>> vmInstanceServerMap) throws EdgeServiceFault {
		if (vmInstanceServerMap.isEmpty()) {
			return;
		}

		Iterator<String> instanceUuidIt = vmInstanceServerMap.keySet().iterator();
		while (instanceUuidIt.hasNext()) {
			String instanceUuid = instanceUuidIt.next();
			HashMap<Integer, String> serverMap = vmInstanceServerMap.get(instanceUuid);
			if (serverMap == null || serverMap.keySet() == null || serverMap.keySet().isEmpty()) {
				continue;
			}

			try {
				int[] dbEsxId = new int[1];
				esxDao.as_edge_esx_getESXIdByVMUUID(instanceUuid, dbEsxId);
				int esxId = dbEsxId[0];
				
				//hide this code for zendesk Ticket 68310.
				//both VCenter and ESX server added in the Node discovery
				//user add VM by VCenter, for some reason VCenter down, 
				//after auto discovery, Hypervisor change to ESX server, even though VCenter run later, it won't change correct back. 
				Integer[] serverIdArray = serverMap.keySet().toArray(new Integer[0]);
				List<Integer> serverIdList = Arrays.asList(serverIdArray); 
				if (!serverIdList.contains(esxId)) {
					logger.info("Can not get the Esx Id from auto discovery results."); //added for zendesk Ticket 68310.
//					esxId = serverIdList.get(0);
//					esxDao.as_edge_esx_updateESXIDByVMUUID(instanceUuid, esxId);
				}else {
					String esxHost = serverMap.get(esxId);
					esxDao.as_edge_vsphere_vm_detail_updateEsxHost(instanceUuid,esxHost);
				}
				
			} catch (Exception e) {
				logger.warn("Failed to update ESX server id by VM instance UUID, because " + e.getLocalizedMessage());
			}
		}

		vmInstanceServerMap.clear();
	}
	
	private void fillVmInstanceUUIDServerMap(
			Map<String, HashMap<Integer, String>> vmInstanceServerMap,
			DiscoveryVirtualMachineInfo info,
			int serverId) {
		String instanceUUID = info.getVmInstanceUuid();
		String esxHost = info.getVmEsxHost();
		
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(serverId, esxHost);
		
		HashMap<Integer, String> serverMap = vmInstanceServerMap.get(instanceUUID);
		if (serverMap == null) {
			vmInstanceServerMap.put(instanceUUID, map);
		} else {
			if (!serverMap.containsKey(serverId)) {
				serverMap.put(serverId, esxHost);
			}
		}
	}

	// Compare the exist VMs list with the new List , and mark VMs to deleted which are missing in new list .
	private void setDeletedVMsFlag(DiscoveryESXOption esxOption,
			List<DiscoveryVirtualMachineInfo> vmEntryList) {
		/*
		 * this code is discard and merge into subsequent process
		 *  
		// Check if there are VMs in host table but cannot be found in ESX server already
		Iterator<EdgeHost> it = hosts.iterator();
		while (it.hasNext()) {
			EdgeHost host = it.next();
			for(DiscoveryVirtualMachineInfo vm : vmEntryList){
				// we check if VM exists by instanceUUID
				if(host.getVmInstanceUuid()!=null && 
					host.getVmInstanceUuid().equalsIgnoreCase(vm.getVmInstanceUuid()))
				{
					it.remove();// VM exist , remove from list
					if(host.getVmStatus() == VMStatus.DELETED.getValue()){
						// if VM is marked as deleted, reset it. 
						esxDao.as_edge_esx_host_map_updateStatus(esxOption.getId(), host.getRhostid(), VMStatus.VISIBLE.getValue());
					}
					break;
				}
			}
		}
		// if host list is not empty, we mark these VMs as deleted.
		for(EdgeHost host : hosts){
			esxDao.as_edge_esx_host_map_updateStatus(esxOption.getId(), host.getRhostid(), VMStatus.DELETED.getValue());
			addActivityLog(Severity.Information,host.getRhostname() , 
					EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_VM_Not_Found" , host.getVmname() , esxOption.getEsxServerName()) );			
		} 
		*/
		
		//fanda03 fix 143245;
		/**
		 * the original code delete(just marked as delete) the vms which are not in new discovery list and already be imported to node 
		 * view( host.isvisible = 1 )
		 * we add new code to handle all vms which are not in new discovery list. and then respectively process the nodes which are imported(mark as delete) 
		 * and not imported( actually delete ). so the original code is merge into. 
		 */
		
		// Get nodes from as_table_host ( just the host which already be imported )
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_GetFilteredPagingNodeList(
				esxOption.getId(), NodeGroup.ESX, 0, 1, 0,
				"", 0, 0, 0, 0, 0, 0, 0, 0, "", 0,
				0, Integer.MAX_VALUE,
				EdgeSortOrder.ASC.value(),
				NodeSortCol.hostname.value(), 
				"", 
				new int[1], hosts);
		Comparator<EdgeHost> host_comparator = new 	Comparator<EdgeHost>(){

			@Override
			public int compare(EdgeHost o1, EdgeHost o2) {
				Integer hostId1 = o1.getRhostid();
				Integer hostId2 = o2.getRhostid();
				return hostId1.compareTo(hostId2);
			}
		};
		Collections.sort(hosts, host_comparator);
		
				
		Comparator<DiscoveryVirtualMachineInfo> vm_hostMap_comparator = new Comparator<DiscoveryVirtualMachineInfo>(){
			@Override
			public int compare(DiscoveryVirtualMachineInfo o1, DiscoveryVirtualMachineInfo o2) {
				if( o1.getVmInstanceUuid()!=null && o2.getVmInstanceUuid()!=null )
					return o1.getVmInstanceUuid().compareToIgnoreCase( o2.getVmInstanceUuid( ));
				else {
					return o1.getVmInstanceUuid()!=null  ? 1 : (o2.getVmInstanceUuid() ==null ? 0: -1 ) ;
				}
			}
		};
		Collections.sort( vmEntryList , vm_hostMap_comparator );
		
		//get all vms in esx table
		List<EdgeEsxVmInfo> vmListInDB = new LinkedList<EdgeEsxVmInfo>();
		esxDao.as_edge_vsphere_vm_detail_getVMByEsxServerId(esxOption.getId(), vmListInDB);
		// get all nodes in esx_host_map table
		//List<EdgeEsxHostMapInfo> hostMaps = new ArrayList<EdgeEsxHostMapInfo>();
		//no corresponding storage procedure in cpm!
		//esxDao.as_edge_esx_host_map_list_by_esxid( esxOption.getId(), hostMaps);
		
		//used to mock type
		DiscoveryVirtualMachineInfo vmWrapperHostMapCache = new DiscoveryVirtualMachineInfo();
		
		EdgeHost hostWrapperHostMapCache = new EdgeHost();
		
		//extract vms ( from esx_host_map table) which don't exist in new discovered vm list.
		//Iterator<EdgeEsxHostMapInfo> hostMapsIter = hostMaps.iterator();
		Iterator<EdgeEsxVmInfo> vmEsxIterator = vmListInDB.iterator();
		while(vmEsxIterator.hasNext()) {
			
			//EdgeEsxHostMapInfo hostMapInfo = hostMapsIter.next();
			EdgeEsxVmInfo vmInfo = vmEsxIterator.next();
			if(vmInfo.getVmInstanceUuid()==null){//skip specify hypervisor
				continue;
			}
			
			vmWrapperHostMapCache.setVmInstanceUuid( vmInfo.getVmInstanceUuid());

			int findedIndex =  Collections.binarySearch( vmEntryList, vmWrapperHostMapCache, vm_hostMap_comparator );
			//this host is not in discovered vm list
			if ( findedIndex < 0 ) {
		
				hostWrapperHostMapCache.setRhostid( vmInfo.getHostId());
				
				int findedHostId =-1;
				// s1: the vm is in imported node list((visible)). cannot be directly delete; process as original code( set status as deleted ). 
				if( ( findedHostId = Collections.binarySearch( hosts, hostWrapperHostMapCache, host_comparator ) )>=0  ) {
					esxDao.as_edge_vsphere_vm_detail_updateStatus(vmInfo.getHostId(), VMStatus.DELETED.getValue() );
					addActivityLog(Severity.Information, hosts.get(findedHostId).getRhostname() , 
							EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_VM_Not_Found" , hosts.get(findedHostId).getVmname() , esxOption.getEsxServerName()) );
				}
				//this host is not imported but be deleted in esx; so we directly delete it
				else {
					hostMgrDao.as_edge_host_remove( vmInfo.getHostId());
					logger.info("EsxDiscoveryTask.setDeletedVMsFlag() : delete node, nodeId:" + vmInfo.getHostId());
				}		
			}
			//this host is in discovered node list
			else {
				hostWrapperHostMapCache.setRhostid( vmInfo.getHostId() );
				DiscoveryVirtualMachineInfo discovedVMInfo = vmEntryList.get(findedIndex);
				if(vmInfo.getStatus() == VMStatus.DELETED.getValue() && 0 == discovedVMInfo.getVmConnectionState()){
					// if VM is marked as deleted, reset it. 
					esxDao.as_edge_vsphere_vm_detail_updateStatus(vmInfo.getHostId(), VMStatus.VISIBLE.getValue() );
				} else {
					int findedHostId = -1;
					findedHostId = Collections.binarySearch( hosts, hostWrapperHostMapCache, host_comparator);
					// if VM connection state is "disconnected"
					if( findedHostId >= 0  && 1 == discovedVMInfo.getVmConnectionState()) { 
						esxDao.as_edge_vsphere_vm_detail_updateStatus(vmInfo.getHostId(), VMStatus.DELETED.getValue());
						addActivityLog(Severity.Information, hosts.get(findedHostId).getRhostname() , 
								EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_VM_Not_Found" , hosts.get(findedHostId).getVmname() , esxOption.getEsxServerName()) );
					}
				}
			}	
		}
	}
	
	private void updateDB(DiscoveryVirtualMachineInfo vm, GatewayId gatewayId, int esxId, int[] isExist) throws EdgeServiceFault {
		int[] output = new int[1];
		
		try {
			esxDao.as_edge_host_getHostByInstanceUUID(gatewayId.getRecordId(),vm.getVmInstanceUuid(), output);
			if(output[0] > 0){
				List<EdgeHost> resultList = new LinkedList<EdgeHost>();
				hostMgrDao.as_edge_host_list(output[0], 1, resultList);
				if(resultList.isEmpty())
					return;
				EdgeHost edgeHost = resultList.get(0);
				String updatedHostName = edgeHost.getRhostname();
				int rHostType = edgeHost.getRhostType();
				if ( HostTypeUtil.isHyperVVirtualMachine(rHostType) || HostTypeUtil.isVMWareVirtualMachine(rHostType)) { // HBBU node
					List<String> ipList = DiscoveryUtil.getIpAdressByHostName(updatedHostName);
					if (!ipList.contains(edgeHost.getRhostname())) {
						updatedHostName = vm.getVmHostName();
					}
				}
				
				String vmIp = vm.getVmIP() == null ?  edgeHost.getIpaddress() : vm.getVmIP();
				String vmGuestOs = StringUtil.isEmptyOrNull(vm.getVmGuestOS())?edgeHost.getOsdesc():vm.getVmGuestOS();
				
				esxDao.as_edge_esx_updateLicenseInfo(output[0], vm.isVmEsxEssential() ? 1 : 0, vm.getVmEsxSocketCount());
				
				if (!vm.isWindowsOS()) {
					if(CommonUtil.isGuestOSLinux(vm.getVmGuestOS())) {
						rHostType = HostTypeUtil.setLinuxVMNode(edgeHost.getRhostType());
						rHostType = rHostType  & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue();
					} else {
						rHostType = HostTypeUtil.setVMNonWindowsOS(edgeHost.getRhostType());
					}
				} else {
					rHostType = rHostType & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue();
				}
				
				//Handle the hostname
				updatedHostName = DiscoveryUtil.getToUpdatedHostName(edgeHost.getRhostname(), updatedHostName);
				
		//		List<String> fqdnNameList = com.ca.arcserve.edge.app.base.util.CommonUtil.getFqdnNamebyHostNameOrIp(updatedHostName);
				List<String> fqdnNameList = new ArrayList<String>();
				if(gatewayId != null && gatewayId.isValid()){
					try {
						IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
						fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(updatedHostName);
					} catch (Exception e) {
						logger.error("[EsxDiscoveryTask] updateDB() get fqdn name failed.",e);
					}
				}
				String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
				
				hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), new Date(),
						updatedHostName, edgeHost.getNodeDescription(), vmIp,
						vmGuestOs, edgeHost.getOstype(), edgeHost.getIsVisible(),
						edgeHost.getAppStatus(), "", rHostType, edgeHost.getProtectionTypeBitmap(),
						fqdnNames, new int[1]);
				
				nodeService.saveVMToDB(gatewayId.getRecordId(), esxId, output[0], vm.getVmInstanceUuid(), updatedHostName, vm.getVmName(), vm.getVmUuid(), null, 
						vm.getVmXPath(), vmGuestOs, "", "", 0, 0, 0, "", false);
				isExist[0] = 1;
			}else{
				//Comment those: since after auto discovery, we cannot add nodes from server
				//insert new vm node
				int rHostType = HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue();
				if (!vm.isWindowsOS()) {
					if(CommonUtil.isGuestOSLinux(vm.getVmGuestOS())) {
						rHostType = HostTypeUtil.setLinuxVMNode(rHostType);
						rHostType = rHostType  & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue();
					} else {
						rHostType = HostTypeUtil.setVMNonWindowsOS(rHostType);
					}
				} else {
					rHostType = rHostType & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue();
				}
				
				String vmHostName = vm.getVmHostName();
				if(!StringUtil.isEmptyOrNull(vmHostName)){
					vmHostName = vmHostName.toLowerCase();
				}
//				List<String> fqdnNameList = com.ca.arcserve.edge.app.base.util.CommonUtil.getFqdnNamebyHostNameOrIp(vmHostName);
				List<String> fqdnNameList = new ArrayList<String>();
				if(gatewayId != null && gatewayId.isValid()){
					try {
						IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId);
						fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(vmHostName);
					} catch (Exception e) {
						logger.error("[EsxDiscoveryTask] updateDB() get fqdn name failed.",e);
					}
				}
				String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
				
				hostMgrDao.as_edge_host_update(-1, // rhostid
						new Date(), // lastupdated
						vmHostName, // rhostname
						"", // node description
						vm.getVmIP() == null ? "" : vm.getVmIP(), // ipaddress
						vm.getVmGuestOS() == null ? "" : vm.getVmGuestOS(), // osdesc
						"",//ostype
						0, // IsVisible
						0, // appStatus,
						"", // ServerPrincipalName,
						rHostType,
						ProtectionType.WIN_D2D.getValue(),
						fqdnNames,
						output);
				connectionInfoDao.as_edge_connect_info_update(output[0], null, null,
						null, 0, 0, 0,
						"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
				nodeService.saveVMToDB(gatewayId.getRecordId(), esxId, output[0], vm.getVmInstanceUuid(), vm.getVmHostName(), vm.getVmName(), vm.getVmUuid(), vm.getVmEsxHost(), 
						vm.getVmXPath(), vm.getVmGuestOS() == null ? "" : vm.getVmGuestOS(), "", "", 0, 0, IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE, "", false);
				esxDao.as_edge_esx_updateLicenseInfo(output[0], vm.isVmEsxEssential() ? 1 : 0, vm.getVmEsxSocketCount());
				this.gatewayService.bindEntity(gatewayId, output[0], EntityType.Node);
				isExist[0] = 0;
			}
		} catch (Exception e) {
			logger.error("[EsxDiscoveryTask] updateDB() failed for vm: "+vm.getVmName(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Dao_Execption, "");
		}
	}
	
	public static String getHtmlContent(String subject, DiscoveryESXOption option, Date beginTime, Date endTime, HashMap<String, List<DiscoveryVirtualMachineInfo>> nodesMap)
	{
		String template = "";
		String templatePre = "";
		try
		{
			logger.debug("getHtmlContent - start");
			
			//HTML format
			StringBuffer htmlTemplate = new StringBuffer();
			StringBuffer htmlTemplate2 = new StringBuffer();
			StringBuffer htmlTemplate3 = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(EmailContentTemplate.getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");  // VC/Esx name
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");  // User
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");  // Discover Begin Time
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");  // Discover End Time
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=%s>%s</a></TD></TR>");  // Access Address
						
			InetAddress inet = null;
			String hostName = option.getEsxServerName().trim();
			try {
				inet = InetAddress.getByName(hostName);
				hostName = inet.getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}
			
			Iterator<String> keys = nodesMap.keySet().iterator();			
			while(keys.hasNext())
			{
				String esxName = keys.next();
				List<DiscoveryVirtualMachineInfo> vms = nodesMap.get(esxName);
				if(vms.size() == 0)
					continue; 
				
				if(!esxName.equalsIgnoreCase(hostName))  // VC name and Esx Name are different
					htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>"+EDGEMAIL_Esxname+"</B></TD><TD>"+esxName+"</TD></TR>");  // Esx name
				htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>"+EDGEMAIL_DiscoveredVMAmount+"</B></TD><TD>"+Integer.toString(vms.size())+"</TD></TR>");  // Discovered Nodes Amount
				htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>"+EDGEMAIL_VMName+"</B></TD><TD>");   
				htmlTemplate2.append("													<TABLE border=\"0\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\" style=\"border:0\">");
				
				String nodeInfo = "";
				String vmHostName = "";
				for (int i=0; i<vms.size(); i++) {
					DiscoveryVirtualMachineInfo node = vms.get(i);
					if(node.getVmHostName() == null || node.getVmHostName().isEmpty())
						vmHostName = unknown;
					else
						vmHostName = node.getVmHostName();
					
					nodeInfo=node.getVmName()+"("+vmHostName+")";
					
					htmlTemplate2.append("													<TR><TD>"+nodeInfo+"</TD></TR>");
				}
				
				htmlTemplate2.append("													</TABLE>");
				htmlTemplate2.append("											 </TD></TR>");				
			}
			
			htmlTemplate3.append("	</TABLE>");
			htmlTemplate3.append("</BODY>");
			htmlTemplate3.append("</HTML>");			
			
			String url = EdgeEmailService.GetInstance().getApplicationUrl();
			templatePre = String.format(htmlTemplate.toString(),subject,
					EDGEMAIL_VC_Esxname, option.getEsxServerName(), 
					EDGEMAIL_VC_EsxUser, option.getEsxUserName(),
					EDGEMAIL_DiscoveryBeginTime, formatter.format(beginTime), 
					EDGEMAIL_DiscoveryEndTime, formatter.format(endTime), 
					EDGEMAIL_AccessAddress, url,url
					);	

			template = templatePre + htmlTemplate2 + htmlTemplate3;
			
		}
		catch (Exception e)
		{
			logger.error("getHtmlContent got exception:" + e.getMessage());
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	public static String getPlainTextContent(String subject, DiscoveryESXOption option, Date beginTime, Date endTime, HashMap<String, List<DiscoveryVirtualMachineInfo>> nodesMap)
	{
		logger.debug("getPlainTextContent - start");
		String template = "";		
		try {			
				StringBuffer plainTemplate = new StringBuffer();
				
				String url = EdgeEmailService.GetInstance().getApplicationUrl();
				plainTemplate.append(EDGEMAIL_VC_Esxname+": ");
				plainTemplate.append(option.getEsxServerName());
				plainTemplate.append("   |   ");
				
				plainTemplate.append(EDGEMAIL_VC_EsxUser+": ");
				plainTemplate.append(option.getEsxUserName());
				plainTemplate.append("   |   ");
				
				plainTemplate.append(EDGEMAIL_DiscoveryBeginTime+": ");
				plainTemplate.append(formatter.format(beginTime));
				plainTemplate.append("   |   ");
				
				plainTemplate.append(EDGEMAIL_DiscoveryEndTime+": ");
				plainTemplate.append(formatter.format(endTime));
				plainTemplate.append("   |   ");
				
				plainTemplate.append(EDGEMAIL_AccessAddress+": ");								
				plainTemplate.append(url);			
				plainTemplate.append("   |   ");				
				
				InetAddress inet = null;
				String hostName = option.getEsxServerName().trim();
				try {
					inet = InetAddress.getByName(hostName);
					hostName = inet.getHostName();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
				
				Iterator<String> keys = nodesMap.keySet().iterator();			
				while(keys.hasNext())
				{
					String esxName = keys.next();
					List<DiscoveryVirtualMachineInfo> vms = nodesMap.get(esxName);
					if(vms.size() == 0)
						continue; 
					
					if(!esxName.equalsIgnoreCase(hostName))  // VC name and Esx Name are different
					{
						plainTemplate.append("\r\n");
						plainTemplate.append(EDGEMAIL_Esxname+": ");								
						plainTemplate.append(esxName);
						plainTemplate.append("   |   ");
					}
					
					plainTemplate.append(EDGEMAIL_DiscoveredVMAmount+": ");
					plainTemplate.append(Integer.toString(vms.size()));
					plainTemplate.append("   |   ");
					
					plainTemplate.append(EDGEMAIL_VMName+": "+"\r\n");
					
					String nodeInfo = "";
					String vmHostName = "";
					for (int i=0; i<vms.size(); i++) {
						DiscoveryVirtualMachineInfo node = vms.get(i);
						
						if(node.getVmHostName() == null || node.getVmHostName().isEmpty())
							vmHostName = unknown;
						else
							vmHostName = node.getVmHostName();						
						nodeInfo=node.getVmName()+"("+vmHostName+")";
						
						plainTemplate.append(nodeInfo+"\n\n");
					}
					
				}
				
				template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.error("getPlainTextContent got exception:"  + e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}
	
	private void addActivityLog(Severity severity, String nodeName, String message) {
		IActivityLogService logService = new ActivityLogServiceImpl();
		ActivityLog activityLog = new ActivityLog();

		if(nodeName == null || nodeName.isEmpty()){
			nodeName = EdgeCMWebServiceMessages.getResource( "policyDeployment_UnknownNode" );
		}
		activityLog.setModule(Module.Common);
		activityLog.setSeverity(severity);
		activityLog.setMessage(message);
		activityLog.setNodeName(nodeName);

		try {
			logService.addLog(activityLog);
		} catch (EdgeServiceFault e) {
			logger.error("add activity log failed", e);
		}
	}
	
}
