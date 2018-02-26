package com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors;

//import org.apache.log4j.Logger;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DJobStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VCMStorage;
import com.ca.arcflash.webservice.replication.LRUCache;
import com.ca.arcflash.webservice.service.HAService;

public class VCMStatusCollector extends BaseCollector
{
	private static final Logger logger = Logger.getLogger( VCMStatusCollector.class );
	private static VCMStatusCollector instance = null;
	
	// Copied from com.ca.arcflash.ui.client.model.BackupStatusModel.
	// It's defined in UI project, and cannot be used in back end.
	public class BackupStatusModel
	{
		public static final int Unknown		= 0;
		public static final int Finished	= 1;
		public static final int Failed		= 2;
		public static final int Active		= 3;
		public static final int Canceled	= 4;
		public static final int Crashed		= 5;
		public static final int All			= 6;
	}
	
	
	public LRUCache<String, Object> lruCache = new LRUCache<String, Object>(100, true);
	
	public static synchronized VCMStatusCollector getInstance()
	{
		if (instance == null)
			instance = new VCMStatusCollector();
		
		return instance;
	}

	@Override
	public D2DStatusInfo getStatusInfo( String uuid )
	{		
		try
		{
			D2DStatusInfo statusInfo = new D2DStatusInfo();
			statusInfo.setMonitorUUID(HAService.getInstance().retrieveCurrentNodeID());
			ProductionServerRoot serverRoot = null;
			SummaryModel summaryModel = HAService.getInstance().getProductionServerSummaryModelFromMonitor( uuid );
			if (summaryModel != null) {
				serverRoot = summaryModel.getServerRoot();
				getMostRecentConversionInfo( statusInfo, summaryModel, serverRoot );
				getRecoveryPointSnapshotsInfo( statusInfo, summaryModel, serverRoot );
				getSnapshots( statusInfo, summaryModel );
//				getDestinationInfo( statusInfo, summaryModel );
//				getVmRunningStatus(statusInfo, uuid, serverRoot, summaryModel.isHyperVModel());
			}
			
//			getVmName(statusInfo, uuid, serverRoot);
//			getAutoOfflieCopyStatusAndHeartbeatStatus(uuid, statusInfo);
			getOverallStatus( statusInfo );
			
			return statusInfo;
		}
		catch (Exception e)
		{
			logger.error( "Error getting VCM status info.", e );
			return D2DStatusInfo.NullObject;
		}
	}
	
	public D2DStatusInfo getVCMStatusInfo( String uuid )
	{		
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(uuid);
		if (failoverJobScript == null)
			return D2DStatusInfo.NullObject;
		try
		{
			D2DStatusInfo statusInfo = new D2DStatusInfo();
			statusInfo.setMonitorUUID(HAService.getInstance().retrieveCurrentNodeID());
					
			ProductionServerRoot serverRoot = null;
			SummaryModel summaryModel = HAService.getInstance().getProductionServerSummaryModelFromMonitor( uuid );
			if (summaryModel != null) {
				serverRoot = summaryModel.getServerRoot();
				getMostRecentConversionInfo( statusInfo, summaryModel, serverRoot );
//				getRecoveryPointSnapshotsInfo( statusInfo, summaryModel, serverRoot );
//				getDestinationInfo( statusInfo, summaryModel );
			}
			
//			String vmUuid = null;
//			HeartBeatModel heartBeatModel = HeartBeatModelManager.getHeartBeatModel();
//			Map<String, ARCFlashNode> nodeMap = heartBeatModel.getMonitoredARCFlashNodesMap();
//			ARCFlashNode node = nodeMap.get(uuid);
//			VirtualMachineInfo vmInfo = node.getVmInfo();
//			if (vmInfo != null)
//				vmUuid = vmInfo.getVmGUID();
			
			if (!HAService.getInstance().getVSBStatus(statusInfo, summaryModel, serverRoot, uuid))
				return D2DStatusInfo.NullObject;
			
//			getVmName(statusInfo, uuid, serverRoot);
//			getSnapshots(statusInfo, uuid, vmUuid, statusInfo.getVmName());
//			getVmRunningStatus(statusInfo, uuid, vmUuid, summaryModel.isHyperVModel());
//			getAutoOfflieCopyStatusAndHeartbeatStatus(uuid, statusInfo);
			getOverallStatus( statusInfo );
			
			return statusInfo;
		}
		catch (Exception e)
		{
			logger.error( "Error getting VCM status info for node " + uuid, e );
			return D2DStatusInfo.NullObject;
		}
	}
	
