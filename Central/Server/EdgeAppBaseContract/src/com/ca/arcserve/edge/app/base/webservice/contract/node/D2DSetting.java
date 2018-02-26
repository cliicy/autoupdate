package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class D2DSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5468775415563194015L;

	private int retryTimes;
	private int retryInterval;	
	
	public boolean contentEqualsOther(Object obj) {
		if(obj == null) return false;
		
		if(obj instanceof D2DSetting) {
			D2DSetting temp = (D2DSetting)obj;
			
			return this.retryTimes == temp.retryTimes
				&& this.retryInterval == temp.retryInterval;
			
		} else return false;
	}
	
	public D2DSetting() {
		retryTimes = 5;
		retryInterval = 300;
	}
	
	public int getRetryTimes() {
		return retryTimes;
	}
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	public int getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}	
}
