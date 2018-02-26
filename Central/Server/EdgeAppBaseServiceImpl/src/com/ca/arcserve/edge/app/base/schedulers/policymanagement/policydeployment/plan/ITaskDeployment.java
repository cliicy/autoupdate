package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public interface ITaskDeployment {
	
	/**
	 * Deploy the encapsulated task, update the deploy status and handle all errors/warnings.
	 * @return <code>true</code> if deploy task successful. <code>false</code>, otherwise.
	 */
	boolean deployTask(PolicyDeploymentTask argument, UnifiedPolicy plan, boolean updateDeployStatusOnSuccess);
	
	/**
	 * remove the encapsulated task, update the deploy status and handle all errors/warnings.
	 * @return <code>true</code> if remove task successful. <code>false</code>, otherwise.
	 */
	boolean removeTask(PolicyDeploymentTask argument, boolean updateDeployStatusOnSuccess);

}
