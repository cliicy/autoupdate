package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveDestType;
import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveJob;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;

public class D2DArchiveSynchronizer {
	private static final Logger logger = Logger.getLogger(D2DArchiveSynchronizer.class);
	
	static final String SQLStmt_Insert_ArchiveJob = 
		"INSERT INTO dbo.sync_d2d_archive_job" +
        "(Id,ArchiveJobID,Path,DestPath,Status,JobMethod,DetailTime,ArchiveDataSize" +
        ",CopyDataSize,ScheduleCount,IsEncrypted,CompressionFlag,BranchId) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String SQLStmt_Update_ArchiveJob =
		"UPDATE dbo.sync_d2d_archive_job" +
			"  SET ArchiveJobID = ?" + 
			"  ,DestPath = ?" + 
		    "  ,Status = ?" +
		    "  ,DetailTime = ?" +
		    "  ,ArchiveDataSize = ?" +
		    "  ,CopyDataSize = ?" +
		 " WHERE Id = ? AND BranchId = ?";
	static final String SQLStmt_Query_ArchiveJob_Exist =
		"SELECT count(*) FROM dbo.sync_d2d_archive_job WHERE Id = ? AND BranchId = ?";
	static final String SQLStmt_Delete_ArchiveJob_By_Branch =
		"DELETE FROM dbo.sync_d2d_archive_job WHERE BranchId = ?";
	
	EdgeDaoCommonExecuter ede = null;
	
	public boolean begin()
	{	
		logger.debug("Enter ...");
		
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
	
	public boolean end(boolean succeed)
	{
		logger.debug("enter...");
		
		try {
			if(ede == null) {
				logger.debug("begin() not called yet. exit");
				return true;
			}
			
			if(succeed)
				ede.CommitTrans();
			else
				ede.RollbackTrans();
		} catch (Throwable e) {
			logger.error(e.toString());
			return false;
		}finally {
			ede.CloseDao();
		}
		
		return true;
	}
	
	private String getConvertedArchiveDest(long archiveDestType, String archiveDestPath) {
		String destTypeStr="";
		if(archiveDestType==ArchiveDestType.CLOUD_TYPE_AMAZON_S3) {
			destTypeStr = D2DSyncMessage.GetMessage(D2DSyncMessage.D2DSync_ArchiveDestType_AMAZON_S3);
			destTypeStr = destTypeStr + "\\";
		}
		else
		if(archiveDestType==ArchiveDestType.CLOUD_TYPE_EUCALYPTUS) {
			destTypeStr = D2DSyncMessage.GetMessage(D2DSyncMessage.D2DSync_ArchiveDestType_EUCALYPTUS);
			destTypeStr = destTypeStr + "\\";
		}
		else
		if(archiveDestType==ArchiveDestType.CLOUD_TYPE_WINDOWS_AZURE_BLOB) {
			destTypeStr = D2DSyncMessage.GetMessage(D2DSyncMessage.D2DSync_ArchiveDestType_WINDOWS_AZURE_BLOB);
			destTypeStr = destTypeStr + "\\";
		}
			
		String convertedDest = destTypeStr + archiveDestPath;
		return convertedDest;
	}
	
	private boolean DeleteArchiveJob(int Id, int branchid) {
		logger.debug("enter...");
		
		String sqlStr = "DELETE FROM sync_d2d_archive_job WHERE Id = " + Id
					+	" AND BranchId = " + branchid;
		
		try {
			ede.ExecuteDao(sqlStr, null);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	private boolean ArchiveJobExist(int Id, int branchid) {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(Id);
			para.add(branchid);
			List<Long> count = new ArrayList<Long>();
			ede.ExecuteDao(SQLStmt_Query_ArchiveJob_Exist, para, count);
			if(count.get(0) > 0)
				return true;
			else
				return false;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	private boolean InsertArchiveJobRecord(ArchiveJob rec, int branchid) {
		logger.debug("enter...");
		
		Timestamp tTime = GetArchiveJobTimestamp(rec);
		if(tTime == null) {
			logger.debug("Failed to get archive job timestamp");
			return false;
		}
		
		String convertedDestPath = getConvertedArchiveDest(rec.GetDestType(), rec.GetDestPath());
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(rec.GetId());
			para.add(rec.GetArchiveJobID());
			para.add(rec.GetPath());
			para.add(convertedDestPath);
			para.add(rec.GetStatus());
			para.add(rec.GetJobMethod());
			para.add(tTime);
			para.add(rec.GetArchiveDataSize());
			para.add(rec.GetCopyDataSize());
			para.add(rec.GetScheduleCount());
			para.add(rec.GetIsEncrypted());
			para.add(rec.GetCompressionFlag());
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Insert_ArchiveJob, para);
			
			return true;	
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	private Timestamp GetArchiveJobTimestamp(ArchiveJob rec) {
		String strTime = "";
		if(rec.GetYear() == 0 && rec.GetMonth() == 0 && rec.GetDay() == 0)
			strTime = "1980-01-01 12:00:00";
		else
			strTime = String.format("%04d-%02d-%02d %02d:%02d:%02d", 
				rec.GetYear(),rec.GetMonth(), rec.GetDay(),
				rec.GetHour(),rec.GetMinute(),rec.GetSecond());
		
		try {
			Timestamp tTime = Timestamp.valueOf(strTime);
			return tTime;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private boolean UpdateArchiveJobRecord(ArchiveJob rec, int branchid) {
		logger.debug("enter...");
		
		Timestamp tTime = GetArchiveJobTimestamp(rec);
		if(tTime == null) {
			logger.debug("Failed to get archive job timestamp");
			return false;
		}
		
		String convertedDestPath = getConvertedArchiveDest(rec.GetDestType(), rec.GetDestPath());
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(rec.GetArchiveJobID());
			para.add(convertedDestPath);
			para.add(rec.GetStatus());
			para.add(tTime);
			para.add(rec.GetArchiveDataSize());
			para.add(rec.GetCopyDataSize());
			para.add(rec.GetId());
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Update_ArchiveJob, para);
			
			return true;	
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean SaveArchiveJob(ArchiveJob rec, int branchid){
		try {
			if(ArchiveJobExist(rec.GetId(), branchid)) {
				if(rec.GetOperation().equals("ADD")) {
					if(false == DeleteArchiveJob(rec.GetId(), branchid)){
						logger.debug("DeleteArchiveJob() failed\n");
						return false;
					}
					
					return InsertArchiveJobRecord(rec, branchid); 
				}
				else
					return UpdateArchiveJobRecord(rec, branchid);
			}
			else {
				if(rec.GetOperation().equals("ADD")) {
					return InsertArchiveJobRecord(rec, branchid);
				}
				else {
					logger.debug("No record exist, ignore this update\n");
					return true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(e);
			return false;
		}
	}
	
	public void DelArchiveJobByBranch(int branchid) throws Exception {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Delete_ArchiveJob_By_Branch, para);
		} catch ( Exception e){
			logger.debug(e);
			throw e;
		}
	}
}
