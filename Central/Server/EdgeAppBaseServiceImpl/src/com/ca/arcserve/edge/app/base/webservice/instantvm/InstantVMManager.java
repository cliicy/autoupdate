package com.ca.arcserve.edge.app.base.webservice.instantvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMStatus;
import com.ca.arcflash.webservice.IInstantVMService;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.IVMJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.InstantVMConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVmStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.JobStatus;
import com.ca.arcserve.linuximaging.webservice.data.restore.VmStatus;
import com.ca.arcserve.linuximaging.webservice.edge.SynchronizeContext;

public class InstantVMManager {
	
	private static InstantVMManager instance = new InstantVMManager();
	
	private static Logger logger = Logger.getLogger(InstantVMManager.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private Map<String,InstantVM> cachedMap= Collections.synchronizedMap(new HashMap<String,InstantVM>());
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	
	public static InstantVMManager getInstance() { 
		return instance;
	}
	
	private InstantVMManager() {
	}
	
	public void loadInstantVMs() {
		try {
			List<InstantVM> vms = InstantVMServiceUtil.getInstantVMListFromDB();
			synchronized(cachedMap){
				cachedMap.clear();
				for(InstantVM vm : vms){
					cachedMap.put(vm.getUuid(), vm);
				}
			}
		} catch (Exception e){
			logger.error("Failed to load InstanceVMs in contextlistener.", e);
		}
	}
	
	public void destory() {
		List<InstantVM> vms = new ArrayList<InstantVM>(cachedMap.values());
		for(InstantVM vm : vms){
			InstantVMServiceUtil.save(vm);
		}
	}

	public void save(InstantVM vm){
		if(vm==null){
			return;
		}
		if(vm.getNodeId()==0){
			int[] hostIds = new int[1];
			hostMgrDao.as_edge_host_getHostIdByUuid(vm.getNodeUuid(), ProtectionType.WIN_D2D.getValue(),
					hostIds);
			if(hostIds[0]==0){//check whether it is vm
				hostMgrDao.as_edge_host_vm_by_instanceUUID(vm.getNodeUuid(), hostIds);
			}
			vm.setNodeId(hostIds[0]);
		}
		logger.info("save instant vm: "+InstantVMServiceUtil.parseObjectToXmlString(vm));
		synchronized(cachedMap){
			cachedMap.put(vm.getUuid(), vm);
		}
		if(logger.isDebugEnabled()){
			InstantVMServiceUtil.save(vm);
		}
	}
	
	public void remove(InstantVM vm){
		if(vm==null){
			return;
		}
		synchronized(cachedMap){
			cachedMap.remove(vm.getUuid());
		}
		InstantVMServiceUtil.remove(vm);
	}
	
	public InstantVMPagingResult getPagingInstantVMs(InstantVMPagingConfig config, InstantVMFilter filter) {
		List<InstantVM> vms = getInstantVMList(filter);
		sortInstantVMResult(vms, config);
		
		int totalCount = vms.size();
		int pageCount = config.getCount();
		int startIndex = config.getStartIndex();

		if (startIndex + pageCount > totalCount) {
			pageCount = totalCount - startIndex;
		}
		List<InstantVM> list = new ArrayList<InstantVM>(pageCount);
		for (int i = startIndex; i < startIndex+pageCount; i++) {
			list.add(vms.get(i));
			//TODO get immediate status from proxy service
//			updateInstantVM(vms.get(i));
		}
		
		Map<Integer, List<InstantVM>> map = getVmListByGroup(list);
		
		Collection<List<InstantVM>> c = map.values();
		Iterator<List<InstantVM>> it = c.iterator();
		for (; it.hasNext();) {
			List<InstantVM> oneGroup = it.next();
			updateInstantVMByGroup(oneGroup);
		}
	        
		InstantVMPagingResult result = new InstantVMPagingResult();
		result.setStartIndex(startIndex);
		result.setTotalCount(totalCount);
		result.setData(list);
		return result;
	}
	
	private List<InstantVM> getInstantVMList(InstantVMFilter filter){
		List<InstantVM> result = new ArrayList<InstantVM>();
		List<InstantVM> ivms = getAllInstantVMs();
		if(ivms == null || ivms.size()==0){
			return result;
		}
		//only support to filter gateway id in current build
		if(filter == null || filter.getGatewayId() <= 0){
			return ivms;
		}
		for(InstantVM ivm : ivms){
			if(ivm.getGatewayId()==filter.getGatewayId()){
				result.add(ivm);
			}
		}
		return result;
	}
	
	/** 
	 * new update vm method.
	 * 
	 * */
	private Map<Integer, List<InstantVM>> getVmListByGroup(List<InstantVM> vms){
		
		Map<Integer, List<InstantVM>> map = new HashMap<Integer, List<InstantVM>>();
		for (Iterator<InstantVM> it = vms.iterator(); it.hasNext();){
			InstantVM vm = it.next();
			if(map.containsKey(vm.getRecoveryServerId())){
				List<InstantVM> contains = map.get(vm.getRecoveryServerId());
				contains.add(vm);
			}else{
				List<InstantVM> notContains = new ArrayList<InstantVM>();
				notContains.add(vm);
				map.put(vm.getRecoveryServerId(), notContains);
			}
		}
		
		return map;
	}
	
	/** 
	 * new update vm method.
	 * 
	 * */
	public void updateInstantVMByGroup(final List<InstantVM> list){
		startMonitorJob(new Runnable() {

			@Override
			public void run() {
				if(CollectionUtils.isEmpty(list)){
					return;
				}
				
				
				if(list.get(0).getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_WINDOWS){
					InstantVMStatus status = null;
					try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(list.get(0).getRecoveryServerId())){
						connection.connect();
						IInstantVMService service = connection.getService();
						
						for(int i = 0; i < list.size(); i++){
							status = service.GetIVMStatus(list.get(i).getUuid());
							logger.debug(list.get(i).getRecoveryServer()+" return IVM Status: " + InstantVMServiceUtil.parseObjectToXmlString(status));
							appendInstantVMStatus(list.get(i), status);
						}
						
					} catch (Exception e) {
						logger.error("failed to get instant vm status from " + list.get(0).getRecoveryServer(), e);
					}
				}else{
					try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(list.get(0).getRecoveryServerId())){
						connection.connect();
						ILinuximagingService service = connection.getService();
						
//						for(int i = 0; i < list.size(); i++){
//							VmStatus vmStatus = service.getVMStatus(list.get(i).getUuid()); 
//							if(vmStatus!=null)
//								logger.debug(list.get(i).getName()+"[VM name] "+list.get(i).getRecoveryServer()+"[recoveryServer]"+" return IVM Status: " + vmStatus.getVmStatus());
//							getLinuxIVMStatus(list.get(i), vmStatus);
//						}
						
						List<String> jobUUIDs = new ArrayList<String>();
						for(int i = 0; i < list.size(); i++){
							jobUUIDs.add(list.get(i).getUuid());
						}
						
						List<VmStatus> vmStatusList = service.getVMStatus(jobUUIDs); 
						
						if(!CollectionUtils.isEmpty(vmStatusList)){
							for(int j =0; j<vmStatusList.size(); j++){
								VmStatus vmStatus = vmStatusList.get(j);
								logger.debug(list.get(0).getRecoveryServer()+" return Linux VmStatus: " + InstantVMServiceUtil.parseObjectToXmlString(vmStatus));
								for(int m = 0; m < list.size(); m++){
									if(list.get(m).getUuid().equalsIgnoreCase(vmStatus.getJobUUID())){
										getLinuxIVMStatus(list.get(m), vmStatus);
									}
								}
							}
						}
					} catch (Exception e) {
						logger.error("failed to get linux instant vm status from " + list.get(0).getRecoveryServer(), e);
					}
					
				}
				
			}
			
		});
	}
	
	private void sortInstantVMResult(List<InstantVM> vms, final InstantVMPagingConfig config) {
		Collections.sort(vms, new Comparator<InstantVM>() {
			@Override
			public int compare(InstantVM vm1, InstantVM vm2) {
				int result = 0; 
				switch (config.getOrderCol()) {					
					case name:
						result = compareValue(vm1.getName(), vm2.getName());
						break;
					case recoveryPoint:
						result = compareValue(vm1.getRecoveryPoint().getTime(), vm2.getRecoveryPoint().getTime());
						break;
					case vmLocation:
						result = compareValue(vm1.getVmLocation(), vm2.getVmLocation());
						break;
					case recoveryServer:
						result = compareValue(vm1.getRecoveryServer(), vm2.getRecoveryServer());
						break;					
					case description:
						result = compareValue(vm1.getDescription(), vm2.getDescription());
						break;
					default:
						result = compareValue(vm1.getName(), vm2.getName());
						break;
				}				
				if (EdgeSortOrder.DESC.equals(config.getOrderType())) {
					result = 0 - result;
				}
				return result;
			}
		});
	}		
	
	private int compareValue(String value1, String value2) {
		if (null == value1 && null == value2) {
			return 0;
		} else if (null == value1 && null != value2) {
			return -1;
		} else if (null != value1 && null == value2) {
			return 1;
		}							
		return value1.compareTo(value2);
	}
	
	private int compareValue(Date value1, Date value2) {
		if (null == value1 && null == value2) {
			return 0;
		} else if (null == value1 && null != value2) {
			return -1;
		} else if (null != value1 && null == value2) {
			return 1;
		}							
		return value1.compareTo(value2);
	}
	
