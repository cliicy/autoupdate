package com.ca.arcflash.ui.client.model;

public enum DestType {
	OrigLoc(1), AlterLoc(2), DumpFile(3), ExchRestore2RSG(4), ExchRestore2RDB(5),AlterVM(6);
	private int value;

	private DestType(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}
}
