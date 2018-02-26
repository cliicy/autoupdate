package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.data.backup.BackupStatus;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacade.RHAScenarioState;
import com.ca.arcflash.webservice.replication.BaseReplicationCommand;
import com.ca.arcflash.webservice.replication.ManualConversionUtility;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;

public class RemoteVCMSessionMonitor implements Runnable
{
	private static class NodeInfo
	{
		private String uuid = null;
		private String sessionFolder = null;
		private Date lastSessionTime = null;
		private boolean hasPendingJobs = false;
		
		public String getUuid()
		{
			return uuid;
		}
		public void setUuid( String uuid )
		{
			this.uuid = uuid;
		}
		
		public String getSessionFolder()
		{
			return sessionFolder;
		}
		
		public void setSessionFolder( String sessionFolder )
		{
			this.sessionFolder = sessionFolder;
		}
		
		public Date getLastSessionTime()
		{
			return lastSessionTime;
		}
		
		public void setLastSessionTime( Date lastSessionTime )
		{
			this.lastSessionTime = lastSessionTime;
		}
		
		public boolean isHasPendingJobs()
		{
			return hasPendingJobs;
		}
		
		public void setHasPendingJobs( boolean hasPendingJobs )
		{
			this.hasPendingJobs = hasPendingJobs;
		}
	}
	
	private static class RHAScenarioInfo
	{
		private String rootPath = null;
		private RHAScenarioState state = RHAScenarioState.Unknown;
		private List<NodeInfo> managedNodes = new LinkedList<NodeInfo>();
		
		public String getRootPath()
		{
			return rootPath;
		}
		
		public void setRootPath( String rootPath )
		{
			this.rootPath = rootPath;
		}
		
		public RHAScenarioState getState()
		{
			return state;
		}
		
		public void setState( RHAScenarioState state )
		{
			this.state = state;
		}

		public List<NodeInfo> getManagedNodes()
		{
			return managedNodes;
		}
	}
	
	private static final int DEFAULT_POLLINGINTERVAL = 3 * 60; // 3 minutes (in seconds)
	private static final int MIN_HEARTBEATLOG_INTERVAL = 10 * 60; // 10 minutes
	
	private static RemoteVCMSessionMonitor instance = null;
	private static Logger logger = Logger.getLogger( RemoteVCMSessionMonitor.class );
	
	private NativeFacade nativeFacade;
	private List<NodeInfo> nodeList;
	private List<RHAScenarioInfo> rhaScenarioList;
	private int pollingInterval;
	private ScheduledExecutorService scheduler;
	private long lastHeartbeatLogTime;
	
	private RemoteVCMSessionMonitor()
	{
		this.nativeFacade = BackupService.getInstance().getNativeFacade();
		this.nodeList = new LinkedList<NodeInfo>();
		this.rhaScenarioList = new LinkedList<RHAScenarioInfo>();
		this.pollingInterval = DEFAULT_POLLINGINTERVAL;
		this.scheduler = null;
		this.lastHeartbeatLogTime = 0;
	}
	
	public static RemoteVCMSessionMonitor getInstance()
	{
		if (instance == null)
			instance = new RemoteVCMSessionMonitor();
		
		return instance;
	}
	
	public void Initialize()
	{
		try
		{
			logger.info( "RemoteVCMSessionMonitor: Initialize() begin." );
			
			getInfoOfMonitoredNodes();
			initRHAScenarioStates();
			
			this.scheduler = Executors.newScheduledThreadPool( 1 );
			this.scheduler.scheduleAtFixedRate( this, 1, this.pollingInterval, TimeUnit.SECONDS );
			
			logger.info( "RemoteVCMSessionMonitor: Initialize() completed." );
		}
		catch (Exception e)
		{
			logger.error( "RemoteVCMSessionMonitor: Initialize() failed.", e );
		}
	}
	
	public void shutDown(){
		this.scheduler.shutdownNow();
	}

