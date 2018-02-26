package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.taskpreprocessors.UnifiedTaskPreprocessor;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;

public class PolicyDeploymentTaskAssigner implements Runnable
{
	private static PolicyDeploymentTaskAssigner instance;
	private static Logger logger = Logger.getLogger( PolicyDeploymentTaskAssigner.class );
	
	private PolicyDeploymentScheduler scheduler;
	private EdgeApplicationType edgeAppType;
	private List<PolicyDeploymentTask> workingTaskList;
	private List<PolicyDeploymentTask> allTaskList;
	private ThreadPoolExecutor taskRunnerExecutor;
	
	//////////////////////////////////////////////////////////////////////////
	
	private PolicyDeploymentTaskAssigner(
		PolicyDeploymentScheduler scheduler, EdgeApplicationType edgeAppType )
	{
		this.scheduler = scheduler;
		this.edgeAppType = edgeAppType;
		///fanda03 fix too many threads
		this.taskRunnerExecutor = new ThreadPoolExecutor(
			10, 20, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>() , new NamingThreadFactory("PolicyDeploymentTaskAssigner"));
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static PolicyDeploymentTaskAssigner getInstance(
		PolicyDeploymentScheduler scheduler, EdgeApplicationType edgeAppType )
	{
		if (instance == null)
			instance = new PolicyDeploymentTaskAssigner( scheduler, edgeAppType );
		
		return instance;
	}
	
	public void destroy(){
		taskRunnerExecutor.shutdownNow();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void run()
	{
		logger.info(
			"Thread for assigning policy deployment tasks started." );
		
		for (;;)
		{
			// wait for pending tasks
			
			try
			{
				synchronized(this.scheduler) {
					while (this.scheduler.getPendingTaskList() == null)
						this.scheduler.wait();
					
					logger.info(
						"Task assigner thread wake up, has tasks to do." );
					synchronized( this )
					{
						this.workingTaskList = this.scheduler.getPendingTaskList();
						this.scheduler.setPendingTaskList( null );
					}
				}					
			}
			catch (InterruptedException e)
			{
				logger.info(
					"Thread for assigning policy deployment tasks interrupted." );
				return;
			}
			catch (Exception e)
			{
				logger.error(
					"Waiting for pending policy deployment tasks failed.",
					e );
				continue;
			}
			
			try
			{
				logger.info( "Begin assigning policy deployment tasks." );
				
				// clear caches
				
				PolicyContentCache.getInstance().clear();
				HostInfoCache.getInstance().clear();
				
				// distinguish different APP type
				List<PolicyDeploymentTask> cpmNodeTaskList = new ArrayList<PolicyDeploymentTask>();
				List<PolicyDeploymentTask> hbbuNodeTaskList = new ArrayList<PolicyDeploymentTask>();
				List<PolicyDeploymentTask> vcmNodeTaskList = new ArrayList<PolicyDeploymentTask>();
				List<PolicyDeploymentTask> unifiedNodeTaskList = new ArrayList<PolicyDeploymentTask>();
				for(PolicyDeploymentTask task : this.workingTaskList) {
					if (this.edgeAppType == EdgeApplicationType.CentralManagement) {
						if (task.getPolicyType() == PolicyTypes.VMBackup) {
							hbbuNodeTaskList.add(task);					
						} else if (task.getPolicyType() == PolicyTypes.Unified){
							unifiedNodeTaskList.add(task);
						} else if (task.getPolicyType() == PolicyTypes.VCM || task.getPolicyType() == PolicyTypes.RemoteVCM){
							vcmNodeTaskList.add(task);
						} else {
							cpmNodeTaskList.add(task);
						}
					} else if (this.edgeAppType == EdgeApplicationType.vShpereManager) {
						hbbuNodeTaskList.add(task);
					} else {
						vcmNodeTaskList.add(task);
					}
				}
				
				// pre-process tasks
				
				allTaskList = new ArrayList<PolicyDeploymentTask>();
				if (unifiedNodeTaskList.size() > 0) {
					UnifiedTaskPreprocessor.getInstance().process(unifiedNodeTaskList);
					allTaskList.addAll(unifiedNodeTaskList);
				}

				// assign tasks
				
				for (PolicyDeploymentTask task : allTaskList)
				{
					try {
						this.taskRunnerExecutor.execute(
							new PolicyDeploymentTaskRunner( task, this ) );
					} catch (RejectedExecutionException e) {
						logger.info("PolicyDeploymentTaskAssigner is already shutdown");
						return;
					}
				}
				
				logger.info("Complete assigning policy deployment tasks, wait for tasks completed." );
				
				// wait for all tasks completed
				
				try
				{
					synchronized( this )
					{
						while (this.allTaskList.size() > 0)
							this.wait();
						
						logger.info( "All tasks completed." );
	
						this.allTaskList = null;
					}
				}
				catch (InterruptedException e)
				{
					logger.info(
						"Thread for assigning policy deployment tasks interrupted." );
					return;
				}
				catch (Exception e)
				{
					logger.error(
						"Waiting for all policy deployment tasks to complete failed.",
						e );
					throw e;
				}
			
			}
			catch (Exception e)
			{
				this.allTaskList = null;
				logger.error(
					"Assigning policy deployment tasks failed, some tasks will be ignored.",
					e );
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	public void onTaskComplete( PolicyDeploymentTask task )
	{
		synchronized( this )
		{
			this.allTaskList.remove( task );
			this.notifyAll();
		}
	}

}
