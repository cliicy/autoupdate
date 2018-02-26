/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.apm;

/**
 * @author wanwe14
 *
 */
public class CheckUpdateReturnCode{
	public final static int ERROR_PM_REQ_FAILED = -1;
	public final static int ERROR_PM_REQ_SUCCESS = 0;
	public final static int ERROR_PM_COMM_FAILED = 1;
	public final static int ERROR_PM_COMM_SUCCESS = 2;
	public final static int ERROR_PM_SEND_SUCCESS = 3;
	public final static int ERROR_PM_SEND_FAILED = 4;
	public final static int ERROR_PM_READ_SUCCESS = 5;
	public final static int ERROR_PM_READ_FAILED = 6;
}
