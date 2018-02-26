package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;

public class VolumeType {
	
	public static final int EVT_UNKNOWN = 0;    // Unknown Type
	public static final int EVT_BASIC = 1;
	public static final int EVT_DYNAMIC = 2;
	
	public static String getDisplayName(int type) {
		switch (type) {
		case EVT_BASIC:
			return UIContext.Constants.volumeTypeBasic();
			
		case EVT_DYNAMIC:
			return UIContext.Constants.volumeTypeDynamic();

		default:
//			return null;
			return UIContext.Constants.unknown();
		}
	}
}
