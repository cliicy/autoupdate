package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DNodeStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.D2DQueryTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentExceptionErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;

public class DeployUtil {
	private static final Logger logger = Logger.getLogger(DeployUtil.class);
	private static IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();

	public static void updatePlanDeployStatus(int policyId, int status) {
		edgePolicyDao.updateDeployTasks_UpdateDeployTaskStatus_ByPolicyId(policyId, status);
	}

	public static void updateNodeDeployStatus(int nodeId, int status) {
		edgePolicyDao.updateDeployTasks_UpdateDeployTaskStatus_ByHostId(nodeId, status);
	}

	public static boolean hasError(List<PolicyDeploymentError> errorList) {
		if (errorList == null) {
			return false;
		}

		for (PolicyDeploymentError error : errorList) {
			if (error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error)
				return true;
		}

		return false;
	}

	public static synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful)
			throws DeploymentException {
		setDeployStatus(task, isSuccessful, isSuccessful ? 0 : PolicyDeployStatus.DeploymentFailed);
	}

	public static synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful, int falsereason)
			throws DeploymentException {
		setDeployStatus(task, isSuccessful, falsereason, false);
	}

	public static synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful, int falsereason,
			boolean forceunassign) throws DeploymentException {
		boolean isInTransaction = false;
		boolean isTransactionCompleted = false;

		try {
			edgePolicyDao.beginTrans();
			isInTransaction = true;

			List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.getHostPolicyMap(task.getHostId(), task.getPolicyType(), mapList);

			if (mapList.size() <= 0)
				return;

			EdgeHostPolicyMap map = mapList.get(0);

			if (map.getDeployStatus() != PolicyDeployStatus.Deploying) {
				// the record has been changed
				isTransactionCompleted = true;
				return;
			}

			if ((map.getDeployReason() == PolicyDeployReasons.PolicyUnassigned) && (forceunassign || isSuccessful)) {
				if(Utils.hasBit(task.getContentFlag(), PlanTaskType.LinuxBackup)){
					updateHostMapForLinux(task);
				}else{
					edgePolicyDao.deleteHostPolicyMap(task.getHostId(), task.getPolicyType());
				}
				isTransactionCompleted = true;
				return;
			}

			int deployStatus = isSuccessful ? PolicyDeployStatus.DeployedSuccessfully : falsereason;
			map.setDeployStatus(deployStatus);
			map.setTryCount(map.getTryCount() + 1);

			if (isSuccessful) {
				map.setLastSuccDeploy(new Date());
			}

			edgePolicyDao.setHostPolicyMap(task.getHostId(), task.getPolicyType(), map.getPolicyId(),
					map.getDeployStatus(), map.getDeployReason(), map.getDeployFlags(), map.getTryCount(),
					map.getLastSuccDeploy());

			isTransactionCompleted = true;
		} catch (Exception e) {
			logger.error("setDeployStatus failed.", e);
			throw new DeploymentException(DeploymentExceptionErrorCode.SetDeployStatusException);
		} finally {
			if (isInTransaction) {
				if (isTransactionCompleted)
					edgePolicyDao.commitTrans();
				else
					// not completed
					edgePolicyDao.rollbackTrans();
			}
		}
	}

	private static void updateHostMapForLinux(PolicyDeploymentTask task){
		if((task.getDeployFlags() & PolicyDeployFlags.BackupTaskDeleted)!=0 || (task.getDeployFlags() & PolicyDeployFlags.UnregisterNodeAfterUnassign) !=0){
			@SuppressWarnings("unchecked")
			List<Integer> nodeList = (List<Integer>)task.getTaskParameters();
			for(Integer nodeId:nodeList){
				edgePolicyDao.deleteHostPolicyMap(nodeId, task.getPolicyType());
			}
		}else{
			edgePolicyDao.deleteHostPolicyMapByPolicyId(task.getPolicyId(), task.getPolicyType());
		}
	}

	public static void addDeploymentLogs(long edgeTaskId, PolicyDeploymentTask task,
			List<PolicyDeploymentError> errorList) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentLogs(edgeTaskId, task, errorList);
	}

	public static void addDeploymentSucceedLog(long edgeTaskId, PolicyDeploymentTask task, String nodeName) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentSucceedLog(edgeTaskId, task, nodeName);
	}

	public static void addWarningErrorMessageFromD2D(PolicyDeploymentTask task, List<PolicyDeploymentError> errorList,
			boolean removePolicyFlag) {
		warningErrorMessageWriter.addWarningErrorMessageFromD2D(task, errorList, removePolicyFlag);
	}

	public static void addDeploymentFailedLog(long edgeTaskId, PolicyDeploymentTask task, String nodeName,
			String failureReason) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog(edgeTaskId, task, nodeName, failureReason);
	}

	public static void addDeploymentFailedLog(PolicyDeploymentTask task, String nodeName, String failureReason) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog(task, nodeName, failureReason);
	}

	public static void addDeploymentFailedLog(long edgeTaskId, PolicyDeploymentTask task, String nodeName) {
		PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog(edgeTaskId, task, nodeName);
	}

	public static void writeActivityLog(Severity severity, int hostId, String nodeName, String message) {
		PolicyDeploymentLogWriter.getInstance().writeActivityLog(severity, hostId, nodeName, message);
	}

	public static void writeActivityLog(long edgeTaskId, Severity severity, int hostId,  String nodeName, String message) {
		PolicyDeploymentLogWriter.getInstance().writeActivityLog(edgeTaskId, severity, hostId, nodeName, message);
	}
	
	public static void forceCheckAgentStatus(int hostId){
		List<EdgeD2DNodeStatus> hoststatus = new ArrayList<EdgeD2DNodeStatus>();
		hostMgrDao.as_edge_enum_D2D_node_by_id(hostId, hoststatus);
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(hostId, 1, hosts);
		if(hoststatus.size()>0 && hosts.size()>0){
			D2DQueryTask task=new D2DQueryTask(hoststatus.get(0), hosts.get(0));
			task.run();
		}
	}
}
