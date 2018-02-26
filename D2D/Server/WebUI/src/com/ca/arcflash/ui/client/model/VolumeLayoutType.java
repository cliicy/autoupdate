package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;

public final class VolumeLayoutType {
	public static final int EVLT_UNKNOWN = 0; // Unknown Type
	public static final int EVLT_SIMPLE = 1;        
	public static final int EVLT_MIRROR = 2; 
	public static final int EVLT_SPANNED = 3; 
	public static final int EVLT_STRIPPED = 4; 
	public static final int EVLT_RAID5 = 5;
	
	public static String getDisplayName(int type) {
		switch (type) {
		case EVLT_SIMPLE:
			return UIContext.Constants.volumeLayoutSimple();

		case EVLT_MIRROR:
			return UIContext.Constants.volumeLayoutMirror();
			
		case EVLT_SPANNED:
			return UIContext.Constants.volumeLayoutSpanned();
			
		case EVLT_STRIPPED:
			return UIContext.Constants.volumeLayoutStripped();

		case EVLT_RAID5:
			return UIContext.Constants.volumeLayoutRaid5();

		default:
			return UIContext.Constants.unknown();
		}
	}
	
	public static boolean isBackupSupport(int type) {
		switch (type) {
		case EVLT_SIMPLE:
		case EVLT_MIRROR:
		case EVLT_SPANNED:
		case EVLT_STRIPPED:
		case EVLT_RAID5:
			return true;
		default:
			return false;
		}
	}
}
