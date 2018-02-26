package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum HypervProtectionType {
	UNKNOWN(0xFFFFFFFF), 
	DEFAULT(0x00000000),
	STANDALONE(0x00000001), 
	CLUSTER(0x00000002),
	STANDALONEANDCLUSTER(0x00000003);
	
	private final int value;
	private HypervProtectionType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static HypervProtectionType parse(int value) {
		HypervProtectionType[] types = HypervProtectionType.values();
		for (HypervProtectionType type : types) {
			if (type.value == value) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
