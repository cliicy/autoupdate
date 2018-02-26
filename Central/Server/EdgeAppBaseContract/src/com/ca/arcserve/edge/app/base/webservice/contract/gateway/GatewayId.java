package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GatewayId implements Serializable
{
	private static final long serialVersionUID = -3641979009599510540L;
	
	public static final int INVALID_RECORD_ID = 0;
	public static final GatewayId INVALID_GATEWAY_ID = new GatewayId( INVALID_RECORD_ID );
	
	private int recordId;
	
	public GatewayId()
	{
		setRecordId( INVALID_RECORD_ID );
	}
	
	public GatewayId( int recordId )
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
		
		return (this.recordId == ((GatewayId)object).recordId);
	}
	
	@Override
	public String toString()
	{
		return "GatewayId { recordId: " + this.recordId + " }";
	}
}
