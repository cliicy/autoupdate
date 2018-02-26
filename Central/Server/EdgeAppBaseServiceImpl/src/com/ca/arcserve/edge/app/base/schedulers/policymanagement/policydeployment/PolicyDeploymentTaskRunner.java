package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.factories.DeployTaskRunnerFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces.IDeployTaskRunner;

public class PolicyDeploymentTaskRunner implements Runnable
{
	private PolicyDeploymentTaskAssigner taskAssigner;
	private PolicyDeploymentTask task;
	private static Logger logger = Logger.getLogger( PolicyDeploymentTaskRunner.class );
	//////////////////////////////////////////////////////////////////////////
	
	public PolicyDeploymentTaskRunner(
		PolicyDeploymentTask task, PolicyDeploymentTaskAssigner taskAssigner )
	{
		this.taskAssigner = taskAssigner;
		this.task = task;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public PolicyDeploymentTask getTask()
	{
		return task;
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void run()
	{
		try{
			IDeployTaskRunner taskRunner = DeployTaskRunnerFactory.getTaskRunner();
			taskRunner.doTask( task );
		} catch(Throwable e) {
			logger.error("Policy task is failed! " + task.toString(), e);
		} finally {
			this.taskAssigner.onTaskComplete( this.task );
		}
	}
}
