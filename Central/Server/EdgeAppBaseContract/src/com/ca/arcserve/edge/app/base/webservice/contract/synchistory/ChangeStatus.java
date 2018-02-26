package com.ca.arcserve.edge.app.base.webservice.contract.synchistory;

public enum ChangeStatus {

	NORMAL(0),
	REFULLSYNC(1),
	BLOCKSYNC(2);
	
	private int value;
	
	private ChangeStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
    public static ChangeStatus parse(int v) {
        for (ChangeStatus c: ChangeStatus.values()) {
            if (c.value == v) {
                return c;
            }
        }
        return null;
    }
}
