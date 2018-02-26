package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ASBUMediaGroupType {
	TAPE_GROUP(1,"Tape Group"), CHANGER_GROUP(2,"Library Group"), RAID_TAPE_GROUP(3,"Raid Tap Group"), RAID_CHANGER_GROUP(4, "Raid Changer Group");

	private int value;
	private String name;

	ASBUMediaGroupType(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}

	public static ASBUMediaGroupType fromValue(int value) {
		for (ASBUMediaGroupType item : ASBUMediaGroupType.values()) {
			if (item.value == value)
				return item;
		}
		return null;
	}
}
