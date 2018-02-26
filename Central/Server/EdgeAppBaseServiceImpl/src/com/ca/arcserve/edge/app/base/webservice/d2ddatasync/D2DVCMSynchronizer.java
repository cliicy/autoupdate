package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMEventRec;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;

public class D2DVCMSynchronizer {
	private static final Logger logger = Logger.getLogger(D2DActiveSyncLogSynchronizer.class);
	private int branchid = 0;
	EdgeDaoCommonExecuter ede = null;
	
	public void setBranchid(int value) {
		branchid = value;
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
	
	public boolean DeleteAllEventsByBranch() {
		String sqlStrDelAll= "DELETE FROM sync_d2d_vcm_event WHERE branchid = " + branchid;
		try {
			ede.ExecuteDao(sqlStrDelAll, null);
			return true;
		} catch ( Exception e){
			logger.error(e.getMessage(), e);
			
			return false;
		}
	}
	
	private void alignVCMEventRec(VCMEventRec eventRec){
		if(eventRec.GetTaskGuid() == null)
			eventRec.SetTaskGuid("");
		
		if(eventRec.GetTaskName() == null)
			eventRec.SetTaskName("");
			
		if(eventRec.GetTaskType() == null)
			eventRec.SetTaskType("");
		
		if(eventRec.GetStartTime() == null)
			eventRec.SetStartTime("1980-01-01 11:00:00");
		
		if(eventRec.GetEndTime() == null)
			eventRec.SetEndTime("1980-01-01 11:00:00");
		
		if(eventRec.GetSrcHostName() == null)
			eventRec.SetSrcHostName("");
			
		if(eventRec.GetSrcVMName() == null)
			eventRec.SetSrcVMName("");
			
		if(eventRec.GetSrcVirtualCenterName() == null)
			eventRec.SetSrcVirtualCenterName("");
			
		if(eventRec.GetSrcVMUUID() == null)
			eventRec.SetSrcVMUUID(""); 
			
		if(eventRec.GetSrcVMType() == null)
			eventRec.SetSrcVMType("");
			
		if(eventRec.GetDestHostName() == null)
			eventRec.SetDestHostName("");
			
		if(eventRec.GetDestVMName() == null)
			eventRec.SetDestVMName("");
			
		if(eventRec.GetDestVirtualCenterName() == null)
			eventRec.SetDestVirtualCenterName("");
			
		if(eventRec.GetDestVMUUID() == null)
			eventRec.SetDestVMUUID("");
			
		if(eventRec.GetDestVMType() == null)
			eventRec.SetDestVMType("");
			
		if(eventRec.GetStatus() == null)
			eventRec.SetStatus("");
			
		if(eventRec.GetStatusComment() == null)
			eventRec.SetStatusComment("");
		
		if(eventRec.GetVcmMonitorHost() == null)
			eventRec.SetVcmMonitorHost("");
		
		if(eventRec.GetIsProxy() == null)
			eventRec.SetIsProxy("");
		
		if(eventRec.GetAfGuid() == null)
			eventRec.SetAfGuid("");
	}
	
	public boolean UpdateVCMEvent(VCMEventRec eventRec) {
		int count = 0;
		String sqlStrInsert = 
					  "INSERT INTO sync_d2d_vcm_event "
                    + "(taskGuid,taskName,taskType,startTime,endTime,srcHostName"
                    + ",srcVMName,srcVirtualCenterName,srcVMUUID,srcVMType,destHostName"
                    + ",destVMName,destVirtualCenterName,destVMUUID,destVMType,status"
                    + ",statusComment,vmwareProxy,afGuid,jobId,branchid) "
                    + "VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                    + eventRec.GetJobId() + ","
                    + branchid + ")";
		String sqlStrUpdate = 
					  "UPDATE sync_d2d_vcm_event "
                    + "SET taskName = ?,taskType = ?,startTime = ?,endTime = ?"
                    + ",srcHostName = ?,srcVMName = ?,srcVirtualCenterName = ?"
                    + ",srcVMUUID = ?,srcVMType = ?,destHostName = ?,destVMName = ?"
                    + ",destVirtualCenterName = ?,destVMUUID = ?,destVMType = ?"
                    + ",status = ?,statusComment = ?,vmwareProxy = ? "
                    + ",afGuid = ?,jobId =  " + eventRec.GetJobId() + " "
                   // + "WHERE taskGuid = ? and branchid = " + branchid;
                    + "WHERE branchid = " + branchid; //change for issue 151479 <zhaji22>
		String sqlStrQuery = 
					  "SELECT count(*) FROM sync_d2d_vcm_event "
		           // + "WHERE taskGuid = ? and branchid = " + branchid;
				      + "WHERE branchid = " + branchid; //change for issue 151479 <zhaji22>
		
		String sqlStrDelete= 
				  "Delete FROM sync_d2d_vcm_event "
			      + "WHERE branchid = " + branchid; //change for issue 151479 <zhaji22>
		
		alignVCMEventRec(eventRec);
		
		java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(eventRec.GetStartTime());
		java.sql.Timestamp endTime   = java.sql.Timestamp.valueOf(eventRec.GetEndTime());
		
		String vmwareProxy="";
		if(eventRec.GetIsProxy().equalsIgnoreCase("true")) {
			vmwareProxy = eventRec.GetVcmMonitorHost()==null?"":eventRec.GetVcmMonitorHost();
		}
		
		List<Object> para = new ArrayList<Object>();
		try {
			//para.add(eventRec.GetTaskGuid()); //delete for issue 151479 <zhaji22>
			List<Integer> cnt = new ArrayList<Integer>();
			ede.ExecuteDao(sqlStrQuery, null, cnt, 0);
			count = cnt.get(0);
			
			para.clear();
			if(count <= 0) {
				addInsertParameter(para, eventRec);
				ede.ExecuteDao(sqlStrInsert, para);
			}else if(count>1){
				//if there are a lot of dirty in db, delete them then insert
				ede.ExecuteDao(sqlStrDelete, null);
				addInsertParameter(para, eventRec);
				ede.ExecuteDao(sqlStrInsert, para);
			}
			else {
				para.add(eventRec.GetTaskName());
				para.add(eventRec.GetTaskType());
				para.add(startTime);
				para.add(endTime);
				para.add(eventRec.GetSrcHostName());
				para.add(eventRec.GetSrcVMName());
				para.add(eventRec.GetSrcVirtualCenterName());
				para.add(eventRec.GetSrcVMUUID());
				para.add(eventRec.GetSrcVMType());
				para.add(eventRec.GetDestHostName());
				para.add(eventRec.GetDestVMName());
				para.add(eventRec.GetDestVirtualCenterName());
				para.add(eventRec.GetDestVMUUID());
				para.add(eventRec.GetDestVMType());
				para.add(eventRec.GetStatus());
				para.add(eventRec.GetStatusComment());
				para.add(vmwareProxy);
				para.add(eventRec.GetAfGuid());
			//	para.add(eventRec.GetTaskGuid());  //delete for issue 151479 <zhaji22>
				ede.ExecuteDao(sqlStrUpdate, para);
			}
			
			return true;
		} catch ( Exception e){
			logger.error(e.toString());
			
			return false;
		}
	}
	
	private void addInsertParameter(List<Object> para , VCMEventRec eventRec){
		java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(eventRec.GetStartTime());
		java.sql.Timestamp endTime   = java.sql.Timestamp.valueOf(eventRec.GetEndTime());
		String vmwareProxy="";
		if(eventRec.GetIsProxy().equalsIgnoreCase("true")) {
			vmwareProxy = eventRec.GetVcmMonitorHost()==null?"":eventRec.GetVcmMonitorHost();
		}
		para.add(eventRec.GetTaskGuid());
		para.add(eventRec.GetTaskName());
		para.add(eventRec.GetTaskType());
		para.add(startTime);
		para.add(endTime);
		para.add(eventRec.GetSrcHostName());
		para.add(eventRec.GetSrcVMName());
		para.add(eventRec.GetSrcVirtualCenterName());
		para.add(eventRec.GetSrcVMUUID());
		para.add(eventRec.GetSrcVMType());
		para.add(eventRec.GetDestHostName());
		para.add(eventRec.GetDestVMName());
		para.add(eventRec.GetDestVirtualCenterName());
		para.add(eventRec.GetDestVMUUID());
		para.add(eventRec.GetDestVMType());
		para.add(eventRec.GetStatus());
		para.add(eventRec.GetStatusComment());
		para.add(vmwareProxy);
		para.add(eventRec.GetAfGuid());
	}
}
