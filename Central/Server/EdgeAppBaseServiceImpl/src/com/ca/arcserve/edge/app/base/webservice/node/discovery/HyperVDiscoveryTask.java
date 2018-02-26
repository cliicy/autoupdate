package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting.SettingType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervEntityType;
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
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;

public class HyperVDiscoveryTask implements Callable<Void> {

	private DiscoveryHyperVOption[] hyperVOptions;
	private IHyperVDiscoveryMonitor monitor;
	private static Logger logger = Logger.getLogger(HyperVDiscoveryTask.class);
	private static IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private static IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private static String unknown = EdgeCMWebServiceMessages.getResource("EDGEMAIL_Unknown");
	private static String EDGEMAIL_Esxname = EdgeCMWebServiceMessages.getResource("EDGEMAIL_hyperVName");
	private static String EDGEMAIL_DiscoveredVMAmount = EdgeCMWebServiceMessages
			.getResource("EDGEMAIL_DiscoveredVMAmount");
	private static String EDGEMAIL_VMName = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VMName");
	private static String EDGEMAIL_VC_Esxname = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VC_Esxname");
	private static String EDGEMAIL_VC_EsxUser = EdgeCMWebServiceMessages.getResource("EDGEMAIL_VC_EsxUser");
	private static String EDGEMAIL_DiscoveryBeginTime = EdgeCMWebServiceMessages
			.getResource("EDGEMAIL_DiscoveryBeginTime");
	private static String EDGEMAIL_DiscoveryEndTime = EdgeCMWebServiceMessages.getResource("EDGEMAIL_DiscoveryEndTime");
	private static String EDGEMAIL_AccessAddress = EdgeCMWebServiceMessages.getResource("EDGEMAIL_AccessAddress");
	private static SimpleDateFormat formatter = new SimpleDateFormat( MessageReader.getDateFormat("fullTimeDateFormat") );
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	public HyperVDiscoveryTask() {
		this(null, null);
	}

	public HyperVDiscoveryTask(DiscoveryHyperVOption[] hyperVOptions, IHyperVDiscoveryMonitor monitor) {
		this.hyperVOptions = hyperVOptions;
		this.monitor = monitor;
	}

	public void setHyperVOptions(DiscoveryHyperVOption[] hyperVOptions) {
		this.hyperVOptions = hyperVOptions;
	}

