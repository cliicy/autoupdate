package com.ca.arcserve.edge.app.base.webservice.gateway;

public enum QueueType
{
	InvocFromConsoleReq		( "InvocFromConsole.Req" ),
	InvocFromConsoleResp	( "InvocFromConsole.Resp" ),
	InvocToConsoleReq		( "InvocToConsole.Req" ),
	InvocToConsoleResp		( "InvocToConsole.Resp" );
	
	private String name;
	
	QueueType( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
