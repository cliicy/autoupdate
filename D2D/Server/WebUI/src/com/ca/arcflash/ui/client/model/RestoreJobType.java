package com.ca.arcflash.ui.client.model;

public enum RestoreJobType {
	UNKNOWN(-1), FileSystem(1), VSS_SQLServer(2), VSS_Exchange(3), VM_Recovery(
			4), GRT_Exchange(6), VM_RESTORE_FILE_TO_ORIGINAL(7), VM_RESTORE_SQLSERVER_TO_ORIGINAL(
			8), VM_RESTORE_EXCHANGE_TO_ORIGINAL(9), VM_RESTORE_FILE_TO_ALTER(10), VM_RESTORE_SQLSERVER_TO_ALTER(
			11), VM_RESTORE_EXCHANGE_TO_ALTER(12), ActiveDirectory(13); 
	private RestoreJobType(int value) {
		this.value = value;
	}

	private int value;

	public int getValue() {
		return value;
	}
}