//	public void updateInstantVM(final InstantVM vm){
//		startMonitorJob(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(vm == null){
//					return;
//				}
//				
//				if(vm.getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_WINDOWS){
//					InstantVMStatus status = null;
//					try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(vm.getRecoveryServerId())){
//						connection.connect();
//						IInstantVMService service = connection.getService();
//						status = service.GetIVMStatus(vm.getUuid());
//						logger.debug(vm.getRecoveryServer()+" return IVM Status: " + InstantVMServiceUtil.parseObjectToXmlString(status));
//						appendInstantVMStatus(vm, status);
//					} catch (Exception e) {
//						logger.error("failed to get instant vm status from " + vm.getRecoveryServer(), e);
//					}
//				}else{
//					long beginTime = System.currentTimeMillis();
//					logger.debug("begin to create connecton :" + beginTime);
//					try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(vm.getRecoveryServerId())){
//						connection.connect();
//						ILinuximagingService service = connection.getService();
//						VmStatus vmStatus = service.getVMStatus(vm.getUuid()); 
//						if(vmStatus!=null)
//							logger.debug(vm.getName()+"[VM name] "+vm.getRecoveryServer()+"[recoveryServer]"+" return IVM Status: " + vmStatus.getVmStatus());
//						getLinuxIVMStatus(vm, vmStatus);
//						
//						long endTime = System.currentTimeMillis();
//						Calendar c=Calendar.getInstance(); 
//						c.setTimeInMillis(endTime - beginTime); 
//						logger.debug("*****success get linux status, spend time: "+ c.get(Calendar.MINUTE)+" min: "+c.get(Calendar.SECOND)+" s");
//					} catch (Exception e) {
//						long excepTime = System.currentTimeMillis();
//						Calendar c=Calendar.getInstance(); 
//						c.setTimeInMillis(excepTime - beginTime); 
//						logger.error("exception get linux status, spend time:"+ c.get(Calendar.MINUTE)+" min: "+c.get(Calendar.SECOND)+" s");
//						logger.error("failed to get linux instant vm status from " + vm.getRecoveryServer(), e);
//					}
//					
//					
//				}
//			}
//			
//		});
//	}
	