	@Override
	public void run()
	{
		printHeartbeatLog();
		
		synchronized( this.rhaScenarioList )
		{
			for (RHAScenarioInfo scenarioInfo : this.rhaScenarioList)
			{
				try
				{
					RHAScenarioState state =
						this.nativeFacade.getRHAScenarioState( scenarioInfo.getRootPath() );
					if (state != scenarioInfo.getState())
					{
						scenarioInfo.setState( state );
						logger.info( "State of RHA scenario with root path \'" +
							scenarioInfo.getRootPath() + "\' has changed. New state: " + state );
						addActivityLogForRHAScenarioState( scenarioInfo, state );
					}
				}
				catch (Exception e)
				{
					logger.error( "Error getting state of RHA scenario with root path \'" +
						scenarioInfo.getRootPath() + "\'.", e );
				}
			}
		}

		synchronized( this.nodeList )
		{
			for (NodeInfo nodeInfo : this.nodeList)
			{
				RHAScenarioState scenarioState = getLastRHAScenarioStateByNode( nodeInfo );
				if ((scenarioState == RHAScenarioState.Sync) || (scenarioState == RHAScenarioState.Unknown))
					continue;
				
				Date newLastSessionTime = getLastSessionTime( nodeInfo.getSessionFolder() );
				if (newLastSessionTime == null)
					continue;
				
				Date oldLastSessionTime = nodeInfo.getLastSessionTime();
				nodeInfo.setLastSessionTime( newLastSessionTime );
				
				if ((oldLastSessionTime == null) ||
					(newLastSessionTime.compareTo( oldLastSessionTime ) > 0))
				{
					logger.info( "New session detected. Node uuid: " + nodeInfo.getUuid() +
						", Backup time: " + newLastSessionTime );
					launchConversion( nodeInfo );
				}
				else if (nodeInfo.isHasPendingJobs())
				{
					if (!canStartConversionJob(nodeInfo.getUuid())) {
						if (logger.isInfoEnabled()) {
							logger.info("The conversion job can't running at this time for node:" + nodeInfo.getUuid()
									+ " Maybe another job is running or the job is paused.");
						}
						continue;
					}
					logger.info( "Has pending jobs. Node uuid: " + nodeInfo.getUuid() );
					launchConversion( nodeInfo );
				}
			}
		}
	}
	
	private boolean canStartConversionJob(String uuid) {
		ReplicationJobScript replicationJobScript = HAService.getInstance().getReplicationJobScript(uuid);
		return (replicationJobScript.getAutoReplicate() && !replicationJobScript.getIsPlanPaused() && !isConversionJobRunning(uuid));
	}
	
	private void printHeartbeatLog()
	{
		long currentTime = this.nativeFacade.getTickCount();
		if ((this.lastHeartbeatLogTime == 0) ||
			(currentTime - this.lastHeartbeatLogTime > MIN_HEARTBEATLOG_INTERVAL * 1000))
		{
			logger.debug( "RemoteVCMSessionMonitor.Run() runs well." );
			this.lastHeartbeatLogTime = currentTime;
		}
	}
	
	private void launchConversion( NodeInfo nodeInfo )
	{
		String uuid = nodeInfo.getUuid();
		logger.info( "Try to start conversion. Node uuid: " + uuid );

		try
		{
			nodeInfo.setHasPendingJobs( false );
			HAService.getInstance().startReplication( uuid );
			
			logger.info( "New job scheduled. Node uuid: " + uuid );
		}
		catch (Exception e)
		{
			logger.error( "Error launching conversion job.", e );
		}
	}
	
	private boolean isConversionJobRunning( String uuid )
	{
		Lock jobLock = CommonService.getInstance().getRepJobLock( uuid );
		if (!jobLock.tryLock())
			return true;

		jobLock.unlock();
		return false;
	}
	
	// This will be called by conversion jobs when they started
	public void onConversionStarted( String uuid )
	{
		logger.info( "Set node info when conversion job started. UUID: " + uuid );
		
		try
		{
			synchronized( this.nodeList )
			{
				NodeInfo nodeInfo = findNode( uuid );
				if (nodeInfo == null)
				{
					logger.error( "Node doesn't exists, add node. UUID: " + uuid );
				}
				else // node exists
				{
					Date lastSessionTime = getLastSessionTime( nodeInfo.getSessionFolder() );
					if (lastSessionTime != null)
						nodeInfo.setLastSessionTime( lastSessionTime );
					
					logger.info( "Node info was set. UUID: " + uuid );
					logger.info( "Node info (" + uuid + ") - Last session time: " + lastSessionTime );
				}
			}
		}
		catch (Exception e)
		{
			logger.error( "Error occurred when processing conversion starting.", e );
		}
	}
	