	private void getMostRecentConversionInfo(
		D2DStatusInfo statusInfo, SummaryModel summaryModel, ProductionServerRoot serverRoot )
	{
		statusInfo.setLastBackupStatus( D2DStatus.Error );
		statusInfo.setLastBackupJobStatus( D2DJobStatus.Unknown );
		
		if ((summaryModel == null) || (serverRoot == null))
			return;
		
		if((serverRoot.getReplicaRoot() != null) &&
			(serverRoot.getReplicaRoot().getReplicaTime() > serverRoot.getMostRecentRepTimeMilli()))
		{
			long repliTime = serverRoot.getReplicaRoot().getReplicaTime();
			if((repliTime > 0) && (summaryModel.getSnapshots() != null) && (summaryModel.getSnapshots().size() > 0))
			{
				Date date = new Date( repliTime );
				statusInfo.setLastBackupStartTime( date );
				statusInfo.setLastBackupStatus( D2DStatus.OK );
				statusInfo.setLastBackupJobStatus( D2DJobStatus.Finished );
			}
		}
		else if (serverRoot.getMostRecentRepTimeMilli() > 0)
		{
			long repliTime = serverRoot.getMostRecentRepTimeMilli();
			Date date = new Date( repliTime );
			statusInfo.setLastBackupStartTime( date );
			
			int status = serverRoot.getMostRecentRepStatus();
			statusInfo.setLastBackupJobStatus( convertVCMJobStatusValueToEnum( status ) );
			switch (status)
			{
			case BackupStatusModel.Finished:
				statusInfo.setLastBackupStatus( D2DStatus.OK );
				break;
				
			case BackupStatusModel.Canceled:
				statusInfo.setLastBackupStatus( D2DStatus.Warning );
				break;
				
			default:
				statusInfo.setLastBackupStatus( D2DStatus.Error );
			}
		}
		statusInfo.setMostRecentRepDuration(serverRoot.getMostRecentRepDuration());
	}
	
	private D2DJobStatus convertVCMJobStatusValueToEnum( int statusValue )
	{
		switch (statusValue)
		{
		case BackupStatusModel.Finished:
			return D2DJobStatus.Finished;
			
		case BackupStatusModel.Failed:
			return D2DJobStatus.Failed;
			
		case BackupStatusModel.Active:
			return D2DJobStatus.Active;
			
		case BackupStatusModel.Canceled:
			return D2DJobStatus.Canceled;
			
		case BackupStatusModel.Crashed:
			return D2DJobStatus.Crashed;
		}
		
		return D2DJobStatus.Unknown;
	}
	
	private void getRecoveryPointSnapshotsInfo(
		D2DStatusInfo statusInfo, SummaryModel summaryModel, ProductionServerRoot serverRoot )
	{
		statusInfo.setRecoveryPointStatus( D2DStatus.Error );
		statusInfo.setRecoveryPointCount( -1 );
		statusInfo.setRecoveryPointRetentionCount( -1 );
		
		if (summaryModel == null)
			return;
		
		List<VMSnapshotsInfo> snapShotList = summaryModel.getSnapshots();
		if (snapShotList != null)
		{
			int snapshotCount = snapShotList.size();
			int retentionCount = (serverRoot != null) ? serverRoot.getRetentionCount() : 0;
			statusInfo.setRecoveryPointCount( snapshotCount );
			statusInfo.setRecoveryPointRetentionCount( retentionCount );
			
			if(snapshotCount <= 0)
				statusInfo.setRecoveryPointStatus( D2DStatus.Error );
			
			else if ((serverRoot == null) || (snapshotCount < serverRoot.getRetentionCount()))
				statusInfo.setRecoveryPointStatus( D2DStatus.Warning );
			
			else
				statusInfo.setRecoveryPointStatus( D2DStatus.OK );
		}
	}
	