//	private InstantVmStatus intToStatus(int intS){
//		InstantVmStatus status = InstantVmStatus.Unknown;
//		switch(intS){
//		case 1:
//			status = InstantVmStatus.PowerON;
//			break;
//		case 2:
//			status = InstantVmStatus.PowerOff;
//			break;
//		case 3:
//			status = InstantVmStatus.Suspended;
//			break;
//		case 4:
//			status = InstantVmStatus.Unknown;
//			break;
//		}
//		return status;
//	}
	
	private void getLinuxIVMStatus(InstantVM vm, VmStatus vmStatus){
		if(vmStatus == null){
			return;
		}
		InstantVmStatus status = InstantVmStatus.Unknown;
		switch(vmStatus.getVmStatus()){
		case VmStatus.VM_STATUS_JOB_NOT_EXIST:
			logger.error("job doesn't exist at agent side, so remove vm: "+InstantVMServiceUtil.parseObjectToXmlString(vm));
			remove(vm);
			break;
		case VmStatus.VM_STATUS_WAITING_BOOT:
		case VmStatus.VM_STATUS_VM_RUNNING:
			switch(vmStatus.getPowerStatus()){
				case 1:
					status = InstantVmStatus.PowerON;
					break;
				case 2:
					status = InstantVmStatus.PowerOff;
					break;
				case 3:
					status = InstantVmStatus.Suspended;
					break;
				case 4:
					status = InstantVmStatus.Unknown;
					break;
			}
			break;
		case VmStatus.VM_STATUS_JOB_NOT_RUNNING:
			status = InstantVmStatus.Unknown;
			break;
		case VmStatus.VM_STATUS_VM_NOT_CREATE:
			status = InstantVmStatus.Unknown;
			break;
		case VmStatus.VM_STATUS_VM_NOT_EXIST:
			status = InstantVmStatus.NotExist;
			break;
		case VmStatus.VM_STATUS_VM_PREPARE:
			status = InstantVmStatus.Prepare;
			break;
		case VmStatus.VM_STATUS_JOB_FAILED:
			status = InstantVmStatus.Error;
			break;
		case VmStatus.VM_STATUS_JOB_CANCELED:
			status = InstantVmStatus.NotExist;
			break;
		case VmStatus.VM_STATUS_CAN_NOT_CONNECT_TO_HYPERVISOR:
			status = InstantVmStatus.ConnectionError;
			break;
		default:
			status = InstantVmStatus.Unknown;
		}
		
		if(vmStatus.getVmName()!=null && !"".equals(vmStatus.getVmName()))
			vm.setName(vmStatus.getVmName());
		vm.setJobId(vmStatus.getJobId());
		vm.setStatus(status);
	}
	
	
	private void appendInstantVMStatus(InstantVM vm, InstantVMStatus status) {
		if(status==null||status.getJobState()==null){
			return;
		}
		vm.setJobId(status.getJobID());
		switch(status.getJobState()){
		case Unknown:	// No job script
			logger.error("job doesn't exist at agent side, so remove vm: "+InstantVMServiceUtil.parseObjectToXmlString(vm));
			remove(vm);
			break;	
		case Running:
			switch(status.getNodesStatus().get(0).getState()){
			case Unknown:
				vm.setStatus(InstantVmStatus.Unknown);
				break;
			case PowerOn:
				vm.setStatus(InstantVmStatus.PowerON);
				break;
			case PowerOff:
				vm.setStatus(InstantVmStatus.PowerOff);
				break;
			case Suspended:
				vm.setStatus(InstantVmStatus.Suspended);
				break;
			case NotExist:
				vm.setStatus(InstantVmStatus.NotExist);
				break;
			case Abnormal:
				vm.setStatus(InstantVmStatus.Abnormal);
				break;
			default:
				vm.setStatus(InstantVmStatus.Unknown);
			}
			break;
		case Crash:		// Agent crashed
		case Error:
			vm.setStatus(InstantVmStatus.Error);
			break;
		default:
			vm.setStatus(InstantVmStatus.Unknown);
		}
		if(status.getNodesStatus()==null||status.getNodesStatus().size()==0){
			return;
		}
		if (status.getNodesStatus() != null
				&& status.getNodesStatus().size() != 0
				&& status.getNodesStatus().get(0).getIvmName() != null
				&& !"".equals(status.getNodesStatus().get(0).getIvmName())) {

			vm.setName(status.getNodesStatus().get(0).getIvmName());
		}
		vm.getDetail().setVmFullSpace(status.getNodesStatus().get(0).getDataStoreCapacity());
		vm.getDetail().setVmFreeSpace(vm.getDetail().getVmFullSpace()-status.getNodesStatus().get(0).getUsedSpace());
		vm.getDetail().getVmInfo().setVmUUID(status.getNodesStatus().get(0).getIvmUUID());
		vm.getDetail().getVmInfo().setCPU(status.getNodesStatus().get(0).getCpuNum());
		vm.getDetail().getVmInfo().setMemorySize(status.getNodesStatus().get(0).getMemory());
	}
	
	public void startMonitorJob(Runnable task) {
		if(task==null)
			return;
		
		EdgeExecutors.getCachedPool().submit(task);
	}
	
	public long powerOnIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault {
		long result = 0;
		InstantVM vm = cachedMap.get(instantVMJobUUID);
		if(vm==null){
			return -100;
		}
		if(vm.getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_WINDOWS){
			try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(vm.getRecoveryServerId())){
				connection.connect();
				IInstantVMService service = connection.getService();
	//			result = service.PowerOnIvm(instantVMJobUUID, vm.getDetail().getVmInfo().getVmUUID());
				logger.info("connect "+vm.getRecoveryServer()+ " to power on IVM: "+instantVMJobUUID);
				result = service.PowerOnIvm(instantVMJobUUID, null);
				logger.info(vm.getRecoveryServer()+" return: "+result);
				if(result == 0){
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMSuccessful", vm.getNodeName()), -1);
				}else{
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMFailed", vm.getNodeName()), -1);
				}
			} catch (Exception e) {
				logger.error("failed to powerOnIVM from " + vm.getRecoveryServer(), e);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMFailed", vm.getNodeName()), -1);
				throw e;
			}
		}else{
			try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(vm.getRecoveryServerId())){
				connection.connect();
				ILinuximagingService service = connection.getService();
				logger.info("connect "+vm.getRecoveryServer()+ " to power on IVM: "+instantVMJobUUID);
				result = service.powerOnVM(instantVMJobUUID);
				logger.info(vm.getRecoveryServer()+" return: "+result);
				if(result == 0){
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMSuccessful", vm.getNodeName()), -1);
				}else{
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMFailed", vm.getNodeName()), -1);
				}
			} catch (Exception e) {
				logger.error("failed to powerOnIVM from " + vm.getRecoveryServer(), e);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOnInstantVMFailed", vm.getNodeName()), -1);
				throw e;
			}
		}
		return result;
	}

	public long powerOffIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault {
		long result = 0;
		InstantVM vm = cachedMap.get(instantVMJobUUID);
		if(vm==null){
			return -100;
		}
		if(vm.getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_WINDOWS){
			try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(vm.getRecoveryServerId())){
				connection.connect();
				IInstantVMService service = connection.getService();
	//			result = service.PowerOffIvm(instantVMJobUUID, vm.getDetail().getVmInfo().getVmUUID());
				logger.info("connect "+vm.getRecoveryServer()+ " to power off IVM: "+instantVMJobUUID);
				result = service.PowerOffIvm(instantVMJobUUID, null);
				logger.info(vm.getRecoveryServer()+" return: "+result);
				if(result == 0){
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMSuccessful", vm.getNodeName()), -1);
				}else{
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMFailed", vm.getNodeName()), -1);
				}
			} catch (Exception e) {
				logger.error("failed to powerOffIVM from " + vm.getRecoveryServer(), e);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMFailed", vm.getNodeName()), -1);
				throw e;
//				throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, ""));
			}
		}else{
			try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(vm.getRecoveryServerId())){
				connection.connect();
				ILinuximagingService service = connection.getService();
				logger.info("connect "+vm.getRecoveryServer()+ " to power off IVM: "+instantVMJobUUID);
				result = service.powerOffVM(instantVMJobUUID);
				logger.info(vm.getRecoveryServer()+" return: "+result);
				if(result == 0){
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMSuccessful", vm.getNodeName()), -1);
				}else{
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMFailed", vm.getNodeName()), -1);
				}
			} catch (Exception e) {
				logger.error("failed to powerOffIVM from " + vm.getRecoveryServer(), e);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("powerOffInstantVMFailed", vm.getNodeName()), -1);
				throw e;
			}
		}
		return result;
	}

	public InstantVMOperationResult stopInstantVM(String instantVMJobUUID, boolean forceRemove) {
		InstantVMOperationResult result = new InstantVMOperationResult(false);
		result.setIVMJobUUID(instantVMJobUUID);
		InstantVM vm = cachedMap.get(instantVMJobUUID);
		if(vm==null){
			return result;
		}
		if(vm.getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_WINDOWS){
			try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(vm.getRecoveryServerId())){
				connection.connect();
				IInstantVMService service = connection.getService();
				logger.info("connect "+vm.getRecoveryServer()+ " to stop InstantVM: "+instantVMJobUUID);
				long ret = service.stopInstantVM(instantVMJobUUID, true);
				logger.info(vm.getRecoveryServer()+ " return: "+ret);
				if(ret == 0){
					result.setResult(true);
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMSuccessful", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				}else{
					result.setResult(false);
					result.setErrorCode(ret);
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMFailed", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				}
			} catch (Exception e) {
				result.setResult(false);
				result.setErrorCode(InstantVMOperationResult.FAIL_CONNECT_RECOVERY_SERVER);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMFailed", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				logger.error("failed to stopInstantVM from " + vm.getRecoveryServer(), e);
			}
		}else{
						
			try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(vm.getRecoveryServerId())){
				connection.connect();
				int re = -1;
				ILinuximagingService service = connection.getService();
				logger.info("connect "+vm.getRecoveryServer()+ " to stop InstantVM: "+instantVMJobUUID);
				re= service.deleteInstantVMJob(instantVMJobUUID);
				logger.info(vm.getRecoveryServer()+" return: "+re);
				if(re == 0){
					result.setResult(true);
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMSuccessful", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				}else{
					result.setResult(false);
					result.setErrorCode(re);
					InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMFailed", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				}
			} catch (Exception e) {
				result.setResult(false);
				result.setErrorCode(InstantVMOperationResult.FAIL_CONNECT_RECOVERY_SERVER);
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, vm.getNodeId(), vm.getNodeName(), EdgeCMWebServiceMessages.getMessage("stopInstantVMFailed", vm.getNodeName()), JobType.JOBTYPE_STOP_INSTANT_VM);
				logger.error("failed to stopInstantVM from " + vm.getRecoveryServer(), e);
			}
		}
		if(result.isResult()){
			remove(vm);
		}else if(forceRemove){
			remove(vm);
		}
		logger.info("stopInstantVM("+instantVMJobUUID+", "+forceRemove+") return: "+InstantVMServiceUtil.parseObjectToXmlString(result));
		return result;
	}

	public List<InstantVM> getAllInstantVMs() {
		List<InstantVM> vms = new ArrayList<InstantVM>(cachedMap.values());
		return vms;
	}
	
	/*Instant VM Job Monitor Implement*/
	/**
	 * the first job monitor, we should record it into table as_edge_d2dJobHistory_monitor
	 * @param jobMonitor
	 * @param productType
	 */
	public void handleJobMonitor(FlashJobMonitor jobMonitor, JobHistoryProductType productType) {
		logger.debug("handleJobMonitor: " + jobMonitor + " from " + productType.name());
		InstantVMJobMonitor monitor = convert2InstantVMJobMonitor(jobMonitor, productType);
		if(monitor==null){
			return;
		}
		logger.debug(productType.name()+" source, cache key is " + monitor.getJobMonitorId());
		
		InstantVM ivm = getInstantVMByUuid(jobMonitor.getJobUUID());
		if(ivm == null){
			return;
		}
		if(productType == JobHistoryProductType.LinuxD2D){
			if(ivm.isStartJobFinished() && jobMonitor.getJobPhase() != SynchronizeContext.JOBPHASE_CANCEL_JOB){
				// SynchronizeContext.JOBPHASE_CANCEL_JOB is Stop IVM job, which should display job monitor.
				logger.debug("[LinuxD2D]Start IVM job " + jobMonitor.getJobUUID()+ " finished. No need to display job monitor.");
				return;
			}
		}else if(productType == JobHistoryProductType.D2D){
			if(ivm.isStartJobFinished() && jobMonitor.getJobType() == JobType.JOBTYPE_START_INSTANT_VM){
				logger.debug("[D2D]Start IVM job " + jobMonitor.getJobUUID()+ " finished. No need to display job monitor.");
				return;
			}
		}
		
		D2DAllJobStatusCache.getD2DAllJobStatusCache().put(monitor.getJobMonitorId(), monitor);
		logger.debug(productType.name()+" job monitor: " + InstantVMServiceUtil.parseObjectToXmlString(jobMonitor));
	}
	
	private InstantVM getInstantVMByUuid(String jobUuid){
		List<InstantVM> ivms = getAllInstantVMs();
		if(ivms == null || ivms.size()==0){
			return null;
		}
		for(InstantVM ivm : ivms){
			if(ivm.getUuid().equalsIgnoreCase(jobUuid)){
				return ivm;
			}
		}
		return null;
	}
	
	private String generateJobMonitorId(FlashJobMonitor jobMonitor, JobHistoryProductType productType){
		StringBuilder sb = new StringBuilder();
		sb.append(productType.name()).append("[IVM]")
				.append("-").append(jobMonitor.getNodeId())
				.append("-").append(jobMonitor.getRunningServerId())
				.append("-").append(jobMonitor.getJobType())
				.append("-").append(jobMonitor.getJobUUID());
		return sb.toString();
	}
	
	private String generateJobMonitorId4RPS(InstantVMJobMonitor monitor, InstantVM ivm) {
		StringBuilder sb = new StringBuilder();
		sb.append(JobHistoryProductType.RPS.name()).append("[IVM]")
				.append("-").append(ivm.getDetail().getRpsServerId())
				.append("-").append(monitor.getNodeId())
				.append("-").append(monitor.getJobType())
				.append("-").append(monitor.getJobUUID());
		return sb.toString();
	}
	
	private float calculateProgress(FlashJobMonitor jm) {
		long phase = jm.getJobPhase();
		if(phase == InstantVMConfig.IVM_JOB_INIT){
			return 25f;
		}else if(phase == InstantVMConfig.IVM_JOB_START){
			return 50f;
		}else if(phase == InstantVMConfig.IVM_JOB_CREATED){
			return 75f;
		}else if(phase == InstantVMConfig.IVM_JOB_RUNNING){
			return 100f;
		}else if(phase == InstantVMConfig.IVM_JOB_FAILED){
			return 100f;
		}else if(phase == InstantVMConfig.IVM_JOB_STOPPING){
			return 50f;
		}else if(phase == InstantVMConfig.IVM_JOB_STOPPED){
			return 100f;
		}
		return 0;
	}
	
	private float calculateLinuxProgress(FlashJobMonitor jm) {
		long phase = jm.getJobPhase();
		if(phase == InstantVMConfig.IVM_JOB_INIT){
			return 20f;
		}else if(phase == InstantVMConfig.IVM_JOB_START){
			return 40f;
		}else if(phase == InstantVMConfig.IVM_JOB_CREATED){
			return 60f;
		}else if(phase == InstantVMConfig.IVM_JOB_RUNNING){
			return 80f;
		}else if(phase == InstantVMConfig.IVM_JOB_READY){
			return 100f;
		}else if(phase == InstantVMConfig.IVM_JOB_FAILED){
			return 100f;
		}else if(phase == InstantVMConfig.IVM_JOB_STOPPING){
			return 50f;
		}else if(phase == InstantVMConfig.IVM_JOB_STOPPED){
			return 100f;
		}
		return 0;
	}

	private List<InstantVM> getInstantVMList(int nodeId, int proxyId) {
		List<InstantVM> result = new ArrayList<InstantVM>();
		List<InstantVM> ivms = getAllInstantVMs();
		if(ivms == null || ivms.size()==0){
			return result;
		}else{
			for(InstantVM ivm : ivms){
				if(ivm.getNodeId() == nodeId && ivm.getRecoveryServerId() == proxyId){
					result.add(ivm);
				}
			}
		}
		return result;
	}
	
	/**
	 * Windows Instant VM job monitor implement for D2DJobMonitorReader
	 * @param nodeId
	 * @param proxyId
	 * @return
	 */
	public List<InstantVMJobMonitor> getInstantVMJobMonitor(int nodeId, int proxyId) {
		logger.debug("[D2D][Windows]getInstantVMJobMonitor() nodeId: "+nodeId+", proxyId: "+proxyId);
		IInstantVMService service;
		List<InstantVMJobMonitor> list = new ArrayList<InstantVMJobMonitor>();
		List<InstantVM> ivms = getInstantVMList(nodeId, proxyId);
		if(ivms.size()==0){
			logger.debug("[D2D][Windows]There is no instant vm running for nodeId: "+nodeId+", proxyId: "+proxyId);
			return list;
		}
		try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(proxyId)){
			connection.connect();
			service = connection.getService();
			for(InstantVM ivm : ivms){
				IVMJobMonitor jm =service.queryInstantVM(ivm.getUuid());
				logger.debug("[D2D][Windows]"+ivm.getRecoveryServer()+" ID "+ivm.getRecoveryServerId()+" queryInstantVM("+ivm.getUuid()+") return: "+InstantVMServiceUtil.parseObjectToXmlString(jm));
				if(jm!=null && jm.getJobId()>0){
					InstantVMJobMonitor monitor = convert2InstantVMJobMonitor(jm, JobHistoryProductType.D2D);
					list.add(monitor);
					logger.debug("[D2D][Windows]InstantVMJobMonitor: "+InstantVMServiceUtil.parseObjectToXmlString(monitor));
				}
			}
			
		} catch (Exception e) {
			logger.error("[D2D][Windows]failed to queryInstantVM() ", e);
		}
		return list;
	}
	
	/**
	 * Windows Instant VM job monitor implement for RPSJobMonitorReader
	 * @param jm
	 * @param productType
	 * @return
	 */
	public InstantVMJobMonitor getInstantVMJobMonitor4RPS(FlashJobMonitor jm) {
		logger.debug("[RPS][Windows]getInstantVMJobMonitor4RPS() FlashJobMonitor: "+InstantVMServiceUtil.parseObjectToXmlString(jm));
		InstantVM ivm = getInstantVMByUuid(jm.getJobUUID());
		if(ivm != null && ivm.isStartJobFinished() && jm.getJobType() == JobType.JOBTYPE_START_INSTANT_VM){
			logger.debug("[RPS][Windows]Start IVM job " + jm.getJobUUID()+ " finished. No need to display job monitor.");
			return null;
		}
		InstantVMJobMonitor monitor = convert2InstantVMJobMonitor(jm, JobHistoryProductType.RPS);
		logger.debug("[RPS][Windows]InstantVMJobMonitor: "+InstantVMServiceUtil.parseObjectToXmlString(monitor));
		return monitor;
	}
	
	private InstantVMJobMonitor convert2InstantVMJobMonitor(FlashJobMonitor jm, JobHistoryProductType productType) {
		if(jm==null)
			return null;
		InstantVM ivm = getInstantVMByUuid(jm.getJobUUID());
		if(ivm == null){
			logger.debug("["+productType.name()+"][Windows]There is no running instant VM " + jm.getJobUUID());
			return null;
		}
			
		InstantVMJobMonitor monitor = new InstantVMJobMonitor();
		monitor.setNodeId(ivm.getNodeId());
		monitor.setAgentNodeName(ivm.getNodeName());
		monitor.setD2dUuid(ivm.getNodeUuid());
		monitor.setRunningServerId(ivm.getRecoveryServerId());
		monitor.setServerNodeName(ivm.getRecoveryServer());
		monitor.setHypervisor(ivm.getDetail().getHypervisor());
		monitor.setProxy(ivm.getRecoveryServer());
		monitor.setVmPath(ivm.getDetail().getVmInfo().getVmConfigPath());
		monitor.setRecoveryPoint(ivm.getRecoveryPoint());
		
		if(jm instanceof IVMJobMonitor){
			monitor.setVmName(((IVMJobMonitor) jm).getVmDisplayName());
		}else{
			monitor.setVmName(ivm.getName());
		}
		
		monitor.setLinuxNode(jm.isLinuxNode());
		monitor.setJobUUID(jm.getJobUUID());
		monitor.setRunningOnRPS(jm.isRunningOnRPS());
		monitor.setVmInstanceUUID(jm.getVmInstanceUUID());
		monitor.setJobId(jm.getJobId());
		monitor.setJobType(jm.getJobType());
		monitor.setJobMethod(jm.getJobMethod());
		monitor.setStartTime(jm.getStartTime());
		monitor.setElapsedTime(jm.getElapsedTime());
		monitor.setJobStatus(jm.getJobStatus());
		monitor.setJobPhase(jm.getJobPhase());
		monitor.setProgress(calculateProgress(jm));
		
		if(productType == JobHistoryProductType.LinuxD2D){
			/*if(ivm.isStartJobFinished()){
				monitor.setJobType(JobType.JOBTYPE_STOP_INSTANT_VM);
			}else{
				monitor.setJobType(JobType.JOBTYPE_START_INSTANT_VM);
			}
			if(jm.getJobPhase() == SynchronizeContext.JOBPHASE_READY_FOR_USE || jm.getJobPhase() == SynchronizeContext.JOBPHASE_RUNNING){
				ivm.setStartJobFinished(true);
			}*/
			if(jm.isFinished()){
				ivm.setStartJobFinished(true);
			}
		}else if(productType == JobHistoryProductType.D2D){
			if(jm.getJobPhase() == InstantVMConfig.IVM_JOB_RUNNING || jm.getJobPhase() == InstantVMConfig.IVM_JOB_FAILED){
				ivm.setStartJobFinished(true);
			}
		}
		
		monitor.setDataStoreUUID(jm.getDataStoreUUID());
		monitor.setFinished(jm.isFinished());
		monitor.setOnDemand(jm.isOnDemand());
		monitor.setRemainTime(jm.getRemainTime());
		monitor.setSourceRPSId(jm.getSourceRPSId());
		monitor.setTargetRPSId(jm.getTargetRPSId());
		monitor.setTargetRpsUUID(jm.getTargetRpsUUID());
		
		monitor.setHistoryProductType(productType.getValue());
		if(productType==JobHistoryProductType.RPS){
			monitor.setJobMonitorId(generateJobMonitorId4RPS(monitor, ivm));
		}else{
			monitor.setJobMonitorId(generateJobMonitorId(monitor, productType));
		}
		
		return monitor;
	}
	
	/**
	 * Linux Instant VM job monitor implement for LinuxD2DJobMonitorReader, RPSJobMonitorReader
	 * @param jobUUID
	 * @param productType
	 * @return
	 */
	public InstantVMJobMonitor getInstantVMJobMonitor4Linux(String jobUUID, JobHistoryProductType productType) {
		logger.debug("["+productType.name()+"][Linux]getInstantVMJobMonitor4Linux(): "+jobUUID);
		InstantVM ivm = getInstantVMByUuid(jobUUID);
		if(ivm == null){
			logger.debug("["+productType.name()+"][Linux]There is no running instant VM " + jobUUID);
			return null;
		}
		
		try(LinuxD2DConnection linuxD2DConnection = connectionFactory.createLinuxD2DConnection(ivm.getRecoveryServerId())){
			linuxD2DConnection.connect();
			ILinuximagingService linuxService = linuxD2DConnection.getService();	
			List<JobStatus> list = linuxService.getInstantVMJobList(jobUUID);
			JobStatus status = null;
			if(list == null || list.size() == 0){
				logger.debug("["+productType.name()+"][Linux]"+ivm.getRecoveryServer()+" ID "+ivm.getRecoveryServerId()+" getInstantVMJobList("+jobUUID+") return empty.");
			}else{
				status = list.get(0);
				logger.debug("["+productType.name()+"][Linux]"+ivm.getRecoveryServer()+" ID "+ivm.getRecoveryServerId()+" getInstantVMJobList("+jobUUID+") return IVM job status: "+InstantVMServiceUtil.parseObjectToXmlString(status));
			}
			if(status!=null && status.getJobID()>0){
				status.setUuid(jobUUID);
				InstantVMJobMonitor jobMonitor = convert2InstantVMJobMonitor(status, productType);
				logger.debug("["+productType.name()+"][Linux]InstantVMJobMonitor: "+InstantVMServiceUtil.parseObjectToXmlString(jobMonitor));
				return jobMonitor;
			}
		} catch (EdgeServiceFault e) {
			logger.error("["+productType.name()+"][Linux]getInstantVMJobList() occurs exception: ",e);
		}
		logger.debug("["+productType.name()+"][Linux]InstantVMJobMonitor: null");
		return null;
	}
	
	private InstantVMJobMonitor convert2InstantVMJobMonitor(JobStatus status, JobHistoryProductType productType) {
		InstantVM ivm = getInstantVMByUuid(status.getUuid());
		if(ivm == null) 
			return null;
		InstantVMJobMonitor monitor = new InstantVMJobMonitor();
		monitor.setNodeId(ivm.getNodeId());
		monitor.setAgentNodeName(ivm.getNodeName());
		monitor.setD2dUuid(ivm.getNodeUuid());
		monitor.setRunningServerId(ivm.getRecoveryServerId());
		monitor.setServerNodeName(ivm.getRecoveryServer());
		monitor.setVmName(ivm.getName());
		monitor.setHypervisor(ivm.getDetail().getHypervisor());
		monitor.setProxy(ivm.getRecoveryServer());
		monitor.setVmPath(ivm.getDetail().getVmInfo().getVmPath());
		monitor.setRecoveryPoint(ivm.getRecoveryPoint());
		monitor.setDataStoreUUID(ivm.getDetail().getDataStoreUuid());
		
		monitor.setLinuxNode(true);
		monitor.setJobUUID(status.getUuid());
		monitor.setRunningOnRPS(false);
//		monitor.setVmInstanceUUID(status.getVmInstanceUUID());
		monitor.setJobId(status.getJobID());
		monitor.setJobType(status.getJobType());
		monitor.setJobMethod(InstantVMServiceUtil.convert2JobMethod(status.getJobType()));
		monitor.setStartTime(status.getExecuteTime());
		monitor.setElapsedTime(status.getElapsedTime());
		monitor.setJobStatus(InstantVMServiceUtil.convert2JobStatus(status.getStatus()));
		monitor.setJobPhase(status.getJobPhase());
		monitor.setProgress(calculateLinuxProgress(monitor));
		//TODO Linux job phase can't sign the start job finished.
		
		monitor.setHistoryProductType(productType.getValue());
		monitor.setJobMonitorId(generateJobMonitorId(monitor, productType));
		
		return monitor;
	}
	
	
	public void changeDataStoreName(String dataStoreUUID, String dataStoreName){
		if(StringUtil.isEmptyOrNull(dataStoreUUID)){
			return;
		}
		List<InstantVM> vms = getAllInstantVMs();
		if(vms!=null && vms.size()!=0){
			for(InstantVM vm : vms){
				if(dataStoreUUID.equals(vm.getDetail().getDataStoreUuid())){
					vm.getDetail().setDataStore(dataStoreName);
				}
			}
		}
	}

	public boolean isRecoveryServer(int id) {
		List<InstantVM> ivms = getAllInstantVMs();
		if(ivms == null || ivms.size()==0){
			return false;
		}
		for(InstantVM ivm : ivms){
			if(ivm.getRecoveryServerId() == id){
				return true;
			}
		}
		return false;
	}


}
