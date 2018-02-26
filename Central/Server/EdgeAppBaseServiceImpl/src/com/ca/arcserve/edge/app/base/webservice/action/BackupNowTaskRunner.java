package com.ca.arcserve.edge.app.base.webservice.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.LinuxD2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.BackupNowTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.base.webservice.node.LinuxNodeServiceImpl;

public class BackupNowTaskRunner extends AbstractTaskRunner<Integer>{
	
	private static final Logger logger = Logger.getLogger(BackupNowTaskRunner.class);
	private LinuxNodeServiceImpl linuxNodeService = new LinuxNodeServiceImpl();
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private static IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	private Node node;
	private BackupNowTaskParameter backupNowTaskParameter;
	
	public BackupNowTaskRunner(Integer entityKey,
			ActionTaskParameter<Integer> parameter, CountDownLatch doneSignal,
			ActionTaskManager<Integer> manager) {
		super(entityKey, parameter, doneSignal, manager);
		backupNowTaskParameter = (BackupNowTaskParameter)parameter;
	}

	@Override
	protected void excute() {
		logger.info("[BackupNowTaskRunner] excute() Submit backup job begin, node id = " + entityKey + ".");
		boolean isLinuxNode = false;
		
		try {
			node = getNodeById(entityKey);
			if (isLinuxNodeAllowBackup(node)){
				isLinuxNode = true;
				submitLinuxBackupJob(node);
			} else if (isD2DNodeAllowBackup(node)) {
				submitBackupJob(node);
			} else if (isVMNodeAllowBackup(node)) {
				if (node.getVmStatus() == VMStatus.DELETED.getValue()) {
					createWarnningResult(Severity.Warning, node, EdgeCMWebServiceMessages.getMessage("submitD2DBackupJobsSkipGrayedOut", node.getVmName()));
					String vmName = StringUtil.isEmptyOrNull(node.getHostname())?("VM("+node.getVmName()+")"):node.getHostname();
					logger.info("[BackupNowTaskRunner] submitBackupJob() warnning. node: "+node.getId()+"_"+vmName+"is not exist in esx/vcenter.");
				} else {
					submitVMBackupJob(node);
				}
			} else{//made failed entity for the node which have no plan.
				EdgeServiceFaultBean fault  = new EdgeServiceFaultBean(EdgeServiceErrorCode.Backup_NoBackupConfiguration, null);
				String errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),fault);
				createFailedResult(Severity.Error, node, errorMessage);
			}
		} catch (SOAPFaultException e) {
			logger.error("[BackupNowTaskRunner] excute() Submit backup job failed.",e);
			if (e.getFault().getFaultCodeAsQName().getLocalPart() != null) {
				generateFailedResultForAgentSideError(e,isLinuxNode);
			} else {
				if (isLinuxNode) {
					createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitLinuxBackupJobsFailed"));
				} else {
					createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsFailed"));
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error("[BackupNowTaskRunner] excute() Submit backup job failed.",e);
			createFailedResult(e);
		} catch (Exception e) {
			logger.error("[BackupNowTaskRunner] excute() Submit backup job failed.",e);
			if (isLinuxNode) {
				createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitLinuxBackupJobsFailed"));
			} else {
				createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsFailed"));
			}
		}
	}
	
	private void generateFailedResultForAgentSideError(SOAPFaultException e, boolean isLinuxNode){
		String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
		String errorMessage = e.getFault().getFaultString();
		String args = e.getFault().getFaultActor();
		boolean isArgsNeeded = false;
		if (args != null && !args.equals(errorMessage)) {
			isArgsNeeded = true;
		}
		if (FlashServiceErrorCode.Login_WrongUUID.equals(errorCode)	|| FlashServiceErrorCode.Login_WrongCredential.equals(errorCode)) {
			createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("failedtoConnectToD2DError"));
		} else if (FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION.equals(errorCode) 
				||FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION_MERGE.equals(errorCode)
				||FlashServiceErrorCode.Common_OtherJobIsRunning.equals(errorCode) || com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode.Common_JobIsRunning.equals(errorCode)) {
			createWarnningResult(Severity.Warning, node, isLinuxNode == true ? LinuxD2DWebServiceErrorMessages.getMessage(errorCode) : D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
		} else if (FlashServiceErrorCode.MERGE_CONVERT_MANUAL_JOB_FULL.equals(errorCode)) {
			if (parameter.getEntityIds().size() > 1) {
				try {
					if (isD2DNodeAllowBackup(node)) {
						submitBackupJob(node, false);
						logger.info("[BackupNowTaskRunner] generateFailedResultForAgentSideError() the error is MERGE_CONVERT_MANUAL_JOB_FULL, so submit again, and no convert.");
					}else {
						createFailedResult(Severity.Error, node, D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
					}
				} catch (EdgeServiceFault e1) {
					createFailedResult(Severity.Error, node, D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
				}
			} else {
				createFailedResult(Severity.Error, node, D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage));
			}
		} else {
			//defect765032 invoke agent API, agent API will return the complete message, no need to find according to errorCode
			String messageFromCode = isLinuxNode == true ? LinuxD2DWebServiceErrorMessages.getMessage(errorCode,isArgsNeeded ? args : errorMessage) :D2DWebServiceErrorMessages.getMessage(errorCode, isArgsNeeded ? args : errorMessage);
			if(StringUtil.isEmptyOrNull(messageFromCode)){
				messageFromCode = errorMessage;
			}
			createFailedResult(Severity.Error, node, messageFromCode);
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
	
	private void submitBackupJob(Node node, boolean convert) throws EdgeServiceFault {
		D2DConnection connection = connectionFactory.createD2DConnection(node.getId());
		connection.connect();
		int regStatus = connection.getService().QueryEdgeMgrStatus(
				CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
		
		if (1 != regStatus) {
			createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsManagedByOthers"));
			logger.info("[BackupNowTaskRunner] submitBackupJob() failed. the node: "+node.getId()+"_"+node.getHostname()+ "is managed by other console");
			return;
		}else {
			connection.getService().backupWithFlag(backupNowTaskParameter.getBackupType(), backupNowTaskParameter.getJobName(), convert);	
			createSuccessResult(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsSuccess"));
			logger.info("[BackupNowTaskRunner] submitBackupJob() succeed. node: "+node.getId()+"_"+node.getHostname());
		}
	}
	
	protected void submitBackupJob(Node node) throws EdgeServiceFault{
		submitBackupJob(node, true);
	}
	
	protected void submitLinuxBackupJob(Node node) throws EdgeServiceFault{
		linuxNodeService.backupLinuxNode(node.getId(), node.getHostname(), backupNowTaskParameter.getBackupType());
		createSuccessResult(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitLinuxBackupJobsSuccess"));
		logger.info("[BackupNowTaskRunner] submitLinuxBackupJob() succeed. node: "+node.getId()+"_"+node.getHostname());
	}
	
	private void submitVMBackupJob(Node node) throws EdgeServiceFault {
		String vmName = StringUtil.isEmptyOrNull(node.getHostname())?("VM("+node.getVmName()+")"):node.getHostname();
		VirtualMachine vm = new VirtualMachine();

		if (node.isVMwareMachine()){
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
			if(vmList.isEmpty()){
				logger.error("[BackupNowTaskRunner] submitVMBackupJob() Ignore this node since it doesn't have ESX information");
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
		D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));
		connection.connect();
		int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.vShpereManager, EdgeCommonUtil.getLocalFqdnName());
		if (1 != regStatus) {
			createFailedResult(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsProxyManagedByOthers"));
			logger.info("[BackupNowTaskRunner] submitVMBackupJob() failed. the node: "+node.getId()+"_"+vmName+ "is managed by other console");
			return;
		}else {
			connection.getService().backupVMWithFlag(backupNowTaskParameter.getBackupType(), backupNowTaskParameter.getJobName(), vm, false);
			createSuccessResult(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitVMBackupJobsSuccess"));	
			logger.info("[BackupNowTaskRunner] submitVMBackupJob() succeed. node: "+node.getId()+"_"+vmName);
		}
	}
	
	private void createFailedResult(EdgeServiceFault e){
		String nodeName = node.getHostname();
		if(StringUtil.isEmptyOrNull(nodeName))
			nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
		long logId = NodeExceptionUtil.generateActivityLogByException(Module.SubmitD2DJob,node,"submitBackupJob_Log", e);
		addFailedEntities(entityKey,logId);
	}
	
	private void createFailedResult(Severity severity, Node node, String message){
		long logId = generateLog(severity, node, message);
		addFailedEntities(entityKey, logId);
	}
	
	private void createWarnningResult(Severity severity, Node node, String message){
		long logId = generateLog(severity, node, message);
		addWarnningEntities(entityKey, String.valueOf(logId));
	}
	
	private void createSuccessResult(Severity severity, Node node, String message){
		generateLog(severity, node, message);
		addSucceedEntities(entityKey);
	}
	
	private long generateLog(Severity severity, Node node, String message) {
		String logMsg = EdgeCMWebServiceMessages.getMessage("submitBackupJob_Log", message);
		ActivityLog log = new ActivityLog();
		String nodeName = (node==null?"":node.getHostname());
		int nodeId = (node==null?0:node.getId());
		if(StringUtil.isEmptyOrNull(nodeName))
			nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
		log.setNodeName(nodeName);
		log.setHostId(nodeId);
		log.setModule(Module.SubmitD2DJob);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(logMsg);
		
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error("[BackupNowTaskRunner] generateLog() failed.", e);
		}
		return 0;
	}
	
	private Node getNodeById(int id) throws EdgeServiceFault{
		NodeDetail nodeDetail = nodeService.getNodeDetailInformation(id);	
		List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
		policyDao.getHostPolicyMap(id, PolicyTypes.Unified, maps);
		if (!maps.isEmpty()) {
			List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
			policyDao.as_edge_policy_list(maps.get(0).getPolicyId(), 0, policyList);	
			if (!policyList.isEmpty()) {
				nodeDetail.setPolicyType(policyList.get(0).getType());
				nodeDetail.setPolicyContentFlag(policyList.get(0).getContentflag());				
			}
		}
		return nodeDetail;
	}
}
