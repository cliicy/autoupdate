package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public class TaskDeploymentFactory {
	
	public static List<ITaskDeployment> create(PolicyDeploymentTask task, UnifiedPolicy plan) {
		List<ITaskDeployment> deployments = new ArrayList<ITaskDeployment>();
		
		if (task.getDeployReason() == PolicyDeployReasons.PolicyUnassigned && plan.getConversionConfiguration() != null) {
			deployments.add(new VSBTaskDeployment());
		}
		
		if (plan.getVSphereBackupConfiguration() != null) {
			deployments.add(new VSphereBackupTaskDeployment());
		} else if (plan.getLinuxBackupsetting() != null) {
			deployments.add(new LinuxBackupTaskDeployment());
		} else if (plan.getBackupConfiguration() != null) {
			deployments.add(new D2DBackupTaskDeployment());
		}
		
		if (task.getDeployReason() != PolicyDeployReasons.PolicyUnassigned && plan.getConversionConfiguration() != null) {
			deployments.add(new VSBTaskDeployment());
		}
		
		return deployments;
	}
	
	public static List<ITaskDeployment> createUnusedTasks(PolicyDeploymentTask task) {
		List<ITaskDeployment> deployments = new ArrayList<ITaskDeployment>();
		
		if ((task.getDeployFlags() & PolicyDeployFlags.ConversionTaskDeleted) != 0) {
			deployments.add(new VSBTaskDeployment());
		}
		
		if ((task.getDeployFlags() & PolicyDeployFlags.BackupTaskDeleted) != 0) {
			if(Utils.hasBit(task.getContentFlag(), PlanTaskType.LinuxBackup)){
				deployments.add(new LinuxBackupTaskDeployment());
			}else{
				deployments.add(new D2DBackupTaskDeployment());
			}
		}
		
		if ((task.getDeployFlags() & PolicyDeployFlags.VMBackupTaskDeleted) != 0
				&& Utils.hasBit(task.getContentFlag(), PlanTaskType.WindowsVMBackup)) {
			deployments.add(new VSphereBackupTaskDeployment());
		}
		
		return deployments;
	}

}