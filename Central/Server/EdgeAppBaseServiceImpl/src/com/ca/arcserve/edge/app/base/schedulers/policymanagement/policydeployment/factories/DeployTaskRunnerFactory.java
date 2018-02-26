package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.factories;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.deploytaskrunner.UnifiedDeployTaskRunner;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces.IDeployTaskRunner;

public class DeployTaskRunnerFactory
{
	public static IDeployTaskRunner getTaskRunner()
	{
		return new UnifiedDeployTaskRunner();
	}
}
