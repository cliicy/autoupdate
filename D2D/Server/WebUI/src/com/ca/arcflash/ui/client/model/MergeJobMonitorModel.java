package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MergeJobMonitorModel extends BaseModelData {
	private static final long serialVersionUID = 4257872906501359429L;	
	
	public static enum JobPhase {
	    EJP_UNKNOWN,            ///ZZ: Unknown phase, usually exist when job monitor is just initialized.  
	    EJP_PROC_ENTER,             ///ZZ: Enter merge job process.
	    EJP_INIT_BKDEST,            ///ZZ: Merge job is initializing backup destination.
	    EJP_ENUM_SESS,              ///ZZ: Merge job is enumerating session to decide if any session should be merged.
	    EJP_CONTINUE_FAILED_MERGE,  ///ZZ: Repair sessions which are merged failed in last merge job.
	    EJP_LOCK_SESS,              ///ZZ: Lock session for merge.
	    EJP_WAIT_4_LOCK,            ///ZZ: Session is used by other operation, wait session lock for merge.
	    EJP_MERGE_SESS,             ///ZZ: Merge session data.
	    EJP_MERGE_PREPROCESS,      ///ZZ: Pre-process session data for merge.
	    EJP_MERGE_DISK_INIT,		 ///ZZ: Initialization of merging disk.
	    EJP_MERGE_DISK_DATA,        ///ZZ: Merging data.
	    EJP_UNINIT_BKDES,           ///ZZ: Un-initialize backup destination, including cut exist network connection.
	    EJP_WAIT_STOP,              ///ZZ: Merge job receive stop command and release resource before exit.
	    EJP_END_OF_JOB,             ///ZZ: Merge job is end and process will exit soon.
	    EJP_PROC_EXIT               ///ZZ: Merge job process exit.
	}
	
	public static enum JobStatus {
		EJS_UNKNOWN,  ///ZZ: Unknown status, usually exist when job monitor is just initialized. 
	    EJS_JOB_FINISH,   ///ZZ: Job finishes successfully
	    EJS_JOB_FAILED,   ///ZZ: Job failed because some internal error.
	    EJS_JOB_STOPPED,  ///ZZ: Job is stopped by user or other job.
	    EJS_JOB_SKIPPED,  ///ZZ: Job is skipped because no need to merge.
	    EJR_JOB_FAILLOCK, ///ZZ: Job is skipped because failed to lock session, it is a bit defferent from EJR_JOB_SKIPPED
	    EJS_JOB_CRASH     ///ZZ: Job crashes. We consider no EJP_END_OF_JOB phase when process exit.
	}
	
	public enum MergeMethod {
		EMM_INC_2_FUL(1),  ///ZZ: Merge method: merge incremental session to full session. 
	    EMM_MULTI_INC_2H(2),   ///ZZ: Merge method: merge multiple sessions, may include full session, to latest session in merge range. 
	    EMM_MULTI_INC_2L(3),///ZZ: Merge method: merge multiple sessions, may include full session, to oldest session in merge range.
		EMM_RMV_SESS(4);///ZZ: Merge method: remove session range begin with full session for backup set or some special use.
	    
	    private int value;
		
		private MergeMethod(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public Integer getJobStatus() {
		return (Integer)get("status");
	}
	
	public void setJobStatus(Integer status) {
		set("status", status);
	}
	
	public Integer getJobPhase() {
		return (Integer)get("phase");
	}
	
	public void setJobPhase(Integer phase) {
		set("phase", phase);
	}	
	
	public Integer getJobId() {
		return (Integer)get("id");
	}
	
	public void setJobId(Integer id) {
		set("id", id);
	}

	public Integer getMergeOpt() {
    	return get("MergeOpt");
    }
	public void setMergeOpt(Integer dwMergeOpt) {
    	set("MergeOpt", dwMergeOpt);
    }
	public Integer getMergeMethod() {
    	return (Integer)get("MergeMethod");
    }
	public void setMergeMethod(Integer dwMergeMethod) {
    	set("MergeMethod", dwMergeMethod);
    }
	public Integer getRetentionCnt() {
    	return get("RetentionCnt");
    }
	public void setRetentionCnt(Integer dwRetentionCnt) {
    	set("RetentionCnt", dwRetentionCnt);
    }
	public Integer getSessStart() {
    	return (Integer)get("SessStart");
    }
	public void setSessStart(Integer dwSessStart) {
		set("SessStart", dwSessStart);
    }
	public Integer getEndStart() {
    	return (Integer)get("EndStart");
    }
	public void setEndStart(Integer dwEndStart) {
    	set("EndStart", dwEndStart);
    }

	public Integer getSessCnt2Merge() {
    	return (Integer)get("SessCnt2Merge");
    }
	public void setSessCnt2Merge(Integer dwSessCnt2Merge) {
    	set("SessCnt2Merge", dwSessCnt2Merge);
    }
	public Integer getSessCntMerged() {
    	return (Integer)get("SessCntMerged");
    }
	public void setSessCntMerged(Integer dwSessCntMerged) {
		set("SessCntMerged", dwSessCntMerged);
    }
	public Integer getCurSess2Merge() {
    	return (Integer)get("CurSess2Merge");
    }
	public void setCurSess2Merge(Integer dwCurSess2Merge) {
    	set("CurSess2Merge",dwCurSess2Merge);
    }
	public Integer getDiskCnt2Merge() {
    	return (Integer)get("DiskCnt2Merge");
    }
	public void setDiskCnt2Merge(Integer dwDiskCnt2Merge) {
    	set("DiskCnt2Merge", dwDiskCnt2Merge);
    }
	public Integer getDiskCntMerged() {
    	return (Integer)get("DiskCntMerged");
    }
	public void setDiskCntMerged(Integer dwDiskCntMerged) {
    	set("DiskCntMerged", dwDiskCntMerged);
    }
	public Integer getCurDiskSig2Merge() {
    	return (Integer)get("CurDiskSig2Merge");
    }
	public void setCurDiskSig2Merge(Integer dwCurDiskSig2Merge) {
    	set("CurDiskSig2Merge", dwCurDiskSig2Merge);
    }
	public Long getDiskBytes2Merge() {
    	return (Long)get("DiskBytes2Merge");
    }
	public void setDiskBytes2Merge(Long ullDiskBytes2Merge) {
    	set("DiskBytes2Merge", ullDiskBytes2Merge);
    }
	public Long getDiskBytesMerged() {
    	return (Long)get("DiskBytesMerged");
    }
	public void setDiskBytesMerged(Long ullDiskBytesMerged) {
    	set("DiskBytesMerged", ullDiskBytesMerged);
    }
	public Long getSessBytes2Merge() {
    	return (Long)get("SessBytes2Merge");
    }
	public void setSessBytes2Merge(Long ullSessBytes2Merge) {
    	set("SessBytes2Merge", ullSessBytes2Merge);
    }
	public Long getSessBytesMerged() {
    	return (Long)get("SessBytesMerged");
    }
	public void setSessBytesMerged(Long ullSessBytesMerged) {
    	set("SessBytesMerged", ullSessBytesMerged);
    }
	public Long getTotalBytes2Merge() {
    	return (Long)get("TotalBytes2Merge");
    }
	public void setTotalBytes2Merge(Long ullTotalBytes2Merge) {
    	set("TotalBytes2Merge", ullTotalBytes2Merge);
    }
	public Long getTotalBytesMerged() {
    	return (Long)get("TotalBytesMerged");
    }
	public void setTotalBytesMerged(Long ullTotalBytesMerged) {		
    	set("TotalBytesMerged", ullTotalBytesMerged);
    }
	public Float getMergePercentage() {
    	return (Float)get("MergePercentage");
    }
	public void setMergePercentage(Float fMergePercentage) {
    	set("MergePercentage", fMergePercentage);
    }
	public Long getStartTime() {
		return (Long)get("StartTime");
	}
	public void setStartTime(Long startTime) {
		set("StartTime", startTime);
	}
	public Long getElapsedTime() {
		return (Long)get("ElapsedTime");
	}
	public void setElapsedTime(Long elapsedTime) {
		set("ElapsedTime", elapsedTime);
	}
	public String getVMInstanceUUID() {	
		return (String)get("vmInstanceUUID");		
	}
	public void setVmInstanceUUID(String uuid) {
		set("vmInstanceUUID", uuid);
	}
	
	public Long getTimeRemain() {
		return (Long)get("TimeRemain");
	}
	public void setTimeRemain(Long timeRemain) {
		set("TimeRemain", timeRemain);
	}
	public Boolean isVHDMerge() {
		return (Boolean)get("VHDMerge");
	}
	public void setVHDMerge(Boolean isVHD) {
		set("VHDMerge", isVHD);
	}

	public Integer getSessRangeCnt() {
		return (Integer)get("SessRangeCnt");
	}
	
	public void setSessRangeCnt(Integer sessRangeCnt ) {		
		set("SessRangeCnt", sessRangeCnt);
	}
	
	public Integer getCurrentMergeRangeStart() {
		return (Integer)get("currentMergeRangeStart");
	}
	
	public void setCurrentMergeRangeStart(Integer currentMergeRangeStart ) {		
		set("currentMergeRangeStart", currentMergeRangeStart);
	}
	
	public Integer getCurrentMergeRangeEnd() {
		return (Integer)get("currentMergeRangeEnd");
	}
	
	public void setCurrentMergeRangeEnd(Integer currentMergeRangeEnd ) {		
		set("currentMergeRangeEnd", currentMergeRangeEnd);
	}

	public String getServerNodeName() {
		return (String)get("serverNodeName");
	}

	public void setServerNodeName(String serverNodeName) {
		set("serverNodeName", serverNodeName);
	}
}
