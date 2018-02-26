package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.taskpreprocessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.AgentInstallationInPlanHelper;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces.ITaskPreprocessor;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;

public class UnifiedTaskPreprocessor implements ITaskPreprocessor {
	
	private static UnifiedTaskPreprocessor instance = null;
	
	private UnifiedTaskPreprocessor(){
		
	}

	public static UnifiedTaskPreprocessor getInstance(){
		if(instance == null)
			instance = new UnifiedTaskPreprocessor();
		return instance;
	}
	
	@Override
	public void process(List<PolicyDeploymentTask> taskList) {
		linuxD2DpreProcess(taskList);
		windowsD2DpreProcess(taskList);
		windowsVMpreProcess(taskList);
		//vsbPreProcess(taskList);
	}
	
	private List<PolicyDeploymentTask> getDeploymentTasksByType(List<PolicyDeploymentTask> allTasks, PlanTaskType type) {
		List<PolicyDeploymentTask> tasks = new ArrayList<PolicyDeploymentTask>();
		
		for (PolicyDeploymentTask task : allTasks) {
			if (Utils.hasBit(task.getContentFlag(), type)){
				tasks.add(task);
			}
		}
		
		return tasks;
	}
	
	private String getTaskMergeKey(PolicyDeploymentTask task) {
		return task.getPolicyId() + "-" + task.getDeployReason() + "-" + task.getDeployFlags();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, PolicyDeploymentTask> mergeTasks(List<PolicyDeploymentTask> tasks) {
		Map<String, PolicyDeploymentTask> mergedTasks = new HashMap<String, PolicyDeploymentTask>();
		
		for (PolicyDeploymentTask task : tasks) {
			String taskMergeKey = getTaskMergeKey(task);
			if (mergedTasks.containsKey(taskMergeKey)) {
				((List<Integer>) mergedTasks.get(taskMergeKey).getTaskParameters()).add(task.getHostId());
			} else {
				List<Integer> mergedHostIds = new ArrayList<Integer>();
				mergedHostIds.add(task.getHostId());
				task.setTaskParameters(mergedHostIds);
				mergedTasks.put(taskMergeKey, task);
			}
		}
		
		return mergedTasks;
	}

	private void linuxD2DpreProcess(List<PolicyDeploymentTask> taskList) {
		List<PolicyDeploymentTask> targetTasks = getDeploymentTasksByType(taskList, PlanTaskType.LinuxBackup);
		
		Map<String, PolicyDeploymentTask> mergedTasks = mergeTasks(targetTasks);
		
		taskList.removeAll(targetTasks);
		taskList.addAll(mergedTasks.values());
	}
	
	private void windowsD2DpreProcess(List<PolicyDeploymentTask> taskList) {
		List<PolicyDeploymentTask> windowsD2DTasks = getDeploymentTasksByType(taskList, PlanTaskType.WindowsD2DBackup);
		
		AgentInstallationInPlanHelper agentInstall = new AgentInstallationInPlanHelper();
		for (PolicyDeploymentTask task : windowsD2DTasks) {
			if (task.getDeployReason() != PolicyDeployReasons.PolicyUnassigned && agentInstall.needInstallAgentForSource(task)) {
				taskList.remove(task);
			}
		}
	}
	
	private void windowsVMpreProcess(List<PolicyDeploymentTask> taskList) {
		List<PolicyDeploymentTask> targetTasks = getDeploymentTasksByType(taskList, PlanTaskType.WindowsVMBackup);
		
		Map<Integer, List<PolicyDeploymentTask>> vmTaskMap = mergeTaskListByPolicyId(targetTasks);
		
		AgentInstallationInPlanHelper agentInstallForProxy = new AgentInstallationInPlanHelper();
		for (Integer policyId : vmTaskMap.keySet()) {
			List<PolicyDeploymentTask> policyTasks = vmTaskMap.get(policyId);
			if(policyTasks==null || policyTasks.isEmpty())
				return;
			if (policyTasks.get(0).getDeployReason() != PolicyDeployReasons.PolicyUnassigned && agentInstallForProxy.needInstallAgentForProxy(policyId, policyTasks)) {
				taskList.removeAll(policyTasks);
				targetTasks.removeAll(policyTasks);
			}
		}
		
		Map<String, PolicyDeploymentTask> mergedTasks = mergeTasks(targetTasks);
		
		taskList.removeAll(targetTasks);
		taskList.addAll(mergedTasks.values());
	}
	
	private void vsbPreProcess(List<PolicyDeploymentTask> taskList) {
		List<PolicyDeploymentTask> targetTasks = getDeploymentTasksByType(taskList, PlanTaskType.LocalConversion);
		AgentInstallationInPlanHelper agentInstallForMonitor = new AgentInstallationInPlanHelper();
		for (PolicyDeploymentTask task : targetTasks) {
			if (task.getDeployReason() != PolicyDeployReasons.PolicyUnassigned && agentInstallForMonitor.needInstallAgentForMonitor(task)) {
				taskList.remove(task);
			}
		}
	}
	
	private Map<Integer,List<PolicyDeploymentTask>> mergeTaskListByPolicyId(List<PolicyDeploymentTask> taskListNeedMerge){
		Map<Integer,List<PolicyDeploymentTask>> taskMap = new HashMap<Integer,List<PolicyDeploymentTask>>();
		for(PolicyDeploymentTask task:taskListNeedMerge){
			if(taskMap.get(task.getPolicyId()) == null){
				List<PolicyDeploymentTask> tempTaskList = new ArrayList<PolicyDeploymentTask>();
				tempTaskList.add(task);
				taskMap.put(task.getPolicyId(),tempTaskList);
			}
			else {
				List<PolicyDeploymentTask> tempList = taskMap.get(task.getPolicyId());
				tempList.add(task);
			}
		}
		return taskMap;
	}
}
