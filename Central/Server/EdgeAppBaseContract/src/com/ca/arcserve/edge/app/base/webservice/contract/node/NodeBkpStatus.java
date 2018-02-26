package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class NodeBkpStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7749195617614150137L;
	private int d2dStatus;

	/**
	 * Set the UDP agent backup status.
	 * 
	 * @param d2dStatus
	 */
	public void setD2dStatus(int d2dStatus) {
		this.d2dStatus = d2dStatus;
	}

	/**
	 * Get the UDP agent backup status.
	 * <p>
	 * <ul>
	 * <li>1: FIT
	 * <li>4: WARNINGNOSETTING, no destination settings
	 * <li>5: WARNINGLOWFREEDISK
	 * <li>6: ERRORNOTACCESS
	 * <li>7: ERRORWEBSERVICE, web service error
	 * <li>8: ERROR_D2D_CANNOT_ACCESS_EDGE, UDP agent cannot access UDP console
	 * </ul>
	 * 
	 * @return
	 */
	public int getD2dStatus() {
		return d2dStatus;
	}

}
