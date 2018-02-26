package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.webservice.d2dactivelog.LogRec;

public class D2DActiveSyncLogSynchronizer {
	private static final Logger logger = Logger.getLogger(D2DActiveSyncLogSynchronizer.class);
	
	private static final String SQLStmt_Insert_D2DActiveLog = 
		"INSERT INTO dbo.sync_d2d_activity_log (uniqueID, jobno, flags, logtime, logmsg, branchid) VALUES (?,?,?,?,?,?)";
	
	private static final String SQLStmt_Del_D2DActiveLog_By_Time = 
		"DELETE FROM dbo.sync_d2d_activity_log  WHERE uniqueID = ? AND branchid = ? AND logtime < ?";
	private static final String SQLStmt_Clean_AllD2DActiveLog = 
		"DELETE FROM dbo.sync_d2d_activity_log  WHERE branchid = ?";
	
	private EdgeDaoCommonExecuter ede = null;
	
	private int branchid = 0;
	
	public void setBranchid(int value) {
		branchid = value;
	}
	
	public boolean ProcActiveLog(LogRec logRec)
	{
		if(logRec.getOper().matches("ADD"))
		{
			return InsertActiveLog(logRec);
		}
		else if(logRec.getOper().matches("PRUNE"))
		{
			return DeleteActiveLog(logRec);
		}
		
		return true;
	}
	
	public boolean CleanAllActiveLog()
	{
		logger.debug("CleanAllActiveLog() enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Clean_AllD2DActiveLog, para);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	private boolean DeleteActiveLog(LogRec logRec)
	//String uniqueID, long branchid, java.sql.Timestamp purgeTime)
	{
		logger.debug("D2DActiveSyncLogSynchronizer.DeleteActiveLog() enter...");
		
		java.sql.Timestamp purgeTime = java.sql.Timestamp.valueOf(logRec.getStrTime());
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(logRec.getUniqueID());
			para.add(branchid);
			para.add(purgeTime);
			ede.ExecuteDao(SQLStmt_Del_D2DActiveLog_By_Time, para);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	private boolean InsertActiveLog(LogRec logRec) {
		logger.debug("enter...");
		
		java.sql.Timestamp logTime = java.sql.Timestamp.valueOf(logRec.getStrTime());
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(logRec.getUniqueID());
			para.add(logRec.getJobNo());
			para.add(logRec.getFlags());
			para.add(logTime);
			para.add(logRec.getStrLog());
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Insert_D2DActiveLog, para);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean begin()
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
	
	public boolean end(boolean succeed)
	{
		logger.debug("enter...");
		
		try {
			if(ede == null) {
				logger.debug("begin() not called. exit directly");
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
			try {
				if(ede != null)
					ede.CloseDao();
			}catch(Throwable t) {
				logger.error(t.toString());
			}
		}
		
		return true;
	}
}
