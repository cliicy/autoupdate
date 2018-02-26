package com.ca.arcserve.edge.app.base.webservice.contract.filter;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;

public class BaseFilter implements Serializable {

	private static final long serialVersionUID = 1004473560208120019L;
	
	private int id;
	/**
	 * Type:
	 * 1. Recent job
	 * 2. From XX Unit
	 * 3. Time stamp
	 * 4. All time
	 */
	private int type = 2;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		BaseFilter f = (BaseFilter)obj;
		if (f.getType() != this.getType()) {
			return false;
		}
		if (f.getType() == 2) {
			return f.getUnit() == this.getUnit() && f.getAmount() == this.getAmount();
		} else if (f.getType() == 3) {
			return f.getTimeStamp().getYear() == this.getTimeStamp().getYear()
					&& f.getTimeStamp().getMonth() == this.getTimeStamp().getMonth()
					&& f.getTimeStamp().getDay() == this.getTimeStamp().getDay()
					&& f.getTimeStamp().getHours() == this.getTimeStamp().getHours()
					&& f.getTimeStamp().getMinutes() == this.getTimeStamp().getMinutes()
					&& f.getTimeStamp().getSeconds() == this.getTimeStamp().getSeconds();
		} else {
			return true;
		}
	}

	private boolean defaultFilter = false;
	private Date timeStamp = new Date();
	private ServerDate serverTimeStemp;
	private int amount = 60;
	/**
	 * Unit:
	 * 1. Minute
	 * 2. Hour
	 * 3. Day
	 */
	private int unit = 1;
	private FilterType filterType = FilterType.LogTimeFilter;

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public ServerDate getServerTimeStemp() {
		return serverTimeStemp;
	}

	public void setServerTimeStemp(ServerDate serverTimeStemp) {
		this.serverTimeStemp = serverTimeStemp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isDefaultFilter() {
		return defaultFilter;
	}

	public void setDefaultFilter(boolean defaultFilter) {
		this.defaultFilter = defaultFilter;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

}
