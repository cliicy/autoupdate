package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces;

import java.util.List;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;

public interface ITaskPreprocessor
{
	void process( List<PolicyDeploymentTask> taskList );
}
