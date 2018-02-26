package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

public enum GatewayUpdateMessageType
{
	UpdaterMessage			( 1 ),	// messages come from AutoUpdate.exe
	InstallerMessage		( 2 ),	// messages come from InstallUpdates.exe
	GmPackageMessage		( 3 ),	// messages come from the GM package
	UpdatePackageMessage	( 4 );	// messages come from the update package
	
	private int value;
	
	GatewayUpdateMessageType( int value )
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public static GatewayUpdateMessageType fromValue( int value )
	{
		for (GatewayUpdateMessageType item : GatewayUpdateMessageType.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
