package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.RecoveryPointDataItem;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHostBackupStats;

public interface IEdgeJobHistoryDao {
	
	/**
	 * consistent with the definition in C++
	 * Identify if this is a D2D job or RPS job.  1 - D2D; 3 - RPS; 5 - Linux D2D
	 */
	public static enum JobHistoryProductType {
		D2D(1), RPS(3), LinuxD2D(5), ASBU(4);
		private final int value;
		
		JobHistoryProductType(int value){
			this.value = value;
		}
		public int getValue(){
			return this.value;
		}
		
		public static JobHistoryProductType parse(int value){
			switch (value) {
			case 1:
				return D2D;
			case 3:
				return RPS;
			case 4:
				return ASBU;
			case 5:
				return LinuxD2D;
			default:
				return null;
			}
		}
	}
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_add")
	void as_edge_d2dJobHistory_add(@In(jdbcType = Types.VARCHAR) String version, long jobId, long jobType, long jobMethod, long jobStatus, 
			Date jobUTCStartDate, Date jobLocalStartDate,
			Date jobUTCEndDate, Date jobLocalEndDate,
			int serverId,
			int agentId,
			int sourceRPSId,
			int targetRPSId,
			@In(jdbcType = Types.VARCHAR)String sourceDataStoreUUID, 
			@In(jdbcType = Types.VARCHAR)String targetDataStoreUUID,
			@In(jdbcType = Types.VARCHAR)String planUUID,
			@In(jdbcType = Types.VARCHAR)String targetPlanUUID,
			int productType,
			int isMsp,
			@Out long[] output_identity );
	@StoredProcedure(name = "as_edge_d2dJobHistory_getJobHistory")
	void as_edge_d2dJobHistory_getJobHistory(long jobStatus,
			Date datetime,
			int startIndex, int count, 
			@In(jdbcType = Types.VARCHAR) String sortOrder,
			@In(jdbcType = Types.VARCHAR) String sortCol,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeJobHistory> jobHistroy) ;
	
	// Get all plan's recent events for dashboard
	@StoredProcedure(name = "as_edge_d2dJobHistory_getLastJobHistory")
	void as_edge_d2dJobHistory_getLastJobHistory(long jobStatus,
			int startIndex, int count, 
			@In(jdbcType = Types.VARCHAR) String sortOrder,
			@In(jdbcType = Types.VARCHAR) String sortCol,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeJobHistory> jobHistroy) ;
	
	/**
	 * Get D2D/RPS job history of the specified node id.
	 * @param productType D2D or RPS product type.
	 * @param refer to as_edge_host.rhostid
	 * @param jobId ignored when -1.
	 * @param startIndex start from 0.
	 * @param count paging count
	 * @param totalCount total logs
	 * @param logs paging logs
	 */
	@StoredProcedure(name = "as_edge_d2dJobHistory_getPagingList")
	void as_edge_d2dJobHistory_getPagingList(int productType, int targetOrServerId,int serverId, long jobId, long jobStatus, 
			@In(jdbcType = Types.VARCHAR)String dataStoreUUID,
			int startIndex, int count, 
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeJobHistory> jobHistroy);

	@StoredProcedure(name = "as_edge_d2dJobHistory_deleteAll")
	void as_edge_d2dJobHistory_deleteAll(int productType, int nodeId);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_deleteOld")
	void as_edge_d2dJobHistory_deleteOld(int productType, int nodeId, Date date);

	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_delete")
	void as_edge_d2dJobHistory_monitor_delete(long jobId, long jobType, long nodeId, long serverId);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_insert")
	void as_edge_d2dJobHistory_monitor_insert(long jobId, long jobType, long jobMethod, long jobStatus, long nodeId, long serverId, long sourceRpsNodeId, long targetRpsNodeId, Date datetime, int productType, long id, String planUUID, String targetPlanUUID
											, String jobUUID, @In(jdbcType = Types.VARCHAR) String agentNodeName, @In(jdbcType = Types.VARCHAR) String serverNodeName, @In(jdbcType = Types.VARCHAR) String uuid);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_delete_all")
	void as_edge_d2dJobHistory_monitor_delete_all();
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getPlans")
	void as_edge_d2dJobHistory_getPlans(int group, Date time, @ResultSet List<EdgeJobHistory> plans);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getLastPlans")
	void as_edge_d2dJobHistory_getLastPlans(int group, Date time, @ResultSet List<EdgeJobHistory> plans);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_isOnDemandJobExists")
	void as_edge_d2dJobHistory_isOnDemandJobExists(int group, boolean recent, Date time, @Out boolean[] exist);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getJobTypes")
	void as_edge_d2dJobHistory_getJobTypes(int group, Date time, String planUuid, @ResultSet List<EdgeJobHistory> jobTypes);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getLastJobTypes")
	void as_edge_d2dJobHistory_getLastJobTypes(int group, Date time, String planUuid, @ResultSet List<EdgeJobHistory> jobTypes);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getOnDemandJobTypes")
	void as_edge_d2dJobHistory_getOnDemandJobTypes(int group, boolean recent, Date time, @ResultSet List<EdgeJobHistory> jobTypes);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getJobHistories")
	void as_edge_d2dJobHistory_getJobHistories(int group, Date time, String planUuid, long jobType, int startIndex, int count, int orderType, String sortCol, @Out(jdbcType = Types.INTEGER) int[] totalCount, @ResultSet List<EdgeJobHistory> plans);
	
