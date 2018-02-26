package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

public enum JobStatusForPlan {
	JobFinished(1),
	JobWarning(2),
	JobFailed(3)
	;
	private int value;
	
	private JobStatusForPlan(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static JobStatusForPlan getJobHistoryStatusForPlan(JobStatus status) {
		switch (status) {
		case Active:
		case Finished:		
		case BackupJob_PROC_EXIT:
			return JobFinished;
		case Canceled:
		case Waiting:
		case Skipped:
		case Stop:
		case Missed:
		case Incomplete:
			return JobWarning;
		case Failed:
		case Crash:
		case LicenseFailed:
			return JobFailed;
		default:
			return JobFailed;
		}
	}
}