	private void getDestinationInfo(
		D2DStatusInfo statusInfo, SummaryModel summaryModel )
	{
		statusInfo.setDestinationStatus( D2DStatus.Error );
		
		if (summaryModel == null)
			return;
		
		List<VMStorage> vmStorageList = summaryModel.getStorages();
		if ((vmStorageList != null) && (vmStorageList.size() > 0))
		{
			D2DStatus destStatus = D2DStatus.OK;
			
			List<VCMStorage> vcmStorageList = new LinkedList<VCMStorage>();
			for (VMStorage storage : vmStorageList)
			{
				long freeSize = storage.getFreeSize();
				if (freeSize == 0)
					destStatus = D2DStatus.Warning;
				
				String name = storage.getName();
				name = getShowName( summaryModel.isHyperVModel(), name );
				
				VCMStorage vcmStorage = new VCMStorage();
				vcmStorage.setName( name );
				vcmStorage.setFreeSize( freeSize );
				vcmStorage.setColdStandSize(storage.getColdStandySize());
				vcmStorage.setTotalSize(storage.getTotalSize());
				vcmStorage.setOtherSize(storage.getOtherSize());
				vcmStorageList.add( vcmStorage );
			}
			statusInfo.setDestinationVCMStorages( vcmStorageList.toArray( new VCMStorage[0] ) );
			
			statusInfo.setDestinationStatus( destStatus );
		}
	}
	
	private String getShowName( boolean hyperVModel, String name )
	{
		if(hyperVModel && name != null && name.length() > 1 && name.charAt(1) == ':')
		{
			return name.charAt(0) + ":\\";
		}
			
		return name;
	}
	
	private void getOverallStatus( D2DStatusInfo statusInfo )
	{
		D2DStatus overallStatus = D2DStatus.OK;
		
		if ((statusInfo.getLastBackupStatus() == D2DStatus.Error) ||
			(statusInfo.getRecoveryPointStatus() == D2DStatus.Error) ||
			(statusInfo.getDestinationStatus() == D2DStatus.Error))
			overallStatus = D2DStatus.Error;
		
		else if ((statusInfo.getLastBackupStatus() == D2DStatus.Warning) ||
			(statusInfo.getRecoveryPointStatus() == D2DStatus.Warning) ||
			(statusInfo.getDestinationStatus() == D2DStatus.Warning))
			overallStatus = D2DStatus.Warning;
		
		statusInfo.setOverallStatus( overallStatus );
	}
	
//	private void getVmName( D2DStatusInfo statusInfo, String uuid, ProductionServerRoot serverRoot )
//	{
//		statusInfo.setVmName(HAService.getInstance().getVmName(uuid, serverRoot));
//	}
//	
//	private void getAutoOfflieCopyStatusAndHeartbeatStatus( String uuid, D2DStatusInfo statusInfo )
//	{
//		Integer[] status = HAService.getInstance().getAutoOfflieCopyStatusAndHeartbeatStatus(uuid);
//		statusInfo.setHeartbeatStatus(status[0]);
//		statusInfo.setAutoOfflieCopy(status[1]);
//	}
	
	private void getSnapshots(D2DStatusInfo statusInfo, SummaryModel summaryModel)
	{
		if (summaryModel != null) {
			List<VMSnapshotsInfo> snapshots = summaryModel.getSnapshots();
			if (snapshots != null)
				statusInfo.setSnapshots(snapshots.toArray(new VMSnapshotsInfo[0]));
		}
	}
	
//	private void getSnapshots(D2DStatusInfo statusInfo, String uuid, String vmGuid, String vmName)
//	{
//		VMSnapshotsInfo[] vmSnapshots = HAService.getInstance().getVMSnapshots(uuid, vmGuid, vmName);
//		statusInfo.setSnapshots(vmSnapshots);
//	}
//	
//	private void getVmRunningStatus(D2DStatusInfo statusInfo, String AFGuid, String vmUuid, boolean isHyperVModel)
//	{
//		statusInfo.setVmStatus(HAService.getInstance().getVmPowerState(vmUuid, AFGuid, statusInfo.getVmName(), isHyperVModel));
//		if (statusInfo.getVmStatus() != VMPowerStatus.power_on)  // VM is not running, return.
//			return;
//		
//		try {
//			statusInfo.setCurrentRunningSnapshot(HAService.getInstance().getCurrentRunningSnapShotGuid(AFGuid));
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
	
	public Object lookupLRUCache(String key)
	{
		return lruCache.get(key);
	}
	
	public void putLRUCache(String key, Object obj)
	{
		lruCache.put(key, obj);
	}
	
	public void clearLRUCache()
	{
		lruCache.clear();
	}
}
