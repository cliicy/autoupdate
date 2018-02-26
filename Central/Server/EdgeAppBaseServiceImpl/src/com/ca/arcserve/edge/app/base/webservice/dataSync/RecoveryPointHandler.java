package com.ca.arcserve.edge.app.base.webservice.dataSync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.ds.DataSizesFromStorage;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ApplianceUtils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.RPMScheduleData;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.linux.EdgeService4LinuxD2DUtil;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.linuximaging.webservice.data.NodeBackupInfo;

/**
 * 
 * @author fanda03
 * the operation of this handle is launched by CPM, not pushed by Agent; so can not use this handler in HandlerFactory;
 */
public class RecoveryPointHandler  {

	private static Integer mExecuteQueueSize = 5; 
	private static RecoveryPointHandler rpHandler = new RecoveryPointHandler();
	
	//this project cannot see the DeviceTypeInMedia class;
	private static int DeviceTypeD2D = 4;
	private static int DeviceTypeDedup = 5;
	public static RecoveryPointHandler getInstance() {
		return rpHandler;
	}

	private Logger logger = Logger.getLogger(RecoveryPointHandler.class); 
	private ReentrantLock runLock = new ReentrantLock();
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private IEdgeVCMDao vsphereDao = DaoFactory.getDao( IEdgeVCMDao.class );
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
	private IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);

	private ThreadPoolExecutor mExecutorService =  new ThreadPoolExecutor(mExecuteQueueSize, mExecuteQueueSize,
            60L, TimeUnit.SECONDS,  new LinkedBlockingQueue <Runnable>(), new NamingThreadFactory("RecoveryPointHandler"));
	private CompletionService<Void> service = new ExecutorCompletionService<Void>(mExecutorService);;
	
	private RPSNodeServiceImpl rpsService = new RPSNodeServiceImpl();
	private RPSDataStoreServiceImpl dsService = new RPSDataStoreServiceImpl();
	private PolicyManagementServiceImpl policyManagementServiceImpl = new PolicyManagementServiceImpl();

	private List<PlanNodeInformation> planNodeMap = new ArrayList<PlanNodeInformation>();
	private Map<RPMKey, RPMSummaryData> rpSummaryCache = Collections.synchronizedMap( new HashMap<RPMKey, RPMSummaryData>() ); 
	private List<RPSDataStoreVolumeSize> rpsDSVolumeSizeList = new ArrayList<RPSDataStoreVolumeSize>();	
	private Set<SyncProvider> successSyncProvider = Collections.synchronizedSet( new HashSet<SyncProvider>() );  
	private List<Runnable> tasks = new ArrayList<Runnable>();
	
	private List<DestinationSettings> shareFolderSettings = new ArrayList<DestinationSettings>();		
	private List<DestinationSettings> hbbuLocalFolderSettings = new ArrayList<DestinationSettings>();
	private List<DestinationSettings> d2dLocalFolderSettings = new ArrayList<DestinationSettings>();

	private Thread currentRunningThread = null;
	private RecoveryPointHandler() {
		mExecutorService.allowCoreThreadTimeOut(true);
	}
	public void destory() {
		if (currentRunningThread != null) {
			currentRunningThread.interrupt();
		}
		this.mExecutorService.shutdownNow();
	}
	
	public void syncRecoveryPointTotalSize( ) {
		int taskId = -1;
		try {
			if( !runLock.tryLock() ) {
				logger.info("RecoveryPointHandler: thread is running , skip this call!");
			}
			else {
				/*
				 * the UI will try to call delete function after job success; but if the delete call temporary fail and user start a new job before next delete re-try;
				 * it may has issue; so we always delete old task when re-run the sync job;
				 * 
				 * delete this code, seems the sp layer already handle this case!
				 */
//				if( oldTaskId !=null  ) {
//					TaskMonitor.deleteTask(oldTaskId);
//				}
				logger.info("RecoveryPointHandler: start sync!");
				currentRunningThread = Thread.currentThread();
				taskId = TaskMonitor.registerNewTask(Module.RecoveryPointSummarySync, RPMScheduleData.RecoveryPointSyncTargetMarker,  TaskStatus.InProcess, new TaskDetail()  );
				doSync();
				logger.info("RecoveryPointHandler: end sync!");
				TaskMonitor.updateTaskStatus(taskId, TaskStatus.OK, new TaskDetail() );	
//				TaskMonitor.deleteTask( taskId );	
			}
		}
		catch( Throwable e ) {
			logger.error( " RecoveryPointHandler: sync recovery point error " , e );
			if( taskId != -1 ) {
				TaskMonitor.updateTaskStatus(taskId, TaskStatus.Error, new TaskDetail() );	
//				TaskMonitor.deleteTask( taskId );
			}
		}
		finally {
			currentRunningThread = null;
			if ( runLock.isHeldByCurrentThread() ) {
				runLock.unlock();
			}
		}
	}
	private void doSync() {
		rpSummaryCache.clear();
		rpsDSVolumeSizeList.clear();
		planNodeMap.clear();
		successSyncProvider.clear();
		tasks.clear();
		shareFolderSettings.clear();
		hbbuLocalFolderSettings.clear();
		d2dLocalFolderSettings.clear();
		
		generateNodePlanMapInfo();

		syncFromRPS(); 
		syncFromLinuxProxy();
		
		if( !waitFinish() ){
			return;
		}

		syncFromD2DDest();
		
		if( !waitFinish() ){
			return;
		}
		
		updateDB();
	
	}
	
	private boolean waitFinish(){
		for (Runnable task : tasks) {
			service.submit(task, null);
		}
	
		try {
			for (int i = 0; i < tasks.size(); ++i) {
				service.take();
			}
			return true;
		}
		catch( InterruptedException e ) {
			logger.error( " RecoveryPointHandler: wait RPS/Proxy, interrupted! " , e );
			return false;
		}
		finally {
			tasks.clear();
		}
	}
	private void generateNodePlanMapInfo() {
		int bitMap = 0;
		bitMap = Utils.setBit(bitMap, PlanTaskType.WindowsD2DBackup, true);
		bitMap = Utils.setBit(bitMap, PlanTaskType.LinuxBackup, true);
		bitMap = Utils.setBit(bitMap, PlanTaskType.WindowsVMBackup, true);
		bitMap = Utils.setBit(bitMap, PlanTaskType.Replication, true);
		bitMap = Utils.setBit(bitMap, PlanTaskType.MspServerReplication, true);
		planNodeMap = new ArrayList<PlanNodeInformation>();
		List<EdgeHostPolicyMap> hostPolicyMap = new ArrayList<EdgeHostPolicyMap>();
		edgePolicyDao.getHostPolicyMapByHostAndPlanTaskType( -1, bitMap, hostPolicyMap );
		
		try {
			///plan fetch operation is too slow!; we only fetch plan which has node association
			//List<Integer> planInfos = policyManagementServiceImpl.getPlanIds();
			Set<Integer> planInfos = new TreeSet<Integer>();
			for( EdgeHostPolicyMap hostPolicy : hostPolicyMap  ) {
				planInfos.add(hostPolicy.getPolicyId());
			}
			
			//get planList with node information;
			List<PlanNodeInformation> planNodeInfos = new ArrayList<PlanNodeInformation>();
			for( EdgeHostPolicyMap hostPolicy : hostPolicyMap  ) {
				for( Integer planId : planInfos ){
					if(planId == hostPolicy.getPolicyId()){
						PlanNodeInformation planNode = new PlanNodeInformation(hostPolicy.getPolicyId(), hostPolicy.getHostId());
						planNodeInfos.add(planNode);
					}
				}
			}			

			for( PlanNodeInformation pi : planNodeInfos ) {
				PlanNodeInformation plan = null; 

				UnifiedPolicy planDetail = policyManagementServiceImpl.loadUnifiedPolicyById( pi.getPlanId() );
				if( planDetail.getLinuxBackupsetting() !=null ) {
					plan = new PlanNodeInformation( pi.getPlanId(),  DestProviderType.Linux, pi.getNodeId() ); 
				}
				else if( planDetail.getBackupConfiguration() != null ) {					
					plan = new PlanNodeInformation( pi.getPlanId(), planDetail.getBackupConfiguration().isD2dOrRPSDestType()? DestProviderType.Agent : DestProviderType.RPS , pi.getNodeId()); ///true agent share folder; false data store; 
					
					if (plan.getProviderType() == DestProviderType.Agent) {
						BackupConfiguration bc = planDetail.getBackupConfiguration();
						if (bc != null) {
							if (isValidLocalPath(bc.getDestination())) { // local folder
								DestinationSettings localFolder = new DestinationSettings(pi.getNodeId(), false, bc.getDestination(), "", "", false);
								d2dLocalFolderSettings.add(localFolder);
								plan.setDestination(bc.getDestination());

							} else { // share folder
								if (!cotainShareFolderPath(shareFolderSettings,	bc.getDestination())) {
									DestinationSettings shareFolder = new DestinationSettings( pi.getNodeId(), true, bc.getDestination(),
											bc.getUserName(), bc.getPassword(), false);
									
									shareFolderSettings.add(shareFolder);
				}
								plan.setProviderType(DestProviderType.Sharefolder);
								plan.setDestination(bc.getDestination());
				}
				}
				}
			}
				else if( planDetail.getVSphereBackupConfiguration()!=null ) {
					plan = new PlanNodeInformation( pi.getPlanId(), planDetail.getVSphereBackupConfiguration().isD2dOrRPSDestType()? DestProviderType.HBBU : DestProviderType.RPS , pi.getNodeId()); 
					
					if (plan.getProviderType() == DestProviderType.HBBU){
						VSphereBackupConfiguration bc = planDetail.getVSphereBackupConfiguration();
						if(isValidLocalPath(bc.getDestination())){ //local folder
							VSphereProxy proxy = bc.getvSphereProxy();
							DestinationSettings localFolder = new DestinationSettings( proxy.getVSphereProxyHostID(), false, bc.getDestination(), "", "", true);
							hbbuLocalFolderSettings.add(localFolder);
							plan.setDestination(bc.getDestination());
							
						}else{ // share folder
							if(!cotainShareFolderPath(shareFolderSettings, bc.getDestination())){
								DestinationSettings shareFolder = new DestinationSettings(bc.getvSphereProxy().getVSphereProxyHostID(), true, 
										bc.getDestination(), bc.getUserName(), bc.getPassword(), true);	
								shareFolderSettings.add(shareFolder);
							}
							plan.setProviderType(DestProviderType.Sharefolder);
							plan.setDestination(bc.getDestination());
						}
					}
				}
				else if( planDetail.getMspServerReplicationSettings()!=null ) {
					plan = new PlanNodeInformation( pi.getPlanId(), DestProviderType.RPS, pi.getNodeId()); 
				}
				if( plan != null ) {
					planNodeMap.add( plan );
				}
			}

		} catch (EdgeServiceFault e) {
			logger.error( " RecoveryPointHandler: get plan info error " , e );
		
		}
	}
	private void updateDB() {

		for( SyncProvider provider:  successSyncProvider ) {
			jobHistoryDao.as_edge_recovery_point_summary_delete( provider.getNodeId(), provider.getProviderType().ordinal(), getRecoveryPointKeepDateFrom(), getRecoveryPointKeepDateTo());	
						
		}
		
		for( Map.Entry<RPMKey, RPMSummaryData> entry:  rpSummaryCache.entrySet() ) {
			RPMKey key = entry.getKey();
			RPMSummaryData data = entry.getValue();
			if( data !=null ) {
				try {
					jobHistoryDao.as_edge_handle_recovery_point_summarydata( key.getProtectedNodeLocalId(), key.getProtectedNodeName(), key.getDestIdentify(), key.getDestinationProvider().getNodeId(), key.getDestinationProvider().getProviderType().ordinal(), 
							data.getRawSize(), data.getBackupDataSize(), data.getRestorableDataSize(), data.getDest(), data.getDeviceType() );
				}
				catch( Exception e ) {
					logger.error( " RecoveryPointHandler: insert data fail! " , e );
				}			
			}
		}
		
		for(RPSDataStoreVolumeSize rpsVolumeSize : rpsDSVolumeSizeList){
			jobHistoryDao.as_edge_recovery_point_rps_volumesize_delete(rpsVolumeSize.getRpsNodeId());
			try{
				jobHistoryDao.as_edge_handle_recovery_point_rps_volumesize(rpsVolumeSize.getRpsNodeId(), rpsVolumeSize.getCapacitySpace(), rpsVolumeSize.getUsedSpace() ); 
			}catch( Exception e ) {
				logger.error( " RecoveryPointHandler: insert rps datastore volume size data fail! " , e );
			}	
		}

	}
	
	private Date getRecoveryPointKeepDateFrom(){
		Date curDate = new Date();
		curDate.setDate(curDate.getDate() - 6);
		return new Date(curDate.getYear(), curDate.getMonth(), curDate.getDate());
	}
	
	private Date getRecoveryPointKeepDateTo(){
		Date curDate = new Date();		
		return new Date(curDate.getYear(), curDate.getMonth(), curDate.getDate());
	}
	
	private int getHostId(String nodeUUID){
		int hostId = D2DEdgeServiceImpl.getVmHostId(nodeUUID);
		if (hostId == 0) {
			hostId = EdgeService4LinuxD2DUtil.getLinuxNodeHostId(nodeUUID); //for linux node, uuid is  nodename;
		}
		if( hostId ==0 ) {
			hostId = D2DEdgeServiceImpl.getD2DHostId(nodeUUID);
		}
		
		if(hostId !=0){
			return hostId;
		}
		else {
			logger.error( " RecoveryPointHandler: the node with UUId =  " + nodeUUID + " don't have corresponding id in CPM ! "  );
			return -1;
		}
	}
	
	private synchronized boolean deleteNodeInHostPolicyMap( RPMKey key, RPMSummaryData summaryData, int hostId , String destinationInPlan){
		if( hostId != 0 ) {			
			Iterator<PlanNodeInformation> iter =  this.planNodeMap.iterator();
			while( iter.hasNext() ) {
				PlanNodeInformation hpm = iter.next();	
				
				if ( hpm.getNodeId() == hostId ) {
					
					if( key.getDestinationProvider().getProviderType() == hpm.getProviderType() 
							&& destinationInPlan.equals(hpm.getDestination())  ) {
						key.setProtectedNodeLocalId( hostId );
						rpSummaryCache.put( key, summaryData );

						iter.remove();	
					}
					return true;
				}
			}			
		}
		
		return false; //not found in map
	}
	
	/**
	 * this operation based on a fact that if part information of the node can be obtained by using one strategy, all of this node's  information 
	 * can be obtained using this strategy;
	 * for example: if one datastore contain one node( replication or backup ); the backup/replica chain of this node can be obtained using 
	 * sync from rps, never use sync From Proxy or agent self;
	 * so this function may try to delete one node repeated in one strategy( back/replicate to different rps ), 
	 * but never try to delete one node in different strategy
	 * @param nodeId
	 */
	private synchronized int deleteNodeInHostPolicyMap( RPMKey key, RPMSummaryData summaryData ){
		String nodeUUID = key.getProtectedNodeUUID();
		int hostId = D2DEdgeServiceImpl.getVmHostId(nodeUUID);
		if (hostId == 0) {
			hostId = EdgeService4LinuxD2DUtil.getLinuxNodeHostId(nodeUUID); //for linux node, uuid is  nodename;
		}
		if( hostId ==0 ) {
			hostId = D2DEdgeServiceImpl.getD2DHostId(nodeUUID);
		}
		
//		if(  hostId != 0 ) {
			key.setProtectedNodeLocalId( hostId );
			rpSummaryCache.put( key, summaryData );
			
			Iterator<PlanNodeInformation> iter =  this.planNodeMap.iterator();
			while( iter.hasNext() ) {
				PlanNodeInformation hpm = iter.next();	
				
				if ( hpm.getNodeId() == hostId ) {
					if( key.getDestinationProvider().getProviderType() == hpm.getProviderType()  ) {
						iter.remove();	
					}
					break;
				}
			}
			return hostId;
//		}
//		else {
//			logger.error( " RecoveryPointHandler: the node with UUId =  " + nodeUUID + " don't have corresponding id in CPM ! "  );
//			return -1;
//		}
	}

	public void syncFromRPS() {

		try {
			List<RpsNode> nodeList = rpsService.getRpsNodesByGroup(0, -1 );
			for( RpsNode rps : nodeList ) {
				List<DataStoreSettingInfo> settings = dsService.getDataStoreListByNode( rps.getNode_id() );
				if( settings.size() >0 ) {
					tasks.add(new SyncRPSRecoveryPointTask(settings, rps));
				} else {
					jobHistoryDao.as_edge_recovery_point_summary_delete( rps.getNode_id(), DestProviderType.RPS.ordinal(), getRecoveryPointKeepDateFrom(), getRecoveryPointKeepDateTo() );
					jobHistoryDao.as_edge_recovery_point_rps_volumesize_delete(rps.getNode_id());
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error( " RecoveryPointHandler: get rps node list error " , e );
		}
		
	}
	public void syncFromHBBUProxySelf(List<DestinationSettings> destinationSettings) {
		try {
			/*issue 114180:
			 * now the id in as_edge_vsphere_proxy_info has no relation to rhotid in as_edge_host table, actually it's a self_increment column;
			 * after ask Liu Hongding, he said hbbu has no method to identify  proxy list from as_edge_host/connection_info table; 
			 * so we can only obtain connection info from this table;
			 * */
			List<EdgeD2DHost> proxys = new ArrayList<EdgeD2DHost>(); 
			vsphereDao.as_edge_vsphere_proxy_list( proxys ); 

			for(DestinationSettings setting : destinationSettings){
				for( EdgeD2DHost proxy : proxys ) {
					if(proxy.getRhostid() == setting.getNodeId()){
						tasks.add(new SyncVMBackupProxyRecoveryPointTask(proxy, setting.getDestination()));	
						break;
					}
				}
			}			
		} 
		catch (Exception e) {
			logger.error( " RecoveryPointHandler: get protected node summary from proxy error " , e );
		}
	}
	
	public void syncFromLinuxProxy() {

		//connectionInfoDao.as_edge_linux_d2d_server_by_hostid(registrationNodeInfo.getId(), connInfoLst);
		List<EdgeHost> linuxD2DList = new ArrayList<EdgeHost>();
		try{
			hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, linuxD2DList); //get all d2d server and then select one to validate linux node
			for (EdgeHost linuxServer : linuxD2DList) {
				tasks.add(new SyncLinuxServerRecoveryPointTask(linuxServer.getRhostid()));
			}
		}
		catch (Exception e) {
			logger.error( " RecoveryPointHandler: get protected node summary from linux server error " , e );
		}
	}

	public void syncFromD2DDest(){
				
		//share folder
		if(shareFolderSettings.size()>0){
			syncFromShareFolder(shareFolderSettings);
		}
		
		//HBBU Local folder
		if(hbbuLocalFolderSettings.size()>0){
			syncFromHBBUProxySelf(hbbuLocalFolderSettings);
		}
		
		//D2D Local folder
		if(d2dLocalFolderSettings.size()>0){
			syncFromAgentSelf(d2dLocalFolderSettings);
		}
		
	}
	
	private boolean cotainShareFolderPath(List<DestinationSettings> shareFolderList, String shareFolder){
		for(DestinationSettings dstSetting : shareFolderList){
			if(dstSetting.getDestination().equals(shareFolder)){
				return true;
			}
		}		
		return false;
	}

	public void syncFromAgentSelf(List<DestinationSettings> d2dLocalFolderSettings) {
		for (DestinationSettings setting : d2dLocalFolderSettings) {
			int nodeId = setting.getNodeId();
			try {
				D2DConnectInfo connInfo = this.policyManagementServiceImpl.getD2DConnectInfo(nodeId);
				if(StringUtil.isEmptyOrNull(connInfo.getHostName())) {
					logger.error("syncFromAgentSelf(): the node : " + nodeId + "have empty hostnam, is it a vm but the proxy sync fail?"); 
					continue;
				}
			
				tasks.add(new SyncD2DRecoveryPointTask(connInfo.getHostId(),setting.getDestination()));
			} 
			catch (Exception e) {
				logger.error(" RecoveryPointHandler syncFromAgentSelf: get connection info error for node id= "+ nodeId, e);
			}
		}
	}

	public void syncFromShareFolder(List<DestinationSettings> shareFolderSettings){
		D2DConnectInfo connInfo = null;
		int nodeId;

		for (DestinationSettings setting : shareFolderSettings) {			
			nodeId = setting.getNodeId();
			try {
				connInfo = this.policyManagementServiceImpl.getD2DConnectInfo(nodeId);
				if (!StringUtil.isEmptyOrNull(connInfo.getHostName())) {
					logger.info("syncFromShareFolder: connect D2D node " + nodeId + " to sync the recovery point of Share folder.");
					tasks.add(new SyncD2DShareFolderRecoveryPointTask(connInfo.getHostId(),shareFolderSettings));					
					break;
				}				
			
			} catch (Exception e) {
				logger.error(" RecoveryPointHandler syncFromShareFolder: get connection info error for node id= " + nodeId, e);
			}			
		}		
			
	}
	

	private class SyncRPSRecoveryPointTask implements Runnable {
		private  List<DataStoreSettingInfo> settings;
		private RpsNode rps;
		private SyncProvider syncProvider;
		public SyncRPSRecoveryPointTask( List<DataStoreSettingInfo>  settings, RpsNode rps  ) {
			this.syncProvider = new SyncProvider( rps.getNode_id() , DestProviderType.RPS );
			this.settings = settings;
			this.rps = rps;
		}

		@Override
		public void run() {
			logger.debug("Rps recovery point Summary sync: process rps node: " + rps.getNode_id());
			boolean bSyncSuccess = false;
			try (RPSConnection connection = EdgeCommonUtil.getRPSServerProxyByNodeId(rps.getNode_id())){
				for (DataStoreSettingInfo setting : settings) {
					try{
						List<DataSizesFromStorage> sizes = connection.getService().getDataSizesFromStorage(
								setting.getDatastore_name(), false,
								setting.getDSCommSetting().getUser(),
								setting.getDSCommSetting().getPassword());
						for (DataSizesFromStorage sizeInfo : sizes) {
							RPMKey key = new RPMKey(sizeInfo.getNodeUUID(),	setting.getDatastore_name(), syncProvider);
							key.setProtectedNodeName(sizeInfo.getNodeName());
							String dsDisplayName = rps.getNode_name() + "->" + setting.getDisplayName();
							boolean isDedup = setting.getEnableGDD() > 0;
							RPMSummaryData summaryData = new RPMSummaryData(sizeInfo.getRawDataSize(), sizeInfo.getDataStorageSize(), dsDisplayName,
									isDedup ? DeviceTypeDedup : DeviceTypeD2D, sizeInfo.getRestorableDataSize()); ///5 means dedup; 4 means file; only used in data distribution Report!

							deleteNodeInHostPolicyMap(key, summaryData);
						}
						
						bSyncSuccess = true;
						
					}catch (Exception e) {
						logger.error("Failed to  sync data from datastore: " + setting.getDisplayName(), e);
					}					
				}				

				if(bSyncSuccess)
					successSyncProvider.add(syncProvider); 
				
				// get max size
				if (ApplianceUtils.isAppliance()) {
					com.ca.arcflash.rps.webservice.data.RPSDataStoreVolumeSize volumeSize =  connection.getService().getVolumeSize4DataStore();
					RPSDataStoreVolumeSize dsSize = new RPSDataStoreVolumeSize(
							rps.getNode_id(), volumeSize.getCapacitySapce(),
							volumeSize.getUsedSpace());
					rpsDSVolumeSizeList.add(dsSize);
				}
				
			} catch (Exception e) {
				logger.error("Failed to connect RPS server [id="+ rps.getNode_id() + "] to get recovery point", e);
			}
		}
	}
	
	private class SyncD2DRecoveryPointTask implements Runnable {
		protected int connectHostId;
		protected SyncProvider syncProvider;
		protected String destination;
		public SyncD2DRecoveryPointTask(int hostId, String destination) {
			this.connectHostId = hostId;
			this.destination = destination;
			syncProvider = new SyncProvider(connectHostId, DestProviderType.Agent );
		}
		
		@Override
		public void run() {
			logger.debug("agent recovery point Summary sync: process d2d node local folder: " + connectHostId );
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

			try (D2DConnection connection = connectionFactory.createD2DConnection(connectHostId)) {
				connection.connect();
				try{
					List<com.ca.arcflash.webservice.data.DataSizesFromStorage> sizes = connection.getService().getDataSizesFromStorage(destination, "", "");
					fillDataForD2DLocalFolder(sizes);
					successSyncProvider.add(syncProvider);					
				}catch (Exception e){
					logger.error( "Failed to sync recovery point of D2D node local folder: " + destination , e);
				}				
			} catch (Exception e) {
				logger.error("Failed to sync recovery point of D2D node local folder [id=" + connectHostId + "] " , e);
			}
		}
		
		protected void fillDataForD2DLocalFolder(List<com.ca.arcflash.webservice.data.DataSizesFromStorage> sizes ){
			if(sizes == null)
				return ;
			
			List<Integer> hostIdList = new ArrayList<Integer>();
			for (com.ca.arcflash.webservice.data.DataSizesFromStorage sizeInfo : sizes) {
				RPMKey key = new RPMKey(sizeInfo.getNodeUUID(),	sizeInfo.getDestination(), syncProvider);
				RPMSummaryData summaryData = new RPMSummaryData(
						sizeInfo.getRawDataSize(),
						sizeInfo.getDataStorageSize(),
						sizeInfo.getDestination(), DeviceTypeD2D,
						sizeInfo.getRestorableDataSize()); // /5 means dedup; 4 means file; only used in data distribution Report!

				key.setProtectedNodeName(sizeInfo.getNodeName());
				int hostId = getHostId(key.getProtectedNodeUUID());
				if (hostId > 0) {
					if(deleteNodeInHostPolicyMap(key, summaryData, hostId, destination)) { //if it was found in the map, add it to cache and delete it from map
						hostIdList.add(hostId);
						continue;
					}
					
					//if the node was not found in the planNodeMap, but it was found in the node list, which means it has multiple backup folder, so add it to the cache(rpSummaryCache).
					for (int id : hostIdList) {
						if (id == hostId) {								
							key.setProtectedNodeLocalId(hostId);
							rpSummaryCache.put(key, summaryData);
							break;
						}
					}
					
				}
			}
		}


	}
	
	private class SyncVMBackupProxyRecoveryPointTask extends SyncD2DRecoveryPointTask {
		
		private EdgeD2DHost proxy;

		public SyncVMBackupProxyRecoveryPointTask(EdgeD2DHost proxy, String destination ) {
			super(proxy.getRhostid(), destination);
			syncProvider = new SyncProvider(connectHostId, DestProviderType.HBBU );

			this.proxy = proxy;
		}
		
		@Override
		public void run() {
			logger.debug("VM backup proxy recovery point Summary sync: process d2d node: " + connectHostId );
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			
			try (D2DConnection connection = connectionFactory.createVMBackupProxyConnection(proxy)) {
				connection.connect();
				try{
					List<com.ca.arcflash.webservice.data.DataSizesFromStorage> sizes = connection.getService().getDataSizesFromStorage(destination, "", "");
					fillDataForD2DLocalFolder(sizes);
					successSyncProvider.add(syncProvider);
					
				}catch (Exception e){
					logger.error( "Failed to sync recovery point of HBBU local folder: " + destination, e);
				}
				
			} catch (Exception e) {
				logger.error("Failed to connect [id=" + connectHostId + "] to get recovery point of HBBU local folder ", e);
			}
		}
	}

	private class SyncD2DShareFolderRecoveryPointTask implements Runnable {
		private  List<DestinationSettings> destinationSettings;
		private SyncProvider syncProvider;		
		private int connectHostId;
		
		public SyncD2DShareFolderRecoveryPointTask(int hostId, List<DestinationSettings> destinationSettings) {
			this.connectHostId = hostId;
			this.destinationSettings = destinationSettings;
			syncProvider = new SyncProvider(connectHostId, DestProviderType.Sharefolder );
		}

		@Override
		public void run() {
			logger.debug("agent recovery point Summary sync: process share folder  ");
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

			try (D2DConnection connection = connectionFactory.createD2DConnection(connectHostId)) {
				connection.connect();
				for (DestinationSettings setting : destinationSettings) {	
					List<Integer> hostIdList = new ArrayList<Integer>(); 
					
					try{
						List<com.ca.arcflash.webservice.data.DataSizesFromStorage> sizes = connection.getService().getDataSizesFromStorage(setting.getDestination(), setting.getUserName(), setting.getUserPwd());
						for (com.ca.arcflash.webservice.data.DataSizesFromStorage sizeInfo : sizes) {
							int hostId = getHostId(sizeInfo.getNodeUUID());
							if (hostId > 0) {
								syncProvider.setNodeId(hostId);
								RPMKey key = new RPMKey(sizeInfo.getNodeUUID(),	sizeInfo.getDestination(), syncProvider);
								key.setProtectedNodeName(sizeInfo.getNodeName());
								RPMSummaryData summaryData = new RPMSummaryData(
										sizeInfo.getRawDataSize(),
										sizeInfo.getDataStorageSize(),
										sizeInfo.getDestination(), DeviceTypeD2D,
										sizeInfo.getRestorableDataSize()); // 5 means dedup; 4 means file; only used in data distribution Report!

								if (deleteNodeInHostPolicyMap(key, summaryData,	hostId, setting.getDestination())) { // if it's found in the map, add it to cache and delete it from map
									hostIdList.add(hostId);								
									successSyncProvider.add(syncProvider);
									continue;
								}
								
								//if the node was not found in the planNodeMap, but it was found in the node list, which means it has multiple backup folder, so add it to the cache(rpSummaryCache).   
								for (int id : hostIdList) {
									if (id == hostId) {								
										key.setProtectedNodeLocalId(hostId);
										rpSummaryCache.put(key, summaryData);									
										break;
									}
								}
							}						
						}				
					}catch(Exception e) {
						logger.error( "Failed to sync recovery point of sharefolder: " + setting.getDestination(), e);
					}				
				}
			}catch (Exception e) {
					logger.error( "Failed to connect D2D: " + connectHostId + "to get recovery point of share folder" , e);
			}
		}

	}
	
	
	private class SyncLinuxServerRecoveryPointTask implements Runnable {
		
		private int connectHostId;
		private SyncProvider syncProvider;
		
		public SyncLinuxServerRecoveryPointTask(int connectHostId) {
			this.connectHostId = connectHostId;
			syncProvider = new SyncProvider( connectHostId, DestProviderType.Linux );
		}
		
		@Override
		public void run() {
			IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
			try (LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(connectHostId)) {
				connection.connect();
				List<NodeBackupInfo> backupInfos = connection.getService().getNodeBackupInfo( null );
				for( NodeBackupInfo info : backupInfos ) {
					RPMKey key = new RPMKey( info.getNodeName(), info.getBackupDestination(), syncProvider );
					key.setProtectedNodeName(info.getNodeName());
					//linux return data with k-byte unit
					RPMSummaryData summaryData =  new RPMSummaryData( info.getRawDataSize() *1024,  info.getDataSize()*1024, info.getBackupDestination(), 
						DeviceTypeD2D, info.getRestoreableSize()*1024); ///5 means dedup; 4 means file; only used in data distribution Report!
		
					deleteNodeInHostPolicyMap( key, summaryData ); 
				}
				successSyncProvider.add(syncProvider); ///only add when successful call webservice!
			} catch (Exception e) {
				logger.info("Failed to sync recovery point of linux server [id=" + connectHostId + "], error message = " + e.getMessage());
			}
		}
	}
	
	private boolean isValidLocalPath(String path){
		if(path == null)
			return false;
		path=path.trim();
		return path.matches("[a-zA-Z]:(\\\\[^:]*)?");

	}
	
	private class DestinationSettings {
		private int nodeId;		
		private boolean isShareFolder;
		private String destination;
		private String userName;
		private String userPwd;
		private boolean isProxy;
		
		public DestinationSettings(int nodeId, boolean isShareFolder, String destination, String userName, String userPwd, boolean isProxy ){
			this.nodeId = nodeId;
			this.isShareFolder = isShareFolder;
			this.destination = destination;
			this.userName = userName;
			this.userPwd = userPwd;
			this.isProxy = isProxy;
		}
		
		public boolean isProxy() {
			return isProxy;
		}
		public void setProxy(boolean isProxy) {
			this.isProxy = isProxy;
		}
		public int getNodeId() {
			return nodeId;
		}
		public void setNodeId(int nodeId) {
			this.nodeId = nodeId;
		}
		public boolean isShareFolder() {
			return isShareFolder;
		}
		public void setShareFolder(boolean isShareFolder) {
			this.isShareFolder = isShareFolder;
		}
		public String getDestination() {
			return destination;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getUserPwd() {
			return userPwd;
		}
		public void setUserPwd(String userPwd) {
			this.userPwd = userPwd;
		}		
	}
	
	private class RPSDataStoreVolumeSize {
		private int rpsNodeId;
		private long capacitySpace;
		private long usedSpace;
		
		public RPSDataStoreVolumeSize(int rpsNodeId, long capacitySpace, long usedSpace){
			this.rpsNodeId = rpsNodeId;
			this.capacitySpace = capacitySpace;
			this.usedSpace = usedSpace;
		}
		public int getRpsNodeId() {
			return rpsNodeId;
		}
		public void setRpsNodeId(int rpsNodeId) {
			this.rpsNodeId = rpsNodeId;
		}
		public long getCapacitySpace() {
			return capacitySpace;
		}
		public void setCapacitySpace(long capacitySpace) {
			this.capacitySpace = capacitySpace;
		}
		public long getUsedSpace() {
			return usedSpace;
		}
		public void setUsedSpace(long usedSpace) {
			this.usedSpace = usedSpace;
		}
		
	}	
	private static class PlanNodeInformation {
		private int nodeId = -1;
		private int planId = -1;
		private String destination;

		//0 share folder, 1 rps
		private DestProviderType providerType; 

		public PlanNodeInformation( int planId, int nodeId ) { 
			this.planId = planId;
			this.nodeId = nodeId;
		}

		public PlanNodeInformation( int planId, DestProviderType providerType, int nodeId ) { 
			this.planId = planId;
			this.providerType = providerType;
			this.nodeId = nodeId;
		}
		
		public int getNodeId() {
			return nodeId;
		}

		public int getPlanId() {
			return planId;
		}
		public DestProviderType getProviderType() {
			return providerType;
		}
		
		public void setProviderType(DestProviderType providerType){
			this.providerType = providerType;
		}
		
		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}
	}
	
	
	public static enum DestProviderType {
		Linux, Agent, HBBU, RPS, Sharefolder;

	}
	private static class SyncProvider {
		
		private long nodeId;
		private DestProviderType providerType;
		public SyncProvider( long connectHostId , DestProviderType providerType ) {
			this.nodeId = connectHostId;
			this.providerType = providerType;
		}
		public void setNodeId(long nodeId) {
			this.nodeId = nodeId;
		}
		public void setProviderType(DestProviderType providerType) {
			this.providerType = providerType;
		}
		public long getNodeId() {
			return nodeId;
		}
		public DestProviderType getProviderType() {
			return providerType;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
			result = prime * result
					+ ((providerType == null) ? 0 : providerType.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SyncProvider other = (SyncProvider) obj;
			if (nodeId != other.nodeId)
				return false;
			if (providerType != other.providerType)
				return false;
			return true;
		}
	}
	

	public static class RPMKey {

		private String protectedNodeUUID; 
		private long protectedNodeLocalId;
		private String destIdentify;
		private String protectedNodeName;
		
		private SyncProvider destinationProvider;

		public RPMKey( String protectedNodeUUID,  String destIdentify, SyncProvider destProvider  ) {
			this.protectedNodeUUID = protectedNodeUUID;

			this.destIdentify = destIdentify;
			this.destinationProvider = destProvider;
		}
		
		public String getProtectedNodeName() {
			return protectedNodeName;
		}
		public void setProtectedNodeName(String protectedNodeName) {
			this.protectedNodeName = protectedNodeName;
		}

		public String getProtectedNodeUUID() {
			return protectedNodeUUID;
		}
		public void setProtectedNodeUUID(String protectedNodeUUID) {
			this.protectedNodeUUID = protectedNodeUUID;
		}
		public String getDestIdentify() {
			return destIdentify;
		}
		public void setDestIdentify(String destIdentify) {
			this.destIdentify = destIdentify;
		}
	
		public long getProtectedNodeLocalId() {
			return protectedNodeLocalId;
		}
		public void setProtectedNodeLocalId(long protectedNodeLocalId) {
			this.protectedNodeLocalId = protectedNodeLocalId;
		}
		public SyncProvider getDestinationProvider() {
			return destinationProvider;
		}

		public void setDestinationProvider(SyncProvider destinationProvider) {
			this.destinationProvider = destinationProvider;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((destIdentify == null) ? 0 : destIdentify.hashCode());
			result = prime
					* result
					+ ((destinationProvider == null) ? 0 : destinationProvider
							.hashCode());
			result = prime
					* result
					+ (int) (protectedNodeLocalId ^ (protectedNodeLocalId >>> 32));
			result = prime
					* result
					+ ((protectedNodeUUID == null) ? 0 : protectedNodeUUID
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RPMKey other = (RPMKey) obj;
			if (destIdentify == null) {
				if (other.destIdentify != null)
					return false;
			} else if (!destIdentify.equals(other.destIdentify))
				return false;
			if (destinationProvider == null) {
				if (other.destinationProvider != null)
					return false;
			} else if (!destinationProvider.equals(other.destinationProvider))
				return false;
			if (protectedNodeLocalId != other.protectedNodeLocalId)
				return false;
			if (protectedNodeUUID == null) {
				if (other.protectedNodeUUID != null)
					return false;
			} else if (!protectedNodeUUID.equals(other.protectedNodeUUID))
				return false;
			return true;
		}
	}
	
	public static class RPMSummaryData {

		private  long rawSize;
		private long backupDataSize;
		private long restorableDataSize;
		private String dest;
		private int deviceType;
		
		public RPMSummaryData( long rawSize, long backupSize, String dest, int deviceType , long restorableDataSize){
			this.rawSize = rawSize;
			this.backupDataSize = backupSize;
			this.restorableDataSize = restorableDataSize;
			this.dest = dest;
			this.deviceType = deviceType;
		}
		
		public long getRawSize() {
			return rawSize;
		}

		public void setRawSize(long rawSize) {
			this.rawSize = rawSize;
		}

		public long getBackupDataSize() {
			return backupDataSize;
		}

		public void setBackupDataSize(long backupDataSize) {
			this.backupDataSize = backupDataSize;
		}

		public String getDest() {
			return dest;
		}

		public void setDest(String dest) {
			this.dest = dest;
		}

		public int getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(int deviceType) {
			this.deviceType = deviceType;
		}
		
		public long getRestorableDataSize() {
			return restorableDataSize;
		}

		public void setRestorableDataSize(long restorableDataSize) {
			this.restorableDataSize = restorableDataSize;
		}
	}
}
