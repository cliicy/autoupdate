package com.ca.arcserve.edge.app.base.scheduler;

import org.quartz.SchedulerException;

public class EdgeSchedulerException extends SchedulerException{
	   public static final int ERR_BAD_SCHEDULE_TYPE = -1;
	   public static final int ERR_BAD_SCHEDULE_PARAMETER = -2;
	   public static final int ERR_BAD_SCHEDULE_INVALID = -3;
	   public static final int ERR_BAD_SCHEDULE_ID_INVALID = -4;
	   public static final int ERR_BAD_SCHEDULE_GlOBAL_ID_INVALID = -5;
	   public static final int ERR_BAD_SCHEDULE_ID_EXISTED = -6;
	   public static final int ERR_BAD_SCHEDULE_JOB_NONEXISTED = -7;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ERR_BAD_SCHEDULE_JOB_EXISTED = -8;
	private int errorCode;
	public EdgeSchedulerException(String msg, Throwable cause, int errorCode) {
		super(msg, cause);
		this.setErrorCode(errorCode);
		// TODO Auto-generated constructor stub
	}
	public EdgeSchedulerException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}
	public EdgeSchedulerException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EdgeSchedulerException( int errorCode) {
		super("");
		this.setErrorCode(errorCode);
		// TODO Auto-generated constructor stub
	}
	public EdgeSchedulerException( String msg,int errorCode) {
		super(msg);
		this.setErrorCode(errorCode);
		// TODO Auto-generated constructor stub
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
