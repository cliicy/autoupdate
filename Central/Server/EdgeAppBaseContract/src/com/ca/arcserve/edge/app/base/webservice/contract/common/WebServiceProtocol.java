package com.ca.arcserve.edge.app.base.webservice.contract.common;

public enum WebServiceProtocol
{
	Http	( 1 ),
	Https	( 2 );
	
	private int value;
	
	WebServiceProtocol( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static WebServiceProtocol fromValue( int value )
	{
		for (WebServiceProtocol item : WebServiceProtocol.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