	private void getInfoOfMonitoredNodes()
	{
		try
		{
			logger.info( "RemoteVCMSessionMonitor: getInfoOfMonitoredNodes() begin." );
			
			synchronized( this.nodeList )
			{
				this.nodeList.clear();
				
				File configFolder = new File(
					ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath() );
				if (!configFolder.exists())
				{
					logger.info( "RemoteVCMSessionMonitor: Configuration path doesn't exist." );
					return;
				}
				
				for (File file : configFolder.listFiles())
				{
					String filename = file.getName();
					if (filename.length() != 40)
						continue;
					
					String uuid = filename.substring( 0, filename.lastIndexOf( "." ) );
					
					VirtualMachine vm = new VirtualMachine();
					vm.setVmInstanceUUID( uuid );
					VMBackupConfiguration vmConfig = VSphereService.getInstance().getVMBackupConfiguration( vm );
					if (!ManualConversionUtility.isVSBWithoutHASupport(vmConfig))
						continue;
					
					this.addNodeInfo( uuid, vmConfig.getDestination() );
				}
			}
			
			logger.info( "RemoteVCMSessionMonitor: getInfoOfMonitoredNodes() completed." );
		}
		catch (Exception e)
		{
			logger.error( "RemoteVCMSessionMonitor: getInfoOfMonitoredNodes() failed.", e );
		}
	}
	
	@Deprecated
	private Integer[] getIntactSessions( String sessionFolderPath )
	{
		try
		{
			List<Integer> sessionList = this.nativeFacade.getIntactSessions( sessionFolderPath );
			if (sessionList == null)
			{
				logger.error( "RemoteVCMSessionMonitor: nativeFacade.getIntactSessions() returns null." );
				return null;
			}
			
			Integer[] sessionArray = sessionList.toArray( new Integer[0] );
			Arrays.sort( sessionArray, new Comparator<Integer>()
				{
					@Override
					public int compare( Integer int1, Integer int2 )
					{
						return (int1 - int2);
					}
				} );
			
			return sessionArray;
		}
		catch (Exception e)
		{
			logger.error( "RemoteVCMSessionMonitor: getIntactSessions() failed.", e );
			return null;
		}
	}
	
	private void addNodeInfo( String uuid, String sessionFolderPath )
	{
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setUuid( uuid );
		nodeInfo.setSessionFolder( sessionFolderPath );
		nodeInfo.setLastSessionTime( getLastSessionTime( nodeInfo.getSessionFolder() ) );

		this.nodeList.add( nodeInfo );
		
		this.addNodeToRHAScenarios( nodeInfo );
	}
	
	private NodeInfo findNode( String uuid )
	{
		for (NodeInfo nodeInfo : this.nodeList)
		{
			if (nodeInfo.getUuid().equalsIgnoreCase( uuid ))
				return nodeInfo;
		}
		
		return null;
	}
	
	public void addNode( String uuid, String sessionFolderPath )
	{
		logger.info( "RemoteVCMSessionMonitor: Try to add node. UUID: " + uuid );
		
		synchronized( this.nodeList )
		{
			NodeInfo nodeInfo = findNode( uuid );
			if (nodeInfo == null)
			{
				logger.info( "RemoteVCMSessionMonitor: Node doesn't exists, add node. UUID: " + uuid );
				addNodeInfo( uuid, sessionFolderPath );
			}
			else // node is already existed
			{
				logger.info( "RemoteVCMSessionMonitor: Node exists, set node info. " +
					"UUID: " + uuid + ", Session folder: " + sessionFolderPath );
				
				this.removeNodeFromRHAScenarios( nodeInfo );
				
				// We don't need to get session list from the new session folder, and
				// it will be retrieved when next polling, and will be compared with
				// the session list of the old session folder.
				
				nodeInfo.setSessionFolder( sessionFolderPath );
				
				this.addNodeToRHAScenarios( nodeInfo );
			}
			
			logger.info( "RemoteVCMSessionMonitor: Adding node (or setting node info if it exists) done." );
		}
	}
	
