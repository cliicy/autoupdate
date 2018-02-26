package com.ca.arcflash.ui.client.model;

public enum EmailServerType {
	Other(0),Google(1),Yahoo(2),Live(3);
	private int type;

	EmailServerType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
