package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.LinuxD2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.LinuxD2DJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class BackupNowJob {
	
	private static final Logger logger = Logger.getLogger(BackupNowJob.class);
	
	protected int gatewayid = 0;
	protected int groupID;
	protected int groupType;
	protected int backupType;
	protected String jobName;
	protected IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	protected NodeServiceImpl service = new NodeServiceImpl();
	protected LinuxNodeServiceImpl linuxNodeService = new LinuxNodeServiceImpl();
	protected ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	
	private static IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	protected static NodeServiceImpl nodeService =  new NodeServiceImpl();
	
	protected BackupNowJob(){
	}
	
	public BackupNowJob(int groupID, int groupType, int backupType, String jobName){
		this.groupID = groupID;
		this.groupType = groupType;
		this.backupType = backupType;
		this.jobName = jobName;
	}
	
	public BackupNowJob(int gatewayId, int groupID, int groupType){
		this.groupID = groupID;
		this.groupType = groupType;
	}
	
	public List<Node> getNodeList(){
		try {
			List<Integer> ids = nodeService.getNodeIdsByGroup(gatewayid, groupID, groupType);
			return	nodeService.getNodeListByIDs(ids);
		} catch (Exception e) {
			logger.error("[BackupNowJob] getNodeList() failed for gatewayid: "+gatewayid+" groupId: "+groupID+" groupType: "+groupType);
		}
		return new ArrayList<Node>();
	}
	
	public void cancelBackupByGroup(){
		Runnable cancelBackupJobTask = new Runnable(){

			@Override
			public void run() {
				List<Node> nodeList = getNodeList();
				
				for (Node node:nodeList){
					//get job detail, check if backup job is running
					String jobStatusKey = node.isLinuxNode()?"LinuxD2D" : "D2D"+"-"+node.getId()+"-";
					List<FlashJobMonitor> list = D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(jobStatusKey);
					
					//cancel backup job
					if(list != null && !list.isEmpty()){
						for(FlashJobMonitor jobMonitor:list){
							
							long jobType = jobMonitor.getJobType();
							if(jobType == JobType.JOBTYPE_VM_BACKUP || jobType == JobType.JOBTYPE_BACKUP || 
									jobType == JobType.JOBTYPE_VMWARE_VAPP_BACKUP || jobType == JobType.JOBTYPE_FILECOPY_BACKUP
									|| jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE){
								if(node.isLinuxNode()){
									LinuxD2DJobMonitor linuxJobMonitor = (LinuxD2DJobMonitor)jobMonitor;
									try {
										linuxNodeService.cancelLinuxJob(linuxJobMonitor.getNodeId(), linuxJobMonitor.getJobUUID());
										logger.debug("cancel job by group: cancel linux backup job successful.");
									} catch (EdgeServiceFault e) {
										generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("CancelLinuxBackupJobinGroupFaild"));
									}
								} else {
									try {
										// For RPS waiting job ,defect 213462 Cancel backup of a plan won't cancel the backup jobs in waiting state
										if ( jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_WAITING && jobMonitor.getTargetRPSId()>0 ){										
											service.cancelJobById(jobMonitor.getTargetRPSId(), node.getHostname(), jobMonitor.getJobId(), jobMonitor.getJobType(), node.getD2DUUID(), node.getVmInstanceUUID(), true);	
										}								
										else										
											service.cancelJobById(jobMonitor.getNodeId(), node.getHostname(), jobMonitor.getJobId(), jobMonitor.getJobType(), node.getD2DUUID(), node.getVmInstanceUUID(), false);
										logger.debug("cancel job by group: cancel backup job successful.");
									} catch (EdgeServiceFault e) {
										generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("CancelBackupJobinGroupFaild"));
									}
								}
							}
						}
					}
				}
			}	
		};
		
		EdgeExecutors.getCachedPool().submit(cancelBackupJobTask);
	}
	
	public void submitBackupJobs(){
		Runnable backupJobTask = new Runnable(){

			@Override
			public void run() {
				List<Node> nodeList = getNodeList();
				
				for (Node node:nodeList){
					logger.debug("Submit backup job begin, node id = " + node.getId() + ", name = " + node.getHostname());
					boolean isLinuxNode = false;
					try {
						if (isLinuxNodeAllowBackup(node)){
							isLinuxNode = true;
							submitLinuxBackupJob(node);
						} else if (isD2DNodeAllowBackup(node)) {
							submitBackupJob(node);
						} else if (isVMNodeAllowBackup(node)) {
							if (node.getVmStatus() == VMStatus.DELETED.getValue()) {
								generateLog(Severity.Warning, node, EdgeCMWebServiceMessages.getMessage("submitD2DBackupJobsSkipGrayedOut", node.getVmName()));
							} else {
								submitVMBackupJob(node);
							}
						} else {
							logger.warn("Submit backup job failed, the node is not allowed to do backup job! id = " + node.getId() + ", name = " + node.getHostname());
						}
					} catch (SOAPFaultException e) {
						if (e.getFault().getFaultCodeAsQName().getLocalPart() != null) {
							String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
							String errorMessage = e.getFault().getFaultString();
							String args = e.getFault().getFaultActor();
							boolean isArgsNeeded = false;
							if (args != null && !args.equals(errorMessage)) {
								isArgsNeeded = true;
							}
							
							logger.debug("Submit backup job failed (SOAPFaultException), error code = " + errorCode + ", error message = " + errorMessage);
							
							if (FlashServiceErrorCode.Login_WrongUUID.equals(errorCode)	|| FlashServiceErrorCode.Login_WrongCredential.equals(errorCode)) {
								generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("failedtoConnectToD2DError"));
							} else if (FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION.equals(errorCode) 
									||FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION_MERGE.equals(errorCode)
									||FlashServiceErrorCode.Common_OtherJobIsRunning.equals(errorCode) || com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode.Common_JobIsRunning.equals(errorCode)) {
								generateLog(Severity.Warning, node, isLinuxNode == true ? LinuxD2DWebServiceErrorMessages.getMessage(errorCode) : D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
							} else if (FlashServiceErrorCode.MERGE_CONVERT_MANUAL_JOB_FULL.equals(errorCode)) {
								if (nodeList.size() > 1) {
									try {
										if (isD2DNodeAllowBackup(node)) {											
											submitBackupJob(node, false);
										}
									} catch (EdgeServiceFault e1) {
										generateLog(Severity.Error, node, D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
									}
								} else {
									generateLog(Severity.Error, node, D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
								}
							} else {								
								generateLog(Severity.Error, node, isLinuxNode == true ? LinuxD2DWebServiceErrorMessages.getMessage(errorCode,isArgsNeeded ? args : errorMessage) :D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
							}
						} else {
							if (isLinuxNode) {
								generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitLinuxBackupJobsFailed"));
							} else {
								generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsFailed"));
							}
						}
						
					} catch (EdgeServiceFault e) {
						logger.debug("Submit backup job failed (EdgeServiceFault), error code = " + e.getFaultInfo().getCode() + ", error message = " + e.getFaultInfo().getMessage());
						if (isLinuxNode) {
							generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitLinuxBackupJobsFailed"));
						} else {
							generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsFailed"));
						}
					}
				}
			}

			private boolean isD2DNodeAllowBackup(Node node) {
				return node.getPolicyType() == PolicyTypes.Unified && Utils.hasBit(node.getPolicyContentFlag(), PlanTaskType.WindowsD2DBackup);
			}
			private boolean isVMNodeAllowBackup(Node node) {
				return node.getPolicyType() == PolicyTypes.Unified && Utils.hasBit(node.getPolicyContentFlag(), PlanTaskType.WindowsVMBackup);
			} 
			
			private boolean isLinuxNodeAllowBackup(Node node){
				return node.getPolicyType() == PolicyTypes.Unified && Utils.hasBit(node.getPolicyContentFlag(), PlanTaskType.LinuxBackup);
			}
		};
		
		EdgeExecutors.getCachedPool().submit(backupJobTask);
	}
	
	private void submitBackupJob(Node node, boolean convert) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(node.getId())) {
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(
					CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
			
			if (1 != regStatus) {
				generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsManagedByOthers"));
				return;
			}
			
			connection.getService().backupWithFlag(backupType, jobName, convert);
			
			generateLog(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsSuccess"));
		} catch (WebServiceException e) {
			generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsCantConnectD2D", node.getHostname()));
		}
	}
	
	protected void submitBackupJob(Node node) throws EdgeServiceFault{
		submitBackupJob(node, true);
	}
	
	protected void submitLinuxBackupJob(Node node) throws EdgeServiceFault{
		linuxNodeService.backupLinuxNode(node.getId(), node.getHostname(), backupType);
	}
	
	private void submitVMBackupJob(Node node) throws EdgeServiceFault {
		VirtualMachine vm = new VirtualMachine();

		if (node.isVMwareMachine()){
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
			if(vmList.isEmpty()){
				logger.debug("ignore this node since it doesn't have ESX information");
				return;
			}
			vm.setVmName(vmList.get(0).getVmName());
			vm.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
			vm.setVmUUID(vmList.get(0).getVmUuid());
		}else{
			List<EdgeHyperVHostMapInfo> hypervHostMap = new LinkedList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_getById(node.getId(), hypervHostMap);

			vm.setVmName(hypervHostMap.get(0).getVmName());
			vm.setVmInstanceUUID(hypervHostMap.get(0).getVmInstanceUuid());
			vm.setVmUUID(hypervHostMap.get(0).getVmUuid());
		}
		
		ConnectionContext context = new VMConnectionContextProvider(node.getId()).create();
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.vShpereManager, EdgeCommonUtil.getLocalFqdnName());
			if (1 != regStatus) {
				generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsProxyManagedByOthers"));
				return;
			}
			
			connection.getService().backupVMWithFlag(backupType, jobName, vm, false);
			
			generateLog(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitVMBackupJobsSuccess"));
		} catch (WebServiceException e) {
			generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsCantConnectProxy", context.getHost()));
		}
	}
	
	protected boolean isAllow2SubmitBackupJob(Node node) {
		return (node.isD2dInstalled() && node.getD2dManaged() == NodeManagedStatus.Managed)
				|| (!node.isPhysicalMachine() && node.getPolicyIDForEsx() > 0);
	}
	
	protected void generateLog(Severity severity, Node node, String message) {
		ActivityLog log = new ActivityLog();
		log.setNodeName(node.getHostname());
		log.setModule(Module.SubmitD2DJob);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		
		try {
			logService.addLog(log);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected List<Node> retrieveTargetNodeList(int groupID, int groupType) {
		EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
		List<Node> nodeList = null;
		NodePagingConfig pagingConfig = new NodePagingConfig();
		
		pagingConfig.setOrderCol(NodeSortCol.hostname);
		pagingConfig.setOrderType(EdgeSortOrder.ASC);
		pagingConfig.setPagesize(Integer.MAX_VALUE);
		pagingConfig.setStartpos(0);
		
		
		try {
			NodePagingResult result = service.getNodesESXByGroupAndTypePaging(groupID, groupType, nodeFilter, pagingConfig);
			nodeList = result.getData();
		} catch (EdgeServiceFault e) {
			return new LinkedList<Node>();
		}
		return nodeList;
	}
	
	protected List<Node> retrieveTargetNodeList(int[] ids){
		if (ids == null)
			return new LinkedList<Node>();
		
		List<Node> nodeList = new ArrayList<Node>(ids.length);
		for (int id:ids){
			try {
				NodeDetail node = service.getNodeDetailInformation(id);
				
				List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
				policyDao.getHostPolicyMap(id, PolicyTypes.Unified, maps);
				
				if (!maps.isEmpty()) {
					List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
					policyDao.as_edge_policy_list(maps.get(0).getPolicyId(), 0, policyList);
					
					if (!policyList.isEmpty()) {
						node.setPolicyType(policyList.get(0).getType());
						node.setPolicyContentFlag(policyList.get(0).getContentflag());
						nodeList.add(node);						
					}
				}
			} catch (EdgeServiceFault e) {
				logger.error("can't find node by id", e);
			}
			
		}
		
		return nodeList;
	}
}
