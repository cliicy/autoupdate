package com.ca.arcserve.edge.app.base.common.udpapplication;

import com.ca.arcserve.edge.app.base.webservice.gateway.settings.ConsoleMessageServiceSettings;

public class ConsoleApplication extends UDPApplication
{
	public enum ConsoleAppState
	{
		Unknown,
		Starting,
		Initializing,
		Ready,
		InitializationFailed,
		Stopping,
	}
	
	private ConsoleAppState consoleAppState = ConsoleAppState.Unknown;
	private ConsoleMessageServiceSettings messageServiceSettings = null;
	
	public ConsoleApplication()
	{
	}
	
	@Override
	public UDPApplicationType getApplicationType()
	{
		return UDPApplicationType.Console;
	}

	@Override
	public boolean isReady()
	{
		return (this.consoleAppState == ConsoleAppState.Ready);
	}

	@Override
	public void initialize()
	{
		// I plan to move all initialization processes to here. The context listener of
		// a web application should only be a listener, it should invoke corresponding
		// functions on some specific events. For example invoke this function when
		// the contextInitialized() get invoked.
	}

	public ConsoleAppState getConsoleAppState()
	{
		return consoleAppState;
	}

	public void setConsoleAppState( ConsoleAppState consoleAppState )
	{
		if (consoleAppState == null)
			consoleAppState = ConsoleAppState.Unknown;
		this.consoleAppState = consoleAppState;
	}

	public ConsoleMessageServiceSettings getMessageServiceSettings()
	{
		return messageServiceSettings;
	}

	public void setMessageServiceSettings(
		ConsoleMessageServiceSettings messageServiceSettings )
	{
		this.messageServiceSettings = messageServiceSettings;
	}
}
