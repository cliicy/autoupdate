package com.ca.arcserve.edge.app.rps.appdaos.model;

public enum RpsDaoNodeIsvisible {
	visible(1), invisible(2);

	private final int value;

	RpsDaoNodeIsvisible(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
