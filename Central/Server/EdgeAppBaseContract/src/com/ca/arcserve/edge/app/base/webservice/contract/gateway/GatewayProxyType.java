package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

public enum GatewayProxyType
{
	IEProxy			( 0 ),
	CustomProxy		( 1 );
	
	private int value;
	
	GatewayProxyType( int value )
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public static GatewayProxyType fromValue( int value )
	{
		for (GatewayProxyType item : GatewayProxyType.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
