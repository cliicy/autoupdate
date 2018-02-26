package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ASBURegularGroup {
	TSI_REGULAR_GROUP(0x00000000L, "Regular Group"),
	TSI_SHARED_GROUP(0x00000001L, "Shared Group"),
	TSI_GROUP_ON_PRIMARY(0x00000002L, "Group On Primary"),
	TSI_NAS_ENABLED_GROUP(0x00000004L, "Nas Enabled Group"),
	TSI_FSD_GROUP(0x00000008L, "FSD Group"),
	TSI_EMPTY_GROUP(0x00000020L, "Empty Group"),
	TSI_DEDUPE_GROUP(0x00000080L, "Dedupe Group"),
	TSI_VTL_GROUP(0x00000100L, "VTL Group"),
	TSI_CLOUD_GROUP(0x00008000L, "Cloud Group"),
	TSI_STAGING_GROUP(0x00000010L, "Staging Group");
	
	private long value;
	private String name;
	
	private ASBURegularGroup(long value, String name){
		this.value = value;
		this.name = name;
	}
	
	

	public String getName() {
		return name;
	}



	public long getValue() {
		return value;
	}
	
	public static ASBURegularGroup fromValue(long value) {
		for (ASBURegularGroup item : ASBURegularGroup.values()) {
			if (item.value == value)
				return item;
		}
		return null;
	}
}
