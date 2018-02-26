package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps.RpsBaseSynchronizer;

public class D2DBackupDataSynchronizer {
	static final String SQLStmt_Insert_Edge_Sync_History = "INSERT INTO dbo.as_edge_sync_history (last_cache_id, status, componentid, branchid) values (?, ?, ?, ?)";
	static final String SQLStmt_Update_Sync_Status = "UPDATE dbo.as_edge_sync_history SET last_cache_id = ?, status = ?, last_update = getutcdate() WHERE branchid = ? AND componentid = ?";
	static final String SQLStmt_Get_Sync_Status = "SELECT last_cache_id from dbo.as_edge_sync_history WHERE branchid = ? AND componentid = ?";
	static final String SQLStmt_Insert_d2d_dest_path = 
		"INSERT INTO sync_d2d_dest_path (path,capacity,backup_used,others_used,update_time, branchid)" 
		+ " VALUES (?, ? ,? ,?,Getutcdate(),?)";
	static final String SQLStmt_Get_d2d_dest_path_id = "SELECT id from dbo.sync_d2d_dest_path WHERE path = ?";
	
	static final String SQLStmt_Insert_d2d_job = 
		"INSERT INTO dbo.sync_d2d_job(job_id, sess_id, sessGuid, uniqueID, display_name,bk_type,compress_type,data_sizeKB,trans_data_sizeKB , protected_data_sizeB , "
		+ "catalog_sizeKB,status,dest_path_id,backupDest,recover_point,update_time,encrypt_type, encrypt_password_hash,"
		+ "TotalRawDataSizeWritten,BMRFlag, appType,branchid)"
		+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,Getutcdate(),?,?,?,?,1,?) ";
	static final String SQLStmt_Get_job_internal_id = "SELECT id FROM dbo.sync_d2d_job WHERE job_id = ? AND uniqueID = ? AND branchid = ?";
	static final String SQLStmt_Delete_job = "DELETE FROM dbo.sync_d2d_job WHERE job_id=? AND uniqueID = ? AND branchid=?";
	
	static final String SQLStmt_Insert_d2d_session = 
		"INSERT INTO dbo.sync_d2d_session (sub_sessid,job_internal_id,type,flags,display_name,mount_point"
		+ ",guid,vol_dat_sizeB,catalog_file,isBootVolume, isSystemVolume, update_time,branchid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, Getutcdate(), ?) ";
	static final String SQLStmt_Get_session_id = "SELECT id FROM dbo.sync_d2d_session WHERE sub_sessid = ? AND job_internal_id = ? AND branchid = ?";
	static final String SQLStmt_Delete_Session=
		"DELETE FROM dbo.sync_d2d_session WHERE sub_sessid=? AND job_internal_id=? AND branchid=?";
	static final String SQLStmt_Delete_Session_By_Job=
		"DELETE FROM dbo.sync_d2d_session WHERE branchid=? AND job_internal_id IN (SELECT id FROM dbo.sync_d2d_job WHERE job_id=? AND uniqueID = ? AND branchid=?)";
	static final String SQLStmt_Get_Sess_Cnt_By_Job=
		"SELECT count(*) AS cnt FROM dbo.sync_d2d_session WHERE branchid=? AND job_internal_id IN (SELECT id FROM dbo.sync_d2d_job WHERE job_id=? AND uniqueID = ? AND branchid=?)";
	
	public static final int SYNC_STATUS_SUCCEED = 0;
	public static final int SYNC_STATUS_FAILED = 1;
	public static final int SYNC_STATUS_IN_PROGRESS = 2;
	
	private static final Logger logger = Logger.getLogger(D2DBackupDataSynchronizer.class);
	
	EdgeDaoCommonExecuter ede = null;
	
	public void connect()
	{
		logger.debug("D2DBackupDataSynchronizer.connect() enter...");
		
		if(ede == null) {
			ede = DataBaseConnectionFactory.getInstance().createEdgeDaoCommonExecuter();
		}
	}
	
	public boolean begin()
	{	
		logger.debug("D2DBackupDataSynchronizer.begin() enter...");
		
		try {
			ede.BeginTrans();
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public void disconnect()
	{
		logger.debug("D2DBackupDataSynchronizer.disconnect() enter...");
		
		if(ede != null) {
			ede.CloseDao();
		}
	}
	
	public boolean end(boolean succeed, long last_cache_id, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.end() enter...");
		
		try {
			if(ede == null) {
				return true;
			}
			
			if(succeed)
				ede.CommitTrans();
			else
				ede.RollbackTrans();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return false;
		}
	}
	
	public int UpdateSyncHistory(int theBranchId, int cacheId, int status) {
		connect();
		
		if(false == begin()){
			logger.debug("begin() failed\n");
			disconnect();
			return -2;
		}
		
		int mresult = GetSyncRecord(theBranchId);
		if (mresult == -2) {
			end(false, -1, -1);
			disconnect();
			logger.debug("UpdateSyncHistory(): SQL Operation failed!!\n");
			return -2;
		} else if (mresult == -1) {
			logger.debug("Insert D2D sync history record!!(branchid:" + theBranchId + ")\n");
			boolean result = InsertEdgeSyncHistoryRecord(cacheId,status,theBranchId);
			if (result == false) {
				logger.debug("UpdateSyncHistory(): Insert D2D sync history record failed!!\n");
				end(false, -1, -1);
				disconnect();
				return -2;
			}
		} else {
			logger.debug("UpdateSyncHistory(): Update D2D sync history record!!(branchid:"
						+ theBranchId + ")\n");
			boolean result = UpdateSyncRecord(cacheId,status,theBranchId);
			if (result == false) {
				logger.debug("UpdateSyncHistory(): Update D2D sync history record failed!!\n");
				end(false, -1, -1);
				disconnect();
				return -2;
			}
		}

		end(true, -1, -1);
		disconnect();
		return 0;
	}
	
	public boolean DeleteAllByBranchId(int branchid)
	{
		logger.debug("DeleteAllByBranchId() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(EdgeSyncComponents.ARCserve_D2D.getValue());
			ede.ExecuteDao("DELETE FROM dbo.as_edge_sync_history WHERE branchid=? AND componentid = ?",
					para);
			
			para.clear();
			para.add(branchid);
			ede.ExecuteDao("DELETE FROM dbo.sync_d2d_job WHERE branchid=?", para);
			
			para.clear();
			para.add(branchid);
			ede.ExecuteDao("DELETE FROM dbo.sync_d2d_session WHERE branchid=?", para);
			
			return true;
			
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean InsertEdgeSyncHistoryRecord(long last_cache_id, int status, int branchid )
	{	
		logger.debug("D2DBackupDataSynchronizer.InsertEdgeSyncHistoryRecord() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			logger.debug("status:" + status + " " + "last_cache_id:" + last_cache_id + " " + "branchid:" + branchid);

			para.add(last_cache_id);
			para.add(status);
			para.add(EdgeSyncComponents.ARCserve_D2D.getValue());
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Insert_Edge_Sync_History, para);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean UpdateSyncRecord(long last_cache_id, int status, int branchid )
	{	
		logger.debug("D2DBackupDataSynchronizer.UpdateSyncStatus() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(last_cache_id);
			para.add(status);
			para.add(branchid);
			para.add(EdgeSyncComponents.ARCserve_D2D.getValue());
			ede.ExecuteDao(SQLStmt_Update_Sync_Status, para);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public int GetSyncRecord(final int branchid )
	{	
		logger.debug("GetSyncRecord() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(EdgeSyncComponents.ARCserve_D2D.getValue());
			List<Long> lastCacheId = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Get_Sync_Status, para, lastCacheId);
			
			if(lastCacheId.size() > 0)
			{	
				return 0;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -2;
		} 
	}
	
	public long GetJobInternalId(long job_id, String uniqueID, long branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.GetJobInternalId() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(job_id);
			para.add(uniqueID);
			para.add(branchid);
			List<Long> jobId = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Get_job_internal_id, para, jobId);
			
			if(jobId.size() > 0)
			{	
				long result = jobId.get(0).longValue();
				return result;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -1;
		}
	}
	
	public long GetJobInternalIdBySessId(long sess_id, String uniqueID, long branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.GetJobInternalId() enter...");
		
		String sqlStr = "SELECT id FROM dbo.sync_d2d_job WHERE sess_id = ? AND uniqueID = ? AND branchid = ?";
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sess_id);
			para.add(uniqueID);
			para.add(branchid);
			List<Long> jobInternalId = new ArrayList<Long>();
			ede.ExecuteDao(sqlStr, para, jobInternalId);
			
			if(jobInternalId.size() > 0)
			{	
				long result = jobInternalId.get(0).longValue();
				return result;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -1;
		}
	}
	
	public boolean DeleteJobRecord(long job_id, String uniqueID, long branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.DeleteJobRecord() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(job_id);
			para.add(uniqueID);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Delete_job, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean DeleteJobRecordBySessId(long sess_id, String uniqueID, long branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.DeleteJobRecordBySessId() enter...");
		
		String sqlStr = "DELETE FROM dbo.sync_d2d_job WHERE sess_id=? AND uniqueID = ? AND branchid = ?";
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sess_id);
			para.add(uniqueID);
			para.add(branchid);
			ede.ExecuteDao(sqlStr, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	private long GetSessCountByJob(long job_id, String uniqueID, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.GetSessCountByJob() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(job_id);
			para.add(uniqueID);
			para.add(branchid);
			List<Long> count = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Get_Sess_Cnt_By_Job, para, count);
			
			if(count.size() > 0)
			{	
				long result = count.get(0).longValue();
				return result;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -1;
		}
	}
	
	private long GetSessCountBySessId(long sess_id, String uniqueID, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.GetSessCountByJob() enter...");
		
		String sqlStr = "SELECT count(*) AS cnt FROM dbo.sync_d2d_session WHERE branchid=? AND job_internal_id IN (SELECT id FROM dbo.sync_d2d_job WHERE sess_id=? AND uniqueID = ? AND branchid=?)";
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(sess_id);
			para.add(uniqueID);
			para.add(branchid);
			List<Long> count = new ArrayList<Long>();
			ede.ExecuteDao(sqlStr, para, count);
			
			if(count.size() > 0)
			{	
				long result = count.get(0).longValue();
				return result;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -1;
		}
	}
	
	public boolean DeleteSessionRecordsByJob(long job_id, String uniqueID, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.DeleteSessionRecordsByJob() enter...");
		
		long cnt = GetSessCountByJob(job_id, uniqueID, branchid);
		if(cnt == -1)
		{
			logger.debug("GetSessCountByJob() failed!\n");
			return false;
		}
		else if (cnt == 0)
		{
			logger.debug("No session records to delete!!\n");
			return true;
		}
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(job_id);
			para.add(uniqueID);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Delete_Session_By_Job, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean DeleteSessionRecordsBySessId(long sess_id, String uniqueID, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.DeleteSessionRecordsByJob() enter...");
		
		long cnt = GetSessCountBySessId(sess_id, uniqueID, branchid);
		if(cnt == -1)
		{
			logger.debug("GetSessCountByJob() failed!\n");
			return false;
		}
		else if (cnt == 0)
		{
			logger.debug("No session records to delete!!\n");
			return true;
		}
		
		String sqlStr = "DELETE FROM dbo.sync_d2d_session WHERE branchid=? AND job_internal_id IN (SELECT id FROM dbo.sync_d2d_job WHERE sess_id=? AND uniqueID = ? AND branchid=?)";
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(sess_id);
			para.add(uniqueID);
			para.add(branchid);
			ede.ExecuteDao(sqlStr, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean Purge_D2D_Job(long sess_id, String uniqueID, int branchid)
	{
		if(GetJobInternalIdBySessId(sess_id, uniqueID, branchid) != -1)
		{
			logger.debug("D2D job record(sess_id:" + sess_id + ",branchid:" + branchid + ") exist already, delete the old one!!");
			
			if(DeleteSessionRecordsBySessId(sess_id, uniqueID, branchid) == false)
			{
				logger.debug("Delete job-related session records failed!(sess_id:" + sess_id + ",branchid:" + branchid + ") ");
				return false;
			}
			
			if(DeleteJobRecordBySessId(sess_id, uniqueID, branchid) == false)
			{
				logger.debug("Delete job record failed!(sess_id:" + sess_id + ",branchid:" + branchid + ") ");
				return false;
			}
		}
		else
		{
			logger.debug("There is no record(sess_id:" + sess_id + ",branchid:" + branchid + ") to purge");	
		}
		
		return true;
	}
	
	public boolean Insert_D2D_Job(long job_id, long sess_id, String sessGuid, String uniqueID, String display_name, String backupType, int compress_type,
			long data_sizeKB, long trans_data_sizeKB,long protected_data_sizeB, long catalog_sizeKB, String status, long dest_path_id, String backupDest,
			Timestamp recover_point, int encrypt_type, String encrypt_password_hash, 
			long totalRawDataSizeWritten, int bMRFlag, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.Insert_D2D_Job() enter...");
		
		if(GetJobInternalId(job_id, uniqueID, branchid) != -1)
		{
			logger.debug("D2D job record(job_id:" + job_id + ",branchid:" + branchid + ") exist already, delete the old one!!");
			
			if(DeleteSessionRecordsByJob(job_id, uniqueID, branchid) == false)
			{
				logger.debug("Delete job-related session records failed!(job_id:" + job_id + ",branchid:" + branchid + ") ");
				return false;
			}
			
			if(DeleteJobRecord(job_id, uniqueID, branchid) == false)
			{
				logger.debug("Delete job record failed!(job_id:" + job_id + ",branchid:" + branchid + ") ");
				return false;
			}
		}
		
		List<Object> para = new ArrayList<Object>();
		try {	
			para.add(job_id);
			para.add(sess_id);
			para.add(sessGuid);
			para.add(uniqueID);
			para.add(display_name);
			para.add(backupType);
			para.add(compress_type);
			para.add(data_sizeKB);
			para.add(trans_data_sizeKB);
			para.add(protected_data_sizeB);
			para.add(catalog_sizeKB);
			para.add(status);
			para.add(dest_path_id);
			para.add(backupDest);
			para.add(recover_point);
			para.add(encrypt_type);
			para.add(encrypt_password_hash);
			para.add(totalRawDataSizeWritten);
			para.add(bMRFlag);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Insert_d2d_job, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean Insert_D2D_Job_RPS_Info(long job_id, String uniqueID, int branchid, String datastoreName,
			String policyName) {
		Properties p = RpsBaseSynchronizer.getSqlStmtPro();
		String sqlStmt = p.getProperty("sqlStmt_InsertD2DJob_Rps_Info");
		List<Object> para = new ArrayList<Object>();
		try {
			long id = GetJobInternalId(job_id, uniqueID, branchid);
			para.add(id);
			para.add(datastoreName);
			para.add(policyName);

			ede.ExecuteDao(sqlStmt, para);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public long GetSessionId(long sub_sessid, long job_internal_id, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.GetSessionId() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sub_sessid);
			para.add(job_internal_id);
			para.add(branchid);
			List<Long> sessionId = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Get_session_id, para, sessionId);
			
			if(sessionId.size() > 0)
			{	
				long result = sessionId.get(0).longValue();
				return result;
			}
			else
				return -1;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return -1;
		}
	}
	
	public boolean DeleteSessionRecord(long sub_sessid, long job_internal_id, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.DeleteSessionRecord() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sub_sessid);
			para.add(job_internal_id);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Delete_Session, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	public boolean Insert_D2D_Session(long sub_sessid, long job_internal_id, String type, long flags,
			String display_name, String mount_point, String guid, long vol_dat_sizeB, String catalog_file, 
			int isBootVolume, int isSystemVolume, int branchid)
	{
		logger.debug("D2DBackupDataSynchronizer.Insert_D2D_Session() enter...");
		
		if(GetSessionId(sub_sessid,  job_internal_id,  branchid) != -1)
		{
			logger.debug("D2D session record(sub_sessid:" + sub_sessid + ",job_internal_id:" + job_internal_id + ",branchid:" + branchid + ") exist already, delete the old one!!");
			
			if(DeleteSessionRecord(sub_sessid, job_internal_id, branchid) == false)
			{
				logger.debug("Delete D2D session record(sub_sessid:" + sub_sessid + ",job_internal_id:" + job_internal_id + ",branchid:" + branchid + ") failed!!!");
				return false;
			}
		}
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sub_sessid);
			para.add(job_internal_id);
			para.add(type);
			para.add(flags);
			para.add(display_name);
			para.add(mount_point);
			para.add(guid);
			para.add(vol_dat_sizeB);
			para.add(catalog_file);
			para.add(isBootVolume);
			para.add(isSystemVolume);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Insert_d2d_session, para);
					
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
}
