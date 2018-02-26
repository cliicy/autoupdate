package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatusCode;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;

public class EdgeGatewayUpdateStatus
{
	private int gatewayId;
	private int updateStatus = GatewayUpdateStatusCode.NoNeedToUpdate.getValue();
	private String detailedMessage;
	private Date updateStartTime;
	private Date lastReportStatusTime;
	private Date updateTime;
	private Date instantiationTime = new Date();
	
	public int getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( int gatewayId )
	{
		this.gatewayId = gatewayId;
	}

	public int getUpdateStatus()
	{
		return updateStatus;
	}

	public void setUpdateStatus( int updateStatus )
	{
		this.updateStatus = updateStatus;
	}

	public String getDetailedMessage()
	{
		return detailedMessage;
	}

	public void setDetailedMessage( String detailedMessage )
	{
		this.detailedMessage = detailedMessage;
	}

	public Date getUpdateStartTime()
	{
		return updateStartTime;
	}

	public void setUpdateStartTime( Date updateStartTime )
	{
		this.updateStartTime = updateStartTime;
	}

	public Date getLastReportStatusTime()
	{
		return lastReportStatusTime;
	}

	public void setLastReportStatusTime( Date lastReportStatusTime )
	{
		this.lastReportStatusTime = lastReportStatusTime;
	}

	public Date getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime( Date updateTime )
	{
		this.updateTime = updateTime;
	}
	
	public boolean isTimeout()
	{
		if (this.lastReportStatusTime == null)
			return false;
		
		return ((this.instantiationTime.getTime() - this.lastReportStatusTime.getTime()) / 1000) >
			EdgeGatewayBean.getGatewayUpgradeTimeout();
	}
}
