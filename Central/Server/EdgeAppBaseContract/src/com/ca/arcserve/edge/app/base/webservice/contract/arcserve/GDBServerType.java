package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum GDBServerType {
	GDB_UNKNOW(0),
	GDB_REGULAR(1),
	GDB_IN_PRIMARY(2),
	GDB_IN_BRANCH(3);
	
	private final int value;
	
	GDBServerType(int i)
	{
		value = i;
	}
	
    public int value() {
        return value;
    }
    
    public static GDBServerType fromValue(int v) {
        for (GDBServerType c: GDBServerType.values()) {
            if (c.value == v) {
                return c;
            }
        }
        return null;
    }
}
