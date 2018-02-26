/**
 * 
 */
package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;

/**
 * @author lijwe02
 * 
 */
public interface IEdgeVSBDao {
	@StoredProcedure(name = "dbo.as_edge_vsb_converter_cu")
	void as_edge_vsb_converter_cu(
			int insertOnly,
			int hostId,
			@In(jdbcType = Types.VARCHAR) String hostName, 
			int port, int protocol,
			@In(jdbcType = Types.VARCHAR) String userName, 
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid, 
			@EncryptSave @In(jdbcType = Types.VARCHAR) String authUuid,
			@Out(jdbcType = Types.INTEGER) int[] id,
			@Out(jdbcType = Types.INTEGER) int[] insert);

	@StoredProcedure(name = "as_edge_vsb_converter_getByHostId")
	void as_edge_vsb_converter_getByHostId(
			int hostId, 
			@ResultSet List<EdgeVCMConnectInfo> converterList);
	
	@StoredProcedure(name = "as_edge_vsb_converter_getById")
	void as_edge_vsb_converter_getById(
			int id, 
			@ResultSet List<EdgeVCMConnectInfo> converterList);

	@StoredProcedure(name = "as_edge_host_converter_map_cu")
	void as_edge_host_converter_map_cu(int hostId, int converterId, int converterType, int taskType, int oldConverterId);

	@StoredProcedure(name = "as_edge_host_converter_map_d")
	void as_edge_host_converter_map_d(int hostId);

	@StoredProcedure(name = "dbo.as_edge_vsb_monitor_cu")
	void as_edge_vsb_monitor_cu(
			@In(jdbcType = Types.VARCHAR) String hostName, 
			int port, int protocol,
			@In(jdbcType = Types.VARCHAR) String userName, 
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid, 
			@EncryptSave @In(jdbcType = Types.VARCHAR) String authUuid,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "as_edge_vsb_monitor_getByHostId")
	void as_edge_vsb_monitor_getByHostId(
			int hostId, 
			@ResultSet List<HostConnectInfo> monitorList);
	
	@StoredProcedure(name = "as_edge_vsb_is_converter")
	void as_edge_vsb_is_converter(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String hostUUID, 
			@ResultSet List<HostConnectInfo> converterList);
	
	@StoredProcedure(name = "as_edge_vsb_is_monitor")
	void as_edge_vsb_is_monitor(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String hostUUID, 
			@ResultSet List<HostConnectInfo> monitorList);

	@StoredProcedure(name = "as_edge_host_monitor_map_cu")
	void as_edge_host_monitor_map_cu(int hostId, int monitorId);

	@StoredProcedure(name = "as_edge_host_monitor_map_d")
	void as_edge_host_monitor_map_d(int hostId);
	
	@StoredProcedure(name = "as_edge_vsb_converter_getUsageCount")
	void as_edge_vsb_converter_getUsageCount(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid, 
			@Out(jdbcType = Types.INTEGER) int[] count);
	
	@StoredProcedure(name = "as_edge_host_vsb_status_cu")
	void as_edge_host_vsb_status_cu(
			int hostId,
			int overallStatus,
			@In(jdbcType = Types.TIMESTAMP) Date lastBackupStartTime,
			int lastBackupType,
			int lastBackupJobStatus,
			int lastBackupStatus,
			int recPointRetentionCount,
			int recPointCount,
			@In(jdbcType = Types.VARCHAR) String recPointMounted,
			int recPointStatus,
			boolean isUseBackupSets,
			@In(jdbcType = Types.VARCHAR) String destPath,
			int isDestAccessible,
			long destFreeSpace,
			int destEstimatedBackupCount,
			int destStatus,
			int isDriverInstalled,
			int isRestarted,
			int estimatedValue,
			int isBackupConfiged, 
			@In(jdbcType = Types.VARCHAR) String vmName, 
			int vmPowerStatus,
			int autoOfflieCopyStatus, 
			int heartbeatStatus, 
			@In(jdbcType = Types.VARCHAR) String recentSnapshot,
			long snapshotTimeZoneOffset,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String currentRunningSnapshot);
	
	@StoredProcedure(name = "as_edge_host_vsb_status_getByHostId")
	void as_edge_host_vsb_status_getByHostId(
			int hostId, 
			@ResultSet List<D2DStatusInfo> vsbStatusList);
	
	@StoredProcedure(name="as_edge_host_vsb_snapshot_cu")
	void as_edge_host_vsb_snapshot_cu(int hostId,
			@In(jdbcType = Types.VARCHAR) String snapGuid ,
			@In(jdbcType = Types.VARCHAR) String sessionName,
			@In(jdbcType = Types.VARCHAR) String sessionGuid,
			long timestamp,
			@In(jdbcType = Types.VARCHAR) String localTime,
			long snapNo,
			@In(jdbcType = Types.VARCHAR) String bootableSnapGuid,
			int powerOffBackup,
			@In(jdbcType = Types.VARCHAR) String desc,
			long timeZoneOffset);
	
	@StoredProcedure(name = "as_edge_host_vsb_snapshot_deleteByHostId")
	void as_edge_host_vsb_snapshot_deleteByHostId(int hostId);
	
	@StoredProcedure(name = "as_edge_host_vsb_snapshot_getByHostId")
	void as_edge_host_vsb_snapshot_getByHostId(
			int hostId, 
			@ResultSet List<VMSnapshotsInfo> snapshotList);
	
	@StoredProcedure(name = "as_edge_host_countWithVSBTask")
	void as_edge_host_countWithVSBTask(@Out(jdbcType = Types.INTEGER) int[] count);
	
	@StoredProcedure(name = "as_edge_vsb_converter_forNodesimportedFromRHA")
	void as_edge_vsb_converter_forNodesimportedFromRHA(
			@ResultSet List<EdgeVCMConnectInfo> converterList);
	
	@StoredProcedure(name = "as_edge_host_list_byConverterId")
	void as_edge_host_list_byConverterId(
			int converterId, 
			@ResultSet List<EdgeHost> hostList);
	
	@StoredProcedure(name = "as_edge_host_list_byMonitorId")
	void as_edge_host_list_byMonitorId(
			int monitorId,
			@ResultSet List<EdgeHost> hostList);
	
	@StoredProcedure(name = "as_edge_vsb_CleanUpUselessConverterMaps")
	void cleanUpUselessConverterMaps();
	
	@StoredProcedure(name = "as_edge_vsb_GetUselessConverters")
	void getUselessConverters(
		@ResultSet List<IntegerId> uselessConverterIdList
		);
	
	@StoredProcedure(name = "as_edge_vsb_DeleteConverter")
	void deleteConverter(
		int converterId
		);
}
