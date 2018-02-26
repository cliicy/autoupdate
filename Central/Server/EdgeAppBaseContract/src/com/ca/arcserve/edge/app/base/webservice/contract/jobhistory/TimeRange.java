package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.util.Date;

public enum TimeRange {
	Last24Hour(-24), Last48Hour(-48);
	
	private int hourRange;
	
	private TimeRange(int hourRange) {
		this.hourRange = hourRange;
	}
	
	public Date getTime() {
		Date now = new Date();
		return new Date(now.getTime() + hourRange * 3600 * 1000 );
	}
	
}
