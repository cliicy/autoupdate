package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DJobsStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.JobStatus2Edge;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class D2DJobStatusSynchronizer {
	private static final Logger logger = Logger.getLogger(D2DJobStatusSynchronizer.class);
	
	static final String SQLStmt_Insert_JobStatus = 
		"INSERT INTO dbo.sync_d2d_jobstatus" +
        "(branchid,jobid,starttime,curProcessDiskName,estimateBytesDisk,estimateBytesJob,flags" +
        ",jobMethod,jobPhase,jobStatus,jobType,sessionID,transferBytesDisk,transferBytesJob," +
		"elapsedTime,volMethod) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String SQLStmt_Update_JobStatus =
		"UPDATE dbo.sync_d2d_jobstatus" +
		    "  SET jobid = ?" +
		    "  ,starttime = ?" +
		    "  ,curProcessDiskName = ?" +
		    "  ,estimateBytesDisk = ?" +
		    "  ,estimateBytesJob = ?" +
		    "  ,flags = ?" +
		    "  ,jobMethod = ?" +
		    "  ,jobPhase = ?" +
		    "  ,jobStatus = ?" +
		    "  ,jobType = ?" +
		    "  ,sessionID = ?" +
		    "  ,transferBytesDisk = ?" +
		    "  ,transferBytesJob = ?" +
		    "  ,elapsedTime = ?" +
		    "  ,volMethod = ?" +
		 " WHERE branchid = ?";
	static final String SQLStmt_Query_jobid =
		"SELECT jobid FROM dbo.sync_d2d_jobstatus WHERE branchid = ?";
	
	EdgeDaoCommonExecuter ede = null;
	
	private int branchid = 0;
	
	public void setBranchid(int value) {
		branchid = value;
	}
	
	private boolean begin()
	{	
		logger.debug("enter...");
		
		if(ede == null)
			ede = DataBaseConnectionFactory.getInstance().createEdgeDaoCommonExecuter();
		
		try {
			ede.BeginTrans();
			
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	private boolean end(boolean succeed)
	{
		logger.debug("enter...");
		
		try {
			if(succeed)
			{
				ede.CommitTrans();
			}
			else
			{
				ede.RollbackTrans();
			}
		
			ede.CloseDao();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			return false;
		}
		
		return true;
	}
	
	private boolean JobStatusRecordExist(int branchid) throws Exception {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			List<Long> jobId = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Query_jobid, para, jobId);
			
			if(jobId.size()>0)
			{	return true;
			}
			else
			{
				return false;
			}
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private boolean InsertJobStatusRecord(JobStatus2Edge statusRecord) throws Exception {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			para.add(statusRecord.getJobId());
			para.add(statusRecord.getStartTime());
			para.add(statusRecord.getCurProcessDiskName());
			para.add(statusRecord.getEstimateBytesDisk());
			para.add(statusRecord.getEstimateBytesJob());
			para.add(statusRecord.getFlags());
			para.add(statusRecord.getJobMethod());
			para.add(statusRecord.getJobPhase());
			para.add(statusRecord.getJobStatus());
			para.add(statusRecord.getJobType());
			para.add(statusRecord.getSessionID());
			para.add(statusRecord.getTransferBytesDisk());
			para.add(statusRecord.getTransferBytesJob());
			para.add(statusRecord.getElapsedTime());
			para.add(statusRecord.getVolMethod());
			ede.ExecuteDao(SQLStmt_Insert_JobStatus, para);
			
			return true;
				
				
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private boolean UpdateD2DJobStatusRecord(JobStatus2Edge statusRecord) throws Exception {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(statusRecord.getJobId());
			para.add(statusRecord.getStartTime());
			para.add(statusRecord.getCurProcessDiskName());
			para.add(statusRecord.getEstimateBytesDisk());
			para.add(statusRecord.getEstimateBytesJob());
			para.add(statusRecord.getFlags());
			para.add(statusRecord.getJobMethod());
			para.add(statusRecord.getJobPhase());
			para.add(statusRecord.getJobStatus());
			para.add(statusRecord.getJobType());
			para.add(statusRecord.getSessionID());
			para.add(statusRecord.getTransferBytesDisk());
			para.add(statusRecord.getTransferBytesJob());
			para.add(statusRecord.getElapsedTime());
			para.add(statusRecord.getVolMethod());
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Update_JobStatus, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			throw e;
		}
	}
	
	public int SyncD2DJobStatus(JobStatus2Edge statusRecord)
	{
		boolean result=true;
		boolean needEnd=false;
		// public final static int JOBTYPE_BACKUP = 0;
		if (null!=statusRecord&&statusRecord.getJobType()==0) {		
			D2DBackupJobStatusInfo backupInfo = D2DJobsStatusCache.getD2DBackupJobsStatusCache().get(String.valueOf(branchid));
			
			if (backupInfo != null && backupInfo.getJobPhase() != statusRecord.getJobPhase()) {
				if (statusRecord.getJobPhase() == Constants.JobExitPhase || statusRecord.getJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
					D2DJobsStatusCache.getD2DBackupJobsStatusCache().updateJobPhase(D2DJobsStatusCache.JOB_PHASE, branchid);
				} else {					
					D2DJobsStatusCache.getD2DBackupJobsStatusCache().updateJobPhase((int) statusRecord.getJobPhase(), branchid);
				}
			}
			D2DJobsStatusCache.getD2DBackupJobsStatusCache().put(String.valueOf(branchid), convertBackupStatusBean(statusRecord));
		}
		try {
			if(begin() == false)
			{
				return -1;
			}
			needEnd = true;
			if(JobStatusRecordExist(branchid))
			{
				logger.debug("D2D job status record of branchid:" + branchid +" existed. Update it...");
				result = UpdateD2DJobStatusRecord(statusRecord);
			}
			else
			{
				logger.debug("D2D job status record of branchid:" + branchid +" doesn't exist. Insert new one...");
				result = InsertJobStatusRecord(statusRecord);
			}
			
			if(result == false)
			{
				logger.debug("Sync D2D job status of branchid:" + branchid + " failed!!");
				return -1;
			}
			
			return 0;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
			result=false;
			return -1;
		}finally {
			if(needEnd) {
				end(result);
			}
		}
	}
	
	private D2DBackupJobStatusInfo convertBackupStatusBean(JobStatus2Edge statusRecord){
		D2DBackupJobStatusInfo backupJobStatusInfo = new D2DBackupJobStatusInfo();
		backupJobStatusInfo.setCurrentProcessDiskName(statusRecord.getCurProcessDiskName());
		backupJobStatusInfo.setElapsedTime(statusRecord.getElapsedTime());
		backupJobStatusInfo.setEstimateBytesDisk(statusRecord.getEstimateBytesDisk());
		backupJobStatusInfo.setEstimateBytesJob(statusRecord.getEstimateBytesJob());
		backupJobStatusInfo.setFlags(statusRecord.getFlags());
		backupJobStatusInfo.setId(statusRecord.getJobId());
		backupJobStatusInfo.setJobMethod(statusRecord.getJobMethod());
		backupJobStatusInfo.setJobPhase(statusRecord.getJobPhase());
		backupJobStatusInfo.setJobStatus(statusRecord.getJobStatus());
		backupJobStatusInfo.setJobType(statusRecord.getJobType());
		backupJobStatusInfo.setNodeId(branchid);
		backupJobStatusInfo.setSessionID(statusRecord.getSessionID());
		backupJobStatusInfo.setBackupStartTime(statusRecord.getStartTime());
		backupJobStatusInfo.setTransferBytesDisk(statusRecord.getTransferBytesDisk());
		backupJobStatusInfo.setTransferBytesJob(statusRecord.getTransferBytesJob());
		backupJobStatusInfo.setVolMethod(statusRecord.getVolMethod());
		
	    backupJobStatusInfo.setnReadSpeed(statusRecord.getReadSpeed());
	    backupJobStatusInfo.setnWriteSpeed(statusRecord.getWriteSpeed());
	    backupJobStatusInfo.setTotalSizeRead(statusRecord.getTotalSizeRead());
	    backupJobStatusInfo.setTotalSizeWritten(statusRecord.getTotalSizeWritten());
	    backupJobStatusInfo.setUlMergedSession(statusRecord.getUlMergedSession());
	    backupJobStatusInfo.setUlProcessedFolder(statusRecord.getUlProcessedFolder());
	    backupJobStatusInfo.setUlTotalFolder(statusRecord.getUlTotalFolder());
	    backupJobStatusInfo.setUlTotalMergedSessions(statusRecord.getUlTotalMergedSessions());
	    backupJobStatusInfo.setThrottling(statusRecord.getThrottling());
	    backupJobStatusInfo.setCtCurCatVol(statusRecord.getCtCurCatVol());
	    backupJobStatusInfo.setCompressLevel(statusRecord.getCompressLevel());
	    backupJobStatusInfo.setEncInfoStatus(statusRecord.getEncInfoStatus());
	    backupJobStatusInfo.setCtBKStartTime(statusRecord.getCtBKStartTime());
	    backupJobStatusInfo.setCurVolMntPoint(statusRecord.getCurVolMntPoint());
	    backupJobStatusInfo.setWszMailFolder(statusRecord.getWszMailFolder());
	    
	    if (statusRecord.getJobPhase() == Constants.JobExitPhase || statusRecord.getJobPhase() == BaseBackupJob.BackupJob_Phase_PROC_EXIT) {
			D2DStatusInfo statusInfo = new D2DStatusInfo();
			statusInfo.setBackupConfiged(statusRecord.getIsBackupConfiged()==1);
			statusInfo.setDestinationAccessible(statusRecord.getIsDestinationAccessible()==1);
			statusInfo.setDestinationEstimatedBackupCount(statusRecord.getDestinationEstimatedBackupCount());
			statusInfo.setDestinationFreeSpace(statusRecord.getDestinationFreeSpace());
			statusInfo.setDestinationPath(statusRecord.getDestinationPath());
			statusInfo.setDestinationStatus(NodeServiceImpl.dbValueToD2DStatus(statusRecord.getDestinationStatus()));
			statusInfo.setDriverInstalled(statusRecord.getIsDriverInstalled()==1);
			statusInfo.setEstimatedValue(NodeServiceImpl.dbValueToD2DEstimatedValue(statusRecord.getEstimatedValue()));
			statusInfo.setLastBackupJobStatus(NodeServiceImpl.dbValueToD2DJobStatus(statusRecord.getLastBackupJobStatus()));
			statusInfo.setLastBackupStartTime(new Date(statusRecord.getLastBackupTime()));
			statusInfo.setLastBackupStatus(NodeServiceImpl.dbValueToD2DStatus(statusRecord.getLastBackupStatus()));
			statusInfo.setLastBackupType(NodeServiceImpl.dbValueToD2DBackupType(statusRecord.getLastBackupType()));
			statusInfo.setOverallStatus(NodeServiceImpl.dbValueToD2DStatus(statusRecord.getOverallStatus()));
			statusInfo.setRecoveryPointCount(statusRecord.getRecoveryPointCount());
			statusInfo.setRecoveryPointMounted(statusRecord.getRecoveryPointMounted());
			statusInfo.setRecoveryPointRetentionCount(statusRecord.getRecoveryPointRetentionCount());
			statusInfo.setRecoveryPointStatus(NodeServiceImpl.dbValueToD2DStatus(statusRecord.getRecoveryPointStatus()));
			statusInfo.setIsUseBackupSets(statusRecord.getIsUseBackupSets());
			statusInfo.setRestarted(statusRecord.getIsRestarted()==1);
			backupJobStatusInfo.setD2DStatusInfo(statusInfo);
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
			hostMgrDao.setD2DStatusInfo(
					branchid,
					statusRecord.getOverallStatus(),
					new Date(statusRecord.getLastBackupTime()),
					statusRecord.getLastBackupType(),
					statusRecord.getLastBackupJobStatus(),
					statusRecord.getLastBackupStatus(),
					statusRecord.getRecoveryPointRetentionCount(),
					statusRecord.getRecoveryPointCount(),
					statusRecord.getRecoveryPointMounted(),
					statusRecord.getRecoveryPointStatus(),
					statusRecord.getIsUseBackupSets(),
					statusRecord.getDestinationPath(),
					statusRecord.getIsDestinationAccessible(),
					statusRecord.getDestinationFreeSpace(),
					statusRecord.getDestinationEstimatedBackupCount(),
					statusRecord.getDestinationStatus(),
					statusRecord.getIsDriverInstalled(),
					statusRecord.getIsRestarted(),
					statusRecord.getEstimatedValue(),
					statusRecord.getIsBackupConfiged());
		} 
		
		return backupJobStatusInfo;
	}
}
