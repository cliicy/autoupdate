package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyDeployTask;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class PolicyDeploymentScheduler extends TimerTask
{
	//////////////////////////////////////////////////////////////////////////

	@SuppressWarnings( "serial" )
	public class InitializationException extends Exception {}

	@SuppressWarnings( "serial" )
	public class NotInitializedException extends Exception {}

	@SuppressWarnings( "serial" )
	public class CollectTasksException extends Exception {}

	//////////////////////////////////////////////////////////////////////////

	private static final String REG_POLICYMAN_SUBKEY	= "PolicyManagement";
	private static final String REGVALUE_TRYCOUNTLIMIT	= "DeploymentTryCountLimit";
	private static final String REGVALUE_RETRYINTERVAL	= "DeploymentRetryInterval";
	private static final String REGVALUE_RESYNCINTERVAL	= "DeploymentResyncInterval";

	private static final int DEFAULT_TRYCOUNT		= 3;
	private static final int DEFAULT_RETRYINTERVAL	= 10 * 60 * 1000; // 10 minutes (in milliseconds)
	private static final int DEFAULT_RESYNCINTERVAL	= 7 * 24 * 60; // 7 days (in minutes)

	private static PolicyDeploymentScheduler instance = null;
	private static Logger logger = Logger.getLogger( PolicyDeploymentScheduler.class );

	private EdgeApplicationType edgeAppType;
	private List<Integer> policyTypes;
	private List<PolicyDeploymentTask> pendingTaskList;
	private IEdgePolicyDao edgePolicyDao;
	private int tryCountLimit;
	private int retryInterval;
	private int resyncInterval;
	private Thread doTaskThread;
	private Timer timer;
	private boolean isInited;
	private PolicyDeploymentTaskAssigner taskAssigner;

	//////////////////////////////////////////////////////////////////////////

	private PolicyDeploymentScheduler()
	{
		EdgeApplicationType edgeAppType = EdgeWebServiceContext.getApplicationType();

		this.edgeAppType		= edgeAppType;
		this.policyTypes		= getPolicyTypesByApplicationType( edgeAppType );
		this.edgePolicyDao		= null;
		this.pendingTaskList	= null;
		this.doTaskThread		= null;
		this.tryCountLimit		= DEFAULT_TRYCOUNT;
		this.retryInterval		= DEFAULT_RETRYINTERVAL;
		this.resyncInterval		= DEFAULT_RESYNCINTERVAL;
		this.timer				= null;
		this.isInited			= false;
	}

	//////////////////////////////////////////////////////////////////////////

	public synchronized void initializate()
	{
		try
		{
			this.edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
			loadRegistrySettings();

			taskAssigner = PolicyDeploymentTaskAssigner.getInstance( this, this.edgeAppType );
			this.doTaskThread = new Thread(
				taskAssigner,
				"PolicyDeployment.TaskAssignerThread" );
			this.doTaskThread.setDaemon(true);
			this.doTaskThread.start();

			this.timer = new Timer( "Policy Deployment Timer" );
			this.timer.schedule( this, this.retryInterval, this.retryInterval );

			this.isInited = true;
			//Because console will maintain node remote deploy status, and when the remote deploy finished, the plan status will
			//do corresponding change. so deleted the deployiing-d2d plan resume code
			doDeploymentNow( true , false);
		}
		catch (Exception e)
		{
			logger.error(
				"PolicyDeploymentScheduler(): Initialization failed.", e );
		}
	}
	
	public void destroy(){
		if(taskAssigner!=null)
			taskAssigner.destroy();
	}

	//////////////////////////////////////////////////////////////////////////

	public static PolicyDeploymentScheduler getInstance()
	{
		if (instance == null)
			instance = new PolicyDeploymentScheduler();

		return instance;
	}

	private List<Integer> getPolicyTypesByApplicationType( EdgeApplicationType appType )
	{
		List<Integer> policyTypeList = new ArrayList<Integer>();
		policyTypeList.add( PolicyTypes.BackupAndArchiving );
		policyTypeList.add( PolicyTypes.VMBackup );
		policyTypeList.add(PolicyTypes.VCM);
		policyTypeList.add(PolicyTypes.RemoteVCM);
		policyTypeList.add(PolicyTypes.Unified);
		return policyTypeList;
	}

	//////////////////////////////////////////////////////////////////////////
	// Load some settings from registry. These settings include:
	// - tryCountLimit
	// - retryInterval
	// - resyncInterval

	private void loadRegistrySettings()
	{
		String appRootKeyPath = CommonUtil.getAppRootKey( this.edgeAppType );
		if (!appRootKeyPath.endsWith( "\\" ))
			appRootKeyPath += "\\";

		String keyPath = appRootKeyPath + REG_POLICYMAN_SUBKEY;

		int value;

		value = getRegistryValueInt( keyPath, REGVALUE_TRYCOUNTLIMIT );
		if (value > 0)
			this.tryCountLimit = value;

		value = getRegistryValueInt( keyPath, REGVALUE_RETRYINTERVAL );
		if (value > 0)
			this.retryInterval = value * 1000; // the registry value is in seconds

		value = getRegistryValueInt( keyPath, REGVALUE_RESYNCINTERVAL );
		if (value > 0)
			this.resyncInterval = value;
	}

	//////////////////////////////////////////////////////////////////////////

	private int getRegistryValueInt( String keyPath, String valueName )
	{
		try
		{
			String valueString =
				CommonUtil.getApplicationExtentionKey( keyPath, valueName );

			return Integer.parseInt( valueString );
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// This method is for launch the deployment manually.
	// - collect tasks into pending task list
	// - notify the do-tasks thread to continue

	public synchronized void doDeploymentNow()
	{
		doDeploymentNow( false , true);
	}
	
	public synchronized void doDeploymentNowByPlanId(int planId){
		doDeploymentNowByEntityId(0,planId);
	}

	public synchronized void doDeploymentNowByHostId(int hostId){
		doDeploymentNowByEntityId(hostId, 0);
	}
	
	public synchronized void doDeploymentNowByHostIdAndPolicyId(int hostId, int policyId){
		doDeploymentNowByEntityId(hostId, policyId);
	}
	
	private synchronized void doDeploymentNowByEntityId(int hostId, int planId){
		if (!this.isInited){
			logger.error( "[PolicyDeployMentScheduler] doDeploymentNowByEntityId(): Not initialized." );
			return;
		}
		try{
			PolicyManagementServiceImpl.setNextTaskId();
			
			List<PolicyDeploymentTask> taskList = new ArrayList<PolicyDeploymentTask>();
			List<EdgePolicyDeployTask> daoTaskList = new ArrayList<EdgePolicyDeployTask>();
			this.edgePolicyDao.as_edge_policy_deployTasksBy_HostId_PolicyId(hostId, planId,tryCountLimit, daoTaskList);
			addTasksFromDaoTaskList( taskList, daoTaskList );
			mergeTasksIntoPendingTaskList( taskList );
			
			if ((this.pendingTaskList != null) && (this.pendingTaskList.size() > 0)){
				notifyAll();
			}
		}catch (Exception e){
			logger.error( "[PolicyDeployMentScheduler] doDeploymentNowByEntityId(): failed.", e );
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	
	private synchronized void doDeploymentNow( boolean includeRunningTasks, boolean includeFailedTask)
	{
		if (!this.isInited)
		{
			logger.error( "doDeploymentNow(): Not initialized." );
			return;
		}

		try
		{
			PolicyManagementServiceImpl.setNextTaskId();
			
			collectTasks( includeRunningTasks, includeFailedTask); 

			if ((this.pendingTaskList != null) && (this.pendingTaskList.size() > 0))
				notifyAll();
		}
		catch (Exception e)
		{
			logger.error( "doDeploymentNow(): failed.", e );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// - find tasks from host-policy map table
	// - merge new tasks into pending task list

	private synchronized void collectTasks( boolean includeRunningTasks, boolean includeFailedTasks) throws
		CollectTasksException
	{
		try
		{
			List<PolicyDeploymentTask> taskList = new ArrayList<PolicyDeploymentTask>();
			List<EdgePolicyDeployTask> daoTaskList;

			for (Integer policyType : this.policyTypes)
			{
				if (includeRunningTasks)
				{
					daoTaskList = new ArrayList<EdgePolicyDeployTask>();
					this.edgePolicyDao.getDeployTasks_Running(
						policyType, daoTaskList );
					addTasksFromDaoTaskList( taskList, daoTaskList );
				}
				
				if(includeFailedTasks){
					daoTaskList = new ArrayList<EdgePolicyDeployTask>();
					this.edgePolicyDao.getDeployTasks_Failed(
						policyType, this.tryCountLimit, this.retryInterval, daoTaskList );
					addTasksFromDaoTaskList( taskList, daoTaskList );
				}
	
				daoTaskList = new ArrayList<EdgePolicyDeployTask>();
				this.edgePolicyDao.getDeployTasks_Pending(
					policyType, this.retryInterval, daoTaskList );
				addTasksFromDaoTaskList( taskList, daoTaskList );
	
//				daoTaskList = new ArrayList<EdgePolicyDeployTask>();
//				this.edgePolicyDao.getDeployTasks_Resync(
//					policyType, this.resyncInterval, daoTaskList );
//				addTasksFromDaoTaskList( taskList, daoTaskList );
				
			}

			mergeTasksIntoPendingTaskList( taskList );
		}
		catch (Exception e)
		{
			logger.error( "collectTasks(): failed.", e );

			throw (CollectTasksException)
				new CollectTasksException().initCause( e );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Merge task from the new task list to the pending task list. Task with
	// same HostId and PolicyType will be replaced by the new task in the new
	// task list.

	private synchronized void mergeTasksIntoPendingTaskList(
		List<PolicyDeploymentTask> taskList )
	{
		if (taskList.size() == 0)
			return;

		if (this.pendingTaskList == null)
			this.pendingTaskList = new ArrayList<PolicyDeploymentTask>();

		for (PolicyDeploymentTask newTask : taskList)
		{
			PolicyDeploymentTask oldTask = findTaskInTaskList(
				this.pendingTaskList, newTask.getHostId(), newTask.getPolicyType() );
			if (oldTask != null)
				this.pendingTaskList.remove( oldTask );

			this.pendingTaskList.add( newTask );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	private PolicyDeploymentTask findTaskInTaskList(
		List<PolicyDeploymentTask> taskList, int hostId, int policyType )
	{
		for (PolicyDeploymentTask task : taskList)
		{
			if ((task.getHostId() == hostId) && (task.getPolicyType() == policyType))
				return task;
		}
		return null;
	}

	//////////////////////////////////////////////////////////////////////////
	
	private PolicyDeploymentTask convertDaoTaskToPolicyDeploymentTask(
		EdgePolicyDeployTask daoTask )
	{
		PolicyDeploymentTask task = new PolicyDeploymentTask();
		task.setHostId( daoTask.getHostId() );
		task.setPolicyType( daoTask.getPolicyType() );
		task.setPolicyId( daoTask.getPolicyId() );
		task.setDeployReason( daoTask.getDeployReason() );
		task.setDeployFlags( daoTask.getDeployFlags() );
		task.setTaskParameters( null );
		task.setProductType(daoTask.getProductType());
		task.setRpsTask(daoTask.isRpsTask());
		task.setContentFlag(daoTask.getContentFlag());
		return task;
	}

	//////////////////////////////////////////////////////////////////////////

	private void addTasksFromDaoTaskList(
		List<PolicyDeploymentTask> taskList, List<EdgePolicyDeployTask> daoTaskList )
	{
		assert taskList != null;
		if (taskList == null)
			return;

		assert daoTaskList != null;
		if (daoTaskList == null)
			return;

		for (EdgePolicyDeployTask daoTask : daoTaskList)
		{
			PolicyDeploymentTask task =
				convertDaoTaskToPolicyDeploymentTask( daoTask );

			taskList.add( task );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public synchronized List<PolicyDeploymentTask> getPendingTaskList()
	{
		return pendingTaskList;
	}

	//////////////////////////////////////////////////////////////////////////

	public synchronized void setPendingTaskList(
		List<PolicyDeploymentTask> pendingTaskList )
	{
		this.pendingTaskList = pendingTaskList;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void run()
	{
		doDeploymentNow();
	}
}