	public void setMonitor(IHyperVDiscoveryMonitor monitor) {
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
				DiscoveryManager.getInstance().updateLastHyperVDiscoveryDate();
			}
		} catch (EdgeServiceFault e) {
			String errorCode = e.getFaultInfo().getCode();
			String errorMessage = EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_JobDiscoverHyperVDBFail");
			logger.error("Failed to discover virtual machines from Hyper-V servers. ("
					+ e.getFaultInfo().getMessage() + ")");
			monitor.onTaskFail(errorCode, errorMessage);
		}

		return null;
	}

	private boolean isTaskCanceled() {
		return Thread.currentThread().isInterrupted();
	}

	protected String doDiscovery() throws EdgeServiceFault {
		String retval = null;

		if (hyperVOptions == null || hyperVOptions.length == 0) {
			return retval;
		}

		Date beginTime = null;
		Date endTime = null;
		
		// init job with pending status
		for (DiscoveryHyperVOption option : hyperVOptions) {
			int taskId = TaskMonitor.registerNewTask(Module.Discovery, option.getServerName(), getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_PENDING));
			option.setTaskId(taskId);
		}
		
		for (DiscoveryHyperVOption hyperVOption : hyperVOptions) {
			try {
				TaskMonitor.updateTaskStatus(hyperVOption.getTaskId(), TaskStatus.InProcess, getTaskDetail(hyperVOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_ACTIVE));
				beginTime = new Date(System.currentTimeMillis());
				boolean enable = DiscoveryUtil.getEnableAutoDiscoveryEmailAlert();
				monitor.onDiscoveryStart(hyperVOption);

				List<DiscoveryVirtualMachineInfo> vmEntryList = new LinkedList<DiscoveryVirtualMachineInfo>();
				HashMap<String, List<DiscoveryVirtualMachineInfo>> hm = new HashMap<String, List<DiscoveryVirtualMachineInfo>>();
				List<DiscoveryVirtualMachineInfo> jNodesNewAdded = new LinkedList<DiscoveryVirtualMachineInfo>();
				hm.put(hyperVOption.getServerName(), jNodesNewAdded);

				try {
					if (isTaskCanceled()) {
						deleteAllTask();
						return retval;
					}

					List<DiscoveryVirtualMachineInfo> hyperVNodeVMList = HyperVManagerAdapter.getInstance().getHypervVMList(
						hyperVOption.getGatewayId(), hyperVOption.getServerName(), hyperVOption.getUsername(),
						hyperVOption.getPassword(), hyperVOption.getHypervProtectionType());

					int[] isExist = new int[1];
					for (DiscoveryVirtualMachineInfo info : hyperVNodeVMList) {
						if (isTaskCanceled()) {
							deleteAllTask();
							return retval;
						}

						updateDB(hyperVOption, info, hyperVOption.getId(), isExist);
						if (isExist[0] == 0) {							
							jNodesNewAdded.add(info);
						} else {							
							monitor.onDiscoveryUpdate(info);
						}
					}

					vmEntryList.addAll(hyperVNodeVMList);

					if (isTaskCanceled()) {
						deleteAllTask();
						return retval;
					}

					monitor.onDiscoverySuccessful(hyperVOption, vmEntryList);

					if (isTaskCanceled()) {
						deleteAllTask();
						return retval;
					}

					// Set VMs to deleted if there are VMs in host table but cannot be found in Hyper-V server.
					setDeletedVMsFlag(hyperVOption, vmEntryList);
				} catch (EdgeServiceFault e) {
					saveAutoDiscoveryResult(hyperVOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED.getDiscoveryStatus(), beginTime, endTime, -1);
					retval = e.getFaultInfo().getCode();
					String errorMessage = e.getFaultInfo().getMessage();
					monitor.onDiscoveryFail(hyperVOption, vmEntryList, retval, errorMessage);
				}

				boolean bSend = false;
				Iterator<String> keys = hm.keySet().iterator();
				while (keys.hasNext()) {
					String hyperVName = keys.next();
					List<DiscoveryVirtualMachineInfo> vms = hm.get(hyperVName);
					if (vms.size() > 0) {
						bSend = true;
						break;
					}
				}

				endTime = new Date(System.currentTimeMillis());
				saveAutoDiscoveryResult(hyperVOption.getId(), DiscoveryStatus.DISCOVERY_STATUS_FINISHED.getDiscoveryStatus(), beginTime, endTime, jNodesNewAdded.size());

				if (hyperVOption.getJobType() == 1 && enable && bSend) {

					String content = "";
					EmailTemplateSetting template = DiscoveryUtil.getEmailTemplateSetting();
					if (null == template) // not configure the email template
						continue;

					String subject = template.getSubject() + ": "
							+ EdgeCMWebServiceMessages.getMessage("EDGEMAIL_EsxServer_NewNodesDiscovered", hyperVOption.getServerName());

					if (template.getHtml_flag() == 1) {
						content = getHtmlContent(subject, hyperVOption, beginTime, endTime, hm);
					} else {
						content = getPlainTextContent(subject, hyperVOption, beginTime, endTime, hm);
					}

					DiscoveryUtil.sendAutoDiscoveryEmailWithHost(subject, content);
					DiscoveryUtil.sendAutoDiscoveryEmailWithHostToCPM(subject, content);
				}
			} finally {
				TaskMonitor.deleteTask(hyperVOption.getTaskId());
			}
		}

		return retval;
	}
	
	private void deleteAllTask() {
		for (DiscoveryHyperVOption option : hyperVOptions) {
			TaskMonitor.deleteTask(option.getTaskId());
		}
	}
	
	private TaskDetail<DiscoveryHistory> getTaskDetail(int id, DiscoveryStatus status) {
		TaskDetail<DiscoveryHistory> detail = new TaskDetail<DiscoveryHistory>();
		DiscoveryHistory history = new DiscoveryHistory();
		history.setId(id);
		history.setDiscoveryType(SettingType.HYPERV);
		history.setStatus(status);
		detail.setRawData(history);
		return detail;
	}
	
	private void saveAutoDiscoveryResult(int relatedId, int jobStatus, Date startTime, Date endTime, int result) {
		adDao.as_edge_save_ad_discovery_result(relatedId, SettingType.HYPERV.ordinal(), jobStatus, startTime, endTime, result);
	}

	private void setDeletedVMsFlag(DiscoveryHyperVOption esxOption, List<DiscoveryVirtualMachineInfo> vmEntryList) {
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_GetFilteredPagingNodeList(esxOption.getId(), NodeGroup.HYPERV, 0, 1, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0,
				Integer.MAX_VALUE, EdgeSortOrder.ASC.value(), NodeSortCol.hostname.value(), "", new int[1], hosts);
		Comparator<EdgeHost> host_comparator = new Comparator<EdgeHost>() {

			@Override
			public int compare(EdgeHost o1, EdgeHost o2) {
				Integer hostId1 = o1.getRhostid();
				Integer hostId2 = o2.getRhostid();
				return hostId1.compareTo(hostId2);
			}
		};
		Collections.sort(hosts, host_comparator);

		Comparator<DiscoveryVirtualMachineInfo> vm_hostMap_comparator = new Comparator<DiscoveryVirtualMachineInfo>() {
			@Override
			public int compare(DiscoveryVirtualMachineInfo o1, DiscoveryVirtualMachineInfo o2) {
				if (o1.getVmInstanceUuid() != null && o2.getVmInstanceUuid() != null)
					return o1.getVmInstanceUuid().compareToIgnoreCase(o2.getVmInstanceUuid());
				else {
					return o1.getVmInstanceUuid() != null ? 1 : (o2.getVmInstanceUuid() == null ? 0 : -1);
				}
			}
		};
		Collections.sort(vmEntryList, vm_hostMap_comparator);

		// get all nodes in esx_host_map table
		List<EdgeHyperVHostMapInfo> hostMaps = new ArrayList<EdgeHyperVHostMapInfo>();
		// no corresponding storage procedure in cpm!
		hyperVDao.as_edge_hyperv_host_map_list_by_hypervid(esxOption.getId(), hostMaps);

		// used to mock type
		DiscoveryVirtualMachineInfo vmWrapperHostMapCache = new DiscoveryVirtualMachineInfo();
		EdgeHost hostWrapperHostMapCache = new EdgeHost();

		// extract vms ( from esx_host_map table) which don't exist in new
		// discovered vm list.
		Iterator<EdgeHyperVHostMapInfo> hostMapsIter = hostMaps.iterator();
		while (hostMapsIter.hasNext()) {

			EdgeHyperVHostMapInfo hostMapInfo = hostMapsIter.next();

			if(hostMapInfo.getVmInstanceUuid()==null){//skip specify hypervisor
				continue;
			}
			
			vmWrapperHostMapCache.setVmInstanceUuid(hostMapInfo.getVmInstanceUuid());

			// this host is not in discovered vm list
			if (Collections.binarySearch(vmEntryList, vmWrapperHostMapCache, vm_hostMap_comparator) < 0) {

				hostWrapperHostMapCache.setRhostid(hostMapInfo.getHostId());

				int findedHostId = -1;
				// s1: the vm is in imported node list((visible)). cannot be
				// directly delete; process as original code( set status as
				// deleted ).
				if ((findedHostId = Collections.binarySearch(hosts, hostWrapperHostMapCache, host_comparator)) >= 0) {
					hyperVDao.as_edge_hyperv_host_map_updateStatus(esxOption.getId(), hostMapInfo.getHostId(),
							VMStatus.DELETED.getValue());
					addActivityLog(
							Severity.Information,
							hosts.get(findedHostId).getRhostname(),
							EdgeCMWebServiceMessages.getMessage("autoDiscovery_hyperV_VM_Not_Found",
									hosts.get(findedHostId).getVmname(), esxOption.getServerName()));
				}
				// this host is not imported but be deleted in esx; so we
				// directly delete it
				else {
					hostMgrDao.as_edge_host_remove(hostMapInfo.getHostId());
					logger.info("HyperVDiscoveryTask.setDeletedVMsFlag() : delete node, nodeId:" + hostMapInfo.getHostId());
				}
			}
			// this host is in discovered node list
			else {
				if (hostMapInfo.getStatus() == VMStatus.DELETED.getValue()) {
					// if VM is marked as deleted, reset it.
					hyperVDao.as_edge_hyperv_host_map_updateStatus(esxOption.getId(), hostMapInfo.getHostId(),
							VMStatus.VISIBLE.getValue());
				}
			}
		}
	}

	private void updateDB(DiscoveryHyperVOption hyperVOption, DiscoveryVirtualMachineInfo vm, int hyperVId,
			int[] isExist) throws EdgeServiceFault {
		int[] output = new int[1];

		try {
			hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(hyperVOption.getGatewayId().getRecordId(), vm.getVmInstanceUuid(), output);
			if (output[0] > 0) {
				List<EdgeHost> resultList = new LinkedList<EdgeHost>();
				hostMgrDao.as_edge_host_list(output[0], 1, resultList);
				EdgeHost edgeHost = null;
				String originalHostName = null;
				if(!resultList.isEmpty()){
					edgeHost = resultList.get(0);
					originalHostName = edgeHost.getRhostname();
				}
				
				String newHostName = vm.getVmHostName();
				if (newHostName != null && !newHostName.isEmpty()) {
					if(edgeHost != null){
						int rHostType = edgeHost.getRhostType();
						if ( HostTypeUtil.isHyperVVirtualMachine(rHostType) || HostTypeUtil.isVMWareVirtualMachine(rHostType)) { // HBBU node
							List<String> ipList = DiscoveryUtil.getIpAdressByHostName(newHostName);
							if (ipList.contains(edgeHost.getRhostname())) {
								newHostName = null;
							}
						} else {
							newHostName = null;
						}
					}
				}
				
				//Handle hostname
				newHostName = DiscoveryUtil.getToUpdatedHostName(originalHostName, newHostName);
				
				hostMgrDao.as_edge_host_update_hostInfo(output[0], newHostName,
						vm.getVmIP() == null ? "" : vm.getVmIP());
				
				String serverName = vm.getVmEsxHost();
				if (serverName != null && !serverName.isEmpty()) {
					List<EdgeHyperVHostMapInfo> hostMapInfo = new LinkedList<EdgeHyperVHostMapInfo>();
					hyperVDao.as_edge_hyperv_host_map_getById(output[0], hostMapInfo);
					if (hostMapInfo != null && !hostMapInfo.isEmpty()) {
						String oldServerName = hostMapInfo.get(0).getHypervHost();
						List<String> ipHostList = DiscoveryUtil.getIpAdressAndHostNames(serverName);
						if (ipHostList.contains(oldServerName)) {
							serverName = null;
						}
					}
				}
				hyperVDao.as_edge_hyperv_host_map_update(output[0], vm.getVmName(), vm.getVmUuid(),
						vm.getVmInstanceUuid(), serverName, vm.getVmGuestOS());
				/*
				 * No data migration case in hyperV, so no need to update hyperV id
				 */
//				hyperVDao.as_edge_hyperv_host_map_updateHyperVIDByVMUUID(vm.getVmInstanceUuid(), hyperVId);
				// update OS type
				resultList.clear();
				hostMgrDao.as_edge_host_list(output[0], 1, resultList);
				if(!resultList.isEmpty()){
					edgeHost = resultList.get(0);
				}
				if (edgeHost!= null && !StringUtil.isEmptyOrNull(vm.getVmGuestOS())) {
					edgeHost.setOsdesc(vm.getVmGuestOS());
					if (!vm.isWindowsOS()) {
						if(CommonUtil.isGuestOSLinux(vm.getVmGuestOS())) {
							edgeHost.setRhostType(HostTypeUtil.setLinuxVMNode(edgeHost.getRhostType()));
							edgeHost.setRhostType(edgeHost.getRhostType() & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
						} else {
							edgeHost.setRhostType(HostTypeUtil.setVMNonWindowsOS(edgeHost.getRhostType()));
						}
					} else {
						edgeHost.setRhostType(edgeHost.getRhostType() & ~HostType.EDGE_NODE_VM_NONWINDOWS.getValue());
					}
					
					String hostName = edgeHost.getRhostname();
					if(!StringUtil.isEmptyOrNull(hostName))
						hostName = hostName.toLowerCase();
					
//					List<String> fqdnNameList = com.ca.arcserve.edge.app.base.util.CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
					List<String> fqdnNameList = new ArrayList<String>();
					if(hyperVOption.getGatewayId() != null && hyperVOption.getGatewayId().isValid()){
						try {
							IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hyperVOption.getGatewayId());
							fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
						} catch (Exception e) {
							logger.error("[HypervDiscoveryTask] updateDB() get fqdn name failed.",e);
						}
					}
					String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
					
					hostMgrDao.as_edge_host_update(edgeHost.getRhostid(), edgeHost.getLastupdated(),
							hostName, edgeHost.getNodeDescription(), edgeHost.getIpaddress(),
							edgeHost.getOsdesc(), edgeHost.getOstype(), edgeHost.getIsVisible(),
							edgeHost.getAppStatus(), "", edgeHost.getRhostType(), edgeHost.getProtectionTypeBitmap(),
							fqdnNames, new int[1]);
				}
				
				isExist[0] = 1;
			} else {
				//Comment those: since after auto discovery, we cannot add nodes from server
				int hostType = HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue();
				if(vm.getVmType() == HypervEntityType.HypervStandAloneVMINCluster.getValue()){ // cluster vm
					hostType = HostTypeUtil.setHyperVClusterVirtualsMachine(hostType);
				}
				String vmHostName = vm.getVmHostName();
				if(!StringUtil.isEmptyOrNull(vmHostName)){
					vmHostName = vmHostName.toLowerCase();
				}
//				List<String> fqdnNameList = com.ca.arcserve.edge.app.base.util.CommonUtil.getFqdnNamebyHostNameOrIp(vmHostName);
				List<String> fqdnNameList = new ArrayList<String>();
				if(hyperVOption.getGatewayId() != null && hyperVOption.getGatewayId().isValid()){
					try {
						IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hyperVOption.getGatewayId());
						fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(vmHostName);
					} catch (Exception e) {
						logger.error("[HyperVDiscoveryTask] updateDB() get fqdn name failed.",e);
					}
				}
				String fqdnNames = com.ca.arcserve.edge.app.base.util.CommonUtil.listToCommaString(fqdnNameList);
				
				// insert new vm node
				hostMgrDao.as_edge_host_update(-1, // rhostid
						new Date(), // lastupdated
						vmHostName, // rhostname
						"", // node description
						vm.getVmIP() == null ? "" : vm.getVmIP(), // ipaddress
						vm.getVmGuestOS() == null ? "" : vm.getVmGuestOS(), // osdesc
						"",// ostype
						0, // IsVisible
						0, // appStatus,
						"", // ServerPrincipalName,
						hostType, 
						ProtectionType.WIN_D2D.getValue(), fqdnNames, output);

				connectionInfoDao.as_edge_connect_info_update(output[0], null, null,
						null, 0, 0, 0,
						"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
				
				// insert esx_host_map table
				hyperVDao.as_edge_hyperv_host_map_add(hyperVId, output[0], IEdgeHyperVDao.HYPERV_HOST_STATUS_VISIBLE,
						vm.getVmName(), vm.getVmUuid(), vm.getVmInstanceUuid(), hyperVOption.getServerName(),
						vm.getVmGuestOS());
				logger.info("[HyperVDiscoveryTask]:updateDB() insert one item to as_edge_hyperv_host_map, "
						+ "the nodeId is "+output[0] +"the vminstanceuuid is "+vm.getVmInstanceUuid()+" the hypervid is "+hyperVId);
				this.gatewayService.bindEntity(hyperVOption.getGatewayId(), output[0],EntityType.Node);
				isExist[0] = 0;
			}
			
			hyperVDao.as_edge_hyperv_updateLicenseInfo(output[0], vm.getVmEsxSocketCount());
		} catch (Exception e) {
			logger.error("[HyperVDiscoveryTask] updateDB() failed for vm: "+vm.getVmName(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Dao_Execption, "");
		}
	}

	public static String getHtmlContent(String subject, DiscoveryHyperVOption option, Date beginTime, Date endTime,
			HashMap<String, List<DiscoveryVirtualMachineInfo>> nodesMap) {
		String template = "";
		String templatePre = "";
		try {
			logger.debug("getHtmlContent - start");

			// HTML format
			StringBuffer htmlTemplate = new StringBuffer();
			StringBuffer htmlTemplate2 = new StringBuffer();
			StringBuffer htmlTemplate3 = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(EmailContentTemplate.getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>"); // VC/Esx
																								// name
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>"); // User
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>"); // Discover
																								// Begin
																								// Time
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>"); // Discover
																								// End
																								// Time
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=%s>%s</a></TD></TR>"); // Access
			// Address

			InetAddress inet = null;
			String hostName = option.getServerName().trim();
			try {
				inet = InetAddress.getByName(hostName);
				hostName = inet.getHostName();
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
			}

			Iterator<String> keys = nodesMap.keySet().iterator();
			while (keys.hasNext()) {
				String esxName = keys.next();
				List<DiscoveryVirtualMachineInfo> vms = nodesMap.get(esxName);
				if (vms.size() == 0)
					continue;

				if (!esxName.equalsIgnoreCase(hostName)) // VC name and Esx Name
															// are different
					htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>" + EDGEMAIL_Esxname + "</B></TD><TD>" + esxName
							+ "</TD></TR>"); // Esx name
				htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>" + EDGEMAIL_DiscoveredVMAmount + "</B></TD><TD>"
						+ Integer.toString(vms.size()) + "</TD></TR>"); // Discovered
																		// Nodes
																		// Amount
				htmlTemplate2.append("		<TR><TD BGCOLOR=#DDDDDD><B>" + EDGEMAIL_VMName + "</B></TD><TD>");
				htmlTemplate2
						.append("													<TABLE border=\"0\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\" style=\"border:0\">");

				String nodeInfo = "";
				String vmHostName = "";
				for (int i = 0; i < vms.size(); i++) {
					DiscoveryVirtualMachineInfo node = vms.get(i);
					if (node.getVmHostName() == null || node.getVmHostName().isEmpty())
						vmHostName = unknown;
					else
						vmHostName = node.getVmHostName();

					nodeInfo = node.getVmName() + "(" + vmHostName + ")";

					htmlTemplate2.append("													<TR><TD>" + nodeInfo + "</TD></TR>");
				}

				htmlTemplate2.append("													</TABLE>");
				htmlTemplate2.append("											 </TD></TR>");
			}

			htmlTemplate3.append("	</TABLE>");
			htmlTemplate3.append("</BODY>");
			htmlTemplate3.append("</HTML>");

			String url = EdgeEmailService.GetInstance().getApplicationUrl();
			templatePre = String.format(htmlTemplate.toString(), subject, EDGEMAIL_VC_Esxname, option.getServerName(),
					EDGEMAIL_VC_EsxUser, option.getUsername(), EDGEMAIL_DiscoveryBeginTime, formatter.format(beginTime),
					EDGEMAIL_DiscoveryEndTime, formatter.format(endTime), EDGEMAIL_AccessAddress, url, url);

			template = templatePre + htmlTemplate2 + htmlTemplate3;

		} catch (Exception e) {
			logger.error("getHtmlContent got exception:" + e.getMessage());
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}

	public static String getPlainTextContent(String subject, DiscoveryHyperVOption option, Date beginTime,
			Date endTime, HashMap<String, List<DiscoveryVirtualMachineInfo>> nodesMap) {
		logger.debug("getPlainTextContent - start");
		String template = "";
		try {
			StringBuffer plainTemplate = new StringBuffer();

			String url = EdgeEmailService.GetInstance().getApplicationUrl();
			plainTemplate.append(EDGEMAIL_VC_Esxname + ": ");
			plainTemplate.append(option.getServerName());
			plainTemplate.append("   |   ");

			plainTemplate.append(EDGEMAIL_VC_EsxUser + ": ");
			plainTemplate.append(option.getServerName());
			plainTemplate.append("   |   ");

			plainTemplate.append(EDGEMAIL_DiscoveryBeginTime + ": ");
			plainTemplate.append(formatter.format(beginTime));
			plainTemplate.append("   |   ");

			plainTemplate.append(EDGEMAIL_DiscoveryEndTime + ": ");
			plainTemplate.append(formatter.format(endTime));
			plainTemplate.append("   |   ");

			plainTemplate.append(EDGEMAIL_AccessAddress + ": ");
			plainTemplate.append(url);
			plainTemplate.append("   |   ");

			InetAddress inet = null;
			String hostName = option.getServerName().trim();
			try {
				inet = InetAddress.getByName(hostName);
				hostName = inet.getHostName();
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
			}

			Iterator<String> keys = nodesMap.keySet().iterator();
			while (keys.hasNext()) {
				String esxName = keys.next();
				List<DiscoveryVirtualMachineInfo> vms = nodesMap.get(esxName);
				if (vms.size() == 0)
					continue;

				if (!esxName.equalsIgnoreCase(hostName)) // VC name and Esx Name
															// are different
				{
					plainTemplate.append("\r\n");
					plainTemplate.append(EDGEMAIL_Esxname + ": ");
					plainTemplate.append(esxName);
					plainTemplate.append("   |   ");
				}

				plainTemplate.append(EDGEMAIL_DiscoveredVMAmount + ": ");
				plainTemplate.append(Integer.toString(vms.size()));
				plainTemplate.append("   |   ");

				plainTemplate.append(EDGEMAIL_VMName + ": " + "\r\n");

				String nodeInfo = "";
				String vmHostName = "";
				for (int i = 0; i < vms.size(); i++) {
					DiscoveryVirtualMachineInfo node = vms.get(i);

					if (node.getVmHostName() == null || node.getVmHostName().isEmpty())
						vmHostName = unknown;
					else
						vmHostName = node.getVmHostName();
					nodeInfo = node.getVmName() + "(" + vmHostName + ")";

					plainTemplate.append(nodeInfo + "\n\n");
				}

			}

			template = plainTemplate.toString();
		} catch (Exception e) {
			logger.error("getPlainTextContent got exception:" + e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}

	private void addActivityLog(Severity severity, String nodeName, String message) {
		IActivityLogService logService = new ActivityLogServiceImpl();
		ActivityLog activityLog = new ActivityLog();

		if (nodeName == null || nodeName.isEmpty()) {
			nodeName = EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownNode");
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
