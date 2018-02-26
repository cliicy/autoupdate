package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VCMStorage;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;

public class D2DStatusCommonService {
	
	private static Logger log = Logger.getLogger(D2DStatusCommonService.class);
	private static D2DStatusCommonService statusService = new D2DStatusCommonService();
	
	private D2DStatusCommonService() {
		
	}
	
	public static D2DStatusCommonService getD2DStatusCommonServiceInstance() {
		return statusService;
	}
	
	public void saveD2DStatusInfo( int hostId, D2DStatusInfo statusInfo )
	{
		try
		{
			if (statusInfo == null)
				throw new IllegalArgumentException( "statusInfo is null" );
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
			hostMgrDao.setD2DStatusInfo(
					hostId,
					statusInfo.getOverallStatus().ordinal(),
					statusInfo.getLastBackupStartTime(),
					statusInfo.getLastBackupType().ordinal(),
					statusInfo.getLastBackupJobStatus().ordinal(),
					statusInfo.getLastBackupStatus().ordinal(),
					statusInfo.getRecoveryPointRetentionCount(),
					statusInfo.getRecoveryPointCount(),
					statusInfo.getRecoveryPointMounted(),
					statusInfo.getRecoveryPointStatus().ordinal(),
					statusInfo.getIsUseBackupSets(),
					statusInfo.getDestinationPath(),
					statusInfo.isDestinationAccessible() ? 1 : 0,
							statusInfo.getDestinationFreeSpace(),
							statusInfo.getDestinationEstimatedBackupCount(),
							statusInfo.getDestinationStatus().ordinal(),
							statusInfo.isDriverInstalled() ? 1 : 0,
									statusInfo.isRestarted() ? 1 : 0,
											statusInfo.getEstimatedValue().ordinal(),
											statusInfo.isBackupConfiged() ? 1 : 0
			);
		}
		catch (Exception e)
		{
			log.error( "Error writing D2D status info into database. Host Id: " + hostId, e );
		}
	}
	
	public void saveVSBStatusInfo(int hostId, D2DStatusInfo statusInfo) {
		try {
			if (statusInfo == null) {
				return;
			}
			IEdgeVSBDao vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
			List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
			vsbDao.as_edge_vsb_converter_getByHostId(hostId, converterList);
			if (converterList == null || converterList.size() <= 0) {
				log.error("The host has no converter informatin, skip to save the vsb status.");
				return;
			}
			List<HostConnectInfo> monitorList = new ArrayList<HostConnectInfo>();
			vsbDao.as_edge_vsb_monitor_getByHostId(hostId, monitorList);
			if (monitorList == null || monitorList.size() <= 0) {
				log.error("The host has no monitor information, skip to save the vsb status.");
				return;
			}
			// Monitor exists, then we will check monitor uuid
			String monitorUuid = monitorList.get(0).getUuid();
			if (monitorUuid != null && monitorUuid.length() > 0) {
				String sendUuid = statusInfo.getMonitorUUID();
				if (!monitorUuid.equals(sendUuid)) {
					log.error("The uuid of the sender and the uuid of monitor is not equal, skip to save the vsb status.");
					return;
				}
			}
			String recentSnapshotTime = "";
			long snapshotTimeZoneOffset = 0;
			VMSnapshotsInfo[] snapshots = statusInfo.getSnapshots();
			if (snapshots != null && snapshots.length > 0) {
				Arrays.sort(snapshots, new Comparator<VMSnapshotsInfo>() {

					@Override
					public int compare(VMSnapshotsInfo o1, VMSnapshotsInfo o2) {
						// Sort snapshot by timestamp in DESC order, so the latest snapshot at first
						return (int) (o2.getTimestamp() - o1.getTimestamp());
					}
				});
				VMSnapshotsInfo recentSnapshot = snapshots[0];
				recentSnapshotTime = String.valueOf(recentSnapshot.getTimestamp());
				snapshotTimeZoneOffset = recentSnapshot.getTimeZoneOffset();
			}
			vsbDao.as_edge_host_vsb_status_cu(hostId, statusInfo.getOverallStatus().ordinal(), statusInfo
					.getLastBackupStartTime(), statusInfo.getLastBackupType().ordinal(), statusInfo
					.getLastBackupJobStatus().ordinal(), statusInfo.getLastBackupStatus().ordinal(), statusInfo
					.getRecoveryPointRetentionCount(), statusInfo.getRecoveryPointCount(), statusInfo
					.getRecoveryPointMounted(), statusInfo.getRecoveryPointStatus().ordinal(), statusInfo
					.getIsUseBackupSets(), statusInfo.getDestinationPath(), statusInfo.isDestinationAccessible() ? 1
					: 0, statusInfo.getDestinationFreeSpace(), statusInfo.getDestinationEstimatedBackupCount(),
					statusInfo.getDestinationStatus().ordinal(), statusInfo.isDriverInstalled() ? 1 : 0, statusInfo
							.isRestarted() ? 1 : 0, statusInfo.getEstimatedValue().ordinal(), statusInfo
							.isBackupConfiged() ? 1 : 0, statusInfo.getVmName(), statusInfo.getVmStatus().ordinal(),
					statusInfo.getAutoOfflieCopy(), statusInfo.getHeartbeatStatus(), recentSnapshotTime,
					snapshotTimeZoneOffset, statusInfo.getCurrentRunningSnapshot());

			// Save snapshot list
			vsbDao.as_edge_host_vsb_snapshot_deleteByHostId(hostId);
			if (snapshots != null) {
				for (VMSnapshotsInfo snapshot : snapshots) {
					vsbDao.as_edge_host_vsb_snapshot_cu(hostId, snapshot.getSnapGuid(), snapshot.getSessionName(),
							snapshot.getSessionGuid(), snapshot.getTimestamp(), snapshot.getLocalTime(),
							snapshot.getSnapNo(), snapshot.getBootableSnapGuid(), snapshot.isPowerOffBackup() ? 1 : 0,
							snapshot.getDesc(), snapshot.getTimeZoneOffset());
				}
			}

			// Save VCM Storage information
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostMgrDao.clearVCMStorages(hostId);

			int no = 0;
			for (VCMStorage storage : statusInfo.getDestinationVCMStorages()) {
				hostMgrDao.insertVCMStorage(hostId, no, storage.getName(), storage.getFreeSize(),storage.getColdStandSize(),storage.getTotalSize(),storage.getOtherSize());
				no++;
			}
		} catch (Exception e) {
			log.error("Failed to save VSB status.", e);
		}
	}
	
}
