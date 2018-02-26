package com.ca.arcflash.ha.utils;

import com.ca.arcflash.ha.vmwaremanager.WriteLogInterface;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.scheduler.Constants;

class ActivityLogObj{
	private long jobID;
	private String afguid;
	
	public ActivityLogObj(){
		this(-1,"");
	}
	public ActivityLogObj(long jobID, String afguid){
		this.jobID = jobID;
		this.afguid = afguid;
	}
	
	public long getJobID() {
		return jobID;
	}
	public void setJobID(long jobID) {
		this.jobID = jobID;
	}
	public String getAfguid() {
		return afguid;
	}
	public void setAfguid(String afguid) {
		this.afguid = afguid;
	}
	
	
}
public class WriteLogImpl implements WriteLogInterface {

	private static WriteLogImpl writeLogImpl = new WriteLogImpl();
	private ThreadLocal<ActivityLogObj> writeActiveLogLocal = new ThreadLocal<ActivityLogObj>();
	
	private WriteLogImpl(){
		
	}
	
	public static WriteLogImpl getInstance(){
		return writeLogImpl;
	}
	
	public void setActiveLogObj(long jobID, String afguid){
		ActivityLogObj activityLogObj = writeActiveLogLocal.get();
		if(activityLogObj==null){
			activityLogObj = new ActivityLogObj();
		}
		activityLogObj.setJobID(jobID);
		activityLogObj.setAfguid(afguid);
		writeActiveLogLocal.set(activityLogObj);
	}
	
	public void RemoveActiveLogObj(){
		writeActiveLogLocal.remove();
	}
	
	
	@Override
	public void printErrorLog(String msg) {
		// TODO Auto-generated method stub
		ActivityLogObj activityLogObj = writeActiveLogLocal.get();
		msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_VMWARE_MSG, msg);
		if(activityLogObj!=null)
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, activityLogObj.getJobID(), Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, activityLogObj.getAfguid());
	}

	@Override
	public void printInfoLog(String msg) {
		// TODO Auto-generated method stub
		ActivityLogObj activityLogObj = writeActiveLogLocal.get();
		msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_VMWARE_MSG, msg);
		if(activityLogObj!=null)
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, activityLogObj.getJobID(), Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, activityLogObj.getAfguid());
	}
}
