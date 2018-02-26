package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.deploytaskrunner;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.ITaskDeployment;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.RpsSettingTaskDeployment;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.TaskDeploymentFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.VSphereBackupTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.asbuintegration.ASBUDestinationManager;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.LinuxNodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class UnifiedDeployTaskRunner extends AbstractDeployTaskRunner {
	
	private static Logger logger = Logger.getLogger(UnifiedDeployTaskRunner.class);
	private static IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IActivityLogService activityLogService = null;
	private long edgeTaskId = PolicyManagementServiceImpl.getTaskId();
	
	@Override
	protected void deployPolicy(PolicyDeploymentTask task)
	{
		logger.info("UnifiedDeployTaskRunner.deployPolicy(): Enter. " + task);
		
		UnifiedPolicy plan;
		logger.debug("UnifiedDeployTaskRunner.deployPolicy(): load plan details.");
		
		try {
			plan = policyManagementServiceImpl.loadUnifiedPolicyById(task.getPolicyId());
		} catch (EdgeServiceFault e) {
			logger.error("UnifiedDeployTaskRunner.deployPolicy(): cannot load plan information by id.", e);
			return;
		}
		logger.info( "UnifiedDeployTaskRunner.deployPolicy(): " + plan );

		List<ITaskDeployment> deployments = TaskDeploymentFactory.create(task, plan);
		boolean hasTaskToDeploy = deployments != null && deployments.size() > 0;
		
		removeUnusedTasks(task, !hasTaskToDeploy);
		
		logger.info("UnifiedDeployTaskRunner.deployPolicy(): Begin to deploy tasks. Deployer count: " + deployments.size() );
		for (int i = 0; i < deployments.size(); ++i)
		{
			String deployerInfo = deployments.get(i).getClass().getSimpleName();
			logger.info("UnifiedDeployTaskRunner.deployPolicy(): Index: " + i + ", Deployer: " + deployerInfo );
			
			if (!deployments.get(i).deployTask(task, plan, i == deployments.size() - 1))
			{
				if(deployments.get(i) instanceof VSphereBackupTaskDeployment){
					//when vm have hbbu task + vsb task, then not to block vsb task deployment
					logger.error("UnifiedDeployTaskRunner.deployPolicy(): vsphere backup deploy have errors.");
					continue;
				}else {
					logger.error("UnifiedDeployTaskRunner.deployPolicy(): Deploying task failed.");
					break;
				}
			}
			logger.info("UnifiedDeployTaskRunner.deployPolicy(): Deploying task succeed.");
		}
		logger.info("UnifiedDeployTaskRunner.deployPolicy(): Deploying all tasks finished." );
		
		updatePlanStatus(task, plan);
		
		logger.info("UnifiedDeployTaskRunner.deployPolicy(): Leave.");
	}
	
	private boolean removeUnusedTasks(PolicyDeploymentTask task, boolean updateDeployStatusOnSuccess) {
		
		logger.info( "removeUnusedTasks(): Begin to remove unused tasks." );
		
		List<ITaskDeployment> deployerList = TaskDeploymentFactory.createUnusedTasks(task);
		int deployerSize = deployerList.size();
		logger.info( "removeUnusedTasks(): Deployer count: " + deployerSize );
		
		for (int i = 0; i < deployerSize; i++) {
			ITaskDeployment deployment = deployerList.get(i);
			logger.info("UnifiedDeployTaskRunner.removeUnusedTasks - remove unused task ["
					+ deployment.getClass().getSimpleName() + "], " + task);
			if (!deployment.removeTask(task, updateDeployStatusOnSuccess && i == deployerSize - 1)) {
				logger.warn("UnifiedDeployTaskRunner.removeUnusedTasks - remove unused task ["
						+ deployment.getClass().getSimpleName() + "] failed, " + task);
				return false;
			}
			logger.info("UnifiedDeployTaskRunner.removeUnusedTasks - remove unused task ["
					+ deployment.getClass().getSimpleName() + "] succeed, " + task);
		}
		
		logger.info( "removeUnusedTasks(): Removing unused tasks finished." );

		return true;
	}
	
	@Override
	protected void removePolicy(PolicyDeploymentTask task) {
		logger.debug("UnifiedDeployTaskRunner.removePolicy - Enter, " + task);
		
		UnifiedPolicy plan;
		
		try {
			plan = policyManagementServiceImpl.loadUnifiedPolicyById(task.getPolicyId());
		} catch (EdgeServiceFault e) {
			logger.error("UnifiedDeployTaskRunner.removePolicy - cannot load plan information by id.", e);
			return;
		}
		
		if (isModify(task)) {
			removeUnusedTasks(task, true);
		} else {			
			List<ITaskDeployment> deployments = TaskDeploymentFactory.create(task, plan);
			for (int i = 0; i < deployments.size(); ++i) {
				if (!deployments.get(i).removeTask(task, i == deployments.size() - 1)) {
					break;
				}
			}
			if ((task.getDeployFlags() & PolicyDeployFlags.UnregisterNodeAfterUnassign) != 0){
				if(Utils.hasBit(task.getContentFlag(), PlanTaskType.LinuxBackup)){
					unRegisterLinuxD2D(task);
				}else{
					unregisterD2D(task);
				}
			}
		}
		if(Utils.hasBit(task.getContentFlag(), PlanTaskType.LinuxBackup)){
			@SuppressWarnings("unchecked")
			List<Integer> idList = (List<Integer>)task.getTaskParameters();
			for(Integer id : idList){
				edgePolicyDao.deleteHostPolicyMap(id, task.getPolicyType());
			}
		}else{
			edgePolicyDao.deleteHostPolicyMap(task.getHostId(), task.getPolicyType());
		}
		
		updatePlanStatus(task, plan);
		
		logger.debug("UnifiedDeployTaskRunner.removePolicy - Leave, " + task);
	}
	
	private void unregisterD2D(PolicyDeploymentTask task){
		try
		{
			NodeServiceImpl nodeServiceImpl = new NodeServiceImpl();
			this.edgePolicyDao.deleteHostPolicyMap( task.getHostId(), task.getPolicyType() );
			nodeServiceImpl.unregisterD2D( task.getHostId() );

			// Need to remove records here , since deleteNode method just set to invisible  
			this.edgeHostMgrDao.as_edge_host_remove(task.getHostId());
			logger.info("UnifiedDeployTaskRunner.unregisterD2D(): delete node, nodeId:" + task.getHostId());
		}
		catch (Exception e)
		{
			logger.warn("removePolicy(): Deleting host policy map and unregister " +
				"D2D after unassign policy failed." );
			
			// Ignore this exception, and D2D will connect Edge when
			// service starts and before job run, and clear the date
			// save on D2D.
		}
		
	}
	
	private void unRegisterLinuxD2D(PolicyDeploymentTask task){
		LinuxNodeServiceImpl impl = new LinuxNodeServiceImpl();
		impl.unRegisterLinuxD2DForTask(task);
		
	}
	
	private void updatePlanStatus(PolicyDeploymentTask task, UnifiedPolicy plan) {
		
		logger.info( "updatePlanStatus(): Begin to update plan deployment status." );
		
		int status = getOverallDeployStatus(task.getPolicyId());
		String msg = "";
		
		if (status == 1) {	// failed
			String msgid;
			if (isDelete(task)) {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.DeleteFailed);
				msgid="deletePlanFailed";
			} else if (isModify(task)) {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.ModifyFailed);
				msgid="modifyPlanFailed";
			} else if(isRedeploy(task)){
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.DeployFailed);
				msgid="redeployPlanFailed";
			} else {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.DeployFailed);
				msgid="createPlanFailed";
			}
			msg = EdgeCMWebServiceMessages.getMessage( msgid, plan.getName() );
			writePlanActivityLog(Severity.Error, 0, msg);
		} else if (status == 2) {	// success
			String msgid;
			if (isDelete(task)) {
				deletePlan(task, plan);
				msgid="deletePlanSuccessful";
			} else if (isModify(task)) {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.ModifySucess);
				msgid="modifyPlanSuccessful";
			} else if (isRedeploy(task)) {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.DeploySuccess);
				msgid="redeployPlanSuccessful";
			} else {
				policyDao.as_edge_policy_updateStatus(task.getPolicyId(), PlanStatus.DeploySuccess);
				msgid="createPlanSuccessful";
			}
			msg = EdgeCMWebServiceMessages.getMessage( msgid, plan.getName() ); 
			writePlanActivityLog(Severity.Information, 0, msg);
		}
		logger.info( "updatePlanStatus(): Updating plan deployment status finished. Status: " + status + ", Message: " + msg );
	}
	
	public static synchronized int getOverallDeployStatus(int policyId) {
		int[] status = new int[1];
		policyDao.as_edge_policy_getOverallDeployStatus(policyId, status);
		return status[0];
	}
	
	private boolean isModify(PolicyDeploymentTask task) {
		return (task.getDeployFlags() & PolicyDeployFlags.ModifyPlan) != 0;
	}

	private boolean isRedeploy(PolicyDeploymentTask task) {
		return (task.getDeployFlags() & PolicyDeployFlags.RedeployPlan) != 0 
				|| task.getDeployReason() == PolicyDeployReasons.EnablePlan
				|| task.getDeployReason() == PolicyDeployReasons.DisablePlan;
	}
	
	private boolean isDelete(PolicyDeploymentTask task) {
		return (task.getDeployFlags() & PolicyDeployFlags.DeletePlan) != 0;
	}

	private void deletePlan(PolicyDeploymentTask argument, UnifiedPolicy plan) {
		RpsSettingTaskDeployment rpsPolicyTaskDeployment = new RpsSettingTaskDeployment();
		
		try {
			rpsPolicyTaskDeployment.deleteRpsPolicySettings(plan);
			ASBUDestinationManager.getInstance().deleteASBUSettings(plan);
			policyDao.as_edge_policy_remove(argument.getPolicyId());
		} catch (EdgeServiceFault e) {
			policyDao.as_edge_policy_updateStatus(argument.getPolicyId(), PlanStatus.DeleteFailed);
		}
	}
	

	private void writePlanActivityLog(
			Severity severity, int nodeid, String message )
	{
		try{
			LogAddEntity log = new LogAddEntity();
			log.setJobId( edgeTaskId );
			log.setTargetHostId(nodeid);
			log.setSeverity( severity );
			log.setMessage(message);
			this.getActivityLogService().addUnifiedLog(log);
		}
		catch (Exception e)
		{
			logger.error( "writePlanActivityLog(): Error writting activity log. (Node id: '" +
					nodeid + "', Message: '" + message + "')", e );
		}
	}
	
	private IActivityLogService getActivityLogService()
	{
		if (this.activityLogService == null)
			this.activityLogService = new ActivityLogServiceImpl();

		return this.activityLogService;
	}
}
