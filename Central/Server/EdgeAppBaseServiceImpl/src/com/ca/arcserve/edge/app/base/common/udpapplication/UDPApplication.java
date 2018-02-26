package com.ca.arcserve.edge.app.base.common.udpapplication;

public abstract class UDPApplication
{
	public enum UDPApplicationType
	{
		Console,
		Gateway,
	}
	
	private static UDPApplication instance = null;
	
	private Object messageService;
	
	public static void setInstance( UDPApplication instance )
	{
		UDPApplication.instance = instance;
	}
	
	public static UDPApplication getInstance()
	{
		return instance;
	}

	public Object getMessageService()
	{
		return messageService;
	}

	public void setMessageService( Object messageService )
	{
		this.messageService = messageService;
	}

	public abstract UDPApplicationType getApplicationType();
	public abstract boolean isReady();
	public abstract void initialize();
}