	public void removeNode( String uuid )
	{
		logger.info( "RemoteVCMSessionMonitor: Try to remove node. UUID: " + uuid );
		
		synchronized( this.nodeList )
		{
			NodeInfo toBeRemoved = findNode( uuid );
			if (toBeRemoved != null)
			{
				this.nodeList.remove( toBeRemoved );
				this.removeNodeFromRHAScenarios( toBeRemoved );
			}
		}
	}
	
	private RHAScenarioInfo findRHAScenario( String rootPath )
	{
		for (RHAScenarioInfo scenarioInfo : this.rhaScenarioList)
		{
			if (scenarioInfo.getRootPath().equalsIgnoreCase( rootPath ))
				return scenarioInfo;
		}
		
		return null;
	}
	
	private void addNodeToRHAScenarios( NodeInfo nodeInfo )
	{
		synchronized( this.rhaScenarioList )
		{
			String rootPath = ManualConversionUtility.getRHAScenarioRootPathFromSessionFolderPath(
				nodeInfo.getSessionFolder() );
			RHAScenarioInfo scenarioInfo = findRHAScenario( rootPath );
			if (scenarioInfo == null)
			{
				scenarioInfo = new RHAScenarioInfo();
				scenarioInfo.setRootPath( rootPath );
				scenarioInfo.setState( RHAScenarioState.Unknown );
				scenarioInfo.getManagedNodes().add( nodeInfo );
			}
			else // exists already
			{
				scenarioInfo.getManagedNodes().add( nodeInfo );
			}
			
			this.rhaScenarioList.add( scenarioInfo );
		}
	}
	
	private void removeNodeFromRHAScenarios( NodeInfo nodeInfo )
	{
		synchronized( this.rhaScenarioList )
		{
			String rootPath = ManualConversionUtility.getRHAScenarioRootPathFromSessionFolderPath(
				nodeInfo.getSessionFolder() );
			RHAScenarioInfo scenarioInfo = findRHAScenario( rootPath );
			if (scenarioInfo == null)
				return;
			
			scenarioInfo.getManagedNodes().remove( nodeInfo );
			if (scenarioInfo.getManagedNodes().size() == 0)
				this.rhaScenarioList.remove( scenarioInfo );
		}
	}
	
	private void initRHAScenarioStates()
	{
		synchronized( this.rhaScenarioList )
		{
			for (RHAScenarioInfo scenarioInfo : this.rhaScenarioList)
			{
				try
				{
					RHAScenarioState state =
						this.nativeFacade.getRHAScenarioState( scenarioInfo.getRootPath() );
					scenarioInfo.setState( state );
					logger.info( "State of RHA scenario with root path \'" +
						scenarioInfo.getRootPath() + "\' is < " + state + " >." );
					addActivityLogForRHAScenarioState( scenarioInfo, state );
				}
				catch (Exception e)
				{
					logger.error( "Error getting state of RHA scenario with root path \'" +
						scenarioInfo.getRootPath() + "\'.", e );
				}
			}
		}
	}
	
	private void addActivityLogForRHAScenarioState(
		RHAScenarioInfo scenarioInfo, RHAScenarioState state )
	{
		String messageKey = "";
		long level = Constants.AFRES_AFALOG_INFO;
		
		switch (state)
		{
		case Run:
			messageKey = ReplicationMessage.REPLICATION_RHASENARIO_IS_RUNNING;
			level = Constants.AFRES_AFALOG_INFO;
			break;
			
		case Stop:
			messageKey = ReplicationMessage.REPLICATION_RHASENARIO_IS_STOPPED;
			level = Constants.AFRES_AFALOG_WARNING;
			break;
			
		case Sync:
			messageKey = ReplicationMessage.REPLICATION_RHASENARIO_IS_SYNCHRONIZING;
			level = Constants.AFRES_AFALOG_WARNING;
			break;
			
		case Unknown:
			messageKey = ReplicationMessage.REPLICATION_RHASENARIO_STATEIS_UNKNOWN;
			level = Constants.AFRES_AFALOG_WARNING;
			break;
			
		default:
			logger.error( "Unknown RHA scenario state. State: " + state );
			return;
		}
		
		String message = ReplicationMessage.getResource( messageKey );
		addActivityLogForRHAScenario( level, scenarioInfo, message );
	}
	
