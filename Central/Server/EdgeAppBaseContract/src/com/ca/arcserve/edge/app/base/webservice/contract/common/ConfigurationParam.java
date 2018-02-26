package com.ca.arcserve.edge.app.base.webservice.contract.common;


public enum ConfigurationParam{
	UN_KNOWN				(0),
	External_Console_URL	(1),	//site management using this for gateway to connect
	ConsoleUuid				(2),
	ConsoleVersion			(3)
	;
	private final int value;
	private ConfigurationParam(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static ConfigurationParam parse(int value) {
		ConfigurationParam[] types = ConfigurationParam.values();
		for (ConfigurationParam type : types) {
			if (type.value == value) {
				return type;
			}
		}
		return UN_KNOWN;
	}
}