	// Get recent events for dashboard group by plan
	@StoredProcedure(name = "as_edge_d2dJobHistory_getLastJobHistories")
	void as_edge_d2dJobHistory_getLastJobHistories(int group, String planUuid, long jobType, int startIndex, int count, int orderType, String sortCol, @Out(jdbcType = Types.INTEGER) int[] totalCount, @ResultSet List<EdgeJobHistory> plans);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_getOnDemandJobHistories")
	void as_edge_d2dJobHistory_getOnDemandJobHistories(int group, boolean recent, Date time, long jobType, int startIndex, int count, int orderType, String sortCol, @Out(jdbcType = Types.INTEGER) int[] totalCount, @ResultSet List<EdgeJobHistory> jobHistories);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_select")
	void as_edge_d2dJobHistory_monitor_select(long jobId, long jobType, int nodeId, long serverId, @Out long[] id);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_select_by_type_nodeId")
	void selectJobMonitorByJobTypeAndNodeId(long jobType, long nodeId, @ResultSet List<JobMonitor> monitors);
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_select_by_type_serverId")
	void selectJobMonitorByJobTypeAndServerId(long jobType, long serverId, @ResultSet List<JobMonitor> monitors);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_select_by_type")
	void selectJobMonitorByJobType(long jobType, @ResultSet List<EdgeJobHistory> monitors);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_getJobMonitor")
	void as_edge_d2dJobHistory_monitor_getJobMonitor(long agentId, long serverId,String dataStoreUUID, @ResultSet List<EdgeJobHistory> monitors);

	@StoredProcedure(name = "as_edge_d2dJobHistory_monitor_getJobMonitorByHistoy")
	void as_edge_d2dJobHistory_monitor_getJobMonitorByHistoy(long jobType, long jobId,long serverId,long agentId , @ResultSet List<EdgeJobHistory> monitors);
	
	@StoredProcedure(name = "as_edge_d2dJobHistory_purge")
	void as_edge_d2dJobHistory_purge(Date retentionDate);
	
	
	@StoredProcedure( name="as_edge_jobhistory_detail_add" )
	void as_edge_d2dJobHistoryDetail_add( long jobHistoryId,	long protectedDataSize,long rawDataSize,long backupedDataSize, long syncReadSixe, long ntfsVolumeSize, long virtualDiskProvisionSize,
			@In(jdbcType = Types.VARCHAR)String backupDestination, @In(jdbcType = Types.VARCHAR)String sessionIds, int bmrFlag, int sessionEncrypted, int recoveryPointType, 
			@In(jdbcType = Types.VARCHAR)String hyperVisorName, @In(jdbcType = Types.VARCHAR)String vCenterName,int vmType, @In(jdbcType = Types.VARCHAR)String conversionProxyName );
	

//	@StoredProcedure( name="as_edge_handle_recovery_point" )
//	void as_edge_handle_recovery_point( int operation, long jobHistoryId, @In(jdbcType = Types.VARCHAR)String sessguid,
//			long rawDataSize,long backupDataSize, @In(jdbcType = Types.VARCHAR)String backupDestination );
	
	@StoredProcedure( name="as_edge_handle_recovery_point_summarydata" )
	void as_edge_handle_recovery_point_summarydata( long agentId, String AgentName, @In(jdbcType = Types.VARCHAR)String deviceId,long desintaionProvider, int providerType,
			long rawDataSize,long backupDataSize, long restorableDataSize, @In(jdbcType = Types.VARCHAR)String backupDestination, int deviceType );
	
	
	@StoredProcedure( name="as_edge_recovery_point_summary_delete" )
	void as_edge_recovery_point_summary_delete( long desintaionProvider, int providerType, 
			@In(jdbcType = Types.TIMESTAMP) Date keepDateFrom,
			@In(jdbcType = Types.TIMESTAMP) Date keepDateTo );

	@StoredProcedure( name="as_edge_recovery_point_rps_volumesize_delete" )
	void as_edge_recovery_point_rps_volumesize_delete( long rpsNodeId);

	
	@StoredProcedure( name="as_edge_handle_recovery_point_rps_volumesize" )
	void as_edge_handle_recovery_point_rps_volumesize( long rpsNodeId, long maxDataSize,long usedDataSize);	
	
	@StoredProcedure( name="as_edge_getRpsVolume_MaxSize" )
	void as_edge_getRpsVolume_MaxSize(@Out long[] maxSize );
	
	@StoredProcedure(name = "as_edge_get_backup_stats")
	void as_edge_get_backup_stats(int timeOffset,
			@ResultSet List<EdgeHostBackupStats> statsList);
	
	@StoredProcedure(name = "as_edge_job_monitor_get_serverId_by_jobType_and_nodeId")
	void getJobMonitorServerIdByJobTypeAndNodeId(long jobType, int nodeId, @ResultSet List<IntegerId> idList);
	
	@StoredProcedure(name="as_edge_getRecoveryPointDatas")
	void getRecoveryPointDatas(
			@In(jdbcType = Types.TIMESTAMP) Date startDate, 
			@In(jdbcType = Types.TIMESTAMP) Date endDate,
			int timezone,
			@ResultSet List<RecoveryPointDataItem> datas);
	
	@StoredProcedure(name="as_edge_jobSchedule_add")
	void addJobSchedule(int nodeId, int serverId, long jobType, long scheduleUTCTime, long scheduleUTCTimeZone);

	@StoredProcedure(name = "dbo.as_edge_backupData_of_d2d_collect")
	void getD2DBackupData(int timeOffSet,
			@ResultSet List<RecoveryPointDataItem> datas);
}


