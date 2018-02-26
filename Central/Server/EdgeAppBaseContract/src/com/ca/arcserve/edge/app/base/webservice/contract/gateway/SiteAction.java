package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

public enum SiteAction
{
	NoAction				( 0 ),
	RegisterGatewayHost		( 1 ),	// Set when site was just created and no gateway host registered, and will be
									// cleared when a gateway host is registered.
	ReregisterGatewayHost	( 2 ),	// Set when following cases:
									//  - Console got reinstalled without overwriting database
									//  - Console's URL (server name, port or protocol) changed
	RestartGatewayHost		( 3 );	// Set when following cases:
									//  - Failed to set heartbeat interval
	
	private int value;
	
	SiteAction( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static SiteAction fromValue( int value )
	{
		for (SiteAction item : SiteAction.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
