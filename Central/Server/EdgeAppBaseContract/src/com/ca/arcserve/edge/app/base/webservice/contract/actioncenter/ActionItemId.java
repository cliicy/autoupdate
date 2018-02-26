package com.ca.arcserve.edge.app.base.webservice.contract.actioncenter;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;

public class ActionItemId implements Serializable
{
	private static final long serialVersionUID = -5963326881428061144L;

	public static final int INVALID_RECORD_ID = 0;
	public static final SiteId INVALID_SITE_ID = new SiteId( INVALID_RECORD_ID );
	
	private int recordId;
	
	public ActionItemId()
	{
		setRecordId( INVALID_RECORD_ID );
	}
	
	public ActionItemId( int recordId )
	{
		setRecordId( recordId );
	}

	public int getRecordId()
	{
		return recordId;
	}

	public void setRecordId( int recordId )
	{
		this.recordId = recordId;
	}

	public boolean isValid()
	{
		return (this.recordId != INVALID_RECORD_ID);
	}
	
	@Override
	public int hashCode()
	{
		return recordId;
	}
	
	@Override
	public boolean equals( Object object )
	{
		if (!(object instanceof GatewayId))
			return false;
		
		return (this.recordId == ((ActionItemId)object).recordId);
	}
	
	@Override
	public String toString()
	{
		return "SiteId { recordId: " + this.recordId + " }";
	}
}
