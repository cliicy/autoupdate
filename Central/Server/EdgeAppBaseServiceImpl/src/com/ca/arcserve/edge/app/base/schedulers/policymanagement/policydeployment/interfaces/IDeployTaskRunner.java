package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;

public interface IDeployTaskRunner
{
	void doTask( PolicyDeploymentTask task );
}
