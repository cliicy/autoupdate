package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

public enum GatewayUpdateStatusCode
{
	NA								( 0, 0 ),
	NoNeedToUpdate					( 1, 1 ),
	NotifyingGatewayToUpgrade		( 2, 2 ),
	FailedToNotifyGatewayToUpgrade	( 3, 3 ),
	GettingUpdateInfo				( 4, 4 ),
	FailedToGetUpdateInfo			( 5, 5 ),
	DownloadingUpdates				( 6, 6 ),
	FailedToDownloadUpdates			( 7, 7 ),
	InstallingUpdates				( 8, 8 ),
	FailedToInstallUpdates			( 9, 9 ),
	UpdatedSuccessfully				( 10, 10 ),
	UpdatedSuccessfullyNeedReboot	( 11, 11 );
	
	// (1) No Need --> (2) Notify --> (4) Get Info --> (6) Download --> (8) Install --> (10) Succ / (11) Nd.Reboot --> (1)
	//                  \              \                \                \
	//                  (3) N.Failed   (5) GI Failed    (7) DL Failed    (9) Inst Failed
	
	private int order;  // we can use the enum value's ordinal to achieve this, but if someone
						// adjust the defining order of some values carelessly, or because he
						// doesn't know the that order is very significant to our feature, our
						// feature may work incorrectly. To avoid this risk, we use a order
						// value to specify the order explicitly.
	private int value;
	
	GatewayUpdateStatusCode( int order, int value )
	{
		this.order = order;
		this.value = value;
	}
	
	public int getOrder()
	{
		return order;
	}

	public int getValue()
	{
		return this.value;
	}
	
	public static GatewayUpdateStatusCode fromValue( int value )
	{
		for (GatewayUpdateStatusCode item : GatewayUpdateStatusCode.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
	
	public boolean isInProgressStatus()
	{
		if ((this == GatewayUpdateStatusCode.NotifyingGatewayToUpgrade) ||
			(this == GatewayUpdateStatusCode.GettingUpdateInfo) ||
			(this == GatewayUpdateStatusCode.DownloadingUpdates) ||
			(this == GatewayUpdateStatusCode.InstallingUpdates))
			return true;
		
		return false;
	}
	
	public boolean isFailureStatus()
	{
		if ((this == GatewayUpdateStatusCode.FailedToNotifyGatewayToUpgrade) ||
			(this == GatewayUpdateStatusCode.FailedToGetUpdateInfo) ||
			(this == GatewayUpdateStatusCode.FailedToDownloadUpdates) ||
			(this == GatewayUpdateStatusCode.FailedToInstallUpdates))
			return true;
		
		return false;
	}
}