	private void addActivityLogForRHAScenario( long logLevel, RHAScenarioInfo scenarioInfo, String message )
	{
		if (scenarioInfo == null)
		{
			logger.error( "Invalid parameter for addActivityLogForRHAScenario(): scenarioInfo is null." );
			return;
		}
		
		for (NodeInfo nodeInfo : scenarioInfo.getManagedNodes())
		{
			HACommon.addActivityLogByAFGuid(
				logLevel, -1, Constants.AFRES_AFJWBS_GENERAL,
				new String[] { message, "", "", "", "" }, nodeInfo.getUuid() );
		}
	}
	
	private RHAScenarioState getLastRHAScenarioStateByNode( NodeInfo nodeInfo )
	{
		for (RHAScenarioInfo scenarioInfo : this.rhaScenarioList)
		{
			if (scenarioInfo.getManagedNodes().contains( nodeInfo ))
				return scenarioInfo.getState();
		}
		
		return RHAScenarioState.Unknown;
	}
	
	public void setHasPendingJobs( String uuid, boolean hasPendingJobs )
	{
		logger.info( "RemoteVCMSessionMonitor: Try to set pending job for node. UUID: " +
			uuid + ", hasPendingJobs: " + hasPendingJobs );
		
		synchronized( this.nodeList )
		{
			NodeInfo nodeInfo = findNode( uuid );
			if (nodeInfo == null)
			{
				logger.error( "RemoteVCMSessionMonitor: Node doesn't exists, add node. UUID: " + uuid );
			}
			else // node exists
			{
				nodeInfo.setHasPendingJobs( hasPendingJobs );
				logger.info( "RemoteVCMSessionMonitor: Pending job was set. UUID: " + uuid );
			}
		}
	}
	
	private Date getLastSessionTime( String sessionFolder )
	{
		if (!sessionFolder.endsWith( "\\" ))
			sessionFolder += "\\";
		
		String dataFolderPath = sessionFolder + "VStore";
		File sessionDataFolder = new File( dataFolderPath );
		File[] sessions = sessionDataFolder.listFiles();
		if ((sessions == null) || (sessions.length == 0))
		{
			logger.error(
				"No sessions or failed to list session data folder. Folder: " +
				dataFolderPath );
			return null;
		}
		
		Arrays.sort( sessions, new Comparator<File>()
			{
				@Override
				public int compare( File file1, File file2 )
				{
					return file1.getName().compareTo( file2.getName() );
				}
			}
		);
		
		for (int i = sessions.length - 1; i >= 0; i --)
		{
			if(!BaseReplicationCommand.checkBackupSessionConfigFile(sessions[i])){
				logger.info("backup config file is not avaliable.This session will be skipped");
				continue;
			}
			
			if (!BaseReplicationCommand.checkIndexOfBackupSessionFile(sessions[i])) {
				logger.warn("Session index file is not avaliable.This session will be skipped.");
				continue;
			}

			String backupInfoFilePath = sessions[i].getPath() + "\\BackupInfo.XML";			
			try
			{
				String fileContent = CommonUtil.readFileAsString( backupInfoFilePath );
				BackupInfo backupInfo = BackupInfoFactory.getBackupInfoFromString( fileContent );
				if ((backupInfo == null) ||
					(BackupConverterUtil.string2BackupStatus( backupInfo.getBackupStatus() ) != BackupStatus.Finished))
				{
					logger.info(
						"Error parsing backup info, or session doesn't complete. File path: " +
						backupInfoFilePath );
					continue;
				}
				
				Date backupTime = BackupConverterUtil.string2Date(
					backupInfo.getDate() + " " + backupInfo.getTime() );
				
				if (backupTime == null)
				{
					logger.error( "Error getting backup time. File path: " + backupInfoFilePath );
					continue;
				}
				
				return backupTime;
			}
			catch (Exception e)
			{
				logger.error( "Error processing BackupInfo.xml. File path: " + backupInfoFilePath, e );
				continue;
			}
		}
		
		return null;
	}
}
