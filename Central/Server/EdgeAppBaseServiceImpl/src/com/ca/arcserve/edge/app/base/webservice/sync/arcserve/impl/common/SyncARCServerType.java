package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;


public enum SyncARCServerType {
	SERVER_UNKNOW(0),
	REGULAR_PRIMARY(1),
	GDB_SELF_BRANCH(2),
	GDB_SELF_PRIMARY(3),
	GDB_SELF_GDB(4),
	REGULAR_MEMBER(5),
	REGULAR_STAND_ALONE(6),
	REGULAR_BRANCH(7);

	
	private final int value;
	
	SyncARCServerType(int i)
	{
		value = i;
	}
	
    public int value() {
        return value;
    }
    
    public static SyncARCServerType fromValue(int v) {
        for (SyncARCServerType c: SyncARCServerType.values()) {
            if (c.value == v) {
                return c;
            }
        }
        return null;
    }

}
