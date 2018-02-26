package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.DashboardFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;

/**
 * The filter is used for extracted from JobHistoryFilter. The original class
 * was used in several APIs, but they're only using a part of the class. In
 * order to make it clearer, I split the original class into two according to
 * how APIs using the fields. By far, this class is only used in dashboard.
 * 
 * @author panbo01 (2014-08-21)
 */
public class JobHistoryFilter4Dashboard implements Serializable {

	private static final long serialVersionUID = 1084934550214286984L;
	
	private DashboardFilterType jobStatusGroup = DashboardFilterType.All;
	private BaseFilter jobTimeFilter = new BaseFilter();
	private String planUUID;
	private long jobType = -1;
	private JobHistoryPagingConfig pagingConfig = null;
	
	public DashboardFilterType getJobStatusGroup()
	{
		return jobStatusGroup;
	}
	public void setJobStatusGroup( DashboardFilterType jobStatusGroup )
	{
		this.jobStatusGroup = jobStatusGroup;
	}
	public BaseFilter getJobTimeFilter()
	{
		return jobTimeFilter;
	}
	public void setJobTimeFilter( BaseFilter jobTimeFilter )
	{
		this.jobTimeFilter = jobTimeFilter;
	}
	public String getPlanUUID() {
		return planUUID;
	}
	public void setPlanUUID(String planUUID) {
		this.planUUID = planUUID;
	}
	public long getJobType() {
		return jobType;
	}
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	
	public JobHistoryPagingConfig getPagingConfig()
	{
		return pagingConfig;
	}
	
	public void setPagingConfig( JobHistoryPagingConfig pagingConfig )
	{
		this.pagingConfig = pagingConfig;
	}
}
