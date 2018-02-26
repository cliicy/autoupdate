package com.ca.arcserve.edge.app.base.webservice.contract.dashboard;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.TimeRange;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class DashboardGroupItem implements Serializable, BeanModelTag {
	
	private static final long serialVersionUID = 3944103512494334182L;
	
	@SuppressWarnings("unused")
	private String iconPath;
	private String name;
	private int count;
	private DashboardFilterType filterType = DashboardFilterType.All;
	private String bHeader;
	private String eHeader;
	private TimeRange timeRange = TimeRange.Last24Hour;
	private BaseFilter filter;
	
	public DashboardGroupItem() {
		filter = new BaseFilter();
		filter.setType(1); // Most Recent Run
	}
	
	public BaseFilter getFilter() {
		return filter;
	}

	public void setFilter(BaseFilter filter) {
		this.filter = filter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getIconPath() {
		String path = "images/Empty.gif";
		if (filterType == DashboardFilterType.All) {
			path = "images/all-jobs_16x16.png";
		}
		if (filterType == DashboardFilterType.JobsCompleted) {
			path = "images/status-okay_16x16.png";
		}
		if (filterType == DashboardFilterType.JobsFailed) {
			path = "images/status-failed_16x16.png";
		}
		if (filterType == DashboardFilterType.JobsScheduled) {
			path = "images/StatusSchedule.gif";
		}
		if (filterType == DashboardFilterType.JobsCanceled) {
			path = "images/status-warning_16x16.png";
		}
		if (filterType == DashboardFilterType.JobsInProgress) {
			path = "images/spinner-static_16x16.png";
		}
		return path;
	}

	public DashboardFilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(DashboardFilterType filterType) {
		this.filterType = filterType;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	

	public String getbHeader() {
		return bHeader;
	}

	public void setbHeader(String bHeader) {
		this.bHeader = bHeader;
	}

	public String geteHeader() {
		return eHeader;
	}

	public void seteHeader(String eHeader) {
		this.eHeader = eHeader;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	
}
