package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;


/**
 * Reference com.ca.arcflash.webservice.constants.JobStatus
 * 	int JOBSTATUS_ACTIVE               = 0;
	int JOBSTATUS_FINISHED             = 1;
	int JOBSTATUS_CANCELLED            = 2;
	int JOBSTATUS_FAILED               = 3;
	int JOBSTATUS_INCOMPLETE           = 4;
	int JOBSTATUS_IDLE                 = 5;
	int JOBSTATUS_WAITING              = 6;
	int JOBSTATUS_CRASH                = 7;
	int JOBSTATUS_LICENSE_FAILED       = 9;	
	long BackupJob_PROC_EXIT           = 10;
	int JOBSTATUS_SKIPPED              = 11;
	int JOBSTATUS_STOP                 = 12;
	int JOBSTATUS_FAIL_LOCK            = 14;
	int JOBSTATUS_MISSED               = 10000;
 */
public enum JobStatus {
	
	All(-1),
	Active(0),
	Finished(1),
	Canceled(2),
	Failed(3),
	Incomplete(4),// means finish
	Idle(5),// no use
	Waiting(6),
	Crash(7),
	LicenseFailed(9),
	BackupJob_PROC_EXIT(10),
	Skipped(11),
	Stop(12),
	Missed(10000)
	;

	private int value;
	
	private JobStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static JobStatus parse(long jobStatus) {
		switch ((int)jobStatus) {
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_ACTIVE:
			return Active;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_FINISHED:
			return Finished;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_CANCELLED:
			return Canceled;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_FAILED:
			return Failed;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_INCOMPLETE:
			return Incomplete;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_IDLE:
			return Idle;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_WAITING:
			return Waiting;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_CRASH:
			return Crash;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_LICENSE_FAILED:
			return LicenseFailed;
		case (int)com.ca.arcflash.webservice.constants.JobStatus.BackupJob_PROC_EXIT:
			return BackupJob_PROC_EXIT;	
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_SKIPPED:
			return Skipped;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_STOP:
			return Stop;
		case com.ca.arcflash.webservice.constants.JobStatus.JOBSTATUS_MISSED:
			return Missed;
		default:
			return Failed;
		}
	}

}
