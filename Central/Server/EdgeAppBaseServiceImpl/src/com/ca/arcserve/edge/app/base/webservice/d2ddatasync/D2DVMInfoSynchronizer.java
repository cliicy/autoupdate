package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;

public class D2DVMInfoSynchronizer {
	private static final Logger logger = Logger.getLogger(D2DVMInfoSynchronizer.class);
	
	private static final String SQLStmt_Update_D2D_Job_VMInfo = 
		"Update dbo.sync_d2d_job set vmName = ?, vmInstUUID = ? , vmHostName = ?, vCenterName = ?, esxName = ?, vmGuestOSTypeValue = ?,"
		+ "vmGuestOSTypeString = ?, vmType = 1, appType = ? where sessGuid = ? and branchid = ?";
	
	private static final String SQLStmt_Query_D2D_Job_By_SessGuid = 
		"select count(*) from dbo.sync_d2d_job where sessGuid = ? and branchid = ?";
	
	private static final String SQLStmt_DeleteAll_TempVMHost=
			"Delete from dbo.sync_tempVMHost where branchid = ?";
	private static final String SQLStmt_Query_TempVMHost=
			"select count(*) from dbo.sync_tempVMHost where vmInstUUID = ?";
	private static final String SQLStmt_Update_TempVMHost=
			"Update dbo.sync_tempVMHost set vmHostName = ? , vmName = ? , overallStatus = ? , branchid = ? where vmInstUUID = ?";
	private static final String SQLStmt_Insert_TempVMHost=
			"Insert dbo.sync_tempVMHost(vmInstUUID,vmHostName,vmName,overallStatus,branchid)Values (?,?,?,?,?)";
	
	EdgeDaoCommonExecuter ede = null;
	
	private int branchid = 0;
	
	public void setBranchid(int value) {
		branchid = value;
	}
	
	public boolean UpdateD2DJobVMInfo(String vmName, String vmInstUUID, String vmHostName, String vCenterName,
			String esxName, long vmGuestOSTypeValue, String vmGuestOSTypeString, int appType, String sessGuid)
	{
		logger.debug("enter...");
		
		if(!IsD2DJobRecordExist(sessGuid, branchid)) {
			logger.debug("D2D job record doest'nt exist, return without update. " + sessGuid);
			return true;
		}
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(vmName);
			para.add(vmInstUUID);
			para.add(vmHostName);
			para.add(vCenterName);
			para.add(esxName);
			para.add(vmGuestOSTypeValue);
			para.add(vmGuestOSTypeString);
			para.add(appType);
			para.add(sessGuid);
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_Update_D2D_Job_VMInfo, para);
			
			return true;
		} catch ( Exception e){
			logger.error(e.toString());
			return false;
		}
	}

	
	private boolean IsD2DJobRecordExist(String sessGuid, int branchid)
	//String uniqueID, long branchid, java.sql.Timestamp purgeTime)
	{
		logger.debug("enter...");
		
		int cnt = 0;
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(sessGuid);
			para.add(branchid);
			List<Integer> count = new ArrayList<Integer>();
			ede.ExecuteDao(SQLStmt_Query_D2D_Job_By_SessGuid, para, count, 0);
			
			cnt = count.get(0);
			if(cnt == 0)
				return false;
			else
				return true;
		} catch ( Exception e){
			logger.error(e.toString());
			
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
			logger.error(e.toString());
			
			return false;
		}
		return true;
	}
	
	public boolean end(boolean succeed)
	{
		logger.debug("enter...");
		
		try {
			if(ede == null){
				return true;
			}
			
			if(succeed)
				ede.CommitTrans();
			else
				ede.RollbackTrans();
		} catch (Throwable e) {
			logger.error(e.toString());
			return false;
		} finally {
			ede.CloseDao();
		}
		
		return true;
	}
	
	public boolean UpdateTempVMHostInfo(String vmInstUUID , int overallStatus,String vmHostName,String vmName) {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(vmInstUUID);
			List<Integer> count = new ArrayList<Integer>();
			ede.ExecuteDao(SQLStmt_Query_TempVMHost, para, count, 0);
			
			int cnt = count.get(0);
			if(cnt == 0){
				para.clear();
				para.add(vmInstUUID);
				para.add(vmHostName);
				para.add(vmName);
				para.add(overallStatus);
				para.add(branchid);
				ede.ExecuteDao(SQLStmt_Insert_TempVMHost, para);
			}else {
				para.clear();
				para.add(vmHostName);
				para.add(vmName);
				para.add(overallStatus);
				para.add(branchid);
				para.add(vmInstUUID);
				ede.ExecuteDao(SQLStmt_Update_TempVMHost, para);
			}
			return true;
		} catch ( Exception e){
			logger.error(e.toString());
			return false;
		}
	}
	
	public boolean DeleteAllTempVMHost() {
		logger.debug("enter...");
		
		List<Object> para = new ArrayList<Object>();
		try {
			para.add(branchid);
			ede.ExecuteDao(SQLStmt_DeleteAll_TempVMHost,para);
			return true;
		} catch ( Exception e){
			logger.error(e.toString());
			return false;
		}
	}
}
