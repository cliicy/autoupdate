package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyDeployTask;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.deploytaskrunner.UnifiedDeployTaskRunner;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class PlanStatusUpdater {
	private static IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private static PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
	
	public static void updatePlanPendingScheduleDeploy(DeployTargetDetail target){
		int[] conditiion = new int[]{PolicyDeployStatus.ToBeDeployed,PolicyDeployStatus.Deploying};
		updatePolicyHostMapStatusByCondition(PolicyDeployStatus.ToBeDeployAsScheduled,target, conditiion);
	}
	
	public static void updatePlanDeployingD2D(DeployTargetDetail target){
		int[] conditiion = new int[]{PolicyDeployStatus.ToBeDeployAsScheduled,PolicyDeployStatus.ToBeDeployed,PolicyDeployStatus.Deploying};
		updatePolicyHostMapStatusByCondition(PolicyDeployStatus.DeployingD2D,target, conditiion);
	}
	
	public static void updatePlanDeployD2DFailed(DeployTargetDetail target){
		int[] conditiion = new int[]{PolicyDeployStatus.DeployingD2D};
		List<EdgePolicyDeployTask> needChangeTasks = getTasksNeedUpdateStatus(target,conditiion);
		if(needChangeTasks == null)
			return;
		updatePolicyHostMapStatusByCondition(PolicyDeployStatus.DeployD2DFailed,needChangeTasks);
		for(EdgePolicyDeployTask task: needChangeTasks){
			String errorMessage = target.getProgressMessage();
			if(StringUtil.isEmptyOrNull(errorMessage)){
				errorMessage = target.getFinalDetailMessage();
			}
			List<Integer> vmIds = new ArrayList<Integer>();
			vmIds.add(task.getHostId());
			PolicyDeploymentTask deploymentTask = convertDaoTaskToContract(task, vmIds);
			writePolicyDeployErrorOrWarnning(deploymentTask, target.getServerName(), errorMessage);
			updatePlanStatus(task.getPolicyId(), task.getDeployFlags());
		}
	}
	
	public static void updatePlanDeployD2DSuccessed(DeployTargetDetail target){
		int[] conditiion = new int[]{PolicyDeployStatus.DeployingD2D};
		updatePolicyHostMapStatusByCondition(PolicyDeployStatus.DeployD2DSucceed,target, conditiion);
	}
	
	private static void updatePolicyHostMapStatusByCondition(int policyDeployStauts, DeployTargetDetail target, int[] condition){
		updatePolicyHostMapStatusByCondition(policyDeployStauts,getTasksNeedUpdateStatus(target, condition));
	}
	
	private static void updatePolicyHostMapStatusByCondition(int policyDeployStauts, List<EdgePolicyDeployTask> needChangeTasks){
		if(needChangeTasks == null)
			return;
		for (EdgePolicyDeployTask task : needChangeTasks) {
			List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.getHostPolicyMap(task.getHostId(), task.getPolicyType(), mapList );
			if(mapList.isEmpty())
				continue;
			EdgeHostPolicyMap map = mapList.get(0);
			int tryCount = map.getTryCount();
			if(policyDeployStauts == PolicyDeployStatus.DeployD2DFailed){
				tryCount+=1;
			}
			edgePolicyDao.setHostPolicyMap(map.getHostId(), map.getPolicyType(), map.getPolicyId(),
					policyDeployStauts, map.getDeployReason(), map.getDeployFlags(), tryCount, map.getLastSuccDeploy());
		}
	}
	
	private static List<EdgePolicyDeployTask> getTasksNeedUpdateStatus(DeployTargetDetail target, int[] condition){
		List<EdgePolicyDeployTask> needChangeTasks = new ArrayList<EdgePolicyDeployTask>();
		List<EdgePolicyDeployTask> daoTaskList = new ArrayList<EdgePolicyDeployTask>();
			
		//Agent deploy task
		edgePolicyDao.as_edge_policy_getDeployTaskByHostId(target.getNodeID(),daoTaskList);
		if(daoTaskList != null && daoTaskList.size() > 0){
			for (EdgePolicyDeployTask task : daoTaskList) {
				if(matchCondition(task.getDeployStatus(), condition)){
					needChangeTasks.add(task);
				}
			}
		}
			
		//HBBU deploy task
		daoTaskList.clear();
		edgePolicyDao.as_edge_policy_getDeployTaskByProxyId(target.getNodeID(),daoTaskList);
		if(daoTaskList != null && daoTaskList.size() > 0){
			for (EdgePolicyDeployTask task : daoTaskList) {
				if(matchCondition(task.getDeployStatus(), condition)){
					needChangeTasks.add(task);
				}
			}
		}
		
		//vsb deploy task
		daoTaskList.clear();
		edgePolicyDao.as_edge_policy_getDeployTaskByMonitorHostId(target.getNodeID(),daoTaskList);
		if(daoTaskList != null && daoTaskList.size() > 0){
			for (EdgePolicyDeployTask task : daoTaskList) {
				if(matchCondition(task.getDeployStatus(), condition)){
					needChangeTasks.add(task);
				}
			}
		}
		return needChangeTasks;
	}
	
	private static boolean matchCondition(int policyDeployStatus,int[] condition){
		if(condition.length == 0)
			return true;
		for (int i = 0; i < condition.length; i++) {
			if(policyDeployStatus==condition[i])
				return true;
		}
		return false;
	}
	
	private static void updatePlanStatus(int planId,int deployFlags) {
		int status = UnifiedDeployTaskRunner.getOverallDeployStatus(planId);
		if (status == 1) {	// failed
			if ((deployFlags & PolicyDeployFlags.DeletePlan) != 0) {
				edgePolicyDao.as_edge_policy_updateStatus(planId, PlanStatus.DeleteFailed);
			} else if ((deployFlags & PolicyDeployFlags.ModifyPlan) != 0) {
				edgePolicyDao.as_edge_policy_updateStatus(planId, PlanStatus.ModifyFailed);
			} else {
				edgePolicyDao.as_edge_policy_updateStatus(planId, PlanStatus.DeployFailed);
			}
		} 
	}
	
	private static PolicyDeploymentTask convertDaoTaskToContract(EdgePolicyDeployTask task , Object taskParameters){
		PolicyDeploymentTask deploymentTask = new PolicyDeploymentTask();
		deploymentTask.setContentFlag(task.getContentFlag());
		deploymentTask.setDeployFlags(task.getDeployFlags());
		deploymentTask.setDeployReason(task.getDeployReason());
		deploymentTask.setHostId(task.getHostId());
		deploymentTask.setPolicyId(task.getPolicyId());
		deploymentTask.setPolicyType(task.getPolicyType());
		deploymentTask.setProductType(task.getProductType());
		deploymentTask.setTaskParameters(taskParameters);
		return deploymentTask;
	}
	
	public static void writePolicyDeployErrorOrWarnning(PolicyDeploymentTask deploymentTask, String hostName,String errorMessage){
		if(Utils.hasHbbuBackupTask(deploymentTask.getContentFlag())){
			String messageTitle = EdgeCMWebServiceMessages.getResource("deployAgentToProxyFailed");
			String message = messageTitle+": "+errorMessage;
			PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog4VM(
							PolicyManagementServiceImpl.getTaskId(), deploymentTask, message);
			warningErrorMessageWriter.addErrorMessage4VM(deploymentTask, hostName, message);
		}else if(Utils.hasBackupTask(deploymentTask.getContentFlag())){
			String messageTitle = EdgeCMWebServiceMessages.getResource("deployAgentFaild");
			String message = messageTitle+": "+errorMessage;
			PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog( PolicyManagementServiceImpl.getTaskId(), deploymentTask, null,
					message);
			warningErrorMessageWriter.addErrorMessage(deploymentTask, hostName,message);
		}else if (Utils.hasLocalVSBTask(deploymentTask.getContentFlag())) {
			String messageTitle = EdgeCMWebServiceMessages.getResource("deployAgentToMonitorFaild");
			String message = messageTitle+": "+errorMessage;
			PolicyDeploymentLogWriter.getInstance().addDeploymentFailedLog( PolicyManagementServiceImpl.getTaskId(), deploymentTask, null,
					message);
			warningErrorMessageWriter.addErrorMessage(deploymentTask, hostName,message);
		}
	}
}
