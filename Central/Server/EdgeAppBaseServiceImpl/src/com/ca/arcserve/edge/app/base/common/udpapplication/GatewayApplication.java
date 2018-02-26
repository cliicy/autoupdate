package com.ca.arcserve.edge.app.base.common.udpapplication;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.settings.GatewayMessageServiceSettings;

public class GatewayApplication extends UDPApplication
{
	public enum GatewayAppState
	{
		Unknown,
		Starting,
		Initializing,
		Ready,
		InitializationFailed,
		Stopping,
		Updating,
	}
	
	private GatewayAppState gatewayAppState = GatewayAppState.Unknown;
	private GatewayMessageServiceSettings messageServiceSettings = null;
	private GatewayConnectInfo gatewayConnectInfo = null;
	
	public GatewayApplication()
	{
	}

	@Override
	public UDPApplicationType getApplicationType()
	{
		return UDPApplicationType.Gateway;
	}

	@Override
	public boolean isReady()
	{
		return (this.gatewayAppState == GatewayAppState.Ready);
	}

	@Override
	public void initialize()
	{
		// I plan to move all initialization processes to here. The context listener of
		// a web application should only be a listener, it should invoke corresponding
		// functions on some specific events. For example invoke this function when
		// the contextInitialized() get invoked.
	}

	public synchronized GatewayAppState getGatewayAppState()
	{
		return gatewayAppState;
	}

	public synchronized void setGatewayAppState( GatewayAppState gatewayAppState )
	{
		if (gatewayAppState == null)
			gatewayAppState = GatewayAppState.Unknown;
		this.gatewayAppState = gatewayAppState;
	}

	public GatewayMessageServiceSettings getMessageServiceSettings()
	{
		return messageServiceSettings;
	}

	public void setMessageServiceSettings(
		GatewayMessageServiceSettings messageServiceSettings )
	{
		this.messageServiceSettings = messageServiceSettings;
	}

	public GatewayConnectInfo getGatewayConnectInfo()
	{
		return gatewayConnectInfo;
	}

	public void setGatewayConnectInfo( GatewayConnectInfo gatewayConnectInfo )
	{
		this.gatewayConnectInfo = gatewayConnectInfo;
	}

}
